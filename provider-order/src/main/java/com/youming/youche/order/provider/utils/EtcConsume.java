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
            throw new BusinessException("Etc费用明细不能为空!");
        }
        // 资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if (isOwnCar != 1 ) {
                // ETC欠款应收
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_ARREARS_CO_FEE) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(orderId);// 订单ID
                    off.setBusinessId(businessId);// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("cm_etc_info");// 业务对象表
                    off.setBusiKey(etcId);// 业务流水ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                }
                // 查询司机对应资金渠道订单
                OrderLimit olTemp = orderLimitMapper.getOrderLimitByUserIdAndOrderId(orderId,userId, -1);
                if(olTemp==null){
                    continue;
                }
                // Etc费用
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(businessId);// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("cm_etc_info");// 业务对象表
                    off.setBusiKey(etcId);// 业务流水ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    List<OperDataParam> odps = new ArrayList();
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getIncome()), "-"));//订单ETC
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));//订单ETC利润
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getAmount()), "+"));//已付ETC金额(已付)
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getIncome()), "-"));//已付ETC金额(已付)
                    odps.add(new OperDataParam("NO_PAY_ETC", String.valueOf(off.getAmount()), "-"));//应付ETC金额(未付)
                    this.updateOrderLimit(olTemp.getOrderId(),userId,odps);
                }
                // 可用金额
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_AVAILABLE_FEE) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(businessId);// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("cm_etc_info");// 业务对象表
                    off.setBusiKey(etcId);// 业务流水ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    List<OperDataParam> odps = new ArrayList();
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getAmount()), "+"));//订单ETC
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getIncome()), "-"));//订单ETC
                    odps.add(new OperDataParam("account_balance", String.valueOf(off.getAmount()), "-"));//账户可提现金额
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getAmount()), "+"));//已付ETC金额(已付)
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getIncome()), "-"));//已付ETC金额(已付)
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));//订单ETC利润
                    odps.add(new OperDataParam("DEBT_MONEY", String.valueOf(off.getAmount()), "+"));//欠款
                    odps.add(new OperDataParam("PAID_DEBT", String.valueOf(off.getAmount()), "+"));//已付欠款
                    this.updateOrderLimit(olTemp.getOrderId(),userId,odps);
                }
                // ETC欠款应付
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_ARREARS_FEE) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(businessId);// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("cm_etc_info");// 业务对象表
                    off.setBusiKey(etcId);// 业务流水ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    List<OperDataParam> odps = new ArrayList();
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getAmount()), "+"));//订单ETC
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(off.getIncome()), "-"));//订单ETC
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getAmount()), "+"));//已付ETC金额(已付)
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(off.getIncome()), "-"));//已付ETC金额(已付)
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(off.getIncome()), "+"));//订单ETC利润
                    odps.add(new OperDataParam("DEBT_MONEY", String.valueOf(off.getAmount()), "+"));//欠款
                    odps.add(new OperDataParam("NO_PAY_DEBT", String.valueOf(off.getAmount()), "+"));//未付欠款
                    this.updateOrderLimit(olTemp.getOrderId(),userId,odps);
                }
                // 现金费用
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ORDER_CASH_CONSUME_FEE) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(businessId);// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("cm_etc_info");// 业务对象表
                    off.setBusiKey(etcId);// 业务流水ID
                    off.setBackIncome(rel.getBackIncome());
                    off.setTenantId(tenantId);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    List<OperDataParam> odps = new ArrayList();
                    //odps.add(new OperDataParam("PAID_CASH", String.valueOf(rel.getAmountFee()), "+"));//已付预付款现金(已付)
                    odps.add(new OperDataParam("ORDER_CASH", String.valueOf(rel.getAmountFee()), "-"));
                    odps.add(new OperDataParam("NO_PAY_CASH", String.valueOf(rel.getAmountFee()), "-"));//应付预付款现金(未付)
                    odps.add(new OperDataParam("NO_WITHDRAW_CASH", String.valueOf(rel.getAmountFee()), "-"));//现金未提现金额
                    odps.add(new OperDataParam("ETC_INCOME", String.valueOf(rel.getIncome()), "+"));
                    odps.add(new OperDataParam("ORDER_ETC", String.valueOf(rel.getAmountFee()-rel.getIncome()), "+"));//订单ETC
                    odps.add(new OperDataParam("PAID_ETC", String.valueOf(rel.getAmountFee()), "+"));//已付ETC
                    this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                }
                // 未到期费用
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE) {
                    // 查询业务科目明细，将现有业务科目细分
                    List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                    for (BusiSubjectsDtl bsd : bsds) {
                        // 资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setOrderId(olTemp.getOrderId());// 订单ID
                        off.setBusinessId(bsd.getDtlBusinessId());// 业务类型
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                        off.setBookType(bsd.getBooType());// 账户类型
                        off.setBookTypeName(bsd.getRemark());// 账户类型
                        off.setSubjectsId(rel.getSubjectsId());// 科目ID
                        off.setSubjectsName(rel.getSubjectsName());// 科目名称
                        off.setBusiTable("cm_etc_info");// 业务对象表
                        off.setBusiKey(etcId);// 业务流水ID
                        off.setTenantId(tenantId);
                        if(bsd.getBooType()==3001 || bsd.getBooType()==3007){//转现金和ETC的费用需要减去手续费
                            off.setAmount(rel.getAmountFee()-rel.getPoundageFee());// 交易金额（单位分）
                        }else{
                            off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                        }
                        if ((bsd.getHasIncome() != null && bsd.getHasIncome().intValue() == 1)) {
                            off.setIncome(rel.getIncome());
                        }
                        if(bsd.getBooType()==3003){//预支未到期需要手续费
                            off.setIncome(rel.getPoundageFee());
                        }
                        if (bsd.getHasBack() != null && bsd.getHasBack().intValue() == 1) {
                            off.setBackIncome(rel.getBackIncome());
                        }
                        off.setInoutSts(bsd.getInoutSts());// 收支状态:收in支out转io
//                        off.setPoundageFee(rel.getPoundageFee());//未到期手续费 // 这个字段从代码逻辑上来看  是没有用到的
                        this.createOrderFundFlow(inParam, off, user);
                        orderFundFlowService.saveOrUpdate(off);
                        // 订单限制表操作
                        List<OperDataParam> odps = this.getOpateDateParam(bsd,off);
                        this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                    }
                }
                // ETC欠款费用
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ARREARS_ETC_CONSUME_FEE) {
                    // 查询业务科目明细，将现有业务科目细分
                    List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                    for (BusiSubjectsDtl bsd : bsds) {
                        // 资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());// 订单ID
                        off.setBusinessId(bsd.getDtlBusinessId());// 业务类型
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(bsd.getDtlBusinessId())).getCodeName());// 业务名称
                        off.setBookType(bsd.getBooType());// 账户类型
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(bsd.getBooType())).getCodeName());// 账户类型
                        off.setSubjectsId(rel.getSubjectsId());// 科目ID
                        off.setSubjectsName(rel.getSubjectsName());// 科目名称
                        off.setBusiTable("cm_etc_info");// 业务对象表
                        off.setBusiKey(etcId);// 业务流水ID
                        off.setTenantId(tenantId);
                        if (bsd.getHasIncome() != null&& bsd.getHasIncome().intValue() == 1) {
                            off.setIncome(rel.getIncome());
                        }
                        if (bsd.getHasBack() != null&& bsd.getHasBack().intValue() == 1) {
                            off.setBackIncome(rel.getBackIncome());
                        }
                        off.setInoutSts(bsd.getInoutSts());// 收支状态:收in支out转io
                        this.createOrderFundFlow(inParam, off, user);
                        // 订单限制表操作
                        List<OperDataParam> odps = this.getOpateDateParam(bsd,off);
                        orderFundFlowService.saveOrUpdate(off);
                        this.updateOrderLimit(olTemp.getOrderId(),userId, odps);
                    }
                    // 抵扣
                    List<OrderLimit> orderLimitsFinal = orderLimitMapper.getOrderLimit(userId,vehicleAffiliation, OrderAccountConst.NO_PAY.NO_PAY_FINAL,tenantId,-1);
                    //需要其他单抵扣的金额
                    long deductibleAmount = 0L;
                    for(OrderLimit ol : orderLimitsFinal){
                        if (ol.getOrderId() == orderId) {
                            continue;
                        }
                        deductibleAmount += ol.getNoPayFinal();
                    }
                    if(deductibleAmount > rel.getAmountFee()){//其他单足够抵扣本单产生的欠款
                        //抵扣金额
                        this.matchAmountToOrderLimit(olTemp.getNoPayDebt(),0,0,OrderAccountConst.NO_PAY.NO_PAY_FINAL,orderLimitsFinal);
                        // 将提现金额分摊给订单
                        for (OrderLimit olTempFinal : orderLimitsFinal) {
                            if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                            }
                        }
                    }else{
                        if(deductibleAmount > 0){
                            //抵扣金额
                            this.matchAmountToOrderLimit(olTemp.getNoPayDebt(),0,0,OrderAccountConst.NO_PAY.NO_PAY_FINAL,orderLimitsFinal);
                            // 将提现金额分摊给订单
                            for (OrderLimit olTempFinal : orderLimitsFinal) {
                                if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                    this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                                }
                            }
                        }
                        //其他单不扣抵扣导致本单上产生欠款
                    }
                }
            }else if(isOwnCar == 1){
                //自有车处理
                //Etc消费可以对应到订单
                OrderLimit olTemp = orderLimitMapper.getOrderLimitByUserIdAndOrderId(orderId, userId, -1);
                // Etc金额扣减
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(businessId);// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("cm_etc_info");// 业务对象表
                    off.setBusiKey(etcId);// 业务流水ID
                    off.setTenantId(tenantId);
                    off.setBackIncome(0l);
                    off.setIncome(rel.getIncome());
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    this.createOrderFundFlow(inParam, off, user);
                    // 订单限制表操作
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
     * 抵扣欠款流水和处理
     */
    public void payForDebt(Long fianlOrderId, Long debtOrderId, Long amount, Long accountDatailsId, ParametersNewDto inParam, LoginInfo user){
        List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(EnumConsts.PayInter.PAY_FINAL, EnumConsts.SubjectIds.DEDUCTION_ARREARS);
        Long tenantId = inParam.getTenantId();
        for(BusiSubjectsDtl bsd : bsds){
            //资金流水操作
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(amount);//交易金额（单位分）
            off.setBusinessId(EnumConsts.PayInter.PAY_FINAL);//业务类型
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(EnumConsts.PayInter.PAY_FINAL)).getCodeName());//业务名称
            off.setBookType(bsd.getBooType());//账户类型
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(bsd.getBooType())).getCodeName());//账户类型
            off.setSubjectsId(bsd.getSubjectsId());//科目ID
            off.setSubjectsName("");//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setInoutSts(bsd.getInoutSts());//收支状态:收in支out转io
            off.setTenantId(tenantId);
            this.createOrderFundFlow(inParam,off, user);
            if(bsd.getDtlBusinessId()==11000010L){
                off.setSubjectsName("抵扣欠款");//科目名称
                off.setOrderId(fianlOrderId);//订单ID
                //订单限制表操作
                List<OperDataParam> odps = new ArrayList();
                odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"-"));
                odps.add(new OperDataParam("PAID_FINAL_PAY",String.valueOf(amount),"+"));
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(fianlOrderId , odps);
            }
            if(bsd.getDtlBusinessId()==11000011L){
                off.setSubjectsName("欠款");//科目名称
                off.setOrderId(debtOrderId);//订单ID
                //订单限制表操作
                List<OperDataParam> odps = new ArrayList();
                odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"+"));
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(debtOrderId , odps);
            }
            if(bsd.getDtlBusinessId()==11000012L){
                off.setSubjectsName("欠款");//科目名称
                off.setOrderId(debtOrderId);//订单ID
                //订单限制表操作
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

    /*回写订单限制数据（包含userId，因为自有车可以包含副驾）*/
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

    /*回写订单限制数据*/
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

    /*根据资金流向数据和业务科目明细下面操作拼接操作列表*/
    public List<OperDataParam> getOpateDateParam(BusiSubjectsDtl bsd,OrderFundFlow off) {
        List<OperDataParam> odps = new ArrayList();
        List<BusiSubjectsDtlOperate> bsdos = busiSubjectsDtlOperateMapper.queryBusiSubjectsDtlOperate(bsd.getId());
        for(BusiSubjectsDtlOperate bsdo:bsdos){
            try {
                OperDataParam odp = this.getOperDataParam(bsdo, off);
                odps.add(odp);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("转换异常");
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
}
