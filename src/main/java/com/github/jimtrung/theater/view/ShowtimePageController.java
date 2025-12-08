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
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ShowtimePageController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;
    private MovieService movieService;
    private ShowtimeService showtimeService;
    private AuditoriumService auditoriumService;

    @FXML
    private UserHeaderController userHeaderController;

    @FXML
    private FlowPane showtimeList;

    @FXML
    private Button bookedTicketButton;

    public void setScreenController(ScreenController controller) {
        this.screenController = controller;
        if (userHeaderController != null) userHeaderController.setScreenController(controller);
    }

    public void setAuthTokenUtil(AuthTokenUtil util) {
        this.authTokenUtil = util;
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

    public void setAuditoriumService(AuditoriumService service) {
        this.auditoriumService = service;
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

        showtimeList.getChildren().clear();

        CompletableFuture
            .supplyAsync(() -> {
                try {
                    return showtimeService.getAllShowtimes();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .thenAccept(showtimes -> {
                Platform.runLater(() -> {
                    if (showtimes == null || showtimes.isEmpty()) {
                        Label msg = new Label("Không có suất chiếu nào");
                        msg.setTextFill(Color.WHITE);
                        msg.setStyle("-fx-font-size: 16;");
                        showtimeList.getChildren().add(msg);
                        return;
                    }

                    for (Showtime s : showtimes) {
                        showtimeList.getChildren().add(createShowtimeCardAsync(s));
                    }
                });
            });
    }

    private VBox createShowtimeCardAsync(Showtime showtime) {
        VBox box = new VBox();
        box.setSpacing(6);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: #333333; -fx-background-radius: 10;");
        box.setPrefWidth(220);

        ImageView img = new ImageView();
        img.setFitWidth(220);
        img.setFitHeight(260);

        try {
            img.setImage(new Image("file:resources/movies/" + showtime.getMovieId() + ".jpg"));
        } catch (Exception e) {
            img.setImage(new Image("file:resources/movies/not_found.png"));
        }

        Label title = new Label("Đang tải tên phim...");
        title.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: white;");

        Label auditorium = new Label("Đang tải phòng...");
        auditorium.setStyle("-fx-text-fill: #cccccc;");

        Label time = new Label("Giờ chiếu: " + showtime.getStartTime() + " - " + showtime.getEndTime());
        time.setStyle("-fx-text-fill: #bbbbbb;");

        Label date = new Label("Ngày: " + showtime.getStartTime().getDayOfMonth());
        date.setStyle("-fx-text-fill: #bbbbbb;");

        int price = (int) (Math.random() * 5 + 65) * 1000;
        Label priceLabel = new Label("Giá vé: " + price + " đ");
        priceLabel.setStyle("-fx-text-fill: lightgreen;");

        Button bookBtn = new Button("Đặt vé");
        bookBtn.setStyle("-fx-background-color: darkred; -fx-text-fill: white; -fx-padding: 6 12;");

        bookBtn.setOnAction(e -> {
            FXMLLoader bookTicketLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/book_ticket.fxml")));
            BookTicketController bookTicketController = bookTicketLoader.getController();
            bookTicketController.setShowtimeId(showtime.getId());
        });

        box.getChildren().addAll(img, title, auditorium, time, date, priceLabel, bookBtn);

        CompletableFuture.runAsync(() -> {
            try {
                var movie = movieService.getMovieById(showtime.getMovieId());
                var aud = auditoriumService.getAuditoriumById(showtime.getAuditoriumId());

                Platform.runLater(() -> {
                    if (movie != null) title.setText(movie.getName());
                    if (aud != null) auditorium.setText("Phòng: " + aud.getName());
                });

            } catch (Exception ignored) {}
        });

        return box;
    }
}
