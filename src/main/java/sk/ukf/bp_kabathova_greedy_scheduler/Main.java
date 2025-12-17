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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private TableView<Job> dataTableView = new TableView<>();
    private TableView<ScheduledJob> tableView = new TableView<>();
    private ObservableList<ScheduledJob> displayedJobs = FXCollections.observableArrayList();
    private DataLoader loader = new DataLoader();
    private FileChooser fileChooser = new FileChooser();
    private ExportManager exportManager = new ExportManager();
    private ArrayList<Job> jobs;
    private TimeConverter timeConverter = new TimeConverter();
    private ComboBox<String> algorithmComboBox = new ComboBox<>();
    private TableView<SchedulerResult> resultTableView = new TableView<>();
    private ObservableList<SchedulerResult> displayedResults = FXCollections.observableArrayList();
    private HashMap<String, GreedyScheduler> schedulers = new HashMap<>();
    private StatsBox statsBox = new StatsBox();
    private ComparisonBox comparisonBox = new ComparisonBox();
    private Label statusLabel;
    private boolean datasetModified = false;
    Tab scheduleDiagramTab = new Tab("Rozvrh");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        jobs = loader.loadFromResource("/JobSampleData100.csv");
        initialiseSchedulers();

        setTableView();
        setResultTableView();

        SplitPane splitPane = new SplitPane(tableView, statsBox);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.6);

        VBox resultsTabLayout = new VBox(10, resultTableView, comparisonBox);
        VBox.setVgrow(resultTableView, Priority.ALWAYS);

        TabPane tabPane = new TabPane();
        Tab dataTab = new Tab("Úlohy", createDatasetTableViewLayout(stage));
        Tab jobsTab = new Tab("Plánované úlohy", splitPane);
        jobsTab.setClosable(false);
        scheduleDiagramTab.setClosable(false);
        Tab resultTab = new Tab("Výsledky", resultsTabLayout);
        ChartBox chartBox = new ChartBox(displayedResults);
        Tab chartsTab = new Tab("Grafy", chartBox);
        tabPane.getTabs().addAll(dataTab, jobsTab, scheduleDiagramTab);

        WeightsDialog weightsDialog = new WeightsDialog(weights -> {
            double profitWeight = weights[0];
            double timeWeight = weights[1];
            double jobsWeight = weights[2];
            applyWeightUpdate(profitWeight, timeWeight, jobsWeight);
        });

        HBox algorithmBar = new HBox(10);
        algorithmBar.setPadding(new Insets(5));
        algorithmBar.setAlignment(Pos.CENTER_LEFT);
        algorithmBar.setStyle("-fx-background-color: rgb(239,239,239); -fx-border-color: rgb(176, 176, 176); -fx-border-width: 0 0 1 0;");
        algorithmBar.getChildren().addAll(new Label(" Algoritmus:"), algorithmComboBox);

        MenuBar menuBar = createMenuBar(stage, tabPane, dataTab, jobsTab, resultTab, chartsTab, chartBox, weightsDialog);

        VBox topContainer = new VBox(menuBar, algorithmBar);

        root.setTop(topContainer);
        root.setCenter(tableView);
        root.setCenter(tabPane);
        root.setBottom(createStatusBar());
        updateStatusLabel(null, false);

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("Kabathova Greedy Plánovač");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar(Stage stage, TabPane tabPane, Tab dataTab, Tab jobsTab, Tab resultTab, Tab chartsTab, ChartBox chartBox, WeightsDialog weightsDialog) {
        MenuBar menuBar = new MenuBar();
        // File menu
        Menu fileMenu = new Menu("Súbor");
        MenuItem uploadItem = new MenuItem("Nahrať úlohy");
        uploadItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.SHORTCUT_DOWN));
        uploadItem.setOnAction(e -> {
            ArrayList<Job> uploadedJobs = uploadJobFile(stage);
            if (!uploadedJobs.isEmpty()) {
                jobs = uploadedJobs;
                dataTableView.refresh();
                initialiseSchedulers();
                Toast.show(stage, "Úlohy boli úspešne nahrané!", Toast.ToastType.SUCCESS, 2000);
                updateStatusLabel(null, false);
            } else {
                Toast.show(stage, "Chyba pri nahrávaní úloh!", Toast.ToastType.ERROR, 2000);
            }
        });

        MenuItem exportResultItem = new MenuItem("Exportovať výsledky do CSV");
        exportResultItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
        exportResultItem.setOnAction(e -> exportManager.exportTableViewToCSV(stage, resultTableView, "vysledky.csv"));

        MenuItem exportJobsItem = new MenuItem("Exportovať úlohy do CSV");
        exportJobsItem.setAccelerator(new KeyCodeCombination(KeyCode.J, KeyCombination.SHORTCUT_DOWN));
        exportJobsItem.setOnAction(e -> exportManager.exportTableViewToCSV(stage, tableView, "ulohy_" + statsBox.getAlgorithmNameLabel().getText() + ".csv"));

        MenuItem exportScheduleDiagramItem = new MenuItem("Exportovať rozvrh do PNG");
        exportScheduleDiagramItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
        exportScheduleDiagramItem.setOnAction(e -> exportManager.exportScheduleDiagramToPNG(stage, ((ScheduleView) scheduleDiagramTab.getContent()).getScrollPaneSchedule(), statsBox.getAlgorithmNameLabel().getText()));
        exportScheduleDiagramItem.setDisable(true);

        MenuItem exportAllChartsItem = new MenuItem("Exportovať všetky grafy do PNG");
        exportAllChartsItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        exportAllChartsItem.setOnAction(e -> exportManager.exportAllCharts(stage, chartBox));
        exportAllChartsItem.setDisable(true);

        // Charts menu
        Menu chartsMenu = new Menu("Exportovať grafy do PNG");
        MenuItem profitChartItem = new MenuItem("Graf zisku");
        profitChartItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        profitChartItem.setOnAction(e -> exportManager.exportSingleChart(stage, chartBox.getProfitBarChart(), "graf_zisk.png"));
        profitChartItem.setDisable(true);

        MenuItem executionTimeChartItem = new MenuItem("Graf času vykonania");
        executionTimeChartItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        executionTimeChartItem.setOnAction(e -> exportManager.exportSingleChart(stage, chartBox.getExecutionTimeBarChart(), "graf_cas_vykonania.png"));
        executionTimeChartItem.setDisable(true);

        MenuItem jobsChartItem = new MenuItem("Graf úloh");
        jobsChartItem.setAccelerator(new KeyCodeCombination(KeyCode.J, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        jobsChartItem.setOnAction(e -> exportManager.exportSingleChart(stage, chartBox.getJobsBarChart(), "graf_ulohy.png"));
        jobsChartItem.setDisable(true);

        MenuItem totalTimeChartItem = new MenuItem("Graf celkového času");
        totalTimeChartItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        totalTimeChartItem.setOnAction(e -> exportManager.exportSingleChart(stage, chartBox.getTotalTimeBarChart(), "graf_celkovy_cas.png"));
        totalTimeChartItem.setDisable(true);

        chartsMenu.getItems().addAll(profitChartItem, executionTimeChartItem, jobsChartItem, totalTimeChartItem);

        MenuItem exitItem = new MenuItem("Ukončiť");
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(uploadItem, new SeparatorMenuItem(), exportJobsItem, exportScheduleDiagramItem, exportResultItem, exportAllChartsItem, chartsMenu, new SeparatorMenuItem(), exitItem);

        // Run menu
        Menu runMenu = new Menu("Spustiť");
        MenuItem runItem = new MenuItem("Spustiť vybraný");
        runItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        runItem.setOnAction(e -> {
            runScheduler(stage);
            tabPane.getSelectionModel().select(jobsTab);
            exportScheduleDiagramItem.setDisable(false);
        });

        MenuItem runAllItem = new MenuItem("Spustiť všetky");
        runAllItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        runAllItem.setOnAction(e -> {
            runAllSchedulers(stage, tabPane, resultTab, chartsTab, chartBox, weightsDialog);
            exportScheduleDiagramItem.setDisable(false);
            exportAllChartsItem.setDisable(false);
            profitChartItem.setDisable(false);
            executionTimeChartItem.setDisable(false);
            jobsChartItem.setDisable(false);
            totalTimeChartItem.setDisable(false);
        });

        runMenu.getItems().addAll(runItem, runAllItem);

        // Options menu
        Menu optionsMenu = new Menu("Možnosti");
        MenuItem weightsItem = new MenuItem("Váhy");
        weightsItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
        weightsItem.setOnAction(e -> weightsDialog.showAndWait(weightsDialog.getWeights()));

        optionsMenu.getItems().addAll(weightsItem);

        // View menu
        Menu viewMenu = new Menu("Zobraziť");
        MenuItem viewDataset = new MenuItem("Úlohy");
        viewDataset.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.SHORTCUT_DOWN));
        viewDataset.setOnAction(e -> {
            if (!tabPane.getTabs().contains(dataTab)) tabPane.getTabs().add(dataTab);
            tabPane.getSelectionModel().select(dataTab);
        });

        MenuItem viewJobs = new MenuItem("Plánované úlohy");
        viewJobs.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN));
        viewJobs.setOnAction(e -> {
            if (!tabPane.getTabs().contains(jobsTab)) tabPane.getTabs().add(jobsTab);
            tabPane.getSelectionModel().select(jobsTab);
        });

        MenuItem viewScheduleDiagram = new MenuItem("Rozvrh");
        viewScheduleDiagram.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN));
        viewScheduleDiagram.setOnAction(e -> {
            if (!tabPane.getTabs().contains(scheduleDiagramTab)) tabPane.getTabs().add(scheduleDiagramTab);
            tabPane.getSelectionModel().select(scheduleDiagramTab);
        });

        MenuItem viewResults = new MenuItem("Výsledky");
        viewResults.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN));
        viewResults.setOnAction(e -> {
            if (!tabPane.getTabs().contains(resultTab)) tabPane.getTabs().add(resultTab);
            tabPane.getSelectionModel().select(resultTab);
        });

        MenuItem viewCharts = new MenuItem("Grafy");
        viewCharts.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN));
        viewCharts.setOnAction(e -> {
            if (!tabPane.getTabs().contains(chartsTab)) tabPane.getTabs().add(chartsTab);
            tabPane.getSelectionModel().select(chartsTab);
        });

        viewMenu.getItems().addAll(viewDataset, viewJobs, viewScheduleDiagram, viewResults, viewCharts);

        menuBar.getMenus().addAll(fileMenu, runMenu, optionsMenu, viewMenu);

        return menuBar;
    }

    private Node createDatasetTableViewLayout(Stage stage) {
        dataTableView.getItems().clear();
        dataTableView.setEditable(true);

        TableColumn<Job, String> idCol = new TableColumn<>("ID úlohy");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(e -> {
            Job job = e.getRowValue();
            job.setID(e.getNewValue());
            notifyDatasetChanged(stage);
            dataTableView.refresh();
        });
        idCol.setMinWidth(140);

        TableColumn<Job, Integer> durationCol = new TableColumn<>("Trvanie");
        durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDuration()).asObject());
        durationCol.setCellFactory(TextFieldTableCell.forTableColumn(new ValidatedIntegerConverter(stage)));
        durationCol.setOnEditCommit(e -> {
            if (e.getNewValue() != null) {
                Job job = e.getRowValue();
                job.setDuration(e.getNewValue());
                notifyDatasetChanged(stage);
            }
            dataTableView.refresh();
        });

        TableColumn<Job, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setMinWidth(210);
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(timeConverter.minutesToDateTimeString(cellData.getValue().getDeadline())));
        deadlineCol.setCellFactory(col -> new TableCell<>() {

                    private DateTimePicker picker;

                    @Override
                    public void startEdit() {
                        super.startEdit();

                        Job job = getTableView().getItems().get(getIndex());

                        picker = new DateTimePicker(timeConverter.minutesToDateTimeString(job.getDeadline()));

                        picker.setOnKeyPressed(e -> {
                            switch (e.getCode()) {
                                case ENTER -> commitEdit(picker.getDateTimeString());
                                case ESCAPE -> cancelEdit();
                            }
                        });

                        picker.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                            if (!isFocused) {
                                commitEdit(picker.getDateTimeString());
                            }
                        });

                        setGraphic(picker);
                        setText(null);
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        updateItem(getItem(), false);
                    }

                    @Override
                    public void commitEdit(String value) {
                        Job job = getTableView().getItems().get(getIndex());

                        job.setDeadline(picker.getMinutes());

                        notifyDatasetChanged(stage);

                        super.commitEdit(value);
                        dataTableView.refresh();
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || !isEditing()) {
                            setText(item);
                            setGraphic(null);
                        }
                    }
                }

        );

        TableColumn<Job, Integer> profitCol = new TableColumn<>("Zisk");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProfit()).asObject());
        profitCol.setCellFactory(TextFieldTableCell.forTableColumn(new ValidatedIntegerConverter(stage)));
        profitCol.setOnEditCommit(e -> {
            if (e.getNewValue() != null) {
                Job job = e.getRowValue();
                job.setProfit(e.getNewValue());
                notifyDatasetChanged(stage);
            }
            dataTableView.refresh();
        });

        dataTableView.getColumns().addAll(idCol, durationCol, deadlineCol, profitCol);
        dataTableView.setItems(FXCollections.observableList(jobs));

        Button addBtn = new Button("Pridať úlohu");
        addBtn.setOnAction(e -> {
            AddJobDialog addJobDialog = new AddJobDialog();
            addJobDialog.showAndWait();
            Job addedJob = addJobDialog.getAddedJob();
            if (addedJob != null) {
                jobs.add(addedJob);
                notifyDatasetChanged(stage);
                dataTableView.refresh();
            }
        });

        Button deleteBtn = new Button("Vymazať úlohu");
        deleteBtn.setOnAction(e -> {
            Job selectedJob = dataTableView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                deleteConfirmation.setTitle("Vymazanie");
                deleteConfirmation.setHeaderText("Vymazanie úlohy");
                deleteConfirmation.setContentText("Naozaj si prajete vymazať označenú úlohu?");
                deleteConfirmation.showAndWait().ifPresent(event -> {
                    if (event == ButtonType.OK) {
                        jobs.remove(selectedJob);
                        notifyDatasetChanged(stage);
                        dataTableView.refresh();
                    }
                });
            }
        });

        HBox dataButtonsControls = new HBox(10);
        dataButtonsControls.setAlignment(Pos.CENTER);
        dataButtonsControls.getChildren().addAll(addBtn, deleteBtn);

        VBox dataLayout = new VBox(10);
        dataLayout.getChildren().addAll(dataTableView, dataButtonsControls);
        dataLayout.setPadding(new Insets(15));
        VBox.setVgrow(dataTableView, Priority.ALWAYS);

        return dataLayout;
    }

    private void setTableView() {
        tableView.getColumns().clear();

        TableColumn<ScheduledJob, String> idCol = new TableColumn<>("ID úlohy");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        idCol.setMinWidth(140);

        TableColumn<ScheduledJob, Integer> durationCol = new TableColumn<>("Trvanie");
        durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDuration()).asObject());

        TableColumn<ScheduledJob, String> startTimeCol = new TableColumn<>("Začiatok");
        startTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(timeConverter.minutesToDateTimeString(cellData.getValue().getStartTime())));
        startTimeCol.setMinWidth(120);
        startTimeCol.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<ScheduledJob, String> endTimeCol = new TableColumn<>("Koniec");
        endTimeCol.setMinWidth(120);
        endTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(timeConverter.minutesToDateTimeString(cellData.getValue().getEndTime())));

        TableColumn<ScheduledJob, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setMinWidth(120);
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(timeConverter.minutesToDateTimeString(cellData.getValue().getDeadline())));

        TableColumn<ScheduledJob, Integer> profitCol = new TableColumn<>("Zisk");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProfit()).asObject());

        tableView.getColumns().addAll(idCol, durationCol, startTimeCol, endTimeCol, deadlineCol, profitCol);
        tableView.getSortOrder().add(startTimeCol);
        tableView.setItems(displayedJobs);
    }

    private void setResultTableView() {
        resultTableView.getColumns().clear();

        TableColumn<SchedulerResult, String> nameCol = new TableColumn<>("Algoritmus");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlgorithmName()));

        TableColumn<SchedulerResult, Integer> scheduledJobsCountCol = new TableColumn<>("Plánované úlohy");
        scheduledJobsCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScheduledJobsCount()).asObject());

        TableColumn<SchedulerResult, Integer> jobsCountCol = new TableColumn<>("Celkový počet úloh");
        jobsCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUnscheduledJobsCount()).asObject());

        TableColumn<SchedulerResult, Integer> profitCol = new TableColumn<>("Celkový zisk");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalProfit()).asObject());

        TableColumn<SchedulerResult, Integer> timeCol = new TableColumn<>("Celkový čas");
        timeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalTimeUsed()).asObject());

        TableColumn<SchedulerResult, Double> executionTimeCol = new TableColumn<>("Čas vykonania");
        executionTimeCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getExecutionTimeMillis()).asObject());

        TableColumn<SchedulerResult, Double> scoreCol = new TableColumn<>("Skóre");
        scoreCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getScore()).asObject());

        resultTableView.getColumns().addAll(nameCol, scheduledJobsCountCol, jobsCountCol, profitCol, timeCol, executionTimeCol, scoreCol);
        resultTableView.getColumns().forEach(col -> col.setMinWidth(105));
        nameCol.setMinWidth(160);
        jobsCountCol.setMinWidth(140);
        resultTableView.setItems(displayedResults);
    }

    private void setupResultTableViewInteractions(SchedulerResult bestResult) {
        resultTableView.setRowFactory(tv -> {
            TableRow<SchedulerResult> row = new TableRow<>() {
                @Override
                public void updateItem(SchedulerResult item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (item.getAlgorithmName().equals(bestResult.getAlgorithmName())) {
                        setStyle("-fx-background-color: gold; -fx-border-color: #d5b000;");
                    } else {
                        setStyle("");
                    }
                }
            };

            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    SchedulerResult schedulerResult = row.getItem();
                    SchedulerDetailsDialog schedulerDetailsDialog = new SchedulerDetailsDialog(schedulerResult.getAlgorithmName(), schedulers.get(schedulerResult.getAlgorithmName()).getScheduledJobs());
                    schedulerDetailsDialog.showAndWait();
                }
            });
            return row;
        });
    }

    private void setAlgorithmComboBox() {
        algorithmComboBox.getItems().clear();
        algorithmComboBox.getItems().addAll(schedulers.keySet());
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

    private void runScheduler(Stage stage) {
        if (datasetModified) {
            initialiseSchedulers();
            datasetModified = false;
            Toast.show(stage, "Plánovanie aktualizované.", Toast.ToastType.INFO, 2000);
        }
        String selected = algorithmComboBox.getValue();
        GreedyScheduler scheduler = schedulers.get(selected);
        if (scheduler != null) {
            getAlgorithmResult(scheduler);
            scheduleDiagramTab.setContent(new ScheduleView(scheduler.getScheduledJobs(), timeConverter));
            tableView.sort();
            updateStatusLabel(selected, true);
        } else {
            System.out.println("Plánovač sa nenašiel " + selected);
        }
    }

    private void runAllSchedulers(Stage stage, TabPane tabPane, Tab resultTab, Tab chartsTab, ChartBox chartBox, WeightsDialog weightsDialog) {
        if (datasetModified) {
            initialiseSchedulers();
            datasetModified = false;
            Toast.show(stage, "Plánovanie aktualizované.", Toast.ToastType.INFO, 2000);
        }

        getAllResults();
        chartBox.setResultObservableList(displayedResults);

        double profitWeight = weightsDialog.getProfitWeight();
        double timeWeight = weightsDialog.getTimeWeight();
        double jobsWeight = weightsDialog.getJobsWeight();

        for (SchedulerResult result : displayedResults) {
            result.calculateScore(profitWeight, timeWeight, jobsWeight);
        }
        comparisonBox.update(displayedResults);

        ArrayList<TableColumn<SchedulerResult, ?>> sortOrder = new ArrayList<>(resultTableView.getSortOrder());
        resultTableView.refresh();
        setupResultTableViewInteractions(getBestResult());
        resultTableView.getSortOrder().setAll(sortOrder);
        resultTableView.sort();
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

    private SchedulerResult getBestResult() {
        SchedulerResult best = Collections.max(displayedResults, Comparator.comparingDouble(SchedulerResult::getScore));
        setBestResult(best.getAlgorithmName());
        return best;
    }

    private void setBestResult(String name) {
        GreedyScheduler scheduler = schedulers.get(name);
        if (scheduler != null) {
            getAlgorithmResult(scheduler);
            scheduleDiagramTab.setContent(new ScheduleView(scheduler.getScheduledJobs(), timeConverter));
        } else {
            System.out.println("Plánovač sa nenašiel " + name);
        }
    }

    private void getAllResults() {
        displayedResults.clear();
        for (GreedyScheduler scheduler : schedulers.values()) {
            SchedulerResult schedulerResult = scheduler.getResult();
            displayedResults.add(schedulerResult);
        }
    }

    private void applyWeightUpdate(double profitWeight, double timeWeight, double jobsWeight) {
        for (SchedulerResult result : displayedResults) {
            result.calculateScore(profitWeight, timeWeight, jobsWeight);
        }

        resultTableView.refresh();
        setupResultTableViewInteractions(getBestResult());
        comparisonBox.update(displayedResults);
        resultTableView.sort();
    }

    private ArrayList<Job> uploadJobFile(Stage stage) {
        fileChooser.setTitle("Vyberte súbor .csv");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV súbory", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                return loader.loadFromFile(selectedFile);
            } catch (Exception e) {
                Toast.show(stage, "Nahranie súboru zlyhalo", Toast.ToastType.ERROR, 2500);
            }
        }
        return new ArrayList<>();
    }

    private HBox createStatusBar() {
        statusLabel = new Label();
        statusLabel.setPadding(new Insets(5, 10, 5, 10));

        HBox statusBar = new HBox(statusLabel);
        statusBar.setStyle("-fx-background-color: rgb(239,239,239); -fx-border-color: rgb(176, 176, 176); -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(Pos.CENTER_LEFT);

        return statusBar;
    }

    private void updateStatusLabel(String algorithmName, boolean runInfo) {
        String base = "Dataset: " + loader.getFileName() + " | Počet úloh: " + jobs.size();
        if (runInfo) {
            if (algorithmName != null) {
                base += " | Algoritmus: " + algorithmName;
            }
            base += " | Posledné spustenie: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"));
        }
        statusLabel.setText(base);
    }

    private void markDatasetAsModified() {
        datasetModified = true;
    }

    private void notifyDatasetChanged(Stage stage) {
        markDatasetAsModified();
        Toast.show(stage, "Údaje datasetu boli zmenené. Spustite opäť plánovanie.", Toast.ToastType.WARNING, 3000);
        statusLabel.setText("Nastala zmena údajov v datasete. Spustite znova plánovanie. Zmena nastala " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    public static void main(String[] args) {
        launch();
    }
}