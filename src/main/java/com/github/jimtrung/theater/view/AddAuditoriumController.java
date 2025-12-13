package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.util.AlertHelper;
import com.github.jimtrung.theater.util.NullCheckerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.OffsetDateTime;

public class AddAuditoriumController {
    private ScreenController screenController;
    private AuthService authService;
    private AuditoriumService auditoriumService;


    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }



    @FXML private TextField auditoriumNameField;
    @FXML private TextField auditoriumTypeField;
    @FXML private TextField auditoriumCapacityField;
    @FXML private TextArea auditoriumNoteField;

    public void handleOnOpen() {
        // Kiểm tra thông tin người dùng
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
            screenController.activate("home");
        }
    }

    @FXML
    private void handleBackButton() {
        screenController.activate("auditoriumList");
    }

    @FXML
    public void handleAddAuditoriumButton() {
        // Lấy giữ liệu từ .fxml
        Auditorium auditorium = new Auditorium();
        auditorium.setName(auditoriumNameField.getText().trim());
        auditorium.setType(auditoriumTypeField.getText().trim());
        auditorium.setNote(auditoriumNoteField.getText().trim());
        auditorium.setCapacity(Integer.valueOf(auditoriumCapacityField.getText().trim()));
        auditorium.setCreatedAt(OffsetDateTime.now());
        auditorium.setUpdatedAt(OffsetDateTime.now());
        
        // Kiểm tra xem nếu có field nào chưa nhập thì alert
        try {
            if (NullCheckerUtil.hasNullField(auditorium)) {
                AlertHelper.showError("Lỗi nhập liệu", "Vui lòng nhập đầy đủ thông tin");
            }
            else {
                auditoriumService.insertAuditorium(auditorium);
                AuditoriumListController listController = (AuditoriumListController) screenController.getController("auditoriumList");
                if (listController != null) {
                    listController.refreshData();
                }
                screenController.activate("auditoriumList");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
