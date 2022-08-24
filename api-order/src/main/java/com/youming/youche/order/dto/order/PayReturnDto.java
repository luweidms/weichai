package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/16 11:57
 */
@Data
public class PayReturnDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int reqCode;
    private String reqMess;
    private String thirdLogNo;

}
