package faang.school.servicetemplate.controller;

import java.util.List;
import java.util.stream.Stream;

import faang.school.servicetemplate.dto.CalculationResult;
import faang.school.servicetemplate.model.CalculationRequest;
import faang.school.servicetemplate.model.CalculationType;
import faang.school.servicetemplate.service.CalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Slf4j
public class CalculatorController {
    private final CalculatorService calculatorService;

    @Autowired
    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/calculate/all")
    public ResponseEntity<CalculationResult> calculateAll(@RequestParam int a, @RequestParam int b) {
        log.info("Got a request to calculate all values for a={} and b={}", a, b);

        List<Integer> results =
                Stream.of(CalculationType.ADD, CalculationType.SUB, CalculationType.MUL, CalculationType.DIV)
                        .parallel()
                        .map(it -> new CalculationRequest(a, b, it))
                        .map(calculatorService::calculate)
                        .toList();

        return ResponseEntity.ok(new CalculationResult(results.get(0), results.get(1), results.get(2), results.get(3)));
    }
}
