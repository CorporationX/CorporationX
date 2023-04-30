package faang.school.servicetemplate.controller;

import faang.school.servicetemplate.dto.CalculationResult;
import faang.school.servicetemplate.dto.Error;
import faang.school.servicetemplate.model.Calculation;
import faang.school.servicetemplate.model.CalculationJpa;
import faang.school.servicetemplate.model.CalculationType;
import faang.school.servicetemplate.repository.CalculationJdbcRepository;
import faang.school.servicetemplate.repository.CalculationJpaRepository;
import faang.school.servicetemplate.util.BaseContextTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


class CalculatorControllerTest extends BaseContextTest {

    @Autowired
    private CalculationJdbcRepository calculationJdbcRepository;

    @Autowired
    private CalculationJpaRepository calculationJpaRepository;

    @BeforeEach
    void tearUp() {
        calculationJdbcRepository.deleteAll();
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 100, 1, 0})
    void shouldReturn_InternalServerError_OnZeroDivision(int a) throws Exception {
        var response = performCalculateAll(a, 0, 500);
        var error = objectMapper.readValue(response, Error.class);

        Assertions.assertThat(error.code())
                .isEqualTo("ZERO_DIVISION");

        Assertions.assertThat(error.message())
                .isEqualTo("Trying to divide " + a + " by zero!");

        var inDbJdbc = calculationJdbcRepository.getAll();
        Assertions.assertThat(inDbJdbc).hasSize(3);
        Assertions.assertThat(inDbJdbc.stream().map(Calculation::type))
                .containsExactlyInAnyOrder(CalculationType.ADD, CalculationType.SUB, CalculationType.MUL);

        var inDbJpa = calculationJpaRepository.findAll();
        Assertions.assertThat(inDbJpa).hasSize(3);
        Assertions.assertThat(inDbJpa.stream().map(CalculationJpa::getCalculationType))
                .containsExactlyInAnyOrder(CalculationType.ADD, CalculationType.SUB, CalculationType.MUL);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "10, 2, 12, 8, 20, 5,",
                    "100, 1, 101, 99, 100, 100",
                    "2, 2, 4, 0, 4, 1"
            }
    )
    void shouldReturn_AllResults(
            int a,
            int b,
            int expectedAdd,
            int expectedSub,
            int expectedMul,
            int expectedDiv
    ) throws Exception {
        var response = performCalculateAll(a, b, 200);
        var result = objectMapper.readValue(response, CalculationResult.class);

        Assertions.assertThat(result.add()).isEqualTo(expectedAdd);
        Assertions.assertThat(result.sub()).isEqualTo(expectedSub);
        Assertions.assertThat(result.mul()).isEqualTo(expectedMul);
        Assertions.assertThat(result.div()).isEqualTo(expectedDiv);

        var inDbJdbc = calculationJdbcRepository.getAll();
        Assertions.assertThat(inDbJdbc).hasSize(4);
        Assertions.assertThat(inDbJdbc.stream().map(Calculation::type))
                .containsExactlyInAnyOrder(CalculationType.values());

        var inDbJpa = calculationJpaRepository.findAll();
        Assertions.assertThat(inDbJpa).hasSize(4);
        Assertions.assertThat(inDbJpa.stream().map(CalculationJpa::getCalculationType))
                .containsExactlyInAnyOrder(CalculationType.values());
    }

    private String performCalculateAll(int a, int b, int expectedStatus) throws Exception {
        return mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/calculate/all")
                        .param("a", String.valueOf(a))
                        .param("b", String.valueOf(b))
                )
                .andExpect(MockMvcResultMatchers.status().is(expectedStatus))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
