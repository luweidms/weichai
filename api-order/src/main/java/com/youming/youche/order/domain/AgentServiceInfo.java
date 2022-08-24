package com.youming.youche.order.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author zengwen
 * @since 2022-04-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AgentServiceInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 代收服务商类型
     */
    private Integer agentServiceType;
    /**
     * 返利时间
     */
    private LocalDateTime calRebateTime;
    /**
     * 消费记录导入时间
     */
    private LocalDateTime consumptionImtTime;
    /**
     * 管车宝账号
     */
    private String gcbAccount;
    /**
     * 管车宝密碼
     */
    private String gcbPassword;
    /**
     * 路歌油站ID
     */
    private String lgOilStationId;
    /**
     * 不开票状态 1启用 0暂停
     */
    private Integer noBillState;
    /**
     * 加油返利
     */
    private String rebate;
    /**
     * 开票返利
     */
    private String rebateBill;
    /**
     * 返利月份
     */
    private String rebateMonth;
    /**
     * 用戶id
     */
    private Long serviceUserId;
    /**
     * 状态
     */
    private Integer state;


}
