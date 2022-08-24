package com.youming.youche.record.provider.service.impl.other;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.other.IDistrictService;
import com.youming.youche.record.domain.other.District;
import com.youming.youche.record.provider.mapper.other.DistrictMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 县、区表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
@DubboService(version = "1.0.0")
public class DistrictServiceImpl extends BaseServiceImpl<DistrictMapper, District> implements IDistrictService {

    @Override
    public List<District> getDistrictDataList() {
        List<District> list = this.list();
        if (list != null && list.size() != 0) {
            return list;
        }
        return null;
    }

    @Override
    public Long getCountryIdByName(Long cityId, String districtName) {
        Long districtId = null;
        if (cityId != null && cityId > 0 && StringUtils.isNotEmpty(districtName)) {
            List<District> districts = this.getDistrictDataList();
            for (District d : districts) {
                if ((cityId.longValue() == d.getCityId() && d.getName().equals(districtName))
                        || d.getName().replace("区", "").replace("县", "").equals(districtName)
                        || d.getName().equals(districtName.replace("区", "").replace("县", ""))) {
                    districtId = d.getId();
                    break;
                }
            }
        }
        return districtId;
    }

}
