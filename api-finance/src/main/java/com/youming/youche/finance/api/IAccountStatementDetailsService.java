package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.finance.domain.AccountStatement;
import com.youming.youche.finance.domain.AccountStatementDetails;
import com.youming.youche.finance.dto.GetAppDetailListDto;
import com.youming.youche.finance.vo.order.AccountStatementInVo;

import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 17:16
 */
public interface IAccountStatementDetailsService extends IBaseService<AccountStatementDetails> {

    /**
     * 批量添加对账单明细
     * @param accountStatement
     * @param list
     */
    void batchSaveDetails(AccountStatement accountStatement, List<AccountStatementInVo> list, LoginInfo loginInfo);

    /**
     * 刷新对账单的ETC费用，在发送、确认、结算对账单时调用
     * @param flowId
     */
    void refreshEtcFeeForBill(long flowId, LoginInfo loginInfo);

    /**
     * 该函数的功能描述:移除车辆费用明细
     * @param id
     * @param loginInfo
     */
    void removeDetail(long id, LoginInfo loginInfo);

    /**
     * APP接口-车辆费用明细-查询列表 接口编号：28305
     */
    Page<GetAppDetailListDto> getAppDetailList(Long accountStatementId, String carDriverName, String carDriverPhone, String plateNumber, Integer vehicleClass, Integer pageNum, Integer pageSize);

    /**
     * 修改该对账单下所有车辆匹配的ETC消费记录，将状态改为已结算，同时反写ETC消费记录对应的对账单编号
     */
    void updateEtcToBill(AccountStatement as, LoginInfo loginInfo);
}
