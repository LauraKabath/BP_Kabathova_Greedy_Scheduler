package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class AddJobDialog extends Dialog<Job> {
    private Job addedJob;
    private TextField jobName;
    private TextField jobDuration;
    private DateTimePicker jobDeadlinePicker;
    private TimeConverter timeConverter;
    private TextField jobProfit;
    private Stage stage;

    public AddJobDialog() {
        super();
        timeConverter = new TimeConverter();
        this.setTitle("Pridajte úlohu");
        addedJob = null;
        this.stage = (Stage) this.getDialogPane().getScene().getWindow();
        buildDialog();
    }

    private void buildDialog() {
        Pane pane = createGridPane();
        getDialogPane().setContent(pane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            if (!validateDialog()) {
                e.consume();
            } else {
                addedJob = new Job(jobName.getText(), Integer.parseInt(jobDuration.getText()), jobDeadlinePicker.getMinutes(), Integer.parseInt(jobProfit.getText()));
            }
        });
        cancelButton.addEventFilter(ActionEvent.ACTION, e -> this.close());
    }

    public Job getAddedJob() {
        return addedJob;
    }

    private boolean validateDialog() {
        if ((jobName.getText().isEmpty()) || (jobDuration.getText().isEmpty()) || (jobProfit.getText().isEmpty())) {
            Toast.show(stage, "Vyplňte všetky polia!", Toast.ToastType.WARNING, 2500);
            return false;
        }

        if (!isIntegerValid(jobDuration.getText()) || !isIntegerValid(jobProfit.getText())) {
            Toast.show(stage, "Zadajte platné celé čísla väčšie alebo rovné nule.", Toast.ToastType.WARNING, 2500);
            return false;
        }
        return true;
    }

    private boolean isIntegerValid(String value) {
        return value.matches("\\d+") && Integer.parseInt(value) >= 0;
    }

    private Pane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(8);
        gridPane.setPadding(new Insets(15));

        Label idLabel = new Label("ID úlohy");
        Label durationLabel = new Label("Trvanie");
        Label deadlineLabel = new Label("Deadline");
        Label profitLabel = new Label("Zisk");

        jobName = new TextField();
        jobName.setPromptText("Zadajte názov/ID úlohy");
        jobName.setFocusTraversable(false);

        jobDuration = new TextField();
        jobDuration.setPromptText("Zadajte trvanie úlohy");
        jobDuration.setFocusTraversable(false);
        attachListener(jobDuration);

        jobDeadlinePicker = new DateTimePicker();

        jobProfit = new TextField();
        jobProfit.setPromptText("Zadajte zisk/prioritu úlohy");
        jobProfit.setFocusTraversable(false);
        attachListener(jobProfit);

        gridPane.add(idLabel, 0, 0);
        gridPane.add(jobName, 1, 0);
        GridPane.setHgrow(jobName, Priority.ALWAYS);

        gridPane.add(durationLabel, 0, 1);
        gridPane.add(jobDuration, 1, 1);
        GridPane.setHgrow(jobDuration, Priority.ALWAYS);

        gridPane.add(deadlineLabel, 0, 2);
        gridPane.add(jobDeadlinePicker, 1, 2);
        GridPane.setHgrow(jobDeadlinePicker, Priority.ALWAYS);

        gridPane.add(profitLabel, 0, 3);
        gridPane.add(jobProfit, 1, 3);
        GridPane.setHgrow(jobProfit, Priority.ALWAYS);

        return gridPane;
    }

    private void attachListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("\\D", ""));
            }
        });

    }

}
