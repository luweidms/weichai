package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.api.order.IRateItemService;
import com.youming.youche.order.domain.order.BillAgreement;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.provider.mapper.order.BillAgreementMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class BillAgreementServiceImpl extends BaseServiceImpl<BillAgreementMapper, BillAgreement> implements IBillAgreementService {
    @Autowired
    private IBillSettingService billSettingService;
    @Autowired
    private IBillPlatformService billPlatformService;
    @Autowired
    private IRateItemService rateItemService;

    @Override
    public BillAgreement createOrSelectBillAgreement(Long billMethod, Long billInfoId) {
        if (null == billMethod || 0 == billMethod) {
            return null;
        }

        BillAgreement billAgreement = getBillAgreement(billMethod, billInfoId);
        if (null == billAgreement) {
            billAgreement = new BillAgreement();
            billAgreement.setBillInfoId(billInfoId);
            billAgreement.setBillMethod(billMethod);
            this.save(billAgreement);
        }

        return billAgreement;
    }

    @Override
    public Map<String, Object> calculationServiceFee(Long openUserId, Long cash, Long oil, Long etc, Long billAmount, Long tenantId, Map<String, Object> inParam) {
        if (openUserId == null || openUserId <= 0) {
            throw new BusinessException("请输入票据平台id");
        }
        if (cash == null || cash < 0) {
            throw new BusinessException("现金金额不合法");
        }
        if (oil == null || oil < 0) {
            throw new BusinessException("油费金额不合法");
        }
        if (etc == null || etc < 0) {
            throw new BusinessException("etc金额不合法");
        }
        if (billAmount == null || billAmount < 0) {
            throw new BusinessException("开票金额不合法");
        }
        if ((cash + oil + etc) != billAmount.longValue()) {
            throw new BusinessException("开票金额与现金 + 油 +etc 不等");
        }
        BillSetting billSetting = billSettingService.getBillSetting(tenantId);
        if (billSetting == null) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票信息");
        }
        if (billSetting.getRateId() == null || billSetting.getRateId() <= 0) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票费率设置");
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        BillPlatform bpf = billPlatformService.queryAllBillPlatformByUserId(openUserId);
        if (bpf == null) {
            throw new BusinessException("根据开票平台用户id：" + openUserId + " 未找到开票平台信息");
        }
        if (bpf.getServiceFeeFormula() == null) {
            throw new BusinessException("根据开票平台用户id： " + openUserId + " 未找到开票服务计算公式");
        }
        String tempFormula = bpf.getServiceFeeFormula();
        String formula = tempFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        String tempHaCostFormula = bpf.getHaCost();
        String haCostFormula = tempHaCostFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        //开票率
        Double billRate = rateItemService.getRateValue(billSetting.getRateId(), billAmount);
        if (billRate == null) {
            throw new BusinessException("根据车队费率id： " + billSetting.getRateId() + " 开票金额" + (double)billAmount/100 + " 未找到开票费率");
        }
        if (inParam != null) {
            if (inParam.get("rate") != null) {
                billRate =  (Double) inParam.get("rate");
            }
        }
        engine.put("d", cash);
        engine.put("o", oil);
        engine.put("e", etc);
        engine.put("r", billRate / 100);
        Double serviceFee = null;
        Double serviceCost=null;
        try {
            serviceFee = (Double) engine.eval(formula);
            serviceCost = (Double) engine.eval(haCostFormula);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Long billServiceFee =  Math.round(serviceFee != null ?serviceFee:0);//四舍五入
        Long billServiceCost = Math.round(serviceCost !=null? serviceCost:0);//四舍五入
        long lugeBillServiceFee = Math.round(serviceFee);//四舍五入
        long lugeBillServiceCost = Math.round(serviceCost);//四舍五入
        //56k服务费算法直接截取，路歌服务费四舍五入，比如123.678分，56K: 123分，路歌：124分
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("56KBillServiceFee", billServiceFee);
        result.put("56KBillServiceCost", billServiceCost);
        result.put("billServiceFee", billServiceFee);
        result.put("billServiceCost", billServiceCost);
        result.put("lugeBillServiceFee", lugeBillServiceFee);
        result.put("lugeBillServiceCost", lugeBillServiceCost);
        result.put("billRate", billRate);
        return result;
    }


    public BillAgreement getBillAgreement(Long billMethod, Long billInfoId) {
        LambdaQueryWrapper<BillAgreement> lambda=new QueryWrapper<BillAgreement>().lambda();
        lambda.eq(BillAgreement::getBillInfoId,billInfoId)
              .eq(BillAgreement::getBillInfoId,billInfoId);
        return this.getOne(lambda);
    }
}
