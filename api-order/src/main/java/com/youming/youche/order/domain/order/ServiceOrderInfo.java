package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author wuhao
* @since 2022-05-18
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ServiceOrderInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 现金余额
     */
    private Long cashBalance;
    /**
     * 用戶id
     */
    private Long createUserId;
    /**
     * 订单油
     */
    private Long oilBalance;
    /**
     * 油卡金额单位
     */
    private Long oilFee;
    /**
     * 加油升数
     */
    private Long oilLitre;
    /**
     * 加油价格
     */
    private Long oilPrice;

    private String oilsId;
    /**
     * 油品级别
     */
    private String oilsLevel;
    /**
     * 油品名称
     */
    private String oilsName;
    /**
     * 油品类型
     */
    private String oilsType;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 订单状态
     */
    private Integer orderState;
    /**
     * 订单类型
     */
    private Integer orderType;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 加油站id
     */
    private String stationId;
    /**
     * 进程处理时间
     */
    private LocalDateTime taskDate;
    /**
     * 进程处理结果
     */
    private String taskResult;
    /**
     * 进程处理状态
     */
    private Integer taskState;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户手机
     */
    private String userPhone;
    /**
     * 订单状态
     */
    @TableField(exist = false)
    private  String orderStateName;
    /**
     * 订单类型
     */
    @TableField(exist = false)
    private  String  orderTypeName;//SERVICE_ORDER_TYPE
}
