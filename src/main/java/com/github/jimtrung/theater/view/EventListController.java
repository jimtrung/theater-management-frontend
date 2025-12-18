package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.service.AuthService;
import com.github.jimtrung.theater.model.Promotion;
import com.github.jimtrung.theater.service.PromotionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class EventListController {
    private ScreenController screenController;
    private AuthService authService;

    @FXML
    private UserHeaderController userHeaderController;

    @FXML
    private FlowPane eventFlowPane;

    private PromotionService promotionService;

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void handleOnOpen() {
        if (userHeaderController != null) userHeaderController.handleOnOpen();
        if (eventFlowPane != null) {
            eventFlowPane.getChildren().clear();
            
            // Asynchronous loading ideally, but for now blocking call as simple impl
            List<Promotion> promotions = promotionService != null ? promotionService.getPromotions() : List.of();

            if (promotions.isEmpty()) {
                Label placeholder = new Label("Hiện tại chưa có chương trình khuyến mãi nào.");
                placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
                eventFlowPane.getChildren().add(placeholder);
            } else {
                for (Promotion p : promotions) {
                    eventFlowPane.getChildren().add(createPromotionCard(p));
                }
            }
        }
    }

    private javafx.scene.layout.VBox createPromotionCard(Promotion p) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(10);
        card.setStyle("-fx-background-color: #222; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false); // Fill the box
        
        try {
            if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                imageView.setImage(new javafx.scene.image.Image(p.getImageUrl(), true)); // logic true for async loading
            } else {
                // Fallback
                 imageView.setImage(new javafx.scene.image.Image(getClass().getResource("/images/logo.png").toExternalForm())); 
                 // Assuming logo exists or use a simpler placeholder logic
            }
        } catch (Exception e) {
            // ignore
        }

        Label nameLabel = new Label(p.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        nameLabel.setWrapText(true);

        String dateStr = "";
        if (p.getStartDate() != null && p.getEndDate() != null) {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateStr = p.getStartDate().format(fmt) + " - " + p.getEndDate().format(fmt);
        }
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12px;");
        
        Label descLabel = new Label(p.getDescription());
        descLabel.setStyle("-fx-text-fill: #ddd; -fx-font-size: 13px;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60); // Limit height

        card.getChildren().addAll(imageView, nameLabel, dateLabel, descLabel);
        return card;
    }
}
