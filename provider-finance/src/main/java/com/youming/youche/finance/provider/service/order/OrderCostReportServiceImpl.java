package com.youming.youche.finance.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.order.IOrderCostDetailReportService;
import com.youming.youche.finance.api.order.IOrderCostOtherReportService;
import com.youming.youche.finance.api.order.IOrderCostReportService;
import com.youming.youche.finance.api.order.IOrderMainReportService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.constant.PayOutIntfUtil;
import com.youming.youche.finance.domain.order.OrderCostDetailReport;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.finance.domain.order.OrderCostReport;
import com.youming.youche.finance.domain.order.OrderMainReport;
import com.youming.youche.finance.domain.sys.SysAttach;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.order.OrderCostReportDto;
import com.youming.youche.finance.dto.order.OrderMainReportDto;
import com.youming.youche.finance.provider.mapper.SysAttachMapper;
import com.youming.youche.finance.provider.mapper.order.OrderCostDetailReportMapper;
import com.youming.youche.finance.provider.mapper.order.OrderCostOtherReportMapper;
import com.youming.youche.finance.provider.mapper.order.OrderCostReportMapper;
import com.youming.youche.finance.provider.mapper.order.OrderMainReportMapper;
import com.youming.youche.finance.vo.order.OrderCostReportVo;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderStateTrackOperService;
import com.youming.youche.order.api.order.IOrderVehicleTimeNodeService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOpRecord;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_COST_REPORT;
import static com.youming.youche.conts.SysStaticDataEnum.IS_AUTH.IS_AUTH0;
import static com.youming.youche.conts.SysStaticDataEnum.IS_AUTH.IS_AUTH1;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
@DubboService(version = "1.0.0")
public class OrderCostReportServiceImpl extends BaseServiceImpl<OrderCostReportMapper, OrderCostReport> implements IOrderCostReportService {
    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    OrderMainReportMapper orderMainReportMapper;

    @Resource
    IOrderMainReportService iOrderMainReportServicel;

    @Resource
    IOrderCostDetailReportService iOrderCostDetailReportService;


    @Resource
    IOrderCostOtherReportService iOrderCostOtherReportService;

    @Resource
    OrderCostDetailReportMapper orderCostDetailReportMapper;

    @Resource
    OrderCostOtherReportMapper orderCostOtherReportMapper;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService iOrderSchedulerHService;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;

    @DubboReference(version = "1.0.0")
    IOrderInfoService OrderInfoService;

    @DubboReference(version = "1.0.0")
    IOrderInfoHService iOrderInfoHService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    SysAttachMapper sysAttachMapper;

    @DubboReference(version = "1.0.0")
    IOrderStateTrackOperService iOrderStateTrackOperService;

    @DubboReference(version = "1.0.0")
    IOrderVehicleTimeNodeService iOrderVehicleTimeNodeService;


    @DubboReference(version = "1.0.0")
    com.youming.youche.order.api.order.IBusiSubjectsRelService iBusiSubjectsRelService;


    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;

    @DubboReference(version = "1.0.0")
    IOilCardManagementService iOilCardManagementService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;


    /**
     * 接口说明 获取费用上报列表
     * 聂杰伟
     * 2022-3-8
     *
     * @return
     */
    @Override
    public IPage<OrderCostReportDto> selectReport(String orderId, String plateNumber, String userName,
                                                  String linkPhone, String startTime, String endTime,
                                                  String subUserName, Integer state, Integer sourceRegion,
                                                  Integer desRegion, Boolean waitDeal,
                                                  String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);//当前用户信息
        List<Long> lids = new ArrayList<>();
        if (waitDeal) {
            //TODO 审核流程
            lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_COST_REPORT, loginInfo.getUserInfoId(), loginInfo.getTenantId());
        }
        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if(startTime != null && StringUtils.isNotEmpty(startTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime+" 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if(endTime != null && StringUtils.isNotEmpty(endTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime+" 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        Page<OrderCostReportDto> page =new Page<>(pageNum,pageSize);
        IPage<OrderCostReportDto> selectReport = baseMapper.selectReport(orderId, plateNumber, userName, linkPhone,
                startTime1, endTime1, subUserName, state, sourceRegion, desRegion, loginInfo.getTenantId(), lids,waitDeal,page);
        List<Long> busiIds = new ArrayList<>();
        Map<Long, Boolean> hasPermissionMap = null;
        for (OrderCostReportDto dto :selectReport.getRecords()) {
            busiIds.add(dto.getId());
        }
            for (OrderCostReportDto dto:selectReport.getRecords()) {
                busiIds.add(dto.getId());
                dto.setStateName(dto.getState()==null? "": getSysStaticData("ORDER_COST_REPORT_STATE",
                        String.valueOf(dto.getState())).getCodeName());
                if (dto.getSourceRegion()>-1){
                    dto.setSourceRegionName(getSysStaticData("SYS_CITY", dto.getSourceRegion() + "").getCodeName());
                }
                if (dto.getDesRegion()>-1){ // 到达市
                    dto.setDesRegionName(getSysStaticData("SYS_CITY", dto.getDesRegion() + "").getCodeName());
                }
                if(!busiIds.isEmpty()) {
                    //判断当前的用户对传入的业务主键是否有审核权限
                    hasPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.ORDER_COST_REPORT, busiIds);
                    long id = dto.getId();
                    Boolean flg = hasPermissionMap.get(id);
                    dto.setIsAudit(flg ? IS_AUTH1:IS_AUTH0);
                }
                VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(dto.getPlateNumber());
                if (vehicleDataInfo != null) {
                    TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId());
                    if (tenantVehicleRel != null && tenantVehicleRel.getVehicleClass() != null) {
                        dto.setVehicleCode(vehicleDataInfo.getId());
                        dto.setVehicleClass(tenantVehicleRel.getVehicleClass());
                    }
                }
            }

            return selectReport;
    }

    //费用审核
    @Transactional
    @Override
    public void auditOrderCostReport(String busiCode, Long busiId, String desc, Integer chooseResult,
                                     String loadMileage, String capacityLoadMileage, String accessToken) {

        if (chooseResult != AuditConsts.RESULT.SUCCESS) {
            loadMileage = null;
            capacityLoadMileage = null;
        }
        //处理上报费用记录
        this.dealExamineOrderCostReport(busiId, desc, StringUtils.isNotEmpty(loadMileage) ? (long) (Double.valueOf(loadMileage) * 1000) : null,
                StringUtils.isNotBlank(capacityLoadMileage) ? (long) (Double.valueOf(capacityLoadMileage) * 1000) : null);
        //TODO 调用审核流程
        this.auditOrderCostReportS(busiCode, busiId, desc, chooseResult,accessToken);

    }

    /**
     * 处理费用上报审核记录
     *
     * @param id
     * @param auditRemark
     * @throws Exception
     */
    @Override
    public void dealExamineOrderCostReport(Long id, String auditRemark, Long loadMileage, Long capacityLoadMileage) {
        if (id == null || id <= 0) {
            throw new BusinessException("请输入费用上报记录id");
        }

        QueryWrapper<OrderMainReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        OrderMainReport report = orderMainReportMapper.selectOne(queryWrapper);
        if (report == null) {
            throw new BusinessException("根据费用上报记录id：" + id + " 未找到上报费用记录信息");
        }
        report.setState(OrderConsts.ORDER_COST_REPORT.AUDIT);
        report.setAuditRemark(auditRemark);
        iOrderMainReportServicel.saveOrUpdate(report);
        QueryWrapper<OrderCostDetailReport> queryWrapper1 = new QueryWrapper<>();
        //查找费用上报明细
        queryWrapper.eq("order_id", report.getOrderId());
        List<OrderCostDetailReport> orderCostDetailReports = orderCostDetailReportMapper.selectList(queryWrapper1);

        QueryWrapper<OrderCostReport> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", report.getOrderId());
        List<OrderCostReport> orderCostReports = baseMapper.selectList(wrapper);
        if (orderCostReports != null && orderCostReports.size() > 0) {
            for (OrderCostDetailReport report1 : orderCostDetailReports) {
                if (report1.getState() != null && report1.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && report1.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    report1.setState(OrderConsts.ORDER_COST_REPORT.AUDIT);
                    report1.setAuditRemark(auditRemark);
                    iOrderCostDetailReportService.saveOrUpdate(report1);
                }
                //更新其他费用上报状态
                QueryWrapper<OrderCostOtherReport> reportQueryWrapper = new QueryWrapper<>();
                reportQueryWrapper.eq("REL_ID", report.getId());
                List<OrderCostOtherReport> orderCostOtherReports = orderCostOtherReportMapper.selectList(reportQueryWrapper);
                if (orderCostOtherReports != null) {
                    for (OrderCostOtherReport detail : orderCostOtherReports) {
                        if (detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                            detail.setState(OrderConsts.ORDER_COST_REPORT.AUDIT);
                            detail.setAuditRemark(auditRemark);
                            iOrderCostOtherReportService.saveOrUpdate(detail);
                        }
                    }
                }
            }
        }

        if (orderCostReports != null && orderCostReports.size() > 0) {
            for (OrderCostReport detail : orderCostReports) {
                if (detail.getState() != null && detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    detail.setState(OrderConsts.ORDER_COST_REPORT.AUDIT);
                    detail.setAuditRemark(auditRemark);
                    if (loadMileage == null && capacityLoadMileage != null || capacityLoadMileage == null && loadMileage != null
                            || loadMileage != null && loadMileage < 0 || capacityLoadMileage != null && capacityLoadMileage < 0
                    ) {
                        throw new BusinessException("载重校准里程或空载校准里程填写不正确！");
                    }
                    detail.setLoadMileage(loadMileage != null ? loadMileage : null);
                    detail.setCapacityLoadMileage(capacityLoadMileage != null ? capacityLoadMileage : null);
                    detail.setCheckTime(DateUtil.localDateTime(new Date()));
                    this.saveOrUpdate(detail);
                }
            }
        }
    }

    /**
     * 聂杰伟
     * 2022-3-10
     * 接口说明；根据订单号查询上报信息
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderCostReportDto getOrderCostDetailReportByOrderId(String orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);;//当前用户信息
        Long order = Long.valueOf(orderId);
        if (order < 0) {
            throw new BusinessException("订单号错误！");
        }
        //TODO 通过订单号获取订单信息
        OrderInfo orderInfo = OrderInfoService.getOrder(order);
        if (orderInfo == null) {
            //获取历史订单表的数据
            try {
                OrderInfoH orderH = iOrderInfoHService.getOrderH(order);
                if(orderInfo == null){
                  orderInfo = new OrderInfo();
                }
                org.springframework.beans.BeanUtils.copyProperties(orderH,orderInfo);

            } catch (Exception e) {
                throw new BusinessException("转换异常！");
            }
        }

        Integer sourceRegion = orderInfo.getSourceRegion();//始发市
        Integer desRegion = orderInfo.getDesRegion();//到达市
        OrderCostReportDto orderCostReportDto = new OrderCostReportDto();
        if (sourceRegion != null) {
            orderCostReportDto.setDesRegionName(getSysStaticData("SYS_CITY", sourceRegion + "").getCodeName());
        }
        if (desRegion != null) {
            orderCostReportDto.setDesRegionName(getSysStaticData("SYS_CITY", desRegion + "").getCodeName());
        }
        // 订单状态
        orderCostReportDto.setOrderStateName(orderInfo.getOrderState()==null? "":getSysStaticData("ORDER_STATE",String.valueOf(orderInfo.getOrderState())).getCodeName());
        QueryWrapper<OrderMainReport> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderMainReport orderMainReport = orderMainReportMapper.selectOne(wrapper);
        if (null == orderMainReport) {
            boolean doSave = this.isDoSave(orderInfo);
            orderCostReportDto.setIsDoSave(doSave);
            return orderCostReportDto;
        }
        BeanUtil.copyProperties(orderMainReport,orderCostReportDto);
        OrderCostReportDto orderCostDetailReport = this.getOrderCostDetailReport(orderMainReport);
        orderCostDetailReport.setIsDoSave(isDoSave(orderInfo));
        orderCostDetailReport.setDesRegionName(orderCostReportDto.getDesRegionName());
        orderCostDetailReport.setDesRegionName(orderCostReportDto.getDesRegionName());
        return orderCostDetailReport;
    }

    /***
     * 根据主键id查询上报信息
     * @param orderMainReport
     * @return
     * @throws Exception
     */
    @Override
    public OrderCostReportDto getOrderCostDetailReport(OrderMainReport orderMainReport) {
        try {
            QueryWrapper<OrderCostReport> wrapper = new QueryWrapper<>();
            wrapper.eq("ORDER_ID",orderMainReport.getOrderId());
            List<OrderCostReport> orderCostReports = baseMapper.selectList(wrapper);
            if (null == orderCostReports || orderCostReports.isEmpty()){
                return null;
            }
            OrderCostReportDto orderCostReportDto = new OrderCostReportDto();
            OrderCostReport orderCostReport = orderCostReports.get(0);
            orderCostReportDto.setLoadMileage(orderCostReport.getLoadMileage());
            orderCostReportDto.setCapacityLoadMileage(orderCostReport.getCapacityLoadMileage());
            orderCostReport.setId(orderMainReport.getId());
            orderCostReportDto.setOrderCostReport(orderCostReport);
            Long startKm = orderCostReport.getStartKm();
            if(startKm > 0){
                orderCostReportDto.setStartKmStr(String.valueOf(CommonUtil.divide(startKm,100)));

            }
            Long loadingKm = orderCostReport.getLoadingKm();
            if(loadingKm > 0){
               orderCostReportDto.setLoadingKmStr(String.valueOf(CommonUtil.divide(loadingKm,100)));
            }

            Long unloadingKm = orderCostReport.getUnloadingKm();
            if(unloadingKm > 0){
                orderCostReportDto.setUnloadingKmStr(String.valueOf(CommonUtil.divide(unloadingKm,100)));
            }

            Long endKm =orderCostReport.getEndKm();
            if(endKm > 0){
                orderCostReportDto.setEndKmStr(String.valueOf(CommonUtil.divide(endKm,100)));
            }
            FastDFSHelper client = FastDFSHelper.getInstance();
            if(StringUtils.isNotBlank(orderCostReport.getStartKmUrl())){
                orderCostReportDto.setStartKmUrl(client.getHttpURL(orderCostReport.getStartKmUrl()).split("\\?")[0]);
            }else if (orderCostReport.getStartKmFile()>0){
                QueryWrapper<SysAttach> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("id",orderCostReport.getStartKmFile());
                SysAttach sysAttach = sysAttachMapper.selectOne(wrapper1);
                if (null !=sysAttach){
                    orderCostReportDto.setStartKmUrl(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            String loadingKmUrl = orderCostReport.getLoadingKmUrl();
            Long loadingKmFile = orderCostReport.getLoadingKmFile();
            if(StringUtils.isNotBlank(loadingKmUrl)){
                orderCostReportDto.setUnloadingKmUrl(client.getHttpURL(loadingKmUrl).split("\\?")[0]);
            }else if(loadingKmFile > 0){
                QueryWrapper<SysAttach> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("id",orderCostReport.getStartKmFile());
                SysAttach sysAttach = sysAttachMapper.selectOne(wrapper1);
                if(null != sysAttach){
                    orderCostReportDto.setLoadingKmUrl(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            String endKmUrl = orderCostReport.getEndKmUrl();
            Long endKmFile = orderCostReport.getEndKmFile();
            if(StringUtils.isNotBlank(endKmUrl)){
                orderCostReport.setEndKmUrl(client.getHttpURL(endKmUrl).split("\\?")[0]);
            }else if(endKmFile > 0){
                QueryWrapper<SysAttach> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("id",orderCostReport.getStartKmFile());
                SysAttach sysAttach = sysAttachMapper.selectOne(wrapper1);if(null != sysAttach){
                 orderCostReportDto.setEndKmUrl(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            Integer state = orderMainReport.getState();
            orderCostReportDto.setStateName(state==null ? "":getSysStaticData("ORDER_COST_REPORT_STATE",String.valueOf(state)).getCodeName());
            orderCostReportDto.setState(state);
            Integer isFullOil = orderCostReport.getIsFullOil();
            if(isFullOil > 0 && isFullOil==0){
            }else{
                orderCostReportDto.setIsFullOilName("未选择");
//                List<OrderCostDetailReport> orderCostDetailReportList = this.getOrderCostDetailReport(orderMainReport.getId(), false);
//                List<OrderCostDetailReport> oilCostDataList = new ArrayList<>();
//               List<OrderCostDetailReport>  etcCostDataList  = new ArrayList<>();
//
//                //油总费用
//                long oilTotalAmount = 0;
//                //etc总费用
//                long etcTotalAmount = 0;
//                if(null != orderCostDetailReportList){
//                    for (int i = 0; i < orderCostDetailReportList.size(); i++) {
//                        OrderCostDetailReport orderCostDetailReport = orderCostDetailReportList.get(i);
//                        long amount =orderCostDetailReport.getAmount();
//                        if(amount > 0){
//                            orderCostDetailReport.setAmount(amount);
//                        }
//                        Integer paymentWay =orderCostDetailReport.getPaymentWay();
//                        orderCostDetailReport.setPaymentWayName(paymentWay==null? "":getSysStaticData("REPORT_PAYMENT_WAY",String.valueOf(paymentWay)).getCodeName());
//                        Integer stateTmp = orderCostDetailReport.getState();
//                        orderCostDetailReport.setStateName(stateTmp==null?"":getSysStaticData("ORDER_COST_REPORT_STATE",String.valueOf(stateTmp)).getCodeName());
//                        if(null != orderCostDetailReport.getTableType() && orderCostDetailReport.getTableType() == OrderConsts.TABLE_TYPE.TABLE_TYPE1){
//                            oilCostDataList.add(orderCostDetailReport);
//                            if(amount > 0){
//                                oilTotalAmount += amount;
//                            }
//                        }else if(null != orderCostDetailReport.getTableType() && orderCostDetailReport.getTableType() == OrderConsts.TABLE_TYPE.TABLE_TYPE2){
//                            etcCostDataList.add(orderCostDetailReport);
//                            if(amount > 0){
//                                etcTotalAmount += amount;
//                            }
//                        }
//                        String fileUrl = orderCostDetailReport.getFileUrl();
//                        if(StringUtils.isNotBlank(fileUrl)){
//                            orderCostDetailReport.setFileUrl(client.getHttpURL(fileUrl).split("\\?")[0]);
//                        }else if(null != orderCostDetailReport.getFileId()){
//                            QueryWrapper<SysAttach> queryWrapper = new QueryWrapper<>();
//                            wrapper.eq("id",orderCostDetailReport.getFileId());
//                            SysAttach sysAttach = sysAttachMapper.selectOne(queryWrapper);
//                            if(null != sysAttach){
//                                orderCostDetailReport.setFileUrl(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
//                            }
//                        }
//                        String fileUrl1 = orderCostDetailReport.getFileUrl1();
//                        if(StringUtils.isNotBlank(fileUrl1)){
//                            orderCostDetailReport.setFileUrl1(client.getHttpURL(fileUrl1).split("\\?")[0]);
//                        }else if(null != orderCostDetailReport.getFileId1()){
//                            QueryWrapper<SysAttach> queryWrapper = new QueryWrapper<>();
//                            wrapper.eq("id",orderCostDetailReport.getFileId1());
//                            SysAttach sysAttach = sysAttachMapper.selectOne(queryWrapper);
//                            if(null != sysAttach){
//                                orderCostDetailReport.setFileUrl1(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
//                            }
//                        }
//
//                        String fileUrl2 = orderCostDetailReport.getFileUrl2();
//                        if(StringUtils.isNotBlank(fileUrl2)){
//                            orderCostDetailReport.setFileUrl2(client.getHttpURL(fileUrl2).split("\\?")[0]);
//                        }else if(null != orderCostDetailReport.getFileId2()){
//                            QueryWrapper<SysAttach> queryWrapper = new QueryWrapper<>();
//                            wrapper.eq("id",orderCostDetailReport.getFileId2());
//                            SysAttach sysAttach = sysAttachMapper.selectOne(queryWrapper);
//                            if(null != sysAttach){
//                                orderCostDetailReport.setFileUrl2(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
//                            }
//                        }
//
//                    }
//                }
            }
            //查询其他费用
            Long otherTotalAmount=0L;
            List<OrderCostOtherReport> orderCostOtherReportVOList = this.getOrderCostOtherReport(orderMainReport.getId(), false);
            List<OrderCostOtherReport> rtnOrderCostOtherReportVOList = new ArrayList<>();
            if(orderCostOtherReportVOList!=null) {
                String fileUrl=null;
                for (OrderCostOtherReport OrderCostOtherReport : orderCostOtherReportVOList) {
                    fileUrl = OrderCostOtherReport.getFileUrl1();
                    if(StringUtils.isNotBlank(fileUrl)){
                        OrderCostOtherReport.setFileUrl1( client.getHttpURL(fileUrl).split("\\?")[0]);
                    }
                    fileUrl = OrderCostOtherReport.getFileUrl2();
                    if(StringUtils.isNotBlank(fileUrl)){
                        OrderCostOtherReport.setFileUrl2( client.getHttpURL(fileUrl).split("\\?")[0]);
                    }
                    fileUrl = OrderCostOtherReport.getFileUrl3();
                    if(StringUtils.isNotBlank(fileUrl)){
                        OrderCostOtherReport.setFileUrl3( client.getHttpURL(fileUrl).split("\\?")[0]);
                    }

                    if (OrderCostOtherReport.getOilMileage() != null && OrderCostOtherReport.getOilMileage() > 0L) {
                        OrderCostOtherReport.setOilMileageStr(String.valueOf(CommonUtil.divide(OrderCostOtherReport.getOilMileage(), 1000)));
                    }
                    OrderCostOtherReport.setConsumeFeeStr(OrderCostOtherReport.getConsumeFee() != null ? String.valueOf(CommonUtil.divide(OrderCostOtherReport.getConsumeFee(), 100)) : null);
                    OrderCostOtherReport.setStateName(OrderCostOtherReport.getState()==null ?
                            "":getSysStaticData("ORDER_COST_REPORT_STATE",String.valueOf(OrderCostOtherReport.getState())).getCodeName());

                    otherTotalAmount+=OrderCostOtherReport.getConsumeFee()==null?0:OrderCostOtherReport.getConsumeFee();
                    rtnOrderCostOtherReportVOList.add(OrderCostOtherReport);
                }
            }
//                orderCostReportDto.setEtcCostDataList(etcCostDataList);
//                orderCostReportDto.setOilCostDataList(oilCostDataList);
            orderCostReportDto.setOtherTotalAmount(CommonUtil.divide(otherTotalAmount));
            orderCostReportDto.setOtherCostDataList(rtnOrderCostOtherReportVOList);
            return orderCostReportDto;
        }catch (Exception e){
         throw  new BusinessException("转换失败");
        }
    }

    /**
     * 接口说明 费用上报审核
     * 聂杰伟
     * 2022-3-10-15：13
     *
     * @param orderCostReportVo
     * @return
     */
    @Override
    public void examineOrderCostReport(OrderCostReportVo orderCostReportVo,  String accessToken) {
        if (orderCostReportVo == null) {
            throw new BusinessException("请输入审核入参");
        }
        //费用上报记录id
        Long id = orderCostReportVo.getId();
        //审核原因
        String auditRemark = orderCostReportVo.getRemark();
        if (id == null || id <= 0) {
            throw new BusinessException("请输入费用上报记录id");
        }
        this.dealExamineOrderCostReport(id, auditRemark, orderCostReportVo.getLoadMileage(), orderCostReportVo.getCapacityLoadMileage());
    }

    /**
     * 费用明细列表
     * 聂杰伟
     * @param paymentWay 支付方式  1 油卡 2 现金 3 全部
     * @param typeId 数据类型 1油费 2 路桥费
     * @param orderId 订单id
     * @param plateNumber 车牌号
     * @param carDriverMan 司机名称
     * @param cardNo 卡号
     * @param startTime  上报时间 开始
     * @param endTime 上报时间结束
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    @Override
    public PageInfo<OrderMainReportDto> queryOrderCostDetailReports(Integer paymentWay, Long typeId, Long orderId,
                                                                    String plateNumber, String carDriverMan,
                                                                    String cardNo, String startTime,
                                                                    String endTime, Integer pageNum,
                                                                    Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);;//当前用户信息
        GetVehicleExpenseDto dto = new GetVehicleExpenseDto();
        if(startTime != null && StringUtils.isNotEmpty(startTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime+" 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            dto.setBeginApplyTime1(beginApplyTime1);
        }
        if(endTime != null && StringUtils.isNotEmpty(endTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime+" 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            dto.setEndApplyTime1(endApplyTime1);
        }
//        Page<OrderMainReportDto> dtoPage = new Page<>(pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<OrderMainReportDto> collect = baseMapper.queryOrderCostDetailReports( paymentWay, typeId,
                orderId, plateNumber, carDriverMan, cardNo, dto, loginInfo.getTenantId());

        for (OrderMainReportDto dto1:collect) {
            // 加油公里数除1000
            if (dto1.getOilMileage()!=null){
                dto1.setOilMileageStr(String.valueOf(CommonUtil.divide(dto1.getOilMileage(), 1000)));
            }
           //  上报金额
            if (dto1.getConsumeFee()!=null){
                dto1.setConsumeFeeStr(dto1.getConsumeFee()==null?null:dto1.getConsumeFee()/100.00);
            }
            // 付款方式 1:油卡 2:现金
            if (dto1.getPaymentWay()!=null){
                dto1.setPaymentWayStr(dto1.getPaymentWay()==1?"油卡":"现金");
            }
        }
        PageInfo<OrderMainReportDto> customerInfoPageInfo = new PageInfo<>(collect);
        return customerInfoPageInfo;
    }

    /**
     * 明细列表导出
     * @param paymentWay
     * @param typeId
     * @param orderId
     * @param plateNumber
     * @param carDriverMan
     * @param cardNo
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @param record
     */
    @Override
    public void exportParticulars(Integer paymentWay, Long typeId, Long orderId, String plateNumber,
                                  String carDriverMan, String cardNo, String startTime, String endTime,
                                  Integer pageNum, Integer pageSize, String accessToken, ImportOrExportRecords record) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        GetVehicleExpenseDto dto = new GetVehicleExpenseDto();
        if(startTime != null && StringUtils.isNotEmpty(startTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime+" 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            dto.setBeginApplyTime1(beginApplyTime1);
        }
        if(endTime != null && StringUtils.isNotEmpty(endTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime+" 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            dto.setEndApplyTime1(endApplyTime1);
        }
        List<OrderMainReportDto> records = baseMapper.queryOrderCostDetailReports(paymentWay, typeId, orderId,
                plateNumber, carDriverMan, cardNo, dto, loginInfo.getTenantId());

        for (OrderMainReportDto dto1:records) {
            // 加油公里数除1000
            if (dto1.getOilMileage()!=null){
                dto1.setOilMileageStr(String.valueOf(CommonUtil.divide(dto1.getOilMileage(), 1000)));
            }
            //  上报金额
            if (dto1.getConsumeFee()!=null){
                dto1.setConsumeFeeStr(dto1.getConsumeFee()==null?null:dto1.getConsumeFee()/100.00);
            }
            // 付款方式 1:油卡 2:现金
            if (dto1.getPaymentWay()!=null){
                dto1.setPaymentWayStr(dto1.getPaymentWay()==1?"油卡":"现金");
            }
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            if (records != null && records.size() > 0) {
            }
            showName = new String[]{"上报时间", "消费金额",
                    "上报费用类型", "支付方式", "卡号",
                    "加油公里数" ,"订单号"};
            resourceFild = new String[]{"getSubTime", "getConsumeFeeStr",
                    "getTypeName", "getPaymentWayStr", "getCardNo",
                    "getOilMileageStr","getOrderId"
            };

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, OrderMainReportDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "上报费用明细导出.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.saveOrUpdate(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.saveOrUpdate(record);
            e.printStackTrace();
        }

    }

    @Override
    public List<OrderCostDetailReport> getOrderCostDetailReport(Long id, Boolean hasAudit) {

//        Session session = SysContexts.getEntityManager();
//        Criteria ca = session.createCriteria(OrderCostDetailReport.class);
//        ca.add(Restrictions.eq("relId", id));
        QueryWrapper<OrderCostDetailReport> wrapper = new QueryWrapper<>();
        wrapper.eq("rel_id",id);
        if(hasAudit){
//            ca.add(Restrictions.ne("state", OrderConsts.ORDER_COST_REPORT.AUDIT));
//            ca.add(Restrictions.ne("state", OrderConsts.ORDER_COST_REPORT.AUDIT_PASS));
            wrapper.ne("state",OrderConsts.ORDER_COST_REPORT.AUDIT);
            wrapper.ne("state",OrderConsts.ORDER_COST_REPORT.AUDIT_PASS);
        }else{
                // TODO 待释放
            wrapper.orderByAsc("table_type");
            wrapper.orderByAsc("id");
        }
//        List<OrderCostDetailReport> list = ca.list();
        List<OrderCostDetailReport> orderCostDetailReports = orderCostDetailReportMapper.selectList(wrapper);
        return orderCostDetailReports;

    }

    /**
     * 根据费用上报id查询其他上报费用
     * @param relId
     * @param hasAudit
     * @return
     * @throws Exception
     */
    @Override
    public List<OrderCostOtherReport> getOrderCostOtherReport(long relId, boolean hasAudit) {

        QueryWrapper<OrderCostOtherReport> wrapper = new QueryWrapper<>();
        wrapper.eq("rel_id",relId);
        if(hasAudit){
            wrapper.ne("state",OrderConsts.ORDER_COST_REPORT.AUDIT);
            wrapper.ne("state",OrderConsts.ORDER_COST_REPORT.AUDIT_PASS);
        }
        wrapper.orderByAsc("id");
        List<OrderCostOtherReport> orderCostOtherReports = orderCostOtherReportMapper.selectList(wrapper);
        return orderCostOtherReports;
    }

    /**
     * 流程结束，审核通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    @Override
    public void sucess(Long busiId, String desc, Map paramsMap,String accessToken ) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        IOrderCostReportSV orderCostReportSV = (IOrderCostReportSV) SysContexts.getBean("orderCostReportSV");
//        IOrderStateTrackOperSV orderStateTrackOperSV = (IOrderStateTrackOperSV) SysContexts.getBean("orderStateTrackOperSV");
//        OrderVehicleTimeNodeTF orderVehicleTimeNodeTF = (OrderVehicleTimeNodeTF) SysContexts.getBean("orderVehicleTimeNodeTF");
//        IOrderOpRecordSV orderOpRecordSV = (IOrderOpRecordSV)SysContexts.getBean("orderOpRecordSV");
        //long id = DataFormat.getLongKey(paramsMap, "id");
//        OrderMainReport report = orderCostReportSV.getObjectById(OrderMainReport.class, busiId);
        OrderMainReport report = iOrderMainReportServicel.getById(busiId);
        if (report == null) {
            throw new BusinessException("根据费用上报id：" + busiId + " 未找到费用上报记录");
        }
        report.setState(OrderConsts.ORDER_COST_REPORT.AUDIT_PASS);
        if (report.getIsAuditPass() == null || report.getIsAuditPass() != OrderConsts.ORDER_COST_REPORT.IS_AUDIT_PASS) {
            report.setIsAuditPass(OrderConsts.ORDER_COST_REPORT.IS_AUDIT_PASS);
        }
        report.setAuditRemark(desc);
//        orderCostReportSV.saveOrUpdate(report);
//        orderOpRecordSV.saveOrUpdate(report.getOrderId() ,301);
//        session.saveOrUpdate(orderOpRecord);
        OrderOpRecord orderOpRecord = new OrderOpRecord();
        orderOpRecord.setOrderId(report.getOrderId());
        orderOpRecord.setOpType(301);
        iOrderMainReportServicel.saveOrUpdate(report);
//
        //油卡理论余额减少消费金额、生成账户流水
//        List<OrderCostDetailReport> list = orderCostReportSV.getOrderCostDetailReportByOrderId(report.getOrderId());
//        List<OrderCostReport> list1 = orderCostReportSV.getOrderCostReportByOrderId(report.getOrderId());
        QueryWrapper<OrderCostDetailReport> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",report.getOrderId());
        List<OrderCostDetailReport> list = orderCostDetailReportMapper.selectList(wrapper);
        QueryWrapper<OrderCostReport> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("order_id",report.getOrderId());
        List<OrderCostReport> list1 = baseMapper.selectList(wrapper1);
        if (list != null && list.size() > 0) {
            Set<Long> userIds = new HashSet<Long>();
            for (OrderCostDetailReport detail : list) {
                if (detail.getState() != null && detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    if (detail.getUserId() == null || detail.getUserId() <= 0) {
                        throw new BusinessException("费用上报明细id为：" + busiId + " 的记录未找到用户userid");
                    }
                    userIds.add(detail.getUserId());
                }
            }
            this.dealOrderCostDetailReport(userIds, list, report, desc);
        }
        if (list1 != null && list1.size() > 0) {
            for (OrderCostReport detail : list1) {
                if (detail.getState() != null && detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    detail.setState(OrderConsts.ORDER_COST_REPORT.AUDIT_PASS);
                    detail.setAuditRemark(desc);
//                    orderCostReportSV.saveOrUpdate(detail);
                    this.saveOrUpdate(detail);
                    //公里数上报
                    if (detail.getStartKm() != null || detail.getLoadingKm() != null || detail.getUnloadingKm() != null || detail.getEndKm() != null) {
//                        orderStateTrackOperSV.saveOrUpdate(report.getOrderId(), -1, OrderConsts.OrderOpType.MILEAGE_REPORT);
                        iOrderStateTrackOperService.saveOrUpdate(report.getOrderId(),-1L,OrderConsts.OrderOpType.MILEAGE_REPORT);
                    }

                    //满油上报
                    if (detail.getIsFullOil() != null && detail.getIsFullOil() == OrderConsts.IS_FULL_OIL.IS_FULL_OIL1) {
//                        IOrderInfoSV orderInfoSV = (IOrderInfoSV) SysContexts.getBean("orderInfoSV");
//                        OrderInfoH orderInfoH = orderInfoSV.getOrderH(report.getOrderId());
                        OrderInfoH orderInfoH = iOrderInfoHService.getOrderH(report.getOrderId());
                        boolean isCancelled = false;
                        if (orderInfoH != null && orderInfoH.getOrderState() != null
                                && orderInfoH.getOrderState().intValue() == OrderConsts.ORDER_STATE.CANCELLED) {
                            isCancelled = true;
                        }
                        if (!isCancelled) {
//                            orderVehicleTimeNodeTF.truncationVehicleTimeNode(report.getOrderId(), null);
                            iOrderVehicleTimeNodeService.truncationVehicleTimeNode(report.getOrderId(), null);
                        }
//                        orderStateTrackOperSV.saveOrUpdate(report.getOrderId(), -1, OrderConsts.OrderOpType.FILLED_WITH_OIL_REPORT);
                        iOrderStateTrackOperService.saveOrUpdate(report.getOrderId(),-1L,OrderConsts.OrderOpType.FILLED_WITH_OIL_REPORT);
                    }
                }
            }
        }

        this.dealOrderCostOtherReport(report.getId(), OrderConsts.ORDER_COST_REPORT.AUDIT_PASS, desc);
    }

    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    @Override
    public void fail(Long busiId, String desc, Map paramsMap,String accessToken ) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderMainReport report = iOrderMainReportServicel.getById(busiId);
        if (report == null) {
            throw new BusinessException("根据费用上报id：" + busiId + " 未找到费用上报记录");
        }
//        report.setState(OrderConsts.ORDER_COST_REPORT.IS_AUDIT_NOT_PASS);
        report.setState(OrderConsts.ORDER_COST_REPORT.AUDIT_NOT_PASS);
        report.setAuditRemark(desc);
        iOrderMainReportServicel.saveOrUpdate(report);
        QueryWrapper<OrderCostDetailReport> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",report.getOrderId());
        List<OrderCostDetailReport> list = orderCostDetailReportMapper.selectList(wrapper);

        QueryWrapper<OrderCostReport> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("order_id",report.getOrderId());
        List<OrderCostReport> list1 = baseMapper.selectList(wrapper1);

        if (list != null && list.size() > 0) {
            for (OrderCostDetailReport detail : list) {
                if (detail.getState() != null && detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState()
                        != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    detail.setState(OrderConsts.ORDER_COST_REPORT.AUDIT_NOT_PASS);
                    detail.setAuditRemark(desc);
//                    orderCostReportSV.saveOrUpdate(detail);
                    iOrderCostDetailReportService.saveOrUpdate(detail);
                }
            }
        }
        if (list1 != null && list1.size() > 0) {
            for (OrderCostReport detail : list1) {
                if (detail.getState() != null && detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    detail.setState(OrderConsts.ORDER_COST_REPORT.AUDIT_NOT_PASS);
                    detail.setAuditRemark(desc);
//                    orderCostReportSV.saveOrUpdate(detail);
                    this.saveOrUpdate(detail);
                }
            }
        }

        this.dealOrderCostOtherReport(report.getId(),OrderConsts.ORDER_COST_REPORT.AUDIT_NOT_PASS,desc);
    }

    @Override
    public Boolean isDoSave(OrderInfo orderInfo) {
        if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.FINISH) {
            return false;
        } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.CANCELLED) {
//         TODO  通过订单号查询 IOrderSchedulerService
            SysCfg sysCfg = this.getSysCfg("BANK_INIT", "0");
            String cfgValue = sysCfg.getCfgValue();
            OrderScheduler orderScheduler = this.getOrderScheduler(orderInfo.getOrderId());
            if (orderScheduler.getDependTime()!=null){
                Date date = DateUtil.localDateTime2Date(orderScheduler.getDependTime());
                Date newDate = PayOutIntfUtil.getDay(date, Integer.parseInt(cfgValue));
                Date nowDate = new Date();
                if (nowDate.getTime() > newDate.getTime()) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * niejeiwei
     * 司机小程序
     * 费用审核
     * 50031
     * @param vo
     * @param accessToken
     */
    @Override
    public void examineOrderCostReportByWx(OrderCostReportVo vo, String accessToken) {
        Long id=vo.getId();
        String auditRemark=vo.getAuditRemark();
        int chooseResult=vo.getChooseResult();

        if (id == null || id <= 0) {
            throw new BusinessException("请输入费用上报记录id");
        }
		/*if (StringUtils.isBlank(auditRemark)) {
			throw new BusinessException("请输入审核原因");
		}
		if (StringUtils.isBlank(chooseResult)) {
			throw new BusinessException("请输入审核结果");
		}*/
        String loadMileage=vo.getLoadMileages();
        String capacityLoadMileage=vo.getCapacityLoadMileages();
        if(chooseResult!=AuditConsts.RESULT.SUCCESS) {
            loadMileage=null;
            capacityLoadMileage=null;
        }
        this.dealExamineOrderCostReport(id, auditRemark,StringUtils.isNotBlank(loadMileage)?(long)(Double.valueOf(loadMileage)*1000):null
                ,StringUtils.isNotBlank(capacityLoadMileage)?(long)(Double.valueOf(capacityLoadMileage)*1000):null);
        this.auditOrderCostReportS(AuditConsts.AUDIT_CODE.ORDER_COST_REPORT ,id,auditRemark,chooseResult,accessToken);
    }

    @Override
    public List<VehicleMiscellaneouFeeDto> getVehicleOrderFeeByMonth(String plateNumber, Long tenantId, String month) {
        return baseMapper.getVehicleOrderFeeByMonth(plateNumber, tenantId, month);
    }

    private OrderScheduler getOrderScheduler(Long orderId) {
        //通过订单号查询 IOrderSchedulerService
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        if (null == orderScheduler) {
            com.youming.youche.order.domain.order.OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);

            if (orderSchedulerH == null) {
                throw new BusinessException("未查询到订单靠台时间!");
            }
            orderScheduler = new OrderScheduler();
            try {
                BeanUtils.copyProperties(orderScheduler, orderSchedulerH);
            } catch (Exception e) {

            }
        }
        return orderScheduler;
    }


    private void auditOrderCostReportS(String busiCode, Long busiId, String desc, int chooseResult,String accessToken) {
        if (!ORDER_COST_REPORT.equals(busiCode)) {
            throw new BusinessException("费用上报审核业务编码不正确，请联系客服！");
        }
        if (busiId == null || busiId <= 0) {
            throw new BusinessException("缺少上报费用记录ID！");
        }
        if (AuditConsts.RESULT.SUCCESS != chooseResult && chooseResult != AuditConsts.RESULT.FAIL) {
            throw new BusinessException("审核结果不正确！");
        }
        if (AuditConsts.RESULT.FAIL == chooseResult && StringUtils.isBlank(desc)) {
            throw new BusinessException("审核不通过，审核原因需要填写");
        }
        AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(busiCode, busiId, desc, chooseResult, accessToken);
        //  审核成功调用
        if (null != auditCallbackDto && !auditCallbackDto.getIsNext()  && auditCallbackDto.getIsAudit() && chooseResult == AuditConsts.RESULT.SUCCESS){
            sucess(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
         //  审核失败调用
        }else if (AuditConsts.RESULT.FAIL == chooseResult && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()){
            fail(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
        }
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
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



    /**
     * 处理上报其他费用状态
     * @param reId
     * @param state
     * @param desc
     * @throws Exception
     */
    private void dealOrderCostOtherReport(Long reId,int state, String desc)  {
        List<OrderCostOtherReport> orderCostOtherReport = this.getOrderCostOtherReport(reId, false);
        if (orderCostOtherReport != null) {
            for (OrderCostOtherReport detail : orderCostOtherReport) {
                if (detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS) {
                    detail.setState(state);
                    detail.setAuditRemark(desc);
                    detail.setUpdateTime(LocalDateTime.now());
                    iOrderCostOtherReportService.saveOrUpdate(detail);
                }
            }
        }
    }


    public void dealOrderCostDetailReport(Set<Long> userIds, List<OrderCostDetailReport> list,OrderMainReport report, String desc)  {
        if (userIds == null) {
            throw new BusinessException("用户id集合不能为空");
        }
        if (list == null) {
            throw new BusinessException("费用上报明细不能为空");
        }
        if (report == null) {
            throw new BusinessException("费用上报不能为空");
        }
        if (report.getOrderId() == null) {
            throw new BusinessException("费用上报订单号不能为空");
        }
        if (userIds != null && userIds.size() > 0) {
            long oilCardBalance = 0;
            long oilCashBalance = 0;
            long etcCardBalance = 0;
            long etcCashBalance = 0;
            for (Long userId : userIds) {
                for (OrderCostDetailReport detail : list) {
                    if (detail.getState() != null && detail.getState() >= OrderConsts.ORDER_COST_REPORT.SUBMITTED && detail.getState() != OrderConsts.ORDER_COST_REPORT.AUDIT_PASS
                            && String.valueOf(userId).equals(String.valueOf(detail.getUserId()))) {
                        Long balance = detail.getAmount() == null ? 0L : detail.getAmount();
                        //油费---油卡
                        if (detail.getPaymentWay() != null && detail.getPaymentWay() == OrderConsts.REPORT_PAYMENT_WAY.REPORT_PAYMENT_WAY1) {
                            String cardNum = detail.getCardNo() == null ? "" : detail.getCardNo();
                            //油卡理论余额减少消费金额
//                            oilCardManagementTF.updateCardBalanceByCardNum(cardNum, - balance,report.getOrderId());
                            iOilCardManagementService.updateCardBalanceByCardNum(cardNum,- balance,report.getOrderId(),null);
                            oilCardBalance += balance;
//                            orderStateTrackOperSV.saveOrUpdate(report.getOrderId(), -1, OrderConsts.OrderOpType.OIL_FEE_REPORT);
                            iOrderStateTrackOperService.saveOrUpdate(report.getOrderId(), -1L, OrderConsts.OrderOpType.OIL_FEE_REPORT);
                        }
                        //油费---现金
                        if (detail.getPaymentWay() != null && detail.getPaymentWay() == OrderConsts.REPORT_PAYMENT_WAY.REPORT_PAYMENT_WAY2
                                && detail.getTableType() != null && detail.getTableType() == OrderConsts.TABLE_TYPE.TABLE_TYPE1) {
                            oilCashBalance += balance;
//                            orderStateTrackOperSV.saveOrUpdate(report.getOrderId(), -1, OrderConsts.OrderOpType.OIL_FEE_REPORT);
                            iOrderStateTrackOperService.saveOrUpdate(report.getOrderId(), -1L, OrderConsts.OrderOpType.OIL_FEE_REPORT);
                        }
                        //路桥费---ETC卡号
                        if (detail.getPaymentWay() != null && detail.getPaymentWay() == OrderConsts.REPORT_PAYMENT_WAY.REPORT_PAYMENT_WAY3) {
                            etcCardBalance += balance;
                        }
                        //路桥费---现金
                        if (detail.getPaymentWay() != null && detail.getPaymentWay() == OrderConsts.REPORT_PAYMENT_WAY.REPORT_PAYMENT_WAY2
                                && detail.getTableType() != null && detail.getTableType() ==  OrderConsts.TABLE_TYPE.TABLE_TYPE2) {
                            etcCashBalance += balance;
                        }
                        detail.setState(OrderConsts.ORDER_COST_REPORT.AUDIT_PASS);
                        detail.setAuditRemark(desc);
//                        orderCostReportSV.saveOrUpdate(detail);
                        iOrderCostDetailReportService.saveOrUpdate(detail);
                    }
                }
                List<com.youming.youche.order.domain.order.BusiSubjectsRel> busiList = new ArrayList<>();
                if (oilCardBalance > 0) {

                    com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel =
                            iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ORDER_COST_REPORT_OIL_CARD, oilCardBalance);
                    busiList.add(amountFeeSubjectsRel);
                }
                if (oilCashBalance > 0) {
                    com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel =
                            iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ORDER_COST_REPORT_OIL_CASH, oilCashBalance);
                    busiList.add(amountFeeSubjectsRel);
                }
                if (etcCardBalance > 0) {
                    com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel =
                            iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ORDER_COST_REPORT_LUQIAO_CARD, etcCardBalance);
                    busiList.add(amountFeeSubjectsRel);
                }
                if (etcCashBalance > 0) {
                    com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel =
                            iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ORDER_COST_REPORT_LUQIAO_CASH, etcCashBalance);
                    busiList.add(amountFeeSubjectsRel);
                }
                if (busiList != null && busiList.size() > 0) {
                    // 计算费用集合
//                    List<BusiSubjectsRel> busiListFee = busiSubjectsRelTF.feeCalculation(EnumConsts.PayInter.ORDER_COST_REPORT, busiList);
                    List<BusiSubjectsRel> busiListFee = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ORDER_COST_REPORT, busiList);
                    long soNbr = CommonUtil.createSoNbr();
//                    SysOperator sysOperator = operatorSV.getSysOperatorByUserIdOrPhone(userId, null, 0L);
                    SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
                    if (sysOperator == null) {
                        throw new BusinessException("根据用户id：" + userId +" 没有找到用户信息!");
                    }
//                    accountDetailsTF.createAccountDetails(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.ORDER_COST_REPORT,
//                            userId, userId, sysOperator.getOperatorName(), busiListFee, soNbr, report.getOrderId(),
//                            report.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);

                    accountDetailsService.createAccountDetails(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.ORDER_COST_REPORT,
                            userId, userId, sysOperator.getOpName(), busiListFee, soNbr, report.getOrderId(),
                            report.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);

                }
            }
        }
    }
    public SysCfg getSysCfg(String cfgName, String cfgSystem){
        SysCfg sysCfg = (SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (Integer.parseInt(cfgSystem) == -1 || Integer.parseInt(cfgSystem)==(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }
}
