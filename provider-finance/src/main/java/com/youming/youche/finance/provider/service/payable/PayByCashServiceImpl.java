package com.youming.youche.finance.provider.service.payable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAdvanceExpireInfoService;
import com.youming.youche.finance.api.IOaFilesService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.api.payable.ICutDataRecordService;
import com.youming.youche.finance.api.payable.IPayByCashService;
import com.youming.youche.finance.api.tyre.ITyreSettlementBillService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.constant.OaLoanData;
import com.youming.youche.finance.domain.AdvanceExpireInfo;
import com.youming.youche.finance.domain.OaFiles;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.domain.payable.CheckPasswordErr;
import com.youming.youche.finance.domain.payable.CutDataRecord;
import com.youming.youche.finance.domain.tyre.TyreSettlementBill;
import com.youming.youche.finance.dto.DoQueryDto;
import com.youming.youche.finance.dto.PayManagerDto;
import com.youming.youche.finance.provider.mapper.munual.PayoutIntfMapper;
import com.youming.youche.finance.provider.mapper.payable.CheckPasswordErrMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
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
import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.PayManager;
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
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.QueryPayManagerDto;
import com.youming.youche.order.util.BeanUtils;
import com.youming.youche.order.vo.QueryPayManagerVo;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.system.api.IBasicConfigurationService;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.SysMagUtil;
import com.youming.youche.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1816;
import static com.youming.youche.order.commons.AuditConsts.AUDIT_CODE.PAY_MANAGER;

/**
 * @ClassName PlatformPayServiceImpl
 * @Description ????????????
 * @Author ??????
 * @Date 2022/4/19 15:38
 */
@DubboService(version = "1.0.0")
public class PayByCashServiceImpl implements IPayByCashService {
    private static final Logger log = LoggerFactory.getLogger(PayByCashServiceImpl.class);

    @Resource
    LoginUtils loginUtils;

    @Resource
    CheckPasswordErrMapper checkPasswordErrMapper;

    @Resource
    PayoutIntfMapper payoutIntfMapper;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfExpansionService payoutIntfExpansionService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;

    @DubboReference(version = "1.0.0")
    IBillInfoService billInfoService;

    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderFeeService feeService;

    @DubboReference(version = "1.0.0")
    IOrderInfoExtService orderInfoExtService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;

    @DubboReference(version = "1.0.0")
    IOrderGoodsService orderGoodsService;

    @DubboReference(version = "1.0.0")
    IOrderGoodsHService orderGoodsHService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService busiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;

    @DubboReference(version = "1.0.0")
    ISysAttachService sysAttachService;

    @DubboReference(version = "1.0.0")
    IPayManagerService payManagerService;

    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @DubboReference(version = "1.0.0")
    IOrderFeeExtService orderFeeExtService;

    @DubboReference(version = "1.0.0")
    IOrderFeeExtHService orderFeeExtHService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;

    @DubboReference(version = "1.0.0")
    IBillPlatformService billPlatformService;

    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;

    @DubboReference(version = "1.0.0")
    IOpAccountService iOpAccountService;

    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsSummaryService accountDetailsSummaryService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    IPayoutIntfThreeService payoutIntfThreeService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    ITyreSettlementBillService tyreSettlementBillService;

    @Resource
    IAdvanceExpireInfoService advanceExpireInfoService;

    @Resource
    ICutDataRecordService cutDataRecordService;

    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @DubboReference(version = "1.0.0")
    IBasicConfigurationService basicConfigurationService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @Resource
    IOaFilesService oaFilesService;
    @Resource
    IOaLoanThreeService oaLoanThreeService;

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;


    @Override
    public void confirmation(String accId, String payAccId, Integer isAutomatic, Long flowId, Integer userType, String fileId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        PayoutIntf payoutIntf = payoutIntfService.getById(flowId);
        PayoutIntfExpansion payoutIntfExpansion = payoutIntfExpansionService.getPayoutIntfExpansion(payoutIntf.getId());
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
        AccountBankRel receiptRel = null;
        AccountBankRel paymentRel = null;
        CmbBankAccountInfo receiptRelInfo = null;
        CmbBankAccountInfo paymentRelInfo = null;
        Boolean bank = true;
        if (payoutIntf == null) {
            throw new BusinessException("?????????????????????");
        }
        if (isAutomatic < 0) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if (payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            throw new BusinessException("???????????????????????????");
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            throw new BusinessException("ETC?????????????????????????????????");
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)
                && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        if (!payoutIntf.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) || !payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
            throw new BusinessException("?????????" + payoutIntf.getId() + "????????????????????????????????????");
        }
        if (payoutIntf.getRespCode() != null && payoutIntf.getRespCode().equals(HttpsMain.respCodeInvalid)) {
            throw new BusinessException("?????????" + payoutIntf.getId() + "??????????????????????????????");
        }
        if ("??????????????????????????????????????????".equals(payoutIntf.getAccName()) || "???????????????????????????????????????".equals(payoutIntf.getAccName())) {
            throw new BusinessException("56K????????????????????????????????????????????????");
        }
        if (StringUtils.isNotEmpty(accId)) {
            if ((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0) {//56k????????????????????????????????????accid???56???????????????????????????????????????????????????
                accId = payoutIntf.getPinganCollectAcctId();
            }
            receiptRel = accountBankRelService.getAcctNo(accId);//
            if (receiptRel == null) {
                receiptRelInfo = bankAccountService.getByMbrNo(accId);
            }
            //???????????????????????????????????????????????????
            if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                //  PayOutIntfUtil.getRandomPinganBankInfoOut(payoutIntf.getUserId(), payoutIntf.getPayTenantId());//????????????????????????????????????
            }
        }
        if (StringUtils.isNotEmpty(payAccId)) {
            paymentRel = accountBankRelService.getAcctNo(payAccId);
            if (paymentRel == null) {
                paymentRelInfo = bankAccountService.getByMbrNo(payAccId);
            }
        }
        payoutIntf.setUpdateTime(LocalDateTime.now());
        payoutIntf.setUpdateOpId(user.getUserInfoId());
        payoutIntf.setOpId(user.getUserInfoId());
        payoutIntf.setIsAutomatic(isAutomatic);
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020)) {//???????????????
//            TyreSettlementBillSV tyreSettlementBillSV = (TyreSettlementBillSV)SysContexts.getBean("tyreSettlementBillSV");
//            ITyreSettlementBillTF tyreSettlementBillTF = (ITyreSettlementBillTF)SysContexts.getBean("tyreSettlementBillTF");
            TyreSettlementBill tyreSettlementBillVO = tyreSettlementBillService.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode());

            if (isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1)) {//????????????
                tyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE6, "??????????????????", accessToken);
            } else {
                tyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE3, "????????????", accessToken);
            }
        }
        //???????????????
        if (payoutIntf.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER) && this.isCard(receiptRel) && isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1)) {
            if (paymentRel == null && paymentRelInfo == null) {
                throw new BusinessException("???????????????????????????????????????????????????!");
            }
            if ((paymentRel != null && !StringUtils.isNotEmpty(paymentRel.getAcctNo())) && (paymentRelInfo != null && !StringUtils.isNotEmpty(paymentRelInfo.getCertNo()))) {
                throw new BusinessException("???????????????????????????????????????????????????!");
            }
            if ((paymentRel != null && !payoutIntf.getPayObjId().equals(paymentRel.getUserId())) && (paymentRelInfo != null && !payoutIntf.getPayObjId().equals(paymentRelInfo.getUserId()))) {
                throw new BusinessException("???????????????????????????????????????!");
            }
            //??????????????????????????????
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                if (!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("???????????????????????????????????????!");
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)) {
                if (!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("?????????????????????????????????!");
                }
                OilRechargeAccountDetailsFlow oi = oilRechargeAccountDetailsFlowService.getRechargeDetailsFlows(payoutIntf.getBusiCode(), SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2);
                if (oi != null && StringUtils.isNotEmpty(oi.getBankAccName()) && !paymentRel.getAcctName().equals(oi.getBankAccName())) {
                    throw new BusinessException("??????????????????????????????????????????????????????,???????????????????????????" + oi.getBankAccName() + "!");
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) && payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                if (!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("?????????????????????????????????!");
                }
            }
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                //????????????????????????????????????
                if (!this.payBill(paymentRel.getAcctName())) {
                    throw new BusinessException("?????????????????????,???????????????????????????????????????!");
                }
                //???????????????????????????????????????????????????
                if (payoutIntf.getOrderId() != null && payoutIntf.getOrderId() > 0) {
                    String status = this.vehicleOrDriverBill(payoutIntf.getOrderId());
                    if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1)) {
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2)) {
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    //???????????????????????????????????????????????????????????????
                    String lookUpName = this.judgebillLookUp(payoutIntf.getOrderId());
                    if (StringUtils.isNotEmpty(lookUpName) && !paymentRel.getAcctName().equals(lookUpName)) {
                        throw new BusinessException("?????????????????????????????????????????????????????????,??????????????????????????????" + lookUpName + "!");
                    }
                }
            }
            payoutIntf.setIsTurnAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1);
            Long appendFreight = payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight();
            String mes = "[" + user.getName() + "]????????????????????????";
            //????????????
            String time = DateUtil.formatDateByFormat(TimeUtil.getDataTime(), "yyMMddHHmmss");
            Long billServiceFee = payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee();
            List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
            SysUser driverInfo = sysUserService.getSysOperatorByUserIdOrPhone(payoutIntf.getUserId(), null);
            tenantBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50135, payoutIntf.getTxnAmt() + appendFreight));
            tenantBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50141, billServiceFee));
            List<BusiSubjectsRel> tenantSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_DRIVER, tenantBusiList);
            accountDetailsService.createAccountDetailsNew(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.PAY_DRIVER,
                    payoutIntf.getPayObjId(), payoutIntf.getUserId(), driverInfo.getName(), tenantSubjectsRelList, payoutIntf.getId(), payoutIntf.getOrderId(), payoutIntf.getPayTenantId(), payoutIntf.getPayUserType(), Long.valueOf(time));
            //??????????????????0??????????????????????????????????????????????????????????????????0????????????????????????????????????
            if (payoutIntf.getTxnAmt() > 0L) {
                payoutIntf.setRemark("????????????");
                receiptRelInfo = bankAccountService.getByMbrNo(accId);
                payoutIntf.setReceivablesBankAccName(receiptRelInfo.getCertName());
                payoutIntf.setReceivablesBankAccNo(receiptRelInfo.getCertNo());
                payoutIntf.setPinganCollectAcctId(receiptRelInfo.getMerchNo());
                payoutIntf.setAccName(receiptRelInfo.getCertName());
                payoutIntf.setAccNo(receiptRelInfo.getMerchNo());
                //????????????
                List<BusiSubjectsRel> driverBusiList = new ArrayList<BusiSubjectsRel>();
                SysUser tenantInfo = sysUserService.getSysOperatorByUserIdOrPhone(payoutIntf.getUserId(), null);
                driverBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50136, payoutIntf.getTxnAmt()));
                List<BusiSubjectsRel> driverSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_DRIVER, driverBusiList);
                accountDetailsService.createAccountDetailsNew(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.PAY_DRIVER,
                        payoutIntf.getUserId(), payoutIntf.getPayObjId(), tenantInfo.getOpName(), driverSubjectsRelList, payoutIntf.getId(), payoutIntf.getOrderId(), payoutIntf.getPayTenantId(), payoutIntf.getUserType(), Long.valueOf(time));
                bank = false;
                mes = "[" + user.getName() + "]????????????????????????????????????????????????";

            }
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.Payoutchunying, Long.valueOf(flowId), SysOperLogConst.OperType.Update, mes, null);
        }
        //????????????
        else if (isAutomatic.equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            //????????????
            if (StringUtils.isNotEmpty(fileId)) {
                saveFileForPayoutInfoExpense(payoutIntfExpansion, fileId);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String datestr = sdf.format(new Date());
            payoutIntf.setRespCode(HttpsMain.respCodeSuc);
            payoutIntf.setRespMsg("????????????");
            payoutIntf.setRemark("????????????");
            payoutIntf.setCompleteTime(datestr);
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

            if (assignTenantFlag) {
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
                payoutIntf.setVerificationState(2);
            } else if (payoutIntf.getBusiId() == EnumConsts.PayInter.TUBE_EXPENSE_ABLE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE
                    || (payoutIntf.getSubjectsId() == SUBJECTIDS1816 && payoutIntf.getIsDriver() == OrderAccountConst.PAY_TYPE.STAFF)
                    || (payoutIntf.getSubjectsId() == SUBJECTIDS1816 && payoutIntf.getTenantId() != null && payoutIntf.getTenantId().equals(payoutIntf.getPayTenantId()))) {
                payoutIntfService.doPay200(payoutIntf, null, null, false, user, accessToken);
                payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                payoutIntf.setVerificationDate(LocalDateTime.now());
                payoutIntf.setPayTime(LocalDateTime.now());
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
                if (payoutIntf.getSubjectsId() == SUBJECTIDS1816) {
                    payManagerService.updatePayManagerState(payoutIntf.getId());
                }
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
                /**
                 *?????????????????????
                 */

            } else if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
                /**
                 *?????????????????????
                 */
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.recharge);
            } else {
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.recharge);
            }
            String mes = "[" + user.getName() + "]??????????????????";
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.Payoutchunying, Long.valueOf(flowId), SysOperLogConst.OperType.Update, mes, null);
        }
        //????????????
        else {
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
                if((receiptRel != null && !payoutIntf.getUserId().equals(receiptRel.getUserId()) && (receiptRelInfo != null && !payoutIntf.getUserId().equals(receiptRelInfo.getUserId())))
                        || (paymentRel != null && !payoutIntf.getPayObjId().equals(paymentRel.getUserId()) && (paymentRelInfo != null && !payoutIntf.getPayObjId().equals(paymentRelInfo.getUserId())))){
                    throw new BusinessException("??????????????????????????????????????????????????????!");
                }
            }

            //??????????????????????????????
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                if (!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("???????????????????????????????????????!");
                }
            }
            //??????????????????????????????????????????
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                    && (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER
                    || userType == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER || userType == SysStaticDataEnum.USER_TYPE.BILL_SERVER_USER)) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("???????????????????????????????????????????????????!");
                }
            }
            //????????????
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN)) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("???????????????????????????????????????!");
                }
            }
            //???????????????||?????? || ?????? ????????????????????????
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                    && (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER || userType == SysStaticDataEnum.USER_TYPE.DRIVER_USER
                    || userType == SysStaticDataEnum.USER_TYPE.ADMIN_USER || userType == SysStaticDataEnum.USER_TYPE.RECEIVER_USER)) {
                if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                    if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                        throw new BusinessException("??????????????????????????????||??????||?????? || ?????? ??????????????????!");
                    }
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB)) {
                if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL) {
                    if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                        throw new BusinessException("????????????????????????????????????????????????????????????!");
                    }
                } else {
                    if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                        throw new BusinessException("????????????????????????????????????????????????????????????!");
                    }
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("ETC??????????????????????????????!");
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE) && sysTenantDef.getActualController() != null) {
                if (receiptRel.getAcctName().equals(sysTenantDef.getActualController())) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????????????????!");
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("????????????????????????????????????!");
                }
                OilRechargeAccountDetailsFlow oi = oilRechargeAccountDetailsFlowService.getRechargeDetailsFlows(payoutIntf.getBusiCode(), SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2);
                if (oi != null && StringUtils.isNotEmpty(oi.getBankAccName()) && !paymentRel.getAcctName().equals(oi.getBankAccName())) {
                    throw new BusinessException("??????????????????????????????????????????????????????,???????????????????????????" + oi.getBankAccName() + "!");
                }
            }
            if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) && payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("????????????????????????????????????!");
                }
            }
            //????????????????????????????????????????????????5W????????????????????????
            if (payoutIntf.getBusiId() == EnumConsts.PayInter.TUBE_EXPENSE_ABLE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.PAY_MANGER
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.DRIVER_EXPENSE_ABLE
                    || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE) {
                String amountFee = (String) sysCfgService.getCfgVal(SysStaticDataEnum.JUDEGE_AMOUNT.AMOUNT_FEN, 0, String.class);
                long amountFeeFen = Long.parseLong(amountFee);
                if (payoutIntf.getTxnAmt() > amountFeeFen) {
                    boolean flg = this.JudgeAmount(receiptRel.getPinganCollectAcctId());
                    if (!flg) {
                        throw new BusinessException("????????????!??????????????????5???,???????????????,??????????????????????????????????????????,???????????????????????????????????????!");
                    }
                }
            }
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                //????????????????????????????????????
                if (!this.payBill(paymentRel.getAcctName())) {
                    throw new BusinessException("?????????????????????,???????????????????????????????????????!");
                }
                //???????????????????????????????????????????????????
                if (payoutIntf.getOrderId() != null && payoutIntf.getOrderId() > 0) {
                    String status = this.vehicleOrDriverBill(payoutIntf.getOrderId());
                    if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1)) {
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2)) {
                        throw new BusinessException("?????????????????????,?????????????????????????????????!");
                    }
                    //???????????????????????????????????????????????????????????????
                    String lookUpName = this.judgebillLookUp(payoutIntf.getOrderId());
                    if (StringUtils.isNotEmpty(lookUpName) && !paymentRel.getAcctName().equals(lookUpName)) {
                        throw new BusinessException("?????????????????????????????????????????????????????????,??????????????????????????????" + lookUpName + "!");
                    }
                }
            }
            payoutIntf.setIsTurnAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1);
            payoutIntf.setRemark("????????????");
            String mes = "[" + user.getName() + "]????????????????????????";
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.Payoutchunying, Long.valueOf(flowId), SysOperLogConst.OperType.Update, mes);
        }

        if (paymentRel != null) {
            if (paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);
            } else {
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
        if ((receiptRel != null || receiptRelInfo != null) && !payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE)
                && !payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)) {
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
                payoutIntf.setPinganCollectAcctId(receiptRelInfo.getMerchNo());
                if (!payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)) {
                    payoutIntf.setAccName(receiptRelInfo.getCertName());
                    payoutIntf.setAccNo(receiptRelInfo.getMerchNo());
                    payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }
        }
        //????????????????????????????????????????????????????????????
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL && bank) {
            if (!payoutIntfService.judge(payoutIntf, payoutIntfExpansion)) {
                Map map = payoutIntfService.judgeName(payoutIntf, payoutIntfExpansion);
                String payBankAccName = DataFormat.getStringKey(map, "payBankAccName");
                String receivablesBankAccName = DataFormat.getStringKey(map, "receivablesBankAccName");
                if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                    if (StringUtils.isEmpty(receivablesBankAccName)) {
                        throw new BusinessException("????????????[" + payoutIntf.getId() + "]????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????:[" + payBankAccName + "]");
                    } else {
                        throw new BusinessException("????????????[" + payoutIntf.getId() + "]??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????:[" + payBankAccName + "]" + "?????????????????????[" + receivablesBankAccName + "]");
                    }
                }
            }
        }
        //?????????????????????????????????
        payoutIntfService.update(payoutIntf);
    }


    @Override
    public void sure(String busiCode, String flowId, String desc, Integer chooseResult, String accId, String payAccId, Integer isAutomatic, Integer userType, String fileId, String expireTime, String serviceFee, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        String[] flowIds = flowId.split(",");
        Map<Long, Map<String, Object>> out = new HashMap<>();
        List<Long> list = new ArrayList<>();
        for (String s : flowIds) {
            list.add(Long.valueOf(s));
        }
        //?????????????????? ?????????
        out = auditOutService.queryAuditRealTimeOperation(baseUser.getUserInfoId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE, list, baseUser.getTenantId());
        for (String string : flowIds) {
            Map<String, Object> p = out.get(Long.valueOf(string));
            Boolean b = false;
            if (out != null && p != null && p.get("isFinallyNode") != null) {
                b = (Boolean) p.get("isFinallyNode"); //???????????????????????????
            }
            //????????????
            if (b && chooseResult.equals(SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1)) { //?????????????????????true and ????????????
                // true ??????????????? false ????????????
                AuditCallbackDto judge = auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, Long.valueOf(string), desc, chooseResult, null, AuditConsts.OperType.WEB, null, accessToken);
                if (judge != null && !judge.getIsNext() && chooseResult.equals(SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT1)) {
                    this.confirmation(accId, payAccId, isAutomatic, Long.valueOf(string), userType, fileId, accessToken);
                    /**
                     * ?????????????????????????????????,??????????????????????????????????????????
                     */
                    if (list.size() == 1) {
                        if (StringUtils.isNotBlank(serviceFee)) {
                            this.confirmationServiceFee(com.youming.youche.util.CommonUtils.multiply(serviceFee), Long.valueOf(string), accessToken);
                        }
                    } else {
                        Map resMap = payoutIntfThreeService.doQueryServiceFee(accessToken, 0L, Long.valueOf(string));
                        long OrdServiceFee = DataFormat.getLongKey(resMap, "56kServiceFee");
                        this.confirmationServiceFee(OrdServiceFee, Long.valueOf(string), accessToken);
                    }

                }
            } else {
                AuditCallbackDto judge = auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, Long.valueOf(string), desc, chooseResult, accessToken);
                if (chooseResult.intValue() != 1) {
                    if (judge != null && judge.getIsAudit()) {
                        fail(judge.getBusiId(), judge.getDesc(), judge.getParamsMap(), accessToken);
                    }
                }
            }
        }
        //?????????????????? -- 20190903
        if (chooseResult == SysStaticDataEnum.CHOOSE_RESULT.CHOOSE_RESULT2 && list != null && list.size() > 0) {
            List<PayoutIntf> payList = payoutIntfService.getPayoutIntf(list);
            if (payList != null && payList.size() > 0) {
                for (PayoutIntf pay : payList) {
                    if (pay.getSubjectsId().longValue() == EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN) {
                        Date now = new Date();
                        Date date = null;
                        LocalDateTime localDateTime = null;
                        if (StringUtils.isNotBlank(expireTime)) {
                            try {
                                date = DateUtil.formatStringToDate(expireTime, DateUtil.DATETIME_FORMAT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (date.before(now)) {
                                throw new BusinessException("????????????????????????????????????");
                            }
                        }
                        //????????????????????????
                        PayoutIntf payoutIntf = payoutIntfService.getPayoutIntf(pay.getOrderId());
                        if (date != null) {
                            localDateTime = DateUtil.asLocalDateTime(date);
                        }
                        if (payoutIntf != null) {
                            this.dealPayoutIntf(payoutIntf, localDateTime, accessToken);
                        }
                        this.dealPayoutIntf(pay, localDateTime, accessToken);
                    }
                    if (pay.getSubjectsId().longValue() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {//???????????????????????????????????????????????????????????????????????????????????????????????????
                        this.rejectTireRentalFee(pay, baseUser, accessToken);
                    }
                }
            }
        }
    }

    @Override
    public boolean payBill(String payName) {
//        IBillInfoTF iBillInfoTF = (IBillInfoTF) SysContexts.getBean("billInfoTF");
        BillInfo billInfo = billInfoService.getBillInfoByLookUp(payName);
        if (billInfo == null) {
            return false;
        }
        if (!billInfoService.completeness(billInfo)) {
            return false;
        }
        return true;
    }

    @Override
    public String vehicleOrDriverBill(Long orderId) {
//        IUserTF iUserTF = (IUserTF) SysContexts.getBean("userTF");
//        IVehicleTF iVehicleTF = (IVehicleTF) SysContexts.getBean("vehicleTF");
//        IOrderSchedulerTF iOrderSchedulerTF = (IOrderSchedulerTF) SysContexts.getBean("orderSchedulerTF");
//        IOrderGoodsTF iOrderGoodsTF = (IOrderGoodsTF) SysContexts.getBean("orderGoodsTF");
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
        OrderGoods orderGoods = orderGoodsService.getOneGoodInfo(orderId);
        OrderGoodsH orderGoodsH = orderGoodsHService.getOneGoodInfoH(orderId);
        if (orderScheduler != null && orderGoods != null && orderGoods.getGoodsType() != null) {
            if (!userDataInfoService.checkCompleteness(orderScheduler.getCarDriverId(), orderGoods.getGoodsType())) {
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1;
            }
            if (!vehicleDataInfoService.checkVehicleCompleteNess(orderScheduler.getPlateNumber(), orderGoods.getGoodsType())) {
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2;
            }
        } else if (orderSchedulerH != null && orderGoodsH != null && orderGoodsH.getGoodsType() != null) {
            if (!userDataInfoService.checkCompleteness(orderSchedulerH.getCarDriverId(), orderGoodsH.getGoodsType())) {
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1;
            }
            if (!vehicleDataInfoService.checkVehicleCompleteNess(orderSchedulerH.getPlateNumber(), orderGoodsH.getGoodsType())) {
                return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2;
            }
        }
        return SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE3;
    }

    @Override
    public boolean JudgeAmount(String accNo) {
        AccountBankRel accountBankRel = accountBankRelService.getAcctNo(accNo);
        if (accountBankRel == null) {
            return false;
        }
        if (!StringUtils.isNotEmpty(accountBankRel.getBranchId())) {
            return false;
        }
        return true;
    }

    /**
     * ??????????????????
     *
     * @param pay
     * @throws Exception
     */
    @Override
    public void dealPayoutIntf(PayoutIntf pay, LocalDateTime expireDate, String accessToken) {
        if (pay == null) {
            throw new BusinessException("????????????????????????");
        }
        if (pay.getSubjectsId().longValue() != EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN
                && pay.getSubjectsId().longValue() != EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN) {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        if (pay.getRespCode() != null) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "dealPayoutIntf" + pay.getFlowId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        LoginInfo baseUser = loginUtils.get(accessToken);
        CutDataRecord driverRecord = new CutDataRecord();
        CutDataRecord fleetRecord = new CutDataRecord();
        long amount = (pay.getTxnAmt() == null ? 0L : pay.getTxnAmt());
        long billServiceFee = (pay.getBillServiceFee() == null ? 0L : pay.getBillServiceFee());
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
        //???????????????
        OrderLimit ol = iOrderLimitService.getOrderLimitByUserIdAndOrderId(pay.getUserId(), pay.getOrderId(), -1);
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
        Date date = new Date();
        if (expireDate != null) {
            ol.setFinalPlanDate(expireDate);
        } else {
            ol.setFinalPlanDate(DateUtil.asLocalDateTime(DateUtil.addDate(date, 10)));
        }
        ol.setUpdateDate(DateUtil.asLocalDateTime(date));
        ol.setStsNote("???????????????_??????????????????");
        iOrderLimitService.saveOrUpdate(ol);
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
        sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.AdvanceExpire, ol.getOrderId(), SysOperLogConst.OperType.Update, "??????????????????????????????????????????" + (expireDate == null ?  "" : expireDate.format(formatter02)));
    }

    public Boolean isCard(AccountBankRel accountBankRel) {
        if (accountBankRel == null) {
            return true;
        }
        if (StringUtils.isBlank(accountBankRel.getAcctNo())) {
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @throws Exception
     */
    public void saveFileForPayoutInfoExpense(PayoutIntfExpansion payoutIntfExpansion, String strfileId) {
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            // ????????????
            if (strfileId != null && !"".equals(strfileId)) {
                String[] files = strfileId.split(",");
                for (int i = 0; i < files.length; i++) {
                    long fileId = Long.parseLong(files[i]);
                    //LambdaQueryWrapper<SysAttach> queryWrapper = Wrappers.lambdaQuery();
                    //queryWrapper.eq(SysAttach::getId, fileId);
                    //List<SysAttach> attach = sysAttachService.getBaseMapper().selectList(queryWrapper);
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

    @Override
    public String judgebillLookUp(Long orderId) {

        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderId);
        if (orderFeeExt != null && orderFeeExt.getBillLookUp() != null) {
            return orderFeeExt.getBillLookUp();
        }
        if (orderFeeExtH != null && orderFeeExtH.getBillLookUp() != null) {
            return orderFeeExtH.getBillLookUp();
        }
        return "";
    }

    public void confirmationServiceFee(long billServiceAmount, long flowId, String token) {
        LoginInfo user = loginUtils.get(token);
        long tenantId = user.getTenantId();
        PayoutIntf payout = payoutIntfService.getById(flowId);
        //?????? ????????????
        if (StringUtils.isBlank(payout.getVehicleAffiliation()) || Long.valueOf(payout.getVehicleAffiliation()) <= 10L
                || payout.getIsAutomatic() == null || payout.getIsAutomatic().intValue() != 1) {
            return;
        }
        /**
         * ??????????????????????????????
         */
        long fee = payoutIntfService.getPayoutIntfBySubFee(payout.getOrderId());
        long cash = 0;
        long openUserId = Long.valueOf(payout.getVehicleAffiliation());
        if (openUserId <= 10) {
            return;
        }
//        IOrderFeeTF orderFeeTF=(IOrderFeeTF) SysContexts.getBean("orderFeeTF");
//        IOrderFeeSV orderFeeSV = (IOrderFeeSV) SysContexts.getBean("orderFeeSV");
//        IOrderInfoSV orderInfoSV = (IOrderInfoSV) SysContexts.getBean("orderInfoSV");
//        IOrderSchedulerSV orderSchedulerSV = (IOrderSchedulerSV) SysContexts.getBean("orderSchedulerSV");
        if (payout.getOrderId() == null) {
            return;
        }
        long orderId = payout.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(payout.getOrderId());
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderFee orderFee = feeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);

        //???????????????
        cash = payoutIntfService.getPayoutIntfBySubFee(payout.getOrderId());

        // if(fee == cash ){
        //??????????????????
        Map resMap = payoutIntfService.doQueryServiceFee(fee, flowId, user);
        long serviceFee = DataFormat.getLongKey(resMap, "56kServiceFee");

        //??????????????????
        long payervice = payoutIntfService.getPayoutIntfServiceFee(payout.getOrderId());
        if (serviceFee > payervice) {
            billServiceAmount = serviceFee - payervice;
        }
        // }
        if (payout.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB
                || payout.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
            return;
        }
        long userId = Long.valueOf(payout.getVehicleAffiliation());//?????????
        String vehicleAffiliation = String.valueOf(userId);
        String oilAffiliation = String.valueOf(userId);


        long receSubjectsId = EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB;
        String sysPre = billPlatformService.getPrefixByUserId(userId);
        String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
        boolean is56K = false;
        if (sysPre.equals(sysParame56K)) {//56K??????
            is56K = true;
        }

        if (is56K) {
            receSubjectsId = EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB;
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
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // ???????????????
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(receSubjectsId, billServiceAmount);
        busiList.add(amountFeeSubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PLATFORM_BILL_SERVICE_FEE, busiList);
        // ????????????????????????????????????????????????
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PLATFORM_BILL_SERVICE_FEE, sysOperator.getUserInfoId(), sysOperator.getOpName(), account, busiSubjectsRelList, soNbr, flowId,
                sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, user);
        if (billServiceAmount > 0) {
            int isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
            PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, billServiceAmount, -1L, vehicleAffiliation, 0L,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.PLATFORM_BILL_SERVICE_FEE, receSubjectsId, oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER, SysStaticDataEnum.USER_TYPE.SERVICE_USER, 0L, token);
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
            payoutIntfService.doSavePayOutIntfVirToVirNew(payoutIntf, payout, token);
            SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.applyOpenBill;//????????????
            sysOperLogService.saveSysOperLog(user, busiCode, payoutIntf.getId(), SysOperLogConst.OperType.Add, "?????????????????????");
        }

    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    /**
     * ???????????????????????????
     *
     * @param pay
     * @throws Exception
     */
    public void rejectTireRentalFee(PayoutIntf pay, LoginInfo baseUser, String token) {
        if (pay == null) {
            throw new BusinessException("????????????????????????");
        }
        if (pay.getSubjectsId().longValue() != EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if (pay.getRespCode() != null) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "rejectTireRentalFee" + pay.getFlowId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
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
        if (tyreSettlementBillVO == null) {
            throw new BusinessException("?????????????????????" + pay.getBusiCode() + "?????????????????????????????????");
        }
        tyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE1, "???????????????????????????", token);


        //?????????????????????
        OrderAccount account = orderAccountService.queryOrderAccount(pay.getUserId(), vehicleAffiliation, 0L, tenantId, pay.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.TIRE_RENTAL_CASH_BACK_4022, amount);
        busiList.add(amountFeeSubjectsRel);

        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, busiList);
        // ????????????????????????????????????????????????
        Long soNbr = CommonUtil.createSoNbr();
        OrderResponseDto param = new OrderResponseDto();
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_FOR_REPAIR,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getOpName(), null, tenantId, null, "", param, vehicleAffiliation, baseUser);

        driverRecord.setUpdateTime(LocalDateTime.now());
        driverRecord.setCreateTime(LocalDateTime.now());
        driverRecord.setType(2);
        driverRecord.setNote("???????????????????????????");
        driverRecord.setFlowIdOne(pay.getId());
        driverRecord.setFlowIdTwo(account.getId());
        driverRecord.setDealState(OrderAccountConst.TASK_STATE.STATE_SUCCESS);
        driverRecord.setTenantId(pay.getPayTenantId());
        driverRecord.setOpId(baseUser.getId());
        driverRecord.setDealTime(LocalDateTime.now());
        driverRecord.setDealRemark("??????");
        orderAccountService.saveOrUpdate(account);
        cutDataRecordService.saveOrUpdate(driverRecord);

        //??????????????????
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(pay.getPayObjId(), vehicleAffiliation, 0L, tenantId, pay.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.TIRE_RENTAL_CASH_BACK_4023, amount);
        fleetBusiList.add(payableOverdueRel);

        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, fleetBusiList);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_FOR_REPAIR,
                sysOperator.getUserInfoId(), sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, baseUser);
        fleetRecord.setCreateTime(LocalDateTime.now());
        fleetRecord.setUpdateTime(LocalDateTime.now());
        fleetRecord.setType(2);
        fleetRecord.setNote("???????????????????????????");
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
        pay.setRespMsg("???????????????????????????");
        // pay.setUpDate(date);
        payoutIntfService.saveOrUpdate(pay);

        //??????????????????
        try {
            auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //????????????????????????
    public void fail(Long busiId, String desc, Map paramsMap, String token) {
        PayoutIntf payoutIntf = payoutIntfService.getById(busiId);

        if (payoutIntf == null) {
            throw new BusinessException("?????????????????????????????????" + busiId + "???????????????");
        }
        if (!payoutIntf.getTxnType().equals(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE) || !payoutIntf.getIsAutomatic().equals(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0)) {
            throw new BusinessException("?????????????????????" + busiId + "??????????????????????????????????????????????????????");
        }

        //??????????????????
        payoutIntfService.startAuditProcess(payoutIntf, token);
//		//??????????????????
        SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.Payoutchunying;
        sysOperLogService.saveSysOperLog(loginUtils.get(token), busiCode, busiId, SysOperLogConst.OperType.Audit, "??????????????????????????????" + desc);

    }

    @Override
    public String doQueryPassType(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        Long adminUserId = null;
        if (sysTenantDef != null) {
            adminUserId = sysTenantDef.getAdminUser();
        } else {
            adminUserId = loginInfo.getUserInfoId();
        }
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(adminUserId);
        Integer errPwdNum = this.getPwdNum(loginInfo.getUserInfoId(), SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
        Integer maxPayError = 3;
        //????????????????????????????????????3???
        if (errPwdNum >= maxPayError) {
            return userDataInfo.getMobilePhone();
        }
        return null;
    }

    @Override
    public String doWithdrawal(String accessToken, String payPasswd, Integer status) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        Long adminUserId = null;
//        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
//        if (sysTenantDef != null) {
//            adminUserId = sysTenantDef.getAdminUser();
//        } else {
            adminUserId = loginInfo.getUserInfoId();
 //       }
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(adminUserId);
        Integer errPwdNum = this.getPwdNum(loginInfo.getUserInfoId(), SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
        if (userDataInfo == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        Timestamp d = new Timestamp(System.currentTimeMillis());
        Integer maxPayError = 3;
        //????????????????????????????????????3???
        if (errPwdNum >= maxPayError) {
            return userDataInfo.getMobilePhone();
        }

        try {

            if (StringUtils.isEmpty(userDataInfo.getAccPassword()) || !passwordEncoder.matches(payPasswd, userDataInfo.getAccPassword())) {
                //?????????????????????
                this.setNumberTimes(loginInfo.getUserInfoId(), SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);

                errPwdNum = errPwdNum + 1;
                if (errPwdNum > 0 && errPwdNum < maxPayError) {
                    throw new BusinessException("?????????????????????,???????????????" + (maxPayError - errPwdNum) + "??????????????????!");
                } else if (errPwdNum >= maxPayError) {
                    return userDataInfo.getMobilePhone();
                }
            }
            //??????????????????????????????APP??????????????????????????? ??????????????????????????? ???????????????????????? ?????????0??????
            if (errPwdNum > 0) {
                CheckPasswordErr checkPasswordErr = this.getCheckInfo(loginInfo.getUserInfoId(), SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
                if (checkPasswordErr != null) {
                    checkPasswordErr.setOperDate(LocalDateTime.now());
                    checkPasswordErr.setErrorTimes(0);
                    checkPasswordErrMapper.updateById(checkPasswordErr);
                } else {
//                    log.error("=========================???????????????????????????????????????????????????");
                }
            }
            //??????????????????
            if (status == SysStaticDataEnum.PWD_STATUS.PWD_STATUS1) {
                this.saveCheckPasswordErr(loginInfo.getUserInfoId(), SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("????????????");
        }
        return null;
    }

    @Override
    @Transactional
    public void withdraw(String accessToken, Long flowId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        com.youming.youche.finance.domain.munual.PayoutIntf payoutIntf = payoutIntfMapper.selectById(flowId);
        if (!payoutIntf.getRespCode().equals(HttpsMain.respCodeCollection)) {
            throw new BusinessException("???????????????????????????????????????!");
        }
        payoutIntf.setRespCode(null);
        payoutIntf.setIsAutomatic(OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0);
        payoutIntf.setRemark("????????????????????????????????????");
        String mes = "[" + loginInfo.getName() + "]????????????????????????????????????";
        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.Payoutchunying, flowId, SysOperLogConst.OperType.Update, mes);
        payoutIntfMapper.updateById(payoutIntf);

        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (payoutIntf.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER) && payoutIntf.getPayTenantId() > 0) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("tenantName", sysTenantDef.getName());
            paraMap.put("busiCode", payoutIntf.getBusiCode());
            paraMap.put("amount", String.valueOf(CommonUtil.getDoubleFormatLongMoney(payoutIntf.getTxnAmt(), 2)));

            SysSmsSend sysSmsSend = new SysSmsSend();
            sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.PAY_DRIVER);
            sysSmsSend.setBillId(userDataInfo.getMobilePhone());
            sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS);
            sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
            sysSmsSend.setObjId(userDataInfo.getMobilePhone());
            sysSmsSend.setParamMap(paraMap);

            sysSmsSendService.sendSms(sysSmsSend);
        }

        //????????????
        String time = DateUtil.formatDateByFormat(TimeUtil.getDataTime(), "yyMMddHHmmss");
        Long billServiceFee = payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee();
        List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();

//        SysOperator driverInfo = sysOperatorMapper.getSysOperatorByUserIdOrPhone(payoutIntf.getUserId(), null);
        SysUser driverInfo = sysUserService.getByUserInfoId(payoutIntf.getUserId());

        tenantBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50137, payoutIntf.getTxnAmt()));
        tenantBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50143, billServiceFee));

        List<BusiSubjectsRel> tenantSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_WITHDRAW, tenantBusiList);
        accountDetailsService.createAccountDetailsNew(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.PAY_WITHDRAW,
                payoutIntf.getPayObjId(), payoutIntf.getUserId(), driverInfo.getName(), tenantSubjectsRelList, payoutIntf.getId(), payoutIntf.getOrderId(), payoutIntf.getPayTenantId(), payoutIntf.getPayUserType(), Long.valueOf(time));
        //????????????
        List<BusiSubjectsRel> driverBusiList = new ArrayList<BusiSubjectsRel>();
//        SysOperator tenantInfo = sysOperatorMapper.getSysOperatorByUserIdOrPhone(payoutIntf.getUserId(), null);
        SysUser tenantInfo = sysUserService.getByUserInfoId(payoutIntf.getUserId());
        driverBusiList.add(busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50138, payoutIntf.getTxnAmt()));
        List<BusiSubjectsRel> driverSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_WITHDRAW, driverBusiList);
        accountDetailsService.createAccountDetailsNew(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.PAY_WITHDRAW,
                payoutIntf.getUserId(), payoutIntf.getPayObjId(), tenantInfo.getName(), driverSubjectsRelList, payoutIntf.getId(), payoutIntf.getOrderId(), payoutIntf.getPayTenantId(), payoutIntf.getUserType(), Long.valueOf(time));
    }

    /**
     * ??????  ???????????????????????????????????????????????????????????????
     *
     * @param userId
     * @param pwdType
     * @return
     */
    public Integer getPwdNum(long userId, Integer pwdType) {
        if (userId <= 0) {
            throw new BusinessException("????????????????????????!");
        }
        Integer errPwdNum = 0;
        CheckPasswordErr checkPasswordErr = this.getCheckInfo(userId, pwdType);
        if (checkPasswordErr != null) {
            errPwdNum = checkPasswordErr.getErrorTimes();
        }
        return errPwdNum;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param userId
     * @param pwdType
     * @return
     */
    public CheckPasswordErr getCheckInfo(long userId, Integer pwdType) {
        LambdaQueryWrapper<CheckPasswordErr> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckPasswordErr::getUserId, userId);
        queryWrapper.eq(CheckPasswordErr::getCheckType, pwdType);
        queryWrapper.between(CheckPasswordErr::getCheckDate, DateUtil.parseDate(DateUtil.getCurrDateTimeBegin()), DateUtil.parseDate(DateUtil.getCurrDateTimeEnd()));
        List<CheckPasswordErr> list = checkPasswordErrMapper.selectList(queryWrapper);
        CheckPasswordErr checkPasswordErr = null;
        if (list.size() > 0) {
            checkPasswordErr = list.get(0);
        }
        return checkPasswordErr;
    }

    @Transactional
    public void setNumberTimes(long userId, Integer pwdType) {
        long id = CommonUtil.createOrderId();
        CheckPasswordErr checkPasswordErr = getCheckInfo(userId, pwdType);
        if (checkPasswordErr == null) {
            checkPasswordErr = new CheckPasswordErr();
            checkPasswordErr.setId(id);
            checkPasswordErr.setCheckDate(LocalDateTime.now());
            checkPasswordErr.setErrorTimes(1); //????????????
            checkPasswordErr.setOperDate(LocalDateTime.now());
            checkPasswordErr.setCheckType(pwdType);
            checkPasswordErr.setUserId(userId);
            checkPasswordErrMapper.insert(checkPasswordErr);
        } else {
            //????????????1
            checkPasswordErr.setErrorTimes(checkPasswordErr.getErrorTimes() + 1);
            checkPasswordErrMapper.updateById(checkPasswordErr);
        }
    }

    @Transactional
    public void saveCheckPasswordErr(long userId, Integer pwdType) {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        long id = CommonUtil.createOrderId();
        CheckPasswordErr checkPasswordErr = getCheckInfo(userId, pwdType);
        if (checkPasswordErr == null) {
            checkPasswordErr = new CheckPasswordErr();
            checkPasswordErr.setId(id);
            checkPasswordErr.setCheckDate(LocalDateTime.now());
            checkPasswordErr.setErrorTimes(0);
            checkPasswordErr.setOperDate(LocalDateTime.now());
            checkPasswordErr.setCheckType(pwdType);
            checkPasswordErr.setUserId(userId);
            checkPasswordErr.setStatus(SysStaticDataEnum.PWD_STATUS.PWD_STATUS1);
            checkPasswordErrMapper.insert(checkPasswordErr);
        } else {
            checkPasswordErr.setStatus(SysStaticDataEnum.PWD_STATUS.PWD_STATUS1);
            checkPasswordErrMapper.updateById(checkPasswordErr);
        }
    }


    @Override
    public boolean checkSmsCode(String billId, String captcha, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //????????????????????????????????????????????????
//        captcha = EncryPwd.pwdDecryp(captcha);//??????
        String code = String.valueOf(redisUtil.get(billId + SysStaticDataEnum.SYS_CODE_TYPE.PAY_CASH_RESET_CODE + ""));
        if (!captcha.equals(code)) {
            throw new BusinessException("????????????????????????");
        }
//        Timestamp d = new Timestamp(System.currentTimeMillis());
        CheckPasswordErr checkPasswordErr = this.getCheckInfo(loginInfo.getUserInfoId(), SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
        if (checkPasswordErr != null) {
            checkPasswordErr.setOperDate(LocalDateTime.now());
            checkPasswordErr.setErrorTimes(0);
            checkPasswordErrMapper.updateById(checkPasswordErr);
            return true;
        } else {
            log.error("=========================?????????????????????????????????");
            return false;
        }
    }

    @Override
    public boolean doQueryCode(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long adminUserId = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getAdminUser();
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(adminUserId);
        //???????????????
        Object code = redisUtil.get(SysStaticDataEnum.SYS_CODE_TYPE.PAY_CASH_RESET_CODE + "" + userDataInfo.getMobilePhone());
        if (code != null && StringUtils.isNotEmpty(String.valueOf(code))) {
            return true;
        }
        String randomCode = SysMagUtil.getRandomNumber(6);
        String codeKey = userDataInfo.getMobilePhone() + SysStaticDataEnum.SYS_CODE_TYPE.PAY_CASH_RESET_CODE + "";
        boolean redisResult = redisUtil.setex(codeKey, randomCode, 120);
        if (redisResult) {
            log.info("?????????????????????[" + codeKey + "]?????????????????????????????????????????????????????????????????????????????????" + randomCode + "????????????2??????");
        }
        /**????????????*/
        //??????--?????????????????????[${userName}]?????????????????????????????????????????????????????????????????????????????????${CODE}????????????2?????????
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("userName", loginInfo.getName());
        paraMap.put("code", randomCode);//?????????
        SysSmsSend sysSmsSend = new SysSmsSend();
        sysSmsSend.setBillId(userDataInfo.getMobilePhone());
        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.PAY_CASH_RESET_CODE);
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.SYS_CODE_TYPE.PAY_CASH_RESET_CODE));
        sysSmsSend.setObjId(String.valueOf(adminUserId));
        sysSmsSend.setParamMap(paraMap);
        sysSmsSendService.sendSms(sysSmsSend);
        return true;
    }

    @Override
    public void confirmationSMS(Long templateId, com.youming.youche.finance.domain.munual.PayoutIntf payoutIntf) {
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (payoutIntf.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER) && payoutIntf.getPayTenantId() > 0) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("tenantName", sysTenantDef.getName());
            paraMap.put("userName", userDataInfo.getLinkman());
            paraMap.put("amount", String.valueOf(CommonUtil.getDoubleFormatLongMoney(payoutIntf.getTxnAmt(), 2)));
            SysSmsSend sysSmsSend = new SysSmsSend();
            sysSmsSend.setTemplateId(templateId);
            sysSmsSend.setBillId(userDataInfo.getMobilePhone());
            sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS);
            sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
            sysSmsSend.setObjId(userDataInfo.getMobilePhone());
            sysSmsSend.setParamMap(paraMap);

            sysSmsSendService.sendSms(sysSmsSend);
        }
    }

    @Override
    public Page<QueryPayManagerDto> doQueryAllPayManagerWX(QueryPayManagerVo queryPayManagerVo, Long orgId, Long payAmt, Integer isNeedBiil, Integer payId, String accessToken, Integer pageSize, Integer pageNum) {
        Page<QueryPayManagerDto> pagination = payoutIntfService.doQueryAllPayManager(queryPayManagerVo, pageSize, pageNum, accessToken);
        List<QueryPayManagerDto> list = pagination.getRecords();
        List busiIds = new ArrayList();
        for (QueryPayManagerDto queryPayManagerDto : list) {
            busiIds.add(queryPayManagerDto.getPayId());
        }

        dealPermission(PAY_MANAGER, busiIds, list, accessToken);
        return pagination;
    }

    private void dealPermission(String payManager, List busiIds, List<QueryPayManagerDto> list, String accessToken) {
        Map<Long, Boolean> hasPermissionMap = null;
        if (!busiIds.isEmpty()) {
            hasPermissionMap = auditOutService.isHasPermission(accessToken, payManager, busiIds);
            if (null == hasPermissionMap) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                QueryPayManagerDto queryPayManagerDto1 = list.get(i);
                Boolean flg = hasPermissionMap.get(queryPayManagerDto1.getPayId());
                if (flg != null && flg == true) {
                    queryPayManagerDto1.setIsAudit(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                }
                queryPayManagerDto1.setIsAudit(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
            }
        }
    }

    @Override
    public List<DoQueryDto> doQueryPayType(String accessToken) {
        String basicName = basicConfigurationService.doQuery("PAYMANAGER_TYPE", accessToken);
        List basicList = new ArrayList();
        if (StringUtils.isNotEmpty(basicName)) {
            String[] basicNames = basicName.split(",");
            for (String name : basicNames) {
                DoQueryDto doQueryDto = new DoQueryDto();
                if (StringUtils.isNotEmpty(name)) {
                    doQueryDto.setCodeName(name);
                    doQueryDto.setCodeValue(name);
                    doQueryDto.setUpdateShow("0");
                    basicList.add(doQueryDto);
                }
            }
        }
        return basicList;
    }

    @Override
    public PayManagerDto getPayManager(Long payId, Integer state, Long payAmt, Integer isNeedBill, String accessToken) {
        PayManagerDto payManagerDto = new PayManagerDto();
        LoginInfo user = loginUtils.get(accessToken);
        PayManager payManager = payManagerService.getById(payId);
        if (null == payManager) {
            throw new BusinessException("?????????????????????");
        }
        try {
            BeanUtils.copyProperties(payManager, payManagerDto);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        payManagerDto.setState(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, user.getTenantId(), "PAYMANAGER_STATE", state + ""));
        payManagerDto.setPayAmt(CommonUtil.divide(payAmt));
        payManagerDto.setIsNeedBill(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, user.getTenantId(), "PAYMANAGER_BILL", isNeedBill + ""));

        AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payManager.getReceAccNo());
        if (null != accountBankRel) {
            payManagerDto.setReceBankNo(accountBankRel.getAcctNo());
            payManagerDto.setReceBankName(accountBankRel.getBankName());
            payManagerDto.setReceName(accountBankRel.getAcctName());
            payManagerDto.setReceUserName(accountBankRel.getAcctName());

            if (StringUtils.isNotEmpty(accountBankRel.getBranchName())) {
                payManagerDto.setReceBankName(accountBankRel.getBranchName());
            }
        }
        List<Long> fileId = new ArrayList<Long>();//??????id
        List<String> fileUrl = new ArrayList<String>();//??????url
        QueryWrapper<OaLoanData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("relId", payId).eq("relType", OaLoanData.RELTYPE5);
        List<OaFiles> oaFiles = oaFilesService.list();
        if (oaFiles != null) {
            for (OaFiles of : oaFiles) {
                fileId.add(of.getFileId());
                fileUrl.add(of.getFileUrl());
            }
        }
        //????????????
        SysOperLogConst.BusiCode busi_code = SysOperLogConst.BusiCode.payManager;//????????????
        List<SysOperLog> sysOperLogs = sysOperLogService.querySysOperLog(busi_code, payId, false, user.getTenantId(), AuditConsts.AUDIT_CODE.PAY_MANAGER, null);
        payManagerDto.setSysOperLogs(sysOperLogs);
        payManagerDto.setFileId(fileId);
        payManagerDto.setFileUrl(fileUrl);
        return payManagerDto;
    }

    @Override
    public String oaLoanDriverAudit(Long busiId, String desc, String chooseResult, String loanSubject, String remark, Integer loanTransReason, String accidentDate, String insuranceDate, String accidentType, String accidentReason, String dutyDivide, String accidentDivide, String insuranceFirm, String insuranceMoney, String reportNumber, String accidentExplain, String accessToken) {
        OaLoan oaLoan = oaLoanThreeService.queryOaLoanById(busiId);
        if (oaLoan == null) {
            throw new BusinessException("????????????????????????!");
        }

        /**
         * 2018-12-07 ????????????????????????????????????????????????????????????
         */
        if (!(org.apache.commons.lang3.StringUtils.equals(OaLoanConsts.STS.STS0 + "", oaLoan.getSts() + "") || org.apache.commons.lang3.StringUtils.equals(OaLoanConsts.STS.STS1 + "", oaLoan.getSts() + ""))) {
            throw new BusinessException("?????????????????????????????????????????????");
        }

        if (org.apache.commons.lang3.StringUtils.equals(OaLoanConsts.STS.STS0 + "", oaLoan.getSts() + "")) {//?????????

			/*if(StringUtils.equals("1",chooseResult) && (StringUtils.isBlank(oaLoan.getCollectAcctId()) || StringUtils.isBlank(oaLoan.getAccNo()) || StringUtils.isBlank(oaLoan.getAccName()))){
				throw new BusinessException("???????????????web????????????");
			}*/
            if (org.apache.commons.lang3.StringUtils.equals("1", chooseResult) && org.apache.commons.lang3.StringUtils.equals(loanSubject, OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT10 + "")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (org.apache.commons.lang3.StringUtils.isNotBlank(accidentDate)) {
                    try {
                        oaLoan.setAccidentDate(getDateToLocalDateTime(sdf.parse(accidentDate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(insuranceDate)) {
                    try {
                        oaLoan.setInsuranceDate(getDateToLocalDateTime(sdf.parse(insuranceDate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(accidentType)) {
                    oaLoan.setAccidentType(Integer.parseInt(accidentType));
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }

                if (org.apache.commons.lang3.StringUtils.isNotBlank(accidentReason)) {
                    oaLoan.setAccidentReason(Integer.parseInt(accidentReason));
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }

                if (org.apache.commons.lang3.StringUtils.isNotBlank(dutyDivide)) {
                    oaLoan.setDutyDivide(Integer.parseInt(dutyDivide));
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(accidentDivide)) {
                    oaLoan.setAccidentDivide(Integer.parseInt(accidentDivide));
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(insuranceFirm)) {
                    oaLoan.setInsuranceFirm(insuranceFirm);
                } else {
                    throw new BusinessException("?????????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(reportNumber)) {
                    oaLoan.setReportNumber(reportNumber);
                } else {
                    throw new BusinessException("??????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(insuranceMoney) && CommonUtil.isNumber(insuranceMoney)) {
                    oaLoan.setInsuranceMoney(Long.parseLong(insuranceMoney));
                } else {
                    throw new BusinessException("???????????????????????????????????????");
                }

                oaLoan.setAccidentExplain(accidentExplain);
            }
            if (org.apache.commons.lang3.StringUtils.equals("1", chooseResult) && OaLoanConsts.LAUNCH.LAUNCH2 == oaLoan.getLaunch() && org.apache.commons.lang3.StringUtils.equals(loanSubject, OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT5 + "")) {
                oaLoan.setLoanTransReason(loanTransReason);
                oaLoan.setRemark(remark);
            }
            oaLoan.setSts(OaLoanConsts.STS.STS1);//??????????????????
            oaLoanThreeService.saveOrUpdate(oaLoan);
        }
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
        }
        AuditCallbackDto auditCallbackDto = auditSettingService.sure(auditCode, busiId, desc, Integer.parseInt(chooseResult), accessToken);
        //  ??????????????????
        if (null != auditCallbackDto && !auditCallbackDto.getIsNext() && auditCallbackDto.getIsAudit() && Integer.parseInt(chooseResult) == AuditConsts.RESULT.SUCCESS) {
            oaLoanThreeService.sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
            //  ??????????????????
        } else if (AuditConsts.RESULT.FAIL == Integer.parseInt(chooseResult) && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
            oaLoanThreeService.fail(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
        }
        return "Y";
    }

    @Override
    public void DealPassError(Long userId, String accPwd) {
        Timestamp d = new Timestamp(System.currentTimeMillis());

        Integer maxPayError = (Integer) sysCfgService.getCfgVal("MODIFY_PASS_ERROR_TIMES", SysStaticDataEnum.SYSTEM_CFG.CFG_0, Integer.class);
        if (maxPayError == null) {
            maxPayError = 5;
        }
        Integer errPwdNum = this.getPwdNum(userId, SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
        //????????????????????????????????????5???
        if (errPwdNum >= maxPayError) {
            throw new BusinessException("??????????????????????????????????????????" + maxPayError + "???!");
        }
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
        String encode = passwordEncoder.encode(userDataInfo.getAccPassword());
        if (userDataInfo == null) {
            throw new BusinessException("??????????????????????????????!");
        }
//        String t = EncryPwd.pwdDecryp(accPwd);
        System.out.println(userDataInfo.getAccPassword());
        if (StringUtils.isEmpty(userDataInfo.getAccPassword()) || !passwordEncoder.matches(accPwd, userDataInfo.getAccPassword())) {
            //?????????????????????
            this.setNumberTimes(userId, SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
            errPwdNum = errPwdNum + 1;
            if (errPwdNum > 0 && errPwdNum < maxPayError) {
                throw new BusinessException("?????????????????????,???????????????" + (maxPayError - errPwdNum) + "??????????????????!");
            } else if (errPwdNum >= maxPayError) {
                throw new BusinessException("?????????????????????????????????????????????" + maxPayError + "???!");
            }
        }
        //??????????????????????????????APP??????????????????????????? ??????????????????????????? ???????????????????????? ?????????0??????
        if (errPwdNum > 0) {
            CheckPasswordErr checkPasswordErr = this.getCheckInfo(userId, SysStaticDataEnum.PWD_TYPE.PWD_PAY_ERR);
            if (checkPasswordErr != null) {
                checkPasswordErr.setOperDate(LocalDateTime.now());
                checkPasswordErr.setErrorTimes(0);
                checkPasswordErrMapper.updateById(checkPasswordErr);
            } else {
                log.error("=========================???????????????????????????????????????????????????");
            }
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param oaLoan
     * @return
     */
    public boolean checkLoanBelongDriver(OaLoan oaLoan) {
        boolean flag = false;
        if (oaLoan.getClassify() != null && oaLoan.getLaunch() != null) {
            if (oaLoan.getClassify() == 2 && oaLoan.getLaunch() == 2) {
                List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));//????????????
                for (SysStaticData data : dataList) {
                    if (org.apache.commons.lang3.StringUtils.equals(data.getCodeValue(), oaLoan.getLoanSubject() + "")) {
                        flag = true;
                    }
                }
            }
        }
        if (oaLoan.getClassify() == null && oaLoan.getLaunch() == null) {
            return flag;
        }
        return flag;
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
}
