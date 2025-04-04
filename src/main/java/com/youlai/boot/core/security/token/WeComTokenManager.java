package com.youlai.boot.core.security.token;

import com.fasterxml.jackson.databind.JsonNode;
import com.youlai.boot.common.constant.RedisConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class WeComTokenManager {
    private final RedisTemplate<String, Object> redisTemplate;

    private RestTemplate restTemplate  = new RestTemplate();

    public String getAccessToken() {
        String token = (String) redisTemplate.opsForValue().get(RedisConstants.WeCom.TOKEN_KEY);
        if (token == null) {
            return refreshToken();
        }
        return token;
    }
    private String refreshToken() {
        Map<String,Object> config = (Map<String, Object>) redisTemplate.opsForValue().get(RedisConstants.WeCom.CONFIG_INFO);
        String url = String.format("%s?corpid=%s&corpsecret=%s",config.get("tokenApi"), config.get("corpId"), config.get("corpSecret"));
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        assert response != null;
        if (response.get("errcode").intValue() == 0) {
            String newToken = response.get("access_token").asText();
            int expiresIn = response.get("expires_in").intValue();

            redisTemplate.opsForValue().set(
                    RedisConstants.WeCom.TOKEN_KEY,
                    newToken,
                    expiresIn - 300,  // 提前5分钟过期
                    TimeUnit.SECONDS
            );
            return newToken;
        }
        throw new RuntimeException("刷新Token失败: " + response);
    }
}
