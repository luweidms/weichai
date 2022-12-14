package com.youming.youche.order.provider.service;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.PayForFinalChargeIn;
import com.youming.youche.order.domain.PayManager;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.provider.mapper.PayManagerMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

;


/**
* <p>
    *  ???????????????
    * </p>
* @author caoyajie
* @since 2022-04-15
*/
@DubboService(version = "1.0.0")
@Service
    public class PayManagerServiceImpl extends BaseServiceImpl<PayManagerMapper, PayManager> implements IPayManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayManagerServiceImpl.class);

    @Resource
    LoginUtils loginUtils;

    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    IOrderLimitService iOrderLimitService;

    @Resource
    IOrderAccountService iOrderAccountService;

    @Resource
     ICreditRatingRuleService creditRatingRuleService;

    @Lazy
    @Resource
     IPayFeeLimitService payFeeLimitService;
    @Resource
    IBusiSubjectsRelService busiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    IOrderOilSourceService iOrderOilSourceService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @Resource
    private IAccountDetailsService accountDetailsService;

    @Lazy
    @Autowired
    private IBillPlatformService billPlatformService;
    @Lazy
    @Autowired
    private  IBillAgreementService iBillAgreementService;
    @Lazy
    @Resource
    IBaseBusiToOrderService iBaseBusiToOrderService;
    @Resource
    private  IOrderProblemInfoService iOrderProblemInfoService;

    @Resource
    private  IOrderAgingInfoService iOrderAgingInfoService;
    @Lazy
    @Resource
    IPayoutIntfService payoutIntfService;
    @Lazy
    @Resource
     IOrderSchedulerService orderSchedulerService;
    @Lazy
    @Resource
     IOrderSchedulerHService orderSchedulerHService;
    @Override
    public void payForFinalCharge(PayForFinalChargeIn in,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        IOpAccountTF opAccountTF = (OpAccountTF) SysContexts.getBean("opAccountTF");
//        IAccountDetailsTF accountDetailsTF = (AccountDetailsTF) SysContexts.getBean("accountDetailsTF");
//        IBusiSubjectsRelTF busiSubjectsRelTF = (BusiSubjectsRelTF) SysContexts.getBean("busiSubjectsRelTF");
//        ISysOperatorSV operatorSV = (ISysOperatorSV) SysContexts.getBean("sysOperatorSV");
//        IUserSV userSV = (UserSV) SysContexts.getBean("userSV");
//        ITenantSV tenantSV = (TenantSV) SysContexts.getBean("tenantSV");
//        IOrderLimitSV orderLimitSV = (IOrderLimitSV) SysContexts.getBean("orderLimitSV");
//        BaseUser baseUser = SysContexts.getCurrentOperator();
        if (in == null) {
            throw new BusinessException("????????????????????????");
        }
        Long userId = in.getUserId();
        Long orderId = in.getOrderId();
        Long tenantId = in.getTenantId();
        Long finalFee = in.getFinalFee();
        Long insuranceFee = in.getInsuranceFee();
        String vehicleAffiliation = in.getVehicleAffiliation();
        Integer paymentDay = in.getPaymentDay();
        List<OrderProblemInfo> problemInfos = in.getProblemInfos();
        List<OrderAgingInfo> agingInfos = in.getAgingInfos();
        LOGGER.info("??????????????????:userId=" + userId + " finalFee???" + finalFee + " insuranceFee???" + insuranceFee + " informationFee???" + " objId???" + orderId + " vehicleAffiliation???" + vehicleAffiliation + " tenantId???" +tenantId);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("???????????????id");
        }
        if (finalFee == null || finalFee < 0L) {
            throw new BusinessException("???????????????");
        }
        if (insuranceFee == null || insuranceFee < 0L) {
            throw new BusinessException("???????????????");
        }
        if (paymentDay == null ) {
            throw new BusinessException("?????????????????????");
        }
        //  TODO ??????
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payForFinalCharge" + orderId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[] { DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2) });
        map.put("yyyyMM", new String[] { DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2) });
        // ????????????id?????????????????????id
//        Long tenantUserId = tenantSV.getTenantAdminUser(tenantId);
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
//        UserDataInfo tenantUser = userSV.getUserDataInfo(tenantUserId);
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
//        SysOperator tenantSysOperator = operatorSV.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }

        // ??????????????????????????????
//        SysOperator sysOperator = operatorSV.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }

        // ????????????ID???????????????????????????????????????
        // ????????????ID???????????????????????????????????????
//        OrderLimit ol = orderLimitSV.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        OrderLimit ol =iOrderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("??????????????????" + orderId + " ??????ID???" + userId + " ???????????????????????????");
        }
        if (ol.getFianlSts() != null ) {
            throw new BusinessException("????????????" + orderId + "????????????????????????,???????????????");
        }
//        OrderAccount account = opAccountTF.queryOrderAccount(userId, vehicleAffiliation, 0, tenantId,ol.getOilAffiliation(),ol.getUserType());
        OrderAccount account =  iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),ol.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel finalSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.FINAL_CHARGE, finalFee.longValue());
        busiList.add(finalSubjectsRel);
        BusiSubjectsRel insuranceSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.INSURANCE_SUB, insuranceFee.longValue());
        busiList.add(insuranceSubjectsRel);
        BusiSubjectsRel exceptinFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_FINE_FEE, 0l);
        busiList.add(exceptinFeeSubjectsRel);
        BusiSubjectsRel agingFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PRESCRIPTION_FINE, 0l);
        busiList.add(agingFeeSubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FINAL,busiList);
        //???????????????????????????????????????????????????
        long exceptionFeeSum = 0L;
        long agingFeeSum = 0L;
        long tempFinalFee = (finalFee - insuranceFee.longValue());//???????????????????????????????????????????????????
        List<Long> problems = new ArrayList<Long>();
        if (problemInfos != null && problemInfos.size() > 0 && tempFinalFee > 0) {
            for (OrderProblemInfo opi : problemInfos) {
                if (tempFinalFee <= 0) {
                    break;
                }
                if (opi.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                        && opi.getState() == SysStaticDataEnum.EXPENSE_STATE.END && opi.getProblemDealPrice() < 0) {
                    Long problemDealPrice = opi.getProblemDealPrice() == null ? 0 : opi.getProblemDealPrice();
                    Long deductionFee = (opi.getDeductionFee() == null ? 0 : opi.getDeductionFee());
                    Long tempDeductionFee = Math.abs(problemDealPrice) - Math.abs(deductionFee);
                    if (tempDeductionFee <= 0) {
                        continue;
                    }
                    if (tempFinalFee > Math.abs(tempDeductionFee)) {
                        opi.setDeductionFee(deductionFee + (-tempDeductionFee));
                        opi.setFinalDeductionFee((opi.getFinalDeductionFee() == null ? 0L : opi.getFinalDeductionFee()) + (-tempDeductionFee));
//                        orderLimitSV.saveOrUpdate(opi);
                        iOrderProblemInfoService.saveOrUpdate(opi);
                        problems.add(-tempDeductionFee);
                        exceptionFeeSum += (-tempDeductionFee);
                        tempFinalFee -= Math.abs(tempDeductionFee);
                    } else {
                        opi.setDeductionFee(deductionFee + (-tempFinalFee));
                        opi.setFinalDeductionFee((opi.getFinalDeductionFee() == null ? 0L : opi.getFinalDeductionFee()) + (-tempFinalFee));
//                        orderLimitSV.saveOrUpdate(opi);
                        iOrderProblemInfoService.saveOrUpdate(opi);
                        problems.add(-tempFinalFee);
                        exceptionFeeSum += (-tempFinalFee);
                        tempFinalFee = 0L;
                    }
                }
            }
            if (problems.size() > 0) {
                BusiSubjectsRel bus = null;
                Iterator<BusiSubjectsRel> iterator = busiSubjectsRelList.iterator();
                while(iterator.hasNext()){
                    BusiSubjectsRel bsr = iterator.next();
                    if (bsr.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_FINE_FEE) {
                        bus = bsr;
                        iterator.remove();
                        break;
                    }

                }
                for (Long exceptinFee : problems) {
                    if (bus != null) {
                        BusiSubjectsRel exceptinFeeRel = new BusiSubjectsRel();
//                        BeanUtils.copyProperties(exceptinFeeRel, bus);
                        org.springframework.beans.BeanUtils.copyProperties(exceptinFeeRel, bus);
                        exceptinFeeRel.setAmountFee(exceptinFee);
                        busiSubjectsRelList.add(exceptinFeeRel);
                    }
                }
            }
        }
        if (agingInfos != null && agingInfos.size() > 0 && tempFinalFee > 0) {
            for (OrderAgingInfo oai : agingInfos) {
                if (tempFinalFee <= 0) {
                    break;
                }
                if (oai.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                    Long finePrice = oai.getFinePrice() == null ? 0 : oai.getFinePrice();
                    Long deductionFee = (oai.getDeductionFee() == null ? 0 : oai.getDeductionFee());
                    Long tempDeductionFee = Math.abs(finePrice) - Math.abs(deductionFee);
                    if (tempDeductionFee <= 0) {
                        continue;
                    }
                    if (tempFinalFee > Math.abs(tempDeductionFee)) {
                        oai.setDeductionFee(deductionFee + tempDeductionFee);
                        oai.setFinalDeductionFee((oai.getFinalDeductionFee() == null ? 0L : oai.getFinalDeductionFee()) + tempDeductionFee);
//                        orderLimitSV.saveOrUpdate(oai);
                        iOrderAgingInfoService.saveOrUpdate(oai);;
                        agingFeeSum += tempDeductionFee;
                        tempFinalFee -= tempDeductionFee;
                    } else {
                        oai.setDeductionFee(deductionFee + tempFinalFee);
                        oai.setFinalDeductionFee((oai.getFinalDeductionFee() == null ? 0L : oai.getFinalDeductionFee()) + tempFinalFee);
//                        orderLimitSV.saveOrUpdate(oai);
                        iOrderAgingInfoService.saveOrUpdate(oai);
                        agingFeeSum += tempFinalFee;
                        tempFinalFee = 0L;
                    }
                }
            }
            for (BusiSubjectsRel bsr : busiSubjectsRelList) {
                if (bsr.getSubjectsId() == EnumConsts.SubjectIds.PRESCRIPTION_FINE) {
                    bsr.setAmountFee(agingFeeSum);
                    break;
                }
            }
        }

        // ????????????????????????????????????????????????
        long soNbr = CommonUtil.createSoNbr();
//        accountDetailsTF.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_FINAL, tenantUser.getUserId(), tenantSysOperator.getOperatorName(),account, busiSubjectsRelList, soNbr, orderId, null, null, tenantId, null, "", null, vehicleAffiliation);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE,
                EnumConsts.PayInter.PAY_FINAL,
                sysOperator.getUserInfoId(),
                tenantSysOperator.getOpName(),
                account, busiSubjectsRelList,
                soNbr, orderId,
                null, null, tenantId,
                null, "", null, vehicleAffiliation,loginInfo);

        // ????????????????????????
//        ISysOperLogSV logSV = (ISysOperLogSV) SysContexts.getBean("sysOperLogSV");
        String remark = "???????????????" + "????????????" + orderId + " ??????:" + finalFee + "??????:" + insuranceFee + "????????????:" + exceptionFeeSum;
        sysOperLogService.saveSysOperLog(loginInfo,
                SysOperLogConst.BusiCode.PayForFinalCharge,
                21000014L, SysOperLogConst.OperType.Add,loginInfo.getName() + remark);
        // ?????????????????????????????????????????????
//        BusiToOrder busiToOrder = (BusiToOrder) SysContexts.getBean("busiToOrder");
//        Map<String, String> inParam = iOrderAccountService.setParameters(sysOperator.getUserId(), sysOperator.getBillId(),
//                EnumConsts.PayInter.PAY_FINAL, orderId, finalFee, vehicleAffiliation, String.valueOf(paymentDay));
        ParametersNewDto inParamNew = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.PAY_FINAL, orderId, finalFee, vehicleAffiliation, String.valueOf(paymentDay));
//        inParam.put("tenantId", String.valueOf(tenantId));
        inParamNew.setTenantId(tenantId);
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantUser.getLinkman());
        iOrderOilSourceService.busiToOrder(inParamNew, busiSubjectsRelList,loginInfo);
    }

    @Override
    public void updatePayManagerState(Long flowId) {
        LambdaUpdateWrapper<PayManager> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PayManager::getState, 3);
        updateWrapper.eq(PayManager::getId, flowId);
        this.update(updateWrapper);
    }

    @Override
    public Long getAdvanceServiceCharge(long userId, long amountFee, Long tenantId, Integer userType) {
        LOGGER.info("????????????:userId=" + userId + "????????????=" + amountFee );
        if (userId < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (amountFee <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("???????????????????????????");
        }
        //?????????????????????
        // ????????????id??????????????????
        UserDataInfo user = userDataInfoService.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("????????????????????????!");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }

        //??????????????????????????????
        List<OrderAccount> accountList = iOrderAccountService.getOrderAccountQuey(userId, OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, -1L,userType);

        //??????????????????
        long canAdvance = 0;
        //???????????????
        long serviceFee = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0) {
                CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId,ac.getSourceTenantId());

                if (userType!= com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVICE_USER&&rating == null) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
                if (ac.getMarginBalance() > 0) {
                    if (userType!= com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVICE_USER||(rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES)) {
                        canAdvance += ac.getMarginBalance();
                    }
                }
            }
        }
        if (amountFee > canAdvance) {
            throw new BusinessException("????????????????????????????????????????????? " + canAdvance / 100 + "???");
        }
        long tempAmountFee = amountFee;
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == null || ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE0 || ac.getSourceTenantId() <= 0 || ac.getMarginBalance() <= 0) {
                continue;
            }
            CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId,ac.getSourceTenantId());
            if (rating == null) {
                throw new BusinessException("???????????????id??????" + ac.getSourceTenantId()+ " ???????????????????????????");
            }
            if (rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES) {
                if (ac.getMarginBalance() >= tempAmountFee) {
                    serviceFee += new Double(tempAmountFee * rating.getCounterFee()).longValue();
                    break;
                } else {
                    tempAmountFee -= ac.getMarginBalance();
                    serviceFee += new Double(ac.getMarginBalance() * rating.getCounterFee()).longValue();
                }
            }
        }
        return serviceFee;
    }

    @Override
    public void advancePayMarginBalance(Long userId, Long amountFee, Long objId, Long tenantId, Integer userType,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LOGGER.info("????????????:userId=" + userId + "amountFee=" + amountFee + "objId=" + objId);
        if (userId < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (amountFee <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("???????????????????????????");
        }
        // TODO ????????????
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "advancePayMarginBalance" + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        // ????????????id??????????????????
       UserDataInfo user = userDataInfoService.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("????????????????????????!");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        //??????????????????????????????
        List<OrderAccount> accountList = iOrderAccountService.getOrderAccount(userId, OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, -1L,userType);
        //??????????????????
        long canAdvance = 0;
        long serviceCharge = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getSourceTenantId() <= 0 || ac.getMarginBalance() <= 0) {
                continue;
            }
            CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId,ac.getSourceTenantId());
            if (userType!= com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVICE_USER&&rating == null) {
                throw new BusinessException("??????????????????????????????????????????");
            }
            if (ac.getAccState() != null && ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1) {
                if (userType== com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVICE_USER||(rating.getIsAdvance()
                        != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES)) {
                    canAdvance += ac.getMarginBalance();
                    //?????????????????????
                    Float  counterFee = rating.getCounterFee() == null ? 0.0F : rating.getCounterFee();
                    serviceCharge += new Double(ac.getMarginBalance() * counterFee).longValue();
                }
            }
        }
        if (amountFee > canAdvance) {
            throw new BusinessException("????????????????????????????????????????????? " + canAdvance / 100 + "???!" + "?????????????????????" + serviceCharge/100 + "???!");
        }
        long sumTranFee = amountFee;//??????????????????
        long soNbr = CommonUtil.createSoNbr();
        Calendar cd = Calendar.getInstance();
        for (OrderAccount ac : accountList) {
            if(sumTranFee<=0){
                break;
            }
            if (ac.getAccState() == null || ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE0 || ac.getMarginBalance() <= 0) {
                continue;
            }
            CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId,ac.getSourceTenantId());
            if (rating == null) {
                log.error("???????????????????????????"+userId+"????????????"+ac.getSourceTenantId()+"??????????????????????????????");
                continue;
            }
            //??????????????????
            SysTenantDef payTenantDef = iSysTenantDefService.getSysTenantDef(ac.getSourceTenantId());
            if (rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES) {
                boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(ac.getSourceTenantId());
                long sumAccountMargin = ac.getMarginBalance();//???????????????????????????
                if(sumAccountMargin>sumTranFee){//???????????????????????????????????????????????????????????????????????????????????????
                    sumAccountMargin=sumTranFee;
                }
                if(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ac.getVehicleAffiliation())){//????????????
                    this.advancePayMarginNoBill(sysOperator,sumAccountMargin, ac, payTenantDef, rating, ac.getSourceTenantId(), soNbr, objId, cd, isAutoTransfer,userType,accessToken);
                }else{//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    this.advancePayMarginNeedBill(sumAccountMargin, ac, payTenantDef, rating, ac.getSourceTenantId(), soNbr, objId, cd, isAutoTransfer,userType,accessToken);
                }
                sumTranFee-=sumAccountMargin;
            }
        }
    }

    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public void advancePayMarginNoBill(SysUser sysOperator, long accountMarginBalance, OrderAccount ac,
                                       SysTenantDef payTenantDef, CreditRatingRule rating, long tenantId,
                                       long soNbr, long objId, Calendar cd, boolean isAutoTransfer, Integer userType,
                                       String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // ????????????
        BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
        amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.BEFOREPAY_SUB);
        amountFeeSubjectsRel.setAmountFee(accountMarginBalance);
        BusiSubjectsRel amountPayFeeSubjectsRel = new BusiSubjectsRel();
        amountPayFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.POUNDAGE_SUB);
        amountPayFeeSubjectsRel.setAmountFee(0l);
        amountFeeSubjectsRel.setIncome(0L);
        Long ratingAmount = 0L;//??????????????????
        if (rating != null) {
            ratingAmount =new Double(accountMarginBalance * rating.getCounterFee()).longValue();
            amountPayFeeSubjectsRel.setAmountFee(ratingAmount);
            amountFeeSubjectsRel.setIncome(ratingAmount);
        }
        busiList.add(amountFeeSubjectsRel);
        busiList.add(amountPayFeeSubjectsRel);
        //????????????
        BusiSubjectsRel receivableInSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN, accountMarginBalance);
        busiList.add(receivableInSubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, busiList);
        // ????????????????????????????????????????????????
        accountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, sysOperator.getOpUserId(),
                sysOperator.getOpName(), ac, busiSubjectsRelList, soNbr, objId,sysOperator.getOpName(),
                null, ac.getSourceTenantId(), null, "", null, ac.getVehicleAffiliation(),loginInfo);

        //??????????????????
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(payTenantDef.getAdminUser(), ac.getVehicleAffiliation(),
                0L, ac.getSourceTenantId(),ac.getOilAffiliation(),userType);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ADVANCE_PAY_PAYABLE_IN,
                accountMarginBalance-new Double(accountMarginBalance * rating.getCounterFee()).longValue());
        fleetBusiList.add(payableOverdueRel);

        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, fleetBusiList);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE,
                sysOperator.getOpUserId(), sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, objId,
                sysOperator.getOpName(), null, ac.getSourceTenantId(), null, "", null, ac.getVehicleAffiliation(),loginInfo);
        //?????????????????????
        com.youming.youche.order.domain.order.PayoutIntf payOutIntfVirToVir=new PayoutIntf();
        payOutIntfVirToVir.setVehicleAffiliation(ac.getVehicleAffiliation());
        payOutIntfVirToVir.setOilAffiliation(ac.getOilAffiliation());
        payOutIntfVirToVir.setUserId(sysOperator.getOpUserId());
        //????????????????????????
        payOutIntfVirToVir.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        payOutIntfVirToVir.setUserType(userType);
        //????????????????????????
        payOutIntfVirToVir.setObjId(StringUtils.isBlank(sysOperator.getBillId())?null:Long.valueOf(sysOperator.getBillId()));
        payOutIntfVirToVir.setTxnAmt(accountMarginBalance-new Double(accountMarginBalance * rating.getCounterFee()).longValue());//?????????????????????
        payOutIntfVirToVir.setBusiId(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE);//??????
        payOutIntfVirToVir.setObjType(SysStaticDataEnum.OBJ_TYPE.TURN_CASH + "");
        payOutIntfVirToVir.setWaitBillingAmount(accountMarginBalance-new Double(accountMarginBalance * rating.getCounterFee()).longValue());
        payOutIntfVirToVir.setPayObjId(payTenantDef.getAdminUser());
        payOutIntfVirToVir.setCreateDate(com.youming.youche.finance.commons.util.DateUtil.localDateTime(cd.getTime()));
        payOutIntfVirToVir.setTenantId(tenantId);
        payOutIntfVirToVir.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);
        BankBalanceInfo bankBalanceInfo = null;
        //??????????????????????????????
        bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(sysOperator.getOpUserId(), EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT,"",payOutIntfVirToVir.getPayUserType());
        payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.USER);//??????????????????
        payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
        payOutIntfVirToVir.setAccNo(bankBalanceInfo.getAccId());//????????????
        payOutIntfVirToVir.setAccName(bankBalanceInfo.getCustName());
        payOutIntfVirToVir.setSubjectsId(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN);
        payOutIntfVirToVir.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
        payOutIntfVirToVir.setPayTenantId(ac.getSourceTenantId());

        if (payTenantDef==null||payTenantDef.getAdminUser() == null || payTenantDef.getAdminUser() <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        BankBalanceInfo payBankBalanceInfo =null;
        if(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ac.getVehicleAffiliation())){
            payBankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(sysOperator.getOpUserId(), EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT,"",payOutIntfVirToVir.getPayUserType());
        }else{
            payBankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(sysOperator.getOpUserId(), EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT,"",payOutIntfVirToVir.getPayUserType());
        }
        payOutIntfVirToVir.setPayAccNo(payBankBalanceInfo.getAccId());//????????????
        payOutIntfVirToVir.setPayAccName(payBankBalanceInfo.getCustName());
        payOutIntfVirToVir.setRemark("??????");
        //????????????????????????????????????
        if (isAutoTransfer) {
            payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
        } else {
            payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
        }
        payoutIntfService.doSavePayOutIntfVirToVir(payOutIntfVirToVir,accessToken);
        // ?????????????????????????????????????????????
        Map<String, String> inParam = iOrderAccountService.setParameters(sysOperator.getOpUserId(),
                sysOperator.getBillId(),EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, 0L, accountMarginBalance, ac.getVehicleAffiliation(), "");

        inParam.put("tenantId", String.valueOf(ac.getSourceTenantId()));
        inParam.put("flowId", payOutIntfVirToVir.getId()+"");
        inParam.put("userType", userType+"");
        iBaseBusiToOrderService.busiToOrder(inParam,busiSubjectsRelList);
        // ????????????
        String remark = "???????????????????????????????????????0??????????????????" + accountMarginBalance + "????????????" +
                amountFeeSubjectsRel.getIncome() +"?????????????????????????????????" + ac.getTenantId();
        sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.
                SysOperLogConst.BusiCode.AdvancePayMarginBalance, ac.getId(), SysOperLogConst.OperType.Add, sysOperator.getOpName() + remark);
    }

    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public void advancePayMarginNeedBill(Long accountMarginBalance,OrderAccount ac,SysTenantDef payTenantDef,CreditRatingRule rating,Long tenantId,
                                         Long soNbr,Long objId,Calendar cd,Boolean isAutoTransfer,Integer userType,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //????????????????????????????????????
        long sumMarginBalance = accountMarginBalance;//??????????????????????????????
        List<OrderLimit> orderLimits = iOrderLimitService.getOrderLimit(loginInfo.getUserInfoId(),
                ac.getVehicleAffiliation(), OrderAccountConst.NO_PAY.NO_PAY_FINAL, tenantId, userType);
        for (OrderLimit ol : orderLimits) {
            if (sumMarginBalance == 0L) {
                continue;
            }
            long orderMarginBalance = ol.getNoPayFinal();//????????????????????????
            long orderId = ol.getOrderId();
            if (orderMarginBalance > sumMarginBalance) {
                orderMarginBalance = sumMarginBalance;
            }
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            // ????????????
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.BEFOREPAY_SUB);
            amountFeeSubjectsRel.setAmountFee(orderMarginBalance);
            BusiSubjectsRel amountPayFeeSubjectsRel = new BusiSubjectsRel();
            amountPayFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.POUNDAGE_SUB);
            amountPayFeeSubjectsRel.setAmountFee(0l);
            amountFeeSubjectsRel.setIncome(0L);
            Long ratingAmount = 0L;//??????????????????
            if (rating != null) {
                ratingAmount = new Double(orderMarginBalance * rating.getCounterFee()).longValue();
                amountPayFeeSubjectsRel.setAmountFee(ratingAmount);
                amountFeeSubjectsRel.setIncome(ratingAmount);
            }
            busiList.add(amountFeeSubjectsRel);
            busiList.add(amountPayFeeSubjectsRel);
            //????????????
            BusiSubjectsRel receivableInSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN, orderMarginBalance);
            busiList.add(receivableInSubjectsRel);
            // ??????????????????
            List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, busiList);
            // ????????????????????????????????????????????????
            accountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE, EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, loginInfo.getUserInfoId(),
                    loginInfo.getName(), ac, busiSubjectsRelList, soNbr, objId, loginInfo.getName(),
                    null, ac.getSourceTenantId(), null, "", null, ac.getVehicleAffiliation(), loginInfo);
            //???????????? ????????? 20190717 ?????????????????????????????????
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(ac.getVehicleAffiliation()), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(ac.getVehicleAffiliation()), orderMarginBalance - ratingAmount, 0L, 0L, orderMarginBalance - ratingAmount, tenantId, null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            //??????????????????
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(payTenantDef.getAdminUser(), ac.getVehicleAffiliation(), 0L, ac.getSourceTenantId(), ac.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ADVANCE_PAY_PAYABLE_IN, orderMarginBalance - new Double(orderMarginBalance * rating.getCounterFee()).longValue());
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4012, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }

            // ??????????????????
            List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, fleetBusiList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE,
                    loginInfo.getUserInfoId(), loginInfo.getName(), fleetAccount, fleetSubjectsRelList, soNbr, objId,
                    loginInfo.getName(), null, ac.getSourceTenantId(), null, "", null, ac.getVehicleAffiliation(), loginInfo);
            //?????????????????????
            PayoutIntf payOutIntfVirToVir = new PayoutIntf();
            payOutIntfVirToVir.setVehicleAffiliation(ac.getVehicleAffiliation());
            payOutIntfVirToVir.setOilAffiliation(ac.getOilAffiliation());
            payOutIntfVirToVir.setOrderId(orderId);
            payOutIntfVirToVir.setUserId(loginInfo.getUserInfoId());
            //????????????????????????
            payOutIntfVirToVir.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            payOutIntfVirToVir.setUserType(userType);
            //????????????????????????
            payOutIntfVirToVir.setObjId(StringUtils.isBlank(loginInfo.getTelPhone()) ? null : Long.valueOf(loginInfo.getTelPhone()));
            payOutIntfVirToVir.setTxnAmt(orderMarginBalance - new Double(orderMarginBalance * rating.getCounterFee()).longValue());//?????????????????????
            payOutIntfVirToVir.setBusiId(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE);//??????
            payOutIntfVirToVir.setObjType(SysStaticDataEnum.OBJ_TYPE.TURN_CASH + "");
            payOutIntfVirToVir.setWaitBillingAmount(orderMarginBalance - new Double(orderMarginBalance * rating.getCounterFee()).longValue());
            payOutIntfVirToVir.setPayObjId(payTenantDef.getAdminUser());
            payOutIntfVirToVir.setPayTenantId(payTenantDef.getId());
            payOutIntfVirToVir.setCreateDate(com.youming.youche.finance.commons.util.DateUtil.localDateTime(cd.getTime()));
            payOutIntfVirToVir.setTenantId(payTenantDef.getId());
            payOutIntfVirToVir.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);
            payOutIntfVirToVir.setSubjectsId(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN);
            payOutIntfVirToVir.setBusiCode(orderId + "");
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(ol.getOrderId());
            if (orderScheduler != null) {
                payOutIntfVirToVir.setPlateNumber(orderScheduler.getPlateNumber());
            } else {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(ol.getOrderId());
                payOutIntfVirToVir.setPlateNumber(orderSchedulerH.getPlateNumber());
            }
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ac.getVehicleAffiliation())) {
                //HA????????????
                BankBalanceInfo haBankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(
                        Long.valueOf(ac.getVehicleAffiliation()), EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT,
                        "", payOutIntfVirToVir.getUserType());
                payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);//?????????????????????
                payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                payOutIntfVirToVir.setAccNo(haBankBalanceInfo.getAccId());//????????????
                payOutIntfVirToVir.setAccName(haBankBalanceInfo.getCustName());
            } else {//???????????????
                BankBalanceInfo bankBalanceInfo = null;
                //??????????????????????????????
                bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(loginInfo.getUserInfoId(),
                        EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT, "", payOutIntfVirToVir.getUserType());
                payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.USER);//??????????????????
                payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                payOutIntfVirToVir.setAccNo(bankBalanceInfo.getAccId());//????????????
                payOutIntfVirToVir.setAccName(bankBalanceInfo.getCustName());
            }

            if (payTenantDef == null || payTenantDef.getAdminUser() == null || payTenantDef.getAdminUser() <= 0L) {
                throw new BusinessException("???????????????????????????id!");
            }
            BankBalanceInfo payBankBalanceInfo = null;
            payBankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(loginInfo.getUserInfoId(),
                    EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT, "", payOutIntfVirToVir.getPayUserType());
            payOutIntfVirToVir.setPayAccNo(payBankBalanceInfo.getAccId());//????????????
            payOutIntfVirToVir.setPayAccName(payBankBalanceInfo.getCustName());
            payOutIntfVirToVir.setRemark("??????");
            payOutIntfVirToVir.setBillServiceFee(serviceFee);
            //????????????????????????????????????
            if (isAutoTransfer) {
                payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
            } else {
                payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
            }
            payoutIntfService.doSavePayOutIntfVirToVir(payOutIntfVirToVir,accessToken);
            // ?????????????????????????????????????????????
            Map<String, String> inParam = iOrderAccountService.setParameters(loginInfo.getUserInfoId(),
                    loginInfo.getTelPhone(), EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE, 0L,
                    orderMarginBalance, ac.getVehicleAffiliation(), "");
            inParam.put("tenantId", String.valueOf(ac.getSourceTenantId()));
            inParam.put("flowId", payOutIntfVirToVir.getId() + "");
            inParam.put("userType", userType + "");
            iBaseBusiToOrderService.busiToOrder(inParam, busiSubjectsRelList);
            sumMarginBalance -= orderMarginBalance;

            String remark = "??????????????????????????????" + orderId + "???????????????" + ac.getVehicleAffiliation() + "???????????????"
                    + orderMarginBalance + "????????????" + amountFeeSubjectsRel.getIncome() + "?????????????????????????????????" + ac.getTenantId();
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.AdvancePayMarginBalance, ac.getId(),
                    SysOperLogConst.OperType.Add, loginInfo.getName() + remark);
        }
    }
}
