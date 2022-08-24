package com.youming.youche.finance.domain;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

    import java.io.Serializable;

/**
* <p>
    * 车辆费用表 历史表
    * </p>
* @author liangyan
* @since 2022-04-25
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class VehicleExpenseVer extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 车辆Id
            */
    private Long vehicleId;

            /**
            * 车队id
            */
    private Long tenantId;

            /**
            * 操作人
            */
    private Long opId;

            /**
            * 出车仪表公里数
            */
    private Long mileageDepartureInstrument;

            /**
            * 收车仪表公里数
            */
    private Long mileageReceivingInstrument;

            /**
            * 司机id
            */
    private Long carUserId;

            /**
            * 车牌
            */
    private String plateNumber;

            /**
            * 司机姓名
            */
    private String userName;

            /**
            * 司机手机号
            */
    private String linkPhone;

            /**
            * 申请单号
            */
    private String applyNo;

            /**
            * 部门id
            */
    private Long orgId;

            /**
            * 归属部门
            */
    private String expenseDepartment;

    /**
     * 状态：1待审核，2审核中，3待支付，4已完成
     */
    private Integer state;

    /**
     * 收车 url
     */
    private String endKmUrl;
    /**
     * 出车url
     */
    private String startKmUrl;


}
