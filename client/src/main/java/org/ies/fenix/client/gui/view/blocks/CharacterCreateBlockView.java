package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.ies.fenix.client.gui.model.script.CharacterCreateBlockModel;

public class CharacterCreateBlockView extends BaseBlockView {

    private TextField nameField;
    private ColorPicker colorPicker;
    private CharacterCreateBlockModel model;

    // ============================================================
    //  MODO CATÁLOGO (solo imagen, sin modelo, sin listeners)
    // ============================================================
    public CharacterCreateBlockView() {

        Label chr = new Label("CHR");
        Label col = new Label("COL");

        TextField nameMock = new TextField();
        nameMock.setDisable(true);
        nameMock.setPrefWidth(120);

        TextField colorMock = new TextField();
        colorMock.setDisable(true);
        colorMock.setPrefWidth(80);

        chr.setStyle("-fx-background-color: #d7b4ff; -fx-padding: 5; -fx-border-color: black;");
        col.setStyle("-fx-background-color: #d7b4ff; -fx-padding: 5; -fx-border-color: black;");
        nameMock.setStyle("-fx-background-color: #e8e8e8; -fx-border-color: black;");
        colorMock.setStyle("-fx-background-color: #e8e8e8; -fx-border-color: black;");

        HBox row = new HBox(10, chr, nameMock, col, colorMock);
        getChildren().add(row);

        setStyle("-fx-background-color: #f7d75c; -fx-padding: 10; -fx-border-color: black;");
    }
    public CharacterCreateBlockView(CharacterCreateBlockModel model) {
        this.model = model;

        Label chr = new Label("CHR");
        Label col = new Label("COL");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(model.getName());
        nameField.textProperty().addListener((obs, o, n) -> model.setName(n));

        TextField colorField = new TextField();
        colorField.setPromptText("Color (HEX)");
        colorField.setText(model.getColor());
        colorField.textProperty().addListener((obs, o, n) -> model.setColor(n));

        chr.setStyle("-fx-background-color: #d7b4ff; -fx-padding: 5; -fx-border-color: black;");
        col.setStyle("-fx-background-color: #d7b4ff; -fx-padding: 5; -fx-border-color: black;");
        nameField.setStyle("-fx-border-color: black;");
        colorField.setStyle("-fx-border-color: black;");

        HBox row = new HBox(10, chr, nameField, col, colorField);
        getChildren().add(row);

        setStyle("-fx-background-color: #fff7c2; -fx-padding: 10; -fx-border-color: black;");
    }

}
