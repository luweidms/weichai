package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应收逾期
 *
 * @author hzx
 * @date 2022/4/13 16:06
 */
@Data
public class QueryDouOverdueVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long flowId;//主键
    private Long orderId; // 订单号码
    private Long sourceRegion; // 始发地点
    private Long desRegion; // 目的地点
    private String startTime; // 靠台开始
    private String endTime; // 靠台结束
    private String payName; // 付款方
    private String payConFirm; // 账户状态
    private Long sourceUserId;// 用户ID
    private Integer payUserType;// 付款用户类型
    private Long userId;// 用户ID
    private String type;// 类型
    private List<String> types;
    private List<String> payConFirms;
}
