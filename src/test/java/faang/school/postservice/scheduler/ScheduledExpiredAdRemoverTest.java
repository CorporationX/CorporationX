package faang.school.postservice.scheduler;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.ad.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @BeforeEach
    public void init() {
        int maxListSize = 1;
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "maxListSize", maxListSize);
    }

    @Test
    void deleteExpiredAdsScheduled() {

        when(adRepository.findAllExpiredIds()).thenReturn(List.of(1L, 2L, 3L));

        scheduledExpiredAdRemover.deleteExpiredAdsScheduled();

        InOrder inOrder = inOrder(adRepository, adService);
        inOrder.verify(adRepository, times(1)).findAllExpiredIds();
        inOrder.verify(adService, times(3)).deleteAds(anyList());
    }

    @Test
    void deleteExpiredAdsScheduledNoExpiredAds() {

        when(adRepository.findAllExpiredIds()).thenReturn(Collections.emptyList());

        scheduledExpiredAdRemover.deleteExpiredAdsScheduled();

        InOrder inOrder = inOrder(adRepository, adService);
        inOrder.verify(adRepository, times(1)).findAllExpiredIds();
    }
}