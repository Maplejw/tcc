package com.igg.boot.framework.redis;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix="igg.redis.dynamic")
@Data
public class RedisDynamicProperties {
	private Map<String, RedisProperties> redis;
	private boolean enabled = false;
	
}
