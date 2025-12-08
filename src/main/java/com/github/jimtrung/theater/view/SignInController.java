package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.dto.TokenPair;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.awt.*;

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
        try { user = (User) authService.getUser(); } catch (Exception _) {}

        if (user != null) {
            if (user.getRole() == UserRole.user) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.administrator) screenController.activate("homePageManager");
        }

        passwordField.clear();
        passwordField.setVisible(true);

        visiblePasswordField.clear();
        visiblePasswordField.setVisible(false);
        showPasswordCheckBox.setSelected(false);

        usernameField.clear();
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    public void handleBackButton() {
        screenController.activate("home");
    }

    @FXML
    public void handleSignInButton() {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());

        try {
            Object response = authService.signIn(user);
            if (response instanceof ErrorResponse errRes) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sign in error");
                alert.setHeaderText(null);
                alert.setContentText(errRes.message());
                alert.showAndWait();
                return;
            }

            TokenPair tokenPair = (TokenPair) response;
            authTokenUtil.saveAccessToken(tokenPair.accessToken());
            authTokenUtil.saveRefreshToken(tokenPair.refreshToken());

            usernameField.clear();
            passwordField.clear();
            visiblePasswordField.clear();
            showPasswordCheckBox.setSelected(false);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign in error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to sign in");
            alert.showAndWait();
            return;
        }

        screenController.activate("home");
    }

    @FXML
    private void TogglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
        }
    }
}
