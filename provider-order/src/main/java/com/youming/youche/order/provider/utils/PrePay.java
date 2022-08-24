package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.PayoutOrder;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.BusiSubjectsDtlMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/13 15:00
 */
@Component
public class PrePay {

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    IOrderLimitService orderLimitService;

    @Resource
    BusiSubjectsDtlMapper busiSubjectsDtlMapper;

    @Resource
    IPayoutOrderService payoutOrderService;

    @Resource
    IAccountDetailsService accountDetailsService;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long businessId = inParam.getBusinessId();
        Long flowId = inParam.getFlowId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String billId = inParam.getBillId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        String oilAffiliation = inParam.getOilAffiliation();
        Long tenantId = inParam.getTenantId();
        Integer userType = inParam.getUserType();
        if (rels == null) {
            throw new BusinessException("预支费用明细不能为空!");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("请输入用户类型");
        }
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel:rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //预支金额
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_SUB) {
                //查询司机对应资金渠道订单
                List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimit(userId,vehicleAffiliation, OrderAccountConst.NO_PAY.NO_PAY_FINAL,tenantId,userType);
                //司机
                this.matchAmountToOrderLimit(rel.getAmountFee(), rel.getIncome() == null ? 0L : rel.getIncome() , rel.getBackIncome() == null ? 0L : rel.getBackIncome(), OrderAccountConst.NO_PAY.NO_PAY_FINAL, orderLimits);
                if(orderLimits==null||orderLimits.size()==0){
                    throw new BusinessException("预支金额匹配不到订单！！");
                }else{
                    long noPayFinal = 0L;
                    for(OrderLimit ol : orderLimits){
                        noPayFinal+=ol.getNoPayFinal();
                    }
                    if(noPayFinal==0||noPayFinal<rel.getAmountFee()){
                        throw new BusinessException("预支金额与订单尾款金额不匹配！！");
                    }
                }
                //查询业务科目明细，将现有业务科目细分
                List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                //将油费金额分摊给订单
                for (OrderLimit olTemp : orderLimits) {
                    if (olTemp.getMatchAmount() != null && olTemp.getMatchAmount() > 0L) {
                        for (BusiSubjectsDtl bsd:bsds) {
                            //资金流水操作
                            OrderFundFlow off = new OrderFundFlow();
                            off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                            if (bsd.getId() == 15) {
                                off.setAmount(olTemp.getMatchAmount()-olTemp.getMatchIncome());//现金增加要减去车队利润
                            }
                            off.setBillId(billId);
                            off.setUserName(userDataInfo.getLinkman());
                            off.setBatchId(flowId+"");
                            off.setOpDate(LocalDateTime.now());
                            off.setOpId(user.getId());
                            off.setOpName(user.getName());
                            off.setVehicleAffiliation(olTemp.getVehicleAffiliation());
                            off.setOrderId(olTemp.getOrderId());//订单ID
                            off.setBusinessId(bsd.getDtlBusinessId());//业务类型
                            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                            off.setBookType(bsd.getBooType());//账户类型
                            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//账户类型
                            off.setSubjectsId(rel.getSubjectsId());//科目ID
                            off.setSubjectsName(rel.getSubjectsName());//科目名称
                            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                            off.setBusiKey(accountDatailsId);//业务流水ID
                            off.setTenantId(tenantId);
                            if (bsd.getHasIncome() != null && bsd.getHasIncome().intValue() == 1) {
                                off.setIncome(olTemp.getMatchIncome());
                            }
                            if(bsd.getHasBack()!=null && bsd.getHasBack().intValue()==1){
                                off.setBackIncome(olTemp.getMatchBackIncome());
                            }
                            off.setInoutSts(bsd.getInoutSts());//收支状态:收in支out转io
                            this.createOrderFundFlow(inParam,off, user);
                            //订单限制表操作
                            orderFundFlowService.saveOrUpdate(off);
                            if (bsd.getId() == 15) {
                                olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                olTemp.setOrderCash(olTemp.getOrderCash()+olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                olTemp.setNoPayCash(olTemp.getNoPayCash()+olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                //记录操作订单
                                PayoutOrder payoutOrder = new PayoutOrder();
                                payoutOrder.setAmount(olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                                payoutOrder.setVehicleAffiliation(vehicleAffiliation);
                                //payoutOrder.setOilAffiliation(oilAffiliation);
                                payoutOrder.setTenantId(tenantId);
                                payoutOrder.setUserId(userId);
                                payoutOrder.setBatchId(flowId);
                                payoutOrder.setOrderId(olTemp.getOrderId());
                                payoutOrderService.save(payoutOrder);
                            }
                            if (bsd.getId() == 14) {
                                olTemp.setOrderFinal(olTemp.getOrderFinal() - olTemp.getMatchAmount());
                                olTemp.setNoPayFinal(olTemp.getNoPayFinal() - olTemp.getMatchAmount());
                                olTemp.setFinalIncome(olTemp.getFinalIncome() + olTemp.getMatchIncome());
                                olTemp.setMarginAdvance((olTemp.getMarginAdvance() == null ? 0L : olTemp.getMarginAdvance()) + olTemp.getMatchAmount());

                                // 写入账户明细表 -- 20190412加
                                AccountDetails accDet = new AccountDetails();
                                accDet.setUserId(userId);
                                //会员体系改造开始

                                //会员体系改造结束
                                accDet.setBusinessTypes(EnumConsts.BusiType.CONSUME_CODE);
                                accDet.setBusinessNumber(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE);
                                accDet.setSubjectsId(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN);
                                accDet.setSubjectsName(rel.getSubjectsName());
                                // 支出
                                if (rel.getSubjectsType() == EnumConsts.PayInter.FEE_OUT) {
                                    accDet.setAmount(-olTemp.getMatchAmount());
                                }
                                // 支入
                                if (rel.getSubjectsType() == EnumConsts.PayInter.FEE_IN) {
                                    accDet.setAmount(olTemp.getMatchAmount());
                                }
                                accDet.setUserType(userType);
                                accDet.setCostType(rel.getSubjectsType());
                                accDet.setCreateTime(LocalDateTime.now());
                                accDet.setNote(rel.getSubjectsName());
                                accDet.setSoNbr(CommonUtil.createSoNbr());
                                accDet.setOrderId(olTemp.getOrderId() + "");
                                accDet.setOtherUserId(null);
                                accDet.setOtherName("");
                                accDet.setBookType(bsd.getBooType());
                                accDet.setTenantId(tenantId);
                                accountDetailsService.saveAccountDetails(accDet);
                            }
                            orderLimitService.saveOrUpdate(olTemp);
                        }
                    }
                }
            }
            //应收逾期(预支)
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN){

            }
            //应付逾期(预支)
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_PAY_PAYABLE_IN){

            }
        }
        return null;
    }

    /*产生订单资金流水数据*/
    public OrderFundFlow setOrderFundFlow(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user){
        if(off == null){
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        //UserDataInfo userDataInfo = userDataInfoSV.getUserByUserId(userId);
        UserDataInfo userDataInfo = null;
        try {
            userDataInfo = userDataInfoService.getUserDataInfo(userId);
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
        if(user != null){
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }

    private List<OrderLimit> matchAmountToOrderLimit(long amount, long income, long backIncome, String fieldName, List<OrderLimit> orderLimits) {
        double incomeRatio = ((double)income)/((double)amount);
        double backIncomeRatio = ((double)backIncome)/((double)amount);
        for (OrderLimit ol : orderLimits) {
            try {
                Method method = ol.getClass().getDeclaredMethod("get" + fieldName);
                Long orderAmount = (Long) method.invoke(ol);
                if (orderAmount == null || orderAmount == 0L) {
                    continue;
                }
                if (amount > orderAmount) {
                    ol.setMatchAmount(orderAmount);
                    ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                    ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                    //剩余金额=总金额-分摊掉金额
                    amount = amount - orderAmount;
                    income -= ol.getMatchIncome();
                    backIncome -= ol.getMatchBackIncome();
                } else if (amount <= orderAmount) {
                    ol.setMatchAmount(amount);
                    ol.setMatchIncome(income);
                    ol.setMatchBackIncome(backIncome);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("转换异常");
            }
        }
        return orderLimits;
    }

    private OrderFundFlow createOrderFundFlow(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user){
        if(off==null){
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        //UserDataInfo userDataInfo = userDataInfoSV.getUserByUserId(userId);
        UserDataInfo userDataInfo = null;
        try {
            userDataInfo = userDataInfoService.getUserDataInfo(userId);
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
        if(user != null){
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
            off.setUpdateOpId(user.getId());//修改人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }
}
