package com.youming.youche.cloud.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SystemNotifyDto implements Serializable {

    private static final long serialVersionUID = 2128610219869678131L;
    /**
     * 标题
     */
    private String title;
    /**
     * 短信内容
     */
    private String smsContent;
    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    private String info = "Y";
    /**
     * 车队名称
     */
    private String smsTenantName;

}
