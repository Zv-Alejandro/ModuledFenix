package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.DecisionBlockModel;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;

public class DecisionBlockView extends ParentBlockView {

    private DecisionBlockModel model;
    private TextArea sentenceArea;
    private VBox optionsContainer;

    public DecisionBlockView(DecisionBlockModel model) {
        this.model = model;

        getStyleClass().add("block-editor");

        Label decisionLabel = new Label("DECISION");
        decisionLabel.getStyleClass().add("block-label");

        sentenceArea = new TextArea();
        sentenceArea.setPromptText("What is the decision question?");
        sentenceArea.setPrefHeight(60);
        sentenceArea.setText(model.getSentence());
        sentenceArea.getStyleClass().add("block-textarea");

        sentenceArea.textProperty().addListener((obs, oldV, newV) -> {
            model.setSentence(newV);
        });

        optionsContainer = new VBox(10);
        for (OptionBlockModel opt : model.getOptions()) {
            optionsContainer.getChildren().add(new OptionBlockView(opt));
        }

        Button addOptionBtn = new Button("+ Add Option");
        addOptionBtn.getStyleClass().add("block-button");
        addOptionBtn.setOnAction(e -> addNewOption());

        // Añadir todo al contentWrapper vertical
        contentWrapper.getChildren().addAll(
                decisionLabel,
                sentenceArea,
                optionsContainer,
                addOptionBtn
        );
    }


    // ============================================================
    //  MODO CATÁLOGO
    // ============================================================
    public DecisionBlockView() {

        getStyleClass().add("block-catalog");

        Label title = new Label("DECISION");
        title.getStyleClass().add("block-label");

        TextArea previewSentence = new TextArea("Decision text...");
        previewSentence.setDisable(true);
        previewSentence.setPrefHeight(50);
        previewSentence.getStyleClass().add("block-textarea");

        VBox previewOptions = new VBox(5);
        previewOptions.getChildren().add(new OptionBlockView());
        previewOptions.getChildren().add(new OptionBlockView());

        getChildren().addAll(title, previewSentence, previewOptions);
    }

    // ============================================================
    //  Añadir nueva opción
    // ============================================================
    private void addNewOption() {
        OptionBlockModel newOption = new OptionBlockModel();
        model.getOptions().add(newOption);
        optionsContainer.getChildren().add(new OptionBlockView(newOption));
    }
}
