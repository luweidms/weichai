package com.youming.youche.record.provider.service.impl.oa;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.oa.IOaFilesService;
import com.youming.youche.record.domain.oa.OaFiles;
import com.youming.youche.record.provider.mapper.oa.OaFilesMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @author hzx
 * @date 2022/4/18 14:14
 */
@DubboService(version = "1.0.0")
public class OaFilesServiceImpl extends BaseServiceImpl<OaFilesMapper, OaFiles> implements IOaFilesService {

    public static final int RELTYPE6 = 6;//维修保养

    @Override
    public List<OaFiles> getRepairOrderPicList(List<Long> flowIdList) {
        LambdaQueryWrapper<OaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OaFiles::getRelId, flowIdList);
        queryWrapper.eq(OaFiles::getRelType, RELTYPE6);
        return this.list(queryWrapper);
    }

    @Override
    public List<OaFiles> queryOaFilesById(Long LId) {
        LambdaQueryWrapper<OaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OaFiles::getRelId, LId);
        return this.list(queryWrapper);
    }

    @Override
    public List<OaFiles> getRepairOrderPicListById(Long flowId) {
        LambdaQueryWrapper<OaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OaFiles::getRelId, flowId);
        queryWrapper.eq(OaFiles::getRelType, RELTYPE6);
        return this.list(queryWrapper);
    }


}
