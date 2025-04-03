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
    private final RestTemplate restTemplate = new RestTemplate();
    String corpId = "ww1b363bc3d08980ca";
    String agentSecret = "D2_01oTAMwCkcctG__IjRhJK0L99Zz8n29afC6HGuLI";
    String toUser = "@all";
    Integer agentId = 1000002;
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
    @Operation(summary = "企业微信通知")
    @PostMapping("/wechat/send")
    @Log(value = "企业微信通知", module = LogModuleEnum.SETTING)
    public Result<?> sendWechatMessage(@RequestBody Map<String, String> params){
        String content = params.get("content");
        String token = getToken();
        if (token == null)
            return Result.failed();

        String url = "http://47.108.227.234:8088/cgi-bin/message/send?access_token=" + token;
        String json = String.format(
                "{\"touser\":\"%s\",\"agentid\":%d,\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}",
                toUser, agentId, content.replace("\"", "\\\"")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            String result = restTemplate.postForObject(url, entity, String.class);
            Result.failed("\"errcode\":0");
        } catch (Exception e) {
            System.err.println("发送失败：" + e.getMessage());
            Result.failed("发送失败：" + e.getMessage());
        }
        return Result.success();
    }
    private String getToken() {


        String url = "http://47.108.227.234:8088/cgi-bin/gettoken?corpid=" + corpId + "&corpsecret=" + agentSecret;
        try {
            String result = restTemplate.getForObject(url, String.class);
            return result.contains("\"access_token\"")
                    ? result.split("\"access_token\":\"")[1].split("\",\"")[0]
                    : null;
        } catch (Exception e) {
            System.err.println("获取Token失败：" + e.getMessage());
            return null;
        }
    }
}
