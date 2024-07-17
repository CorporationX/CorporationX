package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    private static final long GOAL_ID = 1L;

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;
    @InjectMocks
    private GoalServiceImpl goalService;
    private Goal goal;
    private GoalDto goalDto;

    @BeforeEach
    void setUp() {
        goal = new Goal();
        goalDto = new GoalDto();
        goal.setId(GOAL_ID);
        goalDto.setId(GOAL_ID);
    }

    @Test
    public void whenDeleteByIdAndIdNotExistThenThrowException() {
        when(goalRepository.existsById(GOAL_ID)).thenReturn(false);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> goalService.deleteById(GOAL_ID));
    }

    @Test
    public void whenDeleteByIdSuccessfully() {
        when(goalRepository.existsById(GOAL_ID)).thenReturn(true);
        goalService.deleteById(GOAL_ID);
        verify(goalRepository).deleteById(GOAL_ID);
    }

    @Test
    public void whenFindGoalsByUserIdThenReturnGoalDtos() {
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(Stream.of(goal));
        when(goalMapper.toDtoList(List.of(goal))).thenReturn(List.of(goalDto));
        List<GoalDto> actual = goalService.findGoalsByUserId(3L);
        assertThat(actual).isEqualTo(List.of(goalDto));
    }
}