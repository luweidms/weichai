package com.youming.youche.system.provider.service;

import com.youming.youche.components.citys.City;
import com.youming.youche.components.citys.District;
import com.youming.youche.components.citys.Province;
import com.youming.youche.system.api.ISelectCityService;
import com.youming.youche.system.provider.mapper.SelectCityMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description : 城市选择服务类
 * @Author : luwei
 * @Date : 2022/1/24 9:05 下午
 * @Version : 1.0
 **/
@DubboService(version = "1.0.0")
@Service
public class SelectCityServiceImpl implements ISelectCityService {

    @Resource
    private SelectCityMapper selectCityMapper;

    @Override
    public List<Province> doQueryProvices(Province province) {
        return selectCityMapper.doQueryProvices(province);
    }

    @Override
    public List<City> doQueryRegion(City city) {
        return selectCityMapper.doQueryRegion(city);
    }

    @Override
    public List<District> getDistrictData(District district) {
        return selectCityMapper.getDistrictData(district);
    }
}
