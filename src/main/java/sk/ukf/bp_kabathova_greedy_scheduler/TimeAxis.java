package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TimeAxis extends VBox {

    public static final int START_HOUR = 7;
    public static final int END_HOUR = 22;
    public static final int HOUR_HEIGHT = 59;

    public TimeAxis() {
        setPrefWidth(60);
        setPadding(new Insets(5));
        setSpacing(2.05);

        for (int h = START_HOUR; h <= END_HOUR; h++) {
            Label label = new Label(h + ":00");
            label.setLayoutY((h - START_HOUR) * 60);
            label.setPrefHeight(HOUR_HEIGHT);
            label.setAlignment(Pos.TOP_RIGHT);
            getChildren().add(label);
        }
    }
}
