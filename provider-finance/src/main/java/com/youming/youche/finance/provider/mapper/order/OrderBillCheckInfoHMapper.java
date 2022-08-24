package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderBillCheckInfoH;
import com.youming.youche.finance.dto.order.OrderCheckInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账单核销费用明细历史表
 *
 * @author hzx
 * @date 2022/2/9 14:10
 */
public interface OrderBillCheckInfoHMapper extends BaseMapper<OrderBillCheckInfoH> {

    int insertCheckInfoH(@Param("billNumber") String billNumber,
                         @Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 查询最近一次插入的账单的核销记录
     */
    List<OrderCheckInfoDto> queryRecentRecordByBillNumber(@Param("billNumber") String billNumber);

}
