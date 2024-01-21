package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserSkillFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserSkillFilterTest {

    private UserFilterDto dto;
    private UserSkillFilter filter;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
        filter = new UserSkillFilter();
    }

    @Test
    void testIsApplicable() {
        dto.setSkillPattern("SQL");
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        dto.setSkillPattern("SQL");

        List<User> createdUsers = List.of(
                User.builder().skills(List.of(
                        new Skill(1L, "SQL", null, null, null, null, null, null))).build(),
                User.builder().skills(List.of(
                        new Skill(12L, "Kafka", null, null, null, null, null, null))).build(),
                User.builder().skills(List.of(
                        new Skill(1432L, "RabbitMQ", null, null, null, null, null, null))).build()
        );

        Stream<User> users = createdUsers.stream();
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }
}
