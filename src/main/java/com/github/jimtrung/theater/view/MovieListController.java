package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class MovieListController {

    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private MovieService movieService;
    private ObservableList<Movie> movieList;


    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
        refreshData();
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    @FXML
    private TableView movieTable;

    @FXML
    private TableColumn<Movie, String> nameColumn;

    @FXML
    private TableColumn<Movie, String> authorColumn;

    @FXML
    private TableColumn<Movie, String> genreColumn;

    @FXML
    private TableColumn<Movie, Integer> ageLimit;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genres"));
        ageLimit.setCellValueFactory(new PropertyValueFactory<>("ageLimit"));

        movieList = FXCollections.observableArrayList();
        movieTable.setItems(movieList);
    }


    public void refreshData() {
        if(movieService != null && movieList != null) {
            try {
                movieList.setAll(movieService.getAllMovies());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private Button closeBtn;

    @FXML
    private Button addMovieBtn;

    @FXML
    private Button deleteAllBtn;

    public void handleCloseBtn() {
        screenController.activate("homePageManager");
    }

    public void handleAddMovie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_movie_dialog.fxml"));
            screenController.addScreen("addMovie", loader);

            AddMovieController addMovieController = loader.getController();
            addMovieController.setScreenController(screenController);
            addMovieController.setMovieService(movieService);
            addMovieController.setAuthTokenUtil(authTokenUtil);
            addMovieController.setMovieListController(this);

            screenController.activate("addMovie");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteAllMovie() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete all movie ?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            movieService.deleteAllMovies();
            refreshData();
        }
        else {
            System.out.println("Delete all operation cancelled !");
        }
    }
}
