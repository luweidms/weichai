package com.youming.youche.order.provider.mapper.order;

import com.youming.youche.order.domain.order.OrderInfoExt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.vo.OrderVerifyInfoOut;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* <p>
* 订单扩展表Mapper接口
* </p>
* @author CaoYaJie
* @since 2022-03-15
*/
    public interface OrderInfoExtMapper extends BaseMapper<OrderInfoExt> {

        List<OrderVerifyInfoOut> getOrderVerifyInfoOut(@Param("plateNumber") String plateNumber,
                                                       @Param("orderId") Long orderId,
                                                       @Param("dependTime") LocalDateTime dependTime,
                                                       @Param("orderStateLT") Integer orderStateLT,
                                                       @Param("userId") Long userId,
                                                       @Param("isQueryLastOrder") boolean isQueryLastOrder);

    List<OrderVerifyInfoOut> getOrderVerifyInfoOutH(@Param("orderId") Long orderId,
                                                   @Param("orderStateLT") Integer orderStateLT);

    }
