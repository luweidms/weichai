package com.youming.youche.finance.provider.service.ac;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewExtService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNewExt;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryInfoNewExtMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryInfoNewMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author: luona
 * @date: 2022/5/12
 * @description: T0DO
 * @version: 1.0
 */
@DubboService(version = "1.0.0")
public class CmSalaryInfoNewExtServiceImpl extends BaseServiceImpl<CmSalaryInfoNewExtMapper, CmSalaryInfoNewExt> implements ICmSalaryInfoNewExtService {

    @Override
    public CmSalaryInfoNewExt getCmSalaryExtBysalaryId(Long salaryId) {
        LambdaQueryWrapper<CmSalaryInfoNewExt> lambda= Wrappers.lambdaQuery();
        lambda.eq(CmSalaryInfoNewExt::getSalaryId,salaryId)
               .orderByDesc(CmSalaryInfoNewExt::getId)
               .last("limit 1");
        return this.getOne(lambda);
    }
}
