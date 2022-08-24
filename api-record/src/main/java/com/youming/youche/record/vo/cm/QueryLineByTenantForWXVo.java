package com.youming.youche.record.vo.cm;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryLineByTenantForWXVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sourceProvince; // 始发省份ID
    private Integer sourceCity; // 始发市编码ID
    private Integer sourceCounty; // 始发县区ID
    private Long customerId; // 客户编号
    private Integer desProvince; // 目的省份ID
    private Integer desCity; // 目的市编码ID
    private Integer desCounty; // 目的县区ID
    private String lineCodeRule; // 路线编码
    private String lineCodeName; // 线路名称
    private String companyName; // 公司名称(全称)
    private String detailAddress; // 详细地址
    private Long tenantId;

}
