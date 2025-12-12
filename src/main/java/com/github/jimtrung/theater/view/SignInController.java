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
import javafx.scene.control.Label;
import java.util.concurrent.CompletableFuture;

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
        
        if (usernameErrorLabel != null) {
             usernameErrorLabel.setVisible(false);
        }
        if (passwordErrorLabel != null) {
             passwordErrorLabel.setVisible(false);
        }

        passwordField.textProperty().unbindBidirectional(visiblePasswordField.textProperty());
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

        passwordField.clear();
        passwordField.setVisible(true);
        passwordField.setManaged(true);

        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        showPasswordCheckBox.setSelected(false);

        usernameField.clear();
        
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                usernameErrorLabel.setVisible(false);
            } else {
                validateUsername(newValue);
            }
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
             if (newValue == null || newValue.isEmpty()) {
                passwordErrorLabel.setText("Vui lòng nhập mật khẩu");
                passwordErrorLabel.setVisible(true);
            } else {
                passwordErrorLabel.setVisible(false);
            }
        });
    }

    private void validateUsername(String username) {
        usernameErrorLabel.setText("Đang kiểm tra...");
        usernameErrorLabel.setVisible(true);
        usernameErrorLabel.setStyle("-fx-text-fill: gray;");

        CompletableFuture.supplyAsync(() -> authService.checkUsername(username))
            .thenAccept(exists -> javafx.application.Platform.runLater(() -> {
                if (!exists) {
                    usernameErrorLabel.setText("Tài khoản không tồn tại!");
                    usernameErrorLabel.setVisible(true);
                    usernameErrorLabel.setStyle("-fx-text-fill: red;");
                } else {
                    usernameErrorLabel.setText("Tài khoản hợp lệ.");
                    usernameErrorLabel.setVisible(true);
                    usernameErrorLabel.setStyle("-fx-text-fill: green;");
                }
            }));
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
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

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
                alert.setTitle("Lỗi đăng nhập");
                alert.setHeaderText(null);
                String msg = errRes.message();
                if (msg.contains("Bad credentials") || msg.contains("User not found")) {
                    alert.setContentText("Tên đăng nhập hoặc mật khẩu không chính xác!");
                } else if (msg.contains("User account is locked")) {
                    alert.setContentText("Tài khoản đã bị khóa!");
                } else {
                    alert.setContentText("Đăng nhập thất bại: " + msg);
                }
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
                alert.setTitle("Lỗi đăng nhập");
            alert.setHeaderText(null);
            alert.setContentText("Đăng nhập thất bại");
            alert.showAndWait();
            return;
        }

        screenController.activate("home");
    }

    @FXML
    private void TogglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }
}
