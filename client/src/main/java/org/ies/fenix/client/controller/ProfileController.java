package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.controller.dto.ServerResponseDTO;
import org.ies.fenix.controller.dto.client.ClientInfoDTO;
import org.ies.fenix.controller.dto.client.FileUploadDTO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static org.ies.fenix.client.utils.ImageUtils.setAvatar;
import static org.ies.fenix.client.utils.ImageUtils.setCoverImage;

public class ProfileController implements Initializable {

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

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final SessionManager sessionManager;
    private final IPurchaseController purchaseApiService;

    public ProfileController(StageManager stageManager,
                             IClientController clientApiService,
                             SessionManager sessionManager,
                             IPurchaseController purchaseApiService) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.sessionManager = sessionManager;
        this.purchaseApiService = purchaseApiService;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClientInfo();
        loadBio();
        loadProfileImage();
        loadGamesAcquiredCount();
    }

    // ============================================================
    // PROFILE DATA
    // ============================================================

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

    private void loadProfileImage() {
        try {
            ResponseEntity<byte[]> response = clientApiService.getProfileImage(buildHeader());

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().length > 0) {

                setCoverImage(response.getBody(), profileImage, 180);
                profileIcon.setVisible(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    // ============================================================
    // PROFILE ACTIONS
    // ============================================================

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

    private void refreshProfileImage() {
        ResponseEntity<byte[]> response = clientApiService.getProfileImage(buildHeader());

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().length == 0) {
            return;
        }

        byte[] imageBytes = response.getBody();

        setCoverImage(imageBytes, profileImage, 180);
        profileIcon.setVisible(false);

        NavbarController navbar = stageManager
                .getBaseLayoutController()
                .getNavbarController();

        setAvatar(
                imageBytes,
                navbar.getTopProfileImage(),
                navbar.getTopProfileIcon(),
                40
        );

        System.out.println("profile picture updated");
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    void switchLibraryScene() {
        stageManager.switchScene(FxmlView.LIBRARY);
    }

    @FXML
    void switchToMarketplaceScene() {
        stageManager.switchScene(FxmlView.MARKETPLACE);
    }

    @FXML
    void switchToUploadGameScene() {
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