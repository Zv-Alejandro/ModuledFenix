package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;

import java.net.URL;
import java.util.ResourceBundle;

public class ScriptEditorController implements Initializable {

    private final DragAndDropService dragService = new DragAndDropService();


    @FXML
    private VBox workspace;

    @FXML
    private VBox palette;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addPaletteBlock(BlockFactory.createNarrativeBlock());
        addPaletteBlock(BlockFactory.createShowBlock());
        addPaletteBlock(BlockFactory.createDecisionBlock());
        addPaletteBlock(BlockFactory.createOptionBlock());
        addPaletteBlock(BlockFactory.createSceneBlock());
        addPaletteBlock(BlockFactory.createBackgroundBlock());
        addPaletteBlock(BlockFactory.createCharacterCreateBlock());
        addPaletteBlock(BlockFactory.createCharacterBlock());
        setupWorkspaceDrop();
    }

    private void addPaletteBlock(BaseBlockView block){
        block.setOnDragDetected(event ->
                dragService.startDrag(block, event)
        );

        palette.getChildren().add(block);
    }

    private void setupWorkspaceDrop() {

        workspace.setPickOnBounds(true);

        workspace.setOnDragOver(dragService::handleDragOver);

        workspace.setOnDragDropped(event -> {

            BaseBlockView paletteBlock =
                    dragService.getDraggedBlock();

            if (paletteBlock != null) {

                // 1. Crear modelo
                BaseBlockModel model =
                        paletteBlock.createModel();

                // 2. Crear vista interactiva
                BaseBlockView editorBlock =
                        BlockFactory.createView(model);

                // 3. Añadir al workspace
                workspace.getChildren().add(editorBlock);
            }

            event.setDropCompleted(true);
            event.consume();
        });
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
