package org.ies.fenix.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Button theoryButton;

    @FXML
    private Button generatorButton;

    @FXML
    private Button scalesButton;

    @FXML
    private Button chordsButton;

    @FXML
    private Button intervalsButton;

    @FXML
    private Button dictationsButton;

    @FXML
    private Label helloLabel;

    @FXML
    private ImageView gClef;

    private final StageManager stageManager;

    StringProperty nameProperty = new SimpleStringProperty();

    public HomeController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gClef.setFitWidth(1000);
        gClef.setPreserveRatio(true);
        gClef.setSmooth(true);
        helloLabel.textProperty().bind(nameProperty);
    }

    @FXML
    void switchToTheoryScene() {
        stageManager.switchToNextScene(FxmlView.SCALES_THEORY);
    }

    @FXML
    void switchToEmailScene() {
        stageManager.switchToNextScene(FxmlView.EMAIL);
    }

    @FXML
    void switchToChordsScene() {
    }

    @FXML
    public void switchToChordGeneratorScene() {
    }

    @FXML
    public void switchToIntervalsScene(ActionEvent actionEvent) {
        stageManager.switchToNextScene(FxmlView.MARKETPLACE);
    }

    @FXML
    public void switchToDictationsScene(ActionEvent actionEvent) {
    }
}
