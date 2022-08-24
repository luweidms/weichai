package com.youming.youche.market.vo.etc;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CmEtcInfoVo implements Serializable {
    //etc消费记录传入的值
    private String beginDate;//消费时间
    private String endDate;//消费结束时间
    private LocalDateTime beginDate1;
    private  LocalDateTime endDate1;
    private String etcCardNo;//ETC卡号
    private String plateNumber;//车牌号
    private Integer etcCardType;//卡类型
    private String tradingSite;//交易地点
    private String orderId;//归属订单
    private Integer etcUserType;//车辆类型 2 招商车 3自有车
    private Integer paymentType;//付费类型
    private String billNo;//账单号
    private String deductBeginDate;//扣费开始
    private String deductEndDate;//扣费结束
    private  LocalDateTime deductBeginDate1;
    private  LocalDateTime deductEndDate1;
    private String billStartDate;//账单开始
    private String billEndDate;//账单结束
    private  LocalDateTime billStartDate1;
    private  LocalDateTime billEndDate1;
    private String tenantName;//车队名称
    private Integer chargingState;////扣款状态 0未扣费 3反写成功、4仿写失败、1扣费成功、2扣费失败
    private Long productId;//站点名称
    private Long tenantId; //租户id
    private Long serviceProviderId;//服务商id
    private Integer state;//数据有效状态

    private  String begCarDependDate; // 靠台开始
    private  String EndCarDependDate; // 靠台结束
    private  LocalDateTime begCarDependDate1; // 靠台开始
    private  LocalDateTime EndCarDependDate1; // 靠台结束
    private  Integer paymentWay;//'成本模式（跟订单结算模式同步）',
    private List<Long> etcIds;


    private  String consumeMoney;
    private  String etcConsumeTime;
    private String id;

    private List<String> ids;
    private  String etcid;
    private  String coverType;
    private  Long userId;
}
