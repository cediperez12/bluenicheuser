package com.coecs.bluenicheuser;

public class Job {

    private String pushId;
    private String JobTitle;
    private String JobDescription;
    private double pay;
    private User user;
    private User worker;
    private long dateStart;
    private long dateEnd;
    private String status;
    private int workerRating;
    private int userRating;
    private double profit;
    private int workerRatingConfirmed;
    private int userRatingConfirmed;

    public int getWorkerRatingConfirmed() {
        return workerRatingConfirmed;
    }

    public void setWorkerRatingConfirmed(int workerRatingConfirmed) {
        this.workerRatingConfirmed = workerRatingConfirmed;
    }

    public int getUserRatingConfirmed() {
        return userRatingConfirmed;
    }

    public void setUserRatingConfirmed(int userRatingConfirmed) {
        this.userRatingConfirmed = userRatingConfirmed;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public void setJobTitle(String jobTitle) {
        JobTitle = jobTitle;
    }

    public String getJobDescription() {
        return JobDescription;
    }

    public void setJobDescription(String jobDescription) {
        JobDescription = jobDescription;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getWorkerRating() {
        return workerRating;
    }

    public void setWorkerRating(int workerRating) {
        this.workerRating = workerRating;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public Job() {
    }

    public Job(String PushId){
        pushId = PushId;
    }


}
