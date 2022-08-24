package com.youming.youche.market.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/3/12 14:07
 */
@Data
public class UserRepairInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 维修单号
    private String repairCode;

    // 服务商名
    private String serviceName;

    // 站点名称
    private String productName;

    // 车牌号码
    private String plateNumber;

    // 司机名称
    private String driverName;

    // 司机手机
    private String driverBill;

    // 维修时间
    private String repairDateBegin;

    // 维修时间
    private String repairDateEnd;

    // 审核状态
    private Integer state;

    // 快速过滤
    private Boolean todo;

    private Long tenantId;

    private Long productId;
    private String qualityStar;
    private String priceStar;
    private String serviceStar;

    // 状态 1保养费 2维修费
    private Integer maintenanceType;

}
