package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.DecisionBlockModel;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.utils.ExpandableTextArea;

import java.util.ArrayList;
import java.util.List;

public class DecisionBlockView extends ContainerBlockView {

    private TextArea textArea;
    private final List<BaseBlockModel> options = new ArrayList<>();

    // ============================================================
    //  MODO EDITOR
    // ============================================================
    public DecisionBlockView(
            DecisionBlockModel model,
            DragAndDropService dragService
    ) {

        super.model = model;

        getStyleClass().add("block-editor");

        // ============================================================
        // HEADER
        // ============================================================

        Label label = new Label("DECISION");
        label.getStyleClass().add("block-label");
        label.setMinWidth(Region.USE_PREF_SIZE);

        textArea = new ExpandableTextArea();
        textArea.setPromptText("Decision question...");
        textArea.setText(model.getSentence());
        textArea.getStyleClass().add("block-textarea");

        textArea.textProperty().addListener((obs, oldV, newV) -> {
            model.setSentence(newV);
        });

        HBox row = new HBox(label, textArea);
        row.getStyleClass().add("block-row");

        // IMPORTANTE: layout estable
        row.setMaxWidth(Region.USE_PREF_SIZE);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(textArea, Priority.NEVER);

        // ============================================================
        // CHILDREN
        // ============================================================

        childrenContainer = new VBox(5);
        childrenContainer.setPadding(new Insets(0, 0, 0, 20));
        childrenContainer.getStyleClass().add("block-children-container");

        // CRÍTICO: el container NO debe bloquear eventos de drag
        childrenContainer.setPickOnBounds(false);

        for (OptionBlockModel option : model.getOptions()) {

            BaseBlockView optionView =
                    BlockFactory.createView(
                            option,
                            option.getType(),
                            dragService
                    );

            childrenContainer.getChildren().add(optionView);
        }

        // ============================================================
        // CONTENT
        // ============================================================

        VBox content = new VBox(5, row, childrenContainer);
        content.getStyleClass().add("block-content");

        getChildren().add(content);

        // ============================================================
        // DRAG & DROP (IMPORTANTE)
        // ============================================================

        setupContainerDragAndDrop(dragService);

        // ============================================================
        // BLOCK SIZE (consistente con editor de nodos)
        // ============================================================

        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxWidth(Region.USE_PREF_SIZE);
    }

    @Override
    public BaseBlockModel createModel() {
        return new DecisionBlockModel();
    }

    @Override
    public boolean canContain(BaseBlockModel child) {
        return child.getType().equals("option");
    }

    // ============================================================
    //  MODO CATÁLOGO (bloque horizontal simple)
    // ============================================================
    public DecisionBlockView() {

        getStyleClass().add("block-catalog");

        Label label = new Label("DECISION");
        label.getStyleClass().add("block-label");

        TextField preview = new TextField();
        preview.setDisable(true);
        preview.getStyleClass().add("block-textfield");

        // BLOQUE HORIZONTAL
        getChildren().addAll(label, preview);
    }

}
