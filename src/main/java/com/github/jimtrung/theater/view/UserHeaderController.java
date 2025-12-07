package com.github.jimtrung.theater.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import java.net.URL;
import java.util.ResourceBundle;

public class UserHeaderController implements Initializable {

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
    private AuthTokenUtil authTokenUtil;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initial state logic if needed, but mostly handled in handleOnOpen or listener
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
    private void handleHome() {
        if (screenController != null) screenController.activate("home");
    }

    @FXML
    private void handleShowTimes() {
        if (screenController != null) screenController.activate("showtimePage");
    }

    @FXML
    private void handleNews() {
        if (screenController != null) screenController.activate("tintuc");
    }

    @FXML
    private void handlePromotion() {
        if (screenController != null) screenController.activate("eventList");
    }

    @FXML
    private void handleAbout() {
        // TODO: Implement about page
    }

    @FXML
    private void handleRegister() {
        if (screenController != null) screenController.activate("signup");
    }

    @FXML
    private void handleLogin() {
        if (screenController != null) screenController.activate("signin");
    }

    @FXML
    private void handleProfile() {
        if (screenController != null) screenController.activate("profile");
    }

    @FXML
    private void handleSettings() {
        // TODO: Implement settings
    }

    @FXML
    private void handleLogout() {
        if (authService != null) {
            authService.logout();
        }
        checkAuthStatus();
        if (screenController != null) screenController.activate("home");
    }
}
