package com.youming.youche.finance.dto;

import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QueryClaimExpenseInfoDto implements Serializable {
    private Long amount; //金额
    private String appReason; // 申请备注
    private Long expenseId; //报销ID
    private List<String> imgIds; //文件ID
    private List<String> imgUrls; //文件路径
    private Long orderId; //订单号
    private String plateNumber; //车牌号
    private String specialExpenseNum; //费用编号
    private Long stairCategoryId; //一级类目ID
    private String stairCategoryName; //一级类目名称
    private Integer isNeedBill; //是否需要开票
    private String isNeedBillName; //是否需要开票名称
    private Integer sts; //状态
    private Long userId; //用户ID
    private String userName; //用户名称
    private LocalDateTime appDate; //申请时间
    private String stsName; //状态名称
    private String verifyNotes; //校验节点
    private Double weightFee; //过磅重量
    private List<SysOperLog> sysOperLogs; //日志
}
