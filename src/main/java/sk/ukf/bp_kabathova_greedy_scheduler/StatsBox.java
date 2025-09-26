package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StatsBox extends VBox {
    private Label algorithmNameLabel;
    private Label profitLabel;
    private Label executionTimeLabel;
    private Label totalTimeLabel;
    private StackPane chartPane;
    private Label pieChartCaption;
    private PieChart pieChart;

    public StatsBox() {
        algorithmNameLabel = new Label();
        profitLabel = new Label("Zisk: 0");
        executionTimeLabel = new Label("Čas vykonania: 0");
        totalTimeLabel = new Label("Celkový čas: 0");
        pieChartCaption = new Label();
        pieChartCaption.setTextFill(Color.BLACK);
        pieChartCaption.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 2;");
        pieChartCaption.setVisible(false);
        pieChart = new PieChart();

        chartPane = new StackPane();
        chartPane.getChildren().addAll(pieChart, pieChartCaption);
        chartPane.setAlignment(pieChartCaption, Pos.TOP_LEFT);

        getChildren().addAll(algorithmNameLabel, profitLabel, executionTimeLabel, totalTimeLabel, chartPane);
        setSpacing(15);
        setPadding(new Insets(10));
    }

    public void updateLabels(String name, int profit, int totalTime, double executionTime) {
        algorithmNameLabel.setText(name);
        profitLabel.setText("Zisk: " + profit);
        executionTimeLabel.setText("Čas vykonania: " + executionTime + " ms");
        totalTimeLabel.setText("Celkový čas: " + totalTime);
    }

    public void createJobsPieChart(SchedulerResult result) {
        pieChartCaption.setVisible(false);
        int scheduled = result.getScheduledJobsCount();
        int unscheduled = result.getUnscheduledJobsCount() - scheduled;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Plánované", scheduled),
                new PieChart.Data("Neplánované", unscheduled)
        );

        pieChart.setData(pieChartData);
        pieChart.setTitle("Prehľad plánovania úloh");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);

        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setOnMouseClicked(event -> {
                    Point2D local = chartPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                    pieChartCaption.setTranslateX(local.getX());
                    pieChartCaption.setTranslateY(local.getY());
                    pieChartCaption.setText(data.getName() + ": " + (int) data.getPieValue());
                    pieChartCaption.setVisible(true);
                });
            }
        }
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
