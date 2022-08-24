package com.youming.youche.order.domain.order;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.commons.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostReport extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 空载校准里程 单位(米)
     */
    private Long capacityLoadMileage;

    /**
     * 校准时间
     */
    private LocalDateTime checkTime;

    /**
     * 交车仪表盘公里数 单位(米)
     */
    private Long endKm;

    /**
     * 交车仪表盘公里数 附件
     */
    private Long endKmFile;

    /**
     * 交车仪表盘公里数 附件URL
     */
    private String endKmUrl;

    /**
     * 是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAudit;

    /**
     * 是否曾经审核通过 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAuditPass;

    /**
     * 是否加满油(0未加满油 1已加满油)
     */
    private Integer isFullOil;

    /**
     * 载重校准里程 单位(米)
     */
    private Long loadMileage;

    /**
     * 装货仪表盘公里数 单位(米)
     */
    private Long loadingKm;

    /**
     * 装货仪表盘公里数 附件
     */
    private Long loadingKmFile;

    /**
     * 装货仪表盘公里数 附件URL
     */
    private String loadingKmUrl;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 操作人
     */
    private String opName;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 出车仪表盘公里数 单位(米)
     */
    private Long startKm;

    /**
     * 出车仪表盘公里数附件
     */
    private Long startKmFile;

    /**
     * 出车仪表盘公里数附件URL
     */
    private String startKmUrl;

    /**
     * 上报状态 0未提交 1已提交 2待审核 3审核中 4审核不通过 5审核通过
     */
    private Integer state;

    /**
     * 提交时间
     */
    private LocalDateTime subTime;

    /**
     * 提交人
     */
    private Long subUserId;

    /**
     * 提交人
     */
    private String subUserName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 卸货仪表盘公里数 单位(米)
     */
    private Long unloadingKm;

    /**
     * 卸货仪表盘公里数 附件
     */
    private Long unloadingKmFile;

    /**
     * 卸货仪表盘公里数 附件URL
     */
    private String unloadingKmUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private boolean isEdit = true;

    public boolean attributeIsEmpty(Object... objects) {
        if (null == objects) {
            return true;
        }
        for (int i = 0; i < objects.length; i++) {
            Object value = objects[i];
            if (value instanceof Long) {
                Long longValue = (Long) value;
                return (null == longValue);
            } else if (value instanceof String) {
                return StringUtils.isBlank(value.toString());
            } else if (value instanceof Integer) {
                Integer inValue = (Integer) value;
                return (null == inValue);
            }
        }
        return true;
    }

    public void checkData() {

        if (!isEdit) {
            return;
        }
        if (!attributeIsEmpty(startKm, startKmFile)) {
            if (null == startKm || startKm < 0) {
                throw new BusinessException("请输入出车仪表盘公里数！");
            }
            if (null == startKmFile || startKmFile < 0) {
                throw new BusinessException("请上传出车仪表盘公里数附件！");
            }

        }
        if (!attributeIsEmpty(loadingKm, loadingKmFile)) {
            if (null == loadingKm || loadingKm < 0) {
                throw new BusinessException("请输入装货仪表盘公里数！");
            }
            if (null == loadingKmFile || loadingKmFile < 0) {
                throw new BusinessException("请上传装货仪表盘公里数附件！");
            }
        }
        if (!attributeIsEmpty(unloadingKm, unloadingKmFile)) {
            if (null == unloadingKm || unloadingKm < 0) {
                throw new BusinessException("请输入卸货仪表盘公里数！");
            }
            if (null == unloadingKmFile || unloadingKmFile < 0) {
                throw new BusinessException("请上传卸货仪表盘公里数附件！");
            }
            if (null == loadingKm || loadingKm < 0) {
                throw new BusinessException("请输入装货仪表盘公里数！");
            }
            if (null == loadingKmFile || loadingKmFile < 0) {
                throw new BusinessException("请上传装货仪表盘公里数附件！");
            }
        }
        if (!attributeIsEmpty(endKm, endKmFile)) {
            if (null == endKm || endKm < 0) {
                throw new BusinessException("请输入交车仪表盘公里数！");
            }
            if (null == endKmFile || endKmFile < 0) {
                throw new BusinessException("请上传交车仪表盘公里数附件！");
            }
            if (null == isFullOil || isFullOil < 0) {
                throw new BusinessException("请选择是否加满油！");
            }
        }

        /**
         * 出车仪表盘公里数：
         装货仪表盘公里数：上报时，需检测出车仪表盘公里数是否有值，出车仪表盘公里数没有上报则该项不可上报，数值必须大于等于出车仪表盘公里数
         卸货仪表盘公里数：上报时，需检测出车仪表盘公里数是否有值，装货仪表盘公里数没有上报则该项不可上报，数值必须大于等于（出车仪表盘公里数 | 装货仪表盘公里数）
         交车仪表盘公里数：上报时，需检测出车仪表盘公里数是否有值，出车仪表盘公里数没有上报则该项不可上报，数值必须大于等于(卸货仪表盘公里数 | 装货仪表盘公里数 | 出车仪表盘公里数)
         */
        if (!attributeIsEmpty(loadingKm, loadingKmFile) || !attributeIsEmpty(unloadingKm, unloadingKmFile) || !attributeIsEmpty(endKm, endKmFile)) {
            if (null == startKm || startKm < 0) {
                throw new BusinessException("请输入出车仪表盘公里数！");
            }
            if (null == startKmFile || startKmFile < 0) {
                throw new BusinessException("请上传出车仪表盘公里数附件！");
            }
        }

        if (null != startKm && null != loadingKm && loadingKm < startKm) {
            throw new BusinessException("装货仪表盘公里数必须大于出车仪表盘公里数！");
        }

        if (null != loadingKm && null != unloadingKm && unloadingKm < loadingKm) {
            throw new BusinessException("卸货仪表盘公里数必须大于装货仪表盘公里数！");
        } else if (null != startKm && null != unloadingKm && unloadingKm < startKm) {
            throw new BusinessException("卸货仪表盘公里数必须大于出车仪表盘公里数！");
        }


        if (null != unloadingKm && null != endKm && endKm < unloadingKm) {
            throw new BusinessException("交车仪表盘公里数必须大于卸货仪表盘公里数！");
        } else if (null != loadingKm && null != endKm && endKm < loadingKm) {
            throw new BusinessException("交车仪表盘公里数必须大于装货仪表盘公里数！");
        } else if (null != startKm && null != endKm && endKm < startKm) {
            throw new BusinessException("交车仪表盘公里数必须大于出车仪表盘公里数！");
        }
    }

    public void submitCheckData() {
        if (!isEdit) {
            return;
        }
        if (null == startKm || startKm < 0) {
            throw new BusinessException("请输入出车仪表盘公里数！");
        }
        if (null == startKmFile || startKmFile < 0) {
            throw new BusinessException("请上传出车仪表盘公里数附件！");
        }
        if (null == endKm || endKm < 0) {
            throw new BusinessException("请输入交车仪表盘公里数！");
        }
        if (null == endKmFile || endKmFile < 0) {
            throw new BusinessException("请上传交车仪表盘公里数附件！");
        }
        if (null == isFullOil || isFullOil < 0) {
            throw new BusinessException("请选择是否加满油！");
        }
        this.checkData();
    }
}
