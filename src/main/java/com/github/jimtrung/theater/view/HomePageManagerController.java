package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

import java.awt.*;

public class HomePageManagerController {

    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    @FXML
    private Button MovieButton;

    @FXML
    private Button profileButton;

    @FXML
    public void handleMovieButton() {
        screenController.activate("movieList");
    }

    @FXML
    public void handleProfileButton() {
        screenController.activate("profile");
    }

    @FXML
    public void handleLogOutButton(ActionEvent event) {
        authTokenUtil.clearRefreshToken();
        authTokenUtil.clearAccessToken();

        screenController.activate("home");
    }
}
