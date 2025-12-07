package com.github.jimtrung.theater.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import com.github.jimtrung.theater.model.User;
import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import java.net.URL;
import java.util.ResourceBundle;

public class TinTucController implements Initializable {
    private ScreenController screenController;
    private AuthTokenUtil authTokenUtil;
    private AuthService authService;

    @FXML
    private FlowPane newsGrid;

    @FXML
    private UserHeaderController userHeaderController;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
        if (userHeaderController != null) userHeaderController.setAuthTokenUtil(authTokenUtil);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();

        User user = null;
        try {
            user = (User) authService.getUser();
        } catch (Exception ignored) { }
        
        // Logic to update UI based on user state can go here
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSampleNews();
    }

    private void loadSampleNews() {
        addNewsCard("Gian hàng Trung tâm Chiếu phim Quốc gia góp mặt tại Hội chợ Mùa Thu 2025.",
                "24/10/2025", "/images/cat.jpg");

        addNewsCard("CINETOUR 'Tay Anh Giữ Một Vì Sao' tại Trung tâm Chiếu phim Quốc gia ngày 10/10/2025.",
                "13/10/2025", "/images/cat.jpg");

        addNewsCard("Cinetour 'Tay Anh Giữ Một Vì Sao' tại NCC ngày 9/10/2025.",
                "10/10/2025", "/images/cat.jpg");
    }

    private void addNewsCard(String title, String date, String imagePath) {
        VBox card = new VBox();
        card.getStyleClass().add("news-card");

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);

        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("news-date");

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().add("news-title");

        card.getChildren().addAll(imageView, dateLabel, titleLabel);
        newsGrid.getChildren().add(card);
    }

    @FXML
    private void goHome() {
        if (screenController != null) {
            screenController.activate("home");
        }
    }
    
    @FXML
    private void handlePrevButton() {
        
    }
    
    @FXML
    private void handleNextButton() {
        
    }
}
