package com.youming.youche.system.domain.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 车队经营状况
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
@Data
@Accessors(chain = true)
public class SysTenantBusinessState extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 访客档案ID
     */
    private Long visitId;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 车队类型：1、车队；2、三方；3、混合
     */
    private Integer tenantType;

    /**
     * 年度营业额
     */
    private Double annualTurnover;

    /**
     * 客户类型
     */
    private String customerType;

    /**
     * 其他客户类型
     */
    private String otherCustomerType;

    /**
     * 车辆型号
     */
    private String vehicleType;

    /**
     * 自有车数量
     */
    private Integer ownVehicleNumber;

    /**
     * 招商车数量
     */
    private Integer businessVehicleNumber;

    /**
     * 挂靠车数量
     */
    private Integer attachedVehicleNumber;

    /**
     * 外调车用量（辆/天）
     */
    private Integer otherVehicleUser;

    /**
     * 员工数
     */
    private Integer staffNumber;

    /**
     * 车管人数
     */
    private Integer vehicleManageNumber;

    /**
     * 调度员人数
     */
    private Integer dispatcherNumber;

    /**
     * 客户/开单员人数
     */
    private Integer customerServiceNumber;

    /**
     * 财务人数
     */
    private Integer financialNumber;

    /**
     * 系统负责人姓名
     */
    private String systemUser;

    /**
     * 系统负责人电话
     */
    private String systemPhone;

    /**
     * 基础资料负责人姓名
     */
    private String infoUser;

    /**
     * 基础资料负责人电话
     */
    private String infoPhone;

    /**
     * 开单员姓名
     */
    private String openOrderUser;

    /**
     * 开单员电话
     */
    private String openOrderPhone;

    /**
     * 财务姓名
     */
    private String financialUser;

    /**
     * 财务电话
     */
    private String financialPhone;

    /**
     * 后服姓名
     */
    private String serviceUser;

    /**
     * 后服电话
     */
    private String servicePhone;

    /**
     * 干线整车占比
     */
    private Double mainVehiclePercent;

    /**
     * 经停占比
     */
    private Double stopPercent;

    /**
     * 城配占比
     */
    private Double distributPercent;

    /**
     * 零担占比
     */
    private Double retailPercent;

    /**
     * 中石油石化卡占比
     */
    private Double petroChinaPercent;

    /**
     * 三方油卡占比
     */
    private Double threePartyPercent;

    /**
     * 自购油占比
     */
    private Double purchasePercent;

    /**
     * 外部民营油占比
     */
    private Double externalPercent;

    /**
     * 后付费占比
     */
    private Double afterpayPercent;

    /**
     * 预付费占比
     */
    private Double prepayPercent;

    /**
     * 现金支付占比
     */
    private Double cashPercent;

    /**
     * 发卡方
     */
    private String issuers;

    /**
     * 其他发卡方
     */
    private String otherIssuers;

    /**
     * 自有车运费核算
     */
    private String ownFreightType;

    /**
     * 外调车运费核算
     */
    private String otherFreightType;

    /**
     * 系统使用经验
     */
    private String systemUsed;

    /**
     * 其他车载硬件
     */
    private String otherHardwareUsed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 其他系统使用经验
     */
    private String otherSystemUsed;

    /**
     * 车载硬件
     */
    private String hardwareUsed;

}
