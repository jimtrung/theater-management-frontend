package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.TokenPair;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.awt.*;
import java.net.URI;

public class SignInController {
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
        User user = null;
        try {
            user = authService.getUser();
        } catch (Exception e) {}
        if (user != null) {
            if (user.getRole() == UserRole.USER) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.ADMINISTRATOR) screenController.activate("homePageManager");
        }
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button githubSignInButton;

    @FXML
    public void handleBackButton(ActionEvent event) {
        screenController.activate("home");
    }

    @FXML
    public void handleSignInButton(ActionEvent event) {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());

        try {
            TokenPair tokenPair = (TokenPair) authService.signIn(user);
            authTokenUtil.saveAccessToken(tokenPair.accessToken());
            authTokenUtil.saveRefreshToken(tokenPair.refreshToken());
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign in error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to sign in\n");
            alert.showAndWait();
            return;
        }
        screenController.activate("home");
    }

    @FXML
    public void handleGoogleSignInButton() {
        try {
            String backendAuthUrl = "http://localhost:8080/oauth2/authorization/google";

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(backendAuthUrl));
                System.out.println("Browser opened → please login with Google...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign in error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to sign in with Google\n");
            alert.showAndWait();
            return;
        }
    }
}
