package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.finance.api.payable.IPayByCashService;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.dto.youca.ProductNearByOutDto;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.order.IConsumeOilFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.api.order.other.IServiceOrderInfoService;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ServiceOrderInfo;
import com.youming.youche.order.dto.ConsumeOilFlowDetailsOutDto;
import com.youming.youche.order.dto.ConsumeOilFlowDetailsWxOutDto;
import com.youming.youche.order.dto.ConsumeOilFlowDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxOutDto;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.order.dto.OilAccountOutDto;
import com.youming.youche.order.dto.OilServiceInDto;
import com.youming.youche.order.dto.OilServiceOutDto;
import com.youming.youche.order.dto.PayForOilInDto;
import com.youming.youche.order.provider.mapper.order.ConsumeOilFlowMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.order.vo.ConsumeOilFlowVo;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 消费油记录表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class ConsumeOilFlowServiceImpl extends BaseServiceImpl<ConsumeOilFlowMapper, ConsumeOilFlow> implements IConsumeOilFlowService {
    @Resource
    ConsumeOilFlowMapper consumeOilFlowMapper;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @Resource
    private LoginUtils loginUtils;
    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private IServiceProductService serviceProductService;
    @DubboReference(version = "1.0.0")
    IServiceProductService iServiceProductService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    IPayByCashService iPayByCashService;
    @Resource
    IOperationOilService iOperationOilService;
    @Resource
    IOrderOilSourceService iOrderOilSourceService;
    @Resource
    IOpAccountService iOpAccountService;
    @Resource
    IPayManagerService iPayManagerService;
    @Resource
    IServiceOrderInfoService iServiceOrderInfoService;
    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService1;
    @DubboReference(version = "1.0.0")
    ITenantServiceRelService tenantServiceRelService;

    @DubboReference(version = "1.0.0")
    ISysCfgService iSysCfgService;

    @Resource
    IOrderLimitService iOrderLimitService;
    @Override
    public List<ConsumeOilFlow> getConsumeOilFlow(List<Long> flowIds) {
        LambdaQueryWrapper<ConsumeOilFlow> qw = new LambdaQueryWrapper<>();
        qw.in(ConsumeOilFlow::getId, flowIds);
        return this.list(qw);
    }

    @Override
    public List<ConsumeOilFlow> getConsumeOilFlowByServiceId(Long serviceUserId,String endTime,String startTime) {
        LambdaQueryWrapper<ConsumeOilFlow> qw = new LambdaQueryWrapper<>();
        qw.eq(ConsumeOilFlow::getUserId,serviceUserId)
                .le(ConsumeOilFlow::getCreateTime,endTime)
                .ge(ConsumeOilFlow::getCreateTime,startTime)
                .eq(ConsumeOilFlow::getCostType,2)
                .groupBy(ConsumeOilFlow::getOrderId,ConsumeOilFlow::getOilAffiliation,ConsumeOilFlow::getVehicleAffiliation,ConsumeOilFlow::getUserType,ConsumeOilFlow::getPayUserType,ConsumeOilFlow::getUserId)
                .orderByDesc(ConsumeOilFlow::getCreateTime,ConsumeOilFlow::getId);
        return this.list(qw);
    }

    @Override
    public Page<ConsumeOilFlow> queryConsumeOilFlowsNew(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize) {
        Page<ConsumeOilFlow> consumeOilFlowPage = consumeOilFlowMapper.queryConsumeOilFlowsNew(new Page<>(pageNum, pageSize), advanceExpireOutVo);
        return consumeOilFlowPage;
    }

    @Override
    public List<ConsumeOilFlow> getConsumeOilFlowByOrderId(String orderId, Integer userType, Integer payUserType, Long tenantId) {
        LambdaQueryWrapper<ConsumeOilFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsumeOilFlow::getOrderId, orderId);
        queryWrapper.eq(ConsumeOilFlow::getCostType, OrderAccountConst.CONSUME_COST_TYPE.TYPE2);
        if (tenantId != null && tenantId.longValue() > 0) {
            queryWrapper.eq(ConsumeOilFlow::getTenantId, tenantId);
        }
        if (userType > 0) {
            queryWrapper.eq(ConsumeOilFlow::getUserType, userType);
        }
        if (payUserType > 0) {
            queryWrapper.eq(ConsumeOilFlow::getPayUserType, payUserType);
        }
        return this.list(queryWrapper);
    }

    @Override
    public ConsumeOilFlow getConsumeOilFlow(Long flowId) {
        return consumeOilFlowMapper.getConsumeOilFlow(flowId);
    }

    @Override
    public ConsumeOilFlowWxOutDto getConsumeOilFlowByWx(ConsumeOilFlowVo consumeOilFlowVo, Integer pageNum, Integer pageSize) {
        if (consumeOilFlowVo.getUserId() <= 0) {
            throw new BusinessException("请输入用户id");
        }
        if (consumeOilFlowVo.getProductId() <= 0) {
            throw new BusinessException("请输入产品id");
        }
        if (StringUtils.isNotEmpty(consumeOilFlowVo.getFleetName())) {
            //根据车队名称模糊查询
            List<Long> tenantIds = new ArrayList<Long>();
            List<SysTenantDef> list = sysTenantDefService.getSysTenantDefByName(consumeOilFlowVo.getFleetName());
            if (list == null || list.size() <= 0) {
                tenantIds.add(-1L);
            } else {
                for (SysTenantDef sysTenantDef : list) {
                    tenantIds.add(sysTenantDef.getId());
                }
            }
            if (consumeOilFlowVo.getTenantId() != null) {
                tenantIds.add(consumeOilFlowVo.getTenantId());
            }
            consumeOilFlowVo.setTenantIds(tenantIds);
        } else if (consumeOilFlowVo.getTenantId() != null && consumeOilFlowVo.getTenantId() > 0) {
            List<Long> tenantIds = new ArrayList<Long>();
            tenantIds.add(consumeOilFlowVo.getTenantId());
            consumeOilFlowVo.setTenantIds(tenantIds);
        }

        if (StringUtils.isNotBlank(consumeOilFlowVo.getIsExpire())) {
            consumeOilFlowVo.setStateList(Arrays.asList(consumeOilFlowVo.getIsExpire().split(",")));
//            String[] stateArr = consumeOilFlowVo.getIsExpire().split("\\,");
        }
        consumeOilFlowVo.setCostType(OrderAccountConst.CONSUME_COST_TYPE.TYPE2);
        ConsumeOilFlowWxOutDto consumeOilFlowSumByWx = null;
        try {
            consumeOilFlowSumByWx = consumeOilFlowMapper.getConsumeOilFlowSumByWx(consumeOilFlowVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ConsumeOilFlowWxOutDto consumeOilFlowSumByWx = consumeOilFlowMapper.getConsumeOilFlowSumByWx(consumeOilFlowVo);
        long marginBalance = 0;
        long expireBalance = 0;
        long platformServiceCharge = 0;
        String productName = "";
        if (consumeOilFlowSumByWx != null) {
            marginBalance = consumeOilFlowSumByWx.getMarginBalance() == 0 ? 0 : consumeOilFlowSumByWx.getMarginBalance();
            expireBalance = consumeOilFlowSumByWx.getExpireBalance() == 0 ? 0 : consumeOilFlowSumByWx.getExpireBalance();
            platformServiceCharge = consumeOilFlowSumByWx.getPlatformServiceCharge() == 0 ? 0 : consumeOilFlowSumByWx.getPlatformServiceCharge();
            productName = consumeOilFlowSumByWx.getProductName() == null ? "" : consumeOilFlowSumByWx.getProductName();
        }
        Page<ConsumeOilFlowWxDto> consumeOilFlowByWxPage = null;
        try {
            consumeOilFlowByWxPage = consumeOilFlowMapper.getConsumeOilFlowByWx(new Page<>(pageNum, pageSize), consumeOilFlowVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Page<ConsumeOilFlowWxDto> consumeOilFlowByWxPage = consumeOilFlowMapper.getConsumeOilFlowByWx(new Page<>(pageNum,pageSize),consumeOilFlowVo);
        List<ConsumeOilFlowWxDto> list = consumeOilFlowByWxPage.getRecords();
        List<ConsumeOilFlowWxDto> outList = new ArrayList<>();
        for (ConsumeOilFlowWxDto dto : list) {
            ConsumeOilFlowWxDto out = new ConsumeOilFlowWxDto();
            BeanUtils.copyProperties(dto, out);
            LocalDateTime createDate = dto.getCreateDate();
            LocalDateTime getDate = dto.getGetDate();
            String otherName = dto.getOtherName();
            int state = dto.getState();
            out.setCreateDate(createDate);
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(dto.getTenantId());
            if (sysTenantDef != null) {
                out.setSourceFleet(sysTenantDef.getName());
            } else {
                out.setSourceFleet(otherName);
            }
            String stateName = readisUtil.getSysStaticData(OrderAccountConst.COMMON_KEY.MARGIN_STATE, String.valueOf(state)).getCodeName();
            out.setStateName(stateName);
            //账期
            int paymentDays = CommonUtil.getDifferDay(createDate, getDate);
            out.setPaymentDays(paymentDays);
            outList.add(out);
        }
        consumeOilFlowByWxPage.setRecords(outList);
        ConsumeOilFlowWxOutDto outDto = new ConsumeOilFlowWxOutDto();
        outDto.setPage(consumeOilFlowByWxPage);
        outDto.setMarginBalance(marginBalance);
        outDto.setExpireBalance(expireBalance);
        outDto.setPlatformServiceCharge(platformServiceCharge);
        outDto.setProductName(productName);

        if (outDto != null) {
            productName = outDto.getProductName();
            if (productName == null || "".equals(productName) || " ".equals(productName)) {
                //根据产品id查询产品信息
                ServiceProduct serviceProduct = serviceProductService.getServiceProduct(consumeOilFlowVo.getProductId());
                if (serviceProduct == null) {
                    throw new BusinessException("根据产品id：" + consumeOilFlowVo.getProductId() + ",未找到产品信息");
                }
                outDto.setProductName(serviceProduct.getProductName());
            }
        }
        return outDto;
    }

    @Override
    public ConsumeOilFlowDetailsWxOutDto getConsumeOilFlowDetailsByWx(ConsumeOilFlowVo consumeOilFlowVo) {
        Long flowId = consumeOilFlowVo.getFlowId();
        String flowIds = consumeOilFlowVo.getFlowIds();
        if (flowId <= 0) {
            throw new BusinessException("流水号不合法!");
        }
        ConsumeOilFlow flow = consumeOilFlowMapper.getConsumeOilFlows(flowId);
        if (flow == null) {
            throw new BusinessException("根据流水号：" + flowId + "未找到司机加油记录");
        }
        ConsumeOilFlowDetailsWxOutDto out = new ConsumeOilFlowDetailsWxOutDto();
        BeanUtils.copyProperties(flow,out);
        out.setStateName(readisUtil.getSysStaticData(OrderAccountConst.COMMON_KEY.MARGIN_STATE, String.valueOf(out.getState())).getCodeName());
        out.setCreateDate(flow.getCreateTime());
        //消费人
        flow.setOtherName(flow.getOtherName()==null?"":flow.getOtherName());
        String consumeName = flow.getOtherName() + "(" + flow.getOtherUserBill() + ")";
        out.setConsumeName(consumeName);
//        UserDataInfo user = userDataInfoService.getUserDataInfo(flow.getUserId());
//        if (user != null) {
//            Long tenantId = user.getTenantId();
//            if (tenantId != null && tenantId > 0) {
//                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
//                if (sysTenantDef == null) {
//                    throw new BusinessException("未找到消费人的归属车队!");
//                } else {
//                    out.setBelongingFleet(sysTenantDef.getName());
//                }
//            } else {
//                out.setBelongingFleet("未加入车队");
//            }
//        }
        //归属车队
        if (flow.getTenantId()!=null && flow.getTenantId() > 0) {
            out.setBelongingFleet(sysTenantDefService.getSysTenantDef(flow.getTenantId()).getName());
        }else {
            out.setBelongingFleet("未加入车队");
        }
        //资金来源车队
        if (flow.getTenantId()!=null) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(flow.getTenantId());
            if (sysTenantDef == null) {
                out.setSourceFleet(flow.getOtherName());
                //平安现金加油没有租户
                //throw new BusinessException("根据车队id" + flow.getTenantId() + " 未找到资金来源车队!");
            } else {
                out.setSourceFleet(sysTenantDef.getName());
            }
        }
        //账期
        int paymentDays = CommonUtil.getDifferDay(flow.getCreateTime(), flow.getGetDate());
        out.setPaymentDays(paymentDays);
        List<String> flowIds1 = null;
        if (flowIds == null || StringUtils.isBlank(flowIds)) {
            flowIds1 = null;
        } else {
            flowIds1 = Arrays.asList(flowIds.split(","));
        }
        ConsumeOilFlowDto dto = consumeOilFlowMapper.doQueryConsumeOilFlowTxm(flowIds1);
        Long amount = dto.getAmount() > 0 ? dto.getAmount() : 0L;
        Long platformAmount = dto.getPlatformAmount() > 0 ? dto.getPlatformAmount() : 0L;
        String oilRise = dto.getOilRise();
        out.setAmount(amount);
        out.setPlatformAmount(platformAmount);
        out.setOilRise(Float.valueOf(oilRise));
        return out;
    }


    /**
     * 司机小程序
     * niejeiwei
     * APP接口-优惠加油-加油记录
     * 50000
     *
     * @param vo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<ConsumeOilFlowDto> getConsumeOilFlowOut(ConsumeOilFlowVo vo, Integer pageNum, Integer pageSize, String accessToken) {
        vo.setCostType(OrderAccountConst.CONSUME_COST_TYPE.TYPE1);
        Page<ConsumeOilFlowDto> page = new Page<>(pageNum, pageSize);
        Page<ConsumeOilFlowDto> consumeOilFlowOut = baseMapper.getConsumeOilFlowOut(page, vo);
        return consumeOilFlowOut;
    }


    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-加油记录-加油详情
     * 50001
     *
     * @param
     * @return
     */
    @Override
    public ConsumeOilFlowDetailsOutDto getConsumeOilFlowDetails(Long flowId) {
        if (flowId <= 0) {
            throw new BusinessException("流水号不合法!");
        }
        ConsumeOilFlow flow = this.getConsumeOilFlow(flowId);
        if (flow == null) {
            throw new BusinessException("根据流水号：" + flowId + "未找到司机加油记录");
        }
        ConsumeOilFlowDetailsOutDto out = new ConsumeOilFlowDetailsOutDto();
        BeanUtil.copyProperties(flow, out);
        out.setMarginBalance((flow.getMarginBalance() == null ? 0L : flow.getMarginBalance()) - (flow.getAdvanceFee() == null ? 0L : flow.getAdvanceFee()));
        long payAmount = ((flow.getOilBalance() == null ? 0L : flow.getOilBalance()) + (flow.getBalance() == null ? 0L : flow.getBalance()) + (flow.getMarginBalance() == null ? 0L : flow.getMarginBalance()));
        out.setPayAmount(payAmount);
        return out;
    }


    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-加油记录-评
     * 50002
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public ConsumeOilFlowDetailsOutDto evaluateConsumeOilFlow(ConsumeOilFlowVo vo) {
        if (vo.getFlowId() <= 0) {
            throw new BusinessException("流水号不合法!");
        }
        ConsumeOilFlow flow = this.getConsumeOilFlow(vo.getFlowId());
        if (flow == null) {
            throw new BusinessException("根据流水号：" + vo.getFlowId() + "未找到司机加油记录");
        }
        flow.setIsEvaluate(OrderAccountConst.CONSUME_OIL_FLOW.IS_EVALUATE_YES);
        flow.setEvaluatePrice(vo.getEvaluatePrice());
        flow.setEvaluateQuality(vo.getEvaluateQuality());
        flow.setEvaluateService(vo.getEvaluateService());
        this.saveOrUpdate(flow);
        ConsumeOilFlowDetailsOutDto dto = new ConsumeOilFlowDetailsOutDto();
        dto.setFlag("y");
        return dto;
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-确认支付
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public ConsumeOilFlowDetailsOutDto confirmPayForOil(ConsumeOilFlowVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (vo.getUserId() <= 0) {
            throw new BusinessException("请输入用户id");
        }
        if (vo.getProductId() <= 0) {
            throw new BusinessException("请输入产品id");
        }
        if (vo.getOilPrice() <= 0) {
            throw new BusinessException("请输入加油价格");
        }
        if (StringUtils.isEmpty(vo.getOilRiseTemp())) {
            throw new BusinessException("请输入加油升数");
        }
        if (StringUtils.isEmpty(vo.getPayPasswd())) {
            throw new BusinessException("请输入支付密码");
        }
        if (StringUtils.isBlank(vo.getPlateNumber())) {
            throw new BusinessException("请输入车牌号");
        } else {
            VehicleDataInfo VehicleDataInfo = vehicleDataInfoService.getVehicleDataInfo(vo.getPlateNumber());
            if (VehicleDataInfo == null) {
                throw new BusinessException("该车牌号：" + vo.getPlateNumber() + " 不存在系统当中");
            }
        }
        boolean isInit = userDataInfoService.doInits(vo.getUserId());
        if (isInit) {
            throw new BusinessException("为了您的账户安全，请前往我的钱包及时修改密码!");
        }
        //校验支付密码（对于支付密码处理（正确、错误））
        iPayByCashService.DealPassError(vo.getUserId(), vo.getPayPasswd());
        //根据产品id查询产品信息
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(vo.getProductId());
        if (serviceProduct == null) {
            throw new BusinessException("根据产品id：" + vo.getProductId() + ",未找到产品信息");
        }
        BigDecimal b1 = new BigDecimal(vo.getOilRiseTemp()).setScale(2, BigDecimal.ROUND_HALF_UP);
        float oilRise = b1.floatValue();
        if (StringUtils.isNotBlank(vo.getLocaleBalanceState()) && vo.getLocaleBalanceState().equals(String.valueOf(ServiceConsts.LOCALE_BALANCE_STATE.YES))) {
            if (vo.getAmountFee() <= 0) {
                throw new BusinessException("请输入加油总金额");
            }
        } else {
            // 升数乘以单价 总金额
            vo.setAmountFee(new BigDecimal((float) vo.getOilPrice() * oilRise).setScale(0, BigDecimal.ROUND_HALF_UP).longValue());
        }
        PayForOilInDto in = new PayForOilInDto();
        in.setAmountFee(vo.getAmountFee());
        in.setUserId(vo.getUserId());
        in.setServiceUserId(serviceProduct.getServiceUserId());
        in.setProductId(vo.getProductId());
        in.setOilPrice(vo.getOilPrice());
        in.setOilRise(oilRise);
        in.setLocaleBalanceState(vo.getLocaleBalanceState());
        in.setTenantId(loginInfo.getTenantId());
        in.setFromType(OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE1);
        in.setPlateNumber(vo.getPlateNumber());
        // 油卡消费接口
        ConsumeOilFlow flow = iOperationOilService.payForOil(in, accessToken);

        // TODO 加新逻辑
        TenantServiceRel serviceRel = tenantServiceRelService.getTenantServiceRel(loginInfo.getTenantId(), serviceProduct.getServiceUserId());
        if (serviceRel != null && serviceRel.getBalanceType() != null) {
            Integer balanceType = serviceRel.getBalanceType();
            if (balanceType != null && balanceType == 1 && serviceRel.getPaymentDays() != null && serviceRel.getPaymentDays() == 0) {
                setPayData(accessToken, flow);
            } else if (balanceType != null && balanceType == 2 && serviceRel.getPaymentDays() != null &&
                    serviceRel.getPaymentMonth() != null && serviceRel.getPaymentDays()==0  && serviceRel.getPaymentMonth()==0) {
                setPayData(accessToken, flow);
            } else if (balanceType != null && balanceType == 3) {
                setPayData(accessToken, flow);
            }
        }
        ConsumeOilFlowDetailsOutDto out = new ConsumeOilFlowDetailsOutDto();
        BeanUtil.copyProperties(flow, out);
        long payAmount = ((flow.getOilBalance() == null ? 0L : flow.getOilBalance()) + (flow.getBalance() == null ? 0L : flow.getBalance()));
        out.setFlowId(flow.getOrderNum());
        out.setPayAmount(payAmount);
        return out;
    }

    /**
     * 加 转入平台支付的方法
     * @param accessToken
     * @param u
     */
    private void setPayData(String accessToken,  ConsumeOilFlow us) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer[] dealStates = new Integer[]{OrderAccountConst.STATE.INIT};
        List<ConsumeOilFlow> cList = this.getConsumeOilFlowNew(OrderAccountConst.CONSUME_COST_TYPE.TYPE2, dealStates,
                null, -1, -1,us.getOrderId());
        for (ConsumeOilFlow map:cList) {
            String flowIds = map.getFlowIds();
            Long tenantId = map.getTenantId();
            Long flowId = map.getFlowId();
            Long undueAmount = map.getUndueAmount();
            loginInfo.setTenantId(tenantId);
            try {
                ConsumeOilFlow c = this.get(flowId);
                Long payFlowId = iOrderOilSourceService.payTurnCash(c, undueAmount, accessToken);
                if (payFlowId == null) {
                    continue;
                }
                List<ConsumeOilFlow> consumeOilFlowList = this.doQueryConsumeOilFlow(flowIds);
                for (ConsumeOilFlow u : consumeOilFlowList) {
                        //一笔一笔做业务
                        iOrderLimitService.marginTurnCash(u.getUserId(), u.getVehicleAffiliation(), u.getUndueAmount(),
                                u.getId(), u.getTenantId(), OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2,
                                u.getOilAffiliation(), payFlowId, u, accessToken);
                    u.setExpiredAmount((u.getExpiredAmount() == null ? 0L : u.getExpiredAmount()) + u.getUndueAmount());
                    u.setUndueAmount(0L);
                    u.setGetResult("处理成功");
                    u.setState(OrderAccountConst.STATE.SUCCESS);
                    this.saveOrUpdate(u);
                }
            } catch (Exception ex) {
                //只要有一笔失败就全部失败事务回滚
                log.error("未到期转已到期失败FLOW_ID: " + flowIds + " 错误信息:" + ex.getMessage());
                ex.printStackTrace();
                this.updateConsumeOilFlow(flowIds, OrderAccountConst.STATE.FAIL, "处理失败");
                continue;
            }
        }
    }
    @Override
    public ConsumeOilFlow createConsumeOilFlow(Long userId, String userBill, String userName, Integer costType, String orderId, Long amount, Long oilPrice, Float oilRise, Long otherUserId, String otherUserBill, String otherName, String vehicleAffiliation, Long tenantId, Long productId, Integer isNeedBill, int userType, int payUserType) {
        ConsumeOilFlow cof = new ConsumeOilFlow();
        cof.setUserId(userId);
        //会员体系改造开始
        cof.setUserType(userType);
        cof.setPayUserType(payUserType);
        //会员体系改造结束
        cof.setUserBill(userBill);
        cof.setUserName(userName);
        cof.setCostType(costType);
        cof.setOrderId(orderId);
        cof.setAmount(amount);
        cof.setOilPrice(oilPrice);
        cof.setOilRise(oilRise);
        cof.setOtherUserId(otherUserId);
        cof.setOtherUserBill(otherUserBill);
        cof.setOtherName(otherName);
        cof.setVehicleAffiliation(vehicleAffiliation);
        cof.setProductId(productId);
        cof.setTenantId(tenantId);
        cof.setIsNeedBill(isNeedBill);
        cof.setCreateTime(LocalDateTime.now());

        return cof;
    }


    /**
     * APP接口-预支界面
     * 司机小程序
     * niejiewei
     * 50006
     *
     * @param vo
     * @return
     */
    @Override
    public Long advanceUI(ConsumeOilFlowVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = vo.getUserId();
        Integer userType = loginInfo.getUserType();
        if (userId <= 1) {
            throw new BusinessException("请输入用户id!");
        }
        Map<String, Object> map = iOpAccountService.getMarginBalanceUI(userId, userType);

        if (map == null) {
            throw new BusinessException("根据租户id：" + userId + "未找到用户的账户");
        }
        Long canAdvance = (Long) map.get(OrderAccountConst.ACCOUNT_KEY.canAdvance);
        if (canAdvance == null) {
            throw new BusinessException("根据租户id：" + userId + "未找到用户的可预支金额");
        }
        return canAdvance;
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-查询预支手续费
     * 50007
     *
     * @param vo
     * @return
     */
    @Override
    public Long getAdvanceFee(ConsumeOilFlowVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = vo.getUserId();
        Long amountFee = vo.getAdvanceAmount();
        Integer userType = loginInfo.getUserType();
        if (userId <= 1) {
            throw new BusinessException("请输入用户id!");
        }
        if (amountFee <= 0) {
            throw new BusinessException("请输入预支金额!");
        }
        Long serviceCharge = iPayManagerService.getAdvanceServiceCharge(userId, amountFee, null, userType);
        return serviceCharge;
    }


    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-预支
     * 50008
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public ConsumeOilFlowDetailsOutDto confirmAdvance(ConsumeOilFlowVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = vo.getUserId();
        Long amountFee = vo.getAdvanceAmount();
        String payPasswd = vo.getPayPasswd();//支付密码
        String payCode = vo.getPayCode();  //手机验证码
//        Integer userType = loginInfo.getUserType();
        Integer userType = 1;
        if (userId <= 1) {
            throw new BusinessException("请输入用户id!");
        }
        if (amountFee <= 0) {
            throw new BusinessException("请输入预支金额!");
        }
        if (StringUtils.isEmpty(payPasswd)) {
            throw new BusinessException("请输入支付密码");
        }
        boolean isInit = userDataInfoService.doInit(userId);
        if (isInit) {
            throw new BusinessException("为了您的账户安全，请前往我的钱包及时修改密码!");
        }
//        checkPasswordErrSV.DealPassError(userId,payPasswd);
        // TODO 校验支付密码（对于支付密码处理（正确、错误））
        iPayByCashService.doWithdrawal(accessToken, payPasswd, null);
        // 预支接口  userId  预支用户编号  amountFee 预支金额单位(分)  objId 预支业务编号    tenantId 租户id
        iPayManagerService.advancePayMarginBalance(userId, amountFee, 0L, -1L, userType, accessToken);
        ConsumeOilFlowDetailsOutDto dto = new ConsumeOilFlowDetailsOutDto();
        dto.setFlag("y");
        return dto;
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-可预支金额详情
     * 50009
     *
     * @param vo
     * @return
     */
    @Override
    public List<MarginBalanceDetailsOut> getAdvanceDetails(ConsumeOilFlowVo vo, String accessToken) {
        Long userId = vo.getUserId();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer userType = loginInfo.getUserType();
        if (userId == null || userId <= 1) {
            throw new BusinessException("请输入用户id!");
        }
        //获取用户未到期金额
        List<MarginBalanceDetailsOut> marginBalance = iOpAccountService.getMarginBalance(userId, userType);
        return marginBalance;
    }


    /**
     * 找油网加油_支付
     * niejiewei
     * 司机小程序
     * 50036
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public ConsumeOilFlowDetailsOutDto payForOrderOil(ConsumeOilFlowVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        long id = vo.getId();
        String payPasswd = vo.getPayPasswd();//支付密码
        if (id <= 0) {
            throw new BusinessException("请输入支付单号记录id");
        }
//        ServiceOrderInfo soi = serviceOrderInfoSV.getServiceOrderInfo(id);
        ServiceOrderInfo soi = iServiceOrderInfoService.getById(id);
        if (soi == null) {
            throw new BusinessException("根据支付单号记录id：" + id + " 未找到订单信息");
        }
        if (soi.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY) {
            throw new BusinessException("订单已支付，不能重复支付，请刷新页面！");
        } else if (soi.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.CANCEL_PAY) {
            throw new BusinessException("订单已取消，不能支付，请刷新页面！");
        }
        Long userId = soi.getUserId();
        Long productId = soi.getProductId();
        Long oilPrice = soi.getOilPrice();
        Long oilRiseTemp = soi.getOilLitre();
        String orderNum = soi.getOrderId();
        String plateNumber = soi.getPlateNumber();
        Long amountFee = soi.getOilFee();
        if (userId == null || userId <= 0) {
            throw new BusinessException("未找到司机id");
        }
        if (productId == null || productId <= 0) {
            throw new BusinessException("请输入产品id");
        }
        if (oilPrice == null || oilPrice <= 0) {
            throw new BusinessException("请输入加油价格");
        }
        if (oilRiseTemp == null || oilRiseTemp <= 0) {
            throw new BusinessException("请输入加油升数");
        }
        if (amountFee == null || amountFee <= 0) {
            throw new BusinessException("请输入加油金额");
        }
        if (StringUtils.isBlank(payPasswd)) {
            throw new BusinessException("请输入支付密码");
        }
        if (StringUtils.isBlank(orderNum)) {
            throw new BusinessException("请输入加油单号");
        }
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("请输入车牌号");
        } else {
            VehicleDataInfo VehicleDataInfo = vehicleDataInfoService.getVehicleDataInfo(plateNumber);
            if (VehicleDataInfo == null) {
                throw new BusinessException("该车牌号：" + plateNumber + " 不存在系统当中");
            }
        }
        boolean isInit = userDataInfoService.doInit(userId);
        if (isInit) {
            throw new BusinessException("为了您的账户安全，请前往我的钱包及时修改密码!");
        }
        // TODO 校验支付密码（对于支付密码处理（正确、错误））
        iPayByCashService.doWithdrawal(accessToken, payPasswd, null);
        //根据产品id查询产品信息
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(productId);
        if (serviceProduct == null) {
            throw new BusinessException("根据产品id：" + productId + ",未找到产品信息");
        } else {
            if (serviceProduct.getState() == null || serviceProduct.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2) {
                throw new BusinessException("产品id为：" + productId + "的产品状态无效");
            }
        }
        List<ConsumeOilFlow> list = this.getConsumeOilByOrderNum(orderNum, -1, -1);
        if (list != null && list.size() > 0) {
            throw new BusinessException("此支付单号：" + orderNum + "已经支付过");
        }

        //加油金额
        BigDecimal b1 = new BigDecimal((float) oilRiseTemp / 1000).setScale(2, BigDecimal.ROUND_HALF_UP);
        float oilRise = b1.floatValue();
        PayForOilInDto in = new PayForOilInDto();
        in.setAmountFee(amountFee);
        in.setUserId(userId);
        in.setServiceUserId(serviceProduct.getServiceUserId());
        in.setProductId(productId);
        in.setOilPrice(oilPrice);
        in.setOilRise(oilRise);
        in.setOrderNum(orderNum);
        in.setTenantId(loginInfo.getTenantId());
        in.setFromType(OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE2);
        in.setPlateNumber(plateNumber);
        in.setId(id);
        ConsumeOilFlow flow = iOperationOilService.payForOil(in, accessToken);
        ConsumeOilFlowDetailsOutDto out = new ConsumeOilFlowDetailsOutDto();
        BeanUtils.copyProperties(out, flow);
        out.setMarginBalance((flow.getMarginBalance() == null ? 0L : flow.getMarginBalance()) - (flow.getAdvanceFee() == null ? 0L : flow.getAdvanceFee()));
        long payAmount = ((flow.getOilBalance() == null ? 0L : flow.getOilBalance()) + (flow.getBalance() == null ? 0L : flow.getBalance()) + (flow.getMarginBalance() == null ? 0L : flow.getMarginBalance()));
        out.setPayAmount(payAmount);
        out.setServiceOrderId(id);
        return out;
    }

    /**
     * 根据找油网单号查询司机加油记录
     *
     * @param orderNum    找油网单号
     * @param userType    收款方用户类型
     * @param payUserType 付款方用户类型
     * @return list
     * @throws Exception
     */
    @Override
    public List<ConsumeOilFlow> getConsumeOilByOrderNum(String orderNum, Integer userType, Integer payUserType) {
        LambdaQueryWrapper<ConsumeOilFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumeOilFlow::getOrderNum, orderNum)
                .eq(ConsumeOilFlow::getCostType, OrderAccountConst.CONSUME_COST_TYPE.TYPE2);
        if (userType > 0) {
            wrapper.eq(ConsumeOilFlow::getUserType, userType);
        }
        if (payUserType > 0) {
            wrapper.eq(ConsumeOilFlow::getPayUserType, payUserType);
        }
        return baseMapper.selectList(wrapper);
    }


    /**
     * 油账户列表
     * niejiewei
     * 司机小程序
     * 50040
     *
     * @param vo
     * @return
     */
    @Override
    public ConsumeOilFlowDetailsOutDto getOilAccount(ConsumeOilFlowVo vo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = vo.getUserId();
        Integer userType = vo.getUserType();
        if (userId <= 1) {
            throw new BusinessException("请输入用户id");
        }
        if (userType <= 0) {
            userType = loginInfo.getUserType();
        }
        ConsumeOilFlowDto consumeOilFlowDto = baseMapper.doQueryOilSum(userId, userType);

        Long amountSum = null;
        if (consumeOilFlowDto != null && consumeOilFlowDto.getAmountSum() != null) {
            amountSum = consumeOilFlowDto.getAmountSumL()==null?consumeOilFlowDto.getAmountSum():consumeOilFlowDto.getAmountSum()+consumeOilFlowDto.getAmountSumL();
        }
        Long sharedOilSum = this.doQuerySharedOilSum(userId, userType);
        Map privateMap = this.doQueryPrivateOilSum(userId, userType);
        List<OilAccountOutDto> list = new ArrayList<>();
        if (sharedOilSum > 0) {
            OilAccountOutDto oilAccountOut = new OilAccountOutDto();
            oilAccountOut.setAmount(sharedOilSum);//共享油站费用
            oilAccountOut.setRemark("全平台所有油站");
            oilAccountOut.setUserId(userId);
            oilAccountOut.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            list.add(oilAccountOut);
        }
        Iterator<Map.Entry<Long, Long>> it = privateMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Long> entry = it.next();
            OilAccountOutDto out = new OilAccountOutDto();
            Long key = entry.getKey();
            Long value = entry.getValue();
            Page<ServiceProduct> productPage = iServiceProductService.doQueryPrivateProduct(key, pageNum, pageSize);
//            long totalNum = page.getTotalNum();
            Long totalNum = productPage.getTotal();
//            List<Map> pageList = page.getItems();
            List<ServiceProduct> records = productPage.getRecords();
            String remark = "";
            if (totalNum > 0) {
                if (totalNum == 1) {
//                    remark = (String) pageList.get(0).get("productName");
                    remark = records.get(0).getProductName();
                } else {
//                    remark = (String) pageList.get(0).get("productName") + "," + (String) pageList.get(1).get("productName");
                    String productName = "";
                    if (records.size() > 2) {
                        productName = records.get(1).getProductName();
                    }
                    remark = records.get(0).getProductName() + "," + productName;
                }
                remark += ("...等" + totalNum + "个加油站");
            } else {
                remark = "0个加油站";
            }
            out.setAmount(value);//私有油站费用
            out.setRemark(remark);
            out.setTenantId(key);
            out.setUserId(userId);
            out.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            list.add(out);
        }
        ConsumeOilFlowDetailsOutDto dto = new ConsumeOilFlowDetailsOutDto();
        dto.setAmountSum(amountSum);//司机账户总金额
        dto.setList(list);
        return dto;
    }

    @Override
    public IPage<ProductNearByOutDto> getOilStationDetails(ConsumeOilFlowVo vo, String accessToken, Integer pageNum, Integer pageSize) {
        if (vo.getAmount() == null || vo.getAmount() < 0) {
            throw new BusinessException("请输入正确的金额");
        }
        List<ProductNearByOutDto> productNearByOutDtos = iServiceProductService.queryNearbyOil1(vo.getLongitude(),
                vo.getLatitude(), vo.getIsShare(), vo.getAmount(), vo.getTenantId(), accessToken);
        Page<ProductNearByOutDto> page = new Page<>(pageNum, pageNum);
        page.setRecords(productNearByOutDtos);
        page.setTotal(productNearByOutDtos.size());
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        return page;
    }

    @Override
    public List<OilServiceOutDto> getOilStationDetailslist(Long userId, List<OilServiceInDto> products, Integer isNeedBill, Long amount, Long tenantId) {
        if (userId == null || userId < 1) {
            throw new BusinessException("请输入用户编号");
        }
        if (products == null) {
            throw new BusinessException("请输入产品信息集合");
        }
        if (amount == null || amount < 0) {
            throw new BusinessException("请输入油费金额");
        }
        List<OilServiceOutDto> out = new ArrayList<OilServiceOutDto>();
        //油费金额是否预存
        for (OilServiceInDto oilServiceIn : products) {
            //服务商用户id
            Long serviceUserId = oilServiceIn.getServiceId();
            //油价
            Long oilPrice = (oilServiceIn.getOilPrice() == null ? 0L : oilServiceIn.getOilPrice());

            //根据服务商id查询租户与服务商关系
            ServiceInfo serviceInfo = iServiceInfoService1.getServiceInfoById(serviceUserId);
            if (serviceInfo == null) {
                throw new BusinessException("未找到服务商信息");
            }
            TenantServiceRel tenantServiceRel = null;
            long noUseQuotaAmt = 0l;
            tenantServiceRel = tenantServiceRelService.getTenantServiceRel(tenantId, serviceUserId);
            if (tenantServiceRel != null && tenantServiceRel.getQuotaAmt() != null && tenantServiceRel.getQuotaAmt() >= 0) {
                noUseQuotaAmt = tenantServiceRel.getQuotaAmt() - (tenantServiceRel.getUseQuotaAmt() == null ? 0L : tenantServiceRel.getUseQuotaAmt());
                if (noUseQuotaAmt < 0) {
                    noUseQuotaAmt = 0;
                }
                if (noUseQuotaAmt < amount) {
                    amount = noUseQuotaAmt;
                }
            }

            OilServiceOutDto oso = new OilServiceOutDto();
            float oilRise = 0.0f;
            if (oilPrice != null && oilPrice > 0) {
                oilRise = new BigDecimal((float) amount / oilPrice).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            }
            oso.setProductId(oilServiceIn.getProductId());
            oso.setOilPrice(oilPrice);
            oso.setOilRise(oilRise);
            oso.setConsumeOilBalance(amount);
            out.add(oso);
        }
        return out;
    }

    @Override
    public boolean dealCancel(String tradeId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (org.apache.commons.lang3.StringUtils.isBlank(tradeId)) {
            throw new BusinessException("订单号不能为空！");
        }
        long serviceUserId = 0L;
        try {
            serviceUserId = Long.parseLong(iSysCfgService.getCfgVal("ZHAOYOU_TRADE_SERVICE_USER_ID", 0, String.class).toString());
        } catch (Exception e) {
            throw new BusinessException("未找到服务商配置信息！");
        }
        if (loginInfo == null || loginInfo.getUserInfoId() == null || loginInfo.getUserInfoId().longValue() != serviceUserId) {
            throw new BusinessException("对不起，你不能取消订单，如需取消订单请联系油站工作人员。");
        }
        try {
            iServiceOrderInfoService.updateServiceOrderState(Long.parseLong(tradeId), ServiceConsts.SERVICE_ORDER_STATE.CANCEL_PAY, accessToken);
        } catch (BusinessException be) {
            throw new BusinessException(String.valueOf(be));
        } catch (Exception e) {
            throw new BusinessException(String.valueOf(e));
        }
        return true;
    }

    @Override
    public List<ConsumeOilFlow> getConsumeOilFlowNew(int costType, Integer[] states, Date getDate, Integer userType, Integer payUserType,String orderId) {
        List<ConsumeOilFlow> list = new ArrayList<>();
        try{
            list =  consumeOilFlowMapper.getConsumeOilFlowNew(costType, states, getDate, userType, payUserType,orderId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ConsumeOilFlow> doQueryConsumeOilFlow(String flowIds) {
        String[] flowId = flowIds.split(",");
        List<Long> list = new ArrayList<>();
        for (String cFlowId : flowId) {
            list.add(Long.valueOf(cFlowId));
        }
        LambdaQueryWrapper<ConsumeOilFlow> consumeOilFlowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeOilFlowLambdaQueryWrapper.in(ConsumeOilFlow::getId, list);
        return super.list(consumeOilFlowLambdaQueryWrapper);
    }

    @Override
    public void updateConsumeOilFlow(String flowIds, int state, String getResult) {
        String [] flowId = flowIds.split(",");
        List<Long> list = new ArrayList<>();
        for(String cFlowId:flowId){
            list.add(Long.valueOf(cFlowId));
        }
        LambdaUpdateWrapper<ConsumeOilFlow> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(ConsumeOilFlow::getState,state).set(ConsumeOilFlow::getGetResult,getResult)
                .in(ConsumeOilFlow::getId,flowIds);
       super.update(lambdaUpdateWrapper);
    }


    public Long doQuerySharedOilSum(Long userId, Integer userType) {
        Long orderAmt = 0L;
        Long oilAmt = 0L;
        ConsumeOilFlowDto listOrder = baseMapper.doQuerySharedOilSum(userId, userType);
        ConsumeOilFlowDto listOil = baseMapper.doQuerySharedOilSums(userId, userType);

        if (listOrder != null) {
            orderAmt = listOrder.getOrderAmt();
        }
        if (listOil != null) {
            oilAmt = listOil.getOilAmt();
        }
        return orderAmt + oilAmt;
    }

    public Map<Long, Long> doQueryPrivateOilSum(Long userId, Integer userType) {
        Map<Long, Long> map = new HashMap();
        List<ConsumeOilFlowDto> listOrder = baseMapper.selectSqlOrder(userId, userType);
        List<ConsumeOilFlowDto> listOil = baseMapper.selectSqlOil(userId, userType);
//        Iterator itOrder = listOrder.iterator();
//        Iterator itOil = listOil.iterator();
//        while (itOrder.hasNext()){
//            Map orderMap = (Map) itOrder.next();
//            Long key = DataFormat.getLongKey(orderMap ,"suorceTenantId");
//            Long value = DataFormat.getLongKey(orderMap ,"oilBalance");
//            map.put(key,value);
//        }
//
//        while (itOil.hasNext()){
//            Map oilMap = (Map) itOil.next();
//            Long key = DataFormat.getLongKey(oilMap,"suorceTenantId");
//            Long value = DataFormat.getLongKey(oilMap,"oilBalance");
//            if(map.containsKey(key)){
//                map.put(key, map.get(key)+value);
//            }else {
//                map.put(key,value);
//            }
//        }
        for (ConsumeOilFlowDto dto : listOrder) {
            Long key = dto.getSuorceTenantId();
            Long value = dto.getOilBalance();
            map.put(key, value);
        }
        for (ConsumeOilFlowDto dto : listOil) {
            Long key = dto.getSuorceTenantId();
            Long value = dto.getOilBalance();
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + value);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }


}
