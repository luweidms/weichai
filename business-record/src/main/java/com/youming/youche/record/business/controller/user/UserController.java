package com.youming.youche.record.business.controller.user;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.record.api.order.IOrderSchedulerService;
import com.youming.youche.record.api.pay.IPayFeeLimitService;
import com.youming.youche.record.api.tenant.ITenantServiceRelService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.common.*;
import com.youming.youche.record.domain.tenant.TenantServiceRel;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.ApplyRecordQueryDto;
import com.youming.youche.record.dto.GetTenantsDto;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.record.dto.UserInfoDto;
import com.youming.youche.record.dto.driver.*;
import com.youming.youche.record.vo.*;
import com.youming.youche.record.vo.driver.*;
import com.youming.youche.record.vo.user.CarAuthInfoVo;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.csource.fastdfs.ClientGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 司机档案
 */
@RestController
@RequestMapping("userBo")
public class UserController extends BaseController<UserDataInfo, IUserDataInfoRecordService> {


    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final int operTypes[] = new int[]{1, 2};

    @Resource
    private HttpServletRequest request;

    @DubboReference(version = "1.0.0")
    IUserService iUserService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @DubboReference(version = "1.0.0")
    ITenantServiceRelService iTenantServiceRelService;

    @DubboReference(version = "1.0.0")
    IPayFeeLimitService iPayFeeLimitService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @Override
    public IUserDataInfoRecordService getService() {
        return iUserDataInfoRecordService;
    }


    /**
     * 查询我邀请的以及邀请我的数量
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryInviteNum")
    public ResponseResult queryInviteNum() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            DriverApplyRecordNumDto driverApplyRecordNumDto = iUserService.queryInviteNum(accessToken);
            return ResponseResult.success(driverApplyRecordNumDto);
        } catch (Exception e) {

            return ResponseResult.failure("查询异常");
        }
    }

    /***
     * 会员管理/会员(司机)档案列表查询
     * @return
     * @throws Exception
     */
    @GetMapping("doQueryDrivers")
    public ResponseResult doQueryDrivers(Integer page, Integer pageSize,
                                         DoQueryDriversVo doQueryDriversVo) {
        try {
            Map map = new HashMap();
            if (doQueryDriversVo == null) {
                doQueryDriversVo = new DoQueryDriversVo();
                doQueryDriversVo.setCarUserType(-1);
                doQueryDriversVo.setState(-1);
            } else {
                if (doQueryDriversVo.getCarUserType() == null) {
                    doQueryDriversVo.setCarUserType(-1);
                }
                if (doQueryDriversVo.getState() == null) {
                    doQueryDriversVo.setState(-1);
                }
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<DoQueryDriversDto> driversDtoPage = iUserService.doQueryCarDriver(doQueryDriversVo, page, pageSize, accessToken);
            return ResponseResult.success(driversDtoPage);
        } catch (Exception e) {
            LOGGER.error("(司机)档案列表查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }


    /***
     * 会员管理/会员(司机)档案
     * @return
     * @throws Exception
     */
    @GetMapping("export")
    public ResponseResult export(DoQueryDriversVo doQueryDriversVo) {
        try {
            if (doQueryDriversVo == null) {
                doQueryDriversVo = new DoQueryDriversVo();
                doQueryDriversVo.setCarUserType(-1);
                doQueryDriversVo.setState(-1);
            } else {
                if (doQueryDriversVo.getCarUserType() == null) {
                    doQueryDriversVo.setCarUserType(-1);
                }
                if (doQueryDriversVo.getState() == null) {
                    doQueryDriversVo.setState(-1);
                }
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("司机信息信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iUserService.doQueryCarDriverList(doQueryDriversVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("(司机)档案列表查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /***
     * 导入管理/会员(司机)档案
     * @return
     * @throws Exception
     */
    @PostMapping("driver")
    public ResponseResult driverImport(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "司机信息.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("司机档案导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iUserService.driverImport(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("(司机)档案列表查询异常" + e);
            return ResponseResult.failure("导入异常，请联系管理员处理");
        }
    }


    /***
     * 会员管理/会员(司机)档案列表查询
     * @return
     * @throws Exception
     */
    @GetMapping("doQueryOBMDrivers")
    public ResponseResult doQueryOBMDrivers(DoQueryOBMDriversVo doQueryOBMDriversVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (doQueryOBMDriversVo.getHasVer() == null) {
            doQueryOBMDriversVo.setHasVer(false);
        }
        Page<DoQueryOBMDriversVo> driversDtoPage = iUserService.doQueryOBMCarDriver(doQueryOBMDriversVo, accessToken);
        return ResponseResult.success(driversDtoPage);
    }


    /***
     * 是否能更换手机号
     * @return
     * @throws Exception
     */
    @GetMapping("isCanModifyMobile")
    public ResponseResult isCanModifyMobile(@RequestParam("userId") Long userId) {
        try {
            if (userId == null) {
                return ResponseResult.failure("司机ID不能为空");
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
            if (null == user) {
                return ResponseResult.failure("用户未登陆");
            }
            //判断是否有接过单
            if (iOrderSchedulerService.hasDriverOrder(userId)) {
                LOGGER.info(userId + "司机已经有接过单，不能修改手机号");
                return ResponseResult.failure("司机已经有接过单，不能修改手机号");
            }
            //判断是否为其他车队的自有车司机
            List<TenantUserRel> tenantUserRelList = iUserService.getAllTenantUserRels(userId);
            for (TenantUserRel tenantUserRel : tenantUserRelList) {
                //如果是其他车队的自有车不能修改
                if ((tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) && (tenantUserRel.getTenantId().intValue() != user.getTenantId().intValue())) {
                    return ResponseResult.failure("其他车队的自有车司机不能修改手机号");
                } else if ((tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) && (tenantUserRel.getTenantId().intValue() == user.getTenantId().intValue())) {
                    return ResponseResult.success("成功");
                }
            }
            return ResponseResult.success("成功");
        } catch (Exception e) {
            LOGGER.error("验证司机是否能够更换手机号异常：" + e);
            return ResponseResult.failure("验证司机是否能够更换手机号异常");
        }
    }

    /**
     * 查询账号是否存在，存在的话返回信息
     *
     * @return
     * @throws Exception
     */
    @GetMapping("isExistMobile")
    public ResponseResult isExistMobile(@RequestParam("loginAcct") String loginAcct) throws Exception {
        if (StringUtils.isEmpty(loginAcct)) {
            return ResponseResult.failure("手机号为空");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map resultMap = iUserService.isExistMobile(loginAcct, accessToken, false);
        return ResponseResult.success(resultMap);
    }

    /***
     * 未注册司机添加
     * @return
     * @throws Exception
     */
    @PostMapping("doAddDriver")
    public ResponseResult doAddDriver(@RequestBody DoAddDriverVo doAddDriverVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        //验证基础信息
        String loginAcct = doAddDriverVo.getLoginAcct();
        String linkman = doAddDriverVo.getLinkman();
        String identification = doAddDriverVo.getIdentification();
        if (StringUtils.isEmpty(loginAcct)) {
            return ResponseResult.failure("请输入司机账号");
        }
        if (StringUtils.isEmpty(identification)) {
            return ResponseResult.failure("请输入身份证号");
        }
        if (!IDCardUtil.isIDCard(identification)) {
            return ResponseResult.failure("请输入正确的身份证号");
        }
        if (StringUtils.isEmpty(linkman)) {
            return ResponseResult.failure("真实姓名不能为空");
        }
        if (StringUtils.isEmpty(loginAcct)) {
            return ResponseResult.failure("账号不能为空！");
        }
        //验证司机账号是否存在
        UserDataInfo user = iUserDataInfoRecordService.getDriver(loginAcct); //查询用户信息
        if (null != user) {
            List<TenantServiceRel> tenantUserRelList = iTenantServiceRelService.getTenantServiceRelList(user.getId(), null);
            if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                if (null != user.getUserType() && user.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {//验证账号类型
                    return ResponseResult.failure("司机账号已存在");
                }
            }
            UserDataInfo checkReuslt = iUserDataInfoRecordService.isExistIdentification(identification, user);
            //验证身份证号是否被其他司机注册
            if (null != checkReuslt) {
                return ResponseResult.failure(
                        "尾号为：" + identification.substring(identification.length() - 4) + "的身份证号已经被" + checkReuslt.getLinkman() + "(" + checkReuslt.getMobilePhone() + ")使用请重新输入");
            }
        }
        String carUserType = doAddDriverVo.getCarUserType();
        if (StringUtils.isEmpty(carUserType)) {
            return ResponseResult.failure("carUserType参数错误");
        }
        Map map = iUserService.isExistMobile(loginAcct, accessToken, false);
        int type = DataFormat.getIntKey(map, "type");
        if (type != 1 && type != 4) {
            return ResponseResult.failure("该账号异常,请重新输入！");
        }
        /**
         * 公司自有车 业务招商 归属部门
         */
        if (carUserType.equals("1") || carUserType.equals("2")) {
            Long attachedOrgId = doAddDriverVo.getAttachedOrgId();
            if (attachedOrgId == null || attachedOrgId < 0) {
                return ResponseResult.failure("归属部门不能为空");
            }
        }
        /**
         * 司机档案中录入司机工资大于15000元（配置项）时报错、司机档案中录入补贴大于200元/天（配置项）时报错、司机档案中（里程工资）录入里程模式大于 1元/公里（配置项）时报错
         * 公司自有车才有薪资信息 归属部门
         */
        if (carUserType.equals("1")) {
            Long salary = doAddDriverVo.getSalary();
            Long subsidy = doAddDriverVo.getSubsidy();
            int salaryPattern = doAddDriverVo.getSalaryPattern();//工资模式
            if (salaryPattern < 0) {
                return ResponseResult.failure("请选择薪资模式");
            }
            String message = null;
            if (salary > 0) {
                message = iPayFeeLimitService.judgePayFeeLimit(salary, EnumConsts.PAY_FEE_LIMIT.TYPE2, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE6, null, accessToken);
                if (!StringUtils.isEmpty(message)) {
                    return ResponseResult.failure(message);
                }
            }
            if (subsidy > 0) {
                message = iPayFeeLimitService.judgePayFeeLimit(subsidy, EnumConsts.PAY_FEE_LIMIT.TYPE2, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE7, null, accessToken);
                if (!StringUtils.isEmpty(message)) {
                    return ResponseResult.failure(message);
                }
            }
            //验证里程模式是否达到配置上线
            if (salaryPattern == SysStaticDataEnum.SALARY_PATTERN.MILEAGE) {//里程模式
                List<UserSalaryInfoVo> userSalaryInfoList = doAddDriverVo.getUserSalaryInfoList();
                if (userSalaryInfoList != null && userSalaryInfoList.size() > 0) {
                    for (UserSalaryInfoVo userSalaryInfo : userSalaryInfoList) {
                        String price = userSalaryInfo.getPrice();
                        if (price != null && !"".equals(price)) {
                            Double priceDouble = Double.parseDouble(price);
                            message = iPayFeeLimitService.judgePayFeeLimit(priceDouble.longValue(), EnumConsts.PAY_FEE_LIMIT.TYPE2, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE10, null, accessToken);
                            if (!StringUtils.isEmpty(message)) {
                                return ResponseResult.failure(message);
                            }
                        }
                    }
                }
            }
        }
        iUserService.doAddDriver(doAddDriverVo, accessToken);
        return ResponseResult.success("添加成功");
    }


    /***
     * 运营端司机添加
     * @return
     * @throws Exception
     */
    @PostMapping("doAddOBMDriver")
    public ResponseResult doAddOBMDriver(@RequestBody DoAddOBMDriver doAddOBMDriver) {
        // try {
        if (doAddOBMDriver == null) {
            return ResponseResult.failure("数据错误");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddOBMDriver.getLinkman())) {
            return ResponseResult.failure("真实姓名不能为空");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddOBMDriver.getLoginAcct())) {
            return ResponseResult.failure("账号不能为空");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddOBMDriver.getIdentification())) {
            return ResponseResult.failure("请输入身份证号");
        }
        if (!IDCardUtil.isIDCard(doAddOBMDriver.getIdentification())) {
            return ResponseResult.failure("请输入正确的身份证号");
        }
        Map map = iUserService.isExistMobile(doAddOBMDriver.getLoginAcct());
        int type = DataFormat.getIntKey(map, "type");
        if (type != 1 && type != 2) {
            return ResponseResult.failure("该手机号码已经注册，请重新输入！");
        }
        if (doAddOBMDriver.getUserId() == null) {
            doAddOBMDriver.setUserId(-1L);

        }
        //身份证判重
        String s = iUserService.checkExistIdentification(doAddOBMDriver.getIdentification(), doAddOBMDriver.getUserId());
        if (s != null) {
            return ResponseResult.failure(s);
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserService.doAddDriverForOBMS(doAddOBMDriver, accessToken);
        return ResponseResult.success("添加成功");

    }


    /**
     * 模糊查询车辆信息
     *
     * @param plateNumber 车牌号
     * @return 车辆信息
     */
    @GetMapping("doQueryVehicleForQuick")
    public ResponseResult doQueryVehicleForQuick(String plateNumber) {
        try {
            if (StringUtils.isEmpty(plateNumber)) {
                return ResponseResult.failure("车牌号为空");
            }
            Page page = iUserService.doQueryVehicleForQuick(plateNumber);
            return ResponseResult.success(page);
        } catch (Exception e) {
            LOGGER.error("查询异常：" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 模糊查询当前车队车辆信息
     *
     * @param plateNumber 车牌号
     * @return 车队车辆信息
     */
    @GetMapping("doQueryModernVehicleForQuick")
    public ResponseResult doQueryModernVehicleForQuick(String plateNumber) {
        try {
            if (StringUtils.isEmpty(plateNumber)) {
                return ResponseResult.failure("车牌号为空");
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page page = iUserService.doQueryModernVehicleForQuick(plateNumber, accessToken);
            return ResponseResult.success(page);
        } catch (Exception e) {
            LOGGER.error("查询异常：" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 根据手机号查询用户信息及用户车辆列表
     *
     * @param loginAcct 手机号
     * @return 用户即用户车辆列表
     */
    @GetMapping("getDriverUserInfoForQuick")
    public ResponseResult getDriverUserInfoForQuick(String loginAcct) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            DriverUserInfoQuickVo driverUserInfoQuickVo = iUserService.getDriverUserInfoForQuick(loginAcct, accessToken);
            return ResponseResult.success(driverUserInfoQuickVo);
        } catch (Exception e) {
            LOGGER.error("查询异常：" + e);
            return ResponseResult.failure("查询异常");
        }

    }

    /**
     * 查询申请详情 我邀请的
     *
     * @param applyId 申请id
     * @return 申请信息详情
     * @throws Exception
     */
    @GetMapping("selectApplyRecordVo")
    public ResponseResult selectApplyRecordVo(Long applyId) throws Exception {
        if (applyId == null) {
            return ResponseResult.failure("申请id为空");
        }
        DriverApplyRecordVo driverApplyRecordVo = iUserService.selectApplyRecordVo(applyId);
        return ResponseResult.success(driverApplyRecordVo);
    }

    /**
     * 查询申请详情 被邀请的
     *
     * @param applyId 申请id
     * @return 申请信息详情
     * @throws Exception
     */
    @GetMapping("getBeApplyRecord")
    public ResponseResult getBeApplyRecord(Long applyId) throws Exception {
        if (applyId == null) {
            throw new BusinessException("参数错误");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BeApplyRecordVo beApplyRecordVo = iUserService.getBeApplyRecord(applyId, accessToken);
        return ResponseResult.success(beApplyRecordVo);
    }

    /**
     * 快速新增车辆
     *
     * @return
     */
    @PostMapping("quickAdd")
    public ResponseResult quickAdd(@RequestBody QuickAddDriverVo quickAddDriverVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        if (StringUtils.isBlank(quickAddDriverVo.getLoginAcct())) {
            return ResponseResult.failure("请输入司机账号");
        }
        if (StringUtils.isBlank(quickAddDriverVo.getLinkman())) {
            return ResponseResult.failure("请输入司机名称");
        }
        if (StringUtils.isBlank(quickAddDriverVo.getPlateNumber())) {
            return ResponseResult.failure("请输入车牌号码");
        } else if (!isCarNo(quickAddDriverVo.getPlateNumber())) {
            return ResponseResult.failure("请输入正确车牌号码");
        }

//        if (StringUtils.isBlank(quickAddDriverVo.getQrCodeUrl())) {
//            return ResponseResult.failure("数据错误！");
//        }
        if (quickAddDriverVo.getVehicleStatus() == null) {
            quickAddDriverVo.setVehicleStatus(-1);
        }
        if (quickAddDriverVo.getVehicleLength() == null) {
            quickAddDriverVo.setVehicleLength("-1");
        }
        try {
            String message = iUserService.quick(quickAddDriverVo, accessToken);
            if (message != null) {
                return ResponseResult.failure(message);
            }
            return ResponseResult.success("快速新增成功");
        } catch (Exception e) {
            LOGGER.error("快速新增异常：" + e);
            return ResponseResult.failure("快速新增异常");
        }
    }

    /***
     * 会员管理/会员(司机)档案查询 审核查询单个司机信息
     * @return
     * @throws Exception
     */
    @GetMapping("doGetDriver")
    public ResponseResult doGetDriver(@RequestParam("shareFlg") Integer shareFlg,
                                      @RequestParam("userId") Long userId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Map resultMap = new HashMap();
            if (shareFlg == 1) {
                resultMap = iUserService.doGetDriverForOBMS(userId, accessToken);
            } else {
                resultMap = iUserService.doGetDriver(userId, accessToken);
            }
            return ResponseResult.success(resultMap);
        } catch (Exception e) {
            LOGGER.error("查询异常：" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 删除司机时展示司机在车队内所有绑定的车辆（运营）
     */
    @GetMapping("getDriverAllVehicle")
    public ResponseResult getDriverAllVehicle(@RequestParam("userId") Long userId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
            List<VehicleDataInfo> tenantUserRelList = iTenantVehicleRelService.getTenantVehicleRelByDriverUserIdObms(userId, user.getTenantId());
            return ResponseResult.success(tenantUserRelList);
        } catch (Exception e) {
            LOGGER.error("查询异常：" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 删除司机时展示司机在车队内所有绑定的车辆(车队)
     */
    @GetMapping("getDriverAllVehicleMotorcade")
    public ResponseResult getDriverAllVehicleMotorcade(@RequestParam("userId") Long userId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
            List<TenantVehicleRel> tenantUserRelList = iUserService.getDriverAllVehicle(userId, user.getTenantId());
            return ResponseResult.success(tenantUserRelList);
        } catch (Exception e) {
            LOGGER.error("查询异常：" + e);
            return ResponseResult.failure("查询异常");
        }
    }


    /***
     * 会员管理/会员(司机)移除
     */
    @PostMapping("doRemoveDriver")
    public ResponseResult doRemoveDriver(@RequestBody DoRemoveDriverDto doRemoveDriverDto) throws Exception {
        if (doRemoveDriverDto.getTenantUserRelId() != null && doRemoveDriverDto.getTenantUserRelId() < 0) {
            throw new BusinessException("用户ID不能为空！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserService.deleteDriver(doRemoveDriverDto.getTenantUserRelId(), doRemoveDriverDto.getTenantVehicleIds(), accessToken);
        return ResponseResult.success("移除成功");
    }


    /***
     * 修改司机手机号码
     */
    @PostMapping("doUpdateMobile")
    public ResponseResult doUpdateMobile(@RequestBody UpdateMobileDto updateMobileDto) {
        if (StringUtils.isEmpty(updateMobileDto.getMobilePhone())) {
            throw new BusinessException("司机账号不能为空！");
        }
        if (updateMobileDto.getOldMobilePhone() != null) {
            if (updateMobileDto.getOldMobilePhone().equals(updateMobileDto.getMobilePhone())) {
                throw new BusinessException("新旧手机号一致，无需修改");
            }
        }
        if (updateMobileDto.getRelId() == null) {
            updateMobileDto.setRelId(-1L);
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String massage = iUserService.doUpdateDriverMobile(updateMobileDto.getMobilePhone(), updateMobileDto.getOldMobilePhone(),
                updateMobileDto.getUserId(), updateMobileDto.getRelId(), accessToken);

        return ResponseResult.success(massage);
    }

    /***
     * 修改司机档案
     * 1、自有车改为其他类型
     * 2、类型不变
     */
    @PostMapping("doUpdateDriver")
    public ResponseResult doUpdateDriver(@RequestBody DoAddDriverVo doAddDriverVo) throws Exception {
        String loginAcct = doAddDriverVo.getLoginAcct();
        String carUserType = doAddDriverVo.getCarUserType();
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddDriverVo.getLinkman())) {
            throw new BusinessException("真实姓名不能为空！");
        }

        if (org.apache.commons.lang.StringUtils.isEmpty(loginAcct)) {
            throw new BusinessException("账号不能为空！");
        }

        if (StringUtils.isEmpty(carUserType)) {
            throw new BusinessException("司机类型不能为空！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddDriverVo.getIdentification())) {
            throw new BusinessException("请输入身份证号！");
        }

        if (!IDCardUtil.isIDCard(doAddDriverVo.getIdentification())) {
            throw new BusinessException("请输入正确的身份证号!");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(doAddDriverVo.getVehicleType()) && doAddDriverVo.getVehicleType().length() > 10) {
            throw new BusinessException("准驾类型不正确!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserService.doUpdateDriver(doAddDriverVo, accessToken);
        return ResponseResult.success("修改成功");
    }


    /***
     * 修改司机档案
     * 1、自有车改为其他类型
     * 2、类型不变
     */
    @PostMapping("doUpdateOBMDriver")
    public ResponseResult doUpdateOBMDriver(@RequestBody DoAddOBMDriverVo doAddDriverVo) throws Exception {
        String loginAcct = doAddDriverVo.getLoginAcct();

        if (org.apache.commons.lang.StringUtils.isEmpty(doAddDriverVo.getLinkman())) {
            throw new BusinessException("真实姓名不能为空！");
        }

        if (org.apache.commons.lang.StringUtils.isEmpty(loginAcct)) {
            throw new BusinessException("账号不能为空！");
        }
        if (doAddDriverVo.getCarUserType() == null) {
            doAddDriverVo.setCarUserType("-1");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddDriverVo.getIdentification())) {
            throw new BusinessException("请输入身份证号！");
        }

        if (!IDCardUtil.isIDCard(doAddDriverVo.getIdentification())) {
            throw new BusinessException("请输入正确的身份证号!");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(doAddDriverVo.getVehicleType()) && doAddDriverVo.getVehicleType().length() > 10) {
            throw new BusinessException("准驾类型不正确!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        DoAddDriverVo doAddDriver = new DoAddDriverVo();
        BeanUtil.copyProperties(doAddDriverVo, doAddDriver);
        iUserService.doUpdateDriver(doAddDriver, accessToken);
        return ResponseResult.success("修改成功");
    }


    /**
     * 查询我邀请的列表
     *
     * @param carUserType     申请类型
     * @param applyState      审核状态
     * @param page            分页参数
     * @param rows            分页参数
     * @param billId          司机账号
     * @param tenantName      车队名称
     * @param tenantLinkPhone 车队手机
     * @return 我邀请的列表
     */
    @GetMapping("doQueryApplyRecords")
    public ResponseResult doQueryApplyRecords(@RequestParam("carUserType") Integer carUserType,
                                              @RequestParam("applyState") Integer applyState,
                                              @RequestParam("page") Integer page,
                                              @RequestParam("rows") Integer rows,
                                              @RequestParam("billId") String billId,
                                              @RequestParam("tenantName") String tenantName,
                                              @RequestParam("tenantLinkPhone") String tenantLinkPhone) {
        try {
            ApplyRecordQueryDto applyRecordQueryDto = new ApplyRecordQueryDto();
            if (carUserType == null) {
                carUserType = -1;
            }
            if (applyState == null) {
                applyState = -1;
            }
            applyRecordQueryDto.setBillId(billId);
            applyRecordQueryDto.setApplyState(applyState);
            applyRecordQueryDto.setCarUserType(carUserType);
            applyRecordQueryDto.setTenantLinkPhone(tenantLinkPhone);
            applyRecordQueryDto.setTenantName(tenantName);
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page pageInfo = iUserService.doQueryApplyRecords(applyRecordQueryDto, page, rows, accessToken);
            return ResponseResult.success(pageInfo);
        } catch (Exception e) {
            LOGGER.error("查询我邀请的异常：" + e);
            return ResponseResult.failure("查询我邀请的异常");
        }
    }

    /**
     * 查询邀请我的列表
     *
     * @param carUserType     申请类型
     * @param applyState      审核状态
     * @param page            分页参数
     * @param rows            分页参数
     * @param billId          司机账号
     * @param tenantName      车队名称
     * @param tenantLinkPhone 车队手机
     * @return 邀请我的列表
     */
    @GetMapping("doQueryBeApplyRecords")
    public ResponseResult doQueryBeApplyRecords(@RequestParam("carUserType") Integer carUserType,
                                                @RequestParam("applyState") Integer applyState,
                                                @RequestParam("page") Integer page,
                                                @RequestParam("rows") Integer rows,
                                                @RequestParam("billId") String billId,
                                                @RequestParam("tenantName") String tenantName,
                                                @RequestParam("tenantLinkPhone") String tenantLinkPhone) {
        try {
            if (carUserType == null) {
                carUserType = -1;
            }
            if (applyState == null) {
                applyState = -1;
            }
            ApplyRecordQueryDto applyRecordQueryDto = new ApplyRecordQueryDto();
            applyRecordQueryDto.setBillId(billId);
            applyRecordQueryDto.setApplyState(applyState);
            applyRecordQueryDto.setCarUserType(carUserType);
            applyRecordQueryDto.setTenantLinkPhone(tenantLinkPhone);
            applyRecordQueryDto.setTenantName(tenantName);
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page pageInfo = iUserService.doQueryBeApplyRecords(applyRecordQueryDto, page, rows, accessToken);
            return ResponseResult.success(pageInfo);
        } catch (Exception e) {
            LOGGER.error("查询邀请我的异常：" + e);
            return ResponseResult.failure("查询邀请我的异常");
        }
    }


    /**
     * 司机档案 -- 历史档案
     *
     * @param carUserType            司机类型
     * @param linkman                司机名称
     * @param page                   分页参数
     * @param rows                   分页参数
     * @param loginAcct              司机账号
     * @param attachTenantName       归属车队
     * @param attachTennantLinkman   车队联系人
     * @param attachTennantLinkPhone 车队电话
     * @return 司机历史信息查询
     */
    @GetMapping("doQueryDriverHis")
    public ResponseResult doQueryDriverHis(@RequestParam("carUserType") Integer carUserType,
                                           @RequestParam("linkman") String linkman,
                                           @RequestParam("page") Integer page,
                                           @RequestParam("rows") Integer rows,
                                           @RequestParam("loginAcct") String loginAcct,
                                           @RequestParam("attachTenantName") String attachTenantName,
                                           @RequestParam("attachTennantLinkman") String attachTennantLinkman,
                                           @RequestParam("attachTennantLinkPhone") String attachTennantLinkPhone) {
        try {
            if (carUserType == null) {
                carUserType = -1;
            }
            DriverQueryDto driverQueryDto = new DriverQueryDto();
            driverQueryDto.setAttachTenantName(attachTenantName);
            driverQueryDto.setLinkman(linkman);
            driverQueryDto.setCarUserType(carUserType);
            driverQueryDto.setLoginAcct(loginAcct);
            driverQueryDto.setAttachTennantLinkman(attachTennantLinkman);
            driverQueryDto.setAttachTennantLinkPhone(attachTennantLinkPhone);
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page pageInfo = iUserService.doQueryDriverHis(driverQueryDto, page, rows, accessToken);
            return ResponseResult.success(pageInfo);
        } catch (Exception e) {
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 司机档案 邀请 -- 再次邀请
     *
     * @param doAddApplyAgainVo 邀请信息
     * @return
     */
    @PostMapping("doAddApplyAgain")
    public ResponseResult doAddApplyAgain(@RequestBody DoAddApplyAgainVo doAddApplyAgainVo) throws Exception {
        if (doAddApplyAgainVo == null) {
            return ResponseResult.failure("参数为空");
        }
        if (doAddApplyAgainVo.getApplyId() == null) {
            throw new BusinessException("申请ID不能为空！");
        }
        if (StringUtils.isEmpty(doAddApplyAgainVo.getApplyRemark())) {
            throw new BusinessException("申请说明不能为空！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String massage = iUserService.doAddApplyAgain(doAddApplyAgainVo, accessToken);
        if (massage != null) {
            return ResponseResult.failure(massage);
        }
        return ResponseResult.success();
    }

    /**
     * 添加司机邀请
     */
    @PostMapping("doAddApply")
    public ResponseResult doAddApply(@RequestBody DoAddApplyVo doAddApplyVo) throws Exception {
        try {
            if (doAddApplyVo == null) {
                return ResponseResult.failure("参数为空");
            }
            if (doAddApplyVo.getCarUserType() == null) {
                return ResponseResult.failure("用户类型不能为空");
            }
            if (doAddApplyVo.getBusiId() == null) {
                return ResponseResult.failure("用户ID不能为空！");
            }
            if (doAddApplyVo.getCarUserType() != 3 && StringUtils.isEmpty(doAddApplyVo.getApplyRemark())) {
                return ResponseResult.failure("申请说明不能为空！");
            }
            if (doAddApplyVo.getTenantId() == null) {
                doAddApplyVo.setTenantId(-1L);
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            String massage = iUserService.doAddApply(doAddApplyVo, accessToken);
            if (massage != null) {
                return ResponseResult.failure(massage);
            }
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }


    /**
     * 审核司机
     *
     * @return
     * @throws Exception
     */
    @PostMapping("doAudit")
    public ResponseResult doAudit(@RequestBody DriverAuditVo driverAuditVo) {
        try {
            if (driverAuditVo == null) {
                return ResponseResult.failure("参数错误");
            }
            if (AuditConsts.RESULT.FAIL == driverAuditVo.getChooseResult() && StringUtils.isBlank(driverAuditVo.getDesc())) {
                return ResponseResult.failure("审核不通过，审核原因需要填写");
            }
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            iUserService.doAuditForOBMS(driverAuditVo, token);
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    /**
     * 审核被邀请的
     *
     * @return
     * @throws Exception
     */
    @PostMapping("doAuditApply")
    public ResponseResult doAuditApply(@RequestBody AuditApplyVo auditApplyVo) throws Exception {
        if (auditApplyVo == null) {
            throw new BusinessException("参数错误");
        }
        if (AuditConsts.RESULT.FAIL == auditApplyVo.getChooseResult() && org.apache.commons.lang.StringUtils.isBlank(auditApplyVo.getDesc())) {
            throw new BusinessException("审核不通过，审核原因需要填写");
        }
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        iUserService.doAuditApply(auditApplyVo, token);
        return ResponseResult.success();
    }

    public static boolean isCarNo(String carNo) {
        Pattern p = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(?:(?![A-Z]{4})[A-Z0-9]){4}[A-Z0-9挂学警港澳]{1}$");
        Matcher m = p.matcher(carNo);
        if (!m.matches()) {
            return false;
        }
        return true;
    }

    //获取流文件
    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 10068 修改员工与收款人信息
     *
     * @param userId       - 用户编号
     * @param userName     - 用户名称
     * @param receiverName - 收款人名称
     * @param idCard       - 身份证号
     */
    @GetMapping("updateUserDataInfo")
    public ResponseResult updateUserDataInfo(UpdateUserDataInfoVo updateUserDataInfoVo) {
        iUserDataInfoRecordService.updateUserDataInfo(updateUserDataInfoVo);
        return ResponseResult.success();
    }

    /**
     * 接口编号 10031
     * <p>
     * 接口入参：
     * <p>
     * 接口出参：
     * isMultiTenantUser    是否有多个租户  true 进入 false 不进入
     * tenantList           isMultiTenantUser为true的情况下，返回所有的车队信息 key为车队id  value为车队名称
     * 小程序获取租户列表
     */
    @GetMapping("getTenants")
    public ResponseResult getTenants() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        GetTenantsDto tenants = iUserDataInfoRecordService.getTenants(accessToken);
        return ResponseResult.success(tenants);
    }

    /**
     * 10022 检查手机号码
     *
     * @param billId  手机号码
     * @param appCode 小程序区分：1 服务商  2 驻场
     * @return 0正常 1 手机号码错误  2 不是有效员工  待扩展
     */
    @GetMapping("checkMobilePhoneForWx")
    public ResponseResult checkMobilePhoneForWx(String billId, Integer appCode) {

        return ResponseResult.success(
                iUserDataInfoRecordService.checkMobilePhoneForWx(billId, appCode)
        );

    }

    /**
     * 10011 修改支付密码
     *
     * @param userId 用户编号
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     */
    @PostMapping("modifyPayPwd")
    public ResponseResult modifyPayPwd(Long userId, String oldPwd, String newPwd, Integer operType) {
        if (userId <= 0) {
            throw new BusinessException("用户编号不能为空!");
        }

        if (StringUtils.isEmpty(newPwd)) {
            throw new BusinessException("新支付密码不能为空!");
        }
//        //解密
//        newPwd = EncryPwd.pwdDecryp(newPwd);
//        if (StringUtils.isEmpty(newPwd)) {
//            throw new BusinessException("新支付密码不能为空!");
//        }
        //限制密码只能输入数字
        if (!CommonUtil.isInteger(newPwd)) {
            throw new BusinessException("支付密码只支持数字");
        }
        if (newPwd.length() > 6 || Integer.parseInt(newPwd) < 0) {
            throw new BusinessException("支付密码必须是6位数字");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserDataInfoRecordService.modifyPayPwd(userId, oldPwd, newPwd, operType, accessToken);
        return ResponseResult.success();
    }

    /**
     * 10015 找回支付密码接口
     *
     * @param billId       用户手机
     * @param userId       用户编号
     * @param captcha      验证码
     * @param loginPasswd  登录密码
     * @param newPayPasswd 新支付密码 （需加密）
     */
    @PostMapping("payPasswdReset")
    public ResponseResult payPasswdReset(Long userId, String newPayPasswd, String captcha,
                                         String billId, String loginPasswd, int channel) {
        if (userId == null) {
            throw new BusinessException("用户编号不能为空!");
        }

        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("手机号不能为空!");
        }

        if (StringUtils.isEmpty(captcha)) {
            throw new BusinessException("短信验证码不能为空!");
        }

//        captcha = EncryPwd.pwdDecryp(captcha);
//        if (StringUtils.isEmpty(captcha)) {
//            throw new BusinessException("短信验证码不能为空!");
//        }

        if (!CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("请输入正确的手机号码!");
        }

//        //解密
//        newPayPasswd = EncryPwd.pwdDecryp(newPayPasswd);
        if (StringUtils.isEmpty(newPayPasswd)) {
            throw new BusinessException("支付密码不能为空!");
        }

        //限制密码只能输入数字
        if (!CommonUtil.isInteger(newPayPasswd)) {
            throw new BusinessException("支付密码只支持数字");
        }
        if (newPayPasswd.length() > 6 || Integer.parseInt(newPayPasswd) < 0) {
            throw new BusinessException("支付密码必须是6位数字");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserDataInfoRecordService.payPasswdReset(userId, newPayPasswd, captcha, billId, loginPasswd, channel, accessToken);
        return ResponseResult.success();
    }

    /**
     * 10010 修改登录手机号码
     *
     * @param userId     用户编号
     * @param billId     新手机号码
     * @param identiCode 验证码      base 加密
     * @param loginPwd   旧登录密码  base 加密
     */
    @PostMapping("modifyLoginPhone")
    public ResponseResult modifyLoginPhone(String billId, Long userId, String identiCode, String loginPwd) {
        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("请输入需要修改的手机号码！");
        }
        if (!CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("输入正确的手机号码!");
        }
        if (StringUtils.isEmpty(identiCode)) {
            throw new BusinessException("新手机的短信验证码不为能空！");
        }
        if (StringUtils.isEmpty(loginPwd)) {
            throw new BusinessException("请输入登录密码！");
        }
        if (userId <= 0) {
            throw new BusinessException("请输入用户编号！");
        }

        //解密
        identiCode = EncryPwd.pwdDecryp(identiCode);
        if (StringUtils.isEmpty(identiCode)) {
            throw new BusinessException("新手机的短信验证码不为能空！");
        }

        boolean istrue = true/*CommonUtils.checkCode(billId, identiCode, EnumConsts.RemoteCache.UPDATE_PHONENUMBER)*/;
        if (!istrue) {
            throw new BusinessException("短信验证码错误!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserDataInfoRecordService.modifyLoginPhone(billId, userId, identiCode, loginPwd, accessToken);
        return ResponseResult.success();
    }

    /**
     * 10008 设置/修改 登录密码接口
     *
     * @param billId      用户手机
     * @param oldPassword 旧密码 （修改密码必传）Base64加密
     * @param newPassword 新密码 （需加密）
     * @param operType    1-设置登录密码，2-修改登录密码
     */
    @PostMapping("loginPasswdMng")
    public ResponseResult loginPasswdMng(Integer operType, String billId, String newPassword, String oldPassword) {

        if (!CommonUtils.isContains(operTypes, operType)) {
            throw new BusinessException("输入操作类型错误！");
        }

        if (!CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("请输入正确的手机号码!");
        }

        if (StringUtils.isEmpty(newPassword)) {
            if (operType == 1) {
                throw new BusinessException("新密码不能为空!");
            } else {
                throw new BusinessException("密码不能为空!");
            }
        }

        //解密
        newPassword = EncryPwd.pwdDecryp(newPassword);

        if (!CommonUtils.isCheckPwd(newPassword)) {
            throw new BusinessException("密码长度6～16个字符，字母区分大小写!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iUserDataInfoRecordService.loginPasswdMng(operType, billId, newPassword, oldPassword, accessToken);
        return ResponseResult.success();

    }

    /**
     * 10009 忘记登录密码接口
     *
     * @param billId      用户手机
     * @param captcha     验证码
     * @param newPassword 新密码 （需加密）
     */
    @PostMapping("loginPasswdReset")
    public ResponseResult loginPasswdReset(String billId, String captcha, String newPassword) {
        if (!CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("请输入正确的手机号码!");
        }

        //解密 todo
        boolean bl = true/*CommonUtils.checkCode(billId, captcha, EnumConsts.RemoteCache.VALIDKEY_CODE_RESET)*/;

        if (!bl) {
            throw new BusinessException("输入的手机验证码错误！");
        }


        if (StringUtils.isEmpty(newPassword)) {
            throw new BusinessException("密码不能为空!");
        }

        //解密
        newPassword = EncryPwd.pwdDecryp(newPassword);
        if (!CommonUtils.isCheckPwd(newPassword)) {
            throw new BusinessException("密码长度6～16个字符，字母区分大小写!");
        }
        iUserDataInfoRecordService.loginPasswdReset(billId, captcha, newPassword);
        return ResponseResult.success();
    }

    /**
     * 14002 查询司机邀请列表 小程序
     * mobilePhone	       手机号码
     * tenantName          车队名称
     * carUserTypes        邀请种类，多个类型逗号隔开
     * states              状态,多个逗号隔开
     * <p>
     * 接口出参：
     * id                               邀请主键
     * mobilePhone                      邀请账号
     * applyCarUserTypeName	          邀请种类
     * applyTenantName	              申请车队
     * createDate	                      申请时间	 	格式：2015-05-06
     * state                            状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * stateName                        状态中文
     * auditFlg                         审核标志  1可以审核  0 不可审核
     */
    @GetMapping("getBeApplyRecords")
    public ResponseResult getBeApplyRecords(ApplyRecordQueryVo vo,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        vo.setApplyState(-1);
        vo.setCarUserType(-1);

        if (StringUtils.isEmpty(vo.getStates())) {
            vo.setStates("0,1,2");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page page = iUserService.doQueryBeApplyRecordsNew(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(page);
    }


    /**
     * 11004 APP获取系统参数数据（批量获取、单个获取）
     * 接口入参：
     * cfgName 系统数据名称 （可以不传（不传查所有的APP控制参数）入参为数组（可以传空数组（查所有的APP控制参数），单个、多个） 字段数据值为数据）
     * 接口出参：
     * 具体出参格式看报文
     * cfgValue	系统参数值
     * cfgName	 系统参数名
     * cfgRemark	系统备注
     */
    @GetMapping("getSysCfgInfo")
    public ResponseResult getSysCfgInfo(CfgVO cfgVO) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysCfg> cfg = iUserService.getSysCfgInfo(cfgVO, accessToken);
        return ResponseResult.success(cfg);
    }

    /**
     * 14006 查询申请详情
     * 接口入参：
     *        id	         邀请编号
     * 接口出参：
     *       id  主键id，处理邀请时回调用
     *       tenantName	          申请车队
     *       tennantLinkPhone	      车队电话
     *       createDate	         申请时间	 	格式：2015-05-06
     *       mobilePhone         邀请账号
     *       applyCarUserTypeName       申请类型
     *       applyRemark         申请说明
     *       applyFileUrl            申请附件,小图
     *       state              状态0 : 处理中  1 : 接受邀请 2：驳回邀请 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过  处理中的需要可以审核
     *       stateName              状态中文
     *       auditRemark        理由
     *       applyDriverPhone    司机手机
     *       applyDriverName    原有车辆司机
     *       vehicles[           车辆列表
     *                  plateNumber   车牌
     *                  vehicleTypeString   车辆类型
     *                  checked      查看详情的时候该车是否已经被选中
     *              ]
     */
    @PostMapping("getBeApplyRecordWx")
    public ResponseResult getBeApplyRecordWx(Long id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BeApplyRecordVo beApplyRecordWx = iUserService.getBeApplyRecordWx(id, accessToken);
        return ResponseResult.success(beApplyRecordWx);
    }

    /**
     * 14003 司机列表查询
     *
     * 入参：
     *      linkman 姓名
     *      plateNumber 车牌号码
     *      loginAcct 手机号码
     * 出参：loginAcct 手机号码
     *      linkman 姓名
     *      carUserType 司机类型
     *      carUserTypeName 司机类型名称
     *      userId 用户编号
     */
    @PostMapping("doQueryCarDrivers")
    public ResponseResult doQueryCarDrivers(String linkman, String plateNumber, String loginAcct, Long userId,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<DoQueryDriversDto> page = iUserService.doQueryCarDrivers(plateNumber, SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, linkman, loginAcct, pageNum, pageSize, accessToken, userId);
        return ResponseResult.success(page);
    }

    /**
     * 14001 审核司机
     * 接口入参：
     *        busiId  主键id
     *        chooseResult   1审核通过  2审核不通过
     *        desc            审核备注
     *        plateNumbers    转移车辆，多个车用逗号隔开
     *        driverUserId    转移接受司机
     */
    @PostMapping("auditApplyRecord")
    public ResponseResult auditApplyRecord(Long busiId, String desc, String driverUserId, Integer chooseResult, String plateNumbers) {
        if (busiId <= 0) {
            throw new BusinessException("主键id不能为空");
        }
        if (chooseResult <= 0) {
            throw new BusinessException("审核结果不能为空");

        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        iUserService.doAudit(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY, busiId, desc, chooseResult, driverUserId, plateNumbers, accessToken);
        return ResponseResult.success();
    }

    /**
     * 10018 ( 废弃 ) 小程序自动登录
     * <p>
     * 接口入参：
     *         platformType               小程序区分：1 服务商  2 驻场 3 司机
     * 接口出参：
     *         info                 微信登录标志  1登录成功  2 未绑定OpenId
     *         userType             用户类型
     *         tenantId             租户编号，只有一个租户的情况下返回
     *         isMultiTenantUser    是否需要进入车队选择页面  true 进入 false 不进入
     *         tenantList           isMultiTenantUser为true的情况下，返回所有的车队信息 key为车队id  value为车队名称
     *         tokenId              登录tokenId
     *
     */
    @PostMapping("loginIn")
    public ResponseResult loginIn(Integer platformType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        UserInfoDto userInfoDto = iUserService.loginIn(platformType, accessToken);
        return ResponseResult.success(userInfoDto);
    }

    /**
     * 判断短信验证码与登录密码是否正确 10023
     *
     * @param userId      用户编号
     * @param billId      手机号
     * @param captcha     验证码 （需加密（base64））
     * @param loginPasswd 登录密码 （需加密（base64））
     * @param channel     渠道 1-web，2-app，3-小程序
     */
    @PostMapping("checkCaptchaAndLoginPasswd")
    public ResponseResult checkCaptchaAndLoginPasswd(Long userId, String billId, String captcha, String loginPasswd, Integer channel) {
        if (userId == null) {
            throw new BusinessException("用户编号不能为空!");
        }

        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("手机号不能为空!");
        }

        if (StringUtils.isEmpty(captcha)) {
            throw new BusinessException("短信验证码不能为空!");
        }

        captcha = EncryPwd.pwdDecryp(captcha);
        if (StringUtils.isEmpty(captcha)) {
            throw new BusinessException("短信验证码不能为空!");
        }
        iUserDataInfoService.checkCaptchaAndLoginPasswd(userId, billId, captcha, loginPasswd, channel);
        return ResponseResult.success();
    }

    /**
     * 10061 小程序接口，根据跟单人员，模糊匹配跟单人员手机号
     * <p>
     * 入参：
     * <ul>
     *     <li>loginAcct        账号（手机号）     </li>
     * 	   <li>linkman          姓名              </li>
     * 	   <li>employeeNumber   员工工号          </li>
     * 	   <li>staffPosition    职位              </li>
     * 	   <li>lockFlag         状态  1启用，2禁用 </li>
     * 	   <li>orgId            员工关联的部门</li>
     * 出参：
     *        linkman        姓名
     *        identification 身份证号码
     *        loginAcct      手机号
     *        employeeNumber 员工工号
     *        staffPosition  职位
     *        tenantName     归属车队名称
     *        adminUserMobile 超管手机号
     */
    @GetMapping("getUserInfoByUserName")
    public ResponseResult getUserInfoByUserName(StaffDataInfoVo vo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<StaffDataInfoDto> userInfoByUserName = iUserDataInfoRecordService.getUserInfoByUserName(vo, accessToken);

        return ResponseResult.success(userInfoByUserName);

    }

    /**
     * 10070 修改司机路哥运输协议
     *
     * @param userId           用户id
     * @param lugeAgreement    路歌运输协议
     * @param lugeAgreementUrl 路歌运输协议url
     */
    @PostMapping("updateLugeAgreement")
    public ResponseResult updateLugeAgreement(Long userId, Long lugeAgreement, String lugeAgreementUrl) {
        if (userId < 0) {
            throw new BusinessException("参数错误");
        }
        if (org.apache.commons.lang.StringUtils.isBlank(lugeAgreementUrl)) {
            throw new BusinessException("参数错误");
        }
        String trackerHost = ClientGlobal.getG_secret_key() + "/";
        lugeAgreementUrl = lugeAgreementUrl.replace(trackerHost, "");

        iUserDataInfoService.updateLugeAgreement(userId, lugeAgreement, lugeAgreementUrl);

        return ResponseResult.success();
    }

    /**
     * 我的认证资料查询接口 10013
     *
     * @param userId 用户编号
     */
    @GetMapping("queryAuthInfo")
    public ResponseResult queryAuthInfo(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("认证用户编号不能为空！");
        }

        return ResponseResult.success(
                iUserDataInfoRecordService.queryAuthInfo(userId)
        );
    }

    /**
     * 10014 我的认证资料保存接口
     *
     * @param vo 用户信息
     */
    @PostMapping("saveCarAuthInfo")
    public ResponseResult saveCarAuthInfo(@RequestBody CarAuthInfoVo vo) {
        if (vo.getUserId() == null || vo.getUserId() <= 0) {
            throw new BusinessException("认证用户编号不能为空！");
        }
        if (StringUtils.isEmpty(vo.getUserName())) {
            throw new BusinessException("认证用户名不能为空！");
        }
        if (StringUtils.isEmpty(vo.getIdentification())) {
            throw new BusinessException("身份证不能为空！");
        }
        if (StringUtils.isEmpty(vo.getAdriverLicenseSn())) {
            throw new BusinessException("驾驶证不能为空！");
        }
        if (StringUtils.isEmpty(vo.getVehicleType())) {
            throw new BusinessException("准驾类型不能为空！");
        }
        if (vo.getIdenPictureFront() == null || vo.getIdenPictureFront() <= 0) {
            throw new BusinessException("身份证正面图片不能为空！");
        }
        if (StringUtils.isEmpty(vo.getIdenPictureFrontUrl())) {
            throw new BusinessException("身份证正面图片不能为空！");
        }
        if (vo.getIdenPictureBack() == null || vo.getIdenPictureBack() <= 0) {
            throw new BusinessException("身份证背面图片不能为空！");
        }
        if (StringUtils.isEmpty(vo.getIdenPictureBackUrl())) {
            throw new BusinessException("身份证背面图片不能为空！");
        }
        if (vo.getAdriverLicenseOriginal() == null || vo.getAdriverLicenseOriginal() <= 0) {
            throw new BusinessException("驾驶证正本图片不能为空！");
        }
        if (StringUtils.isEmpty(vo.getAdriverLicenseOriginalUrl())) {
            throw new BusinessException("驾驶证正本图片不能为空！");
        }
        if (vo.getAdriverLicenseDuplicate() == null || vo.getAdriverLicenseDuplicate() <= 0) {
            throw new BusinessException("驾驶证副本图片不能为空！");
        }
        if (StringUtils.isEmpty(vo.getAdriverLicenseDuplicateUrl())) {
            throw new BusinessException("驾驶证副本图片不能为空！");
        }

        if (vo.getQcCerti() == null || vo.getQcCerti() <= 0) {
            throw new BusinessException("从业资格证副本图片不能为空！");
        }
        if (StringUtils.isEmpty(vo.getQcCertiUrl())) {
            throw new BusinessException("从业资格证副本图片不能为空！");
        }

        if (!IDCardUtil.isIDCard(vo.getIdentification())) {
            throw new BusinessException("请输入正确的身份证号!");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        iUserDataInfoRecordService.saveCarAuthInfo(vo, accessToken);
        return ResponseResult.success();
    }


    /**
     * 查询司机订单(30074)
     *
     * @param userId 用户编号
     */
    @GetMapping("/queryDriverOrder")
    public ResponseResult queryDriverOrder(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空！");
        }
        boolean b = iOrderSchedulerService.hasDriverOrder(userId);
        return ResponseResult.success(b);
    }

}
