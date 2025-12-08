package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ShowtimeInformationController {
    private ScreenController screenController;
    private AuthService authService;
    private ShowtimeService showtimeService;
    private MovieService movieService;
    private AuditoriumService auditoriumService;
    private ShowtimeListController showtimeListController;

    private UUID uuid;
    private Showtime currentShowtime;

    @FXML private TextField showtimeMovieNameField;
    @FXML private TextField showtimeAuditoriumNameField;
    @FXML private TextField showtimeQuantityField;
    @FXML private TextField showtimeDateField;
    @FXML private TextField showtimeStartTimeField;
    @FXML private TextField showtimeEndTimeField;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setShowtimeService(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void setShowtimeListController(ShowtimeListController showtimeListController) {
        this.showtimeListController = showtimeListController;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void handleOnOpen() {
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
             screenController.activate("home");
             return;
        }

        try {
            currentShowtime = showtimeService.getShowtimeById(uuid);
            if (currentShowtime != null) {
                 Movie movie = movieService.getMovieById(currentShowtime.getMovieId());
                 Auditorium auditorium = auditoriumService.getAuditoriumById(currentShowtime.getAuditoriumId());
                 
                 showtimeMovieNameField.setText(movie != null ? movie.getName() : "Unknown");
                 showtimeAuditoriumNameField.setText(auditorium != null ? auditorium.getName() : "Unknown");
                 showtimeQuantityField.setText("0"); // Quantity not in model yet, default 0

                 DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                 DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                 showtimeDateField.setText(currentShowtime.getStartTime().format(dateFormatter));
                 showtimeStartTimeField.setText(currentShowtime.getStartTime().format(timeFormatter));
                 showtimeEndTimeField.setText(currentShowtime.getEndTime().format(timeFormatter));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackButton() {
        screenController.activate("showtimeList");
    }

    @FXML
    public void handleDeleteButton() {
        try {
            showtimeService.deleteShowtimeById(uuid);
            if (showtimeListController != null) {
                showtimeListController.refreshData();
            }
            screenController.activate("showtimeList");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
