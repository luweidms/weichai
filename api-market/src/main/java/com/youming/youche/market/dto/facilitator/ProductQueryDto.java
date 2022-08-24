package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQueryDto implements Serializable {
    private static final long serialVersionUID = -253340185505743284L;
    /**
     * 用户编码
     */
    private Long serviceUserId;
    /**
     * 站点名称
     */
    private String productName;
    /**
     * 服务电话
     */
    private String serviceCall;
    /**
     * 地址
     */
    private String address;
    /**
     * 是否有效，1为有效，2为无效
     */
    private Integer state;
    /**
     * 审核状态
     */
    private Integer authState;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 服务商
     */
    private String serviceName;
    /**
     *
     */
    private Double billBeginPrice=-1d;
    private Double billEndPrice=-1d;
    private Double noBillBeginPrice=-1d;
    private Double noBillEndPrice=-1d;
    /**
     * 市编码ID
     */
    private Integer cityId;
    /**
     * 是否共享（1.是，2.否）
     */
    private Integer isShare;
}
