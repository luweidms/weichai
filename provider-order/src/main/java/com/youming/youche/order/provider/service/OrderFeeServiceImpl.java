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
    * 订单费用表 服务实现类
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
            throw new BusinessException("请输入油卡号");
        }
        long count = oilCardNums.stream().distinct().count();
        boolean isRepeat = count < oilCardNums.size();
        if (isRepeat) {
            throw new BusinessException("输入的油卡号重复!");
        }
        Map map = new ConcurrentHashMap();
        boolean isEnough = false;

        // 查询订单费用信息
        OrderFee orderFee = this.getOrderFee(orderId);

        // 查询订单拓展信息
        OrderInfoExt orderInfoExt = orderInfoExtService.selectByOrderId(orderId);
        if (orderFee == null) {
            throw new BusinessException("找不到订单["+orderId+"]信息");
        }
        List<OilCardManagement> listOut = new ArrayList<OilCardManagement>();
        if (oilFee == null || oilFee <= 0) {
            oilFee = orderFee.getPreOilFee();
        }
        for (String oilCardNum : oilCardNums) {
            Long oilCardFee = 0L;
            Boolean isEquivalenceCard = false;
            if (!isPay) {//不是支付中 支付中会增加油卡以防叠加
                //若是卡号跟现付等值卡相等 需加上现付等值卡金额
                // 卡类型和油卡一致
                if(orderFee.getPrePayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()) {
                    if (oilCardNum.equals(orderFee.getPrePayEquivalenceCardNumber())) {
                        oilCardFee += orderFee.getPrePayEquivalenceCardAmount() == null ? 0 : orderFee.getPrePayEquivalenceCardAmount();
                        isEquivalenceCard = true;
                    }
                }

                //若是卡号跟后付等值卡相等 需后上现付等值卡金额
                if(orderFee.getAfterPayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()) {
                    if (oilCardNum.equals(orderFee.getAfterPayEquivalenceCardNumber())) {
                        oilCardFee += orderFee.getAfterPayEquivalenceCardAmount() == null ? 0 : orderFee.getAfterPayEquivalenceCardAmount();
                        isEquivalenceCard = true;
                    }
                }
            }

            // 查询卡信息
            List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, orderFee.getTenantId());
            OilCardManagement oilCard = new OilCardManagement();
            if (list == null || list.size() <= 0) {
                if (isEquivalenceCard) {//卡号是等值卡
                    //没添加卡是初始化
                    oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                    oilCard.setOilCarNum(oilCardNum);
                }else{
                    if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
                        oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                        oilCard.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", oilCard.getCardType() + ""));
                        oilCard.setOilCarNum(oilCardNum);
                    }else{
                        throw new BusinessException("实体油卡["+oilCardNum+"]不可用!");
                    }
                }
            }else{
                oilCard = list.get(0);
//                session.evict(oilCard);
            }
            //若油卡类型为服务商油卡
            int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
            oilCard.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", cardType + ""));
            oilCardFee += oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;
            //重新再初始化理论余额
            oilCard.setCardBalance(oilCardFee);
            oilCard.setIsNeedWarn(false);
            //供应商的卡不需判断理论金额
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
                        throw new BusinessException("油卡["+oilCardNum+"]理论余额为0不可用!");
                    }
                }
                //理论余额大于油费 则不需要再次输入卡号
                if (oilFee <= oilCardFee) {
                    isEnough = true;
                    listOut.add(oilCard);
                }else{//理论余额小于油费 则油费减去余额 再次比较
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
            throw new BusinessException("找不到订单["+orderInfo.getOrderId()+"]信息");
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

            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(),  SysOperLogConst.OperType.Update, "审核支付预付款，绑定油卡："+(oilCardNums == null || oilCardNums.size() == 0 ? "无" : oilCardNums.toString()));
        }
    }

    /**
     * 回单审核通过
     * @param orderId 订单号
     * @param verifyDesc 审核备注
     * @param reciveType 回单类型
     * @return
     *
     */
    @Override
    public String reciveVerifyPayPass(Long orderId, String verifyDesc, String receiptsStr, Integer reciveType,String accessToken) {
        //TODO 暂时放下统一处理 锁
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "reciveVerifyPayPass" + orderId, 3, 5);
        boolean isLock =true;
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
//        IAuditTF auditTF = (IAuditTF) SysContexts.getBean("auditTF");
        // TODO 审核流

        boolean sure = false;
//        OrderInfo orderInfo = orderInfoSV.getOrder(orderId);;
        OrderInfo orderInfo =orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("未找到订单号为["+orderId+"]在途订单，请刷新页面！");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("订单处于修改审核中,不能支付！");
        }
        try {
            //TODO  审核
          AuditCallbackDto auditCallbackDto =  auditSettingService.sure(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS,accessToken);
            if (auditCallbackDto != null && auditCallbackDto.getIsNext()) {
                sure = auditCallbackDto.getIsNext();
            }
//            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
//                sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
//            }
        } catch (BusinessException e) {
            if ("该业务数据不能审核".equals(e.getMessage())) {

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
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("业务审核类型不能为空！");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }

        boolean isNotAudit = false;
        boolean sure = false;
        AuditCallbackDto auditCallbackDto = null;
        // 审核流程
        try {
            auditCallbackDto = auditSettingService.sure(auditCode, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
            if (auditCallbackDto != null && auditCallbackDto.getIsNext()) {
                sure = auditCallbackDto.getIsNext();
            }
//            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
//             //   sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
//            }
        } catch (BusinessException e) {
            if ("该业务数据不能审核".equals(e.getMessage())) {
                isNotAudit = true;
            }else{
                throw new BusinessException(e.getMessage());
            }
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("未找到订单号为["+orderId+"]在途订单，请刷新页面！");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("订单处于修改审核中,不能支付！");
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
                // 回单审核
                iOrderReceiptService.verifyRece(String.valueOf(orderId), false, false, verifyDesc, receiptsStr, reciveType, accessToken, false);
            }
        }else{
            throw new BusinessException("业务审核类型异常！");
        }
    }

    /**
     * 回单审核不通过
     * @param orderId 订单号
     * @param verifyDesc 审核备注
     * @param load 是否驳回合同
     * @param receipt 是否驳回回单
     * @return
     * @throws Exception
     */
    @Override
    public String reciveVerifyPayFail(Long orderId, String verifyDesc, boolean load, boolean receipt,String accessToken) {
        // TODO 暂不处理 等统一处理
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "reciveVerifyPayFail" + orderId, 3, 5);
        boolean isLock= true;
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
//        IAuditTF auditTF = (IAuditTF) SysContexts.getBean("auditTF");
//        IOrderReciveTF orderReciveTF =(IOrderReciveTF) SysContexts.getBean("orderReciveTF");
        boolean isNotAudit = false;
        try {
            //  TODO 审核
            auditSettingService.sure(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId, verifyDesc, AuditConsts.RESULT.FAIL,accessToken);
//            auditTF.sure(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId, verifyDesc, AuditConsts.RESULT.FAIL);
        } catch (BusinessException e) {
            if ("该业务数据不能审核".equals(e.getMessage())) {
                isNotAudit = true;
            }else{
                throw new BusinessException(e.getMessage());
            }
        }
        if (!load && !receipt) {
            throw new BusinessException("回单审核状态异常！");
        }
//        orderReciveTF.verifyRece(orderId, load, receipt, null, null, OrderConsts.RECIVE_TYPE.SINGLE);
        iOrderReceiptService.verifyRece(String.valueOf(orderId), load, receipt, verifyDesc, null, OrderConsts.RECIVE_TYPE.SINGLE,accessToken,null);
        if (isNotAudit) {
//            IAuditOutTF outTF = (IAuditOutTF) SysContexts.getBean("auditOutTF");
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderId);
            params.put("auditCode", AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE);
            //TODO 审核模块
            iAuditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId,
                    SysOperLogConst.BusiCode.OrderInfo, params,accessToken);
//            outTF.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId,
//                    SysOperLogConst.BusiCode.OrderInfo, params);
        }
        return null;
    }




    /**
     * 聂杰伟
     * 是否需要上传协议
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
                // TODO 车辆归宿
//                String billForm = BillPlatformCacheUtil.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation())); // 车辆归宿
                String billForm ="";
                if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//路歌
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
                    // TODO 车辆归宿
//                    String billForm = BillPlatformCacheUtil.getPrefixByUserId(Long.parseLong(orderFeeH.getVehicleAffiliation()));// 车辆归宿
                    String billForm ="";
                    if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//路歌
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

        Long copilotDaySalary = 0L;// 单位分
        Long monthSubSalary = 0L;
        Long subsidyDay = 0L;
        String dateString = "";
        UserDataInfo userDataInfo = userDataInfoService.getById(userId);
        if (userDataInfo == null) {//非自有车司机不计算补贴
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
//            throw new BusinessException("未找到"+(isCopilot ? "副驾驶":"主驾驶或切换司机")+"工资信息！");
            copilotDaySalary = salaryRel.getSubsidy();// 单位分
            monthSubSalary = salaryRel.getSalary();
        }

        subsidyDay = 0L;
        dateString = "";
        Double estStartTime = Double.parseDouble(readisUtil.getSysCfg("ESTIMATE_START_TIME", "0").getCfgValue());
        Map<String, Object> orderIdMap = orderSchedulerService.getPreOrderIdByUserid(userId, dependTime, tenantId,orderId);
        //预估订单时效
        arriveTime = arriveTime +((transitLineSize < 0 ? 0 : transitLineSize)+1 * estStartTime.floatValue());
        LocalDateTime preArriveDate = null;
        StringBuffer print = new StringBuffer("计算补贴打印信息：");
        if (orderIdMap != null) {
            if(OrderConsts.TableType.ORI==(Integer) orderIdMap.get("type")){
                //原表
                OrderScheduler scheduler=  orderSchedulerService.getOrderScheduler(DataFormat.getLongKey(orderIdMap, "orderId"));
                preArriveDate = orderInfoExtService.getOrderArriveDate(scheduler.getOrderId(), scheduler.getDependTime()
                        , scheduler.getCarStartDate(), scheduler.getArriveTime(), false);

            }else if(OrderConsts.TableType.HIS==(Integer) orderIdMap.get("type")){
                //历史表
                OrderSchedulerH schedulerH=  orderSchedulerHService.getOrderSchedulerH(DataFormat.getLongKey(orderIdMap, "orderId"));
                preArriveDate = orderInfoExtService.getOrderArriveDate(schedulerH.getOrderId(), schedulerH.getDependTime()
                        , schedulerH.getCarStartDate(), schedulerH.getArriveTime(), true);
            }
            print.append("上一单订单号:[").append(orderIdMap.get("orderId")).append("] ");

            print.append(" 上一单达到时间:[").append(orderDateUtil.stringByLocalDateTime(preArriveDate))
                    .append("]");
        }
        print.append("本单靠台时间:[").append(orderDateUtil.stringByLocalDateTime(dependTime));
        print.append("本单达到时限:[").append(arriveTime).append("]小时");

        Map<String, Object> subsidyDayMap = culateSubsidy(dependTime, arriveTime, preArriveDate);
        subsidyDay = Long.valueOf(subsidyDayMap.get("subsidyDay").toString());
        dateString = subsidyDayMap.get("dateString").toString();

        print.append("补贴天数：[").append(subsidyDay).append("]");
        print.append("补贴日期：[").append(dateString).append("]");
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
            // 没有上一单
            LocalDateTime arriveDate = orderDateUtil.addHourAndMins(dependTime, arriveTime);
            subsidyDay = OrderDateUtil.getDifferDay(getDateByLocalDateTime(dependTime),
                    getDateByLocalDateTime(arriveDate)) + 1;
//        } else {
//            // 有上一单
//            // 获取上一单的到达时间
//            // 本单的预计到达时间
//            LocalDateTime arriveDate = orderDateUtil.addHourAndMins(dependTime, arriveTime);
//            // 本单到达时间-（上一单到达时间+1），如果为负数 ，就取0
//            if (arriveDate.isBefore(preArriveDate)) {
//                subsidyDay = 0;
//            } else {
//                if (arriveDate.getYear() != preArriveDate.getYear()
//                        || arriveDate.getMonth() != preArriveDate.getMonth()) {
//                    // 如果上一单的时间跟本单的是不同年月的，按本单的单月1号计算
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
                if (billForm != null && billForm56K.equals(billForm)) {//56K票据平台
                    OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderInfo.getOrderId());
                    orderFeeExt.setIsNeedTrackSync(1);//需要同步
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
            if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//路歌
                iOrderSyncTypeInfoService.saveOrderSyncTypeInfo(orderInfo.getOrderId(), syncType, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE,accessToken);
            }
        }
    }

    @Override
    public String payProFee(long orderId, List<String> oilCardNums,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId <= 0) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        boolean isLock = true;
        //todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payProFee" + orderId, 3, 5);
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("找不到订单["+orderId+"]信息");
        }
        String msg = "Y";
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (scheduler.getCarDriverId() == null || scheduler.getCarDriverId() <= 0) {
            throw new BusinessException("未找到车辆信息,不能支付");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("订单处于修改审核中,不能支付");
        }
        if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.CANCELLED) {
            throw new BusinessException("订单已撤销不能支付");
        }
        if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("已经支付，请不要重复操作!");
        }
        if (scheduler.getVehicleClass() != null && scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() <= 0)) {
            throw new BusinessException("未选择成本模式，不能支付!");
        }
        //校验等值卡是否需要添加
        iOrderInfoHService.checkOilCarType(orderFee, orderInfo.getTenantId());
        //添加预付等值卡
        if(orderFee.getPrePayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()
                && StringUtils.isNotBlank(orderFee.getPrePayEquivalenceCardNumber())) {
            oilCardManagementService.saveEquivalenceCard(orderInfo,scheduler, orderFee.getPrePayEquivalenceCardNumber(), orderFee.getPrePayEquivalenceCardAmount(),true,true,loginInfo);
        }
        //添加尾款等值卡
        if(orderFee.getAfterPayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()
                && StringUtils.isNotBlank(orderFee.getAfterPayEquivalenceCardNumber())) {
            oilCardManagementService.saveEquivalenceCard(orderInfo,scheduler, orderFee.getAfterPayEquivalenceCardNumber(), orderFee.getAfterPayEquivalenceCardAmount(),true,true,loginInfo);
        }
        //设置首次支付状态
        this.setFirstPayFlag(orderInfoExt, scheduler, orderFee, orderInfo,orderFeeExt,accessToken);
        String vehicleAffiliation = orderFee.getVehicleAffiliation();
        if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {// 已外发
            Long userId = 0L;
            if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// 有归属租户
                // 外调车
                SysTenantDef tenantDef = iSysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                if (tenantDef == null) {
                    log.error("未找到车队信息，请联系客服！租户ID["+orderInfo.getToTenantId()+"]");
                    throw new BusinessException("未找到车队信息，请联系客服！");
                }
                userId = tenantDef.getAdminUser();
                if(!iAccountBankUserTypeRelService.isUserTypeBindCard(userId,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,SysStaticDataEnum.USER_TYPE.ADMIN_USER)) {
                    msg = "收款方"+tenantDef.getName()+"没有绑定银行卡，请提醒对方绑定银行卡， 此次交易只能使用线下付款。";
                }
            }else if( scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
                userId = scheduler.getCollectionUserId();
            } else {// 无归属车队 纯C车
                userId = scheduler.getCarDriverId();
                if(!iAccountBankUserTypeRelService.isUserTypeBindCard(userId,SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    msg = "收款方"+scheduler.getCarDriverMan()+"没有绑定银行卡，请提醒对方绑定银行卡， 此次交易只能使用线下付款。";
                }
            }
            if (orderFee.getVehicleAffiliation() == null) {
                log.error("订单资金属性有误,请重新开单!资金渠道["+orderFee.getVehicleAffiliation()+"]");
                throw new BusinessException("订单资金属性有误,请重新开单!");
            }
            long amountFee = orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee();
            if (orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {
                // 油卡验证
                this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler,orderInfoExt.getOilAffiliation(),true,loginInfo);
                Long pledgeFee = Long .parseLong(readisUtil.getSysCfg("PLEDGE_OILCARD_FEE", "0").getCfgValue());
                if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant() != OrderConsts.IS_TEMP_TENANT.YES) {
                    // 抵押金额  临时车队到司机不抵扣押金
                    oilCardManagementService.oilPledgeHandle(orderId, userId, vehicleAffiliation, oilCardNums, pledgeFee,
                            orderFee.getTenantId(),scheduler.getVehicleClass(),scheduler,loginInfo,accessToken);
                }
            }
            AdvanceChargeVo advanceChargeIn=new AdvanceChargeVo();
            int payType = OrderAccountConst.PAY_ADVANCE_TYPE.ORDINARY_PAY;
            boolean isPayTempOrder = false;
            if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
                //临时车队到司机
                payType = OrderAccountConst.PAY_ADVANCE_TYPE.TEMPORARY_FLEET_PAY_DRIVER;
            }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES
                    && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0){
                //车队对临时车队
                payType = OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_TEMPORARY_FLEET;
                //同时支付临时车队预付款
                isPayTempOrder = true;
            }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES){
                //代收
                payType = OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER;
                advanceChargeIn.setCollectionDriverUserId(scheduler.getCarDriverId());
            }
            if (StringUtils.isNotBlank(orderFee.getVehicleAffiliation()) &&
                    (orderFeeExt.getAppendFreight() == null || orderFeeExt.getAppendFreight() <= 0)
                    && (orderFeeExt.getOilBillType() == null || orderFeeExt.getOilBillType().intValue() != OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2)
            ) {
                String billForm = billPlatformService.getPrefixByUserId(Long.parseLong(orderFee.getVehicleAffiliation()));
                if (billForm != null && SysStaticDataEnum.BILL_FORM.BILL_56K.equals(billForm)) {//56K票据平台
                    BillSetting billSetting = iBillSettingService.getBillSettingByTenantId(orderInfo.getTenantId());
                    if (billSetting == null) {
                        throw new BusinessException("车队票据属性有误，请联系客服!");
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
                throw new BusinessException("找不到司机信息");
            }
            boolean isUserBindCard = iAccountBankUserTypeRelService.isUserTypeBindCard(scheduler.getCarDriverId(),SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            if(!isUserBindCard) {
                msg = "收款方"+scheduler.getCarDriverMan()+"没有绑定银行卡，请提醒对方绑定银行卡， 此次交易只能使用线下付款。";
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
                                throw new BusinessException("司机账户还有"+paymentWayName+"的油费，不能支付，请先转移。");
                            }
                        }else{
                            if ((out.getCostOil() != null && out.getCostOil() > 0) || (out.getContractOil() != null && out.getContractOil() > 0) ) {
                                SysStaticData payment_way = getSysStaticData("PAYMENT_WAY", orderInfoExt.getPaymentWay().toString());
                                String paymentWayName = "";
                                if(payment_way != null) {
                                    paymentWayName = payment_way.getCodeName();
                                }
                                throw new BusinessException("司机账户还有非"+paymentWayName+"的油费，不能支付，请先转移。");
                            }
                        }
                    }
                }
                if (orderInfoExt.getPaymentWay() != null
                        && (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST || orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)) {
                    if (orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {
                        // 油卡验证
                        if (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                            this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler,orderInfoExt.getOilAffiliation(),true,loginInfo);
                            //成本模式支付司机补贴
                            this.paySubsidy(orderId,loginInfo,accessToken);
                        }else if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE){
                            oilCardNums = new ArrayList<>();
                            //自有车实报实销模式
                            List<OrderOilCardInfo> orderOilCardInfos = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
                            for (OrderOilCardInfo orderOilCardInfo : orderOilCardInfos) {
                                String oilCardNum = orderOilCardInfo.getOilCardNum();
                                List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, scheduler.getTenantId());
                                if (list == null || list.size() <= 0) {
                                    throw new BusinessException("实体油卡["+oilCardNum+"]不可用!");
                                }

                                OilCardManagement oilCard = list.get(0);
                                //若油卡类型为服务商油卡
                                int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
                                Long balance = orderOilCardInfo.getOilFee() == null ? 0 : orderOilCardInfo.getOilFee();
                                //供应商的卡才需充值记录
                                if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                                    //实体油卡充值记录
                                    iOilEntityService.saveOilCardLog(oilCardNum, scheduler.getTenantId(), scheduler.getCarDriverId(), scheduler
                                            ,orderInfo,balance,oilCard,"0");
                                }
                                oilCardNums.add(oilCardNum);
                            }
                        }
                        Long pledgeFee = null; // todo
                        // 抵押金额
                        oilCardManagementService.oilPledgeHandle(orderId, scheduler.getCarDriverId(), vehicleAffiliation, oilCardNums, pledgeFee,
                                orderFee.getTenantId(),scheduler.getVehicleClass(),scheduler,loginInfo,accessToken);
                    }
                    if (scheduler.getCopilotUserId() != null && scheduler.getCopilotUserId() > 0) {
                        if(!iAccountBankUserTypeRelService.isUserTypeBindCard(scheduler.getCopilotUserId(),SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                            msg = "收款方"+(isUserBindCard ? "" : scheduler.getCarDriverMan()+"和")+scheduler.getCopilotMan()+"没有绑定银行卡，请提醒对方绑定银行卡， 此次交易只能使用线下付款。";
                        }
                    }
                    AdvanceChargeVo advanceChargeIn=new AdvanceChargeVo();
                    advanceChargeIn.setMasterUserId(scheduler.getCarDriverId());
                    advanceChargeIn.setVehicleAffiliation("0");//自有车资金渠道设为0
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
                    // 承包价的自有车
                    if (orderFee.getVehicleAffiliation() == null) {
                        throw new BusinessException("订单资金属性有误,请重新开单!");
                    }
                    // 个体司机
                    long amountFee = orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee();
                    if (orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {// 有实体油需要油卡号
                        // 油卡验证
                        this.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler,orderInfoExt.getOilAffiliation(),true,loginInfo);
                        Long pledgeFee = Long .parseLong(readisUtil.getSysCfg("PLEDGE_OILCARD_FEE", "0").getCfgValue());
                        // 抵押金额
                        oilCardManagementService.oilPledgeHandle(orderId, scheduler.getCarDriverId(), vehicleAffiliation, oilCardNums, pledgeFee,
                                orderFee.getTenantId(),scheduler.getVehicleClass(),scheduler,loginInfo,accessToken);
                    }
                    AdvanceChargeVo advanceChargeIn=new AdvanceChargeVo();
                    advanceChargeIn.setAmountFee(amountFee);
                    advanceChargeIn.setUserId(scheduler.getCarDriverId());
                    advanceChargeIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                    advanceChargeIn.setETCFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
                    advanceChargeIn.setVehicleAffiliation("0");//自有车资金渠道设为0
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
        orderInfoExt.setPreAmountFlag(OrderConsts.AMOUNT_FLAG.ALREADY_PAY);// 预付款 已支付
        orderFee.setPreAmountTime(getDateToLocalDateTime(new Date()));
        // 操作日志
        String userName = loginInfo.getName();
        if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
            //临时车队订单取消
            userName = "系统自动";
            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo,SysOperLogConst.OperType.Update,
                     "[" + userName + "]支付了订单预付款",accessToken, orderInfo.getOrderId());
        }else if(orderInfoExt.getPaymentWay()==null||orderInfoExt.getPaymentWay()!= OrderConsts.PAYMENT_WAY.EXPENSE){
            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                    "支付预付款，绑定油卡："+(oilCardNums==null||oilCardNums.size()==0?"无":oilCardNums.toString()),accessToken, orderInfo.getOrderId());
        }
        // 同步支付中心
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
                // 平台开票的,需要调用基础数据的接口，获取对应的平台开票的渠道
                String limitDate = readisUtil.getSysCfg("SYNC_DATE_LIMIT", "0").getCfgValue();
                //旧数据不做更换
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
                        && billForm != null) {//切换56K票据平台
                    if (billForm56K.equals(billForm)) {
                        try {
                            orderSync56KService.syncOrderInfoTo56K(orderInfoExt.getOrderId(), false, false);
                            orderSync56KService.syncOrderInfoTo56K(orderInfoExt.getOrderId(), true, false);
                        } catch (Exception e) {
                            log.error("切换56K票据平台同步失败", e);
                        }
                    }else if(SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){
                        Integer syncType = OrderConsts.SYNC_TYPE.ADD;
                        iOrderSyncTypeInfoService.saveOrderSyncTypeInfo(orderInfo.getOrderId(), syncType, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE,accessToken);
                    }
                }
                orderInfoExt.setOilAffiliation(!"0".equals(orderInfoExt.getOilAffiliation())&&!"1".equals(orderInfoExt.getOilAffiliation())
                        ?orderFee.getVehicleAffiliation():orderInfoExt.getOilAffiliation());
                OrderGoods orderGoods = iOrderGoodsService.getOrderGoods(orderInfo.getOrderId());
                // 订单限制表
                iOpAccountService.createOrderLimit(orderInfo, orderInfoExt, orderFee, orderScheduler, orderGoods, orderFeeExt,orderInfo.getTenantId());
            }
        }
    }

    /**
     * 更新订单异常费用
     *
     * @throws Exception
     */
    @Override
    public void updateOrderExceptionPrice(OrderProblemInfo orderProblemInfo, Long exceptionPrice) {
        if (exceptionPrice == null) {
            throw new BusinessException("请输入正确的费用信息");
        }
        OrderFee orderFee = orderFeeMapper.selectOne(new QueryWrapper<OrderFee>().eq("order_id",orderProblemInfo.getOrderId()));
        if (orderFee == null) {
            OrderFeeH orderFeeH  = orderFeeHMapper.selectOne(new QueryWrapper<OrderFeeH>().eq("order_id",orderProblemInfo.getOrderId()));
            if (orderFeeH != null) {
                if (orderProblemInfo.getProblemCondition() != null
                        && SysStaticDataEnum.PROBLEM_CONDITION.COST == orderProblemInfo.getProblemCondition()) {
                    Long costExceptionFee = orderFeeH.getCostExceptionFee() == null ? 0L
                            : orderFeeH.getCostExceptionFee();
                    if (exceptionPrice >= 0) { // 大于0 计入异常收入
                        orderFeeH.setExceptionIn((orderFeeH.getExceptionIn() == null ? 0 : orderFeeH.getExceptionIn())
                                + Math.abs(exceptionPrice));
                    } else {// 异常支出
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
                throw new BusinessException("未找到订单[" + orderProblemInfo.getOrderId() + "]信息!");
            }
        } else {
            if (orderProblemInfo.getProblemCondition() != null
                    && SysStaticDataEnum.PROBLEM_CONDITION.COST == orderProblemInfo.getProblemCondition()) {
                Long costExceptionFee = orderFee.getCostExceptionFee() == null ? 0L : orderFee.getCostExceptionFee();
                if (exceptionPrice >= 0) { // 大于0 计入异常收入
                    orderFee.setExceptionIn((orderFee.getExceptionIn() == null ? 0 : orderFee.getExceptionIn())
                            + Math.abs(exceptionPrice));
                } else {// 异常支出
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
            throw new BusinessException("缺少订单号！");
        }
        OrderFeeExt orderFeeExt=orderFeeExtService.getOrderFeeExt(orderId);
        if(orderFeeExt==null) {
            OrderFeeExtH orderFeeExtH=orderFeeExtHService.getOrderFeeExtH(orderId);
            if(orderFeeExtH!=null) {
                orderFeeExt=new OrderFeeExt();
                BeanUtils.copyProperties(orderFeeExtH,orderFeeExt);
            }else {
                throw new BusinessException("未找到订单号["+orderId+"]的信息");
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
            // 需要平台票
            if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
                // 指派车辆，外调车 自有车
                if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {
                    // c 端车
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

            if (billForm != null && billForm56K.equals(billForm)) {//56K票据平台
                orderSync56KService.syncOrderInfoTo56K(orderInfo.getOrderId(), isUpdate,false);
            }else if(billForm != null && SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm)){//路歌
                // TODO: 2022/3/25 同步到路歌
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
                    // 自有车
                    OrderDetailsOutDto orderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
                    OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
                    if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                        // 上一单需要平台票
                        OrderScheduler preOrderScheduler = orderDetailsOut.getOrderScheduler();
                        OrderFee preOrderFee = orderDetailsOut.getOrderFee();
                        OrderGoods preOrderGoods = orderDetailsOut.getOrderGoods();
                        OrderFeeExt preOrderFeeExt = orderDetailsOut.getOrderFeeExt();
                        OrderInfoExt orderInfoExt = orderDetailsOut.getOrderInfoExt();
                        this.syncBillFormOrder(orderInfo, preOrderGoods, preOrderScheduler,
                                preOrderFee, preOrderFeeExt, orderInfoExt, false,false);
                        return;
                    } else {
                        // 还有再上一单
                        if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                            orderId = orderInfo.getFromOrderId();
                            OrderDetailsOutDto fromOrderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
                            orderInfo = fromOrderDetailsOut.getOrderInfo();
                            if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                                // 再上一单需要平台票
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
                    // C端车 当前订单需要开票 在调用订单保存的时候，就处理了
                    if (newOrderInfo.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                        //
                        OrderDetailsOutDto orderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
                        OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
                        if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                            // 上一单需要平台票
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
     * 计算费用
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
                //实报实销(只能模式)无现金ETC
                if(preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
                    //承包价
                    cashFee = preOrderFee.getPreCashFee() == null ? 0 : preOrderFee.getPreCashFee()
                            + (preOrderFee.getArrivePaymentFee() == null ? 0 : preOrderFee.getArrivePaymentFee())
                            + (preOrderFee.getFinalFee() == null ? 0 : preOrderFee.getFinalFee())
                            +(preOrderFee.getPreEtcFee() == null ? 0 : preOrderFee.getPreEtcFee());
                }else if(preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE || preOrderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST){
                    //实报实销
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
            //外调车修改订单需同步消费金额
            OrderLimitFeeOutDto feeOut = updateOrderService.getOrderLimitFeeOut(preOrderFee.getOrderId(), preOrderFee.getPreCashFee()==null?0:preOrderFee.getPreCashFee()
                    ,  preOrderFee.getPreOilVirtualFee()==null?0:preOrderFee.getPreOilVirtualFee(), preOrderFee.getPreEtcFee()==null?0:preOrderFee.getPreEtcFee(), preOrderFee.getTenantId());
            Long entityOilMax = redisUtil.get(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + preOrderFee.getOrderId())  == null ? 0 :
                    Long.parseLong((String) redisUtil.get(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + preOrderFee.getOrderId()));


            log.info("订单["+preOrderFee.getOrderId()+"]消费金额:"+feeOut.getUseCash()
                    +"，订单未付金额:"+feeOut.getNoPayCash()+"，订单未付油:"+feeOut.getNoPayOil()
                    +"，订单消费油:"+feeOut.getUseOil()+"，订单消费ETC:"+feeOut.getUseEtc()
                    +"，订单未付ETC:"+feeOut.getNoPayEtc()
            );

            if (preOrderInfoExt.getPreAmountFlag() != null && preOrderInfoExt.getPreAmountFlag().intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                //同步支付中心金额 = 消费金额 + 未付金额
                virtualFee = (feeOut.getUseOil() == null ? 0 : feeOut.getUseOil())
                        + (feeOut.getNoPayOil() == null ? 0 : feeOut.getNoPayOil());
                //现金需要加上尾款减去保费
                cashFee = (feeOut.getUseCash() == null ? 0 : feeOut.getUseCash())
                        + (feeOut.getNoPayCash() == null ? 0 : feeOut.getNoPayCash())
                        + (preOrderFee.getArrivePaymentState() == 1 ? 0 :
                        (preOrderFee.getArrivePaymentFee() == null ? 0 : preOrderFee.getArrivePaymentFee()))
                        + (preOrderFee.getFinalFee() == null ? 0 : preOrderFee.getFinalFee())
                        - (preOrderFee.getInsuranceFee() == null ? 0 : preOrderFee.getInsuranceFee());
                etcFee =  (feeOut.getUseEtc() == null ? 0 : feeOut.getUseEtc())
                        + (feeOut.getNoPayEtc() == null ? 0 : feeOut.getNoPayEtc());
                //若是能拿到缓存最大油将使用最大油同步
                entityOilFee = entityOilMax != null ? entityOilMax : preOrderFee.getPreOilFee();
            }else{
                //同步支付中心金额 = 消费金额 + 未付金额
                virtualFee =preOrderFee.getPreOilVirtualFee() == null ? 0 : preOrderFee.getPreOilVirtualFee();
                //现金需要加上尾款减去保费
                cashFee = preOrderFee.getPreCashFee() == null ? 0 : preOrderFee.getPreCashFee()
                        + (preOrderFee.getArrivePaymentFee() == null ? 0 : preOrderFee.getArrivePaymentFee())
                        + (preOrderFee.getFinalFee() == null ? 0 : preOrderFee.getFinalFee())
                        +(preOrderFee.getPreEtcFee() == null ? 0 : preOrderFee.getPreEtcFee());
                etcFee =  (preOrderFee.getPreEtcFee() == null ? 0 : preOrderFee.getPreEtcFee());
                //若是能拿到缓存最大油将使用最大油同步
                entityOilFee = preOrderFee.getPreOilFee() == null ? 0 : preOrderFee.getPreOilFee();
            }
            //外调车现金增加异常金额
            List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),
                    orderInfo.getTenantId());
            if (list != null && list.size() > 0) {
                for (OrderProblemInfo p : list) {
                    // 需要审核通过的成本异常才需要记录
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
     * 同步支付中心数据
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
     *            如果为空，则取费用表的数据，如果不为空，则取这个值
     */
    private void synPayCenter(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler,
                              OrderFee orderFee, OrderFeeExt orderFeeExt, Long cashFee,Long entityOilFee,Long virtualFee,Long etcFee){
        if (StringUtils.isEmpty(orderFee.getVehicleAffiliation())) {
            log.error("根据开票方式的id获取开票方式返回为空,订单号：[" + orderInfo.getOrderId() + "]开票的渠道：为空");
            orderFeeExt.setResultInfo("根据开票方式的id获取开票方式返回为空");
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
        Long distance = orderScheduler.getDistance();// 单位米
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
                // 虚拟油
                totalFee=totalFee+virtualFee;
                orderInfoIn.setVirtualOilFee(virtualFee);
            }else{
                // 虚拟油
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
        //获取系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        //时区的日期和时间
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        //获取时刻
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
     * 油卡校验以及油卡操作
     * @param oilCardNums 油卡集合
     * @param orderFee
     * @param orderInfo
     * @param oilFee 预付实体油费
     */
    @Override
    public void verifyOilCardNum(List<String> oilCardNums,OrderFee orderFee,OrderInfo orderInfo
            ,long oilFee, OrderScheduler scheduler,String oilAffiliation,boolean isLastAudit,LoginInfo loginInfo) {
        //油卡余额校验
        if(oilFee>0&&(isLastAudit||oilCardNums.size()>0)) {
            if (oilCardNums == null || oilCardNums.size() <= 0) {
                throw new BusinessException("请输入油卡号");
            }
            Map verifyMap =  this.verifyOilCardNum(orderInfo.getOrderId(),oilCardNums,true,orderFee
                    .getPreOilFee());
            boolean  isEnough = DataFormat.getBooleanKey(verifyMap, "isEnough");
            if (!isEnough) {
                throw new BusinessException("油卡理论余额不足!");
            }
        }
        String oilcard = "";
        int service = 0;
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());

        //订单原先绑定的油卡
        List<OrderOilCardInfo> oilCardInfosOld = null;
        if(orderInfoExt.getPaymentWay()==null||orderInfoExt.getPaymentWay()!= OrderConsts.PAYMENT_WAY.EXPENSE) {
            oilCardInfosOld = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderInfo.getOrderId(), null);
        }

        for (String oilCardNum : oilCardNums) {
            if (StringUtils.isBlank(oilCardNum)) {
                throw new BusinessException("请输入油卡号");
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
                        throw new BusinessException("实体油卡["+oilCardNum+"]创建失败!");
                    }
                }else{
                    throw new BusinessException("实体油卡["+oilCardNum+"]不可用!");
                }
            }
            OilCardManagement oilCard = list.get(0);
            //若油卡类型为服务商油卡
            int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;

            Long balance = 0L;
            Long oilCardFee = oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;

            //供应商的卡不需判断理论金额
            if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                if (service == 1) {
                    throw new BusinessException("不能使用多张供应商卡,卡号["+oilCardNum+"]!");
                }
                service++;
                balance = oilFee;
            }else{
                //理论余额大于油费 则不需要再次输入卡号
                if (oilFee <= oilCardFee) {
                    balance = oilFee;
                }else{//理论余额小于油费 则油费减去余额 再次比较
                    balance = oilCardFee;
                }
            }
            oilFee = oilFee - balance;
            if(isLastAudit) {
                //实体油卡充值记录
                iOilEntityService.saveOilCardLog(oilCardNum, scheduler.getTenantId(), scheduler.getCarDriverId(), scheduler
                        ,orderInfo,balance,oilCard,oilAffiliation);
                //修改油卡理论金额(客户油卡/自购油卡)
                if (cardType !=  SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                    // TODO 改之前用的 loginInfo.getid
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
                    cardLog.setLogDesc(loginInfo.getName()+"使用了油卡["+oilCardNum+"]");
                    iOilCardLogService.saveOrdUpdate(cardLog, EnumConsts.OIL_LOG_TYPE.USE);
                }

                //有接单方 支付预付款油卡同步接单
                if (orderInfo.getOrderType() != null
                        && orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
                    if (orderInfo.getIsTransit() != null
                            && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES
                            && orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {// 已外发
                        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// 有归属租户
                            // TODO 改之前用的 loginInfo.getid
                            oilCardManagementService.modifyOilCardBalance(oilCardNum, balance, true,
                                    scheduler.getPlateNumber(),scheduler.getCarDriverMan(),
                                    scheduler.getOrderId(),loginInfo.getUserInfoId(),
                                    orderInfo.getToTenantId(),false,loginInfo);
                        }
                    }
                }
            }
            //记录最后一张油卡
            oilcard = oilCardNum;
            //保存订单油卡记录
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
                throw new BusinessException("油卡理论余额不足!");
            }
            //记录车辆最后油卡号
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

        //移除订单原先绑定的油卡
        if(oilCardInfosOld!=null) {
            for (OrderOilCardInfo orderOilCardInfo : oilCardInfosOld) {
                iOrderOilCardInfoService.deleteOrderOilCardInfo(orderOilCardInfo.getId());
            }
        }

    }

    @Override
    public String payArriveFee(Long orderId,String accessToken) {
        if (orderId <= 0) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        // todo
        boolean isLock = true;
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payArriveFee" + orderId, 3, 5);
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("找不到订单["+orderId+"]信息");
        }
        String msg = "Y";
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (scheduler.getCarDriverId() == null || scheduler.getCarDriverId() <= 0) {
            throw new BusinessException("未找到车辆信息,不能支付！");
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
            throw new BusinessException("车辆类型异常,不能支付！");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("订单处于修改审核中,不能支付！");
        }
        if (orderInfo.getOrderState() == SysStaticDataEnum.ORDER_STATE.CANCELLED) {
            throw new BusinessException("订单已撤销不能支付！");
        }
        if (orderInfoExt.getPreAmountFlag() != OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("未支付预付款，不能支付到付款！");
        }
        if (orderFee.getArrivePaymentState() != null && orderFee.getArrivePaymentState() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("已经支付，请不要重复操作！");
        }
        if (scheduler.getVehicleClass() != null && scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() <= 0)) {
            throw new BusinessException("未选择成本模式，不能支付！");
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
        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// 有归属租户
            // 外调车
            SysTenantDef tenantDef = iSysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(),true);
            if (tenantDef == null) {
                log.error("未找到车队信息，请联系客服！租户ID["+orderInfo.getToTenantId()+"]");
                throw new BusinessException("未找到车队信息，请联系客服！");
            }
            userId = tenantDef.getAdminUser();
        }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
            userId = scheduler.getCollectionUserId();
        } else {// 无归属车队 纯C车
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
        // 同步支付中心
        this.synPayCenterUpdateOrderOrProblemInfo(orderInfo, scheduler);
        iOrderFeeService.saveOrUpdate(orderFee);
        return msg;
    }

    @Override
    public void payArriveCharge(PayArriveChargeVo in,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (in == null) {
            throw new BusinessException("请输入入参对象");
        }
        Long userId = in.getUserId();
        Long orderId = in.getOrderId();
        Long arriveFee = in.getArriveFee();
        Long tenantId = in.getTenantId();
        List<OrderProblemInfo> problemInfos = in.getProblemInfos();
        List<OrderAgingInfo> agingInfos = in.getAgingInfos();
        log.info("支付到付款接口:userId=" + userId + "orderId：" + orderId + " arriveFee：" + arriveFee  + " tenantId：" +tenantId);
        if (userId == null || userId <= 0) {
            throw new BusinessException("请输入用户编号");
        }
        if (orderId == null || orderId <= 0L) {
            throw new BusinessException("请输入订单号");
        }
        if (arriveFee == null || arriveFee < 0L) {
            throw new BusinessException("请输入到付款金额");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("请输入租户id");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payArriveCharge" + orderId, 3, 5);
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        // 通过租户id，找到租户用户id
        SysTenantDef tenantSysTenantDef = iSysTenantDefService.getSysTenantDef(tenantId);
        if (tenantSysTenantDef == null) {
            throw new BusinessException("根据租户id" + tenantId + "没有找到租户信息");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserId(tenantSysTenantDef.getAdminUser());
        if (tenantSysOperator == null) {
            throw new BusinessException("根据用户id："+ tenantSysTenantDef.getAdminUser() + "没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("根据用户id："+ userId + "没有找到用户信息!");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        // 根据用户ID和订单号获取资金渠道
        OrderLimit ol = iOrderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("根据订单号：" + orderId + " 用户ID：" + userId + " 未找到订单限制记录");
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
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_ARRIVE_CHARGE,busiList);
        //判断是否需要抵扣异常罚款、时效罚款
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
        // 写入账户明细表并修改账户金额费用
        long soNbr = CommonUtil.createSoNbr();
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_ARRIVE_CHARGE,
                tenantSysTenantDef.getAdminUser(), tenantSysOperator.getName(),account, busiSubjectsRelList, soNbr, orderId,
                null, null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

        //应收应付逾期
        List<BusiSubjectsRel> fleetSubjectsRelList = null;
        if (tempArriveFee > 0) {
            //路歌开票 服务费 20190717  司机应收账户不记服务费
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation),
                    String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation),
                        tempArriveFee, 0L, 0L, tempArriveFee, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            //车队应付逾期
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantSysTenantDef.getAdminUser(), vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB, tempArriveFee);
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.
                        SubjectIds.PAYABLE_IN_SERVICE_FEE_4006, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }

            // 计算费用集合
            fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_ARRIVE_CHARGE, fleetBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_ARRIVE_CHARGE,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null,
                    vehicleAffiliation,loginInfo);
            //是否自动打款
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
            payoutIntf.setRemark("现金(支付到付款)");
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
                //写入payout_order
                iPayoutOrderService.createPayoutOrder(userId, tempArriveFee, OrderAccountConst.FEE_TYPE.CASH_TYPE,
                        payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }


        // 写入订单限制表和订单资金流向表
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

        // 支付到付款操作日志
        String remark = "支付到付款：" + "订单号：" + orderId + " 到付款:" + arriveFee + "抵扣异常罚款:" + exceptionFeeSum + "抵扣时效罚款:" + agingFeeSum;
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

        log.info("支付预付款接口:userId=" + userId + " vehicleAffiliation=" + vehicleAffiliation + " amountFee=" + amountFee
                + " virtualOilFee" + virtualOilFee + " oilFee=" + entityOilFee + " ETCFee=" + ETCFee + " 订单号="+ orderId+ " 油账户类型="+ oilAccountType + " 油票据类型="+ oilBillType);
        if (userId < 0) {
            throw new BusinessException("请输入用户编号");
        }
        if (tenantId == null || tenantId <= -1L) {
            throw new BusinessException("请输入租户id");
        }
        if (payType <= 0) {
            throw new BusinessException("请输入支付方式");
        }
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("请输入订单资金渠道");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        if (oilConsumer == null || oilConsumer < OrderConsts.OIL_CONSUMER.SELF) {
            throw new BusinessException("请输入订单油费消费方式");
        }
        if (oilAccountType == null || oilAccountType < OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
            throw new BusinessException("请输入订单油费油来源账户类型");
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
            throw new BusinessException("请输入订单油费油票类型");
        }
        Date date = new Date();
        // 通过租户id，找到租户用户id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        // TODO: 2022/3/28
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payAdvanceCharge" + orderId, 3, 5);
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        OrderOilSource thisSourceList = null;//本单付出去油
        List<OrderOilSource> sourceList = null;
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            throw new BusinessException("根据订单号：" + orderId + " 没有找到订单信息");
        }
        if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
            isOwnCarUser = true;
        }
        OrderLimit limit = iOrderLimitService.getOrderLimit(userId, orderId, tenantId,-1,loginInfo);
        if (limit == null) {
            throw new BusinessException("根据订单号：" + orderId + "用户ID：" + userId + "租户id：" + tenantId + "未找到订单限制记录");
        }
        long soNbr = CommonUtil.createSoNbr();
        long tempVirtualOilFee = virtualOilFee;
        //普通支付方式
        if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.ORDINARY_PAY) {
            //车队账户上的虚拟油只能给自己的自有车
            //如果选择客户油，就只能使用客户油分配
            if (virtualOilFee > 0 && isOwnCarUser && oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                OrderAccountBalanceDto oilBalance = iOrderAccountService.getOrderAccountBalance(tenantUserId, "oilBalance", tenantId, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                OrderAccountOutVo orderAccountOut =  oilBalance.getOa();
                if(null == orderAccountOut){
                    throw new BusinessException("查询车队油账户错误");
                }
                //车队可用油账户余额
                Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
                if(null == canUseOilBalance){
                    canUseOilBalance = 0L;
                }
                if (canUseOilBalance.longValue() > 0) {
                    if (virtualOilFee > canUseOilBalance.longValue()) {
                        sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                        if (sourceList == null || sourceList.size() <= 0) {
                            throw new BusinessException("车队油账户分配出错");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceList) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != canUseOilBalance) {
                                throw new BusinessException("充值油与车队账户油分配不一致");
                            }
                        }
                        tempVirtualOilFee -= canUseOilBalance;
                    } else {
                        sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(virtualOilFee, tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                        if (sourceList == null || sourceList.size() <= 0) {
                            throw new BusinessException("车队油账户分配出错");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceList) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != virtualOilFee) {
                                throw new BusinessException("充值油与车队账户油分配不一致");
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
        } else if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_TEMPORARY_FLEET) {//支付给临时车队
            OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(userId, orderId, orderId, virtualOilFee, virtualOilFee, 0L, tenantId,getDateToLocalDateTime(date), loginInfo.getId(), isNeedBill, vehicleAffiliation,limit.getOrderDate(),oilAffiliation,oilConsumer,
                    0L,0L,0L,0L,0L,0L,limit.getUserType(),oilAccountType,oilBillType,loginInfo);
            thisSourceList = oos;
        } else if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.TEMPORARY_FLEET_PAY_DRIVER) {//临时车队支付给司机
            Long sourceOrderId = limit.getSourceOrderId();
            if (sourceOrderId == null || sourceOrderId <= 0) {
                throw new BusinessException("没有找到用户信息!");
            }
            OrderLimit sourceOrderlimit = iOrderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, sourceOrderId,-1);
            if (sourceOrderlimit == null) {
                throw new BusinessException("根据订单号：" + sourceOrderId + "用户ID：" + tenantUserId + "未找到订单限制记录");
            }
            sourceOrderlimit.setPaidOil(entityOilFee);
            iOrderLimitService.saveOrUpdate(limit);
            String sourceVehicleAffiliation = sourceOrderlimit.getVehicleAffiliation();
            String sourceOilAffiliation = sourceOrderlimit.getVehicleAffiliation();
            Long sourceTenantId = sourceOrderlimit.getTenantId();
            Integer sourceIsNeedBill = sourceOrderlimit.getIsNeedBill();
            OrderOilSource orderOilSource = iOrderOilSourceService.getOrderOilSource(tenantUserId, sourceOrderId, sourceOrderId, sourceVehicleAffiliation, sourceTenantId,-1,sourceOrderlimit.getOilAccountType(),sourceOrderlimit.getOilConsumer(),sourceOrderlimit.getOilBillType(),sourceOilAffiliation);
            if (orderOilSource == null) {
                throw new BusinessException("根据订单号：" + sourceOrderId + "用户ID：" + tenantUserId + "租户ID：" + sourceTenantId + "资金渠道：" + sourceVehicleAffiliation + " 油账户类型：" + sourceOrderlimit.getOilAccountType() + " 油消费类型：" + sourceOrderlimit.getOilConsumer() + " 油开票类型：" + sourceOrderlimit.getOilBillType() + " 未找到订单油来源记录");
            }
            long noPayOil = (orderOilSource.getNoPayOil() == null ? 0L : orderOilSource.getNoPayOil());
            long entityOil = (sourceOrderlimit.getOrderEntityOil() == null ? 0L : sourceOrderlimit.getOrderEntityOil());
            if ((noPayOil != virtualOilFee ) || ( entityOil != entityOilFee)) {
                throw new BusinessException("来源订单油与要给出去的订单油不一致");
            }
            OrderAccount sourceOilAccount = iOrderAccountService.queryOrderAccount(tenantUserId, sourceVehicleAffiliation, 0L, sourceTenantId,sourceOilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            //新的OrderOilSource
            List<OrderOilSource> orderOilSourceList = new ArrayList<>();
            orderOilSourceList.add(orderOilSource);
            MatchAmountUtil.matchAmounts(virtualOilFee, 0, 0, "noPayOil",
                    "noRebateOil","noCreditOil", orderOilSourceList);
            sourceList = iOrderAccountService.dealTemporaryFleetOil(sourceOilAccount, orderOilSource, tenantUserId, orderId, tenantId, sourceIsNeedBill, sourceVehicleAffiliation, userId, EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,accessToken);
        } else if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER) {//支付给代收、司机（油费）
            if (orderScheduler.getIsCollection() == null || orderScheduler.getIsCollection() != OrderConsts.IS_COLLECTION.YES) {
                throw new BusinessException("订单不是代收模式，不能使用代收方式支付");
            }
            if (collectionDriverUserId == null || collectionDriverUserId < 1) {
                throw new BusinessException("代收订单司机用户id不能为空");
            }
            SysUser sysCollectionDriverOperator = iSysOperatorService.getSysOperatorByUserId(collectionDriverUserId);
            if (sysCollectionDriverOperator == null) {
                throw new BusinessException("根据用户id" + collectionDriverUserId + "没有找到用户信息!");
            }
            OrderLimit limitDriver = iOrderLimitService.getOrderLimit(collectionDriverUserId, orderId, tenantId,-1,loginInfo);
            if (limitDriver == null) {
                throw new BusinessException("根据订单号：" + orderId + "用户ID：" + collectionDriverUserId + "租户id：" + tenantId + "未找到订单限制记录");
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

            // 计算费用集合
            List<BusiSubjectsRel> collectionDriverRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, collectionDriverBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), collectionDriverAccount, collectionDriverRelList, soNbr, orderId,
                    sysCollectionDriverOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(collectionDriverUserId, sysCollectionDriverOperator.getBillId(), EnumConsts.PayInter.BEFORE_PAY_CODE, orderId, amountFee + virtualOilFee + entityOilFee + ETCFee, vehicleAffiliation, "");
            iOrderOilSourceService.busiToOrder(parametersNewDto, collectionDriverRelList,loginInfo);
        }

        OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,limit.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();

        //应收逾期
        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, amountFee);
        busiList.add(amountFeeSubjectsRel);
        //路歌开票 服务费 20190717 司机应收账户不记服务费
        long serviceFee = 0;
        boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
        if (isLuge) {
            Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), amountFee, 0L, 0L, amountFee, tenantId,null);
            serviceFee = (Long) result.get("lugeBillServiceFee");
        }
        if (payType != OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER) {//支付给代收、司机（油费）
            BusiSubjectsRel OilFeeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_Entity_SUB, entityOilFee);
            busiList.add(OilFeeSubjectsRelEntity);
            BusiSubjectsRel consumeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB, entityOilFee);
            busiList.add(consumeSubjectsRelEntity);
            BusiSubjectsRel OilFeeSubjectsRelVirtual = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_Virtual_SUB, virtualOilFee);
            busiList.add(OilFeeSubjectsRelVirtual);
        }
        BusiSubjectsRel amountETCFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_ETC_SUB, ETCFee);
        busiList.add(amountETCFeeSubjectsRel);
        //共享油费母卡充值油分配
        if (virtualOilFee > 0 && oilConsumer == OrderConsts.OIL_CONSUMER.SHARE ) {
            Integer distributionType = null;
            long tempUserId = userId;
            if (payType == OrderAccountConst.PAY_ADVANCE_TYPE.FLEET_PAY_COLLECTION_DRIVER) {
                tempUserId = collectionDriverUserId;
            }
            if (isOwnCarUser) {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                    if (tempVirtualOilFee > 0) {//分配返利油，转移油
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                        OrderOilSource oos = iOrderOilSourceService.saveOrderOilSource(userId, orderId, orderId, tempVirtualOilFee, tempVirtualOilFee, 0L, tenantId,getDateToLocalDateTime(date), loginInfo.getId(), isNeedBill, vehicleAffiliation,limit.getOrderDate(),oilAffiliation,oilConsumer,
                                0L,0L,0L,0L,0L,0L,limit.getUserType(),oilAccountType,oilBillType,loginInfo);
                        thisSourceList = oos;
                    }
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        throw new BusinessException("自有车油费不能获取运输专票");
                    }
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                }
            } else {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {//返利油，转移油
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//获取油票
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                    } else if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//获取运输专票
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
                    throw new BusinessException("订单油来源关系为空");
                }
                if (noPayOil == null) {
                    throw new BusinessException("充值现金不能为空");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("返利不能为空");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("授信不能为空");
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

        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList);
        // 写入账户明细表并修改账户金额费用
        OrderResponseDto param = new OrderResponseDto();
        param.setSourceList(sourceList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,loginInfo);

        //车队应付逾期
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB, amountFee);
        fleetBusiList.add(payableOverdueRel);
        if (serviceFee > 0) {
            BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4000, serviceFee);
            fleetBusiList.add(payableServiceFeeSubjectsRel);
        }
        //56附加运费处理 20191014
        long appendFreight = 0;
        boolean is56k = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K));
        if (is56k) {
            AdditionalFee af = this.deal56kAppendFreight(advanceChargeIn, tenantId,accessToken);
            if (af != null) {
                appendFreight = af.getAppendFreight() == null ? 0L : af.getAppendFreight();
            }
            //抵扣运输专票
            if (oilBillType == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2 && virtualOilFee > 0) {
                appendFreight = 0;
            }
            if (appendFreight > 0) {
                BusiSubjectsRel appendFreightSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_ADDITIONAL_FEE_4019, appendFreight);
                fleetBusiList.add(appendFreightSubjectsRel);
                iAdditionalFeeService.saveOrUpdate(af);
            }
        }
        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

        //生成待打款支付记录
        //是否自动打款
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
            payoutIntf.setRemark("现金(支付预付款)");
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
                //写入payout_order
                iPayoutOrderService.createPayoutOrder(userId, amountFee, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }
        //自有车订单勾选了平台开票，在支付预付款时，同时操作以下流水：
        //触发车队的平安虚拟对公账户往HA账户打款，打款的金额=（补贴或现金 ）+ 油费(虚拟+实体) + （路桥费|ETC）+ 尾款，系统再触发HA往委托的账户打款（支付中心）
        if (isOwnCarUser && isNeedBill == OrderAccountConst.ORDER_BILL_TYPE.platformBill) {
            if (totalFee > 0) {
                this.payOwnCarBillAmount(orderId, totalFee, userId, tenantId, soNbr,accessToken);
            }
        }
        // 写入订单限制表和订单资金流向表
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
            throw new BusinessException("入参不合法");
        }
        Long orderId = advanceChargeIn.getOrderId();
        String vehicleAffiliation = advanceChargeIn.getVehicleAffiliation();
        boolean is56k = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K));
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("请输入订单号");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("请输入租户id");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderGoods orderGoods = iOrderGoodsService.getOrderGoods(orderId);
        if (orderInfo == null) {
            throw new BusinessException("根据订单号：" + orderId + "未找到订单信息");
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
            //获取车队默认的收款私户
            Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
            AccountBankRel bank = iAccountBankRelService.getDefaultAccountBankRel(tenantUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            if (bank == null) {
                throw new BusinessException("根据超管id：" + tenantUserId +  "用户角色： " + SysStaticDataEnum.USER_TYPE.ADMIN_USER + " 未找到绑定的默认收款私户");
            }
            af.setReceivablesBankAccName(bank.getAcctName());
            af.setReceivablesBankAccNo(bank.getAcctNo());
        }
        return af;
    }

    @Override
    public void payOwnCarBillAmount(Long orderId, Long totalFee, Long userId, Long tenantId, long soNbr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // 通过租户id，找到租户用户id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        SysTenantDef tenantSysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(tenantUserId);
        if (tenantSysTenantDef == null) {
            throw new BusinessException("根据用户id：" + tenantUserId + " 未找到租户信息");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        //是否自动打款
        boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(tenantId);
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        String vehicleAffiliation = orderFee.getVehicleAffiliation(); //开票资金渠道
        String oilAffiliation = vehicleAffiliation;
        //判断收款账户是车队，还是司机
        BillPlatform bpf = billPlatformService.queryBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
        if (bpf == null) {
            throw new BusinessException("根据开票平台：" + vehicleAffiliation + " 未找到开票平台信息");
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
        if (receivables == 1) {//车队收款
            //生成账户流水
            List<BusiSubjectsRel> subjectsList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel receiveSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE, totalFee);
            subjectsList.add(receiveSubjectsRel);
            BusiSubjectsRel paySubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_PAY_TOTAL_FEE, totalFee);
            subjectsList.add(paySubjectsRel);
            //路歌开票 服务费 20190717
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
            // 20190727 会员体系改造，所有付款给自己的账户也记应收应付
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), fleetAccount, haRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            //车队应收应付
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.HAVIR, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, totalFee, tenantId, String.valueOf(vehicleAffiliation), orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE,String.valueOf(oilAffiliation),SysStaticDataEnum.USER_TYPE.ADMIN_USER,SysStaticDataEnum.USER_TYPE.ADMIN_USER,serviceFee,accessToken);
            payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
            payoutIntf.setRemark("平台开票");
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            payoutIntf.setBusiCode(String.valueOf(orderId));
            if (orderScheduler != null) {
                payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
            }
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);
            //虚拟油
            List<PayoutOrder> payoutOrderList = new ArrayList<PayoutOrder>();
            //现金+尾款
            PayoutOrder payoutOrder = iPayoutOrderService.createPayoutOrder(tenantUserId, totalFee, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            payoutOrderList.add(payoutOrder);
            long payoutOrderTotalFee = 0;
            for (PayoutOrder p : payoutOrderList) {
                payoutOrderTotalFee += (p.getAmount() == null ? 0L : p.getAmount());
            }
            if (payoutOrderTotalFee != totalFee) {
                throw new BusinessException("订单总运费不等于各项费用之和");
            }
        } else if (receivables == 2) {//司机收款
            //司机应收
            OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel receivableSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE, totalFee);
            busiList.add(receivableSubjectsRel);
            //路歌开票 服务费 20190717
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = iBillAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), totalFee, 0L, 0L, totalFee, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList);
            // 写入账户明细表并修改账户金额费用
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

            //车队应付
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_PAYABLE, totalFee);
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4000, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }
            // 计算费用集合
            List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

            //打款记录
            PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, totalFee, -1L, String.valueOf(vehicleAffiliation), orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE,String.valueOf(oilAffiliation),SysStaticDataEnum.USER_TYPE.ADMIN_USER,SysStaticDataEnum.USER_TYPE.DRIVER_USER,serviceFee,accessToken);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("平台开票");
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
                throw new BusinessException("订单总运费不等于各项费用之和");
            }
        } else {
            throw new BusinessException("自有车开平台票，未找到收款人是车队还是司机配置");
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

        log.info("支付预付款接口:mainUserId=" + masterUserId +  "entiyOilFee=" + entiyOilFee
                + "fictitiousOilFee=" + fictitiousOilFee + "bridgeFee=" + bridgeFee
                + "masterSubsidy=" + masterSubsidy + "slaveSubsidy=" + slaveSubsidy + "slaveUserId=" + slaveUserId + " 油账户类型="+ oilAccountType + " 油票据类型="+ oilBillType);
        Date date = new Date();
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("请输入订单资金渠道");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        if (oilConsumer == null || oilConsumer < 1) {
            throw new BusinessException("请输入订单油费消费方式");
        }
        if (oilAccountType == null || oilAccountType < OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
            throw new BusinessException("请输入订单油费油来源账户类型");
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
            throw new BusinessException("请输入订单油费油票类型");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[] { DateUtil.formatDate(date, DateUtil.YEAR_MONTH_FORMAT2) });
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payAdvanceChargeToOwnCar" + orderId, 3, 5);
        // TODO: 2022/3/28
        boolean isLock = true;
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        // 通过租户id，找到租户用户id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过手机获取用户信息
        //UserDataInfo mainUser = userSV.getUserDataInfo(masterUserId);masterUserId
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(masterUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        OrderOilSource thisSourceList = null;//本单付出去油
        List<OrderOilSource> sourceList = null;
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            throw new BusinessException("根据订单号：" + orderId + " 没有找到订单信息");
        }
        if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
            isOwnCarUser = true;
        }
        OrderLimit limit = iOrderLimitService.getOrderLimit(masterUserId, orderId, tenantId,-1,loginInfo);
        if (limit == null) {
            throw new BusinessException("根据订单号：" + orderId + "用户ID：" + masterUserId + "租户id：" + tenantId + "未找到订单限制记录");
        }
        long tempVirtualOilFee = fictitiousOilFee;
        //车队账户上的虚拟油只能给自己的自有车
        if (fictitiousOilFee > 0 && isOwnCarUser && oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
            OrderAccountBalanceDto oilBalance = iOrderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",tenantId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut =  oilBalance.getOa();;
            if(null == orderAccountOut){
                throw new BusinessException("查询车队油账户错误");
            }
            //车队可用油账户余额
            Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
            if(null == canUseOilBalance){
                canUseOilBalance = 0L;
            }
            if (canUseOilBalance.longValue() > 0) {
                if (fictitiousOilFee > canUseOilBalance.longValue()) {
                    sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, masterUserId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                    if (sourceList == null || sourceList.size() <= 0) {
                        throw new BusinessException("车队油账户分配出错");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceList) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != canUseOilBalance) {
                            throw new BusinessException("充值油与车队账户油分配不一致");
                        }
                    }
                    tempVirtualOilFee -= canUseOilBalance;
                } else {
                    sourceList = iOrderOilSourceService.matchOrderAccountToOrderLimit(fictitiousOilFee, tenantUserId, orderId,tenantId, isNeedBill, vehicleAffiliation, masterUserId,EnumConsts.PayInter.BEFORE_PAY_CODE,EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT,loginInfo);
                    if (sourceList == null || sourceList.size() <= 0) {
                        throw new BusinessException("车队油账户分配出错");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceList) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != fictitiousOilFee) {
                            throw new BusinessException("充值油与车队账户油分配不一致");
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
        // 根据用户ID和资金渠道类型获取账户信息
        OrderAccount account = iOrderAccountService.queryOrderAccount(sysOperator.getUserInfoId(), vehicleAffiliation,0L, tenantId,oilAffiliation,limit.getUserType());
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        // 查询订单信息

        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountEntiyOilFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_ENTIY_OIL_FEE, entiyOilFee);
        busiList.add(amountEntiyOilFeeSubjectsRel);
        BusiSubjectsRel consumeSubjectsRelEntity = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CONSUME_BEFORE_ENTIY_OIL_FEE, entiyOilFee);
        busiList.add(consumeSubjectsRelEntity);
        BusiSubjectsRel amountFictitiousOilFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE, fictitiousOilFee);
        busiList.add(amountFictitiousOilFeeSubjectsRel);
        BusiSubjectsRel amountBridgeFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFOREPAY_BRIDGE, bridgeFee);
        busiList.add(amountBridgeFeeSubjectsRel);
        //应收逾期
        BusiSubjectsRel amountMasterSubsidySubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, masterSubsidy);
        busiList.add(amountMasterSubsidySubjectsRel);
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList);
        long soNbr = CommonUtil.createSoNbr();
        //共享油费母卡充值油分配
        if (fictitiousOilFee > 0 && oilConsumer == OrderConsts.OIL_CONSUMER.SHARE ) {
            Integer distributionType = null;
            if (isOwnCarUser) {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                    if (tempVirtualOilFee > 0) {//分配返利油，转移油
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                    }
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        throw new BusinessException("自有车油费不能获取运输专票");
                    }
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                }
            } else {
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {//返利油，转移油
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
                }
                if (oilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                    if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//获取油票
                        distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                    } else if (oilBillType ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//获取运输专票
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
                    throw new BusinessException("订单油来源关系为空");
                }
                if (noPayOil == null) {
                    throw new BusinessException("充值现金不能为空");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("返利不能为空");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("授信不能为空");
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
        // 计算费用集合
        // 写入账户明细表并修改账户金额费用
        OrderResponseDto param = new OrderResponseDto();
        param.setSourceList(sourceList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE,
                EnumConsts.PayInter.BEFORE_PAY_CODE, tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account,
                busiSubjectsRelList, soNbr, orderId, sysOperator.getName(), null, tenantId, null, "",param,vehicleAffiliation,loginInfo);

        //车队应付逾期
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRelMaster = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB, masterSubsidy);
        fleetBusiList.add(payableOverdueRelMaster);
        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        //生成待打款支付记录
        //是否自动打款
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
            payoutIntf.setRemark("主驾驶补贴(支付预付款)");
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
                //写入payout_order
                iPayoutOrderService.createPayoutOrder(masterUserId, masterSubsidy, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }
        //自有车订单勾选了平台开票，在支付预付款时，同时操作以下流水：
        //触发车队的平安虚拟对公账户往HA账户打款，打款的金额=（补贴或现金 ）+ 油费(虚拟+实体) + （路桥费|ETC）+ 尾款，系统再触发HA往委托的账户打款（支付中心）
        if (isOwnCarUser && isNeedBill == OrderAccountConst.ORDER_BILL_TYPE.platformBill) {
            if (totalFee > 0) {
                this.payOwnCarBillAmount(orderId, totalFee, masterUserId, tenantId, soNbr,accessToken);
            }
        }
        // 写入订单限制表和订单资金流向表
        ParametersNewDto parametersNewDto = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.BEFORE_PAY_CODE, orderId, entiyOilFee + fictitiousOilFee + bridgeFee + masterSubsidy + slaveSubsidy, vehicleAffiliation, "");
        parametersNewDto.setTotalFee(String.valueOf(entiyOilFee + fictitiousOilFee + bridgeFee + masterSubsidy + slaveSubsidy));
        parametersNewDto.setTenantUserId(tenantUserId);
        parametersNewDto.setTenantBillId(tenantSysOperator.getBillId());
        parametersNewDto.setTenantUserName(tenantUser.getLinkman());
        busiSubjectsRelList.addAll(fleetSubjectsRelList);
        iOrderOilSourceService.busiToOrder(parametersNewDto, busiSubjectsRelList,loginInfo);
        // 副驾驶司机补贴
        if (slaveUserId > 0) {
            OrderLimit slaveLimit = iOrderLimitService.getOrderLimit(slaveUserId, orderId, tenantId,-1,loginInfo);
            if (slaveLimit == null) {
                throw new BusinessException("根据订单号：" + orderId + "用户ID：" + slaveUserId + "租户id：" + tenantId + "未找到订单限制记录");
            }
            // 通过userID获取用户信息
            //UserDataInfo user = userSV.getUserDataInfo(slaveUserId);
            SysUser sysOperator1 = iSysOperatorService.getSysOperatorByUserIdOrPhone(slaveUserId, null, 0L);
            if (sysOperator1 == null) {
                throw new BusinessException("没有找到副驾驶用户信息!");
            }
            // 根据用户ID和资金渠道类型获取账户信息
            OrderAccount account1 = iOrderAccountService.queryOrderAccount(sysOperator1.getUserInfoId(), vehicleAffiliation,0L, tenantId,oilAffiliation,slaveLimit.getUserType());
            // 预付款费用（副驾驶）
            List<BusiSubjectsRel> busiList1 = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel amountSlaveSubsidySubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, slaveSubsidy);
            busiList1.add(amountSlaveSubsidySubjectsRel);
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList1 = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, busiList1);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE, tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(),
                    account1, busiSubjectsRelList1, soNbr, orderId, sysOperator1.getName(), null,tenantId, null, "", param,vehicleAffiliation,loginInfo);
            //车队应付逾期
            fleetBusiList.clear();
            BusiSubjectsRel payableOverdueRelSlave = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB, slaveSubsidy);
            fleetBusiList.add(payableOverdueRelSlave);
            // 计算费用集合
            List<BusiSubjectsRel> fleetSubjectsRelList1 = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.BEFORE_PAY_CODE, fleetBusiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.BEFORE_PAY_CODE,
                    sysOperator1.getUserInfoId(), sysOperator1.getName(), fleetAccount, fleetSubjectsRelList1, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            if (slaveSubsidy > 0) {
                PayoutIntf payoutIntf1 = iPayoutIntfService.createPayoutIntf(slaveUserId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, slaveSubsidy, -1L, vehicleAffiliation, orderId,
                        tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,slaveLimit.getUserType(),0L,accessToken);
                payoutIntf1.setObjId(Long.valueOf(sysOperator1.getBillId()));
                payoutIntf1.setRemark("副驾驶补贴(支付预付款)");
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
                    //写入payout_order
                    iPayoutOrderService.createPayoutOrder(slaveUserId, slaveSubsidy, OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf1.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                }
            }
            // 写入订单限制表和订单资金流向表
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
            throw new BusinessException("订单号不能为空！");
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
                throw new BusinessException("未找到订单["+orderId+"]信息");
            }
        }
        return "Y";
    }

    @Override
    public void verifyFeeOut(OrderScheduler orderScheduler, OrderFee orderFee, OrderInfoExt orderInfoExt, Long price, Long agingId, Long problemId) {
        //未调度的单 外调车/自有车承包价
        if(orderScheduler.getVehicleClass() == null || (( orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                || (orderScheduler.getVehicleClass()==SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT
        ))){
            Long agingFee = 0L;
            //agingId > 0 为修改  时效修改时不需记录该时效罚款
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
            //成本扣减项  + 时效罚款
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
            //大于到付+尾款费用
            if (fee > sumFee) {
                throw new BusinessException("扣减金额不能超过订单"+(arrivePaymentState ? "" : "到付款加")+"尾款金额!");
            }
        }
    }

    @Override
    public void cancelDriverSwitchSubsidy(Long orderId,LoginInfo loginInfo,String token) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
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
            throw new BusinessException("订单号不能为空！");
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
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("业务审核类型不能为空！");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        boolean isNotAudit = false; //业务数据不能审核
        try {
            AuditCallbackDto sure = auditSettingService.sure(auditCode, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
        } catch (BusinessException e) {
            if ("该业务数据不能审核".equals(e.getMessage())) {
                isNotAudit = true;
            }else{
                throw new BusinessException(e.getMessage());
            }
        }

        if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {

        }else if(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)){

        }else if(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)){
            if (!load && !receipt) {
                throw new BusinessException("回单审核状态异常！");
            }
            iOrderReceiptService.verifyRece(String.valueOf(orderId), load, receipt, null, null, OrderConsts.RECIVE_TYPE.SINGLE, accessToken);
        }else{
            throw new BusinessException("业务审核类型异常！");
        }

        // 不能审核
        if (isNotAudit) {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderId);
            params.put("auditCode", auditCode);
            iAuditService.startProcess(auditCode, orderId,
                    SysOperLogConst.BusiCode.OrderInfo, params, accessToken, tenantId);
        }
    }

    /**
     * 订单同步支付中心(历史订单)
     * @param orderInfoH
     * @param orderSchedulerH
     * @throws Exception
     */
    public void synPayCenterOrderH(OrderInfoH orderInfoH, OrderSchedulerH orderSchedulerH,String accessToken) {
        // 需要平台票
        if (orderInfoH.getIsNeedBill() != null && orderInfoH.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderInfoH.getOrderId());
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderInfoH.getOrderId());
            OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH(orderInfoH.getOrderId());
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderInfoH.getOrderId());
            if (orderSchedulerH.getVehicleClass() != null && orderSchedulerH.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (orderInfoExtH.getPreAmountFlag() !=null &&
                        orderInfoExtH.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    //修改过订单的自有车不需同步
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
//                if (billForm != null && (billForm56K.equals(billForm) || SysStaticDataEnum.BILL_FORM.BILL_Luge.equals(billForm))) {//56K票据平台
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
     * 同步支付中心数据
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
     *            如果为空，则取费用表的数据，如果不为空，则取这个值
     */
//    private void synPayCenterH(OrderInfoH orderInfo, OrderGoodsH orderGoods, OrderSchedulerH orderScheduler,
//                               OrderFeeH orderFee, OrderFeeExtH orderFeeExt, Long cashFee,Long entityOilFee,Long virtualFee,Long etcFee,String accessToken){
//        if (StringUtils.isEmpty(orderFee.getVehicleAffiliation())) {
//            log.error("根据开票方式的id获取开票方式返回为空,订单号：[" + orderInfo.getOrderId() + "]开票的渠道：为空");
//            orderFeeExt.setResultInfo("根据开票方式的id获取开票方式返回为空");
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
//            log.error("根据租户id和开票方式获取支付中心的账号密码返回为空,订单号：[" + orderInfo.getOrderId() + "]开票的渠道：["
//                    + orderFee.getVehicleAffiliation() + "]，租户id[" + orderInfo.getTenantId() + "]");
//            orderFeeExt.setResultInfo("根据租户id和开票方式获取支付中心的账号密码返回为空");
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
//        Long distance = orderScheduler.getDistance();// 单位米
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
//                    //实报实销无现金ETC
//                    if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT){
//                        //承包价
//                        cashFee = orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee()
//                                + (orderFee.getFinalFee() == null ? 0 : orderFee.getFinalFee())
//                                +(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
//                    }else if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE || orderInfoExt.getPaymentWay() == PAYMENT_WAY.COST){
//                        //实报实销
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
//                // 虚拟油
//                totalFee=totalFee+virtualFee;
//                orderInfoIn.setVirtualOilFee(virtualFee);
//            }else{
//                // 虚拟油
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
//            // 同步支付中心，并且更新同步结果
//            OrderInfoResultInfo infoResultInfo = null;
//            orderFeeExt.setExecDate(LocalDateTime.now());
//            try {
//                infoResultInfo = payOutIntf.syncOrderInfo(orderInfoIn);
//            } catch (Exception e) {
//                log.error("同步支付中心失败，订单[" + orderInfo.getOrderId() + "]", e);
//                orderFeeExt.setResultInfo("调用支付中心失败");
//            }
//
//            if (infoResultInfo != null) {
//                if (OrderConsts.PAY_CENTER.CODE_SUCESS.equals(infoResultInfo.getCode())) {
//                    orderFeeExt.setXid(String.valueOf(infoResultInfo.getId()));
//                    orderFeeExt.setResultCode(OrderConsts.PAY_CENTER.CODE_SUCESS);
//                    orderFeeExt.setResultInfo("同步成功");
//                } else {
//                    orderFeeExt.setResultCode(infoResultInfo.getCode());
//                    orderFeeExt.setResultInfo(infoResultInfo.getMsg());
//                }
//            }
//        }else{
//            orderFeeExt.setResultInfo("订单总费用为0，不同步支付中心！");
//        }
//        orderFeeExtHService.saveOrUpdate(orderFeeExt);
//
//    }

    /***
     * 校验油卡理论金额是否足够
     */
    public Map verifyOilCardNum(Long orderId,List<String> oilCardNums,boolean isPay,Long oilFee){
        if (oilCardNums == null || oilCardNums.size() <= 0) {
            throw new BusinessException("请输入油卡号");
        }
        long count = oilCardNums.stream().distinct().count();
        boolean isRepeat = count < oilCardNums.size();
        if (isRepeat) {
            throw new BusinessException("输入的油卡号重复!");
        }
        Map map = new ConcurrentHashMap();
        boolean isEnough = false;
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        if (orderFee == null) {
            throw new BusinessException("找不到订单["+orderId+"]信息");
        }
        List<OilCardManagement> listOut = new ArrayList<OilCardManagement>();
        if (oilFee == null || oilFee <= 0) {
            oilFee = orderFee.getPreOilFee();
        }
        for (String oilCardNum : oilCardNums) {
            Long oilCardFee = 0L;
            Boolean isEquivalenceCard = false;
            if (!isPay) {//不是支付中 支付中会增加油卡以防叠加
                //若是卡号跟等值卡相等 需加上等值卡金额
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
                if (isEquivalenceCard) {//卡号是等值卡
                    //没添加卡是初始化
                    oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                    oilCard.setOilCarNum(oilCardNum);
                }else{
                    if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.YES) {
                        oilCard.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
                        oilCard.setOilCarNum(oilCardNum);
                    }else{
                        throw new BusinessException("实体油卡["+oilCardNum+"]不可用!");
                    }
                }
            }else{
                oilCard = list.get(0);
            }
            //若油卡类型为服务商油卡
            int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
            oilCard.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", cardType + ""));
            oilCardFee += oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;
            //重新再初始化理论余额
            oilCard.setCardBalance(oilCardFee);
            oilCard.setIsNeedWarn(false);
            //供应商的卡不需判断理论金额
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
                        throw new BusinessException("油卡["+oilCardNum+"]理论余额为0不可用!");
                    }
                }
                //理论余额大于油费 则不需要再次输入卡号
                if (oilFee <= oilCardFee) {
                    isEnough = true;
                    listOut.add(oilCard);
                }else{//理论余额小于油费 则油费减去余额 再次比较
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
     * 修改订单(异常审核通过)同步支付中心
     */
    @Override
    public void synPayCenterUpdateOrderOrProblemInfo(OrderInfo orderInfo, OrderScheduler orderScheduler) {
        // 需要平台票
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            OrderFee preOrderFee = iOrderFeeService.getOrderFee(orderInfo.getOrderId());
            OrderInfoExt preOrderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());
            OrderGoods preOrderGoods = iOrderGoodsService.getOrderGoods(orderInfo.getOrderId());
            OrderFeeExt preOrderFeeExt = orderFeeExtService.getOrderFeeExt(orderInfo.getOrderId());
            if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (preOrderInfoExt.getPreAmountFlag() !=null &&
                        preOrderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    //修改过订单的自有车不需同步
                    return;
                }
            }
            this.syncBillFormOrder(orderInfo, preOrderGoods, orderScheduler,
                    preOrderFee, preOrderFeeExt, preOrderInfoExt, true,true);
        }
    }

    /**
     * 支付切换司机补贴
     */
    public String paySubsidy(Long orderId, LoginInfo loginInfo,String token){
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
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
     * 记录日志
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
