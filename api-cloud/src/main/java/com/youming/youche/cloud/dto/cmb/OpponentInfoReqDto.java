package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpponentInfoReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:10
 */
@Data
public class OpponentInfoReqDto implements Serializable {
    /** 单笔请求流水 */
    private String sepReqNo;

    /** 分账对手方账号，分给子商户时填子商户的mbrNo，分给平台时填” PLT001” */
    private String oppAccNo;

    /** 分账对手方户名，分给子商户时填子商户的mbrName，分给平台时不需要填写 */
    private String oppAccName;

    /** 是否进入到冻结金额中，
     Y：是，N：否
     不填时为N */
    private String isFrozenRecv;

    /** 分账金额 */
    private String tranAmt;

    /** 摘要 */
    private String remark;
}
