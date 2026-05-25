package org.ies.fenix.client.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.ies.fenix.client.controller.BaseLayoutController;
import org.ies.fenix.client.controller.GameController;
import org.ies.fenix.client.listener.SceneResizeListener;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Manages the main JavaFX {@link Stage} of the application.
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *     <li>Loading FXML views.</li>
 *     <li>Switching between screens.</li>
 *     <li>Wrapping application screens inside the shared base layout.</li>
 *     <li>Managing navbar back/forward navigation history.</li>
 *     <li>Opening selected games by passing the current game id to {@link GameController}.</li>
 *     <li>Applying full screen or windowed mode depending on the target view.</li>
 * </ul>
 *
 * <p>Views that use the base layout are loaded inside {@code base-layout.fxml}, so they share
 * the global navbar and the global progress bar. Login and register views are loaded directly
 * without the base layout.</p>
 */
public class StageManager {

    private static final int MAX_NAVBAR_BACK_HISTORY = 2;

    // ============================================================
    // STAGE / LOADING DEPENDENCIES
    // ============================================================

    private final Stage primaryStage;
    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;
    private final SceneResizeListener sceneResizeListener;

    // ============================================================
    // NAVIGATION STATE
    // ============================================================

    private final Deque<FxmlView> backHistory = new ArrayDeque<>();
    private final Deque<FxmlView> forwardHistory = new ArrayDeque<>();

    private FxmlView currentView;
    private FxmlView previousView;

    // ============================================================
    // CURRENT LAYOUT / VIEW DATA
    // ============================================================

    private BaseLayoutController baseLayoutController;
    private Integer currentGameId;

    /**
     * Creates the stage manager used by the application.
     *
     * @param fxmlLoader          application FXML loader factory
     * @param primaryStage        main JavaFX stage
     * @param applicationTitle    title displayed in the application window
     * @param sceneResizeListener optional listener used when the scene width changes
     */
    public StageManager(FxmlLoader fxmlLoader,
                        Stage primaryStage,
                        String applicationTitle,
                        SceneResizeListener sceneResizeListener) {
        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
        this.sceneResizeListener = sceneResizeListener;
    }

    // ============================================================
    // PUBLIC SCREEN SWITCHING
    // ============================================================

    /**
     * Switches the current scene to the given view.
     *
     * <p>This method stores the current view in the navigation history when the target view
     * belongs to the main application layout.</p>
     *
     * @param view target view to display
     */
    public void switchScene(final FxmlView view) {
        switchSceneInternal(view, true);
    }

    /**
     * Switches the current scene to the given view and returns its controller.
     *
     * <p>This is useful when the caller needs to configure the controller immediately after
     * loading the view, for example when opening the Game screen with a selected game id.</p>
     *
     * @param view target view to display
     * @param <T>  expected controller type
     * @return controller instance of the loaded FXML
     */
    public <T> T switchSceneAndGetController(final FxmlView view) {
        return switchSceneAndGetControllerInternal(view, true);
    }

    /**
     * Reloads the current view without modifying the navbar navigation history.
     */
    public void reloadCurrentScene() {
        if (currentView == null) {
            throw new IllegalStateException("No hay vista cargada");
        }

        switchSceneInternal(currentView, false);
    }

    // ============================================================
    // INTERNAL SCREEN SWITCHING
    // ============================================================

    /**
     * Loads and displays a view.
     *
     * @param view                  target view
     * @param saveNavigationHistory true if the current view should be registered in history
     */
    private void switchSceneInternal(final FxmlView view, boolean saveNavigationHistory) {
        previousView = currentView;

        if (saveNavigationHistory) {
            registerNormalNavigation(view);
        }

        currentView = view;

        try {
            Parent rootNode = loadView(view);
            createAndShowScene(rootNode);
        } catch (IOException e) {
            throw new RuntimeException("Could not load view: " + view, e);
        }
    }

    /**
     * Loads and displays a view, returning the loaded controller.
     *
     * @param view                  target view
     * @param saveNavigationHistory true if the current view should be registered in history
     * @param <T>                   expected controller type
     * @return controller instance of the loaded FXML
     */
    private <T> T switchSceneAndGetControllerInternal(final FxmlView view, boolean saveNavigationHistory) {
        previousView = currentView;

        if (saveNavigationHistory) {
            registerNormalNavigation(view);
        }

        currentView = view;

        try {
            ControllerLoadResult<T> result = loadViewAndController(view);
            createAndShowScene(result.rootNode());
            return result.controller();
        } catch (IOException e) {
            throw new RuntimeException("Could not load view: " + view, e);
        }
    }

    // ============================================================
    // FXML LOADING
    // ============================================================

    /**
     * Loads the requested view.
     *
     * <p>If the view uses the base layout, this method first loads {@code base-layout.fxml},
     * then loads the requested content view and injects it into the base layout.</p>
     *
     * @param view target view
     * @return root node that must be placed in the scene
     * @throws IOException if the FXML cannot be loaded
     */
    private Parent loadView(FxmlView view) throws IOException {
        if (view.usesBaseLayout()) {
            FXMLLoader baseLoader = fxmlLoader.createLoader("/fxml/base-layout.fxml");
            Parent baseRoot = baseLoader.load();

            baseLayoutController = baseLoader.getController();

            FXMLLoader contentLoader = fxmlLoader.createLoader(view.getFxmlPath());
            Parent content = contentLoader.load();

            configureControllerIfNeeded(view, contentLoader.getController());

            baseLayoutController.setContent(content);
            baseLayoutController.setActiveView(view);

            return baseRoot;
        }

        FXMLLoader loader = fxmlLoader.createLoader(view.getFxmlPath());
        return loader.load();
    }

    /**
     * Loads the requested view and returns both its root node and controller.
     *
     * @param view target view
     * @param <T>  expected controller type
     * @return wrapper containing the root node and controller
     * @throws IOException if the FXML cannot be loaded
     */
    private <T> ControllerLoadResult<T> loadViewAndController(FxmlView view) throws IOException {
        if (view.usesBaseLayout()) {
            FXMLLoader baseLoader = fxmlLoader.createLoader("/fxml/base-layout.fxml");
            Parent baseRoot = baseLoader.load();

            baseLayoutController = baseLoader.getController();

            FXMLLoader contentLoader = fxmlLoader.createLoader(view.getFxmlPath());
            Parent content = contentLoader.load();
            T controller = contentLoader.getController();

            configureControllerIfNeeded(view, controller);

            baseLayoutController.setContent(content);
            baseLayoutController.setActiveView(view);

            return new ControllerLoadResult<>(baseRoot, controller);
        }

        FXMLLoader loader = fxmlLoader.createLoader(view.getFxmlPath());
        Parent rootNode = loader.load();
        T controller = loader.getController();

        return new ControllerLoadResult<>(rootNode, controller);
    }

    /**
     * Applies additional configuration to controllers that need data immediately after loading.
     *
     * @param view       loaded view
     * @param controller loaded controller
     */
    private void configureControllerIfNeeded(FxmlView view, Object controller) {
        if (view == FxmlView.GAME && controller instanceof GameController gameController) {
            if (currentGameId != null) {
                gameController.setSelectedGameId(currentGameId);
            }
        }
    }

    // ============================================================
    // SCENE / WINDOW CONFIGURATION
    // ============================================================

    /**
     * Creates or updates the JavaFX scene and applies the correct window mode.
     *
     * <p>The {@link Scene} is created only once. Later screen changes reuse the same scene and
     * replace only its root node. This avoids the visible resize transition that can appear when
     * assigning a new scene while the stage is already in full screen mode.</p>
     *
     * @param rootNode root node of the view to display
     */
    private void createAndShowScene(Parent rootNode) {
        boolean shouldUseFullScreen = shouldOpenFullScreen(currentView);

        prepareWindowForSceneChange(shouldUseFullScreen);
        createOrUpdateScene(rootNode);

        if (shouldUseFullScreen) {
            openAsFullScreen();
        } else {
            openAsWindowed();
        }
    }

    /**
     * Prepares the stage before replacing the current screen content.
     *
     * <p>If the next view is also full screen, the stage remains in full screen mode.
     * This avoids leaving and entering full screen again during navigation.</p>
     *
     * @param nextViewUsesFullScreen true if the next view should use full screen mode
     */
    private void prepareWindowForSceneChange(boolean nextViewUsesFullScreen) {
        primaryStage.setResizable(true);

        if (!nextViewUsesFullScreen && primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(false);
        }
    }

    /**
     * Opens the current scene in real full screen mode.
     *
     * <p>If the stage is already visible and already in full screen mode, this method does
     * not request full screen again. That prevents repeated full screen transitions when moving
     * between main application screens.</p>
     */
    private void openAsFullScreen() {
        primaryStage.setMaximized(false);

        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }

        if (!primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(true);
        }
    }

    /**
     * Opens the current scene as a normal window.
     */
    private void openAsWindowed() {
        primaryStage.setFullScreen(false);
        primaryStage.setMaximized(false);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Decides whether a view should open in real full screen mode.
     *
     * @param view target view
     * @return true if the view should use real full screen mode
     */
    private boolean shouldOpenFullScreen(FxmlView view) {
        if (view == null) {
            return false;
        }

        return switch (view) {
            case MARKETPLACE,
                 LIBRARY,
                 GAME,
                 PROFILE,
                 UPLOAD_GAME,
                 GUI -> true;

            case LOGIN,
                 EMAIL,
                 USER_CREATE -> false;
        };
    }

    /**
     * Creates the JavaFX scene once and reuses it on later screen changes.
     *
     * <p>Creating a new {@link Scene} on every navigation can make JavaFX briefly resize the
     * window, especially when the stage is in full screen mode. Reusing the scene and changing
     * only its root keeps the stage stable.</p>
     *
     * @param rootNode root node to display
     */
    private void createOrUpdateScene(Parent rootNode) {
        Scene currentScene = primaryStage.getScene();

        if (currentScene == null) {
            Scene scene = new Scene(rootNode);

            String stylesheet = Objects.requireNonNull(
                    getClass().getResource("/styles/styles.css"),
                    "Global stylesheet not found: /styles/styles.css"
            ).toExternalForm();

            scene.getStylesheets().add(stylesheet);

            scene.widthProperty().addListener((observable, oldWidth, newWidth) -> {
                if (sceneResizeListener != null) {
                    sceneResizeListener.onSceneResized(newWidth);
                }
            });

            primaryStage.setTitle(applicationTitle);
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            primaryStage.setScene(scene);
            return;
        }

        currentScene.setRoot(rootNode);
    }

    // ============================================================
    // NAVBAR HISTORY
    // ============================================================

    /**
     * Registers normal navigation between base-layout views.
     *
     * <p>Only views that use the base layout are stored in the navbar history. Login and other
     * independent screens clear the navbar history.</p>
     *
     * @param targetView view being opened
     */
    private void registerNormalNavigation(FxmlView targetView) {
        if (targetView == null) {
            return;
        }

        if (!targetView.usesBaseLayout()) {
            clearNavbarHistory();
            return;
        }

        if (currentView == null) {
            forwardHistory.clear();
            return;
        }

        if (!currentView.usesBaseLayout()) {
            clearNavbarHistory();
            return;
        }

        if (currentView == targetView) {
            forwardHistory.clear();
            return;
        }

        addBackHistory(currentView);
        forwardHistory.clear();
    }

    /**
     * Adds a view to the navbar back history.
     *
     * @param view view to store
     */
    private void addBackHistory(FxmlView view) {
        if (view == null || !view.usesBaseLayout()) {
            return;
        }

        backHistory.addLast(view);

        while (backHistory.size() > MAX_NAVBAR_BACK_HISTORY) {
            backHistory.removeFirst();
        }
    }

    /**
     * Opens the previous view from the navbar history.
     */
    public void goBackFromNavbar() {
        if (!canGoBackFromNavbar()) {
            return;
        }

        FxmlView targetView = backHistory.removeLast();

        if (currentView != null && currentView.usesBaseLayout()) {
            forwardHistory.addLast(currentView);
        }

        switchSceneInternal(targetView, false);
    }

    /**
     * Opens the next view from the navbar forward history.
     */
    public void goForwardFromNavbar() {
        if (!canGoForwardFromNavbar()) {
            return;
        }

        FxmlView targetView = forwardHistory.removeLast();

        if (currentView != null && currentView.usesBaseLayout()) {
            addBackHistory(currentView);
        }

        switchSceneInternal(targetView, false);
    }

    /**
     * @return true if there is a previous navbar view available
     */
    public boolean canGoBackFromNavbar() {
        return !backHistory.isEmpty();
    }

    /**
     * @return true if there is a next navbar view available
     */
    public boolean canGoForwardFromNavbar() {
        return !forwardHistory.isEmpty();
    }

    /**
     * Clears navbar back and forward history.
     */
    public void clearNavbarHistory() {
        backHistory.clear();
        forwardHistory.clear();
    }

    // ============================================================
    // GAME NAVIGATION
    // ============================================================

    /**
     * Opens the game detail screen for the given game id.
     *
     * @param gameId selected game id
     */
    public void openGame(Integer gameId) {
        if (gameId == null) {
            return;
        }

        currentGameId = gameId;

        GameController gameController = switchSceneAndGetController(FxmlView.GAME);

        if (gameController != null) {
            gameController.setSelectedGameId(gameId);
        }
    }

    /**
     * Goes back to the previously opened view.
     *
     * <p>This is a simple fallback navigation method used by screens that are not using the
     * navbar back/forward system directly.</p>
     */
    public void goBack() {
        if (previousView == null) {
            switchScene(FxmlView.MARKETPLACE);
            return;
        }

        switchScene(previousView);
    }

    // ============================================================
    // WINDOW MODE HELPERS
    // ============================================================

    /**
     * Enables real full screen mode.
     */
    public void switchToFullScreenMode() {
        primaryStage.setMaximized(false);

        if (!primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(true);
        }
    }

    /**
     * Returns the application to windowed mode.
     */
    public void switchToWindowedMode() {
        primaryStage.setFullScreen(false);
        primaryStage.setMaximized(false);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    /**
     * @return true if the stage is currently in full screen mode
     */
    public boolean isStageFullScreen() {
        return primaryStage.isFullScreen();
    }

    /**
     * Closes the main application window.
     */
    public void exit() {
        primaryStage.close();
    }

    // ============================================================
    // GETTERS
    // ============================================================

    /**
     * @return currently loaded base layout controller
     */
    public BaseLayoutController getBaseLayoutController() {
        return baseLayoutController;
    }

    /**
     * @return primary JavaFX stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // ============================================================
    // INTERNAL RECORDS
    // ============================================================

    /**
     * Small wrapper used when loading a view and its controller together.
     *
     * @param rootNode   root node loaded from FXML
     * @param controller controller loaded from FXML
     * @param <T>        controller type
     */
    private record ControllerLoadResult<T>(Parent rootNode, T controller) {
    }
}