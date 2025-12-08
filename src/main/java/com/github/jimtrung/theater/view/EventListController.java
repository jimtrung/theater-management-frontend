package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import javafx.fxml.FXML;

public class EventListController {
    private ScreenController screenController;
    private AuthService authService;

    @FXML
    private UserHeaderController userHeaderController;

    @FXML
    private javafx.scene.layout.FlowPane eventFlowPane;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();
        if (eventFlowPane != null) {
            eventFlowPane.getChildren().clear();
            javafx.scene.control.Label placeholder = new javafx.scene.control.Label("Sự kiện đang được cập nhật...");
            placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            eventFlowPane.getChildren().add(placeholder);
        }
    }
}
