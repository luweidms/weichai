package com.youming.youche.record.dto.cm;

import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryLineByTenantForWXDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long lineId; // 线路id
    private String lineCodeRule; // 路线编码
    private String lineCodeName; // 线路名称
    private Integer sourceProvince; // 始发省份ID
    private Integer sourceCity; // 始发市编码ID
    private Integer sourceCounty; // 始发县id
    private Integer desProvince; // 目的省份ID
    private Integer desCity; // 目的市编码ID
    private Integer desCounty; // 目的地县编码id
    private String companyName; // 公司名称(全称)
    private String sourceAddress; // 始发地详细地址
    private String desAddress; // 目的地详细地址
    private Double estimateIncome; // 预估应收
    private String taiwanDate; // 靠台时间（格式 HH:mm）
    private String sourceEand; // 始发地经度
    private String sourceNand; // 始发地纬度
    private String desEand; // 目的地经度
    private String desNand; // 目的地纬度
    private String navigatDesLocation; // 文字描述目的地址，不填写默认使用百度定位地址
    private String navigatSourceLocation; // 文字描述出发地址，不填写默认使用百度定位地址
    private Integer arriveTime; // 到达时限（小时）

    private List<CmCustomerLineSubway> subWayList; // 客户线路经停点表

    private String sourceProvinceName; // 起始省
    private String sourceCityName; // 起始市
    private String desProvinceName; // 起始区县
    private String desCityName; // 目的省
    private String desCountyName; // 目的市
    private String sourceCountyName; // 目的区县

}
