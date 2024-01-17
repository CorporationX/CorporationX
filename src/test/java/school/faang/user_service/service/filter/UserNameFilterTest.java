package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserNameFilterTest {

    @Test
    void testIsApplicable() {
        UserNameFilter filter = new UserNameFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("R");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        UserNameFilter filter = new UserNameFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserNameFilter filter = new UserNameFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("R");

        User user1 = User.builder().username("Ruslan").build();
        User user2 = User.builder().username("Sergey").build();
        User user3 = User.builder().username("Roman").build();
        Mockito.when(user1.getUsername()).thenReturn("Ruslan");
        Mockito.when(user2.getUsername()).thenReturn("Sergey");
        Mockito.when(user3.getUsername()).thenReturn("Sergey");

        Stream<User> users = Stream.of(user1, user2, user3);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}