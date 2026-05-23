package org.ies.fenix.client.gui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.ies.fenix.client.gui.model.script.FenixCharacterModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;

public class EditorRegistry {

    private static final ObservableList<FenixCharacterModel> characters =
            FXCollections.observableArrayList();

    private static final ObservableList<SceneBlockModel> scenes =
            FXCollections.observableArrayList();

    public static ObservableList<FenixCharacterModel> getCharacters() {
        return characters;
    }

    public static ObservableList<SceneBlockModel> getScenes() {
        return scenes;
    }

    public static void addCharacter(FenixCharacterModel c) {
        characters.add(c);
    }

    public static void addScene(SceneBlockModel s) {
        scenes.add(s);
    }
}

