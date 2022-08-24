package com.youming.youche.finance.vo;

import com.youming.youche.finance.domain.VehicleExpenseDetailed;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VehicleExpenseVo implements Serializable {

    private static final long serialVersionUID = 4088276817392653926L;

    private List<VehicleExpenseDetailed> vehicleExpenseDetailedStr;

    /**
     * VehicleExpense id
     */
    private Long id;

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

    /**
     * 归属部门
     */
    private String expenseDepartment;

    /**
     * 归属部门id
     */
    private Long orgId;
}
