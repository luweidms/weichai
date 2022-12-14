package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IDriverInfoExtService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.facilitator.DriverInfoExt;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.oil.ICarLastOilService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountBankUserTypeRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IAdditionalFeeService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilCardLogService;
import com.youming.youche.order.api.order.IOilEntityService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderSyncTypeInfoService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.api.order.other.IOrderSync56KService;
import com.youming.youche.order.api.order.other.IPayCenterAccountInfoService;
import com.youming.youche.order.api.order.other.IUpdateOrderService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.oil.CarLastOil;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.AdditionalFee;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OilCardLog;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutOrder;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.DriverOrderOilOutDto;
import com.youming.youche.order.dto.FeesDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import com.youming.youche.order.dto.OrderDriverSubsidyDto;
import com.youming.youche.order.dto.OrderInfoInDto;
import com.youming.youche.order.dto.OrderLimitFeeOutDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.UpdateTheOwnCarOrderInDto;
import com.youming.youche.order.provider.mapper.OrderDriverSubsidyMapper;
import com.youming.youche.order.provider.mapper.OrderFeeMapper;
import com.youming.youche.order.provider.mapper.order.OrderFeeHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoExtMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerHMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.vo.AdvanceChargeVo;
import com.youming.youche.order.vo.CopilotMapVo;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;
import com.youming.youche.order.vo.PayArriveChargeVo;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
* <p>
    * ??????????????? ???????????????
    * </p>
* @author liangyan
* @since 2022-03-09
*/
@DubboService(version = "1.0.0")
@Service
public class OrderFeeServiceImpl extends BaseServiceImpl<OrderFeeMapper, OrderFee> implements IOrderFeeService {
    private static final Log log = LogFactory.getLog(OrderFeeServiceImpl.class);

//    @DubboReference(version = "1.0.0")
//    IUserService iUserService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private OrderDateUtil orderDateUtil;
    @Resource
    IOrderFeeService iOrderFeeService;
    @Resource
    private OrderFeeMapper orderFeeMapper;
    @Autowired
    private IOrderFeeExtService orderFeeExtService;
    @Resource
    private IOrderFeeExtHService orderFeeExtHService;
    @Resource
    private IOrderInfoExtService orderInfoExtService;
    @Resource
    private IOilCardManagementService oilCardManagementService;
    @Resource
    private IServiceInfoService serviceInfoService;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private IOrderReceiptService iOrderReceiptService;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;

    @Resource
    private IBillPlatformService billPlatformService;

    @Resource
    private IOrderSync56KService orderSync56KService;
    @Resource
    private IUpdateOrderService updateOrderService;

    @Resource
    private IOrderProblemInfoService orderProblemInfoService;

    @Resource
    private IOrderAgingInfoService orderAgingInfoService;
    @DubboReference(version = "1.0.0")
    private ITenantUserSalaryRelService tenantUserSalaryRelService;
    @Resource
    private IOrderSchedulerService orderSchedulerService;

    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    private IAuditService iAuditService;
    @Resource
    private OrderInfoExtMapper orderInfoExtMapper;
    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    private IDriverInfoExtService  iDriverInfoExtService;
    @Resource
    private OrderInfoHMapper orderInfoHMapper;
    @Resource
    private OrderSchedulerHMapper orderSchedulerHMapper;
    @Resource
    private  OrderFeeHMapper orderFeeHMapper;

    @Resource
    IOrderSyncTypeInfoService iOrderSyncTypeInfoService;


    @Resource
    IOrderInfoHService iOrderInfoHService;

    @Resource
    IOrderGoodsService iOrderGoodsService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    private IOpAccountService iOpAccountService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    IAccountBankUserTypeRelService iAccountBankUserTypeRelService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    IOrderOilCardInfoService iOrderOilCardInfoService;

    @Resource
    com.youming.youche.order.api.service.IServiceInfoService iServiceInfoService;

    @Resource
    IOilEntityService iOilEntityService;

    @Resource
    IOilCardLogService iOilCardLogService;

    @Resource
    ICarLastOilService iCarLastOilService;

    @Resource
    IBillSettingService iBillSettingService;

    @Resource
    IOrderOpRecordService iOrderOpRecordService;

    @Resource
    IOperationOilService iOperationOilService;

    @Resource
    OrderDriverSubsidyMapper orderDriverSubsidyMapper;

    @Resource
    IOrderDriverSubsidyService iOrderDriverSubsidyService;

    @Resource
    IPayoutIntfService iPayoutIntfService;

    @Resource
    IOrderAgingAppealInfoService iOrderAgingAppealInfoService;

    @DubboReference(version = "1.0.0")
    ISysUserService iSysOperatorService;

    @Resource
    IOrderLimitService iOrderLimitService;

    @Resource
    IOrderAccountService iOrderAccountService;

    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;

    @Resource
    IOrderAgingInfoService iOrderAgingInfoService;

    @Resource
    IAccountDetailsService iAccountDetailsService;

    @Resource
    IBillAgreementService iBillAgreementService;

    @Resource
    IPayFeeLimitService iPayFeeLimitService;

    @Resource
    IPayoutOrderService iPayoutOrderService;

    @Resource
    IOrderOilSourceService iOrderOilSourceService;

    @Autowired
    private IOrderTransferInfoService orderTransferInfoService;

    @Resource
    IOilRechargeAccountService iOilRechargeAccountService;

    @Resource
    IAdditionalFeeService iAdditionalFeeService;

    @Resource
    IAccountBankRelService iAccountBankRelService;
    @Lazy
    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderFeeHService orderFeeHService;
    @Resource
    private IOrderInfoExtHService orderInfoExtHService;
    @Resource
    private IOrderGoodsHService orderGoodsHService;
    @Resource
    private IPayCenterAccountInfoService payCenterAccountInfoService;
    @Resource
    private ReadisUtil orderRedisUtil;
    @Resource
    private IOrderDriverSwitchInfoService orderDriverSwitchInfoService;
    @Resource
    private IOrderDriverSubsidyService orderDriverSubsidyService;
    @Resource
    private IPayoutIntfService payoutIntfService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Override
    public OrderFee getOrderFeeByOrderId(Long orderId) {

        QueryWrapper<OrderFee> orderFeeQueryWrapper = new QueryWrapper<>();
        orderFeeQueryWrapper.eq("order_id",orderId);
        List<OrderFee> orderFees = orderFeeMapper.selectList(orderFeeQueryWrapper);
        if(orderFees != null && orderFees.size() > 0){
            return orderFees.get(0);
        }
        return null;
    }

    @Override
    public List<Long> getOrderByPaymentWay(Long driverId, Integer paymentWay) {
        return orderFeeMapper.queryOrderByDriver(driverId, paymentWay);
    }

    @Override
    public OrderFee selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderFee> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderFee::getOrderId,orderId);
        return getOne(wrapper,false);
    }

    @Override
    public Map verifyOilCardNum(Long orderId, List<String> oilCardNums, boolean isPay, Long oilFee,String accessToken ) {

        if (oilCardNums == null || oilCardNums.size() <= 0) {
            throw new BusinessException("??????????????????");
        }
        long count = oilCardNums.stream().distinct().count();
        boolean isRepeat = count < oilCardNums.size();
        if (isRepeat) {
            throw new BusinessException("????????????????????????!");
        }
        Map map = new ConcurrentHashMap();
        boolean isEnough = false;

        // ????????????????????????
        OrderFee orderFee = this.getOrderFee(orderId);

        // ????????????????????????
        OrderInfoExt orderInfoExt = orderInfoExtService.selectByOrderId(orderId);
        if (orderFee == null) {
            throw new BusinessException("???????????????["+orderId+"]??????");
        }
        List<OilCardManagement> listOut = new ArrayList<OilCardManagement>();
        if (oilFee == null || oilFee <= 0) {
            oilFee = orderFee.getPreOilFee();
        }
        for (String oilCardNum : oilCardNums) {
            Long oilCardFee = 0L;
            Boolean isEquivalenceCard = false;
            if (!isPay) {//??????????????? ????????????????????????????????????
                //???????????????????????????????????? ??????????????????????????????
                // ????????????????????????
                if(orderFee.getPrePayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()) {
                    if (oilCardNum.equals(orderFee.getPrePayEquivalenceCardNumber())) {
                        oilCardFee += orderFee.getPrePayEquivalenceCardAmount() == null ? 0 : orderFee.getPrePayEquivalenceCardAmount();
                        isEquivalenceCard = true;
                    }
                }

                //???????????????????????????????????? ??????????????????????????????
                if(orderFee.getAfterPayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()) {
                    if (oilCardNum.equals(orderFee.getAfterPayEquivalenceCardNumber())) {
                        oilCardFee += orderFee.getAfterPayEquivalenceCardAmount() == null ? 0 : orderFee.getAfterPayEquivalenceCardAmount();
                        isEquivalenceCard = true;
                    }
                }
            }

            // ???????????????
            List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, orderFee.getTenantId());
            OilCardManagement oilCard = new OilCardManagement();
            if (list == null || list.size() <= 0) {
                if (isEquivalenceCard) {//??????????????????
                    //????????????????????????
                    oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                    oilCard.setOilCarNum(oilCardNum);
                }else{
                    if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
                        oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                        oilCard.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", oilCard.getCardType() + ""));
                        oilCard.setOilCarNum(oilCardNum);
                    }else{
                        throw new BusinessException("????????????["+oilCardNum+"]?????????!");
                    }
                }
            }else{
                oilCard = list.get(0);
//                session.evict(oilCard);
            }
            //?????????????????????????????????
            int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
            oilCard.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", cardType + ""));
            oilCardFee += oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;
            //??????????????????????????????
            oilCard.setCardBalance(oilCardFee);
            oilCard.setIsNeedWarn(false);
            //???????????????????????????????????????
            if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {

                ServiceInfoVo serviceInfo = serviceInfoService.seeServiceInfoMotorcade(oilCard.getUserId(), accessToken);
                if (serviceInfo != null) {
                    if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().intValue() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD) {
                        oilCard.setIsNeedWarn(true);
                    }
                }
                isEnough = true;
                listOut.add(oilCard);
                //break;
            }else{
                if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant().intValue() != OrderConsts.IS_TEMP_TENANT.YES) {
                    if (oilCardFee == 0L) {
                        throw new BusinessException("??????["+oilCardNum+"]???????????????0?????????!");
                    }
                }
                //???????????????????????? ??????????????????????????????
                if (oilFee <= oilCardFee) {
                    isEnough = true;
                    listOut.add(oilCard);
                }else{//???????????????????????? ????????????????????? ????????????
                    oilFee -= oilCardFee;
                    listOut.add(oilCard);
                }
            }
        }
        if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
            isEnough = true;
        }
        map.put("listOut", listOut);
        map.put("isEnough", isEnough);
        return map;
    }

    @Override
    public void verifyOilCardNum(OrderInfo orderInfo, List<String> oilCardNums, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (orderInfo == null||orderInfo.getOrderId()==null||orderInfo.getOrderId()<=0) {
            throw new BusinessException("???????????????["+orderInfo.getOrderId()+"]??????");
        }

        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());
        OrderFee orderFee = this.getOrderFee(orderInfo.getOrderId());
        if((orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE)
                && orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0
        ) {
            OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderInfo.getOrderId());
            if(oilCardNums==null) {
                oilCardNums=new ArrayList<String>();
            }
            this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler, orderInfoExt.getOilAffiliation(),false, loginInfo);

            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(),  SysOperLogConst.OperType.Update, "???????????????????????????????????????"+(oilCardNums == null || oilCardNums.size() == 0 ? "???" : oilCardNums.toString()));
        }
    }

    /**
     * ??????????????????
     * @param orderId ?????????
     * @param verifyDesc ????????????
     * @param reciveType ????????????
     * @return
     *
     */
    @Override
    public String reciveVerifyPayPass(Long orderId, String verifyDesc, String receiptsStr, Integer reciveType,String accessToken) {
        //TODO ???????????????????????? ???
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "reciveVerifyPayPass" + orderId, 3, 5);
        boolean isLock =true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
//        IAuditTF auditTF = (IAuditTF) SysContexts.getBean("auditTF");
        // TODO ?????????

        boolean sure = false;
//        OrderInfo orderInfo = orderInfoSV.getOrder(orderId);;
        OrderInfo orderInfo =orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("?????????????????????["+orderId+"]?????????????????????????????????");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("???????????????????????????,???????????????");
        }
        try {
            //TODO  ??????
          AuditCallbackDto auditCallbackDto =  auditSettingService.sure(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS,accessToken);
            if (auditCallbackDto != null && auditCallbackDto.getIsNext()) {
                sure = auditCallbackDto.getIsNext();
            }
//            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
//                sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
//            }
        } catch (BusinessException e) {
            if ("???????????????????????????".equals(e.getMessage())) {

            }else{
                throw new BusinessException(e.getMessage());
            }
        }
        if (!sure) {
//            IOrderReciveTF orderReciveTF =(IOrderReciveTF) SysContexts.getBean("orderReciveTF");
//            orderReciveTF.verifyRece(orderId, false, false, verifyDesc, receiptsStr, reciveType,false);
            iOrderReceiptService.verifyRece(String.valueOf(orderId), false, false,
                    verifyDesc, receiptsStr, reciveType,accessToken,false);
        }
        return null;
    }

    @Override
    public void verifyPayPass(Long orderId, String auditCode, String verifyDesc, List<String> oilCardNums, String receiptsStr, Integer reciveType, String accessToken) {
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "verifyPayPass" + orderId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }

        boolean isNotAudit = false;
        boolean sure = false;
        AuditCallbackDto auditCallbackDto = null;
        // ????????????
        try {
            auditCallbackDto = auditSettingService.sure(auditCode, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
            if (auditCallbackDto != null && auditCallbackDto.getIsNext()) {
                sure = auditCallbackDto.getIsNext();
            }
//            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
//             //   sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
//            }
        } catch (BusinessException e) {
            if ("???????????????????????????".equals(e.getMessage())) {
                isNotAudit = true;
            }else{
                throw new BusinessException(e.getMessage());
            }
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("?????????????????????["+orderId+"]?????????????????????????????????");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("???????????????????????????,???????????????");
        }
        if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {
            if (!sure) {
                this.payProFee(orderId, oilCardNums, accessToken);
            }else {
                this.verifyOilCardNum(orderInfo, oilCardNums, accessToken);
            }
        }else if(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)){
            if (isNotAudit) {
                this.payArriveFee(orderId, accessToken);
            }
        }else if(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)){
            if (!sure) {
                // ????????????
                iOrderReceiptService.verifyRece(String.valueOf(orderId), false, false, verifyDesc, receiptsStr, reciveType, accessToken, false);
            }
        }else{
            throw new BusinessException("???????????????????????????");
        }
    }

    /**
     * ?????????????????????
     * @param orderId ?????????
     * @param verifyDesc ????????????
     * @param load ??????????????????
     * @param receipt ??????????????????
     * @return
     * @throws Exception
     */
    @Override
    public String reciveVerifyPayFail(Long orderId, String verifyDesc, boolean load, boolean receipt,String accessToken) {
        // TODO ???????????? ???????????????
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "reciveVerifyPayFail" + orderId, 3, 5);
        boolean isLock= true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
//        IAuditTF auditTF = (IAuditTF) SysContexts.getBean("auditTF");
//        IOrderReciveTF orderReciveTF =(IOrderReciveTF) SysContexts.getBean("orderReciveTF");
        boolean isNotAudit = false;
        try {
            //  TODO ??????
            auditSettingService.sure(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId, verifyDesc, AuditConsts.RESULT.FAIL,accessToken);
//            auditTF.sure(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId, verifyDesc, AuditConsts.RESULT.FAIL);
        } catch (BusinessException e) {
            if ("???????????????????????????".equals(e.getMessage())) {
                isNotAudit = true;
            }else{
                throw new BusinessException(e.getMessage());
            }
        }
        if (!load && !receipt) {
            throw new BusinessException("???????????????????????????");
        }
//        orderReciveTF.verifyRece(orderId, load, receipt, null, null, OrderConsts.RECIVE_TYPE.SINGLE);
        iOrderReceiptService.verifyRece(String.valueOf(orderId), load, receipt, verifyDesc, null, OrderConsts.RECIVE_TYPE.SINGLE,accessToken,null);
        if (isNotAudit) {
//            IAuditOutTF outTF = (IAuditOutTF) SysContexts.getBean("auditOutTF");
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderId);
            params.put("auditCode", AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE);
            //TODO ????????????
            iAuditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId,
                    SysOperLogConst.BusiCode.OrderInfo, params,accessToken);
//            outTF.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId,
//                    SysOperLogConst.BusiCode.OrderInfo, params);
        }
        return null;
    }




    /**
     * ?????????
     * ????????????????????????
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean isNeedUploadAgreementByLuge(Long orderId) {
        boolean isNeedUploadAgreement = false;
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        if (orderInfo != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            QueryWrapper<OrderScheduler> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id",orderId);
            OrderScheduler orderScheduler = orderSchedulerMapper.selectOne(queryWrapper);
            QueryWrapper<OrderFee> feeQueryWrapper = new QueryWrapper<>();
            feeQueryWrapper.eq("order_id",orderId);
            OrderFee orderFee =orderFeeMapper.selectOne(feeQueryWrapper);
            if (StringUtils.isNotBlank(orderFee.getVehicleAffiliation())
                    && orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId() > 0) {
                // TODO ????????????
//                String billForm = BillPlatformCacheUtil.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation())); // ????????????
                String billForm ="";
                if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//??????
                    UserDataInfo userDataInfo = userDataInfoService.getById(orderScheduler.getCarDriverId());
//                    DriverInfoExt driverInfoExt = driverInfoExtTF.getDriverInfoExtByUserId(orderScheduler.getCarDriverId());
                    DriverInfoExt driverInfoExt =iDriverInfoExtService.createDriverInfoExt(orderScheduler.getCarDriverId());
                    if (userDataInfo != null && (userDataInfo.getLugeAgreement() == null
                            || userDataInfo.getLugeAgreement() <= 0)
                            || (driverInfoExt == null || driverInfoExt.getLuGeAuthState() == null
                            || !driverInfoExt.getLuGeAuthState())) {
                        isNeedUploadAgreement = true;
                    }
                }
            }
        }else{
            QueryWrapper<OrderInfoH> infoHQueryWrapper  = new QueryWrapper<>();
            infoHQueryWrapper.eq("order_id",orderId);
            OrderInfoH orderInfoH =orderInfoHMapper.selectOne(infoHQueryWrapper);
            if (orderInfoH != null && orderInfoH.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                QueryWrapper<OrderSchedulerH> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_id",orderId);
                OrderSchedulerH orderSchedulerH =orderSchedulerHMapper.selectOne(queryWrapper);
                QueryWrapper<OrderFeeH> feeHQueryWrapper = new QueryWrapper<>();
                feeHQueryWrapper.eq("order_id",orderId);
                OrderFeeH  orderFeeH =orderFeeHMapper.selectOne(feeHQueryWrapper);
                if (StringUtils.isNotBlank(orderFeeH.getVehicleAffiliation())
                        && orderSchedulerH.getCarDriverId() != null && orderSchedulerH.getCarDriverId() > 0) {
                    // TODO ????????????
//                    String billForm = BillPlatformCacheUtil.getPrefixByUserId(Long.parseLong(orderFeeH.getVehicleAffiliation()));// ????????????
                    String billForm ="";
                    if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//??????
                        UserDataInfo userDataInfo = userDataInfoService.getById(orderSchedulerH.getCarDriverId());
                        DriverInfoExt driverInfoExt =iDriverInfoExtService.createDriverInfoExt(orderSchedulerH.getCarDriverId());
                        if (userDataInfo != null && (userDataInfo.getLugeAgreement() == null
                                || userDataInfo.getLugeAgreement() <= 0)
                                || (driverInfoExt == null || driverInfoExt.getLuGeAuthState() == null
                                || !driverInfoExt.getLuGeAuthState())) {
                            isNeedUploadAgreement = true;
                        }
                    }
                }
            }
        }
        return isNeedUploadAgreement;
    }

    @Override
    public CopilotMapVo culateSubsidy(Long userId, Long tenantId, LocalDateTime dependTime, Float arriveTime, Long orderId, boolean isCopilot, Integer transitLineSize) {


        CopilotMapVo copilotMapVo = new CopilotMapVo();

        Long copilotDaySalary = 0L;// ?????????
        Long monthSubSalary = 0L;
        Long subsidyDay = 0L;
        String dateString = "";
        UserDataInfo userDataInfo = userDataInfoService.getById(userId);
        if (userDataInfo == null) {//?????????????????????????????????
            copilotMapVo.setCopilotSalary(0L);
            copilotMapVo.setSubsidyCopilotDay(subsidyDay);
            copilotMapVo.setCopilotSubsidyTime(dateString);
            copilotMapVo.setCopilotDaySalary(copilotDaySalary);
            copilotMapVo.setMonthSubSalary(monthSubSalary);
            copilotMapVo.setCopilotSalaryPattern(null);
            return copilotMapVo;
        }
        TenantUserSalaryRel salaryRel = tenantUserSalaryRelService.getTenantUserRalaryRelByUserId(userId, tenantId);

        if (salaryRel != null) {
//            throw new BusinessException("?????????"+(isCopilot ? "?????????":"????????????????????????")+"???????????????");
            copilotDaySalary = salaryRel.getSubsidy();// ?????????
            monthSubSalary = salaryRel.getSalary();
        }

        subsidyDay = 0L;
        dateString = "";
        Double estStartTime = Double.parseDouble(readisUtil.getSysCfg("ESTIMATE_START_TIME", "0").getCfgValue());
        Map<String, Object> orderIdMap = orderSchedulerService.getPreOrderIdByUserid(userId, dependTime, tenantId,orderId);
        //??????????????????
        arriveTime = arriveTime +((transitLineSize < 0 ? 0 : transitLineSize)+1 * estStartTime.floatValue());
        LocalDateTime preArriveDate = null;
        StringBuffer print = new StringBuffer("???????????????????????????");
        if (orderIdMap != null) {
            if(OrderConsts.TableType.ORI==(Integer) orderIdMap.get("type")){
                //??????
                OrderScheduler scheduler=  orderSchedulerService.getOrderScheduler(DataFormat.getLongKey(orderIdMap, "orderId"));
                preArriveDate = orderInfoExtService.getOrderArriveDate(scheduler.getOrderId(), scheduler.getDependTime()
                        , scheduler.getCarStartDate(), scheduler.getArriveTime(), false);

            }else if(OrderConsts.TableType.HIS==(Integer) orderIdMap.get("type")){
                //?????????
                OrderSchedulerH schedulerH=  orderSchedulerHService.getOrderSchedulerH(DataFormat.getLongKey(orderIdMap, "orderId"));
                preArriveDate = orderInfoExtService.getOrderArriveDate(schedulerH.getOrderId(), schedulerH.getDependTime()
                        , schedulerH.getCarStartDate(), schedulerH.getArriveTime(), true);
            }
            print.append("??????????????????:[").append(orderIdMap.get("orderId")).append("] ");

            print.append(" ?????????????????????:[").append(orderDateUtil.stringByLocalDateTime(preArriveDate))
                    .append("]");
        }
        print.append("??????????????????:[").append(orderDateUtil.stringByLocalDateTime(dependTime));
        print.append("??????????????????:[").append(arriveTime).append("]??????");

        Map<String, Object> subsidyDayMap = culateSubsidy(dependTime, arriveTime, preArriveDate);
        subsidyDay = Long.valueOf(subsidyDayMap.get("subsidyDay").toString());
        dateString = subsidyDayMap.get("dateString").toString();

        print.append("???????????????[").append(subsidyDay).append("]");
        print.append("???????????????[").append(dateString).append("]");
        log.info(print);

        if (copilotDaySalary != null) {
            Long subsidyFee = subsidyDay.longValue() * copilotDaySalary.longValue();
            copilotMapVo.setCopilotSalary(subsidyFee);
        } else {
            copilotMapVo.setCopilotSalary(0L);
        }
        copilotMapVo.setSubsidyCopilotDay(subsidyDay);
        copilotMapVo.setCopilotSubsidyTime(dateString);
        copilotMapVo.setCopilotDaySalary(copilotDaySalary);
        copilotMapVo.setMonthSubSalary(monthSubSalary);
        copilotMapVo.setCopilotSalaryPattern(salaryRel !=null ? salaryRel.getSalaryPattern():null );

        return copilotMapVo;
    }

    public Map<String, Object> culateSubsidy(LocalDateTime dependTime, Float arriveTime,
                                             LocalDateTime preArriveDate) {

        int subsidyDay = 0;
//        if (preArriveDate == null) {
            // ???????????????
            LocalDateTime arriveDate = orderDateUtil.addHourAndMins(dependTime, arriveTime);
            subsidyDay = OrderDateUtil.getDifferDay(getDateByLocalDateTime(dependTime),
                    getDateByLocalDateTime(arriveDate)) + 1;
//        } else {
//            // ????????????
//            // ??????????????????????????????
//            // ???????????????????????????
//            LocalDateTime arriveDate = orderDateUtil.addHourAndMins(dependTime, arriveTime);
//            // ??????????????????-????????????????????????+1????????????????????? ?????????0
//            if (arriveDate.isBefore(preArriveDate)) {
//                subsidyDay = 0;
//            } else {
//                if (arriveDate.getYear() != preArriveDate.getYear()
//                        || arriveDate.getMonth() != preArriveDate.getMonth()) {
//                    // ???????????????????????????????????????????????????????????????????????????1?????????
//                    Date dateByLocalDateTime = getDateByLocalDateTime(arriveDate);
//                    Date date = DateUtil.parseDate(
//                            DateUtil.formatDate(dateByLocalDateTime,
//                                    DateUtil.YEAR_MONTH_FORMAT) + "-01", DateUtil.DATE_FORMAT);
//                    preArriveDate =getLocalDateTimeByDate(date);
//                }
//                subsidyDay = OrderDateUtil.getDifferDay(getDateByLocalDateTime(preArriveDate),
//                        getDateByLocalDateTime(arriveDate))+ 1;
//            }
//        }
        String dateString = "";
//        if (preArriveDate == null) {
//            preArriveDate = dependTime;
            for (int j = 0; j < subsidyDay; j++) {
                dateString += " " + DateUtil.formatDate(
                        DateUtil.addDate(getDateByLocalDateTime(dependTime), j), "MM-dd");
            }
//        }
//        else {
//            for (int j = 0; j < subsidyDay; j++) {
//                dateString += " " + DateUtil.formatDate(
//                        DateUtil.addDate(getDateByLocalDateTime(preArriveDate), j), "MM-dd");
//            }
//        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("subsidyDay", subsidyDay);
        retMap.put("dateString", dateString);
        return retMap;
    }

    @Override
    public void syncOrderTrackTo56K(OrderInfo orderInfo, OrderFee orderFee,String accessToken) {
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (StringUtils.isNotBlank(orderFee.getVehicleAffiliation())) {
                String billForm56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"").getCodeName();
                String billForm = billPlatformService.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation()));
                if (billForm != null && billForm56K.equals(billForm)) {//56K????????????
                    OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderInfo.getOrderId());
                    orderFeeExt.setIsNeedTrackSync(1);//????????????
                    orderFeeExtService.saveOrUpdate(orderFeeExt);
                }else if(billForm != null &&  SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){
                    Integer syncType = OrderConsts.SYNC_TYPE.TRACK;
                    iOrderSyncTypeInfoService.saveOrderSyncTypeInfo(orderInfo.getOrderId(), syncType, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE,accessToken);
                }
            }
        }
    }

    @Override
    public void syncBillForm(OrderInfo orderInfo, OrderFee orderFee, int syncType,String accessToken) {
        if (StringUtils.isNotBlank(orderFee.getVehicleAffiliation()) && orderInfo.getIsNeedBill() != null
                && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            String billForm = billPlatformService.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation()));
            if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//??????
                iOrderSyncTypeInfoService.saveOrderSyncTypeInfo(orderInfo.getOrderId(), syncType, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE,accessToken);
            }
        }
    }

    @Override
    public String payProFee(long orderId, List<String> oilCardNums,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        boolean isLock = true;
        //todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payProFee" + orderId, 3, 5);
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("???????????????["+orderId+"]??????");
        }
        String msg = "Y";
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (scheduler.getCarDriverId() == null || scheduler.getCarDriverId() <= 0) {
            throw new BusinessException("?????????????????????,????????????");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("???????????????????????????,????????????");
        }
        if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.CANCELLED) {
            throw new BusinessException("???????????????????????????");
        }
        if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("????????????????????????????????????!");
        }
        if (scheduler.getVehicleClass() != null && scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() <= 0)) {
            throw new BusinessException("????????????????????????????????????!");
        }
        //?????????????????????????????????
        iOrderInfoHService.checkOilCarType(orderFee, orderInfo.getTenantId());
        //?????????????????????
        if(orderFee.getPrePayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()
                && StringUtils.isNotBlank(orderFee.getPrePayEquivalenceCardNumber())) {
            oilCardManagementService.saveEquivalenceCard(orderInfo,scheduler, orderFee.getPrePayEquivalenceCardNumber(), orderFee.getPrePayEquivalenceCardAmount(),true,true,loginInfo);
        }
        //?????????????????????
        if(orderFee.getAfterPayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()
                && StringUtils.isNotBlank(orderFee.getAfterPayEquivalenceCardNumber())) {
            oilCardManagementService.saveEquivalenceCard(orderInfo,scheduler, orderFee.getAfterPayEquivalenceCardNumber(), orderFee.getAfterPayEquivalenceCardAmount(),true,true,loginInfo);
        }
        //????????????????????????
        this.setFirstPayFlag(orderInfoExt, scheduler, orderFee, orderInfo,orderFeeExt,accessToken);
        String vehicleAffiliation = orderFee.getVehicleAffiliation();
        if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {// ?????????
            Long userId = 0L;
            if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// ???????????????
                // ?????????
                SysTenantDef tenantDef = iSysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                if (tenantDef == null) {
                    log.error("????????????????????????????????????????????????ID["+orderInfo.getToTenantId()+"]");
                    throw new BusinessException("??????????????????????????????????????????");
                }
                userId = tenantDef.getAdminUser();
                if(!iAccountBankUserTypeRelService.isUserTypeBindCard(userId,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,SysStaticDataEnum.USER_TYPE.ADMIN_USER)) {
                    msg = "?????????"+tenantDef.getName()+"????????????????????????????????????????????????????????? ???????????????????????????????????????";
                }
            }else if( scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
                userId = scheduler.getCollectionUserId();
            } else {// ??????????????? ???C???
                userId = scheduler.getCarDriverId();
                if(!iAccountBankUserTypeRelService.isUserTypeBindCard(userId,SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    msg = "?????????"+scheduler.getCarDriverMan()+"????????????????????????????????????????????????????????? ???????????????????????????????????????";
                }
            }
            if (orderFee.getVehicleAffiliation() == null) {
                log.error("????????????????????????,???????????????!????????????["+orderFee.getVehicleAffiliation()+"]");
                throw new BusinessException("????????????????????????,???????????????!");
            }
            long amountFee = orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee();
            if (orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {
                // ????????????
                this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler,orderInfoExt.getOilAffiliation(),true,loginInfo);
                Long pledgeFee = Long .parseLong(readisUtil.getSysCfg("PLEDGE_OILCARD_FEE", "0").getCfgValue());
                if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant() != OrderConsts.IS_TEMP_TENANT.YES) {
                    // ????????????  ????????????????????????????????????
                    oilCardManagementService.oilPledgeHandle(orderId, userId, vehicleAffiliation, oilCardNums, pledgeFee,
                            orderFee.getTenantId(),scheduler.getVehicleClass(),scheduler,loginInfo,accessToken);
                }
            }
            AdvanceChargeVo advanceChargeIn=new AdvanceChargeVo();
            int payType = OrderAccountConst.PAY_ADVANCE_TYPE.ORDINARY_PAY;
            boolean isPayTempOrder = false;
            if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
                //?????????????????????
                payType = OrderAccountConst.PAY_ADVANCE_TYPE.TEMPORARY_FLEET_PAY_DRIVER;
            }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES
                    && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0){
                //?????????????????????
                payType = OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_TEMPORARY_FLEET;
                //?????????????????????????????????
                isPayTempOrder = true;
            }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES){
                //??????
                payType = OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER;
                advanceChargeIn.setCollectionDriverUserId(scheduler.getCarDriverId());
            }
            if (StringUtils.isNotBlank(orderFee.getVehicleAffiliation()) &&
                    (orderFeeExt.getAppendFreight() == null || orderFeeExt.getAppendFreight() <= 0)
                    && (orderFeeExt.getOilBillType() == null || orderFeeExt.getOilBillType().intValue() != OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2)
            ) {
                String billForm = billPlatformService.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation()));
                if (billForm != null && SysStaticDataEnum.BILL_FORM.BILL_56K.equals(billForm)) {//56K????????????
                    BillSetting billSetting = iBillSettingService.getBillSettingByTenantId(orderInfo.getTenantId());
                    if (billSetting == null) {
                        throw new BusinessException("??????????????????????????????????????????!");
                    }
                    double attachFee = billSetting.getAttachFree() == null ? 0 : billSetting.getAttachFree();
                    if (attachFee > 0) {
                        Double appendFreight = ( (orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee()) +
                                (orderFee.getArrivePaymentFee() == null ? 0 : orderFee.getArrivePaymentFee()) +
                                (orderFee.getFinalFee() == null ? 0 : orderFee.getFinalFee()) ) * attachFee / 100.0;
                        orderFeeExt.setAppendFreight(appendFreight.longValue());
                    }
                }
            }
            advanceChargeIn.setAmountFee(amountFee);
            advanceChargeIn.setUserId(userId);
            advanceChargeIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
            advanceChargeIn.setETCFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
            advanceChargeIn.setVehicleAffiliation(orderFee.getVehicleAffiliation());
            advanceChargeIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
            advanceChargeIn.setOrderId(orderInfo.getOrderId());
            advanceChargeIn.setTenantId(orderInfo.getTenantId());
            advanceChargeIn.setIsNeedBill(orderInfo.getIsNeedBill());
            advanceChargeIn.setTotalFee(orderFee.getTotalFee() == null ? 0 : orderFee.getTotalFee());
            advanceChargeIn.setPayType(payType);
            advanceChargeIn.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
            advanceChargeIn.setOilAffiliation(orderInfoExt.getOilAffiliation());
            advanceChargeIn.setOilConsumer(orderFeeExt.getOilConsumer() ==null ? OrderConsts.OIL_CONSUMER.SELF : orderFeeExt.getOilConsumer());
            advanceChargeIn.setOilAccountType(orderFeeExt.getOilAccountType());
            advanceChargeIn.setOilBillType(orderFeeExt.getOilBillType());
            this.payAdvanceCharge(advanceChargeIn,accessToken);
            if (isPayTempOrder) {
                this.payProFee(orderInfo.getToOrderId(), oilCardNums,accessToken);
            }
        } else {
            if (scheduler.getCarDriverId() == null) {
                throw new BusinessException("?????????????????????");
            }
            boolean isUserBindCard = iAccountBankUserTypeRelService.isUserTypeBindCard(scheduler.getCarDriverId(),SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            if(!isUserBindCard) {
                msg = "?????????"+scheduler.getCarDriverMan()+"????????????????????????????????????????????????????????? ???????????????????????????????????????";
            }
            if (scheduler.getVehicleClass() != null
                    && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (orderFee.getPreOilVirtualFee() != null && orderFee.getPreOilVirtualFee() > 0) {

                    OrderPaymentWayOilOut out=iOperationOilService.getOrderPaymentWayOil(scheduler.getCarDriverId(),loginInfo.getTenantId(),SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                    if (orderInfoExt.getPaymentWay() != null){
                        if (orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                            if (out.getExpenseOil() != null && out.getExpenseOil() > 0) {
                                SysStaticData payment_way = getSysStaticData("PAYMENT_WAY", orderInfoExt.getPaymentWay().toString());
                                String paymentWayName = "";
                                if(payment_way != null)
                                {
                                    paymentWayName = payment_way.getCodeName();
                                }
                                throw new BusinessException("??????????????????"+paymentWayName+"??????????????????????????????????????????");
                            }
                        }else{
                            if ((out.getCostOil() != null && out.getCostOil() > 0) || (out.getContractOil() != null && out.getContractOil() > 0) ) {
                                SysStaticData payment_way = getSysStaticData("PAYMENT_WAY", orderInfoExt.getPaymentWay().toString());
                                String paymentWayName = "";
                                if(payment_way != null) {
                                    paymentWayName = payment_way.getCodeName();
                                }
                                throw new BusinessException("?????????????????????"+paymentWayName+"??????????????????????????????????????????");
                            }
                        }
                    }
                }
                if (orderInfoExt.getPaymentWay() != null
                        && (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST || orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)) {
                    if (orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {
                        // ????????????
                        if (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                            this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler,orderInfoExt.getOilAffiliation(),true,loginInfo);
                            //??????????????????????????????
                            this.paySubsidy(orderId,loginInfo,accessToken);
                        }else if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE){
                            oilCardNums = new ArrayList<>();
                            //???????????????????????????
                            List<OrderOilCardInfo> orderOilCardInfos = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
                            for (OrderOilCardInfo orderOilCardInfo : orderOilCardInfos) {
                                String oilCardNum = orderOilCardInfo.getOilCardNum();
                                List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, scheduler.getTenantId());
                                if (list == null || list.size() <= 0) {
                                    throw new BusinessException("????????????["+oilCardNum+"]?????????!");
                                }

                                OilCardManagement oilCard = list.get(0);
                                //?????????????????????????????????
                                int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
                                Long balance = orderOilCardInfo.getOilFee() == null ? 0 : orderOilCardInfo.getOilFee();
                                //?????????????????????????????????
                                if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                                    //????????????????????????
                                    iOilEntityService.saveOilCardLog(oilCardNum, scheduler.getTenantId(), scheduler.getCarDriverId(), scheduler
                                            ,orderInfo,balance,oilCard,"0");
                                }
                                oilCardNums.add(oilCardNum);
                            }
                        }
                        Long pledgeFee = null; // todo
                        // ????????????
                        oilCardManagementService.oilPledgeHandle(orderId, scheduler.getCarDriverId(), vehicleAffiliation, oilCardNums, pledgeFee,
                                orderFee.getTenantId(),scheduler.getVehicleClass(),scheduler,loginInfo,accessToken);
                    }
                    if (scheduler.getCopilotUserId() != null && scheduler.getCopilotUserId() > 0) {
                        if(!iAccountBankUserTypeRelService.isUserTypeBindCard(scheduler.getCopilotUserId(),SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                            msg = "?????????"+(isUserBindCard ? "" : scheduler.getCarDriverMan()+"???")+scheduler.getCopilotMan()+"????????????????????????????????????????????????????????? ???????????????????????????????????????";
                        }
                    }
                    AdvanceChargeVo advanceChargeIn=new AdvanceChargeVo();
                    advanceChargeIn.setMasterUserId(scheduler.getCarDriverId());
                    advanceChargeIn.setVehicleAffiliation("0");//???????????????????????????0
                    advanceChargeIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
                    advanceChargeIn.setFictitiousOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                    advanceChargeIn.setBridgeFee(orderFeeExt.getPontage() == null ? 0 : orderFeeExt.getPontage());
                    advanceChargeIn.setMasterSubsidy(orderFeeExt.getSalary() == null ? 0 : orderFeeExt.getSalary());
                    advanceChargeIn.setSlaveSubsidy(orderFeeExt.getCopilotSalary() == null ? 0 : orderFeeExt.getCopilotSalary());
                    advanceChargeIn.setSlaveUserId(scheduler.getCopilotUserId() == null ? 0 : scheduler.getCopilotUserId());
                    advanceChargeIn.setOrderId(orderInfo.getOrderId());
                    advanceChargeIn.setTenantId(orderInfo.getTenantId());
                    advanceChargeIn.setIsNeedBill(orderInfo.getIsNeedBill());
                    advanceChargeIn.setTotalFee(orderFee.getCostPrice() == null ? 0 : orderFee.getCostPrice());
                    advanceChargeIn.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
                    advanceChargeIn.setOilAffiliation(orderInfoExt.getOilAffiliation());
                    advanceChargeIn.setOilConsumer(orderFeeExt.getOilConsumer() ==null ? OrderConsts.OIL_CONSUMER.SELF : orderFeeExt.getOilConsumer());
                    advanceChargeIn.setOilAccountType(orderFeeExt.getOilAccountType());
                    advanceChargeIn.setOilBillType(orderFeeExt.getOilBillType());
                    this.payAdvanceChargeToOwnCar(advanceChargeIn,accessToken);
                } else if (orderInfoExt.getPaymentWay() != null
                        && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
                    // ?????????????????????
                    if (orderFee.getVehicleAffiliation() == null) {
                        throw new BusinessException("????????????????????????,???????????????!");
                    }
                    // ????????????
                    long amountFee = orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee();
                    if (orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {// ???????????????????????????
                        // ????????????
                        this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler,orderInfoExt.getOilAffiliation(),true,loginInfo);
                        Long pledgeFee = Long .parseLong(readisUtil.getSysCfg("PLEDGE_OILCARD_FEE", "0").getCfgValue());
                        // ????????????
                        oilCardManagementService.oilPledgeHandle(orderId, scheduler.getCarDriverId(), vehicleAffiliation, oilCardNums, pledgeFee,
                                orderFee.getTenantId(),scheduler.getVehicleClass(),scheduler,loginInfo,accessToken);
                    }
                    AdvanceChargeVo advanceChargeIn=new AdvanceChargeVo();
                    advanceChargeIn.setAmountFee(amountFee);
                    advanceChargeIn.setUserId(scheduler.getCarDriverId());
                    advanceChargeIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                    advanceChargeIn.setETCFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
                    advanceChargeIn.setVehicleAffiliation("0");//???????????????????????????0
                    advanceChargeIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
                    advanceChargeIn.setOrderId(orderInfo.getOrderId());
                    advanceChargeIn.setTenantId(orderInfo.getTenantId());
                    advanceChargeIn.setIsNeedBill(orderInfo.getIsNeedBill());
                    advanceChargeIn.setTotalFee(orderFee.getTotalFee() == null ? 0 : orderFee.getTotalFee());
                    advanceChargeIn.setPayType(OrderAccountConst.PAY_ADVANCE_TYPE.ORDINARY_PAY);
                    advanceChargeIn.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
                    advanceChargeIn.setOilAffiliation(orderInfoExt.getOilAffiliation());
                    advanceChargeIn.setOilConsumer(orderFeeExt.getOilConsumer() ==null ? OrderConsts.OIL_CONSUMER.SELF : orderFeeExt.getOilConsumer());
                    advanceChargeIn.setOilAccountType(orderFeeExt.getOilAccountType());
                    advanceChargeIn.setOilBillType(orderFeeExt.getOilBillType());
                    this.payAdvanceCharge(advanceChargeIn,accessToken);
                }
            }
        }
        iOrderOpRecordService.saveOrUpdate(orderId, OrderConsts.OrderOpType.PRE_FEE,accessToken);
        orderInfoExt.setPreAmountFlag(OrderConsts.AMOUNT_FLAG.ALREADY_PAY);// ????????? ?????????
        orderFee.setPreAmountTime(getDateToLocalDateTime(new Date()));
        // ????????????
        String userName = loginInfo.getName();
        if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
            //????????????????????????
            userName = "????????????";
            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo,SysOperLogConst.OperType.Update,
                     "[" + userName + "]????????????????????????",accessToken, orderInfo.getOrderId());
        }else if(orderInfoExt.getPaymentWay()==null||orderInfoExt.getPaymentWay()!= OrderConsts.PAYMENT_WAY.EXPENSE){
            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                    "?????????????????????????????????"+(oilCardNums==null||oilCardNums.size()==0?"???":oilCardNums.toString()),accessToken, orderInfo.getOrderId());
        }
        // ??????????????????
        this.synPayCenterUpdateOrderOrProblemInfo(orderInfo, scheduler);
        orderInfoService.saveOrUpdate(orderInfo);
        iOrderFeeService.saveOrUpdate(orderFee);
        orderInfoExtService.saveOrUpdate(orderInfoExt);
        orderFeeExtService.saveOrUpdate(orderFeeExt);
        orderSchedulerService.saveOrUpdate(scheduler);
        return msg;
    }

    @Override
    public void setFirstPayFlag(OrderInfoExt orderInfoExt, OrderScheduler orderScheduler, OrderFee orderFee, OrderInfo orderInfo, OrderFeeExt orderFeeExt,String accessToken) {
        if (orderInfoExt.getFirstPayFlag() == null || orderInfoExt.getFirstPayFlag().intValue() == OrderConsts.AMOUNT_FLAG.WILL_PAY) {
            orderInfoExt.setFirstPayFlag(OrderConsts.AMOUNT_FLAG.ALREADY_PAY);
            if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                // ???????????????,????????????????????????????????????????????????????????????????????????
                String limitDate = readisUtil.getSysCfg("SYNC_DATE_LIMIT", "0").getCfgValue();
                //?????????????????????
                try {
                    if (StringUtils.isNotBlank(limitDate) &&
                            DateUtil.formatStringToDate(limitDate, DateUtil.DATETIME_FORMAT).getTime() > getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime()
                            ) {

                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Long billMethod = billPlatformService.getBillMethodByTenantId(orderInfo.getTenantId());
                String vehicleAffiliationOld = orderFee.getVehicleAffiliation();
                if (billMethod != null && billMethod > 0) {
                    orderFee.setVehicleAffiliation(billMethod.toString());
                }
                String billForm56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"").getCodeName();
                String billForm = billPlatformService.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation()));
                if ((StringUtils.isBlank(vehicleAffiliationOld) || !orderFee.getVehicleAffiliation().equals(vehicleAffiliationOld))
                        && billForm != null) {//??????56K????????????
                    if (billForm56K.equals(billForm)) {
                        try {
                            orderSync56KService.syncOrderInfoTo56K(orderInfoExt.getOrderId(), false, false);
                            orderSync56KService.syncOrderInfoTo56K(orderInfoExt.getOrderId(), true, false);
                        } catch (Exception e) {
                            log.error("??????56K????????????????????????", e);
                        }
                    }else if(SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){
                        Integer syncType = OrderConsts.SYNC_TYPE.ADD;
                        iOrderSyncTypeInfoService.saveOrderSyncTypeInfo(orderInfo.getOrderId(), syncType, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE,accessToken);
                    }
                }
                orderInfoExt.setOilAffiliation(!"0".equals(orderInfoExt.getOilAffiliation())&&!"1".equals(orderInfoExt.getOilAffiliation())
                        ?orderFee.getVehicleAffiliation():orderInfoExt.getOilAffiliation());
                OrderGoods orderGoods = iOrderGoodsService.getOrderGoods(orderInfo.getOrderId());
                // ???????????????
                iOpAccountService.createOrderLimit(orderInfo, orderInfoExt, orderFee, orderScheduler, orderGoods, orderFeeExt,orderInfo.getTenantId());
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @throws Exception
     */
    @Override
    public void updateOrderExceptionPrice(OrderProblemInfo orderProblemInfo, Long exceptionPrice) {
        if (exceptionPrice == null) {
            throw new BusinessException("??????????????????????????????");
        }
        OrderFee orderFee = orderFeeMapper.selectOne(new QueryWrapper<OrderFee>().eq("order_id",orderProblemInfo.getOrderId()));
        if (orderFee == null) {
            OrderFeeH orderFeeH  = orderFeeHMapper.selectOne(new QueryWrapper<OrderFeeH>().eq("order_id",orderProblemInfo.getOrderId()));
            if (orderFeeH != null) {
                if (orderProblemInfo.getProblemCondition() != null
                        && SysStaticDataEnum.PROBLEM_CONDITION.COST == orderProblemInfo.getProblemCondition()) {
                    Long costExceptionFee = orderFeeH.getCostExceptionFee() == null ? 0L
                            : orderFeeH.getCostExceptionFee();
                    if (exceptionPrice >= 0) { // ??????0 ??????????????????
                        orderFeeH.setExceptionIn((orderFeeH.getExceptionIn() == null ? 0 : orderFeeH.getExceptionIn())
                                + Math.abs(exceptionPrice));
                    } else {// ????????????
                        orderFeeH
                                .setExceptionOut((orderFeeH.getExceptionOut() == null ? 0 : orderFeeH.getExceptionOut())
                                        + Math.abs(exceptionPrice));
                    }
                    orderFeeH.setCostExceptionFee(costExceptionFee + exceptionPrice);
                    orderFeeHMapper.updateById(orderFeeH);
                } else if (orderProblemInfo.getProblemCondition() != null
                        && SysStaticDataEnum.PROBLEM_CONDITION.INCOME == orderProblemInfo.getProblemCondition()) {
                    long incomeExceptionFee = orderFeeH.getIncomeExceptionFee() == null ? 0L
                            : orderFeeH.getIncomeExceptionFee();
                    orderFeeH.setIncomeExceptionFee(incomeExceptionFee + exceptionPrice);
                    orderFeeHMapper.updateById(orderFeeH);
                }
            } else {
                throw new BusinessException("???????????????[" + orderProblemInfo.getOrderId() + "]??????!");
            }
        } else {
            if (orderProblemInfo.getProblemCondition() != null
                    && SysStaticDataEnum.PROBLEM_CONDITION.COST == orderProblemInfo.getProblemCondition()) {
                Long costExceptionFee = orderFee.getCostExceptionFee() == null ? 0L : orderFee.getCostExceptionFee();
                if (exceptionPrice >= 0) { // ??????0 ??????????????????
                    orderFee.setExceptionIn((orderFee.getExceptionIn() == null ? 0 : orderFee.getExceptionIn())
                            + Math.abs(exceptionPrice));
                } else {// ????????????
                    orderFee.setExceptionOut((orderFee.getExceptionOut() == null ? 0 : orderFee.getExceptionOut())
                            + Math.abs(exceptionPrice));
                }
                orderFee.setCostExceptionFee(costExceptionFee + exceptionPrice);
                saveOrUpdate(orderFee);
            } else if (orderProblemInfo.getProblemCondition() != null
                    && SysStaticDataEnum.PROBLEM_CONDITION.INCOME == orderProblemInfo.getProblemCondition()) {
                long incomeExceptionFee = orderFee.getIncomeExceptionFee() == null ? 0L
                        : orderFee.getIncomeExceptionFee();
                orderFee.setIncomeExceptionFee(incomeExceptionFee + exceptionPrice);
                saveOrUpdate(orderFee);
            }
        }
    }

    @Override
    public Long queryCustomOil(Long excludeOrderId,Long tenantId) {
        Long customOil = orderFeeMapper.queryOrderNoPayCustomOil(tenantId, excludeOrderId);
        if (customOil == null) {
            return 0L;
        }else{
            return customOil;
        }
    }

    @Override
    public Long queryCustomOil(Long excludeOrderId, LoginInfo loginInfo) {
        Long noPayCustomOilFee = orderFeeMapper.queryOrderNoPayCustomOil(loginInfo.getTenantId(), excludeOrderId);
        if (noPayCustomOilFee == null) {
            noPayCustomOilFee = 0L;
        }

        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        Long customOilFee = iOrderAccountService.queryOilBalance(sysTenantDef.getAdminUser(), loginInfo.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        return customOilFee - noPayCustomOilFee;
    }

    @Override
    public OrderFeeExt getOrderFeeExtByOrderId(Long orderId) {
        if(orderId==null||orderId<=0) {
            throw new BusinessException("??????????????????");
        }
        OrderFeeExt orderFeeExt=orderFeeExtService.getOrderFeeExt(orderId);
        if(orderFeeExt==null) {
            OrderFeeExtH orderFeeExtH=orderFeeExtHService.getOrderFeeExtH(orderId);
            if(orderFeeExtH!=null) {
                orderFeeExt=new OrderFeeExt();
                BeanUtils.copyProperties(orderFeeExtH,orderFeeExt);
            }else {
                throw new BusinessException("??????????????????["+orderId+"]?????????");
            }
        }

        return orderFeeExt;
    }

    @Override
    public OrderFee getOrderFee(Long orderId) {
        LambdaQueryWrapper<OrderFee> lambda=new QueryWrapper<OrderFee>().lambda();
        lambda.eq(OrderFee::getOrderId,orderId);
        List<OrderFee> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void synPayCenterAddOrder(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler,
                                     OrderFee orderFee, OrderFeeExt orderFeeExt, OrderInfoExt orderInfoExt) {
        if (orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            // ???????????????
            if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
                // ???????????????????????? ?????????
                if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {
                    // c ??????
                    this.syncBillFormOrder(orderInfo, orderGoods, orderScheduler,
                            orderFee, orderFeeExt, orderInfoExt, false,false);
                }
            }
        }
    }

    @Override
    public void syncBillFormOrder(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler,
                                  OrderFee orderFee, OrderFeeExt orderFeeExt, OrderInfoExt orderInfoExt,
                                  boolean isCalculateFee, boolean isUpdate) {
        if (StringUtils.isNotBlank(orderFee.getVehicleAffiliation())) {
            String billForm56K = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"").getCodeName();
            String billForm = billPlatformService.getPrefixByUserId(Long.valueOf(orderFee.getVehicleAffiliation()));

            if (billForm != null && billForm56K.equals(billForm)) {//56K????????????
                orderSync56KService.syncOrderInfoTo56K(orderInfo.getOrderId(), isUpdate,false);
            }else if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//??????
                // TODO: 2022/3/25 ???????????????
                /*                IOrderSyncTypeInfoSV orderSyncTypeInfoSV = (IOrderSyncTypeInfoSV) SysContexts.getBean("orderSyncTypeInfoSV");
                Integer syncType = isUpdate ? OrderConsts.SYNC_TYPE.UPDATE : OrderConsts.SYNC_TYPE.ADD;
                orderSyncTypeInfoSV.saveOrderSyncTypeInfo(orderInfo.getOrderId(), syncType, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE);*/
            }else{
                Long cashFee = null;
                Long entityOilFee = null;
                Long virtualFee = null;
                Long etcFee = null;
                if (isCalculateFee) {
                    FeesDto feeMap = this.calculateSyncFee(orderFee, orderInfo, orderInfoExt, orderScheduler);
//                    cashFee = DataFormat.getLongKey(feeMap, "cashFee");
//                    entityOilFee = DataFormat.getLongKey(feeMap, "entityOilFee");
//                    virtualFee = DataFormat.getLongKey(feeMap, "virtualFee");
//                    etcFee = DataFormat.getLongKey(feeMap, "etcFee");
                }
                synPayCenter(orderInfo, orderGoods, orderScheduler, orderFee, orderFeeExt, cashFee, entityOilFee, virtualFee, etcFee);
            }
        }
    }

    @Override
    public void synPayCenterAcceptOrder(OrderInfo newOrderInfo, OrderScheduler orderScheduler) {
        Long orderId = newOrderInfo.getOrderId();
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
            if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (newOrderInfo.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    // ?????????
                    OrderDetailsOutDto orderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
                    OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
                    if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                        // ????????????????????????
                        OrderScheduler preOrderScheduler = orderDetailsOut.getOrderScheduler();
                        OrderFee preOrderFee = orderDetailsOut.getOrderFee();
                        OrderGoods preOrderGoods = orderDetailsOut.getOrderGoods();
                        OrderFeeExt preOrderFeeExt = orderDetailsOut.getOrderFeeExt();
                        OrderInfoExt orderInfoExt = orderDetailsOut.getOrderInfoExt();
                        this.syncBillFormOrder(orderInfo, preOrderGoods, preOrderScheduler,
                                preOrderFee, preOrderFeeExt, orderInfoExt, false,false);
                        return;
                    } else {
                        // ??????????????????
                        if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                            orderId = orderInfo.getFromOrderId();
                            OrderDetailsOutDto fromOrderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
                            orderInfo = fromOrderDetailsOut.getOrderInfo();
                            if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                                // ???????????????????????????
                                OrderScheduler preOrderScheduler = fromOrderDetailsOut.getOrderScheduler();
                                OrderFee preOrderFee = fromOrderDetailsOut.getOrderFee();
                                OrderGoods preOrderGoods = fromOrderDetailsOut.getOrderGoods();
                                OrderFeeExt preOrderFeeExt = fromOrderDetailsOut.getOrderFeeExt();
                                OrderInfoExt orderInfoExt = fromOrderDetailsOut.getOrderInfoExt();
                                this.syncBillFormOrder(orderInfo, preOrderGoods, preOrderScheduler,
                                        preOrderFee, preOrderFeeExt, orderInfoExt, false,false);
                                return;
                            }
                        }
                    }
                }
            } else if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    ||orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    ||orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                if (newOrderInfo.getToTenantId() == null || newOrderInfo.getToTenantId() <= 0) {
                    // C?????? ???????????????????????? ?????????????????????????????????????????????
                    if (newOrderInfo.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                        //
                        OrderDetailsOutDto orderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
                        OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
                        if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                            // ????????????????????????
                            OrderScheduler preOrderScheduler = orderDetailsOut.getOrderScheduler();
                            OrderFee preOrderFee = orderDetailsOut.getOrderFee();
                            OrderGoods preOrderGoods = orderDetailsOut.getOrderGoods();
                            OrderFeeExt preOrderFeeExt = orderDetailsOut.getOrderFeeExt();
                            OrderInfoExt orderInfoExt = orderDetailsOut.getOrderInfoExt();
                            this.syncBillFormOrder(orderInfo, preOrderGoods, preOrderScheduler,
                                    preOrderFee, preOrderFeeExt, orderInfoExt, false,false);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * ????????????
     * @param preOrderFee
     * @param orderInfo
     * @param preOrderInfoExt
     * @param orderScheduler
     * @return
     * @throws Exception
     */
    @Override
    public FeesDto calculateSyncFee(OrderFee preOrderFee, OrderInfo orderInfo,
                                    OrderInfoExt preOrderInfoExt, OrderScheduler orderScheduler){
        Long virtualFee = 0L;
        Long cashFee = 0L;
        Long etcFee = 0L;
        Long entityOilFee = 0L;
        Long totalFee = 0L;
        //,boolean isIndependentCash
        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            if (preOrderInfoExt.getPaymentWay() != null) {
                //????????????(????????????)?????????ETC
                if(preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
                    //?????????
                    cashFee = preOrderFee.getPreCashFee() == null ? 0 : preOrderFee.getPreCashFee()
                            + (preOrderFee.getArrivePaymentFee() == null ? 0 : preOrderFee.getArrivePaymentFee())
                            + (preOrderFee.getFinalFee() == null ? 0 : preOrderFee.getFinalFee())
                            +(preOrderFee.getPreEtcFee() == null ? 0 : preOrderFee.getPreEtcFee());
                }else if(preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE || preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST){
                    //????????????
                    cashFee = preOrderFee.getCostPrice() == null ? 0 : preOrderFee.getCostPrice();
                }
            }
            if(preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
                cashFee += preOrderFee.getPreOilVirtualFee() == null ? 0 : preOrderFee.getPreOilVirtualFee();
                cashFee += preOrderFee.getPreOilFee() == null ? 0 : preOrderFee.getPreOilFee();
            }
        }else if(orderScheduler.getVehicleClass() == null || ( orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )){
            //??????????????????????????????????????????
            OrderLimitFeeOutDto feeOut = updateOrderService.getOrderLimitFeeOut(preOrderFee.getOrderId(), preOrderFee.getPreCashFee()==null?0:preOrderFee.getPreCashFee()
                    ,  preOrderFee.getPreOilVirtualFee()==null?0:preOrderFee.getPreOilVirtualFee(), preOrderFee.getPreEtcFee()==null?0:preOrderFee.getPreEtcFee(), preOrderFee.getTenantId());
            Long entityOilMax = redisUtil.get(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + preOrderFee.getOrderId())  == null ? 0 :
                    Long.parseLong((String) redisUtil.get(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + preOrderFee.getOrderId()));


            log.info("??????["+preOrderFee.getOrderId()+"]????????????:"+feeOut.getUseCash()
                    +"?????????????????????:"+feeOut.getNoPayCash()+"??????????????????:"+feeOut.getNoPayOil()
                    +"??????????????????:"+feeOut.getUseOil()+"???????????????ETC:"+feeOut.getUseEtc()
                    +"???????????????ETC:"+feeOut.getNoPayEtc()
            );

            if (preOrderInfoExt.getPreAmountFlag() != null && preOrderInfoExt.getPreAmountFlag().intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                //???????????????????????? = ???????????? + ????????????
                virtualFee = (feeOut.getUseOil() == null ? 0 : feeOut.getUseOil())
                        + (feeOut.getNoPayOil() == null ? 0 : feeOut.getNoPayOil());
                //????????????????????????????????????
                cashFee = (feeOut.getUseCash() == null ? 0 : feeOut.getUseCash())
                        + (feeOut.getNoPayCash() == null ? 0 : feeOut.getNoPayCash())
                        + (preOrderFee.getArrivePaymentState() == 1 ? 0 :
                        (preOrderFee.getArrivePaymentFee() == null ? 0 : preOrderFee.getArrivePaymentFee()))
                        + (preOrderFee.getFinalFee() == null ? 0 : preOrderFee.getFinalFee())
                        - (preOrderFee.getInsuranceFee() == null ? 0 : preOrderFee.getInsuranceFee());
                etcFee =  (feeOut.getUseEtc() == null ? 0 : feeOut.getUseEtc())
                        + (feeOut.getNoPayEtc() == null ? 0 : feeOut.getNoPayEtc());
                //??????????????????????????????????????????????????????
                entityOilFee = entityOilMax != null ? entityOilMax : preOrderFee.getPreOilFee();
            }else{
                //???????????????????????? = ???????????? + ????????????
                virtualFee =preOrderFee.getPreOilVirtualFee() == null ? 0 : preOrderFee.getPreOilVirtualFee();
                //????????????????????????????????????
                cashFee = preOrderFee.getPreCashFee() == null ? 0 : preOrderFee.getPreCashFee()
                        + (preOrderFee.getArrivePaymentFee() == null ? 0 : preOrderFee.getArrivePaymentFee())
                        + (preOrderFee.getFinalFee() == null ? 0 : preOrderFee.getFinalFee())
                        +(preOrderFee.getPreEtcFee() == null ? 0 : preOrderFee.getPreEtcFee());
                etcFee =  (preOrderFee.getPreEtcFee() == null ? 0 : preOrderFee.getPreEtcFee());
                //??????????????????????????????????????????????????????
                entityOilFee = preOrderFee.getPreOilFee() == null ? 0 : preOrderFee.getPreOilFee();
            }
            //?????????????????????????????????
            List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),
                    orderInfo.getTenantId());
            if (list != null && list.size() > 0) {
                for (OrderProblemInfo p : list) {
                    // ????????????????????????????????????????????????
                    if (p.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                            && SysStaticDataEnum.EXPENSE_STATE.END == p.getState().intValue()) {
                        cashFee += p.getProblemDealPrice() == null ? 0 : p.getProblemDealPrice();
                    }
                }
            }
            List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(orderInfo.getOrderId());
            if (agingInfos != null && agingInfos.size() > 0) {
                for (OrderAgingInfo agingInfo : agingInfos) {
                    if (agingInfo != null && agingInfo.getAuditSts() !=null
                            && SysStaticDataEnum.EXPENSE_STATE.END == agingInfo.getAuditSts().intValue() ) {
                        cashFee += 0 - (agingInfo.getFinePrice() == null ? 0 : agingInfo.getFinePrice());
                    }
                }
            }
        }
        return FeesDto.of().setVirtualFee(virtualFee).setCashFee(cashFee).setEtcFee(etcFee).setEntityOilFee(entityOilFee)
                .setTotalFee(totalFee);
    }



    /**
     * ????????????????????????
     *
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param cashFee
     * @param entityOilFee
     * @param virtualFee
     * @param etcFee
     *            ???????????????????????????????????????????????????????????????????????????
     */
    private void synPayCenter(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler,
                              OrderFee orderFee, OrderFeeExt orderFeeExt, Long cashFee,Long entityOilFee,Long virtualFee,Long etcFee){
        if (StringUtils.isEmpty(orderFee.getVehicleAffiliation())) {
            log.error("?????????????????????id??????????????????????????????,????????????[" + orderInfo.getOrderId() + "]????????????????????????");
            orderFeeExt.setResultInfo("?????????????????????id??????????????????????????????");
            orderFeeExtService.saveOrUpdate(orderFeeExt);
            return;
        }

        OrderInfoInDto orderInfoIn = new OrderInfoInDto();


        orderInfoIn.setOrderId(orderInfo.getOrderId());
        orderInfoIn.setTenantId(orderInfo.getTenantId());
        orderInfoIn.setCustName(orderGoods.getCompanyName());
        orderInfoIn.setCustAddress(orderGoods.getAddress());
        orderInfoIn.setGoodsName(orderGoods.getGoodsName());
        orderInfoIn.setGoodsVolume(orderGoods.getSquare());
        orderInfoIn.setGoodsWeight(orderGoods.getWeight());

        orderInfoIn.setSourceAddressDetail(orderGoods.getAddrDtl());
        orderInfoIn.setSourceEand(orderGoods.getEand());
        orderInfoIn.setSourceNand(orderGoods.getNand());

        orderInfoIn.setDesAddressDetail(orderGoods.getDesDtl());
        orderInfoIn.setDesNand(orderGoods.getNandDes());
        orderInfoIn.setDesEand(orderGoods.getEandDes());
        Long distance = orderScheduler.getDistance();// ?????????
        orderInfoIn.setArriveTime(orderScheduler.getArriveTime());
        orderInfoIn.setReceiveName(orderGoods.getReciveName());
        orderInfoIn.setReceivePhone(orderGoods.getRecivePhone());
        orderInfoIn.setPlateNumber(orderScheduler.getPlateNumber());

        orderInfoIn.setDriverUserName(orderScheduler.getCarDriverMan());
        orderInfoIn.setDriverUserId(orderScheduler.getCarDriverId());
        orderInfoIn.setDriverPhone(orderScheduler.getCarDriverPhone());

        Long totalFee=0L;
        if(orderScheduler.getVehicleClass() != null
                && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1){
            if (cashFee != null && cashFee > 0) {
                totalFee=totalFee+cashFee;
                orderInfoIn.setCashFee(cashFee);
            } else {
                totalFee=totalFee+cashFee;
                orderInfoIn.setCashFee(cashFee);
            }
            orderInfoIn.setEntityOilFee(0L);
            orderInfoIn.setEtcFee(0L);
            orderInfoIn.setVirtualOilFee(0L);
        }else if(orderScheduler.getVehicleClass() != null
                && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4){
            if (cashFee != null && cashFee > 0) {
                totalFee=totalFee+cashFee;
                orderInfoIn.setCashFee(cashFee);
            } else {
                cashFee=(orderFee.getPreCashFee() == null ? 0L : orderFee.getPreCashFee().longValue())
                        + (orderFee.getFinalFee() == null ? 0L : orderFee.getFinalFee().longValue());
                totalFee=totalFee+cashFee;
                orderInfoIn.setCashFee(cashFee);
            }
            if (etcFee != null && etcFee > 0) {
                totalFee=totalFee+etcFee;
                orderInfoIn.setEtcFee(etcFee);
            }else{
                orderInfoIn.setEtcFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
                totalFee=totalFee+(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
            }
            if (virtualFee != null && virtualFee > 0) {
                // ?????????
                totalFee=totalFee+virtualFee;
                orderInfoIn.setVirtualOilFee(virtualFee);
            }else{
                // ?????????
                totalFee=totalFee+(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                orderInfoIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
            }
            if (entityOilFee != null && entityOilFee > 0) {
                totalFee=totalFee+entityOilFee;
                orderInfoIn.setEntityOilFee(entityOilFee);
            }else{
                totalFee=totalFee+(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
                orderInfoIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
            }
        }
        orderInfoIn.setTotalFee(totalFee);

        orderFeeExtService.saveOrUpdate(orderFeeExt);


    }

    public Date getDateByLocalDateTime(LocalDateTime localDateTime){
        //????????????????????????
        ZoneId zoneId = ZoneId.systemDefault();
        //????????????????????????
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        //????????????
        Date date = Date.from(zonedDateTime.toInstant());
        return date;
    }

    public LocalDateTime getLocalDateTimeByDate(Date date){
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = date.toInstant().atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
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
     * ??????????????????????????????
     * @param oilCardNums ????????????
     * @param orderFee
     * @param orderInfo
     * @param oilFee ??????????????????
     */
    @Override
    public void verifyOilCardNum(List<String> oilCardNums,OrderFee orderFee,OrderInfo orderInfo
            ,long oilFee, OrderScheduler scheduler,String oilAffiliation,boolean isLastAudit,LoginInfo loginInfo) {
        //??????????????????
        if(oilFee>0&&(isLastAudit||oilCardNums.size()>0)) {
            if (oilCardNums == null || oilCardNums.size() <= 0) {
                throw new BusinessException("??????????????????");
            }
            Map verifyMap =  this.verifyOilCardNum(orderInfo.getOrderId(),oilCardNums,true,orderFee
                    .getPreOilFee());
            boolean  isEnough = DataFormat.getBooleanKey(verifyMap, "isEnough");
            if (!isEnough) {
                throw new BusinessException("????????????????????????!");
            }
        }
        String oilcard = "";
        int service = 0;
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());

        //???????????????????????????
        List<OrderOilCardInfo> oilCardInfosOld = null;
        if(orderInfoExt.getPaymentWay()==null||orderInfoExt.getPaymentWay()!= OrderConsts.PAYMENT_WAY.EXPENSE) {
            oilCardInfosOld = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderInfo.getOrderId(), null);
        }

        for (String oilCardNum : oilCardNums) {
            if (StringUtils.isBlank(oilCardNum)) {
                throw new BusinessException("??????????????????");
            }
            List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, scheduler.getTenantId());
            if (list == null || list.size() <= 0) {
                if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
                    if(isLastAudit) {
                        oilCardManagementService.modifyOilCardBalance(
                                oilCardNum,
                                oilFee,
                                true,scheduler.getPlateNumber(),
                                scheduler.getCarDriverMan(),orderInfo.getOrderId(),loginInfo.getId(),
                                orderInfo.getTenantId(),false,loginInfo);
                    }
                    list = oilCardManagementService.findByOilCardNum(oilCardNum, scheduler.getTenantId());
                    if (list == null || list.size() == 0) {
                        throw new BusinessException("????????????["+oilCardNum+"]????????????!");
                    }
                }else{
                    throw new BusinessException("????????????["+oilCardNum+"]?????????!");
                }
            }
            OilCardManagement oilCard = list.get(0);
            //?????????????????????????????????
            int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;

            Long balance = 0L;
            Long oilCardFee = oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;

            //???????????????????????????????????????
            if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                if (service == 1) {
                    throw new BusinessException("??????????????????????????????,??????["+oilCardNum+"]!");
                }
                service++;
                balance = oilFee;
            }else{
                //???????????????????????? ??????????????????????????????
                if (oilFee <= oilCardFee) {
                    balance = oilFee;
                }else{//???????????????????????? ????????????????????? ????????????
                    balance = oilCardFee;
                }
            }
            oilFee = oilFee - balance;
            if(isLastAudit) {
                //????????????????????????
                iOilEntityService.saveOilCardLog(oilCardNum, scheduler.getTenantId(), scheduler.getCarDriverId(), scheduler
                        ,orderInfo,balance,oilCard,oilAffiliation);
                //????????????????????????(????????????/????????????)
                if (cardType !=  SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                    // TODO ??????????????? loginInfo.getid
                    oilCardManagementService.modifyOilCardBalance(oilCardNum, balance, false,
                            scheduler.getPlateNumber(),scheduler.getCarDriverMan(),
                            scheduler.getOrderId(),loginInfo.getUserInfoId(),
                            orderInfo.getTenantId(),false,loginInfo);
                }else{
                    OilCardLog cardLog = new OilCardLog();
                    cardLog.setCardId(oilCard.getId());
                    cardLog.setCarDriverMan(scheduler.getCarDriverMan());
                    cardLog.setCarUserType(scheduler.getVehicleClass());
                    cardLog.setOrderId(scheduler.getOrderId());
                    cardLog.setOilFee(balance);
                    cardLog.setPlateNumber(scheduler.getPlateNumber());
                    cardLog.setTenantId(orderInfo.getTenantId());
                    cardLog.setUserId(loginInfo.getId());
                    cardLog.setLogDesc(loginInfo.getName()+"???????????????["+oilCardNum+"]");
                    iOilCardLogService.saveOrdUpdate(cardLog, EnumConsts.OIL_LOG_TYPE.USE);
                }

                //???????????? ?????????????????????????????????
                if (orderInfo.getOrderType() != null
                        && orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
                    if (orderInfo.getIsTransit() != null
                            && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES
                            && orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {// ?????????
                        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// ???????????????
                            // TODO ??????????????? loginInfo.getid
                            oilCardManagementService.modifyOilCardBalance(oilCardNum, balance, true,
                                    scheduler.getPlateNumber(),scheduler.getCarDriverMan(),
                                    scheduler.getOrderId(),loginInfo.getUserInfoId(),
                                    orderInfo.getToTenantId(),false,loginInfo);
                        }
                    }
                }
            }
            //????????????????????????
            oilcard = oilCardNum;
            //????????????????????????
            OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
            orderOilCardInfo.setCreateTime(getDateToLocalDateTime(new Date()));
            orderOilCardInfo.setOilCardNum(oilCardNum);
            orderOilCardInfo.setOilFee(balance);
            orderOilCardInfo.setCardType(cardType);
            orderOilCardInfo.setOrderId(orderInfo.getOrderId());
            orderOilCardInfo.setTenantId(orderInfo.getTenantId());
            iOrderOilCardInfoService.saveOrUpdate(orderOilCardInfo);
        }

        if(isLastAudit) {
            if (oilFee != 0) {
                throw new BusinessException("????????????????????????!");
            }
            //???????????????????????????
            CarLastOil lastOil = iCarLastOilService.getCarLastOilByPlateNumber(scheduler.getPlateNumber(),
                    scheduler.getTenantId());
            if (lastOil != null) {
                lastOil.setOilCarNum(oilcard);
                iCarLastOilService.saveOrUpdate(lastOil);
            } else {
                CarLastOil carLastOil = new CarLastOil();
                carLastOil.setOilCarNum(oilcard);
                carLastOil.setTenantId(scheduler.getTenantId());
                carLastOil.setPlateNumber(scheduler.getPlateNumber());
                carLastOil.setCreateTime(getDateToLocalDateTime(new Date()));
                iCarLastOilService.saveOrUpdate(carLastOil);
            }
        }

        //?????????????????????????????????
        if(oilCardInfosOld!=null) {
            for (OrderOilCardInfo orderOilCardInfo : oilCardInfosOld) {
                iOrderOilCardInfoService.deleteOrderOilCardInfo(orderOilCardInfo.getId());
            }
        }

    }

    @Override
    public String payArriveFee(Long orderId,String accessToken) {
        if (orderId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        // todo
        boolean isLock = true;
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payArriveFee" + orderId, 3, 5);
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("???????????????["+orderId+"]??????");
        }
        String msg = "Y";
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (scheduler.getCarDriverId() == null || scheduler.getCarDriverId() <= 0) {
            throw new BusinessException("?????????????????????,???????????????");
        }
        if (scheduler.getVehicleClass() != null &&
                !((scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT
                )
                        ||	( scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                )) {
            throw new BusinessException("??????????????????,???????????????");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("???????????????????????????,???????????????");
        }
        if (orderInfo.getOrderState() == SysStaticDataEnum.ORDER_STATE.CANCELLED) {
            throw new BusinessException("??????????????????????????????");
        }
        if (orderInfoExt.getPreAmountFlag() != OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("?????????????????????????????????????????????");
        }
        if (orderFee.getArrivePaymentState() != null && orderFee.getArrivePaymentState() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (scheduler.getVehicleClass() != null && scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() <= 0)) {
            throw new BusinessException("???????????????????????????????????????");
        }
        List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),
                orderFee.getTenantId());
        List<OrderProblemInfo> problemInfos = new ArrayList<>();
        for (OrderProblemInfo Info : list) {
            if (Info.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                    && Info.getState() == SysStaticDataEnum.EXPENSE_STATE.END && Info.getProblemDealPrice() < 0) {
                problemInfos.add(Info);
            }
        }
        List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(orderInfo.getOrderId());
        List<OrderAgingInfo> agingInfosOut = new ArrayList<>();
        if (agingInfos != null && agingInfos.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : agingInfos) {
                if (orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {

                    OrderAgingAppealInfo appealInfo = iOrderAgingAppealInfoService.getAppealInfoBYAgingId(orderAgingInfo.getId(),
                            false);
                    if (appealInfo != null) {
                        if (appealInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                                || appealInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
                            continue;
                        }
                    }
                    agingInfosOut.add(orderAgingInfo);
                }
            }
        }
        PayArriveChargeVo in = new PayArriveChargeVo();
        Long userId = 0L;
        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// ???????????????
            // ?????????
            SysTenantDef tenantDef = iSysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(),true);
            if (tenantDef == null) {
                log.error("????????????????????????????????????????????????ID["+orderInfo.getToTenantId()+"]");
                throw new BusinessException("??????????????????????????????????????????");
            }
            userId = tenantDef.getAdminUser();
        }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
            userId = scheduler.getCollectionUserId();
        } else {// ??????????????? ???C???
            userId = scheduler.getCarDriverId();
        }
        in.setUserId(userId);
        in.setProblemInfos(problemInfos);
        in.setAgingInfos(agingInfosOut);
        in.setArriveFee(orderFee.getArrivePaymentFee());
        in.setOrderId(orderId);
        in.setTenantId(orderFee.getTenantId());
        this.payArriveCharge(in,accessToken);
        orderFee.setArrivePaymentState(OrderConsts.AMOUNT_FLAG.ALREADY_PAY);
        orderFee.setArrivePaymentTime(getDateToLocalDateTime(new Date()));
        // ??????????????????
        this.synPayCenterUpdateOrderOrProblemInfo(orderInfo, scheduler);
        iOrderFeeService.saveOrUpdate(orderFee);
        return msg;
    }

    @Override
    public void payArriveCharge(PayArriveChargeVo in,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (in == null) {
            throw new BusinessException("?????????????????????");
        }
        Long userId = in.getUserId();
        Long orderId = in.getOrderId();
        Long arriveFee = in.getArriveFee();
        Long tenantId = in.getTenantId();
        List<OrderProblemInfo> problemInfos = in.getProblemInfos();
        List<OrderAgingInfo> agingInfos = in.getAgingInfos();
        log.info("?????????????????????:userId=" + userId + "orderId???" + orderId + " arriveFee???" + arriveFee  + " tenantId???" +tenantId);
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (orderId == null || orderId <= 0L) {
            throw new BusinessException("??????????????????");
        }
        if (arriveFee == null || arriveFee < 0L) {
            throw new BusinessException("????????????????????????");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("???????????????id");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payArriveCharge" + orderId, 3, 5);
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        // ????????????id?????????????????????id
        SysTenantDef tenantSysTenantDef = iSysTenantDefService.getSysTenantDef(tenantId);
        if (tenantSysTenantDef == null) {
            throw new BusinessException("????????????id" + tenantId + "????????????????????????");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserId(tenantSysTenantDef.getAdminUser());
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????id???"+ tenantSysTenantDef.getAdminUser() + "????????????????????????!");
        }
        // ????????????id??????????????????
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("????????????id???"+ userId + "????????????????????????!");
        }
        //????????????????????????
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        // ????????????ID??????????????????????????????
        OrderLimit ol = iOrderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("??????????????????" + orderId + " ??????ID???" + userId + " ???????????????????????????");
        }
        String vehicleAffiliation = ol.getVehicleAffiliation();
        String oilAffiliation = ol.getOilAffiliation();
        OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,ol.getUserType());

        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel arriveChargeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB, arriveFee.longValue());
        busiList.add(arriveChargeSubjectsRel);
        BusiSubjectsRel exceptinFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_EXCEPTION, 0L);
        busiList.add(exceptinFeeSubjectsRel);
        BusiSubjectsRel agingFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION, 0L);
        busiList.add(agingFeeSubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_ARRIVE_CHARGE,busiList);
        //???????????????????????????????????????????????????
        long exceptionFeeSum = 0L;
        long agingFeeSum = 0L;
        long tempArriveFee = arriveFee;
        List<Long> problems = new ArrayList<Long>();
        if (problemInfos != null && problemInfos.size() > 0 && tempArriveFee > 0) {
            for (OrderProblemInfo opi : problemInfos) {
                if (tempArriveFee <= 0) {
                    break;
                }
                if (opi.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                        && opi.getState() == SysStaticDataEnum.EXPENSE_STATE.END && opi.getProblemDealPrice() < 0) {
                    Long problemDealPrice = (opi.getProblemDealPrice() == null ? 0 : opi.getProblemDealPrice());
                    Long deductionFee = (opi.getDeductionFee() == null ? 0 : opi.getDeductionFee());
                    Long tempDeductionFee = Math.abs(problemDealPrice) - Math.abs(deductionFee);
                    if (tempDeductionFee <= 0) {
                        continue;
                    }
                    if (tempArriveFee > Math.abs(tempDeductionFee)) {
                        opi.setDeductionFee(deductionFee + (-tempDeductionFee));
                        opi.setArriveDeductionFee((opi.getArriveDeductionFee() == null ? 0L : opi.getArriveDeductionFee()) + (-tempDeductionFee));
                        orderProblemInfoService.saveOrUpdate(opi);
                        problems.add(-tempDeductionFee);
                        exceptionFeeSum += (-tempDeductionFee);
                        tempArriveFee -= Math.abs(tempDeductionFee);
                    } else {
                        opi.setDeductionFee(deductionFee + (-tempArriveFee));
                        opi.setArriveDeductionFee((opi.getArriveDeductionFee() == null ? 0L : opi.getArriveDeductionFee()) + (-tempArriveFee));
                        orderProblemInfoService.saveOrUpdate(opi);
                        problems.add(-tempArriveFee);
                        exceptionFeeSum += (-tempArriveFee);
                        tempArriveFee = 0L;
                    }
                }
            }
            if (problems.size() > 0) {
                BusiSubjectsRel bus = null;
                Iterator<BusiSubjectsRel> iterator = busiSubjectsRelList.iterator();
                while(iterator.hasNext()){
                    BusiSubjectsRel bsr = iterator.next();
                    if (bsr.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_EXCEPTION) {
                        bus = bsr;
                        iterator.remove();
                        break;
                    }

                }
                for (Long exceptinFee : problems) {
                    if (bus != null) {
                        BusiSubjectsRel exceptinFeeRel = new BusiSubjectsRel();
                        BeanUtils.copyProperties(exceptinFeeRel, bus);
                        exceptinFeeRel.setAmountFee(exceptinFee);
                        busiSubjectsRelList.add(exceptinFeeRel);
                    }
                }
            }
        }
        if (agingInfos != null && agingInfos.size() > 0 && tempArriveFee > 0) {
            for (OrderAgingInfo oai : agingInfos) {
                if (tempArriveFee <= 0) {
                    break;
                }
                if (oai.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                    Long finePrice = oai.getFinePrice() == null ? 0 : oai.getFinePrice();
                    Long deductionFee = (oai.getDeductionFee() == null ? 0 : oai.getDeductionFee());
                    Long tempDeductionFee = Math.abs(finePrice) - Math.abs(deductionFee);
                    if (tempDeductionFee <= 0) {
                        continue;
                    }
                    if (tempArriveFee > Math.abs(tempDeductionFee)) {
                        oai.setDeductionFee(deductionFee + tempDeductionFee);
                        oai.setArriveDeductionFee((oai.getArriveDeductionFee() == null ? 0L : oai.getArriveDeductionFee()) + tempDeductionFee);
                        iOrderAgingInfoService.saveOrUpdate(oai);
                        agingFeeSum += tempDeductionFee;
                        tempArriveFee -= tempDeductionFee;
                    } else {
                        oai.setDeductionFee(deductionFee + tempArriveFee);
                        oai.setArriveDeductionFee((oai.getArriveDeductionFee() == null ? 0L : oai.getArriveDeductionFee()) + tempArriveFee);
                        iOrderAgingInfoService.saveOrUpdate(oai);
                        agingFeeSum += tempArriveFee;
                        tempArriveFee = 0L;
                    }
                }
            }
            for (BusiSubjectsRel bsr : busiSubjectsRelList) {
                if (bsr.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION) {
                    bsr.setAmountFee(agingFeeSum);
                    break;
                }
            }
        }
        // ????????????????????????????????????????????????
        long soNbr = CommonUtil.createSoNbr();
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_ARRIVE_CHARGE,
                tenantSysTenantDef.getAdminUser(), tenantSysOperator.getName(),account, busiSubjectsRelList, soNbr, orderId,
                null, null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

        //??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = null;
        if (tempArriveFee > 0) {
            //???????????? ????????? 20190717  ?????????????????????????????????
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation),
                    String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation),
                        tempArriveFee, 0L, 0L, tempArriveFee, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            //??????????????????
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantSysTenantDef.getAdminUser(), vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB, tempArriveFee);
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.
                        SubjectIds.PAYABLE_IN_SERVICE_FEE_4006, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }

            // ??????????????????
            fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_ARRIVE_CHARGE, fleetBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_ARRIVE_CHARGE,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null,
                    vehicleAffiliation,loginInfo);
            //??????????????????
            boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
            Integer isAutomatic = null;
            if (isAutoTransfer) {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
            } else {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
            }
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, tempArriveFee, -1L, vehicleAffiliation, orderId,
                    tenantId, isAutomatic, isAutomatic, tenantSysTenantDef.getAdminUser(), OrderAccountConst.PAY_TYPE.TENANT,
                    EnumConsts.PayInter.PAY_ARRIVE_CHARGE, EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB,
                    oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,ol.getUserType(),serviceFee,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("??????(???????????????)");
            if (isTenant) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                payoutIntf.setTenantId(sysTenantDef.getId());
            }
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
            }
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            payoutIntf.setBusiCode(String.valueOf(orderId));
            if (orderScheduler != null) {
                payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
            }
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                //??????payout_order
                iPayoutOrderService.createPayoutOrder(userId, tempArriveFee, OrderAccountConst.FEE_TYPE.CASH_TYPE,
                        payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }


        // ?????????????????????????????????????????????
        ParametersNewDto inParam = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.PAY_ARRIVE_CHARGE, orderId, arriveFee, vehicleAffiliation, "");
        inParam.setTenantBillId(String.valueOf(tenantId));
        inParam.setTenantUserId(tenantSysTenantDef.getAdminUser());
        inParam.setTenantBillId(tenantSysOperator.getBillId());
        inParam.setTenantUserName(tenantSysTenantDef.getName());
        inParam.setBatchId(Long.toString(soNbr));
        inParam.setOrderLimitBase(ol);
        if (fleetSubjectsRelList != null) {
            busiSubjectsRelList.addAll(fleetSubjectsRelList);
        }
        iOrderOilSourceService.busiToOrderNew(inParam, busiSubjectsRelList,loginInfo);

        // ???????????????????????????
        String remark = "??????????????????" + "????????????" + orderId + " ?????????:" + arriveFee + "??????????????????:" + exceptionFeeSum + "??????????????????:" + agingFeeSum;
//        saveSysOperLog(SysOperLogConst.BusiCode.PayForFinalCharge, EnumConsts.PayInter.PAY_ARRIVE_CHARGE, SysOperLogConst.OperType.Add,loginInfo.getName() + remark);
        saveSysOperLog(SysOperLogConst.BusiCode.PayForFinalCharge, SysOperLogConst.OperType.Add,loginInfo.getName() + remark,accessToken,orderId);

    }

    @Override
    public void payAdvanceCharge(AdvanceChargeVo advanceChargeIn,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        long userId = advanceChargeIn.getUserId();
        String vehicleAffiliation = advanceChargeIn.getVehicleAffiliation();
        long amountFee = advanceChargeIn.getAmountFee();
        long virtualOilFee = advanceChargeIn.getVirtualOilFee();
        long entityOilFee = advanceChargeIn.getEntityOilFee();
        long ETCFee = advanceChargeIn.getETCFee();
        long orderId = advanceChargeIn.getOrderId();
        Long tenantId = advanceChargeIn.getTenantId();
        long totalFee = advanceChargeIn.getTotalFee();
        int isNeedBill = advanceChargeIn.getIsNeedBill();
        int payType = advanceChargeIn.getPayType();
        //int oilUserType = advanceChargeIn.getOilUserType();
        String oilAffiliation = advanceChargeIn.getOilAffiliation();
        Integer oilConsumer = advanceChargeIn.getOilConsumer();
        Long collectionDriverUserId = advanceChargeIn.getCollectionDriverUserId();
        Integer oilAccountType = advanceChargeIn.getOilAccountType();
        Integer oilBillType = advanceChargeIn.getOilBillType();

        log.info("?????????????????????:userId=" + userId + " vehicleAffiliation=" + vehicleAffiliation + " amountFee=" + amountFee
                + " virtualOilFee" + virtualOilFee + " oilFee=" + entityOilFee + " ETCFee=" + ETCFee + " ?????????="+ orderId+ " ???????????????="+ oilAccountType + " ???????????????="+ oilBillType);
        if (userId < 0) {
            throw new BusinessException("?????????????????????");
        }
        if (tenantId == null || tenantId <= -1L) {
            throw new BusinessException("???????????????id");
        }
        if (payType <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("???????????????????????????");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("??????????????????????????????");
        }
        if (oilConsumer == null || oilConsumer < OrderConsts.OIL_CONSUMER.SELF) {
            throw new BusinessException("?????????????????????????????????");
        }
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
        if (oilBillType == null || oilBillType < OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {
            throw new BusinessException("?????????????????????????????????");
        }
        Date date = new Date();
        // ????????????id?????????????????????id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        // TODO: 2022/3/28
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payAdvanceCharge" + orderId, 3, 5);
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        // ????????????id??????????????????
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????????????????
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        OrderOilSource thisSourceList = null;//??????????????????
        List<OrderOilSource> sourceList = null;
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
        }
        if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
            isOwnCarUser = true;
        }
        OrderLimit limit = iOrderLimitService.getOrderLimit(userId, orderId, tenantId,-1,loginInfo);
        if (limit == null) {
            throw new BusinessException("??????????????????" + orderId + "??????ID???" + userId + "??????id???" + tenantId + "???????????????????????????");
        }
        long soNbr = CommonUtil.createSoNbr();
        long tempVirtualOilFee = virtualOilFee;
        //??????????????????
        if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.ORDINARY_PAY) {
            //??????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????
            if (virtualOilFee > 0 && isOwnCarUser && oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                OrderAccountBalanceDto oilBalance = iOrderAccountService.getOrderAccountBalance(tenantUserId, "oilBalance", tenantId, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                OrderAccountOutVo orderAccountOut =  oilBalance.getOa();
                if(null == orderAccountOut){
                    throw new BusinessException("???????????????????????????");
                }
                //???????????????????????????
                Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
                if(null == canUseOilBalance){
                    canUseOilBalance = 0L;
                }
                if (canUseOilBalance.longValue() > 0) {
                    if (virtualOilFee > canUseOilBalance.longValue()) {
                        sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                        if (sourceList == null || sourceList.size() <= 0) {
                            throw new BusinessException("???????????????????????????");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceList) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != canUseOilBalance) {
                                throw new BusinessException("??????????????????????????????????????????");
                            }
                        }
                        tempVirtualOilFee -= canUseOilBalance;
                    } else {
                        sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(virtualOilFee, tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                        if (sourceList == null || sourceList.size() <= 0) {
                            throw new BusinessException("???????????????????????????");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceList) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != virtualOilFee) {
                                throw new BusinessException("??????????????????????????????????????????");
                            }
                        }
                        tempVirtualOilFee = 0L;
                    }
                }
            } else {
                OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(userId, orderId, orderId, virtualOilFee, virtualOilFee, 0L, tenantId,getDateToLocalDateTime(date),loginInfo.getId(), isNeedBill, vehicleAffiliation,limit.getOrderDate(),oilAffiliation,oilConsumer,
                        0L,0L,0L,0L,0L,0L,limit.getUserType(),oilAccountType,oilBillType,loginInfo);
                thisSourceList = oos;
            }
        } else if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_TEMPORARY_FLEET) {//?????????????????????
            OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(userId, orderId, orderId, virtualOilFee, virtualOilFee, 0L, tenantId,getDateToLocalDateTime(date), loginInfo.getId(), isNeedBill, vehicleAffiliation,limit.getOrderDate(),oilAffiliation,oilConsumer,
                    0L,0L,0L,0L,0L,0L,limit.getUserType(),oilAccountType,oilBillType,loginInfo);
            thisSourceList = oos;
        } else if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.TEMPORARY_FLEET_PAY_DRIVER) {//???????????????????????????
            Long sourceOrderId = limit.getSourceOrderId();
            if (sourceOrderId == null || sourceOrderId <= 0) {
                throw new BusinessException("????????????????????????!");
            }
            OrderLimit sourceOrderlimit = iOrderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, sourceOrderId,-1);
            if (sourceOrderlimit == null) {
                throw new BusinessException("??????????????????" + sourceOrderId + "??????ID???" + tenantUserId + "???????????????????????????");
            }
            sourceOrderlimit.setPaidOil(entityOilFee);
            iOrderLimitService.saveOrUpdate(limit);
            String sourceVehicleAffiliation = sourceOrderlimit.getVehicleAffiliation();
            String sourceOilAffiliation = sourceOrderlimit.getVehicleAffiliation();
            Long sourceTenantId = sourceOrderlimit.getTenantId();
            Integer sourceIsNeedBill = sourceOrderlimit.getIsNeedBill();
            OrderOilSource orderOilSource = iOrderOilSourceService.getOrderOilSource(tenantUserId, sourceOrderId, sourceOrderId, sourceVehicleAffiliation, sourceTenantId,-1,sourceOrderlimit.getOilAccountType(),sourceOrderlimit.getOilConsumer(),sourceOrderlimit.getOilBillType(),sourceOilAffiliation);
            if (orderOilSource == null) {
                throw new BusinessException("??????????????????" + sourceOrderId + "??????ID???" + tenantUserId + "??????ID???" + sourceTenantId + "???????????????" + sourceVehicleAffiliation + " ??????????????????" + sourceOrderlimit.getOilAccountType() + " ??????????????????" + sourceOrderlimit.getOilConsumer() + " ??????????????????" + sourceOrderlimit.getOilBillType() + " ??????????????????????????????");
            }
            long noPayOil = (orderOilSource.getNoPayOil() == null ? 0L : orderOilSource.getNoPayOil());
            long entityOil = (sourceOrderlimit.getOrderEntityOil() == null ? 0L : sourceOrderlimit.getOrderEntityOil());
            if ((noPayOil != virtualOilFee ) || ( entityOil != entityOilFee)) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
            OrderAccount sourceOilAccount = iOrderAccountService.queryOrderAccount(tenantUserId, sourceVehicleAffiliation, 0L, sourceTenantId,sourceOilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            //??????OrderOilSource
            List<OrderOilSource> orderOilSourceList = new ArrayList<>();
            orderOilSourceList.add(orderOilSource);
            MatchAmountUtil.matchAmounts(virtualOilFee, 0, 0, "noPayOil",
                    "noRebateOil","noCreditOil", orderOilSourceList);
            sourceList = iOrderAccountService.dealTemporaryFleetOil(sourceOilAccount, orderOilSource, tenantUserId, orderId, tenantId, sourceIsNeedBill, sourceVehicleAffiliation, userId, EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,accessToken);
        } else if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER) {//????????????????????????????????????
            if (orderScheduler.getIsCollection() == null || orderScheduler.getIsCollection() != OrderConsts.IS_COLLECTION.YES) {
                throw new BusinessException("?????????????????????????????????????????????????????????");
            }
            if (collectionDriverUserId == null || collectionDriverUserId < 1) {
                throw new BusinessException("????????????????????????id????????????");
            }
            SysUser sysCollectionDriverOperator = iSysOperatorService.getSysOperatorByUserId(collectionDriverUserId);
            if (sysCollectionDriverOperator == null) {
                throw new BusinessException("????????????id" + collectionDriverUserId + "????????????????????????!");
            }
            OrderLimit limitDriver = iOrderLimitService.getOrderLimit(collectionDriverUserId, orderId, tenantId,-1,loginInfo);
            if (limitDriver == null) {
                throw new BusinessException("??????????????????" + orderId + "??????ID???" + collectionDriverUserId + "??????id???" + tenantId + "???????????????????????????");
            }
            OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(collectionDriverUserId, orderId, orderId, virtualOilFee, virtualOilFee, 0L, tenantId,getDateToLocalDateTime(date), loginInfo.getId(), isNeedBill, vehicleAffiliation,limitDriver.getOrderDate(),oilAffiliation,oilConsumer,
                    0L,0L,0L,0L,0L,0L,limitDriver.getUserType(),oilAccountType,oilBillType,loginInfo);
            thisSourceList = oos;
            OrderAccount collectionDriverAccount = iOrderAccountService.queryOrderAccount(collectionDriverUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,limitDriver.getUserType());
            List<BusiSubjectsRel> collectionDriverBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel OilFeeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_Entity_SUB, entityOilFee);
            collectionDriverBusiList.add(OilFeeSubjectsRelEntity);
            BusiSubjectsRel consumeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB, entityOilFee);
            collectionDriverBusiList.add(consumeSubjectsRelEntity);
            BusiSubjectsRel OilFeeSubjectsRelVirtual = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_Virtual_SUB, virtualOilFee);
            collectionDriverBusiList.add(OilFeeSubjectsRelVirtual);

            // ??????????????????
            List<BusiSubjectsRel> collectionDriverRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, collectionDriverBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), collectionDriverAccount, collectionDriverRelList, soNbr, orderId,
                    sysCollectionDriverOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(collectionDriverUserId, sysCollectionDriverOperator.getBillId(), EnumConsts.PayInter.BEFORE_PAY_CODE, orderId, amountFee + virtualOilFee + entityOilFee + ETCFee, vehicleAffiliation, "");
            iOrderOilSourceService.busiToOrder(parametersNewDto, collectionDriverRelList,loginInfo);
        }

        OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,limit.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();

        //????????????
        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, amountFee);
        busiList.add(amountFeeSubjectsRel);
        //???????????? ????????? 20190717 ?????????????????????????????????
        long serviceFee = 0;
        boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
        if (isLuge) {
            Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), amountFee, 0L, 0L, amountFee, tenantId,null);
            serviceFee = (Long) result.get("lugeBillServiceFee");
        }
        if (payType != OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER) {//????????????????????????????????????
            BusiSubjectsRel OilFeeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_Entity_SUB, entityOilFee);
            busiList.add(OilFeeSubjectsRelEntity);
            BusiSubjectsRel consumeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB, entityOilFee);
            busiList.add(consumeSubjectsRelEntity);
            BusiSubjectsRel OilFeeSubjectsRelVirtual = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_Virtual_SUB, virtualOilFee);
            busiList.add(OilFeeSubjectsRelVirtual);
        }
        BusiSubjectsRel amountETCFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_ETC_SUB, ETCFee);
        busiList.add(amountETCFeeSubjectsRel);
        //?????????????????????????????????
        if (virtualOilFee > 0 && oilConsumer == OrderConsts.OIL_CONSUMER.SHARE ) {
            Integer distributionType = null;
            long tempUserId = userId;
            if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER) {
                tempUserId = collectionDriverUserId;
            }
            if (isOwnCarUser) {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                    if (tempVirtualOilFee > 0) {//???????????????????????????
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                        OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(userId, orderId, orderId, tempVirtualOilFee, tempVirtualOilFee, 0L, tenantId,getDateToLocalDateTime(date), loginInfo.getId(), isNeedBill, vehicleAffiliation,limit.getOrderDate(),oilAffiliation,oilConsumer,
                                0L,0L,0L,0L,0L,0L,limit.getUserType(),oilAccountType,oilBillType,loginInfo);
                        thisSourceList = oos;
                    }
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        throw new BusinessException("???????????????????????????????????????");
                    }
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                }
            } else {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {//?????????????????????
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//????????????
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                    } else if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//??????????????????
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2;
                    }
                }
            }
            if (tempVirtualOilFee > 0 && distributionType != null) {
                Map<String, Object> map = iOilRechargeAccountService.distributionOil(tempUserId, tenantUserId, tempVirtualOilFee, String.valueOf(orderId), EnumConsts.SubjectIds.BEFORE_Virtual_SUB, tenantId, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1,distributionType,loginInfo);
                Long noPayOil = (Long) map.get("noPayOil");
                Long noRebateOil = (Long) map.get("noRebateOil");
                Long noCreditOil = (Long) map.get("noCreditOil");
                if (thisSourceList == null) {
                    throw new BusinessException("???????????????????????????");
                }
                if (noPayOil == null) {
                    throw new BusinessException("????????????????????????");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("??????????????????");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("??????????????????");
                }
                thisSourceList.setSourceAmount(noPayOil);
                thisSourceList.setNoPayOil(noPayOil);
                thisSourceList.setCreditOil(noCreditOil);
                thisSourceList.setNoCreditOil(noCreditOil);
                thisSourceList.setRebateOil(noRebateOil);
                thisSourceList.setNoRebateOil(noRebateOil);
                iOrderOilSourceService.saveOrUpdate(thisSourceList);
            }
        }

        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList);
        // ????????????????????????????????????????????????
        OrderResponseDto param = new OrderResponseDto();
        param.setSourceList(sourceList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,loginInfo);

        //??????????????????
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB, amountFee);
        fleetBusiList.add(payableOverdueRel);
        if (serviceFee > 0) {
            BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4000, serviceFee);
            fleetBusiList.add(payableServiceFeeSubjectsRel);
        }
        //56?????????????????? 20191014
        long appendFreight = 0;
        boolean is56k = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K));
        if (is56k) {
            AdditionalFee af = this.deal56kAppendFreight(advanceChargeIn, tenantId,accessToken);
            if (af != null) {
                appendFreight = af.getAppendFreight() == null ? 0L : af.getAppendFreight();
            }
            //??????????????????
            if (oilBillType == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2 && virtualOilFee > 0) {
                appendFreight = 0;
            }
            if (appendFreight > 0) {
                BusiSubjectsRel appendFreightSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_ADDITIONAL_FEE_4019, appendFreight);
                fleetBusiList.add(appendFreightSubjectsRel);
                iAdditionalFeeService.saveOrUpdate(af);
            }
        }
        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

        //???????????????????????????
        //??????????????????
        boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        if (amountFee > 0 || appendFreight > 0) {
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, amountFee, -1L, vehicleAffiliation, orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,limit.getUserType(),serviceFee,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("??????(???????????????)");
            payoutIntf.setAppendFreight(appendFreight);
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
            }
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                //??????payout_order
                iPayoutOrderService.createPayoutOrder(userId, amountFee, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }
        //??????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????HA??????????????????????????????=?????????????????? ???+ ??????(??????+??????) + ????????????|ETC???+ ????????????????????????HA??????????????????????????????????????????
        if (isOwnCarUser && isNeedBill == OrderAccountConst.ORDER_BILL_TYPE.platformBill) {
            if (totalFee > 0) {
                this.payOwnCarBillAmount(orderId, totalFee, userId, tenantId, soNbr,accessToken);
            }
        }
        // ?????????????????????????????????????????????
        ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(userId, sysOperator.getBillId(),EnumConsts.PayInter.BEFORE_PAY_CODE, orderId, amountFee + virtualOilFee + entityOilFee + ETCFee,vehicleAffiliation, "");
        parametersNewDto.setTotalFee(String.valueOf(totalFee));
        parametersNewDto.setTenantUserId(tenantUserId);
        parametersNewDto.setTenantBillId(tenantSysOperator.getBillId());
        parametersNewDto.setTenantUserName(tenantUser.getLinkman());
        busiSubjectsRelList.addAll(fleetSubjectsRelList);
        iOrderOilSourceService.busiToOrder(parametersNewDto, busiSubjectsRelList,loginInfo);
    }

    @Override
    public AdditionalFee deal56kAppendFreight(AdvanceChargeVo advanceChargeIn, Long tenantId,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (advanceChargeIn ==  null) {
            throw new BusinessException("???????????????");
        }
        Long orderId = advanceChargeIn.getOrderId();
        String vehicleAffiliation = advanceChargeIn.getVehicleAffiliation();
        boolean is56k = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K));
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderGoods orderGoods = iOrderGoodsService.getOrderGoods(orderId);
        if (orderInfo == null) {
            throw new BusinessException("??????????????????" + orderId + "?????????????????????");
        }
        Long appendFreight = orderFeeExt.getAppendFreight() == null ? 0L :  orderFeeExt.getAppendFreight() ;
        AdditionalFee af = null;
        if (is56k && appendFreight > 0) {
            af = new AdditionalFee();
            af.setVehicleAffiliation(vehicleAffiliation);
            af.setAppendFreight(appendFreight);
            af.setDependTime(orderScheduler.getDependTime());
            af.setDesRegion(orderInfo.getDesRegion());
            af.setOrderId(orderScheduler.getOrderId());
            af.setVehicleCode(orderScheduler.getVehicleCode());
            af.setPlateNumber(orderScheduler.getPlateNumber());
            af.setSourceRegion(orderInfo.getSourceRegion());
            af.setTenantId(orderInfo.getTenantId());
            af.setVehicleClass(orderScheduler.getVehicleClass());
            af.setVehicleLengh(orderScheduler.getCarLengh());
            af.setVehicleStatus(orderScheduler.getCarStatus());
            af.setCarUserId(orderScheduler.getCarDriverId());
            af.setCarDriverMan(orderScheduler.getCarDriverMan());
            af.setCarDriverPhone(orderScheduler.getCarDriverPhone());
            af.setCustomName(orderGoods.getCustomName());
            af.setSourceName(orderScheduler.getSourceName());
            af.setTenantId(tenantId);
            af.setOpId(loginInfo.getId());
            af.setCreateTime(LocalDateTime.now());
            af.setUpdateTime(LocalDateTime.now());
            af.setUpdateOpId(loginInfo.getId());
            af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE1);
            af.setDealState(OrderAccountConst.ADDITIONAL_FEE_DEAL_STATE.STATE0);
            //?????????????????????????????????
            Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
            AccountBankRel bank = iAccountBankRelService.getDefaultAccountBankRel(tenantUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            if (bank == null) {
                throw new BusinessException("????????????id???" + tenantUserId +  "??????????????? " + SysStaticDataEnum.USER_TYPE.ADMIN_USER + " ????????????????????????????????????");
            }
            af.setReceivablesBankAccName(bank.getAcctName());
            af.setReceivablesBankAccNo(bank.getAcctNo());
        }
        return af;
    }

    @Override
    public void payOwnCarBillAmount(Long orderId, Long totalFee, Long userId, Long tenantId, long soNbr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // ????????????id?????????????????????id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        SysTenantDef tenantSysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(tenantUserId);
        if (tenantSysTenantDef == null) {
            throw new BusinessException("????????????id???" + tenantUserId + " ?????????????????????");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????????????????
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        //??????????????????
        boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(tenantId);
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        String vehicleAffiliation = orderFee.getVehicleAffiliation(); //??????????????????
        String oilAffiliation = vehicleAffiliation;
        //??????????????????????????????????????????
        BillPlatform bpf = billPlatformService.queryBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
        if (bpf == null) {
            throw new BusinessException("?????????????????????" + vehicleAffiliation + " ???????????????????????????");
        }
        int receivables = bpf.getPayAcctType();
        SysCfg sysCfg = readisUtil.getSysCfg("OWN_CAR_OPEN_BILL", "0");
        if (sysCfg != null) {
            String tenantIds = sysCfg.getCfgValue();
            String[] tenantIdsArr = null;
            if (StringUtils.isNotBlank(tenantIds)) {
                tenantIdsArr = tenantIds.trim().split("\\,");
                if (Arrays.asList(tenantIdsArr).contains(String.valueOf(tenantId))) {
                    receivables = 1;
                }
            }
        }
        if (receivables == 1) {//????????????
            //??????????????????
            List<BusiSubjectsRel> subjectsList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel receiveSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE, totalFee);
            subjectsList.add(receiveSubjectsRel);
            BusiSubjectsRel paySubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_PAY_TOTAL_FEE, totalFee);
            subjectsList.add(paySubjectsRel);
            //???????????? ????????? 20190717
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), totalFee, 0L, 0L, totalFee, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
                if (serviceFee > 0) {
                    BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4000, serviceFee);
                    subjectsList.add(payableServiceFeeSubjectsRel);
                }
            }
            List<BusiSubjectsRel> haRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, subjectsList);
            //accountDetailsTF.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE, tenantUserId, tenantUserId, tenantSysOperator.getOperatorName(), haRelList, soNbr, orderId, tenantId, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            // 20190727 ?????????????????????????????????????????????????????????????????????
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), fleetAccount, haRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            //??????????????????
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.HAVIR, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, totalFee, tenantId, String.valueOf(vehicleAffiliation), orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE,String.valueOf(oilAffiliation),SysStaticDataEnum.USER_TYPE.ADMIN_USER,SysStaticDataEnum.USER_TYPE.ADMIN_USER,serviceFee,accessToken);
            payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
            payoutIntf.setRemark("????????????");
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            payoutIntf.setBusiCode(String.valueOf(orderId));
            if (orderScheduler != null) {
                payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
            }
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            //?????????
            List<PayoutOrder> payoutOrderList = new ArrayList<PayoutOrder>();
            //??????+??????
            PayoutOrder payoutOrder = iPayoutOrderService.createPayoutOrder(tenantUserId, totalFee, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            payoutOrderList.add(payoutOrder);
            long payoutOrderTotalFee = 0;
            for (PayoutOrder p : payoutOrderList) {
                payoutOrderTotalFee += (p.getAmount() == null ? 0L : p.getAmount());
            }
            if (payoutOrderTotalFee != totalFee) {
                throw new BusinessException("??????????????????????????????????????????");
            }
        } else if (receivables == 2) {//????????????
            //????????????
            OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel receivableSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE, totalFee);
            busiList.add(receivableSubjectsRel);
            //???????????? ????????? 20190717
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), totalFee, 0L, 0L, totalFee, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            // ??????????????????
            List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList);
            // ????????????????????????????????????????????????
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

            //????????????
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_PAYABLE, totalFee);
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4000, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }
            // ??????????????????
            List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

            //????????????
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, totalFee, -1L, String.valueOf(vehicleAffiliation), orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE,String.valueOf(oilAffiliation),SysStaticDataEnum.USER_TYPE.ADMIN_USER,SysStaticDataEnum.USER_TYPE.DRIVER_USER,serviceFee,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("????????????");
            if (isTenant) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                payoutIntf.setTenantId(tenantId);
            }
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
            }
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            payoutIntf.setBusiCode(String.valueOf(orderId));
            if (orderScheduler != null) {
                payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
            }
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            List<PayoutOrder> payoutOrderList = new ArrayList<PayoutOrder>();
            PayoutOrder payoutOrder = iPayoutOrderService.createPayoutOrder(userId, totalFee, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            payoutOrderList.add(payoutOrder);
            long payoutOrderTotalFee = 0;
            for (PayoutOrder p : payoutOrderList) {
                payoutOrderTotalFee += (p.getAmount() == null ? 0L : p.getAmount());
            }
            if (payoutOrderTotalFee != totalFee) {
                throw new BusinessException("??????????????????????????????????????????");
            }
        } else {
            throw new BusinessException("?????????????????????????????????????????????????????????????????????");
        }

    }

    @Override
    public void payAdvanceChargeToOwnCar(AdvanceChargeVo advanceChargeIn, String accessToken) {
        Long masterUserId = advanceChargeIn.getMasterUserId();
        String vehicleAffiliation = advanceChargeIn.getVehicleAffiliation();
        long entiyOilFee = advanceChargeIn.getEntityOilFee();
        long fictitiousOilFee = advanceChargeIn.getFictitiousOilFee();
        long bridgeFee = advanceChargeIn.getBridgeFee();
        long masterSubsidy = advanceChargeIn.getMasterSubsidy();
        long slaveSubsidy = advanceChargeIn.getSlaveSubsidy();
        Long slaveUserId = advanceChargeIn.getSlaveUserId();
        long orderId = advanceChargeIn.getOrderId();
        Long tenantId = advanceChargeIn.getTenantId();
        long totalFee = advanceChargeIn.getTotalFee();
        int isNeedBill = advanceChargeIn.getIsNeedBill();
        String oilAffiliation = advanceChargeIn.getOilAffiliation();
        Integer oilConsumer = advanceChargeIn.getOilConsumer();
        Integer oilAccountType = advanceChargeIn.getOilAccountType();
        Integer oilBillType = advanceChargeIn.getOilBillType();

        LoginInfo loginInfo = loginUtils.get(accessToken);

        log.info("?????????????????????:mainUserId=" + masterUserId +  "entiyOilFee=" + entiyOilFee
                + "fictitiousOilFee=" + fictitiousOilFee + "bridgeFee=" + bridgeFee
                + "masterSubsidy=" + masterSubsidy + "slaveSubsidy=" + slaveSubsidy + "slaveUserId=" + slaveUserId + " ???????????????="+ oilAccountType + " ???????????????="+ oilBillType);
        Date date = new Date();
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("???????????????????????????");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("??????????????????????????????");
        }
        if (oilConsumer == null || oilConsumer < 1) {
            throw new BusinessException("?????????????????????????????????");
        }
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
        if (oilBillType == null || oilBillType < OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {
            throw new BusinessException("?????????????????????????????????");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[] { DateUtil.formatDate(date, DateUtil.YEAR_MONTH_FORMAT2) });
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payAdvanceChargeToOwnCar" + orderId, 3, 5);
        // TODO: 2022/3/28
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("????????????????????????????????????!");
        }
        // ????????????id?????????????????????id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        // ??????????????????????????????
        //UserDataInfo mainUser = userSV.getUserDataInfo(masterUserId);masterUserId
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(masterUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        OrderOilSource thisSourceList = null;//??????????????????
        List<OrderOilSource> sourceList = null;
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
        }
        if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
            isOwnCarUser = true;
        }
        OrderLimit limit = iOrderLimitService.getOrderLimit(masterUserId, orderId, tenantId,-1,loginInfo);
        if (limit == null) {
            throw new BusinessException("??????????????????" + orderId + "??????ID???" + masterUserId + "??????id???" + tenantId + "???????????????????????????");
        }
        long tempVirtualOilFee = fictitiousOilFee;
        //??????????????????????????????????????????????????????
        if (fictitiousOilFee > 0 && isOwnCarUser && oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
            OrderAccountBalanceDto oilBalance = iOrderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",tenantId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut =  oilBalance.getOa();;
            if(null == orderAccountOut){
                throw new BusinessException("???????????????????????????");
            }
            //???????????????????????????
            Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
            if(null == canUseOilBalance){
                canUseOilBalance = 0L;
            }
            if (canUseOilBalance.longValue() > 0) {
                if (fictitiousOilFee > canUseOilBalance.longValue()) {
                    sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, masterUserId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                    if (sourceList == null || sourceList.size() <= 0) {
                        throw new BusinessException("???????????????????????????");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceList) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != canUseOilBalance) {
                            throw new BusinessException("??????????????????????????????????????????");
                        }
                    }
                    tempVirtualOilFee -= canUseOilBalance;
                } else {
                    sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(fictitiousOilFee, tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, masterUserId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                    if (sourceList == null || sourceList.size() <= 0) {
                        throw new BusinessException("???????????????????????????");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceList) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != fictitiousOilFee) {
                            throw new BusinessException("??????????????????????????????????????????");
                        }
                    }
                    tempVirtualOilFee = 0L;
                }
            }
        } else {
            OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(masterUserId, orderId, orderId, fictitiousOilFee, fictitiousOilFee, 0L, tenantId,getDateToLocalDateTime(date), loginInfo.getId(), isNeedBill, vehicleAffiliation,limit.getOrderDate(),oilAffiliation,oilConsumer,
                    0L,0L,0L,0L,0L,0L,limit.getUserType(),oilAccountType,oilBillType,loginInfo);
            thisSourceList = oos;
        }
        // ????????????ID???????????????????????????????????????
        OrderAccount account = iOrderAccountService.queryOrderAccount(sysOperator.getUserInfoId(), vehicleAffiliation,0L, tenantId,oilAffiliation,limit.getUserType());
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        // ??????????????????

        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountEntiyOilFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_ENTIY_OIL_FEE, entiyOilFee);
        busiList.add(amountEntiyOilFeeSubjectsRel);
        BusiSubjectsRel consumeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CONSUME_BEFORE_ENTIY_OIL_FEE, entiyOilFee);
        busiList.add(consumeSubjectsRelEntity);
        BusiSubjectsRel amountFictitiousOilFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE, fictitiousOilFee);
        busiList.add(amountFictitiousOilFeeSubjectsRel);
        BusiSubjectsRel amountBridgeFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFOREPAY_BRIDGE, bridgeFee);
        busiList.add(amountBridgeFeeSubjectsRel);
        //????????????
        BusiSubjectsRel amountMasterSubsidySubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, masterSubsidy);
        busiList.add(amountMasterSubsidySubjectsRel);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList);
        long soNbr = CommonUtil.createSoNbr();
        //?????????????????????????????????
        if (fictitiousOilFee > 0 && oilConsumer == OrderConsts.OIL_CONSUMER.SHARE ) {
            Integer distributionType = null;
            if (isOwnCarUser) {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                    if (tempVirtualOilFee > 0) {//???????????????????????????
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                    }
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        throw new BusinessException("???????????????????????????????????????");
                    }
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                }
            } else {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {//?????????????????????
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//????????????
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                    } else if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//??????????????????
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2;
                    }
                }
            }
            if (tempVirtualOilFee > 0 && distributionType != null) {
                Map<String, Object> resultMap = iOilRechargeAccountService.distributionOil(masterUserId, tenantUserId, fictitiousOilFee, String.valueOf(orderId), EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE, tenantId, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1,distributionType,loginInfo);
                Long noPayOil = (Long) resultMap.get("noPayOil");
                Long noRebateOil = (Long) resultMap.get("noRebateOil");
                Long noCreditOil = (Long) resultMap.get("noCreditOil");
                if (thisSourceList == null) {
                    throw new BusinessException("???????????????????????????");
                }
                if (noPayOil == null) {
                    throw new BusinessException("????????????????????????");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("??????????????????");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("??????????????????");
                }
                thisSourceList.setSourceAmount(noPayOil);
                thisSourceList.setNoPayOil(noPayOil);
                thisSourceList.setCreditOil(noCreditOil);
                thisSourceList.setNoCreditOil(noCreditOil);
                thisSourceList.setRebateOil(noRebateOil);
                thisSourceList.setNoRebateOil(noRebateOil);
                iOrderOilSourceService.saveOrUpdate(thisSourceList);
            }
        }
        // ??????????????????
        // ????????????????????????????????????????????????
        OrderResponseDto param = new OrderResponseDto();
        param.setSourceList(sourceList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE,
                EnumConsts.PayInter.BEFORE_PAY_CODE, tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account,
                busiSubjectsRelList, soNbr, orderId, sysOperator.getName(), null, tenantId, null, "",param,vehicleAffiliation,loginInfo);

        //??????????????????
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRelMaster = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB, masterSubsidy);
        fleetBusiList.add(payableOverdueRelMaster);
        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        //???????????????????????????
        //??????????????????
        boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        if (masterSubsidy > 0) {
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(masterUserId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, masterSubsidy, -1L, vehicleAffiliation, orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,limit.getUserType(),0L,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("???????????????(???????????????)");
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
            }
            payoutIntf.setBusiCode(String.valueOf(orderId));
            if (orderScheduler != null) {
                payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
            }
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                //??????payout_order
                iPayoutOrderService.createPayoutOrder(masterUserId, masterSubsidy, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }
        //??????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????HA??????????????????????????????=?????????????????? ???+ ??????(??????+??????) + ????????????|ETC???+ ????????????????????????HA??????????????????????????????????????????
        if (isOwnCarUser && isNeedBill == OrderAccountConst.ORDER_BILL_TYPE.platformBill) {
            if (totalFee > 0) {
                this.payOwnCarBillAmount(orderId, totalFee, masterUserId, tenantId, soNbr,accessToken);
            }
        }
        // ?????????????????????????????????????????????
        ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.BEFORE_PAY_CODE, orderId, entiyOilFee + fictitiousOilFee + bridgeFee + masterSubsidy + slaveSubsidy, vehicleAffiliation, "");
        parametersNewDto.setTotalFee(String.valueOf(entiyOilFee + fictitiousOilFee + bridgeFee + masterSubsidy + slaveSubsidy));
        parametersNewDto.setTenantUserId(tenantUserId);
        parametersNewDto.setTenantBillId(tenantSysOperator.getBillId());
        parametersNewDto.setTenantUserName(tenantUser.getLinkman());
        busiSubjectsRelList.addAll(fleetSubjectsRelList);
        iOrderOilSourceService.busiToOrder(parametersNewDto, busiSubjectsRelList,loginInfo);
        // ?????????????????????
        if (slaveUserId > 0) {
            OrderLimit slaveLimit = iOrderLimitService.getOrderLimit(slaveUserId, orderId, tenantId,-1,loginInfo);
            if (slaveLimit == null) {
                throw new BusinessException("??????????????????" + orderId + "??????ID???" + slaveUserId + "??????id???" + tenantId + "???????????????????????????");
            }
            // ??????userID??????????????????
            //UserDataInfo user = userSV.getUserDataInfo(slaveUserId);
            SysUser sysOperator1 = iSysOperatorService.getSysOperatorByUserIdOrPhone(slaveUserId, null, 0L);
            if (sysOperator1 == null) {
                throw new BusinessException("?????????????????????????????????!");
            }
            // ????????????ID???????????????????????????????????????
            OrderAccount account1 = iOrderAccountService.queryOrderAccount(sysOperator1.getUserInfoId(), vehicleAffiliation,0L, tenantId,oilAffiliation,slaveLimit.getUserType());
            // ??????????????????????????????
            List<BusiSubjectsRel> busiList1 = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel amountSlaveSubsidySubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, slaveSubsidy);
            busiList1.add(amountSlaveSubsidySubjectsRel);
            // ??????????????????
            List<BusiSubjectsRel> busiSubjectsRelList1 = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList1);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE, tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(),
                    account1, busiSubjectsRelList1, soNbr, orderId, sysOperator1.getName(), null,tenantId, null, "", param,vehicleAffiliation,loginInfo);
            //??????????????????
            fleetBusiList.clear();
            BusiSubjectsRel payableOverdueRelSlave = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB, slaveSubsidy);
            fleetBusiList.add(payableOverdueRelSlave);
            // ??????????????????
            List<BusiSubjectsRel> fleetSubjectsRelList1 = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    sysOperator1.getUserInfoId(), sysOperator1.getName(), fleetAccount, fleetSubjectsRelList1, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            if (slaveSubsidy > 0) {
                PayoutIntf payoutIntf1 = iPayoutIntfService.createPayoutIntf(slaveUserId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, slaveSubsidy, -1L, vehicleAffiliation, orderId,
                        tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,slaveLimit.getUserType(),0L,accessToken);
                payoutIntf1.setObjId(Long.valueOf(sysOperator1.getBillId()));
                payoutIntf1.setRemark("???????????????(???????????????)");
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                        !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                    payoutIntf1.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                }
                payoutIntf1.setBusiCode(String.valueOf(orderId));
                if (orderScheduler != null) {
                    payoutIntf1.setPlateNumber(orderScheduler.getPlateNumber());
                }
                iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf1,accessToken);
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                        !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                    //??????payout_order
                    iPayoutOrderService.createPayoutOrder(slaveUserId, slaveSubsidy, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf1.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                }
            }
            // ?????????????????????????????????????????????
            ParametersNewDto parametersNewDto1 = iOrderOilSourceService.setParametersNew(sysOperator1.getUserInfoId(), sysOperator1.getBillId(),
                    EnumConsts.PayInter.BEFORE_PAY_CODE, orderId, slaveSubsidy, vehicleAffiliation, "");
            parametersNewDto1.setTotalFee(String.valueOf(entiyOilFee + fictitiousOilFee + bridgeFee + masterSubsidy + slaveSubsidy));
            parametersNewDto1.setTenantUserId(tenantUserId);
            parametersNewDto1.setTenantBillId(tenantSysOperator.getBillId());
            parametersNewDto1.setTenantUserName(tenantUser.getLinkman());
            busiSubjectsRelList1.addAll(fleetSubjectsRelList1);
            iOrderOilSourceService.busiToOrder(parametersNewDto1, busiSubjectsRelList1,loginInfo);
        }
    }

    @Override
    public String synPayCenter(Long orderId,String accessToken) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo != null) {
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            synPayCenterUpdateOrderOrProblemInfo(orderInfo, orderScheduler);
        }else{
            OrderInfoH orderInfoH = iOrderInfoHService.getOrderH(orderId);
            if (orderInfoH != null) {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                synPayCenterOrderH(orderInfoH, orderSchedulerH,accessToken);
            }else{
                throw new BusinessException("???????????????["+orderId+"]??????");
            }
        }
        return "Y";
    }

    @Override
    public void verifyFeeOut(OrderScheduler orderScheduler, OrderFee orderFee, OrderInfoExt orderInfoExt, Long price, Long agingId, Long problemId) {
        //??????????????? ?????????/??????????????????
        if(orderScheduler.getVehicleClass() == null || (( orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                || (orderScheduler.getVehicleClass()==SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT
        ))){
            Long agingFee = 0L;
            //agingId > 0 ?????????  ??????????????????????????????????????????
            if (agingId <= 0) {
                List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(orderFee.getOrderId());
                if (agingInfos != null && agingInfos.size() > 0) {
                    for (OrderAgingInfo agingInfo : agingInfos) {
                        if (agingInfo != null
                                && (agingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END
                                || agingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.AUDIT
                                || agingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                        )) {
                            agingFee = agingInfo.getFinePrice() == null ? 0 : agingInfo.getFinePrice();
                        }
                    }
                }
            }
            boolean arrivePaymentState = true;
            Long sumFee =(orderFee.getFinalFee() == null ? 0 :orderFee.getFinalFee() )  - (orderFee.getInsuranceFee() == null ? 0 : orderFee.getInsuranceFee());
            if (orderFee.getArrivePaymentState() != null
                    && orderFee.getArrivePaymentState().intValue() != OrderConsts.AMOUNT_FLAG.ALREADY_PAY
                    && orderFee.getArrivePaymentFee() != null && orderFee.getArrivePaymentFee() > 0
            ) {
                arrivePaymentState = false;
                sumFee += orderFee.getArrivePaymentFee() == null ? 0 :orderFee.getArrivePaymentFee();
            }
            //???????????????  + ????????????
            Long fee = Math.abs(price) + Math.abs(agingFee);
            List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderFee.getOrderId(), null);
            if (list != null && list.size() > 0) {
                for (OrderProblemInfo op : list) {
                    if ((op.getState() == SysStaticDataEnum.EXPENSE_STATE.END
                            || op.getState() == SysStaticDataEnum.EXPENSE_STATE.AUDIT
                            || op.getState() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                    )
                            && !Objects.equals(op.getId(),problemId)
                            && op.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                    ) {
                        SysStaticData problemInfoData = orderRedisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE, op.getProblemType());
                        if (problemInfoData != null && problemInfoData.getCodeId() == EnumConsts.Exception_Deal_Type.REDUCEMONEY.getType()) {
                            fee += (op.getProblemDealPrice() == null ?
                                    Math.abs(op.getProblemPrice() == null ? 0 : op.getProblemPrice())
                                    : Math.abs(op.getProblemDealPrice())) - (op.getDeductionFee() == null ? 0 : Math.abs(op.getDeductionFee()));
                        }
                    }
                }
            }
            //????????????+????????????
            if (fee > sumFee) {
                throw new BusinessException("??????????????????????????????"+(arrivePaymentState ? "" : "????????????")+"????????????!");
            }
        }
    }

    @Override
    public void cancelDriverSwitchSubsidy(Long orderId,LoginInfo loginInfo,String token) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler != null) {
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            if (orderScheduler.getVehicleClass() != null
                    && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.COST) {
                List<OrderDriverSwitchInfo>  switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrder(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                if (switchInfos != null && switchInfos.size() > 0) {
                    for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
                        if (orderDriverSwitchInfo.getReceiveUserId() != null &&
                                !orderDriverSwitchInfo.getReceiveUserId().equals(orderScheduler.getCarDriverId())
                                && !orderDriverSwitchInfo.getReceiveUserId().equals(orderScheduler.getCopilotUserId())) {
                            Integer isPayed = OrderConsts.AMOUNT_FLAG.ALREADY_PAY;
                            Long subsidyFee = orderDriverSubsidyService.findOrderDriverSubSidyFee(orderId, orderDriverSwitchInfo.getReceiveUserId(),null,null, false,isPayed);
                            if (subsidyFee > 0) {
                                AcOrderSubsidyInDto acOrderSubsidyIn = new AcOrderSubsidyInDto();
                                acOrderSubsidyIn.setOrderId(orderId);
                                acOrderSubsidyIn.setDriverUserId(orderDriverSwitchInfo.getReceiveUserId());
                                acOrderSubsidyIn.setDriverSubsidy(- subsidyFee);
                                acOrderSubsidyIn.setDriverUserName(orderDriverSwitchInfo.getReceiveUserName());
                                acOrderSubsidyIn.setVehicleAffiliation("0");
                                acOrderSubsidyIn.setOilAffiliation(orderInfoExt.getOilAffiliation());
                                acOrderSubsidyIn.setTenantId(orderScheduler.getTenantId());
                                payoutIntfService.payAddSubsidy(acOrderSubsidyIn,loginInfo,token);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void cancelDriverSwitchOilFee(Long orderId, LoginInfo loginInfo,String token) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler != null) {
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            OrderInfo orderInfo = orderInfoService.getOrder(orderId);
            if (orderScheduler.getVehicleClass() != null
                    && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null
                    && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.COST) {
                List<DriverOrderOilOutDto>  driverOrderOilOuts = updateOrderService.queryDriverOilByOrderId(orderId);
                for (DriverOrderOilOutDto driverOrderOilOut : driverOrderOilOuts) {
                    if (driverOrderOilOut.getUserId() != null &&
                            !driverOrderOilOut.getUserId().equals(orderScheduler.getCarDriverId())
                            && !driverOrderOilOut.getUserId().equals(orderScheduler.getCopilotUserId())) {
                        UpdateTheOwnCarOrderInDto in = new UpdateTheOwnCarOrderInDto();
                        in.setVehicleAffiliation("0");
                        in.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
                        in.setOrderId(orderId);
                        in.setTenantId(orderInfo.getTenantId());
                        in.setIsNeedBill(orderInfo.getIsNeedBill());
                        in.setSlaveUserId(orderScheduler.getCopilotUserId());
                        in.setOriginalEntiyOilFee(0L);
                        in.setUpdateEntiyOilFee(0L);
                        in.setOriginalFictitiousOilFee(driverOrderOilOut.getOrderOil()  == null ? 0L :driverOrderOilOut.getOrderOil()) ;
                        in.setUpdateFictitiousOilFee(0L);
                        in.setOriginalBridgeFee(0L);
                        in.setUpdateBridgeFee(0L);
                        in.setOriginalMasterSubsidy(0L);
                        in.setUpdateMasterSubsidy(0L);
                        in.setOriginalSlaveSubsidy(0L);
                        in.setUpdateSlaveSubsidy(0L);
                        in.setMasterUserId(driverOrderOilOut.getUserId());
                        iOrderOilSourceService.updateTheOwnCarOrder(in,loginInfo,token);
                    }
                }
            }
        }
    }

    @Override
    public String getXid(Long orderId) {
        OrderFeeExt orderFeeExt = getOrderFeeExtByOrderId(orderId);
        return orderFeeExt.getXid();
    }

    @Override
    public void verifyPayFail(Long orderId, String auditCode, String verifyDesc, boolean load, boolean receipt, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "verifyPayFail" + orderId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        boolean isNotAudit = false; //????????????????????????
        try {
            AuditCallbackDto sure = auditSettingService.sure(auditCode, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
        } catch (BusinessException e) {
            if ("???????????????????????????".equals(e.getMessage())) {
                isNotAudit = true;
            }else{
                throw new BusinessException(e.getMessage());
            }
        }

        if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {

        }else if(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)){

        }else if(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)){
            if (!load && !receipt) {
                throw new BusinessException("???????????????????????????");
            }
            iOrderReceiptService.verifyRece(String.valueOf(orderId), load, receipt, null, null, OrderConsts.RECIVE_TYPE.SINGLE, accessToken);
        }else{
            throw new BusinessException("???????????????????????????");
        }

        // ????????????
        if (isNotAudit) {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderId);
            params.put("auditCode", auditCode);
            iAuditService.startProcess(auditCode, orderId,
                    SysOperLogConst.BusiCode.OrderInfo, params, accessToken, tenantId);
        }
    }

    /**
     * ????????????????????????(????????????)
     * @param orderInfoH
     * @param orderSchedulerH
     * @throws Exception
     */
    public void synPayCenterOrderH(OrderInfoH orderInfoH, OrderSchedulerH orderSchedulerH,String accessToken) {
        // ???????????????
        if (orderInfoH.getIsNeedBill() != null && orderInfoH.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderInfoH.getOrderId());
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderInfoH.getOrderId());
            OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH(orderInfoH.getOrderId());
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderInfoH.getOrderId());
            if (orderSchedulerH.getVehicleClass() != null && orderSchedulerH.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (orderInfoExtH.getPreAmountFlag() !=null &&
                        orderInfoExtH.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    //???????????????????????????????????????
                    return;
                }
            }
            OrderFee preOrderFee = new OrderFee();
            OrderFeeExt preOrderFeeExt = new OrderFeeExt();
            OrderInfoExt preOrderInfoExt = new OrderInfoExt();
            OrderInfo orderInfo = new OrderInfo();
            OrderGoods orderGoods = new OrderGoods();
            OrderScheduler orderScheduler = new OrderScheduler();
            BeanUtils.copyProperties(orderGoods, orderGoodsH);
            BeanUtils.copyProperties(preOrderFeeExt, orderFeeExtH);
            BeanUtils.copyProperties(preOrderFee, orderFeeH);
            BeanUtils.copyProperties(preOrderInfoExt, orderInfoExtH);
            BeanUtils.copyProperties(orderInfo, orderInfoH);
            BeanUtils.copyProperties(orderScheduler, orderSchedulerH);
            if (StringUtils.isNotBlank(preOrderFee.getVehicleAffiliation())) {
//                String billForm56K = SysStaticDataUtil.getSysStaticDataCodeName(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"");
//                String billForm = BillPlatformCacheUtil.getPrefixByUserId(Long.parseLong(preOrderFee.getVehicleAffiliation()));
//                if (billForm != null && (billForm56K.equals(billForm) || SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm))) {//56K????????????
//                    this.syncBillFormOrder(orderInfo, orderGoods, orderScheduler, preOrderFee,
//                            preOrderFeeExt, preOrderInfoExt, false,true);
//                }else{
                    FeesDto feesDto = this.calculateSyncFee(preOrderFee, orderInfo, preOrderInfoExt, orderScheduler);
                    Long cashFee = feesDto.getCashFee();
                    Long entityOilFee = feesDto.getEntityOilFee();
                    Long virtualFee = feesDto.getVirtualFee();
                    Long etcFee = feesDto.getEtcFee();
//                    synPayCenterH(orderInfoH, orderGoodsH, orderSchedulerH, orderFeeH, orderFeeExtH, cashFee,entityOilFee,virtualFee,etcFee,accessToken);
//                }
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param cashFee
     * @param entityOilFee
     * @param virtualFee
     * @param etcFee
     *            ???????????????????????????????????????????????????????????????????????????
     */
//    private void synPayCenterH(OrderInfoH orderInfo, OrderGoodsH orderGoods, OrderSchedulerH orderScheduler,
//                               OrderFeeH orderFee, OrderFeeExtH orderFeeExt, Long cashFee,Long entityOilFee,Long virtualFee,Long etcFee,String accessToken){
//        if (StringUtils.isEmpty(orderFee.getVehicleAffiliation())) {
//            log.error("?????????????????????id??????????????????????????????,????????????[" + orderInfo.getOrderId() + "]????????????????????????");
//            orderFeeExt.setResultInfo("?????????????????????id??????????????????????????????");
//            orderFeeExtHService.saveOrUpdate(orderFeeExt);
//            return;
//        }
//
//        OrderInfoInDto orderInfoIn = new OrderInfoInDto();
//        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());
//        PayCenterAccountInfo accountInfo = payCenterAccountInfoService
//                .getPayCenterAccountInfo(Long.valueOf(orderFee.getVehicleAffiliation()), orderInfo.getTenantId());
//
//        if (accountInfo != null) {
//            orderInfoIn.setAcctId(accountInfo.getLoginAcct());
//            orderInfoIn.setPassword(accountInfo.getPassword());
//        } else {
//            log.error("????????????id????????????????????????????????????????????????????????????,????????????[" + orderInfo.getOrderId() + "]??????????????????["
//                    + orderFee.getVehicleAffiliation() + "]?????????id[" + orderInfo.getTenantId() + "]");
//            orderFeeExt.setResultInfo("????????????id????????????????????????????????????????????????????????????");
//            orderFeeExtHService.saveOrUpdate(orderFeeExt);
//            return;
//        }
//        FastDFSHelper client = FastDFSHelper.getInstance();
//        orderInfoIn.setOrderId(orderInfo.getOrderId());
//        orderInfoIn.setTenantId(orderInfo.getTenantId());
//        orderInfoIn.setCustName(orderGoods.getCompanyName());
//        orderInfoIn.setCustLinkman(orderGoods.getLineName());
//        orderInfoIn.setCustLinkphone(orderGoods.getLinePhone());
//        orderInfoIn.setCustAddress(orderGoods.getAddress());
//        orderInfoIn.setGoodsName(orderGoods.getGoodsName());
//        orderInfoIn.setGoodsVolume(orderGoods.getSquare());
//        orderInfoIn.setGoodsWeight(orderGoods.getWeight());
//        orderInfoIn.setVehicleType(orderGoods.getVehicleLengh()+orderGoods.getVehicleStatus());
//
//        orderInfoIn.setSourceProvince(orderInfo.getSourceProvine());
//        orderInfoIn.setSourceCity(orderInfo.getSourceCounty());
//        orderInfoIn.setSourceAddressDetail(orderGoods.getAddrDtl());
//        orderInfoIn.setSourceEand(orderGoods.getEand());
//        orderInfoIn.setSourceNand(orderGoods.getNand());
//
//        orderInfoIn.setDesProvince(orderInfo.getDesProvine());
//        orderInfoIn.setDesCity(orderInfo.getDesCounty());
//        orderInfoIn.setDesAddressDetail(orderGoods.getDesDtl());
//        orderInfoIn.setDesNand(orderGoods.getNandDes());
//        orderInfoIn.setDesEand(orderGoods.getEandDes());
//        Long distance = orderScheduler.getDistance();// ?????????
//        if (distance != null && distance > 0)
//            orderInfoIn.setDistance(distance.floatValue() / 1000);
//        orderInfoIn.setDependDate(orderScheduler.getDependTime());
//        orderInfoIn.setArriveTime(orderScheduler.getArriveTime());
//        orderInfoIn.setReceiveName(orderGoods.getReciveName());
//        orderInfoIn.setReceivePhone(orderGoods.getRecivePhone());
//        orderInfoIn.setContractUrl(orderGoods.getContractUrl() != null ? client.getHttpURL(orderGoods.getContractUrl()).split("\\?")[0] : "");
//        List<OrderReceipt> receipts=iOrderReceiptService.findOrderReceipts(orderInfo.getOrderId(),acc,null);
//        if(receipts!=null&&receipts.size()>0) {
//            String receiptsUrl=receipts.get(receipts.size()-1).getFlowUrl();
//            orderInfoIn.setReceiptsUrl(receiptsUrl != null ? client.getHttpURL(receiptsUrl).split("\\?")[0] : "");
//        }
//
//        orderInfoIn.setPlateNumber(orderScheduler.getPlateNumber());
//
//        orderInfoIn.setDriverUserName(orderScheduler.getCarDriverMan());
//        orderInfoIn.setDriverUserId(orderScheduler.getCarDriverId());
//        orderInfoIn.setDriverPhone(orderScheduler.getCarDriverPhone());
//        orderInfoIn.setOrderCreateDate(orderInfo.getCreateTime());
//
//        Long totalFee=0L;
//        if(orderScheduler.getVehicleClass() != null
//                && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1){
//            if (cashFee != null && cashFee > 0) {
//                totalFee=totalFee+cashFee;
//                orderInfoIn.setCashFee(cashFee);
//            } else {
//                if (orderInfoExt.getPaymentWay() != null) {
//                    //?????????????????????ETC
//                    if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
//                        //?????????
//                        cashFee = orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee()
//                                + (orderFee.getFinalFee() == null ? 0 : orderFee.getFinalFee())
//                                +(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
//                    }else if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE || orderInfoExt.getPaymentWay() == PAYMENT_WAY.COST){
//                        //????????????
//                        cashFee = orderFee.getCostPrice() == null ? 0 : orderFee.getCostPrice();
//                    }
//                    if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
//                        cashFee += orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee();
//                        cashFee += orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee();
//                    }
//                }
//                totalFee=totalFee+cashFee;
//                orderInfoIn.setCashFee(cashFee);
//            }
//            orderInfoIn.setEntityOilFee(0L);
//            orderInfoIn.setEtcFee(0L);
//            orderInfoIn.setVirtualOilFee(0L);
//        }else if(orderScheduler.getVehicleClass() != null
//                && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
//                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
//                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4){
//            if (cashFee != null && cashFee > 0) {
//                totalFee=totalFee+cashFee;
//                orderInfoIn.setCashFee(cashFee);
//            } else {
//                cashFee=(orderFee.getPreCashFee() == null ? 0L : orderFee.getPreCashFee().longValue())
//                        + (orderFee.getFinalFee() == null ? 0L : orderFee.getFinalFee().longValue());
//                totalFee=totalFee+cashFee;
//                orderInfoIn.setCashFee(cashFee);
//            }
//            if (etcFee != null && etcFee > 0) {
//                totalFee=totalFee+etcFee;
//                orderInfoIn.setEtcFee(etcFee);
//            }else{
//                orderInfoIn.setEtcFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
//                totalFee=totalFee+(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
//            }
//            if (virtualFee != null && virtualFee > 0) {
//                // ?????????
//                totalFee=totalFee+virtualFee;
//                orderInfoIn.setVirtualOilFee(virtualFee);
//            }else{
//                // ?????????
//                totalFee=totalFee+(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
//                orderInfoIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
//            }
//            if (entityOilFee != null && entityOilFee > 0) {
//                totalFee=totalFee+entityOilFee;
//                orderInfoIn.setEntityOilFee(entityOilFee);
//            }else{
//                totalFee=totalFee+(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
//                orderInfoIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
//            }
//        }
//        orderInfoIn.setTotalFee(totalFee);
//        if (totalFee > 0) {
//            // ?????????????????????????????????????????????
//            OrderInfoResultInfo infoResultInfo = null;
//            orderFeeExt.setExecDate(LocalDateTime.now());
//            try {
//                infoResultInfo = payOutIntf.syncOrderInfo(orderInfoIn);
//            } catch (Exception e) {
//                log.error("?????????????????????????????????[" + orderInfo.getOrderId() + "]", e);
//                orderFeeExt.setResultInfo("????????????????????????");
//            }
//
//            if (infoResultInfo != null) {
//                if (OrderConsts.PAY_CENTER.CODE_SUCESS.equals(infoResultInfo.getCode())) {
//                    orderFeeExt.setXid(String.valueOf(infoResultInfo.getId()));
//                    orderFeeExt.setResultCode(OrderConsts.PAY_CENTER.CODE_SUCESS);
//                    orderFeeExt.setResultInfo("????????????");
//                } else {
//                    orderFeeExt.setResultCode(infoResultInfo.getCode());
//                    orderFeeExt.setResultInfo(infoResultInfo.getMsg());
//                }
//            }
//        }else{
//            orderFeeExt.setResultInfo("??????????????????0???????????????????????????");
//        }
//        orderFeeExtHService.saveOrUpdate(orderFeeExt);
//
//    }

    /***
     * ????????????????????????????????????
     */
    public Map verifyOilCardNum(Long orderId,List<String> oilCardNums,boolean isPay,Long oilFee){
        if (oilCardNums == null || oilCardNums.size() <= 0) {
            throw new BusinessException("??????????????????");
        }
        long count = oilCardNums.stream().distinct().count();
        boolean isRepeat = count < oilCardNums.size();
        if (isRepeat) {
            throw new BusinessException("????????????????????????!");
        }
        Map map = new ConcurrentHashMap();
        boolean isEnough = false;
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        if (orderFee == null) {
            throw new BusinessException("???????????????["+orderId+"]??????");
        }
        List<OilCardManagement> listOut = new ArrayList<OilCardManagement>();
        if (oilFee == null || oilFee <= 0) {
            oilFee = orderFee.getPreOilFee();
        }
        for (String oilCardNum : oilCardNums) {
            Long oilCardFee = 0L;
            Boolean isEquivalenceCard = false;
            if (!isPay) {//??????????????? ????????????????????????????????????
                //?????????????????????????????? ????????????????????????
                if(orderFee.getPrePayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()) {
                    if (oilCardNum.equals(orderFee.getPrePayEquivalenceCardNumber())) {
                        oilCardFee += orderFee.getPrePayEquivalenceCardAmount() == null ? 0 : orderFee.getPrePayEquivalenceCardAmount();
                        isEquivalenceCard = true;
                    }
                }
                if(orderFee.getAfterPayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()) {
                    if (oilCardNum.equals(orderFee.getAfterPayEquivalenceCardNumber())) {
                        oilCardFee += orderFee.getAfterPayEquivalenceCardAmount() == null ? 0 : orderFee.getAfterPayEquivalenceCardAmount();
                        isEquivalenceCard = true;
                    }
                }
            }
            List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, orderFee.getTenantId());
            OilCardManagement oilCard = new OilCardManagement();
            if (list == null || list.size() <= 0) {
                if (isEquivalenceCard) {//??????????????????
                    //????????????????????????
                    oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                    oilCard.setOilCarNum(oilCardNum);
                }else{
                    if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
                        oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                        oilCard.setOilCarNum(oilCardNum);
                    }else{
                        throw new BusinessException("????????????["+oilCardNum+"]?????????!");
                    }
                }
            }else{
                oilCard = list.get(0);
            }
            //?????????????????????????????????
            int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
            oilCard.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", cardType + ""));
            oilCardFee += oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;
            //??????????????????????????????
            oilCard.setCardBalance(oilCardFee);
            oilCard.setIsNeedWarn(false);
            //???????????????????????????????????????
            if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                ServiceInfo serviceInfo = iServiceInfoService.getServiceInfoById(oilCard.getUserId());
                if (serviceInfo != null) {
                    if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().intValue() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD) {
                        oilCard.setIsNeedWarn(true);
                    }
                }
                isEnough = true;
                listOut.add(oilCard);
                break;
            }else{
                if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant().intValue() != OrderConsts.IS_TEMP_TENANT.YES) {
                    if (oilCardFee == 0L) {
                        throw new BusinessException("??????["+oilCardNum+"]???????????????0?????????!");
                    }
                }
                //???????????????????????? ??????????????????????????????
                if (oilFee <= oilCardFee) {
                    isEnough = true;
                    listOut.add(oilCard);
                }else{//???????????????????????? ????????????????????? ????????????
                    oilFee -= oilCardFee;
                    listOut.add(oilCard);
                }
            }
        }
        if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
            isEnough = true;
        }
        map.put("listOut", listOut);
        map.put("isEnough", isEnough);
        return map;
    }

    /**
     * ????????????(??????????????????)??????????????????
     */
    @Override
    public void synPayCenterUpdateOrderOrProblemInfo(OrderInfo orderInfo, OrderScheduler orderScheduler) {
        // ???????????????
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            OrderFee preOrderFee = iOrderFeeService.getOrderFee(orderInfo.getOrderId());
            OrderInfoExt preOrderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());
            OrderGoods preOrderGoods = iOrderGoodsService.getOrderGoods(orderInfo.getOrderId());
            OrderFeeExt preOrderFeeExt = orderFeeExtService.getOrderFeeExt(orderInfo.getOrderId());
            if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (preOrderInfoExt.getPreAmountFlag() !=null &&
                        preOrderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    //???????????????????????????????????????
                    return;
                }
            }
            this.syncBillFormOrder(orderInfo, preOrderGoods, orderScheduler,
                    preOrderFee, preOrderFeeExt, preOrderInfoExt, true,true);
        }
    }

    /**
     * ????????????????????????
     */
    public String paySubsidy(Long orderId, LoginInfo loginInfo,String token){
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler != null) {
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            List<OrderDriverSubsidyDto> orderDriverSubsidys =  iOrderDriverSubsidyService.findDriverNoPaySubsidys(orderId, null, orderScheduler.getCarDriverId(), orderScheduler.getCopilotUserId(), orderScheduler.getTenantId(), false);
            if (orderDriverSubsidys != null && orderDriverSubsidys.size() > 0) {
                for (OrderDriverSubsidyDto dto : orderDriverSubsidys) {
                    Long userId = dto.getUserId();
                    Long subsidy = dto.getSubsidy();
                    String userName = dto.getUserName();
                    if (userId != null && userId > 0 && subsidy > 0) {
                        AcOrderSubsidyInDto acOrderSubsidyIn = new AcOrderSubsidyInDto();
                        acOrderSubsidyIn.setOrderId(orderId);
                        acOrderSubsidyIn.setDriverUserId(userId);
                        acOrderSubsidyIn.setDriverSubsidy(subsidy);
                        acOrderSubsidyIn.setDriverUserName(userName);
                        acOrderSubsidyIn.setVehicleAffiliation("0");
                        acOrderSubsidyIn.setOilAffiliation(orderInfoExt.getOilAffiliation());
                        acOrderSubsidyIn.setTenantId(orderScheduler.getTenantId());
                        iPayoutIntfService.paySubsidy(acOrderSubsidyIn,loginInfo,token);
                    }
                    iOrderDriverSubsidyService.updateDriverSubsidyPayType(orderId, userId, OrderConsts.AMOUNT_FLAG.ALREADY_PAY);
                }
            }
        }
        return "Y";
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

    /**
     * ????????????
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }
}
