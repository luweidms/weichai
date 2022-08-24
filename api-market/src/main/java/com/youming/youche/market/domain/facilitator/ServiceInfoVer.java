package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>
 * 服务商版本表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceInfoVer extends BaseDomain  {





    /**
     * 用户编码
     */
    private Long serviceUserId;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    /**
     * 操作人
     */
    private Long opId;


    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;

    /**
     * 是否删除（0：否，1：是）
     */
    private Integer isDel;

    /**
     * 是否认证
     */
    private Integer isAuth;

    /**
     * 有效状态
     */
    private Integer state;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * logo
     */
    private String logoId;

    /**
     * logo地址
     */
    private String logoUrl;

    /**
     * 平台代收
     */
    private Integer agentCollection;


}
