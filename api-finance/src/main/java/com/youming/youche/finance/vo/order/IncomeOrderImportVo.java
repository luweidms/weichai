package com.youming.youche.finance.vo.order;

import com.youming.youche.finance.domain.order.OrderDiffInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzx
 * @date 2022/4/9 14:57
 */
@Data
public class IncomeOrderImportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<OrderDiffInfo> orderDiffInfos;//差异列表
    private String orderId;
    private String billNumber;

}
