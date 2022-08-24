package com.youming.youche.order.provider.mapper.transit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.transit.TransitCustH;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 客户停经历史表Mapper接口
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
public interface TransitCustHMapper extends BaseMapper<TransitCustH> {

    int insertTransitCustHByTransitCust(@Param("orderId") Long orderId);

}
