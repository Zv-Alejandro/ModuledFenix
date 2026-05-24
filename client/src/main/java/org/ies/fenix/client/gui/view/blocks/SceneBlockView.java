package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;

import java.util.ArrayList;
import java.util.List;

public class SceneBlockView extends ContainerBlockView {

    private TextField sceneNameField;
    private final List<BaseBlockModel> structure = new ArrayList<>();

    // ============================================================
    // MODO EDITOR
    // ============================================================
    public SceneBlockView(
            SceneBlockModel model,
            DragAndDropService dragService
    ) {

        super.model = model;

        getStyleClass().add("block-scene");

        // ============================================================
        // HEADER
        // ============================================================

        Label title = new Label("SCENE");
        title.getStyleClass().add("block-label");
        title.setMinWidth(Region.USE_PREF_SIZE);

        sceneNameField = new TextField();
        sceneNameField.setPromptText("Scene name");
        sceneNameField.setText(model.getName());
        sceneNameField.getStyleClass().add("block-textfield");

        sceneNameField.setPrefWidth(300);
        sceneNameField.setMinWidth(300);

        sceneNameField.textProperty().addListener((obs, oldV, newV) -> {
            model.setName(newV);
        });

        HBox row = new HBox(10, title, sceneNameField);
        row.getStyleClass().add("block-row");

        // IMPORTANTE: layout estable
        row.setMaxWidth(Region.USE_PREF_SIZE);
        HBox.setHgrow(title, Priority.NEVER);
        HBox.setHgrow(sceneNameField, Priority.NEVER);

        // ============================================================
        // CHILDREN
        // ============================================================

        childrenContainer = new VBox(5);
        childrenContainer.setPadding(new Insets(0, 0, 0, 20));
        childrenContainer.getStyleClass().add("block-children-container");

        // IMPORTANTE: el VBox debe recibir eventos de drag aunque esté vacío
        childrenContainer.setPickOnBounds(true);
        childrenContainer.setMinHeight(40);
        childrenContainer.setFillWidth(true);

        for (BaseBlockModel child : model.getChildren()) {

            BaseBlockView childView =
                    BlockFactory.createView(
                            child,
                            child.getType(),
                            dragService
                    );

            childrenContainer.getChildren().add(childView);
        }

        // ============================================================
        // DRAG & DROP (TODO EL BLOQUE)
        // ============================================================

        setupContainerDragAndDrop(dragService);

        // ============================================================
        // LAYOUT FINAL
        // ============================================================

        VBox content = new VBox(5, row, childrenContainer);
        content.getStyleClass().add("block-content");

        getChildren().add(content);

        // ============================================================
        // SIZE BEHAVIOR (consistente con editor de nodos)
        // ============================================================

        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxWidth(Region.USE_PREF_SIZE);
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
                 "background",
                 "character" -> true;

            default -> false;
        };
    }

    @Override
    protected int normalizeInsertionIndex(
            BaseBlockView block,
            int index,
            VBox target
    ) {

        BaseBlockModel insertedModel = block.getModel();

        if (insertedModel == null) {
            return index;
        }

        int size = target.getChildren().size();

        if ("decision".equals(insertedModel.getType())) {
            return size;
        }

        if (size == 0) {
            return index;
        }

        Node lastNode = target.getChildren().get(size - 1);

        if (lastNode instanceof BaseBlockView lastBlock) {

            BaseBlockModel lastModel = lastBlock.getModel();

            if (lastModel != null && "decision".equals(lastModel.getType())) {
                return Math.min(index, size - 1);
            }
        }

        return index;
    }
}
