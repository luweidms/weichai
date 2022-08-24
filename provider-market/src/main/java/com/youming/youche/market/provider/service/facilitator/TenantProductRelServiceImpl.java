package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceInvitationService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.IServiceProductVerService;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.api.facilitator.ITenantProductRelVerService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantProductRelVer;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.dto.facilitator.TenantProductRelOutDto;
import com.youming.youche.market.provider.mapper.facilitator.TenantProductRelMapper;
import com.youming.youche.market.provider.transfer.TenantProductRelTransfer;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.CooperationProductVo;
import com.youming.youche.market.vo.facilitator.TenantProductVo;
import com.youming.youche.market.vo.facilitator.logs;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_CITY;
import static com.youming.youche.conts.SysStaticDataEnum.COOPERATION_TYPE.NEW_COOPERATION;
import static com.youming.youche.conts.SysStaticDataEnum.PT_TENANT_ID;
import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL;
import static com.youming.youche.conts.SysStaticDataEnum.isFleet.TENANT;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_DISTRICT;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;
import static com.youming.youche.record.common.EnumConsts.SysStaticData.SYS_PROVINCE;


/**
 * <p>
 * 租户与站点关系表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-23
 */
@DubboService(version = "1.0.0")
@Service
public class TenantProductRelServiceImpl extends BaseServiceImpl<TenantProductRelMapper, TenantProductRel> implements ITenantProductRelService {
    @Resource
    private TenantProductRelMapper tenantProductRelMapper;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @Autowired
    private IServiceInvitationService serviceInvitationService;
    @Autowired
    private IServiceInfoService serviceInfoService;
    @Autowired
    private ITenantServiceRelService tenantServiceRelService;
    @Autowired
    private IServiceProductService serviceProductService;
    @Autowired
    private IServiceProductVerService serviceProductVerService;
    @Autowired
    private ITenantProductRelVerService tenantProductRelVerService;
    @Autowired
    private ITenantProductRelService tenantProductRelService;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Autowired
    private IUserDataInfoMarketService userDataInfoService;
    @Autowired
    private IOilPriceProvinceService oilPriceProvinceService;
    @Resource
    LoginUtils loginUtils;
    @Resource
    private TenantProductRelTransfer tenantProductRelTransfer;
    @Resource
    private ReadisUtil readisUtil;


    @Override
    public void loseCooperationTenant(Long productId, LoginInfo user) {
        LambdaQueryWrapper<TenantProductRel> lambda =new QueryWrapper<TenantProductRel>().lambda();
        lambda.eq(TenantProductRel::getProductId,productId)
               .eq(TenantProductRel::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        List<TenantProductRel> tenantProductRels = tenantProductRelMapper.selectList(lambda);
        if (tenantProductRels != null && tenantProductRels.size() > 0) {
            for (TenantProductRel productRel : tenantProductRels) {
                productRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                saveOrUpdate(productRel, true,user);
                sysOperLogService.saveSysOperLog(
                        user,
                        SysOperLogConst.BusiCode.TenantProductRel, productId,
                        SysOperLogConst.OperType.Update, "服务商把站点设为无效"
                );
            }
        }
    }

    @Override
    public TenantProductRel updateProductRel(LoginInfo user, ProductSaveDto productSaveIn, Long tenantId,
                                             Long productId, String productName, Long serviceUserId, Integer isFleet) {
        TenantProductRel tenantProductRel = null;
        //getIsFleet 1 为车队修改 2位服务商修改
        if (isFleet == TENANT) {
//            boolean isUpdate = true;
            tenantProductRel = getTenantProductRel(tenantId, productId);
            if(tenantProductRel == null){
                tenantProductRel =getTenantProductRel(1L, productId);
            }
            //已注册 通过地址匹配的站点  生产邀请记录 服务商确认后才会生成合作关系
            if (productSaveIn.getIsRegistered() != null && productSaveIn.getIsRegistered() == 1) {
                if (tenantProductRel != null && (productSaveIn.getState() == null || productSaveIn.getState() < 0)) {
                    throw new BusinessException("该服务商站点【" + productName + "】已经在注册了，不能重复注册！");
                }
                serviceInvitationService.saveInvitation(user,productId, tenantId, CommonUtil.getLongByString(productSaveIn.getFixedBalance()),
                        productSaveIn.getFloatBalance(),-1L,null, null, serviceUserId,
                       NEW_COOPERATION,productSaveIn.getLocaleBalanceState());
            } else {
                if (productSaveIn.getIsFleet() == 2 && (productSaveIn.getState() == null || productSaveIn.getState() < 0)) {
                    throw new BusinessException("状态不能为空，请选择！");
                }
                tenantProductRel.setServiceCall(productSaveIn.getServiceCall());
                tenantProductRel.setCityId(productSaveIn.getCityId());
                tenantProductRel.setCountyId(productSaveIn.getCountyId());
                tenantProductRel.setProvinceId(productSaveIn.getProvinceId());
                tenantProductRel.setIntroduce(productSaveIn.getIntroduce());
                tenantProductRel.setAddress(productSaveIn.getAddress());
                tenantProductRel.setProductName(productSaveIn.getProductName());
                tenantProductRel.setIsShare(productSaveIn.getIsShare());
                tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                saveOrUpdate(tenantProductRel, true,user);
                //保存历史记录
                tenantProductRelVerService.saveProductVer(tenantProductRel, productSaveIn, true,user);


                sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(), SysOperLogConst.OperType.Update, "修改");
            }

        } else if (isFleet == ServiceConsts.isFleet.SERVICE || isFleet == ServiceConsts.isFleet.OBMS) {
            tenantProductRel = getTenantProductRel(PT_TENANT_ID, productId);
            if(null != tenantProductRel){
                tenantProductRel.setState(productSaveIn.getState());
            }
            if (productSaveIn.getIsShare() == ServiceConsts.IS_SHARE.YES) {//是共享
                if (tenantProductRel == null) {
                    tenantProductRel = new TenantProductRel();
                    BeanUtils.copyProperties(productSaveIn,tenantProductRel);
                    tenantProductRel.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
                    tenantProductRel.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
                    tenantProductRel.setTenantId(PT_TENANT_ID);
                    if (productSaveIn.getProductState() != null && productSaveIn.getProductState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                        tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                    } else {
                        tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                    }
                    tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
                    saveOrUpdate(tenantProductRel, false,user);
                    //保存历史记录
                    tenantProductRelVerService.saveProductVer(tenantProductRel, productSaveIn, true,user);
                    sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel,
                            tenantProductRel.getProductId(), SysOperLogConst.OperType.Add, "新增");
                } else {
//                    BeanUtils.copyProperties(tenantProductRel,productSaveIn);
//                    tenantProductRel.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
//                    tenantProductRel.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
//                    if(productSaveIn.getProductState() != null && productSaveIn.getProductState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO){
//                        tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
//                    } else {
//                        tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
//                    }
//                    tenantProductRel
//                    saveOrUpdate(tenantProductRel,true);
                    if (productSaveIn.getProductType() != null && productSaveIn.getProductType().intValue() == ServiceConsts.PRODUCT_TYPE.ZHAOYOU) {
                        BeanUtils.copyProperties(tenantProductRel, productSaveIn);
                        tenantProductRel.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
                        tenantProductRel.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
                        if (productSaveIn.getProductState() != null && productSaveIn.getProductState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                            tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                        } else {
                            tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                        }
                        tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
                        saveOrUpdate(tenantProductRel,true,user);
                    }
                    tenantProductRelVerService.saveProductRelVelShare(productSaveIn, tenantProductRel,user);
//                    logSV.saveSysOperLog(SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getRelId(),  SysOperLogConst.OperType.Update, "修改");
                }
            } else if (productSaveIn.getIsShare() == ServiceConsts.IS_SHARE.NO && tenantProductRel != null) {
                //保存历史记录
                tenantProductRelVerService.saveProductVer(tenantProductRel, productSaveIn, true,user);
                if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.SERVICE) {
                    tenantProductRelService.remove(tenantProductRel.getId());
                }
                sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel,
                        tenantProductRel.getId(), SysOperLogConst.OperType.Del, "取消共享！");
            }
        } else if (isFleet == ServiceConsts.isFleet.OBMS_SHARE) {
            //新增共享信息
            tenantProductRel = getTenantProductRel(tenantId, productId);
            if (tenantProductRel == null) {
                tenantProductRel = new TenantProductRel();
            }
            BeanUtils.copyProperties(tenantProductRel, productSaveIn);
            tenantProductRel.setCooperationState(ServiceConsts.COOPERATION_STATE.IN_COOPERATION);
            tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
            tenantProductRel.setTenantId(PT_TENANT_ID);
            tenantProductRel.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
            if (StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())) {
                tenantProductRel.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
            }
            saveOrUpdate(tenantProductRel, false,user);

            //保存历史记录
            tenantProductRelVerService.saveProductVer(tenantProductRel, productSaveIn, false,user);

        }
        return tenantProductRel;
    }

    @Override
    public TenantProductRel getTenantProductRel(Long tenantId, Long productId) {
        LambdaQueryWrapper<TenantProductRel> lambda= new QueryWrapper<TenantProductRel>().lambda();
        lambda.eq(TenantProductRel::getTenantId,tenantId)
                .eq(TenantProductRel::getProductId,productId);
        List<TenantProductRel> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TenantProductRel saveProductRel(Boolean isSaveRel, ProductSaveDto productSaveIn, Long productId,
                                           Long tenantId ,LoginInfo baseUser) {
        TenantProductRel tenantProductRel = null;
        //只有共享油站的时候才需要添加关系
        if (isSaveRel) {
            //保存合作关系
            tenantProductRel = new TenantProductRel();
            if(StringUtils.isNotBlank(productSaveIn.getFixedBalance())){
                tenantProductRel.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
            }
            tenantProductRel.setFloatBalance(productSaveIn.getFloatBalance());
            if(StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())){
                tenantProductRel.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
            }
            tenantProductRel.setFloatBalanceBill(productSaveIn.getFloatBalanceBill());
            tenantProductRel.setServiceCharge(productSaveIn.getServiceCharge());
            tenantProductRel.setServiceChargeBill(productSaveIn.getServiceChargeBill());
            tenantProductRel.setLocaleBalanceState(productSaveIn.getLocaleBalanceState());

            tenantProductRel.setProductId(productId);
            if (productSaveIn.getIsShare() != null && productSaveIn.getIsShare() == 1) {
                tenantProductRel.setTenantId(tenantId);
                tenantProductRel.setCooperationState(ServiceConsts.COOPERATION_STATE.IN_COOPERATION);
//                tenantProductRel.setTenantId(PT_TENANT_ID);
            } else {
                tenantProductRel.setTenantId(tenantId);
                tenantProductRel.setCooperationState(ServiceConsts.COOPERATION_STATE.IN_COOPERATION);
            }
            if (productSaveIn.getIsFleet() != null
                    && (productSaveIn.getIsFleet() == ServiceConsts.isFleet.OBMS || productSaveIn.getIsFleet() == ServiceConsts.isFleet.OBMS_SHARE)
            ) {
                tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
            } else {
                if (productSaveIn.getIsProductAudit() == null || productSaveIn.getIsProductAudit()) {
                    tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                }else{
                    tenantProductRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
                }
            }
            if (productSaveIn.getProductType() != null && productSaveIn.getProductType().intValue() == ServiceConsts.PRODUCT_TYPE.ZHAOYOU) {
                save(tenantProductRel);
            }else{
                saveOrUpdate(tenantProductRel, false,baseUser);
            }
            //保存历史记录
            tenantProductRelVerService.saveProductVer(tenantProductRel, productSaveIn, false,baseUser);
        }
        return tenantProductRel;
    }

    @Override
    public List<TenantProductRel> getServiceProductList(Long serviceUserId, Integer serviceType, Long tenantId) {
        return  tenantProductRelMapper.getServiceProductList(serviceUserId, serviceType, tenantId);
    }

    /**
     * 新增修改
     *
     * @param tenantProductRel
     * @param isUpdate
     */
    public void saveOrUpdate(TenantProductRel tenantProductRel, Boolean isUpdate,LoginInfo baseUser) {

        if (isUpdate) {
            tenantProductRel.setUpdateTime(LocalDateTime.now());
            tenantProductRel.setOpId(baseUser.getId());
        } else {
            tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            tenantProductRel.setCreateTime(LocalDateTime.now());
            tenantProductRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
        }
        this.saveOrUpdate(tenantProductRel);
    }



    @Override
    public Integer cooperationNum(Long productId) {
        LambdaQueryWrapper<TenantProductRel> lambda=new QueryWrapper<TenantProductRel>().lambda();
        lambda.eq(TenantProductRel::getProductId,productId)
                .notIn(TenantProductRel::getTenantId,PT_TENANT_ID);
        return this.count(lambda);
    }

    @Override
    public void saveTenantProductRel(TenantProductRel tenantProductRel, Boolean isDel,LoginInfo user) {
        TenantProductRelVer tenantProductRelVer = new TenantProductRelVer();
        BeanUtils.copyProperties(tenantProductRel,tenantProductRelVer);
        if (isDel) {
            tenantProductRelVer.setIsDel(EnumConsts.SERVICE_IS_DEL.YES);
        }
        tenantProductRelVer.setUpdateTime(LocalDateTime.now());
        tenantProductRelVer.setUpdateOpId(user.getId());
        tenantProductRelVer.setRelId(tenantProductRel.getId());
        tenantProductRelVer.setTenantId(user.getTenantId());
        tenantProductRelVerService.saveOrUpdate(tenantProductRelVer);
    }


    @Override
    public ResponseResult delTenantProduct(Long productId,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(user.getTenantId(), productId);
        if(tenantProductRel==null){
            tenantProductRel = tenantProductRelService.getTenantProductRel(1L, productId);
        }
        if (tenantProductRel == null) {
            throw new BusinessException("未找到该站点信息！");
        }
        if (tenantProductRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_YES) {
            throw new BusinessException("该站点不是无效，不能删除！");
        }
        //保存历史表
        saveTenantProductRel(tenantProductRel, true,user);
//        TenantProductRelVer tenantProductRelVer = new TenantProductRelVer();
//        BeanUtils.copyProperties(tenantProductRelVer, tenantProductRel);
//        tenantProductRelVer.setIsDel(EnumConsts.SERVICE_IS_DEL.YES);
//        tenantProductRelSV.saveProductHis(tenantProductRelVer);
        //删除数据
        this.remove(tenantProductRel.getId());


        //记录历史表
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(), SysOperLogConst.OperType.Remove, "移除");
        return ResponseResult.success();
    }

    @Override
    public TenantProductRel update(ProductSaveDto productSaveIn,LoginInfo baseUser) {
        ServiceProduct serviceProduct = serviceProductService.getById(productSaveIn);
        //校验服务商有效性
        serviceInfoService.checkServiceInfo(productSaveIn, serviceProduct.getServiceUserId(), true);
        //校验租户服务商关系有效性
        if (baseUser.getTenantId() != null) {
            tenantServiceRelService.checkTenantService(baseUser.getTenantId(), productSaveIn, true);
        }
        //校验站点有效性
        serviceProductService.checkProduct(productSaveIn, serviceProduct);
        if (serviceProduct.getProductType() != null && serviceProduct.getProductType() == ServiceConsts.PRODUCT_TYPE.ZHAOYOU) {
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
        if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.SERVICE) {
            Integer isBillAbility = serviceProduct.getIsBillAbility();
            BeanUtils.copyProperties(serviceProduct, productSaveIn);
            //共享的时候，需要审核
            if (productSaveIn.getIsShare() == ServiceConsts.IS_SHARE.YES ) {
                if (productSaveIn.getIsProductAudit() == null || productSaveIn.getIsProductAudit()) {
                    serviceProduct.setIsBillAbility(isBillAbility);
                    serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                }else{
                    serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
                }
                //保存历史版本
                serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, true,baseUser);
            }
            serviceProduct.setState(productSaveIn.getProductState());
            serviceProductService.saveOrUpdate(serviceProduct, true,baseUser);


            //改为无效的时候，需要把所有合作车队关系设为无效

            if (this.checkEnumIsNotNull(productSaveIn.getProductState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                loseCooperationTenant(serviceProduct.getId(),baseUser);
            }
            sysOperLogService.saveSysOperLog(baseUser,SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
                    SysOperLogConst.OperType.Update, "服务商修改了站点！");


        } else if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.OBMS_SHARE) {//运营后台共享操作
            serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            serviceProduct.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
            serviceProductService.saveOrUpdate(serviceProduct, true,baseUser);

            serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, false,baseUser);
            sysOperLogService.saveSysOperLog(baseUser,SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
                    SysOperLogConst.OperType.Update, "平台把站点设为共享");
        } else if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.OBMS) {//运营后台站点管理

            serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, true,baseUser);
            serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            serviceProductService.saveOrUpdate(serviceProduct, true,baseUser);
            sysOperLogService.saveSysOperLog(baseUser,SysOperLogConst.BusiCode.ServiceProduct, serviceProduct.getId(),
                    SysOperLogConst.OperType.Update, "操作员：" + baseUser.getName() + "，修改了站点");
        }

        //修改站点与车队关系
        TenantProductRel tenantProductRel = tenantProductRelService.updateProductRel(baseUser,productSaveIn, baseUser.getTenantId(),
                serviceProduct.getId(), serviceProduct.getProductName(), serviceProduct.getServiceUserId(),
                productSaveIn.getIsFleet());
        //设置地理位置 问题
        readisUtil.redisProductAddress(serviceProduct);
        return tenantProductRel;
    }

    @Override
    public void delTenantProduct(Long userId, Integer serviceType, Long tenantId,LoginInfo user) {
        List<TenantProductRel> list = this.getServiceProductList(userId, serviceType, tenantId);

        if (list != null && list.size() > 0) {
            for (TenantProductRel tenantProductRel : list) {
                if (tenantProductRel != null) {
                    //保存历史记录
                    this.saveTenantProductRel(tenantProductRel, true,user);
                    //删除合作关系
                    this.remove(tenantProductRel.getId());
                    sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getId(),
                            SysOperLogConst.OperType.Remove, "删除");
                }
            }
        }
    }

    @Override
    public List<TenantProductRelOutDto> getTenantProductList(Long tenantId, List<Long> productIds) {
        return tenantProductRelMapper.getTenantProductList(tenantId,productIds);
    }

    @Override
    public CooperationProductVo getCooperationProduct(Long relId,String accessToken) {
        if(relId < 0){
            throw new BusinessException("请选择这合作站点！");
        }
        LoginInfo user = loginUtils.get(accessToken);
        TenantProductRel tenantProductRel = this.getById(relId);
        if (tenantProductRel == null) {
            throw new BusinessException("站点与车队合作信息有误！");
        }


        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantProductRel.getTenantId());
        CooperationProductVo map = new CooperationProductVo();

        ServiceProduct serviceProduct = serviceProductService.getById(tenantProductRel.getProductId());
        if (serviceProduct == null) {
            throw new BusinessException("站点信息不存在！");
        }

        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(tenantProductRel.getTenantId(), serviceProduct.getServiceUserId());
        if (tenantServiceRel == null) {
            throw new BusinessException("车队与该商家合作信息有误！");
        }
        map.setTenantName(sysTenantDef.getName());
        map.setLinkMan(sysTenantDef.getActualController());
        map.setLinkPhone(sysTenantDef.getActualControllerPhone());
        map.setOilPrice(0L);
        String priceType = "";
        if (serviceProduct.getProvinceId() != null) {
            long oilPrice=0L;
            if(tenantServiceRel.getIsBill()== SysStaticDataEnum.IS_BILL_ABILITY.YSE) {
                oilPrice = calculationPrice(tenantProductRel.getFloatBalanceBill(), tenantProductRel.getFixedBalanceBill(), oilPriceProvinceService.getOilPrice(serviceProduct.getProvinceId()));
            }else {
                oilPrice = calculationPrice(tenantProductRel.getFloatBalance(), tenantProductRel.getFixedBalance()==null?tenantProductRel.getFixedBalanceBill():tenantProductRel.getFixedBalance(), oilPriceProvinceService.getOilPrice(serviceProduct.getProvinceId()));
            }
            map.setOilPrice(oilPrice);
        }
        map.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.NO);
        if (tenantProductRel.getLocaleBalanceState() != null && tenantProductRel.getLocaleBalanceState() == ServiceConsts.LOCALE_BALANCE_STATE.YES) {
            priceType = "现场价结算";
            map.setLocaleBalanceState(ServiceConsts.LOCALE_BALANCE_STATE.YES);
        }else  if (tenantProductRel.getFixedBalance() != null && tenantProductRel.getFixedBalance() > 0) {
            priceType = "固定价结算";
        } else if (StringUtils.isNotBlank(tenantProductRel.getFloatBalance()) && CommonUtil.isNumber(tenantProductRel.getFloatBalance())) {
            priceType = "浮动价结算";
        }
        ServiceInfo serviceInfo = serviceInfoService.getById(tenantServiceRel.getServiceUserId());
        if(null != serviceInfo){
            map.setServiceType(serviceInfo.getServiceType());
        }
        map.setPriceType(priceType);
        map.setPaymentDays(tenantServiceRel.getPaymentDays());
        map.setBalanceType( tenantServiceRel.getBalanceType());
        map.setBalanceTypeName(new Integer(2).equals(tenantServiceRel.getBalanceType())?"月结":new Integer(1).equals(tenantServiceRel.getBalanceType())?"账期":"无账期");
        map.setPaymentMonth(tenantServiceRel.getPaymentMonth());
        map.setQuotaAmt(tenantServiceRel.getQuotaAmt());
        map.setUseQuotaAmt(tenantServiceRel.getUseQuotaAmt());
        map.setIsBill(tenantServiceRel.getIsBill() == 1 ? "开票" : "不开票");
        map.setStateName(readisUtil.getSysStaticData(EnumConsts.SysStaticData.COOPERATION_STATE, String.valueOf(tenantProductRel.getCooperationState())).getCodeName());
        map.setState(tenantProductRel.getState());
        map.setTenantId(tenantProductRel.getTenantId());
        map.setProductId(tenantProductRel.getProductId());
        SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.getBusiCode(SysOperLogConst.BusiCode.TenantProductRel.getCode());
        List<SysOperLog> logs = sysOperLogService.querySysOperLog(busiCode,
                relId, false, user.getTenantId(),AuditConsts.AUDIT_CODE.SERVICE_PRODUCT,null);
        List<logs> list = new ArrayList<>();
        if (logs != null && logs.size() > 0) {
            for (SysOperLog log : logs) {
                if (log.getOperType() != SysOperLogConst.OperType.Audit.getCode()
                        || log.getOperType() != SysOperLogConst.OperType.AuditUser.getCode()) {
                    logs logMap = new logs();
                    logMap.setOperComment(log.getOperComment());
                    logMap.setCreateDate(log.getCreateTime());
                    logMap.setId(log.getId());
                    list.add(logMap);
                }
            }
        }
        map.setLogs(list);
        return map;
    }

    @Override
    public TenantProductRel getProductRelIsShare(Long tenantId, Long productId) {
        LambdaQueryWrapper<TenantProductRel> lambda= Wrappers.lambdaQuery();
        lambda.in(TenantProductRel::getTenantId, new Long[]{tenantId, PT_TENANT_ID});
        lambda.eq(TenantProductRel::getProductId,productId);
        List<TenantProductRel> tenantProductRels = this.list(lambda);
        TenantProductRel tenantProductRelNew = null;
        if (tenantProductRels != null && tenantProductRels.size() > 0) {
            if (tenantProductRels.size() == 1) {
                tenantProductRelNew = tenantProductRels.get(0);
            } else {
                for (TenantProductRel tenantProductRel : tenantProductRels) {
                    if (tenantProductRel.getTenantId().equals(tenantId)) {
                        tenantProductRelNew = tenantProductRel;
                    }
                }
            }

        }

        return tenantProductRelNew;
    }

    @Override
    public List<TenantProductRel> getTenantProductRelList(Long productId) {
        LambdaQueryWrapper<TenantProductRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenantProductRel::getProductId,productId);
        List<TenantProductRel> tenantProductRels = baseMapper.selectList(wrapper);
        return tenantProductRels;
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
         * 校验枚举
         *
         * @param value
         * @param EnumKey
         * @return
         */
    private boolean checkEnumIsNotNull(Integer value, int EnumKey) {
        if (value != null && value == EnumKey) {
            return true;
        }
        return false;
    }

    @Override
    public TenantProductVo getTenantProduct(Long productId, Integer isShare, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);;
        ServiceProduct serviceProduct = serviceProductService.getById(productId);
        TenantProductVo tenantProductOut = new TenantProductVo();
        TenantProductRel tenantProductRel = null;
        if (isShare == 1) {
            ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(serviceProduct.getServiceUserId());
            UserDataInfo userDataInfo = userDataInfoService.getById(serviceProduct.getServiceUserId());
            tenantProductOut.setServiceUserId(serviceProduct.getServiceUserId());
            tenantProductOut.setLinkman(userDataInfo.getLinkman());
            tenantProductOut.setLoginAcct(userDataInfo.getMobilePhone());
            tenantProductOut.setServiceName(serviceInfo.getServiceName());
            tenantProductOut.setServiceType(serviceInfo.getServiceType());
            tenantProductOut.setIsShare(serviceProduct.getIsShare());
            tenantProductRel =  getTenantProductRel(PT_TENANT_ID, productId);
            if(tenantProductRel == null){
                tenantProductRel =  getTenantProductRel(baseUser.getTenantId(), productId);
            }
        } else {
            tenantProductRel = getTenantProductRel(baseUser.getTenantId(), productId);
            if(tenantProductRel== null){
                tenantProductRel = getTenantProductRel(1L, productId);
            }
        }
        BeanUtils.copyProperties(serviceProduct,tenantProductOut);
        tenantProductOut.setProductId(serviceProduct.getId());
        TenantProductRelVer tenantProductRelVer = null;
        if (tenantProductRel != null) {
            if (isShare == ServiceConsts.IS_SHARE.YES &&serviceProduct.getBusinessType()!=null&& serviceProduct.getBusinessType() == OIL) {
                Long oilPriceProvince = oilPriceProvinceService.getOilPrice(serviceProduct.getProvinceId());
                long oilPrice = oilPriceProvinceService.calculationOilPrice(tenantProductRel, oilPriceProvince, true, false);
                tenantProductOut.setOilPrice(CommonUtil.getDoubleFormatLongMoney(oilPrice, 2, 1));
                if (serviceProduct.getIsBillAbility()!=null&&serviceProduct.getIsBillAbility() == 1) {//需要开票
                    long oilPriceBill = oilPriceProvinceService.calculationOilPrice(tenantProductRel, oilPriceProvince, true, true);
                    tenantProductOut.setOilPriceBill(CommonUtil.getDoubleFormatLongMoney(oilPriceBill, 2, 1));
                }
            }
            tenantProductOut.setCooperationNum(cooperationNum(productId));
            BeanUtils.copyProperties(tenantProductRel,tenantProductOut,"isShare","serviceCall","productName","introduce","provinceId","cityId","countyId","address");
            tenantProductRelVer = tenantProductRelVerService.getTenantProductRelVer(tenantProductRel.getId());
            if(tenantProductRelVer != null){
                if( tenantProductRelVer.getState() != null){
                    String stateName = readisUtil.getSysStaticData(SYS_STATE_DESC,tenantProductRelVer.getState().toString()).getCodeName();
                    tenantProductRelVer.setStateName(stateName);
                }
                tenantProductRelVer.setIsShare(tenantProductRel.getIsShare());
                tenantProductRelVer.setServiceCall(tenantProductRel.getServiceCall());
                tenantProductRelVer.setProductName(tenantProductRel.getProductName());
                tenantProductRelVer.setIntroduce(tenantProductRel.getIntroduce());
                if(tenantProductRel.getProvinceId() != null){
                    tenantProductRelVer.setProvinceId(tenantProductRel.getProvinceId());
                    tenantProductRelVer.setProvinceName(readisUtil.getSysStaticData(SYS_PROVINCE,tenantProductRel.getProvinceId().toString()).getCodeName());
                }
                if(tenantProductRel.getCityId() != null){
                    tenantProductRelVer.setCityId(tenantProductRel.getCityId());
                    tenantProductRelVer.setCityName(readisUtil.getSysStaticData(SYS_CITY, tenantProductRel.getCityId().toString()).getCodeName());
                }
                if(tenantProductRel.getCountyId() != null){
                    tenantProductRelVer.setCountyId(tenantProductRel.getCountyId());
                    tenantProductRelVer.setCountyName(readisUtil.getSysStaticData(SYS_DISTRICT, tenantProductOut.getCountyId().toString()).getCodeName());
                }
                tenantProductRelVer.setAddress(tenantProductRel.getAddress());
            }
            tenantProductOut.setTenantProductRelVer(tenantProductRelVer);

        }
        TenantProductVo productOut = tenantProductRelTransfer.ok(tenantProductOut);
        return productOut;
    }


    @Override
    public TenantProductRel save(ProductSaveDto productSaveIn, LoginInfo baseUser) {
        //校验服务商有效性
        serviceInfoService.checkServiceInfo(productSaveIn, productSaveIn.getServiceUserId(), false);

        //校验租户服务商关系有效性
        if (baseUser.getTenantId() != null) {
            tenantServiceRelService.checkTenantService(baseUser.getTenantId(), productSaveIn, false);
        }
       /* if (productSaveIn.getIsBillAbility() != null && productSaveIn.getIsBillAbility() == 2) {
            productSaveIn.setFixedBalanceBill(null);
            productSaveIn.setFloatBalanceBill(null);
            productSaveIn.setServiceChargeBill(null);
            productSaveIn.setFloatServiceChargeBill(null);
        }*/
        List<ServiceProduct> list = serviceProductService.getServiceProductByName(productSaveIn.getProductName());
        if (list != null && list.size() > 0) {
            throw new BusinessException("站点["+productSaveIn.getProductName()+"]已经注册过，不能重复注册！");
        }


        //保存站点信息
        ServiceProduct serviceProduct = new ServiceProduct();
        BeanUtils.copyProperties(productSaveIn,serviceProduct);
        serviceProduct.setId(null);
        serviceProduct.setLogoId(null);
        serviceProduct.setParentAccountBalance(0L);
        serviceProduct.setIsBillAbility(1);
        if (productSaveIn.getProductType() != null && productSaveIn.getProductType().intValue() == SysStaticDataEnum.PRODUCT_TYPE.ZHAOYOU) {
            serviceProduct.setLogoUrl("image/ZhouYouLogoMin.png");
        }else{
            serviceProduct.setLogoUrl("image/discountsLogo.png");
        }
        serviceProduct.setBusinessType(productSaveIn.getServiceType());
        serviceProduct.setServiceUserId(productSaveIn.getServiceUserId());
        if (productSaveIn.getIsFleet() != null && productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.SERVICE) {
            serviceProduct.setTenantId(ServiceConsts.SERVICE_TENANT_ID);
        } else {
            serviceProduct.setTenantId(baseUser.getTenantId());
        }


        boolean isAuth = false;
        if (productSaveIn.getIsFleet() != null && productSaveIn.getIsFleet() == ServiceConsts.isFleet.OBMS) {
            isAuth = true;
        }
        if (productSaveIn.getIsFleet() != null && productSaveIn.getIsFleet() == ServiceConsts.isFleet.SERVICE
                && productSaveIn.getIsShare() != null && productSaveIn.getIsShare() == ServiceConsts.IS_SHARE.YES) {
            isAuth = true;
        }
        //只有平台新增和服务商共享才需要审核 todo
//        if (isAuth && (productSaveIn.getIsProductAudit() == null || productSaveIn.getIsProductAudit())) {
            serviceProduct.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
            serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
//        } else {
//            serviceProduct.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
//            serviceProduct.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
//        }


        serviceProductService.saveOrUpdata(serviceProduct, false,baseUser);
        //服务商新增的时候，存在子账号就保存
        if (productSaveIn.getIsFleet() == ServiceConsts.isFleet.SERVICE && StringUtils.isNotBlank(productSaveIn.getLoginAcct())) {
//            String childUserId = sysUserService.productSubAccount(productSaveIn.getLoginAcct(), productSaveIn.getLinkman(), serviceProduct.getId(), baseUser);
//            IUserSV userSV = (IUserSV) SysContexts.getBean("userSV");
//            long userId = userSV.saveServerChildUser(productSaveIn.getLoginAcct(), -1L, "", productSaveIn.getLinkman());
//            serviceProduct.setChildAccountUserId(userId);
            String childUserId=   saveChildUserId(productSaveIn.getLoginAcct(), productSaveIn.getLinkman(), serviceProduct.getId(), baseUser);
            serviceProduct.setChildAccountUserId(DataFormat.getLongKey(childUserId));
            serviceProductService.saveOrUpdate(serviceProduct);
        }

        //保存历史记录
        serviceProductVerService.saveServiceProductVer(productSaveIn, serviceProduct, false,baseUser);


        boolean isSaveRel = productSaveIn.getIsFleet()==ServiceConsts.isFleet.TENANT||
                productSaveIn.getIsShare()!=null&&productSaveIn.getIsShare() == ServiceConsts.IS_SHARE.YES;
        //保存 站点与租户关系
        TenantProductRel tenantProductRel = tenantProductRelService.saveProductRel(isSaveRel, productSaveIn,
                serviceProduct.getId(), baseUser.getTenantId(), baseUser);

        //设置地理位置
        readisUtil.redisProductAddress(serviceProduct);
        if (tenantProductRel != null) {
            sysOperLogService.saveSysOperLog(baseUser,SysOperLogConst.BusiCode.TenantProductRel, tenantProductRel.getProductId(),
                    SysOperLogConst.OperType.Add, "新增");
        }
        sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.ServiceProduct,
                serviceProduct.getId(), SysOperLogConst.OperType.Add, "创建了站点");
        productSaveIn.setProductId(serviceProduct.getId());
        return tenantProductRel;
    }

    /**
     * 新增修改
     *
     * @param tenantProductRel
     * @param isUpdate
     */
    public void saveOrUpdate(TenantProductRel tenantProductRel, boolean isUpdate, LoginInfo baseUser) {


        /*if(null != tenantProductRel.getFixedBalanceBill() && tenantProductRel.getFixedBalanceBill().longValue() != -1 && tenantProductRel.getFixedBalanceBill().longValue() <= 0){
            throw new BusinessException("价格必须大于0");
        }
        if(StringUtils.isNotBlank(tenantProductRel.getFloatBalanceBill()) && CommonUtil.isNumber(tenantProductRel.getFloatBalanceBill()) && Double.parseDouble(tenantProductRel.getFloatBalanceBill()) <= 0){
            throw new BusinessException("价格必须大于0");
        }


        if(null != tenantProductRel.getFixedBalance() && tenantProductRel.getFixedBalance().longValue() != -1 && tenantProductRel.getFixedBalance().longValue() <= 0){
            throw new BusinessException("价格必须大于0");
        }
        if(CommonUtil.isNumberNotNull(tenantProductRel.getFloatBalance()) && Double.parseDouble(tenantProductRel.getFloatBalance()) <= 0){
            throw new BusinessException("价格必须大于0");
        }

        if(CommonUtil.isNumberNotNull(tenantProductRel.getServiceCharge()) && Double.parseDouble(tenantProductRel.getServiceCharge()) <= 0){
            throw new BusinessException("服务费必须大于0");
        }

        if(CommonUtil.isNumberNotNull(tenantProductRel.getServiceChargeBill()) && Double.parseDouble(tenantProductRel.getServiceChargeBill()) <= 0){
            throw new BusinessException("服务费必须大于0");
        }*/


        if (isUpdate) {
            tenantProductRel.setUpdateTime(LocalDateTime.now());
            tenantProductRel.setOpId(baseUser.getId());
            this.update(tenantProductRel);
        } else {
            tenantProductRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            tenantProductRel.setCreateTime(LocalDateTime.now());
            tenantProductRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
            this.save(tenantProductRel);
        }

    }
    /**
     * 获取当前用户工具
     * @param accessToken
     * @return
     */

    /**
     *
     * */
    private String  saveChildUserId(String loginAcct, String linkman, Long productId, LoginInfo baseUser){
        Map map = this.checkChildLoginAcct(loginAcct, baseUser);
        //returnResult 1:不存在账号  2：已经存在该账号并且是本服务商下的，3：已经存在该站点，不属于当前服务商下的站点
        int returnResult = DataFormat.getIntKey(map,"returnResult");
        long userId = DataFormat.getLongKey(map,"userId");
        if(returnResult == 2){
            userId = DataFormat.getLongKey(map,"userId");
        }else if(returnResult == 3){
            throw new BusinessException("该账号已经被其他服务商注册！");
        }
        ServiceProduct serviceProduct = serviceProductService.getById(productId);
        userId = sysUserService.saveServerChildUser(loginAcct, userId, "", linkman);
        //只有新增的时候才需要更新站点表
        serviceProduct.setChildAccountUserId(userId);
        serviceProductService.saveOrUpdata(serviceProduct, true,baseUser);
        return String.valueOf(userId);

    }
    public Map checkChildLoginAcct(String loginAcct, LoginInfo baseUser) {
        try {
            if(StringUtils.isBlank(loginAcct)){
                throw new BusinessException("请输入账号！");
            }else if(!CommonUtil.isCheckMobiPhone(loginAcct)){
                throw new BusinessException("请输入正确的手机号码！");
            }
            SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(null, loginAcct);
            Map<String,Object> map = new HashMap<>();
            if(sysOperator == null){
                map.put("returnResult",1);
                return map;
            }
            //判断是否服务商
            boolean isService = serviceInfoService.isService(sysOperator.getUserInfoId());
            if(isService){
                if(sysOperator.getId().equals(baseUser.getId())) {
                    map.put("returnResult",2);
                }else {
                    map.put("returnResult",3);
                }
            }else {
                //判断是否服务商子账号
                List<ServiceProduct> list = serviceProductService.getServiceProductByChild(sysOperator.getUserInfoId());
                if(list == null || list.size() == 0){
                    map.put("returnResult",1);
                }else {
                    if(list.get(0).getServiceUserId()==baseUser.getId()) {
                        map.put("returnResult",2);
                    }else {
                        map.put("returnResult",3);
                    }
                }

            }

            map.put("userId",sysOperator.getUserInfoId());
            map.put("loginAcct",sysOperator.getLoginAcct());
            SysUser sysUser = sysUserService.getById(sysOperator.getId());
            map.put("linkman",sysUser.getOpName());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
