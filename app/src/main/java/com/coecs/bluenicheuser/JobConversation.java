package com.coecs.bluenicheuser;

public class JobConversation {

    private String jobId;
    private String workerUID;
    private String userUID;

    public JobConversation() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getWorkerUID() {
        return workerUID;
    }

    public void setWorkerUID(String workerUID) {
        this.workerUID = workerUID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
