package pl.createcompetition.tournamentservice.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import pl.createcompetition.tournamentservice.kafka.domain.InternalEvent;
import pl.createcompetition.tournamentservice.kafka.domain.KafkaProducerConfigData;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducerConfigData kafkaProducerConfigData;

    @Bean
    public KafkaTemplate<String, InternalEvent> kafkaTemplate(final ProducerFactory<String, InternalEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, InternalEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerConfig());
    }

    private Map<String, Object> getProducerConfig() {

        final Map<String, Object> properties = new HashMap<>();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        properties.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
        properties.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
        properties.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());


        properties.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaProducerConfigData.getReconnectionBackoffMs());
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProducerConfigData.getRetryBackoffMs());


        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");


        return properties;
    }
}