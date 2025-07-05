package sk.ukf.bp_kabathova_greedy_scheduler;

import java.io.*;
import java.util.ArrayList;

public class DataLoader {
    private String file;
    private ArrayList<Job> jobs;

    public DataLoader() {
        file =  "/JobSampleData.csv";
        jobs = new ArrayList<>();
    }

    public DataLoader(String file) {
        this.file = file;
        jobs = new ArrayList<>();
    }

    public ArrayList<Job> getJobs() {
        BufferedReader br = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream(file);
            if (inputStream == null) {
                throw new FileNotFoundException(file + " not found");
            }

            br = new BufferedReader(new InputStreamReader(inputStream));
            String line =  br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                String jobID = data[0];

                int duration = Integer.parseInt(data[1]);
                int deadline = Integer.parseInt(data[2]);
                int profit = Integer.parseInt(data[3]);

                Job job = new Job(jobID, duration, deadline, profit);
                jobs.add(job);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally{
            try {
                if (br != null) br.close();
            } catch (IOException ioException){
                System.out.println(ioException.getMessage());
            }
        }
        return jobs;
    }
}
