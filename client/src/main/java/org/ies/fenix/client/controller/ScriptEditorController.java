package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.util.BlockFactory;

public class ScriptEditorController {

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private VBox workspace;

    // ============================================================
    // BLOCK ACTIONS
    // ============================================================

    @FXML
    public void addNarrativeBlock() {
        addBlock(BlockFactory.createNarrativeBlock());
    }

    @FXML
    public void addShowBlock() {
        addBlock(BlockFactory.createShowBlock());
    }

    @FXML
    public void addDecisionBlock() {
        addBlock(BlockFactory.createDecisionBlock());
    }

    @FXML
    public void addBackgroundBlock() {
        addBlock(BlockFactory.createBackgroundBlock());
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private void addBlock(Node block) {
        workspace.getChildren().add(block);
    }

    // ============================================================
    // FUTURE BLOCKS
    // ============================================================

    /*
    @FXML
    public void addMusicBlock() {
        addBlock(BlockFactory.createMusicBlock());
    }
    */
}