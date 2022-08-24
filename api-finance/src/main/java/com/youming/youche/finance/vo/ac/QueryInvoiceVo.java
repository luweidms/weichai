package com.youming.youche.finance.vo.ac;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/24 14:34
 */
@Data
public class QueryInvoiceVo implements Serializable {

    /**
     * 申请号
     */
    private String applyNum;

    /**
     * 申请方
     */
    private String applyUserName;

    /**
     * 开票状态集合：1未开票>2已开票>3开票完成
     */
    private List<Integer> billState;

    /**
     * 开票方用户id
     */
    private Long userId;

    /**
     * 用户类型：0-司机 1-服务商 2-租户ID 3-HA虚拟 4-HA实体 5-车队员工
     */
    private Integer type;
}
