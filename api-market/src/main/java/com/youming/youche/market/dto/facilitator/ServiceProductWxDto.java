package com.youming.youche.market.dto.facilitator;

import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.annotation.SysStaticDataInfoDict;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceProductWxDto implements Serializable {
    /**
     * 站点id
     */
    private Long productId;
    /**
     * 站点名称
     */
    private String productName;

    @SysStaticDataInfoDict(dictDataSource =  EnumConsts.SysStaticData.SYS_STATE_DESC)
    private Integer state;

    /**
     *  '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    @SysStaticDataInfoDict(dictDataSource =  EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE)
    private Integer authState;

    private String isShareName;
    /**
     * 是否共享（1.是，2.否）
     */
    private Integer isShare;

    /**
     * 租户联系人
     */
    private String linkMan;

    /**
     * 联系人手机
     */
    private String mobilePhone;

    /**
     * 服务电话
     */
    private String serviceCall;
    /**
     * 省份ID
     */
    @SysStaticDataInfoDict(dictDataSource = EnumConsts.SysStaticData.SYS_PROVINCE,dictText="provinceName")
    private Integer provinceId;

    /**
     * 市编码id
     */
    @SysStaticDataInfoDict(dictDataSource = EnumConsts.SysStaticData.SYS_CITY,dictText="cityName")
    private Integer cityId;

    /**
     * 县区ID
     */
    @SysStaticDataInfoDict(dictDataSource =  EnumConsts.SysStaticData.SYS_DISTRICT,dictText="countyName")
    private Integer countyId;

    /**
     * 详细地址
     */
    private String address;


    private Integer delState;
}
