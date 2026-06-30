package sn.rangmoygaaw.transport.infrastructure.persistence.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.port.out.GpsCacheRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisGpsAdapter implements GpsCacheRepository {

    private static final Logger log = LoggerFactory.getLogger(RedisGpsAdapter.class);
    private static final String PREFIX = "bus:position:";

    private final RedisTemplate<String, String> redisTemplate;
    private final Duration ttl;    
   

    public RedisGpsAdapter(
            RedisTemplate<String, String> redisTemplate,
            @Value("${transport.gps.cache-ttl-seconds:300}") long cacheTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofSeconds(Math.max(1, cacheTtlSeconds));
    }



    @Override
    public void savePosition(UUID busId, PositionGPS position) {
        String key   = "bus:position:" + busId;
        String value = position.getLatitude() + ","
                     + position.getLongitude() + ","
                     + position.getTimestamp();
        
        System.out.println("Redis SAVE: " + key + " = " + value); // ← ajoute
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Optional<PositionGPS> getPosition(UUID busId) {
        String value = redisTemplate.opsForValue().get(PREFIX + busId);
        if (value == null) return Optional.empty();

        String[] parts = value.split(",");
        if (parts.length != 3) {
            log.warn("Format GPS invalide pour bus {}: {}", busId, value);
            return Optional.empty();
        }

        try {
            return Optional.of(new PositionGPS(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Long.parseLong(parts[2])
            ));
        } catch (RuntimeException ex) {
            log.warn("Impossible de parser la position GPS pour bus {}: {}", busId, value, ex);
            return Optional.empty();
        }
    }

    @Override
    public void removePosition(UUID busId) {
        redisTemplate.delete(PREFIX + busId);
    }
}