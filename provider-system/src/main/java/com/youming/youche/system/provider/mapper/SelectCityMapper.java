package com.youming.youche.system.provider.mapper;

import com.youming.youche.components.citys.City;
import com.youming.youche.components.citys.District;
import com.youming.youche.components.citys.Province;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SelectCityMapper {

    List<Province> doQueryProvices(@Param("province") Province province);

    List<City> doQueryRegion(@Param("city") City city);

    List<District> getDistrictData(@Param("district") District district);
}
