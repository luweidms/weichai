package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.dto.QueryAuditAgingDto;
import com.youming.youche.order.vo.OrderAgingListInVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 时效罚款表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface OrderAgingInfoMapper extends BaseMapper<OrderAgingInfo> {


   Page<OrderAgingInfoDto> queryOrderAgingQuery(Page<OrderAgingInfoDto> page,
                                                @Param("orderAgingListInVo") OrderAgingListInVo orderAgingListInVo,
                                                @Param("tenantId") Long tenantId,
                                                @Param("isAging") Boolean isAging,
                                                @Param("isAgingAppeal") Boolean isAgingAppeal);


   List<QueryAuditAgingDto> queryOrderAgingList(@Param("orderId") Long orderId,
                                                @Param("state") Integer state);

   List<WorkbenchDto> getTableInvalidExamineCount();

   /**
    * 车队小程序 首页数字统计 - 时效待审核数量
    */
   Integer getStatisticsStatuteOfLimitations(@Param("agingIdStr") String agingIdStr, @Param("appealIdStr") String appealIdStr, @Param("tenantId") Long tenantId);

}
