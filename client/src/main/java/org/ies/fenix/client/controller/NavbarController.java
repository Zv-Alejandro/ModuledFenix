package org.ies.fenix.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.client.utils.ImageUtils;
import org.ies.fenix.controller.IClientController;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.http.ResponseEntity;

/**
 * Controller for the global navigation bar.
 *
 * <p>The navbar is shared by the main application screens and provides access
 * to navigation history, main sections, profile information, logout and app
 * closing.</p>
 */
public class NavbarController {

    private static final String TAB_ACTIVE = "tab-active";
    private static final String TAB_INACTIVE = "tab-inactive";

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private FontIcon topProfileIcon;

    @FXML
    private ImageView topProfileImage;

    @FXML
    private Hyperlink marketplace;

    @FXML
    private Hyperlink library;

    @FXML
    private Hyperlink username;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final SessionManager sessionManager;

    /**
     * Creates the navbar controller with its required services.
     *
     * @param stageManager     application scene manager
     * @param clientApiService client API service
     * @param sessionManager   current user session manager
     */
    public NavbarController(StageManager stageManager,
                            IClientController clientApiService,
                            SessionManager sessionManager) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.sessionManager = sessionManager;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    /**
     * Initializes the navbar user data and history buttons.
     */
    @FXML
    public void initialize() {
        ImageUtils.initialConfig(
                clientApiService,
                sessionManager,
                username,
                topProfileImage,
                topProfileIcon
        );

        updateHistoryButtons();
    }

    // ============================================================
    // NAVIGATION HISTORY
    // ============================================================

    /**
     * Navigates to the previous screen stored in the navbar history.
     */
    @FXML
    public void goBackFromNavbar() {
        stageManager.goBackFromNavbar();
    }

    /**
     * Navigates to the next screen stored in the navbar history.
     */
    @FXML
    public void goForwardFromNavbar() {
        stageManager.goForwardFromNavbar();
    }

    /**
     * Enables or disables the history buttons depending on the current history state.
     */
    private void updateHistoryButtons() {
        if (backButton != null) {
            backButton.setDisable(!stageManager.canGoBackFromNavbar());
        }

        if (forwardButton != null) {
            forwardButton.setDisable(!stageManager.canGoForwardFromNavbar());
        }
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    public void goMarketplace() {
        stageManager.switchScene(FxmlView.MARKETPLACE);
    }

    @FXML
    public void goLibrary() {
        stageManager.switchScene(FxmlView.LIBRARY);
    }

    @FXML
    public void goProfile() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    @FXML
    public void goUpload() {
        stageManager.switchScene(FxmlView.UPLOAD_GAME);
    }

    @FXML
    public void goScriptEditor() {
        stageManager.switchScene(FxmlView.GUI);
    }

    // ============================================================
    // LOGOUT / CLOSE
    // ============================================================

    /**
     * Closes the current server session if possible, then clears the local session.
     */
    @FXML
    public void logout() {
        requestLogOut();
        closeLocalSession();
    }

    public void requestLogOut() {
        try {
            String authorization = sessionManager.getAuthorizationHeader();

            if (authorization != null) {
                ResponseEntity<Void> response = clientApiService.logout(authorization);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    showError("Logout failed", "The server could not close your session.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Logout failed", "There was an error closing your session.");
        }
    }

    /**
     * Logs out and closes the JavaFX application.
     */
    @FXML
    public void closeApplication() {
        requestLogOut();
        Platform.exit();
    }

    /**
     * Clears local session data and returns to the login screen.
     */
    private void closeLocalSession() {
        sessionManager.clearSession();
        stageManager.clearNavbarHistory();
        stageManager.switchScene(FxmlView.LOGIN);
    }

    // ============================================================
    // ACTIVE TAB
    // ============================================================

    /**
     * Updates the visual state of the navbar tabs.
     *
     * @param view currently active view
     */
    public void setActiveTab(FxmlView view) {
        clearTabStyles();

        switch (view) {
            case MARKETPLACE -> setActive(marketplace);
            case LIBRARY -> setActive(library);
            case PROFILE -> setActive(username);
        }

        setInactiveIfNeeded(marketplace);
        setInactiveIfNeeded(library);
        setInactiveIfNeeded(username);

        updateHistoryButtons();
    }

    private void clearTabStyles() {
        clearTabStyles(marketplace);
        clearTabStyles(library);
        clearTabStyles(username);
    }

    private void clearTabStyles(Hyperlink tab) {
        tab.getStyleClass().removeAll(TAB_ACTIVE, TAB_INACTIVE);
    }

    private void setActive(Hyperlink tab) {
        tab.getStyleClass().add(TAB_ACTIVE);
    }

    private void setInactiveIfNeeded(Hyperlink tab) {
        if (!tab.getStyleClass().contains(TAB_ACTIVE)) {
            tab.getStyleClass().add(TAB_INACTIVE);
        }
    }

    // ============================================================
    // ALERTS
    // ============================================================

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ============================================================
    // GETTERS
    // ============================================================

    /**
     * Returns the navbar profile image.
     *
     * @return top profile image view
     */
    public ImageView getTopProfileImage() {
        return topProfileImage;
    }

    /**
     * Returns the fallback profile icon shown when the user has no profile image.
     *
     * @return top profile icon
     */
    public FontIcon getTopProfileIcon() {
        return topProfileIcon;
    }
}