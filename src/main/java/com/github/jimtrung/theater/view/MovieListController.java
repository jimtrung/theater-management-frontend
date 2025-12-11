package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.MovieGenre;
import com.github.jimtrung.theater.service.MovieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovieListController {

    private ScreenController screenController;
    private MovieService movieService;
    private ObservableList<Movie> movieList;
    private UUID uuid;

    @FXML
    private TableView<Movie> movieTable;
    
    @FXML
    private TextField searchField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private TableColumn<Movie, String> nameColumn;
    @FXML
    private TableColumn<Movie, String> directorColumn;
    @FXML
    private TableColumn<Movie, String> actorsColumn;
    @FXML
    private TableColumn<Movie, String> genresColumn;
    @FXML
    private TableColumn<Movie, String> premiereColumn;
    @FXML
    private TableColumn<Movie, Integer> durationColumn;
    @FXML
    private TableColumn<Movie, Integer> ratedColumn;
    @FXML
    private TableColumn<Movie, String> languageColumn;
    
    // FilteredList for filtering
    private javafx.collections.transformation.FilteredList<Movie> filteredData;

    /* ===== SETTERS ===== */
    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public TableView<Movie> getMovieTable() {
        return movieTable;
    }

    /* ===== INITIALIZE ===== */
    public void handleOnOpen() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // --- Columns ---
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        directorColumn.setCellValueFactory(new PropertyValueFactory<>("director"));
        actorsColumn.setCellValueFactory(cellData -> {
            var actors = cellData.getValue().getActors();
            String actorsStr = actors != null ? String.join(", ", actors) : "";
            return new javafx.beans.property.SimpleStringProperty(actorsStr);
        });
        genresColumn.setCellValueFactory(cellData -> {
            var genres = cellData.getValue().getGenres();
            String genresStr = "";
            if (genres != null) {
                genresStr = genres.stream()
                        .map(s -> {
                            try {
                                return MovieGenre.valueOf(s).toVietnamese();
                            } catch (Exception e) {
                                return s;
                            }
                        })
                        .collect(Collectors.joining(", "));
            }
            return new javafx.beans.property.SimpleStringProperty(genresStr);
        });
        premiereColumn.setCellValueFactory(cellData -> {
            var premiere = cellData.getValue().getPremiere();
            String dateStr = premiere != null ? premiere.format(formatter) : "";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        ratedColumn.setCellValueFactory(new PropertyValueFactory<>("rated"));
        languageColumn.setCellValueFactory(cellData -> {
            var lang = cellData.getValue().getLanguage();
            return new javafx.beans.property.SimpleStringProperty(lang != null ? lang.toVietnamese() : "");
        });

        // --- Movie list ---
        movieList = FXCollections.observableArrayList();
        
        // Wrap in FilteredList
        filteredData = new javafx.collections.transformation.FilteredList<>(movieList, p -> true);
        
        // Wrap in SortedList (optional but good for sorting)
        javafx.collections.transformation.SortedList<Movie> sortedData = new javafx.collections.transformation.SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(movieTable.comparatorProperty());
        
        movieTable.setItems(sortedData);

        // --- Init Filters ---
        if (languageComboBox != null) {
             languageComboBox.setItems(FXCollections.observableArrayList(
                 java.util.Arrays.stream(com.github.jimtrung.theater.model.MovieLanguage.values())
                 .map(com.github.jimtrung.theater.model.MovieLanguage::toVietnamese)
                 .collect(Collectors.toList())
             ));
        }
        
        // Listeners for filters
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        if (datePicker != null) datePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        if (languageComboBox != null) languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // --- Row selection ---
        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                uuid = newSel.getId();
                System.out.println("🎬 Selected movie: " + newSel.getName() + " | ID: " + uuid);
                handleClickItem(uuid);
            }
        });

        refreshData();
    }
    
    private void updateFilter() {
        filteredData.setPredicate(movie -> {
            // 1. Search Text (Name or Director)
            String lowerCaseFilter = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            if (!lowerCaseFilter.isEmpty()) {
                boolean matchName = movie.getName() != null && movie.getName().toLowerCase().contains(lowerCaseFilter);
                boolean matchDirector = movie.getDirector() != null && movie.getDirector().toLowerCase().contains(lowerCaseFilter);
                if (!matchName && !matchDirector) {
                    return false; 
                }
            }
            
            // 2. Date (Exact match on Premiere Date)
            if (datePicker.getValue() != null) {
                if (movie.getPremiere() == null) return false;
                if (!movie.getPremiere().toLocalDate().isEqual(datePicker.getValue())) {
                    return false;
                }
            }
            
            // 3. Language
            if (languageComboBox.getValue() != null && !languageComboBox.getValue().isEmpty()) {
                String selectedLang = languageComboBox.getValue();
                if (movie.getLanguage() == null || !movie.getLanguage().toVietnamese().equals(selectedLang)) {
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
        languageComboBox.setValue(null);
    }

    @FXML
    public void handleAddMovieButton() {
        screenController.activate("addMovie");
    }

    @FXML
    public void handleClickItem(UUID id) {
        MovieInformationController controller = (MovieInformationController) screenController.getController("movieInformation");
        if (controller != null) {
            controller.setUuid(id);
            controller.setMovieListController(this);
        }
        screenController.activate("movieInformation");
    }

    @FXML
    public void handleDeleteFilteredButton() {
        if (filteredData == null || filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Không có phim nào trong danh sách lọc để xóa.");
            alert.showAndWait();
            return;
        }

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa " + filteredData.size() + " phim đang hiển thị không?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Create a copy of the list to avoid concurrent modification issues if the list updates live
                List<Movie> toDelete = new java.util.ArrayList<>(filteredData);
                for (Movie m : toDelete) {
                    movieService.deleteMovieById(m.getId());
                }
                refreshData();
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setContentText("Đã xóa các phim được lọc.");
                success.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi khi xóa: " + e.getMessage());
            error.showAndWait();
        }
    }

    @FXML
    public void handleDeleteAllButton() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa tất cả phim không?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                movieService.deleteAllMovies();
                refreshData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCloseButton() {
        screenController.activate("homePageManager");
    }

    /* ===== HELPERS ===== */
    public void refreshData() {
        if (movieService != null && movieList != null) {
            try {
                movieList.setAll(movieService.getAllMovies());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMovie(Movie updatedMovie) {
        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getId().equals(updatedMovie.getId())) {
                movieList.set(i, updatedMovie);
                break;
            }
        }
    }
}
