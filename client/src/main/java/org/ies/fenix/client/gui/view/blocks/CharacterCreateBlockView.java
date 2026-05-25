package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.CharacterCreateBlockModel;
import org.ies.fenix.client.gui.util.EditorRegistry;

public class CharacterCreateBlockView extends BaseBlockView {

    private TextField nameField;
    private ColorPicker colorPicker;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public CharacterCreateBlockView() {

        getStyleClass().add("block-catalog");

        Label chr = new Label("NAME");
        Label col = new Label("COLOR");

        chr.getStyleClass().add("block-label");
        col.getStyleClass().add("block-label");

        TextField nameMock = new TextField();
        nameMock.setDisable(true);
        nameMock.setPrefWidth(40); // ancho mínimo
        nameMock.setMaxWidth(Double.MAX_VALUE); // puede crecer
        HBox.setHgrow(nameMock, Priority.ALWAYS); // ocupa espacio sobrante
        nameMock.getStyleClass().add("block-textfield");

        TextField colorMock = new TextField();
        colorMock.setDisable(true);
        colorMock.setPrefWidth(80);
        colorMock.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(colorMock, Priority.ALWAYS);
        colorMock.getStyleClass().add("block-textfield");

        HBox.setHgrow(chr, Priority.NEVER);
        HBox.setHgrow(col, Priority.NEVER);


        HBox row = new HBox(10, chr, nameMock, col, colorMock);
        row.getStyleClass().add("block-row");

        getChildren().add(row);
    }

    // ============================================================
    //  MODO EDITOR (bloque real con modelo)
    // ============================================================
    public CharacterCreateBlockView(CharacterCreateBlockModel model) {

        super.model = model;
        getStyleClass().add("block-editor");

        // ============================================================
        // LABELS
        // ============================================================

        Label chr = new Label("AVATAR NAME");
        Label col = new Label("DISPLAY COLOR");

        chr.getStyleClass().add("block-label");
        col.getStyleClass().add("block-label");

        // Deja que JavaFX calcule su tamaño preferido
        chr.setMinWidth(Region.USE_PREF_SIZE);
        col.setMinWidth(Region.USE_PREF_SIZE);

        chr.setMaxWidth(Region.USE_PREF_SIZE);
        col.setMaxWidth(Region.USE_PREF_SIZE);

        // Si quieres que ambos tengan el mismo ancho:
        // el más grande manda
        chr.widthProperty().addListener((obs, oldW, newW) -> {
            double max = Math.max(newW.doubleValue(), col.getWidth());
            chr.setMinWidth(max);
            col.setMinWidth(max);
        });
        col.widthProperty().addListener((obs, oldW, newW) -> {
            double max = Math.max(newW.doubleValue(), chr.getWidth());
            chr.setMinWidth(max);
            col.setMinWidth(max);
        });

        // ============================================================
        // NAME FIELD (EXPANDIBLE)
        // ============================================================

        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(model.getName());
        nameField.getStyleClass().add("block-textfield");

        nameField.textProperty().addListener((obs, o, n) -> {
            model.setName(n);
            EditorRegistry.updateCharacter(model);
        });

        nameField.setMinWidth(140);
        nameField.setPrefWidth(220);
        nameField.setMaxWidth(Double.MAX_VALUE);



        nameField.setMinWidth(140);
        nameField.setPrefWidth(220);
        nameField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameField, Priority.ALWAYS);

        // ============================================================
        // COLOR PICKER (FIJO)
        // ============================================================

        colorPicker = new ColorPicker();
        try {
            colorPicker.setValue(Color.web(model.getColor()));
        } catch (Exception e) {
            colorPicker.setValue(Color.WHITE);
        }

        colorPicker.getStyleClass().add("block-colorpicker");

        colorPicker.valueProperty().addListener((obs, o, n) -> {
            model.setColor(toHex(n));
            EditorRegistry.updateCharacter(model);
        });

        colorPicker.setMinWidth(Region.USE_PREF_SIZE);
        colorPicker.setMaxWidth(Region.USE_PREF_SIZE);

        // ============================================================
        // ROW LAYOUT
        // ============================================================

        HBox row = new HBox(12, chr, nameField, col, colorPicker);
        row.getStyleClass().add("block-row");

        row.setFillHeight(true);
        row.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(nameField, Priority.ALWAYS);

        getChildren().add(row);
    }




    // ============================================================
    //  UTILIDAD: Color → HEX
    // ============================================================
    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }
    @Override
    public BaseBlockModel createModel() {
        return new CharacterCreateBlockModel();
    }
}
