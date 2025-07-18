package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatsBox extends VBox {
    private Label algorithmNameLabel;
    private Label profitLabel;
    private Label executionTimeLabel;
    private Label totalTimeLabel;

    public StatsBox() {
        algorithmNameLabel = new Label();
        profitLabel = new Label("Profit: 0");
        executionTimeLabel = new Label("Execution Time: 0");
        totalTimeLabel = new Label("Total Time: 0");

        getChildren().addAll(algorithmNameLabel, profitLabel, executionTimeLabel, totalTimeLabel);
        setSpacing(15);
        setPadding(new Insets(10));
    }

    public void updateLabels(String name, int profit, int totalTime, double executionTime) {
        algorithmNameLabel.setText(name);
        profitLabel.setText("Profit: " + profit);
        totalTimeLabel.setText("Total Time: " + totalTime);
        executionTimeLabel.setText("Execution Time: " + executionTime);
    }

    public Label getAlgorithmNameLabel() {
        return algorithmNameLabel;
    }

    public Label getProfitLabel() {
        return profitLabel;
    }

    public Label getExecutionTimeLabel() {
        return executionTimeLabel;
    }

    public Label getTotalTimeLabel() {
        return totalTimeLabel;
    }
}
