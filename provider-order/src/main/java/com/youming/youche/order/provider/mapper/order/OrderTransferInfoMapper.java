package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.dto.OrderTransferInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface OrderTransferInfoMapper extends BaseMapper<OrderTransferInfo> {

    /**
     * 在线接单--列表查询（30034）
     *
     * @param page
     * @param orderTransferInfoDto
     * @return
     */
    Page<OrderTransferInfoDto> queryOrderTransferInfoList(Page<OrderTransferInfoDto> page, @Param("orderTransferInfoDto") OrderTransferInfoDto orderTransferInfoDto,
                                                          @Param("tenantId") Long tenantId);
}
