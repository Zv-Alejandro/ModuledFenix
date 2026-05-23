package org.ies.fenix.client.gui.view.blocks;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public abstract class BaseBlockView extends VBox {

    public BaseBlockView() {

        // Espaciado vertical entre elementos internos
        setSpacing(10);

        // Padding interno del bloque
        setPadding(new Insets(15));

        // Clase CSS base para todos los bloques
        getStyleClass().add("block");
    }
}
