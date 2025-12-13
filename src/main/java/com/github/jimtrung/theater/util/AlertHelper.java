package com.github.jimtrung.theater.util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertHelper {

    public static void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    public static Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
