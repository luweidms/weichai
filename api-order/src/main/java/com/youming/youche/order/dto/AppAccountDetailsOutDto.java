package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/10
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AppAccountDetailsOutDto implements Serializable {
    private static final long serialVersionUID = -4564433792992858357L;
    private Long userId;//用户id
    private Long businessNumber;//流水大目id
    private String bussinessName;//流水大目名称
    private Long amount;//流水大目金额
    private Integer costType;//费用类型 1：支出 2：收入3：其他
    private LocalDateTime createTime;//时间
    private String detailMonth;//流水月份
    private String orderId;//订单号
    private String vehicleAffiliation;//来源
    private List<SubjectsOutDto> subList;//科目明细项

    private String subtitle;//二级标题

    private String showType;//0：普通；1：招商车扣费；2：工资发放
}
