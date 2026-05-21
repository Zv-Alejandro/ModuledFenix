package org.ies.fenix.client.gui.model.script;

import java.util.LinkedList;

public class DecisionBlockModel extends BaseBlockModel {

    private String sentence;
    private LinkedList<OptionBlockModel> options = new LinkedList<>();

    public DecisionBlockModel() {
        super("decision");
    }

    public String getSentence() { return sentence; }
    public void setSentence(String sentence) { this.sentence = sentence; }

    public LinkedList<OptionBlockModel> getOptions() { return options; }
    public void setOptions(LinkedList<OptionBlockModel> options) { this.options = options; }
}

