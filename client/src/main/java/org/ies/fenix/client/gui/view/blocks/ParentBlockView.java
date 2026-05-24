package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class ParentBlockView extends BaseBlockView {

    protected final VBox contentWrapper;

    public ParentBlockView() {

        // Contenedor vertical interno
        contentWrapper = new VBox(10);

        // Que el VBox crezca dentro del HBox
        contentWrapper.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(contentWrapper, Priority.ALWAYS);

        // Añadirlo como único hijo del bloque
        getChildren().add(contentWrapper);
    }

    public VBox getContentWrapper() {
        return contentWrapper;
    }
}
