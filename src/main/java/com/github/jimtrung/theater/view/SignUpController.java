package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;
import com.github.jimtrung.theater.util.AlertHelper;
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

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visibleConfirmPasswordField;
    @FXML private CheckBox showConfirmPasswordCheckBox;
    @FXML private Label usernameErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @FXML
    public void handleBackButton() {
        screenController.activate("home");
    }


    public void handleOnOpen() {
        User user = null;
        try { user = (User) authService.getUser(); } catch (Exception _) {}

        if (user != null) {
            if (user.getRole() == UserRole.user) screenController.activate("homePageUser");
            if (user.getRole() == UserRole.administrator) screenController.activate("homePageManager");
        }

        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        confirmPasswordField.clear();
        visibleConfirmPasswordField.clear();
        showPasswordCheckBox.setSelected(false);

        usernameErrorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);

        passwordField.textProperty().unbindBidirectional(visiblePasswordField.textProperty());
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

        confirmPasswordField.textProperty().unbindBidirectional(visibleConfirmPasswordField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(visibleConfirmPasswordField.textProperty());

        passwordField.setVisible(true);
        passwordField.setManaged(true);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        confirmPasswordField.setVisible(true);
        confirmPasswordField.setManaged(true);
        visibleConfirmPasswordField.setVisible(false);
        visibleConfirmPasswordField.setManaged(false);

        // Real-time Validation Listeners
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateAll());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateAll());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateAll());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateAll());

    }

    @FXML
    public void handleSignUpButton() {

        if (!validateAll()) {
            return;
        }

        User user = new User();
        user.setUsername(usernameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPassword(passwordField.getText());

        try {
            Object response = authService.signUp(user);

            if (response instanceof ErrorResponse errRes) {
                AlertHelper.showError("Lỗi đăng ký", errRes.message());
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Lỗi đăng ký", "Đăng ký thất bại");
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

            visibleConfirmPasswordField.setText(confirmPasswordField.getText());
            visibleConfirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);

            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            visibleConfirmPasswordField.setVisible(false);
            visibleConfirmPasswordField.setManaged(false);
        }
    }

    private boolean validateAll() {
        boolean isValid = true;

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // ===== Username =====
        if (username.isEmpty()) {
            usernameErrorLabel.setText("Tên đăng nhập không được để trống!");
            usernameErrorLabel.setVisible(true);
            isValid = false;
        } else if (authService.checkUsername(username)) {
            usernameErrorLabel.setText("Tên đăng nhập đã tồn tại!");
            usernameErrorLabel.setVisible(true);
            isValid = false;
        } else {
            usernameErrorLabel.setVisible(false);
        }

        // ===== Email =====
        if (email.isEmpty()) {
            emailErrorLabel.setText("Email không được để trống!");
            emailErrorLabel.setVisible(true);
            isValid = false;
        } else if (!java.util.regex.Pattern.matches(EMAIL_PATTERN, email)) {
            emailErrorLabel.setText("Email không hợp lệ!");
            emailErrorLabel.setVisible(true);
            isValid = false;
        } else if (authService.checkEmail(email)) {
            emailErrorLabel.setText("Email đã được sử dụng!");
            emailErrorLabel.setVisible(true);
            isValid = false;
        } else {
            emailErrorLabel.setVisible(false);
        }

        // ===== Password + Confirm =====
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Mật khẩu không được để trống!");
            passwordErrorLabel.setVisible(true);
            isValid = false;
        } else if (!java.util.regex.Pattern.matches(PASSWORD_PATTERN, password)) {
            passwordErrorLabel.setText("Mật khẩu yếu! Cần 8+ ký tự (Hoa, thường, số, đặc biệt)");
            passwordErrorLabel.setVisible(true);
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            passwordErrorLabel.setText("Mật khẩu xác nhận không khớp!");
            passwordErrorLabel.setVisible(true);
            isValid = false;
        } else {
            passwordErrorLabel.setVisible(false);
        }

        return isValid;
    }

}
