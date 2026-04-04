package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateStringConverter extends StringConverter<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DateStringConverter() {}

    @Override
    public String toString(LocalDate date) {
        if (date != null) return formatter.format(date);
        else return "";
    }

    @Override
    public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) return LocalDate.parse(string, formatter);
        else return null;
    }
}
