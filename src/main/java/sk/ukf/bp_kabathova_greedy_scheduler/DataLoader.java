package sk.ukf.bp_kabathova_greedy_scheduler;

import java.io.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class DataLoader {
    private ArrayList<Job> jobs;
    private String fileName;
    private TimeConverter timeConverter;
    private String errorMessage;

    public DataLoader(String fileName) {
        this.fileName = fileName;
        timeConverter = new TimeConverter();
        errorMessage = "";
    }

    public DataLoader() {
        jobs = new ArrayList<>();
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
            System.out.println(e.getMessage());
        }
        return jobs;
    }

    public ArrayList<Job> loadFromFile(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            fileName = file.getName();
            loadJobs(fileReader);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobs;
    }

    private void loadJobs(Reader reader){
        jobs.clear();
        errorMessage = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split("[;,]");
                if (data.length < 4) throw new IllegalArgumentException("Nesprávny počet stĺpcov v datasete.");
                String jobID = data[0].trim();
                if (jobID.isEmpty()) throw new IllegalArgumentException("ID úlohy nemôže byť prázdne.");
                int duration = Integer.parseInt(data[1].trim());
                if (duration <= 0)
                    throw new IllegalArgumentException("Neplatné trvanie úlohy. Trvanie musí byť kladné číslo.");
                int deadline = timeConverter.deadlineToMinutes(data[2].trim());
                if (deadline < 0) throw new IllegalArgumentException("Neplatný deadline úlohy.");
                int profit = Integer.parseInt(data[3].trim());
                if (profit < 0)
                    throw new IllegalArgumentException("Neplatný profit úlohy. Profit musí byť kladné číslo.");
                Job job = new Job(jobID, duration, deadline, profit);
                jobs.add(job);
            }
        } catch (NumberFormatException numberError){
            jobs.clear();
            errorMessage = "Niektorá číselná hodnota nie je celé číslo. Skontrolujte trvanie a profit.";
        } catch (Exception e) {
            jobs.clear();
            errorMessage = e.getMessage();
        } finally{
            try {
                if (br != null) br.close();
            } catch (IOException ioException){
                jobs.clear();
                errorMessage = ioException.getMessage();
            }
        }
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
