package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimePicker extends HBox {
    private DatePicker datePicker;
    private Spinner<Integer> hourSpinner;
    private Spinner<Integer> minuteSpinner;
    private TimeConverter timeConverter;

    public DateTimePicker() {
        initialise(LocalDateTime.of(2025, 9, 1, 7, 30));
    }

    public DateTimePicker(String dateTimeString){
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, TimeConverter.FORMAT);
        initialise(dateTime);

    }

    private void initialise(LocalDateTime initialDateTime){
        setSpacing(5);
        setAlignment(Pos.CENTER);

        timeConverter = new TimeConverter();

        datePicker = new DatePicker(initialDateTime.toLocalDate());

        hourSpinner = new Spinner<>(0, 23, initialDateTime.getHour());
        hourSpinner.setEditable(true);

        minuteSpinner = new Spinner<>(0, 55, initialDateTime.getMinute(), 5);
        minuteSpinner.setEditable(true);

        getChildren().addAll(datePicker, new Label(" "), hourSpinner, new Label(":"), minuteSpinner);
    }

    public String getDateTimeString() {
        LocalDate date = datePicker.getValue();
        LocalDateTime dateTime = LocalDateTime.of(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                hourSpinner.getValue(),
                minuteSpinner.getValue()
        );
        return dateTime.format(TimeConverter.FORMAT);
    }

    public int getMinutes() {
        return timeConverter.deadlineToMinutes(getDateTimeString());
    }
}
