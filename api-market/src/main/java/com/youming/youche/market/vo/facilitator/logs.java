package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class logs implements Serializable {

    private String operComment;

    protected LocalDateTime createDate;

    private Long id;
}
