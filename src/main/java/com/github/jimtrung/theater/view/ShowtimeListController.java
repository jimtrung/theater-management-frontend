package com.github.jimtrung.theater.view;

import javafx.fxml.FXML;

public class ShowtimeListController {
    private ScreenController screenController;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void handleAddShowtimeButton() {
        screenController.activate("addShowtime");
    }

    @FXML
    public void handleCloseButton() {
        screenController.activate("homePageManager");
    }
    
    @FXML
    public void handleDeleteAllButton() {
       // TODO: Implement this
    }
}
