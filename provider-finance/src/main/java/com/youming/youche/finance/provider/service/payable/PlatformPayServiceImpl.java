package com.youming.youche.finance.provider.service.payable;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAdvanceExpireInfoService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.api.payable.ICheckPasswordErrService;
import com.youming.youche.finance.api.payable.ICutDataRecordService;
import com.youming.youche.finance.api.payable.IPlatformPayService;
import com.youming.youche.finance.api.tyre.ITyreSettlementBillService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.domain.AdvanceExpireInfo;
import com.youming.youche.finance.domain.payable.CheckPasswordErr;
import com.youming.youche.finance.domain.payable.CutDataRecord;
import com.youming.youche.finance.domain.tyre.TyreSettlementBill;
import com.youming.youche.finance.dto.payable.BalanceJudgmentDto;
import com.youming.youche.finance.vo.payable.BalanceJudgmentVo;
import com.youming.youche.finance.vo.payable.ComfirmPaymentVo;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IAccountDetailsSummaryService;
import com.youming.youche.order.api.order.IBillInfoService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.AccountDetailsSummary;
import com.youming.youche.order.domain.order.BillInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1816;
import static com.youming.youche.conts.OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
import static com.youming.youche.finance.constant.OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0;

/**
 * @ClassName PlatformPayServiceImpl
 * @Description ????????????
 * @Author zag
 * @Date 2022/4/7 15:38
 */
@DubboService(version = "1.0.0")
public class PlatformPayServiceImpl implements IPlatformPayService {

    @DubboReference(version = "1.0.0")
    IPayFeeLimitService payFeeLimitService;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfExpansionService payoutIntfExpansionService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;

    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService busiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IOrderLimitService orderLimitService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @DubboReference(version = "1.0.0")
    IBillInfoService billInfoService;

    @DubboReference(version = "1.0.0")
    IOrderGoodsService orderGoodsService;

    @DubboReference(version = "1.0.0")
    IOrderGoodsHService orderGoodsHService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;

    @DubboReference(version = "1.0.0")
    IOrderFeeExtService orderFeeExtService;

    @DubboReference(version = "1.0.0")
    IOrderFeeExtHService orderFeeExtHService;

    @DubboReference(version = "1.0.0")
    IOrderFeeService orderFeeService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysAttachService sysAttachService;
    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;
    @DubboReference(version = "1.0.0")
    IOrderInfoExtService orderInfoExtService;
    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;
    @DubboReference(version = "1.0.0")
    IBillPlatformService billPlatformService;
    @DubboReference(version = "1.0.0")
    IPayManagerService payManagerService;
    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;
    @Resource
    ITyreSettlementBillService tyreSettlementBillService;
    @DubboReference(version = "1.0.0")
    IAccountDetailsSummaryService accountDetailsSummaryService;
    @DubboReference(version = "1.0.0")
    IOverdueReceivableService overdueReceivableService;
    @DubboReference(version = "1.0.0")
    IOpAccountService iOpAccountService;
    @Resource
    IAdvanceExpireInfoService advanceExpireInfoService;

    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService iBaseBusiToOrderService;


    @Resource
    ICutDataRecordService cutDataRecordService;

    @Resource
    ICheckPasswordErrService checkPasswordErrService;

    @Resource
    IPayoutIntfThreeService iPayoutIntfThreeService;

    @Resource
    LoginUtils loginUtils;

    @Override
    public String doQueryCheckPasswordErr(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long isPass =payFeeLimitService.getAmountLimitCfgVal(loginInfo.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_FEE_IS_BILL_407);
        if(isPass != SysStaticDataEnum.PWD_STATUS.PWD_STATUS1){
            return SysStaticDataEnum.PWD_STATUS.PWD_STATUS1+"";
        }
        CheckPasswordErr checkPasswordErr =checkPasswordErrService.getCheckInfo(loginInfo.getUserInfoId(),SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
        if(checkPasswordErr != null){
            if(checkPasswordErr.getStatus() != null && checkPasswordErr.getStatus() == SysStaticDataEnum.PWD_STATUS.PWD_STATUS1){
                return SysStaticDataEnum.PWD_STATUS.PWD_STATUS1+"";
            }
            else {
                return SysStaticDataEnum.PWD_STATUS.PWD_STATUS2+"";
            }
        }
        else {
            return SysStaticDataEnum.PWD_STATUS.PWD_STATUS2+"";
        }
    }


    @Override
    @Transactional
    public void sure(ComfirmPaymentVo comfirmPaymentVo,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String[] flowIds = comfirmPaymentVo.getFlowId().split(",");
        Map<Long, Map<String,Object>> out = new HashMap<>();
        List<Long> list = new ArrayList<>();
        for(String s:flowIds){
            list.add(Long.valueOf(s));
        }
        out = auditOutService.queryAuditRealTimeOperation(loginInfo.getUserInfoId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE,list,loginInfo.getTenantId());
        for(String string:flowIds){
            Map<String,Object> p = out.get(Long.valueOf(string));
            Boolean b = false;
            if (out != null && p != null && p.get("isFinallyNode") != null) {
                b = (Boolean) p.get("isFinallyNode"); //???????????????????????????
            }
            //????????????
            if(b && comfirmPaymentVo.getChooseResult().equals(SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1)){ //?????????????????????true and ????????????
                // true ??????????????? false ????????????
                AuditCallbackDto judge = auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE,Long.valueOf(string),comfirmPaymentVo.getDesc()
                        ,comfirmPaymentVo.getChooseResult(),null,AuditConsts.OperType.WEB,null,accessToken);
                if(judge !=null && !judge.getIsNext() && comfirmPaymentVo.getChooseResult().equals(SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1)){
                    this.confirmation(comfirmPaymentVo.getAccId(),comfirmPaymentVo.getPayAccId(),comfirmPaymentVo.getIsAutomatic(),Long.valueOf(string),
                            comfirmPaymentVo.getUserType(),comfirmPaymentVo.getFileId(),loginInfo,accessToken);
                    /**
                     * ?????????????????????????????????,??????????????????????????????????????????
                     */
                    if (list.size() == 1) {
                        if (StringUtils.isNotBlank(comfirmPaymentVo.getServiceFee())) {
                            this.confirmationServiceFee(CommonUtils.multiply(comfirmPaymentVo.getServiceFee()), Long.valueOf(string),accessToken);
                        }
                    } else {
                        //if (StringUtils.isNotBlank(serviceFee)) {
                        Map resMap = payoutIntfService.doQueryServiceFee(0L, Long.valueOf(string),accessToken);
                        long OrdServiceFee = DataFormat.getLongKey(resMap,"56kServiceFee");
                        this.confirmationServiceFee(OrdServiceFee, Long.valueOf(string),accessToken);
                        //}
                    }
                }

            }
            else {
                // ??????
                if (comfirmPaymentVo.getChooseResult().equals(SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT2)) {
                    Long busiId = Long.valueOf(string);
                    AuditCallbackDto sure = auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiId, comfirmPaymentVo.getDesc(), comfirmPaymentVo.getChooseResult(), accessToken);
                    if (sure != null && sure.getIsAudit() && sure.getIsAudit() ) {
                        this.fail(sure.getBusiId(), sure.getDesc(), sure.getParamsMap(), accessToken);
                    }
                } else { // ????????????  ????????????????????????
                    Long busiId = Long.valueOf(string);
                    AuditCallbackDto sure = auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiId, comfirmPaymentVo.getDesc(), comfirmPaymentVo.getChooseResult(), accessToken);
                    if (sure != null && !sure.getIsAudit() && sure.getIsAudit()) {
                        this.fail(sure.getBusiId(), sure.getDesc(), sure.getParamsMap(), accessToken);
                    }
                }
            }
        }

        //?????????????????? -- 20190903
        if (comfirmPaymentVo.getChooseResult().equals(SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT2) && list != null && list.size() > 0) {
            List<PayoutIntf> payList = payoutIntfService.getPayoutIntf(list);
            if (payList != null && payList.size() > 0) {
                for (PayoutIntf pay : payList) {
                    if (pay.getSubjectsId().longValue() == EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN) {
                        Date now = new Date();
                        Date date = null;
                        if (StringUtils.isNotBlank(comfirmPaymentVo.getExpireTime())) {
                            try {
                                date = DateUtil.formatStringToDate(comfirmPaymentVo.getExpireTime(), DateUtil.DATETIME_FORMAT);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                throw new BusinessException("???????????????????????????");
                            }
                            if (date.before(now)) {
                                throw new BusinessException("????????????????????????????????????");
                            }
                        }
                        //????????????????????????
                        PayoutIntf payoutIntf = payoutIntfService.getPayoutIntf(pay.getOrderId());
                        if (payoutIntf != null) {
                            this.dealPayoutIntf(payoutIntf, date,accessToken);
                        }
                        this.dealPayoutIntf(pay, date,accessToken);
                    }
                    //???????????????????????????????????????????????????????????????????????????????????????????????????
                    if(pay.getSubjectsId().longValue() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                        this.rejectTireRentalFee(pay,loginInfo,accessToken);
                    }
                }
            }
        }
    }

    @Override
    public BalanceJudgmentDto balanceJudgment(String accessToken, BalanceJudgmentVo balanceJudgmentVo) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if (balanceJudgmentVo.getUserId() == null || balanceJudgmentVo.getUserId() <= 0) {
            balanceJudgmentVo.setUserId(sysTenantDefService.getSysTenantDef(tenantId).getAdminUser());
        }

        if(balanceJudgmentVo.getUserId() == null || balanceJudgmentVo.getUserId() < 0){
            throw new BusinessException("?????????userId!");
        }
        if(!StringUtils.isNotEmpty(balanceJudgmentVo.getPayPinganPayAcctId())){
            throw new BusinessException("?????????????????????????????????!");
        }

        // ????????????????????????????????????
        AccountBankRel receiptRel = accountBankRelService.getAcctNo(balanceJudgmentVo.getPayPinganPayAcctId());
        CmbBankAccountInfo receiptRelInfo = bankAccountService.getByMerchNo(balanceJudgmentVo.getPayPinganPayAcctId());
        if(receiptRel == null && receiptRelInfo == null){
            throw new BusinessException("????????????????????????????????????!");
        }
        if((receiptRel != null && !StringUtils.isNotEmpty(receiptRel.getAcctNo())) && (receiptRelInfo != null && !StringUtils.isNotEmpty(receiptRelInfo.getCertNo()))){
            throw new BusinessException("?????????????????????????????????!");
        }

        BalanceJudgmentDto balanceJudgmentDto = new BalanceJudgmentDto();

        //??????
        if (balanceJudgmentVo.getType().equals(1)) {
            // ??????????????????????????????
            String b=bankAccountService.getBalance((receiptRel != null && StringUtils.isNotEmpty(receiptRel.getPinganCollectAcctId())) ? receiptRel.getPinganPayAcctId() : receiptRelInfo.getMbrNo());
            BankBalanceInfo balanceInfo = new BankBalanceInfo();
            balanceInfo.setBalance(Double.valueOf(b));
            BankBalanceInfo balanceInfo2 =new BankBalanceInfo();
            balanceInfo2.setBalance(0.00);
            // ?????? ??????
            if(balanceInfo.getBalance() >= CommonUtil.getDoubleFormatLongMoney(balanceJudgmentVo.getBalance(), 2)){
                balanceJudgmentDto.setState(1);
                return balanceJudgmentDto;
            }else if ((balanceInfo.getBalance() + balanceInfo2.getBalance()) >=CommonUtil.getDoubleFormatLongMoney(balanceJudgmentVo.getBalance(), 2)) {
                // ???????????? ????????????????????????????????????
                balanceJudgmentDto.setState(2);
                BigDecimal b1 = new BigDecimal(Double.toString(CommonUtil.getDoubleFormatLongMoney(balanceJudgmentVo.getBalance(), 2)));
                BigDecimal b2 = new BigDecimal(Double.toString(balanceInfo.getBalance()));
                balanceJudgmentDto.setAmount(b1.subtract(b2).doubleValue());
                return balanceJudgmentDto;
            }else {
                // ????????????
                balanceJudgmentDto.setState(3);
                return balanceJudgmentDto;
            }
        } else {//??????????????????
            String b=bankAccountService.getBalance((receiptRel != null && StringUtils.isNotEmpty(receiptRel.getPinganCollectAcctId())) ? receiptRel.getPinganPayAcctId() : receiptRelInfo.getMbrNo());//??????????????????
            BankBalanceInfo balanceInfo = new BankBalanceInfo(); balanceInfo.setBalance(Double.valueOf(b));
            BankBalanceInfo balanceInfo2 =new BankBalanceInfo(); balanceInfo2.setBalance(0.00);
            if(balanceInfo.getBalance() >= CommonUtil.getDoubleFormatLongMoney(balanceJudgmentVo.getBalance(), 2)){
                balanceJudgmentDto.setState(1);
                return balanceJudgmentDto;
            }else if ((balanceInfo.getBalance() + balanceInfo2.getBalance()) >=CommonUtil.getDoubleFormatLongMoney(balanceJudgmentVo.getBalance(), 2)) {
                balanceJudgmentDto.setState(2);
                BigDecimal b1 = new BigDecimal(Double.toString(CommonUtil.getDoubleFormatLongMoney(balanceJudgmentVo.getBalance(), 2)));
                BigDecimal b2 = new BigDecimal(Double.toString(balanceInfo.getBalance()));
                balanceJudgmentDto.setAmount(b1.subtract(b2).doubleValue());
                return balanceJudgmentDto;
            }else {
                balanceJudgmentDto.setState(3);
                return balanceJudgmentDto;
            }
        }
    }

    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String accessToken) {
        com.youming.youche.finance.domain.munual.PayoutIntf payoutIntf = iPayoutIntfThreeService.getPayoutIntfByFlowId(busiId, -1L);
        if(payoutIntf==null){
            throw new BusinessException("?????????????????????????????????"+busiId+"???????????????");
        }
        if(!payoutIntf.getTxnType().equals(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE) || !payoutIntf.getIsAutomatic().equals(IS_TURN_AUTOMATIC_0)){
            throw new BusinessException("?????????????????????"+busiId+"??????????????????????????????????????????????????????");
        }

        com.youming.youche.order.domain.order.PayoutIntf payout = new com.youming.youche.order.domain.order.PayoutIntf();
        BeanUtil.copyProperties(payoutIntf, payout);
        //??????????????????
        payoutIntfService.startAuditProcess(payout, accessToken);
    }

    @Transactional(rollbackFor = Exception.class)
    public  void confirmation(String accId, String payAccId, Integer isAutomatic, Long flowId,Integer userType,String fileId,LoginInfo loginInfo,String accessToken){
        PayoutIntf payoutIntf = payoutIntfService.getById(flowId);
        PayoutIntfExpansion payoutIntfExpansion  = payoutIntfExpansionService.getPayoutIntfExpansion(flowId);
//        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper=Wrappers.lambdaQuery();
//        queryWrapper.eq(CmbBankAccountInfo::getMbrNo,payAccId);
//        CmbBankAccountInfo payAccount= bankAccountService.getOne(queryWrapper);
//        queryWrapper=Wrappers.lambdaQuery();
//        queryWrapper.eq(CmbBankAccountInfo::getMbrNo,accId);
//        CmbBankAccountInfo recvAccount=bankAccountService.getOne(queryWrapper);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
        AccountBankRel receiptRel = null;
        AccountBankRel paymentRel = null;
        CmbBankAccountInfo receiptRelInfo = null;
        CmbBankAccountInfo paymentRelInfo = null;
        Boolean bank = true;
        if(payoutIntf == null){
            throw new BusinessException("?????????????????????");
        }
        if(isAutomatic < 0){
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if(payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)){
            throw new BusinessException("???????????????????????????");
        }
        if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)){
            throw new BusinessException("???????????????????????????????????????");
        }
        if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)){
            throw new BusinessException("ETC?????????????????????????????????");
        }
        if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)){
            throw new BusinessException("???????????????????????????????????????");
        }
        if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)){
            throw new BusinessException("??????????????????????????????????????????");
        }
        if(!payoutIntf.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) || !payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)){
            throw new BusinessException("?????????" + flowId + "????????????????????????????????????");
        }
        if(payoutIntf.getRespCode()!= null && payoutIntf.getRespCode().equals(HttpsMain.respCodeInvalid)){
            throw new BusinessException("?????????" + flowId + "??????????????????????????????");
        }
        if("??????????????????????????????????????????".equals(payoutIntf.getAccName()) || "???????????????????????????????????????".equals(payoutIntf.getAccName())){
//            List<String> nameList = new ArrayList<>();
//            nameList.add("??????????????????????????????");
//            nameList.add("??????????????????????????????");
//            nameList.add("???????????????????????????????????????");
//            nameList.add("??????????????????????????????");
//            nameList.add("??????????????????????????????");
//            nameList.add("????????????????????????????????????");

//            if(!nameList.contains(payoutIntf.getPayAccName())){
            throw new BusinessException("56K????????????????????????????????????????????????");
//            }
        }
        if(StringUtils.isNotEmpty(accId)){
            if ((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0) {//56k????????????????????????????????????accid???56???????????????????????????????????????????????????
                accId = payoutIntf.getPinganCollectAcctId();
            }
            receiptRel = accountBankRelService.getAcctNo(accId);
            if (receiptRel == null) {
                receiptRelInfo = bankAccountService.getByMerchNo(accId);
            }
            //???????????????????????????????????????????????????
            if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                getRandomPinganBankInfoOut(payoutIntf.getUserId(),payoutIntf.getPayTenantId());//????????????????????????????????????
            }
        }
        if(StringUtils.isNotEmpty(payAccId)){
            paymentRel = accountBankRelService.getAcctNo(payAccId);
            if (paymentRel == null) {
                paymentRelInfo = bankAccountService.getByMerchNo(payAccId);
            }
        }
        payoutIntf.setUpdateTime(LocalDateTime.now());
        payoutIntf.setUpdateOpId(loginInfo.getUserInfoId());

        payoutIntf.setOpId(loginInfo.getUserInfoId());
        payoutIntf.setIsAutomatic(isAutomatic);
        if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020)) {//???????????????
            TyreSettlementBill tyreSettlementBillVO =tyreSettlementBillService.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode()) ;

            if(isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1)) {//????????????
                tyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE6, "??????????????????",accessToken);
            }else {
                tyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE3, "????????????",accessToken);
            }
        }
        //???????????????
        if(payoutIntf.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER) && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1) && this.isCard(receiptRel)){
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)){
                if(!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("?????????????????????????????????!");
                }
                OilRechargeAccountDetailsFlow oi = oilRechargeAccountDetailsFlowService.getRechargeDetailsFlows(payoutIntf.getBusiCode(),SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2);
                if(oi != null && StringUtils.isNotEmpty(oi.getBankAccName()) &&!paymentRel.getAcctName().equals(oi.getBankAccName())){
                    throw new BusinessException("??????????????????????????????????????????????????????,???????????????????????????"+oi.getBankAccName() +"!");
                }
            }
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) && payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL){
                if(!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("?????????????????????????????????!");
                }
            }
            if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL){
                //????????????????????????????????????
                if(!this.payBill(paymentRel.getAcctName())){
                    throw new BusinessException("?????????????????????,???????????????????????????????????????!");
                }
                //???????????????????????????????????????????????????
                if(payoutIntf.getOrderId() != null && payoutIntf.getOrderId() > 0){
                    String status = this.vehicleOrDriverBill(payoutIntf.getOrderId());
                    if(status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1)){
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    if(status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2)){
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    //???????????????????????????????????????????????????????????????
                    String lookUpName = this.judgebillLookUp(payoutIntf.getOrderId());
                    if(StringUtils.isNotEmpty(lookUpName) && !paymentRel.getAcctName().equals(lookUpName)){
                        throw new BusinessException("?????????????????????????????????????????????????????????,??????????????????????????????"+lookUpName +"!");
                    }
                }
            }
            payoutIntf.setIsTurnAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1);
            Long appendFreight = payoutIntf.getAppendFreight()==null?0L:payoutIntf.getAppendFreight();
            String mes = "["+loginInfo.getName()+"]????????????????????????";
            //????????????
            String time = DateUtil.formatDateByFormat(TimeUtil.getDataTime(),"yyMMddHHmmss");
            Long billServiceFee = payoutIntf.getBillServiceFee()==null?0L: payoutIntf.getBillServiceFee();
            List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
            SysUser driverInfo= sysUserService.getSysOperatorByUserIdOrPhone(payoutIntf.getUserId(), null);
            tenantBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50135, payoutIntf.getTxnAmt()+appendFreight));
            tenantBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50141, billServiceFee));
            List<BusiSubjectsRel> tenantSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_DRIVER , tenantBusiList);
            accountDetailsService.createAccountDetailsNew(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter. PAY_DRIVER ,
                    payoutIntf.getPayObjId(), payoutIntf.getUserId(), driverInfo.getOpName(), tenantSubjectsRelList, payoutIntf.getId(), payoutIntf.getOrderId(), payoutIntf.getPayTenantId(),payoutIntf.getPayUserType(),Long.valueOf(time));
            payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
            //??????????????????0??????????????????????????????????????????????????????????????????0????????????????????????????????????
            if(payoutIntf.getTxnAmt()>0L) {
                //payoutIntf.setRespCode(HttpsMain.respCodeCollection); //?????????????????????   ?????????????????????????????????
                payoutIntf.setRemark("????????????");
                receiptRelInfo = bankAccountService.getByMerchNo(accId);
                payoutIntf.setReceivablesBankAccName(receiptRelInfo.getCertName());
                payoutIntf.setReceivablesBankAccNo(receiptRelInfo.getCertNo());
                payoutIntf.setPinganCollectAcctId(accId);
                payoutIntf.setAccName(receiptRelInfo.getCertName());
                payoutIntf.setAccNo(accId);

                //????????????
                List<BusiSubjectsRel> driverBusiList = new ArrayList<BusiSubjectsRel>();
                SysUser tenantInfo = sysUserService.getSysOperatorByUserIdOrPhone(payoutIntf.getUserId(), null);
                driverBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50136, payoutIntf.getTxnAmt()));
                List<BusiSubjectsRel> driverSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_DRIVER , driverBusiList);
                accountDetailsService.createAccountDetailsNew(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter. PAY_DRIVER ,
                        payoutIntf.getUserId(), payoutIntf.getPayObjId(), tenantInfo.getOpName(), driverSubjectsRelList, payoutIntf.getId(), payoutIntf.getOrderId(), payoutIntf.getPayTenantId(),payoutIntf.getUserType(),Long.valueOf(time));
                bank = false;
                mes = "["+loginInfo.getName()+"]????????????????????????????????????????????????";

            }
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.Payoutchunying,Long.valueOf(flowId),SysOperLogConst.OperType.Update,mes);
        }
        //????????????
        else if(isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)){
            //????????????
            if(StringUtils.isNotEmpty(fileId)){
                saveFileFor_payoutInfo_Expense(payoutIntfExpansion,fileId);
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            String datestr=sdf.format(new Date());
            payoutIntf.setRespCode(HttpsMain.respCodeSuc);
            payoutIntf.setRespMsg("????????????");
            payoutIntf.setRemark("????????????");
            payoutIntf.setCompleteTime(datestr);//OA_LOAN_AVAILABLE
            payoutIntf.setIsTurnAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0);

            OrderScheduler orderScheduler = orderSchedulerService.selectByOrderId(payoutIntf.getOrderId());
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(payoutIntf.getOrderId());

            boolean assignTenantFlag = false;
            if (
                    (orderScheduler != null && orderScheduler.getAppointWay() == com.youming.youche.finance.constant.OrderConsts.AppointWay.APPOINT_TENANT)
                            ||
                            (orderSchedulerH != null && orderSchedulerH.getAppointWay() == com.youming.youche.finance.constant.OrderConsts.AppointWay.APPOINT_TENANT)
            ) {
                assignTenantFlag = true;
            }

            // ???????????????  ?????????????????????
            if (assignTenantFlag) {
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
                payoutIntf.setVerificationState(2);
            } else if(payoutIntf.getBusiId() == EnumConsts.PayInter.TUBE_EXPENSE_ABLE
                    ||  payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE
                    || (payoutIntf.getSubjectsId() == SUBJECTIDS1816 && payoutIntf.getIsDriver() == OrderAccountConst.PAY_TYPE.STAFF)
                    || (payoutIntf.getSubjectsId() == SUBJECTIDS1816 && payoutIntf.getTenantId()!=null&&payoutIntf.getTenantId().equals(payoutIntf.getPayTenantId()))){
                payoutIntfService.doPay200(payoutIntf, null, null,false,loginInfo,accessToken);
                payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                payoutIntf.setVerificationDate(LocalDateTime.now());
                payoutIntf.setPayTime(LocalDateTime.now());
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
                if(payoutIntf.getSubjectsId() == SUBJECTIDS1816) {
                    payManagerService.updatePayManagerState(payoutIntf.getId());
                }
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE,payoutIntf.getId(),null,SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1,null,AuditConsts.OperType.TASK,payoutIntf.getPayTenantId(),accessToken);
            }else if(payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE,payoutIntf.getId(),null,SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1,null,AuditConsts.OperType.TASK,payoutIntf.getPayTenantId(),accessToken);
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.recharge);
            }
            else {
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.recharge);
            }

            String mes = "["+loginInfo.getName()+"]??????????????????";
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.Payoutchunying,Long.valueOf(flowId), SysOperLogConst.OperType.Update, mes);
        }
        //????????????
        else{
            if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                if((receiptRel == null && receiptRelInfo == null) || (paymentRel == null && paymentRelInfo == null)){
                    throw new BusinessException("??????????????????????????????????????????????????????????????????!");
                }
                if(
                        (
                                (receiptRel == null || !StringUtils.isNotEmpty(receiptRel.getAcctNo()))
                                        &&
                                        (receiptRelInfo == null || !StringUtils.isNotEmpty(receiptRelInfo.getCertNo()))
                        )
                                ||
                                (
                                        (paymentRel == null || !StringUtils.isNotEmpty(paymentRel.getAcctNo()))
                                                &&
                                                (paymentRelInfo == null || !StringUtils.isNotEmpty(paymentRelInfo.getCertNo()))
                                )
                ) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????!");
                }
                //if(!payoutIntf.getUserId().equals(receiptRel.getUserId()) || !payoutIntf.getPayObjId().equals(paymentRel.getUserId())){
                if((receiptRel != null && !payoutIntf.getUserId().equals(receiptRel.getUserId()) && (receiptRelInfo != null && !payoutIntf.getUserId().equals(receiptRelInfo.getUserId())))
                        || (paymentRel != null && !payoutIntf.getPayObjId().equals(paymentRel.getUserId()) && (paymentRelInfo != null && !payoutIntf.getPayObjId().equals(paymentRelInfo.getUserId())))){
                    throw new BusinessException("??????????????????????????????????????????????????????!");
                }
            }

            //??????????????????????????????
            if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL){
                if(!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("???????????????????????????????????????!");
                }
            }
            //??????????????????????????????????????????
            if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                    && (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER
                    || userType == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER || userType == SysStaticDataEnum.USER_TYPE.BILL_SERVER_USER)){
                if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("???????????????????????????????????????????????????!");
                }
            }
            //????????????
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN)){
                if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("???????????????????????????????????????!");
                }
            }
            //???????????????||?????? || ?????? ????????????????????????
            if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                    && (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER || userType == SysStaticDataEnum.USER_TYPE.DRIVER_USER
                    || userType == SysStaticDataEnum.USER_TYPE.ADMIN_USER || userType == SysStaticDataEnum.USER_TYPE.RECEIVER_USER)){
                if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                    if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)){
                        throw new BusinessException("??????????????????????????????||??????||?????? || ?????? ??????????????????!");
                    }
                }
            }
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB)){
                if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL){
                    if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                        throw new BusinessException("????????????????????????????????????????????????????????????!");
                    }
                }
                else {
                    if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)){
                        throw new BusinessException("????????????????????????????????????????????????????????????!");
                    }
                }
            }
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)){
                if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("ETC??????????????????????????????!");
                }
            }
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE) && sysTenantDef.getActualController() != null){
                if(receiptRel.getAcctName().equals(sysTenantDef.getActualController())){
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????????????????!");
                }
            }
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)){
                if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("????????????????????????????????????!");
                }
                OilRechargeAccountDetailsFlow oi = oilRechargeAccountDetailsFlowService.getRechargeDetailsFlows(payoutIntf.getBusiCode(),SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2);
                if(oi != null && StringUtils.isNotEmpty(oi.getBankAccName()) &&!paymentRel.getAcctName().equals(oi.getBankAccName())){
                    throw new BusinessException("??????????????????????????????????????????????????????,???????????????????????????"+oi.getBankAccName() +"!");
                }
            }
            if(payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) && payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL){
                if(!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)){
                    throw new BusinessException("????????????????????????????????????!");
                }
            }
            //????????????????????????????????????????????????5W????????????????????????
            if(payoutIntf.getBusiId() == EnumConsts.PayInter.TUBE_EXPENSE_ABLE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.PAY_MANGER
                    ||	payoutIntf.getBusiId() == EnumConsts.PayInter.DRIVER_EXPENSE_ABLE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE){
                String amountFee = (String) sysCfgService.getCfgVal(SysStaticDataEnum.JUDEGE_AMOUNT.AMOUNT_FEN, 0, String.class);
                long amountFeeFen = Long.parseLong(amountFee);
                if(payoutIntf.getTxnAmt() > amountFeeFen){
//                    boolean flg  = withdrawalsTF.JudgeAmount(receiptRel.getPinganCollectAcctId());
//                    if(!flg){
//                        throw new BusinessException("????????????!??????????????????5???,???????????????,??????????????????????????????????????????,???????????????????????????????????????!");
//                    }
                }
            }
            if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL){
                //????????????????????????????????????
                if(!this.payBill(paymentRel.getAcctName())){
                    throw new BusinessException("?????????????????????,???????????????????????????????????????!");
                }
                //???????????????????????????????????????????????????
                if(payoutIntf.getOrderId() != null && payoutIntf.getOrderId() > 0){
                    String status = this.vehicleOrDriverBill(payoutIntf.getOrderId());
                    if(status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1)){
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    if(status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2)){
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    //???????????????????????????????????????????????????????????????
                    String lookUpName = this.judgebillLookUp(payoutIntf.getOrderId());
                    if(StringUtils.isNotEmpty(lookUpName) && !paymentRel.getAcctName().equals(lookUpName)){
                        throw new BusinessException("?????????????????????????????????????????????????????????,??????????????????????????????"+lookUpName +"!");
                    }
                }
            }
            payoutIntf.setIsTurnAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1);
            payoutIntf.setRemark("????????????");
            String mes = "["+loginInfo.getName()+"]????????????????????????";
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.Payoutchunying,Long.valueOf(flowId), SysOperLogConst.OperType.Update, mes);
        }

        if(paymentRel != null){
            if(paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)){
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);
            }
            else{
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);
            }
            payoutIntf.setPayBankAccName(paymentRel.getAcctName());
            payoutIntf.setPayBankAccNo(paymentRel.getAcctNo());
            payoutIntf.setPayAccName(paymentRel.getAcctName());
            payoutIntf.setPayAccNo(paymentRel.getPinganPayAcctId());
        }

        if (paymentRel == null && paymentRelInfo != null) {
            payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);
            payoutIntf.setPayBankAccName(paymentRelInfo.getCertName());
            payoutIntf.setPayBankAccNo(paymentRelInfo.getCertNo());
            payoutIntf.setPayAccName(paymentRelInfo.getCertName());
            payoutIntf.setPayAccNo(paymentRelInfo.getMbrNo());
        }

        //???????????????(?????????) --??????  ??????????????????   ???????????????   ETC??????????????????????????? 56k???????????????
        //if(receiptRel != null && !payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE)
        //        && !payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302 )){
        if((receiptRel != null || receiptRelInfo != null) && !payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE)
                && !payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302 )){

            if (receiptRel != null) {
                payoutIntf.setReceivablesBankAccName(receiptRel.getAcctName());
                payoutIntf.setReceivablesBankAccNo(receiptRel.getAcctNo());
                payoutIntf.setPinganCollectAcctId(receiptRel.getPinganCollectAcctId());
                if (!payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)) {
                    payoutIntf.setAccName(receiptRel.getAcctName());
                    payoutIntf.setAccNo(receiptRel.getPinganCollectAcctId());
                    if (receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                        payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
                    } else {
                        payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT);
                    }
                }
            }

            if (receiptRel == null && receiptRelInfo != null) {
                payoutIntf.setReceivablesBankAccName(receiptRelInfo.getCertName());
                payoutIntf.setReceivablesBankAccNo(receiptRelInfo.getCertNo());
                payoutIntf.setPinganCollectAcctId(accId);
                if (!payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)) {
                    payoutIntf.setAccName(receiptRelInfo.getCertName());
                    payoutIntf.setAccNo(receiptRelInfo.getMerchNo());
                    payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }
        }
        //????????????????????????????????????????????????????????????
        if(payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL && bank){
            if(!payoutIntfService.judge(payoutIntf,payoutIntfExpansion)){
                Map map = payoutIntfService.judgeName(payoutIntf,payoutIntfExpansion);
                String payBankAccName = DataFormat.getStringKey(map, "payBankAccName");
                String receivablesBankAccName = DataFormat.getStringKey(map, "receivablesBankAccName");
                if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                    if(StringUtils.isEmpty(receivablesBankAccName)){
                        throw new BusinessException("????????????[" +payoutIntf.getId()  + "]????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????:["+payBankAccName + "]" );
                    }else {
                        throw new BusinessException("????????????[" +payoutIntf.getId()  + "]??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????:["+payBankAccName + "]" + "?????????????????????[" + receivablesBankAccName + "]");
                    }
                }
            }
        }
        //?????????????????????????????????
        payoutIntfService.update(payoutIntf);
        try {
            //??????????????????????????????
            if(payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE || payoutIntf.getBusiId() ==  EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE) {
                OverdueReceivable overdueReceivable = new OverdueReceivable();
                if (payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE) {
                    //????????????
                    overdueReceivable.setType(2);
                } else if (payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE) {
                    //????????????
                    overdueReceivable.setType(3);
                }
                overdueReceivable.setOrderId(payoutIntf.getOrderId());
                overdueReceivable.setDependTime(payoutIntfExpansion.getDependTime());
                overdueReceivable.setSourceRegion(payoutIntfExpansion.getSourceRegion());
                overdueReceivable.setDesRegion(payoutIntfExpansion.getDesRegion());
                OrderGoods orderGoods = orderGoodsService.getOrderGoods(payoutIntf.getOrderId());
                if (orderGoods == null || orderGoods.getOrderId() == null) {
                    overdueReceivable.setName(orderGoods.getCompanyName());
                } else {
                    OrderGoodsH orderGoodsH = orderGoodsHService.getCustomNumberH(orderGoods.getOrderId());
                    if (orderGoodsH != null && orderGoodsH.getOrderId() != null) {
                        overdueReceivable.setName(orderGoodsH.getCompanyName());
                    }
                }
                overdueReceivable.setPaid(0L);
                overdueReceivable.setTxnAmt(payoutIntf.getTxnAmt() == null ? 0 : payoutIntf.getTxnAmt());
                overdueReceivable.setPayConfirm(0);
                overdueReceivable.setBusinessNumber(payoutIntfExpansion.getBusiCode());
                overdueReceivable.setTenantId(loginInfo.getTenantId());
                if(payoutIntf.getUserId() != null) {
                    overdueReceivable.setAdminUserId(payoutIntf.getUserId());
                }
                overdueReceivableService.save(overdueReceivable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Boolean isCard(AccountBankRel accountBankRel){
        if(accountBankRel == null){
            return true;
        }
        if(StringUtils.isBlank(accountBankRel.getAcctNo())){
            return true;
        }
        return false;
    }

    public void  confirmationServiceFee(long billServiceAmount,long flowId,String accessToken){
        LoginInfo loginInfo = loginUtils.get(accessToken);
        long tenantId  = loginInfo.getTenantId();
        PayoutIntf payout = payoutIntfService.get(flowId);
        //?????? ????????????
        if(StringUtils.isBlank(payout.getVehicleAffiliation()) || Long.valueOf(payout.getVehicleAffiliation()) <= 10L
                || payout.getIsAutomatic() ==null || payout.getIsAutomatic().intValue() != 1){
            return ;
        }
        /**
         * ??????????????????????????????
         */
        long fee = getPayoutIntfBySubFee(payout.getOrderId());
        long cash = 0;
        long openUserId = Long.valueOf(payout.getVehicleAffiliation());
        if(openUserId <= 10 ){
            return;
        }

        if(payout.getOrderId() == null){
            return ;
        }
        long orderId = payout.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(payout.getOrderId());
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderFee orderFee = orderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);

        //???????????????
        cash = payoutIntfService.getPayoutIntfBySubFee(payout.getOrderId());

        // if(fee == cash ){
        //??????????????????
        Map resMap = payoutIntfService.doQueryServiceFee(fee, flowId,loginInfo);
        long serviceFee= DataFormat.getLongKey(resMap,"56kServiceFee");

        //??????????????????
        long payervice = payoutIntfService.getPayoutIntfServiceFee(payout.getOrderId());
        if(serviceFee > payervice){
            billServiceAmount = serviceFee - payervice;
        }
        // }
        if(payout.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB
                || payout.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB ){
            return;
        }
        long userId = Long.valueOf(payout.getVehicleAffiliation());//?????????
        String vehicleAffiliation = String.valueOf(userId);
        String oilAffiliation = String.valueOf(userId);

        long receSubjectsId = EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB;
        String sysPre = billPlatformService.getPrefixByUserId(userId);
        String sysParame56K = sysStaticDataService.getSysStaticDatas(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K);
        boolean is56K = false;
        if(sysPre.equals(sysParame56K)){//56K??????
            is56K=true;
        }

        if(is56K){
            receSubjectsId=EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB;
        }

        //????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }

        long soNbr = CommonUtil.createSoNbr();
        long tenantUserId = payout.getPayObjId();//?????????
        // SysOperator tenantSysOperator = operatorSV.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null);

        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.SERVICE_USER);

        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // ???????????????
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(receSubjectsId, billServiceAmount);
        busiList.add(amountFeeSubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PLATFORM_BILL_SERVICE_FEE, busiList);
        // ????????????????????????????????????????????????
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PLATFORM_BILL_SERVICE_FEE,sysOperator.getUserInfoId(), sysOperator.getOpName(), account, busiSubjectsRelList, soNbr, flowId,
                sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        if(billServiceAmount > 0 ) {
            int isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
            PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, billServiceAmount, -1L, vehicleAffiliation, 0L,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.PLATFORM_BILL_SERVICE_FEE, receSubjectsId, oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER, SysStaticDataEnum.USER_TYPE.SERVICE_USER, 0L,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("?????????????????????");
            if (isTenant) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                payoutIntf.setTenantId(sysTenantDef.getId());
            }
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
            }
            payoutIntf.setBusiCode(String.valueOf(payout.getBusiCode()));
            payoutIntf.setOrderId(payout.getOrderId());

            payoutIntfService.doSavePayOutIntfVirToVirNew(payoutIntf,payout,accessToken);

            SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.applyOpenBill;//????????????
            sysOperLogService.saveSysOperLog(loginInfo,busiCode, payoutIntf.getId(), SysOperLogConst.OperType.Add, "?????????????????????");
        }
        //aob.setApplyState(OrderAccountConst.APPLY_OPEN_BILL.APPLY_STATE2);
        /*if (deductionList != null && deductionList.size() > 0) {
            for (ApplyOpenBill apply : deductionList) {
                apply.setUpdateDate(date);
                apply.setApplyState(OrderAccountConst.APPLY_OPEN_BILL.APPLY_STATE2);
                billManageSV.saveOrUpdate(apply);
            }
        }*/
    }

    public long getPayoutIntfBySubFee(Long orderId) {
        List<Long> subjectsIds = new ArrayList<Long>();
		/*subjectsIds.add(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB);
		subjectsIds.add(EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB);
		subjectsIds.add(EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN);*/
        subjectsIds.add(EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB);
        subjectsIds.add(EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB);
        LambdaQueryWrapper<PayoutIntf> queryWrapper=Wrappers.lambdaQuery();
        queryWrapper.eq(PayoutIntf::getOrderId,orderId)
                .notIn(PayoutIntf::getSubjectsId,subjectsIds)
                .eq(PayoutIntf::getIsAutomatic,AUTOMATIC1)
                .eq(PayoutIntf::getTxnType,"200");
        queryWrapper.and((wrapper->{
            wrapper.isNull(PayoutIntf::getRespCode)
                    .or()
                    .ne(PayoutIntf::getRespCode,"5");
        }));

        //?????????????????????
        List<PayoutIntf> intfList = payoutIntfService.list(queryWrapper);
        long ordTotalFee = 0;
        if(intfList!=null && intfList.size() > 0 ){
            for(PayoutIntf intf : intfList){
                ordTotalFee  += intf.getTxnAmt();
            }
        }
        return ordTotalFee;
    }

    public void dealPayoutIntf(PayoutIntf  pay,Date expireDate,String accessToken){
        if (pay == null ) {
            throw new BusinessException("????????????????????????");
        }
        if (pay.getSubjectsId().longValue() != EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN
                && pay.getSubjectsId().longValue() != EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN) {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        if (pay.getRespCode() != null) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        //TODO ????????????????????????
        //boolean isLock = SysContexts.getLock(this.getClass().getName() + "dealPayoutIntf" + pay.getFlowId(), 3, 5);
        boolean isLock =true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        LoginInfo baseUser = loginUtils.get(accessToken);
        Date date = new Date();
        CutDataRecord driverRecord = new CutDataRecord();
        CutDataRecord fleetRecord = new CutDataRecord();
        long amount = (pay.getTxnAmt() == null ? 0L :  pay.getTxnAmt());
        long billServiceFee = (pay.getBillServiceFee() == null ? 0L : pay.getBillServiceFee());
        String vehicleAffiliation = pay.getVehicleAffiliation();
        Long tenantId = pay.getPayTenantId();

        SysUser sysOperator=sysUserService.getSysOperatorByUserId(pay.getUserId());
        if (sysOperator == null) {
            throw new BusinessException("????????????id???" + pay.getUserId() + "????????????????????????!");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(pay.getPayObjId());
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????id???" + pay.getPayObjId() + "????????????????????????!");
        }
        //???????????????
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(pay.getUserId(), pay.getOrderId(),-1);
        if (ol == null) {
            throw new BusinessException("????????????id???" + pay.getUserId() + " ????????????" + pay.getOrderId() + "??????????????????");
        }
        ol.setNoPayCash(ol.getNoPayCash() - amount);
        ol.setNoWithdrawCash(ol.getNoWithdrawCash() - amount);
        ol.setOrderFinal(ol.getOrderFinal() + amount);
        ol.setNoPayFinal(ol.getNoPayFinal() + amount);
        ol.setOrderCash(ol.getOrderCash() - amount);
        ol.setMarginTurn(ol.getMarginTurn() - amount);
        ol.setFianlSts(0);
        ol.setExpireType(null);
        if (expireDate != null) {
            ol.setFinalPlanDate(DateUtil.asLocalDateTime(expireDate));
        } else {
            ol.setFinalPlanDate(DateUtil.asLocalDateTime(DateUtil.addDate(date, 10)));
        }
        ol.setUpdateDate(DateUtil.asLocalDateTime(date));
        ol.setStsNote("???????????????_??????????????????");
        orderLimitService.saveOrUpdate(ol);
        //      IAccountStatementSV accountStatementSV = (IAccountStatementSV) SysContexts.getBean("accountStatementSV");
        List<AccountDetailsSummary> accountDetailsSummaryList = accountDetailsSummaryService.doQueryPaidPayment(ol.getOrderId(), tenantId, ol.getUserId(), ol.getUserType());
        if (accountDetailsSummaryList != null && accountDetailsSummaryList.size() > 0) {
            Date startTime = DateUtil.asDate(pay.getCreateDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            calendar.add(Calendar.MINUTE, -1);
            startTime = calendar.getTime();
            calendar.add(Calendar.MINUTE, 2);
            Date endTime = calendar.getTime();
            for (AccountDetailsSummary accountDetailsSummary : accountDetailsSummaryList) {
                if (com.youming.youche.commons.util.DateUtil.isEffectiveDate(DateUtil.asDate(accountDetailsSummary.getCreateTime()), startTime, endTime)
                        && accountDetailsSummary.getAmount().longValue() == pay.getTxnAmt().longValue()) {
                    accountDetailsSummaryService.removeById(accountDetailsSummary.getId());
                }
            }
        }

        //??????
        OrderAccount driverAccount = orderAccountService.queryOrderAccount(pay.getUserId(), pay.getVehicleAffiliation(), 0L, pay.getPayTenantId(), pay.getOilAffiliation(), pay.getUserType());
        long receiveOver = driverAccount.getReceivableOverdueBalance();
        long marginBalance = driverAccount.getMarginBalance();
		/*driverAccount.setMarginBalance(marginBalance + amount);
		driverAccount.setReceivableOverdueBalance(receiveOver - amount);*/
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.MARGIN_TURN_CASH_BACK_4015, amount);
        busiList.add(amountFeeSubjectsRel);
        BusiSubjectsRel marginRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.MARGIN_TURN_CASH_BACK_4016, amount);
        busiList.add(marginRel);

        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, busiList);
        // ????????????????????????????????????????????????
        Long soNbr = CommonUtil.createSoNbr();
        OrderResponseDto param = new OrderResponseDto();
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAYFOR_CODE,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), driverAccount, busiSubjectsRelList, soNbr, ol.getOrderId(),
                sysOperator.getOpName(), null, tenantId, null, "", param, vehicleAffiliation, baseUser);

        driverRecord.setCreateTime(LocalDateTime.now());
        driverRecord.setUpdateTime(LocalDateTime.now());
        driverRecord.setType(2);
        driverRecord.setNote("????????????????????????");
        driverRecord.setFlowIdOne(pay.getId());
        driverRecord.setFlowIdTwo(driverAccount.getId());
        driverRecord.setDealState(OrderAccountConst.TASK_STATE.STATE_SUCCESS);
        driverRecord.setTenantId(pay.getPayTenantId());
        driverRecord.setOpId(baseUser.getId());
        driverRecord.setDealTime(LocalDateTime.now());
        driverRecord.setDealRemark("??????");
        orderAccountService.saveOrUpdate(driverAccount);
        cutDataRecordService.saveOrUpdate(driverRecord);

        //??????
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(pay.getPayObjId(), pay.getVehicleAffiliation(), 0L, pay.getPayTenantId(), pay.getOilAffiliation(), pay.getPayUserType());
        long payOver = fleetAccount.getPayableOverdueBalance();
        //fleetAccount.setPayableOverdueBalance(payOver - amount - billServiceFee);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.MARGIN_TURN_CASH_BACK_4017, amount + billServiceFee);
        fleetBusiList.add(payableOverdueRel);

        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, fleetBusiList);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAYFOR_CODE,
                sysOperator.getUserInfoId(), sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, ol.getOrderId(),
                tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, baseUser);
        fleetRecord.setCreateTime(LocalDateTime.now());
        fleetRecord.setUpdateTime(LocalDateTime.now());
        fleetRecord.setType(2);
        fleetRecord.setNote("????????????????????????");
        fleetRecord.setFlowIdOne(pay.getId());
        fleetRecord.setFlowIdTwo(fleetAccount.getId());
        fleetRecord.setDealState(OrderAccountConst.TASK_STATE.STATE_SUCCESS);
        fleetRecord.setTenantId(pay.getPayTenantId());
        fleetRecord.setOpId(baseUser.getId());
        fleetRecord.setDealTime(LocalDateTime.now());
        fleetRecord.setDealRemark("??????");
        orderAccountService.saveOrUpdate(fleetAccount);
        cutDataRecordService.saveOrUpdate(fleetRecord);

        //??????????????????
        pay.setRespCode(HttpsMain.respCodeInvalid);
        pay.setRespMsg("????????????????????????");
        pay.setUpdateTime(LocalDateTime.now());
        payoutIntfService.saveOrUpdate(pay);

        AdvanceExpireInfo advance = advanceExpireInfoService.getAdvanceExpireInfoByFlowId(ol.getOrderId(), OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1);
        if (advance != null) {
            advanceExpireInfoService.removeById(advance.getId());
        }
        //??????????????????
        try {
            auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        DateTimeFormatter formatter02 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // ??????????????????
        sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.AdvanceExpire, ol.getOrderId(), SysOperLogConst.OperType.Update, "??????????????????????????????????????????" + DateUtil.formatDate(expireDate));

    }

    public void rejectTireRentalFee(PayoutIntf pay,LoginInfo loginInfo,String accessToken) {
        if (pay == null) {
            throw new BusinessException("????????????????????????");
        }
        if (pay.getSubjectsId().longValue() != EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if (pay.getRespCode() != null) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        //TODO ????????????????????????
        //boolean isLock = SysContexts.getLock(this.getClass().getName() + "rejectTireRentalFee" + pay.getFlowId(), 3, 5);
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }

        Date date = new Date();
        CutDataRecord driverRecord = new CutDataRecord();
        CutDataRecord fleetRecord = new CutDataRecord();
        long amount = (pay.getTxnAmt() == null ? 0L : pay.getTxnAmt());
        String vehicleAffiliation = pay.getVehicleAffiliation();
        Long tenantId = pay.getPayTenantId();

        SysUser sysOperator = sysUserService.getSysOperatorByUserId(pay.getUserId());
        if (sysOperator == null) {
            throw new BusinessException("????????????id???" + pay.getUserId() + "????????????????????????!");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(pay.getPayObjId());
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????id???" + pay.getPayObjId() + "????????????????????????!");
        }

        TyreSettlementBill tyreSettlementBillVO = tyreSettlementBillService.getTyreSettlementBillVOByBusiCode(pay.getBusiCode());
        if(tyreSettlementBillVO == null){
            throw new BusinessException("?????????????????????" + pay.getBusiCode() + "?????????????????????????????????");
        }
        tyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE1, "???????????????????????????",accessToken);

        //?????????????????????
        OrderAccount account = orderAccountService.queryOrderAccount(pay.getUserId(), vehicleAffiliation, 0L, tenantId, pay.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.TIRE_RENTAL_CASH_BACK_4022, amount);
        busiList.add(amountFeeSubjectsRel);

        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, busiList);
        // ????????????????????????????????????????????????
        Long soNbr = CommonUtil.createSoNbr();
        Map<String, Object> param = new HashMap<String, Object>();
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_FOR_REPAIR,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, null);

        driverRecord.setCreateTime(LocalDateTime.now());
        driverRecord.setUpdateTime(LocalDateTime.now());
        driverRecord.setType(2);
        driverRecord.setNote("???????????????????????????");
        driverRecord.setFlowIdOne(pay.getId());
        driverRecord.setFlowIdTwo(account.getId());
        driverRecord.setDealState(OrderAccountConst.TASK_STATE.STATE_SUCCESS);
        driverRecord.setTenantId(pay.getPayTenantId());
        driverRecord.setOpId(loginInfo.getUserInfoId());
        driverRecord.setDealTime(LocalDateTime.now());
        driverRecord.setDealRemark("??????");
        cutDataRecordService.saveOrUpdate(driverRecord);
        orderAccountService.saveOrUpdate(account);
        //??????????????????
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(pay.getPayObjId(), vehicleAffiliation, 0L, tenantId, pay.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.TIRE_RENTAL_CASH_BACK_4023, amount);
        fleetBusiList.add(payableOverdueRel);

        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, fleetBusiList);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_FOR_REPAIR,
                sysOperator.getUserInfoId(), sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
        fleetRecord.setUpdateTime(LocalDateTime.now());
        fleetRecord.setCreateTime(LocalDateTime.now());
        fleetRecord.setType(2);
        fleetRecord.setNote("???????????????????????????");
        fleetRecord.setFlowIdOne(pay.getId());
        fleetRecord.setFlowIdTwo(fleetAccount.getId());
        fleetRecord.setDealState(OrderAccountConst.TASK_STATE.STATE_SUCCESS);
        fleetRecord.setTenantId(pay.getPayTenantId());
        fleetRecord.setOpId(loginInfo.getUserInfoId());
        fleetRecord.setDealTime(LocalDateTime.now());
        fleetRecord.setDealRemark("??????");
        cutDataRecordService.saveOrUpdate(fleetRecord);
        orderAccountService.saveOrUpdate(fleetAccount);
        //??????????????????
        pay.setRespCode(HttpsMain.respCodeInvalid);
        pay.setRespMsg("???????????????????????????");
        pay.setUpdateTime(LocalDateTime.now());
        payoutIntfService.saveOrUpdate(pay);

        //??????????????????
        try {
            auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean payBill(String payName){
        BillInfo billInfo = billInfoService.getBillInfoByLookUp(payName);
        if(billInfo==null){
            return false;
        }
        if(!billInfoService.completeness(billInfo)) {
            return false;
        }
        return true;
    }

    public String judgebillLookUp(Long orderId) {
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderId);
        if(orderFeeExt != null && orderFeeExt.getBillLookUp() != null){
            return orderFeeExt.getBillLookUp();
        }
        if(orderFeeExtH != null && orderFeeExtH.getBillLookUp() != null){
            return orderFeeExtH.getBillLookUp();
        }
        return "";
    }

    public String vehicleOrDriverBill(Long orderId){
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderSchedulerH orderSchedulerH = orderSchedulerService.getOrderSchedulerH(orderId);
        OrderGoods orderGoods = orderGoodsService.getOneGoodInfo(orderId);
        OrderGoodsH orderGoodsH = orderGoodsHService.getOneGoodInfoH(orderId);
        if(orderScheduler != null && orderGoods !=null && orderGoods.getGoodsType() !=null){
            if(!checkCompleteness(orderScheduler.getCarDriverId(),orderGoods.getGoodsType())){
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1;
            }
            if(!checkVehicleCompleteNess(orderScheduler.getPlateNumber(),orderGoods.getGoodsType())){
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2;
            }
        }
        else if(orderSchedulerH != null && orderGoodsH !=null && orderGoodsH.getGoodsType() !=null){
            if(!checkCompleteness(orderSchedulerH.getCarDriverId(),orderGoodsH.getGoodsType())){
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1;
            }
            if(!checkVehicleCompleteNess(orderSchedulerH.getPlateNumber(),orderGoodsH.getGoodsType())){
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2;
            }
        }
        return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE3;
    }


    /**
     * ?????????????????????????????????????????????
     * @param userId
     * @return true:?????????false????????????
     */
    public boolean checkCompleteness(long userId, int goodsType){
        SysStaticData sysStaticData = sysStaticDataService.getSysStaticData("DRIVER_COMPLETENESS", goodsType+"");
        char[] config = sysStaticData.getCodeName().toCharArray();

        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);


        char[] completeness = userDataInfo.getCompleteness().toCharArray();
        if (config.length != completeness.length) {
            throw new BusinessException("????????????");
        }

        for (int index = 0; index < config.length; index++) {
            if (config[index] > completeness[index]) {
                //log.info("======?????????????????????======" + userDataInfo.getCompleteness());
                return false;
            }
        }

        return true;
    }

    /**
     * ???????????????????????????
     * @param plateNumber ?????????
     * @param goodsType ???????????????1-?????????2-?????????
     * @return
     */
    public boolean checkVehicleCompleteNess(String plateNumber, int goodsType) {

        VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getVehicleDataInfo(plateNumber);


        if (vehicleDataInfo == null) {
            return false;
        }

        vehicleDataInfoService.doUpdateVehicleCompleteness(vehicleDataInfo.getId(),vehicleDataInfo.getTenantId());

        if (vehicleDataInfo.getCompleteness() == null) {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }

        String completeNess = "";

        if (vehicleDataInfo.getLicenceType() == null) {
            return false;
        }

        if (vehicleDataInfo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {//??????
            if (goodsType == SysStaticDataEnum.GOODS_TYPE.COMMON_GOODS){
                completeNess = sysStaticDataService.getSysStaticData("VEHICLE_COMPLETENESS", "3").getCodeName();
            }else {
                completeNess = sysStaticDataService.getSysStaticData("VEHICLE_COMPLETENESS", "4").getCodeName();
            }
        } else {//??????
            completeNess = sysStaticDataService.getSysStaticData("VEHICLE_COMPLETENESS", goodsType + "").getCodeName();
        }

        if (StringUtils.isEmpty(completeNess)) {
            throw new BusinessException("????????????????????????????????????????????????");
        }

        char[] config = completeNess.toCharArray();
        char[] completeness = vehicleDataInfo.getCompleteness().toCharArray();

        if (config.length != completeness.length) {
            throw new BusinessException("????????????????????????????????????");
        }

        for (int index = 0; index < config.length; index++) {
            if (config[index] > completeness[index]) {
                //log.info("======?????????????????????======" + vehicleDataInfo.getCompleteness());
                return false;
            }
        }

        return true;
    }

    /**
     * ???????????????????????????
     * @throws Exception
     */
    public void saveFileFor_payoutInfo_Expense(PayoutIntfExpansion payoutIntfExpansion,String strfileId){
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            // ????????????
            if (strfileId != null && !"".equals(strfileId)) {
                String[] files = strfileId.split(",");
                for (int i = 0; i < files.length; i++) {
                    long fileId = Long.parseLong(files[i]);
                    //LambdaQueryWrapper<SysAttach> queryWrapper=Wrappers.lambdaQuery();
                    //queryWrapper.eq(SysAttach::getId,fileId);
                    SysAttach attach =sysAttachService.getAttachByFlowId(fileId);
                    payoutIntfExpansion.setFileId(Long.valueOf(strfileId));
                    if (attach.getFileName().indexOf("jpg") > -1
                            || attach.getFileName().indexOf("gif") > -1
                            || attach.getFileName().indexOf("png") > -1
                            || attach.getFileName().indexOf("bmp") > -1) {
                        payoutIntfExpansion.setFileUrl(CommonUtils.getBigPicUrl(client.getHttpURL(attach.getStorePath())));
                    } else {
                        payoutIntfExpansion.setFileUrl(client.getHttpURL(attach.getStorePath()));
                    }
                    payoutIntfExpansionService.update(payoutIntfExpansion);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PinganBankInfoOutDto getRandomPinganBankInfoOut(Long userId, Long tenantId) {
        //List<AccountBankRel> accountBankRelList = accountBankRelSV.getBankInfo(userId,BANK_TYPE_0);
        List<AccountBankRel> accountBankRelList = accountBankRelService.getCollectAmount(tenantId);//????????????
        if (accountBankRelList != null && accountBankRelList.size() > 0) {
            PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
            int bankSize = accountBankRelList.size();
            Double needBillBankCardQuantityLimit = Double.parseDouble(String.valueOf(sysCfgService.getCfgVal("NEED_BILL_BANK_CARD_QUANTITY_LIMIT", 0, String.class)));
            if (bankSize < needBillBankCardQuantityLimit) {
                throw new BusinessException("??????????????????????????????????????????"+needBillBankCardQuantityLimit+"?????????????????????????????????");
            }
            Random ra =new Random();
            int index = ra.nextInt(bankSize);
            AccountBankRel o = accountBankRelList.get(index);
            if (o.getBankType().equals(BANK_TYPE_0) ) {
                pinganBankInfoOut.setPrivatePinganAcctIdM(o.getPinganCollectAcctId());
                pinganBankInfoOut.setPrivatePinganAcctIdN(o.getPinganPayAcctId());
                pinganBankInfoOut.setPrivateAcctName(o.getAcctName());
                pinganBankInfoOut.setPrivateAcctNo(o.getAcctNo());
                pinganBankInfoOut.setPrivateBankCode(o.getBankName());
            }
            return pinganBankInfoOut;
        } else {
            throw new BusinessException("????????????????????????????????????????????????");
        }
    }

    @Override
    public void stopAutomatic(Long flowId,Integer isNeedBill,String accessToken){
        LoginInfo loginInfo = loginUtils.get(accessToken);
        PayoutIntf intf =payoutIntfService.get(flowId);
        if(intf.getIsTurnAutomatic() != null && intf.getIsTurnAutomatic().equals((OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_2))){
            throw new BusinessException("?????????????????????????????????!");
        }
        intf.setIsTurnAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_2);
        intf.setRemark("????????????????????????");
        String mes = "["+loginInfo.getName()+"]????????????????????????";
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.Payoutchunying,Long.valueOf(flowId), SysOperLogConst.OperType.Update, mes);
        payoutIntfService.update(intf);
    }

    @Override
    public BalanceJudgmentDto babalanceJudgments(Long userId, Integer userType, Long balance, String payAcctId) {
        AccountBankRel receiptRel = null;
        if(userId < 0){
            throw new BusinessException("?????????userId!");
        }
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(payAcctId)){//??????APP ???????????????
            //throw new BusinessException("?????????????????????????????????!");
            receiptRel = accountBankRelService.getAcctNo(payAcctId);
        }else {
            receiptRel = accountBankRelService.getDefaultAccountBankRel(userId,OrderAccountConst.BANK_TYPE.TYPE0,userType);
        }
        if(receiptRel == null){
            throw new BusinessException("????????????????????????????????????!");
        }
        if(!org.apache.commons.lang3.StringUtils.isNotEmpty(receiptRel.getAcctNo())){
            throw new BusinessException("?????????????????????????????????!");
        }
        BankBalanceInfo balanceInfo =iBaseBusiToOrderService.getBankBalanceToUserIdNo(userId, 22,receiptRel.getPinganPayAcctId(),receiptRel.getBindUserType());//????????????????????????
        BankBalanceInfo balanceInfo2 =iBaseBusiToOrderService.getBankBalanceToUserId(userId, 2,receiptRel.getPinganCollectAcctId(),receiptRel.getBindUserType());//???????????????????????????
        BalanceJudgmentDto balanceJudgmentDto = new BalanceJudgmentDto();
        if(balanceInfo.getBalance() >= CommonUtil.getDoubleFormatLongMoney(balance, 2)){
            balanceJudgmentDto.setState(1);
            return balanceJudgmentDto;
        }else if ((balanceInfo.getBalance() + balanceInfo2.getBalance()) >=CommonUtil.getDoubleFormatLongMoney(balance, 2)) {
            balanceJudgmentDto.setState(2);
            BigDecimal b1 = new BigDecimal(Double.toString(CommonUtil.getDoubleFormatLongMoney(balance, 2)));
            BigDecimal b2 = new BigDecimal(Double.toString(balanceInfo.getBalance()));
            balanceJudgmentDto.setAmount(b1.subtract(b2).doubleValue());
            return balanceJudgmentDto;
        }else {
            balanceJudgmentDto.setState(3);
            return balanceJudgmentDto;
        }
    }
}
