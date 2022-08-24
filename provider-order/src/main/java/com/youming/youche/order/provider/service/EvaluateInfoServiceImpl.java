package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.order.api.order.other.IEvaluateInfoService;
import com.youming.youche.order.domain.EvaluateInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.provider.mapper.EvaluateInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author zengwen
* @since 2022-05-23
*/
@DubboService(version = "1.0.0")
    public class EvaluateInfoServiceImpl extends BaseServiceImpl<EvaluateInfoMapper, EvaluateInfo> implements IEvaluateInfoService {


    @Override
    public List<EvaluateInfo> queryEvaluateInfo(Long id, Long busiId, Integer evaluateBusiType) {
        LambdaQueryWrapper<EvaluateInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null && id > 0) {
            queryWrapper.eq(EvaluateInfo::getId, id);
        }
        if (busiId != null && busiId > 0) {
            queryWrapper.eq(EvaluateInfo::getBusiId, busiId);
        }
        if (evaluateBusiType != null && evaluateBusiType > 0) {
            queryWrapper.eq(EvaluateInfo::getEvaluateBusiType, evaluateBusiType);
        }
        return baseMapper.selectList(queryWrapper);
    }
}
