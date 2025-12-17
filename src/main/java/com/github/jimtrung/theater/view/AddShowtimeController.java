package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.util.NullCheckerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import com.github.jimtrung.theater.util.AlertHelper;
import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AddShowtimeController {
    private ScreenController screenController;
    private AuthService authService;
    private MovieService movieService;
    private AuditoriumService auditoriumService;
    private ShowtimeService showtimeService;


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

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void setShowtimeService(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }



    public void handleOnOpen() {
        // Kiểm tra thông tin người dùng
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
            screenController.activate("home");
            return;
        }

        // Clear thông tin cũ và load thông tin mới
        clearFields();
        loadMovies();
        loadAuditoriums();
        loadTimes();
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
            movieListView.setCellFactory(param -> new ListCell<>() {
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
            auditoriumListView.setCellFactory(param -> new ListCell<>() {
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
    private void handleBackButton() {
        screenController.activate("showtimeList");
    }

    @FXML
    private void handleAddShowtimeButton() {
        try {
            ZoneOffset offset = ZoneOffset.ofHours(7);
            LocalTime startTime = LocalTime.parse(startTimePicker.getValue());
            LocalTime endTime = LocalTime.parse(endTimePicker.getValue());
            
            if (!endTime.isAfter(startTime)) {
                AlertHelper.showError("Lỗi", "Thời gian kết thúc phải sau thời gian bắt đầu!");
                return;
            }

            if (showtimeDatePicker.getValue().isBefore(java.time.LocalDate.now())) {
                AlertHelper.showError("Lỗi", "Ngày chiếu không được ở trong quá khứ!");
                return;
            }
            if (showtimeDatePicker.getValue().isEqual(java.time.LocalDate.now())) {
                if (startTime.isBefore(LocalTime.now())) {
                    AlertHelper.showError("Lỗi", "Thời gian bắt đầu đã qua!");
                    return;
                }
            }

            OffsetDateTime startOdt = OffsetDateTime.of(showtimeDatePicker.getValue(), startTime, offset);
            OffsetDateTime endOdt = OffsetDateTime.of(showtimeDatePicker.getValue(), endTime, offset);

            Showtime showtime = new Showtime();
            showtime.setMovieId(selectedMovie.getId());
            showtime.setAuditoriumId(selectedAuditorium.getId());
            showtime.setStartTime(startOdt);
            showtime.setEndTime(endOdt);

            if (NullCheckerUtil.hasNullField(showtime)) {
                AlertHelper.showError("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            showtimeService.insertShowtime(showtime);
            ShowtimeListController listController = (ShowtimeListController) screenController.getController("showtimeList");
            if (listController != null) {
                listController.refreshData();
            }
            screenController.activate("showtimeList");
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Lỗi", "Lỗi thêm suất chiếu: " + e.getMessage());
        }
    }

    private void clearFields() {
        searchMovieField.clear();
        searchAuditoriumField.clear();
        showtimeDatePicker.setValue(null);
        startTimePicker.getSelectionModel().clearSelection();
        endTimePicker.getSelectionModel().clearSelection();
        selectedMovie = null;
        selectedAuditorium = null;
    }
}
