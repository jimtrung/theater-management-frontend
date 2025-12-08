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
    private TextField movieDurationField;
    @FXML
    private TextField movieLanguageField;
    @FXML
    private TextField movieRatedField;
    @FXML
    private TextArea movieDescriptionField;
    @FXML
    private TextField movieDirectorField;
    @FXML
    private TextField movieActorsField;
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
            }));

            // --- Filter genres ---
            searchGenreField.textProperty().addListener((obs, oldV, newV) -> {
                String filter = newV.toLowerCase();
                filteredGenres.setPredicate(g -> filter.isEmpty() || g.name().toLowerCase().contains(filter));
            });

            // Clear fields
            movieNameField.clear();
            movieDurationField.clear();
            movieLanguageField.clear();
            movieRatedField.clear();
            movieDescriptionField.clear();
            movieDirectorField.clear();
            movieActorsField.clear();
            searchGenreField.clear();
            genreListView.getSelectionModel().clearSelection();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load genres: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddMovieButton() {
        try {
            // --- Validate required fields ---
            if (isEmpty(movieNameField) ||
                    isEmpty(movieDurationField) ||
                    isEmpty(movieLanguageField) ||
                    isEmpty(movieRatedField) ||
                    isEmpty(movieDirectorField)) {
                showError("Please enter complete information");
                return;
            }

            Movie movie = new Movie();
            movie.setId(UUID.randomUUID());
            movie.setName(movieNameField.getText().trim());
            movie.setDescription(movieDescriptionField.getText().trim());
            movie.setLanguage(MovieLanguage.valueOf(movieLanguageField.getText().trim()));
            movie.setDirector(movieDirectorField.getText().trim());
            movie.setPremiere(OffsetDateTime.now());
            movie.setCreatedAt(OffsetDateTime.now());
            movie.setUpdatedAt(OffsetDateTime.now());

            try {
                movie.setDuration(Integer.parseInt(movieDurationField.getText().trim()));
                movie.setRated(Integer.parseInt(movieRatedField.getText().trim()));
            } catch (NumberFormatException e) {
                showError("Duration and Rated must be valid numbers");
                return;
            }

            // --- Get selected genres ---
            List<String> selectedGenres = genreListView.getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .map(Enum::name)
                    .toList();
            movie.setGenres(selectedGenres);

            // --- Get actors ---
            String actorsText = movieActorsField.getText();
            if (actorsText != null && !actorsText.trim().isEmpty()) {
                List<String> actorsList = Arrays.stream(actorsText.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                movie.setActors(actorsList);
            } else {
                movie.setActors(List.of());
            }

            // --- Insert movie ---
            movieService.insertMovie(movie);

            System.out.println("[DEBUG] Inserted movie with " + movie.getActors().size() + " actors.");

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

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }
}
