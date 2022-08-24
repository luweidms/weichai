package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOilCardInfoService;
import com.youming.youche.order.domain.order.OilCardInfo;
import com.youming.youche.order.provider.mapper.order.OilCardInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OilCardInfoServiceImpl extends BaseServiceImpl<OilCardInfoMapper, OilCardInfo> implements IOilCardInfoService {


    @Override
    public Integer getOilCardInfo(OilCardInfo oilCardInfo) {
        LambdaQueryWrapper<OilCardInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(OilCardInfo::getOilCardNum, oilCardInfo.getOilCardNum());
        if (null != oilCardInfo.getOilCardType()) {
            lambda.eq(OilCardInfo::getOilCardType, oilCardInfo.getOilCardType());
        }
        if(null != oilCardInfo.getEtcCardType()){
            lambda.eq(OilCardInfo::getEtcCardType,oilCardInfo.getEtcCardType());
            lambda.notIn(OilCardInfo::getState,6,7);
        }
        return this.count(lambda);
    }
}
