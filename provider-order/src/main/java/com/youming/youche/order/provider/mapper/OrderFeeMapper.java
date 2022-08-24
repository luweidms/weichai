package com.youming.youche.order.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.OrderFee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单费用表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-09
 */
public interface OrderFeeMapper extends BaseMapper<OrderFee> {

    /**
     * 查询车队订单未付客户油
     * @param tenantId
     * @return
     * @throws Exception
     */
    Long queryOrderNoPayCustomOil(@Param("tenantId") Long tenantId,@Param("excludeOrderId") Long excludeOrderId);

    /**
     * 查询司机 下的指定类型订单 id
     * @param driverId
     * @param paymentWay
     * @return
     */
    List<Long> queryOrderByDriver(@Param("driverId") Long driverId,@Param("paymentWay") Integer paymentWay);

}
