package com.youming.youche.finance.domain.ac;

    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 驾驶员工资发送信息表
    * </p>
* @author zengwen
* @since 2022-06-29
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalarySendInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 司机工资单ID
            */
    private Long salaryId;

            /**
            * 发送时间
            */
    private LocalDateTime sendTime;


}
