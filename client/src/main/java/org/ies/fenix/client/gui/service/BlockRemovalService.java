package org.ies.fenix.client.gui.service;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.*;
import org.ies.fenix.client.gui.util.EditorRegistry;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

public class BlockRemovalService {

    private static final String REMOVAL_HANDLER_INSTALLED =
            "fenix-removal-handler-installed";

    public void makeRemovable(BaseBlockView block) {

        if (Boolean.TRUE.equals(block.getProperties().get(REMOVAL_HANDLER_INSTALLED))) {
            return;
        }

        block.getProperties().put(REMOVAL_HANDLER_INSTALLED, true);

        block.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            if (event.getButton() != MouseButton.SECONDARY) {
                return;
            }

            BaseBlockView clickedBlock = findClickedBlock(event.getTarget());

            if (clickedBlock != block) {
                return;
            }

            remove(block);
            event.consume();
        });
    }

    public void remove(BaseBlockView block) {

        if (block.isPaletteBlock()) {
            return;
        }

        if (!(block.getParent() instanceof VBox parentContainer)) {
            return;
        }

        parentContainer.getChildren().remove(block);
        removeFromModel(block);
    }

    private BaseBlockView findClickedBlock(EventTarget target) {

        if (!(target instanceof Node node)) {
            return null;
        }

        while (node != null) {

            if (node instanceof BaseBlockView blockView) {
                return blockView;
            }

            node = node.getParent();
        }

        return null;
    }

    private void removeFromModel(BaseBlockView block) {

        BaseBlockModel blockModel = block.getModel();

        if (blockModel == null) {
            return;
        }

        unregisterFromEditorRegistry(blockModel);

        BaseBlockModel parentModel = blockModel.getParent();

        if (parentModel instanceof SceneBlockModel scene) {
            scene.removeChild(blockModel);
            blockModel.setParent(null);
            return;
        }

        if (parentModel instanceof DecisionBlockModel decision
                && blockModel instanceof OptionBlockModel option) {

            decision.getOptions().remove(option);
            blockModel.setParent(null);
        }
    }

    private void unregisterFromEditorRegistry(BaseBlockModel blockModel) {

        if (blockModel instanceof SceneBlockModel scene) {
            EditorRegistry.removeScene(scene);
            return;
        }

        if (blockModel instanceof CharacterCreateBlockModel characterCreate) {
            EditorRegistry.removeCharacter(characterCreate);
        }
    }
}
