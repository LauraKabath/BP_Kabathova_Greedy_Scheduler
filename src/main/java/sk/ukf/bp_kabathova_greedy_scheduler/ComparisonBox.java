package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Comparator;

public class ComparisonBox extends HBox {
    public ComparisonBox() {
        setSpacing(15);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
    }

    public void update(ObservableList<SchedulerResult> results) {
        getChildren().clear();

        ObservableList<SchedulerResult> topResults = FXCollections.observableArrayList(results);
        topResults.sort(Comparator.comparingDouble(SchedulerResult::getScore).reversed());

        if (topResults.size() > 3) {
            topResults.remove(3, topResults.size());
        }

        for (SchedulerResult result : topResults) {
            VBox card = createCard(result);
            getChildren().add(card);
        }
    }

    private VBox createCard(SchedulerResult result) {
        Label title = new Label(result.getAlgorithmName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label profit = new Label("Zisk: " + result.getTotalProfit());
        Label executionTime = new Label("Čas: " + result.getExecutionTimeMillis() + " ms");
        Label jobs = new Label("Plánované úlohy: " + result.getScheduledJobsCount() + "/" + result.getUnscheduledJobsCount());
        Label score = new Label("Skóre: " + result.getScore());

        VBox card = new VBox(5, title, profit, executionTime, jobs, score);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: rgb(224,233,255); -fx-border-color: rgb(70,108,180); -fx-border-radius: 4; -fx-background-radius: 4;");
        return card;
    }
}

