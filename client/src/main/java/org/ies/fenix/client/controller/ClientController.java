package org.ies.fenix.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.dto.client.ClientLoginDTO;
import org.ies.fenix.controller.dto.client.ClientRegisterDTO;
import org.ies.fenix.controller.dto.client.LoginResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private Text title;

    @FXML
    private Text subtitle;

    @FXML
    private VBox center;

    @FXML
    private HBox upper;

    @FXML
    private BorderPane root;

    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView settingsImage;

    @FXML
    private Button loginButton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passwordCheck;

    @FXML
    private Label clientErrorLabel;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final SessionManager sessionManager;

    private final StringProperty errorProperty = new SimpleStringProperty();

    private String email = "";

    public ClientController(StageManager stageManager,
                            IClientController clientApiService,
                            SessionManager sessionManager) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.sessionManager = sessionManager;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureErrorLabel();
        configureErrorResetListeners();
    }

    private void configureErrorLabel() {
        clientErrorLabel.textProperty().bind(errorProperty);
        clientErrorLabel.visibleProperty().bind(errorProperty.isNotEmpty());
        clientErrorLabel.managedProperty().bind(clientErrorLabel.visibleProperty());
    }

    private void configureErrorResetListeners() {
        username.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        password.textProperty().addListener((obs, oldVal, newVal) -> clearError());

        if (passwordCheck != null) {
            passwordCheck.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        }
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public void loadUserAndOpenMarketPlace() {
        String name = username.getText();
        String rawPassword = password.getText();

        if (!validateLoginForm(name, rawPassword)) {
            return;
        }

        LoginResponseDTO body;

        try {
            ResponseEntity<LoginResponseDTO> response =
                    clientApiService.login(new ClientLoginDTO(name, rawPassword));

            if (!response.getStatusCode().is2xxSuccessful()) {
                showError(getLoginErrorMessage(response));
                return;
            }

            body = response.getBody();

        } catch (RestClientException e) {
            e.printStackTrace();
            showError("Could not connect to server.");
            return;
        }

        if (body == null || body.getToken() == null) {
            showError(getInvalidLoginResponseMessage(body));
            return;
        }

        saveSession(body);
        openMarketplaceAfterLogin();
    }

    private boolean validateLoginForm(String name, String rawPassword) {
        if (name.isBlank()) {
            showError("Username cannot be blank.");
            return false;
        }

        if (rawPassword.isBlank()) {
            showError("Password cannot be blank.");
            return false;
        }

        return true;
    }

    private String getLoginErrorMessage(ResponseEntity<LoginResponseDTO> response) {
        if (response.getBody() != null) {
            return response.getBody().getMessage();
        }

        return "Login failed.";
    }

    private String getInvalidLoginResponseMessage(LoginResponseDTO body) {
        if (body != null && body.getMessage() != null) {
            return body.getMessage();
        }

        return "Invalid server response.";
    }

    private void saveSession(LoginResponseDTO body) {
        sessionManager.saveSession(
                body.getToken(),
                body.getClientId(),
                body.getUsername()
        );

        clearError();
    }

    private void openMarketplaceAfterLogin() {
        try {
            stageManager.switchScene(FxmlView.MARKETPLACE);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Login ok, but Marketplace could not be loaded. Check console.");
        }
    }

    // ============================================================
    // REGISTER
    // ============================================================

    public void saveUserAndOpenLogInView() {
        String name = username.getText();
        String rawPassword = password.getText();
        String repeatedPassword = passwordCheck.getText();

        if (!validateRegisterForm(name, rawPassword, repeatedPassword)) {
            return;
        }

        try {
            ResponseEntity<?> response = clientApiService.register(
                    new ClientRegisterDTO(name, email, rawPassword)
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                showError(getRegisterErrorMessage(response));
                return;
            }

            clearError();
            stageManager.switchScene(FxmlView.LOGIN);

        } catch (RestClientException e) {
            e.printStackTrace();
            showError("Could not connect to server.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Register ok, but login view could not be loaded. Check console.");
        }
    }

    private boolean validateRegisterForm(String name, String rawPassword, String repeatedPassword) {
        if (name.isBlank() || name.length() > 20) {
            showError("Username must not be blank or longer than 20 characters.");
            return false;
        }

        if (!rawPassword.equals(repeatedPassword)) {
            showError("Passwords do not match.");
            return false;
        }

        if (rawPassword.length() >= 10) {
            showError("Password must be less than 10 characters.");
            return false;
        }

        return true;
    }

    private String getRegisterErrorMessage(ResponseEntity<?> response) {
        Object body = response.getBody();

        if (body instanceof String message) {
            return message;
        }

        return "Registration failed.";
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    void switchEmailFormView() {
        stageManager.switchScene(FxmlView.EMAIL);
    }

    void setEmail(String email) {
        this.email = email;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private void showError(String message) {
        errorProperty.set(message);
    }

    private void clearError() {
        errorProperty.set("");
    }
}