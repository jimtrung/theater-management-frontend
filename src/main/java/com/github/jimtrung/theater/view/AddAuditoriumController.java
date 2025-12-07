package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;

public class AddAuditoriumController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuditoriumService auditoriumService;
    private AuditoriumListController auditoriumListController;

    public void setAuditoriumListController(AuditoriumListController auditoriumListController) {
        this.auditoriumListController = auditoriumListController;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    private com.github.jimtrung.theater.service.AuthService authService;

    public void setAuthService(com.github.jimtrung.theater.service.AuthService authService) {
        this.authService = authService;
    }

    public void handleOnOpen() {
        com.github.jimtrung.theater.model.User user = null;
        try {
            user = (com.github.jimtrung.theater.model.User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
            screenController.activate("home");
        }
    }

    public void handleCloseButton() {
        screenController.activate("auditoriumList");
    }

    public void handleAddAuditoriumButtonClick() {
        Auditorium auditorium = new Auditorium();
        auditorium.setName(auditoriumNameField.getText().toString().trim());
        auditorium.setType(auditoriumTypeField.getText().toString().trim());
        auditorium.setNote(auditoriumNoteField.getText().toString().trim());

        if (isEmpty(auditoriumCapacityField)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter complete information");
            alert.showAndWait();
            return;
        }

        auditorium.setCapacity(Integer.valueOf(auditoriumCapacityField.getText().toString().trim()));
        try {
            if (hasNullField(auditorium)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter complete information");
                alert.showAndWait();
                return;
            }
            else {
                auditoriumService.insertAuditorium(auditorium);
                auditoriumListController.refreshData();
                screenController.activate("auditoriumList");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasNullField(Object obj) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getName().equalsIgnoreCase("id")) {
                    continue;
                }

                Object value = field.get(obj);
                if (value == null) {
                    System.out.println("[ERROR] Field '" + field.getName() + "' is null!");
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    @FXML
    private TextField auditoriumNameField;

    @FXML
    private TextField auditoriumTypeField;

    @FXML
    private TextField auditoriumCapacityField;

    @FXML
    private TextArea auditoriumNoteField;
}
