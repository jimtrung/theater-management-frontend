package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import com.github.jimtrung.theater.model.MovieGenre;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.time.OffsetDateTime;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomePageUserController {
    @FXML
    private UserHeaderController userHeaderController;

    private ScreenController screenController;
    private AuthService authService;
    private MovieService movieService;
    private ShowtimeService showtimeService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setShowtimeService(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @FXML
    private FlowPane movieList;

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();

        if (screenController.getContext("selectedMovieId") != null) {
            screenController.setContext("selectedMovieId", null);
        }

        movieList.getChildren().clear();

        List<Movie> movies;
        try {
            movies = movieService.getAllMovies();
            List<Showtime> showtimes = showtimeService.getAllShowtimes();
            
            java.time.OffsetDateTime now = OffsetDateTime.now();
            Set<UUID> movieIdsWithShowtimes = showtimes.stream()
                    .filter(s -> s.getStartTime().isAfter(now))
                    .map(Showtime::getMovieId)
                    .collect(Collectors.toSet());

            movies = movies.stream()
                    .filter(movie -> movieIdsWithShowtimes.contains(movie.getId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể tải danh sách phim");
            alert.showAndWait();
            return;
        }

        if (movies == null || movies.isEmpty()) {
            Label emptyLabel = new Label("Không có phim nào để hiển thị");
            emptyLabel.getStyleClass().add("empty-label");
            movieList.getChildren().add(emptyLabel);
            return;
        }

        for (Movie movie : movies) {
            VBox movieCard = createMovieCard(movie);
            movieList.getChildren().add(movieCard);
        }
    }

    @FXML
    public void handleSignUpButton() {
        screenController.activate("signup");
    }

    @FXML
    public void handleSignInButton() {
        screenController.activate("signin");
    }

    @FXML
    public void handleLogOutButton() {
        authService.logout();

        screenController.activate("home");
    }

    @FXML
    public void handleNewsButton() {
        screenController.activate("tintuc");
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(250);
        card.setMaxWidth(250);
        card.getStyleClass().add("movie-card");

        ImageView poster = new ImageView();
        try {
            poster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/movies/" + movie.getId() + ".jpg"))));
        } catch (Exception e) {
            try {
                poster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/movies/not_found.png"))));
            } catch (Exception ex) {
                try {
                    poster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cat.jpg"))));
                } catch (Exception _) {}
            }
        }
        poster.setFitWidth(204);
        poster.setFitHeight(230);
        poster.setPreserveRatio(false);
        poster.getStyleClass().add("poster");

        Label title = new Label(movie.getName());
        title.getStyleClass().add("movie-title");

        String genresText = movie.getGenres().stream()
                .map(g -> {
                    try {
                        return MovieGenre.valueOf(g).toVietnamese();
                    } catch (IllegalArgumentException e) {
                        return g;
                    }
                })
                .collect(Collectors.joining(", "));
        
        Label genres = new Label(genresText);
        genres.getStyleClass().add("movie-genre");

        Label rated = new Label("Độ tuổi: " + movie.getRated() + "+");
        rated.getStyleClass().add("movie-age");

        Label duration = new Label(movie.getDuration() + " phút");
        duration.getStyleClass().add("movie-duration");

        Label language = new Label("Ngôn ngữ: " + (movie.getLanguage() != null ? movie.getLanguage().toVietnamese() : "Không xác định"));
        language.getStyleClass().add("movie-language");

        card.setOnMouseClicked(e -> {
            screenController.setContext("selectedMovieId", movie.getId());
            screenController.activate("showtimePage");
        });
        card.setCursor(javafx.scene.Cursor.HAND);

        card.getChildren().addAll(poster, title, genres, rated, duration, language);
        return card;
    }
}
