package com.youming.youche.system.domain.ac;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-02-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PayFeeLimit extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Integer type;

    private Integer subType;

    private String subTypeName;

    private Long value;

    private LocalDateTime createDate;

    private Long opId;

    private String opName;

    private LocalDateTime opDate;

    private String remark;

    private Long tenantId;


}
