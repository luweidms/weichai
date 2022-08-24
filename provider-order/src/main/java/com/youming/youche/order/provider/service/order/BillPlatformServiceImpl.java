package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.provider.mapper.order.BillPlatformMapper;
import com.youming.youche.order.provider.utils.ReadisUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.order.constant.BaseConstant.CUSTOMER_SERVICE_PHONE;
import static com.youming.youche.order.constant.BaseConstant.OWN_CAR_OPEN_BILL;


/**
 * <p>
 * 票据平台表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class BillPlatformServiceImpl extends BaseServiceImpl<BillPlatformMapper, BillPlatform> implements IBillPlatformService {

    @Resource
    private ReadisUtil readisUtil;
    @Autowired
    private IBillSettingService billSettingService;

    @Resource
    private BillPlatformMapper billPlatformMapper;


    @Override
    public Integer getPayAcctType(Long tenantId, String vehicleAffiliation) {

        SysCfg sysCfg = readisUtil.getSysCfg(OWN_CAR_OPEN_BILL, "0");
        if (sysCfg != null) {
            String tenantIds = sysCfg.getCfgValue();
            String[] tenantIdsArr = null;
            if (StringUtils.isNotBlank(tenantIds)) {
                tenantIdsArr = tenantIds.trim().split("\\,");
                if (Arrays.asList(tenantIdsArr).contains(String.valueOf(tenantId))) {
                    return 1;
                }
            }
        }
        if (StringUtils.isNotBlank(vehicleAffiliation)) {
            BillPlatform bpf = queryBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
            if (bpf == null) {
                throw new BusinessException("根据开票平台：" + vehicleAffiliation + " 未找到开票平台信息");
            }
            return bpf.getPayAcctType();
        } else {
            // 平台开票的,需要调用基础数据的接口，获取对应的平台开票的渠道
            Long billMethod = getBillMethodByTenantId(tenantId);

            if (billMethod == null || billMethod == 0) {
                String customerPhone = readisUtil.getSysCfg(CUSTOMER_SERVICE_PHONE, "0").getCfgValue();
                throw new BusinessException("您的车队时暂时没有开通平台开票的功能，不能勾选平台开票。如需开通该功能，请联系平台客服：" + customerPhone);
            }
            BillPlatform billPlatform = queryBillPlatformByUserId(billMethod);
            if (billPlatform == null) {
                throw new BusinessException("票据平台升级中，不能勾选平台开票。");
            }
            int receivables = billPlatform.getPayAcctType();
            return receivables;
        }
    }

    @Override
    public Long getBillMethodByTenantId(Long tenantId) {
        BillSetting billSetting = billSettingService.getBillSettingByTenantId(tenantId);
        return null == billSetting ? null : billSetting.getBillMethod();
    }

    @Override
    public BillPlatform queryBillPlatformByUserId(Long userId) {
        if (userId <= 0) {
            return null;
        }
        LambdaQueryWrapper<BillPlatform> lambda = new QueryWrapper<BillPlatform>().lambda();
        lambda.eq(BillPlatform::getUserId, userId);
        List<BillPlatform> resultList = this.list(lambda);
        if (resultList != null && resultList.size() > 0)
            return resultList.get(0);
        else
            return null;
    }

    @Override
    public Boolean judgeBillPlatform(Long openUserId, String billPlatform) {
        if (openUserId == null) {
            throw new BusinessException("请输入票据平台用户id");
        }
        if (StringUtils.isBlank(billPlatform)) {
            throw new BusinessException("请输入票据平台类型");
        }
        boolean flag = false;
        String sysPre = getPrefixByUserId(openUserId);
        String codeName = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, billPlatform).getCodeName();
        if (StringUtils.isNotBlank(sysPre) && StringUtils.isNotBlank(codeName)) {
            if (sysPre.equals(codeName)) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    public  String getPrefixByUserId(Long userId) {
        BillPlatform billPlatform = getBillPlatformByUserId(userId);
        return null == billPlatform ? null : billPlatform.getSysPre();
    }

    public  BillPlatform getBillPlatformByUserId(Long userId) {
        List<BillPlatform> list = this.list();
        if (CollectionUtils.isNotEmpty(list)) {
            for (BillPlatform form : list) {
                if (form.getUserId().equals(userId)) {
                    return form;
                }
            }
        }
        return null;
    }

    public BillPlatform queryAllBillPlatformByUserId(long userId) {

        if (userId <= 0) {
            return null;
        }

        Map setMap = new HashMap();
        QueryWrapper<BillPlatform> billPlatformQueryWrapper = new QueryWrapper<>();
        billPlatformQueryWrapper.eq("user_id", userId);
        List<BillPlatform> billPlatforms = billPlatformMapper.selectList(billPlatformQueryWrapper);
        if (billPlatforms != null && billPlatforms.size() > 0) {
            return billPlatforms.get(0);
        }
        return null;
    }
}
