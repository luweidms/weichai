package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/4/14 11:21
 */
@Data
public class OrderStatementListOutDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Date dependTime;
    private Integer orderState;
    private String companyName;//客户名称
    private String sourceName;//线路名称
    private Long carDriverId;
    private String carDriverMan;//司机名称
    private String carDriverPhone;//司机手机
    private Long payeeUserId;
    private String payee;//收款人名称
    private String payeePhone;//收款人手机
    private Integer isCollection;//是否代收
    private Long totalFee;//中标价
    private Long totalAmount;//总运费=中标价+异常补偿
    private Long preCashFee;//预付现金
    private Long preOilFee;//预付实体油卡
    private Long preOilVirtualFee;//预付虚拟油
    private Long preEtcFee;//预付etc
    private Long finalFee;//尾款
    private Long totalOilFee;//总油费
    private Long exceptionIn;//异常补偿
    private Long exceptionOut;//异常扣减
    private Long finePrice;//时效罚款
    private Integer preAmountFlag;//是否支付预付款
    private Boolean isHis;//是否历史订单
    private Long toTenantId;//转单车队Id
    private String sourceCode;//线路Id
    private Long customUserId;//客户ID
    private Long insuranceFee;//保费


    private Long userId;//限制表用户id
    private String userPhone;//限制表手机号
    private Long paidFinal;//限制表已付尾款 = 已付尾款=已到期+预支的尾款+已抵扣尾款
    private Integer fianlSts;//限制表尾款到期状态
    private Long paidEtc;//限制表已付etc
    private Long oilTurnCash;//限制表油转现
    private Long etcTurnCash;//限制表etc转现
    private Long pledgeOilcardFee;//限制表油卡押金
    private Long paidCash;//限制表已付现金
    private Long noPayCash;//限制表未付现金
    private String fianlStsName;//尾款到期状态名字
    private Long noPayFinal;//限制表未付尾款
    private Long noPayAmount;//订单未付金额
    private Long paidAmount;//订单已付金额
    private Long arriveFee;//限制表到付款(抵扣掉异常，时效后的)
    private String orderLine;//运输线路
    private String plateNumber;//车牌号

    private Long arrivePaymentFee;//到付款
    private Integer arrivePaymentState;//到付状态

    private String userType;
    private String orderStateName;

    public String getFianlStsName() {
        if (fianlSts != null) {
            if (fianlSts == 0) {
                setFianlStsName("未到期");
            } else if (fianlSts == 1) {
                setFianlStsName("已到期");
            } else if (fianlSts == 2) {
                setFianlStsName("未到期转到期失败");
            }
        } else {
            setFianlStsName("未支付");
        }
        return fianlStsName;
    }
}
