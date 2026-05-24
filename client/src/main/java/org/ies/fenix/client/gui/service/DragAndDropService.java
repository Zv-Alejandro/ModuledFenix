package org.ies.fenix.client.gui.service;

import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.util.DragContext;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

public class DragAndDropService {

    public static final DataFormat BLOCK_FORMAT =
            new DataFormat("fenix/block");

    private DragContext context = new DragContext();

    // ============================================================
    // START DRAG (FIX: root resolution)
    // ============================================================

    public void startDrag(BaseBlockView view, MouseEvent event) {

        BaseBlockView root = resolveRoot(view);

        Dragboard db = root.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();

        String type = root.getType();

        System.out.println("Drag: " + type);

        content.put(BLOCK_FORMAT, type);

        db.setContent(content);

        DragContext ctx = new DragContext();
        ctx.setDraggedView(root);

        if (root.getParent() instanceof VBox vBox) {
            ctx.setSourceContainer(vBox);
        }

        this.context = ctx;

        event.consume();
    }

    // ============================================================
    // DRAG OVER (FIX: validation hook ready)
    // ============================================================

    public void handleDragOver(DragEvent event) {

        if (!event.getDragboard().hasContent(BLOCK_FORMAT)) {
            return;
        }

        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    }

    // ============================================================
    // CONTEXT
    // ============================================================

    public DragContext getContext() {
        return context;
    }

    // ============================================================
    // FIX: ROOT RESOLUTION (CRÍTICO)
    // ============================================================

    private BaseBlockView resolveRoot(BaseBlockView view) {

        while (view.getParent() instanceof BaseBlockView parent) {
            view = parent;
        }

        return view;
    }
}