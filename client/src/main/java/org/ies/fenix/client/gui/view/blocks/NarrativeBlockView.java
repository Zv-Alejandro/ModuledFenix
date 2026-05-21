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

        Label title = new Label("TEXT");

        textArea = new TextArea();
        textArea.setPromptText("Describe the scene...");
        textArea.setPrefHeight(80);

        // Cargar valor inicial
        textArea.setText(model.getNarration());

        // Listener: actualiza el modelo cuando el usuario escribe
        textArea.textProperty().addListener((obs, oldValue, newValue) -> {
            model.setNarration(newValue);
        });

        getChildren().addAll(title, textArea);

        // Estilo opcional para distinguirlo del catálogo
        setStyle("-fx-background-color: #fff7c2; -fx-padding: 10; -fx-border-color: black;");
    }

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public NarrativeBlockView() {
        Label title = new Label("TEXT");

        TextArea preview = new TextArea("Scene description...");
        preview.setDisable(true);
        preview.setPrefHeight(80);

        getChildren().addAll(title, preview);

        // Estilo tipo tarjeta para toolbox
        setStyle("-fx-background-color: #f7d75c; -fx-padding: 10; -fx-border-color: black;");
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
