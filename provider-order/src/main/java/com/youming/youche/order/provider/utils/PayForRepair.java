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
 * @date 2022/6/13 14:22
 */
@Component
public class PayForRepair {

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    IOrderLimitService orderLimitService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        long userId = inParam.getUserId();
        long flowId = inParam.getFlowId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        long accountDatailsId = inParam.getAccountDatailsId();
        long repairId = inParam.getRepairId();
        if (rels == null) {
            throw new BusinessException("支付维修单明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for(BusiSubjectsRel rel:rels){
            //金额为0不进行处理
            if(rel.getAmountFee()==0L){
                continue;
            }
            //可用支付维修费
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.BALANCE_PAY_REPAIR){
                //查询司机对应资金渠道订单
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setNoPayCash("1");
                inParam.setFaceBalanceUnused("1");
                List<OrderLimit> orderLimits = null;
                orderLimits = this.getAgentOrder(inParam);
                if(orderLimits != null && orderLimits.size() >0){
                    orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                    if(orderLimits != null && orderLimits.size() > 0){
                        continue;
                    }else{
                        throw new BusinessException("经济人信息费不能用于支付，只能提现!");
                    }
                }else{
                    orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                }
                this.matchAmountToOrder(rel.getAmountFee(),rel.getIncome(),rel.getBackIncome(), "NoPayCash", orderLimits);
                for(OrderLimit olTemp : orderLimits){
                    if(olTemp.getMatchAmount()!=null && olTemp.getMatchAmount() > 0L){
                        //资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());//订单ID
                        off.setBusinessId(rel.getBusinessId());//业务类型
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//账户类型
                        off.setSubjectsId(rel.getSubjectsId());//科目ID
                        off.setSubjectsName(rel.getSubjectsName());//科目名称
                        off.setBusiTable("USER_REPAIR_MARGIN");//业务对象表
                        off.setBusiKey(flowId);//业务流水ID
                        off.setFaceType(5);//对方类型:1油老板2车老板3经纪人4司机5维修商
                        off.setFaceUserId(rel.getOtherId());//对方用户ID
                        off.setFaceUserName(rel.getOtherName());//对方用户名
                        off.setIncome(olTemp.getMatchIncome());
                        off.setBackIncome(olTemp.getMatchBackIncome());
                        this.createOrderFundFlow(inParam,off, user);
                        off.setInoutSts("out");//收支状态:收in支out转io
                        off.setNoWithdrawOil(off.getCost());//油老板和维修商共用这个字段
                        orderFundFlowService.saveOrUpdate(off);
                        //订单限制表操作
                        olTemp.setPaidRepair((olTemp.getPaidRepair() == null ? 0L : olTemp.getPaidRepair()) + off.getCost());
                        olTemp.setNoPayCash(olTemp.getNoPayCash() - off.getAmount());
                        olTemp.setPaidCash(olTemp.getPaidCash() + off.getAmount());
                        olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() - off.getAmount());
                        if (off.getBackIncome() != null) {
                            olTemp.setNoPayCash(olTemp.getNoPayCash() + off.getBackIncome());
                            olTemp.setPaidCash(olTemp.getPaidCash() - off.getBackIncome());
                            olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + off.getBackIncome());
                        }
                        olTemp.setNoWithdrawRepair((olTemp.getNoWithdrawRepair() == null ? 0L : olTemp.getNoWithdrawRepair()) + off.getCost());
                        if (off.getIncome() != null && off.getIncome() > 0) {//志鸿利润
                            olTemp.setRepairIncome((olTemp.getRepairIncome() == null ? 0L : olTemp.getRepairIncome()) + off.getIncome());
                        }
                        orderLimitService.saveOrUpdate(olTemp);
                    }
                }
            }
            //维修基金(不操作订单，只生成资金流向记录)
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR){
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(repairId);//订单ID
                off.setBusinessId(rel.getBusinessId());//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("USER_REPAIR_MARGIN");//业务对象表
                off.setBusiKey(flowId);//业务流水ID
                off.setFaceType(5);//对方类型:1油老板2车老板3经纪人4司机5维修商
                off.setFaceUserId(rel.getOtherId());//对方用户ID
                off.setFaceUserName(rel.getOtherName());//对方用户名
                off.setIncome(rel.getIncome());
                off.setBackIncome(rel.getBackIncome());
                this.createOrderFundFlow(inParam,off, user);
                off.setInoutSts("out");//收支状态:收in支out转io
                orderFundFlowService.saveOrUpdate(off);
            }
        }
        return null;
    }

    //查询经纪人订单
    public List<OrderLimit> getAgentOrder(ParametersNewDto inParam) {
        String faceBalanceUnused = inParam.getFaceBalanceUnused();
        String faceMarginUnused = inParam.getFaceMarginUnused();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long userId = inParam.getUserId();
        Integer userType = inParam.getUserType();
        List<OrderLimit> list = orderLimitMapper.getAgentOrder(userId, vehicleAffiliation, faceBalanceUnused, faceMarginUnused, userType);
        return list;
    }

    public long matchAmountToOrder(Long amount, Long income, Long backIncome, String fieldName, List<OrderLimit> orderLimits) {
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

    /*产生订单资金流水数据*/
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
