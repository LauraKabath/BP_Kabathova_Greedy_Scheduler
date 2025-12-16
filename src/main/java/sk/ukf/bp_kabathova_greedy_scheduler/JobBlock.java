package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class JobBlock extends StackPane {
    private final ScheduledJob scheduledJob;
    private final Label jobLabel;

    public JobBlock(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
        int duration = scheduledJob.getDuration();
        jobLabel = new Label(scheduledJob.getID() + " (" + duration + " min)");
        jobLabel.setWrapText(true);

        getBlockStyle();

        double y;
        if (scheduledJob.getStartTime() > 900){
            y = scheduledJob.getStartTime() % (24 * 60);
        } else {
            y = scheduledJob.getStartTime();
        }

        setLayoutY(y);
        setPrefHeight(duration);
        setPrefWidth(120);

        getChildren().add(jobLabel);
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    private void getBlockStyle() {
        switch (scheduledJob.getProfit() / 170) {
            case 0:
                setStyle("-fx-background-color: #dfecfb;-fx-border-color: #becee8;");
                break;
            case 1:
                setStyle("-fx-background-color: #B4D6FE;-fx-border-color: #88a7d5;");
                break;
            case 2:
                setStyle("-fx-background-color: #57A5FF;-fx-border-color: #2c5b8a;");
                break;
            case 3:
                setStyle("-fx-background-color: #4f8ed1;-fx-border-color: #294c68;");
                break;
            default:
                setStyle("-fx-background-color: #05458F;-fx-border-color: #022857;");
                jobLabel.setTextFill(Color.WHITE);
                break;
        }
    }
}
