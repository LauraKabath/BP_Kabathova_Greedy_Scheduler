package sk.ukf.bp_kabathova_greedy_scheduler;

import java.io.*;
import java.util.ArrayList;

public class DataLoader {
    private ArrayList<Job> jobs;
    private String fileName;
    private TimeConverter timeConverter;

    public DataLoader(String fileName) {
        this.fileName = fileName;
        timeConverter = new TimeConverter();
    }

    public DataLoader() {
        jobs = new ArrayList<>();
        timeConverter = new TimeConverter();
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
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split("[;,]");
                if (data.length < 4) continue;
                String jobID = data[0].trim();
                int duration = Integer.parseInt(data[1].trim());
                int deadline = timeConverter.deadlineToMinutes(data[2].trim());
                int profit = Integer.parseInt(data[3].trim());
                Job job = new Job(jobID, duration, deadline, profit);
                jobs.add(job);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally{
            try {
                if (br != null) br.close();
            } catch (IOException ioException){
                System.out.println(ioException.getMessage());
            }
        }
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public String getFileName() {
        return fileName;
    }
}
