package com.youming.youche.record.provider.service.impl.tenant;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.trailer.ITenantTrailerRelService;
import com.youming.youche.record.api.trailer.ITrailerLineRelService;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.api.vehicle.IVehicleIdleService;
import com.youming.youche.record.common.ChkIntfData;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.trailer.*;
import com.youming.youche.record.domain.vehicle.VehicleIdle;
import com.youming.youche.record.dto.TrailerManagementDto;
import com.youming.youche.record.dto.VehicleCertInfoDto;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineMapper;
import com.youming.youche.record.provider.mapper.trailer.*;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.utils.ReadisUtil;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.vo.OrderInfoVo;
import com.youming.youche.record.vo.StaffDataInfoVo;
import com.youming.youche.record.vo.VehicleLineRelsVo;
import com.youming.youche.record.vo.driver.TrailerManagementVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.utils.excel.ExcelParse;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
@DubboService(version = "1.0.0")
@Service
public class TrailerManagementServiceImpl extends BaseServiceImpl<TrailerManagementMapper, TrailerManagement> implements ITrailerManagementService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LoginUtils loginUtils;
    ;

    @Resource
    private TenantTrailerRelMapper tenantTrailerRelMapper;

    @Resource
    private TenantTrailerRelVerMapper tenantTrailerRelVerMapper;

    @Resource
    private TrailerLineRelMapper trailerLineRelMapper;

    @Resource
    private TrailerLineRelVerMapper trailerLineRelVerMapper;

    @Resource
    private TrailerManagementVerMapper trailerManagementVerMapper;

    @Resource
    private TrailerManagementMapper trailerManagementMapper;

    @Resource
    private UserDataInfoRecordMapper userDataInfoRecordMapper;

    @Resource
    private CmCustomerLineMapper cmCustomerLineMapper;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;

    @Autowired
    private ISysUserService sysUserService;

    @Resource
    private ITenantTrailerRelService iTenantTrailerRelService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @Resource
    ITrailerLineRelService iTrailerLineRelService;

    @Resource
    private IVehicleIdleService vehicleIdleService;


    private final static String percentAge = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";


    @Override
    public TrailerManagementVo getTrailerByTrailerId(Long trailerId, String acclnt) {
        LoginInfo loginInfo = loginUtils.get(acclnt);
        TrailerManagement trailerManagement = baseMapper.selectById(trailerId);

        QueryWrapper<TenantTrailerRel> queryWrapper = new QueryWrapper<TenantTrailerRel>();
        queryWrapper.eq("trailer_id", trailerId);
        queryWrapper.orderByDesc("id");
        List<TenantTrailerRel> tenantTrailerRelList = tenantTrailerRelMapper.selectList(queryWrapper);
        TenantTrailerRel tenantTrailerRel = null;
        if (tenantTrailerRelList != null && tenantTrailerRelList.size() > 0) {
            tenantTrailerRel = tenantTrailerRelList.get(0);
            if (null != tenantTrailerRel.getAttachedRootOrgTwoId()) {
                String nameById = iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), tenantTrailerRel.getAttachedRootOrgTwoId());
                tenantTrailerRel.setAttachedRootOrgTwoIdName(nameById);
            }
        }
        if (trailerManagement != null && null != trailerManagement.getTrailerMaterial()) {
            String material = readisUtil.getSysStaticData("TRAILER_MATERIAL", trailerManagement.getTrailerMaterial().toString()).getCodeName();
            trailerManagement.setTrailerMaterialName(material);
        }
        trailerManagement.setSourceProvinceName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_PROVINCE", String.valueOf(trailerManagement.getSourceProvince())));
        trailerManagement.setSourceCountyName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_DISTRICT", String.valueOf(trailerManagement.getSourceCounty())));
        trailerManagement.setSourceRegionName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_CITY", String.valueOf(trailerManagement.getSourceRegion())));


        QueryWrapper<TenantTrailerRelVer> queryWrapper1 = new QueryWrapper<TenantTrailerRelVer>();
        queryWrapper1.eq("trailer_Id", tenantTrailerRel)
                .eq("delete_flag", 9);
        List<TenantTrailerRelVer> tenantTrailerRelVerList = tenantTrailerRelVerMapper.selectList(queryWrapper1);
        TenantTrailerRelVer tenantTrailerRelVer = null;
        if (tenantTrailerRelVerList != null && tenantTrailerRelVerList.size() > 0) {
            tenantTrailerRelVer = tenantTrailerRelVerList.get(0);
        }
        if (tenantTrailerRelVer != null) {
            if (null != tenantTrailerRelVer.getAttachedRootOrgTwoId()) {
                String nameById = iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), tenantTrailerRelVer.getAttachedRootOrgTwoId());
                tenantTrailerRelVer.setAttachedRootOrgTwoIdName(nameById);
            }
            if (null != tenantTrailerRelVer.getTrailerOwnerShip()) {
                String codeName = readisUtil.getSysStaticData("OWNER_SHIP", tenantTrailerRelVer.getTrailerOwnerShip().toString()).getCodeName();
                tenantTrailerRelVer.setTrailerOwnerShipName(codeName);
            }
        }
        // ????????????????????????
        String trailerLineRelStr = trailerLineRelMapper.getTrailerLineRelStr(trailerId);

        List<TrailerLineRel> trailerLineRelList = trailerLineRelMapper.getTrailerLineRelList(trailerId, 1);

        TrailerManagementVo trailerManagementVo = new TrailerManagementVo();
        trailerManagementVo.setTrailer(trailerManagement);
        trailerManagementVo.setTrailerRel(tenantTrailerRel);
        trailerManagementVo.setTrailerHis(tenantTrailerRelVer);
        trailerManagementVo.setLineRelStr(trailerLineRelStr);
        trailerManagementVo.setTrailerLineRelList(trailerLineRelList);
        return trailerManagementVo;

    }


    /**
     * ????????????????????????
     *
     * @param trailerId ????????????ID
     * @return
     */
    @Override
    public TrailerManagementVo getTrailerVerByTrailerId(Long trailerId, String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<TrailerManagementVer> trailerManagementVerList = trailerManagementVerMapper.getTrailerManagementVerList(trailerId, 0, loginInfo.getTenantId());
        TrailerManagementVer trailerManagementVer = null;
        if (trailerManagementVerList != null && trailerManagementVerList.size() > 0) {
            trailerManagementVer = trailerManagementVerList.get(0);
        }
        if (null != trailerManagementVer) {
            if (null != trailerManagementVer.getTrailerMaterial()) {
                String material = readisUtil.getSysStaticData("TRAILER_MATERIAL", trailerManagementVer.getTrailerMaterial().toString()).getCodeName();
                trailerManagementVer.setTrailerMaterialName(material);
            }
            trailerManagementVer.setSourceProvinceName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_PROVINCE", String.valueOf(trailerManagementVer.getSourceProvince())));
            trailerManagementVer.setSourceCountyName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_DISTRICT", String.valueOf(trailerManagementVer.getSourceCounty())));
            trailerManagementVer.setSourceRegionName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_CITY", String.valueOf(trailerManagementVer.getSourceRegion())));

        }


        List<TenantTrailerRelVer> tenantTrailerRelVerList = tenantTrailerRelVerMapper.getTenantTrailerRelVerList(trailerId, loginInfo.getTenantId(), 0);
        TenantTrailerRelVer tenantTrailerRelVer = null;
        if (tenantTrailerRelVerList != null && tenantTrailerRelVerList.size() > 0) {
            tenantTrailerRelVer = tenantTrailerRelVerList.get(0);
        }
        if (null != tenantTrailerRelVer) {
            if (null != tenantTrailerRelVer.getAttachedRootOrgTwoId()) {
                String nameById = iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), tenantTrailerRelVer.getAttachedRootOrgTwoId());
                tenantTrailerRelVer.setAttachedRootOrgTwoIdName(nameById);
            }
            if (null != tenantTrailerRelVer.getTrailerOwnerShip()) {
                String codeName = readisUtil.getSysStaticData("OWNER_SHIP", tenantTrailerRelVer.getTrailerOwnerShip().toString()).getCodeName();
                tenantTrailerRelVer.setTrailerOwnerShipName(codeName);
            }
        }


        //????????????
        String trailerLineRelVerStr = tenantTrailerRelVerMapper.getTrailerLineRelVerStr(trailerId);
        TrailerManagementVo trailerManagementVo = new TrailerManagementVo();
        trailerManagementVo.setTrailerManagementVer(trailerManagementVer);
        trailerManagementVo.setTrailerHis(tenantTrailerRelVer);
        trailerManagementVo.setLineRelStr(trailerLineRelVerStr);

        return trailerManagementVo;
    }


    @Override
    public Page<com.youming.youche.record.vo.TrailerManagementVo> doQueryTrailerList(Page<com.youming.youche.record.vo.TrailerManagementVo> page, String trailerNumber, Integer isState, Integer sourceProvince, Integer sourceRegion, Integer sourceCounty, Integer trailerMaterial, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List busiIds = new ArrayList();
        Page<com.youming.youche.record.vo.TrailerManagementVo> trailerManagementVoPage = baseMapper.doQueryTrailerList(page, loginInfo.getTenantId(), trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial);
        if (trailerManagementVoPage.getRecords() != null && trailerManagementVoPage.getRecords().size() > 0) {
            for (com.youming.youche.record.vo.TrailerManagementVo record : trailerManagementVoPage.getRecords()) {
                busiIds.add(record.getId());
                if (record.getTrailerMaterial() != null) {
                    if (record.getTrailerMaterial() == 1) {
                        record.setTrailerMaterialName("?????????");
                    } else if (record.getTrailerMaterial() == 2) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 3) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 4) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 5) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 6) {
                        record.setTrailerMaterialName("??????");
                    }
                }
                String sourceRegion1 = "";
                if (record.getSourceRegion() != null) {
                    sourceRegion1 = baseMapper.getSourceRegion(record.getSourceRegion());
                }
                String sourceCounty1 = "";
                if (record.getSourceCounty() != null) {
                    sourceCounty1 = baseMapper.getSourceCounty(record.getSourceCounty());
                }
                String name = "";
                if (record.getIsState() != null) {
                    if (record.getIsState() == 1) {
                        name = name + "??????";
                    } else if (record.getIsState() == 2) {
                        name = name + "??????";
                    }
                }
                String positionStatusName = "";
                if (StringUtils.isNotEmpty(sourceRegion1)) {
                    positionStatusName += sourceRegion1;
                }
                if (StringUtils.isNotEmpty(sourceCounty1)) {
                    positionStatusName += "/" + sourceCounty1;
                }
                if (StringUtils.isNotEmpty(name)) {
                    positionStatusName += "-" + name;
                }
                record.setPositionStatusName(positionStatusName);

            }
            if (!busiIds.isEmpty()) {
                Map<Long, Boolean> hasPermissionMap = null;
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_TRAILER, busiIds, accessToken);
                for (com.youming.youche.record.vo.TrailerManagementVo vo : trailerManagementVoPage.getRecords()) {
                    Long trailerId = vo.getId();
                    Boolean flg = hasPermissionMap.get(trailerId);
                    vo.setAudit(flg ? 1 : 0);
                }
            }
        }
        return trailerManagementVoPage;
    }


    @Transactional
    @Override
    public Boolean updateTrailerManagement(Short flag, Long vid, String trailerNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UpdateWrapper<TrailerManagement> lambda = new UpdateWrapper<TrailerManagement>();
        lambda.set("idle", flag)
                .eq("id", vid);
        boolean flags;
        this.update(lambda);
        VehicleIdle vehicleIdle = new VehicleIdle();
        vehicleIdle.setTenantId(loginInfo.getTenantId());
        vehicleIdle.setFlag(1);
        //??????????????????????????????
        List<VehicleIdle> vehicleIdles = vehicleIdleService.queryVehicleIdle(vid);
        if (flag == 0) {
            vehicleIdle = vehicleIdles.get(0);
            vehicleIdle.setUpdateTime(LocalDateTime.now());
        } else {
            if (vehicleIdles != null && vehicleIdles.size() > 0) {
                vehicleIdle = vehicleIdles.get(0);
                vehicleIdle.setCreateTime(LocalDateTime.now());
                vehicleIdle.setUpdateTime(null);
            } else {
                vehicleIdle.setPlateNumber(trailerNumber);
                vehicleIdle.setCreateTime(LocalDateTime.now());
                vehicleIdle.setUpdateTime(null);
                vehicleIdle.setVid(vid);
            }
        }
        flags = vehicleIdleService.saveOrUpdate(vehicleIdle);
        return flags;
    }

    @Override
    public Page<com.youming.youche.record.vo.TrailerManagementVo> doQueryTrailerListByorgId(Page<com.youming.youche.record.vo.TrailerManagementVo> page, String trailerNumber, Integer isState, Integer sourceProvince, Integer sourceRegion, Integer sourceCounty, Integer trailerMaterial, String accessToken, Long orgId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List busiIds = new ArrayList();
        Page<com.youming.youche.record.vo.TrailerManagementVo> trailerManagementVoPage = baseMapper.doQueryTrailerListByOrgId(page, loginInfo.getTenantId(), trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial, orgId);
        if (trailerManagementVoPage.getRecords() != null && trailerManagementVoPage.getRecords().size() > 0) {
            for (com.youming.youche.record.vo.TrailerManagementVo record : trailerManagementVoPage.getRecords()) {
                busiIds.add(record.getId());
                if (record.getTrailerMaterial() != null) {
                    if (record.getTrailerMaterial() == 1) {
                        record.setTrailerMaterialName("?????????");
                    } else if (record.getTrailerMaterial() == 2) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 3) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 4) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 5) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 6) {
                        record.setTrailerMaterialName("??????");
                    }
                }
                if (record.getSourceRegion() != null && record.getSourceCounty() != null && record.getIsState() != null) {
                    String sourceRegion1 = baseMapper.getSourceRegion(record.getSourceRegion());
                    String sourceCounty1 = baseMapper.getSourceCounty(record.getSourceCounty());
                    String name = "";
                    if (record.getIsState() == 1) {
                        name = name + "??????";
                    } else if (record.getIsState() == 2) {
                        name = name + "??????";
                    }
                    record.setPositionStatusName(sourceRegion1 + "/" + sourceCounty1 + "-" + name);
                }
            }
            if (!busiIds.isEmpty()) {
                Map<Long, Boolean> hasPermissionMap = null;
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_TRAILER, busiIds, accessToken);
                for (com.youming.youche.record.vo.TrailerManagementVo vo : trailerManagementVoPage.getRecords()) {
                    Long trailerId = vo.getId();
                    Boolean flg = hasPermissionMap.get(trailerId);
                    vo.setAudit(flg ? 1 : 0);
                }
            }

        }
        return trailerManagementVoPage;
    }


    @Override
    @Async
    public void doQueryTrailerListExport(String trailerNumber, Integer isState, Integer sourceProvince, Integer sourceRegion, Integer sourceCounty, Integer trailerMaterial, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<com.youming.youche.record.vo.TrailerManagementVo> trailerManagementVos = baseMapper.doQueryTrailerListExport(loginInfo.getTenantId(), trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial);
        if (trailerManagementVos != null && trailerManagementVos.size() > 0) {
            for (com.youming.youche.record.vo.TrailerManagementVo record : trailerManagementVos) {
                if (record.getTrailerMaterial() != null) {
                    if (record.getTrailerMaterial() == 1) {
                        record.setTrailerMaterialName("?????????");
                    } else if (record.getTrailerMaterial() == 2) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 3) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 4) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 5) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 6) {
                        record.setTrailerMaterialName("??????");
                    }
                }

                if (record.getSourceRegion() != null && record.getSourceCounty() != null && record.getIsState() != null) {
                    String sourceRegion1 = baseMapper.getSourceRegion(record.getSourceRegion());
                    String sourceCounty1 = baseMapper.getSourceCounty(record.getSourceCounty());
                    String name = "";
                    if (record.getIsState() == 1) {
                        name = name + "??????";
                    } else if (record.getIsState() == 2) {
                        name = name + "??????";
                    }
                    record.setPositionStatusName(sourceRegion1 + "/" + sourceCounty1 + "-" + name);
                }
            }
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"????????????", "????????????", "????????????", "????????????", "??????"};
            resourceFild = new String[]{"getTrailerNumber", "getPositionStatusName", "getTrailerMaterialName", "getUsageCount", "getAuditStateName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(trailerManagementVos, showName, resourceFild,
                    com.youming.youche.record.vo.TrailerManagementVo.class,
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
    public Page<com.youming.youche.record.vo.TrailerManagementVo> doQueryTrailerListDel(Page<com.youming.youche.record.vo.TrailerManagementVo> page, String trailerNumber, Integer isState, Integer sourceProvince, Integer sourceRegion, Integer sourceCounty, Integer trailerMaterial, Integer deleteFlag, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<com.youming.youche.record.vo.TrailerManagementVo> trailerManagementVoPage = baseMapper.doQueryTrailerListDel(page, loginInfo.getTenantId(), trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial);
        if (trailerManagementVoPage.getRecords() != null && trailerManagementVoPage.getRecords().size() > 0) {
            for (com.youming.youche.record.vo.TrailerManagementVo record : trailerManagementVoPage.getRecords()) {
                if (record.getTrailerMaterial() != null) {
                    if (record.getTrailerMaterial() == 1) {
                        record.setTrailerMaterialName("?????????");
                    } else if (record.getTrailerMaterial() == 2) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 3) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 4) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 5) {
                        record.setTrailerMaterialName("??????");
                    } else if (record.getTrailerMaterial() == 6) {
                        record.setTrailerMaterialName("??????");
                    }
                }

                if (record.getSourceRegion() != null && record.getSourceCounty() != null && record.getIsState() != null) {
                    String sourceRegion1 = baseMapper.getSourceRegion(record.getSourceRegion());
                    String sourceCounty1 = baseMapper.getSourceCounty(record.getSourceCounty());
                    String name = "";
                    if (record.getIsState() == 1) {
                        name = name + "??????";
                    } else if (record.getIsState() == 2) {
                        name = name + "??????";
                    }
                    record.setPositionStatusName(sourceRegion1 + "/" + sourceCounty1 + "-" + name);
                }
            }
        }
        return trailerManagementVoPage;
    }

    @Override
    @Async
    public void doQueryTrailerListDelExport(String trailerNumber, Integer isState, Integer sourceProvince, Integer sourceRegion, Integer sourceCounty, Integer trailerMaterial, Integer deleteFlag, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        try {
            List<com.youming.youche.record.vo.TrailerManagementVo> trailerManagementVos = baseMapper.doQueryTrailerListDelExport(loginInfo.getTenantId(), trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial);
            if (trailerManagementVos != null && trailerManagementVos.size() > 0) {
                for (com.youming.youche.record.vo.TrailerManagementVo record : trailerManagementVos) {
                    if (record.getTrailerMaterial() != null) {
                        if (record.getTrailerMaterial() == 1) {
                            record.setTrailerMaterialName("?????????");
                        } else if (record.getTrailerMaterial() == 2) {
                            record.setTrailerMaterialName("??????");
                        } else if (record.getTrailerMaterial() == 3) {
                            record.setTrailerMaterialName("??????");
                        } else if (record.getTrailerMaterial() == 4) {
                            record.setTrailerMaterialName("??????");
                        } else if (record.getTrailerMaterial() == 5) {
                            record.setTrailerMaterialName("??????");
                        } else if (record.getTrailerMaterial() == 6) {
                            record.setTrailerMaterialName("??????");
                        }
                    }
                    if (record.getSourceRegion() != null && record.getSourceCounty() != null && record.getIsState() != null) {
                        String sourceRegion1 = baseMapper.getSourceRegion(record.getSourceRegion());
                        String sourceCounty1 = baseMapper.getSourceCounty(record.getSourceCounty());
                        String name = "";
                        if (record.getIsState() == 1) {
                            name = name + "??????";
                        } else if (record.getIsState() == 2) {
                            name = name + "??????";
                        }
                        record.setPositionStatusName(sourceRegion1 + "/" + sourceCounty1 + "-" + name);
                    }
                }
                String[] showName = null;
                String[] resourceFild = null;
                showName = new String[]{"????????????", "????????????", "????????????", "????????????"};
                resourceFild = new String[]{"getTrailerNumber", "getPositionStatusName", "getTrailerMaterialName", "getUsageCount"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(trailerManagementVos, showName,
                        resourceFild, com.youming.youche.record.vo.TrailerManagementVo.class,
                        null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "??????????????????.xlsx", inputStream.available());
                os.close();
                inputStream.close();
                importOrExportRecords.setMediaUrl(path);
                importOrExportRecords.setState(2);
                importOrExportRecordsService.update(importOrExportRecords);
            } else {
                importOrExportRecords.setState(4);
                importOrExportRecordsService.update(importOrExportRecords);
            }
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }

    }

    @Override
    public Integer deleteTrailer(Long trailerId, String accessToken) {
        Integer num = 0;
        QueryWrapper<TenantTrailerRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trailer_id", trailerId);
        List<TenantTrailerRel> tenantTrailerRelList = tenantTrailerRelMapper.selectList(queryWrapper);
        TenantTrailerRel tenantTrailerRel = null;
        if (tenantTrailerRelList != null && tenantTrailerRelList.size() > 0) {
            tenantTrailerRel = tenantTrailerRelList.get(0);
        }
        if (tenantTrailerRel != null) {
            TenantTrailerRelVer relVer = new TenantTrailerRelVer();
            BeanUtil.copyProperties(tenantTrailerRel, relVer);
            int i = tenantTrailerRelMapper.deleteById(tenantTrailerRel);
            num = num + i;
            relVer.setDeleteFlag(9);
            relVer.setRelId(tenantTrailerRel.getId());
            int i1 = tenantTrailerRelVerMapper.insert(relVer);
            num = num + i1;
            saveSysOperLog(SysOperLogConst.BusiCode.Trailer, SysOperLogConst.OperType.Del, "??????????????????", accessToken, trailerId);

        }
        return num;
    }

    @Transactional
    @Override
    public ResponseResult doSaveOrUpdate(TrailerManagementDto trailerManagementDto, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // ????????????
        Boolean flag = null;
        if (StringUtils.isEmpty(trailerManagementDto.getTrailerNumber())) {
            throw new BusinessException("?????????????????????");
        }
        if (!ChkIntfData.chkPlateNumber(trailerManagementDto.getTrailerNumber())) {
            throw new BusinessException("??????????????????????????????");
        }
        if (trailerManagementDto.getTrailerOwnerShip() != null && trailerManagementDto.getTrailerOwnerShip() < 0) {
            throw new BusinessException("?????????????????????");
        }
        if (trailerManagementDto.getTrailerMaterial() != null && trailerManagementDto.getTrailerMaterial() < 0) {
            throw new BusinessException("??????????????????");
        }
        if (StringUtils.isEmpty(trailerManagementDto.getTrailerLoad()) || !(CommonUtil.isNumber(trailerManagementDto.getTrailerLoad()))
        ) {
            throw new BusinessException("???????????????????????????");
        }
        if (StringUtils.isEmpty(trailerManagementDto.getTrailerVolume()) || !(CommonUtil.isNumber(trailerManagementDto.getTrailerVolume()))) {
            throw new BusinessException("??????????????????");
        }
        if (StringUtils.isEmpty(trailerManagementDto.getTrailerLength()) || !(CommonUtil.isNumber(trailerManagementDto.getTrailerLength()))
        ) {
            throw new BusinessException("???????????????????????????");
        }
        if (StringUtils.isEmpty(trailerManagementDto.getTrailerWide()) || !(CommonUtil.isNumber(trailerManagementDto.getTrailerWide()))
        ) {
            throw new BusinessException("???????????????????????????");
        }
        if (StringUtils.isEmpty(trailerManagementDto.getTrailerHigh()) || !(CommonUtil.isNumber(trailerManagementDto.getTrailerHigh()))
        ) {
            throw new BusinessException("???????????????????????????");
        }
        if (trailerManagementDto.getIsState() != null && trailerManagementDto.getIsState() < 0) {
            throw new BusinessException("????????????????????????");
        }
        if (trailerManagementDto.getSourceProvince() != null && trailerManagementDto.getSourceProvince() < 0) {
            throw new BusinessException("??????????????????????????????");
        }
        if (trailerManagementDto.getSourceRegion() != null && trailerManagementDto.getSourceRegion() < 0) {
            throw new BusinessException("??????????????????????????????");
        }
        if (trailerManagementDto.getIsEdit() != null && trailerManagementDto.getIsEdit() < 0) {
            throw new BusinessException("??????????????????????????????");
        }


        List<TrailerLineRel> trailerLineRelList = Lists.newArrayList();
        List<TrailerLineRelVer> trailerLineRelVerList = Lists.newArrayList();

        //??????
        if (trailerManagementDto.getIsEdit() == 2) {

            TrailerManagement trailer = baseMapper.getTrailerManagement(trailerManagementDto.getTrailerNumber(), loginInfo.getTenantId());
            TenantTrailerRel trailerRel = tenantTrailerRelMapper.getTenantTrailerRelByTrailerId(trailer.getId(), loginInfo.getTenantId());

          /*  if (trailer.getIsAutit() == 0 || trailer.getIsAutit() == 2) {//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????ver??????
                if (trailer.getIsAutit() == 2) {
                    saveSysOperLog(SysOperLogConst.BusiCode.Trailer, SysOperLogConst.OperType.Update, "??????????????????", accessToken, trailer.getId());
                    //??????????????????
                    Map inMap = new HashMap();
                    inMap.put("svName", "trailerTF");
                    boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_TRAILER, trailer.getId(), SysOperLogConst.BusiCode.Trailer, inMap, accessToken);
                    if (!bool) {
                        throw new BusinessException("???????????????????????????");
                    }
                }

                long oriTrailerId = trailer.getId();//?????????ID???????????????????????????ID?????????

                BeanUtil.copyProperties(trailerManagementDto,trailer);
                trailer.setId(oriTrailerId);
                trailer.setIsAutit(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);//????????????????????????????????????2???????????????????????????????????????

                BeanUtil.copyProperties(trailerManagementDto,trailerRel);
                trailerRel.setTrailerId(oriTrailerId);
                trailerRel.setIsAutit(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
                baseMapper.updateById(trailer);
                tenantTrailerRelMapper.updateById(trailerRel);

                //??????????????????
                List<VehicleLineRelsVo> vehicleLineRelsArray = trailerManagementDto.getVehicleLineRelsArray();
                if (vehicleLineRelsArray != null && vehicleLineRelsArray.size() > 0) {
                    //????????????????????????
//                    trailerSV.delLineRelList(trailer.getTrailerId());
                    iTrailerLineRelService.delLineRelList(trailer.getId());
                    //????????????????????????
                    for (int i = 0; i < vehicleLineRelsArray.size(); i++) {
                        VehicleLineRelsVo vehicleLineRelsVo = vehicleLineRelsArray.get(i);
                        TrailerLineRel lineRel = new TrailerLineRel();
                        BeanUtil.copyProperties(vehicleLineRelsVo,lineRel);
                        lineRel.setState(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                        lineRel.setTrailerId(trailer.getId());
                        lineRel.setTrailerNumber(trailer.getTrailerNumber());
                        iTrailerLineRelService.save(lineRel);
                    }

                }
            }else {*/
            TrailerManagementVer trailerVer = new TrailerManagementVer();
            BeanUtil.copyProperties(trailerManagementDto, trailerVer);

       /*         if(null != trailerManagementDto.getTrailerStatus()){
                    trailerVer.setTrailerStatus(trailerManagementDto.getTrailerStatus());
                }
                if (null != trailerManagementDto.getRegistrationTime() && !trailerManagementDto.getRegistrationTime().equals("")){
                    trailerVer.setRegistrationTime(trailerManagementDto.getRegistrationTime());
                }
                if(null != trailerManagementDto.getCarVersion() ){
                    trailerVer.setCraVersion(trailerManagementDto.getCarVersion());
                }
                if(null != trailerManagementDto.getTrailerPictureUrl()){
                    trailerVer.setTrailerPictuerUrl(trailerManagementDto.getTrailerPictureUrl());
                }
                if(null != trailerManagementDto.getTrailerPictureId()){
                    trailerVer.setTrailerPictuerId(trailerManagementDto.getTrailerPictureId());
                }
                if (null != trailerManagementDto.getRegistrationTime()){
                    trailerVer.setRegistrationTime(trailerManagementDto.getRegistrationTime());
                }*/

            TenantTrailerRelVer trailerRelVer = new TenantTrailerRelVer();
            BeanUtil.copyProperties(trailerManagementDto, trailerRelVer);
            trailerRelVer.setTypeFee(trailerManagementDto.getTyreFee());
            trailerRelVer.setSeasonalVerlTime(trailerManagementDto.getSeasonalVeriTime());

            if (trailerManagementDto.getDepreciatedMonth() != null) {
                trailerRelVer.setDepreaciatedMonth(trailerManagementDto.getDepreciatedMonth());
            }

    /*            trailerRelVer.setRelId(trailerManagementDto.getRelId());
                if(trailerManagementDto.getTrailerId() != null){
                    trailerRelVer.setTrailerId(trailerManagementDto.getTrailerId());
                }
                if(trailerManagementDto.getAttachedMan() != null){
                    trailerRelVer.setAttachedMan(trailerManagementDto.getAttachedMan());
                }
                if(trailerManagementDto.getAttachedManId() != null){
                    trailerRelVer.setAttachedManId(trailerManagementDto.getAttachedManId());
                }
                if(trailerManagementDto.getDepreciatedMonth() != null){
                    trailerRelVer.setDepreaciatedMonth(trailerManagementDto.getDepreciatedMonth());
                }*/
         /*           boolean b;
                    try {
                        b = switchChangedVal(trailer, trailerVer, trailerRel, trailerRelVer);
                    } catch (Exception e) {
                        b = true;
                    }

                    if (b) {*/
            //????????????????????????????????????????????????
            trailerManagementVerMapper.updtTrailerVerAuditStatus(SysStaticDataEnum.EXPENSE_STATE.CANCEL, trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, loginInfo.getTenantId());

            //????????????????????????????????????????????????
            tenantTrailerRelVerMapper.updtTrailerRelVerAuditStatus(SysStaticDataEnum.EXPENSE_STATE.CANCEL, trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, loginInfo.getTenantId());

            //??????????????????????????????
            trailerLineRelVerMapper.updtLineRelVerStatus(SysStaticDataEnum.EXPENSE_STATE.CANCEL, trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);

            //????????????ver
            trailerVer.setTrailerId(trailer.getId());
            trailerVer.setTenantId(loginInfo.getTenantId());
            trailerVer.setIsAutit(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            trailerVer.setVehicleValidityExpiredTime(trailerManagementDto.getVehicleValidityExpiredTime());
            trailerVer.setVehicleValidityTime(trailerManagementDto.getVehicleValidityTime());
            trailerVer.setTrailerMaterial(trailerManagementDto.getTrailerMaterial());
            trailerVer.setTrailerLength(trailerManagementDto.getTrailerLength());
            trailerVer.setTrailerWide(trailerManagementDto.getTrailerWide());
            trailerVer.setTrailerHigh(trailerManagementDto.getTrailerHigh());
            trailerVer.setSource(trailerManagementDto.getSource());
            trailerVer.setTrailerVolume(trailerManagementDto.getTrailerVolume());
            if (trailerManagementDto.getRegistrationTime() != null && !trailerManagementDto.getRegistrationTime().equals("")) {
                trailerVer.setRegistrationTime(trailerManagementDto.getRegistrationTime());
            }
            trailerVer = (TrailerManagementVer) nullStringToNull(trailerVer, TrailerManagementVer.class);
            if (null == trailerVer.getId()) {
                trailerManagementVerMapper.insert(trailerVer);
            } else {
                trailerManagementVerMapper.updateById(trailerVer);
            }
//                        //????????????ver
//                        trailerVer.setTrailerId(trailer.getTrailerId());
//                        trailerVer.setIsAutit(EXPENSE_STATE.CHECK_PENDING);
//                        trailerSV.saveOrUpdate(trailerVer);

            //????????????ver???
            trailerRelVer.setRelId(trailerRel.getId());
            trailerRelVer.setIsAutit(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            trailerRelVer.setCreateTime(LocalDateTime.now());
            trailerRelVer.setUpdateTime(LocalDateTime.now());
            trailerRelVer.setUpdateOpId(loginInfo.getUserInfoId());
            trailerRelVer.setTenantId(loginInfo.getTenantId());
            trailerRelVer = (TenantTrailerRelVer) nullStringToNull(trailerRelVer, TenantTrailerRelVer.class);
            tenantTrailerRelVerMapper.insert(trailerRelVer);


            if (trailerManagementDto.getVehicleLineRelsArray() != null && trailerManagementDto.getVehicleLineRelsArray().size() > 0) {
                List<VehicleLineRelsVo> vehicleLineRelsArray = trailerManagementDto.getVehicleLineRelsArray();
                for (int i = 0; i < vehicleLineRelsArray.size(); i++) {
                    VehicleLineRelsVo lineRelVer = vehicleLineRelsArray.get(i);
                    TrailerLineRelVer trailerLineRelVer = new TrailerLineRelVer();
                    trailerLineRelVer.setTrailerId(trailer.getId());
                    trailerLineRelVer.setTrailerNumber(trailer.getTrailerNumber());
                    trailerLineRelVer.setVerState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
                    trailerLineRelVer.setLineCodeRule(lineRelVer.getLineCodeRule());
                    trailerLineRelVer.setLineId(lineRelVer.getLineId());
                    trailerLineRelVer.setState(lineRelVer.getState());
                    if (null == trailerLineRelVer.getId()) {
                        trailerLineRelVerMapper.insert(trailerLineRelVer);
                    } else {
                        trailerLineRelVerMapper.updateById(trailerLineRelVer);
                    }
                }

            }

            saveSysOperLog(SysOperLogConst.BusiCode.Trailer, SysOperLogConst.OperType.Update, "??????????????????", accessToken, trailer.getId());
            //??????????????????
            Map inMap = new HashMap();
            inMap.put("svName", "trailerTF");
            boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_TRAILER, trailer.getId(), SysOperLogConst.BusiCode.Trailer, inMap, accessToken);
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }
//                    }
            trailer.setTrailerSate(1);
            baseMapper.updateById(trailer);
            tenantTrailerRelMapper.updateById(trailerRel);
//            }
            return ResponseResult.success("???????????? ?????????");
        } else if (trailerManagementDto.getIsEdit() == 1) {
            //??????
            Long trailerId = 0L;

            Long tenantId = loginInfo.getTenantId();

            TrailerManagement trailerManagement = baseMapper.getTrailerManagement(trailerManagementDto.getTrailerNumber(), tenantId);
            TenantTrailerRel tailerRel = tenantTrailerRelMapper.getTenantTrailerRel(trailerManagementDto.getTrailerNumber(), tenantId);

            if (trailerManagement != null || tailerRel != null) {
                throw new BusinessException("??????????????????????????????");
            }

            TrailerManagement tmObj = new TrailerManagement();
            BeanUtil.copyProperties(trailerManagementDto, tmObj);
            if (null != trailerManagementDto.getVehicleValidityExpiredTime()) {
                String time = trailerManagementDto.getVehicleValidityExpiredTime().replaceAll(" ", "");

                tmObj.setVehicleValidityExpiredTime(LocalDate.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            tmObj.setVehicleValidityTime(trailerManagementDto.getVehicleValidityTime());
            tmObj.setIsAutit(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
            tmObj.setTenantId(tenantId);
            if (null == tmObj.getVehicleValidityTime() || tmObj.getVehicleValidityTime().equals("")) {
                tmObj.setVehicleValidityTime(null);
            }
            if (null == tmObj.getRegistationTime() || tmObj.getRegistationTime().equals("")) {
                tmObj.setRegistationTime(null);
            }
            if (null != trailerManagementDto && trailerManagementDto.getRegistrationTime() != null && !trailerManagementDto.getRegistrationTime().equals("")) {
                tmObj.setRegistationTime(trailerManagementDto.getRegistrationTime());
            }
            if (null == tmObj.getVehicleValidityTime() || tmObj.getVehicleValidityTime().equals("")) {
                tmObj.setVehicleValidityTime(null);
            }
            if (null == tmObj.getOperateValidityTime() || tmObj.getOperateValidityTime().equals("")) {
                tmObj.setOperateValidityTime(null);
            }
            if (null == tmObj.getVehicleValidityExpiredTime() || tmObj.getVehicleValidityExpiredTime().equals("")) {
                tmObj.setVehicleValidityExpiredTime(null);
            }
            if (null == tmObj.getOperateValidityExpiredTime() || tmObj.getOperateValidityExpiredTime().equals("")) {
                tmObj.setOperateValidityExpiredTime(null);
            }
            tmObj.setTrailerSate(0);
            baseMapper.insert(tmObj);
            trailerId = tmObj.getId();
            TenantTrailerRel relObj = new TenantTrailerRel();
            BeanUtil.copyProperties(trailerManagementDto, relObj);
            if (trailerManagementDto.getAttachedMan() != null) {
                relObj.setAttachedMan(trailerManagementDto.getAttachedMan());
            }
            if (trailerManagementDto.getAttachedManId() != null) {
                relObj.setAttachedManId(trailerManagementDto.getAttachedManId());
            }
            if (trailerManagementDto.getDepreciatedMonth() != null) {
                relObj.setDepreciatedMonth(trailerManagementDto.getDepreciatedMonth());
            }
            relObj.setTrailerId(trailerId);
            relObj.setIsAutit(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
            relObj.setTenantId(tenantId);
            if (null == relObj.getPurchaseDate() || relObj.getPurchaseDate().equals("")) {
                relObj.setPurchaseDate(null);
            }
            if (null == relObj.getPrevMaintainTime() || relObj.getPrevMaintainTime().equals("")) {
                relObj.setPrevMaintainTime(null);
            }
            if (null == relObj.getInsuranceExpiredTime() || relObj.getInsuranceExpiredTime().equals("")) {
                relObj.setInsuranceExpiredTime(null);
            }
            if (null == relObj.getInsuranceTime() || relObj.getInsuranceTime().equals("")) {
                relObj.setInsuranceTime(null);
            }
            if (null == relObj.getSeasonalVeriTime() || relObj.getSeasonalVeriTime().equals("")) {
                relObj.setSeasonalVeriTime(null);
            }
            if (null == relObj.getAnnualVeriTime() || relObj.getAnnualVeriTime().equals("")) {
                relObj.setAnnualVeriTime(null);
            }
            if (null == relObj.getAnnualVeriExpiredTime() || relObj.getAnnualVeriExpiredTime().equals("")) {
                relObj.setAnnualVeriExpiredTime(null);
            }
            if (null == relObj.getSeasonalVeriExpiredTime() || relObj.getSeasonalVeriExpiredTime().equals("")) {
                relObj.setSeasonalVeriExpiredTime(null);
            }
            if (null == relObj.getInsuranceExpiredTime() || relObj.getInsuranceExpiredTime().equals("")) {
                relObj.setInsuranceExpiredTime(null);
            }
            if (null == relObj.getBusinessInsuranceTime() || relObj.getBusinessInsuranceTime().equals("")) {
                relObj.setBusinessInsuranceTime(null);
            }
            if (null == relObj.getBusinessInsuranceExpiredTime() || relObj.getBusinessInsuranceExpiredTime().equals("")) {
                relObj.setBusinessInsuranceExpiredTime(null);
            }
            if (null == relObj.getOtherInsuranceTime() || relObj.getOtherInsuranceTime().equals("")) {
                relObj.setOtherInsuranceTime(null);
            }
            if (null == relObj.getOtherInsuranceExpiredTime() || relObj.getOtherInsuranceExpiredTime().equals("")) {
                relObj.setOtherInsuranceExpiredTime(null);
            }
            tenantTrailerRelMapper.insert(relObj);

            //??????????????????
            if (trailerManagementDto.getVehicleLineRelsArray() != null && trailerManagementDto.getVehicleLineRelsArray().size() > 0) {
                List<VehicleLineRelsVo> vehicleLineRelsArray = trailerManagementDto.getVehicleLineRelsArray();
                for (int i = 0; i < vehicleLineRelsArray.size(); i++) {
                    VehicleLineRelsVo lineRel = vehicleLineRelsArray.get(i);
                    TrailerLineRel trailerLineRel = new TrailerLineRel();
                    trailerLineRel.setTrailerId(trailerId);
                    trailerLineRel.setTrailerNumber(tmObj.getTrailerNumber());
                    trailerLineRel.setState(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
                    trailerLineRel.setLineCodeRule(lineRel.getLineCodeRule());
                    trailerLineRel.setLineId(lineRel.getLineId());
                    trailerLineRel.setState(lineRel.getState());
                    trailerLineRelMapper.insert(trailerLineRel);
                }
            }
            saveSysOperLog(SysOperLogConst.BusiCode.Trailer, SysOperLogConst.OperType.Add, "??????????????????", accessToken, trailerId);

            //logSV.saveSysOperLog(SysOperLogConst.BusiCode.Trailer, trailerId, SysOperLogConst.OperType.Add, "??????????????????");
            //??????????????????
            Map inMap = new HashMap();
            inMap.put("svName", "trailerTF");
            boolean bool = false;
            try {
                if (trailerId != null) {
                    bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_TRAILER, trailerId, SysOperLogConst.BusiCode.Trailer, inMap, accessToken);
                } else {
                    return ResponseResult.failure("???????????????????????????");
                }
            } catch (Exception e) {
                throw new BusinessException("?????????????????????????????????");
            }
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }
            return ResponseResult.success("????????????");
        }
        return ResponseResult.failure("????????????");
    }

    /**
     * ????????????????????????Null
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static Object nullStringToNull(Object obj, Class<?> clazz) {
        String str = JSONObject.toJSONString(obj);
        Map<String, Object> stringObjectMap = JSONObject.parseObject(str, Map.class);
        Iterator<Map.Entry<String, Object>> it = stringObjectMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (null != entry.getValue() && "".equals(entry.getValue())) {
                it.remove();
            }
        }
        return JSONObject.parseObject(JSONObject.toJSONString(stringObjectMap), clazz);
    }


    /**
     * ???????????????????????????
     *
     * @throws Exception
     **/
    public boolean switchChangedVal(TrailerManagement veh, TrailerManagementVer vehVer, TenantTrailerRel usr, TenantTrailerRelVer usrVer) throws Exception {

        List<String> proVer = getPropertiesVer();
        boolean isVerBool = false;
        for (Iterator<String> it = proVer.iterator(); it.hasNext(); ) {
            String pro = it.next();
            String typeName = pro.substring(0, pro.indexOf("."));
            String proName = pro.substring(pro.indexOf(".") + 1, pro.length());
            if ("vrailerManagement".equals(typeName)) {
                Field proField = null;
                try {
                    proField = veh.getClass().getDeclaredField(proName);
                } catch (Exception e) {
                    proField = veh.getClass().getSuperclass().getDeclaredField(proName);
                }
                Field proFieldVer = null;
                try {
                    proFieldVer = vehVer.getClass().getDeclaredField(proName);
                } catch (Exception e) {
                    proFieldVer = vehVer.getClass().getSuperclass().getDeclaredField(proName);
                }
                proField.setAccessible(true);
                proFieldVer.setAccessible(true);
                Object objVal = proField.get(veh);
                Object objValVer = proFieldVer.get(vehVer);
                if (!((objVal == null && objValVer == null) || (objVal != null && objValVer != null && objVal.equals(objValVer)))) {
                    //????????????????????????????????????????????????
                    isVerBool = true;
                    proFieldVer.set(vehVer, objValVer);
                    proField.set(veh, objVal);
                }
            }
            if ("tenantTrailerRel".equals(typeName)) {
                Field proField = null;
                try {
                    proField = usr.getClass().getDeclaredField(proName);
                } catch (Exception e) {
                    proField = usr.getClass().getSuperclass().getDeclaredField(proName);
                }
                Field proFieldVer = null;
                try {
                    proFieldVer = usrVer.getClass().getDeclaredField(proName);
                } catch (Exception e) {
                    proFieldVer = usrVer.getClass().getSuperclass().getDeclaredField(proName);
                }
                proField.setAccessible(true);
                proFieldVer.setAccessible(true);
                Object objVal = proField.get(usr);
                Object objValVer = proFieldVer.get(usrVer);
                if (!((objVal == null && objValVer == null) || (objVal != null && objValVer != null && objVal.equals(objValVer)))) {
                    //????????????????????????????????????????????????
                    isVerBool = true;
                    proFieldVer.set(usrVer, objValVer);
                    proField.set(usr, objVal);
                }
            }
        }
        return isVerBool;
    }

    private List<String> propertiesVer;

    private List<String> getPropertiesVer() {

        propertiesVer = new ArrayList<String>();
        propertiesVer.add("vrailerManagement.trailerNumber");

        propertiesVer.add("vrailerManagement.trailerMaterial");
        propertiesVer.add("vrailerManagement.trailerLoad");
        propertiesVer.add("vrailerManagement.trailerVolume");
        propertiesVer.add("vrailerManagement.trailerLength");
        propertiesVer.add("vrailerManagement.trailerWide");
        propertiesVer.add("vrailerManagement.trailerHigh");
        propertiesVer.add("vrailerManagement.isState");//???????????????
        //      propertiesVer.add("vrailerManagement.trailerPictureUrl");
        //      propertiesVer.add("vrailerManagement.trailerPictureId");
        //     propertiesVer.add("vrailerManagement.registrationTime");
        propertiesVer.add("vrailerManagement.registrationNumble");
        propertiesVer.add("vrailerManagement.vehicleValidityTime");
        propertiesVer.add("vrailerManagement.operateValidityTime");
        propertiesVer.add("vrailerManagement.sourceProvince");//????????????
        propertiesVer.add("vrailerManagement.sourceRegion");//?????????
        propertiesVer.add("vrailerManagement.sourceCounty");//?????????


//        propertiesVer.add("tenantTrailerRel.trailerOwnerShip");
//        propertiesVer.add("tenantTrailerRel.attachedRootOrgTwoId");//????????????
//        propertiesVer.add("tenantTrailerRel.price");
//        propertiesVer.add("tenantTrailerRel.loanInterest");
//        propertiesVer.add("tenantTrailerRel.interestPeriods");
//        propertiesVer.add("tenantTrailerRel.payInterestPeriods");
//        propertiesVer.add("tenantTrailerRel.purchaseDate");
//        propertiesVer.add("tenantTrailerRel.depreciatedMonth");
//        propertiesVer.add("tenantTrailerRel.insuranceFee");
//        propertiesVer.add("tenantTrailerRel.examVehicleFee");
//        propertiesVer.add("tenantTrailerRel.maintainFee");
//        propertiesVer.add("tenantTrailerRel.repairFee");
//        propertiesVer.add("tenantTrailerRel.tyreFee");
//        propertiesVer.add("tenantTrailerRel.otherFee");
//        propertiesVer.add("tenantTrailerRel.annualVeriTime");
//        propertiesVer.add("tenantTrailerRel.seasonalVeriTime");
//        propertiesVer.add("tenantTrailerRel.insuranceTime");
//        propertiesVer.add("tenantTrailerRel.insuranceCode");
//        propertiesVer.add("tenantTrailerRel.maintainDis");
//        propertiesVer.add("tenantTrailerRel.maintainWarnDis");
//        propertiesVer.add("tenantTrailerRel.prevMaintainTime");
//        propertiesVer.add("tenantTrailerRel.attachedManId");//?????????
//        propertiesVer.add("tenantTrailerRel.residual");//??????


        return propertiesVer;
    }

    @Override
    public Page<OrderInfoVo> queryTrailerOrderList(Page<OrderInfoVo> page, String accessToken, String trailerPlate, String plateNumber,
                                                   String carUserName, String carUserPhone, Long orderId, Integer sourceRegion,
                                                   Integer desRegion, String dependTimeBegin, String dependTimeEnd) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<OrderInfoVo> orderInfoVos = baseMapper.queryTrailerOrderList(page, loginInfo.getTenantId(), trailerPlate, plateNumber, carUserName,
                carUserPhone, orderId, sourceRegion, desRegion, dependTimeBegin, dependTimeEnd);
        if (orderInfoVos.getRecords() != null && orderInfoVos.getRecords().size() > 0) {
            for (OrderInfoVo record : orderInfoVos.getRecords()) {
                try {
                    if (record.getDesRegion() != null) {
                        record.setDesRegionName(iSysStaticDataService.getSysStaticData("SYS_CITY", record.getDesRegion() + "").getCodeName());
                    }
                    if (record.getSourceRegion() != null) {
                        record.setSourceRegionName(iSysStaticDataService.getSysStaticData("SYS_CITY", record.getSourceRegion() + "").getCodeName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return orderInfoVos;
    }

    @Override
    @Async
    public void batchImport(byte[] byteBuffer, ImportOrExportRecords record, String accessToken, String fileName) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        try {
            List<TrailerManagementDto> failureList = new ArrayList<TrailerManagementDto>();
            InputStream is = new ByteArrayInputStream(byteBuffer);
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(fileName, is);

            int maxNum = 300; // ??????????????????
            // ??????????????????
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);

            if (rows == 1) {
                throw new BusinessException("??????????????????0???????????????");
            }
            if (rows > maxNum) {
                throw new BusinessException("?????????????????????" + maxNum + "???????????????????????????[" + rows + "]");
            }
            Map<String, Integer> checkMap = new HashMap<String, Integer>();
            int success = 0;
            int x = 1;
            for (int i = 2; i <= rows; i++) {
                String tmpMsg = "";
                Map param = new HashMap();
                TrailerManagementDto trailerManagementDto = new TrailerManagementDto();
                List<TrailerLineRel> trailerLineRelList = new ArrayList<TrailerLineRel>();
                List<TrailerLineRelVer> trailerLineRelVerList = new ArrayList<TrailerLineRelVer>();
                String updtLineId = "";
                x++;
                String trailerNumber = parse.readExcelByRowAndCell(sheetNo, i, 1);
                if (StringUtils.isEmpty(trailerNumber)) {
                    tmpMsg += "????????????????????????";
                } else {
                    if (!ChkIntfData.chkPlateNumber(trailerNumber)) {
                        tmpMsg += "????????????????????????";
                    } else {
                        TrailerManagement trailer = trailerManagementMapper.getTrailerManagement(trailerNumber, loginInfo.getTenantId());
                        //??????????????????
                        //?????????
                        if (null != trailer && trailer.getIsAutit() == 1) {
                            tmpMsg += "???????????????????????????";
                        } else {
                            if (checkMap.containsKey(trailerNumber)) {
                                Integer c = checkMap.get(trailerNumber);
                                tmpMsg += "??????????????????" + c + "?????????????????????";
                            } else {
                                param.put("trailerNumber", trailerNumber);
                                trailerManagementDto.setTrailerNumber(trailerNumber);
                                checkMap.put(trailerNumber, x);
                            }
                        }
                    }
                }

                String trailerOwnerShipName = parse.readExcelByRowAndCell(sheetNo, i, 2);
                String trailerOwnerShipStr = null;
                if (StringUtils.isEmpty(trailerOwnerShipName)) {
                    tmpMsg += "?????????????????????";
                } else {
                    trailerOwnerShipStr = getCodeValue("OWNER_SHIP", trailerOwnerShipName);
                    if (StringUtils.isEmpty(trailerOwnerShipStr)) {
                        tmpMsg += "?????????????????????";
                    }
                    param.put("trailerOwnerShip", trailerOwnerShipStr);
                    trailerManagementDto.setTrailerOwnerShipName(trailerOwnerShipName);
                }

                String trailerMaterialName = parse.readExcelByRowAndCell(sheetNo, i, 3);
                String trailerMaterialStr = null;
                if (StringUtils.isEmpty(trailerMaterialName)) {
                    tmpMsg += "??????????????????";
                } else {
                    trailerMaterialStr = getCodeValue("TRAILER_MATERIAL", trailerMaterialName);
                    if (StringUtils.isEmpty(trailerMaterialStr)) {
                        tmpMsg += "??????????????????";
                    }
                    param.put("trailerMaterial", trailerMaterialStr);
                    trailerManagementDto.setTrailerMaterialStr(trailerMaterialName);
                }

                String trailerLoad = parse.readExcelByRowAndCell(sheetNo, i, 4);
                if (StringUtils.isEmpty(trailerLoad)) {
                    tmpMsg += "??????????????????";
                } else if (!CommonUtil.isNumber(trailerLoad)
                        || !trailerLoad.matches(percentAge)) {
                    tmpMsg += "??????????????????";
                }
                if (StringUtils.isNotEmpty(trailerLoad)) {
                    param.put("trailerLoad", Double.parseDouble(trailerLoad) * 100);
                    trailerManagementDto.setTrailerLoad(trailerLoad);
                }


                String trailerVolume = parse.readExcelByRowAndCell(sheetNo, i, 5);
                if (StringUtils.isEmpty(trailerVolume)) {
                    tmpMsg += "??????????????????";
                } else if (!CommonUtil.isNumber(trailerVolume)) {
                    tmpMsg += "??????????????????";
                }
                if (StringUtils.isNotEmpty(trailerVolume)) {
                    param.put("trailerVolume", Double.parseDouble(trailerVolume) * 100);
                    trailerManagementDto.setTrailerVolume(trailerVolume);
                }


                String trailerLength = parse.readExcelByRowAndCell(sheetNo, i, 6);
                if (StringUtils.isEmpty(trailerLength)) {
                    tmpMsg += "??????????????????";
                } else if (!CommonUtil.isNumber(trailerLength)
                        || !trailerLength.matches(percentAge)) {
                    tmpMsg += "??????????????????";
                }
                if (StringUtils.isNotEmpty(trailerLength)) {
                    param.put("trailerLength", Double.parseDouble(trailerLength) * 100);
                    trailerManagementDto.setTrailerLength(trailerLength);
                }


                String trailerWide = parse.readExcelByRowAndCell(sheetNo, i, 7);
                if (StringUtils.isEmpty(trailerWide)) {
                    tmpMsg += "??????????????????";
                } else if (!CommonUtil.isNumber(trailerWide)
                        || !trailerWide.matches(percentAge)) {
                    tmpMsg += "??????????????????";
                }
                if (StringUtils.isNotEmpty(trailerWide)) {
                    param.put("trailerWide", Double.parseDouble(trailerWide) * 100);
                    trailerManagementDto.setTrailerWide(trailerWide);
                }
                String trailerHigh = parse.readExcelByRowAndCell(sheetNo, i, 8);
                if (StringUtils.isEmpty(trailerHigh)) {
                    tmpMsg += "??????????????????";
                } else if (!CommonUtil.isNumber(trailerHigh)
                        || !trailerHigh.matches(percentAge)) {
                    tmpMsg += "??????????????????";
                }
                if (StringUtils.isNotEmpty(trailerHigh)) {
                    param.put("trailerHigh", Double.parseDouble(trailerHigh) * 100);
                    trailerManagementDto.setTrailerHigh(trailerHigh);
                }


                String isStateName = parse.readExcelByRowAndCell(sheetNo, i, 9);
                String isStateStr = null;
                if (StringUtils.isNotEmpty(isStateName)) {
                    isStateStr = getCodeValue("TRAILER_TYPE", isStateName);
                    if (StringUtils.isEmpty(isStateStr)) {
                        tmpMsg += "??????????????????";
                    }
                    param.put("isState", isStateStr);
                    trailerManagementDto.setIsStateStr(isStateName);
                }

                //?????????????????????  ????????
                String trailerSourceRegionStr = parse.readExcelByRowAndCell(sheetNo, i, 10);
                long sourceProvince = 0L;
                long sourceRegion = 0L;
                long sourceCounty = 0L;
                if (StringUtils.isNotEmpty(trailerSourceRegionStr)) {
                    String[] trailerSourceRegion = trailerSourceRegionStr.split("??");
                    if (trailerSourceRegion.length != 3) {
                        tmpMsg += "????????????????????????";
                    } else {
                        sourceProvince = Long.parseLong(getSysStaticData("SYS_PROVINCE", trailerSourceRegion[0]).getCodeValue());
                        if (sourceProvince <= 0) {
                            tmpMsg += "????????????????????????";
                        } else {
                            sourceRegion = Long.parseLong(getSysStaticData("SYS_CITY", trailerSourceRegion[1]).getCodeValue());
                            if (sourceRegion <= 0) {
                                tmpMsg += "????????????????????????";
                            } else {
                                sourceCounty = Long.parseLong(getSysStaticData("SYS_DISTRICT", trailerSourceRegion[2]).getCodeValue());
                                if (sourceCounty <= 0) {
                                    tmpMsg += "????????????????????????";
                                }
                            }
                        }
                    }
                    param.put("sourceProvince", sourceProvince);
                    param.put("sourceRegion", sourceRegion);
                    param.put("sourceCounty", sourceCounty);
                    param.put("trailerSourceRegionStr", trailerSourceRegionStr);
                    trailerManagementDto.setTrailerSourceRegionStr(trailerSourceRegionStr);
                }

                String attachedOrgName = parse.readExcelByRowAndCell(sheetNo, i, 11);
                Long attachedOrgId = null;
                if (StringUtils.isNotBlank(attachedOrgName)) {
                    List<SysOrganize> sysOrganizes = iSysOrganizeService.querySysOrganizeTreeTrailer(accessToken, 1, true);
                    List<SysOrganize> collect = sysOrganizes.stream().filter(a -> a.getParentOrgId() != -1 && a.getState() == 1).collect(Collectors.toList());
                    if (collect != null && collect.size() > 0) {
                        attachedOrgId = collect.get(0).getId();
                    }
                } else {
//                    attachedOrgId = getOrgIdByName(attachedOrgName, loginInfo.getTenantId(), 1);
//                    if (attachedOrgId == null) {
//                        tmpMsg += "????????????????????????";
//                    }
                }
                param.put("attachedRootOrgTwoId", attachedOrgId);
                trailerManagementDto.setOrgName(attachedOrgName);
                String attachedUserPhone = parse.readExcelByRowAndCell(sheetNo, i, 12);
                if (StringUtils.isNotEmpty(attachedUserPhone)) {
                    StaffDataInfoVo staffDataInfoIn = new StaffDataInfoVo();
                    staffDataInfoIn.setLoginAcct(attachedUserPhone);
                    LambdaQueryWrapper<SysUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
                    lambdaQueryWrapper.eq(SysUser::getLoginAcct, attachedUserPhone);
                    List<SysUser> info = sysUserService.list(lambdaQueryWrapper);
                    //          List<StaffDataInfoDto> info = userDataInfoRecordMapper.queryStaffInfo(staffDataInfoIn, loginInfo.getTenantId());
                    if (info == null || info.size() == 0 || info.isEmpty()) {
                        tmpMsg += "???????????????????????????";
                    } else {
                        param.put("attachedMan", info.get(0).getName());
                        param.put("attachedManId", info.get(0).getUserInfoId());
                        trailerManagementDto.setAttachedMan(info.get(0).getName());
                    }
                    param.put("attachedUserPhone", attachedUserPhone);
                    trailerManagementDto.setAttachedUserPhone(attachedUserPhone);
                }

                String lineCodeRules = parse.readExcelByRowAndCell(sheetNo, i, 13);
                param.put("lineCodeRules", lineCodeRules);
                trailerManagementDto.setLineCodeRules(lineCodeRules);
                if (StringUtils.isNotEmpty(lineCodeRules)) {
                    String[] lineCodeRuleList = lineCodeRules.split(",");
                    StringBuffer lineCodeRulesSb = new StringBuffer();
                    for (String s : lineCodeRuleList) {
                        lineCodeRulesSb.append("'").append(s).append("',");
                    }
                    List<VehicleLineRelsVo> lines = cmCustomerLineMapper.getCmCustomerLineByLineCodeRulesTrailer(lineCodeRulesSb.substring(0, lineCodeRulesSb.length() - 1));
                    if (lines == null || lines.isEmpty()) {
                        tmpMsg += "????????????????????????";
                    } else {
                        if (lineCodeRuleList.length == lines.size()) {
                            List<Map> lineList = new ArrayList<>();
                            for (int j = 0; j < lines.size(); j++) {

                                VehicleLineRelsVo line = lines.get(j);
                                long lineId = line.getLineId();
                                TrailerLineRel trailerLineRel = new TrailerLineRel();
                                trailerLineRel.setLineId(lineId == -1 ? null : lineId);
                                trailerLineRel.setLineCodeRule(line.getLineCodeRule());
                                trailerLineRel.setOpId(loginInfo.getId());
                                trailerLineRel.setOpDate(LocalDateTime.now());
                                trailerLineRelList.add(trailerLineRel);

                                TrailerLineRelVer trailerLineRelVer = new TrailerLineRelVer();
                                trailerLineRelVer.setRelId(trailerLineRel.getId());
                                trailerLineRelVer.setLineId(lineId == -1 ? null : lineId);
                                trailerLineRelVer.setLineCodeRule(line.getLineCodeRule());
                                trailerLineRelVer.setOpId(loginInfo.getId());
                                trailerLineRelVer.setOpDate(LocalDateTime.now());
                                trailerLineRelVer.setVerState(1);

                                trailerLineRelVerList.add(trailerLineRelVer);

                                if (j == lines.size() - 1) {
                                    updtLineId = updtLineId + lineId;
                                } else {
                                    updtLineId = updtLineId + lineId + ",";
                                }
                            }
                            param.put("trailerLineRelList", trailerLineRelList);
                            param.put("trailerLineRelVerList", trailerLineRelVerList);
                            param.put("updtLineId", updtLineId);
                            param.put("vehicleLineRelsArray", lines);
                        } else {
                            Set tmp = new HashSet();
                            for (VehicleLineRelsVo line : lines) {
                                tmp.add(line.getLineCodeRule());
                            }
                            String lineMsg = "";
                            for (String s1 : lineCodeRuleList) {
                                if (!tmp.contains(s1)) {
                                    lineMsg += s1 + ",";
                                }
                            }
                            if (StringUtils.isNotEmpty(lineMsg)) {
                                tmpMsg += "???????????????" + lineMsg.substring(0, lineMsg.length() - 1) + "????????????";
                            }
                        }
                    }
                }


                String price = parse.readExcelByRowAndCell(sheetNo, i, 14);
                if (StringUtils.isNotEmpty(price)) {
                    if (!CommonUtil.isNumber(price)) {
                        tmpMsg += "??????????????????";
                    } else {
                        param.put("price", Double.parseDouble(price) * 100);
                        trailerManagementDto.setPrice(price);
                    }
                }
                String residual = parse.readExcelByRowAndCell(sheetNo, i, 15);
                if (StringUtils.isNotEmpty(residual)) {
                    if (!CommonUtil.isNumber(residual)) {
                        tmpMsg += "??????????????????";
                    } else {
                        param.put("residual", Double.parseDouble(residual) * 100);
                        trailerManagementDto.setResidual(residual);
                    }
                }
                String loanInterest = parse.readExcelByRowAndCell(sheetNo, i, 16);
                if (StringUtils.isNotEmpty(loanInterest)) {
                    if (!CommonUtil.isNumber(loanInterest)) {
                        tmpMsg += "????????????????????????";
                    } else {
                        param.put("loanInterest", Double.parseDouble(loanInterest) * 100);
                        trailerManagementDto.setLoanInterest(loanInterest);
                    }
                }
                String interestPeriods = parse.readExcelByRowAndCell(sheetNo, i, 17);
                if (StringUtils.isNotEmpty(interestPeriods)) {
                    if (!CommonUtil.isNumber(interestPeriods)) {
                        tmpMsg += "????????????????????????";
                    } else {
                        param.put("interestPeriods", interestPeriods);
                        trailerManagementDto.setInterestPeriods(Integer.valueOf(interestPeriods));
                    }
                }
                String payInterestPeriods = parse.readExcelByRowAndCell(sheetNo, i, 18);
                if (StringUtils.isNotEmpty(payInterestPeriods)) {
                    if (!CommonUtil.isNumber(payInterestPeriods)) {
                        tmpMsg += "????????????????????????";
                    } else {
                        param.put("payInterestPeriods", payInterestPeriods);
                        trailerManagementDto.setPayInterestPeriods(Integer.valueOf(payInterestPeriods));
                    }
                }
                String purchaseDateStr = parse.readExcelByRowAndCell(sheetNo, i, 19);
                if (StringUtils.isNotEmpty(purchaseDateStr)) {
                    param.put("purchaseDate", purchaseDateStr);
                    trailerManagementDto.setPurchaseDate(purchaseDateStr);
                }
                String depreciatedMonth = parse.readExcelByRowAndCell(sheetNo, i, 20);
                if (StringUtils.isNotEmpty(depreciatedMonth)) {
                    if (!CommonUtil.isNumber(depreciatedMonth)) {
                        tmpMsg += "????????????????????????";
                    } else {
                        param.put("depreciatedMonth", depreciatedMonth);
                        trailerManagementDto.setDepreciatedMonth(Integer.valueOf(depreciatedMonth));
                    }
                }
                String insuranceFee = parse.readExcelByRowAndCell(sheetNo, i, 21);
                if (StringUtils.isNotEmpty(insuranceFee)) {
                    if (!CommonUtil.isNumber(insuranceFee)) {
                        tmpMsg += "??????????????????";
                    } else {
                        param.put("insuranceFee", Double.parseDouble(insuranceFee) * 100);
                        trailerManagementDto.setInsuranceFeeStr(insuranceFee);
                    }
                }
                String examVehicleFee = parse.readExcelByRowAndCell(sheetNo, i, 22);
                if (StringUtils.isNotEmpty(examVehicleFee)) {
                    if (!CommonUtil.isNumber(examVehicleFee)) {
                        tmpMsg += "?????????????????????";
                    } else {
                        param.put("examVehicleFee", Double.parseDouble(examVehicleFee) * 100);
                        trailerManagementDto.setExamVehicleFeeStr(examVehicleFee);
                    }
                }
                String maintainFee = parse.readExcelByRowAndCell(sheetNo, i, 23);
                if (StringUtils.isNotEmpty(maintainFee)) {
                    if (!CommonUtil.isNumber(maintainFee)) {
                        tmpMsg += "?????????????????????";
                    } else {
                        param.put("maintainFee", Double.parseDouble(maintainFee) * 100);
                        trailerManagementDto.setMaintainFeeStr(maintainFee);
                    }
                }
                String repairFee = parse.readExcelByRowAndCell(sheetNo, i, 24);
                if (StringUtils.isNotEmpty(repairFee)) {
                    if (!CommonUtil.isNumber(repairFee)) {
                        tmpMsg += "?????????????????????";
                    } else {
                        param.put("repairFee", Double.parseDouble(repairFee) * 100);
                        trailerManagementDto.setRepairFeeStr(repairFee);
                    }
                }
                String tyreFee = parse.readExcelByRowAndCell(sheetNo, i, 25);
                if (StringUtils.isNotEmpty(tyreFee)) {
                    if (!CommonUtil.isNumber(tyreFee)) {
                        tmpMsg += "?????????????????????";
                    } else {
                        param.put("tyreFee", Double.parseDouble(tyreFee) * 100);
                        trailerManagementDto.setTyreFeeStr(tyreFee);
                    }
                }
                String otherFee = parse.readExcelByRowAndCell(sheetNo, i, 26);
                if (StringUtils.isNotEmpty(otherFee)) {
                    if (!CommonUtil.isNumber(otherFee)) {
                        tmpMsg += "??????????????????";
                    } else {
                        param.put("otherFee", Double.parseDouble(otherFee) * 100);
                        trailerManagementDto.setOtherFeeStr(otherFee);
                    }
                }
                String annualVeriTime = parse.readExcelByRowAndCell(sheetNo, i, 27);
                if (StringUtils.isNotEmpty(annualVeriTime)) {
                    param.put("annualVeriTime", annualVeriTime);
                    trailerManagementDto.setAnnualVeriTime(annualVeriTime);
                }
                String annualVeriExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 28);
                if (StringUtils.isNotEmpty(annualVeriExpiredTime)) {
                    param.put("annualVeriExpiredTime", annualVeriExpiredTime);
                    trailerManagementDto.setAnnualVeriExpiredTime(annualVeriExpiredTime);
                }

                String seasonalVeriTime = parse.readExcelByRowAndCell(sheetNo, i, 29);
                if (StringUtils.isNotEmpty(seasonalVeriTime)) {
                    param.put("seasonalVeriTime", seasonalVeriTime);
                    trailerManagementDto.setSeasonalVeriTime(seasonalVeriTime);
                }
                String seasonalVeriExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 30);
                if (StringUtils.isNotEmpty(seasonalVeriExpiredTime)) {
                    param.put("seasonalVeriExpiredTime", seasonalVeriExpiredTime);
                    trailerManagementDto.setSeasonalVeriExpiredTime(seasonalVeriExpiredTime);
                }
                String businessInsuranceTime = parse.readExcelByRowAndCell(sheetNo, i, 31);
                if (StringUtils.isNotEmpty(businessInsuranceTime)) {
                    param.put("businessInsuranceTime", businessInsuranceTime);
                    trailerManagementDto.setBusinessInsuranceTime(businessInsuranceTime);
                }
                String businessInsuranceExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 32);
                if (StringUtils.isNotEmpty(businessInsuranceExpiredTime)) {
                    param.put("businessInsuranceExpiredTime", businessInsuranceExpiredTime);
                    trailerManagementDto.setBusinessInsuranceExpiredTime(businessInsuranceExpiredTime);
                }
                String businessInsuranceCode = parse.readExcelByRowAndCell(sheetNo, i, 33);
                if (StringUtils.isNotEmpty(businessInsuranceCode)) {
                    param.put("businessInsuranceCode", businessInsuranceCode);
                    trailerManagementDto.setBusinessInsuranceCode(businessInsuranceCode);
                }

                String insuranceTime = parse.readExcelByRowAndCell(sheetNo, i, 34);
                if (StringUtils.isNotEmpty(insuranceTime)) {
                    param.put("insuranceTime", insuranceTime);
                    trailerManagementDto.setInsuranceTime(insuranceTime);
                }
                String insuranceExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 35);
                if (StringUtils.isNotEmpty(insuranceExpiredTime)) {
                    param.put("insuranceExpiredTime", insuranceExpiredTime);
                    trailerManagementDto.setInsuranceExpiredTime(insuranceExpiredTime);
                }
                String insuranceCode = parse.readExcelByRowAndCell(sheetNo, i, 36);
                if (StringUtils.isNotEmpty(insuranceCode)) {
                    param.put("insuranceCode", insuranceCode);
                    trailerManagementDto.setInsuranceCode(insuranceCode);
                }

                String otherInsuranceTime = parse.readExcelByRowAndCell(sheetNo, i, 37);
                if (StringUtils.isNotEmpty(otherInsuranceTime)) {
                    param.put("otherInsuranceTime", otherInsuranceTime);
                    trailerManagementDto.setOtherInsuranceTime(otherInsuranceTime);
                }
                String otherInsuranceExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 38);
                if (StringUtils.isNotEmpty(otherInsuranceExpiredTime)) {
                    param.put("otherInsuranceExpiredTime", otherInsuranceExpiredTime);
                    trailerManagementDto.setOtherInsuranceExpiredTime(otherInsuranceExpiredTime);
                }
                String otherInsuranceCode = parse.readExcelByRowAndCell(sheetNo, i, 39);
                if (StringUtils.isNotEmpty(otherInsuranceCode)) {
                    param.put("otherInsuranceCode", otherInsuranceCode);
                    trailerManagementDto.setOtherInsuranceCode(otherInsuranceCode);
                }

                String maintainDis = parse.readExcelByRowAndCell(sheetNo, i, 40);
                if (StringUtils.isNotEmpty(maintainDis)) {
                    param.put("maintainDis", Integer.parseInt(maintainDis) * 100);
                    trailerManagementDto.setMaintainDisStr(maintainDis);
                }
                String maintainWarnDis = parse.readExcelByRowAndCell(sheetNo, i, 41);
                if (StringUtils.isNotEmpty(maintainWarnDis)) {
                    param.put("maintainWarnDis", Integer.parseInt(maintainWarnDis) * 100);
                    trailerManagementDto.setMaintainWarnDisStr(maintainWarnDis);
                }
                String prevMaintainTime = parse.readExcelByRowAndCell(sheetNo, i, 42);
                if (StringUtils.isNotEmpty(prevMaintainTime)) {
                    param.put("prevMaintainTime", prevMaintainTime);
                    trailerManagementDto.setPrevMaintainTime(prevMaintainTime);
                }
                String registrationTime = parse.readExcelByRowAndCell(sheetNo, i, 43);
                if (StringUtils.isNotEmpty(registrationTime)) {
                    param.put("registrationTime", registrationTime);
                    trailerManagementDto.setRegistrationTime(registrationTime);
                }
                String registrationNumble = parse.readExcelByRowAndCell(sheetNo, i, 44);
                if (StringUtils.isNotEmpty(registrationNumble)) {
                    param.put("registrationNumble", registrationNumble);
                    trailerManagementDto.setRegistrationNumble(registrationNumble);
                }
                String vehicleValidityTime = parse.readExcelByRowAndCell(sheetNo, i, 45);
                if (StringUtils.isNotEmpty(vehicleValidityTime)) {
                    param.put("vehicleValidityTime", vehicleValidityTime);
                    trailerManagementDto.setVehicleValidityTime(vehicleValidityTime);
                }
                String vehicleValidityExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 46);
                if (StringUtils.isNotEmpty(vehicleValidityExpiredTime)) {
                    param.put("vehicleValidityExpiredTime", vehicleValidityExpiredTime);
                    trailerManagementDto.setVehicleValidityExpiredTime(vehicleValidityExpiredTime);
                }

                String operateValidityTime = parse.readExcelByRowAndCell(sheetNo, i, 47);
                if (StringUtils.isNotEmpty(operateValidityTime)) {
                    param.put("operateValidityTime", operateValidityTime);
                    trailerManagementDto.setOperateValidityTime(operateValidityTime);
                }

                String operateValidityExpiredTime = parse.readExcelByRowAndCell(sheetNo, i, 48);
                if (StringUtils.isNotEmpty(operateValidityExpiredTime)) {
                    param.put("operateValidityExpiredTime", operateValidityExpiredTime);
                    trailerManagementDto.setOperateValidityExpiredTime(operateValidityExpiredTime);
                }

                trailerManagementDto.setReasonFailure(tmpMsg); // ????????????

                //?????????????????????????????????
                if (org.apache.commons.lang3.StringUtils.isEmpty(tmpMsg)) {
                    TrailerManagementDto trailerManagementDto1 = new TrailerManagementDto();
                    BeanUtil.copyProperties(param, trailerManagementDto1);
                    trailerManagementDto1.setIsEdit(1);
                    ResponseResult integer = null;
                    try {
                        integer = doSaveOrUpdate(trailerManagementDto1, accessToken);
                        //??????????????????
                        if (integer != null) {
                            if (integer.getCode() == 20000) {
                                success++;
                            } else {
                                tmpMsg += "?????????????????????";
                                trailerManagementDto.setReasonFailure(tmpMsg);
                                failureList.add(trailerManagementDto);
                            }
                        }
                    } catch (BusinessException businessException) {
                        businessException.printStackTrace();
                        tmpMsg += businessException.getMessage();
                        trailerManagementDto.setReasonFailure(tmpMsg);
                        failureList.add(trailerManagementDto);
                    }
                } else {
                    //BeanUtil.copyProperties(param, trailerManagementDto);
                    failureList.add(trailerManagementDto);
                }
            }

            if (CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????(??????)", "?????????(??????)", "??????(??????)", "??????(???)(??????)", "??????(?????????)(??????)", "???(???)(??????)", "???(???)(??????)",
                        "???(???)(??????)", "??????(??????)", "????????????(??????)", "????????????", "??????????????????", "????????????", "??????(???)",
                        "??????(??????", "????????????(???/???)", "????????????(???)", "????????????(???)", "????????????", "????????????", "??????(???/???)",
                        "?????????(???/???)", "?????????(???/??????)", "?????????(???/??????)", "?????????(???/??????)", "????????????(???/??????)", "??????????????????", "??????????????????",
                        "??????????????????", "??????????????????", "?????????????????????", "?????????????????????", "???????????????", "?????????????????????", "?????????????????????",
                        "???????????????", "?????????????????????", "?????????????????????", "???????????????", "????????????(??????)", "??????????????????(??????)", "??????????????????",
                        "????????????", "????????????", "?????????????????????", "?????????????????????", "?????????????????????", "?????????????????????", "????????????"};
                resourceFild = new String[]{"getTrailerNumber", "getTrailerOwnerShipName", "getTrailerMaterialStr", "getTrailerLoad", "getTrailerVolume", "getTrailerLength", "getTrailerWide",
                        "getTrailerHigh", "getIsStateStr", "getTrailerSourceRegionStr", "getOrgName", "getAttachedUserPhone", "getLineCodeRules", "getPrice",
                        "getResidual", "getLoanInterest", "getInterestPeriods", "getPayInterestPeriods", "getPurchaseDate", "getDepreciatedMonth", "getInsuranceFeeStr",
                        "getExamVehicleFeeStr", "getMaintainFeeStr", "getRepairFeeStr", "getTyreFeeStr", "getOtherFeeStr", "getAnnualVeriTime", "getAnnualVeriExpiredTime",
                        "getSeasonalVeriTime", "getSeasonalVeriExpiredTime", "getBusinessInsuranceTime", "getBusinessInsuranceExpiredTime", "getBusinessInsuranceCode", "getInsuranceTime", "getInsuranceExpiredTime",
                        "getInsuranceCode", "getOtherInsuranceTime", "getOtherInsuranceExpiredTime", "getOtherInsuranceCode", "getMaintainDisStr", "getMaintainWarnDisStr", "getPrevMaintainTime",
                        "getRegistrationTime", "getRegistrationNumble", "getVehicleValidityTime", "getVehicleValidityExpiredTime", "getOperateValidityTime", "getOperateValidityExpiredTime", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild,
                        TrailerManagementDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "??????????????????.xlsx", inputStream.available());
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                record.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    record.setRemarks(success + "?????????" + failureList.size() + "?????????");
                    record.setState(2);
                }
                if (success == 0) {
                    record.setRemarks(failureList.size() + "?????????");
                    record.setState(4);
                }
            } else {
                record.setRemarks(success + "?????????");
                record.setState(3);
            }
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            try {
                record.setState(4);
                record.setFailureReason("???????????????????????????????????????????????????????????????");
                record.setRemarks("???????????????????????????????????????????????????????????????");
                String[] showName = new String[]{"??????????????????", "??????????????????", "????????????"};
                String[] resourceFild = new String[]{"getId", "getMediaName", "getFailureReason"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(Lists.newArrayList(record), showName, resourceFild, ImportOrExportRecords.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "??????????????????.xlsx", inputStream.available());
                record.setFailureUrl(failureExcelPath);
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                importOrExportRecordsService.saveRecords(record, accessToken);
                e.printStackTrace();
            } catch (Exception e1) {
                record.setState(5);
                record.setFailureReason("???????????????????????????????????????????????????????????????");
                record.setRemarks("???????????????????????????????????????????????????????????????");
                importOrExportRecordsService.saveRecords(record, accessToken);
                e.printStackTrace();

            }
        }
    }

    @Transactional
    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String token) {
        TrailerManagement trailer = getById(busiId);
        QueryWrapper<TenantTrailerRel> relQueryWrapper = new QueryWrapper<>();
        LoginInfo user = loginUtils.get(token);
        relQueryWrapper.eq("trailer_Id", trailer.getId()).
                eq("tenant_id", user.getTenantId()).orderByDesc("id").last(" limit 1");
        TenantTrailerRel trailerRel = tenantTrailerRelMapper.selectOne(relQueryWrapper);

        if (trailer.getTrailerSate() != null && trailer.getTrailerSate() == 0) {//??????????????????????????????????????????????????????
            //???????????????????????????????????????????????????
            tenantTrailerRelMapper.updtRecordAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            //????????????????????????????????????????????????????????????
            tenantTrailerRelMapper.updtTrailerRelAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());
            //??????????????????????????????????????????????????????
            tenantTrailerRelMapper.updtLineRelStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
        } else {//????????????
            TrailerManagementVer manageVer = getTrailerManagementVer(busiId, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());
            TenantTrailerRelVer trailerRefVer = getTenantTrailerRelVer(busiId, user.getTenantId(), SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            List<TrailerLineRelVer> lineRefVerList = getTrailerLineRelVerList(busiId, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            Long id = trailer.getId();
            BeanUtil.copyProperties(manageVer, trailer);
            trailer.setTrailerMaterial(manageVer.getTrailerMaterial());
            trailer.setRegistationTime(manageVer.getRegistrationTime());
            trailer.setIsAutit(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
            trailer.setId(id);
            saveOrUpdate(trailer);
            Long id1 = trailerRel.getId();
            BeanUtil.copyProperties(trailerRefVer, trailerRel);
            trailerRel.setTyreFee(trailerRefVer.getTypeFee());
            trailerRel.setSeasonalVeriTime(trailerRefVer.getSeasonalVerlTime());
            trailerRel.setDepreciatedMonth(trailerRefVer.getDepreaciatedMonth());
            trailerRel.setIsAutit(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
            if (trailerRefVer != null && trailerRefVer.getRelId() != null) {
                trailerRel.setId(trailerRefVer.getRelId());
            }
            trailerRel.setId(id1);
            iTenantTrailerRelService.saveOrUpdate(trailerRel);
            if (lineRefVerList != null && lineRefVerList.size() > 0) {
                //????????????????????????
                trailerLineRelMapper.delLineRelList(trailer.getId());
                //????????????????????????
                for (int i = 0; i < lineRefVerList.size(); i++) {
                    TrailerLineRelVer lineRelVer = lineRefVerList.get(i);
                    TrailerLineRel lineRel = new TrailerLineRel();
                    BeanUtil.copyProperties(lineRelVer, lineRel);
                    lineRel.setState(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
                    iTrailerLineRelService.saveOrUpdate(lineRel);
                }
            } else {
                //????????????????????????
                trailerLineRelMapper.delLineRelList(trailer.getId());
            }
        }

        //?????????????????????????????????????????????????????????
        tenantTrailerRelMapper.updtTrailerVerAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());
        tenantTrailerRelMapper.updtTrailerRelVerAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());

        //??????????????????VER????????????????????????????????????
        tenantTrailerRelMapper.updtLineRelVerStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
    }

    @Transactional
    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String token) {
        LoginInfo user = loginUtils.get(token);
        TrailerManagement trailer = getById(busiId);
        //????????????????????????????????????????????????????????????
        tenantTrailerRelMapper.updtTrailerVerAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());
        tenantTrailerRelMapper.updtTrailerRelVerAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());

        if (trailer.getIsAutit() == 0) {//???????????????????????????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????
            tenantTrailerRelMapper.updtRecordAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            //???????????????????????????????????????????????????????????????
            tenantTrailerRelMapper.updtTrailerRelAuditStatus(trailer.getId(), SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, user.getTenantId());
            //?????????????????????????????????????????????????????????
            //trailerSV.updtLineRelStatus(trailer.getTrailerId(), EXPENSE_STATE.TURN_DOWN, EXPENSE_STATE.CHECK_PENDING);
        }
        LambdaUpdateWrapper<TrailerManagement> lambda = Wrappers.lambdaUpdate();
        lambda.set(TrailerManagement::getIsAutit, 2)
                .eq(TrailerManagement::getId, busiId);
        this.update(lambda);

    }


    private List<TrailerLineRelVer> getTrailerLineRelVerList(Long trailerId, int status) {
        QueryWrapper<TrailerLineRelVer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trailer_Id", trailerId).
                eq("ver_State", status);
        return trailerLineRelVerMapper.selectList(queryWrapper);
    }

    private TenantTrailerRelVer getTenantTrailerRelVer(Long trailerId, long tenantId, int status) {
        QueryWrapper<TenantTrailerRelVer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trailer_Id", trailerId).
                eq("tenant_id", tenantId).
                eq("is_autit", status).
                orderByDesc("id");
        List<TenantTrailerRelVer> list = tenantTrailerRelVerMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private TrailerManagementVer getTrailerManagementVer(Long trailerId, Integer status, Long tenantId) {
        QueryWrapper<TrailerManagementVer> managementVerQueryWrapper = new QueryWrapper<>();
        managementVerQueryWrapper.eq("trailer_Id", trailerId).
                eq("tenant_id", tenantId).
                eq("is_autit", status);
        List<TrailerManagementVer> manageVerList = trailerManagementVerMapper.selectList(managementVerQueryWrapper);
        if (manageVerList != null && manageVerList.size() > 0) {
            return manageVerList.get(0);
        }
        return null;
    }


    private SysStaticData getSysStaticData(String codeType, String codeName) {
        List<SysStaticData> list;
        list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));

        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (codeName.indexOf(sysStaticData.getCodeName()) >= 0) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    private String getCodeValue(String codeType, String codeName) {
        List<com.youming.youche.commons.domain.SysStaticData> sysStaticDatas = (List<com.youming.youche.commons.domain.SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (StrUtil.isEmpty(codeName)) {
            return null;
        }
        String codeValue = null;
        for (com.youming.youche.commons.domain.SysStaticData sysStaticData : sysStaticDatas) {
            if (codeName.equals(sysStaticData.getCodeName())) {
                codeValue = sysStaticData.getCodeValue();
                break;
            }
        }
        return codeValue;
    }

    private Long getOrgIdByName(String orgName, Long tenantId, Integer state) {
        List<SysOrganize> sysOrganizes = iSysOrganizeService.querySysOrganize(tenantId, state);

        if (sysOrganizes == null || sysOrganizes.isEmpty()) {
            return null;
        }
        if (StringUtils.isEmpty(orgName)) {
            return null;
        }
        Long orgId = null;
        for (SysOrganize sysOrganize : sysOrganizes) {
            if (orgName.equals(sysOrganize.getOrgName())) {
                orgId = sysOrganize.getId();
                break;
            }
        }
        return orgId;
    }


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


    @Override
    public TrailerManagement getTrailerManagement(String trailerNumber, Long tenantId) {
        LambdaQueryWrapper<TrailerManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrailerManagement::getTrailerNumber, trailerNumber);
        queryWrapper.eq(TrailerManagement::getTenantId, tenantId);

        List<TrailerManagement> trailerManagements = getBaseMapper().selectList(queryWrapper);
        if (trailerManagements == null || trailerManagements.isEmpty()) {
            return null;
        }
        return trailerManagements.get(0);
    }

    @Override
    public VehicleCertInfoDto getTenantTrialer(String plateNumber, Long tenantId) {
        LambdaQueryWrapper<TrailerManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrailerManagement::getTrailerNumber, plateNumber);
        queryWrapper.eq(TrailerManagement::getTenantId, tenantId);

        List<TrailerManagement> trailerManagements = getBaseMapper().selectList(queryWrapper);
        if (trailerManagements == null || trailerManagements.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<TenantTrailerRel> trailerRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        trailerRelLambdaQueryWrapper.eq(TenantTrailerRel::getTrailerNumber, plateNumber);
        trailerRelLambdaQueryWrapper.eq(TenantTrailerRel::getTenantId, tenantId);
        List<TenantTrailerRel> tenantTrailerRels = tenantTrailerRelMapper.selectList(trailerRelLambdaQueryWrapper);
        if (tenantTrailerRels == null || tenantTrailerRels.isEmpty()) {
            return null;
        }

        TrailerManagement vehicleDataInfo = trailerManagements.get(0);
        TenantTrailerRel tenantVehicleCertRel = tenantTrailerRels.get(0);

        VehicleCertInfoDto out = new VehicleCertInfoDto();
        try {
//            BeanUtils.copyProperties(out, tenantVehicleCertRel);
            BeanUtil.copyProperties(tenantVehicleCertRel, out);
            BeanUtil.copyProperties(vehicleDataInfo, out);
//            BeanUtils.copyProperties(out, vehicleDataInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ZoneId zoneId = ZoneId.systemDefault();

        out.setOperateValidityTime(Date.from(vehicleDataInfo.getOperateValidityExpiredTime().atStartOfDay(zoneId).toInstant()));
        out.setVehicleValidityTime(Date.from(vehicleDataInfo.getVehicleValidityExpiredTime().atStartOfDay(zoneId).toInstant()));
        out.setBusiInsuranceTimeEnd(Date.from(tenantVehicleCertRel.getBusinessInsuranceExpiredTime().atStartOfDay(zoneId).toInstant()));
        out.setOtherInsuranceTimeEnd(Date.from(tenantVehicleCertRel.getOtherInsuranceExpiredTime().atStartOfDay(zoneId).toInstant()));
        out.setInsuranceTimeEnd(Date.from(tenantVehicleCertRel.getInsuranceExpiredTime().atStartOfDay(zoneId).toInstant()));

        return out;
    }

    @Override
    public List<TrailerManagement> getNotUsedTrailer(Long tenantId, String trailerNumber) {
        return trailerManagementMapper.getTrailerQuery(trailerNumber, tenantId);
    }

    @Override
    public Page<TrailerManagement> getLocalNotUsedTrailerPage(Integer sourceRegion, String trailerNumber, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        LambdaQueryWrapper<TrailerManagement> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(TrailerManagement::getTenantId, loginInfo.getTenantId());
//        queryWrapper.like(TrailerManagement::getTrailerNumber, "%" + trailerNumber + "%");

//        Page<TrailerManagement> page = this.page(new Page<TrailerManagement>(pageNum, pageSize), queryWrapper);
        Page<TrailerManagement> page = trailerManagementMapper.getLocalNotUsedTrailerPage(new Page<>(pageNum, pageSize), trailerNumber, loginInfo.getTenantId());
        if (page != null && page.getTotal() > 0) {

            List<TrailerManagement> records = page.getRecords();
            for (TrailerManagement record : records) {
                record.setSourceProvinceName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_PROVINCE", String.valueOf(record.getSourceProvince())));
                record.setSourceCountyName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_DISTRICT", String.valueOf(record.getSourceCounty())));
                record.setSourceRegionName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "SYS_CITY", String.valueOf(record.getSourceRegion())));
                record.setStateName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "TRAILER_TYPE", String.valueOf(record.getIsState())));
            }

            page.setRecords(records);
        }

        return page;
    }

    @Override
    public List<WorkbenchDto> getTableTrailerCount() {
        LocalDateTime localDateTime = LocalDate.now().plusMonths(1).atStartOfDay();
        return tenantTrailerRelMapper.getTableTrailerCount(localDateTime);
    }

    @Override
    public List<WorkbenchDto> getTableTrailerCarCount() {
        return trailerManagementMapper.getTableTrailerCarCount();
    }


    @Override
    public TrailerManagement getTrailerManagement(Long trailerId) {
        return this.getById(trailerId);
    }

    @Override
    public String updateLoaction(long trailerId, String lat, String lng, int provinceId, int regionId, int countyId) {
        TrailerManagement tr = getTrailerManagement(trailerId);
        if (tr != null && tr.getId() != null && tr.getId() > 0) {
            tr.setEand(lat);
            tr.setNand(lng);
            tr.setSourceProvince(provinceId);
            tr.setSourceRegion(regionId);
            tr.setSourceCounty(countyId);
            this.update(tr);
            return "Y";
        }
        return "N";
    }

}
