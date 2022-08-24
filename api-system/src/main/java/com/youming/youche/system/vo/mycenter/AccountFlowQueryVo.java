package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccountFlowQueryVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 16:49
 */
@Data
public class AccountFlowQueryVo implements Serializable {

    /** 用户Id */
    private Long userId;
    /** 付款账户名 */
    private String payMbrName;
    /** 收款账户名 */
    private String recvMbrName;
    /** 交易流水号 */
    private String serialNumber;
    /** 交易类型 */
    private String tranType;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 车队Id */
    private Long tenantId;
}
