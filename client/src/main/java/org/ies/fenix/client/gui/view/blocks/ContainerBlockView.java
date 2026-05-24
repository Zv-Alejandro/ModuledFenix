package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.*;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.gui.util.DragContext;

public abstract class ContainerBlockView extends BaseBlockView {

    protected VBox childrenContainer;
    protected DragAndDropService dragService;

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

        // =========================
        // NEW FROM PALETTE
        // =========================
        if (draggedView.isPaletteBlock()) {

            BaseBlockModel model = draggedView.createModel();

            blockToInsert =
                    BlockFactory.createView(
                            model,
                            model.getType(),
                            dragService
                    );

        } else {
            // =========================
            // REORDER EXISTING
            // =========================
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

    // ============================================================
    // INSERT LOGIC (sin cambios grandes, solo más estable)
    // ============================================================

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

        // Reordenar dentro del mismo contenedor
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

        if (index < 0) index = 0;
        if (index > target.getChildren().size()) {
            index = target.getChildren().size();
        }

        index = normalizeInsertionIndex(block, index, target);

        if (index < 0) {
            index = 0;
        }

        if (index > target.getChildren().size()) {
            index = target.getChildren().size();
        }

        target.getChildren().add(index, block);

        // =========================
        // MODEL SYNC
        // =========================

        BaseBlockModel parent = getModel();

    if (parent == null) {
        return;
    }

    if (parent instanceof SceneBlockModel scene) {

        scene.getChildren().remove(block.getModel());
        scene.getChildren().add(index, block.getModel());
        block.getModel().setParent(scene);
    }

    else if (parent instanceof DecisionBlockModel decision) {

        decision.getOptions().remove(block.getModel());
        decision.getOptions().add(index, (OptionBlockModel) block.getModel());
        block.getModel().setParent(decision);
    }
}

    private int calculateInsertionIndex(double mouseY) {

        for (int i = 0; i < childrenContainer.getChildren().size(); i++) {

            Node child = childrenContainer.getChildren().get(i);

            double centerY =
                    child.getBoundsInParent().getMinY()
                            + child.getBoundsInParent().getHeight() / 2;

            if (mouseY < centerY) return i;
        }

        return childrenContainer.getChildren().size();
    }
}