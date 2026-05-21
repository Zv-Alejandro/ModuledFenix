package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.ies.fenix.client.gui.util.EditorRegistry;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;

public class OptionBlockView extends BaseBlockView {

    private TextArea optionSentenceArea;
    private ComboBox<SceneBlockModel> jumpSceneCombo;
    private OptionBlockModel model;

    // ============================================================
    //  MODO EDITOR
    // ============================================================
    public OptionBlockView(OptionBlockModel model) {
        this.model = model;

        getStyleClass().add("block-editor");

        Label instruction1 = new Label("OPTION");
        instruction1.getStyleClass().add("block-label");

        optionSentenceArea = new TextArea();
        optionSentenceArea.setPrefWidth(250);
        optionSentenceArea.setPrefHeight(60);
        optionSentenceArea.getStyleClass().add("block-textarea");

        Label instruction2 = new Label("JUMP SCENE");
        instruction2.getStyleClass().add("block-label");

        jumpSceneCombo = new ComboBox<>();
        jumpSceneCombo.setPrefWidth(250);
        jumpSceneCombo.setItems(EditorRegistry.getScenes());
        jumpSceneCombo.getStyleClass().add("block-combo");

        // Cargar valores iniciales
        optionSentenceArea.setText(model.getOptionSentence());
        jumpSceneCombo.setValue(model.getSceneBlockModel());

        // Listeners
        optionSentenceArea.textProperty().addListener((obs, oldV, newV) -> {
            model.setOptionSentence(newV);
        });

        jumpSceneCombo.valueProperty().addListener((obs, oldV, newV) -> {
            model.setSceneBlockModel(newV);
        });

        HBox row1 = new HBox(10, instruction1, optionSentenceArea);
        row1.getStyleClass().add("block-row");

        HBox row2 = new HBox(10, instruction2, jumpSceneCombo);
        row2.getStyleClass().add("block-row");

        getChildren().addAll(row1, row2);
    }

    // ============================================================
    //  MODO CATÁLOGO
    // ============================================================
    public OptionBlockView() {

        getStyleClass().add("block-catalog");

        Label optionLabel = new Label("OPTION");
        optionLabel.getStyleClass().add("block-label");

        TextArea previewText = new TextArea("...");
        previewText.setDisable(true);
        previewText.setPrefHeight(60);
        previewText.getStyleClass().add("block-textarea");

        Label jumpLabel = new Label("JUMP SCENE");
        jumpLabel.getStyleClass().add("block-label");

        ComboBox<String> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);
        previewCombo.getItems().add("...");
        previewCombo.setValue("...");
        previewCombo.getStyleClass().add("block-combo");

        HBox row1 = new HBox(10, optionLabel, previewText);
        row1.getStyleClass().add("block-row");

        HBox row2 = new HBox(10, jumpLabel, previewCombo);
        row2.getStyleClass().add("block-row");

        getChildren().addAll(row1, row2);
    }
}
