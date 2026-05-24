package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.DecisionBlockModel;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;

import java.util.ArrayList;
import java.util.List;

public class DecisionBlockView extends ContainerBlockView {

    private TextField sentenceField;
    private final List<BaseBlockModel> options = new ArrayList<>();

    // ============================================================
    //  MODO EDITOR (bloque horizontal simple)
    // ============================================================
    public DecisionBlockView(
            DecisionBlockModel model,
            DragAndDropService dragService
    ) {

        super.model = model;
        getStyleClass().add("block-editor");

        Label label = new Label("DECISION");

        label.getStyleClass().add("block-label");

        sentenceField = new TextField();

        sentenceField.setPromptText(
                "Decision question..."
        );

        sentenceField.setText(model.getSentence());

        sentenceField.getStyleClass()
                .add("block-textfield");

        sentenceField.textProperty().addListener(
                (obs, oldV, newV) -> {

                    model.setSentence(newV);
                }
        );

        HBox row = new HBox(
                10,
                label,
                sentenceField
        );

        row.getStyleClass().add("block-row");

        childrenContainer = new VBox(5);

        childrenContainer.setPadding(
                new Insets(0,0,0,10)
        );

        for (OptionBlockModel option
                : model.getOptions()) {

            BaseBlockView optionView =
                    BlockFactory.createView(
                            option,
                            option.getType(),
                            dragService
                    );

            childrenContainer
                    .getChildren()
                    .add(optionView);
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
    @Override
    public BaseBlockModel createModel() {
        return new DecisionBlockModel();
    }

    @Override
    public boolean canContain(BaseBlockModel child) {
        return child.getType().equals("option");
    }

}
