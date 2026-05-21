package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;
import org.ies.fenix.client.gui.util.BlockFactory;

public class SceneBlockView extends BaseBlockView {

    private SceneBlockModel model;
    private TextField sceneNameField;
    private VBox childrenContainer;

    public SceneBlockView(SceneBlockModel model) {
        this.model = model;

        Label title = new Label("SCENE");

        sceneNameField = new TextField();
        sceneNameField.setPromptText("Scene name");
        sceneNameField.setText(model.getName());

        sceneNameField.textProperty().addListener((obs, oldV, newV) -> {
            model.setName(newV);
        });

        childrenContainer = new VBox(10);

        // Cargar hijos existentes
        for (BaseBlockModel child : model.getChildren()) {
            childrenContainer.getChildren().add(BlockFactory.createView(child));
        }

        setSpacing(10);
        getChildren().addAll(title, sceneNameField, childrenContainer);

        setStyle("-fx-background-color: #dfe8ff; -fx-padding: 15; -fx-border-color: #4a6aff; -fx-border-width: 2;");
    }

    public SceneBlockView() {

        // --- BARRA SUPERIOR ---
        HBox topBar = new HBox(10);

        Label sceneLabel = new Label("SCENE");
        sceneLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

        TextField emptyField = new TextField();
        emptyField.setDisable(true);
        emptyField.setPrefWidth(200);
        emptyField.setStyle("-fx-background-color: white; -fx-border-color: black;");

        topBar.getChildren().addAll(sceneLabel, emptyField);
        topBar.setStyle("-fx-background-color: #f7d75c; -fx-padding: 5; -fx-border-color: black;");

        // --- BARRA INFERIOR ---
        HBox bottomBar = new HBox();
        bottomBar.setStyle("-fx-background-color: #f7d75c; -fx-padding: 10; -fx-border-color: black;");
        bottomBar.setPrefHeight(20);

        // --- LAYOUT GENERAL ---
        setSpacing(0);
        getChildren().addAll(topBar, bottomBar);

        setStyle("-fx-border-color: black; -fx-border-width: 2;");
    }

    public VBox getChildrenContainer() {
        return childrenContainer;
    }

    public SceneBlockModel getModel() {
        return model;
    }
}
