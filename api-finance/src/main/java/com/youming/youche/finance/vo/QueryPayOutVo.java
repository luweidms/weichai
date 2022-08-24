package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/16
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class QueryPayOutVo implements Serializable {
    private String beginDate; //开始时间
    private String endDate; //结束时间
    private String paysts; //支付状态名称
    private String accName; //账号名称
    private String queryMsg; //返回应答
    private String payType; //支付状态
    private String finishTime; //完成开始
    private String endTime; //完成结束
    private String beginPayTime; //支付开始
    private String endPayTime; //支付结束
    private Long flowId; //支付流水号
    private String batchSeq; //序列号
    private Long objId; //手机号码
    private String payBankAccName; //付款账户名
    private String payBankAccNo; //支付账号
    private String receivablesBankAccName; //收款账户名
    private String receivablesBankAccNo; //收款账号
    private String userName; //业务方
    private String busiCode; //业务单号
    private String plateNumber; //车牌号码
    private Integer isNeedBill; //票据类型
    private String dependTimeStart; //靠台开始
    private String dependTimeEnd; //靠台结束
    private String customName; //客户名称
    private String sourceName; //路线名称
    private String collectionUserName; //代收名称
    private String orderRemark; //订单备注
    private Long orgId; //归属部门
    private Long tenantId; //车队ID

    private Integer pageNum; //页码
    private Integer pageSize; //每页显示条数
    private Long serviceUserId; //服务用户ID

}
