package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.NarrativeBlockModel;
import org.ies.fenix.client.utils.ExpandableTextArea;

public class NarrativeBlockView extends BaseBlockView {

    private TextArea textArea;
    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public NarrativeBlockView(NarrativeBlockModel model) {

        super.model = model;

        getStyleClass().add("block-editor");

        Label title = new Label("TEXT");
        title.getStyleClass().add("block-label");
        title.setMinWidth(Region.USE_PREF_SIZE);

        textArea = new ExpandableTextArea();
        textArea.setPromptText("Describe the scene...");
        textArea.setPrefHeight(80);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.getStyleClass().add("block-textarea");

        HBox.setHgrow(title, Priority.NEVER);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        textArea.setText(model.getNarration());

        textArea.textProperty().addListener((obs, oldValue, newValue) -> {
            model.setNarration(newValue);
        });

        HBox row = new HBox(10, title, textArea);
        row.getStyleClass().add("block-row");
        row.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(row, Priority.ALWAYS);

        getChildren().add(row);
    }

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public NarrativeBlockView() {

        Label title = new Label("TEXT");
        title.getStyleClass().add("block-label");

        TextField preview = new TextField();
        preview.setDisable(true);
        preview.getStyleClass().add("block-textfield");

        // El label mantiene su tamaño natural
        HBox.setHgrow(title, Priority.NEVER);

        // El campo ocupa el espacio sobrante
        preview.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(preview, Priority.ALWAYS);

        HBox row = new HBox(10, title, preview);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }

    @Override
    public BaseBlockModel createModel() {
        return new NarrativeBlockModel();
    }
}
