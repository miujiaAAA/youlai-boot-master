package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息渠道实体类
 */
@TableName("sys_msgchannel")
@Getter
@Setter
public class MessageChannel extends BaseEntity {

    /**
     * 系统ID
     */
    private String sysid;

    /**
     * 代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 配置信息
     */
    private String configinfo;

    /**
     * 租户ID
     */
    private String tenantid;

    /**
     * 插件ID
     */
    private String pluginid;

    /**
     * 类型
     */
    private String type;

    /**
     * 名称2
     */
    private String name2;

    /**
     * 名称3
     */
    private String name3;

    /**
     * 名称4
     */
    private String name4;

    /**
     * 名称5
     */
    private String name5;

    /**
     * 名称6
     */
    private String name6;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 更新时间
     */
    private String update_time;

    /**
     * 创建人
     */
    private String create_by;

    /**
     * 更新人
     */
    private String update_by;
}
