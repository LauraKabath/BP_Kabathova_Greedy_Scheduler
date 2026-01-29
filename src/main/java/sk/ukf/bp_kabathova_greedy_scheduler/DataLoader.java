package sk.ukf.bp_kabathova_greedy_scheduler;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DataLoader {
    private ArrayList<Job> jobs;
    private ArrayList<String> jobDates;
    private String fileName;
    private TimeConverter timeConverter;
    private String errorMessage;

    public DataLoader(String fileName) {
        this.fileName = fileName;
        jobDates = new ArrayList<>();
        timeConverter = new TimeConverter();
        errorMessage = "";
    }

    public DataLoader() {
        jobs = new ArrayList<>();
        jobDates = new ArrayList<>();
        timeConverter = new TimeConverter();
        errorMessage = "";
    }


    public ArrayList<Job> loadFromResource(String filepath) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(filepath);
            fileName = filepath.substring(1);
            if (inputStream == null) {
                throw new FileNotFoundException(filepath + " not found");
            }
            loadJobs(new InputStreamReader(inputStream));
        } catch (Exception e) {
            errorMessage = e.getMessage();
            clearArrayLists();
        }
        return jobs;
    }

    public ArrayList<Job> loadFromFile(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            fileName = file.getName();
            loadJobs(fileReader);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            clearArrayLists();
        }
        return jobs;
    }

    private void loadJobs(Reader reader) {
        clearArrayLists();
        errorMessage = "";
        BufferedReader br = null;
        int lineNumber = 2;
        try {
            br = new BufferedReader(reader);
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split("[;,]");
                if (data.length != 4) throw new IllegalArgumentException("Nesprávny počet stĺpcov v datasete.");
                String jobID = data[0].trim();
                if (jobID.isEmpty()) throw new IllegalArgumentException("Prázdne ID úlohy na riadku " + lineNumber + ".");
                int duration = Integer.parseInt(data[1].trim());
                if (duration <= 0)
                    throw new IllegalArgumentException("Neplatné trvanie úlohy na riadku " + lineNumber + ".\nTrvanie musí byť kladné číslo.");
                jobDates.add(data[2].trim());
                int profit = Integer.parseInt(data[3].trim());
                if (profit < 0)
                    throw new IllegalArgumentException("Neplatný profit úlohy na riadku" + lineNumber + ".\nProfit musí byť kladné číslo.");
                Job job = new Job(jobID, duration, 0, profit);
                jobs.add(job);
                lineNumber++;
            }
            setBaseDateTimeFromEarliest();
            applyDeadlines();
        } catch (NumberFormatException numberError) {
            clearArrayLists();
            errorMessage = "Niektorá číselná hodnota na riadku " + lineNumber + ", nie je celé číslo.\nSkontrolujte trvanie a profit.";
        } catch (Exception e) {
            clearArrayLists();
            errorMessage = e.getMessage();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ioException) {
                clearArrayLists();
                errorMessage = ioException.getMessage();
            }
        }
    }

    private void setBaseDateTimeFromEarliest() {
        LocalDate earliestDate = null;
        int lineNumber = 2;
        try {
            for (String dateString : jobDates) {
                LocalDateTime dateTime = LocalDateTime.parse(dateString, TimeConverter.FORMAT);
                LocalDate date = dateTime.toLocalDate();
                if (earliestDate == null || date.isBefore(earliestDate)) {
                    earliestDate = date;
                }
                lineNumber++;
            }

            if (earliestDate != null) {
                TimeConverter.setBaseTime(earliestDate.atTime(7, 0));
            }
        } catch (Exception e) {
            errorMessage = "Deadline má neplatný formát dátumu alebo času na riadku "  + lineNumber + ".";
            clearArrayLists();
        }
    }

    private void applyDeadlines() {
        for (int i = 0; i < jobs.size(); i++) {
            jobs.get(i).setDeadline(timeConverter.deadlineToMinutes(jobDates.get(i)));
        }
    }

    private void clearArrayLists() {
        jobs.clear();
        jobDates.clear();
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public String getFileName() {
        return fileName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
