package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderOilCardInfoVer extends BaseDomain {

    private static final long serialVersionUID = 1L;



    /**
     * 油卡渠道 0 添加 1 车辆绑定
     */
    private Integer cardChannel;
    /**
     * 油卡类型
     */
    private Integer cardType;


    /**
     * 油卡号
     */
    private String oilCardNum;
    /**
     * 校验油费
     */
    private Long oilFee;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改状态
     */
    private Integer updateState;
    /**
     * 卡类型名称
     */
    @TableField(exist = false)
    private String cardTypeName;
    /**
     * 理论余额
     */
    @TableField(exist=false)
    private Long cardBalance;


}
