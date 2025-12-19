package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Ticket;
import com.github.jimtrung.theater.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;


import java.awt.*;

public class HomePageManagerController {

    private ScreenController screenController;
    private AuthService authService;

    private MovieService movieService;
    private AuditoriumService auditoriumService;
    private ShowtimeService showtimeService;
    private TicketService ticketService;
    private UserService userService;

    @FXML private Label userCountText;
    @FXML private Label movieCountText;
    @FXML private Label auditoriumCountText;
    @FXML private Label showtimeCountText;
    @FXML private Label paidTicketCountText;
    @FXML private Label pendingTicketCountText;
    @FXML private Label revenueText;
    @FXML private Pane chartGrid;


    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void setShowtimeService(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    public void handleMovieButton() {
        screenController.activate("movieList");
    }

    @FXML
    public void handleAuditoriumButton() {
        screenController.activate("auditoriumList");
    }

    @FXML
    public void handleShowtimeButton() {
        screenController.activate("showtimeList");
    }

    @FXML
    public void handleProfileButton() {
        screenController.activate("profile");
    }

    @FXML
    public void handleLogOutButton(ActionEvent event) {
        authService.logout();
        screenController.activate("home");
    }

    @FXML
    public void handleOnOpen() {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            int movieCount = movieService.getAllMovies().size();
            int auditoriumCount = auditoriumService.getAllAuditoriums().size();
            int showtimeCount = showtimeService.getAllShowtimes().size();
            int paidTicketCount = ticketService.getAllTickets("PAID").size();
            int pendingTicketCount = ticketService.getAllTickets("PENDING").size();
            int userCount = userService.getAllUsers().size();

            long totalRevenue = 0;
            for (Ticket ticket : ticketService.getAllTickets("PAID")) {
                totalRevenue += ticket.getPrice();
            }

            userCountText.setText("- Người dùng: " + userCount);
            movieCountText.setText("- Phim: " + movieCount);
            auditoriumCountText.setText("- Phòng: " + auditoriumCount);
            showtimeCountText.setText("- Suất chiếu: " + showtimeCount);
            paidTicketCountText.setText("- Vé đã thanh toán: " + paidTicketCount);
            pendingTicketCountText.setText("- Vé chưa thanh toán: " + pendingTicketCount);
            revenueText.setText("- Tổng doanh thu dự kiến: " + totalRevenue + " VNĐ");

            buildBarChart(
                    userCount,
                    movieCount,
                    auditoriumCount,
                    showtimeCount,
                    paidTicketCount,
                    pendingTicketCount
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void buildBarChart(
            int userCount,
            int movieCount,
            int auditoriumCount,
            int showtimeCount,
            int paidTicketCount,
            int pendingTicketCount
    ) {
        chartGrid.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelRotation(0);

        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);

        barChart.setPrefWidth(chartGrid.getPrefWidth());
        barChart.setPrefHeight(chartGrid.getPrefHeight());

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>("Người dùng", userCount));
        series.getData().add(new XYChart.Data<>("Phim", movieCount));
        series.getData().add(new XYChart.Data<>("Phòng", auditoriumCount));
        series.getData().add(new XYChart.Data<>("Suất chiếu", showtimeCount));
        series.getData().add(new XYChart.Data<>("Vé đã TT", paidTicketCount));
        series.getData().add(new XYChart.Data<>("Vé chưa TT", pendingTicketCount));

        barChart.getData().add(series);

        chartGrid.getChildren().add(barChart);
    }

}
