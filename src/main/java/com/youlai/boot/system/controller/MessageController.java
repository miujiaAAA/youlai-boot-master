package com.youlai.boot.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.system.service.MessageChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.util.Map;

/**
 * 消息控制层
 */
@Tag(name = "14.消息管理接口")
@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageChannelService messageService;
    private final ObjectMapper objectMapper;
    @Operation(summary = "获取对应的消息通道")
    @GetMapping("/{key}/getChannelInfo")
    public Result<?> getChannelInfo(@PathVariable String key){
        Map<String, String> channelInfo = messageService.getChannelInfo(key);
        Map<String, String> configMap = null;
        if (channelInfo.get("configinfo") != null && channelInfo.get("configinfo").length() > 0) {
            try {
                 configMap = objectMapper.readValue(channelInfo.get("configinfo"), Map.class);
            } catch (Exception e) {
                return Result.failed("Failed to parse JSON: " + e.getMessage());
            }
        }
        return Result.success(configMap);
    }
    @Operation(summary = "更新通道数据")
    @PostMapping("/{key}/saveChannelInfo")
    @Log(value = "更新通道数据", module = LogModuleEnum.SETTING)
    public Result<?> saveChannelInfo(@PathVariable String key, @RequestBody Map<String, String> channelInfo){
        messageService.saveChannelInfo(key, channelInfo);
        return Result.success();
    }
    @Operation(summary = "企业微信通知(POST)")
    @PostMapping("/wechat/send")
    @Log(value = "企业微信通知", module = LogModuleEnum.SETTING)
    public Result<?> sendWechatMessage(@RequestBody Map<String, String> params){
            messageService.sendWeComMessage(params.get("toUser"), params.get("content"));
        return Result.success();
    }
    @Operation(summary = "企业微信通知 (GET)")
    @GetMapping("/wechat/send")
    @Log(value = "企业微信通知", module = LogModuleEnum.SETTING)
    public Result<?> sendWechatMessageGet(@RequestParam String toUser, @RequestParam String msg) {
        messageService.sendWeComMessage(toUser, msg);
        return Result.success();
    }
}
