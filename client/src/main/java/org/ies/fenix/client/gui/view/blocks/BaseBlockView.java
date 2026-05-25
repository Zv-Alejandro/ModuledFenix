package org.ies.fenix.client.gui.view.blocks;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;

public abstract class BaseBlockView extends HBox {

    private boolean paletteBlock = false;
    protected BaseBlockModel model;

    public BaseBlockView() {

        setSpacing(10);
        setPadding(new Insets(15));
        getStyleClass().add("block");
        setPickOnBounds(true);

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

    public abstract BaseBlockModel createModel();


    public boolean isPaletteBlock() {
        return paletteBlock;
    }

    public void setPaletteBlock(boolean paletteBlock) {
        this.paletteBlock = paletteBlock;
    }

    public String getType() {
        BaseBlockModel m = getModel();
        return (m != null) ? m.getType() : createModel().getType();
    }

    public BaseBlockModel getModel() {
        return model;
    }
}