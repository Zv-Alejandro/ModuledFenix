package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.ies.fenix.client.gui.model.script.DialogBlockModel;
import org.ies.fenix.client.gui.model.script.FenixCharacterModel;

import java.util.List;

public class DialogBlockView extends BaseBlockView {

    private ComboBox<FenixCharacterModel> characterCombo;
    private TextArea textArea;
    private DialogBlockModel model;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public DialogBlockView() {

        Label instruction1 = new Label("SHOW");

        ComboBox<FenixCharacterModel> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);

        TextArea previewText = new TextArea("Character dialog...");
        previewText.setDisable(true);
        previewText.setPrefHeight(60);

        getChildren().addAll(instruction1, previewCombo, previewText);

        // Estilo opcional para que parezca un bloque
        setStyle("-fx-background-color: #f7d75c; -fx-padding: 10; -fx-border-color: black;");
    }

    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public DialogBlockView(List<FenixCharacterModel> characterList, DialogBlockModel model) {
        this.model = model;

        Label instruction1 = new Label("SHOW");

        characterCombo = new ComboBox<>();
        characterCombo.getItems().addAll(characterList);

        // Cargar valor inicial
        characterCombo.setValue(model.getCharacter());

        // Listener → actualiza modelo
        characterCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            model.setCharacter(newValue);
        });

        textArea = new TextArea();
        textArea.setPromptText("What does the character say?");
        textArea.setPrefHeight(60);

        // Cargar valor inicial
        textArea.setText(model.getDialog());

        // Listener → actualiza modelo
        textArea.textProperty().addListener((obs, oldValue, newValue) -> {
            model.setDialog(newValue);
        });

        getChildren().addAll(instruction1, characterCombo, textArea);

        setStyle("-fx-background-color: #fff7c2; -fx-padding: 10; -fx-border-color: black;");
    }

    // ============================================================
    //  MÉTODOS AUXILIARES
    // ============================================================

    public void updateCharacters(List<FenixCharacterModel> characterList) {
        if (characterCombo != null) {
            characterCombo.getItems().setAll(characterList);
        }
    }

    public DialogBlockModel getModel() {
        return model;
    }
}
