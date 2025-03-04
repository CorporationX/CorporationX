package school.faang.user_service.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Nested
    class GetMenteesTests {

        @Test
        void getMentees_shouldReturnDtoList_whenMenteesExist() {

            Long mentorId = 1L;
            User mentee = new User();
            mentee.setId(2L);
            mentee.setUsername("menteeUser");
            mentee.setEmail("mentee@example.com");

            List<User> mentees = List.of(mentee);
            when(mentorshipRepository.findAllMenteesByMentorId(mentorId))
                    .thenReturn(mentees);

            UserDto menteeDto = new UserDto();
            menteeDto.setId(mentee.getId());
            menteeDto.setName(mentee.getUsername());
            menteeDto.setEmail(mentee.getEmail());
            when(userMapper.toDto(mentee)).thenReturn(menteeDto);

            List<UserDto> result = mentorshipService.getMentees(mentorId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(menteeDto, result.get(0));
        }

        @Test
        void getMentees_shouldReturnEmptyList_whenNoMenteesExist() {

            Long mentorId = 2L;
            when(mentorshipRepository.findAllMenteesByMentorId(mentorId))
                    .thenReturn(List.of());

            List<UserDto> result = mentorshipService.getMentees(mentorId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
