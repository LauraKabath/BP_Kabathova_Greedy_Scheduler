package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DayColumn extends Pane {
    private final Label dateLabel;
    private final ArrayList<JobBlock> jobs;

    public DayColumn(LocalDate date) {
        this.dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy (EEEE)")));
        this.jobs = new ArrayList<>();
        setPrefWidth(120);
        setStyle("-fx-border-color: #e3e2e2;");
    }

    public void addJob(JobBlock block) {
        jobs.add(block);
    }

    public void sortJobsByStartTime() {
        jobs.sort((b1, b2) -> Integer.compare(b1.getScheduledJob().getStartTime(), b2.getScheduledJob().getStartTime()));
        getChildren().addAll(jobs);
    }

    public Label getDateLabel() {
        return dateLabel;
    }

}
