package org.ies.fenix.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class GameUploader {

    private static final String API_URL =
            "http://localhost:8080/api/games/create/upload";

    private static final String TOKEN =
            "452be70d605c482980c0b1340c53ce5c1aa3b307ebe34329a0e735fbe4a0a441";

    private static final Path GAMES_FOLDER =
            Path.of("C:\\Users\\aleja\\Pictures\\FenixGame");

    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    public static void main(String[] args) throws Exception {

        RestClient client = RestClient.create();

        try (Stream<Path> stream = Files.list(GAMES_FOLDER)) {

            stream.filter(Files::isDirectory)
                    .forEach(gameDir -> {

                        try {
                            uploadGame(client, gameDir);
                        } catch (Exception e) {
                            System.out.println(
                                    "Error uploading "
                                            + gameDir.getFileName()
                                            + ": "
                                            + e.getMessage()
                            );
                        }

                    });

        }
    }

    private static void uploadGame(RestClient client,
                                   Path gameDir) throws Exception {

        String title = gameDir.getFileName().toString().trim();

        Path metadataPath = gameDir.resolve("metadata.json");

        if (!Files.exists(metadataPath)) {
            System.out.println("Skipping " + title + ": metadata missing");
            return;
        }

        Metadata metadata = MAPPER.readValue(
                metadataPath.toFile(),
                Metadata.class
        );

        File gameZip = findFile(gameDir, "game");

        File logo = findFile(gameDir, "logo");

        if (gameZip == null) {
            System.out.println("Skipping " + title + ": game file missing");
            return;
        }

        if (logo == null) {
            System.out.println("Skipping " + title + ": logo not found");
            return;
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("title", title);
        body.add("description", metadata.getDescription());
        body.add("tags", String.join(", ", metadata.getTags()));
        body.add("price", String.valueOf(metadata.getPrice()));

        body.add("gameFile", new FileSystemResource(gameZip));
        body.add("logoFile", new FileSystemResource(logo));

        addOptional(body, "verticalImage", gameDir, "vertical");
        addOptional(body, "horizontalImageOne", gameDir, "horizontal2");
        addOptional(body, "horizontalImageTwo", gameDir, "horizontal1");
        System.out.println("Uploading: " + title);

        ResponseEntity<String> response =
                client.post()
                        .uri(API_URL)
                        .header("Authorization","Bearer "+TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(body)
                        .retrieve()
                        .toEntity(String.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("--------------------------------");
    }

    // 🔥 busca cualquier archivo que contenga keyword (sin importar extensión)
    private static File findFile(Path dir, String keyword) throws Exception {

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
    }

    // 🔥 opcionales sin extensión fija
    private static void addOptional(MultiValueMap<String, Object> body,
                                    String field,
                                    Path dir,
                                    String keyword) throws Exception {

        File file = findFile(dir, keyword);

        if (file != null) {
            body.add(field, new FileSystemResource(file));
        }
    }
}