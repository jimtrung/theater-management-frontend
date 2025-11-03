package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {
    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        User user = null;
        try { user = (User) authService.getUser(); } catch (Exception _) {}

        if (user != null) {
            System.out.println("[DEBUG] Role: " + user.getRole());
            if (user.getRole() == UserRole.USER) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.ADMINISTRATOR) screenController.activate("homePageManager");
        }
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
}
