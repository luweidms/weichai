package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TenantAccountFlowQueryVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/24 18:21
 */
@Data
public class TenantAccountFlowQueryVo implements Serializable {

    /** 帐户Id */
    private Long accountId;
    /** 流水号 */
    private String serialNumber;
    /** 交易类型 */
    private String tranType;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;

}
