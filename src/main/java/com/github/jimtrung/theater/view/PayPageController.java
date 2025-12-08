package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;

public class PayPageController {
    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        if (!authService.isLoggedIn()) {
            screenController.activate("signin");
        }
    }
    
    @FXML
    private void handleBackButton() {
        screenController.activate("bookTicket");
    }    
}
