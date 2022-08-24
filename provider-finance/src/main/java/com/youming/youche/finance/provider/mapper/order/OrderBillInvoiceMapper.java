package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderBillInvoice;
import com.youming.youche.finance.dto.order.OrderBillInvoiceDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/2/8 13:08
 */
public interface OrderBillInvoiceMapper extends BaseMapper<OrderBillInvoice> {

    /**
     * 查询发票号
     */
    List<Map<String, Object>> getReceiptNumbersByBillNumbers(@Param("billNumbers") String billNumbers);

    /**
     * 查询账单发票列表
     */
    List<OrderBillInvoiceDto> queryBillReceipt(@Param("billNumbers") String billNumber,
                                               @Param("tenantId") Long tenantId);

    int deleteInvoiceRecordByBillNumber(@Param("billNumber") String billNumber,
                                        @Param("tenantId") Long tenantId);

    int insertInvoiceRecord(@Param("orderBillInvoice") OrderBillInvoice orderBillInvoice);
}
