package sk.ukf.bp_kabathova_greedy_scheduler;

import java.util.ArrayList;

public class ProfitPerDurationScheduler extends GreedyScheduler {
    public ProfitPerDurationScheduler(ArrayList<Job> unscheduledJobs) {
        super(unscheduledJobs);
    }

    @Override
    public ArrayList<ScheduledJob> schedule() {
        unscheduledJobs.sort((j1, j2) -> {
            double ratio1 = (double) j1.getProfit() / j1.getDuration();
            double ratio2 = (double) j2.getProfit() / j2.getDuration();
            return Double.compare(ratio2, ratio1);
        });
        allocateJobs();
        return scheduledJobs;
    }
}
