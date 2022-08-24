package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单油卡表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderOilCardInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 油卡号
     */
    private String oilCardNum;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 油费
     */
    private Long oilFee;

    /**
     * 油卡类型
     */
    @SysStaticDataInfoDict(dictDataSource = "OIL_CARD_TYPE")
    private Integer cardType;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 油卡渠道 0 添加 1 车辆绑定
     */
    private Integer cardChannel;

    /**
     * 理论余额
     */
    @TableField(exist=false)
    private Long cardBalance;

    @TableField(exist=false)
    private Boolean isNeedWarn;//是否需要提醒
    /**
     * 油卡类型名称
     */
    @TableField(exist=false)
    private String cardTypeName;



}
