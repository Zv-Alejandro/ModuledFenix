package org.ies.fenix.client.gui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.ies.fenix.client.gui.model.script.CharacterCreateBlockModel;
import org.ies.fenix.client.gui.model.script.FenixCharacterModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;

import java.util.HashMap;
import java.util.Map;

public class EditorRegistry {

    private static final ObservableList<FenixCharacterModel> characters =
            FXCollections.observableArrayList();

    private static final ObservableList<SceneBlockModel> scenes =
            FXCollections.observableArrayList();

    private static final Map<CharacterCreateBlockModel, FenixCharacterModel> characterLinks =
            new HashMap<>();

    public static ObservableList<FenixCharacterModel> getCharacters() {
        return characters;
    }

    public static ObservableList<SceneBlockModel> getScenes() {
        return scenes;
    }

    public static void addScene(SceneBlockModel scene) {

        if (scene == null || scenes.contains(scene)) {
            return;
        }

        scenes.add(scene);
    }

    public static void updateScene(SceneBlockModel scene) {

        if (scene == null) {
            return;
        }

        int index = scenes.indexOf(scene);

        if (index < 0) {
            scenes.add(scene);
            return;
        }

        scenes.set(index, scene);
    }

    public static void removeScene(SceneBlockModel scene) {

        if (scene == null) {
            return;
        }

        scenes.remove(scene);
    }

    public static void addCharacter(CharacterCreateBlockModel characterCreateModel) {

        if (characterCreateModel == null) {
            return;
        }

        if (characterLinks.containsKey(characterCreateModel)) {
            updateCharacter(characterCreateModel);
            return;
        }

        FenixCharacterModel character = new FenixCharacterModel();
        character.setName(characterCreateModel.getName());
        character.setColor(characterCreateModel.getColor());

        characterLinks.put(characterCreateModel, character);
        characters.add(character);
    }

    public static void updateCharacter(CharacterCreateBlockModel characterCreateModel) {

        if (characterCreateModel == null) {
            return;
        }

        FenixCharacterModel character = characterLinks.get(characterCreateModel);

        if (character == null) {
            addCharacter(characterCreateModel);
            return;
        }

        character.setName(characterCreateModel.getName());
        character.setColor(characterCreateModel.getColor());

        int index = characters.indexOf(character);

        if (index >= 0) {
            characters.set(index, character);
        }
    }

    public static void removeCharacter(CharacterCreateBlockModel characterCreateModel) {

        if (characterCreateModel == null) {
            return;
        }

        FenixCharacterModel character = characterLinks.remove(characterCreateModel);

        if (character != null) {
            characters.remove(character);
        }
    }
}