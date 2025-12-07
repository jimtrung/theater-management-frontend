package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie; 
import com.github.jimtrung.theater.model.MovieLanguage;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class MovieInformationController {

    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private MovieService movieService;
    private MovieListController movieListController;
    private UUID uuid;

    public void setMovieListController(MovieListController movieListController) {
        this.movieListController = movieListController;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    // ===== BUTTON HANDLERS =====
    @FXML
    public void handleBackButton() {
        movieListController.getMovieTable().getSelectionModel().clearSelection();
        screenController.removeScreen("movieInformation");
        screenController.activate("movieList");
    }

    @FXML
    public void handleEditButton() {
        try {
            Movie updatedMovie = new Movie();
            updatedMovie.setName(movieNameField.getText().trim());
            updatedMovie.setDescription(movieDescriptionField.getText().trim());
            updatedMovie.setLanguage(MovieLanguage.valueOf(movieLanguageField.getText().trim()));
            updatedMovie.setDirector(movieDirectorField.getText().trim());

            // Parse rated (int)
            try {
                updatedMovie.setRated(Integer.parseInt(movieRatedField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("[WARN] - Invalid rated input. Default to 0");
                updatedMovie.setRated(0);
            }

            // Parse duration (int)
            try {
                updatedMovie.setDuration(Integer.parseInt(movieDurationField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("[WARN] - Invalid duration input. Default to 0");
                updatedMovie.setDuration(0);
            }

            // Parse premiere date (ISO format: 2025-11-03T10:00:00Z)
            try {
                updatedMovie.setPremiere(OffsetDateTime.parse(moviePremiereField.getText().trim()));
            } catch (DateTimeParseException e) {
                System.out.println("[WARN] - Invalid premiere format.");
                updatedMovie.setPremiere(null);
            }

            // Genres (comma-separated string)
            String genresText = movieGenresField.getText().trim();
            if (!genresText.isEmpty()) {
                updatedMovie.setGenres(List.of(genresText.split(",\\s*")));
            }
            
            // Actors (comma-separated string)
            String actorsText = movieActorsField.getText().trim();
            if (!actorsText.isEmpty()) {
                updatedMovie.setActors(List.of(actorsText.split(",\\s*")));
            } else {
                updatedMovie.setActors(List.of());
            }

            movieService.updateMovie(uuid, updatedMovie);

            Movie movie = movieService.getMovieById(uuid);
            movieListController.updateMovie(movie);

            System.out.println("[INFO] - Movie updated successfully: " + updatedMovie.getName());
            screenController.activate("movieList");

        } catch (Exception e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteButton() throws Exception {
        movieService.deleteMovieById(uuid);
        movieListController.refreshData();
        screenController.activate("movieList");
    }

    @FXML
    public void handleOnOpen() throws Exception {
        System.out.println("Movie id received: " + uuid);
        Movie movie = movieService.getMovieById(uuid);

        if (movie == null) {
            System.out.println("[WARN] - Movie not found with id: " + uuid);
            return;
        }

        movieNameField.setText(movie.getName());
        movieDescriptionField.setText(movie.getDescription());
        movieLanguageField.setText(movie.getLanguage().name());
        movieDurationField.setText(String.valueOf(movie.getDuration()));
        movieRatedField.setText(String.valueOf(movie.getRated()));
        movieDirectorField.setText(movie.getDirector() != null ? movie.getDirector() : "");
        moviePremiereField.setText(movie.getPremiere() != null ? movie.getPremiere().toString() : "");
        movieGenresField.setText(movie.getGenres() != null ? String.join(", ", movie.getGenres()) : "");
        movieActorsField.setText(movie.getActors() != null ? String.join(", ", movie.getActors()) : "");

        System.out.println("[DEBUG] - Movie loaded: " + movie.getName());
    }

    // ====== FXML BINDINGS ======
    @FXML private TextField movieNameField;
    @FXML private TextArea movieDescriptionField;
    @FXML private TextField movieDirectorField;
    @FXML private TextField movieActorsField;
    @FXML private TextField movieGenresField;
    @FXML private TextField moviePremiereField;
    @FXML private TextField movieDurationField;
    @FXML private TextField movieLanguageField;
    @FXML private TextField movieRatedField;

    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private Button backButton;
}
