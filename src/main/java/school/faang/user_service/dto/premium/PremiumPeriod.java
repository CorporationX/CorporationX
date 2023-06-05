package school.faang.user_service.dto.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PremiumPeriod {
    MONTHLY(30, 10),
    QUARTERLY(90, 25),
    YEARLY(365, 80);

    private final int days;
    private final int price;

    public static PremiumPeriod fromDays(int days) {
        for (PremiumPeriod period : PremiumPeriod.values()) {
            if (period.days == days) {
                return period;
            }
        }
        throw new IllegalArgumentException("No PremiumPeriod for days: " + days);
    }
}
