package com.example.tinybroker.scheduler;

import com.example.tinybroker.service.BrokerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequeueScheduler {

    private final BrokerService brokerService;

    public RequeueScheduler(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @Scheduled(fixedRate = 3000)
    public void requeueExpiredMessages() {
        brokerService.requeueExpiredInFlightMessages();
    }
}