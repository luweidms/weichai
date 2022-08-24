package com.youming.youche.finance.provider.service.payable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.finance.domain.payable.CheckPasswordErr;
import com.youming.youche.finance.provider.mapper.payable.CheckPasswordErrMapper;
import com.youming.youche.finance.api.payable.ICheckPasswordErrService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author zag
* @since 2022-04-07
*/
@DubboService(version = "1.0.0")
public class CheckPasswordErrServiceImpl extends BaseServiceImpl<CheckPasswordErrMapper, CheckPasswordErr> implements ICheckPasswordErrService {


    @Override
    public CheckPasswordErr getCheckInfo(long userId, Integer pwdType) {
        LambdaQueryWrapper<CheckPasswordErr> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CheckPasswordErr::getUserId, userId)
                .eq(CheckPasswordErr::getCheckType, pwdType)
                .between(CheckPasswordErr::getCheckDate, DateUtil.getCurrDateTimeBegin(), DateUtil.getCurrDateTimeEnd());
        CheckPasswordErr checkPasswordErr = null;
        List<CheckPasswordErr> list = baseMapper.selectList(queryWrapper);
        if (list.size() > 0) {
            checkPasswordErr = (CheckPasswordErr) list.get(0);
        }
        return checkPasswordErr;
    }
}
