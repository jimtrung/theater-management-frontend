package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class IntroController {

    private ScreenController screenController;
    private AuthService authService;

    @FXML
    private UserHeaderController userHeaderController;

    // ===== Buttons =====
    @FXML
    private Button btnSystem;
    @FXML
    private Button btnRoom;
    @FXML
    private Button btnService;


    // ===== Content panes =====
    @FXML
    private VBox SystemPane;
    @FXML
    private VBox RoomPane;
    @FXML
    private VBox ServicePane;


    // ===== Inject =====
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null)
            userHeaderController.setScreenController(screenController);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null)
            userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() {
        if (userHeaderController != null)
            userHeaderController.handleOnOpen();

        // mặc định mở tab Bộ máy tổ chức
        showSystem();
    }

    // ===== Tab logic =====
    private void reset() {
        SystemPane.setVisible(false);
        SystemPane.setManaged(false);

        RoomPane.setVisible(false);
        RoomPane.setManaged(false);

        ServicePane.setVisible(false);
        ServicePane.setManaged(false);


        btnSystem.getStyleClass().remove("active");
        btnRoom.getStyleClass().remove("active");
        btnService.getStyleClass().remove("active");

    }

    @FXML
    private void showSystem() {
        reset();
        SystemPane.setVisible(true);
        SystemPane.setManaged(true);
        btnSystem.getStyleClass().add("active");
    }

    @FXML
    private void showRoom() {
        reset();
        RoomPane.setVisible(true);
        RoomPane.setManaged(true);
        btnRoom.getStyleClass().add("active");
    }

    @FXML
    private void showService() {
        reset();
        ServicePane.setVisible(true);
        ServicePane.setManaged(true);
        btnService.getStyleClass().add("active");
    }
}

