package com.youming.youche.record.provider.service.impl.other;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.other.ICityService;
import com.youming.youche.record.api.other.IDistrictService;
import com.youming.youche.record.api.other.IProvinceService;
import com.youming.youche.record.domain.other.City;
import com.youming.youche.record.domain.other.District;
import com.youming.youche.record.domain.other.Province;
import com.youming.youche.record.dto.other.DataListDto;
import com.youming.youche.record.dto.other.ProvinceNameDto;
import com.youming.youche.record.provider.mapper.other.CityMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 城市表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
@DubboService(version = "1.0.0")
public class CityServiceImpl extends BaseServiceImpl<CityMapper, City> implements ICityService {

    @Resource
    IProvinceService iProvinceService;

    @Resource
    IDistrictService iDistrictService;

    @Override
    public List<City> getCityDataList() {
        List<City> list = this.list();
        if (list != null && list.size() != 0) {
            return list;
        }
        return null;
    }

    @Override
    public DataListDto queryAddressInfo() {
        List<Province> provinceDataList = iProvinceService.getProvinceDataList();
        List<City> cityDataList = this.getCityDataList();
        List<District> districtDataList = iDistrictService.getDistrictDataList();

        DataListDto dto = new DataListDto();
        dto.setSysProvince(provinceDataList);
        dto.setSysCity(cityDataList);
        dto.setSysDistrict(districtDataList);
        return dto;
    }

    @Override
    public ProvinceNameDto getProvinceName(Integer type, String name, Long parentId) {
        Long id = null;

        if (type == 1) {
            id = Long.valueOf(String.valueOf(iProvinceService.getProvinceIdByName(name)));
        } else if (type == 2) {
            id = this.getCityIdByName(parentId, name);
        } else if (type == 3) {
            id = iDistrictService.getCountryIdByName(parentId, name);
        }

        ProvinceNameDto dto = new ProvinceNameDto();
        dto.setId(id);
        dto.setName(name);
        if (type != 1) {
            dto.setParentId(parentId);
        }

        return dto;
    }

    @Override
    public Long getCityIdByName(Long provinceId, String cityName) {
        Long cityId = null;
        if (provinceId != null && provinceId > 0 && StringUtils.isNotEmpty(cityName)) {
            List<City> citys = this.getCityDataList();
            for (City city : citys) {
                if ((city.getProvId().longValue() == provinceId && city.getName().equals(cityName))
                        || city.getName().replace("市", "").equals(cityName)
                        || city.getName().equals(cityName.replace("市", ""))) {
                    cityId = city.getId();
                    break;
                }
            }
        }
        return cityId;
    }

}
