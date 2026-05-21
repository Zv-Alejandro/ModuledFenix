package org.ies.fenix.client.gui.model.script;

public class OptionBlockModel extends BaseBlockModel {

    private String optionSentence;
    private SceneBlockModel sceneBlockModel;

    public OptionBlockModel() {
        super("option");
    }

    public String getOptionSentence() { return optionSentence; }
    public void setOptionSentence(String optionSentence) { this.optionSentence = optionSentence; }

    public SceneBlockModel getSceneBlockModel() { return sceneBlockModel; }
    public void setSceneBlockModel(SceneBlockModel sceneBlockModel) { this.sceneBlockModel = sceneBlockModel; }
}

