package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserExperienceFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserExperienceFilterTest {

    private UserFilterDto dto;
    private UserExperienceFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserExperienceFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setExperienceMax(10);
        dto.setExperienceMin(5);
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setExperienceMax(10);
        dto.setExperienceMin(5);

        List<User> createdUsers = List.of(
                User.builder().experience(7).build(),
                User.builder().experience(17).build(),
                User.builder().experience(2).build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}
