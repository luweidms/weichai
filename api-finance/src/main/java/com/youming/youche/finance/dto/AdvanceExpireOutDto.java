package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/13
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AdvanceExpireOutDto implements Serializable {
    private long flowId;//流水号
    private String userName;//收款方
    private String userPhone;//手机号码
    private long orderId; //订单编号
    private long userId;//用户Id
    private String driverName;//主驾名称
    private Long marginBalance;//到期款
    private int type;//到期类型
    private String typeName;//到期类型名称
    private LocalDateTime planExpireDate;//计划到期时间
    private Integer state;//状态
    private String stateName;//状态名称
    private String reason;//原因
    private String busiCode;//业务编码
    private Long soNbr;//批次号
    private String oilAffiliation;//资金来源
    private String vehicleAffiliation;//
    private Integer userType;//用户类型
    private Integer payUserType;//支付用户类型
    private String flowIds;//流水号
    /**
     * 到期类型：0自动到期；1手动到期
     */
    private Integer expireType;
    private  Double marginBalanceDouble;//到期款

}
