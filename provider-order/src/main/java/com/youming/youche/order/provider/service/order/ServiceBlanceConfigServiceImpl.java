package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.order.api.order.IServiceBlanceConfigService;
import com.youming.youche.order.domain.order.ServiceBlanceConfig;
import com.youming.youche.order.provider.mapper.order.ServiceBlanceConfigMapper;
import com.youming.youche.order.provider.utils.ReadisUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.youming.youche.order.constant.BaseConstant.DEFAULT_WARN_BLANCE;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceBlanceConfigServiceImpl extends BaseServiceImpl<ServiceBlanceConfigMapper, ServiceBlanceConfig> implements IServiceBlanceConfigService {
    @Resource
    private ReadisUtil readisUtil;
    @DubboReference(version = "1.0.0")
    private IServiceInfoService serviceInfoService;
    @DubboReference(version = "1.0.0")
    private IServiceProductService serviceProductService;

    @Override
    public void doUpdServiceBlanceConfig(Long anentId, Long serviceId, Long productId, Long amount) {
        ServiceBlanceConfig serviceBlanceConfigService = this.getServiceBlanceConfig(anentId, serviceId, -1L);
        if (null == serviceBlanceConfigService) {
            return;
        }
        Long serviceBlanceConfigServiceAmount = serviceBlanceConfigService.getReserveBalance();
        if (null == serviceBlanceConfigServiceAmount) {
            serviceBlanceConfigServiceAmount = 0l;
        }
        serviceBlanceConfigService.setReserveBalance(serviceBlanceConfigServiceAmount - amount);

        this.update(serviceBlanceConfigService);
        ServiceBlanceConfig serviceBlanceConfigProduct = this.getServiceBlanceConfig(anentId, -1L, productId);
        if (null == serviceBlanceConfigProduct) {
            return;
        }
        Long serviceBlanceConfigProductAmount = serviceBlanceConfigProduct.getReserveBalance();
        if (null == serviceBlanceConfigProductAmount) {
            serviceBlanceConfigProductAmount = 0l;
        }
        serviceBlanceConfigProduct.setReserveBalance(serviceBlanceConfigProductAmount - amount);
        this.update(serviceBlanceConfigProduct);
        String defaultWarnBlance = readisUtil.getSysCfg(DEFAULT_WARN_BLANCE, "0").getCfgValue();
        Long warnBalance = Long.parseLong(defaultWarnBlance);
        if (null != serviceBlanceConfigService.getWarnBalance()) {
            warnBalance = serviceBlanceConfigService.getWarnBalance();
        }
        if (serviceBlanceConfigService.getReserveBalance() <= warnBalance && StringUtils.isNotBlank(serviceBlanceConfigService.getWarnTels())) {
            String[] tels = serviceBlanceConfigService.getWarnTels().split(",");
            ServiceInfo service = serviceInfoService.getServiceInfoByServiceUserId(serviceId);
            String name = "";
            if (null != service) {
                name = service.getServiceName();
            }
            for (int i = 0; i < tels.length; i++) {
                sendMsg(tels[i], name, serviceBlanceConfigService.getReserveBalance());
            }
        }
        if (null != serviceBlanceConfigProduct.getWarnBalance()) {
            warnBalance = serviceBlanceConfigProduct.getWarnBalance();
        }
        if (serviceBlanceConfigProduct.getReserveBalance() <= warnBalance && StringUtils.isNotBlank(serviceBlanceConfigProduct.getWarnTels())) {
            String[] tels = serviceBlanceConfigProduct.getWarnTels().split(",");
            ServiceProduct product = serviceProductService.getById(productId);
            String name = "";
            if (null != product) {
                name = product.getProductName();
            }
            for (int i = 0; i < tels.length; i++) {
                sendMsg(tels[i], name, serviceBlanceConfigProduct.getReserveBalance());
            }
        }
    }

    //todo 发送短信
    private void sendMsg(String billId, String name, long amount) {
//        Map<String, Object> smsMap = new HashMap<String, Object>();
//        Map<String, String> paraMap = new HashMap<String, String>();
//
//        smsMap.put("template_id", 5000000003l);
//        smsMap.put("bill_id",billId);
//        smsMap.put("sms_type",SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
//        smsMap.put("OBJ_TYPE", SysStaticDataEnum.OBJ_TYPE.NOTIFY);
//
//        paraMap.put("name",name);
//        paraMap.put("amount",CommonUtil.divide(amount));
//        smsMap.put("paraMap", paraMap);
//        SysSmsSendSV sysSmsSendSV = (SysSmsSendSV) SysContexts.getBean("smsSendSV");
//        sysSmsSendSV.sendSms(smsMap,0);
    }

    @Override
    public ServiceBlanceConfig getServiceBlanceConfig(Long anentId, Long serviceId, Long productId) {
        LambdaQueryWrapper<ServiceBlanceConfig> lambda = Wrappers.lambdaQuery();
        lambda.eq(ServiceBlanceConfig::getAgentServiceUserId, anentId);
        if (serviceId > 0) {
            lambda.eq(ServiceBlanceConfig::getServiceUserId, serviceId);
        }
        if (productId > 0) {
            lambda.eq(ServiceBlanceConfig::getProductId, productId);
        }
        lambda.last("limit 1");
        return this.getOne(lambda);
    }
}
