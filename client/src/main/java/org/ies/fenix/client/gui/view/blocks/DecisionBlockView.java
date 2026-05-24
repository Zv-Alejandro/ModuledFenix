package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.DecisionBlockModel;

public class DecisionBlockView extends ContainerBlockView {

    private DecisionBlockModel model;
    private TextField sentenceField;

    // ============================================================
    //  MODO EDITOR (bloque horizontal simple)
    // ============================================================
    public DecisionBlockView(DecisionBlockModel model) {
        this.model = model;

        getStyleClass().add("block-editor");

        Label label = new Label("DECISION");
        label.getStyleClass().add("block-label");

        sentenceField = new TextField();
        sentenceField.setPromptText("Decision question...");
        sentenceField.setText(model.getSentence());
        sentenceField.getStyleClass().add("block-textfield");

        sentenceField.textProperty().addListener((obs, oldV, newV) -> {
            model.setSentence(newV);
        });

        // BLOQUE HORIZONTAL
        getChildren().addAll(label, sentenceField);

        childrenContainer = new VBox(5);

        childrenContainer.setPadding(
                new Insets(0,0,0,10)
        );
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
