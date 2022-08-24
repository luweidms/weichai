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
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderInfoExtVer extends BaseDomain {

    private static final long serialVersionUID = 1L;



    /**
     * 时效星级
     */
    private Integer agingStarLevel;
    /**
     * 审核id
     */
    private Long auditId;
    /**
     * 审核名称
     */
    private String auditName;
    /**
     * 回货联系人id
     */
    private String backhaulLinkMan;
    /**
     * 回货联系人电话
     */
    private String backhaulLinkManBill;
    /**
     * 回货联系人id
     */
    private Long backhaulLinkManId;
    /**
     * 回货价格
     */
    private Long backhaulPrice;
    /**
     * 载重油耗
     */
    private Float capacityOil;
    /**
     * 预估运行时间（毫秒）
     */
    private Long estRunTime;
    /**
     * 首次支付状态 0 未支付 1 已支付
     */
    private Integer firstPayFlag;
    /**
     * g7油耗 l
     */
    private Float g7AvgOil;
    /**
     * g7平均速度 km/h
     */
    private Float g7AvgSpeed;
    /**
     * g7行驶距离 km
     */
    private Float g7Distance;
    /**
     * g7订单油耗l
     */
    private Float g7OrderOil;
    /**
     * g7空载 km
     */
    private Float g7RunWay;
    /**
     * 是否找回程货：0表示没有，1表示要找回程货
     */
    private Integer isBackhaul;
    /**
     * 是否评价 0未评价 1已评价(费用类型为1才有)
     */
    private Integer isEvaluate;
    /**
     * 是否临时车队单 0 不是 1 是
     */
    private Integer isTempTenant;
    /**
     * 是否修改
     */
    private Integer isUpdate;
    /**
     * 是否使用车辆油耗
     */
    private Integer isUseCarOilCost;
    /**
     * 路歌协议地址
     */
    private String lugeSignAgreeUrl;
    /**
     * 态度星级
     */
    private Integer mannerStarLevel;
    /**
     * 油资金渠道
     */
    private String oilAffiliation;
    /**
     * 油是否需要发票 0不需要，1需要
     */
    private Integer oilIsNeedBill;
    /**
     * 油使用方式 1使用客户油;2使用本车队
     */
    private Integer oilUseType;
    /**
     * 操作时间
     */
    private LocalDateTime opDate;
    /**
     * 操作员ID
     */
    private Long opId;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 付款方式 1:包干模式 2:实报实销模式 3:承包模式
     */
    private Integer paymentWay;
    /**
     * 估算价格状态0:等待2:失败1:成功
     */
    private Integer preAmountFlag;
    /**
     * 费率
     */
    private Double rateValue;
    /**
     * 实际运行时间
     */
    private Long relRunTime;
    /**
     * 空载油耗
     */
    private Float runOil;
    /**
     * 空驶距离（单位米）
     */
    private Long runWay;
    /**
     * 服务星级
     */
    private Integer serviceStarLevel;
    /**
     * 签订协议状态:0:未同步 1:待签订 2:已签订 3:已驳回
     */
    private Integer signAgreeState;
    /**
     * 路歌运单id
     */
    private String taxWayBillId;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 挂车预估运行时间（毫秒）
     */
    private Long trailerEstRunTime;
    /**
     * 挂车实际运行时间（毫秒）
     */
    private Long trailerRelRunTime;
    /**
     * 修改操作员id
     */
    private Long updateOpId;
    /**
     * 修改类型
     */
    private Integer updateType;
    /**
     * 自有车付款方式
     */
    @TableField(exist = false)
    private String paymentWayName;

}
