package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;

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
            if (user.getRole() == UserRole.user) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.administrator) screenController.activate("homePageManager");
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
}
