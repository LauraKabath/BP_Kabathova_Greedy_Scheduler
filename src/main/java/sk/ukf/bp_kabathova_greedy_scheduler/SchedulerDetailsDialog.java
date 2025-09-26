package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SchedulerDetailsDialog {
    private Stage detailsStage;
    private TableView<ScheduledJob> jobsTableView;

    public SchedulerDetailsDialog(String schedulerName, ArrayList<ScheduledJob> scheduledJobs) {
        setupTableView(scheduledJobs);
        initDetailsDialog(schedulerName);
    }

    private void setupTableView(ArrayList<ScheduledJob> scheduledJobs) {
        jobsTableView = new TableView<>();

        TableColumn<ScheduledJob, String> idCol = new TableColumn<>("ID úlohy");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        idCol.setMinWidth(140);

        TableColumn<ScheduledJob, Integer> profitCol = new TableColumn<>("Zisk");
        profitCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProfit()).asObject());
        profitCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ScheduledJob, Integer> startTimeCol = new TableColumn<>("Začiatok");
        startTimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStartTime()).asObject());
        startTimeCol.setSortType(TableColumn.SortType.ASCENDING);
        startTimeCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ScheduledJob, Integer> endTimeCol = new TableColumn<>("Koniec");
        endTimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEndTime()).asObject());
        endTimeCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ScheduledJob, Integer> durationCol = new TableColumn<>("Trvanie");
        durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDuration()).asObject());
        durationCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ScheduledJob, Integer> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDeadline()).asObject());
        deadlineCol.setStyle("-fx-alignment: CENTER;");


        jobsTableView.getColumns().addAll(idCol, profitCol, startTimeCol, endTimeCol, durationCol, deadlineCol);
        jobsTableView.setItems(FXCollections.observableArrayList(scheduledJobs));
        jobsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        jobsTableView.getSortOrder().add(startTimeCol);
    }

    private void initDetailsDialog(String name) {
        detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.setTitle(name + " - poradie vykonávania úloh");

        VBox layout = new VBox(10, jobsTableView);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout);
        detailsStage.setScene(scene);
        detailsStage.setResizable(false);
    }

    public void showAndWait() {
        detailsStage.showAndWait();
    }
}
