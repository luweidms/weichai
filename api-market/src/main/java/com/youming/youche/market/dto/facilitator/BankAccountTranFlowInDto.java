package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountTranFlowInDto implements Serializable {
    /** 用户Id */
    private String userId;
    /** 帐户级别：0个人；1车队 */
    private String acclevel;
    /** 转出帐号 */
    private String payMbrNo;
    /** 转出帐户名 */
    private String payMbrName;
    /** 转入帐号 */
    private String recvMbrNo;
    /** 转入帐户名 */
    private String recvMbrName;
    /** 银行流水号 */
    private String respNo;
    /** 业务类型 */
    private String tranType;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /**类型*/
    private String businessType;
}
