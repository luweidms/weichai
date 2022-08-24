package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/4/21 15:58
 */
@Data
public class GetTenantsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收款人id 和 收款人名称
     */
    private List<Map> tenantList;

}
