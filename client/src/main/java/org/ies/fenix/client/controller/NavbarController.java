package org.ies.fenix.client.controller;

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

    @FXML
    public void goBackFromNavbar() {
        stageManager.goBackFromNavbar();
    }

    @FXML
    public void goForwardFromNavbar() {
        stageManager.goForwardFromNavbar();
    }

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

    // ============================================================
    // LOGOUT
    // ============================================================

    @FXML
    public void logout() {
        try {
            String authorization = sessionManager.getAuthorizationHeader();

            if (authorization != null) {
                ResponseEntity<Void> response = clientApiService.logout(authorization);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    showError("Logout failed", "The server could not close your session.");
                    return;
                }
            }

            closeLocalSession();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Logout failed", "There was an error closing your session.");
        }
    }

    private void closeLocalSession() {
        sessionManager.clearSession();
        stageManager.clearNavbarHistory();
        stageManager.switchScene(FxmlView.LOGIN);
    }

    // ============================================================
    // ACTIVE TAB
    // ============================================================

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

    public ImageView getTopProfileImage() {
        return topProfileImage;
    }

    public FontIcon getTopProfileIcon() {
        return topProfileIcon;
    }
}