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
            throw new BusinessException("??????????????????????????????!");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("?????????????????????");
        }
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //????????????????????????
        for (BusiSubjectsRel rel:rels) {
            //?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_SUB) {
                //????????????????????????????????????
                List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimit(userId,vehicleAffiliation, OrderAccountConst.NO_PAY.NO_PAY_FINAL,tenantId,userType);
                //??????
                this.matchAmountToOrderLimit(rel.getAmountFee(), rel.getIncome() == null ? 0L : rel.getIncome() , rel.getBackIncome() == null ? 0L : rel.getBackIncome(), OrderAccountConst.NO_PAY.NO_PAY_FINAL, orderLimits);
                if(orderLimits==null||orderLimits.size()==0){
                    throw new BusinessException("????????????????????????????????????");
                }else{
                    long noPayFinal = 0L;
                    for(OrderLimit ol : orderLimits){
                        noPayFinal+=ol.getNoPayFinal();
                    }
                    if(noPayFinal==0||noPayFinal<rel.getAmountFee()){
                        throw new BusinessException("????????????????????????????????????????????????");
                    }
                }
                //??????????????????????????????????????????????????????
                List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                //??????????????????????????????
                for (OrderLimit olTemp : orderLimits) {
                    if (olTemp.getMatchAmount() != null && olTemp.getMatchAmount() > 0L) {
                        for (BusiSubjectsDtl bsd:bsds) {
                            //??????????????????
                            OrderFundFlow off = new OrderFundFlow();
                            off.setAmount(olTemp.getMatchAmount());//???????????????????????????
                            if (bsd.getId() == 15) {
                                off.setAmount(olTemp.getMatchAmount()-olTemp.getMatchIncome());//?????????????????????????????????
                            }
                            off.setBillId(billId);
                            off.setUserName(userDataInfo.getLinkman());
                            off.setBatchId(flowId+"");
                            off.setOpDate(LocalDateTime.now());
                            off.setOpId(user.getId());
                            off.setOpName(user.getName());
                            off.setVehicleAffiliation(olTemp.getVehicleAffiliation());
                            off.setOrderId(olTemp.getOrderId());//??????ID
                            off.setBusinessId(bsd.getDtlBusinessId());//????????????
                            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//????????????
                            off.setBookType(bsd.getBooType());//????????????
                            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//????????????
                            off.setSubjectsId(rel.getSubjectsId());//??????ID
                            off.setSubjectsName(rel.getSubjectsName());//????????????
                            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                            off.setBusiKey(accountDatailsId);//????????????ID
                            off.setTenantId(tenantId);
                            if (bsd.getHasIncome() != null && bsd.getHasIncome().intValue() == 1) {
                                off.setIncome(olTemp.getMatchIncome());
                            }
                            if(bsd.getHasBack()!=null && bsd.getHasBack().intValue()==1){
                                off.setBackIncome(olTemp.getMatchBackIncome());
                            }
                            off.setInoutSts(bsd.getInoutSts());//????????????:???in???out???io
                            this.createOrderFundFlow(inParam,off, user);
                            //?????????????????????
                            orderFundFlowService.saveOrUpdate(off);
                            if (bsd.getId() == 15) {
                                olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                olTemp.setOrderCash(olTemp.getOrderCash()+olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                olTemp.setNoPayCash(olTemp.getNoPayCash()+olTemp.getMatchAmount()-olTemp.getMatchIncome());
                                //??????????????????
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

                                // ????????????????????? -- 20190412???
                                AccountDetails accDet = new AccountDetails();
                                accDet.setUserId(userId);
                                //????????????????????????

                                //????????????????????????
                                accDet.setBusinessTypes(EnumConsts.BusiType.CONSUME_CODE);
                                accDet.setBusinessNumber(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE);
                                accDet.setSubjectsId(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN);
                                accDet.setSubjectsName(rel.getSubjectsName());
                                // ??????
                                if (rel.getSubjectsType() == EnumConsts.PayInter.FEE_OUT) {
                                    accDet.setAmount(-olTemp.getMatchAmount());
                                }
                                // ??????
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
            //????????????(??????)
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN){

            }
            //????????????(??????)
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_PAY_PAYABLE_IN){

            }
        }
        return null;
    }

    /*??????????????????????????????*/
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
        off.setUserId(userId);//??????ID
        off.setBillId(billId);//??????
        off.setUserName(userDataInfo.getLinkman());//?????????
        off.setBatchId(batchId);//??????
        off.setBatchAmount(amount);//???????????????
        if(user != null){
            off.setOpId(user.getId());//?????????ID
            off.setOpName(user.getName());//?????????
        }
        off.setOpDate(LocalDateTime.now());//????????????
        off.setVehicleAffiliation(vehicleAffiliation);//????????????
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//?????????????????????????????????
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
                    //????????????=?????????-???????????????
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
                throw new BusinessException("????????????");
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
        off.setUserId(userId);//??????ID
        off.setBillId(billId);//??????
        off.setUserName(userDataInfo.getLinkman());//?????????
        off.setBatchId(batchId);//??????
        off.setBatchAmount(amount);//???????????????
        if(user != null){
            off.setOpId(user.getId());//?????????ID
            off.setOpName(user.getName());//?????????
            off.setUpdateOpId(user.getId());//?????????
        }
        off.setOpDate(LocalDateTime.now());//????????????
        off.setVehicleAffiliation(vehicleAffiliation);//????????????
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//?????????????????????????????????
        return off;
    }
}
