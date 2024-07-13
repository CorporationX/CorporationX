package faang.school.postservice.publisher;

import faang.school.postservice.event.Event;

public interface MessagePublisher<T extends Event> {
    void publish(T event);
}
