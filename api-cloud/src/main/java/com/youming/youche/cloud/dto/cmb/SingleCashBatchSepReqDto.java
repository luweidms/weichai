package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SingleCashBatchSepReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:27
 */
@Data
public class SingleCashBatchSepReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 分账批次请求流水 */
    private String reqNo;

    /** 上次分账银行返回流水，仅当该流水对应的totalStatus为SP时才能使用 */
    private String lastRespNo;

    /** 付方子商户号 */
    private String mbrNo;

    /** 付方子商户名称 */
    private String mbrName;

    /** 是否从付方冻结余额中转出，
     Y：是，N：否 */
    private String isFrozenPay;

    /** 分账总金额，每次都与第一次相同 */
    private String prepareSepAmt;

    /** 银行返回的冻结流水，当isFrozenPay为Y时必填。该冻结流水可以从接口4.2、接口4.8的返回参数中获取 */
    private String frozenNo;

    /** 支取方式:
     1:无校验
     2:短信验证码，需要附带短信包 */
    private String wdwTyp;

    /** 分账对手方列表，最多10条 */
    private List<OpponentInfoReqDto> opponentList;
}
