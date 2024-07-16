package faang.school.postservice.kafka.producer;

import faang.school.postservice.event.Event;

public interface MessageProducer<T extends Event> {
    void publish(T event);
}
