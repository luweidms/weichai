package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.domain.facilitator.TenantProductRelVer;
import lombok.Data;

import java.io.Serializable;
@Data
public class TenantProductVo implements Serializable {

    /**
     * 产品历史信息
     */
    private TenantProductRelVer tenantProductRelVer;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 服务电话
     */
    private String serviceCall;

    /**
     * 省ID
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 县ID
     */
    private Integer countyId;

    /**
     * 县名称
     */
    private String countyName;

    /**
     * 市ID
     */
    private Integer cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 地址
     */
    private String address;

    /**
     * 简介
     */
    private String introduce;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 状态名称
     */
    private String stateName;

    /**
     * 开票浮动价
     */
    private String floatBalance;

    /**
     * 开票固定价
     */
    private Long fixedBalance;

    /**
     * 开票浮动价结算
     */
    private String floatBalanceBill;

    /**
     * 开票固定价结算
     */
    private Long fixedBalanceBill;

    /**
     * 经度
     */
    private String nand;

    /**
     * 纬度
     */
    private String eand;

    /**
     * 车队关系ID
     */
    private Long relId;

    /**
     * 开票固定名称
     */
    private String fixedBalanceName;

    /**
     * 开票浮动名称
     */
    private String fixedBalanceBillName;

    /**
     * 是否有开票能力
     */
    private Integer isBillAbility;

    /**
     * 是否有开票能力名称
     */
    private String isBillAbilityName;

    /**
     * 油单价
     */
    private String oilPrice;

    /**
     * 油单价
     */
    private String oilPriceBill;

    private Integer cooperationNum;//合作车队数量

    /**
     * 登录名称
     */
    private String loginAcct;

    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 名称
     */
    private String linkman;

    /**
     * 服务商ID
     */
    private Long serviceUserId;

    /**
     * 服务商类型
     */
    private Integer serviceType;

    /**
     * 是否共享
     */
    private Integer isShare;

    /**
     * 服务商费用
     */
    private String serviceCharge;

    /**
     * 服务商费用账单
     */
    private String serviceChargeBill;

    /**
     * 油卡类型
     */
    private Integer oilCardType;

    /**
     * 油卡类型名称
     */
    private String oilCardTypeName;

    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;

    /**
     * 是否审核
     */
    private Integer isAuth;

    /**
     * 位置类型
     */
    private Integer locationType;

    /**
     * 产品图片ID
     */
    private Long productPicId;

    /**
     * 位置类型名称
     */
    private String locationTypeName;
}
