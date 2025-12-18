package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Promotion;
import com.github.jimtrung.theater.service.PromotionService; // Giả định bạn có lớp gọi API tương ứng ở Frontend
import com.github.jimtrung.theater.dto.PromotionRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

public class PromotionController {

    private ScreenController screenController;
    private PromotionService promotionService;

    @FXML private ComboBox<String> sortComboBox;
    @FXML private FlowPane promotionGrid;
    @FXML private Label pageLabel;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    private ObservableList<PromotionRequest> promotionList;

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    /**
     * Hàm này được gọi khi màn hình Khuyến mãi được mở ra (tương tự handleOnOpen)
     */
    @FXML
    public void initialize() {
        promotionList = FXCollections.observableArrayList();

        // Thiết lập sự kiện sắp xếp khi chọn ComboBox
        sortComboBox.setOnAction(event -> handleSort());

        refreshData();
    }

    /**
     * Lấy dữ liệu từ Backend và hiển thị
     */
    public void refreshData() {
        if (promotionService != null) {
            try {
                // Gọi API từ Backend (trả về danh sách DTO PromotionRequest)
                List<PromotionRequest> data = promotionService.getAllPromotions();
                promotionList.setAll(data);
                renderPromotionGrid(promotionList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Vẽ các thẻ Khuyến mãi vào FlowPane
     */
    private void renderPromotionGrid(List<PromotionRequest> list) {
        promotionGrid.getChildren().clear();
        for (PromotionRequest promo : list) {
            // Ở đây bạn có thể tạo một Component (VBox/AnchorPane) đại diện cho thẻ KM
            // Hoặc đơn giản là một Label/Button để test
            Button promoCard = new Button(promo.name() + "\n" + promo.description());
            promoCard.getStyleClass().add("promo-card"); // Class CSS bạn tự định nghĩa
            promoCard.setPrefSize(250, 150);

            promoCard.setOnAction(e -> handleCardClick(promo));

            promotionGrid.getChildren().add(promoCard);
        }
    }

    /**
     * Logic Sắp xếp: Gần nhất đến muộn nhất
     */
    @FXML
    private void handleSort() {
        String selected = sortComboBox.getValue();
        if (selected == null) return;

        Comparator<PromotionRequest> comparator;

        switch (selected) {
            case "Mới nhất đến cũ nhất":
                comparator = Comparator.comparing(PromotionRequest::startDate).reversed();
                break;
            case "Cũ nhất đến mới nhất":
                comparator = Comparator.comparing(PromotionRequest::startDate);
                break;
            case "Đang diễn ra":
                OffsetDateTime now = OffsetDateTime.now();
                List<PromotionRequest> activeOnly = promotionList.stream()
                        .filter(p -> p.startDate().isBefore(now) && p.endDate().isAfter(now))
                        .toList();
                renderPromotionGrid(activeOnly);
                return; // Thoát hàm vì đã render danh sách lọc
            default:
                return;
        }

        FXCollections.sort(promotionList, comparator);
        renderPromotionGrid(promotionList);
    }

    private void handleCardClick(PromotionRequest promo) {
        // Xử lý khi click vào một khuyến mãi (ví dụ: xem chi tiết)
        System.out.println("Bạn đã chọn khuyến mãi: " + promo.name());
    }

    @FXML
    public void handlePrevPage() {
        // Logic phân trang nếu cần
    }

    @FXML
    public void handleNextPage() {
        // Logic phân trang nếu cần
    }

    @FXML
    public void handleHomeButton() {
        screenController.activate("homePage");
    }
}