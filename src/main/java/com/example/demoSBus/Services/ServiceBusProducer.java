package com.example.demoSBus.Services;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServiceBusProducer {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBusProducer.class);

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${azure.servicebus.queue-name}")
    private String queueName;

    public void sendMessage(String message) {
        if (connectionString == null || connectionString.isEmpty()) {
            logger.error("Azure Service Bus connection string is not configured.");
            throw new IllegalArgumentException("Service Bus connection string cannot be null or empty.");
        }
        if (queueName == null || queueName.isEmpty()) {
            logger.error("Azure Service Bus queue name is not configured.");
            throw new IllegalArgumentException("Queue name cannot be null or empty.");
        }

        try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient()) {

            ServiceBusMessage serviceBusMessage = new ServiceBusMessage(message);
            senderClient.sendMessage(serviceBusMessage);

            logger.info("Message sent to queue '{}': {}", queueName, message);

        } catch (Exception e) {
            logger.error("Error sending message to Service Bus queue '{}': {}", queueName, e.getMessage());
            throw new RuntimeException("Failed to send message to Azure Service Bus", e);
        }
    }
}
