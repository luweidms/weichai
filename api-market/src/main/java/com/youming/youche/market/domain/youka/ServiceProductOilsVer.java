package com.youming.youche.market.domain.youka;

    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableId;
    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 服务商油表
    * </p>
* @author XXX
* @since 2022-03-24
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ServiceProductOilsVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    private Long hisId;
    /**
     * 区名称
     */
    private String areaCode;
    /**
     * 城市名称
     */
    private String cityCode;
    /**
     * 城市ID
     */
    private Integer cityId;
    /**
     * 区ID
     */
    private Integer countyId;

    private LocalDateTime createDate;
    /**
     * 是否停用(0:停用 1:可用)
     */
    private Integer isStop;
    /**
     * 油品唯一标识
     */
    private String oilsId;
    /**
     * 油品级别(国V、国IV)
     */
    private String oilsLevel;
    /**
     * 油品名称(柴油、汽油等)
     */
    private String oilsName;
    /**
     * 油品类型(0#，97#)
     */
    private String oilsType;
    /**
     * 站点价格
     */
    private Long originalPrice;
    /**
     * 结算价格
     */
    private Long price;
    /**
     * 服务商产品id
     */
    private Long productId;
    /**
     * 省份名称
     */
    private String provinceCode;
    /**
     * 省份ID
     */
    private Integer provinceId;
    /**
     * 加油站id(找油网标识)
     */
    private String stationId;
    /**
     * 更新时间
     */
    private LocalDateTime updateDate;


}
