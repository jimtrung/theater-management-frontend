package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.ErrorResponse;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private Label verifiedField;
    @FXML private TextField createdAtField;

    public void handleOnOpen() {
        Object response = null;
        try {
            response = authService.getUser();
        } catch (Exception e) {
            AlertHelper.showError("Lỗi kết nối", "Không thể lấy thông tin người dùng");
            e.printStackTrace();
            return;
        }

        if (response instanceof User userInfo) {
            usernameField.setText(userInfo.getUsername());
            emailField.setText(userInfo.getEmail());

            boolean isVerified = userInfo.getVerified();
            verifiedField.setText(isVerified ? "Đã xác thực" : "Chưa xác thực");
            verifiedField.getStyleClass().removeAll("status-verified", "status-unverified");
            verifiedField.getStyleClass().add(isVerified ? "status-verified" : "status-unverified");

            createdAtField.setText(userInfo.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else if (response instanceof ErrorResponse errRes){
            screenController.activate("home");
            AlertHelper.showError("Lỗi kết nối", errRes.message());
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        screenController.activate("home");
    }

    @FXML
    public void handleEditButton(ActionEvent event) {
    }

    @FXML
    public void handleLogOutButton(ActionEvent event) {
        authService.logout();
        screenController.activate("home");
    }
}
