package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.ArrayList;

public class ShortestJobFirstScheduler extends GreedyScheduler{
    public ShortestJobFirstScheduler(ArrayList<Job> unscheduledJobs) {
        super(unscheduledJobs);
    }

    @Override
    protected void schedule() {
        unscheduledJobs.sort((j1, j2) -> Integer.compare(j1.getDuration(), j2.getDuration()));
        allocateJobs();
    }
}
