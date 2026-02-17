package com.pagamentos.wallet.service;

import com.pagamentos.wallet.service.dto.TransferNotificationDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransferNotification(TransferNotificationDto notification) {
        try {
            logger.info("Enviando notificação de transferência para o tópico 'transaction-success': {}", notification);

            // Envia a mensagem. A chave é o ID da transação (garante ordem se necessário)
            kafkaTemplate.send("transaction-success", String.valueOf(notification.transactionId()), notification);

        } catch (Exception e) {
            // Em um cenário real, poderíamos salvar em uma tabela de "dead-letter" para tentar de novo depois
            logger.error("Erro ao enviar mensagem para o Kafka", e);
        }
    }
}