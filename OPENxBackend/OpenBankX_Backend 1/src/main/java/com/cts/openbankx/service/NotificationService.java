package com.cts.openbankx.service;

import com.cts.openbankx.enums.NotificationCategory;
import com.cts.openbankx.enums.NotificationStatus;
import com.cts.openbankx.model.Notification;
import com.cts.openbankx.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification send(String recipientType,
                             String recipientId,
                             String message) {
        return send(recipientType, recipientId, message, NotificationCategory.USAGE);
    }

    public Notification send(String recipientType,
                             String recipientId,
                             String message,
                             NotificationCategory category) {

        Notification n = new Notification();
        n.setRecipientType(recipientType);
        n.setRecipientId(recipientId);
        n.setMessage(message);
        n.setCategory(category);
        n.setStatus(NotificationStatus.UNREAD);
        n.setCreatedDate(LocalDateTime.now());

        return repo.save(n);
    }



    public void notifyUser(Long userId, String message, NotificationCategory category) {
        send("USER", userId.toString(), message, category);
    }

    public void notifyAdmin(String message, NotificationCategory category) {
        send("ROLE", "ADMIN", message, category);
    }

    public void notifyOperations(String message, NotificationCategory category) {
        send("ROLE", "OPERATIONS", message, category);
    }

    public void notifyTpp(Long userId, String message, NotificationCategory category) {
        send("USER", userId.toString(), message, category);
    }
}