package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/14
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AdvanceExpireOutVo implements Serializable {
   /**
    * 流水号
    */
   private String flowId;
   private Long orderId;//订单号
   private String userName ;//用户名称
   private String userPhone;//手机号码
   private String mainDriver;//主驾司机
   private Integer state;//到期状态：0未到期 1已到期
   private boolean isIncludeManual;//是否包含手动到期数据
   private Long tenantId;//租户Id
   private Integer userType;//收款方类型
   private Integer payUserType;//付款方类型
   private String busiCode;//业务单号

   private String signType;//到期类型
   private Integer stateNum;

}
