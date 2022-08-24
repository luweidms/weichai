package com.youming.youche.finance.api.ac;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.ac.ApplyOpenBill;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.dto.ac.BillManageDto;
import com.youming.youche.finance.vo.ac.QueryInvoiceVo;

import java.util.List;

/**
 * <p>
 * 申请开票记录表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
public interface IApplyOpenBillService extends IBaseService<ApplyOpenBill> {

    /**
     * 票据服务费支付成功后回写开票申请记录状态
     *
     * @param payout 开票申请记录流水id(已写到payout_intf表里orderId)
     */
    void payForBillServiceWriteBack(PayoutIntf payout, String accessToken);

    /**
     * 根据申请号，查询开票申请记录
     */
    List<ApplyOpenBill> getApplyOpenBill(String applyNum);

    /**
     * 查询抵扣票
     */
    List<ApplyOpenBill> getDeductionBillByParentFlowId(Long parentFlowId);

    /**
     * 服务商小程序 票据管理-列表 [21160]
     *
     * @param accessToken
     * @param queryInvoiceVo
     * @return
     */
    IPage<BillManageDto> doQueryInvoice(String accessToken, QueryInvoiceVo queryInvoiceVo, Integer pageNum, Integer pageSize);

}
