package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.DecisionBlockModel;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;

import java.util.List;

public class DecisionBlockView extends BaseBlockView {

    private DecisionBlockModel model;
    private TextArea sentenceArea;
    private VBox optionsContainer;
    private List<SceneBlockModel> scenes;

    public DecisionBlockView(DecisionBlockModel model, List<SceneBlockModel> scenes) {
        this.model = model;
        this.scenes = scenes;

        Label decisionLabel = new Label("DECISION");

        sentenceArea = new TextArea();
        sentenceArea.setPromptText("What is the decision question?");
        sentenceArea.setPrefHeight(60);
        sentenceArea.setText(model.getSentence());

        sentenceArea.textProperty().addListener((obs, oldV, newV) -> {
            model.setSentence(newV);
        });

        optionsContainer = new VBox(10);

        for (OptionBlockModel opt : model.getOptions()) {
            optionsContainer.getChildren().add(new OptionBlockView(opt, scenes));
        }

        Button addOptionBtn = new Button("+ Add Option");
        addOptionBtn.setOnAction(e -> addNewOption());

        setSpacing(10);
        getChildren().addAll(decisionLabel, sentenceArea, optionsContainer, addOptionBtn);
    }

    public DecisionBlockView() {

        Label title = new Label("DECISION");

        TextArea previewSentence = new TextArea("Decision text...");
        previewSentence.setDisable(true);
        previewSentence.setPrefHeight(50);

        VBox previewOptions = new VBox(5);
        previewOptions.getChildren().add(new OptionBlockView());
        previewOptions.getChildren().add(new OptionBlockView());

        getChildren().addAll(title, previewSentence, previewOptions);

        setStyle("-fx-background-color: #f7d75c; -fx-padding: 10; -fx-border-color: black;");
    }


    private void addNewOption() {
        OptionBlockModel newOption = new OptionBlockModel();
        model.getOptions().add(newOption);
        optionsContainer.getChildren().add(new OptionBlockView(newOption, scenes));
    }
}
