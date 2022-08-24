package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.vo.QueryDriverOilByOrderIdVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    public interface OrderOilSourceMapper extends BaseMapper<OrderOilSource> {

        List<QueryDriverOilByOrderIdVo> queryDriverOilByOrderId(@Param("orderId") Long orderId,
                                                                @Param("userType") Integer userType);

    }
