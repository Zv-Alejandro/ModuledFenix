package org.ies.fenix.client.gui.view.blocks;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import org.ies.fenix.client.gui.model.script.BaseBlockModel;
import org.ies.fenix.client.gui.util.EditorRegistry;
import org.ies.fenix.client.gui.model.script.OptionBlockModel;
import org.ies.fenix.client.gui.model.script.SceneBlockModel;
import org.ies.fenix.client.utils.ExpandableTextArea;

public class OptionBlockView extends BaseBlockView {

    private TextArea optionSentenceArea;
    private ComboBox<SceneBlockModel> jumpSceneCombo;

    // ============================================================
    //  MODO EDITOR
    // ============================================================
    public OptionBlockView(OptionBlockModel model) {

        super.model = model;

        getStyleClass().add("block-editor");

        // ===== OPTION =====

        Label instruction1 = new Label("OPTION");
        instruction1.getStyleClass().add("block-label");
        instruction1.setMinWidth(Region.USE_PREF_SIZE);

        optionSentenceArea = new ExpandableTextArea();
        optionSentenceArea.setPromptText("Option sentence...");
        optionSentenceArea.getStyleClass().add("block-textarea");

        // ===== JUMP SCENE =====

        Label instruction2 = new Label("JUMP SCENE");
        instruction2.getStyleClass().add("block-label");
        instruction2.setMinWidth(Region.USE_PREF_SIZE);

        jumpSceneCombo = new ComboBox<>();
        jumpSceneCombo.setItems(EditorRegistry.getScenes());
        jumpSceneCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(SceneBlockModel scene) {
                if (scene == null) {
                    return "";
                }

                String name = scene.getName();

                if (name == null || name.isBlank()) {
                    return "Unnamed scene";
                }

                return name;
            }

            @Override
            public SceneBlockModel fromString(String string) {
                return null;
            }
        });
        jumpSceneCombo.getStyleClass().add("block-combo");

        jumpSceneCombo.setValue(model.getSceneBlockModel());

        jumpSceneCombo.valueProperty().addListener((obs, oldV, newV) -> {
            model.setSceneBlockModel(newV);
        });

        // ===== LAYOUT =====

        HBox row1 = new HBox(10, instruction1, optionSentenceArea);
        row1.getStyleClass().add("block-row");

        HBox row2 = new HBox(10, instruction2, jumpSceneCombo);
        row2.getStyleClass().add("block-row");

        getChildren().addAll(row1, row2);

        // ===== BLOCK SIZE =====

        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxWidth(Region.USE_PREF_SIZE);
    }

    // ============================================================
    //  MODO CATÁLOGO
    // ============================================================
    public OptionBlockView() {

        getStyleClass().add("block-catalog");

        Label optionLabel = new Label("OPTION");
        optionLabel.getStyleClass().add("block-label");
        optionLabel.setMinWidth(Region.USE_PREF_SIZE); // ← evita que desaparezca

        TextField previewText = new TextField("...");
        previewText.setDisable(true);
        previewText.getStyleClass().add("block-textfield");

        HBox.setHgrow(optionLabel, Priority.NEVER);
        previewText.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(previewText, Priority.ALWAYS);

        HBox row1 = new HBox(10, optionLabel, previewText);
        row1.getStyleClass().add("block-row");
        row1.setMaxWidth(Double.MAX_VALUE);          // ← la fila puede crecer
        HBox.setHgrow(row1, Priority.ALWAYS);        // ← la fila ocupa todo el ancho

        Label jumpLabel = new Label("JUMP SCENE");
        jumpLabel.getStyleClass().add("block-label");
        jumpLabel.setMinWidth(Region.USE_PREF_SIZE); // ← evita que desaparezca

        ComboBox<String> previewCombo = new ComboBox<>();
        previewCombo.setDisable(true);
        previewCombo.getItems().add("...");
        previewCombo.setValue("...");
        previewCombo.getStyleClass().add("block-combo");

        HBox.setHgrow(jumpLabel, Priority.NEVER);
        previewCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(previewCombo, Priority.ALWAYS);

        HBox row2 = new HBox(10, jumpLabel, previewCombo);
        row2.getStyleClass().add("block-row");
        row2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(row2, Priority.ALWAYS);

        getChildren().addAll(row1, row2);
    }

    @Override
    public BaseBlockModel createModel() {
        return new OptionBlockModel();
    }


}
