//package com.youming.youche.market.provider.service.facilitator;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.youming.youche.commons.base.BaseServiceImpl;
//import com.youming.youche.conts.SysStaticDataEnum;
//import com.youming.youche.market.api.facilitator.*;
//import com.youming.youche.market.domain.facilitator.*;
//import com.youming.youche.market.provider.mapper.facilitator.SysTenantDefMapper;
//import com.youming.youche.market.vo.facilitator.SysTenantOutVo;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//
//
///**
// * <p>
// * 车队表 服务实现类
// * </p>
// *
// * @author CaoYaJie
// * @since 2022-02-07
// */
//@DubboService(version = "1.0.0")
//@Service
//public class SysTenantDefServiceImpl extends BaseServiceImpl<SysTenantDefMapper, SysTenantDef> implements ISysTenantDefService {
//    @Resource
//     private IBillSettingService billSettingService;
//    @Resource
//    private IBillPlatformService billPlatformService;
//    @Resource
//    private ISysTenantBusinessStateService businessStateService;
//    @Resource
//    private IUserDataInfoMarketService userDataInfoService;
//    @Resource
//    private SysTenantDefMapper sysTenantDefMapper;
//
//    @Override
//    public SysTenantDef getSysTenantDefByAdminUserId(Long userId) {
//        LambdaQueryWrapper<SysTenantDef> lambda=new QueryWrapper<SysTenantDef>().lambda();
//        lambda.eq(SysTenantDef::getAdminUser,userId)
//                .eq(SysTenantDef::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
//        SysTenantDef sysTenantDef = this.getOne(lambda);
//        return sysTenantDef;
//    }
//
//    @Override
//    public SysTenantDef getSysTenantDefByAdminUserId(Long userId, Boolean isAllTenant) {
//        if (!isAllTenant) {
//            return getSysTenantDefByAdminUserId(userId);
//        }
//        LambdaQueryWrapper<SysTenantDef> lambda=new QueryWrapper<SysTenantDef>().lambda();
//        lambda.eq(SysTenantDef::getAdminUser,userId);
//        SysTenantDef sysTenantDef = this.getOne(lambda);
//        return sysTenantDef;
//    }
//
//    @Override
//    public SysTenantOutVo getTenantById(Long tenantId) {
//        LambdaQueryWrapper<SysTenantDef> lambda=new QueryWrapper<SysTenantDef>().lambda();
//        lambda.eq(SysTenantDef::getId,tenantId);
//        //获取车队基本信息
//        SysTenantDef sysTenantDef = this.getOne(lambda);
//        SysTenantOutVo sysTenantOut = new SysTenantOutVo();
//        BeanUtils.copyProperties(sysTenantDef,sysTenantOut);
//        sysTenantOut.setCompanyName(sysTenantDef.getName());
//
//        //查出票据信息
//        BillSetting billSetting = billSettingService.getBillSetting(sysTenantDef.getId());
//        sysTenantOut.setBillSetting(billSetting);
//
//        //票据平台名称
//        if(billSetting!=null&&billSetting.getBillMethod()>0) {
//            BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(billSetting.getBillMethod());
//            if(billPlatform!=null){
//                sysTenantOut.setPlateName(billPlatform.getPlatName());
//            }
//        }
//
//        SysTenantBusinessState businessState = businessStateService.queryByTenantId(sysTenantDef.getId());
//        if(businessState.getStaffNumber()==null){
//            businessState.setStaffNumber(0);
//        }
//        if(businessState.getAnnualTurnover()==null){
//            businessState.setAnnualTurnover(Double.valueOf(0));
//        }
//        sysTenantOut.setBusinessState(businessState);
//
//        //查出超管信息
//        UserDataInfo userDataInfo = userDataInfoService.getById(sysTenantDef.getAdminUser());
//        sysTenantOut.setLinkManIdentification(userDataInfo.getIdentification());
//        sysTenantOut.setAdminUserName(userDataInfo.getLinkman());
//
//        if (null != sysTenantDef.getPreSaleServiceId()) {
//            UserDataInfo preSaleUser = userDataInfoService.getById(sysTenantDef.getPreSaleServiceId());
//            sysTenantOut.setPreSaleServicePhone(preSaleUser.getMobilePhone());
//            sysTenantOut.setPreSaleServiceName(preSaleUser.getLinkman());
//        }
//        if (null != sysTenantDef.getAfterSaleServiceId()) {
//            UserDataInfo afterSaleUser = userDataInfoService.getById(sysTenantDef.getAfterSaleServiceId());
//            sysTenantOut.setAfterSaleServicePhone(afterSaleUser.getMobilePhone());
//            sysTenantOut.setAfterSaleServiceName(afterSaleUser.getLinkman());
//        }
//
//        return sysTenantOut;
//    }
//
//
//
//    @Override
//    public SysTenantDef getSysTenantDef(long tenantId, boolean isAllTenant) {
//        if (!isAllTenant) {
//            return this.getSysTenantDef(tenantId);
//        }
//        SysTenantDef one = this.getById(tenantId);
//        if (one != null) {
//            return one;
//        }
//        return null;
//    }
//
//    @Override
//    public SysTenantDef getSysTenantDef(long tenantId) {
//        LambdaQueryWrapper<SysTenantDef> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SysTenantDef::getId, tenantId);
//        queryWrapper.eq(SysTenantDef::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
//        SysTenantDef one = this.getOne(queryWrapper);
//        if (one != null) {
//            return one;
//        }
//        return null;
//    }
//
//    @Override
//    public Long getTenantAdminUser(Long tenantId) {
//        SysTenantDef sysTenantDef = this.getSysTenantDef(tenantId, true);
//        if(sysTenantDef == null){
//            return null;
//
//        }
//        return sysTenantDef.getAdminUser();
//    }
//}
