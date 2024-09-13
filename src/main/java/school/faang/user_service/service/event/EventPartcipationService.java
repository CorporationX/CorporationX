package school.faang.user_service.service.event;

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

    // добавить конструктор класса и аннотацию
    @Bean
    public void registerParticipant(long eventId, long userId){ // добавить в качестве параметра ивент
        //if(userId == ) сделать проверку на то, что пользователь уже зарегистрирован на событие
        List<User> usersOnCurrentEvent = eventParticipationRepository.findAllParticipantsByEventId(eventId); // для поиска всех участников по id события

        for(User user : usersOnCurrentEvent){
            if(user.getId() == userId){
                // ВЫКИНУТЬ ИСКЛЮЧЕНИЕ
                return;
            }
        }
        eventParticipationRepository.register(eventId, userId);
    }
}
