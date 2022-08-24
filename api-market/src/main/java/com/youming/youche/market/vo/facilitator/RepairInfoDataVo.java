package com.youming.youche.market.vo.facilitator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;

@Data
public class RepairInfoDataVo implements Serializable {
   private Page<RepairInfoVo> items;
   private Long unexpired;
   private Long beExpired;
   private Long serviceCharge;
   private String productName;

}
