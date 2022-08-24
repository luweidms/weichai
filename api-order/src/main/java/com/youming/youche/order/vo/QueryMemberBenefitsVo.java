package com.youming.youche.order.vo;

import com.youming.youche.order.domain.CreditRatingRule;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueryMemberBenefitsVo implements Serializable {

    private CreditRatingRule creditRatingRule1;

    private CreditRatingRule creditRatingRule2;

    private CreditRatingRule creditRatingRule3;

}
