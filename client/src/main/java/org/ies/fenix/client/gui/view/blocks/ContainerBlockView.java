package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.*;
import org.ies.fenix.client.gui.service.BlockRemovalService;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.gui.util.DragContext;
import org.ies.fenix.client.gui.util.EditorRegistry;

public abstract class ContainerBlockView extends BaseBlockView {

    protected VBox childrenContainer;
    protected DragAndDropService dragService;

    private final BlockRemovalService blockRemovalService =
            new BlockRemovalService();

    public abstract boolean canContain(BaseBlockModel child);

    public void setupContainerDragAndDrop(DragAndDropService dragService) {

        this.dragService = dragService;

        childrenContainer.setPickOnBounds(true);

        childrenContainer.setOnDragOver(event -> {

            if (event.getDragboard().hasContent(DragAndDropService.BLOCK_FORMAT)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        childrenContainer.setOnDragDropped(event -> {

            DragContext context = dragService.getContext();
            BaseBlockView draggedView = context.getDraggedView();

            if (draggedView == null) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            BaseBlockView blockToInsert;

            if (draggedView.isPaletteBlock()) {

                BaseBlockModel model = draggedView.createModel();

                blockToInsert =
                        BlockFactory.createView(
                                model,
                                model.getType(),
                                dragService
                        );

            } else {
                blockToInsert = draggedView;
            }

            BaseBlockModel model = blockToInsert.getModel();

            if (model == null || !canContain(model)) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            insertBlockAt(event, blockToInsert);

            event.setDropCompleted(true);
            event.consume();
        });
    }

    protected int normalizeInsertionIndex(
            BaseBlockView block,
            int index,
            VBox target
    ) {
        return index;
    }

    private void insertBlockAt(DragEvent event, BaseBlockView block) {

        VBox target = childrenContainer;
        VBox source = block.getParent() instanceof VBox vBox
                ? vBox
                : null;

        int index = calculateInsertionIndex(event.getY());

        if (source == target) {
            int oldIndex = target.getChildren().indexOf(block);

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

        index = clampIndex(index, target);
        index = normalizeInsertionIndex(block, index, target);
        index = clampIndex(index, target);

        target.getChildren().add(index, block);
        blockRemovalService.makeRemovable(block);

        syncModelAfterInsert(block, index);
    }

    private void syncModelAfterInsert(BaseBlockView block, int index) {

        BaseBlockModel parent = getModel();
        BaseBlockModel blockModel = block.getModel();

        if (blockModel == null) {
            return;
        }

        registerInEditorRegistry(blockModel);

        if (parent == null) {
            return;
        }

        if (parent instanceof SceneBlockModel scene) {

            scene.getChildren().remove(blockModel);

            if (index > scene.getChildren().size()) {
                index = scene.getChildren().size();
            }

            scene.getChildren().add(index, blockModel);
            blockModel.setParent(scene);
            return;
        }

        if (parent instanceof DecisionBlockModel decision
                && blockModel instanceof OptionBlockModel option) {

            decision.getOptions().remove(option);

            if (index > decision.getOptions().size()) {
                index = decision.getOptions().size();
            }

            decision.getOptions().add(index, option);
            blockModel.setParent(decision);
        }
    }

    private void registerInEditorRegistry(BaseBlockModel blockModel) {

        if (blockModel instanceof SceneBlockModel scene) {
            EditorRegistry.addScene(scene);
            return;
        }

        if (blockModel instanceof CharacterCreateBlockModel characterCreate) {
            EditorRegistry.addCharacter(characterCreate);
        }
    }

    private int clampIndex(int index, VBox target) {

        if (index < 0) {
            return 0;
        }

        if (index > target.getChildren().size()) {
            return target.getChildren().size();
        }

        return index;
    }

    private int calculateInsertionIndex(double mouseY) {

        for (int i = 0; i < childrenContainer.getChildren().size(); i++) {

            Node child = childrenContainer.getChildren().get(i);

            double centerY =
                    child.getBoundsInParent().getMinY()
                            + child.getBoundsInParent().getHeight() / 2;

            if (mouseY < centerY) {
                return i;
            }
        }

        return childrenContainer.getChildren().size();
    }
}