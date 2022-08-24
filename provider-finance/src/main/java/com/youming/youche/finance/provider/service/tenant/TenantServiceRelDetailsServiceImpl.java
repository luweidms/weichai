package com.youming.youche.finance.provider.service.tenant;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.tenant.ITenantServiceRelDetailsService;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.domain.tenant.TenantServiceRelDetails;
import com.youming.youche.finance.provider.mapper.tenant.TenantServiceRelDetailsMapper;
import com.youming.youche.order.api.order.IConsumeOilFlowExtService;
import com.youming.youche.order.api.order.IConsumeOilFlowService;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.record.api.tenant.ITenantServiceRelService;
import com.youming.youche.record.domain.tenant.TenantServiceRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 授信额度已用流水表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
@DubboService(version = "1.0.0")
public class TenantServiceRelDetailsServiceImpl extends BaseServiceImpl<TenantServiceRelDetailsMapper, TenantServiceRelDetails> implements ITenantServiceRelDetailsService {

    @DubboReference(version = "1.0.0")
    IConsumeOilFlowService iConsumeOilFlowService;

    @DubboReference(version = "1.0.0")
    IConsumeOilFlowExtService iConsumeOilFlowExtService;

    @DubboReference(version = "1.0.0")
    ITenantServiceRelService iTenantServiceRelService;

    @Override
    public void oilExpireWriteBack(PayoutIntf pay) {
        if (pay == null) {
            throw new BusinessException("打款记录不能为空");
        }
        if (pay.getSubjectsId() != null && pay.getSubjectsId() == EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) {
            List<ConsumeOilFlow> list = iConsumeOilFlowService.getConsumeOilFlowByOrderId(pay.getBusiCode(), -1, -1, -1L);
            if (list == null || list.size() <= 0) {
                throw new BusinessException("根据加油业务单号：" + pay.getBusiCode() + " 未找到加油记录");
            }
            for (ConsumeOilFlow flow : list) {
                ConsumeOilFlowExt ext = iConsumeOilFlowExtService.getConsumeOilFlowExtByFlowId(flow.getId());
                if (ext != null) {
                    if (ext.getOilConsumer() == OrderConsts.OIL_CONSUMER.SELF && ext.getCreditLimit() != null && ext.getCreditLimit() == OrderAccountConst.CONSUME_OIL_FLOW_EXT.CREDIT_LIMIT1) {
                        TenantServiceRel tenantServiceRel = iTenantServiceRelService.getTenantServiceRel(flow.getTenantId(), flow.getUserId());
                        if (tenantServiceRel == null) {
                            throw new BusinessException("根据租户id：" + flow.getTenantId() + " 服务商用户id：" + flow.getUserId() + " 未找到服务商与租户关系");
                        }
                        Long beforeAmount = tenantServiceRel.getUseQuotaAmt() == null ? 0L : tenantServiceRel.getUseQuotaAmt();
                        Long afterAmount = tenantServiceRel.getUseQuotaAmt() - flow.getAmount();
                        tenantServiceRel.setUseQuotaAmt(afterAmount);
                        iTenantServiceRelService.saveOrUpdate(tenantServiceRel);
                        TenantServiceRelDetails tenantServiceRelDetails = new TenantServiceRelDetails();
                        tenantServiceRelDetails.setFlowId(ext.getFlowId());
                        tenantServiceRelDetails.setAmount(flow.getAmount());
                        tenantServiceRelDetails.setOpType(2);//减少
                        tenantServiceRelDetails.setTenantId(ext.getTenantId());
                        tenantServiceRelDetails.setBeforeAmount(beforeAmount);
                        tenantServiceRelDetails.setAfterAmount(afterAmount);
                        this.save(tenantServiceRelDetails);
                    }
                }
            }
        }
    }

}
