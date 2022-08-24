package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysStsticDataDto implements Serializable {
    private static final long serialVersionUID = -465823680196774188L;

    private String codeName;
    private String codeValue;
    private int sortId;
    private String codeDesc;
    private String codeType;
    private List listData;
}
