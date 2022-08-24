package com.youming.youche.finance.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IZhangPingOrderRecordService;
import com.youming.youche.finance.api.IZhangPingRecordService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.ZhangPingOrderRecord;
import com.youming.youche.finance.domain.ZhangPingRecord;
import com.youming.youche.finance.dto.LimitAndSourceDto;
import com.youming.youche.finance.provider.mapper.ZhangPingRecordMapper;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* <p>
    *  服务实现类
    * </p>
* @author WuHao
* @since 2022-04-13
*/
@DubboService(version = "1.0.0")
public class ZhangPingRecordServiceImpl extends BaseServiceImpl<ZhangPingRecordMapper, ZhangPingRecord> implements IZhangPingRecordService {

    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;
    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;
    @DubboReference(version = "1.0.0")
    IOrderOilSourceService iOrderOilSourceService;
    @Resource
    LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;
    @Resource
    IZhangPingRecordService iZhangPingRecordService;
    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @DubboReference(version = "1.0.0")
    IAccountDetailsService iAccountDetailsService;
    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;
    @DubboReference(version = "1.0.0")
    IOilSourceRecordService iOilSourceRecordService;
    @Resource
    IZhangPingOrderRecordService iZhangPingOrderRecordService;
    @DubboReference(version = "1.0.0")
    IBaseBillInfoService iBaseBillInfoService;
    @DubboReference(version = "1.0.0")
    IOperationOilService iOperationOilService;


    @Transactional
    @Override
    public void saveForceZhangPingNew(Long userId,  String zhangPingType, Integer userType, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if(user == null || user.getTenantId() == null){
            throw new BusinessException("用户未登录！");
        }
        if (userId == null || userId <= 0L) {
            throw new BusinessException("未找到平账用户id");
        }
        if (StringUtils.isEmpty(zhangPingType)) {
            throw new BusinessException("请输入平账类型");
        }
        if (user.getTenantId() == null || user.getTenantId() <= 0) {
            throw new BusinessException("请输入租户id");
        }
        // TODO: 2022/4/13
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "saveForceZhangPing" + userId , 3, 5);
//        if(!isLock){
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        LimitAndSourceDto limitAndSourceDto = new LimitAndSourceDto();
        List<OrderLimit> driverOrderLimit = iOrderLimitService.getOrderLimitZhangPing(userId, user.getTenantId(),userType);
        //查询司机充值油来源
        List<RechargeOilSource> sourceList = iOrderOilSourceService.getRechargeOilSource(userId, user.getTenantId(),userType);

        if (driverOrderLimit != null && driverOrderLimit.size() > 0) {
            for(OrderLimit ol : driverOrderLimit) {
                OrderAccount orderAccount = iOrderAccountService.queryOrderAccount(ol.getUserId(), ol.getVehicleAffiliation(), 0L, ol.getTenantId(),ol.getOilAffiliation(),ol.getUserType());
                limitAndSourceDto.setOrderLimit(ol);
                this.forceZhangPingOrderLimit(userId, orderAccount, zhangPingType, user.getTenantId(), limitAndSourceDto,accessToken);

            }
        }
        if (sourceList != null && sourceList.size() > 0) {
            for(RechargeOilSource ros : sourceList) {
                OrderAccount orderAccount = iOrderAccountService.queryOrderAccount(ros.getUserId(), ros.getVehicleAffiliation(), 0L, ros.getSourceTenantId(),ros.getOilAffiliation(),ros.getUserType());
                limitAndSourceDto.setRechargeOilSource(ros);
                this.forceZhangPingRechargeOilSource(userId, orderAccount, zhangPingType, user.getTenantId(), limitAndSourceDto,accessToken);
            }
        }
        if (driverOrderLimit != null && driverOrderLimit.size() > 0 || driverOrderLimit != null && driverOrderLimit.size() > 0) {
            // 操作日志
            String remark = "强制平账：" + "";
            iSysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.SaveForceZhangPing,
                    userId, SysOperLogConst.OperType.Add,user.getName() + remark,user.getTenantId());
        }
    }

    private void forceZhangPingRechargeOilSource(Long userId, OrderAccount orderAccount, String zhangPingType, Long tenantId, LimitAndSourceDto limitAndSourceDto,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0L) {
            throw new BusinessException("未找到平账用户id");
        }
        if (zhangPingType == null || zhangPingType.isEmpty()) {
            throw new BusinessException("请选择单账户平账还是选择账户平账!");
        }
        if (tenantId <= 0) {
            throw new BusinessException("请输入租户id");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("根据租户id" + tenantId + "没有找到租户的用户id!");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        LoginInfo userInfo = loginUtils.get(accessToken);
        if (orderAccount == null) {
            throw new BusinessException("没有找到资金账户信息!");
        }
        RechargeOilSource ol = limitAndSourceDto.getRechargeOilSource();
        if (ol == null) {
            throw new BusinessException("RechargeOilSource为空");
        }
        String vehicleAffiliation = orderAccount.getVehicleAffiliation();
        long balance = 0;
        //充值油
        Long rechargeOil = orderAccount.getRechargeOilBalance() == null ?0L : orderAccount.getRechargeOilBalance();
        Long marginBalance = orderAccount.getMarginBalance() == null ? 0L : orderAccount.getMarginBalance();
        Long oilBalance = orderAccount.getOilBalance() == null ? 0L : orderAccount.getOilBalance();
        Long etcBalance = orderAccount.getEtcBalance() == null ? 0L : orderAccount.getEtcBalance();
        Long pledgeOilcardFee = (orderAccount.getPledgeOilCardFee() == null ? 0L : orderAccount.getPledgeOilCardFee());

        Long ZhangPingAllAmount =  ol.getNoPayOil() + ol.getNoCreditOil() + ol.getNoRebateOil() ;
        if (ZhangPingAllAmount == 0) {
            return;
        }
        ZhangPingRecord zpr = new ZhangPingRecord();
        Date date = new Date();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        Long soNbr = CommonUtil.createSoNbr();
        if (EnumConsts.ZHANG_PING.ZHANH_PING_TYPE1.equals(zhangPingType)) {//单账户平账逻辑
            zpr.setBatchId(soNbr);
            zpr.setUserId(userId);
            //会员体系改造开始
            zpr.setUserType(orderAccount.getUserType());
            //会员体系改造结束
            zpr.setBeforeBalance(balance);
            zpr.setBeforeMarginBalance(marginBalance);
            zpr.setBeforeOilBalance(oilBalance);
            zpr.setBeforeEtcBalance(etcBalance);
            zpr.setBeforRepairFund(0L);
            zpr.setBeforePledgeOilcardFee(pledgeOilcardFee);
            zpr.setBeforeRechargeOilBalance(rechargeOil);
            zpr.setZhangPingBalance(balance);
            zpr.setZhangPingMarginBalance(0l);
            zpr.setZhangPingOilBalance(0l);
            zpr.setZhangPingEtcBalance(0l);
            zpr.setZhangPingRepairFund(0L);
            zpr.setZhangPingPledgeOilcard(0l);
            zpr.setZhangPingRechargeOilBalance(ZhangPingAllAmount );
            zpr.setAfterBalance(0L);
            zpr.setAfterMarginBalance(marginBalance);
            zpr.setAfterOilBalance(oilBalance);
            zpr.setAfterEtcBalance(etcBalance);
            zpr.setAfrerRepairFund(0L);
            zpr.setAfterPledgeOilcardFee(pledgeOilcardFee );
            zpr.setAfterRechargeOilBalance(rechargeOil - ZhangPingAllAmount);
            zpr.setZhangPingType(Integer.parseInt(zhangPingType));
            zpr.setAccountType(EnumConsts.ZHANG_PING.ZHANH_PING_ACCOUNT_TYPE1);
            zpr.setVehicleAffiliation(vehicleAffiliation);
            zpr.setCreateDate(getDateToLocalDateTime(date));
            zpr.setOpUserId(userInfo.getId());
            zpr.setOpUserName(userInfo.getName());
            zpr.setOpDate(getDateToLocalDateTime(date));
            zpr.setOpRemark("单账户平账");
            zpr.setTenantId(userInfo.getTenantId());
            iZhangPingRecordService.saveOrUpdate(zpr);
            iOperationOilService.saveClearAccountOilRecord(userId, OrderAccountConst.CLEAR_ACCOUNT_OIL.ZHANG_PING_TYPE, OrderAccountConst.CLEAR_ACCOUNT_OIL.ORDER_OIL_SOURCE, ol.getId(), soNbr, orderAccount.getId(), ZhangPingAllAmount, tenantId,accessToken);

            ZhangPingOrderRecord zpor = new ZhangPingOrderRecord();
            zpor.setBatchId(soNbr);
            zpor.setOrderId(ol.getId());
            zpor.setUserId(userId);
            //会员体系改造开始
            zpor.setUserType(orderAccount.getUserType());
            //会员体系改造结束
            zpor.setNoPayCash(0l);
            zpor.setNoWithdrawCash(0l);
            zpor.setNoPayOil(ZhangPingAllAmount);
            zpor.setNoPayEtc(0l);
            zpor.setNoPayDeb(0l);
            zpor.setNoPayFinal(0l);
            zpor.setZhangPingCash(0L);
            zpor.setZhangPingWithdrawCash(0L);
            zpor.setZhangPingOil(ZhangPingAllAmount);
            zpor.setZhangPingEtc(0l);
            zpor.setZhangPingFinal(0l);
            zpor.setNoPayCashAfter(0L);
            zpor.setNoWithdrawCashAfter(0L);
            zpor.setNoPayOilAfter(0L);
            zpor.setNoPayEtcAfter(0L);
            zpor.setNoPayDebAfter(0L);
            zpor.setNoPayFinalAfter(0L);
            zpor.setZhangPingOrderType(Integer.parseInt(EnumConsts.ZHANG_PING.ZHANH_PING_TYPE1));
            zpor.setAccountOrderType(EnumConsts.ZHANG_PING.ZHANH_PING_ACCOUNT_TYPE1);
            zpor.setIsDriverOrder(EnumConsts.ZHANG_PING.RECHARGE_OIL_TYPE3);
            zpor.setVehicleAffiliation(vehicleAffiliation);
            zpor.setCreateDate(getDateToLocalDateTime(date));
            zpor.setRemark("单账户平账");
            zpor.setTenantId(ol.getTenantId());
            iZhangPingOrderRecordService.saveOrUpdate(zpor);


            boolean isInheritOil = false;
            if (ZhangPingAllAmount > 0) {
                if (!ol.getRechargeOrderId().equals(ol.getSourceOrderId())) {
                    isInheritOil = true;
                    List<BusiSubjectsRel> fleetOilList = new ArrayList<BusiSubjectsRel>();
                    OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(tenantUserId, ol.getVehicleAffiliation(),tenantId,ol.getSourceTenantId(),ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                    BusiSubjectsRel fleetOilSubjectsRel =  iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_ZHANG_PING_OIL_BALANCE, ZhangPingAllAmount);
                    fleetOilList.add(fleetOilSubjectsRel);
                    // 计算费用集合
                    List<BusiSubjectsRel> tempSubList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.FORCE_ZHANG_PING, fleetOilList);
                    iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.FORCE_ZHANG_PING,
                            tenantUserId, "", tenantAccount, tempSubList, soNbr, 0l,"", null, tenantId, null, "", null, tenantAccount.getVehicleAffiliation(),user);

                }
            }
            List<BusiSubjectsRel> busiSubjectsRelList = null;
            if ( ZhangPingAllAmount > 0) {
                //回退共享油
                if (ol.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && !isInheritOil) {
                    //原路返回还是回退到转移账户
                    Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                    Long tempNoPayOil = ol.getNoPayOil();
                    Long tempNoRebateOil = ol.getNoRebateOil();
                    Long tempNoCreditOil = ol.getNoCreditOil();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("noPayOil", tempNoPayOil);
                    map.put("noRebateOil", tempNoRebateOil);
                    map.put("noCreditOil", tempNoCreditOil);
                    iOilSourceRecordService.recallOil(userId, String.valueOf(ol.getRechargeOrderId()), tenantUserId, EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT, tenantId, map,recallType,user);
                }
                BusiSubjectsRel oilSubjectsRel =  iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ZHANG_PING_RECHARGE_OIL_OUT, ZhangPingAllAmount);
                busiList.add(oilSubjectsRel);
                orderResponseDto.setRechargeOilSource(ol);
                //计算费用集合
                busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.FORCE_ZHANG_PING, busiList);
                //写入账户明细表并修改账户金额费用
                iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter.FORCE_ZHANG_PING,
                        sysOperator.getUserInfoId(),sysOperator.getName(),
                        orderAccount,busiSubjectsRelList,soNbr,0L,
                        sysOperator.getName(),getDateToLocalDateTime(date),
                        tenantId,"","",orderResponseDto,
                        vehicleAffiliation,user);

                ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(), EnumConsts.PayInter.FORCE_ZHANG_PING, 0L,ZhangPingAllAmount,vehicleAffiliation,"");
                parametersNewDto.setFlowId(zpor.getFlowId());
                parametersNewDto.setBatchId(String.valueOf(soNbr));
                parametersNewDto.setTenantId(tenantId);
                parametersNewDto.setRechargeOilSource(ol);
                parametersNewDto.setZhangPingType(zhangPingType);
                parametersNewDto.setTenantUserId(tenantUserId);
                parametersNewDto.setType("2");
                iOrderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList,user);
            }
        }

    }
    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    private void forceZhangPingOrderLimit(Long userId, OrderAccount orderAccount, String zhangPingType, Long tenantId, LimitAndSourceDto limitAndSourceDto,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0L) {
            throw new BusinessException("未找到平账用户id");
        }
        if (zhangPingType == null || zhangPingType.isEmpty()) {
            throw new BusinessException("请选择单账户平账还是选择账户平账!");
        }
        if (tenantId <= 0) {
            throw new BusinessException("请输入租户id");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("根据租户id" + tenantId + "没有找到租户的用户id!");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }

        if (orderAccount == null) {
            throw new BusinessException("没有找到资金账户信息!");
        }
        OrderLimit ol = limitAndSourceDto.getOrderLimit();
        if (ol == null) {
            throw new BusinessException("orderLimit为空");
        }
        String vehicleAffiliation = orderAccount.getVehicleAffiliation();
        long balance = 0;
        //未到期金额
        Long marginBalance = orderAccount.getMarginBalance() == null ? 0L :orderAccount.getMarginBalance();

        //油卡金额
        Long oilBalance = orderAccount.getOilBalance() == null ? 0L : orderAccount.getOilBalance();
        //etc金额
        Long etcBalance = orderAccount.getEtcBalance() == null ? 0L : orderAccount.getEtcBalance();
        //油卡抵押金额
        long pledgeOilcardFee = (orderAccount.getPledgeOilCardFee() == null ? 0L : orderAccount.getPledgeOilCardFee());
        long ZhangPingAllAmount = ol.getNoPayFinal() + ol.getNoPayOil() + ol.getNoPayEtc()  + ol.getPledgeOilcardFee() ;
        if (ZhangPingAllAmount == 0) {
            return;
        }
        ZhangPingRecord zpr = new ZhangPingRecord();
        Date date = new Date();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        OrderResponseDto orderResponseDto = new OrderResponseDto();

        long soNbr = CommonUtil.createSoNbr();
        if (EnumConsts.ZHANG_PING.ZHANH_PING_TYPE1.equals(zhangPingType)) {//单账户平账逻辑
            zpr.setBatchId(soNbr);
            zpr.setUserId(userId);
            //会员体系改造开始
            zpr.setUserType(orderAccount.getUserType());
            //会员体系改造结束
            zpr.setBeforeBalance(balance);
            zpr.setBeforeMarginBalance(marginBalance);
            zpr.setBeforeOilBalance(oilBalance);
            zpr.setBeforeEtcBalance(etcBalance);
            zpr.setBeforRepairFund(0L);
            zpr.setBeforePledgeOilcardFee(pledgeOilcardFee);
            zpr.setBeforeRechargeOilBalance(0L);
            zpr.setZhangPingBalance(balance);
            zpr.setZhangPingMarginBalance(ol.getNoPayFinal());
            zpr.setZhangPingOilBalance(ol.getNoPayOil());
            zpr.setZhangPingEtcBalance(ol.getNoPayEtc());
            zpr.setZhangPingRepairFund(0L);
            zpr.setZhangPingPledgeOilcard(ol.getPledgeOilcardFee());
            zpr.setZhangPingRechargeOilBalance(0L);
            zpr.setAfterBalance(0L);
            zpr.setAfterMarginBalance(marginBalance - ol.getNoPayFinal());
            zpr.setAfterOilBalance(oilBalance - ol.getNoPayOil());
            zpr.setAfterEtcBalance(etcBalance - ol.getNoPayEtc());
            zpr.setAfrerRepairFund(0L);
            zpr.setAfterPledgeOilcardFee(pledgeOilcardFee - ol.getPledgeOilcardFee());
            zpr.setAfterRechargeOilBalance(0L);
            zpr.setZhangPingType(Integer.parseInt(zhangPingType));
            zpr.setAccountType(EnumConsts.ZHANG_PING.ZHANH_PING_ACCOUNT_TYPE1);
            zpr.setVehicleAffiliation(vehicleAffiliation);
            zpr.setCreateDate(getDateToLocalDateTime(date));
            zpr.setOpUserId(user.getId());
            zpr.setOpUserName(user.getName());
            zpr.setOpDate(getDateToLocalDateTime(date));
            zpr.setOpRemark("单账户平账");
            zpr.setTenantId(user.getTenantId());
            iZhangPingRecordService.saveOrUpdate(zpr);
            if (ol.getNoPayFinal() > 0) {
                BusiSubjectsRel marginSubjectsRel = new BusiSubjectsRel();
                marginSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT);
                marginSubjectsRel.setAmountFee(ol.getNoPayFinal());
                busiList.add(marginSubjectsRel);
            }
            if (ol.getNoPayEtc() > 0) {
                BusiSubjectsRel etcSubjectsRel = new BusiSubjectsRel();
                etcSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT);
                etcSubjectsRel.setAmountFee(ol.getNoPayEtc());
                busiList.add(etcSubjectsRel);
            }
            if (ol.getPledgeOilcardFee() > 0) {
                BusiSubjectsRel pledgeSubjectsRel = new BusiSubjectsRel();
                pledgeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OIL_CARD_PLEDGE_OUT);
                pledgeSubjectsRel.setAmountFee(ol.getPledgeOilcardFee());
                busiList.add(pledgeSubjectsRel);
            }
            ZhangPingOrderRecord zpor = new ZhangPingOrderRecord();
            zpor.setBatchId(soNbr);
            zpor.setOrderId(ol.getOrderId());
            zpor.setUserId(userId);
            //会员体系改造开始
            zpor.setUserType(orderAccount.getUserType());
            //会员体系改造结束
            zpor.setNoPayCash(ol.getNoPayCash());
            zpor.setNoWithdrawCash(ol.getNoWithdrawCash());
            zpor.setNoPayOil(ol.getNoPayOil());
            zpor.setNoPayEtc(ol.getNoPayEtc());
            zpor.setNoPayDeb(ol.getNoPayDebt());
            zpor.setNoPayFinal(ol.getNoPayFinal());
            zpor.setZhangPingCash(0L);
            zpor.setZhangPingWithdrawCash(0L);
            zpor.setZhangPingOil(ol.getNoPayOil());
            zpor.setZhangPingEtc(ol.getNoPayEtc());
            zpor.setZhangPingFinal(ol.getNoPayFinal());
            zpor.setNoPayCashAfter(0L);
            zpor.setNoWithdrawCashAfter(0L);
            zpor.setNoPayOilAfter(0L);
            zpor.setNoPayEtcAfter(0L);
            zpor.setNoPayDebAfter(0L);
            zpor.setNoPayFinalAfter(0L);
            zpor.setZhangPingOrderType(Integer.parseInt(EnumConsts.ZHANG_PING.ZHANH_PING_TYPE1));
            zpor.setAccountOrderType(EnumConsts.ZHANG_PING.ZHANH_PING_ACCOUNT_TYPE1);
            zpor.setIsDriverOrder(EnumConsts.ZHANG_PING.IS_DRIVER_ORDER_TYPE1);
            zpor.setVehicleAffiliation(vehicleAffiliation);
            zpor.setCreateDate(getDateToLocalDateTime(date));
            zpor.setRemark("单账户平账");
            zpor.setTenantId(ol.getTenantId());
            iZhangPingOrderRecordService.saveOrUpdate(zpor);

            List<BusiSubjectsRel> orderBusiList = new ArrayList<BusiSubjectsRel>();
            List<BusiSubjectsRel> tempDriverSubList = null;
            List<OrderOilSource> sourceList = null;
            OrderOilSource oilSource = null;
            boolean isInheritOil = false;
            if (ol.getNoPayOil() > 0) {
                sourceList = iOrderOilSourceService.getOrderOilSourceByUserIdAndOrderId(ol.getUserId(), ol.getOrderId(),ol.getUserType());
                if (sourceList != null && sourceList.size() > 0) {
                    for (OrderOilSource oos : sourceList) {
                        if (oos.getOrderId().longValue() != oos.getSourceOrderId()) {
                            isInheritOil = true;
                            long oil = (oos.getNoPayOil() + oos.getNoCreditOil() + oos.getNoRebateOil());
                            List<BusiSubjectsRel> fleetOilList = new ArrayList<BusiSubjectsRel>();
                            OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(tenantUserId, oos.getVehicleAffiliation(),oos.getSourceTenantId(),oos.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                            BusiSubjectsRel fleetOilSubjectsRel =  iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_ZHANG_PING_OIL_BALANCE, oil);
                            fleetOilSubjectsRel.setAmountFee(oil);
                            fleetOilList.add(fleetOilSubjectsRel);
                            // 计算费用集合
                            List<BusiSubjectsRel> tempSubList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.FORCE_ZHANG_PING, fleetOilList);
                            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.FORCE_ZHANG_PING,
                                    tenantUserId, "", tenantAccount, tempSubList, soNbr, ol.getSourceOrderId(),"", null, tenantId, null, "", null, tenantAccount.getVehicleAffiliation(),user);

                            //司机真正油资金来源账户扣减
                            OrderAccount driverAccount = iOrderAccountService.queryOrderAccount(userId, oos.getVehicleAffiliation(),oos.getSourceTenantId(),oos.getOilAffiliation(),oos.getUserType());
                            BusiSubjectsRel oilSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT, oil);
                            orderBusiList.add(oilSubjectsRel);
                            tempDriverSubList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.FORCE_ZHANG_PING, orderBusiList);
                            //写入账户明细表并修改账户金额费用
                            orderResponseDto.setOilSource(oos);
                            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter.FORCE_ZHANG_PING,
                                    sysOperator.getUserInfoId(),sysOperator.getOpName(),driverAccount,tempDriverSubList,soNbr,0L,sysOperator.getOpName(),getDateToLocalDateTime(date),tenantId,"","",orderResponseDto,vehicleAffiliation,user);
                        } else {
                            oilSource = oos;
                        }
                    }
                } else {
                    throw new BusinessException("根据订单号：" + ol.getOrderId() + " 用户id：" + ol.getUserId() + " 未找到订单油来源");
                }
            }
            if (!isInheritOil && ol.getNoPayOil() > 0) {
                BusiSubjectsRel oilSubjectsRel =  iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT, ol.getNoPayOil());
                busiList.add(oilSubjectsRel);
                orderResponseDto.setOrderOilSource(sourceList.get(0));
                //回退共享油
                if (oilSource.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && !isInheritOil) {
                    //原路返回还是回退到转移账户
                    Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                    if (oilSource != null && oilSource.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && ol.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        Long oilFee = ol.getMatchAmount() == null ? 0L : ol.getMatchAmount();
                        List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(oilSource.getOrderId());
                        if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                            BaseBillInfo bbi = baseBillInfoList.get(0);
                            if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                                recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                            } else {
                                bbi.setOil(bbi.getOil() - oilFee);
                                bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                                bbi.setUpdateTime(LocalDateTime.now());
                                iBaseBillInfoService.saveOrUpdate(bbi);
                                recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                            }
                        }
                    }
                    Long tempNoPayOil = oilSource.getNoPayOil();
                    Long tempNoRebateOil = oilSource.getNoRebateOil();
                    Long tempNoCreditOil = oilSource.getNoCreditOil();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("noPayOil", tempNoPayOil);
                    map.put("noRebateOil", tempNoRebateOil);
                    map.put("noCreditOil", tempNoCreditOil);
                    iOilSourceRecordService.recallOil(userId, String.valueOf(ol.getOrderId()), tenantUserId, EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT, tenantId, map,recallType,user);
                }
            }
            //计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList = new ArrayList<BusiSubjectsRel>();
            if (busiList != null && busiList.size() > 0) {
                busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.FORCE_ZHANG_PING, busiList);
                //写入账户明细表并修改账户金额费用
                iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter.FORCE_ZHANG_PING,
                        sysOperator.getUserInfoId(),sysOperator.getOpName(),orderAccount,busiSubjectsRelList,soNbr,0L,sysOperator.getOpName(),getDateToLocalDateTime(date),tenantId,"","",orderResponseDto,vehicleAffiliation,user);
            }
            if (isInheritOil) {
                busiSubjectsRelList.addAll(tempDriverSubList);
            }
            ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(), EnumConsts.PayInter.FORCE_ZHANG_PING, ol.getOrderId(),ZhangPingAllAmount,vehicleAffiliation,"");
            parametersNewDto.setOrderLimitBase(ol);
            parametersNewDto.setSourceList(sourceList);
            parametersNewDto.setFlowId(zpor.getFlowId());
            parametersNewDto.setBatchId(String.valueOf(soNbr));//资金流向批次
            parametersNewDto.setTenantId(tenantId);//单账户平账
            parametersNewDto.setZhangPingType(zhangPingType);
            parametersNewDto.setType("1");
            parametersNewDto.setTenantUserId(tenantUserId);
            iOrderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList,user);
        }
    }
}
