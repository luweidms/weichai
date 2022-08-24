package com.youming.youche.market.dto.facilitator;

import com.youming.youche.market.domain.facilitator.ServiceProductEtc;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDetailDto implements Serializable {
    private Long productId;//站点id
    private Long tenantId;//创建租户
    private String productName;//站点名称
    private String serviceCall;//服务电话
    private Integer provinceId;//省份id
    private String provinceName;//省份名称
    private Integer cityId;//城市id
    private String cityName;
    private Integer countyId;//区域id
    private String countyName;
    private String loginAcct;//服务商账号
    private String linkman;//服务商联系人
    private String serviceName;//服务商公司名称
    private Integer isBillAbility;//支持开票
    private Integer isShare;//是否共享
    private String floatBalance;//不开票浮动价格
    private String floatBalanceBill;//开票浮动价格
    private Long fixedBalance;//不开票固定价
    private String fixedBalanceBillName;
    private String  fixedBalanceName;
    private Long fixedBalanceBill;//开票固定价
    private String serviceCharge;//不开票服务费
    private String serviceChargeBill;//开票服务费
    private Integer authState;//认证状态
    private String authStateName;//认证状态名称
    private Integer state;//是否有效
    private String stateName;//是否有效名称
    private Integer cooperationNum;//合作车队数量
    private String address;//详细地址
    private String nand;//纬度
    private String eand;//经度
    private Long serviceUserId;
    private Integer serviceType;
    private String introduce;

    private Integer oilCardType;
    private String oilCardTypeName;
    private String floatServiceChargeBill;
    private String floatServiceCharge;


    private String isBillAbilityName;

    private Integer locationType ;
    private Long productPicId;

    private String locationTypeName ;

    /**ETC站点信息**/
    private ServiceProductEtc serviceProductEtc;
}
