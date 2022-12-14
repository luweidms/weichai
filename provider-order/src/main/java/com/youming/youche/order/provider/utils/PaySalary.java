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
        //????????????????????????
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitUserId(orderId, userId, user.getTenantId(), -1);
        if(orderLimits==null || orderLimits.size()<=0){
            throw new BusinessException("?????????????????????!");
        }
        if(rels==null){
            throw new BusinessException("????????????????????????!");
        }
        OrderLimit limit = orderLimits.get(0);
        //????????????????????????
        for(BusiSubjectsRel rel:rels){
            if(rel.getAmountFee()==0L){
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//???????????????????????????
            off.setOrderId(orderId);//??????ID
            off.setBusinessId(businessId);//????????????
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//????????????
            off.setBookType(Long.parseLong(rel.getBookType()));//????????????
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//????????????
            off.setSubjectsId(rel.getSubjectsId());//??????ID
            off.setSubjectsName(rel.getSubjectsName());//????????????
            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
            off.setBusiKey(accountDatailsId);//????????????ID
            this.createOrderFundFlow(inParam,off, user);
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_SUB_DEDUCTION) {//??????
                Long  amountFee = Math.abs(rel.getAmountFee());
                if (rel.getAmountFee() > 0) {
                    limit.setOrderCash((limit.getOrderCash() == null ? 0 : limit.getOrderCash()) + amountFee);
                    limit.setNoPayCash((limit.getNoPayCash() == null ? 0 : limit.getNoPayCash() ) + amountFee);
                    limit.setNoWithdrawCash((limit.getNoWithdrawCash() == null ? 0 : limit.getNoWithdrawCash()) + amountFee);
                    off.setInoutSts("in");//????????????:???in???out???io
                }else{//??????0
                    limit.setNoPayCash((limit.getNoPayCash() == null ? 0 : limit.getNoPayCash() ) - amountFee);
                    limit.setNoWithdrawCash((limit.getNoWithdrawCash() == null ? 0 : limit.getNoWithdrawCash()) - amountFee);
                    limit.setPaidCash((limit.getPaidCash() == null ? 0 : limit.getPaidCash()) + amountFee);
                    limit.setWithdrawCash((limit.getWithdrawCash() == null ? 0 : limit.getWithdrawCash()) + amountFee);
                    off.setInoutSts("out");//????????????:???in???out???io
                }
                orderLimitService.saveOrUpdate(limit);
                orderFundFlowService.saveOrUpdate(off);
            }else if(rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_AWARD16){//????????????
                off.setInoutSts("out");//????????????:???in???out???io
                limit.setPaidDebt( limit.getPaidDebt() +(limit.getNoPayDebt() == null ? 0 :  limit.getNoPayDebt()));//?????????????????????
                limit.setNoPayDebt(0L);//??????????????????
                orderLimitService.saveOrUpdate(limit);
                orderFundFlowService.saveOrUpdate(off);
            }
        }
        return null;
    }

    /**
     * ???????????????????????????????????????
     * @param inParam
     * @return
     * @throws Exception
     */
    public List<OrderLimit> matchOrderInfoForPaySalary(ParametersNewDto inParam) {
        long driverId = inParam.getUserId();
        if(driverId < 0){
            throw new BusinessException("??????id?????????");
        }
        long amountFee = inParam.getAmount();
        if(amountFee <= 0){
            throw new BusinessException("????????????????????????");
        }
        String salaryMonth = inParam.getSalaryMonth();
        if(StringUtils.isEmpty(salaryMonth)){
            throw new BusinessException("????????????????????????");
        }
        //???????????????????????????
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
                throw new BusinessException("????????????????????????");
            }
            OrderLimit ol = orderLimitList.get(0);
            ol.setMatchAmount(orderSalary);
            retOrderLimits.add(ol);
        }
        return retOrderLimits;
    }

    /*??????????????????????????????*/
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
