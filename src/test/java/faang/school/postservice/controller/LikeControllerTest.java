package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    private MockMvc mockMvc;
    @Mock
    private LikeService likeService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private LikeController controller;

    private final long userId = 1L;
    private final long postId = 2L;
    private final long commentId = 3L;
    private final long likeId = 4L;
    private LikeDto likeDto;

    @BeforeEach
    public void setUp() {
        likeDto = LikeDto.builder()
                .id(likeId)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void likePost() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(likeService.addLikeOnPost(userId, postId)).thenReturn(likeDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(likeDto);

        mockMvc.perform(post("/like/post/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(likeId));

        InOrder inOrder = inOrder(userContext, likeService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(likeService, times(1)).addLikeOnPost(userId, postId);
    }

    @Test
    void deleteLikeFromPost() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(likeDto);

        mockMvc.perform(delete("/like/post/" + postId + "/" + likeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(userContext, likeService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(likeService, times(1)).removeLikeFromPost(likeId, userId, postId);
    }

    @Test
    void likeComment() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(likeService.addLikeOnComment(userId, commentId)).thenReturn(likeDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(likeDto);

        mockMvc.perform(post("/like/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(likeId));

        InOrder inOrder = inOrder(userContext, likeService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(likeService, times(1)).addLikeOnComment(userId, commentId);
    }

    @Test
    void deleteLikeFromComment() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(likeDto);

        mockMvc.perform(delete("/like/comment/" + commentId + "/" + likeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(userContext, likeService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(likeService, times(1)).removeLikeFromComment(likeId, userId, commentId);
    }
}