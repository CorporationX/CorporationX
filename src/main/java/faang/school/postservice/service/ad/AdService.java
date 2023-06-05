package faang.school.postservice.service.ad;

import faang.school.postservice.client.PaymentServiceClient;
import faang.school.postservice.dto.ad.AdDto;
import faang.school.postservice.dto.payment.Currency;
import faang.school.postservice.dto.payment.PaymentRequest;
import faang.school.postservice.dto.payment.PaymentResponse;
import faang.school.postservice.dto.payment.PaymentStatus;
import faang.school.postservice.mapper.ad.AdMapper;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdService {

    private static final int BATCH_SIZE = 100;

    private final AdRepository adRepository;
    private final PostService postService;
    private final PaymentServiceClient paymentServiceClient;
    private final AdMapper adMapper;
    private final ThreadPoolExecutor adRemoverThreadPool;

    @Transactional
    public AdDto buyAd(long userId, long postId, int days, int appearances) {
        PaymentRequest request = new PaymentRequest(
                (long) (Math.random() * 1000000),
                new BigDecimal(10),
                Currency.USD
        );
        PaymentResponse response = paymentServiceClient.sendPayment(request).getBody();
        if (response != null && response.status().equals(PaymentStatus.SUCCESS)) {
            return createOrUpdate(userId, postId, days, appearances);
        }
        throw new RuntimeException("Payment failed");
    }

    @Transactional(readOnly = true)
    public List<AdDto> getRemainingAds(long userId) {
        return adRepository.findAllByBuyerId(userId).stream()
                .map(adMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeExpiredAds() {
        List<Ad> ads = StreamSupport.stream(adRepository.findAll().spliterator(), false)
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0)
                .toList();
        for (int i = 0; i < ads.size(); i += BATCH_SIZE) {
            List<Ad> batch = ads.subList(i, Math.min(i + BATCH_SIZE, ads.size()));
            CompletableFuture.runAsync(() -> adRepository.deleteAll(batch), adRemoverThreadPool);
        }
    }

    private AdDto createOrUpdate(long userId, long postId, int days, int appearances) {
        Ad advert = adRepository.findByPostId(postId)
                .map(ad -> update(ad, days, appearances))
                .orElseGet(() -> create(userId, postId, days, appearances));
        return adMapper.toDto(advert);
    }

    private Ad create(long userId, long postId, int days, int appearances) {
        var post = postService.findById(postId);
        var ad = Ad.builder()
                .post(post)
                .buyerId(userId)
                .appearancesLeft(appearances)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(days))
                .build();
        return adRepository.save(ad);
    }

    private Ad update(Ad ad, int days, int appearances) {
        ad.setAppearancesLeft(ad.getAppearancesLeft() + appearances);
        ad.setEndDate(ad.getEndDate().plusDays(days));
        return adRepository.save(ad);
    }
}
