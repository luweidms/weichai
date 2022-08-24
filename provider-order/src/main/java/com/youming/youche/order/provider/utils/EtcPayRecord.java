package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.BusiSubjectsDtlMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.vo.QueryOrderLimitByCndVo;
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
 * @date 2022/6/14 9:09
 */
@Component
public class EtcPayRecord {

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    IOrderLimitService orderLimitService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    BusiSubjectsDtlMapper busiSubjectsDtlMapper;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Long userId = inParam.getUserId();
        Long flowId = inParam.getFlowId();
        if(orderId == 0L){
            throw new BusinessException("订单ID不能为空!");
        }
        //查询订单限制数据
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitUserId(orderId, userId, user.getTenantId(), -1);
        if(orderLimits==null || orderLimits.size()<=0){
            throw new BusinessException("订单信息不存在!");
        }
        if(rels==null){
            throw new BusinessException("业务明细不能为空!");
        }
        OrderLimit ol = orderLimits.get(0);
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        for(BusiSubjectsRel rel:rels){
            //金额为0不进行处理
            if(rel.getAmountFee()==0L){
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("pay_etc_info");
            off.setBusiKey(flowId);
            off.setCost(rel.getAmountFee());
            if(rel.getSubjectsId()== EnumConsts.SubjectIds.ETC_PAY_QIAN_TONG_OUT){
                long dept = 0;
                if(ol.getNoPayEtc() < off.getAmount()){
                    dept = ol.getNoPayEtc()  - off.getAmount();
                }
                if(dept < 0){
//                    抵扣欠款
                    this.finalForDebt(Math.abs(dept),ol,inParam, user);
                    OrderFundFlow offDebt = new OrderFundFlow();
                    offDebt.setAmount(Math.abs(dept));//交易金额（单位分）
                    offDebt.setOrderId(orderId);//订单ID
                    offDebt.setBusinessId(businessId);//业务类型
                    offDebt.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                    offDebt.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                    offDebt.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                    offDebt.setSubjectsId(EnumConsts.SubjectIds.ETC_PAY_QIAN_TONG_DEBT);//科目ID
                    offDebt.setSubjectsName("ETC自动充值消费产生欠款");//科目名称
                    offDebt.setCost(rel.getAmountFee());
                    offDebt.setBusiTable("pay_etc_info");
                    offDebt.setBusiKey(flowId);
                    offDebt.setInoutSts("out");
                    this.createOrderFundFlow(inParam,offDebt, user);
                    orderFundFlowService.saveOrUpdate(offDebt);

                    off.setShouldAmount(dept);//记录抵扣金额
                    ol.setPaidEtc(ol.getPaidEtc() + off.getAmount());
                    ol.setNoPayEtc(0l);
                }else{
                    ol.setPaidEtc(ol.getPaidEtc() + off.getAmount());
                    ol.setNoPayEtc(ol.getNoPayEtc() - off.getAmount());
                }
                off.setInoutSts("out");//收支状态:收in支out转io
            }
            this.createOrderFundFlow(inParam,off, user);
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(ol);
        }
        return null;
    }

    /**
     * 抵扣欠款
     * @param dept  需要抵扣的欠款
     * @param ol  orderLimit
     * @param inParam
     * @throws Exception
     */
    public void finalForDebt(Long dept, OrderLimit ol, ParametersNewDto inParam, LoginInfo user) {
        long userId = inParam.getUserId();
        // 抵扣 现金
        QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
        queryOrderLimitByCndVo.setUserId(userId);
        queryOrderLimitByCndVo.setVehicleAffiliation(ol.getVehicleAffiliation());
        queryOrderLimitByCndVo.setNoPayFinal("1");
        queryOrderLimitByCndVo.setOrderId(ol.getOrderId());
        List<OrderLimit> orderLimitsFinal = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);

        //需要其他单抵扣的金额
        long deductibleAmount = 0L;
        for(OrderLimit orderLimit : orderLimitsFinal){
            deductibleAmount += orderLimit.getNoPayFinal();
        }
        if(deductibleAmount > dept){//其他单足够抵扣本单产生的欠款
            //抵扣金额
            this.matchAmountToOrder(dept,0L,0L, "NoPayFinal",orderLimitsFinal);
            ol.setDebtMoney(ol.getDebtMoney() + dept);
            ol.setNoPayDebt(ol.getNoPayDebt() + dept);
            // 将提现金额分摊给订单
            for (OrderLimit olTempFinal : orderLimitsFinal) {
                if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                    this.payForDebt(olTempFinal, ol, olTempFinal.getMatchAmount(), 999999999L, inParam, user);
                }
            }
        }else{
            if(deductibleAmount > 0){
                //抵扣金额
                this.matchAmountToOrder(deductibleAmount,0L,0L, "NoPayFinal",orderLimitsFinal);
                // 将提现金额分摊给订单
                ol.setDebtMoney(ol.getDebtMoney() + dept);
                ol.setNoPayDebt(ol.getNoPayDebt() + dept);
                for (OrderLimit olTempFinal : orderLimitsFinal) {
                    if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                        this.payForDebt(olTempFinal, ol, olTempFinal.getMatchAmount(), 888888888L, inParam, user);
                    }
                }
            }else{
                ol.setDebtMoney(ol.getDebtMoney() + dept);
                ol.setNoPayDebt(ol.getNoPayDebt() + dept);
            }
        }
    }

    private long matchAmountToOrder(Long amount, Long income, Long backIncome, String fieldName, List<OrderLimit> orderLimits) {
        //保存剩余金额
        long allAmount = amount;
        if(income==null){
            income = 0L;
        }
        if(backIncome==null){
            backIncome = 0L;
        }
        double incomeRatio = ((double)income)/((double)amount);
        double backIncomeRatio = ((double)backIncome)/((double)amount);
        try {
            for (OrderLimit ol : orderLimits) {
                Method method = ol.getClass().getDeclaredMethod("get" + fieldName);
                Long value = (Long) method.invoke(ol);
                if (value == 0L) {
                    continue;
                }
                if (allAmount > value) {
                    ol.setMatchAmount(value);
                    ol.setMatchIncome(new Double(value * incomeRatio).longValue());
                    ol.setMatchBackIncome(new Double(value * backIncomeRatio).longValue());
                    //剩余金额=总金额-分摊掉金额
                    allAmount = allAmount - value;
                    income -= ol.getMatchIncome();
                    backIncome -= ol.getMatchBackIncome();
                } else if (allAmount <= value) {
                    ol.setMatchAmount(allAmount);
                    ol.setMatchIncome(income);
                    ol.setMatchBackIncome(backIncome);
                    allAmount = 0L;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常");
        }
        return allAmount;
    }

    /**
     * 抵扣欠款流水和处理
     */
    public void payForDebt(OrderLimit olTempFinal, OrderLimit debtOrder, Long amount, Long accountDatailsId, ParametersNewDto inParam, LoginInfo user){
        List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(EnumConsts.PayInter.PAY_FINAL, EnumConsts.SubjectIds.DEDUCTION_ARREARS);
        for (BusiSubjectsDtl bsd : bsds) {
            //资金流水操作
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(amount);//交易金额（单位分）
            off.setBusinessId(EnumConsts.PayInter.PAY_FINAL);//业务类型
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(EnumConsts.PayInter.PAY_FINAL)).getCodeName());//业务名称
            off.setBookType(bsd.getBooType());//账户类型
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(bsd.getBooType())).getCodeName());//账户类型
            off.setSubjectsId(bsd.getSubjectsId());//科目ID
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setTenantId(olTempFinal.getTenantId());
            this.createOrderFundFlow(inParam,off, user);
            if (bsd.getDtlBusinessId() == 11000010L) {
                off.setOrderId(olTempFinal.getOrderId());//订单ID
                off.setSubjectsName("抵扣其他单欠款");//科目名称
                off.setBookTypeName("抵扣欠款");
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setSubjectsId(EnumConsts.SubjectIds.DEDUCTION_OTHER_ORDER_DEBT);//科目ID

                //订单限制表操作
                olTempFinal.setNoPayFinal((olTempFinal.getNoPayFinal() == null ? 0 : olTempFinal.getNoPayFinal()) - amount);
                olTempFinal.setPaidFinalPay((olTempFinal.getPaidFinalPay() == null ? 0 : olTempFinal.getPaidFinalPay()) + amount);
                orderFundFlowService.saveOrUpdate(off);
                orderLimitService.saveOrUpdate(olTempFinal);
            }
            if (bsd.getDtlBusinessId() == 11000012L) {
                off.setOrderId(debtOrder.getOrderId());//订单ID
                off.setInoutSts("in");//收支状态:收in支out转io
                off.setSubjectsName("归还欠款");
                off.setBookTypeName("本单欠款");
                //订单限制表操作
                debtOrder.setNoPayDebt((debtOrder.getNoPayDebt() == null ? 0 : debtOrder.getNoPayDebt()) - amount);
                debtOrder.setPaidDebt((debtOrder.getPaidDebt() == null ? 0 : debtOrder.getPaidDebt()) + amount);
                orderFundFlowService.saveOrUpdate(off);
                orderLimitService.saveOrUpdate(debtOrder);
            }
        }
    }

    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlow(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user){
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
