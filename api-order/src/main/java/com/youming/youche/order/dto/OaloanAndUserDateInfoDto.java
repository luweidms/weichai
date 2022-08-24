package com.youming.youche.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.capital.domain.UserDataInfo;
import com.youming.youche.order.domain.order.OaLoan;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class OaloanAndUserDateInfoDto implements Serializable {

    private OaLoan oaloan;

    private UserDataInfo userDataInfo;

}
