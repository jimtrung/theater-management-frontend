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

import com.github.jimtrung.theater.dto.ShowtimeRevenueDTO;

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
    @FXML private TableColumn<Showtime, Integer> quantityColumn;
    @FXML private TableColumn<Showtime, String> revenueColumn;

    private Map<UUID, Long> soldTicketsMap = new HashMap<>();
    private Map<UUID, Long> revenueMap = new HashMap<>();

    @FXML private TextField searchField;
    @FXML private DatePicker datePicker;
    
    // FilteredList
    private javafx.collections.transformation.FilteredList<Showtime> filteredData;

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
        
        // Listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());

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
            
            // Wrap in FilteredList
            filteredData = new javafx.collections.transformation.FilteredList<>(showtimeList, p -> true);
            
            // Wrap in SortedList
            javafx.collections.transformation.SortedList<Showtime> sortedData = new javafx.collections.transformation.SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(showtimeTable.comparatorProperty());
            
            showtimeTable.setItems(sortedData);
            
            // Force refresh columns
            showtimeTable.refresh();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateFilter() {
        filteredData.setPredicate(showtime -> {
            // 1. Search Text
            String lowerCaseFilter = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            if (!lowerCaseFilter.isEmpty()) {
                String mName = movieNames.getOrDefault(showtime.getMovieId(), "").toLowerCase();
                String aName = auditoriumNames.getOrDefault(showtime.getAuditoriumId(), "").toLowerCase();
                if (!mName.contains(lowerCaseFilter) && !aName.contains(lowerCaseFilter)) {
                    return false;
                }
            }
            
            // 2. Date
            if (datePicker.getValue() != null) {
                ZoneOffset offset = ZoneOffset.ofHours(7);
                if (!showtime.getStartTime().withOffsetSameInstant(offset).toLocalDate().isEqual(datePicker.getValue())) {
                    return false;
                }
            }
            
            return true;
        });
    }
    
    @FXML
    public void handleClearFilters() {
        searchField.setText("");
        datePicker.setValue(null);
    }
    
    @FXML
    public void handleDeleteFilteredButton() {
        if (filteredData == null || filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Không có lịch chiếu nào trong danh sách lọc để xóa.");
            alert.showAndWait();
            return;
        }

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa " + filteredData.size() + " lịch chiếu đang hiển thị không?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                List<Showtime> toDelete = new java.util.ArrayList<>(filteredData);
                for (Showtime s : toDelete) {
                    showtimeService.deleteShowtimeById(s.getId());
                }
                refreshData();
            }
        } catch (Exception e) {
            e.printStackTrace();
             Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi khi xóa: " + e.getMessage());
            error.showAndWait();
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
