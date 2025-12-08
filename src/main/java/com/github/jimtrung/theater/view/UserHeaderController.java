package com.github.jimtrung.theater.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;

public class UserHeaderController {

    @FXML
    private HBox authButtons;

    @FXML
    private HBox userInfo;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        checkAuthStatus();
    }

    private void checkAuthStatus() {
        try {
            if (authService != null && authService.isLoggedIn()) {
                authButtons.setVisible(false);
                authButtons.setManaged(false);
                userInfo.setVisible(true);
                userInfo.setManaged(true);
                User user = (User) authService.getUser();
                if (user != null && usernameLabel != null) {
                    usernameLabel.setText(user.getUsername());
                }
            } else {
                authButtons.setVisible(true);
                authButtons.setManaged(true);
                userInfo.setVisible(false);
                userInfo.setManaged(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHomeButton() {
        if (screenController != null) screenController.activate("home");
    }

    @FXML
    private void handleShowTimesButton() {
        if (screenController != null) screenController.activate("showtimePage");
    }

    @FXML
    private void handleBookedTicketButton() {
        if (screenController != null) screenController.activate("bookedTicket");
    }

    @FXML
    private void handleNewsButton() {
        if (screenController != null) screenController.activate("tintuc");
    }

    @FXML
    private void handlePromotionButton() {
        if (screenController != null) screenController.activate("eventList");
    }

    @FXML
    private void handlePriceButton() {
        if (screenController != null) screenController.activate("price");
    }

    @FXML
    private void handleAboutButton() {
        // TODO: Implement about page
    }

    @FXML
    private void handleSignUpButton() {
        if (screenController != null) screenController.activate("signup");
    }

    @FXML
    private void handleSignInButton() {
        if (screenController != null) screenController.activate("signin");
    }

    @FXML
    private void handleProfileButton() {
        if (screenController != null) screenController.activate("profile");
    }

    @FXML
    private void handleLogoutButton() {
        if (authService != null) {
            authService.logout();
        }
        checkAuthStatus();
        if (screenController != null) screenController.activate("home");
    }
}
