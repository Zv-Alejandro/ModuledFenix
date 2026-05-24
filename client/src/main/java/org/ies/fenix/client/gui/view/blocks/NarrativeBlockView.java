package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.ies.fenix.client.gui.model.script.NarrativeBlockModel;

public class NarrativeBlockView extends BaseBlockView {

    private TextArea textArea;
    private NarrativeBlockModel model;

    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public NarrativeBlockView(NarrativeBlockModel model) {
        this.model = model;

        getStyleClass().add("block-editor");

        Label title = new Label("TEXT");
        title.getStyleClass().add("block-label");

        textArea = new TextArea();
        textArea.setPromptText("Describe the scene...");
        textArea.setPrefHeight(80);
        textArea.getStyleClass().add("block-textarea");

        textArea.setText(model.getNarration());

        textArea.textProperty().addListener((obs, oldValue, newValue) -> {
            model.setNarration(newValue);
        });

        getChildren().addAll(title, textArea);
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



    // ============================================================
    //  Cargar datos existentes (si se reabre un script)
    // ============================================================
    public void loadFromModel() {
        if (model != null) {
            textArea.setText(model.getNarration());
        }
    }
}
