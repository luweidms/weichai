package com.youming.youche.cloud.dto.sys;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysSmsSendDto implements Serializable {

    private static final long serialVersionUID = -1717242693791885610L;

    private String smsId; // 消息记录编号
    private String smsContent; // 消息内容
    private String smsType; // 消息类型
    private String sendFlag; // 短信状态	 	0失败（未读） 、1成功（未读）、2已读、3删除（失效）
    private String sendFlagName; // 短信状态描述
    private String sendDate; //  发送时间	 	格式：2015-05-06
    private String billId;// 手机号
    private String objType; // 对象类型
    private String objTypeName; // 对象类型名称
    private String objId; // 业务编号
    private String channelType; // 渠道类型

}
