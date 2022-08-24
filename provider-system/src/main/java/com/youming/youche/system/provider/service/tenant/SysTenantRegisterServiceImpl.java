package com.youming.youche.system.provider.service.tenant;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysOperLogConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.tenant.ISysTenantRegisterService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.tenant.SysTenantRegister;
import com.youming.youche.system.provider.mapper.tenant.SysTenantRegisterMapper;
import com.youming.youche.system.vo.TenantRegisterQueryVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * <p>
 * 车队注册表，用于收集车队信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@DubboService(version = "1.0.0")
public class SysTenantRegisterServiceImpl extends BaseServiceImpl<SysTenantRegisterMapper, SysTenantRegister> implements ISysTenantRegisterService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    private ISysOperLogService sysOperLogService;

    @Override
    public SysTenantRegister getTenantByRegisterId(Long registerId) {
        return super.getById(registerId);
    }

    @Override
    public IPage<SysTenantRegister> queryTenantRegister(TenantRegisterQueryVo tenantRegisterQueryVo, Integer pageNum, Integer pageSize) {
        if (null == tenantRegisterQueryVo) {
            return null;
        }
        LambdaQueryWrapper<SysTenantRegister> queryWrapper =  Wrappers.lambdaQuery();
        if(StrUtil.isNotEmpty(tenantRegisterQueryVo.getActualController()))
        {
            queryWrapper.eq(SysTenantRegister::getActualController,tenantRegisterQueryVo.getActualController());
        }
        if(StrUtil.isNotEmpty(tenantRegisterQueryVo.getCompanyName())){
            queryWrapper.like(SysTenantRegister::getCompanyName,tenantRegisterQueryVo.getCompanyName());
        }
        if(StrUtil.isNotEmpty(tenantRegisterQueryVo.getEndDate())){
            queryWrapper.lt(SysTenantRegister::getCreateTime,tenantRegisterQueryVo.getEndDate());
        }
        if(StrUtil.isNotEmpty(tenantRegisterQueryVo.getStartDate())){
            queryWrapper.gt(SysTenantRegister::getCreateTime,tenantRegisterQueryVo.getStartDate());
        }
        if(StrUtil.isNotEmpty(tenantRegisterQueryVo.getLinkPhone())){
            queryWrapper.eq(SysTenantRegister::getLinkPhone,tenantRegisterQueryVo.getLinkPhone());
        }
        if( null != tenantRegisterQueryVo.getAuditState()){
            queryWrapper.eq(SysTenantRegister::getAuditState,tenantRegisterQueryVo.getAuditState());
        }
        Page<SysTenantRegister> userDataInfoPage = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(userDataInfoPage,queryWrapper);
    }

    @Transactional
    @Override
    public void audit(Long id, Integer auditState, String auditContent,String accessToken) {
        if (null == auditState || (auditState < 1 && auditState > 3)) {
            throw new BusinessException("错误的审核操作");
        }
        SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
        SysTenantRegister sysTenantRegister = super.getById(id);
        sysTenantRegister.setAuditState(auditState);
        sysTenantRegister.setAuditContent(auditContent);
        sysTenantRegister.setBuildState(SysStaticDataEnum.TENANT_BUILD_STATE.UNBUILT);
        sysTenantRegister.setAuditContentName(sysUser.getName());
        super.update(sysTenantRegister);
        saveSysOperLog(SysOperLogConst.BusiCode.TenantRegister, SysOperLogConst.OperType.Audit, auditContent, accessToken);
    }

    /**
     * 记录日志
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }
}
