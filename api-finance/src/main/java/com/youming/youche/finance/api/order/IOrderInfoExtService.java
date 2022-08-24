package com.youming.youche.finance.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.OilTurnEntity;
import com.youming.youche.finance.domain.order.OrderInfoExt;
import com.youming.youche.finance.dto.OilCardRechargeDto;
import com.youming.youche.finance.dto.PayoutInfoOutDto;
import com.youming.youche.finance.dto.order.TurnCashDto;
import com.youming.youche.finance.vo.OilCardRechargeVo;
import com.youming.youche.finance.vo.QueryPayOutVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* <p>
    * 订单扩展表 服务类
    * </p>
* @author liangyan
* @since 2022-03-15
*/
public interface IOrderInfoExtService extends IBaseService<OrderInfoExt> {
    /**
     * 通过orderId查询order_info_Ext
     * @param orderId
     * @return
     */
    OrderInfoExt getOrderInfoExtByOrderId(Long orderId);

    /**
     * 油卡和ETC转现(根据月份查询某个账号的油卡和ETC)
     *
     * @param userId      转移用户
     * @param month       转移月份
     * @param turnType    转移类型 1油转现，2ETC转现
     * @param turnOilType 1油转现金 2油转实体卡
     * @param userType    用户类型
     */
    TurnCashDto queryOilAndEtcBalance(Long userId, String month, String turnType, String turnOilType,
                                      Integer userType, String accessToken);


    /**
     * 支付结果
     * @param queryPayOutVo
     * @return
     */
    Page<PayoutInfoOutDto> queryPayOut(QueryPayOutVo queryPayOutVo, String accessToken);


    /**
     * 支付结果导出
     * @param queryPayOutVo
     * @param accessToken
     * @param importOrExportRecords
     */
    void queryPayOutListExport(QueryPayOutVo queryPayOutVo, String accessToken, ImportOrExportRecords importOrExportRecords);


    /**
     * 油转油卡列表查询
     * @param oilCardRechargeVo
     * @param accessToken
     * @return
     */
    Page<OilCardRechargeDto> oilCardRecharge(OilCardRechargeVo oilCardRechargeVo,String accessToken);


    /**
     * 油转油卡列表导出
     * @param oilCardRechargeVo
     * @param accessToken
     * @param importOrExportRecords
     */
    void oilCardRechargeListExport(OilCardRechargeVo oilCardRechargeVo, String accessToken, ImportOrExportRecords importOrExportRecords);


    /**
     * 油转油卡核销
     *
     * @param ids oil_turn_entity表主键
     */
    void verificatOilTurnEntity(String ids,String accessToken);


}
