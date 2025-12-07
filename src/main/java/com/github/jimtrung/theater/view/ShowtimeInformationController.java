package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;

public class ShowtimeInformationController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) {
        }

        if (user == null || user.getRole() != UserRole.administrator) { // Assuming role 1 is Admin
            // Not admin or not logged in, redirect
             screenController.activate("home"); // Redirect to user home or login
        }
    }

    @FXML
    public void handleBackButton() {
        screenController.activate("showtimeList");
    }

    @FXML
    public void handleDeleteButton() {
        // TODO: Implement showtime deletion
        screenController.activate("showtimeList");
    }
}
