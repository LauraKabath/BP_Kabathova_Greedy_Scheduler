package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;

public class ChartBox extends HBox {
    BarChart<String, Number> profitBarChart;
    BarChart<String, Number> executionTimeBarChart;
    BarChart<String, Number> jobsBarChart;
    BarChart<String, Number> totalTimeBarChart;
    ObservableList<SchedulerResult> resultObservableList;

    public ChartBox(ObservableList<SchedulerResult> resultObservableList) {
        this.resultObservableList = resultObservableList;
        createCharts();
        getChildren().addAll(profitBarChart, executionTimeBarChart, jobsBarChart, totalTimeBarChart);
    }

    public ObservableList<SchedulerResult> getResultObservableList() {
        return resultObservableList;
    }

    public void setResultObservableList(ObservableList<SchedulerResult> resultObservableList) {
        this.resultObservableList = resultObservableList;
        refreshCharts();
    }

    private void createCharts(){
        createProfitBarChart();
        createExecutionTimeBarChart();
        createJobsBarChart();
        createTotalTimeBarChart();
    }

    public void refreshCharts(){
        getChildren().clear();
        createCharts();
        getChildren().addAll(profitBarChart, executionTimeBarChart, jobsBarChart, totalTimeBarChart);
    }

    private void createProfitBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        profitBarChart = new BarChart<>(xAxis, yAxis);
        profitBarChart.setTitle("Celkový zisk na algoritmus");
        xAxis.setLabel("Algoritmus");
        yAxis.setLabel("Celkový zisk");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Zisk");

        for (SchedulerResult result : resultObservableList) {
            series.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getTotalProfit()));
        }

        profitBarChart.getData().add(series);
    }

    private void createExecutionTimeBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        executionTimeBarChart = new BarChart<>(xAxis, yAxis);
        executionTimeBarChart.setTitle("Časy vykonania algoritmov");
        xAxis.setLabel("Algoritmus");
        yAxis.setLabel("Čas vykonania (ms)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Čas vykonania");

        for (SchedulerResult result : resultObservableList) {
            series.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getExecutionTimeMillis()));
        }

        executionTimeBarChart.getData().add(series);
    }

    private void createJobsBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        jobsBarChart = new BarChart<>(xAxis, yAxis);
        jobsBarChart.setTitle("Prehľad plánovania úloh");
        xAxis.setLabel("Algoritmus");
        yAxis.setLabel("Hodnota");

        XYChart.Series<String, Number> seriesScheduled = new XYChart.Series<>();
        seriesScheduled.setName("Plánované úlohy");

        XYChart.Series<String, Number> seriesUnscheduled = new XYChart.Series<>();
        seriesUnscheduled.setName("Neplánované úlohy");

        for (SchedulerResult result : resultObservableList) {
            seriesScheduled.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getScheduledJobsCount()));
            seriesUnscheduled.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getUnscheduledJobsCount() - result.getScheduledJobsCount()));
        }

        jobsBarChart.getData().addAll(seriesScheduled, seriesUnscheduled);
    }

    private void createTotalTimeBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        totalTimeBarChart = new BarChart<>(xAxis, yAxis);
        totalTimeBarChart.setTitle("Celkový použitý čas");
        xAxis.setLabel("Algoritmus");
        yAxis.setLabel("Celkový čas");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Celkový použitý čas");
        for (SchedulerResult result : resultObservableList) {
            series.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getTotalTimeUsed()));
        }
        totalTimeBarChart.getData().add(series);
    }

    public BarChart<String, Number> getProfitBarChart() {
        return profitBarChart;
    }

    public BarChart<String, Number> getExecutionTimeBarChart() {
        return executionTimeBarChart;
    }

    public BarChart<String, Number> getJobsBarChart() {
        return jobsBarChart;
    }

    public BarChart<String, Number> getTotalTimeBarChart() {
        return totalTimeBarChart;
    }
}
