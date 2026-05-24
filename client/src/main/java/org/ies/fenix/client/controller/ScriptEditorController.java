package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;
import org.ies.fenix.client.gui.view.blocks.WorkspaceBlockView;

import java.net.URL;
import java.util.ResourceBundle;

public class ScriptEditorController implements Initializable {

    private final DragAndDropService dragService =
            new DragAndDropService();

    @FXML
    private VBox workspace;

    @FXML
    private VBox palette;

    private WorkspaceBlockView workspaceBlockView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupWorkspace();

        addPaletteBlock(BlockFactory.createNarrativeBlock());
        addPaletteBlock(BlockFactory.createShowBlock());
        addPaletteBlock(BlockFactory.createDecisionBlock());
        addPaletteBlock(BlockFactory.createOptionBlock());
        addPaletteBlock(BlockFactory.createSceneBlock());
        addPaletteBlock(BlockFactory.createBackgroundBlock());
        addPaletteBlock(BlockFactory.createCharacterCreateBlock());
        addPaletteBlock(BlockFactory.createCharacterBlock());
    }

    private void setupWorkspace() {

        workspaceBlockView = new WorkspaceBlockView(dragService);

        workspaceBlockView.setMaxWidth(Double.MAX_VALUE);
        workspaceBlockView.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(workspaceBlockView, Priority.ALWAYS);

        workspace.getChildren().clear();
        workspace.getChildren().add(workspaceBlockView);
    }

    private void addPaletteBlock(BaseBlockView block) {

        block.setPaletteBlock(true);

        block.setOnDragDetected(event -> {
            dragService.startDrag(block, event);
            event.consume();
        });

        palette.getChildren().add(block);
    }
}