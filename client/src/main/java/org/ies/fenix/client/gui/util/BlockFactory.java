package org.ies.fenix.client.gui.util;

import javafx.scene.Node;
import org.ies.fenix.client.gui.model.script.*;
import org.ies.fenix.client.gui.view.blocks.*;

public class BlockFactory {

    // ============================================================
    //  Crear vista desde modelo (modo editor)
    // ============================================================
    public static BaseBlockView createView(BaseBlockModel model) {

        return switch (model.getType()) {
            case "text" -> new NarrativeBlockView((NarrativeBlockModel) model);
            case "dialog" -> new DialogBlockView((DialogBlockModel) model);
            case "background" -> new BackgroundBlockView((BackgroundBlockModel) model);
            case "character_create" -> new CharacterCreateBlockView((CharacterCreateBlockModel) model);
            case "decision" -> new DecisionBlockView((DecisionBlockModel) model);
            case "option" -> new OptionBlockView((OptionBlockModel) model);
            case "scene" -> new SceneBlockView((SceneBlockModel) model);
            case "character" -> new CharacterBlockView((CharacterBlockModel) model);
            default -> throw new IllegalArgumentException("Unknown block type: " + model.getType());
        };

    }

    // ============================================================
    //  Bloques de catálogo (sin modelo)
    // ============================================================
    public static NarrativeBlockView createNarrativeBlock() {
        return new NarrativeBlockView();
    }

    public static DialogBlockView createShowBlock() {
        return new DialogBlockView();
    }

    public static SceneBlockView createSceneBlock() {
        return new SceneBlockView();
    }

    public static OptionBlockView createOptionBlock(){
        return new OptionBlockView();
    }

    public static DecisionBlockView createDecisionBlock() {
        return new DecisionBlockView();
    }

    public static BackgroundBlockView createBackgroundBlock() { return new BackgroundBlockView();}

    public static CharacterCreateBlockView createCharacterCreateBlock() {
        return new CharacterCreateBlockView();
    }

    public static CharacterBlockView createCharacterBlock() {
        return new CharacterBlockView();
    }
}
