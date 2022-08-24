package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlService;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlVerService;
import com.youming.youche.market.api.facilitator.IServiceInvitationService;
import com.youming.youche.market.api.facilitator.IServiceInvitationVerService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.api.facilitator.ITenantProductRelVerService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelVerService;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceInvitation;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtl;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtlVer;
import com.youming.youche.market.domain.facilitator.ServiceInvitationVer;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantProductRelVer;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.dto.facilitator.CooperationDataInfoDto;
import com.youming.youche.market.dto.facilitator.CooperationDto;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInvitationMapper;
import com.youming.youche.market.provider.mapper.facilitator.UserDataInfoMarketMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import com.youming.youche.market.vo.facilitator.BusinessInvitationVo;
import com.youming.youche.market.vo.facilitator.ServiceInvitationVxVo;
import com.youming.youche.record.vo.UserDataInfoBackVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.youming.youche.record.common.SysStaticDataEnum.PT_TENANT_ID;
import static com.youming.youche.record.common.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD;


/**
 * <p>
 * 服务商申请合作 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInvitationServiceImpl extends BaseServiceImpl<ServiceInvitationMapper, ServiceInvitation> implements IServiceInvitationService {
    @Resource
    private IServiceInvitationVerService serviceInvitationVerService;
    @Autowired
    private IServiceInvitationDtlService serviceInvitationDtlService;
    @Autowired
    private IServiceInvitationDtlVerService serviceInvitationDtlVerService;
    @Resource
    private ServiceInvitationMapper serviceInvitationMapper;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Autowired
    private IOilPriceProvinceService oilPriceProvinceService;
    @Lazy
    @Autowired
    private ITenantProductRelService tenantProductRelService;
    @Lazy
    @Autowired
    private IServiceProductService serviceProductService;
    @Autowired
    private ITenantServiceRelService tenantServiceRelService;
    @Autowired
    private IServiceInfoService serviceInfoService;
    @Autowired
    private ITenantServiceRelVerService tenantServiceRelVerService;
    @Resource
    private LoginUtils loginUtils;
    @Autowired
    private ITenantProductRelVerService tenantProductRelVerService;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @Resource
    UserDataInfoMarketMapper userDataInfoMapper;



    @Override
    public ServiceInvitation saveInvitation(LoginInfo user, Long productId, Long tenantId, Long fixedBalance, String floatBalance, Long fixedBalanceBill, String floatBalanceBill, String serviceCharge, Long serviceUserId, Integer cooperationType, Integer localeBalanceState) {
        //保存合作详情
        ServiceInvitation serviceInvitation = new ServiceInvitation();
        serviceInvitation.setIsRead(SysStaticDataEnum.IS_SHARE.NO);
        serviceInvitation.setTenantId(user.getTenantId());
        serviceInvitation.setCooperationType(cooperationType);
        serviceInvitation.setServiceUserId(serviceUserId);
        serviceInvitation.setUserId(user.getId());
        saveOrUpdate(serviceInvitation, false);

        //保存历史记录
        ServiceInvitationVer serviceInvitationVer = new ServiceInvitationVer();
        BeanUtils.copyProperties(serviceInvitation,serviceInvitationVer);
        serviceInvitationVer.setUpdateOpId(user.getId());
        serviceInvitationVer.setUpdateDate(LocalDateTimeUtil.presentTime());
        serviceInvitationVerService.save(serviceInvitationVer);
        //保存明细
        if(productId>0) {
            ServiceInvitationDtl serviceInvitationDtl = new ServiceInvitationDtl();
            serviceInvitationDtl.setFixedBalance(fixedBalance);
            serviceInvitationDtl.setFloatBalance(floatBalance);

            serviceInvitationDtl.setFixedBalanceBill(fixedBalanceBill);
            serviceInvitationDtl.setFloatBalanceBill(floatBalanceBill);

            serviceInvitationDtl.setLocaleBalanceState(localeBalanceState);
            serviceInvitationDtl.setInviteId(serviceInvitation.getId());
            serviceInvitationDtl.setServiceCharge(serviceCharge);
            serviceInvitationDtl.setProductId(productId);
            serviceInvitationDtlService.save(serviceInvitationDtl);


            //把旧的历史设为不可用
            serviceInvitationDtlVerService.checkHisSetNot(serviceInvitationDtl.getId(),user);


            //保存明细详情
            ServiceInvitationDtlVer serviceInvitationDtlVer = new ServiceInvitationDtlVer();
            BeanUtils.copyProperties(serviceInvitationDtl,serviceInvitationDtlVer);
            serviceInvitationDtlVer.setUpdateOpId(user.getId());
            serviceInvitationDtlVer.setUpdateTime(LocalDateTimeUtil.presentTime());
            serviceInvitationDtlVerService.save(serviceInvitationDtlVer);
            serviceInvitation.setServiceInvitationDtl(serviceInvitationDtl);
        }
        return serviceInvitation;
    }

    @Override
    public List<CooperationDto> queryCooperationList(Long id) {
        return  serviceInvitationMapper.queryCooperationList(id);
    }

    @Override
    public Page<ServiceInvitationVxVo> queryBusinessInvitationVx(Integer pageNum, Integer pageSize,
                                                                 String tenantName, List<Integer> cooperationType,
                                                                 List<Integer> authState, String linkPhone,
                                                                 String linkman,String token) {
        LoginInfo loginInfo = loginUtils.get(token);
        Page<ServiceInvitationVxVo> page = new Page<>(pageNum, pageSize);
        Page<ServiceInvitationVxVo> serviceInvitationVxVoPage = serviceInvitationMapper.queryBusinessInvitation(page, tenantName,
                cooperationType, authState, linkPhone, linkman,loginInfo);
        List<ServiceInvitationVxVo> records = serviceInvitationVxVoPage.getRecords();
        if(records != null && records.size() > 0){
            for (ServiceInvitationVxVo record : records) {
                if(record.getCooperationType() != null){
                    if (record.getCooperationType() == 1) {
                        record.setCooperationTypeName("初次合作申请");
                    } else if (record.getCooperationType() == 2) {
                        record.setCooperationTypeName("修改合作申请");
                    }
                }
            }
        }
        serviceInvitationVxVoPage.setRecords(records);
        return serviceInvitationVxVoPage;
    }

    @Override
    public BusinessInvitationVo getBusinessInvitation(Long id, Integer cooperationType) {
        if(id == null || id < 0){
            throw new BusinessException("请输入邀请ID！");
        }
        if(cooperationType == null && cooperationType < 0){
            throw new BusinessException("申请合作类型，必传！");
        }
        ServiceInvitation serviceInvitation = this.getById(id);
        if (serviceInvitation == null) {
            throw new BusinessException("邀请记录不存在！");
        }
        BusinessInvitationVo map = new BusinessInvitationVo();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(serviceInvitation.getTenantId());
        map.setTenantName(sysTenantDef.getName());
        map.setLinkman(sysTenantDef.getActualController());
        map.setLinkPhone(sysTenantDef.getActualControllerPhone());
        map.setCreateDate(serviceInvitation.getCreateDate());
        map.setCooperationType(serviceInvitation.getCooperationType());
        map.setId(id);
        map.setTenantId(sysTenantDef.getId());

        List<CooperationDto> list = this.queryCooperationList(id);
        List<CooperationDataInfoDto> cooperationList = new ArrayList<>();
        Long serviceUserId = serviceInvitation.getServiceUserId();
        if (cooperationType == 1) {
            if (list != null && list.size() > 0) {
                for (CooperationDto map1 : list) {
                    CooperationDataInfoDto cooperationMap = new CooperationDataInfoDto();
                    //                    int isBillAbility = DataFormat.getIntKey(map1,"isBillAbility");
                    String floatBalance = "0";
                    if(StringUtils.isNotBlank(map1.getFloatBalance())){
                        floatBalance=map1.getFloatBalance();
                    }
                    long fixedBalance = 0;
                    if(map1.getFixedBalance() != null){
                        fixedBalance=map1.getFixedBalance();
                    }
                    String serviceCharge = "0";
                    if(StringUtils.isNotBlank(map1.getServiceCharge())){
                        serviceCharge=map1.getServiceCharge();
                    }
                    long originalPrice = 0;
                    if(map1.getOilPrice() != null){
                       originalPrice= map1.getOilPrice();
                    }
                    int localeBalanceState = 0;
                    if( map1.getLocaleBalanceState() != null){
                        localeBalanceState=map1.getLocaleBalanceState();
                    }
                    long oilPrice = 0L;
                    cooperationMap.setLocaleBalanceState(localeBalanceState < 0 ? 0 : localeBalanceState);
                    cooperationMap.setLocaleBalanceStateName(localeBalanceState == 1 ? "现场价" : "");
                    oilPrice = oilPriceProvinceService.calculationSharePrice(floatBalance, fixedBalance, originalPrice, serviceCharge);
                    cooperationMap.setOilPrice(oilPrice > 0 ? oilPrice : 0);
                    cooperationMap.setProductName(map1.getProductName());
                    cooperationMap.setProductId( map1.getProductId());
                    cooperationList.add(cooperationMap);
                    serviceUserId = map1.getServiceUserId();
                }
            }
        } else {
            List<ServiceInvitationDtl> serviceInvitationDtls = serviceInvitationDtlService.getInviteDtlList(serviceInvitation.getId());

            if (serviceInvitationDtls != null && serviceInvitationDtls.size() > 0) {
                for (ServiceInvitationDtl serviceInvitationDtl : serviceInvitationDtls) {
                    CooperationDataInfoDto cooperationMap = new CooperationDataInfoDto();
                    TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(serviceInvitation.getTenantId(), serviceInvitationDtl.getProductId());
                    if(tenantProductRel==null){
                        tenantProductRel = tenantProductRelService.getTenantProductRel(1L,serviceInvitationDtl.getProductId());
                    }
                    ServiceProduct serviceProduct = serviceProductService.getById(serviceInvitationDtl.getProductId());
                    OilPriceProvince oilPriceProvince = oilPriceProvinceService.getOilPriceProvince(serviceProduct.getProvinceId());
                    cooperationMap.setProductName( serviceProduct.getProductName());
                    cooperationMap.setPaymentDays("");
                    cooperationMap.setPaymentDaysType("");
                    cooperationMap.setPaymentDaysOldType("");
                    cooperationMap.setOilPrice(oilPriceProvince.getOilPrice());
                    if (serviceInvitationDtl.getLocaleBalanceState() != null && serviceInvitationDtl.getLocaleBalanceState() == ServiceConsts.LOCALE_BALANCE_STATE.YES) {
                        cooperationMap.setPaymentDays("现场价");
                        cooperationMap.setPaymentDaysType( "3");
                    }else if ((serviceProduct.getIsBillAbility()==null||serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL0)
                            && StringUtils.isNotBlank(serviceInvitationDtl.getFloatBalance()) && CommonUtil.isNumber(serviceInvitationDtl.getFloatBalance())) {
                        cooperationMap.setPaymentDays("浮动价结算");
                        cooperationMap.setFloatBalance(serviceInvitationDtl.getFloatBalance());
                        cooperationMap.setPaymentDaysOldType("1");

                    } else if ((serviceProduct.getIsBillAbility()==null||serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL0)
                            &&serviceInvitationDtl.getFixedBalance() != null && serviceInvitationDtl.getFixedBalance() > 0) {
                        cooperationMap.setPaymentDays("固定价结算");
                        cooperationMap.setFixedBalance(serviceInvitationDtl.getFixedBalance());
                        cooperationMap.setPaymentDaysType("2");
                    }else if ((serviceProduct.getIsBillAbility()!=null&&serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL1)
                            &&StringUtils.isNotBlank(serviceInvitationDtl.getFloatBalanceBill()) && CommonUtil.isNumber(serviceInvitationDtl.getFloatBalanceBill())) {
                        cooperationMap.setPaymentDays("浮动价结算");
                        cooperationMap.setFloatBalance(serviceInvitationDtl.getFloatBalanceBill());
                        cooperationMap.setPaymentDaysType("1");

                    } else if ((serviceProduct.getIsBillAbility()!=null&&serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL1)
                            &&serviceInvitationDtl.getFixedBalanceBill() != null && serviceInvitationDtl.getFixedBalanceBill() > 0) {
                        cooperationMap.setPaymentDays("固定价结算");
                        cooperationMap.setFixedBalance(serviceInvitationDtl.getFixedBalanceBill());
                        cooperationMap.setPaymentDaysType("2");
                    }

                    if (tenantProductRel.getLocaleBalanceState() != null && tenantProductRel.getLocaleBalanceState() == ServiceConsts.LOCALE_BALANCE_STATE.YES) {
                        cooperationMap.setPaymentDaysOld("现场价");
                        cooperationMap.setPaymentDaysOldType("3");
                    }else if ((serviceProduct.getIsBillAbility()==null||serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL0)
                            &&StringUtils.isNotBlank(tenantProductRel.getFloatBalance()) && CommonUtil.isNumber(tenantProductRel.getFloatBalance())) {
                        cooperationMap.setPaymentDaysOld("浮动价结算");
                        cooperationMap.setFloatBalanceOld(tenantProductRel.getFloatBalance());
                        cooperationMap.setPaymentDaysOldType("1");

                    } else if ((serviceProduct.getIsBillAbility()==null||serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL0)
                            &&tenantProductRel.getFixedBalance() != null && tenantProductRel.getFixedBalance() > 0) {
                        cooperationMap.setPaymentDaysOld("固定价结算");
                        cooperationMap.setFixedBalanceOld(tenantProductRel.getFixedBalance());
                        cooperationMap.setPaymentDaysOldType("2");

                    }else if ((serviceProduct.getIsBillAbility()!=null&&serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL1)
                            &&StringUtils.isNotBlank(tenantProductRel.getFloatBalanceBill()) && CommonUtil.isNumber(tenantProductRel.getFloatBalanceBill())) {
                        cooperationMap.setPaymentDaysOld("浮动价结算");
                        cooperationMap.setFloatBalanceOld(tenantProductRel.getFloatBalanceBill());
                        cooperationMap.setPaymentDaysOldType("1");

                    } else if ((serviceProduct.getIsBillAbility()!=null&&serviceProduct.getIsBillAbility()== ServiceConsts.IS_NEED_BILL.IS_NEED_BILL1)
                            &&tenantProductRel.getFixedBalanceBill() != null && tenantProductRel.getFixedBalanceBill() > 0) {
                        cooperationMap.setPaymentDaysOld("固定价结算");
                        cooperationMap.setFixedBalanceOld( tenantProductRel.getFixedBalanceBill());
                        cooperationMap.setPaymentDaysOldType("2");

                    }
                    cooperationList.add(cooperationMap);
                }
            }

        }
        map.setCooperationList(cooperationList);
        if (serviceUserId != null && serviceUserId >= 0) {
            TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(sysTenantDef.getId(),serviceUserId);
            ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceUserId);
            map.setIsBill(tenantServiceRel.getIsBill());
            map.setIsBillName(tenantServiceRel.getIsBill() == 1 ? "需要开票" : "不需要开票");
            map.setPaymentDays( tenantServiceRel.getPaymentDays());
            map.setPaymentMonth(tenantServiceRel.getPaymentMonth());
            map.setBalanceType(tenantServiceRel.getBalanceType());
            map.setServiceType(serviceInfo.getServiceType());
            String balanceTypeName = "账期";
            if(tenantServiceRel.getBalanceType() == 2){
                balanceTypeName = "月结";
            }else if(tenantServiceRel.getBalanceType() == 3){
                balanceTypeName = "无账期";
            }
            map.setBalanceTypeName(balanceTypeName);
            map.setQuotaAmt(tenantServiceRel.getQuotaAmt());
            if(tenantServiceRel.getQuotaAmt()== null){
                map.setQuotaAmtName("0.0");
            }else{
                map.setQuotaAmtName(tenantServiceRel.getQuotaAmt().toString());
            }

            if(null == tenantServiceRel.getQuotaAmt()){
                map.setQuotaAmtName("不受限");
            }
            map.setUseQuotaAmt(tenantServiceRel.getUseQuotaAmt());
            if(cooperationType == 2) {
                TenantServiceRelVer tenantServiceRelVer=tenantServiceRelVerService.getTenantServiceRelVerByInvitationId(tenantServiceRel.getId(),id);
                if(tenantServiceRelVer == null){
                    tenantServiceRelVer = tenantServiceRelVerService.getTenantServiceRelVer(tenantServiceRel.getId());
                }
                if(tenantServiceRelVer!=null) {
                    map.setIsBillNew(tenantServiceRelVer.getIsBill());
                    map.setIsBillNameNew(tenantServiceRelVer.getIsBill() == 1 ? "需要开票" : "不需要开票");
                    map.setPaymentDaysNew(tenantServiceRelVer.getPaymentDays());
                    map.setPaymentMonthNew(tenantServiceRelVer.getPaymentMonth());
                    map.setBalanceTypeNew(tenantServiceRelVer.getBalanceType());
                    String balanceTypeNameVer = "账期";
                    if(tenantServiceRelVer.getBalanceType() == 2){
                        balanceTypeNameVer = "月结";
                    }else if(tenantServiceRelVer.getBalanceType() == 3){
                        balanceTypeNameVer = "无账期";
                    }
                    map.setBalanceTypeNameNew(balanceTypeNameVer);
                    map.setQuotaAmtNew(tenantServiceRelVer.getQuotaAmt());
                    if(tenantServiceRelVer.getQuotaAmt()==null){
                        map.setQuotaAmtNameNew("0.0");
                    }else{
                        map.setQuotaAmtNameNew(tenantServiceRelVer.getQuotaAmt().toString());
                    }

                    if(null == tenantServiceRelVer.getQuotaAmt()){
                        map.setQuotaAmtNameNew("不受限");
                    }

                    map.setQuotaAmtRelNew(tenantServiceRelVer.getQuotaAmtRel());
                    if(null == tenantServiceRelVer.getQuotaAmtRel()){
                        map.setQuotaAmtRelNameNew("不受限");
                    }else{
                        map.setQuotaAmtRelNameNew(tenantServiceRelVer.getQuotaAmtRel().toString());
                    }
                }
            }
        } else {
            map.setIsBill(null);
            map.setPaymentDays(null);
            map.setBalanceType(null);
            map.setBalanceTypeName("");
            map.setPaymentMonth(null);
        }


        map.setRemark(serviceInvitation.getApplyReason());
        map.setFileId(serviceInvitation.getFileId());
        map.setFileUrl( serviceInvitation.getFileUrl());
        return map;
    }

    @Override
    public Boolean auditInvitation(Long id, Integer authState, String remark,
                                   Integer cooperationType, String quotaAmt,String accessToken) {
        if(id < 0){
            throw new BusinessException("申请ID不能为空！");
        }
        if(authState < 0){
            throw new BusinessException("审核状态不能为空！");
        }
        if(cooperationType < 0){
            throw new BusinessException("申请合作类型不能为空！");
        }
        if(StringUtils.isBlank(quotaAmt)){
            quotaAmt = "";
        }
        ServiceInvitation serviceInvitation = this.getById(id);
        if(null == serviceInvitation){
            throw new BusinessException("邀请记录不存在");
        }
        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceInvitation.getServiceUserId());
        if(null != serviceInfo.getServiceType() && (serviceInfo.getServiceType() == OIL_CARD)){
            throw new BusinessException("请在web端审核");
        }
        LoginInfo user = loginUtils.get(accessToken);
        if (authState == 3 && StringUtils.isBlank(remark)) {
            throw new BusinessException("驳回时，理由不能为空，请填写！");
        }
        if (serviceInvitation == null) {
            throw new BusinessException("邀请信息有误！");
        }
        if (serviceInvitation.getAuthState() != SysStaticDataEnum.INVITE_AUTH_STATE.WAIT) {
            throw new BusinessException("该邀请信息已经审核过了，不能重复审核！");
        }

        if (serviceInfo == null) {
            throw new BusinessException("服务商信息不存在！");
        }
        List<ServiceInvitationDtl> inviteDtlList = serviceInvitationDtlService.getInviteDtlList(id);


        if (inviteDtlList != null && inviteDtlList.size() > 0) {
            for (ServiceInvitationDtl serviceInvitationDtl : inviteDtlList) {
                TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(serviceInvitation.getTenantId(),
                        serviceInvitationDtl.getProductId());
                if(tenantProductRel==null){
                    tenantProductRel =  tenantProductRelService.getTenantProductRel(1L,
                            serviceInvitationDtl.getProductId());
                }
                TenantProductRelVer tenantProductRelVer = null;
                if (tenantProductRel != null) {
                    tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
                }
                if (cooperationType == 1) {
                    if (authState == 2) {
                        Long fixedBalance = serviceInvitationDtl.getFixedBalance();
                        String floatBalance = serviceInvitationDtl.getFloatBalance();
                        String serviceCharge = serviceInvitationDtl.getServiceCharge();


                        Long fixedBalanceBill = serviceInvitationDtl.getFixedBalanceBill();
                        String floatBalanceBill = serviceInvitationDtl.getFloatBalanceBill();
                        String serviceChargeBill = serviceInvitationDtl.getServiceChargeBill();

                        if (tenantProductRel == null) {
                            ProductSaveDto productSaveIn = new ProductSaveDto();
                            if(serviceInfo.getServiceType() != OIL_CARD){
                                Double floatBalanceDouble = calculationServiceFee(floatBalance, serviceCharge, -1L);
                                Double fixedBalanceDouble = calculationServiceFee("", serviceCharge, fixedBalance==null?0:fixedBalance);

                                Double floatBalanceDoubleBill = calculationServiceFee(floatBalanceBill, serviceChargeBill, -1L);
                                Double fixedBalanceDoubleBill = calculationServiceFee("", serviceChargeBill, fixedBalanceBill==null?0:fixedBalanceBill);
                                //不开票价格
                                if (floatBalanceDouble > 0) {
                                    productSaveIn.setFloatBalance(String.valueOf(CommonUtil.getDoubleFormat(floatBalanceDouble * 100, 4)));
                                } else if (fixedBalanceDouble > 0) {
                                    productSaveIn.setFixedBalance(String.valueOf(fixedBalanceDouble));

                                }
                                //开票价格(默认)
                                if (floatBalanceDoubleBill > 0) {
                                    productSaveIn.setFloatBalanceBill(String.valueOf(CommonUtil.getDoubleFormat(floatBalanceDoubleBill * 100, 4)));
                                } else if (fixedBalanceDoubleBill > 0) {
                                    productSaveIn.setFixedBalanceBill(String.valueOf(fixedBalanceDoubleBill));

                                }
                                productSaveIn.setLocaleBalanceState(serviceInvitationDtl.getLocaleBalanceState());
                            }else{
                                productSaveIn.setFloatBalance(floatBalance);
                                productSaveIn.setFloatBalanceBill(floatBalanceBill);
                                productSaveIn.setServiceCharge(serviceCharge);
                                productSaveIn.setServiceChargeBill(serviceChargeBill);
                                if(fixedBalance > 0){
                                    productSaveIn.setFixedBalance(CommonUtil.divide(fixedBalance));
                                }
                                if(fixedBalanceBill > 0){
                                    productSaveIn.setFixedBalanceBill(CommonUtil.divide(fixedBalanceBill));
                                }
                            }

                            TenantProductRel tenantProductRelPT = tenantProductRelService.getTenantProductRel(user.getTenantId(),serviceInvitationDtl.getProductId());
                            if(null != tenantProductRelPT){
                                productSaveIn.setServiceCharge(tenantProductRelPT.getServiceCharge());
                                productSaveIn.setServiceChargeBill(tenantProductRelPT.getServiceChargeBill());
                            }
                            tenantProductRel = tenantProductRelService.saveProductRel(true, productSaveIn,
                                    serviceInvitationDtl.getProductId(), serviceInvitation.getTenantId(),user);
                            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(),
                                    SysOperLogConst.OperType.Add, "新增");
                            //启动审核流程
                            Map inMap = new HashMap();
                            inMap.put("svName", "serviceProductTF");
                            boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_PRODUCT, tenantProductRel.getProductId(),
                                    SysOperLogConst.BusiCode.TenantProductRel, inMap, accessToken,tenantProductRel.getTenantId());
                            if (!bool) {
                                throw new BusinessException("启动审核流程失败！");
                            }
                        } else {
                            Long tenantProductRelId =null;
                            if(tenantProductRel != null && tenantProductRel.getId() != null){
                                tenantProductRelId=tenantProductRel.getId();
                            }
                            BeanUtils.copyProperties(tenantProductRelVer,tenantProductRel);
                            tenantProductRel.setCooperationState(ServiceConsts.COOPERATION_STATE.IN_COOPERATION);
                            tenantProductRel.setServiceAuthState(ServiceConsts.SERVICE_AUTH_STATE.PASS);
                            tenantProductRel.setUpdateTime(LocalDateTime.now());
                            tenantProductRel.setOpId(user.getId());
                            tenantProductRel.setId(tenantProductRelId);
                            tenantProductRel.setTenantId(serviceInvitation.getTenantId());
                            tenantProductRel.setAuthState(ServiceConsts.SERVICE_AUTH_STATE.PASS);
                    //        tenantProductRel.setId(null);
                            tenantProductRelService.saveOrUpdate(tenantProductRel);
                            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel,
                                    tenantProductRel.getProductId(), SysOperLogConst.OperType.Audit, "服务商审核通过",serviceInvitation.getTenantId());
                        }
                    } else if (authState == 3 && tenantProductRel != null) {
                        tenantProductRel.setServiceAuthState(ServiceConsts.SERVICE_AUTH_STATE.FAIL);
                        tenantProductRel.setUpdateTime(LocalDateTime.now());
                        tenantProductRel.setOpId(user.getId());
                        tenantProductRel.setId(null);
                        tenantProductRelService.saveOrUpdate(tenantProductRel);
                        sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel,
                                tenantProductRel.getProductId(), SysOperLogConst.OperType.Audit,
                                "服务商审核不通过，原因：" + remark);
                    }
                } else if (cooperationType == 2) {
                    if(null != tenantProductRel){

                        if (authState == 2 ) {
                            tenantProductRel.setFloatBalance(serviceInvitationDtl.getFloatBalance());
                            tenantProductRel.setFixedBalance(serviceInvitationDtl.getFixedBalance());

                            tenantProductRel.setFloatBalanceBill(serviceInvitationDtl.getFloatBalanceBill());
                            tenantProductRel.setFixedBalanceBill(serviceInvitationDtl.getFixedBalanceBill());

                            tenantProductRel.setLocaleBalanceState(serviceInvitationDtl.getLocaleBalanceState());

                            tenantProductRel.setServiceAuthState(ServiceConsts.SERVICE_AUTH_STATE.PASS);
                            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel,
                                    tenantProductRel.getProductId(), SysOperLogConst.OperType.Audit, "服务商审核通过");
                        } else if (authState == 3) {
                            tenantProductRel.setServiceAuthState(ServiceConsts.SERVICE_AUTH_STATE.FAIL);
                            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel,
                                    tenantProductRel.getProductId(), SysOperLogConst.OperType.Audit, "服务商审核不通过，原因：" + remark);
                        }
                        tenantProductRel.setServiceAuthRemark(remark);
                        tenantProductRel.setUpdateTime(LocalDateTime.now());
                        tenantProductRel.setOpId(user.getId());
                        if(tenantProductRel.getFixedBalanceBill() != null){
                            tenantProductRel.setFloatBalanceBill(null);
                        }
                        if(tenantProductRel.getFloatBalanceBill() != null){
                            tenantProductRel.setFixedBalanceBill(null);
                        }
                        if(tenantProductRel.getLocaleBalanceState() != null && tenantProductRel.getLocaleBalanceState() == 1){
                            tenantProductRel.setFloatBalanceBill(null);
                            tenantProductRel.setFixedBalanceBill(null);
                        }
                        tenantProductRelService.saveOrUpdate(tenantProductRel);
                    }
                } else {
                    throw new BusinessException("申请合作类型有误！");
                }
            }
        } /*else {
            throw new BusinessException("邀请信息有误！");
        }*/

        if (authState == 2) {
            serviceInvitation.setAuthState(SysStaticDataEnum.INVITE_AUTH_STATE.PASS);
            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.serviceInvitation,
                    serviceInvitation.getId(), SysOperLogConst.OperType.Audit, "服务商审核通过" );
        } else if (authState == 3) {
            serviceInvitation.setAuthState(SysStaticDataEnum.INVITE_AUTH_STATE.FAIL);
            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.serviceInvitation,
                    serviceInvitation.getId(), SysOperLogConst.OperType.Audit, "服务商审核不通过，原因：" + remark);
        } else {
            throw new BusinessException("审核状态有误！");
        }
        serviceInvitation.setAuthReason(remark);
        serviceInvitation.setAuthManId(user.getUserInfoId());
        serviceInvitation.setAuthDate(LocalDateTimeUtil.presentTime());
        serviceInvitation.setIsRead(ServiceConsts.IS_SHARE.NO);
        this.saveOrUpdate(serviceInvitation, true);

        //审核服务商邀请
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(serviceInvitation.getTenantId(),
                serviceInvitation.getServiceUserId());
        if(tenantServiceRel==null) {
            throw new BusinessException("未找到车队服务商信息！");
        }
        Long amt = tenantServiceRel.getQuotaAmt();
        if(cooperationType==1) {
            tenantServiceRel.setInvitationState(authState==2?SysStaticDataEnum.INVITE_AUTH_STATE.PASS:SysStaticDataEnum.INVITE_AUTH_STATE.FAIL);
        }else {
            tenantServiceRel.setInvitationState(SysStaticDataEnum.INVITE_AUTH_STATE.PASS);
        }


        //审核服务商信息修改（是否开票、账期）
        if(serviceInfo.getServiceType() != OIL_CARD && cooperationType==2&&authState==2) {
            TenantServiceRelVer tenantServiceRelVer=tenantServiceRelVerService.getTenantServiceRelVer(tenantServiceRel.getId());
            if(tenantServiceRelVer==null) {
                throw new BusinessException("未找到服务商修改记录！");
            }
            Long tenantServiceRelId=null;
            if(tenantServiceRel.getId() != null){
                tenantServiceRelId=tenantServiceRel.getId();
            }
            BeanUtils.copyProperties(tenantServiceRelVer,tenantServiceRel);
            tenantServiceRel.setId(tenantServiceRelId);
        }else if(cooperationType!=1&&cooperationType!=2){
            throw new BusinessException("申请合作类型有误！");
        }
        if(authState == 2){
            if("".equals(quotaAmt)){

                tenantServiceRel.setQuotaAmt(null);
                TenantServiceRelVer tenantServiceRelVer=tenantServiceRelVerService.getTenantServiceRelVer(tenantServiceRel.getId());
                if(!Objects.equals(tenantServiceRelVer.getQuotaAmt(),tenantServiceRel.getQuotaAmt())){
                    TenantServiceRelVer tenantServiceRelVerNew = new TenantServiceRelVer();
                    BeanUtils.copyProperties(tenantServiceRelVer,tenantServiceRelVerNew);
                    tenantServiceRelVerNew.setQuotaAmt(tenantServiceRel.getQuotaAmt());
                    tenantServiceRelVerNew.setQuotaAmtRel(amt);
                    tenantServiceRelVerNew.setInvitationId(serviceInvitation.getId());
                    tenantServiceRelVerNew.setId(null);
                    tenantServiceRelVerService.saveTenantServiceRelHis(tenantServiceRelVerNew,false,user);
                }else{
                    TenantServiceRelVer tenantServiceRelVerNew = new TenantServiceRelVer();
                    BeanUtils.copyProperties(tenantServiceRelVer,tenantServiceRelVerNew);
                    tenantServiceRelVerNew.setQuotaAmt(tenantServiceRel.getQuotaAmt());
                    tenantServiceRelVerNew.setQuotaAmtRel(amt);
                    tenantServiceRelVerNew.setInvitationId(serviceInvitation.getId());
                    tenantServiceRelVerNew.setId(null);
                    tenantServiceRelVerService.saveTenantServiceRelHis(tenantServiceRelVerNew,false,user);
                }
            }else{
                if(null != quotaAmt && CommonUtil.isNumber(quotaAmt)){
                    TenantServiceRelVer tenantServiceRelVer=tenantServiceRelVerService.getTenantServiceRelVer(tenantServiceRel.getId());

                    tenantServiceRel.setQuotaAmt(CommonUtil.multiply(quotaAmt));
                    if(!Objects.equals(tenantServiceRelVer.getQuotaAmt(),tenantServiceRel.getQuotaAmt())){
                        TenantServiceRelVer tenantServiceRelVerNew = new TenantServiceRelVer();
                        BeanUtils.copyProperties(tenantServiceRelVer,tenantServiceRelVerNew);
                        tenantServiceRelVerNew.setQuotaAmt(tenantServiceRel.getQuotaAmt());
                        tenantServiceRelVerNew.setQuotaAmtRel(amt);
                        tenantServiceRelVerNew.setInvitationId(serviceInvitation.getId());
                        tenantServiceRelVerNew.setId(null);
                        tenantServiceRelVerService.saveTenantServiceRelHis(tenantServiceRelVerNew,false,user);
                    }else{
                        TenantServiceRelVer tenantServiceRelVerNew = new TenantServiceRelVer();
                        BeanUtils.copyProperties(tenantServiceRelVer,tenantServiceRelVerNew);
                        tenantServiceRelVerNew.setQuotaAmt(tenantServiceRel.getQuotaAmt());
                        tenantServiceRelVerNew.setQuotaAmtRel(amt);
                        tenantServiceRelVerNew.setInvitationId(serviceInvitation.getId());
                        tenantServiceRelVerNew.setId(null);
                        tenantServiceRelVerService.saveTenantServiceRelHis(tenantServiceRelVerNew,false,user);
                    }
                }
            }
            tenantServiceRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
        }
        tenantServiceRel.setOpId(user.getUserInfoId());
        tenantServiceRel.setUpdateTime(LocalDateTime.now());
        tenantServiceRelService.saveOrUpdate(tenantServiceRel);

        return true;
    }

    public void saveOrUpdate(ServiceInvitation serviceInvitation,boolean isUpdate){
        if(!isUpdate){
            serviceInvitation.setAuthState(SysStaticDataEnum.INVITE_AUTH_STATE.WAIT);
            serviceInvitation.setCreateDate(LocalDateTimeUtil.presentTime());
            serviceInvitation.setIsRead(SysStaticDataEnum.IS_READ.NO);
        }
        this.saveOrUpdate(serviceInvitation);
    }


    /**
     * 计算平台所要收取的服务费
     *
     * @param floatBalance
     * @param serviceCharge
     * @param fixedBalance
     * @return
     * @throws Exception
     */
    private Double calculationServiceFee(String floatBalance, String serviceCharge, long fixedBalance)  {
        Double serviceChargeDouble = 0D;
        if (StringUtils.isNotBlank(serviceCharge)) {
            serviceChargeDouble = Double.valueOf(String.valueOf(serviceCharge)) / 100;
        }
        double price = 0D;
        if (fixedBalance > 0) {
            price = CommonUtil.getDoubleFormatLongMoney(fixedBalance, 2) * (1D + serviceChargeDouble);
        } else {
            Double floatBalanceDouble = 0D;
            if (StringUtils.isNotBlank(floatBalance)) {
                floatBalanceDouble = Double.valueOf(String.valueOf(floatBalance)) / 100;
            }
            price = floatBalanceDouble * (1D + serviceChargeDouble);
        }
        return price;
    }

    @Override
    public int countInvitationAuth(String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
//        UserDataInfo userDataInfo = userDataInfoMapper.selectById(user.getUserInfoId());
//        if ((userDataInfo.getUserType().intValue() != SysStaticDataEnum.USER_TYPE.SERVICE_USER) &&
//                (userDataInfo.getUserType().intValue() != SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER)) {
//            throw new BusinessException("不是服务商类型，不能查询！");
//        }
        return this.countInvitationAuth(user.getId());
    }

    private int countInvitationAuth(Long serviceUserId) {
        QueryWrapper<ServiceInvitation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_user_id",serviceUserId)
                   .eq("auth_state",SysStaticDataEnum.INVITE_AUTH_STATE.WAIT);
        return baseMapper.selectCount(queryWrapper);
    }
}
