package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOilCardVehicleRelService;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.dto.OilCardSaveInDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.order.GetOilCardByCardNumDto;
import com.youming.youche.order.dto.order.OilCardPledgeOrderListDto;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.vo.GetOilCardByCardNumVo;
import com.youming.youche.order.provider.mapper.OilCardManagementMapper;
import com.youming.youche.order.provider.mapper.OilCardVehicleRelMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.vo.OilCardPledgeOrderListVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.youming.youche.conts.SysStaticDataEnum.OILCARD_STATUS.STATUS_CB;
import static com.youming.youche.conts.SysStaticDataEnum.OILCARD_STATUS.STATUS_LOSS;
import static com.youming.youche.conts.SysStaticDataEnum.OILCARD_STATUS.STATUS_YES;
import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD;


/**
* <p>
    * 油卡管理表 服务实现类
    * </p>
* @author liangyan
* @since 2022-03-07
*/
@DubboService(version = "1.0.0")
@Service
public class OilCardManagementServiceImpl extends BaseServiceImpl<OilCardManagementMapper, OilCardManagement> implements IOilCardManagementService {
    private static final Logger log = LoggerFactory.getLogger(OilCardManagementServiceImpl.class);
    @Resource
    RedisUtil redisUtil;
    @Resource
    private OilCardVehicleRelMapper oilCardVehicleRelMapper;

    @Resource
    private IOrderAccountService iOrderAccountService;
    @DubboReference(version = "1.0.0")
    IServiceInfoService serviceInfoService;
    @DubboReference(version = "1.0.0")
    ITenantServiceRelService tenantServiceRelService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Autowired
    private IOilCardInfoService oilCardInfoService;
    @Autowired
    private IOilCardLogService oilCardLogService;
    @Autowired
    IOilCardVehicleRelService oilCardVehicleRelService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @Lazy
    @Autowired
    private IOrderInfoService orderInfoService;
    @Lazy
    @Resource
    private IOrderInfoHService orderInfoHService;
    @Lazy
    @Autowired
    private IOrderSchedulerService orderSchedulerService;
    @Lazy
    @Autowired
    private IOrderSchedulerHService orderSchedulerHService;
    @Lazy
    @Autowired
    private IOrderFeeHService orderFeeHService;
    @Lazy
    @Autowired
    private IOrderFeeExtHService orderFeeExtHService;
    @Lazy
    @Autowired
    private IOrderLimitService orderLimitService;
    @Lazy
    @Autowired
    private IBusiSubjectsRelService busiSubjectsRelService;
    @Lazy
    @Autowired
    private IPayoutIntfService payoutIntfService;
    @Lazy
    @Autowired
    private IBillPlatformService billPlatformService;
    @Lazy
    @Autowired
    private IBillAgreementService billAgreementService;
    @Lazy
    @Autowired
    private IPayFeeLimitService payFeeLimitService;
    @Lazy
    @Autowired
    private IPayoutOrderService payoutOrderService;
    @Lazy
    @Autowired
    private IAccountDetailsService accountDetailsService;
    @Lazy
    @Autowired
    private IOrderOilSourceService orderOilSourceService;
    @Lazy
    @Resource
    IOilCardManagementService  oilCardManagementSV;

    @Resource
    IOilCardLogService iOilCardLogService;
    @Lazy
    @Resource
    LoginUtils loginUtils;
    @Resource
    IOrderSchedulerService iOrderSchedulerService;

    @Resource
    IOrderOilCardInfoService iOrderOilCardInfoService;

    @Resource
    OilCardManagementMapper oilCardManagementMapper;

    @Resource
    IServiceInfoService iServiceInfoService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Override
    public List<OilCardManagement> findByPlateNumber(String plateNumber, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        Long tenantId = Long.parseLong(accessToken);
        List<OilCardManagement> byPlateNumber=null;
        if (tenantId != null){
            byPlateNumber = oilCardVehicleRelMapper.findByPlateNumber(plateNumber, tenantId);
        }

        return byPlateNumber;
    }

    @Override
    public Map checkCardNum(String oilCardNum, String accessToken) {
        if (StringUtils.isBlank(oilCardNum)) {
            throw new BusinessException("请输入油卡号！");
        }

        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        // 判断用户绑定的油卡  是否超出限制  油卡是否是供应商油卡  是否是无效油卡
        this.checkCardNum(oilCardNum, tenantId);
        Map<String, Object> map = new HashMap<>();
        map.put("info", "Y");
        return map;
    }

    @Override
    public boolean checkCardNum(String oilCardNum, Long tenantId) {
        List<OilCardManagement> oilCardManagementByCard = this.getOilCardManagementByCard(oilCardNum, tenantId);
        if (oilCardManagementByCard == null || oilCardManagementByCard.size() == 0) {
            return true;
        }
        if (oilCardManagementByCard.size() >= 2) {
            throw new BusinessException("油卡号异常，无法添加！");
        }
        OilCardManagement oilCardManagement = oilCardManagementByCard.get(0);
        if (oilCardManagement.getCardType() == null || oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
            throw new BusinessException("该油卡已是供应商油卡，不能添加！");
        }
        if (oilCardManagement.getOilCardStatus() == SysStaticDataEnum.OILCARD_STATUS.STATUS_NO) {
            throw new BusinessException("无效油卡不能添加！");
        }
        return true;
    }

    @Override
    public List<OilCardManagement> getOilCardManagementByCard(String oilCardNum, Long tenantId) {
        LambdaQueryWrapper<OilCardManagement> lambda=new QueryWrapper<OilCardManagement>().lambda();
        lambda.eq(OilCardManagement::getOilCarNum,oilCardNum)
                .eq(OilCardManagement::getTenantId,tenantId)
                .ne(OilCardManagement::getOilCardStatus,STATUS_CB)
                .ne(OilCardManagement::getOilCardStatus,STATUS_LOSS);
        return this.list(lambda);
    }

    @Override
    public long queryOilBalance(Long userId, Long tenantId, int userType) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("请输入用户id");
        }

        //查询司机油卡余额
        OrderAccountBalanceDto orderAccountBalance = iOrderAccountService.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.OIL_BALANCE, tenantId, userType);
        if (orderAccountBalance == null) {
            throw new BusinessException("未找到用户账户信息");
        }
        long oilBalance = 0;
        if (orderAccountBalance != null) {
            oilBalance = orderAccountBalance.getAccountList().get(0).getOilBalance() == null ? 0L : orderAccountBalance.getAccountList().get(0).getOilBalance();
        } else {
            throw new BusinessException("未找到用户油卡余额");
        }
        return oilBalance;

    }

    @Override
    public void pledgeOilCardBalance(String oilCardNum, Long balance, Long tenantId, Long toTenantId, boolean isRelease) {

        boolean isToTenant = false;
        if (toTenantId != null && toTenantId > 0) {
            isToTenant = true;
        }
        List<OilCardManagement> oilCardManagementByCard = this.getOilCardManagementByCard(oilCardNum, tenantId);
        if (oilCardManagementByCard != null && oilCardManagementByCard.size() >= 2) {
            throw new BusinessException("该油卡[" + oilCardNum + "]异常，无法使用！");
        }
        if (oilCardManagementByCard == null || oilCardManagementByCard.size() == 0) {
            throw new BusinessException("该油卡[" + oilCardNum + "]不存在，无法增减金额！");
        } else {
            OilCardManagement oilCardManagement = oilCardManagementByCard.get(0);
            if (oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                throw new BusinessException("该油卡[" + oilCardNum + "]已是供应商油卡，不能使用！");
            }
            if (isToTenant){
                List<OilCardManagement> toTenantOilCard = this.getOilCardManagementByCard(oilCardNum, toTenantId);
                if (toTenantOilCard != null && toTenantOilCard.size() > 0) {
                    OilCardManagement oilCard = toTenantOilCard.get(0);
                    if (oilCard != null && oilCard.getCardType() != null) {
                        if (oilCard.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            throw new BusinessException("承运方已持有["+oilCardNum+"]供应商油卡，不能使用！");
                        }
                        if (oilCard.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                            throw new BusinessException("承运方已持有["+oilCardNum+"]自购油卡，不能使用！");
                        }
                    }
                }
            }
            if (oilCardManagement.getOilCardStatus() == SysStaticDataEnum.OILCARD_STATUS.STATUS_NO) {
                throw new BusinessException("无效油卡[" + oilCardNum + "]不能使用！");
            }
            Long cardBalance = oilCardManagement.getCardBalance();
            cardBalance = cardBalance == null ? 0L : cardBalance;
            balance = balance == null ? 0L : balance;
            long oilFee = 0L;
            if (isRelease) {//释放加钱
                oilFee = cardBalance + balance;
            }else{
                oilFee = cardBalance - balance < 0 ? 0 : cardBalance - balance;
            }
            oilCardManagement.setCardBalance(oilFee);
            this.saveOrUpdate(oilCardManagement, true);
        }

    }

    @Override
    public void saveEquivalenceCard(OrderInfo orderInfo, OrderScheduler scheduler, String cardNumber, Long cardAmount, Boolean isAdd, Boolean isAddCarriage,LoginInfo user) {
        if (orderInfo.getOrderType() != null
                && orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {//不是来源接单的才能添加卡
            this.modifyOilCardBalance(
                    cardNumber,
                    cardAmount,
                    isAdd,scheduler.getPlateNumber(),
                    scheduler.getCarDriverMan(),orderInfo.getOrderId(),user.getId(),
                    orderInfo.getTenantId(),false,user);
        }
    }

    @Override
    public void modifyOilCardBalance(String oilCardNum, Long balance, Boolean isAdd, String plateNumber,
                                     String carDriverMan, Long orderId, Long userId, Long tenantId,
                                     Boolean isUpdateOrder,LoginInfo baseUser) {
        OilCardSaveInDto oilCardSaveIn = new OilCardSaveInDto();
        String linkman = "";
        boolean isToTenant = false;
        if (baseUser.getTenantId().equals(tenantId)) {
            UserDataInfo userDataInfo = userDataInfoService.getById(userId);
            linkman = userDataInfo == null ? "" : userDataInfo.getLinkman();
            oilCardSaveIn.setOpId(userId);
        } else {
            linkman = sysTenantDefService.getSysTenantDef(tenantId).getName();
            isToTenant = true;
            oilCardSaveIn.setOpId(tenantId);
        }
        List<OilCardManagement> oilCardManagementByCard = this.getOilCardManagementByCard(oilCardNum, tenantId);
        if (oilCardManagementByCard != null && oilCardManagementByCard.size() >= 2) {
            throw new BusinessException("该油卡[" + oilCardNum + "]异常，无法使用！");
        }
        String logDesc = "";
        Long oilFee = 0L;
        if (oilCardManagementByCard == null || oilCardManagementByCard.size() == 0) {
            if (!isAdd) {
                throw new BusinessException("该油卡[" + oilCardNum + "]不存在无法扣减金额！");
            }
            oilCardSaveIn.setOilCarNum(oilCardNum);
            oilCardSaveIn.setCardBalance(balance);
            oilCardSaveIn.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER);
            oilCardSaveIn.setOilCardStatus(STATUS_YES);
            logDesc = "[" + linkman + "]"+(isUpdateOrder ? "通过修改订单" : "")+ "充值￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
            oilFee = balance;
        } else {
            OilCardManagement oilCardManagement = oilCardManagementByCard.get(0);
            if (oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                throw new BusinessException("该油卡[" + oilCardNum + "]"+(isToTenant ? "在承运方" : "")+"已是供应商油卡，不能使用！");
            }
            if (isToTenant && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                throw new BusinessException("承运方已持有["+oilCardNum+"]自购油卡，不能使用！");
            }
            if (oilCardManagement.getOilCardStatus() == SysStaticDataEnum.OILCARD_STATUS.STATUS_NO) {
                throw new BusinessException("油卡[" + oilCardNum + "]"+(isToTenant ? "在承运方" : "")+"无效不能使用！");
            }
            BeanUtils.copyProperties( oilCardManagement,oilCardSaveIn);
            oilCardSaveIn.setCardId(oilCardManagement.getId());
            Long cardBalance = oilCardManagement.getCardBalance();
            cardBalance = cardBalance == null ? 0L : cardBalance;
            balance = balance == null ? 0L : balance;
            if (isAdd) {
                Long l = cardBalance + balance;
                oilCardSaveIn.setCardBalance(l);
                oilFee = balance;
                logDesc = "[" + linkman + "]" + "充值￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
            } else {
                if (cardBalance < balance) {
                    //最大清零
                    balance = cardBalance;
                }
                long l = cardBalance - balance;
                oilCardSaveIn.setCardBalance(l);
                oilFee = -balance;
                logDesc = "[" + linkman + "]" + "减少了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
            }
        }
        oilCardSaveIn.setOpName(linkman);
        oilCardSaveIn.setTenantId(tenantId);
        oilCardSaveIn.setIsFleet(1);
        oilCardSaveIn.setIsSkipVerify(true);
        OilCardManagement oilCardManagement = this.save(oilCardSaveIn,baseUser);
        OilCardLog oilCardLog = new OilCardLog();
        oilCardLog.setCardId(oilCardManagement.getId());
        oilCardLog.setCarDriverMan(carDriverMan);
        oilCardLog.setOrderId(orderId);
        oilCardLog.setUserId(userId);
        oilCardLog.setLogDesc(logDesc);
        oilCardLog.setOilFee(oilFee);
        oilCardLog.setTenantId(tenantId);
        oilCardLog.setPlateNumber(plateNumber);
        oilCardLog.setLogType(EnumConsts.OIL_LOG_TYPE.ADD_OR_REDUCE);
        oilCardLog.setLogDate(LocalDateTime.now());
        oilCardLog.setCreateTime(LocalDateTime.now());
        oilCardLogService.saveOrUpdate(oilCardLog);
    }

    @Override
    public void saveOrUpdate(OilCardManagement oilCardManagement, Boolean isUpdate) {
        if(isUpdate){
            this.update(oilCardManagement);
        }else {
            this.save(oilCardManagement);
        }
    }

    @Override
    public List<OilCardManagement> getOilCardByOrderId(Long orderId, Long tenantId) {
        LambdaQueryWrapper<OilCardManagement> lambda=Wrappers.lambdaQuery();
        lambda.eq(OilCardManagement::getPledgeOrderId,orderId)
              .eq(OilCardManagement::getTenantId,tenantId);
        return this.list(lambda);
    }

    @Override
    public void oilPledgeHandle(Long orderId, Long userId, String vehicleAffiliation,
                                List<String> oilCardNums, Long pledgeFee, Long tenantId,
                                Integer vehicleClass, OrderScheduler scheduler,LoginInfo user,String token) {
        for (String oilCardNum : oilCardNums) {
            if (org.apache.commons.lang.StringUtils.isBlank(oilCardNum)) {
                throw new BusinessException("请输入油卡号");
            }
            List<OilCardManagement> list = this.findByOilCardNum(oilCardNum, tenantId);
            if (list == null || list.size() <= 0) {
                throw new BusinessException("实体油卡[" + oilCardNum + "]不可用!");
            }
            OilCardManagement oilCard = list.get(0);
            Map map = new ConcurrentHashMap();
            map.put("cardId", oilCard.getId());
            //释放油卡
            if (oilCard.getPledgeOrderId() != null && oilCard.getPledgeOrderId() > 0) {
                this.reclaim(oilCard.getPledgeOrderId() == null ? 0 : oilCard.getPledgeOrderId(),
                        oilCard.getId(),tenantId,user,token);
            }
            if (vehicleClass != null
                    && (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4)) {
                oilCard.setPledgeOrderId(orderId);// 更改卡号中的抵押订单
                //oilCard.setPledgeFee(Long.parseLong(SysCfgUtil.getCfgVal("PLEDGE_OILCARD_FEE", 0, String.class).toString()));
                oilCard.setPledgeFee(null);
            }else{
                oilCard.setPledgeOrderId(null);// 更改卡号中的抵押订单
                oilCard.setPledgeFee(0L);
            }
            this.saveOrUpdate(oilCard);// 保存信息
        }
    }

    @Override
    public void pledgeOrReleaseOilCardAmount(Long userId, String vehicleAffiliation, Long amountFee,
                                             Long orderId, Long tenantId, Integer pledgeType,LoginInfo user,String token) {
        log.info("抵扣释放油卡押金接口:userId=" + userId + " vehicleAffiliation=" + vehicleAffiliation + " amountFee=" + amountFee+ " 订单号=" + orderId + " 抵扣类型(0抵押1释放)=" + pledgeType);
        if (userId < 1) {
            throw new BusinessException("请输入用户编号");
        }
        //todo 加锁
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "pledgeOrReleaseOilCardAmount" + orderId + pledgeType, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        if (org.apache.commons.lang.StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("资金渠道不能为空");
        }
        if (amountFee <= 0) {
            throw new BusinessException("抵押释放金额不能小于等于0");
        }
        if (pledgeType < 0) {
            throw new BusinessException("请输入抵押类型");
        }
        if (orderId <= 0) {
            throw new BusinessException("请输入订单号");
        }
        // 通过租户id，找到租户用户id
        Long tenantUserId = sysTenantDefService.getSysTenantDef(tenantId).getAdminUser();
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = userDataInfoService.getById(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 根据用户ID和资金渠道类型获取账户信息
        OrderLimit ol = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("根据订单号：" + orderId + " 用户ID：" + userId + " 未找到订单限制记录");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (org.apache.commons.lang.StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation,0L, tenantId,oilAffiliation,ol.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        if (pledgeType == OrderAccountConst.PLEDGE_RELEASE_TYPE.PLEDGE) {//抵押
			/*//司机应付逾期油卡抵押金
			BusiSubjectsRel pledgePayableRel = busiSubjectsRelTF.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN, amountFee);
			busiList.add(pledgePayableRel);
			//车队应收逾期油卡抵押金
			BusiSubjectsRel pledgeReceivableRel = busiSubjectsRelTF.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_PLEDGE_RECEIVABLE_IN, amountFee);
			fleetBusiList.add(pledgeReceivableRel);
			//是否自动打款
			boolean isAutoTransfer = payFeeLimitTF.isMemberAutoTransfer(account.getSourceTenantId());
			Integer isAutomatic = null;
			if (isAutoTransfer) {
				isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
			} else {
				isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
			}
			PayoutIntf payoutIntf = payoutIntfSV.createPayoutIntf(tenantUserId, PAY_TYPE.TENANT, TXN_TYPE.XX_TXN_TYPE, amountFee, tenantId, VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, orderId,
					-1L, isAutomatic, isAutomatic, userId, PAY_TYPE.USER, EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD, EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN,VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
			payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
			String sysValue = (String) SysCfgUtil.getCfgVal(COMMON_KEY.PLEDGE_OILCARD_FEE_DAY, 0, String.class);
			int day = 30;
			if (!StringUtils.isEmpty(sysValue)) {
				day = Integer.parseInt(sysValue);
			}
			payoutIntf.setCreateDate(DateUtil.addDate(new Date(), day));
			if (isTenant) {
				payoutIntf.setPayType(PAY_TYPE.TENANT);
				payoutIntf.setPayTenantId(sysTenantDef.getTenantId());
			}
			payoutIntf.setRemark("油卡抵押金额");
			payoutIntfTF.doSavePayOutIntfVirToVir(payoutIntf);*/
            //20180903油卡押金调整
            //油卡抵押(未到期)
            BusiSubjectsRel pledgeMarginRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN, amountFee);
            busiList.add(pledgeMarginRel);

        } else if (pledgeType == OrderAccountConst.PLEDGE_RELEASE_TYPE.RELEASE) {//释放
            // 提现记录更改状态
            List<PayoutIntf> list = payoutIntfService.queryPayoutIntf(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,
                    EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN, orderId);
            if (list != null && list.size() > 0) {
                for (PayoutIntf pay : list) {
                    pay.setRespCode("3");
                    pay.setRespMsg("油卡抵押释放");
                    pay.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                    pay.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
                    pay.setIsTurnAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
                    payoutIntfService.saveOrUpdate(pay);
                }
                //司机应付逾期油卡释放金
                BusiSubjectsRel pledgePayableRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT,
                        amountFee);
                busiList.add(pledgePayableRel);
                //车队应收逾期油卡释放金
                BusiSubjectsRel pledgeReceivableRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_OUT,
                        amountFee);
                fleetBusiList.add(pledgeReceivableRel);
            } else {//20180903油卡押金调整
                //释放油卡抵押金，判断订单尾款是否到期
                if (ol.getFianlSts() != null && ol.getFianlSts() == OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS1 && amountFee > 0) {//已到期
                    //司机应收逾期油卡抵押金
                    BusiSubjectsRel pledgePayableRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN, amountFee);
                    busiList.add(pledgePayableRel);
                    //路歌开票 服务费 20190717 司机应收账户不记服务费
                    long serviceFee = 0;
                    boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                    if (isLuge) {
                        Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), amountFee, 0L, 0L, amountFee, tenantId,null);
                        serviceFee = (Long) result.get("lugeBillServiceFee");
                    }
                    //车队应付逾期油卡抵押金
                    BusiSubjectsRel pledgeReceivableRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_IN, amountFee);
                    fleetBusiList.add(pledgeReceivableRel);
                    if (serviceFee > 0) {
                        BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4009, serviceFee);
                        fleetBusiList.add(payableServiceFeeSubjectsRel);
                    }
                    //是否自动打款
                    boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
                    Integer isAutomatic = null;
                    if (isAutoTransfer) {
                        isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
                    } else {
                        isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
                    }
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER,
                            OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, amountFee, -1L, vehicleAffiliation, orderId,
                            tenantId, isAutomatic, isAutomatic,tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                            EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,
                            EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN,
                            oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,
                            ol.getUserType(),serviceFee,token);
                    payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
                    payoutIntf.setRemark("油卡抵押释放金额");
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
                    } else {
                        OrderSchedulerH  orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        //写入payout_order
                        payoutOrderService.createPayoutOrder(userId, amountFee, OrderAccountConst.FEE_TYPE.CASH_TYPE,
                                payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                    }
                } else {
                    //司机应收逾期油卡抵押金
                    BusiSubjectsRel pledgePayableRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN,
                            amountFee);
                    busiList.add(pledgePayableRel);
                }
            }
        }
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,busiList);
        // 写入账户明细表并修改账户金额费用
        long soNbr = CommonUtil.createSoNbr();
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr,
                orderId, sysOperator.getName(), null, tenantId, null, "", null,
                vehicleAffiliation,user);

        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = null;
        if (fleetBusiList != null && fleetBusiList.size() > 0) {
            OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation,
                    0L, tenantId,ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,
                    fleetBusiList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null,
                    vehicleAffiliation,user);
        }

        // 写入订单限制表和订单资金流向表
        ParametersNewDto inParamNew = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(),
                sysOperator.getBillId(),EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD, orderId, amountFee,
                vehicleAffiliation,"");
        inParamNew.setTenantId(tenantId);
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantUser.getLinkman());
        if (fleetSubjectsRelList != null && fleetSubjectsRelList.size() > 0) {
            busiSubjectsRelList.addAll(fleetSubjectsRelList);
        }
        orderOilSourceService.busiToOrderNew(inParamNew, busiSubjectsRelList,user);
    }

    @Override
    public String reclaim(Long orderId, Long cardId, Long tenantId,LoginInfo user,String token) {
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "reclaim" + cardId+orderId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        List<OilCardManagement> oilCardByOrderId = this.getOilCardByOrderId(orderId, tenantId);
        if (oilCardByOrderId == null || oilCardByOrderId.size() == 0) {
            return "Y";
        }
        OilCardManagement oilCardManagement = null;
        for (OilCardManagement oilCardManagementF : oilCardByOrderId) {
            if (oilCardManagementF.getId().equals(cardId)) {
                oilCardManagement = oilCardManagementF;
            }
        }
        if (oilCardManagement == null) {
            throw new BusinessException("找不到油卡信息");
        }

        //Long pledgeFee = Long.parseLong(SysCfgUtil.getCfgVal("PLEDGE_OILCARD_FEE", 0, String.class).toString());
        Long pledgeOrderId = oilCardManagement.getPledgeOrderId();
        if (pledgeOrderId != null && pledgeOrderId > 0) {
           /* if (oilCardManagement.getPledgeFee() != null && oilCardManagement.getPledgeFee() > 0) {
                pledgeFee = oilCardManagement.getPledgeFee();
            }*/
            OrderInfo pledgeOrder = orderInfoService.getOrder(pledgeOrderId);
            if (pledgeOrder != null) {

                //释放金额
                /*if (pledgeOrder.getIsTransit() != null && pledgeOrder.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {
                    IOrderSchedulerSV orderSchedulerSV = (IOrderSchedulerSV) SysContexts.getBean("orderSchedulerSV");
                    OrderScheduler orderScheduler = orderSchedulerSV.getOrderScheduler(pledgeOrder.getOrderId());
                    if (orderScheduler == null) {
                        throw new BusinessException("未找到司机信息！");
                    }
                    Long userId = 0L;
                    if (pledgeOrder.getToTenantId() != null && pledgeOrder.getToTenantId() > 0) {//有归属租户 外调车
                        SysTenantDef tenantDef = tenantSV.getTenantCode(null, pledgeOrder.getToTenantId());
                        if (tenantDef == null) {
                            throw new BusinessException("未找到车队信息!");
                        }
                        userId = tenantDef.getAdminUser();
                    } else {//无归属车队 纯C车
                        userId = orderScheduler.getCarDriverId();
                    }
                    IOrderFeeSV orderFeeSV = (IOrderFeeSV) SysContexts.getBean("orderFeeSV");
                    OrderFee orderFee = orderFeeSV.getOrderFee(pledgeOrderId);
                    if (orderFee == null) {
                        throw new BusinessException("费用渠道为空！");
                    }
                    if (oilCardByOrderId.size() == 1) {
                        String vehicleAffiliation = orderFee.getVehicleAffiliation();
                        operationOilTF.pledgeOrReleaseOilCardAmount(userId, vehicleAffiliation, pledgeFee, pledgeOrder.getOrderId(), pledgeOrder.getTenantId(), OrderAccountConst.PLEDGE_RELEASE_TYPE.RELEASE);
                    }
                }*/
            } else {
                OrderInfoH pledgeOrderHis = orderInfoHService.getOrderH(pledgeOrderId);
                if (pledgeOrderHis != null) {
                    //释放金额
                    if (pledgeOrderHis.getIsTransit() != null && pledgeOrderHis.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {
                        OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(pledgeOrderHis.getOrderId());
                        if (orderSchedulerH == null) {
                            throw new BusinessException("未找到司机信息！");
                        }
                        Long userId = 0L;
                        if (pledgeOrderHis.getToTenantId() != null && pledgeOrderHis.getToTenantId() > 0) {//有归属租户 外调车
                            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(pledgeOrderHis.getToTenantId(),true);
                            if (tenantDef == null) {
                                throw new BusinessException("未找到车队信息!");
                            }
                            userId = tenantDef.getAdminUser();
                        }else if(orderSchedulerH.getIsCollection() != null && orderSchedulerH.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
                            userId = orderSchedulerH.getCollectionUserId();
                        }  else {//无归属车队 纯C车
                            userId = orderSchedulerH.getCarDriverId();
                        }

                        OrderFeeH orderFeeH = orderFeeHService.selectByOrderId(pledgeOrderId);
                        if (orderFeeH == null) {
                            throw new BusinessException("费用渠道为空！");
                        }
                        if (oilCardByOrderId.size() == 1) {
                            OrderFeeExtH orderFeeExtH=orderFeeExtHService.getOrderFeeExtH(pledgeOrderId);
                            Long pledgeFee=orderFeeExtH.getOilDepositActual().longValue();
                            if(orderFeeExtH==null||pledgeFee==null) {
                                throw new BusinessException("未找到油卡抵押押金！");
                            }

                            String vehicleAffiliation = orderFeeH.getVehicleAffiliation();
                            if(pledgeFee>0) {
                                this.pledgeOrReleaseOilCardAmount(userId, vehicleAffiliation, pledgeFee,
                                        pledgeOrderHis.getOrderId(), pledgeOrderHis.getTenantId(),
                                        OrderAccountConst.PLEDGE_RELEASE_TYPE.RELEASE,user,token);
                            }
                        }
                    }
                } else {
                    throw new BusinessException("找不到上笔冻结订单");
                }
            }
        } else {
            throw new BusinessException("该油卡未使用，不用回收！");
        }
        oilCardManagement.setPledgeOrderId(-1L);//更改卡号中的抵押订单
        oilCardManagement.setPledgeFee(-1L);//更改卡号中的抵押金额
        this.saveOrUpdate(oilCardManagement, true);//保存信息
        //生成一条使用日志

        OilCardLog oilCardLog = new OilCardLog();
        oilCardLog.setCardId(cardId);
        oilCardLog.setLogDesc("[" + user.getName() + "]回收油卡");
        oilCardLog.setCreateTime(LocalDateTime.now());
        oilCardLog.setTenantId(user.getTenantId());
        oilCardLog.setUserId(user.getId());
        oilCardLog.setCreateTime(LocalDateTime.now());
        oilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.RECOVER);
        //操作日志
       sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(),
               SysOperLogConst.OperType.Update, "回收油卡");

        return "Y";
    }

    @Override
    public List<OilCardManagement> getOilCardsByPlateNumber(String plateNumber, String accessToken) {
        if(StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("请传入车牌号！");
        }
        Long tenantId=loginUtils.get(accessToken).getTenantId();
        return oilCardVehicleRelMapper.findByPlateNumber(plateNumber, tenantId);
    }

    @Override
    public List<OilCardManagement> getOilCardsByPlateNumbers(String plateNumber,String accessToken) {
        if(StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("请传入车牌号！");
        }
        Long tenantId=loginUtils.get(accessToken).getTenantId();
        return oilCardManagementSV.findByPlateNumber(plateNumber, tenantId.toString());
    }

    @Override
    public int releaseOilCardByOrderId(Long orderId, Long tenantId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("缺少订单号或订单号不正确");
        }
        if (tenantId == null || tenantId < 0) {
            throw new BusinessException("缺少租户ID参数");
        }

        List<OilCardManagement> list = this.getOilCardByOrderId(orderId, tenantId);
        if (list == null || list.size() == 0) {
            throw new BusinessException("未找到该订单绑定的油卡");
        }

        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);

        if (orderScheduler == null || orderScheduler.getCarDriverId() == null || orderScheduler.getCarDriverId() <= 0) {
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null || orderSchedulerH.getCarDriverId() == null || orderSchedulerH.getCarDriverId() <= 0) {
                throw new BusinessException("未找到该订单信息");
            }
            orderScheduler = new OrderScheduler();
            orderScheduler.setOrderId(orderId);
            orderScheduler.setCarDriverId(orderSchedulerH.getCarDriverId());
            orderScheduler.setPlateNumber(orderSchedulerH.getPlateNumber());
            orderScheduler.setCarDriverMan(orderSchedulerH.getCarDriverMan());
            orderScheduler.setCarDriverPhone(orderSchedulerH.getCarDriverPhone());
        }

        //记录油卡操作
        String logdesc = "[" + loginInfo.getName() + "]释放油卡";
        for (OilCardManagement oilCardManagement : list) {
            OilCardLog oilCardLog = new OilCardLog();
            oilCardLog.setCardId(oilCardManagement.getId());
            oilCardLog.setCarDriverMan(orderScheduler.getCarDriverMan());
            oilCardLog.setOrderId(orderId);
            oilCardLog.setUserId(orderScheduler.getCarDriverId());
            oilCardLog.setLogDesc(logdesc);
            oilCardLog.setOilFee(oilCardManagement.getPledgeFee());
            oilCardLog.setTenantId(tenantId);
            oilCardLog.setPlateNumber(orderScheduler.getPlateNumber());
            oilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.RECOVER);
        }

        int result = this.updateOilCardByOrder(orderId);
        return result;
    }

    @Override
    public int releaseOilCardByOrderId(Long orderId, Long tenantId, LoginInfo user) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("缺少订单号或订单号不正确");
        }
        if (tenantId == null || tenantId < 0) {
            throw new BusinessException("缺少租户ID参数");
        }

        List<OilCardManagement> list = this.getOilCardByOrderId(orderId, tenantId);
        if (list == null || list.size() == 0) {
            throw new BusinessException("未找到该订单绑定的油卡");
        }

        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);

        if (orderScheduler == null || orderScheduler.getCarDriverId() == null || orderScheduler.getCarDriverId() <= 0) {
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null || orderSchedulerH.getCarDriverId() == null || orderSchedulerH.getCarDriverId() <= 0) {
                throw new BusinessException("未找到该订单信息");
            }
            orderScheduler = new OrderScheduler();
            orderScheduler.setOrderId(orderId);
            orderScheduler.setCarDriverId(orderSchedulerH.getCarDriverId());
            orderScheduler.setPlateNumber(orderSchedulerH.getPlateNumber());
            orderScheduler.setCarDriverMan(orderSchedulerH.getCarDriverMan());
            orderScheduler.setCarDriverPhone(orderSchedulerH.getCarDriverPhone());
        }

        //记录油卡操作
        String logdesc = "[" + user.getName() + "]释放油卡";
        for (OilCardManagement oilCardManagement : list) {
            OilCardLog oilCardLog = new OilCardLog();
            oilCardLog.setCardId(oilCardManagement.getId());
            oilCardLog.setCarDriverMan(orderScheduler.getCarDriverMan());
            oilCardLog.setOrderId(orderId);
            oilCardLog.setUserId(orderScheduler.getCarDriverId());
            oilCardLog.setLogDesc(logdesc);
            oilCardLog.setOilFee(oilCardManagement.getPledgeFee());
            oilCardLog.setTenantId(tenantId);
            oilCardLog.setPlateNumber(orderScheduler.getPlateNumber());
            oilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.RECOVER);
        }

        int result = this.updateOilCardByOrder(orderId);
        return result;
    }

    @Override
    public int updateOilCardByOrder(Long orderId) {
        LambdaUpdateWrapper<OilCardManagement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(OilCardManagement::getPledgeOrderId, null);
        updateWrapper.set(OilCardManagement::getPledgeFee, null);
        updateWrapper.eq(OilCardManagement::getPledgeOrderId, orderId);
        boolean update = this.update(updateWrapper);
        return update ? 1 : 0;
    }

    /***
     * isReclaim :是否回收
     * oilCardNum ： 油卡号
     * cardId ： 油卡ID
     * orderId : 订单号
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getOilCardByOrderIds(long orderId,String accessToken) {
//        IOrderOilCardInfoSV orderOilCardInfoSV = (IOrderOilCardInfoSV) SysContexts.getBean("orderOilCardInfoSV");
//        List<OrderOilCardInfo> orderOilCardInfos = orderOilCardInfoSV.queryOrderOilCardInfoByOrderId(orderId, null);
        List<OrderOilCardInfo> orderOilCardInfos = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderIds(orderId, null, accessToken);
        List<Map<String, Object>> list = new ArrayList<>();
        if (orderOilCardInfos != null && orderOilCardInfos.size() > 0) {
            orderOilCardInfos.forEach(orderOilCardInfo -> {
                Map<String, Object> map = new HashMap<>();
                List<OilCardManagement> oilCardManagementByCard = oilCardManagementSV.getOilCardManagementByCard(orderOilCardInfo.getOilCardNum(), orderOilCardInfo.getTenantId());

                if (oilCardManagementByCard == null || oilCardManagementByCard.size() == 0) {
                    throw new BusinessException("无该油卡！");
                }
                OilCardManagement oilCardManagement = oilCardManagementByCard.get(0);
                if (oilCardManagement.getPledgeOrderId() == null || oilCardManagement.getPledgeOrderId() < 0 || oilCardManagement.getPledgeOrderId().longValue() != orderId) {
                    map.put("isReclaim", true);
                } else if (oilCardManagement.getPledgeOrderId().longValue() == orderId) {
                    map.put("isReclaim", false);
                }
                map.put("oilCardNum", oilCardManagement.getOilCarNum());
                map.put("cardId", oilCardManagement.getId());
                map.put("orderId", orderId);
                list.add(map);
            });
        }
        return list;
    }

    /**
     *  主键查询
     * @param cardId
     * @return
     */
    @Override
    public OilCardManagement getOilCardManagement(Long cardId) {
//        OilCardManagement oilCardManagement = oilCardManagementSV.getOilCardManagement(cardId);
        QueryWrapper<OilCardManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("id",cardId);
        OilCardManagement oilCardManagement = baseMapper.selectOne(wrapper);
        if (oilCardManagement.getCardType() != null && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
//            IServiceInfoSV serviceInfoSV = (IServiceInfoSV) SysContexts.getBean("serviceInfoSV");
//            ServiceInfo serviceInfo = serviceInfoSV.getServiceInfoById(oilCardManagement.getUserId());
            ServiceInfo serviceInfo =iServiceInfoService.getServiceInfoByServiceUserId(oilCardManagement.getUserId());
            if (serviceInfo != null) {
//                oilCardManagement.setCompanyName(serviceInfo.getServiceName());
                oilCardManagement.setLinkman(serviceInfo.getServiceName());
            }
        }
        // TODO  老代码  return oilCardManagementSV.getOilCardManagement(cardId);
        return oilCardManagement;
    }

    @Override
    public List<OilCardManagement> findByOilCardNum(String oilCardNum, Long tenantId) {
        LambdaQueryWrapper<OilCardManagement> lambda= Wrappers.lambdaQuery();
        lambda.eq(OilCardManagement::getOilCarNum,oilCardNum)
              .eq(OilCardManagement::getTenantId,tenantId)
              .eq(OilCardManagement::getOilCardStatus,STATUS_YES) ;
        return this.list(lambda);
    }

    @Override
    public OilCardManagement save(OilCardSaveInDto saveIn,LoginInfo baseUser) {
        String oilCarNum = saveIn.getOilCarNum();
        Integer oilCardStatus = saveIn.getOilCardStatus();
        Long userId = saveIn.getUserId();
        Integer cardType = saveIn.getCardType();
        Long cardBalance = saveIn.getCardBalance();
        if (StringUtils.isBlank(oilCarNum)) {
            throw new BusinessException("请输入油卡号");
        }
        if (oilCardStatus == null || oilCardStatus <= 0) {
            throw new BusinessException("请选择油卡状态");
        }
        if (cardType == null || cardType < 0) {
            throw new BusinessException("请选择来源类型！");
        }
        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && (userId == null || userId < 0)) {
            throw new BusinessException("请选择服务商");
        }
        boolean isFleet = saveIn.getIsFleet() == null || saveIn.getIsFleet() != 1;

        if (isFleet && (cardBalance == null || cardBalance < 0)) {
            throw new BusinessException("请输入理论金额！");
        }
        Long cardId = saveIn.getCardId();
        boolean isUpdate = false;
        long tenantId = saveIn.getTenantId();


        int oldCardType = -1;
        long oldCardBalance = 0;

        OilCardManagement oilCardManagement = null;
        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
            ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(userId);
            if(null == serviceInfo){
                throw new BusinessException("服务商信息不存在,请联系管理员！");
            }
            if ( null != serviceInfo.getServiceType() && serviceInfo.getServiceType().intValue() != OIL_CARD) {
                TenantServiceRel tenantServiceRel =  tenantServiceRelService.getTenantServiceRel(tenantId, userId);
                if (tenantServiceRel == null) {
                    throw new BusinessException("未和该服务商有合作关系，不能添加相关服务商的油卡信息！");
                } else {
                    if (tenantServiceRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                        throw new BusinessException("和该服务商的关系是无效，不能操作该油卡信息！");
                    }
                }
            }
        }else{
            saveIn.setUserId(null);
        }
        List list = this.getOilCardManagementByCard(oilCarNum, tenantId);
        if (cardId != null && cardId > 0) {
            isUpdate = true;
            oilCardManagement = this.getById(cardId);
            if(null == oilCardManagement){
                throw new BusinessException("油卡信息不存在");
            }
            if(null != oilCardManagement.getOilCardStatus() && oilCardManagement.getOilCardStatus().intValue() == STATUS_CB){
                throw new BusinessException("该油卡已被服务商回收");
            }
            if (StringUtils.isBlank(oilCardManagement.getOilCarNum())) {
                throw new BusinessException("数据有问题[油卡号为空],请尽快修复]");
            }
            if (!oilCardManagement.getOilCarNum().equals(oilCarNum) && list.size() > 0) {
                throw new BusinessException("油卡号已存在，请填写其他油卡号");
            }

            oldCardType = oilCardManagement.getCardType();
            oldCardBalance = oilCardManagement.getCardBalance() == null ? 0L : oilCardManagement.getCardBalance();
        } else {
            if (list.size() > 0) {
                throw new BusinessException("油卡号已存在，请填写其他油卡号");
            }
            OilCardInfo oilCardInfo = new OilCardInfo();
            oilCardInfo.setOilCardNum(oilCarNum);
            if (saveIn.getIsSkipVerify() == null || !saveIn.getIsSkipVerify()) {
                Integer count = oilCardInfoService.getOilCardInfo(oilCardInfo);
                if(count > 0){
                    throw new BusinessException("该卡号为平台服务商拥有，不允许自行增加，请联系服务商处理。");
                }
            }
            oilCardManagement = new OilCardManagement();
        }

        BeanUtils.copyProperties(saveIn,oilCardManagement);

        if (!isUpdate) {
            oilCardManagement.setCreateDate(LocalDateTime.now());
            oilCardManagement.setTenantId(tenantId);
            oilCardManagement.setId(CommonUtil.getAtomicCounter());
        }
        this.saveOrUpdate(oilCardManagement, isUpdate);

        this.batchSaveOilCardVehicleRel(saveIn.getVehicleNumberStr(),oilCardManagement.getId(), oilCardManagement.getOilCarNum(), tenantId);

        if (isFleet) {
            long balance = 0;
            String logDesc = "";
            boolean isLog = false;
            if (isUpdate) {
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.OWN && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                    balance = -oldCardBalance;
                    isLog = true;
                    logDesc = "[" + baseUser.getName() + "]" + "减少了￥" + CommonUtil.getDoubleFormatLongMoney(-balance, 2);
                }
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                    balance = oilCardManagement.getCardBalance();
                    isLog = true;
                    logDesc = "[" + baseUser.getName() + "]" + "增加了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
                }
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.OWN
                        && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN
                        && oldCardBalance != oilCardManagement.getCardBalance().longValue()) {
                    balance = oilCardManagement.getCardBalance().longValue() - oldCardBalance;
                    isLog = true;
                    if (balance > 0) {
                        logDesc = "[" + baseUser.getName() + "]" + "增加了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
                    } else {
                        logDesc = "[" + baseUser.getName() + "]" + "减少了￥" + CommonUtil.getDoubleFormatLongMoney(-balance, 2);
                    }
                }
            } else if (oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                balance = oilCardManagement.getCardBalance().longValue();
                isLog = true;
                logDesc = "[" + baseUser.getName() + "]" + "增加了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
            }
            if (isLog) {
                oilCardLogService.saveCardLog(oilCardManagement.getId(), balance, logDesc,null,baseUser);
            }
        }

        //操作日志
        if (isUpdate) {

            sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(), SysOperLogConst.OperType.Update, "修改", saveIn.getOpId(), tenantId);
        } else {
            sysOperLogService.saveSysOperLog(baseUser,SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(), SysOperLogConst.OperType.Add, "新增", saveIn.getOpId(), tenantId);
        }
        return oilCardManagement;
    }



    /**
     * 保存油卡车辆关系
     * @param vehicleNumberStr
     * @param cardId
     * @param oilCardNum
     * @param tenantId
     */
    public void batchSaveOilCardVehicleRel(String vehicleNumberStr,Long cardId,String oilCardNum,Long tenantId) {
        if(cardId!=null&&cardId>0) {

            oilCardVehicleRelService.deleteOilCardVehicleRelByCardId(cardId);
        }
        if(StringUtils.isNotBlank(vehicleNumberStr)) {
            String[] vehicleNumberArr=vehicleNumberStr.split(",");
            for (String vehicleNumber : vehicleNumberArr) {
                if(StringUtils.isNotBlank(vehicleNumber)) {
                    oilCardVehicleRelService.saveOilCardVehicleRel(vehicleNumber, cardId, oilCardNum, tenantId);
                }
            }
        }
    }

    @Override
    public void updateCardBalanceByCardNum(String cardNum, Long balance, Long orderId,String accessToken) {
        if(StringUtils.isBlank(cardNum)) {
            throw new BusinessException("请传入油卡号！");
        }
        if(balance==null) {
            throw new BusinessException("请传入金额！");
        }
        LoginInfo user = loginUtils.get(accessToken);
        Long tenantId=user.getTenantId();
        List<OilCardManagement> list=oilCardManagementSV.findByOilCardNum(cardNum, tenantId);
        if(list==null||list.size()==0) {
            throw new BusinessException("未找到卡号["+cardNum+"]的油卡！");
        }
        if(list.size()>1) {
            throw new BusinessException("存在多张相同卡号["+cardNum+"]的油卡，请核实！");
        }
        OilCardManagement oilCard=list.get(0);
        String logDesc=null;
        if(balance<0) {
            logDesc="[" + user.getName()+ "]" + "消费了￥" + CommonUtil.getDoubleFormatLongMoney(-balance, 2)
                    +",实际扣减了￥"+CommonUtil.getDoubleFormatLongMoney(-balance>oilCard.getCardBalance()?oilCard.getCardBalance():-balance, 2);
        }else {
            logDesc="[" + user.getName()+ "]" + "充值了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
        }
        Long cardBalance=balance;
        if((oilCard.getCardBalance()==null?0:oilCard.getCardBalance())+balance<0) {
            cardBalance=0L;
        }else {
            cardBalance+=oilCard.getCardBalance();
        }
        oilCard.setCardBalance(cardBalance);

        oilCardManagementSV.saveOrUpdate(oilCard, true);
        this.saveCardLog(oilCard.getId(), balance, logDesc,orderId,accessToken);
    }

    private void saveCardLog(Long id, Long balance, String logDesc, Long orderId,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        OilCardLog oilCardLog = new OilCardLog();
        oilCardLog.setCardId(id);
        oilCardLog.setLogDesc(logDesc);
        oilCardLog.setTenantId(user.getTenantId());
        oilCardLog.setUserId(user.getId());
        oilCardLog.setOilFee(balance);
        if(orderId!=null&&orderId>0) {
            oilCardLog.setOrderId(orderId);
            OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
            if(orderScheduler!=null) {
                oilCardLog.setPlateNumber(orderScheduler.getPlateNumber());
                oilCardLog.setCarDriverMan(orderScheduler.getCarDriverMan());
            }
        }
        iOilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.ADD_OR_REDUCE);

    }

    @Override
    public int doCheckOilCardNum(String oilCardNum, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (StringUtils.isBlank(oilCardNum)) {
//            throw new BusinessException("请输入油卡号");
            return 1;
        }

        OilCardManagement oilCardManagement = oilCardManagementSV.doCheckOilCardNum(oilCardNum,user.getTenantId());
        if(null == oilCardManagement){
//            throw new BusinessException("该卡号不存在，不能修改。");
            return 2;
        }
        if(null == oilCardManagement.getUserId()){
            return 0;
        }
        ServiceInfo serviceInfo = serviceInfoService.getById(user.getId());
        if(null != serviceInfo && null != serviceInfo.getServiceType() && serviceInfo.getServiceType()== OIL_CARD){
//            throw new BusinessException("该卡号为平台卡号，要走线上打款，不能修改");
            return 3;
        }
        return 0;
    }

    @Override
    public OilCardManagement doCheckOilCardNum(String oilCardNum, Long tenantId) {
        QueryWrapper<OilCardManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("oil_car_num",oilCardNum).eq("tenant_id",tenantId);
        List<OilCardManagement> list = this.list(queryWrapper);
        if (list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public com.youming.youche.order.domain.service.ServiceInfo getServiceInfoByCardNum(String cardNum, Long tenantId) {
        return oilCardManagementMapper.updateOilCarNum(cardNum, tenantId);
    }

    @Override
    public boolean verifyOilCardNumIsExists(String oilCardNum,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (StringUtils.isBlank(oilCardNum)) {
            throw new BusinessException("请输入油卡号");
        }
        QueryWrapper<OilCardManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("oil_car_num",oilCardNum);
        queryWrapper.eq("tenant_id",user.getTenantId());
        queryWrapper.eq("oil_card_status",STATUS_YES);
        List<OilCardManagement> oilCardManagements = oilCardManagementMapper.selectList(queryWrapper);
        return oilCardManagements.size() > 0;
    }

    @Override
    public List<GetOilCardByCardNumDto> getOilCardByCardNum(String cardNum, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        GetOilCardByCardNumVo vo = new GetOilCardByCardNumVo();
        vo.setOilCarNum(cardNum);
        vo.setOilCardStatus(SysStaticDataEnum.OILCARD_STATUS.STATUS_YES);
        vo.setOrderByOilCardNum(1);
        vo.setTenantId(tenantId);

        // 查询卡号信息
        List<GetOilCardByCardNumDto> getOilCardByCardNumDtos = baseMapper.doQuery(vo);
        for (GetOilCardByCardNumDto getOilCardByCardNumDto : getOilCardByCardNumDtos) {
            if (getOilCardByCardNumDto.getOilCardStatus() != null && getOilCardByCardNumDto.getOilCardStatus() > 0) {
                getOilCardByCardNumDto.setOilCardStatusName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("OILCARD_STATUS", String.valueOf(getOilCardByCardNumDto.getOilCardStatus())).getCodeName());
            }

            if (getOilCardByCardNumDto.getCardType() != null && getOilCardByCardNumDto.getCardType() > 0) {
                getOilCardByCardNumDto.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("OIL_CARD_TYPE", String.valueOf(getOilCardByCardNumDto.getCardType())).getCodeName());
            }

            if (getOilCardByCardNumDto.getOilCardType() != null && getOilCardByCardNumDto.getOilCardType() > 0) {
                getOilCardByCardNumDto.setOilCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SERV_OIL_CARD_TYPE", String.valueOf(getOilCardByCardNumDto.getOilCardType())).getCodeName());
            }

            if (getOilCardByCardNumDto.getCardBalance() != null && getOilCardByCardNumDto.getCardBalance() > 0) {
                getOilCardByCardNumDto.setCardBalanceStr(CommonUtil.divide(getOilCardByCardNumDto.getCardBalance()));
            }
        }


        return getOilCardByCardNumDtos;
    }

    @Override
    public Page<OilCardPledgeOrderListDto> queryOilCardPledgeOrderInfo(OilCardPledgeOrderListVo vo) {
        Page<OilCardPledgeOrderListDto> pledgeOrderListDtoPage = new Page<>(vo.getPageNum(), vo.getPageSize());
        return baseMapper.queryOilCardPledgeOrderInfo(pledgeOrderListDtoPage, vo);
    }
}
