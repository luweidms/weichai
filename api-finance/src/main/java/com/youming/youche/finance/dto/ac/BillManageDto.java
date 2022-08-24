package com.youming.youche.finance.dto.ac;

import com.youming.youche.util.CommonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/4/24 14:29
 */
@Data
public class BillManageDto implements Serializable {

    private Long orderId; // 订单号
    private String dateTimeStart;// 时间开始
    private String dateTimeEnd;// 时间结束
    private String plateNumber;// 车牌号码
    private Integer vehicleClass;// 车辆种类
    private Integer isNeedBill;// 票据类型
    private Integer billState;// 状态
    private String billStateName;
    private String applyNum;// 申请号
    private Date dependTime;// 靠台时间
    private String vehicleClassName;// 车辆种类中文
    private Long cash;// 现金
    private Double cashDouble;
    private Long oil;// 邮费
    private Double oilDouble;
    private Long etc;// ETC
    private Double etcDouble;
    private Long income;// 抵扣金额
    private Double incomeDouble;
    private Long withdrawAmount;// 提现金额
    private Long noWithdrawAmount;// 未提现金额
    private Double withdrawAmountDouble;
    private Double noWithdrawAmountDouble;
    private String payAcctName;// 付款账户名称
    private String receiveAcctName;// 收款账户名称
    private String openBillUser;// 开票方
    private String openUserName;// 开票方
    private Long billServiceAmount;// 票据服务费
    private Double billServiceAmountDouble;
    private String expressNumber;//快递单号
    private String invoiceNumber;//发票号
    private String[] invoiceNumberList;//发票号
    private Date createDate;//申请时间
    private Integer orderNum;//订单数
    private Long busiAmount;//业务金额
    private Double busiAmountDouble;
    private Long openBillAmount;//开票总金额
    private Double openBillAmountDouble;//开票总金额
    private Integer deductionNum;//抵扣票
    private Integer applyState;
    private String applyStateName;
    private String applyUserName;//申请方
    private String hurryUserName;//催票方
    private String remark;//备注
    private Long flowId;
    private Long billCost;//票据成本
    private Double billCostDouble;//票据成本
    private Integer type;//1：查询开票中的订单数   2：查询申请记录里面的订单数
    private String expressCompany;//快递公司
    private Integer  billType;//1非平台票，2平台票
    private String  billTypeIn;//1非平台票，2平台票
    private String  billTypeName;
    private Long openUserId;
    private Integer orderType;//1增加订单 2减少订单
    private Long tenantId;
    private Double billRate;//开票率
    private String invoiceRecordCode;//票据中心申请号
    private String applyNumList;
    private Long approvalAmount;
    private Double approvalAmountDouble;
    private Integer orderState;//订单是否全部完成 1 未完成 2已完成
    private Integer orderFund;//资金是否走完  1 未完成 2已完成
    private Long appendFreight;//附加运费
    private double appendFreightDouble;//附加运费
    private String notOpenBillCause;//不可开票原因

    public Double getBillCostDouble() {
        if(billCost != null){
            setBillCostDouble(CommonUtils.getDoubleFormatLongMoney(billCost,2));
        }
        return billCostDouble;
    }

    public Double getBusiAmountDouble() {
        if(busiAmount != null){
            setBusiAmountDouble(CommonUtils.getDoubleFormatLongMoney(busiAmount,2));
        }
        return busiAmountDouble;
    }

    public Double getOpenBillAmountDouble() {
        if(openBillAmount != null){
            setOpenBillAmountDouble(CommonUtils.getDoubleFormatLongMoney(openBillAmount,2));
        }
        return openBillAmountDouble;
    }

    public Double getCashDouble() {
        if(cash != null){
            setCashDouble(CommonUtils.getDoubleFormatLongMoney(cash,2));
        }
        return cashDouble;
    }

    public Double getOilDouble() {
        if(oil != null){
            setOilDouble(CommonUtils.getDoubleFormatLongMoney(oil,2));
        }
        return oilDouble;
    }

    public Double getEtcDouble() {
        if(etc != null){
            setEtcDouble(CommonUtils.getDoubleFormatLongMoney(etc, 2));
        }
        return etcDouble;
    }

    public Double getIncomeDouble() {
        if(income != null){
            setIncomeDouble(CommonUtils.getDoubleFormatLongMoney(income, 2));
        }
        return incomeDouble;
    }

    public Double getWithdrawAmountDouble() {
        if(withdrawAmount != null){
            setWithdrawAmountDouble(CommonUtils.getDoubleFormatLongMoney(withdrawAmount, 2));
        }
        return withdrawAmountDouble;
    }

    public Double getNoWithdrawAmountDouble() {
        if(noWithdrawAmount != null){
            setNoWithdrawAmountDouble(CommonUtils.getDoubleFormatLongMoney(noWithdrawAmount, 2));
        }
        return noWithdrawAmountDouble;
    }

    public Double getBillServiceAmountDouble() {
        if(billServiceAmount != null){
            setBillServiceAmountDouble(CommonUtils.getDoubleFormatLongMoney(billServiceAmount, 2));
        }
        return billServiceAmountDouble;
    }

    public double getAppendFreightDouble() {
        if(appendFreight != null){
            setAppendFreightDouble(CommonUtils.getDoubleFormatLongMoney(appendFreight, 2));
        }
        return appendFreightDouble;
    }
}
