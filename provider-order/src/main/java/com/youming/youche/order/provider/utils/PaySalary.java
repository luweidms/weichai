package com.youming.youche.order.provider.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.OrderInfoSalaryDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/6/14 11:04
 */
@Component
public class PaySalary {

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
    OrderInfoMapper orderInfoMapper;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        Long orderId = inParam.getOrderId();
        Long tenantId = inParam.getTenantId();
        Long userId = inParam.getUserId();
        Integer isDebt = inParam.getIsDebt();
        //查询订单限制数据
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitUserId(orderId, userId, user.getTenantId(), -1);
        if(orderLimits==null || orderLimits.size()<=0){
            throw new BusinessException("订单信息不存在!");
        }
        if(rels==null){
            throw new BusinessException("预付明细不能为空!");
        }
        OrderLimit limit = orderLimits.get(0);
        //处理业务费用明细
        for(BusiSubjectsRel rel:rels){
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
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            this.createOrderFundFlow(inParam,off, user);
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_SUB_DEDUCTION) {//补贴
                Long  amountFee = Math.abs(rel.getAmountFee());
                if (rel.getAmountFee() > 0) {
                    limit.setOrderCash((limit.getOrderCash() == null ? 0 : limit.getOrderCash()) + amountFee);
                    limit.setNoPayCash((limit.getNoPayCash() == null ? 0 : limit.getNoPayCash() ) + amountFee);
                    limit.setNoWithdrawCash((limit.getNoWithdrawCash() == null ? 0 : limit.getNoWithdrawCash()) + amountFee);
                    off.setInoutSts("in");//收支状态:收in支out转io
                }else{//小于0
                    limit.setNoPayCash((limit.getNoPayCash() == null ? 0 : limit.getNoPayCash() ) - amountFee);
                    limit.setNoWithdrawCash((limit.getNoWithdrawCash() == null ? 0 : limit.getNoWithdrawCash()) - amountFee);
                    limit.setPaidCash((limit.getPaidCash() == null ? 0 : limit.getPaidCash()) + amountFee);
                    limit.setWithdrawCash((limit.getWithdrawCash() == null ? 0 : limit.getWithdrawCash()) + amountFee);
                    off.setInoutSts("out");//收支状态:收in支out转io
                }
                orderLimitService.saveOrUpdate(limit);
                orderFundFlowService.saveOrUpdate(off);
            }else if(rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_AWARD16){//订单欠款
                off.setInoutSts("out");//收支状态:收in支out转io
                limit.setPaidDebt( limit.getPaidDebt() +(limit.getNoPayDebt() == null ? 0 :  limit.getNoPayDebt()));//欠款转已付欠款
                limit.setNoPayDebt(0L);//未付欠款清零
                orderLimitService.saveOrUpdate(limit);
                orderFundFlowService.saveOrUpdate(off);
            }
        }
        return null;
    }

    /**
     * 自有车司机发放工资匹配订单
     * @param inParam
     * @return
     * @throws Exception
     */
    public List<OrderLimit> matchOrderInfoForPaySalary(ParametersNewDto inParam) {
        long driverId = inParam.getUserId();
        if(driverId < 0){
            throw new BusinessException("司机id不正确");
        }
        long amountFee = inParam.getAmount();
        if(amountFee <= 0){
            throw new BusinessException("实发工资不能为空");
        }
        String salaryMonth = inParam.getSalaryMonth();
        if(StringUtils.isEmpty(salaryMonth)){
            throw new BusinessException("发放月份不能为空");
        }
        //查询当前的所有订单
        List<OrderInfoSalaryDto> list = orderInfoMapper.queryOrderInfoSalary(DateUtil.parseDate(salaryMonth, DateUtil.YEAR_MONTH_FIRSTDAY), DateUtil.addMonth(DateUtil.parseDate(salaryMonth, DateUtil.YEAR_MONTH_FIRSTDAY), 1));
        HashMap<Long, Long> ordersRunTimes = new HashMap<>();
        Long runTimeSum = 0l;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                if (list.get(i) == null) {
                    continue;
                }
            }
            Long orderId = list.get(i).getOrderId();
            Long estRunTime = list.get(i).getEstRunTime() == null ? 0 : list.get(i).getEstRunTime();
            Long relRunTime = list.get(i).getRelRunTime() == null ? 0 : list.get(i).getRelRunTime();
            if(relRunTime <= 0){
                ordersRunTimes.put(orderId, estRunTime);
                runTimeSum += estRunTime;
            }else{
                ordersRunTimes.put(orderId, relRunTime);
                runTimeSum += relRunTime;
            }
        }

        List<OrderLimit> retOrderLimits = new ArrayList<>();
        for(Map.Entry<Long, Long> me : ordersRunTimes.entrySet()){
            Long orderId = me.getKey();
            Long orderRunTime = me.getValue();
            Long orderSalary  = ((orderRunTime / runTimeSum ) * amountFee);
            LambdaQueryWrapper<OrderLimit> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderLimit::getOrderId, orderId);
            List<OrderLimit> orderLimitList = orderLimitMapper.selectList(queryWrapper);
            if(orderLimitList.isEmpty()){
                throw new BusinessException("找不到订单限制表");
            }
            OrderLimit ol = orderLimitList.get(0);
            ol.setMatchAmount(orderSalary);
            retOrderLimits.add(ol);
        }
        return retOrderLimits;
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
