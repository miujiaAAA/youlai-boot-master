package com.youlai.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wecom")
public class WeComConfig {
    private String corpId;
    private String agentSecret;
    private Integer agentId;

    // getters and setters
    public String getCorpId() { return corpId; }
    public void setCorpId(String corpId) { this.corpId = corpId; }
    public String getAgentSecret() { return agentSecret; }
    public void setAgentSecret(String agentSecret) { this.agentSecret = agentSecret; }
    public Integer getAgentId() { return agentId; }
    public void setAgentId(Integer agentId) { this.agentId = agentId; }
}
