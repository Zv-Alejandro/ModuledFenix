package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.client.utils.GameInstallUtils;
import org.ies.fenix.client.utils.ImageUtils;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.IPurchaseController;
import org.ies.fenix.controller.dto.purchase.LibraryGameDTO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.List;

public class LibraryController {

    private static final int MAX_COLUMNS = 4;

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private TextField searchField;

    @FXML
    private VBox leftGamesList;

    @FXML
    private GridPane libraryGrid;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;
    private final IPurchaseController purchaseApiService;

    public LibraryController(StageManager stageManager,
                             IClientController clientApiService,
                             IGameController gameApiService,
                             SessionManager sessionManager,
                             IPurchaseController purchaseApiService) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.purchaseApiService = purchaseApiService;
        this.gameApiService = gameApiService;
        this.sessionManager = sessionManager;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    @FXML
    private void initialize() {
        loadLibrary();
    }

    // ============================================================
    // LIBRARY DATA
    // ============================================================

    private void loadLibrary() {
        try {
            List<LibraryGameDTO> games = requestLibraryGames();

            clearLibraryViews();

            if (games == null || games.isEmpty()) {
                showEmptyLibraryMessage();
                return;
            }

            renderLibraryGames(games);

        } catch (Exception e) {
            e.printStackTrace();
            clearLibraryViews();
            showEmptyLibraryMessage();
        }
    }

    private List<LibraryGameDTO> requestLibraryGames() {
        Integer clientId = sessionManager.getClientId();

        ResponseEntity<List<LibraryGameDTO>> response =
                purchaseApiService.getLibraryByClientId(
                        buildHeader(),
                        clientId
                );

        return response.getBody();
    }

    private void clearLibraryViews() {
        leftGamesList.getChildren().clear();
        libraryGrid.getChildren().clear();
    }

    private void renderLibraryGames(List<LibraryGameDTO> games) {
        int col = 0;
        int row = 0;

        for (LibraryGameDTO game : games) {
            leftGamesList.getChildren().add(createLeftGameRow(game));
            libraryGrid.add(createLibraryCard(game), col, row);

            col++;

            if (col == MAX_COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    // ============================================================
    // LEFT PANEL
    // ============================================================

    private HBox createLeftGameRow(LibraryGameDTO game) {
        HBox rowBox = new HBox(16);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        rowBox.getStyleClass().add("library-left-game-row");

        ImageView icon = createLogoImageView(game);

        Hyperlink title = new Hyperlink(game.getTitle());
        title.getStyleClass().add("library-left-game-title");
        title.setOnAction(event -> openGame(game.getGameId()));

        HBox iconWrapper = new HBox(icon);
        iconWrapper.setAlignment(Pos.CENTER);
        iconWrapper.getStyleClass().add("library-left-game-icon-wrapper");

        rowBox.getChildren().addAll(iconWrapper, title);

        return rowBox;
    }

    private ImageView createLogoImageView(LibraryGameDTO game) {
        ImageView icon = new ImageView();
        icon.setFitWidth(36);
        icon.setFitHeight(36);
        icon.setPreserveRatio(true);

        loadLogoIntoImageView(game, icon);

        return icon;
    }

    private void loadLogoIntoImageView(LibraryGameDTO game, ImageView icon) {
        try {
            byte[] bytes = gameApiService.getLogo(
                    buildHeader(),
                    game.getGameId()
            ).getBody();

            ImageUtils.setAvatar(
                    bytes,
                    icon,
                    36
            );

        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // GRID CARDS
    // ============================================================

    private StackPane createLibraryCard(LibraryGameDTO game) {
        StackPane cardWrapper = new StackPane();
        cardWrapper.getStyleClass().add("library-card-click-wrapper");

        VBox card = new VBox();
        card.getStyleClass().add("library-card");

        ImageView cover = createCoverImageView(game);

        HBox coverWrapper = new HBox();
        coverWrapper.setAlignment(Pos.CENTER);
        coverWrapper.getStyleClass().add("library-cover-wrapper");
        coverWrapper.getChildren().add(cover);

        card.getChildren().add(coverWrapper);

        Button playButton = createPlayButton(game);

        configureCardHover(cardWrapper, cover, playButton);

        cardWrapper.getChildren().addAll(card, playButton);

        return cardWrapper;
    }

    private ImageView createCoverImageView(LibraryGameDTO game) {
        ImageView cover = new ImageView();
        cover.setFitWidth(170);
        cover.setFitHeight(245);
        cover.setPreserveRatio(false);

        loadVerticalCoverIntoImageView(game, cover);

        return cover;
    }

    private void loadVerticalCoverIntoImageView(LibraryGameDTO game, ImageView cover) {
        try {
            byte[] bytes = gameApiService.getVertical(
                    buildHeader(),
                    game.getGameId()
            ).getBody();

            if (bytes != null && bytes.length > 0) {
                cover.setImage(new Image(new ByteArrayInputStream(bytes)));
            }

        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // PLAY BUTTON
    // ============================================================

    private Button createPlayButton(LibraryGameDTO game) {
        Button playButton = new Button("  PLAY");
        playButton.setGraphic(new FontIcon(MaterialDesignP.PLAY));
        playButton.setStyle(getPlayButtonStyle());
        playButton.setPrefWidth(160);
        playButton.setPrefHeight(40);
        playButton.setVisible(false);

        StackPane.setAlignment(playButton, Pos.CENTER);

        playButton.setOnMousePressed(event -> playButton.setStyle(getPressedPlayButtonStyle()));
        playButton.setOnMouseReleased(event -> playButton.setStyle(getPlayButtonStyle()));

        playButton.setOnAction(event -> launchGame(game.getGameId()));

        return playButton;
    }

    private String getPlayButtonStyle() {
        return """
                -fx-background-color: #2ecc71;
                -fx-text-fill: white;
                -fx-font-size: 18px;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """;
    }

    private String getPressedPlayButtonStyle() {
        return """
                -fx-background-color: #27ae60;
                -fx-text-fill: white;
                -fx-font-size: 18px;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """;
    }

    private void configureCardHover(StackPane cardWrapper,
                                    ImageView cover,
                                    Button playButton) {
        GaussianBlur blur = new GaussianBlur(0);

        cardWrapper.setOnMouseEntered(event -> {
            blur.setRadius(12);
            cover.setEffect(blur);
            playButton.setVisible(true);
        });

        cardWrapper.setOnMouseExited(event -> {
            blur.setRadius(0);
            cover.setEffect(blur);
            playButton.setVisible(false);
        });
    }

    // ============================================================
    // EMPTY STATE
    // ============================================================

    private void showEmptyLibraryMessage() {
        Label leftMessage = new Label("You don't have any games yet.");
        leftMessage.setStyle("""
                -fx-font-size: 16px;
                -fx-text-fill: #777777;
                -fx-font-weight: bold;
                -fx-padding: 20 0 20 0;
                """);

        leftGamesList.getChildren().add(leftMessage);

        Label gridMessage = new Label("Your library is empty.");
        gridMessage.setStyle("""
                -fx-font-size: 22px;
                -fx-text-fill: #777777;
                -fx-font-weight: bold;
                -fx-padding: 20 0 0 0;
                """);

        libraryGrid.add(gridMessage, 0, 0);
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
    void switchToUploadGameScene() {
        stageManager.switchScene(FxmlView.UPLOAD_GAME);
    }

    @FXML
    public void reloadView() {
        stageManager.reloadCurrentScene();
    }

    private void openGame(Integer gameId) {
        stageManager.openGame(gameId);
    }

    // ============================================================
    // GAME LAUNCH
    // ============================================================

    private void launchGame(Integer gameId) {
        if (gameId == null) {
            showError("No game selected", "Please select a game to play.");
            return;
        }

        try {
            GameInstallUtils.launchGame(gameId);

        } catch (Exception e) {
            showError(
                    "Game not installed",
                    "Download this game from its game page before trying to play it."
            );
        }
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