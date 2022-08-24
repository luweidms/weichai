package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/3/30 10:38
 */
@Data
public class HasPermissionDto implements Serializable {

    private static final long serialVersionUID = 8009742423251321451L;

    private Boolean hasIncomePermission;

    private Boolean hasCostPermission;

}
