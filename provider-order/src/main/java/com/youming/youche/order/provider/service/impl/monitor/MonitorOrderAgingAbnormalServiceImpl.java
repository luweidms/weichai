package com.youming.youche.order.provider.service.impl.monitor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.tsp.ITspVehicleTrackService;
import com.youming.youche.cloud.vo.tsp.TspVehicleTrackVo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.monitor.*;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.common.GpsUtil;
import com.youming.youche.order.commons.MonitorEventsConst;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.monitor.*;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.dto.monitor.*;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingAbnormalMapper;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingArriveMapper;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingDependMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.vo.OrderAgingListInVo;
import com.youming.youche.order.vo.monitor.MonitorOrderAgingVo;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.VehicleCertInfoDto;
import com.youming.youche.record.vo.LicenseVo;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.system.vo.SysTenantVo;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.JsonHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_CITY;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_DISTRICT;

/**
 * 运营大屏
 *
 * @author hzx
 * @date 2022/3/9 10:48
 */
@DubboService(version = "1.0.0")
public class MonitorOrderAgingAbnormalServiceImpl extends BaseServiceImpl<MonitorOrderAgingAbnormalMapper, MonitorOrderAgingAbnormal> implements IMonitorOrderAgingAbnormalService {

    private final static Logger log = LoggerFactory.getLogger(MonitorOrderAgingAbnormalServiceImpl.class);

    //盘点数据到期提前预警天数
    private final static int EXPIRE_DAY = 45;

    @Resource
    MonitorOrderAgingAbnormalMapper monitorOrderAgingAbnormalMapper;

    @Resource
    MonitorOrderAgingDependMapper monitorOrderAgingDependMapper;

    @Resource
    MonitorOrderAgingArriveMapper monitorOrderAgingArriveMapper;

    @Resource
    IMonitorOrderAgingDependHService iMonitorOrderAgingDependHService;

    @Resource
    IMonitorOrderAgingAbnormalHService iMonitorOrderAgingAbnormalHService;

    @Resource
    IMonitorOrderAgingArriveHService iMonitorOrderAgingArriveHService;

    @Resource
    IMonitorOrderAgingDependService iMonitorOrderAgingDependService;

    @Resource
    IMonitorOrderAgingArriveService iMonitorOrderAgingArriveService;

    @Resource
    OrderInfoMapper orderInfoMapper;

    @Resource
    OrderInfoHMapper orderInfoHMapper;

    @Resource
    OrderSchedulerMapper orderSchedulerMapper;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService tenantVehicleRelService;

    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    IOrderSchedulerService iOrderSchedulerService;

    @Resource
    IOrderSchedulerHService iOrderSchedulerHService;

    @Resource
    IOrderGoodsService iOrderGoodsService;

    @Resource
    IOrderGoodsHService iOrderGoodsHService;

    @Resource
    IOrderInfoService orderInfoService;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;

    @Resource
    IOrderInfoExtService orderInfoExtService;

    @Lazy
    @Resource
    OrderDateUtil orderDateUtil;

    @DubboReference(version = "1.0.0")
    ITspVehicleTrackService tspVehicleTrackService;

    @Resource
    ITrailerManagementService iTrailerManagementService;

    @Resource
    IOrderTransitLineInfoService iOrderTransitLineInfoService;

    @Resource
    PlatformTransactionManager platformTransactionManager;

    @Resource
    IOrderAgingInfoService orderAgingInfoService;

    @Override
    public List<MonitorOrderAgingAbnormal> getMonitorOrderAgingAbnormal(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId) {
        LambdaQueryWrapper<MonitorOrderAgingAbnormal> queryWrapper = new LambdaQueryWrapper<>();

        if (orderId != null && orderId > 0) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getOrderId, orderId);
        }
        if (type != null && type > 0) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getType, type);
        }
        if (tenantId != null && tenantId > 0) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getTenantId, tenantId);
        }
        if (StringUtils.isNotBlank(plateNumber)) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getPlateNumber, plateNumber);
        }
        if (lineType != null && lineType > 0) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getLineType, lineType);
        }

        List<MonitorOrderAgingAbnormal> monitorOrderAgingAbnormals = getBaseMapper().selectList(queryWrapper);
        return monitorOrderAgingAbnormals;
    }

    @Override
    public MonitorOrderAgingDto queryMonitorOrderAgingCount(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        monitorOrderAgingVo.setBusiId(loginInfo.getTenantId());
        monitorOrderAgingVo.setIsTenant(true);

        Long busiId = monitorOrderAgingVo.getBusiId();
        Boolean isTenant = monitorOrderAgingVo.getIsTenant();
        Integer sourceRegion = monitorOrderAgingVo.getSourceRegion();
        Integer desRegion = monitorOrderAgingVo.getDesRegion();
        Long orgId = monitorOrderAgingVo.getOrgId();
        String plateNumber = monitorOrderAgingVo.getPlateNumber();

        Integer aging = 0; // 时效
        Integer depend = 0; // 晚靠台
        Integer stay = 0;
        Integer amble = 0;
        Integer estLast = 0;
        Integer late = 0; // 迟到

        if (isTenant) { // 车队
            // 时效
            OrderAgingListInVo orderAgingListInVo = new OrderAgingListInVo();
            orderAgingListInVo.setSourceRegion(sourceRegion);
            orderAgingListInVo.setDesRegion(desRegion);
            orderAgingListInVo.setPlateNumber(plateNumber);
//            orderAgingListInVo.setSelectType(1);
            orderAgingListInVo.setTodo(true);
            Page<OrderAgingInfoDto> orderAgingInfoDtoPage = orderAgingInfoService.queryOrderAgingList(orderAgingListInVo, accessToken, 9999, 1);
            if (orderAgingInfoDtoPage.getRecords() != null) {
                aging = orderAgingInfoDtoPage.getRecords().size();
            }

            List<MonitorOrderAgingAbnormal> abnormalsAmble = monitorOrderAgingAbnormalMapper.queryAgingAbnormalList("false", "false", null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE1, busiId, sourceRegion, desRegion, orgId, plateNumber);
            amble = abnormalsAmble == null ? 0 : abnormalsAmble.size();

            List<MonitorOrderAgingAbnormal> abnormalsStay = monitorOrderAgingAbnormalMapper.queryAgingAbnormalList("false", "false", null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE2, busiId, sourceRegion, desRegion, orgId, plateNumber);
            stay = abnormalsStay == null ? 0 : abnormalsStay.size();

//            List<MonitorOrderAgingDepend> depends = monitorOrderAgingDependMapper.queryAgingDependList("false", "false", null, null, busiId, sourceRegion, desRegion, orgId, plateNumber);
            List<Map> depends = monitorOrderAgingDependMapper.queryAgingDependListNew(busiId, plateNumber, orgId, sourceRegion, desRegion);
            depend = depends == null ? 0 : depends.size();

            List<MonitorOrderAgingArrive> agingArrives = monitorOrderAgingArriveMapper.queryAgingArriveList("false", "false", null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, busiId, sourceRegion, desRegion, orgId, plateNumber);
            estLast = agingArrives == null ? 0 : agingArrives.size();

//            List<MonitorOrderAgingArrive> agingArrives1 = monitorOrderAgingArriveMapper.queryAgingArriveList("false", "false", null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, busiId, sourceRegion, desRegion, orgId, plateNumber);
//            List<MonitorOrderAgingArrive> agingArrives2 = monitorOrderAgingArriveMapper.queryAgingArriveList("false", "false", null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, busiId, sourceRegion, desRegion, orgId, plateNumber);
            List<Map> lateMap = monitorOrderAgingArriveMapper.queryAgingArriveListNew(busiId, plateNumber, orgId, sourceRegion, desRegion);
            late = lateMap == null ? 0 : lateMap.size();
        } else {//订单
            String isHis = "false";
            String isOrderHis = "false";
            LambdaQueryWrapper<OrderInfo> orderInfoQueryWrapper = new LambdaQueryWrapper<>();
            orderInfoQueryWrapper.eq(OrderInfo::getOrderId, busiId);
            OrderInfo orderInfo = orderInfoMapper.selectList(orderInfoQueryWrapper).get(0);
            if (orderInfo == null) {
                LambdaQueryWrapper<OrderInfoH> orderInfoHQueryWrapper = new LambdaQueryWrapper<>();
                orderInfoHQueryWrapper.eq(OrderInfoH::getOrderId, busiId);
                OrderInfoH orderInfoH = orderInfoHMapper.selectList(orderInfoHQueryWrapper).get(0);
                if (orderInfoH == null) {
                    throw new BusinessException("未找到订单[" + busiId + "]信息");
                }
                isHis = "true";
                isOrderHis = "true";
            } else {
                if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.ARRIVED) {
                    isHis = "true";
                }
            }
            List<MonitorOrderAgingAbnormal> abnormalsAmble = monitorOrderAgingAbnormalMapper.queryAgingAbnormalList(isOrderHis, isHis, busiId, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE1, null, sourceRegion, desRegion, orgId, plateNumber);
            amble = abnormalsAmble == null ? 0 : abnormalsAmble.size();
            List<MonitorOrderAgingAbnormal> abnormalsStay = monitorOrderAgingAbnormalMapper.queryAgingAbnormalList(isOrderHis, isHis, busiId, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE2, null, sourceRegion, desRegion, orgId, plateNumber);
            stay = abnormalsStay == null ? 0 : abnormalsStay.size();
            List<MonitorOrderAgingDepend> depends = monitorOrderAgingDependMapper.queryAgingDependList(isOrderHis, isHis, busiId, null, null, sourceRegion, desRegion, orgId, plateNumber);
            depend = depends == null ? 0 : depends.size();
            List<MonitorOrderAgingArrive> agingArrives = monitorOrderAgingArriveMapper.queryAgingArriveList(isOrderHis, isHis, busiId, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null, sourceRegion, desRegion, orgId, plateNumber);
            estLast = agingArrives == null ? 0 : agingArrives.size();
            List<MonitorOrderAgingArrive> agingArrives1 = monitorOrderAgingArriveMapper.queryAgingArriveList(isOrderHis, isHis, busiId, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null, sourceRegion, desRegion, orgId, plateNumber);
            List<MonitorOrderAgingArrive> agingArrives2 = monitorOrderAgingArriveMapper.queryAgingArriveList(isOrderHis, isHis, busiId, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, null, sourceRegion, desRegion, orgId, plateNumber);
            late = (agingArrives1 == null ? 0 : agingArrives1.size()) + (agingArrives2 == null ? 0 : agingArrives2.size());
        }

        return new MonitorOrderAgingDto(aging, depend, stay, amble, estLast, late);
    }

    @Override
    public List<EventsStaticDataDto> getCheckData(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (monitorOrderAgingVo == null) monitorOrderAgingVo = new MonitorOrderAgingVo();

        List<EventsStaticDataDto> eventsDatas = new ArrayList<EventsStaticDataDto>();
        int count = 0;
        //盘点总数
        count = getVehicleCount(monitorOrderAgingVo.getVehicleClass(), monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getOrgId(), loginInfo.getTenantId());
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.CHECK_EVENTS.CHECK, "盘点", count));
        //空闲24小时以上的车
        count = monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.isSelfVehicleClass()
                ? this.getNoneOrderVehicleCount(loginInfo.getTenantId(), monitorOrderAgingVo.getOrgId(), monitorOrderAgingVo.getPlateNumber(), 24, 48) : 0;
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.CHECK_EVENTS.RETENTION24, "无单24h+", count));
        //空闲48小时以上的车
        count = monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.isSelfVehicleClass()
                ? this.getNoneOrderVehicleCount(loginInfo.getTenantId(), monitorOrderAgingVo.getOrgId(), monitorOrderAgingVo.getPlateNumber(), 48, 72) : 0;
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.CHECK_EVENTS.RETENTION48, "无单48h+", count));
        //空闲72小时以上的车
        count = monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.isSelfVehicleClass()
                ? this.getNoneOrderVehicleCount(loginInfo.getTenantId(), monitorOrderAgingVo.getOrgId(), monitorOrderAgingVo.getPlateNumber(), 72, -1) : 0;
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.CHECK_EVENTS.RETENTION72, "无单72h+", count));

        return eventsDatas;
    }

    @Override
    public List<EventsStaticDataDto> getExpireData(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if (monitorOrderAgingVo.getVehicleClass() == null) {
            monitorOrderAgingVo.setVehicleClass(0);
        }

//        Date expireDate = DateUtil.addDate(new Date(), EXPIRE_DAY);
        List<EventsStaticDataDto> eventsDatas = new ArrayList<EventsStaticDataDto>();

        int count1 = 0; // 行驶证审
        int count2 = 0; // 营运证审
        int count3 = 0; // 交强险
        int count4 = 0; // 商业险
        int count5 = 0; // 其他险

        /*
            需求改动
         */
        List<LicenseVo> licenseVos = monitorOrderAgingAbnormalMapper.queryMaturityDoc(tenantId, monitorOrderAgingVo.getPlateNumber());
        for (LicenseVo licenseVo : licenseVos) {
            if ("1".equals(licenseVo.getAnnualreviewType())) {
                count1 += 1;
            } else if ("2".equals(licenseVo.getAnnualreviewType())) {
                count2 += 1;
            } else if ("3".equals(licenseVo.getAnnualreviewType())) {
                count3 += 1;
            } else if ("4".equals(licenseVo.getAnnualreviewType())) {
                count4 += 1;
            } else if ("5".equals(licenseVo.getAnnualreviewType())) {
                count5 += 1;
            }
        }

//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() >= 1 && monitorOrderAgingVo.getVehicleClass() != 6) {
//            count += getValidityVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    monitorOrderAgingVo.getVehicleClass(), tenantId);
//        }
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() == 6) {
//            count += getValidityVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    6, tenantId);
//        }
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.EXPIRE_EVENTS.VEHICLEVALIDITY, "行驶证审", count1));

//        count = 0;
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() >= 1 && monitorOrderAgingVo.getVehicleClass() != 6) {
//            count += getInsuranceVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    monitorOrderAgingVo.getVehicleClass(), tenantId);
//        }
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() == 6) {
//            count += getInsuranceVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    6, tenantId);
//        }
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.EXPIRE_EVENTS.INSURANCE, "交强险", count3));

//        count = 0;
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() >= 1 && monitorOrderAgingVo.getVehicleClass() != 6) {
//            count += getBusiInsuranceVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    monitorOrderAgingVo.getVehicleClass(), tenantId);
//        }
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() == 6) {
//            count += getBusiInsuranceVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    6, tenantId);
//        }
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.EXPIRE_EVENTS.BUSI_INSURANCE, "商业险", count4));

//        count = 0;
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() >= 1 && monitorOrderAgingVo.getVehicleClass() != 6) {
//            count += getOtherInsuranceVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    monitorOrderAgingVo.getVehicleClass(), tenantId);
//        }
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() == 6) {
//            count += getOtherInsuranceVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    6, tenantId);
//        }
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.EXPIRE_EVENTS.OTHER_INSURANCE, "其他险", count5));

//        count = 0;
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() >= 1 && monitorOrderAgingVo.getVehicleClass() != 6) {
//            count += getOperateValidityVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    monitorOrderAgingVo.getVehicleClass(), tenantId);
//        }
//        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() == 6) {
//            count += getOperateValidityVehicleCount(expireDate, monitorOrderAgingVo.getPlateNumber(),
//                    6, tenantId);
//        }
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.EXPIRE_EVENTS.OPRATEVALIDITY, "营运证审", count2));

        int count = 0;
        if (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.getVehicleClass() == 1) {
            List list = getMaintainWarningVehicle(monitorOrderAgingVo.getOrgId(), monitorOrderAgingVo.getPlateNumber(), tenantId);
            count = list == null ? 0 : list.size();
        }
        eventsDatas.add(new EventsStaticDataDto(MonitorEventsConst.EXPIRE_EVENTS.MAINTAIN, "保养", count));

        return eventsDatas;
    }

    @Override
    public List<EventsVehicleDto> queryEventsVehicleOutNew(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        Integer agingType = monitorOrderAgingVo.getAgingType();
        Integer sourceRegion = monitorOrderAgingVo.getSourceRegion();
        Integer desRegion = monitorOrderAgingVo.getDesRegion();
        Long orgId = monitorOrderAgingVo.getOrgId();
        String plateNumber = monitorOrderAgingVo.getPlateNumber();

        List<Map> plateNumberList = new ArrayList<>();
        if (agingType != null && agingType > 0) {
            if (agingType == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE1) {
//                List<Map> list = monitorOrderAgingAbnormalMapper.queryAgingDependPlateNumberList(false, false, null, null, tenantId, sourceRegion, desRegion, orgId, plateNumber);
                List<Map> list = monitorOrderAgingDependMapper.queryAgingDependListNew(tenantId, plateNumber, orgId, sourceRegion, desRegion);
                if (list != null && list.size() > 0) {
                    plateNumberList.addAll(list);
                }
            } else if (agingType == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE2) {
                List<Map> list = monitorOrderAgingAbnormalMapper.queryAgingAbnormalPlateNumberList(false, false, null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE2, tenantId, sourceRegion, desRegion, orgId, plateNumber);
                if (list != null && list.size() > 0) {
                    plateNumberList.addAll(list);
                }
            } else if (agingType == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE3) {
                List<Map> list = monitorOrderAgingAbnormalMapper.queryAgingAbnormalPlateNumberList(false, false, null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE1, tenantId, sourceRegion, desRegion, orgId, plateNumber);
                if (list != null && list.size() > 0) {
                    plateNumberList.addAll(list);
                }
            } else if (agingType == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE4) {
                List<Map> list = monitorOrderAgingAbnormalMapper.queryAgingArrivePlateNumberList(false, false, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, tenantId, sourceRegion, desRegion, orgId, plateNumber);
                if (list != null && list.size() > 0) {
                    plateNumberList.addAll(list);
                }
            } else if (agingType == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE5) {
//                List<Map> list = monitorOrderAgingAbnormalMapper.queryAgingArrivePlateNumberList(false, false, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, tenantId, sourceRegion, desRegion, orgId, plateNumber);
//                List<Map> list1 = monitorOrderAgingAbnormalMapper.queryAgingArrivePlateNumberList(false, false, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, tenantId, sourceRegion, desRegion, orgId, plateNumber);
                List<Map> list = monitorOrderAgingArriveMapper.queryAgingArriveListNew(tenantId, plateNumber, orgId, sourceRegion, desRegion);
                if (list != null && list.size() > 0) {
                    plateNumberList.addAll(list);
                }
//                if (list1 != null && list1.size() > 0) {
//                    plateNumberList.addAll(list1);
//                }
            } else if (agingType == 6) {
                // 时效
                List<Map> list = new ArrayList<>();
                OrderAgingListInVo orderAgingListInVo = new OrderAgingListInVo();
                orderAgingListInVo.setSourceRegion(sourceRegion);
                orderAgingListInVo.setDesRegion(desRegion);
                orderAgingListInVo.setPlateNumber(plateNumber);
//                orderAgingListInVo.setSelectType(1);
                orderAgingListInVo.setTodo(true);
                Page<OrderAgingInfoDto> orderAgingInfoDtoPage = orderAgingInfoService.queryOrderAgingList(orderAgingListInVo, accessToken, 9999, 1);
                if (orderAgingInfoDtoPage.getRecords() != null) {
                    for (OrderAgingInfoDto record : orderAgingInfoDtoPage.getRecords()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("orderId", record.getOrderId());
                        map.put("plateNumber", null == record.getPlateNumber() ? "" : record.getPlateNumber());
                        list.add(map);
                    }
                }

                if (list != null && list.size() > 0) {
                    plateNumberList.addAll(list);
                }
            }
        } else {
            List<Map> list = monitorOrderAgingAbnormalMapper.queryAgingArrivePlateNumberList(false, false, null, null, tenantId, sourceRegion, desRegion, orgId, plateNumber);
            List<Map> list1 = monitorOrderAgingAbnormalMapper.queryAgingDependPlateNumberList(false, false, null, null, tenantId, sourceRegion, desRegion, orgId, plateNumber);
            List<Map> list2 = monitorOrderAgingAbnormalMapper.queryAgingAbnormalPlateNumberList(false, false, null, null, tenantId, sourceRegion, desRegion, orgId, plateNumber);
            if (list != null && list.size() > 0) {
                plateNumberList.addAll(list);
            }
            if (list1 != null && list1.size() > 0) {
                plateNumberList.addAll(list1);
            }
            if (list2 != null && list2.size() > 0) {
                plateNumberList.addAll(list2);
            }
        }

        List<Map> listOut = plateNumberList;
        if (listOut != null && listOut.size() > 0) {
            List newList = new ArrayList<>();
            Set set = new HashSet();
            for (int i = 0; i < listOut.size(); i++) {
                Map m = listOut.get(i);
                Map newMap = new ConcurrentHashMap();
                newMap.put("orderId", DataFormat.getLongKey(m, "orderId"));
                newMap.put("plateNumber", DataFormat.getStringKey(m, "plateNumber"));
                newMap.put("vehicleClass", DataFormat.getStringKey(m, "vehicleClass"));
                m.remove("orderId");
                m.remove("vehicleClass");
//                if (set.add(m)) {
                    newList.add(newMap);
//                }
            }
            plateNumberList.clear();
            plateNumberList.addAll(newList);
        }

        List<EventsVehicleDto> eventsVehicleDtos = new ArrayList<EventsVehicleDto>();
        if (plateNumberList != null && plateNumberList.size() > 0) {
            for (Map m : plateNumberList) {
                String plateNumberStr = DataFormat.getStringKey(m, "plateNumber");
                Long orderId = DataFormat.getLongKey(m, "orderId");
                Integer vehicleClass = DataFormat.getIntKey(m, "vehicleClass");

                OrderScheduler orderScheduler = null;
                LambdaQueryWrapper<OrderScheduler> orderSchedulerLambdaQueryWrapper = new LambdaQueryWrapper<>();
                orderSchedulerLambdaQueryWrapper.eq(OrderScheduler::getOrderId, orderId);
                List<OrderScheduler> orderSchedulerList = orderSchedulerMapper.selectList(orderSchedulerLambdaQueryWrapper);
                if (orderSchedulerList != null && orderSchedulerList.size() > 0) {
                    orderScheduler = orderSchedulerList.get(0);
                }

                EventsVehicleDto eventsVehicleDto = new EventsVehicleDto();
                eventsVehicleDto.setPlateNumber(plateNumberStr);
                eventsVehicleDto.setOrderId(orderId);
                eventsVehicleDto.setVehicleClass(vehicleClass);
                if (orderScheduler != null) {
                    Integer gpsType = queryVehicleGpsType(orderScheduler.getVehicleCode());
                    String nand = "";
                    String enad = "";
                    Map locationMap = this.setRealLocation(gpsType, orderScheduler, orderId);
                    if (StringUtils.isNotBlank(DataFormat.getStringKey(locationMap, "sourceNand"))
                            && StringUtils.isNotBlank(DataFormat.getStringKey(locationMap, "sourceEand"))) {
                        nand = DataFormat.getStringKey(locationMap, "sourceNand");
                        enad = DataFormat.getStringKey(locationMap, "sourceEand");
                        eventsVehicleDto.setCurrLatitude(Double.parseDouble(nand));
                        eventsVehicleDto.setCurrLongitude(Double.parseDouble(enad));
                    }
                }
                List<Object> monitorOrderAgings = new ArrayList<>();
                if (orderId != null && orderId > 0) {
                    monitorOrderAgings = queryMonitorOrderAgingList(orderId, agingType);
                }
                eventsVehicleDto.setMonitorOrderAgings(monitorOrderAgings);

                if (eventsVehicleDto.getVehicleClass() != 6) {
                    VehicleDataInfo vehicleData = iVehicleDataInfoService.getVehicleDataInfo(eventsVehicleDto.getPlateNumber());
                    if (vehicleData != null) {
                        eventsVehicleDto.setVinNo(vehicleData.getVinNo());
                    }
                }

                eventsVehicleDtos.add(eventsVehicleDto);
            }
        }

        return eventsVehicleDtos;
    }

    @Override
    public void getVehicleListByAgingExport(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        List<EventsVehicleDto> eventsVehicleDtos = monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.getVehicleClass() <= 1 ?
                this.queryEventsVehicleOutNew(monitorOrderAgingVo, accessToken) : new ArrayList<EventsVehicleDto>();
        String eventName = "";
        if (monitorOrderAgingVo.getAgingType() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE1) {
            eventName = "晚靠台";
        } else if (monitorOrderAgingVo.getAgingType() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE2) {
            eventName = "异常停留";
        } else if (monitorOrderAgingVo.getAgingType() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE3) {
            eventName = "堵车缓行";
        } else if (monitorOrderAgingVo.getAgingType() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE4) {
            eventName = "预计延迟";
        } else if (monitorOrderAgingVo.getAgingType() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE5) {
            eventName = "迟到";
        } else if (monitorOrderAgingVo.getAgingType() == 6) {
            eventName = "时效";
        }

        for (EventsVehicleDto eventsVehicleDto : eventsVehicleDtos) {
            eventsVehicleDto.setEventName(eventName);
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"车牌号", "业务类型", "订单号"};
            resourceFild = new String[]{"getPlateNumber", "getEventName", "getOrderId"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(eventsVehicleDtos, showName, resourceFild, EventsVehicleDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "异常车辆列表.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public List<EventsVehicleNewDto> getVehicleListMap(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<EventsVehicleNewDto> eventInfoList = this.getVehicleList(monitorOrderAgingVo, loginInfo.getTenantId());
        for (EventsVehicleNewDto eventsVehicleOut : eventInfoList) {

            if (eventsVehicleOut.getVehicleClass() != 6) {
                VehicleDataInfo vehicleData = iVehicleDataInfoService.getVehicleDataInfo(eventsVehicleOut.getPlateNumber());
                if (vehicleData != null) {
                    eventsVehicleOut.setVinNo(vehicleData.getVinNo());
                }
            }

            if (eventsVehicleOut.getOrderId() != null) {
                eventsVehicleOut.setOrderId(null);
            }

        }
        return eventInfoList;
    }


    @Override
    public void getVehicleListMapExport(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        List<EventsVehicleNewDto> vehicleListMap = getVehicleListMap(monitorOrderAgingVo, accessToken);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"车牌号", "业务类型", "订单号"};
            resourceFild = new String[]{"getPlateNumber", "getEventName", "getOrderId"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(vehicleListMap, showName, resourceFild, EventsVehicleNewDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "异常车辆列表.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public EventsInfoDto getEventsByVehicle(String eventCode, String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if (StringUtils.isBlank(plateNumber) || StringUtils.isBlank(eventCode)) {
            throw new BusinessException("车牌号或事件编码不能为空！");
        }
        EventsInfoDto eventsInfoOut = new EventsInfoDto();
        if (eventCode.startsWith("1")) {
            eventsInfoOut = getEventsByVehicle(plateNumber, tenantId);
            return eventsInfoOut;
        }

        EventOrderInfoDto eventOrderInfo = new EventOrderInfoDto();
        CheckInfoDto checkInfo = new CheckInfoDto();

        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(plateNumber);
        if (null != vehicleDataInfo) {
            eventOrderInfo.setVehicleCode(vehicleDataInfo.getId());
        }

        //获取车辆最后一单的订单信息
        int vehicleClass = plateNumber.indexOf("挂") >= 0 ? 6 : 0;
        Map<String, Object> map = getLastOrderInfo(vehicleClass, plateNumber, tenantId);
        boolean hasLastOrder = false;
        if (map != null) {
            try {
                BeanUtil.copyProperties(map, eventOrderInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            hasLastOrder = true;
        }
        if (vehicleClass != 6) {
            //查询车辆信息
            map = getVehicleInfo(plateNumber, tenantId);
            if (map != null) {
                try {
                    BeanUtil.copyProperties(map, eventOrderInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                eventOrderInfo.setCarDriverMan(DataFormat.getStringKey(map, "linkMan"));
                eventOrderInfo.setCarDriverPhone(DataFormat.getStringKey(map, "linkPhone"));
                eventOrderInfo.setCarDriverId(DataFormat.getLongKey(map, "userId"));
                if (!hasLastOrder && map.get("createDate") != null) {
                    Date date = null;
                    try {
                        date = DateUtil.formatStringToDate(DataFormat.getStringKey(map, "createDate"), "yyyy-MM-dd HH:mm:ss");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    eventOrderInfo.setCarDepartureDate(date);
                }
            }
        } else {
            eventOrderInfo.setTrailerNumber(plateNumber);
            eventOrderInfo.setVehicleClassName("挂车");
            eventOrderInfo.setVehicleClass(6);
            TrailerManagement trailer = iTrailerManagementService.getTrailerManagement(plateNumber, tenantId);
            if (trailer != null) {
                eventOrderInfo.setTrailerId(trailer.getId());
            }
        }
        eventOrderInfo.setPlateNumber(plateNumber);
        //当前车辆位置
        Location currLocation = null;
        String[] loctionArr = iVehicleDataInfoService.getPositionByBillId(eventOrderInfo.getCarDriverPhone(), eventOrderInfo.getPlateNumber());
        if (loctionArr != null) {
            currLocation = new Location(Double.parseDouble(loctionArr[1]), Double.parseDouble(loctionArr[0]), DateUtil.parseDate(loctionArr[2]).getTime());
        } else {
            currLocation = this.getLocationByTenant(tenantId);
        }

        eventsInfoOut.setCurrLatitude(currLocation != null ? currLocation.getLatitude() : 0);
        eventsInfoOut.setCurrLongitude(currLocation != null ? currLocation.getLongitude() : 0);

        if (eventCode.startsWith("2")) {
            if (MonitorEventsConst.CHECK_EVENTS.RETENTION24.equals(eventCode)) {
                checkInfo.setCheckCode(MonitorEventsConst.CHECK_EVENTS.RETENTION24);
                checkInfo.setCheckName("无单24h+");
            } else if (MonitorEventsConst.CHECK_EVENTS.RETENTION48.equals(eventCode)) {
                checkInfo.setCheckCode(MonitorEventsConst.CHECK_EVENTS.RETENTION48);
                checkInfo.setCheckName("无单48h+");
            } else if (MonitorEventsConst.CHECK_EVENTS.RETENTION72.equals(eventCode)) {
                checkInfo.setCheckCode(MonitorEventsConst.CHECK_EVENTS.RETENTION72);
                checkInfo.setCheckName("无单72h+");
            } else if (MonitorEventsConst.CHECK_EVENTS.CHECK.equals(eventCode)) {
                checkInfo.setCheckCode(MonitorEventsConst.CHECK_EVENTS.CHECK);
                checkInfo.setCheckName("盘点");
            }
            //计算备用时间，当前时间减去上单离场时间
            if (eventOrderInfo.getCarDepartureDate() != null) {
                checkInfo.setSpareHour((new Date().getTime() - eventOrderInfo.getCarDepartureDate().getTime()) / (1000 * 60 * 60));
            }
        } else if (eventCode.startsWith("3")) {
            VehicleCertInfoDto vehicleCerInfoOut = null;
            if (vehicleClass == 6) {
                VehicleCertInfoDto tenantTrialer = iTrailerManagementService.getTenantTrialer(plateNumber, tenantId);
                BeanUtil.copyProperties(tenantTrialer, vehicleCerInfoOut);
            } else {
                vehicleCerInfoOut = iVehicleDataInfoService.getTenantVehicleCertRel(plateNumber);
            }
            if (vehicleCerInfoOut != null) {
                try {
                    BeanUtil.copyProperties(vehicleCerInfoOut, checkInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (MonitorEventsConst.EXPIRE_EVENTS.VEHICLEVALIDITY.equals(eventCode)) {
                    checkInfo.setCheckCode(MonitorEventsConst.EXPIRE_EVENTS.VEHICLEVALIDITY);
                    checkInfo.setCheckName("行驶证审");
                    //计算要多少天行驶证到期
                    if (vehicleCerInfoOut.getVehicleValidityTime() != null) {
                        checkInfo.setRemainingDay((vehicleCerInfoOut.getVehicleValidityTime().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                    }
                } else if (MonitorEventsConst.EXPIRE_EVENTS.INSURANCE.equals(eventCode)) {
                    checkInfo.setCheckCode(MonitorEventsConst.EXPIRE_EVENTS.INSURANCE);
                    checkInfo.setCheckName("交强险");
                    //计算要多少天保险到期
                    if (vehicleCerInfoOut.getInsuranceTimeEnd() != null) {
                        checkInfo.setRemainingDay((vehicleCerInfoOut.getInsuranceTimeEnd().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                    }
                    checkInfo.setInsuranceTime(vehicleCerInfoOut.getInsuranceTimeEnd());
                } else if (MonitorEventsConst.EXPIRE_EVENTS.BUSI_INSURANCE.equals(eventCode)) {
                    checkInfo.setCheckCode(MonitorEventsConst.EXPIRE_EVENTS.BUSI_INSURANCE);
                    checkInfo.setCheckName("商业险");
                    //计算要多少天保险到期
                    if (vehicleCerInfoOut.getBusiInsuranceTimeEnd() != null) {
                        checkInfo.setRemainingDay((vehicleCerInfoOut.getBusiInsuranceTimeEnd().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                    }
                    checkInfo.setInsuranceTime(vehicleCerInfoOut.getBusiInsuranceTimeEnd());
                } else if (MonitorEventsConst.EXPIRE_EVENTS.OTHER_INSURANCE.equals(eventCode)) {
                    checkInfo.setCheckCode(MonitorEventsConst.EXPIRE_EVENTS.OTHER_INSURANCE);
                    checkInfo.setCheckName("其他险");
                    //计算要多少天保险到期
                    if (vehicleCerInfoOut.getOtherInsuranceTimeEnd() != null) {
                        checkInfo.setRemainingDay((vehicleCerInfoOut.getOtherInsuranceTimeEnd().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                    }
                    checkInfo.setInsuranceTime(vehicleCerInfoOut.getOtherInsuranceTimeEnd());
                } else if (MonitorEventsConst.EXPIRE_EVENTS.OPRATEVALIDITY.equals(eventCode)) {
                    checkInfo.setCheckCode(MonitorEventsConst.EXPIRE_EVENTS.OPRATEVALIDITY);
                    checkInfo.setCheckName("营运证审");
                    //计算要多少天营运证到期
                    if (vehicleCerInfoOut.getOperateValidityTime() != null) {
                        checkInfo.setRemainingDay((vehicleCerInfoOut.getOperateValidityTime().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                    }
                } else if (MonitorEventsConst.EXPIRE_EVENTS.MAINTAIN.equals(eventCode)) {
                    checkInfo.setCheckCode(MonitorEventsConst.EXPIRE_EVENTS.MAINTAIN);
                    checkInfo.setCheckName("保养");
                    checkInfo.setMaintainDis(vehicleCerInfoOut.getMaintainDis() != null ? vehicleCerInfoOut.getMaintainDis() : 0);
                    if (vehicleCerInfoOut.getPrevMaintainTime() != null) {
                        //查询上次保养时间到目前行驶公里数
                        List<Map> list = this.getMaintainWarningVehicle(null, plateNumber, tenantId);
                        if (list != null && list.size() > 0) {
                            //计算剩多少公里数需要保养，保养周期公里数-上次保养到目前行驶的公里数
                            Long distance = DataFormat.getLongKey(list.get(0), "distance");
                            if (distance == null || distance < 0) {
                                distance = 0L;
                            }
                            checkInfo.setRemainingMileage(checkInfo.getMaintainDis() - distance / 1000);
                        }
                    }
                }
            }
        } else if (eventCode.startsWith("4")) {
        }

        if (eventOrderInfo.getVehicleClass() != null && eventOrderInfo.getVehicleClass() == 6) {
            eventOrderInfo.setVehicleClassName("挂车");
            // 添加挂车id和车牌
            if (eventOrderInfo.getTrailerId() == null) {
                eventOrderInfo.setTrailerNumber(plateNumber);
                TrailerManagement trailer = iTrailerManagementService.getTrailerManagement(plateNumber, tenantId);
                if (trailer != null) {
                    eventOrderInfo.setTrailerId(trailer.getId());
                }
            }
        } else if (eventOrderInfo.getVehicleClass() != null) {
            SysStaticData sysStaticData = getSysStaticData("VEHICLE_CLASS", eventOrderInfo.getVehicleClass().toString());
            eventOrderInfo.setVehicleClassName(sysStaticData.getCodeName());
        }

        if (eventOrderInfo.getDesRegion() != null && (eventOrderInfo.getDesRegion()) != 0) {
            SysStaticData sys_city = getSysStaticData("SYS_CITY", eventOrderInfo.getDesRegion().toString());
            eventOrderInfo.setDesRegionName(sys_city.getCodeName());
        } else {
            eventOrderInfo.setDesRegionName("");
        }
        if (eventOrderInfo.getSourceRegion() != null && (eventOrderInfo.getSourceRegion()) != 0) {
            SysStaticData sys_city = getSysStaticData("SYS_CITY", eventOrderInfo.getSourceRegion().toString());
            eventOrderInfo.setSourceRegionName(sys_city.getCodeName());
        } else {
            eventOrderInfo.setSourceRegionName("");
        }

        if (null == eventOrderInfo.getCustomName()) {
            eventOrderInfo.setCustomName("");
        }

        eventsInfoOut.setEventOrderInfo(eventOrderInfo);
        eventsInfoOut.setCheckInfo(checkInfo);
        return eventsInfoOut;
    }

    @Override
    public List<Location> queryOrderTravelTrack(Long orderId, String accessToken) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号码不能为空！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler orderScheduler = new OrderScheduler();
        List<Location> listOut = new ArrayList<>();
        boolean isOrderHis = false;
        if (orderInfo == null) {
            OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            OrderSchedulerH schedulerH = iOrderSchedulerService.getOrderSchedulerH(orderId);
            BeanUtil.copyProperties(schedulerH, orderScheduler);
            isOrderHis = true;
        } else {
            if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.ARRIVED) {
            }
            orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT);
        Double dependAdvanceHour = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_DEPEND_ADVANCE_HOUR", 0, String.class, accessToken) == null ? 0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_DEPEND_ADVANCE_HOUR", 0, String.class, accessToken) + "");
        Double estStartTime = Double.parseDouble(String.valueOf(sysCfgRedisUtils.getCfgVal(-1L, "ESTIMATE_START_TIME", 0, String.class, accessToken)));
        String startDate = dateTimeFormatter.format(OrderDateUtil.subHourAndMins(orderScheduler.getCarDependDate() == null ? orderScheduler.getDependTime() : orderScheduler.getCarDependDate(), dependAdvanceHour.floatValue()));
        String endDate = null;
        endDate = orderScheduler.getCarArriveDate() == null ?
                dateTimeFormatter.format(orderDateUtil.addHourAndMins(orderInfoExtService.getOrderArriveDate(orderId,
                        orderScheduler.getCarDependDate() == null ? orderScheduler.getDependTime() :
                                orderScheduler.getCarDependDate(), orderScheduler.getCarStartDate(),
                        orderScheduler.getArriveTime(), isOrderHis), estStartTime.floatValue()))
                : dateTimeFormatter.format(orderDateUtil.addHourAndMins(orderScheduler.getCarArriveDate(), estStartTime.floatValue()));

       // List<TspVehicleTrackVo> vehicleTrackList = tspVehicleTrackService.getVehicleTrackList(orderScheduler.getPlateNumber(), startDate, endDate);
        List<TspVehicleTrackVo> vehicleTrackList = null;
        if (vehicleTrackList == null || vehicleTrackList.size() == 0) {

            if (orderScheduler.getVehicleClass() != null) {
                //未开发
//                if (orderScheduler.getVehicleClass().intValue() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
//                    if (StringUtils.isNotBlank(orderScheduler.getCarDriverPhone())) {
//                        listOut = locationDataSV.getLocation(startDate, endDate, orderScheduler.getCarDriverPhone()+"");
//                    }
//                }else if(orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1){
//                    IOrderDriverSwitchSV orderDriverSwitchSV = (IOrderDriverSwitchSV) SysContexts.getBean("orderDriverSwitchSV");
//                    List<OrderDriverSwitchInfo>  driverSwitchInfos = orderDriverSwitchSV.getSwitchInfosByOrder(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
//                    if (driverSwitchInfos != null && driverSwitchInfos.size() > 0) {
//                        for (int i = 0; i < driverSwitchInfos.size(); i++) {
//                            OrderDriverSwitchInfo switchInfo = driverSwitchInfos.get(i);
//                            String date = endDate;
//                            if (i == 0) {
//                                if (switchInfo.getAffirmDate().getTime()
//                                        > DateUtil.formatStringToDate(endDate,  DateUtil.DATETIME_FORMAT).getTime()) {
//                                    List<Map> list = locationDataSV.getLocation(startDate, endDate, orderScheduler.getCarDriverPhone()+"");
//                                    listOut.addAll(list);
//                                    break;
//                                }else{
//                                    List<Map> list = locationDataSV.getLocation(startDate, DateUtil.formatDate(switchInfo.getAffirmDate(), DateUtil.DATETIME_FORMAT), orderScheduler.getCarDriverPhone()+"");
//                                    listOut.addAll(list);
//                                }
//                            }else{
//                                OrderDriverSwitchInfo lastSwitchInfo = driverSwitchInfos.get(i-1);
//                                date = DateUtil.formatDate(lastSwitchInfo.getAffirmDate(), DateUtil.DATETIME_FORMAT);
//                                if (switchInfo.getAffirmDate().getTime()
//                                        > DateUtil.formatStringToDate(endDate,  DateUtil.DATETIME_FORMAT).getTime()) {
//                                    List<Map> list = locationDataSV.getLocation(DateUtil.formatDate(lastSwitchInfo.getAffirmDate(), DateUtil.DATETIME_FORMAT),
//                                            endDate, lastSwitchInfo.getReceiveUserPhone()+"");
//                                    listOut.addAll(list);
//                                    break;
//                                }else{
//                                    List<Map> list = locationDataSV.getLocation(DateUtil.formatDate(lastSwitchInfo.getAffirmDate(), DateUtil.DATETIME_FORMAT),
//                                            DateUtil.formatDate(switchInfo.getAffirmDate(), DateUtil.DATETIME_FORMAT),
//                                            lastSwitchInfo.getReceiveUserPhone()+"");
//                                    listOut.addAll(list);
//                                }
//                            }
//                            List<Map> list = locationDataSV.getLocation(DateUtil.formatDate(switchInfo.getAffirmDate()),
//                                    date,
//                                    switchInfo.getReceiveUserPhone()+"");
//                            listOut.addAll(list);
//                        }
//                    }else{
//                        listOut = locationDataSV.getLocation(startDate, endDate, orderScheduler.getCarDriverPhone()+"");
//                    }
//                }
            }
        } else {
            for (TspVehicleTrackVo t : vehicleTrackList
            ) {
                Location location = new Location();
                if (t.getLat() == null || Double.valueOf(t.getLng()) == null || t.getCreateTime() == null) {
                    continue;
                }
                location.setLatitude(Double.valueOf(t.getLat()));
                location.setLongitude(Double.valueOf(t.getLng()));
                location.setTimeStr(t.getTime());
                listOut.add(location);
            }
        }
        return listOut;
    }

    @Override
    public Map setRealLocation(int gpsType, OrderScheduler orderScheduler, Long orderId) {
        String equipmentNumber = "";
        Map map = new ConcurrentHashMap<>();
        if (gpsType == OrderConsts.GPS_TYPE.G7) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(orderScheduler.getVehicleCode());
            equipmentNumber = vehicleDataInfo.getEquipmentCode();
        } else if (gpsType == OrderConsts.GPS_TYPE.YL || gpsType == OrderConsts.GPS_TYPE.BD) {
            equipmentNumber = orderScheduler.getPlateNumber();
        } else if (gpsType == OrderConsts.GPS_TYPE.APP) {
            String billId = null == orderScheduler.getCarDriverPhone() ? "" : orderScheduler.getCarDriverPhone();
            if (null != orderScheduler.getVehicleClass() && orderScheduler.getVehicleClass() == 1) {
                billId = orderScheduler.getOnDutyDriverPhone(); // 自有车取当值司机
            }
            equipmentNumber = billId;
        }
        String nand = "";
        String eand = "";
        String[] locations = iVehicleDataInfoService.getPositionByBillId(equipmentNumber, orderScheduler.getPlateNumber());
        if (locations != null && CommonUtil.checkIsDouble(locations[0]) && CommonUtil.checkIsDouble(locations[1])) {
            nand = String.valueOf(locations[0]);
            eand = String.valueOf(locations[1]);
        }
        map.put("equipmentNumber", null == equipmentNumber ? "" : equipmentNumber);
        map.put("sourceNand", nand);
        map.put("sourceEand", eand);
        return map;
    }

    @Override
    public List<Object> queryMonitorOrderAgingList(Long orderId, Integer agingType) {
        List<Object> listOut = new ArrayList<>();
        boolean isHis = false;

        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderId, orderId);
        OrderInfo orderInfo = orderInfoMapper.selectList(orderInfoLambdaQueryWrapper).get(0);

        if (orderInfo == null) {

            LambdaQueryWrapper<OrderInfoH> orderInfoHLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderInfoHLambdaQueryWrapper.eq(OrderInfoH::getOrderId, orderId);
            OrderInfoH orderInfoH = orderInfoHMapper.selectList(orderInfoHLambdaQueryWrapper).get(0);

            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            isHis = true;
        } else {
            if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.ARRIVED) {
                isHis = true;
            }
        }
        if (agingType != null && agingType > 0) {
            if (agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE1) {
                if (isHis) {
                    List<MonitorOrderAgingDependH> agingDepends = iMonitorOrderAgingDependHService.getMonitorOrderAgingDependH(orderId, null, null, null, null);
                    if (agingDepends != null && agingDepends.size() > 0) {
                        listOut.addAll(agingDepends);
                    }
                } else {
                    List<MonitorOrderAgingDepend> agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, null, null, null);
                    if (agingDepends != null && agingDepends.size() > 0) {
                        listOut.addAll(agingDepends);
                    }
                }
            } else if (agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE2
                    || agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE3) {
                if (agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE2) {
                    agingType = OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE2;
                } else {
                    agingType = OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE1;
                }
                if (isHis) {
                    List<MonitorOrderAgingAbnormalH> abnormalList = iMonitorOrderAgingAbnormalHService.getMonitorOrderAgingAbnormalH(orderId, null, agingType, null, null);
                    if (abnormalList != null && abnormalList.size() > 0) {
                        listOut.addAll(abnormalList);
                    }
                } else {
                    List<MonitorOrderAgingAbnormal> abnormalList = getMonitorOrderAgingAbnormal(orderId, null, agingType, null, null);
                    if (abnormalList != null && abnormalList.size() > 0) {
                        listOut.addAll(abnormalList);
                    }
                }
            } else if (agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE4
                    || agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE5) {
                if (agingType.intValue() == OrderConsts.MONITOR_AGING_TYPE.MONITOR_AGING_TYPE4) {
                    if (isHis) {
                        List<MonitorOrderAgingArriveH> lastList = iMonitorOrderAgingArriveHService.getMonitorOrderAgingArriveH(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null);
                        if (lastList != null && lastList.size() > 0) {
                            listOut.addAll(lastList);
                        }
                    } else {
                        List<MonitorOrderAgingArrive> realLastList = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null);
                        if (realLastList != null && realLastList.size() > 0) {
                            listOut.addAll(realLastList);
                        }
                    }
                } else {
                    if (isHis) {
                        List<MonitorOrderAgingArriveH> lastList = iMonitorOrderAgingArriveHService.getMonitorOrderAgingArriveH(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null);
                        if (lastList != null && lastList.size() > 0) {
                            listOut.addAll(lastList);
                        }
                        List<MonitorOrderAgingArriveH> realLastList = iMonitorOrderAgingArriveHService.getMonitorOrderAgingArriveH(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, null);
                        if (realLastList != null && realLastList.size() > 0) {
                            listOut.addAll(realLastList);
                        }
                    } else {
                        List<MonitorOrderAgingArrive> lastList = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null);
                        if (lastList != null && lastList.size() > 0) {
                            listOut.addAll(lastList);
                        }
                        List<MonitorOrderAgingArrive> realLastList = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, null);
                        if (realLastList != null && realLastList.size() > 0) {
                            listOut.addAll(realLastList);
                        }
                    }
                }
            }
        } else {
            setMonitorOrderAgingList(listOut, orderId, isHis);
        }
        return listOut;
    }

    @Override
    public String doTask(Map<String, Object> arg0) {
        this.calculateAging();
        return null;
    }

    @Override
    public void createAgingHis(Long orderId) {
        List<MonitorOrderAgingAbnormal> abnormals = this.getMonitorOrderAgingAbnormal(orderId, null, null, null, null);
        if (abnormals != null && abnormals.size() > 0) {
            for (MonitorOrderAgingAbnormal monitorOrderAgingAbnormal : abnormals) {
                MonitorOrderAgingAbnormalH agingAbnormalH = new MonitorOrderAgingAbnormalH();
                BeanUtil.copyProperties(monitorOrderAgingAbnormal, agingAbnormalH);
                agingAbnormalH.setId(null);
                iMonitorOrderAgingAbnormalHService.saveOrUpdate(agingAbnormalH);
            }
            this.deleteMonitorOrderAgingAbnormal(orderId, null, null);
        }
        List<MonitorOrderAgingArrive> agingArrives = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, null, null);
        if (agingArrives != null && agingArrives.size() > 0) {
            for (MonitorOrderAgingArrive monitorOrderAgingArrive : agingArrives) {
                MonitorOrderAgingArriveH agingArriveH = new MonitorOrderAgingArriveH();
                BeanUtil.copyProperties(monitorOrderAgingArrive, agingArriveH);
                agingArriveH.setId(null);
                iMonitorOrderAgingArriveHService.saveOrUpdate(agingArriveH);
            }
            iMonitorOrderAgingArriveService.deleteMonitorOrderAgingArrive(orderId, null);
        }
        List<MonitorOrderAgingDepend> agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, null, null, null);
        if (agingDepends != null && agingDepends.size() > 0) {
            for (MonitorOrderAgingDepend monitorOrderAgingDepend : agingDepends) {
                MonitorOrderAgingDependH agingDependH = new MonitorOrderAgingDependH();
                BeanUtil.copyProperties(monitorOrderAgingDepend, agingDependH);
                agingDependH.setId(null);
                iMonitorOrderAgingDependHService.saveOrUpdate(agingDependH);
            }
            iMonitorOrderAgingDependService.deleteMonitorOrderAgingDepend(orderId, null, null);
        }
    }

    @Override
    public boolean deleteMonitorOrderAgingAbnormal(Long orderId, Integer type, Integer lineType) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        LambdaQueryWrapper<MonitorOrderAgingAbnormal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonitorOrderAgingAbnormal::getOrderId, orderId);
        if (type != null && type > 0) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getType, type);
        }
        if (lineType != null && lineType > 0) {
            queryWrapper.eq(MonitorOrderAgingAbnormal::getLineType, lineType);
        }
        return this.remove(queryWrapper);
    }

    /**
     * 查询盘点车数量
     */
    private int getVehicleCount(Integer type, String plateNumber, Long orgId, Long tenantId) {
        if (orgId == null) {
            orgId = 0L;
        }
        if (type == null || type < 0 || type > 6) {
            type = 0;
        }
        return monitorOrderAgingAbnormalMapper.queryVehicleCount(type, tenantId, plateNumber, orgId);
    }

    /**
     * 获取某个时间内无订单的车辆数量
     */
    public int getNoneOrderVehicleCount(Long tenantId, Long orgId, String plateNumber, int hour, int hourEnd) {

        // 自由车 1
        List<Map> list = monitorOrderAgingAbnormalMapper.queryNoneOrderVehicle(1, tenantId
                , orgId, plateNumber, hour, hourEnd, "true");
        if (list != null && list.size() > 0) {
            return DataFormat.getIntKey(list.get(0), "count");
        }

        return 0;
    }

    /**
     * 获取需要行驶证审的车辆
     */
    private Integer getValidityVehicleCount(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalMapper.getValidityTrailerCountQuery("VEHICLE_VALIDITY_EXPIRED_TIME", "vehicleValidityTime",
                    tenantId, plateNumber, expireDate);
        } else {
            return monitorOrderAgingAbnormalMapper.getSearchVehicleCountQuery("VEHICLE_VALIDITY_TIME", "vehicleValidityTime",
                    tenantId, plateNumber, expireDate, vehicleClass, 2);
        }
    }

    /**
     * 获取保险快到期的车辆
     */
    private int getInsuranceVehicleCount(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalMapper.getSearchTrailerCountQuery("INSURANCE_EXPIRED_TIME", "insuranceTime",
                    tenantId, plateNumber, expireDate);
        } else {
            return monitorOrderAgingAbnormalMapper.getSearchVehicleCountQuery("INSURANCE_TIME_END", "insuranceTime",
                    tenantId, plateNumber, expireDate, vehicleClass, 2);
        }
    }

    /**
     * 获取商业险快到期的车辆数量
     */
    private Integer getBusiInsuranceVehicleCount(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalMapper.getSearchTrailerCountQuery("BUSINESS_INSURANCE_EXPIRED_TIME", "insuranceTime",
                    tenantId, plateNumber, expireDate);
        } else {
            return monitorOrderAgingAbnormalMapper.getSearchVehicleCountQuery("BUSI_INSURANCE_TIME_END", "insuranceTime",
                    tenantId, plateNumber, expireDate, vehicleClass, 2);
        }
    }

    /**
     * 获取其他险快到期的车辆数量
     */
    private Integer getOtherInsuranceVehicleCount(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalMapper.getSearchTrailerCountQuery("OTHER_INSURANCE_EXPIRED_TIME", "insuranceTime",
                    tenantId, plateNumber, expireDate);
        } else {
            return monitorOrderAgingAbnormalMapper.getSearchVehicleCountQuery("OTHER_INSURANCE_TIME_END", "insuranceTime",
                    tenantId, plateNumber, expireDate, vehicleClass, 2);
        }
    }

    /**
     * 获取行驶证快到期或营运证快到期的车辆
     */
    private Integer getOperateValidityVehicleCount(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalMapper.getValidityTrailerCountQuery("OPERATE_VALIDITY_EXPIRED_TIME", "operateValidityTime",
                    tenantId, plateNumber, expireDate);
        } else {
            return monitorOrderAgingAbnormalMapper.getSearchVehicleCountQuery("OPERATE_VALIDITY_TIME", "operateValidityTime",
                    tenantId, plateNumber, expireDate, vehicleClass, 2);
        }
    }

    /**
     * 获取保养预警的车辆
     */
    public List<Map> getMaintainWarningVehicle(Long orgId, String plateNumber, Long tenantId) {
        List<Map> list = new ArrayList<Map>();
//        if (StringUtils.isNotBlank(plateNumber) && plateNumber.contains("挂") || StringUtils.isBlank(plateNumber)) {
//            list.addAll(monitorOrderAgingAbnormalMapper.queryMaintainWarningTrailer(tenantId, orgId, plateNumber));
//        }
//        if (StringUtils.isNotBlank(plateNumber) && !plateNumber.contains("挂") || StringUtils.isBlank(plateNumber)) {
//            list.addAll(monitorOrderAgingAbnormalMapper.queryMaintainWarningVehicle(tenantId, orgId, plateNumber));
//        }
        // 车辆维修 -- 保养数据查询
        List<Map> maps = monitorOrderAgingAbnormalMapper.queryMaintainWarningVehicleBY(tenantId, plateNumber);
        for (Map map : maps) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(DataFormat.getStringKey(map, "platenumber"));
            if (null != vehicleDataInfo) {
                map.put("vehiclecode", vehicleDataInfo.getId());
            }
        }
        list.addAll(maps);

        // 维修工单 -- 维修非查询
        list.addAll(monitorOrderAgingAbnormalMapper.queryMaintainWarningVehicleWX(tenantId, plateNumber));

        return list;
    }

    /**
     * 获取车辆使用定位类型
     */
    public Integer queryVehicleGpsType(Long vehicleCode) {
        int gpsType = OrderConsts.GPS_TYPE.APP;
        if (vehicleCode != null) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(vehicleCode);
            if (vehicleDataInfo != null) {
                if (vehicleDataInfo.getLocationServ() != null) {
                    if (vehicleDataInfo.getLocationServ().intValue() == OrderConsts.GPS_TYPE.G7) {
                        gpsType = OrderConsts.GPS_TYPE.G7;
                    } else if (vehicleDataInfo.getLocationServ().intValue() == OrderConsts.GPS_TYPE.YL) {
                        gpsType = OrderConsts.GPS_TYPE.YL;
                    } else if (vehicleDataInfo.getLocationServ().intValue() == OrderConsts.GPS_TYPE.BD) {
                        gpsType = OrderConsts.GPS_TYPE.BD;
                    }
                }
            }
        }
        return gpsType;
    }

    private void setMonitorOrderAgingList(List<Object> listOut, Long orderId, boolean isHis) {
        if (isHis) {
            List<MonitorOrderAgingDependH> agingDepends = iMonitorOrderAgingDependHService.getMonitorOrderAgingDependH(orderId, null, null, null, null);
            if (agingDepends != null && agingDepends.size() > 0) {
                listOut.addAll(agingDepends);
            }
            List<MonitorOrderAgingAbnormalH> ambleList = iMonitorOrderAgingAbnormalHService.getMonitorOrderAgingAbnormalH(orderId, null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE1, null, null);
            if (ambleList != null && ambleList.size() > 0) {
                listOut.addAll(ambleList);
            }
            List<MonitorOrderAgingAbnormalH> stayList = iMonitorOrderAgingAbnormalHService.getMonitorOrderAgingAbnormalH(orderId, null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE2, null, null);
            if (stayList != null && stayList.size() > 0) {
                listOut.addAll(stayList);
            }
            List<MonitorOrderAgingArriveH> estLastList = iMonitorOrderAgingArriveHService.getMonitorOrderAgingArriveH(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null);
            if (estLastList != null && estLastList.size() > 0) {
                listOut.addAll(estLastList);
            }
            List<MonitorOrderAgingArriveH> lastList = iMonitorOrderAgingArriveHService.getMonitorOrderAgingArriveH(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null);
            if (lastList != null && lastList.size() > 0) {
                listOut.addAll(lastList);
            }
            List<MonitorOrderAgingArriveH> realLastList = iMonitorOrderAgingArriveHService.getMonitorOrderAgingArriveH(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, null);
            if (realLastList != null && realLastList.size() > 0) {
                listOut.addAll(realLastList);
            }
        } else {
            List<MonitorOrderAgingDepend> agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, null, null, null);
            if (agingDepends != null && agingDepends.size() > 0) {
                listOut.addAll(agingDepends);
            }
            List<MonitorOrderAgingAbnormal> ambleList = getMonitorOrderAgingAbnormal(orderId, null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE1, null, null);
            if (ambleList != null && ambleList.size() > 0) {
                listOut.addAll(ambleList);
            }
            List<MonitorOrderAgingAbnormal> stayList = getMonitorOrderAgingAbnormal(orderId, null, OrderConsts.MONITOR_ABNORMAL_TYPE.MONITOR_ABNORMAL_TYPE2, null, null);
            if (stayList != null && stayList.size() > 0) {
                listOut.addAll(stayList);
            }
            List<MonitorOrderAgingArrive> estLastList = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null);
            if (estLastList != null && estLastList.size() > 0) {
                listOut.addAll(estLastList);
            }
            List<MonitorOrderAgingArrive> lastList = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null);
            if (lastList != null && lastList.size() > 0) {
                listOut.addAll(lastList);
            }
            List<MonitorOrderAgingArrive> realLastList = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, null);
            if (realLastList != null && realLastList.size() > 0) {
                listOut.addAll(realLastList);
            }
        }
    }

    /**
     * 获取对应事件的车辆 (需求改动)
     */
    private List<EventsVehicleNewDto> getVehicleList(MonitorOrderAgingVo monitorOrderAgingVo, Long tenantId) {
        if (monitorOrderAgingVo == null) {
            monitorOrderAgingVo = new MonitorOrderAgingVo();
        }
//        if(monitorOrderAgingVo.getOrgId() == null){
//            monitorOrderAgingVo.setOrgId(-1L);
//        }
//        if(monitorOrderAgingVo.getVehicleClass() == null){
//            monitorOrderAgingVo.setVehicleClass(-1);
//        }
        if (monitorOrderAgingVo.getOrgId() == null) {
            monitorOrderAgingVo.setOrgId(-1L);
        }

        // 测试处理时长
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("===============获取对应事件的车辆,参数：" + monitorOrderAgingVo.toString() + "开始时间：" + watch.toString());

        List<EventsVehicleNewDto> vehicleDatas = new ArrayList<EventsVehicleNewDto>();
        //时效车辆
        if (StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode()) && monitorOrderAgingVo.getEventCode().startsWith("1")) {
            List<EventsInfoDto> eventInfoOuts = getVehicleEventsList(monitorOrderAgingVo, tenantId);
            Location tenantLocation = this.getLocationByTenant(tenantId);
            if (eventInfoOuts != null) {
                for (EventsInfoDto eventsInfoOut : eventInfoOuts) {
                    EventsVehicleNewDto eventsVehicleOut = new EventsVehicleNewDto();
                    if (CommonUtil.checkIsDouble("" + eventsInfoOut.getCurrLatitude()) && eventsInfoOut.getCurrLatitude() > 0
                            && CommonUtil.checkIsDouble("" + eventsInfoOut.getCurrLongitude()) && eventsInfoOut.getCurrLongitude() > 0) {
                        eventsVehicleOut.setCurrLatitude(eventsInfoOut.getCurrLatitude());
                        eventsVehicleOut.setCurrLongitude(eventsInfoOut.getCurrLongitude());
                    } else if (tenantLocation != null) {
                        eventsVehicleOut.setCurrLatitude(tenantLocation.getLatitude());
                        eventsVehicleOut.setCurrLongitude(tenantLocation.getLongitude());
                    }
                    eventsVehicleOut.setCarDriverPhone(eventsInfoOut.getEventOrderInfo().getCarDriverPhone());
                    eventsVehicleOut.setPlateNumber(eventsInfoOut.getEventOrderInfo().getPlateNumber());
                    eventsVehicleOut.setEventCode(StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode()) && !MonitorEventsConst.EFFICIENCY_EVENTS.ALL.equals(monitorOrderAgingVo.getEventCode())
                            ? monitorOrderAgingVo.getEventCode() : eventsInfoOut.getEventsInfoArr().get(0).getEventCode());
                    eventsVehicleOut.setEventName(StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode()) && !MonitorEventsConst.EFFICIENCY_EVENTS.ALL.equals(monitorOrderAgingVo.getEventCode())
                            ? MonitorEventsConst.EFFICIENCY_EVENTS.getNameByCode(monitorOrderAgingVo.getEventCode())
                            : MonitorEventsConst.EFFICIENCY_EVENTS.getNameByCode(eventsInfoOut.getEventsInfoArr().get(0).getEventCode()));
                    eventsVehicleOut.setHasOrderFlag(true);
                    vehicleDatas.add(eventsVehicleOut);
                }
            }
            log.info("===============获取对应事件的车辆,参数：" + monitorOrderAgingVo.toString() + "获取时效时间：" + watch.toString());
        }
        //盘点车辆
        else if (StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode()) && monitorOrderAgingVo.getEventCode().startsWith("2")) {
            if (MonitorEventsConst.CHECK_EVENTS.CHECK.equals(monitorOrderAgingVo.getEventCode())) {
                vehicleDatas.addAll(getCheckVehicle(monitorOrderAgingVo.getVehicleClass(), monitorOrderAgingVo.getOrgId().intValue(), monitorOrderAgingVo.getPlateNumber(), tenantId));
            } else if (MonitorEventsConst.CHECK_EVENTS.RETENTION24.equals(monitorOrderAgingVo.getEventCode())
                    && (monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.isSelfVehicleClass())) {
                vehicleDatas.addAll(getNoneOrderVehicleForTwo(monitorOrderAgingVo.getOrgId().intValue(), monitorOrderAgingVo.getPlateNumber(), tenantId));
            } else if (MonitorEventsConst.CHECK_EVENTS.RETENTION48.equals(monitorOrderAgingVo.getEventCode())
                    && (monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.isSelfVehicleClass())) {
                vehicleDatas.addAll(getNoneOrderVehicleForFour(monitorOrderAgingVo.getOrgId().intValue(), monitorOrderAgingVo.getPlateNumber(), tenantId));
            } else if (MonitorEventsConst.CHECK_EVENTS.RETENTION72.equals(monitorOrderAgingVo.getEventCode())
                    && (monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.isSelfVehicleClass())) {
                vehicleDatas.addAll(getNoneOrderVehicleForSeven(monitorOrderAgingVo.getOrgId().intValue(), monitorOrderAgingVo.getPlateNumber(), tenantId));
            }
            log.info("===============获取对应事件的车辆,参数：" + monitorOrderAgingVo.toString() + "获取盘点车辆时间：" + watch.toString());
            // 行驶证审
        }else if (StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode()) && monitorOrderAgingVo.getEventCode().startsWith("3")) {
            Date expireDate = DateUtil.addDate(new Date(), EXPIRE_DAY);
            if (monitorOrderAgingVo.getVehicleClass() == null) {
                monitorOrderAgingVo.setVehicleClass(0);
            }
            if (MonitorEventsConst.EXPIRE_EVENTS.VEHICLEVALIDITY.equals(monitorOrderAgingVo.getEventCode())) {
                vehicleDatas.addAll(getValidityVehicle(expireDate, monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getVehicleClass(), tenantId));
            } else if (MonitorEventsConst.EXPIRE_EVENTS.INSURANCE.equals(monitorOrderAgingVo.getEventCode())) {
                vehicleDatas.addAll(getInsuranceVehicle(expireDate, monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getVehicleClass(), tenantId));
            } else if (MonitorEventsConst.EXPIRE_EVENTS.BUSI_INSURANCE.equals(monitorOrderAgingVo.getEventCode())) {
                vehicleDatas.addAll(getBusiInsuranceVehicle(expireDate, monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getVehicleClass(), tenantId));
            } else if (MonitorEventsConst.EXPIRE_EVENTS.OTHER_INSURANCE.equals(monitorOrderAgingVo.getEventCode())) {
                vehicleDatas.addAll(getOtherInsuranceVehicle(expireDate, monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getVehicleClass(), tenantId));
            } else if (MonitorEventsConst.EXPIRE_EVENTS.OPRATEVALIDITY.equals(monitorOrderAgingVo.getEventCode())) {
                vehicleDatas.addAll(getOperateValidityVehicle(expireDate, monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getVehicleClass(), tenantId));
            } else if (MonitorEventsConst.EXPIRE_EVENTS.MAINTAIN.equals(monitorOrderAgingVo.getEventCode())
                    && (monitorOrderAgingVo.getVehicleClass() == 0 || monitorOrderAgingVo.isSelfVehicleClass())) {
                vehicleDatas.addAll(getMaintainVehicle(monitorOrderAgingVo.getOrgId().intValue(), monitorOrderAgingVo.getPlateNumber(), monitorOrderAgingVo.getVehicleClass(), tenantId));
            }
            log.info("===============获取对应事件的车辆,参数：" + monitorOrderAgingVo.toString() + "获取过期车辆时间：" + watch.toString());
        } else if (StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode()) && monitorOrderAgingVo.getEventCode().startsWith("4")) {

        } else {
            throw new BusinessException("事件编号不正确！");
        }

        return vehicleDatas;
    }

    /**
     * 获取有时效异常的车辆列表
     */
    private List<EventsInfoDto> getVehicleEventsList(MonitorOrderAgingVo monitorOrderAgingVo, Long tenantId) {
        //查找本租户下面的时效事件
        List<EventsInfoDto> eventInfos = getEventInfoOuts(tenantId);
        if (eventInfos == null) {
            return null;
        }
        Set<String> plateNumbers = new HashSet<String>();//用于过滤重复的车辆
        Iterator<EventsInfoDto> it = eventInfos.iterator();
        while (it.hasNext()) {
            EventsInfoDto eventsInfoOut = it.next();
            if (eventsInfoOut.getEventOrderInfo() == null || eventsInfoOut.getEventsInfoArr() == null || eventsInfoOut.getEventsInfoArr().size() == 0) {
                it.remove();
                continue;
            }
            //过滤车牌号
            if (StringUtils.isNotBlank(monitorOrderAgingVo.getPlateNumber())
                    && !monitorOrderAgingVo.getPlateNumber().equals(eventsInfoOut.getEventOrderInfo().getPlateNumber())
                    || plateNumbers.contains(eventsInfoOut.getEventOrderInfo().getPlateNumber())) {
                it.remove();
                continue;
            }
            plateNumbers.add(eventsInfoOut.getEventOrderInfo().getPlateNumber());

            //过滤车类型
            if (monitorOrderAgingVo.getVehicleClass() != null && monitorOrderAgingVo.getVehicleClass() >= 0
                    && !monitorOrderAgingVo.getVehicleClass().equals(eventsInfoOut.getEventOrderInfo().getVehicleClass())) {
                it.remove();
                continue;
            }
            //过滤部门
            if (monitorOrderAgingVo.getOrgId() != null && monitorOrderAgingVo.getOrgId() > 0
                    && !monitorOrderAgingVo.getOrgId().equals(eventsInfoOut.getEventOrderInfo().getOrgId())) {
                it.remove();
                continue;
            }
            //过滤出发城市
            if (monitorOrderAgingVo.getSourceRegion() != null && monitorOrderAgingVo.getSourceRegion() > 0
                    && !monitorOrderAgingVo.getSourceRegion().equals(eventsInfoOut.getEventOrderInfo().getSourceRegion())) {
                it.remove();
                continue;
            }
            //过滤目的城市
            if (monitorOrderAgingVo.getDesRegion() != null && monitorOrderAgingVo.getDesRegion() > 0
                    && !monitorOrderAgingVo.getDesRegion().equals(eventsInfoOut.getEventOrderInfo().getDesRegion())) {
                it.remove();
                continue;
            }
            //过滤事件
            if (!MonitorEventsConst.EFFICIENCY_EVENTS.ALL.equals(monitorOrderAgingVo.getEventCode())
                    && StringUtils.isNotBlank(monitorOrderAgingVo.getEventCode())) {
                boolean isContainFlag = false;
                for (BaseEventsInfoDto baseEventsInfo : eventsInfoOut.getEventsInfoArr()) {
                    if (monitorOrderAgingVo.getEventCode().equals(baseEventsInfo.getEventCode())) {
                        isContainFlag = true;
                    }
                }
                if (!isContainFlag) {
                    it.remove();
                    continue;
                }
            }
        }
        return eventInfos;
    }

    // 得到盘点车辆
    private List<EventsVehicleNewDto> getCheckVehicle(Integer vehicleClass, Integer orgId, String plateNumber, Long tenantId) {
        List<Map> list = this.getVehicleList(vehicleClass, plateNumber, orgId, tenantId);
        return this.getCheckVehicles2(list, MonitorEventsConst.CHECK_EVENTS.CHECK, "盘点", tenantId);
    }

    // 得到滞留24h+车辆
    private List<EventsVehicleNewDto> getNoneOrderVehicleForTwo(Integer orgId, String plateNumber, Long tenantId) {
        List<Map> list = getNoneOrderVehicle(tenantId, orgId, plateNumber, 24, 48);
        return this.getCheckVehicles2(list, MonitorEventsConst.CHECK_EVENTS.RETENTION24, "无单24h+", tenantId);
    }

    // 得到滞留48h+车辆
    private List<EventsVehicleNewDto> getNoneOrderVehicleForFour(Integer orgId, String plateNumber, Long tenantId) {
        List<Map> list = getNoneOrderVehicle(tenantId, orgId, plateNumber, 48, 72);
        return this.getCheckVehicles2(list, MonitorEventsConst.CHECK_EVENTS.RETENTION48, "无单48h+", tenantId);
    }

    //得到滞留72h+车辆
    private List<EventsVehicleNewDto> getNoneOrderVehicleForSeven(Integer orgId, String plateNumber, Long tenantId) {
        List<Map> list = getNoneOrderVehicle(tenantId, orgId, plateNumber, 72, -1);
        return this.getCheckVehicles2(list, MonitorEventsConst.CHECK_EVENTS.RETENTION72, "无单72h+", tenantId);
    }

    // 得到需要行驶证审车辆
    private List<EventsVehicleNewDto> getValidityVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (vehicleClass == 0 || vehicleClass >= 1 && vehicleClass != 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getValidityVehicle(expireDate, plateNumber, vehicleClass, tenantId));
//        }
//        if (vehicleClass == 0 || vehicleClass == 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getValidityVehicle(expireDate, plateNumber, 6, tenantId));
//        }
        List<LicenseVo> licenseVos = monitorOrderAgingAbnormalMapper.queryMaturityDoc(tenantId, plateNumber);
        List<LicenseVo> collect = licenseVos.stream().filter(bean -> "1".equals(bean.getAnnualreviewType())).collect(Collectors.toList());
        for (LicenseVo licenseVo : collect) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("plateNumber", licenseVo.getPlateNumber());
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(licenseVo.getPlateNumber());
            if (null != vehicleDataInfo) {
                map.put("vehicleCode", vehicleDataInfo.getId());
            }
            list.add(map);
        }

        return getCheckVehicles(list, MonitorEventsConst.EXPIRE_EVENTS.VEHICLEVALIDITY, "行驶证审", tenantId);
    }

    // 得到交强险快到期 车辆
    private List<EventsVehicleNewDto> getInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (vehicleClass == 0 || vehicleClass >= 1 && vehicleClass != 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getInsuranceVehicle(expireDate, plateNumber, vehicleClass, tenantId));
//        }
//        if (vehicleClass == 0 || vehicleClass == 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getInsuranceVehicle(expireDate, plateNumber, 6, tenantId));
//        }
        List<LicenseVo> licenseVos = monitorOrderAgingAbnormalMapper.queryMaturityDoc(tenantId, plateNumber);
        List<LicenseVo> collect = licenseVos.stream().filter(bean -> "3".equals(bean.getAnnualreviewType())).collect(Collectors.toList());
        for (LicenseVo licenseVo : collect) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("plateNumber", licenseVo.getPlateNumber());
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(licenseVo.getPlateNumber());
            if (null != vehicleDataInfo) {
                map.put("vehicleCode", vehicleDataInfo.getId());
            }
            list.add(map);
        }

        return getCheckVehicles(list, MonitorEventsConst.EXPIRE_EVENTS.INSURANCE, "交强险", tenantId);
    }

    // 得到商业险快到期 车辆
    private List<EventsVehicleNewDto> getBusiInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (vehicleClass == 0 || vehicleClass >= 1 && vehicleClass != 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getBusiInsuranceVehicle(expireDate, plateNumber, vehicleClass, tenantId));
//        }
//        if (vehicleClass == 0 || vehicleClass == 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getBusiInsuranceVehicle(expireDate, plateNumber, 6, tenantId));
//        }
        List<LicenseVo> licenseVos = monitorOrderAgingAbnormalMapper.queryMaturityDoc(tenantId, plateNumber);
        List<LicenseVo> collect = licenseVos.stream().filter(bean -> "4".equals(bean.getAnnualreviewType())).collect(Collectors.toList());
        for (LicenseVo licenseVo : collect) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("plateNumber", licenseVo.getPlateNumber());
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(licenseVo.getPlateNumber());
            if (null != vehicleDataInfo) {
                map.put("vehicleCode", vehicleDataInfo.getId());
            }
            list.add(map);
        }

        return getCheckVehicles(list, MonitorEventsConst.EXPIRE_EVENTS.BUSI_INSURANCE, "商业险", tenantId);
    }

    // 得到其他险快到期 车辆
    private List<EventsVehicleNewDto> getOtherInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (vehicleClass == 0 || vehicleClass >= 1 && vehicleClass != 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getOtherInsuranceVehicle(expireDate, plateNumber, vehicleClass, tenantId));
//        }
//        if (vehicleClass == 0 || vehicleClass == 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getOtherInsuranceVehicle(expireDate, plateNumber, 6, tenantId));
//        }
        List<LicenseVo> licenseVos = monitorOrderAgingAbnormalMapper.queryMaturityDoc(tenantId, plateNumber);
        List<LicenseVo> collect = licenseVos.stream().filter(bean -> "5".equals(bean.getAnnualreviewType())).collect(Collectors.toList());
        for (LicenseVo licenseVo : collect) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("plateNumber", licenseVo.getPlateNumber());
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(licenseVo.getPlateNumber());
            if (null != vehicleDataInfo) {
                map.put("vehicleCode", vehicleDataInfo.getId());
            }
            list.add(map);
        }

        return getCheckVehicles(list, MonitorEventsConst.EXPIRE_EVENTS.OTHER_INSURANCE, "其他险", tenantId);
    }

    // 得到营运证快到期车辆
    private List<EventsVehicleNewDto> getOperateValidityVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        if (vehicleClass == 0 || vehicleClass >= 1 && vehicleClass != 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getOperateValidityVehicle(expireDate, plateNumber, vehicleClass, tenantId));
//        }
//        if (vehicleClass == 0 || vehicleClass == 6) {
//            list.addAll(iMonitorOrderAgingAbnormalHService.getOperateValidityVehicle(expireDate, plateNumber, 6, tenantId));
//        }
        List<LicenseVo> licenseVos = monitorOrderAgingAbnormalMapper.queryMaturityDoc(tenantId, plateNumber);
        List<LicenseVo> collect = licenseVos.stream().filter(bean -> "2".equals(bean.getAnnualreviewType())).collect(Collectors.toList());
        for (LicenseVo licenseVo : collect) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("plateNumber", licenseVo.getPlateNumber());
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(licenseVo.getPlateNumber());
            if (null != vehicleDataInfo) {
                map.put("vehicleCode", vehicleDataInfo.getId());
            }
            list.add(map);
        }

        return getCheckVehicles(list, MonitorEventsConst.EXPIRE_EVENTS.OPRATEVALIDITY, "营运证审", tenantId);
    }

    // 得到达到预警公里数车辆
    private List<EventsVehicleNewDto> getMaintainVehicle(Integer orgId, String plateNumber, int vehicleClass, Long tenantId) {
        List<Map> list = getMaintainWarningVehicle(orgId.longValue(), plateNumber, tenantId);
        return this.getCheckVehicles2(list, MonitorEventsConst.EXPIRE_EVENTS.MAINTAIN, "保养", tenantId);
    }

    /**
     * 获取盘点车辆列表
     */
    private List<Map> getVehicleList(Integer type, String plateNumber, Integer orgId, Long tenantId) {
        if (orgId == null) {
            orgId = 0;
        }
        if (type == null) {
            type = 0;
        }
        return tenantVehicleRelService.queryVehicleList(type, tenantId, plateNumber, orgId);
    }

    /**
     * 获取某个时间内无订单的车辆
     */
    private List<Map> getNoneOrderVehicle(Long tenantId, Integer orgId, String plateNumber, int hour, int hourEnd) {
        // 1 自由车公司
        return tenantVehicleRelService.queryNoneOrderVehicle(1, tenantId
                , orgId.longValue(), plateNumber, hour, hourEnd, "false");
    }

    private List<EventsVehicleNewDto> getCheckVehicles2(List<Map> list, String eventCode, String eventName, Long tenantId) {
        Location currLocation = null;//当前车辆位置
        List<EventsVehicleNewDto> vehicleDatas = new ArrayList<EventsVehicleNewDto>();
        Map<String, String> plateNumbers = this.getVehicleByRuning(tenantId);//在途车辆集合
        Location tenantLocation = this.getLocationByTenant(tenantId);
        Integer vehicleClass = 0;
        String billId = null;
        for (Map map : list) {
            currLocation = null;
            EventsVehicleNewDto eventsVehicleOut = new EventsVehicleNewDto();
            vehicleClass = DataFormat.getIntKey(map, "vehicleClass");
            try {
                BeanUtil.copyProperties(map, eventsVehicleOut);
            } catch (Exception e) {
                e.printStackTrace();
            }
            eventsVehicleOut.setPlateNumber(DataFormat.getStringKey(map, "platenumber"));
            eventsVehicleOut.setVehicleCode(DataFormat.getStringKey(map, "vehiclecode"));
            eventsVehicleOut.setCarDriverPhone(DataFormat.getStringKey(map, "cardriverphone"));
            if (map.containsKey("vehicleNum")) {
                eventsVehicleOut.setPlateNumber(DataFormat.getStringKey(map, "vehicleNum"));
            }
            if (map.containsKey("linkPhone")) {
                eventsVehicleOut.setCarDriverPhone(DataFormat.getStringKey(map, "linkPhone"));
            }
            if (map.containsKey("carDriverPhone")) {
                eventsVehicleOut.setCarDriverPhone(DataFormat.getStringKey(map, "carDriverPhone"));
            }
            billId = eventsVehicleOut.getCarDriverPhone();

            String[] loctionArr = null;
            if (vehicleClass != null && vehicleClass != 6) {
                String plateNumberInfo = "";
//                Object obj = redisUtil.hget("VEHICLE_GPS_EQUIPMENT_INFO", eventsVehicleOut.getPlateNumber());
                Object obj = null;
                if (obj != null) {
                    plateNumberInfo = obj.toString();
                }
                if (StringUtils.isNotBlank(plateNumberInfo)) {
                    Map plateNumberMap = JsonHelper.parseJSON2Map(plateNumberInfo);
                    Integer locationServ = DataFormat.getIntKey(plateNumberMap, "locationServ");
                    String equipmentCode = DataFormat.getStringKey(plateNumberMap, "equipmentCode");
                    loctionArr = getPositionByBillId(billId, eventsVehicleOut.getPlateNumber(), locationServ <= 0 ? null : locationServ, equipmentCode);
                } else {
                    loctionArr = getPositionByBillId(billId, eventsVehicleOut.getPlateNumber(), OrderConsts.GPS_TYPE.APP, eventsVehicleOut.getPlateNumber());
                }
            }

            if (loctionArr != null) {
                currLocation = new Location(Double.parseDouble(loctionArr[1]), Double.parseDouble(loctionArr[0]), DateUtil.parseDate(loctionArr[2]).getTime());
            } else {
                currLocation = tenantLocation;
            }
            eventsVehicleOut.setCurrLatitude(currLocation != null ? currLocation.getLatitude() : 0);
            eventsVehicleOut.setCurrLongitude(currLocation != null ? currLocation.getLongitude() : 0);
            eventsVehicleOut.setEventCode(eventCode);
            eventsVehicleOut.setEventName(eventName);
            eventsVehicleOut.setHasOrderFlag(plateNumbers.containsKey(eventsVehicleOut.getPlateNumber()));
            eventsVehicleOut.setOrderId(DataFormat.getLongKey(plateNumbers, eventsVehicleOut.getPlateNumber()));
            vehicleDatas.add(eventsVehicleOut);
        }
        return vehicleDatas;
    }

    private List<EventsVehicleNewDto> getCheckVehicles(List<Map<String, Object>> list, String eventCode, String eventName, Long tenantId) {
        List<EventsVehicleNewDto> vehicleDatas = new ArrayList<EventsVehicleNewDto>();
        Map<String, String> plateNumbers = this.getVehicleByRuning(tenantId);//在途车辆集合
        Location tenantLocation = this.getLocationByTenant(tenantId);
        //当前车辆位置
        Location currLocation = null;
        for (Map<String, Object> map : list) {
            currLocation = null;
            EventsVehicleNewDto eventsVehicleOut = new EventsVehicleNewDto();
            try {
                BeanUtils.copyProperties(eventsVehicleOut, map);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] loctionArr = iVehicleDataInfoService.getPositionByBillId(eventsVehicleOut.getCarDriverPhone(), eventsVehicleOut.getPlateNumber());
            if (loctionArr != null) {
                currLocation = new Location(Double.parseDouble(loctionArr[1]), Double.parseDouble(loctionArr[0]), DateUtil.parseDate(loctionArr[2]).getTime());
            } else {
                currLocation = tenantLocation;
            }
            eventsVehicleOut.setCurrLatitude(currLocation != null ? currLocation.getLatitude() : 0.0);
            eventsVehicleOut.setCurrLongitude(currLocation != null ? currLocation.getLongitude() : 0.0);
            eventsVehicleOut.setEventCode(eventCode);
            eventsVehicleOut.setEventName(eventName);
            eventsVehicleOut.setHasOrderFlag(plateNumbers.containsKey(eventsVehicleOut.getPlateNumber()));
            eventsVehicleOut.setOrderId(DataFormat.getLongKey(plateNumbers, eventsVehicleOut.getPlateNumber()));
            vehicleDatas.add(eventsVehicleOut);
        }
        return vehicleDatas;
    }

    // 获取在途车辆
    private Map<String, String> getVehicleByRuning(Long tenantId) {
        List<Map> list = orderInfoMapper.queryPlateNumberOrderByTenantId(tenantId);

        Map<String, String> plateNumbers = new HashMap<String, String>();
        String tempPlateNumber = null;
        for (Map map : list) {
            tempPlateNumber = DataFormat.getStringKey(map, "plateNumber");
            if (StringUtils.isNotBlank(tempPlateNumber)) {
                plateNumbers.put(tempPlateNumber, DataFormat.getStringKey(map, "orderId"));
            }

            tempPlateNumber = DataFormat.getStringKey(map, "trailerPlate");
            if (StringUtils.isNotBlank(tempPlateNumber)) {
                plateNumbers.put(tempPlateNumber, DataFormat.getStringKey(map, "orderId"));
            }
        }

        return plateNumbers;
    }

    // 获取车队位置的坐标
    private Location getLocationByTenant(Long tenantId) {
        Location location = getLocationByTenantId(tenantId);
        if (location == null) {
            try {
                SysTenantVo tenant = sysTenantDefService.getTenantById(tenantId);

                if (tenant != null && tenant.getCityId() != null && tenant.getDistrictId() != null) {
                    String cityName = "";
                    String districtName = "";
                    if (tenant.getCityId() != null) {
                        cityName = getSysStaticData(SYS_CITY, tenant.getCityId().toString()).getCodeName();
                    }
                    if (tenant.getDistrictId() != null) {
                        districtName = getSysStaticData(SYS_DISTRICT, tenant.getDistrictId().toString()).getCodeName();
                    }
                    Map map = GpsUtil.getaddressLocation(cityName, districtName, tenant.getAddress());
                    if (map.containsKey("lng") && map.containsKey("lat")) {
                        location = new Location(Double.parseDouble(map.get("lng").toString()), Double.parseDouble(map.get("lat").toString()));
//                        redisUtil.hset("TENANT_LOCATION", String.valueOf(tenantId), location.getLongitude() + "|" + location.getLatitude());
                    }
                }
            } catch (Exception e) {
                log.error("查询租户信息错误！", e);
            }
        }
        return location;
    }

    private Location getLocationByTenantId(Long tenantId) {
        String locationStr = (String) redisUtil.hget("TENANT_LOCATION", String.valueOf(tenantId));

        if (StringUtils.isNotBlank(locationStr)) {
            String[] arr = locationStr.split("|");
            if (arr.length == 2) {
                return new Location(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            }
        }

        return null;
    }

    private String[] getPositionByBillId(String billId, String platNumber, Integer locationServ, String equipmentCode) {
        String[] returnInfo = null;
        String position = "";
        if (locationServ != null && locationServ.intValue() == OrderConsts.GPS_TYPE.G7) {
            if (StringUtils.isNotEmpty(equipmentCode)) {
                Object o = redisUtil.get(EnumConsts.RemoteCache.Vehicle_G7_Position + equipmentCode);
                if (o != null) position = o.toString();
            } else {
                return null;
            }
        } else if (locationServ != null && locationServ.intValue() == OrderConsts.GPS_TYPE.YL) {
            Object o = redisUtil.get(EnumConsts.RemoteCache.Vehicle_YL_Position + platNumber);
            if (o != null) position = o.toString();
        } else if (locationServ != null && locationServ.intValue() == OrderConsts.GPS_TYPE.BD) {
            Object o = redisUtil.get(EnumConsts.RemoteCache.Vehicle_BD_Position + platNumber);
            if (o != null) position = o.toString();
        } else {
            Object o = redisUtil.get(EnumConsts.RemoteCache.Vehicle_Gps_Position + billId);
            if (o != null) position = o.toString();
        }

        if (StringUtils.isNotEmpty(position)) {
            //记录车辆当前位置信息(key:VGP_随车手机,value:纬度|经度|时间)
            String[] pos = position.split("\\|");
            if (pos != null && pos.length >= 3) {
                returnInfo = new String[3];
                returnInfo[0] = pos[0];
                returnInfo[1] = pos[1];
                if (pos.length >= 5) {
                    returnInfo[2] = pos[4];
                } else {
                    returnInfo[2] = pos[2];
                }

            }
        }
        return returnInfo;
    }

    private LoginInfo getLoginInfo(String accessToken) {
        LoginInfo loginInfo = null;
        if (redisUtil.hasKey("loginInfo:" + accessToken)) {
            loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        }
        return loginInfo;
    }

    private SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    public List<EventsInfoDto> getEventInfoOuts(Long tenantId) {
        if (tenantId == null || tenantId < 1) {
            return null;
        }
        // 车辆对应事件
        Set<Object> obj = null;
        Set<Object> res = redisUtil.sgetAll("VEHICLE_EVENTS_" + tenantId);
        if (res != null && res.size() > 0) {
            obj = res;
        }
        Set<String> set = new HashSet<>();

        if (set != null) {
            try {
                BeanUtils.copyProperties(set, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<EventsInfoDto> list = new ArrayList<EventsInfoDto>();
            for (String str : set) {
                list.add(JSON.parseObject(str, EventsInfoDto.class));
            }
            return list;
        }
        return null;
    }

    /**
     * 获取车辆异常详情
     *
     * @param plateNumber 车牌号
     */
    public EventsInfoDto getEventsByVehicle(String plateNumber, Long tenantId) {
        //查找本租户下面的时效事件
        List<EventsInfoDto> eventInfos = getEventInfoOuts(tenantId);
        if (eventInfos == null) {
            return null;
        }
        Iterator<EventsInfoDto> it = eventInfos.iterator();
        EventsInfoDto eventsInfoOut = null;
        while (it.hasNext()) {
            eventsInfoOut = it.next();
            if (eventsInfoOut.getEventOrderInfo() != null && plateNumber.equals(eventsInfoOut.getEventOrderInfo().getPlateNumber())) {
                return eventsInfoOut;
            }
        }
        return null;
    }

    /***
     * 获取车辆最后一单的订单信息
     */
    private Map<String, Object> getLastOrderInfo(int type, String plateNumber, Long tenandId) {
        Map<String, Object> map = this.getOrderScheduler(type, plateNumber, tenandId);
        if (map != null) {
            Long orderId = DataFormat.getLongKey(map, "orderId");
            if (OrderConsts.TableType.ORI == DataFormat.getIntKey(map, "type")) {
                OrderGoods orderGoods = iOrderGoodsService.getOneGoodInfo(orderId);
                if (orderGoods != null) {
                    map.put("customName", orderGoods.getCustomName());
                    map.put("eand", orderGoods.getEand());
                    map.put("nand", orderGoods.getNand());
                    map.put("eandDes", orderGoods.getEandDes());
                    map.put("nandDes", orderGoods.getNandDes());
                    map.put("localPhone", orderGoods.getLocalPhone());
                    map.put("localUserName", orderGoods.getLocalUserName());
                }

                LambdaQueryWrapper<OrderInfo> orderInfoQueryWrapper = new LambdaQueryWrapper<>();
                orderInfoQueryWrapper.eq(OrderInfo::getOrderId, orderId);
                OrderInfo orderInfo = orderInfoMapper.selectList(orderInfoQueryWrapper).get(0);

                if (orderInfo != null) {
                    map.put("sourceRegion", orderInfo.getSourceRegion());
                    map.put("desRegion", orderInfo.getDesRegion());
                }
                OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
                if (orderScheduler != null) {
                    map.put("carDepartureDate", orderScheduler.getCarDepartureDate());
                    map.put("carDependDate", orderScheduler.getCarDependDate());
                    map.put("dependTime", orderScheduler.getDependTime());
                    map.put("carArriveDate", orderScheduler.getCarArriveDate());
                    map.put("carStartDate", orderScheduler.getCarStartDate());
                    map.put("createDate", orderScheduler.getCreateTime());
                }
            } else {
                OrderGoodsH orderGoods = iOrderGoodsHService.getOneGoodInfoH(DataFormat.getLongKey(map, "orderId"));
                if (orderGoods != null) {
                    map.put("customName", orderGoods.getCustomName());
                    map.put("eand", orderGoods.getEand());
                    map.put("nand", orderGoods.getNand());
                    map.put("eandDes", orderGoods.getEandDes());
                    map.put("nandDes", orderGoods.getNandDes());
                    map.put("localPhone", orderGoods.getLocalPhone());
                    map.put("localUserName", orderGoods.getLocalUserName());
                }

                LambdaQueryWrapper<OrderInfoH> orderInfoHQueryWrapper = new LambdaQueryWrapper<>();
                orderInfoHQueryWrapper.eq(OrderInfoH::getOrderId, orderId);
                OrderInfoH orderInfoH = orderInfoHMapper.selectList(orderInfoHQueryWrapper).get(0);

                if (orderInfoH != null) {
                    map.put("sourceRegion", orderInfoH.getSourceRegion());
                    map.put("desRegion", orderInfoH.getDesRegion());
                }

                OrderSchedulerH orderScheduler = iOrderSchedulerHService.getOrderSchedulerH(orderId);
                if (orderScheduler != null) {
                    map.put("carDepartureDate", orderScheduler.getCarDepartureDate());
                    map.put("carDependDate", orderScheduler.getCarDependDate());
                    map.put("dependTime", orderScheduler.getDependTime());
                    map.put("carArriveDate", orderScheduler.getCarArriveDate());
                    map.put("carStartDate", orderScheduler.getCarStartDate());
                    map.put("createDate", orderScheduler.getCreateTime());
                }
            }
        }
        return map;
    }

    private Map<String, Object> getOrderScheduler(int type, String plateNumber, Long tenantId) {
        Map<String, Object> map = null;
        if (type != 6) {
            map = iOrderSchedulerService.getPreOrderIdByPlateNumber(plateNumber, null, tenantId, null);
        } else {
            map = iOrderSchedulerService.getPreOrderIdByTrailerPlate(plateNumber, tenantId);
        }
        return map;
    }

    /**
     * 根据车牌获取车辆信息
     */
    private Map getVehicleInfo(String plateNumber, Long tenantId) {
        List<Map> list = this.getVehicleList(null, plateNumber, null, tenantId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    // ------------------------------- 异常大屏-订单时效监控 --------------------------------------
    private void calculateAging() {
        List<SysTenantDef> sysTenantDefs = sysTenantDefService.querySysTenantDef(null);
        if (sysTenantDefs != null && sysTenantDefs.size() > 0) {
            //上次进程执行时间
            String taskDate = null;
            Object obj = redisUtil.get(EnumConsts.RemoteCache.MONITOR_TASK_DATE);
            if (obj != null) {
                taskDate = obj.toString();
            }

            Date lastTaskDate = null;
            try {
                lastTaskDate = StringUtils.isBlank(taskDate) ? null : DateUtil.formatStringToDate(taskDate, DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //异常大屏时效监控-到靠台地速度(米)/min
            Double dependDistance = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_DEPEND_DISTANCE", 0, String.class, null) == null ? 0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_DEPEND_DISTANCE", 0, String.class, null) + "");
            //异常大屏时效监控-靠台前计算时限(小时)
            Double dependSurplusTime = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_DEPEND_SURPLUS_TIME", 0, String.class, null) == null ? 0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_DEPEND_SURPLUS_TIME", 0, String.class, null) + "");

            //异常大屏时效监控-行驶里程限制(米)
            Double travelMileageLimit = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_TRAVEL_MILEAGE_LIMINT", 0, String.class, null) == null ? 0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_TRAVEL_MILEAGE_LIMINT", 0, String.class, null) + "");

            //异常大屏时效监控-行驶占比
            Double moitorTravelRatio = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_TRAVEL_RATIO", 0, String.class, null) == null ? 0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_TRAVEL_RATIO", 0, String.class, null) + "");
            //异常大屏时效监控-异常时间计算时间
            Double moitorAgingCalculateTime = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_AGING_CALCULATE_TIME", 0, String.class, null) == null ? 0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_AGING_CALCULATE_TIME", 0, String.class, null) + "");

            //计算各个车队订单时效
            for (SysTenantDef sysTenantDef : sysTenantDefs) {
                if (sysTenantDef.getVirtualState() == null || sysTenantDef.getVirtualState() == 0) {
                    //不是临时车队
                    List<OrderInfo> orderInfos = orderInfoService.queryOrderInfoByTenantId(sysTenantDef.getId());
                    if (orderInfos != null && orderInfos.size() > 0) {
                        for (OrderInfo orderInfo : orderInfos) {
                            try {
                                Boolean isShiftHis = false;
                                boolean isDeleteArrive = false;
                                boolean isDeleteDepend = false;
                                Long orderId = orderInfo.getOrderId();

                                String isHisOrderAging = null;
                                Object isHisOrderAgingObj = redisUtil.get(EnumConsts.RemoteCache.IS_HIS_ORDER_AGING + orderId + "");
                                if (isHisOrderAgingObj != null) {
                                    isHisOrderAging = isHisOrderAgingObj.toString();
                                }

                                if (StringUtils.isNotBlank(isHisOrderAging) && Boolean.parseBoolean(isHisOrderAging)) {
                                    continue;
                                }
                                //本次订单异常状态
                                int currAbnormalType = OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE1;
                                OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderInfo.getOrderId());
                                OrderGoods orderGoods = iOrderGoodsService.getOrderGoods(orderId);
                                //获取车辆中绑定GPS类型
                                int gpsType = iOrderSchedulerService.queryVehicleGpsType(orderScheduler.getVehicleCode());
                                String realNand = "";
                                String realEand = "";
                                String equipmentNumber = "";
                                //获取当前所在经纬度
                                Map locationMap = this.setRealLocation(gpsType, orderScheduler, orderId);
                                realNand = DataFormat.getStringKey(locationMap, "sourceNand");
                                realEand = DataFormat.getStringKey(locationMap, "sourceEand");
                                equipmentNumber = DataFormat.getStringKey(locationMap, "equipmentNumber");
                                if (StringUtils.isBlank(realNand) || StringUtils.isBlank(realEand)) {
                                    continue;
                                }
                                if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                                    if ((getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime() - new Date().getTime()) > dependSurplusTime * 3600.0 * 1000.0) {
                                        //距离订单要求靠台时间还剩余1小时开始进入统计
                                        continue;
                                    }
                                    Map distanceMap = GpsUtil.getDirectionV2(Double.parseDouble(realEand), Double.parseDouble(realNand),
                                            Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()), null, 0);
                                    // 返回的单位是米
                                    Long surolusDistance = Long.valueOf(distanceMap.get("distance") + "");
                                    if (new Date().getTime() <= getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime()) {
                                        Double surolusDate = ((getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime() - new Date().getTime()) / (3600.0 * 1000.0)) * 100;
                                        // 剩余时间 * 0.5km/min
                                        Double estDistance = surolusDate * (dependDistance * 60);
                                        if (surolusDistance > estDistance.longValue()) {//
                                            Double time = dependDistance == 0 ? 0 : ((surolusDistance * 1.0) / (dependDistance * 1.0) / 60.0);
                                            LocalDateTime estDependDate = orderDateUtil.addHourAndMins(LocalDateTime.now(), time.floatValue());
                                            this.setEstDependInfo(orderId, orderScheduler.getPlateNumber(), estDependDate, orderScheduler.getDependTime(),
                                                    realEand, realNand, orderGoods.getEand(), orderGoods.getNand(),
                                                    orderGoods.getAddrDtl(), orderInfo.getTenantId(), surolusDistance, surolusDate, 0, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE1);
                                        }
                                    } else if (new Date().getTime() > getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime()) {
                                        //若是有晚靠台则覆盖 否则 新建晚靠台
                                        Double surolusDate = ((new Date().getTime() - getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime()) / (3600.0 * 1000.0)) * 100;
                                        Double time = dependDistance == 0 ? 0 : ((surolusDistance * 1.0) / (dependDistance * 1.0) / 60.0);
                                        LocalDateTime estDependDate = orderDateUtil.addHourAndMins(LocalDateTime.now(), time.floatValue());
                                        this.setEstDependInfo(orderId, orderScheduler.getPlateNumber(), estDependDate, orderScheduler.getDependTime(),
                                                realEand, realNand, orderGoods.getEand(), orderGoods.getNand(),
                                                orderGoods.getAddrDtl(), orderInfo.getTenantId(), surolusDistance, surolusDate, 0, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE2);
                                    }
                                } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TRANSPORT_ING) {
                                    Map<String, Object> map = iOrderSchedulerService.queryOrderTrackLocation(orderId);
                                    if (map != null) {
                                        Integer trackType = DataFormat.getIntKey(map, "trackType");
                                        Integer lineType = DataFormat.getIntKey(map, "lineType");
                                        String eand = DataFormat.getStringKey(map, "eand");
                                        String nand = DataFormat.getStringKey(map, "nand");
                                        String lineDetail = DataFormat.getStringKey(map, "lineDetail");
                                        LocalDateTime estDate = map.get("estDate") == null ? null : (LocalDateTime) map.get("estDate");
                                        //经停点离台使用
                                        LocalDateTime realDependDate = map.get("realDependDate") == null ? null : (LocalDateTime) map.get("realDependDate");
                                        //获取订单上次异常事件类型
                                        String abnormal = redisUtil.get(EnumConsts.RemoteCache.MONITOR_ORDER_ABNORMAL_TYPE + orderId).toString();
                                        int abnormalType = OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE1;
                                        String abnormalInfo = redisUtil.get(EnumConsts.RemoteCache.MONITOR_ORDER_ABNORMAL_INFO + orderId).toString();
                                        Date lastStartDate = null;
                                        String lastStartNand = null;
                                        String lastStartEand = null;
                                        if (org.apache.commons.lang3.StringUtils.isNoneBlank(abnormalInfo)) {
                                            String[] abnormalInfos = abnormalInfo.split("\\|");
                                            lastStartDate = DateUtil.formatStringToDate(abnormalInfos[0], DateUtil.DATETIME_FORMAT);
                                            lastStartEand = abnormalInfos[2];
                                            lastStartNand = abnormalInfos[3];
                                        } else {
                                            //初始化离台时间
                                            Map lastStartMap = iOrderSchedulerService.queryLastStartInfo(orderId, lineType);
                                            lastStartDate = lastStartMap.get("lastDate") == null ? null : (Date) lastStartMap.get("lastDate");
                                            lastStartNand = lastStartMap.get("nand") == null ? null : String.valueOf(lastStartMap.get("nand"));
                                            lastStartEand = lastStartMap.get("eand") == null ? null : String.valueOf(lastStartMap.get("eand"));
                                        }
                                        if (org.apache.commons.lang3.StringUtils.isNotBlank(abnormal)) {
                                            abnormalType = Integer.parseInt(abnormal);
                                        }
                                        if (trackType == OrderConsts.TRACK_TYPE.TYPE1) {//靠台
                                            //预估晚靠台
                                            Map distanceMap = GpsUtil.getDirectionV2(Double.parseDouble(realEand), Double.parseDouble(realNand),
                                                    Double.parseDouble(eand), Double.parseDouble(nand), null, 0);
                                            // 返回的单位是米
                                            Long surolusDistance = Long.valueOf(distanceMap.get("distance") + "");
                                            if (new Date().getTime() <= getLocalDateTimeToDate(estDate).getTime()) {
                                                Double surolusDate = ((getLocalDateTimeToDate(estDate).getTime()) - new Date().getTime() / (3600.0 * 1000.0)) * 100;
                                                // 剩余时间 * 0.5km/min
                                                Double estDistance = surolusDate * (dependDistance * 60);
                                                //剩余时间 * 0.5km/min
                                                if (surolusDistance > estDistance.longValue()) {//
                                                    Double time = dependDistance == 0 ? 0 : ((surolusDistance * 1.0) / (dependDistance * 1.0) / 60.0);
                                                    LocalDateTime estDependDate = orderDateUtil.addHourAndMins(LocalDateTime.now(), time.floatValue());
                                                    this.setEstDependInfo(orderId, orderScheduler.getPlateNumber(), estDependDate, estDate,
                                                            realEand, realNand, eand, nand,
                                                            lineDetail, orderInfo.getTenantId(),
                                                            surolusDistance, surolusDate, lineType, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE1);
                                                }
                                            } else if (new Date().getTime() > getLocalDateTimeToDate(estDate).getTime()) {
                                                Double surolusDate = ((new Date().getTime() - getLocalDateTimeToDate(estDate).getTime()) / (3600.0 * 1000.0)) * 100;
                                                Double time = dependDistance == 0 ? 0 : ((surolusDistance * 1.0) / (dependDistance * 1.0) / 60.0);
                                                LocalDateTime estDependDate = orderDateUtil.addHourAndMins(LocalDateTime.now(), time.floatValue());
                                                this.setEstDependInfo(orderId, orderScheduler.getPlateNumber(), estDependDate, estDate,
                                                        realEand, realNand, eand, nand,
                                                        orderGoods.getAddrDtl(), orderInfo.getTenantId(), surolusDistance, surolusDate, lineType, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE2);
                                            }
                                            //查询是否缓行 异常停留
                                            if (lastStartDate != null && new Date().getTime() - lastStartDate.getTime() >= moitorAgingCalculateTime * 60 * 60 * 1000) {
                                                this.setAbnormalInfo(orderId, orderScheduler.getPlateNumber(), orderInfo.getTenantId(), getDateToLocalDateTime(lastStartDate), abnormalType
                                                        , lastStartEand, lastStartNand, realEand, realNand, lineDetail, lineType, currAbnormalType);
                                            }
                                        } else if (trackType == OrderConsts.TRACK_TYPE.TYPE2) {//离台
                                            //实际晚靠台  需取实际靠台时间 以及预估靠台时间
                                            LocalDateTime lastEstDate = iOrderSchedulerService.getOrderEstimateDate(orderId, orderScheduler.getDependTime(), orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false, lineType);
                                            if (lastEstDate != null && realDependDate != null) {
                                                if (getLocalDateTimeToDate(lastEstDate).getTime() < getLocalDateTimeToDate(realDependDate).getTime()) {
                                                    this.setRealDependInfo(orderId, orderScheduler.getPlateNumber(), realDependDate, lastEstDate,
                                                            eand, nand, eand, nand,
                                                            lineDetail, orderInfo.getTenantId(), 0l, lineType);
                                                }
                                            }

                                        } else if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {//到达
                                            //查询是否缓行 异常停留
                                            if (lastStartDate != null && new Date().getTime() - lastStartDate.getTime() >= moitorAgingCalculateTime * 60 * 60 * 1000) {
                                                this.setAbnormalInfo(orderId, orderScheduler.getPlateNumber(), orderInfo.getTenantId(), getDateToLocalDateTime(lastStartDate), abnormalType
                                                        , lastStartEand, lastStartNand, realEand, realNand, lineDetail, lineType, currAbnormalType);
                                            }
                                        } else {
                                            continue;
                                        }
                                        if (orderScheduler.getMileageNumber() > travelMileageLimit) {//订单的公里数小于配置项
                                            LocalDateTime carStartDate = orderScheduler.getCarStartDate();
                                            List<OrderTransitLineInfo> transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
                                            Double arriveTime = orderScheduler.getArriveTime().doubleValue();
                                            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                                                for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                                                    arriveTime += orderTransitLineInfo.getArriveTime().doubleValue();
                                                    if (carStartDate == null && orderTransitLineInfo.getCarStartDate() != null) {
                                                        carStartDate = orderTransitLineInfo.getCarStartDate();
                                                    }
                                                }
                                            }
                                            if (carStartDate != null) {
                                                Long orderTravlledDistance = this.calculateOrderTravlledDistance(carStartDate, new Date(), equipmentNumber);
                                                if (orderTravlledDistance > travelMileageLimit) {//行驶公里数大于配置项
                                                    LocalDateTime estArriveDate = orderInfoExtService.getOrderArriveDate(orderId, orderScheduler.getDependTime(), orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false);
                                                    if (new Date().getTime() <= getLocalDateTimeToDate(estArriveDate).getTime()) {
                                                        //预估到达
                                                        Double useHour = CommonUtil.getDoubleFormat((new Date().getTime() - getLocalDateTimeToDate(carStartDate).getTime()) / (1.0 * 3600 * 1000), 2);
                                                        //计算当前行驶占比
                                                        Integer orderMileageNumber = orderScheduler.getMileageNumber();
                                                        Double timeRatio = CommonUtil.getDoubleFormat(useHour / arriveTime, 4);
                                                        Double ratio = CommonUtil.getDoubleFormat(orderTravlledDistance * 1.0 / orderMileageNumber * 1.0, 4);
                                                        if ((ratio / timeRatio) > moitorTravelRatio) {//(行驶公里数/订单里程) /（行驶时间/时效) >配置项
                                                            String timeLimitRatio = useHour + "h/" + arriveTime + "h(" + (CommonUtil.getDoubleFormat(timeRatio * 100, 2)) + "%)";
                                                            String mileageRatio = CommonUtil.getDoubleFormat(orderTravlledDistance / 1000.0, 3) + "km/"
                                                                    + CommonUtil.getDoubleFormat(orderMileageNumber / 1000.0, 3) + "km(" + (CommonUtil.getDoubleFormat(ratio * 100, 2)) + "%)";
                                                            this.setEstArriveInfo(orderId, orderScheduler.getPlateNumber(), estArriveDate,
                                                                    LocalDateTime.now(), realEand, realNand,
                                                                    orderGoods.getEandDes(), orderGoods.getNandDes(), orderGoods.getDesDtl(),
                                                                    orderInfo.getTenantId(), OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1
                                                                    , timeLimitRatio, mileageRatio);
                                                        }
                                                    } else if (new Date().getTime() > getLocalDateTimeToDate(estArriveDate).getTime()) {
                                                        //迟到
                                                        this.setEstArriveInfo(orderId, orderScheduler.getPlateNumber(), estArriveDate,
                                                                LocalDateTime.now(), realEand, realNand,
                                                                orderGoods.getEandDes(), orderGoods.getNandDes(), orderGoods.getDesDtl(),
                                                                orderInfo.getTenantId(), OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2
                                                                , null, null);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (orderInfo.getOrderState().intValue() == OrderConsts.ORDER_STATE.ARRIVED) {
                                    LocalDateTime estArriveDate = orderInfoExtService.getOrderArriveDate(orderId, orderScheduler.getDependTime(), orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false);
                                    if (getLocalDateTimeToDate(orderScheduler.getCarArriveDate()).getTime() > getLocalDateTimeToDate(estArriveDate).getTime()) {
                                        //晚到达
                                        this.setRealArriveInfo(orderId, orderScheduler.getPlateNumber(), estArriveDate, orderScheduler.getCarArriveDate(),
                                                orderGoods.getEandDes(), orderGoods.getNandDes(), orderGoods.getEandDes(), orderGoods.getNandDes(),
                                                orderGoods.getDesDtl(), orderInfo.getTenantId());
                                    } else {
                                        isDeleteArrive = true;
                                    }
                                    isShiftHis = true;
                                }
                                if (orderInfo.getOrderState().intValue() > OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                                    if (orderScheduler.getCarDependDate() != null && getLocalDateTimeToDate(orderScheduler.getCarDependDate()).getTime() > getLocalDateTimeToDate(orderScheduler.getDependTime()).getTime()) {
                                        this.setRealDependInfo(orderId, orderScheduler.getPlateNumber(), orderScheduler.getCarDependDate(), orderScheduler.getDependTime(),
                                                orderGoods.getEand(), orderGoods.getNand(), orderGoods.getEand(), orderGoods.getNand(),
                                                orderGoods.getAddrDtl(), orderInfo.getTenantId(), 0l, 0);
                                    } else {//删除预估晚靠台
                                        isDeleteDepend = true;
                                    }
                                }
                                // 事务手动提交
//                                platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                                if (isDeleteArrive) {
                                    iMonitorOrderAgingArriveService.deleteMonitorOrderAgingArrive(orderId, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1);
                                    iMonitorOrderAgingArriveService.deleteMonitorOrderAgingArrive(orderId, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2);
                                }
                                if (isDeleteDepend) {
                                    iMonitorOrderAgingDependService.deleteMonitorOrderAgingDepend(orderId, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE1, 0);
                                    iMonitorOrderAgingDependService.deleteMonitorOrderAgingDepend(orderId, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE2, 0);
                                }
                                if (isShiftHis) {
                                    //移入历史
                                    this.createAgingHis(orderId);
                                    redisUtil.set(EnumConsts.RemoteCache.IS_HIS_ORDER_AGING + orderId + "", "true");
                                }
                                // 事务手动提交
//                                platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 事务手动回滚
//                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                continue;
                            }
                        }
                    }
                }
            }
            lastTaskDate = new Date();
            redisUtil.setex(EnumConsts.RemoteCache.MONITOR_TASK_DATE, DateUtil.formatDate(lastTaskDate, DateUtil.DATETIME_FORMAT), 15 * 24 * 60 * 60);
        }
    }

    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 设置预估靠台时效
     */
    private void setEstDependInfo(Long orderId, String plateNumber, LocalDateTime carDependTime, LocalDateTime dependTime, String currEand, String currNand
            , String dependEand, String dependNand, String lineDetail, Long tenantId, Long surolusDistance, Double surolusDate
            , Integer lineType, Integer type) {
        //预估晚靠台
        List<MonitorOrderAgingDepend> agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE1, lineType, null);
        MonitorOrderAgingDepend agingDepend = new MonitorOrderAgingDepend();
        if (agingDepends != null && agingDepends.size() > 0) {
            agingDepend = agingDepends.get(0);
        } else {
            if (type == OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE2) {
                agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE2, lineType, null);
                if (agingDepends != null && agingDepends.size() > 0) {
                    agingDepend = agingDepends.get(0);
                } else {
                    agingDepend.setCreateTime(LocalDateTime.now());
                }
            } else {
                agingDepend.setCreateTime(LocalDateTime.now());
            }
        }
        agingDepend.setPlateNumber(plateNumber);
        agingDepend.setType(type);
        agingDepend.setLineType(lineType);
        agingDepend.setCarDependTime(carDependTime);
        agingDepend.setDependTime(dependTime);
        agingDepend.setUpdateTime(LocalDateTime.now());
        agingDepend.setDependEand(dependEand);
        agingDepend.setDependNand(dependNand);
        agingDepend.setCurrEand(currEand);
        agingDepend.setCurrNand(currNand);
        agingDepend.setLineDetail(lineDetail);
        agingDepend.setTenantId(tenantId);
        agingDepend.setOrderId(orderId);
        agingDepend.setSurplusDate(surolusDate.longValue());
        agingDepend.setSurplusDistance(surolusDistance);
        iMonitorOrderAgingDependService.saveOrUpdate(agingDepend);
    }

    /**
     * 设置异常事件
     *
     * @param lastTaskDate     上次进程执行时间
     * @param abnormalType     上次异常状态
     * @param lineType         线路类型 0 目的地  > 0 经停点标识
     * @param currAbnormalType 当前异常状态(用于更新)
     */
    private void setAbnormalInfo(Long orderId, String plateNumber, Long tenantId, LocalDateTime lastTaskDate, Integer abnormalType, String startEand, String startNand
            , String endEand, String endNand, String lineDetail, Integer lineType, Integer currAbnormalType) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(startNand) && org.apache.commons.lang3.StringUtils.isNotBlank(startEand)) {
            //异常大屏时效监控-最小行驶速度(米)/min
            Double speedMinuteMin = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_SPEED_MINUTE_MIN", 0, String.class, null) == null ?
                    0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_SPEED_MINUTE_MIN", 0, String.class, null) + "");

            //异常大屏时效监控-最大行驶速度(米)/min
            Double speedMinuteMax = sysCfgRedisUtils.getCfgVal(-1, "MONITOR_SPEED_MINUTE_MAX", 0, String.class, null) == null ?
                    0 : Double.parseDouble(sysCfgRedisUtils.getCfgVal(-1, "MONITOR_SPEED_MINUTE_MAX", 0, String.class, null) + "");

            //有上一点
            Map distanceMap = GpsUtil.getDirectionV2(Double.parseDouble(startEand), Double.parseDouble(startNand),
                    Double.parseDouble(endEand), Double.parseDouble(endNand), null, 0);
            // 返回的单位是米
            Long surolusDistance = Long.valueOf(distanceMap.get("distance") + "");
            double travelTime = CommonUtil.getDoubleFormat((new Date().getTime() - getLocalDateTimeToDate(lastTaskDate).getTime()) / (1.0 * 60 * 60 * 1000), 2);
            double speedMinute = surolusDistance / travelTime;
            if (speedMinute < speedMinuteMin) {//异常停留
                currAbnormalType = OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE3;
            } else if (speedMinute > speedMinuteMin && speedMinute < speedMinuteMax) {//行驶缓慢
                currAbnormalType = OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE2;
            }
            boolean isUpdate = false;
            MonitorOrderAgingAbnormal monitorOrderAgingAbnormal = new MonitorOrderAgingAbnormal();
            if (abnormalType == currAbnormalType && (abnormalType == OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE2
                    || abnormalType == OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE3)) {
                //上次有行驶缓慢
                List<MonitorOrderAgingAbnormal> abnormals = this.getMonitorOrderAgingAbnormal(orderId, null, abnormalType, null, null);
                if (abnormals != null && abnormals.size() > 0) {
                    monitorOrderAgingAbnormal = abnormals.get(0);
                    isUpdate = true;
                }
            }
            if (!isUpdate) {
                monitorOrderAgingAbnormal.setCreateTime(LocalDateTime.now());
                monitorOrderAgingAbnormal.setStartDate(lastTaskDate);
                monitorOrderAgingAbnormal.setStartEand(startEand);
                monitorOrderAgingAbnormal.setStartNand(startNand);
                monitorOrderAgingAbnormal.setOrderId(orderId);
                monitorOrderAgingAbnormal.setTenantId(tenantId);
            }
            if (currAbnormalType != null
                    && (currAbnormalType == OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE2
                    || currAbnormalType == OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE3)) {
                monitorOrderAgingAbnormal.setLineType(lineType);
                monitorOrderAgingAbnormal.setEndDate(LocalDateTime.now());
                monitorOrderAgingAbnormal.setEndEand(endEand);
                monitorOrderAgingAbnormal.setEndNand(endNand);
                monitorOrderAgingAbnormal.setLineDetail(lineDetail);
                monitorOrderAgingAbnormal.setPlateNumber(plateNumber);
                Double continueTime = (getLocalDateTimeToDate(monitorOrderAgingAbnormal.getEndDate()).getTime()
                        - getLocalDateTimeToDate(monitorOrderAgingAbnormal.getStartDate()).getTime()) / (1.0 * 60 * 60 * 1000) * 100;
                monitorOrderAgingAbnormal.setContinueTime(continueTime.longValue());
                monitorOrderAgingAbnormal.setType(currAbnormalType);
                monitorOrderAgingAbnormal.setUpdateTime(LocalDateTime.now());
                this.saveOrUpdate(monitorOrderAgingAbnormal);
                //存入本次异常信息
                redisUtil.setex(EnumConsts.RemoteCache.MONITOR_ORDER_ABNORMAL_TYPE + orderId, currAbnormalType, 15 * 24 * 60 * 60);
            } else {
                redisUtil.setex(EnumConsts.RemoteCache.MONITOR_ORDER_ABNORMAL_TYPE + orderId, OrderConsts.ABNORMAL_TYPE.ABNORMAL_TYPE1, 15 * 24 * 60 * 60);
            }
            redisUtil.setex(EnumConsts.RemoteCache.MONITOR_ORDER_ABNORMAL_INFO + orderId,
                    DateUtil.formatDate(new Date(), DateUtil.DATETIME_FORMAT) + "|" + lineType + "|" + endEand + "|" + endNand, 15 * 24 * 60 * 60);
        }
    }

    /**
     * 设置实际靠台时效
     */
    private boolean setRealDependInfo(Long orderId, String plateNumber, LocalDateTime carDependTime, LocalDateTime dependTime, String currEand, String currNand
            , String dependEand, String dependNand, String lineDetail, Long tenantId, Long surolusDistance, Integer lineType) {
        Boolean isContinue = false;
        List<MonitorOrderAgingDepend> agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE3, lineType, null);
        if (agingDepends != null && agingDepends.size() > 0) {//有实际晚不反写
            //本次经纬度更新
            isContinue = true;
            return isContinue;
        } else {
            agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE1, lineType, null);
            MonitorOrderAgingDepend agingDepend = new MonitorOrderAgingDepend();
            if (agingDepends != null && agingDepends.size() > 0) {//有预估
                agingDepend = agingDepends.get(0);
            } else {//有晚靠台
                agingDepends = iMonitorOrderAgingDependService.getMonitorOrderAgingDepend(orderId, null, OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE2, lineType, null);
                if (agingDepends != null && agingDepends.size() > 0) {
                    agingDepend = agingDepends.get(0);
                } else {//没有晚靠台
                    agingDepend.setCreateTime(LocalDateTime.now());
                }
            }
            agingDepend.setPlateNumber(plateNumber);
            agingDepend.setType(OrderConsts.MONITOR_DEPEND_TYPE.MONITOR_DEPEND_TYPE3);
            agingDepend.setCarDependTime(carDependTime);
            agingDepend.setDependTime(dependTime);
            agingDepend.setUpdateTime(LocalDateTime.now());
            agingDepend.setDependEand(dependEand);
            agingDepend.setDependNand(dependNand);
            agingDepend.setOrderId(orderId);
            agingDepend.setCurrEand(currEand);
            agingDepend.setCurrNand(currNand);
            agingDepend.setLineDetail(lineDetail);
            agingDepend.setTenantId(tenantId);
            agingDepend.setLineType(lineType);
            Double surolusDate = ((getLocalDateTimeToDate(carDependTime).getTime() - getLocalDateTimeToDate(dependTime).getTime()) / (3600.0 * 1000.0)) * 100;
            agingDepend.setSurplusDate(surolusDate.longValue());
            agingDepend.setSurplusDistance(0L);
            iMonitorOrderAgingDependService.saveOrUpdate(agingDepend);
        }
        return isContinue;
    }

    /**
     * 计算订单行驶距离
     */
    private Long calculateOrderTravlledDistance(LocalDateTime starDate, Date endDate, String equipmentNumber) {

        Double orderTravlledDistance = 0.0;
        List<Map> locationList = null/*locationDataSV.getLocation(DateUtil.formatDate(starDate,DateUtil.DATETIME_FORMAT),
                DateUtil.formatDate(endDate,DateUtil.DATETIME_FORMAT), equipmentNumber)*/;
        if (locationList != null && locationList.size() > 0) {
            for (int i = 0; i < locationList.size() - 1; i++) {
                if (i == 0 || i == locationList.size() - 2) {
                    continue;
                }
                Map lastMap = locationList.get(i);
                Map nextMap = locationList.get(i + 1);
                String lastNand = DataFormat.getStringKey(lastMap, "latitude");
                String lastEand = DataFormat.getStringKey(lastMap, "longitude");
                String nextNand = DataFormat.getStringKey(nextMap, "latitude");
                String nextEand = DataFormat.getStringKey(nextMap, "longitude");
                if (StringUtils.isNotBlank(lastEand) && StringUtils.isNotBlank(lastNand)
                        && StringUtils.isNotBlank(nextNand) && StringUtils.isNotBlank(nextEand)) {
                    orderTravlledDistance += GpsUtil.getDistance(Double.parseDouble(lastNand), Double.parseDouble(lastEand)
                            , Double.parseDouble(nextNand), Double.parseDouble(nextEand));
                }
            }
        }
        return orderTravlledDistance.longValue();
    }

    /**
     * 设置预估到达时效
     *
     * @param type           时效类型 预估-迟到
     * @param timeLimitRatio 时效比例
     * @param mileageRatio   里程比例
     */
    private boolean setEstArriveInfo(Long orderId, String plateNumber, LocalDateTime estArriveDate, LocalDateTime realArriveDate, String currEand, String currNand
            , String endEand, String endNand, String lineDetail, Long tenantId, Integer type, String timeLimitRatio, String mileageRatio) {
        Boolean isContinue = false;
        List<MonitorOrderAgingArrive> agingArrives = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null);
        MonitorOrderAgingArrive agingArrive = new MonitorOrderAgingArrive();
        if (agingArrives != null && agingArrives.size() > 0) {//有预估
            agingArrive = agingArrives.get(0);
        } else {
            if (type == OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2) {
                agingArrives = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null);
                if (agingArrives != null && agingArrives.size() > 0) {
                    agingArrive = agingArrives.get(0);
                } else {
                    agingArrive.setCreateTime(LocalDateTime.now());
                }
            } else {
                agingArrive.setCreateTime(LocalDateTime.now());
            }
        }
        agingArrive.setPlateNumber(plateNumber);
        agingArrive.setType(type);
        agingArrive.setEstArriveDate(estArriveDate);
        agingArrive.setRealArriveDate(realArriveDate);
        agingArrive.setUpdateTime(LocalDateTime.now());
        agingArrive.setCurrNand(currNand);
        agingArrive.setCurrEand(currEand);
        agingArrive.setEndEand(endEand);
        agingArrive.setEndNand(endNand);
        agingArrive.setOrderId(orderId);
        agingArrive.setLineDetail(lineDetail);
        agingArrive.setTenantId(tenantId);
        Double lateTime = 0.0;
        if (type == OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2) {//预估无迟到时间
            lateTime = ((getLocalDateTimeToDate(realArriveDate).getTime() - getLocalDateTimeToDate(estArriveDate).getTime()) / (3600.0 * 1000.0)) * 100;
        }
        agingArrive.setTimeLimitRatio(timeLimitRatio);
        agingArrive.setMileageRatio(mileageRatio);
        agingArrive.setLateTime(lateTime.longValue());
        iMonitorOrderAgingArriveService.saveOrUpdate(agingArrive);
        return isContinue;
    }

    /**
     * 设置实际到达时效
     */
    private boolean setRealArriveInfo(Long orderId, String plateNumber, LocalDateTime estArriveDate, LocalDateTime realArriveDate, String currEand, String currNand
            , String endEand, String endNand, String lineDetail, Long tenantId) {
        Boolean isContinue = false;

        List<MonitorOrderAgingArrive> agingArrives = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3, null);
        if (agingArrives != null && agingArrives.size() > 0) {//有实际不反写
            //本次经纬度更新
            isContinue = true;
            return isContinue;
        } else {
            agingArrives = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE1, null);
            MonitorOrderAgingArrive agingArrive = new MonitorOrderAgingArrive();
            if (agingArrives != null && agingArrives.size() > 0) {//有预估
                agingArrive = agingArrives.get(0);
            } else {
                agingArrives = iMonitorOrderAgingArriveService.getMonitorOrderAgingArrive(orderId, null, OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE2, null);
                if (agingArrives != null && agingArrives.size() > 0) {
                    agingArrive = agingArrives.get(0);
                } else {
                    agingArrive.setCreateTime(LocalDateTime.now());
                    agingArrive.setType(OrderConsts.MONITOR_ARRIVE_TYPE.ABNORMAL_TYPE3);
                    agingArrive.setEstArriveDate(estArriveDate);
                    agingArrive.setRealArriveDate(realArriveDate);
                }
            }
            agingArrive.setPlateNumber(plateNumber);
            agingArrive.setUpdateTime(LocalDateTime.now());
            agingArrive.setCurrNand(currNand);
            agingArrive.setCurrEand(currEand);
            agingArrive.setEndEand(endEand);
            agingArrive.setEndNand(endNand);
            agingArrive.setOrderId(orderId);
            agingArrive.setLineDetail(lineDetail);
            agingArrive.setTenantId(tenantId);
            Double lateTime = ((getLocalDateTimeToDate(realArriveDate).getTime() - getLocalDateTimeToDate(estArriveDate).getTime()) / (3600.0 * 1000.0)) * 100;
            agingArrive.setLateTime(lateTime.longValue());
            iMonitorOrderAgingArriveService.saveOrUpdate(agingArrive);
        }
        return isContinue;
    }

}
