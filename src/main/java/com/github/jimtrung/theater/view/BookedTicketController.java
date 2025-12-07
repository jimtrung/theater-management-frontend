package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;

public class BookedTicketController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;

    @FXML
    private UserHeaderController userHeaderController;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        if (userHeaderController != null) userHeaderController.setAuthTokenUtil(authTokenUtil);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();
        
        // TODO: Load booked tickets from service
        // This controller is responsible for displaying the user's booked tickets
        // The header handles all authentication and navigation
    }
}
