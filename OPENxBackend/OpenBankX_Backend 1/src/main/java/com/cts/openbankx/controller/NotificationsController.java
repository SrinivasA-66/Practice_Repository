package com.cts.openbankx.controller;

import com.cts.openbankx.enums.NotificationStatus;
import com.cts.openbankx.model.Notification;
import com.cts.openbankx.service.NotificationService;
import com.cts.openbankx.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    private final NotificationService service;
    private final NotificationRepository repo;

    public NotificationsController(NotificationService service, NotificationRepository repo) {
        this.service = service;
        this.repo = repo;
    }


    @GetMapping
    public List<Notification> getAll(
            @RequestParam(required = false) String recipientId,
            @RequestParam(required = false) String role) {

        List<Notification> result = new ArrayList<>();


        if (recipientId != null && !recipientId.isEmpty()) {
            result.addAll(repo.findByRecipientId(recipientId));
        }


        if (role != null && !role.isEmpty()) {
            List<Notification> roleNotifs = repo.findByRecipientId(role);

            for (Notification n : roleNotifs) {
                if (result.stream().noneMatch(r -> r.getNotificationId().equals(n.getNotificationId()))) {
                    result.add(n);
                }
            }
        }


        if ((recipientId == null || recipientId.isEmpty()) && (role == null || role.isEmpty())) {
            return repo.findAll();
        }


        result.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        return result;
    }

    @PutMapping("/{id}")
    public Notification update(@PathVariable Long id, @RequestBody Notification update) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (update.getStatus() != null) {
            n.setStatus(update.getStatus());
        }
        return repo.save(n);
    }

    @PutMapping("/mark-all-read")
    public void markAllRead(@RequestParam String recipientId,
                            @RequestParam(required = false) String role) {
        List<Notification> unread = repo.findByRecipientIdAndStatus(recipientId, NotificationStatus.UNREAD);
        for (Notification n : unread) {
            n.setStatus(NotificationStatus.READ);
            repo.save(n);
        }
        if (role != null && !role.isEmpty()) {
            List<Notification> roleUnread = repo.findByRecipientIdAndStatus(role, NotificationStatus.UNREAD);
            for (Notification n : roleUnread) {
                n.setStatus(NotificationStatus.READ);
                repo.save(n);
            }
        }
    }

    @PostMapping
    public Notification send(@RequestParam String recipientType,
                             @RequestParam String recipientId,
                             @RequestParam String message) {
        return service.send(recipientType, recipientId, message);
    }
}