package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.ICreditRatingRuleFeeService;
import com.youming.youche.order.domain.CreditRatingRuleFee;
import com.youming.youche.order.provider.mapper.CreditRatingRuleFeeMapper;
import com.youming.youche.util.CommonUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* <p>
    *  服务实现类
    * </p>
* @author liangyan
* @since 2022-03-07
*/
@DubboService(version = "1.0.0")
    public class CreditRatingRuleFeeServiceImpl extends BaseServiceImpl<CreditRatingRuleFeeMapper, CreditRatingRuleFee> implements ICreditRatingRuleFeeService {

    @Resource
    private CreditRatingRuleFeeMapper creditRatingRuleFeeMapper;

    @Override
    public List<CreditRatingRuleFee> queryCreditRatingRuleFees(Long ruleId) {

        QueryWrapper<CreditRatingRuleFee> creditRatingRuleFeeQueryWrapper = new QueryWrapper<>();
        creditRatingRuleFeeQueryWrapper.eq("rule_id", ruleId)
                .eq("state", 1);
        List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeMapper.selectList(creditRatingRuleFeeQueryWrapper);
        for (CreditRatingRuleFee creditRatingRuleFee: creditRatingRuleFees) {
            Long insuranceFee = creditRatingRuleFee.getInsuranceFee();
            if(insuranceFee != null && insuranceFee >0 ){
                BigDecimal s = CommonUtils.objToLongDiv100(insuranceFee);
                if (s!= null){
                    creditRatingRuleFee.setInsuranceFeeStr(s.toString());
                }
                //double v = insuranceFee.doubleValue() / 100;
            }else {
                creditRatingRuleFee.setInsuranceFeeStr("0");
            }
        }
        return creditRatingRuleFees;
    }

    @Override
    public List<CreditRatingRuleFee> queryCreditRatingRuleFeeCopy(Long ruleId) {
        QueryWrapper<CreditRatingRuleFee> creditRatingRuleFeeQueryWrapper = new QueryWrapper<>();
        creditRatingRuleFeeQueryWrapper.eq("rule_id", ruleId)
                .eq("state", 1);
        List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeMapper.selectList(creditRatingRuleFeeQueryWrapper);
        return creditRatingRuleFees;
    }

    @Override
    @Transactional
    public Integer saveUpdateCreditRatingRuleFee(List<CreditRatingRuleFee> feeList) {

        QueryWrapper<CreditRatingRuleFee> creditRatingRuleFeeQueryWrapper = new QueryWrapper<>();
        creditRatingRuleFeeQueryWrapper.eq("rule_id",feeList.get(0).getRuleId());
        creditRatingRuleFeeMapper.delete(creditRatingRuleFeeQueryWrapper);
        int i=0;
        for (CreditRatingRuleFee creditRatingRuleFee : feeList) {
            int insert = creditRatingRuleFeeMapper.insert(creditRatingRuleFee);
            i++;
        }
        return i;
    }

    @Override
    public CreditRatingRuleFee getFees(Long ruleId, Integer minFee, Integer maxFee) {
        QueryWrapper<CreditRatingRuleFee> creditRatingRuleFeeQueryWrapper = new QueryWrapper<>();
        if(maxFee == null){
            creditRatingRuleFeeQueryWrapper.eq("rule_id", ruleId)
                    .eq("state", 1)
                    .ge("min_fee",8000);
        }else {
            creditRatingRuleFeeQueryWrapper.eq("rule_id", ruleId)
                    .eq("state", 1)
                    .ge("min_fee",minFee)
                    .le("max_fee",maxFee);
        }
        List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeMapper.selectList(creditRatingRuleFeeQueryWrapper);
        if(creditRatingRuleFees != null && creditRatingRuleFees.size() > 0){
            return creditRatingRuleFees.get(0);
        }
        return null;
    }

    @Override
    public List<CreditRatingRuleFee> getCreditRatingRuleFeesWx(Long ruleId) {
        LambdaQueryWrapper<CreditRatingRuleFee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditRatingRuleFee::getRuleId, ruleId);
        List<CreditRatingRuleFee> list = this.list(queryWrapper);
        return list;
    }

}
