package com.youming.youche.order.dto;

import com.youming.youche.order.vo.CustomerInfoVo;
import com.youming.youche.order.vo.DispatchInfoVo;
import lombok.Data;

import java.io.Serializable;

/***
 * @Description: 订单实体对象
 * @Author: luwei
 * @Date: 2022/8/24 16:30
 * @Version: 1.0
 **/
@Data
public class OrderDto implements Serializable {

   /**
    *
    */
   private DispatchInfoVo dispatchInfoVo;

   /**
    * 客户实体信息
    */
   private CustomerInfoVo customerInfoVo;

   /**
    *
    */
   private OrderIdsDto orderIdsDto;

   private OtherDataDto otherDataDto;

   /**
    * 订单修改类型
    */
   private Integer updateType;

   /**
    * 調度
    */
   private Integer dispatch;

   /**
    * 訂單詳情類型
    */
   private Integer orderDetailsType;

   /**
    * 智能模式 部门id
    */
   private Long departmentId;
   /**
    * 智能模式 部门名称
    */
   private String departmentName;

   /**
    * 油卡号
    */
   private String oilCardNum;

}
