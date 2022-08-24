package com.youming.youche.order.domain.trailer;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hzx
 * @since 2022-03-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TrailerManagement extends BaseDomain {

    /**
     * 挂车牌
     */
    private String trailerNumber;

    /**
     * 车长
     */
    private String trailerLength;

    /**
     * 车型
     */
    private String trailerStatus;

    /**
     * 吨位
     */
    private String trailerLoad;

    /**
     * 容积
     */
    private String trailerVolume;

    /**
     * 审核时间
     */
    private LocalDateTime authDate;

    /**
     * 状态 1为在途 2为在台
     */
    private Integer isState;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 始发省
     */
    private Integer sourceProvince;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 始发县区
     */
    private Integer sourceCounty;

    /**
     * 到达省
     */
    private Integer desProvince;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 到达县区
     */
    private Integer desCounty;

    /**
     * 起始地
     */
    private String source;

    /**
     * 目的地
     */
    private String des;

    private String carVersion;

    private String brandModel;

    private String operCerti;

    private String vinNo;

    /**
     * 宽
     */
    private String trailerWide;

    /**
     * 高
     */
    private String trailerHigh;

    /**
     * 桥数 1单桥、2双桥
     */
    private Integer trailerBridgeNumber;

    /**
     * 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     */
    private Integer trailerMaterial;

    /**
     * 登记日期
     */
    private LocalDate registationTime;

    /**
     * 图片url
     */
    private String trailerPictureUrl;

    /**
     * 图片
     */
    private Long trailerPictureId;

    /**
     * 登记证号
     */
    private String registrationNumble;

    /**
     * 行驶证有效期
     */
    private LocalDate vehicleValidityTime;

    /**
     * 营运证有效期
     */
    private LocalDate operateValidityTime;

    /**
     * 有效期 1:运营证 2：行驶证
     */
    private Integer validityTime;

    /**
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer isAutit;

    /**
     * 行驶证到期时间
     */
    private LocalDate vehicleValidityExpiredTime;

    /**
     * 运营证到期时间
     */
    private LocalDate operateValidityExpiredTime;

    /**
     * 运营证图片url
     */
    private String operCertiUrl;

    private LocalDateTime createDate;

    private LocalDate registrationTime;

    /**
     * 0启用、1闲置
     */
    private Integer idle;


}
