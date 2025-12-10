package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Movie; 
import com.github.jimtrung.theater.model.MovieGenre;
import com.github.jimtrung.theater.model.MovieLanguage;
import com.github.jimtrung.theater.service.MovieService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;

import java.util.List;
import java.util.UUID;


public class MovieInformationController {

    private ScreenController screenController;
    private MovieService movieService;
    private MovieListController movieListController;
    private UUID uuid;
    private Movie currentMovie;

    public void setMovieListController(MovieListController movieListController) {
        this.movieListController = movieListController;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    @FXML
    public void handleBackButton() {
        screenController.activate("movieList");
    }

    @FXML
    public void handleEditButton() {
        try {
            if (currentMovie == null) {
                currentMovie = new Movie();
                currentMovie.setId(uuid);
            }
            
            currentMovie.setName(movieNameField.getText().trim());
            currentMovie.setDescription(movieDescriptionField.getText().trim());
            currentMovie.setLanguage(movieLanguageComboBox.getValue());
            currentMovie.setDirector(movieDirectorField.getText().trim());
            
            currentMovie.setDuration(movieDurationSpinner.getValue());
            currentMovie.setRated(movieRatedSpinner.getValue());

            // Parse premiere date
            if (moviePremiereDatePicker.getValue() != null) {
                currentMovie.setPremiere(moviePremiereDatePicker.getValue().atStartOfDay().atOffset(java.time.ZoneOffset.UTC));
            } else {
                currentMovie.setPremiere(null);
            }

            // Genres
            List<String> selectedGenres = genreListView.getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .map(Enum::name)
                    .toList();
            currentMovie.setGenres(selectedGenres);
            
            currentMovie.setActors(List.copyOf(actorsList));

            movieService.updateMovie(uuid, currentMovie);

            Movie movie = movieService.getMovieById(uuid);
            movieListController.updateMovie(movie);

            screenController.activate("movieList");

        } catch (Exception e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteButton() throws Exception {
        movieService.deleteMovieById(uuid);
        movieListController.refreshData();
        screenController.activate("movieList");
    }

    @FXML
    public void handleOnOpen() throws Exception {
        this.currentMovie = movieService.getMovieById(uuid);

        movieNameField.setText(currentMovie.getName());
        movieDescriptionField.setText(currentMovie.getDescription());
        movieDirectorField.setText(currentMovie.getDirector() != null ? currentMovie.getDirector() : "");
        
        movieDurationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, currentMovie.getDuration(), 10));
        movieRatedSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 18, currentMovie.getRated()));
        
        if (currentMovie.getPremiere() != null) {
            moviePremiereDatePicker.setValue(currentMovie.getPremiere().toLocalDate());
        } else {
            moviePremiereDatePicker.setValue(null);
        }

        // Language
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
        movieLanguageComboBox.getSelectionModel().select(currentMovie.getLanguage());

        // --- Actors ---
        actorsList.clear();
        actorsFlowPane.getChildren().clear();
        actorInputField.clear();
        if (currentMovie.getActors() != null) {
            for (String actor : currentMovie.getActors()) {
                addActorTag(actor);
            }
        }
        
        actorInputField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                event.consume();
                String name = actorInputField.getText().trim();
                if (!name.isEmpty() && !actorsList.contains(name)) {
                    addActorTag(name);
                    actorInputField.clear();
                }
            }
        });

        // --- Genres ---
        ObservableList<MovieGenre> allGenres = FXCollections.observableArrayList(MovieGenre.values());
        FilteredList<MovieGenre> filteredGenres = new FilteredList<>(allGenres, p -> true);
        genreListView.setItems(filteredGenres);
        genreListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        
        genreListView.getSelectionModel().clearSelection();
        if (currentMovie.getGenres() != null) {
            for (String genreStr : currentMovie.getGenres()) {
                try {
                    MovieGenre g = MovieGenre.valueOf(genreStr);
                    genreListView.getSelectionModel().select(g);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // Search filter
        searchGenreField.textProperty().addListener((obs, oldV, newV) -> {
             String filter = newV.toLowerCase();
             filteredGenres.setPredicate(g -> filter.isEmpty() ||
                     g.name().toLowerCase().contains(filter) ||
                     g.toVietnamese().toLowerCase().contains(filter));
        });
    }

    private void addActorTag(String name) {
        actorsList.add(name);

        javafx.scene.layout.HBox tag = new javafx.scene.layout.HBox();
        tag.setAlignment(javafx.geometry.Pos.CENTER);
        tag.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 15; -fx-padding: 5 10;");
        tag.setSpacing(5);

        Label label = new Label(name);
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 10; -fx-padding: 0;");
        closeBtn.setCursor(javafx.scene.Cursor.HAND);
        closeBtn.setOnAction(e -> {
            actorsFlowPane.getChildren().remove(tag);
            actorsList.remove(name);
        });

        tag.getChildren().addAll(label, closeBtn);
        actorsFlowPane.getChildren().add(tag);
    }

    @FXML private TextField movieNameField;
    @FXML private TextArea movieDescriptionField;
    @FXML private TextField movieDirectorField;
    
    @FXML private FlowPane actorsFlowPane;
    @FXML private TextField actorInputField;
    private final ObservableList<String> actorsList = FXCollections.observableArrayList();

    @FXML private TextField searchGenreField;
    @FXML private ListView<MovieGenre> genreListView;

    @FXML private DatePicker moviePremiereDatePicker;
    @FXML private Spinner<Integer> movieDurationSpinner;
    @FXML private ComboBox<MovieLanguage> movieLanguageComboBox;
    @FXML private Spinner<Integer> movieRatedSpinner;
}
