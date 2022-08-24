package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 订单扩展表
    * </p>
* @author CaoYaJie
* @since 2022-03-15
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderInfoExt extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单编号
            */
    private Long orderId;

            /**
            * 预估运行时间（毫秒）
            */
    private Long estRunTime;

            /**
            * 空驶距离（单位米）
            */
    private Long runWay;

            /**
            * g7油耗 l
            */
    private Float g7AvgOil;

            /**
            * g7平均速度 km/h
            */
    private Float g7AvgSpeed;

            /**
            * g7空载 km
            */
    private Float g7RunWay;

            /**
            * g7行驶距离 km
            */
    private Float g7Distance;

            /**
            * g7订单油耗l
            */
    private Float g7OrderOil;

            /**
            * 实际运行时间
            */
    private Long relRunTime;

            /**
            * 载重油耗
            */
    private Float capacityOil;

            /**
            * 空载油耗
            */
    private Float runOil;

            /**
            * 挂车预估运行时间（毫秒）
            */
    private Long trailerEstRunTime;

            /**
            * 挂车实际运行时间（毫秒）
            */
    private Long trailerRelRunTime;

            /**
            * 估算价格状态0:等待2:失败1:成功
            */
    private Integer preAmountFlag;

            /**
            * 操作时间
            */
   
    private LocalDateTime opDate;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 修改数据的操作人id
            */
    private Long updateOpId;

            /**
            * 是否找回程货：0表示没有，1表示要找回程货
            */
    private Integer isBackhaul;

            /**
            * 回货价格
            */
    private Long backhaulPrice;

            /**
            * 回货联系人名称
            */
    private String backhaulLinkMan;

            /**
            * 回货联系人id
            */
    private Long backhaulLinkManId;

            /**
            * 回货联系人电话
            */
    private String backhaulLinkManBill;

            /**
            * 审核id
            */
    private Long auditId;

            /**
            * 审核名称
            */
    private String auditName;

            /**
            * 是否使用车辆油耗
            */
    private Integer isUseCarOilCost;

            /**
            * 是否评价 1为已评价
            */
    private Integer isEvaluate;

            /**
            * 时效星级
            */
    private Integer agingStarLevel;

            /**
            * 服务星级
            */
    private Integer serviceStarLevel;

            /**
            * 态度星级
            */
    private Integer mannerStarLevel;

            /**
            * 订单修改类型
            */
    private Integer updateType;

            /**
            * 是否临时车队单 0 不是 1 是
            */
    private Integer isTempTenant;

            /**
            * 是否有修改订单 0 没有 1 有
            */
    private Integer isUpdate;

            /**
            * 油使用方式 1使用客户油;2使用本车队
            */
    private Integer oilUseType;

            /**
            * 油是否需要发票 0不需要，1需要
            */
    private Integer oilIsNeedBill;

            /**
            * 油是否需要发票 0不需要，1需要
            */
    private String oilAffiliation;

            /**
            * 付款方式 1:包干模式 2:实报实销模式 3:承包模式
            */
    @SysStaticDataInfoDict(dictDataSource = "PAYMENT_WAY")
    private Integer paymentWay;

            /**
            * 首次支付状态 0 未支付 1 已支付
            */
    private Integer firstPayFlag;

            /**
            * 路歌运单id
            */
    private String taxWayBillId;

            /**
            * 路歌协议地址
            */
    private String lugeSignAgreeUrl;

            /**
            * 签订协议状态:0:未同步 1:待签订 2:已签订 3:已驳回 
            */
    private Integer signAgreeState;

            /**
            * 费率
            */
    private Double rateValue;
    @TableField(exist = false)
    private String paymentWayName;


}
