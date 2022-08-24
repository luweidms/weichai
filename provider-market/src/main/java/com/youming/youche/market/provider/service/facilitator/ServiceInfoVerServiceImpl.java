package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceInfoVerService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.api.facilitator.IUserDataInfoVerService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceInfoVer;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;
import com.youming.youche.market.dto.facilitator.ServiceSaveInDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInfoVerMapper;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import com.youming.youche.system.api.ISysOperLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.youming.youche.conts.EnumConsts.STATE.STATE_YES;


/**
 * <p>
 * 服务商版本表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-26
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInfoVerServiceImpl extends BaseServiceImpl<ServiceInfoVerMapper,ServiceInfoVer> implements IServiceInfoVerService {
    @Resource
    private IServiceInfoService serviceInfoService;
    @Resource
    private IUserDataInfoVerService userDataInfoVerService;
    @Resource
    private IUserDataInfoMarketService userDataInfoService;
    @Resource
    private LoginUtils loginUtils;
    @Lazy
    @Resource
    private IServiceProductService serviceProductService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public void saveServiceInfoHis(ServiceInfoVer serviceInfoVer, LoginInfo baseUser) {
        serviceInfoVer.setUpdateTime(LocalDateTime.now());
        serviceInfoVer.setOpId(baseUser.getId());
        this.saveOrUpdate(serviceInfoVer);
    }

    @Override
    public void saveServiceInfoHis(ServiceSaveInDto serviceSaveIn, Long tenantId, Integer isAuth,LoginInfo baseUser) {
        ServiceInfoVer serviceInfoVer=new ServiceInfoVer();
        serviceInfoVer.setTenantId(tenantId);
        BeanUtils.copyProperties(serviceSaveIn,serviceInfoVer);
        serviceInfoVer.setLogoId(null);
        serviceInfoVer.setLogoUrl("image/discountsLogo.png");
        serviceInfoVer.setIsDel(EnumConsts.SERVICE_IS_DEL.NO);
        serviceInfoVer.setIsAuth(isAuth);
        this.saveServiceInfoHis(serviceInfoVer,baseUser);
    }

    @Override
    public ServiceInfoVer getServiceInfoVer(Long serviceUserId) {
        LambdaQueryWrapper<ServiceInfoVer> lambda=new QueryWrapper<ServiceInfoVer>().lambda();
        lambda.eq(ServiceInfoVer::getServiceUserId,serviceUserId)
                .orderByDesc(ServiceInfoVer::getId)
                .last("limit 1");
        return this.getOne(lambda);
    }

    @Override
    @Transactional
    public ResponseResult auditServiceInfo(Long serviceUserId, Boolean isPass, String auditReason, String accessToken ) {
        if(serviceUserId==null||serviceUserId<0) {
//            return  ResponseResult.failure("服务商ID不能为空");
            throw new BusinessException("服务商ID不能为空");
        }
        if(StringUtils.isBlank(auditReason)) {
//            return ResponseResult.failure("请输入审核意见");
            auditReason="";
        }
        ServiceInfo serviceInfo =serviceInfoService.getServiceInfoByServiceUserId(serviceUserId);
        if(serviceInfo==null||serviceInfo.getServiceUserId()==null) {
            return ResponseResult.failure("未找到对应服务商信息");
        }
        if(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO==serviceInfo.getState()) {
            return ResponseResult.failure("该服务商已停用，不能审核!");
        }
        LoginInfo baseUser = loginUtils.get(accessToken);;

        serviceInfo.setAuthReason(auditReason);
        serviceInfo.setAuthDate(LocalDateTimeUtil.presentTime());
        serviceInfo.setAuthManId(baseUser.getId().toString());
        serviceInfo.setAuthManName(baseUser.getName());

        //审核通过
        if(isPass) {
            //1.同步服务商信息
            ServiceInfoVer serviceInfoVer=getServiceInfoVer(serviceUserId);
            Long id = serviceInfo.getId();
            LocalDateTime createTime = serviceInfo.getCreateTime();
            if(serviceInfoVer!=null) {
                BeanUtils.copyProperties(serviceInfoVer,serviceInfo);
            }
            serviceInfo.setCreateTime(createTime);
            serviceInfo.setId(id);
            serviceInfo.setAuthFlag(SysStaticDataEnum.EXPENSE_STATE.END);
            serviceInfo.setIsAuth(SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS);
            serviceInfo.setState(STATE_YES);
            serviceInfo.setAuthReason(null);
            //不支持开票的情况下,修改服务商站点也不能开票
            if(serviceInfo.getIsBillAbility()!=1) {
                serviceProductService.notBillAbility(serviceInfo.getServiceUserId(),baseUser);
            }

        }else {
            serviceInfo.setAuthFlag(SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN);
        }

        //同步用户信息
        userDataInfoService.doAuditServ(serviceUserId,isPass,auditReason,baseUser);


        //更新服务商信息
        serviceInfoService.doSaveOrUpdate(serviceInfo, true,baseUser);
        if(StringUtils.isNotBlank(auditReason)){
         LambdaUpdateWrapper<ServiceInfo> lambda= Wrappers.lambdaUpdate();
            lambda.set(ServiceInfo::getAuthReason,auditReason)
                  .eq(ServiceInfo::getId,serviceInfo.getId());
            serviceInfoService.update(lambda);
        }

        //添加操作日志
        sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.ServiceInfo,serviceUserId, SysOperLogConst.OperType.Audit,"服务商档案审核"+(isPass?"通过":"不通过")+";"+(StringUtils.isBlank(auditReason)?"":"原因：["+auditReason+"]"));
        return ResponseResult.success("审核成功");
    }

    @Override
    public UserDataInfoVer doGetServ(Long userId) {
        if(userId<=0){
            throw new BusinessException("用户编号不能为空！");

        }
        UserDataInfoVer ver = userDataInfoVerService.getUserDataInfoVerNoTenant(userId);
        return ver;
    }

    @Override
    public ServiceInfoVo getServiceInfoVer(Long serviceUserId,LoginInfo user) {
        if(serviceUserId==null||serviceUserId<=0) {
            throw new BusinessException("请输入服务商用户ID!");
        }
//        if(SysStaticDataEnum.PT_TENANT_ID!=user.getTenantId()) {
//            throw new BusinessException("当前用户不能查看平台服务商信息!");
//        }

        ServiceInfoVer serviceInfoVer = null;
        ServiceInfoVo serviceInfoOut = new ServiceInfoVo();
        ServiceInfo serviceInfo= serviceInfoService.getServiceInfoByServiceUserId(serviceUserId);
        if(serviceInfo!=null&&serviceInfo.getAuthFlag()!=null&&serviceInfo.getAuthFlag()==SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
            serviceInfoVer =getServiceInfoVer(serviceUserId);

            BeanUtils.copyProperties(serviceInfoVer,serviceInfoOut);

        }else if(serviceInfo!=null) {
            BeanUtils.copyProperties(serviceInfo,serviceInfoOut);
        }

        UserDataInfoVer userDataInfoVer = doGetServ(serviceUserId);
        if(userDataInfoVer != null){
            BeanUtils.copyProperties(userDataInfoVer,serviceInfoOut);
        }else {
            UserDataInfo userDataInfo = userDataInfoService.getById(serviceUserId);
            BeanUtils.copyProperties(userDataInfo,serviceInfoOut);
        }

        return serviceInfoOut;
    }
    /**
     * 获取当前用户工具
     * @param accessToken
     * @return
     */

}
