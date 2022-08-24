package com.youming.youche.cloud.dto.sys;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnReadCountDto implements Serializable {

    private static final long serialVersionUID = 9074366123516598956L;

    private String unReadCount; // 消息未读数量
    private String unReadCountOrderAssistant; // 订单助手未读数量
    private String unReadCountInviteInfo; // 邀请信息未读数量
    private String unReadCountProgress; // 进度通知未读数量
    private String unReadCountSystemNotify; // 系统提醒未读数量

}
