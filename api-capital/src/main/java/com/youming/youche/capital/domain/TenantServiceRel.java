package com.youming.youche.capital.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author Terry
* @since 2022-03-02
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantServiceRel extends BaseDomain {

    private static final long serialVersionUID = 1L;
    private int type;
    //服务名称
    private String serviceName;
    //授信金额
    private long quotaAmt;
    private Long quotaAmtL;
    private String quotaAmtStr;
    public String getQoutaAmtStr(){
        if(null == quotaAmtL){
            this.setQuotaAmtStr("不限额");
        }else {
            this.setQuotaAmtStr(quotaAmt+"");
        }
        return this.quotaAmtStr;
    }
    //已使用授信金额
    private long useQuotaAmt;
    //未使用授信金额
    private long notUseQuotaAmt;
    //用户编码
    private long serviceUserId;



}
