package faang.school.servicetemplate.service;

import faang.school.servicetemplate.exception.ZeroDivisionException;
import faang.school.servicetemplate.model.CalculationJpa;
import faang.school.servicetemplate.model.CalculationRequest;
import faang.school.servicetemplate.model.CalculationType;
import faang.school.servicetemplate.repository.CalculationJdbcRepository;
import faang.school.servicetemplate.repository.CalculationJpaRepository;
import faang.school.servicetemplate.service.cache.CalculationTtlCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    private final CalculationJdbcRepository calculationJdbcRepository;

    private final CalculationJpaRepository calculationJpaRepository;

    private final CalculationTtlCacheService cacheService;

    @Autowired
    public CalculatorService(CalculationJdbcRepository calculationJdbcRepository,
                             CalculationJpaRepository calculationJpaRepository,
                             CalculationTtlCacheService cacheService) {
        this.calculationJdbcRepository = calculationJdbcRepository;
        this.calculationJpaRepository = calculationJpaRepository;
        this.cacheService = cacheService;
    }

    public int calculate(CalculationRequest request) {
        int a = request.a(), b = request.b();
        CalculationType type = request.calculationType();

        // check if presents in cache, and return if it does
        Integer cacheResult = cacheService.get(request);
        if (cacheResult != null) {
            return cacheResult;
        }

        int result;
        if (type == CalculationType.ADD) {
            result = add(a, b);
        } else if (type == CalculationType.SUB) {
            result = sub(a, b);
        } else if (type == CalculationType.MUL) {
            result = mul(a, b);
        } else if (type == CalculationType.DIV) {
            result = div(a, b);
        } else {
            var message = String.format("CalculationType %s is not implemented", type.name());
            throw new RuntimeException(message);
        }
        cacheService.cache(request, result);

        // Просто для демонстрации разных инструментов работы с БД: jdbcTemplate и JPA
        if (a > b) {
            calculationJdbcRepository.saveCalculation(a, b, type, result);
        } else {
            calculationJpaRepository.save(new CalculationJpa(a, b, type, result));
        }

        return result;
    }

    private int add(int a, int b) {
        bigCalculationImitation();
        return a + b;
    }

    private int sub(int a, int b) {
        bigCalculationImitation();
        return a - b;
    }

    private int mul(int a, int b) {
        bigCalculationImitation();
        return a * b;
    }

    private int div(int a, int b) {
        if (b == 0) {
            throw new ZeroDivisionException(a);
        }

        bigCalculationImitation();
        return a / b;
    }

    private void bigCalculationImitation() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
