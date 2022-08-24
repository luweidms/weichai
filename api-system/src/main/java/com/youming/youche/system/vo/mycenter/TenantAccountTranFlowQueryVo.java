package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TenantAccountTranFlowQueryVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/4/21 16:16
 */
@Data
public class TenantAccountTranFlowQueryVo implements Serializable {

    /** 车队Id */
    private Long tenantId;
    /** 银行流水号 */
    private String bankNo;
    /** 支付流水号 */
    private String payNo;
    /** 业务类型 空：所有；0；转入；1：转出 */
    private String businessType;
    /** 转出帐号 */
    private String payMbrNo;
    /** 转出账户名 */
    private String payMbrName;
    /** 转入帐号 */
    private String recvMbrNo;
    /** 转入账户名 */
    private String recvMbrName;
    /** 业务开始时间 */
    private String startTime;
    /** 业务结束时间 */
    private String endTime;

}
