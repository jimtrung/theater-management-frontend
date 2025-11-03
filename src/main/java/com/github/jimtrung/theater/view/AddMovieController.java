package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Actor;
import com.github.jimtrung.theater.model.Director;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.MovieGenre;
import com.github.jimtrung.theater.service.ActorService;
import com.github.jimtrung.theater.service.DirectorService;
import com.github.jimtrung.theater.service.MovieActorService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
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
    private AuthTokenUtil authTokenUtil;
    private MovieService movieService;
    private ActorService actorService;
    private DirectorService directorService;
    private MovieListController movieListController;
    private MovieActorService movieActorService;

    public void setMovieListController(MovieListController movieListController) {
        this.movieListController = movieListController;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }

    public void setDirectorService(DirectorService directorService) {
        this.directorService = directorService;
    }

    public void setMovieActorService(MovieActorService movieActorService) {
        this.movieActorService = movieActorService;
    }

    @FXML
    private TextField movieNameField;
    @FXML
    private TextField movieGenresField;
    @FXML
    private TextField movieDurationField;
    @FXML
    private TextField movieLanguageField;
    @FXML
    private TextField movieRatedField;
    @FXML
    private TextArea movieDescriptionField;
    @FXML
    private ListView<Director> directorListView;
    @FXML
    private ListView<Actor> actorListView;
    @FXML
    private TextField searchDirectorField;
    @FXML
    private TextField searchActorField;
    @FXML
    private ListView<MovieGenre> genreListView;
    @FXML
    private TextField searchGenreField;

    public void handleCloseButton() {
        screenController.activate("movieList");
    }

    @FXML
    public void handleOnOpen() {
        try {
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

            // --- Directors ---
            List<Director> directors = (List<Director>) directorService.getAllDirectors();
            ObservableList<Director> allDirectors = FXCollections.observableArrayList(directors);
            FilteredList<Director> filteredDirectors = new FilteredList<>(allDirectors, p -> true);
            directorListView.setItems(filteredDirectors);
            directorListView.setCellFactory(CheckBoxListCell.forListView(d -> {
                SimpleBooleanProperty selected = new SimpleBooleanProperty();
                selected.addListener((obs, oldV, newV) -> {
                    if (newV) {
                        directorListView.getSelectionModel().select(d);
                    } else {
                        directorListView.getSelectionModel().clearSelection(directorListView.getItems().indexOf(d));
                    }
                });
                return selected;
            }));
            searchDirectorField.textProperty().addListener((obs, oldV, newV) -> {
                String filter = newV.toLowerCase();
                filteredDirectors.setPredicate(d ->
                        filter.isEmpty() || d.toString().toLowerCase().contains(filter)
                );
            });

            // --- Actors ---
            List<Actor> actors = (List<Actor>) actorService.getAllActors();
            ObservableList<Actor> allActors = FXCollections.observableArrayList(actors);
            FilteredList<Actor> filteredActors = new FilteredList<>(allActors, p -> true);
            actorListView.setItems(filteredActors);
            actorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            actorListView.setCellFactory(CheckBoxListCell.forListView(a -> {
                SimpleBooleanProperty selected = new SimpleBooleanProperty();
                selected.addListener((obs, oldV, newV) -> {
                    if (newV) {
                        actorListView.getSelectionModel().select(a);
                    } else {
                        actorListView.getSelectionModel().clearSelection(actorListView.getItems().indexOf(a));
                    }
                });
                return selected;
            }));
            searchActorField.textProperty().addListener((obs, oldV, newV) -> {
                String filter = newV.toLowerCase();
                filteredActors.setPredicate(a ->
                        filter.isEmpty() || a.toString().toLowerCase().contains(filter)
                );
            });

            System.out.println("[DEBUG] Loaded " + directors.size() + " directors and " + actors.size() + " actors.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load actors/directors: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddMovieButtonClick() {
        try {
            // --- Validate required fields ---
            if (isEmpty(movieNameField) ||
                    isEmpty(movieDurationField) ||
                    isEmpty(movieLanguageField) ||
                    isEmpty(movieRatedField)) {
                showError("Please enter complete information");
                return;
            }

            Movie movie = new Movie();
            movie.setId(UUID.randomUUID());
            movie.setName(movieNameField.getText().trim());
            movie.setDescription(movieDescriptionField.getText().trim());
            movie.setLanguage(movieLanguageField.getText().trim());
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

            // --- Get selected director (only 1 allowed) ---
            Director selectedDirector = null;
            List<Director> selectedDirectors = directorListView.getSelectionModel().getSelectedItems();
            if (!selectedDirectors.isEmpty()) {
                selectedDirector = selectedDirectors.get(0);
                movie.setDirectorId(selectedDirector.getId());
            }

            // --- Insert movie ---
            movieService.insertMovie(movie);

            // --- Insert selected actors ---
            List<Actor> selectedActors = actorListView.getSelectionModel().getSelectedItems();
            List<UUID> actorIds = selectedActors.stream()
                    .map(Actor::getId)
                    .toList();
            movieActorService.insertMovieActors(movie.getId(), actorIds);
            System.out.println("[DEBUG] Inserted " + selectedActors.size() + " actors.");

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
