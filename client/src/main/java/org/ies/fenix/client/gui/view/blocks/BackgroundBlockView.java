package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.ies.fenix.client.gui.model.script.BackgroundBlockModel;

import java.io.File;
import java.util.List;

public class BackgroundBlockView extends BaseBlockView {

    private ComboBox<File> backgroundCombo;
    private BackgroundBlockModel model;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public BackgroundBlockView() {

        Label title = new Label("SET BACKGROUND");

        ComboBox<String> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);
        previewCombo.getItems().add("HALL");
        previewCombo.setValue("HALL");

        getChildren().addAll(title, previewCombo);

        // Estilo tipo tarjeta
        setStyle("-fx-background-color: #f7d75c; -fx-padding: 10; -fx-border-color: black;");
    }

    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public BackgroundBlockView(List<File> availableBackgrounds, BackgroundBlockModel model) {
        this.model = model;

        Label title = new Label("SET BACKGROUND");

        backgroundCombo = new ComboBox<>();
        backgroundCombo.getItems().addAll(availableBackgrounds);

        // Cargar valor inicial
        backgroundCombo.setValue(model.getImage());

        // Listener → actualiza el modelo
        backgroundCombo.valueProperty().addListener((obs, oldV, newV) -> {
            model.setImage(newV);
        });

        getChildren().addAll(title, backgroundCombo);

        setStyle("-fx-background-color: #fff7c2; -fx-padding: 10; -fx-border-color: black;");
    }

    // ============================================================
    //  Mét-odo auxiliar para recargar fondos si cambian
    // ============================================================
    public void updateBackgroundList(List<File> backgrounds) {
        if (backgroundCombo != null) {
            backgroundCombo.getItems().setAll(backgrounds);
        }
    }
}
