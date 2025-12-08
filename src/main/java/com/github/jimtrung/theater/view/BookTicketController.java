package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;

import java.util.UUID;

public class BookTicketController {
    private ScreenController screenController;
    private AuthService authService;
    private UUID showtimeId;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setShowtimeId(UUID showtimeId) {
        this.showtimeId = showtimeId;
    }

    public void handleOnOpen() {
    }
    
    @FXML
    private void handleBookTicket() {
        
    }
}
