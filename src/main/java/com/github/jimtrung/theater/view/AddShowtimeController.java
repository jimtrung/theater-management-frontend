package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AddShowtimeController {

    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
//    private AuditoriumService auditoriumService;
    private ShowtimeListController showtimeListController;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public void setShowtimeListController(ShowtimeListController showtimeListController) {
        this.showtimeListController = showtimeListController;
    }

    @FXML
    private Button showMoviesBtn;

    @FXML
    private ListView<String> movieListView;

    @FXML
    private TextField selectedMovieField;

    @FXML
    private void initialize() {
        // Khởi tạo danh sách phim
        movieListView.getItems().addAll(
                "Inception",
                "Interstellar",
                "The Dark Knight",
                "Avatar",
                "Titanic",
                "Avengers: Endgame"
        );

        // Khi click chọn phim → hiện trong TextField
        movieListView.setOnMouseClicked(e -> {
            String selectedMovie = movieListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                selectedMovieField.setText(selectedMovie);
            }
        });
    }

    @FXML
    private void handleShowMovies() {
        // Ẩn/hiện danh sách
        movieListView.setVisible(!movieListView.isVisible());
    }
}
