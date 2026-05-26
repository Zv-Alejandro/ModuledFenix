package org.ies.fenix.server.services;

import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.ies.fenix.controller.dto.game.GameSearchDTO;
import org.ies.fenix.controller.dto.teaser.TeaserResponseDTO;
import org.ies.fenix.server.models.*;
import org.ies.fenix.server.repositories.*;
import org.ies.fenix.server.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Service responsible for managing games in the server side of the application.
 * <p>
 * This class contains the main business logic related to:
 * </p>
 * <ul>
 *     <li>Creating and publishing games.</li>
 *     <li>Validating uploaded game files and image files.</li>
 *     <li>Persisting game files, logos and teaser images in the local file system.</li>
 *     <li>Retrieving games for marketplace, profile and detail views.</li>
 *     <li>Downloading stored game files.</li>
 *     <li>Formatting game metadata such as size and download counters.</li>
 * </ul>
 * <p>
 * Uploaded game files are stored under {@code uploads/games/{gameId}/files}.
 * Uploaded logo images are stored under {@code uploads/games/{gameId}/logo}.
 * Uploaded teaser images are stored under {@code uploads/games/{gameId}/teasers}.
 * </p>
 * <p>
 * Images are restricted to JavaFX-compatible formats. Unsupported formats such
 * as WebP are rejected during MIME validation.
 * </p>
 */

@Service
public class GameService {

    @Autowired private GameRepository gameRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private TeaserRepository teaserRepository;
    @Autowired private ClientService clientService;

    /**
     * Base directory where all uploaded game-related files are stored.
     */
    private static final String BASE_UPLOAD_DIR = Paths.get("").toAbsolutePath() + "/uploads/games/";

    /**
     * Number of bytes read from a file to detect its MIME type.
     * <p>
     * Only the header is read instead of loading the entire file into memory.
     * This prevents memory issues when validating large uploaded ZIP files.
     * </p>
     */
    private static final int MIME_DETECTION_BYTES = 8192;


    // ============================================================
    //                      CREATE GAME
    // ============================================================
    /**
     * Creates a new game and stores its associated files.
     * <p>
     * The method validates the authenticated client, checks the required input,
     * ensures that the game title is unique, persists the {@link Game} entity,
     * stores the uploaded files and finally returns the created game as a DTO.
     * </p>
     *
     * @param token              authorization token of the developer creating the game
     * @param title              title of the game
     * @param description        textual description of the game
     * @param price              game price; if {@code null}, zero is used
     * @param tagsText           comma-separated list of tag names
     * @param gameFile           ZIP file containing the game build
     * @param logoFile           required logo image
     * @param verticalImage      optional vertical teaser image
     * @param horizontalImageOne optional first horizontal teaser image
     * @param horizontalImageTwo optional second horizontal teaser image
     * @return DTO containing the created game information
     * @throws IllegalArgumentException if the token is invalid, required data is missing,
     *                                  the title already exists or a tag does not exist
     * @throws RuntimeException         if any uploaded file cannot be saved or validated
     */
    @Transactional
    public GameResponseDTO createGame(
            String token,
            String title,
            String description,
            BigDecimal price,
            String tagsText,
            MultipartFile gameFile,
            MultipartFile logoFile,
            MultipartFile verticalImage,
            MultipartFile horizontalImageOne,
            MultipartFile horizontalImageTwo
    ) {

        Client client = validateAndGetClient(token);
        validateGameInput(title, gameFile, logoFile);
        ensureTitleIsUnique(title);

        Game game = buildGameEntity(client, title, description, price, gameFile, parseTags(tagsText));
        game = gameRepository.save(game);

        saveMainFiles(game, gameFile, logoFile);
        saveTeasers(game, verticalImage, horizontalImageOne, horizontalImageTwo);

        return getGameById(game.getId());
    }

    // ============================================================
    //                      DOWNLOAD GAME
    // ============================================================
    /**
     * Returns the stored game file as a Spring {@link Resource}.
     *
     * @param gameId identifier of the game to download
     * @return resource pointing to the stored game ZIP file
     * @throws IllegalArgumentException if the game does not exist
     * @throws IllegalStateException    if the game has no stored file or the file cannot be found
     */
    public Resource downloadGame(Integer gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        String key = game.getGameFileKey();
        if (key == null) throw new IllegalStateException("Game file not available");

        File file = findFile(BASE_UPLOAD_DIR + game.getId() + "/files", key);
        return new FileSystemResource(file);
    }

    // ============================================================
    //                      VALIDATION
    // ============================================================
    /**
     * Validates the provided authorization token and returns the authenticated client.
     *
     * @param token authorization token, with or without the {@code Bearer } prefix
     * @return authenticated client
     * @throws IllegalArgumentException if the token is invalid or does not belong to a client
     */
    private Client validateAndGetClient(String token) {
        Client client = clientService.getClient(token);
        if (client == null) throw new IllegalArgumentException("Token is not valid");
        return client;
    }
    /**
     * Validates the required fields for game creation.
     *
     * @param title    game title
     * @param gameFile uploaded game ZIP file
     * @param logoFile uploaded logo image
     * @throws IllegalArgumentException if the title, game file or logo image is missing
     */
    private void validateGameInput(String title, MultipartFile gameFile, MultipartFile logoFile) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (gameFile == null || gameFile.isEmpty()) throw new IllegalArgumentException("Game file is required");
        if (logoFile == null || logoFile.isEmpty()) throw new IllegalArgumentException("Logo image is required");
    }
    /**
     * Ensures that no existing game has the same title, ignoring case.
     *
     * @param title title to validate
     * @throws IllegalArgumentException if another game already uses the same title
     */
    private void ensureTitleIsUnique(String title) {
        if (gameRepository.existsByTitleIgnoreCase(title))
            throw new IllegalArgumentException("A game with this title already exists");
    }

    // ============================================================
    //                      GAME ENTITY
    // ============================================================

    /**
     * Builds a new {@link Game} entity before it is persisted.
     *
     * @param client   developer that owns the game
     * @param title    game title
     * @param description game description
     * @param price    game price
     * @param gameFile uploaded game file, used to calculate the approximate size
     * @param tags     list of associated tags
     * @return initialized game entity
     */
    private Game buildGameEntity(Client client, String title, String description, BigDecimal price,
                                 MultipartFile gameFile, List<Tag> tags) {

        Game game = new Game();
        game.setTitle(title.trim());
        game.setDescription(description);
        game.setDev(client);
        game.setPrice(price != null ? price : BigDecimal.ZERO);
        game.setDownloads(0);
        game.setSizeMb(calculateSizeMb(gameFile));
        game.setTags(tags);
        return game;
    }

    /**
     * Parses a comma-separated list of tag names and resolves them from the database.
     *
     * @param tagsText comma-separated tag names
     * @return list of resolved {@link Tag} entities; empty list if no tags are provided
     * @throws IllegalArgumentException if any tag name does not exist
     */
    private List<Tag> parseTags(String tagsText) {
        if (tagsText == null || tagsText.isBlank()) return List.of();

        return Arrays.stream(tagsText.split(","))
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .map(t -> tagRepository.findByNameIgnoreCase(t)
                        .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + t)))
                .toList();
    }

    // ============================================================
    //                      FILE SAVE
    // ============================================================

    /**
     * Saves the main files associated with a game: the game ZIP file and the logo image.
     * <p>
     * The generated storage keys are assigned to the game entity and persisted.
     * </p>
     *
     * @param game     game entity
     * @param gameFile uploaded game ZIP file
     * @param logoFile uploaded logo image
     */
    private void saveMainFiles(Game game, MultipartFile gameFile, MultipartFile logoFile) {
        game.setGameFileKey(saveGameFile(game, gameFile));
        game.setGameLogoKey(saveImage(game, logoFile, "logo"));
        gameRepository.save(game);
    }

    /**
     * Saves the optional teaser images of a game.
     * <p>
     * The expected teaser order is:
     * </p>
     * <ol>
     *     <li>{@code VERTICAL}</li>
     *     <li>{@code HORIZONTAL_1}</li>
     *     <li>{@code HORIZONTAL_2}</li>
     * </ol>
     *
     * @param game    game associated with the teasers
     * @param teasers optional uploaded teaser image files
     */
    private void saveTeasers(Game game, MultipartFile... teasers) {
        String[] types = {"VERTICAL", "HORIZONTAL_1", "HORIZONTAL_2"};

        for (int i = 0; i < teasers.length; i++) {
            MultipartFile file = teasers[i];
            if (file == null || file.isEmpty()) continue;

            String key = saveImage(game, file, "teasers");

            Teaser teaser = new Teaser();
            teaser.setGame(game);
            teaser.setObjectKey(key);
            teaser.setType(types[i]);

            teaserRepository.save(teaser);
        }
    }

    /**
     * Validates and stores the uploaded game file.
     * <p>
     * Only ZIP MIME types are accepted.
     * </p>
     *
     * @param game game associated with the file
     * @param file uploaded ZIP file
     * @return generated object key used to locate the stored file
     * @throws IllegalArgumentException if the uploaded file is not a supported ZIP type
     */
    private String saveGameFile(Game game, MultipartFile file) {
        validateMime(file,
                "application/zip",
                "application/x-zip-compressed"
        );

        return saveFile(
                BASE_UPLOAD_DIR + game.getId() + "/files",
                file,
                "zip",
                false
        );
    }


    /**
     * Validates and stores an uploaded image file.
     * <p>
     * The stored extension is derived from the detected MIME type, not blindly
     * from the original filename. This prevents storing a file with a misleading
     * extension.
     * </p>
     *
     * @param game   game associated with the image
     * @param file   uploaded image file
     * @param folder target folder inside the game directory
     * @return generated object key used to locate the stored image
     * @throws IllegalArgumentException if the image type is not compatible with JavaFX
     */
    private String saveImage(Game game, MultipartFile file, String folder) {
        String extension = getJavaFxCompatibleImageExtension(file);

        return saveFile(
                BASE_UPLOAD_DIR + game.getId() + "/" + folder,
                file,
                extension,
                true
        );
    }

    /**
     * Stores an uploaded file in the local file system.
     *
     * @param folderPath      destination folder path
     * @param file            uploaded multipart file
     * @param defaultExt      extension to use when no extension is available or when forced
     * @param forceDefaultExt whether {@code defaultExt} should always be used
     * @return generated object key without extension
     * @throws RuntimeException if the file cannot be saved
     */
    private String saveFile(String folderPath,
                            MultipartFile file,
                            String defaultExt,
                            boolean forceDefaultExt) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String key = UUID.randomUUID().toString().replace("-", "");
            String ext = forceDefaultExt
                    ? defaultExt
                    : FileUtils.getExtension(file.getOriginalFilename());

            if (ext.isEmpty()) ext = defaultExt;

            File target = new File(folder, key + "." + ext);
            file.transferTo(target);

            return key;

        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage(), e);
        }
    }

    /**
     * Detects the MIME type of an uploaded image and returns the corresponding
     * JavaFX-compatible file extension.
     *
     * @param file uploaded image file
     * @return safe extension matching the detected MIME type
     * @throws IllegalArgumentException if the image type is unsupported
     * @throws RuntimeException         if MIME detection fails
     */
    private String getJavaFxCompatibleImageExtension(MultipartFile file) {
        try {
            String detected = FileUtils.getContentType(
                    readHeaderBytes(file),
                    file.getOriginalFilename()
            );

            return switch (detected) {
                case "image/png" -> "png";
                case "image/jpeg" -> "jpg";
                case "image/gif" -> "gif";
                case "image/bmp", "image/x-ms-bmp" -> "bmp";
                default -> throw new IllegalArgumentException(
                        "Invalid image type: " + detected + ". Allowed formats: PNG, JPG, JPEG, GIF and BMP"
                );
            };
        } catch (IOException e) {
            throw new RuntimeException("Error validating image type", e);
        }
    }

    /**
     * Validates that an uploaded file matches one of the allowed MIME types.
     * <p>
     * Only the first {@link #MIME_DETECTION_BYTES} bytes are read to avoid
     * loading large files completely into memory.
     * </p>
     *
     * @param file    uploaded file to validate
     * @param allowed allowed MIME types
     * @throws IllegalArgumentException if the detected MIME type is not allowed
     * @throws RuntimeException         if the file cannot be read
     */
    private void validateMime(MultipartFile file, String... allowed) {
        try {
            String detected = FileUtils.getContentType(
                    readHeaderBytes(file),
                    file.getOriginalFilename()
            );

            if (Arrays.stream(allowed).noneMatch(detected::equals)) {
                throw new IllegalArgumentException("Invalid file type: " + detected);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error validating file type", e);
        }
    }

    private byte[] readHeaderBytes(MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream()) {
            return inputStream.readNBytes(MIME_DETECTION_BYTES);
        }
    }

    private void validateMimeStartsWith(MultipartFile file, String prefix) {
        try {
            String detected = FileUtils.getContentType(
                    readHeaderBytes(file),
                    file.getOriginalFilename()
            );

            if (!detected.startsWith(prefix)) {
                throw new IllegalArgumentException("Invalid image type: " + detected);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error validating image type", e);
        }
    }

    // ============================================================
    //                      FILE LOAD
    // ============================================================

    /**
     * Loads the logo image of a game from the local file system.
     *
     * @param id game identifier
     * @return raw image bytes
     * @throws IllegalArgumentException if the game does not exist
     * @throws IllegalStateException    if the stored logo file cannot be found
     * @throws RuntimeException         if the file cannot be read
     */
    public byte[] loadLogo(Integer id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        File file = findFile(BASE_UPLOAD_DIR + game.getId() + "/logo", game.getGameLogoKey());

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error loading logo", e);
        }
    }

    /**
     * Loads the vertical teaser image of a game.
     *
     * @param id game identifier
     * @return raw image bytes
     */
    public byte[] getVerticalImage(Integer id) {
        return loadTeaser(id, "VERTICAL");
    }

    /**
     * Loads the first horizontal teaser image of a game.
     *
     * @param id game identifier
     * @return raw image bytes
     */
    public byte[] getHorizontal1Image(Integer id) {
        return loadTeaser(id, "HORIZONTAL_1");
    }

    /**
     * Loads the second horizontal teaser image of a game.
     *
     * @param id game identifier
     * @return raw image bytes
     */
    public byte[] getHorizontal2Image(Integer id) {
        return loadTeaser(id, "HORIZONTAL_2");
    }

    /**
     * Loads a teaser image by game identifier and teaser type.
     *
     * @param gameId game identifier
     * @param type   teaser type, such as {@code VERTICAL}, {@code HORIZONTAL_1} or {@code HORIZONTAL_2}
     * @return raw teaser image bytes
     * @throws IllegalStateException if the teaser or its file cannot be found
     * @throws RuntimeException      if the file cannot be read
     */
    private byte[] loadTeaser(Integer gameId, String type) {
        Teaser teaser = teaserRepository.findByGameIdAndType(gameId, type)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Teaser not found: " + type));

        File file = findFile(BASE_UPLOAD_DIR + gameId + "/teasers", teaser.getObjectKey());

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error loading teaser: " + type, e);
        }
    }

    /**
     * Finds a stored file by its object key regardless of its extension.
     *
     * @param folder folder where the file is expected to be stored
     * @param key    object key without extension
     * @return matching file
     * @throws IllegalStateException if no file exists for the given key
     */
    private File findFile(String folder, String key) {
        File dir = new File(folder);
        File[] matches = dir.listFiles((d, name) -> name.startsWith(key + "."));
        if (matches == null || matches.length == 0)
            throw new IllegalStateException("File with key " + key + " not found");
        return matches[0];
    }

    // ============================================================
    //                      QUERY METHODS
    // ============================================================

    /**
     * Retrieves a game by its identifier with developer and tags loaded.
     *
     * @param id game identifier
     * @return detailed game DTO, or {@code null} if the game does not exist
     */
    public GameResponseDTO getGameById(Integer id) {
        return gameRepository.findByIdWithDevAndTags(id)
                .map(this::toGameDetailResponseDTO)
                .orElse(null);
    }

    /**
     * Retrieves all games ordered by most recent first.
     *
     * @return list of marketplace game DTOs
     */
    public List<GameResponseDTO> getAllGames() {
        return gameRepository.findAllWithDevAndTagsOrderByIdDesc()
                .stream()
                .map(this::toMarketplaceResponseDTO)
                .toList();
    }

    /**
     * Retrieves games according to the provided search criteria.
     * <p>
     * Search priority is:
     * </p>
     * <ol>
     *     <li>Title</li>
     *     <li>Developer username</li>
     *     <li>Tags</li>
     *     <li>Random fallback list with optional limit</li>
     * </ol>
     *
     * @param dto search criteria
     * @return list of matching game DTOs
     */
    public List<GameResponseDTO> getGames(GameSearchDTO dto) {
        java.util.List<Game> games;

        if (dto.getTitle() != null && !dto.getTitle().isEmpty())
            games = gameRepository.findByTitleContainingIgnoreCase(dto.getTitle());
        else if (dto.getDeveloperName() != null && !dto.getDeveloperName().isEmpty())
            games = gameRepository.findByDev_Username(dto.getDeveloperName());
        else if (dto.getTagNames() != null && !dto.getTagNames().isEmpty())
            games = gameRepository.findByAllTagNames(dto.getTagNames(), dto.getTagNames().size());
        else {
            games = gameRepository.findAll();
            Collections.shuffle(games);
            games = games.stream().limit(dto.getLimit() != null ? dto.getLimit() : 25).toList();
        }

        return games.stream().map(this::toResponseDTO).toList();
    }

    /**
     * Retrieves the games created by the authenticated client.
     *
     * @param authorization authorization header or token
     * @return list of games created by the authenticated client
     * @throws IllegalArgumentException if the token is invalid
     */
    public List<GameResponseDTO> getCreatedGamesByMe(String authorization) {
        Client client = validateAndGetClient(authorization);

        return gameRepository.findCreatedGamesByDevIdWithTags(client.getId())
                .stream()
                .map(this::toProfileCreatedGameResponseDTO)
                .toList();
    }

    // ============================================================
    //                      DTO MAPPING
    // ============================================================

    /**
     * Converts a game entity into a reduced DTO for the profile-created-games view.
     *
     * @param game game entity
     * @return reduced game response DTO
     */
    private GameResponseDTO toProfileCreatedGameResponseDTO(Game game) {
        GameResponseDTO dto = new GameResponseDTO();

        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setGameLogoKey(game.getGameLogoKey());

        if (game.getTags() != null) {
            dto.setTags(game.getTags().stream().map(Tag::getName).toList());
        } else {
            dto.setTags(List.of());
        }

        return dto;
    }


    /**
     * Converts a game entity into a detailed response DTO.
     *
     * @param game game entity
     * @return detailed game response DTO
     */
    private GameResponseDTO toGameDetailResponseDTO(Game game) {
        GameResponseDTO dto = new GameResponseDTO();

        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setSizeApproximation(formatSizeFromMB(game.getSizeMb()));
        dto.setDownloadsApproximation(formatDownloads(game.getDownloads()));
        dto.setPrice(game.getPrice());
        dto.setDevUsername(game.getDev() != null ? game.getDev().getUsername() : "Unknown");
        dto.setGameLogoKey(game.getGameLogoKey());
        dto.setGameFileKey(game.getGameFileKey());

        if (game.getTags() != null) {
            dto.setTags(game.getTags().stream().map(Tag::getName).toList());
        } else {
            dto.setTags(List.of());
        }

        dto.setTeasers(java.util.List.of());

        return dto;
    }

    /**
     * Converts a game entity into a general response DTO including teasers when available.
     *
     * @param game game entity
     * @return game response DTO
     */
    private GameResponseDTO toResponseDTO(Game game) {
        GameResponseDTO dto = new GameResponseDTO();

        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setSizeApproximation(formatSizeFromMB(game.getSizeMb()));
        dto.setDownloadsApproximation(formatDownloads(game.getDownloads()));
        dto.setPrice(game.getPrice());
        dto.setDevUsername(game.getDev() != null ? game.getDev().getUsername() : "Unknown");
        dto.setGameLogoKey(game.getGameLogoKey());
        dto.setGameFileKey(game.getGameFileKey());

        if (game.getTags() != null) {
            dto.setTags(game.getTags().stream().map(Tag::getName).toList());
        } else {
            dto.setTags(List.of());
        }

        if (game.getTeasers() != null) {
            dto.setTeasers(game.getTeasers().stream().map(this::toTeaserResponseDTO).toList());
        } else {
            dto.setTeasers(List.of());
        }

        return dto;
    }

    /**
     * Converts a game entity into a DTO optimized for marketplace listing.
     *
     * @param game game entity
     * @return marketplace game response DTO
     */
    private GameResponseDTO toMarketplaceResponseDTO(Game game) {
        GameResponseDTO dto = new GameResponseDTO();

        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setSizeApproximation(formatSizeFromMB(game.getSizeMb()));
        dto.setDownloadsApproximation(formatDownloads(game.getDownloads()));
        dto.setPrice(game.getPrice());
        dto.setDevUsername(game.getDev() != null ? game.getDev().getUsername() : "Unknown");
        dto.setGameLogoKey(game.getGameLogoKey());
        dto.setGameFileKey(game.getGameFileKey());

        if (game.getTags() != null) {
            dto.setTags(game.getTags()
                    .stream()
                    .map(Tag::getName)
                    .toList());
        } else {
            dto.setTags(List.of());
        }

        dto.setTeasers(List.of());

        return dto;
    }

    /**
     * Converts a teaser entity into its response DTO.
     *
     * @param teaser teaser entity
     * @return teaser response DTO
     */
    private TeaserResponseDTO toTeaserResponseDTO(Teaser teaser) {
        TeaserResponseDTO dto = new TeaserResponseDTO();
        dto.setId(teaser.getId());
        dto.setGameId(teaser.getGame().getId());
        dto.setObjectKey(teaser.getObjectKey());
        dto.setType(teaser.getType());
        return dto;
    }

    // ============================================================
    //                      FORMATTERS
    // ============================================================

    /**
     * Formats a size expressed in megabytes into a human-readable string.
     * <p>
     * Examples:
     * </p>
     * <ul>
     *     <li>{@code 512 -> "512 MB"}</li>
     *     <li>{@code 1024 -> "1 GB"}</li>
     * </ul>
     *
     * @param mb size in megabytes
     * @return formatted size string
     */
    public String formatSizeFromMB(BigDecimal mb) {
        if (mb == null || mb.compareTo(BigDecimal.ZERO) == 0) return "0 MB";

        final int FIGURE = 3;
        String[] units = {"MB", "GB", "TB", "PB"};
        int unitIndex = 0;

        BigDecimal base = BigDecimal.valueOf(1024);

        while (mb.compareTo(base) >= 0 && unitIndex < units.length - 1) {
            mb = mb.divide(base, MathContext.DECIMAL128);
            unitIndex++;
        }

        BigDecimal rounded = mb.round(new MathContext(FIGURE, RoundingMode.HALF_UP))
                .stripTrailingZeros();

        return rounded.toPlainString() + " " + units[unitIndex];
    }

    /**
     * Formats a download count into a compact human-readable representation.
     * <p>
     * Examples:
     * </p>
     * <ul>
     *     <li>{@code 950 -> "950"}</li>
     *     <li>{@code 1500 -> "1.5K"}</li>
     *     <li>{@code 1000000 -> "1M"}</li>
     * </ul>
     *
     * @param downloads number of downloads
     * @return formatted downloads string
     */
    public String formatDownloads(long downloads) {
        if (downloads == 0) return "0";

        final int FIGURE = 3;
        String[] units = {"", "K", "M", "B", "T"};
        double value = downloads;
        int unitIndex = 0;

        while (value >= 1000 && unitIndex < units.length - 1) {
            value /= 1000;
            unitIndex++;
        }

        double scale = Math.pow(10, Math.floor(Math.log10(value)) + 1);
        double rounded = Math.round(value / scale * Math.pow(10, FIGURE))
                / Math.pow(10, FIGURE) * scale;

        return (rounded % 1 == 0)
                ? String.format("%.0f%s", rounded, units[unitIndex])
                : rounded + units[unitIndex];
    }

    /**
     * Calculates the uploaded file size in megabytes.
     *
     * @param file uploaded file
     * @return file size in MB rounded to two decimal places, or zero if the file is missing
     */
    private BigDecimal calculateSizeMb(MultipartFile file) {
        if (file == null || file.isEmpty()) return BigDecimal.ZERO;

        double sizeMb = file.getSize() / 1024.0 / 1024.0;
        return BigDecimal.valueOf(sizeMb).setScale(2, RoundingMode.HALF_UP);
    }
}