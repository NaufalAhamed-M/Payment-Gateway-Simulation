package com.example.payment.repository;

import com.example.payment.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

    Optional<WebhookEvent> findByEventReference(String eventReference);
}
