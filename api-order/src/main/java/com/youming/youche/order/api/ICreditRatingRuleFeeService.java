package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.CreditRatingRuleFee;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-07
 */
public interface ICreditRatingRuleFeeService extends IBaseService<CreditRatingRuleFee> {

    /**
     * 查询保费设置
     * @param ruleId
     * @return
     */
    List<CreditRatingRuleFee> queryCreditRatingRuleFees(Long ruleId);

    /***
     * @Description: 初始化保费设置
     * @Author: luwei
     * @Date: 2022/7/28 21:42
     * @Param ruleId:
     * @return: java.util.List<com.youming.youche.order.domain.CreditRatingRuleFee>
     * @Version: 1.0
     **/
    List<CreditRatingRuleFee> queryCreditRatingRuleFeeCopy(Long ruleId);

    /**
     * 司机权益 新增CreditRatingRuleFee接口
     * @param
     * @return
     */
    Integer saveUpdateCreditRatingRuleFee(List<CreditRatingRuleFee> feeList);

    /**
     *  通过ruleId 、最小和最大限制查找保费
     *
     * @param ruleId
     * @param minFee
     * @param maxFee
     * @return
     */
    CreditRatingRuleFee getFees(Long ruleId,Integer minFee,Integer maxFee);

    /**
     * 查询保险费规则列表
     */
    List<CreditRatingRuleFee> getCreditRatingRuleFeesWx(Long ruleId);

    }

