package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.PayFeeLimit;
import com.youming.youche.order.dto.PayFeeLimitDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
public interface IPayFeeLimitService extends IBaseService<PayFeeLimit> {

    /**
     * 查询租户资金风控的配置值
     * 接口编号 10042
     *
     * @param tenantId 租户编号
     * @param cfgType  1	中标价上限 ,返回结果为金额类型，单位为分
     *                 2	成本异常上限,返回结果为金额类型，单位为分
     *                 3	收入异常上限,返回结果为金额类型，单位为分
     *                 4	司机借支上限,返回结果为金额类型，单位为分
     *                 5	招商车费用补款上限,返回结果为金额类型，单位为分
     *                 6	司机基本工资上限,返回结果为金额类型，单位为分
     *                 7	基本模式补贴上限,返回结果为金额类型，单位为分
     *                 8	自有车司机总工资上限,返回结果为金额类型，单位为分
     *                 9	合作个体户总工资上限,返回结果为金额类型，单位为分
     *                 10	里程模式补贴上限,返回结果为金额类型，单位为分
     *                 11	扫码加油上限,返回结果为金额类型，单位为分
     *                 12	扫码加油次数上限,返回结果为整数
     *                 13	是否自动打款   （0-否，1-是）
     *                 14	自有车运费 （0-私人账户付款，1-对公账户付款）
     *                 15	司机工资 （0-私人账户付款，1-对公账户付款）
     *                 16      自有车油费是否需要发票（1-需要，0-不需要）
     */
    Long getAmountLimitCfgVal(Long tenantId, Integer cfgType);


    /**
     * 判断租户是否要自动打款
     * @param tendId 租户编号
     * @return true-是，false-否
     * @throws Exception
     */
    Boolean isMemberAutoTransfer(Long tendId);

    /**
     * 判断费用支出输入值与配置项比较
     * @param value  比较值
     * @param type	 类型
     * @param subType	 科目
     * @return
     */
    void judgePayFeeLimit(Long value, String type, String subType,Long tenantId);

    /**
     * 查询费用支出配置项
     * @param
     * @return
     */
    List<PayFeeLimit> queryPayFeeLimit(Integer type, Integer subType, Long tenantId);

    /**
     * 14008
     * 查询资金风控
     *
     * @param tenantId--租户编号 type--风控类型，具体值如下
     *                       <p>
     *                       1	中标价上限
     *                       2	成本异常上限
     *                       3	收入异常上限
     *                       4	司机借支上限
     *                       5	招商车费用补款上限
     *                       6	司机基本工资上限
     *                       7	司机补贴上限
     *                       8	自有车司机总工资上限
     *                       9	合作个体户总工资上限
     *                       10	里程单价上限
     *                       11	扫码加油上限
     *                       12	扫码加油次数上限
     *                       13	是否自动打款
     */
    PayFeeLimitDto queryPayFeeLimit(Long tenantId, Integer subType);

}
