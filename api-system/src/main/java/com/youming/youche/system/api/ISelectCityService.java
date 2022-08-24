package com.youming.youche.system.api;

import com.youming.youche.components.citys.City;
import com.youming.youche.components.citys.District;
import com.youming.youche.components.citys.Province;

import java.util.List;

/**
 * @Description : 城市选择服务类
 * @Author : luwei
 * @Date : 2022/1/24 9:05 下午
 * @Version : 1.0
 **/
public interface ISelectCityService {

    /***
     * @Description: 查询省份
     * @Author: luwei
     * @Date: 2022/1/24 9:15
     * @return: java.util.List<com.youming.youche.compoonents.citys.Province>
     * @Version: 1.0
     **/
    List<Province>  doQueryProvices(Province province);

    /**
     * 查询地市
     * @Author: luwei
     * @Date: 2022/1/24 9:45 下午
     * @Param provicesId:34
     * @return: java.util.List<com.youming.youche.compoonents.citys.City>
     * @Version: 1.0
     **/
    List<City> doQueryRegion(City city);

    /***
     * @Description: 查询县区
     * @Author: luwei
     * @Date: 2022/1/24 10:09 下午
     * @Param cityId:
     * @return: java.util.List<com.youming.youche.components.citys.District>
     * @Version: 1.0
     **/
    List<District> getDistrictData(District district);


}
