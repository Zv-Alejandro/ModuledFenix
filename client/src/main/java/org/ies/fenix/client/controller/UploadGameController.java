package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UploadGameController {

    private static final String DEFAULT_PRICE = "0";
    private static final int MAX_SELECTED_TAGS = 6;

    private static final List<String> AVAILABLE_TAGS = List.of(
            "Romance",
            "Mystery",
            "Horror",
            "Drama",
            "Fantasy",
            "Sci-fi",
            "Comedy",
            "Adventure",
            "Psychological",
            "Thriller",
            "Slice of Life",
            "Supernatural",
            "Detective",
            "School",
            "Music",
            "Post-apocalyptic",
            "Cyberpunk",
            "Historical",
            "Action",
            "Puzzle",
            "Emotional"
    );

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private FlowPane tagsContainer;

    @FXML
    private Label selectedTagsLabel;

    @FXML
    private Label gameFileNameLabel;

    @FXML
    private Label gameDropAreaLabel;

    @FXML
    private Label logoFileNameLabel;

    @FXML
    private Label verticalFileNameLabel;

    @FXML
    private Label horizontalOneFileNameLabel;

    @FXML
    private Label horizontalTwoFileNameLabel;

    @FXML
    private ImageView logoImageView;

    @FXML
    private ImageView verticalImageView;

    @FXML
    private ImageView horizontalImageOneView;

    @FXML
    private ImageView horizontalImageTwoView;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;
    private final RestClient restClient;

    // ============================================================
    // SELECTED DATA
    // ============================================================

    private final Set<String> selectedTags = new LinkedHashSet<>();

    private File selectedGameFile;
    private File selectedLogoImage;
    private File selectedVerticalImage;
    private File selectedHorizontalImageOne;
    private File selectedHorizontalImageTwo;

    public UploadGameController(StageManager stageManager,
                                IGameController gameApiService,
                                SessionManager sessionManager,
                                RestClient restClient) {
        this.stageManager = stageManager;
        this.gameApiService = gameApiService;
        this.sessionManager = sessionManager;
        this.restClient = restClient;
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    private void initialize() {
        loadAvailableTags();
        updateSelectedTagsLabel();
    }

    private void loadAvailableTags() {
        tagsContainer.getChildren().clear();

        for (String tag : AVAILABLE_TAGS) {
            ToggleButton tagButton = new ToggleButton(tag);
            tagButton.getStyleClass().add("tag-chip");

            tagButton.setOnAction(event -> handleTagSelection(tagButton, tag));

            tagsContainer.getChildren().add(tagButton);
        }
    }

    private void handleTagSelection(ToggleButton tagButton, String tag) {
        if (tagButton.isSelected()) {
            if (selectedTags.size() >= MAX_SELECTED_TAGS) {
                tagButton.setSelected(false);
                showError(
                        "Tag limit reached",
                        "You can select up to " + MAX_SELECTED_TAGS + " tags."
                );
                return;
            }

            selectedTags.add(tag);
        } else {
            selectedTags.remove(tag);
        }

        updateSelectedTagsLabel();
    }

    private void updateSelectedTagsLabel() {
        if (selectedTags.isEmpty()) {
            selectedTagsLabel.setText("No tags selected");
            return;
        }

        selectedTagsLabel.setText(
                "Selected (" + selectedTags.size() + "/" + MAX_SELECTED_TAGS + "): "
                        + String.join(", ", selectedTags)
        );
    }

    // ============================================================
    // FORM ACTIONS
    // ============================================================

    @FXML
    private void clearForm() {
        stageManager.goBack();
    }

    @FXML
    private void submitGame() {
        try {
            if (!validateForm()) {
                return;
            }

            MultiValueMap<String, Object> body = buildUploadBody();
            ResponseEntity<GameResponseDTO> response = uploadGame(body);

            if (response.getStatusCode().is2xxSuccessful()) {
                showInfo("Game published", "Your game has been published successfully.");
                stageManager.switchScene(FxmlView.MARKETPLACE);
                return;
            }

            showError("Upload failed", "The server could not publish the game.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Upload failed", "There was an error publishing the game.");
        }
    }

    private boolean validateForm() {
        if (titleField.getText() == null || titleField.getText().isBlank()) {
            showError("Missing title", "You must write a title for the game.");
            return false;
        }

        if (selectedGameFile == null) {
            showError("Missing game file", "You must choose a game file.");
            return false;
        }

        if (selectedLogoImage == null) {
            showError("Missing logo", "You must choose a logo image.");
            return false;
        }

        if (selectedTags.size() > MAX_SELECTED_TAGS) {
            showError(
                    "Too many tags",
                    "You can select up to " + MAX_SELECTED_TAGS + " tags."
            );
            return false;
        }

        if (!AVAILABLE_TAGS.containsAll(selectedTags)) {
            showError(
                    "Invalid tags",
                    "Only the available tags can be used."
            );
            return false;
        }

        return true;
    }

    // ============================================================
    // UPLOAD
    // ============================================================

    private MultiValueMap<String, Object> buildUploadBody() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("title", titleField.getText().trim());
        body.add("description", getDescriptionText());
        body.add("tags", getTagsText());
        body.add("price", DEFAULT_PRICE);

        body.add("gameFile", new FileSystemResource(selectedGameFile));
        body.add("logoFile", new FileSystemResource(selectedLogoImage));

        addOptionalFile(body, "verticalImage", selectedVerticalImage);
        addOptionalFile(body, "horizontalImageOne", selectedHorizontalImageOne);
        addOptionalFile(body, "horizontalImageTwo", selectedHorizontalImageTwo);

        return body;
    }

    private void addOptionalFile(MultiValueMap<String, Object> body,
                                 String name,
                                 File file) {
        if (file != null) {
            body.add(name, new FileSystemResource(file));
        }
    }

    private ResponseEntity<GameResponseDTO> uploadGame(MultiValueMap<String, Object> body) {
        return restClient.post()
                .uri("/api/games/create/upload")
                .header("Authorization", sessionManager.getAuthorizationHeader())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toEntity(GameResponseDTO.class);
    }

    // ============================================================
    // FILE CHOOSERS
    // ============================================================

    @FXML
    private void chooseGameFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose game file");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Game files", "*.zip")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile == null) {
            return;
        }

        selectedGameFile = selectedFile;

        gameFileNameLabel.setText(selectedFile.getName());
        updateGameDropArea(selectedFile);
    }

    private void updateGameDropArea(File selectedFile) {
        if (gameDropAreaLabel == null || selectedFile == null) {
            return;
        }

        gameDropAreaLabel.setText("Selected file:\n" + selectedFile.getName());

        gameDropAreaLabel.getStyleClass().remove("upload-placeholder-text");

        if (!gameDropAreaLabel.getStyleClass().contains("upload-selected-file-text")) {
            gameDropAreaLabel.getStyleClass().add("upload-selected-file-text");
        }
    }

    @FXML
    private void chooseLogoImage() {
        File selectedFile = chooseImageFile("Choose game logo");

        if (selectedFile == null) {
            return;
        }

        selectedLogoImage = selectedFile;
        logoFileNameLabel.setText(selectedFile.getName());
        logoImageView.setImage(new Image(selectedFile.toURI().toString()));
    }

    @FXML
    private void chooseVerticalImage() {
        File selectedFile = chooseImageFile("Choose marketplace cover");

        if (selectedFile == null) {
            return;
        }

        selectedVerticalImage = selectedFile;
        verticalFileNameLabel.setText(selectedFile.getName());
        verticalImageView.setImage(new Image(selectedFile.toURI().toString()));
    }

    @FXML
    private void chooseHorizontalImageOne() {
        File selectedFile = chooseImageFile("Choose game page banner");

        if (selectedFile == null) {
            return;
        }

        selectedHorizontalImageOne = selectedFile;
        horizontalOneFileNameLabel.setText(selectedFile.getName());
        horizontalImageOneView.setImage(new Image(selectedFile.toURI().toString()));
    }

    @FXML
    private void chooseHorizontalImageTwo() {
        File selectedFile = chooseImageFile("Choose preview image");

        if (selectedFile == null) {
            return;
        }

        selectedHorizontalImageTwo = selectedFile;
        horizontalTwoFileNameLabel.setText(selectedFile.getName());
        horizontalImageTwoView.setImage(new Image(selectedFile.toURI().toString()));
    }

    private File chooseImageFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg",
                        "*.webp"
                )
        );

        return fileChooser.showOpenDialog(null);
    }

    // ============================================================
    // ALERTS
    // ============================================================

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String getDescriptionText() {
        if (descriptionArea.getText() == null) {
            return "";
        }

        return descriptionArea.getText().trim();
    }

    private String getTagsText() {
        return String.join(", ", selectedTags);
    }
}