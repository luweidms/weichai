package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.dto.facilitator.OilPricePeovinceDto;
import com.youming.youche.market.dto.facilitator.OilPriceProvinceDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 全国油价表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-08
 */
public interface IOilPriceProvinceService extends IBaseService<OilPriceProvince> {
    /**
     * 查询油价
     *
     * @param provinceId
     * @return
     */
    Long getOilPrice(long provinceId);

    /**
     * 计算油价
     *
     * @param tenantProductRel
     * @param oilPriceProvince
     * @return
     */
    Long calculationOilPrice(TenantProductRel tenantProductRel, Long oilPriceProvince, Boolean isShare, Boolean isBill);

    /**
     * @param floatBalance
     * @param fixedBalance
     * @param originalPrice
     * @param serviceCharge
     * @return
     */
    Long calculationSharePrice(String floatBalance, Long fixedBalance, Long originalPrice, String serviceCharge);

    /**
     * 全国油价分页
     *
     * @param provinceId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<OilPriceProvince> queryOilPrice(Long provinceId, Integer pageNum, Integer pageSize);

    /**
     * 修改价格
     *
     * @param oilPrice
     * @return
     */
    ResponseResult updateOilPriceProvince(OilPriceProvinceDto oilPrice,String accessToken);


    /**
     * 油价导入
     *
     * @param
     * @return
     */
    ResponseResult importOilPriceExcel(List<OilPricePeovinceDto> oilPriceProvince, String accessToken);

//    /**
//     * 油站二维码生成
//     * @return
//     * @throws Exception
//     */
//    Map<String,Object> orCodeByOilInd(Long productId) throws Exception;

    /**
     *  获取全国油价信息
     * @param provinceId
     * @return
     */
     OilPriceProvince getOilPriceProvince(Integer provinceId);

}
