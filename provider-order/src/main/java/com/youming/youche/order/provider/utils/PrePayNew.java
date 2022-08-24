package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IBusiSubjectsDtlService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
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

@Component
@RequiredArgsConstructor
public class PrePayNew {
    private final IOrderLimitService orderLimitService;
    private final IOrderFundFlowService orderFundFlowService;
    private final IBusiSubjectsDtlService busiSubjectsDtlService;
    private final ReadisUtil readisUtil;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;

    /**
     * 消费油，维修保养特殊预支
     *
     * @param inParam
     * @param rels
     * @return
     * @throws Exception
     */
    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {

        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long userId = inParam.getUserId();
        Long tenantId = inParam.getTenantId();
        Integer isNeedBill = inParam.getIsNeedBill();
        Integer advanceType = inParam.getAdvanceType();
        List<OrderLimit> orderLimits = inParam.getOrderLimit();
        if (rels == null) {
            throw new BusinessException("预支费用明细不能为空!");
        }
        if (orderLimits == null || orderLimits.size() <= 0) {
            throw new BusinessException("订单集合为空");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //预支金额
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_SUB) {
                //司机
                this.matchAdvanceAmountToOrderLimit(rel.getAmountFee(), rel.getIncome() == null ? 0L : rel.getIncome(), rel.getBackIncome() == null ? 0L : rel.getBackIncome(), userId, vehicleAffiliation, tenantId, isNeedBill, OrderAccountConst.NO_PAY.NO_PAY_FINAL, orderLimits, advanceType);
                //查询业务科目明细，将现有业务科目细分
                List<BusiSubjectsDtl> bsds = busiSubjectsDtlService.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                //将油费金额分摊给订单
                for (OrderLimit olTemp : orderLimits) {
                    if (olTemp.getMatchAmount() != null && olTemp.getMatchAmount() > 0L) {
                        for (BusiSubjectsDtl bsd : bsds) {
                            //资金流水操作
                            OrderFundFlow off = new OrderFundFlow();
                            off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                            if (bsd.getId() == 15) {
                                off.setAmount(olTemp.getMatchAmount() - olTemp.getMatchIncome());//现金增加要减去利润
                            }
                            off.setOrderId(olTemp.getOrderId());//订单ID
                            off.setBusinessId(bsd.getDtlBusinessId());//业务类型
                            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                            off.setBookType(bsd.getBooType());//账户类型
                            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE, String.valueOf(off.getBookType())).getCodeName());//账户类型
                            off.setSubjectsId(rel.getSubjectsId());//科目ID
                            off.setSubjectsName(rel.getSubjectsName());//科目名称
                            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                            off.setBusiKey(accountDatailsId);//业务流水ID
                            off.setTenantId(tenantId);
                            if (bsd.getHasIncome() != null && bsd.getHasIncome().intValue() == 1) {
                                off.setIncome(olTemp.getMatchIncome());
                            }
                            if (bsd.getHasBack() != null && bsd.getHasBack().intValue() == 1) {
                                off.setBackIncome(olTemp.getMatchBackIncome());
                            }
                            off.setInoutSts(bsd.getInoutSts());//收支状态:收in支out转io
                            this.setOrderFundFlow(inParam, off, user);
                            //订单限制表操作
                            orderFundFlowService.saveOrUpdate(off);
                            if (bsd.getId() == 15) {
                                olTemp.setNoPayCash(olTemp.getNoPayCash() + olTemp.getMatchAmount() - olTemp.getMatchIncome());
                                olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + olTemp.getMatchAmount() - olTemp.getMatchIncome());
                                olTemp.setOrderCash(olTemp.getOrderCash() + olTemp.getMatchAmount() - olTemp.getMatchIncome());
                            }
                            if (bsd.getId() == 14) {
                                olTemp.setOrderFinal(olTemp.getOrderFinal() - olTemp.getMatchAmount());
                                olTemp.setNoPayFinal(olTemp.getNoPayFinal() - olTemp.getMatchAmount());
                                olTemp.setFinalIncome(olTemp.getFinalIncome() + olTemp.getMatchIncome());
                            }
                            orderLimitService.saveOrUpdate(olTemp);
                        }
                        olTemp.setMatchAmount(0L);
                        olTemp.setMatchIncome(0L);
                    }
                }
            }
        }
        return null;
    }

    public List<OrderLimit> matchAdvanceAmountToOrderLimit(Long amount, Long income, Long backIncome, Long userId,
                                                           String vehicleAffiliation, Long tenantId, Integer isNeedBill,
                                                           String fieldName, List<OrderLimit> orderLimits,
                                                           Integer advanceType) {
        double incomeRatio = ((double) income) / ((double) amount);
        double backIncomeRatio = ((double) backIncome) / ((double) amount);
        for (OrderLimit ol : orderLimits) {
            Long orderAmount = ol.getNoPayFinal();
            //司机要票(维修商能不能开票)
            if (isNeedBill == OrderAccountConst.ORDER_IS_NEED_BILL.YES) {
                if (advanceType == OrderAccountConst.ADVANCE_TYPE.OIL_ADVANCE) {//油消费预支
                    if (orderAmount != null && orderAmount != 0L && ol.getUserId() == userId && ol.getVehicleAffiliation().equals(vehicleAffiliation) && tenantId == ol.getTenantId() && (ol.getIsNeedBill() == OrderAccountConst.ORDER_BILL_TYPE.carrierBill || ol.getIsNeedBill() == OrderAccountConst.ORDER_BILL_TYPE.platformBill)) {
                        if (amount > orderAmount) {
                            ol.setMatchAmount(orderAmount);
                            ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                            ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                            //剩余金额=总金额-分摊掉金额
                            amount = amount - orderAmount;
                        } else if (amount <= orderAmount) {
                            ol.setMatchAmount(amount);
                            ol.setMatchIncome(new Double(amount * incomeRatio).longValue());
                            ol.setMatchBackIncome(new Double(amount * backIncomeRatio).longValue());
                            break;
                        }
                    }
                }
                if (advanceType == OrderAccountConst.ADVANCE_TYPE.REPAIR_ADVANCE) {//维修费
                    if (orderAmount != null && orderAmount != 0L && ol.getUserId() == userId && ol.getVehicleAffiliation().equals(vehicleAffiliation) && tenantId == ol.getTenantId()) {
                        if (amount > orderAmount) {
                            ol.setMatchAmount(orderAmount);
                            ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                            ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                            //剩余金额=总金额-分摊掉金额
                            amount = amount - orderAmount;
                        } else if (amount <= orderAmount) {
                            ol.setMatchAmount(amount);
                            ol.setMatchIncome(new Double(amount * incomeRatio).longValue());
                            ol.setMatchBackIncome(new Double(amount * backIncomeRatio).longValue());
                            break;
                        }
                    }
                }
            } else {
                if (orderAmount != null && orderAmount != 0L && ol.getUserId() == userId && ol.getVehicleAffiliation().equals(vehicleAffiliation) && tenantId == ol.getTenantId() && ol.getIsNeedBill() == OrderAccountConst.ORDER_BILL_TYPE.notNeedBill) {
                    if (amount > orderAmount) {
                        ol.setMatchAmount(orderAmount);
                        ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                        ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                        //剩余金额=总金额-分摊掉金额
                        amount = amount - orderAmount;
                    } else if (amount <= orderAmount) {
                        ol.setMatchAmount(amount);
                        ol.setMatchIncome(new Double(amount * incomeRatio).longValue());
                        ol.setMatchBackIncome(new Double(amount * backIncomeRatio).longValue());
                        break;
                    }
                }
            }
        }
        return orderLimits;
    }

    /*产生订单资金流水数据*/
    public OrderFundFlow setOrderFundFlow(ParametersNewDto inParam, OrderFundFlow off, LoginInfo currOper) {
        if (off == null) {
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        ;
        //UserDataInfo userDataInfo = userDataInfoSV.getUserByUserId(userId);
        UserDataInfo userDataInfo = null;
        try {
            userDataInfo = userDataInfoService.get(userId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long amount = inParam.getAmount();
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
