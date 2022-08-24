package com.youming.youche.record.domain.contract;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author Mrchen
 * @since 2022-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Contract extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 合同编号
     */
    private String htBh;

    /**
     * 客户名称
     */
    private String htName;

    /**
     * 合同内容
     */
    private String htContent;

    /**
     * 图片附件
     */
    private String htAccessory;

    /**
     * 签订日期
     */
    private String htCreatetime;
    /**
     * 到期日期
     */
    private String htLefttime;

    /**
     * 操作时间
     */
    private String htUpdatetime;

    /**
     * 车队id
     */
    private Long tenantId;

}
