package org.ies.fenix.client.gui.view.blocks;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class BaseBlockView extends HBox {

    public BaseBlockView() {

        // Espaciado vertical entre elementos internos
        setSpacing(10);

        // Padding interno del bloque
        setPadding(new Insets(15));

        // Clase CSS base para todos los bloques
        getStyleClass().add("block");

        // Cada vez que se añada un hijo, se le aplica el crecimiento automático
        getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Node node : change.getAddedSubList()) {
                        node.maxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(node, Priority.ALWAYS);
                    }
                }
            }
        });
    }
}
