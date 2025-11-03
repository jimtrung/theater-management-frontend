package com.github.jimtrung.theater.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class TinTucController implements Initializable {
    private ScreenController screenController;

    @FXML
    private FlowPane newsGrid;

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
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
}
