package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.*;

public abstract class GreedyScheduler {
    protected static final int WORK_START_MIN = 0;
    protected static final int WORK_END_MIN = 15 * 60;
    protected static final int DAY_MINUTES = 24 * 60;
    protected ArrayList<Job> unscheduledJobs;
    protected ArrayList<ScheduledJob> scheduledJobs;
    protected SchedulerResult result;
    protected int maxSlotsCount;
    protected int slotsCount;
    protected int totalProfit;

    public GreedyScheduler(ArrayList<Job> unscheduledJobs) {
        this.unscheduledJobs = new ArrayList<>(unscheduledJobs);
        scheduledJobs = new ArrayList<>();
        totalProfit = 0;
        maxSlotsCount = getTimeSlotsCount();
        slotsCount = 0;
    }

    protected abstract void schedule();

    public SchedulerResult getResult() {
        if (result == null) {
            long start = System.nanoTime();
            schedule();
            long end = System.nanoTime();
            result = new SchedulerResult(getName(), totalProfit, slotsCount, scheduledJobs.size(), unscheduledJobs.size(), end - start);
        }
        return result;
    }

    protected void allocateJobs() {
        boolean[] timeSlots = new boolean[maxSlotsCount];
        for (Job job : unscheduledJobs) {
            int deadline = job.getDeadline();
            int duration = job.getDuration();
            int latestStart = deadline - duration;

            for (int start = latestStart; start >= 0; start--){
                if (!fitsInWorkingHours(start, duration)) continue;

                boolean fit = true;

                for (int i = start; i < start + duration; i++){
                    if (i >= maxSlotsCount || timeSlots[i]){
                        fit = false;
                        break;
                    }
                }

                if (fit){
                    for (int j = start; j < start + duration; j++){
                        timeSlots[j] = true;
                        slotsCount++;
                    }
                    scheduledJobs.add(new ScheduledJob(job, start));
                    break;
                }
            }
        }

        countTotalProfit();
    }

    protected boolean fitsInWorkingHours(int start, int duration) {
        int end = start + duration;
        int current = start;

        while (current < end) {
            int minuteInDay = current % DAY_MINUTES;
            if (minuteInDay < WORK_START_MIN || minuteInDay >= WORK_END_MIN) {
                return false;
            }
            current++;
        }

        return true;
    }

    protected int getTimeSlotsCount() {
        if (maxSlotsCount == 0) {
            for (Job job : unscheduledJobs) {
                if (job.getDeadline() > maxSlotsCount) maxSlotsCount = job.getDeadline();
            }
        }
        return maxSlotsCount;
    }

    protected void countTotalProfit(){
        totalProfit = 0;
        for  (ScheduledJob job : scheduledJobs) {
            totalProfit += job.getProfit();
        }
    }

    protected String getName(){
        return getClass().getSimpleName();
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

    public int getSlotsCount() {
        return slotsCount;
    }
}
