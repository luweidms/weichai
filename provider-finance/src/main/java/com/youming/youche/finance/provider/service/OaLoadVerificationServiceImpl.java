package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.finance.api.IOaLoadVerificationService;
import com.youming.youche.finance.domain.OaLoadVerification;
import com.youming.youche.finance.provider.mapper.OaLoadVerificationMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author Terry
* @since 2022-03-09
*/
@DubboService(version = "1.0.0")
    public class OaLoadVerificationServiceImpl extends BaseServiceImpl<OaLoadVerificationMapper, OaLoadVerification> implements IOaLoadVerificationService {


    @Resource
    private LoginUtils loginUtils;
    @Override
    public List<OaLoadVerification> queryOaLoadVerificationsById(Long Lid) {
        List<OaLoadVerification> oaLoadVerifications = baseMapper.queryOaLoadVerificationsById(Lid);
        return oaLoadVerifications;
    }

    @Override
    public List<OaLoadVerification> getOaLoadVerificationsByLId(Long LId) {
        LambdaQueryWrapper<OaLoadVerification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OaLoadVerification::getLId, LId);
        List<OaLoadVerification> list = this.list(queryWrapper);
        return list;
    }

}
