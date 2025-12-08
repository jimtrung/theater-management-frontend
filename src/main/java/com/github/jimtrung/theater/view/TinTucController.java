package com.github.jimtrung.theater.view;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.Cursor;

import java.util.ArrayList;
import java.util.List;

import com.github.jimtrung.theater.service.AuthService;

public class TinTucController {

    private ScreenController screenController;

    @FXML
    private FlowPane newsGrid;

    @FXML
    private UserHeaderController userHeaderController;

    private final int ITEMS_PER_PAGE = 8;
    private int currentPage = 0;

    private final List<Object[]> newsList = new ArrayList<>();

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        if (userHeaderController != null) userHeaderController.setScreenController(screenController);
    }

    public void setAuthService(AuthService authService) {
        if (userHeaderController != null) userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();

        loadData();

        showPage(currentPage);
    }

    private void loadData() {
        newsList.add(new Object[]{
                "Gian hàng Trung tâm Chiếu phim Quốc gia chính thức góp mặt tại Hội chợ Mùa Thu 2025.",
                "24/10/2025", "card1.jpg"
        });

        newsList.add(new Object[]{
                "CINETOUR 'Tay Anh Giữ Một Vì Sao' tại Trung tâm Chiếu phim Quốc gia ngày 10/10/2025.",
                "13/10/2025", "card2.jpg"
        });

        newsList.add(new Object[]{
                "CINETOUR 'Tay Anh Giữ Một Vì Sao' tại NCC ngày 9/10/2025.",
                "10/10/2025", "card3.jpg"
        });

        newsList.add(new Object[]{
                "Đại hội Đại biểu Đảng bộ chính phủ lần thứ I, nhiệm kỳ 2025-2030",
                "06/10/2025", "card4.jpg"
        });

        newsList.add(new Object[]{
                "TRUNG THU NÀY ĐẾN TRUNG TÂM CHIẾU PHIM QUỐC GIA NHẬN QUÀ CHO BÉ",
                "02/10/2025", "card5.jpg"
        });

        newsList.add(new Object[]{
                "Úm ba la… dàn trai đẹp của Tử Chiến Trên Không đã chính thức “đổ bộ” tại Trung tâm Chiếu phim Quốc gia",
                "30/09/2025", "card6.jpg"
        });

        newsList.add(new Object[]{
                "HOẠT ĐỘNG GIÁO DỤC – TRẢI NGHIỆM…",
                "29/09/2025", "card7.jpg"
        });

        newsList.add(new Object[]{
                "Buổi ra mắt và họp báo bộ phim…",
                "24/09/2025", "card8.jpg"
        });

        newsList.add(new Object[]{"Gian hàng NCC…", "24/10/2025", "card9.jpg"});
        newsList.add(new Object[]{"CINETOUR…", "13/10/2025", "card10.jpg"});
        newsList.add(new Object[]{"Cinetour NCC…", "10/10/2025", "card11.jpg"});
        newsList.add(new Object[]{"Đại hội Đảng…", "06/10/2025", "card12.jpg"});
        newsList.add(new Object[]{"TRUNG THU…", "02/10/2025", "card13.jpg"});
        newsList.add(new Object[]{"Úm ba la…", "30/09/2025", "card14.jpg"});
        newsList.add(new Object[]{"Trải nghiệm…", "29/09/2025", "card15.jpg"});
        newsList.add(new Object[]{"Họp báo…", "24/09/2025", "card16.jpg"});
    }

    @FXML
    private void handlePrevButton() {
        if (currentPage > 0) {
            currentPage--;
            showPage(currentPage);
        }
    }

    @FXML
    private void handleNextButton() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < newsList.size()) {
            currentPage++;
            showPage(currentPage);
        }
    }

    private void showPage(int page) {
        newsGrid.getChildren().clear();

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, newsList.size());

        for (int i = start; i < end; i++) {
            Object[] item = newsList.get(i);
            String title = (String) item[0];
            String date = (String) item[1];
            String image = (String) item[2];

            newsGrid.getChildren().add(buildCard(title, date, image));
        }
    }

    private VBox buildCard(String title, String date, String imagePath) {
        VBox card = new VBox();
        card.getStyleClass().add("news-card");
        card.setCursor(Cursor.HAND);

        Image img;
        try {
            img = new Image(getClass().getResourceAsStream("images/" + imagePath));
        } catch (Exception e) {
            img = new Image(getClass().getResourceAsStream("images/not_found.png"));
        }

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);

        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("news-date");

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().add("news-title");

        card.getChildren().addAll(imageView, dateLabel, titleLabel);
        return card;
    }
}
