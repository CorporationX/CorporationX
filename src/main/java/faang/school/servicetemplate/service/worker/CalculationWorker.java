package faang.school.servicetemplate.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import faang.school.servicetemplate.model.CalculationRequest;
import faang.school.servicetemplate.service.CalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationWorker {
    private final CalculatorService calculatorService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public CalculationWorker(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    public void processCalculation(CalculationRequest request) {
        doProcess(request);
    }

    public void processCalculationAsync(CalculationRequest request) {
        var future = executorService.submit(() -> doProcess(request));
        scheduler.schedule(
                () -> {
                    if (!future.isDone()) {
                        future.cancel(true);
                    }

                }, 5, TimeUnit.SECONDS
        );
    }

    private void doProcess(CalculationRequest request) {
        log.info("Got a request to process calculation request: " + request);
        var result = calculatorService.calculate(request);
        var logMessage = String.format("Calculation request is processed: %d %s %d = %d",
                request.a(), request.calculationType(), request.b(), result);
        log.info(logMessage);
    }
}
