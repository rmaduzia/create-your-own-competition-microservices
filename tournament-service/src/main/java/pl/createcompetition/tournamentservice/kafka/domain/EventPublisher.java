package pl.createcompetition.tournamentservice.kafka.domain;

public interface EventPublisher <E extends InternalEvent> {

    void send(final E event);
}
