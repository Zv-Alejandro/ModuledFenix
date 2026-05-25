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
 * <p>The base layout is the shared structure used by the main screens of the application.
 * It contains:</p>
 *
 * <ul>
 *     <li>The global navbar at the top.</li>
 *     <li>A central content area where each screen is injected dynamically.</li>
 *     <li>A global progress bar at the bottom.</li>
 * </ul>
 *
 * <p>This controller is intentionally simple. It should not contain business logic from
 * Marketplace, Library, Profile or Game screens. Its responsibility is only to manage the
 * shared layout.</p>
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
     * Configures the base layout after the FXML has been loaded.
     *
     * <p>The root and content area are allowed to grow as much as the stage allows. This is
     * important for maximized screens, because injected views may have fixed preferred sizes
     * in their FXML files.</p>
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
     * <p>If the injected node is a {@link Region}, it is configured so it can expand to fill
     * the available space inside the {@link StackPane}. This avoids visual issues where the
     * application window is maximized but the screen content remains stuck at its preferred
     * FXML width, leaving an empty area on the right.</p>
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
     * Returns the global progress bar in case another controller needs direct access to it.
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