package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.gui.service.DragAndDropService;
import org.ies.fenix.client.gui.util.BlockFactory;
import org.ies.fenix.client.gui.view.blocks.BaseBlockView;
import org.ies.fenix.client.gui.view.blocks.WorkspaceBlockView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the visual script editor.
 *
 * <p>This screen contains two main areas:</p>
 *
 * <ul>
 *     <li>A palette with the available script blocks.</li>
 *     <li>A workspace where blocks can be dropped and arranged.</li>
 * </ul>
 *
 * <p>The controller only prepares the editor UI and connects palette blocks to
 * the drag-and-drop service. The internal behavior of each block is handled by
 * the block view/model classes and the editor services.</p>
 */
public class ScriptEditorController implements Initializable {

    // ============================================================
    // SERVICES
    // ============================================================

    /**
     * Service responsible for handling drag-and-drop operations between the
     * palette and the workspace.
     */
    private final DragAndDropService dragService = new DragAndDropService();

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private VBox workspace;

    @FXML
    private VBox palette;

    // ============================================================
    // STATE
    // ============================================================

    private WorkspaceBlockView workspaceBlockView;

    // ============================================================
    // INITIALIZATION
    // ============================================================

    /**
     * Initializes the visual editor after the FXML file has been loaded.
     *
     * <p>The workspace is created first, then the available palette blocks are
     * added one by one.</p>
     *
     * @param url            FXML location
     * @param resourceBundle localization resources
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupWorkspace();
        setupPalette();
    }

    /**
     * Creates and attaches the main workspace block.
     *
     * <p>The workspace is allowed to grow vertically so it can use all available
     * space inside the editor screen.</p>
     */
    private void setupWorkspace() {
        workspaceBlockView = new WorkspaceBlockView(dragService);

        workspaceBlockView.setMaxWidth(Double.MAX_VALUE);
        workspaceBlockView.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(workspaceBlockView, Priority.ALWAYS);

        workspace.getChildren().clear();
        workspace.getChildren().add(workspaceBlockView);
    }

    /**
     * Adds all available block types to the palette.
     */
    private void setupPalette() {
        addPaletteBlock(BlockFactory.createNarrativeBlock());
        addPaletteBlock(BlockFactory.createShowBlock());
        addPaletteBlock(BlockFactory.createDecisionBlock());
        addPaletteBlock(BlockFactory.createOptionBlock());
        addPaletteBlock(BlockFactory.createSceneBlock());
        addPaletteBlock(BlockFactory.createBackgroundBlock());
        addPaletteBlock(BlockFactory.createCharacterCreateBlock());
        addPaletteBlock(BlockFactory.createCharacterBlock());
    }

    /**
     * Adds a block to the palette and configures it as a draggable template.
     *
     * <p>Palette blocks are not meant to be edited directly as final script
     * elements. They work as reusable templates that can be dragged into the
     * workspace.</p>
     *
     * @param block block view to add to the palette
     */
    private void addPaletteBlock(BaseBlockView block) {
        block.setPaletteBlock(true);

        block.setOnDragDetected(event -> {
            dragService.startDrag(block, event);
            event.consume();
        });

        palette.getChildren().add(block);
    }
}