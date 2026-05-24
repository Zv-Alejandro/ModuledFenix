package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.model.script.CharacterCreateBlockModel;

public class CharacterCreateBlockView extends BaseBlockView {

    private TextField nameField;
    private ColorPicker colorPicker;
    private CharacterCreateBlockModel model;

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
        this.model = model;

        getStyleClass().add("block-editor");

        Label chr = new Label("CHR");
        Label col = new Label("COL");

        chr.getStyleClass().add("block-label");
        col.getStyleClass().add("block-label");

        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(model.getName());
        nameField.getStyleClass().add("block-textfield");

        nameField.textProperty().addListener((obs, o, n) -> model.setName(n));

        colorPicker = new ColorPicker();
        try {
            colorPicker.setValue(Color.web(model.getColor()));
        } catch (Exception e) {
            colorPicker.setValue(Color.WHITE);
        }
        colorPicker.getStyleClass().add("block-colorpicker");

        colorPicker.valueProperty().addListener((obs, o, n) -> {
            String hex = toHex(n);
            model.setColor(hex);
        });

        HBox row = new HBox(10, chr, nameField, col, colorPicker);
        row.getStyleClass().add("block-row");

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
