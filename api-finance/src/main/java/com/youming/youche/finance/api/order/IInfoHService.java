package com.youming.youche.finance.api.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.order.InfoH;
import com.youming.youche.finance.domain.order.OrderInfo;
import com.youming.youche.finance.dto.order.OrderDealInfoDto;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.finance.vo.order.OrderDealInfoVo;

/**
* <p>
    * 新的订单表 服务类
    * </p>
* @author liangyan
* @since 2022-03-15
*/
    public interface IInfoHService extends IBaseService<InfoH> {

    /**
     * 通过orderId查询order Info数据
     * @param orderId
     * @return
     * @throws Exception
     */
    InfoH getOrderInfoHByOrderId(Long orderId)throws Exception;


    /**
     * 查询司机账户未到期明细
     * @param accessToken
     * @param orderDealInfoVo
     * @return
     */
    IPage<OrderDealInfoDto> getDealOrderInfo(String accessToken, OrderDealInfoVo orderDealInfoVo);

    /**
     * 导出司机账户未到期明细
     * @param accessToken
     * @param orderDealInfoVo
     * @param importOrExportRecords
     */
    void downloadExcelFile(String accessToken, OrderDealInfoVo orderDealInfoVo, ImportOrExportRecords importOrExportRecords);

}
