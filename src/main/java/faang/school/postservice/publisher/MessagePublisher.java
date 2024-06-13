package faang.school.postservice.publisher;

public interface MessagePublisher<T> {
    void publish(T event);
}
