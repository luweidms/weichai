package com.youming.youche.order.api.order;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 订单费用扩展表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
public interface IOrderFeeExtService extends IBaseService<OrderFeeExt> {


    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    OrderFeeExt getOrderFeeExt(Long orderId);

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    OrderFeeExt selectByOrderId(Long orderId);

    /**
     * 计算切换司机补贴
     * @param orderId
     * @throws Exception
     */
    void calculateDriverSwitchSubsidy(Long orderId,Boolean isVer);


    /**
     * 保存计算切换司机补贴
     * @param orderId 订单号
     * @param userId 司机ID
     * @param isVer 是否版本
     * @param isPayed 支付状态
     * @throws Exception
     */
    void setDriverSubsidy(Long orderId, Long userId, Boolean isVer,
                          Integer isPayed, LoginInfo user);



    /**
     * 计算自有车司机的补贴
     *  1 如果车辆没有上一单的数据，直接取线路的时限 * 会员信息里面的 按工资，按趟为0，按里程0
     *  2 如果有车辆有上一单的数据，取上一单的时间到达时间，如果有校准时效（时效罚款），那这个时间，如果都没有，拿预估达到时间
     *     本单达到时间=靠台时间+线路时限
     *     补贴天数：本单到达时间-（上一单到达时间+1），如果为负数 ，就取0
     *     如果上一单的时间跟本单的是不同年月的，按本单的单月1号计算
     *
     * @param userId
     * @param tenantId
     * @param dependTime 靠台时间
     * @param arriveTime 到达时限  单位小时
     * @param orderId 排除的订单id
     * @param isCopilot 是否主驾
     * @param transitLineSize 经停点数量
     * @return
     *        num :天数
     *        fee :补贴费用
     *        date: 补贴的具体天数
     *        subsidy:每天补贴金额
     *        salary：每月薪资

     */
     Map<String,Object> culateSubsidy(Long userId, Long tenantId, LocalDateTime dependTime, Float arriveTime,
                                            Long orderId, Boolean isCopilot, Integer transitLineSize);

}
