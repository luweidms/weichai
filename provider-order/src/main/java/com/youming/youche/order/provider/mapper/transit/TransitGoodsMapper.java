package com.youming.youche.order.provider.mapper.transit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.transit.TransitGoods;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 运输货物表Mapper接口
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
public interface TransitGoodsMapper extends BaseMapper<TransitGoods> {

    int deleteTransitGoodsByOrderId(@Param("orderId") Long orderId);

}
