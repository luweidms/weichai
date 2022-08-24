package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.record.api.tenant.ITenantStaffRelService;
import com.youming.youche.record.domain.tenant.TenantStaffRel;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.record.provider.mapper.tenant.TenantStaffRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-21
 */
@DubboService(version = "1.0.0")
public class TenantStaffRelServiceImpl extends ServiceImpl<TenantStaffRelMapper, TenantStaffRel> implements ITenantStaffRelService {


    @Resource
    TenantStaffRelMapper tenantStaffRelMapper;
    @Resource
    LoginUtils loginUtils;

    @Override
    public List<TenantStaffRel> getStaffRel(Long userId) {
        QueryWrapper<TenantStaffRel> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_info_id",userId);
        queryWrapper.eq("STATE",1);
        List<TenantStaffRel> list=tenantStaffRelMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public void updateStaffInfo(TenantStaffRel tenantStaffRel){
        this.updateById(tenantStaffRel);
    }

    @Override
    public List<String> queryStaffInfo(String phone) {
        return tenantStaffRelMapper.queryStaffInfo(phone);
    }

    @Override
    public List<StaffDataInfoDto> queryStaffInfoList(StaffDataInfoDto staffDataInfoDto) {
        List<StaffDataInfoDto> listOut = tenantStaffRelMapper.selectPeople(staffDataInfoDto);
        return listOut;
    }

    @Override
    public List<TenantStaffRel> getTenantStaffRel(Long userId) {
        LambdaQueryWrapper<TenantStaffRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantStaffRel::getUserInfoId, userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<TenantStaffRel> getTenantStaffRelByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("没有传入用户编号！");
        }
        List<TenantStaffRel> list = baseMapper.getTenantStaffRelByUserId(userId);

        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }

}
