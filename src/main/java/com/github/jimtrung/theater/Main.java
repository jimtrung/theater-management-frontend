package com.github.jimtrung.theater;

import com.github.jimtrung.theater.service.AuthService;
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
    AuthTokenUtil  authTokenUtil = new AuthTokenUtil();
    AuthService authService = new AuthService(authTokenUtil);

    try {
      String accessToken = authService.refresh();
      authTokenUtil.saveAccessToken(accessToken);
      System.out.println("Access token: " + accessToken);
      System.out.println("Refresh token: " + authTokenUtil.loadRefreshToken());
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
