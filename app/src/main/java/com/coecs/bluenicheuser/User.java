package com.coecs.bluenicheuser;

public class User {

    private String uid,firstname,lastname,userType,workerCoverLocation;
    private double userRate;
    private int job;
    private double workerJobRate;

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getWorkerCoverLocation() {
        return workerCoverLocation;
    }

    public void setWorkerCoverLocation(String workerCoverLocation) {
        this.workerCoverLocation = workerCoverLocation;
    }

    public double getUserRate() {
        return userRate;
    }

    public void setUserRate(double userRate) {
        this.userRate = userRate;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public double getWorkerJobRate() {
        return workerJobRate;
    }

    public void setWorkerJobRate(double workerJobRate) {
        this.workerJobRate = workerJobRate;
    }
}
