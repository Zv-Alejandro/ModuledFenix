package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;
import org.ies.fenix.client.gui.util.BlockFactory;

public class SceneBlockView extends BaseBlockView {

    private SceneBlockModel model;
    private TextField sceneNameField;
    private VBox childrenContainer;

    // ============================================================
    //  MODO EDITOR
    // ============================================================
    public SceneBlockView(SceneBlockModel model) {
        this.model = model;

        getStyleClass().add("block-scene");

        Label title = new Label("SCENE");
        title.getStyleClass().add("block-label");

        sceneNameField = new TextField();
        sceneNameField.setPromptText("Scene name");
        sceneNameField.setText(model.getName());
        sceneNameField.getStyleClass().add("block-textfield");

        sceneNameField.textProperty().addListener((obs, oldV, newV) -> {
            model.setName(newV);
        });

        childrenContainer = new VBox(10);

        for (BaseBlockModel child : model.getChildren()) {
            childrenContainer.getChildren().add(BlockFactory.createView(child));
        }

        HBox row = new HBox(10, title, sceneNameField);
        row.getStyleClass().add("block-row");

        getChildren().addAll(row, childrenContainer);
    }

    // ============================================================
    //  MODO CATÁLOGO
    // ============================================================
    public SceneBlockView() {

        getStyleClass().add("block");
        Label sceneLabel = new Label("SCENE");
        sceneLabel.getStyleClass().add("block-label");

        TextField emptyField = new TextField();
        emptyField.setDisable(true);
        emptyField.getStyleClass().add("block-textfield");

        emptyField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(emptyField, Priority.ALWAYS);

        HBox topBar = new HBox(10, sceneLabel, emptyField);
        topBar.getStyleClass().add("block-row");

        // ESTA ES LA LÍNEA QUE FALTABA
        HBox.setHgrow(topBar, Priority.ALWAYS);

        getChildren().addAll(topBar);
    }




    public VBox getChildrenContainer() {
        return childrenContainer;
    }

    public SceneBlockModel getModel() {
        return model;
    }
}
