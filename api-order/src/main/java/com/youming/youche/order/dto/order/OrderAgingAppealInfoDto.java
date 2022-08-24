package com.youming.youche.order.dto.order;

import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/7/18 13:45
 */
@Data
public class OrderAgingAppealInfoDto implements Serializable {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 时效罚款id
     */
    private Long agingId;

    /**
     * 操作人名称
     */
    private String opName;

    /**
     * 申诉备注
     */
    private String remark;

    /**
     * 申诉图片id
     */
    private String imgIds;

    /**
     * 申诉图片地址
     */
    private String imgUrls;

    /**
     * 审核状态
     */
    private Integer auditSts;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 处理金额
     */
    private Long dealFinePrice;

    private String[] imgUrlArr;

    private List<SysOperLog> operLogs;

    private String auditStsName;
}
