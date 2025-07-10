package sk.ukf.bp_kabathova_greedy_scheduler;

public class Job {
    protected String ID;
    protected int duration;
    protected int deadline;
    protected int profit;

    public Job(String ID, int duration, int deadline, int profit) {
        this.ID = ID;
        this.duration = duration;
        this.deadline = deadline;
        this.profit = profit;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    @Override
    public String toString() {
        return ID + " {" +
                ", duration=" + duration +
                ", deadline=" + deadline +
                ", profit=" + profit +
                '}';
    }
}
