package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.vo.OperDataParam;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengwen
 * @date 2022/6/13 17:52
 */
@Component
public class BackRecharge {

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    OrderLimitMapper orderLimitMapper;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long businessId = inParam.getBusinessId();
        Long orderId = inParam.getOrderId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        Long flowId = inParam.getFlowId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        if(rels == null){
            throw new BusinessException("异常补偿费用明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        OrderLimit olTemp = null;
        //查询经济人订单
        List<OrderLimit> faceOrderLimits = orderLimitMapper.getFaceOrderLimit(inParam.getOrderId(), inParam.getUserId(), inParam.getUserType());
        //查询司机订单
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitByOrderId(orderId,-1);
        String sign = "";
        if(faceOrderLimits != null && faceOrderLimits.size() >0){
            olTemp = faceOrderLimits.get(0);
            sign = "2";
        }else if(orderLimits != null && orderLimits.size() >0){
            olTemp = orderLimits.get(0);
            sign = "1";
        }
        //处理业务费用明细
        for(BusiSubjectsRel rel:rels){
            //金额为0不进行处理
            if(rel.getAmountFee() == 0L){
                continue;
            }
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.BACK_RECHARGE){
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("payout_intf");//业务对象表
                off.setBusiKey(flowId);//业务流水ID
                off.setInoutSts("in");//收支状态:收in支out转io
                this.createOrderFundFlow(inParam,off, user);
                //订单限制表操作
                List<OperDataParam> odps = new ArrayList();
                if("1".equals(sign)){//司机
                    odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(off.getAmount()),"+"));
                    odps.add(new OperDataParam("PAID_CASH",String.valueOf(off.getAmount()),"-"));
                }
                if("2".equals(sign)){//经济人
                    odps.add(new OperDataParam("FACE_BALANCE_UNUSED",String.valueOf(off.getAmount()),"+"));
                }
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(olTemp.getOrderId() , odps);
            }
        }
        return null;
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

    /*回写订单限制数据*/
    public int updateOrderLimit(Long orderId, List<OperDataParam> odps){
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
        //session.clear();
        return ret;
    }
}
