package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.youlai.boot.system.mapper.MessageChannelMapper;
import com.youlai.boot.system.model.entity.MessageChannel;
import com.youlai.boot.system.service.MessageChannelService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class MessageChannelServiceImpl extends ServiceImpl<MessageChannelMapper, MessageChannel> implements MessageChannelService {
    private final Gson gson;

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
            String configInfoJson = gson.toJson(channelInfo);
            LambdaUpdateWrapper<MessageChannel> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            this.update(lambdaUpdateWrapper.eq(MessageChannel::getCode, key).set(MessageChannel::getConfiginfo, configInfoJson));
        } catch (Exception e) {
        // 捕获异常并记录错误日志
        log.error("Error occurred while saving channel info for key: {}",e);
        throw new RuntimeException("Failed to save channel info", e);
    }


        }

}
