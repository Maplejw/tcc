package com.igg.boot.framework.redis;


import lombok.Data;

import java.util.Map;

@Data
public class DynamicJedisTemplate {
    private Map<String,JedisTemplate> jedisClusterTemplateMap;
}
