package com.youming.youche.table.dto.receivable;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 应收详情
 */
@Data
public class ReceivableDetailsDto implements Serializable {

    private static final long serialVersionUID = 4911951503648641631L;

    private Long orderId; // 订单号
    private String dependDate; // 靠台时间
    private String companyName; // 客户名称
    private String customNumber; // 客户单号
    private String customerId; // 客户回单号
    private Integer startPoint;
    private String startPointStr;// 起始地
    private Integer endPoint;
    private String endPointStr;// 到达地
    private String sourceName; // 线路名称
    private Integer lineProp;
    private String linePropStr; // 线路属性(和订单类型是一个字段)
    private Long orgId;//部门id
    private String orgName; // 归属部门
    private String goodsInfo; // 货物信息
    private String plateNumber; // 车牌号
    private String carType; // 车型
    private String trailerPlate; // 挂车车牌
    private Integer orderType;//订单类型
    private String orderTypeStr; // 订单类型
    private Integer orderState;
    private String orderStateStr; // 订单状态
    private String receiptNumber; // 发票号码
    private Integer financeSts;
    private String financeStsName; // 核销状态
    private Long getAmount;
    private Double getAmountDouble; // 核销金额
    private Integer isCreateBill;
    private String isCreateBillStr; // 账单生成
    private String billNumber; // 账单号码

    public Double getGetAmountDouble() {
        if (getGetAmount() != null) {
            setGetAmountDouble(CommonUtil.getDoubleFormatLongMoney(getGetAmount(), 2));
        } else {
            setGetAmountDouble(0.0);
        }
        return getAmountDouble;
    }

    public String getLinePropStr() {
        setLinePropStr(getOrderTypeStr());
        return linePropStr;
    }

    private Integer balanceType; // 结算方式

    private String receivableDate; // 应收日期

    private String createTime; // 全款 1 创建时间

    private Integer collectionTime; // 收款期限 预付+尾款账期 2

    private String updateTime; // 审核通过后的时间

    private Integer collectionMonth; // 预付+尾款月结 3 收款月

    private Integer collectionDay; // 预付+尾款月结 3 收款天

    private String carDependDate; // 靠台时间

}
