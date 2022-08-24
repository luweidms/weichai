package com.youming.youche.finance.vo.munual;

import lombok.Data;

import java.io.Serializable;

/**
 * 现金打款入参
 *
 * @author zengwen
 * @date 2022/4/6 17:25
 */
@Data
public class QueryPayoutIntfsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 应付开始时间
    String startTime;

    // 应付结束时间
    String endTime;

    // 核销开始时间
    String verificationStartTime;

    // 核销结束时间
    String verificationEndTime;

    // 靠台开始时间
    String dependTimeStart;

    // 靠台结束时间
    String dependTimeEnd;

    // 业务方
    String userName;

    // 车牌号码
    String plateNumber;

    // 业务单号
    String busiCode;

    // 客户名称
    String customName;

    // 线路名称
    String sourceName;

    // 代收名称
    String collectionUserName;

    // 收票主体
    String billLookUp;

    // 票据类型
    Integer isNeedBill;

    // 打款状态
    Integer verificationState;

    // 电话号码
    Long phone;

    String accountName;

    String txnType;

    // 线路ID
    Long sourceUserId;

    // 订单描述
    String orderRemark;

    // 用户类型
    Integer userType;

    Long payObjId;

    Long payTenantId;

    // 页码
    Integer pageNum;

    // 每页显示条数
    Integer pageSize;

    // 流水号(主键)
    Long flowId;


}
