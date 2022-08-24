package com.youming.youche.record.vo.cm;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 向子俊
 * @version 1.0.0
 * @ClassName Address.java
 * @Description TODO地址库
 * @createTime 2022年02月15日 09:57:00
 */
@Data
public class CmAddressVo implements Serializable {
    private Long id;
    private String addressName;
    private Long tenantId;
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    private String cityName;
    private Integer districtId;
    private String districtName;
    private String addressDetail;
    private String addressShow;
    private String lng;
    private String lat;
    private String createTime;
    private String updateTime;
    private Long customerId;
}
