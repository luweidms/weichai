package com.youming.youche.record.dto.other;

import com.youming.youche.record.domain.other.City;
import com.youming.youche.record.domain.other.District;
import com.youming.youche.record.domain.other.Province;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Province> sysProvince; // 省

    private List<City> sysCity; // 市

    private List<District> sysDistrict; // 县、区

}
