package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SepInfoRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:21
 */
@Data
public class SepInfoRepDto implements Serializable {
    /** 同请求接口中sepReqNo流水 */
    private String sepReqNo;

    /** 银行处理流水 */
    private String sepRespNo;

    /** T：处理中
     F：失败
     Y：成功 */
    private String sepStatus;

    /** 是否进入到冻结金额中，
     Y：是，N：否 */
    private String isFrozenRecv;

    /** 当isFrozenRecv为Y时，返回银行冻结流水 */
    private String frozenNo;

    /** 分账对手方账号 */
    private String oppAccNo;

    /** 分账对手方户名 */
    private String oppAccName;

    /** 分账金额 */
    private String tranAmt;

    /** 交易日期 */
    private String tranDate;

    /** 交易完成时间 */
    private String tranTime;

    /** 收方交易后余额，-1表示无值或未同步到 */
    private String recvMbrBal;

    /** 摘要 */
    private String remark;
}
