package com.youming.youche.order.api;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.dto.CreditRatingRuleDto;
import com.youming.youche.order.dto.OrderFeeDto;
import com.youming.youche.order.dto.SaveOrUpdateCreditRatingRuleDto;
import com.youming.youche.order.vo.CreditRatingRuleVo;
import com.youming.youche.order.vo.OrderFeeVo;
import com.youming.youche.order.vo.QueryMemberBenefitsVo;

import java.util.List;


/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-04
*/
public interface ICreditRatingRuleService extends IService<CreditRatingRule> {

    //录入订单页面 指派车辆选择（外调车、业务招商车、自有车（承包模式）、指派车队）的时候查询该司机成本模式比例
    CreditRatingRuleVo getLevel (Integer carUserType, String accessToken, Float guideMerchant, Double guidePrice);

    /**
     * 接单的算费接口
     * @param accessToken
     * @param orderFeeDto
     * @param subsidy
     * @return
     */
    OrderFeeVo getEstimatedCosts(String accessToken,OrderFeeDto orderFeeDto, Long subsidy) ;

    /**
     * 通过车辆类型查询比例规则
     * @param carUserType  1社会车、2招商车、3自有车、4、个体户、5、外调合同车
     * @param accessToken
     * @return
     */
    QueryMemberBenefitsVo queryMemberBenefits(String accessToken);

    /**
     *  司机权益 新增司机各种比例接口
     * @param saveOrUpdateCreditRatingRuleDto
     * @param accessToken
     */
    void saveOrUpdateCreditRatingRule(SaveOrUpdateCreditRatingRuleDto saveOrUpdateCreditRatingRuleDto, String accessToken);

    /**
     * 司机权益 新增CreditRatingRule接口
     * @param creditRatingRule
     * @return
     */
    Long saveUpdateCreditRatingRule(CreditRatingRule creditRatingRule);

    /**
     * 根据用户编号和租户编号查询用户权益配置信息
     * @param userId
     * @param tenantId
     * @return
     */
    CreditRatingRule getCreditRatingRule(Long userId, Long tenantId);

    /**
     * 更新用户权益配置
     * @param creditRatingRule
     * @param tenantId
     * @return
     */
    Integer updateCreditRatingRule(CreditRatingRuleDto creditRatingRule, Long tenantId);
    /**
     * 查询当前用户的权益
     * @param tenantId 租户ID
     * @param carUserType 车量类型 1-公司自有车，2-招商车挂靠车，3-临时外调车
     * @return
     * @throws Exception
     */
    CreditRatingRule queryCreditRatingRule(Long tenantId, Integer carUserType);

    /**
     * 会员权益列表查询
     * @param carUserType
     * @param tenantId
     * @return
     */
    List<CreditRatingRule> queryMemberBenefits(Integer carUserType,Long tenantId);

    /**
     * 14007
     * 查询司机权益接口
     *
     * @param tenantId    租户ID
     * @param carUserType 车量类型 1-公司自有车，2-招商车挂靠车，3-临时外调车
     */
    CreditRatingRule queryCreditRatingRuleWx(Long tenantId, Integer carUserType);

    /***
     * @Description: 查询司机权益
     * @Author: luwei
     * @Date: 2022/7/23 16:47
     * @Param tenantId:
     * @return: null
     * @Version: 1.0
     **/
    List<CreditRatingRule>  getCreditRatingRules(Long tenantId);


}
