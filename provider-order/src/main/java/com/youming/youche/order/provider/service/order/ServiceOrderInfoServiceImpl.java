package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.youka.IServiceProductOilsService;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.youka.ServiceProductOils;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.other.IEvaluateInfoService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.api.order.other.IServiceOrderInfoService;
import com.youming.youche.order.domain.EvaluateInfo;
import com.youming.youche.order.domain.order.ServiceOrderInfo;
import com.youming.youche.order.dto.OilServiceInDto;
import com.youming.youche.order.dto.OilServiceOutDto;
import com.youming.youche.order.dto.PayDetailsDto;
import com.youming.youche.order.dto.QueryDriverOrderDto;
import com.youming.youche.order.dto.order.OilVehiclesAndTenantDto;
import com.youming.youche.order.dto.order.QueryServiceOrderInfoDetailsDto;
import com.youming.youche.order.dto.order.ServiceOrderInfoListDto;
import com.youming.youche.order.provider.mapper.order.ServiceOrderInfoMapper;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
* <p>
    *  服务实现类
    * </p>
* @author wuhao
* @since 2022-05-18
*/
@DubboService(version = "1.0.0")
    public class ServiceOrderInfoServiceImpl extends BaseServiceImpl<ServiceOrderInfoMapper, ServiceOrderInfo> implements IServiceOrderInfoService {

    @Resource
    private LoginUtils loginUtils;
    @Resource
    RedisUtil redisUtil;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IServiceProductService iServiceProductService;

    @DubboReference(version = "1.0.0")
    IServiceProductOilsService iServiceProductOilsService;

    @DubboReference(version = "1.0.0")
    IOilPriceProvinceService iOilPriceProvinceService;

    @Resource
    IOperationOilService iOperationOilService;

    @DubboReference(version = "1.0.0")
    ISysCfgService iSysCfgService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    IOrderReceiptService iOrderReceiptService;

    @Resource
    IEvaluateInfoService evaluateInfoService;
    @DubboReference(version = "1.0.0")
    IServiceProductService serviceProductService;
    @Override
    public Long saveOrUpdates(ServiceOrderInfo info,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (info != null) {
            SysOperLogConst.OperType operType = SysOperLogConst.OperType.Add;
            String des = "新增订单";
            if (info.getId() != null && info.getId() > 0) {//修改
                operType = SysOperLogConst.OperType.Update;
                des = "修改订单";
            }else{
                info.setCreateTime(LocalDateTime.now());
                if (info.getOrderType() == null || info.getOrderType() != ServiceConsts.SERVICE_ORDER_TYPE.OR_CODE) {
                    info.setOrderState(ServiceConsts.SERVICE_ORDER_STATE.NO_PAY);
                }
                info.setCreateUserId(loginInfo.getUserInfoId());
            }
            info.setUpdateTime(LocalDateTime.now());
            if (info.getOrderType() == null || info.getOrderType() <= 0) {
                throw new BusinessException("订单类型不能为空！");
            }
            if (info.getProductId() == null || info.getProductId() <= 0) {
                throw new BusinessException("服务商产品id不能为空！");
            }
            if (info.getOrderType().intValue() == ServiceConsts.SERVICE_ORDER_TYPE.ZHAOYOU) {
                if (StringUtils.isBlank(info.getStationId())) {
                    throw new BusinessException("油站id不能为空！");
                }
                if (StringUtils.isBlank(info.getOrderId())) {
                    throw new BusinessException("订单id不能为空！");
                }
                if (StringUtils.isBlank(info.getOilsId())) {
                    throw new BusinessException("油品id不能为空！");
                }
            }
            if (info.getOilFee() == null || info.getOilFee() < 0) {
                throw new BusinessException("消费总金额不能为空！");
            }
            if (info.getOilLitre() == null || info.getOilLitre() < 0) {
                throw new BusinessException("加油升数不能为空！");
            }
            info.setTaskState(ServiceConsts.TASK_STATE.TASK_STATE);
            this.saveOrUpdate(info);
            sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.ServiceOrderInfo, info.getId(),operType,
                    des);
            return info.getId();
        }else{
            throw new BusinessException("支付信息不能为空！");
        }
    }

    @Override
    public void updateServiceOrderState(Long id, Integer orderState,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (id == null || id <= 0) {
            throw new BusinessException("支付订单号不能为空！");
        }
        if (orderState == null || orderState < 0) {
            throw new BusinessException("支付订单状态不能为空！");
        }
        ServiceOrderInfo info = baseMapper.selectById(id);
        if (info != null) {
            if (orderState.intValue() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY) {
                if (info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY) {
                    throw new BusinessException("订单已支付，不能重复支付！");
                }
            }else if(orderState.intValue() == ServiceConsts.SERVICE_ORDER_STATE.CANCEL_PAY){
                if (info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.MIDWAY_PAY) {
                    throw new BusinessException("订单已支付，不能撤单！");
                }
                if (info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY) {
                    throw new BusinessException("订单已支付成功，不能撤单！");
                }
                if (info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.CANCEL_PAY) {
                    throw new BusinessException("订单已撤单，不能重复撤单！");
                }
            }else if(info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.MIDWAY_PAY){
                if (info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY
                        || info.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.MIDWAY_PAY) {
                    throw new BusinessException("订单已支付，不能重复支付！");
                }
            }

            info.setOrderState(orderState);
            this.saveOrUpdate(info);
            info.setOrderStateName(info.getOrderState()==null?"": getSysStaticData("SERVICE_ORDER_STATE",String.valueOf(info.getOrderState())).getCodeName());
            sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.ServiceOrderInfo,
                    info.getId(), SysOperLogConst.OperType.Update,
                    "修改订单状态为"+info.getOrderStateName());
        }else{
            throw new BusinessException("未找到支付订单信息！");
        }
    }

    @Override
    public void synServiceOrderBalance(Long id, Long oilBalance, Long cashBalance,String accessToken) {
        if (id == null || id <= 0) {
            throw new BusinessException("支付订单号不能为空！");
        }
//        ServiceOrderInfo info = serviceOrderInfoSV.getServiceOrderInfo(id);
        ServiceOrderInfo info = baseMapper.selectById(id);
        if (info != null) {
            info.setOilBalance(oilBalance);
            info.setCashBalance(cashBalance);
            this.saveOrUpdate(info);
        }else{
            throw new BusinessException("未找到支付订单信息！");
        }
    }

    @Override
    public PayDetailsDto zhaoYouPayDetails(String orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        Date endDate = new Date();
        Double time = Double.parseDouble(iSysCfgService.getCfgVal("ZHAOYOU_PAY_RESULT_TIME", 0, String.class).toString());
        Date startDate = DateUtil.addMinis(endDate, -time.intValue());
        if (org.apache.commons.lang.StringUtils.isNotBlank(orderId)) {
            endDate = null;
            startDate = null;
        }

        PayDetailsDto codeMap = new PayDetailsDto();

        List<ServiceOrderInfo> serviceOrderInfos = this.queryServiceOrderDisposeWait(loginInfo.getUserInfoId(), orderId, null, startDate, endDate);
        if (serviceOrderInfos != null && serviceOrderInfos.size() > 0) {

            ServiceOrderInfo serviceOrderInfo = serviceOrderInfos.get(0);
            ServiceProduct product = iServiceProductService.getServiceProduct(serviceOrderInfo.getProductId());
            if (product == null) {
                throw new BusinessException("未找到油站信息！");
            }

            ServiceProductOils serviceProductOils = iServiceProductOilsService.getServiceProductOilsByOilsId(serviceOrderInfo.getOilsId());
            if (serviceProductOils == null) {
                throw new BusinessException("未找到油品信息！");
            }

            codeMap.setOilFee(serviceOrderInfo.getOilFee());
            codeMap.setServiceOrderId(serviceOrderInfo.getId());
            codeMap.setProductName(product.getProductName());
            codeMap.setProductId(product.getId());
            codeMap.setZhaoYouOrderId(serviceOrderInfo.getOrderId());
            codeMap.setCreateDate(serviceOrderInfo.getCreateTime());
            codeMap.setOilPrice(serviceOrderInfo.getOilPrice());

            long originalPrice = 0;
            if (product.getProvinceId() != null && product.getProvinceId() > 0) {
                OilPriceProvince oilPriceProvince = iOilPriceProvinceService.getOilPriceProvince(product.getProvinceId());
                if (oilPriceProvince != null) {
                    originalPrice = oilPriceProvince.getOilPrice();
                }
            }
            codeMap.setOrgOilPrice(originalPrice);

            codeMap.setPlateNumber(serviceOrderInfo.getPlateNumber());
            codeMap.setOilLitre(serviceOrderInfo.getOilLitre());
            Double originalFee = (originalPrice * serviceOrderInfo.getOilLitre()) / 1000.0;
            codeMap.setOriginalFee(originalFee.longValue());
            codeMap.setSaveFee(originalFee.longValue() - serviceOrderInfo.getOilFee());

            List<OilServiceInDto> products = new ArrayList<OilServiceInDto>();
            OilServiceInDto oilServiceIn = new OilServiceInDto();
            oilServiceIn.setProductId(product.getId());
            oilServiceIn.setServiceId(product.getServiceUserId());
            products.add(oilServiceIn);
            List<OilServiceOutDto> oilServiceOuts = iOperationOilService.queryOilRise(serviceOrderInfo.getUserId(), products, -1L, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            OilServiceOutDto oilServiceOut = null;
            if (oilServiceOuts != null && oilServiceOuts.size() > 0) {
                oilServiceOut = oilServiceOuts.get(0);
            }

            Long balance = 0L;
            if (oilServiceOut != null) {
                balance = oilServiceOut.getConsumeOilBalance() == null ? 0 : oilServiceOut.getConsumeOilBalance();
            }
            codeMap.setBalance(balance);

        }

        return codeMap;
    }

    @Override
    public List<ServiceOrderInfo> queryServiceOrderDisposeWait(Long userId, String orderId, String plateNumber, Date startDate, Date endDate) {
        LambdaQueryWrapper<ServiceOrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceOrderInfo::getOrderType, ServiceConsts.SERVICE_ORDER_TYPE.ZHAOYOU);
        queryWrapper.eq(ServiceOrderInfo::getOrderState, ServiceConsts.SERVICE_ORDER_STATE.NO_PAY);
        if (userId != null && userId > 0) {
            queryWrapper.eq(ServiceOrderInfo::getUserId, userId);
        }
        if (StringUtils.isNoneBlank(plateNumber)) {
            queryWrapper.eq(ServiceOrderInfo::getPlateNumber, plateNumber);
        }
        if (StringUtils.isNoneBlank(orderId)) {
            queryWrapper.eq(ServiceOrderInfo::getOrderId, orderId);
        }
        if (startDate != null) {
            queryWrapper.eq(ServiceOrderInfo::getCreateTime, startDate);
        }
        if (endDate != null) {
            queryWrapper.eq(ServiceOrderInfo::getCreateTime, endDate);
        }
        queryWrapper.orderByDesc(ServiceOrderInfo::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<OilVehiclesAndTenantDto> getAddOilVehiclesAndTenant(String accessToken) {
        Long carDriverId = loginUtils.get(accessToken).getId();

        Set<String> vehicles = new HashSet<String>();
        Map<String, Object> plateNumbers = new HashMap<String, Object>();
        List<OilVehiclesAndTenantDto> rtnList = new ArrayList<OilVehiclesAndTenantDto>();

        //查询司机名下的车辆
        //查询司机在途行驶的车辆
        List<TenantVehicleRel> tenantVehicleRels = iTenantVehicleRelService.getTenantVehicleRelByDriverUserIdAndAuthState(carDriverId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        if (null != tenantVehicleRels) {
            for (TenantVehicleRel tenantVehicleRel : tenantVehicleRels) {
                if (tenantVehicleRel.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {

                    if (!vehicles.contains(tenantVehicleRel.getPlateNumber())) {
                        vehicles.add(tenantVehicleRel.getPlateNumber());
                    }

                    int index = DataFormat.getIntKey(plateNumbers, tenantVehicleRel.getPlateNumber());
                    if (index == -2) {
                        continue;
                    } else {

                        OilVehiclesAndTenantDto rtnMap = new OilVehiclesAndTenantDto();
                        if (index >= 0) {
                            rtnMap = rtnList.get(index);
                        }
                        rtnMap.setPlateNumber(tenantVehicleRel.getPlateNumber());

                        String tenantName = "平台";
                        if (tenantVehicleRel.getVehicleClass() != null
                                && tenantVehicleRel.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                            if (tenantVehicleRel.getTenantId() != null && tenantVehicleRel.getTenantId() > 1) {
                                SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(tenantVehicleRel.getTenantId(), true);
                                if (sysTenantDef != null) {
                                    tenantName = sysTenantDef.getName();
                                }
                            }
                            plateNumbers.put(tenantVehicleRel.getPlateNumber(), -2);
                        } else {
                            if (index < 0) {
                                plateNumbers.put(tenantVehicleRel.getPlateNumber(), rtnList.size() == 0 ? 0 : rtnList.size());
                            }
                        }
                        rtnMap.setTenantName(tenantName);

                        if (index < 0) {
                            rtnList.add(rtnMap);
                        }
                    }

                }
            }
        }

        List<QueryDriverOrderDto> list = iOrderReceiptService.queryDriverOrderPlateNumber(carDriverId);
        if (list != null) {
            for (QueryDriverOrderDto map : list) {
                String plateNumber = map.getPlateNumber();
                if (org.apache.commons.lang.StringUtils.isNotBlank(plateNumber) && !vehicles.contains(plateNumber)) {
                    vehicles.add(plateNumber);
                    String tenantName = map.getTenantName();

                    OilVehiclesAndTenantDto rtnMap = new OilVehiclesAndTenantDto();
                    rtnMap.setPlateNumber(plateNumber);
                    rtnMap.setTenantName(org.apache.commons.lang.StringUtils.isBlank(tenantName) ? "平台" : tenantName);

                    rtnList.add(rtnMap);
                }
            }
        }

        return rtnList;
    }

    @Override
    public void evaluateServiceOrderInfo(Integer evaluateService, Integer evaluateQuality, Integer evaluatePrice, Long serviceOrderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();

        ServiceOrderInfo serviceOrderInfo = baseMapper.selectById(serviceOrderId);
        if (serviceOrderInfo == null) {
            throw new BusinessException("未找到加油记录！");
        }

        // 订单支付成功-支付宝中
        if (serviceOrderInfo.getOrderState() == null ||
                (serviceOrderInfo.getOrderState().intValue() != ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY
                        && serviceOrderInfo.getOrderState().intValue() != ServiceConsts.SERVICE_ORDER_STATE.MIDWAY_PAY)) {
            throw new BusinessException("该记录目前不可评价！");
        }

        // 查看订单是否评价
        List<EvaluateInfo> evaluateInfos = evaluateInfoService.queryEvaluateInfo(null, serviceOrderInfo.getId(), ServiceConsts.EVALUATE_BUSI_TYPE.OIL_RECORD_CODE);
        if (evaluateInfos != null && evaluateInfos.size() > 0) {
            throw new BusinessException("已评价，不可重复评价！");
        }
        EvaluateInfo evaluateInfo = new EvaluateInfo();
        evaluateInfo.setCreateTime(LocalDateTime.now());
        evaluateInfo.setBusiId(serviceOrderId);
        evaluateInfo.setEvaluatePrice(evaluatePrice);
        evaluateInfo.setEvaluateQuality(evaluateQuality);
        evaluateInfo.setEvaluateService(evaluateService);
        evaluateInfo.setOpId(userInfoId);
        evaluateInfo.setEvaluateBusiType(ServiceConsts.EVALUATE_BUSI_TYPE.OIL_RECORD_CODE);
        evaluateInfoService.saveOrUpdate(evaluateInfo);
    }

    @Override
    public QueryServiceOrderInfoDetailsDto queryServiceOrderInfoDetails(Long serviceOrderId) {
        // 查询服务商订单
        ServiceOrderInfo serviceOrderInfo = baseMapper.selectById(serviceOrderId);
        if (serviceOrderInfo == null) {
            throw new BusinessException("未找到加油记录！");
        }

        QueryServiceOrderInfoDetailsDto dto = new QueryServiceOrderInfoDetailsDto();
        dto.setServiceOrderId(serviceOrderInfo.getId());
        dto.setZhaoYouOrderId(serviceOrderInfo.getOrderId());

        // 查询服务商产品信息
        ServiceProduct product = serviceProductService.getServiceProduct(serviceOrderInfo.getProductId());
        String productName = "";
        String productAddress = "";
        String productPhone = "";
        if (product != null) {
            productName = product.getProductName();
            productAddress = product.getAddress();
            productPhone = product.getServiceCall();
        }
        dto.setCreateDate(serviceOrderInfo.getCreateTime());
        dto.setProductName(productName);
        dto.setProductAddress(productAddress);
        dto.setProductPhone(productPhone);
        dto.setOilFee(serviceOrderInfo.getOilFee());
        dto.setOilBalance(serviceOrderInfo.getOilBalance());
        dto.setCashBalance(serviceOrderInfo.getCashBalance());
        dto.setPlateNumber(serviceOrderInfo.getPlateNumber());
        dto.setOilLitre(serviceOrderInfo.getOilLitre());
        dto.setOrderState(serviceOrderInfo.getOrderState());
        dto.setOrderStateName(serviceOrderInfo.getOrderStateName());
        dto.setOrderType(serviceOrderInfo.getOrderType());
        int isEvaluate = 2;//不可评价
        Integer evaluateService = 0;
        Integer evaluateQuality = 0;
        Integer evaluatePrice = 0;

        // 服务商订单支付成功
        if (serviceOrderInfo.getOrderState() != null && serviceOrderInfo.getOrderState().intValue() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY) {

            // 查看是否评价
            List<EvaluateInfo> evaluateInfos = evaluateInfoService.queryEvaluateInfo(null, serviceOrderInfo.getId(), ServiceConsts.EVALUATE_BUSI_TYPE.OIL_RECORD_CODE);
            if (evaluateInfos == null || evaluateInfos.size() == 0) {
                isEvaluate = 0;//可评价
            }else{
                EvaluateInfo evaluateInfo = evaluateInfos.get(0);
                evaluateService = evaluateInfo.getEvaluateService() == null ? 0 : evaluateInfo.getEvaluateService();
                evaluateQuality = evaluateInfo.getEvaluateQuality() == null ? 0 : evaluateInfo.getEvaluateQuality();
                evaluatePrice = evaluateInfo.getEvaluatePrice() == null ? 0 : evaluateInfo.getEvaluatePrice();
                isEvaluate = 1;//已评价
            }
        }
        dto.setEvaluateService(evaluateService);
        dto.setEvaluateQuality(evaluateQuality);
        dto.setEvaluatePrice(evaluatePrice);
        dto.setIsEvaluate(isEvaluate);
        return dto;
    }

    @Override
    public Page<ServiceOrderInfo> queryServiceOrderList(Long userId, String plateNumber, Date startDate, Date endDate,Integer pageNum,Integer pageSize) {
        LambdaQueryWrapper<ServiceOrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (userId != null && userId > 0) {
            queryWrapper.eq(ServiceOrderInfo::getUserId, userId);
        }
        if (StringUtils.isNoneBlank(plateNumber)) {
            queryWrapper.eq(ServiceOrderInfo::getPlateNumber, plateNumber);
        }
        if (startDate != null) {
            queryWrapper.ge(ServiceOrderInfo::getCreateTime, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(ServiceOrderInfo::getCreateTime, endDate);
        }
        queryWrapper.orderByDesc(ServiceOrderInfo::getCreateTime);
        return this.page(new Page<ServiceOrderInfo>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public Page<ServiceOrderInfoListDto> queryServiceOrderInfoList(String accessToken,Integer pageNum,Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        Page<ServiceOrderInfo> serviceOrderInfoPage = this.queryServiceOrderList(loginInfo.getUserInfoId(), null, null, null,pageNum,pageSize);

        List<ServiceOrderInfoListDto> outList = new ArrayList<ServiceOrderInfoListDto>();

        for (ServiceOrderInfo serviceOrderInfo : serviceOrderInfoPage.getRecords()) {
            ServiceOrderInfoListDto codeMap = new ServiceOrderInfoListDto();
            codeMap.setServiceOrderId(serviceOrderInfo.getId());
            codeMap.setZhaoYouOrderId(serviceOrderInfo.getOrderId());
            ServiceProduct product = serviceProductService.getServiceProduct(serviceOrderInfo.getProductId());
            String productName = "";
            if (product != null) {
                productName = product.getProductName();
            }
            codeMap.setCreateDate(serviceOrderInfo.getCreateTime());
            codeMap.setProductName(productName);
            codeMap.setOilFee(serviceOrderInfo.getOilFee());
            codeMap.setPlateNumber(serviceOrderInfo.getPlateNumber());
            codeMap.setOilLitre(serviceOrderInfo.getOilLitre());
            codeMap.setOrderState(serviceOrderInfo.getOrderState());
            codeMap.setOrderStateName(getSysStaticData("SERVICE_ORDER_STATE", String.valueOf(serviceOrderInfo.getOrderState())).getCodeName());
            codeMap.setOrderType(serviceOrderInfo.getOrderType());
            int isEvaluate = 2;//不可评价
            if (serviceOrderInfo.getOrderState() != null && serviceOrderInfo.getOrderState() == ServiceConsts.SERVICE_ORDER_STATE.SUCCEED_PAY) {
                List<EvaluateInfo> evaluateInfos = evaluateInfoService.queryEvaluateInfo(null, serviceOrderInfo.getId(), ServiceConsts.EVALUATE_BUSI_TYPE.OIL_RECORD_CODE);
                if (evaluateInfos == null || evaluateInfos.size() == 0) {
                    isEvaluate = 0;//可评价
                } else {
                    isEvaluate = 1;//已评价
                }
            }
            codeMap.setIsEvaluate(isEvaluate);
            outList.add(codeMap);
        }

        Page<ServiceOrderInfoListDto> serviceOrderInfoListDtoPage = new Page<>();
        serviceOrderInfoListDtoPage.setRecords(outList);
        serviceOrderInfoListDtoPage.setCurrent(serviceOrderInfoPage.getCurrent());
        serviceOrderInfoListDtoPage.setSize(serviceOrderInfoPage.getSize());
        serviceOrderInfoListDtoPage.setTotal(serviceOrderInfoPage.getTotal());
        serviceOrderInfoListDtoPage.setPages(serviceOrderInfoPage.getPages());

        return serviceOrderInfoListDtoPage;

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

}
