package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventPartcipationService;


@Component
public class EventParticipationController {
    private EventPartcipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventPartcipationService eventParticipationService){
        this.eventParticipationService = eventParticipationService;
    }
    //внедрить бин класса EventParticipationService
}
