package com.youming.youche.market.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 维修记录列表
 *
 * @author hzx
 * @date 2022/3/12 15:12
 */
@Data
public class UserRepairInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // 维修单号
    private String repairCode;

    // 建单时间
    private String createTime;

    // 服务商名称
    private String businessName;

    // 站点名称
    private String oilNum;

    // 车牌号码
    private String plateNumber;

    // 司机姓名
    private String userName;

    // 司机手机号
    private String userBill;

    // 维修时间
    private String repairDate;

    // 交付时间
    private String deliveryDate;

    // 消费金额
    private String totalFee;

    private String oilRateInvoice; // 商品开票折扣率

    // 状态
    private String repairState;
    private String repairStateName;

    // id
    private Long repairId;

    private Integer hasVer;

    private Integer state;
    private String stateStr;

    public String getStateStr() {
        // 状态 1保养费 2维修费
        if (getState() != null) {
            if (getState() == 1) {
                return "保养";
            } else if (getState() == 2) {
                return "维修";
            }
        }
        return "";
    }

}
