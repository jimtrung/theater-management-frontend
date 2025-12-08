package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;

public class HomePageManagerController {

    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
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
}
