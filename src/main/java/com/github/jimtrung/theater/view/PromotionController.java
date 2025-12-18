package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Promotion;
import com.github.jimtrung.theater.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.github.jimtrung.theater.view.UserHeaderController;
import com.github.jimtrung.theater.service.AuthService;

public class PromotionController {

    private ScreenController screenController;
    private PromotionService promotionService;
    private AuthService authService;
    
    @FXML UserHeaderController userHeaderController;

    @FXML private ComboBox<String> sortComboBox;
    @FXML private FlowPane promotionGrid;
    @FXML private Label pageLabel;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    private ObservableList<Promotion> promotionList;

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
        userHeaderController.setScreenController(screenController);
    }
    
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        userHeaderController.setAuthService(authService);
    }

    public void handleOnOpen() { 
        userHeaderController.handleOnOpen();

        promotionList = FXCollections.observableArrayList();

        sortComboBox.setOnAction(event -> handleSort());

        refreshData();
    }

    public void refreshData() {
        if (promotionService != null) {
            try {
                List<Promotion> data = promotionService.getAllPromotions();
                promotionList.setAll(data);
                renderPromotionGrid(promotionList);
            } catch (Exception e) {
                System.err.println("[DEBUG] PromotionController: Error fetching promotions");
                e.printStackTrace();
            }
        } else {
            System.err.println("[DEBUG] PromotionController: PromotionService is NULL");
        }
    }

    private void renderPromotionGrid(List<Promotion> list) {
        promotionGrid.getChildren().clear();
        for (Promotion promo : list) {
            VBox card = new VBox(10);
            card.getStyleClass().add("promo-card");
            card.setStyle("-fx-background-color: #222; -fx-padding: 10; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");
            card.setPrefWidth(280);
            card.setMaxWidth(280);

            ImageView imageView = new ImageView();
            imageView.setFitWidth(260);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(false);
            System.out.println("[DEBUG] PromotionController: Image URL: " + promo.getImageUrl()); 
            try {
                if (promo.getImageUrl() != null && !promo.getImageUrl().isEmpty()) {
                    imageView.setImage(new Image(promo.getImageUrl(), true)); 
                }
            } catch (Exception e) {
                System.err.println("[DEBUG] Error loading image for promo: " + promo.getName());
                e.printStackTrace();
            }
            
            // Text Content
            Label nameLabel = new Label(promo.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
            nameLabel.setWrapText(true);

            Label descLabel = new Label(promo.getDescription());
            descLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 13px;");
            descLabel.setWrapText(true);
            descLabel.setMaxHeight(60);

            card.getChildren().addAll(imageView, nameLabel, descLabel);

            promotionGrid.getChildren().add(card);
        }
    }

    @FXML
    private void handleSort() {
        String selected = sortComboBox.getValue();
        if (selected == null) return;

        Comparator<Promotion> comparator;

        switch (selected) {
            case "Mới nhất đến cũ nhất":
                comparator = Comparator.comparing(Promotion::getStartDate).reversed();
                break;
            case "Cũ nhất đến mới nhất":
                comparator = Comparator.comparing(Promotion::getStartDate);
                break;
            case "Đang diễn ra":
                OffsetDateTime now = OffsetDateTime.now();
                List<Promotion> activeOnly = promotionList.stream()
                        .filter(p -> p.getStartDate() != null && p.getEndDate() != null)
                        .filter(p -> p.getStartDate().isBefore(now) && p.getEndDate().isAfter(now))
                        .toList();
                renderPromotionGrid(activeOnly);
                return; // Thoát hàm vì đã render danh sách lọc
            default:
                return;
        }

        FXCollections.sort(promotionList, comparator);
        renderPromotionGrid(promotionList);
    }
}
