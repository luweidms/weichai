package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: QuickAddDriverVo
 * @Package: com.youming.youche.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/19 9:09
 * @company:
 */
@Data
public class QuickAddDriverVo implements Serializable {

    /**
     * 司机手机号
     */
    private  String loginAcct;

    /**
     * 司机姓名
     */
    private String linkman;
    /**
     *
     */
    private Long vehicleCode;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 车辆类型(1:厢车 2:平板 3:高栏 4:冷柜 7:油罐车 8:轿运车 9: 奶罐车 10:小货车 5:其他车 11:摆渡车)
     */
    private Integer vehicleStatus;
    /**
     * 车型（公共接口 获取所有车型车长)
     */
    private String vehicleLength;
    /**
     * 牌照类型(1:整车 2:拖头)
     */
    private Integer licenceType;
    /**
     * 二维码地址
     */
    private String qrCodeUrl;
}
