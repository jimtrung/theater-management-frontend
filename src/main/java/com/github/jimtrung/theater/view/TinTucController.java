package com.github.jimtrung.theater.view;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class TinTucController {

    @FXML
    private FlowPane flowPane;

    @FXML
    private Button btnPrev;

    @FXML
    private Button btnNext;

    private List<NewsItem> newsList = new ArrayList<>();
    private int currentPage = 0;
    private final int itemsPerPage = 8; // số card mỗi trang

    @FXML
    public void initialize() {
        // Thêm dữ liệu demo (có thể thay bằng dữ liệu từ DB hoặc API)
        newsList.add(new NewsItem("24/10/2025", "Gian hàng Trung tâm Chiếu phim Quốc gia chính thức góp mặt tại Hội chợ Mùa Thu 2025.", "/images/card1.jpg"));
        newsList.add(new NewsItem("13/10/2025", "CINETOUR 'Tay Anh Giữ Một Vì Sao' tại Trung tâm Chiếu phim Quốc gia ngày 10/10/2025.", "/images/card2.jpg"));
        newsList.add(new NewsItem("10/10/2025", "Cinetour 'Tay Anh Giữ Một Vì Sao' tại NCC ngày 9/10/2025.", "/images/card3.jpg"));
        newsList.add(new NewsItem("06/10/2025", "Đại hội Đại biểu Đảng bộ chính phủ lần thứ I, nhiệm kỳ 2025-2030", "/images/card4.jpg"));
        newsList.add(new NewsItem("02/10/2025", "TRUNG THU NÀY ĐẾN TRUNG TÂM CHIẾU PHIM QUỐC GIA NHẬN QUÀ CHO BÉ", "/images/card5.jpg"));
        newsList.add(new NewsItem("30/09/2025", "Úm ba la… dàn trai đẹp của Tử Chiến Trên Không đã chính thức “đổ bộ” tại Trung tâm Chiếu phim Quốc gia", "/images/card6.jpg"));
        newsList.add(new NewsItem("29/09/2025", "HOẠT ĐỘNG GIÁO DỤC – TRẢI NGHIỆM CỦA HỌC SINH KHỐI 10 TRƯỜNG THPT LÂM NGHIỆP CÙNG BỘ PHIM “MƯA ĐỎ”", "/images/card7.jpg"));
        newsList.add(new NewsItem("24/09/2025", "Buổi ra mắt và họp báo bộ phim “Tử Chiến Trên Không” tại Trung tâm Chiếu phim Quốc gia", "/images/card8.jpg"));

        newsList.add(new NewsItem("24/10/2025", "Gian hàng Trung tâm Chiếu phim Quốc gia chính thức góp mặt tại Hội chợ Mùa Thu 2025.", "/images/card9.jpg"));
        newsList.add(new NewsItem("13/10/2025", "CINETOUR 'Tay Anh Giữ Một Vì Sao' tại Trung tâm Chiếu phim Quốc gia ngày 10/10/2025.", "/images/card10.jpg"));
        newsList.add(new NewsItem("10/10/2025", "Cinetour 'Tay Anh Giữ Một Vì Sao' tại NCC ngày 9/10/2025.", "/images/card11.jpg"));
        newsList.add(new NewsItem("06/10/2025", "Đại hội Đại biểu Đảng bộ chính phủ lần thứ I, nhiệm kỳ 2025-2030", "/images/card12.jpg"));
        newsList.add(new NewsItem("02/10/2025", "TRUNG THU NÀY ĐẾN TRUNG TÂM CHIẾU PHIM QUỐC GIA NHẬN QUÀ CHO BÉ", "/images/card13.jpg"));
        newsList.add(new NewsItem("30/09/2025", "Úm ba la… dàn trai đẹp của Tử Chiến Trên Không đã chính thức “đổ bộ” tại Trung tâm Chiếu phim Quốc gia", "/images/card14.jpg"));
        newsList.add(new NewsItem("29/09/2025", "HOẠT ĐỘNG GIÁO DỤC – TRẢI NGHIỆM CỦA HỌC SINH KHỐI 10 TRƯỜNG THPT LÂM NGHIỆP CÙNG BỘ PHIM “MƯA ĐỎ”", "/images/card15.jpg"));
        newsList.add(new NewsItem("24/09/2025", "Buổi ra mắt và họp báo bộ phim “Tử Chiến Trên Không” tại Trung tâm Chiếu phim Quốc gia", "/images/card16.jpg"));

        // Hiển thị trang đầu tiên
        showPage(currentPage);

        // Nút prev/next
        btnPrev.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                showPage(currentPage);
            }
        });

        btnNext.setOnAction(e -> {
            if ((currentPage + 1) * itemsPerPage < newsList.size()) {
                currentPage++;
                showPage(currentPage);
            }
        });
    }

    private void showPage(int page) {
        flowPane.getChildren().clear();
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, newsList.size());

        for (int i = start; i < end; i++) {
            NewsItem item = newsList.get(i);
            VBox card = createNewsCard(item);
            flowPane.getChildren().add(card);
        }
    }

    private VBox createNewsCard(NewsItem item) {
        VBox card = new VBox(10);
        card.getStyleClass().add("news-card");

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(item.getImageUrl())));
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);

        Label dateLabel = new Label(item.getDate());
        dateLabel.getStyleClass().add("news-date");

        Label titleLabel = new Label(item.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().add("news-title");

        card.getChildren().addAll(imageView, dateLabel, titleLabel);
        return card;
    }

    // Class để lưu thông tin tin tức
    private static class NewsItem {
        private final String date;
        private final String title;
        private final String imageUrl;

        public NewsItem(String date, String title, String imageUrl) {
            this.date = date;
            this.title = title;
            this.imageUrl = imageUrl;
        }

        public String getDate() {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
