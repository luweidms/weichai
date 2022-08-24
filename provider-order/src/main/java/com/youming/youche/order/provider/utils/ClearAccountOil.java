package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SysStaticData.ACCOUNT_DETAILS_BUSINESS_NUMBER;
import static com.youming.youche.order.constant.BaseConstant.ACCOUNT_BOOK_TYPE;

/**
 * 清除油账户
 *
 * @author dacheng
 */
@Component
@RequiredArgsConstructor
public class ClearAccountOil {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private final ReadisUtil readisUtil;
    private final IOrderFundFlowService orderFundFlowService;
    private final IOrderLimitService orderLimitService;
    private final IOrderOilSourceService orderOilSourceService;
    private final IRechargeOilSourceService rechargeOilSourceService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user)  {
        Long orderId = inParam.getOrderId();
        Long businessId =inParam.getBusinessId();
        Long accountDatailsId =inParam.getAccountDatailsId();;//
        Long userId =inParam.getUserId();
        Long tenantId =inParam.getTenantId();
        Long tenantUserId = inParam.getTenantUserId();
        if (rels == null) {
            throw new BusinessException("预付明细不能为空!");
        }
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE, rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setTenantId(tenantId);
            off.setInoutSts("out");
            this.createOrderFundFlowNew(inParam, off,user);
            orderFundFlowService.saveOrUpdate(off);
            //清零订单油
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CLEAR_ORDER_ACCOUNT_OIL_FEE) {
                OrderOilSource oos = inParam.getOilSource();
                if (oos == null) {
                    throw new BusinessException("未找到订单油来源记录");
                }
                //查询订单限制数据
                OrderLimit ol = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId, -1);
                if (ol == null) {
                    throw new BusinessException("订单信息不存在!");
                }
                ol.setNoPayOil(ol.getNoPayOil() - off.getAmount());
                orderLimitService.saveOrUpdate(ol);
                //回退来源订单油
                if (oos.getOrderId().longValue() != oos.getSourceOrderId().longValue()) {
                    OrderOilSource source = orderOilSourceService.getOrderOilSource(tenantUserId, oos.getSourceOrderId(), oos.getSourceOrderId(), oos.getVehicleAffiliation(), oos.getSourceTenantId(), -1, oos.getOilAccountType(), oos.getOilConsumer(), oos.getOilBillType(), oos.getOilAffiliation());
                    if (source == null) {
                        throw new BusinessException("未找到订单号为 " + oos.getSourceOrderId() + " 油账户类型：" + oos.getOilAccountType() + " 油消费类型：" + oos.getOilConsumer() + " 油开票类型：" + oos.getOilBillType() + "订单油来源记录");
                    }
                    OrderLimit tempLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, source.getOrderId(), -1);
                    if (tempLimit == null) {
                        throw new BusinessException("未找到单号为:" + source.getOrderId() + "的订单限制表");
                    }
                    tempLimit.setNoWithdrawOil(tempLimit.getNoWithdrawOil() - off.getAmount());
                    tempLimit.setPaidOil(tempLimit.getPaidOil() - off.getAmount());
                    tempLimit.setNoPayOil(tempLimit.getNoPayOil() + off.getAmount());
                    orderLimitService.saveOrUpdate(tempLimit);
                    source.setNoPayOil(source.getNoPayOil() + oos.getNoPayOil());
                    source.setNoCreditOil(source.getNoCreditOil() + oos.getNoCreditOil());
                    source.setNoRebateOil(source.getNoRebateOil() + oos.getNoRebateOil());
                    if (source.getPaidOil() > 0) {//判断来源订单是否提前撤单了，如果已经撤单了，就不用减了
                        source.setPaidOil(source.getPaidOil() - oos.getNoPayOil());
                    }
                    if (source.getPaidCreditOil() > 0) {
                        source.setPaidCreditOil(source.getPaidCreditOil() - oos.getNoCreditOil());
                    }
                    if (source.getPaidRebateOil() > 0) {
                        source.setPaidRebateOil(source.getPaidRebateOil() - oos.getNoRebateOil());
                    }
                    orderOilSourceService.saveOrUpdate(source);
                }
                oos.setSourceAmount(oos.getSourceAmount() - oos.getNoPayOil());
                oos.setNoPayOil(0L);
                oos.setCreditOil(oos.getCreditOil() - oos.getNoCreditOil());
                oos.setNoCreditOil(0L);
                oos.setRebateOil(oos.getRebateOil() - oos.getNoRebateOil());
                oos.setNoRebateOil(0L);
                orderOilSourceService.saveOrUpdate(oos);
            }
            //清零充值油
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CLEAR_RECHARGE_ACCOUNT_OIL_FEE) {
                RechargeOilSource ros =inParam.getRechargeOilSource() ;
                if (ros == null) {
                    throw new BusinessException("未找到充值油来源记录");
                }
                if (ros.getFromFlowId() != null && ros.getFromFlowId() > 0) {
                    OrderOilSource oos =inParam.getOilSource ();
                    if (oos == null) {
                        throw new BusinessException("未找到订单油来源记录");
                    }
                    OrderLimit tempLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, oos.getOrderId(), -1);
                    if (tempLimit == null) {
                        throw new BusinessException("未找到单号为:" + oos.getOrderId() + "的订单限制表");
                    }
                    tempLimit.setPaidOil(tempLimit.getPaidOil() - off.getAmount());
                    tempLimit.setNoPayOil(tempLimit.getNoPayOil() + off.getAmount());
                    tempLimit.setNoWithdrawOil(tempLimit.getNoWithdrawOil() - off.getAmount());
                    orderLimitService.saveOrUpdate(tempLimit);
                    oos.setNoPayOil(oos.getNoPayOil() + ros.getNoPayOil());
                    oos.setNoCreditOil(oos.getNoCreditOil() + ros.getNoCreditOil());
                    oos.setNoRebateOil(oos.getNoRebateOil() + ros.getNoRebateOil());
                    if (oos.getPaidOil() > 0) {//判断来源订单是否提前撤单了，如果已经撤单了，就不用减了
                        oos.setPaidOil(oos.getPaidOil() - ros.getNoPayOil());
                    }
                    if (oos.getPaidCreditOil() > 0) {
                        oos.setPaidCreditOil(oos.getPaidCreditOil() - ros.getNoCreditOil());
                    }
                    if (oos.getPaidRebateOil() > 0) {
                        oos.setPaidRebateOil(oos.getPaidRebateOil() - ros.getNoRebateOil());
                    }
                    orderOilSourceService.saveOrUpdate(oos);
                }
                ros.setSourceAmount(ros.getSourceAmount() - ros.getNoPayOil());
                ros.setNoPayOil(0L);
                ros.setCreditOil(ros.getCreditOil() - ros.getNoCreditOil());
                ros.setNoCreditOil(0L);
                ros.setRebateOil(ros.getRebateOil() - ros.getNoRebateOil());
                ros.setNoRebateOil(0L);
                rechargeOilSourceService.saveOrUpdate(ros);
            }
        }
        return null;
    }


    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo currOper) {
        if (off == null) {
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        UserDataInfo userDataInfo = null;
        userDataInfo = userDataInfoService.get(userId);
        Long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if (currOper != null) {
            off.setOpId(currOper.getId());//操作人ID
            off.setOpName(currOper.getName());//操作人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if (off.getIncome() == null) {
            off.setIncome(0L);
        }
        if (off.getBackIncome() == null) {
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }

}
