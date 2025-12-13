package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.MovieGenre;
import com.github.jimtrung.theater.service.MovieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.github.jimtrung.theater.util.AlertHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.collections.transformation.SortedList;

public class MovieListController {

    private ScreenController screenController;
    private MovieService movieService;

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> nameColumn;
    @FXML private TableColumn<Movie, String> directorColumn;
    @FXML private TableColumn<Movie, String> actorsColumn;
    @FXML private TableColumn<Movie, String> genresColumn;
    @FXML private TableColumn<Movie, String> premiereColumn;
    @FXML private TableColumn<Movie, Integer> durationColumn;
    @FXML private TableColumn<Movie, Integer> ratedColumn;
    @FXML private TableColumn<Movie, String> languageColumn;

    private UUID uuid;
    private ObservableList<Movie> movieList;

    public TableView<Movie> getMovieTable() {
        return movieTable;
    }

    public void handleOnOpen() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // --- Columns ---
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        directorColumn.setCellValueFactory(new PropertyValueFactory<>("director"));
        actorsColumn.setCellValueFactory(cellData -> {
            var actors = cellData.getValue().getActors();
            String actorsStr = actors != null ? String.join(", ", actors) : "";
            return new SimpleStringProperty(actorsStr);
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
            return new SimpleStringProperty(genresStr);
        });
        premiereColumn.setCellValueFactory(cellData -> {
            var premiere = cellData.getValue().getPremiere();
            String dateStr = premiere != null ? premiere.format(formatter) : "";
            return new SimpleStringProperty(dateStr);
        });
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        ratedColumn.setCellValueFactory(new PropertyValueFactory<>("rated"));
        languageColumn.setCellValueFactory(cellData -> {
            var lang = cellData.getValue().getLanguage();
            return new SimpleStringProperty(lang != null ? lang.toVietnamese() : "");
        });

        movieList = FXCollections.observableArrayList();
        
        SortedList<Movie> sortedData = new SortedList<>(movieList);
        sortedData.comparatorProperty().bind(movieTable.comparatorProperty());
        
        movieTable.setItems(sortedData);

        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                uuid = newSel.getId();
                handleClickItem(uuid);
            }
        });

        refreshData();
    }
    
    @FXML
    public void handleAddMovieButton() {
        screenController.activate("addMovie");
    }

    @FXML
    public void handleClickItem(UUID id) {
        screenController.setContext("selectedMovieId", id);
        screenController.activate("movieInformation");
    }

    @FXML
    public void handleDeleteAllButton() {
        try {
            Optional<ButtonType> result = AlertHelper.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa tất cả phim không?");
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
