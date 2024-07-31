package pl.createcompetition.tournamentservice.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.kafka")
@Configuration
@Getter
@Setter
public class KafkaProperties {

    String bootstrapServers;

    Map<String, Object> getSafeSettings() {

        final Map<String, Object> properties = new HashMap<>();

        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        return properties;
    }

}
