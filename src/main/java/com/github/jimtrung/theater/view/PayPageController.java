package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.TicketService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;

public class PayPageController {
    private ScreenController screenController;
    private AuthService authService;
    private TicketService ticketService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @FXML private Label ticketMovieName, ticketAuditoriumName, ticketStartTime, ticketEndTime, ticketShowDate, ticketSeatName, ticketPrice;
    @FXML private ImageView ticketQRCode;

    public void handleOnOpen() {
        if (!authService.isLoggedIn()) {
            screenController.activate("signin");
            return;
        }
        
        // Load data from context
        try {
            Map<String, Object> cart = (Map<String, Object>) screenController.getContext("cart");
             if (cart != null) {
                 ticketMovieName.setText((String) cart.get("movieName"));
                 ticketAuditoriumName.setText((String) cart.get("auditoriumName"));
                 ticketStartTime.setText(cart.get("startTime").toString());
                 ticketEndTime.setText(cart.get("endTime").toString());
                 ticketShowDate.setText(cart.get("date").toString());
                 ticketSeatName.setText((String) cart.get("seatNames"));
                 ticketPrice.setText(cart.get("totalPrice") + " VNĐ");
             }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    @FXML
    public void confirmPayment() {
        try {
             @SuppressWarnings("unchecked")
             java.util.Map<String, Object> cart = (java.util.Map<String, Object>) screenController.getContext("cart");
             if (cart == null) return;

             java.util.UUID showtimeId = (java.util.UUID) cart.get("showtimeId");
             @SuppressWarnings("unchecked")
             java.util.List<java.util.UUID> seatIds = (java.util.List<java.util.UUID>) cart.get("seatIds");
             
             com.github.jimtrung.theater.dto.BookingRequest req = new com.github.jimtrung.theater.dto.BookingRequest(showtimeId, seatIds);

             // Run booking asynchronously
             CompletableFuture.runAsync(() -> {
                 try {
                     ticketService.bookTickets(req);
                     
                     Platform.runLater(() -> {
                         Alert alert = new Alert(Alert.AlertType.INFORMATION);
                         alert.setContentText("Đặt vé thành công!");
                         alert.showAndWait();
                         screenController.activate("home");
                     });
                 } catch (Exception e) {
                     e.printStackTrace();
                     Platform.runLater(() -> {
                         Alert alert = new Alert(Alert.AlertType.ERROR);
                         alert.setContentText("Lỗi: " + e.getMessage());
                         alert.showAndWait();
                     });
                 }
             });

        } catch (Exception e) {
             e.printStackTrace();
             javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
             alert.setContentText("Lỗi: " + e.getMessage());
             alert.showAndWait();
        }
    }

    @FXML
    private void handleBackButton() {
        screenController.activate("bookTicket");
    }    
}
