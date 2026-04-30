package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.dto.client.ClientLoginDTO;
import org.ies.fenix.controller.dto.client.ClientNameDTO;
import org.ies.fenix.controller.dto.client.LoginResponseDTO;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.util.ResourceBundle;

public class MarketplaceController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private Hyperlink username;

    @FXML
    private Hyperlink marketplace;

    @FXML
    private Hyperlink library;

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final SessionManager sessionManager;


    public MarketplaceController(StageManager stageManager, IClientController clientApiService, SessionManager sessionManager) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.sessionManager = sessionManager;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ResponseEntity<ClientNameDTO> response = clientApiService.getUsername("Bearer " + sessionManager.getToken());

            if (response.getStatusCode().value() == 200 && response.getBody() != null) {
                username.setText(response.getBody().getUsername());
            }
        } catch (RuntimeException e) {
            e.printStackTrace(); //needs to be handled
        }
    }


}
