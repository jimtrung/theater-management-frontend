package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {
    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        User user = null;
        try { user = (User) authService.getUser(); } catch (Exception _) {}

        if (user != null) {
            if (user.getRole() == UserRole.USER) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.ADMINISTRATOR) screenController.activate("homePageManager");
        }
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleBackButton(ActionEvent event) {
        screenController.activate("home");
    }

    @FXML
    public void handleSignUpButton(ActionEvent event) {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setEmail(emailField.getText());
        user.setPhoneNumber(phoneNumberField.getText());
        user.setPassword(passwordField.getText());

        Object response = null;
        try {
            response = authService.signUp(user);

            if (response instanceof ErrorResponse errRes) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sign up error");
                alert.setHeaderText(null);
                alert.setContentText(errRes.message());
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign up error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to sign up\n");
            alert.showAndWait();
            return;
        }

        screenController.activate("signin");
    }
}
