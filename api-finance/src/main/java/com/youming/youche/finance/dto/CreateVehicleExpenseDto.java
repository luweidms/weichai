package com.youming.youche.finance.dto;

import com.youming.youche.finance.domain.VehicleExpenseDetailed;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateVehicleExpenseDto implements Serializable {

    List<VehicleExpenseDetailedDto> vehicleExpenseDetailedStr;

    /**
     * 车辆费用表主键Id
     */
    private Long vehicleExpenseId;
    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 车辆Id
     */
    private Long vehicleId;
    /**
     * 车牌
     */
    private String plateNumber;
    /**
     * 司机Id
     */
    private Long carUserId;

    /**
     * 出车仪表公里数(米)
     */
    private Long mileageDepartureInstrument;

    /**
     * 收车仪表公里数(米)
     */
    private Long mileageReceivingInstrument;

    /**
     * 司机
     */
    private String userName;
    /**
     * 司机手机号
     */
    private String linkPhone;
    /**
     * 部门id
     */
    private Long orgId;
    /**
     * 归属部门
     */
    private String expenseDepartment;

    /**
     * 收车 url
     */
    private String endKmUrl;
    /**
     * 出车url
     */
    private String startKmUrl;

    /**
     * 主键id
     */
    private Long id;



}
