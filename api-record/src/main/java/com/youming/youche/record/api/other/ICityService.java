package com.youming.youche.record.api.other;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.other.City;
import com.youming.youche.record.dto.other.DataListDto;
import com.youming.youche.record.dto.other.ProvinceNameDto;

import java.util.List;

/**
 * <p>
 * 城市表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
public interface ICityService extends IBaseService<City> {

    /**
     * 获取所有城市记录
     */
    List<City> getCityDataList();

    /**
     * 省市县区数据
     */
    DataListDto queryAddressInfo();

    /**
     * 通过省市区名称获取id
     *
     * @return 接口编码：11017
     * type 1 省 2 市 3 区
     */
    ProvinceNameDto getProvinceName(Integer type, String name, Long parentId);

    /**
     * 获取市ID
     *
     * @param provinceId 省编号
     * @param cityName   市名称
     */
    Long getCityIdByName(Long provinceId, String cityName);

}
