package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.core.security.token.WeComTokenManager;
import com.youlai.boot.system.mapper.MessageChannelMapper;
import com.youlai.boot.system.model.entity.MessageChannel;
import com.youlai.boot.system.service.MessageChannelService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageChannelServiceImpl extends ServiceImpl<MessageChannelMapper, MessageChannel> implements MessageChannelService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final WeComTokenManager tokenManager;
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 初始化权限缓存
     */
    @PostConstruct
    public void initWeComConfigCache() {
        log.info("初始化企业微信配置缓存... ");
        refreshWeComCache();
    }

    /**
     * 刷新企业微信配置缓存
     */
    @Override
    public void refreshWeComCache() {
        LambdaQueryWrapper<MessageChannel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MessageChannel::getType, "wecom").select(MessageChannel::getConfiginfo);
        MessageChannel messageChannel = getOne(lambdaQueryWrapper);

        if (messageChannel == null) {
            throw new IllegalStateException("未找到企业微信配置");
        } else {
            try {
                String configInfoJson = messageChannel.getConfiginfo();
                Map<String, String> configInfoMap = objectMapper.readValue(configInfoJson, new TypeReference<Map<String, String>>() {});
                redisTemplate.opsForValue().set(RedisConstants.WeCom.CONFIG_INFO, configInfoMap);
            } catch (Exception e) {
                log.error("配置文件的解析异常: {}", e.getMessage(), e);
                throw new RuntimeException("配置文件的解析异常", e);
            }
        }
    }

    @Override
    public Map<String, String> getChannelInfo(String key) {
        LambdaQueryWrapper<MessageChannel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MessageChannel::getCode, key).select(MessageChannel::getConfiginfo);
        MessageChannel messageChannel = getOne(lambdaQueryWrapper);
        if (messageChannel != null) {
            return Map.of("configinfo", messageChannel.getConfiginfo());
        } else {
            return Map.of();
        }
    }

    @Override
    public void saveChannelInfo(String key, Map<String, String> channelInfo) {
        if (key == null || channelInfo == null) {
            throw new IllegalArgumentException("Key and channelInfo cannot be null");
        }
        try {
            String configInfoJson = objectMapper.writeValueAsString(channelInfo);
            LambdaUpdateWrapper<MessageChannel> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            this.update(lambdaUpdateWrapper.eq(MessageChannel::getCode, key).set(MessageChannel::getConfiginfo, configInfoJson));
        } catch (Exception e) {
        // 捕获异常并记录错误日志
        log.error("Error occurred while saving channel info for key: {}",e);
        throw new RuntimeException("Failed to save channel info", e);
    }
        }

    @Override
    public void sendWeComMessage(String toUser, String content) {
        // 获取企业微信配置信息
        Map<String, String> configInfo = getConfigInfo();
        if (configInfo == null) {
            throw new IllegalStateException("企业微信配置信息未找到");
        }
        if (!configInfo.containsKey("agentId")) {
            throw new IllegalStateException("企业微信配置信息中缺少 agentid");
        }

        // 构造请求 URL 和请求体
        String url = configInfo.get("sendApi") + "?access_token=" + getAccessToken();
        String agentId = configInfo.get("agentId");
        Map<String, Object> requestBody = createRequestBody(toUser, agentId, content);

        // 发送 HTTP 请求
        try {
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), createHeaders());
            restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            // 捕获并记录异常
            System.err.println("发送企业微信消息失败: " + e.getMessage());
            throw new RuntimeException("发送企业微信消息失败", e);
        }
    }
    private Map<String, String> getConfigInfo() {
        Object configObj = redisTemplate.opsForValue().get(RedisConstants.WeCom.CONFIG_INFO);
        if (!(configObj instanceof Map)) {
            return null;
        }
        return (Map<String, String>) configObj;
    }


    private String getAccessToken() {
        // 缓存访问令牌，避免频繁调用 API
        return tokenManager.getAccessToken();
    }
    private Map<String, Object> createRequestBody(String toUser, String agentId, String content) {
        return Map.of(
                "touser", toUser,
                "msgtype", "text",
                "agentid", agentId,
                "text", Map.of("content", content)
        );
    }
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
