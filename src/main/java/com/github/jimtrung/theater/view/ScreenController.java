package com.github.jimtrung.theater.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class ScreenController {
    private final Map<String, Parent> screens = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();
    private final StackPane root;
    private final Stack<String> screenHistory = new Stack<>();

    public Object getController(String name) {
        return controllers.get(name);
    }

    public ScreenController(StackPane root) {
        this.root = root;
    }

    public void addScreen(String name, FXMLLoader loader) throws Exception {
        Parent node = loader.load();
        Object controller = loader.getController();

        screens.put(name, node);
        controllers.put(name, controller);
    }

    public void activate(String name) {
        if (screenHistory.isEmpty() || !screenHistory.peek().equals(name)) {
            screenHistory.push(name);
        }
        
        Parent screen = screens.get(name);
        if (screen != null) {
            root.getChildren().setAll(screen);

            Object controller = controllers.get(name);
            if (controller != null) {
                try {
                    controller.getClass()
                            .getMethod("handleOnOpen")
                            .invoke(controller);
                } catch (NoSuchMethodException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Screen not found: " + name);
        }
    }
    
    public void goBack() {
        if (screenHistory.size() > 1) {
            screenHistory.pop();
            String previousScreenName = screenHistory.peek();
            activate(previousScreenName);
        }
    }

    public StackPane getRoot() {
        return root;
    }

    public void removeScreen(String name) {
        screens.remove(name);
        controllers.remove(name);
    }
    
    private final Map<String, Object> context = new HashMap<>();
    
    public void setContext(String key, Object value) {
        context.put(key, value);
    }
    
    public Object getContext(String key) {
        return context.get(key);
    }
}
