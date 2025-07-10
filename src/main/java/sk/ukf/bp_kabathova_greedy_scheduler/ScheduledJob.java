package sk.ukf.bp_kabathova_greedy_scheduler;

public class ScheduledJob extends Job {
    private int startTime;
    private int endTime;
    public ScheduledJob(String ID, int duration, int deadline, int profit, int startTime) {
        super(ID, duration, deadline, profit);
        this.startTime = startTime;
        endTime = startTime +  duration;
    }

    public ScheduledJob(Job job, int startTime) {
        super(job.getID(), job.getDuration(), job.getDeadline(), job.getProfit());
        this.startTime = startTime;
        endTime = startTime + job.getDuration();
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return ID + " {" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", deadline=" + deadline +
                ", profit=" + profit +
                '}';
    }
}
