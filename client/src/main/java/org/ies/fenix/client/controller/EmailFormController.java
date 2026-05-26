package org.ies.fenix.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;

import java.net.URL;
import java.util.ResourceBundle;

import static org.ies.fenix.client.utils.EmailValidator.isValidEmail;

/**
 * Controller for the first step of the user registration flow.
 *
 * <p>This screen asks the user for an email address before moving to the
 * account creation form. The email is validated locally and then passed to
 * {@link ClientController}, where the rest of the registration data is entered.</p>
 */
public class EmailFormController implements Initializable {

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
    private Button continueButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private Label clientErrorLabel;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;

    // ============================================================
    // STATE
    // ============================================================

    private final StringProperty errorProperty = new SimpleStringProperty();

    /**
     * Creates the controller with the required navigation manager.
     *
     * @param stageManager application scene manager
     */
    public EmailFormController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    /**
     * Configures bindings, listeners and static image properties after the FXML
     * has been loaded.
     *
     * @param location  FXML location
     * @param resources localization resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureErrorLabel();
        configureErrorResetListener();
        configureImages();
    }

    /**
     * Binds the error label to the internal error property.
     *
     * <p>The label is only visible and managed when there is an active error
     * message. This avoids leaving empty space in the layout.</p>
     */
    private void configureErrorLabel() {
        clientErrorLabel.textProperty().bind(errorProperty);
        clientErrorLabel.visibleProperty().bind(errorProperty.isNotEmpty());
        clientErrorLabel.managedProperty().bind(clientErrorLabel.visibleProperty());
    }

    /**
     * Clears the current error as soon as the user edits the email field.
     */
    private void configureErrorResetListener() {
        emailTextField.textProperty().addListener((observable, oldText, newText) -> clearError());
    }

    /**
     * Applies static configuration to the decorative images of the screen.
     */
    private void configureImages() {
        logoImage.setFitWidth(294.0);
        logoImage.setSmooth(true);

        settingsImage.setFitWidth(15.0);
        settingsImage.setSmooth(true);
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    /**
     * Validates the email and opens the user creation screen.
     *
     * <p>If the email is valid, it is passed to the next controller so the user
     * does not need to write it again.</p>
     */
    @FXML
    private void switchToUserCreateView() {
        String email = getEmailText();

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        ClientController controller =
                stageManager.switchSceneAndGetController(FxmlView.USER_CREATE);

        controller.setEmail(email);
    }

    /**
     * Returns to the login screen.
     */
    @FXML
    private void switchLogInView() {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    // ============================================================
    // HELPERS
    // ============================================================

    /**
     * Returns the current email text without surrounding blank spaces.
     *
     * @return trimmed email text, or an empty string if the field is empty
     */
    private String getEmailText() {
        if (emailTextField.getText() == null) {
            return "";
        }

        return emailTextField.getText().trim();
    }

    /**
     * Shows an error message in the form.
     *
     * @param message error message to display
     */
    private void showError(String message) {
        errorProperty.set(message);
    }

    /**
     * Clears the current form error.
     */
    private void clearError() {
        errorProperty.set("");
    }
}