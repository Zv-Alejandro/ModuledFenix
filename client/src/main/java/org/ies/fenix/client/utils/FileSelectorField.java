package org.ies.fenix.client.utils;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

public class FileSelectorField extends HBox {

    private final TextField textField = new TextField();
    private final Button button = new Button("Choose...");
    private File selectedFile;

    // Callback cuando se selecciona un archivo
    private Consumer<File> onFileSelected;

    public FileSelectorField() {
        super(10);

        textField.setEditable(false);
        textField.setPrefWidth(200);
        textField.getStyleClass().add("block-textfield");

        button.getStyleClass().add("block-button");
        button.setOnAction(e -> openChooser());

        getChildren().addAll(textField, button);
        getStyleClass().add("file-selector-field");
    }

    private void openChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            textField.setText(file.getName());

            // Notificar al bloque que un archivo fue seleccionado
            if (onFileSelected != null) {
                onFileSelected.accept(file);
            }
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
        textField.setText(file != null ? file.getName() : "");
    }

    public void setOnFileSelected(Consumer<File> callback) {
        this.onFileSelected = callback;
    }
}
