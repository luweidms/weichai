package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/12 13:17
 */
@Data
public class QueryOrderResponsiblePartyDto implements Serializable {

    private String name;

    private Integer type;
}
