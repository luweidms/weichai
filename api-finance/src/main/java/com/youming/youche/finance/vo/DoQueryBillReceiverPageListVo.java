package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/22 13:10
 */
@Data
public class DoQueryBillReceiverPageListVo implements Serializable {

    /**
     * 选择月份
     */
    private List<String> monList;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人名称
     */
    private String billReceiverName;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机手机号
     */
    private String driverMobile;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

}
