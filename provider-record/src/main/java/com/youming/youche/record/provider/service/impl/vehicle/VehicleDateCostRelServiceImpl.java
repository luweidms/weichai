package com.youming.youche.record.provider.service.impl.vehicle;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.trailer.IVehicleDateCostRelService;
import com.youming.youche.record.domain.vehicle.VehicleDateCostRel;
import com.youming.youche.record.dto.trailer.DateCostDto;
import com.youming.youche.record.dto.trailer.TrailerGuaCarDto;
import com.youming.youche.record.dto.trailer.TrailerGuaPeiZhiDto;
import com.youming.youche.record.dto.trailer.VehicleDateCostRelDto;
import com.youming.youche.record.dto.trailer.ZcVehicleTrailerDto;
import com.youming.youche.record.provider.mapper.vehicle.VehicleDateCostRelMapper;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.PKUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


@DubboService(version = "1.0.0")
public class VehicleDateCostRelServiceImpl extends BaseServiceImpl<VehicleDateCostRelMapper, VehicleDateCostRel> implements IVehicleDateCostRelService {

    @Resource
    VehicleDateCostRelMapper vehicleDateCostRelMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    private ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public List<VehicleDateCostRelDto> queryZcjz(String accessToken) throws ParseException {
        baseMapper.deleteAll();//清楚表中数据重新添加
        LoginInfo loginInfo = loginUtils.get(accessToken);
        int monthCount = 0;
        List<VehicleDateCostRelDto> list = vehicleDateCostRelMapper.queryAll(loginInfo.getTenantId());
        for (VehicleDateCostRelDto vehicleDateCostRelDto : list) {
            if (vehicleDateCostRelDto.getLicenceType() == 1) {
                Date purchaseDate = vehicleDateCostRelDto.getPurchaseDate();
                if (purchaseDate != null) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String format = df.format(new Date());
                    Date parse = df.parse(format);
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    start.setTime(purchaseDate);
                    end.setTime(parse);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    monthCount = result + month;// 当前时间减去购买时间 得出的月数
                    // 购买原价 -折旧（(原价-残值)/折旧月数）
                    if (vehicleDateCostRelDto.getPrice() != null) {
                        Long yj = vehicleDateCostRelDto.getPrice() / 100;  //原价
                        if (vehicleDateCostRelDto.getResidual() != null) {
                            Long cz = vehicleDateCostRelDto.getResidual() / 100;//残值
                            if (vehicleDateCostRelDto.getDepreciatedMonth() != null) {
                                if (vehicleDateCostRelDto.getDepreciatedMonth() != 0) {
                                    Long yzjz = (yj - cz) / vehicleDateCostRelDto.getDepreciatedMonth();//月折旧期数
                                    int syzjqs = vehicleDateCostRelDto.getDepreciatedMonth() - monthCount; //剩余折旧期数
                                    // 净值
                                    Long jz = (yj - (yzjz * monthCount));
                                    vehicleDateCostRelDto.setZcjz(jz);
                                    vehicleDateCostRelDto.setSyzjqs(syzjqs);
                                    vehicleDateCostRelDto.setZcdyzj(yzjz);
                                }

                            }

                        }

                    }

                }
                VehicleDateCostRel vehicleDateCostRel = new VehicleDateCostRel();
                vehicleDateCostRel.setLicenceType(vehicleDateCostRelDto.getLicenceType());
                vehicleDateCostRel.setBrandModel(vehicleDateCostRelDto.getBrandModel());
                vehicleDateCostRel.setSyzjqs(vehicleDateCostRelDto.getSyzjqs());
                vehicleDateCostRel.setZcdyzj(vehicleDateCostRelDto.getZcdyzj());
                vehicleDateCostRel.setZcjz(vehicleDateCostRelDto.getZcjz());
                vehicleDateCostRel.setStates("正常");
                vehicleDateCostRel.setXingzhi("自有");
                vehicleDateCostRel.setPlateNumber(vehicleDateCostRelDto.getPlateNumber());
                baseMapper.insertS(vehicleDateCostRel);
            } else if (vehicleDateCostRelDto.getLicenceType() == 2) {
                Date purchaseDate1 = vehicleDateCostRelDto.getPurchaseDate();
                if (purchaseDate1 != null) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String format = df.format(new Date());
                    Date parse = df.parse(format);
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    start.setTime(purchaseDate1);
                    end.setTime(parse);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    monthCount = result + month;// 当前时间减去购买时间 得出的月数
                    // 购买原价 -折旧（(原价-残值)/折旧月数）
                    if (vehicleDateCostRelDto.getPrice() != null) {
                        Long yj = vehicleDateCostRelDto.getPrice() / 100;  //原价
                        if (vehicleDateCostRelDto.getResidual() != null) {
                            Long cz = vehicleDateCostRelDto.getResidual() / 100;//残值
                            if (vehicleDateCostRelDto.getDepreciatedMonth() != null) {
                                if (vehicleDateCostRelDto.getDepreciatedMonth() != 0) {
                                    Long yzjz = (yj - cz) / vehicleDateCostRelDto.getDepreciatedMonth();//月折旧期数
                                    int syzjqs = vehicleDateCostRelDto.getDepreciatedMonth() - monthCount; //剩余折旧期数
                                    // 净值
                                    Long jz = (yj - (yzjz * monthCount));
                                    vehicleDateCostRelDto.setZcjz(jz);
                                    vehicleDateCostRelDto.setSyzjqs(syzjqs);
                                    vehicleDateCostRelDto.setZcdyzj(yzjz);
                                }
                            }

                        }

                    }
                }
                VehicleDateCostRel vehicleDateCostRel = new VehicleDateCostRel();
                vehicleDateCostRel.setLicenceType(vehicleDateCostRelDto.getLicenceType());
                vehicleDateCostRel.setBrandModel(vehicleDateCostRelDto.getBrandModel());
                vehicleDateCostRel.setSyzjqs(vehicleDateCostRelDto.getSyzjqs());
                vehicleDateCostRel.setZcdyzj(vehicleDateCostRelDto.getZcdyzj());
                vehicleDateCostRel.setStates("正常");
                vehicleDateCostRel.setXingzhi("自有");
                vehicleDateCostRel.setZcjz(vehicleDateCostRelDto.getZcjz());
                vehicleDateCostRel.setPlateNumber(vehicleDateCostRelDto.getPlateNumber());
                baseMapper.insertSs(vehicleDateCostRel);
            }
        }

        return list;
    }


    public List<DateCostDto> queryByType() {
        List<DateCostDto> dateCostDtos = baseMapper.queryByType();
        List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
        List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
        List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
        for (DateCostDto dateCostDto : dateCostDtos) {
            dateCostDto.setVehicleStatusName("");
            dateCostDto.setVehicleLengthName("");
            if (dateCostDto.getVehicleStatus() != null && dateCostDto.getVehicleStatus() > 0) {

                Integer vehicleStatus1 = dateCostDto.getVehicleStatus();
                for (SysStaticData sysStaticData : vehicleStatusList) {
                    if (sysStaticData.getId().equals(Long.valueOf(vehicleStatus1))) {
                        dateCostDto.setVehicleStatusName(sysStaticData.getCodeName());
                        break;
                    }
                }
            }
            try {
                if (StringUtils.isNotBlank(dateCostDto.getVehicleLength()) && !"-1".equals(dateCostDto.getVehicleLength())) {
                    String vehicleLength1 = dateCostDto.getVehicleLength();
                    if (dateCostDto.getVehicleStatus() != null && dateCostDto.getVehicleStatus() == 8) {

                        for (SysStaticData sysStaticData : vehicleStatusSubtypeList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                dateCostDto.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    } else {

                        for (SysStaticData sysStaticData : vehicleLengthList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                dateCostDto.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dateCostDto.getVehicleStatus() != null) {
                if (dateCostDto.getVehicleStatus() > 0
                        && StringUtils.isNotBlank(dateCostDto.getVehicleLength())
                        && !dateCostDto.getVehicleLength().equals("-1")) {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleStatusName() + "/" + dateCostDto.getVehicleLengthName());
                } else if (dateCostDto.getVehicleStatus() > 0 &&
                        (dateCostDto.getVehicleLength() != null &&
                                (StringUtils.isNotBlank(dateCostDto.getVehicleLength()) || !dateCostDto.getVehicleLength().equals("-1")))) {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleStatusName());
                } else {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleLengthName());
                }
            }
        }
        countVehicleName(dateCostDtos);
        return dateCostDtos;
    }

    /**
     * 获取车辆类型
     * */
    private void countVehicleName(List<DateCostDto> dateCostDtos) {
        List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
        for (DateCostDto dateCostDto : dateCostDtos) {
            Integer vehicleStatus1 = dateCostDto.getVehicleStatus();
            if (vehicleStatus1 == null) {
                continue;
            }
            for (SysStaticData sysStaticData : vehicleStatusList) {
                if (sysStaticData.getId().equals(Long.valueOf(vehicleStatus1))) {
                    dateCostDto.setVehicleStatusName(sysStaticData.getCodeName());
                    break;
                }
            }
        }
    }


    public List<DateCostDto> queryByTyperTwo() {
        List<DateCostDto> dateCostDtos = baseMapper.queryByTyperTwo();
        List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
        List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
        List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
        for (DateCostDto dateCostDto : dateCostDtos) {
            dateCostDto.setVehicleStatusName("");
            dateCostDto.setVehicleLengthName("");
            if (dateCostDto.getVehicleStatus() != null && dateCostDto.getVehicleStatus() > 0) {

                Integer vehicleStatus1 = dateCostDto.getVehicleStatus();
                for (SysStaticData sysStaticData : vehicleStatusList) {
                    if (sysStaticData.getId().equals(Long.valueOf(vehicleStatus1))) {
                        dateCostDto.setVehicleStatusName(sysStaticData.getCodeName());
                        break;
                    }
                }
            }
            try {
                if (StringUtils.isNotBlank(dateCostDto.getVehicleLength()) && !"-1".equals(dateCostDto.getVehicleLength())) {
                    String vehicleLength1 = dateCostDto.getVehicleLength();
                    if (dateCostDto.getVehicleStatus() != null && dateCostDto.getVehicleStatus() == 8) {

                        for (SysStaticData sysStaticData : vehicleStatusSubtypeList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                dateCostDto.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    } else {

                        for (SysStaticData sysStaticData : vehicleLengthList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                dateCostDto.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dateCostDto.getVehicleStatus() != null) {
                if (dateCostDto.getVehicleStatus() > 0
                        && StringUtils.isNotBlank(dateCostDto.getVehicleLength())
                        && !dateCostDto.getVehicleLength().equals("-1")) {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleStatusName() + "/" + dateCostDto.getVehicleLengthName());
                } else if (dateCostDto.getVehicleStatus() > 0 &&
                        (dateCostDto.getVehicleLength() != null &&
                                (StringUtils.isNotBlank(dateCostDto.getVehicleLength()) || !dateCostDto.getVehicleLength().equals("-1")))) {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleStatusName());
                } else {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleLengthName());
                }
            }
        }
        countVehicleName(dateCostDtos);
        return dateCostDtos;
    }

    @Override
    public List<TrailerGuaCarDto> queryGua(String accessToken) throws ParseException {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        int monthCount = 0;
        List<TrailerGuaCarDto> list = baseMapper.queryGua(loginInfo.getTenantId());
        for (TrailerGuaCarDto guaCarDto : list) {
            Date purchaseDate = guaCarDto.getPurchaseDate();
            if (purchaseDate != null) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String format = df.format(new Date());
                Date parse = df.parse(format);
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(purchaseDate);
                end.setTime(parse);
                int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                monthCount = result + month;// 当前时间减去购买时间 得出的月数
                // 购买原价 -折旧（(原价-残值)/折旧月数）
                if (guaCarDto.getPrice() != null) {
                    Long yj = guaCarDto.getPrice() / 100;  //原价
                    if (guaCarDto.getResidual() != null) {
                        Long cz = guaCarDto.getResidual() / 100;//残值
                        if (guaCarDto.getDepreciatedMonth() != null) {
                            if (guaCarDto.getDepreciatedMonth() != 0) {
                                Long yzjz = (yj - cz) / guaCarDto.getDepreciatedMonth();//月折旧期数
                                int syzjqs = guaCarDto.getDepreciatedMonth() - monthCount; //剩余折旧期数
                                // 净值
                                Long jz = (yj - (yzjz * monthCount));
                                guaCarDto.setZcjz(jz);
                                guaCarDto.setSyzjqs(syzjqs);
                                guaCarDto.setZcdyzj(yzjz);
                            }
                        }

                    }

                }

            }
            VehicleDateCostRel vehicleDateCostRel = new VehicleDateCostRel();
            vehicleDateCostRel.setLicenceType(3);
            vehicleDateCostRel.setBrandModel("-");
            vehicleDateCostRel.setSyzjqs(guaCarDto.getSyzjqs());
            vehicleDateCostRel.setZcdyzj(guaCarDto.getZcdyzj());
            vehicleDateCostRel.setZcjz(guaCarDto.getZcjz());
            vehicleDateCostRel.setStates("正常");
            vehicleDateCostRel.setXingzhi("自有");
            vehicleDateCostRel.setTrailerNumber(guaCarDto.getTrailerNumber());
            baseMapper.insertSss(vehicleDateCostRel);
        }
        return list;
    }

    public List<TrailerGuaPeiZhiDto> queryAllDto() {
        return baseMapper.queryAllDto();
    }

    @Override
    public List<ZcVehicleTrailerDto> queryZcXq() {
        List<ZcVehicleTrailerDto> dtos = baseMapper.queryZcXq();
        if (dtos!=null){
            for (ZcVehicleTrailerDto zcVehicleTrailerDto : dtos) {
                Random r = new Random();
                for (int i = 0; i < 1; i++) {
                    zcVehicleTrailerDto.setId(PKUtil.getPK());
                }

            }
            dtos.get(0).setLicenceType(1);
            dtos.get(1).setLicenceType(2);
            dtos.get(2).setLicenceType(3);
        }
//        else {
//            dtos.get(0).setLicenceType(1);
//            dtos.get(1).setLicenceType(2);
//            dtos.get(2).setLicenceType(3);
//        }
        List<TrailerGuaPeiZhiDto> guaPeiZhiDtos = queryAllDto();
        List<DateCostDto> dateCostDtos = new ArrayList<>();
        List<SysStaticData> trailerMaterial = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("TRAILER_MATERIAL"));

        for (TrailerGuaPeiZhiDto guaPeiZhiDto : guaPeiZhiDtos) {

            DateCostDto dateCostDto = new DateCostDto();
            BeanUtils.copyProperties(guaPeiZhiDto, dateCostDto);
            dateCostDto.setPlateNumber(guaPeiZhiDto.getTrailerNumber());
            dateCostDto.setRj(guaPeiZhiDto.getTrailerVolume() / 100);
            if (guaPeiZhiDto.getTrailerMaterial() != null && guaPeiZhiDto.getTrailerMaterial() > 0) {
                Long vehicleStatus1 = Long.valueOf(guaPeiZhiDto.getTrailerMaterial());
                for (SysStaticData sysStaticData : trailerMaterial) {
                    if (sysStaticData.getCodeId().equals(vehicleStatus1)) {
                        dateCostDto.setCaizhi(sysStaticData.getCodeName());
                        break;
                    }
                }
            }
            dateCostDtos.add(dateCostDto);
        }

        List<DateCostDto> dtos1 = queryByTyperTwo();
        for (DateCostDto dateCostDto : dtos1) {
            Double rj = dateCostDto.getRj() / 100;
            if (rj != null && rj != 0) {
                dateCostDto.setRj(rj);
            }
        }
        List<DateCostDto> dtos2 = queryByType();
        for (DateCostDto dateCostDto : dtos2) {
            Double rj = dateCostDto.getRj() / 100;
            if (rj != null && rj != 0) {
                dateCostDto.setRj(rj);
            }
        }
        for (ZcVehicleTrailerDto dto : dtos) {
            if (dto.getLicenceType() != null) {
                if (dto.getLicenceType() == 1) {
                    dto.setChildren(dtos2);
                } else if (dto.getLicenceType() == 2) {
                    dto.setChildren(dtos1);
                } else {
                    dto.setChildren(dateCostDtos);
                }
            }
        }
        return dtos;
    }

    /**
     * 卖出资产详情
     */
    @Async
    @Override
    public void queryZcXqList(String accessToken, ImportOrExportRecords record) {
        List<ZcVehicleTrailerDto> quer =selectAssetDetails(accessToken);
        List<DateCostDto> dates = new ArrayList<>();
        for (ZcVehicleTrailerDto zcVehicleTrailerDto : quer) {
            DateCostDto dateCostDto = new DateCostDto();
            dateCostDto.setLicenceType(zcVehicleTrailerDto.getLicenceType());
            dateCostDto.setCount(zcVehicleTrailerDto.getCount());
            dateCostDto.setSyzjqsze(zcVehicleTrailerDto.getSyzjqsze());
            dateCostDto.setZcdyzjze(zcVehicleTrailerDto.getZcdyzjze());
            dateCostDto.setZcjzze(zcVehicleTrailerDto.getZcjzze());
            dates.add(dateCostDto);
            dates.addAll(zcVehicleTrailerDto.getChildren());
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"牌照类型", "车辆品牌", "资产名称", "车辆数量", "车辆状态", "车辆性质", "资产净值", "剩余折旧期数", "资产单月折旧", "车型", "品牌型号", "材质", "容积", "发动机号", "车架号"};
            resourceFild = new String[]{"getLicenceTypeName", "getBrandModel", "getPlateNumber", "getCount", "getStates", "getXingzhi", "getZcjzze", "getSyzjqsze", "getZcdyzjze", "getVehicleInfo", "getPpxh", "getCaizhi", "getRjName", "getEngineNo", "getVinNo"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(dates, showName, resourceFild, DateCostDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "资产详情.xlsx", inputStream.available());
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

    /**
     * 查询资产详情
     */
    @Override
    public List<ZcVehicleTrailerDto> selectAssetDetails(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<ZcVehicleTrailerDto> newList = new ArrayList<>();
        List<DateCostDto> list = baseMapper.selectAssetDetails(loginInfo.getTenantId());
        if (list != null && list.size() > 0) {
            modifyList(list);
        }
        //1:整车；2：拖头；3：挂车
        for (int i = 1; i <= 3; i++) {
            ZcVehicleTrailerDto info = new ZcVehicleTrailerDto();
            info.setId(PKUtil.getPK());
            info.setLicenceType(i);
            info.setZcjzze("0.00");
            info.setZcdyzjze("0.00");
            newList.add(info);
        }
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        List<DateCostDto> list1 = new ArrayList<>();
        List<DateCostDto> list2 = new ArrayList<>();
        List<DateCostDto> list3 = new ArrayList<>();
        for (DateCostDto dto : list) {

            ZcVehicleTrailerDto info = newList.get(dto.getLicenceType() - 1);

            //剩余折旧期数：总折旧月数-当前折旧月数
            Integer syzjqs = null;
            if (dto.getDepreciatedMonth() != null && dto.getDqzjys() != null) {
                syzjqs = dto.getDepreciatedMonth() - dto.getDqzjys();
                if (syzjqs < 0) {
                    syzjqs = 0;
                }
            }

            //已用折旧期数
            Integer yyzjqs = null;
            if (dto.getDepreciatedMonth() != null && syzjqs != null) {
                yyzjqs = dto.getDepreciatedMonth() - syzjqs;
            }

            //资产单月折旧：（总价 - 残值）/折旧月数
            Double zcdyzj = null;
            if (StringUtils.isNotBlank(dto.getPrice()) && StringUtils.isNotBlank(dto.getResidual()) && dto.getDepreciatedMonth() != null) {
                Long cz = Long.parseLong(dto.getPrice()) - Long.parseLong(dto.getResidual());//差值：（总价 - 残值）
                if (cz > 0 && dto.getDepreciatedMonth() > 0) {
                    zcdyzj = cz / (double)dto.getDepreciatedMonth();
                }
            }

            //资产净值：原价 - 折旧 = 原价 - (资产单月折旧 * 已用折旧期数)
            Double jz = null;
            if (StringUtils.isNotBlank(dto.getPrice()) && zcdyzj != null && yyzjqs != null) {
                Double zj = zcdyzj * yyzjqs;
                jz = Long.parseLong(dto.getPrice()) - zj;
            }

            if (jz != null) {
                dto.setZcjzze(df.format(jz / 100D));//资产净值
                info.setZcjzzeL(info.getZcjzzeL() + jz);
            }
            if (syzjqs != null) {
                dto.setSyzjqsze(syzjqs);//剩余折旧期数
                info.setSyzjqsze(info.getSyzjqsze() == null ? syzjqs : info.getSyzjqsze() + syzjqs);
            }
            if (zcdyzj != null) {
                dto.setZcdyzjze(df.format(zcdyzj / 100D));//资产单月折旧
                info.setZcdyzjzeL(info.getZcdyzjzeL() + zcdyzj);
            }

            newList.get(dto.getLicenceType() - 1).setCount(info.getCount() + 1);
            if (dto.getLicenceType() == 1) {
                list1.add(dto);
            } else if (dto.getLicenceType() == 2) {
                list2.add(dto);
            } else if (dto.getLicenceType() == 3) {
                list3.add(dto);
            }
        }
        for (ZcVehicleTrailerDto info : newList) {
            if (info.getLicenceType() == 1) {
                info.setChildren(list1);
            } else if (info.getLicenceType() == 2) {
                info.setChildren(list2);
            } else if (info.getLicenceType() == 3) {
                info.setChildren(list3);
            }
            if (info.getZcdyzjzeL() > 0) {
                info.setZcdyzjze(df.format(info.getZcdyzjzeL() / 100D));
            }
            if (info.getZcjzzeL() > 0) {
                info.setZcjzze(df.format(info.getZcjzzeL() / 100D));
            }
        }
        return newList;
    }

    /**
     * 修改列表
     */
    private void modifyList(List<DateCostDto> list){
        List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
        List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
        List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
        List<SysStaticData> trailerMaterial = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("TRAILER_MATERIAL"));

        for (DateCostDto dateCostDto : list) {
            dateCostDto.setVehicleStatusName("");
            dateCostDto.setVehicleLengthName("");
            if (dateCostDto.getVehicleStatus() != null && dateCostDto.getVehicleStatus() > 0) {

                Integer vehicleStatus1 = dateCostDto.getVehicleStatus();
                for (SysStaticData sysStaticData : vehicleStatusList) {
                    if (sysStaticData.getId().equals(Long.valueOf(vehicleStatus1))) {
                        dateCostDto.setVehicleStatusName(sysStaticData.getCodeName());
                        break;
                    }
                }
            }
            try {
                if (StringUtils.isNotBlank(dateCostDto.getVehicleLength()) && !"-1".equals(dateCostDto.getVehicleLength())) {
                    String vehicleLength1 = dateCostDto.getVehicleLength();
                    if (dateCostDto.getVehicleStatus() != null && dateCostDto.getVehicleStatus() == 8) {

                        for (SysStaticData sysStaticData : vehicleStatusSubtypeList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                dateCostDto.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    } else {

                        for (SysStaticData sysStaticData : vehicleLengthList) {
                            if (sysStaticData.getId().equals(Long.valueOf(vehicleLength1))) {
                                dateCostDto.setVehicleLengthName(sysStaticData.getCodeName());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dateCostDto.getVehicleStatus() != null) {
                if (dateCostDto.getVehicleStatus() > 0
                        && StringUtils.isNotBlank(dateCostDto.getVehicleLength())
                        && !dateCostDto.getVehicleLength().equals("-1")) {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleStatusName() + "/" + dateCostDto.getVehicleLengthName());
                } else if (dateCostDto.getVehicleStatus() > 0 &&
                        (dateCostDto.getVehicleLength() != null &&
                                (StringUtils.isNotBlank(dateCostDto.getVehicleLength()) || !dateCostDto.getVehicleLength().equals("-1")))) {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleStatusName());
                } else {
                    dateCostDto.setVehicleInfo(dateCostDto.getVehicleLengthName());
                }
            }

            if (StringUtils.isNotBlank(dateCostDto.getCaizhi()) && !"-1".equals(dateCostDto.getCaizhi())) {
                Long vehicleStatus1 = Long.valueOf(dateCostDto.getCaizhi());
                for (SysStaticData sysStaticData : trailerMaterial) {
                    if (sysStaticData.getCodeId().equals(vehicleStatus1)) {
                        dateCostDto.setCaizhi(sysStaticData.getCodeName());
                        break;
                    }
                }
            }

            if(dateCostDto.getRj() !=null){
                Double rj = dateCostDto.getRj() / 100;
                if (rj != 0) {
                    dateCostDto.setRj(rj);
                }
            }

            if(dateCostDto.getStates().equals("0")){
                dateCostDto.setStates("启用");
            }else {
                dateCostDto.setStates("闲置");
            }

        }
    }

}
