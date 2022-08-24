package com.youming.youche.order.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 * @author liangyan
 * @since 2022-03-25
 */
@Data
public class CopilotMapVo implements Serializable {

    private static final long serialVersionUID = 596295361879305839L;

    /**
     * 副驾驶补贴
     */
    private Long copilotSalary;

    /**
     * 预估副驾驶补贴天数
     */
    private Long subsidyCopilotDay;

    /**
     * 副驾驶每天补贴金额
     */
    private Long copilotDaySalary;

    /**
     * 副驾驶月工资
     */
    private Long monthSubSalary;

    /**
     * 副驾补贴具体时间
     */
    private String copilotSubsidyTime;

    /**
     * 副驾驶工资模式：1普通，2里程，3按趟
     */
    private Integer copilotSalaryPattern;
}
