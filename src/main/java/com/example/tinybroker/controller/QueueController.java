package com.example.tinybroker.controller;

import com.example.tinybroker.model.Message;
import com.example.tinybroker.model.PublishRequest;
import com.example.tinybroker.model.QueueStatsResponse;
import com.example.tinybroker.service.BrokerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/queues")

public class QueueController {

    private final BrokerService brokerService;

    public QueueController(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @PostMapping("/publish")
    public ResponseEntity<Message> publish(@RequestBody PublishRequest request) {
        Message message = brokerService.publish(request.getQueueName(), request.getPayload());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{queueName}/consume")
    public ResponseEntity<Message> consume(@PathVariable String queueName) {
        Optional<Message> message = brokerService.consume(queueName);
        return message.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/ack/{messageId}")
    public ResponseEntity<String> ack(@PathVariable String messageId) {
        boolean acked = brokerService.ack(messageId);
        if (!acked) {
            return ResponseEntity.badRequest().body("Unable to ack message");
        }
        return ResponseEntity.ok("Message acknowledged");
    }

    @PostMapping("/fail/{messageId}")
    public ResponseEntity<String> fail(@PathVariable String messageId) {
        boolean failed = brokerService.fail(messageId);
        if (!failed) {
            return ResponseEntity.badRequest().body("Unable to fail message");
        }
        return ResponseEntity.ok("Message marked failed or requeued");
    }

    @GetMapping("/{queueName}/stats")
    public ResponseEntity<QueueStatsResponse> stats(@PathVariable String queueName) {
        return ResponseEntity.ok(brokerService.getStats(queueName));
    }
}