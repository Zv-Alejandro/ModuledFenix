package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.controller.dto.ServerResponseDTO;
import org.ies.fenix.controller.dto.client.ClientInfoDTO;
import org.ies.fenix.controller.dto.client.FileUploadDTO;
import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

import static org.ies.fenix.client.utils.ImageUtils.setAvatar;
import static org.ies.fenix.client.utils.ImageUtils.setCoverImage;

/**
 * Controller for the user profile screen.
 *
 * <p>This screen displays the current user's account information, biography,
 * profile picture, activity counters and games published by the user.</p>
 *
 * <p>It also allows the user to update their bio and upload a profile image.</p>
 */
public class ProfileController implements Initializable {

    private static final int CREATED_GAMES_COLUMNS = 3;
    private static final int PROFILE_IMAGE_SIZE = 180;
    private static final int NAVBAR_PROFILE_IMAGE_SIZE = 40;
    private static final int CREATED_GAME_LOGO_SIZE = 72;
    private static final int CREATED_GAME_LOGO_RADIUS = 36;

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    public TextField nameField;

    @FXML
    public TextField emailField;

    @FXML
    public PasswordField passwordField;

    @FXML
    private TextArea bio;

    @FXML
    private ImageView profileImage;

    @FXML
    private FontIcon profileIcon;

    @FXML
    private Label gamesCreatedValue;

    @FXML
    private Label gamesAcquiredValue;

    @FXML
    private GridPane createdGamesContainer;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;
    private final IPurchaseController purchaseApiService;

    /**
     * Creates the profile controller.
     *
     * @param stageManager       application scene manager
     * @param clientApiService   client API service
     * @param gameApiService     game API service
     * @param sessionManager     current user session manager
     * @param purchaseApiService purchase API service
     */
    public ProfileController(StageManager stageManager,
                             IClientController clientApiService,
                             IGameController gameApiService,
                             SessionManager sessionManager,
                             IPurchaseController purchaseApiService) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.gameApiService = gameApiService;
        this.sessionManager = sessionManager;
        this.purchaseApiService = purchaseApiService;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    /**
     * Loads all profile data once the FXML has been initialized.
     *
     * @param location  FXML location
     * @param resources localization resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClientInfo();
        loadBio();
        loadProfileImage();
        loadGamesAcquiredCount();
        loadCreatedGames();
    }

    // ============================================================
    // PROFILE DATA
    // ============================================================

    /**
     * Loads basic client information such as username, email, password mask
     * and created games count.
     */
    private void loadClientInfo() {
        try {
            ResponseEntity<ClientInfoDTO> response = clientApiService.getClientInfo(buildHeader());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return;
            }

            ClientInfoDTO clientInfo = response.getBody();

            nameField.setText(clientInfo.getUsername());
            emailField.setText(clientInfo.getEmail());
            passwordField.setText(buildPasswordMask(clientInfo.getPasswordCharacter()));
            gamesCreatedValue.setText(String.valueOf(clientInfo.getCreatedGamesCount()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the current user's biography.
     */
    private void loadBio() {
        try {
            ResponseEntity<String> response = clientApiService.getBio(buildHeader());

            if (response.getStatusCode().value() != 404) {
                bio.setText(response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the profile image if the user has uploaded one.
     */
    private void loadProfileImage() {
        try {
            ResponseEntity<byte[]> response = clientApiService.getProfileImage(buildHeader());

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().length > 0) {

                setCoverImage(response.getBody(), profileImage, PROFILE_IMAGE_SIZE);
                profileIcon.setVisible(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the amount of games acquired by the current user.
     */
    private void loadGamesAcquiredCount() {
        try {
            Integer clientId = sessionManager.getClientId();

            var response = purchaseApiService.getLibraryByClientId(
                    buildHeader(),
                    clientId
            );

            var games = response.getBody();
            int count = games == null ? 0 : games.size();

            gamesAcquiredValue.setText(String.valueOf(count));

        } catch (Exception e) {
            e.printStackTrace();
            gamesAcquiredValue.setText("0");
        }
    }

    /**
     * Loads and renders the games published by the current user.
     */
    private void loadCreatedGames() {
        createdGamesContainer.getChildren().clear();

        try {
            ResponseEntity<List<GameResponseDTO>> response =
                    gameApiService.getCreatedGamesByMe(buildHeader());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                showEmptyCreatedGamesMessage();
                return;
            }

            List<GameResponseDTO> games = response.getBody();

            gamesCreatedValue.setText(String.valueOf(games.size()));

            if (games.isEmpty()) {
                showEmptyCreatedGamesMessage();
                return;
            }

            renderCreatedGames(games);

        } catch (Exception e) {
            e.printStackTrace();
            showEmptyCreatedGamesMessage();
        }
    }

    /**
     * Places published game cards in a fixed-column grid.
     *
     * @param games games published by the current user
     */
    private void renderCreatedGames(List<GameResponseDTO> games) {
        int col = 0;
        int row = 0;

        for (GameResponseDTO game : games) {
            VBox card = createCreatedGameCard(game);

            createdGamesContainer.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setVgrow(card, Priority.NEVER);

            col++;

            if (col == CREATED_GAMES_COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Shows a message when the user has not published any games.
     */
    private void showEmptyCreatedGamesMessage() {
        Label emptyLabel = new Label("You have not published any games yet.");
        emptyLabel.setWrapText(true);
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);
        emptyLabel.getStyleClass().add("profile-created-games-empty");

        createdGamesContainer.add(emptyLabel, 0, 0);
        GridPane.setColumnSpan(emptyLabel, CREATED_GAMES_COLUMNS);
        GridPane.setHgrow(emptyLabel, Priority.ALWAYS);
    }

    // ============================================================
    // CREATED GAME CARDS
    // ============================================================

    private VBox createCreatedGameCard(GameResponseDTO game) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("profile-created-game-card");

        StackPane logoWrapper = createCreatedGameLogoWrapper(game);

        Label title = new Label(game.getTitle());
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);
        title.getStyleClass().add("profile-created-game-title");

        FlowPane tagsBox = createCreatedGameTagsBox(game);

        card.getChildren().addAll(logoWrapper, title, tagsBox);

        return card;
    }

    private StackPane createCreatedGameLogoWrapper(GameResponseDTO game) {
        StackPane wrapper = new StackPane();
        wrapper.getStyleClass().add("profile-created-game-logo-wrapper");

        ImageView logo = new ImageView();
        logo.setFitWidth(CREATED_GAME_LOGO_SIZE);
        logo.setFitHeight(CREATED_GAME_LOGO_SIZE);
        logo.setPreserveRatio(false);
        logo.setSmooth(true);

        Circle clip = new Circle(
                CREATED_GAME_LOGO_RADIUS,
                CREATED_GAME_LOGO_RADIUS,
                CREATED_GAME_LOGO_RADIUS
        );
        logo.setClip(clip);

        try {
            ResponseEntity<byte[]> response = gameApiService.getLogo(buildHeader(), game.getId());

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().length > 0) {

                logo.setImage(new Image(new ByteArrayInputStream(response.getBody())));
                wrapper.getChildren().add(logo);
                return wrapper;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FontIcon fallbackIcon = new FontIcon("mdi2g-gamepad-variant-outline");
        fallbackIcon.setIconSize(34);
        fallbackIcon.setIconColor(Color.web("#8C6A52"));

        wrapper.getChildren().add(fallbackIcon);
        return wrapper;
    }

    private FlowPane createCreatedGameTagsBox(GameResponseDTO game) {
        FlowPane tagsBox = new FlowPane();
        tagsBox.setHgap(7);
        tagsBox.setVgap(7);
        tagsBox.setAlignment(Pos.CENTER);
        tagsBox.setMaxWidth(Double.MAX_VALUE);

        if (game.getTags() == null || game.getTags().isEmpty()) {
            Label noTags = new Label("No tags");
            noTags.getStyleClass().add("profile-created-game-tag-empty");
            tagsBox.getChildren().add(noTags);
            return tagsBox;
        }

        for (String tag : game.getTags()) {
            Label tagLabel = new Label(tag);
            tagLabel.getStyleClass().add("profile-created-game-tag");
            tagsBox.getChildren().add(tagLabel);
        }

        return tagsBox;
    }

    // ============================================================
    // PROFILE ACTIONS
    // ============================================================

    /**
     * Updates the current user's biography and reloads the profile screen.
     */
    @FXML
    public void updateProfileBio() {
        try {
            ResponseEntity<ServerResponseDTO> response =
                    clientApiService.updateBio(buildHeader(), bio.getText());

            if (!response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null) {
                System.out.println(response.getBody().getMessage());
            }

            stageManager.reloadCurrentScene();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lets the user choose and upload a new profile picture.
     */
    @FXML
    public void uploadProfilePicture() {
        File selectedFile = chooseProfileImage();

        if (selectedFile == null) {
            return;
        }

        try {
            FileUploadDTO dto = buildFileUploadDTO(selectedFile);
            ResponseEntity<ServerResponseDTO> response = uploadProfileImage(dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                showAlert(
                        "no se pudo subir la imagen",
                        response.getBody() != null ? response.getBody().getMessage() : "error desconocido"
                );
                return;
            }

            refreshProfileImage();

        } catch (HttpClientErrorException e) {
            showAlert("no se pudo subir la imagen", e.getResponseBodyAsString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // IMAGE UPLOAD
    // ============================================================

    private File chooseProfileImage() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        return fileChooser.showOpenDialog(null);
    }

    private FileUploadDTO buildFileUploadDTO(File selectedFile) throws IOException {
        String mimeType = Files.probeContentType(selectedFile.toPath());
        byte[] bytes = Files.readAllBytes(selectedFile.toPath());

        return new FileUploadDTO(selectedFile.getName(), mimeType, bytes);
    }

    private ResponseEntity<ServerResponseDTO> uploadProfileImage(FileUploadDTO dto) {
        return clientApiService.uploadProfilePicture(buildHeader(), dto);
    }

    /**
     * Reloads the profile image after a successful upload and updates the navbar
     * avatar as well.
     */
    private void refreshProfileImage() {
        ResponseEntity<byte[]> response = clientApiService.getProfileImage(buildHeader());

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().length == 0) {
            return;
        }

        byte[] imageBytes = response.getBody();

        setCoverImage(imageBytes, profileImage, PROFILE_IMAGE_SIZE);
        profileIcon.setVisible(false);

        NavbarController navbar = stageManager
                .getBaseLayoutController()
                .getNavbarController();

        setAvatar(
                imageBytes,
                navbar.getTopProfileImage(),
                navbar.getTopProfileIcon(),
                NAVBAR_PROFILE_IMAGE_SIZE
        );

        System.out.println("profile picture updated");
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    private void switchLibraryScene() {
        stageManager.switchScene(FxmlView.LIBRARY);
    }

    @FXML
    private void switchToMarketplaceScene() {
        stageManager.switchScene(FxmlView.MARKETPLACE);
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
    // ALERTS
    // ============================================================

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setGraphic(null);

        if (message == null || message.isBlank()) {
            message = "Ha ocurrido un error desconocido.";
        }

        FontIcon icon = new FontIcon("mdi2a-alert-circle");
        icon.setIconSize(48);
        icon.getStyleClass().add("alert-icon");

        Label title = new Label(header);
        title.getStyleClass().add("alert-title");

        Label text = new Label(message);
        text.setWrapText(true);
        text.getStyleClass().add("alert-message");

        VBox content = new VBox(12, icon, title, text);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("alert-content");

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/alert.css").toExternalForm()
        );

        alert.showAndWait();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String buildHeader() {
        return sessionManager.getAuthorizationHeader();
    }

    private String buildPasswordMask(int passwordCharacter) {
        return "*".repeat(Math.max(0, passwordCharacter));
    }
}