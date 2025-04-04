package com.youlai.boot.system.service;

import java.util.Map;

/**
 * 消息配置接口
 */
public interface MessageChannelService {

    /**
     * 刷新微信配置缓存
     */
    void refreshWeComCache();
    /**
     * 获取消息通道
     */
    Map<String,String> getChannelInfo(String key);

    /**
     * 更新通道数据
     */
    void saveChannelInfo(String key, Map<String, String> channelInfo);

    /**
     * 推送企业微信消息
     */
    void sendWeComMessage(String toUser, String content);

}
