package com.youming.youche.system.business.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class BusinessIdDto implements Serializable {


    private static final long serialVersionUID = 4080016876576216675L;

    private Long id;
}
