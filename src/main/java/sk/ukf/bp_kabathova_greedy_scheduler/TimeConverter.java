package sk.ukf.bp_kabathova_greedy_scheduler;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConverter {
    public static final DateTimeFormatter FORMAT =  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static LocalDateTime baseTime = LocalDateTime.of(2025, 9, 1, 7,0);
    private int workStartHour;
    private int workEndHour;

    TimeConverter() {
        this.workStartHour = 7;
        this.workEndHour = 22;
    }

    public static void setBaseTime(LocalDateTime baseTime) {
        TimeConverter.baseTime = baseTime;
    }

    TimeConverter(int workStartHour, int workEndHour) {
        this.workStartHour = workStartHour;
        this.workEndHour = workEndHour;
    }

    public int getWorkStartHour() {
        return workStartHour;
    }

    public void setWorkStartHour(int workStartHour) {
        this.workStartHour = workStartHour;
    }

    public int getWorkEndHour() {
        return workEndHour;
    }

    public void setWorkEndHour(int workEndHour) {
        this.workEndHour = workEndHour;
    }

    public int deadlineToMinutes(String deadlineString){
        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineString, FORMAT);
            long minutes = Duration.between(baseTime, deadline).toMinutes();
            if (minutes < 0) return 0;
            return (int) minutes;
        } catch (DateTimeException e){
            throw new IllegalArgumentException("Deadline má neplatný formát dátumu alebo času.");
        }
    }

    public String minutesToDateTimeString(int minutes){
        LocalDateTime datetime = baseTime.plusMinutes(minutes);
        return datetime.format(FORMAT);
    }

}
