package com.youming.youche.order.provider.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.dto.OrderDriverSubsidyDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-12
 */
public interface OrderDriverSubsidyMapper extends BaseMapper<OrderDriverSubsidy> {


    /**
     * 查找司机补贴
     * @param startDate 补贴开始时间
     * @param endDate 补贴结束时间
     * @param orderId 订单号
     * @param userId
     * @param tenantId
     * @param isHis 是否查历史
     * @param excludeOrderId 排除订单
     * @return
     */
     List<OrderDriverSubsidy> findDriverSubsidys(@Param("startDate") LocalDateTime startDate,@Param("endDate") LocalDateTime endDate,
                                                 @Param("orderId") Long orderId,
                                                 @Param("userId") Long userId,
                                                 @Param("tenantId") Long tenantId, @Param("tableName") String  tableName,
                                                 @Param("excludeOrderId") Long excludeOrderId,
                                                 @Param("isHis")Boolean isHis);

    /**
     * 查询订单司机补贴天数
     *
     * @param orderId
     * @param isHis
     * @return orderId 订单号  userId 司机Id userName 司机名称 subsidyDay 补贴天  subsidy 补贴费用
     */
    List<Map> queryOrderDriverSubsidyDay(@Param("orderId") Long orderId, @Param("isHis") Boolean isHis);

    /**
     * 查找司机未付补贴
     * 聂杰伟
     * @param orderId
     * @param userId
     * @param carDriverId
     * @param copilotUserId
     * @param tenantId
     * @param tableName
     * @return
     */
    List<OrderDriverSubsidyDto> findDriverNoPaySubsidys(@Param("orderId") Long orderId, @Param("userId")Long userId, @Param("carDriverId")Long carDriverId,
                                                        @Param("copilotUserId")Long copilotUserId, @Param("tenantId")Long tenantId,
                                                        @Param("tableName")String tableName);

}
