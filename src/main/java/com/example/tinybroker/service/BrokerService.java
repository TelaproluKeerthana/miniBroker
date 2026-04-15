package com.example.tinybroker.service;

import com.example.tinybroker.model.Message;
import com.example.tinybroker.model.MessageStatus;
import com.example.tinybroker.model.QueueStatsResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BrokerService {

    private final Map<String, Deque<String>> queues = new ConcurrentHashMap<>();
    private final Map<String, Message> messageStore = new ConcurrentHashMap<>();
    private final Map<String, Object> queueLocks = new ConcurrentHashMap<>();

    private static final long VISIBILITY_TIMEOUT_SECONDS = 15;
    private static final int MAX_RETRIES = 3;

    public Message publish(String queueName, String payload) {
        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();

        Message message = new Message(
                id,
                queueName,
                payload,
                MessageStatus.READY,
                now,
                now,
                0
        );

        messageStore.put(id, message);

        Deque<String> queue = queues.computeIfAbsent(queueName, k -> new ArrayDeque<>());
        Object lock = queueLocks.computeIfAbsent(queueName, k -> new Object());

        synchronized (lock) {
            queue.offerLast(id);
        }

        return message;
    }

    public Optional<Message> consume(String queueName) {
        Deque<String> queue = queues.computeIfAbsent(queueName, k -> new ArrayDeque<>());
        Object lock = queueLocks.computeIfAbsent(queueName, k -> new Object());

        synchronized (lock) {
            int size = queue.size();
            Instant now = Instant.now();

            for (int i = 0; i < size; i++) {
                String messageId = queue.pollFirst();
                if (messageId == null) {
                    return Optional.empty();
                }

                Message message = messageStore.get(messageId);
                if (message == null) {
                    continue;
                }

                boolean consumable =
                        message.getStatus() == MessageStatus.READY &&
                        !message.getVisibleAt().isAfter(now);

                if (consumable) {
                    message.setStatus(MessageStatus.IN_FLIGHT);
                    message.setVisibleAt(now.plusSeconds(VISIBILITY_TIMEOUT_SECONDS));

                    queue.offerLast(messageId);
                    return Optional.of(message);
                } else {
                    queue.offerLast(messageId);
                }
            }
        }

        return Optional.empty();
    }

    public boolean ack(String messageId) {
        Message message = messageStore.get(messageId);
        if (message == null) {
            return false;
        }

        if (message.getStatus() != MessageStatus.IN_FLIGHT) {
            return false;
        }

        message.setStatus(MessageStatus.ACKED);
        return true;
    }

    public boolean fail(String messageId) {
        Message message = messageStore.get(messageId);
        if (message == null) {
            return false;
        }

        if (message.getStatus() != MessageStatus.IN_FLIGHT) {
            return false;
        }

        int newRetryCount = message.getRetryCount() + 1;
        message.setRetryCount(newRetryCount);

        if (newRetryCount >= MAX_RETRIES) {
            message.setStatus(MessageStatus.FAILED);
        } else {
            message.setStatus(MessageStatus.READY);
            message.setVisibleAt(Instant.now());
        }

        return true;
    }

    public void requeueExpiredInFlightMessages() {
        Instant now = Instant.now();

        for (Message message : messageStore.values()) {
            if (message.getStatus() == MessageStatus.IN_FLIGHT &&
                !message.getVisibleAt().isAfter(now)) {

                int newRetryCount = message.getRetryCount() + 1;
                message.setRetryCount(newRetryCount);

                if (newRetryCount >= MAX_RETRIES) {
                    message.setStatus(MessageStatus.FAILED);
                } else {
                    message.setStatus(MessageStatus.READY);
                    message.setVisibleAt(now);
                }
            }
        }
    }

    public QueueStatsResponse getStats(String queueName) {
        int ready = 0;
        int inFlight = 0;
        int acked = 0;
        int failed = 0;

        for (Message message : messageStore.values()) {
            if (!queueName.equals(message.getQueueName())) {
                continue;
            }

            switch (message.getStatus()) {
                case READY -> ready++;
                case IN_FLIGHT -> inFlight++;
                case ACKED -> acked++;
                case FAILED -> failed++;
            }
        }

        return new QueueStatsResponse(queueName, ready, inFlight, acked, failed);
    }
}