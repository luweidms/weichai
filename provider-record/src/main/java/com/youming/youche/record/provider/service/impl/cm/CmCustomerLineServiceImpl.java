package com.youming.youche.record.provider.service.impl.cm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.cm.ICmCustomerLineSubwayService;
import com.youming.youche.record.api.cm.ICmCustomerLineSubwayVerService;
import com.youming.youche.record.api.cm.ICmCustomerLineVerService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.GpsUtil;
import com.youming.youche.record.common.Monitor;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import com.youming.youche.record.domain.cm.CmCustomerLineSubwayVer;
import com.youming.youche.record.domain.cm.CmCustomerLineVer;
import com.youming.youche.record.dto.cm.CmCustomerLineDto;
import com.youming.youche.record.dto.cm.CmCustomerLineOrderExtend;
import com.youming.youche.record.dto.cm.CmCustomerLineOutDto;
import com.youming.youche.record.dto.cm.QueryLineByTenantForWXDto;
import com.youming.youche.record.provider.mapper.cm.CmCustomerInfoMapper;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineMapper;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineSubwayMapper;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineSubwayVerMapper;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineVerMapper;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.utils.ExcelUtils;
import com.youming.youche.record.vo.cm.CmCustomerLineVo;
import com.youming.youche.record.vo.cm.QueryLineByTenantForWXVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * ????????????????????? ???????????????
 * </p>
 *
 * @author ?????????
 * @since 2022-01-15
 */
@DubboService(version = "1.0.0")
@Service
public class CmCustomerLineServiceImpl extends BaseServiceImpl<CmCustomerLineMapper, CmCustomerLine> implements ICmCustomerLineService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LoginUtils loginUtils;

    @Resource
    CmCustomerLineMapper customerLineMapper;

    @Resource
    CmCustomerLineVerMapper customerLineVerMapper;

    @Resource
    CmCustomerLineSubwayMapper lineSubwayMapper;

    @Resource
    CmCustomerLineSubwayVerMapper lineSubwayVerMapper;

    @Resource
    CmCustomerInfoMapper customerInfoMapper;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;

    @Resource
    ICmCustomerLineVerService cmCustomerLineVerService;

    @Resource
    ICmCustomerLineSubwayVerService cmCustomerLineSubwayVerService;

    @Resource
    ICmCustomerLineSubwayService cmCustomerLineSubwayService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @Resource
    UserDataInfoRecordMapper userDataInfoRecordMapper;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

//    @Resource
//    @Lazy
//    ICmCustomerInfoService iCmCustomerInfoService;

    /**
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.domain.cm.CmCustomerLine>
     * @author ?????????
     * @Description //TODO ??????????????????
     * @date 14:14 2022/1/21 0021
     * @Param [linePage, customerLine]
     */
    @Override
    public Page<CmCustomerLineDto> findAllLine(Page<CmCustomerLineDto> linePage, CmCustomerLineVo customerLine, String accessToken) throws Exception {
        LoginInfo user = loginUtils.get(accessToken);
        ;
        List busiIds = new ArrayList();
        if (!StringUtils.isEmpty(customerLine.getSourceProvinceAndCity())) {
            String sourceProvinceAndCity = customerLine.getSourceProvinceAndCity();
            int first = sourceProvinceAndCity.indexOf(",");
            int trim = sourceProvinceAndCity.indexOf(" ");
            int last = sourceProvinceAndCity.lastIndexOf(",");
            String province = sourceProvinceAndCity.substring(first + 1, trim);
            String city = sourceProvinceAndCity.substring(last + 1);
            customerLine.setSourceProvince(Integer.parseInt(province));
            customerLine.setSourceCity(Integer.parseInt(city));
        }
        if (!StringUtils.isEmpty(customerLine.getDesProvinceAndCity())) {
            String desProvinceAndCity = customerLine.getDesProvinceAndCity();
            int first = desProvinceAndCity.indexOf(",");
            int trim = desProvinceAndCity.indexOf(" ");
            int last = desProvinceAndCity.lastIndexOf(",");
            String province = desProvinceAndCity.substring(first + 1, trim);
            String city = desProvinceAndCity.substring(last + 1);
            customerLine.setDesProvince(Integer.parseInt(province));
            customerLine.setDesCity(Integer.parseInt(city));
        }
        String validDays = customerLine.getValidDays();
        if (!StringUtils.isEmpty(validDays) && "1".equals(validDays)) {
            validDays = getSysStaticDataId("CUCUSTOMER_LINE_VALIDDAYS", "ALL").getCodeName();
        }
        customerLine.setValidDays(validDays);
        Page<CmCustomerLineDto> cmCustomerLines = baseMapper.selectAllLine(linePage, customerLine, user);
        List<CmCustomerLineDto> records = cmCustomerLines.getRecords();
        if (records != null && records.size() > 0) {
            Map<Long, Boolean> hasPermissionMap = null;
            records.forEach(temp -> {
                if (temp.getOrgId() != null) {
                    temp.setSaleDaparmentName(sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), temp.getOrgId()));
                }
                if (temp.getSourceProvince() != null) {
                    String sourceProvinceAndCity = "";
                    SysStaticData sysStaticData = getSysStaticDataId(EnumConsts.SysStaticData.SYS_PROVINCE, String.valueOf(temp.getSourceProvince()));
                    if (sysStaticData.getCodeName() != null) {
                        sourceProvinceAndCity = sysStaticData.getCodeName();
                    }
                    if (temp.getSourceCity() != null) {
                        sysStaticData = getSysStaticDataId(EnumConsts.SysStaticData.SYS_CITY, String.valueOf(temp.getSourceCity()));
                        if (sysStaticData.getCodeName() != null && StrUtil.isNotEmpty(sourceProvinceAndCity)) {
                            sourceProvinceAndCity = sourceProvinceAndCity + "/" + sysStaticData.getCodeName();
                        }
                    }
                    temp.setSourceProvinceNameAndCityName(sourceProvinceAndCity);
                }
                if (temp.getDesProvince() != null) {
                    String descProvinceAndCity = "";
                    SysStaticData sysStaticData = getSysStaticDataId(EnumConsts.SysStaticData.SYS_PROVINCE, String.valueOf(temp.getDesProvince()));
                    if (sysStaticData.getCodeName() != null) {
                        descProvinceAndCity = sysStaticData.getCodeName();
                    }
                    if (temp.getSourceCity() != null) {
                        sysStaticData = getSysStaticDataId(EnumConsts.SysStaticData.SYS_CITY, String.valueOf(temp.getDesCity()));
                        if (sysStaticData.getCodeName() != null && StrUtil.isNotEmpty(descProvinceAndCity)) {
                            descProvinceAndCity = descProvinceAndCity + "/" + sysStaticData.getCodeName();
                        }
                    }
                    temp.setDesProvinceNameAndCityName(descProvinceAndCity);
                }
                Integer payWay = temp.getPayWay();
                Integer reciveTime = temp.getReciveTime();
                Integer reciveTimeDay = temp.getReciveTimeDay();
                Integer recondTime = temp.getRecondTime();
                Integer recondTimeDay = temp.getRecondTimeDay();
                Integer invoiceTime = temp.getInvoiceTime();
                Integer invoiceTimeDay = temp.getInvoiceTimeDay();
                Integer collectionTime = temp.getCollectionTime();
                Integer collectionTimeDay = temp.getCollectionTimeDay();
                Integer vehicleStatus = temp.getVehicleStatus();//??????
                String vehicleLength = temp.getVehicleLength();//??????PRICE_ENUM
                Integer priceEnum = temp.getPriceEnum();//????????????
                String vehicleStatusName = getSysStaticDataId("VEHICLE_STATUS@ORD", vehicleStatus + "").getCodeName();
                String vehicleLengthName = getSysStaticDataId("VEHICLE_LENGTH", vehicleLength + "").getCodeName();
                if(StrUtil.isEmpty(vehicleLengthName)){
                    vehicleLengthName = vehicleLength;
                }
                temp.setVehicleType(vehicleStatusName + "/" + vehicleLengthName);
                String priceEnumCodeName = getSysStaticDataId("PRICE_ENUM", priceEnum + "").getCodeName();
                temp.setPriceEnumName(priceEnumCodeName);
                if (!StringUtils.isEmpty(String.valueOf(payWay)) && payWay > 2) {
                    temp.setReciveTimeMonth(reciveTime);
                    temp.setRecondTimeMonth(recondTime);
                    temp.setInvoiceTimeMonth(invoiceTime);
                    temp.setCollectionTimeMonth(collectionTime);
                }
                busiIds.add(temp.getId());
            });
            if (!busiIds.isEmpty()) {
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_CUST_LINE, busiIds, accessToken);
                for (CmCustomerLineDto cl : records
                ) {
                    Boolean flg = hasPermissionMap.get(cl.getId());
                    cl.setIsAuth(flg ? 1 : 0);
                }
            }
        }
        return cmCustomerLines;
    }

    @Override
    public List<CmCustomerLineDto> findLineById(Long lineId, Integer isEdit, String accessToken) throws Exception {
        LoginInfo user = loginUtils.get(accessToken);
        List<CmCustomerLineDto> rtnList = new ArrayList<>();
        CmCustomerLineDto dtoVer = new CmCustomerLineDto();
        if (lineId > 0) {
            //??????????????????
            QueryWrapper<CmCustomerLine> customerLineQueryWrapper = new QueryWrapper<>();
            customerLineQueryWrapper.eq("id", lineId).eq("tenant_id", user.getTenantId());
            CmCustomerLine line = customerLineMapper.selectOne(customerLineQueryWrapper);
            CmCustomerLineDto dto = new CmCustomerLineDto();
            BeanUtil.copyProperties(line, dto);
            if (StrUtil.isEmpty(dto.getCompanyName()) && StrUtil.isNotEmpty(dto.getCustomerName())) {
                dto.setCompanyName(dto.getCustomerName());
            }
            //????????????
            Integer vehicleStatus = dto.getVehicleStatus();
            String vehicleLength = dto.getVehicleLength();
            String vehicleStatusName = getSysStaticDataId("VEHICLE_STATUS@ORD", vehicleStatus + "").getCodeName();
            String vehicleLengthName = getSysStaticDataId("VEHICLE_LENGTH", vehicleLength + "").getCodeName();
            dto.setVehicleType(vehicleStatusName + "/" + vehicleLengthName);
            //????????????
            Long reciveProvinceId = dto.getReciveProvinceId();
            Long reciveCityId = dto.getReciveCityId();
            if (dto.getSourceProvince() != null) {
                String sourceProvinceName = getSysStaticDataId("SYS_PROVINCE", dto.getSourceProvince() + "").getCodeName();
                dto.setSourceProvinceName(sourceProvinceName);
            }
            if (dto.getSourceCity() != null) {
                String sourceRegionName = getSysStaticDataId("SYS_CITY", dto.getSourceCity() + "").getCodeName();
                dto.setSourceRegionName(sourceRegionName);
            }
            if (dto.getSourceCounty() != null) {
                String sourceCountyName = getSysStaticDataId("SYS_DISTRICT", dto.getSourceCounty() + "").getCodeName();
                dto.setSourceCountyName(sourceCountyName);
            }
            if (dto.getDesProvince() != null) {
                String desProvinceName = getSysStaticDataId("SYS_PROVINCE", dto.getDesProvince() + "").getCodeName();
                dto.setDesProvinceName(desProvinceName);
            }
            if (dto.getDesCity() != null) {
                String desRegionName = getSysStaticDataId("SYS_CITY", dto.getDesCity() + "").getCodeName();
                dto.setDesRegionName(desRegionName);
            }
            if (dto.getDesCounty() != null) {
                String desCountyName = getSysStaticDataId("SYS_DISTRICT", dto.getDesCounty() + "").getCodeName();
                dto.setDesCountyName(desCountyName);
            }
            String reciveProvince = getSysStaticDataId("SYS_PROVINCE", reciveProvinceId + "").getCodeName();
            String reciveCity = getSysStaticDataId("SYS_CITY", reciveCityId + "").getCodeName();
            dto.setReciveProvinceAndCity(reciveProvince + "" + reciveCity);
            Integer goodsType = dto.getGoodsType();
            if (goodsType != null && goodsType == 1) {
                dto.setGoodsTypeName("??????");
            } else {
                dto.setGoodsTypeName("?????????");
            }
            //????????????
            String orgName = sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), dto.getOrgId());
            dto.setSaleDaparmentName(orgName);
            //???????????????
            QueryWrapper<CmCustomerLineSubway> subwayQueryWrapper = new QueryWrapper<>();
            subwayQueryWrapper.eq("line_id", lineId);
            List<CmCustomerLineSubway> subwayDto = lineSubwayMapper.selectList(subwayQueryWrapper);
            if (subwayDto != null && subwayDto.size() > 0) {
                for (CmCustomerLineSubway subay : subwayDto
                ) {
                    if (subay.getDesProvince() != null) {
                        subay.setDesProvinceName(getSysStaticDataId("SYS_PROVINCE", subay.getDesProvince() + "").getCodeName());
                    }
                    if (subay.getDesCity() != null) {
                        subay.setDesRegionName(getSysStaticDataId("SYS_CITY", subay.getDesCity() + "").getCodeName());
                    }
                    if (subay.getDesCounty() != null) {
                        subay.setDesCountyName(getSysStaticDataId("SYS_DISTRICT", subay.getDesCounty() + "").getCodeName());
                    }
                }
            }

            dto.setLineSubwayList(subwayDto);
            if (dto.getDriverUserId() != null && dto.getDriverUserId() > 0) {
                Map userDataInfo = userDataInfoRecordMapper.getUserQuery(dto.getDriverUserId(), user.getTenantId());
                if (userDataInfo != null) {
                    String driver = userDataInfo.get("linkMan") + "(" + userDataInfo.get("mobilePhone") + ")";
                    dto.setDriver(driver);
                }
            }
            CmCustomerInfo cust = new CmCustomerInfo();
            if (line != null) {
                cust = customerInfoMapper.selectById(line.getCustomerId());
            }
            //??????????????????
            if (cust != null) {
                dto.setCustomerName(cust.getCompanyName());
            }
            //????????????
            Integer payWay = dto.getPayWay();
            Integer reciveTime = dto.getReciveTime();
            Integer reciveTimeDay = dto.getReciveTimeDay();
            Integer recondTime = dto.getRecondTime();
            Integer recondTimeDay = dto.getRecondTimeDay();
            Integer invoiceTime = dto.getInvoiceTime();
            Integer invoiceTimeDay = dto.getInvoiceTimeDay();
            Integer collectionTime = dto.getCollectionTime();
            Integer collectionTimeDay = dto.getCollectionTimeDay();
            if (payWay != null && payWay > 2) {
                dto.setReciveTimeMonth(reciveTime);
                dto.setRecondTimeMonth(recondTime);
                dto.setInvoiceTimeMonth(invoiceTime);
                dto.setCollectionTimeMonth(collectionTime);
            }
            rtnList.add(dto);
            Integer isAuth = dto.getIsAuth();
            if (isEdit == 1 || isEdit == 3 || isEdit == 2) {
                QueryWrapper<CmCustomerLineVer> customerLineVerQueryWrapper = new QueryWrapper<>();
                customerLineVerQueryWrapper.eq("line_id", dto.getId())
                        .orderByDesc("id").last("limit 1");
                CmCustomerLineVer ver = customerLineVerMapper.selectOne(customerLineVerQueryWrapper);

                if (null != ver) {
                    BeanUtil.copyProperties(ver, dtoVer);
                    //????????????
                    vehicleStatus = dtoVer.getVehicleStatus();
                    vehicleLength = dtoVer.getVehicleLength();
                    vehicleStatusName = getSysStaticDataId("VEHICLE_STATUS@ORD", vehicleStatus + "").getCodeName();
                    vehicleLengthName = getSysStaticDataId("VEHICLE_LENGTH", vehicleLength + "").getCodeName();
                    dtoVer.setVehicleType(vehicleStatusName + "/" + vehicleLengthName);
                    //????????????
                    reciveProvinceId = dtoVer.getReciveProvinceId();
                    reciveCityId = dtoVer.getReciveCityId();
                    reciveProvince = getSysStaticDataId("SYS_PROVINCE", reciveProvinceId + "").getCodeName();
                    reciveCity = getSysStaticDataId("SYS_CITY", reciveCityId + "").getCodeName();
                    if (dtoVer.getSourceProvince() != null) {
                        String sourceProvinceVerName = getSysStaticDataId("SYS_PROVINCE", dtoVer.getSourceProvince() + "").getCodeName();
                        dtoVer.setSourceProvinceName(sourceProvinceVerName);
                    }
                    if (dtoVer.getSourceCity() != null) {
                        String sourceRegionVerName = getSysStaticDataId("SYS_CITY", dtoVer.getSourceCity() + "").getCodeName();
                        dtoVer.setSourceRegionName(sourceRegionVerName);
                    }
                    if (dtoVer.getSourceCounty() != null) {
                        String sourceCountyVerName = getSysStaticDataId("SYS_DISTRICT", dtoVer.getSourceCounty() + "").getCodeName();
                        dtoVer.setSourceCountyName(sourceCountyVerName);
                    }
                    if (dtoVer.getDesProvince() != null) {
                        String desProvinceName = getSysStaticDataId("SYS_PROVINCE", dtoVer.getDesProvince() + "").getCodeName();
                        dtoVer.setDesProvinceName(desProvinceName);
                    }
                    if (dtoVer.getDesCity() != null) {
                        String desRegionName = getSysStaticDataId("SYS_CITY", dtoVer.getDesCity() + "").getCodeName();
                        dtoVer.setDesRegionName(desRegionName);
                    }
                    if (dtoVer.getDesCounty() != null) {
                        String desCountyName = getSysStaticDataId("SYS_DISTRICT", dtoVer.getDesCounty() + "").getCodeName();
                        dtoVer.setDesCountyName(desCountyName);
                    }
                }
                dtoVer.setReciveProvinceAndCity(reciveProvince + "" + reciveCity);
                goodsType = dtoVer.getGoodsType();
                if (goodsType != null && goodsType == 1) {
                    dtoVer.setGoodsTypeName("??????");
                } else {
                    dtoVer.setGoodsTypeName("?????????");
                }
                //????????????
                orgName = sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), dtoVer.getOrgId());
                dtoVer.setSaleDaparmentName(orgName);
                //???????????????
                QueryWrapper<CmCustomerLineSubwayVer> subwayVerQueryWrapper = new QueryWrapper<>();
                subwayVerQueryWrapper.eq("line_id", lineId);
                subwayVerQueryWrapper.eq("check_state", 0);
                List<CmCustomerLineSubwayVer> subwayVerDtoV = lineSubwayVerMapper.selectList(subwayVerQueryWrapper);
                if (subwayVerDtoV != null) {
                    if (subwayVerDtoV.size() > 0) {
                        for (CmCustomerLineSubwayVer subay : subwayVerDtoV
                        ) {
                            if (subay.getDesProvince() != null) {
                                subay.setDesProvinceName(getSysStaticDataId("SYS_PROVINCE", subay.getDesProvince() + "").getCodeName());
                            }
                            if (subay.getDesCity() != null) {
                                subay.setDesRegionName(getSysStaticDataId("SYS_CITY", subay.getDesCity() + "").getCodeName());
                            }
                            if (subay.getDesCounty() != null) {
                                subay.setDesCountyName(getSysStaticDataId("SYS_DISTRICT", subay.getDesCounty() + "").getCodeName());
                            }
                        }
                    }
                    dtoVer.setLineSubwayList(subwayVerDtoV);
                }
                if (dtoVer.getDriverUserId() != null && dtoVer.getDriverUserId() > 0) {
                    Map userDataInfo = userDataInfoRecordMapper.getUserQuery(dtoVer.getDriverUserId(), user.getTenantId());
                    if (userDataInfo != null) {
                        String driver = userDataInfo.get("linkMan") + "(" + userDataInfo.get("mobilePhone") + ")";
                        dtoVer.setDriver(driver);
                    }
                }
                //????????????
                payWay = dtoVer.getPayWay();
                reciveTime = dtoVer.getReciveTime();
                reciveTimeDay = dtoVer.getReciveTimeDay();
                recondTime = dtoVer.getRecondTime();
                recondTimeDay = dtoVer.getRecondTimeDay();
                invoiceTime = dtoVer.getInvoiceTime();
                invoiceTimeDay = dtoVer.getInvoiceTimeDay();
                collectionTime = dtoVer.getCollectionTime();
                collectionTimeDay = dtoVer.getCollectionTimeDay();
                if (payWay != null && payWay > 2) {
                    dtoVer.setReciveTimeMonth(reciveTime);
                    dtoVer.setRecondTimeMonth(recondTime);
                    dtoVer.setInvoiceTimeMonth(invoiceTime);
                    dtoVer.setCollectionTimeMonth(collectionTime);
                }
            } else {
                BeanUtil.copyProperties(dto, dtoVer);
            }
        } else {
            throw new BusinessException("??????ID?????????");
        }
        if (dtoVer != null) {
            //???????????????????????????
            //??????????????????
            dtoVer.setHasAllData(0);
            //????????????
            dtoVer.setHasCostPermission(0);
            //????????????
            dtoVer.setHasIncomePermission(0);
            if (sysRoleService.hasAllData(user)) {
                dtoVer.setHasAllData(1);
            }
            if (sysRoleService.hasCostPermission(user)) {
                dtoVer.setHasCostPermission(1);
            }
            if (sysRoleService.hasIncomePermission(user)) {
                dtoVer.setHasIncomePermission(1);
            }
            rtnList.add(dtoVer);
        }
        return rtnList;
    }

//
//    public static void transFeeFenToYuan(List<String> keyList,Map map){
//        BigDecimal dv = new BigDecimal(100);
//        try {
//            for (String string : keyList) {
//                String fenValue = DateFormat.getStringKey(map,string);
//                if(StringUtils.isNotBlank(fenValue))
//                    map.put(string,new BigDecimal(fenValue).divide(dv).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//    }

    /**
     * @Description ??????/????????????????????????
     * @date 17:23 2022/2/26 0026
     */
    @Transactional
    @Override
    public String saveOrUpdateLine(CmCustomerLineVo customerLine, String accessToken) throws BusinessException {
        LoginInfo user = loginUtils.get(accessToken);
        //????????????
        Long id = customerLine.getId();
        Long customerId = customerLine.getCustomerId();
        String lineName = customerLine.getLineName();//???????????????
        String lineTel = customerLine.getLineTel();//????????????
        LocalDate effectBegin = customerLine.getEffectBegin();//???????????????
        LocalDate effectEnd = customerLine.getEffectEnd();//???????????????(??????)
        Long orgId = customerLine.getOrgId();//??????id
        String contractUrl = customerLine.getContractUrl();//????????????
        String goodsName = customerLine.getGoodsName();//????????????
        Integer goodsType = customerLine.getGoodsType();//????????????
        Float goodsWeight = customerLine.getGoodsWeight();//????????????
        Float goodsVolume = customerLine.getGoodsVolume();//????????????
        Integer vehicleStatus = customerLine.getVehicleStatus();
        String vehicleLength = customerLine.getVehicleLength();
        String receiveName = customerLine.getReceiveName();//?????????(?????????)
        String receivePhone = customerLine.getReceivePhone();//????????????(?????????)
        String taiwanDate = customerLine.getTaiwanDate();//????????????
        String sourceAddress = customerLine.getSourceAddress();//?????????????????????
        Integer sourceProvince = customerLine.getSourceProvince();
        Integer sourceCity = customerLine.getSourceCity();
        Integer sourceCounty = customerLine.getSourceCounty();
        String navigatSourceLocation = customerLine.getNavigatSourceLocation();//????????????(????????????)
        String desAddress = customerLine.getDesAddress();//???????????????
        Integer desProvince = customerLine.getDesProvince();
        Integer desCity = customerLine.getDesCity();
        Integer desCounty = customerLine.getDesCounty();
        String navigatDesLocation = customerLine.getNavigatDesLocation();//????????????(????????????)
        Float arriveTime = customerLine.getArriveTime();//????????????
        Float cmMileageNumber = customerLine.getCmMileageNumber();//???????????????
        Float mileageNumber = customerLine.getMileageNumber();//??????????????????
        String lineCodeName = customerLine.getLineCodeName();//????????????
        Long guidePrice = customerLine.getGuidePrice();//????????????
        Long guideMerchant = customerLine.getGuideMerchant();//????????????
        Integer backhaulState = customerLine.getBackhaulState();//????????????(????????????)
        Long backhaulGuideFee = customerLine.getBackhaulGuideFee();//???????????????
        String backhaulGuideName = customerLine.getBackhaulGuideName();//???????????????
        String backhaulGuidePhone = customerLine.getBackhaulGuidePhone();//?????????????????????
        Long pontagePer = customerLine.getPontagePer();//?????????
        Long emptyOilCostPer = customerLine.getEmptyOilCostPer();//????????????
        Long oilCostPer = customerLine.getOilCostPer();//????????????
        BigDecimal priceUnit = customerLine.getPriceUnit();//????????????
        Integer priceEnum = customerLine.getPriceEnum();//??????????????????
        Long estimateIncome = customerLine.getEstimateIncome();//????????????
        String reciveAddress = customerLine.getReciveAddress();//????????????
        Long reciveProvinceId = customerLine.getReciveProvinceId();
        Long reciveCityId = customerLine.getReciveCityId();
        Integer payWay = customerLine.getPayWay();//????????????
        Integer reciveTime = customerLine.getReciveTime();//????????????
        Integer reciveTimeDay = customerLine.getReciveTimeDay();
        Integer reciveTimeMonth = customerLine.getReciveTimeMonth();
        Integer recondTime = customerLine.getRecondTime();//????????????
        Integer recondTimeDay = customerLine.getRecondTimeDay();
        Integer recondTimeMonth = customerLine.getRecondTimeMonth();
        Integer invoiceTime = customerLine.getInvoiceTime();//????????????
        Integer invoiceTimeDay = customerLine.getInvoiceTimeDay();
        Integer invoiceTimeMonth = customerLine.getInvoiceTimeMonth();
        Integer collectionTime = customerLine.getCollectionTime();//????????????
        Integer collectionTimeDay = customerLine.getCollectionTimeDay();
        Integer collectionTimeMonth = customerLine.getCollectionTimeMonth();
        String remarks = customerLine.getRemarks();//??????
        if (!StringUtils.isEmpty(String.valueOf(payWay)) && payWay > 2) {
            customerLine.setReciveTime(reciveTimeMonth);
            customerLine.setRecondTime(recondTimeMonth);
            customerLine.setInvoiceTime(invoiceTimeMonth);
            customerLine.setCollectionTime(collectionTimeMonth);
        }
        customerLine.setTenantId(user.getTenantId());
        String falg = "Y";
        LocalDateTime localDateTime = LocalDateTime.now();
        //????????????
        CmCustomerLineVer cmCustomerLineVer = new CmCustomerLineVer();
        // BeanUtil.copyProperties(customerLine, cmCustomerLineVer);
        //   cmCustomerLineVer.setOrgId(customerLine.getOrgId());
        cmCustomerLineVer.setTenantId(customerLine.getTenantId());
        //cmCustomerLineVer.setOpId(user.getId());
        customerLine.setUpdateTime(localDateTime);
        customerLine.setCreateTime(LocalDateTime.now());
        customerLine.setOpId(user.getId());
        // DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (effectBegin != null) {
            customerLine.setEffectBegin(effectBegin);
        }
        if (effectEnd != null) {
            customerLine.setEffectEnd(effectEnd);
        }
        //Date effectEndDate = DateUtil.formatStringToDate(Date.from(instant));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = effectEnd.atStartOfDay().atZone(zone).toInstant();
        if (DateUtil.diffDate(Date.from(instant), new Date()) < 0) {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        try {

            //?????????????????????????????????
            if (id != null && id.longValue() > 0) {
                cmCustomerLineVer.setLineId(customerLine.getId());
                //?????????????????????,????????????????????????
                if (!"".equals(StringUtils.trimToEmpty(customerLine.getLineCodeName()))) {
                    Integer isExist = baseMapper.checkLineExist(user.getTenantId(), customerLine.getLineCodeName(), customerId, id);
                    if (isExist != null && isExist > 0) {
                        throw new BusinessException("?????????????????????????????????????????????");
                    }
                }
                falg = "YM";
                QueryWrapper<CmCustomerLineVer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("line_id", id).eq("tenant_id", customerLine.getTenantId()).orderByDesc("id");
                List<CmCustomerLineVer> cmCustomerLineList = customerLineVerMapper.selectList(queryWrapper);
                CmCustomerLineVer oldCmCustomerLine = null;
                if (cmCustomerLineList != null && cmCustomerLineList.size() > 0) {
                    oldCmCustomerLine = cmCustomerLineList.get(0);
                } else {
                    CmCustomerLine tmp = this.getById(id);
                    if (null == tmp) {
                        throw new BusinessException("????????????[" + id + "]??????????????????????????????????????????");
                    }
                    oldCmCustomerLine = new CmCustomerLineVer();
                    BeanUtil.copyProperties(tmp, oldCmCustomerLine);
                }
                customerLine.setUpdateTime(localDateTime);
                customerLine.setTenantId(oldCmCustomerLine.getTenantId());
                if (customerLine.getOrgId() == null) {
                    customerLine.setOrgId(oldCmCustomerLine.getOrgId());
                }
                CmCustomerLine oldCustomerLine = super.getById(id);
                oldCustomerLine.setIsAuth(1);
//                customerLine.setLineCodeRule(CommonUtil.getCmCustomerLineCodeRule(id));
                customerLine.setCustomerId(oldCustomerLine.getCustomerId());
                BeanUtil.copyProperties(customerLine, cmCustomerLineVer);
                cmCustomerLineVer.setId(null);
                cmCustomerLineVer.setLineId(id);
                cmCustomerLineVer.setCustomerId(oldCmCustomerLine.getCustomerId());
                cmCustomerLineVer.setUpdateTime(LocalDateTime.now());
                customerLineVerMapper.insert(cmCustomerLineVer);

                //???????????????????????????????????????VER????????????
                if (customerLine.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                    // CmCustomerLine customerLineCopye = new CmCustomerLine();
                    BeanUtil.copyProperties(customerLine, oldCustomerLine);
                    oldCustomerLine.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                    //   customerLine.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE3);
                    baseMapper.updateById(oldCustomerLine);
                }
                //?????????????????????
                //????????????????????????????????????
                CmCustomerLineSubwayVer lineSubwayVer = new CmCustomerLineSubwayVer();
                lineSubwayVer.setUpdateTime(localDateTime);
                UpdateWrapper<CmCustomerLineSubwayVer> lastTime = new UpdateWrapper<>();
                lastTime.eq("line_id", id)
                        .set("check_state", 3);
                lineSubwayVerMapper.update(lineSubwayVer, lastTime);
                //?????????????????????
                List<CmCustomerLineSubway> lineSubwayList = customerLine.getLineSubwayList();
                if (lineSubwayList != null && lineSubwayList.size() > 0) {
                    for (int i = 0; i < lineSubwayList.size(); i++) {
                        CmCustomerLineSubway cmCustomerLineSubway = lineSubwayList.get(i);
                        CmCustomerLineSubwayVer subWayVer = new CmCustomerLineSubwayVer();
                        BeanUtil.copyProperties(cmCustomerLineSubway, subWayVer);
                        subWayVer.setLineId(id);
                        subWayVer.setCustomerId(customerId);
                        subWayVer.setCheckState(0);
                        subWayVer.setSeq(i + 1);
                        subWayVer.setCreateTime(localDateTime);
                        subWayVer.setUpdateTime(localDateTime);
                        lineSubwayVerMapper.insert(subWayVer);
                    }
                }
                saveSysOperLog(SysOperLogConst.BusiCode.CustomerLine, SysOperLogConst.OperType.Update, "?????????????????????", accessToken, id);
            } else {
                //?????????????????????,????????????????????????
                if (!"".equals(StringUtils.trimToEmpty(lineCodeName))) {
                    Integer isExist = baseMapper.checkLineExist(user.getTenantId(), lineCodeName, customerId, id);
                    if (isExist != null && isExist > 0) {
                        throw new BusinessException("?????????????????????????????????????????????");
                    }
                }
                customerLine.setCreateTime(localDateTime);
                customerLine.setUpdateTime(localDateTime);
                customerLine.setTenantId(user.getTenantId());
//            customerLine.setOrgId(user.getId());
                customerLine.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                customerLine.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
                customerLine.setState(SysStaticDataEnum.CUCUSTOMER_LINE_STATE.AVAILABILITY);
                CmCustomerLine newCustomerLine = new CmCustomerLine();
                BeanUtil.copyProperties(customerLine, newCustomerLine);
                //????????????id
                QueryWrapper<CmCustomerLine> maxId = new QueryWrapper<>();
                maxId.orderByDesc("id").last("limit 1");
                CmCustomerLine maxIdCustomer = baseMapper.selectOne(maxId);
                //??????????????????
                if (maxIdCustomer != null && maxIdCustomer.getId() != null) {
                    newCustomerLine.setLineCodeRule(CommonUtil.getCmCustomerLineCodeRule(maxIdCustomer.getId()));
                } else {
                    newCustomerLine.setLineCodeRule(CommonUtil.getCmCustomerLineCodeRule(null));
                }
                baseMapper.insert(newCustomerLine);
                id = newCustomerLine.getId();
                customerLine.setLineCodeRule(newCustomerLine.getLineCodeRule());
                cmCustomerLineVer.setLineId(newCustomerLine.getId());
                if (reciveTimeDay != null) {
                    cmCustomerLineVer.setReciveTimeDay(recondTimeDay);
                }
                if (recondTimeDay != null) {
                    cmCustomerLineVer.setRecondTimeDay(recondTimeDay);
                }
                if (invoiceTimeDay != null) {
                    cmCustomerLineVer.setInvoiceTimeDay(invoiceTimeDay);
                }
                if (collectionTimeDay != null) {
                    cmCustomerLineVer.setCollectionTimeDay(collectionTimeDay);
                }
                BeanUtil.copyProperties(customerLine, cmCustomerLineVer);
                customerLineVerMapper.insert(cmCustomerLineVer);
                //?????????????????????
                List<CmCustomerLineSubway> lineSubwayList = customerLine.getLineSubwayList();
                if (lineSubwayList != null && lineSubwayList.size() > 0) {
                    for (int i = 0; i < lineSubwayList.size(); i++) {
                        CmCustomerLineSubway cmCustomerLineSubway = lineSubwayList.get(i);
                        CmCustomerLineSubwayVer subWayVer = new CmCustomerLineSubwayVer();
                        BeanUtil.copyProperties(cmCustomerLineSubway, subWayVer);
                        subWayVer.setLineId(newCustomerLine.getId());
                        subWayVer.setCustomerId(newCustomerLine.getCustomerId());
                        subWayVer.setCheckState(0);
                        subWayVer.setSeq(i + 1);
                        subWayVer.setCreateTime(localDateTime);
                        subWayVer.setUpdateTime(localDateTime);
                        lineSubwayVerMapper.insert(subWayVer);
                    }
                }
                //?????????????????????????????????
                saveSysOperLog(SysOperLogConst.BusiCode.CustomerLine, SysOperLogConst.OperType.Add, "????????????????????????", accessToken, newCustomerLine.getId());
            }

            //??????????????????
            Map inMap = new HashMap();
            inMap.put("svName", "cmCustomerLineTF");
            boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_CUST_LINE, id, SysOperLogConst.BusiCode.CustomerLine, inMap, accessToken);
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }
        } catch (Exception e) {
            falg = "F";
            e.printStackTrace();
        }
        return falg;
    }

//    @Transactional
//    public String saveOrUpdateLineNew(CmCustomerLineVo customerLine, String accessToken){
//
//        CmCustomerLine cmCustomerLine = new CmCustomerLine();
//
//    }


    @Transactional
    @Override
    public void doPublicAuth(Long lineId, int authState, String auditContent) {
        CmCustomerLine cmCustomerLine = super.getById(lineId);
        QueryWrapper<CmCustomerLineVer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("line_id", lineId);
        queryWrapper.orderByDesc("id");
        List<CmCustomerLineVer> cmCustomerInfoVers = cmCustomerLineVerService.list(queryWrapper);
        CmCustomerLineVer cmCustomerLineVer = null;
        UpdateWrapper<CmCustomerLineSubwayVer> updateWrapper = new UpdateWrapper<>();
        if (cmCustomerInfoVers != null && cmCustomerInfoVers.size() > 0) {
            cmCustomerLineVer = cmCustomerInfoVers.get(0);
        }
        cmCustomerLine.setIsAuth(com.youming.youche.conts.SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        if (authState == com.youming.youche.conts.SysStaticDataEnum.AUTH_STATE.AUTH_STATE3) {
            cmCustomerLine.setAuditContent(auditContent);
            super.update(cmCustomerLine);
            //?????????????????????

            //cmCustomerLineSubwayVerQueryWrapper.("check_state",)
            updateWrapper.eq("line_id", lineId);
            updateWrapper.set("check_state", 1);
            cmCustomerLineSubwayVerService.update(updateWrapper);
        } else if (authState == com.youming.youche.conts.SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
            long workId = cmCustomerLineVer.getWorkId() == null ? -1 : cmCustomerLineVer.getWorkId();
            if (workId > 0) {//??????????????????????????????????????????????????????
                // IWorkOrderTF workOrderTF = CallerProxy.getSVBean(IWorkOrderTF.class, "workOrderTF");
                // workOrderTF.saveWorkRouteRel(workId, cmCustomerLineVer.getLineCodeRule());
            }

            if (null != cmCustomerLineVer && null != cmCustomerLineVer.getEffectEnd()) {
                Date now = new Date();
                ZoneId zone = ZoneId.systemDefault();
                Instant instant = cmCustomerLineVer.getEffectEnd().atStartOfDay().atZone(zone).toInstant();
                if (Date.from(instant).getTime() <= now.getTime()) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
            }
            BeanUtil.copyProperties(cmCustomerLineVer, cmCustomerLine);
            //     BeanUtil.copyProperties(cmCustomerLineVer, cmCustomerLine,true, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            if (cmCustomerLineVer.getLineId() != null) {
                cmCustomerLine.setId(cmCustomerLineVer.getLineId());
            }
            cmCustomerLine.setIsAuth(com.youming.youche.conts.SysStaticDataEnum.IS_AUTH.IS_AUTH0);
            cmCustomerLine.setAuthState(com.youming.youche.conts.SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
            cmCustomerLine.setAuditContent(auditContent);
            super.saveOrUpdate(cmCustomerLine);

            //?????????????????????
            QueryWrapper<CmCustomerLineSubwayVer> cmCustomerLineSubwayVerQueryWrapper = new QueryWrapper<>();
            cmCustomerLineSubwayVerQueryWrapper.eq("line_id", cmCustomerLineVer.getLineId());
            cmCustomerLineSubwayVerQueryWrapper.eq("check_state", 0);
            List<CmCustomerLineSubwayVer> subWayList = cmCustomerLineSubwayVerService.list(cmCustomerLineSubwayVerQueryWrapper);
            //?????????????????????
            //delSubWayList(cmCustomerLineVer.getLineId());
            QueryWrapper<CmCustomerLineSubway> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("line_id", cmCustomerLineVer.getLineId());
            cmCustomerLineSubwayService.remove(queryWrapper1);
            if (subWayList != null && subWayList.size() > 0) {
                //?????????????????????
                for (CmCustomerLineSubwayVer cmCustomerLineSubwayVer : subWayList) {
                    CmCustomerLineSubway subWay = new CmCustomerLineSubway();
                    BeanUtil.copyProperties(cmCustomerLineSubwayVer, subWay);
                    subWay.setId(null);
                    //subWay.setLineId(cmCustomerLineSubwayVer.getLineId());
                    cmCustomerLineSubwayService.saveOrUpdate(subWay);
                }
                //??????ver??????????????????????????????
                updateWrapper.eq("line_id", cmCustomerLineVer.getLineId());
                updateWrapper.set("check_state", 1);
                cmCustomerLineSubwayVerService.update(updateWrapper);
            }
        }
    }

    @Override
    public String createBackhaulNumber() throws Exception {
        QueryWrapper<CmCustomerLine> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("backhaul_number").last("limit 1");
        CmCustomerLine customerLine = customerLineMapper.selectOne(queryWrapper);
        if (customerLine != null) {
            String backhaulNumber = customerLine.getBackhaulNumber();
            String lastNum = backhaulNumber.substring(backhaulNumber.indexOf("R") + 1);
            return "R" + Long.parseLong(lastNum) + 1;
        } else {
            return "R" + 10000001;
        }

    }

    @Override
    public Page<CmCustomerLineDto> getCustomerLineByBackhaul(Page<CmCustomerLineDto> linePage, CmCustomerLineVo customerLine, String accessToken) throws Exception {
        LoginInfo user = loginUtils.get(accessToken);
        ;

        customerLine.setTenantId(user.getTenantId());
        Page<CmCustomerLineDto> cmCustomerLineDtoPage = customerLineMapper.selectCustomerLineByBackhaul(linePage, customerLine);
        return cmCustomerLineDtoPage;
    }

    /**
     * @return void
     * @author ?????????
     * @Description //TODO ??????????????????
     * @date 17:47 2022/2/23 0023
     * @Param [file, records, accessToken]
     */
    @Async
    @Override
    @Transactional
    public String batchImportCustomerLine(byte[] bytes, ImportOrExportRecords records, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        List<CmCustomerLineVo> failureList = new ArrayList<>();
        InputStream is = new ByteArrayInputStream(bytes);
        List<List<String>> lists = new ArrayList<>();
        try {
            lists = getExcelContent(is, 1, (ExcelFilesVaildate[]) null);
            //???????????????
            List<String> listsRows = lists.remove(0);
            if (lists.size() == 0) {
                records.setState(5);
                records.setRemarks("??????????????????0???????????????");
                records.setFailureReason("??????????????????0???????????????");
                importOrExportRecordsService.update(records);
                return null;
            }
            int j = 1;
            int success = 0;
            SysStaticData sysStaticData = new SysStaticData();
            for (List<String> list : lists) {
                CmCustomerLineVo inCustomerLine = new CmCustomerLineVo();
                StringBuffer reasonFailure = new StringBuffer();
                String find = checkDate(list, inCustomerLine);
                if (StrUtil.isNotEmpty(find)) {
                    inCustomerLine.setReasonFailure(find);
                    failureList.add(inCustomerLine);
                    continue;
                }
                String customName = list.get(0);//????????????
                QueryWrapper<CmCustomerInfo> customerInfoQueryWrapper = new QueryWrapper<>();
                customerInfoQueryWrapper.eq("company_name", customName)
                        .eq("tenant_id", user.getTenantId())
                        .eq("type", 1);
                CmCustomerInfo customerInfo = customerInfoMapper.selectOne(customerInfoQueryWrapper);
                if (customerInfo == null) {
                    reasonFailure.append("???????????????");
                    inCustomerLine.setCustomerName(customName);
//                throw new BusinessException("??????["+customName+"]?????????");
                } else {
                    inCustomerLine.setCustomerId(customerInfo.getId() == null ? null : customerInfo.getId());//????????????
                    inCustomerLine.setContractNo("");//????????????
                    inCustomerLine.setLineName(StringUtils.isEmpty(customerInfo.getLineName()) ? null : customerInfo.getLineName());//???????????????
                    inCustomerLine.setLineTel(StringUtils.isEmpty(customerInfo.getLineTel()) ? null : customerInfo.getLinePhone());//????????????
                    inCustomerLine.setPayWay(customerInfo.getPayWay() == null ? null : customerInfo.getPayWay());
                    if (customerInfo.getPayWay() != 3) {
                        inCustomerLine.setReciveTime(customerInfo.getReciveTime() == null ? null : customerInfo.getReciveTime());//???????????????
                        inCustomerLine.setRecondTime(customerInfo.getReconciliationTime() == null ? null : customerInfo.getReconciliationTime());//???????????????
                        inCustomerLine.setInvoiceTime(customerInfo.getInvoiceTime() == null ? null : customerInfo.getInvoiceTime());//???????????????
                        inCustomerLine.setCollectionTime(customerInfo.getCollectionTime() == null ? null : customerInfo.getCollectionTime());//?????????????????????
                    } else {
                        inCustomerLine.setReciveTime(customerInfo.getReciveMonth() == null ? null : customerInfo.getReciveMonth());//???????????????
                        inCustomerLine.setRecondTime(customerInfo.getReconciliationMonth() == null ? null : customerInfo.getReconciliationMonth());//???????????????
                        inCustomerLine.setInvoiceTime(customerInfo.getInvoiceMonth() == null ? null : customerInfo.getInvoiceMonth());//???????????????
                        inCustomerLine.setCollectionTime(customerInfo.getCollectionMonth() == null ? null : customerInfo.getCollectionMonth());//???????????????

                        inCustomerLine.setReciveTimeDay(customerInfo.getReciveDay() == null ? null : customerInfo.getReciveDay());//???????????????
                        inCustomerLine.setRecondTimeDay(customerInfo.getReconciliationDay() == null ? null : customerInfo.getReconciliationDay());//???????????????
                        inCustomerLine.setInvoiceTimeDay(customerInfo.getInvoiceDay() == null ? null : customerInfo.getInvoiceDay());//???????????????
                        inCustomerLine.setCollectionTimeDay(customerInfo.getCollectionDay() == null ? null : customerInfo.getCollectionDay());//?????????????????????
                    }

                    inCustomerLine.setReciveProvinceId(customerInfo.getReciveProvinceId() == null ? null : customerInfo.getReciveProvinceId());//?????????
                    inCustomerLine.setReciveCityId(customerInfo.getReciveCityId() == null ? null : customerInfo.getReciveCityId());//?????????
                    inCustomerLine.setReciveAddress(customerInfo.getReciveAddress() == null ? null : customerInfo.getReciveAddress());//??????????????????
                }

                LocalDate startTime = LocalDate.now(); //????????????
                String endTime = list.get(1);//??????????????????
                String goodsName = list.get(2);//????????????
                String goodsTypeName = list.get(3);//????????????
                String weight = list.get(4);//????????????(???)
                String square = list.get(5);//????????????(???)

                String vehicleStatus = list.get(6);//??????(????????????)
                String vehicleLength = list.get(7);//??????(??????)

                String dependTime = list.get(8);//??????????????????
                String source = list.get(9);//??????????????????
                String des = list.get(10);//??????????????????

                String arrivalTimeLimit = list.get(11);//????????????

                String lineCodeName = "";
                if (list.size() > 12) {
                    lineCodeName = list.get(12);//????????????
                    //?????????????????????,????????????????????????
                    if (!"".equals(StringUtils.trimToEmpty(lineCodeName)) && customerInfo != null && customerInfo.getId() != null) {
                        Integer isExist = baseMapper.checkLineExist(user.getTenantId(), lineCodeName, customerInfo.getId(), null);
                        if (isExist != null && isExist > 0) {
                            reasonFailure.append("?????????????????????????????????????????????");
                        }
                    }
                }

                String cmMileageNumber = "";
                if (list.size() > 13) {
                    cmMileageNumber = list.get(13);//???????????????
                    if (StringUtils.isNotBlank(cmMileageNumber)) {
                        if (!CommonUtil.isNumber(cmMileageNumber)) {
                            reasonFailure.append("??????????????????????????????");
                        }
                    }
                }

                String mileageNumber = "";
                if (list.size() > 14) {
                    mileageNumber = list.get(14);//??????????????????
                    if (StringUtils.isNotBlank(mileageNumber)) {
                        if (!CommonUtil.isNumber(mileageNumber)) {
                            reasonFailure.append("?????????????????????????????????");
                        }
                    }
                }
//            LocalDateTime d = DateUtil.parseDate(dependTime, DateUtil.DATETIME12_FORMAT).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//            inCustomerLine.setTaiwanDate(d.toString());//????????????
                Date d = DateUtil.parseDate(dependTime, DateUtil.DATETIME_FORMAT1);
                inCustomerLine.setTaiwanDate(DateUtil.formatDate(d, DateUtil.DATETIME_FORMAT1).split(" ")[1]);//????????????

                inCustomerLine.setEffectBegin(startTime);
                LocalDate date = DateUtil.parseDate(endTime, DateUtil.DATETIME_FORMAT).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                inCustomerLine.setEffectEnd(date);
//            inCustomerLine.setSaleDaparment(user.getOrgIds().get(0));//????????????
                if (customerInfo != null && customerInfo.getOrgId() != null) {
                    inCustomerLine.setOrgId(customerInfo.getOrgId());
                    inCustomerLine.setSaleDaparmentName(sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), customerInfo.getOrgId()));
                }

                inCustomerLine.setGoodsName(goodsName);
                inCustomerLine.setGoodsType(!"?????????".equals(goodsTypeName) ? 1 : 2);
                inCustomerLine.setGoodsWeight(Float.parseFloat(weight));
                inCustomerLine.setGoodsVolume(Float.parseFloat(square));
//            d = DateUtil.parseDate(dependTime, DateUtil.DATETIME12_FORMAT);
//            inParam.put("saleDaparment",SysContexts.getCurrentOperator().getOrgId());//????????????

                sysStaticData = getSysStaticData("VEHICLE_LENGTH", vehicleLength + "");
//            sysStaticDatas = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("DRIVER_TYPE"));
                if (sysStaticData.getCodeName() != null && sysStaticData.getCodeName().equals(vehicleLength)) {
                    inCustomerLine.setVehicleLength(sysStaticData.getCodeValue());
//                inParam.put("vehicleLength", sysStaticData.getCodeValue());//????????????????????????
                }
                if (StringUtils.isBlank(sysStaticData.getCodeValue())) {
                    reasonFailure.append("???????????????");
//                throw new BusinessException("???????????????");
                }

                sysStaticData = getSysStaticData("VEHICLE_STATUS@ORD", vehicleStatus + "");
                if (sysStaticData.getCodeName() != null && sysStaticData.getCodeName().equals(vehicleStatus)) {
                    inCustomerLine.setVehicleStatus(Integer.parseInt(sysStaticData.getCodeValue()));
//                inParam.put("vehicleStatus", sysStaticData.getCodeValue());//??????????????????????????????
                }
                if (StringUtils.isBlank(sysStaticData.getCodeValue())) {
                    reasonFailure.append("?????????????????????");
//                throw new BusinessException("?????????????????????");
                }

                List<Monitor> sourceMonitors = GpsUtil.getSuggestion("??????", source);
                List<Monitor> desMonitors = GpsUtil.getSuggestion("??????", des);
                if (sourceMonitors == null || sourceMonitors.size() == 0) {
                    reasonFailure.append("????????????????????????");
//                throw new BusinessException("????????????????????????");
                }
                if (desMonitors == null || desMonitors.size() == 0) {
                    reasonFailure.append("????????????????????????");
//                throw new BusinessException("????????????????????????");
                }else {
                    Monitor sourceMonitor = sourceMonitors.get(0);
                    Monitor desMonitor = desMonitors.get(0);
                    inCustomerLine.setSourceNand(sourceMonitor.getLat());//????????????
                    inCustomerLine.setSourceEand(sourceMonitor.getLng());//????????????

                    Long[] rtStr = getPositionIdByCity(sourceMonitor.getLat(), sourceMonitor.getLng());
                    inCustomerLine.setSourceProvince(Math.toIntExact(rtStr[0]));
                    inCustomerLine.setSourceCity(Math.toIntExact(rtStr[1]));
                    inCustomerLine.setSourceCounty(Math.toIntExact(rtStr[2]));

                    inCustomerLine.setDesNand(desMonitor.getLat());//????????????
                    inCustomerLine.setDesEand(desMonitor.getLng());//????????????

                    Long[] desStr = getPositionIdByCity(desMonitor.getLat(), desMonitor.getLng());
                    inCustomerLine.setDesProvince(Math.toIntExact(desStr[0]));
                    inCustomerLine.setDesCity(Math.toIntExact(desStr[1]));
                    inCustomerLine.setDesCounty(Math.toIntExact(desStr[2]));

                    //?????????????????????
                    double mileage = GpsUtil.getDistance(Double.parseDouble(sourceMonitor.getLat()), Double.parseDouble(sourceMonitor.getLng()), Double.parseDouble(desMonitor.getLat()), Double.parseDouble(desMonitor.getLng()));
                    if (StringUtils.isNotBlank(cmMileageNumber)) {
                        inCustomerLine.setCmMileageNumber(Float.parseFloat(cmMileageNumber));//???????????????
                    } else {
                        inCustomerLine.setCmMileageNumber((float) (mileage / 1000));
                    }

                    if (StringUtils.isNotBlank(mileageNumber)) {
                        inCustomerLine.setMileageNumber(Float.parseFloat(mileageNumber));
                    } else {
                        inCustomerLine.setMileageNumber((float) (mileage / 1000));
                    }
                }
                inCustomerLine.setBackhaulFormat(-1);
                inCustomerLine.setSourceAddress(source);
                inCustomerLine.setNavigatSourceLocation(source);
                inCustomerLine.setDesAddress(des);
                inCustomerLine.setNavigatDesLocation(des);
                if (StrUtil.isNotEmpty(arrivalTimeLimit) && StringUtils.isNumeric(arrivalTimeLimit)) {
                    inCustomerLine.setArriveTime(Float.parseFloat(arrivalTimeLimit));
                } else {
                    reasonFailure.append("?????????????????????????????????");
                }
                inCustomerLine.setLineCodeName(lineCodeName);
                inCustomerLine.setPriceEnum(1);
                if (StrUtil.isEmpty(reasonFailure)) {
                    //??????
                    int status = 0;
                    String s = "";
                    try {
                        s = saveOrUpdateLine(inCustomerLine, accessToken);
                    }catch (BusinessException e){
                        reasonFailure.append(e.getMessage()+"!");
                    }
                    if ("Y".equals(s)) {
                        success++;
                    } else {
                        reasonFailure.append("???????????????????????????");
                        inCustomerLine.setReasonFailure(reasonFailure.toString());
                        failureList.add(inCustomerLine);
                    }
                } else {
                    inCustomerLine.setReasonFailure(reasonFailure.toString());
                    failureList.add(inCustomerLine);
                }
            }
            if (CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "????????????", "????????????", "????????????", "??????(???)", "??????(???)", "????????????"
                        , "??????????????????", "????????????", "????????????", "????????????", "????????????", "????????????", "???????????????(KM)"
                        , "??????????????????(KM)", "????????????"};
                resourceFild = new String[]{"getCustomerName", "getEffectEnd", "getGoodsName", "getGoodsTypeName", "getGoodsWeight",
                        "getGoodsVolume", "getVehicleType", "getVehicleLength", "getTaiwanDate", "getSourceAddress", "getDesAddress",
                        "getArriveTime", "getLineCodeName", "getCmMileageNumber", "getMileageNumber", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, CmCustomerLineVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "?????????" + failureList.size() + "?????????");
                    records.setState(2);
                }
                if (success == 0) {
                    records.setRemarks(failureList.size() + "?????????");
                    records.setState(4);
                }
            } else {
                records.setRemarks(success + "?????????");
                records.setState(3);
            }
            importOrExportRecordsService.update(records);
        } catch (Exception e) {
            records.setState(5);
            records.setRemarks("????????????,????????????????????????????????????");
            records.setFailureReason("????????????,????????????????????????????????????");
            importOrExportRecordsService.update(records);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CmCustomerLine selectLineById(Long tenantId, String lineId) {
        return baseMapper.selectLineById(tenantId, lineId);
    }

    @Async
    @Override
    public void exportCustomerLine(ImportOrExportRecords records, CmCustomerLineVo customerLineVo, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        try {
            if (!StringUtils.isEmpty(customerLineVo.getSourceProvinceAndCity())) {
                String sourceProvinceAndCity = customerLineVo.getSourceProvinceAndCity();
                int first = sourceProvinceAndCity.indexOf(",");
                int trim = sourceProvinceAndCity.indexOf(" ");
                int last = sourceProvinceAndCity.lastIndexOf(",");
                String province = sourceProvinceAndCity.substring(first + 1, trim);
                String city = sourceProvinceAndCity.substring(last + 1);
                customerLineVo.setSourceProvince(Integer.parseInt(province));
                customerLineVo.setSourceCity(Integer.parseInt(city));
            }
            if (!StringUtils.isEmpty(customerLineVo.getDesProvinceAndCity())) {
                String desProvinceAndCity = customerLineVo.getDesProvinceAndCity();
                int first = desProvinceAndCity.indexOf(",");
                int trim = desProvinceAndCity.indexOf(" ");
                int last = desProvinceAndCity.lastIndexOf(",");
                String province = desProvinceAndCity.substring(first + 1, trim);
                String city = desProvinceAndCity.substring(last + 1);
                customerLineVo.setDesProvince(Integer.parseInt(province));
                customerLineVo.setDesCity(Integer.parseInt(city));
            }
            List<CmCustomerLineDto> cmCustomerLines = baseMapper.exportAllLine(customerLineVo, user);
            if (cmCustomerLines != null && cmCustomerLines.size() > 0) {
                for (int i = 0; i < cmCustomerLines.size(); i++) {
                    CmCustomerLineDto customerLineDto = cmCustomerLines.get(i);
                    Integer payWay = customerLineDto.getPayWay();//????????????
                    Integer authState = customerLineDto.getAuthState();//???????????????1-?????????  2-????????? 3-????????????
                    Integer state = customerLineDto.getState();//??????????????????
                    Integer vehicleStatus = customerLineDto.getVehicleStatus();//??????
                    String vehicleLength = customerLineDto.getVehicleLength();//??????PRICE_ENUM
                    Integer priceEnum = customerLineDto.getPriceEnum();//????????????
//                    String payWayCodeName = getSysStaticDataId("PAY_WAY", payWay + "").getCodeName();
                    if (!StringUtils.isEmpty(String.valueOf(payWay))) {
                        if (payWay == 1) {
                            customerLineDto.setPayWayName("????????????");
                        } else if (payWay == 2) {
                            customerLineDto.setPayWayName("??????+????????????");
                        } else {
                            customerLineDto.setPayWayName("??????+????????????");
                        }
                    }
                    if (!StringUtils.isEmpty(String.valueOf(authState))) {
                        if (authState == 1) {
                            customerLineDto.setAuthStateName("?????????");
                        } else if (authState == 2) {
                            customerLineDto.setAuthStateName("?????????");
                        } else {
                            customerLineDto.setAuthStateName("????????????");
                        }
                    }
                    if (!StringUtils.isEmpty(String.valueOf(state))) {
                        if (authState == 1) {
                            customerLineDto.setStateName("??????");
                        } else {
                            customerLineDto.setStateName("??????");
                        }
                    }
                    String vehicleStatusName = getSysStaticDataId("VEHICLE_STATUS@ORD", vehicleStatus + "").getCodeName();
                    String vehicleLengthName = getSysStaticDataId("VEHICLE_LENGTH", vehicleLength + "").getCodeName();
                    customerLineDto.setVehicleType(vehicleStatusName + "/" + vehicleLengthName);
                    String priceEnumCodeName = getSysStaticDataId("PRICE_ENUM", priceEnum + "").getCodeName();
                    customerLineDto.setPriceEnumName(priceEnumCodeName);

                    //????????????
                    Integer reciveTime = customerLineDto.getReciveTime();
                    Integer reciveTimeDay = customerLineDto.getReciveTimeDay();
                    Integer recondTime = customerLineDto.getRecondTime();
                    Integer recondTimeDay = customerLineDto.getRecondTimeDay();
                    Integer invoiceTime = customerLineDto.getInvoiceTime();
                    Integer invoiceTimeDay = customerLineDto.getInvoiceTimeDay();
                    Integer collectionTime = customerLineDto.getCollectionTime();
                    Integer collectionTimeDay = customerLineDto.getCollectionTimeDay();
                    if (StringUtils.isEmpty(String.valueOf(payWay)) && payWay > 2) {
                        customerLineDto.setReciveTimeMonth(reciveTime);
                        customerLineDto.setRecondTimeMonth(recondTime);
                        customerLineDto.setInvoiceTimeMonth(invoiceTime);
                        customerLineDto.setCollectionTimeMonth(collectionTime);
                    }

                    if (customerLineDto.getOrgId() != null) {
                        customerLineDto.setSaleDaparmentName(sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), customerLineDto.getOrgId()));
                    }
//                    Float cmMileageNumber = customerLineDto.getCmMileageNumber();
//                    if (StringUtils.isEmpty(String.valueOf(cmMileageNumber))) {
//                        cmMileageNumber = cmMileageNumber / 1000;
//                    }
//                    customerLineDto.setCmMileageNumber(cmMileageNumber);
//                    Long estimateIncome = customerLineDto.getEstimateIncome();
//                    if (StringUtils.isEmpty(String.valueOf(estimateIncome))){
//                        estimateIncome = estimateIncome / 100;
//                    }
//                    customerLineDto.setEstimateIncome(estimateIncome);
                }
                String[] showName = null;
                String[] resourceFild = null;
                String fileName = null;

                showName = new String[]{"????????????", "????????????", "????????????", "????????????", "????????????",
                        "????????????", "?????????(KM)", "????????????", "????????????", "??????", "????????????", "????????????", "????????????(???)",
                        "????????????", "????????????"};
                resourceFild = new String[]{
                        "getLineCodeRule", "getLineCodeName", "getSaleDaparmentName", "getSourceAddress",
                        "getDesAddress", "getCompanyName", "getMileageNumber", "getVehicleType", "getPriceEnumName", "getEstimateIncomeStr",
                        "getPayWayName", "getReciveTimeStr", "getCollectionTimeStr", "getStateName", "getAuthStateName"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(cmCustomerLines, showName, resourceFild, CmCustomerLineDto.class,
                        null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
                os.close();
                inputStream.close();
                records.setMediaUrl(path);
                records.setState(2);
                importOrExportRecordsService.update(records);
            } else {
                records.setState(4);
                importOrExportRecordsService.update(records);
            }
        } catch (Exception e) {
            records.setState(3);
            importOrExportRecordsService.update(records);
            e.printStackTrace();
        }
    }

    @Override
    public Page<CmCustomerLine> getCustomerLineByLineCode(Page<CmCustomerLine> page, String lineCodeRule, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        ;
        Page<CmCustomerLine> cmCustomerLinePage = baseMapper.getCustomerLineByLineCode(page, loginInfo.getTenantId(), lineCodeRule);
        return cmCustomerLinePage;
    }

    @Override
    public CmCustomerLineOrderExtend selectLineListByAddress(Long customerId, String accessToken, String lineCodeRule) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        CmCustomerLineOrderExtend cmCustomerLineOrderExtend = customerLineMapper.selectLineListByAddress(customerId, lineCodeRule, loginInfo.getTenantId());
        return cmCustomerLineOrderExtend;
    }

    /**
     * ?????? id  ??????????????????
     *
     * @param lineId
     * @param workId
     * @param isEdit
     * @param accessToken
     */
    @Override
    public CmCustomerLineOutDto getCustomerLineInfo(Long lineId, Long workId, Integer isEdit, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        CmCustomerLineOutDto outVer = CmCustomerLineOutDto.of();
        if (lineId > 0) {
            CmCustomerLine cmCustomerLine = get(lineId);
            CmCustomerLineOutDto out = CmCustomerLineOutDto.of();
            BeanUtil.copyProperties(cmCustomerLine, out);
            if (out.getDriverUserId() != null && out.getDriverUserId() > 0) {
                UserDataInfo userDataInfo = userDataInfoService.get(out.getDriverUserId());
                if (userDataInfo != null) {
                    String driver = userDataInfo.getLinkman() + "(" + userDataInfo.getMobilePhone() + ")";
                    out.setDriver(driver);
                }
            }
            // TODO: 2022/3/29 ???????????? ??????????????????
            LambdaQueryWrapper<CmCustomerInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CmCustomerInfo::getId, cmCustomerLine.getId());
            wrapper.eq(CmCustomerInfo::getTenantId, loginInfo.getTenantId());
            CmCustomerInfo cmCustomerInfo = customerInfoMapper.selectOne(wrapper);
            if (cmCustomerInfo != null) {
                out.setCustomerName(cmCustomerInfo.getCustomerName());
            }
            Integer isAuth = out.getIsAuth();
            if ((isEdit == 2 || isEdit == 3) && isAuth == SysStaticDataEnum.IS_AUTH.IS_AUTH1 && cmCustomerInfo.getAuthState().equals(EnumConsts.CUSTOMER_AUTH_STATE.STATE2)) {
                LambdaQueryWrapper<CmCustomerLineVer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(CmCustomerLineVer::getLineId, lineId)
                        .eq(CmCustomerLineVer::getTenantId, loginInfo.getTenantId());
                CmCustomerLineVer ver = customerLineVerMapper.selectOne(lambdaQueryWrapper);
                if (null != ver) {
                    BeanUtil.copyProperties(ver, outVer);
                    if (outVer.getDriverUserId() != null && outVer.getDriverUserId() > 0) {
                        UserDataInfo userDataInfo = userDataInfoService.get(outVer.getDriverUserId());
                        if (userDataInfo != null) {
                            String driver = userDataInfo.getLinkman() + "(" + userDataInfo.getMobilePhone() + ")";
                            outVer.setDriver(driver);
                        }

                    }
                }
            }
        } else {
            throw new BusinessException("??????id??????");
        }
        // TODO ???????????????????????????
        //???????????????????????????
//        rtnMap.put("hasAllData", "0");//??????????????????
//        rtnMap.put("hasCostPermission", "0");//????????????
//        rtnMap.put("hasIncomePermission", "0");//????????????
//
//        PermissionCacheUtil permissionUtil = new PermissionCacheUtil();
//        if (permissionUtil.hasAllData())
//            rtnMap.put("hasAllData", "1");
//        if (permissionUtil.hasCostPermission())
//            rtnMap.put("hasCostPermission", "1");
//        if (permissionUtil.hasIncomePermission())
//            rtnMap.put("hasIncomePermission", "1");
        return outVer;
    }

    @Override
    public CmCustomerLine getCmCustomerLineById(long id) {
        CmCustomerLine cmCustomerLine = this.getById(id);
        if (cmCustomerLine != null) {
            return cmCustomerLine;
        }
        return null;
    }

    @Override
    public List<CmCustomerLineSubway> getCustomerLineSubwayList(long lineId) {
        QueryWrapper<CmCustomerLineSubway> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("line_id", lineId);
        List<CmCustomerLineSubway> cmCustomerLineSubwayList = cmCustomerLineSubwayService.list(queryWrapper);
        return cmCustomerLineSubwayList;
    }

    @Override
    public CmCustomerLine queryLineForOrderTask(String lineCodeRule,
                                                List<Long> orgList, LoginInfo user) {
        if (orgList == null) {
            throw new BusinessException("????????????????????????");
        }

        if (lineCodeRule == null || "".equals(lineCodeRule)) {
            log.error("??????????????????");
            return null;
        }
        LambdaQueryWrapper<CmCustomerLine> lambda = Wrappers.lambdaQuery();
        lambda.eq(CmCustomerLine::getLineCodeRule, lineCodeRule);
        Boolean hasAllDataPermission = sysRoleService.hasAllData(user);
        if (!hasAllDataPermission) {
            if (orgList.size() > 0) {
                lambda.in(CmCustomerLine::getSaleDaparment, orgList);
            }
        }
        return this.getOne(lambda);
    }

    @Override
    public Page<QueryLineByTenantForWXDto> doQueryLineByTenantForWX(QueryLineByTenantForWXVo vo, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        // ????????????
        if (StringUtils.isNotBlank(vo.getCompanyName())) {
            vo.setCompanyName("'" + vo.getCompanyName() + "'");
        }
        if (StringUtils.isNotBlank(vo.getLineCodeName())) {
            vo.setLineCodeName("'" + vo.getLineCodeName() + "'");
        }
        if (StringUtils.isNotBlank(vo.getLineCodeRule())) {
            vo.setLineCodeRule("'" + vo.getLineCodeRule() + "'");
        }
        if (StringUtils.isNotBlank(vo.getDetailAddress())) {
            vo.setDetailAddress("'" + vo.getDetailAddress() + "'");
        }

        vo.setTenantId(loginInfo.getTenantId());
        Page<QueryLineByTenantForWXDto> pageDto = baseMapper.doQueryLineByTenantForWX(new Page<QueryLineByTenantForWXDto>(pageNum, pageSize), vo);
        try {
            List<QueryLineByTenantForWXDto> records = pageDto.getRecords();
            for (QueryLineByTenantForWXDto dto : records) {
                Integer pid = dto.getSourceProvince();
                Integer cid = dto.getSourceCity();
                Integer sourceCounty = dto.getSourceCounty();
                Integer dpid = dto.getDesProvince();
                Integer dcid = dto.getDesCity();
                Integer desCounty = dto.getDesCounty();


                /***??????????????????****/
                String estimateIncome = null;
                if (dto.getEstimateIncome() != null) {
                    estimateIncome = String.valueOf(dto.getEstimateIncome());
                    BigDecimal dv = new BigDecimal(100);
                    dto.setEstimateIncome(Double.valueOf(new BigDecimal(estimateIncome).divide(dv).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                }

                if (pid != null) {
                    dto.setSourceProvinceName(getSysStaticDataId("SYS_PROVINCE", String.valueOf(pid)).getCodeName());
                } else {
                    dto.setSourceProvinceName("");
                }
                if (cid != null) {
                    dto.setSourceCityName(getSysStaticDataId("SYS_CITY", String.valueOf(cid)).getCodeName());
                } else {
                    dto.setSourceCityName("");
                }
                if (dpid != null) {
                    dto.setDesProvinceName(getSysStaticDataId("SYS_PROVINCE", String.valueOf(dpid)).getCodeName());
                } else {
                    dto.setDesProvinceName("");
                }
                if (dcid != null) {
                    dto.setDesCityName(getSysStaticDataId("SYS_CITY", String.valueOf(dcid)).getCodeName());
                } else {
                    dto.setDesCityName("");
                }
                if (sourceCounty != null) {
                    dto.setSourceCountyName(getSysStaticDataId("SYS_DISTRICT", String.valueOf(sourceCounty)).getCodeName());
                } else {
                    dto.setSourceCountyName("");
                }
                if (desCounty != null) {
                    dto.setDesCountyName(getSysStaticDataId("SYS_DISTRICT", String.valueOf(desCounty)).getCodeName());
                } else {
                    dto.setDesCountyName("");
                }

                //?????????????????????
                long lineId = dto.getLineId();
                List<CmCustomerLineSubway> subWayList = getCustomerLineSubwayList(lineId);
                dto.setSubWayList(subWayList);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return pageDto;
    }

    @Override
    public CmCustomerLineOutDto getCustomerLineInfoForWx(Long lineId, String accessToken) {
        CmCustomerLine line = this.getCmCustomerLineById(lineId);
        CmCustomerLineOutDto out = CmCustomerLineOutDto.of();
        BeanUtil.copyProperties(line, out);
        SysOrganize byId = iSysOrganizeService.getById(line.getOrgId());
        if (byId != null) {
            out.setOrgName(byId.getOrgName());
        }
        List<CmCustomerLineSubway> subWayList = this.getCustomerLineSubwayList(lineId);
        out.setSubWayList(subWayList);
        if (out.getSourceProvince() != null) {
            String sourceProvinceName = getSysStaticDataId("SYS_PROVINCE", out.getSourceProvince() + "").getCodeName();
            out.setSourceProvinceName(sourceProvinceName);
        }
        if (out.getSourceCity() != null) {
            String sourceRegionName = getSysStaticDataId("SYS_CITY", out.getSourceCity() + "").getCodeName();
            out.setSourceCityName(sourceRegionName);
        }
        if (out.getSourceCounty() != null) {
            String sourceCountyName = getSysStaticDataId("SYS_DISTRICT", out.getSourceCounty() + "").getCodeName();
            out.setSourceCountyName(sourceCountyName);
        }
        if (out.getDesProvince() != null) {
            String desProvinceName = getSysStaticDataId("SYS_PROVINCE", out.getDesProvince() + "").getCodeName();
            out.setDesProvinceName(desProvinceName);
        }
        if (out.getDesCity() != null) {
            String desRegionName = getSysStaticDataId("SYS_CITY", out.getDesCity() + "").getCodeName();
            out.setDesCityName(desRegionName);
        }
        if (out.getDesCounty() != null) {
            String desCountyName = getSysStaticDataId("SYS_DISTRICT", out.getDesCounty() + "").getCodeName();
            out.setDesCountyName(desCountyName);
        }
        return out;
    }

    @Override
    public List<WorkbenchDto> getTableLineCount() {
        return baseMapper.getTableLineCount();
    }


    /**
     * ????????????
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    private SysStaticData getSysStaticData(String codeType, String codeName) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));

        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (codeName.indexOf(sysStaticData.getCodeName()) >= 0) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }


    public SysStaticData getSysStaticDataId(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    List<List<String>> getExcelContent(InputStream inputStream, int beginRow, ExcelFilesVaildate[] validates) throws Exception {
        DataFormatter dataFormatter = new DataFormatter();
        List<List<String>> fileContent = new ArrayList();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        if (rows >= 1000 + beginRow) {
            throw new BusinessException("excel??????????????????????????????????????????????????????" + 1000);
        } else {
            if (rows >= beginRow) {
                List rowList = null;
                Row row = null;

                for (int i = beginRow - 1; i < rows; ++i) {
                    row = sheet.getRow(i);
                    rowList = new ArrayList();
                    fileContent.add(rowList);
                    if (row != null) {
                        int cells = row.getLastCellNum();
                        if (cells > 200) {
                            throw new BusinessException("??????????????????200????????????????????????");
                        }

                        for (int j = 0; j < cells; ++j) {
                            Cell cell = row.getCell(j);
                            String cellValue = "";
                            if (cell != null) {
                                log.debug("Reading Excel File row:" + i + ", col:" + j + " cellType:" + cell.getCellType());
                                switch (cell.getCellType()) {
                                    case 0:
                                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                            cellValue = DateUtil.formatDateByFormat(cell.getDateCellValue(), DateUtil.DATETIME_FORMAT);
                                        } else {
                                            cellValue = dataFormatter.formatCellValue(cell);
                                        }
                                        break;
                                    case 1:
                                        cellValue = cell.getStringCellValue();
                                        break;
                                    case 2:
                                        cellValue = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    case 3:
                                        cellValue = "";
                                        break;
                                    case 4:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case 5:
                                        cellValue = String.valueOf(cell.getErrorCellValue());
                                }
                            }

                            if (validates != null && validates.length > j) {
                                if (cellValue == null) {
                                    throw new BusinessException("???" + (i + beginRow - 1) + "???,???" + (j + 1) + "????????????????????????" + validates[j].getErrorMsg());
                                }

                                Pattern p = Pattern.compile(validates[j].getPattern());
                                Matcher m = p.matcher(cellValue);
                                if (!m.matches()) {
                                    throw new BusinessException("???" + (i + beginRow - 1) + "???,???" + (j + 1) + "????????????????????????" + validates[j].getErrorMsg());
                                }
                            }

                            rowList.add(cellValue);
                        }
                    }
                }
            }

            return fileContent;
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public Long[] getPositionIdByCity(String lat, String lng) {
        Long[] rtStr = new Long[3];
        try {
            Map reqMap = GpsUtil.getBaiduAdder(lat + "," + lng);
            String city = reqMap.get("city") + "";
            String province = reqMap.get("province") + "";
            String district = reqMap.get("district") + "";
            Long cityId = null;
            Long provinceId = null;
            Long districtId = null;
            if (StringUtils.isNotEmpty(province)) {
                provinceId = Long.parseLong(getSysStaticData("SYS_PROVINCE", province + "").getCodeValue());
                //List<Province> provinces= SysStaticDataUtil.getProvinceDataList("SYS_PROVINCE");
            }
            if (StringUtils.isNotEmpty(city)) {
                cityId = Long.parseLong(getSysStaticData("SYS_CITY", city + "").getCodeValue());
            }
            if (StringUtils.isNotEmpty(district)) {
                districtId = Long.parseLong(getSysStaticData("SYS_DISTRICT", district + "").getCodeValue());
            }
            rtStr[0] = provinceId;
            rtStr[1] = cityId;
            rtStr[2] = districtId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtStr;
    }

    String checkDate(List<String> strings, CmCustomerLineVo inCustomerLine) {
        String find = "";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT);
        for (int i = 0; i <= 14; i++) {
            switch (i) {
                case 0:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "????????????????????????!";
                    } else {
                        inCustomerLine.setCustomerName(strings.get(i));
                    }
                    break;
                case 1:
                    try {
                        if (ExcelUtils.getExcelValue(strings, i) == null) {
                            find += "?????????????????????!";
                        } else {
                            inCustomerLine.setEffectEnd(LocalDate.parse(strings.get(i), dateTimeFormatter));
                        }
                    } catch (Exception e) {
                        find += "????????????????????????!";
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "????????????????????????!";
                    } else {
                        inCustomerLine.setGoodsName(strings.get(i));
                    }
                    break;
                case 3:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "????????????????????????!";
                    } else {
                        inCustomerLine.setGoodsTypeName((strings.get(i)));
                    }
                    break;
                case 4:
                    try {
                        if (ExcelUtils.getExcelValue(strings, i) == null) {
                            find += "????????????????????????!";
                        } else {
                            inCustomerLine.setGoodsWeight(Float.valueOf(strings.get(i)));
                        }
                    } catch (Exception e) {
                        find += "??????????????????????????????!";
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    try {
                        if (ExcelUtils.getExcelValue(strings, i) == null) {
                            find += "????????????????????????!";
                        } else {
                            inCustomerLine.setGoodsVolume(Float.valueOf(strings.get(i)));
                        }
                    } catch (Exception e) {
                        find += "??????????????????????????????!";
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "??????????????????!";
                    } else {
                        inCustomerLine.setVehicleType(strings.get(i));
                    }
                    break;
                case 7:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "??????????????????!";
                    } else {
                        inCustomerLine.setVehicleLength(strings.get(i));
                    }
                    break;
                case 8:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "????????????????????????!";
                    } else {
                        inCustomerLine.setTaiwanDate(strings.get(i));
                    }
                    break;
                case 9:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "?????????????????????!";
                    } else {
                        inCustomerLine.setSourceAddress(strings.get(i));
                    }
                    break;
                case 10:
                    if (ExcelUtils.getExcelValue(strings, i) == null) {
                        find += "?????????????????????!";
                    } else {
                        inCustomerLine.setDesAddress(strings.get(i));
                    }
                    break;
                case 11:
                    try {
                        if (ExcelUtils.getExcelValue(strings, i) == null) {
                            find += "????????????????????????!";
                        } else {
                            inCustomerLine.setArriveTime(Float.valueOf(strings.get(i)));
                        }
                    } catch (Exception e) {
                        find += "??????????????????????????????!";
                        e.printStackTrace();
                    }
                    break;
                case 12:
                    inCustomerLine.setLineCodeName(strings.get(i));
                    break;
                case 13:
                    try {
                        if (ExcelUtils.getExcelValue(strings, i) == null) {
                            find += "???????????????????????????!";
                        } else {
                            inCustomerLine.setCmMileageNumber(Float.valueOf(strings.get(i)));
                        }
                    } catch (Exception e) {
                        find += "??????????????????????????????!";
                        e.printStackTrace();
                    }
                    break;
                case 14:
                    try {
                        if (ExcelUtils.getExcelValue(strings, i) == null) {
                            find += "?????????????????????!";
                        } else {
                            inCustomerLine.setMileageNumber(Float.valueOf(strings.get(i)));
                        }
                    } catch (Exception e) {
                        find += "????????????????????????";
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return find;
    }

}
