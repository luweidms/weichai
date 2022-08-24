package com.youming.youche.cloud.vo.sys;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageFlagDto implements Serializable {
    private static final long serialVersionUID = 2476629986847043786L;

    private Integer smsId; // 消息记录编号
    private String sendDate; // 发送时间	 格式：2016-03-31
    private Integer objectType; // 业务类型 :1：修改消息为已读状态2：删除短信

}
