package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.provider.mapper.SysAttachMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 图片资源表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-17
 */
@DubboService(version = "1.0.0")
public class SysAttachServiceImpl extends BaseServiceImpl<SysAttachMapper, SysAttach> implements ISysAttachService {


    @Override
    public Long saveById(SysAttach sysAttach) {
        super.save(sysAttach);
        return sysAttach.getId();
    }


    @Override
    public List<SysAttach> selectAllByBusinessIdAndBusinessCode(Long businessId, Integer businessCode) {

        return baseMapper.selectAllByBusinessIdAndBusinessCode(businessId,businessCode);
    }

    @Override
    public List<SysAttach> selectAllInfoByIds(List<Long> flowIds) {
        LambdaQueryWrapper<SysAttach> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysAttach::getId, flowIds);

        List<SysAttach> list = this.list();
        return list;
    }

    @Override
    public SysAttach getAttachByFlowId(Long flowId) {
        SysAttach sysAttach = null;
        SysAttach byId = this.getById(flowId);
        sysAttach = byId;
        return sysAttach;
    }
}
