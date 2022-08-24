package com.youming.youche.record.domain.cm;

import com.youming.youche.commons.base.BaseDomain;
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
public class Address extends BaseDomain implements Serializable {
    /**名称*/
    private String addressName;
    /**租户Id*/
    private Long tenantId;
    /**省id*/
    private Integer provinceId;
    /**省*/
    private String provinceName;
    /**市id*/
    private Integer cityId;
    /**市*/
    private String cityName;
    /**区id*/
    private Integer districtId;
    /**区*/
    private String districtName;
    /**导航详细地址*/
    private String addressDetail;
    /**展示地址*/
    private String addressShow;
    /**经度*/
    private String lng;
    /**纬度*/
    private String lat;
    /**客户id**/
    private Long customerId;
}
