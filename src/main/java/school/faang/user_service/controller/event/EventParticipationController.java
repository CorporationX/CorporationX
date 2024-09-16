package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.event.EventPartcipationService;


@Controller
public class EventParticipationController {
    private EventPartcipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventPartcipationService eventParticipationService){
        this.eventParticipationService = eventParticipationService;
    }
    @Bean
    public void registerParticipant(long userId, long eventId){
        eventParticipationService.registerParticipant(userId, eventId);
    }
    @Bean
    public void unregisterParticipant(long userId, long eventId){
        eventParticipationService.unregisterParticipant(userId, eventId);
    }
    @Bean
    public void getParticipant(long eventId){
        eventParticipationService.getParticipant(eventId);
    }
    @Bean
    public void getParticipantsCount(long eventId){
        eventParticipationService.getParticipantsCount(eventId);
    }

}
