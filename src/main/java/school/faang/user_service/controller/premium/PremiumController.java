package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping("/premium")
    public PremiumDto buyPremium(@RequestParam(name = "days") int days) {
        PremiumPeriod period = PremiumPeriod.fromDays(days);
        return premiumService.buyPremium(userContext.getUserId(), period);
    }
}
