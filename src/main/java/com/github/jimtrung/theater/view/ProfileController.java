package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class ProfileController {
    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        Object response = null;
        try {
            response = authService.getUser();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fetch error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to fetch user");
            alert.showAndWait();
            e.printStackTrace();
            return;
        }

        if (response instanceof User userInfo) {
            usernameField.setText(userInfo.getUsername());
            emailField.setText(userInfo.getEmail());
            // passwordField.setText(userInfo.getPassword()); // Not present in FXML
            
            boolean isVerified = userInfo.getVerified();
            verifiedField.setText(isVerified ? "Đã xác thực" : "Chưa xác thực");
            verifiedField.getStyleClass().removeAll("status-verified", "status-unverified");
            verifiedField.getStyleClass().add(isVerified ? "status-verified" : "status-unverified");

            createdAtField.setText(userInfo.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else if (response instanceof ErrorResponse errRes){
            screenController.activate("home");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fetch error");
            alert.setHeaderText(null);
            alert.setContentText(errRes.message());
            alert.showAndWait();
        }
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private javafx.scene.control.Label verifiedField;

    @FXML
    private TextField createdAtField;

    @FXML
    public void handleBackButton(ActionEvent event) { screenController.activate("home"); }

    @FXML
    public void handleEditButton(ActionEvent event) {
    }

    @FXML
    public void handleLogOutButton(ActionEvent event) {
        authService.logout();

        screenController.activate("home");
    }
}
