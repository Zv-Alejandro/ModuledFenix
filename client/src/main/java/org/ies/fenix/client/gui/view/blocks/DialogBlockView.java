package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.util.EditorRegistry;
import org.ies.fenix.client.gui.model.script.DialogBlockModel;
import org.ies.fenix.client.gui.model.script.FenixCharacterModel;
import org.ies.fenix.client.utils.ExpandableTextArea;

public class DialogBlockView extends BaseBlockView {

    private ComboBox<FenixCharacterModel> characterCombo;
    private TextArea textArea;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public DialogBlockView() {

        // Modo catálogo (amarillo)
        getStyleClass().add("block-catalog");

        Label instruction1 = new Label("SHOW");
        instruction1.getStyleClass().add("block-label");

        ComboBox<FenixCharacterModel> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);
        previewCombo.setStyle("    -fx-border-color: black;\n" +
                "    -fx-border-width: 1;");

        // Cambiamos TextArea → TextField
        TextField previewText = new TextField("");
        previewText.setDisable(true);
        previewText.getStyleClass().add("block-textfield");

        // El label no crece
        HBox.setHgrow(instruction1, Priority.NEVER);

        // El combo ocupa el espacio sobrante
        previewCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(previewCombo, Priority.ALWAYS);

        // El textfield también ocupa el espacio sobrante
        previewText.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(previewText, Priority.ALWAYS);

        HBox row1 = new HBox(10, instruction1, previewCombo);
        row1.getStyleClass().add("block-row");

        getChildren().addAll(row1, previewText);
    }

    @Override
    public BaseBlockModel createModel() {
        return new DialogBlockModel();
    }


    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public DialogBlockView(DialogBlockModel model) {

        super.model = model;

        getStyleClass().add("block-editor");

        // ===== SHOW CHARACTER =====

        Label instruction1 = new Label("SHOW");
        instruction1.getStyleClass().add("block-label");
        instruction1.setMinWidth(Region.USE_PREF_SIZE);

        characterCombo = new ComboBox<>();
        characterCombo.setItems(EditorRegistry.getCharacters());
        characterCombo.setValue(model.getCharacter());
        characterCombo.getStyleClass().add("block-combo");

        characterCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            model.setCharacter(newValue);
        });

        // ===== DIALOG TEXT =====

        textArea = new ExpandableTextArea();
        textArea.setPromptText("What does the character say?");
        textArea.setPrefHeight(60);
        textArea.setText(model.getDialog());
        textArea.getStyleClass().add("block-textarea");

        textArea.textProperty().addListener((obs, oldValue, newValue) -> {
            model.setDialog(newValue);
        });

        // ===== LAYOUT =====

        HBox row1 = new HBox(10, instruction1, characterCombo);
        row1.getStyleClass().add("block-row");

        getChildren().addAll(row1, textArea);

        // ===== IMPORTANT =====

        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxWidth(Region.USE_PREF_SIZE);
    }
}

