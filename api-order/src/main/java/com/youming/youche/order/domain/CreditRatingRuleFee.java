package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-07
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CreditRatingRuleFee extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 保费金额
            */
    private Long insuranceFee;

    /**
     * 保费金额
     */
    @TableField(exist=false)
    private String insuranceFeeStr;

            /**
            * 运费范围结束,-1表示忽略.\n如果min_fee和max_fee都为-1，表示此等级规则无效
            */
    private Integer maxFee;

            /**
            * 运费范围开始,-1表示忽略
            */
    private Integer minFee;

            /**
            * 规则编号，credit_rating_rule中主键
            */
    private Long ruleId;

            /**
            * 状态1 : 可用  0 : 不可用
            */
    private Integer state;


}
