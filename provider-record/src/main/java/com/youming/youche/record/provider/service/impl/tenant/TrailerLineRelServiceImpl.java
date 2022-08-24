package com.youming.youche.record.provider.service.impl.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.trailer.ITrailerLineRelService;
import com.youming.youche.record.domain.trailer.TrailerLineRel;
import com.youming.youche.record.provider.mapper.trailer.TrailerLineRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @version:
 * @Title: TrailerLineRelServiceImpl
 * @Package: com.youming.youche.record.provider.service.impl.tenant
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/28 14:03
 * @company:
 */
@DubboService(version = "1.0.0")
public class TrailerLineRelServiceImpl extends ServiceImpl<TrailerLineRelMapper, TrailerLineRel> implements ITrailerLineRelService {

    @Override
    public boolean delLineRelList(Long id) {
        LambdaQueryWrapper<TrailerLineRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TrailerLineRel::getTrailerId,id);
        return remove(wrapper);
    }
}
