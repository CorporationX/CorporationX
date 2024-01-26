package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.filter.user.UserContactFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserContactFilterTest {

    private UserFilterDto dto;
    private UserContactFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserContactFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setContactPattern("@a");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setContactPattern("VK");

        List<User> createdUsers = List.of(
                User.builder().contacts(List.of(
                        new Contact(1L, new User(), "@artem23", ContactType.VK))).build(),
                User.builder().contacts(List.of(
                        new Contact(11L, new User(), "@aaakss2", ContactType.TELEGRAM))).build(),
                User.builder().contacts(List.of(
                        new Contact(12L, new User(), "@bob1997", ContactType.VK))).build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}