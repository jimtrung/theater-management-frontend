package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class SignUpController {
    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;
    
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField visibleConfirmPasswordField;

    @FXML
    private CheckBox showConfirmPasswordCheckBox;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    public void handleBackButton() {
        screenController.activate("home");
    }

    // Pattern Validation
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public void handleOnOpen() {
        User user = null;
        try { user = (User) authService.getUser(); } catch (Exception _) {}

        if (user != null) {
            if (user.getRole() == UserRole.user) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.administrator) screenController.activate("homePageManager");
        }
        
        // Initial clean up
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        confirmPasswordField.clear();
        visibleConfirmPasswordField.clear();
        showPasswordCheckBox.setSelected(false);
        
        emailErrorLabel.setVisible(false);
        // emailErrorLabel.setManaged(false); // Reserved space
        passwordErrorLabel.setVisible(false);
        // passwordErrorLabel.setManaged(false); // Reserved space

        // Bindings
        passwordField.textProperty().unbindBidirectional(visiblePasswordField.textProperty());
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        
        // Note: confirmPasswordField binding
        confirmPasswordField.textProperty().unbindBidirectional(visibleConfirmPasswordField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(visibleConfirmPasswordField.textProperty());
        
        // Reset state
        passwordField.setVisible(true);
        passwordField.setManaged(true);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        
        confirmPasswordField.setVisible(true);
        confirmPasswordField.setManaged(true);
        visibleConfirmPasswordField.setVisible(false);
        visibleConfirmPasswordField.setManaged(false);

        // Real-time Validation Listeners
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail(newValue));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
    }

    private void validateEmail(String email) {
        if (!java.util.regex.Pattern.matches(EMAIL_PATTERN, email)) {
            emailErrorLabel.setText("Email không hợp lệ!");
            emailErrorLabel.setVisible(true);
        } else {
            emailErrorLabel.setVisible(false);
        }
    }

    private void validatePassword(String password) {
        if (!java.util.regex.Pattern.matches(PASSWORD_PATTERN, password)) {
            passwordErrorLabel.setText("Mật khẩu yếu! Cần 8+ ký tự (Hoa, thường, số, đặc biệt)");
            passwordErrorLabel.setVisible(true);
        } else {
            passwordErrorLabel.setVisible(false);
        }
    }

    @FXML
    public void handleSignUpButton() {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setEmail(emailField.getText());
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

    @FXML
    private void TogglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }
}
