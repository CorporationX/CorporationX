package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.SkillService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class SkillControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SkillService skillService;

    @Test
    void createSkillTest() throws Exception {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("SQL");
        SkillDto result = new SkillDto();
        result.setId(1L);
        result.setTitle("SQL");
        when(skillService.create(skillDto)).thenReturn(result);

        mvc.perform( MockMvcRequestBuilders
                        .post("/skill")
                        .content(asJsonString(skillDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("SQL"));
    }

    @Test
    void createSkill_EmptyTitleDataValidationExceptionTest() throws Exception {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");

        mvc.perform( MockMvcRequestBuilders
                        .post("/skill")
                        .content(asJsonString(skillDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ErrorMessage.INVALID_SKILL_PROVIDED));
    }

    @Test
    void createSkill_NullTitleDataValidationExceptionTest() throws Exception {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(null);
        skillDto.setTitle(null);

        mvc.perform( MockMvcRequestBuilders
                        .post("/skill")
                        .content(asJsonString(skillDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ErrorMessage.INVALID_SKILL_PROVIDED));
    }

    @Test
    void getUserSkills() {
    }

    @Test
    void getOfferedSkills() {
    }

    @Test
    void acquireSkillFromOffers() {
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}