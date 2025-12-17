package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.Showtime;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.service.ShowtimeService;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.github.jimtrung.theater.dto.ShowtimeRevenueDTO;

public class ShowtimeListController {
    private ScreenController screenController;
    private AuthService authService;
    private ShowtimeService showtimeService;
    private MovieService movieService;
    private AuditoriumService auditoriumService;

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

    @FXML private TableView<Showtime> showtimeTable;
    @FXML private TableColumn<Showtime, String> movieColumn;
    @FXML private TableColumn<Showtime, String> auditoriumColumn;
    @FXML private TableColumn<Showtime, String> startColumn;
    @FXML private TableColumn<Showtime, String> finishColumn;
    @FXML private TableColumn<Showtime, String> dateColumn;
    @FXML private TableColumn<Showtime, Integer> quantityColumn;
    @FXML private TableColumn<Showtime, String> revenueColumn;

    private Map<UUID, Long> soldTicketsMap = new HashMap<>();
    private Map<UUID, Long> revenueMap = new HashMap<>();
    private ObservableList<Showtime> showtimeList;
    private Map<UUID, String> movieNames = new HashMap<>();
    private Map<UUID, String> auditoriumNames = new HashMap<>();

    public void handleOnOpen() {
        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
             screenController.activate("home");
             return;
        }

        initializeColumns();
        
        refreshData();
    }

    private void initializeColumns() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        ZoneOffset offset = ZoneOffset.ofHours(7);

        movieColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(movieNames.getOrDefault(cellData.getValue().getMovieId(), "Không xác định")));
        
        auditoriumColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(auditoriumNames.getOrDefault(cellData.getValue().getAuditoriumId(), "Không xác định")));

        startColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStartTime().withOffsetSameInstant(offset).format(timeFormatter)));

        finishColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEndTime().withOffsetSameInstant(offset).format(timeFormatter)));

        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStartTime().withOffsetSameInstant(offset).format(dateFormatter)));
            
        quantityColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(soldTicketsMap.getOrDefault(cellData.getValue().getId(), 0L).intValue()));
            
        revenueColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%,d VNĐ", revenueMap.getOrDefault(cellData.getValue().getId(), 0L))));

        showtimeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                handleClickItem(newSel.getId());
            }
        });
    }

    public void refreshData() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            movieNames = movies.stream().collect(Collectors.toMap(Movie::getId, Movie::getName));

            List<Auditorium> auditoriums = auditoriumService.getAllAuditoriums();
            auditoriumNames = auditoriums.stream().collect(Collectors.toMap(Auditorium::getId, Auditorium::getName));

            List<Showtime> showtimes = showtimeService.getAllShowtimes();
            
            // Fetch stats
            List<ShowtimeRevenueDTO> stats = showtimeService.getShowtimeStats();
            soldTicketsMap = stats.stream().collect(Collectors.toMap(ShowtimeRevenueDTO::showtimeId, ShowtimeRevenueDTO::soldTickets));
            revenueMap = stats.stream().collect(Collectors.toMap(ShowtimeRevenueDTO::showtimeId, ShowtimeRevenueDTO::revenue));

            showtimeList = FXCollections.observableArrayList(showtimes);
            
            // Wrap in SortedList
            javafx.collections.transformation.SortedList<Showtime> sortedData = new javafx.collections.transformation.SortedList<>(showtimeList);
            sortedData.comparatorProperty().bind(showtimeTable.comparatorProperty());
            
            showtimeTable.setItems(sortedData);
            
            showtimeTable.refresh();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddShowtimeButton() {
        screenController.activate("addShowtime");
    }

    @FXML
    public void handleCloseButton() {
        screenController.activate("homePageManager");
    }

    @FXML
    public void handleClickItem(UUID id) {
        screenController.setContext("selectedShowtimeId", id);
        screenController.activate("showtimeInformation");
    }
    
    @FXML
    public void handleDeleteAllButton() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa tất cả các suất chiếu không?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                showtimeService.deleteAllShowtimes();
                refreshData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
