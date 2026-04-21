package org.ies.fenix.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;


import java.net.URL;
import java.util.ResourceBundle;

public class EmailFormController implements Initializable {

    @FXML
    private Text title;

    @FXML
    private Text subtitle;

    @FXML
    private VBox center;

    @FXML
    private HBox upper;

    @FXML
    private BorderPane root;

    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView settingsImage;

    @FXML
    private Button continueButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField email;

    private final StageManager stageManager;

    StringProperty nameProperty = new SimpleStringProperty();

    public EmailFormController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoImage.setFitWidth(294.0);
        logoImage.setSmooth(true);
        settingsImage.setFitWidth(15.0);
        settingsImage.setSmooth(true);
    }

    @FXML
    void switchToUserCreateView(){ stageManager.switchToNextScene(FxmlView.USER_CREATE);}

    @FXML
    void switchLogInView(){ stageManager.switchToNextScene(FxmlView.LOGIN);}



}
