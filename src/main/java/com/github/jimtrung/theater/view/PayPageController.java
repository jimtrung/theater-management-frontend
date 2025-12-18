package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.BillRequest;
import com.github.jimtrung.theater.dto.BookingRequest;
import com.github.jimtrung.theater.model.Ticket;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.BillService;
import com.github.jimtrung.theater.service.TicketService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import com.github.jimtrung.theater.util.AlertHelper;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PayPageController {
    private ScreenController screenController;
    private AuthService authService;
    private TicketService ticketService;
    private BillService billService;
    private AuthTokenUtil authTokenUtil;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) { this.authTokenUtil = authTokenUtil; }
    public void setBillService(BillService billService) { this.billService = billService; }

    @FXML private Label ticketMovieName, ticketAuditoriumName, ticketStartTime, ticketEndTime, ticketShowDate, ticketSeatName, ticketPrice;
    @FXML private ImageView ticketQRCode;

    public void handleOnOpen() {
        if (!authService.isLoggedIn()) {
            screenController.activate("signin");
            return;
        }
        
        try {
            @SuppressWarnings("unchecked")
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
             Map<String, Object> cart = (Map<String, Object>) screenController.getContext("cart");
             if (cart == null) return;

             UUID showtimeId = (UUID) cart.get("showtimeId");
             @SuppressWarnings("unchecked")
             List<UUID> seatIds = (List<UUID>) cart.get("seatIds");
             
             // Run booking/payment asynchronously
             CompletableFuture.runAsync(() -> {
                 try {

                     if (cart.containsKey("ticketIds")) {
                         // Pay for existing pending tickets
                         @SuppressWarnings("unchecked")
                         List<UUID> ticketIds = (List<UUID>) cart.get("ticketIds");
                         ticketService.payTickets(ticketIds);
                     } else {
                        // Fallback: Book then pay (should not be reached if BookTicketController is used)
                        BookingRequest req = new BookingRequest(showtimeId, seatIds);
                        List<Ticket> booked = ticketService.bookTickets(req);
                        List<UUID> ids = booked.stream().map(Ticket::getId).collect(Collectors.toList());
                        ticketService.payTickets(ids);
                     }
                     User user = (User) authService.getUser() ;
                     BillRequest billRequest = new BillRequest(user.getEmail(), ticketMovieName.getText(),
                             "7/10" , ticketShowDate.getText(), ticketStartTime.getText() ,
                             "NCC" , ticketSeatName.getText() , ticketAuditoriumName.getText(),
                             "Không có đồ đi kèm", UUID.randomUUID().toString(), ticketPrice.getText() , "Online");;
                     billService.createBill(user.getId(), billRequest);
                     
                     Platform.runLater(() -> {
                         AlertHelper.showInfo("Thông báo", "Thanh toán thành công! Vé đã được gửi đến email của bạn.");
                         screenController.activate("home");
                     });
                 } catch (Exception e) {
                     e.printStackTrace();
                     Platform.runLater(() -> {
                         AlertHelper.showError("Lỗi", "Lỗi: " + e.getMessage());
                     });
                 }
             });

        } catch (Exception e) {
             e.printStackTrace();
             e.printStackTrace();
             AlertHelper.showError("Lỗi", "Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButton() {
        screenController.goBack();   
    }    
}
