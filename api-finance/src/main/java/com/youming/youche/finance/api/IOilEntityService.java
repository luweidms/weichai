package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.OilEntity;
import com.youming.youche.finance.domain.RechargeInfoRecord;
import com.youming.youche.finance.vo.OilEntityInfoDto;
import com.youming.youche.finance.vo.OilEntityVo;
import com.youming.youche.order.domain.order.PayoutIntf;

/**
* <p>
    * 油充值核销表 服务类
    * </p>
* @author WuHao
* @since 2022-04-14
*/
    public interface IOilEntityService extends IBaseService<OilEntity> {

    IPage<OilEntityInfoDto> getOilEntitys(OilEntityVo oilEntityVo,
                                          Integer pageNum, Integer pageSize, String accessToken);

    void batchVerificatOrder(String orderIds,String accessToken);

    /**
     * 油卡导出
     */
    void OilCardList1(String accessToken, ImportOrExportRecords record, OilEntityVo oilEntityVo);

    void doAccountIn(Long userId, Integer state, String remark, Long sourceTenantId, Long userType,String accessToken);

    void updateOilCarNum(String orderId, String oilCarNum, String accessToken);

    /**
     * 撤销
     */
    void revokeOilCharge(String id,String accessToken);

    /**
     * 撤销油卡相关金额回退
     *
     * @param rir
     * @throws Exception
     */
    void revokeOilCharge(RechargeInfoRecord rir,String accessToken);

    /**
     * 撤销油卡相关金额回退
     *
     * @param pay
     * @throws Exception
     */
    void revokeOilCharge(PayoutIntf pay,String accessToken);
}
