package com.youming.youche.market.dto.facilitator;

import com.youming.youche.market.domain.facilitator.UserDataInfo;
import lombok.Data;

import java.util.List;
@Data
public class ServiceInfoDto extends UserDataInfo {

    /**
     * 服务商
     */
    private String serviceName;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 服务商类型名称
     */
    private String serviceTypeName;
    /**
     * 地址
     */
    private String companyAddress;
    /**
     * 是否开票（1.是、2.否）
     */
    private Integer isBill;
    /**
     * 账期
     */
    private Integer paymentDays;
    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;
    /**
     * 账期结算月份
     */
    private Integer paymentMonth;

    private String quotaAmtStr;

    /**
     * 0：未注册 1：已注册为有服务商信息 2：已注册存在服务商信息
     */
    private Integer info;

    private Integer isBillAbility;

    private Long serviceUserId;

    private List applyMap;

    /**
     * 登录账户
     */
    private String loginAcct;


}
