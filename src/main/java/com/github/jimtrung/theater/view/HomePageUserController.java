package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomePageUserController {
    private ScreenController screenController;
    private AuthService authService;
    private AuthTokenUtil authTokenUtil;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void handleOnOpen() {
        User user = null;
        try {
            user = authService.getUser();
        } catch (Exception e) {
        }
        if (user == null) screenController.activate("home");
    }

    @FXML
    private Label titleLabel;

    @FXML
    private Button signupButton;

    @FXML
    private Button signinButton;

    @FXML
    private Button settingsButton;

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
}
