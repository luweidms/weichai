package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CfgVO implements Serializable {
    private List<String> cfgNames;

    private int system;

    private Long tenantId;
}
