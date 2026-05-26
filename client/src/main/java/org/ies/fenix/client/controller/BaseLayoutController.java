package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.ies.fenix.client.config.FxmlView;

/**
 * Controller for the main application layout.
 *
 * <p>This controller manages the shared structure used by the main screens of the
 * application. It is responsible for the global navbar, the central content area
 * and the global progress bar.</p>
 *
 * <p>Business logic from specific screens such as Marketplace, Library, Profile
 * or Game should not be placed here. This controller only manages the common
 * visual shell of the application.</p>
 */
public class BaseLayoutController {

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private BorderPane root;

    @FXML
    private HBox navbar;

    @FXML
    private StackPane contentArea;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private NavbarController navbarController;

    // ============================================================
    // INITIALIZATION
    // ============================================================

    /**
     * Configures the base layout once the FXML file has been loaded.
     *
     * <p>The root and the content area are allowed to grow as much as the stage
     * allows. This prevents injected views from staying fixed to their preferred
     * FXML size when the application window is maximized.</p>
     */
    @FXML
    private void initialize() {
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    // ============================================================
    // CONTENT MANAGEMENT
    // ============================================================

    /**
     * Replaces the current central content with the given node.
     *
     * <p>If the injected node is a {@link Region}, it is configured to expand
     * inside the {@link StackPane}. This avoids empty areas when the window is
     * resized or maximized.</p>
     *
     * @param node screen content to display in the central area
     */
    public void setContent(Node node) {
        if (node instanceof Region region) {
            region.setMinSize(0, 0);
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        contentArea.getChildren().setAll(node);
    }

    /**
     * Updates the active navbar tab according to the currently visible view.
     *
     * @param view currently active view
     */
    public void setActiveView(FxmlView view) {
        if (navbarController != null && view != null) {
            navbarController.setActiveTab(view);
        }
    }

    // ============================================================
    // GLOBAL PROGRESS BAR
    // ============================================================

    /**
     * Shows the global progress bar in indeterminate mode.
     *
     * <p>This is useful when an operation is running but its exact progress
     * cannot be calculated.</p>
     */
    public void showProgress() {
        progressBar.setVisible(true);
        progressBar.setManaged(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }

    /**
     * Hides and resets the global progress bar.
     */
    public void hideProgress() {
        progressBar.setVisible(false);
        progressBar.setManaged(false);
        progressBar.setProgress(0);
    }

    /**
     * Shows the global progress bar with a specific progress value.
     *
     * @param value progress value between {@code 0.0} and {@code 1.0}
     */
    public void setProgress(double value) {
        progressBar.setVisible(true);
        progressBar.setManaged(true);
        progressBar.setProgress(value);
    }

    /**
     * Returns the global progress bar in case another controller needs to bind
     * its progress property directly.
     *
     * @return global progress bar
     */
    public ProgressBar getGlobalProgressBar() {
        return progressBar;
    }

    // ============================================================
    // CHILD CONTROLLERS
    // ============================================================

    /**
     * Returns the controller of the included navbar.
     *
     * @return navbar controller
     */
    public NavbarController getNavbarController() {
        return navbarController;
    }
}