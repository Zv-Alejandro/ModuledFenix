package org.ies.fenix.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ies.fenix.client.config.FxmlLoader;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.listener.SceneResizeListener;
import org.ies.fenix.client.config.StageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends Application {

    private static Stage stage;

    private StageManager stageManager;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        SceneResizeListener resizeListener = newWidth -> {
            // handle resize here, or forward it to another object
            System.out.println("Scene resized: " + newWidth);
        };

        FxmlLoader fxmlLoader = new FxmlLoader();
        String applicationTitle = "Fenix";

        stageManager = new StageManager(
                fxmlLoader,
                primaryStage,
                applicationTitle,
                resizeListener
        );
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

    private String getApiResponse(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
