package com.example.tinybroker.model;

public class QueueStatsResponse {
    private String queueName;
    private int readyCount;
    private int inFlightCount;
    private int ackedCount;
    private int failedCount;

    public QueueStatsResponse(String queueName, int readyCount, int inFlightCount, int ackedCount, int failedCount) {
        this.queueName = queueName;
        this.readyCount = readyCount;
        this.inFlightCount = inFlightCount;
        this.ackedCount = ackedCount;
        this.failedCount = failedCount;
    }

    public String getQueueName() {
        return queueName;
    }

    public int getReadyCount() {
        return readyCount;
    }

    public int getInFlightCount() {
        return inFlightCount;
    }

    public int getAckedCount() {
        return ackedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }
}