package org.ies.fenix.client.gui.service;

import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.util.DragContext;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

public class DragAndDropService {

    public static final DataFormat BLOCK_FORMAT =
            new DataFormat("fenix/block");

    private DragContext context = new DragContext();

    public void startDrag(BaseBlockView view, MouseEvent event) {

        Dragboard db = view.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();

        String type = view.getType();

        System.out.println("Drag: " + type);

        content.put(BLOCK_FORMAT, type);

        db.setContent(content);

        DragContext ctx = new DragContext();
        ctx.setDraggedView(view);

        if (view.getParent() instanceof VBox vBox) {
            ctx.setSourceContainer(vBox);
        }

        this.context = ctx;

        event.consume();
    }

    public void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasContent(BLOCK_FORMAT)) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    public DragContext getContext() {
        return context;
    }
}