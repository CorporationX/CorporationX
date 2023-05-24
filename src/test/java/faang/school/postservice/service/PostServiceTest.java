package faang.school.postservice.service;

import java.time.LocalDateTime;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.util.BaseContextTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;


class PostServiceTest extends BaseContextTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserContext userContext;
    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    void test() {
        var userId = 2L;
        userContext.setUserId(userId);

        Mockito.when(userServiceClient.getUser(userId))
                .thenReturn(new UserDto(userId, "", ""));

        var dto = new PostDto(
                1L,
                "content",
                userId,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        postService.createPost(dto);

        var found = postMapper.toDto(postService.findById(1L));

        Assertions.assertThat(found.getContent()).isEqualTo(dto.getContent());
    }
}
