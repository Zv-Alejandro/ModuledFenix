package org.ies.fenix.client.gui.model.script;

public class NarrativeBlockModel extends BaseBlockModel {

    private String narration;

    public NarrativeBlockModel() {
        super("text");
    }

    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}


