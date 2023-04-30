package faang.school.servicetemplate.service.cache;

import java.time.Duration;

import faang.school.servicetemplate.model.CalculationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CalculationTtlCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CalculationTtlCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cache(CalculationRequest request, int result) {
        redisTemplate.opsForValue().set(requestKey(request), String.valueOf(result), Duration.ofMinutes(5));
    }

    public Integer get(CalculationRequest request) {
        var valueRaw = redisTemplate.opsForValue().get(requestKey(request));
        if (valueRaw == null) {
            return null;
        }
        return Integer.parseInt((String) valueRaw);
    }

    private String requestKey(CalculationRequest request) {
        return request.a() + request.calculationType().name() + request.b();
    }
}
