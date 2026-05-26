package org.ies.fenix.client.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.client.utils.GameInstallUtils;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseCreateDTO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.ies.fenix.client.utils.ImageUtils.initialConfig;
import static org.ies.fenix.client.utils.ImageUtils.setCoverImage;

/**
 * Controller for the game detail screen.
 *
 * <p>This screen shows the selected game information, its banner, tags and
 * actions related to purchase, download and play.</p>
 *
 * <p>The controller also handles ZIP extraction after download and stores the
 * installation path so the game can be launched later.</p>
 */
public class GameController {

    private static final int MAX_VISIBLE_TAGS = 6;
    private static final int TAGS_PER_ROW = 3;

    private static final double TAG_WIDTH = 105.0;
    private static final double TAG_HEIGHT = 26.0;
    private static final double TAG_ROW_SPACING = 8.0;

    private static final int DOWNLOAD_BUFFER_SIZE = 8192;

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    public FontIcon topProfileIcon;

    @FXML
    public ImageView topProfileImage;

    @FXML
    public Label selectedGameDeveloper;

    @FXML
    public Label selectedGameTitle;

    @FXML
    public Label selectedGameTitle2;

    @FXML
    public Label selectedGameDescription1;

    @FXML
    public Label selectedGameDescription2;

    @FXML
    public Label selectedGameDescription3;

    @FXML
    public Label selectedGameMainQuote;

    @FXML
    public ImageView selectedGameBannerImage;

    @FXML
    public StackPane bannerWrapper;

    @FXML
    public VBox tagContainerFather;

    @FXML
    private Hyperlink username;

    @FXML
    private Button playButton;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;
    private final RestClient restClient;
    private final IPurchaseController purchaseApiService;

    // ============================================================
    // STATE
    // ============================================================

    private Integer selectedGameId;
    private byte[] currentBannerBytes;

    /**
     * Creates the game detail controller.
     *
     * @param stageManager       application scene manager
     * @param clientApiService   client API service
     * @param gameApiService     game API service
     * @param sessionManager     current user session manager
     * @param restClient         REST client used for file download
     * @param purchaseApiService purchase API service
     */
    public GameController(StageManager stageManager,
                          IClientController clientApiService,
                          IGameController gameApiService,
                          SessionManager sessionManager,
                          RestClient restClient,
                          IPurchaseController purchaseApiService) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.gameApiService = gameApiService;
        this.sessionManager = sessionManager;
        this.restClient = restClient;
        this.purchaseApiService = purchaseApiService;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    /**
     * Initializes navbar user data, banner behavior and play button state.
     */
    @FXML
    private void initialize() {
        initialConfig(clientApiService, sessionManager, username, topProfileImage, topProfileIcon);

        configureBannerImage();
        configureBannerClip();
        updatePlayButtonState();
    }

    private void configureBannerImage() {
        selectedGameBannerImage.setPreserveRatio(false);
        selectedGameBannerImage.setSmooth(true);
        selectedGameBannerImage.setCache(false);
        selectedGameBannerImage.setViewport(null);
        selectedGameBannerImage.setVisible(false);

        bannerWrapper.widthProperty().addListener((observable, oldValue, newValue) -> refreshBannerCover());
        bannerWrapper.heightProperty().addListener((observable, oldValue, newValue) -> refreshBannerCover());
    }

    /**
     * Clips the banner image to the banner wrapper bounds.
     */
    private void configureBannerClip() {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(bannerWrapper.widthProperty());
        clip.heightProperty().bind(bannerWrapper.heightProperty());

        bannerWrapper.setClip(clip);
    }

    // ============================================================
    // SELECTED GAME
    // ============================================================

    /**
     * Sets the selected game and loads its information from the backend.
     *
     * @param selectedGameId selected game identifier
     */
    public void setSelectedGameId(Integer selectedGameId) {
        this.selectedGameId = selectedGameId;
        loadSelectedGame();
        updatePlayButtonState();
    }

    private void loadSelectedGame() {
        if (selectedGameId == null) {
            return;
        }

        try {
            ResponseEntity<GameResponseDTO> response =
                    gameApiService.getById(buildHeader(), selectedGameId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return;
            }

            renderSelectedGame(response.getBody());
            loadHorizontalTwoIntoBanner(selectedGameId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderSelectedGame(GameResponseDTO game) {
        String title = game.getTitle() != null ? game.getTitle() : "Untitled";
        String developer = game.getDevUsername() != null ? game.getDevUsername() : "Unknown";
        String description = game.getDescription() != null
                ? game.getDescription()
                : "No description available.";

        selectedGameTitle.setText(title);
        selectedGameTitle2.setText("Title: " + title);
        selectedGameDeveloper.setText("Developer: " + developer);

        selectedGameDescription1.setText(description);
        selectedGameDescription2.setText("");
        selectedGameDescription3.setText("");

        selectedGameMainQuote.setText(title);

        renderTags(game.getTags());
    }

    // ============================================================
    // TAGS
    // ============================================================

    private void renderTags(List<String> tags) {
        tagContainerFather.getChildren().clear();

        if (tags == null || tags.isEmpty()) {
            return;
        }

        HBox firstRow = createTagRow();
        HBox secondRow = createTagRow();

        List<String> visibleTags = tags.stream()
                .limit(MAX_VISIBLE_TAGS)
                .toList();

        for (int i = 0; i < visibleTags.size(); i++) {
            Label tagLabel = createTagLabel(visibleTags.get(i));

            if (i < TAGS_PER_ROW) {
                firstRow.getChildren().add(tagLabel);
            } else {
                secondRow.getChildren().add(tagLabel);
            }
        }

        tagContainerFather.getChildren().add(firstRow);

        if (!secondRow.getChildren().isEmpty()) {
            tagContainerFather.getChildren().add(secondRow);
        }
    }

    private HBox createTagRow() {
        HBox row = new HBox(TAG_ROW_SPACING);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label createTagLabel(String text) {
        Label tagLabel = new Label(text);
        tagLabel.getStyleClass().addAll("tag", "game-tag");

        tagLabel.setMinWidth(TAG_WIDTH);
        tagLabel.setPrefWidth(TAG_WIDTH);
        tagLabel.setMaxWidth(TAG_WIDTH);

        tagLabel.setMinHeight(TAG_HEIGHT);
        tagLabel.setPrefHeight(TAG_HEIGHT);
        tagLabel.setMaxHeight(TAG_HEIGHT);

        tagLabel.setAlignment(Pos.CENTER);
        tagLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        tagLabel.setWrapText(false);

        return tagLabel;
    }

    // ============================================================
    // BANNER
    // ============================================================

    private void loadHorizontalTwoIntoBanner(Integer gameId) {
        if (gameId == null || selectedGameBannerImage == null) {
            return;
        }

        try {
            ResponseEntity<byte[]> response = gameApiService.getHorizontal2(
                    buildHeader(),
                    gameId
            );

            if (!hasValidImageBody(response)) {
                return;
            }

            currentBannerBytes = response.getBody();

            Platform.runLater(this::refreshBannerCover);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasValidImageBody(ResponseEntity<byte[]> response) {
        return response.getStatusCode().is2xxSuccessful()
                && response.getBody() != null
                && response.getBody().length > 0;
    }

    /**
     * Recalculates the banner image so it covers the available banner area.
     */
    private void refreshBannerCover() {
        if (currentBannerBytes == null || currentBannerBytes.length == 0) {
            return;
        }

        double width = bannerWrapper.getWidth();
        double height = bannerWrapper.getHeight();

        if (width <= 0) {
            width = bannerWrapper.getPrefWidth();
        }

        if (height <= 0) {
            height = bannerWrapper.getPrefHeight();
        }

        if (width <= 0 || height <= 0) {
            return;
        }

        setCoverImage(
                currentBannerBytes,
                selectedGameBannerImage,
                width,
                height
        );

        selectedGameBannerImage.setManaged(true);
        selectedGameBannerImage.setOpacity(1.0);
        selectedGameBannerImage.toFront();
    }

    // ============================================================
    // DOWNLOAD
    // ============================================================

    /**
     * Downloads the selected game.
     *
     * <p>If the user has not purchased the game yet, the controller asks for
     * confirmation and creates the purchase before requesting the download.</p>
     */
    @FXML
    private void onDownload() {
        try {
            if (selectedGameId == null) {
                showError("No game selected", "Please select a game to download.");
                return;
            }

            if (!canDownloadSelectedGame()) {
                return;
            }

            Resource resource = requestGameResource();

            if (resource == null) {
                return;
            }

            File target = chooseDownloadTarget(resource);

            if (target == null) {
                hideProgress();
                return;
            }

            startDownload(resource, target);

        } catch (HttpClientErrorException e) {
            hideProgress();
            showError("Server error", e.getResponseBodyAsString());

        } catch (Exception e) {
            hideProgress();
            showError("Download failed", "Unexpected error");
        }
    }

    private boolean canDownloadSelectedGame() {
        boolean purchased = hasPurchased(selectedGameId);

        if (purchased) {
            return true;
        }

        boolean confirmed = showPurchaseConfirmation();

        if (!confirmed) {
            return false;
        }

        return performPurchase(selectedGameId);
    }

    private Resource requestGameResource() {
        BaseLayoutController base = stageManager.getBaseLayoutController();
        base.showProgress();

        ResponseEntity<Resource> response = restClient.get()
                .uri("/api/games/download/" + selectedGameId)
                .header("Authorization", buildHeader())
                .retrieve()
                .toEntity(Resource.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            base.hideProgress();
            showError("Download failed", "The server returned an error.");
            return null;
        }

        Resource resource = response.getBody();

        if (resource == null) {
            base.hideProgress();
            showError("Download failed", "Empty file received.");
            return null;
        }

        return resource;
    }

    private File chooseDownloadTarget(Resource resource) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(getDownloadFileName(resource));

        return chooser.showSaveDialog(stageManager.getPrimaryStage());
    }

    private String getDownloadFileName(Resource resource) {
        String filename = resource.getFilename();

        if (filename == null || filename.isBlank()) {
            return "game_" + selectedGameId + ".zip";
        }

        return filename;
    }

    /**
     * Starts the download task and binds it to the global progress bar.
     */
    private void startDownload(Resource resource, File target) {
        BaseLayoutController base = stageManager.getBaseLayoutController();

        Task<Void> downloadTask = createDownloadTask(
                resource,
                target,
                selectedGameId
        );

        base.getGlobalProgressBar()
                .progressProperty()
                .bind(downloadTask.progressProperty());

        downloadTask.setOnSucceeded(event -> {
            finishDownload();
            updatePlayButtonState();
        });

        downloadTask.setOnFailed(event -> {
            finishDownload();
            updatePlayButtonState();
            showError("Download failed", "The game could not be downloaded or extracted.");
        });

        downloadTask.setOnCancelled(event -> {
            finishDownload();
            updatePlayButtonState();
        });

        new Thread(downloadTask).start();
    }

    /**
     * Creates the background task that writes the downloaded file to disk.
     *
     * <p>If the downloaded file is a ZIP, it is extracted and its installation
     * path is stored for later launch.</p>
     */
    private Task<Void> createDownloadTask(Resource resource, File target, Integer gameId) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                long fileSize = resource.contentLength();

                try (InputStream in = resource.getInputStream();
                     OutputStream out = new FileOutputStream(target)) {

                    byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
                    long totalRead = 0;
                    int read;

                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        totalRead += read;

                        updateProgress(totalRead, fileSize);
                    }
                }

                if (isZipFile(target)) {
                    updateProgress(ProgressBar.INDETERMINATE_PROGRESS, 1);

                    Path extractionDirectory = unzipFile(target);
                    GameInstallUtils.saveInstallPath(gameId, extractionDirectory);
                }

                return null;
            }
        };
    }

    private boolean isZipFile(File file) {
        return file != null
                && file.getName() != null
                && file.getName().toLowerCase().endsWith(".zip");
    }

    /**
     * Extracts a ZIP file into a folder with the same name as the ZIP.
     *
     * <p>The normalized target path is checked before extracting each entry to
     * avoid path traversal problems.</p>
     *
     * @param zipFile ZIP file to extract
     * @return extraction directory
     * @throws Exception if the file cannot be extracted
     */
    private Path unzipFile(File zipFile) throws Exception {
        Path outputDirectory = getExtractionDirectory(zipFile);

        Files.createDirectories(outputDirectory);

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path targetPath = outputDirectory
                        .resolve(entry.getName())
                        .normalize();

                if (!targetPath.startsWith(outputDirectory)) {
                    throw new SecurityException("Invalid ZIP entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(zipInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        }

        return outputDirectory;
    }

    private Path getExtractionDirectory(File zipFile) {
        String fileName = zipFile.getName();

        if (fileName.toLowerCase().endsWith(".zip")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }

        File parent = zipFile.getParentFile();

        if (parent == null) {
            return Path.of(fileName);
        }

        return parent.toPath().resolve(fileName);
    }

    private void finishDownload() {
        BaseLayoutController base = stageManager.getBaseLayoutController();

        base.getGlobalProgressBar().progressProperty().unbind();
        base.hideProgress();
    }

    private void hideProgress() {
        stageManager.getBaseLayoutController().hideProgress();
    }

    // ============================================================
    // PLAY
    // ============================================================

    /**
     * Launches the selected game if it has already been installed.
     */
    @FXML
    private void onPlay() {
        if (selectedGameId == null) {
            showError("No game selected", "Please select a game to play.");
            updatePlayButtonState();
            return;
        }

        if (!GameInstallUtils.canLaunchGame(selectedGameId)) {
            updatePlayButtonState();
            showError(
                    "Game not installed",
                    "Download this game before trying to play it."
            );
            return;
        }

        try {
            GameInstallUtils.launchGame(selectedGameId);

        } catch (Exception e) {
            updatePlayButtonState();
            showError(
                    "Game not installed",
                    "The game executable could not be found. Try downloading it again."
            );
        }
    }

    private void updatePlayButtonState() {
        if (playButton == null) {
            return;
        }

        boolean canPlay = GameInstallUtils.canLaunchGame(selectedGameId);
        playButton.setDisable(!canPlay);
    }

    // ============================================================
    // PURCHASE
    // ============================================================

    private boolean hasPurchased(Integer gameId) {
        Integer clientId = sessionManager.getClientId();

        try {
            ResponseEntity<Boolean> response =
                    purchaseApiService.hasPurchased(
                            buildHeader(),
                            clientId,
                            gameId
                    );

            Boolean purchased = response.getBody();
            return purchased != null && purchased;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Asks the user to confirm the purchase before downloading a game.
     */
    private boolean showPurchaseConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Acquire game");
        alert.setHeaderText(null);
        alert.setGraphic(null);

        FontIcon icon = new FontIcon("mdi2c-controller-classic");
        icon.setIconSize(46);
        icon.getStyleClass().add("purchase-dialog-icon");

        Label title = new Label("You don't own this game yet");
        title.getStyleClass().add("purchase-dialog-title");

        Label message = new Label("To download and play this game, you need to acquire it first.");
        message.setWrapText(true);
        message.getStyleClass().add("purchase-dialog-message");

        VBox content = new VBox(12, icon, title, message);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("purchase-dialog-content");

        ButtonType yes = new ButtonType("Acquire game");
        ButtonType no = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yes, no);
        alert.getDialogPane().setContent(content);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/styles.css").toExternalForm()
        );

        alert.getDialogPane().getStyleClass().add("purchase-dialog-pane");

        Button yesButton = (Button) alert.getDialogPane().lookupButton(yes);
        yesButton.getStyleClass().add("purchase-dialog-confirm-button");

        Button noButton = (Button) alert.getDialogPane().lookupButton(no);
        noButton.getStyleClass().add("purchase-dialog-cancel-button");

        return alert.showAndWait().orElse(no) == yes;
    }

    private boolean performPurchase(Integer gameId) {
        Integer clientId = sessionManager.getClientId();

        try {
            PurchaseCreateDTO dto = new PurchaseCreateDTO();
            dto.setClientId(clientId);
            dto.setGameId(gameId);

            ResponseEntity<?> response =
                    purchaseApiService.createPurchase(
                            buildHeader(),
                            dto
                    );

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            showError("Purchase failed", "Could not complete the purchase.");
            return false;
        }
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    private void switchProfileScene() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    @FXML
    private void switchToMarketplaceScene() {
        stageManager.switchScene(FxmlView.MARKETPLACE);
    }

    @FXML
    private void switchToLibraryScene() {
        stageManager.switchScene(FxmlView.LIBRARY);
    }

    @FXML
    private void switchToUploadGameScene() {
        stageManager.switchScene(FxmlView.UPLOAD_GAME);
    }

    @FXML
    public void reloadView() {
        stageManager.reloadCurrentScene();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String buildHeader() {
        return sessionManager.getAuthorizationHeader();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}