package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EntitysDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> entityIds;

}
