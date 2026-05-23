package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.ies.fenix.client.gui.util.EditorRegistry;
import org.ies.fenix.client.gui.model.script.DialogBlockModel;
import org.ies.fenix.client.gui.model.script.FenixCharacterModel;

public class DialogBlockView extends BaseBlockView {

    private ComboBox<FenixCharacterModel> characterCombo;
    private TextArea textArea;
    private DialogBlockModel model;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public DialogBlockView() {

        getStyleClass().add("block-catalog");

        Label instruction1 = new Label("SHOW");
        instruction1.getStyleClass().add("block-label");

        ComboBox<FenixCharacterModel> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);
        previewCombo.getStyleClass().add("block-combo");

        TextArea previewText = new TextArea("Character dialog...");
        previewText.setDisable(true);
        previewText.setPrefHeight(60);
        previewText.getStyleClass().add("block-textarea");

        HBox row1 = new HBox(10, instruction1, previewCombo);
        row1.getStyleClass().add("block-row");

        getChildren().addAll(row1, previewText);
    }

    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public DialogBlockView(DialogBlockModel model) {
        this.model = model;

        getStyleClass().add("block-editor");

        Label instruction1 = new Label("SHOW");
        instruction1.getStyleClass().add("block-label");

        characterCombo = new ComboBox<>();
        characterCombo.setItems(EditorRegistry.getCharacters());
        characterCombo.setValue(model.getCharacter());
        characterCombo.getStyleClass().add("block-combo");

        characterCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            model.setCharacter(newValue);
        });

        textArea = new TextArea();
        textArea.setPromptText("What does the character say?");
        textArea.setPrefHeight(60);
        textArea.setText(model.getDialog());
        textArea.getStyleClass().add("block-textarea");

        textArea.textProperty().addListener((obs, oldValue, newValue) -> {
            model.setDialog(newValue);
        });

        HBox row1 = new HBox(10, instruction1, characterCombo);
        row1.getStyleClass().add("block-row");

        getChildren().addAll(row1, textArea);
    }

    // ============================================================
    //  MÉTODOS AUXILIARES
    // ============================================================
    public DialogBlockModel getModel() {
        return model;
    }
}
