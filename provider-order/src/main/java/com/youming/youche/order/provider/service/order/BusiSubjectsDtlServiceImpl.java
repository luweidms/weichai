package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IBusiSubjectsDtlService;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.provider.mapper.order.BusiSubjectsDtlMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class BusiSubjectsDtlServiceImpl extends BaseServiceImpl<BusiSubjectsDtlMapper, BusiSubjectsDtl> implements IBusiSubjectsDtlService {


    @Override
    public List<BusiSubjectsDtl> queryBusiSubjectsDtl(Long businessId, Long subjectsId) {
        LambdaQueryWrapper<BusiSubjectsDtl> lambda= Wrappers.lambdaQuery();
        lambda.eq(BusiSubjectsDtl::getBusinessId,businessId)
              .eq(BusiSubjectsDtl::getSubjectsId,subjectsId)
              .orderByAsc(BusiSubjectsDtl::getSortId);
        return this.list(lambda);
    }
}
