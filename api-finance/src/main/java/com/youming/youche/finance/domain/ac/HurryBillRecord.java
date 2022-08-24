package com.youming.youche.finance.domain.ac;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-24
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class HurryBillRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 申请号
     */
    private String applyNum;

    /**
     * 是否催平台票：1平台票，2非平台票
     */
    private Integer hurryBillType;

    /**
     * 催票方用户id
     */
    private Long hurryUserId;

    /**
     * 催票方
     */
    private String hurryUserName;

    /**
     * 处理状态：null初始化，1成功，2失败
     */
    private Integer isReport;

    /**
     * 是否查看 1:未查看 2:已查看
     */
    private Integer lookType;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 开票方用户id
     */
    private Long openUserId;

    /**
     * 开票方
     */
    private String openUserName;

    /**
     * apply_open_bill表主键id
     */
    private Long otherFlowId;

    /**
     * 催票描述
     */
    private String remark;

    /**
     * 处理描述
     */
    private String reportRemark;

    /**
     * 处理时间
     */
    private LocalDateTime reportTime;

    /**
     * 车队ID
     */
    private Long tenantId;

    /**
     * 修改用户ID
     */
    private Long updateOpId;
}
