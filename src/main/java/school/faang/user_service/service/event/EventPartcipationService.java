package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Configuration
public class EventPartcipationService {

    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventPartcipationService(EventParticipationRepository eventParticipationRepository){
        this.eventParticipationRepository = eventParticipationRepository;
    }
    @Bean
    public void registerParticipant(long eventId, long userId){
        List<User> usersOnCurrentEvent = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        for(User user : usersOnCurrentEvent){
            if(user.getId() == userId){
                try {
                    throw new Exception("Пользователь уже зарегистрирован");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        eventParticipationRepository.register(eventId, userId);
    }
    @Bean
    public void unregisterParticipant(long userId, long eventId){
        List<User> usersOnCurrentEvent = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for(User user : usersOnCurrentEvent){
            if(user.getId() == userId){
                eventParticipationRepository.unregister(eventId, userId);
            }
        }
        try {
            throw new Exception("Пользователь не найден");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    public List<User> getParticipant(long eventId){
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }
    @Bean
    public int getParticipantsCount(long eventId){
        return eventParticipationRepository.countParticipants(eventId);
    }
}
