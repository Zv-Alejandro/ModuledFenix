package org.ies.fenix.client.gui.model.script;

public class DialogBlockModel extends BaseBlockModel {

    private FenixCharacterModel character;
    private String dialog;

    public DialogBlockModel() {
        super("dialog"); // ← ESTO ES OBLIGATORIO
    }

    public FenixCharacterModel getCharacter() { return character; }
    public void setCharacter(FenixCharacterModel character) { this.character = character; }

    public String getDialog() { return dialog; }
    public void setDialog(String dialog) { this.dialog = dialog; }
}

