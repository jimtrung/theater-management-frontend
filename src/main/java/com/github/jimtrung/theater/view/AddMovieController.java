package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.MovieGenre;
import com.github.jimtrung.theater.model.MovieLanguage;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddMovieController {

    private ScreenController screenController;
    private AuthService authService;
    private MovieService movieService;
    private MovieListController movieListController;

    public void setMovieListController(MovieListController movieListController) {
        this.movieListController = movieListController;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private TextField movieNameField;
    @FXML
    private Spinner<Integer> movieDurationSpinner;
    @FXML
    private ComboBox<MovieLanguage> movieLanguageComboBox;
    @FXML
    private Spinner<Integer> movieRatedSpinner;
    @FXML
    private DatePicker moviePremiereDatePicker;
    @FXML
    private TextArea movieDescriptionField;
    @FXML
    private TextField movieDirectorField;
    @FXML
    private FlowPane actorsFlowPane;
    @FXML
    private TextField actorInputField;
    
    private final ObservableList<String> actorsList = FXCollections.observableArrayList();
    @FXML
    private ListView<MovieGenre> genreListView;
    @FXML
    private TextField searchGenreField;

    @FXML
    public void handleBackButton() {
        screenController.activate("movieList");
    }

    @FXML
    public void handleOnOpen() {
        try {
            User user = null;
            try {
                user = (User) authService.getUser();
            } catch (Exception ignored) { }

            if (user == null || user.getRole() != UserRole.administrator) {
                screenController.activate("home");
                return;
            }

            // --- Load genres ---
            ObservableList<MovieGenre> allGenres = FXCollections.observableArrayList(MovieGenre.values());
            FilteredList<MovieGenre> filteredGenres = new FilteredList<>(allGenres, p -> true);

            genreListView.setItems(filteredGenres);
            genreListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            genreListView.setCellFactory(CheckBoxListCell.forListView(genre -> {
                SimpleBooleanProperty selected = new SimpleBooleanProperty();
                selected.addListener((obs, oldV, newV) -> {
                    if (newV) {
                        if (!genreListView.getSelectionModel().getSelectedItems().contains(genre)) {
                            genreListView.getSelectionModel().select(genre);
                        }
                    } else {
                        genreListView.getSelectionModel().clearSelection(genreListView.getItems().indexOf(genre));
                    }
                });
                return selected;
            }, new StringConverter<MovieGenre>() {
                @Override
                public String toString(MovieGenre object) {
                    return object != null ? object.toVietnamese() : "";
                }

                @Override
                public MovieGenre fromString(String string) {
                    return MovieGenre.fromVietnamese(string);
                }
            }));

            // --- Filter genres ---
            searchGenreField.textProperty().addListener((obs, oldV, newV) -> {
                String filter = newV.toLowerCase();
                filteredGenres.setPredicate(g -> filter.isEmpty() ||
                        g.name().toLowerCase().contains(filter) ||
                        g.toVietnamese().toLowerCase().contains(filter));
            });

            // Clear fields
            movieNameField.clear();
            movieDurationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 120, 10));
            movieLanguageComboBox.getSelectionModel().clearSelection();
            movieRatedSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 18, 13));
            moviePremiereDatePicker.setValue(null);
            movieDescriptionField.clear();
            movieDirectorField.clear();
            actorsList.clear();
            actorsFlowPane.getChildren().clear();
            actorInputField.clear();
            searchGenreField.clear();
            genreListView.getSelectionModel().clearSelection();
            
            // --- Actors Input Handler ---
            actorInputField.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    event.consume(); // Prevent default action
                    String name = actorInputField.getText().trim();
                    if (!name.isEmpty() && !actorsList.contains(name)) {
                        addActorTag(name);
                        actorInputField.clear();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load genres: " + e.getMessage());
        }
        
        // Load languages
        movieLanguageComboBox.setItems(FXCollections.observableArrayList(MovieLanguage.values()));
        movieLanguageComboBox.setConverter(new StringConverter<MovieLanguage>() {
            @Override
            public String toString(MovieLanguage object) {
                return object != null ? object.toVietnamese() : "";
            }

            @Override
            public MovieLanguage fromString(String string) {
                return MovieLanguage.fromVietnamese(string);
            }
        });
    }

    @FXML
    public void handleAddMovieButton() {
        try {
            // --- Validate required fields ---
            if (isEmpty(movieNameField) ||
                    movieDurationSpinner.getValue() == 0 ||
                    movieLanguageComboBox.getValue() == null ||
                    movieDirectorField.getText().trim().isEmpty() ||
                    moviePremiereDatePicker.getValue() == null) {
                showError("Please enter complete information");
                return;
            }

            Movie movie = new Movie();
            movie.setName(movieNameField.getText().trim());
            movie.setDescription(movieDescriptionField.getText().trim());
            movie.setLanguage(movieLanguageComboBox.getValue());
            movie.setDirector(movieDirectorField.getText().trim());
            
            // Premiere
            java.time.LocalDate date = moviePremiereDatePicker.getValue();
            movie.setPremiere(date.atStartOfDay().atOffset(java.time.ZoneOffset.UTC));
            
            movie.setCreatedAt(OffsetDateTime.now());
            movie.setUpdatedAt(OffsetDateTime.now());

            movie.setDuration(movieDurationSpinner.getValue());
            movie.setRated(movieRatedSpinner.getValue());

            // --- Get selected genres ---
            List<String> selectedGenres = genreListView.getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .map(Enum::name)
                    .toList();
            movie.setGenres(selectedGenres);

            movie.setGenres(selectedGenres);

            // --- Get actors ---
            // --- Get actors ---
            if (!actorsList.isEmpty()) {
                movie.setActors(List.copyOf(actorsList));
            } else {
                movie.setActors(List.of());
            }

            // --- Insert movie ---
            movieService.insertMovie(movie);

            movieListController.refreshData();
            screenController.activate("movieList");

        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void addActorTag(String name) {
        actorsList.add(name);

        javafx.scene.layout.HBox tag = new javafx.scene.layout.HBox();
        tag.setAlignment(javafx.geometry.Pos.CENTER);
        tag.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 15; -fx-padding: 5 10;");
        tag.setSpacing(5);

        Label label = new Label(name);
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 10; -fx-padding: 0;");
        closeBtn.setCursor(javafx.scene.Cursor.HAND);
        closeBtn.setOnAction(e -> {
            actorsFlowPane.getChildren().remove(tag);
            actorsList.remove(name);
        });

        tag.getChildren().addAll(label, closeBtn);
        actorsFlowPane.getChildren().add(tag);
    }
    
    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }
}
