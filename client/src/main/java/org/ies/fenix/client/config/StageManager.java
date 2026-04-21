package org.ies.fenix.client.config;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StageManager {

    private Stage primaryStage;
    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;
    private final java.util.function.Function<Class<?>, Object> controllerFactory;

    public StageManager(FxmlLoader fxmlLoader,
                        String applicationTitle,
                        java.util.function.Function<Class<?>, Object> controllerFactory) {
        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
        this.controllerFactory = controllerFactory;
        this.fxmlLoader.setControllerFactory(controllerFactory);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(final FxmlView view) {
        primaryStage.setTitle(applicationTitle);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        Parent rootNode = loadRootNode(view.getFxmlPath());
        Scene scene = new Scene(rootNode);

        String stylesheet = Objects.requireNonNull(getClass()
                        .getResource("/styles/styles.css"))
                .toExternalForm();
        scene.getStylesheets().add(stylesheet);

        scene.widthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number oldSceneWidth,
                                Number newSceneWidth) {
                // no Spring event bus; handle resize locally if needed
            }
        });

        primaryStage.setScene(scene);
        rootNode.applyCss();
        rootNode.autosize();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void switchToNextScene(final FxmlView view) {
        Parent rootNode = loadRootNode(view.getFxmlPath());
        rootNode.applyCss();
        rootNode.autosize();

        Scene scene = new Scene(rootNode);
        String stylesheet = Objects.requireNonNull(getClass()
                        .getResource("/styles/styles.css"))
                .toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private Parent loadRootNode(String fxmlPath) {
        try {
            return fxmlLoader.load(fxmlPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}