package com.youming.youche.record.provider.service.impl.cm;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.record.api.cm.ICmCustomerInfoVerService;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.record.domain.cm.CmCustomerInfoVer;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.dto.BackUserDto;
import com.youming.youche.record.provider.mapper.cm.CmCustomerInfoMapper;
import com.youming.youche.record.provider.mapper.cm.CmCustomerInfoVerMapper;
import com.youming.youche.record.vo.cm.CmCustomerInfoVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * ???????????????/??????????????? ???????????????
 * </p>
 *
 * @author ?????????
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class CmCustomerInfoServiceImpl extends BaseServiceImpl<CmCustomerInfoMapper, CmCustomerInfo>
        implements ICmCustomerInfoService {

    @Resource
    private CmCustomerInfoMapper customerInfoMapper;

    @Resource
    private CmCustomerInfoVerMapper customerInfoVerMapper;

    @Resource
    private ICmCustomerInfoVerService cmCustomerInfoVerService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @Resource
    ICmCustomerLineService iCmCustomerLineService;

    @DubboReference(version = "1.0.0")
    ISysRoleService iSysRoleService;


    private static Log log = LogFactory.getLog(CmCustomerInfoServiceImpl.class);


    //@DubboReference(version = "1.0.0")
    //private ISysOperLogService logService;
    //@Autowired
    //private ISysStaticDataMarketService staticDataService;
    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    /**
     * ????????????
     *
     * @return
     */
    @Override
    public Page<CmCustomerInfo> findCustomerList(Page<CmCustomerInfo> page, CmCustomerInfoVo cmCustomerInfo, String accessToken) throws Exception {
        LoginInfo user = loginUtils.get(accessToken);

        List busiIds = new ArrayList();
        //??????????????????
        Page<CmCustomerInfo> cmCustomerInfoPage = baseMapper.selectAllCustomer(page, cmCustomerInfo, user);

        for (CmCustomerInfo cm : cmCustomerInfoPage.getRecords()) {
            if (cm.getOrgId() != null) {
                //TODO ?????????????????? ???????????????????????????????????????????????????  ???????????????????????????????????????????????????  ?????????????????? (???????????????????????????????????????)
                cm.setSaleDaparmentName(sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), cm.getOrgId()));
            }
            busiIds.add(cm.getId());
        }
        Map<Long, Boolean> hasPermissionMap = null;
        if (!busiIds.isEmpty()) {
            hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_CUST, busiIds, accessToken);
            for (CmCustomerInfo cm : cmCustomerInfoPage.getRecords()
            ) {
                Boolean flg = hasPermissionMap.get(cm.getId());
                cm.setIsAuth(flg ? 1 : 0);
            }
        }
        return cmCustomerInfoPage;
    }

    /**
     * ????????????
     *
     * @return
     */
    @Async
    @Override
    public void exportCustomer(CmCustomerInfoVo cmCustomerInfo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo user = loginUtils.get(accessToken);
        ;
        List<CmCustomerInfo> list = baseMapper.exportCustomer(cmCustomerInfo, user);
        try {
            if (list != null && list.size() > 0) {
                //??????????????????
                for (CmCustomerInfo cm : list
                ) {
                    if (cm.getOrgId() != null) {
                        cm.setSaleDaparmentName(sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), cm.getOrgId()));
                    }
                }
                String[] showName = null;
                String[] resourceFild = null;
                String fileName = null;
                showName = new String[]{"????????????", "????????????", "????????????", "????????????", "?????????", "???????????????0????????????1?????????", "???????????????1:????????????2:????????????"};
                resourceFild = new String[]{"getCompanyName", "getCustomerName", "getSaleDaparmentName", "getAddress", "getState", "getAuthState"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, CmCustomerInfoVo.class,
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
            } else {
                importOrExportRecords.setState(4);
                importOrExportRecords.setFailureReason("???????????????????????????!");
                importOrExportRecordsService.update(importOrExportRecords);
            }
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public List findCustomerById(Long customerId, String accessToken) {
        List list = new ArrayList();
        LoginInfo user = loginUtils.get(accessToken);

        CmCustomerInfo cmCustomerInfo = customerInfoMapper.selectCustomerById(customerId, user.getTenantId());
        CmCustomerInfoVer cmCustomerInfoVer = customerInfoVerMapper.selectCustomerVerById(customerId, user.getTenantId());
        list.add(cmCustomerInfo);
        list.add(cmCustomerInfoVer);
        if (cmCustomerInfo != null) {
            Integer oddWay = cmCustomerInfo.getOddWay();
            Integer state = cmCustomerInfo.getState();
            Integer payWay = cmCustomerInfo.getPayWay();
            Long orgIdVer = cmCustomerInfo.getOrgId();
            Long reciveProvinceId = cmCustomerInfo.getReciveProvinceId();
            Long reciveCityId = cmCustomerInfo.getReciveCityId();
            if (oddWay > -1) {
                List<SysStaticData> sysStaticData = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("RECIVE_TYPE"));
                //outVerMap.put("oddWayName",oddWayName);
            }
            if (payWay > -1) {
                //String payWayName = SysStaticDataUtil.getSysStaticDataCodeName("BALANCE_TYPE",payWay+"");
                //outVerMap.put("payWayName",payWayName);
            }
            if (state > -1) {
                //String stateName = SysStaticDataUtil.getSysStaticDataCodeName("SYS_STATE_DESC",state+"");
                //outVerMap.put("stateName",stateName);
            }
            if (reciveProvinceId > -1) {
                //outVerMap.put("reciveProvinceName", CommonUtil.getProvinceNameById(reciveProvinceId));
            }
            if (reciveCityId > -1) {
                //outVerMap.put("reciveCityName",CommonUtil.getCityNameById(reciveCityId));
            }

            if (orgIdVer != null && orgIdVer > -1) {
                //outVerMap.put("orgName", OragnizeCacheUtil.getOrgNameByOrgId(orgIdVer));
            }
        }
        //redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        return list;
    }


    /**
     * ??????ID ??????????????????
     */
    @Override
    public List getCustomerInfo(Long customerId, int isEdit, String accessToken) {
        List list = new ArrayList();
        CmCustomerInfo cmCustomerInfo;
        LoginInfo user = loginUtils.get(accessToken);

        //?????? isEdit 0????????? 1????????? 2?????????3?????????
        if (isEdit == 2 || isEdit == 3) {
            QueryWrapper<CmCustomerInfoVer> cmCustomerInfoVerQueryWrapper = new QueryWrapper<>();
            cmCustomerInfoVerQueryWrapper.eq("customer_id", customerId);
            cmCustomerInfoVerQueryWrapper.orderByDesc("id");
            List<CmCustomerInfoVer> ver = cmCustomerInfoVerService.list(cmCustomerInfoVerQueryWrapper);

            //??????????????????????????????????????????
            CmCustomerInfoVer cust = ver.get(0);
            if (cust.getOrgId() == null || cust.getOrgId() <= 0) {
                throw new BusinessException("???????????????????????????????????????");
            }
            cmCustomerInfo = super.getById(customerId);
            CmCustomerInfoVer outVer = new CmCustomerInfoVer();
            BeanUtil.copyProperties(cust, outVer);
            // Map outVerMap = BeanUtils.convertBean2Map(ver);
            int oddWay = outVer.getOddWay();
            int state = outVer.getState();
            int payWay = outVer.getPayWay();
            long orgIdVer = outVer.getOrgId();

            long reciveProvinceId = outVer.getReciveProvinceId();
            long reciveCityId = outVer.getReciveCityId();
            SysStaticData sysStaticData = new SysStaticData();
            if (oddWay > -1) {
                sysStaticData = getSysStaticDataId("RECIVE_TYPE", oddWay + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outVer.setOddWayName(sysStaticData.getCodeName());
                }
            }
            if (payWay > -1) {
                sysStaticData = getSysStaticDataId("BALANCE_TYPE", payWay + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outVer.setPayWayName(sysStaticData.getCodeName());
                }
            }
            if (state > -1) {
//                sysStaticData = getSysStaticDataId("SYS_STATE_DESC", state + "");
                if (state == 1) {
                    outVer.setStateName("??????");
                } else {
                    outVer.setStateName("??????");
                }
//                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
//                    outVer.setStateName(sysStaticData.getCodeName());
//                }
            }
            if (reciveProvinceId > -1) {
                sysStaticData = getSysStaticDataId("SYS_PROVINCE", reciveProvinceId + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outVer.setReciveProvinceName(sysStaticData.getCodeName());
                }
            }
            if (reciveCityId > -1) {
                sysStaticData = getSysStaticDataId("SYS_CITY", reciveCityId + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outVer.setReciveCityName(sysStaticData.getCodeName());
                }
            }

            if (orgIdVer > -1) {
                String orgName = sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), orgIdVer);
                if (StrUtil.isNotEmpty(orgName)) {
                    outVer.setOrgName(orgName);
                }
            }

            CmCustomerInfo outCmCustomer = new CmCustomerInfo();
            long orgId = -1;
            BeanUtil.copyProperties(cmCustomerInfo, outCmCustomer);
            int oddWayInfo = outCmCustomer.getOddWay();
            int stateInfo = outCmCustomer.getState();
            int payWayInfo = outCmCustomer.getPayWay();
            if (outCmCustomer.getOrgId() != null) {
                orgId = outCmCustomer.getOrgId();
            }

            if (oddWayInfo > -1) {
                sysStaticData = getSysStaticDataId("RECIVE_TYPE", oddWayInfo + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outCmCustomer.setOddWayName(sysStaticData.getCodeName());
                }
            }
            if (payWayInfo > -1) {
                sysStaticData = getSysStaticDataId("BALANCE_TYPE", payWayInfo + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outCmCustomer.setPayWayName(sysStaticData.getCodeName());
                }
            }
            if (stateInfo > -1) {
                if (stateInfo == 1) {
                    outCmCustomer.setStateName("??????");
                } else {
                    outCmCustomer.setStateName("??????");
                }
//                sysStaticData = getSysStaticDataId("SYS_STATE_DESC", stateInfo + "");
//                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
//                    outCmCustomer.setStateName(sysStaticData.getCodeName());
//                }
            }
            long reciveProvinceIdInfo = outCmCustomer.getReciveProvinceId();
            long reciveCityIdInfo = outCmCustomer.getReciveCityId();
            if (reciveProvinceIdInfo > -1) {
                sysStaticData = getSysStaticDataId("SYS_PROVINCE", reciveProvinceIdInfo + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outCmCustomer.setReciveProvinceName(sysStaticData.getCodeName());
                }
            }
            if (reciveCityIdInfo > -1) {
                sysStaticData = getSysStaticDataId("SYS_CITY", reciveCityIdInfo + "");
                if (StrUtil.isNotEmpty(sysStaticData.getCodeName())) {
                    outCmCustomer.setReciveCityName(sysStaticData.getCodeName());
                }
            }

            if (orgId > -1) {
                String orgName = sysOrganizeService.getCurrentTenantOrgNameById(user.getTenantId(), orgId);
                if (StrUtil.isNotEmpty(orgName)) {
                    outCmCustomer.setOrgName(orgName);
                }
            }

            list.add(outCmCustomer);
            list.add(outVer);
            return list;
        } else {
            cmCustomerInfo = super.getById(customerId);
        }
        CmCustomerInfoVo out = new CmCustomerInfoVo();
        if (cmCustomerInfo != null) {
            BeanUtil.copyProperties(cmCustomerInfo, out);
            list.add(out);
        }
        return list;
    }

    /**
     * @return java.lang.String
     * @author ?????????
     * @Description //TODO ???????????????
     * @date 16:28 2022/1/5 0005
     * @Param [cmCustomerInfo]
     */
    @Transactional
    @Override
    public String saveOrUpdateCustomer(CmCustomerInfoVo cmCustomerInfo, String accessToken) throws Exception {
        LoginInfo user = loginUtils.get(accessToken);
        ;
        Long orgId = cmCustomerInfo.getOrgId();
        String falg = "Y";
        LocalDateTime localDateTime = LocalDateTime.now();
        CmCustomerInfoVer cmCustomerInfoVer = new CmCustomerInfoVer();
        CmCustomerInfo oldCustomerInfo = null;
        cmCustomerInfo.setTenantId(user.getTenantId());
		/*List<CmCustomerInfo> lists = customerInfoMapper.doQueryCustomerByCompanyName(cmCustomerInfo);
		if(lists != null && lists.size() > 0){
			throw new BusinessException("????????????["+cmCustomerInfo.getCompanyName()+"]?????????????????????????????????????????????????????????");
		}*/
        if (cmCustomerInfo.getId() != null && cmCustomerInfo.getId() > 0) {
            falg = "YM";
            oldCustomerInfo = customerInfoMapper.selectCustomerById(cmCustomerInfo.getId(), user.getTenantId());
            if (oldCustomerInfo == null) {
                throw new BusinessException("????????????[" + cmCustomerInfo.getId() + "]????????????????????????????????????");
            }
            oldCustomerInfo.setAuthState(EnumConsts.CUSTOMER_AUTH_STATE.STATE1);
            oldCustomerInfo.setAuthState(1);
            //session.evict(oldCustomerInfo);
            //?????????????????????????????????
            cmCustomerInfo.setCreateTime(oldCustomerInfo.getCreateTime());
            cmCustomerInfo.setTenantId(oldCustomerInfo.getTenantId());
            cmCustomerInfo.setOrgId(oldCustomerInfo.getOrgId());
            BeanUtil.copyProperties(oldCustomerInfo, cmCustomerInfoVer);
        } else {
            cmCustomerInfo.setAuthState(EnumConsts.CUSTOMER_AUTH_STATE.STATE1);
            cmCustomerInfo.setCreateTime(localDateTime);
            cmCustomerInfo.setTenantId(user.getTenantId());
            //cmCustomerInfo.setOrgId(user.getOrgId());
            //??????????????????
            Long tenantCustomerMaxId = customerInfoMapper.queryMaxCode(user.getTenantId());
            if (tenantCustomerMaxId == null) {
                tenantCustomerMaxId = 1L;
            }
            DecimalFormat decimalFormat = new DecimalFormat("00000000");
            String retValue = "C" + decimalFormat.format(new BigDecimal(tenantCustomerMaxId + 1));
            cmCustomerInfo.setCustomerCode(retValue);
            cmCustomerInfo.setState(EnumConsts.STATE.STATE_YES + "");
        }
        cmCustomerInfo.setUpdateTime(localDateTime);
        cmCustomerInfo.setOpId(user.getId());
        Long customerId = -1L;
        //???????????????????????????????????????????????????????????????Ver??????
        if ("YM".equals(falg) && switchChangedVal(cmCustomerInfo, oldCustomerInfo)) {
            customerInfoMapper.updateCustomerInfo(oldCustomerInfo, user.getTenantId());
            customerInfoVerMapper.updateCustomerInfoVer(cmCustomerInfo, user.getTenantId());
            //logSV.saveSysOperLog(SysOperLogConst.BusiCode.Customer, oldCustomerInfo.getCustomerId(), SysOperLogConst.OperType.Update, "??????????????????");
            customerId = oldCustomerInfo.getId();
        } else {
            Integer customer = customerInfoMapper.checkCustomer(cmCustomerInfo);
            if (customer != null) {
                throw new BusinessException("????????????[" + cmCustomerInfo.getCompanyName() + "]??????????????????");
            }
            cmCustomerInfo.setIsAuth(1);
            Long lastCustomerId = customerInfoMapper.insertCustomerInfo(cmCustomerInfo);
            customerInfoVerMapper.insertCustomerInfoVer(cmCustomerInfo);//??????????????????
            //SysOperLog sysOperLog = new SysOperLog();
            //logService.saveSysOperLog(sysOperLog,accessToken);
            //logSV.saveSysOperLog(SysOperLogConst.BusiCode.Customer, cmCustomerInfo.getCustomerId(), SysOperLogConst.OperType.Add, "??????????????????");
            customerId = cmCustomerInfo.getId();
        }
        //??????????????????
			/*Map inMap = new HashMap();
			inMap.put("svName","cmCustomerInfoTF");
			IAuditOutTF auditOutTF = (IAuditOutTF)SysContexts.getBean("auditOutTF");
			boolean bool = auditOutTF.startProcess(AUDIT_CODE_CUST,customerId,SysOperLogConst.BusiCode.Customer,inMap);
			if(!bool){
				throw new BusinessException("???????????????????????????");
			}*/
        //}
        return falg;
    }

    @Transactional
    @Override
    public String saveOrUpdateCustomerCopy(CmCustomerInfoVo cmCustomerInfoVo, String accessToken) throws BusinessException {
        LoginInfo user = loginUtils.get(accessToken);
        ;
        CmCustomerInfo cmCustomerInfo = new CmCustomerInfo();
        BeanUtil.copyProperties(cmCustomerInfoVo, cmCustomerInfo);
        String falg = "Y";
        CmCustomerInfoVer cmCustomerInfoVer = new CmCustomerInfoVer();
        CmCustomerInfo oldCustomerInfo = null;
        QueryWrapper<CmCustomerInfo> cmCustomerInfoQueryWrapper = new QueryWrapper<>();
        cmCustomerInfoQueryWrapper.eq("tenant_id", user.getTenantId());
        cmCustomerInfoQueryWrapper.eq("state", SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        cmCustomerInfoQueryWrapper.eq("type", EnumConsts.CUSTOMER_TYPE.YES);
        cmCustomerInfoQueryWrapper.eq("company_name", cmCustomerInfo.getCompanyName());
        if (cmCustomerInfo.getId() != null && cmCustomerInfo.getId() > 0) {
            cmCustomerInfoQueryWrapper.ne("id", cmCustomerInfo.getId());
        }
        List<CmCustomerInfo> lists = super.list(cmCustomerInfoQueryWrapper);
        if (lists != null && lists.size() > 0) {
            throw new BusinessException("????????????[" + cmCustomerInfo.getCompanyName() + "]?????????????????????????????????????????????????????????");
        }
        if (cmCustomerInfo.getId() != null && cmCustomerInfo.getId() > 0) {
            falg = "YM";
            oldCustomerInfo = super.getById(cmCustomerInfo.getId());
            if (oldCustomerInfo == null) {
                throw new BusinessException("????????????[" + cmCustomerInfo.getId() + "]????????????????????????????????????");
            }
            oldCustomerInfo.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            //  cmCustomerInfo.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            cmCustomerInfo.setTenantId(oldCustomerInfo.getTenantId());
            //?????????????????????????????????
            //cmCustomerInfo.setOrgId(oldCustomerInfo.getOrgId());
            cmCustomerInfoVer.setAuthState(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            BeanUtil.copyProperties(cmCustomerInfoVo, cmCustomerInfoVer);
        } else {
            cmCustomerInfo.setAuthState(EnumConsts.CUSTOMER_AUTH_STATE.STATE1);
            cmCustomerInfo.setTenantId(user.getTenantId());
            //??????????????????
            //??????????????????
            Long tenantCustomerMaxId = customerInfoMapper.queryMaxCode(user.getTenantId());
            if (tenantCustomerMaxId == null) {
                tenantCustomerMaxId = 1L;
            }
            DecimalFormat decimalFormat = new DecimalFormat("00000000");
            String retValue = "C" + decimalFormat.format(new BigDecimal(tenantCustomerMaxId + 1));
            cmCustomerInfo.setCustomerCode(retValue);
            cmCustomerInfo.setState(EnumConsts.STATE.STATE_YES);
        }
        cmCustomerInfo.setOpId(user.getId());
        Long customerId = -1L;
        //???????????????????????????????????????????????????????????????Ver??????
        if ("YM".equals(falg)) {
            super.saveOrUpdate(oldCustomerInfo);
            cmCustomerInfoVer.setCreateTime(LocalDateTime.now());
            if (cmCustomerInfoVer.getCustomerId() == null) {
                cmCustomerInfoVer.setCustomerId(oldCustomerInfo.getId());
            }
            cmCustomerInfoVer.setId(null);
            cmCustomerInfoVerService.saveOrUpdate(cmCustomerInfoVer);
            saveSysOperLog(SysOperLogConst.BusiCode.Customer, SysOperLogConst.OperType.Update, "??????????????????", accessToken, oldCustomerInfo.getId());
            customerId = oldCustomerInfo.getId();
        } else {
            if (cmCustomerInfo.getOrgId() == null) {
                List<Long> orgs = new ArrayList<>();
                orgs = user.getOrgIds();
                if (orgs.size() > 0) {
                    cmCustomerInfo.setOrgId(orgs.get(0));
                } else {
                    throw new BusinessException("????????????????????????????????????");
                }
            }
            cmCustomerInfo.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
            cmCustomerInfo.setCreateTime(LocalDateTime.now());
            super.saveOrUpdate(cmCustomerInfo);
            BeanUtil.copyProperties(cmCustomerInfo, cmCustomerInfoVer);
            cmCustomerInfoVer.setCustomerId(cmCustomerInfo.getId());
            cmCustomerInfoVerService.saveOrUpdate(cmCustomerInfoVer);
            saveSysOperLog(SysOperLogConst.BusiCode.Customer, SysOperLogConst.OperType.Add, "??????????????????", accessToken, cmCustomerInfo.getId());
            customerId = cmCustomerInfo.getId();
        }
        //??????????????????
        //????????????
        Map inMap = new HashMap();
        inMap.put("svName", "ICmCustomerInfoService");
        boolean bool = false;
        try {
            bool = auditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_CUST, customerId, SysOperLogConst.BusiCode.Customer, inMap, accessToken);
            if (!bool) {
                log.error("???????????????????????????");
                throw new BusinessException("???????????????????????????");
            }
        } catch (Exception e) {
            falg = "F";
            e.printStackTrace();
        }
        return falg;
    }

    /**
     * ??????????????????
     *
     * @throws Exception
     * @author fenghy
     **/
    @Override
    public boolean switchChangedVal(Object info, Object ver) {
        boolean isVerBool = false;
        try {
            if (info == null || ver == null) {
                return isVerBool;
            }
            BeanUtilsBean instance = BeanUtilsBean.getInstance();
            PropertyDescriptor[] origDescriptors =
                    instance.getPropertyUtils().getPropertyDescriptors(info);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (instance.getPropertyUtils().isReadable(ver, name) &&
                        instance.getPropertyUtils().isWriteable(ver, name)) {
                    try {
                        Object objVal =
                                instance.getPropertyUtils().getSimpleProperty(info, name);
                        //?????????
                        Object objValVer =
                                instance.getPropertyUtils().getSimpleProperty(ver, name);

                        if (!((objVal == null && objValVer == null) || (objVal != null && objValVer != null && objVal.equals(objValVer)))) {
                            //????????????????????????????????????????????????
                            isVerBool = true;
                            instance.copyProperty(ver, name, objVal);
                            instance.copyProperty(info, name, objValVer);
                        }

                    } catch (NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isVerBool;
    }


    @Async
    @Transactional
    @Override
    public void batchImportCustomer(byte[] byteBuffer, ImportOrExportRecords records, String token) {
        List<CmCustomerInfoVo> failureList = new ArrayList<>();
        InputStream inputStream = new ByteArrayInputStream(byteBuffer);
        List<List<String>> lists = new ArrayList<>();
        LoginInfo loginInfo = loginUtils.get(token);
        try {
            lists = getExcelContent(inputStream, 1, (ExcelFilesVaildate[]) null);
            //???????????????
            List<String> listsRows = lists.remove(0);
            int maxNum = 300;
            if (lists.size() == 0) {
                throw new BusinessException("??????????????????0???????????????");
            }
            if (lists.size() > maxNum) {
                throw new BusinessException("?????????????????????" + maxNum + "???????????????????????????[" + lists.size() + "]");
            }
            int j = 1;
            //List<Map> paramList = new ArrayList<>();
            //Map<String, Integer> checkMap = new HashMap<String, Integer>();
            int success = 0;
            for (List<String> l : lists) {
                StringBuffer reasonFailure = new StringBuffer();
                CmCustomerInfoVo cmCustomerInfoVo = new CmCustomerInfoVo();
                // Map param = new HashMap();
                // param.put("type", EnumConsts.CUSTOMER_TYPE.YES);
                cmCustomerInfoVo.setType(EnumConsts.CUSTOMER_TYPE.YES);
                j++;
                String tmpMsg = "";
                if (l.size() != 15 && l.size() != 14) {
                    reasonFailure.append("???[" + j + "]???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                    continue;
                }
                String companyName = l.get(0);
                if (StrUtil.isEmpty(companyName)) {
                    reasonFailure.append("?????????????????????!");
                } else {
                    cmCustomerInfoVo.setCompanyName(companyName);
                    QueryWrapper<CmCustomerInfo> cmCustomerInfoQueryWrapper = new QueryWrapper<>();
                    cmCustomerInfoQueryWrapper.eq("company_name", companyName);
                    cmCustomerInfoQueryWrapper.eq("tenant_id",loginInfo.getTenantId());
                    List<CmCustomerInfo> oldCusts = super.list(cmCustomerInfoQueryWrapper);
                    if (oldCusts != null && oldCusts.size() > 0) {
                        reasonFailure.append("?????????????????????!");
                    }
                }

                String customerName = l.get(1);
                if (StrUtil.isEmpty(companyName)) {
                    reasonFailure.append("?????????????????????!");
                } else {
                    cmCustomerInfoVo.setCustomerName(customerName);
                }
                String lookupName = l.get(2);
                if (StrUtil.isEmpty(lookupName)) {
                    reasonFailure.append("?????????????????????!");
                } else {
                    cmCustomerInfoVo.setLookupName(lookupName);
                    //param.put("lookupName", lookupName);
                }

                String oddWayName = l.get(3);
                if (StrUtil.isEmpty(oddWayName)) {
                    reasonFailure.append("?????????????????????!)");
                } else {
                    //???????????????
                    SysStaticData oddWay = getSysStaticData("RECIVE_TYPE", oddWayName);
                    if (StrUtil.isEmpty(oddWay.getCodeValue())) {
                        reasonFailure.append("?????????????????????!");
                        cmCustomerInfoVo.setOddWayName(oddWayName);
                    } else {
                        cmCustomerInfoVo.setOddWay(Integer.valueOf(oddWay.getCodeValue()));
                        //param.put("oddWay", oddWay.getCodeValue());
                    }
                }

                String address = l.get(4);
                // param.put("address", address);
                cmCustomerInfoVo.setAddress(address);
                //??????????????????
                String reciveCity = l.get(5);
                if (org.apache.commons.lang.StringUtils.isEmpty(reciveCity)) {
                    reasonFailure.append("?????????????????????!");
                } else {
                    Map<String, String> reciveCityMap = CommonUtil.addressResolution(reciveCity);
                    String provinceStr = reciveCityMap.get("province");
                    String cityStr = reciveCityMap.get("city");
                    if(StrUtil.isEmpty(provinceStr) && StrUtil.isEmpty(cityStr)){
                        reasonFailure.append("?????????????????????!");
                        cmCustomerInfoVo.setReciveProvinceName(reciveCity);
                    }else {
                        SysStaticData reciveProvinceId = getSysStaticData("SYS_PROVINCE", provinceStr);
                        if (StrUtil.isEmpty(reciveProvinceId.getCodeValue())) {
                            reasonFailure.append("?????????????????????!");
                            cmCustomerInfoVo.setReciveProvinceName(reciveCity);
                        } else {
                            SysStaticData reciveCityId = getSysStaticData("SYS_CITY", cityStr);
                            if (StrUtil.isEmpty(reciveCityId.getCodeValue())) {
                                reasonFailure.append("?????????????????????!");
                            }
                            cmCustomerInfoVo.setReciveProvinceId(Long.valueOf(reciveProvinceId.getCodeValue()));
                            cmCustomerInfoVo.setReciveCityId(Long.valueOf(reciveCityId.getCodeValue()));
                            cmCustomerInfoVo.setReciveProvinceName(reciveCity);
                            //param.put("reciveProvinceId", reciveProvinceId.getCodeValue());
                            //param.put("reciveCityId", reciveCityId.getCodeValue());
                        }
                    }
                }
                String reciveAddress = l.get(6);
                if (org.apache.commons.lang.StringUtils.isEmpty(reciveAddress)) {
                    reasonFailure.append("???????????????????????????!");
                }
                cmCustomerInfoVo.setReciveAddress(reciveAddress);
                //param.put("reciveAddress", reciveAddress);
                String lineName = l.get(7);
                if (org.apache.commons.lang.StringUtils.isEmpty(lineName)) {
                    reasonFailure.append("??????????????????!");
                }
                cmCustomerInfoVo.setLineName(lineName);
                // param.put("lineName", lineName);

                String lineTel = l.get(8);
                cmCustomerInfoVo.setLineTel(lineTel);
                //param.put("lineTel", lineTel);

                String linePhone = l.get(9);
                if (org.apache.commons.lang.StringUtils.isEmpty(linePhone)) {
                    reasonFailure.append("?????????????????????!");
                }
                cmCustomerInfoVo.setLinePhone(linePhone);
                // param.put("linePhone", linePhone);

                String payWayName = l.get(10);
                if (org.apache.commons.lang.StringUtils.isEmpty(payWayName)) {
                    reasonFailure.append("?????????????????????!");
                } else {
                    SysStaticData payWay = getSysStaticData("BALANCE_TYPE", payWayName);
                    if (StrUtil.isEmpty(payWay.getCodeValue())) {
                        reasonFailure.append("?????????????????????!");
                    } else {
                        // param.put("payWay", payWay);
                        cmCustomerInfoVo.setPayWay(Integer.valueOf(payWay.getCodeValue()));
                        cmCustomerInfoVo.setPayWayName(payWay.getCodeValue());
                        if ("1".equals(payWay.getCodeValue()) || "2".equals(payWay.getCodeValue())) {
                            String reciveTime = l.get(11);
                            if (org.apache.commons.lang.StringUtils.isEmpty(reciveTime) || !CommonUtil.isNumber(reciveTime)) {
                                reasonFailure.append("????????????????????????????????????!");
                            }
                            //param.put("reciveTime", reciveTime);
                            cmCustomerInfoVo.setReciveTime(Integer.valueOf(reciveTime));
                            String reconciliationTime = l.get(12);
                            if (org.apache.commons.lang.StringUtils.isNotEmpty(reconciliationTime) && !CommonUtil.isNumber(reconciliationTime)) {
                                reasonFailure.append("?????????????????????!");
                            }
                            //param.put("reconciliationTime", reconciliationTime);
                            cmCustomerInfoVo.setReconciliationTime(Integer.valueOf(reconciliationTime));
                            String invoiceTime = l.get(13);
                            if (org.apache.commons.lang.StringUtils.isNotEmpty(invoiceTime) && !CommonUtil.isNumber(invoiceTime)) {
                                reasonFailure.append("?????????????????????!");
                            }

                            // param.put("invoiceTime", invoiceTime);
                            cmCustomerInfoVo.setInvoiceTime(Integer.valueOf(invoiceTime));
                            if ("2".equals(payWay.getCodeValue())) {
                                String collectionTime = l.get(14);
                                if (org.apache.commons.lang.StringUtils.isEmpty(collectionTime) || !CommonUtil.isNumber(collectionTime)) {
                                    reasonFailure.append("????????????????????????????????????!");
                                }
                                //param.put("collectionTime", collectionTime);
                                cmCustomerInfoVo.setCollectionTime(Integer.valueOf(collectionTime));
                            }
                        } else {
                            String reciveTime = l.get(11);
                            if (org.apache.commons.lang.StringUtils.isEmpty(reciveTime)) {
                                reasonFailure.append("?????????????????????!");
                            } else {
                                String[] reciveTimes = reciveTime.split(",");
                                if (reciveTimes.length != 2) {
                                    reasonFailure.append("??????????????????????????????!");
                                } else {
                                    int reciveMonth = Integer.parseInt(reciveTimes[0]);
                                    int reciveDay = Integer.parseInt(reciveTimes[1]);
                                    if (reciveMonth <= 0 || reciveMonth > 12) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    if (reciveDay <= 0 || reciveDay > 31) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    cmCustomerInfoVo.setReciveMonth(Integer.valueOf(reciveMonth));
                                    cmCustomerInfoVo.setReciveDay(Integer.valueOf(reciveDay));
                                }
                            }
                            String reconciliationTime = l.get(12);
                            if (StrUtil.isNotEmpty(reconciliationTime)) {
                                String[] reconciliationTimes = reconciliationTime.split(",");
                                if (reconciliationTimes.length != 2) {
                                    reasonFailure.append("??????????????????????????????!");
                                } else {
                                    int reconciliationMonth = Integer.parseInt(reconciliationTimes[0]);
                                    int reconciliationDay = Integer.parseInt(reconciliationTimes[1]);
                                    if (reconciliationMonth <= 0 || reconciliationMonth > 12) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    if (reconciliationDay <= 0 || reconciliationDay > 31) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    cmCustomerInfoVo.setReconciliationMonth(Integer.valueOf(reconciliationMonth));
                                    cmCustomerInfoVo.setReconciliationDay(reconciliationDay);
                                }
                            }
                            String invoiceTime = l.get(13);
                            if (StrUtil.isNotEmpty(invoiceTime)) {
                                String[] invoiceTimes = invoiceTime.split(",");
                                if (invoiceTimes.length != 2) {
                                    reasonFailure.append("??????????????????????????????!");
                                } else {
                                    int invoiceMonth = Integer.parseInt(invoiceTimes[0]);
                                    int invoiceDay = Integer.parseInt(invoiceTimes[1]);
                                    if (invoiceMonth <= 0 || invoiceMonth > 12) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    if (invoiceDay <= 0 || invoiceDay > 31) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    cmCustomerInfoVo.setInvoiceMonth(Integer.valueOf(invoiceMonth));
                                    cmCustomerInfoVo.setInvoiceDay(Integer.valueOf(invoiceDay));
                                }
                            }
                            String collectionTime = null;
                            if (l.get(14) != null) {
                                collectionTime = l.get(14);
                            }
                            if (StrUtil.isEmpty(collectionTime)) {
                                reasonFailure.append("?????????????????????!");
                            } else {
                                String[] collectionTimes = collectionTime.split(",");
                                if (collectionTimes.length != 2) {
                                    reasonFailure.append("??????????????????????????????!");
                                } else {
                                    int collectionMonth = Integer.parseInt(collectionTimes[0]);
                                    int collectionDay = Integer.parseInt(collectionTimes[1]);
                                    if (collectionMonth <= 0 || collectionMonth > 12) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    if (collectionDay <= 0 || collectionDay > 31) {
                                        reasonFailure.append("??????????????????????????????????????????!");
                                    }
                                    cmCustomerInfoVo.setCollectionMonth(collectionMonth);
                                    cmCustomerInfoVo.setCollectionDay(collectionDay);
                                }
                            }

                        }
                    }
                }
                if (StrUtil.isEmpty(reasonFailure)) {
                    try {
                        this.saveOrUpdateCustomerCopy(cmCustomerInfoVo, token);
                        //??????????????????
                        success++;
                    } catch (BusinessException businessException) {
                        cmCustomerInfoVo.setReasonFailure(businessException.getMessage());
                        failureList.add(cmCustomerInfoVo);
                    }
                } else {
                    cmCustomerInfoVo.setReasonFailure(reasonFailure.toString());
                    failureList.add(cmCustomerInfoVo);
                }
            }
            if (CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????(??????)", "????????????(??????)", "????????????(??????)", "????????????(??????)", "????????????",
                        "????????????(??????)", "??????????????????(??????)", "?????????(??????)", "????????????", "????????????(??????)", "????????????(??????)"
                        , "????????????(??????)", "????????????", "????????????", "????????????(??????)", "????????????"};
                resourceFild = new String[]{"getCompanyName", "getCustomerName", "getLookupName", "getOddWayName", "getAddress",
                        "getReciveProvinceName", "getReciveAddress", "getLineName", "getLineTel", "getLinePhone", "getPayWayName",
                        "getReciveTime", "getReconciliationTime", "getInvoiceTime", "getCollectionTime", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, CmCustomerInfoVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                inputStream.close();
                inputStream1.close();
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
            importOrExportRecordsService.update(records);
            e.printStackTrace();
        }
    }

    @Override
    public void doPublicAuth(Long customerId, int authState, String auditContent) throws BusinessException {
        CmCustomerInfo cmCustomerInfo = super.getById(customerId);
        QueryWrapper<CmCustomerInfoVer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId);
        queryWrapper.orderByDesc("id");
        List<CmCustomerInfoVer> cmCustomerInfoVers = cmCustomerInfoVerService.list(queryWrapper);
        CmCustomerInfoVer cmCustomerInfoVer = null;
        if (cmCustomerInfoVers != null && cmCustomerInfoVers.size() > 0) {
            cmCustomerInfoVer = cmCustomerInfoVers.get(0);
        }
        cmCustomerInfo.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        if (authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3) {
            cmCustomerInfo.setAuditContent(auditContent);
            super.update(cmCustomerInfo);
        } else if (authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
            BeanUtil.copyProperties(cmCustomerInfoVer, cmCustomerInfo);
            if (cmCustomerInfoVer.getCustomerId() != null) {
                cmCustomerInfo.setId(cmCustomerInfoVer.getCustomerId());
            }
            cmCustomerInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
            cmCustomerInfo.setAuditContent(auditContent);
            super.saveOrUpdate(cmCustomerInfo);
        }
    }

    @Override
    public void auditingCallBack(Long busiId) throws Exception {

    }

    @Override
    public Page<BackUserDto> doQueryBackUserList(Page<BackUserDto> page, String linkman, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if(StringUtils.isBlank(linkman)){
            linkman="";
        }
        Page<BackUserDto> backUserDtoPage = baseMapper.doQueryBackUserList(page, linkman, user.getTenantId());
        for (BackUserDto backUser : backUserDtoPage.getRecords()
        ) {
            if (backUser.getCarUserType() != null && backUser.getCarUserType() == 0) {
                backUser.setCarUserTypeName("??????");
            } else if (backUser.getCarUserType() != null && backUser.getCarUserType() > -1) {
                SysStaticData sysStaticData = getSysStaticDataId("DRIVER_TYPE", backUser.getCarUserType() + "");
                if (sysStaticData != null && sysStaticData.getCodeName() != null) {
                    backUser.setCarUserTypeName(sysStaticData.getCodeName());
                }
            }
        }
        return backUserDtoPage;
    }

    @Override
    public CmCustomerInfo getInfoById(long id, String accessToken) {
        CmCustomerInfo result = checkDataPermissionCriteria(id, 1, accessToken);
        return result;
    }

    @Override
    public List<WorkbenchDto> getTableCustomerCount() {
        return baseMapper.getTableCustomerCount();
    }

    @Override
    public String queryCustomerIdByName(String name) {
        LambdaQueryWrapper<CmCustomerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(CmCustomerInfo::getCompanyName, name);
        List<CmCustomerInfo> list = this.list(queryWrapper);
        StringBuffer sb = new StringBuffer();
        for (CmCustomerInfo cmCustomerInfo : list) {
            sb.append("'").append(cmCustomerInfo.getId()).append("',");
        }
        if (sb.length() == 0) {
            return "";
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * ?????????????????????Criteria??????
     *
     * @param type 1-?????????2-??????
     */
    public CmCustomerInfo checkDataPermissionCriteria(Long id, Integer type, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        CmCustomerInfo cmCustomerInfo = null;
//        CmCustomerLine cmCustomerLine = null;

//        if (type == 1) {
        cmCustomerInfo = this.getById(id);
        if (cmCustomerInfo == null) {
            throw new BusinessException("???????????????");
        }
//        }
        /*else {
            cmCustomerLine = iCmCustomerLineService.getById(id);
            if (cmCustomerLine == null) {
                throw new BusinessException("???????????????");
            }

        }
*/
        boolean hasAllDataPermission = iSysRoleService.hasAllData(loginInfo);

        if (!hasAllDataPermission) {//????????????????????????,????????????????????????

            if (loginInfo.getOrgIds() == null || loginInfo.getOrgIds().size() == 0) {
                log.info("?????????userId:" + loginInfo.getId() + "?????????????????????????????????????????????,?????????????????????");
                throw new BusinessException("??????????????????????????????????????????????????????");
            }

            List<Long> orgList = sysOrganizeService.getSubOrgList(loginInfo.getTenantId(), loginInfo.getOrgIds());
            long rootOrgid = sysOrganizeService.getRootOragnize(loginInfo.getTenantId()).getId();
            orgList.add(rootOrgid);

            if (type == 1) {//????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                LambdaQueryWrapper<CmCustomerLine> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CmCustomerLine::getId, id);
                queryWrapper.eq(CmCustomerLine::getAuthState, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
                queryWrapper.in(CmCustomerLine::getSaleDaparment, orgList);
                List<CmCustomerLine> list1 = iCmCustomerLineService.list();
                if (list1 != null && list1.size() > 0) {
                    return cmCustomerInfo;
                }
            }

            long orgId = -100L;
            if (type == 1) {
                orgId = cmCustomerInfo.getOrgId();
            }
            /*else {
                orgId = cmCustomerLine.getSaleDaparment();
            }*/

            if (!orgList.contains(orgId)) {
                throw new BusinessException("???????????????????????????????????????");
            }

        }

        return cmCustomerInfo;

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
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));

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


    private List<List<String>> getTxtContent(InputStream inputStream, int beginRow, ExcelFilesVaildate[] validates) throws Exception {
        ArrayList fileContent = new ArrayList();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            List<String> rowList = null;
            String lineContent = null;

            for (int curRowNO = 1; (lineContent = br.readLine()) != null; ++curRowNO) {
                if (beginRow <= curRowNO) {
                    rowList = new ArrayList();
                    fileContent.add(rowList);
                    if (org.apache.commons.lang.StringUtils.isNotEmpty(lineContent) && !lineContent.matches("^(\\t)*$")) {
                        String[] cels = lineContent.split("\\t");
                        if (cels.length > 200) {
                            throw new Exception("??????????????????200????????????????????????");
                        }

                        for (int j = 0; j < cels.length; ++j) {
                            String cellValue = cels[j];
                            if (validates != null && validates.length > j) {
                                if (cellValue == null) {
                                    throw new Exception("???" + curRowNO + "???,???" + (j + 1) + "????????????????????????" + validates[j].getErrorMsg());
                                }

                                Pattern p = Pattern.compile(validates[j].getPattern());
                                Matcher m = p.matcher(cellValue);
                                if (!m.matches()) {
                                    throw new Exception("???" + curRowNO + "???,???" + (j + 1) + "????????????????????????" + validates[j].getErrorMsg());
                                }
                            }
                        }

                        rowList.addAll(Arrays.asList(cels));
                    }
                }

                if (curRowNO >= 1000 + beginRow) {
                    rowList.clear();
                    rowList = null;
                    fileContent.clear();
                    fileContent = null;
                    throw new Exception("txt??????????????????????????????????????????????????????" + 1000);
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }

        }

        return fileContent;
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
}
