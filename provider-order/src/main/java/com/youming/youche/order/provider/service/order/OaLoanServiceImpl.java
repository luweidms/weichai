package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IOaLoadVerificationService;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.constant.OaLoanData;
import com.youming.youche.finance.domain.OaLoadVerification;
import com.youming.youche.order.api.order.IClaimExpenseInfoService;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.domain.order.ClaimExpenseInfo;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.dto.OaloanCountDto;
import com.youming.youche.order.dto.order.OaLoanDto;
import com.youming.youche.order.dto.order.OaLoanListDto;
import com.youming.youche.order.provider.mapper.order.OaLoanMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.record.api.oa.IOaFilesService;
import com.youming.youche.record.common.SysOperLogConst;
import com.youming.youche.record.domain.oa.OaFiles;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
* <p>
    * 借支信息表 服务实现类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
@DubboService(version = "1.0.0")
@Service
    public class OaLoanServiceImpl extends BaseServiceImpl<OaLoanMapper, OaLoan> implements IOaLoanService {

    @Resource
    private OaLoanMapper oaLoanMapper;
    @Resource
    private IClaimExpenseInfoService claimExpenseInfoService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;

    @DubboReference(version = "1.0.0")
    ISysRoleService iSysRoleService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @DubboReference(version = "1.0.0")
    IOaLoadVerificationService iOaLoadVerificationService;

    @DubboReference(version = "1.0.0")
    IOaFilesService iOaFilesService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;

    @Override
    public boolean checkLoanAndExpenseByOrder(Long orderId) {
        boolean exist = false;

        List<OaLoan> oaLoans = this.getOaLoanFee(orderId);
        if(oaLoans!=null && oaLoans.size()>0){
            exist = true;
        }
        List<ClaimExpenseInfo> claimExpenseInfos = claimExpenseInfoService.getValidExpenseInfoByOrder(orderId);
        if(claimExpenseInfos!=null && claimExpenseInfos.size()>0){
            exist = true;
        }
        return exist;
    }

    @Override
    public List<OaLoan> getOaLoanFee(long orderId) {

        QueryWrapper<OaLoan> oaLoanQueryWrapper = new QueryWrapper<>();
        oaLoanQueryWrapper.eq("order_id",orderId)
                .ne("sts",8)
                .ne("sts",2);
        List<OaLoan> oaLoans = oaLoanMapper.selectList(oaLoanQueryWrapper);
        return oaLoans;
    }

    @Override
    public OaLoan queryOaLoanById(Long LId, String... busiCode) {
        LambdaQueryWrapper<OaLoan> lambda= Wrappers.lambdaQuery();
        if(LId>0){
            lambda.eq(OaLoan::getId, LId);
        }else if(busiCode.length>0){
            lambda.eq(OaLoan::getOaLoanId, busiCode[0]);
        } else {
            return null;
        }
        OaLoan oaLoan = null;
        List<OaLoan> lists = this.list(lambda);
        if(lists.size() > 0){
            oaLoan = lists.get(0);
        }
        return oaLoan;
    }

    @Override
    public void updOaLoanPayFlowId(Long batchId, Long flowId) {
        LambdaUpdateWrapper<OaLoan> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(OaLoan::getPayFlowId, batchId);
        updateWrapper.eq(OaLoan::getFlowId, flowId);
        this.update(updateWrapper);
    }

    @Override
    public void setPayFlowIdAfterPay(Long flowId) {
        if(StringUtils.isNotBlank(flowId+"") && flowId>0){
            LambdaQueryWrapper<OaLoan> lambda=Wrappers.lambdaQuery();
            lambda.eq(OaLoan::getFlowId,flowId);
            List<OaLoan> lists = this.list(lambda);
            if(lists!=null && lists.size()==1){
                OaLoan oa = lists.get(0);
                oa.setPayFlowId(flowId);
                this.saveOrUpdate(oa);
            }
        }
    }

    @Override
    public List<OaLoan> queryOaLoan(Long userId, Long tenantId, List<Integer> loanType, String carPhone, Long orderId) {
        List<Integer> sts = new ArrayList<>();
        sts.add(OaLoanConsts.STS.STS3);
        sts.add(OaLoanConsts.STS.STS4);
        sts.add(OaLoanConsts.STS.STS5);
        List<OaLoan> list = oaLoanMapper.selectSalary(userId,tenantId,carPhone,loanType,sts);
        return list;
    }

    @Override
    public OaloanCountDto queryOaloanCount(Long orderId, Long userId) {
//        OaLoan oaLoan = null;
        LambdaQueryWrapper<OaLoan> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OaLoan::getLaunch,OaLoanConsts.LAUNCH.LAUNCH2);
        queryWrapper.eq(OaLoan::getOrderId,orderId);
        if(null != userId){
            queryWrapper.eq(OaLoan::getUserId,userId);
        }
        queryWrapper.in(OaLoan::getSts, new Object[]{3,4,5,6,7});
        List<OaLoan> lists = this.baseMapper.selectList(queryWrapper);
        OaloanCountDto dto=new OaloanCountDto();
        Long amount = 0L;
        if(lists.size() > 0){
            for(OaLoan ol:lists){
                amount+=ol.getAmount();
            }
        }
        int count = oaLoanMapper.queryOaloanCount(orderId, userId);
        dto.setCount(count);
        dto.setAmount(amount);
        return dto;
    }

    @Override
    public Page<OaLoanListDto> queryOaLoanList(String oaLoanId, String loanSubjects, Long orderId, String plateNumber,
                                               String userName, String mobilePhone, String states, Integer queryType,
                                               Boolean waitDeal, String accessToken, Integer pageNum, Integer pageSize) {

        LoginInfo loginInfo = loginUtils.get(accessToken);

        List<Long> lids = null;
        StringBuffer lidBuf = new StringBuffer();
        String lidStr = "";
        if (waitDeal != null && waitDeal) {// 待我审核
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {// 车管借支
                lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TUBE_BORROW, loginInfo.getId(), loginInfo.getTenantId());
            } else if (queryType == OaLoanConsts.QUERY_TYPE.driver) {// 司机借支
                lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DRIVER_BORROW, loginInfo.getId(), loginInfo.getTenantId());
            }
        }

        if (lids != null) {
            for (Long lid : lids) {
                lidBuf.append("'").append(lid).append("',");
            }
            if (lidBuf != null && lidBuf.length() != 0) {
                lidStr = lidBuf.substring(0, lidBuf.length() - 1);
            }
        }

        //判断是否有所有数据权限(操作员如果只有查看部门数据的权限，则只能所有归属于本部门的车管借支；如果有查看所有数据的权限，则可以查看所有的车管借支。)
        List<Long> orgList = null;
        StringBuffer orgBuf = new StringBuffer();
        String orgStr = "";
        boolean hasAllDataPermission = iSysRoleService.hasAllData(loginInfo);
        if (!hasAllDataPermission) {
            orgList = sysOrganizeService.getSubOrgList(loginInfo.getId().toString());
        }

        if (orgList != null) {
            for (Long aLong : orgList) {
                orgBuf.append("'").append(aLong).append("',");
            }
            if (orgBuf != null && orgBuf.length() != 0) {
                orgStr = orgBuf.substring(0, lidBuf.length() - 1);
            }
        }

        Page<OaLoanListDto> oaLoanListDtoPage = baseMapper.queryOaLoanList(new Page<OaLoanListDto>(pageNum, pageSize), loginInfo.getTenantId(), oaLoanId,
                loanSubjects, orderId, plateNumber, userName, mobilePhone, states, queryType, lidStr, orgStr,
                this.getLoanBelongAdminSubjectList(), this.getLoanBelongDriverSubjectList(), waitDeal);

        List<OaLoanListDto> list = oaLoanListDtoPage.getRecords();

        List<Long> busiIds = new ArrayList<>();
        //查询是否有审核权限
        for (OaLoanListDto object : list) {
            long relId = object.getLId();
            busiIds.add(relId);
        }

        Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
        Map<Long, Boolean> hasDriverPermissionMap = new HashMap<Long, Boolean>();
        if (!busiIds.isEmpty()) {
            hasPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.TUBE_BORROW, busiIds);
            hasDriverPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.DRIVER_BORROW, busiIds);
        }

        List<OaLoanListDto> listOut = new ArrayList<OaLoanListDto>();
        if(list!=null&&list.size()>0){
            for (OaLoanListDto object : list) {
                OaLoanListDto map = new OaLoanListDto();
                map.setLId(object.getLId());
                map.setAmount(object.getAmount());
                map.setLoanSubject(object.getLoanSubject());
                map.setLoanSubjectName(getSysStaticData("LOAN_SUBJECT", object.getLoanSubject() + "").getCodeName());
                map.setIsNeedBill(object.getIsNeedBill());
                map.setIsNeedBillName(getSysStaticData("IS_NEED_BILL_OA", object.getIsNeedBill() + "").getCodeName());
                map.setOaLoanId(object.getOaLoanId());
                map.setOrderId(object.getOrderId());
                map.setPlateNumber(object.getPlateNumber());
                map.setPhoneNumber(object.getPhoneNumber());
                map.setUserName(object.getUserName());
                map.setSts(object.getSts());
                map.setStsName(getSysStaticData("LOAN_STATE", object.getSts() + "").getCodeName());
                boolean isTrue=false;
                if(hasPermissionMap.get(object.getLId())!=null&&hasPermissionMap.get(object.getLId())){
                    isTrue=true;
                }
                if(hasDriverPermissionMap.get(object.getLId())!=null&&hasDriverPermissionMap.get(object.getLId())){
                    isTrue=true;
                }
                //判断是否有审核权限
                if((object.getSts()==OaLoanConsts.STS.STS0||object.getSts()==OaLoanConsts.STS.STS1)&&isTrue){
                    map.setHasPermission(true);
                }else{
                    map.setHasPermission(false);
                }
                listOut.add(map);
            }
        }
        oaLoanListDtoPage.setRecords(listOut);

        return oaLoanListDtoPage;
    }

    @Override
    public OaLoanDto queryOaLoanByIdWx(Long LId, String accessToken, String... busiCode) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        List<Long> fileId = new ArrayList<Long>();// 附件id
        List<String> fileUrl = new ArrayList<String>();// 借支信息文件
        List<String> fileName = new ArrayList<String>();
        List<Long> fileIdV = new ArrayList<Long>();// 附件id
        List<String> fileUrlV = new ArrayList<String>();// 核销文件url
        List<String> fileNameV = new ArrayList<String>();// 核销文件名字
        OaLoan oaLoan = this.queryOaLoanById(LId, busiCode);
        LId = oaLoan.getId();
        List<OaLoadVerification> oaLoadVerifications = iOaLoadVerificationService.getOaLoadVerificationsByLId(LId);
        List<OaFiles> oaFiles = iOaFilesService.queryOaFilesById(LId);
        OaLoanDto oaLoanOut = new OaLoanDto();
        BeanUtil.copyProperties(oaLoan, oaLoanOut);
        if (oaFiles != null) {
            for (OaFiles of : oaFiles) {
                if (of.getRelType().equals(OaLoanData.RELTYPE1)) {
                    fileId.add(of.getFileId());
                    fileUrl.add(of.getFileUrl());
                    fileName.add(of.getFileName());
                } else if (of.getRelType().equals(OaLoanData.RELTYPE2)) {
                    fileIdV.add(of.getFileId());
                    fileUrlV.add(of.getFileUrl());
                    fileNameV.add(of.getFileName());
                }
            }
        }
        if (oaLoadVerifications != null && oaLoadVerifications.size() > 0) {
            OaLoadVerification oaLoadVerification = oaLoadVerifications.get(0);
            if (oaLoadVerification.getCashAmount() != null) {
                oaLoanOut.setCashAmount(oaLoadVerification.getCashAmount());
            } else {
                oaLoanOut.setCashAmount(0L);
            }
            if (oaLoadVerification.getBillAmount() != null) {
                oaLoanOut.setBillAmount(oaLoadVerification.getBillAmount());
            } else {
                oaLoanOut.setBillAmount(0L);
            }

            oaLoanOut.setAllCashAmount(0.0f);
            oaLoanOut.setAllBillAmount(0.0f);
            for (OaLoadVerification verification : oaLoadVerifications) {
                if (verification.getCashAmount() != null) {
                    oaLoanOut.setAllCashAmount(verification.getCashAmount() + oaLoanOut.getAllCashAmount());
                }
                if (verification.getBillAmount() != null) {
                    oaLoanOut.setAllBillAmount(verification.getBillAmount() + oaLoanOut.getAllBillAmount());
                }
            }

        }
        oaLoanOut.setFileUrl(fileUrl);
        oaLoanOut.setFileName(fileName);
        oaLoanOut.setFileUrlV(fileUrlV);
        oaLoanOut.setFileNameV(fileNameV);
        oaLoanOut.setFileId(fileId);
        oaLoanOut.setFileIdV(fileIdV);

        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(oaLoan.getUserInfoId());
        oaLoanOut.setMobilePhone(userDataInfo.getMobilePhone());
        oaLoanOut.setAccUserId(oaLoan.getBorrowUserId());
        oaLoanOut.setAccName(oaLoan.getAccName());
        oaLoanOut.setAccNo(oaLoan.getAccNo());
        oaLoanOut.setBankName(oaLoan.getBankName());
        oaLoanOut.setBankBranch(oaLoan.getBankBranch());

        //操作日志
        SysOperLogConst.BusiCode busi_code = SysOperLogConst.BusiCode.TubeBorrow;//车管借支
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
            busi_code = SysOperLogConst.BusiCode.DriverBorrow;
        }

//        List<SysOperLog> sysOperLogs = iSysOperLogService.querySysOperLogAll(busi_code.getCode(), LId, false, oaLoanOut.getTenantId(), auditCode, LId);
//        if (sysOperLogs != null && sysOperLogs.size() != 0) {
//            oaLoanOut.setSysOperLogs(sysOperLogs);
//        }

        oaLoanOut.setAccidentDivideName(getSysStaticData("LOAN_ACCIDENT_DRIVER", oaLoanOut.getAccidentDivide() + "").getCodeName());
        oaLoanOut.setAccidentReasonName(getSysStaticData("LOAN_ACCIDENT_REASON", oaLoanOut.getAccidentReason() + "").getCodeName());
        oaLoanOut.setAccidentTypeName(getSysStaticData("LOAN_ACCIDENT_TYPE", oaLoanOut.getAccidentType() + "").getCodeName());
        oaLoanOut.setDutyDivideName(getSysStaticData("LOAN_DUTY_DIVIDE", oaLoanOut.getDutyDivide() + "").getCodeName());
        oaLoanOut.setIsNeedBillName(getSysStaticData("IS_NEED_BILL_OA", oaLoanOut.getIsNeedBill() + "").getCodeName());
        oaLoanOut.setLoanSubjectName(getSysStaticData("LOAN_SUBJECT", oaLoanOut.getLoanSubject() + "").getCodeName());
        oaLoanOut.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(oaLoanOut.getTenantId(), oaLoanOut.getOrgId()));
        oaLoanOut.setStsName(getSysStaticData("LOAN_STATE", oaLoanOut.getSts() + "").getCodeName());

        oaLoanOut.setCurrentUserId(loginInfo.getUserInfoId());
        oaLoanOut.setCurrentOrgId(loginInfo.getId());
        oaLoanOut.setAmountDouble(CommonUtil.getDoubleFormatLongMoney(oaLoanOut.getAmount(), 2));
        oaLoanOut.setPayedAmountDouble(CommonUtil.getDoubleFormatLongMoney(oaLoanOut.getPayedAmount(), 2));

        return oaLoanOut;
    }

    /**
     * 返回司机申请但资金归属车管的借支类型
     */
    private String getLoanBelongAdminSubjectList() {
        List result = new ArrayList();
        Set extsubject = new HashSet();
        List<SysStaticData> ext = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP")); // 司机借支
        List<SysStaticData> total = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT")); // 司机借支

        for (SysStaticData data : ext) {
            extsubject.add(data.getCodeValue());
        }

        for (SysStaticData data : total) {
            if (!extsubject.contains(data.getCodeValue())) {
                result.add(data.getCodeValue());
            }
        }

        StringBuffer buf = new StringBuffer();
        for (Object o : result) {
            buf.append("'").append(o).append("',");
        }

        return buf != null && buf.length() != 0 ? buf.substring(0, buf.length() - 1) : "";
    }

    /**
     * 返回资金归属司机的借支类型
     */
    private String getLoanBelongDriverSubjectList() {
        List loanSubjects = new ArrayList();
        List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));//司机借支
        for (SysStaticData data : dataList) {
            loanSubjects.add(data.getCodeValue());
        }
        StringBuffer buf = new StringBuffer();
        for (Object loanSubject : loanSubjects) {
            buf.append("'").append(loanSubject).append("',");
        }
        return buf != null && buf.length() != 0 ? buf.substring(0, buf.length() - 1) : "";
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
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

    /**
     * 判断该借支类型是否归属司机
     */
    private boolean checkLoanBelongDriver(OaLoan oaLoan) {
        boolean flag = false;
        if (oaLoan.getClassify() != null && oaLoan.getLaunch() != null) {
            if (oaLoan.getClassify() == 2 && oaLoan.getLaunch() == 2) {
                List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));//司机借支
                for (SysStaticData data : dataList) {
                    if (StringUtils.equals(data.getCodeValue(), oaLoan.getLoanSubject() + "")) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

}
