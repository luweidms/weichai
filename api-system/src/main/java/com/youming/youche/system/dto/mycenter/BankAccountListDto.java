package com.youming.youche.system.dto.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccountListVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/23 15:38
 */
@Data
public class BankAccountListDto implements Serializable {

    private Long id;
    private Long userId;
    private Long tenantId;
    private String certNo;
    private String accType;
    private String certName;
    private String status;
    private String result;
    private String merchNo;
    private String mbrNo;
    private String avaBal;
    private Integer cardCount;

}
