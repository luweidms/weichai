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
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/14 10:45
 */
@Component
public class OilEntity {

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

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long orderId = inParam.getOrderId();
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            // 核销油卡
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_ENTITY_VERIFICATION) {
                // 查询司机对应资金渠道订单
                OrderLimit ol =  orderLimitMapper.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
                if(ol == null ){
                    throw new BusinessException("未传入正确的orderId");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(ol.getOrderId());// 订单ID
                off.setBusinessId(businessId);// 业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("ACCOUNT_DETAILS");// 业务对象表
                off.setBusiKey(accountDatailsId);// 业务流水ID
                off.setBackIncome(0l);
                off.setIncome(0l);
                off.setFaceType(2);// 对方类型:1油老板2车老板3经纪人4司机
                off.setVehicleAffiliation(vehicleAffiliation);
                off.setUserId(userId);
                off.setInoutSts("out");// 收支状态:收in支out转io
                this.createOrderFundFlow(inParam, off, user);
                // 订单限制表操作
                ol.setWithdrawOil(ol.getWithdrawOil() + off.getAmount());
                ol.setPaidOil(ol.getPaidOil() + off.getAmount());
                orderFundFlowService.saveOrUpdate(off);
                orderLimitService.saveOrUpdate(ol);
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
}
