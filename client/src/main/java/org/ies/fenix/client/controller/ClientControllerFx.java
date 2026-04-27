package org.ies.fenix.client.controller;

import dto.client.LoginResponseDTO;
import dto.client.RegisterResponseDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.ies.fenix.client.api.ClientApiService;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientControllerFx implements Initializable {

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

    private final StringProperty errorProperty = new SimpleStringProperty();

    private final StageManager stageManager;
    private final ClientApiService clientApiService;
    private final SessionManager sessionManager;

    public ClientControllerFx(StageManager stageManager,
                              ClientApiService clientApiService,
                              SessionManager sessionManager) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void loadUserAndOpenMarketPlace() {
        clientErrorLabel.setVisible(true);

        String name = username.getText();
        String rawPassword = password.getText();

        if (name == null || name.isBlank()) {
            errorProperty.setValue("Username must not be blank.");
            return;
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            errorProperty.setValue("Password must not be blank.");
            return;
        }

        try {
            LoginResponseDTO response = clientApiService.login(name, rawPassword);

            if (!"OK".equalsIgnoreCase(response.getStatus()) || response.getToken() == null) {
                errorProperty.setValue(response.getMessage());
                return;
            }

            sessionManager.saveSession(
                    response.getToken(),
                    response.getClientId(),
                    response.getUsername()
            );

            clientErrorLabel.setVisible(false);
            stageManager.switchToNextScene(FxmlView.MARKETPLACE);
        } catch (RuntimeException e) {
            errorProperty.setValue("Could not connect to server.");
        }
    }

    @FXML
    public void saveUserAndOpenLogInView() {
        clientErrorLabel.setVisible(true);

        String name = username.getText();
        String rawPassword = password.getText();
        String repeatedPassword = passwordCheck.getText();

        if (name == null || name.isBlank() || name.length() > 20) {
            errorProperty.setValue("Username must not be blank or longer than 20 characters.");
            return;
        }

        if (!rawPassword.equals(repeatedPassword)) {
            errorProperty.setValue("Passwords don't match.");
            return;
        }

        if (rawPassword.length() >= 10) {
            errorProperty.setValue("The password must be less than 10 characters long.");
            return;
        }

        try {
            RegisterResponseDTO response = clientApiService.register(
                    name,
                    name + "@fenix.local",
                    rawPassword
            );

            if (!"OK".equalsIgnoreCase(response.getStatus())) {
                errorProperty.setValue(response.getMessage());
                return;
            }

            clientErrorLabel.setVisible(false);
            stageManager.switchToNextScene(FxmlView.LOGIN);
        } catch (RuntimeException e) {
            errorProperty.setValue("Could not connect to server.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientErrorLabel.textProperty().bind(errorProperty);
        clientErrorLabel.setVisible(false);

        username.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldText,
                                String newText) {
                errorProperty.setValue("");
                clientErrorLabel.setVisible(false);
            }
        });
    }

    @FXML
    void switchEmailFormView() {
        stageManager.switchToNextScene(FxmlView.EMAIL);
    }
}