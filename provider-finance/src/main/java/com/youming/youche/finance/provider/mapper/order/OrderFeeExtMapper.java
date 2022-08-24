package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderFeeExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 订单费用扩展表Mapper接口
* </p>
* @author liangyan
* @since 2022-03-15
*/
    public interface OrderFeeExtMapper extends BaseMapper<OrderFeeExt> {

    List<OrderFeeExt> selectFee(@Param("id") Long id, @Param("tenantId") Long tenantId,@Param("beginDate")String beginDate,@Param("endDate") String endDate);
    }
