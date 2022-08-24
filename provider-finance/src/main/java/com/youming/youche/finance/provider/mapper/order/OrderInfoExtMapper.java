package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.order.OrderInfoExt;
import com.youming.youche.finance.dto.OilCardRechargeDto;
import com.youming.youche.finance.dto.PayoutInfoOutDto;
import com.youming.youche.finance.vo.OilCardRechargeVo;
import com.youming.youche.finance.vo.QueryPayOutVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 订单扩展表Mapper接口
* </p>
* @author liangyan
* @since 2022-03-15
*/
public interface OrderInfoExtMapper extends BaseMapper<OrderInfoExt> {
    /**
     * 支付结果
     * @param page
     * @param queryPayOutVo
     * @return
     */
    Page<PayoutInfoOutDto> queryPayOut(@Param("page") Page<PayoutInfoOutDto> page, @Param("queryPayOutVo") QueryPayOutVo queryPayOutVo,
                                       @Param("payObjId") Long payObjId, @Param("payUserType") Integer payUserType,
                                       @Param("hasAllDataPermission") boolean hasAllDataPermission, @Param("orgList") List<Long> orgList);


    List<PayoutInfoOutDto> queryPayOutListExport(@Param("queryPayOutVo") QueryPayOutVo queryPayOutVo,
                                       @Param("payObjId") Long payObjId, @Param("payUserType") Integer payUserType,
                                       @Param("hasAllDataPermission") boolean hasAllDataPermission, @Param("orgList") List<Long> orgList);


    /**
     * 油转油卡列表查询
     * @param oilCardRechargeVo
     * @return
     */
    Page<OilCardRechargeDto> oilCardRecharge(@Param("page") Page<OilCardRechargeDto> page,@Param("oilCardRechargeVo") OilCardRechargeVo oilCardRechargeVo);

    /**
     * 油转油卡列表导出
     * @param oilCardRechargeVo
     * @return
     */
    List<OilCardRechargeDto> oilCardRechargeExport(@Param("oilCardRechargeVo") OilCardRechargeVo oilCardRechargeVo);



}
