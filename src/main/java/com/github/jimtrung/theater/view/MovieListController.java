package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.service.ActorService;
import com.github.jimtrung.theater.service.DirectorService;
import com.github.jimtrung.theater.service.MovieActorService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class MovieListController {

    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private MovieService movieService;
    private ObservableList<Movie> movieList;
    private UUID uuid;
    private ActorService actorService;
    private DirectorService directorService;
    private MovieActorService movieActorService;

    @FXML
    private TableView<Movie> movieTable;

    @FXML
    private TableColumn<Movie, String> nameColumn;
    @FXML
    private TableColumn<Movie, String> directorColumn;
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

    @FXML
    private Button closeBtn;
    @FXML
    private Button addMovieBtn;
    @FXML
    private Button deleteAllBtn;

    /* ===== SETTERS ===== */
    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }

    public void setDirectorService(DirectorService directorService) {
        this.directorService = directorService;
    }

    public void setMovieActorService(MovieActorService movieActorService) {
        this.movieActorService = movieActorService;
    }

    public TableView<Movie> getMovieTable() {
        return movieTable;
    }

    /* ===== INITIALIZE ===== */
    public void handleOnOpen() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // --- Columns ---
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        directorColumn.setCellValueFactory(cellData -> {
            UUID directorId = cellData.getValue().getDirectorId();
            return new javafx.beans.property.SimpleStringProperty(directorId != null ? directorId.toString() : "");
        });
        genresColumn.setCellValueFactory(cellData -> {
            var genres = cellData.getValue().getGenres();
            String genresStr = genres != null ? String.join(", ", genres) : "";
            return new javafx.beans.property.SimpleStringProperty(genresStr);
        });
        premiereColumn.setCellValueFactory(cellData -> {
            var premiere = cellData.getValue().getPremiere();
            String dateStr = premiere != null ? premiere.format(formatter) : "";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        ratedColumn.setCellValueFactory(new PropertyValueFactory<>("rated"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));

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

    /* ===== BUTTON HANDLERS ===== */
    public void handleAddMovie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_movie_dialog.fxml"));
            screenController.addScreen("addMovie", loader);
            AddMovieController addMovieController = loader.getController();
            addMovieController.setScreenController(screenController);
            addMovieController.setMovieService(movieService);
            addMovieController.setAuthTokenUtil(authTokenUtil);
            addMovieController.setMovieListController(this);
            addMovieController.setActorService(actorService);
            addMovieController.setDirectorService(directorService);
            addMovieController.setMovieActorService(movieActorService);
            screenController.activate("addMovie");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleClickItem(UUID id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/movie_information.fxml"));
            screenController.addScreen("movieInformation", loader);
            MovieInformationController movieInformationController = loader.getController();
            movieInformationController.setScreenController(screenController);
            movieInformationController.setMovieService(movieService);
            movieInformationController.setAuthTokenUtil(authTokenUtil);
            movieInformationController.setMovieListController(this);
            movieInformationController.setUuid(id);
            screenController.activate("movieInformation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteAllMovie() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete all movies?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                movieService.deleteAllMovies();
                refreshData();
                System.out.println("🗑️ All movies deleted successfully!");
            } else {
                System.out.println("❌ Delete all operation cancelled!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCloseBtn() {
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
