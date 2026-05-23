package org.ies.fenix.client.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.ies.fenix.controller.dto.purchase.PurchaseCreateDTO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.ies.fenix.client.utils.ImageUtils.initialConfig;

public class GameController {

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    public FontIcon topProfileIcon;

    @FXML
    public ImageView topProfileImage;

    @FXML
    public Label selectedGameDeveloper;

    @FXML
    public Label selectedGameTitle;

    @FXML
    public Label selectedGameTitle2;

    @FXML
    public Label selectedGameDescription1;

    @FXML
    public Label selectedGameDescription2;

    @FXML
    public Label selectedGameDescription3;

    @FXML
    public Label selectedGameMainQuote;

    @FXML
    public ImageView selectedGameBannerImage;

    @FXML
    public StackPane bannerWrapper;

    @FXML
    public VBox tagContainerFather;

    @FXML
    private Hyperlink username;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;
    private final RestClient restClient;
    private final IPurchaseController purchaseApiService;

    private Integer selectedGameId;

    public GameController(StageManager stageManager,
                          IClientController clientApiService,
                          IGameController gameApiService,
                          SessionManager sessionManager,
                          RestClient restClient,
                          IPurchaseController purchaseApiService) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.gameApiService = gameApiService;
        this.sessionManager = sessionManager;
        this.restClient = restClient;
        this.purchaseApiService = purchaseApiService;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    @FXML
    private void initialize() {
        initialConfig(clientApiService, sessionManager, username, topProfileImage, topProfileIcon);

        configureBannerImage();
        configureBannerClip();
    }

    private void configureBannerImage() {
        selectedGameBannerImage.setPreserveRatio(false);
        selectedGameBannerImage.setSmooth(true);
        selectedGameBannerImage.setCache(false);
        selectedGameBannerImage.setViewport(null);

        selectedGameBannerImage.fitWidthProperty().bind(bannerWrapper.widthProperty());
        selectedGameBannerImage.fitHeightProperty().bind(bannerWrapper.heightProperty());
    }

    private void configureBannerClip() {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(bannerWrapper.widthProperty());
        clip.heightProperty().bind(bannerWrapper.heightProperty());

        bannerWrapper.setClip(clip);
    }

    // ============================================================
    // SELECTED GAME
    // ============================================================

    public void setSelectedGameId(Integer selectedGameId) {
        this.selectedGameId = selectedGameId;
        loadSelectedGame();
    }

    private void loadSelectedGame() {
        if (selectedGameId == null) {
            return;
        }

        try {
            ResponseEntity<GameResponseDTO> response =
                    gameApiService.getById(buildHeader(), selectedGameId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return;
            }

            renderSelectedGame(response.getBody());
            loadHorizontalTwoIntoBanner(selectedGameId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderSelectedGame(GameResponseDTO game) {
        String title = game.getTitle() != null ? game.getTitle() : "Untitled";
        String developer = game.getDevUsername() != null ? game.getDevUsername() : "Unknown";
        String description = game.getDescription() != null
                ? game.getDescription()
                : "No description available.";

        selectedGameTitle.setText(title);
        selectedGameTitle2.setText("Title: " + title);
        selectedGameDeveloper.setText("Developer: " + developer);

        selectedGameDescription1.setText(description);
        selectedGameDescription2.setText("");
        selectedGameDescription3.setText("");

        selectedGameMainQuote.setText(title);

        renderTags(game.getTags());
    }

    // ============================================================
    // TAGS
    // ============================================================

    private void renderTags(List<String> tags) {
        tagContainerFather.getChildren().clear();

        if (tags == null || tags.isEmpty()) {
            return;
        }

        HBox firstRow = createTagRow();
        HBox secondRow = createTagRow();

        List<String> visibleTags = tags.stream()
                .limit(6)
                .toList();

        for (int i = 0; i < visibleTags.size(); i++) {
            Label tagLabel = createTagLabel(visibleTags.get(i));

            if (i < 3) {
                firstRow.getChildren().add(tagLabel);
            } else {
                secondRow.getChildren().add(tagLabel);
            }
        }

        tagContainerFather.getChildren().add(firstRow);

        if (!secondRow.getChildren().isEmpty()) {
            tagContainerFather.getChildren().add(secondRow);
        }
    }

    private HBox createTagRow() {
        HBox row = new HBox(10.0);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label createTagLabel(String text) {
        Label tagLabel = new Label(text);
        tagLabel.getStyleClass().add("tag");
        return tagLabel;
    }

    // ============================================================
    // BANNER
    // ============================================================

    private void loadHorizontalTwoIntoBanner(Integer gameId) {
        if (gameId == null || selectedGameBannerImage == null) {
            return;
        }

        try {
            ResponseEntity<byte[]> response = gameApiService.getHorizontal2(
                    buildHeader(),
                    gameId
            );

            if (!hasValidImageBody(response)) {
                return;
            }

            Image image = new Image(new ByteArrayInputStream(response.getBody()));

            Platform.runLater(() -> showBannerImage(image));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasValidImageBody(ResponseEntity<byte[]> response) {
        return response.getStatusCode().is2xxSuccessful()
                && response.getBody() != null
                && response.getBody().length > 0;
    }

    private void showBannerImage(Image image) {
        selectedGameBannerImage.setViewport(null);
        selectedGameBannerImage.setPreserveRatio(false);
        selectedGameBannerImage.setImage(image);
        selectedGameBannerImage.setVisible(true);
        selectedGameBannerImage.setManaged(true);
        selectedGameBannerImage.setOpacity(1.0);
        selectedGameBannerImage.toFront();
    }

    // ============================================================
    // DOWNLOAD
    // ============================================================

    @FXML
    private void onDownload() {
        try {
            if (selectedGameId == null) {
                showError("No game selected", "Please select a game to download.");
                return;
            }

            if (!canDownloadSelectedGame()) {
                return;
            }

            Resource resource = requestGameResource();

            if (resource == null) {
                return;
            }

            File target = chooseDownloadTarget(resource);

            if (target == null) {
                hideProgress();
                return;
            }

            startDownload(resource, target);

        } catch (HttpClientErrorException e) {
            hideProgress();
            showError("Server error", e.getResponseBodyAsString());

        } catch (Exception e) {
            hideProgress();
            showError("Download failed", "Unexpected error");
        }
    }

    private boolean canDownloadSelectedGame() {
        boolean purchased = hasPurchased(selectedGameId);

        if (purchased) {
            return true;
        }

        boolean confirmed = showPurchaseConfirmation();

        if (!confirmed) {
            return false;
        }

        return performPurchase(selectedGameId);
    }

    private Resource requestGameResource() {
        BaseLayoutController base = stageManager.getBaseLayoutController();
        base.showProgress();

        ResponseEntity<Resource> response = restClient.get()
                .uri("/api/games/download/" + selectedGameId)
                .header("Authorization", buildHeader())
                .retrieve()
                .toEntity(Resource.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            base.hideProgress();
            showError("Download failed", "The server returned an error.");
            return null;
        }

        Resource resource = response.getBody();

        if (resource == null) {
            base.hideProgress();
            showError("Download failed", "Empty file received.");
            return null;
        }

        return resource;
    }

    private File chooseDownloadTarget(Resource resource) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(getDownloadFileName(resource));

        return chooser.showSaveDialog(stageManager.getPrimaryStage());
    }

    private String getDownloadFileName(Resource resource) {
        String filename = resource.getFilename();

        if (filename == null || filename.isBlank()) {
            return "game_" + selectedGameId;
        }

        return filename;
    }

    private void startDownload(Resource resource, File target) {
        BaseLayoutController base = stageManager.getBaseLayoutController();
        Task<Void> downloadTask = createDownloadTask(resource, target);

        base.getGlobalProgressBar()
                .progressProperty()
                .bind(downloadTask.progressProperty());

        downloadTask.setOnSucceeded(event -> finishDownload());
        downloadTask.setOnFailed(event -> finishDownload());
        downloadTask.setOnCancelled(event -> finishDownload());

        new Thread(downloadTask).start();
    }

    private Task<Void> createDownloadTask(Resource resource, File target) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                long fileSize = resource.contentLength();

                try (InputStream in = resource.getInputStream();
                     OutputStream out = new FileOutputStream(target)) {

                    byte[] buffer = new byte[8192];
                    long totalRead = 0;
                    int read;

                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        totalRead += read;

                        updateProgress(totalRead, fileSize);
                    }
                }

                return null;
            }
        };
    }

    private void finishDownload() {
        BaseLayoutController base = stageManager.getBaseLayoutController();

        base.getGlobalProgressBar().progressProperty().unbind();
        base.hideProgress();
    }

    private void hideProgress() {
        stageManager.getBaseLayoutController().hideProgress();
    }

    // ============================================================
    // PURCHASE
    // ============================================================

    private boolean hasPurchased(Integer gameId) {
        Integer clientId = sessionManager.getClientId();

        try {
            ResponseEntity<Boolean> response =
                    purchaseApiService.hasPurchased(
                            buildHeader(),
                            clientId,
                            gameId
                    );

            Boolean purchased = response.getBody();
            return purchased != null && purchased;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean showPurchaseConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm purchase");
        alert.setHeaderText("You don't own this game");
        alert.setContentText("Do you want to acquire this game?");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yes, no);

        return alert.showAndWait().orElse(no) == yes;
    }

    private boolean performPurchase(Integer gameId) {
        Integer clientId = sessionManager.getClientId();

        try {
            PurchaseCreateDTO dto = new PurchaseCreateDTO();
            dto.setClientId(clientId);
            dto.setGameId(gameId);

            ResponseEntity<?> response =
                    purchaseApiService.createPurchase(
                            buildHeader(),
                            dto
                    );

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            showError("Purchase failed", "Could not complete the purchase.");
            return false;
        }
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    void switchProfileScene() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    @FXML
    void switchToMarketplaceScene() {
        stageManager.switchScene(FxmlView.MARKETPLACE);
    }

    @FXML
    void switchToLibraryScene() {
        stageManager.switchScene(FxmlView.LIBRARY);
    }

    @FXML
    void switchToUploadGameScene() {
        stageManager.switchScene(FxmlView.UPLOAD_GAME);
    }

    @FXML
    public void reloadView() {
        stageManager.reloadCurrentScene();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String buildHeader() {
        return sessionManager.getAuthorizationHeader();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}