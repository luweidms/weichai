package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.order.OrderBillInfo;
import com.youming.youche.finance.dto.order.ExportQueryBillInfoDto;
import com.youming.youche.finance.dto.order.ExportQueryOrderInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInfoDto;
import com.youming.youche.finance.vo.order.OrderBillInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hzx
 * @date 2022/2/8 10:58
 */
public interface OrderBillInfoMapper extends BaseMapper<OrderBillInfo> {

    /**
     * 根据主键获取账单信息
     */
    OrderBillInfo getOrderBillInfo(@Param("billNumber") String billNumber);

    /**
     * 应收账单列表
     */
    Page<OrderBillInfoDto> doQuery(Page<OrderBillInfoDto> objectPage,
                                   @Param("orderBillInfoVO") OrderBillInfoVo orderBillInfoVO,
                                   @Param("tenantId") Long tenantId);

    List<OrderBillInfoDto> doQueryExport(@Param("orderBillInfoVO") OrderBillInfoVo orderBillInfoVO,
                                         @Param("tenantId") Long tenantId);

    int updateRecordByBillNumber(@Param("orderBillInfo") OrderBillInfo orderBillInfo);

    int updateRecordByBillNumbers(@Param("billNumbers") String billNumbers,
                                  @Param("billStsCheckAll") Integer billStsCheckAll,
                                  @Param("billStsCheckPart") Integer billStsCheckPart,
                                  @Param("billSts") Integer billSts,
                                  @Param("operId") Long operId,
                                  @Param("createPeceiptName") String createPeceiptName);

    List<OrderBillInfo> getOrderBillInfoByBillNumbers(@Param("billNumbers") String billNumbers);

    int insertOrderBillInfo(@Param("orderBillInfo") OrderBillInfo orderBillInfo);

    List<ExportQueryOrderInfoDto> exportQueryOrderInfo(@Param("billNumber") String billNumbers, @Param("tenantId") Long tenantId);

    List<ExportQueryBillInfoDto> exportQueryBillInfo(@Param("billNumber") String billNumbers, @Param("tenantId") Long tenantId);

    // 获取账单的对账名称
    String getBillNumberCheckName(@Param("billNumber") String billNumber, @Param("tenantId") Long tenantId);

    // 获取订单的对账名称
    String getOrderIdCheckName(@Param("orderId") String orderId, @Param("tenantId") Long tenantId);

}
