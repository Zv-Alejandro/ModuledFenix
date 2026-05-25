package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.CharacterCreateBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;

public class WorkspaceBlockView extends ContainerBlockView {

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public WorkspaceBlockView(DragAndDropService dragService) {

        this.dragService = dragService;

        // Workspace no debe tener apariencia de bloque
        getStyleClass().remove("block");
        getStyleClass().add("workspace");

        // ============================================================
        // CHILDREN CONTAINER
        // ============================================================

        childrenContainer = new VBox(10);
        childrenContainer.setPadding(new Insets(20));
        childrenContainer.getStyleClass().add("workspace-container");

        childrenContainer.setPickOnBounds(true);
        childrenContainer.setMinSize(600, 400);
        childrenContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // IMPORTANTE: permitir drop en todo el workspace
        setupContainerDragAndDrop(dragService);

        getChildren().add(childrenContainer);

        // ============================================================
        // SIZE BEHAVIOR
        // ============================================================

        setMinSize(600, 400);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    // ============================================================
    // REGLAS DE NEGOCIO
    // ============================================================

    @Override
    public boolean canContain(BaseBlockModel child) {

        return child instanceof SceneBlockModel
                || child instanceof CharacterCreateBlockModel;
    }

    // ============================================================
    // API PÚBLICA (CORRECTA, NO EXPONER VBox DIRECTO)
    // ============================================================

    public void addBlock(BaseBlockView block, int index) {

        if (index < 0) {
            index = 0;
        }

        if (index > childrenContainer.getChildren().size()) {
            index = childrenContainer.getChildren().size();
        }

        block.setPaletteBlock(false);
        setupBlockRemoval(block);

        childrenContainer.getChildren().add(index, block);
    }

    private void setupBlockRemoval(BaseBlockView block) {

        block.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {

            if (event.getButton() != MouseButton.SECONDARY) {
                return;
            }

            if (!childrenContainer.getChildren().contains(block)) {
                return;
            }

            removeBlock(block);
            event.consume();
        });
    }

    public void removeBlock(BaseBlockView block) {
        childrenContainer.getChildren().remove(block);
    }

    public int getBlockCount() {
        return childrenContainer.getChildren().size();
    }

    public BaseBlockView getBlockAt(int index) {
        return (BaseBlockView) childrenContainer.getChildren().get(index);
    }

    public VBox getInternalContainer() {
        return childrenContainer;
    }

    @Override
    public BaseBlockModel createModel() {
        return null;
    }
}