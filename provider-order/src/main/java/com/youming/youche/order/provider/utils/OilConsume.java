package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
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
import com.youming.youche.order.provider.mapper.order.SubjectsInfoMapper;
import com.youming.youche.order.vo.OperDataParam;
import com.youming.youche.order.vo.QueryOrderLimitByCndVo;
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
 * @date 2022/6/13 11:28
 */
@Component
public class OilConsume {

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    BusiSubjectsDtlMapper busiSubjectsDtlMapper;

    @Resource
    BusiSubjectsDtlOperateMapper busiSubjectsDtlOperateMapper;

    @Resource
    SubjectsInfoMapper subjectsInfoMapper;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long oilBackLimit = Long.parseLong(sysCfgRedisUtils.getSysCfg("BACK_INCOME_OIL_LIMIT", user.getTenantId()).getCfgValue());
        if(rels==null){
            throw new BusinessException("购油费用明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for(BusiSubjectsRel rel:rels){
            //金额为0不进行处理
            if(rel.getAmountFee()==0L){
                continue;
            }
            //消费预付油卡
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_OIL_SUB){
                //查询司机对应资金渠道订单
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setNoPayOil("1");
                List<OrderLimit> orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                this.matchAmountToOrder(rel.getAmountFee(),rel.getIncome(),rel.getBackIncome(), "NoPayOil", orderLimits);
                //将油费金额分摊给订单
                for(OrderLimit olTemp : orderLimits){
                    if(olTemp.getMatchAmount()!=null && olTemp.getMatchAmount() > 0L){
                        //资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());//订单ID
                        off.setBusinessId(businessId);//业务类型
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                        off.setSubjectsId(rel.getSubjectsId());//科目ID
                        off.setSubjectsName(rel.getSubjectsName());//科目名称
                        off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                        off.setBusiKey(accountDatailsId);//业务流水ID
                        off.setBackIncome(olTemp.getMatchBackIncome());
                        off.setIncome(olTemp.getMatchIncome());
                        off.setFaceType(1);//对方类型:1油老板2车老板3经纪人4司机
                        off.setFaceUserId(rel.getOtherId());//对方用户ID
                        off.setFaceUserName(rel.getOtherName());//对方用户名
                        off.setInoutSts("out");//收支状态:收in支out转io
                        this.createOrderFundFlow(inParam,off, user);
                        off.setNoWithdrawOil(off.getCost());
                        //订单限制表操作
                        List<OperDataParam> odps = new ArrayList();
                        if(off.getBackIncome() <= oilBackLimit && off.getBackIncome() > 0){//返现金额小于某个阈值,就不返现给司机,当作志鸿利润
                            odps.add(new OperDataParam("OIL_INCOME",String.valueOf(off.getBackIncome()),"+"));
                            off.setIncome(off.getBackIncome());
                            off.setBackIncome(0L);
                        }else{
                            odps.add(new OperDataParam("OIL_INCOME",String.valueOf(off.getIncome()),"+"));
                        }
                        odps.add(new OperDataParam("ORDER_OIL",String.valueOf(off.getIncome()),"-"));
                        odps.add(new OperDataParam("PAID_OIL",String.valueOf(off.getCost()),"+"));
                        odps.add(new OperDataParam("NO_PAY_OIL",String.valueOf(off.getAmount()),"-"));
                        if(off.getBackIncome() > oilBackLimit){//返现金额小于某个阈值，就不返现了
                            odps.add(new OperDataParam("NO_PAY_OIL",String.valueOf(off.getBackIncome()),"+"));
                        }
                        odps.add(new OperDataParam("NO_WITHDRAW_OIL",String.valueOf(off.getCost()),"+"));
                        orderFundFlowService.saveOrUpdate(off);
                        this.updateOrderLimit(olTemp.getOrderId(), userId, odps);
                    }
                    olTemp.setMatchAmount(0L);
                    olTemp.setMatchIncome(0L);
                    olTemp.setMatchBackIncome(0L);
                }
            }
            //消费购买油卡
            if(rel.getSubjectsId()==EnumConsts.SubjectIds.BUY_OIL_SUB){
                //查询司机对应资金渠道订单
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setNoPayOil("1");

                inParam.setFaceBalanceUnused("1");
                List<OrderLimit> orderLimits = null;
                orderLimits = this.getAgentOrder(inParam);
                if(orderLimits != null && orderLimits.size() >0){
                    orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                    if(orderLimits != null && orderLimits.size() > 0){
                        continue;
                    }else{
                        throw new BusinessException("经济人信息费不能用于加油，只能提现!");
                    }
                }else{
                    orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                }
                this.matchAmountToOrder(rel.getAmountFee(),rel.getIncome(),rel.getBackIncome(), "NoPayCash", orderLimits);
                //查询业务科目明细，将现有业务科目细分
                List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                //将油费金额分摊给订单
                for(OrderLimit olTemp : orderLimits){
                    if(olTemp.getMatchAmount()!=null && olTemp.getMatchAmount() > 0L){
                        for(BusiSubjectsDtl bsd:bsds){
                            //资金流水操作
                            OrderFundFlow off = new OrderFundFlow();
                            off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                            off.setOrderId(olTemp.getOrderId());//订单ID
                            off.setBusinessId(bsd.getDtlBusinessId());//业务类型
                            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                            off.setBookType(bsd.getBooType());//账户类型
                            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//账户类型
                            off.setSubjectsId(rel.getSubjectsId());//科目ID
                            off.setSubjectsName(rel.getSubjectsName());//科目名称
                            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                            off.setBusiKey(accountDatailsId);//业务流水ID
                            if(bsd.getHasIncome()!=null && bsd.getHasIncome().intValue()==1){
                                off.setIncome(olTemp.getMatchIncome());
                            }
                            if(bsd.getHasBack()!=null && bsd.getHasBack().intValue()==1){
                                off.setBackIncome(olTemp.getMatchBackIncome());
                            }
                            if(off.getBackIncome() != null && off.getBackIncome() <= oilBackLimit && off.getBackIncome() > 0){//返现金额小于某个阈值,就不返现给司机,当作志鸿利润
                                off.setIncome(off.getBackIncome());
                                off.setBackIncome(0L);
                            }
                            this.createOrderFundFlow(inParam,off, user);
                            if(bsd.getDtlBusinessId() == 21000012L){
                                off.setFaceType(1);//对方类型:1油老板2车老板3经纪人4司机
                                off.setFaceUserId(rel.getOtherId());//对方用户ID
                                off.setFaceUserName(rel.getOtherName());//对方用户名
                                off.setNoWithdrawOil(off.getCost());
                            }
                            off.setInoutSts(bsd.getInoutSts());//收支状态:收in支out转io
                            //订单限制表操作
                            List<OperDataParam> odps = this.getOpateDateParam(bsd, off);
                            orderFundFlowService.saveOrUpdate(off);
                            this.updateOrderLimit(olTemp.getOrderId(), userId, odps);
                        }
                    }
                    olTemp.setMatchAmount(0L);
                    olTemp.setMatchIncome(0L);
                    olTemp.setMatchBackIncome(0L);
                }
            }
            //充值购油
            if(rel.getSubjectsId()==EnumConsts.SubjectIds.RECHARGE_CONSUME_OIL){//充值账户
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
                offnew.setBackIncome(rel.getBackIncome());
                offnew.setIncome(rel.getIncome());
                offnew.setFaceType(1);//对方类型:1油老板2车老板3经纪人4司机
                offnew.setFaceUserId(rel.getOtherId());//对方用户ID
                offnew.setFaceUserName(rel.getOtherName());//对方用户名
                offnew.setInoutSts("out");//收支状态:收in支out转io
                this.createOrderFundFlow(inParam,offnew, user);
                offnew.setNoWithdrawOil(offnew.getCost());
                offnew.setVehicleAffiliation("0");//充值账户资金渠道
                orderFundFlowService.saveOrUpdate(offnew);
            }
        }
        return null;
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
}
