package com.youming.youche.market.vo.youca;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author XXX
 * @since 2022-03-17
 */
@Data
public class ConsumeOilFlowVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId ;//业务单号
    private String recordStartTime; //记录开始 yyyy-MM-dd
    private String recordEndTime; //记录结束 yyyy-MM-dd
    private String payStartTime; //结算开始 yyyy-MM-dd
    private String payEndTime; //结算结束 yyyy-MM-dd
    private String driverName; //司机姓名
    private String driverPhone; //司机手机
    private Integer fromType; //来源类型 1自有、2客户、3平台
    private Integer cardType; //卡类型：1中石油，2中石化,3扫码加油
    private String serviceName; //服务商名称
    private String plateNumber; //车牌号
    private String cardNum; //油卡卡号
    private Integer rebate; //返利 0 无  1有
    private Integer expire; //是否到期  0 未到期  1已到期
    private String voucherId; //代金券Id
    private Integer queryType; //查询类型：1服务商  2车队
    private String address; //消费地址
    private String dealRemark; //返利结果
    private String tradeTimeStart ;//交易开始时间
    private String tradeTimeEnd ;//交易结束时间
    private String consumerName ;//客户名称
    private String consumerBill;//客户手机号
    private String settlStartTime;//结算开始时间
    private String settlEndTime ;//结算结束时间
    private String productName ;//产品名称
    private Integer isExpire ;//是否过期
    private String oilStationType ; //油站类型

    private  Long productId;//产品ID
    private String qualityStar;//质量
    private String priceStar;//价格
    private String serviceStar ;//服务商

    private List<Long> extFlowIds;

}
