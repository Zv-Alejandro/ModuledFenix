package org.ies.fenix.client.gui.util;

import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

public class DragContext {

    private VBox sourceContainer;
    private BaseBlockView draggedView;
    private BaseBlockModel draggedModel;

    public BaseBlockView getDraggedView() {
        return draggedView;
    }

    public void setDraggedView(BaseBlockView draggedView) {
        this.draggedView = draggedView;
    }

    public BaseBlockModel getDraggedModel() {
        return draggedModel;
    }

    public void setDraggedModel(BaseBlockModel draggedModel) {
        this.draggedModel = draggedModel;
    }

    public VBox getSourceContainer() {
        return sourceContainer;
    }

    public void setSourceContainer(VBox sourceContainer) {
        this.sourceContainer = sourceContainer;
    }
}