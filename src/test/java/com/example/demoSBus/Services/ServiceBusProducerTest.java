package com.example.demoSBus.Services;

package com.example.demoSBus.Services;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

class ServiceBusProducerTest {

    private ServiceBusProducer serviceBusProducer;

    @Mock
    private ServiceBusClientBuilder serviceBusClientBuilder;

    @Mock
    private ServiceBusClientBuilder.SenderBuilder senderBuilder;

    @Mock
    private ServiceBusSenderClient senderClient;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        serviceBusProducer = new ServiceBusProducer() {
            // Override the logger to use a mock for verification
            private final Logger mockLogger = logger;

            @Override
            public Logger getLogger() {
                return mockLogger;
            }
        };

        // Mock the dependencies
        when(serviceBusClientBuilder.sender()).thenReturn(senderBuilder);
        when(senderBuilder.queueName(anyString())).thenReturn(senderBuilder);
        when(senderBuilder.buildClient()).thenReturn(senderClient);

        // Inject values for connection string and queue name
        serviceBusProducer.connectionString = "fake-connection-string";
        serviceBusProducer.queueName = "test-queue";
    }

    @Test
    void testSendMessage_Success() {
        // Arrange
        String message = "Test message";
        doNothing().when(senderClient).sendMessage(any(ServiceBusMessage.class));

        // Act
        serviceBusProducer.sendMessage(message);

        // Assert
        verify(senderClient, times(1)).sendMessage(any(ServiceBusMessage.class));
        verify(logger, times(1)).info("Message sent to queue '{}': {}", "test-queue", message);
    }

    @Test
    void testSendMessage_Exception() {
        // Arrange
        String message = "Test message";
        doThrow(new RuntimeException("Service Bus error")).when(senderClient).sendMessage(any(ServiceBusMessage.class));

        // Act
        serviceBusProducer.sendMessage(message);

        // Assert
        verify(logger, times(1)).error(eq("Error sending message to Service Bus"), any(RuntimeException.class));
    }
}

