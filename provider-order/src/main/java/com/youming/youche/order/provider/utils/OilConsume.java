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
            throw new BusinessException("??????????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //????????????????????????
        for(BusiSubjectsRel rel:rels){
            //?????????0???????????????
            if(rel.getAmountFee()==0L){
                continue;
            }
            //??????????????????
            if(rel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_OIL_SUB){
                //????????????????????????????????????
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setNoPayOil("1");
                List<OrderLimit> orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                this.matchAmountToOrder(rel.getAmountFee(),rel.getIncome(),rel.getBackIncome(), "NoPayOil", orderLimits);
                //??????????????????????????????
                for(OrderLimit olTemp : orderLimits){
                    if(olTemp.getMatchAmount()!=null && olTemp.getMatchAmount() > 0L){
                        //??????????????????
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(olTemp.getMatchAmount());//???????????????????????????
                        off.setOrderId(olTemp.getOrderId());//??????ID
                        off.setBusinessId(businessId);//????????????
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//????????????
                        off.setBookType(Long.parseLong(rel.getBookType()));//????????????
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//????????????
                        off.setSubjectsId(rel.getSubjectsId());//??????ID
                        off.setSubjectsName(rel.getSubjectsName());//????????????
                        off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                        off.setBusiKey(accountDatailsId);//????????????ID
                        off.setBackIncome(olTemp.getMatchBackIncome());
                        off.setIncome(olTemp.getMatchIncome());
                        off.setFaceType(1);//????????????:1?????????2?????????3?????????4??????
                        off.setFaceUserId(rel.getOtherId());//????????????ID
                        off.setFaceUserName(rel.getOtherName());//???????????????
                        off.setInoutSts("out");//????????????:???in???out???io
                        this.createOrderFundFlow(inParam,off, user);
                        off.setNoWithdrawOil(off.getCost());
                        //?????????????????????
                        List<OperDataParam> odps = new ArrayList();
                        if(off.getBackIncome() <= oilBackLimit && off.getBackIncome() > 0){//??????????????????????????????,?????????????????????,??????????????????
                            odps.add(new OperDataParam("OIL_INCOME",String.valueOf(off.getBackIncome()),"+"));
                            off.setIncome(off.getBackIncome());
                            off.setBackIncome(0L);
                        }else{
                            odps.add(new OperDataParam("OIL_INCOME",String.valueOf(off.getIncome()),"+"));
                        }
                        odps.add(new OperDataParam("ORDER_OIL",String.valueOf(off.getIncome()),"-"));
                        odps.add(new OperDataParam("PAID_OIL",String.valueOf(off.getCost()),"+"));
                        odps.add(new OperDataParam("NO_PAY_OIL",String.valueOf(off.getAmount()),"-"));
                        if(off.getBackIncome() > oilBackLimit){//????????????????????????????????????????????????
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
            //??????????????????
            if(rel.getSubjectsId()==EnumConsts.SubjectIds.BUY_OIL_SUB){
                //????????????????????????????????????
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
                        throw new BusinessException("???????????????????????????????????????????????????!");
                    }
                }else{
                    orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                }
                this.matchAmountToOrder(rel.getAmountFee(),rel.getIncome(),rel.getBackIncome(), "NoPayCash", orderLimits);
                //??????????????????????????????????????????????????????
                List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(businessId, rel.getSubjectsId());
                //??????????????????????????????
                for(OrderLimit olTemp : orderLimits){
                    if(olTemp.getMatchAmount()!=null && olTemp.getMatchAmount() > 0L){
                        for(BusiSubjectsDtl bsd:bsds){
                            //??????????????????
                            OrderFundFlow off = new OrderFundFlow();
                            off.setAmount(olTemp.getMatchAmount());//???????????????????????????
                            off.setOrderId(olTemp.getOrderId());//??????ID
                            off.setBusinessId(bsd.getDtlBusinessId());//????????????
                            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//????????????
                            off.setBookType(bsd.getBooType());//????????????
                            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//????????????
                            off.setSubjectsId(rel.getSubjectsId());//??????ID
                            off.setSubjectsName(rel.getSubjectsName());//????????????
                            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                            off.setBusiKey(accountDatailsId);//????????????ID
                            if(bsd.getHasIncome()!=null && bsd.getHasIncome().intValue()==1){
                                off.setIncome(olTemp.getMatchIncome());
                            }
                            if(bsd.getHasBack()!=null && bsd.getHasBack().intValue()==1){
                                off.setBackIncome(olTemp.getMatchBackIncome());
                            }
                            if(off.getBackIncome() != null && off.getBackIncome() <= oilBackLimit && off.getBackIncome() > 0){//??????????????????????????????,?????????????????????,??????????????????
                                off.setIncome(off.getBackIncome());
                                off.setBackIncome(0L);
                            }
                            this.createOrderFundFlow(inParam,off, user);
                            if(bsd.getDtlBusinessId() == 21000012L){
                                off.setFaceType(1);//????????????:1?????????2?????????3?????????4??????
                                off.setFaceUserId(rel.getOtherId());//????????????ID
                                off.setFaceUserName(rel.getOtherName());//???????????????
                                off.setNoWithdrawOil(off.getCost());
                            }
                            off.setInoutSts(bsd.getInoutSts());//????????????:???in???out???io
                            //?????????????????????
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
            //????????????
            if(rel.getSubjectsId()==EnumConsts.SubjectIds.RECHARGE_CONSUME_OIL){//????????????
                //??????????????????
                OrderFundFlow offnew = new OrderFundFlow();
                offnew.setAmount(rel.getAmountFee());//???????????????????????????
                offnew.setBusinessId(rel.getBusinessId());//????????????
                offnew.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(rel.getBusinessId())).getCodeName());//????????????
                offnew.setBookType(EnumConsts.PayInter.PAY_CASH_CODE);//????????????
                offnew.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.PAY_CASH_CODE)).getCodeName());//????????????
                offnew.setSubjectsId(rel.getSubjectsId());//??????ID
                offnew.setSubjectsName(subjectsInfoMapper.selectById(rel.getSubjectsId()).getSubjectsName());//????????????
                offnew.setBusiTable("ACCOUNT_DETAILS");//???????????????
                offnew.setBusiKey(accountDatailsId);//????????????ID
                offnew.setBackIncome(rel.getBackIncome());
                offnew.setIncome(rel.getIncome());
                offnew.setFaceType(1);//????????????:1?????????2?????????3?????????4??????
                offnew.setFaceUserId(rel.getOtherId());//????????????ID
                offnew.setFaceUserName(rel.getOtherName());//???????????????
                offnew.setInoutSts("out");//????????????:???in???out???io
                this.createOrderFundFlow(inParam,offnew, user);
                offnew.setNoWithdrawOil(offnew.getCost());
                offnew.setVehicleAffiliation("0");//????????????????????????
                orderFundFlowService.saveOrUpdate(offnew);
            }
        }
        return null;
    }

    private long matchAmountToOrder(Long amount, Long income, Long backIncome, String fieldName, List<OrderLimit> orderLimits) {
        //??????????????????
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
                    //????????????=?????????-???????????????
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
            throw new BusinessException("????????????");
        }
        return allAmount;
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

    /*?????????????????????????????????userId???????????????????????????????????????*/
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

    //?????????????????????
    public List<OrderLimit> getAgentOrder(ParametersNewDto inParam) {
        String faceBalanceUnused = inParam.getFaceBalanceUnused();
        String faceMarginUnused = inParam.getFaceMarginUnused();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long userId = inParam.getUserId();
        Integer userType = inParam.getUserType();
        List<OrderLimit> list = orderLimitMapper.getAgentOrder(userId, vehicleAffiliation, faceBalanceUnused, faceMarginUnused, userType);
        return list;
    }

    /*???????????????????????????????????????????????????????????????????????????*/
    public List<OperDataParam> getOpateDateParam(BusiSubjectsDtl bsd,OrderFundFlow off) {
        List<OperDataParam> odps = new ArrayList();
        List<BusiSubjectsDtlOperate> bsdos = busiSubjectsDtlOperateMapper.queryBusiSubjectsDtlOperate(bsd.getId());
        for(BusiSubjectsDtlOperate bsdo:bsdos){
            try {
                OperDataParam odp = this.getOperDataParam(bsdo, off);
                odps.add(odp);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("????????????");
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
