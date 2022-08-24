package com.youming.youche.record.business.controller.violation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.violation.IViolationRecordService;
import com.youming.youche.record.business.controller.user.UserController;
import com.youming.youche.record.common.ChkIntfData;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.violation.ViolationRecord;
import com.youming.youche.record.dto.violation.ViolationDto;
import com.youming.youche.record.vo.violation.ViolationRecordVo;
import com.youming.youche.record.vo.violation.ViolationVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.util.BeanUtils;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * hzx
 * 车辆违章
 *
 * @date 2022/1/7 15:02
 */
@RestController
@RequestMapping("violation")
public class ViolationController extends BaseController<ViolationRecord, IViolationRecordService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @DubboReference(version = "1.0.0")
    private IViolationRecordService iViolationRecordService;

    @DubboReference(version = "1.0.0")
    private IUserDataInfoRecordService iUserDataInfoRecordService;

    @DubboReference(version = "1.0.0")
    private ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IViolationRecordService getService() {
        return iViolationRecordService;
    }

    /**
     * 违章信息查询
     *
     * @param violationVo plateNumber 车牌号码
     *                    linkman 司机名称
     *                    linkmanPhone 司机手机
     *                    startTime 违章开始
     *                    endTime 违章结束
     *                    violationCity 违章城市
     *                    peccancyCode 违章代码
     *                    publicAndPrivateType 公私类型
     * @param pageNum     分页参数
     * @param pageSize    分页参数
     */
    @GetMapping("queryViolationRecordList")
    public ResponseResult queryViolationRecordList(
            ViolationVo violationVo,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            Page<ViolationDto> objectPage = new Page<>(pageNum, pageSize);
            Page<ViolationDto> maps = iViolationRecordService.doQuery(objectPage, violationVo, accessToken);
            return ResponseResult.success(maps);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 违章信息查询
     *
     * @param violationVo plateNumber 车牌号码
     *                    linkman 司机名称
     *                    linkmanPhone 司机手机
     *                    startTime 违章开始
     *                    endTime 违章结束
     *                    violationCity 违章城市
     *                    peccancyCode 违章代码
     *                    publicAndPrivateType 公私类型
     */
    @GetMapping("export")
    public ResponseResult export(ViolationVo violationVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆违章信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iViolationRecordService.doQueryExport(violationVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败车辆违章列表异常" + e);
            return ResponseResult.failure("导出成功车辆违章异常");
        }
    }

    /**
     * 获取违章具体信息
     */
    public ResponseResult getViolationInfo(@RequestParam("recordId") Long recordId) {
        try {
            ViolationRecord violationRecord = iViolationRecordService.queryById(recordId);
            return ResponseResult.success(violationRecord);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 新增或修改
     */
    @PostMapping("saveOrUpdateViolationRecord")
    public ResponseResult saveOrUpdateViolationRecord(@RequestParam("type") Integer type, @RequestParam("flag") Integer flag,
                                                      @RequestParam("recordId") Long recordId, @RequestParam("accessType") Integer accessType,
                                                      @RequestParam("violationWritNo") String violationWritNo, @RequestParam("renfaTime") String renfaTime,
                                                      @RequestParam("violationFine") String violationFine, @RequestParam("plateNumber") String plateNumber,
                                                      @RequestParam("fineImageUrl") String fineImageUrl, @RequestParam("fineImageId") Long fineImageId
    ) {
        try {
//            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//            UserDataInfo user = iUserDataInfoRecordService.getUserDataInfoByAccessToken(accessToken);
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            SysUser sysUser = null;
            HashMap<String, Object> inParam = Maps.newHashMap();
            inParam.put("type", type);
            inParam.put("recordId", recordId);
            inParam.put("violationWritNo", violationWritNo);
            inParam.put("violationFine", violationFine);
            inParam.put("fineImageUrl", fineImageUrl);
            inParam.put("flag", flag);
            inParam.put("accessType", accessType);
            inParam.put("renfaTime", renfaTime);
            inParam.put("plateNumber", plateNumber);
            inParam.put("fineImageId", fineImageId);
            //检查基础数据
            ViolationRecord record = new ViolationRecord();
            if (type != 1 && type != 2) {
                throw new BusinessException("请选择公私判断！");
            }

            if (flag == 1) {//flag=1是修改
                if (recordId <= 0) {
                    throw new BusinessException("违章记录ID为空！");
                }
                record.setId(recordId);
                record.setType(type);
                iViolationRecordService.saveOrUpdateViolationRecord(record, sysUser, accessToken);
                return ResponseResult.success("修改成功");
            }
            //下面是新增的
            if (StringUtils.isBlank(violationWritNo)) {
                throw new BusinessException("请输入查询的文书号！");
            }
            if (accessType == EnumConsts.ACCESS_TYPE.ACCESS_TYPE01) {//手动填写的违章信息
                if (StringUtils.isBlank(renfaTime)) {
                    throw new BusinessException("请输填写认罚日期!");
                }
                if (!CommonUtil.isNumber(violationFine)) {
                    throw new BusinessException("请输入罚款金额!");
                }
                if (StringUtils.isEmpty(plateNumber)) {
                    throw new BusinessException("请输入车牌号码!");
                }
                if (!ChkIntfData.chkPlateNumber(plateNumber)) {
                    throw new BusinessException("输入的车牌号码错误!");
                }
                if (fineImageId <= 0 || StringUtils.isBlank(fineImageUrl)) {
                    throw new BusinessException("请上传罚单照片!");
                }
            }

            //处理数据
            List<String> fenString = new ArrayList<>();
            fenString.add("violationFine");
            fenString.add("overdueFine");
            CommonUtil.transYuanFenToFen(fenString, inParam);
            BeanUtils.populate(record, inParam);
            SimpleDateFormat format = new SimpleDateFormat(DateUtil.DATETIME_FORMAT1);
            record.setRenfaTime(format.format(DateUtil.parseDate(renfaTime, DateUtil.DATETIME_FORMAT1)));//历史问题,名字对不上...
            String violationTime = DataFormat.getStringKey(inParam, "violationTime");
            if (StringUtils.isNotEmpty(violationTime)) {
                record.setViolationTime(format.format(DateUtil.parseDate(renfaTime, DateUtil.DATETIME_FORMAT1)));//历史问题,名字对不上...
            }
            iViolationRecordService.saveOrUpdateViolationRecord(record, sysUser, accessToken);
            return ResponseResult.success("添加成功");
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }


    /**
     * 实现功能: 分页查询违章明细
     *
     * @param monthTime   查询月份
     * @param licenceType 牌照类型 1整车 2拖头
     * @param recordState 处理状态 0、未处理；1、处理中；2、已完成
     */
    @GetMapping("getViolationDetails")
    public ResponseResult getViolationDetails(@RequestParam("monthTime") String monthTime,
                                              @RequestParam("licenceType") Integer licenceType,
                                              @RequestParam("recordState") Integer recordState,
                                              @RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ViolationRecordVo> page = new Page<>(pageNum, pageSize);
        Page<ViolationRecordVo> violationDetails = iViolationRecordService.getViolationDetails(page, monthTime, licenceType, recordState, accessToken);
        return ResponseResult.success(violationDetails);
    }

    /**
     * 违章明细导出
     *
     * @param monthTime   查询月份
     * @param licenceType 牌照类型 1整车 2拖头
     * @param recordState 处理状态 0、未处理；1、处理中；2、已完成
     */
    @GetMapping("getViolationDetails/export")
    public ResponseResult getViolationDetailsExport(String monthTime,
                                                    Integer licenceType,
                                                    Integer recordState) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("违章明细信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iViolationRecordService.getViolationDetailsExport(monthTime, licenceType, recordState, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败违章明细列表异常" + e);
            return ResponseResult.failure("导出成功违章明细异常");
        }
    }

}
