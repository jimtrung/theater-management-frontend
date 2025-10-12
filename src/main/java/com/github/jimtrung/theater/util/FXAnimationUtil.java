package com.github.jimtrung.theater.util;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FXAnimationUtil {
    public static void addHoverEffect(Node node, Double scale, Integer duration) {
        ScaleTransition enter = new ScaleTransition(Duration.millis(duration), node);
        enter.setToX(scale);
        enter.setToY(scale);

       ScaleTransition exit = new ScaleTransition(Duration.millis(duration), node);
       exit.setToX(1.0);
       exit.setToY(1.0);

       node.setOnMouseEntered(e -> enter.playFromStart());
       node.setOnMouseExited(e -> exit.playFromStart());
    }
}
