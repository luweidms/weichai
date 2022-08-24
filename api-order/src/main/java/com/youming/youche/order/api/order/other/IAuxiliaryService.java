package com.youming.youche.order.api.order.other;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.dto.ParametersNewDto;

import java.util.List;

public interface IAuxiliaryService {

    ParametersNewDto setParametersNew(Long userId, String billId, Long businessId, Long orderId, Long amount, String vehicleAffiliation, String finalPlanDate);

    List busiToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user);
}
