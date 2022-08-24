package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SysStaticData.ACCOUNT_DETAILS_BUSINESS_NUMBER;
import static com.youming.youche.order.constant.BaseConstant.ACCOUNT_BOOK_TYPE;

@Component
@RequiredArgsConstructor
public class ForceZhangPingNew {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    private final ReadisUtil readisUtil;
    private final IOrderFundFlowService orderFundFlowService;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderLimitService orderLimitService;
    private final IOilCardManagementService oilCardManagementService;
    private final IRechargeOilSourceService rechargeOilSourceService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user){

        Long userId =inParam.getUserId();
        Long flowId =inParam.getFlowId();
        Long tenantId =inParam.getTenantId();
        String zhangPingType =inParam.getZhangPingType();
        OrderLimit olTemp = inParam.getOrderLimitBase();
        List<OrderOilSource> list =inParam.getSourceList();
        long tenantUserId = inParam.getTenantUserId();
        String type =inParam.getType();
        RechargeOilSource rechargeOilSource = inParam.getRechargeOilSource();

        if (rels == null) {
            throw new BusinessException("结余明细不能为空!");
        }
        if (userId <= 0) {
            throw new BusinessException("请输入用户id");
        }
        if (StringUtils.isEmpty(zhangPingType)) {
            throw new BusinessException("未找到平账类型");
        }
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if ("1".equals(type)) {//订单油
                if (olTemp == null) {
                    throw new BusinessException("未找到订单限制表");
                }
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("zhang_ping_order_record");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setTenantId(tenantId);
                off.setInoutSts("out");// 收支状态:收in支out转io
                this.createOrderFundFlowNew(inParam, off,user);
                orderFundFlowService.saveOrUpdate(off);
                //订单未付油
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT) {
                    // 订单限制表操作
                    olTemp.setNoPayOil(olTemp.getNoPayOil() - off.getAmount());
                    if (list == null) {
                        throw new BusinessException("平账未付油时，未找到订单油来源记录");
                    }
                    for (OrderOilSource oos : list) {
                        if (oos.getOrderId().longValue() != oos.getSourceOrderId().longValue()) {
                            OrderOilSource orderOilSource = orderOilSourceService.getOrderOilSource(tenantUserId, oos.getSourceOrderId(), oos.getSourceOrderId(), oos.getVehicleAffiliation(), oos.getSourceTenantId(),-1,oos.getOilAccountType(),oos.getOilConsumer(),oos.getOilBillType(),oos.getOilAffiliation());
                            if (orderOilSource == null) {
                                throw new BusinessException("根据用户id：" + tenantUserId + " 订单号：" + oos.getSourceOrderId() + "资金渠道：" + oos.getVehicleAffiliation() + "油资金渠道：" + oos.getVehicleAffiliation() + "租户id：" + oos.getSourceTenantId() + " 油账户类型：" + oos.getOilAccountType() + " 油消费类型：" + oos.getOilConsumer() + " 油开票类型：" + oos.getOilBillType() + "未找到油资金来源记录");
                            }
                            long oil = ( oos.getNoPayOil() + oos.getNoCreditOil() + oos.getNoRebateOil());
                            orderOilSource.setNoPayOil(orderOilSource.getNoPayOil() + oos.getNoPayOil());
                            orderOilSource.setNoCreditOil(orderOilSource.getNoCreditOil() + oos.getNoCreditOil());
                            orderOilSource.setNoRebateOil(orderOilSource.getNoRebateOil() + oos.getNoRebateOil());
                            orderOilSource.setPaidOil(orderOilSource.getPaidOil() - oos.getNoPayOil());
                            orderOilSource.setPaidCreditOil(orderOilSource.getPaidCreditOil() - oos.getNoCreditOil());
                            orderOilSource.setPaidRebateOil(orderOilSource.getPaidRebateOil() - oos.getNoRebateOil());
                            orderOilSourceService.saveOrUpdate(orderOilSource);
                            OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, oos.getSourceOrderId(),-1);
                            if (orderLimit == null) {
                                throw new BusinessException("未找到订单号为：" + oos.getOrderId() + "的订单限制记录");
                            }
                            orderLimit.setNoPayOil(orderLimit.getNoPayOil() + oil);
                            orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - oil);
                            orderLimit.setPaidOil(orderLimit.getPaidOil() - oil);
                            orderLimitService.saveOrUpdate(orderLimit);
                        }
                        oos.setNoPayOil(0l);
                        oos.setNoCreditOil(0l);
                        oos.setNoRebateOil(0l);
                        orderOilSourceService.saveOrUpdate(oos);
                    }
                }
                //未付ETC
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT) {
                    // 订单限制表操作
                    olTemp.setNoPayEtc(olTemp.getNoPayEtc() - off.getAmount());
                }
                //未付未到期或欠款
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT) {
                    // 订单限制表操作
                    olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                }
                //油卡抵押金
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_OUT) {
                    // 订单限制表操作
                    olTemp.setPledgeOilcardFee(olTemp.getPledgeOilcardFee() - off.getAmount());
                    //油卡押金记录抹平
                    List<OilCardManagement> oilCardByOrderId = oilCardManagementService.getOilCardByOrderId(olTemp.getOrderId(), olTemp.getTenantId());
                    if (oilCardByOrderId != null && oilCardByOrderId.size() > 0){
                        for (OilCardManagement cardNum : oilCardByOrderId) {
                            cardNum.setPledgeOrderId(-1L);
                            cardNum.setPledgeFee(-1L);
                            oilCardManagementService.saveOrUpdate(cardNum);
                        }
                    }
                }
                orderLimitService.saveOrUpdate(olTemp);
            }
            if ("2".equals(type)) {
                //司机账户未付油
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_RECHARGE_OIL_OUT) {
                    if (rechargeOilSource == null) {
                        throw new BusinessException("平账未付充值油时，未找到油充值记录");
                    }
                    if (!rechargeOilSource.getRechargeOrderId().equals(rechargeOilSource.getSourceOrderId())) {
                        OrderOilSource orderOilSource = orderOilSourceService.getOrderOilSource(tenantUserId, Long.parseLong(rechargeOilSource.getSourceOrderId()), Long.parseLong(rechargeOilSource.getSourceOrderId()), rechargeOilSource.getVehicleAffiliation(), rechargeOilSource.getSourceTenantId(),-1,rechargeOilSource.getOilAccountType(),rechargeOilSource.getOilConsumer(),rechargeOilSource.getOilBillType(),rechargeOilSource.getOilAffiliation());
                        if (orderOilSource == null) {
                            throw new BusinessException("根据用户id：" + tenantUserId + " 订单号：" + rechargeOilSource.getSourceOrderId() + "资金渠道：" + rechargeOilSource.getVehicleAffiliation() + "油资金渠道：" + rechargeOilSource.getVehicleAffiliation() + "租户id：" + rechargeOilSource.getSourceTenantId() + "未找到油资金来源记录");
                        }
                        long oil = ( rechargeOilSource.getNoPayOil() + rechargeOilSource.getNoCreditOil() + rechargeOilSource.getNoRebateOil());
                        orderOilSource.setNoPayOil(orderOilSource.getNoPayOil() + rechargeOilSource.getNoPayOil());
                        orderOilSource.setNoCreditOil(orderOilSource.getNoCreditOil() + rechargeOilSource.getNoCreditOil());
                        orderOilSource.setNoRebateOil(orderOilSource.getNoRebateOil() + rechargeOilSource.getNoRebateOil());
                        orderOilSource.setPaidOil(orderOilSource.getPaidOil() - rechargeOilSource.getNoPayOil());
                        orderOilSource.setPaidCreditOil(orderOilSource.getPaidCreditOil() - rechargeOilSource.getNoCreditOil());
                        orderOilSource.setPaidRebateOil(orderOilSource.getPaidRebateOil() - rechargeOilSource.getNoRebateOil());
                        orderOilSourceService.saveOrUpdate(orderOilSource);
                        OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, Long.parseLong(rechargeOilSource.getSourceOrderId()),-1);
                        if (orderLimit == null) {
                            throw new BusinessException("未找到订单号为：" + rechargeOilSource.getSourceOrderId() + "的订单限制记录");
                        }
                        orderLimit.setNoPayOil(orderLimit.getNoPayOil() + oil);
                        orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - oil);
                        orderLimit.setPaidOil(orderLimit.getPaidOil() - oil);
                        orderLimitService.saveOrUpdate(orderLimit);
                    }
                    rechargeOilSource.setNoPayOil(0l);
                    rechargeOilSource.setNoCreditOil(0l);
                    rechargeOilSource.setNoRebateOil(0l);
                    rechargeOilSourceService.saveOrUpdate(rechargeOilSource);
                }
            }
        }
        return null;
    }



    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo currOper) {
        if(off==null){
            off = new OrderFundFlow();
        }
        Long userId =inParam.getTenantUserId();
        String billId =inParam.getBillId();
        String batchId =inParam.getBatchId();
        UserDataInfo userDataInfo = null;
        userDataInfo = userDataInfoService.getById(userId);
        Long amount =inParam.getAmount();
        String vehicleAffiliation =inParam.getVehicleAffiliation();
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
