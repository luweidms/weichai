package com.youming.youche.record.provider.service.impl.tenant;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.github.pagehelper.PageInfo;
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
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.record.api.tenant.ITenantVehicleCertRelService;
import com.youming.youche.record.api.tenant.ITenantVehicleCertRelVerService;
import com.youming.youche.record.api.tenant.ITenantVehicleCostRelService;
import com.youming.youche.record.api.tenant.ITenantVehicleCostRelVerService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.vehicle.ITenantVehicleRelVerService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoVerService;
import com.youming.youche.record.common.OrderConsts;
import com.youming.youche.record.domain.base.BeidouPaymentRecord;
import com.youming.youche.record.domain.etc.EtcMaintain;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRel;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRelVer;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRel;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRelVer;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import com.youming.youche.record.provider.mapper.base.BeidouPaymentRecordMapper;
import com.youming.youche.record.provider.mapper.etc.EtcMaintainMapper;
import com.youming.youche.record.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleCostRelMapper;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleRelMapper;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleRelVerMapper;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.utils.ReadisUtil;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.vo.TenantVehicleRelVo;
import com.youming.youche.record.vo.VehicleDataVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1;

/**
 * <p>
 * 车队与车辆关系表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantVehicleRelServiceImpl extends ServiceImpl<TenantVehicleRelMapper, TenantVehicleRel>
        implements ITenantVehicleRelService {


    @Lazy
    @Resource
    TenantVehicleRelMapper tenantVehicleRelMapper;

    @Lazy
    @Resource
    private BeidouPaymentRecordMapper beidouPaymentRecordMapper;

    @Lazy
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Lazy
    @Resource
    private EtcMaintainMapper etcMaintainMapper;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @Lazy
    @Resource
    private RedisUtil redisUtil;

    @Lazy
    @Resource
    private LoginUtils loginUtils;
    ;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;

    @Lazy
    @Resource
    IVehicleDataInfoService iVehicleDataInfoService;

    @Lazy
    @Resource
    IVehicleDataInfoVerService iVehicleDataInfoVerService;

    @Lazy
    @Resource
    TenantVehicleCostRelMapper tenantVehicleCostRelMapper;

    @Lazy
    @Resource
    ITenantVehicleCostRelVerService iTenantVehicleCostRelVerService;

    @Lazy
    @Resource
    ITenantVehicleCostRelService iTenantVehicleCostRelService;

    @Lazy
    @Resource
    TenantVehicleRelVerMapper tenantVehicleRelVerMapper;

    @Lazy
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;


    @Resource
    ITenantVehicleCertRelService iTenantVehicleCertRelService;

    @Resource
    ITenantVehicleCertRelVerService iTenantVehicleCertRelVerService;

    @Resource
    ITenantVehicleRelVerService iTenantVehicleRelVerService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @Resource
    UserDataInfoRecordMapper userDataInfoRecordMapper;

    @Override
    public Long saveById(TenantVehicleRel tenantVehicleRel) {
        super.save(tenantVehicleRel);
        return tenantVehicleRel.getId();
    }

    @Override
    public List<TenantVehicleRel> getTenantVehicleRelList(long vehicleCode, Long applyTenantId) {

        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("VEHICLE_CODE", vehicleCode);
        if (applyTenantId != null && applyTenantId >= 0) {
            queryWrapper.eq("TENANT_ID", applyTenantId);
        }
        return tenantVehicleRelMapper.selectList(queryWrapper);
    }

    @Override
    public List<TenantVehicleRel> getTenantVehicleRelList(long vehicleCode) {
        if (vehicleCode < 0) {
            throw new BusinessException("参数错误,车辆编号不能为空");
        }
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("VEHICLE_CODE", vehicleCode)
                .orderByAsc("id");
        return tenantVehicleRelMapper.selectList(queryWrapper);
    }

    @Override
    public TenantVehicleRel getZYVehicleByVehicleCode(long vehicleCode) {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("VEHICLE_CODE", vehicleCode).eq("VEHICLE_CLASS", SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1);
        List<TenantVehicleRel> list = tenantVehicleRelMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TenantVehicleRel getTenantVehicleRel(long vehicleCode, long tenantId) {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("VEHICLE_CODE", vehicleCode).eq("TENANT_ID", tenantId);
        List<TenantVehicleRel> list = tenantVehicleRelMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TenantVehicleRel getTenantVehiclesRel(Long vehicleCode, Integer vehicleClass) {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("VEHICLE_CODE", vehicleCode).eq("VEHICLE_CLASS", vehicleClass);
        List<TenantVehicleRel> list = tenantVehicleRelMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TenantVehicleRel getTenantVehicleRel(long vehicleCode) {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("VEHICLE_CODE", vehicleCode);
        List<TenantVehicleRel> list = tenantVehicleRelMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Page<TenantVehicleRelVo> doQueryVehicleAll(String plateNumber, String linkman, String mobilePhone, String billReceiverMobile, String linkManName,
                                                      String linkPhone, String vehicleLength, String tenantName, Integer vehicleStatus, String startTime,
                                                      String endTime, Integer authStateIn, Integer shareFlgIn, Integer isAuth, Integer vehicleClass, Integer vehicleGps,
                                                      String bdEffectDate, String bdInvalidDate, String accessToken, Integer pageNum,
                                                      Integer pageSize) {
        Page<TenantVehicleRelVo> page = new Page<>(pageNum, pageSize);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (isAuth != null && isAuth == 1) {
            Page<TenantVehicleRelVo> pageData = new Page<>(1, 500);
            Page<TenantVehicleRelVo> vehicleRelVoPage = doQueryVehicleAllAndSetData(loginInfo.getTenantId(), plateNumber, linkman, mobilePhone, billReceiverMobile, linkManName, linkPhone, vehicleLength,
                    tenantName, authStateIn, shareFlgIn, -1, vehicleClass, vehicleGps, vehicleStatus, pageData);
            List<TenantVehicleRelVo> records = vehicleRelVoPage.getRecords();
            if (records != null && records.size() > 0) {
                List<Long> longs = records.stream().map(a -> a.getEid()).collect(Collectors.toList());
                //设置是否有审核权限
                if (CollectionUtils.isNotEmpty(longs)) {
                    Map<Long, Boolean> hasPermission = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, longs, accessToken);
                    for (TenantVehicleRelVo tenantVehicleRelVo : records) {
                        Boolean flg = hasPermission.get(tenantVehicleRelVo.getEid());
                        try {
                            tenantVehicleRelVo.setIsAuth(flg ? 1 : 0);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
                List<TenantVehicleRelVo> collect = records.stream().filter(t -> Objects.equals(t.getIsAuth(), isAuth))
                        .sorted(Comparator.comparing(TenantVehicleRelVo::getCreateTime).reversed()).collect(Collectors.toList());
                List<TenantVehicleRelVo> vehicleRelVos = collect.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
                page.setRecords(vehicleRelVos);
                page.setTotal(collect.size());
                Page<TenantVehicleRelVo> vehicleRelVo = getTenantVehicleRelVo(bdEffectDate, bdInvalidDate, page,loginInfo);
                return getBedou(bdEffectDate, bdInvalidDate, vehicleRelVo);
            }
            return page;
        }
        //分页查询我的车库
        Page<TenantVehicleRelVo> vehicleRelVoPage = doQueryVehicleAllAndSetData(loginInfo.getTenantId(), plateNumber, linkman, mobilePhone, billReceiverMobile, linkManName, linkPhone, vehicleLength,
                tenantName, authStateIn, shareFlgIn, isAuth, vehicleClass, vehicleGps, vehicleStatus, page);
        List<TenantVehicleRelVo> tenantVehicleRelVoList = vehicleRelVoPage.getRecords();
        //过滤购买了北斗服务的车辆
        List<Long> beiDouVehicleList = Lists.newArrayList();
        if (StringUtils.isNotBlank(bdEffectDate) || StringUtils.isNotBlank(bdInvalidDate)) {
            List<BeidouPaymentRecord> beidouPaymentRecordList = beidouPaymentRecordMapper.queryBeiDouVehicleList(bdEffectDate, bdInvalidDate);
            if (beidouPaymentRecordList == null || beidouPaymentRecordList.size() == 0) {
                vehicleRelVoPage.setRecords(null);
                vehicleRelVoPage.setTotal(0);
            }
            for (BeidouPaymentRecord beidouPaymentRecord : beidouPaymentRecordList) {
                beiDouVehicleList.add(beidouPaymentRecord.getVehicleCode());
            }
        }

        if (beiDouVehicleList.size() > 0) {
            List<TenantVehicleRelVo> tenantVehicleRelVoListNew = Lists.newArrayList();
            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                if (beiDouVehicleList.contains(tenantVehicleRelVo.getVehicleCode())) {
                    tenantVehicleRelVoListNew.add(tenantVehicleRelVo);
                }
            }
            vehicleRelVoPage.setRecords(tenantVehicleRelVoListNew);
            vehicleRelVoPage.setTotal(tenantVehicleRelVoListNew.size());
        }

        //设置etc和北斗相关数据
        List<Long> busiIds = Lists.newArrayList();
        if (tenantVehicleRelVoList != null && tenantVehicleRelVoList.size() > 0) {
            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                busiIds.add(tenantVehicleRelVo.getEid());
//            busiIds.add(tenantVehicleRelVo.getVehicleCode());
                tenantVehicleRelVo.setIsUpdatePlateNumber(false);
                Integer vehicleClassL = tenantVehicleRelVo.getVehicleClass();
                if ((vehicleClassL != null )
                        || (vehicleClassL != null && (tenantVehicleRelVo.getTenantId() != null && tenantVehicleRelVo.getTenantId() <= 0))) {
                    Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(tenantVehicleRelVo.getPlateNumber());
                    if (aLong == null) {
                        tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                    }
                    List<EtcMaintain> etcMaintainList = null;
                    if (tenantVehicleRelVo.getVehicleCode() == null ) {
                        etcMaintainList = null;
                    } else {
                        etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), loginInfo.getTenantId());
                        if(etcMaintainList == null || etcMaintainList.size() == 0){
                            if(tenantVehicleRelVo.getTenantId() != null){
                                etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), tenantVehicleRelVo.getTenantId());
                            }
                        }
                    }

                    if (etcMaintainList == null || etcMaintainList.size() == 0) {
                        tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                    }
                    if (etcMaintainList != null && etcMaintainList.size() > 0) {
                        tenantVehicleRelVo.setEtcCardNo(etcMaintainList.get(0).getEtcId());
                    }
                    List<BeidouPaymentRecord> recordByVehicle = beidouPaymentRecordMapper.getRecordByVehicle(tenantVehicleRelVo.getVehicleCode().toString());
                    if (recordByVehicle != null && recordByVehicle.size() > 0) {
                        if (recordByVehicle.size() == 1) {
                            tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "至" + recordByVehicle.get(0).getInvalidDate());
                        } else {
                            tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "..." + recordByVehicle.get(recordByVehicle.size() - 1).getInvalidDate());
                            tenantVehicleRelVo.setBeidouPaymentRecordList(recordByVehicle);
                            tenantVehicleRelVo.setBeiDouMulti(1);
                        }
                    }

                }
            }
            //设置是否有审核权限
            if (CollectionUtils.isNotEmpty(busiIds)) {
                Map<Long, Boolean> hasPermission = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, busiIds, accessToken);
                for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                    Boolean flg = hasPermission.get(tenantVehicleRelVo.getEid());
                    try {
                        tenantVehicleRelVo.setIsAuth(flg ? 1 : 0);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return vehicleRelVoPage;
    }

    public Page<TenantVehicleRelVo> getBedou(String bdEffectDate, String bdInvalidDate, Page<TenantVehicleRelVo> vehicleRelVoPage) {
        List<TenantVehicleRelVo> tenantVehicleRelVoList = vehicleRelVoPage.getRecords();
        //过滤购买了北斗服务的车辆
        List<Long> beiDouVehicleList = Lists.newArrayList();
        if (StringUtils.isNotBlank(bdEffectDate) || StringUtils.isNotBlank(bdInvalidDate)) {
            List<BeidouPaymentRecord> beidouPaymentRecordList = beidouPaymentRecordMapper.queryBeiDouVehicleList(bdEffectDate, bdInvalidDate);
            if (beidouPaymentRecordList == null || beidouPaymentRecordList.size() == 0) {
                vehicleRelVoPage.setRecords(null);
                vehicleRelVoPage.setTotal(0);
            }
            for (BeidouPaymentRecord beidouPaymentRecord : beidouPaymentRecordList) {
                beiDouVehicleList.add(beidouPaymentRecord.getVehicleCode());
            }
        }

        if (beiDouVehicleList.size() > 0) {
            List<TenantVehicleRelVo> tenantVehicleRelVoListNew = Lists.newArrayList();
            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                if (beiDouVehicleList.contains(tenantVehicleRelVo.getVehicleCode())) {
                    tenantVehicleRelVoListNew.add(tenantVehicleRelVo);
                }
            }
            vehicleRelVoPage.setRecords(tenantVehicleRelVoListNew);
            vehicleRelVoPage.setTotal(tenantVehicleRelVoListNew.size());
            return vehicleRelVoPage;
        }

        //设置etc和北斗相关数据
        List<Long> busiIds = Lists.newArrayList();
        if (tenantVehicleRelVoList != null && tenantVehicleRelVoList.size() > 0) {
            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                busiIds.add(tenantVehicleRelVo.getEid());
//            busiIds.add(tenantVehicleRelVo.getVehicleCode());
                tenantVehicleRelVo.setIsUpdatePlateNumber(false);
                Integer vehicleClassL = tenantVehicleRelVo.getVehicleClass();
                if ((vehicleClassL != null && vehicleClassL.intValue() == VEHICLE_CLASS1)
                        || (vehicleClassL != null && (tenantVehicleRelVo.getTenantId() != null && tenantVehicleRelVo.getTenantId() <= 0))) {
                    Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(tenantVehicleRelVo.getPlateNumber());
                    if (aLong == null) {
                        tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                    }
                    List<EtcMaintain> etcMaintainList = null;
                    if (tenantVehicleRelVo.getVehicleCode() == null || tenantVehicleRelVo.getTenantId() == null) {
                        etcMaintainList = null;
                    } else {
                        etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), tenantVehicleRelVo.getTenantId());
                    }

                    if (etcMaintainList == null || etcMaintainList.size() == 0) {
                        tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                    }
                    if (etcMaintainList != null && etcMaintainList.size() > 0) {
                        tenantVehicleRelVo.setEtcCardNo(etcMaintainList.get(0).getEtcId());
                    }
                    List<BeidouPaymentRecord> recordByVehicle = beidouPaymentRecordMapper.getRecordByVehicle(tenantVehicleRelVo.getVehicleCode().toString());
                    if (recordByVehicle != null && recordByVehicle.size() > 0) {
                        if (recordByVehicle.size() == 1) {
                            tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "至" + recordByVehicle.get(0).getInvalidDate());
                        } else {
                            tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "..." + recordByVehicle.get(recordByVehicle.size() - 1).getInvalidDate());
                            tenantVehicleRelVo.setBeidouPaymentRecordList(recordByVehicle);
                            tenantVehicleRelVo.setBeiDouMulti(1);
                        }
                    }

                }
            }
        }
        return vehicleRelVoPage;
    }


    public Page<TenantVehicleRelVo> getTenantVehicleRelVo(String bdEffectDate, String bdInvalidDate, Page<TenantVehicleRelVo> vehicleRelVoPage,LoginInfo user) {
        List<TenantVehicleRelVo> tenantVehicleRelVoList = vehicleRelVoPage.getRecords();

        //过滤购买了北斗服务的车辆
        List<Long> beiDouVehicleList = Lists.newArrayList();
        if (StringUtils.isNotBlank(bdEffectDate) || StringUtils.isNotBlank(bdInvalidDate)) {
            List<BeidouPaymentRecord> beidouPaymentRecordList = beidouPaymentRecordMapper.queryBeiDouVehicleList(bdEffectDate, bdInvalidDate);
            if (beidouPaymentRecordList == null || beidouPaymentRecordList.size() == 0) {
                vehicleRelVoPage.setRecords(null);
                vehicleRelVoPage.setTotal(0);
            }
            for (BeidouPaymentRecord beidouPaymentRecord : beidouPaymentRecordList) {
                beiDouVehicleList.add(beidouPaymentRecord.getVehicleCode());
            }
        }

        if (beiDouVehicleList.size() > 0) {
            List<TenantVehicleRelVo> tenantVehicleRelVoListNew = Lists.newArrayList();
            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                if (beiDouVehicleList.contains(tenantVehicleRelVo.getVehicleCode())) {
                    tenantVehicleRelVoListNew.add(tenantVehicleRelVo);
                }
            }
            vehicleRelVoPage.setRecords(tenantVehicleRelVoListNew);
            vehicleRelVoPage.setTotal(tenantVehicleRelVoListNew.size());
        }

        //设置etc和北斗相关数据
        List<Long> busiIds = Lists.newArrayList();
        for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
            busiIds.add(tenantVehicleRelVo.getEid());
//            busiIds.add(tenantVehicleRelVo.getVehicleCode());
            tenantVehicleRelVo.setIsUpdatePlateNumber(false);
            Integer vehicleClassL = tenantVehicleRelVo.getVehicleClass();
            if ((vehicleClassL != null)
                    || (vehicleClassL != null && (tenantVehicleRelVo.getTenantId() != null && tenantVehicleRelVo.getTenantId() <= 0))) {
                Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(tenantVehicleRelVo.getPlateNumber());
                if (aLong == null) {
                    tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                }
                List<EtcMaintain> etcMaintainList = null;
                if (tenantVehicleRelVo.getVehicleCode() == null) {
                    etcMaintainList = null;
                } else {
                    etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), user.getTenantId());
                    if(etcMaintainList == null || etcMaintainList.size() == 0){
                        if(tenantVehicleRelVo.getTenantId() != null){
                            etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), tenantVehicleRelVo.getTenantId());
                        }
                    }
                }

                if (etcMaintainList == null || etcMaintainList.size() == 0) {
                    tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                }
                if (etcMaintainList != null && etcMaintainList.size() > 0) {
                    tenantVehicleRelVo.setEtcCardNo(etcMaintainList.get(0).getEtcId());
                }
                List<BeidouPaymentRecord> recordByVehicle = beidouPaymentRecordMapper.getRecordByVehicle(tenantVehicleRelVo.getVehicleCode().toString());
                if (recordByVehicle != null && recordByVehicle.size() > 0) {
                    if (recordByVehicle.size() == 1) {
                        tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "至" + recordByVehicle.get(0).getInvalidDate());
                    } else {
                        tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "..." + recordByVehicle.get(recordByVehicle.size() - 1).getInvalidDate());
                        tenantVehicleRelVo.setBeidouPaymentRecordList(recordByVehicle);
                        tenantVehicleRelVo.setBeiDouMulti(1);
                    }
                }

            }
        }
        return vehicleRelVoPage;
    }

    @Override
    public Page<TenantVehicleRelVo> doQueryVehicleAllIsOrder(Integer isWorking, String plateNumber,
                                                             String linkman, String mobilePhone,
                                                             String billReceiverMobile, String linkManName,
                                                             String linkPhone, String vehicleLength,
                                                             String tenantName, Integer vehicleStatus,
                                                             String startTime, String endTime,
                                                             Integer authStateIn, Integer shareFlgIn,
                                                             Integer isAuth, Integer vehicleClass,
                                                             Integer vehicleGps, String bdEffectDate,
                                                             String bdInvalidDate, String accessToken,
                                                             Page<TenantVehicleRelVo> page, Long orgId) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        //分页查询我的车库
        Page<TenantVehicleRelVo> vehicleRelVoPage = doQueryVehicleAllAndSetIsWorking(orgId, isWorking, loginInfo.getTenantId(), plateNumber, linkman, mobilePhone, billReceiverMobile, linkManName, linkPhone, vehicleLength,
                tenantName, authStateIn, shareFlgIn, isAuth, vehicleClass, vehicleGps, vehicleStatus, page);
        List<TenantVehicleRelVo> tenantVehicleRelVoList = vehicleRelVoPage.getRecords();

        //过滤购买了北斗服务的车辆
        List<Long> beiDouVehicleList = Lists.newArrayList();
        if (StringUtils.isNotBlank(bdEffectDate) || StringUtils.isNotBlank(bdInvalidDate)) {
            List<BeidouPaymentRecord> beidouPaymentRecordList = beidouPaymentRecordMapper.queryBeiDouVehicleList(bdEffectDate, bdInvalidDate);
            if (beidouPaymentRecordList == null || beidouPaymentRecordList.size() == 0) {
                vehicleRelVoPage.setRecords(null);
                vehicleRelVoPage.setTotal(0);
            }
            for (BeidouPaymentRecord beidouPaymentRecord : beidouPaymentRecordList) {
                beiDouVehicleList.add(beidouPaymentRecord.getVehicleCode());
            }
        }

        if (beiDouVehicleList.size() > 0) {
            List<TenantVehicleRelVo> tenantVehicleRelVoListNew = Lists.newArrayList();
            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
                if (beiDouVehicleList.contains(tenantVehicleRelVo.getVehicleCode())) {
                    tenantVehicleRelVoListNew.add(tenantVehicleRelVo);
                }
            }
            vehicleRelVoPage.setRecords(tenantVehicleRelVoListNew);
            vehicleRelVoPage.setTotal(tenantVehicleRelVoListNew.size());
        }

        //设置etc和北斗相关数据
        List<Long> busiIds = Lists.newArrayList();
        for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
            busiIds.add(tenantVehicleRelVo.getEid());
//            busiIds.add(tenantVehicleRelVo.getVehicleCode());
            tenantVehicleRelVo.setIsUpdatePlateNumber(false);
            Integer vehicleClassL = tenantVehicleRelVo.getVehicleClass();
            if ((vehicleClassL != null && vehicleClassL.intValue() == VEHICLE_CLASS1)
                    || (vehicleClassL != null && (tenantVehicleRelVo.getTenantId() != null && tenantVehicleRelVo.getTenantId() <= 0))) {
                // TODO 为什么这个地方(有的话会显示订单号) == null 的时候表示的是更换过/没更换过车牌
                Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(tenantVehicleRelVo.getPlateNumber());
                if (aLong == null) {
                    tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                }
                List<EtcMaintain> etcMaintainList = null;
                if (tenantVehicleRelVo.getVehicleCode() == null || tenantVehicleRelVo.getTenantId() == null) {
                    etcMaintainList = null;
                } else {
                    etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), tenantVehicleRelVo.getTenantId());
                }

                // TODO 为什么有车辆没有ETC信息  表示的是更换过/没更换过车牌
                if (etcMaintainList == null || etcMaintainList.size() == 0) {
                    tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                }
                if (etcMaintainList != null && etcMaintainList.size() > 0) {
                    tenantVehicleRelVo.setEtcCardNo(etcMaintainList.get(0).getEtcId());
                }

                // 查询车辆的购买北斗的记录
                List<BeidouPaymentRecord> recordByVehicle = beidouPaymentRecordMapper.getRecordByVehicle(tenantVehicleRelVo.getVehicleCode().toString());
                if (recordByVehicle != null && recordByVehicle.size() > 0) {
                    if (recordByVehicle.size() == 1) {
                        tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "至" + recordByVehicle.get(0).getInvalidDate());
                    } else {
                        tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "..." + recordByVehicle.get(recordByVehicle.size() - 1).getInvalidDate());
                        tenantVehicleRelVo.setBeidouPaymentRecordList(recordByVehicle);
                        tenantVehicleRelVo.setBeiDouMulti(1);
                    }
                }

            }
        }
        //设置是否有审核权限
//        if (CollectionUtils.isNotEmpty(busiIds)) {
//            Map<Long, Boolean> hasPermission = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, busiIds, accessToken);
//            for (TenantVehicleRelVo tenantVehicleRelVo : tenantVehicleRelVoList) {
//                Boolean flg = hasPermission.get(tenantVehicleRelVo.getVehicleCode());
//                tenantVehicleRelVo.setIsAuth(flg ? 1 : 0);
//            }
//        }
        return vehicleRelVoPage;
    }

    @Override
    public List<TenantVehicleRel> doQueryTenantVehicleRel(Long tenantId, Long vehicleCode) {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", vehicleCode).eq("tenant_id", tenantId).
                notIn("vehicle_Class", VEHICLE_CLASS1);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<VehicleDataInfo> getTenantVehicleRelByDriverUserIdObms(long userId, long tenantId) {
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_user_id", userId);
        if (tenantId > 1) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<VehicleDataInfo> vehicleDataInfos = iVehicleDataInfoService.list(queryWrapper);

        return vehicleDataInfos;
    }

    @Override
    public List<TenantVehicleRel> getTenantVehicleRelByDriverUserId(long userId, long tenantId) {
        Long driverTenantId = userDataInfoRecordMapper.getDriverTenantId(userId);
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_user_id", userId);
        if (tenantId > 1) {
            queryWrapper.eq("tenant_id", tenantId);
        } else {
            queryWrapper.eq("tenant_id", driverTenantId);
        }
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<TenantVehicleRel> getTenantVehicleRelByDriverUserId(long userId) {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_user_id", userId);
        return baseMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public void doRemoveVehicleV30(long relId, String token) throws Exception {
        TenantVehicleRel tenantVehicleRel = baseMapper.selectById(relId);
        if (null == tenantVehicleRel) {
            return;
        }
        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getById(tenantVehicleRel.getVehicleCode());
        if (null == vehicleDataInfo) {
            return;
        }
        //先创建一份主表的历史记录信息
        VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(tenantVehicleRel.getVehicleCode());
        QueryWrapper<TenantVehicleCostRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", tenantVehicleRel.getVehicleCode())
                .eq("tenant_id", tenantVehicleRel.getTenantId());
        List<TenantVehicleCostRel> tenantVehicleCostRelList = tenantVehicleCostRelMapper.selectList(queryWrapper);
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        if (null != tenantVehicleCostRelList && !tenantVehicleCostRelList.isEmpty()) {
            for (int i = 0; i < tenantVehicleCostRelList.size(); i++) {
                TenantVehicleCostRel vehicleCostRel = tenantVehicleCostRelList.get(i);
                TenantVehicleCostRelVer vehicleCostRelVer = new TenantVehicleCostRelVer();
                BeanUtil.copyProperties(vehicleCostRel, vehicleCostRelVer);
                vehicleCostRelVer.setHisId(vehicleCostRel.getId());
                vehicleCostRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                vehicleCostRelVer.setCreateTime(localDateTime);
                vehicleCostRelVer.setIsAuthSucc(3);
                vehicleCostRelVer.setVehicleCode(vehicleDataInfo.getId());
//                vehicleCostRelVer.setId(vehicleDataInfoVer.getId());

                iTenantVehicleCostRelVerService.save(vehicleCostRelVer);
                //更新原来记录状态为0
                Map paramMap = new HashMap();
                paramMap.put("vehicleCode", vehicleCostRel.getVehicleCode());
                paramMap.put("tenantId", vehicleCostRel.getTenantId());
                paramMap.put("destVerState", SysStaticDataEnum.VER_STATE.VER_STATE_N);
                updateVehicleVerAllVerState("tenant_vehicle_cost_rel_ver", paramMap);
                iTenantVehicleCostRelService.removeById(tenantVehicleCostRelList.get(i).getId());
            }
        }


        //删除证照信息
        QueryWrapper<TenantVehicleCertRel> tenantVehicleCertRelQueryWrapper = new QueryWrapper<>();
        tenantVehicleCertRelQueryWrapper.eq("vehicle_code", tenantVehicleRel.getVehicleCode())
                .eq("tenant_id", tenantVehicleRel.getTenantId());
        List<TenantVehicleCertRel> tenantVehicleCertRelList = iTenantVehicleCertRelService.list(tenantVehicleCertRelQueryWrapper);
        if (null != tenantVehicleCertRelList && !tenantVehicleCertRelList.isEmpty()) {
            for (int i = 0; i < tenantVehicleCertRelList.size(); i++) {
                TenantVehicleCertRel vehicleCertRel = tenantVehicleCertRelList.get(i);
                TenantVehicleCertRelVer vehicleCertRelVer = new TenantVehicleCertRelVer();
                BeanUtil.copyProperties(vehicleCertRel, vehicleCertRelVer);
                vehicleCertRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                vehicleCertRelVer.setCreateTime(LocalDateTime.now());
                vehicleCertRelVer.setIsAuthSucc(3);
//                vehicleCertRelVer.setHisId(vehicleDataInfoVer.getId());
                iTenantVehicleCertRelVerService.save(vehicleCertRelVer);
                //更新原来记录状态为0
                Map paramMap = new HashMap();
                paramMap.put("vehicleCode", vehicleCertRel.getVehicleCode());
                paramMap.put("tenantId", vehicleCertRel.getTenantId());
                paramMap.put("destVerState", SysStaticDataEnum.VER_STATE.VER_STATE_N);
                updateVehicleVerAllVerState("tenant_vehicle_cert_rel_ver", paramMap);
                iTenantVehicleCertRelService.removeById(vehicleCertRel.getId());
            }
        }
        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVer.setVehicleHisId(vehicleDataInfoVer.getId());
        tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
        tenantVehicleRelVer.setIsAuthSucc(3);
        tenantVehicleRelVer.setCreateTime(LocalDateTime.now());
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);

        removeById(tenantVehicleRel.getId());

        //删除自有车删除所有的绑定关系
        if (null != tenantVehicleRel.getVehicleClass() && tenantVehicleRel.getVehicleClass().intValue() == VEHICLE_CLASS1) {
            iVehicleDataInfoService.removeOwnCarAndNotifyOtherTenant(vehicleDataInfo.getId(), token);
            //清除邀请
            iVehicleDataInfoService.removeApplyRecord(tenantVehicleRel.getVehicleCode());
        }

        iVehicleDataInfoService.addPtTenantVehicleRel(tenantVehicleRel.getVehicleCode());

        String msg = "车辆-" + tenantVehicleRel.getPlateNumber() + "-与车队-" + tenantVehicleRel.getTenantId() + "-解除" + iSysStaticDataService.getSysStaticData("VEHICLE_CLASS", tenantVehicleRel.getVehicleClass() + "") + "关系";
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Del, msg, token, tenantVehicleRel.getId());
    }

    @Override
    public TenantVehicleRel getTenantVehicleRel(String plateNumber, Long tenantId) {

        if (org.apache.commons.lang.StringUtils.isBlank(plateNumber) || tenantId < 0) {
            throw new BusinessException("参数错误");
        }
        LambdaQueryWrapper<TenantVehicleRel> lambda = Wrappers.lambdaQuery();
        lambda.eq(TenantVehicleRel::getPlateNumber, plateNumber)
                .eq(TenantVehicleRel::getTenantId, tenantId);
        List<TenantVehicleRel> relList = this.list(lambda);
        TenantVehicleRel tenantVehicleRel = null;
        if (relList != null && relList.size() > 0) {
            tenantVehicleRel = relList.get(0);
        }
        return tenantVehicleRel;
    }

    @Override
    public List<Map> queryVehicleList(int type, long tenantId, String vehicleNum, int orgId) {
        return tenantVehicleRelMapper.queryVehicleList(type, tenantId, vehicleNum, orgId);
    }

    @Override
    public List<Map> queryNoneOrderVehicle(Integer vehicleClass, Long tenantId, Long orgId, String plateNumber, int hour, int hourEnd, String isCount) {
        return tenantVehicleRelMapper.queryNoneOrderVehicle(vehicleClass, tenantId, orgId, plateNumber, hour, hourEnd, isCount);
    }

    public void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }


    @Override
    public void changeVehicleDriver(Long tenantId, Long oriDriverUserId, Long destDriverUserId) throws Exception {
        if (tenantId == null || tenantId < 0) {
            throw new BusinessException("参数错误，租户编号不合法");
        }
        if (oriDriverUserId != null && oriDriverUserId < 0) {
            throw new BusinessException("参数错误，车辆原有司机编号不合法");
        }

        if (destDriverUserId != null && destDriverUserId < 0) {
            throw new BusinessException("参数错误，车辆新的司机编号不合法");
        }
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId).eq("driver_user_Id", oriDriverUserId);
        List<TenantVehicleRel> vehicleRelList = baseMapper.selectList(queryWrapper);
        if (vehicleRelList != null) {
            for (TenantVehicleRel tenantVehicleRel : vehicleRelList) {
                //tenantVehicleRel.setDriverUserId(destDriverUserId);
                baseMapper.upDriverUserIdNull(tenantVehicleRel.getId());
                QueryWrapper<TenantVehicleRelVer> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("rel_Id", tenantVehicleRel.getId()).
                        eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y).
                        orderByDesc("id").last(" limit 1");
                TenantVehicleRelVer tenantVehicleRelVer = tenantVehicleRelVerMapper.selectOne(queryWrapper1);
                if (tenantVehicleRelVer != null) {
//                    tenantVehicleRelVer.setDriverUserId(destDriverUserId);
//                    UpdateWrapper<TenantVehicleRelVer> updateWrapper=new UpdateWrapper<>();
//                    updateWrapper.set("driver_User_Id",null);
                    tenantVehicleRelVerMapper.upDriverUserIdNull(tenantVehicleRelVer.getId());
                }
            }
        }

    }

    public void updateVehicleVerAllVerState(String tableName, Map<String, Object> inMap) throws Exception {
        iVehicleDataInfoService.updateVehicleVerAllVerState(tableName, inMap);
    }


    /**
     * 实现功能: 查询我的车库和封装数据
     *
     * @param
     * @param vehicleStatus
     * @return
     */
    public Page<TenantVehicleRelVo> doQueryVehicleAllAndSetData(Long tenantId, String plateNumber, String linkman,
                                                                String mobilePhone, String billReceiverMobile,
                                                                String linkManName, String linkPhone, String vehicleLength,
                                                                String tenantName, Integer authStateIn, Integer shareFlgIn,
                                                                Integer isAuth, Integer vehicleClass, Integer vehicleGps,
                                                                Integer vehicleStatus, Page<TenantVehicleRelVo> page) {

        Page<TenantVehicleRelVo> vehicleRelVoPage = baseMapper.doQueryVehicleAll(page, tenantId, plateNumber, linkManName,
                linkPhone, billReceiverMobile, linkman, tenantName, mobilePhone, authStateIn, vehicleLength, vehicleStatus,
                shareFlgIn, vehicleClass, isAuth, vehicleGps);
        //封装分页数据
        if (vehicleRelVoPage.getRecords() != null && vehicleRelVoPage.getRecords().size() > 0) {
            List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
//            List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
//            List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
//            List<SysStaticData> customerAuthState = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("CUSTOMER_AUTH_STATE"));

            for (TenantVehicleRelVo tenantVehicleRelVo : vehicleRelVoPage.getRecords()) {
                if (tenantVehicleRelVo.getAuthState() != null && tenantVehicleRelVo.getAuthState() > -1) {
                    tenantVehicleRelVo.setAuthStateName(iSysStaticDataService.getSysStaticDatas("CUSTOMER_AUTH_STATE", tenantVehicleRelVo.getAuthState()));

                }
                if (tenantVehicleRelVo.getVehicleClass() != null && tenantVehicleRelVo.getVehicleClass() > -1) {
                    tenantVehicleRelVo.setVehicleClassName(iSysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", tenantVehicleRelVo.getVehicleClass()));

                }
                if (tenantVehicleRelVo.getLicenceType() != null && tenantVehicleRelVo.getLicenceType() > -1) {
                    tenantVehicleRelVo.setLicenceTypeName(iSysStaticDataService.getSysStaticDatas("LICENCE_TYPE", tenantVehicleRelVo.getLicenceType())
                    );
                }
                if (tenantVehicleRelVo.getTenantId() != null) {
                    if (tenantVehicleRelVo.getTenantId() < 0) {
                        tenantVehicleRelVo.setTenantName("平台合作车");
                    }
                    if (tenantVehicleRelVo.getShareFlg() != null) {
                        if (tenantVehicleRelVo.getShareFlg() > -1 && tenantVehicleRelVo.getTenantId().equals(tenantId)) {
                            tenantVehicleRelVo.setShareFlgName(iSysStaticDataService.getSysStaticDatas("SHARE_FLG", tenantVehicleRelVo.getShareFlg()));
                        }
                    }
                    if (!tenantVehicleRelVo.getTenantId().equals(tenantId)) {
                        tenantVehicleRelVo.setCooperationCount("");
                    }/* else {
                        tenantVehicleRelVo.setTenantName("");
                        tenantVehicleRelVo.setLinkPhone("");
                    }*/
                }
                if (tenantVehicleRelVo.getLocationServ() != null) {
                    if (tenantVehicleRelVo.getLocationServ() == -1 || tenantVehicleRelVo.getLocationServ() == 0) {
                        tenantVehicleRelVo.setLocationServName(iSysStaticDataService.getSysStaticDatas("VEHICLE_GPS_TYPE", OrderConsts.GPS_TYPE.APP));
                    } else {
                        tenantVehicleRelVo.setLocationServName(iSysStaticDataService.getSysStaticDatas("VEHICLE_GPS_TYPE", tenantVehicleRelVo.getLocationServ()));
                    }
                }
                if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() > 0) {
                    Long vehicleStatus1 = Long.valueOf(tenantVehicleRelVo.getVehicleStatus());
                    for (SysStaticData sysStaticData : vehicleStatusList) {
                        if (sysStaticData.getId().equals(vehicleStatus1)) {
                            tenantVehicleRelVo.setVehicleStatusName(sysStaticData.getCodeName());
                            break;
                        }
                    }
                }
//                if (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) && tenantVehicleRelVo.getVehicleLength().equals("-1")) {
//                    tenantVehicleRelVo.setVehicleLengthName(getVehicleLengthName("" + tenantVehicleRelVo.getVehicleLength(), tenantVehicleRelVo.getVehicleLength()));
//                }

                try {
                    if (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) && !"-1".equals(tenantVehicleRelVo.getVehicleLength())) {
                        String vehicleLength1 = tenantVehicleRelVo.getVehicleLength();
                        if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() == 8) {

                            if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                                SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", tenantVehicleRelVo.getVehicleLength() + "");
                                if (vehicleStatusSubtype != null) {
                                    tenantVehicleRelVo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                                }
                            }
                        } else {
                            tenantVehicleRelVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", tenantVehicleRelVo.getVehicleLength()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tenantVehicleRelVo.getVehicleStatus() != null) {
                    if (tenantVehicleRelVo.getVehicleStatus() > 0
                            && StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength())
                            && !tenantVehicleRelVo.getVehicleLength().equals("-1")) {
                        tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleStatusName() + "/" + tenantVehicleRelVo.getVehicleLengthName());
                    } else if (tenantVehicleRelVo.getVehicleStatus() > 0 &&
                            (tenantVehicleRelVo.getVehicleLength() != null &&
                                    (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) || !tenantVehicleRelVo.getVehicleLength().equals("-1")))) {
                        tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleStatusName());
                    } else {
                        tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleLengthName());
                    }
                }
            }
        }
        return vehicleRelVoPage;
    }


    /**
     * 实现功能: 查询我的车库和封装数据
     *
     * @param
     * @param vehicleStatus
     * @return
     */
    public Page<TenantVehicleRelVo> doQueryVehicleAllAndSetIsWorking(Long orgId, Integer isWorking, Long tenantId, String plateNumber, String linkman,
                                                                     String mobilePhone, String billReceiverMobile,
                                                                     String linkManName, String linkPhone, String vehicleLength,
                                                                     String tenantName, Integer authStateIn, Integer shareFlgIn,
                                                                     Integer isAuth, Integer vehicleClass, Integer vehicleGps,
                                                                     Integer vehicleStatus, Page<TenantVehicleRelVo> page) {

        Page<TenantVehicleRelVo> vehicleRelVoPage = baseMapper.doQueryVehicleAllIsOrder(orgId, page, isWorking, tenantId, plateNumber, linkManName,
                linkPhone, billReceiverMobile, linkman, tenantName, mobilePhone, authStateIn, vehicleLength, vehicleStatus,
                shareFlgIn, vehicleClass, isAuth, vehicleGps);

        List<TenantVehicleRelVo> records = vehicleRelVoPage.getRecords();
        //该车辆无订单
        List<TenantVehicleRelVo> records1 = new ArrayList<>();
        //该车辆有订单
        List<TenantVehicleRelVo> records2 = new ArrayList<>();

        for (TenantVehicleRelVo tenantVehicleRelVo : records) {

            // 通过车牌查找最后一单订单
            OrderScheduler orderSchedulerByPlateNumber = orderSchedulerService.getOrderSchedulerByPlateNumber(tenantVehicleRelVo.getPlateNumber());

            // 订单不存在
            if (orderSchedulerByPlateNumber == null || orderSchedulerByPlateNumber.getOrderId() == null) {
                records1.add(tenantVehicleRelVo);
            } else {
                OrderInfo order = orderInfoService.getOrder(orderSchedulerByPlateNumber.getOrderId());

                // 订单存在  订单状态为 已拒单/已完成/已撤销
                if (order != null && order.getOrderState() == 4 || order.getOrderState() == 14 || order.getOrderState() == 15) {
                    records1.add(tenantVehicleRelVo);
                } else if (order == null) {
                    records1.add(tenantVehicleRelVo);
                } else {
                    records2.add(tenantVehicleRelVo);
                }

            }
        }
        if (isWorking == null) {
            //全部
            vehicleRelVoPage.setRecords(records);
        } else if (isWorking == 1) { // 无订单
            //当前无订单
            vehicleRelVoPage.setRecords(records1);
            vehicleRelVoPage.setTotal(records1.size());

        } else if (isWorking == 2) { // 有订单
            //当前有订单
            vehicleRelVoPage.setRecords(records2);
            vehicleRelVoPage.setTotal(records2.size());
        }
        //封装分页数据
        if (vehicleRelVoPage.getRecords() != null && vehicleRelVoPage.getRecords().size() > 0) {
            List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
            List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
            List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
            List<SysStaticData> customerAuthState = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("CUSTOMER_AUTH_STATE"));

            for (TenantVehicleRelVo tenantVehicleRelVo : vehicleRelVoPage.getRecords()) {
                if (tenantVehicleRelVo.getAuthState() != null && tenantVehicleRelVo.getAuthState() > -1) {
                    tenantVehicleRelVo.setAuthStateName(iSysStaticDataService.getSysStaticDatas("CUSTOMER_AUTH_STATE", tenantVehicleRelVo.getAuthState()));

                }
                if (tenantVehicleRelVo.getVehicleClass() != null && tenantVehicleRelVo.getVehicleClass() > -1) {
                    tenantVehicleRelVo.setVehicleClassName(iSysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", tenantVehicleRelVo.getVehicleClass()));

                }
                if (tenantVehicleRelVo.getLicenceType() != null && tenantVehicleRelVo.getLicenceType() > -1) {
                    tenantVehicleRelVo.setLicenceTypeName(iSysStaticDataService.getSysStaticDatas("LICENCE_TYPE", tenantVehicleRelVo.getLicenceType())
                    );
                }
                if (tenantVehicleRelVo.getTenantId() != null) {
                    if (tenantVehicleRelVo.getTenantId() < 0) {
                        tenantVehicleRelVo.setTenantName("平台合作车");
                    }
                    if (tenantVehicleRelVo.getShareFlg() != null) {
                        if (tenantVehicleRelVo.getShareFlg() > -1 && tenantVehicleRelVo.getTenantId().equals(tenantId)) {
                            tenantVehicleRelVo.setShareFlgName(iSysStaticDataService.getSysStaticDatas("SHARE_FLG", tenantVehicleRelVo.getShareFlg()));
                        }
                    }
                    if (!tenantVehicleRelVo.getTenantId().equals(tenantId)) {
                        tenantVehicleRelVo.setCooperationCount("");
                    }/* else {
                        tenantVehicleRelVo.setTenantName("");
                        tenantVehicleRelVo.setLinkPhone("");
                    }*/
                }
                if (tenantVehicleRelVo.getLocationServ() != null) {
                    if (tenantVehicleRelVo.getLocationServ() == -1 || tenantVehicleRelVo.getLocationServ() == 0) {
                        tenantVehicleRelVo.setLocationServName(iSysStaticDataService.getSysStaticDatas("VEHICLE_GPS_TYPE", OrderConsts.GPS_TYPE.APP));
                    } else {
                        tenantVehicleRelVo.setLocationServName(iSysStaticDataService.getSysStaticDatas("VEHICLE_GPS_TYPE", tenantVehicleRelVo.getLocationServ()));
                    }
                }
                if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() > 0) {
                    Long vehicleStatus1 = Long.valueOf(tenantVehicleRelVo.getVehicleStatus());
                    for (SysStaticData sysStaticData : vehicleStatusList) {
                        if (sysStaticData.getId().equals(vehicleStatus1)) {
                            tenantVehicleRelVo.setVehicleStatusName(sysStaticData.getCodeName());
                            break;
                        }
                    }
                }
//                if (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) && tenantVehicleRelVo.getVehicleLength().equals("-1")) {
//                    tenantVehicleRelVo.setVehicleLengthName(getVehicleLengthName("" + tenantVehicleRelVo.getVehicleLength(), tenantVehicleRelVo.getVehicleLength()));
//                }

                try {
                    if (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) && !"-1".equals(tenantVehicleRelVo.getVehicleLength())) {
                        String vehicleLength1 = tenantVehicleRelVo.getVehicleLength();
                        if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() == 8) {

                            if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                                SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", tenantVehicleRelVo.getVehicleLength() + "");
                                if (vehicleStatusSubtype != null) {
                                    tenantVehicleRelVo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                                }
                            }
                        } else {
                            tenantVehicleRelVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", tenantVehicleRelVo.getVehicleLength()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tenantVehicleRelVo.getVehicleStatus() != null) {
                    if (tenantVehicleRelVo.getVehicleStatus() > 0
                            && StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength())
                            && !tenantVehicleRelVo.getVehicleLength().equals("-1")) {
                        tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleStatusName() + "/" + tenantVehicleRelVo.getVehicleLengthName());
                    } else if (tenantVehicleRelVo.getVehicleStatus() > 0 &&
                            (tenantVehicleRelVo.getVehicleLength() != null &&
                                    (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) || !tenantVehicleRelVo.getVehicleLength().equals("-1")))) {
                        tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleStatusName());
                    } else {
                        tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleLengthName());
                    }
                }
            }
        }
        return vehicleRelVoPage;
    }

    @Override
    public PageInfo<VehicleDataVo> doQueryVehicleAllDis(Integer authStateIn, Integer shareFlgIn,
                                                    Integer isAuth, String startTime, String endTime, String plateNumber,
                                                    String linkManName, String linkPhone, String linkman, String mobilePhone,
                                                    String tenantName, String vehicleLength, Integer vehicleStatus, String accessToken,Integer  pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<VehicleDataVo> vehicleDataVoPage = baseMapper.doQueryVehicleAllDis(authStateIn, shareFlgIn, isAuth, startTime, endTime, plateNumber,
                linkManName, linkPhone, linkman, mobilePhone, tenantName, vehicleLength, vehicleStatus);
        //封装数据
        if (vehicleDataVoPage != null && vehicleDataVoPage.size() > 0) {
            List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
            List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));
            List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
            List<SysStaticData> customerAuthState = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("CUSTOMER_AUTH_STATE"));
            for (VehicleDataVo record : vehicleDataVoPage) {
                if (record.getTenantId() != null && record.getTenantId() < 0) {
                    record.setTenantName("平台合作车");
                }
                if (record.getShareFlg() != null && record.getShareFlg() == 0) {
                    record.setShareFlgName("否");
                } else {
                    record.setShareFlgName("是");
                }
                if (record.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
                    record.setAuthStateName("已认证");
                } else {
                    record.setAuthStateName("未认证");
                }
                if (record.getAuthState() != null && record.getAuthState() > -1) {
                    Integer authState = record.getAuthState();
                    for (SysStaticData sysStaticData : customerAuthState) {
                        if (sysStaticData.getCodeValue().equals(authState)) {
                            record.setAuthStateName(sysStaticData.getCodeName());
                            break;
                        }
                    }
                }
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
                if (record.getLicenceType() > -1) {
                    record.setLicenceTypeName(iSysStaticDataService.getSysStaticDatas("LICENCE_TYPE", record.getLicenceType())
                    );
                }
                if (record.getLocationServ() != null) {
                    if (record.getLocationServ() == -1 || record.getLocationServ() == 0) {
                        record.setLocationServName("APP定位");
                    } else if (record.getLocationServ() == OrderConsts.GPS_TYPE.G7) {
                        record.setLocationServName("G7定位");
                    } else if (record.getLocationServ() == OrderConsts.GPS_TYPE.APP) {
                        record.setLocationServName("APP定位");
                    } else if (record.getLocationServ() == OrderConsts.GPS_TYPE.YL) {
                        record.setLocationServName("易流定位");
                    } else if (record.getLocationServ() == OrderConsts.GPS_TYPE.BD) {
                        record.setLocationServName("北斗定位");
                    }
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
        PageInfo<VehicleDataVo> customerInfoPageInfo = new PageInfo<>(vehicleDataVoPage);
        return customerInfoPageInfo;
    }

    /**
     * 清除一个司机作为副驾驶和随车司机的信息
     */
    @Override
    public void clearCopilotDriverAndFollowDriver(Long driverUserId) throws Exception {
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("copilot_driver_id", driverUserId);
        //清除副驾驶
        List<VehicleDataInfo> vehicleDataInfoList2 = iVehicleDataInfoService.list(queryWrapper);
        if (vehicleDataInfoList2 != null && !vehicleDataInfoList2.isEmpty()) {
            for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList2) {
                vehicleDataInfo.setCopilotDriverId(null);
                iVehicleDataInfoService.updateById(vehicleDataInfo);
                VehicleDataInfoVer vehicleDataInfoVer = getInfoVerById(vehicleDataInfo.getId());
                vehicleDataInfoVer.setCopilotDriverId(null);
                iVehicleDataInfoVerService.updateById(vehicleDataInfoVer);
            }
        }
        //清楚随车司机
        QueryWrapper<VehicleDataInfo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("follow_driver_Id", driverUserId);
        vehicleDataInfoList2 = iVehicleDataInfoService.list(queryWrapper2);
        if (vehicleDataInfoList2 != null && !vehicleDataInfoList2.isEmpty()) {
            for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList2) {
                vehicleDataInfo.setFollowDriverId(null);
                iVehicleDataInfoService.updateById(vehicleDataInfo);
                VehicleDataInfoVer vehicleDataInfoVer = getInfoVerById(vehicleDataInfo.getId());
                vehicleDataInfoVer.setFollowDriverId(null);
                iVehicleDataInfoVerService.updateById(vehicleDataInfoVer);
            }
        }

    }

    @Override
    public void removePtVehicle(String plateNumber, Long vehicleCode) throws Exception {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", 1);
        if (org.apache.commons.lang.StringUtils.isNotBlank(plateNumber)) {
            queryWrapper.eq("plate_Number", plateNumber);
        }
        if (vehicleCode != null && vehicleCode > 0) {
            queryWrapper.eq("vehicle_Code", vehicleCode);
        }
        List<TenantVehicleRel> ptVehicleRelList = baseMapper.selectList(queryWrapper);
        if (ptVehicleRelList != null && ptVehicleRelList.size() > 0) {
            for (TenantVehicleRel vehicleRel : ptVehicleRelList) {
                removeById(vehicleRel.getId());
            }
        }
    }

    private VehicleDataInfoVer getInfoVerById(Long id) {
        QueryWrapper<VehicleDataInfoVer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", id).orderByDesc("id");
        List<VehicleDataInfoVer> list = iVehicleDataInfoVerService.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new VehicleDataInfoVer();
    }

    @Resource
    private ReadisUtil readisUtil;

    @Override
    @Async
    @Transactional
    public void acquireExcelFile(ImportOrExportRecords record, String plateNumber, String linkman, String mobilePhone, String billReceiverMobile, String linkManName, String linkPhone, String vehicleLength, String tenantName, Integer vehicleStatus, String startTime, String endTime, Integer authStateIn, Integer shareFlgIn, Integer isAuth, Integer vehicleClass, Integer vehicleGps, String bdEffectDate, String bdInvalidDate, String fieldName, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        try {
            List<TenantVehicleRelVo> vehicleRelVoPage = baseMapper.getVehicleAll(loginInfo.getTenantId(), plateNumber, linkManName,
                    linkPhone, billReceiverMobile, linkman, tenantName, mobilePhone, authStateIn, vehicleLength, vehicleStatus,
                    shareFlgIn, vehicleClass, isAuth, vehicleGps);
            //封装分页数据
            //过滤购买了北斗服务的车辆
            List<Long> beiDouVehicleList = Lists.newArrayList();
            if (StringUtils.isNotBlank(bdEffectDate) || StringUtils.isNotBlank(bdInvalidDate)) {
                List<BeidouPaymentRecord> beidouPaymentRecordList = beidouPaymentRecordMapper.queryBeiDouVehicleList(bdEffectDate, bdInvalidDate);
                for (BeidouPaymentRecord beidouPaymentRecord : beidouPaymentRecordList) {
                    beiDouVehicleList.add(beidouPaymentRecord.getVehicleCode());
                }
            }

            if (beiDouVehicleList.size() > 0) {
                List<TenantVehicleRelVo> tenantVehicleRelVoListNew = Lists.newArrayList();
                for (TenantVehicleRelVo tenantVehicleRelVo : vehicleRelVoPage) {
                    if (beiDouVehicleList.contains(tenantVehicleRelVo.getVehicleCode())) {
                        tenantVehicleRelVoListNew.add(tenantVehicleRelVo);
                    }
                }
            }

            //设置etc和北斗相关数据
            List<Long> busiIds = Lists.newArrayList();
            List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
            List<SysStaticData> vehicleLengthList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
            List<SysStaticData> vehicleStatusSubtypeList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS_SUBTYPE"));

            for (TenantVehicleRelVo tenantVehicleRelVo : vehicleRelVoPage) {
                busiIds.add(tenantVehicleRelVo.getEid());
                tenantVehicleRelVo.setIsUpdatePlateNumber(false);
                Integer vehicleClassL = tenantVehicleRelVo.getVehicleClass();
                if ((vehicleClassL != null && vehicleClassL.intValue() == VEHICLE_CLASS1)
                        || (vehicleClassL != null && (tenantVehicleRelVo.getTenantId() != null && tenantVehicleRelVo.getTenantId() <= 0))) {
                    Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(tenantVehicleRelVo.getPlateNumber());
                    if (aLong == null) {
                        tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                    }
                    List<EtcMaintain> etcMaintainList = null;
                    if (tenantVehicleRelVo.getVehicleCode() == null || tenantVehicleRelVo.getTenantId() == null) {
                        etcMaintainList = null;
                    } else {
                        etcMaintainList = etcMaintainMapper.queryEtcMaintainByVehicleCode(tenantVehicleRelVo.getVehicleCode().toString(), tenantVehicleRelVo.getTenantId());
                    }

                    if (etcMaintainList == null || etcMaintainList.size() == 0) {
                        tenantVehicleRelVo.setIsUpdatePlateNumber(true);
                    }
                    if (etcMaintainList != null && etcMaintainList.size() > 0) {
                        tenantVehicleRelVo.setEtcCardNo(etcMaintainList.get(0).getEtcId());
                    }
                    List<BeidouPaymentRecord> recordByVehicle = beidouPaymentRecordMapper.getRecordByVehicle(tenantVehicleRelVo.getVehicleCode().toString());
                    if (recordByVehicle != null && recordByVehicle.size() > 0) {
                        if (recordByVehicle.size() == 1) {
                            tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "至" + recordByVehicle.get(0).getInvalidDate());
                        } else {
                            tenantVehicleRelVo.setBeiDouSerivceDate(recordByVehicle.get(0).getEffectDate() + "..." + recordByVehicle.get(recordByVehicle.size() - 1).getInvalidDate());
                            tenantVehicleRelVo.setBeidouPaymentRecordList(recordByVehicle);
                            tenantVehicleRelVo.setBeiDouMulti(1);
                        }
                    }
                }
                if (tenantVehicleRelVo.getVehicleClass() != null) {
//                String codeName = readisUtil.getSysStaticData("VEHICLE_CLASS", tenantVehicleRelVo.getVehicleClass().toString()).getCodeName();
                    tenantVehicleRelVo.setVehicleClassName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "VEHICLE_CLASS", String.valueOf(tenantVehicleRelVo.getVehicleClass())));
                }
                if (tenantVehicleRelVo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
                    tenantVehicleRelVo.setAuthStateName("已认证");
                } else {
                    tenantVehicleRelVo.setAuthStateName("未认证");
                }
                tenantVehicleRelVo.setVehicleLengthName("");
                tenantVehicleRelVo.setVehicleStatusName("");
                try {
                    if (StringUtils.isNotBlank(tenantVehicleRelVo.getVehicleLength()) && !"-1".equals(tenantVehicleRelVo.getVehicleLength())) {
                        String vehicleLength1 = tenantVehicleRelVo.getVehicleLength();
                        if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() == 8) {

                            if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                                SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", tenantVehicleRelVo.getVehicleLength() + "");
                                if (vehicleStatusSubtype != null) {
                                    tenantVehicleRelVo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                                }
                            }
                        } else {
                            tenantVehicleRelVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", tenantVehicleRelVo.getVehicleLength()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (tenantVehicleRelVo.getVehicleStatus() != null && tenantVehicleRelVo.getVehicleStatus() > 0) {
                    Long vehicleStatus1 = Long.valueOf(tenantVehicleRelVo.getVehicleStatus());
                    for (SysStaticData sysStaticData : vehicleStatusList) {
                        if (sysStaticData.getId().equals(vehicleStatus1)) {
                            tenantVehicleRelVo.setVehicleStatusName(sysStaticData.getCodeName());
                            break;
                        }
                    }
                }

                tenantVehicleRelVo.setVehicleInfo(tenantVehicleRelVo.getVehicleStatusName() + "/" + tenantVehicleRelVo.getVehicleLengthName());
                tenantVehicleRelVo.getVehicleInfo();
            }
            //设置是否有审核权限
            if (CollectionUtils.isNotEmpty(busiIds)) {
                Map<Long, Boolean> hasPermission = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, busiIds, accessToken);
                for (TenantVehicleRelVo tenantVehicleRelVo : vehicleRelVoPage) {
                    Boolean flg = hasPermission.get(tenantVehicleRelVo.getEid());
                    tenantVehicleRelVo.setIsAuth(flg ? 1 : 0);
                }
            }
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"车牌号码", "车辆种类", "司机姓名", "司机手机",
                    "牌照类型", "归属车队", "车队手机", "合作车队",
                    "是否共享", "定位类型", "定位生效时间", "车辆类型",
                    "ECT卡号", "认证状态"};
            resourceFild = new String[]{"getPlateNumber", "getVehicleClassName", "getLinkman", "getMobilePhone",
                    "getLicenceTypeName", "getTenantName", "getLinkPhone", "getCooperationCount",
                    "getShareFlgName", "getLocationServName", "getBeiDouSerivceDate", "getVehicleInfo",
                    "getEtcCardNo", "getAuthStateName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(vehicleRelVoPage, showName, resourceFild,
                    TenantVehicleRelVo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, fieldName, inputStream.available());
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
    public Long getZYCount(Long vehicleCode, Long tenantId, Integer vehicleClass) {
        Long zyCount = tenantVehicleRelMapper.getZYCount(vehicleCode, tenantId, vehicleClass);
        return zyCount;
    }

    @Override
    public List<WorkbenchDto> getTableCompulsoryInsuranceCount() {
        LocalDateTime localDateTime = LocalDate.now().plusMonths(1).atStartOfDay();
        return tenantVehicleRelMapper.getTableCompulsoryInsuranceCount(localDateTime);
    }

    @Override
    public List<WorkbenchDto> getTableCommercialInsuranceCount() {
        LocalDateTime localDateTime = LocalDate.now().plusMonths(1).atStartOfDay();
        return tenantVehicleRelMapper.getTableCommercialInsuranceCount(localDateTime);
    }

    @Override
    public List<WorkbenchDto> getTableOwnerCarCount() {
        return tenantVehicleRelMapper.getTableOwnerCarCount();
    }

    @Override
    public List<WorkbenchDto> getTableTemporaryCarCount() {
        return tenantVehicleRelMapper.getTableTemporaryCarCount();
    }

    @Override
    public List<WorkbenchDto> getTableAttractCarCount() {
        return tenantVehicleRelMapper.getTableAttractCarCount();
    }

    @Override
    public TenantVehicleRel getZYVehicleByPlateNumber(String plateNumber) {
        LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantVehicleRel::getPlateNumber, plateNumber);
        queryWrapper.eq(TenantVehicleRel::getVehicleClass, VEHICLE_CLASS1);
        List<TenantVehicleRel> list = this.list(queryWrapper);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    @Override
    public List<TenantVehicleRel> doQueryTenantVehicleRelByRelIds(List<Long> relIdList) {
        LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TenantVehicleRel::getId, relIdList);
        return this.list(queryWrapper);
    }

    @Override
    public List<TenantVehicleRel> getTenantVehicleRels(Long driverUserId, Integer vehicleClass) {
        LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantVehicleRel::getDriverUserId, driverUserId);
        queryWrapper.eq(TenantVehicleRel::getVehicleClass, vehicleClass);
        return this.list(queryWrapper);
    }

    @Override
    public List<TenantVehicleRel> getTenantVehicleRelByDriverUserIdAndAuthState(Long driverUserId, Integer authState) {
        LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantVehicleRel::getDriverUserId, driverUserId);
        queryWrapper.eq(null != authState && authState > 0, TenantVehicleRel::getAuthState, authState);
        return this.list(queryWrapper);
    }

    public String getVehicleLengthName(String vehicleStatus, String vehicleLength) {

        String name = iSysStaticDataService.getSysStaticDatas("VEHICLE_STATUS", Integer.parseInt(vehicleLength));

       /* List<SysStaticData> lengthList = iSysStaticDataService.getSysStaticDataListByCodeName("VEHICLE_LENGTH");
        if (null != staticData && CollectionUtils.isNotEmpty(lengthList)) {
            for (SysStaticData lengthData : lengthList) {
                if (staticData.getCodeId().equals(lengthData.getCodeId()) && lengthData.getCodeValue().equals(vehicleLength)) {
                    name = lengthData.getCodeName();
                    break;
                }
            }

        }*/
        return name;
    }

    @Override
    public TenantVehicleRel getTenantVehicleRelByBeApplyTenantId(Long vehicleCode, Long beApplyTenantId) {
        LambdaQueryWrapper<TenantVehicleRel> lamdba = new QueryWrapper<TenantVehicleRel>().lambda();
        lamdba.eq(TenantVehicleRel::getVehicleCode, vehicleCode)
                .eq(TenantVehicleRel::getTenantId, beApplyTenantId)
                .last("limit 1");
        return this.getOne(lamdba);
    }

    /**
     * 实现功能: 查询车辆注册信息
     *
     * @param plateNumber
     * @return
     */
    public void getVehicleIsRegister(String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer vehicleClass = baseMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());

    }


    /**
     * 导出Excel(07版.xlsx)到文件服务器
     *
     * @param excelName Excel名称
     * @param sheetName sheet页名称
     * @param clazz     Excel要转换的类型
     * @param data      要导出的数据
     * @throws Exception
     */
    public static ImportOrExportRecords export2Fdfs(ImportOrExportRecords record, String excelName, String sheetName, Class clazz, List data) {
        try {
            // 这里URLEncoder.encode可以防止中文乱码
            excelName = URLEncoder.encode(excelName, "UTF-8");
            //response.setHeader("Content-disposition", "attachment;filename=" + excelName + ExcelTypeEnum.XLSX.getValue());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            EasyExcel.write(os, clazz).sheet(sheetName).doWrite(data);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, excelName, inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
        } catch (Exception e) {
            record.setState(3);
            e.printStackTrace();
        }
        return record;
    }

}
