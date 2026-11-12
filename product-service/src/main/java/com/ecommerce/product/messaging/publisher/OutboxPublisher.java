package com.ecommerce.product.messaging.publisher;

import com.ecommerce.product.entity.OutboxEvent;
import com.ecommerce.product.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private static final int BATCH_SIZE = 100;

    private final OutboxEventService outboxEventService;
    private final ProductEventPublisher productEventPublisher;
    private final Executor applicationTaskExecutor;

    @Scheduled(fixedDelayString = "${outbox.polling-delay-ms:3000}")
    public void publishOutboxEvents() {
        List<OutboxEvent> events;

        try {
            events = outboxEventService.claimPendingEvents(BATCH_SIZE);
        } catch (Exception e) {
            log.error("Failed to claim Outbox events", e);
            return;
        }

        for (OutboxEvent event : events) {
            log.info(
                    "Scheduler thread: {} - dispatch eventId: {}",
                    Thread.currentThread().getName(),
                    event.getId()
            );

            applicationTaskExecutor.execute(() -> publishEvent(event));
        }
    }

    private void publishEvent(OutboxEvent event) {
        log.info(
                "Worker thread: {} - processing eventId: {}",
                Thread.currentThread().getName(),
                event.getId()
        );

        try {
            // Wait for Kafka to confirm the send result.
            productEventPublisher.publish(event).join();

            // Mark SENT only after Kafka confirms success.
            outboxEventService.markAsSent(event.getId());

            log.info(
                    "Published Outbox event successfully. eventId={}, topic={}",
                    event.getId(),
                    event.getTopic()
            );

        } catch (CompletionException e) {
            String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            handlePublishFailure(event, errorMessage);
        } catch (Exception e) {
            handlePublishFailure(event, e.getMessage());
        }
    }

    private void handlePublishFailure(OutboxEvent event, String errorMessage) {
        log.error(
                "Failed to publish Outbox event. eventId={}, topic={}, error={}",
                event.getId(),
                event.getTopic(),
                errorMessage
        );

        try {
            outboxEventService.markForRetry(event.getId(), errorMessage);
        } catch (Exception e) {
            log.error(
                    "Failed to update Outbox event after publish failure. eventId={}",
                    event.getId(),
                    e
            );
        }
    }
}
