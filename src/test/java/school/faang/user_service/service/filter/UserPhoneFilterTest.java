package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserPhoneFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserPhoneFilterTest {

    private UserFilterDto dto;
    private UserPhoneFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserPhoneFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setPhonePattern("+7");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setPhonePattern("+7");

        List<User> createdUsers = List.of(
                User.builder().phone("+79992002020").build(),
                User.builder().phone("+342398423").build(),
                User.builder().phone("+79119111111").build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}
