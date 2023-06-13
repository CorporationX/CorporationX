package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final UserService userService;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod period) {
        if (!premiumRepository.existsByUserId(userId)) {
            PaymentRequest request = new PaymentRequest(
                    (long) (Math.random() * 1000),
                    new BigDecimal(period.getPrice()),
                    Currency.USD
            );
            PaymentResponse response = paymentServiceClient.sendPayment(request).getBody();
            if (response != null && response.status().equals(PaymentStatus.SUCCESS)) {
                User user = userService.findUser(userId);
                Premium premium = Premium.builder()
                        .user(user)
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusDays(period.getDays()))
                        .build();
                return premiumMapper.toDto(premiumRepository.save(premium));
            }
            throw new RuntimeException("Payment failed");
        }
        throw new DataValidationException("User with id " + userId + " already has premium");
    }
}
