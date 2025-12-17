package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.model.MovieGenre;
import com.github.jimtrung.theater.model.MovieLanguage;
import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.model.UserRole;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AlertHelper;
import com.github.jimtrung.theater.util.NullCheckerUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import java.time.LocalDate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class AddMovieController {
    private ScreenController screenController;
    private AuthService authService;
    private MovieService movieService;




    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML private TextField movieNameField;
    @FXML private Spinner<Integer> movieDurationSpinner;
    @FXML private ComboBox<MovieLanguage> movieLanguageComboBox;
    @FXML private Spinner<Integer> movieRatedSpinner;
    @FXML private DatePicker moviePremiereDatePicker;
    @FXML private TextArea movieDescriptionField;
    @FXML private TextField movieDirectorField;
    @FXML private FlowPane actorsFlowPane;
    @FXML private TextField actorInputField;
    @FXML private ListView<MovieGenre> genreListView;
    @FXML private TextField searchGenreField;
    
    private final ObservableList<String> actorsList = FXCollections.observableArrayList();

    @FXML
    public void handleBackButton() {
        screenController.activate("movieList");
    }

    @FXML
    public void handleOnOpen() {
        try {
            // Kiểm tra thông tin người dùng
            User user = null;
            try {
                user = (User) authService.getUser();
            } catch (Exception ignored) { }

            if (user == null || user.getRole() != UserRole.administrator) {
                screenController.activate("home");
                return;
            }

            // Clear hết dữ liệu cũ
            clearFields();

            // Gán cho các ô dữ liệu
            ObservableList<MovieGenre> allGenres = FXCollections.observableArrayList(MovieGenre.values());
            FilteredList<MovieGenre> filteredGenres = new FilteredList<>(allGenres, p -> true);

            genreListView.setItems(filteredGenres);
            genreListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Lấy dữ liệu từ enum MovieGenre và thêm vào CheckBoxList
            genreListView.setCellFactory(CheckBoxListCell.forListView(genre -> {
                SimpleBooleanProperty selected = new SimpleBooleanProperty();
                selected.addListener((obs, oldV, newV) -> {
                    if (newV) {
                        if (!genreListView.getSelectionModel().getSelectedItems().contains(genre)) {
                            genreListView.getSelectionModel().select(genre);
                        }
                    } else {
                        genreListView.getSelectionModel().clearSelection(genreListView.getItems().indexOf(genre));
                    }
                });
                return selected;
                // Đảm bảo MovieGenre hiển thị Tiếng Việt
            }, new StringConverter<MovieGenre>() {
                @Override
                public String toString(MovieGenre object) {
                    return object != null ? object.toVietnamese() : "";
                }

                @Override
                public MovieGenre fromString(String string) {
                    return MovieGenre.fromVietnamese(string);
                }
            }));

            // Tìm kiếm MovieGenre
            searchGenreField.textProperty().addListener((obs, oldV, newV) -> {
                String filter = newV.toLowerCase();
                filteredGenres.setPredicate(g -> filter.isEmpty() ||
                        g.name().toLowerCase().contains(filter) ||
                        g.toVietnamese().toLowerCase().contains(filter));
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Lỗi", "Lỗi tải thể loại: " + e.getMessage());
        }

        // Tuơng tự MovieGenre, lấy data từ enum và chuyển sang Tiếng Việt
        movieLanguageComboBox.setItems(FXCollections.observableArrayList(MovieLanguage.values()));
        movieLanguageComboBox.setConverter(new StringConverter<MovieLanguage>() {
            @Override
            public String toString(MovieLanguage object) {
                return object != null ? object.toVietnamese() : "";
            }

            @Override
            public MovieLanguage fromString(String string) {
                return MovieLanguage.fromVietnamese(string);
            }
        });
    }

    @FXML
    public void handleAddMovieButton() {
        try {
            // Lấy thông tin của phim từ các field đã nhập
            Movie movie = new Movie();
            movie.setName(movieNameField.getText().trim());
            movie.setDescription(movieDescriptionField.getText().trim());
            movie.setLanguage(movieLanguageComboBox.getValue());
            movie.setDirector(movieDirectorField.getText().trim());
            
            LocalDate date = moviePremiereDatePicker.getValue();
            movie.setPremiere(date.atStartOfDay().atOffset(ZoneOffset.UTC));
            
            movie.setCreatedAt(OffsetDateTime.now());
            movie.setUpdatedAt(OffsetDateTime.now());
            movie.setDuration(movieDurationSpinner.getValue());
            movie.setRated(movieRatedSpinner.getValue());

            List<String> selectedGenres = genreListView.getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .map(Enum::name)
                    .toList();
            movie.setGenres(selectedGenres);

            movie.setGenres(selectedGenres);

            if (!actorsList.isEmpty()) {
                movie.setActors(List.copyOf(actorsList));
            } else {
                movie.setActors(List.of());
            }

            // Kiểm tra xem nếu có field nào chưa nhập thì alert
            if (NullCheckerUtil.hasNullField(movie)) {
                AlertHelper.showError("Lỗi", "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            // Thêm phim, làm mới các fields và quay về màn hình movieList
            movieService.insertMovie(movie);
            MovieListController listController = (MovieListController) screenController.getController("movieList");
            if (listController != null) {
                listController.refreshData();
            }
            screenController.activate("movieList");

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError( "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void clearFields() {
        movieNameField.clear();
        movieDurationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 120, 10));
        movieLanguageComboBox.getSelectionModel().clearSelection();
        movieRatedSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 18, 13));
        moviePremiereDatePicker.setValue(null);
        movieDescriptionField.clear();
        movieDirectorField.clear();
        actorsList.clear();
        actorsFlowPane.getChildren().clear();
        actorInputField.clear();
        searchGenreField.clear();
        genreListView.getSelectionModel().clearSelection();
    }
}
