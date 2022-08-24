package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 时效罚款申诉表
 * </p>
 *
 * @author hzx
 * @since 2022-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderAgingAppealInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


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

    @TableField(exist = false)
    private String[] imgUrlArr;

    @TableField(exist = false)
    private List<SysOperLog> operLogs;

    @TableField(exist = false)
    private String auditStsName;

}
