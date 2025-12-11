package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.service.*;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ShowtimePageController {
    private ScreenController screenController;
    private AuthService authService;
    private MovieService movieService;
    private ShowtimeService showtimeService;

    @FXML
    private UserHeaderController userHeaderController;

    @FXML private FlowPane showtimeList;
    @FXML private ImageView moviePoster;
    @FXML private Label movieTitle, movieDuration, movieRated, movieGenre, movieDescription, movieDirector, movieActors;


    public void setScreenController(ScreenController controller) {
        this.screenController = controller;
        if (userHeaderController != null) userHeaderController.setScreenController(controller);
    }

    public void setAuthService(AuthService service) {
        this.authService = service;
        if (userHeaderController != null) userHeaderController.setAuthService(service);
    }

    public void setMovieService(MovieService service) {
        this.movieService = service;
    }

    public void setShowtimeService(ShowtimeService service) {
        this.showtimeService = service;
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();

        loadShowtimes();
    }

    private void handleLogout() {
        authService.logout();

        screenController.activate("home");
    }

    private void loadShowtimes() {
        if (showtimeList == null) return;
        
        UUID movieId = (UUID) screenController.getContext("selectedMovieId");
        if (movieId == null) return;

        showtimeList.getChildren().clear();

        CompletableFuture.runAsync(() -> {
            try {
                var movie = movieService.getMovieById(movieId);
                var showtimes = showtimeService.getAllShowtimes();
                
                Platform.runLater(() -> {
                    if (movie != null) {
                        try {
                           moviePoster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/movies/" + movie.getId() + ".jpg"))));
                        } catch (Exception e) {
                           try {
                               moviePoster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/movies/not_found.png"))));
                           } catch (Exception ex) {
                               try {
                                   moviePoster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cat.jpg"))));
                               } catch (Exception _) {}
                           }
                        }
                        
                        movieTitle.setText(movie.getName());
                        movieDescription.setText(movie.getDescription());
                        movieDuration.setText(movie.getDuration() + " min");
                        movieRated.setText(movie.getRated() + "+");
                        movieDirector.setText("Director: " + movie.getDirector());
                        movieActors.setText("Actors: " + (movie.getActors() != null ? String.join(", ", movie.getActors()) : ""));
                        movieGenre.setText("Genre: " + (movie.getGenres() != null ? String.join(", ", movie.getGenres()) : ""));
                    }
                    
                    if (showtimes != null) {
                        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
                        var filtered = showtimes.stream()
                                .filter(s -> s.getMovieId().equals(movieId))
                                .filter(s -> s.getStartTime().isAfter(now)) 
                                .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime())) // Sort by time
                                .toList();
                                
                        if (filtered.isEmpty()) {
                            Label msg = new Label("Chưa có lịch chiếu.");
                            msg.setStyle("-fx-text-fill: white;");
                            showtimeList.getChildren().add(msg);
                        } else {
                            for (Showtime s : filtered) {
                                showtimeList.getChildren().add(createShowtimeButton(s));
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Button createShowtimeButton(Showtime showtime) {
        // Fix: Convert to +7 timezone
        java.time.OffsetDateTime localTime = showtime.getStartTime().withOffsetSameInstant(java.time.ZoneOffset.ofHours(7));
        String timeStr = localTime.toLocalTime().toString();
        String dateStr = localTime.toLocalDate().toString();
        
        Button btn = new Button(dateStr + "\n" + timeStr);
        btn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold; -fx-alignment: center;");
        btn.setPrefWidth(120);
        btn.setPrefHeight(60);
        btn.setCursor(javafx.scene.Cursor.HAND);
        
        btn.setOnAction(e -> {
            screenController.setContext("selectedShowtimeId", showtime.getId());

            screenController.setContext("selectedShowtimeId", showtime.getId());
            screenController.activate("bookTicket");
        });
        
        return btn;
    }
}
