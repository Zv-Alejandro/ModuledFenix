package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.client.utils.ImageUtils;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.ITagController;
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

    private static final double LOGO_PREVIEW_SIZE = 140.0;
    private static final double VERTICAL_PREVIEW_WIDTH = 255.0;
    private static final double VERTICAL_PREVIEW_HEIGHT = 370.0;
    private static final double HORIZONTAL_PREVIEW_WIDTH = 760.0;
    private static final double HORIZONTAL_PREVIEW_HEIGHT = 180.0;

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
    private final ITagController tagApiService;
    private final SessionManager sessionManager;
    private final RestClient restClient;

    // ============================================================
    // SELECTED DATA
    // ============================================================

    private final Set<String> availableTags = new LinkedHashSet<>();
    private final Set<String> selectedTags = new LinkedHashSet<>();

    private File selectedGameFile;
    private File selectedLogoImage;
    private File selectedVerticalImage;
    private File selectedHorizontalImageOne;
    private File selectedHorizontalImageTwo;

    public UploadGameController(StageManager stageManager,
                                IGameController gameApiService,
                                ITagController tagApiService,
                                SessionManager sessionManager,
                                RestClient restClient) {
        this.stageManager = stageManager;
        this.gameApiService = gameApiService;
        this.tagApiService = tagApiService;
        this.sessionManager = sessionManager;
        this.restClient = restClient;
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    private void initialize() {
        configureImageViews();
        loadAvailableTags();
        updateSelectedTagsLabel();
    }

    private void configureImageViews() {
        logoImageView.setPreserveRatio(false);
        logoImageView.setSmooth(true);

        verticalImageView.setPreserveRatio(false);
        verticalImageView.setSmooth(true);

        horizontalImageOneView.setPreserveRatio(false);
        horizontalImageOneView.setSmooth(true);

        horizontalImageTwoView.setPreserveRatio(false);
        horizontalImageTwoView.setSmooth(true);
    }

    // ============================================================
    // TAGS
    // ============================================================

    private void loadAvailableTags() {
        tagsContainer.getChildren().clear();
        selectedTags.clear();
        availableTags.clear();

        try {
            ResponseEntity<List<String>> response =
                    tagApiService.getNames(sessionManager.getAuthorizationHeader());

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().isEmpty()) {

                showError("Tags unavailable", "No tags were found in the database.");
                updateSelectedTagsLabel();
                return;
            }

            availableTags.addAll(response.getBody());
            renderAvailableTags();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Tags unavailable", "The tags could not be loaded from the database.");
        }

        updateSelectedTagsLabel();
    }

    private void renderAvailableTags() {
        tagsContainer.getChildren().clear();

        for (String tag : availableTags) {
            ToggleButton tagButton = new ToggleButton(tag);
            tagButton.getStyleClass().add("tag-chip");

            tagButton.setOnAction(event -> handleTagSelection(tagButton, tag));

            tagsContainer.getChildren().add(tagButton);
        }
    }

    private void handleTagSelection(ToggleButton tagButton, String tag) {
        if (!availableTags.contains(tag)) {
            tagButton.setSelected(false);
            showError("Invalid tag", "This tag is not available in the database.");
            return;
        }

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

        if (!availableTags.containsAll(selectedTags)) {
            showError(
                    "Invalid tags",
                    "Only tags from the database can be used."
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

        ImageUtils.setAvatarFromFile(
                selectedFile,
                logoImageView,
                LOGO_PREVIEW_SIZE
        );
    }

    @FXML
    private void chooseVerticalImage() {
        File selectedFile = chooseImageFile("Choose Library cover");

        if (selectedFile == null) {
            return;
        }

        selectedVerticalImage = selectedFile;
        verticalFileNameLabel.setText(selectedFile.getName());

        ImageUtils.setCoverImageFromFile(
                selectedFile,
                verticalImageView,
                VERTICAL_PREVIEW_WIDTH,
                VERTICAL_PREVIEW_HEIGHT
        );
    }

    @FXML
    private void chooseHorizontalImageOne() {
        File selectedFile = chooseImageFile("Choose Marketplace image");

        if (selectedFile == null) {
            return;
        }

        selectedHorizontalImageOne = selectedFile;
        horizontalOneFileNameLabel.setText(selectedFile.getName());

        ImageUtils.setCoverImageFromFile(
                selectedFile,
                horizontalImageOneView,
                HORIZONTAL_PREVIEW_WIDTH,
                HORIZONTAL_PREVIEW_HEIGHT
        );
    }

    @FXML
    private void chooseHorizontalImageTwo() {
        File selectedFile = chooseImageFile("Choose Game page background");

        if (selectedFile == null) {
            return;
        }

        selectedHorizontalImageTwo = selectedFile;
        horizontalTwoFileNameLabel.setText(selectedFile.getName());

        ImageUtils.setCoverImageFromFile(
                selectedFile,
                horizontalImageTwoView,
                HORIZONTAL_PREVIEW_WIDTH,
                HORIZONTAL_PREVIEW_HEIGHT
        );
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
        showStyledAlert(
                Alert.AlertType.INFORMATION,
                title,
                content,
                "mdi2c-check-circle-outline",
                "upload-alert-info-icon"
        );
    }

    private void showError(String title, String content) {
        showStyledAlert(
                Alert.AlertType.ERROR,
                title,
                content,
                "mdi2a-alert-circle-outline",
                "upload-alert-error-icon"
        );
    }

    private void showStyledAlert(Alert.AlertType type,
                                 String title,
                                 String content,
                                 String iconLiteral,
                                 String iconStyleClass) {
        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("upload-alert-pane");
        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/styles.css").toExternalForm()
        );

        ButtonType acceptButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(acceptButton);

        dialogPane.setContent(createAlertContent(title, content, iconLiteral, iconStyleClass));

        dialogPane.lookupButton(acceptButton)
                .getStyleClass()
                .add("upload-alert-button");

        alert.showAndWait();
    }

    private HBox createAlertContent(String title,
                                    String content,
                                    String iconLiteral,
                                    String iconStyleClass) {
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(46);
        icon.getStyleClass().add(iconStyleClass);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("upload-alert-title");

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("upload-alert-message");

        VBox textBox = new VBox(8, titleLabel, contentLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox contentBox = new HBox(18, icon, textBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(22, 26, 16, 26));
        contentBox.getStyleClass().add("upload-alert-content");

        return contentBox;
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