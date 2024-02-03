package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserNameFilterTest {

    private UserFilterDto dto;
    private UserNameFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserNameFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setNamePattern("R");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setNamePattern("R");

        List<User> createdUsers = List.of(
                User.builder().username("Ruslan").build(),
                User.builder().username("Oleg").build(),
                User.builder().username("Roman").build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}