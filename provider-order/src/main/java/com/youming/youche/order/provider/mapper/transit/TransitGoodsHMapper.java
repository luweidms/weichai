package com.youming.youche.order.provider.mapper.transit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.transit.TransitGoodsH;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 运输货物历史表Mapper接口
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
public interface TransitGoodsHMapper extends BaseMapper<TransitGoodsH> {

    int insertTransitGoodsHBytransitGoods(@Param("orderId") Long orderId);

}
