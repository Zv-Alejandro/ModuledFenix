package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;

import java.util.List;

public class OptionBlockView extends BaseBlockView {

    private TextArea optionSentenceArea;
    private ComboBox<SceneBlockModel> jumpSceneCombo;
    private OptionBlockModel model;

    public OptionBlockView(OptionBlockModel model, List<SceneBlockModel> scenes) {
        this.model = model;

        Label instruction1 = new Label("OPTION");
        optionSentenceArea = new TextArea();
        optionSentenceArea.setPrefWidth(250);
        optionSentenceArea.setPrefHeight(60);

        Label instruction2 = new Label("JUMP SCENE");
        jumpSceneCombo = new ComboBox<>();
        jumpSceneCombo.setPrefWidth(250);

        // Cargar lista de escenas
        jumpSceneCombo.getItems().addAll(scenes);

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
        HBox row2 = new HBox(10, instruction2, jumpSceneCombo);

        setSpacing(10);
        getChildren().addAll(row1, row2);
    }

    // Modo catálogo
    public OptionBlockView() {
        Label optionLabel = new Label("OPTION");
        TextArea previewText = new TextArea("...");
        previewText.setDisable(true);

        Label jumpLabel = new Label("JUMP SCENE");
        ComboBox<String> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);
        previewCombo.getItems().add("...");
        previewCombo.setValue("...");

        HBox row1 = new HBox(10, optionLabel, previewText);
        HBox row2 = new HBox(10, jumpLabel, previewCombo);

        getChildren().addAll(row1, row2);
    }
}
