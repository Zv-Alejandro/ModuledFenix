package org.ies.fenix.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ies.fenix.client.api.ClientApiService;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlLoader;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.client.controller.ClientControllerFx;
import org.ies.fenix.client.controller.EmailFormController;
import org.ies.fenix.client.controller.HomeController;
import org.ies.fenix.client.controller.MarketplaceController;
import org.ies.fenix.client.listener.SceneResizeListener;

public class Main extends Application {

    private static Stage stage;

    private StageManager stageManager;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        SceneResizeListener resizeListener = newWidth -> {
            System.out.println("Scene resized: " + newWidth);
        };

        FxmlLoader fxmlLoader = new FxmlLoader();
        String applicationTitle = "Fenix";

        ClientApiService clientApiService = new ClientApiService();
        SessionManager sessionManager = new SessionManager();

        stageManager = new StageManager(
                fxmlLoader,
                primaryStage,
                applicationTitle,
                resizeListener
        );

        fxmlLoader.setControllerFactory(clazz -> {
            if (clazz == ClientControllerFx.class) {
                return new ClientControllerFx(stageManager, clientApiService, sessionManager);
            }

            if (clazz == EmailFormController.class) {
                return new EmailFormController(stageManager);
            }

            if (clazz == HomeController.class) {
                return new HomeController(stageManager);
            }

            if (clazz == MarketplaceController.class) {
                return new MarketplaceController();
            }

            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot instantiate controller: " + clazz.getName(), e);
            }
        });

        showLoginScene();
    }

    @Override
    public void stop() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showLoginScene() {
        stageManager.switchScene(FxmlView.LOGIN);
    }
}
