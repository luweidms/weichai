package com.youming.youche.finance.domain.ac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/4/11 16:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmSalaryTemplate extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 年月
     */
    private String templateMonth;

    /**
     * 版本号
     */
    private String ver;

    /**
     * 状态：1-有效 0-无效
     */
    private Integer state;

    /**
     * 操作员ID
     */
    private Long opId;

    /**
     * 操作员
     */
    private String opName;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 修改操作员ID
     */
    private Long updateOpId;

    /**
     * 租户ID
     */
    private Long tenantId;
}
