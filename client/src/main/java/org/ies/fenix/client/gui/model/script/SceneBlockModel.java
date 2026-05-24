package org.ies.fenix.client.gui.model.script;

import java.util.LinkedList;
import java.util.List;

public class SceneBlockModel extends BaseBlockModel {
    private String name;
    private final LinkedList<BaseBlockModel> children = new LinkedList<>();

    public SceneBlockModel() {
        super("scene");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<BaseBlockModel> getChildren() { return children; }

    public void addChild(BaseBlockModel block) { children.add(block); }
    public void addChildAt(int index, BaseBlockModel block) { children.add(index, block); }
    public void removeChild(BaseBlockModel block) { children.remove(block); }

    public void moveChild(int oldIndex, int newIndex) {
        BaseBlockModel block = children.remove(oldIndex);
        children.add(newIndex, block);
    }
}

