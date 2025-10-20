package com.github.jimtrung.theater.view;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
            updatedMovie.setAuthor(movieAuthorField.getText().trim());
            updatedMovie.setGenres(movieGenresField.getText().trim());
            updatedMovie.setDescription(movieDescriptionField.getText().trim());

            try {
                updatedMovie.setAgeLimit(Integer.parseInt(ageLimitField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("[WARN] - Invalid age limit input. Default to 0");
                updatedMovie.setAgeLimit(0);
            }

            try {
                updatedMovie.setDuration(Integer.parseInt(movieDurationField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("[WARN] - Invalid duration input. Default to 0");
                updatedMovie.setDuration(0);
            }

            movieService.updateMovie(uuid, updatedMovie);

            Movie movie = movieService.getMovieById(uuid);
            movieListController.updateMovie(movie);

//            movieListController.refreshData();

            System.out.println("[INFO] - Movie updated successfully: " + updatedMovie.getName());

            screenController.activate("movieList");

        } catch (Exception e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleDeleteButton () throws Exception {
        movieService.deleteMovieById(uuid);
        movieListController.refreshData();
        screenController.activate("movieList");
    }

    @FXML
    public void handleOnOpen() throws Exception {
        System.out.println("Movie id was receive: " + uuid);
        Movie movie = new Movie();
        movie = movieService.getMovieById(uuid);

        if (movie == null) {
            System.out.println("[WARN] - Movie not found with id: " + uuid);
            return;
        }

        movieNameField.setText(movie.getName());
        movieAuthorField.setText(movie.getAuthor());
        movieGenresField.setText(movie.getGenres());
        ageLimitField.setText(String.valueOf(movie.getAgeLimit()));
        movieDescriptionField.setText(movie.getDescription());
        movieDurationField.setText(String.valueOf(movie.getDuration()));

        System.out.println("[DEBUG] - Movie loaded: " + movie.getName());
    }

    @FXML
    private TextField movieNameField;

    @FXML
    private TextField movieAuthorField;

    @FXML
    private TextField movieGenresField;

    @FXML
    private TextArea movieDescriptionField;

    @FXML
    private TextField ageLimitField;

    @FXML
    private TextField movieDurationField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button backButton;


}



