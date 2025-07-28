package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        Label profitValue = createLabel(profitSlider);

        Label timeLabel = new Label("Execution Time Weight:");
        Label timeValue = createLabel(timeSlider);

        Label jobsLabel = new Label("Scheduled Jobs Weight:");
        Label jobsValue = createLabel(jobsSlider);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> popupStage.close());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(profitLabel, 0, 0);
        gridPane.add(profitSlider, 1, 0);
        gridPane.add(profitValue, 2, 0);

        gridPane.add(timeLabel, 0, 1);
        gridPane.add(timeSlider, 1, 1);
        gridPane.add(timeValue, 2, 1);

        gridPane.add(jobsLabel, 0, 2);
        gridPane.add(jobsSlider, 1, 2);
        gridPane.add(jobsValue, 2, 2);

        HBox buttonBox = new HBox(saveButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, gridPane, buttonBox);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.setResizable(false);
    }

    private Label createLabel(Slider slider){
        Label label = new Label(String.valueOf(Math.round(slider.getValue()*10)/10.0));
        label.setPrefWidth(40);
        label.setAlignment(Pos.CENTER_RIGHT);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(String.valueOf(Math.round(newValue.doubleValue()*10)/10.0));
        });
        return label;
    }

    public void showAndWait() {
        popupStage.showAndWait();
    }

    public double getProfitWeight() { return profitSlider.getValue(); }
    public double getTimeWeight() { return timeSlider.getValue(); }
    public double getJobsWeight() { return jobsSlider.getValue(); }

}

