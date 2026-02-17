package com.pagamentos.wallet.consumer;

import com.pagamentos.wallet.service.dto.TransferNotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @KafkaListener(topics = "transaction-success", groupId = "wallet-notification-group")
    public void consume(TransferNotificationDto notification) {
        System.out.println("=====================================================");
        System.out.println("RECEBIDO NO KAFKA: Transferência realizada!");
        System.out.println("Valor: " + notification.amount());
        System.out.println("De: " + notification.payerId());
        System.out.println("Para: " + notification.payeeId());
        System.out.println("=====================================================");

    }
}