package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.SeatStatusDTO;
import com.github.jimtrung.theater.model.Seat;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.ShowtimeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookTicketController {
    private ScreenController screenController;
    private AuthService authService;
    private ShowtimeService showtimeService;
    private UUID showtimeId;
    private Set<Seat> selectedSeats = new HashSet<>();

    @FXML private FlowPane seatContainer;
    @FXML private Label selectedSeatsLabel;
    
    @FXML private TextField ticketMovieName;
    @FXML private TextField ticketAuditoriumName;
    @FXML private TextField ticketDate;
    @FXML private TextField ticketStartTime;
    @FXML private TextField ticketEndTime;
    @FXML private TextField ticketPrice;


    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setShowtimeService(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }
    
    private com.github.jimtrung.theater.service.MovieService movieService;
    private com.github.jimtrung.theater.service.AuditoriumService auditoriumService;
    
    public void setMovieService(com.github.jimtrung.theater.service.MovieService s) { this.movieService = s; }
    public void setAuditoriumService(com.github.jimtrung.theater.service.AuditoriumService s) { this.auditoriumService = s; }

    private com.github.jimtrung.theater.model.Showtime currentShowtime;
    private com.github.jimtrung.theater.model.Movie currentMovie;
    private com.github.jimtrung.theater.model.Auditorium currentAuditorium;


    public void setShowtimeId(UUID showtimeId) {
        this.showtimeId = showtimeId;
    }

    public void handleOnOpen() {
        if (showtimeId == null) {
             try {
                showtimeId = (UUID) screenController.getContext("selectedShowtimeId");
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }

        if (showtimeId != null) {
            loadSeats();
            loadDetails();
        } else {
            System.err.println("Showtime ID is null in BookTicketController");
        }
    }
    
    @FXML
    private void handleBackButton() {
        screenController.activate("showtimePage");
    }

    private void loadSeats() {
        seatContainer.getChildren().clear();
        selectedSeats.clear();
        updateSelectedLabel();
        
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Loading seats for showtime: " + showtimeId);
                List<SeatStatusDTO> seats = showtimeService.getSeatsWithStatus(showtimeId);
                System.out.println("Seats loaded: " + (seats != null ? seats.size() : "null"));
                
                if (seats == null || seats.isEmpty()) return;

                seats.sort(Comparator.comparing((SeatStatusDTO d) -> d.seat().getRow())
                        .thenComparing(d -> d.seat().getNumber()));

                javafx.application.Platform.runLater(() -> {
                    try {
                        for (SeatStatusDTO dto : seats) {
                            Button btn = new Button(dto.seat().getRow() + dto.seat().getNumber());
                            btn.getStyleClass().add("seat-button"); // Base class

                            if (dto.isBooked()) {
                                btn.setDisable(true);
                                btn.getStyleClass().add("seat-booked");
                            } else {
                                btn.getStyleClass().add("seat-available");
                                btn.setOnAction(e -> toggleSeat(btn, dto.seat()));
                            }
                            seatContainer.getChildren().add(btn);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void toggleSeat(Button btn, Seat seat) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            btn.getStyleClass().removeAll("seat-selected");
            btn.getStyleClass().add("seat-available");
        } else {
            selectedSeats.add(seat);
            btn.getStyleClass().removeAll("seat-available");
            btn.getStyleClass().add("seat-selected");
        }
        updateSelectedLabel();
    }

    private void updateSelectedLabel() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Ghế đã chọn: Chưa có");
        } else {
            String txt = selectedSeats.stream()
                    .map(s -> s.getRow() + s.getNumber())
                    .sorted()
                    .collect(Collectors.joining(", "));
            selectedSeatsLabel.setText("Ghế đã chọn: " + txt);
        }
    }
    
    @FXML
    private void handleBookTicket() {
        if (selectedSeats.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setContentText("Vui lòng chọn ghế!");
            alert.showAndWait();
            return;
        }
        
        java.util.Map<String, Object> cart = new java.util.HashMap<>();
        cart.put("showtimeId", showtimeId);
        cart.put("movieName", currentMovie != null ? currentMovie.getName() : "Unknown");
        cart.put("auditoriumName", currentAuditorium != null ? currentAuditorium.getName() : "Unknown");
        cart.put("startTime", currentShowtime != null ? currentShowtime.getStartTime().toLocalTime() : "");
        cart.put("endTime", currentShowtime != null ? currentShowtime.getEndTime().toLocalTime() : "");
        cart.put("date", currentShowtime != null ? currentShowtime.getStartTime().toLocalDate() : "");
        
        String seatNames = selectedSeats.stream().map(s -> s.getRow() + s.getNumber()).sorted().collect(Collectors.joining(", "));
        cart.put("seatNames", seatNames);
        
        List<UUID> seatIds = selectedSeats.stream().map(Seat::getId).collect(Collectors.toList());
        cart.put("seatIds", seatIds);
        
        long price = selectedSeats.size() * 50000L; // Fixed price logic for now
        cart.put("totalPrice", price);
        
        screenController.setContext("cart", cart);
        screenController.activate("payPage");
    }

    private void loadDetails() {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<com.github.jimtrung.theater.model.Showtime> all = showtimeService.getAllShowtimes();
                currentShowtime = all.stream().filter(s -> s.getId().equals(showtimeId)).findFirst().orElse(null);
                
                if (currentShowtime != null) {
                    currentMovie = movieService.getMovieById(currentShowtime.getMovieId());
                    currentAuditorium = auditoriumService.getAuditoriumById(currentShowtime.getAuditoriumId());
                    
                    javafx.application.Platform.runLater(() -> {
                         if (currentMovie != null) ticketMovieName.setText(currentMovie.getName());
                         if (currentAuditorium != null) ticketAuditoriumName.setText(currentAuditorium.getName());
                         ticketStartTime.setText(currentShowtime.getStartTime().toLocalTime().toString());
                         ticketEndTime.setText(currentShowtime.getEndTime().toLocalTime().toString());
                         ticketDate.setText(currentShowtime.getStartTime().toLocalDate().toString());
                         ticketPrice.setText("50000 VNĐ / vé");
                    });
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}
