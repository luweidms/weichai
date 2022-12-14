package com.youming.youche.order.provider.service.order.other;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.tenant.ITenantServiceRelDetailsService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.domain.tenant.TenantServiceRelDetails;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.order.api.IClearAccountOilRecordService;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.api.order.other.IServiceOrderInfoService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.commons.PinganIntefaceConst;
import com.youming.youche.order.domain.AgentServiceInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.oil.ClearAccountOilRecord;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
import com.youming.youche.order.domain.order.ServiceOrderInfo;
import com.youming.youche.order.domain.order.TenantAgentServiceRel;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AgentServiceDto;
import com.youming.youche.order.dto.OilAccountOutDto;
import com.youming.youche.order.dto.OilAccountOutListDto;
import com.youming.youche.order.dto.OilServiceInDto;
import com.youming.youche.order.dto.OilServiceOutDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderConsumeOilDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.PayForOilInDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.PayReturnDto;
import com.youming.youche.order.provider.mapper.order.OilRechargeAccountDetailsMapper;
import com.youming.youche.order.provider.mapper.order.PayoutIntfMapper;
import com.youming.youche.order.provider.utils.AcUtil;
import com.youming.youche.order.provider.utils.BusiToOrderUtils;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.finance.commons.util.DateUtil.DATETIME12_FORMAT2;

/**
 * ?????????????????????(???????????????)?????????
 * */
@DubboService(version = "1.0.0")
@Service
public class OperationOilServiceImpl implements IOperationOilService {

    @Resource
    RedisUtil redisUtil;
    @Resource
    private IOrderOilSourceService orderOilSourceService;
    @Resource
    private IConsumeOilFlowExtService oilFlowExtService;
    @Resource
    IRechargeOilSourceService iRechargeOilSourceService;
    @Resource
    IOrderInfoExtService iOrderInfoExtService;
    @Resource
    IOrderInfoExtHService iOrderInfoExtHService;
    @Resource
    private LoginUtils loginUtils;
    @Resource
    IOrderLimitService iOrderLimitService;

    @Resource
    IServiceMatchOrderService iServiceMatchOrderService;

    @Resource
    IConsumeOilFlowService iConsumeOilFlowService;

    @Resource
    IClearAccountOilRecordService iClearAccountOilRecordService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @Resource
    PayoutIntfMapper payoutIntfMapper;

    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;

    @Resource
    IOrderOilSourceService iOrderOilSourceService;
    @Resource
    IOpAccountService iOpAccountService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @Resource
    BusiToOrderUtils busiToOrderUtils;

    @Resource
    IOrderAccountService iOrderAccountService;
    @Resource
    IOilRechargeAccountService iOilRechargeAccountService;

    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Resource
    IAccountDetailsService iAccountDetailsService;
    @DubboReference(version = "1.0.0")
    ITenantUserRelService iTenantUserRelService;
    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;
    @Resource
    IBaseBillInfoService baseBillInfoService;
    @Resource
    IOilSourceRecordService oilSourceRecordService;
    @DubboReference(version = "1.0.0")
     IServiceProductService serviceProductService;
    @DubboReference(version = "1.0.0")
    ITenantProductRelService iTenantProductRelService;
    @Resource
    com.youming.youche.order.api.service.IServiceInfoService iServiceInfoService;
    @Resource
    IPayoutIntfService iPayoutIntfService;
    @Resource
    IAccountBankUserTypeRelService iAccountBankUserTypeRelService;
    @Resource
     IAccountBankRelService accountBankRelService;
    @Resource
    IBaseBusiToOrderService iBaseBusiToOrderService;
    @DubboReference(version = "1.0.0")
     ITenantServiceRelService tenantServiceRelService;

    @Resource
    IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;
    @Resource
    OilRechargeAccountDetailsMapper oilRechargeAccountDetailsMapper;
    @Resource
    IServiceOrderInfoService iServiceOrderInfoService;
    @Lazy
    @Autowired
    private IAccountDetailsService accountDetailsService;
    @DubboReference(version = "1.0.0")
    ITenantServiceRelDetailsService iTenantServiceRelDetailsService;
    @DubboReference(version = "1.0.0")
    ITenantServiceRelService iTenantServiceRelService;
    @DubboReference(version = "1.0.0")
    ISysUserService iSysOperatorService;
    @Resource
    IConsumeOilFlowExtService iConsumeOilFlowExtService;

    @Resource
    IOrderAccountOilSourceService iOrderAccountOilSourceService;

    @Override
    public ConsumeOilFlow payForOil(PayForOilInDto in,String accessToken)  {
        Long userId = in.getUserId();
        Long amountFee = in.getAmountFee();
        Long serviceUserId = in.getServiceUserId();
        Long productId = in.getProductId();
        Long oilPrice = in.getOilPrice();
        Float oilRise = in.getOilRise();
        String plateNumber = in.getPlateNumber();
        String localeBalanceState = in.getLocaleBalanceState();
        Integer fromType = in.getFromType();
        String orderNum = in.getOrderNum();
        if (userId == null || userId < 1L) {
            throw new BusinessException("?????????????????????");
        }
        if (amountFee == null || amountFee <= 0L) {
            throw new BusinessException("???????????????????????????!");
        }
        if (serviceUserId <= 0) {
            throw new BusinessException("??????????????????????????????!");
        }
        if (productId == null || productId <= 0) {
            throw new BusinessException("???????????????id!");
        }
        if (oilPrice == null) {
            throw new BusinessException("?????????????????????!");
        }
        if (oilRise == null) {
            throw new BusinessException("?????????????????????!");
        }
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("??????????????????!");
        }
        if (fromType == null || fromType <= 0) {
            throw new BusinessException("???????????????????????????!");
        }
        if (fromType == OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE2) {
            if (StringUtils.isBlank(orderNum)) {
                throw new BusinessException("???????????????!");
            }
        }
        // TODO ??????
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payForOil" + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[] { DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2) });

        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        //????????????id??????????????????
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(productId);
        if (serviceProduct == null) {
            throw new BusinessException("????????????id???" + productId + ",?????????????????????");
        } else {
            if (serviceProduct.getState() == null || serviceProduct.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2) {
                throw new BusinessException("??????id??????" + productId + "?????????????????????");
            }
        }

        SysUser sysOtherOperator = sysUserService.getSysOperatorByUserId(serviceUserId);
        if (sysOtherOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        ServiceInfo serviceInfo = iServiceInfoService.getServiceUserId(serviceUserId);
        if (serviceInfo == null) {
            throw new BusinessException("????????????????????????");
        } else {
            if (serviceInfo.getServiceType() == null || serviceInfo.getServiceType() != SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                throw new BusinessException("??????????????????????????????????????????");
            }
        }
        //????????????id???????????????????????????
        List<TenantProductRel> tenantProductRelList = iTenantProductRelService.getTenantProductRelList(productId);
        if (tenantProductRelList == null) {
            throw new BusinessException("????????????id???" + productId + ",??????????????????????????????");
        }
        //????????????????????????
        List<Long> sourceTenantIdList = new ArrayList<Long>();
        Map<String, TenantProductRel> tenantProductRelMap = new HashMap<String, TenantProductRel>();
        boolean isSharePorduct = false;
        //??????????????????????????????????????????
        boolean isBelongToTenant = false;
        for (TenantProductRel tpr : tenantProductRelList) {
            if (tpr.getState() == null || tpr.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2 || tpr.getAuthState() == null || tpr.getAuthState() != 2) {
                continue;
            }
            //??????tenantId ???1????????????????????????
            if (tpr.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
                isSharePorduct = true;
            } else if (tpr.getTenantId() != SysStaticDataEnum.PT_TENANT_ID ) {//??????tenantId ???1????????????????????????
                isBelongToTenant = true;
                sourceTenantIdList.add(tpr.getTenantId());
            }
            tenantProductRelMap.put(String.valueOf(tpr.getTenantId()), tpr);
        }
        if (!isSharePorduct && !isBelongToTenant) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????????????????????????????");
        }
        //???????????????????????????
        List<OilServiceInDto> oilServiceInList = new ArrayList<>();
        OilServiceInDto inDto = new OilServiceInDto();
        inDto.setProductId(productId);
        inDto.setServiceId(serviceUserId);
        inDto.setOilPrice(oilPrice);
        oilServiceInList.add(inDto);
        List<OilServiceOutDto> oilServiceOutList = this.queryOilRise(userId, oilServiceInList, null,SysStaticDataEnum.USER_TYPE.DRIVER_USER);

        OilServiceOutDto oilServiceOut = null;
        if (oilServiceOutList == null || oilServiceOutList.size() <= 0) {
            throw new BusinessException("????????????????????????????????????????????? " + 0.00 + "??????!");
        } else {
            oilServiceOut = oilServiceOutList.get(0);
            long canConsumeOil = (oilServiceOut.getConsumeOilBalance() == null ? 0L : oilServiceOut.getConsumeOilBalance());
            if (canConsumeOil == 0 || amountFee > canConsumeOil) {
                throw new BusinessException("????????????????????????????????????????????? " + (double)canConsumeOil/100 + "??????!");
            }
        }
        //?????????
        long totalOilBalance = (oilServiceOut.getOilBalance() == null ? 0L : oilServiceOut.getOilBalance());
        //??????????????????
        long totalBalance = (oilServiceOut.getPinganBalance() == null ? 0L : oilServiceOut.getPinganBalance());
        //???????????????????????????
        long canUseOil = 0;
        long canUseBalance = 0;
        if (amountFee > totalOilBalance) {
            canUseOil = totalOilBalance;
            if ((amountFee - totalOilBalance ) > totalBalance) {
                throw new BusinessException("????????????????????????????????????????????? " + (totalOilBalance  + totalBalance)/100 + "??????!");
            } else {
                canUseBalance = amountFee - totalOilBalance ;
            }
        } else {
            canUseOil = amountFee;
        }
        long useOrderAccountOil = canUseOil ;
        List<ConsumeOilFlow> oilFlowList = null;
        long soNbr = CommonUtil.createSoNbr();
        long incr = redisUtil.incr(EnumConsts.RemoteCache.CONSUME_OIL_ORDER_ID_TO_DRIVER);
        String oilOrder = AcUtil.createDriverConsumeOilOrderId(incr);
        //????????????????????????
        ConsumeOilFlow oilFlow = iConsumeOilFlowService.createConsumeOilFlow(userId, sysOperator.getBillId(),
                sysOperator.getOpName(), OrderAccountConst.CONSUME_COST_TYPE.TYPE1,
                oilOrder, amountFee, oilPrice, oilRise, sysOtherOperator.getUserInfoId(),
                sysOtherOperator.getBillId(), sysOtherOperator.getName(), "",
                null, productId, -1,SysStaticDataEnum.USER_TYPE.DRIVER_USER,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        oilFlow.setProductName(serviceProduct.getProductName());
        oilFlow.setAddress(serviceProduct.getAddress());
        oilFlow.setServiceCall(serviceProduct.getServiceCall());
        oilFlow.setIsEvaluate(OrderAccountConst.CONSUME_OIL_FLOW.IS_EVALUATE_NO);
        oilFlow.setPlateNumber(plateNumber);
        oilFlow.setOilBalance(useOrderAccountOil);
        oilFlow.setBalance(canUseBalance);
        oilFlow.setMarginBalance(0L);
        oilFlow.setOrderNum(orderNum);
        oilFlow.setAdvanceFee(0L);
        oilFlow.setSoNbr(soNbr);
        oilFlow.setFromType(fromType);
        if (StringUtils.isNotBlank(localeBalanceState) && localeBalanceState.equals(String.valueOf(ServiceConsts.LOCALE_BALANCE_STATE.YES)) ) {
            oilFlow.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.YES);
        } else {
            oilFlow.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.NO);
        }
        iConsumeOilFlowService.save(oilFlow);
        if (fromType == OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE1) {//??????????????????????????????????????????????????????????????????
            ServiceOrderInfo info = new ServiceOrderInfo();
            info.setProductId(productId);
            info.setPlateNumber(plateNumber);
            info.setUserId(userId);
//            info.setUserName(sysOperator.getOperatorName());
            info.setUserName(sysOperator.getOpName());
            info.setUserPhone(sysOperator.getBillId());
            info.setOrderState(ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY);
            info.setOrderType(ServiceConsts.SERVICE_ORDER_TYPE.OR_CODE);
            Float oilLitre = (oilRise * 1000);
            info.setOilLitre(oilLitre.longValue());
            info.setOilPrice(oilPrice);
            info.setOilFee(amountFee);
            info.setCreateUserId(userId);
            info.setOrderId(oilFlow.getOrderId());
            info.setOilBalance(useOrderAccountOil);
            info.setCashBalance(canUseBalance);
            Long serviceOrderInfoId = iServiceOrderInfoService.saveOrUpdates(info,accessToken);
            oilFlow.setOrderNum(String.valueOf(serviceOrderInfoId));
        } else {
            iServiceOrderInfoService.updateServiceOrderState(in.getId(), ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY,accessToken);
            iServiceOrderInfoService.synServiceOrderBalance(in.getId(), useOrderAccountOil, canUseBalance,accessToken);
        }
        Map<String,Object> inParm = new HashMap<String,Object>();
        if (canUseOil > 0) {//???????????????
           // ???????????????
            Map<String,Object> result = this.payOrderAccountOil(userId, serviceUserId, sourceTenantIdList, inParm,
                    oilPrice, canUseOil, isSharePorduct, soNbr, productId,oilOrder,accessToken);
            if (result == null) {
                throw new BusinessException("????????????????????????");
            }
            oilFlowList = (List<ConsumeOilFlow>) result.get("consumeOilFlowList");
            if (oilFlowList == null || oilFlowList.size() <= 0) {
                throw new BusinessException("?????????????????????");
            }
        }
        //??????????????????????????? ???????????????????????? ???????????? * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????=?????????/??????
        if (StringUtils.isNotBlank(localeBalanceState) && localeBalanceState.equals(String.valueOf(ServiceConsts.LOCALE_BALANCE_STATE.YES)) ) {
            BigDecimal bigDecimal = new BigDecimal((float)amountFee / oilPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (bigDecimal.floatValue() != oilRise) {
                oilRise = bigDecimal.floatValue();
            }
        }
        //?????????????????????????????????????????????????????????????????????
        float tempOilRise = 0.0f;
        for (ConsumeOilFlow cof : oilFlowList) {
            cof.setPlateNumber(plateNumber);
            cof.setSoNbr(soNbr);
            cof.setOrderNum(orderNum);
            cof.setFromType(fromType);
            if (StringUtils.isNotBlank(localeBalanceState) && localeBalanceState.equals(String.valueOf(ServiceConsts.LOCALE_BALANCE_STATE.YES)) ) {
                cof.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.YES);
            } else {
                cof.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.NO);
            }
            BigDecimal b1 = new BigDecimal(Float.toString(tempOilRise));
            BigDecimal b2 = new BigDecimal(Float.toString(cof.getOilRise()));
            tempOilRise = b1.add(b2).floatValue();
            if (cof == oilFlowList.get(oilFlowList.size() - 1)) {
                if (tempOilRise > oilRise) {
                    BigDecimal b3 = new BigDecimal(tempOilRise - oilRise).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal b4 = new BigDecimal(cof.getOilRise() - b3.floatValue() ).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cof.setOilRise(b4.floatValue());
                }
            }
            cof.setOrderId(oilOrder);
            iConsumeOilFlowService.saveOrUpdate(cof);

        }
        //??????????????????????????????
        if (canUseBalance > 0 && !isSharePorduct) {
            //??????????????????????????????
            List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel consumeBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BALANCE_CONSUME_OIL_SUB, canUseBalance);
            busiBalanceList.add(consumeBalanceSub);
            List<BusiSubjectsRel> balanceRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_OIL_CODE, busiBalanceList);
            iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.PAY_FOR_OIL_CODE, userId, serviceUserId, sysOtherOperator.getOpName(),
                    balanceRelList, soNbr, 0L, -1L,SysStaticDataEnum.USER_TYPE.DRIVER_USER);

            //????????????????????????????????????
            List<BusiSubjectsRel> serviceBusiBalanceList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel serviceBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.INCOME_BALANCE_CONSUME_OIL_SUB, canUseBalance);
            serviceBusiBalanceList.add(serviceBalanceSub);
            List<BusiSubjectsRel> serviceBalanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.INCOME_OIL_CODE, serviceBusiBalanceList);
            iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE,EnumConsts.PayInter.INCOME_OIL_CODE,serviceUserId, userId, sysOperator.getOpName(), serviceBalanceList, soNbr, 0L, -1L,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            //???????????????
            //???????????????????????????
            //String orderId = AcUtil.createConsumeOilOrderId();
            float rise = new BigDecimal((oilRise - tempOilRise)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            ConsumeOilFlow serviceOilFlow = iConsumeOilFlowService.createConsumeOilFlow(serviceUserId, sysOtherOperator.getBillId(),
                    sysOtherOperator.getName(), OrderAccountConst.CONSUME_COST_TYPE.TYPE2,
                    oilOrder, canUseBalance, oilPrice, rise, sysOperator.getUserInfoId(),
                    sysOperator.getBillId(), sysOperator.getOpName(),
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,
                    -1L, productId, -1,SysStaticDataEnum.USER_TYPE.SERVICE_USER,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            serviceOilFlow.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS1);//?????????
            serviceOilFlow.setOilAffiliation(String.valueOf(OrderAccountConst.IS_VERIFICATION.NO_VERIFICATION));
            serviceOilFlow.setSoNbr(soNbr);
            serviceOilFlow.setFromType(OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE1);
            if (StringUtils.isNotBlank(localeBalanceState) && localeBalanceState.equals(String.valueOf(ServiceConsts.LOCALE_BALANCE_STATE.YES)) ) {
                serviceOilFlow.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.YES);
            } else {
                serviceOilFlow.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.NO);
            }
            serviceOilFlow.setGetDate(LocalDateTime.now());
            serviceOilFlow.setGetResult("?????????");
            serviceOilFlow.setUndueAmount(0L);
            serviceOilFlow.setExpiredAmount(canUseBalance);
            serviceOilFlow.setServiceCharge(0L);
            serviceOilFlow.setProductName(serviceProduct.getProductName());
            serviceOilFlow.setAddress(serviceProduct.getAddress());
            serviceOilFlow.setServiceCall(serviceProduct.getServiceCall());
            serviceOilFlow.setOilBalance(0L);
            serviceOilFlow.setBalance(canUseBalance);
            serviceOilFlow.setSubjectsId(EnumConsts.SubjectIds.BALANCE_CONSUME_OIL_SUB);
            serviceOilFlow.setPlateNumber(plateNumber);
            serviceOilFlow.setFromType(fromType);
            iConsumeOilFlowService.save(serviceOilFlow);
            //???????????????????????????????????????????????????????????????????????????
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(serviceUserId, OrderAccountConst.PAY_TYPE.SERVICE,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, canUseBalance, -1L,
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, serviceOilFlow.getId(),
                    -1L, OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1, OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1,
                    userId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.PAY_FOR_OIL_CODE,
                    EnumConsts.SubjectIds.BALANCE_CONSUME_OIL_SUB, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,
                    SysStaticDataEnum.USER_TYPE.DRIVER_USER,SysStaticDataEnum.USER_TYPE.SERVICE_USER,0L,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOtherOperator.getBillId()));
            payoutIntf.setPayTime(LocalDateTime.now());
            payoutIntf.setBusiCode(oilOrder);
            payoutIntf.setPlateNumber(plateNumber);
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            //????????????
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????(?????????????????????)
            int inbankType = EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;//????????????
            //??????????????????????????????????????????????????????????????????????????????????????????
            if (iAccountBankUserTypeRelService.isUserTypeBindCard(serviceUserId,
                    SysStaticDataEnum.USER_TYPE.SERVICE_USER,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                inbankType = EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;//????????????
            } else if (!iAccountBankUserTypeRelService.isUserTypeBindCard(serviceUserId,
                    SysStaticDataEnum.USER_TYPE.SERVICE_USER,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("???????????????????????????????????????????????????!");
            } else {
                AccountBankRel bank = accountBankRelService.getDefaultAccountBankRel(serviceUserId,
                        EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                if (bank != null) {
                    payoutIntf.setAccNo(bank.getPinganCollectAcctId());
                }
            }
            PayReturnDto payReturnOut = iBaseBusiToOrderService.payMemberTransaction(userId, serviceUserId, canUseBalance,
                    EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT, inbankType,
                    payoutIntf.getId()+"",true,"",
                    "",SysStaticDataEnum.USER_TYPE.DRIVER_USER,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            if (payReturnOut == null) {
                throw new BusinessException("???????????????????????????");
            }
            //????????????
            if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts1) {
                payoutIntf.setRespCode(HttpsMain.netTimeOutFail);
                payoutIntf.setRespMsg("????????????");
                payoutIntf.setRespBankCode(PinganIntefaceConst.RESP_BANK_CODE.BANK_OTHER_ERROR);
            }
            //??????
            if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts0) {
                payoutIntf.setRespCode(HttpsMain.respCodeSuc);
                payoutIntf.setRespMsg("??????");
                payoutIntf.setPayTime(LocalDateTime.now());
                payoutIntf.setCompleteTime(DateUtil.formatDate(new Date(),DATETIME12_FORMAT2));
            }
            String thirdLogNo = payReturnOut.getThirdLogNo();
            payoutIntf.setRemark("?????????????????????????????????????????????:" + thirdLogNo);
            payoutIntf.setSerialNumber(thirdLogNo);
            iPayoutIntfService.saveOrUpdate(payoutIntf);
        }
        return oilFlow;
    }

    @Override
    public void pledgeOrReleaseOilCardAmount(long userId, String vehicleAffiliation, long amountFee, long orderId, long tenantId, int pledgeType)  {

    }

    @Override
    public OrderOilSource saveOrderOilSource(Long userId, long orderId, long sourceOrderId, long sourceAmount, long noPayOil, long paidOil, Long sourceTenantId, Date createDate, long opId, int isNeedBill, String vehicleAffiliation, Date orderDate, String oilAffiliation, Integer oilConsumer, long rebateOil, long noRebateOil, long paidRebateOil, long creditOil, long noCreditOil, long paidCreditOil, int userType, int oilAccountType, int oilBillType) {
        return null;
    }

    @Override
    public void oilEntityVerification(long userId, String billId, String vehicleAffiliation, long orderId, long amount, long tenantId) throws Exception {

    }

    @Override
    public boolean judgeOrderIsPledgeOilCard(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
//        List<PayoutIntf> list = payoutIntfSV.queryPayoutIntf(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,
//                EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN, orderId);
        QueryWrapper<PayoutIntf> wrapper = new QueryWrapper<>();
        wrapper.eq("BUSI_ID", EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD)
                .eq("SUBJECTS_ID",EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN)
                .eq("ORDER_ID",orderId);
        List<PayoutIntf> payoutIntfs = payoutIntfMapper.selectList(wrapper);
        if (payoutIntfs != null && payoutIntfs.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public OrderPaymentWayOilOut getOrderPaymentWayOil(Long userId, Long tenantId, int userType) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
        //???????????????????????????
        List<OrderOilSource> list = orderOilSourceService.getOrderOilSourceByUserId(userId,userType);
        //???????????????????????????
        List<RechargeOilSource> list1 = iRechargeOilSourceService.getRechargeOilSourceByUserId(userId,userType);
        //??????
        long costOil = 0;
        //????????????
        long expenseOil = 0;
        //??????
        long contractOil = 0;

        if (list != null && list.size() > 0) {
            for (OrderOilSource oilSource : list) {
                if (oilSource.getTenantId().longValue() != tenantId.longValue()) {
                    continue;
                }
                long noPayOil = oilSource.getNoPayOil();
                long noCreditOil = oilSource.getNoCreditOil();
                long noRebateOil = oilSource.getNoRebateOil();
                long orderOil = (noPayOil + noCreditOil + noRebateOil);
                //????????????????????????????????????????????????????????????????????????
                Long orderId = oilSource.getOrderId();
                OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
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
                    OrderInfoExtH orderInfoExtH = iOrderInfoExtHService.getOrderInfoExtH(orderId);
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
    public void rechargeOrderAccountOil(Long userId, Long rechargeAmount, int isNeedBill, Integer oilAccountType, int userType, Integer oilBillType,String accessToken)   {
        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????id");
        }
        if (rechargeAmount == null || rechargeAmount <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (user.getTenantId() == null || user.getTenantId() <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (oilAccountType == null || oilAccountType <= 0) {
            throw new BusinessException("????????????????????????");
        }
        if (oilBillType == null || oilBillType <= 0) {
            throw new BusinessException("????????????????????????");
        }
        //todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "rechargeOrderAccountOil" + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
		/*boolean isOwnCarUser = userTF.isOwnCarUser(userId, tenantId);
		if (!isOwnCarUser) {
			throw new BusinessException("?????????????????????????????????????????????????????????");
		}*/
        if (userType != SysStaticDataEnum.USER_TYPE.DRIVER_USER) {
            throw new BusinessException("???????????????????????????????????????");
        }
		/*OrderPaymentWayOilOut out = this.getOrderPaymentWayOil(userId,tenantId,userType);
		if (out != null) {
			if ((out.getContractOil() != null && out.getContractOil() > 0) || (out.getCostOil() != null && out.getCostOil() > 0)) {
				throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????");
			}
		}*/
        this.rechargeOil(userId, rechargeAmount, isNeedBill, oilAccountType, userType, oilBillType,accessToken);
        // ????????????
        String remark =  "???????????????" + new BigDecimal((float)rechargeAmount/100).setScale(2, BigDecimal.ROUND_HALF_UP).toString() ;
        iSysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.AccountQuery,userId , SysOperLogConst.OperType.Add,user.getName() + remark);

    }

    @Override
    public void rechargeByCustomerOil(Long userId, Long rechargeAmount, Long tenantId, int userType) throws Exception {

    }

    @Override
    public void rechargeByFleetOil(Long userId, Long rechargeAmount, int isNeedBill, Long tenantId, Integer oilConsumer, int userType) throws Exception {

    }

    @Override
    public RechargeOilSource saveRechargeOilSource(Long userId, Long fromFlowId, String rechargeOrderId, String sourceOrderId, long sourceAmount, long noPayOil, long paidOil, Long tenantId, Date createDate, long opId, int isNeedBill, String vehicleAffiliation, Date orderDate, String oilAffiliation, String rechargeType, Integer oilConsumer, long rebateOil, long noRebateOil, long paidRebateOil, long creditOil, long noCreditOil, long paidCreditOil, int userType, Integer oilAccountType, Integer oilBillType,String accessToken)  {
        LoginInfo user = loginUtils.get(accessToken);
        RechargeOilSource ros = new RechargeOilSource();
        ros.setUserId(userId);

        //????????????????????????
        ros.setUserType(userType);
        //????????????????????????
        ros.setFromFlowId(fromFlowId);
        ros.setRechargeOrderId(rechargeOrderId);
        ros.setSourceOrderId(sourceOrderId);
        ros.setSourceAmount(sourceAmount);
        ros.setNoPayOil(noPayOil);
        ros.setPaidOil(paidOil);
        if (user != null && user.getTenantId() != null && user.getTenantId() > 0) {
            ros.setTenantId(user.getTenantId());
        } else {
            ros.setTenantId(tenantId);//?????????????????????
        }
        ros.setOrderDate(getDateToLocalDateTime(orderDate));
        ros.setCreateTime(getDateToLocalDateTime(createDate));
        ros.setOpId(opId);
        ros.setIsNeedBill(isNeedBill);
        ros.setVehicleAffiliation(vehicleAffiliation);
        ros.setMatchAmount(noPayOil);
        ros.setOilAffiliation(oilAffiliation);
        ros.setRechargeType(Integer.parseInt(rechargeType));
        ros.setOilConsumer(oilConsumer);
        ros.setSourceTenantId(tenantId);
        ros.setRebateOil(rebateOil);
        ros.setNoRebateOil(noRebateOil);
        ros.setPaidRebateOil(paidRebateOil);
        ros.setCreditOil(creditOil);
        ros.setNoCreditOil(noCreditOil);
        ros.setPaidCreditOil(paidCreditOil);
        ros.setOilAccountType(oilAccountType);
        ros.setOilBillType(oilBillType);
        iRechargeOilSourceService.save(ros);
        return ros;
    }

    @Override
    public Map<String, Object> clearAccountOil(Long userId, String accessToken){
        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????id");
        }
        if (user.getTenantId() == null || user.getTenantId() <= 0) {
            throw new BusinessException("???????????????id");
        }
        //todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "clearAccountOil" + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        OrderPaymentWayOilOut out = this.getOrderPaymentWayOil(userId,user.getTenantId(),SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (out != null) {
            if (out.getExpenseOil() == null || out.getExpenseOil() <= 0) {
                throw new BusinessException("????????????????????????????????????????????????????????????????????????");
            }
        }
        long soNbr = CommonUtil.createSoNbr();
        long clearAmount = 0;
        //???????????????
        long clearOrderOil = this.clearOrderOil(userId, user.getTenantId(), soNbr,accessToken);
        //???????????????
        long clearRechargeOil = this.clearRechargeOil(userId, user.getTenantId(), soNbr,accessToken);
        clearAmount = (clearOrderOil + clearRechargeOil);
        //????????????
        String remark = "?????????????????????  " + new BigDecimal((float)clearAmount/100).setScale(2, BigDecimal.ROUND_HALF_UP).toString() ;
        iSysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.AccountQuery,userId ,
                SysOperLogConst.OperType.Add,user.getName() + remark,user.getTenantId());
        return null;
    }

    @Override
    public long clearOrderOil(Long userId, Long tenantId, long soNbr,String accessToken)  {
        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????id");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("????????????id" + tenantId + "???????????????????????????id!");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //???????????????????????????????????????
        TenantUserRel userRel = iTenantUserRelService.getAllTenantUserRelByUserId(userId, user.getTenantId());
        boolean isOwnCarUser = null != userRel && userRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR;
        if (!isOwnCarUser) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????");
        }
        long clearOrderOil = 0;
        //???????????????????????????
        List<OrderOilSource> list = orderOilSourceService.getOrderOilSourceByUserId(userId,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (list != null && list.size() > 0) {
            List<BusiSubjectsRel> fleetOilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel fleetOilSubjectsRel = new BusiSubjectsRel();
            fleetOilSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.CLEAR_ACCOUNT_OIL_ALLOT);
            List<BusiSubjectsRel> driverOilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel driverOilSubjectsRel = new BusiSubjectsRel();
            driverOilSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.CLEAR_ORDER_ACCOUNT_OIL_FEE);
            for (OrderOilSource oos : list) {
                if (oos.getTenantId().longValue() != tenantId.longValue()) {
                    continue;
                }
                Long orderId = oos.getOrderId();

                OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
                boolean isExpense = false;
                if (orderInfoExt != null) {
                    //????????????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                        isExpense = true;
                    }
                } else {
                    OrderInfoExtH  orderInfoExtH = iOrderInfoExtHService.getOrderInfoExtH(orderId);
                    if (orderInfoExtH != null) {
                        //????????????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                            isExpense = true;
                        }
                    } else {
                        throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
                    }
                }
                if (isExpense) {
                    long noPayOil = oos.getNoPayOil();
                    long noCreditOil = oos.getNoCreditOil();
                    long noRebateOil = oos.getNoRebateOil();
                    clearOrderOil += (noPayOil + noCreditOil + noRebateOil);
                    //?????????????????????
                    if (orderId.longValue() != oos.getSourceOrderId().longValue()) {
                        OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(tenantUserId, oos.getVehicleAffiliation(),oos.getSourceTenantId(),oos.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                        fleetOilSubjectsRel.setAmountFee(noPayOil + noCreditOil + noRebateOil);
                        fleetOilList.add(fleetOilSubjectsRel);
                        // ??????????????????
                        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL, fleetOilList);
                        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CLEAR_ACCOUNT_OIL,
                                userId, "", tenantAccount, busiSubjectsRelList, soNbr, orderId,"", null, tenantId, null, "", null, tenantAccount.getVehicleAffiliation(),user);
                        fleetOilList.clear();
                    } else {
                        //???????????????
                        if (oos.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE ) {
                            //???????????????????????????????????????
                            Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                            if (oos != null && oos.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && oos.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                                List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(oos.getOrderId());
                                if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                                    BaseBillInfo bbi = baseBillInfoList.get(0);
                                    if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
                                        recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                                    } else {
                                        bbi.setOil(bbi.getOil() - 0);
                                        bbi.setWithdrawAmount(bbi.getWithdrawAmount() - 0);
                                        bbi.setUpdateTime(LocalDateTime.now());
                                        baseBillInfoService.saveOrUpdate(bbi);
                                        recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                                    }
                                }
                            }
                            Long tempNoPayOil = oos.getNoPayOil();
                            Long tempNoRebateOil = oos.getNoRebateOil();
                            Long tempNoCreditOil = oos.getNoCreditOil();
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("noPayOil", tempNoPayOil);
                            map.put("noRebateOil", tempNoRebateOil);
                            map.put("noCreditOil", tempNoCreditOil);
                            oilSourceRecordService.recallOil(userId, String.valueOf(oos.getOrderId()),
                                    tenantUserId, EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT,
                                    tenantId, map,recallType,user);
                        }
                    }
                    //???????????????????????????
                    OrderAccount account = iOrderAccountService.queryOrderAccount(userId, oos.getVehicleAffiliation(),
                            oos.getSourceTenantId(),oos.getOilAffiliation(),oos.getUserType());
                    driverOilSubjectsRel.setAmountFee(noPayOil + noCreditOil + noRebateOil);
                    driverOilList.add(driverOilSubjectsRel);
                    // ??????????????????
                    List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL, driverOilList);
                    OrderResponseDto orderResponseDto = new OrderResponseDto();
                    orderResponseDto.setOrderOilSource(oos);
                    iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CLEAR_ACCOUNT_OIL,
                            0L, "", account, busiSubjectsRelList, soNbr, orderId,"",
                            null, tenantId, null, "", orderResponseDto, account.getVehicleAffiliation(),user);

                    // ?????????????????????????????????????????????
                    ParametersNewDto parametersNewDto = busiToOrderUtils.setParametersNew(userId,
                            sysOperator.getBillId(), EnumConsts.PayInter.CLEAR_ACCOUNT_OIL,
                            oos.getOrderId(),noPayOil,oos.getVehicleAffiliation(),"");
                    parametersNewDto.setTenantId(tenantId);
                    parametersNewDto.setOilSource(oos);
                    parametersNewDto.setBatchId(String.valueOf(soNbr));
                    parametersNewDto.setTenantUserId(tenantUserId);
                    busiToOrderUtils.busiToOrderNew(parametersNewDto, busiSubjectsRelList,user);
                    driverOilList.clear();
                    //??????????????????
                    this.saveClearAccountOilRecord(userId, OrderAccountConst.CLEAR_ACCOUNT_OIL.CLEAR_OIL_TYPE,
                            OrderAccountConst.CLEAR_ACCOUNT_OIL.ORDER_OIL_SOURCE, oos.getId(), soNbr, account.getId(),
                            noPayOil, tenantId,accessToken);
                }
            }
        }
        return clearOrderOil;
    }

    @Override
    public long clearRechargeOil(Long userId, Long tenantId, long soNbr,String accessToken)  {

        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????id");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("????????????id" + tenantId + "???????????????????????????id!");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        long clearRechargeOil = 0;
        //???????????????????????????
        List<RechargeOilSource> list = iRechargeOilSourceService.getRechargeOilSourceByUserId(userId,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (list != null && list.size() > 0) {
            List<BusiSubjectsRel> fleetOilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel fleetOilSubjectsRel = new BusiSubjectsRel();
            fleetOilSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.CLEAR_ACCOUNT_OIL_ALLOT);
            List<BusiSubjectsRel> driverOilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel driverOilSubjectsRel = new BusiSubjectsRel();
            driverOilSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.CLEAR_RECHARGE_ACCOUNT_OIL_FEE);
            for (RechargeOilSource ros : list) {
                long noPayOil = ros.getNoPayOil();
                long noCreditOil = ros.getNoCreditOil();
                long noRebateOil = ros.getNoRebateOil();
                clearRechargeOil += (noPayOil + noCreditOil + noRebateOil);
                //???????????????
                OrderOilSource source = null;
                if (ros.getFromFlowId() != null && ros.getFromFlowId() > 0) {
                    source = orderOilSourceService.getById(ros.getFromFlowId());
                    if (source == null) {
                        throw new BusinessException("?????????????????????????????????id???" + ros.getFromFlowId() + " ????????????????????????");
                    }
                    OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(tenantUserId,
                            source.getVehicleAffiliation(),source.getSourceTenantId(),
                            source.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                    fleetOilSubjectsRel.setAmountFee(noPayOil + noCreditOil + noRebateOil);
                    fleetOilList.add(fleetOilSubjectsRel);
                    // ??????????????????
                    List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL, fleetOilList);
                    iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CLEAR_ACCOUNT_OIL,
                            userId, "", tenantAccount, busiSubjectsRelList, soNbr, source.getOrderId(),
                            "", null, tenantId, null, "",
                            null, tenantAccount.getVehicleAffiliation(),user);
                    fleetOilList.clear();
                } else {
                    //???????????????
                    if (ros.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE ) {
                        Long tempNoPayOil = ros.getNoPayOil();
                        Long tempNoRebateOil = ros.getNoRebateOil();
                        Long tempNoCreditOil = ros.getNoCreditOil();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("noPayOil", tempNoPayOil);
                        map.put("noRebateOil", tempNoRebateOil);
                        map.put("noCreditOil", tempNoCreditOil);
                        //??????????????????????????????
                        oilSourceRecordService.recallOil(userId, String.valueOf(ros.getRechargeOrderId()), tenantUserId,
                                EnumConsts.SubjectIds.CLEAR_RECHARGE_ACCOUNT_OIL_FEE, tenantId,
                                map,EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1,user);
                    }
                }
                //???????????????????????????
                OrderAccount account = iOrderAccountService.queryOrderAccount(userId,
                        ros.getVehicleAffiliation(),ros.getSourceTenantId(),ros.getOilAffiliation(),ros.getUserType());
                driverOilSubjectsRel.setAmountFee(noPayOil + noCreditOil + noRebateOil);
                driverOilList.add(driverOilSubjectsRel);
                // ??????????????????
                List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL, driverOilList);
                OrderResponseDto orderResponseDto = new OrderResponseDto();
                orderResponseDto.setRechargeOilSource(ros);
                // TODO ?????? 2022-6-27
                if (account!=null){
                    iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CLEAR_ACCOUNT_OIL,
                            0L, "", account, busiSubjectsRelList, soNbr, 0L,"",
                            null, tenantId, null, "", orderResponseDto, account==null?"":account.getVehicleAffiliation(),user);
                }
                // ?????????????????????????????????????????????
                ParametersNewDto parametersNewDto = busiToOrderUtils.setParametersNew(userId, sysOperator.getBillId(),
                        EnumConsts.PayInter.CLEAR_ACCOUNT_OIL, 0L,noPayOil + noCreditOil + noRebateOil,
                        ros.getVehicleAffiliation(),"");
                parametersNewDto.setTenantId(tenantId);
                parametersNewDto.setOilSource(source);
                parametersNewDto.setBatchId(String.valueOf(soNbr));
                parametersNewDto.setTenantUserId(tenantUserId);
                parametersNewDto.setRechargeOilSource(ros);
                busiToOrderUtils.busiToOrderNew(parametersNewDto, busiSubjectsRelList,user);
                driverOilList.clear();
                //??????????????????
                this.saveClearAccountOilRecord(userId, OrderAccountConst.CLEAR_ACCOUNT_OIL.CLEAR_OIL_TYPE,
                        OrderAccountConst.CLEAR_ACCOUNT_OIL.RECHARGE_OIL_SOURCE, ros.getId(), soNbr, account==null?null:account.getId(),
                        noPayOil + noCreditOil + noRebateOil, tenantId,accessToken);
            }
        }
        // ???????????????????????? order_account_oil_source
        iOrderAccountOilSourceService.clearOilBalance(userId, tenantId);
        return clearRechargeOil;
    }

    @Override
    public ClearAccountOilRecord saveClearAccountOilRecord(Long userId, Integer busiType, Integer fromTable, Long fromFlowId, long soNbr, Long accId, Long clearAmount, Long tenantId,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        ClearAccountOilRecord caor = new ClearAccountOilRecord();
        caor.setBusiType(busiType);
        caor.setFromTable(fromTable);
        caor.setFromFlowId(fromFlowId);
        caor.setAccId(accId);
        caor.setClearAmount(clearAmount);
        caor.setUserId(userId);
        caor.setSoNbr(soNbr);
        caor.setCreateTime(LocalDateTime.now());
        caor.setOpId(user.getId());
        caor.setTenantId(tenantId);
        caor.setUpdateTime(LocalDateTime.now());
        caor.setUpdateOpId(user.getOrgId());
        iClearAccountOilRecordService.save(caor);
        return caor;
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
    @Override
    public ConsumeOilFlow payForOrderOil(long userId, long serviceUserId, long productId, Long oilPrice, Float oilRise, long amountFee, String orderNum, Long tenantId, String plateNumber, Integer fromType, Long id) throws Exception {
        return null;
    }

    @Override
    public OilAccountOutListDto getOilAccount(Long userId) {
        if (userId == null || userId <= 1) {
            throw new BusinessException("???????????????id");
        }
        OilAccountOutListDto oilAccountOutList = new OilAccountOutListDto();
        List<OilAccountOutDto> oaoList = new ArrayList<>();
        //????????????????????????
        List<OrderOilSource> oosList = iOrderOilSourceService.getOrderOilSourceByUserId(userId,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        // ????????????????????????
        List<RechargeOilSource> rosList = iRechargeOilSourceService.getRechargeOilSourceByUserId(userId,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        oilAccountOutList.setOaoList(oaoList);
		/*oilAccountOutList.setOosList(oosList);
		oilAccountOutList.setRosList(rosList);*/
        List<OrderOilSource> orderList = new ArrayList<>();
        List<RechargeOilSource> rechargeList = new ArrayList<>();
        oilAccountOutList.setOosList(orderList);
        oilAccountOutList.setRosList(rechargeList);
        List<Object> tempList = new ArrayList<Object>();
        tempList.addAll(oosList);
        tempList.addAll(rosList);

        Map<String, OilAccountOutDto> map = new HashMap<String, OilAccountOutDto>();
        if (tempList != null && tempList.size() > 0) {
            for (Object obj : tempList) {
                Integer oilConsumer = -1;
                Long tenantId = 0L;
                long noPayOil = 0;
                long noRebateOil = 0;
                long noCreditOil = 0;
                long amount = 0;//?????????
                String vehicleAffiliation = "";
                String oilAffiliation = "";
                Integer userType = -1;
                //
                if (obj instanceof OrderOilSource) {
                    OrderOilSource oos = (OrderOilSource) obj;
                    oilConsumer = oos.getOilConsumer().intValue();
                    noPayOil = oos.getNoPayOil();
                    noRebateOil = oos.getNoRebateOil();
                    noCreditOil = oos.getNoCreditOil();
                    // TODO 2022-6-28 ??????
                    tenantId = oos.getSourceTenantId()==0L?oos.getTenantId():oos.getSourceTenantId();
                    vehicleAffiliation = oos.getVehicleAffiliation();
                    oilAffiliation = oos.getOilAffiliation();
                    userType = oos.getUserType();
                } else if (obj instanceof RechargeOilSource) {
                    RechargeOilSource ros = (RechargeOilSource) obj;
                    oilConsumer = ros.getOilConsumer().intValue();
                    noPayOil = ros.getNoPayOil();
                    noRebateOil = ros.getNoRebateOil();
                    noCreditOil = ros.getNoCreditOil();
                    // TODO 2022-6-27 ??????
                    tenantId = ros.getSourceTenantId()==0L?ros.getTenantId():ros.getSourceTenantId();
                    vehicleAffiliation = ros.getVehicleAffiliation();
                    oilAffiliation = ros.getOilAffiliation();
                    userType = ros.getUserType();
                } else {
                    throw new BusinessException("?????????????????????");
                }
                OrderAccount oa = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, userType);
                if (oa.getAccState() == null || oa.getAccState() == 0) { //????????????
                    continue;
                }
                if (obj instanceof OrderOilSource) {
                    OrderOilSource oos = (OrderOilSource) obj;
                    orderList.add(oos);
                } else if (obj instanceof RechargeOilSource) {
                    RechargeOilSource ros = (RechargeOilSource) obj;
                    rechargeList.add(ros);
                }
                String key = String.valueOf(tenantId) + "|" + String.valueOf(oilConsumer);
                amount += (noPayOil + noRebateOil + noCreditOil);
                if (map.get(key) != null) {
                    OilAccountOutDto out = map.get(key);
                    out.setAmount(out.getAmount() + amount);
                } else {
                    OilAccountOutDto out = new OilAccountOutDto();
                    out.setAmount(amount);
                    out.setOilConsumer(oilConsumer);
                    out.setTenantId(tenantId);
                    out.setUserId(userId);
                    map.put(key, out);
                    oaoList.add(out);
                }
            }
        }
        return oilAccountOutList;
    }

    @Override
    public Map<String, Object> payOrderAccountOil(Long userId, Long serviceUserId, List<Long> sourceTenantIdList,
                                                  Map<String, Object> inParm, Long oilPrice, long canUseOil,
                                                  boolean isSharePorduct, long soNbr, Long productId, String orderNum,String accessToken)  {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        //????????????id??????????????????
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(productId);
        if (serviceProduct == null) {
            throw new BusinessException("????????????id???" + productId + ",?????????????????????");
        } else {
            if (serviceProduct.getState() == null || serviceProduct.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2) {
                throw new BusinessException("??????id??????" + productId + "?????????????????????");
            }
        }

        SysUser sysOtherOperator = sysUserService.getSysOperatorByUserId(serviceUserId);
        if (sysOtherOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        // TODO ?????????
        OilAccountOutListDto oilAccountOutList = this.getOilAccount(userId);
        if (oilAccountOutList == null) {
            throw new BusinessException("?????????????????????");
        }

        List<ConsumeOilFlow> oilFlowList = new ArrayList<ConsumeOilFlow>();
        List<OrderOilSource> orderOilSourceList = oilAccountOutList.getOosList();
        List<RechargeOilSource> rechargeOilSourceList = oilAccountOutList.getRosList();
        List<Object> list = new ArrayList<Object>();
        list.addAll(orderOilSourceList);
        list.addAll(rechargeOilSourceList);
        List<Object> tempList = new ArrayList<Object>();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();

        long totalAmount = canUseOil;
        if (isSharePorduct) {//?????????
            for (Object oos : list) {
                if (totalAmount == 0) {
                    break;
                }
                String vehicleAffiliation = "";
                String oilAffiliation = "";
                Integer oilConsumer = -1;
                Long sourceTenantId = 0l;
                long noPayOil = 0;
                long noCreditOil = 0;
                long noRebateOil = 0;
                long subjectsId = 0l;
                Integer userType = null;
                if (oos instanceof OrderOilSource) {
                    OrderOilSource source = (OrderOilSource) oos;
                    vehicleAffiliation = source.getVehicleAffiliation();
                    oilAffiliation = source.getOilAffiliation();
                    sourceTenantId = source.getSourceTenantId();
                    noPayOil = source.getNoPayOil() == null ? 0L :  source.getNoPayOil();
                    noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
                    noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
                    oilConsumer = source.getOilConsumer();
                    subjectsId = EnumConsts.SubjectIds.CONSUME_OIL_SUB;
                    userType = source.getUserType();
                } else if (oos instanceof RechargeOilSource) {
                    RechargeOilSource source = (RechargeOilSource) oos;
                    vehicleAffiliation = source.getVehicleAffiliation();
                    oilAffiliation = source.getOilAffiliation();
                    sourceTenantId = source.getSourceTenantId();
                    noPayOil = source.getNoPayOil() == null ? 0L :  source.getNoPayOil();
                    noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
                    noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
                    oilConsumer = source.getOilConsumer();
                    subjectsId = EnumConsts.SubjectIds.CONSUME_RECHARGE_OIL_SUB;
                    userType = source.getUserType();
                } else {
                    throw new BusinessException("???????????????????????????");
                }

                long oilAmount = (noPayOil + noCreditOil + noRebateOil);
                if (oilAmount <= 0) {
                    continue;
                }
                if (oilConsumer != null && oilConsumer == OrderConsts.OIL_CONSUMER.SHARE) {
                    long amount = 0;
                    if (oilAmount >= totalAmount) {
                        amount = totalAmount;
                        totalAmount = 0;
                    } else {
                        amount = oilAmount;
                        totalAmount -= oilAmount;
                    }
                    OrderAccount ac = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, sourceTenantId,oilAffiliation,userType);
                    tempList.add(oos);
                    MatchAmountUtil.matchAmounts(amount, 0, 0, "noPayOil", "noRebateOil", "noCreditOil", tempList);
                    if (oos instanceof OrderOilSource) {
                        OrderOilSource source = (OrderOilSource) oos;
                        if (source.getMatchAmount() == null || source.getMatchAmount() != amount) {
                            throw new BusinessException("???????????????????????????");
                        }
                        if (ac.getOilBalance() < amount) {
                            throw new BusinessException("????????????????????????????????????");
                        }
                    } else if (oos instanceof RechargeOilSource) {
                        RechargeOilSource source = (RechargeOilSource) oos;
                        if (source.getMatchAmount() == null || source.getMatchAmount() != amount) {
                            throw new BusinessException("???????????????????????????");
                        }
                        if (ac.getRechargeOilBalance() < amount) {
                            throw new BusinessException("????????????????????????????????????");
                        }
                    }
                    // ??????????????????
                    BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(subjectsId, amount);
                    busiList.add(amountFeeSubjectsRel);

                    // ??????????????????
                    List<BusiSubjectsRel> busiSubjectsList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_OIL_CODE, busiList);
//                    Map<String, Object> accountParam = new HashMap<String, Object>();
//                    accountParam.put("oilSource", oos);
                    OrderResponseDto dto = new OrderResponseDto();
                    BeanUtil.copyProperties(oos, dto);
                    iAccountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.PAY_FOR_OIL_CODE, sysOtherOperator.getUserInfoId(),
                            sysOtherOperator.getName(), ac, busiSubjectsList, soNbr, 0L,sysOperator.getName(),
                            null, ac.getSourceTenantId(), String.valueOf(productId), serviceProduct.getProductName(), dto, vehicleAffiliation,loginInfo);

                    Map<String, Object> result = this.payForServiceOil(userId, serviceUserId, oilPrice, amount, soNbr, productId, oos,orderNum,accessToken);
                    ConsumeOilFlow flow = (ConsumeOilFlow) result.get("consumeOilFlow");
                    ConsumeOilFlowExt ext = (ConsumeOilFlowExt) result.get("consumeOilFlowExt");
                    oilFlowList	.add(flow);

                    // ?????????????????????????????????????????????

                    ParametersNewDto dto1  = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(),
                            sysOperator.getBillId(),EnumConsts.PayInter.PAY_FOR_OIL_CODE, 0L, canUseOil, vehicleAffiliation,"");
//                    inParamNew.put("serviceUserId", serviceUserId);
//                    inParamNew.put("tenantId",sourceTenantId);
//                    inParamNew.put("productId", productId);
//                    inParamNew.put("oilSource", oos);
//                    inParamNew.put(OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE, OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE1);
//                    inParamNew.put("consumeOilFlow", flow);
//                    inParamNew.put("consumeOilFlowExt", ext);
//                    busiToOrder.busiToOrderNew(inParamNew, busiSubjectsList);
                    dto1.setServiceUserId(serviceUserId);
                    dto1.setTenantId(sourceTenantId);
                    dto1.setProductId(productId);
                    OrderOilSource source = new OrderOilSource();
                    BeanUtil.copyProperties(dto,source);
                    dto1.setOilSource(source);
                    dto1.setConsumeOilFlow(flow);
                    dto1.setConsumeOilFlowExt(ext);
                    iOrderOilSourceService.busiToOrderNew(dto1, busiSubjectsList,loginInfo);
                    tempList.clear();
                    busiList.clear();
                }
            }
        } else {//?????????
            for (Object oos : list) {
                if (totalAmount == 0) {
                    break;
                }
                String vehicleAffiliation = "";
                String oilAffiliation = "";
                Integer oilConsumer = -1;
                Long sourceTenantId = 0l;
                long noPayOil = 0;
                long noCreditOil = 0;
                long noRebateOil = 0;
                long subjectsId = 0l;
                Integer userType = null;
                if (oos instanceof OrderOilSource) {
                    OrderOilSource source = (OrderOilSource) oos;
                    vehicleAffiliation = source.getVehicleAffiliation();
                    oilAffiliation = source.getOilAffiliation();
                    sourceTenantId = source.getSourceTenantId();
                    noPayOil = source.getNoPayOil() == null ? 0L :  source.getNoPayOil();
                    noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
                    noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
                    oilConsumer = source.getOilConsumer();
                    subjectsId = EnumConsts.SubjectIds.CONSUME_OIL_SUB;
                    userType = source.getUserType();
                } else if (oos instanceof RechargeOilSource) {
                    RechargeOilSource source = (RechargeOilSource) oos;
                    vehicleAffiliation = source.getVehicleAffiliation();
                    oilAffiliation = source.getOilAffiliation();
                    sourceTenantId = source.getSourceTenantId();
                    noPayOil = source.getNoPayOil() == null ? 0L :  source.getNoPayOil();
                    noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
                    noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
                    oilConsumer = source.getOilConsumer();
                    subjectsId = EnumConsts.SubjectIds.CONSUME_RECHARGE_OIL_SUB;
                    userType = source.getUserType();
                } else {
                    throw new BusinessException("???????????????????????????");
                }
                if (!sourceTenantIdList.contains(sourceTenantId)) {
                    continue;
                }
                long oilAmount = (noPayOil + noCreditOil + noRebateOil);
                if (oilAmount <= 0) {
                    continue;
                }
                TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(sourceTenantId, serviceUserId);
                if (tenantServiceRel == null) {
                    throw new BusinessException("????????????id???" + sourceTenantId + " ???????????????id???" + serviceUserId + " ?????????????????????????????????");
                }
                Long noUseQuotaAmt = null;
                if (tenantServiceRel.getQuotaAmt() != null) {//???null?????? ????????????????????????
                    Long useQuotaAmt = tenantServiceRel.getUseQuotaAmt() == null ? 0L : tenantServiceRel.getUseQuotaAmt();
                    noUseQuotaAmt = tenantServiceRel.getQuotaAmt() - useQuotaAmt;
                    if (noUseQuotaAmt < 0) {
                        throw new BusinessException("????????????id???" + sourceTenantId + " ???????????????id???" + serviceUserId + " ????????????????????????????????????????????????????????????");
                    }
                }
                if (oilConsumer != null && oilConsumer == OrderConsts.OIL_CONSUMER.SELF) {
                    long amount = 0;
                    if (oilAmount >= totalAmount) {
                        if (noUseQuotaAmt != null) {
                            if (totalAmount >= noUseQuotaAmt) {
                                amount = noUseQuotaAmt;
                                totalAmount -= noUseQuotaAmt;
                            } else {
                                amount = totalAmount;
                                totalAmount = 0;
                            }
                        } else {
                            amount = totalAmount;
                            totalAmount = 0;
                        }
                    } else {
                        amount = oilAmount;
                        totalAmount -= oilAmount;
                    }
                    OrderAccount ac = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, sourceTenantId,oilAffiliation,userType);
                    tempList.add(oos);
                    MatchAmountUtil.matchAmounts(amount, 0, 0, "noPayOil", "noRebateOil", "noCreditOil", tempList);
                    if (oos instanceof OrderOilSource) {
                        OrderOilSource source = (OrderOilSource) oos;
                        if (source.getMatchAmount() == null || source.getMatchAmount() != amount) {
                            throw new BusinessException("???????????????????????????");
                        }
                        if (ac.getOilBalance() < amount) {
                            throw new BusinessException("????????????????????????????????????");
                        }
                    } else if (oos instanceof RechargeOilSource) {
                        RechargeOilSource source = (RechargeOilSource) oos;
                        if (source.getMatchAmount() == null || source.getMatchAmount() != amount) {
                            throw new BusinessException("???????????????????????????");
                        }
                        if (ac.getRechargeOilBalance()!=null){
                            if (ac.getRechargeOilBalance() < amount) {
                                throw new BusinessException("????????????????????????????????????");
                            }
                        }
                    }
                    // ??????????????????
                    BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(subjectsId, amount);
                    busiList.add(amountFeeSubjectsRel);

                    // ??????????????????
                    List<BusiSubjectsRel> busiSubjectsList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_OIL_CODE, busiList);
//                    Map<String, Object> accountParam = new HashMap<String, Object>();
//                    accountParam.put("oilSource", oos);
                    // TODO 2022-6-28 ??????
                    OrderResponseDto dtos  = new OrderResponseDto();
                    RechargeOilSource sources = new RechargeOilSource();
                    OrderOilSource orderOilSource = new OrderOilSource();
                    if (oos instanceof RechargeOilSource){
                         sources = (RechargeOilSource) oos;
                    }else  if (oos instanceof OrderOilSource){
                        orderOilSource = (OrderOilSource) oos;
                    }
                    dtos.setRechargeOilSource(sources);
                    dtos.setOrderOilSource(orderOilSource);
                    // ??????????????????????????? ???????????????????????? ??? ???????????????
                    dtos.setOilSource(orderOilSource);
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.PAY_FOR_OIL_CODE, sysOtherOperator.getUserInfoId(),
                            sysOtherOperator.getName(), ac, busiSubjectsList, soNbr, 0L,sysOperator.getName(), null,
                            ac.getSourceTenantId(), String.valueOf(productId), serviceProduct.getProductName(), dtos, vehicleAffiliation,loginInfo);

                    Map<String, Object> result = this.payForServiceOil(userId, serviceUserId, oilPrice, amount, soNbr, productId, oos,orderNum,accessToken);
                    ConsumeOilFlow flow = (ConsumeOilFlow) result.get("consumeOilFlow");
                    ConsumeOilFlowExt ext = (ConsumeOilFlowExt) result.get("consumeOilFlowExt");
                    oilFlowList	.add(flow);

                    // ?????????????????????????????????????????????
                    ParametersNewDto dto1 = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(),
                            sysOperator.getBillId(), EnumConsts.PayInter.PAY_FOR_OIL_CODE, 0L, canUseOil, vehicleAffiliation, "");

                      // TODO
//                    inParamNew.put(OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE, OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE1);
                    dto1.setServiceUserId(serviceUserId);
                    dto1.setTenantId(sourceTenantId);
                    dto1.setProductId(productId);
                    OrderOilSource source = new OrderOilSource();
                    BeanUtil.copyProperties(dtos.getOilSource(),source);
                    dto1.setOilSource(source);
                    dto1.setConsumeOilFlow(flow);
                    dto1.setConsumeOilFlowExt(ext);
                    dto1.setRechargeOilSource(sources);
                    iOrderOilSourceService.busiToOrderNew(dto1, busiSubjectsList,loginInfo);
                    tempList.clear();
                    busiList.clear();
                }
            }
        }
        inParm.put("consumeOilFlowList", oilFlowList);
        return inParm;
    }

    @Override
    public Map<String, Object> payForServiceOil(Long userId ,Long serviceUserId,Long oilPrice,
                                                long amount,long soNbr,Long productId,Object obj,String orderNum,String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        ServiceInfo  serviceInfo = iServiceInfoService.getServiceUserId(serviceUserId);
        if (serviceInfo == null) {
            throw new BusinessException("????????????????????????");
        }
        //????????????id??????????????????
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(productId);
        if (serviceProduct == null) {
            throw new BusinessException("????????????id???" + productId + ",?????????????????????");
        } else {
            if (serviceProduct.getState() == null || serviceProduct.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2) {
                throw new BusinessException("??????id??????" + productId + "?????????????????????");
            }
        }
        SysUser sysOtherOperator = iSysOperatorService.getSysOperatorByUserId(serviceUserId);
        if (sysOtherOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        String vehicleAffiliation = "";
        String oilAffiliation = "";
        Long sourceTenantId = 0l;
        Long tenantId = 0l;
        Integer oilConsumer = -1;
        long matchNoPayOil = 0;
        long matchNoRebateOil = 0;
        long matchNoCreditOil = 0;
        long otherFlowId = 0;
        Integer sourceRecordType = -1;
        Integer creditLimit = OrderAccountConst.CONSUME_OIL_FLOW_EXT.CREDIT_LIMIT0;
        if (obj instanceof OrderOilSource) {
            OrderOilSource source = (OrderOilSource) obj;
            vehicleAffiliation = source.getVehicleAffiliation();
            oilAffiliation = source.getOilAffiliation();
            sourceTenantId = source.getSourceTenantId();
            tenantId = source.getTenantId();
            oilConsumer = source.getOilConsumer();
            matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
            matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
            matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
            otherFlowId = source.getId();
            sourceRecordType = OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1;
        } else if (obj instanceof RechargeOilSource) {
            RechargeOilSource source = (RechargeOilSource) obj;
            vehicleAffiliation = source.getVehicleAffiliation();
            oilAffiliation = source.getOilAffiliation();
            sourceTenantId = source.getSourceTenantId();
            tenantId = source.getTenantId();
            oilConsumer = source.getOilConsumer();
            matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
            matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
            matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
            otherFlowId = source.getId();
            sourceRecordType = OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE2;
        } else {
            throw new BusinessException("?????????????????????");
        }
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????
        boolean isCollection = false;
        Long collectionUserId = null;
        if (oilConsumer == OrderConsts.OIL_CONSUMER.SHARE  && serviceInfo.getAgentCollection() != null && serviceInfo.getAgentCollection() == 1) {
            AgentServiceDto agentService1 = iServiceInfoService.getAgentService(sourceTenantId, ServiceConsts.AGENT_SERVICE_TYPE.OIL);
            if (agentService1 == null ) {
                throw new BusinessException("????????????id???" + sourceTenantId + " ???????????????????????????????????????????????????");
            }
            AgentServiceInfo agentService = agentService1.getAgentServiceInfo();
            collectionUserId = agentService.getServiceUserId();
            isCollection = true;
        } else if (oilConsumer == OrderConsts.OIL_CONSUMER.SHARE  && (serviceInfo.getAgentCollection() == null || serviceInfo.getAgentCollection() != 1)) {
            throw new BusinessException(serviceInfo.getServiceName() + "?????????????????????????????????");
        }
        //???????????????
        OrderAccount accountOil = iOrderAccountService.queryOrderAccount(isCollection ? collectionUserId : serviceUserId,
                vehicleAffiliation,0L,sourceTenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        List<BusiSubjectsRel> busiOilList = new ArrayList<BusiSubjectsRel>();
        long subjectsId = 0l;
        if (oilConsumer == OrderConsts.OIL_CONSUMER.SELF ) {//???????????????????????????????????????????????????
            subjectsId = EnumConsts.SubjectIds.INCOME_CONSUME_OIL_SUB;
        } else {
            subjectsId = EnumConsts.SubjectIds.INCOME_CONSUME_OIL_SUB_1683;
        }
        BusiSubjectsRel consumeOilSub = iBusiSubjectsRelService.createBusiSubjectsRel(subjectsId, amount);
        busiOilList.add(consumeOilSub);
        List<BusiSubjectsRel> busiResultSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.INCOME_OIL_CODE, busiOilList);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE,EnumConsts.PayInter.INCOME_OIL_CODE, sysOperator.getUserInfoId(),
                sysOperator.getName(), accountOil, busiResultSubjectsRelList, soNbr,
                0L, "",null, sourceTenantId, null,
                "", null, vehicleAffiliation,loginInfo);


        //String orderId = AcUtil.createConsumeOilOrderId();
        //???????????????????????????
        float rise = new BigDecimal((float)amount/oilPrice).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        ConsumeOilFlow serviceOilFlow = iConsumeOilFlowService.createConsumeOilFlow(serviceUserId, sysOtherOperator.getBillId(),
                sysOtherOperator.getName(), OrderAccountConst.CONSUME_COST_TYPE.TYPE2,
                orderNum, amount, oilPrice, rise, sysOperator.getUserInfoId(), sysOperator.getBillId(), sysOperator.getName(),
                vehicleAffiliation, sourceTenantId, productId, -1,SysStaticDataEnum.USER_TYPE.SERVICE_USER,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        serviceOilFlow.setOilAffiliation(oilAffiliation);
        if (oilConsumer == OrderConsts.OIL_CONSUMER.SELF) {
            serviceOilFlow.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//?????????
            //?????????????????????
            TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(sourceTenantId, serviceUserId);
            int paymentDay = 0;
            int paymentMonth=0;
            if (tenantServiceRel != null) {
                Long useQuotaAmt = tenantServiceRel.getUseQuotaAmt() == null ? 0l : tenantServiceRel.getUseQuotaAmt();
                if (tenantServiceRel.getQuotaAmt() != null) {
                    Long noUseQuotaAmt = tenantServiceRel.getQuotaAmt() - useQuotaAmt;
                    if(amount > noUseQuotaAmt){
                        throw new BusinessException("??????????????????????????????????????????");
                    }
                }
//				boolean isLock = SysContexts.getLock("op-UseQuotaAmt-tenantServiceRel" + tenantServiceRel.getRelId(), 3, 5);
//				if (!isLock) {
//					throw new BusinessException("????????????????????????????????????!");
//				}
                creditLimit = OrderAccountConst.CONSUME_OIL_FLOW_EXT.CREDIT_LIMIT1;
                Long beforeAmount = useQuotaAmt;
                Long afterAmount = useQuotaAmt+amount;
                tenantServiceRel.setUseQuotaAmt(afterAmount);
                iTenantServiceRelService.saveOrUpdate(tenantServiceRel);
                iConsumeOilFlowService.saveOrUpdate(serviceOilFlow);
                TenantServiceRelDetails tenantServiceRelDetails = new TenantServiceRelDetails();
                tenantServiceRelDetails.setFlowId(serviceOilFlow.getId());
                tenantServiceRelDetails.setAmount(serviceOilFlow.getAmount());
                tenantServiceRelDetails.setOpType(1);//??????
                tenantServiceRelDetails.setTenantId(sourceTenantId);
                tenantServiceRelDetails.setBeforeAmount(beforeAmount);
                tenantServiceRelDetails.setAfterAmount(afterAmount);
                iTenantServiceRelDetailsService.save(tenantServiceRelDetails);
                if (tenantServiceRel.getBalanceType() != null && tenantServiceRel.getBalanceType() == 1) {//??????
                    paymentDay = (tenantServiceRel.getPaymentDays() == null ? 0 : tenantServiceRel.getPaymentDays());
                    LocalDateTime localDateTime = DateUtil.localDateTime(OrderDateUtil.addDate(new Date(), paymentDay));
                    serviceOilFlow.setGetDate(localDateTime);
                } else if (tenantServiceRel.getBalanceType() != null  && tenantServiceRel.getBalanceType() == 2) {//??????
                    paymentDay = (tenantServiceRel.getPaymentDays() == null ? 0 : tenantServiceRel.getPaymentDays());
                    paymentMonth=(tenantServiceRel.getPaymentMonth() == null ? 0 : tenantServiceRel.getPaymentMonth());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DAY_OF_MONTH,paymentDay);
                    calendar.add(Calendar.MONTH, paymentMonth);
                    serviceOilFlow.setGetDate(DateUtil.localDateTime(calendar.getTime()));
                } else {
                    serviceOilFlow.setGetDate(LocalDateTime.now());
                }
            } else {
                serviceOilFlow.setGetDate(LocalDateTime.now());
            }
            serviceOilFlow.setGetResult("?????????");
        } else {
            serviceOilFlow.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS1);//?????????
            serviceOilFlow.setGetResult("?????????");
            serviceOilFlow.setGetDate(LocalDateTime.now());
        }
        //??????????????????????????????????????????
        serviceOilFlow.setUndueAmount(amount);
        serviceOilFlow.setExpiredAmount(0L);
        if (serviceOilFlow.getState() == OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS1) {
            serviceOilFlow.setUndueAmount(0L);
            serviceOilFlow.setExpiredAmount(amount);
        }
        serviceOilFlow.setServiceCharge(0L);
        serviceOilFlow.setProductName(serviceProduct.getProductName());
        serviceOilFlow.setAddress(serviceProduct.getAddress());
        serviceOilFlow.setServiceCall(serviceProduct.getServiceCall());
        serviceOilFlow.setOilBalance(amount);
        serviceOilFlow.setSubjectsId(subjectsId);
        iConsumeOilFlowService.saveOrUpdate(serviceOilFlow);
        ConsumeOilFlowExt ext = iConsumeOilFlowExtService.createConsumeOilFlowExt(serviceOilFlow.getId(),
                otherFlowId, sourceRecordType,  oilConsumer, creditLimit, matchNoPayOil, matchNoRebateOil, matchNoCreditOil, tenantId);
        ext.setOilComUserId(collectionUserId);
        iConsumeOilFlowExtService.saveOrUpdate(ext);
        Map<String, Object> map = new HashMap<>();
        map.put("consumeOilFlow", serviceOilFlow);
        map.put("consumeOilFlowExt", ext);
        return map;
    }

    @Override
    public String consumeOilRebateToDriver(Long userId, Long rebate, Long tenantId, Long oilRechargeAccountDetailsId) throws Exception {
        return null;
    }


    @Override
    public boolean getOrderIsConsumeOil(Long orderId)  {
        if (orderId == null || orderId <= 1) {
            throw new BusinessException("???????????????????????????");
        }
        boolean flag = false;
        List<OrderOilSource> list = orderOilSourceService.getOrderOilSourceByOrderId(orderId);
        if (list != null && list.size() > 0) {
            List<Long> flowIds = new ArrayList<>();
            for (OrderOilSource oos : list) {
                flowIds.add(oos.getId());
            }
            List<ConsumeOilFlowExt> exts = oilFlowExtService.getConsumeOilFlowExtByFlowId(flowIds,
                    OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1);
            if (exts != null && exts.size() > 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void rechargeOil(Long userId, Long rechargeAmount, int isNeedBill, Integer oilAccountType, int userType, Integer oilBillType,String accessToken)  {


        LoginInfo user = loginUtils.get(accessToken);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????id");
        }
        if (rechargeAmount == null || rechargeAmount <= 0) {
            throw new BusinessException("?????????????????????");
        }
        Integer oilConsumer = null;
        if (oilAccountType == null || oilAccountType < OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
            throw new BusinessException("??????????????????????????????????????????");
        } else {
            if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
                oilConsumer = OrderConsts.OIL_CONSUMER.SELF;
            }
            if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                oilConsumer = OrderConsts.OIL_CONSUMER.SHARE;
            }
            if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                oilConsumer = OrderConsts.OIL_CONSUMER.SHARE;
            }
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(user.getTenantId());
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("????????????id" + user.getTenantId() + "???????????????????????????id!");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }

        String oilAffiliation = "";
        //???????????????
        if (isNeedBill == OrderAccountConst.ORDER_BILL_TYPE.notNeedBill) {
            oilAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
        } else if (isNeedBill == OrderAccountConst.ORDER_BILL_TYPE.carrierBill) { //??????
            oilAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
        }
        Date date = new Date();
        if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
            //todo
            Long rechargeOrderId = CommonUtil.zhCreateOrderId(user,8);
            this.saveRechargeOilSource(userId, 0L, String.valueOf(rechargeOrderId), String.valueOf(rechargeOrderId), rechargeAmount, rechargeAmount, 0, user.getTenantId(), date,  user.getId(), isNeedBill, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, date, oilAffiliation, OrderAccountConst.RECHARGE_ORDER_ACCOUNT_OIL.FLEET_OIL,oilConsumer,0,0,0,0,0,0,userType,OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1,OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1,accessToken);
        }
        List<RechargeOilSource> sourceList = null;
        if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
            Long tempVirtualOilFee = rechargeAmount;
            OrderAccountBalanceDto oilBlaceMap = iOrderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",user.getTenantId(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut = oilBlaceMap.getOa();
            if(null == orderAccountOut){
                throw new BusinessException("???????????????????????????");
            }
            //???????????????????????????
            Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
            if(null == canUseOilBalance){
                canUseOilBalance = 0l;
            }

            //???????????????????????????????????????
            TenantUserRel userRel = iTenantUserRelService.getAllTenantUserRelByUserId(userId, user.getTenantId());
            boolean isOwnCarUser = null != userRel && userRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR;

            if (canUseOilBalance.longValue() > 0 && isOwnCarUser) {
                Long rechargeOrderId = CommonUtil.zhCreateOrderId(user,8);
                if (rechargeAmount > canUseOilBalance.longValue()) {
                    //???????????????
                    sourceList = iOrderLimitService.matchOrderAccountOilToRechargeOil(canUseOilBalance, tenantUserId, String.valueOf(rechargeOrderId), user.getTenantId(), userId, EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL, EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT,userType,accessToken);
                    if (sourceList == null || sourceList.size() <= 0) {
                        throw new BusinessException("???????????????????????????");
                    } else {
                        long totalMatchAmount = 0;
                        for (RechargeOilSource ros : sourceList) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != canUseOilBalance) {
                            throw new BusinessException("??????????????????????????????????????????");
                        }
                    }
                    tempVirtualOilFee -= canUseOilBalance;
                } else {
                    //???????????????
                    sourceList = iOrderLimitService.matchOrderAccountOilToRechargeOil(rechargeAmount, tenantUserId, String.valueOf(rechargeOrderId), user.getTenantId(), userId, EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL, EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT,userType,accessToken);
                    if (sourceList == null || sourceList.size() <= 0) {
                        throw new BusinessException("???????????????????????????");
                    } else {
                        long totalMatchAmount = 0;
                        for (RechargeOilSource ros : sourceList) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != rechargeAmount) {
                            throw new BusinessException("??????????????????????????????????????????");
                        }
                    }
                    tempVirtualOilFee = 0l;
                }
            }
            //???????????????
            if (tempVirtualOilFee > 0) {
                Long rechargeOrderId = CommonUtil.zhCreateOrderId(user,8);
                Map<String, Object> resultMap = iOilRechargeAccountService.distributionOil(userId, tenantUserId, tempVirtualOilFee, String.valueOf(rechargeOrderId), EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT, user.getTenantId(), OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE2,EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3,user);
                Long noPayOil = (Long) resultMap.get("noPayOil");
                Long noRebateOil = (Long) resultMap.get("noRebateOil");
                Long noCreditOil = (Long) resultMap.get("noCreditOil");
                if (noPayOil == null) {
                    throw new BusinessException("????????????????????????");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("??????????????????");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("??????????????????");
                }
                this.saveRechargeOilSource(userId, 0L, String.valueOf(rechargeOrderId), String.valueOf(rechargeOrderId), noPayOil, noPayOil, 0, user.getTenantId(), date,  user.getId(), isNeedBill, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, date, oilAffiliation, OrderAccountConst.RECHARGE_ORDER_ACCOUNT_OIL.FLEET_OIL,oilConsumer,noRebateOil,noRebateOil,0,noCreditOil,noCreditOil,0,userType,OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2,OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1,accessToken);
            }
        }
        if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {

            Long rechargeOrderId = CommonUtil.zhCreateOrderId(user,8);
            Map<String, Object> resultMap = iOilRechargeAccountService.distributionOil(userId, tenantUserId, rechargeAmount, String.valueOf(rechargeOrderId), EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT, user.getTenantId(), OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE2,EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1,user);
            Long noPayOil = (Long) resultMap.get("noPayOil");
            Long noRebateOil = (Long) resultMap.get("noRebateOil");
            Long noCreditOil = (Long) resultMap.get("noCreditOil");
            if (noPayOil == null) {
                throw new BusinessException("????????????????????????");
            }
            if (noRebateOil == null) {
                throw new BusinessException("??????????????????");
            }
            if (noCreditOil == null) {
                throw new BusinessException("??????????????????");
            }
            this.saveRechargeOilSource(userId, 0L, String.valueOf(rechargeOrderId),
                    String.valueOf(rechargeOrderId), noPayOil, noPayOil, 0,
                    user.getTenantId(), date,  user.getId(), isNeedBill,
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,
                    date, oilAffiliation, OrderAccountConst.RECHARGE_ORDER_ACCOUNT_OIL.FLEET_OIL,oilConsumer,
                    noRebateOil,noRebateOil,0,noCreditOil,noCreditOil,0,userType,
                    OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3,OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1,
                    accessToken);
        }
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        OrderAccount account = iOrderAccountService.queryOrderAccount(userId, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, 0L, user.getTenantId(),OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,userType);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_FEE, rechargeAmount);
        busiList.add(amountFeeSubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL, busiList);
        // ????????????????????????????????????????????????
        long soNbr = CommonUtil.createSoNbr();
        Map<String,Object> param = new HashMap<String,Object>();

        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setRechargeOilSourceList(sourceList);
        param.put(OrderAccountConst.SOURCE_ORDER_OIL.SOURCE_LIST, sourceList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getName(), null, user.getTenantId(), null, "", orderResponseDto, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,user);
        // ?????????????????????????????????????????????


        ParametersNewDto parametersNewDto = busiToOrderUtils.setParametersNew(userId, sysOperator.getBillId(), EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL, 0L,rechargeAmount,OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,"");
        parametersNewDto.setTenantId(user.getTenantId());
        busiToOrderUtils.busiToOrderNew(parametersNewDto, busiSubjectsRelList,user);
    }

    @Override
    public OrderConsumeOilDto getOrderCousumeOil(List<Long> orderIds, Date beginDate, Date endDate, Long userId, Long tenantId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????id");
        }
        Long totalConsumeAmount = 0L;
        Double totalConsumeRise = (double) 0;
        List<ServiceMatchOrder>  ServiceMatchOrderList = iServiceMatchOrderService.getServiceMatchOrder(orderIds, beginDate, endDate,userId,tenantId);
        if (ServiceMatchOrderList != null && ServiceMatchOrderList.size() > 0) {
            List <Long> flowIds = new ArrayList<Long>();
            for (ServiceMatchOrder matchOrder : ServiceMatchOrderList) {
                Long flowId = matchOrder.getOtherFlowId();
                if (flowId != null && flowId > 0) {
                    flowIds.add(flowId);
                }
            }
            Map<String, Long> map = new HashMap<String, Long>();
            if (flowIds != null && flowIds.size() > 0) {
                List<ConsumeOilFlow> oilFlowList = iConsumeOilFlowService.getConsumeOilFlow(flowIds);
                if (oilFlowList != null && oilFlowList.size() > 0) {
                    for (ConsumeOilFlow cof : oilFlowList) {
                        map.put(String.valueOf(cof.getId()), cof.getOilPrice());
                    }
                }
            }
            for (ServiceMatchOrder matchOrder : ServiceMatchOrderList) {
                Long amount = (matchOrder.getAmount() == null ? 0L : matchOrder.getAmount());

                Long flowId = matchOrder.getOtherFlowId();
                if (flowId != null && flowId > 0) {
                    Long oilPrice = map.get(String.valueOf(flowId));
                    if (oilPrice != null && oilPrice > 0 ) {
                        BigDecimal b1 = new BigDecimal((double)amount / oilPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal b2 = new BigDecimal(totalConsumeRise).setScale(2, BigDecimal.ROUND_HALF_UP);
                        totalConsumeRise = b1.add(b2).doubleValue();
                    }
                }
                totalConsumeAmount += amount;
            }
        }
        OrderConsumeOilDto out = new OrderConsumeOilDto();
        out.setTotalConsumeAmount(totalConsumeAmount);
        out.setTotalConsumeRise(totalConsumeRise);
        return out;
    }

    @Override
    public List<OilServiceOutDto> queryOilRise(Long userId, List<OilServiceInDto> products, Long tenantId, Integer userType) {
//        IBankCallTF bankCallTF = BankCallUtil.initBank();
        if (userId == null || userId < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (products == null) {
            throw new BusinessException("???????????????????????????");
        }
        // ????????????id??????????????????
        UserDataInfo user = iUserDataInfoService.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("????????????id" + userId + "?????????????????????");
        }
        OilAccountOutListDto oilAccountOutList = this.getOilAccount(userId);
        if (oilAccountOutList == null) {
            throw new BusinessException("?????????????????????");
        }
        List<OilAccountOutDto> oaoList = oilAccountOutList.getOaoList();
        long shareOil = 0;//?????????
        //????????????
        Map<String, Long> selfOilMap = new HashMap<String, Long>();
        for (OilAccountOutDto tempOut : oaoList) {
            String key = String.valueOf(tempOut.getTenantId());
            if (tempOut.getOilConsumer() != null && tempOut.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {
                shareOil += tempOut.getAmount();
            } else if (tempOut.getOilConsumer() != null && tempOut.getOilConsumer() == OrderConsts.OIL_CONSUMER.SELF) {
                if (selfOilMap.get(key) != null) {
                    Long tempAmount = selfOilMap.get(key);
                    selfOilMap.put(key, tempOut.getAmount() + tempAmount);
                } else {
                    selfOilMap.put(key, (tempOut.getAmount() == null ? 0L : tempOut.getAmount()));
                }
            }
        }

        /*
         * 20190917??????
         *??????????????????1000??????????????????A????????????500????????????B????????????300??????????????????????????????C????????????200????????????
         *????????????????????????????????????B????????????1000??????????????????????????????????????????????????????????????????
         */
        List<OrderOilSource> orderList = oilAccountOutList.getOosList();
        List<RechargeOilSource> rechargeList = oilAccountOutList.getRosList();
        List<Object> tempList = new ArrayList<Object>();
        if (orderList != null) {
            tempList.addAll(orderList);

        }
        if (rechargeList != null) {
            tempList.addAll(rechargeList);
        }
        TenantAgentServiceRel tenantAgentServiceRel = null;
        if (tempList != null && tempList.size() > 0) {
            String orderId = "";
            LocalDateTime createDate = null;
            Long tempSourceTenantId = null;
            for (Object obj : tempList) {
                if (obj instanceof OrderOilSource) {
                    OrderOilSource oos = (OrderOilSource) obj;
                    if (oos.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE) {
                        if (StringUtils.isBlank(orderId)) {
                            orderId = oos.getSourceOrderId() + "";
                            createDate = oos.getCreateTime();
                            tempSourceTenantId = oos.getSourceTenantId();
                        } else {
                            if (oos.getCreateTime().isBefore(createDate)) {//before Date ???LocalDateTime
                                orderId = oos.getSourceOrderId() + "";
                                createDate = oos.getCreateTime();
                                tempSourceTenantId = oos.getSourceTenantId();
                            }
                        }
                    }
                } else if (obj instanceof RechargeOilSource) {
                    RechargeOilSource ros = (RechargeOilSource) obj;
                    rechargeList.add(ros);
                    if (ros.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE) {
                        if (StringUtils.isBlank(orderId)) {
                            orderId = ros.getSourceOrderId() + "";
                            createDate = ros.getCreateTime();
                            tempSourceTenantId = ros.getSourceTenantId();
                        } else {
                            orderId = ros.getSourceOrderId() + "";
                            createDate = ros.getCreateTime();
                            tempSourceTenantId = ros.getSourceTenantId();
                        }
                    }
                }
            }


            if (tempSourceTenantId != null && tempSourceTenantId > 0) {
//                Object[] objList = serviceInfoSV.getAgentService(tempSourceTenantId,ServiceConsts.AGENT_SERVICE_TYPE.OIL);
                AgentServiceDto agentService = iServiceInfoService.getAgentService(tempSourceTenantId, ServiceConsts.AGENT_SERVICE_TYPE.OIL);
                if (agentService == null ) {
                    throw new BusinessException("????????????id???" + tempSourceTenantId + " ???????????????????????????????????????????????????");
                }
                //AgentService  objList[1] ?????????id????????? (TenantAgentServiceRel) objList[0] ??????id????????????????????????AgentService  objList[1] ??????id
                tenantAgentServiceRel = agentService.getTenantAgentServiceRel();
                if(StringUtils.isNotBlank(orderId)) {
                    Long adminUser = iSysTenantDefService.getTenantAdminUser(tempSourceTenantId);
                    List<OilRechargeAccountDetailsFlow> flows = oilRechargeAccountDetailsFlowService.getOrderDetailsFlows(adminUser, orderId, SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE3, 1);
                    if(flows!=null&&flows.size()>0) {
                        for(OilRechargeAccountDetailsFlow flow:flows) {
                            long realId = flow.getRelId();
//                            OilRechargeAccountDetails oilRechargeAccountDetails = oilRechargeAccountFlowSV.getObjectById(OilRechargeAccountDetails.class, realId);
                            LambdaQueryWrapper<OilRechargeAccountDetails> wrapper = new LambdaQueryWrapper<>();
                            wrapper.eq(OilRechargeAccountDetails::getId,realId);
                            OilRechargeAccountDetails oilRechargeAccountDetails =oilRechargeAccountDetailsMapper.selectById(wrapper);
                            Long sourceUserId = oilRechargeAccountDetails.getSourceUserId();
                            if(sourceUserId!=null&&sourceUserId>0L) {
                                AgentServiceDto agentServiceList = iServiceInfoService.getAgentServiceByServiceId(sourceUserId, ServiceConsts.AGENT_SERVICE_TYPE.OIL);
                                if(agentServiceList!=null) {
                                    TenantAgentServiceRel rel = agentServiceList.getTenantAgentServiceRel();
                                    if(rel!=null&&tenantAgentServiceRel.getAgentId().longValue()!=rel.getAgentId().longValue()) {
                                        tenantAgentServiceRel =rel;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (tenantAgentServiceRel == null ) {
                    throw new BusinessException("????????????id???" + tempSourceTenantId + " ???????????????????????????????????????????????????");
                }
            }
        }
        List<OilServiceOutDto> out = new ArrayList<OilServiceOutDto>();
        //BankBalanceInfo  pingAnInfo = bankCallTF.getBankBalanceToUserIdNo(userId, BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT,"");
        BankBalanceInfo  pingAnInfo = new BankBalanceInfo();
        pingAnInfo.setBalance(0.00);
        for (OilServiceInDto oilServiceIn : products) {
            //??????id
            Long productId = oilServiceIn.getProductId();
            //???????????????id
            Long serviceUserId = oilServiceIn.getServiceId();
            //??????
            Long oilPrice = (oilServiceIn.getOilPrice() == null ? 0L : oilServiceIn.getOilPrice());

            //???????????????id??????????????????????????????
            ServiceInfo  serviceInfo = iServiceInfoService.getServiceUserId(serviceUserId);
            if (serviceInfo == null) {
                throw new BusinessException("????????????????????????");
            }
            //????????????id???????????????????????????
            List<TenantProductRel> tenantProductRelList = iTenantProductRelService.getTenantProductRelList(productId);
            if (tenantProductRelList == null) {
                throw new BusinessException("????????????id???" + productId + ",??????????????????????????????");
            }
            //????????????????????????????????????????????????????????????????????????????????????(????????????????????????????????????????????????)
            boolean isSharePorduct = false;
            //??????????????????????????????????????????
            boolean isBelongToTenant = false;
            List<Long> belongToTenantList = new ArrayList<Long>();
            for (TenantProductRel tpr : tenantProductRelList) {
                if (tpr.getState() == null || tpr.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2 || tpr.getAuthState() == null || tpr.getAuthState() != 2) {
                    continue;
                }
                //??????tenantId ???1????????????????????????
                if (tpr.getTenantId() == null || tpr.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
                    isSharePorduct = true;
                } else {
                    isBelongToTenant = true;
                    belongToTenantList.add(tpr.getTenantId());
                }
            }
            if (!isSharePorduct && !isBelongToTenant) {
                throw new BusinessException("????????????id???" + productId + ",??????????????????????????????????????????????????????????????????");
            }
            //?????????
            long oilBalance = 0;
            //?????????????????????????????????
            Double pinganTotalTranOutAmount = pingAnInfo.getBalance();
            long totalBalance = new Double(pinganTotalTranOutAmount * 100).longValue();
            //??????????????????
            if (isSharePorduct) {
                oilBalance += shareOil;
                //?????????,?????????????????????????????????????????????????????????????????????
                totalBalance = 0;
            } else {//?????? ??????????????????
                for (Long sourceTenantId : belongToTenantList) {
                    String key = String.valueOf(sourceTenantId);
                    Long selfOil = (selfOilMap.get(key) == null ? 0L : selfOilMap.get(key));
                    TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(sourceTenantId, serviceUserId);
                    if (tenantServiceRel == null) {
                        throw new BusinessException("????????????id???" + sourceTenantId + ",?????????id???" + serviceUserId + " ???????????????????????????????????????");
                    }
                    if (tenantServiceRel.getQuotaAmt() != null) {
                        //???????????? ??? ?????????????????????
                        long noUseQuotaAmt = tenantServiceRel.getQuotaAmt() - (tenantServiceRel.getUseQuotaAmt() == null ? 0L : tenantServiceRel.getUseQuotaAmt());
                        if (noUseQuotaAmt < 0) {
                            noUseQuotaAmt = 0;
                        }
                        if (noUseQuotaAmt > selfOil) {
                            oilBalance += selfOil;
                        } else {
                            oilBalance += noUseQuotaAmt;
                        }
                    } else {
                        oilBalance += selfOil;
                    }
                }
            }
            OilServiceOutDto oso = new OilServiceOutDto();
            float oilRise = 0.0f;
            if (oilPrice != null && oilPrice > 0) {
                oilRise = new BigDecimal((float)(totalBalance + oilBalance )/oilPrice).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            }
            oso.setProductId(oilServiceIn.getProductId());
            oso.setOilPrice(oilPrice);
            oso.setOilRise(oilRise);
            oso.setConsumeOilBalance(totalBalance + oilBalance );
            oso.setOilBalance(oilBalance);
            oso.setPinganBalance(totalBalance);
            if (tenantAgentServiceRel != null) {
                oso.setAgentServiceId(tenantAgentServiceRel.getAgentId());
            }
            out.add(oso);
        }
        return out;
    }
}
