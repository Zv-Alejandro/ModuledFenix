package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

import java.net.URL;
import java.util.ResourceBundle;

public class ScriptEditorController implements Initializable {

    private final DragAndDropService dragService =
            new DragAndDropService();

    @FXML
    private VBox workspace;

    @FXML
    private VBox palette;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addPaletteBlock(BlockFactory.createNarrativeBlock());
        addPaletteBlock(BlockFactory.createShowBlock());
        addPaletteBlock(BlockFactory.createDecisionBlock());
        addPaletteBlock(BlockFactory.createOptionBlock());
        addPaletteBlock(BlockFactory.createSceneBlock());
        addPaletteBlock(BlockFactory.createBackgroundBlock());
        addPaletteBlock(BlockFactory.createCharacterCreateBlock());
        addPaletteBlock(BlockFactory.createCharacterBlock());

        setupWorkspaceDrop();
    }

    private void addPaletteBlock(BaseBlockView block) {

        block.setPaletteBlock(true);

        block.setOnDragDetected(event -> {
            dragService.startDrag(block, event);
            event.consume();
        });

        palette.getChildren().add(block);
    }

    private void setupWorkspaceDrop() {

        workspace.setPickOnBounds(true);

        workspace.setOnDragOver(dragService::handleDragOver);

        workspace.setOnDragDropped(event -> {

            BaseBlockView dragged =
                    dragService.getContext().getDraggedView();

            if (dragged == null) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            BaseBlockView blockToInsert;

            if (dragged.isPaletteBlock()) {

                BaseBlockModel model = dragged.createModel();

                blockToInsert =
                        BlockFactory.createView(
                                model,
                                model.getType(),
                                dragService
                        );

            } else {

                blockToInsert = dragged;
            }

            insertBlockInWorkspace(event, blockToInsert);

            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void insertBlockInWorkspace(
            DragEvent event,
            BaseBlockView block
    ) {

        VBox source = block.getParent() instanceof VBox vBox
                ? vBox
                : null;

        int index = calculateWorkspaceInsertionIndex(event.getY());

        if (source == workspace) {
            int oldIndex = workspace.getChildren().indexOf(block);

            if (oldIndex == index || oldIndex + 1 == index) {
                return;
            }

            if (oldIndex < index) {
                index--;
            }
        }

        if (source != null) {
            source.getChildren().remove(block);
        }

        if (index < 0) {
            index = 0;
        }

        if (index > workspace.getChildren().size()) {
            index = workspace.getChildren().size();
        }

        workspace.getChildren().add(index, block);
    }

    private int calculateWorkspaceInsertionIndex(double mouseY) {

        for (int i = 0; i < workspace.getChildren().size(); i++) {

            Node child = workspace.getChildren().get(i);

            double centerY =
                    child.getBoundsInParent().getMinY()
                            + child.getBoundsInParent().getHeight() / 2;

            if (mouseY < centerY) {
                return i;
            }
        }

        return workspace.getChildren().size();
    }
}