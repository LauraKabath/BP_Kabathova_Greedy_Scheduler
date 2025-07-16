package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.*;

public class HighestProfitScheduler extends GreedyScheduler {
    public HighestProfitScheduler(ArrayList<Job> unscheduledJobs) {
        super(unscheduledJobs);
    }

    @Override
    protected void schedule() {
        unscheduledJobs.sort((j1, j2) -> Integer.compare(j2.getProfit(), j1.getProfit()));
        allocateJobs();
    }
}
