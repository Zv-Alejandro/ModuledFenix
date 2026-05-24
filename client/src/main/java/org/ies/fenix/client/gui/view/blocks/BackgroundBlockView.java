package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.ies.fenix.client.gui.model.script.BackgroundBlockModel;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.utils.FileSelectorField;

public class BackgroundBlockView extends BaseBlockView {

    private FileSelectorField selector;

    // ============================================================
    //  CONSTRUCTOR SIN PARÁMETROS → MODO CATÁLOGO / NO INTERACTIVO
    // ============================================================
    public BackgroundBlockView() {

        Label title = new Label("BACKGROUND");
        title.getStyleClass().add("block-label");

        TextField preview = new TextField();
        preview.setEditable(false);
        preview.setDisable(true);
        preview.getStyleClass().add("block-textfield");

        preview.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(preview, Priority.ALWAYS);

        HBox.setHgrow(title, Priority.NEVER);

        HBox row = new HBox(10, title, preview);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }

    @Override
    public BaseBlockModel createModel() {
        return new BackgroundBlockModel();
    }


    // ============================================================
    //  CONSTRUCTOR CON PARÁMETROS → MODO EDITOR / INTERACTIVO
    // ============================================================
    public BackgroundBlockView(BackgroundBlockModel model) {

        super.model = model;

        getStyleClass().addAll("block", "block-editor");

        // ============================================================
        // LABEL
        // ============================================================

        Label title = new Label("SET BACKGROUND");
        title.getStyleClass().add("block-label");
        title.setMinWidth(Region.USE_PREF_SIZE);

        // ============================================================
        // FILE SELECTOR
        // ============================================================

        selector = new FileSelectorField();

        if (model.getImage() != null) {
            selector.setSelectedFile(model.getImage());
        }

        selector.setOnFileSelected(file -> {
            if (file != null) {
                model.setImage(file);
            }
        });

        // EXPANSIÓN REAL
        selector.setMaxWidth(Double.MAX_VALUE);
        selector.setMinWidth(360);
        selector.setPrefWidth(500);
        HBox.setHgrow(selector, Priority.ALWAYS);

        // ============================================================
        // ROW LAYOUT
        // ============================================================

        HBox row = new HBox(10, title, selector);
        row.getStyleClass().add("block-row");

        row.setMaxWidth(Double.MAX_VALUE);
        row.setFillHeight(true);

        HBox.setHgrow(title, Priority.NEVER);
        HBox.setHgrow(selector, Priority.ALWAYS);

        // ============================================================
        // ROOT
        // ============================================================

        setMaxWidth(Double.MAX_VALUE);

        getChildren().add(row);
    }
}
