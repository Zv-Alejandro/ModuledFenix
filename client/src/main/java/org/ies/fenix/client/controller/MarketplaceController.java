package org.ies.fenix.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.ies.fenix.client.api.SessionManager;
import org.ies.fenix.client.config.FxmlView;
import org.ies.fenix.client.config.StageManager;
import org.ies.fenix.controller.IClientController;
import org.ies.fenix.controller.IGameController;
import org.ies.fenix.controller.dto.game.GameResponseDTO;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static org.ies.fenix.client.utils.ImageUtils.setCoverImage;

/**
 * Controller for the marketplace screen.
 *
 * <p>The marketplace loads all published games, renders the latest releases,
 * builds a discovery grid and filters the visible games using the search field.</p>
 */
public class MarketplaceController implements Initializable {

    private static final int LATEST_RELEASED_LIMIT = 10;
    private static final int MAX_VISIBLE_TAGS = 6;
    private static final int RECOMMENDATION_ROWS = 3;
    private static final int DISCOVER_GAMES_LIMIT = 21;
    private static final int TAG_COLUMNS = 3;

    private static final double MARKETPLACE_CARD_WIDTH = 320.0;
    private static final double MARKETPLACE_IMAGE_WIDTH = 320.0;
    private static final double MARKETPLACE_IMAGE_HEIGHT = 170.0;
    private static final double MARKETPLACE_IMAGE_CORNER_RADIUS = 12.0;

    private static final double MARKETPLACE_TAG_WIDTH = 98.0;
    private static final double MARKETPLACE_TAG_HEIGHT = 24.0;
    private static final double MARKETPLACE_TAGS_GRID_WIDTH = 320.0;

    // ============================================================
    // FXML FIELDS
    // ============================================================

    @FXML
    private TextField searchField;

    @FXML
    private Label marketplaceHeaderTitle;

    @FXML
    private Label marketplaceHeaderSubtitle;

    @FXML
    private VBox latestReleasedSection;

    @FXML
    private HBox latestReleasedContainer;

    @FXML
    private VBox exploreGamesHeader;

    @FXML
    private GridPane recommendationsContainer;

    // ============================================================
    // DEPENDENCIES
    // ============================================================

    private final StageManager stageManager;
    private final IClientController clientApiService;
    private final IGameController gameApiService;
    private final SessionManager sessionManager;

    // ============================================================
    // STATE
    // ============================================================

    private List<GameResponseDTO> loadedGames = new ArrayList<>();

    public MarketplaceController(StageManager stageManager,
                                 IClientController clientApiService,
                                 IGameController gameApiService,
                                 SessionManager sessionManager) {
        this.stageManager = stageManager;
        this.clientApiService = clientApiService;
        this.gameApiService = gameApiService;
        this.sessionManager = sessionManager;
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMarketplaceGames();
        configureSearch();
    }

    // ============================================================
    // MARKETPLACE DATA
    // ============================================================

    /**
     * Loads all games from the backend and renders the marketplace sections.
     */
    private void loadMarketplaceGames() {
        try {
            ResponseEntity<List<GameResponseDTO>> response =
                    gameApiService.getAllGames(buildHeader());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                clearCarousels();
                return;
            }

            loadedGames = response.getBody();

            renderLatestReleased(loadedGames);
            renderRecommendations(loadedGames);

        } catch (Exception e) {
            e.printStackTrace();
            clearCarousels();
        }
    }

    private void clearCarousels() {
        if (latestReleasedContainer != null) {
            latestReleasedContainer.getChildren().clear();
        }

        if (recommendationsContainer != null) {
            recommendationsContainer.getChildren().clear();
        }
    }

    // ============================================================
    // SEARCH
    // ============================================================

    /**
     * Re-renders marketplace sections whenever the user changes the search text.
     *
     * <p>When the search field is empty, the marketplace shows the normal layout:
     * latest released carousel and explore games. When the user is searching,
     * the latest carousel and the explore header are hidden and unmanaged, so the
     * results grid moves up without leaving blank space.</p>
     */
    private void configureSearch() {
        if (searchField == null) {
            return;
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean searching = newValue != null && !newValue.trim().isBlank();

            setSearchMode(searching);

            if (searching) {
                List<GameResponseDTO> filteredGames = filterGamesByTitle(newValue);
                renderRecommendations(filteredGames);
                return;
            }

            renderLatestReleased(loadedGames);
            renderRecommendations(loadedGames);
        });
    }

    /**
     * Switches the marketplace between normal mode and search mode.
     *
     * @param searching whether the user is currently searching
     */
    private void setSearchMode(boolean searching) {
        if (latestReleasedSection != null) {
            latestReleasedSection.setVisible(!searching);
            latestReleasedSection.setManaged(!searching);
        }

        if (exploreGamesHeader != null) {
            exploreGamesHeader.setVisible(!searching);
            exploreGamesHeader.setManaged(!searching);
        }

        if (marketplaceHeaderTitle != null) {
            marketplaceHeaderTitle.setText(searching ? "Search results" : "Latest released");
        }

        if (marketplaceHeaderSubtitle != null) {
            marketplaceHeaderSubtitle.setText(
                    searching
                            ? "Games matching your search"
                            : "New histories to play!"
            );
        }
    }

    private List<GameResponseDTO> filterGamesByTitle(String searchText) {
        String normalizedSearchText = searchText == null
                ? ""
                : searchText.trim().toLowerCase();

        if (normalizedSearchText.isBlank()) {
            return loadedGames;
        }

        return loadedGames.stream()
                .filter(game -> game.getTitle() != null
                        && game.getTitle().toLowerCase().contains(normalizedSearchText))
                .toList();
    }

    // ============================================================
    // LATEST RELEASED
    // ============================================================

    private void renderLatestReleased(List<GameResponseDTO> games) {
        latestReleasedContainer.getChildren().clear();

        if (games == null || games.isEmpty()) {
            latestReleasedContainer.getChildren().add(
                    createEmptyGamesMessage("There are no games yet.")
            );
            return;
        }

        games.stream()
                .sorted((game1, game2) -> Integer.compare(game2.getId(), game1.getId()))
                .limit(LATEST_RELEASED_LIMIT)
                .forEach(game -> latestReleasedContainer.getChildren().add(createGameCard(game)));
    }

    // ============================================================
    // EXPLORE GAMES
    // ============================================================

    private void renderRecommendations(List<GameResponseDTO> games) {
        recommendationsContainer.getChildren().clear();

        if (games == null || games.isEmpty()) {
            recommendationsContainer.add(
                    createEmptyGamesMessage("There are no games to discover yet."),
                    0,
                    0
            );
            return;
        }

        List<GameResponseDTO> visibleGames = getVisibleRecommendationGames(games);

        int index = 0;

        for (GameResponseDTO game : visibleGames) {
            int row = index % RECOMMENDATION_ROWS;
            int col = index / RECOMMENDATION_ROWS;

            recommendationsContainer.add(createGameCard(game), col, row);

            index++;
        }
    }

    /**
     * Returns the games that should be displayed in the explore grid.
     *
     * <p>In normal mode, the list is shuffled so the explore section feels more
     * dynamic. In search mode, the filtered result order is preserved so the
     * results feel stable while typing.</p>
     *
     * @param games source games
     * @return visible games for the recommendations grid
     */
    private List<GameResponseDTO> getVisibleRecommendationGames(List<GameResponseDTO> games) {
        boolean searching = searchField != null
                && searchField.getText() != null
                && !searchField.getText().trim().isBlank();

        List<GameResponseDTO> visibleGames = new ArrayList<>(games);

        if (!searching) {
            Collections.shuffle(visibleGames);
        }

        return visibleGames.stream()
                .limit(DISCOVER_GAMES_LIMIT)
                .toList();
    }

    // ============================================================
    // GAME CARD
    // ============================================================

    private StackPane createGameCard(GameResponseDTO game) {
        StackPane wrapper = new StackPane();
        wrapper.getStyleClass().add("card-click-wrapper");

        VBox card = new VBox();
        card.getStyleClass().add("card");

        HBox imageWrapper = createImageWrapper(game);
        VBox infoBox = createInfoRow(game);

        card.getChildren().addAll(imageWrapper, infoBox);

        wrapper.getChildren().add(card);
        wrapper.setOnMouseClicked(event -> openGame(game));
        wrapper.setStyle("-fx-cursor: hand;");

        return wrapper;
    }

    private HBox createImageWrapper(GameResponseDTO game) {
        HBox imageWrapper = new HBox();
        imageWrapper.setAlignment(Pos.CENTER);
        imageWrapper.setPrefHeight(MARKETPLACE_IMAGE_HEIGHT);
        imageWrapper.setPrefWidth(MARKETPLACE_CARD_WIDTH);
        imageWrapper.getStyleClass().add("card-image-wrapper");

        Rectangle clip = new Rectangle(MARKETPLACE_CARD_WIDTH, MARKETPLACE_IMAGE_HEIGHT);
        clip.setArcWidth(MARKETPLACE_IMAGE_CORNER_RADIUS * 2);
        clip.setArcHeight(MARKETPLACE_IMAGE_CORNER_RADIUS * 2);
        imageWrapper.setClip(clip);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(MARKETPLACE_IMAGE_HEIGHT);
        imageView.setFitWidth(MARKETPLACE_IMAGE_WIDTH);
        imageView.setPreserveRatio(false);
        imageView.getStyleClass().add("card-image");

        loadHorizontalOneIntoImageView(game, imageView);

        imageWrapper.getChildren().add(imageView);

        return imageWrapper;
    }

    private VBox createInfoRow(GameResponseDTO game) {
        Label titleLabel = new Label(getSafeText(game.getTitle(), "Untitled"));
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(MARKETPLACE_CARD_WIDTH);

        GridPane tagsGrid = createMarketplaceTagsGrid(game.getTags());

        VBox infoBox = new VBox(8.0);
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setPrefWidth(MARKETPLACE_CARD_WIDTH);
        infoBox.setMaxWidth(MARKETPLACE_CARD_WIDTH);

        infoBox.getChildren().addAll(titleLabel, tagsGrid);

        return infoBox;
    }

    // ============================================================
    // TAGS
    // ============================================================

    private GridPane createMarketplaceTagsGrid(List<String> tags) {
        GridPane tagsGrid = new GridPane();
        tagsGrid.setHgap(6.0);
        tagsGrid.setVgap(6.0);
        tagsGrid.setAlignment(Pos.TOP_LEFT);
        tagsGrid.setPrefWidth(MARKETPLACE_TAGS_GRID_WIDTH);
        tagsGrid.setMaxWidth(MARKETPLACE_TAGS_GRID_WIDTH);

        if (tags == null || tags.isEmpty()) {
            return tagsGrid;
        }

        List<String> visibleTags = tags.stream()
                .limit(MAX_VISIBLE_TAGS)
                .toList();

        for (int i = 0; i < visibleTags.size(); i++) {
            Label tagLabel = createTagLabel(visibleTags.get(i));

            int col = i % TAG_COLUMNS;
            int row = i / TAG_COLUMNS;

            tagsGrid.add(tagLabel, col, row);
        }

        return tagsGrid;
    }

    private Label createTagLabel(String text) {
        Label tagLabel = new Label(text);
        tagLabel.getStyleClass().addAll("tag", "marketplace-tag");

        tagLabel.setMinWidth(MARKETPLACE_TAG_WIDTH);
        tagLabel.setPrefWidth(MARKETPLACE_TAG_WIDTH);
        tagLabel.setMaxWidth(MARKETPLACE_TAG_WIDTH);

        tagLabel.setMinHeight(MARKETPLACE_TAG_HEIGHT);
        tagLabel.setPrefHeight(MARKETPLACE_TAG_HEIGHT);
        tagLabel.setMaxHeight(MARKETPLACE_TAG_HEIGHT);

        tagLabel.setAlignment(Pos.CENTER);
        tagLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        tagLabel.setWrapText(false);

        return tagLabel;
    }

    // ============================================================
    // IMAGES
    // ============================================================

    private void loadHorizontalOneIntoImageView(GameResponseDTO game, ImageView imageView) {
        if (game == null || game.getId() == null) {
            return;
        }

        try {
            ResponseEntity<byte[]> response = gameApiService.getHorizontal1(
                    buildHeader(),
                    game.getId()
            );

            if (!hasValidImageBody(response)) {
                return;
            }

            setCoverImage(
                    response.getBody(),
                    imageView,
                    MARKETPLACE_IMAGE_WIDTH,
                    MARKETPLACE_IMAGE_HEIGHT
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasValidImageBody(ResponseEntity<byte[]> response) {
        return response.getStatusCode().is2xxSuccessful()
                && response.getBody() != null
                && response.getBody().length > 0;
    }

    // ============================================================
    // EMPTY STATE
    // ============================================================

    private Label createEmptyGamesMessage(String text) {
        Label emptyLabel = new Label(text);
        emptyLabel.setStyle("""
                -fx-font-size: 18px;
                -fx-text-fill: #777777;
                -fx-font-weight: bold;
                -fx-padding: 20 0 20 0;
                """);
        return emptyLabel;
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    private void openGame(GameResponseDTO game) {
        if (game == null || game.getId() == null) {
            return;
        }

        stageManager.openGame(game.getId());
    }

    @FXML
    private void switchProfileScene() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    @FXML
    private void switchToLibraryScene() {
        stageManager.switchScene(FxmlView.LIBRARY);
    }

    @FXML
    private void switchToUploadGameScene() {
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

    private String getSafeText(String text, String fallback) {
        if (text == null || text.isBlank()) {
            return fallback;
        }

        return text;
    }
}