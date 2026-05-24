package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.DecisionBlockModel;
import org.ies.fenix.client.gui.view.blocks.DecisionBlockView;

public class ParentComponentController {
    @FXML
    private HBox header;

    @FXML
    private VBox contentBox;

    public void initialize() {

        DecisionBlockModel model = new DecisionBlockModel();

        DecisionBlockView topBlock = new DecisionBlockView(model);

        header.getChildren().add(topBlock);

        // DEMO
        contentBox.getChildren().add(
                new DecisionBlockView(new DecisionBlockModel())
        );

        contentBox.getChildren().add(
                new DecisionBlockView(new DecisionBlockModel())
        );
    }

    public VBox getContentBox() {
        return contentBox;
    }
}
