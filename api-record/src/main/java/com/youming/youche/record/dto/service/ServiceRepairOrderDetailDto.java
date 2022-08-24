package com.youming.youche.record.dto.service;

import com.youming.youche.record.domain.oa.OaFiles;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.domain.sys.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @description 车辆维保详情
 * @date 2022/1/21 14:08
 */
@Data
public class ServiceRepairOrderDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;
    // 维修保养订单信息
    private ServiceRepairOrder serviceRepairOrder;
    // 详细地址
    private String address;
    // 服务电话
    private String serviceCall;
    // 维修项目
    private String itemStr;
    //总的配件费
    private String totalPartsPrice;
    //总的人工费
    private String totalItemPrice;
    //总的配件费
    private String totalPartsPriceVer;
    //总的人工费
    private String totalItemPriceVer;
    // 消费总金额(分)
    private Long totalAmountVer;
    //上传图片
    private List<OaFiles> picList;
    //维修项目
    private List<Map> items;
    //审核信息
    private List<SysOperLog> sysOperLogList;
    // 车牌号
    private String vehicleCode;
    // 维保里程
    private Integer maintenanceDis;
    // 最后维保时间
    private String lastMaintenanceDate;
    // 品牌型号
    private String brandModel;
    // 品牌单价
    private Double brandPrice;
    //核算金额
    private Double checkAmount;
    // 描述
    private String desc;
    // 服务商名称
    private String serviceName;
}
