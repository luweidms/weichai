package com.youming.youche.record.provider.service.impl.other;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.other.IProvinceService;
import com.youming.youche.record.domain.other.Province;
import com.youming.youche.record.provider.mapper.other.ProvinceMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 省份表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
@DubboService(version = "1.0.0")
public class ProvinceServiceImpl extends BaseServiceImpl<ProvinceMapper, Province> implements IProvinceService {

    @Override
    public List<Province> getProvinceDataList() {
        List<Province> list = this.list();
        if (list != null && list.size() != 0) {
            return list;
        }
        return null;
    }

    @Override
    public Integer getProvinceIdByName(String provinceName) {
        Integer province = -1;
        if (StringUtils.isNotEmpty(provinceName)) {
            List<Province> p = this.getProvinceDataList();
            for (Province temp : p) {
                if (provinceName.equals(temp.getName())) {
                    province = Integer.parseInt(String.valueOf(temp.getId()));
                } else if (provinceName.indexOf("省") != -1 && temp.getName().equals(provinceName.substring(0, provinceName.indexOf("省")))) {
                    province = Integer.parseInt(String.valueOf(temp.getId()));
                } else if (provinceName.indexOf(temp.getName()) != -1) {
                    province = Integer.parseInt(String.valueOf(temp.getId()));
                }
            }
        }
        return province;
    }

}
