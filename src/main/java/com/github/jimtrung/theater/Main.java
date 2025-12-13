package com.github.jimtrung.theater;

import com.github.jimtrung.theater.service.*;
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
        AuditoriumService auditoriumService = new AuditoriumService(authTokenUtil);
        ShowtimeService showtimeService = new ShowtimeService(authTokenUtil);
        TicketService ticketService = new TicketService(authTokenUtil);

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

        // Admin - HomePageManager
        FXMLLoader homePageManagerLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/admin/home_page_manager.fxml")));
        screenController.addScreen("homePageManager", homePageManagerLoader);
        HomePageManagerController homePageManagerController = homePageManagerLoader.getController();
        homePageManagerController.setScreenController(screenController);
        homePageManagerController.setAuthService(authService);

        // HomePageUser
        FXMLLoader homePageUserLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/home_page_user.fxml")));
        screenController.addScreen("homePageUser", homePageUserLoader);
        HomePageUserController homePageUserController = homePageUserLoader.getController();
        homePageUserController.setScreenController(screenController);
        homePageUserController.setAuthService(authService);
        homePageUserController.setMovieService(movieService);
        homePageUserController.setShowtimeService(showtimeService);

        // MovieList
        FXMLLoader movieListLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/admin/movie_list.fxml")));
        screenController.addScreen("movieList", movieListLoader);
        MovieListController movieListController = movieListLoader.getController();
        movieListController.setScreenController(screenController);
        movieListController.setMovieService(movieService);

        // AuditoriumList
        FXMLLoader auditoriumListLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/admin/auditorium_list.fxml")));
        screenController.addScreen("auditoriumList", auditoriumListLoader);
        AuditoriumListController auditoriumListController = auditoriumListLoader.getController();
        auditoriumListController.setScreenController(screenController);
        auditoriumListController.setAuditoriumService(auditoriumService);

        // TinTuc
        FXMLLoader tinTucLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/tintuc.fxml")));
        screenController.addScreen("tintuc", tinTucLoader);
        TinTucController tinTucController = tinTucLoader.getController();
        tinTucController.setScreenController(screenController);
        tinTucController.setAuthService(authService);
        
        // Book Ticket
        FXMLLoader bookTicketLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/book_ticket.fxml")));
        screenController.addScreen("bookTicket", bookTicketLoader);
        BookTicketController bookTicketController = bookTicketLoader.getController();
        bookTicketController.setScreenController(screenController);
        bookTicketController.setShowtimeService(showtimeService);
        bookTicketController.setMovieService(movieService);
        bookTicketController.setAuditoriumService(auditoriumService);
        bookTicketController.setTicketService(ticketService);

        // Booked Ticket
        FXMLLoader bookedTicketLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/booked_ticket.fxml")));
        screenController.addScreen("bookedTicket", bookedTicketLoader);
        BookedTicketController bookedTicketController = bookedTicketLoader.getController();
        bookedTicketController.setScreenController(screenController);
        bookedTicketController.setTicketService(ticketService);
        bookedTicketController.setShowtimeService(showtimeService);
        bookedTicketController.setMovieService(movieService);
        bookedTicketController.setAuditoriumService(auditoriumService);

        // Pay Page
        FXMLLoader payPageLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/pay_page.fxml")));
        screenController.addScreen("payPage", payPageLoader);
        PayPageController payPageController = payPageLoader.getController();
        payPageController.setScreenController(screenController);
        payPageController.setAuthService(authService);
        payPageController.setTicketService(ticketService);

        // Price
        FXMLLoader priceLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/price.fxml")));
        screenController.addScreen("price", priceLoader);
        PriceController priceController = priceLoader.getController();
        priceController.setScreenController(screenController);
        priceController.setAuthService(authService);

        // Showtime Page
        FXMLLoader showtimePageLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/showtime_page.fxml")));
        screenController.addScreen("showtimePage", showtimePageLoader);
        ShowtimePageController showtimePageController = showtimePageLoader.getController();
        showtimePageController.setScreenController(screenController);
        showtimePageController.setAuthService(authService);
        showtimePageController.setShowtimeService(showtimeService);
        showtimePageController.setMovieService(movieService);

        // Event List
        FXMLLoader eventListLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/user/event_list.fxml")));
        screenController.addScreen("eventList", eventListLoader);
        EventListController eventListController = eventListLoader.getController();
        eventListController.setScreenController(screenController);
        eventListController.setAuthService(authService);
        
        // Admin - Showtime Information
        FXMLLoader showtimeInfoLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/admin/showtime_information.fxml")));
        screenController.addScreen("showtimeInformation", showtimeInfoLoader);
        ShowtimeInformationController showtimeInfoController = showtimeInfoLoader.getController();
        showtimeInfoController.setScreenController(screenController);
        showtimeInfoController.setAuthService(authService);
        showtimeInfoController.setShowtimeService(showtimeService);
        showtimeInfoController.setMovieService(movieService);
        showtimeInfoController.setAuditoriumService(auditoriumService);

        // ShowtimeList
        FXMLLoader showtimeListLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/admin/showtime_list.fxml")));
        screenController.addScreen("showtimeList", showtimeListLoader);
        ShowtimeListController showtimeListController = showtimeListLoader.getController();
        showtimeListController.setScreenController(screenController);
        showtimeListController.setAuthService(authService);
        showtimeListController.setShowtimeService(showtimeService);
        showtimeListController.setMovieService(movieService);
        showtimeListController.setAuditoriumService(auditoriumService);

        // Add movie
        FXMLLoader addMovieLoader = new FXMLLoader(getClass().getResource("/fxml/admin/add_movie.fxml"));
        screenController.addScreen("addMovie", addMovieLoader);
        AddMovieController addMovieController = addMovieLoader.getController();
        addMovieController.setScreenController(screenController);
        addMovieController.setMovieService(movieService);
        addMovieController.setAuthService(authService);

        // Movie information
        FXMLLoader movieInformationLoader = new FXMLLoader(getClass().getResource("/fxml/admin/movie_information.fxml"));
        screenController.addScreen("movieInformation", movieInformationLoader);
        MovieInformationController movieInformationController = movieInformationLoader.getController();
        movieInformationController.setScreenController(screenController);
        movieInformationController.setMovieService(movieService);

        // Add auditorium
        FXMLLoader addAuditoriumLoader = new FXMLLoader(getClass().getResource("/fxml/admin/add_auditorium.fxml"));
        screenController.addScreen("addAuditorium", addAuditoriumLoader);
        AddAuditoriumController addAuditoriumController = addAuditoriumLoader.getController();
        addAuditoriumController.setScreenController(screenController);
        addAuditoriumController.setAuditoriumService(auditoriumService);
        addAuditoriumController.setAuthService(authService);

        // Auditorium Information
        FXMLLoader auditoriumInformationLoader = new FXMLLoader(getClass().getResource("/fxml/admin/auditorium_information.fxml"));
        screenController.addScreen("auditoriumInformation", auditoriumInformationLoader);
        AuditoriumInformationController auditoriumInformationController = auditoriumInformationLoader.getController();
        auditoriumInformationController.setScreenController(screenController);
        auditoriumInformationController.setAuditoriumService(auditoriumService);
        auditoriumInformationController.setAuthService(authService);

        // Add showtime
        FXMLLoader showtimeLoader = new FXMLLoader(getClass().getResource("/fxml/admin/add_showtime.fxml"));
        screenController.addScreen("addShowtime", showtimeLoader);
        AddShowtimeController addShowtimeController = showtimeLoader.getController();
        addShowtimeController.setScreenController(screenController);
        addShowtimeController.setAuthService(authService);
        addShowtimeController.setShowtimeService(showtimeService);
        addShowtimeController.setMovieService(movieService);
        addShowtimeController.setAuditoriumService(auditoriumService);

        screenController.activate("home");

        Scene scene = new Scene(screenController.getRoot());
        stage.setMaximized(true);
        stage.setTitle("National Cinema Center");
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
