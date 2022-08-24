package com.youming.youche.system.domain;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author hzx
 * @since 2022-05-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysEntity extends BaseDomain {

    private String entityName;

    private Integer entityType;

    private LocalDateTime lastModifyDate;

    private Long lastModifyOperatorId;

    private String remark;

    private Integer state;

    private Long tenantId;


}
