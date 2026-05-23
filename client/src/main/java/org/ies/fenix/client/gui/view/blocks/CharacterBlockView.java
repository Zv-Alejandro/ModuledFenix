package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.Label;
import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.layout.HBox;
import org.ies.fenix.client.gui.model.script.CharacterBlockModel;

public class CharacterBlockView extends BaseBlockView {

    private CharacterBlockModel model;
    private TextField selectedFileField;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public CharacterBlockView() {

        getStyleClass().add("block-catalog");

        Label title = new Label("SET CHARACTER");
        title.getStyleClass().add("block-label");

        TextField preview = new TextField("Select file...");
        preview.setDisable(true);
        preview.setPrefWidth(200);
        preview.getStyleClass().add("block-textfield");

        HBox row = new HBox(10, title, preview);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }

    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public CharacterBlockView(CharacterBlockModel model) {
        this.model = model;

        getStyleClass().add("block-editor");

        Label title = new Label("SET CHARACTER");
        title.getStyleClass().add("block-label");

        selectedFileField = new TextField();
        selectedFileField.setPrefWidth(200);
        selectedFileField.setEditable(false);
        selectedFileField.getStyleClass().add("block-textfield");

        // Mostrar archivo actual si existe
        if (model.getImage() != null) {
            selectedFileField.setText(model.getImage().getName());
        }

        Button chooseBtn = new Button("Choose...");
        chooseBtn.getStyleClass().add("block-button");
        chooseBtn.setOnAction(e -> openFileChooser());

        HBox row = new HBox(10, title, selectedFileField, chooseBtn);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }

    // ============================================================
    //  FileChooser
    // ============================================================
    private void openFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Character Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            selectedFileField.setText(file.getName());
            model.setImage(file);
        }
    }
}
