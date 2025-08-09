package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Main extends Application {
    private TableView<ScheduledJob> tableView = new TableView<>();
    private ObservableList<ScheduledJob> displayedJobs =  FXCollections.observableArrayList();
    private DataLoader loader = new DataLoader();
    private FileChooser fileChooser = new FileChooser();
    private ArrayList<Job> jobs;
    private ComboBox<String> algorithmComboBox = new ComboBox<>();
    private TableView<SchedulerResult> resultTableView = new TableView<>();
    private ObservableList<SchedulerResult> displayedResults =  FXCollections.observableArrayList();
    private HashMap<String, GreedyScheduler> schedulers = new HashMap<>();
    private StatsBox statsBox = new StatsBox();
    private Label statusLabel;
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        jobs = loader.loadFromResource("/JobSampleData.csv");
        initialiseSchedulers();

        setTableView();
        setResultTableView();

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

        WeightsDialog weightsDialog = new WeightsDialog();

        Button runButton = new Button("Run");
        runButton.setOnAction(e -> runScheduler());

        Button runAllButton = new Button("Run All");
        runAllButton.setOnAction(e -> runAllSchedulers(tabPane, resultTab, chartsTab, chartBox, weightsDialog));

        HBox controls = new HBox(15);
        controls.getChildren().addAll( new Label(" Algorithm:"), algorithmComboBox, runButton, runAllButton);

        MenuBar menuBar = createMenuBar(stage, tabPane, jobsTab, resultTab, chartsTab, chartBox, weightsDialog);

        VBox topContainer = new VBox(menuBar, controls);

        root.setTop(topContainer);
        root.setCenter(tableView);
        root.setCenter(tabPane);
        root.setBottom(createStatusBar());
        updateStatusLabel(null, false);

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("Kabathova Greedy Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar(Stage stage, TabPane tabPane, Tab jobsTab, Tab resultTab, Tab chartsTab, ChartBox chartBox, WeightsDialog weightsDialog) {
        MenuBar menuBar = new MenuBar();
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem uploadItem = new MenuItem("Upload");
        uploadItem.setOnAction(e -> {
            ArrayList<Job> uploadedJobs = uploadJobFile(stage);
            if (!uploadedJobs.isEmpty()) {
                jobs = uploadedJobs;
                initialiseSchedulers();
                Toast.show(stage, "Jobs uploaded successfully!", Toast.ToastType.SUCCESS, 2000);
                updateStatusLabel(null, false);

            } else {
                Toast.show(stage, "Jobs upload failed!", Toast.ToastType.ERROR, 2000);
            }
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(uploadItem, new SeparatorMenuItem(), exitItem);

        // Run menu
        Menu runMenu = new Menu("Run");
        MenuItem runItem = new MenuItem("Run Selected");
        runItem.setOnAction(e -> runScheduler());

        MenuItem runAllItem = new MenuItem("Run All");
        runAllItem.setOnAction(e -> runAllSchedulers(tabPane, resultTab, chartsTab, chartBox, weightsDialog));

        runMenu.getItems().addAll(runItem, runAllItem);

        // Options menu
        Menu optionsMenu = new Menu("Options");
        MenuItem weightsItem = new MenuItem("Weights");
        weightsItem.setOnAction(e -> weightsDialog.showAndWait());

        optionsMenu.getItems().addAll(weightsItem);

        // View menu
        Menu viewMenu = new Menu("View");
        MenuItem viewJobs = new MenuItem("Scheduled Jobs");
        viewJobs.setOnAction(e -> tabPane.getSelectionModel().select(jobsTab));

        MenuItem viewResults = new MenuItem("Results");
        viewResults.setOnAction(e -> {
            if (!tabPane.getTabs().contains(resultTab)) tabPane.getTabs().add(resultTab);
            tabPane.getSelectionModel().select(resultTab);
        });

        MenuItem viewCharts = new MenuItem("Charts");
        viewCharts.setOnAction(e -> {
            if (!tabPane.getTabs().contains(chartsTab)) tabPane.getTabs().add(chartsTab);
            tabPane.getSelectionModel().select(chartsTab);
        });

        viewMenu.getItems().addAll(viewJobs, viewResults, viewCharts);

        menuBar.getMenus().addAll(fileMenu, runMenu, optionsMenu, viewMenu);

        return menuBar;
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
        scheduledJobsCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScheduledJobsCount()).asObject());

        TableColumn<SchedulerResult, Integer> jobsCountCol = new TableColumn<>("Total number of Jobs");
        jobsCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUnscheduledJobsCount()).asObject());

        TableColumn<SchedulerResult, Integer> profitCol = new TableColumn<>("Total profit");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalProfit()).asObject());

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
        algorithmComboBox.getItems().clear();
        algorithmComboBox.getItems().addAll( schedulers.keySet());
        algorithmComboBox.getSelectionModel().selectFirst();
    }

    private void initialiseSchedulers() {
        schedulers.clear();

        schedulers.put("HighestProfitScheduler", new HighestProfitScheduler(jobs));
        schedulers.put("EarliestDeadlineScheduler", new EarliestDeadlineScheduler(jobs));
        schedulers.put("ProfitPerDurationScheduler", new ProfitPerDurationScheduler(jobs));
        schedulers.put("ShortestJobFirstScheduler", new ShortestJobFirstScheduler(jobs));

        setAlgorithmComboBox();
    }

    private void runScheduler() {
        String selected = algorithmComboBox.getValue();
        GreedyScheduler scheduler = schedulers.get(selected);
        if  (scheduler != null) {
            getAlgorithmResult(scheduler);
            tableView.sort();
            updateStatusLabel(selected, true);
        } else {
            System.out.println("No scheduler found " +  selected);
        }
    }

    private void runAllSchedulers(TabPane tabPane, Tab resultTab, Tab chartsTab, ChartBox chartBox, WeightsDialog weightsDialog){
        getAllResults();
        chartBox.setResultObservableList(displayedResults);

        double profitWeight = weightsDialog.getProfitWeight();
        double timeWeight = weightsDialog.getTimeWeight();
        double jobsWeight = weightsDialog.getJobsWeight();

        for (SchedulerResult result : displayedResults) {
            result.calculateScore(profitWeight, timeWeight, jobsWeight);
        }

        SchedulerResult best = Collections.max(displayedResults, Comparator.comparingDouble(SchedulerResult::getScore));
        setBestResult(best.getAlgorithmName());
        highlightBestResult(best);
        resultTableView.refresh();
        updateStatusLabel(null, true);
        if (!tabPane.getTabs().contains(resultTab)) {
            tabPane.getTabs().add(resultTab);
        }
        if (!tabPane.getTabs().contains(chartsTab)) {
            tabPane.getTabs().add(chartsTab);
        }
        tabPane.getSelectionModel().select(resultTab);
    }

    private void getAlgorithmResult(GreedyScheduler greedyScheduler) {
        SchedulerResult result = greedyScheduler.getResult();
        displayedJobs.setAll(greedyScheduler.getScheduledJobs());
        statsBox.updateLabels(result.getAlgorithmName(), result.getTotalProfit(), result.getTotalTimeUsed(), result.getExecutionTimeMillis());
        statsBox.createJobsPieChart(result);
    }

    private void setBestResult(String name) {
        GreedyScheduler scheduler = schedulers.get(name);
        if (scheduler != null) {
            getAlgorithmResult(scheduler);
        } else {
            System.out.println("Scheduler not found " + name);
        }
    }

    private void getAllResults(){
        displayedResults.clear();
        for  (GreedyScheduler scheduler : schedulers.values()) {
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

    private HBox createStatusBar(){
        statusLabel = new Label();
        statusLabel.setPadding(new Insets(5, 10, 5, 10));

        HBox statusBar = new HBox(statusLabel);
        statusBar.setStyle("-fx-background-color: rgb(239,239,239); -fx-border-color: rgb(176, 176, 176); -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(Pos.CENTER_LEFT);

        return statusBar;
    }

    private void updateStatusLabel(String algorithmName, boolean runInfo){
        String base = "Dataset: " + loader.getFileName() + " | Jobs: " + jobs.size();
        if (runInfo) {
            if (algorithmName != null){
                base += " | Algorithm: " + algorithmName;
            }
            base += " | Last run: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"));
        }
        statusLabel.setText(base);
    }

    public static void main(String[] args) {
        launch();
    }
}