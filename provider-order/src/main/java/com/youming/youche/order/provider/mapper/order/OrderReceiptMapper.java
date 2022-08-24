package com.youming.youche.order.provider.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OrderReceipt;
import com.youming.youche.order.dto.OrderListAppOutDto;
import com.youming.youche.order.dto.QueryDriverOrderDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单-回单Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface OrderReceiptMapper extends BaseMapper<OrderReceipt> {

    void deleteOrderRecipt(@Param("orderId") Long orderId, @Param("flowId") String flowId);


    /**
     * 查询合作车辆列表(30039)
     * @param page
     * @param vehicleCode
     * @param userId
     * @param tenantId
     * @return
     */
    Page<OrderListAppOutDto> queryCooperationOrderList(Page<OrderListAppOutDto> page, @Param("vehicleCode") Long vehicleCode, @Param("userId") Long userId, @Param("tenantId") Long tenantId);

    /**
     * 查询司机在途订单车辆(30057)
     * @param userId
     * @return
     */
    List<QueryDriverOrderDto> queryDriverOrderPlateNumber(Long userId);


    List<QueryDriverOrderDto> queryDriverOrderPlateNumberState(Long userId);
}
