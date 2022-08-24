package com.youming.youche.market.provider.service.youka;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IServiceProductOilsService;
import com.youming.youche.market.domain.youka.OilCardInfo;
import com.youming.youche.market.domain.youka.ServiceProductOils;
import com.youming.youche.market.provider.mapper.youka.ServiceProductOilsMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author XXX
* @since 2022-03-24
*/
@DubboService(version = "1.0.0")
    public class ServiceProductOilsServiceImpl extends BaseServiceImpl<ServiceProductOilsMapper, ServiceProductOils> implements IServiceProductOilsService {


    @Override
    public long getOilCardInfo(OilCardInfo oilCardInfo) {
        int count =0;
        if (null != oilCardInfo.getOilCardType() && null != oilCardInfo.getEtcCardType()){
              count =baseMapper.count(oilCardInfo.getOilCardNum(),oilCardInfo.getOilCardType(),oilCardInfo.getEtcCardType());
        }
        return count;
    }

    @Override
    public List<ServiceProductOils> getServiceProductOils(String oilsId) {
        LambdaQueryWrapper<ServiceProductOils> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceProductOils::getOilsId, oilsId);
        List<ServiceProductOils> list = this.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override
    public ServiceProductOils getServiceProductOilsByOilsId(String oilsId) {

        List<ServiceProductOils> list = this.getServiceProductOils(oilsId);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);

    }

}
