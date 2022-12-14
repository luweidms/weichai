package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IConsumeOilFlowExtService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.UpdateTheOrderInDto;
import com.youming.youche.order.dto.UpdateTheOwnCarOrderInDto;
import com.youming.youche.order.provider.mapper.order.OrderOilSourceMapper;
import com.youming.youche.order.provider.utils.AccountOilAllot;
import com.youming.youche.order.provider.utils.BackRecharge;
import com.youming.youche.order.provider.utils.BeforePay;
import com.youming.youche.order.provider.utils.CancelTheOrder;
import com.youming.youche.order.provider.utils.ClearAccountOil;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ConsumeOilNew;
import com.youming.youche.order.provider.utils.EtcConsume;
import com.youming.youche.order.provider.utils.EtcPayRecord;
import com.youming.youche.order.provider.utils.ExceptionCompensation;
import com.youming.youche.order.provider.utils.ExceptionFeeOut;
import com.youming.youche.order.provider.utils.FinalPay;
import com.youming.youche.order.provider.utils.ForceZhangPing;
import com.youming.youche.order.provider.utils.ForceZhangPingNew;
import com.youming.youche.order.provider.utils.GrantSalary;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.provider.utils.MerchantCar;
import com.youming.youche.order.provider.utils.OaLoanVerification;
import com.youming.youche.order.provider.utils.OilAndEtcTurnCash;
import com.youming.youche.order.provider.utils.OilAndEtcTurnCashNew;
import com.youming.youche.order.provider.utils.OilConsume;
import com.youming.youche.order.provider.utils.OilEntity;
import com.youming.youche.order.provider.utils.OilEtcTurnCash;
import com.youming.youche.order.provider.utils.PayExpenseInfo;
import com.youming.youche.order.provider.utils.PayFixedFee;
import com.youming.youche.order.provider.utils.PayForRepair;
import com.youming.youche.order.provider.utils.PayForRepairNew;
import com.youming.youche.order.provider.utils.PayOaLoan;
import com.youming.youche.order.provider.utils.PaySalary;
import com.youming.youche.order.provider.utils.PayVehicleExpenseInfo;
import com.youming.youche.order.provider.utils.PledgeReleaseOilcard;
import com.youming.youche.order.provider.utils.PrePay;
import com.youming.youche.order.provider.utils.PrePayNew;
import com.youming.youche.order.provider.utils.Recharge;
import com.youming.youche.order.provider.utils.RechargeAccountOilAllot;
import com.youming.youche.order.provider.utils.RepairFundTurnCash;
import com.youming.youche.order.provider.utils.UpdateOrder;
import com.youming.youche.order.provider.utils.UpdateOrderPrice;
import com.youming.youche.order.provider.utils.Withdraw;
import com.youming.youche.order.provider.utils.WithdrawNew;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;
import com.youming.youche.order.vo.QueryDriverOilByOrderIdVo;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class OrderOilSourceServiceImpl extends BaseServiceImpl<OrderOilSourceMapper, OrderOilSource> implements IOrderOilSourceService {
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @Resource
    IOrderAccountService orderAccountService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @Autowired
    private IOrderOilSourceService orderOilSourceService;
    @Autowired
    private IOrderLimitService orderLimitService;
    @Autowired
    private IConsumeOilFlowExtService consumeOilFlowExtService;
    @Autowired
    private IBusiSubjectsRelService busiSubjectsRelService;
    @Autowired
    private IOrderSchedulerService orderSchedulerService;
    @Autowired
    private IOrderSchedulerHService orderSchedulerHService;
    @Autowired
    private IPayFeeLimitService payFeeLimitService;
    @Autowired
    private IPayoutIntfService payoutIntfService;
    @Autowired
    private IAccountDetailsService accountDetailsService;
    @Autowired
    private IBillPlatformService billPlatformService;
    @Autowired
    private IBillAgreementService billAgreementService;
    @Autowired
    private IPayoutOrderService payoutOrderService;
    @Lazy
    @Resource
    private ConsumeOilNew consumeOilNew;
    @Resource
    private PrePayNew prePayNew;
    @Resource
    private OilAndEtcTurnCashNew oilAndEtcTurnCashNew;
    @Resource
    private MatchAmountUtil matchAmountUtil;
    @Resource
    private WithdrawNew withdrawNew;
    @Resource
    private ForceZhangPingNew forceZhangPingNew;
    @Resource
    private  CancelTheOrder cancelTheOrder;
    @Resource
    private PledgeReleaseOilcard pledgeReleaseOilcard;
    @Resource
    private AccountOilAllot accountOilAllot;
    @Resource
    private PayOaLoan payOaLoan;
    @Resource
    private PayExpenseInfo payExpenseInfo;
    @Lazy
    @Resource
    private PayVehicleExpenseInfo payVehicleExpenseInfo;
    @Resource
    private GrantSalary grantSalary;
    @Resource
    private IOrderFundFlowService orderFundFlowService;
    @Resource
    private PayForRepairNew payForRepairNew;
    @Resource
    private UpdateOrder updateOrder;
    @Resource
    private RechargeAccountOilAllot rechargeAccountOilAllot;
    @Resource
    private ClearAccountOil clearAccountOil;
    @Autowired
    private IBaseBillInfoService baseBillInfoService;
    @Autowired
    private IOilSourceRecordService oilSourceRecordService;
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Resource
    private OrderOilSourceMapper orderOilSourceMapper;
    @Resource
    LoginUtils loginUtils;
    @Resource
    private IRechargeOilSourceService rechargeOilSourceService;
    @Resource
    private IOrderInfoExtService orderInfoExtService;
    @Resource
    private IOrderInfoExtHService orderInfoExtHService;
    @Resource
    private BeforePay beforePay;
    @Resource
    private OilConsume oilConsume;
    @Resource
    private PayForRepair payForRepair;
    @Resource
    private FinalPay finalPay;
    @Resource
    private PrePay prePay;
    @Resource
    private OilEtcTurnCash oilEtcTurnCash;
    @Resource
    private OilAndEtcTurnCash oilAndEtcTurnCash;
    @Resource
    private RepairFundTurnCash repairFundTurnCash;
    @Resource
    private ForceZhangPing forceZhangPing;
    @Resource
    private EtcConsume etcConsume;
    @Resource
    private MerchantCar merchantCar;
    @Resource
    private Recharge recharge;
    @Resource
    private Withdraw withdraw;
    @Resource
    private ExceptionCompensation exceptionCompensation;
    @Resource
    private BackRecharge backRecharge;
    @Resource
    private EtcPayRecord etcPayRecord;
    @Resource
    private OilEntity oilEntity;
    @Resource
    private OaLoanVerification oaLoanVerification;
    @Resource
    private PayFixedFee payFixedFee;
    @Resource
    private PaySalary paySalary;
    @Resource
    private UpdateOrderPrice updateOrderPrice;
    @Resource
    private ExceptionFeeOut exceptionFeeOut;

    @Override
    public List<OrderOilSource> getOrderOilSourceByUserId(Long userId, Integer userType) {
        LambdaQueryWrapper<OrderOilSource> lambda=new QueryWrapper<OrderOilSource>().lambda();
        lambda.eq(OrderOilSource::getUserId,userId);
        if(userType != null && userType > 0){
            lambda.eq(OrderOilSource::getUserType, userType);
        }
        lambda.and(wrapper->wrapper.or()
                .gt(OrderOilSource::getNoPayOil,  0L)
                .or()
                .gt(OrderOilSource::getNoCreditOil,  0L)
                .or()
                .gt(OrderOilSource::getNoRebateOil,  0L)
                );
        lambda.orderByAsc(OrderOilSource::getOrderDate);
        return this.list(lambda);
    }

    @Override
    public void updateTheOwnCarOrder(UpdateTheOwnCarOrderInDto inParam, LoginInfo user,String token) {

        Long masterUserId = inParam.getMasterUserId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long originalEntiyOilFee = inParam.getOriginalEntiyOilFee();
        Long updateEntiyOilFee = inParam.getUpdateEntiyOilFee();
        Long originalFictitiousOilFee = inParam.getOriginalFictitiousOilFee();
        Long updateFictitiousOilFee = inParam.getUpdateFictitiousOilFee();
        Long originalBridgeFee = inParam.getOriginalBridgeFee();
        Long updateBridgeFee = inParam.getUpdateBridgeFee();
        Long originalMasterSubsidy = inParam.getOriginalMasterSubsidy();
        Long updateMasterSubsidy = inParam.getUpdateMasterSubsidy();
        Long originalSlaveSubsidy = inParam.getOriginalSlaveSubsidy();
        Long updateSlaveSubsidy = inParam.getUpdateSlaveSubsidy();
        Long slaveUserId = inParam.getSlaveUserId();
        Long orderId = inParam.getOrderId();
        Long tenantId = inParam.getTenantId();
        Integer isNeedBill = inParam.getIsNeedBill();
        int oilUserType = inParam.getOilUserType();
		/*Integer originalOilConsumer = inParam.getOriginalOilConsumer();
		Integer updateOilConsumer = inParam.getUpdateOilConsumer();*/
        Integer originalOilAccountType = inParam.getOriginalOilAccountType();
        Integer updateOilAccountType = inParam.getUpdateOilAccountType();
        Integer originalOilBillType = inParam.getOriginalOilBillType();
        Integer updateOilBillType = inParam.getUpdateOilBillType();
        if (masterUserId == null || masterUserId <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("???????????????id");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("???????????????????????????");
        }
        if (originalEntiyOilFee == null|| updateEntiyOilFee == null || originalEntiyOilFee < 0 || updateEntiyOilFee < 0) {
            throw new BusinessException("????????????????????????");
        }
        if (originalFictitiousOilFee == null|| updateFictitiousOilFee == null || originalFictitiousOilFee < 0 || updateFictitiousOilFee < 0) {
            throw new BusinessException("????????????????????????");
        }
        if (originalBridgeFee == null|| updateBridgeFee == null || originalBridgeFee < 0 || updateBridgeFee < 0) {
            throw new BusinessException("????????????????????????");
        }
        if (originalMasterSubsidy == null|| updateMasterSubsidy == null || originalMasterSubsidy < 0 || updateMasterSubsidy < 0) {
            throw new BusinessException("?????????????????????????????????");
        }
        //todo ??????
     //   boolean isLock = SysContexts.getLock(this.getClass().getName() + "updateTheOwnCarOrder" + orderId + masterUserId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        // ????????????id?????????????????????id
        Long tenantUserId = sysTenantDefService.getSysTenantDef(tenantId).getAdminUser();
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId,null ,0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(masterUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        Boolean isOwnCarUser = true;
        //????????????????????????
        List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSourceByUserIdAndOrderId(masterUserId, orderId,-1);
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(masterUserId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("?????????????????????!");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("??????????????????????????????");
        }
        boolean isUpdateOilAccountType = false;
        if (originalOilAccountType.intValue() != updateOilAccountType.intValue()) {//??????????????????????????????
            isUpdateOilAccountType = true;
            boolean isConsumeOil = this.getOrderIsConsumeOil(orderId);
            if (isConsumeOil) {
                throw new BusinessException("????????????" + orderId + " ???????????????????????????????????????????????????");
            }
        }
        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        OrderAccount account = orderAccountService.queryOrderAccount(masterUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),ol.getUserType());
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //??????????????????

        Boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetList = new ArrayList<BusiSubjectsRel>();
        //???????????????
        if (originalMasterSubsidy  > updateMasterSubsidy ) {
            Long cash = originalMasterSubsidy - updateMasterSubsidy;
            //??????????????????
            BusiSubjectsRel cashRel =  busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW, cash);
            busiList.add(cashRel);
            //??????????????????
            BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_LOW, cash);
            fleetList.add(fleetCashRel);
        } else if (originalMasterSubsidy <  updateMasterSubsidy) {
            Long cash = updateMasterSubsidy - originalMasterSubsidy;
            //??????????????????
            BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP, cash);
            busiList.add(cashRel);
            //??????????????????
            BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_UPP, cash);
            fleetList.add(fleetCashRel);
        }
        //?????????
        this.updateOilAccountType(sourceList, ol, null, busiList,fleetList, isUpdateOilAccountType,isOwnCarUser,inParam,user);
        //?????????
        if (originalEntiyOilFee > updateEntiyOilFee) {
            Long entityOil = originalEntiyOilFee - updateEntiyOilFee;
            BusiSubjectsRel entityRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_LOW, entityOil);
            busiList.add(entityRel);
        } else if (originalEntiyOilFee < updateEntiyOilFee) {
            Long entityOil = updateEntiyOilFee - originalEntiyOilFee;
            BusiSubjectsRel entityRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP, entityOil);
            busiList.add(entityRel);
            BusiSubjectsRel entityOutRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP_OUT, entityOil);
            busiList.add(entityOutRel);
        }
        //ETC
        if (originalBridgeFee > updateBridgeFee) {
            Long bridgeFee = originalBridgeFee - updateBridgeFee;
            BusiSubjectsRel bridgeFeeRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_LUQIAO_LOW, bridgeFee);
            busiList.add(bridgeFeeRel);
        } else if (originalBridgeFee < updateBridgeFee) {
            Long bridgeFee = updateBridgeFee - originalBridgeFee;
            BusiSubjectsRel bridgeFeeRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_LUQIAO_UPP, bridgeFee);
            busiList.add(bridgeFeeRel);
        }
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        if (busiList != null && busiList.size() > 0) {
            // ??????????????????
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, busiList);
            // ????????????????????????????????????????????????
            long soNbr = CommonUtil.createSoNbr();
            OrderResponseDto param = new OrderResponseDto();
            param.setSourceList(sourceList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,user);

            if (fleetList != null && fleetList.size() > 0) {
                // ??????????????????
                List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, fleetList);
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                        sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                        tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,user);
                busiSubjectsRelList.addAll(fleetSubjectsRelList);
            }
            //??????????????????
            for (BusiSubjectsRel rel : busiSubjectsRelList) {
                if (rel.getAmountFee() <= 0) {
                    continue;
                }
                //??????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(masterUserId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), -1L, vehicleAffiliation, orderId,
                            tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,ol.getUserType(),0L,token);
                    payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
                    payoutIntf.setRemark("????????????");
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                    }
                    OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    if (orderScheduler != null) {
                        payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                    } else {
                        OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        //??????payout_order
                        payoutOrderService.createPayoutOrder(masterUserId, rel.getAmountFee(), OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                    }
                }
                //??????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW
                        || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                            -1L, isAutomatic, isAutomatic, masterUserId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                    payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                    payoutIntf.setRemark("????????????");
                    OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    if (orderScheduler != null) {
                        payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                    } else {
                        OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                }
            }
        }
        //?????????????????????
        ParametersNewDto inParamNew = this.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),EnumConsts.PayInter.UPDATE_THE_ORDER, orderId, updateEntiyOilFee + updateFictitiousOilFee + updateBridgeFee + updateBridgeFee + updateSlaveSubsidy, vehicleAffiliation,"");
        inParamNew.setTotalFee(String.valueOf(updateEntiyOilFee + updateFictitiousOilFee + updateBridgeFee + updateBridgeFee + updateSlaveSubsidy));
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantSysOperator.getName());
        inParamNew.setOrderLimitBase(ol);
        inParamNew.setSourceList(sourceList);
        inParamNew.setIsNeedBill(isNeedBill);
        if (busiSubjectsRelList != null) {
            this.busiToOrderNew(inParamNew, busiSubjectsRelList,user);
        }
        //?????????
        if (slaveUserId != null && slaveUserId > 0) {
            SysUser sysOperator1 = sysUserService.getSysOperatorByUserIdOrPhone(slaveUserId, null, 0L);
            if (sysOperator1 == null) {
                throw new BusinessException("????????????????????????!");
            }
            busiList.clear();
            fleetList.clear();
            if (originalSlaveSubsidy == null|| updateSlaveSubsidy == null || originalSlaveSubsidy < 0 || updateSlaveSubsidy < 0) {
                throw new BusinessException("?????????????????????????????????");
            }
            OrderAccount account1 = orderAccountService.queryOrderAccount(slaveUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),ol.getUserType());
            OrderLimit slaveOrder =  orderLimitService.getOrderLimitByUserIdAndOrderId(slaveUserId, orderId,-1);
            if (slaveOrder == null) {
                throw new BusinessException("?????????????????????!");
            }
            //???????????????
            if (originalSlaveSubsidy  > updateSlaveSubsidy ) {
                Long cash = originalSlaveSubsidy - updateSlaveSubsidy;
                //??????????????????
                BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW, cash);
                busiList.add(cashRel);
                //??????????????????
                BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_LOW, cash);
                fleetList.add(fleetCashRel);
            } else if (originalSlaveSubsidy <  updateSlaveSubsidy) {
                Long cash = updateSlaveSubsidy - originalSlaveSubsidy;
                //??????????????????
                BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP, cash);
                busiList.add(cashRel);
                //??????????????????
                BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_UPP, cash);
                fleetList.add(fleetCashRel);
            }
            if (busiList != null && busiList.size() > 0) {
                // ??????????????????
                List<BusiSubjectsRel> busiSubjectsRelList1 = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, busiList);
                // ????????????????????????????????????????????????
                long soNbr = CommonUtil.createSoNbr();
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                        tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account1, busiSubjectsRelList1, soNbr, orderId,
                        sysOperator1.getName(), null, tenantId, null, "", null, vehicleAffiliation,user);
                if (fleetList != null && fleetList.size() > 0) {
                    // ??????????????????
                    List<BusiSubjectsRel> fleetSubjectsRelList1 = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, fleetList);
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                            sysOperator1.getUserInfoId(), sysOperator1.getName(), fleetAccount, fleetSubjectsRelList1, soNbr, orderId,
                            tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,user);
                    busiSubjectsRelList1.addAll(fleetSubjectsRelList1);
                }
                //??????????????????
                for (BusiSubjectsRel rel : busiSubjectsRelList1) {
                    if (rel.getAmountFee() <= 0) {
                        continue;
                    }
                    //??????
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP) {
                        PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(slaveUserId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), -1L, vehicleAffiliation, orderId,
                                tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,ol.getUserType(),0L,token);
                        payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
                        payoutIntf.setRemark("????????????");
                        if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                                !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                            payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                        }
                        OrderScheduler orderScheduler =  orderSchedulerService.getOrderScheduler(orderId);
                        payoutIntf.setBusiCode(String.valueOf(orderId));
                        if (orderScheduler != null) {
                            payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                        } else {
                            OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
                            if (orderSchedulerH != null) {
                                payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                            }
                        }
                        payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                        if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                                !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                            //??????payout_order
                            payoutOrderService.createPayoutOrder(slaveUserId, rel.getAmountFee(), OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                        }
                    }
                    //??????
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW ) {
                        PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                                -1L, isAutomatic, isAutomatic, slaveUserId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                        payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                        payoutIntf.setRemark("????????????");
                        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                        payoutIntf.setBusiCode(String.valueOf(orderId));
                        if (orderScheduler != null) {
                            payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                        } else {
                            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                            if (orderSchedulerH != null) {
                                payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                            }
                        }
                        payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                    }
                }
                //?????????????????????
                inParamNew = this.setParametersNew(sysOperator1.getUserInfoId(), sysOperator1.getBillId(),EnumConsts.PayInter.UPDATE_THE_ORDER, orderId, updateEntiyOilFee + updateFictitiousOilFee + updateBridgeFee + updateBridgeFee + updateSlaveSubsidy, vehicleAffiliation,"");
                inParamNew.setTotalFee(String.valueOf(updateEntiyOilFee + updateFictitiousOilFee + updateBridgeFee + updateBridgeFee + updateSlaveSubsidy));
                inParamNew.setTenantUserId(tenantUserId);
                inParamNew.setTenantBillId(tenantSysOperator.getBillId());
                inParamNew.setTenantUserName(tenantSysOperator.getName());
                inParamNew.setOrderLimitBase(slaveOrder);
                inParamNew.setIsNeedBill(isNeedBill);
                if (busiSubjectsRelList1 != null) {
                    this.busiToOrderNew(inParamNew, busiSubjectsRelList1,user);
                }
            }
        }
    }

    @Override
    public void updateTheOrder(UpdateTheOrderInDto inParam,LoginInfo user,String token) {

        Long userId = inParam.getUserId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long originalAmountFee = inParam.getOriginalAmountFee();
        Long updateAmountFee = inParam.getUpdateAmountFee();
        Long originalVirtualOilFee = inParam.getOriginalVirtualOilFee();
        Long updatelongVirtualOilFee = inParam.getUpdatelongVirtualOilFee();
        Long originalEntityOilFee = inParam.getOriginalEntityOilFee();
        Long updateEntityOilFee = inParam.getUpdateEntityOilFee();
        Long originalEtcFee = inParam.getOriginalEtcFee();
        Long updateEtcFee = inParam.getUpdateEtcFee();
        Long orderId = inParam.getOrderId();
        Long tenantId = inParam.getTenantId();
        Integer isNeedBill = inParam.getIsNeedBill();
        int oilUserType = inParam.getOilUserType();
        Long originalArriveFee = inParam.getOriginalArriveFee();
        Long updateArriveFee = inParam.getUpdateArriveFee();
        Integer isPayArriveFee = inParam.getIsPayArriveFee();
		/*Integer originalOilConsumer = inParam.getOriginalOilConsumer();
		Integer updateOilConsumer = inParam.getUpdateOilConsumer();*/
        Integer originalOilAccountType = inParam.getOriginalOilAccountType();
        Integer updateOilAccountType = inParam.getUpdateOilAccountType();
        Integer originalOilBillType = inParam.getOriginalOilBillType();
        Integer updateOilBillType = inParam.getUpdateOilBillType();
        if (userId == null || userId < 0) {
            throw new BusinessException("?????????????????????");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("???????????????id");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("???????????????????????????");
        }
        if (originalAmountFee == null || updateAmountFee == null || originalAmountFee < 0 || updateAmountFee < 0) {
            throw new BusinessException("????????????????????????0");
        }
        if (originalVirtualOilFee == null || updatelongVirtualOilFee == null || originalVirtualOilFee < 0 || updatelongVirtualOilFee < 0) {
            throw new BusinessException("???????????????????????????0");
        }
        if (originalEntityOilFee == null || updateEntityOilFee == null || originalEntityOilFee < 0 || updateEntityOilFee < 0) {
            throw new BusinessException("???????????????????????????0");
        }
        if (originalEtcFee == null || updateEtcFee == null || originalEtcFee < 0 || updateEtcFee < 0) {
            throw new BusinessException("??????ETC????????????0");
        }
        if (originalArriveFee == null || updateArriveFee == null || originalArriveFee < 0 || updateArriveFee < 0) {
            throw new BusinessException("???????????????0");
        }
        if (isPayArriveFee == null || isPayArriveFee < 0) {
            throw new BusinessException("????????????????????????");
        }
        if (originalOilAccountType == null || updateOilAccountType == null || originalOilAccountType <= 0 || updateOilAccountType <= 0) {
            throw new BusinessException("??????????????????????????????");
        }
        if (originalOilBillType == null || updateOilBillType == null || originalOilBillType <= 0 || updateOilBillType <= 0) {
            throw new BusinessException("????????????????????????");
        }
        //todo ??????
      //  boolean isLock = SysContexts.getLock(this.getClass().getName() + "updateTheOrder" + orderId + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        // ????????????id?????????????????????id
        Long tenantUserId = sysTenantDefService.getSysTenantDef(tenantId).getAdminUser();
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        // ????????????id??????????????????
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderSchedulerH orderSchedulerH = null;
        if (orderScheduler == null) {
            orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
            }
            if (null != orderSchedulerH.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderSchedulerH.getVehicleClass()) {
                isOwnCarUser = true;
            }
        } else {
            if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
                isOwnCarUser = true;
            }
        }
        boolean isUpdateOilAccountType = false;
        if (originalOilAccountType.intValue() != updateOilAccountType.intValue()) {//??????????????????????????????
            isUpdateOilAccountType = true;
            boolean isConsumeOil =  this.getOrderIsConsumeOil(orderId);
            if (isConsumeOil) {
                throw new BusinessException("????????????" + orderId + " ???????????????????????????????????????????????????");
            }
        }
        //????????????????????????
        List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSourceByUserIdAndOrderId(userId, orderId,-1);
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("?????????????????????!");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("??????????????????????????????");
        }
        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long noPayEtc = ol.getNoPayEtc() == null ? 0L : ol.getNoPayEtc();
        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),ol.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetList = new ArrayList<BusiSubjectsRel>();
        long serviceFee = 0;
        long arriveServiceFee = 0;
        //???????????????
        if (originalAmountFee  > updateAmountFee ) {
            Long cash = originalAmountFee - updateAmountFee;
            //??????????????????
            BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW, cash);
            busiList.add(cashRel);
            //??????????????????
            BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_LOW, cash);
            fleetList.add(fleetCashRel);
        } else if (originalAmountFee <  updateAmountFee) {
            Long cash = updateAmountFee - originalAmountFee;
            //??????????????????
            BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP, cash);
            busiList.add(cashRel);
            //???????????? ????????? 20190717  ?????????????????????????????????
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), cash, 0L, 0L, cash, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            //??????????????????
            BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_UPP, cash);
            fleetList.add(fleetCashRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4001, serviceFee);
                fleetList.add(payableServiceFeeSubjectsRel);
            }
        }
        //?????????
        this.updateOilAccountType(sourceList, ol, inParam, busiList,fleetList,
                isUpdateOilAccountType,isOwnCarUser,null,user);
        //?????????
        if (originalEntityOilFee > updateEntityOilFee) {
            Long entityOil = originalEntityOilFee - updateEntityOilFee;
            BusiSubjectsRel entityRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_LOW, entityOil);
            busiList.add(entityRel);
        } else if (originalEntityOilFee < updateEntityOilFee) {
            Long entityOil = updateEntityOilFee - originalEntityOilFee;
            BusiSubjectsRel entityRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP, entityOil);
            busiList.add(entityRel);
            BusiSubjectsRel entityOutRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP_OUT, entityOil);
            busiList.add(entityOutRel);

        }
        //ETC
        if (originalEtcFee > updateEtcFee) {
            Long etcFee = originalEtcFee - updateEtcFee;
            //????????????????????????etc
            Long backUpEtc = 0L;
            //??????????????????
            Long payableEtc = 0L;
            if (noPayEtc >= etcFee) {
                backUpEtc = etcFee;
            } else {
                backUpEtc = noPayEtc;
                payableEtc = etcFee - noPayEtc;
            }
            if (backUpEtc > 0) {
                BusiSubjectsRel etcRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ETC_LOW, backUpEtc);
                busiList.add(etcRel);
            }
            if (payableEtc > 0) {
                //??????????????????
                BusiSubjectsRel payableEtcRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW, payableEtc);
                busiList.add(payableEtcRel);
                //??????????????????
                BusiSubjectsRel fleetPayableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ETC_LOW, payableEtc);
                fleetList.add(fleetPayableOilRel);
            }
        } else if (originalEtcFee < updateEtcFee) {
            Long etcFee = updateEtcFee - originalEtcFee;
            BusiSubjectsRel etcRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ETC_UPP, etcFee);
            busiList.add(etcRel);
        }
        //?????????
        if (isPayArriveFee == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            if (originalArriveFee > updateArriveFee) {
                Long arriveFee = originalArriveFee - updateArriveFee;
                //??????????????????
                BusiSubjectsRel arriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW, arriveFee);
                busiList.add(arriveRel);
                //??????????????????
                BusiSubjectsRel fleetArriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_LOW, arriveFee);
                fleetList.add(fleetArriveRel);
            } else if (originalArriveFee < updateArriveFee) {
                Long arriveFee = updateArriveFee - originalArriveFee;
                //??????????????????
                BusiSubjectsRel arriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP, arriveFee);
                busiList.add(arriveRel);
                //???????????? ????????? 20190717  ?????????????????????????????????
                boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                if (isLuge) {
                    Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), arriveFee, 0L, 0L, arriveFee, tenantId,null);
                    arriveServiceFee = (Long) result.get("lugeBillServiceFee");
                }
                //??????????????????
                BusiSubjectsRel fleetArriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_UPP, arriveFee);
                fleetList.add(fleetArriveRel);
                if (arriveServiceFee > 0) {
                    BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4008, arriveServiceFee);
                    fleetList.add(payableServiceFeeSubjectsRel);
                }
            }
        }

        List<BusiSubjectsRel> busiSubjectsRelList = null;
        if (busiList != null && busiList.size() > 0) {
            // ??????????????????
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, busiList);
            // ????????????????????????????????????????????????
            long soNbr = CommonUtil.createSoNbr();
            OrderResponseDto param = new OrderResponseDto();
            param.setSourceList(sourceList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,user);

            if (fleetList != null && fleetList.size() > 0) {
                //??????????????????
                OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                // ??????????????????
                List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, fleetList);
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                        sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                        tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,user);
                busiSubjectsRelList.addAll(fleetSubjectsRelList);
            }
            //??????????????????
            boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
            Integer isAutomatic = null;
            if (isAutoTransfer) {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
            } else {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
            }
            //??????????????????
            for (BusiSubjectsRel rel : busiSubjectsRelList) {
                if (rel.getAmountFee() <= 0) {
                    continue;
                }
                //??????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), -1L, vehicleAffiliation, orderId,
                            tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,ol.getUserType(),0L,token);
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP ) {
                        payoutIntf.setBillServiceFee(serviceFee);
                    } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP) {
                        payoutIntf.setBillServiceFee(arriveServiceFee);
                    }
                    payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
                    payoutIntf.setRemark("????????????");
                    if (isTenant) {
                        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                        payoutIntf.setTenantId(sysTenantDef.getId());
                    }
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                    }
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    if (orderScheduler != null) {
                        payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                    } else {
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        //??????payout_order
                        payoutOrderService.createPayoutOrder(userId, rel.getAmountFee(), OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                    }
                }
                //??????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW
                        || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                            -1L, isAutomatic, isAutomatic, userId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                    payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                    payoutIntf.setRemark("????????????");
                    if (isTenant) {
                        payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                        payoutIntf.setPayTenantId(sysTenantDef.getId());
                    }
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    if (orderScheduler != null) {
                        payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                    } else {
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                }
            }
        }
        //?????????????????????
        ParametersNewDto inParamNew = setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),EnumConsts.PayInter.UPDATE_THE_ORDER, orderId, updateAmountFee + updatelongVirtualOilFee + updateEntityOilFee + updateEtcFee, vehicleAffiliation,"");
        inParamNew.setTotalFee(String.valueOf(updateAmountFee + updatelongVirtualOilFee + updateEntityOilFee + updateEtcFee));
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantSysOperator.getName());
        inParamNew.setOrderLimitBase(ol);
        inParamNew.setSourceList(sourceList);
        inParamNew.setIsNeedBill(isNeedBill);
        if (busiSubjectsRelList != null) {
            this.busiToOrderNew(inParamNew, busiSubjectsRelList,user);
        }
    }

    /**
     * @param userId ??????id
     * @param billId ???????????????
     * @param businessId ??????id
     * @param orderId ??????id
     * @param amount ??????
     * @param vehicleAffiliation ????????????
     * @param finalPlanDate ??????????????????
     */
    @Override
    public ParametersNewDto setParametersNew(Long userId, String billId, Long businessId, Long orderId, Long amount, String vehicleAffiliation, String finalPlanDate) {
        ParametersNewDto map = new ParametersNewDto();
        map.setUserId(userId);
        map.setBillId(billId);
        map.setBusinessId(businessId);
        map.setOrderId(orderId);
        map.setAmount(amount);
        map.setVehicleAffiliation(vehicleAffiliation);
        map.setFinalPlanDate(finalPlanDate);
        return map;
    }

    @Override
    public List<OrderOilSource> getOrderOilSourceByUserIdAndOrderId(Long userId, Long orderId, Integer userType) {
        LambdaQueryWrapper<OrderOilSource> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderOilSource::getUserId,userId)
                .eq(OrderOilSource::getOrderId,orderId);
        if(userType > 0){
            lambda.eq(OrderOilSource::getUserType, userType);
        }
        lambda.orderByDesc(OrderOilSource::getOrderDate);
        return this.list(lambda);
    }

    @Override
    public Boolean getOrderIsConsumeOil(Long orderId) {
        if (orderId == null || orderId <= 1) {
            throw new BusinessException("???????????????????????????");
        }
        boolean flag = false;
        List<OrderOilSource> list = this.getOrderOilSourceByOrderId(orderId);
        if (list != null && list.size() > 0) {
            List<Long> flowIds = new ArrayList<>();
            for (OrderOilSource oos : list) {
                flowIds.add(oos.getId());
            }
            List<ConsumeOilFlowExt> exts = consumeOilFlowExtService.getConsumeOilFlowExtByFlowId(flowIds, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1);
            if (exts != null && exts.size() > 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public List<OrderOilSource> getOrderOilSourceByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderOilSource> lambda=Wrappers.lambdaQuery();
        lambda.eq(OrderOilSource::getOrderId,orderId);
        return this.list(lambda);
    }

    @Override
    public OrderOilSource getOrderOilSource(Long userId, Long orderId, Long sourceOrderId, String vehicleAffiliation, Long tenantId, Integer userType, Integer oilAccountType, Integer oilConsumer, Integer oilBillType, String oilAffiliation) {
      LambdaQueryWrapper<OrderOilSource> lambda=Wrappers.lambdaQuery();
        if(userType > 0){
            lambda.eq(OrderOilSource::getUserType, userType);
        }
        lambda.eq(OrderOilSource::getUserId, userId);
        lambda.eq(OrderOilSource::getOrderId, orderId);
        lambda.eq(OrderOilSource::getSourceOrderId, sourceOrderId);
        lambda.eq(OrderOilSource::getVehicleAffiliation, vehicleAffiliation);
        lambda.eq(OrderOilSource::getOilAffiliation, oilAffiliation);
        lambda.eq(OrderOilSource::getTenantId, tenantId);
        lambda.eq(OrderOilSource::getOilAccountType, oilAccountType);
        lambda.eq(OrderOilSource::getOilConsumer, oilConsumer);
        lambda.eq(OrderOilSource::getOilBillType, oilBillType);
        List<OrderOilSource> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<OrderOilSource> getOrderOilSource(Long userId, Long sourceOrderId,
                                                  String vehicleAffiliation, Long tenantId,
                                                  Integer userType) {
       LambdaQueryWrapper<OrderOilSource> lambda=Wrappers.lambdaQuery();
        if(userType > 0){
            lambda.eq(OrderOilSource::getUserType, userType);
        }
        lambda.eq(OrderOilSource::getUserId, userId);
        lambda.eq(OrderOilSource::getSourceOrderId, sourceOrderId);
        lambda.eq(OrderOilSource::getVehicleAffiliation, vehicleAffiliation);
        lambda.eq(OrderOilSource::getTenantId, tenantId);
        List<OrderOilSource> list = this.list(lambda);
        return list;
    }

    @Override
    public List<OrderOilSource> matchOrderAccountToOrderLimit(Long amount, Long userId, Long orderId,
                                                              Long tenantId, Integer isNeedBill,
                                                              String vehicleAffiliation, Long driverUserId,
                                                              Long businessId, Long subjectsId,LoginInfo baseuser) {
        // ????????????ID???????????????????????????????????????
        List<OrderOilSource> sourceList = new ArrayList<OrderOilSource>();
        List<OrderAccount> tenantAccount = orderAccountService.getOrderAccount(userId, OrderAccountConst.ORDER_BY.OIL_BALANCE,
                -1L,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        long oilAmount = amount;
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        for (OrderAccount ac : tenantAccount) {
            long oilBalance = ac.getOilBalance();
            if (oilBalance <= 0 || oilAmount == 0) {
                continue;
            }
            //??????????????????
            List<OrderOilSource> orderOilSourceList = orderOilSourceService.getOrderOilSource(ac.getUserId(),
                    Long.valueOf(ac.getOilAffiliation()),ac.getVehicleAffiliation(), ac.getSourceTenantId(),
                    ac.getUserType());
            long shouleMatchAmount = 0L;
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            // ????????????
            if (oilBalance >= oilAmount) {
                shouleMatchAmount = oilAmount;
                oilAmount = 0;
            } else {
                oilAmount -= ac.getOilBalance();
                shouleMatchAmount = ac.getOilBalance();
            }
            BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(subjectsId, shouleMatchAmount);
            busiList.add(amountFeeSubjectsRel);
            // ??????????????????
            List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(businessId, busiList);
            long soNbr = CommonUtil.createSoNbr();
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, businessId,
                    0L, "", ac, busiSubjectsRelList, soNbr, orderId,
                    "", null, tenantId, null, "", null,
                    ac.getVehicleAffiliation(),baseuser);
       //todo
        //    matchAmountUtil.matchAmounts(shouleMatchAmount, 0, 0, "noPayOil","noRebateOil","noCreditOil", orderOilSourceList);
            long matchToatalAmount = 0L;

            for (OrderOilSource ol : orderOilSourceList) {
                if (ol.getMatchAmount() == null || ol.getMatchAmount() <= 0L) {
                    continue;
                }
                long matchNoPayOil = ol.getMatchNoPayOil() == null ? 0 : ol.getMatchNoPayOil();
                long matchNoRebateOil = ol.getMatchNoRebateOil() == null ? 0 : ol.getMatchNoRebateOil();
                long matchNoCreditOil = ol.getMatchNoCreditOil() == null ? 0 : ol.getMatchNoCreditOil();
                OrderOilSource source = orderOilSourceService.saveOrderOilSource(driverUserId,orderId,ol.getSourceOrderId(),
                        matchNoPayOil,matchNoPayOil,0L,ol.getSourceTenantId(),LocalDateTime.now(), baseuser.getId(),
                        ol.getIsNeedBill(),ol.getVehicleAffiliation(),ol.getOrderDate(),ol.getOilAffiliation(),ol.getOilConsumer(),
                        matchNoRebateOil,matchNoRebateOil,0L,matchNoCreditOil,matchNoCreditOil,0L,
                        SysStaticDataEnum.USER_TYPE.DRIVER_USER,ol.getOilAccountType(),ol.getOilBillType(),baseuser);
                source.setMatchAmount(ol.getMatchAmount());
                sourceList.add(source);
                matchToatalAmount += ol.getMatchAmount();
                amountFeeSubjectsRel.setAmountFee(ol.getMatchAmount());
                busiSubjectsRelList = busiSubjectsRelService.feeCalculation(businessId, busiList);
                ParametersNewDto param = this.setParametersNew(ol.getUserId(), sysOperator.getBillId(), businessId,
                        orderId,ol.getMatchAmount(),ol.getVehicleAffiliation(),"");
                param.setOilSource(ol);
                this.busiToOrderNew(param, busiSubjectsRelList,baseuser);

            }
            if (matchToatalAmount != shouleMatchAmount) {
                throw new BusinessException("???????????????" + ac.getVehicleAffiliation() +"????????????????????????????????????????????????????????????");
            }
        }
        if (oilAmount > 0) {//??????????????????????????????????????????????????????????????????????????????
        	/*OrderLimit limit = orderLimitSV.getOrderLimit(driverUserId, orderId, tenantId);
        	OrderOilSource source = operationOilTF.saveOrderOilSource(driverUserId,orderId,orderId,oilAmount,oilAmount,0,tenantId,date,baseUser.getOperId(),isNeedBill,vehicleAffiliation,limit.getOrderDate(),limit.getOilAffiliation(),limit.getOilFeePrestore(),limit.getPinganPayAcctId());
        	sourceList.add(source);*/
            throw new BusinessException("?????????????????????????????????????????????????????????");
        }
        return sourceList;
    }

    @Override
    public OrderOilSource saveOrderOilSource(Long userId, Long orderId, Long sourceOrderId,
                                             Long sourceAmount, Long noPayOil, Long paidOil,
                                             Long sourceTenantId, LocalDateTime createDate, Long opId,
                                             Integer isNeedBill, String vehicleAffiliation,
                                             LocalDateTime orderDate, String oilAffiliation, Integer oilConsumer,
                                             Long rebateOil, Long noRebateOil, Long paidRebateOil,
                                             Long creditOil, Long noCreditOil, Long paidCreditOil,
                                             Integer userType, Integer oilAccountType,
                                             Integer oilBillType,LoginInfo baseUser) {
        OrderOilSource orderOilSource = orderOilSourceService.getOrderOilSource(userId, orderId, sourceOrderId, vehicleAffiliation, sourceTenantId,-1,oilAccountType,oilConsumer,oilBillType,oilAffiliation);
        if (orderOilSource != null) {
            Long tempSourceAmount = orderOilSource.getSourceAmount() == null ? 0L : orderOilSource.getSourceAmount();
            Long tempNoPayOil = orderOilSource.getNoPayOil() == null ? 0L : orderOilSource.getNoPayOil();
            Long tempPaidOil = orderOilSource.getPaidOil() == null ? 0L : orderOilSource.getPaidOil();
            Long tempRebateOil = orderOilSource.getRebateOil() == null ? 0L : orderOilSource.getRebateOil();
            Long tempNoRebateOil = orderOilSource.getNoRebateOil() == null ? 0L : orderOilSource.getNoRebateOil();
            Long tempPaidRebateOil = orderOilSource.getPaidRebateOil() == null ? 0L : orderOilSource.getPaidRebateOil();
            Long tempCreditOil = orderOilSource.getCreditOil() == null ? 0L : orderOilSource.getCreditOil();
            Long tempNoCreditOil = orderOilSource.getNoCreditOil() == null ? 0L : orderOilSource.getNoCreditOil();
            Long tempPaidCreditOil = orderOilSource.getPaidCreditOil() == null ? 0L : orderOilSource.getPaidCreditOil();

            orderOilSource.setSourceAmount(tempSourceAmount + sourceAmount);
            orderOilSource.setNoPayOil(tempNoPayOil + noPayOil);
            orderOilSource.setPaidOil(tempPaidOil + paidOil);
            orderOilSource.setRebateOil(tempRebateOil + rebateOil);
            orderOilSource.setNoRebateOil(tempNoRebateOil + noRebateOil);
            orderOilSource.setPaidRebateOil(tempPaidRebateOil + paidRebateOil);
            orderOilSource.setCreditOil(tempCreditOil + creditOil);
            orderOilSource.setNoCreditOil(tempNoCreditOil + noCreditOil);
            orderOilSource.setPaidCreditOil(tempPaidCreditOil + paidCreditOil);
            orderOilSource.setMatchAmount(noPayOil + noRebateOil + noCreditOil);
            orderOilSource.setOilAffiliation(oilAffiliation);
            orderOilSource.setUserType(userType);
            orderOilSourceService.saveOrUpdate(orderOilSource);
        } else {
            orderOilSource = new OrderOilSource();
            orderOilSource.setUserId(userId);
            orderOilSource.setOrderId(orderId);
            orderOilSource.setSourceOrderId(sourceOrderId);
            orderOilSource.setSourceAmount(sourceAmount);
            orderOilSource.setNoPayOil(noPayOil);
            orderOilSource.setPaidOil(paidOil);
            orderOilSource.setTenantId(baseUser.getTenantId());
            orderOilSource.setSourceTenantId(sourceTenantId);
            orderOilSource.setOrderDate(orderDate);
            orderOilSource.setCreateTime(createDate);
            orderOilSource.setOpId(opId);
            orderOilSource.setIsNeedBill(isNeedBill);
            orderOilSource.setVehicleAffiliation(vehicleAffiliation);
            orderOilSource.setMatchAmount(noPayOil + noRebateOil + noCreditOil);
            orderOilSource.setOilAffiliation(oilAffiliation);
            orderOilSource.setOilConsumer(oilConsumer);
            orderOilSource.setRebateOil(rebateOil);
            orderOilSource.setNoRebateOil(noRebateOil);
            orderOilSource.setPaidRebateOil(paidRebateOil);
            orderOilSource.setCreditOil(creditOil);
            orderOilSource.setNoCreditOil(noCreditOil);
            orderOilSource.setPaidCreditOil(paidCreditOil);
            orderOilSource.setUserType(userType);
            orderOilSource.setOilAccountType(oilAccountType);
            orderOilSource.setOilBillType(oilBillType);
            orderOilSourceService.save(orderOilSource);
        }
        return orderOilSource;
    }


    /**
     *
     * @param sourceList
     * @param ol
     * @param inParam
     * @throws Exception
     */
    public void updateOilAccountType(List<OrderOilSource> sourceList, OrderLimit ol, UpdateTheOrderInDto inParam, List<BusiSubjectsRel> busiList, List<BusiSubjectsRel> fleetList, Boolean isUpdateOilAccountType,
                                     Boolean isOwnCarUser, UpdateTheOwnCarOrderInDto inParam1,LoginInfo user) {


        Long userId = null;
        Long originalVirtualOilFee = null;
        Long updatelongVirtualOilFee = null;
        Long orderId = null;
        Long tenantId = null;
        Integer originalOilAccountType = null;
        Integer updateOilAccountType = null;
        Integer originalOilBillType = null;
        Integer updateOilBillType = null;
        Integer isNeedBill = null;
        String vehicleAffiliation = null;
        if (inParam != null) {
            userId = inParam.getUserId();
            originalVirtualOilFee = inParam.getOriginalVirtualOilFee();
            updatelongVirtualOilFee = inParam.getUpdatelongVirtualOilFee();
            orderId = inParam.getOrderId();
            tenantId = inParam.getTenantId();
            originalOilAccountType = inParam.getOriginalOilAccountType();
            updateOilAccountType = inParam.getUpdateOilAccountType();
            originalOilBillType = inParam.getOriginalOilBillType();
            updateOilBillType = inParam.getUpdateOilBillType();
            isNeedBill = inParam.getIsNeedBill();
            vehicleAffiliation = inParam.getVehicleAffiliation();
        }
        if (inParam1 != null) {
            userId = inParam1.getMasterUserId();
            originalVirtualOilFee = inParam1.getOriginalFictitiousOilFee();
            updatelongVirtualOilFee = inParam1.getUpdateFictitiousOilFee();
            orderId = inParam1.getOrderId();
            tenantId = inParam1.getTenantId();
            originalOilAccountType = inParam1.getOriginalOilAccountType();
            updateOilAccountType = inParam1.getUpdateOilAccountType();
            originalOilBillType = inParam1.getOriginalOilBillType();
            updateOilBillType = inParam1.getUpdateOilBillType();
            isNeedBill = inParam1.getIsNeedBill();
            vehicleAffiliation = inParam1.getVehicleAffiliation();
        }

        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long tenantUserId = sysTenantDefService.getSysTenantDef(tenantId).getAdminUser();
        //?????????
        OrderOilSource oos = null;
        if (originalVirtualOilFee.longValue() > updatelongVirtualOilFee.longValue() && !isUpdateOilAccountType) {//???????????????
            Long virtualOil = originalVirtualOilFee - updatelongVirtualOilFee;
            //?????????????????????????????????
            Long backUpOil = 0L;
            //??????????????????
            Long payableOil = 0L;
            if (noPayOil >= virtualOil) {
                backUpOil = virtualOil;
            } else {
                backUpOil = noPayOil;
                payableOil = virtualOil - noPayOil;
            }
            if (backUpOil > 0) {
            //    matchAmountUtil.matchAmountOilSourceRecord(backUpOil, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
                this.updateOrderAccountOil(userId, orderId, tenantUserId,tenantId, isNeedBill, backUpOil, sourceList,user);
                BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, backUpOil);
                busiList.add(oilRel);
                for (OrderOilSource source : sourceList) {
                    if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                        oos = source;
                        break;
                    }
                }
                //???????????????????????????????????????
                Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                if (oos != null && oos.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && oos.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    Long oilFee = oos.getMatchAmount() == null ? 0L : oos.getMatchAmount();
                    List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(orderId);
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                        } else {
                            bbi.setOil(bbi.getOil() - oilFee);
                            bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                            bbi.setUpdateTime(LocalDateTime.now());
                            baseBillInfoService.saveOrUpdate(bbi);
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                        }
                    }
                }
                //???????????????
                if (oos != null && oos.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && backUpOil > 0) {
                    Long tempNoPayOil = oos.getMatchNoPayOil() == null ? 0L : oos.getMatchNoPayOil();
                    Long tempNoRebateOil = oos.getMatchNoRebateOil() == null ? 0L : oos.getMatchNoRebateOil();
                    Long tempNoCreditOil = oos.getMatchNoCreditOil() == null ? 0L : oos.getMatchNoCreditOil();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("noPayOil", tempNoPayOil);
                    map.put("noRebateOil", tempNoRebateOil);
                    map.put("noCreditOil", tempNoCreditOil);
                    oilSourceRecordService.recallOil(userId, String.valueOf(orderId), tenantUserId,
                            EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, tenantId, map,recallType,user);
                }
                long matchNoPayOil = oos.getMatchNoPayOil() == null ? 0 : oos.getMatchNoPayOil();
                long matchNoRebateOil = oos.getMatchNoRebateOil() == null ? 0 : oos.getMatchNoRebateOil();
                long matchNoCreditOil = oos.getMatchNoCreditOil() == null ? 0 : oos.getMatchNoCreditOil();
                oos.setNoPayOil(oos.getNoPayOil() - matchNoPayOil);
                oos.setSourceAmount(oos.getSourceAmount() - matchNoPayOil);
                oos.setNoCreditOil(oos.getNoCreditOil() - matchNoCreditOil);
                oos.setCreditOil(oos.getCreditOil() - matchNoCreditOil);
                oos.setNoRebateOil(oos.getNoRebateOil() - matchNoRebateOil);
                oos.setRebateOil(oos.getRebateOil() - matchNoRebateOil);
                orderOilSourceService.saveOrUpdate(oos);
            }
            if (payableOil > 0) {
                //??????????????????
                BusiSubjectsRel payableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW, payableOil);
                busiList.add(payableOilRel);
                //??????????????????
                BusiSubjectsRel fleetPayableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_LOW, payableOil);
                fleetList.add(fleetPayableOilRel);
            }
        }
        if (originalVirtualOilFee.longValue() < updatelongVirtualOilFee.longValue() && !isUpdateOilAccountType) {//???????????????
            Long virtualOil = updatelongVirtualOilFee - originalVirtualOilFee;
            long tempVirtualOilFee = virtualOil;
            BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP, virtualOil);
            busiList.add(oilRel);
            if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2 && virtualOil > 0 && isOwnCarUser) {

                OrderAccountBalanceDto oilBlaceMap = orderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",tenantId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                OrderAccountOutVo orderAccountOut =oilBlaceMap.getOa();
                if (null == orderAccountOut) {
                    throw new BusinessException("???????????????????????????");
                }
                //???????????????????????????
                Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
                if (null == canUseOilBalance) {
                    canUseOilBalance = 0L;
                }
                if (canUseOilBalance.longValue() > 0) {
                    if (virtualOil > canUseOilBalance.longValue()) {
                        List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId,
                                orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                                EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                        if (sourceListNew == null || sourceListNew.size() <= 0) {
                            throw new BusinessException("???????????????????????????");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceListNew) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != canUseOilBalance) {
                                throw new BusinessException("??????????????????????????????????????????");
                            }
                        }
                        tempVirtualOilFee -= canUseOilBalance;
                    } else {
                        List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(virtualOil, tenantUserId, orderId,
                                tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                                EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                        if (sourceListNew == null || sourceListNew.size() <= 0) {
                            throw new BusinessException("???????????????????????????");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceListNew) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != virtualOil) {
                                throw new BusinessException("??????????????????????????????????????????");
                            }
                        }
                        tempVirtualOilFee = 0L;
                    }
                }
            }
            for (OrderOilSource source : sourceList) {
                if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                    source.setSourceAmount(source.getSourceAmount() + virtualOil);
                    source.setNoPayOil(source.getNoPayOil() + virtualOil);
                    orderOilSourceService.saveOrUpdate(source);
                    oos = source;
                    break;
                }
            }
            if (oos == null) {
                oos = orderOilSourceService.saveOrderOilSource(userId, orderId, orderId, tempVirtualOilFee, tempVirtualOilFee, 0L, tenantId,LocalDateTime.now(), user.getId(), isNeedBill, vehicleAffiliation,ol.getOrderDate(),ol.getOilAffiliation(),ol.getOilConsumer(),
                        0L,0L,0L,0L,0L,0L,
                        ol.getUserType(),updateOilAccountType,updateOilBillType,user);
            }
            Integer distributionType = null;
            if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
            } else if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//????????????
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                } else if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//??????????????????
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2;
                }
            }

            //?????????????????????????????????
            if (tempVirtualOilFee > 0 && oos.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {
                Map<String, Object> resultMap = oilRechargeAccountService.distributionOil(userId, tenantUserId,
                        tempVirtualOilFee, String.valueOf(orderId),
                        EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP,
                        tenantId, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1,distributionType,user);
                Long tempNoPayOil = (Long) resultMap.get("noPayOil");
                Long noRebateOil = (Long) resultMap.get("noRebateOil");
                Long noCreditOil = (Long) resultMap.get("noCreditOil");
                if (tempNoPayOil == null) {
                    throw new BusinessException("????????????????????????");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("??????????????????");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("??????????????????");
                }
                oos.setSourceAmount(oos.getSourceAmount() + tempNoPayOil - virtualOil);
                oos.setNoPayOil(oos.getNoPayOil() + tempNoPayOil - virtualOil);
                oos.setCreditOil(oos.getCreditOil() + noCreditOil);
                oos.setNoCreditOil(oos.getNoCreditOil() + noCreditOil);
                oos.setRebateOil(oos.getRebateOil() + noRebateOil);
                oos.setNoRebateOil(oos.getNoRebateOil() + noRebateOil);
                orderOilSourceService.saveOrUpdate(oos);
            }
        }
        if (originalVirtualOilFee.longValue() > updatelongVirtualOilFee.longValue() && isUpdateOilAccountType) {//??????
            Long virtualOil = originalVirtualOilFee - updatelongVirtualOilFee;
            //?????????????????????????????????
            Long backUpOil = 0L;
            //??????????????????
            Long payableOil = 0L;
            if (noPayOil >= virtualOil) {
                backUpOil = virtualOil;
            } else {
                backUpOil = noPayOil;
                payableOil = virtualOil - noPayOil;
            }
            BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, backUpOil);
            busiList.add(oilRel);
            if (payableOil > 0) {
                //??????????????????
                BusiSubjectsRel payableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW, payableOil);
                busiList.add(payableOilRel);
                //??????????????????
                BusiSubjectsRel fleetPayableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_LOW, payableOil);
                fleetList.add(fleetPayableOilRel);
            }
            this.dealUpdateOil(sourceList, ol, inParam, isUpdateOilAccountType, isOwnCarUser,inParam1,user);
        }
        if (originalVirtualOilFee.longValue() < updatelongVirtualOilFee.longValue() && isUpdateOilAccountType) {//??????
            Long virtualOil = updatelongVirtualOilFee - originalVirtualOilFee;
            BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP, virtualOil);
            busiList.add(oilRel);
            this.dealUpdateOil(sourceList, ol, inParam, isUpdateOilAccountType, isOwnCarUser,inParam1,user);
        }
        if (originalVirtualOilFee.longValue() == updatelongVirtualOilFee.longValue() && isUpdateOilAccountType) {//???????????????????????????
            this.dealUpdateOil(sourceList, ol, inParam, isUpdateOilAccountType, isOwnCarUser,inParam1,user);
        }
    }

    public Map<String, Object> updateOrderAccountOil(Long userId,long orderId,Long tenantUserId, Long tenantId, int isNeedBill, Long oilFee,
                                                     List<OrderOilSource> sourceList,LoginInfo user)  {


        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
            }
            if (null != orderSchedulerH.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderSchedulerH.getVehicleClass()) {
                isOwnCarUser = true;
            }
        } else {
            if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
                isOwnCarUser = true;
            }
        }

        // ????????????ID???????????????????????????????????????
        OrderAccount tenantAccount = null;
        if (oilFee > 0 && isOwnCarUser) {
            List<BusiSubjectsRel> oilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_UPP);
            for (OrderOilSource source : sourceList) {
                if (orderId != source.getSourceOrderId().longValue() && source.getMatchAmount() != null && source.getMatchAmount() > 0) {
                    tenantAccount = orderAccountService.queryOrderAccount(tenantUserId, source.getVehicleAffiliation(),source.getSourceTenantId(),source.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                    amountFeeSubjectsRel.setAmountFee(source.getMatchAmount());
                    oilList.add(amountFeeSubjectsRel);
                    // ??????????????????
                    List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, oilList);
                    long soNbr = CommonUtil.createSoNbr();
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                            0L, "", tenantAccount, busiSubjectsRelList,
                            soNbr, orderId,"", null, tenantId, null,
                            "", null, tenantAccount.getVehicleAffiliation(),user);
                    oilList.clear();
                }
            }

        }
        Map<String, Object> out = new HashMap<String, Object>();
        return out;
    }

    @Override
    public List busiToOrderNew(ParametersNewDto inParam,List<BusiSubjectsRel> rels,LoginInfo user){
        long userId = inParam.getUserId();
        List results = new ArrayList();
        if (userId == 0L) {
            throw new BusinessException("??????ID????????????!");
        }
        long businessId = inParam.getBusinessId();
        if (businessId == 0L) {
            throw new BusinessException("??????ID????????????!");
        }
        String billId = inParam.getBillId();
        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("????????????????????????!");
        }
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("????????????????????????!");
        }
        //todo ??????
        //?????????????????????????????????????????????????????????
   //     boolean isLock = SysContexts.getLock(this.getClass().getName() + userId + vehicleAffiliation + businessId, 3, 5);
//        if (!isLock)	//???????????????????????????????????????
//            throw new BusinessException("???????????????????????????????????????!");
        //?????????
        if(businessId==EnumConsts.PayInter.PAY_FOR_OIL_CODE){
            results = consumeOilNew.dealToOrderNew(inParam ,rels,user);
        }

        //??????
        if(businessId==EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE){
            results = prePayNew.dealToOrderNew(inParam, rels,user);
        }
        //??????ETC??????(?????????)
        if(businessId==EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH){
            results = oilAndEtcTurnCashNew.dealToOrderNew(inParam, rels,user);
        }
        //??????
        if(businessId==EnumConsts.PayInter.WITHDRAWALS_CODE){
            results = withdrawNew.dealToOrderNew(inParam, rels,user);
        }
        //????????????
        if (businessId == EnumConsts.PayInter.FORCE_ZHANG_PING) {
            results = forceZhangPingNew.dealToOrderNew(inParam, rels,user);
        }
        //??????
        if (businessId == EnumConsts.PayInter.CANCEL_THE_ORDER) {
            results = cancelTheOrder.dealToOrderNew(inParam, rels,user);
        }
        //??????????????????
        if (businessId == EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD) {
            results = pledgeReleaseOilcard.dealToOrderNew(inParam, rels,user);
        }
        //?????????????????????????????????????????????
        if (businessId == EnumConsts.PayInter.BEFORE_PAY_CODE || businessId == EnumConsts.PayInter.UPDATE_THE_ORDER) {
            results = accountOilAllot.dealToOrderNew(inParam, rels,user);
        }
        //??????????????????
        if(businessId == EnumConsts.PayInter.OA_LOAN_AVAILABLE ||businessId == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE ){
            results = payOaLoan.dealToOrderNew(inParam, rels,user);
        }
        //????????????-??????
        if (businessId == EnumConsts.PayInter.DRIVER_EXPENSE_ABLE) {
            results = payExpenseInfo.dealToOrderNew(inParam, rels,user);
        }
        //????????????-??????
        if (businessId == EnumConsts.PayInter.TUBE_EXPENSE_ABLE) {
            results = payVehicleExpenseInfo.dealToOrderNew(inParam, rels,user);
        }
        //????????????
        if (businessId == EnumConsts.PayInter.CAR_DRIVER_SALARY) {
            results = grantSalary.dealToOrderNew(inParam, rels,user);
        }
        //??????????????????(???)
        if (businessId == EnumConsts.PayInter.PAY_FOR_REPAIR) {
            results = payForRepairNew.dealToOrderNew(inParam, rels,user);
        }
        //????????????
        if (businessId == EnumConsts.PayInter.UPDATE_THE_ORDER) {
            results = updateOrder.dealToOrderNew(inParam, rels,user);
        }
        //??????????????????????????????
        if (businessId == EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL) {
            results = rechargeAccountOilAllot.dealToOrderNew(inParam, rels,user);
        }
        //???????????????
        if (businessId == EnumConsts.PayInter.CLEAR_ACCOUNT_OIL) {
            results = clearAccountOil.dealToOrderNew(inParam, rels,user);
        }
        //?????????????????????
        if(businessId == EnumConsts.PayInter.PAYFOR_CODE){
            results = orderFundFlowService.dealToOrderNewCode(inParam, rels,user);
        }
        //?????????
        if (businessId == EnumConsts.PayInter.PAY_ARRIVE_CHARGE) {
            results = orderFundFlowService.dealToOrderNewCharge(inParam, rels,user);
        }
        //???????????????????????????
        if (businessId == EnumConsts.PayInter.ACCOUNT_STATEMENT) {
            results = orderFundFlowService.dealToOrderNew(inParam, rels,user);
        }
        //todo ??????
//        if (isLock) {
//            SysContexts.releaseLockByKey(this.getClass().getName() + userId + vehicleAffiliation + businessId );
//        }
        return results;
    }

    @Override
    public List busiToOrder(ParametersNewDto inParam,List<BusiSubjectsRel> rels,LoginInfo user) {
        Long userId = inParam.getUserId();
        if(userId == null || userId == 0L){
            throw new BusinessException("??????ID????????????!");
        }

        Long businessId = inParam.getBusinessId();
        if(businessId == null || businessId==0L){
            throw new BusinessException("??????ID????????????!");
        }

        String billId = inParam.getBillId();
        if(StringUtils.isEmpty(billId)){
            throw new BusinessException("????????????????????????!");
        }

        String vehicleAffiliation = inParam.getVehicleAffiliation();
        if(StringUtils.isEmpty(vehicleAffiliation)){
            throw new BusinessException("????????????????????????!");
        }

//        //?????????????????????????????????????????????????????????
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + userId + vehicleAffiliation + businessId, 3, 5);
//        if (!isLock)	//???????????????????????????????????????
//            throw new BusinessException("???????????????????????????????????????!");

        List results = new ArrayList();

        /*???????????????????????????????????????-??????*/
        //??????
        if(businessId== EnumConsts.PayInter.BEFORE_PAY_CODE){
            results = beforePay.dealToOrder(inParam, rels, user);
        }
        //?????????
        if(businessId==EnumConsts.PayInter.PAY_FOR_OIL_CODE){
            results = oilConsume.dealToOrder(inParam, rels, user);
        }
        //???????????????
        if(businessId==EnumConsts.PayInter.PAY_FOR_REPAIR){
            results = payForRepair.dealToOrder(inParam, rels, user);
        }
        //??????
        if(businessId==EnumConsts.PayInter.PAY_FINAL){
            results = finalPay.dealToOrder(inParam, rels, user);
        }
        //??????
        if(businessId==EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE){
            results = prePay.dealToOrder(inParam, rels, user);
        }
        //???????????????ETC??????
        if(businessId==EnumConsts.PayInter.OIL_AND_ETC_SURPLUS){
            results = oilEtcTurnCash.dealToOrder(inParam, rels, user);
        }
        //??????ETC??????(?????????)
        if(businessId==EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH){
            results = oilAndEtcTurnCash.dealToOrder(inParam, rels, user);
        }
        //??????????????????
        if(businessId==EnumConsts.PayInter.REPAIR_FUND_TURN_CASH){
            results = repairFundTurnCash.dealToOrder(inParam, rels, user);
        }
        //????????????
        if (businessId == EnumConsts.PayInter.FORCE_ZHANG_PING) {
            results = forceZhangPing.dealToOrder(inParam, rels, user);
        }
        //ETC??????
        if(businessId==EnumConsts.PayInter.CONSUME_ETC_CODE){
            results = etcConsume.dealToOrder(inParam, rels, user);
        }
        //???????????????
        if(businessId==EnumConsts.PayInter.ZHAOSHANG_FEE){
            results = merchantCar.dealToOrder(inParam, rels, user);
        }
        //??????
        if(businessId==EnumConsts.PayInter.RECHARGE_CODE){
            recharge.recharge(inParam, rels, user);
        }
        //??????
        if(businessId==EnumConsts.PayInter.WITHDRAWALS_CODE){
            results = withdraw.dealToOrder(inParam, rels, user);
        }
        //????????????(20170802??????)
        if(businessId == EnumConsts.PayInter.EXCEPTION_FEE){
            results = exceptionCompensation.dealToOrder(inParam, rels, user);
        }
        //??????(?????????????????? /20170806??????)
        if(businessId == EnumConsts.PayInter.BACK_RECHARGE){
            results = backRecharge.dealToOrder(inParam, rels, user);
        }
        //?????????????????????
        /*if(businessId == EnumConsts.PayInter.PAYFOR_CODE){
            BusiToOrderAbstract bto = new PayForCharge();
            results = bto.dealToOrder(inParam, rels);
        }*/

        //ETC??????????????????
        if (businessId == EnumConsts.PayInter.ETC_PAY_CODE) {
            results = etcPayRecord.dealToOrder(inParam, rels, user);
        }

        /*???????????????????????????-??????*/
        //??????????????????
        if(businessId == EnumConsts.PayInter.OIL_ENTITY_VERIFICATION){
            results = oilEntity.dealToOrder(inParam, rels, user);
        }

        //????????????
        if(businessId == EnumConsts.PayInter.OA_LOAN_VERIFICATION){
            results = oaLoanVerification.dealToOrder(inParam, rels, user);
        }
        //??????????????????
        if(businessId == EnumConsts.PayInter.FIXED_FEE_BACKWRITE){
            results = payFixedFee.dealToOrder(inParam, rels, user);
        }
        //???????????? (???????????? - ????????????)
        if(businessId == EnumConsts.PayInter.CAR_DRIVER_SALARY){
            results = paySalary.dealToOrder(inParam, rels, user);
        }
        //????????????
        if(businessId == EnumConsts.PayInter.UPDATE_ORDER_PRICE){
            results = updateOrderPrice.dealToOrder(inParam, rels, user);
        }
        //????????????
        if(businessId == EnumConsts.PayInter.EXCEPTION_FEE_OUT){
            results = exceptionFeeOut.dealToOrder(inParam, rels, user);
        }
        return results;
    }

    @Override
    public List<QueryDriverOilByOrderIdVo> queryDriverOilByOrderId(Long orderId, int userType) {
        List<QueryDriverOilByOrderIdVo> queryDriverOilByOrderIdVos = orderOilSourceMapper.queryDriverOilByOrderId(orderId, userType);
        return queryDriverOilByOrderIdVos;
    }

    @Override
    public OrderPaymentWayOilOut getOrderPaymentWayOil(Long userId, String accessToken, Integer userType) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if (userId == null || userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
        //???????????????????????????
        List<OrderOilSource> list = this.getOrderOilSourceByUserId(userId,userType);
        //???????????????????????????
        List<RechargeOilSource> list1 = rechargeOilSourceService.getRechargeOilSourceByUserId(userId,userType);
        //??????
        Long costOil = 0L;
        //????????????
        Long expenseOil = 0L;
        //??????
        Long contractOil = 0L;

        if (list != null && list.size() > 0) {
            for (OrderOilSource oilSource : list) {
                if (oilSource.getTenantId().longValue() != tenantId.longValue()) {
                    continue;
                }
                Long noPayOil = oilSource.getNoPayOil();
                Long noCreditOil = oilSource.getNoCreditOil();
                Long noRebateOil = oilSource.getNoRebateOil();
                Long orderOil = (noPayOil + noCreditOil + noRebateOil);
                //????????????????????????????????????????????????????????????????????????
                Long orderId = oilSource.getOrderId();
                OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
                if (orderInfoExt != null) {
                    //??????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                        costOil += orderOil;
                    }
                    //????????????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                        expenseOil += orderOil;
                    }
                    //??????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT) {
                        contractOil += orderOil;
                    }
                } else {
                    OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
                    if (orderInfoExtH != null) {
                        //??????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                            costOil += orderOil;
                        }
                        //????????????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                            expenseOil += orderOil;
                        }
                        //??????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT) {
                            contractOil += orderOil;
                        }
                    } else {
                        throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
                    }
                }
            }
        }
        //???????????????????????????????????????
        if (list1 != null && list1.size() > 0) {
            for (RechargeOilSource ros : list1) {
                if (ros.getTenantId().longValue() != tenantId.longValue()) {
                    continue;
                }
                if (ros.getRechargeType() == null
                        || !OrderAccountConst.RECHARGE_ORDER_ACCOUNT_OIL.FLEET_OIL_REBATE.equals(String.valueOf(ros.getRechargeType()))) {
                    long noPayOil = ros.getNoPayOil();
                    long noCreditOil = ros.getNoCreditOil();
                    long noRebateOil = ros.getNoRebateOil();
                    long orderOil = (noPayOil + noCreditOil + noRebateOil);
                    expenseOil += orderOil;
                }
            }
        }

        OrderPaymentWayOilOut out = new OrderPaymentWayOilOut();
        out.setCostOil(costOil);
        out.setExpenseOil(expenseOil);
        out.setContractOil(contractOil);
        return out;
    }

    @Override
    public List<OrderOilSource> getOrderOilSource(OilExcDto oilExcDto) {
        LambdaQueryWrapper<OrderOilSource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderOilSource::getUserId, oilExcDto.getUserId());
        if (StringUtils.isNotEmpty(oilExcDto.getVehicleAffiliation())) {
            queryWrapper.eq(OrderOilSource::getVehicleAffiliation, oilExcDto.getVehicleAffiliation());
        }
        if (StringUtils.isNotBlank(oilExcDto.getVehicleAffiliation())) {
            queryWrapper.eq(OrderOilSource::getOilAffiliation, oilExcDto.getVehicleAffiliation());
        }
        if (oilExcDto.getUserType() > 0) {
            queryWrapper.eq(OrderOilSource::getUserType, oilExcDto.getUserType());
        }
        if (StringUtils.isNotEmpty(oilExcDto.getCreateTime())) {
            queryWrapper.ge(OrderOilSource::getOrderDate, oilExcDto.getCreateTime());
        }
        if (StringUtils.isNotEmpty(oilExcDto.getUpdateTime())) {
            queryWrapper.le(OrderOilSource::getOrderDate, oilExcDto.getUpdateTime());
        }
        queryWrapper.eq(OrderOilSource::getTenantId, oilExcDto.getTenantId());
        queryWrapper.orderByAsc(OrderOilSource::getOrderDate);
        List<OrderOilSource> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public List<RechargeOilSource> getRechargeOilSource(Long userId, Long tenantId, int userType) {
//        Session session = SysContexts.getEntityManager();
//        Criteria ca = session.createCriteria(RechargeOilSource.class);
//        ca.add(Restrictions.eq("userId", userId));
//        ca.add(Restrictions.eq("tenantId", tenantId));
//        if(userType > 0){
//            ca.add(Restrictions.eq("userType", userType));
//        }
//        Disjunction dis = Restrictions.disjunction();
//        dis.add(Restrictions.gt("noPayOil",  0L));
//        dis.add(Restrictions.gt("noCreditOil",  0L));
//        dis.add(Restrictions.gt("noRebateOil",  0L));
//        ca.add(dis);
//        ca.addOrder(Order.asc("createDate"));
        //List<RechargeOilSource> list = ca.list();

        QueryWrapper<RechargeOilSource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("tenant_id",tenantId);
        if(userType > 0){
            queryWrapper.eq("user_type",userType);
        }
        queryWrapper.and(wq->wq.gt("no_pay_oil",0L).or().gt("no_credit_oil",0L).or().gt("no_rebate_oil",0L));
        queryWrapper.orderByAsc("create_time");
        List<RechargeOilSource> list = rechargeOilSourceService.list(queryWrapper);
        return list;
    }

    @Override
    public Long payTurnCash(ConsumeOilFlow c, Long undueAmount,String token) {
        if (c.getUserId() < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (c.getTenantId() < 0) {
            throw new BusinessException("?????????????????????");
        }
        if(undueAmount == 0){
            return null;
        }
        Integer userType = c.getUserType();
        Integer payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;

        //????????????id?????????????????????id
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(c.getTenantId());
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }

        // ??????????????????????????????
        // ????????????id??????????????????
        UserDataInfo user = userDataInfoService.getUserDataInfo(c.getUserId());
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(c.getUserId(), null);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //?????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(c.getUserId());
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        OrderAccount account = orderAccountService.queryOrderAccount(c.getUserId(), c.getVehicleAffiliation(),0L, c.getTenantId(),c.getOilAffiliation(),userType);
        // ????????????ID???????????????????????????????????????
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        long soNbr = CommonUtil.createSoNbr();
        long turnReceSubjectId = EnumConsts.SubjectIds.OIL_TRUN_AVAILABLE_REDUCE;
        long turnPaySubjectId = EnumConsts.SubjectIds.OIL_TRUN_AVAILABLE_ADD;
        long receSubjectId=EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN;//?????????
        long paysubjectId = EnumConsts.SubjectIds.Oil_PAYFOR_PAYABLE_IN;

        //??????????????????
        //		boolean isAutoTransfer = payFeeLimitTF.isMemberAutoTransfer(account.getSourceTenantId());
        long isAutoTransfer = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;//???????????????

        Long adminUser = sysTenantDefService.getTenantAdminUser(c.getTenantId());
        PayoutIntf payOutIntfVirToVir=new PayoutIntf();
        payOutIntfVirToVir.setVehicleAffiliation(c.getVehicleAffiliation());
        payOutIntfVirToVir.setOrderId(0L);
        payOutIntfVirToVir.setUserId(c.getUserId());
        //????????????????????????
        payOutIntfVirToVir.setUserType(userType);
        payOutIntfVirToVir.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //????????????????????????
        payOutIntfVirToVir.setTxnAmt(undueAmount);
        payOutIntfVirToVir.setBusiId(EnumConsts.PayInter.PAYFOR_CODE);//???????????????
        payOutIntfVirToVir.setObjId(StringUtils.isNotBlank(user.getMobilePhone())?Long.valueOf(user.getMobilePhone()):0L);
        payOutIntfVirToVir.setObjType(SysStaticDataEnum.OBJ_TYPE.TURN_CASH + "");
        payOutIntfVirToVir.setPayObjId(adminUser);
        payOutIntfVirToVir.setCreateDate(LocalDateTime.now());
        payOutIntfVirToVir.setWaitBillingAmount(0L);//?????????0
        if(!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(c.getVehicleAffiliation()) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(c.getVehicleAffiliation())){
            payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);//?????????????????????
            payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);

        }else{
            payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);//?????????????????????
            payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
            if(!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(c.getOilAffiliation())){//?????????????????????????????????
                payOutIntfVirToVir.setWaitBillingAmount(undueAmount);
            }
        }
        payOutIntfVirToVir.setBillServiceFee(0L);
        payOutIntfVirToVir.setSubjectsId(receSubjectId);
        payOutIntfVirToVir.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
        payOutIntfVirToVir.setPayTenantId(c.getTenantId());

        if(c!=null){
            LocalDateTime startDate1 = c.getCreateTime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.HOUR_OF_DAY, 2);
            Date endDate1 = calendar1.getTime();

            LocalDateTime startDate2 = c.getGetDate();
            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.HOUR_OF_DAY, 2);
            Date endDate2 = calendar2.getTime();

            if((this.isEffectiveDate(c.getGetDate(), startDate1, endDate1)||this.isEffectiveDate(c.getCreateTime(), startDate2, endDate2))
                    &&(c.getExpireType()==null||c.getExpireType()==0)){//???????????????????????????????????????????????????????????????????????????????????????0???????????????????????????????????????
                isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(),SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_FEE_IS_BILL_405);
            }else{
                isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(),SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401);
                if(c.getFromType()==OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE2){
                    isAutoTransfer=OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;//???????????????????????????????????????????????????????????????
                }
            }
            //????????????????????????????????????
            ConsumeOilFlowExt ext = consumeOilFlowExtService.getConsumeOilFlowExtByFlowId(c.getFlowId());
            payOutIntfVirToVir.setPlateNumber(c.getPlateNumber());
            payOutIntfVirToVir.setBusiCode(c.getOrderId());

        } else {
            throw new BusinessException("?????????????????????");
        }


        if(isTenant){
            payOutIntfVirToVir.setTenantId(sysTenantDef.getId());
        }else{
            payOutIntfVirToVir.setTenantId(-1L);
        }
        payOutIntfVirToVir.setRemark("????????????????????????");
        //????????????????????????????????????
        if (isAutoTransfer==OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) {
            payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
        } else {
            payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
        }

        payOutIntfVirToVir.setOilAffiliation(c.getOilAffiliation());
        payoutIntfService.doSavePayOutIntfVirToVir(payOutIntfVirToVir,token);

        return  payOutIntfVirToVir.getId();
    }


    public void dealUpdateOil(List<OrderOilSource> sourceList,OrderLimit ol,UpdateTheOrderInDto inParam,Boolean isUpdateOilAccountType,
                              Boolean isOwnCarUser,UpdateTheOwnCarOrderInDto inParam1,LoginInfo user) {

        Long userId = null;
        Long originalVirtualOilFee = null;
        Long updatelongVirtualOilFee = null;
        Long orderId = null;
        Long tenantId = null;
        Integer originalOilAccountType = null;
        Integer updateOilAccountType = null;
        Integer originalOilBillType = null;
        Integer updateOilBillType = null;
        Integer isNeedBill = null;
        String vehicleAffiliation = null;
        if (inParam != null) {
            userId = inParam.getUserId();
            originalVirtualOilFee = inParam.getOriginalVirtualOilFee();
            updatelongVirtualOilFee = inParam.getUpdatelongVirtualOilFee();
            orderId = inParam.getOrderId();
            tenantId = inParam.getTenantId();
            originalOilAccountType = inParam.getOriginalOilAccountType();
            updateOilAccountType = inParam.getUpdateOilAccountType();
            originalOilBillType = inParam.getOriginalOilBillType();
            updateOilBillType = inParam.getUpdateOilBillType();
            isNeedBill = inParam.getIsNeedBill();
            vehicleAffiliation = inParam.getVehicleAffiliation();
        }
        if (inParam1 != null) {
            userId = inParam1.getMasterUserId();
            originalVirtualOilFee = inParam1.getOriginalFictitiousOilFee();
            updatelongVirtualOilFee = inParam1.getUpdateFictitiousOilFee();
            orderId = inParam1.getOrderId();
            tenantId = inParam1.getTenantId();
            originalOilAccountType = inParam1.getOriginalOilAccountType();
            updateOilAccountType = inParam1.getUpdateOilAccountType();
            originalOilBillType = inParam1.getOriginalOilBillType();
            updateOilBillType = inParam1.getUpdateOilBillType();
            isNeedBill = inParam1.getIsNeedBill();
            vehicleAffiliation = inParam1.getVehicleAffiliation();
        }

        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long tenantUserId = sysTenantDefService.getSysTenantDef(tenantId).getAdminUser();

        OrderOilSource oos = null;
      //  MatchAmountUtil.matchAmounts(originalVirtualOilFee, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
        this.updateOrderAccountOil(userId, orderId, tenantUserId,tenantId, isNeedBill, originalVirtualOilFee, sourceList,user);
        for (OrderOilSource source : sourceList) {
            if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                oos = source;
                break;
            }
        }
        //???????????????????????????????????????
        Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
        if (oos != null && oos.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && oos.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
            Long oilFee = oos.getMatchAmount() == null ? 0L : oos.getMatchAmount();
            List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(orderId);
            if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                BaseBillInfo bbi = baseBillInfoList.get(0);
                if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
                    recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                } else {
                    bbi.setOil(bbi.getOil() - oilFee);
                    bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                    bbi.setUpdateTime(LocalDateTime.now());
                    baseBillInfoService.saveOrUpdate(bbi);
                    recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                }
            }
        }
        //???????????????
        if (oos != null && oos.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && originalVirtualOilFee > 0) {
            Long tempNoPayOil = oos.getMatchNoPayOil() == null ? 0L : oos.getMatchNoPayOil();
            Long tempNoRebateOil = oos.getMatchNoRebateOil() == null ? 0L : oos.getMatchNoRebateOil();
            Long tempNoCreditOil = oos.getMatchNoCreditOil() == null ? 0L : oos.getMatchNoCreditOil();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("noPayOil", tempNoPayOil);
            map.put("noRebateOil", tempNoRebateOil);
            map.put("noCreditOil", tempNoCreditOil);
            oilSourceRecordService.recallOil(userId, String.valueOf(orderId), tenantUserId,
                    EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, tenantId, map,recallType,user);
        }
        Long virtualOil = updatelongVirtualOilFee - originalVirtualOilFee;
        Long tempVirtualOilFee = virtualOil + noPayOil;
        if (tempVirtualOilFee < 0){
            tempVirtualOilFee = 0L;
        }
        Long tempOil = tempVirtualOilFee;
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2 && updatelongVirtualOilFee > 0 && isOwnCarUser) {
            OrderAccountBalanceDto oilBlaceMap = orderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",tenantId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut = oilBlaceMap.getOa ();
            if (null == orderAccountOut) {
                throw new BusinessException("???????????????????????????");
            }
            //???????????????????????????
            Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
            if(null == canUseOilBalance){
                canUseOilBalance = 0L;
            }
            if (canUseOilBalance.longValue() > 0) {
                if (tempVirtualOilFee > canUseOilBalance.longValue()) {
                    List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId, orderId,
                            tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                            EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                    if (sourceListNew == null || sourceListNew.size() <= 0) {
                        throw new BusinessException("???????????????????????????");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceListNew) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != canUseOilBalance) {
                            throw new BusinessException("??????????????????????????????????????????");
                        }
                    }
                    tempVirtualOilFee -= canUseOilBalance;
                } else {
                    List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(tempVirtualOilFee, tenantUserId,
                            orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                            EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                    if (sourceListNew == null || sourceListNew.size() <= 0) {
                        throw new BusinessException("???????????????????????????");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceListNew) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != tempVirtualOilFee) {
                            throw new BusinessException("??????????????????????????????????????????");
                        }
                    }
                    tempVirtualOilFee = 0L;
                }
            }
        }
        for (OrderOilSource source : sourceList) {
            if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                oos = source;
                break;
            }
        }
        if (oos == null) {
            oos = orderOilSourceService.saveOrderOilSource(userId, orderId, orderId, tempOil, tempOil, 0L, tenantId,LocalDateTime.now(),user.getId(), isNeedBill, vehicleAffiliation,ol.getOrderDate(),ol.getOilAffiliation(),ol.getOilConsumer(),
                    0L,0L,0L,0L,0L,0L,
                    ol.getUserType(),updateOilAccountType,updateOilBillType,user);
        }
        Integer distributionType = null;
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
            distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
        } else if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
            if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//????????????
                distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
            } else if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//??????????????????
                distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2;
            }
        }
        //?????????????????????????????????
        if (tempVirtualOilFee > 0 && updateOilAccountType != OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
            Map<String, Object> resultMap = oilRechargeAccountService.distributionOil(userId, tenantUserId,
                    tempVirtualOilFee, String.valueOf(orderId), EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP,
                    tenantId, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1,distributionType,user);
            Long tempNoPayOil = (Long) resultMap.get("noPayOil");
            Long noRebateOil = (Long) resultMap.get("noRebateOil");
            Long noCreditOil = (Long) resultMap.get("noCreditOil");
            if (tempNoPayOil == null) {
                throw new BusinessException("????????????????????????");
            }
            if (noRebateOil == null) {
                throw new BusinessException("??????????????????");
            }
            if (noCreditOil == null) {
                throw new BusinessException("??????????????????");
            }
            oos.setSourceAmount(tempNoPayOil);
            oos.setNoPayOil(tempNoPayOil);
            oos.setCreditOil(noCreditOil);
            oos.setNoCreditOil(noCreditOil);
            oos.setRebateOil(noRebateOil);
            oos.setNoRebateOil(noRebateOil);
            orderOilSourceService.saveOrUpdate(oos);
        }

        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {//????????????
            oos.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
            oos.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            ol.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            ol.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
            ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            oos.setSourceAmount(tempOil);
            oos.setNoPayOil(tempOil);
            oos.setCreditOil(0L);
            oos.setNoCreditOil(0L);
            oos.setRebateOil(0L);
            oos.setNoRebateOil(0L);
            oos.setMatchNoCreditOil(0L);
            oos.setMatchNoPayOil(0L);
            oos.setMatchNoRebateOil(0L);
        }
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {//???????????????
            oos.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
            oos.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            ol.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            ol.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
            ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
        }
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {//????????????
            oos.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
            oos.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            ol.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            ol.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
            if (updateOilBillType == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//??????
                oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
                ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            }
            if (updateOilBillType == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//??????
                oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
                ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
            }
        }
        orderOilSourceService.saveOrUpdate(oos);
        orderLimitService.saveOrUpdate(ol);
    }

    /**
     * ???????????????????????????[startTime, endTime]????????????????????????????????????
     *
     * @param nowTime ????????????
     * @param startTime ????????????
     * @param endTime ????????????
     * @return
     * @author jqlin
     */
    public boolean isEffectiveDate(LocalDateTime nowTime, LocalDateTime startTime, Date endTime) {
        Date nowTimeDate = Date.from(nowTime.atZone(ZoneId.systemDefault()).toInstant());
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        if (nowTimeDate.getTime() == startDate.getTime()
                || nowTimeDate.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTimeDate);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


}
