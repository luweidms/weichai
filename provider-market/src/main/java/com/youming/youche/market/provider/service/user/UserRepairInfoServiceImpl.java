package com.youming.youche.market.provider.service.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.*;
import com.youming.youche.finance.api.payable.IPayByCashService;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.IServiceSerialService;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.api.repair.IRepairItemsService;
import com.youming.youche.market.api.tenant.ITenantVehicleRelService;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.commons.RepairConsts;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.ServiceSerial;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.repair.RepairItems;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.RepairInfoDto;
import com.youming.youche.market.dto.facilitator.RepairItemsWXOutDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoOutDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoVx;
import com.youming.youche.market.dto.user.RepairItemsDto;
import com.youming.youche.market.dto.user.UserRepairInfoDetailDto;
import com.youming.youche.market.dto.user.UserRepairInfoDto;
import com.youming.youche.market.dto.user.VehicleAfterServingDto;
import com.youming.youche.market.provider.mapper.repair.RepairItemsMapper;
import com.youming.youche.market.provider.mapper.user.UserRepairInfoMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.provider.utis.SysStaticDataRedisUtils;
import com.youming.youche.market.vo.facilitator.RepairInfoDataVo;
import com.youming.youche.market.vo.facilitator.RepairInfoVo;
import com.youming.youche.market.vo.facilitator.RepairItemsVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceVo;
import com.youming.youche.market.vo.facilitator.UserRepairInfoOutVo;
import com.youming.youche.market.vo.user.UserRepairInfoVo;
import com.youming.youche.order.api.IPlatformServiceChargeService;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.PlatformServiceCharge;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.SysStaticData.REPAIR_STATE;

/**
 * ????????????
 *
 * @author hzx
 * @date 2022/3/12 11:14
 */
@DubboService(version = "1.0.0")
@Service
public class UserRepairInfoServiceImpl extends BaseServiceImpl<UserRepairInfoMapper, UserRepairInfo> implements IUserRepairInfoService {

    @Resource
    UserRepairInfoMapper userRepairInfoMapper;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;

//    @DubboReference(version = "1.0.0")
//    IRepairItemsService iRepairItemsService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;
    @Lazy
    @DubboReference(version = "1.0.0")
    private IVehicleDataInfoService vehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    private IAuditService auditService;
    @DubboReference(version = "1.0.0")
    private IConsumeOilFlowService consumeOilFlowService;

    @Autowired
    private IServiceProductService serviceProductService;
    @Autowired
    private IServiceSerialService serviceSerialService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Autowired
    private IServiceInfoService serviceInfoService;

    @Autowired
    private ITenantServiceRelService tenantServiceRelService;

    @Autowired
    private ITenantProductRelService tenantProductRelService;
    @Autowired
    private IRepairItemsService repairItemsService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @Resource
    ITenantProductRelService iTenantProductRelService;

    @Resource
    IServiceProductService iServiceProductService;
    @DubboReference(version = "1.0.0")
    IPayByCashService iPayByCashService;
    @DubboReference(version = "1.0.0")
    IUserRepairMarginService iUserRepairMarginService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;
    @Resource
    RepairItemsMapper repairItemsMapper;

    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService iAccountDetailsService;

    @DubboReference(version = "1.0.0")
    IPlatformServiceChargeService iPlatformServiceChargeService;

    @DubboReference(version = "1.0.0")
    IOrderOilSourceService iOrderOilSourceService;

    @DubboReference(version = "1.0.0")
    private IOrderLimitService orderLimitService;

    @Resource
    private ReadisUtil readisUtil;

    @Override
    public Page<UserRepairInfoDto> queryUserRepairAuth(Page<UserRepairInfoVo> page, UserRepairInfoVo userRepairInfoVo, String accessToken) {

        if (StringUtils.isNotEmpty(userRepairInfoVo.getRepairDateBegin())) {
            userRepairInfoVo.setRepairDateBegin(userRepairInfoVo.getRepairDateBegin() + " 00:00:00");
        }

        if (StringUtils.isNotEmpty(userRepairInfoVo.getRepairDateEnd())) {
            userRepairInfoVo.setRepairDateEnd(userRepairInfoVo.getRepairDateEnd() + " 23:59:59");
        }

        LoginInfo loginInfo = loginUtils.get(accessToken);
        userRepairInfoVo.setTenantId(loginInfo.getTenantId());

        List<Long> busiIds = null;
        if (userRepairInfoVo.getTodo()) {
            busiIds = iAuditNodeInstService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.REPAIR_INFO, loginInfo.getUserInfoId(), loginInfo.getTenantId(), 500);
        }
        String busiIdStr = null;
        if (busiIds != null && busiIds.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Long busiId : busiIds) {
                sb.append("'").append(busiId).append("',");
            }
            busiIdStr = sb.substring(0, sb.length() - 1);
        }

        Page<UserRepairInfoDto> userRepairInfoDtoPage = userRepairInfoMapper.queryUserRepairAuth(page, userRepairInfoVo, busiIdStr);
        List<UserRepairInfoDto> records = userRepairInfoDtoPage.getRecords();

        List<Long> busiIdList = new ArrayList<>();
        if (records != null && records.size() > 0) {
            for (UserRepairInfoDto record : records) {

                List<SysStaticData> sysStaticDatas = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(EnumConsts.SysStaticData.APP_REPAIR_STATE));
                if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
                    for (SysStaticData sysStaticData : sysStaticDatas) {
                        if (sysStaticData.getCodeValue().equals(record.getRepairState())) {
                            record.setRepairStateName(sysStaticData.getCodeName());
                        }
                    }
                }
                Long busiId = record.getRepairId();
                busiIdList.add(busiId);
            }

            Map<Long, Boolean> hasPermissionMap = null;
            if (!busiIdList.isEmpty()) {
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.REPAIR_INFO, busiIdList, accessToken);
            }

            for (UserRepairInfoDto record : records) {
                Long busiId = record.getRepairId();
                if (hasPermissionMap != null && hasPermissionMap.containsKey(busiId) && hasPermissionMap.get(busiId)) {
                    record.setHasVer(0);
                } else {
                    record.setHasVer(1);
                }
            }

        }

        return userRepairInfoDtoPage;
    }

    @Override
    public UserRepairInfoDetailDto getUserRepairDetail(long repairId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UserRepairInfoDetailDto out = new UserRepairInfoDetailDto();
        UserRepairInfo userRepairInfo = userRepairInfoMapper.selectById(repairId);

        Long vehicleCode = userRepairInfo.getVehicleCode();
        if (vehicleCode != null) {
            VehicleDataInfo vehicleData = vehicleDataInfoService.getVehicleData(vehicleCode, loginInfo.getTenantId());
            if (null != vehicleData.getVehicleStatus()) {
                userRepairInfo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", vehicleData.getVehicleStatus()));
            }
            if (StringUtils.isNotBlank(vehicleData.getVehicleLength())) {
                userRepairInfo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", Integer.valueOf(vehicleData.getVehicleLength())));
            }
        }
        if (null == userRepairInfo.getAccountPeriod()) {
            userRepairInfo.setAccountPeriod(0);
        }
        if (userRepairInfo.getRepairState() != null && userRepairInfo.getRepairState() >= 0) {
            userRepairInfo.setRepairStateStr(getSysStaticData(REPAIR_STATE, String.valueOf(userRepairInfo.getRepairState())).getCodeName());
        }
        Long zyCount = iTenantVehicleRelService.getZYCount(userRepairInfo.getVehicleCode(), userRepairInfo.getTenantId(), SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1);
        if (zyCount != null && zyCount > 0) {
            out.setZyCount(1);
        } else {
            out.setZyCount(0);
        }
        out.setRepairInfo(userRepairInfo);
        List<RepairItemsDto> repairItems = repairItemsService.getRepairItems(repairId);
        out.setRepairItems(repairItems);
        List<Map> maps = iAuditNodeInstService.getAuditNodeInstNew(AuditConsts.AUDIT_CODE.REPAIR_INFO, userRepairInfo.getId(), userRepairInfo.getTenantId(), false, AuditConsts.RESULT.SUCCESS, AuditConsts.RESULT.FAIL);
        out.setAuthList(maps);
        return out;
    }

    @Override
    public UserRepairInfo checkIsCash(String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Integer userType = user.getUserType();
        Long userId = user.getUserInfoId();
        List<Long> productId = null;
        if (SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER == userType) {
            List<ServiceProduct> serviceProductByChild = serviceProductService.getServiceProductByChild(userId);
            if (serviceProductByChild != null && serviceProductByChild.size() > 0) {
                productId = serviceProductByChild.stream().map(ServiceProduct::getId).collect(Collectors.toList());
            }
        }
        LambdaQueryWrapper<UserRepairInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(UserRepairInfo::getRepairState, 8);
        if (productId == null) {
            lambda.eq(UserRepairInfo::getServiceUserId, userId);
        } else {
            lambda.in(UserRepairInfo::getProductId, productId);
        }
        lambda.orderByDesc(UserRepairInfo::getId).last("limit 0,1");
        UserRepairInfo one = this.getOne(lambda);
        if (one == null) {
            one = new UserRepairInfo();
        }
        Long repairId = one.getId();
        if (repairId < 0) {
            one.setInfo("N");
        } else {
            one.setInfo("Y");
        }
        return one;
    }

    @Override
    public RepairInfoDataVo queryRepairInfo(RepairInfoDto inParam, String accessToken,
                                            Integer pageSize, Integer pageNum) {
        LoginInfo user = loginUtils.get(accessToken);
        Page<RepairInfoVo> repairInfoVoPage = this.queryRepairInfo(user, inParam, user.getUserInfoId(), false, pageSize, pageNum);
        Long productId = inParam.getProductId();
        String productName = "";
        if (productId != null && productId >= 0) {
            ServiceProduct serviceProduct = serviceProductService.getById(productId);
            productName = serviceProduct.getProductName();
        }
        LocalDateTime one = LocalDateTime.now();
        List<RepairInfoVo> list = repairInfoVoPage.getRecords();
        if (list != null && list.size() > 0) {
            for (RepairInfoVo repairInfoVo : list) {
                repairInfoVo.setServiceCharge(calculationServiceFee(repairInfoVo.getTotalFee(),
                        repairInfoVo.getServiceCharge() == null ? "" : repairInfoVo.getServiceCharge().toString()));
//                if (repairInfoVo.getDeliveryDate() != null) {
//                    if (one.isAfter(repairInfoVo.getDeliveryDate())) {
//                        LambdaQueryWrapper<UserRepairInfo> lambda = Wrappers.lambdaQuery();
//                        lambda.eq(UserRepairInfo::getRepairCode, repairInfoVo.getRepairCode())
//                                .eq(UserRepairInfo::getId, repairInfoVo.getRepairId());
//                        UserRepairInfo repairInfo = this.getOne(lambda);
//                        if (repairInfo != null && repairInfo.getRepairState() != null && repairInfo.getRepairState() == 6) {
//                            repairInfo.setRepairState(7);
//                            repairInfo.setGetResult("?????????");
//                            repairInfoVo.setRepairState(7);
//                            this.update(repairInfo);
//                        }
//                    }
//                }
                if (repairInfoVo.getRepairState() != null) {
                    repairInfoVo.setRepairStateName(readisUtil.getSysStaticData(REPAIR_STATE, repairInfoVo.getRepairState() + "").getCodeName());
                }
            }
        }
        repairInfoVoPage.setRecords(list);
        RepairInfoDataVo repairInfoDataVo = new RepairInfoDataVo();
        List<RepairInfoVo> list1 = this.queryRepairInfo(user, inParam, user.getUserInfoId(), true, pageSize, pageNum).getRecords();
        repairInfoDataVo.setItems(repairInfoVoPage);
        repairInfoDataVo.setUnexpired(0L);
        repairInfoDataVo.setBeExpired(0L);
        repairInfoDataVo.setServiceCharge(0L);
        repairInfoDataVo.setProductName(productName);
        if (list1 != null && list1.size() > 0) {
            for (RepairInfoVo map : list1) {
                repairInfoDataVo.setUnexpired(map.getUnexpired() == null ? 0 : map.getUnexpired());
                repairInfoDataVo.setBeExpired(map.getBeExpired() == null ? 0 : map.getBeExpired());
                repairInfoDataVo.setServiceCharge(map.getServiceCharge() == null ? 0 : CommonUtil.getLongByString(String.valueOf(map.getServiceCharge())));
            }
        }
        return repairInfoDataVo;
    }

    /**
     * ???????????????
     *
     * @param totalFee
     * @param serviceCharge
     * @return
     */
    private Long calculationServiceFee(long totalFee, String serviceCharge) {
        Double serviceChargeDouble = 0D;
        if (StringUtils.isNotBlank(serviceCharge)) {
            serviceChargeDouble = Double.valueOf(serviceCharge) / 100;
        }
        double totalFeeDouble = CommonUtil.getDoubleFormatLongMoney(totalFee, 2);
        double feeDouble = totalFeeDouble * serviceChargeDouble;
        return CommonUtil.getLongByString(String.valueOf(feeDouble));
    }

    @Override
    public Page<RepairInfoVo> queryRepairInfo(LoginInfo user, RepairInfoDto inParam,
                                              Long serviceUserId, Boolean isSum,
                                              Integer pageSize, Integer pageNum) {
        Page<RepairInfoVo> pageInfo = new Page<>(pageNum, pageSize);
        Integer userType = user.getUserType();
        Long userId = user.getUserInfoId();
        user.setUserType(4);
        List<Long> productId = null;
//        if (SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER == userType) {
        List<ServiceProduct> serviceProductByChild = serviceProductService.getServiceProductByChild(userId);
        if (serviceProductByChild != null && serviceProductByChild.size() > 0) {
            productId = serviceProductByChild.stream().map(ServiceProduct::getId).collect(Collectors.toList());
        }
//        }
        return userRepairInfoMapper.queryRepairInfo(pageInfo, inParam, serviceUserId, isSum, productId, user);
    }

    @Override
    public Map<String, Object> doSaveUserRepairInfo(UserRepairInfoVx inParam, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Long productId = inParam.getProductId();
        if (productId <= 0) {
            throw new BusinessException("?????????ID??????!");
        }
        Long driverUserId = inParam.getDriverUserId();
        if (driverUserId <= 0) {
            throw new BusinessException("??????ID??????!");
        }
        String repairDate = inParam.getRepairDate();
        if (StringUtils.isBlank(repairDate)) {
            throw new BusinessException("?????????????????????");
        }
        String deliveryDate = inParam.getDeliveryDate();
        if (StringUtils.isBlank(deliveryDate)) {
            throw new BusinessException("?????????????????????");
        }
        String userBill = inParam.getUserBill();
        if (StringUtils.isBlank(userBill)) {
            throw new BusinessException("?????????????????????");
        }
        String userName = inParam.getUserName();
        if (StringUtils.isBlank(userName)) {
            throw new BusinessException("?????????????????????");
        }
        String plateNumber = inParam.getPlateNumber();
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("??????????????????");
        }
        String totalFee = inParam.getTotalFee();
        if (StringUtils.isBlank(totalFee)) {
            throw new BusinessException("??????????????????");
        }


        int isBill = inParam.getIsBill();
        if (isBill < 0) {
            throw new BusinessException("???????????????????????????");
        }
        ServiceProduct serviceProduct = serviceProductService.getById(productId);
        if (serviceProduct == null) {
            throw new BusinessException("??????????????????????????????");
        }
        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceProduct.getServiceUserId());
        if (serviceInfo == null) {
            throw new BusinessException("???????????????????????????");
        }
        if (serviceInfo.getState() != 1) {
            throw new BusinessException("????????????????????????");
        }

        //?????????????????????????????????????????????
        /*IAccountBankRelSV accountBankRelSV = (IAccountBankRelSV) SysContexts.getBean("accountBankRelSV");
        AccountBankRel accountBankRel=accountBankRelSV.getAccountBankRel(serviceInfo.getServiceUserId(),EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0 );
        if(accountBankRel==null) {
			throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????");
		}
        accountBankRel=accountBankRelSV.getAccountBankRel(serviceInfo.getServiceUserId(),EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1 );
        if(accountBankRel==null) {
			throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????");
		}*/

        Long tenantId = inParam.getTenantId();

        String vehicleClass = inParam.getVehicleClass();
        //??????????????????????????????
        VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getVehicleDataInfo(plateNumber);
        if (vehicleDataInfo == null) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (vehicleDataInfo.getAuthState() != SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (vehicleDataInfo.getTenantId() != null && vehicleDataInfo.getTenantId().longValue() != tenantId) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (tenantId != null && tenantId > 0 && (vehicleDataInfo.getTenantId() == null || vehicleDataInfo.getTenantId().longValue() < 0)) {
            throw new BusinessException("????????????????????????????????????");
        }
        boolean isBillBoolean = isBill == ServiceConsts.IS_BILL_ABILITY.YSE ? true : false;
        if (isBillBoolean && serviceProduct.getIsBillAbility() != null && serviceProduct.getIsBillAbility() == ServiceConsts.IS_BILL_ABILITY.NO) {
            throw new BusinessException("????????????????????????????????????????????????");
        }

        TenantProductRel tenantProductRel = null;
//        Long tenantId = vehicleDataInfo.getTenantId();
        if (tenantId != null && tenantId > 0) {

            TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(tenantId, serviceInfo.getServiceUserId());
            if (isBillBoolean && tenantServiceRel != null && tenantServiceRel.getIsBill() == ServiceConsts.IS_BILL_ABILITY.NO) {
                throw new BusinessException("????????????????????????????????????????????????");
            }
            tenantProductRel = tenantProductRelService.getProductRelIsShare(tenantId, serviceProduct.getId());
        } else {
            Integer isShare = serviceProduct.getIsShare();
            if (isShare == null || isShare == ServiceConsts.IS_SHARE.NO) {
                throw new BusinessException("???????????????????????????????????????????????????????????????????????????");
            }
            tenantProductRel = tenantProductRelService.getTenantProductRel(user.getTenantId(), serviceProduct.getId());
        }
        if (tenantProductRel == null) {
            throw new BusinessException("????????????????????????????????????????????????????????????????????????");
        }
        if (tenantProductRel != null && (tenantProductRel.getState() == null || tenantProductRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
            if (tenantProductRel.getTenantId().longValue() == SysStaticDataEnum.PT_TENANT_ID) {
                throw new BusinessException("???????????????????????????????????????");
            } else {
                throw new BusinessException("????????????????????????????????????????????????");
            }
        }
        if (tenantProductRel.getAuthState() == null || tenantProductRel.getAuthState() != SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {
            throw new BusinessException("?????????????????????????????????????????????");
        }


        Long repairIdIn = inParam.getRepairId();
        UserRepairInfo uri = null;
        String flag = "Y";
        if (repairIdIn == null || repairIdIn < 0) {
            uri = new UserRepairInfo();
        } else {
            uri = this.getById(repairIdIn);
            if ((uri.getIsSure() != null && uri.getIsSure() == 1) && uri.getRepairState() != 5) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
        }
        if (uri == null) {
            throw new BusinessException("??????????????????????????????");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        uri.setVehicleCode(vehicleDataInfo.getId());
        if (StringUtils.isNotBlank(vehicleClass)) {
            uri.setVehicleClass(Integer.valueOf(vehicleClass));
        }

        uri.setRepairDate(LocalDateTimeUtil.convertStringToDate(repairDate));
        uri.setDeliveryDate(LocalDateTimeUtil.convertStringToDate(deliveryDate));


        uri.setUserBill(userBill);
        uri.setUserName(userName);
        uri.setPlateNumber(plateNumber);
        uri.setTotalFee(CommonUtil.getLongByString(totalFee));
        uri.setProductId(productId);
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        uri.setIsSure(1);
        uri.setTenantId(vehicleDataInfo.getTenantId());
        uri.setPayWay(1);
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        if (StringUtils.isNotBlank(vehicleClass)) {
            uri.setVehicleClass(Integer.parseInt(vehicleClass));
        }

        uri.setServiceUserId(serviceProduct.getServiceUserId());
        uri.setServiceName(serviceInfo.getServiceName());
        uri.setProductName(serviceProduct.getProductName());
        uri.setProvinceId(serviceProduct.getProvinceId());
        uri.setCityId(serviceProduct.getCityId());
        uri.setCountyId(serviceProduct.getCountyId());
        uri.setAddress(serviceProduct.getAddress());
        uri.setNand(serviceProduct.getNand());
        uri.setEand(serviceProduct.getEand());
        uri.setUserId(driverUserId);

        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        uri.setRepairState(3);
        uri.setAppRepairState(3);
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        uri.setCreateTime(LocalDateTime.now());
        uri.setOilRateInvoice("100");
        uri.setIsBill(isBill);
        uri.setTenantId(tenantId);

        TenantServiceRel tenantServiceRel = null;

        if (vehicleDataInfo.getTenantId() != null && vehicleDataInfo.getTenantId() > 0) {
            tenantServiceRel = tenantServiceRelService.getTenantServiceRel(vehicleDataInfo.getTenantId(), serviceProduct.getServiceUserId());
        }

        uri.setAcctType(2);
        //??????
        if (tenantServiceRel != null) {
            if (uri.getAccountPeriod() != null) {
                uri.setAccountPeriod(tenantServiceRel.getPaymentDays());
                uri.setGetDate(LocalDateTime.now().plusDays(uri.getAccountPeriod()));
            } else {
                uri.setAccountPeriod(0);
                uri.setGetDate(LocalDateTime.now());
            }
        } else {
            uri.setAccountPeriod(0);
            uri.setGetDate(LocalDateTime.now());
        }
        uri.setGetResult("?????????");
        if (isBill == 1) {
            uri.setServiceCharge(tenantProductRel.getServiceChargeBill());
        } else {
            uri.setServiceCharge(tenantProductRel.getServiceCharge());
        }
        uri.setState(inParam.getTenanceType());
        this.saveOrUpdate(uri);

        long repairId = uri.getId();
        repairId = repairId + 100000000;
        String repairIdStr = repairId + "";
        String repairCode = "S" + repairIdStr.substring(1, 9);
        List<RepairItemsVo> rateItemList = inParam.getRateItemList();
        Long totalFeesList = 0L;
        if (repairIdIn == null || repairIdIn < 0) {//??????

        } else {//??????
            flag = "YM";
            List<RepairItems> list = repairItemsService.getRepairItemsList(vehicleDataInfo.getTenantId(), repairIdIn);
            for (RepairItems repairItem : list) {
                repairItemsService.delRepairItems(repairItem);
            }
        }
        for (RepairItemsVo rateItem : rateItemList) {
            RepairItems ri = new RepairItems();

            String repairRootId = rateItem.getRepairRootId().toString();
            if (repairRootId == null || repairRootId.equals("")) {
                throw new BusinessException("???????????????");
            }
            Long repairRootTwoId = rateItem.getRepairRootTwoId();
            if (Integer.parseInt(repairRootId) != 4 && (repairRootTwoId == null)) {
                throw new BusinessException("??????????????????");
            }
            String univalence = rateItem.getUnivalence();
            if (Integer.parseInt(repairRootId) == 4 && (StringUtils.isBlank(univalence))) {
                throw new BusinessException("???????????????");
            }
            String workingHours = rateItem.getWorkingHours();
            if (Integer.parseInt(repairRootId) == 4 && (StringUtils.isBlank(workingHours))) {
                throw new BusinessException("???????????????");
            }
            String productName = rateItem.getProductName();
            if (StringUtils.isBlank(productName)) {
                throw new BusinessException("???????????????");
            }
            String amount = rateItem.getAmount();
            if (StringUtils.isBlank(amount)) {
                throw new BusinessException("????????????????????????");
            }
            String repairItemName = rateItem.getRepairItemName();
            if (StringUtils.isBlank(repairItemName)) {
                throw new BusinessException("??????????????????");
            }
            totalFeesList += CommonUtil.getLongByString(amount);

            String des = rateItem.getDes();
            if (StringUtils.isBlank(des)) {
                throw new BusinessException("????????????????????????");
            }
            String repairPicIds = rateItem.getRepairPicIds();
            String repairPicUrl = rateItem.getRepairPicUrl();

            ri.setRepairRootId(Long.parseLong(repairRootId));
            if (Long.parseLong(repairRootId) != 4) {
                ri.setRepairRootTwoId(repairRootTwoId);
            }
            if (Long.parseLong(repairRootId) == 4) {
                ri.setUnivalence(CommonUtil.getLongByString(univalence));
                ri.setWorkingHours(Float.parseFloat(workingHours));
            }
            ri.setProductName(productName);
            ri.setAmount(CommonUtil.getLongByString(amount));
            ri.setDes(des);
            ri.setRepairPicUrl(repairPicUrl);
            ri.setRepairPicIds(repairPicIds);
            ri.setRepairId(uri.getId());
            ri.setRepairCode(repairCode);
            ri.setRepairItemName(repairItemName);
            ri.setTenantId(vehicleDataInfo.getTenantId());
            repairItemsService.saveOrUpdate(ri);
        }

        if (uri.getTotalFee().longValue() != totalFeesList.longValue()) {
            throw new BusinessException("??????????????????????????????");
        }
        uri.setRepairCode(repairCode);
        uri.setState(inParam.getTenanceType());
        this.saveOrUpdate(uri);
        if (repairIdIn == null || repairIdIn < 0) {
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.RepairInfo, uri.getId(), SysOperLogConst.OperType.Remove, "????????????????????????");
        } else {
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.RepairInfo, uri.getId(), SysOperLogConst.OperType.Remove, "????????????????????????");
        }

        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????
        Map map = new HashMap();
        map.put("svName", "userRepairInfoTF");
        boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.REPAIR_INFO, uri.getId(), SysOperLogConst.BusiCode.RepairInfo, map, accessToken, uri.getTenantId());
        if (!bool) {
            throw new BusinessException("???????????????????????????");
        }
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        Map<String, Object> out = new HashMap<>();
        out.put("info", flag);
        out.put("productId", serviceProduct.getId());
        out.put("businessUserId", serviceProduct.getServiceUserId());
        return out;
    }

    @Override
    public List<UserRepairInfoVo> getUserRepairByProductIds(List<Long> productIds) {
        return baseMapper.getUserRepairByProductIds(productIds);
    }

    /**
     * 40023
     * ??????????????????
     * niejiewei
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public UserRepairInfoOutDto confirmPayRepairInfo(UserRepairInfoOutVo vo, String accessToken) {
        UserRepairInfoOutDto dto = new UserRepairInfoOutDto();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String capitalChannel = vo.getCapitalChannel();
        Long consumeFee = vo.getConsumeFee();
        String payPasswd = vo.getPayPasswd();
        String payCode = vo.getPayCode();  //???????????????
        //???????????????
        Integer priceStar = vo.getPriceStar();
        Integer qualityStar = vo.getQualityStar();
        Integer serviceStar = vo.getServiceStar();
        Integer isSure = vo.getIsSure();
        Integer payWay = vo.getPayWay();
        String driverDes = vo.getDriverDes();
        Long repairId = vo.getRepairId();
        String repairCode = vo.getRepairCode();
        Long repairFund = vo.getRepairFund();
        Long payTenantId = vo.getPayTenantId();
        /*
         * ???payWay??????????????? 2(??????-?????? )??????selectType:0???????????????????????????????????????????????????1??????????????????????????????,
         * ???payWay??????????????? 4(???????????? )??????selectType:0????????????????????????????????????1????????????????????????,
         * ??????payWay????????????????????????
         */
        String selectType = vo.getSelectType();

        if (isSure != 1 && isSure != 0) {
            throw new BusinessException("?????????????????????");
        }
        if (driverDes == null || StringUtils.isEmpty(driverDes)) {
            throw new BusinessException("??????????????????");
        }
        if (repairId < 0) {
            throw new BusinessException("?????????????????????");
        }
        // ??????????????????
        Long userId = loginInfo.getUserInfoId();
        UserRepairInfo uri = this.getById(repairId);
        if (uri == null) {
            throw new BusinessException("????????????????????????");
        }
        if (uri.getAppRepairState() != null && (uri.getAppRepairState() == RepairConsts.APP_REPAIR_STATE.PAID || uri.getAppRepairState() == RepairConsts.APP_REPAIR_STATE.CASH_PAYMENT)) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????");
        }
        String Code = uri.getRepairCode();
        if (!Code.equals(repairCode)) {
            throw new BusinessException("????????????????????????");
        }

        if (payWay == 1 && payTenantId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        UserDataInfo carDriverUser = iUserDataInfoService.getUserDataInfo(userId);
        Map<String, Object> mapOut = new HashMap<String, Object>();
        if (isSure == 0) {
            //????????????????????????????????????????????????
            uri.setRepairState(RepairConsts.REPAIR_STATE.DRIVER_FAIL);
            uri.setAppRepairState(RepairConsts.APP_REPAIR_STATE.DRIVER_FAIL);
        } else if (isSure == 1) {
            //PayInterface sv = (PayInterface) SysContexts.getBean("payInterface");

            Map<String, Long> inMap = new HashMap<String, Long>();
            //????????????  ???4???????????????
            if (!payWays.contains(payWay)) {
                throw new BusinessException("????????????????????????");
            }
            if (!priceStars.contains(priceStar)) {
                throw new BusinessException("????????????????????????");
            }
            if (!qualityStars.contains(qualityStar)) {
                throw new BusinessException("????????????????????????");
            }
            if (!serviceStars.contains(serviceStar)) {
                throw new BusinessException("????????????????????????");
            }
            if (payWay == 1) {
                TenantProductRel tenantProductRel = iTenantProductRelService.getTenantProductRel(uri.getTenantId(), uri.getProductId());
//                if(tenantProductRel == null){
//                    throw new BusinessException("????????????????????????????????????????????????????????????");
//                }else {
                if (tenantProductRel != null) {
                    if (tenantProductRel.getAuthState() != SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {
                        throw new BusinessException("?????????????????????????????????????????????????????????");
                    }
                    if (tenantProductRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                        throw new BusinessException("????????????????????????????????????????????????");
                    }
                }
                //?????????
                uri.setRepairState(RepairConsts.REPAIR_STATE.WAIT_AUDIT);
                uri.setAppRepairState(RepairConsts.APP_REPAIR_STATE.WAIT_AUDIT);
                uri.setTenantId(payTenantId);
                //??????????????????
                Map map = new HashMap();
                map.put("svName", "userRepairInfoTF");
                boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.REPAIR_INFO,
                        repairId, SysOperLogConst.BusiCode.RepairInfo, map, accessToken, uri.getTenantId());
                if (!bool) {
                    throw new BusinessException("???????????????????????????");
                }
            } else if (payWay == 2 || payWay == 4) {
//                //??????-??????
                if (uri.getProductId() <= 0) {
                    throw new BusinessException("?????????ID????????????");
                }
                if (consumeFee <= 0) {
                    throw new BusinessException("?????????????????????");
                }
                if (StringUtils.isEmpty(payPasswd)) {
                    throw new BusinessException("???????????????????????????");
                }
                if (selectType.isEmpty()) {
                    throw new BusinessException("????????????????????????selectType");
                }
                if (uri.getGetDate() == null) {
                    throw new BusinessException("???????????????????????????????????????");
                }

                ServiceInfo serviceInfo = serviceInfoService.getServiceInfoById(uri.getServiceUserId());
                if (serviceInfo == null) {
                    throw new BusinessException("??????????????????????????????");
                }
                if (serviceInfo.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                    throw new BusinessException("???????????????????????????");
                }
                if (serviceInfo.getIsAuth() != SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {
                    throw new BusinessException("????????????????????????????????????");
                }
                ServiceProduct serviceProduct = iServiceProductService.getServiceProduct(uri.getProductId());
                if (serviceProduct == null) {
                    throw new BusinessException("????????????????????????");
                }
                if (serviceProduct.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                    throw new BusinessException("???????????????????????????");
                }
                if (serviceProduct.getAuthState() != SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {
                    throw new BusinessException("??????????????????????????????");
                }

                if (carDriverUser == null || carDriverUser.getUserType() != SysStaticDataEnum.USER_TYPE.DRIVER_USER) {
                    throw new BusinessException("??????????????????");
                }

                if (uri.getGetDate() == null) {
                    throw new BusinessException("???????????????????????????????????????");
                }
                if (payWay == 4) {
                    if (Integer.parseInt(selectType) == 0) {
                        if (repairFund < 0) {
                            throw new BusinessException("?????????????????????");
                        }
                    }
                }


                boolean isInit = iUserDataInfoService.doInit(userId);
                if (isInit) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????!");
                }
                /*Integer errPwdNum = checkPasswordErrSV.getPwdNum(userId, SysStaticDataEnum.PASSWORD_TYPE.PAY_ERR_TYPE_PASS);
                //???????????????????????????????????????????????????2???
                Integer maxSysCode = (Integer) getCfgVal("MAX_NEED_PAY_CODE", SysStaticDataEnum.SYSTEM_CFG.CFG_0, Integer.class);
                if (maxSysCode == null) {
                    maxSysCode = 2;
                }
                if (errPwdNum != null) {
                    if (errPwdNum >= maxSysCode) {
                        if (StringUtils.isEmpty(payCode)) {
                            throw new BusinessException("???????????????????????????????????????");
                        }
                        payCode = EncryPwd.pwdDecryption(payCode);
                        boolean istrue = CommonUtil.checkCode(SysContexts.getCurrentOperator().getTelphone(), payCode, EnumConsts.RemoteCache.VALIDKEY_PAY);
                        if (!istrue) {
                            throw new BusinessException("?????????????????????!");
                        }
                    }
                }*/
                iPayByCashService.doWithdrawal(accessToken, payPasswd, null);
                // TODO ?????????
//                AccountPayRepairOutDto accountPayRepairOut = iUserRepairMarginService.payForRepairFee(uri, String.valueOf(payWay), selectType,accessToken);
//                dto.setBalance(accountPayRepairOut.getCash());
//                dto.setMarginBalance(accountPayRepairOut.getMarginBalance());
//                dto.setAdvanceFee(accountPayRepairOut.getAdvanceFee());
//                dto.setRepairFund(accountPayRepairOut.getRepairFund());
//                dto.setTotalAmount(accountPayRepairOut.getTotalAmount());
//                dto.setCash(accountPayRepairOut.getCash());
                dto.setSelectType(selectType);
                dto.setPayWay(payWay);
                //??????????????????????????????
                if (payWay == 2) {
                    uri.setRepairState(RepairConsts.REPAIR_STATE.BE_EXPIRED);
                    uri.setAppRepairState(RepairConsts.APP_REPAIR_STATE.PAID);
                } else if (payWay == 4) {
                    uri.setIsConfirmCash(0);
                    uri.setRepairState(RepairConsts.REPAIR_STATE.RECEIPT_TO_BE_CONFIRMED);
                    uri.setAppRepairState(RepairConsts.APP_REPAIR_STATE.CASH_PAYMENT);
                }
                uri.setIsFund(selectType.equals("0") ? 1 : 2);
            } else if (payWay == 3) {
                //????????????
                throw new BusinessException("??????????????????????????????");
            }
            uri.setPriceStar(priceStar);
            uri.setQualityStar(qualityStar);
            uri.setServiceStar(serviceStar);
            uri.setPayWay(payWay);
        }

        uri.setIsSure(isSure);
        uri.setDriverDes(driverDes);
        //??????????????????
        if (isSure == 0) {
            //????????????
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.RepairInfo, uri.getId(), SysOperLogConst.OperType.Audit, "????????????");
        } else {
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.RepairInfo, uri.getId(), SysOperLogConst.OperType.Audit, "????????????");
        }
        this.saveOrUpdate(uri);
        dto.setRepairId(repairId);
        dto.setInfo("y");
        return dto;
    }

    /**
     * ????????????-??????
     *
     * @param vo
     * @return
     * @throws Exception
     */

    @Override
    public UserRepairInfoOutDto getRepairDetail(UserRepairInfoOutVo vo, String accessToken) {
        UserRepairInfoOutDto userRepairInfoOut = new UserRepairInfoOutDto();
        // ??????????????????
        UserRepairInfo userRepairInfo = this.getById(vo.getRepairId());
        BeanUtil.copyProperties(userRepairInfo, userRepairInfoOut);
        if (userRepairInfo.getTenantId() != null && userRepairInfo.getTenantId().longValue() > 0) {
            SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(userRepairInfo.getTenantId());
            userRepairInfoOut.setTenantName(sysTenantDef.getName());
        }
        if (StringUtils.isNotBlank(userRepairInfoOut.getServiceCharge())) {
            double v = CommonUtil.getDoubleFormatLongMoney(userRepairInfoOut.getTotalFee(), 2) * (Double.valueOf(userRepairInfoOut.getServiceCharge()) / 100);
            userRepairInfoOut.setServiceChargeFee(CommonUtil.getLongByString(String.valueOf(CommonUtil.getDoubleFormat(v, 2))));
        }
        userRepairInfoOut.setServiceCharge(userRepairInfo.getServiceCharge());
        userRepairInfoOut.setDriverUserId(userRepairInfo.getUserId());
        // ???????????????-??????
        List<RepairItemsWXOutDto> repairItemsWXOuts = this.getRepairItemsWXOut(null, vo.getRepairId());
        ;
        userRepairInfoOut.setItemList(repairItemsWXOuts);
        userRepairInfoOut.setRepairDate(com.youming.youche.market.commons.CommonUtil.formatDate(DateUtil.localDateTime2Date(userRepairInfo.getRepairDate()), "yyyy-MM-dd HH:mm:ss"));
        userRepairInfoOut.setDeliveryDate(com.youming.youche.market.commons.CommonUtil.formatDate(DateUtil.localDateTime2Date(userRepairInfo.getDeliveryDate()), "yyyy-MM-dd HH:mm:ss"));
        if (userRepairInfo.getIsFund() != null && userRepairInfo.getIsFund() == 2 && userRepairInfo.getPayWay() == 4) {
            userRepairInfoOut.setCashFee(userRepairInfo.getTotalFee());
        }
        List<Map> maps = iAuditNodeInstService.getAuditNodeInstNew(AuditConsts.AUDIT_CODE.REPAIR_INFO, userRepairInfo.getId(), userRepairInfo.getTenantId(), true, AuditConsts.RESULT.SUCCESS, AuditConsts.RESULT.FAIL);
        if (maps != null && maps.size() > 0) {
            for (Map map : maps) {
                map.remove("auditResult");
                map.remove("createDate");
                map.remove("linkman");
                map.remove("mobilePhone");
                map.remove("userType");
                map.remove("userTypeName");
            }
        } else {
            maps = new ArrayList<>();
        }
        /**????????????????????????*/
        userRepairInfoOut.setPayWayName(userRepairInfoOut.getPayWay() == null ? "" : getSysStaticData("REPAIR_PAY_WAY", String.valueOf(userRepairInfoOut.getPayWay())).getCodeName());
        /**?????????????????????*/
        userRepairInfoOut.setRepairStateName(userRepairInfoOut.getRepairState() == null ? "" : getSysStaticData("REPAIR_STATE", String.valueOf(userRepairInfoOut.getRepairState())).getCodeName());
        /**??????????????????*/
        userRepairInfoOut.setAppRepairStateName(userRepairInfoOut.getAppRepairState() == null ? "" : getSysStaticData("APP_REPAIR_STATE", String.valueOf(userRepairInfoOut.getAppRepairState())).getCodeName());
        userRepairInfoOut.setAuditMap(maps);
        userRepairInfoOut.setStateName(userRepairInfo.getState() != null && userRepairInfo.getState() == 1 ? "??????" : "??????");
        return userRepairInfoOut;
    }

    /**
     * ???????????????-??????
     *
     * @param tenantId
     * @param repairId
     * @return
     * @throws Exception
     */
    @Override
    public List<RepairItemsWXOutDto> getRepairItemsWXOut(Long tenantId, Long repairId) {
        List<RepairItems> repairItems = getRepairItemsList(tenantId, repairId);
        List<RepairItemsWXOutDto> repairItemsWXOuts = new ArrayList<>();
        if (repairItems != null && repairItems.size() > 0) {
            for (RepairItems repairItem : repairItems) {
                RepairItemsWXOutDto repairItemsWXOut = new RepairItemsWXOutDto();
                BeanUtil.copyProperties(repairItem, repairItemsWXOut);
                if (StringUtils.isNotBlank(repairItemsWXOut.getRepairPicIds())) {
                    String[] split = repairItemsWXOut.getRepairPicIds().split(",");
                    String[] repairPicUrlArr = new String[split.length];
                    for (int i = 0; i < split.length; i++) {
                        String imgUrl = iServiceProductService.getImgUrl(Long.valueOf(split[i]));
                        repairPicUrlArr[i] = imgUrl;
                    }
                    repairItemsWXOut.setRepairPicUrlArr(repairPicUrlArr);
                }
                repairItemsWXOuts.add(repairItemsWXOut);
            }
        }
        return repairItemsWXOuts;
    }

    /**
     * ???????????????
     *
     * @param tenantId
     * @param repairId
     * @return
     */
    public List<RepairItems> getRepairItemsList(Long tenantId, long repairId) {
        LambdaQueryWrapper<RepairItems> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null && tenantId > 0) {
            wrapper.eq(RepairItems::getTenantId, tenantId);
        }
        wrapper.eq(RepairItems::getRepairId, repairId);
        List<RepairItems> repairItems = repairItemsMapper.selectList(wrapper);
        return repairItems;
    }

    private static List<Integer> payWays = new ArrayList<>();
    private static List<Integer> priceStars = new ArrayList<>();
    private static List<Integer> qualityStars = new ArrayList<>();
    private static List<Integer> serviceStars = new ArrayList<>();

    static {
        payWays.add(1);
        payWays.add(2);
        payWays.add(3);
        payWays.add(4);

        priceStars.add(1);
        priceStars.add(2);
        priceStars.add(3);
        priceStars.add(4);
        priceStars.add(5);


        qualityStars.add(1);
        qualityStars.add(2);
        qualityStars.add(3);
        qualityStars.add(4);
        qualityStars.add(5);

        serviceStars.add(1);
        serviceStars.add(2);
        serviceStars.add(3);
        serviceStars.add(4);
        serviceStars.add(5);

    }

    @Override
    public String cashReceipt(Long repairId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(user.getUserInfoId());
        if (userDataInfo.getUserType() != SysStaticDataEnum.USER_TYPE.SERVICE_USER && userDataInfo.getUserType() != SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER) {
            throw new BusinessException("???????????????????????????????????????????????????");
        }
        UserRepairInfo userRepairInfo = this.getUserRepairInfo(repairId);
        if (userRepairInfo.getRepairState() != RepairConsts.REPAIR_STATE.RECEIPT_TO_BE_CONFIRMED) {
            throw new BusinessException("???????????????????????????????????????");
        }
        userRepairInfo.setRepairState(RepairConsts.REPAIR_STATE.PAID_FOR);
        userRepairInfo.setAppRepairState(RepairConsts.APP_REPAIR_STATE.PAID);
        userRepairInfo.setIsConfirmCash(1);
        this.saveOrUpdate(userRepairInfo);
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.RepairInfo, userRepairInfo.getId(),
                SysOperLogConst.OperType.Update, "?????????[" + userRepairInfo.getServiceName() + "]?????????????????????");
        return "Y";
    }

    @Override
    public UserRepairInfo getUserRepairInfo(Long repairId) {
        UserRepairInfo userRepairInfo = this.getById(repairId);
        return userRepairInfo;
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

    @Override
    public List<UserRepairInfo> lists(Long id, Long tenantId, String beginDate, String endDate) {
        QueryWrapper<UserRepairInfo> userRepairInfoQueryWrapper = new QueryWrapper<>();
        userRepairInfoQueryWrapper.eq("vehicle_code", id).eq("tenant_id", tenantId);
        userRepairInfoQueryWrapper.between("create_time", beginDate, endDate);
        List<UserRepairInfo> list = this.list(userRepairInfoQueryWrapper);
        return list;
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken) {
        audit(busiId, true, desc, accessToken);
    }

    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String accessToken) {
        audit(busiId, false, desc, accessToken);
    }

    @Override
    public void auditingCallBack(Long busiId) {
        UserRepairInfo userRepairInfo = this.getById(busiId);
        Integer repairState = userRepairInfo.getRepairState();
        if (repairState == RepairConsts.REPAIR_STATE.WAIT_AUDIT) {
            userRepairInfo.setAppRepairState(RepairConsts.REPAIR_STATE.AUDITING);
            userRepairInfo.setRepairState(RepairConsts.REPAIR_STATE.AUDITING);
            this.saveOrUpdate(userRepairInfo);
        }
    }

    @Override
    public void companyPayForRepairFee(UserRepairInfo userRepairInfo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (userRepairInfo == null) {
            throw new BusinessException("????????????????????????");
        }
        //?????????ID
        Long userId = userRepairInfo.getUserId();
        //????????????
        Long amountFee = userRepairInfo.getTotalFee();
        //?????????id
        Long serviceUserId = userRepairInfo.getServiceUserId();
        //??????id
        Long productId = userRepairInfo.getProductId();
        //????????????????????????????????????????????????
        LocalDateTime getDate = userRepairInfo.getGetDate();
        //????????????
        Integer payWay = userRepairInfo.getPayWay();
        //????????????
        Integer isBill = userRepairInfo.getIsBill();
        //??????id
        Long tenantId = userRepairInfo.getTenantId();
        if (userId == null || userId <= 1) {
            throw new BusinessException("?????????????????????id");
        }
        if (amountFee == null || amountFee <= 0L) {
            throw new BusinessException("?????????????????????");
        }
        if (serviceUserId == null || serviceUserId <= 1) {
            throw new BusinessException("??????????????????id");
        }
        if (productId == null || productId <= 0L) {
            throw new BusinessException("???????????????id");
        }
        if (getDate == null) {
            throw new BusinessException("?????????????????????");
        }
        if (payWay == null || !EnumConsts.REPAIR_TYPE.SELECT_PAYWAY1.equals(String.valueOf(payWay))) {
            throw new BusinessException("???????????????????????????");
        }
        if (isBill == null || isBill <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "companyPayForRepairFee" + userRepairInfo.getRepairId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }

        // ????????????id??????????????????
        UserDataInfo user = iUserDataInfoService.getUserDataInfo(userId);
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null, user.getTenantId());
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }

        // ????????????id?????????????????????id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }

        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }

        // ????????????id??????????????????
        SysUser serviceSysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(serviceUserId, null, 0L);
        if (serviceSysOperator == null) {
            throw new BusinessException("?????????????????????????????????!");
        }

        String vehicleAffiliation = null;
        String oilAffiliation = null;
        //??????
        int isNeedBill = 0;
        if (isBill == OrderAccountConst.SERVICE_OPEN_BILL.YES) {
            vehicleAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
            isNeedBill = OrderAccountConst.ORDER_IS_NEED_BILL.YES;
            oilAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
        } else {
            vehicleAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
            isNeedBill = OrderAccountConst.ORDER_IS_NEED_BILL.NO;
            oilAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
        }

        //????????????????????????????????????????????????????????????
        OrderAccount account = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();

        // ???????????????
        BusiSubjectsRel sub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.COMPANY_PAY_REPAIR, amountFee);
        sub.setOtherId(serviceSysOperator.getUserInfoId());
        sub.setOtherName(serviceSysOperator.getOpName());
        busiList.add(sub);

        // ??????????????????
        List<BusiSubjectsRel> subjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, busiList);
        long soNbr = com.youming.youche.record.common.CommonUtil.createSoNbr();
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAY_FOR_REPAIR,
                sysOperator.getUserInfoId(), sysOperator.getOpName(), account, subjectsRelList, soNbr, 0L,
                tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

        //??????????????????????????????????????????
        OrderAccount serviceAccount = iOrderAccountService.queryOrderAccount(serviceUserId, vehicleAffiliation, 0L, tenantId, oilAffiliation, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        List<BusiSubjectsRel> serviceBusiList = new ArrayList<BusiSubjectsRel>();
        // ???????????????
        BusiSubjectsRel serviceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FEE_MARGIN, amountFee);
        serviceBusiList.add(serviceSub);
        // ??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.INCOME_REPAIR, serviceBusiList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.INCOME_REPAIR,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), serviceAccount, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

        //??????????????????????????????
        List<UserRepairMargin> repairList = new ArrayList<UserRepairMargin>();
        UserDataInfo byId = iUserDataInfoService.getById(serviceUserId);
        UserRepairMargin userRepairMargin = iUserRepairMarginService.createUserRepairMargin(serviceUserId, serviceSysOperator.getBillId(),
                byId.getLinkman(), OrderAccountConst.CONSUME_COST_TYPE.REPAIR_TYPE2,
                "0", amountFee, userRepairInfo.getUserId(), userRepairInfo.getUserBill(),
                userRepairInfo.getUserName(), vehicleAffiliation, tenantId, productId, isBill, accessToken);
        userRepairMargin.setRepairId(userRepairInfo.getId());
        userRepairMargin.setOrderId(userRepairInfo.getRepairCode());
        // ????????????????????? 1?????????2??????
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(serviceUserId);
        if (tenantServiceRel != null) {
            LocalDateTime updateTime = userRepairInfo.getUpdateTime();
            if (tenantServiceRel.getBalanceType() == 1) {
                userRepairMargin.setGetDate(updateTime.plusDays(tenantServiceRel.getPaymentDays()));
            } else if (tenantServiceRel.getBalanceType() == 2) {
                userRepairMargin.setGetDate(getLastLocalDateTime(updateTime, tenantServiceRel.getPaymentMonth(), tenantServiceRel.getPaymentDays()));
            }
        } else {
            userRepairMargin.setGetDate(userRepairInfo.getGetDate());
        }
        userRepairMargin.setProductName(userRepairInfo.getProductName());
        userRepairMargin.setAddress(userRepairInfo.getAddress());
        userRepairMargin.setGetResult("?????????");
        userRepairMargin.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//?????????
        userRepairMargin.setUndueAmount(amountFee);
        userRepairMargin.setExpiredAmount(0L);
        userRepairMargin.setServiceCharge(0L);
        userRepairMargin.setRepairBalance(0L);
        userRepairMargin.setBalance(0L);
        userRepairMargin.setMarginBalance(0L);
        userRepairMargin.setAdvanceFee(0L);
        userRepairMargin.setSubjectsId(EnumConsts.SubjectIds.COMPANY_PAY_REPAIR);
        userRepairMargin.setOilAffiliation(oilAffiliation);
        if (userRepairInfo.getServiceCharge() != null && !"".equals(userRepairInfo.getServiceCharge())) {
            double rate = Double.parseDouble(userRepairInfo.getServiceCharge());
            long platformAmount = new Double(amountFee * rate / 100).longValue();
            userRepairMargin.setPlatformAmount(platformAmount);
            userRepairMargin.setPlatformRate(userRepairInfo.getOilRateInvoice());
            //?????????????????????
            PlatformServiceCharge pfsc = new PlatformServiceCharge();
            pfsc.setUserId(userRepairMargin.getUserId());
            pfsc.setUserName(userRepairMargin.getUserName());
            pfsc.setMobilePhone(userRepairMargin.getUserBill());
            pfsc.setConsumeAmount(userRepairMargin.getAmount());
            pfsc.setPlatformServiceCharge(userRepairMargin.getPlatformAmount());
            pfsc.setNoVerificationAmount(userRepairMargin.getPlatformAmount());
            pfsc.setVerificationAmount(0L);
            pfsc.setIsVerification(OrderAccountConst.IS_VERIFICATION.NO_VERIFICATION);//?????????
            pfsc.setCostType(OrderAccountConst.PLATFORM_SERVICE_CHARGE_TYPE.REPAIR_TYPE);
            pfsc.setTenantId(-1L);
            pfsc.setConsumeFlowId(userRepairMargin.getId());
            pfsc.setCreateDate(userRepairMargin.getCreateTime());
            pfsc.setOpId(userRepairMargin.getOpId());
            iPlatformServiceChargeService.saveOrUpdate(pfsc);
        }

        iUserRepairMarginService.saveOrUpdate(userRepairMargin);
        repairList.add(userRepairMargin);
        // ?????????????????????????????????????????????
        ParametersNewDto inParamNew = iOrderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.PAY_FOR_REPAIR, 0L, amountFee, vehicleAffiliation, "");
        inParamNew.setServiceUserId(serviceUserId);
        inParamNew.setTenantId(tenantId);
        inParamNew.setProductId(productId);
        inParamNew.setUserRepairMarginlist(repairList);
        inParamNew.setIsNeedBill(isNeedBill);
        inParamNew.setOilAffiliation(oilAffiliation);
        iOrderOilSourceService.busiToOrderNew(inParamNew, subjectsRelList, loginInfo);
    }

    @Override
    public List<VehicleAfterServingDto> getVehicleAfterServingByMonth(String plateNumber, Long tenantId, String month) {
        return baseMapper.getVehicleAfterServingByMonth(plateNumber, tenantId, month);
    }

    @Override
    public List<ServiceSerial> getUserRepairInfoList(Long serviceUserId, String startTime,
                                                     String endTime, Integer serviceType,
                                                     String serialNumber, String token) {
        LoginInfo loginInfo = loginUtils.get(token);
        LambdaQueryWrapper<ServiceSerial> lambda = Wrappers.lambdaQuery();
        if (serviceUserId != null) {
            lambda.eq(ServiceSerial::getServiceUserId, serviceUserId);
        }
        if (StringUtils.isNotBlank(endTime)) {
            lambda.le(ServiceSerial::getTime, endTime);
        }
        if (StringUtils.isNotBlank(startTime)) {
            lambda.ge(ServiceSerial::getTime, startTime);
        }
        if (StringUtils.isNotBlank(serialNumber)) {
            lambda.eq(ServiceSerial::getSerialNumber, serialNumber);
        }
        lambda.eq(ServiceSerial::getTenantId, loginInfo.getTenantId())
                .orderByDesc(ServiceSerial::getId);
        return serviceSerialService.list(lambda);


//        LoginInfo loginInfo = loginUtils.get(token);
//        ServiceInfoVo infoByServiceUserId = serviceInfoService.seeServiceInfoMotorcade(serviceUserId);
//        List<ServiceVo> serviceVos = new ArrayList<>();
//        if (serviceType == 1) {//???
//
//            List<ConsumeOilFlow> oilFlowByServiceId = consumeOilFlowService.getConsumeOilFlowByServiceId(serviceUserId, endTime, startTime);
//            if (oilFlowByServiceId != null && oilFlowByServiceId.size() > 0) {
//                for (ConsumeOilFlow oilFlow : oilFlowByServiceId) {
//                    ServiceVo serviceVo = new ServiceVo();
//                    serviceVo.setServiceName(infoByServiceUserId.getServiceName());
//                    serviceVo.setAmount(oilFlow.getAmount());
//                    serviceVo.setTime(oilFlow.getCreateTime());
//                    serviceVo.setSerialData(oilFlow.getOrderId());
//                    serviceVo.setMobile(infoByServiceUserId.getLoginAcct());
//                    serviceVo.setPlateNumber(oilFlow.getPlateNumber());
//                    serviceVo.setDriverName(oilFlow.getOtherName());
//                    serviceVo.setTypeName("??????");
//                    serviceVos.add(serviceVo);
//                }
//            }
//        } else if (serviceType == 2) { //??????
//            List<UserRepairInfo> list = userRepairInfoMapper.getUserRepairInfo(serviceUserId, endTime, startTime);
//            if (list != null && list.size() > 0) {
//                for (UserRepairInfo repairInfo : list) {
//                    ServiceVo serviceVo = new ServiceVo();
//                    serviceVo.setServiceName(infoByServiceUserId.getServiceName());
//                    serviceVo.setAmount(repairInfo.getTotalFee());
//                    serviceVo.setTime(repairInfo.getCreateTime());
//                    serviceVo.setSerialData(repairInfo.getRepairCode());
//                    serviceVo.setMobile(infoByServiceUserId.getLoginAcct());
//                    serviceVo.setPlateNumber(repairInfo.getPlateNumber());
//                    serviceVo.setDriverName(repairInfo.getUserName());
//
//                    serviceVo.setTypeName(repairInfo.getState() != null && repairInfo.getState()==1 ? "??????":"??????");
//
//                    serviceVos.add(serviceVo);
//                }
//            }
//        }
//
//        return serviceVos;
    }

    /**
     * ??????
     *
     * @param repairId
     * @param isPass
     * @param msg
     * @throws Exception
     */
    private void audit(Long repairId, boolean isPass, String msg, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (repairId == null || repairId < 0) {
            throw new BusinessException("????????????????????????");
        }

        UserRepairInfo repairInfo = this.getUserRepairInfo(repairId);
        if (repairInfo == null) {
            throw new BusinessException("?????????????????????");
        }

        if (repairInfo.getIsSure() != 1) {
            throw new BusinessException("?????????????????????????????????");
        }

        if (isPass) {
            repairInfo.setRepairState(RepairConsts.REPAIR_STATE.UNEXPIRED);
            repairInfo.setAppRepairState(RepairConsts.APP_REPAIR_STATE.PASS_AUDIT);
            repairInfo.setAuditMan(loginInfo.getName());
            repairInfo.setAuditManId(loginInfo.getUserInfoId());
            repairInfo.setUpdateTime(LocalDateTime.now());
        } else {
            repairInfo.setRepairState(RepairConsts.REPAIR_STATE.AUDIT_FAIL);
            repairInfo.setAppRepairState(RepairConsts.APP_REPAIR_STATE.AUDIT_FAIL);
            repairInfo.setUpdateTime(LocalDateTime.now());
        }
        this.saveOrUpdate(repairInfo);

        if (isPass) {
            this.companyPayForRepairFee(repairInfo, accessToken);
            TenantServiceRel serviceRel = tenantServiceRelService.getTenantServiceRel(loginInfo.getTenantId(), repairInfo.getServiceUserId());
            if (serviceRel != null && serviceRel.getBalanceType() != null) {
                Integer balanceType = serviceRel.getBalanceType();
                if (balanceType != null && balanceType == 1 && serviceRel.getPaymentDays() != null && serviceRel.getPaymentDays() == 0) {
                    setPayData(accessToken, repairInfo);
                } else if (balanceType != null && balanceType == 2 && serviceRel.getPaymentDays() != null && serviceRel.getPaymentMonth() != null && serviceRel.getPaymentDays()==0  && serviceRel.getPaymentMonth()==0) {
                    setPayData(accessToken, repairInfo);
                } else if (balanceType != null && balanceType == 3) {
                    setPayData(accessToken, repairInfo);
                }
            }
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.RepairInfo, repairInfo.getId(), SysOperLogConst.OperType.Audit, msg);
        } else {
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.RepairInfo, repairInfo.getId(), SysOperLogConst.OperType.Audit, msg);
        }

    }

    private void setPayData(String accessToken, UserRepairInfo repairInfo) {
        UserRepairMargin repairCode = iUserRepairMarginService.getUserRepairMarginByRepairCode(repairInfo.getRepairCode());
        if(repairCode.getGetDate() == null){
            repairCode.setGetDate(repairInfo.getGetDate());
        }

        orderLimitService.marginTurnCash(repairCode.getUserId(), repairCode.getVehicleAffiliation(), repairCode.getUndueAmount(), repairCode.getId(), repairCode.getTenantId(), OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3, repairCode.getOilAffiliation(), -1L, repairCode, accessToken);

        repairCode.setExpiredAmount((repairCode.getExpiredAmount() == null ? 0L : repairCode.getExpiredAmount()) + repairCode.getUndueAmount());
        repairCode.setUndueAmount(0L);
        repairCode.setGetResult("????????????");
        repairCode.setState(OrderAccountConst.STATE.SUCCESS);
        iUserRepairMarginService.saveOrUpdate(repairCode);
        if (repairCode.getRepairId() != null && repairCode.getRepairId() > 0) {
            repairInfo.setRepairState(SysStaticDataEnum.REPAIR_STATE.DUE);
            this.saveOrUpdate(repairInfo);
        }
    }

    private static LocalDateTime getLastLocalDateTime(LocalDateTime ldt, Integer months, Integer days) {

        String year = ldt.getYear() + "";
        String month = ldt.getMonthValue() + "";
        if (month.length() != 2) {
            month = "0" + month;
        }
        String dateStr = year + "-" + month + "-" + "01" + " 00:00:00";
        System.out.println(dateStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
        System.out.println(localDateTime);
        return localDateTime.plusMonths(months).plusDays(days - 1);

    }

}
