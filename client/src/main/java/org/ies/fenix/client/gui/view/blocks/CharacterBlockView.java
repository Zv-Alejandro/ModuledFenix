package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.CharacterBlockModel;
import org.ies.fenix.client.utils.FileSelectorField;

public class CharacterBlockView extends BaseBlockView {

    private FileSelectorField selector;

    // ============================================================
    //  CONSTRUCTOR SIN PARÁMETROS → MODO CATÁLOGO / NO INTERACTIVO
    // ============================================================
    public CharacterBlockView() {

        Label title = new Label("CHARACTER");
        title.getStyleClass().add("block-label");

        // El preview ocupa el espacio sobrante
        TextField preview = new TextField();
        preview.setEditable(false);
        preview.setDisable(true);
        preview.getStyleClass().add("block-textfield");

        preview.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(preview, Priority.ALWAYS);

        // El label mantiene su tamaño natural
        HBox.setHgrow(title, Priority.NEVER);

        HBox row = new HBox(10, title, preview);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }


    // ============================================================
    //  CONSTRUCTOR CON MODELO → MODO EDITOR / INTERACTIVO
    // ============================================================
    public CharacterBlockView(CharacterBlockModel model) {
        super.model = model;
        getStyleClass().add("block");
        getStyleClass().add("block-editor");

        Label title = new Label("SET CHARACTER");
        title.getStyleClass().add("block-label");
        title.setMinWidth(Region.USE_PREF_SIZE);

        selector = new FileSelectorField();

        // Si ya había imagen en el modelo, mostrarla
        if (model.getImage() != null) {
            selector.setSelectedFile(model.getImage());
        }

        // Callback cuando el usuario selecciona archivo
        selector.setOnFileSelected(file -> {
            if (file != null) {
                model.setImage(file);
            }
        });

        HBox row = new HBox(10, title, selector);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }

    @Override
    public BaseBlockModel createModel() {
        return new CharacterBlockModel();
    }
}
