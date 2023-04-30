package faang.school.servicetemplate.repository;

import java.util.List;

import faang.school.servicetemplate.model.Calculation;
import faang.school.servicetemplate.model.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CalculationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final RowMapper<Calculation> rowMapper;

    static {
        rowMapper = (rs, rowNum) -> new Calculation(
                rs.getInt("a"),
                rs.getInt("b"),
                CalculationType.valueOf(rs.getString("calculation_type")),
                rs.getInt("result")
        );
    }

    @Autowired
    public CalculationJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveCalculation(int a, int b, CalculationType type, int result) {
        String sql = """
                insert into calculation(a, b, calculation_type, result, id)
                    values(:a, :b, :calculation_type, :result, nextval('calculation_id_seq'));
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("a", a)
                .addValue("b", b)
                .addValue("calculation_type", type.name())
                .addValue("result", result);

        jdbcTemplate.update(sql, params);
    }

    public List<Calculation> getAll() {
        String sql = """
                select * from calculation;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, rowMapper);
    }

    public void deleteAll() {
        String sql = """
                truncate table calculation;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();

        jdbcTemplate.update(sql, params);
    }
}
