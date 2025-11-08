package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ValidatedIntegerConverter extends StringConverter<Integer> {
    private final Stage stage;

    public ValidatedIntegerConverter(Stage stage) {
        this.stage = stage;
    }

    @Override
    public String toString(Integer integer) {
        if (integer == null) return "";
        else return integer.toString();
    }

    @Override
    public Integer fromString(String s) {
        if (s == null || s.trim().isEmpty()) {
            Toast.show(stage, "Zadajte číslo!", Toast.ToastType.WARNING, 2500);
            return null;
        }

        try {
            int i = Integer.parseInt(s.trim());
            if (i < 0) {
                Toast.show(stage, "Zadajte celé číslo väčšie alebo rovné nule.", Toast.ToastType.WARNING, 2500);
                return null;
            }
            return i;
        } catch (NumberFormatException e) {
            Toast.show(stage, "Zadajte platné celé číslo!", Toast.ToastType.WARNING, 2500);
            return null;
        }
    }
}
