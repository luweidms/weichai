package com.youming.youche.order.provider.service.impl.oil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.oil.ICarLastOilService;
import com.youming.youche.order.domain.oil.CarLastOil;
import com.youming.youche.order.provider.mapper.oil.CarLastOilMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 车辆最后油卡号表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-24
 */
@DubboService(version = "1.0.0")
@Service
public class CarLastOilServiceImpl extends BaseServiceImpl<CarLastOilMapper, CarLastOil> implements ICarLastOilService {

    @Override
    public CarLastOil getCarLastOilByPlateNumber(String plateNumber, Long tenantId) {
        LambdaQueryWrapper<CarLastOil> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(plateNumber), CarLastOil::getPlateNumber, plateNumber);
        queryWrapper.eq(tenantId > 0, CarLastOil::getTenantId, tenantId);
        queryWrapper.orderByDesc(CarLastOil::getCreateTime);

        List<CarLastOil> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
