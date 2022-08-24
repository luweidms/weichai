package com.youming.youche.finance.domain.payable;

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
 * @author zag
 * @since 2022-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CutDataRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String dealRemark;

    private Integer dealState;

    private LocalDateTime dealTime;

    private Long flowIdOne;

    private Long flowIdTwo;

    private String note;

    private Long opId;

    private Long tenantId;

    private Integer type;

    private Long updateOpId;


}
