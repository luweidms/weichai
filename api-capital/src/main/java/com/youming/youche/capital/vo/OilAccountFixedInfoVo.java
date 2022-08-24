package com.youming.youche.capital.vo;


import com.youming.youche.capital.domain.TenantServiceRel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 加油账户统计详细信息
 * */
@Data
@Accessors(chain = true)
public class OilAccountFixedInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //总的授信额度
    private Long allQuotaAmt;
    //固定额度统计
    private TenantServiceRel quota;
    //不限额度统计
    private TenantServiceRel notQuota;
}