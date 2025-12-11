package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.SeatStatusDTO;
import com.github.jimtrung.theater.model.Seat;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Ticket;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import com.github.jimtrung.theater.service.TicketService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.io.InputStream;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BookedTicketController {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;

    @FXML
    private UserHeaderController userHeaderController;

    @FXML
    private CheckBox selectAllCheckBox;

    @FXML
    private javafx.scene.layout.FlowPane ticketListContainer;
    
    private TicketService ticketService;
    private ShowtimeService showtimeService;
    private MovieService movieService;
    private AuditoriumService auditoriumService;
    
    private Map<UUID, CheckBox> checkBoxes = new HashMap<>();
    private List<Ticket> loadedTickets = new ArrayList<>();

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void setTicketService(TicketService s) { this.ticketService = s; }
    public void setShowtimeService(ShowtimeService s) { this.showtimeService = s; }
    public void setMovieService(MovieService s) { this.movieService = s; }
    public void setAuditoriumService(AuditoriumService s) { this.auditoriumService = s; }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();
        loadTickets();
    }
    
    private void loadTickets() {
        ticketListContainer.getChildren().clear();
        checkBoxes.clear();
        loadedTickets.clear();
        if (selectAllCheckBox != null) selectAllCheckBox.setSelected(false);
        
        CompletableFuture.runAsync(() -> {
            try {
                List<Ticket> tickets = ticketService.getUserTickets();
                // User requirement: "act like a cart... tick the ticket we want to pay".
                // So likely show PENDING.
                List<Ticket> pending = tickets.stream()
                        .filter(t -> "PENDING".equals(t.getStatus()))
                        .collect(Collectors.toList());
                        
                loadedTickets.addAll(pending);
                
                // Group by Showtime to fetch details efficiently
                Set<UUID> showtimeIds = pending.stream().map(Ticket::getShowtimeId).collect(Collectors.toSet());
                Map<UUID, Showtime> showtimes = new HashMap<>();
                Map<UUID, Movie> movies = new HashMap<>();
                
                for (UUID sid : showtimeIds) {
                    // This is not efficient (N+1), but simple for now
                     Showtime st = showtimeService.getAllShowtimes().stream().filter(s -> s.getId().equals(sid)).findFirst().orElse(null);
                     if (st != null) {
                         showtimes.put(sid, st);
                         if (!movies.containsKey(st.getMovieId())) {
                             movies.put(st.getMovieId(), movieService.getMovieById(st.getMovieId()));
                         }
                     }
                }
                
                // Fetch Auditoriums and Seats
                Map<UUID, Auditorium> auditoriums = new HashMap<>();
                Map<UUID, Seat> seatMap = new HashMap<>();
                
                for (UUID sid : showtimeIds) {
                    Showtime st = showtimes.get(sid);
                    if (st != null) {
                         if (!auditoriums.containsKey(st.getAuditoriumId())) {
                             auditoriums.put(st.getAuditoriumId(), auditoriumService.getAuditoriumById(st.getAuditoriumId()));
                         }
                         
                         try {
                             List<SeatStatusDTO> statusList = showtimeService.getSeatsWithStatus(sid);
                             if (statusList != null) {
                                 for (SeatStatusDTO dto : statusList) {
                                     seatMap.put(dto.seat().getId(), dto.seat());
                                 }
                             }
                         } catch (Exception e) { e.printStackTrace(); }
                    }
                }

                Platform.runLater(() -> {
                    if (pending.isEmpty()) {
                        ticketListContainer.getChildren().add(new Label("Không có vé nào chưa thanh toán."));
                        return;
                    }

                    for (Ticket t : pending) {
                        HBox row = new HBox(10);
                        row.setAlignment(Pos.CENTER_LEFT);
                        // Dark background style, compact width
                        row.setPrefWidth(320);
                        row.setMaxWidth(320);
                        row.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 0);");
                        
                        CheckBox cb = new CheckBox();
                        cb.setStyle("-fx-text-fill: white;"); 
                        checkBoxes.put(t.getId(), cb);
                        
                        Showtime st = showtimes.get(t.getShowtimeId());
                        Movie mv = st != null ? movies.get(st.getMovieId()) : null;
                        Auditorium au = st != null ? auditoriums.get(st.getAuditoriumId()) : null;
                        Seat seat = seatMap.get(t.getSeatId());

                        // Image - Smaller
                        ImageView poster = new ImageView();
                        poster.setFitWidth(60);
                        poster.setFitHeight(90);
                        poster.setPreserveRatio(true);
                        
                        if (mv != null) {
                             String imagePath = "/images/movies/" + mv.getId() + ".jpg";
                             try (InputStream is = getClass().getResourceAsStream(imagePath)) {
                                 if (is != null) poster.setImage(new Image(is));
                                 else poster.setImage(new Image(getClass().getResourceAsStream("/images/movies/not_found.png")));
                             } catch (Exception e) {
                                 try { poster.setImage(new Image(getClass().getResourceAsStream("/images/movies/not_found.png"))); } catch (Exception _) {}
                             }
                        }

                        // Info VBox - Compact
                        VBox infoBox = new VBox(2); // Tighter spacing
                        infoBox.setAlignment(Pos.CENTER_LEFT);
                        HBox.setHgrow(infoBox, Priority.ALWAYS); // Ensure text wraps or fills
                        
                        Label lblMovie = new Label(mv != null ? mv.getName() : "Unknown Movie");
                        lblMovie.setWrapText(true);
                        lblMovie.setMaxWidth(200);
                        lblMovie.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;"); // Smaller Header
                        
                        Label lblDate = new Label(st != null ? "Ngày: " + st.getStartTime().toLocalDate() + " | Giờ: " + st.getStartTime().toLocalTime() : "");
                        lblDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;"); // Compact text
                        
                        String seatInfo = seat != null ? (seat.getRow() + seat.getNumber()) : "Unknown";
                        Label lblSeat = new Label("Phòng: " + (au != null ? au.getName() : "Unknown") + " | Ghế: " + seatInfo);
                        lblSeat.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");
                        
                        Label lblPrice = new Label("Giá: " + t.getPrice() + " VNĐ");
                        lblPrice.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

                        infoBox.getChildren().addAll(lblMovie, lblDate, lblSeat, lblPrice);
                        
                        row.getChildren().addAll(cb, poster, infoBox);
                        ticketListContainer.getChildren().add(row);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleSelectAll() {
        boolean selected = selectAllCheckBox.isSelected();
        for (CheckBox cb : checkBoxes.values()) {
            cb.setSelected(selected);
        }
    }

    @FXML
    private void handlePay() {
        List<Ticket> selected = new ArrayList<>();
        for (Map.Entry<UUID, CheckBox> entry : checkBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                loadedTickets.stream().filter(t -> t.getId().equals(entry.getKey())).findFirst().ifPresent(selected::add);
            }
        }
        
        if (selected.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Vui lòng chọn vé để thanh toán!");
            alert.showAndWait();
            return;
        }

        // Logic check: Allow paying for MULTIPLE showtimes? The PayPage is designed for ONE.
        // For now, take first ticket's showtime details.
        
        Ticket first = selected.get(0);
        
        // This part is tricky because PayPage expects "cart" map with all details.
        // I need to fetch details again or cache them.
        
        CompletableFuture.runAsync(() -> {
             try {
                // Fetch details for the first ticket to populate PayPage header
                // Note: If user selects multiple movies, PayPage will show info of the first one but total price of all.
                Showtime st = showtimeService.getAllShowtimes().stream().filter(s -> s.getId().equals(first.getShowtimeId())).findFirst().orElse(null);
                Movie mv = st != null ? movieService.getMovieById(st.getMovieId()) : null;
                Auditorium au = st != null ? auditoriumService.getAuditoriumById(st.getAuditoriumId()) : null;
                
                Platform.runLater(() -> {
                    Map<String, Object> cart = new HashMap<>();
                    cart.put("showtimeId", first.getShowtimeId()); // Uses first showtime ID
                    cart.put("movieName", mv != null ? mv.getName() : "Nhiều phim");
                    cart.put("auditoriumName", au != null ? au.getName() : "...");
                    cart.put("startTime", st != null ? st.getStartTime().toLocalTime() : "");
                    cart.put("endTime", st != null ? st.getEndTime().toLocalTime() : "");
                    cart.put("date", st != null ? st.getStartTime().toLocalDate() : "");
                    
                    cart.put("seatNames", "Đã chọn " + selected.size() + " vé");
                    
                    List<UUID> ticketIds = selected.stream().map(Ticket::getId).collect(Collectors.toList());
                    cart.put("ticketIds", ticketIds);
                    
                    long price = selected.stream().mapToLong(Ticket::getPrice).sum();
                    cart.put("totalPrice", price);
                    
                    screenController.setContext("cart", cart);
                    screenController.activate("payPage");
                });
             } catch (Exception e) { e.printStackTrace(); }
        });
    }

    @FXML
    private void handleDelete() {
        // Not implemented yet
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Tính năng xóa vé chưa được hỗ trợ.");
        alert.showAndWait();
    }
}
