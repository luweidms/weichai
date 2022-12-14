package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.domain.BusiSubjectsDtlOperate;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.BusiSubjectsDtlOperateMapper;
import com.youming.youche.order.provider.mapper.order.BusiSubjectsDtlMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.vo.OperDataParam;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengwen
 * @date 2022/6/13 16:24
 */
@Component
public class EtcConsume {

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    BusiSubjectsDtlMapper busiSubjectsDtlMapper;

    @Resource
    BusiSubjectsDtlOperateMapper busiSubjectsDtlOperateMapper;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Integer isOwnCar = inParam.getIsOwnCar();
        Long etcId = inParam.getEtcId();
        Long tenantId = inParam.getTenantId();
        if (rels == null) {
            throw new BusinessException("Etc????????????????????????!");
        }
        // ??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        // ????????????????????????
        for (BusiSubjectsRel rel : rels) {
            // ?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if (isOwnCar != 1 ) {
                // ETC????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_ARREARS_CO_FEE) {
                    // ??????????????????
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// ???????????????????????????
                    off.setOrderId(orderId);// ??????ID
                    off.setBusinessId(businessId);// ????????????
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
                    off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// ????????????
                    off.setSubjectsId(rel.getSubjectsId());// ??????ID
                    off.setSubjectsName(rel.getSubjectsName());// ????????????
                    off.setBusiTable("cm_etc_info");// ???????????????
                    off.setBusiKey(etcId);// ????????????ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// ????????????:???in???out???io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                }
                // ????????????????????????????????????
                OrderLimit olTemp = orderLimitMapper.getOrderLimitByUserIdAndOrderId(orderId,userId, -1);
                if(olTemp==null){
                    continue;
                }
                // Etc??????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE) {
                    // ??????????????????
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// ???????????????????????????
                    off.setOrderId(olTemp.getOrderId());// ??????ID
                    off.setBusinessId(businessId);// ????????????
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
                    off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// ????????????
                    off.setSubjectsId(rel.getSubjectsId());// ??????ID
                    off.setSubjectsName(rel.getSubjectsName());// ????????????
                    off.setBusiTable("cm_etc_info");// ???????????????
                    off.setBusiKey(etcId);// ????????????ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// ????????????:???in???out???io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // ?????????????????????
                    List<OperDataParam> odps = new ArrayList();
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getIncome()), "-"));//??????ETC
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));//??????ETC??????
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getAmount()), "+"));//??????ETC??????(??????)
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getIncome()), "-"));//??????ETC??????(??????)
                    odps.add(new OperDataParam("NO_PAY_ETC", String.valueOf(off.getAmount()), "-"));//??????ETC??????(??????)
                    this.updateOrderLimit(olTemp.getOrderId(),userId,odps);
                }
                // ????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_AVAILABLE_FEE) {
                    // ??????????????????
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// ???????????????????????????
                    off.setOrderId(olTemp.getOrderId());// ??????ID
                    off.setBusinessId(businessId);// ????????????
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
                    off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// ????????????
                    off.setSubjectsId(rel.getSubjectsId());// ??????ID
                    off.setSubjectsName(rel.getSubjectsName());// ????????????
                    off.setBusiTable("cm_etc_info");// ???????????????
                    off.setBusiKey(etcId);// ????????????ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// ????????????:???in???out???io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // ?????????????????????
                    List<OperDataParam> odps = new ArrayList();
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getAmount()), "+"));//??????ETC
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getIncome()), "-"));//??????ETC
                    odps.add(new OperDataParam("account_balance", String.valueOf(off.getAmount()), "-"));//?????????????????????
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getAmount()), "+"));//??????ETC??????(??????)
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getIncome()), "-"));//??????ETC??????(??????)
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));//??????ETC??????
                    odps.add(new OperDataParam("DEBT_MONEY", String.valueOf(off.getAmount()), "+"));//??????
                    odps.add(new OperDataParam("PAID_DEBT", String.valueOf(off.getAmount()), "+"));//????????????
                    this.updateOrderLimit(olTemp.getOrderId(),userId,odps);
                }
                // ETC????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_ARREARS_FEE) {
                    // ??????????????????
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// ???????????????????????????
                    off.setOrderId(olTemp.getOrderId());// ??????ID
                    off.setBusinessId(businessId);// ????????????
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
                    off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// ????????????
                    off.setSubjectsId(rel.getSubjectsId());// ??????ID
                    off.setSubjectsName(rel.getSubjectsName());// ????????????
                    off.setBusiTable("cm_etc_info");// ???????????????
                    off.setBusiKey(etcId);// ????????????ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// ????????????:???in???out???io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // ?????????????????????
                    List<OperDataParam> odps = new ArrayList();
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getAmount()), "+"));//??????ETC
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getIncome()), "-"));//??????ETC
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getAmount()), "+"));//??????ETC??????(??????)
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getIncome()), "-"));//??????ETC??????(??????)
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));//??????ETC??????
                    odps.add(new OperDataParam("DEBT_MONEY", String.valueOf(off.getAmount()), "+"));//??????
                    odps.add(new OperDataParam("NO_PAY_DEBT", String.valueOf(off.getAmount()), "+"));//????????????
                    this.updateOrderLimit(olTemp.getOrderId(),userId,odps);
                }
                // ????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ORDER_CASH_CONSUME_FEE) {
                    // ??????????????????
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// ???????????????????????????
                    off.setOrderId(olTemp.getOrderId());// ??????ID
                    off.setBusinessId(businessId);// ????????????
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
                    off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// ????????????
                    off.setSubjectsId(rel.getSubjectsId());// ??????ID
                    off.setSubjectsName(rel.getSubjectsName());// ????????????
                    off.setBusiTable("cm_etc_info");// ???????????????
                    off.setBusiKey(etcId);// ????????????ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// ????????????:???in???out???io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // ?????????????????????
                    List<OperDataParam> odps = new ArrayList();
                    //odps.add(new OperDataParam("PAID_CASH", String.valueOf(rel.getAmountFee()), "+"));//?????????????????????(??????)
                    odps.add(new OperDataParam("ORDER_CASH", String.valueOf(rel.getAmountFee()), "-"));
                    odps.add(new OperDataParam("NO_PAY_CASH", String.valueOf(rel.getAmountFee()), "-"));//?????????????????????(??????)
                    odps.add(new OperDataParam("NO_WITHDRAW_CASH", String.valueOf(rel.getAmountFee()), "-"));//?????????????????????
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(rel.getIncome()), "+"));
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(rel.getAmountFee()-rel.getIncome()), "+"));//??????ETC
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(rel.getAmountFee()), "+"));//??????ETC
                    this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                }
                // ???????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE) {
                    // ??????????????????????????????????????????????????????
                    List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                    for (BusiSubjectsDtl bsd : bsds) {
                        // ??????????????????
                        OrderFundFlow off = new OrderFundFlow();
                        off.setOrderId(olTemp.getOrderId());// ??????ID
                        off.setBusinessId(bsd.getDtlBusinessId());// ????????????
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// ????????????
                        off.setBookType(bsd.getBooType());// ????????????
                        off.setBookTypeName(bsd.getRemark());// ????????????
                        off.setSubjectsId(rel.getSubjectsId());// ??????ID
                        off.setSubjectsName(rel.getSubjectsName());// ????????????
                        off.setBusiTable("cm_etc_info");// ???????????????
                        off.setBusiKey(etcId);// ????????????ID
                        off.setTenantId(tenantId);
                        if(bsd.getBooType()==3001 || bsd.getBooType()==3007){//????????????ETC??????????????????????????????
                            off.setAmount(rel.getAmountFee()-rel.getPoundageFee());// ???????????????????????????
                        }else{
                            off.setAmount(rel.getAmountFee());// ???????????????????????????
                        }
                        if ((bsd.getHasIncome() != null && bsd.getHasIncome().intValue() == 1)) {
                            off.setIncome(rel.getIncome());
                        }
                        if(bsd.getBooType()==3003){//??????????????????????????????
                            off.setIncome(rel.getPoundageFee());
                        }
                        if (bsd.getHasBack() != null && bsd.getHasBack().intValue() == 1) {
                            off.setBackIncome(rel.getBackIncome());
                        }
                        off.setInoutSts(bsd.getInoutSts());// ????????????:???in???out???io
//                        off.setPoundageFee(rel.getPoundageFee());//?????????????????? // ????????????????????????????????????  ??????????????????
                        this.createOrderFundFlow(inParam, off, user);
                        orderFundFlowService.saveOrUpdate(off);
                        // ?????????????????????
                        List<OperDataParam> odps = this.getOpateDateParam(bsd,off);
                        this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                    }
                }
                // ETC????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ARREARS_ETC_CONSUME_FEE) {
                    // ??????????????????????????????????????????????????????
                    List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                    for (BusiSubjectsDtl bsd : bsds) {
                        // ??????????????????
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(rel.getAmountFee());// ???????????????????????????
                        off.setOrderId(olTemp.getOrderId());// ??????ID
                        off.setBusinessId(bsd.getDtlBusinessId());// ????????????
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(bsd.getDtlBusinessId())).getCodeName());// ????????????
                        off.setBookType(bsd.getBooType());// ????????????
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(bsd.getBooType())).getCodeName());// ????????????
                        off.setSubjectsId(rel.getSubjectsId());// ??????ID
                        off.setSubjectsName(rel.getSubjectsName());// ????????????
                        off.setBusiTable("cm_etc_info");// ???????????????
                        off.setBusiKey(etcId);// ????????????ID
                        off.setTenantId(tenantId);
                        if (bsd.getHasIncome() != null&& bsd.getHasIncome().intValue() == 1) {
                            off.setIncome(rel.getIncome());
                        }
                        if (bsd.getHasBack() != null&& bsd.getHasBack().intValue() == 1) {
                            off.setBackIncome(rel.getBackIncome());
                        }
                        off.setInoutSts(bsd.getInoutSts());// ????????????:???in???out???io
                        this.createOrderFundFlow(inParam, off, user);
                        // ?????????????????????
                        List<OperDataParam> odps = this.getOpateDateParam(bsd,off);
                        orderFundFlowService.saveOrUpdate(off);
                        this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                    }
                    // ??????
                    List<OrderLimit> orderLimitsFinal = orderLimitMapper.getOrderLimit(userId,vehicleAffiliation, OrderAccountConst.NO_PAY.NO_PAY_FINAL,tenantId,-1);
                    //??????????????????????????????
                    long deductibleAmount = 0L;
                    for(OrderLimit ol : orderLimitsFinal){
                        if (ol.getOrderId() == orderId) {
                            continue;
                        }
                        deductibleAmount += ol.getNoPayFinal();
                    }
                    if(deductibleAmount > rel.getAmountFee()){//??????????????????????????????????????????
                        //????????????
                        this.matchAmountToOrderLimit(olTemp.getNoPayDebt(),0,0,OrderAccountConst.NO_PAY.NO_PAY_FINAL,orderLimitsFinal);
                        // ??????????????????????????????
                        for (OrderLimit olTempFinal : orderLimitsFinal) {
                            if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                            }
                        }
                    }else{
                        if(deductibleAmount > 0){
                            //????????????
                            this.matchAmountToOrderLimit(olTemp.getNoPayDebt(),0,0,OrderAccountConst.NO_PAY.NO_PAY_FINAL,orderLimitsFinal);
                            // ??????????????????????????????
                            for (OrderLimit olTempFinal : orderLimitsFinal) {
                                if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                    this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                                }
                            }
                        }
                        //????????????????????????????????????????????????
                    }
                }
            }else if(isOwnCar == 1){
                //???????????????
                //Etc???????????????????????????
                OrderLimit olTemp = orderLimitMapper.getOrderLimitByUserIdAndOrderId(orderId, userId, -1);
                // Etc????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE) {
                    // ??????????????????
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// ???????????????????????????
                    off.setOrderId(olTemp.getOrderId());// ??????ID
                    off.setBusinessId(businessId);// ????????????
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
                    off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// ????????????
                    off.setSubjectsId(rel.getSubjectsId());// ??????ID
                    off.setSubjectsName(rel.getSubjectsName());// ????????????
                    off.setBusiTable("cm_etc_info");// ???????????????
                    off.setBusiKey(etcId);// ????????????ID
                    off.setTenantId(tenantId);
                    off.setBackIncome(0l);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// ????????????:???in???out???io
                    this.createOrderFundFlow(inParam, off, user);
                    // ?????????????????????
                    List<OperDataParam> odps = new ArrayList();
                    if(olTemp.getNoPayEtc() >= (rel.getAmountFee() - rel.getIncome())){
                        odps.add(new OperDataParam("ORDER_ETC", String.valueOf(rel.getAmountFee() - rel.getIncome()), "+"));
                        odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));
                        odps.add(new OperDataParam("NO_PAY_ETC", String.valueOf(rel.getAmountFee() - rel.getIncome()), "-"));
                    }else{
                        odps.add(new OperDataParam("ORDER_ETC", String.valueOf(rel.getAmountFee() - rel.getIncome()), "+"));
                        odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));
                        odps.add(new OperDataParam("NO_PAY_ETC", String.valueOf(olTemp.getNoPayEtc()), "-"));
                        odps.add(new OperDataParam("PAID_ETC", String.valueOf(rel.getAmountFee() - rel.getIncome() - olTemp.getNoPayEtc()), "+"));
                    }
                    orderFundFlowService.saveOrUpdate(off);
                    this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                }
            }
        }
        return null;
    }


    /**
     * ???????????????????????????
     */
    public void payForDebt(Long fianlOrderId, Long debtOrderId, Long amount, Long accountDatailsId, ParametersNewDto inParam, LoginInfo user){
        List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(EnumConsts.PayInter.PAY_FINAL, EnumConsts.SubjectIds.DEDUCTION_ARREARS);
        Long tenantId = inParam.getTenantId();
        for(BusiSubjectsDtl bsd : bsds){
            //??????????????????
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(amount);//???????????????????????????
            off.setBusinessId(EnumConsts.PayInter.PAY_FINAL);//????????????
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(EnumConsts.PayInter.PAY_FINAL)).getCodeName());//????????????
            off.setBookType(bsd.getBooType());//????????????
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(bsd.getBooType())).getCodeName());//????????????
            off.setSubjectsId(bsd.getSubjectsId());//??????ID
            off.setSubjectsName("");//????????????
            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
            off.setBusiKey(accountDatailsId);//????????????ID
            off.setInoutSts(bsd.getInoutSts());//????????????:???in???out???io
            off.setTenantId(tenantId);
            this.createOrderFundFlow(inParam,off, user);
            if(bsd.getDtlBusinessId()==11000010L){
                off.setSubjectsName("????????????");//????????????
                off.setOrderId(fianlOrderId);//??????ID
                //?????????????????????
                List<OperDataParam> odps = new ArrayList();
                odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"-"));
                odps.add(new OperDataParam("PAID_FINAL_PAY",String.valueOf(amount),"+"));
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(fianlOrderId , odps);
            }
            if(bsd.getDtlBusinessId()==11000011L){
                off.setSubjectsName("??????");//????????????
                off.setOrderId(debtOrderId);//??????ID
                //?????????????????????
                List<OperDataParam> odps = new ArrayList();
                odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"+"));
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(debtOrderId , odps);
            }
            if(bsd.getDtlBusinessId()==11000012L){
                off.setSubjectsName("??????");//????????????
                off.setOrderId(debtOrderId);//??????ID
                //?????????????????????
                List<OperDataParam> odps = new ArrayList();
                odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"-"));
                odps.add(new OperDataParam("PAID_DEBT",String.valueOf(amount),"+"));
                odps.add(new OperDataParam("NO_PAY_DEBT",String.valueOf(amount),"-"));
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(debtOrderId , odps);
            }
        }
    }

    private OrderFundFlow createOrderFundFlow(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user){
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

    /*?????????????????????????????????userId???????????????????????????????????????*/
    public int updateOrderLimit(long orderId, long userId, List<OperDataParam> odps){
        StringBuffer cond = new StringBuffer();
        Map setParam = new HashMap();
        Set keysSet = new HashSet();
        OperDataParam odp = null;
        for(int i=0;i<odps.size();i++){
            odp = odps.get(i);
            if(odp.getOperate()==null||"".equals(odp.getOperate())){
                if(odp.getDataType()!=null && !"".equals(odp.getDataType())){
                    if("Date".equals(odp.getDataType())){
                        cond.append(odp.getColumnName() + " = str_to_date(" + "'" + odp.getColumnValue() + "','%Y%m%d%H%i%s'),");
                    }
                }
                else
                {
                    cond.append(odp.getColumnName() + " = " + " :" + odp.getColumnName() + ",");
                    setParam.put(odp.getColumnName(), odp.getColumnValue());
                }
            }
            else
            {
                if(keysSet.add(odp.getColumnName())){
                    cond.append(odp.getColumnName() + " = IFNULL("+ odp.getColumnName() + ",0) " + odp.getOperate() +" :" + odp.getColumnName() + ",");
                    setParam.put(odp.getColumnName(), odp.getColumnValue());
                }
                else
                {
                    cond.append(odp.getColumnName() + " = IFNULL("+ odp.getColumnName() + ",0) " + odp.getOperate() +" :" + odp.getColumnName( ) + i + ",");
                    setParam.put(odp.getColumnName() + i, odp.getColumnValue());
                }
            }
        }
        String strcond = cond.substring(0, cond.length() - 1);
        int ret = orderLimitMapper.updateOrderLimit(orderId, userId, strcond);
        return ret;
    }

    /*????????????????????????*/
    public int updateOrderLimit(long orderId, List<OperDataParam> odps){
        StringBuffer cond = new StringBuffer();
        Map setParam = new HashMap();
        Set keysSet = new HashSet();
        OperDataParam odp = null;
        for(int i=0;i<odps.size();i++){
            odp = odps.get(i);
            if(odp.getOperate()==null||"".equals(odp.getOperate())){
                if(odp.getDataType()!=null && !"".equals(odp.getDataType())){
                    if("Date".equals(odp.getDataType())){
                        cond.append(odp.getColumnName() + " = str_to_date(" + "'" + odp.getColumnValue() + "','%Y%m%d%H%i%s'),");
                    }
                }
                else
                {
                    cond.append(odp.getColumnName() + " = " + " :" + odp.getColumnName() + ",");
                    setParam.put(odp.getColumnName(), odp.getColumnValue());
                }
            }
            else
            {
                if(keysSet.add(odp.getColumnName())){
                    cond.append(odp.getColumnName() + " = IFNULL("+ odp.getColumnName() + ",0) " + odp.getOperate() +" :" + odp.getColumnName() + ",");
                    setParam.put(odp.getColumnName(), odp.getColumnValue());
                }
                else
                {
                    cond.append(odp.getColumnName() + " = IFNULL("+ odp.getColumnName() + ",0) " + odp.getOperate() +" :" + odp.getColumnName( ) + i + ",");
                    setParam.put(odp.getColumnName() + i, odp.getColumnValue());
                }
            }
        }
        String strcond = cond.substring(0, cond.length() - 1);
        StringBuffer hql = new StringBuffer(" update order_limit set " + strcond + " where order_id = " + orderId);
        int ret = orderLimitMapper.updateOrderLimitUser(orderId, strcond);
        return ret;
    }

    /*???????????????????????????????????????????????????????????????????????????*/
    public List<OperDataParam> getOpateDateParam(BusiSubjectsDtl bsd,OrderFundFlow off) {
        List<OperDataParam> odps = new ArrayList();
        List<BusiSubjectsDtlOperate> bsdos = busiSubjectsDtlOperateMapper.queryBusiSubjectsDtlOperate(bsd.getId());
        for(BusiSubjectsDtlOperate bsdo:bsdos){
            try {
                OperDataParam odp = this.getOperDataParam(bsdo, off);
                odps.add(odp);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("????????????");
            }
        }
        return odps;
    }

    public OperDataParam getOperDataParam(BusiSubjectsDtlOperate bdo,OrderFundFlow off) throws Exception{
        Method method = off.getClass().getDeclaredMethod("get" + bdo.getFlowColum());
        String value = String.valueOf(method.invoke(off));
        return new OperDataParam(bdo.getColumName(),value,bdo.getOperate());
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
}
