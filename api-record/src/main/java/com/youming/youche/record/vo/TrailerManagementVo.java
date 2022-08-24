package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/1/19
 */
@Data
public class TrailerManagementVo implements Serializable {

    /**
     * 挂车id
     */
    private Long id;

    /**
     * 挂车牌
     */
    private String trailerNumber;

    /**
     * 始发省
     */
    private Integer sourceProvince;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 始发区
     */
    private Integer sourceCounty;


    private String positionStatusName;

    /**
     * 状态 1为在途 2为在台
     */
    private Integer isState;

    /**
     * 使用状况
     */
    private Integer usageCount;

    /**
     * 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     */
    private Integer trailerMaterial;

    /**
     * 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     */
    private String trailerMaterialName;

    /**
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer isAutit;

    /**
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer auditState;

    /**
     * 审核（1审核、0不审核）
     */
    private Integer audit;

    private String auditStateName;

    /**
     * 0 闲置 1禁用
     */
    private Short idle;

    public String getAuditStateName() {
        if (auditState == 0) {
            auditStateName = "待审核";
        } else if (auditState == 1) {
            auditStateName = "审核通过";
        } else if (auditState == 2) {
            auditStateName = "审核不通过";
        }
        return auditStateName;
    }

}
