package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.UUID;

public class AuditoriumInformationController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuditoriumService auditoriumService;
    private AuditoriumListController auditoriumListController;
    private UUID uuid;

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void setAuditoriumListController(AuditoriumListController auditoriumListController) {
        this.auditoriumListController = auditoriumListController;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }


    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void handleBackButton() {
        auditoriumListController.getAuditoriumTable().getSelectionModel().clearSelection();
        screenController.removeScreen("auditoriumInformation");
        screenController.activate("auditoriumList");
    }

    @FXML
    public void handleEditButton() {
        try {
            Auditorium updatedAuditorium = new Auditorium();
            updatedAuditorium.setName(auditoriumNameField.getText().trim());
            updatedAuditorium.setType(auditoriumTypeField.getText().trim());
            updatedAuditorium.setNote(auditoriumNoteField.getText().trim());

            try {
                updatedAuditorium.setCapacity(Integer.parseInt(auditoriumCapacityField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("[WARN] - Invalid age limit input. Default to 0");
                updatedAuditorium.setCapacity(0);
            }

            auditoriumService.updateAuditorium(uuid, updatedAuditorium);

            Auditorium auditorium = auditoriumService.getAuditoriumById(uuid);
            auditoriumListController.updateAuditorium(auditorium);

//            movieListController.refreshData();

            System.out.println("[INFO] - Movie updated successfully: " + updatedAuditorium.getName());

            screenController.activate("auditoriumList");

        } catch (Exception e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleDeleteButton () throws Exception {
        auditoriumService.deleteAuditoriumById(uuid);
        auditoriumListController.refreshData();
        screenController.activate("auditoriumList");
    }

    @FXML
    public void handleOnOpen() throws Exception {
        System.out.println("Auditorium id was receive: " + uuid);
        Auditorium auditorium = new Auditorium();
        auditorium = auditoriumService.getAuditoriumById(uuid);

        if (auditorium == null) {
            System.out.println("[WARN] - Movie not found with id: " + uuid);
            return;
        }

        auditoriumNameField.setText(auditorium.getName());
        auditoriumTypeField.setText(auditorium.getType());
        auditoriumCapacityField.setText(String.valueOf(auditorium.getCapacity()));
        System.out.println("[12345]: " + auditorium.getNote());
        auditoriumNoteField.setText(auditorium.getNote());

        System.out.println("[DEBUG] - Auditorium loaded: " + auditorium.getName());
    }

    @FXML
    private TextField auditoriumNameField;

    @FXML
    private TextField auditoriumTypeField;

    @FXML
    private TextField auditoriumCapacityField;

    @FXML
    private TextArea auditoriumNoteField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button backButton;
}



