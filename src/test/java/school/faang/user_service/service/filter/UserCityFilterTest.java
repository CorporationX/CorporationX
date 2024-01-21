package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserCityFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCityFilterTest {

    private UserFilterDto dto;
    private UserCityFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserCityFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setCityPattern("Saint");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setCityPattern("Saint");

        List<User> createdUsers = List.of(
                User.builder().city("Saint-Petersburg").build(),
                User.builder().city("Saint-Etienne").build(),
                User.builder().city("London").build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}