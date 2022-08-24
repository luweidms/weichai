package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@Data
@Accessors(chain = true)
public class BillAccountTenantRel extends BaseDomain  {

    private static final long serialVersionUID = 1L;


    /**
     * 账户SEQ
     */
    private Long accountSeq;

    /**
     * 账户名称
     */
    private String acctName;

    /**
     * 开票信息与收件人信息的关联主键
     */
    private Long billInfoReceiveRel;

    /**
     * 是否为默认售票主体
     */
    private Boolean defaultFlag;

    /**
     * 车队id
     */
    private Long tenantId;


}
