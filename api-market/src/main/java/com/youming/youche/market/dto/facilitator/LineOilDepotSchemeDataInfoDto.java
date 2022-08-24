package com.youming.youche.market.dto.facilitator;

import com.youming.youche.market.domain.facilitator.TenantProductRel;
import lombok.Data;

import java.io.Serializable;

@Data
public class LineOilDepotSchemeDataInfoDto extends TenantProductRel implements Serializable {


    /**
     * 服务商ID
     */
    private Long serviceUserId;
//
//    /**
//     * 站点名称
//     */
//    private String productName;

    private String serviceName;

    private String provincePrice;
//
//    /**
//     * 省份ID
//     */
//    private Integer provinceId;
//
//    /**
//     * 市编码ID
//     */
//    private Integer cityId;
//
//    /**
//     * 县区ID
//     */
//    private Integer countyId;
//
//    /**
//     * 详细地址
//     */
//    private String address;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 子账户用户ID
     */
    private Long childAccountUserId;
//
//    /**
//     * 服务电话
//     */
//    private String serviceCall;

//    /**
//     * 是否有效，1为有效，2为无效
//     */
//    private Integer state;
//
//    /**
//     * 简介
//     */
//    private String introduce;

//    /**
//     * 所属租户
//     */
//    private Long tenantId;

    /**
     * 服务商类型
     */
    private Integer businessType;

    /**
     * 维修站资质 对应静态数据REPAIR_GRADE
     */
    private Integer repairGrade;


//    /**
//     * 修改人
//     */
//    private Long opId;


    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;
//
//    /**
//     * 是否共享（1.是，2.否）
//     */
//    private Integer isShare;

//    /**
//     * 是否需要审核（1：不需要 2：需要）
//     */
//    private Integer isAuth;

//    /**
//     * 审核状态
//     */
//    private Integer authState;

    /**
     * 审核人
     */
    private Long authUserId;

    /**
     * 审核备注
     */
    private String authRemark;

//    /**
//     * 审核时间
//     */
//    private String authDate;

    /**
     * 合作类型
     */
    private Integer productType;

    /**
     * 加油站id(找油网标识)
     */
    private String stationId;

    /**
     * logo
     */
    private String logoId;

    /**
     * logo地址
     */
    private String logoUrl;

    /**
     * 油卡类型 1中石油 2中石化
     */
    private Integer oilCardType;

    /**
     * 母账户余额
     */
    private Long parentAccountBalance;

    /**
     * 消费记录时间
     */
    private String consumptionRecordTime;

    /**
     * 消费记录日期
     */
    private String consumptionRecordDate;

    /**
     * 返利时间
     */
    private String rebateTime;

    /**
     * 返利月份
     */
    private String rebateMonth;

    /**
     * 来源服务商ID
     */
    private Long reServiceId;

    /**
     * 0否 1是
     */
    private Integer isReService;

    /**
     * 所属位置（1高速服务区、2高速出入口、3其他位置）
     */
    private Integer locationType;

    /**
     * 站点图片ID
     */
    private Long productPicId;
}
