package org.ies.fenix.client.gui.model.script;

public class CharacterCreateBlockModel extends BaseBlockModel {

    private String name;
    private String color;

    public CharacterCreateBlockModel() {
        super("character_create");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}

