package org.ies.fenix.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ies.fenix.client.config.FxmlLoader;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;

public class HelloApplication extends Application {

    private static Stage stage;

    private StageManager stageManager;

    @Override
    public void init() {
        FxmlLoader fxmlLoader = new FxmlLoader();
        stageManager = new StageManager(
                fxmlLoader,
                "ClientApp",
                this::createController
        );
    }

    @Override
    public void stop() {
        if (stage != null) {
            stage.close();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stageManager.setPrimaryStage(primaryStage);
        stageManager.switchScene(FxmlView.LOGIN);
    }

    private Object createController(Class<?> controllerClass) {
        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create controller: " + controllerClass.getName(), e);
        }
    }
}
