package com.youming.youche.record.dto.cm;

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
public class CmAddressDto implements Serializable {
    private Long id; // 主键id
    private String addressName; // 地址名称
    private Long tenantId; // 车队id
    private Integer provinceId; // 省id
    private String provinceName; // 省名称
    private Integer cityId; // 市id
    private String cityName; // 市名称
    private Integer districtId; // 区id
    private String districtName; // 区名称
    private String addressDetail; // 地址详情
    private String addressShow; // 地址显示
    private String lng; // 经度
    private String lat; // 维度
    private String createTime; // 创建时间
    private String updateTime; // 修改时间
    //客户全称
    private String companyName;
    private Long customerId; // 客户id
}
