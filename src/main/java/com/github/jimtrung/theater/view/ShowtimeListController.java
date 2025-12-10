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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShowtimeListController {
    private ScreenController screenController;
    private AuthService authService;
    private ShowtimeService showtimeService;
    private MovieService movieService;
    private AuditoriumService auditoriumService;

    private ObservableList<Showtime> showtimeList;
    private Map<UUID, String> movieNames = new HashMap<>();
    private Map<UUID, String> auditoriumNames = new HashMap<>();

    @FXML private TableView<Showtime> showtimeTable;
    @FXML private TableColumn<Showtime, String> movieColumn;
    @FXML private TableColumn<Showtime, String> auditoriumColumn;
    @FXML private TableColumn<Showtime, String> startColumn;
    @FXML private TableColumn<Showtime, String> finishColumn;
    @FXML private TableColumn<Showtime, String> dateColumn;
    @FXML private TableColumn<Showtime, Integer> quantityColumn; // Not yet

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
            new SimpleStringProperty(movieNames.getOrDefault(cellData.getValue().getMovieId(), "Unknown")));
        
        auditoriumColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(auditoriumNames.getOrDefault(cellData.getValue().getAuditoriumId(), "Unknown")));

        startColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStartTime().withOffsetSameInstant(offset).format(timeFormatter)));

        finishColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEndTime().withOffsetSameInstant(offset).format(timeFormatter)));

        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStartTime().withOffsetSameInstant(offset).format(dateFormatter)));
            
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
            showtimeList = FXCollections.observableArrayList(showtimes);
            showtimeTable.setItems(showtimeList);
            
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
        ShowtimeInformationController controller = (ShowtimeInformationController) screenController.getController("showtimeInformation");
        if (controller != null) {
            controller.setUuid(id);
            controller.setShowtimeListController(this);
            screenController.activate("showtimeInformation");
        }
    }
    
    @FXML
    public void handleDeleteAllButton() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete all showtimes?");

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
