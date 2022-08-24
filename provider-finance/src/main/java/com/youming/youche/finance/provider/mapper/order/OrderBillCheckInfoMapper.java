package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账单核销费用明细
 *
 * @author hzx
 * @date 2022/2/9 10:40
 */
public interface OrderBillCheckInfoMapper extends BaseMapper<OrderBillCheckInfo> {

    /**
     * 根据账单号查询核销费用明细
     *
     * @param billNumber 账单号
     * @param tenantId
     * @return
     */
    List<OrderBillCheckInfo> queryAllRecordByBillNumberAndTenantId(@Param("billNumber") String billNumber,
                                                                   @Param("tenantId") Long tenantId);

    /**
     * 删除核销记录
     *
     * @param billNumber 账单号
     * @param tenantId
     */
    int deleteCheckInfoByBillNumberAndTenantId(@Param("billNumber") String billNumber,
                                               @Param("tenantId") Long tenantId);

    /**
     * 保存核销明细
     */
    int insertChechInfo(@Param("orderBillCheckInfo") OrderBillCheckInfo orderBillCheckInfo);

}
