package org.Roclh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Async("ws")
    public void notifySpaceMarineUpdate() {
        log.info("Sending WebSocket update notification");
        try {
            messagingTemplate.convertAndSend("/topic/spacemarines", "refresh");
        } catch (Exception e) {
            log.error("Error sending WebSocket notification: {}", e.getMessage(), e);
        }
    }
}
