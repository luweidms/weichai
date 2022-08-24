package com.youming.youche.record.provider.service.impl.license;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DateUtil;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.license.ILicenseService;
import com.youming.youche.record.api.tenant.ITenantVehicleCertRelService;
import com.youming.youche.record.domain.license.License;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRel;
import com.youming.youche.record.dto.LicenseDto;
import com.youming.youche.record.dto.license.LicenseDetailsDto;
import com.youming.youche.record.dto.license.ZzxqDto;
import com.youming.youche.record.provider.mapper.license.LicenseMapper;
import com.youming.youche.record.vo.LicenseDetailsVo;
import com.youming.youche.record.vo.LicenseVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
public class lIcenseServiceImpl extends BaseServiceImpl<LicenseMapper, License> implements ILicenseService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Resource
    ITenantVehicleCertRelService iTenantVehicleCertRelService;

    @Override
    public Page<LicenseVo> queryAllFindByType(Page<LicenseVo> page, String accessToken, LicenseDto licenseDto) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<LicenseVo> licenseVoPage = baseMapper.queryAllFindByType(page, loginInfo.getTenantId(), licenseDto);
        if (licenseVoPage != null) {
            Calendar now = Calendar.getInstance();
            int i = now.get(Calendar.YEAR);

            int i1 = now.get(Calendar.MONTH) + 1;
            if (licenseVoPage.getRecords() != null && licenseVoPage.getRecords().size() > 0) {
                List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
                List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));

                List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
                Iterator<LicenseVo> iterator = licenseVoPage.getRecords().iterator();
                while (iterator.hasNext()) {
                    LicenseVo record = iterator.next();
                    if (StrUtil.isEmpty(record.getAnnualreviewType())) {
                        iterator.remove();
                        continue;
                    }
//                }
//                for (LicenseVo record : licenseVoPage.getRecords()) {
                    record.setVehicleStatusName("");
                    record.setVehicleLengthName("");
                    if (record.getVehicleStatus() != null && record.getVehicleStatus() > 0) {

                        Integer vehicleStatus1 = record.getVehicleStatus();
                        for (SysStaticData sysStaticData : vehicleStatusList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleStatus1))) {
                                record.setVehicleStatusName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    }
                    try {
                        if (StringUtils.isNotBlank(record.getVehicleLength()) && !"-1".equals(record.getVehicleLength())) {
                            String vehicleLength1 = record.getVehicleLength();
                            if (record.getVehicleStatus() != null && record.getVehicleStatus() == 8) {

                                for (SysStaticData sysStaticData : vehicleStatusSubtypeList) {
                                    if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                        record.setVehicleLengthName(sysStaticData.getCodeName());
                                        break;
                                    }
                                }
                            } else {

                                for (SysStaticData sysStaticData : vehicleLengthList) {
                                    if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                        record.setVehicleLengthName(sysStaticData.getCodeName());
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (record.getVehicleStatus() != null) {
                        if (record.getVehicleStatus() > 0
                                && StringUtils.isNotBlank(record.getVehicleLength())
                                && !record.getVehicleLength().equals("-1")) {
                            record.setVehicleInfo(record.getVehicleStatusName() + "/" + record.getVehicleLengthName());
                        } else if (record.getVehicleStatus() > 0 &&
                                (record.getVehicleLength() != null &&
                                        (StringUtils.isNotBlank(record.getVehicleLength()) || !record.getVehicleLength().equals("-1")))) {
                            record.setVehicleInfo(record.getVehicleStatusName());
                        } else {
                            record.setVehicleInfo(record.getVehicleLengthName());
                        }
                    }
                    record.setName(loginInfo.getName());
                    String effectiveDate = record.getEffectiveDate();
                    if (record.getLicenceType() == 1 || record.getLicenceType() == 2) {
                        if (record.getAnnualreviewData()==null && record.getEffectiveDate()==null){
                            String plateNumber = record.getPlateNumber();
//                            QueryWrapper queryWrapper = new QueryWrapper();
//                            queryWrapper.eq("plate_number",record.getPlateNumber());
                            List<TenantVehicleCertRel> list = iTenantVehicleCertRelService.findPlateNumber(plateNumber);
                            if (list != null) {
                                DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT);
                                TenantVehicleCertRel tenantVehicleCertRel = list.get(0);
                                if (record.getLicenceType() == 1) {
                                    if (tenantVehicleCertRel != null && tenantVehicleCertRel.getAnnualVeriTimeEnd() != null && tenantVehicleCertRel.getAnnualVeriTime() != null) {
                                        record.setAnnualreviewData(tenantVehicleCertRel.getAnnualVeriTime().format(df));
                                        record.setEffectiveDate(tenantVehicleCertRel.getAnnualVeriTimeEnd().format(df));
                                        effectiveDate = tenantVehicleCertRel.getAnnualVeriTimeEnd().format(df);
                                    }
                                } else {
                                    if (tenantVehicleCertRel != null && tenantVehicleCertRel.getVehicleValidityTimeBegin() != null && tenantVehicleCertRel.getVehicleValidityTime() != null) {
                                        record.setAnnualreviewData(tenantVehicleCertRel.getVehicleValidityTimeBegin().format(df));
                                        record.setEffectiveDate(tenantVehicleCertRel.getVehicleValidityTime().format(df));
                                        effectiveDate = tenantVehicleCertRel.getVehicleValidityTime().format(df);
                                    }
                                }
                            }
                        }
                    }
                    if (effectiveDate != null) {
                        int substring = Integer.parseInt(effectiveDate.substring(0, 4));
                        int substring1 = Integer.parseInt(effectiveDate.substring(5, 7));
                        if ((i - substring) >= 0) {
                            record.setLeftDate("已到期");
                            if ((i - substring) == 0) {
                                if (i1 - substring1 >= 0) {
                                    if (i1 - substring1 == 0) {
                                        record.setLeftDate("本月到期");
                                    } else if (i1 - substring1 == 1) {
                                        record.setLeftDate("到期一个月");
                                    } else if (i1 - substring1 == 2) {
                                        record.setLeftDate("到期二个月");
                                    } else if (i1 - substring1 == 3) {
                                        record.setLeftDate("到期三个月");
                                    } else {
                                        record.setLeftDate("已到期");
                                    }
                                } else if (i1 - substring1 <= 0) {
                                    if (i1 - substring1 == 0) {
                                        record.setLeftDate("本月到期");
                                    } else if (i1 - substring1 == -1) {
                                        record.setLeftDate("还有一个月到期");
                                    } else if (i1 - substring1 == -2) {
                                        record.setLeftDate("还有二个月到期");
                                    } else if (i1 - substring1 == -3) {
                                        record.setLeftDate("还有三个月到期");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return licenseVoPage;
    }

    /**
     * 证照详情
     */
    @Override
    public List<ZzxqDto> queryAllFindByAll(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<ZzxqDto> licenseDtoPage = baseMapper.queryAllFindByAll(loginInfo.getTenantId());
        long now = System.currentTimeMillis();//获取出来的是当前时间的毫秒值
        Date date = new Date(); //把毫秒值转化为时间格式
        date.setTime(now);
        //创建格式化时间日期类
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String format = dateFormat.format(date);
        int count = 0;
        if (licenseDtoPage != null && licenseDtoPage.size() > 0) {
            for (ZzxqDto record : licenseDtoPage) {
                if (record.getLicenceType() == 1) {
                    if (record.getViolationTime() != null) {
                        String violationTime = record.getViolationTime();
                        String wzdate = violationTime.substring(0, 7);
                        if (wzdate.equals(dateFormat.format(date))) {
                            record.setWzDealNum(1);
                        } else {
                            record.setWzDealNum(0);
                        }
                    } else {
                        record.setWzDealNum(0);
                    }
                    if (record.getRenfaTime() != null) {
                        String renfaTime = record.getRenfaTime();
                        String rnefaDate = renfaTime.substring(0, 7);
                        if (rnefaDate != dateFormat.format(date)) {
                            record.setWzNotDealNum(0);
                        } else if (dateFormat.format(date).equals(rnefaDate)) {
                            record.setWzDealNum(1);
                        }
                    } else {
                        record.setWzNotDealNum(0);
                    }
                    if (record.getReportDate() != null) {
                        String reportDate = record.getReportDate();
                        String sgDate = reportDate.substring(0, 7);
//                        if (dateFormat.format(date).equals(sgDate)) {
//                            record.setSgNotDealNum(1);
//                        } else {
//                            record.setSgNotDealNum(0);
//                        }
//                    } else {
//                        record.setSgNotDealNum(0);
                    }
                    if (record.getAccidentStatus()!=null){
                        if (record.getAccidentStatus() == 1 || record.getAccidentStatus() == 2){
                            record.setSgNotDealNum(1);
                        }else {
                            record.setSgDealNum(0);
                        }
                        if (record.getAccidentStatus() == 3){
                            record.setSgDealNum(1);
                        }else {
                            record.setSgNotDealNum(0);
                        }
                    }else {
                        record.setSgDealNum(0);
                        record.setSgNotDealNum(0);
                    }
                    if (record.getClaimDate() != null) {
                        String updateTime = record.getClaimDate();
                        String sgdate = updateTime.substring(0, 7);
//                        if (format.equals(sgdate)) {
//                            record.setSgDealNum(1);
//                        } else {
//                            record.setSgDealNum(0);
//                        }
//                    } else {
//                        record.setSgDealNum(0);
                    }

                    if (record.getAnnualVeriTime() != null) {
                        String nsdate = record.getAnnualVeriTime().substring(0, 7);
                        if (format.equals(nsdate)) {
                            record.setNsDealNum(1);
                        } else {
                            record.setNsDealNum(0);
                        }
                    } else {
                        record.setNsDealNum(0);
                    }
                    if (record.getAnnualVeriTimeEnd() != null) {
                        String nsend = record.getAnnualVeriTimeEnd().substring(0, 7);
                        if (nsend.equals(format)) {
                            record.setNsNotDealNum(1);
                        } else {
                            record.setNsNotDealNum(0);
                        }
                    } else {
                        record.setNsNotDealNum(0);
                    }
                    if (record.getInsuranceTime() != null) {
                        String jqdate = record.getInsuranceTime().substring(0, 7);
                        if (jqdate.equals(format)) {
                            record.setJxxDealNum(1);
                        } else {
                            record.setJxxDealNum(0);
                        }
                    } else {
                        record.setJxxDealNum(0);
                    }
                    if (record.getInsuranceTimeEnd() != null) {
                        String jqend = record.getInsuranceTimeEnd().substring(0, 7);
                        if (jqend.equals(format)) {
                            record.setJxxNotDealNum(1);
                        } else {
                            record.setJxxNotDealNum(0);
                        }
                    } else {
                        record.setJxxNotDealNum(0);
                    }
                    if (record.getBusiInsuranceTime() != null) {
                        String syDate = record.getBusiInsuranceTime().substring(0, 7);
                        if (syDate.equals(format)) {
                            record.setSyxDealNum(1);
                        } else {
                            record.setSyxDealNum(0);
                        }
                    } else {
                        record.setSyxDealNum(0);
                    }
                    if (record.getBusiInsuranceTimeEnd() != null) {
                        String syend = record.getBusiInsuranceTimeEnd().substring(0, 7);
                        if (syend.equals(format)) {
                            record.setSyxNotDealNum(1);
                        } else {
                            record.setSyxNotDealNum(0);
                        }
                    } else {
                        record.setSyxNotDealNum(0);
                    }
                    if (record.getOtherInsuranceTime() != null) {
                        String other = record.getOtherInsuranceTime().substring(0, 7);
                        if (other.equals(format)) {
                            record.setOtherDealNum(1);
                        } else {
                            record.setOtherDealNum(0);
                        }
                    } else {
                        record.setOtherDealNum(0);
                    }
                    if (record.getOtherInsuranceTimeEnd() != null) {
                        String otherEND = record.getOtherInsuranceTimeEnd().substring(0, 7);
                        if (otherEND.equals(format)) {
                            record.setOtherNotDealNum(1);
                        } else {
                            record.setOtherNotDealNum(0);
                        }
                    } else {
                        record.setOtherNotDealNum(0);
                    }
                } else if (record.getLicenceType() == 2) {
                    if (record.getViolationTime() != null) {
                        String violationTime = record.getViolationTime();
                        String wzdate = violationTime.substring(0, 7);
                        if (wzdate.equals(dateFormat.format(date))) {
                            record.setWzDealNum(1);
                        } else {
                            record.setWzDealNum(0);
                        }
                    } else {
                        record.setWzDealNum(0);
                    }
                    if (record.getRenfaTime() != null) {
                        String renfaTime = record.getRenfaTime();
                        String rnefaDate = renfaTime.substring(0, 7);
                        if (rnefaDate != dateFormat.format(date)) {
                            record.setWzNotDealNum(0);
                        } else if (dateFormat.format(date).equals(rnefaDate)) {
                            record.setWzDealNum(1);
                        }
                    } else {
                        record.setWzNotDealNum(0);
                    }
                    if (record.getReportDate() != null) {
                        String reportDate = record.getReportDate();
                        String sgDate = reportDate.substring(0, 7);

                    }
                    if (record.getAccidentStatus()!=null){
                        if (record.getAccidentStatus() == 1 || record.getAccidentStatus() == 2){
                            record.setSgNotDealNum(1);
                        }else {
                            record.setSgDealNum(0);
                        }
                        if (record.getAccidentStatus() == 3){
                            record.setSgDealNum(1);
                        }else {
                            record.setSgNotDealNum(0);
                        }
                    }else {
                        record.setSgDealNum(0);
                        record.setSgNotDealNum(0);
                    }

                    if (record.getClaimDate() != null) {
                        String updateTime = record.getClaimDate();
                        String sgdate = updateTime.substring(0, 7);

                    }
                    if (record.getAnnualVeriTime() != null) {
                        String nsdate = record.getAnnualVeriTime().substring(0, 7);
                        if (format.equals(nsdate)) {
                            record.setNsDealNum(1);
                        } else {
                            record.setNsDealNum(0);
                        }
                    } else {
                        record.setNsDealNum(0);
                    }
                    if (record.getAnnualVeriTimeEnd() != null) {
                        String nsend = record.getAnnualVeriTimeEnd().substring(0, 7);
                        if (nsend.equals(format)) {
                            record.setNsNotDealNum(1);
                        } else {
                            record.setNsNotDealNum(0);
                        }
                    }
                    if (record.getInsuranceTime() != null) {
                        String jqdate = record.getInsuranceTime().substring(0, 7);
                        if (jqdate.equals(format)) {
                            record.setJxxDealNum(1);
                        } else {
                            record.setJxxDealNum(0);
                        }
                    } else {
                        record.setNsNotDealNum(0);
                    }
                    if (record.getInsuranceTimeEnd() != null) {
                        String jqend = record.getInsuranceTimeEnd().substring(0, 7);
                        if (jqend.equals(format)) {
                            record.setJxxNotDealNum(1);
                        } else {
                            record.setJxxNotDealNum(0);
                        }
                    } else {
                        record.setJxxNotDealNum(0);
                    }
                    if (record.getBusiInsuranceTime() != null) {
                        String syDate = record.getBusiInsuranceTime().substring(0, 7);
                        if (syDate.equals(format)) {
                            record.setSyxDealNum(1);
                        } else {
                            record.setSyxDealNum(0);
                        }
                    } else {
                        record.setSyxDealNum(0);
                    }
                    if (record.getBusiInsuranceTimeEnd() != null) {
                        String syend = record.getBusiInsuranceTimeEnd().substring(0, 7);
                        if (syend.equals(format)) {
                            record.setSgNotDealNum(1);
                        } else {
                            record.setSgNotDealNum(0);
                        }
                    } else {
                        record.setSgNotDealNum(0);
                    }
                    if (record.getOtherInsuranceTime() != null) {
                        String other = record.getOtherInsuranceTime().substring(0, 7);
                        if (other.equals(format)) {
                            record.setOtherDealNum(1);
                        } else {
                            record.setOtherDealNum(0);
                        }
                    } else {
                        record.setOtherDealNum(0);
                    }
                    if (record.getOtherInsuranceTimeEnd() != null) {
                        String otherEND = record.getOtherInsuranceTimeEnd().substring(0, 7);
                        if (otherEND.equals(format)) {
                            record.setOtherNotDealNum(1);
                        } else {
                            record.setOtherNotDealNum(0);
                        }
                    } else {
                        record.setOtherNotDealNum(0);
                    }
                }
            }
        }
        List<ZzxqDto> zzxqDtos = new ArrayList<>();
        Map<Integer, Map<String, List<ZzxqDto>>> userMap = licenseDtoPage.stream()
                .collect(Collectors.groupingBy(ZzxqDto::getLicenceType, Collectors.groupingBy(ZzxqDto::getBrandModel)));
        userMap.forEach((key1, map) -> {

            map.forEach((key2, user) ->
            {
                IntSummaryStatistics ageStatistics = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getNsDealNum));
                Long sum = ageStatistics.getSum();
                IntSummaryStatistics ageStatistics1 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getNsNotDealNum));
                Long sum1 = ageStatistics1.getSum();
                IntSummaryStatistics ageStatistics2 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getSyxDealNum));
                Long sum2 = ageStatistics2.getSum();
                IntSummaryStatistics ageStatistics3 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getSyxNotDealNum));
                Long sum3 = ageStatistics3.getSum();
                IntSummaryStatistics ageStatistics4 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getSgDealNum));
                Long sum4 = ageStatistics4.getSum();
                IntSummaryStatistics ageStatistics5 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getSgNotDealNum));
                Long sum5 = ageStatistics5.getSum();
                IntSummaryStatistics ageStatistics6 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getJxxDealNum));
                Long sum6 = ageStatistics6.getSum();
                IntSummaryStatistics ageStatistics7 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getJxxNotDealNum));
                Long sum7 = ageStatistics7.getSum();
                IntSummaryStatistics ageStatistics8 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getWzDealNum));
                Long sum8 = ageStatistics8.getSum();
                IntSummaryStatistics ageStatistics9 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getWzNotDealNum));
                Long sum9 = ageStatistics9.getSum();
                IntSummaryStatistics ageStatistics10 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getOtherDealNum));
                Long sum10 = ageStatistics10.getSum();
                IntSummaryStatistics ageStatistics11 = user.stream().collect(Collectors.summarizingInt(ZzxqDto::getOtherNotDealNum));
                Long sum11 = ageStatistics11.getSum();
                ZzxqDto zzxqDto1 = new ZzxqDto();
                for (ZzxqDto zzxqDto : user) {
                    zzxqDto1.setBrandModel(key2);
                    zzxqDto1.setLicenceType(key1);
                    zzxqDto1.setCreateDate(zzxqDto.getCreateDate());
                    zzxqDto1.setNsDealNum(sum.intValue());
                    zzxqDto1.setNsNotDealNum(sum1.intValue());
                    zzxqDto1.setSyxDealNum(sum2.intValue());
                    zzxqDto1.setSyxNotDealNum(sum3.intValue());
                    zzxqDto1.setSgDealNum(sum4.intValue());
                    zzxqDto1.setSgNotDealNum(sum5.intValue());
                    zzxqDto1.setJxxDealNum(sum6.intValue());
                    zzxqDto1.setJxxNotDealNum(sum7.intValue());
                    zzxqDto1.setWzDealNum(sum8.intValue());
                    zzxqDto1.setWzNotDealNum(sum9.intValue());
                    zzxqDto1.setOtherDealNum(sum10.intValue());
                    zzxqDto1.setOtherNotDealNum(sum11.intValue());
                    zzxqDtos.add(zzxqDto);
                }
            });
        });
        return zzxqDtos;
    }

    @Async
    @Override
    public void queryAllFindByAllListExport(String accessToken, ImportOrExportRecords record) {
        SysUser sysUser = getSysUser(accessToken);
        List<ZzxqDto> zzxqDtos = baseMapper.queryAllFindByAll(sysUser.getTenantId());
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"类型一", "车辆品牌", "车辆型号", "年审已处理", "年审未处理", "商业险已处理", "商业险未处理", "交强险已处理", "交强险未处理", "违章已处理", "违章未处理", "事故已处理", "事故未处理", "其他险已处理", "其他险未处理"};
            resourceFild = new String[]{"getLicenceType", "getBrandModel", "getVehicleModel", "getNsDealNum", "getNsNotDealNum", "getSyxDealNum", "getSyxNotDealNum", "getJxxDealNum", "getJxxNotDealNum", "getWzDealNum", "getWzNotDealNum", "getSgDealNum", "getSgNotDealNum",
                    "getOtherDealNum", "getOtherNotDealNum"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(zzxqDtos, showName, resourceFild, ZzxqDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "证照详情.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Override
    public List<LicenseVo> queryAll(String accessToken, LicenseDto licenseDto) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<LicenseVo> licenseVoPage = baseMapper.queryAll(loginInfo.getTenantId(), licenseDto);
        if (licenseVoPage != null) {
            Calendar now = Calendar.getInstance();
            int i = now.get(Calendar.YEAR);

            int i1 = now.get(Calendar.MONTH) + 1;
            if (licenseVoPage != null && licenseVoPage.size() > 0) {
                for (LicenseVo record : licenseVoPage) {
                    String effectiveDate = record.getEffectiveDate();
                    if (effectiveDate != null) {
                        int substring = Integer.parseInt(effectiveDate.substring(0, 4));
                        int substring1 = Integer.parseInt(effectiveDate.substring(5, 7));
                        if ((i - substring) > 0) {
                            record.setLeftDate("已到期");
                        } else if ((i - substring) == 0) {
                            if (i1 - substring1 == 0) {
                                record.setLeftDate("本月到期");
                            } else if (i1 - substring1 == -1) {
                                record.setLeftDate("一个月");
                            } else if (i1 - substring1 == -2) {
                                record.setLeftDate("二个月");
                            } else if (i1 - substring1 == -3) {
                                record.setLeftDate("三个月");
                            } else {
                                record.setLeftDate("三个月以后");
                            }
                        }
                    }
                }
            }
        }
        return licenseVoPage;
    }

    @Async
    @Override
    public void queryAlllist(String accessToken, ImportOrExportRecords record, LicenseDto licenseDto) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        ;
        List<LicenseVo> list = baseMapper.queryAll(loginInfo.getTenantId(), licenseDto);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"车牌号码", "牌照类型", "车长", "车辆的年审日期", "车辆年审有效期截止日期", "车辆年审费用", "用户名", "过期"};
            resourceFild = new String[]{"getPlateNumber", "getLicenceType", "getVehicleLength", "getAnnualreviewData", "getEffectiveDate", "getAnnualreviewCost", "getName", "getLeftDate"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, LicenseVo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "证照到期.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.saveRecords(record, accessToken);
            e.printStackTrace();
        }
    }

    /**
     * 根据证照类型查询证照到期列表
     * @author zag
     * @date 2022/5/3 17:17
     * @param page
     * @param licenseDto
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.record.vo.LicenseVo>
     */
    @Override
    public Page<LicenseVo> selectListByType(Page<LicenseVo> page, String accessToken, LicenseDto licenseDto) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<LicenseVo> licenseVoPage = baseMapper.selectListByType(page, loginInfo.getTenantId(), licenseDto);
        if(licenseVoPage!=null && licenseVoPage.getTotal()>0){
            modifyList(licenseVoPage.getRecords());
        }
        return licenseVoPage;
    }

    private void modifyList(List<LicenseVo> list){
        List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
        List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
        List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
        for (LicenseVo record:list){
            if (record.getVehicleStatus() != null && record.getVehicleStatus() > 0) {

                Integer vehicleStatus1 = record.getVehicleStatus();
                for (SysStaticData sysStaticData : vehicleStatusList) {
                    if (sysStaticData.getId().equals(Long.valueOf(vehicleStatus1))) {
                        record.setVehicleStatusName(sysStaticData.getCodeName());
                        break;
                    }
                }
            }
            try {
                if (StringUtils.isNotBlank(record.getVehicleLength()) && !"-1".equals(record.getVehicleLength())) {
                    String vehicleLength1 = record.getVehicleLength();
                    if (record.getVehicleStatus() != null && record.getVehicleStatus() == 8) {

                        for (SysStaticData sysStaticData : vehicleStatusSubtypeList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                record.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    } else {

                        for (SysStaticData sysStaticData : vehicleLengthList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                record.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (record.getVehicleStatus() != null) {
                if (record.getVehicleStatus() > 0
                        && StringUtils.isNotBlank(record.getVehicleLength())
                        && !record.getVehicleLength().equals("-1")) {
                    record.setVehicleInfo(record.getVehicleStatusName() + "/" + record.getVehicleLengthName());
                } else if (record.getVehicleStatus() > 0 &&
                        (record.getVehicleLength() != null &&
                                (StringUtils.isNotBlank(record.getVehicleLength()) || !record.getVehicleLength().equals("-1")))) {
                    record.setVehicleInfo(record.getVehicleStatusName());
                } else {
                    record.setVehicleInfo(record.getVehicleLengthName());
                }
            }
        }
    }

    /**
     * 导出证照到期
     * @author zag
     * @date 2022/5/3 17:17
     * @param accessToken
     * @param record
     * @param licenseDto
     * @return void
     */
    @Async
    @Override
    public void exportList(String accessToken, ImportOrExportRecords record, LicenseDto licenseDto){
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<LicenseVo> list = baseMapper.selectListByType(loginInfo.getTenantId(), licenseDto);
        modifyList(list);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"车牌号码", "牌照类型", "车型", "证照类型","开始时间", "到期时间", "费用金额", "即将到期"};
            resourceFild = new String[]{"getPlateNumber", "getLicenceTypeName", "getVehicleInfo", "getAnnualreviewTypeName", "getAnnualreviewData","getEffectiveDate", "getAnnualreviewCost", "getLeftDate"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, LicenseVo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "证照到期.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    /**
     * 查询证照详情
     * @author zag
     * @date 2022/5/4 19:08
     * @param accessToken
     * @return java.util.List<com.youming.youche.record.vo.LicenseDetailsVo>
     */
    @Override
    public List<LicenseDetailsVo> selectLicenseDetails(String accessToken){
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<LicenseDetailsDto> list= baseMapper.selectLicenseDetails(loginInfo.getTenantId());
        return getLicenseDetails(list);
    }

    private List<LicenseDetailsVo> getLicenseDetails(List<LicenseDetailsDto> list){
        List<LicenseDetailsVo> newList=new ArrayList<>();
        Map<String,LicenseDetailsVo> map=new HashMap<>();
        for (LicenseDetailsDto dto:list){
            String key=dto.getLicenceType()+","+dto.getBrandModel();
            LicenseDetailsVo vo=null;
            if(!map.containsKey(key)){
                vo=new LicenseDetailsVo();
                vo.setLicenceType(dto.getLicenceType());
                vo.setLicenceTypeName(dto.getLicenceTypeName());
                vo.setBrandModel(dto.getBrandModel());
                map.put(key,vo);
            }else {
                vo = map.get(key);
            }
            if(dto.getBusinessType()==null){
                continue;
            }
            switch (dto.getBusinessType()){
                case 1:
                    vo.setNsOneDealNum(dto.getProcessed());
                    vo.setNsOneNotDealNum(dto.getNotProcessed());
                    break;
                case 2:
                    vo.setNsTwoDealNum(dto.getProcessed());
                    vo.setNsTwoNotDealNum(dto.getNotProcessed());
                    break;
                case 3:
                    vo.setJqxDealNum(dto.getProcessed());
                    vo.setJqxNotDealNum(dto.getNotProcessed());
                    break;
                case 4:
                    vo.setSyxDealNum(dto.getProcessed());
                    vo.setSyxNotDealNum(dto.getNotProcessed());
                    break;
                case 5:
                    vo.setOtherDealNum(dto.getProcessed());
                    vo.setOtherNotDealNum(dto.getNotProcessed());
                    break;
                case 6:
                    vo.setWzDealNum(dto.getProcessed());
                    vo.setWzNotDealNum(dto.getNotProcessed());
                    break;
                case 7:
                    vo.setSgDealNum(dto.getProcessed());
                    vo.setSgNotDealNum(dto.getNotProcessed());
                    break;
            }
        }
        for (Map.Entry entry:map.entrySet()){
            newList.add((LicenseDetailsVo)entry.getValue());
        }
        return newList;
    }

    /**
     * 导出证照详情
     * @author zag
     * @date 2022/5/5 20:02
     * @param accessToken
     * @param record
     * @return void
     */
    @Async
    @Override
    public void exportDetails(String accessToken,ImportOrExportRecords record){
        SysUser sysUser = getSysUser(accessToken);
        List<LicenseDetailsDto> list = baseMapper.selectLicenseDetails(sysUser.getTenantId());
        List<LicenseDetailsVo> newList=getLicenseDetails(list);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"牌照类型", "车辆品牌", "行驶证年审（已处理）", "行驶证年审（未处理）", "营运证年审（已处理）", "营运证年审（未处理）",
                    "商业险（已处理）", "商业险（未处理）","交强险（已处理）", "交强险（未处理）","违章（已处理）", "违章（未处理）",
                    "事故（已处理）", "事故（未处理）","其他险（已处理）", "其他险（未处理）"};
            resourceFild = new String[]{"getLicenceTypeName", "getBrandModel","getNsOneDealNum", "getNsOneNotDealNum", "getNsTwoDealNum","getNsTwoNotDealNum",
                    "getSyxDealNum","getSyxNotDealNum","getJqxDealNum","getJqxNotDealNum","getWzDealNum","getWzNotDealNum",
                    "getSgDealNum","getSgNotDealNum","getOtherDealNum","getOtherNotDealNum",};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(newList, showName, resourceFild, LicenseDetailsVo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "证照详情.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }


    private SysUser getSysUser(String accessToken) {
        SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
        return sysUser;
    }
}
