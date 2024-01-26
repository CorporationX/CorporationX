package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserAboutFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAboutFilterTest {

    private UserFilterDto dto;
    private UserAboutFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserAboutFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setAboutPattern("Engineer");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setAboutPattern("Engineer");

        List<User> createdUsers = List.of(
                User.builder().aboutMe("IT Engineer").build(),
                User.builder().aboutMe("Doctor").build(),
                User.builder().aboutMe("Petroleum Engineer").build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}
