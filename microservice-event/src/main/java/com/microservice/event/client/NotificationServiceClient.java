package com.microservice.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "microservice-notification")
@RequestMapping("/notifications")
public interface NotificationServiceClient {

    @PostMapping("/send")
    void sendNotification(@RequestBody NotificationRequest notificationRequest);

    class NotificationRequest {
        private String recipientId;
        private String message;

        public NotificationRequest() {}

        public NotificationRequest(String recipientId, String message) {
            this.recipientId = recipientId;
            this.message = message;
        }

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
