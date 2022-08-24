package com.youming.youche.order.provider.mapper.transit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.transit.TransitCust;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 客户停经点Mapper接口
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
public interface TransitCustMapper extends BaseMapper<TransitCust> {

    int deleteTransitCustByOrderId(@Param("orderId") Long orderId);

}
