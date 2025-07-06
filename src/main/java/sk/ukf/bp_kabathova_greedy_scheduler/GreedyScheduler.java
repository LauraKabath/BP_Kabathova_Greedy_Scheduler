package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.*;

public abstract class GreedyScheduler {
    protected ArrayList<Job> unscheduledJobs;
    protected ArrayList<Job> scheduledJobs;
    protected int totalProfit;

    public GreedyScheduler(ArrayList<Job> unscheduledJobs) {
        this.unscheduledJobs = new ArrayList<>(unscheduledJobs);
        scheduledJobs = new ArrayList<>();
        totalProfit = 0;
    }

    public abstract ArrayList<Job> schedule();

    protected int getTimeSlotsCount(ArrayList<Job> jobs) {
        int maxCount = 0;
        for (Job job : jobs) {
            if (job.getDeadline() > maxCount) maxCount = job.getDeadline();
        }
        return maxCount;
    }

    protected void countTotalProfit(){
        totalProfit = 0;
        for  (Job job : scheduledJobs) {
            totalProfit += job.getProfit();
        }
    }

    public ArrayList<Job> getUnscheduledJobs() {
        return unscheduledJobs;
    }

    public ArrayList<Job> getScheduledJobs() {
        return scheduledJobs;
    }

    public int getTotalProfit() {
        return totalProfit;
    }
}
