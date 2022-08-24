package com.youming.youche.market.domain.repair;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 维修项
 *
 * @author hzx
 * @since 2022-03-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RepairItems extends BaseDomain {

    /**
     * 维修单id
     */
    private Long repairId;

    /**
     * 维修单号
     */
    private String repairCode;

    /**
     * 一级维修项
     */
    private Long repairRootId;

    /**
     * 二级维修项
     */
    private Long repairRootTwoId;

    /**
     * 一二级维修项拼接
     */
    private String repairItemName;

    /**
     * 单价
     */
    private Long univalence;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 工时
     */
    private Float workingHours;

    /**
     * 金额
     */
    private Long amount;

    /**
     * 描述
     */
    private String des;

    /**
     * 维修图片1url
     */
    private String repair1PicUrl;

    /**
     * 维修图片2url
     */
    private String repair2PicUrl;

    /**
     * 维修图片3url
     */
    private String repair3PicUrl;

    /**
     * 维修图片4url
     */
    private String repair4PicUrl;

    /**
     * 维修图片5url
     */
    private String repair5PicUrl;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 图片id，多张就逗号分开
     */
    private String repairPicIds;

    /**
     * 图片url逗号隔开
     */
    private String repairPicUrl;


}
