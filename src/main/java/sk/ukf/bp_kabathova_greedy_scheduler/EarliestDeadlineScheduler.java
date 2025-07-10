package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.ArrayList;

public class EarliestDeadlineScheduler extends GreedyScheduler {
    public EarliestDeadlineScheduler(ArrayList<Job> unscheduledJobs) {
        super(unscheduledJobs);
    }

    @Override
    public ArrayList<ScheduledJob> schedule() {
        unscheduledJobs.sort((j1, j2) -> Integer.compare(j1.getDeadline(), j2.getDeadline()));
        allocateJobs();
        return scheduledJobs;
    }
}
