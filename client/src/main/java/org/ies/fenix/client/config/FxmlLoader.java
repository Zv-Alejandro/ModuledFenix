package org.ies.fenix.client.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

public class FxmlLoader {

    private Function<Class<?>, Object> controllerFactory = clazz -> {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate controller: " + clazz.getName(), e);
        }
    };

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public Parent load(String fxmlPath) throws IOException {
        URL location = getClass().getResource(fxmlPath);
        if (location == null) {
            throw new IllegalArgumentException("FXML not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(controllerFactory::apply);
        return loader.load();
    }
}