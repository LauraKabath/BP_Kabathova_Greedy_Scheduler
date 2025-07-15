package sk.ukf.bp_kabathova_greedy_scheduler;

public class SchedulerResult {
    private String algorithmName;
    private int totalProfit;
    private int totalTimeUsed;
    private int scheduledJobsCount;
    private int unscheduledJobsCount;
    private long executionTime;

    public SchedulerResult(String algorithmName, int totalProfit, int totalTimeUsed, int scheduledJobsCount, int unscheduledJobsCount, long executionTime) {
        this.algorithmName = algorithmName;
        this.totalProfit = totalProfit;
        this.totalTimeUsed = totalTimeUsed;
        this.scheduledJobsCount = scheduledJobsCount;
        this.unscheduledJobsCount = unscheduledJobsCount;
        this.executionTime = executionTime;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public int getTotalTimeUsed() {
        return totalTimeUsed;
    }

    public int getScheduledJobsCount() {
        return scheduledJobsCount;
    }

    public int getUnscheduledJobsCount() {
        return unscheduledJobsCount;
    }

    public long getExecutionTimeNano() {
        return executionTime;
    }

    public double getExecutionTimeMillis() {
        return executionTime / 1000000.0;
    }

    @Override
    public String toString() {
        return "SchedulerResult {" +
                "algorithmName='" + algorithmName + '\'' +
                ", totalProfit=" + totalProfit +
                ", totalTimeUsed=" + totalTimeUsed +
                ", scheduledJobsCount=" + scheduledJobsCount +
                ", unscheduledJobsCount=" + unscheduledJobsCount +
                ", executionTime=" + executionTime +
                '}';
    }
}

