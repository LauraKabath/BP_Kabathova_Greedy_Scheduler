package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Toast {
    public enum ToastType {
        SUCCESS,
        ERROR,
        INFO,
        WARNING
    }
    public static void show(Stage stage, String message, ToastType type, int durationMs){
        Popup popup = new Popup();

        Label label = new Label(message);
        label.setTextFill(getTextColor(type));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        label.setWrapText(true);
        label.setMaxWidth(250);
        label.setAlignment(Pos.CENTER);

        StackPane container = new StackPane(label);
        container.setPadding(new Insets(10));
        container.setStyle(getBackgroundStyle(type));

        popup.getContent().add(container);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        double x = stage.getX() + (stage.getWidth() - container.getWidth()) / 2;
        double y = stage.getY() + stage.getHeight() - 100;
        popup.show(stage, x, y);

        PauseTransition delay = new PauseTransition(Duration.millis(durationMs));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    private static String getBackgroundStyle(ToastType type){
        switch (type){
            case SUCCESS:
                return "-fx-background-color: rgb(203,246,212); -fx-background-radius: 8;";
            case ERROR:
                return "-fx-background-color: rgb(255, 140, 130); -fx-background-radius: 8;";
            case INFO:
                return "-fx-background-color: rgb(197, 250, 246); -fx-background-radius: 8;";
            case WARNING:
                return "-fx-background-color: rgb(252, 199, 141); -fx-background-radius: 8;";
            default:
                return "-fx-background-color: lightgray; -fx-background-radius: 8;";
        }
    }

    private static Color getTextColor(ToastType type){
        switch (type){
            case SUCCESS:
                return Color.rgb(26, 73, 41);
            case ERROR:
                return Color.rgb(84, 7, 0);
            case INFO:
                return Color.rgb(2, 94, 87);
            case WARNING:
                return Color.rgb(156, 82, 3);
            default:
                return Color.BLACK;
        }
    }
}
