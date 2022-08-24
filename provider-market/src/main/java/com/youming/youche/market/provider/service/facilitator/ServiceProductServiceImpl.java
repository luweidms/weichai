package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.*;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.api.facilitator.*;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.api.youka.IOilCardManagementService;
import com.youming.youche.market.commons.RepairConsts;
import com.youming.youche.market.domain.facilitator.*;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.*;
import com.youming.youche.market.dto.youca.ProductNearByDto;
import com.youming.youche.market.dto.youca.ProductNearByOutDto;
import com.youming.youche.market.dto.youca.ServiceProductOutDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceProductMapper;
import com.youming.youche.market.provider.mapper.user.UserRepairInfoMapper;
import com.youming.youche.market.provider.transfer.*;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.*;
import com.youming.youche.market.vo.user.UserRepairInfoVo;
import com.youming.youche.market.vo.youca.ConsumeOilFlowVo;
import com.youming.youche.market.vo.youca.ProductNearByVo;
import com.youming.youche.market.vo.youca.ServiceProductOutVo;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderStatementService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.dto.OilServiceInDto;
import com.youming.youche.order.dto.OilServiceOutDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.record.common.GpsUtil;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;
import static com.youming.youche.conts.SysStaticDataEnum.IS_AUTH.IS_AUTH0;
import static com.youming.youche.conts.SysStaticDataEnum.PT_TENANT_ID;
import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.*;


/**
 * <p>
 * 服务商站点表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceProductServiceImpl extends BaseServiceImpl<ServiceProductMapper, ServiceProduct> implements IServiceProductService {
    private static final Log log = LogFactory.getLog(ServiceProductServiceImpl.class);
    @Resource
    private ServiceProductMapper serviceProductMapper;
    @Resource
    private LoginUtils loginUtils;
    @Lazy
    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private ITenantProductRelService tenantProductRelService;
    @Autowired
    private IServiceInfoService serviceInfoService;
    @Autowired
    private ITenantProductRelService iTenantProductRelService;
    @Autowired
    private ITenantServiceRelService tenantServiceRelService;
    @Autowired
    private IServiceProductVerService serviceProductVerService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService auditNodeInstService;
    @DubboReference(version = "1.0.0")
    IOperationOilService iOperationOilService;
    @DubboReference(version = "1.0.0")
    com.youming.youche.order.api.order.IConsumeOilFlowService iConsumeOilFlowService;
    @Resource
    private ServiceProductDtoTransfer serviceProductDtoTransfer;
    @Resource
    private IOilCardManagementService oilCardManagementService;
    @Resource
    private IEtcMaintainService etcMaintainService;
    @Lazy
    @Autowired
    private ServiceProductInfoVoTransfer serviceProductInfoVoTransfer;
    @Autowired
    private IUserDataInfoMarketService userDataInfoService;
    @Lazy
    @Autowired
    private IServiceProductEtcService serviceProductEtcService;
    @Lazy
    @Autowired
    private ITenantProductRelVerService tenantProductRelVerService;
    @Lazy
    @Resource
    private ProductDetailTransfer productDetailTransfer;
    @Lazy
    @Resource
    private CooperationTenantTransfer cooperationTenantTransfer;
    @Lazy
    @Autowired
    private IServiceProductEtcVerService serviceProductEtcVerService;
    @Lazy
    @Autowired
    private IAccountBankUserTypeRelService accountBankUserTypeRelService;
    @Lazy
    @Autowired
    private IServiceProductService serviceProductService;
    @Lazy
    @Autowired
    private IServiceInvitationService serviceInvitationService;
    @Lazy
    @Autowired
    private IServiceInvitationDtlService serviceInvitationDtlService;
    @Lazy
    @Resource
    private TenantProductRelTransfer tenantProductRelTransfer;
    @Lazy
    @Autowired
    private IOilPriceProvinceService oilPriceProvinceService;
    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @DubboReference(version = "1.0.0")
    IOpAccountService iOpAccountService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;
    @DubboReference(version = "1.0.0")
     IOrderStatementService iOrderStatementService;

    @Resource
    IUserRepairInfoService iUserRepairInfoService;
    @Resource
    IConsumeOilFlowService iConsumeOilFlowService1;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @Resource
    IOilPriceProvinceService iOilPriceProvinceService;
    @Resource
    IServiceInfoService iServiceInfoService;
    @Resource
    UserRepairInfoMapper userRepairInfoMapper;
    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;
    /**
     * 查询站点
     *
     * @param productQueryDto
     * @param accessToken
     * @return
     */
    @Override
    public Page<ServiceProductVo> queryServiceProductList(ProductQueryDto productQueryDto, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if((productQueryDto.getServiceType() == null || productQueryDto.getServiceType()<=0)&&(productQueryDto.getBillBeginPrice() != null && productQueryDto.getBillBeginPrice()>0||productQueryDto.getBillEndPrice() != null && productQueryDto.getBillEndPrice()>0
                ||productQueryDto.getNoBillBeginPrice() != null && productQueryDto.getNoBillBeginPrice()>0||productQueryDto.getNoBillEndPrice() != null && productQueryDto.getNoBillEndPrice()>0)) {
            productQueryDto.setServiceType(SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL);
            productQueryDto.setIsShare(1);
        }
        Page<ServiceProductDto> page = new Page<>(pageNum, pageSize);
        Page<ServiceProductDto> serviceProductVos = serviceProductMapper.queryServiceProductList(page, productQueryDto, PT_TENANT_ID, user);
        List<ServiceProductDto> records = serviceProductVos.getRecords();
        List<Long> busiIdList = new ArrayList<>();
        List<ServiceProductVo> serviceProductVo = serviceProductDtoTransfer.getServiceProductVo(busiIdList, records, user);
        //设置是否有审核权限
        if (CollectionUtils.isNotEmpty(busiIdList)) {
            Map<Long, Boolean> hasPermission = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.SERVICE_PRODUCT, busiIdList, accessToken);

            for (ServiceProductVo productVo : serviceProductVo) {
                Boolean flg = hasPermission.get(productVo.getProductId());
                try {
                    productVo.setHasVer(flg != null && flg? 0 : 1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        Page<ServiceProductVo> productVoPage = new Page<>();
        productVoPage.setRecords(serviceProductVo);
        productVoPage.setCountId(serviceProductVos.getCountId());
        productVoPage.setCurrent(serviceProductVos.getCurrent());
        productVoPage.setSize(serviceProductVos.getSize());
        productVoPage.setTotal(serviceProductVos.getTotal());
        productVoPage.setHitCount(serviceProductVos.isHitCount());
        productVoPage.setMaxLimit(serviceProductVos.getMaxLimit());
        productVoPage.setPages(serviceProductVos.getPages());
        productVoPage.setSearchCount(serviceProductVos.isSearchCount());
        return productVoPage;
    }


    /**
     * 保存站点
     *
     * @param productSaveIn
     * @return
     */
    @Override
    public ResponseResult saveProduct(ProductSaveDto productSaveIn, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
//        if(user.getTenantId().longValue() != SysStaticDataEnum.PT_TENANT_ID){
//            throw new BusinessException("不是运营后台不能操作！");
//        }
        //     productSaveIn.setIsFleet(ServiceConsts.isFleet.OBMS);
        if (productSaveIn.getIsShare() == null) {
            productSaveIn.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
        }

        Long productId = productSaveIn.getProductId();
        boolean isUpdate = productId != null && productId > 0 ? true : false;

        if (productSaveIn.getServiceType() == null || productSaveIn.getServiceType() < 0) {
            log.error("服务商类型不能为空，请刷新页面！");
            throw new BusinessException("服务商类型不能为空，请刷新页面！");
        }
        if (productSaveIn.getIsFleet() != SysStaticDataEnum.isFleet.OBMS_SHARE) {
            if (productSaveIn.getServiceUserId() == null || productSaveIn.getServiceUserId() < 0) {
                log.error("服务商不能为空，请刷新页面！");
                throw new BusinessException("服务商不能为空，请刷新页面！");
            }
            if (productSaveIn.getServiceType() == OIL || productSaveIn.getServiceType() == REPAIR) {
                if (StringUtils.isBlank(productSaveIn.getProductName())) {
                    log.error("名称不能为空，请填写！");
                    throw new BusinessException("站点名称不能为空，请填写！");
                }
                if (productSaveIn.getProvinceId() == null || productSaveIn.getProvinceId() < 0) {
                    log.error("站点省份不能为空，请选择！");
                    throw new BusinessException("站点省份不能为空，请选择！");
                }
                if (productSaveIn.getCityId() == null || productSaveIn.getCityId() < 0) {
                    log.error("站点城市不能为空，请选择！");
                    throw new BusinessException("站点城市不能为空，请选择！");
                }
                if (StringUtils.isBlank(productSaveIn.getEand()) || StringUtils.isBlank(productSaveIn.getNand())) {
                    log.error("详细地址不能为空，请选择！");
                    throw new BusinessException("详细地址不能为空，请选择！");
                }

                if (productSaveIn.getServiceType() == OIL && (null == productSaveIn.getOilCardType() || productSaveIn.getOilCardType() < 0)) {
                    log.error("请选择油站类型！");
                    throw new BusinessException("请选择油站类型！");
                }

                if (null == productSaveIn.getLocationType() || productSaveIn.getLocationType() < 0) {
                    log.error("请选择所属位置！");
                    throw new BusinessException("请选择所属位置！");
                }
            }
        }
        if (productSaveIn.getIsFleet() != SysStaticDataEnum.isFleet.TENANT) {
            if (productSaveIn.getServiceType() != SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC && (productSaveIn.getIsBillAbility() == null || productSaveIn.getIsBillAbility() < 0)) {
//                throw new BusinessException("支持开票不能为空，请选择！");
                productSaveIn.setIsBillAbility(1);
            }
//            if (productSaveIn.getIsShare() == null || productSaveIn.getIsShare() < 0) {
//                throw new BusinessException("是否共享不能为空，请选择！");
//            }
            if (productSaveIn.getServiceType() == OIL
                    && productSaveIn.getIsShare() == SysStaticDataEnum.IS_SHARE.YES) {
                if (StringUtils.isBlank(productSaveIn.getFloatBalanceBill()) && productSaveIn.getFixedBalanceBill() == null) {
                    log.error("结算单价不能为空，请选填！");
                    throw new BusinessException("结算单价不能为空，请选填！");
                }
                if (StringUtils.isNotBlank(productSaveIn.getFloatBalanceBill()) && StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())) {
                    log.error("结算单价只能选填一个，请重新填写！");
                    throw new BusinessException("结算单价只能选填一个，请重新填写！");
                }
                if (StringUtils.isBlank(productSaveIn.getServiceChargeBill())) {
                    log.error("结算单价平台服务费不能为空，请填写！");
                    throw new BusinessException("结算单价平台服务费不能为空，请填写！");
                }
            }
        } else {
            if(productSaveIn.getIsShare() == null){
                productSaveIn.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
            }
        }
        TenantProductRel tenantProductRel = null;
        if (isUpdate) {
            if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.SERVICE && (productSaveIn.getProductState() == null
                    || productSaveIn.getProductState() < 0)) {
                log.error("站点状态不能为空，请选择！");
                throw new BusinessException("站点状态不能为空，请选择！");
            }
            tenantProductRel = this.update(productSaveIn, user);
        } else {
            tenantProductRel = tenantProductRelService.save(productSaveIn, user);
        }
        if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.TENANT && (productSaveIn.getIsRegistered() == null
                || productSaveIn.getIsRegistered() < 0)) {
            //启动审核流程
            Map inMap = new HashMap();
            inMap.put("svName", "serviceProductTF");
            boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_PRODUCT, tenantProductRel.getProductId(),
                    SysOperLogConst.BusiCode.TenantProductRel, inMap, accessToken);
            if (!bool) {
                throw new BusinessException("数据异常，请联系客服！");
            }
        }
        return ResponseResult.success();
    }

    @Override
    public List<ServiceProduct> queryServiceProduct(Long serviceUserId, Long tenantId) {
        LambdaQueryWrapper<ServiceProduct> lambda= Wrappers.lambdaQuery();
        lambda.eq(ServiceProduct::getServiceUserId,serviceUserId)
              .eq(ServiceProduct::getTenantId,tenantId);
        return this.list(lambda);
    }


    /**
     * 保存站点
     *
     * @param productSaveIn
     * @return
     */
    @Override
    public ResponseResult saveOrUpdate(ProductSaveDto productSaveIn, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        //   productSaveIn.setIsFleet(ServiceConsts.isFleet.OBMS);
        if (productSaveIn.getIsShare() == null) {
            productSaveIn.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
        }
        Long productId = productSaveIn.getProductId();
        boolean isUpdate = productId != null && productId > 0 ? true : false;

        if (productSaveIn.getServiceType() == null || productSaveIn.getServiceType() < 0) {
            log.error("服务商类型不能为空，请刷新页面！");
            return ResponseResult.failure("服务商类型不能为空，请刷新页面！");
        }
        if (productSaveIn.getIsFleet() != SysStaticDataEnum.isFleet.OBMS_SHARE) {
            if (productSaveIn.getServiceUserId() == null || productSaveIn.getServiceUserId() < 0) {
                log.error("服务商不能为空，请刷新页面！");
                return ResponseResult.failure("服务商不能为空，请刷新页面！");
            }
            if (productSaveIn.getServiceType() == OIL || productSaveIn.getServiceType() == REPAIR) {
                if (StringUtils.isBlank(productSaveIn.getProductName())) {
                    log.error("站点名称不能为空，请填写！");
                    return ResponseResult.failure("站点名称不能为空，请填写！");
                }
                if (productSaveIn.getProvinceId() == null || productSaveIn.getProvinceId() < 0) {
                    log.error("站点省份不能为空，请选择！");
                    return ResponseResult.failure("站点省份不能为空，请选择！");
                }
                if (productSaveIn.getCityId() == null || productSaveIn.getCityId() < 0) {
                    log.error("站点城市不能为空，请选择！");
                    return ResponseResult.failure("站点城市不能为空，请选择！");
                }
                if (StringUtils.isBlank(productSaveIn.getEand()) || StringUtils.isBlank(productSaveIn.getNand())) {
                    log.error("详细地址不能为空，请选择！");
                    return ResponseResult.failure("详细地址不能为空，请选择！");
                }

                if (productSaveIn.getServiceType() == OIL && (null == productSaveIn.getOilCardType() || productSaveIn.getOilCardType() < 0)) {
                    log.error("请选择油站类型！");
                    return ResponseResult.failure("请选择油站类型！");
                }

                if (null == productSaveIn.getLocationType() || productSaveIn.getLocationType() < 0) {
                    log.error("请选择所属位置！");
                    return ResponseResult.failure("请选择所属位置！");
                }
            }
        }
        if (productSaveIn.getIsFleet() != SysStaticDataEnum.isFleet.TENANT) {
            if (productSaveIn.getServiceType() != SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC && (productSaveIn.getIsBillAbility() == null || productSaveIn.getIsBillAbility() < 0)) {
//                throw new BusinessException("支持开票不能为空，请选择！");
                productSaveIn.setIsBillAbility(1);
            }
//            if (productSaveIn.getIsShare() == null || productSaveIn.getIsShare() < 0) {
//                throw new BusinessException("是否共享不能为空，请选择！");
//            }
            if (productSaveIn.getServiceType() == OIL
                    && productSaveIn.getIsShare() == SysStaticDataEnum.IS_SHARE.YES) {
                if (StringUtils.isBlank(productSaveIn.getFloatBalanceBill()) && productSaveIn.getFixedBalanceBill() == null) {
                    log.error("结算单价不能为空，请选填！");
                    return ResponseResult.failure("结算单价不能为空，请选填！");
                }
                if (StringUtils.isNotBlank(productSaveIn.getFloatBalanceBill()) && StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())) {
                    log.error("结算单价只能选填一个，请重新填写！");
                    return ResponseResult.failure("结算单价只能选填一个，请重新填写！");
                }
                if (StringUtils.isBlank(productSaveIn.getServiceChargeBill())) {
                    log.error("结算单价平台服务费不能为空，请填写！");
                    return ResponseResult.failure("结算单价平台服务费不能为空，请填写！");
                }
            }
        } else {
            productSaveIn.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
        }
        TenantProductRel tenantProductRel = null;
        if (isUpdate) {
            if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.SERVICE && (productSaveIn.getProductState() == null
                    || productSaveIn.getProductState() < 0)) {
                log.error("站点状态不能为空，请选择！");
                return ResponseResult.failure("站点状态不能为空，请选择！");
            }
            tenantProductRel = this.update(productSaveIn, user);
        } else {
            tenantProductRel = tenantProductRelService.save(productSaveIn, user);
        }
        if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.TENANT && (productSaveIn.getIsRegistered() == null
                || productSaveIn.getIsRegistered() < 0)) {

            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, productSaveIn.getProductId(), SysOperLogConst.OperType.Audit, "提交审核");

            //启动审核流程
            Map inMap = new HashMap();
            inMap.put("svName", "iServiceProductService");
            boolean bool = false;
            try {
                bool = auditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_PRODUCT, user.getTenantId(),
                        SysOperLogConst.BusiCode.ServiceProduct, inMap, accessToken);
                if (!bool) {
                    log.error("启动审核流程失败！");
                    throw new BusinessException("启动审核流程失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return ResponseResult.success("success");
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        LoginInfo user = loginUtils.get(token);
//        String svName = DataFormat.getStringKey(paramsMap, "svName");
//        if (StringUtils.isBlank(svName)) {
//            throw new BusinessException("sv的bean-id不能为空！");
//        }
//        ITenantProductRelSV tenantProductRelSV = (ITenantProductRelSV) SysContexts.getBean("tenantProductRelSV");
        LambdaQueryWrapper<TenantProductRel> lambda = new QueryWrapper<TenantProductRel>().lambda();
        lambda.eq(TenantProductRel::getProductId, busiId)
              .eq(TenantProductRel::getTenantId,user.getTenantId());
        TenantProductRel tenantProductRel = tenantProductRelService.getOne(lambda);
        if (tenantProductRel == null) {
            throw new BusinessException("站点租户关系数据有误！");
        }
        TenantProductRelVer tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
        boolean isUpdatePrice = false;
//        IServiceInvitationTF serviceInvitationTF = (IServiceInvitationTF) SysContexts.getBean("serviceInvitationTF");
//        IServiceProductSV serviceProductSV = (IServiceProductSV) SysContexts.getBean("serviceProductSV");
        ServiceProduct serviceProduct = this.getById(tenantProductRel.getProductId());
        Long productId = null;
        if(serviceProduct!= null && serviceProduct.getId() != null){
            productId= serviceProduct.getId();
        }
        String floatBalance = null;
        if (tenantProductRel.getFloatBalance() != null) {
            floatBalance = tenantProductRel.getFloatBalance();
        }

        String floatBalanceVer = null;
        if (tenantProductRelVer.getFloatBalance() != null) {
            floatBalanceVer = tenantProductRelVer.getFloatBalance();
        }
        Long fixedBalance = null;
        if (tenantProductRel.getFixedBalance() != null) {
            fixedBalance = tenantProductRel.getFixedBalance();
        }
        Long fixedBalanceVer = null;
        if (tenantProductRelVer.getFixedBalance() != null) {
            fixedBalanceVer = tenantProductRelVer.getFixedBalance();
        }

        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceProduct.getServiceUserId());
        if (null == serviceInfo) {
            throw new BusinessException("服务商信息不存在！");
        }
        //检验价格是否变动 - 是的话需要 服务商审核通过
        if (serviceInfo.getServiceType() == OIL) {
            if (serviceProduct.getIsBillAbility() != null && serviceProduct.getIsBillAbility().equals(SysStaticDataEnum.BILL_ABILITY.ENABLE)) {
                isUpdatePrice = this.checkPriceChange(tenantProductRel.getFloatBalanceBill(), tenantProductRelVer.getFloatBalanceBill()
                        , tenantProductRel.getFixedBalanceBill(), tenantProductRelVer.getFixedBalanceBill());
            } else {
                isUpdatePrice = this.checkPriceChange(floatBalance, floatBalanceVer, fixedBalance, fixedBalanceVer);
            }
        }

        ServiceInvitation serviceInvitation = null;
        //如果是油卡服务商需要检验开票和不开票价格是否变动
        if (serviceInfo.getServiceType() == OIL_CARD) {
            boolean isUpdatePriceExt = this.checkPriceChange(tenantProductRel.getFloatBalanceBill(), tenantProductRelVer.getFloatBalanceBill(), tenantProductRel.getFixedBalanceBill(), tenantProductRelVer.getFixedBalanceBill());
            isUpdatePrice = isUpdatePrice || isUpdatePriceExt;
        }
        if ((tenantProductRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO && tenantProductRelVer.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_YES)
                && (tenantProductRel.getCooperationState() != null && tenantProductRel.getCooperationState() == ServiceConsts.COOPERATION_STATE.RELEASED)
        ) {
            serviceInvitation = serviceInvitationService.saveInvitation(user, tenantProductRel.getProductId(),
                    tenantProductRel.getTenantId(), tenantProductRelVer.getFixedBalance(),
                    tenantProductRelVer.getFloatBalance(), tenantProductRelVer.getFixedBalanceBill(), tenantProductRelVer.getFloatBalanceBill(), null, serviceProduct.getServiceUserId(),
                    ServiceConsts.COOPERATION_TYPE.NEW_COOPERATION, tenantProductRelVer.getLocaleBalanceState());
        } else if (isUpdatePrice) {//价格发生了变动就 需要提交到 服务商审核

            //生成一条修改申请合作信息
            serviceInvitation = serviceInvitationService.saveInvitation(user, tenantProductRel.getProductId(),
                    tenantProductRel.getTenantId(), tenantProductRelVer.getFixedBalance(),
                    tenantProductRelVer.getFloatBalance(), tenantProductRelVer.getFixedBalanceBill(), tenantProductRelVer.getFloatBalanceBill(), null, serviceProduct.getServiceUserId(),
                    ServiceConsts.COOPERATION_TYPE.UPDATE_COOPERATION, tenantProductRelVer.getLocaleBalanceState());

            tenantProductRel.setServiceAuthState(ServiceConsts.SERVICE_AUTH_STATE.WAIT);
            tenantProductRel.setState(tenantProductRelVer.getState());
        } else {
            Long id = null;
            if (tenantProductRel.getId() != null) {
                id = tenantProductRel.getId();
            }
            BeanUtils.copyProperties(tenantProductRelVer, tenantProductRel,"isShare","serviceCall","productName","introduce","provinceId","cityId","countyId","address");
            if (id != null) {
                tenantProductRel.setId(id);
            }
            tenantProductRel.setIsAuth(IS_AUTH0);
            tenantProductRel.setState(tenantProductRelVer.getState());
        }
        tenantProductRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
        if (null != serviceInvitation) {
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.serviceInvitation, serviceInvitation.getId(), SysOperLogConst.OperType.Audit, "车队审核邀请合作");
        }
        //如果是服务商邀请车队,需要将服务商那边的车队审核状态改成已认证
        long dtlId = DataFormat.getLongKey(paramsMap, "dtlId");
        if (dtlId > 0) {
            ServiceInvitationDtl serviceInvitationDtl = serviceInvitationDtlService.get(dtlId);
            if (null != serviceInvitationDtl) {
                serviceInvitationDtl.setTenantAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
                serviceInvitationDtlService.update(serviceInvitationDtl);
            }
        }
        //如果是服务商邀请车队,需要将服务商那边的车队审核状态改成已认证
        //服务商银行卡绑定校验
//        IServiceInfoTF serviceInfoTF = (IServiceInfoTF) SysContexts.getBean("serviceInfoTF");
//        serviceInfoTF.checkServiceInfoBindBank(serviceProduct.getServiceUserId(), serviceProduct.getIsShare(),serviceProduct.getIsBillAbility());
        //tenantProductRel.setAuthReason(desc);

        tenantProductRel.setOpId(user.getId());
        tenantProductRelService.saveOrUpdate(tenantProductRel);
        if(serviceProduct.getTenantId().equals(user.getTenantId())){
            serviceProduct.setServiceCall(tenantProductRel.getServiceCall());
            serviceProduct.setCityId(tenantProductRel.getCityId());
            serviceProduct.setCountyId(tenantProductRel.getCountyId());
            serviceProduct.setProvinceId(tenantProductRel.getProvinceId());
            serviceProduct.setIntroduce(tenantProductRel.getIntroduce());
            serviceProduct.setAddress(tenantProductRel.getAddress());
            serviceProduct.setProductName(tenantProductRel.getProductName());
            serviceProduct.setIsShare(tenantProductRel.getIsShare());
            serviceProduct.setAuthState(2);
            serviceProduct.setState(tenantProductRel.getState());
            serviceProduct.setId(productId);
            this.update(serviceProduct);
        }
        sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(), SysOperLogConst.OperType.Audit, "审核通过"+(StringUtils.isNotBlank(desc) ? "，审核意见:"+desc:""));
    }

    @Override
    public void fail(Long busiId, String desc, Map paramsMap,String token) throws BusinessException {
        LoginInfo user = loginUtils.get(token);
//        String svName = DataFormat.getStringKey(paramsMap, "svName");
//        if (StringUtils.isBlank(svName)) {
//            throw new BusinessException("sv的bean-id不能为空！");
//        }
//        IBaseBusi baseBusiSV = (BaseBusiSV) SysContexts.getBean("baseBusiSV");
//        Object [] objects = baseBusiSV.doPublicAuth(TenantProductRel.class, TenantProductRelVer.class, busiId, SysOperLogConst.BusiCode.ServiceInfo, SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_FAIL, desc, "relId", "setAuthState", "setAuthReason");
        LambdaQueryWrapper<TenantProductRel> lambda = new QueryWrapper<TenantProductRel>().lambda();
        lambda.eq(TenantProductRel::getProductId, busiId);
        TenantProductRel relServiceOne = tenantProductRelService.getOne(lambda);
        LambdaUpdateWrapper<TenantProductRel> updateWrapper = new UpdateWrapper<TenantProductRel>().lambda();
        updateWrapper.set(TenantProductRel::getIsAuth, IS_AUTH0)
                .set(TenantProductRel::getAuthReason, desc)
                .eq(TenantProductRel::getId, relServiceOne.getId());
        tenantProductRelService.update(updateWrapper);
        sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel, relServiceOne.getProductId(), SysOperLogConst.OperType.Audit, "审核不通过"+(StringUtils.isNotBlank(desc) ? "，审核意见:"+desc:""));
    }


    /**
     * 校验站点有效性
     *
     * @return
     */
    @Override
    public Boolean checkProduct(ProductSaveDto productSaveIn, ServiceProduct serviceProduct) {
        if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.TENANT) {
            if (serviceProduct == null) {
                throw new BusinessException("站点信息有误，修改失败！");
            }
//            if (CommonUtil.checkEnumIsNull(serviceProduct.getState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)
//                    && (CommonUtil.checkEnumIsNotNull(productSaveIn.getState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES))) {
//                throw new BusinessException("该服务商站点为无效，不能设为有效合作站点！");
//            }
            if (CommonUtil.checkEnumIsNull(serviceProduct.getState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)
                    && (productSaveIn.getState() == null || productSaveIn.getState() < 0)) {
                throw new BusinessException("该服务商站点为无效，不能合作！");
            }
        }
        if ((!serviceProduct.getProductName().equals(productSaveIn.getProductName()))
                && (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.SERVICE
                || productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.OBMS)) {
            LambdaQueryWrapper<ServiceProduct> lambda = new QueryWrapper<ServiceProduct>().lambda();
            lambda.eq(ServiceProduct::getProductName, productSaveIn.getProductName());
            List<ServiceProduct> list = this.list(lambda);
            if (list != null && list.size() > 0) {
                throw new BusinessException("站点已经注册过，不能重复注册！");
            }
        }
        return true;
    }

    @Override
    public void saveOrUpdata(ServiceProduct serviceProduct, boolean isUpdate, LoginInfo user) {
        if (isUpdate) {
            serviceProduct.setUpdateTime(LocalDateTime.now());
            serviceProduct.setOpId(user.getId());
            super.updateById(serviceProduct);
        } else {
            serviceProduct.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            serviceProduct.setCreateTime(LocalDateTime.now());
            super.save(serviceProduct);
        }

    }

    @Override
    public List<ServiceProduct> getServiceProductByName(String productName) {
        LambdaQueryWrapper<ServiceProduct> lambda = new QueryWrapper<ServiceProduct>().lambda();
        if (productName != null && productName != "") {
            lambda.eq(ServiceProduct::getProductName, productName);
            List<ServiceProduct> list = this.list(lambda);
            return list;
        }
        return null;
    }

    @Override
    public List<ServiceProduct> getServiceProductByChild(Long childAccountUserId) {
        LambdaQueryWrapper<ServiceProduct> lambda = new QueryWrapper<ServiceProduct>().lambda();
        lambda.eq(ServiceProduct::getChildAccountUserId, childAccountUserId);
        List<ServiceProduct> list = this.list(lambda);
        return list;
    }

    @Override
    public void notBillAbility(Long serviceUserId, LoginInfo user) {
        LambdaQueryWrapper<ServiceProduct> lambda = new QueryWrapper<ServiceProduct>().lambda();
        lambda.eq(ServiceProduct::getServiceUserId, serviceUserId);
        List<ServiceProduct> serviceProductList = this.list(lambda);
        if (serviceProductList != null && serviceProductList.size() > 0) {
            for (ServiceProduct serviceProduct : serviceProductList) {
                if (serviceProduct.getIsBillAbility() == null || serviceProduct.getIsBillAbility() == ServiceConsts.IS_BILL_ABILITY.YSE) {
                    serviceProduct.setIsBillAbility(ServiceConsts.IS_BILL_ABILITY.NO);
                    this.saveOrUpdata(serviceProduct, true, user);
                    sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(), SysOperLogConst.OperType.Update, "服务商开票能力设为：无，站点都不支持开票");
                }
            }
        }
    }

    @Override
    public List<ServiceProductDto> getServiceProductApply(Long serviceUserId, Integer serviceType, LoginInfo baseUser) {
        List<ServiceProductDto> serviceProductApply = serviceProductMapper.getServiceProductApply(serviceUserId, serviceType, SysStaticDataEnum.INVITE_AUTH_STATE.WAIT, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES, baseUser.getTenantId());
        return serviceProductApply;
    }

    @Override
    public List<ServiceProduct> getProductByServiceUserId(Long serviceUserId) {
        LambdaQueryWrapper<ServiceProduct> lambdaQueryWrapper = new QueryWrapper<ServiceProduct>().lambda();
        lambdaQueryWrapper.eq(ServiceProduct::getServiceUserId, serviceUserId);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public void loseServiceProduct(Long serviceUserId, Integer serviceType, LoginInfo user) {


        List<ServiceProduct> serviceProducts = getServiceProducts(serviceUserId);
        if (serviceProducts != null && serviceProducts.size() > 0) {
            for (ServiceProduct serviceProduct : serviceProducts) {
                serviceProduct.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                this.saveOrUpdate(serviceProduct, true, user);
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(), SysOperLogConst.OperType.Update, "平台停用了服务商");
            }
        }
        //找到该租户与该服务商合作站点，进行失效
        List<TenantProductRel> list = tenantProductRelService.getServiceProductList(serviceUserId, serviceType, null);
        if (list != null && list.size() > 0) {
            for (TenantProductRel tenantProductRel : list) {
                if (tenantProductRel != null) {
                    tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                    tenantProductRelService.saveOrUpdate(tenantProductRel, true, user);
                    sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getId(), SysOperLogConst.OperType.Update, "平台停用了服务商");
                }
            }
            if (serviceType == OIL) {
                //失效oil卡
                oilCardManagementService.loseOilCard(serviceUserId, user);
            } else if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC) {
                //失效etc
                etcMaintainService.loseEtc(serviceUserId, user);
            }
        }
    }

    @Override
    public List<ServiceProduct> getServiceProducts(Long serviceUserId) {
        LambdaQueryWrapper<ServiceProduct> lambda = new QueryWrapper<ServiceProduct>().lambda();
        lambda.eq(ServiceProduct::getServiceUserId, serviceUserId)
                .eq(ServiceProduct::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        return this.list(lambda);
    }

    @Override
    public void saveOrUpdate(ServiceProduct serviceProduct, Boolean isUpdate, LoginInfo user) {
        if (isUpdate) {
            if(serviceProduct.getProductPicId()==null){
                serviceProduct.setProductPicId(-1L);
            }
            serviceProduct.setUpdateTime(LocalDateTime.now());
            serviceProduct.setOpId(user.getId());
            this.updateById(serviceProduct);
        } else {
            serviceProduct.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            serviceProduct.setCreateTime(LocalDateTime.now());
            this.save(serviceProduct);
        }
    }

    @Override
    public Page<ServiceProductInfoVo> queryServiceProductList(Integer pageNum, Integer pageSize, ServiceProductInfoDto serviceProductInfoDto) {
        Page<ServiceProductInfoVo> page = new Page<>(pageNum, pageSize);
        Page<ServiceProductInfoVo> serviceProductInfoVoPage = serviceProductMapper.queryObmsProduct(page, serviceProductInfoDto, PT_TENANT_ID);
        List<ServiceProductInfoVo> records = serviceProductInfoVoPage.getRecords();
        List<ServiceProductInfoVo> serviceProductVo = serviceProductInfoVoTransfer.getServiceProductVo(records);
        serviceProductInfoVoPage.setRecords(serviceProductVo);
        return serviceProductInfoVoPage;
    }

    @Override
    public ServiceDetailVo seeProduct(Long productId) {
        ServiceDetailVo serviceDetailVo = new ServiceDetailVo();
        ProductDetailDto productDetailOut = new ProductDetailDto();
        LambdaQueryWrapper<ServiceProduct> lambda = new QueryWrapper<ServiceProduct>().lambda();
        lambda.eq(ServiceProduct::getId, productId);
        ServiceProduct serviceProduct = this.getOne(lambda);
        if (serviceProduct == null) {
            throw new BusinessException("站点信息有误！");
        }

        UserDataInfo userDataInfo = userDataInfoService.getById(serviceProduct.getServiceUserId());
        BeanUtils.copyProperties(serviceProduct, productDetailOut);
        BeanUtils.copyProperties(userDataInfo, productDetailOut);

        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceProduct.getServiceUserId());
        productDetailOut.setServiceName(serviceInfo.getServiceName());
        productDetailOut.setServiceUserId(serviceInfo.getServiceUserId());
        productDetailOut.setServiceType(serviceInfo.getServiceType());

        productDetailOut.setCooperationNum(tenantProductRelService.cooperationNum(productId));

        ServiceProductVer serviceProductVer = serviceProductVerService.getServiceProductVer(productId);
        productDetailOut.setLoginAcct(userDataInfo.getMobilePhone());
        TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(PT_TENANT_ID, productId);
        if (tenantProductRel != null) {
            BeanUtils.copyProperties(productDetailOut, tenantProductRel);
            productDetailOut.setState(serviceProduct.getState());
        }
        if (productDetailOut.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC) {
            productDetailOut.setServiceProductEtc(serviceProductEtcService.getEtcCardProductInfo(productDetailOut.getProductId(), false));
        }

        if (serviceProduct.getIsAuth() != null && serviceProduct.getIsAuth() == SysStaticDataEnum.IS_AUTH.IS_AUTH1) {
            ProductDetailDto productDetailOutVer = new ProductDetailDto();
            BeanUtils.copyProperties(userDataInfo, productDetailOutVer);
            productDetailOutVer.setServiceName(serviceInfo.getServiceName());
            productDetailOutVer.setServiceUserId(serviceInfo.getServiceUserId());
            productDetailOutVer.setServiceType(serviceInfo.getServiceType());
            BeanUtils.copyProperties(serviceProductVer, productDetailOutVer);
            productDetailOutVer.setLoginAcct(userDataInfo.getMobilePhone());
            if (tenantProductRel != null) {
                TenantProductRelVer tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
                BeanUtils.copyProperties(tenantProductRelVer, productDetailOutVer);
            }
            if (productDetailOut.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC) {
                productDetailOutVer.setServiceProductEtc(serviceProductEtcService.getEtcCardProductInfo(productDetailOut.getProductId(), true));
            }
            ProductDetailDto productDetailDto = productDetailTransfer.toProductDetailDto(productDetailOutVer);
            productDetailDto.setProductId(productId);
            serviceDetailVo.setProductDetailOutVer(productDetailDto);
        }
        ProductDetailDto productDetailDto = productDetailTransfer.toProductDetailDto(productDetailOut);
        productDetailDto.setProductId(productId);
        serviceDetailVo.setProductDetailDto(productDetailDto);
        return serviceDetailVo;
    }

    @Override
    public Page<CooperationTenantDto> queryCooperationProduct(Integer pageNum, Integer pageSize, CooperationProductDto cooperationProductDto) {
        ServiceProduct serviceProduct = this.getById(cooperationProductDto.getProductId());
        if (serviceProduct == null) {
            throw new BusinessException("未找到站点信息！");
        }
        Page<CooperationTenantDto> page = new Page<>(pageNum, pageSize);
        Page<CooperationTenantDto> dtoPage = serviceProductMapper.queryCooperationTenant(page, cooperationProductDto,
                SysStaticDataEnum.AUTH_STATE.AUTH_STATE2,
                SysStaticDataEnum.PT_TENANT_ID);
        List<CooperationTenantDto> list = dtoPage.getRecords();
        if (list != null && list.size() > 0) {
            List<CooperationTenantDto> cooperationTenantDtos = cooperationTenantTransfer.toCooperationTenantDto(list);
            page.setRecords(cooperationTenantDtos);
        }
        return page;
    }

    @Override
    @Transactional
    public ResponseResult auditShareProduct(Long productId, Integer authState, String authRemark, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        ;
//        if (user.getTenantId().longValue() != SysStaticDataEnum.PT_TENANT_ID) {
//            throw new BusinessException("不是运营后台不能操作！");
//        }


        if (productId < 0) {
            log.error("请选择站点审核！");
            return ResponseResult.failure("请选择站点审核！");
        }
        if (StringUtils.isBlank(authRemark)) {
//            throw new BusinessException("审核不通过，请填写审核原因！");
            authRemark = "";
        }

        ServiceProduct serviceProduct = this.getById(productId);
        Long id = null;
        if (serviceProduct.getId() != null) {
            id = serviceProduct.getId();
        }
        if (serviceProduct == null) {
            log.error("站点信息有误！");
            return ResponseResult.failure("站点信息有误！");
        }
        if (serviceProduct.getIsAuth() == IS_AUTH0) {
            log.error("该站点无需审核！");
            return ResponseResult.failure("该站点无需审核！");
        }

        TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(PT_TENANT_ID, productId);
        boolean isDel = false;
        int isShare = ServiceConsts.IS_SHARE.NO;
        //审核成功
        if (authState == SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {

            ServiceProductVer serviceProductVer = serviceProductVerService.getServiceProductVer(productId);
            isShare = serviceProductVer.getIsShare();
            if (serviceProductVer.getIsShare() == ServiceConsts.IS_SHARE.NO) {
                if (tenantProductRel != null) {

                    tenantProductRelService.remove(tenantProductRel.getId());
                }
            }
            //服务商将站点共享
            else if (serviceProductVer.getIsShare() == ServiceConsts.IS_SHARE.YES && serviceProduct.getBusinessType() == 1) {
                /***
                 * 服务商申请将油站设置为共享时，由平台管理人员在运营管理后台进行审核：
                 若审核不通过，则油站的共享申请被驳回，油站共享失败；
                 若审核通过，则需要检测：
                 若该油站没有与任何车队有合作关系，则在审核通过时直接将该油站转为共享油站，
                 同时在油站的名称后面添加“平台油站”字眼，且该字眼不能删除，服务商或者平台管理员只能修改名字，不能修改系统自动添加的字眼。
                 若该油站已经和车队有合作（成为车队的自有油站），则在审核通过时，
                 系统自动复制油站生成一个全新的油站，全新油站的基础信息都原油站一样，
                 油站的名称添加“平台油站”字眼，只是没有合作车队，只是作为共享油站。
                 原来的油站还是保留审核通过前的信息（未共享），新油站与原油站需要建立关联关系。
                 */

                int cooperationNum = tenantProductRelService.cooperationNum(productId);
                //该油站已经和车队有合作（成为车队的自有油站
                if (cooperationNum > 0) {
                    ServiceProduct serviceProductNew = new ServiceProduct();
                    ServiceProductVer serviceProductVerNew = new ServiceProductVer();
                    BeanUtils.copyProperties(serviceProductVer, serviceProductNew);
                    serviceProductNew.setId(null);
                    serviceProductNew.setProductName(getShareName(serviceProductVer.getProductName()));
                    serviceProductNew.setReServiceId(serviceProductVer.getProductId());
                    serviceProductNew.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
                    serviceProductNew.setIsAuth(IS_AUTH0);
                    serviceProductNew.setAuthUserId(user.getId());
                    serviceProductNew.setAuthDate(LocalDateTimeUtil.presentTime());
                    this.saveOrUpdate(serviceProductNew, false, user);
                    BeanUtils.copyProperties(serviceProductNew, serviceProductVerNew);
                    serviceProductVerService.save(serviceProductVerNew);
                    sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, productId, SysOperLogConst.OperType.Audit, "审核通过:" + authRemark, ServiceConsts.SERVICE_TENANT_ID);
                    serviceProductVer.setIsReService(1);
                    serviceProductVer.setIsShare(2);
                    serviceProductVerService.update(serviceProductVer);

                    TenantProductRelVer tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
                    tenantProductRelVer.setProductId(serviceProductNew.getId());
                    BeanUtils.copyProperties(tenantProductRelVer, tenantProductRel);


                } else {
                    //该油站没有与任何车队有合作关系
                    //同时在油站的名称后面添加“平台油站”字眼，且该字眼不能删除，服务商或者平台管理员只能修改名字，不能修改系统自动添加的字眼
                    serviceProductVer.setProductName(getShareName(serviceProductVer.getProductName()));
                    serviceProductVer.setReServiceId(serviceProductVer.getProductId());
                    serviceProductVer.setIsReService(1);
                    serviceProductVerService.update(serviceProductVer);

                    TenantProductRelVer tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
                    BeanUtils.copyProperties(tenantProductRelVer, tenantProductRel);
                }
            } else if (tenantProductRel != null) {

                TenantProductRelVer tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
                BeanUtils.copyProperties(tenantProductRelVer, tenantProductRel);
            }

            BeanUtils.copyProperties(serviceProductVer, serviceProduct);
            String msg = "";
            if (StringUtils.isNotBlank(authRemark)) {
                msg = "，原因：" + authRemark;
            }
            serviceProduct.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
            if (tenantProductRel != null) {
                tenantProductRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
            }
         //   sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, productId, SysOperLogConst.OperType.Audit, "审核通过" + msg);
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, productId, SysOperLogConst.OperType.Audit, "审核通过" + msg, ServiceConsts.SERVICE_TENANT_ID);
            serviceProduct.setAuthRemark("");
        } else if (authState == SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_FAIL) {
            //审核失败
            serviceProduct.setAuthRemark(authRemark);
            if (null != tenantProductRel && serviceProduct.getIsShare() == SysStaticDataEnum.IS_SHARE.NO) {

                ServiceProductVer serviceProductVer = serviceProductVerService.getServiceProductVer(productId);
                if (serviceProductVer.getIsShare() == SysStaticDataEnum.IS_SHARE.YES) {
                    tenantProductRelService.remove(tenantProductRel.getId());
                    isDel = true;
                }
            }
            if (null != tenantProductRel && tenantProductRel.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                tenantProductRelService.remove(tenantProductRel.getId());
                serviceProduct.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
                isDel = true;
            }
       //     sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, productId, SysOperLogConst.OperType.Audit, "审核不通过，原因：" + authRemark);
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, productId, SysOperLogConst.OperType.Audit, "审核不通过，原因：" + authRemark, ServiceConsts.SERVICE_TENANT_ID);

        } else {
            log.error("请选择审核类型！");
            return ResponseResult.failure("请选择审核类型！");
        }
        serviceProduct.setIsAuth(IS_AUTH0);
        serviceProduct.setAuthUserId(user.getId());
        serviceProduct.setAuthDate(LocalDateTimeUtil.presentTime());

        if (!isDel && isShare == ServiceConsts.IS_SHARE.YES && tenantProductRel != null) {
            tenantProductRel.setIsAuth(IS_AUTH0);
//            tenantProductRel.setAuthState(authState == SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_FAIL ? SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH : authState);
            tenantProductRel.setAuthReason(authRemark);
            tenantProductRel.setAuthDate(LocalDateTimeUtil.presentTime());
            tenantProductRel.setAuthManId(user.getId());
            tenantProductRelService.saveOrUpdate(tenantProductRel, true, user);
        }
        //服务商银行卡绑定校验
        if (user.getTenantId() != PT_TENANT_ID) {
            ResponseResult responseResult = this.checkServiceInfoBindBank(serviceProduct.getServiceUserId(), serviceProduct.getIsShare(), serviceProduct.getIsBillAbility());
            if (responseResult.getCode() != 20000) {
                throw new BusinessException(responseResult.getMessage());
            }
        }
        if (id != null) {
            serviceProduct.setId(id);
        }
        this.saveOrUpdate(serviceProduct, true, user);

        /**
         * 审核通过，同步ETC站点信息
         */
        if (authState == SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {

            ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceProduct.getServiceUserId());
            if (serviceInfo != null && serviceInfo.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC) {

                ServiceProductEtcVer etcInfoVer = serviceProductEtcVerService.getServiceProductEtcVer(productId);
                if (etcInfoVer != null) {
                    ServiceProductEtc serviceProductEtc = new ServiceProductEtc();
                    BeanUtils.copyProperties(etcInfoVer, serviceProductEtc);
                    serviceProductEtcService.saveOrUpdate(serviceProductEtc);
                }
            }
        }
        return ResponseResult.success("成功");
    }

    @Override
    public ResponseResult sure(Long productId, Boolean pass, String remark, String accessToken) {
        int type = pass ? 1 : 2;
        LoginInfo user = loginUtils.get(accessToken);
//        if(user.getTenantId().longValue() != SysStaticDataEnum.PT_TENANT_ID){
//            return ResponseResult.failure("不是运营后台不能操作！");
//        }

        ServiceProduct serviceProduct = serviceProductService.getById(productId);

        TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(PT_TENANT_ID, productId);
        if (serviceProduct.getState() != null) {
            if (type == 1) {
                if (serviceProduct.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_YES) {
                    throw new BusinessException("该站点已经在用，无需重复启用！");
                }
            } else if (type == 2 && serviceProduct.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                throw new BusinessException("该站点已经停用，无需重复停用！");
            }
        }
        if (type == 1) {
            serviceProduct.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            if (StringUtils.isNotBlank(remark)) {
                remark = "，原因：" + remark;
            }
            if (tenantProductRel != null) {
                tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                tenantProductRelService.saveOrUpdate(tenantProductRel, true, user);
            }

            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
//                    SysOperLogConst.OperType.Update, "平台把站点设为启用" + remark);
                    SysOperLogConst.OperType.Update, "站点设为启用" + remark);
        } else if (type == 2) {
            serviceProduct.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
            tenantProductRelService.loseCooperationTenant(productId, user);

            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
//                    SysOperLogConst.OperType.Update, "平台把站点设为停用，原因：" + remark);
                    SysOperLogConst.OperType.Update, "站点设为停用，原因：" + remark);
        }
        this.saveOrUpdate(serviceProduct, true, user);
        return ResponseResult.success("成功");
    }

    @Override
    public TenantProductOut getTenantProductHisOut(Long relId, Long productId) throws Exception {
        ServiceProduct serviceProduct = serviceProductService.get(productId);
        TenantProductRelVer tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(relId);
        TenantProductOut tenantProductOut = new TenantProductOut();
        if (serviceProduct != null) {
            BeanUtils.copyProperties(serviceProduct, tenantProductOut);
        }
        if (tenantProductRelVer != null) {
            BeanUtils.copyProperties(tenantProductRelVer, tenantProductOut);
        }
        tenantProductOut = tenantProductRelTransfer.ok(tenantProductOut);
        return tenantProductOut;
    }

    /**
     * 校验服务商是否绑定银行卡
     *
     * @param serviceUserId
     * @param isShare
     * @throws Exception
     */
    @Override
    public ResponseResult checkServiceInfoBindBank(Long serviceUserId, Integer isShare, Integer isBill) {
        if (serviceUserId == null || serviceUserId <= 0) {
            log.error("服务商信息不存在，请联系客服！");
            throw new BusinessException("服务商信息不存在，请联系客服！");
        }
        //服务商银行卡绑定限制
        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceUserId);
        if (serviceInfo == null) {
            log.error("服务商信息有误！");
            throw new BusinessException("服务商信息有误！");
        }

        //判断是否绑定对公账户银行卡
//        if (isBill != null && isBill == ServiceConsts.IS_BILL_ABILITY.YSE) {//支持开票
//            if (!accountBankUserTypeRelService.isUserTypeBindCard(serviceUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER)) {
//                log.error("请先将对公账户绑定银行卡！");
//                throw new BusinessException("请先将对公账户绑定银行卡！");
//            }
//        } else if (isBill != null && isBill == ServiceConsts.IS_BILL_ABILITY.NO) {//不支持开票
//            //判断是否绑定对私账户银行卡
//            if (!accountBankUserTypeRelService.isUserTypeBindCard(serviceUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.SERVICE_USER)) {
//                log.error("请先将对私账户绑定银行卡！");
//                throw new BusinessException("请先将对私账户绑定银行卡!");
//            }
//        }
        return ResponseResult.success();
    }


    private String getShareName(String name) {
        if (StringUtils.isNotBlank(name) && name.endsWith("(平台油站)")) {
            return name;
        } else {
            return name + "(平台油站)";
        }
    }

//    @Override
//    public String saveProduct(ProductSaveDto productSaveIn,String accessToken) {
//        LoginInfo baseUser = loginUtils.get(accessToken);
//        if(baseUser.getTenantId().longValue() != SysStaticDataEnum.PT_TENANT_ID){
//            throw new BusinessException("不是运营后台不能操作！");
//        }
//        productSaveIn.setIsFleet(ServiceConsts.isFleet.OBMS);
//        productSaveIn.setIsFleet(ServiceConsts.isFleet.OBMS);
//        if(productSaveIn.getIsShare() == null) {
//            productSaveIn.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
//        }
//        Long productId = productSaveIn.getProductId();
//        boolean isUpdate = productId != null && productId > 0 ? true : false;
//
//        if (productSaveIn.getServiceType() == null || productSaveIn.getServiceType() < 0) {
//            throw new BusinessException("服务商类型不能为空，请刷新页面！");
//        }
//        if (productSaveIn.getIsFleet() != ServiceConsts.isFleet.OBMS_SHARE) {
//            if (productSaveIn.getServiceUserId() == null || productSaveIn.getServiceUserId() < 0) {
//                throw new BusinessException("服务商不能为空，请刷新页面！");
//            }
//            if(productSaveIn.getServiceType() == OIL || productSaveIn.getServiceType() == REPAIR ){
//                if (StringUtils.isBlank(productSaveIn.getProductName())) {
//                    throw new BusinessException("站点名称不能为空，请填写！");
//                }
//                if (productSaveIn.getProvinceId() == null || productSaveIn.getProvinceId() < 0) {
//                    throw new BusinessException("站点省份不能为空，请选择！");
//                }
//                if (productSaveIn.getCityId() == null || productSaveIn.getCityId() < 0) {
//                    throw new BusinessException("站点城市不能为空，请选择！");
//                }
//                if (StringUtils.isBlank(productSaveIn.getEand()) || StringUtils.isBlank(productSaveIn.getNand())) {
//                    throw new BusinessException("详细地址不能为空，请选择！");
//                }
//
//                if(productSaveIn.getServiceType() == OIL && (null == productSaveIn.getOilCardType() || productSaveIn.getOilCardType() < 0)){
//                    throw new BusinessException("请选择油站类型！");
//                }
//
//                if(null == productSaveIn.getLocationType() || productSaveIn.getLocationType() < 0){
//                    throw new BusinessException("请选择所属位置！");
//                }
//            }
//        }
//        if (productSaveIn.getIsFleet() != ServiceConsts.isFleet.TENANT) {
//            if (productSaveIn.getServiceType() != SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC&&(productSaveIn.getIsBillAbility() == null || productSaveIn.getIsBillAbility() < 0)) {
////                throw new BusinessException("支持开票不能为空，请选择！");
//                productSaveIn.setIsBillAbility(1);
//            }
//            if (productSaveIn.getIsShare() == null || productSaveIn.getIsShare() < 0) {
//                throw new BusinessException("是否共享不能为空，请选择！");
//            }
//            if (productSaveIn.getServiceType() == OIL
//                    && productSaveIn.getIsShare() == ServiceConsts.IS_SHARE.YES) {
//                if (StringUtils.isBlank(productSaveIn.getFloatBalanceBill()) && productSaveIn.getFixedBalanceBill() == null) {
//                    throw new BusinessException("结算单价不能为空，请选填！");
//                }
//                if (StringUtils.isNotBlank(productSaveIn.getFloatBalanceBill()) && StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())) {
//                    throw new BusinessException("结算单价只能选填一个，请重新填写！");
//                }
//                if (StringUtils.isBlank(productSaveIn.getServiceChargeBill())) {
//                    throw new BusinessException("结算单价平台服务费不能为空，请填写！");
//                }
//            }
//        }else {
//            productSaveIn.setIsShare(SysStaticDataEnum.IS_SHARE.NO);
//        }
//
//        TenantProductRel tenantProductRel = null;
//        if (isUpdate) {
//            if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.SERVICE && (productSaveIn.getProductState() == null
//                    || productSaveIn.getProductState() < 0)) {
//                throw new BusinessException("站点状态不能为空，请选择！");
//            }
//            tenantProductRelService
//            tenantProductRel = this.update(productSaveIn);
//        } else {
//            tenantProductRel = this.save(productSaveIn);
//        }
//        if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.TENANT && (productSaveIn.getIsRegistered() == null
//                || productSaveIn.getIsRegistered() < 0)) {
//            //启动审核流程
//            Map inMap = new HashMap();
//            inMap.put("svName", "serviceProductTF");
//            IAuditOutTF auditOutTF = (IAuditOutTF) SysContexts.getBean("auditOutTF");
//            boolean bool = auditOutTF.startProcess(AuditConsts.AUDIT_CODE.SERVICE_PRODUCT,tenantProductRel.getRelId(),
//                    SysOperLogConst.BusiCode.TenantProductRel, inMap);
//            if (!bool) {
//                throw new BusinessException("数据异常，请联系客服！");
//            }
//        }
//        return isUpdate ? "YM" : "Y";
    //   }


    /**
     * 服务商站点修改
     *
     * @param productSaveIn
     * @return
     * @throws Exception
     */
    public TenantProductRel update(ProductSaveDto productSaveIn, LoginInfo baseUser) {
        ServiceProduct serviceProduct = this.getById(productSaveIn.getProductId());
        //校验服务商有效性
        serviceInfoService.checkServiceInfo(productSaveIn, serviceProduct.getServiceUserId(), true);
        //校验租户服务商关系有效性
        if (baseUser.getTenantId() != null) {
            tenantServiceRelService.checkTenantService(baseUser.getTenantId(), productSaveIn, true);
        }
        //校验站点有效性
        this.checkProduct(productSaveIn, serviceProduct);
        if (serviceProduct.getProductType() != null && serviceProduct.getProductType() == SysStaticDataEnum.PRODUCT_TYPE.ZHAOYOU) {
            productSaveIn.setStationId(serviceProduct.getStationId());
            productSaveIn.setProductType(serviceProduct.getProductType());
        }
        /*//不需要支持开票、把开票的信息清空
        if (checkEnumIsNotNull(productSaveIn.getIsBillAbility(), ServiceConsts.IS_BILL_ABILITY.NO)) {
            productSaveIn.setFixedBalanceBill(null);
            productSaveIn.setFloatBalanceBill(null);
            productSaveIn.setServiceChargeBill(null);
            productSaveIn.setFloatServiceChargeBill(null);
        }*/
        //服务商操作
        if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.SERVICE ) {
            Integer isBillAbility = serviceProduct.getIsBillAbility();
            BeanUtils.copyProperties(productSaveIn, serviceProduct);
            //共享的时候，需要审核
            if (productSaveIn.getIsShare() == SysStaticDataEnum.IS_SHARE.YES) {
                if (productSaveIn.getIsProductAudit() == null || productSaveIn.getIsProductAudit()) {
                    serviceProduct.setIsBillAbility(isBillAbility);
                    serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                } else {
                    serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
                }
                //保存历史版本
                serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, true, baseUser);
            }
            serviceProduct.setState(productSaveIn.getProductState());
            saveOrUpdata(serviceProduct, true, baseUser);

            //改为无效的时候，需要把所有合作车队关系设为无效

            if (CommonUtil.checkEnumIsNotNull(productSaveIn.getProductState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                tenantProductRelService.loseCooperationTenant(serviceProduct.getId(), baseUser);
            }
            sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
                    SysOperLogConst.OperType.Update, "服务商修改站点！");


        } else if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.OBMS_SHARE) {//运营后台共享操作

//            serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
//            serviceProduct.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
//            saveOrUpdata(serviceProduct, true, baseUser);
//            serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, false, baseUser);
//            sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
//                    SysOperLogConst.OperType.Update, "平台把站点设为共享");
        } else if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.OBMS) {//运营后台站点管理
            serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, true, baseUser);

            serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            saveOrUpdata(serviceProduct, true, baseUser);
            sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
//                    SysOperLogConst.OperType.Update, "操作员id：" + baseUser.getId() + "，修改了站点");
                    SysOperLogConst.OperType.Update, "服务商修改站点");
        }

        //修改站点与车队关系
        TenantProductRel tenantProductRel = tenantProductRelService.updateProductRel(baseUser, productSaveIn, baseUser.getTenantId(),
                serviceProduct.getId(), serviceProduct.getProductName(), serviceProduct.getServiceUserId(),
                productSaveIn.getIsFleet());
        //设置地理位置
        readisUtil.redisProductAddress(serviceProduct);
        return tenantProductRel;
    }

    /**
     * 设置站点地理位置
     *
     * @param serviceProduct
     */
    public void redisProductAddress(ServiceProduct serviceProduct) {
//        Map<String, String> inMap = new HashMap<String, String>();
//        if (StringUtils.isNotEmpty(serviceProduct.getEand()) && StringUtils.isNotEmpty(serviceProduct.getNand())) {
//            //经纬度不能为空
//            inMap.put("oilId", serviceProduct.getProductId() + "");
//            inMap.put("oilName", serviceProduct.getProductName());
//            inMap.put("eand", serviceProduct.getEand());
//            inMap.put("nand", serviceProduct.getNand());
//            String address = CommonUtil.getProvinceNameById(serviceProduct.getProvinceId());
//            if (serviceProduct.getCityId() != null) {
//                address += CommonUtil.getCityNameById(serviceProduct.getCityId());
//            }
//            if (serviceProduct.getCountyId() != null) {
//                address += CommonUtil.getCountryNameById(serviceProduct.getCountyId());
//            }
//            address += serviceProduct.getAddress();
//            inMap.put("address", address);
//            inMap.put("oilbillId", serviceProduct.getServiceCall());
//            inMap.put("introduce", serviceProduct.getIntroduce());
//            inMap.put("businessType", serviceProduct.getBusinessType() + "");
//        }
//        Map<String, String> outMap = new HashMap<String, String>();
//        outMap.put(serviceProduct.getProductId() + "", JsonHelper.json(inMap));
//        RemoteCacheUtil.hmset(EnumConsts.RemoteCache.OIL_LOCATION_INFO, outMap);
    }

    @Override
    public void loseTenantProduct(LoginInfo user, Long userId, Integer serviceType, Long tenantId, Boolean isAuthPass) {
        //找到该租户与该服务商合作站点，进行失效
        List<TenantProductRel> list = tenantProductRelService.getServiceProductList(userId, serviceType, tenantId);
        if (list != null && list.size() > 0) {
            for (TenantProductRel tenantProductRel : list) {
                if (tenantProductRel != null) {
                    if (isAuthPass) {
                        tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                        tenantProductRelService.saveOrUpdate(tenantProductRel, true, user);
                        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getId(),
                                SysOperLogConst.OperType.Audit, "审核通过");
                    } else {
                        TenantProductRelVer tenantProductRelVer = new TenantProductRelVer();
                        BeanUtils.copyProperties(tenantProductRel, tenantProductRelVer);
                        tenantProductRelVer.setRelId(tenantProductRel.getId());
                        tenantProductRelVer.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                        tenantProductRelVer.setId(null);
                        tenantProductRelVerService.saveProductHis(tenantProductRelVer, user);
                        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getId(),
                                SysOperLogConst.OperType.Update, "无效");
                    }
                }
            }
        }
    }

    @Override
    public ServiceProduct getServiceProduct(String cardNum, int cardType, String custTime) {
        return baseMapper.getServiceProduct(cardNum, cardType, custTime);
    }


    @Override
    public List<ServiceProduct> getServiceProductList(Long userId, Long tenantId) {
        return baseMapper.getServiceProductList(userId, tenantId);
    }

    @Override
    public Boolean saveOrUpdateProduct(ProductSaveDto productSaveIn, String accessToken) {
        //腾讯转百度地图
        Double[] doubles = map_tx2bd(Double.valueOf(productSaveIn.getNand()), Double.valueOf(productSaveIn.getEand()));
        productSaveIn.setNand(String.valueOf(doubles[0]));
        productSaveIn.setEand(String.valueOf(doubles[1]));
        LoginInfo baseUser = loginUtils.get(accessToken);

        productSaveIn.setServiceUserId(baseUser.getUserInfoId());
        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(baseUser.getUserInfoId());
        if (serviceInfo == null) {
            throw new BusinessException("服务商信息有误！");
        }
        productSaveIn.setServiceType(serviceInfo.getServiceType());
       // productSaveIn.setIsFleet(ServiceConsts.isFleet.SERVICE);
        //服务商银行卡绑定校验
        this.checkServiceInfoBindBank(baseUser.getUserInfoId(), productSaveIn.getIsShare(), productSaveIn.getIsBillAbility());
        this.saveProduct(productSaveIn, accessToken);
        return true;
    }

    @Override
    public List<ServiceProductVxVoOK> queryCooperationProductVx(List<String> state, Long productId,
                                                              String tenantName) {
        //    PageHelper.startPage(pageNum, pageSize);
       // Page<ServiceProductVxVoOK> sysOrganizePage = new Page<>(pageNum, pageSize);
        List<ServiceProductVxVoOK> records = serviceProductMapper.queryCooperationProductVx(state,
                productId, tenantName);
       // List<ServiceProductVxVoOK> records = serviceProductVxVoPage.getRecords();
        for (ServiceProductVxVoOK productVxVo : records) {
            String floatBalance = productVxVo.getFloatBalance();
            Long fixedBalance = productVxVo.getFixedBalance();
            Long oilPrice = productVxVo.getOilPrice();
            Integer isBillAbility = productVxVo.getIsBillAbility();
            Long curOilPrice = 0L;
            if (isBillAbility == SysStaticDataEnum.IS_BILL_ABILITY.YSE) {
                if (StringUtils.isNotBlank(productVxVo.getFloatBalanceBill())) {
                    productVxVo.setFloatBalance(productVxVo.getFloatBalanceBill());
                }
                if (productVxVo.getFixedBalanceBill() != null) {
                    productVxVo.setFixedBalance(productVxVo.getFixedBalanceBill());
                }
                curOilPrice = calculationPrice(productVxVo.getFloatBalanceBill(), productVxVo.getFixedBalanceBill()
                        , oilPrice);
            } else {
                curOilPrice = calculationPrice(floatBalance, fixedBalance, oilPrice);
            }
            Integer balanceType = productVxVo.getBalanceType();
            Long quotaAmt = productVxVo.getQuotaAmt();
            Long useQuotaAmt = productVxVo.getUseQuotaAmt();
            String createDate = productVxVo.getCreateDate();

            try {
                Date date = DateUtil.formatStringToDate(createDate, "yyyy-MM-dd");
                productVxVo.setCreateDate(DateUtil.formatDate(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            productVxVo.setCurOilPrice(curOilPrice);
            productVxVo.setBalanceTypeName(balanceType == 2 ? "月结" : balanceType == 1 ? "账期" : "无账期");
            if (quotaAmt != null && quotaAmt > -1) {
                productVxVo.setQuotaAmt(quotaAmt);
            } else {
                productVxVo.setQuotaAmt(null);
            }
            if (useQuotaAmt != null && useQuotaAmt > -1) {
                productVxVo.setUseQuotaAmt(useQuotaAmt);
            } else {
                productVxVo.setUseQuotaAmt(0L);
            }
            if(productVxVo.getCooperationState() != null){
                productVxVo.setCooperationStateName(readisUtil.getSysStaticData(EnumConsts.SysStaticData.COOPERATION_STATE,productVxVo.getCooperationState()+"").getCodeName());
            }
            if (productVxVo.getServiceType() != null) {
                String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, productVxVo.getServiceType().toString()).getCodeName();
                productVxVo.setServiceTypeName(serviceTypeName);
            }
            if(productVxVo.getLocaleBalanceState() != null){
                productVxVo.setLocaleBalanceStateName(productVxVo.getLocaleBalanceState() == 1 ? "现场价" : "");
            }else{
                productVxVo.setLocaleBalanceStateName("");
            }
        }
     //   PageInfo<ServiceProductVxVoOK> pageInfo = new PageInfo<>(records);
       // serviceProductVxVoPage.setRecords(records);
        return records;
    }

    /**
     * 坐标转换，腾讯地图转换成百度地图坐标
     *
     * @param lat 腾讯纬度
     * @param lon 腾讯经度
     * @return 返回结果：经度,纬度
     */
    public static Double[] map_tx2bd(double lat, double lon) {
        double bd_lat;
        double bd_lon;
        double x_pi = 3.14159265358979324;
        double x = lon, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        bd_lon = z * Math.cos(theta) + 0.0065;
        bd_lat = z * Math.sin(theta) + 0.006;
//        System.out.println("bd_lat:"+bd_lat);
//        System.out.println("bd_lon:"+bd_lon);
        Double[] doubles = new Double[]{bd_lat, bd_lon};
        return doubles;
    }

    @Override
    public Page<ServiceProductWxDto> queryWxProduct(Integer pageNum, Integer pageSize,
                                                    List<Integer> authStates, List<Integer> state,
                                                    Integer serviceType, String productName,
                                                    String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<ServiceProductWxDto> page = new Page<>(pageNum, pageSize);
        Page<ServiceProductWxDto> serviceProductWxDtoPage = serviceProductMapper.queryWxProduct(page, authStates, loginInfo.getUserInfoId(), state, serviceType, productName);
        List<ServiceProductWxDto> records = serviceProductWxDtoPage.getRecords();
        if (records != null && records.size() > 0) {
            for (ServiceProductWxDto record : records) {
                if (record.getIsShare() == 1) {
                    record.setIsShareName("共享");
                }
                List<ServiceProductVxVoOK> oks = this.queryCooperationProductVx(null, record.getProductId(), null);
                if(oks != null && oks.size() > 0){
                    record.setDelState(0);
                }else{
                    record.setDelState(1);
                }
            }
            serviceProductWxDtoPage.setRecords(records);
        }
        return serviceProductWxDtoPage;
    }

    @Override
    public Boolean releaseCooperation(Long relId, Long productId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        TenantProductRel tenantProductRel = tenantProductRelService.get(relId);
        if (tenantProductRel == null) {
            throw new BusinessException("合作站点数据有误！");
        }
        if (tenantProductRel.getCooperationState() != null && tenantProductRel.getCooperationState() != ServiceConsts.COOPERATION_STATE.IN_COOPERATION) {
            throw new BusinessException("已经解除了合作，不能重复解除！");
        }
        tenantProductRel.setCooperationState(ServiceConsts.COOPERATION_STATE.RELEASED);
        //车队合作关系设为无效
        tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
        LocalDateTime now = LocalDateTime.now();
        tenantProductRel.setCreateTime(now);
        tenantProductRel.setOpId(user.getId());
        tenantProductRelService.saveOrUpdate(tenantProductRel);

        //保存历史记录
        TenantProductRelVer tenantProductRelVer = new TenantProductRelVer();
        BeanUtils.copyProperties(tenantProductRel, tenantProductRelVer);
        tenantProductRelVer.setUpdateTime(now);
        tenantProductRelVer.setUpdateOpId(user.getUserInfoId());
        tenantProductRelVerService.saveOrUpdate(tenantProductRelVer);
        //记录历史表
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(), SysOperLogConst.OperType.Update, "服务商[" + user.getName() + "]解除了合作");
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(), SysOperLogConst.OperType.Update, "服务商[" + user.getName() + "]解除了合作", tenantProductRel.getTenantId());


        return true;
    }

    @Override
    public ProductDetailVo getProductDetail(Long productId, String accessToken) {
        if (productId < 0) {
            throw new BusinessException("请选择站点！");
        }
        LoginInfo user = loginUtils.get(accessToken);
        ProductDetailVo productDetail = this.getProductDetailData(productId, user);
        if (StringUtils.isNotBlank(productDetail.getNand()) && StringUtils.isNotBlank(productDetail.getEand())) {
            Double[] doubles = map_bd2tx(Double.valueOf(productDetail.getNand()), Double.valueOf(productDetail.getEand()));
            productDetail.setNand(String.valueOf(doubles[0]));
            productDetail.setEand(String.valueOf(doubles[1]));
        }
        return productDetail;
    }


    /**
     * 坐标转换，百度地图坐标转换成腾讯地图坐标
     *
     * @param lat 百度坐标纬度
     * @param lon 百度坐标经度
     * @return 返回结果：纬度,经度
     */
    public static Double[] map_bd2tx(double lat, double lon) {
        double tx_lat;
        double tx_lon;
        double x_pi = 3.14159265358979324;
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        tx_lon = z * Math.cos(theta);
        tx_lat = z * Math.sin(theta);
        Double[] doubles = new Double[]{tx_lat, tx_lon};
        return doubles;
    }

    /**
     * 站点详情-微信
     *
     * @param productId
     * @return
     * @throws Exception
     */
    public ProductDetailVo getProductDetailData(Long productId, LoginInfo user) {
        ServiceProduct serviceProduct = this.getById(productId);
        if (serviceProduct == null) {
            throw new BusinessException("站点信息有误！");
        }
        TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(PT_TENANT_ID, productId);
        if(tenantProductRel == null){
            tenantProductRel = tenantProductRelService.getTenantProductRel(user.getTenantId(), productId);
        }

        ProductDetailVo productDetail = new ProductDetailVo();

        if (serviceProduct.getChildAccountUserId() != null && serviceProduct.getChildAccountUserId() >= 0) {
            UserDataInfo userDataInfo = userDataInfoService.getById(serviceProduct.getChildAccountUserId());
            if (userDataInfo != null) {
                productDetail.setMobilePhone(userDataInfo.getMobilePhone());
                productDetail.setLinkman(userDataInfo.getLinkman());
            }
        }
        productDetail.setProductId(serviceProduct.getId());
        BeanUtils.copyProperties( serviceProduct,productDetail);
        productDetail.setProductState(serviceProduct.getState());
        if (tenantProductRel != null) {
            BeanUtils.copyProperties(tenantProductRel, productDetail,"isShare","serviceCall","productName","introduce","provinceId","cityId","countyId","address");
            if (serviceProduct.getBusinessType() != null && serviceProduct.getBusinessType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                Long oilPrice = oilPriceProvinceService.getOilPrice(serviceProduct.getProvinceId());

                long curOilPrice = calculationPrice(productDetail.getFloatBalance(), productDetail.getFixedBalance(), oilPrice);
                long curOilPriceBill = calculationPrice(productDetail.getFloatBalanceBill(), productDetail.getFixedBalanceBill(), oilPrice);
                productDetail.setCurOilPrice(curOilPrice);
                productDetail.setCurOilPriceBill(curOilPriceBill);
            }
        }

        Integer count = tenantProductRelService.cooperationNum(productId);
        productDetail.setCooperationNum(count == null ? 0 : count);
        productDetail.setAddress(productDetail.getAddress());

        if (null != serviceProduct.getProductPicId() && serviceProduct.getProductPicId() > 0) {
            if (null != serviceProduct) {
                //todo 服务商图片 待处理
                productDetail.setProductPicUrl(serviceProduct.getLogoUrl());
            }
        }

        List<SysOperLog> logs = sysOperLogService.querySysOperLog(SysOperLogConst.BusiCode.ServiceProduct,
                serviceProduct.getId(), false, user.getTenantId(), null, null);
        productDetail.setLogs(logs);
        return productDetail;
    }

    /**
     * 计算费用
     *
     * @param floatBalance
     * @param fixedBalance
     * @param oilPrice
     * @return
     */
    public long calculationPrice(String floatBalance, Long fixedBalance, Long oilPrice) {
        long curOilPrice = 0;
        if (StringUtils.isNotBlank(floatBalance) && CommonUtil.isNumber(floatBalance)) {
            double floatBalanceDouble = (Double.valueOf(floatBalance)) / 100;
            double curOilPriceDouble = floatBalanceDouble * oilPrice;
            curOilPrice = (long) curOilPriceDouble;
        } else if (fixedBalance != null && fixedBalance > 0) {
            curOilPrice = fixedBalance;
        }
        return curOilPrice;
    }

    /**
     * 检验价格是否发生了变动
     *
     * @param floatBalance
     * @param floatBalanceVer
     * @param fixedBalance
     * @param fixedBalanceVer
     * @return
     */
    public boolean checkPriceChange(String floatBalance, String floatBalanceVer, Long fixedBalance, Long fixedBalanceVer) {
        boolean isUpdatePrice = false;
        //检验价格是否变动 - 是的话需要 服务商审核通过
        if (StringUtils.isNotBlank(floatBalance) && StringUtils.isNotBlank(floatBalanceVer)) {
            Double floatBalanceNew = Double.valueOf(floatBalance);
            Double floatBalanceOld = Double.valueOf(floatBalanceVer);
            if (!floatBalanceNew.equals(floatBalanceOld)) {
                isUpdatePrice = true;
            }
        } else if ((StringUtils.isBlank(floatBalance) && StringUtils.isNotBlank(floatBalanceVer))
                || (StringUtils.isNotBlank(floatBalance) && StringUtils.isBlank(floatBalanceVer))) {

            isUpdatePrice = true;
        } else if (CommonUtil.isNotBlankToLong(fixedBalance) && CommonUtil.isNotBlankToLong(fixedBalanceVer)) {

            if (!fixedBalance.equals(fixedBalanceVer)) {
                isUpdatePrice = true;
            }
        } else if ((CommonUtil.isNotBlankToLong(fixedBalance) && (CommonUtil.isBlankToLong(fixedBalanceVer))) ||
                (CommonUtil.isBlankToLong(fixedBalance) && CommonUtil.isNotBlankToLong(fixedBalanceVer))) {
            isUpdatePrice = true;
        }
        return isUpdatePrice;
    }


    @Override
    public ServiceProduct getServiceProduct(Long productId) {
        LambdaQueryWrapper<ServiceProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceProduct::getId, productId);
        ServiceProduct one = this.getOne(queryWrapper);
        return one;
    }

    @Override
    public Map getServiceInfoByUserIdOrChild(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("缺少userId参数");
        }

        Map<String, Object> serviceMap = this.queryChildCompanyName(userId, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        if (serviceMap == null || DataFormat.getLongKey(serviceMap, "userId") <= 0) {
            serviceMap = this.queryChildCompanyName(userId, SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER);
            serviceMap.put("userType", SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER);
        } else {
            serviceMap.put("userType", SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        }

        return serviceMap;
    }

    @Override
    public Map<String, Object> queryChildCompanyName(long userId, int userType) {
        Map<String, Object> map = baseMapper.queryChildCompanyName(userId, userType);
        if (map != null) {
            int serviceType = DataFormat.getIntKey(map, "serviceType");
            if (userType == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER) {
                map.put("serviceTypeName", getSysStaticData(EnumConsts.SysStaticData.SERVICE_BUSI_TYPE, String.valueOf(serviceType)).getCodeName() + "子账号");
            } else if (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
                map.put("serviceTypeName", getSysStaticData(EnumConsts.SysStaticData.SERVICE_BUSI_TYPE, String.valueOf(serviceType)).getCodeName());
            }
            map.put("authStateName", getSysStaticData(EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE, DataFormat.getStringKey(map, "authState")).getCodeName());
        } else {
            map = new HashMap<>();
        }

        return map;
    }

    @Override
    public int countProductNum(String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = userDataInfoService.getById(user.getUserInfoId());
//        if ((userDataInfo.getUserType().intValue() != SysStaticDataEnum.USER_TYPE.SERVICE_USER) &&
//                (userDataInfo.getUserType().intValue() != SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER)) {
//            throw new BusinessException("不是服务商类型，不能查询！");
//        }
        return this.countProductNum(user.getId(), SysStaticDataEnum.USER_TYPE.SERVICE_USER)
                + this.countProductNum(user.getId(), SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER);
    }

    @Override
    public List<WorkbenchDto> getTableManagerSiteCount() {
        return baseMapper.getTableManagerSiteCount();
    }

    /**
     * 查询附近油站信息
     *
     * @param vo 经度、纬度
     * @param accessToken  验签
     * @return 优先显示该司机归属车队和该油站的合作价格，若归属车队没有合作关系，则显示平台给的价格(根据需不需要开票改变)
     * 1、合作车队若按浮动价计算，则显示：折扣*油站归属省份的全国油价
     * 2、合作车队若按固定价计算，则显示：车队配置的规定单价
     * 3、平台若按浮动价计算，显示：平台设置折扣 * 油站归属省份的全国油价 *（1+平台设置手续费比例）
     * 4、平台若按固定价计算，显示：平台设置单价 *（1+平台设置手续费比例）
     */
    @Override
    public ProductNearByVo queryNearbyOil(ServiceProductOutVo vo, String accessToken) {
        ProductNearByVo byVo  = new ProductNearByVo();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //调资金组接口，查看油卡余额（达成）
        com.youming.youche.system.domain.UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(loginInfo.getUserInfoId());
        Long oilBalance = iOrderAccountService.queryOilBalance(loginInfo.getUserInfoId(), loginInfo.getTenantId(),loginInfo.getUserType()==null?userDataInfo.getUserType():loginInfo.getUserType());
//        Map<String, Object> outMap = new HashMap<>();
//        outMap.put("oilBalance", oilBalance);
        byVo.setOilBalance(oilBalance);
        byVo.setOilBalanceDouble(byVo.getOilBalance()==null? null : byVo.getOilBalance()/100.00);
        List<ProductNearByOutDto> outList = this.queryNearby(vo.getLongitude(), vo.getLatitude(), OIL,vo.getCityId(),vo.getLocationType(),vo.getOilCardType(),accessToken);
        if (outList == null || outList.size() == 0) {
           byVo.setOutList(outList);
            return byVo;
        }
        List<Long> oilIds = new ArrayList<>();
        List<OilServiceInDto> oilServiceIns = new ArrayList<>();
        List<ProductNearByOutDto> removeList = new ArrayList<>();
        for (ProductNearByOutDto productNearByOut : outList) {
            //判断油价是否为空或0，是的话剔除调，这种数据问题
            if (CommonUtil.isBlankToLong(productNearByOut.getOilPrice())
                    && (productNearByOut.getLocaleBalanceState() == null || productNearByOut.getLocaleBalanceState() != ServiceConsts.LOCALE_BALANCE_STATE.YES)) {
                removeList.add(productNearByOut);
            } else {
                OilServiceInDto oilServiceIn = new OilServiceInDto();
                oilServiceIn.setServiceId(productNearByOut.getServiceId());
                oilServiceIn.setProductId(productNearByOut.getOilId());
                oilServiceIn.setOilPrice(productNearByOut.getOilPrice());
                oilServiceIns.add(oilServiceIn);
                oilIds.add(productNearByOut.getOilId());
            }
        }
        if (removeList.size() > 0) {
            outList.removeAll(removeList);
        }
        //调订单的接口，查是否指派油站（周嘉）
        Map<String, Long> enMap = iOrderStatementService.queryOrderOilEnRouteUse(loginInfo.getUserInfoId(), oilIds);
        //调资金组接口，查看最低可以加油多少升（达成）
        List<OilServiceOutDto> oilServiceInList = iOperationOilService.queryOilRise(loginInfo.getUserInfoId(), oilServiceIns, loginInfo.getTenantId(),loginInfo.getUserType());
        for (ProductNearByOutDto productNearByOut : outList) {
            productNearByOut.setIsOrder(0);
            if (enMap != null) {
                long orderId = DataFormat.getLongKey(enMap, String.valueOf(productNearByOut.getOilId()));
                if (orderId >= 0) {
                    productNearByOut.setIsOrder(1);
                }
            }
            if (oilServiceInList != null) {
                for (OilServiceOutDto oilServiceOut : oilServiceInList) {
                    if (oilServiceOut.getProductId().equals(productNearByOut.getOilId())) {
                        productNearByOut.setMaxOil(String.valueOf(oilServiceOut.getOilRise()));
                    }
                }
            }
        }
        //排序
        List<ProductNearByOutDto> collect = outList.stream()
                .filter(productNearByOutDto -> productNearByOutDto.getDistanceL()<=70)
                .sorted(Comparator.comparing(ProductNearByOutDto::getDistanceL)).collect(Collectors.toList());
        byVo.setOutList(collect);
        return byVo;
    }
    /**
     * 查询附近站点信息
     *
     * @param longitude
     * @param latitude
     * @param serviceType
     * @return
     * @throws Exception
     */
    @Override
    public List<ProductNearByOutDto> queryNearby(String longitude, String latitude,
                                                 int serviceType, Long cityId, Integer locationType,
                                                 Long oilCardType,String accessToken) {

            LoginInfo loginInfo = loginUtils.get(accessToken);
            String cfgName = "";
            if (serviceType == 1) {
                cfgName = EnumConsts.SysCfg.NEARBY_OIL_POSITION;
            } else if (serviceType == 2) {
                cfgName = EnumConsts.SysCfg.NEARBY_REPAIR_POSITION;
            }
            List<ProductNearByOutDto> outList = new ArrayList<>();
            String position = "";
            try {
//                SysCfg sysCfg = readisUtil.getSysCfg(cfgName, "0");
//                position = sysCfg.getCfgValue();
                position =  getSysCfg(cfgName,"0").getCfgValue();
            } catch (Exception e) {
                position = "50000";
            }
            Double positionDouble = null;
            if (position !=null && StringUtils.isNotEmpty(position)){
                positionDouble = Double.valueOf(position);
            }
            Set<Long> productIds = new HashSet<>();
            List<ProductNearByDto> listObjects = this.getServiceNearBy(loginInfo.getTenantId(), latitude,
                    longitude, positionDouble, serviceType, cityId, locationType, oilCardType);

            if (listObjects != null && listObjects.size() > 0) {
                for (ProductNearByDto objects : listObjects) {
                    ServiceProduct serviceProduct = objects.getServiceProduct().get(0);
                    if (serviceProduct == null) {
                        continue;
                    }
                    TenantProductRel tenantProductRel = objects.getTenantProductRel().get(0);
                    if (tenantProductRel == null) {
                        continue;
                    }
                    ServiceInfo serviceInfo = objects.getServiceInfo().get(0);
                    if (serviceInfo == null) {
                        continue;
                    }
                    ProductNearByOutDto productNearByOut = new ProductNearByOutDto();
                    boolean isShare = false;
                    if ( tenantProductRel.getTenantId()!=null && tenantProductRel.getTenantId().longValue() == PT_TENANT_ID){//是否共享站点
                    }
                    if (serviceType == 1) {
                        OilPriceProvince oilPriceProvince = objects.getOilPriceProvince().get(0);
                        if (oilPriceProvince == null) {
                            continue;
                        }

                        long originalPrice =0L;
                        if ( oilPriceProvince.getOilPrice()!=null){
                            originalPrice = oilPriceProvince.getOilPrice();
                        }
                        //计算油单价
                        long oilPrice = oilPriceProvinceService.calculationOilPrice(tenantProductRel, oilPriceProvince.getOilPrice(), isShare, true);
                        productNearByOut.setOriginalPrice(originalPrice);
                        productNearByOut.setOilPrice(oilPrice);
                    }
                    try {
                        FastDFSHelper client = FastDFSHelper.getInstance();
                        productNearByOut.setLogoId(serviceProduct.getLogoId());
                        productNearByOut.setLogoUrl(serviceProduct.getLogoUrl() != null ? client.getHttpURL(serviceProduct.getLogoUrl()).split("\\?")[0] : null);
                        productNearByOut.setOilName(serviceProduct.getProductName());
                        productNearByOut.setOilId(serviceProduct.getId());
                        productNearByOut.setServiceId(serviceProduct.getServiceUserId());
                        productNearByOut.setIntroduce(serviceProduct.getIntroduce());
                        int productType = serviceProduct.getProductType() == null ? ServiceConsts.PRODUCT_TYPE.EMPTY : serviceProduct.getProductType();
                        productNearByOut.setProductType(productType);
                    }catch ( Exception e ){

                    }
                    Double latitudeDouble = null;
                    Double longitudeDouble = null;
                    Double nandDouble = null;
                    Double eandDouble = null ;
                    if (latitude!=null && StringUtils.isNotEmpty(latitude)){
                        latitudeDouble = Double.valueOf(latitude);
                    }
                    if (longitude!=null &&  StringUtils.isNotEmpty(longitude)){
                        longitudeDouble = Double.valueOf(longitude);
                    }
                    if (serviceProduct.getNand()!=null && StringUtils.isNotEmpty(serviceProduct.getNand())){
                        nandDouble = Double.valueOf(serviceProduct.getNand());
                    }
                    if (serviceProduct.getEand()!=null && StringUtils.isNotEmpty(serviceProduct.getEand())){
                        eandDouble = Double.valueOf(serviceProduct.getEand());
                    }
                    //司机当前位置到油站的距离
                    double distance = GpsUtil.getDistance(latitudeDouble, longitudeDouble, nandDouble,
                            eandDouble);
                    productNearByOut.setDistance(String.valueOf(CommonUtil.getDoubleFormat(distance / 1000, 2)));
                    productNearByOut.setDistanceL(new Double(CommonUtil.getDoubleFormat(distance / 1000, 2)).longValue());
                    String provinceName = null;
//                            CommonUtil.getProvinceNameById(serviceProduct.getProvinceId());
                    String cityName = null;
//                            CommonUtil.getCityNameById(serviceProduct.getCityId());
                    String districtName = null;
//                            serviceProduct.getCountyId() == null ? "" : CommonUtil.getCountryNameById(serviceProduct.getCountyId());
                    // 省
                    if (serviceProduct.getProvinceId() != null && serviceProduct.getProvinceId() > 0) {
                        provinceName = (getSysStaticDataId("SYS_PROVINCE", serviceProduct.getProvinceId() + "").getCodeName());
                    }
                    // 市
                    if (serviceProduct.getCityId() != null && serviceProduct.getCityId() > 0) {
                        cityName = (getSysStaticDataId("SYS_CITY", serviceProduct.getCityId() + "").getCodeName());
                    }
                    // 区、 县
                    if (serviceProduct.getCountyId() != null && serviceProduct.getCountyId() > 0) {
                        districtName = (getSysStaticDataId("SYS_DISTRICT", serviceProduct.getCountyId() + "").getCodeName());


                        productNearByOut.setAddress(provinceName + cityName + districtName + serviceProduct.getAddress());
                        productNearByOut.setServiceCall(serviceProduct.getServiceCall());
                        productNearByOut.setTenantId(tenantProductRel.getTenantId());
                        productNearByOut.setLatitude(serviceProduct.getNand());
                        productNearByOut.setLongitude(serviceProduct.getEand());
                        productNearByOut.setLocaleBalanceState(tenantProductRel.getLocaleBalanceState() == null ? 0 : tenantProductRel.getLocaleBalanceState());
                        productNearByOut.setLocaleBalanceName(productNearByOut.getLocaleBalanceState() == 0 ? "" : "现场价");

                        productNearByOut.setPrice("0");
                        productNearByOut.setService("0");
                        productNearByOut.setQuality("0");
                        outList.add(productNearByOut);
                        productIds.add(serviceProduct.getId());
                    }
                }
                List<Long> productIdsList = new ArrayList<>();
                productIdsList.addAll(productIds);
                Map<String, Map<String, Object>> mark = this.getMark(productIdsList, serviceType);
                Map<Long, List<ProductNearByOutDto>> resultMap = null ;
                try {
                   resultMap = CommonUtil.groupDataByKey(outList, "oilId");
                }catch (Exception e ){

                }
                List<ProductNearByOutDto> remove = new ArrayList<>();
                if (outList != null && outList.size() > 0) {
                    for (ProductNearByOutDto productNearByOut : outList) {
                        if (resultMap.get(productNearByOut.getOilId()).size() > 1 && productNearByOut.getTenantId().longValue() == PT_TENANT_ID) {
                            remove.add(productNearByOut);
                        }
                    }
                    if (remove != null && remove.size() > 0) {
                        outList.removeAll(remove);
                    }
                }
                for (ProductNearByOutDto productNearByOut : outList) {
                    Map<String, Object> map = mark.get(String.valueOf(productNearByOut.getOilId()));
                    if (map != null && map.size() > 0) {
                        String qualityStar = DataFormat.getStringKey(map, "qualityStar");
                        String priceStar = DataFormat.getStringKey(map, "priceStar");
                        String serviceStar = DataFormat.getStringKey(map, "serviceStar");
                        productNearByOut.setQuality(qualityStar);
                        productNearByOut.setPrice(priceStar);
                        productNearByOut.setService(serviceStar);
                        //站点评分为空时 默认满分
                        if (StringUtils.isBlank(productNearByOut.getQuality())) {
                            productNearByOut.setQuality("5");
                        }
                        if (StringUtils.isBlank(productNearByOut.getPrice())) {
                            productNearByOut.setPrice("5");
                        }
                        if (StringUtils.isBlank(productNearByOut.getService())) {
                            productNearByOut.setService("5");
                        }
                    }
                }

            }
             return outList;
        }

    /**
     * 通过这里位置 查油站
     * @param tenantId
     * @param sourceNand
     * @param sourceEand
     * @param maxDistance
     * @return
     */
        @Override
        public List<ProductNearByDto> getServiceNearBy ( Long tenantId, String sourceNand, String sourceEand,
        Double maxDistance, Integer serviceType, Long cityId, Integer locationType, Long oilCardType){
            Double sourceNandDouble = null;
            Double sourceEandDouble = null;
            if (sourceNand!=null && StringUtils.isNotEmpty(sourceNand)){
                sourceEandDouble =Double.parseDouble(sourceNand);
            }
            if (sourceEand!=null && StringUtils.isNotEmpty(sourceEand)){
                sourceNandDouble =Double.parseDouble(sourceEand);
            }
            //靠台地点附近最近的油站
            double[][] fourPoint =  null;
            if ((sourceEandDouble!=null || sourceNandDouble!=null ) && maxDistance!=null){
                fourPoint=GpsUtil.getRectangle4Point(sourceNandDouble, sourceEandDouble, maxDistance);
            }
            String  nand1 =null; // <=
            String  eand2 = null; // >=
            String  nand2 = null; // >=
            String  eand1 = null; // <=
            if (fourPoint!=null){
                  nand1 =String.valueOf(fourPoint[0][0]); // <=
                  eand2 =  String.valueOf(fourPoint[0][1]); // >=
                  nand2 = String.valueOf(fourPoint[3][0]); // >=
                  eand1 = String.valueOf(fourPoint[3][1]); // <=
            }
             List<Long>  tenantIds = new ArrayList<>();
             tenantIds.add(tenantId);
             tenantIds.add(SysStaticDataEnum.PT_TENANT_ID);
             Integer state  = SysStaticDataEnum.SYS_STATE_DESC.STATE_YES;
            Integer businessType =serviceType;
            Integer authState = SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS;
            List<ProductNearByDto> serviceNearBy3 = baseMapper.getServiceNearBy1(nand1, eand2, nand2, eand1, tenantIds,
                    state, businessType, authState, cityId, locationType, oilCardType, serviceType);
            List<ProductNearByDto> dtoList = new ArrayList<>();
            for (ProductNearByDto d  :serviceNearBy3) {
                ProductNearByDto dto = new ProductNearByDto();
                dto.setServiceProduct(d.getServiceProduct());
                dto.setTenantProductRel(d.getTenantProductRel());
                dto.setServiceInfo(d.getServiceInfo());
                dto.setOilPriceProvince(d.getOilPriceProvince());
                dtoList.add(dto);
            }
            return dtoList;
        }

        @Override
        public Map<String, Map<String, Object>> getMark (List < Long > productIds,int serviceType){
            Map<String, Map<String, Object>> returnMap = new HashMap<>();
            List<Long> notNullProductId = new ArrayList<>();
            if (productIds != null && productIds.size() > 0) {
                for (Long productId : productIds) {
//                    String hget = RemoteCacheUtil.hget(EnumConsts.RemoteCache.SERVICE_PRODUCT_MARK, String.valueOf(productId));
                    String hget =null;
                    if (productId!=null){
                        Object hget1 = redisUtil.hget(EnumConsts.RemoteCache.SERVICE_PRODUCT_MARK, productId.toString());
                        if (hget1!=null){
                            hget = hget1.toString();
                        }
                    }
                    if (StringUtils.isNotBlank(hget)) {
                        notNullProductId.add(productId);
                        Map<String, Object> map = JsonHelper.parseJSON2Map(hget);
                        returnMap.put(String.valueOf(productId), map);
                    }
                }
            }
            List<Long> nullProductId = new ArrayList<>();
            nullProductId.addAll(productIds);
            nullProductId.removeAll(notNullProductId);
            if (nullProductId.size() > 0) {
                List<ConsumeOilFlowVo> maps = null;
                if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.REPAIR) {
                    List<UserRepairInfoVo> userRepairByProductIds = iUserRepairInfoService.getUserRepairByProductIds(nullProductId);
                    for (UserRepairInfoVo vo : userRepairByProductIds) {
                        ConsumeOilFlowVo flowVo = new ConsumeOilFlowVo();
                        flowVo.setProductId(vo.getProductId());
                        flowVo.setQualityStar(vo.getQualityStar());
                        flowVo.setPriceStar(vo.getPriceStar());
                        flowVo.setServiceStar(vo.getServiceStar());
                    }
                } else if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                    maps = iConsumeOilFlowService1.getConsumeOilFlowProductIds(nullProductId);
                }
                if (maps != null && maps.size() > 0) {
                    for (ConsumeOilFlowVo map : maps) {
                        long productId = map.getProductId();
                        Map<String, Object> mark = new HashMap<>();
                        mark.put("qualityStar", map.getQualityStar());
                        mark.put("priceStar", map.getPriceStar());
                        mark.put("serviceStar", map.getServiceStar());
//                        RemoteCacheUtil.hset(EnumConsts.RemoteCache.SERVICE_PRODUCT_MARK, String.valueOf(productId), JsonHelper.json(mark));
                        redisUtil.hset(EnumConsts.RemoteCache.SERVICE_PRODUCT_MARK, String.valueOf(productId), JsonHelper.toJson(mark));
//                        RemoteCacheUtil.expire(EnumConsts.RemoteCache.SERVICE_PRODUCT_MARK, ServiceConsts.TIME_OUT);
                        redisUtil.expire(EnumConsts.RemoteCache.SERVICE_PRODUCT_MARK, ServiceConsts.TIME_OUT);
                        returnMap.put(String.valueOf(productId), mark);
                    }
                }
            }
            return returnMap;
        }

    @Override
    public Page<ServiceProduct> doQueryPrivateProduct(Long tenantId, Integer pageNum, Integer pageSize) {
        Page<ServiceProduct> page = new Page<>(pageNum,pageNum);
        Page<ServiceProduct> productPage = baseMapper.doQueryPrivateProduct(page, tenantId);
        return productPage;
    }

    /**
     * 查询附近油站（自有或共享）
     * @param longitude
     * @param latitude
     * @param isShare
     * @param amount
     * @param tenantId
     * @return
     * @throws Exception
     */
    @Override
    public List<ProductNearByOutDto> queryNearbyOil1(String longitude, String latitude, boolean isShare, Long amount, Long tenantId ,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long  tenantIds = null;
        Integer state = SysStaticDataEnum.SYS_STATE_DESC.STATE_YES;
        Integer businessType = SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL;
        Integer authState = SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS;
        if(isShare) {
            tenantId = SysStaticDataEnum.PT_TENANT_ID;
        }else if(null != tenantId && tenantId > -1){
            tenantIds  = tenantId;
        }
        List<ProductNearByDto> serviceNearBy2 = baseMapper.getServiceNearBy2(tenantIds, isShare, longitude, latitude, state, businessType, authState);
        List<ProductNearByDto> dtoList = new ArrayList<>();
        for (ProductNearByDto objects :serviceNearBy2) {
            ProductNearByDto dto = new ProductNearByDto();
            dto.setServiceProduct(objects.getServiceProduct());
            dto.setTenantProductRel(objects.getTenantProductRel());
            dto.setServiceInfo(objects.getServiceInfo());
            dto.setOilPriceProvince(objects.getOilPriceProvince());
            dtoList.add(dto);
        }
        List<ProductNearByOutDto> productNearByOutDtos = this.dealNearbyOil(latitude, longitude, dtoList, OIL, SysStaticDataEnum.IS_BILL_ABILITY.YSE);
        List<ProductNearByOutDto> outList = productNearByOutDtos;
        List<Long> oilIds = new ArrayList<>();
        List<OilServiceInDto> oilServiceIns = new ArrayList<>();
        List<ProductNearByOutDto> removeList = new ArrayList<>();
        for (ProductNearByOutDto productNearByOut : outList) {
            //判断油价是否为空或0，是的话剔除调，这种数据问题
            if (CommonUtil.isBlankToLong(productNearByOut.getOilPrice())
                    && (productNearByOut.getLocaleBalanceState() == null || productNearByOut.getLocaleBalanceState() != ServiceConsts.LOCALE_BALANCE_STATE.YES)) {
                removeList.add(productNearByOut);
            } else {
                OilServiceInDto oilServiceIn = new OilServiceInDto();
                oilServiceIn.setServiceId(productNearByOut.getServiceId());
                oilServiceIn.setProductId(productNearByOut.getOilId());
                oilServiceIn.setOilPrice(productNearByOut.getOilPrice());
                oilServiceIns.add(oilServiceIn);
                oilIds.add(productNearByOut.getOilId());
            }
        }

        //调订单的接口，查是否指派油站（周嘉）
        Map<String, Long> enMap = iOrderStatementService.queryOrderOilEnRouteUse(loginInfo.getUserInfoId(), oilIds);

        //调资金组接口，查看最低可以加油多少升（达成）
        List<OilServiceOutDto> oilServiceInList = iConsumeOilFlowService.getOilStationDetailslist(loginInfo.getUserInfoId(),
                oilServiceIns,
                SysStaticDataEnum.IS_BILL_ABILITY.YSE
                ,amount, tenantId);
        for (ProductNearByOutDto productNearByOut : outList) {
            productNearByOut.setIsOrder(0);
            if (enMap != null) {
                long orderId = DataFormat.getLongKey(enMap, String.valueOf(productNearByOut.getOilId()));
                if (orderId >= 0) {
                    productNearByOut.setIsOrder(1);
                }
            }
            if (oilServiceInList != null) {
                for (OilServiceOutDto oilServiceOut : oilServiceInList) {
                    if (oilServiceOut.getProductId().equals(productNearByOut.getOilId())) {
                        productNearByOut.setMaxOil(String.valueOf(oilServiceOut.getOilRise()));
                    }
                }
            }
        }
        //排序
        Map<String, Boolean> properties1 = new HashMap<>();
        properties1.put("isOrder", true);
        Map<String, Boolean> properties2 = new HashMap<>();
        properties2.put("distance", false);

        CommonUtil.sort(outList, properties1, properties2);

        return outList;
    }


    /**
     * 扫码支付详情接口
     * 接口编码：40001
     * @param oilId 油站id
     * @param tenantId 租户id
     * @return
     */
    @Override
    public ServiceProductOutDto scanCodePayment(Long oilId, Long tenantId, String accessToken) {
        ServiceProductOutDto dto = new ServiceProductOutDto();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (oilId < 0) {
            throw new BusinessException("油站id为空，请传入！");
        }
        if (tenantId == null || tenantId < -1) {
            throw new BusinessException("租户id，不能为空！");
        }
        boolean isShare = tenantId >= 0 ? false : true;
        ServiceProduct serviceProduct = this.getServiceProduct(oilId);
        if (serviceProduct == null) {
            throw new BusinessException("无该站点！");
        }
        ServiceInfo serviceInfo = iServiceInfoService.getServiceInfoById(serviceProduct.getServiceUserId());
        if (serviceInfo == null) {
            throw new BusinessException("无该服务商！");
        }
        if (serviceProduct.getState() == null || serviceProduct.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
            throw new BusinessException("无效站点，不能扫码支付！");
        }
       TenantProductRel tenantProductRel = iTenantProductRelService.getTenantProductRel(PT_TENANT_ID, serviceProduct.getId());
        if (null == tenantProductRel) {
            tenantProductRel = iTenantProductRelService.getTenantProductRel(tenantId, serviceProduct.getId());
        } else {
            isShare = true;
        }
        if (null != tenantProductRel) {
            if (tenantProductRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                throw new BusinessException("无效站点，不能扫码支付！");
            }
            if (tenantProductRel.getAuthState() != SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {
                throw new BusinessException("未认证通过的站点，不能扫码支付！");
            }
        } else {
            //外调车扫码使用现场价
            tenantProductRel = new TenantProductRel();
            tenantProductRel.setLocaleBalanceState(1);
        }

        // 获取全国省份油价
        Long oilPriceProvince = iOilPriceProvinceService.getOilPrice(serviceProduct.getProvinceId());
        // 计算油价
        Long oilPriceBill = iOilPriceProvinceService.calculationOilPrice(tenantProductRel, oilPriceProvince, isShare, true);

        dto.setOilId(serviceProduct.getId());
        dto.setOilName(serviceProduct.getProductName());
        if (serviceProduct.getIsBillAbility() != null && serviceProduct.getIsBillAbility() > 0) {
            dto.setIsBillAbility(serviceProduct.getIsBillAbility());
        } else {
            dto.setIsBillAbility(serviceInfo.getIsBillAbility());
        }

        // 固定价格除以100  浮动价格除以100 因为是百分比
        if (tenantProductRel.getFixedBalanceBill()!=null && tenantProductRel.getFixedBalanceBill()>0){
            dto.setOilPriceDouble(oilPriceBill==null?null:oilPriceBill/100.00);
        }else if (tenantProductRel.getFloatBalanceBill()!=null&& StringUtils.isNotBlank(tenantProductRel.getFloatBalanceBill())){
            dto.setOilPriceDouble(oilPriceBill==null?null:oilPriceBill/100.00);
        }else {
            dto.setOilPriceDouble(oilPriceBill==0?null:oilPriceBill/100.00);
        }
        dto.setOilPrice(oilPriceBill);
        dto.setLocaleBalanceState(tenantProductRel.getLocaleBalanceState() == null ? 0 : tenantProductRel.getLocaleBalanceState());
        dto.setOilPriceBill(oilPriceBill);
        dto.setOriginalPrice(oilPriceProvince);
        String provinceName = null;
        String cityName = null;
        String districtName = null;
        // 省
        if (serviceProduct.getProvinceId() != null && serviceProduct.getProvinceId() > 0) {
            provinceName = (getSysStaticDataId("SYS_PROVINCE", serviceProduct.getProvinceId() + "").getCodeName());
        }
        // 市
        if (serviceProduct.getCityId() != null && serviceProduct.getCityId() > 0) {
            cityName = (getSysStaticDataId("SYS_CITY", serviceProduct.getCityId() + "").getCodeName());
        }
        // 区、 县
        if (serviceProduct.getCountyId() != null && serviceProduct.getCountyId() > 0) {
            districtName = (getSysStaticDataId("SYS_DISTRICT", serviceProduct.getCountyId() + "").getCodeName());
        }
//        dto.setAddress(provinceName + cityName + districtName + serviceProduct.getAddress());
        dto.setAddress(serviceProduct.getAddress());
        //TODO 调资金的接口--算本油站消费余额（达成）
        dto.setConsumeOilBalance(0L);
        dto.setConsumeOilBalanceBilll(0L);
        List<OilServiceInDto> oilServiceIns = new ArrayList<>();
        OilServiceInDto oilServiceIn = new OilServiceInDto();
        oilServiceIn.setOilPrice(oilPriceBill);
        oilServiceIn.setProductId(serviceProduct.getId());
        oilServiceIn.setServiceId(serviceProduct.getServiceUserId());
        oilServiceIns.add(oilServiceIn);
        List<OilServiceOutDto> oilServiceInListBill = iOperationOilService.queryOilRise(loginInfo.getUserInfoId(),
                oilServiceIns, loginInfo.getTenantId(), loginInfo.getUserType());
        if (oilServiceInListBill != null && oilServiceInListBill.size() > 0) {
            dto.setConsumeOilBalanceBilll(oilServiceInListBill.get(0).getConsumeOilBalance());
            dto.setConsumeOilBalance(oilServiceInListBill.get(0).getConsumeOilBalance());
        } else {
            dto.setConsumeOilBalanceBilll(0L);
            dto.setConsumeOilBalance(0L);
        }
        return dto;
    }

    /**
     * 接口编码：40024
     * 附近维修站查询
     * @return
     * @throws Exception
     */
    @Override
    public  ProductNearByVo queryNearbyRepair(ServiceProductOutVo vo, String accessToken) {

        ProductNearByVo productNearByVo = new ProductNearByVo();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        com.youming.youche.system.domain.UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(loginInfo.getUserInfoId());
        if(StringUtils.isBlank(vo.getLatitude())){
            throw new BusinessException("纬度不能为空！");
        }
        if(StringUtils.isBlank(vo.getLongitude())){
            throw new BusinessException("经度不能为空！");
        }
        Long repairFundSum = this.getRepairFundSum(loginInfo.getUserInfoId(), loginInfo.getTenantId(), loginInfo.getUserType()==null?
                userDataInfo.getUserType():loginInfo.getUserType());
        productNearByVo.setOilBalance(repairFundSum);
        //查询未确认的维修交易订单数
        Integer sum = this.countRepairNumNoSure(loginInfo.getUserInfoId());
        productNearByVo.setRepairCount(sum);
        List<ProductNearByOutDto> outList = this.queryNearby(vo.getLongitude(),
                vo.getLatitude(), REPAIR, vo.getCityId(), vo.getLocationType(), vo.getProductPicId(), accessToken);
        //排序
        Map<String, Boolean> properties2 = new HashMap<>();
        properties2.put("distance", false);
        CommonUtil.sort(outList, properties2);
        productNearByVo.setOutList(outList);
        return productNearByVo;
    }

    @Override
    public Long getRepairFundSum(Long userId, Long tenantId, Integer userType) {
        if (userId <= 0) {
            throw new BusinessException("请输入用户编号!");
        }
        OrderAccountBalanceDto orderAccountBalance = iOrderAccountService.getOrderAccountBalance(userId,
                OrderAccountConst.ORDER_BY.REPAIR_FUND, tenantId, userType);
        return orderAccountBalance.getOa().getTotalRepairFund();
    }

    //统计未确认的维修记录
    @Override
    public Integer countRepairNumNoSure(Long driverUserId) {
        LambdaQueryWrapper<UserRepairInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRepairInfo::getUserId,driverUserId)
                .eq(UserRepairInfo::getRepairState, RepairConsts.APP_REPAIR_STATE.WAIT_DRIVER);
        List<UserRepairInfo> userRepairInfos = userRepairInfoMapper.selectList(wrapper);
//        Integer.valueOf(criteria.setProjection(Projections.rowCount()).uniqueResult().toString());
        Long count = userRepairInfos.stream().count();
        return Integer.valueOf(count.toString());
    }

    /**
     * 查询app维修保养记录
     * 40025
     * niejiewei
     * @param appRepairState
     * @return
     */
    @Override
    public IPage<UserRepairInfo> queryAppRepair(String appRepairState, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        loginInfo.getUserInfoId();
        List<String> appRepairStates = null;
        if (appRepairState == null || StringUtils.isBlank(appRepairState)) {
            appRepairStates = null;
        } else {
            appRepairStates = Arrays.asList(appRepairState.split(","));
        }
        Page<UserRepairInfo> page = new Page<>(pageNum,pageSize);
        IPage<UserRepairInfo> userRepairInfoIPage = baseMapper.queryAppRepair(appRepairStates, loginInfo.getUserInfoId(), page);
        return userRepairInfoIPage;
    }

    @Override
    public String getImgUrl(Long flowId) {
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            if (flowId != null && flowId > 0) {
                SysAttach sysAttach = iSysAttachService.getById(flowId);
                if (sysAttach != null) {
                    String url = client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0];
                    return url;
                }
            }
        } catch (Exception e ){
            e.printStackTrace();
        }
        return "";
    }


    private int countProductNum(Long serviceUserId, int userType) {
        QueryWrapper<ServiceProduct> queryWrapper = new QueryWrapper<>();
        if (userType == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER) {
            queryWrapper.eq("child_account_user_id", serviceUserId);
        } else {
            queryWrapper.eq("service_user_id", serviceUserId);
        }
        return baseMapper.selectCount(queryWrapper);
    }

    public com.youming.youche.commons.domain.SysStaticData getSysStaticDataId(String codeType, String codeValue) {
        List<com.youming.youche.commons.domain.SysStaticData> list = (List<com.youming.youche.commons.domain.SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (com.youming.youche.commons.domain.SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    private  List<ProductNearByOutDto>   dealNearbyOil(String latitude, String longitude, List<ProductNearByDto> productNearByDtos, int serviceType, int isBillAbility) {
        Set<Long> productIds = new HashSet<>();
        List<ProductNearByOutDto> outList = new ArrayList<>();
        if (productNearByDtos != null && productNearByDtos.size() > 0) {
            for (ProductNearByDto objects : productNearByDtos) {
                ServiceProduct serviceProduct = objects.getServiceProduct().get(0);
                if (serviceProduct == null) {
                    continue;
                }
                TenantProductRel tenantProductRel = objects.getTenantProductRel().get(0);
                if (tenantProductRel == null) {
                    continue;
                }
                ServiceInfo serviceInfo = objects.getServiceInfo().get(0);
                if (serviceInfo == null) {
                    continue;
                }
                ProductNearByOutDto productNearByOut = new ProductNearByOutDto();
                boolean isShare = tenantProductRel.getTenantId().longValue() == PT_TENANT_ID; //是否共享站点
                if (serviceType == 1) {
                    OilPriceProvince oilPriceProvince = objects.getOilPriceProvince().get(0);
                    if (oilPriceProvince == null) {
                        continue;
                    }
                    long originalPrice = oilPriceProvince.getOilPrice();
                    //计算油单价
                    long oilPrice = iOilPriceProvinceService.calculationOilPrice(tenantProductRel, oilPriceProvince.getOilPrice(), isShare, isBillAbility == 1 ? true : false);
                    productNearByOut.setOriginalPrice(originalPrice);
                    productNearByOut.setOilPrice(oilPrice);
                }
                try {

                } catch (Exception e) {

                }
                try {
                    FastDFSHelper client = FastDFSHelper.getInstance();
                    productNearByOut.setLogoId(serviceProduct.getLogoId());
                    productNearByOut.setLogoUrl(serviceProduct.getLogoUrl() != null ? client.getHttpURL(serviceProduct.getLogoUrl()).split("\\?")[0] : null);
                } catch (Exception e) {

                }
                productNearByOut.setOilName(serviceProduct.getProductName());
                productNearByOut.setOilId(serviceProduct.getId());
                productNearByOut.setServiceId(serviceProduct.getServiceUserId());
                productNearByOut.setIntroduce(serviceProduct.getIntroduce());
                int productType = serviceProduct.getProductType() == null ? ServiceConsts.PRODUCT_TYPE.EMPTY : serviceProduct.getProductType();
                productNearByOut.setProductType(productType);

                //司机当前位置到油站的距离
                double distance = GpsUtil.getDistance(Double.valueOf(latitude), Double.valueOf(longitude), Double.valueOf(serviceProduct.getNand()), Double.valueOf(serviceProduct.getEand()));
                productNearByOut.setDistance(String.valueOf(CommonUtil.getDoubleFormat(distance / 1000, 2)));
                String provinceName = null;
                String cityName = null;
                String districtName = null;
                // 省
                if (serviceProduct.getProvinceId() != null && serviceProduct.getProvinceId() > 0) {
                    provinceName = (getSysStaticDataId("SYS_PROVINCE", serviceProduct.getProvinceId() + "").getCodeName());
                }
                // 市
                if (serviceProduct.getCityId() != null && serviceProduct.getCityId() > 0) {
                    cityName = (getSysStaticDataId("SYS_CITY", serviceProduct.getCityId() + "").getCodeName());
                }
                // 区、 县
                if (serviceProduct.getCountyId() != null && serviceProduct.getCountyId() > 0) {
                    districtName = (getSysStaticDataId("SYS_DISTRICT", serviceProduct.getCountyId() + "").getCodeName());

                    productNearByOut.setAddress(provinceName + cityName + districtName + serviceProduct.getAddress());
                    productNearByOut.setServiceCall(serviceProduct.getServiceCall());
                    productNearByOut.setTenantId(tenantProductRel.getTenantId());
                    productNearByOut.setLatitude(serviceProduct.getNand());
                    productNearByOut.setLongitude(serviceProduct.getEand());
                    productNearByOut.setLocaleBalanceState(tenantProductRel.getLocaleBalanceState() == null ? 0 : tenantProductRel.getLocaleBalanceState());
                    productNearByOut.setLocaleBalanceName(productNearByOut.getLocaleBalanceState() == 0 ? "" : "现场价");

                    productNearByOut.setPrice("0");
                    productNearByOut.setService("0");
                    productNearByOut.setQuality("0");
                    outList.add(productNearByOut);
                    productIds.add(serviceProduct.getId());
                }
            }
            List<Long> productIdsList = new ArrayList<>();
            productIdsList.addAll(productIds);
            Map<String, Map<String, Object>> mark = this.getMark(productIdsList, serviceType);
            Map<Long, List<ProductNearByOutDto>> resultMap = null;
            try {
                resultMap = CommonUtil.groupDataByKey(outList, "oilId");
            } catch (Exception e) {

            }
            List<ProductNearByOutDto> remove = new ArrayList<>();
            if (outList != null && outList.size() > 0) {
                for (ProductNearByOutDto productNearByOut : outList) {
                    if (resultMap.get(productNearByOut.getOilId()).size() > 1 && productNearByOut.getTenantId().longValue() == PT_TENANT_ID) {
                        remove.add(productNearByOut);
                    }
                }
                if (remove != null && remove.size() > 0) {
                    outList.removeAll(remove);
                }
            }
            for (ProductNearByOutDto productNearByOut : outList) {
                Map<String, Object> map = mark.get(String.valueOf(productNearByOut.getOilId()));
                if (map != null && map.size() > 0) {
                    String qualityStar = DataFormat.getStringKey(map, "qualityStar");
                    String priceStar = DataFormat.getStringKey(map, "priceStar");
                    String serviceStar = DataFormat.getStringKey(map, "serviceStar");
                    productNearByOut.setQuality(qualityStar);
                    productNearByOut.setPrice(priceStar);
                    productNearByOut.setService(serviceStar);
                    //站点评分为空时 默认满分
                    if (StringUtils.isBlank(productNearByOut.getQuality())) {
                        productNearByOut.setQuality("5");
                    }
                    if (StringUtils.isBlank(productNearByOut.getPrice())) {
                        productNearByOut.setPrice("5");
                    }
                    if (StringUtils.isBlank(productNearByOut.getService())) {
                        productNearByOut.setService("5");
                    }
                }
            }
//            pagination.setItems(outList);
        }
        return outList;
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

    public SysCfg getSysCfg(String cfgName, String cfgSystem){
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (Integer.parseInt(cfgSystem) == -1 || Integer.parseInt(cfgSystem)==(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }
}
