package pl.createcompetition.tournamentservice.kafka;


import static pl.createcompetition.tournamentservice.kafka.MessageReadConst.Topics.MESSAGE_READ_EVENTS;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.createcompetition.tournamentservice.kafka.domain.EventPublisher;
import pl.createcompetition.tournamentservice.kafka.domain.InternalEvent;

@RequiredArgsConstructor
@Service
public class KafkaAsyncEventPublisher implements EventPublisher<InternalEvent> {

    private final KafkaTemplate<String, InternalEvent> kafkaTemplate;

    @Override
    public void send(InternalEvent event) {
        ProducerRecord<String, InternalEvent> eventToSend = new ProducerRecord<>(
            MESSAGE_READ_EVENTS, event.getKey(), event);
        kafkaTemplate.send(eventToSend);

    }
}
