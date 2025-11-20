package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DeadletterService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var deadletterService = new DeadletterService();
        try (var service = new KafkaService(DeadletterService.class.getSimpleName(),
                "ECOMMERCE_DEADLETTER",
                deadletterService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("------------------------------------------");
        System.out.println("Mensagem com erro!");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }
}