package faang.school.servicetemplate.model;

import java.io.Serializable;

public record CalculationRequest(
        int a,
        int b,
        CalculationType calculationType
) implements Serializable {
    static final long serialVersionUID = -1231231023L;
}
