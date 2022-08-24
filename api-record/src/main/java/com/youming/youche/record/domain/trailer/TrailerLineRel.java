package com.youming.youche.record.domain.trailer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 挂车心愿线路表
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TrailerLineRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 挂车ID
     */
    private Long trailerId;

    /**
     * 挂车车牌号
     */
    private String trailerNumber;

    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 线路类型
     */
    @TableField("STATE")
    private Integer state;

    /**
     * 线路编号
     */
    private String lineCodeRule;

    /**
     * 操作人员
     */
    private Long opId;

    /**
     * 操作时间
     */
    private LocalDateTime opDate;


}
