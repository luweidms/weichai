package com.youming.youche.finance.domain;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

    import java.io.Serializable;

/**
* <p>
    * 车辆费用表
    * </p>
* @author liangyan
* @since 2022-04-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class VehicleExpense extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 车辆Id
            */
    private Long vehicleId;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 司机Id
     */
    private Long carUserId;

            /**
            * 车队id
            */
    private Long tenantId;

            /**
            * 操作人
            */
    private Long opId;

            /**
            * 出车仪表公里数(米)
            */
    private Long mileageDepartureInstrument;

            /**
            * 收车仪表公里数(米)
            */
    private Long mileageReceivingInstrument;
    /**
     * 车牌
     */
    private String plateNumber;
    /**
     * 司机
     */
    private String userName;
    /**
     * 司机手机号
     */
    private String linkPhone;

    /**
     * 归属部门
     */
    private String expenseDepartment;

    /**
     * 归属部门id
     */
    private Long orgId;
    /**
     * 状态：1待审核，2审核中，3审核通过，4审核不通过,8撤销申请
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
