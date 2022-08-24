package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SaveOrUpdateCreditRatingRuleDto implements Serializable {

    private static final long serialVersionUID = -8772709581978917994L;
    private CreditRatingRuleDto creditRatingRuleDto1;
    private CreditRatingRuleDto creditRatingRuleDto2;
    private CreditRatingRuleDto creditRatingRuleDto3;
}
