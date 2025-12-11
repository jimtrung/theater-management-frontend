package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ShowtimeInformationController {
    private ScreenController screenController;
    private AuthService authService;
    private ShowtimeService showtimeService;
    private MovieService movieService;
    private AuditoriumService auditoriumService;
    private ShowtimeListController showtimeListController;

    private UUID uuid;
    private Showtime currentShowtime;
    private Movie selectedMovie;
    private Auditorium selectedAuditorium;

    @FXML private ListView<Movie> movieListView;
    @FXML private ListView<Auditorium> auditoriumListView;
    @FXML private TextField searchMovieField;
    @FXML private TextField searchAuditoriumField;
    @FXML private DatePicker showtimeDatePicker;
    @FXML private ComboBox<String> startTimePicker;
    @FXML private ComboBox<String> endTimePicker;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setShowtimeService(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void setShowtimeListController(ShowtimeListController showtimeListController) {
        this.showtimeListController = showtimeListController;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void handleOnOpen() {
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
             screenController.activate("home");
             return;
        }

        loadMovies();
        loadAuditoriums();
        loadTimes();
        loadShowtimeData();
    }

    private void loadShowtimeData() {
        try {
            currentShowtime = showtimeService.getShowtimeById(uuid);
            if (currentShowtime != null) {
                ZoneOffset offset = ZoneOffset.ofHours(7);
                OffsetDateTime startLocal = currentShowtime.getStartTime().withOffsetSameInstant(offset);
                OffsetDateTime endLocal = currentShowtime.getEndTime().withOffsetSameInstant(offset);

                // Set Date
                showtimeDatePicker.setValue(startLocal.toLocalDate());

                // Set Time
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                
                startTimePicker.setValue(startLocal.format(timeFormatter));
                endTimePicker.setValue(endLocal.format(timeFormatter));

                // Select Movie
                for (Movie m : movieListView.getItems()) {
                    if (m.getId().equals(currentShowtime.getMovieId())) {
                        movieListView.getSelectionModel().select(m);
                        movieListView.scrollTo(m);
                        selectedMovie = m;
                        break;
                    }
                }

                // Select Auditorium
                for (Auditorium a : auditoriumListView.getItems()) {
                    if (a.getId().equals(currentShowtime.getAuditoriumId())) {
                        auditoriumListView.getSelectionModel().select(a);
                        auditoriumListView.scrollTo(a);
                        selectedAuditorium = a;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu suất chiếu: " + e.getMessage());
        }
    }

    private void loadTimes() {
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                times.add(String.format("%02d:%02d", h, m));
            }
        }
        startTimePicker.setItems(times);
        endTimePicker.setItems(times);
    }

    private void loadMovies() {
        try {
            ObservableList<Movie> movies = FXCollections.observableArrayList(movieService.getAllMovies());
            FilteredList<Movie> filteredMovies = new FilteredList<>(movies, p -> true);
            
            movieListView.setItems(filteredMovies);
            movieListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Movie item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            searchMovieField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredMovies.setPredicate(movie -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return movie.getName().toLowerCase().contains(newVal.toLowerCase());
                });
            });

            movieListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                selectedMovie = newVal;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAuditoriums() {
        try {
            ObservableList<Auditorium> auditoriums = FXCollections.observableArrayList(auditoriumService.getAllAuditoriums());
            FilteredList<Auditorium> filteredAuditoriums = new FilteredList<>(auditoriums, p -> true);

            auditoriumListView.setItems(filteredAuditoriums);
            auditoriumListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Auditorium item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " (" + item.getType() + ")");
                    }
                }
            });

            searchAuditoriumField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredAuditoriums.setPredicate(auditorium -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return auditorium.getName().toLowerCase().contains(newVal.toLowerCase());
                });
            });

            auditoriumListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                selectedAuditorium = newVal;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackButton() {
        screenController.activate("showtimeList");
    }

    @FXML
    public void handleDeleteButton() {
        try {
            showtimeService.deleteShowtimeById(uuid);
            if (showtimeListController != null) {
                showtimeListController.refreshData();
            }
            screenController.activate("showtimeList");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi xóa suất chiếu: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdateShowtimeButton() {
        try {
            if (selectedMovie == null || selectedAuditorium == null || 
                showtimeDatePicker.getValue() == null || 
                startTimePicker.getValue() == null || endTimePicker.getValue() == null) {
                
                showAlert("Vui lòng điền đầy đủ thông tin!");
                return;
            }

            LocalTime startTime = LocalTime.parse(startTimePicker.getValue());
            LocalTime endTime = LocalTime.parse(endTimePicker.getValue());
            
            if (!endTime.isAfter(startTime)) {
                showAlert("Thời gian kết thúc phải sau thời gian bắt đầu!");
                return;
            }

            ZoneOffset offset = ZoneOffset.ofHours(7); // Assuming same offset as create

            OffsetDateTime startOdt = OffsetDateTime.of(showtimeDatePicker.getValue(), startTime, offset);
            OffsetDateTime endOdt = OffsetDateTime.of(showtimeDatePicker.getValue(), endTime, offset);

            currentShowtime.setMovieId(selectedMovie.getId());
            currentShowtime.setAuditoriumId(selectedAuditorium.getId());
            currentShowtime.setStartTime(startOdt);
            currentShowtime.setEndTime(endOdt);

            showtimeService.updateShowtime(uuid, currentShowtime);
            
            if (showtimeListController != null) {
                showtimeListController.refreshData();
            }
            
            screenController.activate("showtimeList");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật suất chiếu: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setContentText(message);
        alert.show();
    }
}
