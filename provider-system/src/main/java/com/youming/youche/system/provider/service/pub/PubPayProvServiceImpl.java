package com.youming.youche.system.provider.service.pub;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.pub.IPubPayCityService;
import com.youming.youche.system.api.pub.IPubPayProvService;
import com.youming.youche.system.domain.pub.PubPayCity;
import com.youming.youche.system.domain.pub.PubPayProv;
import com.youming.youche.system.dto.pub.PubPayDto;
import com.youming.youche.system.provider.mapper.pub.PubPayProvMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 平安省份表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
@DubboService(version = "1.0.0")
public class PubPayProvServiceImpl extends BaseServiceImpl<PubPayProvMapper, PubPayProv> implements IPubPayProvService {

    @DubboReference(version = "1.0.0")
    IPubPayCityService iPubPayCityService;

    @Override
    public List<PubPayProv> getPubPayProvList() {
        LambdaQueryWrapper<PubPayProv> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(PubPayProv::getProvNodecode);
        return this.list(qw);
    }

    @Override
    public List<PubPayDto> queryAddressInfo() {

        List<PubPayDto> pubPayDtos = new ArrayList<PubPayDto>();

        List<PubPayProv> pubPayProvList = this.getPubPayProvList();
        for (PubPayProv pubPayProv : pubPayProvList) {
            PubPayDto dto = new PubPayDto();
            List<PubPayCity> pubPayCityList = iPubPayCityService.getPubPayCityList(pubPayProv.getProvNodecode());
            dto.setPubPayProv(pubPayProv);
            dto.setPubPayCityList(pubPayCityList);

            pubPayDtos.add(dto);
        }

        return pubPayDtos;
    }

}
