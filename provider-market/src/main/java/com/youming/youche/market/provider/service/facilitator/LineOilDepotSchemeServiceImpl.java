package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.ILineOilDepotSchemeService;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.domain.facilitator.LineOilDepotScheme;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.dto.etc.EtcMaintainDto;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDataDto;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDataInfoDto;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDto;
import com.youming.youche.market.dto.facilitator.LineOilQueryDto;
import com.youming.youche.market.provider.mapper.facilitator.LineOilDepotSchemeMapper;
import com.youming.youche.market.provider.transfer.ServiceProductDtoTransfer;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.record.common.GpsUtil;
import com.youming.youche.market.dto.facilitator.LineOilQueryInDto;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* <p>
    *  服务实现类
    * </p>
* @author liangyan
* @since 2022-03-25
*/
@DubboService(version = "1.0.0")
@Service
public class LineOilDepotSchemeServiceImpl extends BaseServiceImpl<LineOilDepotSchemeMapper, LineOilDepotScheme> implements ILineOilDepotSchemeService {

    @Resource
    private LoginUtils loginUtils;
    @Resource
    private LineOilDepotSchemeMapper lineOilDepotSchemeMapper;
    @Lazy
    @Resource
    private ServiceProductDtoTransfer serviceProductDtoTransfer;
    @Lazy
    @Autowired
    private IOilPriceProvinceService oilPriceProvinceService;
    @Resource
    private GpsUtil gpsUtil;
    @Override
    public IPage<LineOilDepotScheme> getLineOilDepotSchemeByLineId(String oilName,String accessToken,Integer pageNum,Integer pageSize) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        QueryWrapper<LineOilDepotScheme> lineOilDepotSchemeQueryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(oilName)){
            lineOilDepotSchemeQueryWrapper
                    .eq("tenant_id",baseUser.getTenantId())
                    .like("oil_name",oilName)
                    .orderByDesc("depend_distance");
        }else {
            lineOilDepotSchemeQueryWrapper
                    .eq("tenant_id",baseUser.getTenantId())
                    .orderByDesc("depend_distance");
        }

        List<LineOilDepotScheme> lineOilDepotSchemes = lineOilDepotSchemeMapper.selectList(lineOilDepotSchemeQueryWrapper);

        Page<LineOilDepotScheme> lineOilDepotScheme= null;
        lineOilDepotScheme = new Page<>();
        lineOilDepotScheme.setRecords(lineOilDepotSchemes);
        lineOilDepotScheme.setTotal(lineOilDepotSchemes.size());
        lineOilDepotScheme.setSize(pageSize);
        lineOilDepotScheme.setCurrent(pageNum);

        return lineOilDepotScheme;
    }

    @Override
    public Page<LineOilDepotSchemeDataDto> getLineOilDepotSchemeByLineId(String accessToken, LineOilQueryDto lineOilQueryDto,
                                                                         Integer pageNum,Integer pageSize) {
        Page<LineOilDepotSchemeDataDto> pageInfo = new Page<>(pageNum, pageSize);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(lineOilQueryDto.getType() != null && lineOilQueryDto.getType() ==1){
           return lineOilDepotSchemeMapper.getLineOilDepotSchemeByLineIdInfo(pageInfo,loginInfo, lineOilQueryDto, null);
        }
        if(lineOilQueryDto.getType() != null && lineOilQueryDto.getType() ==2){
            Page<LineOilDepotSchemeDataDto> schemeByLineIdInfo = lineOilDepotSchemeMapper.getLineOilDepotSchemeByLineIdInfo(pageInfo,loginInfo, lineOilQueryDto,
                    null);
            List<LineOilDepotSchemeDataDto> records = schemeByLineIdInfo.getRecords();
            for (LineOilDepotSchemeDataDto record : records) {

                Long oilPrice=null;
                if(record.getTenantId() == SysStaticDataEnum.PT_TENANT_ID ){
                    oilPrice =serviceProductDtoTransfer.calculationSharePrice(record.getFloatBalanceBill(), record.getFixedBalanceBill(), Long.valueOf(record.getProvincePrice()), record.getServiceChargeBill());
                }else{
                    TenantProductRel tenantProductRel=new TenantProductRel();
                    tenantProductRel.setFixedBalance(record.getFixedBalance());
                    tenantProductRel.setFloatBalance(record.getFloatBalance());
                    tenantProductRel.setServiceCharge(record.getServiceCharge());

                    tenantProductRel.setFixedBalanceBill(record.getFixedBalanceBill());
                    tenantProductRel.setFloatBalanceBill(record.getFloatBalanceBill());
                    tenantProductRel.setServiceChargeBill(record.getServiceChargeBill());
                    oilPrice = oilPriceProvinceService.calculationOilPrice(tenantProductRel, Long.valueOf(record.getProvincePrice()), record.getTenantId() == SysStaticDataEnum.PT_TENANT_ID ? true : false, lineOilQueryDto.getIsBill() == 1 ? true : false);
                }
                double distance = GpsUtil.getDistance(lineOilQueryDto.getSourceNand(), lineOilQueryDto.getSourceEand(),Double.parseDouble(record.getNand()), Double.parseDouble(record.getEand()));
                record.setDistance(CommonUtil.getDoubleFormat(distance / 1000,4).toString());
                record.setOilPrice(CommonUtil.getDoubleFormatLongMoney(oilPrice, 2));
                record.setOilId(record.getId());
                record.setOilName(record.getProductName());
                record.setOilPhone(record.getServiceCall());
            }
            schemeByLineIdInfo.setRecords(records);
            return  schemeByLineIdInfo;
        }
        if(lineOilQueryDto.getType() != null && lineOilQueryDto.getType() !=2 && lineOilQueryDto.getType() != 1){
//            LambdaQueryWrapper<LineOilDepotScheme> lambda= Wrappers.lambdaQuery();
//            lambda.eq(LineOilDepotScheme::getLineId, lineOilQueryDto.getLineId())
//                    .eq(LineOilDepotScheme::getTenantId,loginInfo.getTenantId())
//                    .orderByAsc(LineOilDepotScheme::getDependDistance);
//            List<LineOilDepotScheme> list1 = this.list(lambda);

            Page<LineOilDepotSchemeDataDto> schemeByLineIdInfo = null;
                    List<Map> list = null;//获取沿途油站
            try {
                list = gpsUtil.gainLineOilDepot(lineOilQueryDto.getSourceEand(),lineOilQueryDto.getSourceNand(),lineOilQueryDto.getDesEand(),lineOilQueryDto.getDesNand(),loginInfo.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Long> oilIdList  = new ArrayList<>();
            Map<Long,Object> oilPi = new HashMap<>();
            for(Map map : list){
                Long oilId= DataFormat.getLongKey(map, "oilId");
                if(oilId != null && oilId > 0){
                    oilIdList.add(oilId);
                    oilPi.put(oilId,map.get("oilDistance"));
                }
            }
            if(oilIdList.size() > 0){
                schemeByLineIdInfo= lineOilDepotSchemeMapper.getLineOilDepotSchemeByLineIdInfo(pageInfo,loginInfo, lineOilQueryDto, oilIdList);
                List<LineOilDepotSchemeDataDto> records = schemeByLineIdInfo.getRecords();
                if(records != null && records.size() > 0){
                    for (LineOilDepotSchemeDataDto record : records) {
                        String oilDistanceStr = String.valueOf(oilPi.get(record.getId()));
                        Double oilDistance = Double.parseDouble(oilDistanceStr) / 1000.0;
                        Long oilPrice = 0L;
                        if (record != null) {
                            if(StringUtils.isNotBlank(record.getFloatBalanceBill()) && CommonUtil.isNumber(record.getFloatBalanceBill())){
                                Double oilPriceDouble = (record.getOilPrice()/100.0 * (Double.parseDouble((record.getFloatBalanceBill())) / 100.0)) * 100;//省份油价
                                oilPrice = oilPriceDouble.longValue();
                            }
                        }
                        if(record.getFixedBalanceBill() != null && record.getFixedBalanceBill() > 0){
                            oilPrice = record.getFixedBalanceBill();
                        }
                        String userName = "";
                        if (record.getLinkman() != null) {
                            userName = record.getLinkman();
                        }
                        record.setOilId(record.getId());
                        record.setOilName(record.getProductName());
                        record.setOilPhone(record.getServiceCall());
                        record.setOilPrice(CommonUtil.getDoubleFormatLongMoney(oilPrice, 2));
                        record.setUserName(userName);
                        record.setAddress(record.getAddress());
                        record.setDistance(oilDistance.toString());
                    }
                }
            }
            return schemeByLineIdInfo;
        }
        return null;
    }

    @Override
    public IPage<LineOilDepotScheme> recommendLineOilDepotSchemeByLineId(String oilName,Long lineId, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        if(lineId == null){
            return null;
        }
        QueryWrapper<LineOilDepotScheme> lineOilDepotSchemeQueryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(oilName)){
            lineOilDepotSchemeQueryWrapper.eq("line_id",lineId)
                    .eq("tenant_id",baseUser.getTenantId())
                    .like("oil_name",oilName)
                    .le("depend_distance",50000)
                    .orderByDesc("depend_distance");
        }else {
            lineOilDepotSchemeQueryWrapper.eq("line_id",lineId)
                    .eq("tenant_id",baseUser.getTenantId())
                    .le("depend_distance",50000)
                    .orderByDesc("depend_distance");
        }

        List<LineOilDepotScheme> lineOilDepotSchemes = lineOilDepotSchemeMapper.selectList(lineOilDepotSchemeQueryWrapper);

        Page<LineOilDepotScheme> lineOilDepotScheme= null;
        lineOilDepotScheme = new Page<>();
        lineOilDepotScheme.setRecords(lineOilDepotSchemes);
        lineOilDepotScheme.setTotal(lineOilDepotSchemes.size());
        lineOilDepotScheme.setSize(pageSize);
        lineOilDepotScheme.setCurrent(pageNum);

        return lineOilDepotScheme;
    }

    @Override
    public List<LineOilDepotSchemeDto> getLineOilDepotSchemeByLineId(LineOilQueryInDto lineOilQueryIn,Long tenantId) {
        List<LineOilDepotSchemeDataInfoDto> schemeByLineId = lineOilDepotSchemeMapper.getLineOilDepotSchemeByLineId(lineOilQueryIn, tenantId);
        List<LineOilDepotSchemeDto> list=new ArrayList<>();
        for (LineOilDepotSchemeDataInfoDto lineOilDepotSchemeDataInfoDto : schemeByLineId) {
            LineOilDepotSchemeDto map = new LineOilDepotSchemeDto();
            String serviceName =lineOilDepotSchemeDataInfoDto.getServiceName();
            String provincePrice=lineOilDepotSchemeDataInfoDto.getProvincePrice();
            double distance = GpsUtil.getDistance(lineOilQueryIn.getSourceNand(), lineOilQueryIn.getSourceEand(),Double.parseDouble(lineOilDepotSchemeDataInfoDto.getNand()), Double.parseDouble(lineOilDepotSchemeDataInfoDto.getEand()));
            map.setDistance(CommonUtil.getDoubleFormat(distance / 1000,4));

            map.setOilId(lineOilDepotSchemeDataInfoDto.getProductId());
            map.setOilName(lineOilDepotSchemeDataInfoDto.getProductName());
            map.setOilPhone(lineOilDepotSchemeDataInfoDto.getServiceCall());
            Long oilPrice=null;
            if( lineOilDepotSchemeDataInfoDto.getTenantId() == SysStaticDataEnum.PT_TENANT_ID ) {
                oilPrice =calculationSharePrice(lineOilDepotSchemeDataInfoDto.getFloatBalanceBill(), lineOilDepotSchemeDataInfoDto.getFixedBalanceBill(), Long.valueOf(provincePrice), lineOilDepotSchemeDataInfoDto.getServiceChargeBill());
            }else {
                oilPrice = calculationOilPrice(lineOilDepotSchemeDataInfoDto, Long.valueOf(provincePrice), lineOilDepotSchemeDataInfoDto.getTenantId() == SysStaticDataEnum.PT_TENANT_ID ? true : false, lineOilQueryIn.getIsBill() == 1 ? true : false);
            }
            map.setOilPrice(CommonUtil.getDoubleFormatLongMoney(oilPrice, 2));
            map.setAddress(lineOilDepotSchemeDataInfoDto.getAddress());
            map.setServiceName(serviceName);
            map.setEand(lineOilDepotSchemeDataInfoDto.getEand());
            map.setNand(lineOilDepotSchemeDataInfoDto.getNand());
            list.add(map);
        }
        return list;
    }

    /**
     * 计算油价
     *
     * @param tenantProductRel
     * @param oilPriceProvince
     * @return
     */
    public Long calculationOilPrice(TenantProductRel tenantProductRel, Long oilPriceProvince, boolean isShare, boolean isBill) {
        Long fixedBalance = tenantProductRel.getFixedBalance();
        String floatBalance = tenantProductRel.getFloatBalance();
        String serviceCharge = tenantProductRel.getServiceCharge();

        Long fixedBalanceBill = tenantProductRel.getFixedBalanceBill();
        String floatBalanceBill = tenantProductRel.getFloatBalanceBill();
        String serviceChargeBill = tenantProductRel.getServiceChargeBill();
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
            }else {
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
    public long calculationSharePrice(String floatBalance, Long fixedBalance, Long originalPrice, String serviceCharge) {
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
            double oilPriceDouble = (Double.valueOf(floatBalance) / 100) * (CommonUtil.getDoubleFormatLongMoney(originalPrice, 2));
            // 平台若按浮动价计算，显示：平台设置折扣 * 油站归属省份的全国油价 *（1+平台设置手续费比例）；
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        }
        return oilPrice;
    }
}
