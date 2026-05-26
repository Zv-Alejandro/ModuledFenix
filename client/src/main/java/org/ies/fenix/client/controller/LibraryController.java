package org.ies.fenix.client.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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

import java.util.List;

/**
 * Controller for the user's game library.
 *
 * <p>The library shows the games acquired by the current user in two ways:
 * a compact list on the left and a visual grid on the right.</p>
 *
 * <p>Installed games can be launched directly from the grid. Non-installed games
 * remain visible but their play button is disabled.</p>
 */
public class LibraryController {

    private static final int MAX_COLUMNS = 4;

    private static final double LEFT_LOGO_SIZE = 36.0;
    private static final double COVER_WIDTH = 170.0;
    private static final double COVER_HEIGHT = 245.0;

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private TextField searchField;

    @FXML
    private VBox leftGamesList;

    @FXML
    private GridPane libraryGrid;

    @FXML
    private ScrollPane rightLibraryScroll;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;
    private final IPurchaseController purchaseApiService;

    /**
     * Creates the library controller.
     *
     * @param stageManager       application scene manager
     * @param clientApiService   client API service
     * @param gameApiService     game API service
     * @param sessionManager     current user session manager
     * @param purchaseApiService purchase API service
     */
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

    /**
     * Configures the library layout and loads the current user's games.
     */
    @FXML
    private void initialize() {
        configureRightLibraryArea();
        loadLibrary();
    }

    /**
     * Binds the grid width to the visible viewport of the scroll pane.
     *
     * <p>This keeps the grid aligned with the available right-side space.</p>
     */
    private void configureRightLibraryArea() {
        libraryGrid.prefWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> rightLibraryScroll.getViewportBounds().getWidth(),
                        rightLibraryScroll.viewportBoundsProperty()
                )
        );
    }

    // ============================================================
    // LIBRARY DATA
    // ============================================================

    /**
     * Loads and renders the games acquired by the current user.
     */
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
        libraryGrid.setAlignment(Pos.TOP_LEFT);
    }

    private void renderLibraryGames(List<LibraryGameDTO> games) {
        libraryGrid.setAlignment(Pos.TOP_LEFT);

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
        icon.setFitWidth(LEFT_LOGO_SIZE);
        icon.setFitHeight(LEFT_LOGO_SIZE);
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
                    LEFT_LOGO_SIZE
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
        cover.setFitWidth(COVER_WIDTH);
        cover.setFitHeight(COVER_HEIGHT);
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

            ImageUtils.setCoverImage(
                    bytes,
                    cover,
                    COVER_WIDTH,
                    COVER_HEIGHT
            );

        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // PLAY BUTTON
    // ============================================================

    private Button createPlayButton(LibraryGameDTO game) {
        Button playButton = new Button("  PLAY");
        boolean canPlay = GameInstallUtils.canLaunchGame(game.getGameId());

        playButton.setGraphic(new FontIcon(MaterialDesignP.PLAY));
        playButton.setStyle(canPlay ? getPlayButtonStyle() : getDisabledPlayButtonStyle());
        playButton.setDisable(!canPlay);
        playButton.setPrefWidth(160);
        playButton.setPrefHeight(40);
        playButton.setVisible(false);

        StackPane.setAlignment(playButton, Pos.CENTER);

        playButton.setOnMousePressed(event -> {
            if (!playButton.isDisabled()) {
                playButton.setStyle(getPressedPlayButtonStyle());
            }
        });

        playButton.setOnMouseReleased(event -> {
            if (!playButton.isDisabled()) {
                playButton.setStyle(getPlayButtonStyle());
            }
        });

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

    private String getDisabledPlayButtonStyle() {
        return """
                -fx-background-color: #6F6A67;
                -fx-text-fill: #CFC7C0;
                -fx-font-size: 18px;
                -fx-background-radius: 8;
                -fx-cursor: default;
                -fx-opacity: 1.0;
                """;
    }

    /**
     * Shows the play button and blurs the cover while the user hovers the card.
     */
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
        leftGamesList.getChildren().add(createLeftEmptyMessage());

        StackPane emptyState = createEmptyLibraryState();

        libraryGrid.setAlignment(Pos.CENTER);
        libraryGrid.add(emptyState, 0, 0);

        GridPane.setColumnSpan(emptyState, MAX_COLUMNS);
        GridPane.setHgrow(emptyState, Priority.ALWAYS);
        GridPane.setVgrow(emptyState, Priority.ALWAYS);
    }

    private Label createLeftEmptyMessage() {
        Label leftMessage = new Label("No games acquired yet");
        leftMessage.getStyleClass().add("library-left-empty-message");
        return leftMessage;
    }

    private StackPane createEmptyLibraryState() {
        StackPane emptyState = new StackPane();

        emptyState.setMinHeight(520.0);
        emptyState.setMaxWidth(Double.MAX_VALUE);
        emptyState.getStyleClass().add("library-empty-state");

        Circle circlePrimary = createEmptyCircle(
                160,
                -230,
                -105,
                "library-empty-circle-primary"
        );

        Circle circleSecondary = createEmptyCircle(
                120,
                240,
                -45,
                "library-empty-circle-secondary"
        );

        Circle circleAccent = createEmptyCircle(
                95,
                -40,
                150,
                "library-empty-circle-accent"
        );

        VBox content = createEmptyLibraryContent();

        emptyState.getChildren().addAll(
                circlePrimary,
                circleSecondary,
                circleAccent,
                content
        );

        StackPane.setAlignment(content, Pos.CENTER);

        return emptyState;
    }

    private Circle createEmptyCircle(double radius,
                                     double translateX,
                                     double translateY,
                                     String styleClass) {
        Circle circle = new Circle(radius);
        circle.setTranslateX(translateX);
        circle.setTranslateY(translateY);
        circle.setMouseTransparent(true);
        circle.getStyleClass().add(styleClass);

        return circle;
    }

    private VBox createEmptyLibraryContent() {
        FontIcon icon = new FontIcon("mdi2b-book-open-page-variant");
        icon.setIconSize(58);
        icon.getStyleClass().add("library-empty-icon");

        Label title = new Label("Your library is empty");
        title.getStyleClass().add("library-empty-title");

        Label subtitle = new Label("Acquire a game from the marketplace and it will appear here.");
        subtitle.setWrapText(true);
        subtitle.getStyleClass().add("library-empty-subtitle");

        Button exploreButton = new Button("Explore marketplace");
        exploreButton.getStyleClass().add("library-empty-button");
        exploreButton.setOnAction(event -> switchToMarketplaceScene());

        VBox content = new VBox(16, icon, title, subtitle, exploreButton);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(440);
        content.getStyleClass().add("library-empty-content");

        return content;
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    private void switchProfileScene() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    @FXML
    private void switchToMarketplaceScene() {
        stageManager.switchScene(FxmlView.MARKETPLACE);
    }

    @FXML
    private void switchToUploadGameScene() {
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

        if (!GameInstallUtils.canLaunchGame(gameId)) {
            showError(
                    "Game not installed",
                    "Download this game from its game page before trying to play it."
            );
            return;
        }

        try {
            GameInstallUtils.launchGame(gameId);

        } catch (Exception e) {
            showError(
                    "Game not installed",
                    "The game executable could not be found. Try downloading it again."
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