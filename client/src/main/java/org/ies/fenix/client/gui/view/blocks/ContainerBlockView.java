package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;

import java.util.List;

public abstract class ContainerBlockView extends BaseBlockView {

    protected VBox childrenContainer;
    protected List<BaseBlockModel> children;


    public VBox getChildrenContainer() {
        return childrenContainer;
    }

    public abstract boolean canContain(BaseBlockModel child);
}