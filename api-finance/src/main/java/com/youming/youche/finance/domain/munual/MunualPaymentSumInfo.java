package com.youming.youche.finance.domain.munual;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author zengwen
 * @date 2022/4/7 19:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MunualPaymentSumInfo extends BaseDomain {

    private Integer verificationState;
    private Long summoney;
    private Long sumBillServiceFee;
    private Long sumAppendFreight;
}
