package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.UUID;

public class AuditoriumInformationController {
    private ScreenController screenController;
    private AuthService authService;
    private AuditoriumService auditoriumService;
    private UUID uuid;
    private Auditorium currentAuditorium;

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }



    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML private TextField auditoriumNameField;
    @FXML private TextField auditoriumTypeField;
    @FXML private TextField auditoriumCapacityField;
    @FXML private TextArea auditoriumNoteField;

    @FXML
    public void handleBackButton() {
        AuditoriumListController listController = (AuditoriumListController) screenController.getController("auditoriumList");
        if (listController != null) {
            listController.getAuditoriumTable().getSelectionModel().clearSelection();
        }
        // screenController.removeScreen("auditoriumInformation"); // FIXED: Do not remove screen
        screenController.activate("auditoriumList");
    }

    @FXML
    public void handleEditButton() {
        try {
            if (currentAuditorium == null) {
                currentAuditorium = new Auditorium();
                currentAuditorium.setId(uuid);
            }

            currentAuditorium.setName(auditoriumNameField.getText().trim());
            currentAuditorium.setType(auditoriumTypeField.getText().trim());
            currentAuditorium.setNote(auditoriumNoteField.getText().trim());

            try {
                currentAuditorium.setCapacity(Integer.parseInt(auditoriumCapacityField.getText().trim()));
            } catch (NumberFormatException e) {
                currentAuditorium.setCapacity(0);
            }

            auditoriumService.updateAuditorium(uuid, currentAuditorium);

            Auditorium auditorioum = auditoriumService.getAuditoriumById(uuid);
            AuditoriumListController listController = (AuditoriumListController) screenController.getController("auditoriumList");
            if (listController != null) {
                listController.updateAuditorium(auditorioum);
            }

            screenController.activate("auditoriumList");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteButton () throws Exception {
        auditoriumService.deleteAuditoriumById(uuid);
        AuditoriumListController listController = (AuditoriumListController) screenController.getController("auditoriumList");
        if (listController != null) {
            listController.refreshData();
        }
        screenController.activate("auditoriumList");
    }

    @FXML
    public void handleOnOpen() throws Exception {
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
            screenController.activate("home");
            return;
        }

        this.uuid = (UUID) screenController.getContext("selectedAuditoriumId");
        currentAuditorium = auditoriumService.getAuditoriumById(uuid);

        auditoriumNameField.setText(currentAuditorium.getName());
        auditoriumTypeField.setText(currentAuditorium.getType());
        auditoriumCapacityField.setText(String.valueOf(currentAuditorium.getCapacity()));
        auditoriumNoteField.setText(currentAuditorium.getNote());
    }
}



