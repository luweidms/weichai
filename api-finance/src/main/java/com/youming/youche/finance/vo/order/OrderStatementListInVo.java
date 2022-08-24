package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 9:24
 */
@Data
public class OrderStatementListInVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;// 订单号
    private Long tenantId;
    private List<String> plateNumbers;//车牌号
    private String monthStr;//月份 格式 :yyyy-MM,yyyy-MM
    private List<OrderStatementListInMonthDate> monthArr;
    private String plateNumber;//车牌号 App查询条件
    private String orderState;//订单状态
    private List<String> orderStates;
    private Boolean state = false;
    private String companyName;//客户名称
    private String sourceName;//线路名称
    private String carDriverMan;//司机名称
    private String carDriverPhone;//司机手机
    private String payee;//收款人名称
    private String payeePhone;//收款人手机
    private String isCollection;//是否代收
    private Boolean isCollection0 = false;
    private Boolean isCollection1 = false;
    private String finalState;//尾款状态
    private List<String> finalStates = new ArrayList<>();
    private String startTime;//查询开始时间
    private String endTime;//查询结束时间
    private String tenantName;//账单来源车队
    private String tennatBill;//账单来源车队手机号
    private Long userId;//账单接收人用户id
    private String userPhone;
    private String userType;
}
