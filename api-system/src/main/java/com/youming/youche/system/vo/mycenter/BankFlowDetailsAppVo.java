package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/4/25
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class BankFlowDetailsAppVo implements Serializable {
    private Long userId;
    private String queryType; //查询类型：1服务商  2车队
    private String month;
    private Integer userType;
    private String flowType;
    private List<String> pinganAccIdList;
}
