package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/22 19:31
 */
@Data
public class ApplyRecordQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billId;
    private String tenantName; // 车队名称
    private String mobilePhone; // 手机号码

    private String carUserTypes; // 邀请种类，多个类型逗号隔开
    private String states; // 状态,多个逗号隔开

    private Integer carUserType;
    private Integer applyState;

//    private String tenantLinkPhone;
}
