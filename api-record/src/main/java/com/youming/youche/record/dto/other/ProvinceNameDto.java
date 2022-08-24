package com.youming.youche.record.dto.other;

import com.youming.youche.commons.util.DataFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProvinceNameDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id; // 省市县id
    private String name; // 省市县名称
    private Long parentId; // 市县父id
}
