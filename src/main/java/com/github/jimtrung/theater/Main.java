package com.github.jimtrung.theater;

import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import com.github.jimtrung.theater.view.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        ScreenController screenController = new ScreenController(root);
        AuthTokenUtil authTokenUtil = new AuthTokenUtil();
        AuthService authService = new AuthService(authTokenUtil);
        MovieService movieService = new MovieService(authTokenUtil);

        try {
            String accessToken = authService.refresh();
            authTokenUtil.saveAccessToken(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Home
        FXMLLoader homeLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/home.fxml")));
        screenController.addScreen("home", homeLoader);
        HomeController homeController = homeLoader.getController();
        homeController.setScreenController(screenController);
        homeController.setAuthService(authService);

        // Sign Up
        FXMLLoader signUpLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/signup.fxml")));
        screenController.addScreen("signup", signUpLoader);
        SignUpController signUpController = signUpLoader.getController();
        signUpController.setScreenController(screenController);
        signUpController.setAuthService(authService);

        // Sign In
        FXMLLoader signInLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/signin.fxml")));
        screenController.addScreen("signin", signInLoader);
        SignInController signInController = signInLoader.getController();
        signInController.setScreenController(screenController);
        signInController.setAuthService(authService);
        signInController.setAuthTokenUtil(authTokenUtil);

        // Profile
        FXMLLoader profileLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/profile.fxml")));
        screenController.addScreen("profile", profileLoader);
        ProfileController profileController = profileLoader.getController();
        profileController.setScreenController(screenController);
        profileController.setAuthService(authService);
        profileController.setAuthTokenUtil(authTokenUtil);

        // HomePageManager
        FXMLLoader homePageManagerLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/home_page_manager.fxml")));
        screenController.addScreen("homePageManager", homePageManagerLoader);
        HomePageManagerController homePageManagerController = homePageManagerLoader.getController();
        homePageManagerController.setScreenController(screenController);
        homePageManagerController.setAuthService(authService);
        homePageManagerController.setAuthTokenUtil(authTokenUtil);

        // Home
        FXMLLoader homePageUserLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/home_page_user.fxml")));
        screenController.addScreen("homePageUser", homePageUserLoader);
        HomePageUserController homePageUserController = homePageUserLoader.getController();
        homePageUserController.setScreenController(screenController);
        homePageUserController.setAuthService(authService);
        homePageUserController.setAuthTokenUtil(authTokenUtil);

        // MovieList
        FXMLLoader movieListLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/movie_list_view.fxml")));
        screenController.addScreen("movieList", movieListLoader);
        MovieListController movieListController = movieListLoader.getController();
        movieListController.setScreenController(screenController);
        movieListController.setMovieService(movieService);
        movieListController.setAuthTokenUtil(authTokenUtil);

        // Add movie dialog
//        FXMLLoader addMovieLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/add_movie_dialog.fxml")));
//        screenController.addScreen("addMovie", addMovieLoader);
//        AddMovieController addMovieController = addMovieLoader.getController();
//        addMovieController.setScreenController(screenController);
//        addMovieController.setMovieService(movieService);
//        addMovieController.setAuthTokenUtil(authTokenUtil);

        // Start with home screen
        screenController.activate("home");

        Scene scene = new Scene(screenController.getRoot(), 1400, 700);
        stage.setTitle("Theater Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
