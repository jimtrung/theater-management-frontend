package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import com.github.jimtrung.theater.util.FXAnimationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomePageUserController {
    private ScreenController screenController;
    private AuthService authService;
    private MovieService movieService;
    private AuthTokenUtil authTokenUtil;

    @FXML
    private FlowPane movieList;

    @FXML
    private Label titleLabel;

    @FXML
    private Button signupButton;

    @FXML
    private Button signinButton;

    @FXML
    private Button settingsButton;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void handleOnOpen() {
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception _) {
        }

        if (user == null) screenController.activate("home");

        List<Movie> movies = null;
        try {
            movies = movieService.getAllMovies();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fetch movies");
            alert.setHeaderText(null);
            alert.setContentText("Failed to get movies data");
            alert.showAndWait();
            return;
        }

        for (Movie movie : movies) {
            VBox movieCard = createMovieCard(movie);
            movieList.getChildren().add(movieCard);
        }
    }

    @FXML
    public void handleSignUpButton(ActionEvent event) {
        screenController.activate("signup");
    }

    @FXML
    public void handleSignInButton(ActionEvent event) {
        screenController.activate("signin");
    }

    @FXML
    public void handleLogOutButton(ActionEvent event) {
        authTokenUtil.clearRefreshToken();
        authTokenUtil.clearAccessToken();
        screenController.activate("home");
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("movie-card");

        ImageView poster = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cat.jpg"))));
        poster.setFitWidth(204);
        poster.setFitHeight(230);
        poster.setPreserveRatio(false);
        poster.getStyleClass().add("poster");

        Label title = new Label(movie.getName());
        title.getStyleClass().add("movie-title");

        Label genres = new Label(movie.getGenres());
        genres.getStyleClass().add("movie-genre");

        Label ageLimit = new Label("Age: " + movie.getAgeLimit() + "+");
        ageLimit.getStyleClass().add("movie-age");

        Label duration = new Label(movie.getDuration() + " min");
        duration.getStyleClass().add("movie-duration");

        Label description = new Label(movie.getDescription());
        description.getStyleClass().add("movie-desc");
        description.setWrapText(true);
        description.setMaxWidth(200);

        card.getChildren().addAll(poster, title, genres, ageLimit, duration, description);
        return card;
    }
}
