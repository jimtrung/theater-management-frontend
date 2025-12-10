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
        movieTable.setItems(movieList);

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
    public void handleDeleteAllButton() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete all movies?");

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
