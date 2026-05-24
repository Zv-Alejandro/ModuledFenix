package org.ies.fenix.client.utils;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

public class FileSelectorField extends HBox {

    private final TextField textField = new TextField();
    private final Button button = new Button("Choose...");
    private File selectedFile;

    private Consumer<File> onFileSelected;

    public FileSelectorField() {
        super(10);

        textField.setEditable(false);

        // Campo del nombre del archivo:
        // suficiente para nombres algo largos y expansible si hay espacio.
        textField.setMinWidth(260);
        textField.setPrefWidth(380);
        textField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textField, Priority.ALWAYS);

        button.getStyleClass().add("block-button");
        button.setMinWidth(Button.USE_PREF_SIZE);
        button.setPrefWidth(Button.USE_COMPUTED_SIZE);
        HBox.setHgrow(button, Priority.NEVER);
        button.setOnAction(e -> openChooser());

        // El componente completo también debe poder expandirse.
        setMinWidth(360);
        setPrefWidth(500);
        setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(textField, button);
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