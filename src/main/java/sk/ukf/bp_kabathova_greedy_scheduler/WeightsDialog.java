package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WeightsDialog {
    private final Slider profitSlider;
    private final Slider timeSlider;
    private final Slider jobsSlider;
    private Stage popupStage;

    public WeightsDialog() {
        profitSlider = createSlider(0, 10, 5);
        timeSlider = createSlider(0, 10, 2);
        jobsSlider = createSlider(0, 10, 3);
        initPopupStage();
    }

    private Slider createSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(0.5);
        return slider;
    }

    private void initPopupStage(){
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Weights Settings");

        Label profitLabel = new Label("Profit Weight:");
        Label timeLabel = new Label("Execution Time Weight:");
        Label jobsLabel = new Label("Scheduled Jobs Weight:");
        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> popupStage.close());

        VBox layout = new VBox(10, profitLabel, profitSlider, timeLabel, timeSlider, jobsLabel, jobsSlider, saveButton);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.setResizable(false);
    }

    public void showAndWait() {
        popupStage.showAndWait();
    }

    public double getProfitWeight() { return profitSlider.getValue(); }
    public double getTimeWeight() { return timeSlider.getValue(); }
    public double getJobsWeight() { return jobsSlider.getValue(); }

}

