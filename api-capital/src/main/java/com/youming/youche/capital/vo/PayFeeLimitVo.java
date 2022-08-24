package com.youming.youche.capital.vo;

import com.youming.youche.capital.domain.PayFeeLimit;
import lombok.Data;

import java.io.Serializable;

@Data
public class PayFeeLimitVo extends PayFeeLimit implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 单位名称
     */
    private String unitName;

    private String payType;

    public String getPayType() {

        if (getValue() == 0l) {
            return "私人账户付款";
        } else if (getValue() == 1l) {
            return "对公账户付款";
        }else{
            return "未设置付款账户";
        }
    }

    private String needBill;

    public String getNeedBill() {

        if (getValue() == 0l) {
            return "不需要开票";
        } else if (getValue() == 1l) {
            return "需要开票";
        } else {
            return "需要开票";
        }

    }
}
