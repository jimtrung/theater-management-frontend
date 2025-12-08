package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;

public class EventListController {
    private ScreenController screenController;
    private AuthService authService;

    @FXML
    private UserHeaderController userHeaderController;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();
    }
}
