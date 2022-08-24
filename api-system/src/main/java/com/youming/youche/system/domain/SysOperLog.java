package com.youming.youche.system.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysOperLog extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 操作员编号
     */
    private Long opId;

    /**
     * 操作人名称
     */
    private String opName;

    /**
     * 业务编码
     */
    private Integer busiCode;

    /**
     * 业务名称
     */
    private String busiName;

    /**
     * 操作名称:例如新增，修改
     */
    private String operTypeName;

    /**
     * 操作类型
     */
    private Integer operType;

    /**
     * 业务数据的主键
     */
    private Long busiId;

    /**
     * 操作的日志描述
     */
    private String operComment;

    /**
     * 1：APP-ANDROID2：APP-IOS3:WEB
     */
    private String channelType;

    /**
     * 租户ID
     */
    private Long tenantId;


}
