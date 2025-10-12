package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;

public class AddMovieController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
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

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void handleCloseButton() {
        screenController.activate("movieList");
    }

    public void handleAddMovieButtonClick() {
        Movie movie = new Movie();
        movie.setName(movieNameField.getText().toString().trim());
        movie.setAuthor(movieAuthorField.getText().toString().trim());
        movie.setGenres(movieGenresField.getText().toString().trim());
        movie.setDescription(movieDescriptionField.getText().toString().trim());

        if (isEmpty(movieDurationField) || isEmpty(ageLimitField)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter complete information");
            alert.showAndWait();
            return;
        }

        movie.setAgeLimit(Integer.valueOf(ageLimitField.getText().toString().trim()));
        movie.setDuration(Integer.valueOf(movieDurationField.getText().toString().trim()));
        try {
            if (hasNullField(movie)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter complete information");
                alert.showAndWait();
                return;
            }
            else {
                movieService.insertMovie(movie);
                movieListController.refreshData();
                screenController.activate("movieList");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasNullField(Object obj) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getName().equalsIgnoreCase("id")) {
                    continue;
                }

                Object value = field.get(obj);
                if (value == null) {
                    System.out.println("[ERROR] Field '" + field.getName() + "' is null!");
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    @FXML
    private TextField movieNameField;

    @FXML
    private TextField movieAuthorField;

    @FXML
    private TextField movieGenresField;

    @FXML
    private TextField movieDescriptionField;

    @FXML
    private TextField ageLimitField;

    @FXML
    private TextField movieDurationField;
}
