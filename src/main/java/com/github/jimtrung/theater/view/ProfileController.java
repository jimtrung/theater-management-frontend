package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;

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
            usernameLabel.setText(userInfo.getUsername());
            emailLabel.setText(userInfo.getEmail());
            phoneNumberLabel.setText(userInfo.getPhoneNumber());
            passwordLabel.setText(userInfo.getPassword());
            verifiedLabel.setText(userInfo.getVerified().toString());
            createdAtLabel.setText(userInfo.getCreatedAt().toString());
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
    private TextField usernameLabel;

    @FXML
    private TextField emailLabel;

    @FXML
    private TextField phoneNumberLabel;

    @FXML
    private PasswordField passwordLabel;

    @FXML
    private TextField verifiedLabel;

    @FXML
    private TextField createdAtLabel;

    @FXML
    public void handleBackButton(ActionEvent event) { screenController.activate("home"); }

    @FXML
    public void handleEditButton(ActionEvent event) {
    }

    @FXML
    public void handleLogOutButton(ActionEvent event) {
        authTokenUtil.clearRefreshToken();
        authTokenUtil.clearAccessToken();

        screenController.activate("home");
    }
}
