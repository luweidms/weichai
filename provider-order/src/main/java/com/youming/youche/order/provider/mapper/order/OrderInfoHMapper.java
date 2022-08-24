package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.order.OrderDetailsAppDto;
import com.youming.youche.order.vo.OrderListInVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hzx
 * @date 2022/3/9 15:39
 */
public interface OrderInfoHMapper extends BaseMapper<OrderInfoH> {
    @Select("SELECT *from order_info_h where order_id = #{orderId}")
    List<OrderInfoH> getOrderH(Long orderId);

    /**
     * 查询订单列表
     *
     * @param orderListInVo
     * @return
     */
    List<OrderListOut> getOrderListOutExport(@Param("orderListIn") OrderListInVo orderListInVo, @Param("orderIds") List<String> orderIds, @Param("tenantId") Long tenantId);


    /**
     * 查询订单详情 (30002)
     *
     * @param orderId
     * @param isHis
     * @param selectType
     */
    OrderDetailsAppDto queryOrderDetailsAppOut(@Param("orderId") Long orderId, @Param("isHis") Integer isHis, @Param("selectType") Integer selectType);

}
