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

public class StageManager {

    private static final int MAX_NAVBAR_BACK_HISTORY = 2;

    private final Stage primaryStage;
    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;
    private final SceneResizeListener sceneResizeListener;

    private final Deque<FxmlView> backHistory = new ArrayDeque<>();
    private final Deque<FxmlView> forwardHistory = new ArrayDeque<>();

    private FxmlView currentView;
    private FxmlView previousView;
    private BaseLayoutController baseLayoutController;

    private Integer currentGameId;

    public StageManager(FxmlLoader fxmlLoader,
                        Stage primaryStage,
                        String applicationTitle,
                        SceneResizeListener sceneResizeListener) {
        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
        this.sceneResizeListener = sceneResizeListener;
    }

    public void switchScene(final FxmlView view) {
        switchSceneInternal(view, true);
    }

    public <T> T switchSceneAndGetController(final FxmlView view) {
        return switchSceneAndGetControllerInternal(view, true);
    }

    private void switchSceneInternal(final FxmlView view, boolean saveNavigationHistory) {
        previousView = currentView;

        if (saveNavigationHistory) {
            registerNormalNavigation(view);
        }

        currentView = view;

        Parent rootNode;

        try {
            rootNode = loadView(view, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createAndShowScene(rootNode);
    }

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
            throw new RuntimeException(e);
        }
    }

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

    private void addBackHistory(FxmlView view) {
        if (view == null || !view.usesBaseLayout()) {
            return;
        }

        backHistory.addLast(view);

        while (backHistory.size() > MAX_NAVBAR_BACK_HISTORY) {
            backHistory.removeFirst();
        }
    }

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

    public boolean canGoBackFromNavbar() {
        return !backHistory.isEmpty();
    }

    public boolean canGoForwardFromNavbar() {
        return !forwardHistory.isEmpty();
    }

    public void clearNavbarHistory() {
        backHistory.clear();
        forwardHistory.clear();
    }

    public void openGame(Integer gameId) {
        if (gameId == null) {
            return;
        }

        this.currentGameId = gameId;

        GameController gameController = switchSceneAndGetController(FxmlView.GAME);

        if (gameController != null) {
            gameController.setSelectedGameId(gameId);
        }
    }

    public void goBack() {
        if (previousView == null) {
            switchScene(FxmlView.MARKETPLACE);
            return;
        }

        switchScene(previousView);
    }

    public void reloadCurrentScene() {
        if (currentView == null) {
            throw new IllegalStateException("No hay vista cargada");
        }

        switchSceneInternal(currentView, false);
    }

    private Parent loadView(FxmlView view, Object ignoredController) throws IOException {
        if (view.usesBaseLayout()) {

            FXMLLoader baseLoader = fxmlLoader.createLoader("/fxml/base-layout.fxml");
            Parent baseRoot = baseLoader.load();
            this.baseLayoutController = baseLoader.getController();

            FXMLLoader contentLoader = fxmlLoader.createLoader(view.getFxmlPath());
            Parent content = contentLoader.load();

            Object controller = contentLoader.getController();

            configureControllerIfNeeded(view, controller);

            baseLayoutController.setContent(content);
            baseLayoutController.setActiveView(view);

            return baseRoot;
        }

        FXMLLoader loader = fxmlLoader.createLoader(view.getFxmlPath());
        return loader.load();
    }

    private <T> ControllerLoadResult<T> loadViewAndController(FxmlView view) throws IOException {
        Parent rootNode;
        T controller;

        if (view.usesBaseLayout()) {

            FXMLLoader baseLoader = fxmlLoader.createLoader("/fxml/base-layout.fxml");
            Parent baseRoot = baseLoader.load();
            this.baseLayoutController = baseLoader.getController();

            FXMLLoader contentLoader = fxmlLoader.createLoader(view.getFxmlPath());
            Parent content = contentLoader.load();

            controller = contentLoader.getController();

            configureControllerIfNeeded(view, controller);

            baseLayoutController.setContent(content);
            baseLayoutController.setActiveView(view);

            rootNode = baseRoot;

        } else {
            FXMLLoader loader = fxmlLoader.createLoader(view.getFxmlPath());
            rootNode = loader.load();
            controller = loader.getController();
        }

        return new ControllerLoadResult<>(rootNode, controller);
    }

    private void configureControllerIfNeeded(FxmlView view, Object controller) {
        if (view == FxmlView.GAME && controller instanceof GameController gameController) {
            if (currentGameId != null) {
                gameController.setSelectedGameId(currentGameId);
            }
        }
    }

    private void createAndShowScene(Parent rootNode) {
        createAndSetScene(rootNode);

        rootNode.applyCss();
        rootNode.autosize();

        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void createAndSetScene(Parent rootNode) {
        Scene scene = new Scene(rootNode);

        String stylesheet = Objects.requireNonNull(getClass()
                .getResource("/styles/styles.css")).toExternalForm();

        scene.getStylesheets().add(stylesheet);

        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            if (sceneResizeListener != null) {
                sceneResizeListener.onSceneResized(newSceneWidth);
            }
        });

        primaryStage.setTitle(applicationTitle);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setScene(scene);
    }

    public BaseLayoutController getBaseLayoutController() {
        return baseLayoutController;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void switchToFullScreenMode() {
        primaryStage.setFullScreen(true);
    }

    public void switchToWindowedMode() {
        primaryStage.setFullScreen(false);
    }

    public boolean isStageFullScreen() {
        return primaryStage.isFullScreen();
    }

    public void exit() {
        primaryStage.close();
    }

    private record ControllerLoadResult<T>(Parent rootNode, T controller) {
    }
}