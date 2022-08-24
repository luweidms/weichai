package com.youming.youche.system.domain;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 服务商账单表
    * </p>
* @author liangyan
* @since 2022-03-17
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ServiceProviderBill extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 账单编号
            */
    private Long billNo;

            /**
            * 服务商名称
            */
    private String serviceProviderName;

            /**
            * 服务商类型 1.油站、2.维修、3.etc供应商
            */
    private Integer serviceProviderType;
    @TableField(exist = false)
    private String serviceProviderTypeName;

            /**
            * 账单记录
            */
    private Integer billRecords;

    /**
     * 手机号
     */
    private String phone;

            /**
            * 账单金额（元
            */
    private Double billAmount;

            /**
            * 账单周期
            */
    private String billCycle;

            /**
            * 打款状态 0：未打款:1：待确认:2：打款中:3：已打款
            */
    private Integer paymentStatus;

    /**
     * 打款状态 0：未打款:1：待确认:2：打款中:3：已打款
     */
    @TableField(exist = false)
    private String paymentStatusName;

            /**
            * 备注
            */
    private String remark;

            /**
            * 业务单号
            */
    private String billRecordsNo;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 结算金额
     */
    private Long realityBillAmout;


    private Long serviceUserId;


}
