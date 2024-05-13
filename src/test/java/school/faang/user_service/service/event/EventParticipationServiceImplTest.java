package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationServiceImpl;

    @Test
    public void testRegisterParticipantThrowsExceptionIfAlreadyRegistered() {
        long userId = 1L;
        long eventId = 1L;
        List<User> participants = new ArrayList<>();
        User user = new User();
        user.setId(userId);
        participants.add(user);
        when(eventParticipationRepository.findAllParticipantsByEventId(userId)).thenReturn(participants);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> eventParticipationServiceImpl.registerParticipant(eventId, userId));
    }

    @Test
    public void testRegisterParticipantRegistersIfNotAlreadyRegistered() {
        long userId = 1L;
        long eventId = 1L;
        List<User> participants = new ArrayList<>();
        when(eventParticipationRepository.findAllParticipantsByEventId(userId)).thenReturn(participants);

        eventParticipationServiceImpl.registerParticipant(eventId, userId);

        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantThrowsExceptionIfNotRegister() {
        long userId = 1L;
        long eventId = 1L;
        List<User> participants = new ArrayList<>();
        when(eventParticipationRepository.findAllParticipantsByEventId(userId)).thenReturn(participants);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> eventParticipationServiceImpl.unregisterParticipant(eventId, userId));
    }

    @Test
    public void testUnregisterParticipantUnregisteredIfRegistered() {
        long userId = 1L;
        long eventId = 1L;
        List<User> participants = new ArrayList<>();
        User user = new User();
        user.setId(userId);
        participants.add(user);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        eventParticipationServiceImpl.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    public void testGetParticipantReturnCorrectList() {
        long userId = 1L;
        long eventId = 1L;
        List<User> participants = new ArrayList<>();
        User user = new User();
        user.setId(userId);
        participants.add(user);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        List<User> actualParticipants = eventParticipationServiceImpl.getParticipants(eventId);

        Assertions.assertEquals(participants, actualParticipants);
    }

    @Test
    public void testGetParticipantsCountReturnsCorrectCount() {
        long eventId = 1L;
        Integer count = 5;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(count);

        Integer actualCount = eventParticipationServiceImpl.getParticipantsCount(eventId);
        Assertions.assertEquals(count, actualCount);

    }
}