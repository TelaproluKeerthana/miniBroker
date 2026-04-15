package com.example.tinybroker.model;

import java.time.Instant;

public class Message {
    private String id;
    private String queueName;
    private String payload;
    private MessageStatus status;
    private Instant createdAt;
    private Instant visibleAt;
    private int retryCount;

    public Message() {
    }

    public Message(String id, String queueName, String payload, MessageStatus status,
                   Instant createdAt, Instant visibleAt, int retryCount) {
        this.id = id;
        this.queueName = queueName;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
        this.visibleAt = visibleAt;
        this.retryCount = retryCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getVisibleAt() {
        return visibleAt;
    }

    public void setVisibleAt(Instant visibleAt) {
        this.visibleAt = visibleAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}