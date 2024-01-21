package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserCountryFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCountryFilterTest {

    private UserFilterDto dto;
    private UserCountryFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserCountryFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setCountryPattern("R");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setCountryPattern("R");

        List<User> createdUsers = List.of(
                User.builder().country(new Country(1L, "Russia", null)).build(),
                User.builder().country(new Country(2L, "Romania", null)).build(),
                User.builder().country(new Country(3L, "Spain", null)).build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}
