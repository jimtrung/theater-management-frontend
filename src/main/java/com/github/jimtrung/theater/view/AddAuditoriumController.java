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

        String name = auditoriumNameField.getText().trim();
        String type = auditoriumTypeField.getText().trim();
        String capacityText = auditoriumCapacityField.getText().trim();
        String note = auditoriumNoteField.getText().trim();

        // ===== 1. Check rỗng =====
        if (name.isEmpty() || type.isEmpty() || capacityText.isEmpty()) {
            AlertHelper.showError(
                    "Lỗi nhập liệu",
                    "Vui lòng nhập đầy đủ thông tin (Tên, Loại, Sức chứa)"
            );
            return;
        }

        // ===== 2. Check số =====
        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException e) {
            AlertHelper.showError(
                    "Lỗi nhập liệu",
                    "Sức chứa phải là một số nguyên hợp lệ"
            );
            return;
        }

        // ===== 3. Check số dương =====
        if (capacity <= 0) {
            AlertHelper.showError(
                    "Lỗi nhập liệu",
                    "Sức chứa phải lớn hơn 0"
            );
            return;
        }

        // ===== 4. Hợp lệ → tạo object =====
        Auditorium auditorium = new Auditorium();
        auditorium.setName(name);
        auditorium.setType(type);
        auditorium.setCapacity(capacity);
        auditorium.setNote(note);
        auditorium.setCreatedAt(OffsetDateTime.now());
        auditorium.setUpdatedAt(OffsetDateTime.now());

        try {
            auditoriumService.insertAuditorium(auditorium);

            AuditoriumListController listController =
                    (AuditoriumListController) screenController.getController("auditoriumList");

            if (listController != null) {
                listController.refreshData();
            }

            screenController.activate("auditoriumList");

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError(
                    "Lỗi hệ thống",
                    "Không thể thêm phòng chiếu, vui lòng thử lại"
            );
        }
    }

}

