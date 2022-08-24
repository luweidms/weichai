package com.youming.youche.market.domain.youka;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-02-14
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilEntity extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单号
     */
    private String busiCode;
    /**
     * 司机姓名
     */
    private String carDriverMan;
    /**
     * 司机手机号
     */
    private String carDriverPhone;
    /**
     * 创建时间
     */
    private LocalDateTime creationTime;
    /**
     * 客户名称
     */
    private String customName;
    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;
    /**
     * 到达市
     */
    private Integer desRegion;
    /**
     * 是否走平台线上充值：0否，1是(平台服务卡)
     */
    private Integer lineState;
    /**
     * 未核销金额
     */
    private Long noVerificateEntityFee;
    /**
     * 资金(油)渠道类型
     */
    private String oilAffiliation;
    /**
     * 油卡卡号
     */
    private String oilCarNum;
    /**
     * 1订单发起充值；2油卡管理发起充值
     */
    private Integer oilType;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 车牌
     */
    private String plateNumber;
    /**
     * 油卡金额
     */
    private Long preOilFee;
    /**
     * 充值状态：1未充值/待发起、2待付款(平台服务商卡)、3已付款(平台服务商卡)、4服务商已充值(平台服务商卡)、5已充值、6已撤销
     */
    private Integer rechargeState;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 批次号
     */
    private Long soNbr;
    /**
     * 线路名称
     */
    private String sourceName;
    /**
     * 始发市
     */
    private Integer sourceRegion;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 车辆类型
     */
    private Integer vehicleClass;
    /**
     * 车辆编号
     */
    private Long vehicleCode;
    /**
     * 车长
     */
    private String vehicleLengh;
    /**
     * 车结构
     */
    private Integer vehicleStatus;
    /**
     * 核销时间
     */
    private LocalDateTime verificationDate;
    /**
     * 核销状态：1待核销；2已核销
     */
    private Integer verificationState;
    /**
     * 代金券金额（单位分）
     */
    private Long voucherAmount;


}
