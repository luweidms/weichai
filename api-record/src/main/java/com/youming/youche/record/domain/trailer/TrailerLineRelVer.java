package com.youming.youche.record.domain.trailer;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 挂车心愿线路怒表
 * </p>
 *
 * @author Terry
 * @since 2022-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TrailerLineRelVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 关系ID
     */
    private Long relId;

    /**
     * 车辆ID
     */
    private Long trailerId;

    /**
     * 车牌号
     */
    private String trailerNumber;

    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 线路类型
     */
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

    /**
     * 修改操作人
     */
    private Long updateOpId;

    /**
     * 是否有效 0无效 1有效
     */
    private Integer sts;

    /**
     * 1不可用 0可用 8被移除
     */
    private Integer verState;

    /**
     * 挂车主表历史id
     */
    private Long tailerHisId;

    /**
     * 是否审批成功 1-是
     */
    private Integer isAuthSucc;


}
