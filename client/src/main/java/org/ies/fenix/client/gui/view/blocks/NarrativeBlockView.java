package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

        getStyleClass().add("block-catalog");

        Label title = new Label("TEXT");
        title.getStyleClass().add("block-label");

        TextArea preview = new TextArea("Scene description...");
        preview.setDisable(true);
        preview.setPrefHeight(80);
        preview.getStyleClass().add("block-textarea");

        getChildren().addAll(title, preview);
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
