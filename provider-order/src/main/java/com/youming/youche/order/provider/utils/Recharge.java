package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.SubjectsInfoMapper;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/6/13 17:08
 */
@Component
public class Recharge {

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    SubjectsInfoMapper subjectsInfoMapper;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    /**
     * 充值
     * @param inParam
     * @param rels
     * @throws Exception
     */
    public void recharge(ParametersNewDto inParam,
                         List<BusiSubjectsRel> rels,
                         LoginInfo user) {
        Long userId = inParam.getUserId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        if(rels==null){
            throw new BusinessException("充值明细不能为空!");
        }
        BusiSubjectsRel rel = rels.get(0);
        //资金流水操作
        OrderFundFlow offnew = new OrderFundFlow();
        offnew.setAmount(rel.getAmountFee());//交易金额（单位分）
        offnew.setBusinessId(rel.getBusinessId());//业务类型
        offnew.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(rel.getBusinessId())).getCodeName());//业务名称
        offnew.setBookType(EnumConsts.PayInter.PAY_CASH_CODE);//账户类型
        offnew.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.PAY_CASH_CODE)).getCodeName());//账户类型
        offnew.setSubjectsId(rel.getSubjectsId());//科目ID
        offnew.setSubjectsName(subjectsInfoMapper.selectById(rel.getSubjectsId()).getSubjectsName());//科目名称
        offnew.setBusiTable("ACCOUNT_DETAILS");//业务对象表
        offnew.setBusiKey(accountDatailsId);//业务流水ID
        offnew.setBackIncome(0L);
        offnew.setIncome(0L);
        offnew.setInoutSts("in");//收支状态:收in支out转io
        this.createOrderFundFlow(inParam, offnew, user);
        orderFundFlowService.saveOrUpdate(offnew);
    }

    public List dealToOrder(Map<String, String> inParam,
                            List<BusiSubjectsRel> rels) {
        // TODO Auto-generated method stub
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
