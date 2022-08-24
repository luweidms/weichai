package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.order.vo.QueryOrderLimitByCndVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单付款回显表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface OrderLimitMapper extends BaseMapper<OrderLimit> {
    /**
     *该函数的功能描述:判断订单是否有尾款
     * luona
     * @param orderIds
     * @param userType
     * @return
     */
     Map<Long,Boolean> hasFinalOrderLimit(@Param("orderIds") List<Long> orderIds, @Param("userType") Integer userType);

    /**
     * 查询到期列表
     * @param page
     * @param advanceExpireOutVo
     * @return
     */
    Page<OrderLimit> queryOrderLimits(@Param("page") Page<OrderLimit> page, @Param("advanceExpireOutVo") AdvanceExpireOutVo advanceExpireOutVo);

    Page<OrderLimit> selectOr(@Param("userId")String userId, @Param("orderId")String orderId, @Param("startTime")String startTime,
                              @Param("endTime")String endTime, @Param("sourceRegion")String sourceRegion, @Param("desRegion")String desRegion,
                              @Param("userType")Integer userType,@Param("tenantId")Long tenantId,
                              Page<Object> objectPage);


    List<OrderLimit> queryOrderLimitByCnd(@Param("vo") QueryOrderLimitByCndVo vo);

    int updateOrderLimit(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("setSql") String setSql);

    List<OrderLimit> getAgentOrder(@Param("userId") Long userId,
                                   @Param("vehicleAffiliation") String vehicleAffiliation,
                                   @Param("faceBalanceUnused") String faceBalanceUnused,
                                   @Param("faceMarginUnused") String faceMarginUnused,
                                   @Param("userType") Integer userType);

    OrderLimit getOrderLimitByUserIdAndOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId, @Param("userType") Integer userType);

    List<OrderLimit> getOrderLimit(@Param("userId") Long userId,
                                   @Param("capitalChannel") String capitalChannel,
                                   @Param("noPayType") String noPayType,
                                   @Param("tenantId") Long tenantId,
                                   @Param("userType") Integer userType);

    List<OrderLimit> getOrderLimitUserId(@Param("userId") Long userId,
                                         @Param("orderId") Long orderId,
                                         @Param("tenantId") Long tenantId,
                                         @Param("userType") Integer userType);

    List<OrderLimit> getFaceOrderLimit(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("userType") Integer userType);

    int updateOrderLimitUser(@Param("orderId") Long orderId,  @Param("setSql") String setSql);

    List<OrderLimit> getOrderLimitByOrderId(@Param("orderId") Long orderId, @Param("userType") Integer userType);

    OrderLimit getOneOrderLimitByOrderId(@Param("orderId") Long orderId);

    OrderLimit selectOrderLimitId(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("sign") String sign);

    OrderLimit selectOrderLimitByOrderAndUser(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
