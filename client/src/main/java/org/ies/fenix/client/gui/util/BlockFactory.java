package org.ies.fenix.client.gui.util;

import org.ies.fenix.client.gui.model.script.*;
import org.ies.fenix.client.gui.view.blocks.*;

import java.io.File;
import java.util.List;

public class BlockFactory {

    private static List<FenixCharacterModel> characters;
    private static List<SceneBlockModel> scenes;
    private static List<File> backgrounds;

    // Inicializar listas globales
    public static void init(
            List<FenixCharacterModel> characterList,
            List<SceneBlockModel> sceneList,
            List<File> backgroundList
    ) {
        characters = characterList;
        scenes = sceneList;
        backgrounds = backgroundList;
    }

    // Crear vista desde modelo
    public static BaseBlockView createView(BaseBlockModel model) {

        switch (model.getType()) {

            case "text":
                return new NarrativeBlockView((NarrativeBlockModel) model);

            case "dialog":
                return new DialogBlockView(characters, (DialogBlockModel) model);

            case "background":
                return new BackgroundBlockView(backgrounds, (BackgroundBlockModel) model);

            case "character_create":
                return new CharacterCreateBlockView((CharacterCreateBlockModel) model);

            case "decision":
                return new DecisionBlockView((DecisionBlockModel) model, scenes);

            case "option":
                return new OptionBlockView((OptionBlockModel) model, scenes);

            case "scene":
                return new SceneBlockView((SceneBlockModel) model);
        }

        throw new IllegalArgumentException("Unknown block type: " + model.getType());
    }

    // Bloques de catálogo (sin modelo)
    public static NarrativeBlockView createNarrativeBlock() { return new NarrativeBlockView(); }
    public static DialogBlockView createShowBlock() { return new DialogBlockView(); }
    public static SceneBlockView createSceneBlock() { return new SceneBlockView(); }
    public static OptionBlockView createOptionBlock(){ return new OptionBlockView(); }
    public static DecisionBlockView createDecisionBlock() { return new DecisionBlockView(); }
    public static BackgroundBlockView createBackgroundBlock() { return new BackgroundBlockView(); }
    public static CharacterCreateBlockView createCharacterCreateBlock() { return new CharacterCreateBlockView(); }
}
