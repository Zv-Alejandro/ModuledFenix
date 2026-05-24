package org.ies.fenix.client.gui.service;

import javafx.scene.input.*;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

public class DragAndDropService {

    public static final DataFormat BLOCK_FORMAT =
            new DataFormat("fenix/block");

    private BaseBlockView draggedBlock;

    public void startDrag(BaseBlockView source, MouseEvent event) {

        draggedBlock = source;

        Dragboard db =
                source.startDragAndDrop(TransferMode.COPY);

        ClipboardContent content =
                new ClipboardContent();

        // ESTO ES OBLIGATORIO
        content.put(BLOCK_FORMAT, "block");

        db.setContent(content);

        event.consume();
    }

    public void handleDragOver(DragEvent event) {

        Dragboard db = event.getDragboard();

        // SI NO TIENE EL FORMAT → BLOQUEADO
        if (db.hasContent(BLOCK_FORMAT)) {

            event.acceptTransferModes(TransferMode.COPY);
        }

        event.consume();
    }

    public BaseBlockView getDraggedBlock() {
        return draggedBlock;
    }
}