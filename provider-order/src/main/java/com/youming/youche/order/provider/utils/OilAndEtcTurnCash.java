package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
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
 * @date 2022/6/13 15:48
 */
@Component
public class OilAndEtcTurnCash {

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

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Long flowId = inParam.getFlowId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        if (rels == null) {
            throw new BusinessException("结余明细不能为空!");
        }
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitUserId(orderId,userId, user.getTenantId(), -1);
        OrderLimit olTemp = orderLimits.get(0);
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //油转现(油支出)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_OIL) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("out");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                // 订单限制表操作
                olTemp.setNoPayOil(olTemp.getNoPayOil() - off.getAmount());
                olTemp.setOrderOil(olTemp.getOrderOil() - off.getIncome());
                olTemp.setOilIncome(olTemp.getOilIncome() + off.getIncome());
                orderLimitService.saveOrUpdate(olTemp);
            }
            //油转现(抵扣借支油)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.TURN_CASH_DEDUCTIBLE_LOAN_OIL) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                olTemp.setPaidOil(olTemp.getPaidOil() + off.getAmount());
                orderLimitService.saveOrUpdate(olTemp);
            }
            //油转现(现金收入)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_CASH) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                // 订单限制表操作
                olTemp.setNoPayCash(olTemp.getNoPayCash() + off.getAmount());
                olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + off.getAmount());
                orderLimitService.saveOrUpdate(olTemp);
            }

            //油转实体油卡(油支出)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("out");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                // 订单限制表操作
                olTemp.setNoPayOil(olTemp.getNoPayOil() - off.getAmount());
                olTemp.setOrderOil(olTemp.getOrderOil() - off.getIncome());
                olTemp.setOilIncome(olTemp.getOilIncome() + off.getIncome());
                orderLimitService.saveOrUpdate(olTemp);
            }
            //油转实体油卡(抵扣借支油)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.TURN_ENTITY_DEDUCTIBLE_LOAN_OIL) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                olTemp.setPaidOil(olTemp.getPaidOil() + off.getAmount());
                orderLimitService.saveOrUpdate(olTemp);
            }
            //油转实体油卡(实体油卡收入)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                // 订单限制表操作
                //olTemp.setOrderOil(olTemp.getOrderOil() + off.getCost());
                olTemp.setOrderEntityOil(olTemp.getOrderEntityOil() + off.getCost());
                orderLimitService.saveOrUpdate(olTemp);
            }


            //ETC转现(ETC支出)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_ETC) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("out");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                // 订单限制表操作
                olTemp.setNoPayEtc(olTemp.getNoPayEtc() - off.getAmount());
                olTemp.setOrderEtc(olTemp.getOrderEtc() - off.getIncome());
                olTemp.setEtcIncome(olTemp.getEtcIncome() + off.getIncome());
                orderLimitService.saveOrUpdate(olTemp);
            }
            //ETC转现(现金收入)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_CASH) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                // 订单限制表操作
                olTemp.setNoPayCash(olTemp.getNoPayCash() + off.getAmount());
                olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + off.getAmount());
                orderLimitService.saveOrUpdate(olTemp);
            }

            //抵扣欠款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_DEDUCTIBLE_MARGIN ||
                    rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_DEDUCTIBLE_MARGIN ||
                    rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_DEDUCTIBLE_MARGIN) {
                // 资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("turn_cash_log");// 业务对象表
                off.setBusiKey(flowId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + off.getAmount());
                orderLimitService.saveOrUpdate(olTemp);

                // 抵扣
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setHasDebt("1");
                List<OrderLimit> orderLimitsFinal = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                //欠款金额
                this.matchAmountToOrder(rel.getAmountFee(),0L,0L, "NoPayDebt",orderLimitsFinal);
                // 将提现金额分摊给订单
                for (OrderLimit olTempFinal : orderLimitsFinal) {
                    if (olTempFinal.getMatchAmount() == null || olTempFinal.getMatchAmount() == 0L) {
                        continue;
                    }
                    this.payForDebt(off.getOrderId(),olTempFinal, rel, flowId, inParam, user);
                }
            }

        }

        return null;
    }

    /**
     * 抵扣欠款流水处理
     */
    private void payForDebt(long orderId, OrderLimit ol, BusiSubjectsRel rel, Long flowId, ParametersNewDto inParam, LoginInfo user){
        //资金流水操作
        OrderFundFlow off = new OrderFundFlow();
        off.setAmount(ol.getMatchAmount());//交易金额（单位分）
        off.setOrderId(ol.getOrderId());// 订单ID
        off.setBusinessId(rel.getBusinessId());//业务类型
        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//业务名称
        off.setBookType(0L);//账户类型
        off.setBookTypeName("欠款");//账户类型
        off.setSubjectsId(rel.getSubjectsId());//科目ID
        off.setSubjectsName("欠款");//科目名称
        off.setBusiTable("turn_cash_log");//业务对象表
        off.setBusiKey(flowId);//业务流水ID
        off.setInoutSts("out");//收支状态:收in支out转io
        this.createOrderFundFlow(inParam,off, user);
        orderFundFlowService.saveOrUpdate(off);
        ol.setNoPayDebt(ol.getNoPayDebt() - ol.getMatchAmount());
        ol.setPaidDebt(ol.getPaidDebt() + ol.getMatchAmount());
        orderLimitService.saveOrUpdate(ol);
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
}
