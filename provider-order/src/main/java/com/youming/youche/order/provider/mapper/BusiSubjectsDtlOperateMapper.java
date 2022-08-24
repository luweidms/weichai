package com.youming.youche.order.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.BusiSubjectsDtlOperate;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-06-13
*/
    public interface BusiSubjectsDtlOperateMapper extends BaseMapper<BusiSubjectsDtlOperate> {

        List<BusiSubjectsDtlOperate> queryBusiSubjectsDtlOperate(Long dtlId);

    }
