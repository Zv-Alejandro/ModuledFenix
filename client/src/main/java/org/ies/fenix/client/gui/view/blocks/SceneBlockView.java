package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.CharacterCreateBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;

import java.util.ArrayList;
import java.util.List;

public class SceneBlockView extends ContainerBlockView {

    private TextField sceneNameField;
    private final List<BaseBlockModel> structure = new ArrayList<>();
    // ============================================================
    //  MODO EDITOR
    // ============================================================
    public SceneBlockView(
            SceneBlockModel model,
            DragAndDropService dragService
    ) {

        super.model = model;
        getStyleClass().add("block-scene");

        Label title = new Label("SCENE");
        title.getStyleClass().add("block-label");

        sceneNameField = new TextField();

        sceneNameField.setPromptText("Scene name");

        sceneNameField.setText(model.getName());

        sceneNameField.getStyleClass()
                .add("block-textfield");

        sceneNameField.textProperty().addListener(
                (obs, oldV, newV) -> {

                    model.setName(newV);
                }
        );

        HBox row = new HBox(
                10,
                title,
                sceneNameField
        );

        row.getStyleClass().add("block-row");

        childrenContainer = new VBox(5);

        childrenContainer.setPadding(
                new Insets(0,0,0,10)
        );

        for (BaseBlockModel child : model.getChildren()) {

            BaseBlockView childView =
                    BlockFactory.createView(
                            child,child.getType(),
                            dragService
                    );

            childrenContainer
                    .getChildren()
                    .add(childView);
        }

        setupContainerDragAndDrop(dragService);

        VBox content = new VBox(5);

        content.getChildren().addAll(
                row,
                childrenContainer
        );

        getChildren().add(content);
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

    @Override
    public BaseBlockModel createModel() {
        return new SceneBlockModel();
    }


    public VBox getChildrenContainer() {
        return childrenContainer;
    }

    @Override
    public boolean canContain(BaseBlockModel child) {
        return switch (child.getType()) {

            case "character_create",
                 "text",
                 "dialog",
                 "decision",
                 "character"-> true;

            default -> false;
        };
    }
}
