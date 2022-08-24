package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.ISysStaticDataMarketService;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.dto.facilitator.OilPricePeovinceDto;
import com.youming.youche.market.dto.facilitator.OilPriceProvinceDto;
import com.youming.youche.market.provider.mapper.facilitator.OilPriceProvinceMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.system.api.ISysOperLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_PROVINCE;


/**
 * <p>
 * 全国油价表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-08
 */
@DubboService(version = "1.0.0")
@Service
public class OilPriceProvinceServiceImpl extends BaseServiceImpl<OilPriceProvinceMapper, OilPriceProvince> implements IOilPriceProvinceService {
    @Resource
    private ReadisUtil readisUtil;
    @Lazy
    @Autowired
    IServiceProductService serviceProductService;
    @Autowired
    ISysStaticDataMarketService sysStaticDataService;
    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public Long getOilPrice(long provinceId) {
        LambdaQueryWrapper<OilPriceProvince> lambdaQueryWrapper = new QueryWrapper<OilPriceProvince>().lambda();
        lambdaQueryWrapper.eq(OilPriceProvince::getProvinceId, provinceId);
        List<OilPriceProvince> list = this.list(lambdaQueryWrapper);
        if (list != null && list.size() == 1) {
            return list.get(0).getOilPrice();
        }
        return null;
    }

    @Override
    public Long calculationOilPrice(TenantProductRel tenantProductRel, Long oilPriceProvince, Boolean isShare, Boolean isBill) {
        Long fixedBalance = tenantProductRel.getFixedBalance();
        String floatBalance = tenantProductRel.getFloatBalance();
        String serviceCharge = tenantProductRel.getServiceCharge();

        Long fixedBalanceBill = tenantProductRel.getFixedBalanceBill();
        String floatBalanceBill = tenantProductRel.getFloatBalanceBill();
        String serviceChargeBill = tenantProductRel.getServiceChargeBill();
        if(fixedBalance==null){
            fixedBalance=0L;
        }
        if(fixedBalanceBill == null){
            fixedBalanceBill=0L;
        }

//        long originalPrice = oilPriceProvince;
        long oilPrice = 0L;
        //共享的才有 开票价和不开票价
        if (isShare) {
            if (isBill) {//需要开票
                oilPrice = this.calculationSharePrice(floatBalanceBill, fixedBalanceBill, oilPriceProvince, serviceChargeBill);
            } else {//不需要开票的
                oilPrice = this.calculationSharePrice(floatBalance, fixedBalance, oilPriceProvince, serviceCharge);
            }
        } else {//不共享按车队价
            if (isBill) {
                oilPrice = this.calculationPrice(floatBalanceBill, fixedBalanceBill, oilPriceProvince);
            } else {
                oilPrice = this.calculationPrice(floatBalance, fixedBalance, oilPriceProvince);
            }

        }
        return oilPrice;
    }

    /**
     * 计算费用
     *
     * @param floatBalance
     * @param fixedBalance
     * @param oilPrice
     * @return
     */
    public long calculationPrice(String floatBalance, Long fixedBalance, Long oilPrice) {
        long curOilPrice = 0;
        if (StringUtils.isNotBlank(floatBalance) && CommonUtil.isNumber(floatBalance)) {
            double floatBalanceDouble = (Double.valueOf(floatBalance)) / 100;
            double curOilPriceDouble = floatBalanceDouble * oilPrice;
            curOilPrice = (long) curOilPriceDouble;
        } else if (fixedBalance != null && fixedBalance > 0) {
            curOilPrice = fixedBalance;
        }
        return curOilPrice;
    }

    /**
     * 共享-计算费用
     *
     * @param floatBalance
     * @param fixedBalance
     * @param originalPrice
     * @return
     */
    public Long calculationSharePrice(String floatBalance, Long fixedBalance, Long originalPrice, String serviceCharge) {
        long oilPrice = 0;
        Double serviceChargeDouble = 0D;
        if (StringUtils.isNotBlank(serviceCharge)) {
            serviceChargeDouble = Double.valueOf(serviceCharge) / 100;
        }
        if (fixedBalance != null && fixedBalance > 0) {
            //平台若按固定价计算，显示：平台设置单价 *（1+平台设置手续费比例）
            double oilPriceDouble = CommonUtil.getDoubleFormatLongMoney(fixedBalance, 2);
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        } else if (StringUtils.isNotBlank(floatBalance)) {
            if(originalPrice == null){
                originalPrice=0L;
            }
            double oilPriceDouble = (Double.valueOf(floatBalance) / 100) * (CommonUtil.getDoubleFormatLongMoney(originalPrice, 2));
            // 平台若按浮动价计算，显示：平台设置折扣 * 油站归属省份的全国油价 *（1+平台设置手续费比例）；
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        }
        return oilPrice;
    }

    @Override
    public Page<OilPriceProvince> queryOilPrice(Long provinceId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OilPriceProvince> lambda = new QueryWrapper<OilPriceProvince>().lambda();
        Page<OilPriceProvince> oilPriceProvincePage = new Page<>(pageNum, pageSize);
        if (provinceId > 0) {
            lambda.eq(OilPriceProvince::getProvinceId, provinceId);
        }
        Page<OilPriceProvince> page = this.page(oilPriceProvincePage, lambda);
        List<OilPriceProvince> oilPage = page.getRecords();
        for (OilPriceProvince oilPriceProvince : oilPage) {
            String codeName = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, oilPriceProvince.getProvinceId() + "").getCodeName();
            oilPriceProvince.setProvinceName(codeName);
        }
        return page;
    }

    @Override
    public ResponseResult updateOilPriceProvince(OilPriceProvinceDto oilPrice, String accessToken) {
        if (oilPrice.getOilPrice() == null) {
            return ResponseResult.failure("价格不能为空");
        }
        if (oilPrice.getProvinceId() == null) {
            return ResponseResult.failure("省份不能为空");
        }
        LoginInfo user = loginUtils.get(accessToken);;
        Double oilPricei = oilPrice.getOilPrice() * 100;
        UpdateWrapper<OilPriceProvince> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("OIL_PRICE", oilPricei.longValue())
                .set("update_time", LocalDateTime.now())
                .eq("PROVINCE_ID", oilPrice.getProvinceId());
        this.update(updateWrapper);
        if(oilPrice.getProvinceId() != null){
            SysStaticData sysStaticData = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, oilPrice.getProvinceId().toString());
            if(sysStaticData != null){
                String ProvinceName = sysStaticData.getCodeName();
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.updateOilPrice, oilPrice.getId(), SysOperLogConst.OperType.Update, user.getName()+"修改了 省份：" +ProvinceName);
            }else{
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.updateOilPrice,  oilPrice.getId(), SysOperLogConst.OperType.Update, user.getName()+"修改了 省份：" + oilPrice.getProvinceId());
            }
        }
        return ResponseResult.success();
    }


    @Override
    public ResponseResult importOilPriceExcel(List<OilPricePeovinceDto> oilPricePeovinceDtos, String accessToken) {
        if (oilPricePeovinceDtos != null && oilPricePeovinceDtos.size() > 0) {
            LoginInfo user = loginUtils.get(accessToken);;
            for (OilPricePeovinceDto oilPricePeovinceDto : oilPricePeovinceDtos) {
                BigDecimal b = new BigDecimal(oilPricePeovinceDto.getOilPrice());
                double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double value = f1 * 100;
                Long price = value.longValue();
                OilPriceProvince oilPriceProvince = new OilPriceProvince();
                if (price != null) {
                    oilPriceProvince.setOilPrice(price);
                }
                if (oilPricePeovinceDto.getProvinceName() != null) {
                    oilPriceProvince.setProvinceName(oilPricePeovinceDto.getProvinceName());
                }

                Long codeId = sysStaticDataService.getSysSyaticDataByCodeNameAndCodeType(SYS_PROVINCE, oilPricePeovinceDto.getProvinceName()).getCodeId();
                if (codeId != null) {
                    oilPriceProvince.setProvinceId(codeId.intValue());
                }
                LambdaQueryWrapper<OilPriceProvince> lambda = new QueryWrapper<OilPriceProvince>().lambda();
                lambda.eq(OilPriceProvince::getProvinceName, oilPricePeovinceDto.getProvinceName())
                        .eq(OilPriceProvince::getProvinceId, oilPriceProvince.getProvinceId());
                OilPriceProvince province = this.getOne(lambda);
                if (province != null) {
                    UpdateWrapper<OilPriceProvince> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("OIL_PRICE", oilPriceProvince.getOilPrice())
                            .set("update_time", LocalDateTime.now())
                            .set("TENANT_ID", user.getTenantId())
                            .eq("ID", province.getId());
                    this.update(updateWrapper);
                    sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.updateOilPrice, province.getId(), SysOperLogConst.OperType.Update, user.getName()+"修改了 省份:" + province.getProvinceName());
                } else {
                    oilPriceProvince.setCreateTime(LocalDateTime.now());
                    oilPriceProvince.setTenantId(user.getTenantId());
                    this.save(oilPriceProvince);
                    SysStaticData sysStaticData = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, oilPriceProvince.getProvinceId() + "");
                    if(sysStaticData != null && user.getName() != null){
                        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.updateOilPrice, oilPriceProvince.getId(), SysOperLogConst.OperType.Add, user.getName()+"添加了 省份" + sysStaticData.getCodeName());
                    }else{
                        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.updateOilPrice, oilPriceProvince.getId(), SysOperLogConst.OperType.Add, "用户id:"+user.getId()+"创建了 省份id" +oilPriceProvince.getId());
                    }
                }
            }
            return ResponseResult.success("成功");
        }
        return ResponseResult.failure("失败");
    }

    @Override
    public OilPriceProvince getOilPriceProvince(Integer provinceId) {
        LambdaQueryWrapper<OilPriceProvince> lambdaQueryWrapper = new QueryWrapper<OilPriceProvince>().lambda();
        lambdaQueryWrapper.eq(OilPriceProvince::getProvinceId, provinceId);
        List<OilPriceProvince> list = this.list(lambdaQueryWrapper);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private InputStream streamTran(ByteArrayOutputStream in) {
        return new ByteArrayInputStream(in.toByteArray());
    }


}
