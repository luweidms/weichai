package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaveDto implements Serializable {
    private static final long serialVersionUID = -253340185505743284L;
    /**
     * 站点ID
     */
    private Long productId;
    /**
     * 站点名稱
     */
    private String productName;
    /**
     * 省份id
     */
    private Integer provinceId;
    /**
     * 市编码ID
     */
    private Integer cityId;
    /**
     * 县区ID
     */
    private Integer countyId;
    /**
     * 地址
     */
    private String address;
    /**
     * 服务电话
     */
    private String serviceCall;
    /**
     * 简介
     */
    private String introduce;
    /**
     * 浮动价结算
     */
    private String floatBalance;
    /**
     * 固定价结算
     */
    private String fixedBalance;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 地址纬度
     */
    private String nand;//地址纬度
    /**
     * 地址经度
     */
    private String eand;//地址经度
    /**
     * 服务商用户id
     */
    private Long serviceUserId;
    /**
     * 状态
     */
    private Integer state;
    private Integer isFleet;
    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;
    /**
     * 是否共享（1.是，2.否）
     */
    private Integer isShare;
    /**]
     * 站点状态
     */
    private Integer productState;
    /**
     * 浮动价结算(开票)
     */
    private String floatBalanceBill;
    /**
     * 固定价结算(开票)
     */
    private String fixedBalanceBill;
    /**
     * 手续费
     */
    private String serviceCharge;
    /**
     * 开票手续费
     */
    private String serviceChargeBill;
    /**
     * 登录账户
     */
    private String loginAcct;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 联系人姓名
     */
    private String linkman;
    private Integer isRegistered;
    /**
     * 加油站id(找油网标识)
     */
    private String stationId;
    /**
     * 合作类型
     */
    private Integer productType;
    private Boolean isProductAudit;
    /**
     * 油卡类型 1中石油 2中石化
     */
    private Integer oilCardType;
    /**
     * 开票手续费浮动价结算
     */
    private String floatServiceChargeBill;
    /**
     * 不开票手续费浮动价结算
     */
    private String floatServiceCharge;
    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;
    /**
     * 现场价 1是 0 不是
     */
    private Integer locationType ;
    /**
     * 站点图片ID
     */
    private Long productPicId;
}
