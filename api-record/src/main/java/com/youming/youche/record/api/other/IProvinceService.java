package com.youming.youche.record.api.other;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.other.Province;

import java.util.List;

/**
 * <p>
 * 省份表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
public interface IProvinceService extends IBaseService<Province> {

    /**
     * 省份数据
     */
    List<Province> getProvinceDataList();

    /**
     * 根据省份名称获取id
     */
    Integer getProvinceIdByName(String provinceName);

}
