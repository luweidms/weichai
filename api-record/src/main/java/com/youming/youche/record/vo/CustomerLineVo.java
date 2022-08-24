package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/1/14
 */
@Data
public class CustomerLineVo implements Serializable {

    private Long lineId;

    private String backhaulNumber;

    private String lineCodeRule;

    private Integer state;
}
