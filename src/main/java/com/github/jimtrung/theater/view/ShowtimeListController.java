package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

import java.util.UUID;

public class ShowtimeListController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    @FXML
    private Button addBtn;

    @FXML
    private Button closeBtn;

    public void handleAddBtn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_showtime.fxml"));
            screenController.addScreen("addShowtime", loader);

            AddShowtimeController addShowtimeController = loader.getController();
            addShowtimeController.setScreenController(screenController);
//            addShowtimeController.setAuditoriumService(auditoriumService);
            addShowtimeController.setAuthTokenUtil(authTokenUtil);
            addShowtimeController.setShowtimeListController(this);

            screenController.activate("addShowtime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCloseBtn() {
        screenController.activate("homePageManager");
    }
}
