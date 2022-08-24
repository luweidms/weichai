package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.dto.ReceivableOverdueBalanceDto;

import java.util.List;

/**
 * <p>
 * 应收逾期账单表 服务类
 * </p>
 *
 * @author wuhao
 * @since 2022-07-02
 */
public interface IOverdueReceivableService extends IBaseService<OverdueReceivable> {

    /***
     * @Description: 根据业务编码查询逾期账单数据
     * @Author: luwei
     * @Date: 2022/7/2 17:19
     * @Param businessCode:
     * @return: com.youming.youche.order.domain.order.OverdueReceivable
     * @Version: 1.0
     **/
    OverdueReceivable getBusinessCode(String businessCode);

    /***
     * @Description: 统计应收逾期总额
     * @Author: luwei
     * @Date: 2022/7/4 14:13
     * @Param tanantId:
     * @return: java.lang.Long
     * @Version: 1.0
     **/
    Long sumOverdueReceivable(Long tanantId, String name, Long adminUserId,Integer type);

    /***
     * @Description: 车队账户明细
     * @Author: luwei
     * @Date: 2022/7/4 14:13
     * @Param tanantId:
     * @return: java.lang.Long
     * @Version: 1.0
     **/
    List<ReceivableOverdueBalanceDto> accountDetails(Long tanantId,String accessToken);

    /**
     * 车队账户明细  应付逾期  指派车队
     */
    List<ReceivableOverdueBalanceDto> accountDetailPayable(Long tenantId, String accessToken);

}
