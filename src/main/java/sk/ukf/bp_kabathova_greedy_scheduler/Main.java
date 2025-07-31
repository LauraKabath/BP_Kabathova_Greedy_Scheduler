package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main extends Application {
    private TableView<ScheduledJob> tableView = new TableView<>();
    private ObservableList<ScheduledJob> displayedJobs =  FXCollections.observableArrayList();
    private DataLoader loader = new DataLoader();
    FileChooser fileChooser = new FileChooser();
    private ArrayList<Job> jobs;
    private ComboBox<String> algorithmComboBox = new ComboBox<>();
    private TableView<SchedulerResult> resultTableView = new TableView<>();
    private ObservableList<SchedulerResult> displayedResults =  FXCollections.observableArrayList();
    private HighestProfitScheduler highestProfitScheduler;
    private EarliestDeadlineScheduler earliestDeadlineScheduler;
    private ShortestJobFirstScheduler shortestJobFirstScheduler;
    private ProfitPerDurationScheduler profitPerDurationScheduler;
    StatsBox statsBox = new StatsBox();
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        jobs = loader.loadFromResource("/JobSampleData.csv");
        initialiseSchedulers();

        setTableView();
        setResultTableView();
        setAlgorithmComboBox();

        SplitPane splitPane = new SplitPane(tableView, statsBox);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.6);

        TabPane tabPane = new TabPane();
        Tab jobsTab = new Tab("Scheduled Jobs", splitPane);
        jobsTab.setClosable(false);
        Tab resultTab = new Tab("Results", resultTableView);
        ChartBox chartBox = new ChartBox(displayedResults);
        Tab chartsTab = new Tab("Charts", chartBox);
        tabPane.getTabs().add(jobsTab);

        Button runButton = new Button("Run");
        runButton.setOnAction(e -> {
            runScheduler();
        });

        Button runAllButton = new Button("Run All");

        WeightsDialog weightsDialog = new WeightsDialog();

        Button weightButton = new Button("Weights");
        weightButton.setOnAction(e -> weightsDialog.showAndWait());

        runAllButton.setOnAction(e -> {
            getAllResults();
            chartBox.setResultObservableList(displayedResults);

            double profitWeight = weightsDialog.getProfitWeight();
            double timeWeight = weightsDialog.getTimeWeight();
            double jobsWeight = weightsDialog.getJobsWeight();

            for (SchedulerResult result : displayedResults) {
                result.calculateScore(profitWeight, timeWeight, jobsWeight);
            }

            SchedulerResult best = Collections.max(displayedResults, Comparator.comparingDouble(SchedulerResult::getScore));
            highlightBestResult(best);
            resultTableView.refresh();

            if (!tabPane.getTabs().contains(resultTab)) {
                tabPane.getTabs().add(resultTab);
            }
            if (!tabPane.getTabs().contains(chartsTab)) {
                tabPane.getTabs().add(chartsTab);
            }
            tabPane.getSelectionModel().select(resultTab);
        });

        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(e -> {
            ArrayList<Job> uploadedJobs = uploadJobFile(stage);
            if (!uploadedJobs.isEmpty()) {
                jobs = uploadedJobs;
                initialiseSchedulers();
                Toast.show(stage, "Jobs uploaded successfully!", Toast.ToastType.SUCCESS, 2000);
            } else {
                Toast.show(stage, "Jobs upload failed!", Toast.ToastType.ERROR, 2000);
            }
        });

        HBox controls = new HBox(15);
        controls.getChildren().addAll( new Label(" Algorithm:"), algorithmComboBox, runButton, runAllButton, weightButton, uploadButton);

        root.setTop(controls);
        root.setCenter(tableView);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("Kabathova Greedy Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    private void setTableView() {
        tableView.getColumns().clear();

        TableColumn<ScheduledJob, String> idCol = new TableColumn<>("Job ID");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        idCol.setMinWidth(140);

        TableColumn<ScheduledJob, Integer> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDuration()).asObject());

        TableColumn<ScheduledJob, Integer> startTimeCol = new TableColumn<>("Start Time");
        startTimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStartTime()).asObject());
        startTimeCol.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<ScheduledJob, Integer> endTimeCol = new TableColumn<>("End Time");
        endTimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEndTime()).asObject());

        TableColumn<ScheduledJob, Integer> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDeadline()).asObject());

        TableColumn<ScheduledJob, Integer> profitCol = new TableColumn<>("Profit");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProfit()).asObject());

        tableView.getColumns().addAll(idCol, durationCol, startTimeCol, endTimeCol, deadlineCol, profitCol);
        tableView.setItems(displayedJobs);
        tableView.getSortOrder().add(startTimeCol);
    }

    private void setResultTableView() {
        resultTableView.getColumns().clear();

        TableColumn<SchedulerResult, String> nameCol = new TableColumn<>("Algorithm");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlgorithmName()));

        TableColumn<SchedulerResult, Integer> scheduledJobsCountCol = new TableColumn<>("Scheduled Jobs");
        scheduledJobsCountCol.setCellValueFactory(celldata -> new SimpleIntegerProperty(celldata.getValue().getScheduledJobsCount()).asObject());

        TableColumn<SchedulerResult, Integer> jobsCountCol = new TableColumn<>("Total number of Jobs");
        jobsCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUnscheduledJobsCount()).asObject());

        TableColumn<SchedulerResult, Integer> profitCol = new TableColumn<>("Total profit");
        profitCol.setCellValueFactory(celldata -> new SimpleIntegerProperty(celldata.getValue().getTotalProfit()).asObject());

        TableColumn<SchedulerResult, Integer> timeCol = new TableColumn<>("Total Time");
        timeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalTimeUsed()).asObject());

        TableColumn<SchedulerResult,Double> executionTimeCol = new TableColumn<>("Execution time");
        executionTimeCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getExecutionTimeMillis()).asObject());

        TableColumn<SchedulerResult,Double> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getScore()).asObject());

        resultTableView.getColumns().addAll(nameCol, scheduledJobsCountCol, jobsCountCol, profitCol, timeCol, executionTimeCol, scoreCol);
        resultTableView.getColumns().forEach(col -> col.setMinWidth(105));
        nameCol.setMinWidth(160);
        jobsCountCol.setMinWidth(140);
        resultTableView.setItems(displayedResults);
    }

    private void setAlgorithmComboBox() {
        algorithmComboBox.getItems().addAll(
                "Highest Profit",
                "Earliest Deadline",
                "Shortest Job First",
                "Profit per Duration"
        );
        algorithmComboBox.getSelectionModel().selectFirst();
    }

    private void initialiseSchedulers() {
        highestProfitScheduler = new HighestProfitScheduler(jobs);
        earliestDeadlineScheduler = new EarliestDeadlineScheduler(jobs);
        profitPerDurationScheduler = new ProfitPerDurationScheduler(jobs);
        shortestJobFirstScheduler = new ShortestJobFirstScheduler(jobs);
    }

    private void runScheduler() {
        String selected = algorithmComboBox.getValue();
        switch (selected) {
            case "Highest Profit":
                getAlgorithmResult(highestProfitScheduler);
                break;
            case "Earliest Deadline":
                getAlgorithmResult(earliestDeadlineScheduler);
                break;
            case "Profit per Duration":
                getAlgorithmResult(profitPerDurationScheduler);
                break;
            case "Shortest Job First":
            default:
                getAlgorithmResult(shortestJobFirstScheduler);
                break;
        }
        tableView.sort();
    }

    private void getAlgorithmResult(GreedyScheduler greedyScheduler) {
        SchedulerResult result = greedyScheduler.getResult();
        displayedJobs.setAll(greedyScheduler.getScheduledJobs());
        statsBox.updateLabels(result.getAlgorithmName(), result.getTotalProfit(), result.getTotalTimeUsed(), result.getExecutionTimeMillis());
        statsBox.createJobsPieChart(result);
    }

    private void getAllResults(){
        displayedResults.clear();
        GreedyScheduler[] schedulers = new GreedyScheduler[]{highestProfitScheduler, earliestDeadlineScheduler, profitPerDurationScheduler, shortestJobFirstScheduler};
        for  (GreedyScheduler scheduler : schedulers) {
            SchedulerResult schedulerResult = scheduler.getResult();
            displayedResults.add(schedulerResult);
        }
    }

    private void highlightBestResult(SchedulerResult bestResult) {
        resultTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(SchedulerResult item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getAlgorithmName().equals(bestResult.getAlgorithmName())) {
                    setStyle("-fx-background-color: #fff8dc; -fx-border-color: gold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private ArrayList<Job> uploadJobFile(Stage stage){
        fileChooser.setTitle("Select .csv file");
        fileChooser.getExtensionFilters().add(new  FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                return loader.loadFromFile(selectedFile);
            } catch (Exception e) {
                Toast.show(stage, "File upload failed", Toast.ToastType.ERROR, 2500);
            }
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        launch();
    }
}