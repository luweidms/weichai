package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IClaimExpenseInfoService;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.api.order.IOrderAccountOilSourceService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderDriverSubsidyHService;
import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeExtVerService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderFeeVerService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderGoodsVerService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoExtVerService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderInfoVerService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderOilCardInfoVerService;
import com.youming.youche.order.api.order.IOrderOilDepotSchemeService;
import com.youming.youche.order.api.order.IOrderOilDepotSchemeVerService;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoHService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoVerService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderReportService;
import com.youming.youche.order.api.order.IOrderRetrographyCostInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderSchedulerVerService;
import com.youming.youche.order.api.order.IOrderStateTrackOperService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoVerService;
import com.youming.youche.order.api.order.IOrderVehicleTimeNodeService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderAccountOilSource;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderDriverSubsidyH;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderFeeVer;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderGoodsVer;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoExtVer;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderInfoVer;
import com.youming.youche.order.domain.order.OrderMainReport;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilCardInfoVer;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoH;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderReceipt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderSchedulerVer;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.domain.order.OrderTransitLineInfoVer;
import com.youming.youche.order.dto.ClaimExpenseCountDto;
import com.youming.youche.order.dto.OaloanCountDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.OrderLimitDto;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.order.GetOrderDetailForUpdateDto;
import com.youming.youche.order.dto.order.HasPermissionDto;
import com.youming.youche.order.dto.order.OrderDetailsAppDto;
import com.youming.youche.order.dto.order.OrderDetailsDto;
import com.youming.youche.order.dto.order.OrderOilCardInfoDto;
import com.youming.youche.order.dto.order.OrderOilCardInfoVerDto;
import com.youming.youche.order.dto.order.OrderVerDetailsDto;
import com.youming.youche.order.provider.mapper.order.OrderCostReportMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.transit.TransitCustHMapper;
import com.youming.youche.order.provider.mapper.transit.TransitCustMapper;
import com.youming.youche.order.provider.mapper.transit.TransitGoodsHMapper;
import com.youming.youche.order.provider.mapper.transit.TransitGoodsMapper;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import com.youming.youche.order.provider.utils.ObjectCompareUtils;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.provider.utils.VehicleStaticDataUtil;
import com.youming.youche.order.util.OrderUtil;
import com.youming.youche.order.vo.CopilotMapVo;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.UpdateOrderVo;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysMenuBtn;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.youming.youche.order.util.OrderUtil.getAddOrderGoodsCheckNoBlank;
import static com.youming.youche.order.util.OrderUtil.getAddOrderInfoCheckNoBlank;
import static com.youming.youche.order.util.OrderUtil.getAddOrderSchedulerCheckNoBlank;
import static com.youming.youche.order.util.OrderUtil.mul;
import static com.youming.youche.order.util.OrderUtil.objToFloatDiv100;
import static com.youming.youche.order.util.OrderUtil.objToFloatMul100;
import static com.youming.youche.order.util.OrderUtil.objToLongMul100;
import static com.youming.youche.system.constant.AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE;


/**
 * <p>
 * 新的订单表 服务实现类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderInfoHServiceImpl extends BaseServiceImpl<OrderInfoHMapper, OrderInfoH> implements IOrderInfoHService {

    @Resource
    OrderInfoMapper orderInfoMapper;

    @Resource
    IOrderFeeService iOrderFeeService;

    @Resource
    IOrderFeeHService iOrderFeeHService;

    @Resource
    IOrderFeeExtService iOrderFeeExtService;

    @Resource
    IOrderFeeExtHService iOrderFeeExtHService;

    @Resource
    IOrderInfoExtService iOrderInfoExtService;

    @Resource
    IOrderInfoExtHService iOrderInfoExtHService;

    @Resource
    IOrderSchedulerService iOrderSchedulerService;

    @Resource
    IOrderSchedulerHService iOrderSchedulerHService;

    @Resource
    IOrderGoodsService iOrderGoodsService;

    @Resource
    IOrderGoodsHService iOrderGoodsHService;

    @Resource
    IOrderPaymentDaysInfoService iOrderPaymentDaysInfoService;

    @Resource
    IOrderPaymentDaysInfoHService iOrderPaymentDaysInfoHService;

    @Resource
    IOrderTransitLineInfoService iOrderTransitLineInfoService;

    @Resource
    IOrderTransitLineInfoHService iOrderTransitLineInfoHService;

    @Resource
    IOrderOilDepotSchemeService iOrderOilDepotSchemeService;

    @DubboReference(version = "1.0.0")
    ICmCustomerLineService iCmCustomerLineService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    ReadisUtil readisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    IOrderRetrographyCostInfoService iOrderRetrographyCostInfoService;

    @Resource
    IOrderOilCardInfoService iOrderOilCardInfoService;

    @Resource
    IOrderOilCardInfoVerService orderOilCardInfoVerService;

    @Resource
    IOilCardManagementService iOilCardManagementService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    IOrderOpRecordService iOrderOpRecordService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;

    @DubboReference(version = "1.0.0")
    ITrailerManagementService iTrailerManagementService;
    @Lazy
    @Resource
    IOrderInfoService iOrderInfoService;

    @Resource
    IOrderFeeStatementService orderFeeStatementService;

    @Resource
    IOrderStateTrackOperService iOrderStateTrackOperService;

    @Resource
    IOrderReceiptService iOrderReceiptService;

    @Resource
    IOrderTransferInfoService iOrderTransferInfoService;

    @Resource
    IOrderDriverSwitchInfoService iOrderDriverSwitchInfoService;

    @Resource
    IOrderAgingInfoService iOrderAgingInfoService;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;

    @Resource
    IOrderAgingAppealInfoService iOrderAgingAppealInfoService;

    @Resource
    IOrderVehicleTimeNodeService iOrderVehicleTimeNodeService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService iSysSmsSendService;

    @Resource
    IPayoutIntfService iPayoutIntfService;

    @Resource
    IOrderProblemInfoService iOrderProblemInfoService;

    @Resource
    IOrderDriverSubsidyService iOrderDriverSubsidyService;

    @Resource
    IOrderDriverSubsidyHService iOrderDriverSubsidyHService;

    @Resource
    TransitCustMapper transitCustMapper;

    @Resource
    TransitCustHMapper transitCustHMapper;

    @Resource
    TransitGoodsMapper transitGoodsMapper;

    @Resource
    TransitGoodsHMapper transitGoodsHMapper;


    @Resource
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;

    @Resource
    private IOrderAgingInfoService orderAgingInfoService;

    @Resource
    private IOrderProblemInfoService orderProblemInfoService;

    @Resource
    private IOrderLimitService iOrderLimitService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService auditNodeInstService;

    @Resource
    private IOrderCostReportService iOrderCostReportService;

    @DubboReference(version = "1.0.0")
    ISysMenuBtnService sysMenuBtnService;


    @Resource
    private IOrderDriverSubsidyService orderDriverSubsidyService;


    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;

    @Resource
    private VehicleStaticDataUtil vehicleStaticDataUtil;

    @Resource
    IOrderInfoVerService orderInfoVerService;

    @Resource
    IOrderInfoExtVerService orderInfoExtVerService;

    @Resource
    IOrderFeeVerService orderFeeVerService;

    @Resource
    IOrderFeeExtVerService orderFeeExtVerService;

    @Resource
    IOrderSchedulerVerService orderSchedulerVerService;

    @Resource
    IOrderGoodsVerService orderGoodsVerService;

    @Resource
    IOrderPaymentDaysInfoVerService orderPaymentDaysInfoVerService;

    @Resource
    IOrderPaymentDaysInfoService orderPaymentDaysInfoService;

    @Resource
    IOrderTransitLineInfoVerService orderTransitLineInfoVerService;

    @Resource
    IOrderOilDepotSchemeVerService orderOilDepotSchemeVerService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @Resource
    OrderInfoHMapper orderInfoHMapper;

    @Resource
    private IOaLoanService oaLoanService;

    @Resource
    IClaimExpenseInfoService claimExpenseInfoService;

    @Resource
    IOrderInfoService orderInfoService;

    @Resource
    IOrderReportService orderReportService;

    @Resource
    OrderCostReportMapper orderCostReportMapper;

    @Resource
    IPayFeeLimitService payFeeLimitService;

    @Resource
    IOrderAccountService iOrderAccountService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @Resource
    IOrderAccountOilSourceService orderAccountOilSourceService;

    @Override
    public OrderInfoH selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderInfoH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderInfoH::getOrderId, orderId);
        return getOne(wrapper, false);
    }

    @Override
    public OrderInfoH getOrderH(Long orderId) {
        LambdaQueryWrapper<OrderInfoH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderInfoH::getOrderId, orderId);
        List<OrderInfoH> list = this.list(wrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private OrderInfo getOrder(Long orderId) {
        LambdaQueryWrapper<OrderInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderInfo::getOrderId, orderId);
        List<OrderInfo> list = orderInfoMapper.selectList(wrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return new OrderInfo();
        }
    }

    @Override
    public OrderDetailsDto queryOrderDetails(Long orderId, Integer orderDetailsType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空，请联系客服！");
        }
        //权限控制
        boolean orderIncomePermission = sysRoleService.hasorderIncomePermission(loginInfo);
        boolean orderCostPermission = sysRoleService.hasOrderCostPermission(loginInfo);
//        boolean orderIncomePermission =  PermissionCacheUtil.hasOrderIncomePermission();
//        boolean orderCostPermission = PermissionCacheUtil.hasOrderCostPermission();
        OrderInfo orderInfo = getOrder(orderId);
        if(orderInfo.getSourceProvince() != null){
            orderInfo.setSourceProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderInfo.getSourceProvince())).getCodeName());
        }
        if(orderInfo.getSourceRegion() != null){
            orderInfo.setSourceRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderInfo.getSourceRegion())).getCodeName());
        }
        if(orderInfo.getSourceCounty() != null){
            orderInfo.setSourceCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderInfo.getSourceCounty())).getCodeName());
        }
        if(orderInfo.getDesProvince() != null){
            orderInfo.setDesProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderInfo.getDesProvince())).getCodeName());
        }
        if(orderInfo.getDesRegion() != null){
            orderInfo.setDesRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderInfo.getDesRegion())).getCodeName());
        }
        if(orderInfo.getDesCounty() != null){
            orderInfo.setDesCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderInfo.getDesCounty())).getCodeName());
        }
        if(orderInfo.getOrgId() != null){

            // 根据组织ID获取数据组织名称
            String orgNameByOrgId = sysOrganizeService.getOrgNameByOrgId(orderInfo.getOrgId().longValue(), loginInfo.getTenantId());
            if(orgNameByOrgId != null && !orgNameByOrgId.equals("")){
                orderInfo.setOrgIdName(orgNameByOrgId);
            }
        }

        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        OrderDetailsDto out = new OrderDetailsDto();
        List<OrderTransitLineInfo> transitLineInfos = null;
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.setOrderDetailsType(orderDetailsType);
        if (orderInfo != null && orderInfo.getOrderId() != null) {
            out.setIsHis(OrderConsts.TableType.ORI);
            orderFee = iOrderFeeService.getOrderFee(orderId);
            orderFeeExt = iOrderFeeExtService.getOrderFeeExt(orderId);
            orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
            orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
            orderGoods = iOrderGoodsService.getOrderGoods(orderId);
            OrderPaymentDaysInfo costDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.COST);
            OrderPaymentDaysInfo incomeDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
//            session.evict(costDaysInfo);
//            session.evict(incomeDaysInfo);
            if (costDaysInfo == null) {
                costDaysInfo = new OrderPaymentDaysInfo();
            }
            if (incomeDaysInfo == null) {
                incomeDaysInfo = new OrderPaymentDaysInfo();
            }
            costDaysInfo.setId(null);
            incomeDaysInfo.setId(null);
            costDaysInfo.setOrderId(null);
            incomeDaysInfo.setOrderId(null);
            if(incomeDaysInfo.getBalanceType() != null){
                incomeDaysInfo.setBalanceTypeName(getSysStaticData("BALANCE_TYPE", String.valueOf(incomeDaysInfo.getBalanceType())).getCodeName());
            }
            if(costDaysInfo.getBalanceType() != null){
                costDaysInfo.setBalanceTypeName(getSysStaticData("BALANCE_TYPE", String.valueOf(costDaysInfo.getBalanceType())).getCodeName());
            }
            out.setCostPaymentDaysInfo(costDaysInfo);
            out.setIncomePaymentDaysInfo(incomeDaysInfo);
            transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
//            session.evict(orderInfo);
//            session.evict(orderFeeExt);
//            session.evict(orderFee);
//            session.evict(orderInfoExt);
//            session.evict(orderScheduler);
//            session.evict(orderGoods);
        } else {
            //订单表查询不到数据  从订单历史表中查询订单数据
            OrderInfoH orderInfoH = getOrderH(orderId);
            if (orderInfoH != null) {
                out.setIsHis(OrderConsts.TableType.HIS);
                OrderFeeH orderFeeH = iOrderFeeHService.getOrderFeeH(orderId);
                OrderFeeExtH orderFeeExtH = iOrderFeeExtHService.getOrderFeeExtH(orderId);
                OrderInfoExtH orderInfoExtH = iOrderInfoExtHService.getOrderInfoExtH(orderId);
                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
                OrderGoodsH orderGoodsH = iOrderGoodsHService.getOrderGoodsH(orderId);
                OrderPaymentDaysInfoH costDaysInfo = iOrderPaymentDaysInfoHService.queryOrderPaymentDaysInfoH(orderId, OrderConsts.PAYMENT_DAYS_TYPE.COST);
                OrderPaymentDaysInfoH incomeDaysInfo = iOrderPaymentDaysInfoHService.queryOrderPaymentDaysInfoH(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
                if (costDaysInfo != null) {
                    OrderPaymentDaysInfo costPaymentDaysInfo = new OrderPaymentDaysInfo();
                    try {
                        BeanUtil.copyProperties(costDaysInfo, costPaymentDaysInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    costPaymentDaysInfo.setId(null);
                    costPaymentDaysInfo.setOrderId(null);
                    out.setCostPaymentDaysInfo(costPaymentDaysInfo);
                }
                if (incomeDaysInfo != null) {
                    OrderPaymentDaysInfo incomePaymentDaysInfo = new OrderPaymentDaysInfo();
                    try {
                        BeanUtil.copyProperties(incomeDaysInfo, incomePaymentDaysInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    incomePaymentDaysInfo.setId(null);
                    incomePaymentDaysInfo.setOrderId(null);
                    out.setIncomePaymentDaysInfo(incomePaymentDaysInfo);
                }
                orderInfo = new OrderInfo();
                try {
                    BeanUtil.copyProperties(orderFeeH, orderFee);
                    BeanUtil.copyProperties(orderInfoH, orderInfo);
                    BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
                    BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
                    BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
                    BeanUtil.copyProperties(orderGoodsH, orderGoods);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<OrderTransitLineInfoH> transitLineInfoHs = iOrderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
                if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                    transitLineInfos = new ArrayList<>();
                    for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                        OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                        try {
                            BeanUtil.copyProperties(orderTransitLineInfoH, transitLineInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        transitLineInfos.add(transitLineInfo);
                    }
                }
            } else {
                throw new BusinessException("未找到订单[" + orderId + "]信息!");
            }
        }

        if (orderDetailsType == OrderConsts.orderDetailsType.COPY) {
            // 复制订单靠台时间需要更新为当前时间+1天
            orderInfo.setId(null);
            orderFee.setId(null);
            orderFeeExt.setId(null);
            orderInfoExt.setId(null);
            orderScheduler.setId(null);

            orderInfo.setToOrderId(null);
            orderInfo.setToTenantName(null);
            orderInfo.setToTenantId(null);
            orderInfo.setOrderId(null);
            orderFee.setOrderId(null);
            orderFeeExt.setOrderId(null);
            orderInfoExt.setOrderId(null);
            orderScheduler.setOrderId(null);
            Date date = null;
            try {
                date = DateUtil.formatStringToDate(
                        DateUtil.formatDate(DateUtil.addDate(new Date(), 1), DateUtil.DATE_FORMAT) + " "
                                + DateUtil.formatDate(getLocalDateTimeToDate(orderScheduler.getDependTime()), DateUtil.TIME_FORMAT)
                        , DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            orderScheduler.setDependTime(getDateToLocalDateTime(date));

            orderGoods.setContractId(null);
            orderGoods.setContractUrl(null);
            orderGoods.setReceiptsUserId(null);
            orderGoods.setReceiptsUserName(null);
            orderGoods.setTenantId(null);
            orderGoods.setOrderId(null);
            orderGoods.setId(null);
            orderGoods.setCreateTime(null);
        } else if (orderDetailsType == OrderConsts.orderDetailsType.SELECT) {
            //无权限 清除信息
            if (!orderCostPermission) {
                orderInfoExt.setPaymentWay(null);
                orderFee.setTotalFee(null);
                orderFee.setGuidePrice(null);
                orderFee.setPreTotalFee(null);
                orderFee.setPreCashFee(null);
                orderFee.setPreOilVirtualFee(null);
                orderFee.setPreOilFee(null);
                orderFee.setPreEtcFee(null);
                orderFee.setFinalFee(null);
                orderFee.setTotalFee(null);
                orderFee.setInsuranceFee(null);
                orderFee.setArrivePaymentFee(null);
                orderFeeExt.setSalary(null);
                orderFeeExt.setCopilotSalary(null);
                orderFeeExt.setPontage(null);
                orderFeeExt.setEstFee(null);
                orderFeeExt.setPontagePer(null);
                orderInfoExt.setRunOil(null);
                orderInfoExt.setCapacityOil(null);
                orderInfoExt.setOilUseType(null);
                orderInfoExt.setOilIsNeedBill(null);
            }
            if (!orderIncomePermission) {
                orderFee.setCostPrice(null);
                orderFee.setPrePayCash(null);
                orderFee.setAfterPayCash(null);
                orderFee.setPrePayEquivalenceCardAmount(null);
                orderFee.setAfterPayEquivalenceCardAmount(null);
                orderFee.setPrePayEquivalenceCardType(null);
                orderFee.setAfterPayEquivalenceCardType(null);
                orderFee.setAfterPayAcctType(null);
                orderFee.setPrePayEquivalenceCardNumber(null);
                orderFee.setAfterPayEquivalenceCardNumber(null);
                orderFee.setPriceEnum(null);
            }
        }
        try {
            orderGoods.setContractUrl(orderGoods.getContractUrl() == null ? ""
                    : client.getHttpURL(orderGoods.getContractUrl()).split("\\?")[0]);
            orderScheduler.setClientContractUrl(orderScheduler.getClientContractUrl() == null ? ""
                    : client.getHttpURL(orderScheduler.getClientContractUrl()).split("\\?")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String orgNameByOrgId = sysOrganizeService.getOrgNameByOrgId(orderInfo.getOrgId().longValue(), loginInfo.getTenantId());
        orderInfo.setOrgIdName(orgNameByOrgId);
        out.setOrderInfo(orderInfo);
        if (orderScheduler.getCarLengh() != null && orderScheduler.getCarStatus() != null && orderScheduler.getCarStatus() > 0) {
            orderScheduler.setCarLenghName(vehicleStaticDataUtil.getVehicleLengthNameById(orderScheduler.getCarStatus().toString(), orderScheduler.getCarLengh()));
        }
        if (orderScheduler.getCarStatus() != null && orderScheduler.getCarStatus() > 0) {
            orderScheduler.setCarStatusName(readisUtil.getSysStaticDatabyId("VEHICLE_STATUS", orderScheduler.getCarStatus().toString()).getCodeName());
        }
        out.setOrderScheduler(orderScheduler);
        if(orderFee.getPriceEnum() != null){
            String name = getSysStaticData("PRICE_ENUM", orderFee.getPriceEnum().toString()).getCodeName();
            orderFee.setPriceEnumName(name);
        }
        out.setOrderFee(orderFee);
        out.setOrderFeeExt(orderFeeExt);
        if (orderGoods.getVehicleLengh() != null && orderGoods.getVehicleStatus() != null && orderGoods.getVehicleStatus() > 0) {
            orderGoods.setVehicleLenghName(vehicleStaticDataUtil.getVehicleLengthName(orderGoods.getVehicleStatus().toString(), orderGoods.getVehicleLengh()));
        }
        if(orderGoods.getLineName() != null && !orderGoods.getLineName().equals("")){
            orderGoods.setLinkName(orderGoods.getLineName());
        }
        if(orderGoods.getLinePhone() != null && !orderGoods.getLinePhone().equals("")){
            orderGoods.setLinkPhone(orderGoods.getLinePhone());
        }
        if(orderGoods.getGoodsType() != null){
            orderGoods.setGoodsTypeName(getSysStaticData("GOODS_TYPE", String.valueOf(orderGoods.getGoodsType())).getCodeName());
        }
        if(orderGoods.getReciveProvinceId() != null){
            String name = getSysStaticData("SYS_PROVINCE", orderGoods.getReciveProvinceId().toString()).getCodeName();
            orderGoods.setReciveProvinceName(name);
        }
        if(orderGoods.getReciveCityId() != null){
            String name1 = getSysStaticData("SYS_CITY", orderGoods.getReciveCityId().toString()).getCodeName();
            orderGoods.setReciveCityName(name1);
        }
        out.setOrderGoods(orderGoods);
        if(orderInfoExt.getPaymentWay() != null){
            String name1 = getSysStaticData("PAYMENT_WAY", orderInfoExt.getPaymentWay().toString()).getCodeName();
            orderInfoExt.setPaymentWayName(name1);
        }
        out.setOrderInfoExt(orderInfoExt);
        if ((orderInfo.getIsTransit() == null || orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_NO)
                && (orderScheduler.getVehicleClass() != null
                && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && (orderInfoExt.getPaymentWay() != null
                && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST))// 不是外发单
            // 自有车无勾选承包价需查询油站
        ) {
            //查看成本限制
            if (orderDetailsType != OrderConsts.orderDetailsType.SELECT || (orderDetailsType == OrderConsts.orderDetailsType.SELECT && orderCostPermission)) {
                List<OrderOilDepotScheme> depotSchemes = iOrderOilDepotSchemeService.getOrderOilDepotSchemeByOrderId(orderId, false, loginInfo);
                if (orderDetailsType == OrderConsts.orderDetailsType.COPY) {
                    for (OrderOilDepotScheme scheme : depotSchemes) {
//                        session.evict(scheme);
                        scheme.setOrderId(null);
                        scheme.setId(null);
                    }
                }
                out.setSchemes(depotSchemes);
                List<Map> driverSubsidyDays = iOrderSchedulerService.queryOrderDriverSubsidyDay(orderId);
                out.setDriverSubsidyDays(driverSubsidyDays);
            }
        }
        if (out.getIsHis() == OrderConsts.TableType.ORI) {
            if ((orderInfo.getIsTransit() == null || orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_NO)
                    && (orderInfo.getFromOrderId() == null || orderInfo.getFromOrderId() <= 0)) {// 自有单
                if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.TO_BE_LOAD) {// 待装货前
                    out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_ALL);
                } else if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD
                        && orderInfo.getOrderState() < OrderConsts.ORDER_STATE.FINISH) {
                    // 待装货 -->  已完成之前
                    out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
                } else {
                    out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);
                }
            } else if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {// 外发单
                if ((orderInfo.getToOrderId() == null || orderInfo.getToOrderId() <= 0)
                        && (orderInfo.getFromOrderId() == null || orderInfo.getFromOrderId() <= 0)) {
                    // 暂未接单 并且不是在线接单
                    if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.TO_BE_LOAD) {// 待装货前
                        out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_ALL);
                    } else if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD
                            && orderInfo.getOrderState() < OrderConsts.ORDER_STATE.FINISH) {
                        // 待装货 --> 已完成之前
                        out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
                    } else {
                        out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);
                    }
                } else {
                    out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
                }

            }
        } else {
            out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);// 历史单不予修改
        }
        if (orderDetailsType == OrderConsts.orderDetailsType.COPY) {
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                    orderTransitLineInfo.setOrderId(null);
                    orderTransitLineInfo.setId(null);
                }
            }
            //固定线路的订单，
            if (orderInfo.getOrderType() == OrderConsts.OrderType.FIXED_LINE && orderScheduler.getSourceId() != null) {
                CmCustomerLine line = iCmCustomerLineService.getCmCustomerLineById(orderScheduler.getSourceId());
                //订单归属部门初始值为线路的归属部门且不能修改
                if (line != null) {
                    if (line.getSaleDaparment() != null) {
                        orderInfo.setOrgId(line.getSaleDaparment().intValue());
                    }
                }
            }
            // 复制订单可以全量修改
            out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_ALL);
        }
        // 改单若是来自在线接单 该订单待装货前只能重新指派
        if (orderInfo.getOrderType() != null && orderInfo.getOrderType() == OrderConsts.OrderType.ONLINE_RECIVE) {
            if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.TO_BE_LOAD) {// 待装货前
                out.setUpdateState(OrderConsts.UPDATE_STATE.REASSIGN);
            } else {
                out.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);
            }
        }
        out.setIsTransit(orderInfo.getIsTransit());

        Map tenantMap = iSysTenantDefService.getTenantInfo(orderInfo.getTenantId());

        // "当前车队的办公地址就是回单地址"
        out.setReciveAddr(tenantMap.get("detailAddr") != null ? String.valueOf(tenantMap.get("detailAddr")) : "");
        Integer provinceId = null;
        if (tenantMap.get("provinceId") != null) {
            provinceId = Integer.parseInt(tenantMap.get("provinceId").toString());
        }

        Integer cityId = null;
        if (tenantMap.get("cityId") != null) {
            cityId = Integer.parseInt(tenantMap.get("cityId").toString());
        }
        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//转单
            SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
            if (sysTenantDef != null) {
                out.setToTenantAdminUserId(sysTenantDef.getAdminUser() + "");
                out.setToTenantAdminUserName(sysTenantDef.getLinkMan());
                out.setToTenantAdminUserPhone(sysTenantDef.getLinkPhone());
            }
            out.setUpdateType(OrderConsts.UPDATE_TYPE.IS_TRANSFER_ORDER);
            if (orderInfo.getFromTenantId() != null && orderInfo.getFromTenantId() > 0) {//来自接单
                out.setUpdateType(OrderConsts.UPDATE_TYPE.MIDDLE_ORDER);
            }
        } else {
            if (orderInfo.getFromTenantId() != null && orderInfo.getFromTenantId() > 0) {//来自接单
                out.setUpdateType(OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER);
            } else {//自有单
                out.setUpdateType(OrderConsts.UPDATE_TYPE.OWN_ORDER);
            }
        }
        //临时车队订单不能修改
        if (orderInfoExt.getIsTempTenant() != null &&
                orderInfoExt.getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
            out.setUpdateType(OrderConsts.UPDATE_TYPE.MIDDLE_ORDER);
        }
        //无成本权限 不加载成本信息
        if (orderCostPermission && orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
            //自有车实报实销模式
            out.setOrderRetrographyCostInfo(iOrderRetrographyCostInfoService.queryOrderRetrographyCostInfoByCheck(orderId));

            List<OrderOilCardInfo> oilCardInfos = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
            if (oilCardInfos != null && oilCardInfos.size() > 0) {
                for (OrderOilCardInfo orderOilCardInfo : oilCardInfos) {
                    List<OilCardManagement> managements = iOilCardManagementService.findByOilCardNum(orderOilCardInfo.getOilCardNum(), loginInfo.getTenantId());
                    if (managements != null && managements.size() > 0) {
                        OilCardManagement management = managements.get(0);
                        orderOilCardInfo.setCardBalance(management.getCardBalance());
                        orderOilCardInfo.setCardTypeName(getSysStaticData("OIL_CARD_TYPE", managements.get(0).getCardType() == null ? "" : String.valueOf( managements.get(0).getCardType())).getCodeName());
                    }
                }
            }
            out.setOilCardInfos(oilCardInfos);

            }

        //无成本权限 不加载成本信息
        if (orderCostPermission && orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            List<Map> list = null;
            if (list != null && list.size() > 0) {
                Map map = list.get(0);
                out.setChangeCost(DataFormat.getLongKey(map, "change_cost") < 0 ? 0 : DataFormat.getLongKey(map, "change_cost"));
                out.setFixedCost(DataFormat.getLongKey(map, "fixed_cost") < 0 ? 0 : DataFormat.getLongKey(map, "fixed_cost"));
                out.setOperCost(DataFormat.getLongKey(map, "oper_cost") < 0 ? 0 : DataFormat.getLongKey(map, "oper_cost"));
                out.setFee(DataFormat.getLongKey(map, "fee") < 0 ? 0 : DataFormat.getLongKey(map, "fee"));
            }
        }
        out.setTotalOilFee((orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee()) + (orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee()));
        out.setCityId(cityId);
        out.setProvinceId(provinceId);
        out.getCityName();
        out.getProvinceName();
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                if(orderTransitLineInfo.getProvince() != null){
                    orderTransitLineInfo.setProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderTransitLineInfo.getProvince())).getCodeName());
                }
                if(orderTransitLineInfo.getRegion() != null){
                    orderTransitLineInfo.setRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderTransitLineInfo.getRegion())).getCodeName());
                }
                if(orderTransitLineInfo.getCounty() != null){
                    orderTransitLineInfo.setCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderTransitLineInfo.getCounty())).getCodeName());
                }
            }
        }
        out.setTransitLineInfos(transitLineInfos);
        out.setOrderIncomePermission(orderIncomePermission);
        out.setOrderCostPermission(orderCostPermission);

        if (CollectionUtils.isNotEmpty(out.getOilCardInfos())) {
            List<OrderOilCardInfoDto> oilCardInfoDtos = new ArrayList<>();
            for (OrderOilCardInfo oilCardInfo : out.getOilCardInfos()) {
                OrderOilCardInfoDto dto = new OrderOilCardInfoDto();
                BeanUtil.copyProperties(oilCardInfo, dto);
                oilCardInfoDtos.add(dto);
            }
            out.setOilCardInfoDtos(oilCardInfoDtos);
        }
        return out;
    }

    @Override
    public OrderVerDetailsDto queryOrderVerDetails(Long orderId, String accessToken) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空，请联系客服！");
        }
        //   IOrderInfoVerSV orderInfoVerSV = (IOrderInfoVerSV) SysContexts.getBean("orderInfoVerSV");
        //   IOrderPaymentDaysInfoSV orderPaymentDaysInfoSV = (IOrderPaymentDaysInfoSV) SysContexts.getBean("orderPaymentDaysInfoSV");
        OrderInfoVer orderInfoVer = orderInfoVerService.getOrderInfoVer(orderId);
        if(orderInfoVer.getSourceProvince() != null){
            orderInfoVer.setSourceProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderInfoVer.getSourceProvince())).getCodeName());
        }
        if(orderInfoVer.getSourceRegion() != null){
            orderInfoVer.setSourceRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderInfoVer.getSourceRegion())).getCodeName());
        }
        if(orderInfoVer.getSourceCounty() != null){
            orderInfoVer.setSourceCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderInfoVer.getSourceCounty())).getCodeName());
        }
        if(orderInfoVer.getDesProvince() != null){
            orderInfoVer.setDesProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderInfoVer.getDesProvince())).getCodeName());
        }
        if(orderInfoVer.getDesRegion() != null){
            orderInfoVer.setDesRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderInfoVer.getDesRegion())).getCodeName());
        }
        if(orderInfoVer.getDesCounty() != null){
            orderInfoVer.setDesCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderInfoVer.getDesCounty())).getCodeName());
        }
        if (orderInfoVer == null) {
            throw new BusinessException("未找到该订单修改信息!");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //权限控制
        boolean orderIncomePermission = true;
        boolean orderCostPermission = sysRoleService.hasOrderCostPermission(loginInfo);
        OrderVerDetailsDto out = new OrderVerDetailsDto();
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        OrderInfoExtVer orderInfoExtVer = orderInfoExtVerService.getOrderInfoExtVer(orderId);
        OrderFeeVer orderFeeVer = orderFeeVerService.getOrderFeeVer(orderId);
        OrderFeeExtVer orderFeeExtVer = orderFeeExtVerService.getOrderFeeExtVer(orderId);
        OrderSchedulerVer orderSchedulerVer = orderSchedulerVerService.getOrderSchedulerVer(orderId);
        OrderGoodsVer orderGoodsVer = orderGoodsVerService.getOrderGoodsVer(orderId);
        List<OrderPaymentDaysInfoVer> costDaysInfos = orderPaymentDaysInfoVerService.queryOrderPaymentDaysInfoVer(orderId,
                OrderConsts.PAYMENT_DAYS_TYPE.COST, OrderConsts.IS_UPDATE.UPDATE);
        List<OrderPaymentDaysInfoVer> incomeDaysInfos = orderPaymentDaysInfoVerService.queryOrderPaymentDaysInfoVer(orderId,
                OrderConsts.PAYMENT_DAYS_TYPE.INCOME, OrderConsts.IS_UPDATE.UPDATE);

        List<OrderTransitLineInfoVer> lineInfoVers = orderTransitLineInfoVerService.queryOrderTransitLineInfoByOrderId(orderId, OrderConsts.IS_UPDATE.UPDATE);

        for (OrderTransitLineInfoVer orderTransitLineInfoVer: lineInfoVers) {
            if(orderTransitLineInfoVer.getProvince() != null){
                orderTransitLineInfoVer.setProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderTransitLineInfoVer.getProvince())).getCodeName());
            }
            if(orderTransitLineInfoVer.getRegion() != null){
                orderTransitLineInfoVer.setRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderTransitLineInfoVer.getRegion())).getCodeName());
            }
            if(orderTransitLineInfoVer.getCounty() != null){
                orderTransitLineInfoVer.setCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderTransitLineInfoVer.getCounty())).getCodeName());
            }
        }
        out.setTransitLineInfoVers(lineInfoVers);
        if (costDaysInfos != null && costDaysInfos.size() > 0) {
            if(costDaysInfos.get(0).getBalanceType() != null){
                costDaysInfos.get(0).setBalanceTypeName(getSysStaticData("BALANCE_TYPE", String.valueOf(costDaysInfos.get(0).getBalanceType())).getCodeName());
            }
            out.setCostPaymentDaysInfoVer(costDaysInfos.get(0));
        }
        if (incomeDaysInfos != null && incomeDaysInfos.size() > 0) {
            if(incomeDaysInfos.get(0).getBalanceType() != null){
                incomeDaysInfos.get(0).setBalanceTypeName(getSysStaticData("BALANCE_TYPE", String.valueOf(incomeDaysInfos.get(0).getBalanceType())).getCodeName());
            }
            out.setIncomePaymentDaysInfoVer(incomeDaysInfos.get(0));
        }
        try {
            orderGoodsVer.setContractUrl(orderGoodsVer.getContractUrl() == null ? "" : client.getHttpURL(orderGoodsVer.getContractUrl()).split("\\?")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //无权限 清除信息
        if (!orderCostPermission) {
            orderInfoExtVer.setPaymentWay(null);
            orderFeeVer.setTotalFee(null);
            orderFeeVer.setPreTotalFee(null);
            orderFeeVer.setPreCashFee(null);
            orderFeeVer.setPreOilVirtualFee(null);
            orderFeeVer.setPreOilFee(null);
            orderFeeVer.setPreEtcFee(null);
            orderFeeVer.setFinalFee(null);
            orderFeeVer.setTotalFee(null);
            orderFeeVer.setInsuranceFee(null);
            orderFeeExtVer.setSalary(null);
            orderFeeExtVer.setCopilotSalary(null);
            orderFeeExtVer.setPontage(null);
            orderFeeExtVer.setEstFee(null);
            orderFeeExtVer.setPontagePer(null);
            orderInfoExtVer.setRunOil(null);
            orderInfoExtVer.setCapacityOil(null);
            orderInfoExtVer.setOilUseType(null);
            orderInfoExtVer.setOilIsNeedBill(null);
        }
        if (!orderIncomePermission) {
            orderFeeVer.setCostPrice(null);
            orderFeeVer.setPrePayCash(null);
            orderFeeVer.setAfterPayCash(null);
            orderFeeVer.setPrePayEquivalenceCardAmount(null);
            orderFeeVer.setAfterPayEquivalenceCardAmount(null);
            orderFeeVer.setPrePayEquivalenceCardType(null);
            orderFeeVer.setAfterPayEquivalenceCardType(null);
            orderFeeVer.setAfterPayAcctType(null);
            orderFeeVer.setPrePayEquivalenceCardNumber(null);
            orderFeeVer.setAfterPayEquivalenceCardNumber(null);
            orderFeeVer.setPriceEnum(null);
        }
        if ((orderInfoVer.getIsTransit() == null || orderInfoVer.getIsTransit() == OrderConsts.IsTransit.TRANSIT_NO)
                && (orderSchedulerVer.getVehicleClass() != null
                && orderSchedulerVer.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && (orderInfoExtVer.getPaymentWay() != null && orderInfoExtVer.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST))// 不是外发单
                // 自有车无勾选承包价需查询油站
                && orderCostPermission//成本权限
        ) {
//            ILineOilDepotSchemeSV schemeSV = (ILineOilDepotSchemeSV) SysContexts.getBean("lineOilDepotSchemeSV");
//
            List<OrderOilDepotSchemeVer> schemeVers = orderOilDepotSchemeVerService.getOrderOilDepotSchemeVerByOrderId(orderId, true, loginInfo);
            List<OrderOilDepotScheme> schemes = new ArrayList<OrderOilDepotScheme>();
            for (OrderOilDepotSchemeVer orderOilDepotSchemeVer : schemeVers) {
                OrderOilDepotScheme scheme = new OrderOilDepotScheme();
                BeanUtil.copyProperties(orderOilDepotSchemeVer, scheme);
                schemes.add(scheme);
            }
            out.setSchemes(schemes);
            //     IOrderDriverSubsidySV orderDriverSubsidySV = (IOrderDriverSubsidySV) SysContexts.getBean("orderDriverSubsidySV");
            Long subSidyFee = orderDriverSubsidyService.findOrderDriverSubSidyFee(orderId, null, orderSchedulerVer.getCarDriverId(), orderSchedulerVer.getCopilotUserId(), true, null);
            orderFeeExtVer.setDriverSwitchSubsidy(subSidyFee);//重置版本司机补贴
            List<Map> driverSubsidyDays = new ArrayList<>();
            if (StringUtils.isNotBlank(orderFeeExtVer.getSubsidyTime())) {
                Map map = new HashMap();
                map.put("userName", orderSchedulerVer.getCarDriverMan());
                map.put("subsidyFee", orderFeeExtVer.getSalary());
                map.put("subsidyDay", orderFeeExtVer.getSubsidyTime());
                driverSubsidyDays.add(map);
            }
            if (StringUtils.isNotBlank(orderFeeExtVer.getCopilotSubsidyTime())) {
                Map map = new HashMap();
                map.put("userName", orderSchedulerVer.getCopilotMan());
                map.put("subsidyFee", orderFeeExtVer.getCopilotSalary());
                map.put("subsidyDay", orderFeeExtVer.getCopilotSubsidyTime());
                driverSubsidyDays.add(map);
            }
            if (orderSchedulerVer.getVehicleClass() != null && orderSchedulerVer.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                List<OrderDriverSwitchInfo> switchInfos = iOrderDriverSwitchInfoService.getSwitchInfosByOrder(orderInfoVer.getOrderId(), OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                Long driverSwitchSubsidy = 0L;
                if (switchInfos != null && switchInfos.size() > 0) {
                    for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
                        if (orderDriverSwitchInfo.getReceiveUserId() != null &&
                                !orderDriverSwitchInfo.getReceiveUserId().equals(orderSchedulerVer.getCarDriverId())
                                && !orderDriverSwitchInfo.getReceiveUserId().equals(orderSchedulerVer.getCopilotUserId())) {
                            Float arriveTime = orderSchedulerVer.getArriveTime();
                            if (lineInfoVers != null && lineInfoVers.size() > 0) {
                                for (OrderTransitLineInfoVer orderTransitLineInfo : lineInfoVers) {
                                    arriveTime += orderTransitLineInfo.getArriveTime();
                                }
                            }
                            CopilotMapVo carDriver = iOrderFeeService.culateSubsidy(orderDriverSwitchInfo.getReceiveUserId(),
                                    orderSchedulerVer.getTenantId(), orderSchedulerVer.getDependTime(), arriveTime,
                                    orderSchedulerVer.getOrderId(), false, lineInfoVers == null ? 0 : lineInfoVers.size());
                            String subsidyDate = "";
                            Integer subsidyDay = 0;
                            if (StringUtils.isNotBlank(OrderUtil.objToStringEmpty(carDriver.getCopilotSubsidyTime()))) {
                                String[] subsidyTimeArr = OrderUtil.objToStringEmpty(carDriver.getCopilotSubsidyTime()).split(" ");
                                Long subsidy = carDriver.getCopilotDaySalary();
                                Date currDate = new Date();
                                for (String subsidyTime : subsidyTimeArr) {
                                    if (StringUtils.isBlank(subsidyTime)) {
                                        continue;
                                    }
                                    try {
                                        LocalDateTime localDateTime = LocalDateTimeUtil.convertStringToDateYMD(DateUtil.getYear(currDate) + "-" + subsidyTime);
                                        //1.判断司机是否当天存在补贴
                                        List list = iOrderDriverSubsidyService.findDriverSubsidys(localDateTime, null, null, orderDriverSwitchInfo.getReceiveUserId(), orderSchedulerVer.getTenantId(), false, orderId);
                                        if (list == null || list.size() == 0) {
                                            list = iOrderDriverSubsidyService.findDriverSubsidys(localDateTime, null, null, orderDriverSwitchInfo.getReceiveUserId(), orderSchedulerVer.getTenantId(), true, orderId);
                                        }
                                        if (list == null || list.size() == 0) {
                                            subsidyDate += " " + subsidyTime;
                                            subsidyDay++;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (StringUtils.isNotBlank(subsidyDate)) {
                                    Map map = new HashMap();
                                    map.put("userName", orderDriverSwitchInfo.getReceiveUserName());
                                    driverSwitchSubsidy += subsidy != null ? subsidyDay * subsidy : 0;
                                    map.put("subsidyFee", subsidy != null ? subsidyDay * subsidy : 0);
                                    map.put("subsidyDay", subsidyDate);
                                    driverSubsidyDays.add(map);
                                }
                            }
                        }
                    }
                }
                orderFeeExtVer.setDriverSwitchSubsidy(driverSwitchSubsidy);
            }
            out.setDriverSubsidyDays(driverSubsidyDays);
        }
        //无成本权限 不加载成本信息
        if (orderCostPermission && orderSchedulerVer.getVehicleClass() != null && orderSchedulerVer.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExtVer.getPaymentWay() != null && orderInfoExtVer.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
            List<OrderOilCardInfoVer> oilCardInfos = orderOilCardInfoVerService.queryOrderOilCardInfoVerByOrderId(orderId, null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
            for (OrderOilCardInfoVer orderOilCardInfoVer: oilCardInfos) {
                orderOilCardInfoVer.setCardTypeName(getSysStaticData("OIL_CARD_TYPE", orderOilCardInfoVer.getCardType() == null ? "" : String.valueOf(orderOilCardInfoVer.getCardType())).getCodeName());
                if(orderOilCardInfoVer.getOilCardNum() != null){
                    List<OilCardManagement> oilCardManagementByCard = iOilCardManagementService.getOilCardManagementByCard(orderOilCardInfoVer.getOilCardNum(), loginInfo.getTenantId());
                    if(oilCardManagementByCard != null && oilCardManagementByCard.size()>0){
                        OilCardManagement oilCardManagement = oilCardManagementByCard.get(0);

                        orderOilCardInfoVer.setCardBalance(oilCardManagement.getCardBalance());

                    }
                }
            }
            out.setOilCardInfos(oilCardInfos);
        }
        try {
            orderSchedulerVer.setClientContractUrl(orderSchedulerVer.getClientContractUrl() == null ? "" : client.getHttpURL(orderSchedulerVer.getClientContractUrl()).split("\\?")[0]);
            if (orderSchedulerVer.getCarLengh() != null && orderSchedulerVer.getCarStatus() != null && orderSchedulerVer.getCarStatus() > 0) {
                orderSchedulerVer.setCarLenghName(vehicleStaticDataUtil.getVehicleLengthNameById(orderSchedulerVer.getCarStatus().toString(), orderSchedulerVer.getCarLengh()));
            }
            if (orderSchedulerVer.getCarStatus() != null && orderSchedulerVer.getCarStatus() > 0) {
                orderSchedulerVer.setCarStatusName(readisUtil.getSysStaticDatabyId("VEHICLE_STATUS", orderSchedulerVer.getCarStatus().toString()).getCodeName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (orderCostPermission) {//成本权限
            out.setTotalOilFee((orderFeeVer.getPreOilFee() == null ? 0 : orderFeeVer.getPreOilFee()) + (orderFeeVer.getPreOilVirtualFee() == null ? 0 : orderFeeVer.getPreOilVirtualFee()));
            /**
             * 获取司机补贴
             */
            String[] subsidyArr = orderDriverSubsidyService.getDriverSubsidyVer(orderId, loginInfo.getTenantId());
            orderFeeExtVer.setSubsidyTime(subsidyArr[0]);
            orderFeeExtVer.setCopilotSubsidyTime(subsidyArr[1]);
        }
        out.setOrderFeeExtVer(orderFeeExtVer);
        if(orderFeeVer.getPriceEnum() != null){
            String name = getSysStaticData("PRICE_ENUM", orderFeeVer.getPriceEnum().toString()).getCodeName();
            orderFeeVer.setPriceEnumName(name);
        }
        //自有车，报账模式，已支付状态应累加orderFee的油账号充值金额preOilVirtualFee
        if(orderSchedulerVer.getVehicleClass() != null && orderSchedulerVer.getVehicleClass() == 1){
            if(orderInfoExtVer.getPaymentWay() != null && orderInfoExtVer.getPaymentWay() == 2){
                if(orderInfoExtVer.getPreAmountFlag() != null && orderInfoExtVer.getPreAmountFlag() == 1){
                    Long preOilVirtualFeeVer = orderFeeVer.getPreOilVirtualFee();
                    OrderFee orderFee = iOrderFeeService.getOrderFee(orderFeeVer.getOrderId());
                    Long preOilVirtualFee = orderFee.getPreOilVirtualFee();
                    if(preOilVirtualFee == null ){
                        preOilVirtualFee = 0L;
                    }
                    if(preOilVirtualFeeVer == null ){
                        preOilVirtualFeeVer = 0L;
                    }
                    orderFeeVer.setPreOilVirtualFee(preOilVirtualFee + preOilVirtualFeeVer);
                }
            }
        }
        out.setOrderFeeVer(orderFeeVer);
        if(orderGoodsVer.getLineName() != null && !orderGoodsVer.getLineName().equals("")){
            orderGoodsVer.setLinkName(orderGoodsVer.getLineName());
        }
        if(orderGoodsVer.getLinePhone() != null && !orderGoodsVer.getLinePhone().equals("")){
            orderGoodsVer.setLinkPhone(orderGoodsVer.getLinePhone());
        }
        if(orderGoodsVer.getReciveProvinceId() != null){
            String name = getSysStaticData("SYS_PROVINCE", orderGoodsVer.getReciveProvinceId().toString()).getCodeName();
            orderGoodsVer.setReciveProvinceName(name);
        }
        if(orderGoodsVer.getReciveCityId() != null){
            String name1 = getSysStaticData("SYS_CITY", orderGoodsVer.getReciveCityId().toString()).getCodeName();
            orderGoodsVer.setReciveCityName(name1);
        }
        if (orderGoodsVer.getVehicleLengh() != null && orderGoodsVer.getVehicleStatus() != null && orderGoodsVer.getVehicleStatus() > 0) {
            orderGoodsVer.setVehicleLenghName(vehicleStaticDataUtil.getVehicleLengthName(orderGoodsVer.getVehicleStatus().toString(), orderGoodsVer.getVehicleLengh()));
        }
        out.setOrderGoodsVer(orderGoodsVer);
        if(orderInfoExtVer.getPaymentWay() != null){
            String name1 = getSysStaticData("PAYMENT_WAY", orderInfoExtVer.getPaymentWay().toString()).getCodeName();
            orderInfoExtVer.setPaymentWayName(name1);
        }
        out.setOrderInfoExtVer(orderInfoExtVer);
        if(orderInfoVer.getOrgId() != null){
            String orgNameByOrgId = sysOrganizeService.getOrgNameByOrgId(orderInfoVer.getOrgId().longValue(), loginInfo.getTenantId());
            if(orgNameByOrgId != null && !orgNameByOrgId.equals("")){
                orderInfoVer.setOrgIdName(orgNameByOrgId);
            }
        }
        if(orderSchedulerVer.getVehicleClass() != null && (orderSchedulerVer.getVehicleClass() ==2 ||
                orderSchedulerVer.getVehicleClass() ==3)){
            orderInfoVer.setTenantId(orderInfoVer.getToTenantId());
            orderInfoVer.setToTenantId(null);
        }
        out.setOrderInfoVer(orderInfoVer);
        orderSchedulerVer.setOnDutyDriverId(orderSchedulerVer.getCarDriverId());
        orderSchedulerVer.setOnDutyDriverName(orderSchedulerVer.getCarDriverMan());
        orderSchedulerVer.setOnDutyDriverPhone(orderSchedulerVer.getCarDriverPhone());
        if(orderSchedulerVer.getVehicleClass() != null){
            String name = sysStaticDataRedisUtils.getSysStaticDataByCodeValue("VEHICLE_CLASS", orderSchedulerVer.getVehicleClass() + "").getCodeName();
            orderSchedulerVer.setVehicleClassName(name);
        }
        //orderSchedulerVer.setOnDutyDriverId(null);
        //orderSchedulerVer.setOnDutyDriverPhone("");
        //orderSchedulerVer.setOnDutyDriverName("");
        out.setOrderSchedulerVer(orderSchedulerVer);

        if (CollectionUtils.isNotEmpty(out.getOilCardInfos())) {
            List<OrderOilCardInfoVerDto> oilCardInfoDtos = new ArrayList<>();
            for (OrderOilCardInfoVer oilCardInfo : out.getOilCardInfos()) {
                OrderOilCardInfoVerDto dto = new OrderOilCardInfoVerDto();
                BeanUtil.copyProperties(oilCardInfo, dto);
                oilCardInfoDtos.add(dto);
            }
            out.setOilCardInfoDtos(oilCardInfoDtos);
        }
        return out;
    }

    @Override
    public void actionOrder(String action, String orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        this.loading(Long.parseLong(orderId), action, loginInfo.getName(), new Date(), null, accessToken);
    }

    @Override
    public void loading(long orderId, String action, String desc, Date date, String redis, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderInfo orderInfo = getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("找不到订单[" + orderId + "]信息");
        }
        OrderScheduler scheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        OrderGoods orderGoods = iOrderGoodsService.getOrderGoods(orderId);
        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
        if (scheduler.getVehicleCode() == null || scheduler.getVehicleCode() <= 0) {
            throw new BusinessException("订单未调度车辆，不能操作靠台！");
        }
        //校验转单的订单，开单方靠台是否在接单之前
        if (orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0) {
            OrderInfo reciveOrder = getOrder(orderInfo.getToOrderId());
            if (reciveOrder != null && (scheduler.getCarDependDate() == null || reciveOrder.getCreateTime().isAfter(scheduler.getCarDependDate()))) {
                throw new BusinessException("承运方是在靠台前接单，本操作只能由承运方[" + orderInfo.getToTenantName() + "]操作！");
            }
        }
        //开票校验
        iOrderSchedulerService.orderLoadingVerify(orderInfo, scheduler, false);
        iOrderOpRecordService.saveOrUpdate(orderId, OrderConsts.OrderOpType.TRAIL_CHANGE, accessToken);
        boolean isTransit = false;
        if (StringUtils.isNotBlank(action) && "depend".equals(action)) {
            // 靠台操作 待装货－>装货中
            String fromDes = "";
            if (scheduler.getCarDependDate() == null && orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.LOADING);
                scheduler.setCarDependDate(getDateToLocalDateTime(date));
                TenantVehicleRel vehicleRel = iTenantVehicleRelService.getTenantVehicleRel(scheduler.getVehicleCode(),
                        orderInfo.getTenantId());
                // 更新滞留信息
                if (vehicleRel != null) {
                    vehicleRel.setCurrOrder(orderInfo.getOrderId());
                    iTenantVehicleRelService.saveOrUpdate(vehicleRel);
                }
                if (scheduler.getVehicleClass() != null
                        && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    if (scheduler.getLicenceType() != null
                            && scheduler.getLicenceType().intValue() == SysStaticDataEnum.LICENCE_TYPE.TT) {
                        // 到达 如果有挂车更新挂车位置
                        if (scheduler.getTrailerId() != null && scheduler.getTrailerId() > 0) {
                            TrailerManagement management = iTrailerManagementService.getTrailerManagement(scheduler.getTrailerId());
                            if (management != null) {
                                management.setIsState(SysStaticDataEnum.TRAILER_TYPE.TRAILER_TYPE1);
                                iTrailerManagementService.saveOrUpdate(management);
                            }
                        }
                    }
                }
                // 操作日志
                String sourceRegionName = "";
                String name = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                if (StringUtils.isNotBlank(name)) {
                    sourceRegionName = name;
                }
                fromDes = "订单车辆已到达装货点(" + sourceRegionName + ")";
                saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                        "[" + desc + "]已到达装货点(" + sourceRegionName + ")"
                        , accessToken, orderInfo.getOrderId());
                //离场同步56K
                OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
                iOrderFeeService.syncOrderTrackTo56K(orderInfo, orderFee, accessToken);
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TRANSPORT_ING) {
                OrderTransitLineInfo orderTransitLineInfo = iOrderTransitLineInfoService.queryTrackTransitLineInfo(orderId, OrderConsts.TRACK_TYPE.TYPE1);
                if (orderTransitLineInfo != null) {
                    OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
                    List<OrderTransitLineInfo> transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
                    iOrderSchedulerService.verifyTrackNode(scheduler, orderGoods, orderInfo, transitLineInfos, orderTransitLineInfo.getNand(), orderTransitLineInfo.getEand()
                            , date, orderTransitLineInfo.getSortId(), false, orderFee.getVehicleAffiliation(), accessToken);
                    orderTransitLineInfo.setCarDependDate(getDateToLocalDateTime(date));
                    iOrderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                    String regionName = "";
                    String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                    if (StringUtils.isNotBlank(name)) {
                        regionName = name;
                    }
                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                            "[" + desc + "]操作车辆已到达经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")"
                            , accessToken, orderInfo.getOrderId());
                    fromDes = "车辆已到达经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")";
                    isTransit = true;
                } else {
                    throw new BusinessException("订单轨迹状态有误，请刷新页面！");
                }
            }
            iOrderInfoService.saveOrUpdate(orderInfo);
            iOrderSchedulerService.saveOrUpdate(scheduler);
            //反写来源订单信息
            this.updateFromOrderState(orderInfo, OrderConsts.ORDER_STATE.LOADING, fromDes, date, isTransit, false, null, accessToken);
            //更新订单应收应付时间
            this.updateSettleDueDate(scheduler, orderInfo, null, loginInfo);
        } else if (StringUtils.isNotBlank(action) && "leave".equals(action)) {
            // 离场操作 装货中－>运输中
            String fromDes = "";
            if (scheduler.getCarStartDate() == null && orderInfo.getOrderState() == OrderConsts.ORDER_STATE.LOADING) {
                if (orderInfo.getOrderState() == null || orderInfo.getOrderState() != OrderConsts.ORDER_STATE.LOADING) {
                    throw new BusinessException("订单状态有误，请刷新页面！");
                }
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.TRANSPORT_ING);
                // 实际出发时间
                scheduler.setCarStartDate(getDateToLocalDateTime(date));
                // 操作日志
                saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                        "[" + desc + "]完成装货操作", accessToken, orderInfo.getOrderId());
                fromDes = "订单已装货";
                iOrderInfoService.saveOrUpdate(orderInfo);
                iOrderSchedulerService.saveOrUpdate(scheduler);
            } else {
                OrderTransitLineInfo orderTransitLineInfo = iOrderTransitLineInfoService.queryTrackTransitLineInfo(orderId, OrderConsts.TRACK_TYPE.TYPE2);
                if (orderTransitLineInfo != null) {
                    orderTransitLineInfo.setCarStartDate(getDateToLocalDateTime(date));
                    iOrderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                    isTransit = true;
                    String regionName = "";
                    String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                    if (StringUtils.isNotBlank(name)) {
                        regionName = name;
                    }
                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                            "[" + desc + "]操作车辆离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")"
                            , accessToken, orderInfo.getOrderId());
                    fromDes = "车辆离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")";
                } /*else {
                    throw new BusinessException("订单轨迹状态有误，请刷新页面！");
                }*/
            }
            this.updateFromOrderState(orderInfo, OrderConsts.ORDER_STATE.TRANSPORT_ING, fromDes, date, isTransit, false, null, accessToken);
            //更新订单应收应付时间
            this.updateSettleDueDate(scheduler, orderInfo, null, loginInfo);
        } else if (StringUtils.isNotBlank(action) && "arrive".equals(action)) {
            // 到场 运输中－>已到达
//            if (orderInfo.getOrderState() == null || orderInfo.getOrderState() != OrderConsts.ORDER_STATE.TRANSPORT_ING) {
//                throw new BusinessException("订单状态有误，请刷新页面！");
//            }
            if (scheduler.getVehicleClass() != null
                    && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (scheduler.getLicenceType() != null && scheduler.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
                    // 到达 如果有挂车更新挂车位置
                    if (scheduler.getTrailerId() != null && scheduler.getTrailerId() > 0) {
                        TrailerManagement management = iTrailerManagementService.getTrailerManagement(scheduler.getTrailerId());
                        if (management != null) {
                            management.setIsState(SysStaticDataEnum.TRAILER_TYPE.TRAILER_TYPE2);
                            iTrailerManagementService.saveOrUpdate(management);
                        }
                        //更新挂车位置
                        iTrailerManagementService.updateLoaction(scheduler.getTrailerId(), orderGoods.getEandDes(), orderGoods.getNandDes()
                                , orderInfo.getDesProvince() == null ? -1 : orderInfo.getDesProvince(),
                                orderInfo.getDesRegion() == null ? -1 : orderInfo.getDesRegion(),
                                orderInfo.getDesCounty() == null ? -1 : orderInfo.getDesCounty());
                    }
                }
            }
            OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
            List<OrderTransitLineInfo> transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            iOrderSchedulerService.verifyTrackNode(scheduler, orderGoods, orderInfo, transitLineInfos, orderGoods.getNandDes(), orderGoods.getEandDes()
                    , date, null, false, orderFee.getVehicleAffiliation(), accessToken);
            scheduler.setCarArriveDate(getDateToLocalDateTime(date));
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.ARRIVED);
            //记录终结操作
            iOrderFeeService.syncBillForm(orderInfo, orderFee, OrderConsts.SYNC_TYPE.FINALITY, accessToken);

            this.addOrderIdToCache(orderInfo.getOrderId(), orderInfo.getTenantId());
            if (scheduler.getVehicleClass() != null
                    && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                // 实际运行时间
                Date relRunTime = iOrderSchedulerService.queryOrderTrackDate(orderId, OrderConsts.TRACK_TYPE.TYPE3, loginInfo.getTenantId());
                orderInfoExt.setRelRunTime(relRunTime.getTime());
                iOrderInfoExtService.saveOrUpdate(orderInfoExt);
            }
            // 记录订单操作记录 用于车辆反写
            if (scheduler.getVehicleCode() != null && scheduler.getVehicleCode() > 0) {
                iOrderStateTrackOperService.saveOrUpdate(orderId, scheduler.getVehicleCode(), OrderConsts.OrderOpType.TRAIL_CHANGE);
            }
            // 操作日志
            String desRegionName = "";
            String name = getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
            if (StringUtils.isNotBlank(name)) {
                desRegionName = name;
            }
            this.updateFromOrderState(orderInfo, OrderConsts.ORDER_STATE.ARRIVED, "订单已到达卸货点(" + desRegionName + ")", date, isTransit, false, null, accessToken);
            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo,
                    SysOperLogConst.OperType.Update, "[" + desc + "]操作车辆到达卸货点(" + desRegionName + ")"
                    , accessToken, orderInfo.getOrderId());
            iOrderInfoService.saveOrUpdate(orderInfo);
            iOrderSchedulerService.saveOrUpdate(scheduler);
            //更新订单应收应付时间
            this.updateSettleDueDate(scheduler, orderInfo, null, loginInfo);
            //离场同步56K
            iOrderFeeService.syncOrderTrackTo56K(orderInfo, orderFee, accessToken);
        } else if (StringUtils.isNotBlank(action) && "departure".equals(action)) {
            // 离场 已到达－>回单审核
//            if (orderInfo.getOrderState() == null || orderInfo.getOrderState() != OrderConsts.ORDER_STATE.ARRIVED) {
//                throw new BusinessException("订单状态有误，请刷新页面！");
//            }
            TenantVehicleRel vehicleRel = iTenantVehicleRelService.getTenantVehicleRel(scheduler.getVehicleCode(),
                    orderInfo.getTenantId());
            // 更新滞留信息
            if (vehicleRel != null) {
                vehicleRel.setBeforeOrder(orderInfo.getOrderId());
                vehicleRel.setCurrOrder(null);
                iTenantVehicleRelService.saveOrUpdate(vehicleRel);
            }
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT);
            scheduler.setCarDepartureDate(getDateToLocalDateTime(date));
            // 操作日志
            this.updateFromOrderState(orderInfo, OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT, "订单已卸货", date, isTransit, false, null, accessToken);
            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update, "[" + desc + "]完成已卸货操作", accessToken, orderInfo.getOrderId());
            iOrderInfoService.saveOrUpdate(orderInfo);
            iOrderSchedulerService.saveOrUpdate(scheduler);
            //更新订单应收应付时间
            this.updateSettleDueDate(scheduler, orderInfo, null, loginInfo);
            iOrderSchedulerService.compensationTrackDate(orderId, accessToken);//离场补偿
            //离场同步56K
            OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
            iOrderFeeService.syncOrderTrackTo56K(orderInfo, orderFee, accessToken);
        }
        iOrderSchedulerService.setOrderLoncationInfo(orderGoods, scheduler);
    }

    @Override
    public void updateFromOrderState(OrderInfo orderInfo, int orderState, String desc, Date opDate, Boolean isTransit, boolean isGps, Integer member, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
            OrderInfo fromOrder = iOrderInfoService.getOrder(orderInfo.getFromOrderId());
            if (fromOrder != null) {
                if (!isTransit && fromOrder.getOrderState() >= orderState) {
                    return;
                }
                boolean ischange = false;
                OrderScheduler fromScheduler = iOrderSchedulerService.getOrderScheduler(orderInfo.getFromOrderId());
                if (orderState == OrderConsts.ORDER_STATE.LOADING) {// 靠台
                    if (isTransit) {
                        OrderTransitLineInfo orderTransitLineInfo = null;
                        if (isGps) {
                            List<OrderTransitLineInfo> transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(fromOrder.getOrderId());
                            if (transitLineInfos != null && transitLineInfos.size() > 0 && transitLineInfos.size() >= member.intValue()) {
                                orderTransitLineInfo = transitLineInfos.get(member.intValue() - 1);
                            }
                        } else {
                            orderTransitLineInfo = iOrderTransitLineInfoService.queryTrackTransitLineInfo(fromOrder.getOrderId(), OrderConsts.TRACK_TYPE.TYPE1);
                        }
                        if (orderTransitLineInfo != null && orderTransitLineInfo.getCarDependDate() == null) {
                            orderTransitLineInfo.setCarDependDate(getDateToLocalDateTime(opDate));
                            iOrderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                            String regionName = "";
                            String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                            if (StringUtils.isNotBlank(name)) {
                                regionName = name;
                            }
                            desc = "操作车辆已到达装货点(" + regionName + ")";
                            isTransit = true;
                            ischange = true;
                        } else {
                            if (!isGps) {
                                throw new BusinessException("订单轨迹状态有误，请刷新页面！");
                            }
                        }
                    } else if (fromScheduler.getCarDependDate() == null) {
                        if (fromOrder.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                            fromOrder.setOrderState(OrderConsts.ORDER_STATE.LOADING);
                        }
                        fromScheduler.setCarDependDate(getDateToLocalDateTime(opDate));
                        ischange = true;
                    }
                } else if (orderState == OrderConsts.ORDER_STATE.TRANSPORT_ING) {// 离台
                    if (isTransit) {
                        OrderTransitLineInfo orderTransitLineInfo = null;
                        if (isGps) {
                            List<OrderTransitLineInfo> transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(fromOrder.getOrderId());
                            if (transitLineInfos != null && transitLineInfos.size() > 0 && transitLineInfos.size() >= member.intValue()) {
                                orderTransitLineInfo = transitLineInfos.get(member.intValue() - 1);
                            }
                        } else {
                            orderTransitLineInfo = iOrderTransitLineInfoService.queryTrackTransitLineInfo(fromOrder.getOrderId(), OrderConsts.TRACK_TYPE.TYPE2);
                        }
                        if (orderTransitLineInfo != null) {
                            if (orderTransitLineInfo.getCarDependDate() == null) {
                                orderTransitLineInfo.setCarDependDate(getDateToLocalDateTime(opDate));
                                iOrderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                                isTransit = true;
                                ischange = true;
                                String regionName = "";
                                String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                                if (StringUtils.isNotBlank(name)) {
                                    regionName = name;
                                }
                                desc = "车辆靠台装货点(" + regionName + ")";
                            } else if (orderTransitLineInfo.getCarStartDate() == null) {
                                orderTransitLineInfo.setCarStartDate(getDateToLocalDateTime(opDate));
                                iOrderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                                isTransit = true;
                                ischange = true;
                                String regionName = "";
                                String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                                if (StringUtils.isNotBlank(name)) {
                                    regionName = name;
                                }
                                desc = "车辆离台装货点(" + regionName + ")";
                            }
                        } else {
                            throw new BusinessException("订单轨迹状态有误，请刷新页面！");
                        }
                    } else if (fromScheduler.getCarStartDate() == null) {
                        if (fromOrder.getOrderState() == OrderConsts.ORDER_STATE.LOADING) {
                            fromOrder.setOrderState(OrderConsts.ORDER_STATE.TRANSPORT_ING);
                        }
                        fromScheduler.setCarStartDate(getDateToLocalDateTime(opDate));
                        ischange = true;
                    }
                } else if (orderState == OrderConsts.ORDER_STATE.ARRIVED && fromScheduler.getCarArriveDate() == null) {// 到达
                    if (fromOrder.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD && fromOrder.getOrderState() < OrderConsts.ORDER_STATE.ARRIVED) {
                        //记录终结操作
                        OrderFee fromOrderFee = iOrderFeeService.getOrderFee(orderInfo.getFromOrderId());
                        iOrderFeeService.syncBillForm(fromOrder, fromOrderFee, OrderConsts.SYNC_TYPE.FINALITY, accessToken);
                        fromOrder.setOrderState(OrderConsts.ORDER_STATE.ARRIVED);
                        this.addOrderIdToCache(fromOrder.getOrderId(), fromOrder.getTenantId());
                    }
                    fromScheduler.setCarArriveDate(getDateToLocalDateTime(opDate));
                    if (fromScheduler.getVehicleClass() != null && fromScheduler
                            .getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderInfo.getFromOrderId());
                        // 实际运行时间
                        Date relRunTime = iOrderSchedulerService.queryOrderTrackDate(fromScheduler.getOrderId(), OrderConsts.TRACK_TYPE.TYPE3, loginInfo.getTenantId());
                        orderInfoExt.setRelRunTime(relRunTime.getTime());
                        iOrderInfoExtService.saveOrUpdate(orderInfoExt);
                    }
                    ischange = true;
                } else if (orderState == OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT && fromScheduler.getCarDepartureDate() == null) {// 离场
                    fromOrder.setOrderState(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT);
                    fromScheduler.setCarDepartureDate(getDateToLocalDateTime(opDate));
                    //离场同步56K
                    OrderFee fromOrderFee = iOrderFeeService.getOrderFee(orderInfo.getFromOrderId());
                    iOrderFeeService.syncOrderTrackTo56K(fromOrder, fromOrderFee, accessToken);
                    ischange = true;
                }
                if (ischange) {
                  sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.OrderInfo, orderInfo.getFromOrderId(),
                          SysOperLogConst.OperType.Update, desc, fromOrder.getTenantId(),opDate);
                    iOrderInfoService.saveOrUpdate(fromOrder);
                    iOrderSchedulerService.saveOrUpdate(fromScheduler);
                }

                this.updateFromOrderState(fromOrder, orderState, desc, opDate, isTransit,isGps,member,accessToken);
            }
        }
    }

    @Override
    public void updateSettleDueDate(OrderScheduler orderScheduler, OrderInfo orderInfo, Date reciveDate, LoginInfo loginInfo) {
        OrderPaymentDaysInfo costPaymentDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        OrderPaymentDaysInfo incomePaymentDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        //计算应收应付时间
        Date shouldPayDate = getLocalDateTimeToDate(iOrderSchedulerService.getOrderReceivableDate(orderScheduler, orderInfo, costPaymentDaysInfo, getDateToLocalDateTime(reciveDate), loginInfo));
        Date receivableDate = getLocalDateTimeToDate(iOrderSchedulerService.getOrderReceivableDate(orderScheduler, orderInfo, incomePaymentDaysInfo, getDateToLocalDateTime(reciveDate), loginInfo));
        //反写订单应收应付时间
        orderFeeStatementService.updateReceiveSettleDueDate(orderInfo.getOrderId(), getDateToLocalDateTime(receivableDate));
        orderFeeStatementService.updatePaySettleDueDate(orderInfo.getOrderId(), getDateToLocalDateTime(shouldPayDate));
    }

    @Transactional
    @Override
    public void verifyPayPass(Long orderId, String auditCode, String verifyDesc, List<String> oilCardNums, String receiptsStr, Integer reciveType, String accessToken) {
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("业务审核类型不能为空！");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        OrderInfo orderInfo = iOrderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("未找到订单号为[" + orderId + "]在途订单，请刷新页面！");
        }
        if (orderInfo.getOrderUpdateState() != null
                && orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
            throw new BusinessException("订单处于修改审核中,不能支付！");
        }
        if ("[]".equals(receiptsStr)) {
            receiptsStr = "";
        }
        boolean sure = false;
        boolean isNotAudit = false;
        try {
            AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(auditCode, orderId, verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
            if (auditCallbackDto != null && auditCallbackDto.getIsNext()) {
                sure = auditCallbackDto.getIsNext();
            }
            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
                sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
            }
        } catch (BusinessException e) {
            if ("该业务数据不能审核".equals(e.getMessage())) {
                isNotAudit = true;
            } else {
                throw new BusinessException(e.getMessage());
            }
        }
        if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {
            if (!sure) {
                iOrderFeeService.payProFee(orderId, oilCardNums, accessToken);
            } else {
                verifyOilCardNum(orderInfo, oilCardNums, accessToken);
            }
        } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)) {
            if (isNotAudit) {
                iOrderFeeService.payArriveFee(orderId, accessToken);
            }
        } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)) {
            if (!sure) {
                iOrderReceiptService.verifyRece(String.valueOf(orderId), false, false, verifyDesc, receiptsStr, reciveType, accessToken, false);
            }
        } else {
            throw new BusinessException("业务审核类型异常！");
        }

        // 特殊处理 为 报账模式 添加订单账户表记录 付款方式 1:包干模式 2:实报实销模式 3:承包模式
        Integer flag = 0;
        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
        if (orderInfoExt != null) {
            if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 2) {
                flag = orderInfoExt.getPaymentWay();
            }
        } else {
            OrderInfoExtH orderInfoExtH = iOrderInfoExtHService.getOrderInfoExtH(orderId);
            if (orderInfoExtH != null) {
                if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == 2) {
                    flag = orderInfoExtH.getPaymentWay();
                }
            }
        }

        if(flag != 0) {
            LoginInfo loginInfo = loginUtils.get(accessToken);
            OrderAccountOilSource orderAccount = new OrderAccountOilSource();
//            OrderAccount orderAccount = new OrderAccount();

            OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
            if (orderScheduler.getId() != null) {
                orderAccount.setUserId(orderScheduler.getCarDriverId());
            } else {
                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
                if (orderSchedulerH != null) {
                    orderAccount.setUserId(orderSchedulerH.getCarDriverId());
                }
            }
            Long oilBalance = 0L; // 油卡金额
            OrderFee orderFeeByOrderId = iOrderFeeService.getOrderFeeByOrderId(orderId);
            if (orderFeeByOrderId != null) {
                oilBalance += orderFeeByOrderId.getPreOilVirtualFee();
            } else {
                OrderFeeH orderFeeH = iOrderFeeHService.getOrderFeeH(orderId);
                if (orderFeeH != null) {
                    oilBalance += orderFeeH.getPreOilVirtualFee();
                }
            }
//            orderAccount.setUserId(orderScheduler.getCarDriverId());
//            orderAccount.setUserType(3);
//            orderAccount.setUpdateOpId(loginInfo.getId());
//            orderAccount.setUpdateTime(LocalDateTime.now());
//            orderAccount.setOilBalance(0L);
//            orderAccount.setTenantId(loginInfo.getTenantId());
//            orderAccount.setRechargeOilBalance(oilBalance);
//            orderAccount.setOpId(loginInfo.getId());
//            orderAccount.setCreateTime(LocalDateTime.now());
//            orderAccount.setId(null);
//            orderAccountOilSourceService.save(orderAccount);
        }

    }
    @Override
    public String verifyPayFail(Long orderId, String auditCode, String verifyDesc, boolean load, boolean receipt, String accessToken) {
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("业务审核类型不能为空！");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        boolean isNotAudit = false;
        try {
            AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(auditCode, orderId, verifyDesc, AuditConsts.RESULT.FAIL, accessToken);
            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
                fail(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCode, auditCallbackDto.getParamsMap(), accessToken);
            }
        } catch (BusinessException e) {
            if ("该业务数据不能审核".equals(e.getMessage())) {
                isNotAudit = true;
            } else {
                throw new BusinessException(e.getMessage());
            }
        }
        if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {

        } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)) {

        } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)) {
            if (!load && !receipt) {
                throw new BusinessException("回单审核状态异常！");
            }
            iOrderReceiptService.verifyRece(String.valueOf(orderId), load, receipt, verifyDesc, null, OrderConsts.RECIVE_TYPE.SINGLE, accessToken);
        } else {
            throw new BusinessException("业务审核类型异常！");
        }
        if (isNotAudit) {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderId);
            params.put("auditCode", auditCode);
            auditService.startProcess(auditCode, orderId, SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
        }
        return "Y";
    }

    @Override
    public void checkOilCarType(OrderFee orderFee, Long tenantId) {
        //预付款的等值卡校验
        if (orderFee.getPrePayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()) {

            if (StringUtils.isBlank(orderFee.getPrePayEquivalenceCardNumber())) {
                throw new BusinessException("预付等值卡号不能为空，请填写！");
            }

            if (!iOilCardManagementService.checkCardNum(orderFee.getPrePayEquivalenceCardNumber(), tenantId)) {
                throw new BusinessException("预付等值卡已是供应商油卡，不能添加");
            }

        }

        //预付款的等值卡校验
        if (orderFee.getAfterPayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()) {

            if (StringUtils.isBlank(orderFee.getAfterPayEquivalenceCardNumber())) {
                throw new BusinessException("尾款等值卡号不能为空，请填写！");
            }

            if (!iOilCardManagementService.checkCardNum(orderFee.getAfterPayEquivalenceCardNumber(), tenantId)) {
                throw new BusinessException("尾款等值卡已是供应商油卡，不能添加");
            }
        }
    }

    @Override
    public void canel(long orderId, Long problemId, boolean isCheck, String accessToken) {
        OrderInfo orderInfo = iOrderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("找不到订单[" + orderId + "]信息");
        }
        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
        if (isCheck) {
            if (problemId == null) {
                if (orderInfo.getOrderState() == null || (OrderConsts.ORDER_STATE.DISPATCH_ING != orderInfo.getOrderState()
                        && OrderConsts.ORDER_STATE.AUDIT_NOT != orderInfo.getOrderState()
                        && OrderConsts.ORDER_STATE.BILL_NOT != orderInfo.getOrderState()
                        && OrderConsts.ORDER_STATE.TO_BE_AUDIT != orderInfo.getOrderState()
                        && OrderConsts.ORDER_STATE.TO_BE_RECIVE != orderInfo.getOrderState()
                        && OrderConsts.ORDER_STATE.TO_BE_DISPATCH != orderInfo.getOrderState()
                )) {
                    throw new BusinessException("该订单无法取消");
                }
            }
        }
        List<OrderTransferInfo> transferInfos = iOrderTransferInfoService.queryTransferInfoList(null, null, orderId);
        for (OrderTransferInfo orderTransferInfo : transferInfos) {
            if (orderTransferInfo.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                orderTransferInfo.setTransferOrderState(OrderConsts.TransferOrderState.BILL_TIME_OUT);
                iOrderTransferInfoService.saveOrUpdate(orderTransferInfo);
            }
        }
        List<OrderDriverSwitchInfo> switchInfos = iOrderDriverSwitchInfoService.getSwitchInfosByOrder(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE0);
        for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
            if (orderDriverSwitchInfo.getState() != null && orderDriverSwitchInfo.getState().intValue() == OrderConsts.DRIVER_SWIICH_STATE.STATE0) {
                orderDriverSwitchInfo.setState(OrderConsts.DRIVER_SWIICH_STATE.STATE2);
                iOrderDriverSwitchInfoService.saveOrUpdate(orderDriverSwitchInfo);
            }
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderScheduler scheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        /** 时效罚款处理 需全部取消 **/
        List<OrderAgingInfo> agingInfos = iOrderAgingInfoService.queryAgingInfoByOrderId(orderInfo.getOrderId());
        if (agingInfos != null && agingInfos.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : agingInfos) {
                if (orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                        || orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.AUDIT
                ) {
                    if (problemId != null) {
                        orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
                        iOrderAgingInfoService.saveOrUpdate(orderAgingInfo);
                    }
                    try {
                        auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, orderAgingInfo.getId(), orderAgingInfo.getTenantId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                    OrderAgingAppealInfo appealInfo = iOrderAgingAppealInfoService.getAppealInfoBYAgingId(orderAgingInfo.getId(),
                            false);
                    if (appealInfo != null) {
                        if (appealInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                                || appealInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
                            if (problemId != null) {
                                appealInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
                                iOrderAgingAppealInfoService.saveOrUpdate(appealInfo);
                            }
                            try {
                                auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.AGING_APPEAL_CODE, appealInfo.getId(), appealInfo.getTenantId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        if (problemId != null) {
            //已支付预付款
            if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                //不是来源与接单的才能进行操作 并且有实体油
                if (orderInfo.getOrderType() != null
                        && orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE
                        && orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0) {
                    List<OrderOilCardInfo> cards = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
                    if (cards != null && cards.size() > 0) {
                        for (OrderOilCardInfo orderOilCardInfo : cards) {
                            List<OilCardManagement> oilCards = iOilCardManagementService.getOilCardManagementByCard(orderOilCardInfo.getOilCardNum(), orderInfo.getTenantId());
                            if (oilCards != null && oilCards.size() > 0) {
                                OilCardManagement management = oilCards.get(0);
                                //不是供应商油卡就回收余额
                                if (management.getCardType() != null && management.getCardType() != SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                                    iOilCardManagementService.modifyOilCardBalance(
                                            orderOilCardInfo.getOilCardNum(),
                                            orderOilCardInfo.getOilFee(),
                                            true, scheduler.getPlateNumber(),
                                            scheduler.getCarDriverMan(), orderInfo.getOrderId(), loginInfo.getId(),
                                            orderInfo.getTenantId(), true, loginInfo);
                                }
                                //需要有接单方 已外发
                                if (orderInfo.getIsTransit() != null
                                        && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {
                                    // 有归属租户就回收余额
                                    if (orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0) {
                                        iOilCardManagementService.modifyOilCardBalance(
                                                orderOilCardInfo.getOilCardNum(),
                                                orderOilCardInfo.getOilFee(),
                                                false, scheduler.getPlateNumber(),
                                                scheduler.getCarDriverMan(), orderInfo.getToOrderId(), orderInfo.getTenantId(),
                                                orderInfo.getToTenantId(), true, loginInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        if (scheduler.getVehicleClass() != null
                && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            if (scheduler.getLicenceType() != null
                    && scheduler.getLicenceType().intValue() == SysStaticDataEnum.LICENCE_TYPE.TT) {
                // 到达 如果有挂车更新挂车位置
                if (scheduler.getTrailerId() != null && scheduler.getTrailerId() > 0) {
                    TrailerManagement management = iTrailerManagementService.getTrailerManagement(scheduler.getTrailerId());
                    if (management != null) {
                        management.setIsState(SysStaticDataEnum.TRAILER_TYPE.TRAILER_TYPE2);
                        iTrailerManagementService.saveOrUpdate(management);
                    }
                }
            }
        }
        //报账模式移除分摊节点
        if (scheduler.getVehicleClass() != null
                && (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)
                && orderInfoExt.getPaymentWay() != null
                && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
            iOrderVehicleTimeNodeService.delVehicleTimeNode(orderId, getLocalDateTimeToDate(scheduler.getDependTime()),
                    scheduler.getPlateNumber(), scheduler.getTenantId());
        }
        // 自有车 或者 纯C外调车订单才发送短信
        try {
            if (scheduler.getVehicleClass() != null
                     && (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                     || ((scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                     || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                     || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
             )
                     && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                 Map<String, Object> paraMap = new HashMap<String, Object>();
                 paraMap.put("tenantName", orderInfo.getTenantName());
                 paraMap.put("orderId", orderInfo.getOrderId());
                 paraMap.put("opType", "取消");
                 iSysSmsSendService.sendSms(scheduler.getCarDriverPhone(), EnumConsts.SmsTemplate.ORDER_OPERATE,
                         com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT, com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.ORDER_CANCEL, String.valueOf(orderInfo.getOrderId()), paraMap, accessToken);
             } else if (scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                 //外调车代收模式
                 Map<String, Object> paraMap = new HashMap<String, Object>();
                 paraMap.put("tenantName", orderInfo.getTenantName());
                 paraMap.put("orderId", orderInfo.getOrderId());
                 paraMap.put("opType", "取消");
                 iSysSmsSendService.sendSms(scheduler.getCollectionUserPhone(), EnumConsts.SmsTemplate.ORDER_OPERATE,
                         com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT, com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.ORDER_CANCEL, String.valueOf(orderInfo.getOrderId()), paraMap, accessToken);
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        iOrderOpRecordService.saveOrUpdate(orderId, OrderConsts.OrderOpType.CANCEL_ORDER, accessToken);

        // 记录订单操作记录 用于车辆反写
        if (scheduler.getVehicleCode() != null && scheduler.getVehicleCode() > 0) {
            iOrderStateTrackOperService.saveOrUpdate(orderId, scheduler.getVehicleCode(), OrderConsts.OrderOpType.CANCEL_ORDER);
        }
        boolean judgeOrderIsPaid = iPayoutIntfService.judgeOrderIsPaid(orderId);
        if (!judgeOrderIsPaid) {
            //未打款的取消订单
            iOrderFeeService.syncBillForm(orderInfo, orderFee, OrderConsts.SYNC_TYPE.DELETE, accessToken);
        } else {
            //取消开票订单需同步取消56K订单
            iOrderFeeService.syncOrderTrackTo56K(orderInfo, orderFee, accessToken);
            //开票订单同步回单信息
            iOrderFeeService.syncBillForm(orderInfo, orderFee, OrderConsts.SYNC_TYPE.RECIVE, accessToken);
        }
        try {
            auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
            auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
            auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
            auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
            auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<OrderProblemInfo> problemInfos = iOrderProblemInfoService.getOrderProblemInfoByOrderId(orderId, null);
        if (problemInfos != null && problemInfos.size() > 0) {
            for (OrderProblemInfo orderProblemInfo : problemInfos) {
                if (orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                        || orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
                    try {
                        auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.PROBLEM_CODE, orderProblemInfo.getId(), orderProblemInfo.getTenantId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 取消
        orderInfo.setOrderState(OrderConsts.ORDER_STATE.CANCELLED);
        // 操作日志
        String userName = loginInfo.getName();
        if (orderInfoExt.getIsTempTenant() != null && orderInfoExt.getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
            //临时车队订单取消
            userName = "系统自动";
        }

        OrderFeeExt orderFeeExt = iOrderFeeExtService.getOrderFeeExt(orderId);
        OrderPaymentDaysInfo costPaymentDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.COST);
        if (costPaymentDaysInfo == null) {
            throw new BusinessException("成本账期信息有误，请联系客服！");
        }
        OrderPaymentDaysInfo incomePaymentDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        if (incomePaymentDaysInfo == null) {
            throw new BusinessException("收入账期信息有误，请联系客服！");
        }
        // 订单移历史
        getHisOrderInfo(orderInfo, orderInfoExt, orderFee, orderFeeExt, scheduler, costPaymentDaysInfo, incomePaymentDaysInfo, accessToken);
        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                "[" + userName + "]" + "取消订单", accessToken, orderInfo.getOrderId());
    }

    @Override
    public OrderInfoH getHisOrderInfo(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee, OrderFeeExt orderFeeExt, OrderScheduler scheduler, OrderPaymentDaysInfo costPaymentDaysInfo, OrderPaymentDaysInfo incomePaymentDaysInfo, String accessToken) {
        OrderInfoH orderInfoH = null;
        try {
            orderInfoH = new OrderInfoH();
            BeanUtil.copyProperties(orderInfo, orderInfoH);
            OrderInfoExtH orderInfoExtH = new OrderInfoExtH();
            BeanUtil.copyProperties(orderInfoExt, orderInfoExtH);
            OrderFeeH orderFeeH = new OrderFeeH();
            BeanUtil.copyProperties(orderFee, orderFeeH);
            OrderFeeExtH orderFeeExtH = new OrderFeeExtH();
            BeanUtil.copyProperties(orderFeeExt, orderFeeExtH);
            OrderSchedulerH schedulerH = new OrderSchedulerH();
            BeanUtil.copyProperties(scheduler, schedulerH);
            Set<OrderGoods> set = iOrderGoodsService.getOrderGoodsSet(orderInfo.getOrderId());
            orderFeeStatementService.moveOrderFeeStatementToHistory(orderInfo.getOrderId(), accessToken);
            if (set != null) {
                Iterator<OrderGoods> it = set.iterator();
                while (it.hasNext()) {
                    OrderGoods goods = it.next();
                    OrderGoodsH ordGIH = new OrderGoodsH();
                    try {
                        BeanUtil.copyProperties(goods, ordGIH);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iOrderGoodsService.removeById(goods.getId());
                    ordGIH.setId(null);
                    ordGIH.setEndDate(LocalDateTime.now());
                    iOrderGoodsHService.saveOrUpdate(ordGIH);
                }
            }
            OrderPaymentDaysInfoH costPaymentDaysInfoH = new OrderPaymentDaysInfoH();
            OrderPaymentDaysInfoH incomePaymentDaysInfoH = new OrderPaymentDaysInfoH();

            BeanUtil.copyProperties(costPaymentDaysInfo, costPaymentDaysInfoH);
            BeanUtil.copyProperties(incomePaymentDaysInfo, incomePaymentDaysInfoH);

            iOrderInfoService.removeById(orderInfo.getId());
            iOrderInfoExtService.removeById(orderInfoExt.getId());
            iOrderFeeService.removeById(orderFee.getId());
            iOrderFeeExtService.removeById(orderFeeExt.getId());
            iOrderSchedulerService.removeById(scheduler.getId());
            iOrderPaymentDaysInfoService.removeById(costPaymentDaysInfo.getId());
            iOrderPaymentDaysInfoService.removeById(incomePaymentDaysInfo.getId());
            orderInfoH.setEndDate(LocalDateTime.now());
            orderInfoExtH.setEndDate(LocalDateTime.now());
            orderFeeH.setEndDate(LocalDateTime.now());
            orderFeeExtH.setEndDate(LocalDateTime.now());
            schedulerH.setEndDate(LocalDateTime.now());
            orderInfoH.setId(null);
            orderInfoExtH.setId(null);
            orderFeeH.setId(null);
            orderFeeExtH.setId(null);
            schedulerH.setId(null);
            costPaymentDaysInfoH.setId(null);
            costPaymentDaysInfoH.setEndDate(LocalDateTime.now());
            incomePaymentDaysInfoH.setId(null);
            incomePaymentDaysInfoH.setEndDate(LocalDateTime.now());
            iOrderPaymentDaysInfoHService.saveOrUpdate(costPaymentDaysInfoH);
            iOrderPaymentDaysInfoHService.saveOrUpdate(incomePaymentDaysInfoH);
            this.saveOrUpdate(orderInfoH);
            iOrderInfoExtHService.saveOrUpdate(orderInfoExtH);
            iOrderFeeHService.saveOrUpdate(orderFeeH);
            iOrderFeeExtHService.saveOrUpdate(orderFeeExtH);
            iOrderSchedulerHService.saveOrUpdate(schedulerH);

            /**
             * 司机补贴移入历史表  20181010
             */
            List<OrderDriverSubsidy> list = iOrderDriverSubsidyService.findDriverSubsidys(null, null, orderInfoH.getOrderId(), null, orderInfoH.getTenantId(), false, null);
            if (list != null) {
                for (OrderDriverSubsidy orderDriverSubsidy : list) {
                    OrderDriverSubsidyH orderDriverSubsidyH = new OrderDriverSubsidyH();

                    BeanUtil.copyProperties(orderDriverSubsidy, orderDriverSubsidyH);

                    if (com.youming.youche.conts.SysStaticDataEnum.ORDER_STATE.CANCELLED == orderInfoH.getOrderState()) {
                        orderDriverSubsidyH.setIsCancle(1);
                    } else {
                        orderDriverSubsidyH.setIsCancle(0);
                    }
                    iOrderDriverSubsidyHService.save(orderDriverSubsidyH);
                }
            }
            List<OrderTransitLineInfo> transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfoH.getOrderId());
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                    OrderTransitLineInfoH orderTransitLineInfoH = new OrderTransitLineInfoH();
                    BeanUtil.copyProperties(orderTransitLineInfo, orderTransitLineInfoH);
                    iOrderTransitLineInfoHService.save(orderTransitLineInfoH);
                }
            }
            //移除多装多卸货物客户表
            this.moveTransitHis(orderInfoH.getOrderId());
            iOrderTransitLineInfoService.deleteOrderTransitLineInfo(orderInfoH.getOrderId());
            //移除司机补贴
            iOrderDriverSubsidyService.deleteDriverSubsidy(orderInfoH.getOrderId(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderInfoH;
    }

    @Override
    public void moveTransitHis(Long orderId) {
        transitCustHMapper.insertTransitCustHByTransitCust(orderId);
        transitGoodsHMapper.insertTransitGoodsHBytransitGoods(orderId);
        transitCustMapper.deleteTransitCustByOrderId(orderId);
        transitGoodsMapper.deleteTransitGoodsByOrderId(orderId);
    }

    @Override
    @Transactional
    public void doSetPreFeeZeroToRedis(List<OrderListOut> list, String accessToken) {
        for (int i = 0; i < list.size(); i++) {
            OrderListOut out = list.get(i);
//            redisUtil.lpush("ORDER_PRE_FEE_ZERO",out.getOrderId()+","+out.getTenantId());
            execution(out.getOrderId(), out.getTenantId(), accessToken);
        }

    }

    public void execution(long orderId, Long tenantId, String accessToken) {
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderFeeExt orderFeeExt = iOrderFeeExtService.getOrderFeeExt(orderId);
        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
        if (null != orderInfoExt.getPreAmountFlag() && orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.WILL_PAY && (null == orderFee.getPreTotalFee() || (orderFee.getPreTotalFee() <= 0 || orderFee.getPreTotalFee() - (orderFeeExt.getPontage() == null ? 0L : orderFeeExt.getPontage()) <= 0))) {
            if ((null == orderFeeExt.getDriverSwitchSubsidy() || orderFeeExt.getDriverSwitchSubsidy() == 0) && (null == orderFeeExt.getSalary() || orderFeeExt.getSalary() == 0) && (null == orderFeeExt.getCopilotSalary() || orderFeeExt.getCopilotSalary() == 0)) {
                //调用支付预付款
                iOrderFeeService.payProFee(orderId, new ArrayList<>(), accessToken);
                //取消审核流程
                auditOutService.cancelProcess(ORDER_PAY_PRE_FEE_CODE, orderId, tenantId);
            }
        } else {
            throw new BusinessException("订单数据发生变化");
        }
    }

    @Override
    public HasPermissionDto getIncomeAndCostPermission(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        boolean orderIncomePermission = PermissionCacheUtil.hasOrderIncomePermission();
//        boolean orderCostPermission = PermissionCacheUtil.hasOrderCostPermission();
        boolean orderIncomePermission = true;
        boolean orderCostPermission = sysRoleService.hasOrderCostPermission(loginInfo);
        HasPermissionDto hasPermissionDto = new HasPermissionDto();
        hasPermissionDto.setHasIncomePermission(orderIncomePermission);
        hasPermissionDto.setHasCostPermission(orderCostPermission);

        return hasPermissionDto;
    }

    @Override
    public void getDealOrderListExport(OrderListInVo orderListIn, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OrderListOut> orderList = this.getOrderList(orderListIn, accessToken);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "订单号", "客户名称", "运输线路",
                    "线路名称", "预估收入", "品名",
                    "车牌号码", "车辆类型", "车辆种类",
                    "靠台时间", "指导价(元)", "中标价(元)",
                    "订单状态", "异常创建时间", "异常类型",
                    "登记金额(元)", "处理金额", "异常描述",
                    "责任司机", "异常来源", "内部审核",
                    "处理时间"};
            resourceFild = new String[]{
                    "getOrderId", "getAccidentStatusName", "getTransportationRoute",
                    "getSourceName", "getCostPrice", "getGoodsName",
                    "getPlateNumber", "getVehicleClass", "getVehicleClassName",
                    "getDependTime", "getGuidePrice", "getTotalFee",
                    "getOrderState", "getExceptionCreationTime", "getExceptionType",
                    "getRegisteredAmount", "getProcessingAmount", "getExceptionDescription",
                    "getCarDriverMan", "getAbnormalSource", "getInternalAudit",
                    "getProcessingTime"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(orderList, showName, resourceFild, OrderListOut.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "车辆事故信息.xlsx", inputStream.available());
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
    public List<OrderListOut> getOrderList(OrderListInVo orderListIn, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        List<Long> preBusiIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(orderListIn.getBeginOrderTime())) {
            orderListIn.setBeginOrderTime("00:00:00");
        }
        if (StringUtils.isNotEmpty(orderListIn.getEndOrderTime())) {
            orderListIn.setEndOrderTime("23:59:59");
        }
        if (StringUtils.isNotEmpty(orderListIn.getBeginDependTime())) {
            orderListIn.setBeginDependTime("00:00:00");
        }
        if (StringUtils.isNotEmpty(orderListIn.getEndDependTime())) {
            orderListIn.setEndDependTime("23:59:59");
        }
        if (StringUtils.isNotEmpty(orderListIn.getBeginReceiveTime())) {
            orderListIn.setBeginReceiveTime("00:00:00");
        }
        if (StringUtils.isNotEmpty(orderListIn.getEndReceiveTime())) {
            orderListIn.setEndReceiveTime("23:59:59");
        }
        if (StringUtils.isNotEmpty(orderListIn.getBeginProblemDealTime())) {
            orderListIn.setBeginProblemDealTime("00:00:00");
        }
        if (StringUtils.isNotEmpty(orderListIn.getEndProblemDealTime())) {
            orderListIn.setEndProblemDealTime("23:59:59");
        }
        //支付预付款待处理
        boolean preTodo = false;
        boolean arriveTodo = false;
        if (orderListIn.getFinalTodo() != null && orderListIn.getFinalTodo()) {
            //  查询用户待审核的数据
            List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, baseUser.getId(), baseUser.getTenantId(), 1000);
            if (list != null && list.size() > 0) {
                preBusiIds.addAll(list);
            }
        }
        if (orderListIn.getAmountFlag() != null && orderListIn.getAmountFlag() == 99) {
            List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, baseUser.getId(), baseUser.getTenantId(), 1000);
            if (list != null && list.size() > 0) {
                preBusiIds.addAll(list);
            }
            preTodo = true;
            orderListIn.setAmountFlag(null);
        }
        //支付到付款待处理
        if (orderListIn.getArrivePaymentState() != null && orderListIn.getArrivePaymentState() == 99) {
            List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, baseUser.getId(), baseUser.getTenantId(), 1000);
            if (list != null && list.size() > 0) {
                preBusiIds.addAll(list);
            }
            arriveTodo = true;
            orderListIn.setArrivePaymentState(null);
        }
        orderListIn.setArriveTodo(arriveTodo);
        orderListIn.setPreTodo(preTodo);
        orderListIn.setPreBusiIds(preBusiIds);
        if (orderListIn.getTodo() != null && orderListIn.getTodo()) {
            List<Long> busiIds = new ArrayList<>();
            if (orderListIn.getSelectType() != null) {
                if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) { // 查询类型
                    //  查询用户待审核的数据
                    List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.PROBLEM_CODE, baseUser.getId(), baseUser.getTenantId());
                    if (list != null && list.size() > 0) {
                        busiIds.addAll(list);
                    }
                } else if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                    if (orderListIn.getAuditSource() == null || orderListIn.getAuditSource() <= 0) {
                        List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, baseUser.getId(), baseUser.getTenantId());
                        if (list != null && list.size() > 0) {
                            busiIds.addAll(list);
                        }
                        List<Long> list1 = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, baseUser.getId(), baseUser.getTenantId());
                        if (list1 != null && list1.size() > 0) {
                            busiIds.addAll(list1);
                        }
                    } else {
                        if (orderListIn.getAuditSource() == com.youming.youche.record.common.OrderConsts.AUDIT_SOURCE.ABNORMAL_PRICE) {
                            List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, baseUser.getId(), baseUser.getTenantId());
                            if (list != null && list.size() > 0) {
                                busiIds.addAll(list);
                            }
                        } else if (orderListIn.getAuditSource() == com.youming.youche.record.common.OrderConsts.AUDIT_SOURCE.UPDATE_ORDER) {
                            List<Long> list1 = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, baseUser.getId(), baseUser.getTenantId());
                            if (list1 != null && list1.size() > 0) {
                                busiIds.addAll(list1);
                            }
                        }
                    }
                } else if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.AGING_AUDIT) {
                    List<Long> list = auditOutService.getBusiIdByUserId(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, baseUser.getId(), baseUser.getTenantId());
                    if (list != null && list.size() > 0) {
                        busiIds.addAll(list);
                    }
                }
            }
            orderListIn.setBusiId(busiIds);
        }
//        // 判断当前登录人是否拥有"所有数据"权限
//        boolean hasAllData = PermissionCacheUtil.hasAllData();
//        orderListIn.setHasAllData(hasAllData);
//        // 获取权限部门
        List<Long> orgIdList = new ArrayList<>();
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);//获取数据权限
        sysMenuBtns.forEach(sysMenuBtn -> {
            orgIdList.add(sysMenuBtn.getId());
        });
        orderListIn.setOrgIdList(orgIdList);
        //  主查询
        String[] orderid = null;
        if (orderListIn.getOrderIds() != null) {
            orderid = orderListIn.getOrderIds().split(",");
        }
        List<String> orderids = Arrays.asList(orderid);
        List<OrderListOut> list = baseMapper.getOrderListOutExport(orderListIn, orderids, baseUser.getTenantId());
        List<OrderListOut> listOuts = new ArrayList<>();
        List<Long> busiIdList = new ArrayList<Long>();
        for (OrderListOut orderListOut1 : list) {
            busiIdList.add(orderListOut1.getOrderId());
        }
        Map<Long, Map<String, Object>> payPreMap = new HashMap<>();
        Map<Long, Map<String, Object>> payArriveMap = new HashMap<>();
        Map<Long, Map<String, Object>> payFinalMap = new HashMap<>();

        if ((orderListIn.getIsHis() == null || orderListIn.getIsHis().intValue() <= 0)
                && orderListIn.getSelectType() != null) {
            if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER) {
                //  获取业务审核当前操作
                try {
                    payPreMap = auditOutService.queryAuditRealTimeOperation(baseUser.getId(), com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, busiIdList, baseUser.getTenantId());
                    payArriveMap = auditOutService.queryAuditRealTimeOperation(baseUser.getId(), com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, busiIdList, baseUser.getTenantId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.RECEIPT_AUDIT) {
                try {
                    payFinalMap = auditOutService.queryAuditRealTimeOperation(baseUser.getId(), com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, busiIdList, baseUser.getTenantId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Map<Long, Boolean> jurisdictionMap = new ConcurrentHashMap<>();
        Map<Long, Boolean> updaJurisdictionMap = new ConcurrentHashMap<>();
        if (busiIdList.size() > 0) {
            if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) {
                //  判断当前的用户对传入的业务主键是否有审核权限
                jurisdictionMap = auditNodeInstService.isHasPermission(com.youming.youche.conts.AuditConsts.AUDIT_CODE.PROBLEM_CODE, busiIdList, accessToken);
            } else if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                updaJurisdictionMap = auditNodeInstService.isHasPermission(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, busiIdList, accessToken);
                jurisdictionMap = auditNodeInstService.isHasPermission(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, busiIdList, accessToken);
            }
        }
        List<OrderMainReport> costReportMaps = new ArrayList<>();
        if (busiIdList != null && busiIdList.size() > 0) {
            // 查询订单无上报或者上报已提交或者保存未提交标识
            costReportMaps = iOrderCostReportService.getCostReportStateByOrderIds(busiIdList);
        }
        List<OrderMainReport> costReportMap = new ArrayList<>();
        for (OrderMainReport orderMainReport : costReportMaps) {
            OrderMainReport orderMainReports = new OrderMainReport();
            orderMainReports.setOrderId(orderMainReport.getOrderId());
            orderMainReports.setState(orderMainReport.getState());
            costReportMap.add(orderMainReports);
        }
        List<OrderLimitDto> finalExpireMap = new ArrayList<>();
        if (orderListIn.getIsHis() != null && orderListIn.getIsHis() > 0) {//历史表才需查询
            if (busiIdList != null && busiIdList.size() > 0) {
                //   判断订单是否有尾款
                finalExpireMap = iOrderLimitService.hasFinalOrderLimit(busiIdList, -1);
            }
        }
        for (OrderListOut listOut : list) {
            OrderListOut out = new OrderListOut();
            BeanUtil.copyProperties(listOut, out);//数据转换
            Date verifyDependDate = out.getVerifyDependDate();//校验靠台时间
            Date verifyStartDate = out.getVerifyStartDate();//校验离台时间
            Date verifyArriverDate = out.getVerifyArriverDate();//校验到达时间
            Date verifyDepartureDate = out.getVerifyDepartureDate();//校验离场时间
            if (verifyDependDate != null
                    || verifyStartDate != null
                    || verifyArriverDate != null
                    || verifyDepartureDate != null) {
                out.setIsCalibration(true);
            } else {
                out.setIsCalibration(false);
            }
            //  查询订单线路详情
            OrderInfoDto orderInfoDto = iOrderSchedulerService.queryOrderLineString(out.getOrderId());
            out.setOrderLine(orderInfoDto.getOrderLine());
            out.setIsTransitLine(orderInfoDto.getIsTransitLine());
            //  获取订单轨迹状态
            OrderInfoDto orderInfoDto1 = iOrderSchedulerService.queryOrderTrackType(out.getOrderId());
            Integer trackType = null;
            if (orderInfoDto1.getTrackType() != null) {
                trackType = orderInfoDto1.getTrackType() < 0 ? 0 : orderInfoDto1.getTrackType();
            }
            String trackMsg = orderInfoDto1.getTrackMas();
            out.setTrackMsg(trackMsg);
            out.setTrackType(trackType);

            if (orderListIn.getIsHis() != null && orderListIn.getIsHis() > 0) {//历史表才需查询
                Boolean isAddProblem = finalExpireMap.get(0).getIsAddProblem();
                out.setIsAddProblem(isAddProblem == null ? true : isAddProblem);
            } else {
                out.setIsAddProblem(true);
            }
            // 是否需要审核
            Boolean isPayDriverSubsidy = false;
            if (out.getOrderUpdateState() != null
                    && out.getOrderUpdateState() == com.youming.youche.record.common.OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
                out.setIsNeedAudit(true);
            } else {
                out.setIsNeedAudit(false);
            }
            if (out.getOrderState() == com.youming.youche.record.common.OrderConsts.ORDER_STATE.TO_BE_AUDIT
                    && (out.getOrderUpdateState() == null || out.getOrderUpdateState() != com.youming.youche.record.common.OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {// 待审核
                out.setAuditSource(com.youming.youche.record.common.OrderConsts.AUDIT_SOURCE.ABNORMAL_PRICE);
                out.getAuditSourceName();
                out.setIsJurisdiction(jurisdictionMap.get(out.getOrderId()));
            } else if (out.getOrderUpdateState() != null
                    && (out.getOrderUpdateState() == com.youming.youche.record.common.OrderConsts.UPDATE_STATE.UPDATE_PORTION
                    || out.getOrderUpdateState() == com.youming.youche.record.common.OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {// 部分修改
                // 都需审核
                out.setAuditSource(com.youming.youche.record.common.OrderConsts.AUDIT_SOURCE.UPDATE_ORDER);
                out.getAuditSourceName();
                out.setIsJurisdiction(updaJurisdictionMap.get(out.getOrderId()));
            }
            Integer costReportState = costReportMap.get(0).getState() == null ? -1 : costReportMap.get(0).getState();
            if (costReportState < 0) {//未上报
                out.setCostReportStateName("未上报");
            } else {
                out.setCostReportStateName(readisUtil.getSysStaticData("ORDER_COST_REPORT_STATE", costReportState + "").getCodeName());
            }
            if (out.getVehicleClass() != null && out.getVehicleClass() == com.youming.youche.conts.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && out.getPaymentWay() != null && out.getPaymentWay().intValue() == com.youming.youche.record.common.OrderConsts.PAYMENT_WAY.COST) {
                //  是否需要支付补贴
                isPayDriverSubsidy = orderDriverSubsidyService.isPayDriverSubsidy(out.getOrderId(), baseUser);
            }
            out.setIsPayDriverSubsidy(isPayDriverSubsidy);
            // 是否有审核权限
            if (orderListIn.getSelectType() == null || orderListIn.getSelectType() != com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                out.setIsJurisdiction(jurisdictionMap.get(out.getOrderId()));
            }
            Integer exceptionCount = 0;
            Integer agingCount = 0;
            Integer agingAduitCount = 0;

            if (out.getOrderState() != null && out.getOrderState() >= com.youming.youche.record.common.OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                //   根据订单号查询异常
                List<OrderProblemInfo> problemInfos = orderProblemInfoService.getOrderProblemInfoByOrderId(out.getOrderId(), out.getToTenantId());
                exceptionCount = problemInfos == null ? 0 : problemInfos.size();
                //  根据订单号 查询  时间排序 时效罚款表 order_aging_info
                List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getOrderId());

                agingCount = agingInfos == null ? 0 : agingInfos.size();
                if (agingInfos != null && agingInfos.size() > 0) {
                    for (OrderAgingInfo orderAgingInfo : agingInfos) {
                        if (orderAgingInfo.getAuditSts() != null
                                && (orderAgingInfo.getAuditSts().intValue() == com.youming.youche.conts.SysStaticDataEnum.EXPENSE_STATE.AUDIT
                                || orderAgingInfo.getAuditSts().intValue() == com.youming.youche.conts.SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING)) {
                            agingAduitCount++;
                        }
                    }
                }
            }
            //客户时效
            if (out.getFromOrderId() != null && out.getFromOrderId() > 0) {
                //  根据订单号 查询  时间排序 时效罚款表 order_aging_info
                List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getFromOrderId());
                if (agingInfos != null && agingInfos.size() > 0) {
                    for (OrderAgingInfo orderAgingInfo : agingInfos) {
                        if (orderAgingInfo.getAuditSts() != null
                                && orderAgingInfo.getAuditSts().intValue() == com.youming.youche.conts.SysStaticDataEnum.EXPENSE_STATE.END) {
                            agingCount++;
                        }
                    }
                }
            }
            if (out.getVehicleClass() != null && out.getVehicleClass().intValue() == com.youming.youche.conts.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && out.getPaymentWay() != null && out.getPaymentWay().intValue() == com.youming.youche.record.common.OrderConsts.PAYMENT_WAY.COST) {
                Long driverSubsidy = 0L;//切换司机已付补贴
                if (orderListIn.getIsHis() != null && orderListIn.getIsHis() > 0) {
                    //  获取订单司机补贴 历史
                    orderDriverSubsidyService.findOrderHDriverSubSidyFee(out.getOrderId(), null, out.getCarDriverId(), out.getCopilotUserId(), com.youming.youche.record.common.OrderConsts.AMOUNT_FLAG.ALREADY_PAY);

                } else {
                    //  获取订单司机补贴
                    driverSubsidy = orderDriverSubsidyService.findOrderDriverSubSidyFee(out.getOrderId(), null, out.getCarDriverId(), out.getCopilotUserId(), false, com.youming.youche.record.common.OrderConsts.AMOUNT_FLAG.ALREADY_PAY);
                }
                out.setDriverSwitchSubsidy(driverSubsidy);
            }

            out.setAgingCount(agingCount);
            out.setAgingAduitCount(agingAduitCount);
            out.setExceptionCount(exceptionCount);
            //车辆类型
            out.setCarStatusName(out.getCarStatus() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("VEHICLE_STATUS@ORD", out.getCarStatus() + "").getCodeName());
            //用户类型
            out.setCarUserTypeName(out.getCarUserType() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("DRIVER_TYPE", out.getCarUserType() + "").getCodeName());
            //到达市
            out.setDesRegionName(out.getDesRegion() == null ? ""
                    : sysCfgRedisUtils.getCityDataList("SYS_CITY", out.getDesRegion() + "").getName());
            //订单类型
            out.setOrderTypeName(out.getOrderType() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ORDER_TYPE", out.getOrderType() + "").getCodeName());
            //回单类型
            out.setReciveTypeName(out.getReciveType() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_TYPE", out.getReciveType() + "").getCodeName());
            //始发市
            out.setSourceRegionName(out.getSourceRegion() == null ? ""
                    : sysCfgRedisUtils.getCityDataList("SYS_CITY", out.getSourceRegion() + "").getName());
            //车辆类型 例如 招商挂靠车
            out.setVehicleClassName(out.getVehicleClass() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("VEHICLE_CLASS", out.getVehicleClass() + "").getCodeName());
            //订单状态
            out.setOrderStateName(out.getOrderState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ORDER_STATE", out.getOrderState() + "").getCodeName());
            //回单状态
            out.setReciveStateName(out.getReciveState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_STATE", out.getReciveState() + "").getCodeName());
            //合同状态
            out.setLoadStateName(out.getLoadState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_STATE", out.getLoadState() + "").getCodeName());
            //合同图片地址
            out.setContractUrl(
                    out.getContractUrl() == null ? "" : out.getContractUrl());
            ////签收回单URL（多个已英文逗号分开
            out.setReceiptsUrl(
                    out.getReceiptsUrl() == null ? "" : out.getReceiptsUrl());
            Boolean isPreAuditJurisdiction = false;
            String auditPreUserName = null;
            Boolean isPreFinallyNode = false;

            Boolean isArriveAuditJurisdiction = false;
            String auditArriveUserName = null;
            Boolean isArriveFinallyNode = false;

            Boolean isFinalAuditJurisdiction = false;
            String auditFinalUserName = null;
            Boolean isFinalFinallyNode = false;

            if ((orderListIn.getIsHis() == null || orderListIn.getIsHis().intValue() <= 0)
                    && orderListIn.getSelectType() != null) {
                if (orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER) {
                    Map<String, Object> preMap = payPreMap.get(out.getOrderId());
                    Map<String, Object> arriveMap = payArriveMap.get(out.getOrderId());
                    if (preMap != null) {
                        isPreAuditJurisdiction = DataFormat.getBooleanKey(preMap, "isAuditJurisdiction");
                        auditPreUserName = DataFormat.getStringKey(preMap, "auditUserName");
                        isPreFinallyNode = DataFormat.getBooleanKey(preMap, "isFinallyNode");
                    } else {
                        isPreAuditJurisdiction = true;
                        auditPreUserName = null;
                        isPreFinallyNode = true;
                    }
                    if (arriveMap != null) {
                        isArriveAuditJurisdiction = DataFormat.getBooleanKey(arriveMap, "isAuditJurisdiction");
                        auditArriveUserName = DataFormat.getStringKey(arriveMap, "auditUserName");
                        isArriveFinallyNode = DataFormat.getBooleanKey(arriveMap, "isFinallyNode");
                    } else {
                        isArriveAuditJurisdiction = true;
                        auditArriveUserName = null;
                        isArriveFinallyNode = true;
                    }
                }
                Map<String, Object> finalMap = payFinalMap.get(out.getOrderId());
                if (finalMap != null) {
                    isFinalAuditJurisdiction = DataFormat.getBooleanKey(finalMap, "isAuditJurisdiction");
                    auditFinalUserName = DataFormat.getStringKey(finalMap, "auditUserName");
                    isFinalFinallyNode = DataFormat.getBooleanKey(finalMap, "isFinallyNode");
                } else {
                    isFinalAuditJurisdiction = true;
                    auditFinalUserName = null;
                    isFinalFinallyNode = true;
                }
            }
            out.setIsPreAuditJurisdiction(isPreAuditJurisdiction);
            out.setAuditPreUserName(auditPreUserName);
            out.setIsPreFinallyNode(isPreFinallyNode);

            out.setIsArriveAuditJurisdiction(isArriveAuditJurisdiction);
            out.setAuditArriveUserName(auditArriveUserName);
            out.setIsArriveFinallyNode(isArriveFinallyNode);

            out.setIsFinalAuditJurisdiction(isFinalAuditJurisdiction);
            out.setAuditFinalUserName(auditFinalUserName);
            out.setIsFinalFinallyNode(isFinalFinallyNode);
            if (out.getPaymentWay() != null && out.getPaymentWay() == com.youming.youche.record.common.OrderConsts.PAYMENT_WAY.COST) {
//                out.setPreEtcFee(DataFormat.getLongKey(map, "pontage"));
                out.setPreEtcFee(listOut.getPreEtcFee());
            }
            //获取是否需求上传协议
            boolean isNeedUploadAgreement = false;
            if (orderListIn.getSelectType() != null &&
                    orderListIn.getSelectType() == com.youming.youche.record.common.OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER) {
                //  是否需要上传协议
                isNeedUploadAgreement = iOrderFeeService.isNeedUploadAgreementByLuge(out.getOrderId());

            }
            out.setIsNeedUploadAgreement(isNeedUploadAgreement);
            listOuts.add(out);
        }
        return listOuts;

    }


    /**
     * 校验预付款输入的油卡号
     */
    @Override
    public void verifyOilCardNum(OrderInfo orderInfo, List<String> oilCardNums, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderInfo == null || orderInfo.getOrderId() == null || orderInfo.getOrderId() <= 0) {
            throw new BusinessException("找不到订单[" + orderInfo.getOrderId() + "]信息");
        }

        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderInfo.getOrderId());
        if ((orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE)
                && orderFee.getPreOilFee() != null && orderFee.getPreOilFee() > 0
        ) {
            OrderScheduler scheduler = iOrderSchedulerService.getOrderScheduler(orderInfo.getOrderId());
            if (oilCardNums == null) {
                oilCardNums = new ArrayList<String>();
            }
            iOrderFeeService.verifyOilCardNum(oilCardNums, orderFee, orderInfo, orderFee.getPreOilFee(), scheduler, orderInfoExt.getOilAffiliation(), false, loginInfo);

            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                    "审核支付预付款，绑定油卡：" + (oilCardNums == null || oilCardNums.size() == 0 ? "无" : oilCardNums.toString()), accessToken, orderInfo.getOrderId());
        }
    }

    /**
     * 记录日志
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
     * 添加单号到缓存
     */
    public void addOrderIdToCache(Long orderId, Long tenantId) {
        if (orderId == null || orderId <= 0 || tenantId == null || tenantId <= 0) {
            return;
        }
        String tenantIdStr = readisUtil.getSysCfg("WAY_BILL_TENANT_ID", "0").getCfgValue();
        if (StringUtils.isBlank(tenantIdStr)) {
            return;
        }
        List<String> tenantIds = Arrays.asList(tenantIdStr.split(","));
        if (tenantIds == null || !tenantIds.contains(tenantId.toString())) {
            return;
        }

        redisUtil.set(this.getCacheKey(tenantId), orderId.toString());
    }

    private String getCacheKey(Long tenantId) {
        //执行历史订单信息的时间，每个车队对应一个
        return "WAYBILL_HISORDERINFO_ORDERINDS_" + tenantId;
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

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken) {
        String auditCode = DataFormat.getStringKey(paramsMap, "auditCode");
        if (auditCode != null) {
            if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {

            } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)) {
                iOrderFeeService.payArriveFee(busiId, accessToken);
            } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)) {

            }
        }
    }

    @Override
    public void fail(Long busiId, String desc, String auditCode, Map paramsMap, String accessToken) {
        if (auditCode != null) {
            if (AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE.equals(auditCode)) {
                auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, busiId, SysOperLogConst.BusiCode.OrderInfo, paramsMap, accessToken);
            } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE.equals(auditCode)) {
                auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, busiId,
                        SysOperLogConst.BusiCode.OrderInfo, paramsMap, accessToken);
            } else if (AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE.equals(auditCode)) {
                auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, busiId,
                        SysOperLogConst.BusiCode.OrderInfo, paramsMap, accessToken);
            }
        }
    }

    public String getStringKey(Object obj) {
        if (obj != null) {
            if (obj instanceof String[] && ((String[]) ((String[]) obj)).length > 1) {
                StringBuffer sbf = new StringBuffer();

                for (int i = 0; i < ((String[]) ((String[]) obj)).length; ++i) {
                    sbf.append(((String[]) ((String[]) obj))[i]);
                    if (i < ((String[]) ((String[]) obj)).length - 1) {
                        sbf.append(",");
                    }
                }

                return sbf.toString();
            }

            if (obj instanceof String[] && ((String[]) ((String[]) obj)).length > 0) {
                if ("null".equals(((String[]) ((String[]) obj))[0])) {
                    return "";
                }

                return ((String[]) ((String[]) obj))[0];
            }

            if (obj instanceof String) {
                return (String) obj;
            }

            return obj.toString();
        }

        return "";
    }



    @Override
    public OrderDetailsAppDto queryOrderDetailsAppOut(Long orderId, Integer isHis, Integer selectType, boolean isTransfer, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = null;
        OrderDetailsAppDto orderDetailsAppDto = orderInfoHMapper.queryOrderDetailsAppOut(orderId, isHis, selectType);
        if (selectType != null && selectType.intValue() == 1) {
            userId = null;//微信全量查询
        }else{
            userId = loginInfo.getUserInfoId();
        }
        OrderDetailsAppDto appOut = new OrderDetailsAppDto();
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (orderDetailsAppDto!=null){
            BeanUtils.copyProperties(orderDetailsAppDto,appOut);
            appOut.setDependDate(Date.from(orderDetailsAppDto.getDependTime().atZone(ZoneOffset.ofHours(8)).toInstant()));
            appOut.setCarArriveTime(Date.from(orderDetailsAppDto.getCarArriveDate().atZone(ZoneOffset.ofHours(8)).toInstant()));
            List<OrderOilDepotScheme> depotSchemes = iOrderOilDepotSchemeService.getOrderOilDepotSchemeByOrderId(orderId, false, loginInfo);
            OaloanCountDto loanMap = oaLoanService.queryOaloanCount(orderId, userId);
            Long loanSumFee = 0L;
            ClaimExpenseCountDto expenseInfoMap = claimExpenseInfoService.queryClaimExpenseCount(orderId, userId);
            Long expenseSumFee = 0L;
            if (expenseInfoMap != null) {
                expenseSumFee = expenseInfoMap.getAmount();
            }
            if (loanMap != null) {
                loanSumFee = loanMap.getAmount();
            }
            if (appOut.getFromOrderId() != null && appOut.getFromOrderId() > 0) {
                OrderInfo fromOrder = orderInfoService.getOrder(appOut.getFromOrderId());
                if (fromOrder != null) {
                    appOut.setIncomeIsNeedBill(fromOrder.getIsNeedBill());
                }else{
                    OrderInfoH fromOrderH = orderInfoService.getOrderH(appOut.getFromOrderId());
                    if (fromOrderH != null) {
                        appOut.setIncomeIsNeedBill(fromOrderH.getIsNeedBill());
                    }
                }
            }
            List<OrderReportDto> orderReports = orderReportService.queryOrderReportList(orderId, loginInfo.getTenantId(), null, accessToken);
            List<OrderProblemInfo> orderProblemInfos = orderProblemInfoService.getOrderProblemInfoByUserId(orderId, userId);
            Long agingFineFee = 0L;
            Long exceptionFee = 0L;
            if (orderProblemInfos != null && orderProblemInfos.size() > 0) {
                for (OrderProblemInfo orderProblemInfo : orderProblemInfos) {
                    if (orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.END
                            && orderProblemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {// 已完成
                        // 异常
                        exceptionFee += orderProblemInfo.getProblemDealPrice();
                    }
                }
            }
            List<OrderAgingInfo> orderAgingInfos = orderAgingInfoService.queryAgingInfoByUserId(orderId,userId);
            if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
                for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                    if (orderAgingInfo != null && orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                        agingFineFee += orderAgingInfo.getFinePrice();
                    }
                }
            }
            if (isTransfer) {
                SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(appOut.getTenantId(), true);
                if (sysTenantDef != null) {
                    appOut.setCustomPhone(sysTenantDef.getLinkPhone());
                    appOut.setCustomName(sysTenantDef.getShortName());
                }
            }
            appOut.setExpenseSumFee(expenseSumFee);
            appOut.setLoanSumFee(loanSumFee);
            appOut.setAgingFineFee(agingFineFee);
            appOut.setExceptionFee(exceptionFee);
            appOut.setReportNum(orderReports == null ? 0 : orderReports.size());
            appOut.setDepotSchemes(depotSchemes);


            OrderInfoDto orderInfoDto = iOrderSchedulerService.queryOrderLineString(appOut.getOrderId());
            appOut.setOrderLine(orderInfoDto.getOrderLine());
            appOut.setIsTransitLine(orderInfoDto.getIsTransitLine());

            Integer  inReciveTime = 0;
            Integer  inInvoiceTime = 0;
            Integer  inCollectionTime = 0;
            Integer  costCollectionTime = 0;
            Integer  costReciveTime = 0;
            Integer  costInvoiceTime = 0;
            List<OrderTransitLineInfo> transitLineInfos = null;
            if (appOut.getIsHis() != null) {
                if (appOut.getIsHis() == 1) {
                    OrderPaymentDaysInfoH inDaysInfoH =iOrderPaymentDaysInfoHService.queryOrderPaymentDaysInfoH(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
                    OrderPaymentDaysInfoH costDaysInfoH = iOrderPaymentDaysInfoHService.queryOrderPaymentDaysInfoH(orderId, OrderConsts.PAYMENT_DAYS_TYPE.COST);
                    inReciveTime = iOrderPaymentDaysInfoService.calculatePaymentDaysH(inDaysInfoH, appOut.getDependDate(), OrderConsts.CALCULATE_TYPE.RECIVE);
                    inInvoiceTime = iOrderPaymentDaysInfoService.calculatePaymentDaysH(inDaysInfoH, appOut.getDependDate(), OrderConsts.CALCULATE_TYPE.INVOICE);
                    inCollectionTime = iOrderPaymentDaysInfoService.calculatePaymentDaysH(inDaysInfoH, appOut.getDependDate(), OrderConsts.CALCULATE_TYPE.COLLECTION);
                    //设置账期
                    OrderPaymentDaysInfo costPaymentDaysInfo = new OrderPaymentDaysInfo();
//                    BeanUtils.copyProperties(costPaymentDaysInfo, costDaysInfoH);
                    BeanUtils.copyProperties(costDaysInfoH, costPaymentDaysInfo);
                    OrderPaymentDaysInfo incomePaymentDaysInfo = new OrderPaymentDaysInfo();
//                    BeanUtils.copyProperties(incomePaymentDaysInfo, inDaysInfoH);
                    BeanUtils.copyProperties(inDaysInfoH, incomePaymentDaysInfo);
                    appOut.setIncomePaymentDaysInfo(incomePaymentDaysInfo);
                    appOut.setCostPaymentDaysInfo(costPaymentDaysInfo);
                    List<OrderTransitLineInfoH> transitLineInfoHs = iOrderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
                    if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                        transitLineInfos = new ArrayList<>();
                        for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                            OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                            BeanUtils.copyProperties(transitLineInfo, orderTransitLineInfoH);
                            transitLineInfo.setAddress(orderTransitLineInfoH.getNavigatLocation());
                            transitLineInfo.setNavigatLocation(orderTransitLineInfoH.getAddress());
                            transitLineInfos.add(transitLineInfo);
                        }
                    }
                }else if(appOut.getIsHis() == 0){
                        OrderPaymentDaysInfo inDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
                        OrderPaymentDaysInfo costDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.COST);
                        //设置账期
                        transitLineInfos = iOrderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
                        if(transitLineInfos!=null) {
                            String address=null;
                            for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                                address=orderTransitLineInfo.getNavigatLocation();
                                orderTransitLineInfo.setNavigatLocation(orderTransitLineInfo.getAddress());
                                orderTransitLineInfo.setAddress(address);
                            }
                        }
                        appOut.setIncomePaymentDaysInfo(inDaysInfo);
                        appOut.setCostPaymentDaysInfo(costDaysInfo);
                }
            }
            appOut.setTransitLineInfos(transitLineInfos);
            appOut.setInReciveTime(inReciveTime);
            appOut.setInInvoiceTime(inInvoiceTime);
            appOut.setInCollectionTime(inCollectionTime);
            appOut.setCostCollectionTime(costCollectionTime);
            appOut.setCostReciveTime(costReciveTime);
            appOut.setCostInvoiceTime(costInvoiceTime);
            appOut.setIsReportFee(0);
            appOut.setTotalReportFee(0L);//初始化上报费用
            // 自有车重新设置预付金额
            if (appOut.getVehicleClass() != null
                    && appOut.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (appOut.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                    appOut.setPreTotalFee((appOut.getPreOilFeeSum() == null ? 0 : appOut.getPreOilFeeSum())
                            + (appOut.getSubsidyFeeSum() == null ? 0 : appOut.getSubsidyFeeSum()));
                    List<Map> driverSubsidyDays = iOrderSchedulerService.queryOrderDriverSubsidyDay(appOut.getOrderId());
                    appOut.setDriverSubsidyDays(driverSubsidyDays);
                }else if(appOut.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE){
                    appOut.setPreTotalFee(appOut.getPreOilFeeSum() == null ? 0 : appOut.getPreOilFeeSum());
                    OrderMainReport orderMainReport = iOrderCostReportService.getObjectByOrderId(appOut.getOrderId());
                    if (orderMainReport != null) {//有上报费用
                        appOut.setIsReportFee(1);
                        appOut.setOrderCostReportState(orderMainReport.getState());
                    }
                    //自有车实报实销模式
                    Map mapOut = orderCostReportMapper.getTotalAmtByOrderId(orderId, null);
                    appOut.setTotalReportFee(DataFormat.getLongKey(mapOut, "totalAmt") < 0 ? 0 : DataFormat.getLongKey(mapOut, "totalAmt"));
                    //上报费用总和加上其他费用
                    Long orderCostOtherReportAmountByOrderId = iOrderCostReportService.getOrderCostOtherReportAmountByOrderId(orderId);
                    orderCostOtherReportAmountByOrderId=orderCostOtherReportAmountByOrderId==null?0:orderCostOtherReportAmountByOrderId;
                    appOut.setTotalReportFee(appOut.getTotalReportFee()+orderCostOtherReportAmountByOrderId);
                    appOut.setOrderRetrographyCostInfo(iOrderRetrographyCostInfoService.queryOrderRetrographyCostInfo(orderId));
                }


//                List<Map> list = orderInfoMapper.queryOrderCostInfo(orderId);
//                if (list != null && list.size() > 0) {
//                    Map costMap =list.get(0);
//                    appOut.setChangeCost(DataFormat.getLongKey(costMap, "change_cost") < 0 ? 0 : DataFormat.getLongKey(costMap, "change_cost"));
//                    appOut.setFixedCost(DataFormat.getLongKey(costMap, "fixed_cost") < 0 ? 0 : DataFormat.getLongKey(costMap, "fixed_cost"));
//                    appOut.setOperCost(DataFormat.getLongKey(costMap, "oper_cost") < 0 ? 0 : DataFormat.getLongKey(costMap, "oper_cost"));
//                    appOut.setFee(DataFormat.getLongKey(costMap, "fee") < 0 ? 0 : DataFormat.getLongKey(costMap, "fee"));
//                }
            }else if(appOut.getIsCollection() != null
                    && appOut.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
                    && (selectType == null || selectType.intValue() != 1)){
                //代收订单司机看不到除了油费以外的费用
                appOut.setTotalFee(appOut.getPreOilFeeSum());
                appOut.setPreCashFee(0L);
                appOut.setPreEtcFee(0L);
                appOut.setArrivePaymentFee(0L);
                appOut.setPreTotalFee(appOut.getPreOilFeeSum());
                appOut.setFinalFee(0L);
                appOut.setInsuranceFee(0L);
            }
            if (StringUtils.isNotBlank(appOut.getContractUrl())) {
                try {
                    appOut.setAbsoluteCcontractUrl(client.getHttpURL(appOut.getContractUrl()).split("\\?")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //返回回单图片
            List<OrderReceipt> receipts = iOrderReceiptService.findOrderReceipts(orderId, accessToken,null);
            if(receipts!=null&&receipts.size()>0) {
                try {
                    appOut.setAbsoluteReceiptsUrl(client.getHttpURL(receipts.get(receipts.size()-1).getFlowUrl()).split("\\?")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(appOut.getReceiptUrlList()==null) {
                    appOut.setReceiptUrlList(new ArrayList<String>());
                }
                for (OrderReceipt orderReceipt : receipts) {
                    if(StringUtils.isNotBlank(orderReceipt.getFlowUrl())) {
                        try {
                            appOut.getReceiptUrlList().add(client.getHttpURL(orderReceipt.getFlowUrl()).split("\\?")[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Boolean isPreAuditJurisdiction = false;
            String auditPreUserName = null;
            Boolean isPreFinallyNode = false;
            Boolean isArriveAuditJurisdiction = false;
            String auditArriveUserName = null;
            Boolean isArriveFinallyNode = false;
            List<Long> busiIdList = new ArrayList<Long>();
            busiIdList.add(appOut.getOrderId());
            Map<Long, Map<String, Object>>  payPreMap = new HashMap<>();
            Map<Long, Map<String, Object>>  payArriveMap = new HashMap<>();
            if (appOut.getIsHis() == null || appOut.getIsHis().intValue() <= 0) {
                if (loginInfo.getTenantId() != null && loginInfo.getTenantId().longValue() == appOut.getTenantId().longValue()
                        && (appOut.getIsTempTenant() == null || appOut.getIsTempTenant().intValue() == OrderConsts.IS_TEMP_TENANT.NO)
                ) {
                    payPreMap = auditOutService.queryAuditRealTimeOperation(loginInfo.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, busiIdList, loginInfo.getTenantId());
                    payArriveMap = auditOutService.queryAuditRealTimeOperation(loginInfo.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, busiIdList, loginInfo.getTenantId());
                }
            }
            if (appOut.getIsHis() == null || appOut.getIsHis().intValue() <= 0 ) {
                Map<String, Object> preMap  = payPreMap.get(appOut.getOrderId());
                Map<String, Object> arriveMap  = payArriveMap.get(appOut.getOrderId());
                if (preMap != null) {
                    isPreAuditJurisdiction = DataFormat.getBooleanKey(preMap, "isAuditJurisdiction");
                    auditPreUserName = DataFormat.getStringKey(preMap, "auditUserName");
                    isPreFinallyNode = DataFormat.getBooleanKey(preMap, "isFinallyNode");
                }else{
                    isPreAuditJurisdiction = true;
                    auditPreUserName = null;
                    isPreFinallyNode = true;
                }if (arriveMap != null) {
                    isArriveAuditJurisdiction = DataFormat.getBooleanKey(arriveMap, "isAuditJurisdiction");
                    auditArriveUserName = DataFormat.getStringKey(arriveMap, "auditUserName");
                    isArriveFinallyNode = DataFormat.getBooleanKey(arriveMap, "isFinallyNode");
                }else{
                    isArriveAuditJurisdiction = true;
                    auditArriveUserName = null;
                    isArriveFinallyNode = true;
                }
            }
            appOut.setIsPreAuditJurisdiction(isPreAuditJurisdiction);
            appOut.setAuditPreUserName(auditPreUserName);
            appOut.setIsPreFinallyNode(isPreFinallyNode);
            appOut.setIsArriveAuditJurisdiction(isArriveAuditJurisdiction);
            appOut.setAuditArriveUserName(auditArriveUserName);
            appOut.setIsArriveFinallyNode(isArriveFinallyNode);
            boolean isNeedUploadAgreement = false;
            if ( appOut.getIsNeedBill() != null
                    && appOut.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                isNeedUploadAgreement = iOrderFeeService.isNeedUploadAgreementByLuge(appOut.getOrderId());
            }else{
                //最下层接单方同步运输协议签订
                if (appOut.getFromOrderId() != null && appOut.getFromOrderId() > 0
                        && (appOut.getToTenantId() == null || appOut.getToTenantId() <= 0)
                        && appOut.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    isNeedUploadAgreement = iOrderFeeService.isNeedUploadAgreementByLuge(appOut.getFromOrderId());
                }
            }
            appOut.setIsNeedUploadAgreement(isNeedUploadAgreement);
            if (isNeedUploadAgreement) {
                appOut.setLugeSignAgreeUrl("https://gc.oulu56.com/image/运输协议.jpg");
            }
            if (appOut.getVehicleLengh() != null && appOut.getVehicleStatus() != null && appOut.getVehicleStatus() > 0) {
                appOut.setVehicleLengh(getVehicleLengthName(appOut.getVehicleStatus().toString(), appOut.getVehicleLengh()));
            }
            if (appOut.getCarLengh() != null && appOut.getCarStatus() != null && appOut.getCarStatus() > 0) {
                appOut.setCarLengh(vehicleStaticDataUtil.getVehicleLengthNameById(appOut.getCarStatus().toString(), appOut.getCarLengh()));
            }
            String day = readisUtil.getSysCfg("ORDER_COST_REPORT_DAY","-1").getCfgValue();
            appOut.setOrderCostReportDay(day == null ? 0 : Integer.parseInt(day));
        }else {
            throw new BusinessException("未找到该订单[" + orderId + "]信息!");
        }
        appOut.setCarStatusName(appOut.getCarStatus()==null ? "" : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("VEHICLE_STATUS", appOut.getCarStatus() + "").getCodeName());
        appOut.setDesRegionName(appOut.getDesRegion()==null ? "" : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", appOut.getDesRegion() + "").getCodeName());
        appOut.setReciveTypeName(appOut.getReciveType()== null ? "" : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_TYPE", appOut.getReciveType() + "").getCodeName());
        appOut.setSourceRegionName(appOut.getSourceRegion()==null ? "" : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", appOut.getSourceRegion() + "").getCodeName());
        appOut.setVehicleStatusName(appOut.getVehicleStatus()==null? "": sysStaticDataRedisUtils.getSysStaticDataByCodeValue("VEHICLE_STATUS@ORD",appOut.getVehicleStatus()+"").getCodeName());
        //车辆类型
        appOut.setVehicleClassName(appOut.getVehicleClass() == null ? ""
                : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("VEHICLE_CLASS", appOut.getVehicleClass() + "").getCodeName());
        //值班司机初始化为主驾
//            appOut.setOnDutyDriverName(appOut.getCarDriverMan());
        appOut.setIncomeIsNeedBillName(appOut.getIncomeIsNeedBill()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("IS_NEED_BILL", appOut.getIncomeIsNeedBill() +"").getCodeName());
        appOut.getCostPaymentDaysInfo().setBalanceTypeName(appOut.getCostPaymentDaysInfo().getBalanceType()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("BALANCE_TYPE", appOut.getCostPaymentDaysInfo().getBalanceType()+"").getCodeName());
        appOut.getIncomePaymentDaysInfo().setBalanceTypeName(appOut.getIncomePaymentDaysInfo().getBalanceType()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("BALANCE_TYPE", appOut.getIncomePaymentDaysInfo().getBalanceType()+"").getCodeName());
        appOut.setIsNeedBillName(appOut.getIsNeedBill()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("IS_NEED_BILL_OA", appOut.getIsNeedBill() + "").getCodeName());
        //合同状态
        appOut.setLoadStateName(appOut.getLoadState() == null ? ""
                : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_STATE", appOut.getLoadState() + "").getCodeName());
        //订单状态
        appOut.setOrderStateName(appOut.getOrderState() == null ? ""
                : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ORDER_STATE", appOut.getOrderState() + "").getCodeName());
        appOut.setPaymentWayName(appOut.getPaymentWay() == null ? ""
                : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("PAYMENT_WAY", appOut.getPaymentWay() + "").getCodeName());
        appOut.setPreAmountFlagName(appOut.getPreAmountFlag()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("AMOUNT_FLAG", appOut.getPreAmountFlag() +"").getCodeName());
        //回单状态
        appOut.setReciveStateName(appOut.getReciveState() == null ? ""
                : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_STATE", appOut.getReciveState() + "").getCodeName());
        appOut.setSignAgreeStateName(appOut.getSignAgreeState()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SIGN_AGREE_STATE", appOut.getSignAgreeState()+"").getCodeName());
        appOut.setReciveCityName(appOut.getReciveCityId()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", appOut.getReciveCityId()+"").getCodeName());
        appOut.setReciveProvinceName(appOut.getReciveProvinceId()==null?"":getSysStaticData("SYS_PROVINCE", appOut.getReciveProvinceId()+"").getCodeName());
        appOut.setFinalAmountFlagName(appOut.getFinalAmountFlag()==null?"":getSysStaticData("AMOUNT_FLAG", appOut.getFinalAmountFlag()+"").getCodeName());
        appOut.setPreOilFeeSum((appOut.getPreOilFee() == null ? 0 : appOut.getPreOilFee()) +  (appOut.getPreOilVirtualFee() == null ? 0 : appOut.getPreOilVirtualFee()));
        //补贴只计算主驾补贴
        appOut.setSubsidyFeeSum((appOut.getSalary() == null ? 0 : appOut.getSalary())  + (appOut.getDriverSwitchSubsidy() == null ? 0 : appOut.getDriverSwitchSubsidy()));
        appOut.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(appOut.getTenantId(), appOut.getOrgId().longValue()));
        appOut.setTotalCost((appOut.getFixedCost() == null ? 0 : appOut.getFixedCost()) + (appOut.getOperCost() == null ? 0 : appOut.getOperCost()) + (appOut.getFee() == null ? 0 : appOut.getFee()) + (appOut.getChangeCost() == null ? 0 : appOut.getChangeCost()));
        appOut.setArrivePaymentStateName(appOut.getArrivePaymentState()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("AMOUNT_FLAG", appOut.getArrivePaymentState()+"").getCodeName());
        appOut.setGoodsTypeName(appOut.getGoodsType()==null?"":sysStaticDataRedisUtils.getSysStaticDataByCodeValue("GOODS_TYPE", appOut.getGoodsType()+"").getCodeName());
        if (appOut.getTransitLineInfos() != null && appOut.getTransitLineInfos().size() > 0) {
            for (OrderTransitLineInfo orderTransitLineInfo : appOut.getTransitLineInfos()) {
                if(orderTransitLineInfo.getProvince() != null){
                    orderTransitLineInfo.setProvinceName(getSysStaticData("SYS_PROVINCE", String.valueOf(orderTransitLineInfo.getProvince())).getCodeName());
                }
                if(orderTransitLineInfo.getRegion() != null){
                    orderTransitLineInfo.setRegionName(getSysStaticData("SYS_CITY", String.valueOf(orderTransitLineInfo.getRegion())).getCodeName());
                }
                if(orderTransitLineInfo.getCounty() != null){
                    orderTransitLineInfo.setCountyName(getSysStaticData("SYS_DISTRICT", String.valueOf(orderTransitLineInfo.getCounty())).getCodeName());
                }
            }
        }
        appOut.setPreTotalFee(orderDetailsAppDto.getPreTotalFee());
        //预估成本
        OrderFeeExt orderFeeExt = iOrderFeeExtService.getOrderFeeExt(orderId);
        appOut.setEstFee(orderFeeExt.getEstFee());
        if (appOut.getCarStatus()!=null) {
            appOut.setCarStatusName(readisUtil.getSysStaticDatabyId("VEHICLE_STATUS", appOut.getCarStatus().toString()).getCodeName());
        }
        return appOut;
    }

    public  String getVehicleLengthName(String vehicleStatus, String vehicleLength) {
        String name = "";

        SysStaticData staticData = readisUtil.getSysStaticData("VEHICLE_STATUS", vehicleStatus);
        List<SysStaticData> lengthList = readisUtil.getSysStaticDataList("VEHICLE_LENGTH");
        if (null != staticData && CollectionUtils.isNotEmpty(lengthList)) {
            for (SysStaticData lengthData : lengthList) {
                if (Objects.equals(staticData.getCodeId(),lengthData.getCodeId()) && lengthData.getCodeValue().equals(vehicleLength)) {
                    name = lengthData.getCodeName();
                    break;
                }
            }

        }
        return name;
    }

    @Override
    public GetOrderDetailForUpdateDto getOrderDetailForUpdate(Long orderId, Integer updateType, String accessToken) {
        if(orderId == null || orderId <= 0) {
            throw new BusinessException("缺少订单号！");
        }
        // 查询订单详情
        OrderDetailsDto orderDetail = this.queryOrderDetails(orderId, OrderConsts.orderDetailsType.UPDATE, accessToken);
        GetOrderDetailForUpdateDto dto = new GetOrderDetailForUpdateDto();
        Map<String,Object> rtnMap=null;
        switch (updateType) {
            case 1:rtnMap=this.getOrderDetailOfBasic(orderDetail);break;// 获取基础信息
            case 2:rtnMap=this.getOrderDetailOfIncome(orderDetail);break;// 获取收入信息
            case 3:rtnMap=this.getOrderDetailOfDispatch(orderDetail);break;// 获取调度信息
            case 4:rtnMap=this.getOrderDetailOfOther(orderDetail);break;// 获取归属信息
            default:rtnMap=new HashMap<String,Object>();
        }
        rtnMap.put("orderId", orderDetail.getOrderInfo().getOrderId());
        rtnMap.put("orderState", orderDetail.getOrderInfo().getOrderState());
        if (orderDetail.getOrderInfo().getOrderState() != null) {
            rtnMap.put("orderStateName", getSysStaticData("ORDER_STATE", String.valueOf(orderDetail.getOrderInfo().getOrderState())).getCodeName());
        } else {
            rtnMap.put("orderStateName", "");
        }
        rtnMap.put("sourceProvince", orderDetail.getOrderInfo().getSourceProvince());
        rtnMap.put("sourceProvinceName", orderDetail.getOrderInfo().getSourceProvinceName());
        rtnMap.put("sourceRegion", orderDetail.getOrderInfo().getSourceRegion());
        rtnMap.put("sourceRegionName", orderDetail.getOrderInfo().getSourceRegionName());
        rtnMap.put("sourceCounty", orderDetail.getOrderInfo().getSourceCounty());
        rtnMap.put("sourceCountyName", orderDetail.getOrderInfo().getSourceCountyName());
        rtnMap.put("desProvince", orderDetail.getOrderInfo().getDesProvince());
        rtnMap.put("desProvinceName", orderDetail.getOrderInfo().getDesProvinceName());
        rtnMap.put("desRegion", orderDetail.getOrderInfo().getDesRegion());
        rtnMap.put("desRegionName", orderDetail.getOrderInfo().getDesRegionName());
        rtnMap.put("desCounty", orderDetail.getOrderInfo().getDesCounty());
        rtnMap.put("desCountyName", orderDetail.getOrderInfo().getDesCountyName());
        rtnMap.put("remark", orderDetail.getOrderInfo().getRemark());
        rtnMap.put("preAmountFlag", orderDetail.getOrderInfoExt().getPreAmountFlag());
        rtnMap.put("dependTime", orderDetail.getOrderScheduler().getDependTime());
        rtnMap.put("fromOrderId", orderDetail.getOrderInfo().getFromOrderId());
        rtnMap.put("toOrderId", orderDetail.getOrderInfo().getToOrderId());

        BeanUtil.copyProperties(rtnMap, dto);
        return dto;
    }

    //订单修改的基础信息
    private Map<String,Object> getOrderDetailOfBasic(OrderDetailsDto orderDetail){
        Map<String,Object> rtnMap=new HashMap<String,Object>();
        rtnMap.put("customName", orderDetail.getOrderGoods().getCustomName());
        rtnMap.put("companyName", orderDetail.getOrderGoods().getCompanyName());
        rtnMap.put("linkName", orderDetail.getOrderGoods().getLinkName());
        rtnMap.put("linkPhone", orderDetail.getOrderGoods().getLinkPhone());
        rtnMap.put("address", orderDetail.getOrderGoods().getAddress());
        rtnMap.put("clientContractUrl", orderDetail.getOrderScheduler().getClientContractUrl());
        rtnMap.put("clientContractId", orderDetail.getOrderScheduler().getClientContractId());
        rtnMap.put("sourceId", orderDetail.getOrderScheduler().getSourceId());
        rtnMap.put("sourceCode", orderDetail.getOrderScheduler().getSourceCode());
        rtnMap.put("sourceName", orderDetail.getOrderScheduler().getSourceName());
        rtnMap.put("source", orderDetail.getOrderGoods().getSource());
        rtnMap.put("des", orderDetail.getOrderGoods().getDes());
        rtnMap.put("addrDtl", orderDetail.getOrderGoods().getAddrDtl());
        rtnMap.put("desDtl", orderDetail.getOrderGoods().getDesDtl());
        rtnMap.put("nand", orderDetail.getOrderGoods().getNand());
        rtnMap.put("eand", orderDetail.getOrderGoods().getEand());
        rtnMap.put("nandDes", orderDetail.getOrderGoods().getNandDes());
        rtnMap.put("eandDes", orderDetail.getOrderGoods().getEandDes());
        rtnMap.put("navigatSourceLocation", orderDetail.getOrderGoods().getNavigatSourceLocation());
        rtnMap.put("navigatDesLocation", orderDetail.getOrderGoods().getNavigatDesLocation());
        rtnMap.put("arriveTime", orderDetail.getOrderScheduler().getArriveTime());
        rtnMap.put("transitLineInfos", orderDetail.getTransitLineInfos());
        rtnMap.put("distance", orderDetail.getOrderScheduler().getDistance());//客户公里数
        rtnMap.put("mileageNumber", orderDetail.getOrderScheduler().getMileageNumber());//承运方公里数
        rtnMap.put("goodsName", orderDetail.getOrderGoods().getGoodsName());
        rtnMap.put("goodsType", orderDetail.getOrderGoods().getGoodsType());
        rtnMap.put("square", orderDetail.getOrderGoods().getSquare());
        rtnMap.put("weight", orderDetail.getOrderGoods().getWeight());
        rtnMap.put("vehicleLengh", orderDetail.getOrderGoods().getVehicleLengh());
        rtnMap.put("vehicleStatus", orderDetail.getOrderGoods().getVehicleStatus());
        rtnMap.put("customNumber", orderDetail.getOrderGoods().getCustomNumber());
        rtnMap.put("reciveName", orderDetail.getOrderGoods().getReciveName());
        rtnMap.put("recivePhone", orderDetail.getOrderGoods().getRecivePhone());
        rtnMap.put("reciveType", orderDetail.getOrderGoods().getReciveType());
        rtnMap.put("reciveProvinceId", orderDetail.getOrderGoods().getReciveProvinceId());
        rtnMap.put("reciveProvinceName", orderDetail.getOrderGoods().getReciveProvinceName());
        rtnMap.put("reciveCityId", orderDetail.getOrderGoods().getReciveCityId());
        rtnMap.put("reciveCityName", orderDetail.getOrderGoods().getReciveCityName());
        rtnMap.put("reciveAddr", orderDetail.getOrderGoods().getReciveAddr());
        return rtnMap;
    }

    //订单修改的收入信息
    private Map<String,Object> getOrderDetailOfIncome(OrderDetailsDto orderDetail){
        Map<String,Object> rtnMap=new HashMap<String,Object>();
        rtnMap.put("distance", orderDetail.getOrderScheduler().getDistance());//客户公里数
        rtnMap.put("mileageNumber", orderDetail.getOrderScheduler().getMileageNumber());//承运方公里数
        rtnMap.put("square", orderDetail.getOrderGoods().getSquare());
        rtnMap.put("weight", orderDetail.getOrderGoods().getWeight());
        rtnMap.put("isUrgent", orderDetail.getOrderScheduler().getIsUrgent());
        rtnMap.put("priceUnit", orderDetail.getOrderFee().getPriceUnit());
        rtnMap.put("priceEnum", orderDetail.getOrderFee().getPriceEnum());
        rtnMap.put("costPrice", orderDetail.getOrderFee().getCostPrice());
        rtnMap.put("prePayCash", orderDetail.getOrderFee().getPrePayCash());
        rtnMap.put("prePayEquivalenceCardAmount", orderDetail.getOrderFee().getPrePayEquivalenceCardAmount());
        rtnMap.put("prePayEquivalenceCardType", orderDetail.getOrderFee().getAfterPayEquivalenceCardType());
        rtnMap.put("prePayEquivalenceCardNumber", orderDetail.getOrderFee().getPrePayEquivalenceCardNumber());
        rtnMap.put("afterPayCash", orderDetail.getOrderFee().getAfterPayCash());
        rtnMap.put("afterPayEquivalenceCardAmount", orderDetail.getOrderFee().getAfterPayEquivalenceCardAmount());
        rtnMap.put("afterPayEquivalenceCardType", orderDetail.getOrderFee().getAfterPayEquivalenceCardType());
        rtnMap.put("afterPayEquivalenceCardNumber", orderDetail.getOrderFee().getAfterPayEquivalenceCardNumber());
        rtnMap.put("incomePaymentDaysInfo", orderDetail.getIncomePaymentDaysInfo());
        rtnMap.put("orderIncomePermission", orderDetail.getOrderIncomePermission());
        return rtnMap;
    }

    //订单修改的调度信息
    private Map<String,Object> getOrderDetailOfDispatch(OrderDetailsDto orderDetail){
        Map<String,Object> rtnMap=new HashMap<String,Object>();
        rtnMap.put("fromTenantId",orderDetail.getOrderInfo().getFromTenantId());
        rtnMap.put("fromOrderId",orderDetail.getOrderInfo().getFromOrderId());
        rtnMap.put("toOrderId",orderDetail.getOrderInfo().getToOrderId());
        rtnMap.put("orderCostPermission", orderDetail.getOrderCostPermission());
        rtnMap.put("toTenantId",orderDetail.getOrderInfo().getToTenantId());
        rtnMap.put("toTenantName",orderDetail.getOrderInfo().getToTenantName());
        rtnMap.put("isNeedBill",orderDetail.getOrderInfo().getIsNeedBill());
        rtnMap.put("isTransit",orderDetail.getOrderInfo().getIsTransit());

        rtnMap.put("isBackhaul",orderDetail.getOrderInfoExt().getIsBackhaul());
        rtnMap.put("backhaulPrice",orderDetail.getOrderInfoExt().getBackhaulPrice());
        rtnMap.put("backhaulLinkMan",orderDetail.getOrderInfoExt().getBackhaulLinkMan());
        rtnMap.put("runWay",orderDetail.getOrderInfoExt().getRunWay());
        rtnMap.put("oilUseType",orderDetail.getOrderInfoExt().getOilUseType());
        rtnMap.put("backhaulLinkManId",orderDetail.getOrderInfoExt().getBackhaulLinkManId());
        rtnMap.put("backhaulLinkManBill",orderDetail.getOrderInfoExt().getBackhaulLinkManBill());
        rtnMap.put("paymentWay",orderDetail.getOrderInfoExt().getPaymentWay());
        rtnMap.put("capacityOil",orderDetail.getOrderInfoExt().getCapacityOil());
        rtnMap.put("runOil",orderDetail.getOrderInfoExt().getRunOil());
        rtnMap.put("oilIsNeedBill",orderDetail.getOrderInfoExt().getOilIsNeedBill());

        rtnMap.put("acctName",orderDetail.getOrderFee().getAcctName());
        rtnMap.put("acctNo",orderDetail.getOrderFee().getAcctNo());
        rtnMap.put("guidePrice",orderDetail.getOrderFee().getGuidePrice());
        rtnMap.put("insuranceFee",orderDetail.getOrderFee().getInsuranceFee());
        rtnMap.put("totalFee",orderDetail.getOrderFee().getTotalFee());
        rtnMap.put("preTotalFee",orderDetail.getOrderFee().getPreTotalFee());
        rtnMap.put("preCashFee",orderDetail.getOrderFee().getPreCashFee());
        rtnMap.put("preOilVirtualFee",orderDetail.getOrderFee().getPreOilVirtualFee());
        rtnMap.put("preOilFee",orderDetail.getOrderFee().getPreOilFee());
        rtnMap.put("preEtcFee",orderDetail.getOrderFee().getPreEtcFee());
        rtnMap.put("finalFee",orderDetail.getOrderFee().getFinalFee());
        rtnMap.put("finalScale",orderDetail.getOrderFee().getFinalScale());
        rtnMap.put("preTotalScale",orderDetail.getOrderFee().getPreTotalScale());
        rtnMap.put("arrivePaymentFee",orderDetail.getOrderFee().getArrivePaymentFee());
        rtnMap.put("arrivePaymentFeeScale",orderDetail.getOrderFee().getArrivePaymentFeeScale());
        rtnMap.put("preCashScale",orderDetail.getOrderFee().getPreCashScale());
        rtnMap.put("preOilVirtualScale",orderDetail.getOrderFee().getPreOilVirtualScale());
        rtnMap.put("preOilScale",orderDetail.getOrderFee().getPreOilScale());
        rtnMap.put("preEtcScale",orderDetail.getOrderFee().getPreEtcScale());
        rtnMap.put("preTotalScaleStandard",orderDetail.getOrderFee().getPreTotalScaleStandard());
        rtnMap.put("preCashScaleStandard",orderDetail.getOrderFee().getPreCashScaleStandard());
        rtnMap.put("preOilVirtualScaleStandard",orderDetail.getOrderFee().getPreOilVirtualScaleStandard());
        rtnMap.put("preOilScaleStandard",orderDetail.getOrderFee().getPreOilScaleStandard());
        rtnMap.put("preEtcScaleStandard",orderDetail.getOrderFee().getPreEtcScaleStandard());

        rtnMap.put("estFee",orderDetail.getOrderFeeExt().getEstFee());
        rtnMap.put("oilLitreTotal",orderDetail.getOrderFeeExt().getOilLitreTotal());
        rtnMap.put("oilLitreVirtual",orderDetail.getOrderFeeExt().getOilLitreVirtual());
        rtnMap.put("oilLitreEntity",orderDetail.getOrderFeeExt().getOilLitreEntity());
        rtnMap.put("oilPrice",orderDetail.getOrderFeeExt().getOilPrice());
        rtnMap.put("salary",orderDetail.getOrderFeeExt().getSalary());
        rtnMap.put("copilotSalary",orderDetail.getOrderFeeExt().getCopilotSalary());
        rtnMap.put("driverSwitchSubsidy",orderDetail.getOrderFeeExt().getDriverSwitchSubsidy());
        rtnMap.put("pontage",orderDetail.getOrderFeeExt().getPontage());
        rtnMap.put("subsidyTime",orderDetail.getOrderFeeExt().getSubsidyTime());
        rtnMap.put("copilotSubsidyTime",orderDetail.getOrderFeeExt().getCopilotSubsidyTime());
        rtnMap.put("driverSubsidyDays",orderDetail.getDriverSubsidyDays());
        rtnMap.put("pontagePer",orderDetail.getOrderFeeExt().getPontagePer());

        rtnMap.put("appointWay",orderDetail.getOrderScheduler().getAppointWay());
        rtnMap.put("reciveAddr",orderDetail.getOrderScheduler().getReciveAddr());
        rtnMap.put("isCollection",orderDetail.getOrderScheduler().getIsCollection());
        rtnMap.put("plateNumber",orderDetail.getOrderScheduler().getPlateNumber());
        rtnMap.put("vehicleCode",orderDetail.getOrderScheduler().getVehicleCode());
        rtnMap.put("carLengh",orderDetail.getOrderScheduler().getCarLengh());
        rtnMap.put("collectionUserPhone",orderDetail.getOrderScheduler().getCollectionUserPhone());
        rtnMap.put("collectionUserName",orderDetail.getOrderScheduler().getCollectionUserName());
        rtnMap.put("collectionUserId",orderDetail.getOrderScheduler().getCollectionUserId());
        rtnMap.put("collectionName",orderDetail.getOrderScheduler().getCollectionName());
        rtnMap.put("carStatus",orderDetail.getOrderScheduler().getCarStatus());
        rtnMap.put("carDriverMan",orderDetail.getOrderScheduler().getCarDriverMan());
        rtnMap.put("carDriverId",orderDetail.getOrderScheduler().getCarDriverId());
        rtnMap.put("carDriverPhone",orderDetail.getOrderScheduler().getCarDriverPhone());
        rtnMap.put("vehicleClass",orderDetail.getOrderScheduler().getVehicleClass());
        if (orderDetail.getOrderScheduler().getVehicleClass() != null) {
            rtnMap.put("vehicleClassName", getSysStaticData("VEHICLE_CLASS", String.valueOf(orderDetail.getOrderScheduler().getVehicleClass())).getCodeName());
        } else {
            rtnMap.put("vehicleClassName","");
        }

        rtnMap.put("licenceType",orderDetail.getOrderScheduler().getLicenceType());
        rtnMap.put("trailerId",orderDetail.getOrderScheduler().getTrailerId());
        rtnMap.put("trailerPlate",orderDetail.getOrderScheduler().getTrailerPlate());
        rtnMap.put("copilotMan",orderDetail.getOrderScheduler().getCopilotMan());
        rtnMap.put("copilotPhone",orderDetail.getOrderScheduler().getCopilotPhone());
        rtnMap.put("copilotUserId",orderDetail.getOrderScheduler().getCopilotUserId());
        rtnMap.put("billReceiverMobile",orderDetail.getOrderScheduler().getBillReceiverMobile());
        rtnMap.put("billReceiverName",orderDetail.getOrderScheduler().getBillReceiverName());
        rtnMap.put("billReceiverUserId",orderDetail.getOrderScheduler().getBillReceiverUserId());
        rtnMap.put("dispatcherId",orderDetail.getOrderScheduler().getDispatcherId());
        rtnMap.put("dispatcherName",orderDetail.getOrderScheduler().getDispatcherName());
        rtnMap.put("dispatcherBill",orderDetail.getOrderScheduler().getDispatcherBill());
        rtnMap.put("onDutyDriverId",orderDetail.getOrderScheduler().getOnDutyDriverId());
        rtnMap.put("onDutyDriverName",orderDetail.getOrderScheduler().getOnDutyDriverName());
        rtnMap.put("onDutyDriverPhone",orderDetail.getOrderScheduler().getOnDutyDriverPhone());
        rtnMap.put("schemes", orderDetail.getSchemes());//分配油站
        rtnMap.put("oilCardInfos", orderDetail.getOilCardInfos());//报账模式下添加的油卡
        rtnMap.put("costPaymentDaysInfo", orderDetail.getCostPaymentDaysInfo());
        rtnMap.put("reciveAddr", orderDetail.getOrderScheduler().getReciveAddr());
        rtnMap.put("oilAccountType", orderDetail.getOrderFeeExt().getOilAccountType());
        rtnMap.put("oilBillType", orderDetail.getOrderFeeExt().getOilBillType());
        rtnMap.put("oilConsumer", orderDetail.getOrderFeeExt().getOilConsumer());
        return rtnMap;
    }

    //订单修改的归属信息
    private Map<String,Object> getOrderDetailOfOther(OrderDetailsDto orderDetail){
        Map<String,Object> rtnMap=new HashMap<String,Object>();
        rtnMap.put("orgId",orderDetail.getOrderInfo().getOrgId());
        rtnMap.put("localUser",orderDetail.getOrderGoods().getLocalUser());
        rtnMap.put("localUserName",orderDetail.getOrderGoods().getLocalUserName());
        rtnMap.put("localPhone",orderDetail.getOrderGoods().getLocalPhone());
        return rtnMap;
    }

    @Override
    public void updateOrder(UpdateOrderVo updateOrderVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        int updateType = updateOrderVo.getUpdateType();
        Long orderId = updateOrderVo.getOrderId();
        if(orderId == null || orderId <= 0) {
            throw new BusinessException("缺少订单号！");
        }

        OrderInfo orderInfo= orderInfoService.getOrder(orderId);
        if(orderInfo==null) {
            throw new BusinessException("未找到订单信息！");
        }

        OrderDetailsDto orderDetail = null;

        // 订单修改状态不为空  部分修改/特殊修改
        if(orderInfo.getOrderUpdateState() != null
                && (orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION
                || orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)){

            // 封装订单信息
            orderDetail=this.getOrderDetailOut(orderId, accessToken);
        }

        if(orderDetail==null) {
            // 重新查询订单信息
            orderDetail = this.queryOrderDetails(orderId,OrderConsts.orderDetailsType.UPDATE, accessToken);
        }

        switch (updateType) {
            case 1 : this.updateBasicOrderInfo(orderDetail, updateOrderVo); break;//修改基础信息
            case 2 : this.updateIncomeOrderInfo(orderDetail, updateOrderVo); break;//修改收入信息
            case 3 : this.updateDispatchOrderInfo(orderDetail, updateOrderVo); break;//修改调度信息
            case 4 : this.updateOtherOrderInfo(orderDetail, updateOrderVo); break;//修改归属信息
            default:throw new BusinessException("修改订单参数错误！");
        }

        String oilCardNum = updateOrderVo.getOilCardNum();
        if(updateType != 3 && orderDetail.getOilCardInfos() != null) {
            StringBuilder oilCardNumSB=new StringBuilder();
            for (OrderOilCardInfo cardInfo : orderDetail.getOilCardInfos()) {
                if (StringUtils.isNotEmpty(cardInfo.getOilCardNum())) {
                    oilCardNumSB.append(",").append(cardInfo.getOilCardNum());
                }
            }
            if(StringUtils.isNotBlank(oilCardNumSB.toString())) {
                oilCardNum=oilCardNumSB.toString().substring(1);
            }
        }

        //数据校验
        this.billingSaveCheck(orderDetail.getOrderInfo(),orderDetail.getOrderGoods(),orderDetail.getOrderScheduler()
                ,orderDetail.getOrderFee(),orderDetail.getSchemes(), accessToken);
        //修改订单
        orderInfoService.updateOrderInfoByPortionTwo(orderDetail.getOrderInfo(), orderDetail.getOrderFee(), orderDetail.getOrderGoods()
                , orderDetail.getOrderInfoExt(), orderDetail.getOrderFeeExt(), orderDetail.getOrderScheduler()
                , orderDetail.getSchemes(), orderDetail.getUpdateType(),StringUtils.isBlank(oilCardNum)?null:Arrays.asList(oilCardNum.split(","))
                ,orderDetail.getCostPaymentDaysInfo(),orderDetail.getIncomePaymentDaysInfo(),orderDetail.getOilCardInfos()
                ,orderDetail.getTransitLineInfos(), loginInfo, accessToken);
    }

    /**
     * 通过订单版本记录获取订单信息
     * @param orderId
     * @return
     * @throws Exception
     */
    private OrderDetailsDto getOrderDetailOut(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        OrderInfoVer orderInfoVer = orderInfoVerService.getOrderInfoVer(orderId);
        if(orderInfoVer == null) {
            return null;
        }

        OrderDetailsDto orderDetail = new OrderDetailsDto();
        OrderInfo orderInfo=new OrderInfo();
        BeanUtil.copyProperties(orderInfoVer, orderInfo);
        orderDetail.setOrderInfo(orderInfo);

        OrderInfoExtVer orderInfoExtVer = orderInfoExtVerService.getOrderInfoExtVer(orderId);
        if(orderInfoExtVer != null) {
            OrderInfoExt orderInfoExt = new OrderInfoExt();
            BeanUtil.copyProperties(orderInfoExtVer, orderInfoExt);
            orderDetail.setOrderInfoExt(orderInfoExt);
        }

        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
        if(orderInfoExt != null) {
            orderDetail.getOrderInfoExt().setPreAmountFlag(orderInfoExt.getPreAmountFlag());
        }

        OrderFeeVer orderFeeVer = orderFeeVerService.getOrderFeeVer(orderId);
        if(orderFeeVer != null) {
            OrderFee orderFee = new OrderFee();
            BeanUtil.copyProperties(orderFeeVer, orderFee);
            orderDetail.setOrderFee(orderFee);
        }

        OrderFeeExtVer orderFeeExtVer = orderFeeExtVerService.getOrderFeeExtVer(orderId);
        if(orderFeeExtVer != null) {
            OrderFeeExt orderFeeExt = new OrderFeeExt();
            BeanUtil.copyProperties(orderFeeExtVer, orderFeeExt);
            orderDetail.setOrderFeeExt(orderFeeExt);
        }

        OrderSchedulerVer orderSchedulerVer = orderSchedulerVerService.getOrderSchedulerVer(orderId);
        if(orderSchedulerVer != null) {
            OrderScheduler orderScheduler=new OrderScheduler();
            BeanUtil.copyProperties(orderSchedulerVer, orderScheduler);
            orderDetail.setOrderScheduler(orderScheduler);
        }

        OrderGoodsVer orderGoodsVer = orderGoodsVerService.getOrderGoodsVer(orderId);
        if(orderGoodsVer != null) {
            OrderGoods orderGoods=new OrderGoods();
            BeanUtil.copyProperties(orderGoodsVer, orderGoods);
            orderDetail.setOrderGoods(orderGoods);
        }

        List<OrderTransitLineInfoVer> lineInfoVers =  orderTransitLineInfoVerService.queryOrderTransitLineInfoByOrderId(orderId, OrderConsts.IS_UPDATE.UPDATE);
        if(lineInfoVers!=null&&lineInfoVers.size()>0) {
            List<OrderTransitLineInfo> lineInfos =new ArrayList<OrderTransitLineInfo>();
            for (OrderTransitLineInfoVer orderTransitLineInfoVer : lineInfoVers) {
                OrderTransitLineInfo orderTransitLineInfo=new OrderTransitLineInfo();
                BeanUtil.copyProperties(orderTransitLineInfoVer, orderTransitLineInfo);
                lineInfos.add(orderTransitLineInfo);
            }
            orderDetail.setTransitLineInfos(lineInfos);
        }

        List<OrderPaymentDaysInfoVer> costDaysInfos = orderPaymentDaysInfoVerService.queryOrderPaymentDaysInfoVer(orderId,
                OrderConsts.PAYMENT_DAYS_TYPE.COST,OrderConsts.IS_UPDATE.UPDATE);

        OrderPaymentDaysInfo orderPaymentDaysInfo=new OrderPaymentDaysInfo();
        if (costDaysInfos != null && costDaysInfos.size() > 0) {
            BeanUtil.copyProperties(costDaysInfos.get(0), orderPaymentDaysInfo);
        }else {
            BeanUtil.copyProperties(orderPaymentDaysInfo, orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.COST));
        }
        orderDetail.setCostPaymentDaysInfo(orderPaymentDaysInfo);

        List<OrderPaymentDaysInfoVer> incomeDaysInfos = orderPaymentDaysInfoVerService.queryOrderPaymentDaysInfoVer(orderId,
                OrderConsts.PAYMENT_DAYS_TYPE.INCOME,OrderConsts.IS_UPDATE.UPDATE);
        OrderPaymentDaysInfo orderIncomePaymentDaysInfo=new OrderPaymentDaysInfo();
        if (incomeDaysInfos != null && incomeDaysInfos.size() > 0) {
            BeanUtil.copyProperties(incomeDaysInfos.get(0), orderIncomePaymentDaysInfo);
        }else {
            BeanUtil.copyProperties(orderIncomePaymentDaysInfo, orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME));
        }
        orderDetail.setIncomePaymentDaysInfo(orderIncomePaymentDaysInfo);

        if(orderDetail.getOrderInfoExt().getPaymentWay()!=null&&orderDetail.getOrderInfoExt().getPaymentWay()== OrderConsts.PAYMENT_WAY.COST) {
            List<OrderOilDepotSchemeVer> schemeVers = orderOilDepotSchemeVerService.getOrderOilDepotSchemeVerByOrderId(orderId, true, loginInfo);
            if(schemeVers!=null) {
                List<OrderOilDepotScheme> schemes = new ArrayList<OrderOilDepotScheme>();
                for (OrderOilDepotSchemeVer orderOilDepotSchemeVer : schemeVers) {
                    OrderOilDepotScheme scheme = new OrderOilDepotScheme();
                    BeanUtil.copyProperties(orderOilDepotSchemeVer, scheme);
                    schemes.add(scheme);
                }
                orderDetail.setSchemes(schemes);
            }
        }

        List<OrderOilCardInfoVer> oilCardInfoVers = orderOilCardInfoVerService.queryOrderOilCardInfoVerByOrderId(orderId, null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
        if(oilCardInfoVers!=null&&oilCardInfoVers.size()>0) {
            List<OrderOilCardInfo> oilCardInfos =new ArrayList<OrderOilCardInfo>(oilCardInfoVers.size());
            for (OrderOilCardInfoVer orderOilCardInfoVer : oilCardInfoVers) {
                OrderOilCardInfo orderOilCardInfo=new OrderOilCardInfo();
                BeanUtil.copyProperties(orderOilCardInfoVer, orderOilCardInfo);
                oilCardInfos.add(orderOilCardInfo);
            }
            orderDetail.setOilCardInfos(oilCardInfos);
        }

        if ((orderInfo.getIsTransit() == null || orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_NO)
                && (orderInfo.getFromOrderId() == null || orderInfo.getFromOrderId() <= 0)) {// 自有单
            if (orderInfo.getOrderState()!=null && orderInfo.getOrderState() < OrderConsts.ORDER_STATE.TO_BE_LOAD) {// 待装货前
                orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_ALL);
            } else if (orderInfo.getOrderState()!=null && orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD
                    && orderInfo.getOrderState() < OrderConsts.ORDER_STATE.FINISH) {
                // 待装货 -->  已完成之前
                orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
            } else {
                orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);
            }
        } else if (orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) {// 外发单
            if ((orderInfo.getToOrderId() == null || orderInfo.getToOrderId() <= 0)
                    && (orderInfo.getFromOrderId() == null || orderInfo.getFromOrderId() <= 0)) {
                // 暂未接单 并且不是在线接单
                if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.TO_BE_LOAD) {// 待装货前
                    orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_ALL);
                } else if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD
                        && orderInfo.getOrderState() < OrderConsts.ORDER_STATE.FINISH) {
                    // 待装货 --> 已完成之前
                    orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
                } else {
                    orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);
                }
            } else {
                orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
            }
        }else{
            orderDetail.setUpdateState(OrderConsts.UPDATE_STATE.UPDATE_FORBID);// 历史单不予修改
        }

        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//转单
            orderDetail.setUpdateType(OrderConsts.UPDATE_TYPE.IS_TRANSFER_ORDER);
            if (orderInfo.getFromTenantId() != null && orderInfo.getFromTenantId() > 0 ) {//来自接单
                orderDetail.setUpdateType(OrderConsts.UPDATE_TYPE.MIDDLE_ORDER);
            }
        }else{
            if (orderInfo.getFromTenantId() != null && orderInfo.getFromTenantId() > 0) {//来自接单
                orderDetail.setUpdateType(OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER);
            }else{//自有单
                orderDetail.setUpdateType(OrderConsts.UPDATE_TYPE.OWN_ORDER);
            }
        }
        //临时车队订单不能修改
        if (orderDetail.getOrderInfoExt().getIsTempTenant() != null &&
                orderDetail.getOrderInfoExt().getIsTempTenant() == OrderConsts.IS_TEMP_TENANT.YES) {
            orderDetail.setUpdateType(OrderConsts.UPDATE_TYPE.MIDDLE_ORDER);
        }

        return orderDetail;
    }

    /**
     * 修改基础信息
     * @param orderDetail
     * @param updateOrderVo
     * @throws Exception
     */
    private void updateBasicOrderInfo(OrderDetailsDto orderDetail, UpdateOrderVo updateOrderVo) {
        LocalDateTime str=updateOrderVo.getDependTime();
        if(StringUtils.isBlank(String.valueOf(str))) {
            throw new BusinessException("请选择靠台时间！");
        }

        orderDetail.getOrderScheduler().setDependTime(updateOrderVo.getDependTime());
        orderDetail.getOrderGoods().setCompanyName(updateOrderVo.getCompanyName());
        orderDetail.getOrderGoods().setLinkName(updateOrderVo.getLinkName());
        orderDetail.getOrderGoods().setLinkPhone(updateOrderVo.getLinkPhone());
        orderDetail.getOrderGoods().setLineName(updateOrderVo.getLinkName());
        orderDetail.getOrderGoods().setLinePhone(updateOrderVo.getLinkPhone());
        orderDetail.getOrderGoods().setAddress(updateOrderVo.getAddress());
        orderDetail.getOrderScheduler().setClientContractId(updateOrderVo.getClientContractId());
        orderDetail.getOrderScheduler().setClientContractUrl(updateOrderVo.getClientContractUrl());
        orderDetail.getOrderScheduler().setSourceId(updateOrderVo.getSourceId());
        orderDetail.getOrderScheduler().setSourceCode(updateOrderVo.getSourceCode());
        orderDetail.getOrderScheduler().setSourceName(updateOrderVo.getSourceName());
        orderDetail.getOrderGoods().setSource(updateOrderVo.getSource());
        orderDetail.getOrderGoods().setDes(updateOrderVo.getDes());
        orderDetail.getOrderGoods().setAddrDtl(updateOrderVo.getAddrDtl());
        orderDetail.getOrderGoods().setDesDtl(updateOrderVo.getDesDtl());
        orderDetail.getOrderInfo().setSourceProvince(updateOrderVo.getSourceProvince());
        orderDetail.getOrderInfo().setSourceRegion(updateOrderVo.getSourceRegion());
        orderDetail.getOrderInfo().setSourceCounty(updateOrderVo.getSourceCounty());
        orderDetail.getOrderInfo().setDesProvince(updateOrderVo.getDesProvince());
        orderDetail.getOrderInfo().setDesRegion(updateOrderVo.getDesRegion());
        orderDetail.getOrderInfo().setDesCounty(updateOrderVo.getDesCounty());
        orderDetail.getOrderGoods().setNand(updateOrderVo.getNand());
        orderDetail.getOrderGoods().setEand(updateOrderVo.getEand());
        orderDetail.getOrderGoods().setNandDes(updateOrderVo.getNandDes());
        orderDetail.getOrderGoods().setEandDes(updateOrderVo.getEandDes());
        orderDetail.getOrderGoods().setNavigatSourceLocation(updateOrderVo.getNavigatSourceLocation());
        orderDetail.getOrderGoods().setNavigatDesLocation(updateOrderVo.getNavigatDesLocation());
        if(updateOrderVo.getArriveTime() == null) {
            throw new BusinessException("请输入到达时效！");
        }
        orderDetail.getOrderScheduler().setArriveTime(updateOrderVo.getArriveTime());
        orderDetail.setTransitLineInfos(updateOrderVo.getTransitLineInfos());

        //设置经停点
        orderDetail.getOrderScheduler().setDistance(mul(StringUtils.isEmpty(updateOrderVo.getDistance()) ? "0" : updateOrderVo.getDistance(), "1000"));
        orderDetail.getOrderScheduler().setMileageNumber((int)mul(updateOrderVo.getMileageNumber() == null ? "0" : updateOrderVo.getMileageNumber(),"1000"));
        orderDetail.getOrderGoods().setGoodsName(updateOrderVo.getGoodsName());
        orderDetail.getOrderGoods().setGoodsType(updateOrderVo.getGoodsType());
        orderDetail.getOrderGoods().setSquare(updateOrderVo.getSquare() == null ? 0f : updateOrderVo.getSquare());
        orderDetail.getOrderGoods().setWeight(updateOrderVo.getWeight() == null ? 0f : updateOrderVo.getWeight());
        orderDetail.getOrderGoods().setVehicleLengh(updateOrderVo.getVehicleLengh());
        orderDetail.getOrderGoods().setVehicleStatus(updateOrderVo.getVehicleStatus());
        orderDetail.getOrderGoods().setCustomNumber(updateOrderVo.getCustomNumber());
        orderDetail.getOrderGoods().setReciveName(updateOrderVo.getReciveName());
        orderDetail.getOrderGoods().setRecivePhone(updateOrderVo.getRecivePhone());
        orderDetail.getOrderGoods().setReciveType(updateOrderVo.getReciveType());
        orderDetail.getOrderGoods().setReciveProvinceId(updateOrderVo.getReciveProvinceId());
        orderDetail.getOrderGoods().setReciveCityId(updateOrderVo.getReciveCityId());
        orderDetail.getOrderGoods().setReciveAddr(updateOrderVo.getReciveAddr());
        orderDetail.getOrderInfo().setRemark(updateOrderVo.getRemark());
    }

    /**
     * 修改订单收入信息
     * @param orderDetail
     * @param updateOrderVo
     * @throws Exception
     */
    private void updateIncomeOrderInfo(OrderDetailsDto orderDetail, UpdateOrderVo updateOrderVo) {
        orderDetail.getOrderScheduler().setDistance(mul(StringUtils.isEmpty(updateOrderVo.getDistance()) ? "0" : updateOrderVo.getDistance(), "1000"));
        orderDetail.getOrderGoods().setSquare(updateOrderVo.getSquare() == null ? 0 : updateOrderVo.getSquare());
        orderDetail.getOrderGoods().setWeight(updateOrderVo.getWeight() == null ? 0 : updateOrderVo.getWeight());
        orderDetail.getOrderScheduler().setIsUrgent(updateOrderVo.getIsUrgent() == null || updateOrderVo.getIsUrgent() == -1 ? 0 : updateOrderVo.getIsUrgent());
        orderDetail.getOrderFee().setPriceUnit(updateOrderVo.getPriceUnit() == null ? 0 : updateOrderVo.getPriceUnit());
        orderDetail.getOrderFee().setPriceEnum(updateOrderVo.getPriceEnum() != null && updateOrderVo.getPriceEnum() != -1 ? updateOrderVo.getPriceEnum() : null);
        orderDetail.getOrderFee().setCostPrice(updateOrderVo.getCostPrice() == null ? 0L :  CommonUtils.objToLongMul100(updateOrderVo.getCostPrice()));
        orderDetail.getOrderFee().setPrePayCash(updateOrderVo.getPrePayCash() == null ? 0L : CommonUtils.objToLongMul100(updateOrderVo.getPrePayCash()));
        orderDetail.getOrderFee().setPrePayEquivalenceCardType(updateOrderVo.getPrePayEquivalenceCardType() != null && updateOrderVo.getPrePayEquivalenceCardType() != -1 ? updateOrderVo.getPrePayEquivalenceCardType() : null);
        orderDetail.getOrderFee().setPrePayEquivalenceCardAmount(updateOrderVo.getPrePayEquivalenceCardAmount() == null ? 0L : CommonUtils.objToLongMul100(updateOrderVo.getPrePayEquivalenceCardAmount()));
        orderDetail.getOrderFee().setPrePayEquivalenceCardNumber(StringUtils.isNotEmpty(updateOrderVo.getPrePayEquivalenceCardNumber()) ? updateOrderVo.getPrePayEquivalenceCardNumber() : null);
        orderDetail.getOrderFee().setAfterPayCash(updateOrderVo.getAfterPayCash() == null ? 0L : CommonUtils.objToLongMul100(updateOrderVo.getAfterPayCash()));
        //去掉后付等值卡额
        orderDetail.getOrderFee().setAfterPayEquivalenceCardAmount(updateOrderVo.getAfterPayEquivalenceCardAmount() == null ? 0L : Long.parseLong(updateOrderVo.getAfterPayEquivalenceCardAmount()) * 100);
        orderDetail.getOrderFee().setAfterPayEquivalenceCardType(updateOrderVo.getAfterPayEquivalenceCardType() != null && updateOrderVo.getAfterPayEquivalenceCardType() != -1 ? updateOrderVo.getAfterPayEquivalenceCardType() : null);
        orderDetail.getOrderFee().setAfterPayEquivalenceCardNumber(StringUtils.isNotEmpty(updateOrderVo.getAfterPayEquivalenceCardNumber()) ? updateOrderVo.getAfterPayEquivalenceCardNumber() : null);
        orderDetail.getOrderFee().setAfterPayAcctType(updateOrderVo.getAfterPayAcctType() != null && updateOrderVo.getAfterPayAcctType() != -1 ? updateOrderVo.getAfterPayAcctType() : null);
        //设置收入的结算信息
        orderDetail.setIncomePaymentDaysInfo(updateOrderVo.getIncomePaymentDaysInfo());
        this.setOrderPaymentDaysInfo(orderDetail.getIncomePaymentDaysInfo());
    }

    private void setOrderPaymentDaysInfo(OrderPaymentDaysInfo info) {
        info.setBalanceType(info.getBalanceType() != null && info.getBalanceType() < 0 ? null : info.getBalanceType());
        info.setCollectionDay(info.getCollectionDay() != null && info.getCollectionDay() < 0 ? null : info.getCollectionDay());
        info.setCollectionMonth(info.getCollectionMonth() != null && info.getCollectionMonth() < 0 ? null : info.getCollectionMonth());
        info.setCollectionTime(info.getCollectionTime() != null && info.getCollectionTime() < 0 ? null : info.getCollectionTime());
        info.setInvoiceDay(info.getInvoiceDay() != null && info.getInvoiceDay() < 0 ? null : info.getInvoiceDay());
        info.setInvoiceMonth(info.getInvoiceMonth() != null && info.getInvoiceMonth() < 0 ? null : info.getInvoiceMonth());
        info.setInvoiceTime(info.getInvoiceTime() != null && info.getInvoiceTime() < 0 ? null : info.getInvoiceTime());
        info.setReciveDay(info.getReciveDay() != null && info.getReciveDay() < 0 ? null : info.getReciveDay());
        info.setReciveMonth(info.getReciveMonth() != null && info.getReciveMonth() < 0 ? null : info.getReciveMonth());
        info.setReciveTime(info.getReciveTime() != null && info.getReciveTime() < 0 ? null : info.getReciveTime());
        info.setReconciliationDay(info.getReconciliationDay() != null && info.getReconciliationDay() < 0 ? null : info.getReconciliationDay());
        info.setReconciliationMonth(info.getReconciliationMonth() != null && info.getReconciliationMonth() < 0 ? null : info.getReconciliationMonth());
        info.setReconciliationTime(info.getReconciliationTime() != null && info.getReconciliationTime() < 0 ? null : info.getReconciliationTime());
    }

    /**
     * 修改订单调度信息
     * @param orderDetail
     * @param updateOrderVo
     * @throws Exception
     */
    private void updateDispatchOrderInfo(OrderDetailsDto orderDetail, UpdateOrderVo updateOrderVo) {
        if(updateOrderVo.getAppointWay() == null) {
            throw new BusinessException("请选择指派方式！");
        }
        //清空修改前的值
        this.clearnDispatchOrderInfo(orderDetail);

        orderDetail.getOrderScheduler().setAppointWay(updateOrderVo.getAppointWay());
        orderDetail.getOrderInfo().setIsNeedBill(updateOrderVo.getIsNeedBill());
        orderDetail.getOrderFeeExt().setOilAccountType(updateOrderVo.getOilAccountType());
        orderDetail.getOrderFeeExt().setOilConsumer(updateOrderVo.getOilConsumer());
        //指派员工
        if(OrderConsts.AppointWay.APPOINT_LOCAL==orderDetail.getOrderScheduler().getAppointWay()) {
            orderDetail.getOrderScheduler().setDispatcherId(updateOrderVo.getDispatcherId());
            orderDetail.getOrderScheduler().setDispatcherName(updateOrderVo.getDispatcherName());
            orderDetail.getOrderScheduler().setDispatcherBill(updateOrderVo.getDispatcherBill());
        }else if(OrderConsts.AppointWay.APPOINT_TENANT==orderDetail.getOrderScheduler().getAppointWay()) {//指派车队
            orderDetail.getOrderInfo().setToTenantId(updateOrderVo.getToTenantId());
            orderDetail.getOrderInfo().setToTenantName(updateOrderVo.getToTenantName());
            orderDetail.getOrderFee().setAcctName(updateOrderVo.getAcctName());
            orderDetail.getOrderFee().setAcctNo(updateOrderVo.getAcctNo());
        }else if(OrderConsts.AppointWay.APPOINT_CAR==orderDetail.getOrderScheduler().getAppointWay()) {//指派车辆
            orderDetail.getOrderScheduler().setPlateNumber(updateOrderVo.getPlateNumber());
            orderDetail.getOrderScheduler().setVehicleCode(updateOrderVo.getVehicleCode());
            orderDetail.getOrderScheduler().setCarLengh(updateOrderVo.getCarLengh());
            orderDetail.getOrderScheduler().setCarStatus(updateOrderVo.getCarStatus());
            orderDetail.getOrderScheduler().setCarDriverMan(updateOrderVo.getCarDriverMan());
            orderDetail.getOrderScheduler().setCarDriverId(updateOrderVo.getCarDriverId());
            orderDetail.getOrderScheduler().setCarDriverPhone(updateOrderVo.getCarDriverPhone());
            orderDetail.getOrderScheduler().setVehicleClass(updateOrderVo.getVehicleClass());
            orderDetail.getOrderScheduler().setLicenceType(updateOrderVo.getLicenceType());
        }else {
            throw new BusinessException("指派方式不正确！");
        }

        //指派自有车
        if(orderDetail.getOrderScheduler().getVehicleClass() != null
                && orderDetail.getOrderScheduler().getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            if (orderDetail.getOrderScheduler().getCarDriverId() == null || orderDetail.getOrderScheduler().getCarDriverId() <= 0) {
                throw new BusinessException("请选择主驾驶！");
            }
            if(updateOrderVo.getTrailerId() != null && updateOrderVo.getTrailerId() == -1L) {
                orderDetail.getOrderScheduler().setTrailerId(updateOrderVo.getTrailerId());
                orderDetail.getOrderScheduler().setTrailerPlate(updateOrderVo.getTrailerPlate());
            }else {
                orderDetail.getOrderScheduler().setTrailerId(null);
                orderDetail.getOrderScheduler().setTrailerPlate(null);
            }
            if(updateOrderVo.getCopilotUserId() != null && updateOrderVo.getCopilotUserId() == -1L) {
                orderDetail.getOrderScheduler().setCopilotUserId(updateOrderVo.getCopilotUserId());
                orderDetail.getOrderScheduler().setCopilotMan(updateOrderVo.getCopilotMan());
                orderDetail.getOrderScheduler().setCopilotPhone(updateOrderVo.getCopilotPhone());
            }else {
                orderDetail.getOrderScheduler().setCopilotUserId(null);
                orderDetail.getOrderScheduler().setCopilotMan(null);
                orderDetail.getOrderScheduler().setCopilotPhone(null);
            }
            if(updateOrderVo.getPaymentWay() != null) {
                throw new BusinessException("请选择成本方式！");
            }
            orderDetail.getOrderInfoExt().setPaymentWay(updateOrderVo.getPaymentWay());
            if(orderDetail.getOrderInfoExt().getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {//智能模式
                orderDetail.getOrderInfoExt().setCapacityOil(updateOrderVo.getCapacityOil() != null ? updateOrderVo.getCapacityOil() : 0);
                orderDetail.getOrderInfoExt().setRunOil(updateOrderVo.getRunOil() != null ? updateOrderVo.getRunOil() : 0);
                orderDetail.getOrderFeeExt().setPontagePer(updateOrderVo.getPontagePer());
                orderDetail.getOrderInfoExt().setRunWay(updateOrderVo.getRunWay());
                orderDetail.getOrderFeeExt().setOilLitreTotal(updateOrderVo.getOilLitreTotal());
                orderDetail.getOrderFeeExt().setOilLitreVirtual(updateOrderVo.getOilLitreVirtual());
                orderDetail.getOrderFeeExt().setOilLitreEntity(updateOrderVo.getOilLitreEntity());
                orderDetail.getOrderFeeExt().setOilPrice(updateOrderVo.getOilPrice());
                orderDetail.getOrderFeeExt().setPontage(updateOrderVo.getPontage());
                orderDetail.getOrderFeeExt().setSalary(updateOrderVo.getSalary());
                orderDetail.getOrderFeeExt().setCopilotSalary(updateOrderVo.getCopilotSalary());
                orderDetail.getOrderFeeExt().setDriverSwitchSubsidy(updateOrderVo.getDriverSwitchSubsidy());
                orderDetail.getOrderFeeExt().setSubsidyTime(updateOrderVo.getCarDriverSubsidyDate());
                orderDetail.getOrderFeeExt().setCopilotSubsidyTime(updateOrderVo.getCopilotSubsidyDate());
                orderDetail.getOrderFee().setPreOilVirtualFee(updateOrderVo.getPreOilVirtualFee() != null ? updateOrderVo.getPreOilVirtualFee() : 0L);
                orderDetail.getOrderFee().setPreOilFee(updateOrderVo.getPreOilFee() != null ? updateOrderVo.getPreOilFee() : 0L);
                orderDetail.getOrderFee().setPreTotalFee(orderDetail.getOrderFee().getPreOilFee()+orderDetail.getOrderFee().getPreOilVirtualFee());
                orderDetail.getOrderFeeExt().setEstFee(orderDetail.getOrderFeeExt().getPontage()+orderDetail.getOrderFeeExt().getSalary()
                        +orderDetail.getOrderFeeExt().getCopilotSalary()+orderDetail.getOrderFee().getPreTotalFee()+orderDetail.getOrderFeeExt().getDriverSwitchSubsidy());

                orderDetail.setSchemes(new ArrayList<OrderOilDepotScheme>());
                List<OrderOilDepotScheme> schemes = updateOrderVo.getSchemes();
                if(schemes!=null&&schemes.size()>0) {
                    for (OrderOilDepotScheme scheme : schemes) {
                        OrderOilDepotScheme orderOilDepotScheme=new OrderOilDepotScheme();
                        BeanUtil.copyProperties(scheme, orderOilDepotScheme);
                        orderOilDepotScheme.setDependDistance(mul(scheme.getDependDistance(), 1000L));
                        orderOilDepotScheme.setOilDepotFee(objToLongMul100(scheme.getOilDepotFee()));
                        orderOilDepotScheme.setOilDepotLitre(objToLongMul100(scheme.getOilDepotLitre()));
                        orderOilDepotScheme.setOilDepotPrice(objToLongMul100(scheme.getOilDepotPrice()));
                        orderOilDepotScheme.setOrderId(orderDetail.getOrderInfo().getOrderId());
                        orderDetail.getSchemes().add(orderOilDepotScheme);
                    }
                }
            }else if(orderDetail.getOrderInfoExt().getPaymentWay()== OrderConsts.PAYMENT_WAY.CONTRACT) {//承包模式

            }else if(orderDetail.getOrderInfoExt().getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {//报销模式
                orderDetail.getOrderInfoExt().setCapacityOil(updateOrderVo.getCapacityOil() != null ? objToFloatMul100(updateOrderVo.getCapacityOil()) : 0f);
                orderDetail.getOrderInfoExt().setRunOil(updateOrderVo.getRunOil() != null ? objToFloatMul100(updateOrderVo.getRunOil()) : 0);
                orderDetail.getOrderFee().setPreOilVirtualFee(updateOrderVo.getPreOilVirtualFee() != null ?objToLongMul100(updateOrderVo.getPreOilVirtualFee()) : 0L);
                orderDetail.getOrderFee().setPreOilFee(updateOrderVo.getPreOilFee() != null ?objToLongMul100(updateOrderVo.getPreOilFee()) : 0L);
                orderDetail.getOrderFee().setPreTotalFee(orderDetail.getOrderFee().getPreOilFee()+orderDetail.getOrderFee().getPreOilVirtualFee());

                List<OrderOilCardInfo> oilCardInfos = updateOrderVo.getOilCardInfos();
                if(oilCardInfos!=null&&oilCardInfos.size()>0) {
                    if(orderDetail.getOilCardInfos()==null) {
                        orderDetail.setOilCardInfos(new ArrayList<OrderOilCardInfo>());
                    }
                    for (OrderOilCardInfo oilCardInfo : oilCardInfos) {
                        OrderOilCardInfo orderOilCardInfo=new OrderOilCardInfo();
                        BeanUtils.copyProperties(oilCardInfo, orderOilCardInfo);
                        orderDetail.getOilCardInfos().add(orderOilCardInfo);
                    }
                }
            }else {
                throw new BusinessException("成本方式不正确！");
            }

        }

        if(orderDetail.getOrderScheduler().getVehicleClass()!=null&&orderDetail.getOrderScheduler().getVehicleClass()!=1
                || OrderConsts.AppointWay.APPOINT_TENANT == orderDetail.getOrderScheduler().getAppointWay()
                ||(orderDetail.getOrderScheduler().getVehicleClass() != null && orderDetail.getOrderScheduler().getVehicleClass() == 1
                && orderDetail.getOrderInfoExt().getPaymentWay() != null
                && orderDetail.getOrderInfoExt().getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT)){
            orderDetail.getOrderFee().setTotalFee(updateOrderVo.getTotalFee() != null ?objToLongMul100(updateOrderVo.getTotalFee()) : 0L);
            orderDetail.getOrderFee().setInsuranceFee(updateOrderVo.getInsuranceFee() != null ?objToLongMul100(updateOrderVo.getInsuranceFee()) : 0L);

            // 自有车 承包模式
            if(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderDetail.getOrderScheduler().getVehicleClass() && OrderConsts.PAYMENT_WAY.CONTRACT == orderDetail.getOrderInfoExt().getPaymentWay()) {
                orderDetail.getOrderFee().setGuidePrice(orderDetail.getOrderFee().getTotalFee());
            }else {
                orderDetail.getOrderFee().setGuidePrice(updateOrderVo.getGuidePrice() != null ?objToLongMul100(updateOrderVo.getGuidePrice()) : 0L);
            }
            orderDetail.getOrderFee().setPreTotalFee(updateOrderVo.getPreTotalFee() != null ? objToLongMul100(updateOrderVo.getPreTotalFee()) : 0L);
            orderDetail.getOrderFee().setPreCashFee(updateOrderVo.getPreCashFee() != null ? objToLongMul100(updateOrderVo.getPreCashFee()) : 0L);
            orderDetail.getOrderFee().setPreOilVirtualFee(updateOrderVo.getPreOilVirtualFee() != null ? objToLongMul100(updateOrderVo.getPreOilVirtualFee()) : 0L);
            orderDetail.getOrderFee().setPreOilFee(updateOrderVo.getPreOilFee() != null ? objToLongMul100(updateOrderVo.getPreOilFee()) : 0L);
            orderDetail.getOrderFee().setPreEtcFee(updateOrderVo.getPreEtcFee() != null ? objToLongMul100(updateOrderVo.getPreEtcFee()) : 0L);
            orderDetail.getOrderFee().setFinalFee(updateOrderVo.getFinalFee() != null ? objToLongMul100(updateOrderVo.getFinalFee()) : 0L);
            orderDetail.getOrderFee().setFinalScale(updateOrderVo.getFinalScale() != null ? objToLongMul100(updateOrderVo.getFinalScale()) : 0L);
            orderDetail.getOrderFee().setArrivePaymentFee(updateOrderVo.getArrivePaymentFee() != null ? objToLongMul100(updateOrderVo.getArrivePaymentFee()) : 0L);
            if (orderDetail.getOrderFee().getInsuranceFee() != null && orderDetail.getOrderFee().getInsuranceFee() > 0 && orderDetail.getOrderFee().getInsuranceFee() > orderDetail.getOrderFee().getFinalFee()) {
                throw new BusinessException("保费不能大于尾款金额");
            }

            //存4位小数 eg:32% =  3200
            orderDetail.getOrderFee().setPreTotalScale(updateOrderVo.getPreTotalScale() != null ? objToLongMul100(updateOrderVo.getPreTotalScale()) : 0L);
            orderDetail.getOrderFee().setPreCashScale(updateOrderVo.getPreCashScale() != null ? objToLongMul100(updateOrderVo.getPreCashScale()) : 0L);
            orderDetail.getOrderFee().setPreOilVirtualScale(updateOrderVo.getPreOilVirtualScale() != null ? objToLongMul100(updateOrderVo.getPreOilVirtualScale()) : 0L);
            orderDetail.getOrderFee().setPreOilScale(updateOrderVo.getPreOilScale() != null ? objToLongMul100(updateOrderVo.getPreOilScale()) : 0L);
            orderDetail.getOrderFee().setPreEtcScale(updateOrderVo.getPreEtcScale() != null ? objToLongMul100(updateOrderVo.getPreEtcScale()) : 0L);
            orderDetail.getOrderFee().setArrivePaymentFeeScale(updateOrderVo.getArrivePaymentFeeScale() != null ? objToLongMul100(updateOrderVo.getArrivePaymentFeeScale()) : 0L);
            orderDetail.getOrderFee().setPreTotalScaleStandard(updateOrderVo.getPreTotalScaleStandard() != null ? objToLongMul100(updateOrderVo.getPreTotalScaleStandard()) * 100 : 0L);
            orderDetail.getOrderFee().setPreCashScaleStandard(updateOrderVo.getPreCashScaleStandard() != null ? objToLongMul100(updateOrderVo.getPreCashScaleStandard()) * 100 : 0L);
            orderDetail.getOrderFee().setPreOilVirtualScaleStandard(updateOrderVo.getPreOilVirtualScaleStandard() != null ? objToLongMul100(updateOrderVo.getPreOilVirtualScaleStandard()) * 100 : 0L);
            orderDetail.getOrderFee().setPreOilScaleStandard(updateOrderVo.getPreOilScaleStandard() != null ? objToLongMul100(updateOrderVo.getPreOilScaleStandard()) * 100 : 0L);
            orderDetail.getOrderFee().setPreEtcScaleStandard(updateOrderVo.getPreEtcScaleStandard() != null ? objToLongMul100(updateOrderVo.getPreEtcScaleStandard()) * 100 : 0L);

            if((orderDetail.getOrderScheduler().getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    ||orderDetail.getOrderScheduler().getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    ||orderDetail.getOrderScheduler().getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4)
                    && updateOrderVo.getIsCollection() != null && updateOrderVo.getIsCollection() == 1) {
                orderDetail.getOrderScheduler().setCollectionUserPhone(updateOrderVo.getCollectionUserPhone());
                if(StringUtils.isBlank(orderDetail.getOrderScheduler().getCollectionUserPhone())) {
                    throw new BusinessException("代收人手机号为空或格式不正确！");
                }
                orderDetail.getOrderScheduler().setCollectionUserName(updateOrderVo.getCollectionName());
                orderDetail.getOrderScheduler().setCollectionUserId(updateOrderVo.getCollectionUserId());
                orderDetail.getOrderScheduler().setCollectionName(updateOrderVo.getCollectionName());
                if(StringUtils.isBlank(orderDetail.getOrderScheduler().getCollectionName())) {
                    orderDetail.getOrderScheduler().setCollectionName(orderDetail.getOrderScheduler().getCollectionUserName());
                }
                orderDetail.getOrderScheduler().setIsCollection(1);
            }else {
                orderDetail.getOrderScheduler().setIsCollection(0);
            }

            // 招商车  外来挂靠车
            if(orderDetail.getOrderScheduler().getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderDetail.getOrderScheduler().getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                orderDetail.getOrderScheduler().setBillReceiverMobile(updateOrderVo.getBillReceiverMobile());
                orderDetail.getOrderScheduler().setBillReceiverName(updateOrderVo.getBillReceiverName());
                orderDetail.getOrderScheduler().setBillReceiverUserId(updateOrderVo.getBillReceiverUserId());
            }
        }
        if(OrderConsts.AppointWay.APPOINT_LOCAL != orderDetail.getOrderScheduler().getAppointWay()) {
            if(orderDetail.getOrderInfo().getIsNeedBill()==OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                if(orderDetail.getOrderFee().getPreOilVirtualFee()>0) {

                    orderDetail.getOrderFeeExt().setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
                }
            }else {
                orderDetail.getOrderFeeExt().setOilConsumer(updateOrderVo.getOilConsumer() == null ? OrderConsts.OIL_CONSUMER.SELF : updateOrderVo.getOilConsumer());
            }
        }
        orderDetail.getOrderScheduler().setReciveAddr(updateOrderVo.getReciveAddr());
        //设置成本的结算信息
        OrderPaymentDaysInfo costPaymentDaysInfo = updateOrderVo.getCostPaymentDaysInfo();
        if(costPaymentDaysInfo!=null) {
            if(orderDetail.getCostPaymentDaysInfo()==null) {
                orderDetail.setCostPaymentDaysInfo(new OrderPaymentDaysInfo());
            }
            BeanUtil.copyProperties(costPaymentDaysInfo, orderDetail.getCostPaymentDaysInfo());
            this.setOrderPaymentDaysInfo(orderDetail.getCostPaymentDaysInfo());
        }
    }

    /**
     * 清空修改的调度信息
     * @param orderDetail
     */
    private void clearnDispatchOrderInfo(OrderDetailsDto orderDetail) {
        orderDetail.getOrderInfo().setToTenantId(null);
        orderDetail.getOrderInfo().setToTenantName(null);
        orderDetail.getOrderInfo().setIsNeedBill(null);
        orderDetail.getOrderInfoExt().setRunWay(0L);
        orderDetail.getOrderInfoExt().setOilUseType(null);
        orderDetail.getOrderInfoExt().setPaymentWay(null);
        orderDetail.getOrderInfoExt().setCapacityOil(0f);
        orderDetail.getOrderInfoExt().setRunOil(0f);
        orderDetail.getOrderInfoExt().setOilIsNeedBill(null);
        orderDetail.getOrderFee().setAcctName(null);
        orderDetail.getOrderFee().setAcctNo(null);
        orderDetail.getOrderFee().setGuidePrice(0L);
        orderDetail.getOrderFee().setInsuranceFee(0L);
        orderDetail.getOrderFee().setTotalFee(0L);
        orderDetail.getOrderFee().setPreTotalFee(0L);
        orderDetail.getOrderFee().setPreCashFee(0L);
        orderDetail.getOrderFee().setPreOilVirtualFee(0L);
        orderDetail.getOrderFee().setPreOilFee(0L);
        orderDetail.getOrderFee().setPreEtcFee(0L);
        orderDetail.getOrderFee().setFinalFee(0L);
        orderDetail.getOrderFee().setFinalScale(0L);
        orderDetail.getOrderFee().setPreTotalScale(0L);
        orderDetail.getOrderFee().setArrivePaymentFee(0L);
        orderDetail.getOrderFee().setArrivePaymentFeeScale(0L);
        orderDetail.getOrderFee().setPreCashScale(0L);
        orderDetail.getOrderFee().setPreOilVirtualScale(0L);
        orderDetail.getOrderFee().setPreOilScale(0L);
        orderDetail.getOrderFee().setPreEtcScale(0L);
        orderDetail.getOrderFee().setPreOilScale(0L);
        orderDetail.getOrderFee().setPreEtcScale(0L);
        orderDetail.getOrderFee().setPreTotalScaleStandard(0L);
        orderDetail.getOrderFee().setPreCashScaleStandard(0L);
        orderDetail.getOrderFee().setPreOilVirtualScaleStandard(0L);
        orderDetail.getOrderFee().setPreOilScaleStandard(0L);
        orderDetail.getOrderFee().setPreEtcScaleStandard(0L);
        orderDetail.getOrderFeeExt().setEstFee(0L);
        orderDetail.getOrderFeeExt().setOilLitreTotal(0L);
        orderDetail.getOrderFeeExt().setOilLitreVirtual(0L);
        orderDetail.getOrderFeeExt().setOilLitreEntity(0L);
        orderDetail.getOrderFeeExt().setOilPrice(0L);
        orderDetail.getOrderFeeExt().setSalary(0L);
        orderDetail.getOrderFeeExt().setCopilotSalary(0L);
        //orderDetail.getOrderFeeExt().setDriverSwitchSubsidy(0L);
        orderDetail.getOrderFeeExt().setPontage(0L);
        orderDetail.getOrderFeeExt().setSubsidyTime(null);
        orderDetail.getOrderFeeExt().setCopilotSubsidyTime(null);
        orderDetail.getOrderFeeExt().setPontagePer(0L);
        orderDetail.getOrderScheduler().setAppointWay(null);
        orderDetail.getOrderScheduler().setIsCollection(null);
        orderDetail.getOrderScheduler().setPlateNumber(null);
        orderDetail.getOrderScheduler().setVehicleCode(null);
        orderDetail.getOrderScheduler().setCarLengh(null);
        orderDetail.getOrderScheduler().setCollectionUserPhone(null);
        orderDetail.getOrderScheduler().setCollectionUserName(null);
        orderDetail.getOrderScheduler().setCollectionUserId(null);
        orderDetail.getOrderScheduler().setCarStatus(null);
        orderDetail.getOrderScheduler().setCarDriverMan(null);
        orderDetail.getOrderScheduler().setCarDriverId(null);
        orderDetail.getOrderScheduler().setCarDriverPhone(null);
        orderDetail.getOrderScheduler().setVehicleClass(null);
        orderDetail.getOrderScheduler().setLicenceType(null);
        orderDetail.getOrderScheduler().setTrailerId(null);
        orderDetail.getOrderScheduler().setTrailerPlate(null);
        orderDetail.getOrderScheduler().setCopilotMan(null);
        orderDetail.getOrderScheduler().setCopilotPhone(null);
        orderDetail.getOrderScheduler().setCopilotUserId(null);
        orderDetail.getOrderScheduler().setBillReceiverMobile(null);
        orderDetail.getOrderScheduler().setBillReceiverName(null);
        orderDetail.getOrderScheduler().setBillReceiverUserId(null);
        orderDetail.getOrderScheduler().setDispatcherId(null);
        orderDetail.getOrderScheduler().setBillReceiverName(null);
        orderDetail.getOrderScheduler().setBillReceiverUserId(null);
        orderDetail.getOrderScheduler().setDispatcherId(null);
        orderDetail.getOrderScheduler().setDispatcherName(null);
        orderDetail.getOrderScheduler().setDispatcherBill(null);
        orderDetail.setSchemes(null);
        orderDetail.setOilCardInfos(null);
        orderDetail.getOrderFeeExt().setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
        orderDetail.getOrderFeeExt().setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
        orderDetail.getOrderFeeExt().setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
    }

    /**
     * 修改订单归属信息
     * @param orderDetail
     * @param updateOrderVo
     * @throws Exception
     */
    private void updateOtherOrderInfo(OrderDetailsDto orderDetail, UpdateOrderVo updateOrderVo) {
        orderDetail.getOrderInfo().setOrgId(updateOrderVo.getOrgId().intValue());
        orderDetail.getOrderGoods().setLocalUser(updateOrderVo.getLocalUser());
        orderDetail.getOrderGoods().setLocalUserName(updateOrderVo.getLocalUserName());
        orderDetail.getOrderGoods().setLocalPhone(updateOrderVo.getLocalPhone());
        orderDetail.getOrderInfo().setRemark(updateOrderVo.getRemark());
    }

    /**
     * 调用TF前 数据校验
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @throws Exception
     */
    private void billingSaveCheck(OrderInfo orderInfo,OrderGoods orderGoods,OrderScheduler orderScheduler,OrderFee orderFee,List<OrderOilDepotScheme> orderOilDepotSchemeList, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        //数据校验
        try {
            ObjectCompareUtils.isNotBlankNamesMap(orderInfo, getAddOrderInfoCheckNoBlank());
            ObjectCompareUtils.isNotBlankNamesMap(orderGoods, getAddOrderGoodsCheckNoBlank(orderInfo.getOrderType()));
            ObjectCompareUtils.isNotBlankNamesMap(orderScheduler, getAddOrderSchedulerCheckNoBlank(orderScheduler.getVehicleClass(), orderScheduler.getAppointWay()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("数据校验错误");
        }

        if (null != orderFee.getPrePayCash() && orderFee.getPrePayCash() < 0) {
            throw new BusinessException("预付现金不能小于0");
        }
        if (null != orderFee.getPrePayEquivalenceCardAmount() && orderFee.getPrePayEquivalenceCardAmount() < 0) {
            throw new BusinessException("预付等值卡金额不能小于0");
        }
        if (null != orderFee.getAfterPayCash() && orderFee.getAfterPayCash() < 0) {
            throw new BusinessException("尾款现金不能小于0");
        }
        if (null != orderFee.getAfterPayEquivalenceCardAmount() && orderFee.getAfterPayEquivalenceCardAmount() < 0) {
            throw new BusinessException("尾款等值卡不能小于0");
        }

        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            if (orderInfo.getToTenantId().equals(tenantId)) {
                throw new BusinessException("不能指派给本车队!");
            }
        }
        //车辆校验-指派车辆
		/*if(orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR){
			//整车才需要校验
			if(orderScheduler.getLicenceType()!=null && orderScheduler.getLicenceType().intValue()==SysStaticDataEnum.LICENCE_TYPE.ZC) {
				if(StringUtils.isNotBlank(orderGoods.getVehicleLengh())&&!OrderUtil.checkVehicleLength(orderScheduler.getCarLengh(), orderGoods.getVehicleLengh())){
					throw new BusinessException("车辆长度不匹配!");
				}
				if (orderGoods.getVehicleStatus()!=null&&orderGoods.getVehicleStatus()>=0&&!OrderUtil.checkVehicleStatus(orderScheduler.getCarStatus(), orderGoods.getVehicleStatus())) {
					throw new BusinessException("车型不匹配!");
				}
			}
		}*/
        if(orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR || orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT){
            if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if(orderScheduler.getCopilotUserId() != null && orderScheduler.getCarDriverId() != null && orderScheduler.getCopilotUserId().longValue() == orderScheduler.getCarDriverId().longValue()){
                    throw new BusinessException("主驾驶和副驾驶不能是同一个人");
                }
                //自有车
                return;
            }
            if(orderFee.getTotalFee() != null){
                Long maxAmount = payFeeLimitService.getAmountLimitCfgVal(tenantId, SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_BID_AMOUNT_101);
                if(maxAmount != null && ! (maxAmount == -1L)){
                    if(orderFee.getTotalFee().longValue() > maxAmount){
                        throw new BusinessException("中标价不能大于" + objToFloatDiv100(maxAmount) + "元");
                    }
                }
            }
        }
        List list = new ArrayList();
        if(orderOilDepotSchemeList!=null&&orderOilDepotSchemeList.size()>0) {
            for(OrderOilDepotScheme orderOilDepotScheme : orderOilDepotSchemeList){
                if(orderOilDepotScheme!=null&&orderOilDepotScheme.getOilDepotId() != null){
                    if(list.contains(orderOilDepotScheme.getOilDepotId())){
                        throw new BusinessException("油站重复，同一订单不能选择相同油站");
                    }else{
                        list.add(orderOilDepotScheme.getOilDepotId());
                    }
                }
            }
        }
    }
}
