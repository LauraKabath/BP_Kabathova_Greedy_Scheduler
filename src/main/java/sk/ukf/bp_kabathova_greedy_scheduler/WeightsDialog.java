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

import java.util.function.Consumer;

public class WeightsDialog {
    private final Slider profitSlider;
    private final Slider timeSlider;
    private final Slider jobsSlider;
    private double originalProfitWeight = 5;
    private double originalTimeWeight = 2;
    private double originalJobsWeight = 3;
    private Stage popupStage;

    public WeightsDialog(Consumer<double[]> onSave) {
        profitSlider = createSlider(0, 10, originalProfitWeight);
        timeSlider = createSlider(0, 10, originalTimeWeight);
        jobsSlider = createSlider(0, 10, originalJobsWeight);
        initPopupStage(onSave);
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

    private void initPopupStage(Consumer<double[]> onSave){
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Nastavenia váh");

        Label profitLabel = new Label("Váha zisku:");
        Label profitValue = createLabel(profitSlider);

        Label timeLabel = new Label("Váha času vykonania:");
        Label timeValue = createLabel(timeSlider);

        Label jobsLabel = new Label("Váha počtu úloh:");
        Label jobsValue = createLabel(jobsSlider);

        Button saveButton = new Button("Uložiť");
        saveButton.setOnAction(e -> {
            if (onSave != null) {
                onSave.accept(getWeights());
            }
            popupStage.close();
        });

        popupStage.setOnCloseRequest(e -> setOriginalValues());

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

    public void showAndWait(double[] weights) {
        profitSlider.setValue(weights[0]);
        timeSlider.setValue(weights[1]);
        jobsSlider.setValue(weights[2]);

        originalProfitWeight = getProfitWeight();
        originalTimeWeight = getTimeWeight();
        originalJobsWeight =getJobsWeight();

        popupStage.showAndWait();
    }

    public double getProfitWeight() { return profitSlider.getValue(); }
    public double getTimeWeight() { return timeSlider.getValue(); }
    public double getJobsWeight() { return jobsSlider.getValue(); }

    public double[] getWeights(){
        return new double[]{getProfitWeight(), getTimeWeight(), getJobsWeight()};
    }

    private void setOriginalValues(){
        profitSlider.setValue(originalProfitWeight);
        timeSlider.setValue(originalTimeWeight);
        jobsSlider.setValue(originalJobsWeight);
    }

}

