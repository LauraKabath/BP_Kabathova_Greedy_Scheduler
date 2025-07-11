package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    private TableView<ScheduledJob> tableView = new TableView<>();
    private ObservableList<ScheduledJob> displayedJobs =  FXCollections.observableArrayList();
    private ArrayList<Job> jobs;
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        DataLoader loader = new DataLoader();
        jobs = loader.getJobs();

        setTableView();

        Button highestProfitButton = new Button("Highest Profit");
        highestProfitButton.setOnAction(e -> {
            displayedJobs.setAll(new HighestProfitScheduler(jobs).schedule());
        });
        Button earliestDeadlineButton = new Button("Earliest Deadline");
        earliestDeadlineButton.setOnAction(e -> {
            displayedJobs.setAll(new EarliestDeadlineScheduler(jobs).schedule());
        });
        Button shortestJobFirstButton = new Button("Shortest Job First");
        shortestJobFirstButton.setOnAction(e -> {
            displayedJobs.setAll(new ShortestJobFirstScheduler(jobs).schedule());
        });

        HBox controls = new HBox(10);
        controls.getChildren().addAll(highestProfitButton, earliestDeadlineButton, shortestJobFirstButton);

        root.setTop(controls);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 900, 500);
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

        TableColumn<ScheduledJob, Integer> endTimeCol = new TableColumn<>("End Time");
        endTimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEndTime()).asObject());

        TableColumn<ScheduledJob, Integer> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDeadline()).asObject());

        TableColumn<ScheduledJob, Integer> profitCol = new TableColumn<>("Profit");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProfit()).asObject());

        tableView.getColumns().addAll(idCol, durationCol, startTimeCol, endTimeCol, deadlineCol, profitCol);
        tableView.setItems(displayedJobs);
    }

    public static void main(String[] args) {
        launch();
    }
}