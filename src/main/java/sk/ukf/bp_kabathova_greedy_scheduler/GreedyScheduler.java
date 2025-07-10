package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.*;

public abstract class GreedyScheduler {
    protected ArrayList<Job> unscheduledJobs;
    protected ArrayList<ScheduledJob> scheduledJobs;
    protected int totalProfit;

    public GreedyScheduler(ArrayList<Job> unscheduledJobs) {
        this.unscheduledJobs = new ArrayList<>(unscheduledJobs);
        scheduledJobs = new ArrayList<>();
        totalProfit = 0;
    }

    public abstract ArrayList<ScheduledJob> schedule();

    protected void allocateJobs() {
        int maxSlotCount = this.getTimeSlotsCount(unscheduledJobs);
        boolean[] timeSlots = new boolean[maxSlotCount];

        for (Job job : unscheduledJobs) {
            int deadline = job.getDeadline();
            int duration = job.getDuration();
            int latestStart = deadline - duration;

            for (int start = latestStart; start >= 0; start--){
                boolean fit = true;

                for (int i = start; i < start + duration; i++){
                    if (i >= maxSlotCount || timeSlots[i]){
                        fit = false;
                        break;
                    }
                }

                if (fit){
                    for (int j = start; j < start + duration; j++){
                        timeSlots[j] = true;
                    }
                    scheduledJobs.add(new ScheduledJob(job, start));
                    break;
                }
            }
        }

        countTotalProfit();
    }

    protected int getTimeSlotsCount(ArrayList<Job> jobs) {
        int maxCount = 0;
        for (Job job : jobs) {
            if (job.getDeadline() > maxCount) maxCount = job.getDeadline();
        }
        return maxCount;
    }

    protected void countTotalProfit(){
        totalProfit = 0;
        for  (ScheduledJob job : scheduledJobs) {
            totalProfit += job.getProfit();
        }
    }

    public ArrayList<Job> getUnscheduledJobs() {
        return unscheduledJobs;
    }

    public ArrayList<ScheduledJob> getScheduledJobs() {
        return scheduledJobs;
    }

    public int getTotalProfit() {
        return totalProfit;
    }
}
