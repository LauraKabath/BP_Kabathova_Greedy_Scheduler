package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

public class ScheduleView extends BorderPane {
    public ScheduleView(ArrayList<ScheduledJob> scheduledJobs, TimeConverter timeConverter) {
        HBox daysBox = new HBox();
        daysBox.setSpacing(5);

        TreeMap<LocalDate, DayColumn> dayColumns = new TreeMap<>();

        for (ScheduledJob scheduledJob : scheduledJobs) {
            LocalDate date = LocalDateTime.parse(timeConverter.minutesToDateTimeString(scheduledJob.getStartTime()), TimeConverter.FORMAT).toLocalDate();
            dayColumns.putIfAbsent(date, new DayColumn(date));
        }

        for (DayColumn column : dayColumns.values()) {
            VBox dayWrapper = new VBox(new Label(column.getDateLabel().getText()), column);
            dayWrapper.setSpacing(5);
            daysBox.getChildren().add(dayWrapper);
        }

        for (ScheduledJob scheduledJob : scheduledJobs) {
            LocalDate date = LocalDateTime.parse(timeConverter.minutesToDateTimeString(scheduledJob.getStartTime()), TimeConverter.FORMAT).toLocalDate();
            JobBlock block = new JobBlock(scheduledJob);
            dayColumns.get(date).addJob(block);
        }

        for (DayColumn column : dayColumns.values()) {
            column.sortJobsByStartTime();
        }

        setCenter(new ScrollPane(new HBox(new TimeAxis(), daysBox)));
    }
}
