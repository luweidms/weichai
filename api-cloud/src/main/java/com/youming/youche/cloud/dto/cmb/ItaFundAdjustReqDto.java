package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ItaFundAdjustReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:42
 */
@Data
public class ItaFundAdjustReqDto implements Serializable {
    /** 请求流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 待调整银行流水，取自3.1返回bizSeq */
    private String origBizSeq;

    /** 调账金额，须与3.1 返回接口tranAmt一致 */
    private String origTranAmt;

    /** 转入目标子商户编号 */
    private String tgtMbrNo;

    /** 转入目标子商户 */
    private String tgtMbrName;

    /** 调整说明 */
    private String remark;
}
