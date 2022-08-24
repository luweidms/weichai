package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/19
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OilCardRechargeVo implements Serializable {
    private String startTime; //发起开始
    private String endTime; //发起结束
    private String phone; //司机手机
    private String name; //司机姓名
    private String companyName; //服务商名称
    private String oilCardNum; //油卡卡号
    private Integer verificationState; //油卡状态
    private String verifyBeginTime; //核销开始
    private String verifyEndTime; //核销结束
    private Integer pageNum; //页码
    private Integer pageSize; //每页显示条数
    private Long tenantId; //租户ID

}
