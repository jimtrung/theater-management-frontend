package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;

public class ShowtimeInformationController {
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
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) {
        }

        if (user == null || user.getRole() != UserRole.administrator) { // Assuming role 1 is Admin
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
