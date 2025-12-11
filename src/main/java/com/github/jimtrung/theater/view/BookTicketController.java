package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.dto.BookingRequest;
import com.github.jimtrung.theater.dto.SeatStatusDTO;
import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Seat;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.model.Ticket;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import com.github.jimtrung.theater.service.TicketService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BookTicketController {
    private ScreenController screenController;
    private AuthService authService;
    private ShowtimeService showtimeService;
    private UUID showtimeId;
    private Set<Seat> selectedSeats = new HashSet<>();

    @FXML private FlowPane seatContainer;
    @FXML private Label selectedSeatsLabel;
    @FXML private ImageView moviePoster;
    
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
    
    private MovieService movieService;
    private AuditoriumService auditoriumService;
    private TicketService ticketService;
    
    public void setMovieService(MovieService s) { this.movieService = s; }
    public void setAuditoriumService(AuditoriumService s) { this.auditoriumService = s; }
    public void setTicketService(TicketService s) { this.ticketService = s; }

    private Showtime currentShowtime;
    private Movie currentMovie;
    private Auditorium currentAuditorium;


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

        loadSeats();
        loadDetails();
    }
    
    @FXML
    private void handleBackButton() {
        screenController.activate("showtimePage");
    }

    private void loadSeats() {
        seatContainer.getChildren().clear();
        selectedSeats.clear();
        updateSelectedLabel();
        
        CompletableFuture.runAsync(() -> {
            try {
                List<SeatStatusDTO> seats = showtimeService.getSeatsWithStatus(showtimeId);

                if (seats == null || seats.isEmpty()) return;

                seats.sort(Comparator.comparing((SeatStatusDTO d) -> d.seat().getRow())
                        .thenComparing(d -> d.seat().getNumber()));

                Platform.runLater(() -> {
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Vui lòng chọn ghế!");
            alert.showAndWait();
            return;
        }

        // Prompt Pay Now or Pay Later
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đặt vé");
        alert.setHeaderText("Bạn muốn thanh toán ngay hay thanh toán sau?");
        alert.setContentText("Chọn 'Thanh toán ngay' để chuyển đến trang thanh toán.\nChọn 'Thanh toán sau' để lưu vé vào danh sách vé đã đặt (Cart).");

        ButtonType buttonPayNow = new ButtonType("Thanh toán ngay");
        ButtonType buttonPayLater = new ButtonType("Thanh toán sau");
        ButtonType buttonCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonPayNow, buttonPayLater, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonCancel) {
            return;
        }

        try {
             List<UUID> seatIds = selectedSeats.stream().map(Seat::getId).collect(Collectors.toList());
             BookingRequest req = new BookingRequest(showtimeId, seatIds);

             // Create tickets (PENDING)
             List<Ticket> tickets = ticketService.bookTickets(req);
             
             if (result.get() == buttonPayLater) {
                 Alert success = new Alert(Alert.AlertType.INFORMATION);
                 success.setContentText("Đặt vé thành công! Vé đã được lưu vào 'Vé đã đặt'.");
                 success.showAndWait();
                 screenController.activate("bookedTicket");
             } else {
                 // Pay Now
                 Map<String, Object> cart = new HashMap<>();
                 cart.put("showtimeId", showtimeId);
                 cart.put("movieName", currentMovie != null ? currentMovie.getName() : "Không xác định");
                 cart.put("auditoriumName", currentAuditorium != null ? currentAuditorium.getName() : "Không xác định");
                 cart.put("startTime", currentShowtime != null ? currentShowtime.getStartTime().toLocalTime() : "");
                 cart.put("endTime", currentShowtime != null ? currentShowtime.getEndTime().toLocalTime() : "");
                 cart.put("date", currentShowtime != null ? currentShowtime.getStartTime().toLocalDate() : "");
                 
                 String seatNames = selectedSeats.stream().map(s -> s.getRow() + s.getNumber()).sorted().collect(Collectors.joining(", "));
                 cart.put("seatNames", seatNames);
                 
                 // Pass ticketIds for payment
                 List<UUID> ticketIds = tickets.stream().map(Ticket::getId).collect(Collectors.toList());
                 cart.put("ticketIds", ticketIds);
                 
                 long price = tickets.stream().mapToLong(Ticket::getPrice).sum();
                 cart.put("totalPrice", price);
                 
                 screenController.setContext("cart", cart);
                 screenController.activate("payPage");
             }

        } catch (Exception e) {
             e.printStackTrace();
             Alert error = new Alert(Alert.AlertType.ERROR);
             error.setContentText("Đặt vé thất bại: " + e.getMessage());
             error.showAndWait();
        }
    }

    private void loadDetails() {
        CompletableFuture.runAsync(() -> {
            try {
                List<Showtime> all = showtimeService.getAllShowtimes();
                currentShowtime = all.stream().filter(s -> s.getId().equals(showtimeId)).findFirst().orElse(null);
                
                if (currentShowtime != null) {
                    currentMovie = movieService.getMovieById(currentShowtime.getMovieId());
                    currentAuditorium = auditoriumService.getAuditoriumById(currentShowtime.getAuditoriumId());
                    
                     Platform.runLater(() -> {
                         if (currentMovie != null) {
                             ticketMovieName.setText(currentMovie.getName());
                             try {
                                 moviePoster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/movies/" + currentMovie.getId() + ".jpg"))));
                             } catch (Exception e) {
                                 try {
                                     moviePoster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/movies/not_found.png"))));
                                 } catch (Exception ex) {
                                    try {
                                        moviePoster.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cat.jpg"))));
                                    } catch (Exception _) {}
                                 } 
                             }
                         }
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
