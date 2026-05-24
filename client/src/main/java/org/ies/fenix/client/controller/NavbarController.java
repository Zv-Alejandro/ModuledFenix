package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.client.utils.ImageUtils;
import org.ies.fenix.controller.IClientController;
import org.kordamp.ikonli.javafx.FontIcon;

public class NavbarController {

    private static final String TAB_ACTIVE = "tab-active";
    private static final String TAB_INACTIVE = "tab-inactive";

    // ============================================================
    // FXML FIELDS
    // ============================================================

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
    // GETTERS
    // ============================================================

    public ImageView getTopProfileImage() {
        return topProfileImage;
    }

    public FontIcon getTopProfileIcon() {
        return topProfileIcon;
    }
}