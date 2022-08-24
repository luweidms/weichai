package com.youming.youche.finance.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;

import com.youming.youche.finance.api.IOilTurnEntityService;
import com.youming.youche.finance.domain.OilTurnEntity;
import com.youming.youche.finance.provider.mapper.OilTurnEntityMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Date;


/**
* <p>
    *  服务实现类
    * </p>
* @author luona
* @since 2022-04-20
*/
@DubboService(version = "1.0.0")
public class OilTurnEntityServiceImpl extends BaseServiceImpl<OilTurnEntityMapper, OilTurnEntity> implements IOilTurnEntityService {
    @Resource
    OilTurnEntityMapper oilTurnEntityMapper;

    @Override
    public OilTurnEntity getOilTurnEntity(Long id) {
        OilTurnEntity oilTurnEntity = oilTurnEntityMapper.getOilTurnEntity(id);
        return oilTurnEntity;
    }

    @Override
    public int updateById(Long id, Integer state, Date verificationDate, Long noVerificationAmount) {
        int i = oilTurnEntityMapper.updateById(id, state, verificationDate, noVerificationAmount);
        return i;
    }
}
