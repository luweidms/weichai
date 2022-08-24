package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/4/16 9:43
 */
@Data
public class AccountStatementMarginDto implements Serializable {

    private Long flowId; //对账单id
    private List<Map<String, Object>> balanceList;//各个月份余额
    private String billMonth;//账单月份
    private Long settlementAmount;//结算金额
    private String settlementRemark;//结算说明
}
