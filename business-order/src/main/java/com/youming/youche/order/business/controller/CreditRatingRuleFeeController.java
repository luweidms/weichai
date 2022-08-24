package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.ICreditRatingRuleFeeService;
import com.youming.youche.order.domain.CreditRatingRuleFee;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-07
*/
@RestController
@RequestMapping("credit/rating/rule/fee")
public class CreditRatingRuleFeeController extends BaseController<CreditRatingRuleFee,ICreditRatingRuleFeeService> {

    @DubboReference(version = "1.0.0")
    ICreditRatingRuleFeeService creditRatingRuleFeeService;
    @Override
    public ICreditRatingRuleFeeService getService() {
        return creditRatingRuleFeeService;
    }

    /**
     * 司机权益里的保费接口
     * @Param  ruleId    credit-rating-rule 里的自增id
     * @return
     */
    @GetMapping("/queryCreditRatingRuleFees")
    public ResponseResult queryCreditRatingRuleFees (Long ruleId)  {
            if(ruleId == null){
                throw new BusinessException("司机权益数据有误");
            }
            List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeService.queryCreditRatingRuleFees(ruleId);
            return ResponseResult.success(creditRatingRuleFees);
    }
}
