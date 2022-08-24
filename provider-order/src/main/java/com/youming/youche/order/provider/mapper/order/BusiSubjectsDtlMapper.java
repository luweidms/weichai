package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author liangyan
* @since 2022-03-23
*/
    public interface BusiSubjectsDtlMapper extends BaseMapper<BusiSubjectsDtl> {

        List<BusiSubjectsDtl> queryBusiSubjectsDtl(long businessId, long subjectsId);
    }
