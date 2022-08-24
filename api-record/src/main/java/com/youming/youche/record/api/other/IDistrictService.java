package com.youming.youche.record.api.other;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.other.District;

import java.util.List;

/**
 * <p>
 * 县、区表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
public interface IDistrictService extends IBaseService<District> {

    /**
     * 县、区数据
     */
    List<District> getDistrictDataList();

    /**
     * 获取县区Id
     *
     * @param cityId       市编号
     * @param districtName 县区名称
     */
    Long getCountryIdByName(Long cityId, String districtName);

}
