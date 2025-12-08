package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.model.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AddShowtimeController {
    private ScreenController screenController;
    private AuthService authService;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private ListView<String> movieListView;

    @FXML
    private TextField selectedMovieField;

    public void handleOnOpen() {
        User user = null;
        try {
            user = (com.github.jimtrung.theater.model.User) authService.getUser();
        } catch (Exception ignored) { }

        if (user == null || user.getRole() != UserRole.administrator) {
            screenController.activate("home");
        }

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

    @FXML
    private void handleBackButton() {
        screenController.activate("showtimeList");
    }

    @FXML
    private void handleAddShowtimeButton() {
        // TODO: Implement this
    }
}
