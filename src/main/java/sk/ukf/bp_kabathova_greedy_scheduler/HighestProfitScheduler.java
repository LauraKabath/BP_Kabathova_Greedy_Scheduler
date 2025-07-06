package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.*;

public class HighestProfitScheduler extends GreedyScheduler {
    public HighestProfitScheduler(ArrayList<Job> unscheduledJobs) {
        super(unscheduledJobs);
    }

    @Override
    public ArrayList<Job> schedule() {
        unscheduledJobs.sort((j1, j2) -> Integer.compare(j2.getProfit(), j1.getProfit()));

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
                    scheduledJobs.add(job);
                    break;
                }
            }
        }

        this.countTotalProfit();
        return scheduledJobs;
    }
}
