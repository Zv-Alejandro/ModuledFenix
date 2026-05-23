package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.util.BlockFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ScriptEditorController implements Initializable {

    @FXML
    private VBox workspace;

    @FXML
    private VBox palette;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        palette.getChildren().addAll(
                BlockFactory.createNarrativeBlock(),
                BlockFactory.createShowBlock(),
                BlockFactory.createDecisionBlock(),
                BlockFactory.createOptionBlock(),
                BlockFactory.createSceneBlock(),
                BlockFactory.createBackgroundBlock(),
                BlockFactory.createCharacterCreateBlock(),
                BlockFactory.createCharacterBlock()
        );
    }

    // ============================================================
    //  BLOQUES DE TEXTO / DIÁLOGO
    // ============================================================
    @FXML
    public void addNarrativeBlock() {
        workspace.getChildren().add(
                BlockFactory.createNarrativeBlock()
        );
    }

    @FXML
    public void addShowBlock() {
        workspace.getChildren().add(
                BlockFactory.createShowBlock()
        );
    }

    // ============================================================
    //  DECISIONES Y OPCIONES
    // ============================================================
    @FXML
    public void addDecisionBlock() {
        workspace.getChildren().add(
                BlockFactory.createDecisionBlock()
        );
    }

    @FXML
    public void addOptionBlock() {
        workspace.getChildren().add(
                BlockFactory.createOptionBlock()
        );
    }

    // ============================================================
    //  ESCENAS
    // ============================================================
    @FXML
    public void addSceneBlock() {
        workspace.getChildren().add(
                BlockFactory.createSceneBlock()
        );
    }

    // ============================================================
    //  FONDOS Y PERSONAJES
    // ============================================================
    @FXML
    public void addBackgroundBlock() {
        workspace.getChildren().add(
                BlockFactory.createBackgroundBlock()
        );
    }

    @FXML
    public void addCharacterCreateBlock() {
        workspace.getChildren().add(
                BlockFactory.createCharacterCreateBlock()
        );
    }

    @FXML
    public void addCharacterBlock() {
        workspace.getChildren().add(
                BlockFactory.createCharacterBlock()
        );
    }


}
