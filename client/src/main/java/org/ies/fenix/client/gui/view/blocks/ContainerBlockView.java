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

        childrenContainer.setOnDragOver(event -> {
            dragService.handleDragOver(event);
            event.acceptTransferModes(TransferMode.MOVE);
        });

        childrenContainer.setOnDragDropped(event -> {

            DragContext context = dragService.getContext();
            BaseBlockView draggedView = context.getDraggedView();

            if (draggedView == null) {
                event.setDropCompleted(false);
                return;
            }

            BaseBlockView blockToInsert;

            // =========================
            // NEW FROM PALETTE
            // =========================
            if (draggedView.isPaletteBlock()) {

                BaseBlockModel model = draggedView.createModel();

            BaseBlockView view = switch (model.getType()) {

                case "text" -> (model == null)
                        ? new NarrativeBlockView()
                        : new NarrativeBlockView((NarrativeBlockModel) model);

                case "dialog" -> (model == null)
                        ? new DialogBlockView()
                        : new DialogBlockView((DialogBlockModel) model);

                case "background" -> (model == null)
                        ? new BackgroundBlockView()
                        : new BackgroundBlockView((BackgroundBlockModel) model);

                case "character_create" -> (model == null)
                        ? new CharacterCreateBlockView()
                        : new CharacterCreateBlockView((CharacterCreateBlockModel) model);

                case "decision" -> (model == null)
                        ? new DecisionBlockView()
                        : new DecisionBlockView((DecisionBlockModel) model, dragService);

                case "option" -> (model == null)
                        ? new OptionBlockView()
                        : new OptionBlockView((OptionBlockModel) model);

                case "scene" -> (model == null)
                        ? new SceneBlockView()
                        : new SceneBlockView((SceneBlockModel) model, dragService);

                case "character" -> (model == null)
                        ? new CharacterBlockView()
                        : new CharacterBlockView((CharacterBlockModel) model);

                default -> throw new IllegalArgumentException("Unknown type: " + model.getType());
            };

                blockToInsert = view;

                blockToInsert.setOnDragDetected(e ->
                        dragService.startDrag(blockToInsert, e)
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

    private void insertBlockAt(DragEvent event, BaseBlockView block) {

        VBox target = childrenContainer;
        VBox source = block.getParent() instanceof VBox vBox
                ? vBox
                : null;

        int index = calculateInsertionIndex(event.getY());

        // 🔥 FIX reorder same container
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

        if (index < 0) {
            index = 0;
        }

        if (index > target.getChildren().size()) {
            index = target.getChildren().size();
        }

        target.getChildren().add(index, block);

        // =========================
        // MODEL SYNC CLEAN
        // =========================
        BaseBlockModel parent = getModel();

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