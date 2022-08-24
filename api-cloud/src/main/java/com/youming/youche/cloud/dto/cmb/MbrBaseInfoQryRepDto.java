package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MbrBaseInfoQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:58
 */
@Data
public class MbrBaseInfoQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 同请求 */
    private String mbrNo;

    /** 子商户名称 */
    private String mbrName;

    /** 业务系统中的编号 */
    private String fromMbrNo;

    /** 绑定充值账户的户名 */
    private String bindChargeAccName;

    /** 绑定充值账户的尾号 */
    private String bindChargeAccNo;

    /** 创建时间 */
    private String regTime;

    /** 商户信息，详细参数与进件时要素一致（除去图片和文件） */
    private String merchInfo;
}
