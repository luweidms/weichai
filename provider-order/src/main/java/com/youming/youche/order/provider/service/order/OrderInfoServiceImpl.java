package com.youming.youche.order.provider.service.order;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.ILineOilDepotSchemeService;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDto;
import com.youming.youche.market.dto.facilitator.LineOilQueryInDto;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.ITransferInfoService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IBillAccountTenantRelService;
import com.youming.youche.order.api.order.IBillInfoReceiveRelService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.api.order.IClaimExpenseInfoService;
import com.youming.youche.order.api.order.ICmCustomerInfoService;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.api.order.IOilCardLogService;
import com.youming.youche.order.api.order.IOilEntityService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOrderAccountOilSourceService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderDriverSubsidyVerService;
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
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoHService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoVerService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderSchedulerVerService;
import com.youming.youche.order.api.order.IOrderStateTrackOperService;
import com.youming.youche.order.api.order.IOrderStatementService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoVerService;
import com.youming.youche.order.api.order.IOrderVehicleTimeNodeService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IRateService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.api.order.ITenantReceiverRelService;
import com.youming.youche.order.api.order.IVehicleReturnInfoService;
import com.youming.youche.order.api.order.IWorkOrderInfoService;
import com.youming.youche.order.api.order.IWorkOrderRelService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.api.order.other.IOrderPayMethodService;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.CreditRatingRuleFee;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.TransferInfo;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BillAccountTenantRel;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.domain.order.CmCustomerInfo;
import com.youming.youche.order.domain.order.OilCardLog;
import com.youming.youche.order.domain.order.OrderAccountOilSource;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderFeeStatement;
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
import com.youming.youche.order.domain.order.OrderOilSource;
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
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.domain.order.TenantReceiverRel;
import com.youming.youche.order.domain.order.VehicleReturnInfo;
import com.youming.youche.order.domain.order.WorkOrderInfo;
import com.youming.youche.order.domain.order.WorkOrderRel;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.ClaimExpenseCountDto;
import com.youming.youche.order.dto.CustomerDto;
import com.youming.youche.order.dto.EstimatedCostsDto;
import com.youming.youche.order.dto.OaloanCountDto;
import com.youming.youche.order.dto.OilRechargeAccountDto;
import com.youming.youche.order.dto.OrderDto;
import com.youming.youche.order.dto.OrderFeeDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.OrderInfoExportDto;
import com.youming.youche.order.dto.OrderLimitDto;
import com.youming.youche.order.dto.OrderListAppOutDto;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.OrderReciveEchoDto;
import com.youming.youche.order.dto.UpdateOrderFeeExtInDto;
import com.youming.youche.order.dto.UpdateOrderFeeInDto;
import com.youming.youche.order.dto.UpdateOrderGoodsInDto;
import com.youming.youche.order.dto.UpdateOrderInfoExtInDto;
import com.youming.youche.order.dto.UpdateOrderInfoInDto;
import com.youming.youche.order.dto.UpdateOrderSchedulerInDto;
import com.youming.youche.order.dto.UpdateTheOrderInDto;
import com.youming.youche.order.dto.UpdateTheOwnCarOrderInDto;
import com.youming.youche.order.dto.VehicleFeeFromOrderDataDto;
import com.youming.youche.order.dto.order.OrderListAppDto;
import com.youming.youche.order.dto.order.OrderListWxDto;
import com.youming.youche.order.dto.order.QueryOrderOilCardInfoDto;
import com.youming.youche.order.dto.order.QueryOrderResponsiblePartyDto;
import com.youming.youche.order.dto.order.QueryUserOrderJurisdictionDto;
import com.youming.youche.order.dto.order.UserInfoDto;
import com.youming.youche.order.dto.subWayDto;
import com.youming.youche.order.provider.mapper.order.OrderInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ExcelUtils;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import com.youming.youche.order.provider.utils.ObjectCompareUtils;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.PermissionCacheUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.provider.utils.VehicleStaticDataUtil;
import com.youming.youche.order.util.OrderUtil;
import com.youming.youche.order.vo.CopilotMapVo;
import com.youming.youche.order.vo.CustomerInfoVo;
import com.youming.youche.order.vo.DispatchBalanceDataVo;
import com.youming.youche.order.vo.DispatchInfoVo;
import com.youming.youche.order.vo.DispatchOrderVo;
import com.youming.youche.order.vo.EstimatedCostsVo;
import com.youming.youche.order.vo.IncomeBalanceDataVo;
import com.youming.youche.order.vo.OrderFeeVo;
import com.youming.youche.order.vo.OrderListAppVo;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.OrderListWxVo;
import com.youming.youche.order.vo.OrderOilCardInfoVo;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;
import com.youming.youche.order.vo.OrderReceiptVo;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.cm.ICmCustomerLineSubwayService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.DirectionDto;
import com.youming.youche.record.common.GpsUtil;
import com.youming.youche.record.common.OrderConsts;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.dto.DriverDataInfoDto;
import com.youming.youche.record.dto.VehicleCountDto;
import com.youming.youche.record.dto.VehicleDataInfoVxVo;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditConfigService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysMenuBtn;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.ExcelFilesVaildate;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.youming.youche.conts.SysStaticDataEnum.AUTH_STATE.AUTH_STATE2;
import static com.youming.youche.order.constant.BaseConstant.CUSTOMER_SERVICE_PHONE;
import static com.youming.youche.order.constant.BaseConstant.PLEDGE_OILCARD_FEE;
import static com.youming.youche.order.constant.BaseConstant.TENANT_TERRACE_BILL_LIMIT;
import static com.youming.youche.order.constant.BaseConstant.TERRACE_BILL_OIL_FEE_PROPORTION;
import static com.youming.youche.order.util.OrderUtil.getAddOrderGoodsCheckNoBlank;
import static com.youming.youche.order.util.OrderUtil.getAddOrderInfoCheckNoBlank;
import static com.youming.youche.order.util.OrderUtil.getAddOrderSchedulerCheckNoBlank;
import static com.youming.youche.order.util.OrderUtil.mul;
import static com.youming.youche.order.util.OrderUtil.objToFloatDiv100;
import static com.youming.youche.order.util.OrderUtil.objToLong0;
import static com.youming.youche.order.util.OrderUtil.objToLongNull;


/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
@Service
public class OrderInfoServiceImpl extends BaseServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {
    private static final Logger log = LoggerFactory.getLogger(OrderInfoServiceImpl.class);
    @Autowired
    private ICreditRatingRuleService iCreditRatingRuleService;
    @Autowired
    private IPayFeeLimitService payFeeLimitService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private IOrderSchedulerService orderSchedulerService;
    @Autowired
    private IOrderInfoExtService orderInfoExtService;
    @Resource
    private IOrderFeeService orderFeeService;
    @Autowired
    private IOrderFeeExtService orderFeeExtService;
    @Autowired
    private IOrderGoodsService orderGoodsService;
    @Autowired
    private IOrderPaymentDaysInfoService orderPaymentDaysInfoService;
    @Autowired
    private IOrderOilDepotSchemeService orderOilDepotSchemeService;
    @Autowired
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Autowired
    private IOrderOilCardInfoService orderOilCardInfoService;
    @Resource
    private ObjectCompareUtils objectCompareUtils;
    @Autowired
    private IOrderPaymentDaysInfoHService orderPaymentDaysInfoHService;
    @Resource
    OrderInfoHMapper orderInfoHMapper;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    IAuditNodeInstService auditNodeInstService;
    @DubboReference(version = "1.0.0")
    IAuditConfigService auditConfigService;
    @DubboReference(version = "1.0.0")
    ITrailerManagementService trailerManagementService;
    //    @Autowired
//    private IVehicleDataInfoService vehicleDataInfoService;
    @Autowired
    private IBillSettingService billSettingService;
    @Autowired
    private IBillPlatformService billPlatformService;
    @Resource
    ReadisUtil readisUtil;
    @Autowired
    IAccountBankRelService accountBankRelService;
    @Autowired
    IBillAccountTenantRelService billAccountTenantRelService;
    @Autowired
    IBillInfoReceiveRelService billInfoReceiveRelService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Autowired
    private IOilCardManagementService oilCardManagementService;
    @Resource
    private IVehicleReturnInfoService vehicleReturnInfoService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Autowired
    IOrderTransferInfoService orderTransferInfoService;
    @Autowired
    private IRateService rateService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ICmCustomerLineService cmCustomerLineService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;

    @Autowired
    private IOrderFeeStatementService orderFeeStatementService;

    @Autowired
    private IWorkOrderInfoService workOrderInfoService;

    @Autowired
    private IWorkOrderRelService workOrderRelService;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private IOrderInfoVerService orderInfoVerService;

    @Autowired
    private IOrderGoodsVerService orderGoodsVerService;

    @Autowired
    private IOrderInfoExtVerService orderInfoExtVerService;

    @Autowired
    private IOrderFeeExtVerService orderFeeExtVerService;

    @Autowired
    private IOrderSchedulerVerService orderSchedulerVerService;

    @Autowired
    private IOrderPaymentDaysInfoVerService orderPaymentDaysInfoVerService;

    @Autowired
    private IOrderFeeVerService orderFeeVerService;

    @Autowired
    private IOrderTransitLineInfoVerService orderTransitLineInfoVerService;

    @Autowired
    private IOrderOilDepotSchemeVerService orderOilDepotSchemeVerService;

    @Resource
    private IOrderOilSourceService orderOilSourceService;

    @Resource
    private IRechargeOilSourceService rechargeOilSourceService;

    @Resource
    private IOrderInfoExtHService orderInfoExtHService;

    @Resource
    private IOrderProblemInfoService orderProblemInfoService;

    @Resource
    private IOrderAgingInfoService orderAgingInfoService;

    @Resource
    private IOaLoanService oaLoanService;

    @Resource
    private IOrderDriverSubsidyService orderDriverSubsidyService;

    @Resource
    private OrderDateUtil orderDateUtil;

    @Autowired
    private IOrderVehicleTimeNodeService orderVehicleTimeNodeService;

    @Autowired
    private ICmCustomerInfoService cmCustomerInfoService;

    @Autowired
    private IOrderOilCardInfoVerService orderOilCardInfoVerService;

    @Autowired
    private IOilEntityService oilEntityService;

    @Resource
    private IOrderDriverSubsidyVerService orderDriverSubsidyVerService;

    @Autowired
    private IOrderDriverSwitchInfoService orderDriverSwitchInfoService;

    @Resource
    private IOrderLimitService iOrderLimitService;

    @Resource
    private IOrderGoodsHService orderGoodsHService;

    @Resource
    private IOpAccountService opAccountService;

    @Resource
    private VehicleStaticDataUtil vehicleStaticDataUtil;

    @Resource
    private LoginUtils loginUtils;

    @Autowired
    private IOilCardLogService oilCardLogService;

    @Resource
    private IOrderCostReportService iOrderCostReportService;

    @DubboReference(version = "1.0.0")
    private IAuditOutService auditOutService;

    @DubboReference(version = "1.0.0")
    private ILineOilDepotSchemeService lineOilDepotSchemeService;

    @DubboReference(version = "1.0.0")
    private IOilPriceProvinceService oilPriceProvinceService;

    @DubboReference(version = "1.0.0")
    IUserReceiverInfoService userReceiverInfoService;

    @DubboReference(version = "1.0.0")
    private ICmCustomerLineSubwayService cmCustomerLineSubwayService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    private IOrderStateTrackOperService orderStateTrackOperService;

    @Resource
    private IOrderFeeService iOrderFeeService;

    @Autowired
    private ITenantReceiverRelService tenantReceiverRelService;


    @Resource
    private IOrderPayMethodService orderPayMethodService;


    @Autowired
    private IOrderOpRecordService orderOpRecordService;

    @Autowired
    private IPayoutIntfService payoutIntfService;


    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @DubboReference(version = "1.0.0")
    ISysMenuBtnService sysMenuBtnService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Autowired
    IOrderOilCardInfoService iOrderOilCardInfoService;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;
    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;
    @Resource
    private ITransferInfoService transferInfoService;
    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;
    @Autowired
    private IOrderInfoHService orderInfoHService;
    @Autowired
    private IOrderFeeHService orderFeehService;
    @Autowired
    private ICreditRatingRuleService creditRatingRuleService;


    @DubboReference(version = "1.0.0")
    ISysUserOrgRelService sysUserOrgRelService;

    @Resource
    IOrderFeeExtHService orderFeeExtHService;

    @Resource
    IOrderSchedulerHService orderSchedulerHService;

    @Resource
    IOrderInfoService orderInfoService;

    @Resource
    IOrderReceiptService iOrderReceiptService;

    @DubboReference(version = "1.0.0")
    ITenantUserRelService iTenantUserRelService;

    @Resource
    IOrderTransitLineInfoHService orderTransitLineInfoHService;

    @Resource
    IClaimExpenseInfoService claimExpenseInfoService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    IOrderAccountOilSourceService orderAccountOilSourceService;

    @Override
    @Transactional
    public String saveOrder(OrderDto orderDto, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        //??????????????????
        OrderPaymentDaysInfo incomePaymentDaysInfo = new OrderPaymentDaysInfo();
        CustomerInfoVo customerInfoVo = orderDto.getCustomerInfoVo();
        IncomeBalanceDataVo incomeBalanceDataVo = customerInfoVo.getIncomeBalanceDataVo();
        BeanUtils.copyProperties(incomeBalanceDataVo, incomePaymentDaysInfo);
        this.setOrderPaymentDaysInfo(incomePaymentDaysInfo);
        //??????????????????
        OrderPaymentDaysInfo dispatchPaymentDaysInfo = new OrderPaymentDaysInfo();
        DispatchBalanceDataVo balanceDataVo = orderDto.getDispatchInfoVo().getDispatchBalanceDataVo();
        BeanUtils.copyProperties(balanceDataVo, dispatchPaymentDaysInfo);
        this.setOrderPaymentDaysInfo(dispatchPaymentDaysInfo);


        Integer updateType = orderDto.getUpdateType();//????????????
        Integer dispath = orderDto.getDispatch() == null ? -1 : orderDto.getDispatch();//??????
        Integer orderDetailsType = orderDto.getOrderDetailsType();
        Boolean isCopy = false;
        if (orderDetailsType != null && orderDetailsType == 2) {
            isCopy = true;
        }
//        if(orderDto.getDispatchInfoVo().getVehicleClass() != null){
//            if(orderDto.getDispatchInfoVo().getVehicleClass() == 2  || orderDto.getDispatchInfoVo().getVehicleClass() == 3){
//                orderDto.getDispatchInfoVo().setPaymentWay(3);
//            }
//        }
        OrderInfo orderInfo = new OrderInfo();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderGoods orderGoods = new OrderGoods();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        List<OrderOilDepotScheme> orderOilDepotSchemeList = new ArrayList<>();
        //??????????????????
        this.setOrderInfo(orderInfo, orderDto);
        //??????????????????
        this.setGoodsInfo(orderGoods, orderDto);
        //??????????????????
        this.setOrderFee(orderFee, orderDto, user);
//        orderFee.setTotalFee(objToLongMul100(orderDto.getDispatchInfoVo().getTotalFee()));
        //????????????????????????
        this.setOrderInfoExt(orderInfoExt, orderDto);
        orderInfoExt.setPaymentWay(orderDto.getDispatchInfoVo().getPaymentWay());
        //??????????????????
        this.setDispatchInfo(orderScheduler, orderOilDepotSchemeList, orderDto);
        //???????????????????????????
        this.setOrderFeeExt(orderFeeExt, orderDto);
        //?????????????????????????????????
        if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
            orderFeeExt.setCopilotSalary(orderFeeExt.getCopilotSalary() == null ? 0 : orderFeeExt.getCopilotSalary());
            orderFeeExt.setSalary(orderFeeExt.getSalary() == null ? 0 : orderFeeExt.getSalary());
            orderFeeExt.setPontage(orderFeeExt.getPontage() == null ? 0 : orderFeeExt.getPontage());
            orderFee.setPreOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
            orderFee.setPreOilVirtualFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
            orderFee.setPreTotalFee(orderFeeExt.getCopilotSalary() + orderFeeExt.getSalary() + orderFeeExt.getPontage()
                    + orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee());
        }
        //??????????????????  TODO
        List<OrderOilCardInfo> orderOilCardInfos = new ArrayList<>();
        if (orderDto.getDispatchInfoVo().getOilCardStr() != null && orderDto.getDispatchInfoVo().getOilCardStr().size() > 0) {
            List<OrderOilCardInfoVo> oilCardStr = orderDto.getDispatchInfoVo().getOilCardStr();
            for (OrderOilCardInfoVo orderOilCardInfoVo : oilCardStr) {
                String oilFee = orderOilCardInfoVo.getOilFee();
                if (oilFee != null) {
                    OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
                    BeanUtils.copyProperties(orderOilCardInfoVo, orderOilCardInfo);
                    orderOilCardInfo.setOilFee(objToLongMul100(oilFee));
                    orderOilCardInfos.add(orderOilCardInfo);
                }
            }
        }
        //???????????????
        List<OrderTransitLineInfo> orderTransitLineInfos = this.setTransitLineInfos(orderDto.getCustomerInfoVo().getSubWayListStr());

        //???????????????orderId ??? ??????id
        this.setOrderIdAndId(orderInfo, orderFee, orderGoods, orderInfoExt, orderFeeExt, orderScheduler, orderDto);
        //????????????
        this.billingSaveCheck(orderInfo, orderGoods, orderScheduler, orderFee, orderOilDepotSchemeList, user);
        Long orderId = null;


        if (updateType != null && updateType > 0 && dispath <= 0) {
            this.setOtherData(orderDto, orderInfo, orderFee, orderGoods, orderInfoExt, orderFeeExt, orderScheduler);
            String oilCardNum = orderDto.getOilCardNum();
            updateOrderInfoByPortion(orderInfo, orderFee, orderGoods, orderInfoExt, orderFeeExt, orderScheduler
                    , orderOilDepotSchemeList, updateType, StringUtils.isBlank(oilCardNum) ? null : Arrays.asList(oilCardNum.split(",")),
                    dispatchPaymentDaysInfo, incomePaymentDaysInfo, orderOilCardInfos, orderTransitLineInfos, user, accessToken);
        } else {
            //????????????
            if (dispath != null && dispath == 1) {
                //todo
                //      iOrderInfoTF.orderInfoReassign(orderInfo, orderInfoExt, orderGoods, orderFee, orderFeeExt, orderScheduler, orderOilDepotSchemeList, dispatchPaymentDaysInfo, null, "????????????");
                orderInfoReassign(orderInfo, orderInfoExt, orderGoods, orderFee, orderFeeExt, orderScheduler,
                        orderOilDepotSchemeList, dispatchPaymentDaysInfo, null, "????????????", user, accessToken);
            } else {
                orderId = saveOrUpdateOrderInfo(orderInfo, orderFee, orderGoods, orderInfoExt, orderFeeExt, orderScheduler
                        , orderOilDepotSchemeList, dispatchPaymentDaysInfo,
                        incomePaymentDaysInfo, orderOilCardInfos, orderTransitLineInfos, isCopy, accessToken, user);
            }
        }

//        Map<String, String> rtnMap = new HashMap<String, String>(1);
//        rtnMap.put("orderId", orderId != null ? String.valueOf(orderId) : "");
//        return  JsonHelper.json(rtnMap);
        return orderId != null ? String.valueOf(orderId) : "";
    }
//***************************************  ?????????todo??????    *************************************************************/

    /**
     * ??????????????????(?????????)
     *
     * @param orderInfo
     * @param orderFee
     * @param orderGoods
     * @param orderInfoExt
     * @param orderFeeExt
     * @param orderScheduler
     * @param depotSchemes
     * @param updateType            ????????????
     * @param oilCardNums           ?????????
     * @param costPaymentDaysInfo
     * @param incomePaymentDaysInfo
     * @param oilCardInfos
     * @param transitLineInfos
     * @return
     * @throws Exception
     */
    String updateOrderInfoByPortion(OrderInfo orderInfo, OrderFee orderFee, OrderGoods orderGoods,
                                    OrderInfoExt orderInfoExt, OrderFeeExt orderFeeExt, OrderScheduler orderScheduler,
                                    List<OrderOilDepotScheme> depotSchemes, int updateType, List<String> oilCardNums
            , OrderPaymentDaysInfo costPaymentDaysInfo, OrderPaymentDaysInfo incomePaymentDaysInfo,
                                    List<OrderOilCardInfo> oilCardInfos, List<OrderTransitLineInfo> transitLineInfos, LoginInfo user, String accessToken) {


        if (orderInfo == null
                || (orderInfo.getOrderId() == null || orderInfo.getOrderId() <= 0)) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        //??????????????????????????????????????????????????????0
        if (orderScheduler.getAppointWay() != null && orderScheduler.getAppointWay() == 3 && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 3) {
            orderFee.setGuidePrice(0L);
        }
        // todo ?????????????????????
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "updateOrderInfoByPortion" + orderInfo.getOrderId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        int updateTypeOld = 0;
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
        incomePaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        this.initOrderPaymentDaysInfo(costPaymentDaysInfo);
        this.initOrderPaymentDaysInfo(incomePaymentDaysInfo);
        OrderInfo orderInfoOld = this.getOrder(orderInfo.getOrderId());
        OrderScheduler orderSchedulerOld = orderSchedulerService.getOrderScheduler(orderInfo.getOrderId());
        OrderInfoExt orderInfoExtOld = orderInfoExtService.selectByOrderId(orderInfo.getOrderId());
        OrderFee orderFeeOld = orderFeeService.getOrderFeeByOrderId(orderInfo.getOrderId());
        OrderFeeExt orderFeeExtOld = orderFeeExtService.selectByOrderId(orderInfo.getOrderId());
        if (orderFeeExt.getPontage() != null && orderFeeExt.getPontage() > 0) {
            orderFeeExtOld.setPontage(orderFeeExt.getPontage());
        }
        OrderGoods orderGoodsOld = orderGoodsService.selectByOrderId(orderInfo.getOrderId());

//        IOrderPaymentDaysInfoSV orderPaymentDaysInfoSV = (IOrderPaymentDaysInfoSV) SysContexts.getBean("orderPaymentDaysInfoSV");
        OrderPaymentDaysInfo costPaymentDaysInfoOld = this.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        OrderPaymentDaysInfo incomePaymentDaysInfoOld = this.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);

        if (orderInfoExtOld != null) {
            if (!ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[]{"preAmountFlag"})) {//?????????????????????????????????
                throw new BusinessException("???????????????????????????????????????????????????????????????");
            }
        } else {
            throw new BusinessException("???????????????[" + orderInfo.getOrderId() + "]???????????????????????????");
        }
        if (orderInfoOld.getToTenantId() != null && orderInfoOld.getToTenantId() > 0) {//??????
            updateTypeOld = OrderConsts.UPDATE_TYPE.IS_TRANSFER_ORDER;
            if (orderInfoOld.getFromTenantId() != null && orderInfoOld.getFromTenantId() > 0) {//????????????
                updateTypeOld = OrderConsts.UPDATE_TYPE.MIDDLE_ORDER;
            }
        } else {
            if (orderInfoOld.getFromTenantId() != null && orderInfoOld.getFromTenantId() > 0) {//????????????
                updateTypeOld = OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER;
            } else {//?????????
                updateTypeOld = OrderConsts.UPDATE_TYPE.OWN_ORDER;
            }
        }
//        if (updateTypeOld != updateType) {
//            throw new BusinessException("??????????????????????????????????????????????????????????????????");
//        }
        //????????????????????????
        if (orderInfo.getIsNeedBill() == null) {
            orderInfo.setIsNeedBill(orderInfoOld.getIsNeedBill());
        }
        if (orderInfoExt.getPaymentWay() == null) {
            orderInfoExt.setPaymentWay(orderInfoExtOld.getPaymentWay());
        }
        if (orderFee.getAfterPayAcctType() == null) {
            orderFee.setAfterPayAcctType(orderFeeOld.getAfterPayAcctType());
        }
        if (orderFee.getPrePayEquivalenceCardType() == null) {
            orderFee.setPrePayEquivalenceCardType(orderFeeOld.getPrePayEquivalenceCardType());
        }
        if (orderFee.getAfterPayEquivalenceCardType() == null) {
            orderFee.setAfterPayEquivalenceCardType(orderFeeOld.getAfterPayEquivalenceCardType());
        }
        if (orderScheduler.getIsUrgent() == null) {
            orderScheduler.setIsUrgent(orderSchedulerOld.getIsUrgent());
        }
        if (orderScheduler.getVehicleClass() == null) {
            orderScheduler.setVehicleClass(orderSchedulerOld.getVehicleClass());
        }
        //??????????????????
        boolean isUpdateSchedulerLine = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.LINE_PROERTY);
        // todo ?????????
        if (orderGoodsOld == null) {
            orderGoodsOld = new OrderGoods();
        }
        boolean isUpdateGoodsLine = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.LINE_PROERTY);
        boolean isUpdateOrderInfoLine = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, OrderConsts.UPDATE_COMPARE_PROERTY.LINE_PROERTY);
        //??????????????????
        boolean isUpdateGoods = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.GOODS_PROERTY);
        //??????????????????
        boolean isUpdateRecive = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.RECIVE_PROERTY);
        //??????????????????
        boolean isUpdateIncomeFee = !ObjectCompareUtils.isModifyObj(orderFeeOld, orderFee, OrderConsts.UPDATE_COMPARE_PROERTY.INCOME_PROERTY);
        boolean isUpdateIncomeFeeExt = !ObjectCompareUtils.isModifyObj(orderFeeExtOld, orderFeeExt, OrderConsts.UPDATE_COMPARE_PROERTY.INCOME_PROERTY);
        boolean isUpdateIncomeScheduler = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.INCOME_PROERTY_SCHEDULER);
        boolean isUpdateIncomePaymentDays = false;
        if (orderInfoOld.getOrderType() != null && orderInfoOld.getOrderType().intValue() != OrderConsts.OrderType.ONLINE_RECIVE) {
            isUpdateIncomePaymentDays = !ObjectCompareUtils.isModifyObj(incomePaymentDaysInfoOld, incomePaymentDaysInfo, OrderConsts.UPDATE_COMPARE_PROERTY.PAYMENT_DAYS_PROERTY);
        }
        //??????????????????
        boolean isUpdateScheduler = !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, OrderConsts.UPDATE_COMPARE_PROERTY.SCHEDULER_PROERTY);
        boolean isUpdateSchedulerOrg = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, OrderConsts.UPDATE_COMPARE_PROERTY.SCHEDULER_PROERTY);
        boolean isUpdateSchedulerGoods = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.SCHEDULER_PROERTY);

        //??????????????????
        boolean isUpdateCostFee = !ObjectCompareUtils.isModifyObj(orderFeeOld, orderFee, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostFeeExt = !ObjectCompareUtils.isModifyObj(orderFeeExtOld, orderFeeExt, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostInfo = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostScheduler = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostPaymentDays = !ObjectCompareUtils.isModifyObj(costPaymentDaysInfoOld, costPaymentDaysInfo, OrderConsts.UPDATE_COMPARE_PROERTY.PAYMENT_DAYS_PROERTY);
        boolean isUpdateCostInfoExt = !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostOilCard = false;
        //??????????????????
        boolean isUpdateBackhaul = !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, OrderConsts.UPDATE_COMPARE_PROERTY.BAACKHAUL_PROERTY);
        //??????????????????
        boolean isUpdateRamark = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, new String[]{"remark"});
        //??????????????????
        boolean isUpdateDriver = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.DRIVER_PROERTY);
        boolean isUpdateScheme = false;
        //??????????????????
        boolean isUpdatePlateNumber = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.PLATE_NUMBER_PROERTY);
        boolean isUpdateBill = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, new String[]{"isNeedBill"});
        if (depotSchemes != null && depotSchemes.size() > 0) {
            for (int i = depotSchemes.size() - 1; i >= 0; i--) {
                OrderOilDepotScheme scheme = depotSchemes.get(i);
                scheme.setOrderId(orderInfo.getOrderId());
                if (scheme.getOilDepotId() == null || scheme.getOilDepotId() <= 0) {
                    depotSchemes.remove(scheme);
                }
            }
        }
        //??????????????????????????????
        if (orderSchedulerOld.getVehicleClass() != null
                && orderSchedulerOld.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            List<OrderOilDepotScheme> schemes = orderOilDepotSchemeService.getOrderOilDepotSchemeByOrderId(orderInfo.getOrderId(), false, user);
            if (depotSchemes != null && depotSchemes.size() > 0) {
                if (schemes != null && schemes.size() > 0) {
                    if (schemes.size() != depotSchemes.size()) {
                        isUpdateScheme = true;
                    } else {
                        for (int i = 0; i < schemes.size(); i++) {
                            OrderOilDepotScheme scheme = schemes.get(i);
                            OrderOilDepotScheme scheme2 = depotSchemes.get(i);
                            if (!ObjectCompareUtils.isModifyObj(scheme, scheme2, new String[]{"oilDepotId", "oilDepotName", "oilDepotFee", "oilDepotLitre", "dependDistance", "oilDepotPrice"})) {
                                isUpdateScheme = true;
                            }
                        }
                    }
                } else {
                    isUpdateScheme = true;
                }
            } else {
                if (schemes != null && schemes.size() > 0) {
                    isUpdateScheme = true;
                }
            }
        }
        boolean isUpdateTransitLine = false;
        List<OrderTransitLineInfo> transitLineInfosOld = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            if (transitLineInfosOld != null && transitLineInfosOld.size() > 0) {
                if (transitLineInfos.size() != transitLineInfosOld.size()) {
                    isUpdateTransitLine = true;
                } else {
                    for (int i = 0; i < transitLineInfos.size(); i++) {
                        OrderTransitLineInfo lineInfo = transitLineInfos.get(i);
                        OrderTransitLineInfo lineInfoOld = transitLineInfosOld.get(i);
                        if (!ObjectCompareUtils.isModifyObj(lineInfo, lineInfoOld, OrderConsts.UPDATE_COMPARE_PROERTY.TRANSIT_LINE_PROERTY)) {
                            isUpdateTransitLine = true;
                        }
                    }
                }
            } else {
                isUpdateTransitLine = true;
            }
        } else {
            if (transitLineInfosOld != null && transitLineInfosOld.size() > 0) {
                isUpdateTransitLine = true;
            }
        }
        //???????????????????????????
        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getOilUseType() != null && orderInfoExt.getOilUseType() == OrderConsts.OIL_USE_TYPE.CUSTOMOIL) {

            long customOilFee = this.queryCustomOil(orderInfoExt.getOrderId(), user.getTenantId(), user);
            if (orderFee.getPreOilVirtualFee() != null && customOilFee < orderFee.getPreOilVirtualFee()) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
        }

        //????????????????????????????????????
        if ((orderInfoExtOld.getPaymentWay() != null && orderInfoExtOld.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)
                || (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)) {

            List<OrderOilCardInfo> oilCardInfosOld = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderInfo.getOrderId(), null);
            if (oilCardInfos != null && oilCardInfos.size() > 0) {
                if (oilCardInfosOld == null || oilCardInfos.size() != oilCardInfosOld.size()) {
                    isUpdateCostOilCard = true;
                } else {
                    for (int i = 0; i < oilCardInfos.size(); i++) {
                        OrderOilCardInfo orderOilCardInfo = oilCardInfos.get(i);
                        OrderOilCardInfo orderOilCardInfoOld = oilCardInfosOld.get(i);
                        if (orderOilCardInfo.getOilFee() != null && orderOilCardInfo.getOilFee() > 0) {
                            isUpdateCostOilCard = true;
                            break;
                        }
                        if (orderOilCardInfoOld.getOilCardNum() != null && !Objects.equals(orderOilCardInfo.getOilCardNum(), orderOilCardInfoOld.getOilCardNum())) {
                            isUpdateCostOilCard = true;
                        }
                    }
                }
            } else if (oilCardInfosOld != null && oilCardInfosOld.size() > 0) {
                isUpdateCostOilCard = true;
            }
        }
        /**??????????????????*/
        boolean isUpdateCollection = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.COLLECTION_PROERTY);
        /**??????????????????**/
        boolean isUpdateSchedulerTime = !ObjectCompareUtils.isModifyObj(orderSchedulerOld,
                orderScheduler, new String[]{"distance", "dependTime", "mileageNumber"});
        if (!(isUpdateSchedulerLine || isUpdateSchedulerTime || isUpdateGoodsLine || isUpdateOrderInfoLine || isUpdateGoods || isUpdateRecive || isUpdateIncomeFee || isUpdateIncomeFeeExt || isUpdateIncomeScheduler
                || isUpdateScheduler || isUpdateSchedulerOrg || isUpdateCostFee || isUpdateCostFeeExt || isUpdateCostScheduler || isUpdateCostInfo
                || isUpdateBackhaul || isUpdateRamark || isUpdateScheme || isUpdateDriver || isUpdateIncomePaymentDays || isUpdateCostPaymentDays
                || isUpdateCostInfoExt || isUpdateCostOilCard || isUpdateTransitLine || isUpdatePlateNumber || isUpdateCollection || isUpdateBill || isUpdateSchedulerGoods)) {
            throw new BusinessException("????????????????????????");
        }
        if (updateType == OrderConsts.UPDATE_TYPE.OWN_ORDER) {

        } else if (updateType == OrderConsts.UPDATE_TYPE.IS_TRANSFER_ORDER && orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0) {
            if (isUpdateSchedulerLine || isUpdateGoodsLine || isUpdateOrderInfoLine || isUpdateTransitLine) {
//                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateCostFee || isUpdateCostFeeExt || isUpdateCostInfo || isUpdateCostPaymentDays || isUpdateCostInfoExt || isUpdateCostOilCard) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateBackhaul) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdatePlateNumber) {
                throw new BusinessException("????????????????????????????????????");
            }
        } else if (updateType == OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER) {
            if (isUpdateSchedulerLine || isUpdateGoodsLine || isUpdateOrderInfoLine || isUpdateTransitLine) {
//                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateGoods) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateRecive) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateIncomeFeeExt || isUpdateIncomeScheduler || isUpdateIncomePaymentDays) {
                log.error("????????????????????????????????????isUpdateIncomeFee=" + isUpdateIncomeFee + " isUpdateIncomeFeeExt=" + isUpdateIncomeFeeExt +
                        " isUpdateIncomeScheduler =" + isUpdateIncomeScheduler + " isUpdateIncomePaymentDays" + isUpdateIncomePaymentDays);
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateBackhaul) {
                throw new BusinessException("????????????????????????????????????");
            }
        } else if (updateType == OrderConsts.UPDATE_TYPE.MIDDLE_ORDER) {
            throw new BusinessException("????????????????????????");
        }
        if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"arriveTime"})) {
            if (updateType != OrderConsts.UPDATE_TYPE.OWN_ORDER) {
                throw new BusinessException("????????????????????????????????????");
            }
        }
        //boolean isUpdateOilBill =  !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[] {"oilIsNeedBill"});
        if (orderInfoExtOld.getFirstPayFlag() != null && orderInfoExtOld.getFirstPayFlag().intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            if (isUpdateBill) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
        }
        if (isUpdateCollection) {
            boolean orderCostPermission = sysRoleService.hasOrderCostPermission(user);
            if (!orderCostPermission) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
            List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),
                    user.getTenantId());
            if (list != null && list.size() > 0) {
                for (OrderProblemInfo problemInfo : list) {
                    if (problemInfo.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.CANCEL
                            && problemInfo.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN) {
                        throw new BusinessException("??????????????????????????????????????????");
                    }
                }
            }
            if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                //??????????????? ?????????????????????
                if (StringUtils.isBlank(orderScheduler.getCollectionUserPhone())) {
                    throw new BusinessException("??????????????????????????????");
                }
                if (StringUtils.isBlank(orderScheduler.getCollectionName())) {
                    throw new BusinessException("??????????????????????????????");
                }
                UserDataInfo userDataInfo = userDataInfoService.getPhone(orderScheduler.getCollectionUserPhone());
                if (userDataInfo != null) {
                    if (orderScheduler.getCarDriverId().equals(orderScheduler.getCollectionUserId())) {
                        throw new BusinessException("?????????????????????????????????");
                    }
                }
            }
        }
        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[]{"paymentWay"})) {
            if (updateType != OrderConsts.UPDATE_TYPE.OWN_ORDER
                    && updateType != OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER) {
                throw new BusinessException("????????????????????????????????????");
            }

            OrderPaymentWayOilOut out = this.getOrderPaymentWayOil(orderScheduler.getCarDriverId(), user.getTenantId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER);

            if (orderInfoExtOld.getPaymentWay() != null && orderInfoExtOld.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE
                    && orderInfoExt.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                //????????????????????????
                boolean bool = orderSchedulerService.checkPayMentWaySwitchover(orderScheduler.getPlateNumber()
                        , orderScheduler.getDependTime(), user.getTenantId(), orderScheduler.getOrderId());
                if (!bool) {
                    throw new BusinessException("???????????????????????????????????????");
                }
                if (out.getExpenseOil() != null && out.getExpenseOil() > 0) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                }
            } else if (orderInfoExtOld.getPaymentWay() != null && orderInfoExtOld.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE
                    && orderInfoExt.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                if ((out.getCostOil() != null && out.getCostOil() > 0) || (out.getContractOil() != null && out.getContractOil() > 0)) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                }
            } else if ((orderInfoExtOld.getPaymentWay() == null || orderInfoExtOld.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.EXPENSE)
                    && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                if (out.getExpenseOil() != null && out.getExpenseOil() > 0) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                }
            }
        }
        boolean isUpdateVehicle = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"vehicleCode", "plateNumber"});
        if (isUpdateVehicle && orderSchedulerOld.getIsCollection() != null && orderSchedulerOld.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (isUpdateDriver) {//??????????????????????????????????????????
            //????????????????????????????????????????????????
            if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"carDriverId", "carDriverMan", "carDriverPhone"})) {
                /**
                 * ?????????????????????????????????????????????????????????????????????????????????????????????
                 */
				/*orderSchedulerTF.checkLineIsOk(null, orderScheduler.getCarDriverId(),
						null, orderScheduler.getDependTime(), orderInfo.getFromOrderId(), orderInfo.getOrderId());*/
                this.isExistOtherFee(orderScheduler.getOrderId());
            }
            if (orderSchedulerOld.getCopilotUserId() != null && orderSchedulerOld.getCopilotUserId() > 0 && !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"copilotMan", "copilotPhone", "copilotUserId"})) {
                this.isExistOtherFee(orderScheduler.getOrderId());
            }
            if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
                if (orderScheduler.getCarDriverId().longValue() == orderScheduler.getCopilotUserId()
                        .longValue()) {
                    throw new BusinessException("??????????????????????????????????????????????????????");
                }
            }
        }
        if (isUpdateIncomePaymentDays) {
            if (orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
                this.checkPaymentDays(incomePaymentDaysInfo, orderInfo, orderScheduler, orderFee);
            }
        }
        if (isUpdateCostPaymentDays) {//????????????????????????
            this.checkPaymentDays(costPaymentDaysInfo, orderInfo, orderScheduler, orderFee);
        }
        //????????????????????????
        //??????????????????????????????????????????
        orderOilCardInfoVerService.releaseOilCardBalance(orderFee.getOrderId(), user.getTenantId());
        //?????????????????????????????????????????????
        orderOilDepotSchemeVerService.updateSchemeVerIsUpdate(orderFee.getOrderId(), OrderConsts.IS_UPDATE.NOT_UPDATE, user);
        //???????????????????????????????????????
        orderPaymentDaysInfoVerService.loseEfficacyPaymentDaysVerUpdate(orderFee.getOrderId());
        //???????????????????????????
        orderTransitLineInfoVerService.updateTransitLineInfoVerType(orderFee.getOrderId(), OrderConsts.IS_UPDATE.NOT_UPDATE, OrderConsts.IS_UPDATE.UPDATE);
        //???????????????????????????????????????????????????
        orderDriverSubsidyService.updateDriverSubsidyVer(orderFee.getOrderId(), null, null, 1);

        if (orderInfoExtOld.getPreAmountFlag() == com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            if (isUpdateCollection && orderInfoOld.getToTenantId() != null && orderInfoOld.getToTenantId() > 0) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!ObjectCompareUtils.isModifyObj(orderFeeOld, orderFee, new String[]{"prePayEquivalenceCardNumber", "afterPayEquivalenceCardNumber"})) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"appointWay"})) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[]{"paymentWay"})) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (isUpdateDriver) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if ((orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE))) {
//                if ((orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee()) < (orderFeeOld.getPreOilVirtualFee() == null ? 0 : orderFeeOld.getPreOilVirtualFee())) {
//                    throw new BusinessException("???????????????????????????????????????????????????????????????");
//                }
            }
            if (isUpdateIncomePaymentDays) {
                if (incomePaymentDaysInfoOld.getBalanceType() != null
                        && incomePaymentDaysInfoOld.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL) {
                    if (!ObjectCompareUtils.isModifyObj(incomePaymentDaysInfoOld, incomePaymentDaysInfo, new String[]{"balanceType"})) {
                        throw new BusinessException("??????????????????????????????????????????????????????");
                    }
                }
            }
            if (isUpdateCostPaymentDays) {//????????????????????????
                if (costPaymentDaysInfoOld.getBalanceType() != null
                        && costPaymentDaysInfoOld.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL) {
                    if (!ObjectCompareUtils.isModifyObj(costPaymentDaysInfoOld, costPaymentDaysInfo, new String[]{"balanceType"})) {
                        throw new BusinessException("??????????????????????????????????????????????????????");
                    }
                }
            }
            //???????????????????????????????????????
            if (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE) {//????????????????????????????????????
                if (orderFeeOld.getPreOilFee() < orderFee.getPreOilFee()) {//??????????????????
                    if (oilCardNums == null || oilCardNums.size() == 0) {
                        throw new BusinessException("????????????????????????");
                    }
                    Long oilFee = orderFee.getPreOilFee() - orderFeeOld.getPreOilFee();
                    //????????????
                    orderFeeService.verifyOilCardNum(orderFee.getOrderId(), oilCardNums, false, oilFee, accessToken);
                    //??????????????????
                    int service = 0;
                    for (String oilCardNum : oilCardNums) {
                        if (StringUtils.isBlank(oilCardNum)) {
                            throw new BusinessException("??????????????????");
                        }


                        List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, orderFeeOld.getTenantId());
                        if (list == null || list.size() <= 0) {
                            throw new BusinessException("????????????[" + oilCardNum + "]?????????!");
                        }

                        OilCardManagement oilCard = list.get(0);
                        //?????????????????????????????????
                        int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
                        Long balance = 0L;
                        Long oilCardFee = oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;

                        //???????????????????????????????????????
                        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            if (service == 1) {
                                throw new BusinessException("??????????????????????????????,??????[" + oilCardNum + "]!");
                            }
                            service++;
                            balance = oilFee;
                        } else {
                            //???????????????????????? ??????????????????????????????
                            if (oilFee <= oilCardFee) {
                                balance = oilFee;
                            } else {//???????????????????????? ????????????????????? ????????????
                                balance = oilCardFee;
                            }
                        }
                        oilFee = oilFee - balance;
                        //????????????????????????(????????????/????????????)
                        if (cardType != SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            oilCardManagementService.pledgeOilCardBalance(oilCardNum, balance, user.getTenantId(), orderInfoOld.getToTenantId(), false);
                        }
                        OrderOilCardInfoVer orderOilCardInfo = new OrderOilCardInfoVer();
                        orderOilCardInfo.setCreateTime(LocalDateTime.now());
                        orderOilCardInfo.setOilCardNum(oilCardNum);
                        orderOilCardInfo.setOilFee(balance);
                        orderOilCardInfo.setCardType(cardType);
                        orderOilCardInfo.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
                        orderOilCardInfo.setOrderId(orderInfo.getOrderId());
                        orderOilCardInfo.setTenantId(orderInfo.getTenantId());
                        orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfo);
                    }
                    if (oilFee != 0) {
                        throw new BusinessException("????????????????????????!");
                    }
                }
            }
        } else {
            if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"appointWay"})
                    && orderInfoOld.getOrderState() != null && orderInfoOld.getOrderState() > OrderConsts.ORDER_STATE.TO_BE_LOAD
            ) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
        }
        //??????
        if (oilCardInfos != null && oilCardInfos.size() > 0) {
            for (OrderOilCardInfo orderOilCardInfo : oilCardInfos) {
                OrderOilCardInfoVer orderOilCardInfoVer = new OrderOilCardInfoVer();
                BeanUtils.copyProperties(orderOilCardInfo, orderOilCardInfoVer);
                orderOilCardInfoVer.setCreateTime(LocalDateTime.now());
                orderOilCardInfoVer.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
                orderOilCardInfoVer.setOrderId(orderInfo.getOrderId());
                orderOilCardInfoVer.setTenantId(orderInfo.getTenantId());
                orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfoVer);
            }
        }
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                OrderTransitLineInfoVer lineInfoVer = new OrderTransitLineInfoVer();
                BeanUtils.copyProperties(orderTransitLineInfo, lineInfoVer);
                lineInfoVer.setCreateTime(LocalDateTime.now());
                lineInfoVer.setIsUpdate(OrderConsts.IS_UPDATE.UPDATE);
                lineInfoVer.setOrderId(orderInfo.getOrderId());
                lineInfoVer.setTenantId(orderInfo.getTenantId());
                orderTransitLineInfoVerService.saveOrUpdate(lineInfoVer);
            }
        }
        if (isUpdateBill) {
            if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                // ???????????????,????????????????????????????????????????????????????????????????????????

                Long billMethod = billPlatformService.getBillMethodByTenantId(orderInfo.getTenantId());
                if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    if (orderFee.getCostPrice() == null || orderFee.getCostPrice() == 0) {
                        throw new BusinessException("???????????????0???????????????????????????");
                    }
                }
                if (billMethod == null || billMethod == 0) {
                    String customerPhone = readisUtil.getSysCfg(CUSTOMER_SERVICE_PHONE, "0").getCfgValue();
                    throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" + customerPhone);
                }
                BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(billMethod);
                if (billPlatform == null) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                }
                orderFee.setVehicleAffiliation(billMethod.toString());
            } else {
                orderFee.setVehicleAffiliation(orderInfo.getIsNeedBill() == null ? null : orderInfo.getIsNeedBill().toString());
            }
        }
        if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                && (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"dependTime"})
                || isUpdateBill)
        ) {
            //??????????????????
            orderSchedulerService.checkBillDependTime(orderInfo, orderScheduler, Long.parseLong(orderFee.getVehicleAffiliation()), user);
        }
        //??????????????????
        orderInfoExtService.verifyOrderNeedBill(orderInfo, orderInfoExt, orderScheduler, orderFee, orderGoods, transitLineInfos, user);
        //?????????????????????
        if (orderInfo.getIsNeedBill() != null
                && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL &&
                !(orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)) {
            orderInfoExt.setOilAffiliation(orderFee.getVehicleAffiliation());
        } else {
            orderInfoExt.setOilAffiliation("1");
        }
        if (orderSchedulerOld.getVehicleClass() != null
                && orderSchedulerOld.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == com.youming.youche.order.commons.OrderConsts.PAYMENT_WAY.COST) {//?????????????????????????????????????????????
            //????????????????????????
            this.setDriverSubsidy(orderScheduler, orderFeeExt, user, true, transitLineInfos != null ? transitLineInfos.size() : 0, transitLineInfos, accessToken, orderInfoExt, orderInfo, orderFee);
        }
        if (orderInfoOld.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
            // ????????????????????????????????? ??????????????????????????? ???????????????????????????
            checkCostPriceSum(orderFee.getCostPrice(), orderFee.getPrePayCash(), orderFee.getAfterPayCash(),
                    orderFee.getPrePayEquivalenceCardAmount(), orderFee.getAfterPayEquivalenceCardAmount(), user);
        }
        // ???????????????????????????
        if (orderSchedulerOld.getVehicleClass() != null
                && orderSchedulerOld.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST
                && orderFeeExt.getOilLitreVirtual() != null && orderFeeExt.getOilLitreVirtual() > 0) {
            if (depotSchemes != null && depotSchemes.size() > 0) {
                Long oilDepotLitre = 0L;
                for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                    if (orderOilDepotScheme.getOilDepotId() != null && orderOilDepotScheme.getOilDepotId() > 0) {
                        if (orderOilDepotScheme.getOilDepotLitre() == null || orderOilDepotScheme.getOilDepotLitre() <= 0) {
                            throw new BusinessException("???????????????????????????0!");
                        }
                        oilDepotLitre += orderOilDepotScheme.getOilDepotLitre();
                        orderOilDepotScheme.setOrderId(orderInfo.getOrderId());
                        OrderOilDepotSchemeVer depotSchemeVer = new OrderOilDepotSchemeVer();
                        BeanUtils.copyProperties(orderOilDepotScheme, depotSchemeVer);
                        depotSchemeVer.setIsUpdate(OrderConsts.IS_UPDATE.UPDATE);
                        depotSchemeVer.setTenantId(user.getTenantId());
                        orderOilDepotSchemeVerService.saveOrderOilDepotSchemeVer(depotSchemeVer);
                    }
                }
                if (!oilDepotLitre.equals(orderFeeExt.getOilLitreVirtual())) {
                    log.error("??????????????????=" + oilDepotLitre + ",???????????????=" + orderFeeExt.getOilLitreVirtual());
                    throw new BusinessException("????????????????????????????????????????????????");
                }
            }
        } else {
            if (depotSchemes != null && depotSchemes.size() > 0) {
                for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                    if (orderOilDepotScheme.getOilDepotId() != null && orderOilDepotScheme.getOilDepotId() > 0) {
                        if (orderOilDepotScheme.getOilDepotLitre() == null || orderOilDepotScheme.getOilDepotLitre() <= 0) {
                            throw new BusinessException("???????????????????????????0!");
                        }
                    }
                }
            }
        }
        if (isUpdateCostPaymentDays) {
            //?????????????????????
            orderPaymentDaysInfoVerService.setOrderPaymentDaysInfoVer(costPaymentDaysInfoOld, costPaymentDaysInfo);
        }
        if (isUpdateIncomePaymentDays) {
            //?????????????????????
            orderPaymentDaysInfoVerService.setOrderPaymentDaysInfoVer(incomePaymentDaysInfoOld, incomePaymentDaysInfo);
        }
        //depotSchemes
        //????????????????????????
        this.checkBasicsOrderInfo(orderInfo, orderFee, orderScheduler, orderInfoExt, orderGoods, orderFeeExt, transitLineInfos, true, user);
        //???????????????
        checkOilCarType(orderFee, user.getTenantId());
        //??????????????????????????????
        /*checkPinganBankInfo(baseUser.getTenantId(),orderInfo.getIsNeedBill());*/
        OrderInfoVer orderInfoVerOld = orderInfoVerService.getOrderInfoVer(orderInfo.getOrderId());
        if (orderInfoVerOld == null) {//???????????????????????????
            orderInfoVerOld = new OrderInfoVer();
            OrderInfoExtVer orderInfoExtVerOld = new OrderInfoExtVer();
            OrderFeeVer orderFeeVerOld = new OrderFeeVer();
            OrderFeeExtVer orderFeeExtVerOld = new OrderFeeExtVer();
            OrderSchedulerVer orderSchedulerVerOld = new OrderSchedulerVer();
            OrderGoodsVer orderGoodsVerOld = new OrderGoodsVer();
            BeanUtils.copyProperties(orderInfoOld, orderInfoVerOld);
            BeanUtils.copyProperties(orderInfoExtOld, orderInfoExtVerOld);
            BeanUtils.copyProperties(orderFeeOld, orderFeeVerOld);
            BeanUtils.copyProperties(orderFeeExtOld, orderFeeExtVerOld);
            BeanUtils.copyProperties(orderGoodsOld, orderGoodsVerOld);
            BeanUtils.copyProperties(orderSchedulerOld, orderSchedulerVerOld);
            BeanUtils.copyProperties(orderGoodsOld, orderGoodsVerOld);
            orderInfoVerService.saveOrUpdate(orderInfoVerOld);
            orderInfoExtVerService.saveOrUpdate(orderInfoExtVerOld);
            orderFeeVerService.saveOrUpdate(orderFeeVerOld);
            orderFeeExtVerService.saveOrUpdate(orderFeeExtVerOld);
            orderSchedulerVerService.saveOrUpdate(orderSchedulerVerOld);
            orderGoodsVerService.saveOrUpdate(orderGoodsVerOld);
        }
        OrderInfoVer orderInfoVer = new OrderInfoVer();
        OrderInfoExtVer orderInfoExtVer = new OrderInfoExtVer();
        OrderFeeVer orderFeeVer = new OrderFeeVer();
        OrderFeeExtVer orderFeeExtVer = new OrderFeeExtVer();
        OrderSchedulerVer orderSchedulerVer = new OrderSchedulerVer();
        OrderGoodsVer orderGoodsVer = new OrderGoodsVer();

        BeanUtils.copyProperties(orderInfo, orderInfoVer);
        BeanUtils.copyProperties(orderInfoExt, orderInfoExtVer);
        BeanUtils.copyProperties(orderFee, orderFeeVer);
        BeanUtils.copyProperties(orderFeeExt, orderFeeExtVer);
        BeanUtils.copyProperties(orderGoods, orderGoodsVer);
        BeanUtils.copyProperties(orderScheduler, orderSchedulerVer);
        BeanUtils.copyProperties(orderGoods, orderGoodsVer);

        orderInfoVer.setId(orderInfoOld.getId());
        orderInfoExtVer.setId(orderInfoExtOld.getId());
        orderFeeExtVer.setId(orderFeeExtOld.getId());
        orderFeeVer.setId(orderFeeOld.getId());
        orderSchedulerVer.setId(orderSchedulerOld.getId());
        orderGoodsVer.setId(orderGoodsOld.getId());
        if (orderInfoOld.getOrderState() != null && orderInfoOld.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_AUDIT
                && (orderInfoOld.getOrderUpdateState() != null && orderInfoOld.getOrderUpdateState().intValue() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {
            //????????????
        } else {
            orderInfoOld.setOrderUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
            orderInfoVer.setOrderUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
        }

        orderInfoExtOld.setUpdateType(updateType);
        this.saveOrUpdate(orderInfoOld);
        orderInfoExtService.saveOrUpdate(orderInfoExtOld);

        orderInfoExtVer.setUpdateType(updateType);
        orderInfoVerService.saveOrUpdate(orderInfoVer);
        orderInfoExtVerService.saveOrUpdate(orderInfoExtVer);
        orderFeeVerService.saveOrUpdate(orderFeeVer);
        orderFeeExtVerService.saveOrUpdate(orderFeeExtVer);
        orderSchedulerVerService.saveOrUpdate(orderSchedulerVer);
        orderGoodsVerService.saveOrUpdate(orderGoodsVer);

        /**
         * ????????????
         */
        //????????????
        orderDriverSubsidyVerService.saveDriverSubsidyVer(orderInfoVer.getOrderId(), orderSchedulerVer.getCarDriverId()
                , orderFeeExtVer.getSubsidyTime(), orderFeeExtVer.getDriverDaySalary(), 1, orderInfoExt.getPreAmountFlag(), user);
        //????????????
        orderDriverSubsidyVerService.saveDriverSubsidyVer(orderInfoVer.getOrderId(), orderSchedulerVer.getCopilotUserId()
                , orderFeeExtVer.getCopilotSubsidyTime(), orderFeeExtVer.getCopilotDaySalary(), 2, orderInfoExt.getPreAmountFlag(), user);

        sysOperLogService.save(SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Update,
                "[" + user.getName() + "]????????????(?????????)", user);
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("busiId", orderInfoVer.getOrderId());
        boolean isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, orderInfoVer.getOrderId(), SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
        if (!isAudit) {// ?????????????????????
            this.orderUpdateAuditPass(orderInfo.getOrderId(), "", isAudit, user, accessToken);
        }
        //????????????
        this.handleWorkOrder(orderInfo.getWorkId(), orderInfo.getOrderId(), user);

        return "Y";

    }

    /**
     * ?????????????????????????????????(??????,??????...)
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    public void isExistOtherFee(Long orderId) {
        boolean isExistAgingInfo = orderAgingInfoService.isExistAgingInfo(orderId);
        if (isExistAgingInfo) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        boolean isExistProblemInfoInfo = orderProblemInfoService.isExistProblemInfoInfo(orderId);
        if (isExistProblemInfoInfo) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        boolean checkLoanAndExpense = oaLoanService.checkLoanAndExpenseByOrder(orderId);
        if (checkLoanAndExpense) {
            throw new BusinessException("????????????????????????????????????????????????");
        }

    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param userId   ??????id
     * @param tenantId ??????id
     * @param userType ????????????
     * @return OrderPaymentWayOilOut
     */
    public OrderPaymentWayOilOut getOrderPaymentWayOil(Long userId, Long tenantId, int userType) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id");
        }
        //???????????????????????????
        List<OrderOilSource> list = orderOilSourceService.getOrderOilSourceByUserId(userId, userType);
        //???????????????????????????
        List<RechargeOilSource> list1 = rechargeOilSourceService.getRechargeOilSourceByUserId(userId, userType);
        //??????
        long costOil = 0;
        //????????????
        long expenseOil = 0;
        //??????
        long contractOil = 0;
        if (list != null && list.size() > 0) {
            for (OrderOilSource oilSource : list) {
                if (oilSource.getTenantId().longValue() != tenantId.longValue()) {
                    continue;
                }
                long noPayOil = oilSource.getNoPayOil();
                long noCreditOil = oilSource.getNoCreditOil();
                long noRebateOil = oilSource.getNoRebateOil();
                long orderOil = (noPayOil + noCreditOil + noRebateOil);
                //????????????????????????????????????????????????????????????????????????
                Long orderId = oilSource.getOrderId();

                OrderInfoExt orderInfoExt = orderInfoExtService.selectByOrderId(orderId);
                if (orderInfoExt != null) {
                    //??????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                        costOil += orderOil;
                    }
                    //????????????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                        expenseOil += orderOil;
                    }
                    //??????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT) {
                        contractOil += orderOil;
                    }
                } else {
                    OrderInfoExtH orderInfoExtH = orderInfoExtHService.selectByOrderId(orderId);
                    if (orderInfoExtH != null) {
                        //??????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                            costOil += orderOil;
                        }
                        //????????????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                            expenseOil += orderOil;
                        }
                        //??????
                        if (orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT) {
                            contractOil += orderOil;
                        }
                    } else {
                        throw new BusinessException("??????????????????" + orderId + " ????????????????????????");
                    }
                }
            }
        }
        //???????????????????????????????????????
        if (list1 != null && list1.size() > 0) {
            for (RechargeOilSource ros : list1) {
                if (ros.getTenantId().longValue() != tenantId.longValue()) {
                    continue;
                }
                if (ros.getRechargeType() == null
                        || !OrderAccountConst.RECHARGE_ORDER_ACCOUNT_OIL.FLEET_OIL_REBATE.equals(String.valueOf(ros.getRechargeType()))) {
                    long noPayOil = ros.getNoPayOil();
                    long noCreditOil = ros.getNoCreditOil();
                    long noRebateOil = ros.getNoRebateOil();
                    long orderOil = (noPayOil + noCreditOil + noRebateOil);
                    expenseOil += orderOil;
                }
            }
        }

        OrderPaymentWayOilOut out = new OrderPaymentWayOilOut();
        out.setCostOil(costOil);
        out.setExpenseOil(expenseOil);
        out.setContractOil(contractOil);
        return out;
    }

    public OrderPaymentDaysInfo queryOrderPaymentDaysInfo(Long orderId, Integer paymentDaysType) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (paymentDaysType == null || paymentDaysType <= 0) {
            throw new BusinessException("???????????????????????????????????????");
        }
        OrderPaymentDaysInfo orderPaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, paymentDaysType);
        return orderPaymentDaysInfo;
    }

    /**
     * ???????????????????????????
     * ????????????????????????????????????
     *
     * @return
     * @throws Exception
     */
    public Long queryCustomOil(Long excludeOrderId, Long tenantId, LoginInfo user) {
        Long noPayCustomOilFee = orderFeeService.queryCustomOil(excludeOrderId, tenantId);
        if (noPayCustomOilFee == null) {
            noPayCustomOilFee = 0L;
        }
        //?????????????????????
        long customOilFee = oilCardManagementService.queryOilBalance(user.getUserInfoId(), tenantId, SysStaticDataEnum.USER_TYPE.ADMIN_USER);

        return customOilFee - noPayCustomOilFee;
    }

    /**
     * ?????????????????????????????????
     *
     * @param orderInfo
     * @param orderGoodsVer
     * @throws Exception
     */
    private void syncTransferOrderGoods(OrderInfo orderInfo, OrderGoodsVer orderGoodsVer) {
        if (orderInfo == null || orderGoodsVer == null) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        //????????????
        if (orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0) {
            OrderGoods toOrderGoods = orderGoodsService.getOrderGoods(orderInfo.getToOrderId());
            if (toOrderGoods == null) {
                OrderGoodsH toOrderGoodsH = orderGoodsHService.selectByOrderId(orderInfo.getToOrderId());
                if (toOrderGoodsH != null) {
                    toOrderGoodsH.setGoodsName(orderGoodsVer.getGoodsName());
                    toOrderGoodsH.setReciveName(orderGoodsVer.getReciveName());
                    toOrderGoodsH.setRecivePhone(orderGoodsVer.getRecivePhone());
                    toOrderGoodsH.setReciveAddr(orderGoodsVer.getReciveAddr());
                    toOrderGoodsH.setReciveProvinceId(orderGoodsVer.getReciveProvinceId());
                    toOrderGoodsH.setReciveCityId(orderGoodsVer.getReciveCityId());
                    orderGoodsHService.saveOrUpdate(toOrderGoodsH);
                } else {
                    throw new BusinessException("???????????????[" + orderInfo.getToOrderId() + "]???????????????????????????");
                }
            } else {
                toOrderGoods.setGoodsName(orderGoodsVer.getGoodsName());
                toOrderGoods.setReciveName(orderGoodsVer.getReciveName());
                toOrderGoods.setRecivePhone(orderGoodsVer.getRecivePhone());
                toOrderGoods.setReciveAddr(orderGoodsVer.getReciveAddr());
                toOrderGoods.setReciveProvinceId(orderGoodsVer.getReciveProvinceId());
                toOrderGoods.setReciveCityId(orderGoodsVer.getReciveCityId());
                orderGoodsService.saveOrUpdate(toOrderGoods);
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param tenantName
     * @param orderId
     * @param plateNumber
     * @param userPhone
     * @throws Exception
     */
    private void sendTempTenantSms(String tenantName, Long orderId, String plateNumber, String userPhone, String accessToken) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("tenantName", tenantName);
        paraMap.put("userPhone", userPhone);
        paraMap.put("plateNumber", plateNumber);
        sysSmsSendService.sendSms(userPhone, EnumConsts.SmsTemplate.ORDER_REASSIGN_TEMP, SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
                SysStaticDataEnum.OBJ_TYPE.ORDER_REASSIGN, String.valueOf(orderId), paraMap, accessToken);
    }


//***************************************  ?????????todo??????    *************************************************************/


//*************************************** ?????????todo??????     **************************************************************/

    /**
     * ????????????
     *
     * @param orderInfo
     * @param orderInfoExt
     * @param orderGoods
     * @param orderfee
     * @param orderFeeExt
     * @param orderScheduler
     * @param depotSchemes
     * @param costPaymentDaysInfo ????????????
     * @param orderOilCardInfos   ????????????
     * @param desc                ????????????
     * @return
     * @throws Exception
     */
    @Override
    @GlobalTransactional(timeoutMills = 300000, rollbackFor = Exception.class)
    public String orderInfoReassign(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderGoods orderGoods, OrderFee orderfee,
                                    OrderFeeExt orderFeeExt, OrderScheduler orderScheduler, List<OrderOilDepotScheme> depotSchemes, OrderPaymentDaysInfo costPaymentDaysInfo,
                                    List<OrderOilCardInfo> orderOilCardInfos, String desc, LoginInfo loginInfo, String token) {
        try {
            ObjectCompareUtils.isNotBlankNamesMap(orderScheduler,
                    getAddOrderSchedulerCheckNoBlank(orderScheduler.getVehicleClass(), orderScheduler.getAppointWay()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ResponseCode.PARAM_IS_BLANK);
        }
        if (orderInfo.getOrderId() == null || orderInfo.getOrderId() <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());
//????????????????????????
        this.checkBasicsOrderInfo(orderInfo, orderfee, orderScheduler, orderInfoExt, orderGoods, orderFeeExt, transitLineInfos, false, loginInfo);
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
//????????????????????????
        this.checkPaymentDays(costPaymentDaysInfo, orderInfo, orderScheduler, orderfee);
// A?????????B???B?????????A???
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (orderInfo.getTenantId().longValue() == orderInfo.getToTenantId().longValue()) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }

            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                // ?????????????????????????????????????????????
                OrderInfo preOrderInfo = selectByOrderId(orderInfo.getFromOrderId());
//                OrderInfo preOrderInfo = orderInfoSV.getOrder(orderInfo.getFromOrderId());
                if (preOrderInfo.getTenantId().longValue() == orderInfo.getToTenantId().longValue()) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                }
            }
        }
        List<OrderTransferInfo> transferInfos = orderTransferInfoService.queryTransferInfoList(OrderConsts.TransferOrderState.BILL_YES,
                null, orderInfo.getOrderId());
        if (transferInfos != null && transferInfos.size() > 0) {
            throw new BusinessException("??????????????????,????????????!");
        }

        OrderInfo preOrderInfo = selectByOrderId(orderInfo.getOrderId());
        if (preOrderInfo == null) {
            throw new BusinessException("?????????????????????[" + orderInfo.getOrderId() + "]??????????????????");
        }
        //??????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());

        orderInfo.setOrderType(preOrderInfo.getOrderType());
        orderInfo.setTenantName(sysTenantDef.getName());
        if (loginInfo.getOrgIds() != null && loginInfo.getOrgIds().size() > 0) {
            orderInfo.setOpOrgId(loginInfo.getOrgIds().get(0));
        }
        orderInfo.setOpName(loginInfo.getName());

        // ????????????????????????
        orderOilDepotSchemeService.deleteOrderOilDepotSchem(orderInfo.getOrderId());

        orderInfo.setIsTransit(OrderConsts.IsTransit.TRANSIT_NO);
        SysOperLogConst.OperType operType = SysOperLogConst.OperType.Update;
        //??????????????????
        costPaymentDaysInfo.setOrderId(orderInfo.getOrderId());
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
        costPaymentDaysInfo.setTenantId(orderInfo.getTenantId());
        orderPaymentDaysInfoService.saveOrUpdateOrderPaymentDaysInfo(costPaymentDaysInfo);

// ??????????????????
        boolean isLog = setOrderInfoDispatch(operType, desc, orderInfo, orderInfoExt, orderGoods, orderfee, orderFeeExt,
                orderScheduler, depotSchemes, orderOilCardInfos, null, loginInfo, token);
        //???????????????????????????
        if (orderInfo.getOrderType() != null && orderInfo.getOrderType() == OrderConsts.OrderType.ONLINE_RECIVE) {
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                //????????????????????????
                orderTransferInfoService.syncOrderVehicleInfo(orderInfo.getFromOrderId(), orderInfo, orderScheduler);
                orderTransferInfoService.updateOrderTransferPlateNumber(orderInfo.getFromOrderId(), orderScheduler.getPlateNumber(), loginInfo);
            }
        }
        if (!isLog) {
//            sysOperLogService.save(com.youming.youche.system.constant.SysOperLogConst.BusiCode.OrderInfo,orderInfo.getOrderId(),operType,
//                    "[" + loginInfo.getName() + "]" + desc + "??????",loginInfo);
//            sysOperLogSV.saveSysOperLog(com.youming.youche.system.constant..BusiCode.OrderInfo, orderInfo.getOrderId(), operType,
//                    "[" + baseUser.getUserName() + "]" + desc + "??????");SysOperLogConst
        }

        orderInfo.setUpdateTime(LocalDateTime.now());
        orderInfo.setUpdateOpId(loginInfo.getId());
        orderInfo.setOpId(loginInfo.getId());
        OrderInfo orderInfo1 = this.selectByOrderId(orderInfo.getOrderId());
        orderInfo.setId(orderInfo1.getId());

        orderGoods.setUpdateTime(LocalDateTime.now());
        orderGoods.setUpdateOpId(loginInfo.getId());
        orderGoods.setOpId(loginInfo.getId());

        orderfee.setUpdateTime(LocalDateTime.now());
        orderfee.setUpdateOpId(loginInfo.getId());
        orderfee.setOpId(loginInfo.getId());

        orderInfoExt.setUpdateTime(LocalDateTime.now());
        orderInfoExt.setUpdateOpId(loginInfo.getId());
        orderInfoExt.setOpId(loginInfo.getId());

        orderFeeExt.setUpdateTime(LocalDateTime.now());
        orderFeeExt.setUpdateOpId(loginInfo.getId());
        orderFeeExt.setOpId(loginInfo.getId());

        orderScheduler.setUpdateTime(LocalDateTime.now());
        orderScheduler.setUpdateOpId(loginInfo.getId());
        orderScheduler.setOpId(loginInfo.getId());

        super.saveOrUpdate(orderInfo);
		/*// ??????????????????(?????? ?????????????????? capital_channel) ?????????????????????
		orderfee.setVehicleAffiliation(orderInfo.getIsNeedBill().toString());*/

        orderfee.setFinalFeeFlag(com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderfee.setPayoutStatus(com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderfee.setArrivePaymentState(com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderfee.setVerificationState(com.youming.youche.order.commons.OrderConsts.PayOutVerificationState.INIT);
        orderFeeService.saveOrUpdate(orderfee);

        orderGoods.setReciveState(com.youming.youche.order.commons.OrderConsts.ReciveState.NOT_UPLOAD);
        orderGoods.setLoadState(com.youming.youche.order.commons.OrderConsts.ReciveState.NOT_UPLOAD);
        orderGoodsService.saveOrUpdate(orderGoods);

        orderInfoExt.setPreAmountFlag(com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderInfoExtService.saveOrUpdate(orderInfoExt);

        orderFeeExtService.saveOrUpdate(orderFeeExt);

        orderSchedulerService.saveOrUpdate(orderScheduler);
        boolean reuslt = false;
        boolean isAudit = false;
        boolean isUpdateAudit = false;
        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            //?????????????????????????????????????????? true??????  false?????????
            boolean isBindOtherVehicle = vehicleDataInfoService.isBindOtherVehicle(orderScheduler.getCarDriverId(), orderScheduler.getPlateNumber(), loginInfo);
            if (!isBindOtherVehicle) {
                //??????????????????
                reuslt = startProcess(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo.getOrderType(), true,
                        orderInfo.getOrderId(), token, loginInfo);
            } else {
                //???????????????????????????
                isUpdateAudit = true;
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("busiId", orderInfo.getOrderId());
                isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, orderInfo.getOrderId(),
                        SysOperLogConst.BusiCode.OrderInfo, params, token);
            }
        } else if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant() != OrderConsts.IS_TEMP_TENANT.YES) {
            //??????????????????????????????????????????
            reuslt = startProcess(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo.getOrderType(), true,
                    orderInfo.getOrderId(), token, loginInfo);
        }
        //???????????????
        if (isUpdateAudit) {
            auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE,
                    orderInfo.getOrderId(), orderInfo.getTenantId());
            if (!isAudit) {
                //??????????????????
                this.orderUpdateAuditPass(orderInfo.getOrderId(), "", isAudit, loginInfo, token);
            } else {
                // ???????????????????????????????????????????????????????????????
                orderInfo.setOrderState(com.youming.youche.order.commons.OrderConsts.ORDER_STATE.TO_BE_AUDIT);
                //??????????????????????????????
                orderInfo.setOrderUpdateState(OrderConsts.UPDATE_STATE.UPDATE_SPECIAL);
                this.saveOrUpdate(orderInfo);
            }
        } else {
            if (!reuslt) {
                auditOutService.cancelProcess(com.youming.youche.record.common.AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
                // ??????????????????
                auditPriceSuccess(orderInfo, orderInfoExt, orderfee, orderScheduler, orderGoods, orderFeeExt, true,
                        orderInfo.getTenantId(), orderInfo.getOrderId(), loginInfo, token);
            } else {
                // ???????????????????????????????????????????????????????????????
                orderInfo.setOrderState(com.youming.youche.order.commons.OrderConsts.ORDER_STATE.TO_BE_AUDIT);
                this.saveOrUpdate(orderInfo);
            }
        }
        //????????????
        this.handleWorkOrder(orderInfo.getWorkId(), orderInfo.getOrderId(), loginInfo);

        return "Y";
    }


    //    @Override
    public OrderInfo selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderInfo::getOrderId, orderId).last("LIMIT 1");
        return baseMapper.selectOne(wrapper);
    }


//*************************************** ?????????todo??????     *************************************************************/


//*************************************** ?????????todo??????    *************************************************************/


    @Override
    public String updateOrderInfoByPortionTwo(OrderInfo orderInfo, OrderFee orderFee, OrderGoods orderGoods, OrderInfoExt orderInfoExt, OrderFeeExt orderFeeExt,
                                              OrderScheduler orderScheduler, List<OrderOilDepotScheme> depotSchemes, Integer updateType, List<String> oilCardNums,
                                              OrderPaymentDaysInfo costPaymentDaysInfo, OrderPaymentDaysInfo incomePaymentDaysInfo,
                                              List<OrderOilCardInfo> oilCardInfos, List<OrderTransitLineInfo> transitLineInfos, LoginInfo baseUser, String accessToken) {

        if (orderInfo == null
                || (orderInfo.getOrderId() == null || orderInfo.getOrderId() <= 0)) {
            throw new BusinessException("??????????????????????????????????????????");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "updateOrderInfoByPortion" + orderInfo.getOrderId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        int updateTypeOld = 0;
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
        incomePaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        this.initOrderPaymentDaysInfo(costPaymentDaysInfo);
        this.initOrderPaymentDaysInfo(incomePaymentDaysInfo);
        OrderInfo orderInfoOld = orderInfoService.getOrder(orderInfo.getOrderId());
        OrderScheduler orderSchedulerOld = orderSchedulerService.getOrderScheduler(orderInfo.getOrderId());
        OrderInfoExt orderInfoExtOld = orderInfoExtService.getOrderInfoExt(orderInfo.getOrderId());
        OrderFee orderFeeOld = orderFeeService.getOrderFee(orderInfo.getOrderId());
        OrderFeeExt orderFeeExtOld = orderFeeExtService.getOrderFeeExt(orderInfo.getOrderId());
        OrderGoods orderGoodsOld = orderGoodsService.getOrderGoods(orderInfo.getOrderId());

        OrderPaymentDaysInfo costPaymentDaysInfoOld = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        OrderPaymentDaysInfo incomePaymentDaysInfoOld = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);

        if (orderInfoExtOld != null) {
            if (!ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[]{"preAmountFlag"})) {//?????????????????????????????????
                throw new BusinessException("???????????????????????????????????????????????????????????????");
            }
        } else {
            throw new BusinessException("???????????????[" + orderInfo.getOrderId() + "]???????????????????????????");
        }

        if (orderInfoOld.getToTenantId() != null && orderInfoOld.getToTenantId() > 0) {//??????
            updateTypeOld = OrderConsts.UPDATE_TYPE.IS_TRANSFER_ORDER;
            if (orderInfoOld.getFromTenantId() != null && orderInfoOld.getFromTenantId() > 0) {//????????????
                updateTypeOld = OrderConsts.UPDATE_TYPE.MIDDLE_ORDER;
            }
        } else {
            if (orderInfoOld.getFromTenantId() != null && orderInfoOld.getFromTenantId() > 0) {//????????????
                updateTypeOld = OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER;
            } else {//?????????
                updateTypeOld = OrderConsts.UPDATE_TYPE.OWN_ORDER;
            }
        }

        if (updateTypeOld != updateType) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????");
        }
        //????????????????????????
        if (orderInfo.getIsNeedBill() == null) {
            orderInfo.setIsNeedBill(orderInfoOld.getIsNeedBill());
        }
        if (orderInfoExt.getPaymentWay() == null) {
            orderInfoExt.setPaymentWay(orderInfoExtOld.getPaymentWay());
        }
        if (orderFee.getAfterPayAcctType() == null) {
            orderFee.setAfterPayAcctType(orderFeeOld.getAfterPayAcctType());
        }
        if (orderFee.getPrePayEquivalenceCardType() == null) {
            orderFee.setPrePayEquivalenceCardType(orderFeeOld.getPrePayEquivalenceCardType());
        }
        if (orderFee.getAfterPayEquivalenceCardType() == null) {
            orderFee.setAfterPayEquivalenceCardType(orderFeeOld.getAfterPayEquivalenceCardType());
        }
        if (orderScheduler.getIsUrgent() == null) {
            orderScheduler.setIsUrgent(orderSchedulerOld.getIsUrgent());
        }
        if (orderScheduler.getVehicleClass() == null) {
            orderScheduler.setVehicleClass(orderSchedulerOld.getVehicleClass());
        }
        //??????????????????
        boolean isUpdateSchedulerLine = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.LINE_PROERTY);
        boolean isUpdateGoodsLine = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.LINE_PROERTY);
        boolean isUpdateOrderInfoLine = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, OrderConsts.UPDATE_COMPARE_PROERTY.LINE_PROERTY);
        //??????????????????
        boolean isUpdateGoods = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.GOODS_PROERTY);
        //??????????????????
        boolean isUpdateRecive = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.RECIVE_PROERTY);
        //??????????????????
        boolean isUpdateIncomeFee = !ObjectCompareUtils.isModifyObj(orderFeeOld, orderFee, OrderConsts.UPDATE_COMPARE_PROERTY.INCOME_PROERTY);
        boolean isUpdateIncomeFeeExt = !ObjectCompareUtils.isModifyObj(orderFeeExtOld, orderFeeExt, OrderConsts.UPDATE_COMPARE_PROERTY.INCOME_PROERTY);
        boolean isUpdateIncomeScheduler = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.INCOME_PROERTY_SCHEDULER);
        boolean isUpdateIncomePaymentDays = false;
        if (orderInfoOld.getOrderType() != null && orderInfoOld.getOrderType().intValue() != OrderConsts.OrderType.ONLINE_RECIVE) {
            isUpdateIncomePaymentDays = !ObjectCompareUtils.isModifyObj(incomePaymentDaysInfoOld, incomePaymentDaysInfo, OrderConsts.UPDATE_COMPARE_PROERTY.PAYMENT_DAYS_PROERTY);
        }

        //??????????????????
        boolean isUpdateScheduler = !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, OrderConsts.UPDATE_COMPARE_PROERTY.SCHEDULER_PROERTY);
        boolean isUpdateSchedulerOrg = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, OrderConsts.UPDATE_COMPARE_PROERTY.SCHEDULER_PROERTY);
        boolean isUpdateSchedulerGoods = !ObjectCompareUtils.isModifyObj(orderGoodsOld, orderGoods, OrderConsts.UPDATE_COMPARE_PROERTY.SCHEDULER_PROERTY);

        //??????????????????
        boolean isUpdateCostFee = !ObjectCompareUtils.isModifyObj(orderFeeOld, orderFee, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostFeeExt = !ObjectCompareUtils.isModifyObj(orderFeeExtOld, orderFeeExt, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostInfo = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostScheduler = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostPaymentDays = !ObjectCompareUtils.isModifyObj(costPaymentDaysInfoOld, costPaymentDaysInfo, OrderConsts.UPDATE_COMPARE_PROERTY.PAYMENT_DAYS_PROERTY);
        boolean isUpdateCostInfoExt = !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, OrderConsts.UPDATE_COMPARE_PROERTY.COST_PROERTY);
        boolean isUpdateCostOilCard = false;
        //??????????????????
        boolean isUpdateBackhaul = !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, OrderConsts.UPDATE_COMPARE_PROERTY.BAACKHAUL_PROERTY);
        //??????????????????
        boolean isUpdateRamark = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, new String[]{"remark"});
        //??????????????????
        boolean isUpdateDriver = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.DRIVER_PROERTY);
        boolean isUpdateScheme = false;
        //??????????????????
        boolean isUpdatePlateNumber = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.PLATE_NUMBER_PROERTY);
        boolean isUpdateBill = !ObjectCompareUtils.isModifyObj(orderInfoOld, orderInfo, new String[]{"isNeedBill"});
        if (depotSchemes != null && depotSchemes.size() > 0) {
            for (int i = depotSchemes.size() - 1; i >= 0; i--) {
                OrderOilDepotScheme scheme = depotSchemes.get(i);
                scheme.setOrderId(orderInfo.getOrderId());
                if (scheme.getOilDepotId() == null || scheme.getOilDepotId() <= 0) {
                    depotSchemes.remove(scheme);
                }
            }
        }

        //??????????????????????????????
        if (orderSchedulerOld.getVehicleClass() != null
                && orderSchedulerOld.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            List<OrderOilDepotScheme> schemes = orderOilDepotSchemeService.getOrderOilDepotSchemeByOrderId(orderInfo.getOrderId(), false, baseUser);
            if (depotSchemes != null && depotSchemes.size() > 0) {
                if (schemes != null && schemes.size() > 0) {
                    if (schemes.size() != depotSchemes.size()) {
                        isUpdateScheme = true;
                    } else {
                        for (int i = 0; i < schemes.size(); i++) {
                            OrderOilDepotScheme scheme = schemes.get(i);
                            OrderOilDepotScheme scheme2 = depotSchemes.get(i);
                            if (!ObjectCompareUtils.isModifyObj(scheme, scheme2, new String[]{"oilDepotId", "oilDepotName", "oilDepotFee", "oilDepotLitre", "dependDistance", "oilDepotPrice"})) {
                                isUpdateScheme = true;
                            }
                        }
                    }
                } else {
                    isUpdateScheme = true;
                }
            } else {
                if (schemes != null && schemes.size() > 0) {
                    isUpdateScheme = true;
                }
            }
        }
        boolean isUpdateTransitLine = false;
        List<OrderTransitLineInfo> transitLineInfosOld = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            if (transitLineInfosOld != null && transitLineInfosOld.size() > 0) {
                if (transitLineInfos.size() != transitLineInfosOld.size()) {
                    isUpdateTransitLine = true;
                } else {
                    for (int i = 0; i < transitLineInfos.size(); i++) {
                        OrderTransitLineInfo lineInfo = transitLineInfos.get(i);
                        OrderTransitLineInfo lineInfoOld = transitLineInfosOld.get(i);
                        if (!ObjectCompareUtils.isModifyObj(lineInfo, lineInfoOld, OrderConsts.UPDATE_COMPARE_PROERTY.TRANSIT_LINE_PROERTY)) {
                            isUpdateTransitLine = true;
                        }
                    }
                }
            } else {
                isUpdateTransitLine = true;
            }
        } else {
            if (transitLineInfosOld != null && transitLineInfosOld.size() > 0) {
                isUpdateTransitLine = true;
            }
        }

        // ???????????????????????????
        ////???????????????????????????
        //if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
        //        && orderInfoExt.getOilUseType() != null && orderInfoExt.getOilUseType() == OrderConsts.OIL_USE_TYPE.CUSTOMOIL) {
        //    long customOilFee = orderFeeService.queryCustomOil(orderInfoExt.getOrderId(), baseUser);
        //    if (orderFee.getPreOilVirtualFee() != null && customOilFee < orderFee.getPreOilVirtualFee()) {
        //        throw new BusinessException("??????????????????????????????????????????????????????");
        //    }
        //}

        //????????????????????????????????????
        if ((orderInfoExtOld.getPaymentWay() != null && orderInfoExtOld.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)
                || (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)) {
            List<OrderOilCardInfo> oilCardInfosOld = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderInfo.getOrderId(), null);
            if (oilCardInfos != null && oilCardInfos.size() > 0) {
                if (oilCardInfosOld == null || oilCardInfos.size() != oilCardInfosOld.size()) {
                    isUpdateCostOilCard = true;
                } else {
                    for (int i = 0; i < oilCardInfos.size(); i++) {
                        OrderOilCardInfo orderOilCardInfo = oilCardInfos.get(i);
                        OrderOilCardInfo orderOilCardInfoOld = oilCardInfosOld.get(i);
                        if (orderOilCardInfo.getOilFee() != null && orderOilCardInfo.getOilFee() > 0) {
                            isUpdateCostOilCard = true;
                            break;
                        }
                        if (!Objects.equals(orderOilCardInfo.getOilCardNum(), orderOilCardInfoOld.getOilCardNum())) {
                            isUpdateCostOilCard = true;
                        }
                    }
                }
            } else if (oilCardInfosOld != null && oilCardInfosOld.size() > 0) {
                isUpdateCostOilCard = true;
            }
        }

        /**??????????????????*/
        boolean isUpdateCollection = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, OrderConsts.UPDATE_COMPARE_PROERTY.COLLECTION_PROERTY);
        /**??????????????????**/
        boolean isUpdateSchedulerTime = !ObjectCompareUtils.isModifyObj(orderSchedulerOld,
                orderScheduler, new String[]{"distance", "dependTime", "mileageNumber"});

        if (!(isUpdateSchedulerLine || isUpdateSchedulerTime || isUpdateGoodsLine || isUpdateOrderInfoLine || isUpdateGoods || isUpdateRecive || isUpdateIncomeFee || isUpdateIncomeFeeExt || isUpdateIncomeScheduler
                || isUpdateScheduler || isUpdateSchedulerOrg || isUpdateCostFee || isUpdateCostFeeExt || isUpdateCostScheduler || isUpdateCostInfo
                || isUpdateBackhaul || isUpdateRamark || isUpdateScheme || isUpdateDriver || isUpdateIncomePaymentDays || isUpdateCostPaymentDays
                || isUpdateCostInfoExt || isUpdateCostOilCard || isUpdateTransitLine || isUpdatePlateNumber || isUpdateCollection || isUpdateBill || isUpdateSchedulerGoods)) {
            throw new BusinessException("????????????????????????");
        }
        if (updateType == OrderConsts.UPDATE_TYPE.OWN_ORDER) {

        } else if (updateType == OrderConsts.UPDATE_TYPE.IS_TRANSFER_ORDER && orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0) {
            if (isUpdateSchedulerLine || isUpdateGoodsLine || isUpdateOrderInfoLine || isUpdateTransitLine) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateCostFee || isUpdateCostFeeExt || isUpdateCostInfo || isUpdateCostPaymentDays || isUpdateCostInfoExt || isUpdateCostOilCard) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateBackhaul) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdatePlateNumber) {
                throw new BusinessException("????????????????????????????????????");
            }
        } else if (updateType == OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER) {
            if (isUpdateSchedulerLine || isUpdateGoodsLine || isUpdateOrderInfoLine || isUpdateTransitLine) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateGoods) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateRecive) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateIncomeFeeExt || isUpdateIncomeScheduler || isUpdateIncomePaymentDays) {
                log.error("????????????????????????????????????isUpdateIncomeFee=" + isUpdateIncomeFee + " isUpdateIncomeFeeExt=" + isUpdateIncomeFeeExt +
                        " isUpdateIncomeScheduler =" + isUpdateIncomeScheduler + " isUpdateIncomePaymentDays" + isUpdateIncomePaymentDays);
                throw new BusinessException("????????????????????????????????????");
            }
            if (isUpdateBackhaul) {
                throw new BusinessException("????????????????????????????????????");
            }
        } else if (updateType == OrderConsts.UPDATE_TYPE.MIDDLE_ORDER) {
            throw new BusinessException("????????????????????????");
        }
        if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"arriveTime"})) {
            if (updateType != OrderConsts.UPDATE_TYPE.OWN_ORDER) {
                throw new BusinessException("????????????????????????????????????");
            }
        }
        //boolean isUpdateOilBill =  !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[] {"oilIsNeedBill"});
        if (orderInfoExtOld.getFirstPayFlag() != null && orderInfoExtOld.getFirstPayFlag().intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            if (isUpdateBill) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
        }

        if (isUpdateCollection) {
            boolean orderCostPermission = sysRoleService.hasOrderCostPermission(baseUser);
            if (!orderCostPermission) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
            List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),
                    baseUser.getTenantId());
            if (list != null && list.size() > 0) {
                for (OrderProblemInfo problemInfo : list) {
                    if (problemInfo.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.CANCEL
                            && problemInfo.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN) {
                        throw new BusinessException("??????????????????????????????????????????");
                    }
                }
            }
            if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                //??????????????? ?????????????????????
                if (StringUtils.isBlank(orderScheduler.getCollectionUserPhone())) {
                    throw new BusinessException("??????????????????????????????");
                }
                if (StringUtils.isBlank(orderScheduler.getCollectionName())) {
                    throw new BusinessException("??????????????????????????????");
                }
                UserDataInfo userDataInfo = userDataInfoService.getPhone(orderScheduler.getCollectionUserPhone());
                if (userDataInfo != null) {
                    if (orderScheduler.getCarDriverId().equals(orderScheduler.getCollectionUserId())) {
                        throw new BusinessException("?????????????????????????????????");
                    }
                }
            }
        }

        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && !ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[]{"paymentWay"})) {
            if (updateType != OrderConsts.UPDATE_TYPE.OWN_ORDER
                    && updateType != OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER) {
                throw new BusinessException("????????????????????????????????????");
            }

            OrderPaymentWayOilOut out = this.getOrderPaymentWayOil(orderScheduler.getCarDriverId(), baseUser.getTenantId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER);

            if (orderInfoExtOld.getPaymentWay() != null && orderInfoExtOld.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE
                    && orderInfoExt.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                //????????????????????????
                boolean bool = orderSchedulerService.checkPayMentWaySwitchover(orderScheduler.getPlateNumber()
                        , orderScheduler.getDependTime(), orderScheduler.getTenantId(), orderScheduler.getOrderId());
                if (!bool) {
                    throw new BusinessException("???????????????????????????????????????");
                }
                if (out.getExpenseOil() != null && out.getExpenseOil() > 0) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                }
            } else if (orderInfoExtOld.getPaymentWay() != null && orderInfoExtOld.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE
                    && orderInfoExt.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                if ((out.getCostOil() != null && out.getCostOil() > 0) || (out.getContractOil() != null && out.getContractOil() > 0)) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                }
            } else if ((orderInfoExtOld.getPaymentWay() == null || orderInfoExtOld.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.EXPENSE)
                    && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                if (out.getExpenseOil() != null && out.getExpenseOil() > 0) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                }
            }
        }

        boolean isUpdateVehicle = !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"vehicleCode", "plateNumber"});
        if (isUpdateVehicle && orderSchedulerOld.getIsCollection() != null && orderSchedulerOld.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
            throw new BusinessException("???????????????????????????????????????");
        }

        if (isUpdateDriver) {//??????????????????????????????????????????
            //????????????????????????????????????????????????
            if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"carDriverId", "carDriverMan", "carDriverPhone"})) {
                /**
                 * ?????????????????????????????????????????????????????????????????????????????????????????????
                 */
				/*orderSchedulerTF.checkLineIsOk(null, orderScheduler.getCarDriverId(),
						null, orderScheduler.getDependTime(), orderInfo.getFromOrderId(), orderInfo.getOrderId());*/
                this.isExistOtherFee(orderScheduler.getOrderId());
            }
            if (orderSchedulerOld.getCopilotUserId() != null && orderSchedulerOld.getCopilotUserId() > 0 && !ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"copilotMan", "copilotPhone", "copilotUserId"})) {
                this.isExistOtherFee(orderScheduler.getOrderId());
            }
            if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
                if (orderScheduler.getCarDriverId().longValue() == orderScheduler.getCopilotUserId()
                        .longValue()) {
                    throw new BusinessException("??????????????????????????????????????????????????????");
                }
            }
        }

        if (isUpdateIncomePaymentDays) {
            if (orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
                this.checkPaymentDays(incomePaymentDaysInfo, orderInfo, orderScheduler, orderFee);
            }
        }
        if (isUpdateCostPaymentDays) {//????????????????????????
            this.checkPaymentDays(costPaymentDaysInfo, orderInfo, orderScheduler, orderFee);
        }

        //??????????????????????????????????????????
        orderOilCardInfoVerService.releaseOilCardBalance(orderFee.getOrderId(), baseUser.getTenantId());

        //?????????????????????????????????????????????
        orderOilDepotSchemeVerService.updateSchemeVerIsUpdate(orderFee.getOrderId(), OrderConsts.IS_UPDATE.NOT_UPDATE, baseUser);
        //???????????????????????????????????????
        orderPaymentDaysInfoVerService.loseEfficacyPaymentDaysVerUpdate(orderFee.getOrderId());
        //???????????????????????????
        orderTransitLineInfoVerService.updateTransitLineInfoVerType(orderFee.getOrderId(), OrderConsts.IS_UPDATE.NOT_UPDATE, OrderConsts.IS_UPDATE.UPDATE);
        //???????????????????????????????????????????????????
        orderDriverSubsidyService.updateDriverSubsidyVer(orderFee.getOrderId(), null, null, 1);

        if (orderInfoExtOld.getPreAmountFlag() == com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            if (isUpdateCollection && orderInfoOld.getToTenantId() != null && orderInfoOld.getToTenantId() > 0) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!ObjectCompareUtils.isModifyObj(orderFeeOld, orderFee, new String[]{"prePayEquivalenceCardNumber", "afterPayEquivalenceCardNumber"})) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"appointWay"})) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!ObjectCompareUtils.isModifyObj(orderInfoExtOld, orderInfoExt, new String[]{"paymentWay"})) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (isUpdateDriver) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if ((orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE))) {
                if ((orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee()) < (orderFeeOld.getPreOilVirtualFee() == null ? 0 : orderFeeOld.getPreOilVirtualFee())) {
                    throw new BusinessException("???????????????????????????????????????????????????????????????");
                }
            }

            if (isUpdateIncomePaymentDays) {
                if (incomePaymentDaysInfoOld.getBalanceType() != null
                        && incomePaymentDaysInfoOld.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL) {
                    if (!ObjectCompareUtils.isModifyObj(incomePaymentDaysInfoOld, incomePaymentDaysInfo, new String[]{"balanceType"})) {
                        throw new BusinessException("??????????????????????????????????????????????????????");
                    }
                }
            }
            if (isUpdateCostPaymentDays) {//????????????????????????
                if (costPaymentDaysInfoOld.getBalanceType() != null
                        && costPaymentDaysInfoOld.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL) {
                    if (!ObjectCompareUtils.isModifyObj(costPaymentDaysInfoOld, costPaymentDaysInfo, new String[]{"balanceType"})) {
                        throw new BusinessException("??????????????????????????????????????????????????????");
                    }
                }
            }

            //???????????????????????????????????????
            if (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE) {//????????????????????????????????????
                if (orderFeeOld.getPreOilFee() < orderFee.getPreOilFee()) {//??????????????????
                    if (oilCardNums == null || oilCardNums.size() == 0) {
                        throw new BusinessException("????????????????????????");
                    }
                    Long oilFee = orderFee.getPreOilFee() - orderFeeOld.getPreOilFee();
                    //????????????
                    orderFeeService.verifyOilCardNum(orderFee.getOrderId(), oilCardNums, false, oilFee, accessToken);
                    //??????????????????
                    int service = 0;
                    for (String oilCardNum : oilCardNums) {
                        if (StringUtils.isBlank(oilCardNum))
                            throw new BusinessException("??????????????????");
                        List<OilCardManagement> list = oilCardManagementService.findByOilCardNum(oilCardNum, orderFeeOld.getTenantId());
                        if (list == null || list.size() <= 0)
                            throw new BusinessException("????????????[" + oilCardNum + "]?????????!");

                        OilCardManagement oilCard = list.get(0);
                        //?????????????????????????????????
                        int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
                        Long balance = 0L;
                        Long oilCardFee = oilCard.getCardBalance() != null ? oilCard.getCardBalance() : 0L;

                        //???????????????????????????????????????
                        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            if (service == 1) {
                                throw new BusinessException("??????????????????????????????,??????[" + oilCardNum + "]!");
                            }
                            service++;
                            balance = oilFee;
                        } else {
                            //???????????????????????? ??????????????????????????????
                            if (oilFee <= oilCardFee) {
                                balance = oilFee;
                            } else {//???????????????????????? ????????????????????? ????????????
                                balance = oilCardFee;
                            }
                        }
                        oilFee = oilFee - balance;
                        //????????????????????????(????????????/????????????)
                        if (cardType != SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            oilCardManagementService.pledgeOilCardBalance(oilCardNum, balance, baseUser.getTenantId(), orderInfoOld.getToTenantId(), false);
                        }
                        OrderOilCardInfoVer orderOilCardInfo = new OrderOilCardInfoVer();
                        orderOilCardInfo.setCreateTime(LocalDateTime.now());
                        orderOilCardInfo.setOilCardNum(oilCardNum);
                        orderOilCardInfo.setOilFee(balance);
                        orderOilCardInfo.setCardType(cardType);
                        orderOilCardInfo.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
                        orderOilCardInfo.setOrderId(orderInfo.getOrderId());
                        orderOilCardInfo.setTenantId(orderInfo.getTenantId());
                        orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfo);
                    }
                    if (oilFee != 0) {
                        throw new BusinessException("????????????????????????!");
                    }
                }
            }
        } else {
            if (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"appointWay"})
                    && orderInfoOld.getOrderState() != null && orderInfoOld.getOrderState() > OrderConsts.ORDER_STATE.TO_BE_LOAD
            ) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
        }

        //??????
        if (oilCardInfos != null && oilCardInfos.size() > 0) {
            for (OrderOilCardInfo orderOilCardInfo : oilCardInfos) {
                OrderOilCardInfoVer orderOilCardInfoVer = new OrderOilCardInfoVer();
                BeanUtil.copyProperties(orderOilCardInfo, orderOilCardInfoVer);
                orderOilCardInfoVer.setCreateTime(LocalDateTime.now());
                orderOilCardInfoVer.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
                orderOilCardInfoVer.setOrderId(orderInfo.getOrderId());
                orderOilCardInfoVer.setTenantId(orderInfo.getTenantId());
                orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfoVer);
            }
        }
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                OrderTransitLineInfoVer lineInfoVer = new OrderTransitLineInfoVer();
                BeanUtil.copyProperties(orderTransitLineInfo, lineInfoVer);
                lineInfoVer.setCreateTime(LocalDateTime.now());
                lineInfoVer.setIsUpdate(OrderConsts.IS_UPDATE.UPDATE);
                lineInfoVer.setOrderId(orderInfo.getOrderId());
                lineInfoVer.setTenantId(orderInfo.getTenantId());
                orderTransitLineInfoVerService.saveOrUpdate(lineInfoVer);
            }
        }

        if (isUpdateBill) {
            if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                // ???????????????,????????????????????????????????????????????????????????????????????????
                Long billMethod = billSettingService.getBillMethodByTenantId(orderInfo.getTenantId());
                if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    if (orderFee.getCostPrice() == null || orderFee.getCostPrice() == 0) {
                        throw new BusinessException("???????????????0???????????????????????????");
                    }
                }
                if (billMethod == null || billMethod == 0) {
                    String customerPhone = readisUtil.getSysCfg("CUSTOMER_SERVICE_PHONE", "0").getCfgValue();
                    throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" + customerPhone);
                }
                BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(billMethod);
                if (billPlatform == null) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                }
                orderFee.setVehicleAffiliation(billMethod.toString());
            } else {
                orderFee.setVehicleAffiliation(orderInfo.getIsNeedBill() == null ? null : orderInfo.getIsNeedBill().toString());
            }
        }

        if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                && (!ObjectCompareUtils.isModifyObj(orderSchedulerOld, orderScheduler, new String[]{"dependTime"})
                || isUpdateBill)
        ) {
            //??????????????????
            orderSchedulerService.checkBillDependTime(orderInfo, orderScheduler, Long.parseLong(orderFee.getVehicleAffiliation()), baseUser);
        }

        //??????????????????
        orderInfoExtService.verifyOrderNeedBill(orderInfo, orderInfoExt, orderScheduler, orderFee, orderGoods, transitLineInfos, baseUser);

        //?????????????????????
        if (orderInfo.getIsNeedBill() != null
                && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL &&
                !(orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)) {
            orderInfoExt.setOilAffiliation(orderFee.getVehicleAffiliation());
        } else {
            orderInfoExt.setOilAffiliation("1");
        }

        if (orderSchedulerOld.getVehicleClass() != null
                && orderSchedulerOld.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == com.youming.youche.order.commons.OrderConsts.PAYMENT_WAY.COST) {//?????????????????????????????????????????????
            //????????????????????????
            this.setDriverSubsidy(orderScheduler, orderFeeExt, baseUser, true, transitLineInfos != null ? transitLineInfos.size() : 0, transitLineInfos);
        }

        if (orderInfoOld.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
            // ????????????????????????????????? ??????????????????????????? ???????????????????????????
            checkCostPriceSum(orderFee.getCostPrice(), orderFee.getPrePayCash(), orderFee.getAfterPayCash(),
                    orderFee.getPrePayEquivalenceCardAmount(), orderFee.getAfterPayEquivalenceCardAmount(), baseUser);
        }

        // ???????????????????????????
        if (orderSchedulerOld.getVehicleClass() != null
                && orderSchedulerOld.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST
                && orderFeeExt.getOilLitreVirtual() != null && orderFeeExt.getOilLitreVirtual() > 0) {
            if (depotSchemes != null && depotSchemes.size() > 0) {
                Long oilDepotLitre = 0L;
                for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                    if (orderOilDepotScheme.getOilDepotId() != null && orderOilDepotScheme.getOilDepotId() > 0) {
                        if (orderOilDepotScheme.getOilDepotLitre() == null || orderOilDepotScheme.getOilDepotLitre() <= 0) {
                            throw new BusinessException("???????????????????????????0!");
                        }
                        oilDepotLitre += orderOilDepotScheme.getOilDepotLitre();
                        orderOilDepotScheme.setOrderId(orderInfo.getOrderId());
                        OrderOilDepotSchemeVer depotSchemeVer = new OrderOilDepotSchemeVer();
                        BeanUtil.copyProperties(orderOilDepotScheme, depotSchemeVer);
                        depotSchemeVer.setIsUpdate(OrderConsts.IS_UPDATE.UPDATE);
                        orderOilDepotSchemeVerService.saveOrderOilDepotSchemeVer(depotSchemeVer);
                    }
                }
                if (!oilDepotLitre.equals(orderFeeExt.getOilLitreVirtual())) {
                    log.info("??????????????????=" + oilDepotLitre + ",???????????????=" + orderFeeExt.getOilLitreVirtual());
                    throw new BusinessException("????????????????????????????????????????????????");
                }
            }
        } else {
            if (depotSchemes != null && depotSchemes.size() > 0) {
                for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                    if (orderOilDepotScheme.getOilDepotId() != null && orderOilDepotScheme.getOilDepotId() > 0) {
                        if (orderOilDepotScheme.getOilDepotLitre() == null || orderOilDepotScheme.getOilDepotLitre() <= 0) {
                            throw new BusinessException("???????????????????????????0!");
                        }
                    }
                }
            }
        }

        if (isUpdateCostPaymentDays) {
            //?????????????????????
            orderPaymentDaysInfoVerService.setOrderPaymentDaysInfoVer(costPaymentDaysInfoOld, costPaymentDaysInfo);
        }

        if (isUpdateIncomePaymentDays) {
            //?????????????????????
            orderPaymentDaysInfoVerService.setOrderPaymentDaysInfoVer(incomePaymentDaysInfoOld, incomePaymentDaysInfo);
        }

        //depotSchemes
        //????????????????????????
        this.checkBasicsOrderInfo(orderInfo, orderFee, orderScheduler, orderInfoExt, orderGoods, orderFeeExt, transitLineInfos, true, baseUser);

        //???????????????
        checkOilCarType(orderFee, baseUser.getTenantId());

        //??????????????????????????????
        /*checkPinganBankInfo(baseUser.getTenantId(),orderInfo.getIsNeedBill());*/
        OrderInfoVer orderInfoVerOld = orderInfoVerService.getOrderInfoVer(orderInfo.getOrderId());
        if (orderInfoVerOld == null) {//???????????????????????????
            orderInfoVerOld = new OrderInfoVer();
            OrderInfoExtVer orderInfoExtVerOld = new OrderInfoExtVer();
            OrderFeeVer orderFeeVerOld = new OrderFeeVer();
            OrderFeeExtVer orderFeeExtVerOld = new OrderFeeExtVer();
            OrderSchedulerVer orderSchedulerVerOld = new OrderSchedulerVer();
            OrderGoodsVer orderGoodsVerOld = new OrderGoodsVer();
            BeanUtil.copyProperties(orderInfoOld, orderInfoVerOld);
            BeanUtil.copyProperties(orderInfoExtOld, orderInfoExtVerOld);
            BeanUtil.copyProperties(orderFeeOld, orderFeeVerOld);
            BeanUtil.copyProperties(orderFeeExtOld, orderFeeExtVerOld);
            BeanUtil.copyProperties(orderGoodsOld, orderGoodsVerOld);
            BeanUtil.copyProperties(orderSchedulerOld, orderSchedulerVerOld);
            BeanUtil.copyProperties(orderGoodsOld, orderGoodsVerOld);
            orderInfoVerService.saveOrUpdate(orderInfoVerOld);
            orderInfoExtVerService.saveOrUpdate(orderInfoExtVerOld);
            orderFeeVerService.saveOrUpdate(orderFeeVerOld);
            orderFeeExtVerService.saveOrUpdate(orderFeeExtVerOld);
            orderSchedulerVerService.saveOrUpdate(orderSchedulerVerOld);
            orderGoodsVerService.saveOrUpdate(orderGoodsVerOld);
        }
        OrderInfoVer orderInfoVer = new OrderInfoVer();
        OrderInfoExtVer orderInfoExtVer = new OrderInfoExtVer();
        OrderFeeVer orderFeeVer = new OrderFeeVer();
        OrderFeeExtVer orderFeeExtVer = new OrderFeeExtVer();
        OrderSchedulerVer orderSchedulerVer = new OrderSchedulerVer();
        OrderGoodsVer orderGoodsVer = new OrderGoodsVer();

        BeanUtil.copyProperties(orderInfo, orderInfoVer);
        BeanUtil.copyProperties(orderInfoExt, orderInfoExtVer);
        BeanUtil.copyProperties(orderFee, orderFeeVer);
        BeanUtil.copyProperties(orderFeeExt, orderFeeExtVer);
        BeanUtil.copyProperties(orderGoods, orderGoodsVer);
        BeanUtil.copyProperties(orderScheduler, orderSchedulerVer);
        BeanUtil.copyProperties(orderGoods, orderGoodsVer);

        orderInfoVer.setId(orderInfoOld.getId());
        orderInfoExtVer.setId(orderInfoExtOld.getId());
        orderFeeExtVer.setId(orderFeeExtOld.getId());
        orderFeeVer.setId(orderFeeOld.getId());
        orderSchedulerVer.setId(orderSchedulerOld.getId());
        orderGoodsVer.setId(orderGoodsOld.getId());

        if (orderInfoOld.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_AUDIT
                && (orderInfoOld.getOrderUpdateState() != null && orderInfoOld.getOrderUpdateState().intValue() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {
            //????????????
        } else {
            orderInfoOld.setOrderUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
            orderInfoVer.setOrderUpdateState(OrderConsts.UPDATE_STATE.UPDATE_PORTION);
        }

        orderInfoExtOld.setUpdateType(updateType);
        orderInfoService.saveOrUpdate(orderInfoOld);
        orderInfoExtService.saveOrUpdate(orderInfoExtOld);

        orderInfoExtVer.setUpdateType(updateType);
        orderInfoVerService.saveOrUpdate(orderInfoVer);
        orderInfoExtVerService.saveOrUpdate(orderInfoExtVer);
        orderFeeVerService.saveOrUpdate(orderFeeVer);
        orderFeeExtVerService.saveOrUpdate(orderFeeExtVer);
        orderSchedulerVerService.saveOrUpdate(orderSchedulerVer);
        orderGoodsVerService.saveOrUpdate(orderGoodsVer);

        /**
         * ????????????
         */
        orderDriverSubsidyVerService.saveDriverSubsidyVer(orderInfoVer.getOrderId(), orderSchedulerVer.getCarDriverId()
                , orderFeeExtVer.getSubsidyTime(), orderFeeExtVer.getDriverDaySalary(), 1, orderInfoExt.getPreAmountFlag(), baseUser);
        //????????????
        orderDriverSubsidyVerService.saveDriverSubsidyVer(orderInfoVer.getOrderId(), orderSchedulerVer.getCopilotUserId()
                , orderFeeExtVer.getCopilotSubsidyTime(), orderFeeExtVer.getCopilotDaySalary(), 2, orderInfoExt.getPreAmountFlag(), baseUser);

        sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Update, "[" + baseUser.getName() + "]????????????(?????????)");

        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("busiId", orderInfoVer.getOrderId());
        boolean isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, orderInfoVer.getOrderId(),
                SysOperLogConst.BusiCode.OrderInfo, params, accessToken, baseUser.getTenantId());

        if (!isAudit) {// ?????????????????????
            this.orderUpdateAuditPass(orderInfo.getOrderId(), "", isAudit, baseUser, accessToken);
        }
        //????????????
        this.handleWorkOrder(orderInfo.getWorkId(), orderInfo.getOrderId(), baseUser);
        return "Y";

    }

    @Override
    @Transactional
    public Long saveOrUpdateOrderInfo(OrderInfo orderInfo, OrderFee orderfee, OrderGoods orderGoods,
                                      OrderInfoExt orderInfoExt, OrderFeeExt orderFeeExt, OrderScheduler orderScheduler,
                                      List<OrderOilDepotScheme> depotSchemes, OrderPaymentDaysInfo costPaymentDaysInfo,
                                      OrderPaymentDaysInfo incomePaymentDaysInfo, List<OrderOilCardInfo> orderOilCardInfos,
                                      List<OrderTransitLineInfo> transitLineInfos, Boolean isCopy, String accessToken, LoginInfo user) {

        // ??????????????????
        if (orderInfo == null) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        Boolean isUpdate = orderInfo.getOrderId() == null ? false : true;

        // ????????????
        objectCompareUtils.isNotBlankOrderInfo(orderInfo);

        objectCompareUtils.isNotBlankOrderGoods(orderGoods, orderInfo.getOrderType());

        objectCompareUtils.isNotBlankOrderScheduler(orderScheduler, orderScheduler.getAppointWay());

        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
            // ????????????????????????????????????????????????
            if (orderInfo.getTenantId() != null && orderInfo.getTenantId().longValue() == orderInfo.getToTenantId().longValue()) {
                throw new BusinessException("???????????????????????????");
            }
        }

        // ??????????????????
        // ???????????????COST_PRICE(?????????)=PRE_PAY_CASH(??????????????????)+AFTER_PAY_CASH(??????????????????)
        // +PRE_PAY_EQUIVALENCE_CARD_AMOUNT(?????????????????????)+AFTER_PAY_EQUIVALENCE_CARD_AMOUNT(?????????????????????)
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
        incomePaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        //this.initOrderPaymentDaysInfo(costPaymentDaysInfo);
        this.initOrderPaymentDaysInfo(incomePaymentDaysInfo);
        if (orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
            // ????????????????????????????????? ??????????????????????????? ???????????????????????????
            checkCostPriceSum(orderfee.getCostPrice(), orderfee.getPrePayCash(), orderfee.getAfterPayCash(),
                    orderfee.getPrePayEquivalenceCardAmount(), orderfee.getAfterPayEquivalenceCardAmount(), user);
            //????????????????????????
            this.checkPaymentDays(incomePaymentDaysInfo, orderInfo, orderScheduler, orderfee);
        }
        if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant() != OrderConsts.IS_TEMP_TENANT.YES) {
            //???????????????????????? ????????????????????????
            this.checkPaymentDays(costPaymentDaysInfo, orderInfo, orderScheduler, orderfee);
        }
        //????????????????????????
        this.checkBasicsOrderInfo(orderInfo, orderfee, orderScheduler, orderInfoExt, orderGoods, orderFeeExt, transitLineInfos, isUpdate, user);
        //???????????????
        checkOilCarType(orderfee, orderInfo.getTenantId());
		/*//??????????????? ??????????????????????????????????????????
		checkPinganBankInfo(orderInfo.getTenantId(),orderInfo.getIsNeedBill());*/
        long orderId = 0L;
        SysOperLogConst.OperType operType = SysOperLogConst.OperType.Add;
        String desc = orderInfo.getSourceFlag() != null && orderInfo.getSourceFlag() == SysStaticDataEnum.SOURCE_FLAG.IMPORT ? "??????" : "??????";
        if (!isUpdate) {// ????????????
            orderId = CommonUtil.zhCreateOrderId(user, 8);

            if (isCopy) {
                // ???????????????????????????????????????
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo, orderId, operType, "????????????????????????");
            }

        } else {
            operType = SysOperLogConst.OperType.Update;
            desc = "??????";
            orderId = orderInfo.getOrderId();
            List<OrderTransferInfo> transferInfos = orderTransferInfoService.queryTransferInfoList(null,
                    orderInfo.getTenantId(), orderId);
            for (OrderTransferInfo orderTransferInfo : transferInfos) {
                if (orderTransferInfo.getTransferOrderState() == OrderConsts.TransferOrderState.BILL_YES) {
                    throw new BusinessException("??????????????????,????????????????????????!");
                } else if (orderTransferInfo.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                    orderTransferInfo.setTransferOrderState(OrderConsts.TransferOrderState.BILL_NOT);
                    orderTransferInfoService.saveOrUpdate(orderTransferInfo);
                }
            }

            // ????????????????????????
            orderOilDepotSchemeService.deleteOrderOilDepotSchem(orderId);
            orderTransitLineInfoService.deleteOrderTransitLineInfo(orderId);
            orderOilCardInfoService.deleteOrderOilCardInfo(orderId);
        }
        if (OrderConsts.OrderType.TEMPORARY_LINE == orderInfo.getOrderType()) {
            // ????????????
            // ????????????????????????
            // ???????????????????????????????????????
            if (orderGoods.getCustType() != null && orderGoods.getCustType().intValue() == EnumConsts.CUSTOMER_TYPE.NO) {
                // ????????????
                // ????????????????????????
                SaveTmpOrderCustLineInfo(orderGoods, user);
            }
        }
        orderInfo.setIsTransit(OrderConsts.IsTransit.TRANSIT_NO);
        if (!isUpdate) {
            orderInfo.setOrderId(orderId);
            orderGoods.setOrderId(orderId);
            orderfee.setOrderId(orderId);
            orderInfoExt.setOrderId(orderId);
            orderFeeExt.setOrderId(orderId);
            orderScheduler.setOrderId(orderId);
            if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant() != OrderConsts.IS_TEMP_TENANT.YES) {
                //?????????????????????
                orderInfo.setTenantName(sysTenantDefService.getSysTenantDef(user.getTenantId()).getName());
                if (user.getOrgIds() != null && user.getOrgIds().size() > 0) {
                    orderInfo.setOpOrgId(user.getOrgIds().get(0));
                }
                orderInfo.setOpName(user.getName());
            }
        } else {
            //???????????????????????? ??????????????????
            orderInfo.setUpdateTime(LocalDateTime.now());
            orderInfo.setUpdateOpId(user.getId());

            orderGoods.setUpdateTime(LocalDateTime.now());
            orderGoods.setUpdateOpId(user.getId());

            orderfee.setUpdateTime(LocalDateTime.now());
            orderfee.setUpdateOpId(user.getId());

            orderInfoExt.setUpdateTime(LocalDateTime.now());
            orderInfoExt.setUpdateOpId(user.getId());

            if (orderFeeExt != null) {
                orderFeeExt.setUpdateTime(LocalDateTime.now());
                orderFeeExt.setUpdateOpId(user.getId());
            }

            orderScheduler.setUpdateTime(LocalDateTime.now());
            orderScheduler.setUpdateOpId(user.getId());
            //?????????????????????
            orderInfo.setTenantName(sysTenantDefService.getSysTenantDef(user.getTenantId()).getName());
        }
        //?????????????????????
        costPaymentDaysInfo.setOrderId(orderId);
        costPaymentDaysInfo.setTenantId(user.getTenantId());
        incomePaymentDaysInfo.setOrderId(orderId);
        incomePaymentDaysInfo.setTenantId(user.getTenantId());
        //??????????????????
        orderPaymentDaysInfoService.saveOrUpdateOrderPaymentDaysInfo(costPaymentDaysInfo);
        orderPaymentDaysInfoService.saveOrUpdateOrderPaymentDaysInfo(incomePaymentDaysInfo);
        // ????????????????????????
        boolean isLog = setOrderInfoDispatch(operType, desc, orderInfo, orderInfoExt, orderGoods, orderfee, orderFeeExt,
                orderScheduler, depotSchemes, orderOilCardInfos, transitLineInfos, user, accessToken);
        if (!isLog) {
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo, orderId, operType,
                    "[" + user.getName() + "]" + desc + "??????");
        }
        orderInfo.setOpId(user.getId());
        orderInfo.setTenantId(user.getTenantId());
        this.saveOrUpdate(orderInfo);
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            BillSetting billSetting = billSettingService.getBillSetting(orderInfo.getTenantId());
            Double billRate = rateService.getRateValue(billSetting.getRateId(), 0L);
            if (orderInfoExt.getRateValue() == null) {
                orderInfoExt.setRateValue(billRate);
            }
        }
        //
        orderfee.setFinalFeeFlag(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderfee.setPayoutStatus(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderfee.setVerificationState(OrderConsts.PayOutVerificationState.INIT);
        orderfee.setArrivePaymentState(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderfee.setOpId(user.getId());
        orderfee.setTenantId(user.getTenantId());
        if (orderfee.getVehicleAffiliation() == null || orderfee.getVehicleAffiliation().equals("")) {
            orderfee.setVehicleAffiliation("0");
        }
        orderFeeService.saveOrUpdate(orderfee);

        orderGoods.setReciveState(OrderConsts.ReciveState.NOT_UPLOAD);
        orderGoods.setLoadState(OrderConsts.ReciveState.NOT_UPLOAD);
        orderGoods.setOpId(user.getId());
        orderGoods.setTenantId(user.getTenantId());
        if (orderGoods.getLinkName() != null) {
            orderGoods.setLineName(orderGoods.getLinkName());
        }
        if (orderGoods.getLinkPhone() != null) {
            orderGoods.setLinePhone(orderGoods.getLinkPhone());
        }
        orderGoodsService.saveOrUpdate(orderGoods);

        orderInfoExt.setPreAmountFlag(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderInfoExt.setOpId(user.getId());
        orderInfoExt.setTenantId(user.getTenantId());
        orderInfoExtService.saveOrUpdate(orderInfoExt);
        if (orderFeeExt != null) {
            orderFeeExt.setOpId(user.getId());
            orderFeeExt.setTenantId(user.getTenantId());
        }
        orderFeeExtService.saveOrUpdate(orderFeeExt);
        orderScheduler.setOpId(user.getId());
        orderScheduler.setTenantId(user.getTenantId());
        orderSchedulerService.saveOrUpdate(orderScheduler);

        // ???????????????
        saveOrderFeeStatement(orderId, orderfee.getCostPrice(), orderInfo.getTenantId(), user);
        boolean reuslt = false;
        boolean isAudit = false;
        boolean isUpdateAudit = false;
        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            //?????????????????????????????????????????? true??????  false?????????
            boolean isBindOtherVehicle = vehicleDataInfoService.isBindOtherVehicle(orderScheduler.getCarDriverId(),
                    orderScheduler.getPlateNumber(), user);
            if (!isBindOtherVehicle) {
                //??????????????????
//                reuslt = startProcess(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo.getOrderType(), isUpdate,
//                        orderId, accessToken, user);
            } else {
//                //???????????????????????????
//                isUpdateAudit = true;
//                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
//                params.put("busiId", orderInfo.getOrderId());
//                isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, orderInfo.getOrderId(),
//                        SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
            }
        } else if (orderInfoExt.getIsTempTenant() == null || orderInfoExt.getIsTempTenant() != OrderConsts.IS_TEMP_TENANT.YES) {
            //??????????????????????????????????????????
            reuslt = startProcess(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo.getOrderType(), isUpdate,
                    orderId, accessToken, user);
        }
        //???????????????
        if (isUpdateAudit) {
            try {
                auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE,
                        orderInfo.getOrderId(), orderInfo.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isAudit) {
                //??????????????????
                this.orderUpdateAuditPass(orderInfo.getOrderId(), "", isAudit, user, accessToken);
            } else {
                // ???????????????????????????????????????????????????????????????
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_AUDIT);
                //??????????????????????????????
                orderInfo.setOrderUpdateState(OrderConsts.UPDATE_STATE.UPDATE_SPECIAL);
                this.saveOrUpdate(orderInfo);
            }
        } else {
            if (!reuslt) {
                try {
                    auditOutService.cancelProcess(com.youming.youche.record.common.AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderInfo.getOrderId(), orderInfo.getTenantId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // ??????????????????
                this.auditPriceSuccess(orderInfo, orderInfoExt, orderfee, orderScheduler, orderGoods, orderFeeExt, isUpdate,
                        user.getTenantId(), orderId, user, accessToken);
            } else {
                // ???????????????????????????????????????????????????????????????
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_AUDIT);
                this.saveOrUpdate(orderInfo);
            }
        }
        Map<String, Object> resultMap = auditSettingService.getAuditRule(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, accessToken);
        if (resultMap != null && ((orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 3) || (orderScheduler.getAppointWay() != null && orderScheduler.getAppointWay() == 3))) {
            CmCustomerLine cmCustomerLine = null;
            if (orderScheduler.getSourceId() != null) {
                cmCustomerLine = cmCustomerLineService.getCmCustomerLineById(orderScheduler.getSourceId());
            }
            //????????????????????????
            if (resultMap.get("exceedGuidePrice") != null && !"".equals(String.valueOf(resultMap.get("exceedGuidePrice")))
                    && Double.parseDouble(String.valueOf(resultMap.get("exceedGuidePrice"))) > 0) {
                if (cmCustomerLine != null && orderfee.getTotalFee() != null
                        && cmCustomerLine.getGuidePrice() != null
                        && orderfee.getTotalFee() > cmCustomerLine.getGuidePrice()) {
                    Double exceedGuidePrice = Double.parseDouble(String.valueOf(resultMap.get("exceedGuidePrice")));
                    Double v = (exceedGuidePrice + 100) / 100;
                    Double guidePrice = cmCustomerLine.getGuidePrice().doubleValue() * v;

                    guidePrice = (double) Math.round(guidePrice * 100) / 100;

                    if (guidePrice != null && guidePrice < orderfee.getTotalFee().doubleValue()) {
                        restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
                                orderId, accessToken, user, true);
                    }
                }
            }
            //???????????????????????????
            if (resultMap.get("isHigherPrepayment") != null && Boolean.parseBoolean(String.valueOf(resultMap.get("isHigherPrepayment")))) {
                if (orderfee.getPreTotalScaleStandard() != null && orderfee.getPreTotalScale() != null
                        && orderfee.getPreTotalScale() > orderfee.getPreTotalScaleStandard()) {
                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
                            orderId, accessToken, user, false);
                }
            }
            //???????????????????????????
            if (resultMap.get("isHigherOil") != null && Boolean.parseBoolean(String.valueOf(resultMap.get("isHigherOil")))) {
                if (orderfee.getPreOilVirtualScaleStandard() != null && orderfee.getPreOilVirtualScale() != null
                        && orderfee.getPreOilScale() != null && (orderfee.getPreOilVirtualScale() + orderfee.getPreOilScale()) > orderfee.getPreOilVirtualScaleStandard()) {
                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
                            orderId, accessToken, user, false);
                }
            }
            //???????????????????????????
            if (resultMap.get("isLowerOil") != null && Boolean.parseBoolean(String.valueOf(resultMap.get("isLowerOil")))) {
                if (orderfee.getPreOilVirtualScaleStandard() != null && orderfee.getPreOilVirtualScale() != null
                        && orderfee.getPreOilScale() != null && (orderfee.getPreOilVirtualScale() + orderfee.getPreOilScale()) < orderfee.getPreOilVirtualScaleStandard()) {
                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
                            orderId, accessToken, user, false);
                }
            }
            //??????ETC???????????????
            if (resultMap.get("isHigherEtc") != null && Boolean.parseBoolean(String.valueOf(resultMap.get("isHigherEtc")))) {
                if (orderfee.getPreEtcScaleStandard() != null && orderfee.getPreEtcScale() != null
                        && orderfee.getPreEtcScaleStandard() < orderfee.getPreEtcScale()) {
                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
                            orderId, accessToken, user, false);
                }
            }
            //??????ETC???????????????
            if (resultMap.get("isLowerEtc") != null && Boolean.parseBoolean(String.valueOf(resultMap.get("isLowerEtc")))) {
                if (orderfee.getPreEtcScaleStandard() != null && orderfee.getPreEtcScale() != null
                        && orderfee.getPreEtcScaleStandard() > orderfee.getPreEtcScale()) {
                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
                            orderId, accessToken, user, false);
                }
            }
        }
//        if (orderScheduler.getSourceId() != null && !auditService.isAuditIng(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderInfo.getOrderId(), user.getTenantId())) {
//            CmCustomerLine cmCustomerLine = null;
//            if (orderScheduler.getSourceId() != null) {
//                cmCustomerLine = cmCustomerLineService.getCmCustomerLineById(orderScheduler.getSourceId());
//            }
//            if (orderScheduler.getVehicleClass() != null &&
//                    orderInfoExt.getPaymentWay() != null &&
//                    orderScheduler.getVehicleClass() == 1 &&
//                    orderInfoExt.getPaymentWay() == 3) {
//
//
//                if (cmCustomerLine != null && cmCustomerLine.getGuidePrice() != null &&
//                        orderfee.getTotalFee() != null &&
//                        cmCustomerLine.getGuidePrice() < orderfee.getTotalFee()) {
//                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
//                            orderId, accessToken, user);
//
//                }
//            }
//            if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() != 1) {
//                if (cmCustomerLine != null && cmCustomerLine.getGuidePrice() < orderfee.getTotalFee()) {
//                    restartPriceReview(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo, isUpdate,
//                            orderId, accessToken, user);
//                }
//            }
//
//        }
        //????????????
        this.handleWorkOrder(orderInfo.getWorkId(), orderId, user);
        if ((orderfee.getPreOilFee() == null || orderfee.getPreOilFee() == 0) &&
                (orderfee.getPreEtcFee() == null || orderfee.getPreEtcFee() == 0) &&
                (orderfee.getPreCashFee() == null || orderfee.getPreCashFee() == 0) &&
                orderScheduler.getCarDriverId() != null &&
                (orderfee.getPreOilVirtualFee() == null || orderfee.getPreOilVirtualFee() == 0) &&
                orderInfo.getOrderState() != OrderConsts.ORDER_STATE.TO_BE_AUDIT && (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() > 0)) {
            if (auditSettingService.successAuditNodeInstClose("300006", "????????????0????????????????????????", orderId, user)) {
                iOrderFeeService.payProFee(orderId, null, accessToken);
            }

//            orderInfoHService.verifyPayPass(orderId, "300006", "????????????0????????????????????????",
//                    null, "", OrderConsts.RECIVE_TYPE.SINGLE, accessToken);
        }
        return orderId;
    }

    /**
     * ??????????????????
     *
     * @param orderfee
     * @param orderFeeExt
     * @param orderScheduler
     * @param orderInfoExt
     * @param orderInfo
     * @param isUpdate
     * @param orderId
     * @param accessToken
     * @param user
     */
    public void restartPriceReview(OrderFee orderfee, OrderFeeExt orderFeeExt, OrderScheduler orderScheduler,
                                   OrderInfoExt orderInfoExt, OrderInfo orderInfo, Boolean isUpdate, Long orderId,
                                   String accessToken, LoginInfo user, Boolean state) {
        //??????????????????
        startProcessOrder(orderfee, orderFeeExt, orderScheduler, orderInfoExt, orderInfo.getOrderType(), isUpdate,
                orderId, accessToken, user);
        // ???????????????????????????????????????????????????????????????
        orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_AUDIT);
        if (state) {
            orderFeeExt.setTotalAuditSts(1);
            LambdaUpdateWrapper<OrderFeeExt> lambda = Wrappers.lambdaUpdate();
            lambda.set(OrderFeeExt::getTotalAuditSts, 1).eq(OrderFeeExt::getId, orderFeeExt.getId());
            orderFeeExtService.update(lambda);
        }
        //??????????????????????????????
        orderInfo.setOrderUpdateState(0);
        orderInfoService.saveOrUpdate(orderInfo);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param orderInfo
     * @param orderInfoExt
     * @param orderfee
     * @param orderScheduler
     * @param orderGoods
     * @param orderFeeExt
     * @param isUpdate
     * @param tenantId
     * @param orderId
     * @throws Exception
     */
    private void auditPriceSuccess(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderfee,
                                   OrderScheduler orderScheduler, OrderGoods orderGoods,
                                   OrderFeeExt orderFeeExt, Boolean isUpdate,
                                   Long tenantId, Long orderId, LoginInfo user, String accessToken) {
        // ???????????????
        opAccountService.createOrderLimit(orderInfo, orderInfoExt, orderfee, orderScheduler, orderGoods, orderFeeExt, tenantId);
        //todo ??????????????????
        //   orderFeeTF.synPayCenterAddOrder(orderInfo, orderGoods, orderScheduler, orderfee, orderFeeExt,orderInfoExt);


        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
            //????????????????????????????????????
            orderVehicleTimeNodeService.addVehicleTimeNode(orderScheduler.getOrderId(), orderScheduler.getDependTime(), orderScheduler.getPlateNumber(), tenantId);
        }
        // ???????????????????????? ??????????????????
        if (orderScheduler.getVehicleCode() != null && orderScheduler.getVehicleCode() > 0) {
            int oper = OrderConsts.OrderOpType.ADD;
            if (isUpdate != null && isUpdate) {
                oper = OrderConsts.OrderOpType.MODIFY;
            }
            orderStateTrackOperService.saveOrUpdate(orderId, orderScheduler.getVehicleCode(), oper);
        }
        //??????????????????????????????
        this.updateSettleDueDate(orderScheduler, orderInfo, null, user);
        if (!isUpdate) {
            // ????????????????????? ????????????????????????????????????
            orderOpRecordService.saveOrUpdate(orderId, OrderConsts.OrderOpType.ADD, accessToken);
        } else {
            orderOpRecordService.saveOrUpdate(orderId, OrderConsts.OrderOpType.MODIFY, accessToken);
        }
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("busiId", orderInfo.getOrderId());
        params.put("auditCode", AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE);
        auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, orderId,
                SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
        if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass().intValue() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                || (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT
        )) && orderfee.getArrivePaymentFee() != null && orderfee.getArrivePaymentFee() > 0) {
            params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderInfo.getOrderId());
            params.put("auditCode", AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE);
            auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, orderId,
                    SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
        }
        params = new ConcurrentHashMap<String, Object>();
        params.put("busiId", orderInfo.getOrderId());
        params.put("auditCode", AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE);
        auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, orderId,
                SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
        boolean isReturn = false;
        // ????????????
        if (orderInfoExt.getIsBackhaul() != null && orderInfoExt.getIsBackhaul() == 1) {
            isReturn = true;
        }
        // ??????????????????
        if (StringUtils.isNotBlank(orderScheduler.getPlateNumber())) {// ???????????????
            //????????????????????????+2??????

            LocalDateTime inValidDate = orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderScheduler.getDependTime(),
                    orderScheduler.getArriveTime()), 2f);

            vehicleReturnInfoService.disableVehicleReturns(orderScheduler.getPlateNumber(),
                    LocalDateTimeUtil.convertDateToString(inValidDate), isReturn);
            // ???????????????????????????????????????
            if (isReturn) {
                VehicleReturnInfo returnInfo = new VehicleReturnInfo();
                returnInfo.setBeginDate(inValidDate);
                returnInfo.setBeginProvince(orderInfo.getDesProvince());
                //    returnInfo.setBeginProvinceName(orderInfo.getDesProvinceName());
                returnInfo.setBeginRegion(orderInfo.getDesRegion());
                //     returnInfo.setBeginRegionName(orderInfo.getDesRegionName());
                returnInfo.setEndProvince(orderInfo.getSourceProvince());
                //      returnInfo.setEndProvinceName(orderInfo.getSourceProvinceName());
                returnInfo.setEndRegion(orderInfo.getSourceRegion());
                //       returnInfo.setEndRegionName(orderInfo.getSourceRegionName());
                returnInfo.setPlateNumber(orderScheduler.getPlateNumber());
                returnInfo.setVehicleCode(orderScheduler.getVehicleCode());
                returnInfo.setUserId(orderScheduler.getCarDriverId());
                returnInfo.setReturnAmt(orderInfoExt.getBackhaulPrice() == null ? 0 : orderInfoExt.getBackhaulPrice());
                returnInfo.setOrderId(orderInfo.getOrderId());
                vehicleReturnInfoService.addVehicleReturn(returnInfo, user);
            }
        }

        // ????????????????????????
        orderTransferInfoService.saveOrderTransferInfo(orderScheduler, orderInfo, orderGoods, tenantId, orderfee);
        //????????????????????????
        if (orderScheduler.getIsCollection() != null
                && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
                && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
            if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )) {
                //????????????????????????
                OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);

                orderTransferInfoService.acceptOrderTemp(orderInfo, orderGoods, orderInfoExt, orderScheduler,
                        orderfee, orderFeeExt, costPaymentDaysInfo, user, accessToken);
            }
        }
        //todo ??????redis????????????
        //  orderSchedulerTF.setOrderLoncationInfo(orderGoods, orderScheduler);
        //??????????????????
        if (orderScheduler.getVehicleClass() != null
                && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                || ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        ))
                && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0))) {
            //????????? ???C???????????????????????????
            if (StringUtils.isNotBlank(orderScheduler.getCarDriverPhone())) {
                //todo ?????????
                try {
                    List<SysStaticData> sysStaticDataList = readisUtil.getSysStaticDataList("SYS_CITY");
                    orderInfo.setSourceRegionName(orderInfo.getSourceRegion() == null ? ""
                            : readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getSourceRegion() + ""));
                    orderInfo.setDesRegionName(orderInfo.getDesRegion() == null ? ""
                            : readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getDesRegion() + ""));
                    orderSchedulerService.sendOrderSms(orderInfo.getTenantName(), orderInfo.getOrderId(), orderScheduler.getPlateNumber()
                            , orderScheduler.getCarDriverPhone(), orderScheduler.getCarDriverMan(), orderInfo.getSourceRegionName()
                            , orderInfo.getDesRegionName(), orderScheduler.getDependTime(), accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (orderScheduler.getVehicleClass() != null
                && (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                && orderScheduler.getIsCollection() != null
                && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
        ) {
            // ???C????????????????????????
            this.sendTempTenantSms(orderInfo.getTenantName(), orderInfo.getOrderId(), orderScheduler.getPlateNumber(),
                    orderScheduler.getCollectionUserPhone(), accessToken);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param orderId     ?????????
     * @param desc        ????????????
     * @param isNeedAudit ??????????????????
     * @throws Exception
     */
    @Override
    public void orderUpdateAuditPass(Long orderId, String desc, boolean isNeedAudit,
                                     LoginInfo baseUser, String accessToken) {
        // ????????????
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("???????????????????????????????????????");
        }
        // ??????????????????????????????????????????
        OrderInfo orderInfo = this.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("?????????[" + orderId + "]???????????????!");
        }
        if (baseUser == null) {
            baseUser = loginUtils.get(accessToken);
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderGoods orderGoods = orderGoodsService.getOrderGoods(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderFee orderFee = orderFeeService.getOrderFee(orderId);
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        OrderPaymentDaysInfo incomePaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        if (orderInfo.getOrderUpdateState() != null) {
            if (orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
                //??????????????????
                OrderInfoVer orderInfoVer = orderInfoVerService.getOrderInfoVer(orderId);
                OrderGoodsVer orderGoodsVer = orderGoodsVerService.getOrderGoodsVer(orderId);
                OrderInfoExtVer orderInfoExtVer = orderInfoExtVerService.getOrderInfoExtVer(orderId);
                OrderFeeVer orderFeeVer = orderFeeVerService.getOrderFeeVer(orderId);
                OrderFeeExtVer orderFeeExtVer = orderFeeExtVerService.getOrderFeeExtVer(orderId);
                OrderSchedulerVer schedulerVer = orderSchedulerVerService.getOrderSchedulerVer(orderId);
                List<OrderPaymentDaysInfoVer> costPaymentDays = orderPaymentDaysInfoVerService.queryOrderPaymentDaysInfoVer(orderId,
                        OrderConsts.PAYMENT_DAYS_TYPE.COST, OrderConsts.IS_UPDATE.UPDATE);
                List<OrderPaymentDaysInfoVer> incomePaymentDays = orderPaymentDaysInfoVerService.queryOrderPaymentDaysInfoVer(orderId,
                        OrderConsts.PAYMENT_DAYS_TYPE.INCOME, OrderConsts.IS_UPDATE.UPDATE);
                if (orderInfoVer == null) {
                    throw new BusinessException("???????????????[" + orderId + "]????????????!");
                }
                if ((orderInfoExtVer.getPreAmountFlag() == null ? 0 : orderInfoExtVer.getPreAmountFlag().intValue())
                        != (orderInfoExt.getPreAmountFlag() == null ? 0 : orderInfoExt.getPreAmountFlag().intValue())) {//?????????????????????????????????
                    throw new BusinessException("???????????????????????????????????????????????????????????????");
                }
                boolean isUpdateDriver = false;
                boolean isUpdateCopilotDriver = false;
                boolean isUpdatePlateNumber = !ObjectCompareUtils.isModifyObj(orderScheduler, schedulerVer, new String[]{"plateNumber"});

                boolean isUpdateBill = !ObjectCompareUtils.isModifyObj(orderInfo, orderInfoVer, new String[]{"isNeedBill"});
                //boolean isUpdateOilBill =  !ObjectCompareUtils.isModifyObj(orderInfoExt, orderInfoExtVer, new String[] {"oilIsNeedBill"});
                if (orderInfoExt.getFirstPayFlag() != null && orderInfoExt.getFirstPayFlag().intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    if (isUpdateBill) {
                        throw new BusinessException("?????????????????????????????????????????????");
                    }
                }
                int syncType = 0;
                if (orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                        && orderInfoVer.getIsNeedBill().intValue() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    //?????? -- > ????????? ???????????????
                    syncType = 1;
                } else if (orderInfo.getIsNeedBill().intValue() != OrderConsts.IS_NEED_BILL.TERRACE_BILL
                        && orderInfoVer.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    //????????? -->??????
                    syncType = 2;
                } else if (orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                        && orderInfoVer.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    //??????--> ??????
                    syncType = 3;
                }
                isUpdateDriver = !ObjectCompareUtils.isModifyObj(orderScheduler, schedulerVer, new String[]{"carDriverId", "carDriverMan", "carDriverPhone"});
                //?????????
                if (orderScheduler.getVehicleClass() != null
                        && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    isUpdateCopilotDriver = !ObjectCompareUtils.isModifyObj(orderScheduler, schedulerVer, new String[]{"copilotMan", "copilotPhone", "copilotUserId"});
                    //???????????????????????? ??????????????????
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST
                            && !ObjectCompareUtils.isModifyObj(orderInfoExt, orderInfoExtVer, new String[]{"paymentWay"})) {
                        if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                            throw new BusinessException("?????????????????????????????????????????????");
                        }
                        orderOilDepotSchemeService.deleteOrderOilDepotSchem(orderId);
                    }
                }
                if (isUpdateDriver || isUpdateCopilotDriver) {
                    if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                        throw new BusinessException("??????????????????????????????????????????");
                    }
                }
                //??????????????????
//                List<OrderOilCardInfoVer> oilCardInfos = orderOilCardInfoVerService.queryOrderOilCardInfoVerByOrderId(orderId, null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
//                if(oilCardInfos != null && oilCardInfos.size() > 0){
//                    orderOilCardInfoService.deleteOrderOilCardInfo(orderId);
//                    for (OrderOilCardInfoVer orderOilCardInfoVer: oilCardInfos) {
//                        OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
//                        BeanUtil.copyProperties(orderOilCardInfoVer, orderOilCardInfo);
//                        orderOilCardInfoService.saveOrUpdate(orderOilCardInfo);
//                    }
//                }
                List<OrderTransitLineInfoVer> transitLineInfoVers = orderTransitLineInfoVerService.queryOrderTransitLineInfoByOrderId(orderId, OrderConsts.IS_UPDATE.UPDATE);
                List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
                if (transitLineInfoVers != null && transitLineInfoVers.size() > 0) {
                    for (int i = 0; i < transitLineInfoVers.size(); i++) {
                        OrderTransitLineInfoVer infoVer = transitLineInfoVers.get(i);
                        OrderTransitLineInfo info = orderTransitLineInfoService.queryTransitLineInfoByLocation(orderId, i + 1, infoVer.getEand(), infoVer.getNand());
                        if (info != null) {
                            infoVer.setCarDependDate(info.getCarDependDate());
                            infoVer.setCarStartDate(info.getCarStartDate());
                        }
                    }
                }
                if (transitLineInfos != null && transitLineInfos.size() > 0) {
                    //?????????????????????
                    orderTransitLineInfoService.deleteOrderTransitLineInfo(orderId);
                }
                if (transitLineInfoVers != null && transitLineInfoVers.size() > 0) {
                    for (int i = 0; i < transitLineInfoVers.size(); i++) {
                        OrderTransitLineInfoVer info = transitLineInfoVers.get(i);
                        info.setOrderId(orderId);
                        info.setSortId(i + 1);
                        orderTransitLineInfoVerService.setOrderTransitLineInfo(info, baseUser);
                    }
                }
                //??????????????????????????????
                if (orderInfoExt.getUpdateType() == OrderConsts.UPDATE_TYPE.OWN_ORDER
                        || orderInfoExt.getUpdateType() == OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER) {
                    if (orderScheduler.getVehicleClass() != null
                            && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                        //???????????????????????????????????????
                        List<OrderOilDepotSchemeVer> schemeVers = orderOilDepotSchemeVerService.getOrderOilDepotSchemeVerByOrderId(orderId, true, baseUser);
                        if (schemeVers != null && schemeVers.size() > 0) {
                            //?????????????????????
                            orderOilDepotSchemeService.deleteOrderOilDepotSchem(orderId);
                            //??????????????????  ?????????????????????
                            for (OrderOilDepotSchemeVer schemeVer : schemeVers) {
                                OrderOilDepotScheme scheme = new OrderOilDepotScheme();
                                schemeVer.setIsUpdate(OrderConsts.IS_UPDATE.NOT_UPDATE);
                                BeanUtils.copyProperties(schemeVer, scheme);
                                orderOilDepotSchemeVerService.saveOrderOilDepotSchemeVer(schemeVer);
                                //?????????????????????
                                scheme.setId(null);
                                orderOilDepotSchemeService.saveOrUpdate(scheme);
                            }
                        } else {
                            //?????????????????????
                            orderOilDepotSchemeService.deleteOrderOilDepotSchem(orderId);
                        }
                    }
                }
                boolean isSyncCollection = false;
                /**??????????????????*/
                boolean isUpdateCollection = !ObjectCompareUtils.isModifyObj(orderScheduler, schedulerVer, new String[]{"isCollection"});
                boolean isUpdateCollectionInfo = !ObjectCompareUtils.isModifyObj(orderScheduler, schedulerVer, new String[]{"collectionUserPhone", "collectionUserName"});
                if (isUpdateCollection) {
                    if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0 &&
                            orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
                            && (schedulerVer.getIsCollection() == null || schedulerVer.getIsCollection().intValue() != OrderConsts.IS_COLLECTION.YES)
                    ) {
//                        throw new BusinessException("?????????????????????????????????");
                    } else if (schedulerVer.getIsCollection() != null && schedulerVer.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
                            && (orderScheduler.getIsCollection() == null || orderScheduler.getIsCollection().intValue() != OrderConsts.IS_COLLECTION.YES)
                    ) {
                        isSyncCollection = true;
                    }
                } else if (schedulerVer.getIsCollection() != null && schedulerVer.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES && isUpdateCollectionInfo) {
                    isSyncCollection = true;
                }
                if (isSyncCollection) {
                    if (schedulerVer.getVehicleClass() != null && (schedulerVer.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                            || schedulerVer.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                            || schedulerVer.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                    )) {
                        boolean orderCostPermission = sysRoleService.hasOrderCostPermission(baseUser);
                        orderScheduler.setCollectionName(schedulerVer.getCollectionUserName());
                        orderScheduler.setCollectionUserPhone(schedulerVer.getCollectionUserPhone());
                        this.setCollectionInfo(orderInfo, orderScheduler, orderCostPermission, accessToken, baseUser);
                        schedulerVer.setCollectionUserId(orderScheduler.getCollectionUserId());
                    }
                }
                //????????????
                if (costPaymentDays != null && costPaymentDays.size() > 0) {
                    OrderPaymentDaysInfoVer costPaymentDaysInfoVer = costPaymentDays.get(0);
                    orderPaymentDaysInfoService.setOrderPaymentDaysInfo(costPaymentDaysInfo, costPaymentDaysInfoVer);
                    //??????????????????????????????
                    this.updateSettleDueDate(orderScheduler, orderInfo, null, baseUser);
                }
                if (incomePaymentDays != null && incomePaymentDays.size() > 0) {
                    OrderPaymentDaysInfoVer incomePaymentDaysInfoVer = incomePaymentDays.get(0);
                    orderPaymentDaysInfoService.setOrderPaymentDaysInfo(incomePaymentDaysInfo, incomePaymentDaysInfoVer);
                    //??????????????????????????????
                    this.updateSettleDueDate(orderScheduler, orderInfo, null, baseUser);
                }
                long oilFee = (orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee()) -
                        (orderFeeVer.getPreOilFee() == null ? 0 : orderFeeVer.getPreOilFee());
                //??????????????????
                if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )) {
                    //?????????????????????
                    Long entityOilMax = redisUtil.get(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + orderFee.getOrderId()) == null ? 0 :
                            Long.parseLong((String) redisUtil.get(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + orderFee.getOrderId()));
                    if (oilFee > 0) {//??????
                        if (entityOilMax < orderFee.getPreOilFee()) {
                            redisUtil.set(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + orderFee.getOrderId(), String.valueOf(orderFee.getPreOilFee()));
                        }
                    } else if (oilFee < 0) {//??????
                        if (entityOilMax < orderFeeVer.getPreOilFee()) {
                            redisUtil.set(EnumConsts.RemoteCache.ORDER_ENTITY_OIL_MAX + orderFee.getOrderId(), String.valueOf(orderFeeVer.getPreOilFee()));
                        }
                    }
                }
                boolean isAddPrePayEquivalenceCard = false;
                boolean isAddAfterPayEquivalenceCard = false;
                if (orderFeeVer.getPrePayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFeeVer.getPrePayEquivalenceCardType()
                        && StringUtils.isNotBlank(orderFeeVer.getPrePayEquivalenceCardNumber())
                        && orderFee.getPrePayEquivalenceCardType() == null && StringUtils.isBlank(orderFee.getPrePayEquivalenceCardNumber())
                ) {
                    isAddPrePayEquivalenceCard = true;
                }
                //?????????????????????
                if (orderFeeVer.getAfterPayEquivalenceCardType() != null &&
                        OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFeeVer.getAfterPayEquivalenceCardType()
                        && StringUtils.isNotBlank(orderFeeVer.getAfterPayEquivalenceCardNumber())
                        && orderFee.getAfterPayEquivalenceCardType() == null && StringUtils.isBlank(orderFee.getAfterPayEquivalenceCardNumber())
                ) {
                    isAddAfterPayEquivalenceCard = true;
                }
                Long userId = schedulerVer.getCarDriverId();
                if (orderInfoVer.getToTenantId() != null && orderInfoVer.getToTenantId() > 0) {// ???????????????
                    // ?????????
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfoVer.getToTenantId(), true);
                    if (tenantDef == null) {
                        log.error("????????????????????????????????????????????????ID[" + orderInfoVer.getToTenantId() + "]");
                        throw new BusinessException("??????????????????????????????????????????");
                    }
                    userId = tenantDef.getAdminUser();
                } else if (schedulerVer.getIsCollection() != null && schedulerVer.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
                    userId = schedulerVer.getCollectionUserId();
                }
                if ((orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE))
                        || (schedulerVer.getVehicleClass() != null && schedulerVer.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && (orderInfoExtVer.getPaymentWay() != null && orderInfoExtVer.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE))) {
                    this.orderUpdateOilCardHandle(orderInfoExt, orderScheduler, orderInfo);
                }
                //????????????????????????
                if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    //????????????????????? ????????????
                    if (StringUtils.isNotBlank(orderFee.getPrePayEquivalenceCardNumber()) || StringUtils.isNotBlank(orderFee.getAfterPayEquivalenceCardNumber())) {
                        //???????????????
                        if (!ObjectCompareUtils.isModifyObj(orderFeeVer, orderFee, new String[]{"prePayEquivalenceCardAmount"})) {
                            if (orderFeeVer.getPrePayEquivalenceCardAmount() != null && orderFee.getPrePayEquivalenceCardAmount() != null) {
                                long cardAmount = orderFeeVer.getPrePayEquivalenceCardAmount() - orderFee.getPrePayEquivalenceCardAmount();
                                if (cardAmount > 0) {//??????????????????
                                    oilCardManagementService.saveEquivalenceCard(orderInfo, orderScheduler, orderFee.getPrePayEquivalenceCardNumber()
                                            , Math.abs(cardAmount), true, false, baseUser);
                                } else if (cardAmount < 0) {//??????????????????
                                    oilCardManagementService.saveEquivalenceCard(orderInfo, orderScheduler, orderFee.getPrePayEquivalenceCardNumber()
                                            , Math.abs(cardAmount), false, false, baseUser);
                                }
                            }
                        }
                        //???????????????
                        if (!ObjectCompareUtils.isModifyObj(orderFeeVer, orderFee, new String[]{"afterPayEquivalenceCardAmount"})) {
                            if (orderFeeVer.getAfterPayEquivalenceCardAmount() != null && orderFee.getAfterPayEquivalenceCardAmount() != null) {
                                long cardAmount = orderFeeVer.getAfterPayEquivalenceCardAmount() - orderFee.getAfterPayEquivalenceCardAmount();
                                if (cardAmount > 0) {//??????????????????
                                    oilCardManagementService.saveEquivalenceCard(orderInfo, orderScheduler, orderFee.getAfterPayEquivalenceCardNumber()
                                            , Math.abs(cardAmount), true, false, baseUser);
                                } else if (cardAmount < 0) {//??????????????????
                                    oilCardManagementService.saveEquivalenceCard(orderInfo, orderScheduler, orderFee.getAfterPayEquivalenceCardNumber()
                                            , Math.abs(cardAmount), false, false, baseUser);
                                }
                            }
                        }
                    }
                    //??????????????????????????????
                    if (orderInfoExt.getUpdateType() == OrderConsts.UPDATE_TYPE.OWN_ORDER
                            || orderInfoExt.getUpdateType() == OrderConsts.UPDATE_TYPE.IS_ACCEPT_ORDER) {
                        //?????????????????? ?????????????????????
                        if (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                            //???????????? ???????????????
                            this.orderUpdateOilCardHandle(oilFee, userId, orderId, orderScheduler,
                                    orderInfo, orderInfoExt, orderFee, baseUser, accessToken);
                        }
                        if (orderScheduler.getVehicleClass() == null || ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                        )
                                || (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT))) {
                            if ((isUpdateDriver || !orderFeeExt.getOilConsumer().equals(orderFeeExtVer.getOilConsumer())) && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                                    && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT) {
                                //????????????????????? ????????????????????????
                                UpdateTheOrderInDto in = new UpdateTheOrderInDto();
                                in.setUserId(orderScheduler.getCarDriverId());
                                in.setVehicleAffiliation("0");
                                in.setOriginalAmountFee(orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee());
                                in.setUpdateAmountFee(0L);
                                in.setOriginalEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
                                in.setUpdateEntityOilFee(0L);
//                                in.setOriginalVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                                in.setOriginalVirtualOilFee(0L);

                                in.setUpdatelongVirtualOilFee(0L);
                                in.setOriginalEtcFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
                                in.setUpdateEtcFee(0L);
                                in.setOrderId(orderId);
                                in.setIsPayArriveFee(orderFee.getArrivePaymentState() == null ? 0 : orderFee.getArrivePaymentState());
                                in.setOriginalArriveFee(orderFee.getArrivePaymentFee() == null ? 0 : orderFee.getArrivePaymentFee());
                                in.setUpdateArriveFee(0L);
                                in.setTenantId(orderInfo.getTenantId());
                                in.setIsNeedBill(orderInfo.getIsNeedBill());
                                in.setOilUserType(orderInfoExt.getOilUseType() != null ? orderInfoExt.getOilUseType() : -1);
                                in.setOriginalOilConsumer(orderFeeExt.getOilConsumer() == null ? 1 : orderFeeExt.getOilConsumer());
                                in.setUpdateOilConsumer(orderFeeExtVer.getOilConsumer() == null ? 1 : orderFeeExtVer.getOilConsumer());
                                in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
                                in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
                                in.setOriginalOilBillType(orderFeeExt.getOilBillType());
                                in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
                                orderOilSourceService.updateTheOrder(in, baseUser, accessToken);
                                //?????????????????????
                                in.setUserId(schedulerVer.getCarDriverId());
                                in.setOriginalAmountFee(0L);
                                in.setUpdateAmountFee(orderFeeVer.getPreCashFee() == null ? 0 : orderFeeVer.getPreCashFee());
                                in.setOriginalEntityOilFee(0L);
                                in.setUpdateEntityOilFee(orderFeeVer.getPreOilFee() == null ? 0 : orderFeeVer.getPreOilFee());
                                in.setOriginalVirtualOilFee(0L);
//                                in.setUpdatelongVirtualOilFee(orderFeeVer.getPreOilVirtualFee() == null ? 0 : orderFeeVer.getPreOilVirtualFee());
                                in.setUpdatelongVirtualOilFee(0L);
                                in.setOriginalEtcFee(0L);
                                in.setUpdateEtcFee(orderFeeVer.getPreEtcFee() == null ? 0 : orderFeeVer.getPreEtcFee());
                                in.setOriginalArriveFee(0L);
                                in.setUpdateArriveFee(orderFeeVer.getArrivePaymentFee() == null ? 0 : orderFeeVer.getArrivePaymentFee());
                                orderOilSourceService.updateTheOrder(in, baseUser, accessToken);
                            } else {
                                //todo ????????????
                                orderPayMethodService.updateOrderTransit(userId, orderFee, orderFeeVer,
                                        orderInfo, orderInfoExt, orderScheduler, schedulerVer, orderFeeExt,
                                        orderFeeExtVer, isUpdateDriver, baseUser, accessToken);
                            }
                        } else if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                                && orderInfoExt.getPaymentWay() != null && (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST
                                || orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)) {
                            if ((isUpdateDriver || isUpdateCopilotDriver) && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                                if (isUpdateDriver) {
                                    //????????????????????????
                                    UpdateTheOwnCarOrderInDto in = new UpdateTheOwnCarOrderInDto();
                                    in.setVehicleAffiliation("0");
                                    in.setOilUserType(orderInfoExt.getOilUseType() != null ? orderInfoExt.getOilUseType() : -1);
                                    in.setOrderId(orderId);
                                    in.setMasterUserId(orderScheduler.getCarDriverId());
                                    in.setTenantId(orderInfo.getTenantId());
                                    in.setIsNeedBill(orderInfo.getIsNeedBill());
                                    in.setSlaveUserId(orderScheduler.getCopilotUserId());
                                    in.setOriginalEntiyOilFee(orderFee.getPreOilFee());
                                    in.setUpdateEntiyOilFee(0L);
//                                    in.setOriginalFictitiousOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                                    in.setOriginalFictitiousOilFee(0L);
                                    in.setUpdateFictitiousOilFee(0L);
                                    in.setOriginalBridgeFee(orderFeeExt.getPontage() == null ? 0 : orderFeeExt.getPontage());
                                    in.setUpdateBridgeFee(0L);
                                    in.setOriginalMasterSubsidy(orderFeeExt.getSalary() == null ? 0 : orderFeeExt.getSalary());
                                    in.setUpdateMasterSubsidy(0L);
                                    in.setOriginalSlaveSubsidy(0L);
                                    in.setUpdateSlaveSubsidy(0L);
                                    in.setOriginalOilConsumer(orderFeeExt.getOilConsumer());
                                    in.setUpdateOilConsumer(orderFeeExtVer.getOilConsumer());
                                    in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
                                    in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
                                    in.setOriginalOilBillType(orderFeeExt.getOilBillType());
                                    in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
                                    orderOilSourceService.updateTheOwnCarOrder(in, baseUser, accessToken);
                                    //?????????????????????
                                    in.setMasterUserId(schedulerVer.getCarDriverId());
                                    in.setOriginalEntiyOilFee(0L);
                                    in.setUpdateEntiyOilFee(orderFeeVer.getPreOilFee());
                                    in.setOriginalFictitiousOilFee(0L);
//                                    in.setUpdateFictitiousOilFee(orderFeeVer.getPreOilVirtualFee() == null ? 0 : orderFeeVer.getPreOilVirtualFee());
                                    in.setUpdateFictitiousOilFee(0L);
                                    in.setOriginalBridgeFee(0L);
                                    in.setUpdateBridgeFee(orderFeeExtVer.getPontage() == null ? 0 : orderFeeExtVer.getPontage());
                                    in.setOriginalMasterSubsidy(0L);
                                    in.setUpdateMasterSubsidy(orderFeeExtVer.getSalary() == null ? 0 : orderFeeExtVer.getSalary());
                                    orderOilSourceService.updateTheOwnCarOrder(in, baseUser, accessToken);
                                }
                                if (isUpdateCopilotDriver) {
                                    //????????????????????????
                                    UpdateTheOwnCarOrderInDto in = new UpdateTheOwnCarOrderInDto();
                                    in.setVehicleAffiliation("0");
                                    in.setOilUserType(orderInfoExt.getOilUseType() != null ? orderInfoExt.getOilUseType() : -1);
                                    in.setOrderId(orderId);
                                    in.setMasterUserId(orderScheduler.getCarDriverId());
                                    in.setTenantId(orderInfo.getTenantId());
                                    in.setIsNeedBill(orderInfo.getIsNeedBill());
                                    in.setSlaveUserId(orderScheduler.getCopilotUserId());
                                    in.setOriginalEntiyOilFee(0L);
                                    in.setUpdateEntiyOilFee(0L);
                                    in.setOriginalFictitiousOilFee(0L);
                                    in.setUpdateFictitiousOilFee(0L);
                                    in.setOriginalBridgeFee(0L);
                                    in.setUpdateBridgeFee(0L);
                                    in.setOriginalMasterSubsidy(0L);
                                    in.setUpdateMasterSubsidy(0L);
                                    in.setOriginalSlaveSubsidy(orderFeeExt.getCopilotSalary() == null ? 0 : orderFeeExt.getCopilotSalary());
                                    in.setUpdateSlaveSubsidy(0L);
                                    in.setOriginalOilConsumer(orderFeeExt.getOilConsumer());
                                    in.setUpdateOilConsumer(orderFeeExtVer.getOilConsumer());
                                    in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
                                    in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
                                    in.setOriginalOilBillType(orderFeeExt.getOilBillType());
                                    in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
                                    orderOilSourceService.updateTheOwnCarOrder(in, baseUser, accessToken);
                                    //????????????????????????
                                    in.setSlaveUserId(schedulerVer.getCopilotUserId());
                                    in.setUpdateMasterSubsidy(orderFeeExtVer.getCopilotSalary() == null ? 0 : orderFeeExtVer.getCopilotSalary());
                                    in.setOriginalSlaveSubsidy(0L);
                                    orderOilSourceService.updateTheOwnCarOrder(in, baseUser, accessToken);
                                }
                            } else {
                                //??????????????????
                                UpdateTheOwnCarOrderInDto in = new UpdateTheOwnCarOrderInDto();
                                in.setVehicleAffiliation("0");
                                in.setOilUserType(orderInfoExt.getOilUseType() != null ? orderInfoExt.getOilUseType() : -1);
                                in.setOrderId(orderId);
                                in.setTenantId(orderInfo.getTenantId());
                                in.setIsNeedBill(orderInfo.getIsNeedBill());
                                in.setSlaveUserId(orderScheduler.getCopilotUserId());
                                in.setOriginalEntiyOilFee(orderFee.getPreOilFee());
                                in.setUpdateEntiyOilFee(orderFeeVer.getPreOilFee());
                                //??????????????????????????????????????????????????????
//                                in.setOriginalFictitiousOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                                in.setOriginalFictitiousOilFee(0L);
//                                in.setUpdateFictitiousOilFee(orderFeeVer.getPreOilVirtualFee() == null ? 0 : orderFeeVer.getPreOilVirtualFee());
                                in.setUpdateFictitiousOilFee(0L);
                                in.setOriginalBridgeFee(orderFeeExt.getPontage() == null ? 0 : orderFeeExt.getPontage());
                                in.setUpdateBridgeFee(orderFeeExtVer.getPontage() == null ? 0 : orderFeeExtVer.getPontage());
                                in.setOriginalMasterSubsidy(orderFeeExt.getSalary() == null ? 0 : orderFeeExt.getSalary());
                                in.setUpdateMasterSubsidy(orderFeeExtVer.getSalary() == null ? 0 : orderFeeExtVer.getSalary());
                                in.setOriginalSlaveSubsidy(orderFeeExt.getCopilotSalary() == null ? 0 : orderFeeExt.getCopilotSalary());
                                in.setUpdateSlaveSubsidy(orderFeeExtVer.getCopilotSalary() == null ? 0 : orderFeeExtVer.getCopilotSalary());
                                in.setOriginalOilConsumer(orderFeeExt.getOilConsumer());
                                in.setUpdateOilConsumer(orderFeeExtVer.getOilConsumer());
                                in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
                                in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
                                in.setOriginalOilBillType(orderFeeExt.getOilBillType());
                                in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
                                if (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                                    in.setMasterUserId(orderScheduler.getCarDriverId());
                                } else if (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                                    in.setMasterUserId(orderScheduler.getOnDutyDriverId());//??????????????????????????????
                                }
                                orderOilSourceService.updateTheOwnCarOrder(in, baseUser, accessToken);
                            }
                        }
                    }
                }

                if (!ObjectCompareUtils.isModifyObj(orderGoods, orderGoodsVer, OrderConsts.UPDATE_COMPARE_PROERTY.GOODS_PROERTY)
                        || !ObjectCompareUtils.isModifyObj(orderGoods, orderGoodsVer, OrderConsts.UPDATE_COMPARE_PROERTY.RECIVE_PROERTY)) {
                    //todo  ?????????????????????
                    this.syncTransferOrderGoods(orderInfo, orderGoodsVer);
                }
                if (orderScheduler.getAppointWay() != null && orderScheduler.getAppointWay().intValue() == OrderConsts.AppointWay.APPOINT_CAR
                        && schedulerVer.getAppointWay() != null && schedulerVer.getAppointWay().intValue() == OrderConsts.AppointWay.APPOINT_LOCAL) {
                    orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_DISPATCH);
                }
                if ((isUpdateDriver || isUpdatePlateNumber) && orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                    OrderInfo fromOrderInfo = this.getOrder(orderInfo.getFromOrderId());
                    if (fromOrderInfo != null) {
                        String mag = "???????????????"
                                + "?????????[" + orderScheduler.getCarDriverMan() + "-" + orderScheduler.getCarDriverPhone() + "]"
                                + "????????????[" + schedulerVer.getCarDriverMan() + "-" + schedulerVer.getCarDriverPhone() + "]???";
                        if (isUpdatePlateNumber) {
                            mag = "????????????????????????[" + orderScheduler.getPlateNumber() + "]????????????[" + schedulerVer.getPlateNumber() + "]???";
                        }
                        sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getFromOrderId(), SysOperLogConst.OperType.Audit,
                                "[" + baseUser.getName() + "]????????????" + mag, orderInfo.getFromTenantId());
                        if (fromOrderInfo.getFromOrderId() != null && fromOrderInfo.getFromOrderId() > 0) {
                            OrderInfo lastOrderInfo = this.getOrder(fromOrderInfo.getFromOrderId());
                            if (lastOrderInfo != null) {
                                sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OrderInfo,
                                        fromOrderInfo.getFromOrderId(), SysOperLogConst.OperType.Audit,
                                        "[" + sysTenantDefService.getSysTenantDef(fromOrderInfo.getTenantId()).getName() + "]????????????"
                                                + mag, fromOrderInfo.getFromTenantId());
                            }
                        }
                    }
                }
                //????????????
                UpdateOrderInfoInDto orderInfoIn = new UpdateOrderInfoInDto();
                UpdateOrderFeeInDto orderFeeIn = new UpdateOrderFeeInDto();
                UpdateOrderFeeExtInDto orderFeeExtIn = new UpdateOrderFeeExtInDto();
                UpdateOrderInfoExtInDto orderInfoExtIn = new UpdateOrderInfoExtInDto();
                UpdateOrderSchedulerInDto orderSchedulerIn = new UpdateOrderSchedulerInDto();
                UpdateOrderGoodsInDto orderGoodsIn = new UpdateOrderGoodsInDto();

                BeanUtil.copyProperties(orderInfoVer, orderInfoIn);
                BeanUtil.copyProperties(orderInfoExtVer, orderInfoExtIn);
                BeanUtil.copyProperties(orderFeeVer, orderFeeIn);
                BeanUtil.copyProperties(orderFeeExtVer, orderFeeExtIn);
                BeanUtil.copyProperties(orderGoodsVer, orderGoodsIn);
                BeanUtil.copyProperties(schedulerVer, orderSchedulerIn);

                BeanUtil.copyProperties(orderInfoIn, orderInfo);
                BeanUtil.copyProperties(orderInfoExtIn, orderInfoExt);
                BeanUtil.copyProperties(orderFeeIn, orderFee);
                BeanUtil.copyProperties(orderFeeExtIn, orderFeeExt);
                BeanUtil.copyProperties(orderGoodsIn, orderGoods);
                BeanUtil.copyProperties(orderSchedulerIn, orderScheduler);
                if (orderScheduler.getVehicleClass() == null || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    orderInfo.setIsTransit(OrderConsts.IsTransit.TRANSIT_NO);
                } else {
                    orderInfo.setIsTransit(OrderConsts.IsTransit.TRANSIT_YES);
                }
                if (isAddPrePayEquivalenceCard) {
                    oilCardManagementService.saveEquivalenceCard(orderInfo, orderScheduler,
                            orderFee.getPrePayEquivalenceCardNumber(),
                            orderFee.getPrePayEquivalenceCardAmount(), true, true, baseUser);
                }
                if (isAddAfterPayEquivalenceCard) {
                    oilCardManagementService.saveEquivalenceCard(orderInfo, orderScheduler,
                            orderFee.getAfterPayEquivalenceCardNumber(),
                            orderFee.getAfterPayEquivalenceCardAmount(), true, true, baseUser);
                }
                if ((isUpdateDriver || isUpdatePlateNumber) && orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                    //**????????????????????????????????????
                    orderTransferInfoService.syncOrderVehicleInfo(orderInfo.getFromOrderId(), orderInfo, orderScheduler);
                }
                if (isUpdateCollection || isUpdateDriver || isUpdateCopilotDriver
                        || isUpdateBill
                        || (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
                        && isUpdateCollectionInfo)) {
                    //?????????????????? ????????????????????????
                    opAccountService.createOrderLimit(orderInfo, orderInfoExt, orderFee, orderScheduler, orderGoods, orderFeeExt, orderInfo.getTenantId());
                }
                if (isUpdateDriver) {//???????????????
                    List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrder(orderId,
                            OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                    if (switchInfos == null || switchInfos.size() == 0) {//????????????????????????
                        //??????????????????????????????
                        orderScheduler.setOnDutyDriverId(orderScheduler.getCarDriverId());
                        orderScheduler.setOnDutyDriverName(orderScheduler.getCarDriverMan());
                        orderScheduler.setOnDutyDriverPhone(orderScheduler.getCarDriverPhone());
                    }
                }
                orderInfo.setUpdateTime(LocalDateTime.now());
                orderInfo.setUpdateOpId(baseUser.getUserInfoId());

                orderGoods.setUpdateTime(LocalDateTime.now());
                orderGoods.setUpdateOpId(baseUser.getUserInfoId());

                orderFee.setUpdateTime(LocalDateTime.now());
                orderFee.setUpdateOpId(baseUser.getUserInfoId());

                orderInfoExt.setUpdateTime(LocalDateTime.now());
                orderInfoExt.setUpdateOpId(baseUser.getUserInfoId());

                orderFeeExt.setUpdateTime(LocalDateTime.now());
                orderFeeExt.setUpdateOpId(baseUser.getUserInfoId());

                orderScheduler.setUpdateTime(LocalDateTime.now());
                orderScheduler.setUpdateOpId(baseUser.getUserInfoId());

                orderInfo.setOrderUpdateState(0);
                //???????????????????????????
                orderInfoExt.setIsUpdate(OrderConsts.IS_UPDATE.UPDATE);
                orderSchedulerService.saveOrUpdate(orderScheduler);
                if (orderScheduler.getIsCollection() == null || orderScheduler.getIsCollection() == 0) {
                    LambdaUpdateWrapper<OrderScheduler> lambda = Wrappers.lambdaUpdate();
                    lambda.set(OrderScheduler::getIsCollection, orderScheduler.getIsCollection())
                            .set(OrderScheduler::getCollectionUserId, orderScheduler.getCollectionUserId())
                            .set(OrderScheduler::getCollectionUserName, orderScheduler.getCollectionUserName())
                            .set(OrderScheduler::getCollectionUserPhone, orderScheduler.getCollectionUserPhone())
                            .eq(OrderScheduler::getId, orderScheduler.getId());
                    orderSchedulerService.update(lambda);
                }
                super.saveOrUpdate(orderInfo);
                orderInfoExtService.saveOrUpdate(orderInfoExt);
                if (orderInfoExt.getIsBackhaul() == null || orderInfoExt.getIsBackhaul() == 0) {
                    LambdaUpdateWrapper<OrderInfoExt> lambda = Wrappers.lambdaUpdate();
                    lambda.set(OrderInfoExt::getIsBackhaul, orderInfoExt.getIsBackhaul())
                            .set(OrderInfoExt::getBackhaulLinkMan, orderInfoExt.getBackhaulLinkMan())
                            .set(OrderInfoExt::getBackhaulLinkManBill, orderInfoExt.getBackhaulLinkManBill())
                            .set(OrderInfoExt::getBackhaulPrice, orderInfoExt.getBackhaulPrice())
                            .eq(OrderInfoExt::getId, orderInfoExt.getId());
                    orderInfoExtService.update(lambda);
                }
                //??????????????????,????????????????????????????????????????????????,???????????????,???????????????
                if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 2) {
                        Long preTotalFee = orderFee.getPreTotalFee();
                        Long preOilFee = orderFee.getPreOilFee();
                        Long preOilVirtualFee2 = orderFee.getPreOilVirtualFee();

                        if (preTotalFee == null) {
                            preTotalFee = 0L;
                        }
                        if (preOilFee == null) {
                            preOilFee = 0L;
                        }
                        OrderFee orderFee11 = orderFeeService.getOrderFee(orderFee.getOrderId());


                        List<OrderOilCardInfoVer> verList = orderOilCardInfoVerService.queryOrderOilCardInfoVerByOrderId(orderInfo.getOrderId(),
                                null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
                        Long oilFeeVer = 0L;
                        if (verList != null && verList.size() > 0) {

                            for (OrderOilCardInfoVer orderOilCardInfoVer : verList) {
                                Long oilFeeVer2 = orderOilCardInfoVer.getOilFee();
                                oilFeeVer += oilFeeVer2;
                            }
                        }
                        List<OrderOilCardInfo> orderOilCardInfos = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
                        Long oilFee1 = 0L;
                        if (orderOilCardInfos != null && orderOilCardInfos.size() > 0) {
                            for (OrderOilCardInfo orderOilCardInfo : orderOilCardInfos) {
                                Long oilFee2 = orderOilCardInfo.getOilFee();
                                oilFee1 += oilFee2;
                            }
                            orderFee.setPreOilFee((oilFee1 + oilFeeVer));
                            orderFee.setPreTotalFee(orderFee11.getPreTotalFee() + preOilFee);
                        }
                        OrderFee orderFee1 = orderFeeService.getOrderFee(orderFee.getOrderId());
                        //?????????????????????
                        Long preOilVirtualFee = orderFee1.getPreOilVirtualFee();
                        Long preTotalFee2 = orderFee1.getPreTotalFee();
                        if (preOilVirtualFee != null && preOilVirtualFee > 0) {
                            if (orderFee.getPreOilVirtualFee() == null) {
                                orderFee.setPreOilVirtualFee(0L);
                            }
                            if (preTotalFee2 == null) {
                            }
                            Long preOilVirtualFee1 = orderFee.getPreOilVirtualFee();
                            orderFee.setPreOilVirtualFee(preOilVirtualFee1 + preOilVirtualFee);
                            if (orderFee.getPreTotalFee() == null) {
                                preOilVirtualFee1 = 0L;
                            } else {
                                orderFee.setPreTotalFee(orderFee11.getPreTotalFee() + preOilFee + preOilVirtualFee1);
                            }

                            // todo ???????????????????????????
                            OrderAccountOilSource orderAccountOilSource = new OrderAccountOilSource();
                            orderAccountOilSource.setUserId(orderScheduler.getCarDriverId());
                            orderAccountOilSource.setUserType(3);
                            orderAccountOilSource.setUpdateOpId(baseUser.getId());
                            orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                            orderAccountOilSource.setOilBalance(0L);
                            orderAccountOilSource.setTenantId(baseUser.getTenantId());
                            orderAccountOilSource.setRechargeOilBalance(preOilVirtualFee1);
                            orderAccountOilSource.setOpId(baseUser.getId());
                            orderAccountOilSource.setCreateTime(LocalDateTime.now());
                            orderAccountOilSource.setId(null);
                            orderAccountOilSourceService.save(orderAccountOilSource);
                        } else {
                            Long preOilVirtualFee1 = orderFee.getPreOilVirtualFee();
                            orderFee.setPreOilVirtualFee(preOilVirtualFee1);
                            if (orderFee.getPreTotalFee() == null) {
                                preOilVirtualFee1 = 0L;
                            } else {
                                orderFee.setPreTotalFee(orderFee11.getPreTotalFee() + preOilFee + preOilVirtualFee1);
                            }
                            OrderAccountOilSource orderAccountOilSource = new OrderAccountOilSource();
                            orderAccountOilSource.setUserId(orderScheduler.getCarDriverId());
                            orderAccountOilSource.setUserType(3);
                            orderAccountOilSource.setUpdateOpId(baseUser.getId());
                            orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                            orderAccountOilSource.setOilBalance(0L);
                            orderAccountOilSource.setTenantId(baseUser.getTenantId());
                            orderAccountOilSource.setRechargeOilBalance(preOilVirtualFee1);
                            orderAccountOilSource.setOpId(baseUser.getId());
                            orderAccountOilSource.setCreateTime(LocalDateTime.now());
                            orderAccountOilSource.setId(null);
                            orderAccountOilSourceService.save(orderAccountOilSource);
                        }
                    }

                }
                orderFeeService.saveOrUpdate(orderFee);
                orderFeeExtService.saveOrUpdate(orderFeeExt);
                orderGoodsService.saveOrUpdate(orderGoods);
                if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass().intValue() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT
                )) && orderFee.getArrivePaymentFee() != null && orderFee.getArrivePaymentFee() > 0
                        && orderFee.getArrivePaymentState().intValue() == OrderConsts.AMOUNT_FLAG.WILL_PAY) {//?????????
                    AuditNodeInst auditNodeInst = auditNodeInstService.queryAuditIng(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE,
                            orderId, orderInfo.getTenantId());
                    if (auditNodeInst == null) {//?????????????????????
                        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                        params = new ConcurrentHashMap<String, Object>();
                        params.put("busiId", orderInfo.getOrderId());
                        params.put("auditCode", AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE);
                        auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, orderId,
                                SysOperLogConst.BusiCode.OrderInfo, params, accessToken);
                    }
                }
                if (syncType == 2 || syncType == 3) {
                    boolean isUpdate = syncType == 3;
                    orderFeeService.syncBillFormOrder(orderInfo, orderGoods, orderScheduler, orderFee, orderFeeExt, orderInfoExt,
                            true, isUpdate);
                }
                if (syncType == 2) {
                    if (StringUtils.isBlank(orderFeeExt.getBillLookUp())) {
                        BillInfoReceiveRel billInfoReceiveRel = billInfoReceiveRelService.getDefaultBillInfoByTenantId(baseUser.getTenantId());
                        if (billInfoReceiveRel == null || billInfoReceiveRel.getBillInfo() == null
                                || StringUtils.isBlank(billInfoReceiveRel.getBillInfo().getBillLookUp())) {
                            throw new BusinessException("????????????????????????????????????");
                        }
                        orderFeeExt.setBillLookUp(billInfoReceiveRel.getBillInfo().getBillLookUp());
                        BillAccountTenantRel billAccountTenantRel = billAccountTenantRelService.getDefaultBilltAccountByTenantId(baseUser.getTenantId());
                        if (billAccountTenantRel != null && billAccountTenantRel.getAccountSeq() != null) {
                            AccountBankRel accountBankRel = accountBankRelService.getById(billAccountTenantRel.getAccountSeq());
                            if (accountBankRel != null && StringUtils.isNotBlank(accountBankRel.getAcctNo())) {
                                orderFeeExt.setBillAcctNo(accountBankRel.getAcctNo());
                            }
                        }
                        orderFeeExt.setOilBillType(billInfoReceiveRel.getDeduction() == null || !billInfoReceiveRel.getDeduction()
                                ? OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1 : OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
                    }
                } else if (syncType == 1) {
                    orderFeeExt.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
                    orderFeeExt.setBillAcctNo(null);
                    orderFeeExt.setBillLookUp(null);
                }
                if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    //todo ????????????????????????????????????
                    //???????????????????????????????????????????????????
                    if (orderScheduler.getAppointWay() != null && orderScheduler.getAppointWay() != OrderConsts.AppointWay.APPOINT_TENANT) {
                        orderVehicleTimeNodeService.addVehicleTimeNode(orderScheduler.getOrderId(),
                                orderScheduler.getDependTime(), orderScheduler.getPlateNumber(), orderScheduler.getTenantId());
                    }
                }
                if (schedulerVer.getVehicleClass() != null && schedulerVer.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrder(orderInfo.getOrderId(), OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                    if (switchInfos != null && switchInfos.size() > 0) {
                        for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
                            if (orderDriverSwitchInfo.getReceiveUserId() != null &&
                                    !orderDriverSwitchInfo.getReceiveUserId().equals(orderScheduler.getCarDriverId())
                                    && !orderDriverSwitchInfo.getReceiveUserId().equals(orderScheduler.getCopilotUserId())) {
                                Integer isPayed = OrderConsts.AMOUNT_FLAG.WILL_PAY;
                                List<OrderDriverSubsidy> subsidies = orderDriverSubsidyService.getDriverSubsidys(orderId, orderDriverSwitchInfo.getReceiveUserId(), false, baseUser);
                                if (subsidies != null && subsidies.size() > 0) {
                                    OrderDriverSubsidy orderDriverSubsidy = subsidies.get(0);
                                    if (orderDriverSubsidy.getIsPayed() != null) {
                                        isPayed = orderDriverSubsidy.getIsPayed();
                                    }
                                }
                                //todo ??????????????????
                                orderFeeExtService.setDriverSubsidy(orderId, orderDriverSwitchInfo.getReceiveUserId(),
                                        true, isPayed, baseUser);
                                if (isPayed != null && isPayed.intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                                    //?????????????????????????????????
                                    Long subsidyFeeVer = orderDriverSubsidyService.findOrderDriverSubSidyFee(orderId,
                                            orderDriverSwitchInfo.getReceiveUserId(), null, null, true, null);
                                    Long subsidyFee = orderDriverSubsidyService.findOrderDriverSubSidyFee(orderId,
                                            orderDriverSwitchInfo.getReceiveUserId(), null, null, false, null);
                                    Long diffFee = subsidyFeeVer - subsidyFee;
                                    if (diffFee != 0) {
                                        AcOrderSubsidyInDto acOrderSubsidyIn = new AcOrderSubsidyInDto();
                                        acOrderSubsidyIn.setOrderId(orderId);
                                        acOrderSubsidyIn.setDriverUserId(orderDriverSwitchInfo.getReceiveUserId());
                                        acOrderSubsidyIn.setDriverSubsidy(diffFee);
                                        acOrderSubsidyIn.setDriverUserName(orderDriverSwitchInfo.getReceiveUserName());
                                        acOrderSubsidyIn.setVehicleAffiliation("0");
                                        acOrderSubsidyIn.setOilAffiliation(orderInfoExt.getOilAffiliation());
                                        acOrderSubsidyIn.setTenantId(orderScheduler.getTenantId());
                                        payoutIntfService.payAddSubsidy(acOrderSubsidyIn, baseUser, accessToken);
                                    }
                                }
                            }
                            // ??????????????????????????????
                            orderFeeExtService.calculateDriverSwitchSubsidy(orderId, true);
                        }
                    }
                }
                /** ?????????????????? */
                orderDriverSubsidyService.sycDriverSubsidyFromVer(orderId, baseUser);

                // ????????? ?????? ???C??????????????????????????????
                if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                    Map<String, Object> paraMap = new HashMap<String, Object>();
                    paraMap.put("tenantName", orderInfo.getTenantName());
                    paraMap.put("orderId", orderInfo.getOrderId());
                    paraMap.put("opType", "??????");
                    //todo ????????????
//                    sysSmsSend.sendSms(orderScheduler.getCarDriverPhone(), EnumConsts.SmsTemplate.ORDER_OPERATE,
//                            SMS_TYPE.ORDER_ASSISTANT, OBJ_TYPE.ORDER_UPDATE, String.valueOf(orderInfo.getOrderId()), paraMap);
                }
                //??????????????????
                orderOpRecordService.saveOrUpdate(orderId, OrderConsts.OrderOpType.MODIFY, accessToken);
                //todo ??????redis????????????
                //      orderSchedulerTF.setOrderLoncationInfo(orderGoods, orderScheduler);
                // ???????????????????????? ??????????????????
                if (orderScheduler.getVehicleCode() != null && orderScheduler.getVehicleCode() > 0) {
                    orderStateTrackOperService.saveOrUpdate(orderId, orderScheduler.getVehicleCode(),
                            OrderConsts.OrderOpType.MODIFY);
                }
                // ????????????????????????
				/*if (isNeedAudit) {// ????????????
					ISysOperLogSV sysOperLogSV = (ISysOperLogSV) SysContexts.getBean("sysOperLogSV");
					sysOperLogSV.saveSysOperLog(BusiCode.OrderInfo, orderId, OperType.Audit,
							"[" + baseUser.getUserName() + "]???????????????????????????,????????????:" + desc);
				}*/
            } else if (orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL) {
                // ?????????????????????
                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0
                        && ((orderScheduler.getIsCollection() == null
                        || orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.NO))) {
                    orderInfo.setOrderState(StringUtils.isEmpty(orderScheduler.getPlateNumber()) ? OrderConsts.ORDER_STATE.TO_BE_RECIVE : OrderConsts.ORDER_STATE.TO_BE_LOAD);
                } else {
                    orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
                }
                orderInfo.setOrderUpdateState(0);
                this.saveOrUpdate(orderInfo);
                // todo ???????????????????????? //?????????????????????
                this.auditPriceSuccess(orderInfo, orderInfoExt, orderFee, orderScheduler, orderGoods, orderFeeExt,
                        false, orderInfo.getTenantId(), orderId, baseUser, accessToken);
            }
        }

        // ????????????????????????
        OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
        try {
            SysSmsSend sss = new SysSmsSend();
            sss.setBillId(scheduler.getCarDriverPhone());
            sss.setCreateTime(LocalDateTime.now());
            sss.setExpId("0");
            sss.setObjId(String.valueOf(orderInfo.getOrderId()));
            sss.setObjType(String.valueOf(com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.ORDER_UPDATE));
            sss.setSendTime(LocalDateTime.now());
            sss.setSendFlag(0);
            sss.setSmsContent(orderInfo.getTenantName() + "?????????????????????" + orderInfo.getOrderId() + "?????????????????????");
            sss.setSmsType(com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
            sysSmsSendService.saveOrUpdate(sss);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void orderUpdateAuditNoPass(Long orderId, String desc, String accessToken, LoginInfo baseUser) {
        // ???????????????
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("???????????????????????????????????????");
        }
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OrderInfo::getOrderId, orderId).orderByDesc(OrderInfo::getCreateTime).last("limit 1");
        OrderInfo orderInfo = super.getOne(lambdaQueryWrapper);
        if (orderInfo == null) {
            throw new BusinessException("?????????[" + orderId + "]??????????????????");
        }
        if (baseUser == null) {
            baseUser = loginUtils.get(accessToken);
        }
        if (orderInfo.getOrderUpdateState() != null) {
            boolean isUpdateDate = false;
            if (orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
                isUpdateDate = true;
                //????????????????????????
                orderOilCardInfoVerService.releaseOilCardBalance(orderId, baseUser.getTenantId());
                //?????????????????????????????????????????????
                orderOilDepotSchemeVerService.updateSchemeVerIsUpdate(orderId, OrderConsts.IS_UPDATE.NOT_UPDATE, baseUser);
                //???????????????????????????????????????
                orderPaymentDaysInfoVerService.loseEfficacyPaymentDaysVerUpdate(orderId);
                //???????????????????????????
                orderTransitLineInfoVerService.updateTransitLineInfoVerType(orderId, OrderConsts.IS_UPDATE.NOT_UPDATE, OrderConsts.IS_UPDATE.UPDATE);
                //???????????????????????????????????????????????????
//                IOrderDriverSubsidySV orderDriverSubsidySV = (IOrderDriverSubsidySV) SysContexts.getBean("orderDriverSubsidySV");
//                orderDriverSubsidySV.updateDriverSubsidyVer(orderId, null,null, 1);
                orderDriverSubsidyService.updateDriverSubsidyVer(orderId, null, null, 1);
                // ??????????????????????????????????????????
                orderInfo.setOrderUpdateState(0);
                super.saveOrUpdate(orderInfo);
            } else if (orderInfo.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL) {
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.AUDIT_NOT);
                orderInfo.setOrderUpdateState(0);
                super.saveOrUpdate(orderInfo);
            }
            // ???????????????????????????
//            ISysOperLogSV sysOperLogSV = (ISysOperLogSV) SysContexts.getBean("sysOperLogSV");
            sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Audit,
                    "[" + baseUser.getName() + "]??????" + (isUpdateDate ? "?????????" : "") + "???????????????,???????????????:" + desc);
        }
    }

    @Override
    public OrderInfoH getOrderH(Long orderId) {
        List<OrderInfoH> list = orderInfoHMapper.getOrderH(orderId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }


    @Override
    public Boolean setOrderInfoDispatch(SysOperLogConst.OperType operType, String desc,
                                        OrderInfo orderInfo, OrderInfoExt orderInfoExt,
                                        OrderGoods orderGoods, OrderFee orderfee,
                                        OrderFeeExt orderFeeExt, OrderScheduler orderScheduler,
                                        List<OrderOilDepotScheme> depotSchemes,
                                        List<OrderOilCardInfo> orderOilCardInfos,
                                        List<OrderTransitLineInfo> transitLineInfos, LoginInfo baseUser, String token) {
        Boolean isLog = false;
        orderInfo.setTenantId(baseUser.getTenantId());
        orderScheduler.setTenantId(baseUser.getTenantId());
        //???????????????
        if (transitLineInfos != null && transitLineInfos.size() > 0) {

            for (int i = 0; i < transitLineInfos.size(); i++) {
                OrderTransitLineInfo info = transitLineInfos.get(i);
                info.setOrderId(orderInfo.getOrderId());
                info.setCarDependDate(null);
                info.setCarStartDate(null);
                info.setCreateTime(LocalDateTime.now());
                info.setUpdateTime(LocalDateTime.now());
                info.setId(null);
                info.setOpId(baseUser.getUserInfoId());
                info.setTenantId(info.getTenantId());
                info.setSortId(i + 1);
                orderTransitLineInfoService.saveOrUpdate(info);
            }
        }
        if (orderInfo.getOrderType() != null && orderInfo.getOrderType().intValue() == OrderConsts.OrderType.FIXED_LINE
                && orderScheduler.getSourceId() != null && orderScheduler.getSourceId() > 0
                && orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT
        ) {
            CmCustomerLine line = cmCustomerLineService.getById(orderScheduler.getSourceId());
            if (line != null) {
                orderfee.setGuidePrice(line.getGuidePrice());
            }
        }
        String acceptTenantName = "";
        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
            // ????????????
            // ???????????????????????????
            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId());
            if (tenantDef == null) {
                log.error("???????????????????????????????????????[" + orderInfo.getToTenantId() + "]");
                throw new BusinessException("???????????????????????????????????????");
            }
            acceptTenantName = tenantDef.getName();
            //??????????????????????????????
            //checkUserPinganBankInfo(tenantDef.getAdminUser(),acceptTenantName,orderInfo.getIsNeedBill(),false);
        }
        boolean orderCostPermission = sysRoleService.hasOrderCostPermission(baseUser);
        if (OrderConsts.AppointWay.APPOINT_TENANT == orderScheduler.getAppointWay()) {
            if (!orderCostPermission) {
                throw new BusinessException("??????????????????????????????????????????????????????????????????");
            }
            isLog = true;
            sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), operType,
                    "[" + baseUser.getName() + "]" + desc + "?????????????????????[" + acceptTenantName + "]");
            orderInfo.setIsTransit(OrderConsts.IsTransit.TRANSIT_YES);
            // ????????????-- ?????????
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_RECIVE);
        } else if (OrderConsts.AppointWay.APPOINT_CAR == orderScheduler.getAppointWay() || orderScheduler.getAppointWay() == 6) {

			/*if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
				//??????????????????????????????
				checkUserPinganBankInfo(orderScheduler.getCarDriverId(),orderScheduler.getCarDriverMan(),orderInfo.getIsNeedBill(),false);
				if(orderScheduler.getCopilotUserId()!=null && orderScheduler.getCopilotUserId()>0) {
					checkUserPinganBankInfo(orderScheduler.getCopilotUserId(),orderScheduler.getCopilotMan(),orderInfo.getIsNeedBill(),false);
				}
			}*/
            if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {//?????????
                if (orderScheduler.getCarDriverId() == null || orderScheduler.getCarDriverId() <= 0) {
                    throw new BusinessException("?????????????????????");
                }
            }
            //??????????????????????????????
            orderScheduler.setOnDutyDriverId(orderScheduler.getCarDriverId());
            orderScheduler.setOnDutyDriverName(orderScheduler.getCarDriverMan());
            orderScheduler.setOnDutyDriverPhone(orderScheduler.getCarDriverPhone());
            // ????????????
            // 1 ????????????????????????????????????????????????
            // 2 ???????????????????????????????????????????????????
            // ???????????????????????????????????????
            Long noOrderId = null;
            if (operType == SysOperLogConst.OperType.Add) {
                // ???????????????????????????????????????
                if (orderInfo.getOrderType() == OrderConsts.OrderType.ONLINE_RECIVE) {
                    noOrderId = orderInfo.getFromOrderId();
                }

            } else {
                // ??????????????????????????????
                noOrderId = orderInfo.getOrderId();
            }
            /**
             * ?????????????????????????????????????????????????????????????????????????????????????????????
             */
			/*orderSchedulerTF.checkLineIsOk(orderScheduler.getPlateNumber(), orderScheduler.getCarDriverId(),
					orderScheduler.getCopilotUserId(), orderScheduler.getDependTime(), orderInfo.getFromOrderId(), noOrderId);*/

            if ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )) {
                // ????????? ????????????
                if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                    this.setCollectionInfo(orderInfo, orderScheduler, orderCostPermission, token, baseUser);
                }
                if (orderInfoExt.getIsTempTenant() == null) {//???????????????????????????
                    orderInfoExt.setIsTempTenant(OrderConsts.IS_TEMP_TENANT.NO);
                }

                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    isLog = true;
                    //??????????????????????????????
                    // ????????????
                    // ???????????????????????????
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId());
                    if (tenantDef == null) {
                        log.error("???????????????????????????????????????[" + orderInfo.getToTenantId() + "]");
                        throw new BusinessException("???????????????????????????????????????");
                    }
                    acceptTenantName = tenantDef.getName();
                    sysOperLogService.saveSysOperLog(baseUser, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), operType,
                            "[" + baseUser.getName() + "]" + desc + "?????????????????????[" + acceptTenantName + "]");
                    if (!orderCostPermission) {
                        throw new BusinessException("??????????????????????????????????????????????????????????????????");
                    }
                    //orderInfo.setOrderState(ORDER_STATE.TO_BE_RECIVE);
                    orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
                } else {
                    // ??????????????????
                    List<OrderTransferInfo> transferInfos = orderTransferInfoService.queryTransferInfoList(
                            OrderConsts.TransferOrderState.TO_BE_RECIVE, null, orderInfo.getOrderId());
                    for (OrderTransferInfo Info : transferInfos) {
                        if (Info.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                            Info.setTransferOrderState(OrderConsts.TransferOrderState.BILL_TIME_OUT);
                            orderTransferInfoService.saveOrUpdate(Info);
                        }
                    }
                    // C????????? ????????????--?????????
                    orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
                }

                orderInfo.setIsTransit(OrderConsts.IsTransit.TRANSIT_YES);

            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
                OrderPaymentWayOilOut out = this.getOrderPaymentWayOil(orderScheduler.getCarDriverId(), orderInfo.getTenantId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                if (orderInfoExt.getPaymentWay() != null) {
                    if (orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE) {
                        //????????????????????????
                        boolean bool = orderSchedulerService.checkPayMentWaySwitchover(orderScheduler.getPlateNumber()
                                , orderScheduler.getDependTime(), orderScheduler.getTenantId(), orderScheduler.getOrderId());
                        if (!bool) {
                            throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                        }
                        if (out.getExpenseOil() != null && out.getExpenseOil() > 0) {
                            throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                        }
//                        List<Long> orders = orderFeeService.getOrderByPaymentWay(orderScheduler.getCarDriverId(), 2);
//                        UserAccountOutVo userAccountOutVo=new UserAccountOutVo();
//                        userAccountOutVo.setBillId(orderScheduler.getCarDriverPhone());
//                        Page<UserAccountVo> userAccountVoPage = orderAccountService.doAccountQuery(userAccountOutVo, 1, 10, token);
//                        List<UserAccountVo> records = userAccountVoPage.getRecords();
//                        if(orders != null && orders.size() >0 && records != null && records.size()>0 && records.get(0).getOilBalance()
//                                != null &&  records.get(0).getOilBalance() > 0){
//                            throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
//                        }
//                        if(records != null && records.size()>0 && records.get(0).getOilBalance() != null &&  records.get(0).getOilBalance() > 0){
//                            OrderOilSource rechargeOilSource=new OrderOilSource();
//                            rechargeOilSource.setOrderId(orderScheduler.getOrderId());
//                            rechargeOilSource.setUserId(orderScheduler.getCarDriverId());
//                            rechargeOilSource.setSourceOrderId(orderScheduler.getOrderId());
//                            rechargeOilSource.setSourceAmount(records.get(0).getOilBalance() !=null?records.get(0).getOilBalance():0);
//                            rechargeOilSource.setNoPayOil(records.get(0).getOilBalance());
//                            rechargeOilSource.setPaidOil(0L);
//                            rechargeOilSource.setTenantId(baseUser.getTenantId());
//                            rechargeOilSource.setVehicleAffiliation("0");
//                            rechargeOilSource.setOrderDate(LocalDateTime.now());
//                            rechargeOilSource.setCreateTime(LocalDateTime.now());
//                            rechargeOilSource.setOpId(baseUser.getId());
//                            rechargeOilSource.setIsNeedBill(1);
//                            rechargeOilSource.setOilAffiliation("1");
//                            rechargeOilSource.setOilAccountType(1);
//                            rechargeOilSource.setOilBillType(1);
//                            rechargeOilSource.setOilConsumer(1);
//                            rechargeOilSource.setRebateOil(0L);
//                            rechargeOilSource.setNoRebateOil(records.get(0).getOilBalance());
//                            rechargeOilSource.setPaidRebateOil(0L);
//                            rechargeOilSource.setCreditOil(1L);
//                            rechargeOilSource.setNoCreditOil(records.get(0).getOilBalance());
//                            rechargeOilSource.setPaidCreditOil(0L);
//                            rechargeOilSource.setSourceTenantId(0L);
//                            rechargeOilSource.setUserType(3);
//                            orderOilSourceService.save(rechargeOilSource);
                    } else {
                        if ((out.getCostOil() != null && out.getCostOil() > 0) || (out.getContractOil() != null && out.getContractOil() > 0)) {
                            throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                        }
                    }

                } else {
                    if ((out.getCostOil() != null && out.getCostOil() > 0) || (out.getContractOil() != null && out.getContractOil() > 0)) {
                        throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
                    }
//                        UserAccountOutVo userAccountOutVo=new UserAccountOutVo();
//                        userAccountOutVo.setBillId(orderScheduler.getCarDriverPhone());
//                        List<Long> orders = orderFeeService.getOrderByPaymentWay(orderScheduler.getCarDriverId(), 3);
//                        Page<UserAccountVo> userAccountVoPage = orderAccountService.doAccountQuery(userAccountOutVo, 1, 10, token);
//                        List<UserAccountVo> records = userAccountVoPage.getRecords();
//                        if(orders != null && orders.size() >0 && records != null && records.size()>0 && records.get(0).getOilBalance()
//                                != null &&  records.get(0).getOilBalance() > 0){
//                            throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
//                        }
//                        if(records != null && records.size()>0 && records.get(0).getOilBalance() != null &&  records.get(0).getOilBalance() > 0){
//                            RechargeOilSource rechargeOilSource=new RechargeOilSource();
//                            rechargeOilSource.setRechargeType(2);
//                            rechargeOilSource.setFromFlowId(0L);
//                            rechargeOilSource.setRechargeOrderId(orderScheduler.getOrderId()+"");
//                            rechargeOilSource.setUserId(orderScheduler.getCarDriverId());
//                            rechargeOilSource.setSourceOrderId(orderScheduler.getOrderId()+"");
//                            rechargeOilSource.setSourceAmount(records.get(0).getOilBalance() !=null?records.get(0).getOilBalance():0);
//                            rechargeOilSource.setNoPayOil(records.get(0).getOilBalance());
//                            rechargeOilSource.setPaidOil(0L);
//                            rechargeOilSource.setTenantId(baseUser.getTenantId());
//                            rechargeOilSource.setVehicleAffiliation("0");
//                            rechargeOilSource.setOrderDate(LocalDateTime.now());
//                            rechargeOilSource.setCreateTime(LocalDateTime.now());
//                            rechargeOilSource.setOpId(baseUser.getId());
//                            rechargeOilSource.setIsNeedBill(1);
//                            rechargeOilSource.setOilAffiliation("1");
//                            rechargeOilSource.setOilAccountType(1);
//                            rechargeOilSource.setOilBillType(1);
//                            rechargeOilSource.setOilConsumer(1);
//                            rechargeOilSource.setRebateOil(0L);
//                            rechargeOilSource.setNoRebateOil(records.get(0).getOilBalance());
//                            rechargeOilSource.setPaidRebateOil(0L);
//                            rechargeOilSource.setCreditOil(1L);
//                            rechargeOilSource.setNoCreditOil(records.get(0).getOilBalance());
//                            rechargeOilSource.setPaidCreditOil(0L);
//                            rechargeOilSource.setSourceTenantId(0L);
//                            rechargeOilSource.setUserType(3);
//                            rechargeOilSourceService.save(rechargeOilSource);
//                        }
//                    }
                }
                // ????????????????????????????????????1????????????????????????????????????
                if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                    // ???????????????????????????
                    if (orderFeeExt.getOilLitreVirtual() != null && orderFeeExt.getOilLitreVirtual() > 0) {
                        if (depotSchemes != null && depotSchemes.size() > 0) {
                            Long oilDepotLitre = 0L;
                            for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                                if (orderOilDepotScheme.getOilDepotId() != null && orderOilDepotScheme.getOilDepotId() > 0) {
                                    if (orderOilDepotScheme.getOilDepotLitre() == null || orderOilDepotScheme.getOilDepotLitre() <= 0) {
                                        throw new BusinessException("???????????????????????????0!");
                                    }
                                    oilDepotLitre += orderOilDepotScheme.getOilDepotLitre();
                                    orderOilDepotScheme.setOrderId(orderInfo.getOrderId());
                                    orderOilDepotScheme.setTenantId(baseUser.getTenantId());
                                    orderOilDepotSchemeService.save(orderOilDepotScheme);
                                }
                            }
                            if (!oilDepotLitre.equals(orderFeeExt.getOilLitreVirtual())) {
                                //     log.info("??????????????????="+oilDepotLitre+",???????????????="+orderFeeExt.getOilLitreVirtual());
                                throw new BusinessException("????????????????????????????????????????????????");
                            }
                        }
                    } else {
                        if (depotSchemes != null && depotSchemes.size() > 0) {
                            for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                                if (orderOilDepotScheme.getOilDepotId() != null && orderOilDepotScheme.getOilDepotId() > 0) {
                                    if (orderOilDepotScheme.getOilDepotLitre() == null || orderOilDepotScheme.getOilDepotLitre() <= 0) {
                                        throw new BusinessException("???????????????????????????0!");
                                    }
                                }
                            }
                        }
                    }
                    //????????????????????????
                    this.setDriverSubsidy(orderScheduler, orderFeeExt, baseUser, false, transitLineInfos == null ? 0 : transitLineInfos.size(), transitLineInfos, token, orderInfoExt, orderInfo, orderfee);
                } else if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    //?????????????????????????????????????????????

                    if (orderOilCardInfos != null && orderOilCardInfos.size() > 0) {
                        for (OrderOilCardInfo orderOilCardInfo : orderOilCardInfos) {
                            if (StringUtils.isNotBlank(orderOilCardInfo.getOilCardNum())) {
                                orderOilCardInfo.setOrderId(orderInfo.getOrderId());
                                orderOilCardInfo.setTenantId(orderInfo.getTenantId());
                                orderOilCardInfoService.saveOrUpdate(orderOilCardInfo);
                            }
                        }
                    }
                }

                // ??????????????????
                List<OrderTransferInfo> transferInfos = orderTransferInfoService.queryTransferInfoList(
                        OrderConsts.TransferOrderState.TO_BE_RECIVE, null, orderInfo.getOrderId());
                for (OrderTransferInfo Info : transferInfos) {
                    if (Info.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                        Info.setTransferOrderState(OrderConsts.TransferOrderState.BILL_TIME_OUT);
                        orderTransferInfoService.saveOrUpdate(Info);
                    }
                }
                // ????????????--?????????
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
            } else {
                throw new BusinessException("????????????????????????????????????!");
            }

            StringBuffer beforeOrders = new StringBuffer();
            Map<String, Object> carBeforeOrder = orderSchedulerService.getPreOrderIdByPlateNumber(
                    orderScheduler.getPlateNumber(), orderScheduler.getDependTime(), baseUser.getTenantId(), orderInfo.getOrderId());
            if (carBeforeOrder != null) {
                beforeOrders.append("car:").append(carBeforeOrder.get("orderId")).append(";");
            }
            if (orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId() > 0) {
                Map<String, Object> mainBeforeOrder = orderSchedulerService.getPreOrderIdByUserid(
                        orderScheduler.getCarDriverId(), orderScheduler.getDependTime(), baseUser.getTenantId(), null);
                if (mainBeforeOrder != null) {
                    beforeOrders.append("driver:").append(mainBeforeOrder.get("orderId")).append(";");
                }
            }

            if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
                Map<String, Object> subBeforeOrder = orderSchedulerService.getPreOrderIdByUserid(
                        orderScheduler.getCopilotUserId(), orderScheduler.getDependTime(), baseUser.getTenantId(), null);

                if (subBeforeOrder != null) {
                    beforeOrders.append("copilot:").append(subBeforeOrder.get("orderId")).append(";");
                }

            }
            // ??????????????????????????????????????????,??????????????????,??????????????????
            orderInfo.setBeforeOrders(beforeOrders.toString());

        } else {
            // ????????????--?????????
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_DISPATCH);
            // ??????????????????
            List<OrderTransferInfo> transferInfos = orderTransferInfoService
                    .queryTransferInfoList(OrderConsts.TransferOrderState.TO_BE_RECIVE, null, orderInfo.getOrderId());
            for (OrderTransferInfo Info : transferInfos) {
                if (Info.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                    Info.setTransferOrderState(OrderConsts.TransferOrderState.BILL_TIME_OUT);
                    orderTransferInfoService.saveOrUpdate(Info);
                }
            }
        }
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            // ???????????????,????????????????????????????????????????????????????????????????????????

            Long billMethod = billPlatformService.getBillMethodByTenantId(orderInfo.getTenantId());
            if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                String tenantTerraceBillLimit = readisUtil.getSysCfg(TENANT_TERRACE_BILL_LIMIT, "0").getCfgValue();
                if (StringUtils.isNotBlank(tenantTerraceBillLimit)) {
                    List<String> limitArr = Arrays.asList(tenantTerraceBillLimit.split(","));
                    if (limitArr != null && limitArr.contains(String.valueOf(orderInfo.getTenantId()))) {
                        throw new BusinessException("???????????????????????????????????????");
                    }
                }
                if (orderInfoExt.getPaymentWay() != null) {
                    if (orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                        if (orderfee.getCostPrice() == null || orderfee.getCostPrice() == 0) {
                            throw new BusinessException("???????????????0??????????????????????????????");
                        }
                    } else if (orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT) {
                        if (orderfee.getTotalFee() == null || orderfee.getTotalFee() == 0) {
                            throw new BusinessException("??????????????????0??????????????????????????????");
                        }
                    } else if (orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.COST) {
                        if (orderfee.getCostPrice() == null || orderfee.getCostPrice() == 0) {
                            throw new BusinessException("???????????????0??????????????????????????????");
                        }
                    }
                }
            } else if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )) {
                if (orderfee.getTotalFee() == null || orderfee.getTotalFee() == 0) {
                    throw new BusinessException("??????????????????0??????????????????????????????");
                }
            }
            if (billMethod == null || billMethod == 0) {
                String customerPhone = readisUtil.getSysCfg(CUSTOMER_SERVICE_PHONE, "0").getCfgValue();
                throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" + customerPhone);
            }
            BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(billMethod);
            if (billPlatform == null) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
            orderfee.setVehicleAffiliation(billMethod.toString());
        } else {
            orderfee.setVehicleAffiliation(orderInfo.getIsNeedBill().toString());
        }

        //??????????????????
        if (!(orderInfo.getId() != null && orderInfo.getId() > 0) && StringUtils.isNotBlank(orderfee.getVehicleAffiliation())) {
            orderSchedulerService.checkBillDependTime(orderInfo, orderScheduler, Long.parseLong(orderfee.getVehicleAffiliation()), baseUser);
        }
        //??????????????????
        orderInfoExtService.verifyOrderNeedBill(orderInfo, orderInfoExt, orderScheduler, orderfee, orderGoods, transitLineInfos, baseUser);
        //???????????????????????????
	/*	if(orderScheduler.getVehicleClass()!=null&&orderScheduler.getVehicleClass()==VEHICLE_CLASS.VEHICLE_CLASS1
				&&orderInfoExt.getOilUseType()!=null&&orderInfoExt.getOilUseType()==OIL_USE_TYPE.CUSTOMOIL) {
			IOrderStatementTF orderStatementTF = (IOrderStatementTF) SysContexts.getBean("orderStatementTF");
	    	long customOilFee=orderStatementTF.queryCustomOil(orderInfoExt.getOrderId());
	    	if(orderfee.getPreOilVirtualFee() !=  null && customOilFee<orderfee.getPreOilVirtualFee()) {
	    		throw new BusinessException("??????????????????????????????????????????????????????");
	    	}
		}*/
        //?????????????????????????????????????????????????????????????????????
        if (orderInfo.getIsNeedBill() != null
                && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL &&
                !(orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)) {
            orderInfoExt.setOilAffiliation(orderfee.getVehicleAffiliation());
        } else {
            orderInfoExt.setOilAffiliation("1");
        }
        orderInfoExt.setFirstPayFlag(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        if (orderFeeExt != null) {
            if (orderFeeExt.getOilConsumer() == null || orderFeeExt.getOilConsumer() < 0) {
                orderFeeExt.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            }
            if (orderFeeExt.getOilAccountType() == null || orderFeeExt.getOilAccountType() < 0) {
                orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
            }
            printEstimatedCosts(orderInfo, orderScheduler, orderInfoExt, orderFeeExt, depotSchemes, operType);
        }

        return isLog;
    }


    private void printEstimatedCosts(OrderInfo orderInfo, OrderScheduler orderScheduler, OrderInfoExt orderInfoExt,
                                     OrderFeeExt orderFeeExt, List<OrderOilDepotScheme> depotSchemes, SysOperLogConst.OperType operType) {

        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            // ????????????????????????????????????????????????????????????
            StringBuffer print = new StringBuffer();
            if (operType.getCode() == SysOperLogConst.OperType.Add.getCode()) {
                print.append("????????????: ");
            } else {
                print.append("????????????: ");
            }

            print.append("orderId[").append(orderInfo.getOrderId()).append("]");
            print.append("???????????????:[").append(orderInfo.getBeforeOrders()).append("]");
            if (orderInfoExt.getIsUseCarOilCost() != null) {
                print.append(" ????????????[").append(orderInfoExt.getIsUseCarOilCost()).append("]");
            } else {
                print.append(" ????????????[??????]");
            }
            if (orderInfoExt.getRunOil() != null) {
                print.append(" ????????????[").append(orderInfoExt.getCapacityOil()).append("]");
            } else {
                print.append(" ????????????[??????]");
            }
            if (orderInfoExt.getCapacityOil() != null) {
                print.append(" ????????????[").append(orderInfoExt.getRunOil()).append("]");
            } else {
                print.append(" ????????????[??????]");
            }
            if (orderInfoExt.getRunWay() != null) {
                print.append(" ????????????[").append(orderInfoExt.getRunWay()).append("]");
            } else {
                print.append(" ????????????[??????]");
            }
            if (orderScheduler.getDistance() != null) {
                print.append(" ????????????[").append(orderScheduler.getDistance()).append("]");
            } else {
                print.append(" ????????????[??????]");
            }

            if (orderFeeExt.getOilPrice() != null) {
                print.append(" ????????????[").append(orderFeeExt.getOilPrice()).append("]");
            } else {
                print.append(" ????????????[??????]");
            }

            if (depotSchemes != null && depotSchemes.size() > 0) {
                for (OrderOilDepotScheme orderOilDepotScheme : depotSchemes) {
                    print.append(" ??????[").append(orderOilDepotScheme.getOilDepotId()).append("]");
                }
            } else {
                print.append(" ??????[??????]");
            }
            if (orderFeeExt.getPontage() != null) {
                print.append(" ?????????[").append(orderFeeExt.getPontage()).append("]");
            } else {
                print.append(" ?????????[??????]");
            }

            if (orderFeeExt.getSubsidyDay() != null) {
                print.append(" ?????????????????????[").append(orderFeeExt.getSubsidyDay()).append("]");
            } else {
                print.append(" ?????????????????????[??????]");
            }

            if (orderFeeExt.getSubsidyCopilotDay() != null) {
                print.append(" ?????????????????????[").append(orderFeeExt.getSubsidyCopilotDay()).append("]");
            } else {
                print.append(" ?????????????????????[??????]");
            }

            log.info(print.toString());
        }
    }


    /**
     * ??????????????????????????????
     *
     * @param
     * @param orderInfoExt
     * @param orderScheduler
     * @param orderInfo
     * @throws Exception
     */
    private void orderUpdateOilCardHandle(OrderInfoExt orderInfoExt, OrderScheduler orderScheduler, OrderInfo orderInfo) {
        //??????????????????
        List<OrderOilCardInfoVer> verList = orderOilCardInfoVerService.queryOrderOilCardInfoVerByOrderId(orderInfo.getOrderId(),
                null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
        List<OrderOilCardInfo> list = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderInfo.getOrderId(), null);
        List<String> oilCardList = new ArrayList<>();
        List<String> oilCardListVer = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (OrderOilCardInfo orderOilCardInfo : list) {
                oilCardList.add(orderOilCardInfo.getOilCardNum());
            }
        }
        if (verList != null && verList.size() > 0) {
            for (OrderOilCardInfoVer orderOilCardInfoVer : verList) {
                oilCardListVer.add(orderOilCardInfoVer.getOilCardNum());
            }
        }
        if (list != null && list.size() > 0 && (verList == null || verList.size() == 0)) {
            //????????? --> ?????????
            orderOilCardInfoService.deleteOrderOilCardInfo(orderInfo.getOrderId());
        } else if (verList != null && verList.size() > 0 && (list == null || list.size() == 0)) {
            //????????? --> ?????????
            for (OrderOilCardInfoVer orderOilCardInfoVer : verList) {
                orderOilCardInfoVer.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.YET_UPDATE);
                orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfoVer);
                //????????????????????????
                OrderOilCardInfo oilCardInfo = new OrderOilCardInfo();
                BeanUtils.copyProperties(orderOilCardInfoVer, oilCardInfo);
                oilCardInfo.setId(null);
                if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY
                        && orderOilCardInfoVer.getOilFee() != null && orderOilCardInfoVer.getOilFee() > 0) {//??????????????????????????????????????????
                    List<OilCardManagement> managements = oilCardManagementService.findByOilCardNum(oilCardInfo.getOilCardNum(), orderScheduler.getTenantId());
                    if (managements == null || managements.size() <= 0) {
                        throw new BusinessException("????????????[" + oilCardInfo.getOilCardNum() + "]?????????!");
                    }

                    OilCardManagement oilCard = managements.get(0);
                    //?????????????????????????????????
                    int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
                    Long balance = oilCardInfo.getOilFee() == null ? 0 : oilCardInfo.getOilFee();
                    //?????????????????????????????????
                    if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                        //????????????????????????
                        oilEntityService.saveOilCardLog(oilCardInfo.getOilCardNum(), orderScheduler.getTenantId(), orderScheduler.getCarDriverId(), orderScheduler
                                , orderInfo, balance, oilCard, "0");
                    }
                }
                orderOilCardInfoService.saveOrUpdate(oilCardInfo);
            }
        } else if (verList != null && verList.size() > 0 && list != null && list.size() > 0) {
            for (OrderOilCardInfo orderOilCardInfo : list) {//?????????????????????
                if (!oilCardListVer.contains(orderOilCardInfo.getOilCardNum())
                        && (orderOilCardInfo.getCardChannel() == null || orderOilCardInfo.getCardChannel() != OrderConsts.CARD_CHANNEL.VEHICLE_BIND)) {

                    orderOilCardInfoService.removeById(orderOilCardInfo);
                }
            }
            for (OrderOilCardInfoVer orderOilCardInfoVer : verList) {
                orderOilCardInfoVer.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.YET_UPDATE);
                orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfoVer);
                if (oilCardList.contains(orderOilCardInfoVer.getOilCardNum())) {//????????????
                    List<OrderOilCardInfo> cardInfos = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderInfo.getOrderId(), orderOilCardInfoVer.getOilCardNum());
                    if (cardInfos != null && cardInfos.size() > 0) {
                        OrderOilCardInfo oilCardInfo = cardInfos.get(0);
                        if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                            oilCardInfo.setOilFee((oilCardInfo.getOilFee() == null ? 0 : oilCardInfo.getOilFee())
                                    + (orderOilCardInfoVer.getOilFee() == null ? 0 : orderOilCardInfoVer.getOilFee()));
//                            oilCardInfo.setOilFee((orderOilCardInfoVer.getOilFee() == null ? 0 : orderOilCardInfoVer.getOilFee()));
                        } else {
                            oilCardInfo.setOilFee(orderOilCardInfoVer.getOilFee() == null ? 0 : orderOilCardInfoVer.getOilFee());
                        }
                        orderOilCardInfoService.saveOrUpdate(oilCardInfo);
                    }
                } else {//??????
                    //????????????????????????
                    OrderOilCardInfo oilCardInfo = new OrderOilCardInfo();
                    BeanUtils.copyProperties(orderOilCardInfoVer, oilCardInfo);
                    oilCardInfo.setId(null);
                    orderOilCardInfoService.saveOrUpdate(oilCardInfo);
                }
                if (orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY
                        && orderOilCardInfoVer.getOilFee() != null && orderOilCardInfoVer.getOilFee() > 0) {//??????????????????????????????????????????
                    List<OilCardManagement> managements = oilCardManagementService.findByOilCardNum(orderOilCardInfoVer.getOilCardNum(), orderScheduler.getTenantId());
                    if (managements == null || managements.size() <= 0) {
                        throw new BusinessException("????????????[" + orderOilCardInfoVer.getOilCardNum() + "]?????????!");
                    }

                    OilCardManagement oilCard = managements.get(0);
                    //?????????????????????????????????
                    int cardType = oilCard.getCardType() != null ? oilCard.getCardType() : 0;
                    Long balance = orderOilCardInfoVer.getOilFee() == null ? 0 : orderOilCardInfoVer.getOilFee();
                    //?????????????????????????????????
                    if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                        //????????????????????????
                        oilEntityService.saveOilCardLog(orderOilCardInfoVer.getOilCardNum(), orderScheduler.getTenantId(), orderScheduler.getCarDriverId(), orderScheduler
                                , orderInfo, balance, oilCard, "0");
                    }
                }
            }
        }
    }

    @Override
    public OrderInfo getOrder(Long orderId) {
        LambdaQueryWrapper<OrderInfo> lambda = new QueryWrapper<OrderInfo>().lambda();
        lambda.eq(OrderInfo::getOrderId, orderId).orderByDesc(OrderInfo::getId);
        List<OrderInfo> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * ????????????????????????
     *
     * @param oilFee         ????????????
     * @param userId         ??????ID
     * @param orderId        ?????????
     * @param orderScheduler
     * @param orderInfo
     * @param orderInfoExt
     * @param orderFee
     * @param baseUser
     * @throws Exception
     */
    private void orderUpdateOilCardHandle(Long oilFee, Long userId, Long orderId, OrderScheduler orderScheduler,
                                          OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee,
                                          LoginInfo baseUser, String token) {
        if (oilFee > 0) {//??????????????????
            //???????????????????????????????????????  ???????????????????????? ??????????????????
            List<OrderOilCardInfo> list = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
            if (list != null && list.size() > 0) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    OrderOilCardInfo cardInfo = list.get(i);
                    List<OilCardManagement> managementList = oilCardManagementService.getOilCardManagementByCard(cardInfo.getOilCardNum(), cardInfo.getTenantId());
                    if (managementList != null && managementList.size() > 0) {
                        OilCardManagement management = managementList.get(0);
                        Long balance = 0L;
                        Long oilCardFee = cardInfo.getOilFee() == null ? 0 : cardInfo.getOilFee();
                        //???????????????????????????????????????
                        if (management.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {

                        } else {
                            //???????????????????????? ???????????????
                            if (oilFee <= oilCardFee) {
                                balance = oilFee;
                                cardInfo.setOilFee((cardInfo.getOilFee() == null ? 0 : cardInfo.getOilFee()) - oilFee);
                            } else {//???????????????????????? ??????????????????????????? ????????????
                                balance = oilCardFee;
                                cardInfo.setOilFee(0L);
                            }
                            orderOilCardInfoService.saveOrUpdate(cardInfo);
                        }
                        oilFee = oilFee - balance;
                        //???????????????????????????????????????
                        if (management.getCardType() != SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            oilCardManagementService.modifyOilCardBalance(cardInfo.getOilCardNum(), balance, true,
                                    orderScheduler.getPlateNumber(), orderScheduler.getCarDriverMan(),
                                    orderId, orderScheduler.getCarDriverId(), orderScheduler.getTenantId(), true, baseUser);
                        }
                    }
                }
            }
        } else if (oilFee < 0) {//??????????????????
            List<String> oilCardNums = new ArrayList<>();
            List<OrderOilCardInfoVer> list = orderOilCardInfoVerService.queryOrderOilCardInfoVerByOrderId(orderId, null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
            if (list != null && list.size() > 0) {
                //??????????????????????????????????????????????????????????????????????????????
                for (OrderOilCardInfoVer orderOilCardInfoVer : list) {
                    List<OilCardManagement> managementList = oilCardManagementService.getOilCardManagementByCard(orderOilCardInfoVer.getOilCardNum(), orderOilCardInfoVer.getTenantId());
                    if (managementList != null && managementList.size() > 0) {
                        OilCardManagement management = managementList.get(0);
                        //?????????????????????????????????  ????????????????????????????????????
                        OilCardLog oilCardLog = new OilCardLog();
                        oilCardLog.setCardId(management.getId());
                        oilCardLog.setCarDriverMan(orderScheduler.getCarDriverMan());
                        oilCardLog.setOrderId(orderId);
                        oilCardLog.setUserId(orderScheduler.getCarDriverId());
                        oilCardLog.setOilFee(orderOilCardInfoVer.getOilFee() == null ? 0 : -orderOilCardInfoVer.getOilFee());
                        oilCardLog.setTenantId(orderScheduler.getTenantId());
                        oilCardLog.setPlateNumber(orderScheduler.getPlateNumber());
                        if (management.getCardType() != SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                            oilCardLog.setLogDesc("[" + baseUser.getName() + "]??????????????????" + "?????????" + CommonUtil.getDoubleFormatLongMoney(orderOilCardInfoVer.getOilFee(), 2));
                            oilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.ADD_OR_REDUCE);
                        } else {
                            oilCardLog.setLogDesc(baseUser.getName() + "???????????????[" + management.getOilCarNum() + "]");
                            oilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.USE);
                        }
                        //????????????????????????
                        oilEntityService.saveOilCardLog(orderOilCardInfoVer.getOilCardNum(), orderScheduler.getTenantId(), orderScheduler.getCarDriverId(), orderScheduler
                                , orderInfo, orderOilCardInfoVer.getOilFee(), management, orderInfoExt.getOilAffiliation());
                    }
                    //??????????????????????????????????????????
                    List<OrderOilCardInfo> oilCardList = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, orderOilCardInfoVer.getOilCardNum());
                    if (oilCardList == null || oilCardList.size() == 0) {
                        //?????????????????????????????????????????? ????????????????????????
                        oilCardNums.add(orderOilCardInfoVer.getOilCardNum());
                    }
                    //?????????????????????
                    orderOilCardInfoVer.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.YET_UPDATE);
                    orderOilCardInfoVerService.saveOrUpdate(orderOilCardInfoVer);
                    //????????????????????????
                    OrderOilCardInfo oilCardInfo = new OrderOilCardInfo();
                    BeanUtils.copyProperties(oilCardInfo, orderOilCardInfoVer);
                    oilCardInfo.setId(null);
                    orderOilCardInfoService.saveOrUpdate(oilCardInfo);
                }
                if (oilCardNums.size() > 0) {
                    Long pledgeFee = Long
                            .parseLong(readisUtil.getSysCfg(PLEDGE_OILCARD_FEE, "0").getCfgValue());
                    //??????????????????
                    oilCardManagementService.oilPledgeHandle(orderId, userId, orderFee.getVehicleAffiliation(),
                            oilCardNums, pledgeFee, orderInfo.getTenantId(),
                            orderScheduler.getVehicleClass(), orderScheduler, baseUser, token);
                }
            } else {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
        }
    }

    /**
     * ??????????????????
     * ?????????
     *
     * @param orderListIn
     * @param accessToken
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrderListOut> getOrderList(OrderListInVo orderListIn, String accessToken, Integer pageNum, Integer pageSize) {
        List<SysStaticData> sysStaticDataList1 = readisUtil.getSysStaticDataList("AUDIT_SOURCE");
        List<SysStaticData> sysStaticDataList2 = readisUtil.getSysStaticDataList("ORDER_COST_REPORT_STATE");
        List<SysStaticData> sysStaticDataList3 = readisUtil.getSysStaticDataList("VEHICLE_STATUS@ORD");
        List<SysStaticData> sysStaticDataList4 = readisUtil.getSysStaticDataList("DRIVER_TYPE");
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        List<SysStaticData> sysStaticDataList6 = readisUtil.getSysStaticDataList("ORDER_TYPE");
        List<SysStaticData> sysStaticDataList7 = readisUtil.getSysStaticDataList("RECIVE_TYPE");
        List<SysStaticData> sysStaticDataList8 = readisUtil.getSysStaticDataList("VEHICLE_CLASS");
        List<SysStaticData> sysStaticDataList9 = readisUtil.getSysStaticDataList("ORDER_STATE");
        List<SysStaticData> sysStaticDataList10 = readisUtil.getSysStaticDataList("RECIVE_STATE");
        List<SysStaticData> sysStaticDataList11 = readisUtil.getSysStaticDataList("PAYMENT_WAY");
        List<SysStaticData> sysStaticDataList12 = readisUtil.getSysStaticDataList("GOODS_TYPE");
        List<SysStaticData> sysStaticDataList13 = readisUtil.getSysStaticDataList("VEHICLE_STATUS");

        LoginInfo baseUser = loginUtils.get(accessToken);
        List<Long> preBusiIds = new ArrayList<>();
        if (orderListIn.getCustomNumbers() != null && !orderListIn.getCustomNumbers().equals("")) {
            String[] split = orderListIn.getCustomNumbers().split("\n");
            orderListIn.setCustomNumber(Arrays.asList(split));
        }
        //????????????????????????
        boolean preTodo = false;
        boolean arriveTodo = false;
        if (orderListIn.getFinalTodo() != null && orderListIn.getFinalTodo()) {
            //  ??????????????????????????????
            List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId(), 1000);
            if (list != null && list.size() > 0) {
                preBusiIds.addAll(list);
            }
        }
        if (orderListIn.getAmountFlag() != null && orderListIn.getAmountFlag() == 99) {
            List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId(), 1000);
            if (list != null && list.size() > 0) {
                preBusiIds.addAll(list);
            }
            preTodo = true;
            orderListIn.setAmountFlag(null);
        }
        //????????????????????????
        if (orderListIn.getArrivePaymentState() != null && orderListIn.getArrivePaymentState() == 99) {
            List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId(), 1000);
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
                if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) { // ????????????
                    //  ??????????????????????????????
                    List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.PROBLEM_CODE, baseUser.getUserInfoId(), baseUser.getTenantId());
                    if (list != null && list.size() > 0) {
                        busiIds.addAll(list);
                    }
                } else if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                    if (orderListIn.getAuditSource() == null || orderListIn.getAuditSource() <= 0) {
                        List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId());
                        if (list != null && list.size() > 0) {
                            busiIds.addAll(list);
                        }
                        List<Long> list1 = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId());
                        if (list1 != null && list1.size() > 0) {
                            busiIds.addAll(list1);
                        }
                    } else {
                        if (orderListIn.getAuditSource() == OrderConsts.AUDIT_SOURCE.ABNORMAL_PRICE) {
                            List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId());
                            if (list != null && list.size() > 0) {
                                busiIds.addAll(list);
                            }
                        } else if (orderListIn.getAuditSource() == OrderConsts.AUDIT_SOURCE.UPDATE_ORDER) {
                            List<Long> list1 = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, baseUser.getUserInfoId(), baseUser.getTenantId());
                            if (list1 != null && list1.size() > 0) {
                                busiIds.addAll(list1);
                            }
                        }
                    }
                } else if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.AGING_AUDIT) {
                    List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, baseUser.getUserInfoId(), baseUser.getTenantId());
                    if (list != null && list.size() > 0) {
                        busiIds.addAll(list);
                    }
                }
            }
            orderListIn.setBusiId(busiIds);
        }
//        // ?????????????????????????????????"????????????"??????
        boolean hasAllData = sysRoleService.hasAllData(baseUser);
        orderListIn.setHasAllData(hasAllData);
//        // ??????????????????
        List<Long> orgIdList = new ArrayList<>();
        // List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);//??????????????????
//        sysMenuBtns.forEach(sysMenuBtn -> {
//            orgIdList.add(sysMenuBtn.getId());
//        });
        if (loginUtils.get(accessToken) != null) {
            orgIdList = sysOrganizeService.getSubOrgList(accessToken);
        }
        orderListIn.setOrgIdList(orgIdList);
        //  ?????????
        Page<OrderListOut> page = new Page<>(pageNum, pageSize);
        List<String> orderids = null;
        if (orderListIn.getOrderIds() == null || StringUtils.isBlank(orderListIn.getOrderIds())) {
            orderids = null;
        } else {
            orderids = Arrays.asList(orderListIn.getOrderIds().split(","));
        }
        if (orderListIn.getLineKey() != null && orderListIn.getLineKey() != "") {
            if (orderListIn.getLineKey().contains("-")) {
//                String[] split = orderListIn.getLineKey().split("-");
//                if(split.length==2){
                orderListIn.setBegin(orderListIn.getLineKey());
                // orderListIn.setEnd(split[1]);
//                }
                orderListIn.setLineKey(null);
            }
        }
        IPage<OrderListOut> orderListOut = null;

        //??????????????????????????????
        if (orderListIn.getSelectType() != null && orderListIn.getSelectType() == 2 && orderListIn.getFinalTodo()) {
            orderListIn.setFinalTodo(false);
            Page<OrderListOut> pageData = new Page<>(0, 500);
            orderListOut = baseMapper.getOrderListOut(pageData, orderListIn, orderids, baseUser.getTenantId());
            List<OrderListOut> records = orderListOut.getRecords();
            if (records != null && records.size() > 0) {
                List<OrderListOut> orderListOuts = setIsFinalAuditJurisdiction(records, baseUser);
                List<OrderListOut> collect = orderListOuts.stream()
                        .filter(a -> a.getPreAmountFlag() == 1 && a.getOrderState() > 10 && a.getIsFinalAuditJurisdiction())
                        .sorted(Comparator.comparing(OrderListOut::getCreateTime).reversed())
                        .collect(Collectors.toList());
                List<OrderListOut> orderListOuts1 = collect.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
                orderListOut.setRecords(orderListOuts1);
                orderListOut.setTotal(collect.size());
            }
        } //????????????????????????????????????
        else if (orderListIn.getSelectType() != null && orderListIn.getSelectType() == 3 && orderListIn.getTodo()) {
            Page<OrderListOut> pageData = new Page<>(0, 500);
            orderListOut = baseMapper.getOrderListOut(pageData, orderListIn, orderids, baseUser.getTenantId());
            List<OrderListOut> records = orderListOut.getRecords();
            if (records != null && records.size() > 0) {
                List<OrderListOut> collect = records.stream()
                        .filter(a -> a.getExceptionDealNum() != null && a.getExceptionDealNum() > 0)
                        .sorted(Comparator.comparing(OrderListOut::getCreateTime).reversed())
                        .collect(Collectors.toList());
                List<OrderListOut> orderListOuts1 = collect.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
                orderListOut.setRecords(orderListOuts1);
                orderListOut.setTotal(collect.size());
            }
        } else {
            orderListOut = baseMapper.getOrderListOut(page, orderListIn, orderids, baseUser.getTenantId());
        }

        List<OrderListOut> list = orderListOut.getRecords();
        List<OrderListOut> listOuts = new ArrayList<>();
//        FastDFSHelper client = FastDFSHelper.getInstance();// ????????????????????????
        List<Long> busiIdList = new ArrayList<Long>();
        for (OrderListOut orderListOut1 : list) {
            busiIdList.add(orderListOut1.getOrderId());
        }
        Map<Long, Map<String, Object>> payPreMap = new HashMap<>();
        Map<Long, Map<String, Object>> payArriveMap = new HashMap<>();
        Map<Long, Map<String, Object>> payFinalMap = new HashMap<>();

        if ((orderListIn.getIsHis() == null || orderListIn.getIsHis().intValue() <= 0)
                && orderListIn.getSelectType() != null) {
            if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER) {
                //  ??????????????????????????????
                try {
                    payPreMap = auditOutService.queryAuditRealTimeOperation(baseUser.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_PRE_FEE_CODE, busiIdList, baseUser.getTenantId());
                    payArriveMap = auditOutService.queryAuditRealTimeOperation(baseUser.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_ARRIVE_FEE_CODE, busiIdList, baseUser.getTenantId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.RECEIPT_AUDIT) {
                try {
                    payFinalMap = auditOutService.queryAuditRealTimeOperation(baseUser.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, busiIdList, baseUser.getTenantId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Map<Long, Boolean> jurisdictionMap = new ConcurrentHashMap<>();
        Map<Long, Boolean> updaJurisdictionMap = new ConcurrentHashMap<>();
        if (busiIdList.size() > 0) {
            if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) {
                //  ??????????????????????????????????????????????????????????????????
                jurisdictionMap = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.PROBLEM_CODE, busiIdList, accessToken);
            } else if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                updaJurisdictionMap = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, busiIdList, accessToken);
                jurisdictionMap = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, busiIdList, accessToken);
            }
        }
        List<OrderMainReport> costReportMaps = new ArrayList<>();
        if (busiIdList != null && busiIdList.size() > 0) {
            // ?????????????????????????????????????????????????????????????????????
            costReportMaps = iOrderCostReportService.getCostReportStateByOrderIds(busiIdList);
        }
        Map<Long, Integer> costReportMap = new ConcurrentHashMap<Long, Integer>();
        for (OrderMainReport map : costReportMaps) {
            costReportMap.put(map.getOrderId(), map.getState());
        }
        List<OrderLimitDto> finalExpireMap = new ArrayList<>();
        if (orderListIn.getIsHis() != null && orderListIn.getIsHis() > 0) {//?????????????????????
            if (busiIdList != null && busiIdList.size() > 0) {
                //   ???????????????????????????
                finalExpireMap = iOrderLimitService.hasFinalOrderLimit(busiIdList, -1);
            }
        }

        for (OrderListOut listOut : list) {
            OrderListOut out = new OrderListOut();
            BeanUtils.copyProperties(listOut, out);//????????????
            Date verifyDependDate = out.getVerifyDependDate();//??????????????????
            Date verifyStartDate = out.getVerifyStartDate();//??????????????????
            Date verifyArriverDate = out.getVerifyArriverDate();//??????????????????
            Date verifyDepartureDate = out.getVerifyDepartureDate();//??????????????????
            if (verifyDependDate != null
                    || verifyStartDate != null
                    || verifyArriverDate != null
                    || verifyDepartureDate != null) {
                out.setIsCalibration(true);
            } else {
                out.setIsCalibration(false);
            }
            //  ????????????????????????
            OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(out.getOrderId(), sysStaticDataList5);
            out.setOrderLine(orderInfoDto.getOrderLine());
            out.setIsTransitLine(orderInfoDto.getIsTransitLine());
            //  ????????????????????????
            OrderInfoDto orderInfoDto1 = orderSchedulerService.queryOrderTrackType(out.getOrderId(), sysStaticDataList5);
            Integer trackType = null;
            trackType = orderInfoDto1.getTrackType() == null || orderInfoDto1.getTrackType() < 0 ? 0 : orderInfoDto1.getTrackType();
            String trackMsg = orderInfoDto1.getTrackMas() == null ? "" : orderInfoDto1.getTrackMas();
            out.setTrackMsg(trackMsg);
            out.setTrackType(trackType);

            if (orderListIn.getIsHis() != null && orderListIn.getIsHis() > 0) {//?????????????????????
                Boolean isAddProblem = finalExpireMap.get(0).getIsAddProblem();
                out.setIsAddProblem(isAddProblem == null ? true : isAddProblem);
            } else {
                out.setIsAddProblem(true);
            }
            // ??????????????????
            Boolean isPayDriverSubsidy = false;
            if (out.getOrderUpdateState() != null
                    && out.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION) {
                out.setIsNeedAudit(true);
            } else {
                out.setIsNeedAudit(false);
            }
            if (out.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_AUDIT
                    && (out.getOrderUpdateState() == null || out.getOrderUpdateState() != OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {
                out.setAuditSource(OrderConsts.AUDIT_SOURCE.ABNORMAL_PRICE);
                out.setAuditSourceName(readisUtil.getCodeNameStr(sysStaticDataList1, out.getAuditSource().toString()));
                out.setIsJurisdiction(jurisdictionMap.get(out.getOrderId()));
            }
            if (out.getOrderUpdateState() != null
                    && (out.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION
                    || out.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {
                // ????????????
                out.setAuditSource(OrderConsts.AUDIT_SOURCE.UPDATE_ORDER);
                out.setAuditSourceName(readisUtil.getCodeNameStr(sysStaticDataList1, out.getAuditSource().toString()));
                out.setIsJurisdiction(updaJurisdictionMap.get(out.getOrderId()));
            }
            Integer costReportState = costReportMap.get(out.getOrderId()) == null ? -1 : costReportMap.get(out.getOrderId());
//            if(costReportMaps.size() == 0){
//                costReportState = -1;
//            }else if(costReportMaps.get(0).getState() == null){
//                costReportState = -1;
//            }else {
//                costReportState = costReportMap.get(0).getState();
//            }
//            Integer costReportState = costReportMap.get(0).getState() == null ? -1 : costReportMap.get(0).getState();
            if (costReportState < 0) {//?????????
                out.setCostReportStateName("?????????");
            } else {
                out.setCostReportStateName(readisUtil.getCodeNameStr(sysStaticDataList2, costReportState.toString()));
            }
            if (out.getVehicleClass() != null && out.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && out.getPaymentWay() != null && out.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.COST) {
                //  ????????????????????????
                isPayDriverSubsidy = orderDriverSubsidyService.isPayDriverSubsidy(out.getOrderId(), baseUser);
            }
            out.setIsPayDriverSubsidy(isPayDriverSubsidy);
            // ?????????????????????
            if (orderListIn.getSelectType() == null || orderListIn.getSelectType() != OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                out.setIsJurisdiction(jurisdictionMap.get(out.getOrderId()));
            }
            Integer exceptionCount = 0;
            Integer agingCount = 0;
            Integer agingAduitCount = 0;
            if (out.getOrderState() != null && out.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                //   ???????????????????????????
                List<OrderProblemInfo> problemInfos = orderProblemInfoService.getOrderProblemInfoByOrderId(out.getOrderId(), baseUser.getTenantId());
                exceptionCount = problemInfos == null ? 0 : problemInfos.size();
                //  ??????????????? ??????  ???????????? ??????????????? order_aging_info
                List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getOrderId());

                agingCount = agingInfos == null ? 0 : agingInfos.size();
                if (agingInfos != null && agingInfos.size() > 0) {
                    List<OrderAgingInfo> collect = agingInfos.stream().filter(a -> a.getAuditSts() != null &&
                            (a.getAuditSts().intValue() == SysStaticDataEnum.EXPENSE_STATE.AUDIT ||
                                    a.getAuditSts().intValue() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING)).collect(Collectors.toList());
                    agingAduitCount = collect.size();
//                    for (OrderAgingInfo orderAgingInfo : agingInfos) {
//                        if (orderAgingInfo.getAuditSts() != null
//                                && (orderAgingInfo.getAuditSts().intValue() == SysStaticDataEnum.EXPENSE_STATE.AUDIT
//                                || orderAgingInfo.getAuditSts().intValue() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING)) {
//                            agingAduitCount++;
//                        }
//                    }
                }
            }
            //????????????
            if (out.getFromOrderId() != null && out.getFromOrderId() > 0) {
                //  ??????????????? ??????  ???????????? ??????????????? order_aging_info
                List<OrderAgingInfo> agingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getFromOrderId());
                if (agingInfos != null && agingInfos.size() > 0) {
                    for (OrderAgingInfo orderAgingInfo : agingInfos) {
                        if (orderAgingInfo.getAuditSts() != null
                                && orderAgingInfo.getAuditSts().intValue() == SysStaticDataEnum.EXPENSE_STATE.END) {
                            agingCount++;
                        }
                    }
                }
            }
            if (out.getVehicleClass() != null && out.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && out.getPaymentWay() != null && out.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.COST) {
                Long driverSubsidy = 0L;//????????????????????????
                if (orderListIn.getIsHis() != null && orderListIn.getIsHis() > 0) {
                    //  ???????????????????????? ??????
                    orderDriverSubsidyService.findOrderHDriverSubSidyFee(out.getOrderId(), null, out.getCarDriverId(), out.getCopilotUserId(), OrderConsts.AMOUNT_FLAG.ALREADY_PAY);

                } else {
                    //  ????????????????????????
                    driverSubsidy = orderDriverSubsidyService.findOrderDriverSubSidyFee(out.getOrderId(), null, out.getCarDriverId(), out.getCopilotUserId(), false, OrderConsts.AMOUNT_FLAG.ALREADY_PAY);
                }
                out.setDriverSwitchSubsidy(driverSubsidy);
            }

            out.setAgingCount(agingCount);
            out.setAgingAduitCount(agingAduitCount);
            out.setExceptionCount(exceptionCount);
            //????????????

            out.setCarStatusName(out.getCarStatus() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList3, out.getCarUserType() + ""));
            //????????????
            out.setCarUserTypeName(out.getCarUserType() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList4, out.getCarUserType() + ""));
            //?????????
            out.setDesRegionName(out.getDesRegion() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList5, out.getDesRegion() + ""));
            //????????????
            out.setOrderTypeName(out.getOrderType() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList6, out.getOrderType() + ""));
            //????????????
            out.setReciveTypeName(out.getReciveType() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList7, out.getReciveType() + ""));
            //?????????

            out.setSourceRegionName(out.getSourceRegion() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList5, out.getSourceRegion() + ""));
            //???????????? ?????? ???????????????

            out.setVehicleClassName(out.getVehicleClass() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList8, out.getVehicleClass() + ""));
            //????????????
            out.setOrderStateName(out.getOrderState() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList9, out.getOrderState() + ""));
            //????????????

            out.setReciveStateName(out.getReciveState() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList10, out.getReciveState() + ""));
            //????????????
            if (out.getOrderState() != null && out.getOrderState() == 14) {
                out.setLoadState(3);
            }
            out.setLoadStateName(out.getLoadState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_STATE", out.getLoadState() + "").getCodeName());
            //??????????????????
            out.setContractUrl(
                    out.getContractUrl() == null ? "" : out.getContractUrl());
            ////????????????URL??????????????????????????????
            out.setReceiptsUrl(
                    out.getReceiptsUrl() == null ? "" : out.getReceiptsUrl());

            out.setPaymentWayName(out.getPaymentWay() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList11, out.getPaymentWay() + ""));

            out.setGoodsTypeName(out.getGoodsType() == null ? ""
                    : readisUtil.getCodeNameStr(sysStaticDataList12, out.getGoodsType() + ""));

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
                if (orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER) {
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
            if (out.getPaymentWay() != null && out.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
//                out.setPreEtcFee(DataFormat.getLongKey(map, "pontage"));
                out.setPreEtcFee(listOut.getPreEtcFee());
            }
            if (out.getCarLengh() != null && out.getCarStatus() != null && out.getCarStatus() > 0) {
                if (readisUtil.getSysStaticDatabyId("VEHICLE_LENGTH", out.getCarLengh()).getCodeName() != null) {
                    out.setCarLenghName(readisUtil.getSysStaticDatabyId("VEHICLE_LENGTH", out.getCarLengh()).getCodeName());
                } else {
                    out.setCarLenghName("");
                }
                if (readisUtil.getSysStaticDatabyId("VEHICLE_STATUS", String.valueOf(out.getCarStatus())).getCodeName() != null) {
                    out.setCarStatusName(readisUtil.getSysStaticDatabyId("VEHICLE_STATUS", String.valueOf(out.getCarStatus())).getCodeName());
                } else {
                    out.setCarStatusName("");
                }
            }
            out.setSubsidyFeeSum((out.getSalary() == null ? 0 : out.getSalary()) + (out.getCopilotSalary() == null ? 0 :
                    out.getCopilotSalary()) + (out.getDriverSwitchSubsidy() == null ? 0 : out.getDriverSwitchSubsidy()));
            //??????????????????????????????
            boolean isNeedUploadAgreement = false;
            if (orderListIn.getSelectType() != null &&
                    orderListIn.getSelectType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER) {
                //  ????????????????????????
                isNeedUploadAgreement = iOrderFeeService.isNeedUploadAgreementByLuge(out.getOrderId());
            }
            out.setIsNeedUploadAgreement(isNeedUploadAgreement);
            if (null != out.getAppointWay()) {
                if (out.getAppointWay() == 3 || out.getAppointWay() == 6) {//????????????
                    if (out.getVehicleClass() != null && out.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                        out.setOrderName("???????????????");
                    } else {
                        out.setOrderName("???????????????");
                    }
                }
                if (out.getAppointWay() == 4) {
                    out.setOrderName("???????????????");
                }
                if (out.getAppointWay() == 5) {
                    out.setOrderName("???????????????");
                }
            }
            if (out.getPaymentWay() != null && out.getPaymentWay() == 1) {
                out.setPreEtcFee(out.getPontage());
            }
            OrderPaymentDaysInfo orderPaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(out.getOrderId(), com.youming.youche.order.commons.OrderConsts.PAYMENT_DAYS_TYPE.COST);
            OrderPaymentDaysInfoH orderPaymentDaysInfoH = orderPaymentDaysInfoHService.queryOrderPaymentDaysInfoH(out.getOrderId(), com.youming.youche.order.commons.OrderConsts.PAYMENT_DAYS_TYPE.COST);
            if (orderListIn.getIsHis() != null && orderListIn.getIsHis() == 0) {//??????
                if (null != orderPaymentDaysInfo && orderPaymentDaysInfo.getBalanceType() != null && orderPaymentDaysInfo.getBalanceType() == 2) {//??????+????????????

                    OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(out.getOrderId());
                    LocalDateTime time = null;
                    if (null != orderScheduler && orderScheduler.getVerifyStartDate() != null) {
                        time = orderScheduler.getVerifyStartDate();
                    } else {
                        time = orderScheduler.getCarStartDate();
                    }
                    if (null != orderScheduler && null != time && null != orderPaymentDaysInfo.getReconciliationTime()) {
                        LocalDateTime carStartDate = time;
                        Integer reconciliationTime = orderPaymentDaysInfo.getReconciliationTime();
                        LocalDateTime dateTime = carStartDate.plusDays(reconciliationTime);
                        if (null != dateTime && (DateUtil.asDate(dateTime).getTime() < DateUtil.asDate(LocalDateTime.now()).getTime())) {
                            out.setReportState(0);
                        } else {
                            out.setReportState(1);
                        }
                    } else {
                        out.setReportState(1);
                    }
                } else {
                    out.setReportState(1);
                }

                if (null != orderPaymentDaysInfo && null != orderPaymentDaysInfo.getBalanceType() && orderPaymentDaysInfo.getBalanceType() == 3) {//??????+????????????
                    OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(out.getOrderId());
                    LocalDateTime timeData = null;
                    if (null != orderScheduler && orderScheduler.getVerifyDependDate() != null) {
                        timeData = orderScheduler.getVerifyDependDate();
                    } else {
                        timeData = orderScheduler.getCarDependDate();
                    }
                    if (null != orderScheduler && null != timeData
                            && null != orderPaymentDaysInfo.getReconciliationMonth() && null != orderPaymentDaysInfo.getReconciliationDay()) {
                        LocalDateTime carDependDate = timeData;
                        LocalDateTime dateTime = carDependDate.plusMonths(orderPaymentDaysInfo.getReconciliationMonth());
                        LocalDateTime time = dateTime.plusDays(orderPaymentDaysInfo.getReconciliationDay());
                        if (null != time && (DateUtil.asDate(time).getTime() < DateUtil.asDate(LocalDateTime.now()).getTime())) {
                            out.setReportState(0);
                        } else {
                            out.setReportState(1);
                        }
                    } else {
                        out.setReportState(1);
                    }
                } else {
                    out.setReportState(1);
                }
            }
            if (orderListIn.getIsHis() != null && orderListIn.getIsHis() == 1) {//??????
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(out.getOrderId());
                if (null != orderPaymentDaysInfoH && orderPaymentDaysInfoH.getBalanceType() != null && orderPaymentDaysInfoH.getBalanceType() == 2) {//??????+????????????
                    LocalDateTime timeData = null;
                    if (null != orderSchedulerH && orderSchedulerH.getVerifyStartDate() != null) {
                        timeData = orderSchedulerH.getVerifyStartDate();
                    } else {
                        timeData = orderSchedulerH.getCarStartDate();
                    }
                    if (null != orderSchedulerH &&
                            null != timeData &&
                            null != orderPaymentDaysInfoH.getReconciliationTime()) {
                        LocalDateTime carStartDate = timeData;
                        Integer reconciliationTime = orderPaymentDaysInfoH.getReconciliationTime();
                        LocalDateTime dateTime = carStartDate.plusDays(reconciliationTime);
                        if (null != dateTime && (DateUtil.asDate(dateTime).getTime() < DateUtil.asDate(LocalDateTime.now()).getTime())) {
                            out.setReportState(0);
                        } else {
                            out.setReportState(1);
                        }
                    } else {
                        out.setReportState(1);
                    }
                } else {
                    out.setReportState(1);
                }

                if (null != orderPaymentDaysInfoH && orderPaymentDaysInfoH.getBalanceType() != null && orderPaymentDaysInfoH.getBalanceType() == 3) {//??????+????????????
                    LocalDateTime timeData = null;
                    if (null != orderSchedulerH && orderSchedulerH.getVerifyDependDate() != null) {
                        timeData = orderSchedulerH.getVerifyDependDate();
                    } else {
                        timeData = orderSchedulerH.getCarDependDate();
                    }
                    if (null != orderSchedulerH && null != timeData
                            && null != orderPaymentDaysInfoH.getReconciliationMonth() &&
                            null != orderPaymentDaysInfoH.getReconciliationDay()) {
                        LocalDateTime carDependDate = timeData;
                        LocalDateTime dateTime = carDependDate.plusMonths(orderPaymentDaysInfoH.getReconciliationMonth());
                        LocalDateTime time = dateTime.plusDays(orderPaymentDaysInfoH.getReconciliationDay());
                        if (time != null && DateUtil.asDate(time).getTime() < DateUtil.asDate(LocalDateTime.now()).getTime()) {
                            out.setReportState(0);
                        } else {
                            out.setReportState(1);
                        }
                    } else {
                        out.setReportState(1);
                    }
                } else {
                    out.setReportState(1);
                }
            }

            listOuts.add(out);
        }
        Page<OrderListOut> p = new Page<>();
        p.setRecords(listOuts);
        p.setTotal(orderListOut.getTotal());
        p.setSize(pageSize);
        p.setCurrent(pageNum);
        return p;
    }

    /**
     * ??????IsFinalAuditJurisdiction
     *
     * @param records
     * @param baseUser
     * @return
     */
    public List<OrderListOut> setIsFinalAuditJurisdiction(List<OrderListOut> records, LoginInfo baseUser) {
        List<Long> longList = records.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
        Map<Long, Map<String, Object>> payFinalMap = auditOutService.queryAuditRealTimeOperation(baseUser.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, longList, baseUser.getTenantId());
        for (OrderListOut out : records) {
            Map<String, Object> finalMap = payFinalMap.get(out.getOrderId());
            if (finalMap != null) {
                out.setIsFinalAuditJurisdiction(DataFormat.getBooleanKey(finalMap, "isAuditJurisdiction"));

            } else {
                out.setIsFinalAuditJurisdiction(true);
            }
        }
        return records;
    }

    /**
     * ??????IsFinalAuditJurisdiction
     *
     * @param records
     * @param baseUser
     * @return
     */
    public List<OrderListWxDto> setIsFinalAuditJurisdiction1(List<OrderListWxDto> records, LoginInfo baseUser) {
        List<Long> longList = records.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
        Map<Long, Map<String, Object>> payFinalMap = auditOutService.queryAuditRealTimeOperation(baseUser.getUserInfoId(), AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, longList, baseUser.getTenantId());
        for (OrderListWxDto out : records) {
            Map<String, Object> finalMap = payFinalMap.get(out.getOrderId());
            if (finalMap != null) {
                out.setIsFinalAuditJurisdiction(DataFormat.getBooleanKey(finalMap, "isAuditJurisdiction"));

            } else {
                out.setIsFinalAuditJurisdiction(true);
            }
        }
        return records;
    }


    /**
     * ????????????????????????
     *
     * @param info      ?????????
     * @param orderInfo
     * @param scheduler
     * @param orderFee
     * @throws Exception
     */
    private void checkPaymentDays(OrderPaymentDaysInfo info, OrderInfo orderInfo, OrderScheduler scheduler, OrderFee orderFee) {
        if (info != null && info.getBalanceType() != null && info.getPaymentDaysType() != null) {
            if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL) {
                //????????????
                if (info.getPaymentDaysType().intValue() == OrderConsts.PAYMENT_DAYS_TYPE.COST) {
                    //????????????
                    if (scheduler.getAppointWay() != null && scheduler.getAppointWay().intValue() != OrderConsts.AppointWay.APPOINT_LOCAL) {
                        if (orderFee.getFinalFee() != null && orderFee.getFinalFee().intValue() > 0) {//??????
                            throw new BusinessException("????????????????????????100%?????????????????????");
                        }
                    }
                } else if (info.getPaymentDaysType().intValue() == OrderConsts.PAYMENT_DAYS_TYPE.INCOME) {
                    //????????????
                    if (orderInfo.getOrderType() != OrderConsts.OrderType.ONLINE_RECIVE) {
                        //????????????????????????
                        if ((orderFee.getAfterPayCash() != null && orderFee.getAfterPayCash() > 0)
                                || (orderFee.getAfterPayEquivalenceCardAmount() != null && orderFee.getAfterPayEquivalenceCardAmount() > 0)) {
                            throw new BusinessException("????????????????????????????????????????????????????????????");
                        }
                    }
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param orderScheduler
     * @param orderInfo
     * @throws Exception
     */
    @Override
    public void updateSettleDueDate(OrderScheduler orderScheduler, OrderInfo orderInfo, LocalDateTime reciveDate, LoginInfo user) {

        OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        OrderPaymentDaysInfo incomePaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        //????????????????????????
        LocalDateTime shouldPayDate = orderSchedulerService.getOrderReceivableDate(orderScheduler, orderInfo, costPaymentDaysInfo, reciveDate, user);
        LocalDateTime receivableDate = orderSchedulerService.getOrderReceivableDate(orderScheduler, orderInfo, incomePaymentDaysInfo, reciveDate, user);

        //??????????????????????????????
        orderFeeStatementService.updateReceiveSettleDueDate(orderInfo.getOrderId(), receivableDate);
        orderFeeStatementService.updatePaySettleDueDate(orderInfo.getOrderId(), shouldPayDate);
    }

    @Override
    public String saveExportOrders(byte[] bytes, ImportOrExportRecords records, String token) {
        InputStream is = new ByteArrayInputStream(bytes);
        int row = 0;
        int success = 0;
        List<SysStaticData> priceEnums = readisUtil.getSysStaticDataList("PRICE_ENUM");
        List<SysStaticData> pointWays = readisUtil.getSysStaticDataList("PAYMENT_WAY");
        List<SysStaticData> balanceTypes = readisUtil.getSysStaticDataList("BALANCE_TYPE");
        List<SysStaticData> billTypes = readisUtil.getSysStaticDataList("IS_NEED_BILL");
        LoginInfo loginInfo = loginUtils.get(token);
        List<Long> orgIds = this.getOrgIds(loginInfo, token);
        Long tenantId = loginInfo.getTenantId();
        StringBuilder errorInfo = new StringBuilder();
        List<OrderInfoExportDto> orderInfos = new ArrayList<OrderInfoExportDto>();
        List<OrderInfoExportDto> orderException = new ArrayList<OrderInfoExportDto>();
        String tmpLineCode = null, tmpDependTime = null, tmpIsUrgent = null, tmpPlateNumber = null, tmpUseTypeName = null, tmpStr = null;
        try {
            List<List<String>> lists = ExcelUtils.getExcelContent(is, 2, (ExcelFilesVaildate[]) null);
            //???????????????
            int size = lists.get(0).size();
            lists.remove(0);
            for (List<String> list : lists) {
                OrderInfoExportDto result = new OrderInfoExportDto();
                try {
                    if (list.size() <= 40) {
                        continue;
                    }
                    row++;
                    list.add(21, "");
                    result.setLineCodeRule(ExcelUtils.getExcelValue(list, 0));//????????????:????????????
                    result.setSourceName(ExcelUtils.getExcelValue(list, 1));//????????????
                    result.setCarDependDate(ExcelUtils.getExcelValue(list, 2));//????????????
                    result.setCmMileageNumber(ExcelUtils.getExcelValue(list, 3));//???????????????
                    result.setMileageNumber(ExcelUtils.getExcelValue(list, 4));//??????????????????
                    result.setGoodsName(ExcelUtils.getExcelValue(list, 5));//????????????
                    result.setGoodsType(ExcelUtils.getExcelValue(list, 6));//????????????
                    result.setWeight(ExcelUtils.getExcelValue(list, 7));//????????????KG
                    result.setSquare(ExcelUtils.getExcelValue(list, 8));//????????????
                    result.setReciveName(ExcelUtils.getExcelValue(list, 9));//?????????
                    result.setRecivePhone(ExcelUtils.getExcelValue(list, 10));//????????????
                    result.setIsUrgent(ExcelUtils.getExcelValue(list, 11));//???????????????
                    result.setPriceUnit(ExcelUtils.getExcelValue(list, 12));//????????????
                    result.setUnitName(ExcelUtils.getExcelValue(list, 13));//????????????
                    result.setCostPrice(ExcelUtils.getExcelValue(list, 14));//????????????
                    result.setVehicleNumber(ExcelUtils.getExcelValue(list, 15));//?????????
                    result.setCarStatus(ExcelUtils.getExcelValue(list, 16));//????????????
                    result.setTrailerPlate(ExcelUtils.getExcelValue(list, 17));//????????????
                    result.setMainDriverPhone(ExcelUtils.getExcelValue(list, 18));//??????????????????
                    result.setCopilotPhone(ExcelUtils.getExcelValue(list, 19));//???????????????
                    result.setPaymentWay(ExcelUtils.getExcelValue(list, 20));//????????????
//                    result.setIsNeedBill(ExcelUtils.getExcelValue(list, 21));//????????????
                    result.setRunOil(ExcelUtils.getExcelValue(list, 22));//????????????
                    result.setCapacityOil(ExcelUtils.getExcelValue(list, 23));//????????????
                    result.setVirtualAmount(ExcelUtils.getExcelValue(list, 24));//????????????
                    result.setOilAccountType(ExcelUtils.getExcelValue(list, 25));//???????????????
                    result.setCashFee(ExcelUtils.getExcelValue(list, 26));//????????????
                    result.setCbPreCashFee(ExcelUtils.getExcelValue(list, 27));//????????????
                    result.setPreOilVirtualFee(ExcelUtils.getExcelValue(list, 28));//??????????????????
                    result.setEntityOilFee(ExcelUtils.getExcelValue(list, 29));//??????????????????
                    result.setEtcFee(ExcelUtils.getExcelValue(list, 30));//ETC???
                    result.setArriveFee(ExcelUtils.getExcelValue(list, 31));//?????????
                    result.setBalance(ExcelUtils.getExcelValue(list, 32));//??????
                    result.setDistributionOilSource(ExcelUtils.getExcelValue(list, 33));//???????????????
                    result.setLoadEmptyOilCost(ExcelUtils.getExcelValue(list, 34));//????????????
                    result.setIntelligentCapacityOil(ExcelUtils.getExcelValue(list, 35));//????????????
                    result.setBridgeToll(ExcelUtils.getExcelValue(list, 36));//?????????
                    result.setEmptyDistance(ExcelUtils.getExcelValue(list, 37));//????????????
                    result.setOilLitreTotal(ExcelUtils.getExcelValue(list, 38));//????????????
                    result.setZnOilStationName1(ExcelUtils.getExcelValue(list, 39));//????????????1
                    result.setZnOilStationAddress1(ExcelUtils.getExcelValue(list, 40));//????????????1
                    result.setOilRise1(ExcelUtils.getExcelValue(list, 41));//????????????1
                    result.setZnOilStationName2(ExcelUtils.getExcelValue(list, 42));//????????????2
                    result.setZnOilStationAddress2(ExcelUtils.getExcelValue(list, 43));//????????????2
                    result.setOilRise2(ExcelUtils.getExcelValue(list, 44));//????????????2
                    result.setZnOilStationName3(ExcelUtils.getExcelValue(list, 45));//????????????3
                    result.setZnOilStationAddress3(ExcelUtils.getExcelValue(list, 46));//????????????3
                    result.setOilRise3(ExcelUtils.getExcelValue(list, 47));//????????????3
                    result.setIntelligentOilAccountType(ExcelUtils.getExcelValue(list, 48));//???????????????
                    result.setZnOilcardLiters(ExcelUtils.getExcelValue(list, 49));//????????????
                    result.setZnOilPrice(ExcelUtils.getExcelValue(list, 50));//????????????
                    result.setTotalFee(ExcelUtils.getExcelValue(list, 51));//?????????
                    result.setPreCashFee(ExcelUtils.getExcelValue(list, 52));//??????????????????
                    result.setPreOilVirtualFee2(ExcelUtils.getExcelValue(list, 53));//??????????????????
                    result.setCbOilcardMoneyEntity(ExcelUtils.getExcelValue(list, 54));//????????????
                    result.setIntelligentArriveFee(ExcelUtils.getExcelValue(list, 55));//?????????
                    result.setFinalFee(ExcelUtils.getExcelValue(list, 56));//????????????
                    result.setIntelligentoilAccountType1(ExcelUtils.getExcelValue(list, 57));//???????????????
                    result.setCollectionUserPhone(ExcelUtils.getExcelValue(list, 58));//???????????????
                    result.setCollectionUserName(ExcelUtils.getExcelValue(list, 59));//???????????????
                    result.setBalanceType(ExcelUtils.getExcelValue(list, 60));//????????????
                    result.setReciveDay(ExcelUtils.getExcelValue(list, 61));//???????????????
                    result.setReconciliationTime(ExcelUtils.getExcelValue(list, 62));//????????????
                    result.setBillPeriod(ExcelUtils.getExcelValue(list, 63));//????????????
                    result.setPayPeriod(ExcelUtils.getExcelValue(list, 64));//????????????
                    result.setStateNamePhone(ExcelUtils.getExcelValue(list, 65));//??????????????????
                    result.setAttachedOrgName(ExcelUtils.getExcelValue(list, 66));//????????????
                    orderException.add(result);
                    if (StringUtils.isBlank(tmpLineCode = ExcelUtils.getExcelValue(list, 0))) {
                        errorInfo.append("???????????????????????????");

                    }
                    if (!tmpLineCode.startsWith("L")) {
                        errorInfo.append("????????????????????????");

                    }
                    if (StringUtils.isBlank(tmpDependTime = ExcelUtils.getExcelValue(list, 2))) {
                        errorInfo.append("?????????????????????????????????");

                    }
                    if (!"???".equals(tmpIsUrgent = ExcelUtils.getExcelValue(list, 11)) && !"???".equals(tmpIsUrgent)) {
                        errorInfo.append("??????????????????????????????????????????");

                    }
                    OrderInfo orderInfo = new OrderInfo();
                    OrderInfoExt orderInfoExt = new OrderInfoExt();
                    OrderFee orderFee = new OrderFee();
                    OrderGoods orderGoods = new OrderGoods();
                    OrderFeeExt orderFeeExt = new OrderFeeExt();
                    OrderScheduler orderScheduler = new OrderScheduler();
                    OrderPaymentDaysInfo incomePaymentDaysInfo = new OrderPaymentDaysInfo();
                    OrderPaymentDaysInfo dispatchPaymentDaysInfo = new OrderPaymentDaysInfo();
                    List<OrderOilDepotScheme> orderOilDepotSchemeList = new ArrayList<>();
                    List<OrderTransitLineInfo> orderTransitLineInfos = new ArrayList<OrderTransitLineInfo>();//?????????
                    orderInfo.setTenantId(tenantId);
                    orderInfoExt.setTenantId(tenantId);
                    orderFee.setTenantId(tenantId);
                    orderFeeExt.setTenantId(tenantId);
                    orderScheduler.setTenantId(tenantId);
                    incomePaymentDaysInfo.setTenantId(tenantId);
                    if (!this.getLineInfoByLineCode(tmpLineCode, orderInfo, orderInfoExt, orderGoods, orderFee
                            , orderFeeExt, orderScheduler, incomePaymentDaysInfo,
                            orderTransitLineInfos, orgIds, loginInfo)) {
                        errorInfo.append("?????????????????????[" + tmpLineCode + "]????????????????????????????????????????????????");


                    }
                    orderScheduler.setDependTime(cn.hutool.core.date.LocalDateTimeUtil.of(this.checkDependTimeFormat(tmpDependTime)));
                    orderScheduler.setIsUrgent("???".equals(tmpIsUrgent) ? 1 : 0);
                    if (StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 3)) &&
                            !CommonUtil.isDouble(ExcelUtils.getExcelValue(list, 3)) && !CommonUtil.isLong(ExcelUtils.getExcelValue(list, 3))) {
                        errorInfo.append("????????????????????????????????????");

                    } else if (list.size() > 3 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 3))) {
                        orderScheduler.setDistance((long) (Double.parseDouble(ExcelUtils.getExcelValue(list, 3)) * 1000));
                    }
                    if (StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 4))) {
                        Number v = Double.parseDouble(ExcelUtils.getExcelValue(list, 4)) * 1000;
                        orderScheduler.setMileageNumber(v.intValue());
                    }
                    if (StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 5))) {
                        orderGoods.setGoodsName(ExcelUtils.getExcelValue(list, 5));
                    }
                    if (StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 6))) {
                        if ("??????".equals(String.valueOf(ExcelUtils.getExcelValue(list, 6)))) {
                            orderGoods.setGoodsType(SysStaticDataEnum.GOODS_TYPE.COMMON_GOODS);
                        } else if ("?????????".equals(String.valueOf(ExcelUtils.getExcelValue(list, 6)))) {
                            orderGoods.setGoodsType(SysStaticDataEnum.GOODS_TYPE.DANGER_GOODS);
                        } else {
                            errorInfo.append("????????????????????????");

                        }
                    }
                    if (list.size() > 6 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 7))) {
                        if (!CommonUtil.isDouble(ExcelUtils.getExcelValue(list, 7)) && !CommonUtil.isLong(ExcelUtils.getExcelValue(list, 7))) {
                            errorInfo.append("?????????????????????????????????");

                        }
                        orderGoods.setWeight(Float.valueOf(ExcelUtils.getExcelValue(list, 7)));
                    }

                    if (list.size() > 8 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 8))) {
                        if (!CommonUtil.isDouble(ExcelUtils.getExcelValue(list, 8)) && !CommonUtil.isLong(ExcelUtils.getExcelValue(list, 8))) {
                            errorInfo.append("?????????????????????????????????");

                        }
                        orderGoods.setSquare(Float.valueOf(ExcelUtils.getExcelValue(list, 8)));
                    }
                    if (list.size() > 9 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 9))) {
                        orderGoods.setReciveName(ExcelUtils.getExcelValue(list, 9));
                    }
                    if (list.size() > 10 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 10))) {
                        orderGoods.setRecivePhone(ExcelUtils.getExcelValue(list, 10));
                    }
                    if (list.size() > 12 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 12))) {
                        if (!CommonUtil.isDouble(ExcelUtils.getExcelValue(list, 12)) && !CommonUtil.isLong(ExcelUtils.getExcelValue(list, 12))) {
                            errorInfo.append("?????????????????????????????????");

                        }
                        orderFee.setPriceUnit(Double.valueOf(ExcelUtils.getExcelValue(list, 12)));
                    }
                    if (list.size() > 13 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 13))) {
                        int priceEnum = this.getCodeByName(priceEnums, ExcelUtils.getExcelValue(list, 13));
                        if (priceEnum < 0) {
                            errorInfo.append("????????????????????????");

                        }
                        orderFee.setPriceEnum(priceEnum);
                    }
                    if (list.size() > 14 && StringUtils.isNotBlank(ExcelUtils.getExcelValue(list, 14))) {
                        if (!CommonUtil.isDouble(ExcelUtils.getExcelValue(list, 14)) && !CommonUtil.isLong(ExcelUtils.getExcelValue(list, 14))) {
                            errorInfo.append("?????????????????????????????????");

                        }
                        orderFee.setCostPrice(OrderUtil.objToLongMul100(ExcelUtils.getExcelValue(list, 14)));
                    } else {
                        this.calCostPrice(orderFee, orderGoods, orderScheduler);
                    }
                    //???????????????????????????
                    if (incomePaymentDaysInfo.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL) {
                        orderFee.setPrePayCash(orderFee.getCostPrice());
                    } else {
                        orderFee.setPrePayCash(0L);
                    }
                    orderFee.setAfterPayCash(orderFee.getCostPrice() - orderFee.getPrePayCash());

                    //????????????
//                    if (list.size() < 22 || StringUtils.isBlank(tmpStr = ExcelUtils.getExcelValue(list, 21))) {
//                        errorInfo.append("????????????????????????");
//
//                    }
//                    int isNeedBill = this.getCodeByName(billTypes, tmpStr.trim());
//                    if (isNeedBill < 0) {
//                        errorInfo.append("???????????????????????????");
//
//                    }
                    orderInfo.setIsNeedBill(0);
                    //????????????
                    if (list.size() > 15 && list.get(15) != null && StringUtils.isNotBlank(tmpPlateNumber = list.get(15).trim())) {

                        if (list.size() < 17 || StringUtils.isBlank(list.get(16))) {
                            errorInfo.append("????????????????????????");

                        }

                        orderScheduler.setAppointWay(com.youming.youche.finance.constant.OrderConsts.AppointWay.APPOINT_CAR);
                        this.getVehicleInfoByPlateNumber(tmpPlateNumber, orderScheduler, orderInfo, loginInfo);
                        Integer vehicleClass = orderScheduler.getVehicleClass();
                        String codeName = readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass.toString()).getCodeName();
                        if (!codeName.equals(list.get(16).trim())) {
                            errorInfo.append("?????????????????????????????????????????????");

                        }

                        //?????????
                        if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                            if (orderScheduler.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT && (list.size() < 18 || StringUtils.isBlank(list.get(17)))) {
                                errorInfo.append("???????????????????????????????????????");


                            }
                            if (list.size() < 19 || StringUtils.isBlank(list.get(18))) {
                                errorInfo.append("???????????????????????????????????????");


                            }
                            //??????
                            if (list.size() > 17 && StringUtils.isNotBlank(list.get(17))) {
                                this.getTrailerInfo(list.get(17).trim(), orderScheduler, loginInfo.getTenantId());
                            }

                            //??????
                            if (list.size() > 18 && StringUtils.isNotBlank(list.get(18))) {
                                this.getDriverbyPhone(list.get(18).trim(), true, orderScheduler, loginInfo);
                            }
                            //??????
                            if (list.size() > 19 && StringUtils.isNotBlank(list.get(19))) {
                                this.getDriverbyPhone(list.get(19).trim(), false, orderScheduler, loginInfo);
                            }
                            if (list.size() < 21 || StringUtils.isBlank(list.get(20))) {
                                errorInfo.append("?????????????????????????????????");


                            }
                            int pointWay = this.getCodeByName(pointWays, list.get(20).trim());
                            if (pointWay < 0) {
                                errorInfo.append("????????????[" + list.get(20) + "]????????????");


                            }
                            orderInfoExt.setPaymentWay(pointWay);


                            //????????????
                            if (pointWay == com.youming.youche.order.commons.OrderConsts.PAYMENT_WAY.EXPENSE) {
                                if (list.size() > 22 && StringUtils.isNotBlank(list.get(22))) {
                                    if (!CommonUtil.isDouble(list.get(22).trim()) && !CommonUtil.isLong(list.get(22).trim())) {
                                        errorInfo.append("?????????????????????????????????????????????");


                                    }
                                    orderInfoExt.setCapacityOil(Float.valueOf(list.get(22).trim()) * 100);
                                }
                                if (list.size() > 23 && StringUtils.isNotBlank(list.get(23))) {
                                    if (!CommonUtil.isDouble(list.get(23).trim()) && !CommonUtil.isLong(list.get(23).trim())) {
                                        errorInfo.append("?????????????????????????????????????????????");


                                    }
                                    orderInfoExt.setRunOil(Float.valueOf(list.get(23).trim()) * 100);
                                }
                                if (list.size() > 23 && StringUtils.isNotBlank(list.get(24))) {
                                    if (!CommonUtil.isDouble(list.get(24).trim()) && !CommonUtil.isLong(list.get(24).trim())) {
                                        errorInfo.append("??????????????????????????????????????????");


                                    }
                                    orderFee.setPreOilVirtualFee(OrderUtil.objToLongMul100(list.get(24).trim()));
                                } else {
                                    orderFee.setPreOilVirtualFee(0L);
                                }
                                if (orderFee.getPreOilVirtualFee() > 0 && (list.size() < 26 || StringUtils.isBlank(tmpUseTypeName = list.get(25)))) {
                                    errorInfo.append("???????????????0???????????????????????????");


                                } else if (list.size() > 25 && StringUtils.isNotBlank(tmpUseTypeName = list.get(25).trim())) {
                                    if (tmpUseTypeName.equals("????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
                                    } else if (tmpUseTypeName.equals("???????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
                                    } else if (tmpUseTypeName.equals("????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
                                    } else {
                                        errorInfo.append("????????????-???????????????????????????");


                                    }
                                }

                                orderFee.setPreOilFee(0L);
                                orderFeeExt.setEstFee(orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee());
                            }
                            //????????????
                            else if (pointWay == com.youming.youche.order.commons.OrderConsts.PAYMENT_WAY.COST) {
                                if (list.size() > 34 && StringUtils.isNotBlank(tmpStr = list.get(34).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????????????????");


                                    }
                                    orderInfoExt.setCapacityOil(Float.valueOf(tmpStr) * 100);
                                }
                                if (list.size() > 35 && StringUtils.isNotBlank(tmpStr = list.get(35).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????????????????");


                                    }
                                    orderInfoExt.setRunOil(Float.valueOf(tmpStr) * 100);
                                }
                                //????????????????????????????????????????????????
                                if (list.size() > 36 && StringUtils.isNotBlank(tmpStr = list.get(36).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("??????????????????????????????????????????");


                                    }
                                    orderFeeExt.setPontagePer(OrderUtil.objToLongMul100(tmpStr));
                                }
                                //????????????????????????????????????????????????
                                if (list.size() > 37 && StringUtils.isNotBlank(tmpStr = list.get(37).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????????????????");


                                    }
                                    orderInfoExt.setRunWay(OrderUtil.mul(tmpStr, "1000"));
                                }

                                //??????1
                                if (list.size() > 39 && StringUtils.isNotBlank(tmpStr = list.get(39).trim())) {
                                    this.getOilProductByName(tmpStr, orderScheduler.getSourceId(),
                                            orderOilDepotSchemeList, orderGoods, orderInfoExt.getOilIsNeedBill(), loginInfo);
                                    if (list.size() < 42 || StringUtils.isBlank(tmpStr = list.get(41).trim())) {
                                        errorInfo.append("????????????1???????????????");


                                    }
                                    orderOilDepotSchemeList.get(0).setOilDepotLitre(OrderUtil.objToLongMul100(tmpStr));
                                    orderOilDepotSchemeList.get(0).setOilDepotFee(
                                            (long) (Double.valueOf(tmpStr) * orderOilDepotSchemeList.get(0).getOilDepotPrice()));
                                }
                                //??????2
                                if (list.size() > 42 && StringUtils.isNotBlank(tmpStr = list.get(42).trim())) {
                                    this.getOilProductByName(tmpStr, orderScheduler.getSourceId(), orderOilDepotSchemeList,
                                            orderGoods, orderInfoExt.getOilIsNeedBill(), loginInfo);
                                    if (list.size() < 45 || StringUtils.isBlank(tmpStr = list.get(44).trim())) {
                                        errorInfo.append("????????????2???????????????");


                                    }
                                    orderOilDepotSchemeList.get(1).setOilDepotLitre(OrderUtil.objToLongMul100(tmpStr));
                                    orderOilDepotSchemeList.get(1).setOilDepotFee(
                                            (long) (Double.valueOf(tmpStr) * orderOilDepotSchemeList.get(1).getOilDepotPrice()));
                                }
                                //??????3
                                if (list.size() > 45 && StringUtils.isNotBlank(tmpStr = list.get(45).trim())) {
                                    this.getOilProductByName(tmpStr, orderScheduler.getSourceId(),
                                            orderOilDepotSchemeList, orderGoods, orderInfoExt.getOilIsNeedBill(), loginInfo);
                                    if (list.size() < 48 || StringUtils.isBlank(tmpStr = list.get(47).trim())) {
                                        errorInfo.append("????????????3???????????????");


                                    }
                                    orderOilDepotSchemeList.get(2).setOilDepotLitre(OrderUtil.objToLongMul100(tmpStr));
                                    orderOilDepotSchemeList.get(2).setOilDepotFee(
                                            (long) (Double.valueOf(tmpStr) * orderOilDepotSchemeList.get(2).getOilDepotPrice()));
                                }

                                if (list.size() > 50 && StringUtils.isNotBlank(tmpStr = list.get(50).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????????????????");


                                    }
                                    orderFeeExt.setOilPrice(OrderUtil.objToLongMul100(tmpStr));
                                }

                                //????????????
                                this.getFee(orderInfo, orderInfoExt, orderFee, orderFeeExt,
                                        orderScheduler, orderGoods, orderTransitLineInfos.size(), loginInfo);

                                //?????????
                                orderFeeExt.setOilLitreVirtual(0L);
                                orderFee.setPreOilVirtualFee(0L);
                                for (OrderOilDepotScheme item : orderOilDepotSchemeList) {
                                    orderFeeExt.setOilLitreVirtual(orderFeeExt.getOilLitreVirtual() + item.getOilDepotLitre());
                                    orderFee.setPreOilVirtualFee(orderFee.getPreOilVirtualFee() + (long) (item.getOilDepotLitre() / 100.0 * item.getOilDepotPrice()));
                                }

                                //????????????????????????????????????????????????
                                if (list.size() > 38 && StringUtils.isNotBlank(tmpStr = list.get(38).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("????????????????????????[" + tmpStr + "]?????????????????????");


                                    }
                                    orderFeeExt.setOilLitreTotal(OrderUtil.objToLongMul100(tmpStr));
                                }

                                if (orderFeeExt.getOilLitreVirtual() > 0 && (list.size() < 49 || StringUtils.isBlank(tmpStr = list.get(48).trim()))) {
                                    errorInfo.append("??????????????????????????????0???????????????????????????");


                                } else if (list.size() > 48 && StringUtils.isNotBlank(tmpStr = list.get(48).trim())) {
                                    if (tmpStr.equals("????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
                                    } else if (tmpStr.equals("???????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
                                    } else if (tmpStr.equals("????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
                                    } else {
                                        errorInfo.append("????????????-???????????????????????????");


                                    }
                                }

                                if (list.size() > 49 && StringUtils.isNotBlank(tmpStr = list.get(49).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("????????????????????????[" + tmpStr + "]?????????????????????");


                                    }
                                    orderFeeExt.setOilLitreEntity(OrderUtil.objToLongMul100(tmpStr));
                                } else {
                                    if (orderFeeExt.getOilLitreTotal() == null) {
                                        orderFeeExt.setOilLitreTotal(0L);
                                    }
                                    if (orderFeeExt.getOilLitreTotal() >= orderFeeExt.getOilLitreVirtual()) {
                                        orderFeeExt.setOilLitreEntity(orderFeeExt.getOilLitreTotal() - orderFeeExt.getOilLitreVirtual());
                                    } else {
                                        errorInfo.append("?????????????????????[" + (orderFeeExt.getOilLitreVirtual() / 100.0) + "]????????????"
                                                + "??????????????????[" + (orderFeeExt.getOilLitreTotal() / 100.0) + "]???");


                                    }
                                }

                                if (orderFeeExt.getOilLitreTotal() != orderFeeExt.getOilLitreVirtual() + orderFeeExt.getOilLitreEntity()) {
                                    errorInfo.append("????????????????????????[" + (orderFeeExt.getOilLitreTotal() / 100.0) + "]?????????"
                                            + "???????????????[" + (orderFeeExt.getOilLitreVirtual() / 100.0) + "]????????????[" + (orderFeeExt.getOilLitreEntity() / 100.0) + "]?????????");


                                }
                                //???????????????
                                orderFee.setPreOilFee((long) Math.round((orderFeeExt.getOilLitreEntity() * orderFeeExt.getOilPrice() / 100.0)));
                                //????????????
                                orderFeeExt.setEstFee(orderFeeExt.getPontage() + orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee()
                                        + orderFeeExt.getSalary() + orderFeeExt.getCopilotSalary());
                            }
                            //????????????
                            else if (pointWay == com.youming.youche.order.commons.OrderConsts.PAYMENT_WAY.CONTRACT) {


                                if (list.size() > 26 && StringUtils.isNotBlank(tmpStr = list.get(26).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("??????????????????????????????");


                                    }
                                    orderFee.setTotalFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setTotalFee(0L);
                                }
                                if (list.size() > 27 && StringUtils.isNotBlank(tmpStr = list.get(27).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????");


                                    }
                                    orderFee.setPreCashFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setPreCashFee(0L);
                                }
                                if (list.size() > 28 && StringUtils.isNotBlank(tmpStr = list.get(28).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????");


                                    }
                                    orderFee.setPreOilVirtualFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setPreOilVirtualFee(0L);
                                }
                                if (list.size() > 29 && StringUtils.isNotBlank(tmpStr = list.get(29).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("?????????????????????????????????");


                                    }
                                    orderFee.setPreOilFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setPreOilFee(0L);
                                }
                                if (list.size() > 30 && StringUtils.isNotBlank(tmpStr = list.get(30).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("ETC?????????????????????");


                                    }
                                    orderFee.setPreEtcFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setPreEtcFee(0L);
                                }
                                if (list.size() > 31 && StringUtils.isNotBlank(tmpStr = list.get(31).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("??????????????????????????????");


                                    }
                                    orderFee.setArrivePaymentFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setArrivePaymentFee(0L);
                                }
                                if (list.size() > 32 && StringUtils.isNotBlank(tmpStr = list.get(32).trim())) {
                                    if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                        errorInfo.append("???????????????????????????");


                                    }
                                    orderFee.setFinalFee(CommonUtil.multiply(tmpStr));
                                } else {
                                    orderFee.setFinalFee(0L);
                                }

                                orderFee.setInsuranceFee((long) (this.getInsumeFee(3, orderFee, loginInfo)));
                                orderFee.setPreTotalFee(orderFee.getPreCashFee() + orderFee.getPreEtcFee() + orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee());
                                if (orderFee.getFinalFee() < orderFee.getInsuranceFee()) {
                                    errorInfo.append("?????????????????????????????????");


                                }
                                if (orderFee.getTotalFee() != orderFee.getPreTotalFee() + orderFee.getArrivePaymentFee() + orderFee.getFinalFee()) {
                                    errorInfo.append("??????????????????????????????????????????????????????");


                                }
                                if (orderFee.getTotalFee() > 0) {
                                    orderFee.setPreTotalScale((long) (orderFee.getPreTotalFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                    orderFee.setFinalScale((long) (orderFee.getFinalFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                    orderFee.setPreOilVirtualScale((long) (orderFee.getPreOilVirtualFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                    orderFee.setPreEtcScale((long) (orderFee.getPreEtcFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                    orderFee.setPreCashScale((long) (orderFee.getPreCashFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                    orderFee.setArrivePaymentFeeScale((long) (orderFee.getArrivePaymentFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                } else {
                                    orderFee.setPreTotalScale(orderFee.getPreTotalScaleStandard());
                                    orderFee.setFinalScale(100 - orderFee.getPreTotalScaleStandard());
                                    orderFee.setPreOilVirtualScale(orderFee.getPreOilVirtualScaleStandard());
                                    orderFee.setPreEtcScale(orderFee.getPreEtcScaleStandard());
                                    orderFee.setArrivePaymentFeeScale(0L);
                                    orderFee.setPreCashScale(orderFee.getPreCashScaleStandard());
                                }
                                orderFee.setPreOilScale(orderFee.getPreTotalScale() - orderFee.getPreOilVirtualScale() - orderFee.getPreCashScale() - orderFee.getPreEtcScale());

                                if (orderFee.getPreOilVirtualFee() > 0 && (list.size() < 34 || StringUtils.isBlank(tmpStr = list.get(33).trim()))) {
                                    errorInfo.append("???????????????????????????0???????????????????????????");


                                } else if (list.size() > 33 && StringUtils.isNotBlank(tmpStr = list.get(33).trim())) {
                                    if (tmpStr.equals("????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
                                    } else if (tmpStr.equals("???????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
                                    } else if (tmpStr.equals("????????????")) {
                                        orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
                                    } else {
                                        errorInfo.append("????????????-???????????????????????????");


                                    }
                                }
                                if (orderFee.getGuidePrice() == null) {
                                    orderFee.setGuidePrice(orderFee.getTotalFee());
                                }
                            }
                        }
                        //?????????
                        else if (list.size() > 16 && StringUtils.isNotBlank(list.get(16))) {
                            if (!"???????????????".equals(list.get(20).trim())
                                    && !"???????????????".equals(list.get(20).trim())
                                    && !"???????????????".equals(list.get(20).trim())) {
                                errorInfo.append("????????????[" + list.get(20) + "]????????????");


                            }
                            String vehicleClassName = list.get(20).trim();
                            //??????
                            if (list.size() > 18 && StringUtils.isNotBlank(tmpStr = list.get(18).trim())) {
                                if (!tmpStr.equals(orderScheduler.getCarDriverPhone())) {
                                    this.getDriverbyPhone(tmpStr, true, orderScheduler, loginInfo);
                                }
                                //this.getDriverbyPhone(tmpStr, true, orderScheduler);
                            } else {
                                errorInfo.append("?????????????????????????????????");
                            }

                            if (list.size() > 51 && StringUtils.isNotBlank(tmpStr = list.get(51).trim())) {
                                if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                    errorInfo.append("??????????????????????????????");


                                }
                                orderFee.setTotalFee(OrderUtil.objToLongMul100(tmpStr));
                            } else {
                                orderFee.setTotalFee(orderFee.getGuidePrice() != null ? orderFee.getGuidePrice() : 0L);
                            }
                            //??????  ?????????????????????5???????????????
                            Integer type = null;
                            if (orderScheduler.getVehicleClass() == 3) {
                                type = 5;
                            } else {
                                type = orderScheduler.getVehicleClass();
                            }
                            orderFee.setInsuranceFee((long) (this.getInsumeFee(type, orderFee, loginInfo)));

                            if (list.size() > 52 && StringUtils.isNotBlank(tmpStr = list.get(52).trim())) {
                                if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                    errorInfo.append(vehicleClassName + "?????????????????????????????????");


                                }
                                orderFee.setPreCashFee(OrderUtil.objToLongMul100(tmpStr));
                            } else {
                                if (orderFee.getTotalFee() > 0) {
                                    orderFee.setPreCashFee((long) (orderFee.getTotalFee() * orderFee.getPreCashScaleStandard() / 100.0));
                                } else {
                                    orderFee.setPreCashFee(0L);
                                }
                            }
                            if (list.size() > 53 && StringUtils.isNotBlank(tmpStr = list.get(53).trim())) {
                                if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                    errorInfo.append(vehicleClassName + "?????????????????????????????????");


                                }
                                orderFee.setPreOilVirtualFee(OrderUtil.objToLongMul100(tmpStr));
                            } else {
                                if (orderFee.getTotalFee() > 0) {
                                    orderFee.setPreOilVirtualFee((long) (orderFee.getTotalFee() * orderFee.getPreOilVirtualScaleStandard() / 100.0));
                                } else {
                                    orderFee.setPreOilVirtualFee(0L);
                                }
                            }
                            if (list.size() > 54 && StringUtils.isNotBlank(tmpStr = list.get(54).trim())) {
                                if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                    errorInfo.append(vehicleClassName + "?????????????????????????????????");


                                }
                                orderFee.setPreOilFee(OrderUtil.objToLongMul100(tmpStr));
                            } else {
                                if (orderFee.getTotalFee() > 0) {
                                    orderFee.setPreOilFee((long) (orderFee.getTotalFee() * orderFee.getPreOilScaleStandard() / 100.0));
                                } else {
                                    orderFee.setPreOilFee(0L);
                                }
                            }
                            orderFee.setPreEtcFee(0L);

                            orderFee.setPreTotalFee(orderFee.getPreCashFee() + orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee() + orderFee.getPreEtcFee());

                            if (list.size() > 55 && StringUtils.isNotBlank(tmpStr = list.get(55).trim())) {
                                if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                    errorInfo.append(vehicleClassName + "??????????????????????????????");


                                }
                                orderFee.setArrivePaymentFee(OrderUtil.objToLongMul100(tmpStr));
                            } else {
                                orderFee.setArrivePaymentFee(0L);
                            }

                            if (list.size() > 56 && StringUtils.isNotBlank(tmpStr = list.get(56).trim())) {
                                if (!CommonUtil.isDouble(tmpStr) && !CommonUtil.isLong(tmpStr)) {
                                    errorInfo.append(vehicleClassName + "???????????????????????????");


                                }
                                orderFee.setFinalFee(OrderUtil.objToLongMul100(tmpStr));
                            } else {
                                if (orderFee.getTotalFee() > 0) {
                                    orderFee.setFinalFee(orderFee.getTotalFee() - orderFee.getArrivePaymentFee() - orderFee.getPreTotalFee());
                                } else {
                                    orderFee.setFinalFee(0L);
                                }
                            }

                            if (orderFee.getFinalFee() < orderFee.getInsuranceFee()) {
                                errorInfo.append(vehicleClassName + "?????????????????????????????????");


                            }

                            if (orderFee.getTotalFee() != (orderFee.getPreTotalFee() + orderFee.getArrivePaymentFee() + orderFee.getFinalFee())) {
                                errorInfo.append(vehicleClassName + "??????????????????????????????????????????????????????");


                            }
                            if (orderFee.getTotalFee() > 0) {
                                orderFee.setPreTotalScale((long) (orderFee.getPreTotalFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                orderFee.setFinalScale((long) (orderFee.getFinalFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                orderFee.setPreOilVirtualScale((long) (orderFee.getPreOilVirtualFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                orderFee.setPreEtcScale((long) (orderFee.getPreEtcFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                orderFee.setPreCashScale((long) (orderFee.getPreCashFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                                orderFee.setArrivePaymentFeeScale((long) (orderFee.getArrivePaymentFee() * 1.0 / orderFee.getTotalFee() * 100 * 100));
                            } else {
                                orderFee.setPreTotalScale(orderFee.getPreTotalScaleStandard());
                                orderFee.setFinalScale(100 - orderFee.getPreTotalScaleStandard());
                                orderFee.setPreOilVirtualScale(orderFee.getPreOilVirtualScaleStandard());
                                orderFee.setArrivePaymentFeeScale(0L);
                                orderFee.setPreEtcScale(orderFee.getPreEtcScaleStandard());
                                orderFee.setPreCashScale(orderFee.getPreCashScaleStandard());
                            }
                            orderFee.setPreOilScale(orderFee.getPreTotalScale() - orderFee.getPreOilVirtualScale() - orderFee.getPreCashScale() - orderFee.getPreEtcScale());
                            //???????????????????????????????????????
                            if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill() == com.youming.youche.order.commons.OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                                if (orderFee.getPreOilVirtualFee() != null && orderFee.getPreOilVirtualFee() > 0) {
                                    errorInfo.append("?????????????????????????????????????????????????????????????????????");


                                }
                                orderFeeExt.setOilConsumer(com.youming.youche.order.commons.OrderConsts.OIL_CONSUMER.SHARE);
                            }
                            if (orderFee.getPreOilVirtualFee() > 0) {
                                if (list.size() < 58 || StringUtils.isBlank(tmpStr = list.get(57).trim())) {
                                    errorInfo.append("???????????????/???????????????/???????????????-?????????????????????????????????????????????");


                                }
                                if ("????????????".equals(tmpStr)) {
                                    orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
                                } else if ("???????????????".equals(tmpStr)) {
                                    orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
                                } else if ("????????????".equals(tmpStr)) {
                                    orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
                                } else {
                                    errorInfo.append("?????????/?????????/????????????????????????????????????");


                                }
                            }
                            if (orderFee.getTotalFee() != orderFee.getPreTotalFee() + orderFee.getArrivePaymentFee() + orderFee.getFinalFee()) {
                                errorInfo.append("??????????????????????????????????????????????????????");


                            }

                            //??????????????????????????????
                            //??????????????????????????????????????????????????????/???????????????/???????????????????????????
                            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            // ????????????xx?????????????????????????????????????????????????????????????????????
                            String collectionUserPhone = list.get(58);
                            String collectionUserName = list.get(59);
                            if ((orderInfo.getTenantId() == null || orderInfo.getTenantId() <= 0)
                                    && StringUtils.isNotBlank(collectionUserPhone) && StringUtils.isBlank(collectionUserName)) {
                                if (!CommonUtil.isCheckMobiPhoneNew(collectionUserPhone)) {
                                    errorInfo.append("???????????????????????????????????????????????????????????????");


                                }
                                errorInfo.append("????????????????????????????????????????????????");


                            } else if (StringUtils.isNotBlank(collectionUserName) && StringUtils.isBlank(collectionUserPhone)) {
                                errorInfo.append("????????????????????????????????????????????????????????????");


                            } else if (StringUtils.isNotBlank(collectionUserPhone) && StringUtils.isNotBlank(collectionUserName)) {
                                if (!CommonUtil.isCheckMobiPhoneNew(collectionUserPhone)) {
                                    errorInfo.append("???????????????????????????????????????????????????????????????");


                                }
                                orderScheduler.setCollectionUserPhone(collectionUserPhone);
                                orderScheduler.setCollectionName(collectionUserName);
                                orderScheduler.setIsCollection(OrderConsts.IS_COLLECTION.YES);
                            }
                        }

                    }
                    if (orderScheduler.getVehicleClass() == null) {
                        orderScheduler.setVehicleClass(-1);
                    }
                    int jumpCount = 2;
                    //?????????????????????
                    if (orderScheduler.getVehicleClass() != null && StringUtils.isNotBlank(tmpPlateNumber)) {
                        //????????????
                        this.getMemberBenefits(orderScheduler.getVehicleClass(), dispatchPaymentDaysInfo, loginInfo);
                        if (list.size() > 58 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(58 + jumpCount).trim())) {
                            dispatchPaymentDaysInfo.setBalanceType(this.getCodeByName(balanceTypes, tmpStr));
                            if (dispatchPaymentDaysInfo.getBalanceType() < 0) {
                                errorInfo.append("????????????????????????");


                            }
                            if (list.size() > 59 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(59 + jumpCount).trim())) {
                                if (dispatchPaymentDaysInfo.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                                    String[] str = tmpStr.split(",");
                                    if (str.length != 2 || !CommonUtil.isInteger(str[0]) || !CommonUtil.isInteger(str[1])) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    }
                                    if (str.length == 2) {
                                        dispatchPaymentDaysInfo.setReciveMonth(Integer.parseInt(str[0]));
                                        dispatchPaymentDaysInfo.setReciveDay(Integer.parseInt(str[1]));
                                    }
                                } else {
                                    if (!CommonUtil.isInteger(tmpStr)) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    } else {
                                        dispatchPaymentDaysInfo.setReciveTime(Integer.parseInt(tmpStr));
                                    }

                                }
                            } else {
                                errorInfo.append("???????????????????????????");
                            }
                            if (list.size() > 60 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(60 + jumpCount).trim())) {
                                if (dispatchPaymentDaysInfo.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                                    String[] str = tmpStr.split(",");
                                    if (str.length != 2 || !CommonUtil.isInteger(str[0]) || !CommonUtil.isInteger(str[1])) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    }
                                    if (str.length == 2) {
                                        dispatchPaymentDaysInfo.setReconciliationMonth(Integer.parseInt(str[0]));
                                        dispatchPaymentDaysInfo.setReconciliationDay(Integer.parseInt(str[1]));
                                    }
                                } else {
                                    if (!CommonUtil.isInteger(tmpStr)) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    } else {
                                        dispatchPaymentDaysInfo.setReconciliationTime(Integer.parseInt(tmpStr));
                                    }

                                }
                            } else {
                                errorInfo.append("???????????????????????????");
                            }
                            if (list.size() > 61 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(61 + jumpCount).trim())) {
                                if (dispatchPaymentDaysInfo.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                                    String[] str = tmpStr.split(",");
                                    if (str.length != 2 || !CommonUtil.isInteger(str[0]) || !CommonUtil.isInteger(str[1])) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    }
                                    if (str.length == 2) {
                                        dispatchPaymentDaysInfo.setInvoiceMonth(Integer.parseInt(str[0]));
                                        dispatchPaymentDaysInfo.setInvoiceDay(Integer.parseInt(str[1]));
                                    }
                                } else {
                                    if (!CommonUtil.isInteger(tmpStr)) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    } else {
                                        dispatchPaymentDaysInfo.setInvoiceTime(Integer.parseInt(tmpStr));
                                    }

                                }
                            } else {
                                errorInfo.append("???????????????????????????");
                            }
                            if (list.size() > 62 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(62 + jumpCount).trim())) {
                                if (dispatchPaymentDaysInfo.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                                    String[] str = tmpStr.split(",");
                                    if (str.length != 2 || !CommonUtil.isInteger(str[0]) || !CommonUtil.isInteger(str[1])) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    }
                                    if (str.length == 2) {
                                        dispatchPaymentDaysInfo.setCollectionMonth(Integer.parseInt(str[0]));
                                        dispatchPaymentDaysInfo.setCollectionDay(Integer.parseInt(str[1]));
                                    }
                                } else {
                                    if (!CommonUtil.isInteger(tmpStr)) {
                                        errorInfo.append("????????????[" + tmpStr + "]??????????????????");


                                    } else {
                                        dispatchPaymentDaysInfo.setCollectionTime(Integer.parseInt(tmpStr));
                                    }

                                }
                            } else {
                                errorInfo.append("???????????????????????????");
                            }
                        }
                    } else {
                        orderScheduler.setAppointWay(com.youming.youche.finance.constant.OrderConsts.AppointWay.APPOINT_LOCAL);
                        orderScheduler.setDispatcherId(loginInfo.getUserInfoId());
                        orderScheduler.setDispatcherName(loginInfo.getName());
                        orderScheduler.setDispatcherBill(loginInfo.getTelPhone());
                    }

                    //?????????
                    if (list.size() > 63 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(63 + jumpCount).trim())) {
                        this.getStaffData(tmpStr, orderGoods);
                    } else {
                        orderGoods.setLocalUser(loginInfo.getUserInfoId());
                        orderGoods.setLocalUserName(loginInfo.getName());
                        orderGoods.setLocalPhone(loginInfo.getTelPhone());
                    }
                    //????????????
                    if (list.size() > 64 + jumpCount && StringUtils.isNotBlank(tmpStr = list.get(64 + jumpCount).trim())) {
                        Long oragnizeIdByName = getOragnizeIdByName(tmpStr, loginInfo);
                        if (oragnizeIdByName != null) {
                            orderInfo.setOrgId(oragnizeIdByName.intValue());
                        }
                        if (orderInfo.getOrgId() == null) {
                            errorInfo.append("??????????????????????????????");


                        }
                    } else {
                        if (null == loginInfo.getOrgId()) {
                            if (loginInfo.getOrgIds() != null && loginInfo.getOrgIds().size() > 0) {
                                orderInfo.setOrgId(loginInfo.getOrgIds().get(0).intValue());
                            }
                        } else {
                            orderInfo.setOrgId(loginInfo.getOrgId().intValue());
                        }

                    }

                    //????????????
                    if (StringUtils.isBlank(errorInfo.toString())) {
                        this.checkData(orderInfo, orderGoods, orderScheduler, orderFee, orderOilDepotSchemeList, loginInfo);
                    }

                    //???????????????????????????????????????
                    this.getSysTenantDef(tenantId, orderScheduler);
                    //??????????????????
                    if (null != orderScheduler.getIsCollection() && OrderConsts.IS_COLLECTION.YES == orderScheduler.getIsCollection()) {
                        this.createUserReceiverInfo(orderScheduler, tenantId, loginInfo);
                    }
                    //????????????
                    orderInfo.setChannelType(String.valueOf(SysStaticDataEnum.CHANEL_TYPE.CHANEL_WEB));
                    if (StringUtils.isBlank(errorInfo.toString())) {
                        if (orderFeeExt.getPontagePer() == null) {
                            orderFeeExt.setPontagePer(0L);
                        }
                        if (orderInfoExt.getRunWay() == null) {
                            orderInfoExt.setRunWay(0L);
                        }
                        Long orderid = this.saveOrUpdateOrderInfo(orderInfo, orderFee, orderGoods, orderInfoExt, orderFeeExt,
                                orderScheduler, orderOilDepotSchemeList, dispatchPaymentDaysInfo,
                                incomePaymentDaysInfo, null, orderTransitLineInfos, false, token, loginInfo);

                        if (orderid != null) {
                            success++;
                        }
                    } else {
                        result.setFailure(errorInfo.toString());
                        orderInfos.add(result);
                        errorInfo.setLength(0);
                    }
                } catch (Exception e) {
                    OrderInfoExportDto orderInfoExportDto = orderException.get(row - 1);
                    orderInfoExportDto.setFailure(e.getMessage());
                    orderException.set(row - 1, orderInfoExportDto);
                    orderInfos.add(orderInfoExportDto);
                }


            }
            if (orderInfos != null && orderInfos.size() > 0) {
                int dissimilarity = row - success;
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "????????????", "??????????????????", "???????????????(km)", "??????????????????(km)", "????????????", "????????????",
                        "????????????(???)", "????????????(???)", "?????????", "????????????", "???????????????(??????)", "????????????", "????????????", "????????????(???)", "?????????",
                        "????????????", "????????????", "??????????????????", "??????????????????", "????????????(??????)", "????????????", "????????????", "????????????",
                        "????????????", "????????????", "??????????????????", "????????????", "????????????"};
                resourceFild = new String[]{"getLineCodeRule", "getSourceName", "getCarDependDate", "getCmMileageNumber", "getMileageNumber", "getGoodsName", "getGoodsType",
                        "getWeight", "getSquare", "getReciveName", "getRecivePhone", "getIsUrgent", "getPriceUnit", "getUnitName", "getCostPrice", "getVehicleNumber",
                        "getCarStatus", "getTrailerPlate", "getMainDriverPhone", "getCopilotPhone", "getPaymentWay", "getBalanceType", "getReciveDay", "getReconciliationTime",
                        "getBillPeriod", "getPayPeriod", "getStateNamePhone", "getAttachedOrgName", "getFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(orderInfos, showName, resourceFild, OrderInfoExportDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "?????????" + dissimilarity + "?????????");
                    records.setState(2);
                }
                if (success == 0) {
                    records.setRemarks(row + "?????????");
                    records.setState(4);
                }
            } else {
                records.setRemarks(success + "?????????");
                records.setState(3);
            }
            importOrExportRecordsService.update(records);
        } catch (Exception e) {

            try {
                int dissimilarity = row - success;
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "????????????", "??????????????????", "???????????????(km)", "??????????????????(km)", "????????????", "????????????",
                        "????????????(???)", "????????????(???)", "?????????", "????????????", "???????????????(??????)", "????????????", "????????????", "????????????(???)", "?????????",
                        "????????????", "????????????", "??????????????????", "??????????????????", "????????????(??????)", "????????????", "????????????", "????????????",
                        "????????????", "????????????", "??????????????????", "????????????", "????????????"};
                resourceFild = new String[]{"getLineCodeRule", "getSourceName", "getCarDependDate", "getCmMileageNumber", "getMileageNumber", "getGoodsName", "getGoodsType",
                        "getWeight", "getSquare", "getReciveName", "getRecivePhone", "getIsUrgent", "getPriceUnit", "getUnitName", "getCostPrice", "getVehicleNumber",
                        "getCarStatus", "getTrailerPlate", "getMainDriverPhone", "getCopilotPhone", "getPaymentWay", "getBalanceType", "getReciveDay", "getReconciliationTime",
                        "getBillPeriod", "getPayPeriod", "getStateNamePhone", "getAttachedOrgName", "getFailure"};
                OrderInfoExportDto orderInfoExportDto = orderException.get(row - 1);
                orderInfoExportDto.setFailure(e.getMessage());
                orderException.set(row - 1, orderInfoExportDto);

                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(orderException, showName, resourceFild, OrderInfoExportDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = null;
                client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "?????????" + dissimilarity + "?????????");
                    records.setState(2);
                }
                if (success == 0) {
                    List<List<String>> lists = null;
                    InputStream arrayInputStream = new ByteArrayInputStream(bytes);
                    try {
                        lists = ExcelUtils.getExcelContent(arrayInputStream, 2, (ExcelFilesVaildate[]) null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    List<ResponseResult> results = new ArrayList<ResponseResult>(lists.size());
                    //???????????????
                    int size = lists.get(0).size();
                    lists.remove(0);
                    List<List<String>> collect = lists.stream().filter(a -> (a.size() == size)).collect(Collectors.toList());
                    records.setRemarks(collect.size() + "?????????");
                    records.setState(4);
                }
                records.setState(4);
                importOrExportRecordsService.update(records);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            e.printStackTrace();
        }

        return errorInfo.toString();
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("order_id", orderId);
        List<OrderInfo> list = this.list(orderInfoQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<WorkbenchDto> getTableModifyExamineCount() {
        return getBaseMapper().getTableModifyExamineCount();
    }

    @Override
    public List<WorkbenchDto> getTableExceptionExamineCount() {
        return baseMapper.getTableExceptionExamineCount();
    }

    @Override
    public List<WorkbenchDto> getTableReceiptExamineCount() {
        return baseMapper.getTableReceiptExamineCount();
    }

    @Override
    public List<WorkbenchDto> getTableBillTodayCount() {
        List<WorkbenchDto> orderDto = baseMapper.getTableBillTodayCount(LocalDate.now().atStartOfDay());
        List<WorkbenchDto> orderHDto = baseMapper.getTableBillTodayHCount(LocalDate.now().atStartOfDay());

        List<WorkbenchDto> workbenchDtoList = new ArrayList<>();
        for (WorkbenchDto workbenchDto : orderDto) {
            Optional<WorkbenchDto> optional = orderHDto.stream().filter(item -> item.getTenantId() == workbenchDto.getTenantId()).findFirst();
            if (optional.isPresent()) {
                workbenchDto.setCount(workbenchDto.getCount() + optional.get().getCount());
            }

            workbenchDtoList.add(workbenchDto);
        }

        for (WorkbenchDto workbenchDto : orderHDto) {
            Optional<WorkbenchDto> optional = workbenchDtoList.stream().filter(item -> item.getTenantId() == workbenchDto.getTenantId()).findFirst();
            if (!optional.isPresent()) {
                workbenchDtoList.add(workbenchDto);
            }
        }
        return workbenchDtoList;
    }

    @Override
    public List<WorkbenchDto> getTablePrepaidOrderCount() {
        return baseMapper.getTablePrepaidOrderCount();
    }

    @Override
    public List<WorkbenchDto> getTableIntransitOrderCount() {
        return baseMapper.getTableIntransitOrderCount();
    }

    @Override
    public List<WorkbenchDto> getTableRetrialOrderCount() {
        return baseMapper.getTableRetrialOrderCount();
    }

    @Override
    public List<QueryOrderResponsiblePartyDto> queryOrderResponsibleParty(Long orderId) {
        // ????????????????????????
        OrderInfo orderInfo = this.getOrder(orderId);
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        // ???????????????  ???????????????????????????????????????
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = this.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("?????????????????????[" + orderId + "]?????????!");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerService.getOrderSchedulerH(orderId);
            OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderGoodsH, orderGoods);
        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderGoods = orderGoodsService.getOrderGoods(orderId);
        }

        String carrier = "??????(" + orderGoods.getCustomName() + ")";// ?????????
        String consigner = "";// ?????????
        if (orderInfo.getToTenantId() != null) {
            consigner = "??????(" + orderInfo.getToTenantName() + ")";
        } else {
            consigner = "??????(" + orderScheduler.getCarDriverMan() + "-" + orderScheduler.getPlateNumber() + ")";
        }

        List<QueryOrderResponsiblePartyDto> list = new ArrayList<>();
        QueryOrderResponsiblePartyDto retDto = new QueryOrderResponsiblePartyDto();
        retDto.setName(consigner);
        retDto.setType(SysStaticDataEnum.PROBLEM_CONDITION.COST);
        list.add(retDto);

        QueryOrderResponsiblePartyDto carrierDto = new QueryOrderResponsiblePartyDto();
        carrierDto.setName(carrier);
        carrierDto.setType(SysStaticDataEnum.PROBLEM_CONDITION.INCOME);
        list.add(carrierDto);

        return list;
    }

    private void createUserReceiverInfo(OrderScheduler orderScheduler, Long tenantId, LoginInfo user) {
        if (StringUtils.isNotBlank(orderScheduler.getCollectionUserPhone()) && StringUtils.isNotBlank(orderScheduler.getCollectionUserName())) {
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ????????????????????????????????????????????????????????????????????????????????????????????????

            createUserReceiverInfo(orderScheduler.getCollectionUserPhone(),
                    orderScheduler.getCollectionUserName(), orderScheduler.getCollectionUserName(), null, tenantId, user);

			/*UserSV userSV = (UserSV) SysContexts.getBean("userSV");
			UserDataInfo userDataInfo = userSV.getUserDataInfoByMoblile(orderScheduler.getCollectionUserPhone(), false);
			if(null == userDataInfo){
				throw new BusinessException("?????????????????????");
			}
			orderScheduler.setCollectionUserId(userDataInfo.getUserId());*/
        }
    }

    public void createUserReceiverInfo(String phone, String receiverName, String linkman, String remark, Long tenantId, LoginInfo user) {
        if (null == tenantId) {
            tenantId = user.getTenantId();
        }

        UserDataInfo userDataInfo = userDataInfoService.getPhone(phone);
        if (null == userDataInfo) {
            userDataInfo = initUserDataInfo(phone, linkman, user);
        }

        UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null == userReceiverInfo) {
            userReceiverInfo = initUserReceiverInfo(receiverName, userDataInfo.getId(), user);
        } else {
            userReceiverInfo.setReceiverName(receiverName);
            userReceiverInfo.setUpdateTime(LocalDateTime.now());
            userReceiverInfoService.update(userReceiverInfo);
        }

        TenantReceiverRel rel = tenantReceiverRelService.createTenantReceiverRel(userReceiverInfo.getId(), remark, tenantId, user);

        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.UserReceivcer, rel.getId(),
                SysOperLogConst.OperType.Add, "???????????????");
    }

    private UserReceiverInfo initUserReceiverInfo(String receiverName, Long userId, LoginInfo user) {
        UserReceiverInfo userReceiverInfo = new UserReceiverInfo();
        userReceiverInfo.setReceiverName(receiverName);
        userReceiverInfo.setOpId(user.getId());
        userReceiverInfo.setCreateTime(LocalDateTime.now());
        userReceiverInfo.setUpdateTime(LocalDateTime.now());
        userReceiverInfo.setUserId(userId);

        userReceiverInfoService.save(userReceiverInfo);
        return userReceiverInfo;
    }

    private UserDataInfo initUserDataInfo(String phone, String linkman, LoginInfo user) {
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setLinkman(linkman);
        userDataInfo.setMobilePhone(phone);
        userDataInfo.setSourceFlag(0);
        userDataInfoService.save(userDataInfo);
        SysUser sysOperator = new SysUser();
        sysOperator.setTenantId(null);
        sysOperator.setBillId(phone);
        sysOperator.setCreateTime(LocalDateTime.now());
        sysOperator.setLoginAcct(phone);
        sysOperator.setUserInfoId(userDataInfo.getId());
        sysOperator.setOpName(linkman);
        sysOperator.setOpUserId(user.getId());
        sysOperator.setLockFlag(1);
        sysOperator.setState(1);
        sysOperator.setTenantCode(null);
        sysUserService.saveSysUser(sysOperator);

        return userDataInfo;
    }

    /**
     * ??????????????????
     *
     * @return
     * @throws Exception
     */
    private void getSysTenantDef(Long tenantId, OrderScheduler orderScheduler) {
        SysTenantDef tenant = sysTenantDefService.getSysTenantDef(tenantId);
        if (tenant != null) {
            orderScheduler.setReciveAddr(StringUtils.isBlank(tenant.getProvinceName()) ? "" : tenant.getProvinceName()
                    + (StringUtils.isBlank(tenant.getCityName()) ? "" : tenant.getCityName()) + tenant.getAddress());
        }
    }

    /**
     * ??????TF??? ????????????
     *
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @throws Exception
     */
    private void checkData(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler,
                           OrderFee orderFee, List<OrderOilDepotScheme> orderOilDepotSchemeList, LoginInfo user) throws Exception {

        //????????????
        ObjectCompareUtils.isNotBlankNamesMap(orderInfo, getAddOrderInfoCheckNoBlank());
        ObjectCompareUtils.isNotBlankNamesMap(orderGoods, getAddOrderGoodsCheckNoBlank(orderInfo.getOrderType()));
        ObjectCompareUtils.isNotBlankNamesMap(orderScheduler, getAddOrderSchedulerCheckNoBlank(orderScheduler.getVehicleClass(), orderScheduler.getAppointWay()));

        if (null != orderFee.getPrePayCash() && orderFee.getPrePayCash() < 0) {
            throw new BusinessException("????????????????????????0");
        }
        if (null != orderFee.getPrePayEquivalenceCardAmount() && orderFee.getPrePayEquivalenceCardAmount() < 0) {
            throw new BusinessException("?????????????????????????????????0");
        }
        if (null != orderFee.getAfterPayCash() && orderFee.getAfterPayCash() < 0) {
            throw new BusinessException("????????????????????????0");
        }
        if (null != orderFee.getAfterPayEquivalenceCardAmount() && orderFee.getAfterPayEquivalenceCardAmount() < 0) {
            throw new BusinessException("???????????????????????????0");
        }

        //????????????-????????????
        /*if(orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR){
        	//?????????????????????
        	if(orderScheduler.getLicenceType()!=null && orderScheduler.getLicenceType().intValue()==SysStaticDataEnum.LICENCE_TYPE.ZC) {
	            if(!OrderUtil.checkVehicleLength(orderScheduler.getCarLengh(), orderGoods.getVehicleLengh())){
	                throw new BusinessException("?????????????????????");
	            }
	            if (!OrderUtil.checkVehicleStatus(orderScheduler.getCarStatus(), orderGoods.getVehicleStatus())) {
	                throw new BusinessException("???????????????");
	            }
        	}
        }*/
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR || orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCarDriverId() != null && orderScheduler.getCopilotUserId().longValue() == orderScheduler.getCarDriverId().longValue()) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
                //?????????
                return;
            }
            if (orderFee.getTotalFee() != null) {
                String maxAmount = this.getAmountLimitCfgVal(user.getTenantId());
                if (StringUtils.isNotBlank(maxAmount) && !"-1".equals(maxAmount)) {
                    if (orderFee.getTotalFee().longValue() > Long.parseLong(maxAmount)) {
                        throw new BusinessException("?????????????????????" + objToFloatDiv100(maxAmount) + "???");
                    }
                }
            }
        }

        List list = new ArrayList();
        for (OrderOilDepotScheme orderOilDepotScheme : orderOilDepotSchemeList) {
            if (orderOilDepotScheme.getOilDepotId() != null) {
                if (list.contains(orderOilDepotScheme.getOilDepotId())) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                } else {
                    list.add(orderOilDepotScheme.getOilDepotId());
                }
            }
        }
    }

    /**
     * ??????????????????????????????Id
     *
     * @param orgName
     * @return
     */
    public Long getOragnizeIdByName(String orgName, LoginInfo user) {
        List<SysOrganize> lists = sysOrganizeService.getSysOrganizeBytenantId(user.getTenantId());
        if (CollectionUtils.isEmpty(lists)) {
            return null;
        }

        for (SysOrganize oragnize : lists) {
            if (oragnize.getOrgName().equals(orgName) && oragnize.getState() == 1) {
                return oragnize.getId();
            }
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param loginAcct
     * @throws Exception
     */
    private void getStaffData(String loginAcct, OrderGoods orderGoods) {
        UserDataInfo userDataInfo = userDataInfoService.getPhone(loginAcct);
        if (userDataInfo != null) {
            orderGoods.setLocalUser(userDataInfo.getId());
            orderGoods.setLocalUserName(userDataInfo.getLinkman());
            orderGoods.setLocalPhone(loginAcct);
        } else {
            throw new BusinessException("??????????????????[" + loginAcct + "]???????????????;");
        }
    }

    /**
     * ??????????????????
     *
     * @param vihicleClass
     * @throws Exception
     */
    public void getMemberBenefits(int vihicleClass, OrderPaymentDaysInfo paymentDaysInfo, LoginInfo user) {


        List<CreditRatingRule> outRulelist = creditRatingRuleService.queryMemberBenefits(vihicleClass, user.getTenantId());
        if (outRulelist != null && outRulelist.size() > 0) {

            for (CreditRatingRule creditRatingRule : outRulelist) {
                paymentDaysInfo.setBalanceType(creditRatingRule.getSettleType());

                if (creditRatingRule.getSettleType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                    paymentDaysInfo.setReciveMonth(creditRatingRule.getReceiptPeriod());
                    paymentDaysInfo.setReconciliationMonth(creditRatingRule.getCheckPeriod());
                    paymentDaysInfo.setInvoiceMonth(creditRatingRule.getBillPeriod());
                    paymentDaysInfo.setCollectionMonth(creditRatingRule.getPayPeriod());
                    paymentDaysInfo.setReciveDay(creditRatingRule.getReceiptPeriodDay());
                    paymentDaysInfo.setReconciliationDay(creditRatingRule.getCheckPeriodDay());
                    paymentDaysInfo.setInvoiceDay(creditRatingRule.getBillPeriodDay());
                    paymentDaysInfo.setCollectionDay(creditRatingRule.getPayPeriodDay());
                } else {
                    paymentDaysInfo.setReciveTime(creditRatingRule.getReceiptPeriod());
                    paymentDaysInfo.setReconciliationTime(creditRatingRule.getCheckPeriod());
                    paymentDaysInfo.setInvoiceTime(creditRatingRule.getBillPeriod());
                    paymentDaysInfo.setCollectionTime(creditRatingRule.getPayPeriod());
                }
            }
        }
    }

    /**
     * ????????????
     *
     * @param vehicleClass
     * @param
     * @return
     * @throws Exception
     */
    public double getInsumeFee(int vehicleClass, OrderFee orderFee, LoginInfo user) {
        CreditRatingRule creditRatingRule = creditRatingRuleService.queryCreditRatingRule(user.getTenantId(), vehicleClass);
        orderFee.setPreTotalScaleStandard(creditRatingRule != null && creditRatingRule.getAdvanceCharge() != null ? (long) (creditRatingRule.getAdvanceCharge() * 10000) : 0);
        orderFee.setPreOilScaleStandard(0L);
        orderFee.setPreOilVirtualScaleStandard(creditRatingRule != null && creditRatingRule.getOilScale() != null ? (long) (creditRatingRule.getOilScale() * 10000) : 0);
        orderFee.setPreEtcScaleStandard(creditRatingRule != null && creditRatingRule.getEtcScale() != null ? (long) (creditRatingRule.getEtcScale() * 10000) : 0);
        orderFee.setPreCashScaleStandard(orderFee.getPreTotalScaleStandard() - orderFee.getPreOilVirtualScaleStandard() - orderFee.getPreEtcScaleStandard());
        List<CreditRatingRuleFee> list = null;
        if (creditRatingRule != null && creditRatingRule.getFeeList() != null) {
            list = creditRatingRule.getFeeList();
        }

        if (list == null) {
            return 0;
        }
        Long totalFee = null;
        if (orderFee.getTotalFee() != null) {
            totalFee = orderFee.getTotalFee() / 100;
        }
        for (CreditRatingRuleFee creditRatingRuleFee : list) {
            if (creditRatingRuleFee.getMaxFee() != null && creditRatingRuleFee.getMaxFee() != -1) {
                if (totalFee != null && totalFee >= creditRatingRuleFee.getMinFee() && totalFee <= creditRatingRuleFee.getMaxFee()) {
                    return creditRatingRuleFee.getInsuranceFee();
                }
            } else {
                if (totalFee != null && creditRatingRuleFee.getMinFee() != null && totalFee > creditRatingRuleFee.getMinFee()) {
                    return creditRatingRuleFee.getInsuranceFee();
                }
            }
        }

        return 0;
    }

    /**
     * ??????????????????????????????
     *
     * @param productName
     * @throws Exception
     */
    private void getOilProductByName(String productName, Long lineId, List<OrderOilDepotScheme> orderOilDepotSchemeList,
                                     OrderGoods orderGoogds, Integer isBill, LoginInfo user) {
        LineOilQueryInDto lineOilQueryIn = new LineOilQueryInDto();
        lineOilQueryIn.setProductFullName(productName);
        lineOilQueryIn.setType(2);
        lineOilQueryIn.setLineId(lineId);
        lineOilQueryIn.setSourceEand(Double.valueOf(orderGoogds.getEand()));
        lineOilQueryIn.setSourceNand(Double.valueOf(orderGoogds.getNand()));
        lineOilQueryIn.setIsBill(1);
        List<LineOilDepotSchemeDto> oilDepotSchemeByLineId = lineOilDepotSchemeService.getLineOilDepotSchemeByLineId(lineOilQueryIn, user.getTenantId());
        LineOilDepotSchemeDto map = null;
        if (oilDepotSchemeByLineId.size() > 0) {
            map = oilDepotSchemeByLineId.get(0);
        }
        if (map == null) {
            lineOilQueryIn.setType(2);
            List<LineOilDepotSchemeDto> depotSchemeByLineId = lineOilDepotSchemeService.getLineOilDepotSchemeByLineId(lineOilQueryIn, user.getTenantId());
            if (depotSchemeByLineId.size() > 0) {
                map = depotSchemeByLineId.get(0);
            }
        }
        if (map == null) {
            throw new BusinessException("???????????????[" + productName + "]????????????");
        }

        OrderOilDepotScheme orderOilDepotScheme = new OrderOilDepotScheme();
        orderOilDepotScheme.setDependDistance(OrderUtil.objToLongMul100(map.getDistance().toString()));
        orderOilDepotScheme.setOilDepotEand(map.getEand());
        orderOilDepotScheme.setOilDepotNand(map.getNand());
        orderOilDepotScheme.setOilDepotId(Long.valueOf(map.getOilId().toString()));
        orderOilDepotScheme.setOilDepotName(productName);
        orderOilDepotScheme.setOilDepotPrice(OrderUtil.objToLongMul100(map.getOilPrice().toString()));
        orderOilDepotScheme.setOilDepotLitre(0L);
        orderOilDepotScheme.setOilDepotFee(0L);

        orderOilDepotSchemeList.add(orderOilDepotScheme);
    }


    /**
     * ??????????????????
     *
     * @param trailerNumber
     * @param orderScheduler
     */
    public void getTrailerInfo(String trailerNumber, OrderScheduler orderScheduler, Long tenantId) {
        List<TrailerManagement> list = trailerManagementService.getNotUsedTrailer(tenantId, trailerNumber);
        if (list == null || list.size() == 0) {
            throw new BusinessException("??????[" + trailerNumber + "]?????????");
        }
        orderScheduler.setTrailerId(list.get(0).getId());
        orderScheduler.setTrailerPlate(list.get(0).getTrailerNumber());
    }

    /**
     * ????????????
     *
     * @param
     * @param
     * @return
     */
    public void calCostPrice(OrderFee orderFee, OrderGoods orderGoods, OrderScheduler orderScheduler) {
        if (orderFee.getPriceEnum() != null) {
            if (orderFee.getPriceEnum() == SysStaticDataEnum.PRICE_ENUM.CAR) {
                orderFee.setCostPrice((long) (orderFee.getPriceUnit() * 100));
            } else if (orderFee.getPriceEnum() == com.youming.youche.record.common.SysStaticDataEnum.PRICE_ENUM.KG) {
                orderFee.setCostPrice((long) (orderFee.getPriceUnit() * orderGoods.getWeight() * 1000 * 100));
            } else if (orderFee.getPriceEnum() == com.youming.youche.record.common.SysStaticDataEnum.PRICE_ENUM.SQ) {
                orderFee.setCostPrice((long) (orderFee.getPriceUnit() * orderGoods.getSquare() * 100));
            } else if (orderFee.getPriceEnum() == com.youming.youche.record.common.SysStaticDataEnum.PRICE_ENUM.DISTANCE) {
                orderFee.setCostPrice((long) (orderFee.getPriceUnit() * orderScheduler.getMileageNumber() / 10));
            }
        } else {
            orderFee.setCostPrice(0L);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param
     * @param name
     * @return
     */
    private int getCodeByName(List<SysStaticData> list, String name) {
        for (SysStaticData sysStaticData : list) {
            if (sysStaticData.getCodeName().equals(name)) {
                return Integer.parseInt(sysStaticData.getCodeValue());
            }
        }

        return -1;
    }

    /**
     * ????????????????????????????????????
     *
     * @param dependTime
     * @return
     */
    private Date checkDependTimeFormat(String dependTime) {
        try {
            return DateUtil.formatStringToDate(dependTime, "yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            throw new BusinessException("?????????????????????????????????????????????");
        }
    }

    /**
     * ????????????????????????
     *
     * @param costPrice
     * @param prePayCash
     * @param afterPayCash
     * @param prePayEquivalenceCardAmount
     * @param afterPayEquivalenceCardAmount
     */
    private void checkCostPriceSum(Long costPrice, Long prePayCash, Long afterPayCash, Long prePayEquivalenceCardAmount,
                                   Long afterPayEquivalenceCardAmount, LoginInfo user) {
        //todo ??????????????????????????????????????????"????????????"??????
        boolean orderIncomePermission = sysRoleService.hasOrderCostPermission(user);
        if (orderIncomePermission) {
            if (costPrice == null) {
                throw new BusinessException("????????????????????????");
            }
            Long comparePrice = 0L;
            if (prePayCash != null) {
                comparePrice += prePayCash;
            }
            if (afterPayCash != null) {
                comparePrice += afterPayCash;
            }
            if (prePayEquivalenceCardAmount != null) {
                comparePrice += prePayEquivalenceCardAmount;
            }
            if (afterPayEquivalenceCardAmount != null) {
                comparePrice += afterPayEquivalenceCardAmount;
            }

            if (comparePrice.longValue() != costPrice.longValue()) {
                throw new BusinessException("???????????????????????????????????????????????????????????????");
            }
        }
    }


    /**
     * ????????????????????????
     *
     * @param orderInfo
     * @param orderfee
     * @param orderScheduler
     * @param orderInfoExt
     * @param orderGoods
     * @param orderFeeExt
     * @param isUpdate
     * @throws Exception
     */
    private void checkBasicsOrderInfo(OrderInfo orderInfo, OrderFee orderfee, OrderScheduler orderScheduler, OrderInfoExt orderInfoExt
            , OrderGoods orderGoods, OrderFeeExt orderFeeExt, List<OrderTransitLineInfo> transitLineInfos, Boolean isUpdate, LoginInfo baseUser) {
        if (orderInfo.getOrgId() == null || orderInfo.getOrgId() <= 0) {
            throw new BusinessException("????????????????????????");
        }
        // ????????????????????????????????????
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
            if ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )
                    || (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null
                    && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT)) {
                checkTotalFee(orderfee.getTotalFee(), orderfee.getPreTotalFee(), orderfee.getPreCashFee(),
                        orderfee.getPreOilVirtualFee(), orderfee.getPreOilFee(), orderfee.getPreEtcFee(),
                        orderfee.getFinalFee(), orderfee.getInsuranceFee(), orderfee.getArrivePaymentFee());
            }

            if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null
                    && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                this.checkEstFee(orderFeeExt.getEstFee(), orderFeeExt.getSalary(), orderFeeExt.getCopilotSalary(),
                        orderfee.getPreOilVirtualFee(), orderfee.getPreOilFee(), orderFeeExt.getPontage());
            }
        }
        //????????????
//        if (orderInfo.getIsNeedBill() == null || orderInfo.getIsNeedBill() < 0 || orderInfo.getIsNeedBill() > 2) {
//            throw new BusinessException("???????????????????????????????????????????????????");
//        } else if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
//            if (orderGoods.getGoodsType() != null && orderGoods.getGoodsType().intValue() == SysStaticDataEnum.GOODS_TYPE.DANGER_GOODS) {
//                //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                if (orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId().longValue() > 0) {
//                    //????????????????????????
//                    UserDataInfo userDataInfo = userDataInfoService.get(orderScheduler.getCarDriverId());
//                    if (userDataInfo != null) {
//                        if (userDataInfo.getQcCerti() == null) {
//                            throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
//                        }
//                    }
//                }
//                if (orderScheduler.getVehicleCode() != null && orderScheduler.getVehicleCode() > 0) {
//                    VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getById(orderScheduler.getVehicleCode());
//                    if (vehicleDataInfo != null) {
//                        if (vehicleDataInfo.getSpecialOperCertFileId() == null || vehicleDataInfo.getSpecialOperCertFileId() <= 0) {
//                            throw new BusinessException("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
//                        }
//                    }
//                }
//            }
//            if (orderScheduler.getVehicleClass() != null && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
//                boolean notOtherCarGetPlatformBill = billSettingService.supportNotOtherCarGetPlatformBill(orderInfo.getTenantId());
//                if (!notOtherCarGetPlatformBill) {
//                    throw new BusinessException("??????????????????????????????????????????????????????????????????");
//                }
//
//                Integer payAcctType = billPlatformService.getPayAcctType(baseUser.getTenantId(), null);
//                if (payAcctType != null && payAcctType == 1) {
//                    List<AccountBankRel> bankList = accountBankRelService.getCollectAmount(orderInfo.getTenantId());
//                    SysCfg sysCfg = readisUtil.getSysCfg(NEED_BILL_BANK_CARD_QUANTITY_LIMIT, "0");
//                    Double needBillBankCardQuantityLimit = null;
//                    if (sysCfg != null) {
//                        needBillBankCardQuantityLimit = Double.parseDouble(sysCfg.getCfgValue());
//                    }
//                    if (bankList == null || bankList.size() < needBillBankCardQuantityLimit) {
//                        throw new BusinessException("??????????????????????????????????????????" + needBillBankCardQuantityLimit.intValue() + "?????????????????????????????????");
//                    }
//                }
//            }
//
//            if (StringUtils.isBlank(orderGoods.getGoodsName())) {
//                throw new BusinessException("?????????????????????????????????????????????????????????");
//            }
//
//            //??????????????????
//            if (orderInfo.getOrderId() == null || orderInfo.getOrderId() <= 0) {
//                BillInfoReceiveRel billInfoReceiveRel = billInfoReceiveRelService.getDefaultBillInfoByTenantId(baseUser.getTenantId());
//                if (billInfoReceiveRel == null || billInfoReceiveRel.getBillInfo() == null
//                        || StringUtils.isBlank(billInfoReceiveRel.getBillInfo().getBillLookUp())) {
//                    throw new BusinessException("????????????????????????????????????");
//                }
//                orderFeeExt.setBillLookUp(billInfoReceiveRel.getBillInfo().getBillLookUp());
//                BillAccountTenantRel billAccountTenantRel = billAccountTenantRelService.getDefaultBilltAccountByTenantId(baseUser.getTenantId());
//                if (billAccountTenantRel != null && billAccountTenantRel.getAccountSeq() != null) {
//                    AccountBankRel accountBankRel = accountBankRelService.getById(billAccountTenantRel.getAccountSeq());
//                    if (accountBankRel != null && StringUtils.isNotBlank(accountBankRel.getAcctNo())) {
//                        orderFeeExt.setBillAcctNo(accountBankRel.getAcctNo());
//                    }
//                }
//                orderFeeExt.setOilBillType(billInfoReceiveRel.getDeduction() == null || !billInfoReceiveRel.getDeduction()
//                        ? OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1 : OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
//            } else {
//                OrderFeeExt oldorderFeeExt = orderFeeService.getOrderFeeExtByOrderId(orderInfo.getOrderId());
//                if (StringUtils.isBlank(oldorderFeeExt.getBillLookUp())
//                        || (orderFeeExt.getId() != null && orderFeeExt.getId() > 0)) {
//                    BillInfoReceiveRel billInfoReceiveRel = billInfoReceiveRelService.getDefaultBillInfoByTenantId(baseUser.getTenantId());
//                    if (billInfoReceiveRel == null || billInfoReceiveRel.getBillInfo() == null
//                            || StringUtils.isBlank(billInfoReceiveRel.getBillInfo().getBillLookUp())) {
//                        throw new BusinessException("????????????????????????????????????");
//                    }
//                    orderFeeExt.setBillLookUp(billInfoReceiveRel.getBillInfo().getBillLookUp());
//                    orderFeeExt.setBillAcctNo(billInfoReceiveRel.getBillInfo().getAcctNo());
//                    orderFeeExt.setOilBillType(billInfoReceiveRel.getDeduction() == null || !billInfoReceiveRel.getDeduction()
//                            ? OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1 : OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
//                } else {
//                    orderFeeExt.setBillLookUp(oldorderFeeExt.getBillLookUp());
//                    orderFeeExt.setBillAcctNo(oldorderFeeExt.getBillAcctNo());
//                    orderFeeExt.setOilBillType(oldorderFeeExt.getOilBillType());
//                }
//                //todo ????????????
//                //     SysContexts.getEntityManager().evict(oldorderFeeExt);
//            }
//        }


        // ????????????-????????????
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
            // ?????????????????????
			/*if (orderScheduler.getLicenceType() != null
					&& orderScheduler.getLicenceType().intValue() == SysStaticDataEnum.LICENCE_TYPE.ZC) {
				if (StringUtils.isNotBlank(orderGoods.getVehicleLengh()) && Float.parseFloat(orderGoods.getVehicleLengh()) >= 0) {
					if (!OrderUtil.checkVehicleLength(orderScheduler.getCarLengh(), orderGoods.getVehicleLengh())) {
						throw new BusinessException("??????????????????????????????????????????????????????");
					}
				}
				if (orderGoods.getVehicleStatus() != null && orderGoods.getVehicleStatus() >= 0) {
					if (!OrderUtil.checkVehicleStatus(orderScheduler.getCarStatus(), orderGoods.getVehicleStatus())) {
						throw new BusinessException("????????????????????????????????????????????????");
					}
				}
			}*/
            //?????????????????????????????????
//            if (orderScheduler.getVehicleClass() != null
//                    && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
//                if (orderScheduler.getLicenceType() != null
//                        && orderScheduler.getLicenceType().intValue() == SysStaticDataEnum.LICENCE_TYPE.TT) {
//                    if (orderScheduler.getTrailerId() == null || orderScheduler.getTrailerId() <= 0) {
//                        throw new BusinessException("??????????????????");
//                    }
//                }
//            }

            //??????????????????????????????????????????
			/*if(orderfee.getPreOilVirtualFee()!=null&&orderfee.getPreOilVirtualFee()>0
					&&(orderFeeExt.getOilFeePreStore()==null||OIL_FEE_PRESTORE.OIL_FEE_PRESTORE0==orderFeeExt.getOilFeePreStore())
					&&orderFeeExt.getOilConsumer()!=null&&orderFeeExt.getOilConsumer()==OIL_CONSUMER.SHARE) {
				IWithdrawalsTF iWithdrawalsTF =(IWithdrawalsTF)SysContexts.getBean("withdrawalsTF");
				ITenantTF tenantTF =(ITenantTF)SysContexts.getBean("tenantTF");
		        Long userId = tenantTF.getSysTenantDef(SysContexts.getCurrentOperator().getTenantId()).getAdminUser();
		        OrderAccountOut  orderAccountOut=iWithdrawalsTF.queryAccount(userId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
		        if(orderfee.getPreOilVirtualFee()>orderAccountOut.getOilRechargeBalance()) {
		        	throw new BusinessException("?????????????????????????????????");
		        }
			}*/
        }
        //???????????????
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                || orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            if (orderfee.getTotalFee() != null) {
                String maxAmount = getAmountLimitCfgVal(baseUser.getTenantId());
                if (StringUtils.isNotBlank(maxAmount) && !"-1".equals(maxAmount)) {
                    if (orderfee.getTotalFee().longValue() > Long.parseLong(maxAmount)) {
                        throw new BusinessException("?????????????????????" + objToFloatDiv100(maxAmount) + "???");
                    }
                }
                //??????????????????????????????????????????????????????????????????40%
                if (orderFeeExt.getOilBillType() == null || orderFeeExt.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    if ((orderScheduler.getVehicleClass() != null
                            && (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                            || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                            || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                    ))
                            || orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
                        SysCfg sysCfg = readisUtil.getSysCfg(TERRACE_BILL_OIL_FEE_PROPORTION, "0");
                        Double configProportion = null;
                        if (sysCfg != null) {
                            configProportion = Double.parseDouble(sysCfg.getCfgValue());
                        }
                        double oilFee = (orderfee.getPreOilFee() == null ? 0 : orderfee.getPreOilFee())
                                + (orderfee.getPreOilVirtualFee() == null ? 0 : orderfee.getPreOilVirtualFee());
                        double proportion = oilFee / orderfee.getTotalFee();
//                        if (proportion > configProportion) {
//                            throw new BusinessException("???????????????????????????????????????" + (configProportion * 100) + "%");
//                        }
                    }
                }
            }
        }

        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
            // ??????????????????
            checkCarUser(orderScheduler);
            if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
                if (orderInfo.getIsNeedBill() != null) {
//                    if (orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL) {
//                        throw new BusinessException("??????????????????????????????????????????");
//                    }
                }
            } else if (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            ) {
                if (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                    //?????????????????????????????????
                    if (StringUtils.isBlank(orderScheduler.getBillReceiverMobile())) {
                        throw new BusinessException("???????????????[" + orderScheduler.getPlateNumber() + "]??????????????????");
                    }
                }
                // ????????????????????????C??????????????????????????????????????????????????????????????????????????????????????????????????????
                if (orderInfo.getToTenantId() == null
                        && (orderScheduler.getIsCollection() == null || orderScheduler.getIsCollection().intValue() != OrderConsts.IS_COLLECTION.YES)) {
                    // C?????????
//                    if (OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL == orderInfo.getIsNeedBill()) {
//                        // ????????????????????????
//                        throw new BusinessException("?????????????????????????????????????????????????????????");
//                    }

                }
            }
        }
        //???????????????????????????
        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0 && orderInfo.getIsNeedBill() != null
                && orderInfo.getIsNeedBill() > 0) {
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
            checkBillAbility(baseUser.getTenantId(), orderInfo.getToTenantId(), orderInfo.getIsNeedBill());
        }

        /**?????????????????????**/
        if (orderFeeExt != null) {
            if (orderFeeExt.getOilAccountType() == null || orderFeeExt.getOilAccountType() < 1
                    || orderfee.getPreOilVirtualFee() == null || orderfee.getPreOilVirtualFee() <= 0) {
                orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
            }
            if (orderFeeExt.getOilAccountType() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
                orderFeeExt.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            } else if (orderFeeExt.getOilAccountType() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2
                    || orderFeeExt.getOilAccountType() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                orderFeeExt.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            } else {
                throw new BusinessException("??????????????????????????????");
            }
            if (orderFeeExt.getOilBillType() == null || orderFeeExt.getOilBillType() < 0
                    || orderfee.getPreOilVirtualFee() == null || orderfee.getPreOilVirtualFee() <= 0
                    || (orderInfo.getIsNeedBill() == null || orderInfo.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL)
                    || (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)) {
                orderFeeExt.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            }
            if (orderfee.getPreOilVirtualFee() != null && orderfee.getPreOilVirtualFee() > 0
                    && orderFeeExt.getOilBillType() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2
                    && orderFeeExt.getOilAccountType() != OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                throw new BusinessException("??????????????????????????????????????????????????????,??????????????????????????????????????????");
            }
            //????????????????????????????????????????????????????????????
            if (orderFeeExt.getOilAccountType() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2 && orderfee.getPreOilVirtualFee() != null && orderfee.getPreOilVirtualFee() > 0) {
                Long oilFee = orderfee.getPreOilVirtualFee();
                if (orderInfoExt.getPreAmountFlag() != null && orderInfoExt.getPreAmountFlag().intValue() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                    OrderFee orderFeeOld = orderFeeService.getOrderFee(orderInfo.getOrderId());
                    //todo ????????????
                    //  SysContexts.getEntityManager().evict(orderFeeOld);
                    oilFee = orderFeeOld.getPreOilFee() == null ? 0 : orderFeeOld.getPreOilFee() - orderfee.getPreOilVirtualFee();
                    oilFee = oilFee < 0 ? 0 : oilFee;
                }
                OilRechargeAccountDto oilBalanceMap = oilRechargeAccountService.querOilBalanceForOilAccountType(orderFeeExt.getOrderId(), baseUser);
                Long custOilBalance = oilBalanceMap.getCustOilBalance();//??????????????????=?????????+?????????+?????????
                custOilBalance = custOilBalance < 0 ? 0 : custOilBalance;
                Long rebateOilBalance = oilBalanceMap.getRebateOilBalance() < 0 ? 0 : oilBalanceMap.getRebateOilBalance();
                Long transferOilBalance = oilBalanceMap.getTransferOilBalance() < 0 ? 0 : oilBalanceMap.getTransferOilBalance();
                Long invoiceOilBalance = rebateOilBalance + transferOilBalance;
                if (orderScheduler.getVehicleClass() != null
                        && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    invoiceOilBalance += custOilBalance;
                }
                if (orderfee.getPreOilVirtualFee() > invoiceOilBalance) {
                    throw new BusinessException("??????????????????????????????????????????[" + invoiceOilBalance / 100.0 + "]???????????????????????????????????????!");
                }
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param orderScheduler
     * @param orderFeeExt
     * @param baseUser
     * @param isVer
     * @throws Exception
     */
    private void setDriverSubsidy(OrderScheduler orderScheduler, OrderFeeExt orderFeeExt,
                                  LoginInfo baseUser, Boolean isVer, Integer transitLineSize,
                                  List<OrderTransitLineInfo> transitLineInfos, String accessToken,
                                  OrderInfoExt orderInfoExt, OrderInfo orderInfo, OrderFee orderFee) {
        // ????????????????????????????????????1????????????????????????????????????
        Float arriveTime = orderScheduler.getArriveTime();
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                if (orderTransitLineInfo.getArriveTime() == null) {
                    orderTransitLineInfo.setArriveTime(0F);
                }
                arriveTime += orderTransitLineInfo.getArriveTime();
            }
        }
        CopilotMapVo carDriver = orderFeeService.culateSubsidy(orderScheduler.getCarDriverId(),
                baseUser.getTenantId(), orderScheduler.getDependTime(), arriveTime,
                orderScheduler.getOrderId(), false, transitLineSize);
        //todo ????????? ????????????
        if (orderFeeExt.getSalary() == null || orderFeeExt.getSalary() == 0) {
            orderFeeExt.setSalary(carDriver.getCopilotSalary());
        }
        if (orderFeeExt.getSubsidyDay() == null || orderFeeExt.getSubsidyDay() == 0) {
            orderFeeExt.setSubsidyDay(carDriver.getSubsidyCopilotDay().intValue());
        }
        orderFeeExt.setDriverDaySalary(carDriver.getCopilotDaySalary());
        orderFeeExt.setMonthSalary(carDriver.getMonthSubSalary());
        orderFeeExt.setSubsidyTime(carDriver.getCopilotSubsidyTime());
        orderFeeExt.setSalaryPattern(carDriver.getCopilotSalaryPattern());

        Long copilotDaySalary = 0L;
        // ??????????????????
        if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
            if (orderScheduler.getCarDriverId().longValue() == orderScheduler.getCopilotUserId()
                    .longValue()) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
            //todo 1
            CopilotMapVo copilotMap = orderFeeService.culateSubsidy(orderScheduler.getCopilotUserId(),
                    baseUser.getTenantId(), orderScheduler.getDependTime(), arriveTime, orderScheduler.getOrderId(), true, transitLineSize);
            orderFeeExt.setCopilotSalary(copilotMap.getCopilotSalary());
            orderFeeExt.setSubsidyCopilotDay(copilotMap.getSubsidyCopilotDay());
            orderFeeExt.setCopilotDaySalary(copilotMap.getCopilotDaySalary());
            copilotDaySalary = copilotMap.getCopilotDaySalary();
            orderFeeExt.setMonthSubSalary(copilotMap.getMonthSubSalary());
            orderFeeExt.setCopilotSubsidyTime(copilotMap.getCopilotSubsidyTime());
            orderFeeExt.setCopilotSalaryPattern(copilotMap.getCopilotSalaryPattern());

        }
        Integer driverSubsidyDay = 0;
        Integer copilotSubsidyDay = 0;
        /**
         * ?????????????????? 20181010
         */
        String subsidyTimeStr = "";
        String copilotSubsidyTime = "";
        if (StringUtils.isNotBlank(orderFeeExt.getSubsidyTime())) {
            String[] subsidyTimeArr = orderFeeExt.getSubsidyTime().split(" ");
            for (String subsidyTime : subsidyTimeArr) {
                if (StringUtils.isBlank(subsidyTime)) {
                    continue;
                }
                String date = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(date);
                Boolean isBoolean = false;
                if (isVer) {
                    //1.????????????????????????????????????
                    List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, orderScheduler.getCarDriverId(), orderScheduler.getTenantId(), false, orderScheduler.getOrderId());
                    if (list == null || list.size() == 0) {
                        list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, orderScheduler.getCarDriverId(), orderScheduler.getTenantId(), true, orderScheduler.getOrderId());
                    }
                    if (list == null || list.size() == 0) {
                        isBoolean = true;
                    }
                } else {
                    isBoolean = orderDriverSubsidyVerService.saveDriverSubsidy(orderScheduler.getOrderId(), orderScheduler.getCarDriverId(),
                            subsidyDate, carDriver.getCopilotDaySalary(), 11, baseUser);
                }
//                if (isBoolean) {
                subsidyTimeStr += " " + subsidyTime;
                driverSubsidyDay++;
//                }
            }
        }
        if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0 && StringUtils.isNotBlank(orderFeeExt.getCopilotSubsidyTime())) {
            String[] subsidyTimeArr = orderFeeExt.getCopilotSubsidyTime().split(" ");
            for (String subsidyTime : subsidyTimeArr) {
                if (StringUtils.isBlank(subsidyTime)) {
                    continue;
                }
                String data = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(data);
                Boolean isBoolean = false;
                if (isVer) {
                    //1.????????????????????????????????????
                    List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null,
                            orderScheduler.getCopilotUserId(), orderScheduler.getTenantId(),
                            false, orderScheduler.getOrderId());
                    if (list == null || list.size() == 0) {
                        list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null,
                                orderScheduler.getCopilotUserId(), orderScheduler.getTenantId(),
                                true, orderScheduler.getOrderId());
                    }
                    if (list == null || list.size() == 0) {
                        isBoolean = true;
                    }
                } else {
                    isBoolean = orderDriverSubsidyVerService.saveDriverSubsidy(orderScheduler.getOrderId(),
                            orderScheduler.getCopilotUserId(), subsidyDate, copilotDaySalary, 2, baseUser);
                }
                if (isBoolean) {
                    copilotSubsidyTime += " " + subsidyTime;
                    copilotSubsidyDay++;
                }
            }
        }
        //Todo ???????????????
        if (orderFeeExt.getSalary() == null || orderFeeExt.getSalary() == 0) {
            orderFeeExt.setSalary(orderFeeExt.getDriverDaySalary() != null ? driverSubsidyDay * orderFeeExt.getDriverDaySalary() : 0L);
        }
        if (orderFeeExt.getSubsidyDay() == null || orderFeeExt.getSubsidyDay() == 0) {
            orderFeeExt.setSubsidyDay(driverSubsidyDay);
        }
        orderFeeExt.setSubsidyTime(subsidyTimeStr);
        if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
            orderFeeExt.setCopilotSalary(orderFeeExt.getCopilotDaySalary() != null ? copilotSubsidyDay * orderFeeExt.getCopilotDaySalary() : 0L);
            orderFeeExt.setSubsidyCopilotDay(copilotSubsidyDay.longValue());
            orderFeeExt.setCopilotSubsidyTime(copilotSubsidyTime);
        }
        OrderFeeDto orderFeeDto = new OrderFeeDto();
        orderFeeDto.setVehicleCode(orderScheduler.getVehicleCode());
        orderFeeDto.setCarDriverId(orderScheduler.getCarDriverId());
        orderFeeDto.setLineId(orderScheduler.getSourceId());
        Number time = orderScheduler.getArriveTime();
        orderFeeDto.setArriveTime(time.toString());
        orderFeeDto.setPlateNumber(orderScheduler.getPlateNumber());
        Number distance = orderScheduler.getDistance() / 1000;
        orderFeeDto.setDistance(distance.floatValue());
        Long runWay = orderInfoExt.getRunWay() / 1000;
        orderFeeDto.setEmptyDistance(runWay);
        Number pontagePer = orderFeeExt.getPontagePer();
        orderFeeDto.setPontagePer(pontagePer.toString());
        Number runOil = orderInfoExt.getRunOil() / 100;
        orderFeeDto.setLoadEmptyOilCost(runOil.longValue());
        Number v = (orderInfoExt.getCapacityOil()) / 100;
        orderFeeDto.setLoadFullOilCost(v.longValue());
        orderFeeDto.setProvinceId(orderInfo.getSourceProvince());
        String dependTime = LocalDateTimeUtil.convertDateToString(orderScheduler.getDependTime());
        String substring = dependTime.substring(0, 16);
        orderFeeDto.setDependTime(substring);
        OrderFeeVo estimatedCosts = iCreditRatingRuleService.getEstimatedCosts(accessToken, orderFeeDto, null);
        orderFeeExt.setSubsidyDay(estimatedCosts.getDriverSubsidyDays());
        Number carDriverSubsidyValue = estimatedCosts.getCarDriverSubsidyValue();
        orderFeeExt.setSalary(carDriverSubsidyValue.longValue());
        Double pontagePerData = orderFeeExt.getPontagePer().doubleValue() / 100;
        Double mileageNumber = orderScheduler.getMileageNumber().doubleValue() / 1000;
        Double pontage = pontagePerData * mileageNumber;
        orderFeeExt.setPontage((long) (pontage * 100));
        orderFeeExt.setEstFee(orderFeeExt.getPontage() +
                (orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee()) + (orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee()) +
                orderFeeExt.getSalary());
    }

    /**
     * ????????????????????????
     *
     * @param orderScheduler
     * @param orderFeeExt
     * @param baseUser
     * @param isVer
     * @throws Exception
     */
    private void setDriverSubsidy(OrderScheduler orderScheduler, OrderFeeExt orderFeeExt, LoginInfo baseUser, boolean isVer, Integer transitLineSize, List<OrderTransitLineInfo> transitLineInfos) {
        // ????????????????????????????????????1????????????????????????????????????
        Float arriveTime = orderScheduler.getArriveTime();
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                arriveTime += orderTransitLineInfo.getArriveTime();
            }
        }

        CopilotMapVo carDriver = orderFeeService.culateSubsidy(orderScheduler.getCarDriverId(),
                baseUser.getTenantId(), orderScheduler.getDependTime(), arriveTime,
                orderScheduler.getOrderId(), false, transitLineSize);

        orderFeeExt.setSalary(carDriver.getCopilotSalary());
        orderFeeExt.setSubsidyDay(carDriver.getSubsidyCopilotDay().intValue());
        orderFeeExt.setDriverDaySalary(carDriver.getCopilotDaySalary());
        orderFeeExt.setMonthSalary(carDriver.getMonthSubSalary());
        orderFeeExt.setSubsidyTime(carDriver.getCopilotSubsidyTime());
        orderFeeExt.setSalaryPattern(carDriver.getCopilotSalaryPattern());

        Long copilotDaySalary = 0L;
        // ??????????????????
        if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
            if (orderScheduler.getCarDriverId().longValue() == orderScheduler.getCopilotUserId()
                    .longValue()) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
            CopilotMapVo copilotMap = orderFeeService.culateSubsidy(orderScheduler.getCopilotUserId(),
                    baseUser.getTenantId(), orderScheduler.getDependTime(), arriveTime, orderScheduler.getOrderId(), true, transitLineSize);
            orderFeeExt.setCopilotSalary(copilotMap.getCopilotSalary());
            orderFeeExt.setSubsidyCopilotDay(copilotMap.getSubsidyCopilotDay());
            orderFeeExt.setCopilotDaySalary(copilotMap.getCopilotDaySalary());
            copilotDaySalary = copilotMap.getCopilotDaySalary();
            orderFeeExt.setMonthSubSalary(copilotMap.getMonthSubSalary());
            orderFeeExt.setCopilotSubsidyTime(copilotMap.getCopilotSubsidyTime());
            orderFeeExt.setCopilotSalaryPattern(copilotMap.getCopilotSalaryPattern());
        }

        Integer driverSubsidyDay = 0;
        Integer copilotSubsidyDay = 0;
        /**
         * ?????????????????? 20181010
         */
        String subsidyTimeStr = "";
        String copilotSubsidyTime = "";
        if (StringUtils.isNotBlank(orderFeeExt.getSubsidyTime())) {
            String[] subsidyTimeArr = orderFeeExt.getSubsidyTime().split(" ");
            Date currDate = new Date();
            for (String subsidyTime : subsidyTimeArr) {
                if (StringUtils.isBlank(subsidyTime)) {
                    continue;
                }
                String date = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(date);
                Boolean isBoolean = false;
                if (isVer) {
                    //1.????????????????????????????????????
                    List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, orderScheduler.getCarDriverId(), orderScheduler.getTenantId(), false, orderScheduler.getOrderId());
                    if (list == null || list.size() == 0) {
                        list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, orderScheduler.getCarDriverId(), orderScheduler.getTenantId(), true, orderScheduler.getOrderId());
                    }
                    if (list == null || list.size() == 0) {
                        isBoolean = true;
                    }
                } else {
                    isBoolean = orderDriverSubsidyVerService.saveDriverSubsidy(orderScheduler.getOrderId(), orderScheduler.getCarDriverId(), subsidyDate, carDriver.getCopilotDaySalary(), 1, baseUser);
                }
                if (isBoolean) {
                    subsidyTimeStr += " " + subsidyTime;
                    driverSubsidyDay++;
                }
            }
        }

        if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0 && StringUtils.isNotBlank(orderFeeExt.getCopilotSubsidyTime())) {
            String[] subsidyTimeArr = orderFeeExt.getCopilotSubsidyTime().split(" ");
            Date currDate = new Date();
            for (String subsidyTime : subsidyTimeArr) {
                if (StringUtils.isBlank(subsidyTime)) {
                    continue;
                }
                String data = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(data);
                Boolean isBoolean = false;
                if (isVer) {
                    //1.????????????????????????????????????
                    List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, orderScheduler.getCopilotUserId(), orderScheduler.getTenantId(), false, orderScheduler.getOrderId());
                    if (list == null || list.size() == 0) {
                        list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, orderScheduler.getCopilotUserId(), orderScheduler.getTenantId(), true, orderScheduler.getOrderId());
                    }
                    if (list == null || list.size() == 0) {
                        isBoolean = true;
                    }
                } else {
                    isBoolean = orderDriverSubsidyVerService.saveDriverSubsidy(orderScheduler.getOrderId(), orderScheduler.getCopilotUserId(), subsidyDate, copilotDaySalary, 2, baseUser);
                }
                if (isBoolean) {
                    copilotSubsidyTime += " " + subsidyTime;
                    copilotSubsidyDay++;
                }
            }
        }

        orderFeeExt.setSalary(orderFeeExt.getDriverDaySalary() != null ? driverSubsidyDay * orderFeeExt.getDriverDaySalary() : 0L);
        orderFeeExt.setSubsidyDay(driverSubsidyDay);
        orderFeeExt.setSubsidyTime(subsidyTimeStr);
        if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
            orderFeeExt.setCopilotSalary(orderFeeExt.getCopilotDaySalary() != null ? copilotSubsidyDay * orderFeeExt.getCopilotDaySalary() : 0L);
            orderFeeExt.setSubsidyCopilotDay(copilotSubsidyDay.longValue());
            orderFeeExt.setCopilotSubsidyTime(copilotSubsidyTime);
        }

    }

    /**
     * ?????????????????????
     *
     * @param orderPaymentDaysInfo
     * @throws Exception
     */
    private void initOrderPaymentDaysInfo(OrderPaymentDaysInfo orderPaymentDaysInfo) {
        if (orderPaymentDaysInfo.getBalanceType() == null || orderPaymentDaysInfo.getBalanceType() <= 0) {
            if (orderPaymentDaysInfo.getPaymentDaysType() != null) {
                if (orderPaymentDaysInfo.getPaymentDaysType().intValue() == OrderConsts.PAYMENT_DAYS_TYPE.COST) {
                    orderPaymentDaysInfo.setBalanceType(SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER);
                    orderPaymentDaysInfo.setReconciliationTime(0);
                } else if (orderPaymentDaysInfo.getPaymentDaysType().intValue() == OrderConsts.PAYMENT_DAYS_TYPE.INCOME) {
                    orderPaymentDaysInfo.setBalanceType(SysStaticDataEnum.BALANCE_TYPE.PRE_ALL);
                }
                orderPaymentDaysInfo.setReciveTime(0);
                orderPaymentDaysInfo.setCollectionTime(0);
                orderPaymentDaysInfo.setInvoiceTime(0);
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param oilCardStr
     */
    private List<OrderOilCardInfo> getOrderOilCards(String oilCardStr, Integer paymentWay) {
        if (paymentWay == null || paymentWay != OrderConsts.PAYMENT_WAY.EXPENSE) {
            return null;
        }
        List<OrderOilCardInfo> orderOilCardInfos = null;
        if (StringUtils.isNotBlank(oilCardStr)) {
            orderOilCardInfos = new ArrayList<OrderOilCardInfo>();
            String[] arr = oilCardStr.split(";");
            for (String str : arr) {
                if (StringUtils.isNotBlank(str)) {
                    String[] arr2 = str.split(",");
                    OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
                    orderOilCardInfo.setOilCardNum(arr2[0]);
                    orderOilCardInfo.setOilFee(objToLongMul100(arr2[1]));
                    orderOilCardInfo.setCardType(Integer.parseInt(arr2[2]));
                    orderOilCardInfo.setCardChannel("true".equals(arr2[3]) ? OrderConsts.CARD_CHANNEL.ADD : OrderConsts.CARD_CHANNEL.VEHICLE_BIND);
                    orderOilCardInfos.add(orderOilCardInfo);
                }
            }
        }

        return orderOilCardInfos;
    }


    /**
     * ??????????????????
     *
     * @param orderGoods
     * @throws Exception
     */
    private void SaveTmpOrderCustLineInfo(OrderGoods orderGoods, LoginInfo user) {
        CustomerDto customerMap = new CustomerDto();
        if (orderGoods.getCustomUserId() != null) {
            customerMap.setCustomerId(orderGoods.getCustomUserId());
        }
        customerMap.setCompanyName(orderGoods.getCompanyName());
        customerMap.setCustomerName(orderGoods.getCustomName());
//        customerMap.setLineName();
//        customerMap.setLineTel();
//        customerMap.put("lineName", orderGoods.getLinkName());
//        customerMap.put("lineTel", orderGoods.getLinkPhone());
        customerMap.setAddress(orderGoods.getAddress());
        customerMap.setYongyouCode(orderGoods.getYongyouCode());

        customerMap.setReciveProvinceId(orderGoods.getReciveProvinceId());
        customerMap.setReciveCityId(orderGoods.getReciveCityId());
        customerMap.setReciveAddress(orderGoods.getReciveAddr());


        Long customerId = cmCustomerInfoService.doSaveTmpOrderCustLineInfo(customerMap, user);
        orderGoods.setCustomUserId(StringUtils.isNotBlank(customerId.toString()) ? customerId : -1L);
    }

    /**
     * ??????????????????
     *
     * @param orderScheduler
     * @throws Exception
     */
    private void checkCarUser(OrderScheduler orderScheduler) {
        if (orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId() > 0) {
            SysUser carOperator = sysUserService.getSysOperatorByUserDatainfoIdOrPhone(orderScheduler.getCarDriverId(),
                    "");
            if (carOperator == null) {
                log.error("???????????????????????????????????????????????????ID[" + orderScheduler.getCarDriverId() + "]");
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
                SysUser copilotOperator = sysUserService
                        .getSysOperatorByUserDatainfoIdOrPhone(orderScheduler.getCopilotUserId(), null);
                if (copilotOperator == null) {
                    log.error("???????????????????????????????????????????????????ID[" + orderScheduler.getCopilotUserId() + "]");

                    throw new BusinessException("?????????????????????????????????????????????");
                }
            }
        }
    }

    private void checkEstFee(Long estFee, Long salary, Long copilotSalary, Long preOilVirtualFee, Long preOilFee,
                             Long pontage) {
        if (estFee == null) {
            throw new BusinessException("????????????????????????");
        }
        if (salary != null && salary < 0) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (copilotSalary != null && copilotSalary < 0) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (preOilVirtualFee != null && preOilVirtualFee < 0) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (preOilFee != null && preOilFee < 0) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (pontage != null && pontage < 0) {
            throw new BusinessException("?????????????????????????????????");
        }

    }

    private void setOrderIdAndId(OrderInfo orderInfo, OrderFee orderFee, OrderGoods orderGoods, OrderInfoExt orderInfoExt
            , OrderFeeExt orderFeeExt, OrderScheduler orderScheduler, OrderDto orderDto) {
        Long id = orderDto.getOrderIdsDto().getOrderId();
        if (id != null && id > 0) {
            orderInfo.setOrderId(id);
            orderInfoExt.setOrderId(id);
            orderFee.setOrderId(id);
            orderGoods.setOrderId(id);
            orderFeeExt.setOrderId(id);
            orderScheduler.setOrderId(id);

            id = orderDto.getOrderIdsDto().getOrderId();
            orderInfo.setId(id);
            id = orderDto.getOrderIdsDto().getOrderInfoExtId();
            orderInfoExt.setId(id);
            id = orderDto.getOrderIdsDto().getOrderFeeId();
            orderFee.setId(id);
            id = orderDto.getOrderIdsDto().getOrderGoodsId();
            orderGoods.setId(id);
            id = orderDto.getOrderIdsDto().getOrderFeeExtId();
            orderFeeExt.setId(id);
            id = orderDto.getOrderIdsDto().getOrderSchedulerId();
            orderScheduler.setId(id);
        }
    }


    /**
     * ???????????????
     *
     * @param subWayListStr
     * @return
     */
    private List<OrderTransitLineInfo> setTransitLineInfos(List<subWayDto> subWayListStr) {
        if (null == subWayListStr || subWayListStr.isEmpty() || subWayListStr.size() == 0) {
            return null;
        }
        List<OrderTransitLineInfo> list = new ArrayList<OrderTransitLineInfo>();
        for (subWayDto subWayDto : subWayListStr) {
            OrderTransitLineInfo orderTransitLineInfo = new OrderTransitLineInfo();
            orderTransitLineInfo.setAddress(subWayDto.getDesAddress());
            orderTransitLineInfo.setArriveTime(subWayDto.getArriveTime());
            orderTransitLineInfo.setProvince(subWayDto.getDesProvince());
            orderTransitLineInfo.setRegion(subWayDto.getDesCity());
            orderTransitLineInfo.setCounty(subWayDto.getDesCounty());
//            orderTransitLineInfo.setProvinceName(DataFormat.getStringKey(map, "desProvinceName"));
//            orderTransitLineInfo.setRegionName(DataFormat.getStringKey(map,"desCityName"));
//            orderTransitLineInfo.setCountyName(DataFormat.getStringKey(map, "desCountyName"));
            orderTransitLineInfo.setNavigatLocation(subWayDto.getNavigatDesLocation());
            orderTransitLineInfo.setEand(subWayDto.getDesEand());
            orderTransitLineInfo.setNand(subWayDto.getDesNand());
            LocalDateTime carDependDate = subWayDto.getCarDependDate();
            LocalDateTime carStartDate = subWayDto.getCarStartDate();
            if (carDependDate != null) {
                orderTransitLineInfo.setCarDependDate(carDependDate);
            }
            if (carStartDate != null) {
                orderTransitLineInfo.setCarStartDate(carStartDate);
            }
            //orderTransitLineInfo.setDistance(DataFormat.getLongKey(map, "distance"));
            list.add(orderTransitLineInfo);
        }
        return list;
    }

    /**
     * ???????????????????????????
     *
     * @param plateNumber
     * @param orderScheduler
     * @throws Exception
     */
    public void getVehicleInfoByPlateNumber(String plateNumber, OrderScheduler orderScheduler,
                                            OrderInfo orderInfo, LoginInfo user) {
        List<VehicleDataInfoVxVo> list = vehicleDataInfoService.getVehicle(plateNumber, user.getTenantId(), false);

        if (list == null || list.size() == 0) {
            throw new BusinessException("?????????[" + plateNumber + "]????????????????????????????????????????????????????????????");
        }
        VehicleDataInfoVxVo vehicleInfo = list.get(list.size() - 1);
        Long tenantId = vehicleInfo.getTenantId();
        int vehicleClass = vehicleInfo.getVehicleClass();
        if ((tenantId != null && tenantId > 0 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)) {
            if (vehicleInfo.getAuthState() != 2) {
                throw new BusinessException("????????????????????????????????????");
            }
        } else if (vehicleInfo.getVehicleFlag() != null && vehicleInfo.getAuthState() != null && vehicleInfo.getVehicleFlag() == 1 && vehicleInfo.getAuthState() != 2) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (tenantId != null && tenantId > 0 && vehicleClass > SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            if (tenantDef == null) {
                log.error("???????????????????????????????????????[" + tenantId + "]");
                throw new BusinessException("?????????????????????????????????????????????");
            }
            orderInfo.setToTenantId(tenantId);
            orderInfo.setToTenantName(tenantDef.getName());
        }
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            orderScheduler.setBillReceiverMobile(vehicleInfo.getBillReceiverMobile());
            orderScheduler.setBillReceiverName(vehicleInfo.getBillReceiverName());
            orderScheduler.setBillReceiverUserId(vehicleInfo.getBillReceiverUserId());
            if (StringUtils.isBlank(orderScheduler.getBillReceiverMobile())) {
                throw new BusinessException("???????????????/???????????????[" + plateNumber + "]???????????????????????????????????????");
            }
        }
        orderScheduler.setCarDriverId(vehicleInfo.getUserId());
        orderScheduler.setCarDriverMan(vehicleInfo.getCarOwner());
        orderScheduler.setCarDriverPhone(vehicleInfo.getCarPhone());
        orderScheduler.setVehicleCode(vehicleInfo.getVehicleCode());
        orderScheduler.setPlateNumber(plateNumber);
        orderScheduler.setVehicleClass(vehicleClass);
        orderScheduler.setCarLengh(vehicleInfo.getVehicleLength() != null ? vehicleInfo.getVehicleLength() + "" : null);
        orderScheduler.setCarStatus(vehicleInfo.getVehicleStatus());
        orderScheduler.setLicenceType(vehicleInfo.getLicenceType());
    }

    /**
     * ??????TF??? ????????????
     *
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @throws Exception
     */
    private void billingSaveCheck(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler, OrderFee orderFee, List<OrderOilDepotScheme> orderOilDepotSchemeList, LoginInfo user) {
        //
        //todo ????????????
//        ObjectCompareUtils.isNotBlankNamesMap(orderInfo, getAddOrderInfoCheckNoBlank());
//        ObjectCompareUtils.isNotBlankNamesMap(orderGoods, getAddOrderGoodsCheckNoBlank(orderInfo.getOrderType()));
//        ObjectCompareUtils.isNotBlankNamesMap(orderScheduler, getAddOrderSchedulerCheckNoBlank(orderScheduler.getVehicleClass(), orderScheduler.getAppointWay()));

        if (null != orderFee.getPrePayCash() && orderFee.getPrePayCash() < 0) {
            throw new BusinessException("????????????????????????0");
        }
        if (null != orderFee.getPrePayEquivalenceCardAmount() && orderFee.getPrePayEquivalenceCardAmount() < 0) {
            throw new BusinessException("?????????????????????????????????0");
        }
        if (null != orderFee.getAfterPayCash() && orderFee.getAfterPayCash() < 0) {
            throw new BusinessException("????????????????????????0");
        }
        if (null != orderFee.getAfterPayEquivalenceCardAmount() && orderFee.getAfterPayEquivalenceCardAmount() < 0) {
            throw new BusinessException("???????????????????????????0");
        }

        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            Long tenantId = user.getTenantId();
            if (orderInfo.getToTenantId().equals(tenantId)) {
                throw new BusinessException("????????????????????????");
            }
            /*if(StringUtils.isBlank(orderFee.getAcctNo())){
                throw new BusinessException("???????????????????????????????????????");
            }
            if(StringUtils.isBlank(orderFee.getAcctName())){
                throw new BusinessException("????????????????????????????????????");
            }*/
        }
        //????????????-????????????
       /* if(orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR){
        	//?????????????????????
        	if(orderScheduler.getLicenceType()!=null && orderScheduler.getLicenceType().intValue()==SysStaticDataEnum.LICENCE_TYPE.ZC) {
	            if(StringUtils.isNotBlank(orderGoods.getVehicleLengh())&&!OrderUtil.checkVehicleLength(orderScheduler.getCarLengh(), orderGoods.getVehicleLengh())){
	                throw new BusinessException("????????????????????????????????????");
	            }
	            if (orderGoods.getVehicleStatus()!=null&&orderGoods.getVehicleStatus()>=0&&!OrderUtil.checkVehicleStatus(orderScheduler.getCarStatus(), orderGoods.getVehicleStatus())) {
	                throw new BusinessException("??????????????????????????????");
	            }
        	}
        }*/
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR || orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (orderScheduler.getCopilotUserId() != null && orderScheduler.getCarDriverId() != null && orderScheduler.getCopilotUserId().longValue() == orderScheduler.getCarDriverId().longValue()) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
                //?????????
                return;
            }
            if (orderFee.getTotalFee() != null) {
                String maxAmount = this.getAmountLimitCfgVal(user.getTenantId());
                if (StringUtils.isNotBlank(maxAmount) && !"-1".equals(maxAmount)) {
                    if (orderFee.getTotalFee().longValue() > Long.parseLong(maxAmount)) {
                        throw new BusinessException("?????????????????????" + objToFloatDiv100(maxAmount) + "???");
                    }
                }
            }
        }
        List list = new ArrayList();
        for (OrderOilDepotScheme orderOilDepotScheme : orderOilDepotSchemeList) {
            if (orderOilDepotScheme.getOilDepotId() != null) {
                if (list.contains(orderOilDepotScheme.getOilDepotId())) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                } else {
                    list.add(orderOilDepotScheme.getOilDepotId());
                }
            }
        }
    }


    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param tenantId      ????????????
     * @param tranTenantId  ????????????
     * @param isNeedBilling ???????????????
     * @throws Exception
     */
    public void checkBillAbility(Long tenantId, Long tranTenantId, Integer isNeedBilling) {

        if (isNeedBilling == null || isNeedBilling == OrderConsts.IS_NEED_BILL.NOT_NEED_BILL) {
            // ?????????
            return;
        }
        if (tranTenantId == null || tranTenantId < 0) {
            // ???????????????
            return;
        }

        //????????????????????????????????????
        boolean openBill = billSettingService.getInvokeAble(tranTenantId);

        if (OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL == isNeedBilling) {
            // ???????????????
            if (!openBill) {
                throw new BusinessException("??????????????????????????????");
            }
        }
    }

    /**
     * ????????????
     *
     * @throws Exception
     */
    private void getFee(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee, OrderFeeExt orderFeeExt
            , OrderScheduler orderScheduler, OrderGoods orderGoods, int transitLineSize, LoginInfo user) {

        Map<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("carDriverId", orderScheduler.getCarDriverId());
        parmas.put("copilotUserId", orderScheduler.getCopilotUserId());
        parmas.put("tenantId", user.getTenantId());
        parmas.put("dependTime", LocalDateTimeUtil.convertDateToString(orderScheduler.getDependTime()));
        parmas.put("arriveTime", orderScheduler.getArriveTime());
        parmas.put("plateNumber", orderScheduler.getPlateNumber());
        parmas.put("distance", orderScheduler.getMileageNumber());
        parmas.put("pontagePer", orderFeeExt.getPontagePer());
        parmas.put("nand", orderGoods.getNand());
        parmas.put("eand", orderGoods.getEand());
        if (orderInfo.getDesRegion() != null) {
            parmas.put("region", readisUtil.getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName());
        }
        parmas.put("provinceId", orderInfo.getSourceProvince());
        parmas.put("emptyDistance", orderInfoExt.getRunWay());
        parmas.put("oilPrice", orderFeeExt.getOilPrice());
        parmas.put("transitLineSize", transitLineSize);

        Map<String, Object> map = getEstimatedCosts(parmas);
        orderFeeExt.setPontage(map.get("pontageFee") == null ? 0 : Long.valueOf(map.get("pontageFee").toString()));
        orderFeeExt.setSalary(map.get("carDriverSubsidy") == null ? 0 : Long.valueOf(map.get("carDriverSubsidy").toString()));
        orderFeeExt.setCopilotSalary(map.get("copilotSubsidy") == null ? 0 : Long.valueOf(map.get("copilotSubsidy").toString()));
        orderFeeExt.setOilPrice(map.get("oilPrice") == null ? 0 : Long.valueOf(map.get("oilPrice").toString()));
        orderFee.setPreOilFee(map.get("oilSelfTotal") == null ? 0 : Long.valueOf(map.get("oilSelfTotal").toString()));
        orderFee.setPreOilVirtualFee(map.get("oilSelfVirtual") == null ? 0 : Long.valueOf(map.get("oilSelfVirtual").toString()));
        orderFeeExt.setOilLitreVirtual(map.get("oilLitreVirtual") == null ? 0 : Long.valueOf(map.get("oilLitreVirtual").toString()));
        orderFeeExt.setOilLitreTotal(map.get("oilTotal") == null ? 0L : (long) (Double.parseDouble(map.get("oilTotal").toString())));
        orderFeeExt.setOilLitreEntity(orderFeeExt.getOilLitreTotal() - orderFeeExt.getOilLitreVirtual());
        orderFeeExt.setSubsidyTime(map.get("carDriverSubsidyDate") == null ? null : map.get("carDriverSubsidyDate").toString());
        orderFeeExt.setCopilotSubsidyTime(map.get("copilotSubsidyDate") == null ? null : map.get("copilotSubsidyDate").toString());
        orderFeeExt.setEstFee(orderFeeExt.getPontage() + orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee()
                + orderFeeExt.getSalary() + orderFeeExt.getCopilotSalary());
    }


    public Map<String, Object> getEstimatedCosts(Map<String, Object> params) {
        Long carDriverId = DataFormat.getLongKey(params, "carDriverId");
        Long copilotUserId = DataFormat.getLongKey(params, "copilotUserId");
        if (carDriverId != null && copilotUserId != null && carDriverId > 0 && copilotUserId > 0) {
            if (carDriverId.longValue() == copilotUserId.longValue()) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
        }

        Long tenantId = DataFormat.getLongKey(params, "tenantId");
        LocalDateTime dependTime = null;
        if (StringUtils.isNotBlank(DataFormat.getStringKey(params, "dependTime"))) {
            dependTime = LocalDateTimeUtil.convertStringToDate(DataFormat.getStringKey(params, "dependTime"));
        } else {
            throw new BusinessException("?????????????????????!");
        }
        Float arriveTime = null;
        if (StringUtils.isNotBlank(DataFormat.getStringKey(params, "arriveTime"))) {
            arriveTime = Float.parseFloat(DataFormat.getStringKey(params, "arriveTime"));
        } else {
            throw new BusinessException("?????????????????????!");
        }

        String plateNumber = (String) params.get("plateNumber");
        Long distance = DataFormat.getLongKey(params, "distance") < 0 ? 0 : DataFormat.getLongKey(params, "distance");
        Long pontagePer = DataFormat.getLongKey(params, "pontagePer") < 0 ? 0 : DataFormat.getLongKey(params, "pontagePer");
        String nand = (String) params.get("nand");
        String eand = (String) params.get("eand");
        String region = (String) params.get("region");
        Long provinceId = DataFormat.getLongKey(params, "provinceId");
        Long orderId = DataFormat.getLongKey(params, "orderId");
        Long emptyDistance = DataFormat.getLongKey(params, "emptyDistance");
        String oilPriceStr = DataFormat.getStringKey(params, "oilPrice");
        Integer transitLineSize = DataFormat.getIntKey(params, "transitLineSize");
        Integer copilotSubsidyDay = 0;
        String copilotSubsidyDate = "";
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map> driverSubsidyDays = new ArrayList<>();
        retMap.put("copilotSubsidy", 0);
        if (copilotUserId != null && copilotUserId > 0) {
            Map<String, Object> copilotSubsidyMap = orderFeeExtService.culateSubsidy(copilotUserId, tenantId, dependTime, arriveTime, orderId, true, transitLineSize);
            if (StringUtils.isNotBlank(OrderUtil.objToStringEmpty(copilotSubsidyMap.get("date")))) {
                String[] subsidyTimeArr = OrderUtil.objToStringEmpty(copilotSubsidyMap.get("date")).split(" ");

                for (String subsidyTime : subsidyTimeArr) {
                    if (StringUtils.isBlank(subsidyTime)) {

                    }
                    int year = LocalDateTime.now().getYear();
                    LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDateYMD(year + "-" + subsidyTime);
                    //1.????????????????????????????????????
                    List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, copilotUserId, tenantId, false, orderId);
                    if (list == null || list.size() == 0) {
                        list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, copilotUserId, tenantId, true, orderId);
                    }
                    if (list == null || list.size() == 0) {
                        copilotSubsidyDate += " " + subsidyTime;
                        copilotSubsidyDay++;
                    }
                }
            }
            retMap.put("copilotSubsidy", OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")) == null ? 0L : copilotSubsidyDay * OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")));
            retMap.put("copilotSubsidyDate", copilotSubsidyDate);
            if (StringUtils.isNotBlank(copilotSubsidyDate)) {
                UserDataInfo user = userDataInfoService.getUserDataInfo(copilotUserId);
                Map map = new ConcurrentHashMap();
                map.put("userName", user != null ? user.getLinkman() : null);
                map.put("subsidyFee", OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")) == null ? 0L : copilotSubsidyDay * OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")));
                map.put("subsidyDay", copilotSubsidyDate);
                driverSubsidyDays.add(map);
            }
        }
        String carDriverSubsidyDate = "";
        Integer driverSubsidyDay = 0;
        Map<String, Object> carDriverSubsidyMap = orderFeeExtService.culateSubsidy(carDriverId, tenantId, dependTime, arriveTime, orderId, false, transitLineSize);
        if (StringUtils.isNotBlank(OrderUtil.objToStringEmpty(carDriverSubsidyMap.get("date")))) {
            String[] subsidyTimeArr = OrderUtil.objToStringEmpty(carDriverSubsidyMap.get("date")).split(" ");
            for (String subsidyTime : subsidyTimeArr) {
                if (StringUtils.isBlank(subsidyTime)) {
                    continue;
                }
                String data = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(data);
                //1.????????????????????????????????????
                List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, carDriverId, tenantId, false, orderId);
                if (list == null || list.size() == 0) {
                    list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, carDriverId, tenantId, true, orderId);
                }
                if (list == null || list.size() == 0) {
                    carDriverSubsidyDate += " " + subsidyTime;
                    driverSubsidyDay++;
                }
            }
        }
        retMap.put("carDriverSubsidyDate", carDriverSubsidyDate);
        retMap.put("carDriverSubsidy", OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")) == null ? 0L : driverSubsidyDay * OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")));
        if (StringUtils.isNotBlank(carDriverSubsidyDate)) {
            UserDataInfo user = userDataInfoService.getUserDataInfo(carDriverId);
            Map map = new ConcurrentHashMap();
            map.put("userName", user != null ? user.getLinkman() : null);
            map.put("subsidyFee", OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")) == null ? 0L : driverSubsidyDay * OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")));
            map.put("subsidyDay", carDriverSubsidyDate);
            driverSubsidyDays.add(map);
        }
        // ?????????
        if (emptyDistance == null || emptyDistance < 0) {
            emptyDistance = getPreOrderDistance(plateNumber, dependTime, nand, eand, region, tenantId, orderId);
        }
        retMap.put("emptyDistance", emptyDistance);
        Long pontageFee = culateETCFee(distance, pontagePer, emptyDistance);
        retMap.put("pontageFee", pontageFee);


        Long emptyOilCostPer = DataFormat.getLongKey(params, "loadEmptyOilCost") < 0 ? 0 : DataFormat.getLongKey(params, "loadEmptyOilCost");
        Long oilCostPer = DataFormat.getLongKey(params, "loadFullOilCost") < 0 ? 0 : DataFormat.getLongKey(params, "loadFullOilCost");
        Float oilTotal = culateOilConsumption(distance, emptyOilCostPer, oilCostPer, emptyDistance);
        retMap.put("oilTotal", oilTotal);

        if (StringUtils.isNotBlank(oilPriceStr)) {
            retMap.put("oilPrice", oilPriceStr);
        } else {
            OilPriceProvince oilPriceProvince = oilPriceProvinceService.getOilPriceProvince(provinceId.intValue());
            if (oilPriceProvince != null) {
                retMap.put("oilPrice", oilPriceProvince.getOilPrice());
            } else {
                retMap.put("oilPrice", "0");
            }
        }
        Long driverSwitchSubsidy = 0L;
        if (orderId != null && orderId > 0) {
            List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrder(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
            if (switchInfos != null && switchInfos.size() > 0) {
                for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
                    Map<String, Object> driverMap = orderFeeExtService.culateSubsidy(orderDriverSwitchInfo.getReceiveUserId(), tenantId, dependTime, arriveTime, orderId, false, transitLineSize);
                    if (driverMap != null) {
                        Long fee = (Long) driverMap.get("fee");
                        String date = (String) driverMap.get("date");
                        driverSwitchSubsidy += fee;
                        Map map = new ConcurrentHashMap();
                        map.put("userName", orderDriverSwitchInfo.getReceiveUserName());
                        map.put("subsidyFee", fee);
                        map.put("subsidyDay", date);
                        driverSubsidyDays.add(map);
                    }
                }
            }
        }
        retMap.put("driverSubsidyDays", driverSubsidyDays);
        retMap.put("driverSwitchSubsidy", driverSwitchSubsidy);
        return retMap;
    }

    /**
     * ????????????????????????
     *
     * @param plateNumber
     * @param dependTime
     * @param nand
     * @param eand
     * @param region
     * @param tenantId
     * @return ?????? ???
     * @throws Exception
     */
    public Long getPreOrderDistance(String plateNumber, LocalDateTime dependTime, String nand, String eand, String region,
                                    Long tenantId, Long orderId) {
        Map<String, Object> orderIdMap = orderSchedulerService.getPreOrderIdByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        Long distance = 0L;
        if (orderIdMap != null) {
            // ????????????
            String orgNand = "";
            String orgEand = "";
            String orgRegion = "";
            if (OrderConsts.TableType.ORI == (Integer) orderIdMap.get("type")) {
                OrderGoods orderGoods = orderGoodsService.getOrderGoods((Long) orderIdMap.get("orderId"));
                OrderInfo orderInfo = this.getOrder((Long) orderIdMap.get("orderId"));
                orgNand = orderGoods.getNandDes();
                orgEand = orderGoods.getEandDes();
                orgRegion = readisUtil.getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
                // orgRegion = orderInfo.getDesRegionName();
            } else if (OrderConsts.TableType.HIS == (Integer) orderIdMap.get("type")) {
                OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH((Long) orderIdMap.get("orderId"));
                OrderInfoH orderInfoH = orderInfoHService.getOrderH((Long) orderIdMap.get("orderId"));
                orgNand = orderGoodsH.getNandDes();
                orgEand = orderGoodsH.getEandDes();
                orgRegion = readisUtil.getSysStaticData("SYS_CITY", orderInfoH.getDesRegion().toString()).getCodeName();
                //   orgRegion = orderInfoH.getDesRegionName();
            }
            if (nand != null && !nand.equals("") && orgNand != null && !"".equals(orgNand) && orgEand != null && !"".equals(orgEand)
            ) {
                if (!nand.equals(orgNand) || !eand.equals(orgEand)) {
                    DirectionDto distanceMap = GpsUtil.getDirection(orgNand, orgEand, nand, eand, "driving", null, 12, orgRegion,
                            region);
                    // ?????????????????????
                    distance = Long.valueOf(distanceMap.getDistance() + "");
                }
            }
        }
        return distance;
    }

    public Long culateETCFee(Long distance, Long etcFeePer, Long emptyDistance) {
        if (emptyDistance == null) {
            emptyDistance = 0L;
        }
        return (distance + 0) * etcFeePer / 1000;
    }

    public Float culateOilConsumption(Long distance, Long emptyOilCostPer, Long oilCostPer, Long emptyDistance) {
        // ?????????
        Float emptyDistanceFloat = Float.valueOf(emptyDistance) / 100000f;
        Float distanceFloat = Float.valueOf(distance) / 100000f;

        int oilInt = Math.round((emptyDistanceFloat * emptyOilCostPer + oilCostPer * distanceFloat) * 100f);
        DecimalFormat fnum = new DecimalFormat("##0.00");
        String dd = fnum.format((oilInt / 100f) / 100);
        return Float.parseFloat(dd);
    }

    /**
     * ???????????????????????????
     *
     * @return -1 ????????????
     * @throws Exception
     */
    public String getAmountLimitCfgVal(Long tenantId) {
        Long maxAmount = payFeeLimitService.getAmountLimitCfgVal(tenantId, SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_BID_AMOUNT_101);
        return (maxAmount < 0 ? 0 : maxAmount) + "";
    }

    /**
     * ?????????????????????
     * <p>
     * ???????????????????????????????????????????????????????????????
     *
     * @param orderfee
     * @param orderScheduler
     * @param orderType
     * @param isUpdate
     * @param orderId
     * @return
     * @throws Exception
     */
    private boolean startProcess(OrderFee orderfee, OrderFeeExt orderFeeExt, OrderScheduler orderScheduler, OrderInfoExt orderInfoExt,
                                 Integer orderType, Boolean isUpdate, Long orderId, String accessToken, LoginInfo user) {
        Map<String, Object> processMap = new HashMap<String, Object>();

        // ????????????--????????????--????????? ?????????????????????
        if (orderType == OrderConsts.OrderType.FIXED_LINE && orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                || (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT))) {
            processMap.put(AuditConsts.RuleMapKey.GUIDE_PRICE, orderfee.getGuidePrice());
            processMap.put(AuditConsts.RuleMapKey.TOTAL_FEE, orderfee.getTotalFee());
        }
        processMap.put(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE_STANDARD, orderfee.getPreTotalScaleStandard());
        processMap.put(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE, orderfee.getPreTotalScale());

        processMap.put(AuditConsts.RuleMapKey.PRE_OIL_TOTAL_STANDARD,
                ((orderfee.getPreOilScaleStandard() == null ? 0 : orderfee.getPreOilScaleStandard().longValue())
                        + (orderfee.getPreOilVirtualScaleStandard() == null ? 0
                        : orderfee.getPreOilVirtualScaleStandard().longValue())));

        processMap.put(AuditConsts.RuleMapKey.PRE_OIL_TOTAL,
                ((orderfee.getPreOilScale() == null ? 0 : orderfee.getPreOilScale().longValue())
                        + (orderfee.getPreOilVirtualScale() == null ? 0
                        : orderfee.getPreOilVirtualScale().longValue())));

        processMap.put(AuditConsts.RuleMapKey.PRE_ETC_TOTAL_STANDARD, orderfee.getPreEtcScaleStandard());
        processMap.put(AuditConsts.RuleMapKey.PRE_ETC_TOTAL, orderfee.getPreEtcScale());

        processMap.put(AuditConsts.RuleMapKey.IS_UPDATE, isUpdate);

        boolean reuslt = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderId, SysOperLogConst.BusiCode.OrderInfo, processMap, accessToken);

        if (reuslt) {
            saveOrderFeeExtAudit(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderId, processMap, orderFeeExt, user);
        }

        return reuslt;
    }

    /**
     * ?????????????????????
     * <p>
     * ???????????????????????????????????????????????????????????????
     *
     * @param orderfee
     * @param orderScheduler
     * @param orderType
     * @param isUpdate
     * @param orderId
     * @return
     * @throws Exception
     */
    private boolean startProcessOrder(OrderFee orderfee, OrderFeeExt orderFeeExt, OrderScheduler orderScheduler, OrderInfoExt orderInfoExt,
                                      Integer orderType, Boolean isUpdate, Long orderId, String accessToken, LoginInfo user) {
        Map<String, Object> processMap = new HashMap<String, Object>();

        // ????????????--????????????--????????? ?????????????????????
        if (orderType == OrderConsts.OrderType.FIXED_LINE && orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                || (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT))) {
            processMap.put(AuditConsts.RuleMapKey.GUIDE_PRICE, orderfee.getGuidePrice());
            processMap.put(AuditConsts.RuleMapKey.TOTAL_FEE, orderfee.getTotalFee());
        }
        processMap.put(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE_STANDARD, orderfee.getPreTotalScaleStandard());
        processMap.put(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE, orderfee.getPreTotalScale());

        processMap.put(AuditConsts.RuleMapKey.PRE_OIL_TOTAL_STANDARD,
                ((orderfee.getPreOilScaleStandard() == null ? 0 : orderfee.getPreOilScaleStandard().longValue())
                        + (orderfee.getPreOilVirtualScaleStandard() == null ? 0
                        : orderfee.getPreOilVirtualScaleStandard().longValue())));

        processMap.put(AuditConsts.RuleMapKey.PRE_OIL_TOTAL,
                ((orderfee.getPreOilScale() == null ? 0 : orderfee.getPreOilScale().longValue())
                        + (orderfee.getPreOilVirtualScale() == null ? 0
                        : orderfee.getPreOilVirtualScale().longValue())));

        processMap.put(AuditConsts.RuleMapKey.PRE_ETC_TOTAL_STANDARD, orderfee.getPreEtcScaleStandard());
        processMap.put(AuditConsts.RuleMapKey.PRE_ETC_TOTAL, orderfee.getPreEtcScale());

        processMap.put(AuditConsts.RuleMapKey.IS_UPDATE, isUpdate);

        boolean reuslt = auditService.startProcessOrder(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderId, SysOperLogConst.BusiCode.OrderInfo, processMap, accessToken);

        if (reuslt) {
            saveOrderFeeExtAudit(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, orderId, processMap, orderFeeExt, user);
        }

        return reuslt;
    }

    /**
     * ????????????????????? ??????????????? ???????????? ????????????????????????????????????????????????????????????
     *
     * @param auditCode
     * @param busiId
     * @param params
     * @param orderFeeExt
     * @throws Exception
     */
    private void saveOrderFeeExtAudit(String auditCode, Long busiId, Map<String, Object> params,
                                      OrderFeeExt orderFeeExt, LoginInfo user) {
        Map<String, Boolean> checkResultMap = auditConfigService.checkNodePreRule(auditCode, busiId, params, user);
        if (checkResultMap != null) {
            boolean isUpdate = false;
            if (checkResultMap.get(AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE) != null
                    && checkResultMap.get(AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE)) {
                orderFeeExt.setTotalAuditSts(com.youming.youche.finance.constant.OrderConsts.AUDIT_STS.YES);
                isUpdate = true;
            }
            if (checkResultMap.get(AuditConsts.RULE_CODE.HIGHER_PREPAYMENT) != null
                    && checkResultMap.get(AuditConsts.RULE_CODE.HIGHER_PREPAYMENT)) {
                orderFeeExt.setPreTotalAuditSts(com.youming.youche.finance.constant.OrderConsts.AUDIT_STS.YES);
                isUpdate = true;
            }

            if (checkResultMap.get(AuditConsts.RULE_CODE.HIGHER_OIL) != null && checkResultMap.get(AuditConsts.RULE_CODE.HIGHER_OIL)) {
                orderFeeExt.setPreOilAuditSts(com.youming.youche.finance.constant.OrderConsts.AUDIT_STS.YES);
                isUpdate = true;
            }

            if (checkResultMap.get(AuditConsts.RULE_CODE.LOWER_OIL) != null && checkResultMap.get(AuditConsts.RULE_CODE.LOWER_OIL)) {
                orderFeeExt.setPreOilAuditSts(com.youming.youche.finance.constant.OrderConsts.AUDIT_STS.YES);
                isUpdate = true;
            }

            if (checkResultMap.get(AuditConsts.RULE_CODE.HIGHER_ETC) != null && checkResultMap.get(AuditConsts.RULE_CODE.HIGHER_ETC)) {
                orderFeeExt.setPreEtcAuditSts(com.youming.youche.finance.constant.OrderConsts.AUDIT_STS.YES);
                isUpdate = true;
            }

            if (checkResultMap.get(AuditConsts.RULE_CODE.LOWER_ETC) != null && checkResultMap.get(AuditConsts.RULE_CODE.LOWER_ETC)) {
                orderFeeExt.setPreEtcAuditSts(com.youming.youche.finance.constant.OrderConsts.AUDIT_STS.YES);
                isUpdate = true;
            }

            if (isUpdate) {
                orderFeeExtService.saveOrUpdate(orderFeeExt);
            }

        }

    }

    private void setOrderFee(OrderFee orderFee, OrderDto orderDto, LoginInfo user) {
        //??????
        //???????????????
        boolean orderCostPermission = sysRoleService.hasOrderCostPermission(user);
        String tmpStr = orderDto.getCustomerInfoVo().getPrePayCash().toString();
        orderFee.setPrePayCash(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
        if (null != orderDto.getCustomerInfoVo().getPrePayEquivalenceCardType()) {
            tmpStr = orderDto.getCustomerInfoVo().getPrePayEquivalenceCardType().toString();
        } else {
            tmpStr = null;
        }
        orderFee.setPrePayEquivalenceCardType(StringUtils.isNotBlank(tmpStr) && !"-1".equals(tmpStr) ? Integer.parseInt(tmpStr) : null);
        tmpStr = orderDto.getCustomerInfoVo().getPrePayEquivalenceCardAmount().toString();
        orderFee.setPrePayEquivalenceCardAmount(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
        tmpStr = orderDto.getCustomerInfoVo().getPrePayEquivalenceCardNumber();
        orderFee.setPrePayEquivalenceCardNumber("".equals(tmpStr) ? null : tmpStr);
        tmpStr = orderDto.getCustomerInfoVo().getAfterPayCash().toString();
        orderFee.setAfterPayCash(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
        tmpStr = orderDto.getCustomerInfoVo().getAfterPayEquivalenceCardAmount().toString();
        orderFee.setAfterPayEquivalenceCardAmount(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
        if (null != orderDto.getCustomerInfoVo().getPrePayEquivalenceCardType()) {
            tmpStr = orderDto.getCustomerInfoVo().getPrePayEquivalenceCardType().toString();
        } else {
            tmpStr = null;
        }
        orderFee.setAfterPayEquivalenceCardType(StringUtils.isNotBlank(tmpStr) && !"-1".equals(tmpStr) ? Integer.parseInt(tmpStr) : null);
        tmpStr = orderDto.getCustomerInfoVo().getAfterPayEquivalenceCardNumber();
        orderFee.setAfterPayEquivalenceCardNumber("".equals(tmpStr) ? null : tmpStr);
        if (null != orderDto.getCustomerInfoVo().getAfterPayAcctType()) {
            tmpStr = orderDto.getCustomerInfoVo().getAfterPayAcctType().toString();
            orderFee.setAfterPayAcctType(StringUtils.isNotBlank(tmpStr) && !"-1".equals(tmpStr) ? Integer.parseInt(tmpStr) : null);
        } else {
            orderFee.setAfterPayAcctType(null);
        }


        tmpStr = orderDto.getCustomerInfoVo().getPriceUnit().toString();
        orderFee.setPriceUnit(Double.parseDouble(StringUtils.isNotBlank(tmpStr) ? tmpStr : "0"));
        if (orderDto.getCustomerInfoVo().getPriceEnum() != null) {
            tmpStr = orderDto.getCustomerInfoVo().getPriceEnum().toString();
        } else {
            tmpStr = null;
        }

        orderFee.setPriceEnum(StringUtils.isNotBlank(tmpStr) && !"-1".equals(tmpStr) ? Integer.parseInt(tmpStr) : null);
        tmpStr = orderDto.getCustomerInfoVo().getCostPrice().toString();
        orderFee.setCostPrice(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);

        //????????????
        orderFee.setAcctName(orderDto.getDispatchInfoVo().getAcctName());
        orderFee.setAcctNo(orderDto.getDispatchInfoVo().getAcctNo());
        Integer appointWay = orderDto.getDispatchInfoVo().getAppointWay();
        Integer vehicleClass = -1;
        if (null != orderDto.getDispatchInfoVo().getVehicleClass()) {
            vehicleClass = orderDto.getDispatchInfoVo().getVehicleClass();
        }

        Integer paymentWay = -1;
        if (null != orderDto.getDispatchInfoVo().getPaymentWay()) {
            paymentWay = orderDto.getDispatchInfoVo().getPaymentWay();
        }

        if (OrderConsts.PAYMENT_WAY.EXPENSE != paymentWay) {
            tmpStr = orderDto.getDispatchInfoVo().getGuidePrice();
            orderFee.setGuidePrice(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getInsuranceFee();
            orderFee.setInsuranceFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getTotalFee();
            orderFee.setTotalFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
        }

        if (OrderConsts.PAYMENT_WAY.CONTRACT == paymentWay || OrderConsts.AppointWay.APPOINT_TENANT == appointWay || OrderConsts.AppointWay.APPOINT_CAR == appointWay && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 != vehicleClass) {
            if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == vehicleClass && OrderConsts.PAYMENT_WAY.CONTRACT == paymentWay) {
                orderFee.setGuidePrice(orderFee.getTotalFee());
            } else {
                tmpStr = orderDto.getDispatchInfoVo().getGuidePrice();
                orderFee.setGuidePrice(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            }

        } else {
            //??????????????????????????????????????????
            orderFee.setTotalFee(null);
        }
        //???????????????
        if ((OrderConsts.AppointWay.APPOINT_CAR == appointWay || appointWay == 6) && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == vehicleClass) {
            if (orderCostPermission && paymentWay <= 0) {
                throw new BusinessException("????????????????????????");
            }

            if (OrderConsts.PAYMENT_WAY.COST == paymentWay) {
                tmpStr = orderDto.getDispatchInfoVo().getOilSelfVirtual();
                orderFee.setPreOilVirtualFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
                tmpStr = orderDto.getDispatchInfoVo().getOilSelfEntity();
                orderFee.setPreOilFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
                return;
            } else if (OrderConsts.PAYMENT_WAY.EXPENSE == paymentWay) {
                tmpStr = orderDto.getDispatchInfoVo().getPreOilVirtualFee();
                orderFee.setPreOilVirtualFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
                tmpStr = orderDto.getDispatchInfoVo().getPreOilFee();
                orderFee.setPreOilFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
                orderFee.setPreTotalFee(orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee());
                return;
            }
        }

        Long lineId = orderDto.getCustomerInfoVo().getSourceId();
        //?????????????????????????????????????????????????????????
        if ((lineId == null || lineId <= 0) && OrderConsts.AppointWay.APPOINT_LOCAL == appointWay) {
            tmpStr = orderDto.getCustomerInfoVo().getGuidePrice();
            orderFee.setGuidePrice(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getTotalFee();
            orderFee.setTotalFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
        }

        //????????????
        if ((OrderConsts.AppointWay.APPOINT_CAR == appointWay || appointWay == 6) && (OrderConsts.PAYMENT_WAY.CONTRACT == paymentWay || vehicleClass != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1)
                || OrderConsts.AppointWay.APPOINT_TENANT == appointWay) {
            tmpStr = orderDto.getDispatchInfoVo().getPreTotalFee();
            orderFee.setPreTotalFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreCashFee();
            orderFee.setPreCashFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreOilVirtualFee();
            orderFee.setPreOilVirtualFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreOilFee();
            orderFee.setPreOilFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreEtcFee();
            orderFee.setPreEtcFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getFinalFee();
            orderFee.setFinalFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getFinalScale();
            orderFee.setFinalScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getArrivePaymentFee();
            orderFee.setArrivePaymentFee(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            if (orderFee.getInsuranceFee() != null && orderFee.getInsuranceFee() > 0 && orderFee.getInsuranceFee() > orderFee.getFinalFee()) {
                throw new BusinessException("??????????????????????????????");
            }

            //???4????????? eg:32% =  3200
            tmpStr = orderDto.getDispatchInfoVo().getPreTotalScale();
            orderFee.setPreTotalScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreCashScale();
            orderFee.setPreCashScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreOilVirtualScale();
            orderFee.setPreOilVirtualScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreOilScale();
            orderFee.setPreOilScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreEtcScale();
            orderFee.setPreEtcScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getArrivePaymentFeeScale();
            orderFee.setArrivePaymentFeeScale(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);


            tmpStr = orderDto.getDispatchInfoVo().getPreTotalScaleStandard();
            orderFee.setPreTotalScaleStandard(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) * 100 : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreCashScaleStandard();
            orderFee.setPreCashScaleStandard(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) * 100 : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreOilVirtualScaleStandard();
            orderFee.setPreOilVirtualScaleStandard(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) * 100 : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreOilScaleStandard();
            orderFee.setPreOilScaleStandard(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) * 100 : 0);
            tmpStr = orderDto.getDispatchInfoVo().getPreEtcScaleStandard();
            orderFee.setPreEtcScaleStandard(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) * 100 : 0);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param orderFeeExt
     * @param
     */
    private void setOrderFeeExt(OrderFeeExt orderFeeExt, OrderDto orderDto) {
        int vehicleClass = -1;
        if (null != orderDto.getDispatchInfoVo().getVehicleClass()) {
            vehicleClass = orderDto.getDispatchInfoVo().getVehicleClass();
        }
        int paymentWay = 0;
        if (null != orderDto.getDispatchInfoVo().getPaymentWay()) {
            paymentWay = orderDto.getDispatchInfoVo().getPaymentWay();
        }

        String tmpStr = null;
        if (orderDto.getDispatchInfoVo().getOilAccountType() != null) {
            tmpStr = orderDto.getDispatchInfoVo().getOilAccountType().toString();
        } else {
            tmpStr = null;
        }

        if (StringUtils.isNotBlank(tmpStr)) {
            orderFeeExt.setOilAccountType(Integer.parseInt(tmpStr));
        } else {
            orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
        }
        if (orderDto.getDispatchInfoVo().getOilBillType() != null) {
            tmpStr = orderDto.getDispatchInfoVo().getOilBillType().toString();
        } else {
            tmpStr = null;
        }

        if (StringUtils.isNotBlank(tmpStr)) {
            orderFeeExt.setOilBillType("true".equals(tmpStr) ? OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2 : OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
        } else {
            orderFeeExt.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
        }
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            if (OrderConsts.PAYMENT_WAY.EXPENSE != paymentWay) {
                tmpStr = orderDto.getDispatchInfoVo().getPontagePer();
                orderFeeExt.setPontagePer(objToLongMul100(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getOilPrice().toString();
                orderFeeExt.setOilPrice(objToLongMul100(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getPontage();
                orderFeeExt.setPontage(objToLongMul100(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getEstFee();
                orderFeeExt.setEstFee(objToLongMul100(tmpStr));
            } else {
                orderFeeExt.setEstFee(objToLongMul100(orderDto.getDispatchInfoVo().getPreOilFee())
                        + objToLongMul100(orderDto.getDispatchInfoVo().getPreOilVirtualFee()));
            }

            if (OrderConsts.PAYMENT_WAY.COST == paymentWay) {
                tmpStr = orderDto.getDispatchInfoVo().getUserSubsidy();
                orderFeeExt.setSalary(objToLongMul100(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getCopilotSubsidy();
                orderFeeExt.setCopilotSalary(objToLongMul100(tmpStr));

                tmpStr = orderDto.getDispatchInfoVo().getCarDriverSubsidyDate();
                if (StringUtils.isNotBlank(tmpStr)) {
                    orderFeeExt.setSubsidyTime(tmpStr);
                    orderFeeExt.setSubsidyDay(Integer.parseInt(tmpStr));
                }
                tmpStr = orderDto.getDispatchInfoVo().getCopilotSubsidyDate();
                orderFeeExt.setCopilotSubsidyTime(tmpStr);
                tmpStr = orderDto.getDispatchInfoVo().getOilLitreTotal();
                orderFeeExt.setOilLitreTotal(objToLongMul100(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getOilLitreVirtual();
                orderFeeExt.setOilLitreVirtual(objToLongMul100(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getOilLitreEntity();
                orderFeeExt.setOilLitreEntity(objToLongMul100(tmpStr));
            }

        }

        //???????????????????????????
       /* orderFeeExt.setIncomeCashFee(objToLong0(incomeIn.getIncomeCashFee()));
        orderFeeExt.setIncomeEtcFee(objToLong0(incomeIn.getIncomeEtcFee()));
        orderFeeExt.setIncomeFinalFee(objToLong0(incomeIn.getIncomeFinalFee()));
        orderFeeExt.setIncomeInsuranceFee(objToLong0(incomeIn.getIncomeInsuranceFee()));
        orderFeeExt.setIncomeOilFee(objToLong0(incomeIn.getIncomeOilFee()));
        orderFeeExt.setIncomeOilVirtualFee(objToLong0(incomeIn.getIncomeOilVirtualFee()));*/
        //orderFeeExt.setIncomePaymentDays(objToInteger0(incomeIn.getIncomePaymentDays()));
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


    private void setOrderInfo(OrderInfo orderInfo, OrderDto orderDto) {
        orderInfo.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.NEW_INPUT);
        orderInfo.setIsNeedBill(orderDto.getDispatchInfoVo().getIsNeedBill());
        orderInfo.setRemark(orderDto.getDispatchInfoVo().getRemark());
        orderInfo.setFromTenantId(orderDto.getDispatchInfoVo().getFromTenantId());
//??????????????????FromOrderId?????????????????????????????????
        OrderInfo orderInfo1 = this.selectByOrderId(orderDto.getDispatchInfoVo().getOrderId());
        if (orderInfo1 != null && orderInfo1.getFromOrderId() != null && orderInfo1.getFromOrderId() > 0) {
            orderInfo.setFromOrderId(orderInfo1.getFromOrderId());
        } else {
            orderInfo.setFromOrderId(orderDto.getDispatchInfoVo().getFromTenantId());
        }
        orderInfo.setToOrderId(orderDto.getDispatchInfoVo().getToOrderId());
        orderInfo.setOrderState(orderDto.getDispatchInfoVo().getOrderState());
        orderInfo.setOrgId(orderDto.getDispatchInfoVo().getOrgId());
        orderInfo.setOrderId(orderDto.getDispatchInfoVo().getWorkId());
        int vehicleClass = -1;
        Integer appointWay = orderDto.getDispatchInfoVo().getAppointWay();
        DispatchInfoVo dispatchInfoVo = orderDto.getDispatchInfoVo();
        if (null != dispatchInfoVo.getVehicleClass()) {
            vehicleClass = orderDto.getDispatchInfoVo().getVehicleClass();
        }
        String tmpStr = orderDto.getDispatchInfoVo().getIsAgent();
        if (appointWay != null && (OrderConsts.AppointWay.APPOINT_TENANT == appointWay)
                || StringUtils.isNotBlank(tmpStr) && "true".equals(tmpStr)) {
            orderInfo.setToTenantId(orderDto.getDispatchInfoVo().getToTenantId());
            orderInfo.setToTenantName(orderDto.getDispatchInfoVo().getToTenantName());
        } else if (OrderConsts.AppointWay.APPOINT_CAR == appointWay && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 != vehicleClass && vehicleClass > 0) {
            //????????????????????????ToTenantId???????????????????????????????????????
            orderInfo.setTenantId(orderDto.getDispatchInfoVo().getTenantId());
            orderInfo.setTenantName(orderDto.getDispatchInfoVo().getTenantName());
        }

        Long lineId = orderDto.getCustomerInfoVo().getSourceId();

        if (lineId != null && lineId > 0) {
            orderInfo.setOrderType(OrderConsts.OrderType.FIXED_LINE);
        } else if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
            orderInfo.setOrderType(OrderConsts.OrderType.ONLINE_RECIVE);
        } else {
            orderInfo.setOrderType(OrderConsts.OrderType.TEMPORARY_LINE);
        }

        orderInfo.setSourceProvince(orderDto.getCustomerInfoVo().getSourceProvince());
        orderInfo.setSourceRegion(orderDto.getCustomerInfoVo().getSourceRegion());
        orderInfo.setSourceCounty(orderDto.getCustomerInfoVo().getSourceCounty());
        orderInfo.setDesProvince(orderDto.getCustomerInfoVo().getDesProvince());
        orderInfo.setDesRegion(orderDto.getCustomerInfoVo().getDesRegion());
        orderInfo.setDesCounty(orderDto.getCustomerInfoVo().getDesCounty());

        //orderInfo.setIsTransit(DataFormat.getIntKey(map,"dispatchInfo.isTransit"));
    }


    private void setGoodsInfo(OrderGoods orderGoods, OrderDto orderDto) {
        //??????????????????
        orderGoods.setAddress(orderDto.getCustomerInfoVo().getAddress());
        orderGoods.setLineName(orderDto.getCustomerInfoVo().getLinkName());
        orderGoods.setLinePhone(orderDto.getCustomerInfoVo().getLinkPhone());
        orderGoods.setCustomUserId(orderDto.getCustomerInfoVo().getCustomUserId());
        orderGoods.setCompanyName(orderDto.getCustomerInfoVo().getCompanyName());
        orderGoods.setCustomName(orderDto.getCustomerInfoVo().getCustomName());
        if (StringUtils.isBlank(orderGoods.getCustomName())) {
            orderGoods.setCustomName(orderGoods.getCompanyName());
        }
        orderGoods.setGoodsName(orderDto.getCustomerInfoVo().getGoodsName());
        orderGoods.setGoodsType(orderDto.getCustomerInfoVo().getGoodsType());
        if (orderGoods.getGoodsType() < 0) {
            orderGoods.setGoodsType(SysStaticDataEnum.GOODS_TYPE.COMMON_GOODS);
        }
        if (orderDto.getCustomerInfoVo().getWeight() != null && !orderDto.getCustomerInfoVo().getWeight().equals("")) {
            orderGoods.setWeight(Float.valueOf(orderDto.getCustomerInfoVo().getWeight()));
        }
        if (orderDto.getCustomerInfoVo().getSquare() != null && !orderDto.getCustomerInfoVo().getSquare().equals("")) {
            orderGoods.setSquare(Float.valueOf(orderDto.getCustomerInfoVo().getSquare()));
        }

        orderGoods.setVehicleLengh(orderDto.getCustomerInfoVo().getVehicleLengh());
        orderGoods.setVehicleStatus(orderDto.getCustomerInfoVo().getVehicleStatus());

        orderGoods.setReciveType(orderDto.getCustomerInfoVo().getReciveType());
        orderGoods.setReciveName(orderDto.getCustomerInfoVo().getReciveName());
        orderGoods.setRecivePhone(orderDto.getCustomerInfoVo().getRecivePhone());

        orderGoods.setLocalUser(orderDto.getDispatchInfoVo().getLocalUser());
        orderGoods.setLocalUserName(orderDto.getDispatchInfoVo().getLocalUserName());
        orderGoods.setLocalPhone(orderDto.getDispatchInfoVo().getLocalPhone());

        orderGoods.setYongyouCode(orderDto.getCustomerInfoVo().getYongyouCode());

        orderGoods.setCustType(1);//?
        orderGoods.setSource(orderDto.getCustomerInfoVo().getSource());
        orderGoods.setDes(orderDto.getCustomerInfoVo().getDes());
        orderGoods.setAddrDtl(orderDto.getCustomerInfoVo().getAddrDtl());
        orderGoods.setDesDtl(orderDto.getCustomerInfoVo().getDesDtl());
        orderGoods.setNand(orderDto.getCustomerInfoVo().getNand());
        orderGoods.setEand(orderDto.getCustomerInfoVo().getEand());
        orderGoods.setNavigatSourceLocation(orderDto.getCustomerInfoVo().getNavigatSourceLocation());
        orderGoods.setNandDes(orderDto.getCustomerInfoVo().getNandDes());
        orderGoods.setEandDes(orderDto.getCustomerInfoVo().getEandDes());
        orderGoods.setNavigatDesLocation(orderDto.getCustomerInfoVo().getNavigatDesLocation());
        orderGoods.setContactName(orderDto.getCustomerInfoVo().getContactName());
        orderGoods.setContactPhone(orderDto.getCustomerInfoVo().getContactPhone());

        //????????????
        orderGoods.setReciveProvinceId(orderDto.getCustomerInfoVo().getReciveProvinceId());
        ///   orderGoods.setReciveProvinceName(orderDto.getCustomerInfoVo().getReciveProvinceName());
        orderGoods.setReciveCityId(orderDto.getCustomerInfoVo().getReciveCityId());
        //  orderGoods.setReciveCityName(orderDto.getCustomerInfoVo().getReciveCityName());
        orderGoods.setReciveAddr(orderDto.getCustomerInfoVo().getReciveAddr());
        if (orderGoods.getReciveCityId() == null || orderGoods.getReciveCityId() == -1) {
            orderGoods.setReciveCityId(null);
        }
        if (orderGoods.getReciveProvinceId() == null || orderGoods.getReciveProvinceId() == -1) {
            orderGoods.setReciveProvinceId(null);
        }

        //????????????
        String tempStr = orderDto.getCustomerInfoVo().getCustomNumber();
        orderGoods.setCustomNumber(tempStr);
    }

    /**
     * ????????????
     *
     * @param workId
     * @param orderId
     */
    public void handleWorkOrder(Long workId, Long orderId, LoginInfo user) {
        if (workId == null || workId <= 0) {
            return;
        }
        WorkOrderInfo workOrderInfo = workOrderInfoService.getById(workId);
        if (workOrderInfo == null) {
            throw new BusinessException("?????????????????????");
        }
        LambdaQueryWrapper<WorkOrderRel> lambda = new QueryWrapper<WorkOrderRel>().lambda();
        lambda.eq(WorkOrderRel::getWorkId, workId);
        List<WorkOrderRel> rel = workOrderRelService.list(lambda);
        if (rel != null && rel.size() > 0 && !rel.get(0).getOrderId().equals(orderId)) {
            throw new BusinessException("??????????????????????????????");
        } else if (orderId != null && orderId > 0) {
            WorkOrderRel workOrderRel = new WorkOrderRel();
            workOrderRel.setOrderId(orderId);
            workOrderRel.setTenantId(user.getTenantId());
            workOrderRel.setWorkId(workId);
            workOrderRelService.save(workOrderRel);
        }
        if (workOrderInfo.getDealState() != OrderConsts.DEAL_STATE.DEAL_STATE1) {
            workOrderInfo.setDealState(OrderConsts.DEAL_STATE.DEAL_STATE1);
            workOrderInfoService.update(workOrderInfo);
        }

    }

    /**
     * ????????????????????????????????????
     *
     * @param totalFee
     * @param preTotalFee
     * @param preCashFee
     * @param preOilVirtualFee
     * @param preOilFee
     * @param preEtcFee
     * @param finalFee
     * @param insuranceFee
     * @param arrivePaymentFee
     */
    private void checkTotalFee(Long totalFee, Long preTotalFee, Long preCashFee, Long preOilVirtualFee, Long preOilFee,
                               Long preEtcFee, Long finalFee, Long insuranceFee, Long arrivePaymentFee) {
        Long comparePrice = 0L;
        if (totalFee == null) {
            throw new BusinessException("????????????????????????");
        }
        if (preCashFee != null) {
            if (preCashFee < 0) {
                throw new BusinessException("??????????????????????????????");
            } else {
                comparePrice += preCashFee;
            }
        }
        if (preOilVirtualFee != null) {
            if (preOilVirtualFee < 0) {
                throw new BusinessException("?????????????????????????????????");
            } else {
                comparePrice += preOilVirtualFee;
            }
        }
        if (preOilFee != null) {
            if (preOilFee < 0) {
                throw new BusinessException("????????????????????????????????????");
            } else {
                comparePrice += preOilFee;
            }
        }
        if (preEtcFee != null) {
            if (preEtcFee < 0) {
                throw new BusinessException("??????ETC????????????????????????");
            } else {
                comparePrice += preEtcFee;
            }
        }
        if (arrivePaymentFee != null) {
            if (arrivePaymentFee < 0) {
                throw new BusinessException("?????????????????????????????????");
            } else {
                comparePrice += arrivePaymentFee;
            }
        }
        if (finalFee != null) {
            if (finalFee < 0) {
                throw new BusinessException("??????????????????????????????");
            } else {
                comparePrice += finalFee;
            }
        }
        // if(insuranceFee!=null){
        // comparePrice+=insuranceFee;
        // }
        if (comparePrice.longValue() != totalFee.longValue()) {
            throw new BusinessException("???????????????????????????????????????????????????????????????");
        }
    }


    /**
     * ???????????????????????????
     *
     * @param orderInfoExt
     * @param
     */
    private void setOrderInfoExt(OrderInfoExt orderInfoExt, OrderDto orderDto) {
        int vehicleClass = -1;
        if (null != orderDto.getDispatchInfoVo().getVehicleClass()) {
            vehicleClass = orderDto.getDispatchInfoVo().getVehicleClass();
        }
        int paymentWay = 0;
        if (null != orderDto.getDispatchInfoVo().getPaymentWay()) {
            paymentWay = orderDto.getDispatchInfoVo().getPaymentWay();
        }
        String tmpStr = null;
        //??????????????????
        if (orderDto.getDispatchInfoVo().getIsBackhaul() != null) {
            tmpStr = orderDto.getDispatchInfoVo().getIsBackhaul().toString();
        } else {
            tmpStr = null;
        }

        if (StringUtils.isNotBlank(tmpStr) && ("1".equals(tmpStr) || "true".equals(tmpStr))) {
            orderInfoExt.setIsBackhaul(1);
            tmpStr = orderDto.getDispatchInfoVo().getBackhaulPrice().toString();
            orderInfoExt.setBackhaulPrice(StringUtils.isNotBlank(tmpStr) ? objToLongMul100(tmpStr) : 0);
            tmpStr = orderDto.getDispatchInfoVo().getBackhaulLinkMan();
            orderInfoExt.setBackhaulLinkMan(tmpStr);
            tmpStr = orderDto.getDispatchInfoVo().getBackhaulLinkManBill();
            orderInfoExt.setBackhaulLinkManBill(tmpStr);
            if (orderDto.getDispatchInfoVo().getBackhaulLinkManId() != null) {
                tmpStr = orderDto.getDispatchInfoVo().getBackhaulLinkManId().toString();
            } else {
                tmpStr = null;
            }
//                orderDto.getDispatchInfoVo().getBackhaulLinkManId().toString();
            orderInfoExt.setBackhaulLinkManId(objToLongNull(tmpStr));
        } else {
            orderInfoExt.setIsBackhaul(0);
            orderInfoExt.setBackhaulPrice(null);
        }
        //?????????
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            tmpStr = orderDto.getDispatchInfoVo().getEmptyOilCostPer();
            orderInfoExt.setCapacityOil(StringUtils.isNotBlank(tmpStr) ? objToFloatMul100(tmpStr) : 0);
            if (null != orderDto.getDispatchInfoVo().getOilCostPer()) {
                tmpStr = orderDto.getDispatchInfoVo().getOilCostPer();
                orderInfoExt.setRunOil(StringUtils.isNotBlank(tmpStr) ? objToFloatMul100(tmpStr) : 0);
            } else {
                orderInfoExt.setRunOil(0F);
            }

            tmpStr = orderDto.getDispatchInfoVo().getIsUseCarOilCost();
            orderInfoExt.setIsUseCarOilCost(Integer.parseInt(StringUtils.isBlank(tmpStr) ? "0" : tmpStr));
            //????????????
            DispatchInfoVo dispatchInfoVo = orderDto.getDispatchInfoVo();
            if (dispatchInfoVo.getRunWay() != null) {
                tmpStr = dispatchInfoVo.getRunWay().toString();
                orderInfoExt.setRunWay(mul(tmpStr, "1000"));
            } else {
                orderInfoExt.setRunWay(-1L);
            }


            if (OrderConsts.PAYMENT_WAY.CONTRACT != paymentWay && paymentWay != OrderConsts.PAYMENT_WAY.COST && paymentWay != OrderConsts.PAYMENT_WAY.EXPENSE) {
                orderInfoExt.setPaymentWay(null);
            } else {
                orderInfoExt.setPaymentWay(paymentWay);
            }

            //?????????????????????????????????????????????????????????????????????
            if (dispatchInfoVo.getOilUseType() != null) {
                tmpStr = orderDto.getDispatchInfoVo().getOilUseType().toString();
            } else {
                tmpStr = null;
            }

            orderInfoExt.setOilUseType(StringUtils.isNotBlank(tmpStr) ? Integer.parseInt(tmpStr) : null);
            if (orderInfoExt.getOilUseType() != null && orderInfoExt.getOilUseType() == OrderConsts.OIL_USE_TYPE.TENANTOIL) {
            	/*tmpStr=DataFormat.getStringKey(map, "dispatchInfo.oilIsNeedBill");
            	orderInfoExt.setOilIsNeedBill("1".equals(tmpStr)||"true".equals(tmpStr)?1:0);*/
                orderInfoExt.setOilIsNeedBill(1);
            } else {
                orderInfoExt.setOilIsNeedBill(0);
            }

        } else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            orderInfoExt.setPaymentWay(OrderConsts.PAYMENT_WAY.CONTRACT);
        } else {
            orderInfoExt.setPaymentWay(null);
        }
    }


    private void setDispatchInfo(OrderScheduler orderScheduler, List<OrderOilDepotScheme> orderOilDepotSchemeList, OrderDto orderDto) {
        Integer vehicleClass = orderDto.getDispatchInfoVo().getVehicleClass();
        Integer appointWay = orderDto.getDispatchInfoVo().getAppointWay();
        String tmpStr = null;
        if (null != orderDto.getDispatchInfoVo().getOrderId()) {
            tmpStr = orderDto.getDispatchInfoVo().getOrderId().toString();
            orderScheduler.setOrderId(objToLongNull(tmpStr));
        }

        if (orderDto.getCustomerInfoVo().getContractId() != null) {
            tmpStr = orderDto.getCustomerInfoVo().getContractId().toString();
            orderScheduler.setClientContractId(objToLong0(tmpStr));
        } else {
            orderScheduler.setClientContractId(0L);
        }

        tmpStr = orderDto.getCustomerInfoVo().getContractUrl();
        orderScheduler.setClientContractUrl(tmpStr);
        orderScheduler.setAppointWay(appointWay);
        tmpStr = orderDto.getCustomerInfoVo().getIsUrgent().toString();
        orderScheduler.setIsUrgent(StringUtils.isNotBlank(tmpStr) ? Integer.parseInt(tmpStr) : 0);
        tmpStr = orderDto.getDispatchInfoVo().getReciveAddr();
        orderScheduler.setReciveAddr(tmpStr);

        //????????????
        if (appointWay == OrderConsts.AppointWay.APPOINT_LOCAL) {
            tmpStr = orderDto.getDispatchInfoVo().getLocalUserName();
            orderScheduler.setDispatcherName(tmpStr);
            tmpStr = orderDto.getDispatchInfoVo().getLocalUser().toString();
            orderScheduler.setDispatcherId(objToLong0(tmpStr));
            tmpStr = orderDto.getDispatchInfoVo().getLocalPhone();
            orderScheduler.setDispatcherBill(tmpStr);
        }
        //????????????
        if (appointWay == OrderConsts.AppointWay.APPOINT_CAR || appointWay == 6) {
            orderScheduler.setVehicleClass(vehicleClass);
            this.setAppointCarInfo(orderScheduler, orderOilDepotSchemeList, orderDto);

            tmpStr = orderDto.getDispatchInfoVo().getIsAgent();
            if ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4)
                    && StringUtils.isNotBlank(tmpStr) && "true".equals(tmpStr)) {
                tmpStr = orderDto.getDispatchInfoVo().getAgentPhone();
                //||!CommonUtil.isCheckPhone(tmpStr)
                if (StringUtils.isBlank(tmpStr)) {
                    throw new BusinessException("?????????????????????????????????????????????");
                }
                orderScheduler.setCollectionUserPhone(tmpStr);
                tmpStr = orderDto.getDispatchInfoVo().getAgentAccountName();
                orderScheduler.setCollectionUserName(tmpStr);
                orderScheduler.setCollectionName(tmpStr);
                orderScheduler.setIsCollection(1);
                tmpStr = orderDto.getDispatchInfoVo().getAgentId().toString();
                orderScheduler.setCollectionUserId(StringUtils.isBlank(tmpStr) ? null : Long.parseLong(tmpStr));

            } else {
                orderScheduler.setIsCollection(null);
            }
            //???????????????
            if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                tmpStr = orderDto.getDispatchInfoVo().getBillReceiverMobile();
                orderScheduler.setBillReceiverMobile(tmpStr);
                tmpStr = orderDto.getDispatchInfoVo().getBillReceiverName();
                orderScheduler.setBillReceiverName(tmpStr);
                tmpStr = orderDto.getDispatchInfoVo().getBillReceiverUserId();
                orderScheduler.setBillReceiverUserId(StringUtils.isNotBlank(tmpStr) ? Long.valueOf(tmpStr) : null);
            } else {
                orderScheduler.setBillReceiverMobile(null);
                orderScheduler.setBillReceiverName(null);
                orderScheduler.setBillReceiverUserId(null);
            }
        }


        if (orderScheduler.getCopilotUserId() == null || orderScheduler.getCopilotUserId() <= 0) {
            orderScheduler.setCopilotMan(null);
            orderScheduler.setCopilotPhone(null);
        }
        if (orderDto.getDepartmentId() != null) {
            orderScheduler.setDepartmentId(orderDto.getDepartmentId());
            SysOrganize sysOrganize = sysOrganizeService.getById(orderDto.getDepartmentId());
            if (sysOrganize != null && sysOrganize.getOrgName() != null) {
                orderScheduler.setDepartmentName(sysOrganize.getOrgName());
            }
        }

        tmpStr = String.valueOf(orderDto.getCustomerInfoVo().getCmMileageNumber());
        orderScheduler.setDistance(mul(StringUtils.isBlank(tmpStr) ? "0" : tmpStr, "1000"));
        tmpStr = String.valueOf(orderDto.getCustomerInfoVo().getMileageNumber());
        Long mul = mul(StringUtils.isBlank(tmpStr) ? "0" : tmpStr, "1000");
        orderScheduler.setMileageNumber(mul.intValue());
        tmpStr = orderDto.getCustomerInfoVo().getDependTime() + ":00";
        // tmpStr = orderDto.getCustomerInfoVo().getDependTime();
        orderScheduler.setDependTime(LocalDateTimeUtil.convertStringToDate(tmpStr));
        if (orderDto.getCustomerInfoVo().getArriveTime() != null) {
            tmpStr = orderDto.getCustomerInfoVo().getArriveTime().toString();
        } else {
            tmpStr = null;
        }
        orderScheduler.setArriveTime(objToFloat(tmpStr));
        if (orderDto.getCustomerInfoVo().getSourceId() != null) {
            tmpStr = orderDto.getCustomerInfoVo().getSourceId().toString();
        } else {
            tmpStr = null;
        }
        orderScheduler.setSourceId(DataFormat.getLongKey(tmpStr));
        tmpStr = orderDto.getCustomerInfoVo().getLineCodeRule();
        orderScheduler.setSourceCode(tmpStr);
        tmpStr = orderDto.getCustomerInfoVo().getSourceName();
        orderScheduler.setSourceName(tmpStr);

    }

    private void setCollectionInfo(OrderInfo orderInfo, OrderScheduler orderScheduler, Boolean orderCostPermission, String token, LoginInfo user) {
        if (!orderCostPermission) {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        //??????????????? ?????????????????????
        if (StringUtils.isBlank(orderScheduler.getCollectionUserPhone())) {
            throw new BusinessException("??????????????????????????????");
        }
        if (StringUtils.isBlank(orderScheduler.getCollectionName())) {
            throw new BusinessException("??????????????????????????????");
        }
        com.youming.youche.record.domain.tenant.TenantReceiverRel tenantReceiverRel = userReceiverInfoService.checkUserReceiver(orderScheduler.getCollectionUserPhone(), token);
        if (tenantReceiverRel == null || tenantReceiverRel.getId() == null) {
            tenantReceiverRelService.createUserReceiverInfo(orderScheduler.getCollectionUserPhone(),
                    orderScheduler.getCollectionName(), orderScheduler.getCollectionName(), null, null, user);
        } else if (!orderScheduler.getCollectionName().equals(tenantReceiverRel.getReceiverName())) {
            UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(tenantReceiverRel.getUserId());
            if (null != userReceiverInfo) {
                userReceiverInfo.setReceiverName(orderScheduler.getCollectionName());
                userReceiverInfo.setUpdateTime(LocalDateTime.now());
                userReceiverInfoService.updateUserReceiverInfo(userReceiverInfo);
            }
        }
        //?????????????????????????????????ID ?????????ID
        UserDataInfo userDataInfo = userDataInfoService.getPhone(orderScheduler.getCollectionUserPhone());
        if (userDataInfo == null) {
            throw new BusinessException("???????????????????????????");
        }
        //??????????????????
        orderScheduler.setCollectionUserName(orderScheduler.getCollectionName());
        orderScheduler.setCollectionUserId(userDataInfo.getId());
        if (orderScheduler.getCarDriverId().equals(orderScheduler.getCollectionUserId())) {
            throw new BusinessException("?????????????????????????????????");
        }
    }

    private void setAppointCarInfo(OrderScheduler orderScheduler, List<OrderOilDepotScheme> orderOilDepotSchemeList, OrderDto orderDto) {
        String tmpStr = orderDto.getDispatchInfoVo().getPlateNumber();
        orderScheduler.setPlateNumber(tmpStr);
        tmpStr = orderDto.getDispatchInfoVo().getVehicleCode().toString();
        orderScheduler.setVehicleCode(objToLong0(tmpStr));
        if (orderDto.getDispatchInfoVo().getVehicleLength() != null) {
            tmpStr = orderDto.getDispatchInfoVo().getVehicleLength().toString();
            orderScheduler.setCarLengh(tmpStr);
        } else {
            orderScheduler.setCarLengh(null);
        }
        if (orderDto.getDispatchInfoVo().getVehicleStatus() != null) {
            tmpStr = orderDto.getDispatchInfoVo().getVehicleStatus().toString();
            orderScheduler.setCarStatus(DataFormat.getIntKey(tmpStr));
        } else {
            orderScheduler.setCarStatus(null);
        }

        tmpStr = orderDto.getDispatchInfoVo().getCarDriverMan();
        orderScheduler.setCarDriverMan(tmpStr);
        tmpStr = orderDto.getDispatchInfoVo().getCarDriverId().toString();
        orderScheduler.setCarDriverId(objToLong0(tmpStr));
        tmpStr = orderDto.getDispatchInfoVo().getCarDriverPhone();
        orderScheduler.setCarDriverPhone(tmpStr);
        if (null != orderDto.getDispatchInfoVo().getLicenceType()) {
            tmpStr = orderDto.getDispatchInfoVo().getLicenceType().toString();
            orderScheduler.setLicenceType(DataFormat.getIntKey(tmpStr));
        } else {
            orderScheduler.setLicenceType(null);
        }


        //?????????
        if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            if (orderScheduler.getCarDriverId() == null || orderScheduler.getCarDriverId() <= 0) {
                throw new BusinessException("?????????????????????");
            }
            if (orderDto.getDispatchInfoVo().getTrailerId() != null) {
                tmpStr = orderDto.getDispatchInfoVo().getTrailerId().toString();
            } else {
                tmpStr = null;
            }

            if (StringUtils.isNotBlank(tmpStr) && !"-1".equals(tmpStr)) {
                orderScheduler.setTrailerId(objToLong0(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getTrailerPlate();
                orderScheduler.setTrailerPlate(tmpStr);
            } else {
                orderScheduler.setTrailerId(null);
                orderScheduler.setTrailerPlate(null);
            }
            if (orderDto.getDispatchInfoVo().getCopilotUserId() != null) {
                tmpStr = orderDto.getDispatchInfoVo().getCopilotUserId().toString();
            } else {
                tmpStr = null;
            }
            if (StringUtils.isNotBlank(tmpStr) && !"-1".equals(tmpStr)) {
                orderScheduler.setCopilotUserId(objToLong0(tmpStr));
                tmpStr = orderDto.getDispatchInfoVo().getCopilotMan();
                orderScheduler.setCopilotMan(tmpStr);
                tmpStr = orderDto.getDispatchInfoVo().getCopilotPhone();
                orderScheduler.setCopilotPhone(tmpStr);
            } else {
                orderScheduler.setCopilotUserId(null);
            }

            Integer paymentWay = orderDto.getDispatchInfoVo().getPaymentWay() != null ? orderDto.getDispatchInfoVo().getPaymentWay() : null;
            if (paymentWay != null && OrderConsts.PAYMENT_WAY.COST == paymentWay) {
                OrderOilDepotScheme oilDepotSchemeOne = new OrderOilDepotScheme();
                if (null != orderDto.getDispatchInfoVo().getOilDepotIdOne()) {
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotIdOne().toString();
                } else {
                    tmpStr = null;
                }

                if (StringUtils.isNotBlank(tmpStr) && Integer.parseInt(tmpStr) > 0) {
                    oilDepotSchemeOne.setOilDepotId(objToLong0(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotNameOne();
                    oilDepotSchemeOne.setOilDepotName(tmpStr);
                    tmpStr = orderDto.getDispatchInfoVo().getDependDistanceOne().toString();
                    oilDepotSchemeOne.setDependDistance(mul(tmpStr, "1000"));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotPriceOne().toString();
                    oilDepotSchemeOne.setOilDepotPrice(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotLitreOne().toString();
                    oilDepotSchemeOne.setOilDepotLitre(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotFeeOne().toString();
                    oilDepotSchemeOne.setOilDepotFee(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotEandOne();
                    oilDepotSchemeOne.setOilDepotEand(tmpStr);
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotNandOne();
                    oilDepotSchemeOne.setOilDepotNand(tmpStr);
                }

                OrderOilDepotScheme oilDepotSchemeTwo = new OrderOilDepotScheme();
                if (null != orderDto.getDispatchInfoVo().getOilDepotIdTwo()) {
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotIdTwo().toString();
                } else {
                    tmpStr = null;
                }
                if (StringUtils.isNotBlank(tmpStr) && Integer.parseInt(tmpStr) > 0) {
                    oilDepotSchemeTwo.setOilDepotId(objToLong0(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotNameTwo();
                    oilDepotSchemeTwo.setOilDepotName(tmpStr);
                    tmpStr = orderDto.getDispatchInfoVo().getDependDistanceTwo().toString();
                    oilDepotSchemeTwo.setDependDistance(mul(tmpStr, "1000"));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotPriceTwo().toString();
                    oilDepotSchemeTwo.setOilDepotPrice(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotLitreTwo();
                    oilDepotSchemeTwo.setOilDepotLitre(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotFeeTwo().toString();
                    oilDepotSchemeTwo.setOilDepotFee(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotEandTwo();
                    oilDepotSchemeOne.setOilDepotEand(tmpStr);
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotNandTwo();
                    oilDepotSchemeOne.setOilDepotNand(tmpStr);
                }

                OrderOilDepotScheme oilDepotSchemeThree = new OrderOilDepotScheme();
                if (null != orderDto.getDispatchInfoVo().getOilDepotIdThree()) {
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotIdThree().toString();
                } else {
                    tmpStr = null;
                }

                if (StringUtils.isNotBlank(tmpStr) && Integer.parseInt(tmpStr) > 0) {
                    oilDepotSchemeThree.setOilDepotId(objToLong0(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotNameThree();
                    oilDepotSchemeThree.setOilDepotName(tmpStr);
                    tmpStr = orderDto.getDispatchInfoVo().getDependDistanceThree();
                    oilDepotSchemeThree.setDependDistance(mul(tmpStr, "1000"));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotPriceThree();
                    oilDepotSchemeThree.setOilDepotPrice(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotLitreThree();
                    oilDepotSchemeThree.setOilDepotLitre(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotFeeThree();
                    oilDepotSchemeThree.setOilDepotFee(objToLongMul100(tmpStr));
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotEandThree();
                    oilDepotSchemeOne.setOilDepotEand(tmpStr);
                    tmpStr = orderDto.getDispatchInfoVo().getOilDepotNandThree();
                    oilDepotSchemeOne.setOilDepotNand(tmpStr);
                }

                orderOilDepotSchemeList.add(oilDepotSchemeOne);
                orderOilDepotSchemeList.add(oilDepotSchemeTwo);
                orderOilDepotSchemeList.add(oilDepotSchemeThree);
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param orderFee
     * @throws Exception
     */
    public void checkOilCarType(OrderFee orderFee, Long tenantId) {
        //???????????????????????????
        if (orderFee.getPrePayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getPrePayEquivalenceCardType()) {

            if (StringUtils.isBlank(orderFee.getPrePayEquivalenceCardNumber())) {
                throw new BusinessException("?????????????????????????????????????????????");
            }

            if (!oilCardManagementService.checkCardNum(orderFee.getPrePayEquivalenceCardNumber(), tenantId)) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }

        }

        //???????????????????????????
        if (orderFee.getAfterPayEquivalenceCardType() != null &&
                OrderConsts.EQUIVALENCE_CARD_TYPE.OIL_TYPE == orderFee.getAfterPayEquivalenceCardType()) {

            if (StringUtils.isBlank(orderFee.getAfterPayEquivalenceCardNumber())) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (!oilCardManagementService.checkCardNum(orderFee.getAfterPayEquivalenceCardNumber(), tenantId)) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
        }
    }

    /**
     * ????????????
     *
     * @param orderId
     * @param costPrice
     * @param tenantId
     * @throws Exception
     */
    private void saveOrderFeeStatement(Long orderId, Long costPrice, Long tenantId, LoginInfo user) {
        OrderFeeStatement orderFeeStatement = orderFeeStatementService.getOrderFeeStatementById(orderId);
        if (orderFeeStatement == null) {
            orderFeeStatement = new OrderFeeStatement();
        }
        orderFeeStatement.setConfirmAmount(costPrice == null ? 0 : costPrice);
        orderFeeStatement.setReceivableAmount(0L);
        orderFeeStatement.setGetAmount(0L);
        orderFeeStatement.setOrderId(orderId);
        orderFeeStatement.setTenantId(tenantId);
        orderFeeStatementService.saveOrderFeeStatement(orderFeeStatement, user);
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param
     * @param orderInfo
     * @param orderFee
     * @param orderGoods
     * @param orderInfoExt
     * @param orderFeeExt
     * @param orderScheduler
     */
    private void setOtherData(OrderDto orderDto, OrderInfo orderInfo, OrderFee orderFee, OrderGoods orderGoods, OrderInfoExt orderInfoExt
            , OrderFeeExt orderFeeExt, OrderScheduler orderScheduler) {
        orderInfo.setOpOrgId(orderDto.getOtherDataDto().getOpOrgId());
        orderGoods.setReciveState(orderDto.getOtherDataDto().getReciveState());
        orderGoods.setLoadState(orderDto.getOtherDataDto().getLoadState());
        orderFee.setVehicleAffiliation(orderDto.getOtherDataDto().getVehicleAffiliation());
        orderFee.setFinalFeeFlag(orderDto.getOtherDataDto().getFinalFeeFlag());
        orderFee.setPayoutStatus(orderDto.getOtherDataDto().getPayoutStatus());
        orderFee.setVerificationState(orderDto.getOtherDataDto().getVerificationState());
        orderInfoExt.setPreAmountFlag(orderDto.getOtherDataDto().getPreAmountFlag());
        orderFeeExt.setMonthSalary(objToLongMul100(orderDto.getOtherDataDto().getMonthSalary()));
        orderFeeExt.setSalaryPattern(orderDto.getOtherDataDto().getSalaryPattern());
        orderFeeExt.setDriverDaySalary(objToLongMul100(orderDto.getOtherDataDto().getDriverDaySalary()));
    }

    public static float objToFloatMul100(String str) {
        if (StringUtils.isNotBlank(str)) {
            BigDecimal bigA = new BigDecimal(str);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.multiply(bigB).floatValue();
        } else {
            return 0f;
        }
    }

    /**
     * ?????? * 100
     *
     * @param str
     * @return
     */
    public static long objToLongMul100(String str) {
        if (objToFloat(str) != null) {
            BigDecimal bigA = new BigDecimal(str);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.multiply(bigB).longValue();
        } else {
            return 0;
        }
    }

    public static Float objToFloat(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Float.parseFloat(str);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public int refuseOrder(Long orderId, String remark, String accessToken, Integer transferOrderState) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        Long tenantId = loginInfo.getTenantId();
        // ??????????????????????????????????????????
        String logRemark = "??????????????????" + remark;
        // ????????????????????????????????????????????????
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("order_id", orderId);
        List<OrderInfo> orderInfos = orderInfoMapper.selectList(orderInfoQueryWrapper);
        boolean isHis = false;
        OrderInfo orderInfo = new OrderInfo();
        if (orderInfos != null && orderInfos.size() > 0) {
            orderInfo = orderInfos.get(0);
        } else {
            orderInfo = new OrderInfo();

            QueryWrapper<OrderInfoH> orderInfoHQueryWrapper = new QueryWrapper<>();
            orderInfoHQueryWrapper.eq("order_id", orderId);
            List<OrderInfoH> orderInfoHs = orderInfoHMapper.selectList(orderInfoHQueryWrapper);
            if (orderInfoHs != null && orderInfoHs.size() > 0) {
                OrderInfoH infoH = orderInfoHs.get(0);
                BeanUtils.copyProperties(orderInfo, infoH);
                isHis = true;
            } else {
                throw new BusinessException("???????????????[" + orderId + "]??????");
            }
        }
        if (orderInfo.getToTenantId() == null
                || (orderInfo.getToTenantId() != null && orderInfo.getToTenantId().longValue() != tenantId)) {
            throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????");
        }

        if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_RECIVE) {//???????????????????????????
            if (isHis) {
                updateOrderStateH(orderId, tenantId, OrderConsts.ORDER_STATE.BILL_NOT, logRemark);
            } else {
                updateOrderState(orderId, tenantId, OrderConsts.ORDER_STATE.BILL_NOT, logRemark);
            }
        }

        // ????????????????????????????????????????????????
        List<TransferInfo> orderTransferInfoList = transferInfoService.getOrderTransferInfoList(orderId, tenantId, com.youming.youche.order.commons.OrderConsts.TransferOrderState.TO_BE_RECIVE);
        if (orderTransferInfoList != null && orderTransferInfoList.size() > 0) {
            TransferInfo transferInfo = orderTransferInfoList.get(0);

            // ??????????????????
            sysOperLogService.save(SysOperLogConst.BusiCode.OrderInfo, orderId, SysOperLogConst.OperType.Update, logRemark, orderInfo.getOpName(), orderInfo.getOpId(), orderInfo.getTenantId());

            int i = transferInfoService.updateOrderTransferState(orderId, tenantId, 2, new Date(), logRemark);
            return i;
        } else {
            return 0;
        }

    }

    @Override
    public void updateOrderStateH(Long orderId, Long toTenantId, Integer orderState, String refuseOrderReason) {

        QueryWrapper<OrderInfoH> infoHQueryWrapper = new QueryWrapper<>();
        infoHQueryWrapper.eq("order_id", orderId)
                .eq("to_tenant_id", toTenantId);
        OrderInfoH infoH = new OrderInfoH();
        infoH.setRefuseOrderReason(refuseOrderReason)
                .setOrderState(orderState);
        orderInfoHMapper.update(infoH, infoHQueryWrapper);

    }

    @Override
    public void updateOrderState(Long orderId, Long toTenantId, Integer orderState, String refuseOrderReason) {

        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("order_id", orderId)
                .eq("to_tenant_id", toTenantId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setRefuseOrderReason(refuseOrderReason)
                .setOrderState(orderState);
        orderInfoMapper.update(orderInfo, orderInfoQueryWrapper);

    }


    /**
     * ???????????????
     *
     * @param phone
     * @param isCarDriver
     * @param orderScheduler
     * @throws Exception
     */
    public void getDriverbyPhone(String phone, boolean isCarDriver, OrderScheduler orderScheduler, LoginInfo user) {
        Map inParam = new HashMap(1);
        inParam.put("loginAcct", phone);
        //inParam.put("carUserType", 1);
        List<DriverDataInfoDto> lists = vehicleDataInfoService.doQueryCarDriver(phone, user.getTenantId());
        ;
        if (lists.size() > 0) {
            DriverDataInfoDto obj = lists.get(0);
            if (isCarDriver) {
                orderScheduler.setCarDriverId(obj.getId() != null ? obj.getId() : null);
                orderScheduler.setCarDriverMan(obj.getLinkman() != null ? obj.getLinkman() : null);
                orderScheduler.setCarDriverPhone(phone);
            } else {
                orderScheduler.setCopilotUserId(obj.getId() != null ? obj.getId() : null);
                orderScheduler.setCopilotMan(obj.getLinkman() != null ? obj.getLinkman() : null);
                orderScheduler.setCopilotPhone(phone);
            }

        } else {
            throw new BusinessException((isCarDriver ? "??????" : "??????") + "?????????[" + phone + "]??????????????????????????????");
        }
    }


    /**
     * ????????????????????????
     *
     * @return
     * @throws Exception
     */
    private List<Long> getOrgIds(LoginInfo baseUser, String token) {
        List<Long> orgIds = sysUserOrgRelService.selectIdByUserInfoIdAndTenantId(baseUser.getUserInfoId(), baseUser.getTenantId());
        List<Long> orgIdSet = new ArrayList<Long>();
        if (orgIds != null && orgIds.size() > 0) {
            try {
                for (Long orgId : orgIds) {
                    List<Long> orgList = sysOrganizeService.getSubOrgList(token);
                    if (orgList != null && orgList.size() > 0) {
                        orgIdSet.addAll(orgList);
                    }
                    if (!orgIdSet.contains(orgId)) {
                        orgIdSet.add(orgId);
                    }
                }
            } catch (Exception e) {
                log.error("????????????????????????????????????????????????", e);
            }
        }

        return orgIdSet;
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @throws Exception
     */
    private boolean getLineInfoByLineCode(String lineCode, OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderGoods orderGoods
            , OrderFee orderFee, OrderFeeExt orderFeeExt, OrderScheduler orderSchedule,
                                          OrderPaymentDaysInfo incomePaymentDaysInfo
            , List<OrderTransitLineInfo> orderTransitLineInfos, List<Long> orgIds, LoginInfo user) {

        CmCustomerLine line = cmCustomerLineService.queryLineForOrderTask(lineCode, orgIds, user);
        if (line == null || line.getCustomerId() == null || line.getCustomerId() <= 0) {
            throw new BusinessException("???????????????[" + lineCode + "]?????????????????????????????????;");
        }

        CmCustomerInfo c = cmCustomerInfoService.queryCustomerForOrderTask(line.getCustomerId(), orgIds, user);
        if (c == null) {
            throw new BusinessException("???????????????[" + lineCode + "]???????????????????????????????????????;");
        }

        orderInfo.setSourceProvince(line.getSourceProvince());
        orderInfo.setSourceRegion(line.getSourceCity());
        orderInfo.setSourceCounty(line.getSourceCounty());
        orderInfo.setDesProvince(line.getDesProvince());
        orderInfo.setDesRegion(line.getDesCity());
        orderInfo.setDesCounty(line.getDesCounty());
        orderInfo.setOrderType(OrderConsts.OrderType.FIXED_LINE);
        orderInfo.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.IMPORT);

        orderInfoExt.setRunOil((line.getOilCostPer() == null ? 0 : line.getOilCostPer().floatValue()));
        orderInfoExt.setCapacityOil((line.getEmptyOilCostPer() == null ? 0 : line.getEmptyOilCostPer().floatValue()));

        orderGoods.setAddress(c.getAddress());
        orderGoods.setLinkName(c.getLineName());
        orderGoods.setLinkPhone(c.getLinePhone());
        orderGoods.setCustomUserId(c.getId());
        orderGoods.setCompanyName(c.getCompanyName());
        orderGoods.setCustomName(c.getCustomerName());

        orderGoods.setGoodsType(line.getGoodsType());
        orderGoods.setGoodsName(line.getGoodsName());
        orderGoods.setWeight(line.getGoodsWeight());
        orderGoods.setSquare(line.getGoodsVolume());
        orderGoods.setVehicleLengh(line.getVehicleLength());
        orderGoods.setVehicleStatus(line.getVehicleStatus());

        orderGoods.setReciveType(c.getOddWay());
        orderGoods.setReciveName(line.getReceiveName());
        orderGoods.setRecivePhone(line.getReceivePhone());
        orderGoods.setReciveProvinceId(line.getReciveProvinceId());
        orderGoods.setReciveCityId(line.getReciveCityId());
        orderGoods.setReciveAddr(line.getReciveAddress());

        orderGoods.setYongyouCode(c.getYongyouCode());
        orderGoods.setCustType(1);
        String ProvinceName = null;
        if (orderInfo.getSourceProvince() != null) {
            ProvinceName = readisUtil.getSysStaticData("SYS_PROVINCE", orderInfo.getSourceProvince().toString()).getCodeName();
        }
        String RegionName = null;
        if (orderInfo.getSourceRegion() != null) {
            RegionName = readisUtil.getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
        }

        String CountyName = null;
        if (orderInfo.getSourceCounty() != null) {
            CountyName = readisUtil.getSysStaticData("SYS_DISTRICT", orderInfo.getSourceCounty().toString()).getCodeName();
        }

        orderGoods.setSource(ProvinceName + RegionName
                + CountyName);
        String DesProvinceName = null;
        if (orderInfo.getDesProvince() != null) {
            DesProvinceName = readisUtil.getSysStaticData("SYS_PROVINCE", orderInfo.getDesProvince().toString()).getCodeName();
        }
        String DesRegionName = null;
        if (orderInfo.getDesRegion() != null) {
            DesRegionName = readisUtil.getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
        }

        String DesCountyName = null;
        if (orderInfo.getDesCounty() != null) {
            readisUtil.getSysStaticData("SYS_DISTRICT", orderInfo.getDesCounty().toString()).getCodeName();
        }
        orderGoods.setDes(DesProvinceName + DesRegionName + DesCountyName);
        orderGoods.setAddrDtl(line.getSourceAddress());
        orderGoods.setDesDtl(line.getDesAddress());
        orderGoods.setNand(line.getSourceNand());
        orderGoods.setEand(line.getSourceEand());
        orderGoods.setNavigatSourceLocation(line.getNavigatSourceLocation());
        orderGoods.setNandDes(line.getDesNand());
        orderGoods.setEandDes(line.getDesEand());
        if (line.getNavigatDesLocation() != null) {
            orderGoods.setNavigatDesLocation(line.getNavigatDesLocation());
        }
        orderGoods.setContactName(line.getLineName());
        orderGoods.setContactPhone(line.getLineTel());

        orderFee.setPriceEnum(line.getPriceEnum());
        BigDecimal value = line.getPriceUnit();
        if (value != null) {
            orderFee.setPriceUnit(line.getPriceUnit().doubleValue());
        }

        orderFee.setGuidePrice(line.getGuidePrice());

        orderFeeExt.setPontage(line.getPontagePer());

        orderSchedule.setDistance((long) (line.getCmMileageNumber() * 1000));
        orderSchedule.setMileageNumber((int) (line.getMileageNumber() * 1000));
        orderSchedule.setArriveTime(line.getArriveTime());
        orderSchedule.setAppointWay(com.youming.youche.market.commons.OrderConsts.AppointWay.APPOINT_CAR);
        orderSchedule.setSourceCode(line.getLineCodeRule());
        orderSchedule.setSourceId(line.getId());
        orderSchedule.setSourceName(line.getLineCodeName());
        orderSchedule.setClientContractUrl(line.getContractUrl());
        orderSchedule.setClientContractId(line.getContractId());

        incomePaymentDaysInfo.setBalanceType(line.getPayWay());
        if (line.getPayWay() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
            incomePaymentDaysInfo.setReciveMonth(line.getReciveTime());
            incomePaymentDaysInfo.setReciveDay(line.getReciveTimeDay());
            incomePaymentDaysInfo.setInvoiceMonth(line.getInvoiceTime());
            incomePaymentDaysInfo.setInvoiceDay(line.getInvoiceTimeDay());
            incomePaymentDaysInfo.setCollectionMonth(line.getCollectionTime());
            incomePaymentDaysInfo.setCollectionDay(line.getCollectionTimeDay());
            incomePaymentDaysInfo.setReconciliationMonth(line.getRecondTime());
            incomePaymentDaysInfo.setReconciliationDay(line.getRecondTimeDay());
        } else {
            incomePaymentDaysInfo.setReciveTime(line.getReciveTime());
            incomePaymentDaysInfo.setInvoiceTime(line.getInvoiceTime());
            incomePaymentDaysInfo.setCollectionTime(line.getCollectionTime());
            incomePaymentDaysInfo.setReconciliationTime(line.getRecondTime());
        }


        List<com.youming.youche.record.domain.cm.CmCustomerLineSubway> subWayList = cmCustomerLineSubwayService.getCustomerLineSubwayList(line.getId());
        if (subWayList != null) {
            for (com.youming.youche.record.domain.cm.CmCustomerLineSubway cmCustomerLineSubway : subWayList) {
                OrderTransitLineInfo orderTransitLineInfo = new OrderTransitLineInfo();
                orderTransitLineInfo.setAddress(cmCustomerLineSubway.getDesAddress());
                orderTransitLineInfo.setArriveTime(cmCustomerLineSubway.getArriveTime());
                orderTransitLineInfo.setProvince(cmCustomerLineSubway.getDesProvince());
                orderTransitLineInfo.setRegion(cmCustomerLineSubway.getDesCity());
                orderTransitLineInfo.setCounty(cmCustomerLineSubway.getDesCounty());
//                orderTransitLineInfo.setProvinceName(cmCustomerLineSubway.getDesProvinceName());
//                orderTransitLineInfo.setRegionName(cmCustomerLineSubway.getDesCityName());
//                orderTransitLineInfo.setCountyName(cmCustomerLineSubway.getDesCountyName());
                orderTransitLineInfo.setNavigatLocation(cmCustomerLineSubway.getNavigatDesLocation());
                orderTransitLineInfo.setEand(cmCustomerLineSubway.getDesEnd());
                orderTransitLineInfo.setNand(cmCustomerLineSubway.getDesNand());
                orderTransitLineInfos.add(orderTransitLineInfo);
            }
        }

        return true;
    }

    @Override
    public OrderListWxDto queryOrderUpdateDetail(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        OrderListWxDto dto = new OrderListWxDto();
        dto.setOrderId(orderId);

        // ??????????????????
        this.setOrderListWxOut(dto, loginInfo);
        return dto;
    }

    private void setOrderListWxOut(OrderListWxDto out, LoginInfo loginInfo) {
        OrderInfo orderInfo = this.getOrder(out.getOrderId());

        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        // ?????????????????????  ?????????????????????????????????????????????????????????
        if (orderInfo == null) {
            OrderInfoH orderInfoH = this.getOrderH(out.getOrderId());
            if (orderInfoH == null) {
                throw new BusinessException("?????????[" + out.getOrderId() + "]???????????????!");
            }
            OrderFeeH orderFeeH = orderFeehService.getOrderFeeH(out.getOrderId());
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(out.getOrderId());
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(out.getOrderId());
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(out.getOrderId());
            OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH(out.getOrderId());
            orderInfo = new OrderInfo();
            BeanUtil.copyProperties(orderFeeH, orderFee);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderGoodsH, orderGoods);
        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(out.getOrderId());
            orderGoods = orderGoodsService.getOrderGoods(out.getOrderId());
            orderInfoExt = orderInfoExtService.getOrderInfoExt(out.getOrderId());
            orderFee = orderFeeService.getOrderFee(out.getOrderId());
            orderFeeExt = orderFeeExtService.getOrderFeeExt(out.getOrderId());
        }
        // ??????????????????????????????
        OrderInfoVer orderInfoVer = orderInfoVerService.getOrderInfoVer(out.getOrderId());
        OrderGoodsVer orderGoodsVer = orderGoodsVerService.getOrderGoodsVer(out.getOrderId());
        OrderInfoExtVer orderInfoExtVer = orderInfoExtVerService.getOrderInfoExtVer(out.getOrderId());
        OrderFeeVer orderFeeVer = orderFeeVerService.getOrderFeeVer(out.getOrderId());
        OrderFeeExtVer orderFeeExtVer = orderFeeExtVerService.getOrderFeeExtVer(out.getOrderId());
        OrderSchedulerVer schedulerVer = orderSchedulerVerService.getOrderSchedulerVer(out.getOrderId());

        List<Map<String, Object>> compareOut = new ArrayList<Map<String, Object>>();
        // ???????????????????????????????????????
        List<Map<String, Object>> orderInfoList = ObjectCompareUtils.compareFieldsNotIn(orderInfo, orderInfoVer, null);
        List<Map<String, Object>> orderGoodsList = ObjectCompareUtils.compareFieldsNotIn(orderGoods, orderGoodsVer, null);
        List<Map<String, Object>> orderInfoExtList = ObjectCompareUtils.compareFieldsNotIn(orderInfoExt, orderInfoExtVer, null);
        List<Map<String, Object>> orderFeeList = ObjectCompareUtils.compareFieldsNotIn(orderFee, orderFeeVer, null);
        List<Map<String, Object>> orderSchedulerList = ObjectCompareUtils.compareFieldsNotIn(orderScheduler, schedulerVer, null);
        List<Map<String, Object>> orderFeeExtList = ObjectCompareUtils.compareFieldsNotIn(orderFeeExt, orderFeeExtVer, null);

        if (orderInfoList != null && orderInfoList.size() > 0) {
            compareOut.addAll(orderInfoList);
        }
        if (orderGoodsList != null && orderGoodsList.size() > 0) {
            compareOut.addAll(orderGoodsList);
        }
        if (orderInfoExtList != null && orderInfoExtList.size() > 0) {
            compareOut.addAll(orderInfoExtList);
        }
        if (orderFeeList != null && orderFeeList.size() > 0) {
            if (orderFeeList != null) {
                for (int i = 0; i < orderFeeList.size(); i++) {
                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 3) {
                        if (orderFeeList.get(i).getOrDefault("fieldName", "").equals("guidePrice")) {
                            orderFeeList.get(i).put("newValue", orderFeeVer.getTotalFee());
                        }
                    }

                    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 1) {
                        if (orderFeeList.get(i).getOrDefault("fieldName", "").equals("guidePrice")) {
                            orderFeeList.remove(i);
                        }
                    }
                }
                //for (Map<String, Object> stringObjectMap : orderFeeList) {
                //    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 3) {
                //        if (stringObjectMap.getOrDefault("fieldName", "").equals("guidePrice")) {
                //            stringObjectMap.put("newValue", orderFeeVer.getTotalFee());
                //        }
                //    }
                //
                //    if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 1) {
                //        if (stringObjectMap.getOrDefault("fieldName", "").equals("guidePrice")) {
                //            orderFeeList.remove(stringObjectMap);
                //        }
                //    }
                //}
            }
            compareOut.addAll(orderFeeList);
        }
        if (orderSchedulerList != null && orderSchedulerList.size() > 0) {
            compareOut.addAll(orderSchedulerList);
        }
        if (orderFeeExtList != null && orderFeeExtList.size() > 0) {
            compareOut.addAll(orderFeeExtList);
        }

        out.setCompareOut(compareOut);//??????????????????
        List<OrderOilDepotScheme> schemes = orderOilDepotSchemeService.getOrderOilDepotSchemeByOrderId(out.getOrderId(), false, loginInfo);
        List<OrderOilDepotSchemeVer> schemeVers = orderOilDepotSchemeVerService.getOrderOilDepotSchemeVerByOrderId(out.getOrderId(), true, loginInfo);
        out.setSchemes(schemes);//?????????
        out.setSchemeVers(schemeVers);
    }

    @Override
    public Long copyOrderInfo(Long orderId, String dependTime, String accessToken) {
        // ???????????????
        Long newOrderId = this.saveOrderInfoNew(orderId, dependTime, accessToken);
        return newOrderId;
    }

    // ?????????????????????  ??????????????????????????????
    private Long saveOrderInfoNew(Long orderId, String dependTimeStr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        if (StringUtils.isBlank(dependTimeStr)) {
            throw new BusinessException("????????????????????????");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????????????????");
        }
        DateTimeFormatter sf = DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT1);
        LocalDateTime dependTime = LocalDateTime.parse(dependTimeStr, sf);

        OrderInfo orderInfoNew = new OrderInfo();
        OrderFee orderFeeNew = new OrderFee();
        OrderFeeExt orderFeeExtNew = new OrderFeeExt();
        OrderInfoExt orderInfoExtNew = new OrderInfoExt();
        OrderScheduler orderSchedulerNew = new OrderScheduler();
        OrderGoods goodsNew = new OrderGoods();
        OrderPaymentDaysInfo incomePaymentDaysInfoNew = new OrderPaymentDaysInfo();
        OrderPaymentDaysInfo costPaymentDaysInfoNew = new OrderPaymentDaysInfo();

        OrderInfo orderInfo = this.getOrder(orderId);
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods goods = new OrderGoods();
        OrderPaymentDaysInfo incomePaymentDaysInfo = new OrderPaymentDaysInfo();

        // ???????????????????????????????????????
        if (orderInfo != null) {
            orderFee = orderFeeService.getOrderFee(orderId);
            orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            goods = orderGoodsService.getOrderGoods(orderId);
            incomePaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderId, OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        } else {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH != null) {
                OrderFeeH orderFeeH = orderFeehService.getOrderFeeH(orderId);
                OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderId);
                OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH(orderId);
                orderInfo = new OrderInfo();
                BeanUtil.copyProperties(orderFeeH, orderFee);
                BeanUtil.copyProperties(orderInfoH, orderInfo);
                BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
                BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
                BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
                BeanUtil.copyProperties(orderGoodsH, goods);
            } else {
                throw new BusinessException("???????????????[" + orderId + "]??????!");
            }
        }

        BeanUtil.copyProperties(incomePaymentDaysInfo, incomePaymentDaysInfoNew);
        incomePaymentDaysInfoNew.setId(null);
        incomePaymentDaysInfoNew.setOrderId(orderId);
        incomePaymentDaysInfoNew.setTenantId(null);
        incomePaymentDaysInfoNew.setCreateTime(LocalDateTime.now());
        costPaymentDaysInfoNew.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
        costPaymentDaysInfoNew.setCreateTime(dependTime);
        //???????????????
        orderInfoNew.setTenantId(tenantId);
        orderInfoNew.setSourceCounty(orderInfo.getSourceCounty());
        orderInfoNew.setSourceProvince(orderInfo.getSourceProvince());
        orderInfoNew.setSourceRegion(orderInfo.getSourceRegion());
        orderInfoNew.setDesCounty(orderInfo.getDesCounty());
        orderInfoNew.setDesProvince(orderInfo.getDesProvince());
        orderInfoNew.setDesRegion(orderInfo.getDesRegion());
        orderInfoNew.setOrgId(orderInfo.getOrgId());
        orderInfoNew.setOrderType(orderInfo.getOrderType());
        orderInfoNew.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.WEIXIN);
        orderInfoNew.setIsNeedBill(orderInfo.getIsNeedBill());
        //???????????????
        orderFeeNew.setTenantId(tenantId);
        orderFeeNew.setPriceUnit(orderFee.getPriceUnit());
        orderFeeNew.setPriceEnum(orderFee.getPriceEnum());
        orderFeeNew.setCostPrice(orderFee.getCostPrice());
        orderFeeNew.setPrePayCash(orderFee.getPrePayCash());
        orderFeeNew.setAfterPayCash(orderFee.getAfterPayCash());
        orderFeeNew.setPrePayEquivalenceCardAmount(orderFee.getPrePayEquivalenceCardAmount());
        orderFeeNew.setPrePayEquivalenceCardNumber(orderFee.getPrePayEquivalenceCardNumber());
        orderFeeNew.setPrePayEquivalenceCardType(orderFee.getPrePayEquivalenceCardType());
        orderFeeNew.setAfterPayEquivalenceCardAmount(orderFee.getAfterPayEquivalenceCardAmount());
        orderFeeNew.setAfterPayEquivalenceCardNumber(orderFee.getAfterPayEquivalenceCardNumber());
        orderFeeNew.setAfterPayEquivalenceCardType(orderFee.getAfterPayEquivalenceCardType());
        orderFeeNew.setAfterPayAcctType(orderFee.getAfterPayAcctType());
        //?????????????????????
        orderFeeExtNew.setTenantId(tenantId);
        //?????????????????????
        orderInfoExtNew.setTenantId(tenantId);
        //?????????????????????
        orderSchedulerNew.setAppointWay(OrderConsts.AppointWay.APPOINT_LOCAL);
        orderSchedulerNew.setDependTime(dependTime);
        orderSchedulerNew.setArriveTime(orderScheduler.getArriveTime());
        orderSchedulerNew.setSourceId(orderScheduler.getSourceId());
        orderSchedulerNew.setSourceCode(orderScheduler.getSourceCode());
        orderSchedulerNew.setSourceName(orderScheduler.getSourceName());
        orderSchedulerNew.setTenantId(tenantId);
        orderSchedulerNew.setDispatcherBill(loginInfo.getTelPhone());
        orderSchedulerNew.setDispatcherId(loginInfo.getUserInfoId());
        orderSchedulerNew.setDispatcherName(loginInfo.getName());
        orderSchedulerNew.setDistance(orderScheduler.getDistance());
        orderSchedulerNew.setMileageNumber(orderScheduler.getMileageNumber());

        //???????????????
        BeanUtils.copyProperties(goods, goodsNew);
        goodsNew.setOrderId(null);
        goodsNew.setId(null);
        goodsNew.setContractId(null);
        goodsNew.setContractUrl(null);
        goodsNew.setTenantId(tenantId);

        //??????????????????????????????
        List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());

        // ??????????????????
        Long newOrderId = this.saveOrUpdateOrderInfo(orderInfoNew, orderFeeNew, goodsNew, orderInfoExtNew, orderFeeExtNew, orderSchedulerNew
                , null, costPaymentDaysInfoNew, incomePaymentDaysInfoNew, null, transitLineInfos, true, accessToken, loginInfo);
        return newOrderId;
    }


    @Override
    @SuppressWarnings({"rawtypes"})
    public Page<OrderListWxDto> queryOrderListWxOut(OrderListWxVo wxIn, Integer pageNum, Integer pageSize, String accessToken) {
        long startTime = System.currentTimeMillis();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (loginInfo == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        if (wxIn == null) {
            throw new BusinessException("??????????????????!");
        }
        if (wxIn.getTodo() != null && wxIn.getTodo()) {
            List<Long> busiIds = new ArrayList<>();
            if (wxIn.getSelectOrderType() != null) {
                if (wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) {
                    busiIds = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.PROBLEM_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
                } else if (wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
                    List<Long> list = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
                    if (list != null && list.size() > 0) {
                        busiIds.addAll(list);
                    }
                    List<Long> list1 = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
                    if (list1 != null && list1.size() > 0) {
                        busiIds.addAll(list1);
                    }
                } else if (wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.AGING_AUDIT) {
                    busiIds = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
                }
//                if (wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.RECEIPT_AUDIT) {
//                    wxIn.setOrderState(String.valueOf(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT));
//                    wxIn.setOrderSatetTwo(String.valueOf(OrderConsts.ORDER_STATE.RECIVE_AUDIT_NOT));
//                }else if (wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT) {
//                    wxIn.setOrderState(String.valueOf(OrderConsts.ORDER_STATE.TO_BE_AUDIT));
//                    wxIn.setOrderUpdateState(String.valueOf(OrderConsts.UPDATE_STATE.UPDATE_PORTION));
//                    if (wxIn.getTodo() != null && wxIn.getTodo()) {
//                        if (wxIn.getBusiId() == null) {
//                            busiIds.add(Long.valueOf(-1));
//                        }
//                    }
//                } else if (wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) {
//                    wxIn.setOrderStateOne(String.valueOf(OrderConsts.ORDER_STATE.TO_BE_LOAD));
//                    wxIn.setOrderSatetTwo(String.valueOf(OrderConsts.ORDER_STATE.CANCELLED));
//                    if (wxIn.getTodo() != null && wxIn.getTodo()) {
//                        if (wxIn.getBusiId() == null){
//                            busiIds.add(Long.valueOf(-1));
//                        }
//                    }
//                }
            }
            wxIn.setBusiId(busiIds);
        }
        wxIn.setTenantId(loginInfo.getTenantId());
        boolean hasAllData = sysRoleService.hasAllData(loginInfo);
        wxIn.setHasAllData(hasAllData);
        List<Long> orgIdList = sysOrganizeService.getSubOrgList(accessToken);
        wxIn.setOrgIdList(orgIdList);

//        if (StringUtils.isNotBlank(wxIn.getOrderStates())) {
//            String[] arr = wxIn.getOrderStates().split(",");
//            wxIn.setArr(arr);
//        }
        if (StringUtils.isNotBlank(wxIn.getOrderState())) {
            wxIn.setArrs(Arrays.asList(wxIn.getOrderState().split(",")));
        }
//        if (wxIn.getHasAllData() != null && !wxIn.getHasAllData()) {
//            if (wxIn.getOrgIdList() == null) {
//                orgIdList.add(Long.valueOf(-1));
//                wxIn.setOrgIdList(orgIdList);
//            }
//        }
        //????????????????????????
        if (StringUtils.isNotBlank(wxIn.getCustomNumber())) {
            String[] cutomNumbers = wxIn.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sbd = new StringBuilder();
                Set<String> reciveNumber = new HashSet<String>();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (StringUtils.isNotBlank(cutomNumber)) {
                        reciveNumber.add(cutomNumber);
                        wxIn.setReciveNumber(reciveNumber);
                    }
                }
            }
        }
        Page<OrderListWxDto> orderListWxOutDtoPage = null;
        try {
            orderListWxOutDtoPage = orderInfoMapper.queryOrderListWxOut(new Page<>(pageNum, pageSize), wxIn);
            List<OrderListWxDto> records = orderListWxOutDtoPage.getRecords();
            if (wxIn.getSelectOrderType() != null && wxIn.getSelectOrderType() == 2) {
                if (records != null && records.size() > 0) {
                    List<OrderListWxDto> orderListOuts = setIsFinalAuditJurisdiction1(records, loginInfo);
                    List<OrderListWxDto> collect = new ArrayList<>();
                    if (wxIn.getTodo()) {
                        collect = orderListOuts.stream()
                                .filter(a -> a.getPreAmountFlag() == 1 && a.getOrderState() > 10 && a.getIsFinalAuditJurisdiction())
                                .collect(Collectors.toList());
                    } else {
                        collect = orderListOuts.stream()
                                .filter(a -> a.getPreAmountFlag() == 1 && a.getOrderState() > 10)
                                .collect(Collectors.toList());
                    }
                    List<OrderListWxDto> orderListOuts1 = collect.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
                    orderListWxOutDtoPage.setRecords(orderListOuts1);
                    orderListWxOutDtoPage.setTotal(collect.size());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<OrderListWxDto> listOut = new ArrayList<OrderListWxDto>();
        List<OrderListWxDto> list = null;
        if (orderListWxOutDtoPage != null) {
            list = orderListWxOutDtoPage.getRecords();
        } else {
            return orderListWxOutDtoPage;
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Long> busiIdList = new ArrayList<Long>();
        for (OrderListWxDto map : list) {
            busiIdList.add(Long.parseLong(String.valueOf(map.getOrderId())));
        }
        Map<Long, Map<String, Object>> payFinalMap = new HashMap<>();
        if ((wxIn.getIsHis() == null || !wxIn.getIsHis())
                && wxIn.getSelectOrderType() != null && wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.RECEIPT_AUDIT) {
            payFinalMap = auditOutService.queryAuditRealTimeOperation(loginInfo.getId(), AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, busiIdList, loginInfo.getTenantId());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("?????????1???????????????????????????" + (endTime - startTime));
        List<SysStaticData> sysStaticDataListByOrderType = readisUtil.getSysStaticDataList("ORDER_TYPE");
        System.out.println("sysStaticDataListByOrderType???" + sysStaticDataListByOrderType.size());
        List<SysStaticData> sysStaticDataListBySysCity = readisUtil.getSysStaticDataList("SYS_CITY");
        System.out.println("sysStaticDataListBySysCity???" + sysStaticDataListBySysCity.size());
        List<SysStaticData> sysStaticDataListByReciveType = readisUtil.getSysStaticDataList("RECIVE_TYPE");
        System.out.println("sysStaticDataListByReciveType???" + sysStaticDataListByReciveType.size());
        List<SysStaticData> sysStaticDataListByOrderState = readisUtil.getSysStaticDataList("ORDER_STATE");
        System.out.println("sysStaticDataListByOrderState???" + sysStaticDataListByOrderState.size());
        for (OrderListWxDto map : list) {
            OrderListWxDto out = new OrderListWxDto();
            BeanUtils.copyProperties(map, out);
            OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(out.getOrderId());
            out.setOrderLine(orderInfoDto.getOrderLine());
            out.setIsTransitLine(orderInfoDto.getIsTransitLine());
            if (StringUtils.isNotBlank(out.getContractUrl())) {
                try {
                    out.setAbsoluteCcontractUrl(client.getHttpURL(out.getContractUrl()).split("\\?")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_AUDIT
                    && (out.getOrderUpdateState() == null || out.getOrderUpdateState() != OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {// ????????????
                out.setAuditType(1);
                out.setAuditTypeName("????????????");
            } else if (out.getOrderUpdateState() != null
                    && (out.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_PORTION
                    || out.getOrderUpdateState() == OrderConsts.UPDATE_STATE.UPDATE_SPECIAL)) {// ????????????
                out.setAuditType(2);
                out.setAuditTypeName("????????????");
                this.setOrderListWxOut(out, loginInfo);
            } else {
                out.setAuditTypeName("");
            }
            endTime = System.currentTimeMillis();
            System.out.println("?????????2???????????????????????????" + (endTime - startTime));
            Boolean isFinalAuditJurisdiction = false;
            String auditFinalUserName = null;
            Boolean isFinalFinallyNode = false;
            if (wxIn.getSelectOrderType() != null && wxIn.getSelectOrderType() == OrderConsts.SELECT_ORDER_TYPE.RECEIPT_AUDIT) {
                //????????????
                List<OrderReceipt> receipts = iOrderReceiptService.findOrderReceipts(out.getOrderId(), accessToken, null);
                if (receipts != null) {
                    for (OrderReceipt orderReceipt : receipts) {
                        out.setReceiptsUrl(orderReceipt.getFlowUrl());
                        out.setReceiptsNumber(orderReceipt.getReciveNumber());
                        try {
                            out.setAbsoluteReceiptsUrl(client.getHttpURL(orderReceipt.getFlowUrl()).split("\\?")[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        orderReceipt.setFlowUrl(out.getAbsoluteReceiptsUrl());
                    }
                    out.setAbsoluteReceiptsUrls(receipts);
                }

                if (wxIn.getIsHis() == null || !wxIn.getIsHis()) {
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
            }
            endTime = System.currentTimeMillis();
            System.out.println("?????????3???????????????????????????" + (endTime - startTime));
            out.setIsFinalAuditJurisdiction(isFinalAuditJurisdiction);
            out.setAuditFinalUserName(auditFinalUserName);
            out.setIsFinalFinallyNode(isFinalFinallyNode);
            List<SysStaticData> list1 = sysStaticDataListByOrderState.stream().filter(a -> a.getCodeValue().equals(out.getOrderState() + "")).collect(Collectors.toList());
            if (list1 != null && list1.size() > 0) {
                out.setOrderStateName(list1.get(0).getCodeName());
            } else {
                out.setOrderStateName("");
            }
            List<SysStaticData> list2 = sysStaticDataListBySysCity.stream().filter(a -> a.getCodeValue().equals(out.getDesRegion() + "")).collect(Collectors.toList());
            if (list2 != null && list2.size() > 0) {
                out.setDesRegionName(list2.get(0).getCodeName());
            } else {
                out.setDesRegionName("");
            }
            List<SysStaticData> list3 = sysStaticDataListByReciveType.stream().filter(a -> a.getCodeValue().equals(out.getReciveType() + "")).collect(Collectors.toList());
            if (list3 != null && list3.size() > 0) {
                out.setReciveTypeName(list3.get(0).getCodeName());
            } else {
                out.setReciveTypeName("");
            }
            List<SysStaticData> list4 = sysStaticDataListBySysCity.stream().filter(a -> a.getCodeValue().equals(out.getSourceRegion() + "")).collect(Collectors.toList());
            if (list4 != null && list4.size() > 0) {
                out.setSourceRegionName(list4.get(0).getCodeName());
            } else {
                out.setSourceRegionName("");
            }
            List<SysStaticData> list5 = sysStaticDataListBySysCity.stream().filter(a -> a.getCodeValue().equals(out.getReciveCityId() + "")).collect(Collectors.toList());
            if (list5 != null && list5.size() > 0) {
                out.setReciveCityIdName(list5.get(0).getCodeName());
            } else {
                out.setReciveCityIdName("");
            }

//            SysStaticData sysStaticDataByOrderType=sysStaticDataListByOrderType.stream().filter(a -> a.getCodeValue().equals(out.getOrderState())).findFirst().get();
//            out.setOrderStateName(out.getOrderState() == null ? "": sysStaticDataByOrderType==null?"":sysStaticDataByOrderType.getCodeName());
//            SysStaticData sysStaticDataBySysCity=sysStaticDataListBySysCity.stream().filter(a -> a.getCodeValue().equals(out.getDesRegion())).findFirst().get();
//            out.setDesRegionName(out.getDesRegion() == null ? "": sysStaticDataBySysCity==null?"":sysStaticDataBySysCity.getCodeName());
//            SysStaticData sysStaticDataByReciveType=sysStaticDataListByReciveType.stream().filter(a -> a.getCodeValue().equals(out.getReciveType())).findFirst().get();
//            out.setReciveTypeName(out.getReciveType() == null ? "": sysStaticDataByReciveType==null?"":sysStaticDataByReciveType.getCodeName());
//            SysStaticData sysStaticDataBySysCity2=sysStaticDataListBySysCity.stream().filter(a -> a.getCodeValue().equals(out.getSourceRegion())).findFirst().get();
//            out.setSourceRegionName(out.getSourceRegion() == null ? "": sysStaticDataBySysCity2==null?"":sysStaticDataBySysCity2.getCodeName());
//            SysStaticData sysStaticDataBySysCity3=sysStaticDataListBySysCity.stream().filter(a -> a.getCodeValue().equals(out.getReciveCityId())).findFirst().get();
//            out.setReciveCityIdName(out.getSourceRegion() == null ? "": sysStaticDataBySysCity3==null?"":sysStaticDataBySysCity3.getCodeName());
//
//            out.setOrderStateName(out.getOrderState() == null ? ""
//                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ORDER_STATE", out.getOrderState() + "").getCodeName());
//            out.setDesRegionName(out.getDesRegion() == null ? ""
//                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", out.getDesRegion() + "").getCodeName());
//            out.setReciveTypeName(out.getReciveType() == null ? "" : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_TYPE", out.getReciveType() + "").getCodeName());
//            out.setSourceRegionName(out.getSourceRegion() == null ? ""
//                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", out.getSourceRegion() + "").getCodeName());
//            out.setReciveCityIdName(out.getSourceRegion() == null ? ""
//                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", out.getReciveCityId() + "").getCodeName());
            if (out.getPaymentWay() != null) {
                out.setPaymentWayName(sysStaticDataRedisUtils.getSysStaticDataById2String("PAYMENT_WAY", out.getPaymentWay()));
            }
            listOut.add(out);
            endTime = System.currentTimeMillis();
            System.out.println("?????????4???????????????????????????" + (endTime - startTime));
        }
        Page<OrderListWxDto> orderListWxDtoPage = orderListWxOutDtoPage.setRecords(listOut);
//        List<OrderListWxDto> listDto = new ArrayList<OrderListWxDto>();
//        List<OrderListWxDto> listDtoNot = new ArrayList<OrderListWxDto>();
//        List<OrderListWxDto> dtoList = new ArrayList<OrderListWxDto>();
//        int num = 0;
//        if (wxIn.getSelectOrderType() != null) {
//            for (OrderListWxDto dto : orderListWxDtoPage.getRecords()) {
//                if (wxIn.getSelectOrderType() == 2) {  //????????????
//                    if (wxIn.getTodo() && dto.getIsFinalAuditJurisdiction()) {  //?????????
//                        listDto.add(dto);
//                        orderListWxDtoPage.setRecords(listDto);
//                        num++;
//                        orderListWxDtoPage.setTotal(num);
//                    } else if (dto.getIsFinalAuditJurisdiction() == false) {
//                        listDtoNot.add(dto);
//                        orderListWxDtoPage.setRecords(listDtoNot);
//                        num++;
//                        orderListWxDtoPage.setTotal(num);
//                    }
//                } else {
//                    dtoList.add(dto);
//                    orderListWxDtoPage.setRecords(dtoList);
//                    num++;
//                    orderListWxDtoPage.setTotal(num);
//                }
//            }
//        }
        endTime = System.currentTimeMillis();
        long usedTime = endTime - startTime;
        System.out.println("??????????????????????????????" + usedTime);
        return orderListWxDtoPage;
    }

    @Override
    public Page<OrderListWxDto> queryOrderListCollectionOut(OrderListWxVo wxIn, Integer pageNum, Integer pageSize, String accessToken) {
        if (StringUtils.isNotBlank(wxIn.getOrderStates())) {
            String[] arr = wxIn.getOrderStates().split(",");
            wxIn.setArr(arr);
        }

        if (wxIn.getHasAllData() != null && !wxIn.getHasAllData()) {
            if (wxIn.getOrgIdList() == null) {
                wxIn.setOrgIdList(Lists.newArrayList(-1L));
            }
        }

        //????????????????????????
        if (StringUtils.isNotBlank(wxIn.getCustomNumber())) {
            String[] cutomNumbers = wxIn.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sbd = new StringBuilder();
                Set<String> reciveNumber = new HashSet<String>();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (StringUtils.isNotBlank(cutomNumber)) {
                        reciveNumber.add(cutomNumber);
                        wxIn.setReciveNumber(reciveNumber);
                    }
                }
            }
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("????????????");
        }

        Page<OrderListWxDto> orderListWxOutDtoPage = orderInfoMapper.queryOrderListWxOut(new Page<>(pageNum, pageSize), wxIn);
        for (OrderListWxDto record : orderListWxOutDtoPage.getRecords()) {
            OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(record.getOrderId());
            record.setOrderLine(orderInfoDto.getOrderLine());
            record.setIsTransitLine(orderInfoDto.getIsTransitLine());
            if (StringUtils.isNotBlank(record.getContractUrl())) {
                try {
                    if (client != null) {
                        record.setAbsoluteCcontractUrl(client.getHttpURL(record.getContractUrl()).split("\\?")[0]);
                    } else {
                        record.setAbsoluteCcontractUrl(record.getContractUrl());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BusinessException("????????????");
                }
            }
        }
        return orderListWxOutDtoPage;
    }


    @Override
    public boolean loadingWx(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("?????????????????????[" + orderId + "]???????????????!");
        }
        OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderTrackType(orderId);
        if (orderInfo != null) {
            String action = "depend";
            Integer trackType = orderInfoDto.getTrackType();
            if (trackType != null && trackType > 0) {
                if (trackType == OrderConsts.TRACK_TYPE.TYPE1) {
                    action = "depend";
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE2) {
                    action = "leave";
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                    action = "departure";
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                    action = "arrive";
                } else {
                    throw new BusinessException("?????????????????????????????????!");
                }
            } else {
                throw new BusinessException("?????????????????????????????????!");
            }
            orderInfoHService.loading(orderId, action, loginInfo.getName(), new Date(), null, accessToken);
        } else {
            throw new BusinessException("?????????????????????????????????!");
        }
        return true;
    }

    @Override
    public List<QueryOrderOilCardInfoDto> queryOrderOilCardInfo(Long orderId, String plateNumber, String accessToken) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("?????????????????????");
        }

        // ????????????????????????
        List<QueryOrderOilCardInfoDto> orderOilCardInfos = orderOilCardInfoService.queryOrderOilCardInfoByOrderIdWx(orderId, null);
        if ((orderOilCardInfos == null || orderOilCardInfos.size() == 0) && StringUtils.isNotBlank(plateNumber)) {
            // ???????????????????????????  ??????????????????????????????
            return orderOilCardInfoService.getOilCarNumberByPlateNumberWX(plateNumber, accessToken);
        }
        return orderOilCardInfos;
    }

    @Override
    public boolean verifyRece(OrderReceiptVo orderReceiptVo, String accessToken) {
        if (StringUtils.isBlank(orderReceiptVo.getReceiptsStr())) {
            List<OrderReceipt> list = iOrderReceiptService.findOrderReceipts(orderReceiptVo.getOrderId(), accessToken, null);
            if (list != null && list.size() > 0) {
                if (StringUtils.isBlank(orderReceiptVo.getReceiptsNumber())) {
                    throw new BusinessException("????????????????????????");
                }
                list.get(list.size() - 1).setReciveNumber(orderReceiptVo.getReceiptsNumber());
                orderReceiptVo.setReceiptsNumber(String.valueOf(list));
            } else {
                orderReceiptVo.setReceiptsNumber(null);
            }
        } else {
            orderReceiptVo.setReceiptsNumber(orderReceiptVo.getReceiptsStr());
        }
        iOrderReceiptService.verifyRece(orderReceiptVo.getOrderId().toString(),
                orderReceiptVo.getLoad(),
                orderReceiptVo.getReceipt(),
                orderReceiptVo.getVerifyString(),
                orderReceiptVo.getReceiptsNumber(),
                com.youming.youche.order.commons.OrderConsts.RECIVE_TYPE.SINGLE,
                accessToken);
        return true;
    }


    @Override
    public boolean evaluateOrderDirver(Long orderId, Integer serviceStarLevel, Integer agingStarLevel, Integer mannerStarLevel) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("???????????????????????????????????????");
        }
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        if (orderInfoExt != null) {
            orderInfoExt.setServiceStarLevel(serviceStarLevel);
            orderInfoExt.setAgingStarLevel(agingStarLevel);
            orderInfoExt.setMannerStarLevel(mannerStarLevel);
            orderInfoExt.setIsEvaluate(1);
            orderInfoExtService.saveOrUpdate(orderInfoExt);
        } else {
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            if (orderInfoExtH != null) {
                orderInfoExtH.setServiceStarLevel(serviceStarLevel);
                orderInfoExtH.setAgingStarLevel(agingStarLevel);
                orderInfoExtH.setMannerStarLevel(mannerStarLevel);
                orderInfoExtH.setIsEvaluate(1);
                orderInfoExtHService.saveOrUpdate(orderInfoExtH);
            } else {
                throw new BusinessException("?????????????????????[" + orderId + "]??????!");
            }
        }
        return true;
    }

    @Override
    public OrderReciveEchoDto orderReciveEcho(Long orderId, String accessToken) {
        if (orderId != null && orderId <= 0) {
            throw new BusinessException("????????????????????????????????????");
        }
        OrderGoods goods = orderGoodsService.getOrderGoods(orderId);
        Map<String, Object> out = new HashMap<String, Object>();
        if (goods == null) {
            throw new BusinessException("???????????????[" + orderId + "]????????????!");
        }
        OrderReciveEchoDto dto = new OrderReciveEchoDto();
        dto.setReciveState(goods.getReciveState());
        dto.setReciveType(goods.getReciveType());
        dto.setReciveTypeName(goods.getReciveType() == null ? "" : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_TYPE", goods.getReciveType() + "").getCodeName());
        if (goods.getReciveProvinceId() != null && (goods.getReciveProvinceId()) != 0) {
            goods.setReciveProvinceName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_PROVINCE", goods.getReciveProvinceId() + "").getCodeName());
        }
        // ??????id ?????????null  ??????id ??????1?????? ??????0
        if (goods.getReciveCityId() != null) {
            goods.setReciveCityName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", goods.getReciveCityId() + "").getCodeName());
        }
        String reciveAddr = (goods.getReciveProvinceName() == null ? "" : goods.getReciveProvinceName()) +
                (goods.getReciveCityName() == null ? "" : goods.getReciveCityName()) +
                (goods.getReciveAddr() == null ? "" : goods.getReciveAddr());
        dto.setReciveAddr(reciveAddr);
        //????????????
        List<OrderReceipt> receipts = iOrderReceiptService.findOrderReceipts(orderId, accessToken, null);
        if (receipts != null) {
            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (OrderReceipt orderReceipt : receipts) {
                try {
                    orderReceipt.setFlowUrl(client.getHttpURL(orderReceipt.getFlowUrl()).split("\\?")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        dto.setReceipts(receipts);
        return dto;
    }

    @Override
    public QueryUserOrderJurisdictionDto queryUserOrderJurisdiction(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        QueryUserOrderJurisdictionDto dto = new QueryUserOrderJurisdictionDto();
        dto.setHasOrderIncomePermission(sysRoleService.hasOrderIncomePermission(loginInfo)); //??????????????????
        dto.setHasOrderCostPermission(sysRoleService.hasOrderCostPermission(loginInfo)); // ??????????????????
        return dto;
    }

    @Override
    public EstimatedCostsDto getEstimatedCosts(EstimatedCostsVo estimatedCostsVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        estimatedCostsVo.setTenantId(loginInfo.getTenantId());
        if (estimatedCostsVo.getCarDriverId() != null && estimatedCostsVo.getCopilotUserId() != null && estimatedCostsVo.getCarDriverId() > 0 && estimatedCostsVo.getCopilotUserId() > 0) {
            if (estimatedCostsVo.getCarDriverId().longValue() == estimatedCostsVo.getCopilotUserId().longValue()) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
        }

        Long tenantId = estimatedCostsVo.getTenantId();
        LocalDateTime dependTime = null;
        if (StringUtils.isNotBlank(estimatedCostsVo.getDependTime())) {
//            dependTime = LocalDateTimeUtil.convertStringToDate(estimatedCostsVo.getDependTime());
            DateTimeFormatter sf = DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT1);
            dependTime = LocalDateTime.parse(estimatedCostsVo.getDependTime(), sf);
        } else {
            throw new BusinessException("?????????????????????!");
        }
        Float arriveTime = null;
        if (StringUtils.isNotBlank(String.valueOf(estimatedCostsVo.getArriveTime()))) {
            arriveTime = Float.parseFloat(String.valueOf(estimatedCostsVo.getArriveTime()));
        } else {
            throw new BusinessException("?????????????????????!");
        }

        String plateNumber = estimatedCostsVo.getPlateNumber();
        Long distance = null;
        if (estimatedCostsVo.getDistance() != null) {
            distance = estimatedCostsVo.getDistance() < 0 ? 0 : estimatedCostsVo.getDistance();
        }
        Long pontagePer = null;
        if (estimatedCostsVo.getPontagePer() != null) {
            pontagePer = estimatedCostsVo.getPontagePer() < 0 ? 0 : estimatedCostsVo.getPontagePer();
        }
        String nand = estimatedCostsVo.getNand();
        String eand = estimatedCostsVo.getEand();
        String region = estimatedCostsVo.getRegion();
        Long provinceId = estimatedCostsVo.getProvinceId();
        Long orderId = estimatedCostsVo.getOrderId();
        Long emptyDistance = estimatedCostsVo.getEmptyDistance();
        String oilPriceStr = estimatedCostsVo.getOilPrice();
        Integer transitLineSize = estimatedCostsVo.getTransitLineSize();
        Integer copilotSubsidyDay = 0;
        String copilotSubsidyDate = "";
        EstimatedCostsDto dto = new EstimatedCostsDto();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map> driverSubsidyDays = new ArrayList<>();
        dto.setCopilotSubsidy(Float.valueOf(0));
//        retMap.put("copilotSubsidy", 0);
        if (estimatedCostsVo.getCopilotUserId() != null && estimatedCostsVo.getCopilotUserId() > 0) {
            Map<String, Object> copilotSubsidyMap = null;
            copilotSubsidyMap = orderFeeExtService.culateSubsidy(estimatedCostsVo.getCopilotUserId(), tenantId, dependTime, arriveTime, orderId, true, transitLineSize);
            //            Map<String, Object> copilotSubsidyMap = orderFeeExtService.culateSubsidy(estimatedCostsVo.getCopilotUserId(), tenantId, dependTime, arriveTime, orderId, true, transitLineSize);
            if (StringUtils.isNotBlank(OrderUtil.objToStringEmpty(copilotSubsidyMap.get("date")))) {
                String[] subsidyTimeArr = OrderUtil.objToStringEmpty(copilotSubsidyMap.get("date")).split(" ");
                Date currDate = new Date();
                for (String subsidyTime : subsidyTimeArr) {
                    if (StringUtils.isBlank(subsidyTime)) {
                        continue;
                    }
                    String data = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                    LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(data);
                    //1.????????????????????????????????????
                    List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, estimatedCostsVo.getCopilotUserId(), tenantId, false, orderId);
                    if (list == null || list.size() == 0) {
                        list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, estimatedCostsVo.getCopilotUserId(), tenantId, true, orderId);
                    }
                    if (list == null || list.size() == 0) {
                        copilotSubsidyDate += " " + subsidyTime;
                        copilotSubsidyDay++;
                    }
                }
            }
            dto.setCopilotSubsidy(Float.valueOf(OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")) == null ? 0L : copilotSubsidyDay * OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy"))));
            dto.setCopilotSubsidyDate(copilotSubsidyDate);
            if (StringUtils.isNotBlank(copilotSubsidyDate)) {
                UserDataInfo user = userDataInfoService.getUserDataInfo(estimatedCostsVo.getCopilotUserId());
                Map map = new ConcurrentHashMap();
                map.put("userName", user != null ? user.getLinkman() : null);
                map.put("subsidyFee", OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")) == null ? 0L : copilotSubsidyDay * OrderUtil.objToLong0(copilotSubsidyMap.get("subsidy")));
                map.put("subsidyDay", copilotSubsidyDate);
                driverSubsidyDays.add(map);
            }
        }
        String carDriverSubsidyDate = "";
        Integer driverSubsidyDay = 0;
        Map<String, Object> carDriverSubsidyMap = orderFeeExtService.culateSubsidy(estimatedCostsVo.getCarDriverId(), tenantId, dependTime, arriveTime, orderId, false, transitLineSize);
        if (StringUtils.isNotBlank(OrderUtil.objToStringEmpty(carDriverSubsidyMap.get("date")))) {
            String[] subsidyTimeArr = OrderUtil.objToStringEmpty(carDriverSubsidyMap.get("date")).split(" ");
            for (String subsidyTime : subsidyTimeArr) {
                if (StringUtils.isBlank(subsidyTime)) {
                    continue;
                }
                String data = LocalDateTime.now().getYear() + "-" + subsidyTime + " 00:00:00";
                LocalDateTime subsidyDate = LocalDateTimeUtil.convertStringToDate(data);
                //1.????????????????????????????????????
                List list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, estimatedCostsVo.getCarDriverId(), tenantId, false, orderId);
                if (list == null || list.size() == 0) {
                    list = orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, estimatedCostsVo.getCarDriverId(), tenantId, true, orderId);
                }
                if (list == null || list.size() == 0) {
                    carDriverSubsidyDate += " " + subsidyTime;
                    driverSubsidyDay++;
                }
            }
        }
        dto.setCopilotSubsidyDate(carDriverSubsidyDate);
        dto.setCopilotSubsidy(Float.valueOf(OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")) == null ? 0L : driverSubsidyDay * OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy"))));
        if (StringUtils.isNotBlank(carDriverSubsidyDate)) {
            UserDataInfo user = userDataInfoService.getUserDataInfo(estimatedCostsVo.getCarDriverId());
            Map map = new ConcurrentHashMap();
            map.put("userName", user != null ? user.getLinkman() : null);
            map.put("subsidyFee", OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")) == null ? 0L : driverSubsidyDay * OrderUtil.objToLong0(carDriverSubsidyMap.get("subsidy")));
            map.put("subsidyDay", carDriverSubsidyDate);
            driverSubsidyDays.add(map);
        }
        // ?????????
        if (emptyDistance == null || emptyDistance < 0) {
            emptyDistance = getPreOrderDistance(plateNumber, dependTime, nand, eand, region, tenantId, orderId);
        }
        dto.setEmptyDistance(Float.valueOf(emptyDistance));
        Long pontageFee = culateETCFee(distance, pontagePer, emptyDistance);
        dto.setPontageFee(Float.valueOf(pontageFee));


        Long emptyOilCostPer = estimatedCostsVo.getLoadEmptyOilCost() < 0 ? 0 : estimatedCostsVo.getLoadEmptyOilCost();
        Long oilCostPer = estimatedCostsVo.getLoadFullOilCost() < 0 ? 0 : estimatedCostsVo.getLoadFullOilCost();
        Float oilTotal = culateOilConsumption(distance, emptyOilCostPer, oilCostPer, emptyDistance);
        dto.setOilTotal(oilTotal);

        if (StringUtils.isNotBlank(oilPriceStr)) {
            dto.setOilPrice(Float.valueOf(oilPriceStr));
        } else {
            OilPriceProvince oilPriceProvince = oilPriceProvinceService.getOilPriceProvince(provinceId.intValue());
            if (oilPriceProvince != null) {
                dto.setOilPrice(Float.valueOf(oilPriceProvince.getOilPrice()));
            } else {
                dto.setOilPrice(Float.valueOf(0));
            }
        }
        Long driverSwitchSubsidy = 0L;
        if (orderId != null && orderId > 0) {
            List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrder(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
            if (switchInfos != null && switchInfos.size() > 0) {
                for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
                    Map<String, Object> driverMap = orderFeeExtService.culateSubsidy(orderDriverSwitchInfo.getReceiveUserId(), tenantId, dependTime, arriveTime, orderId, false, transitLineSize);
                    if (driverMap != null) {
                        Long fee = (Long) driverMap.get("fee");
                        String date = (String) driverMap.get("date");
                        driverSwitchSubsidy += fee;
                        Map map = new ConcurrentHashMap();
                        map.put("userName", orderDriverSwitchInfo.getReceiveUserName());
                        map.put("subsidyFee", fee);
                        map.put("subsidyDay", date);
                        driverSubsidyDays.add(map);
                    }
                }
            }
        }

        dto.setDriverSubsidyDays(driverSubsidyDays);
        dto.setDriverSwitchSubsidy(Float.valueOf(driverSwitchSubsidy));
        return dto;
    }

    @Override
    public UserInfoDto getUserInfo(Long userId, String accessToken) {
        UserInfoDto dto = new UserInfoDto();

        dto.setBalance(0);
        dto.setVehicleSum(0L);
        dto.setVehicleVer(0);
        dto.setDriverVer(1);

        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
        if (userDataInfo == null) {
            throw new BusinessException("???????????????????????????");
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotEmpty(userDataInfo.getUserPriceUrl())) {
            String url = "";
            try {
                url = client.getHttpURL(userDataInfo.getUserPriceUrl()).split("\\?")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            dto.setUserPriceUrl(url);
        }

        OrderListAppVo in = new OrderListAppVo();
        in.setIsHis(false);
        in.setCarDriverId(userId);
        in.setOrderSelectType(2);
        List<OrderListAppDto> orderListAppDtos = this.queryOrderListAppOut(in);
        dto.setOrderSum(orderListAppDtos.size());

        //??????????????????
        //state ????????????  1???????????? 2???????????? 3?????????????????????????????????  ??????????????????
        if (userDataInfo.getAuthState() != null
                && userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
            dto.setDriverVer(0);

        }

        if (userDataInfo.getTenantId() != null && userDataInfo.getTenantId() > 0) {
            TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(userId, userDataInfo.getTenantId());
            if (tenantUserRel != null) {
                if (tenantUserRel.getState() != null
                        && tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                    dto.setDriverVer(0);
                }
            }
        }

        VehicleCountDto vehicleCountDto = vehicleDataInfoService.doQueryVehicleCountByDriver(userId);
        int authState = vehicleCountDto.getAuthState();
        Long vehicleCount = vehicleCountDto.getVehicleCount();
        if (authState == AUTH_STATE2) {
            dto.setVehicleVer(1);
        }

        if (null != vehicleCount) {
            dto.setVehicleSum(vehicleCount);
        }
        dto.setUserName(userDataInfo.getLinkman());
        dto.setUserLevel(1);
        dto.setUserLevelName("????????????");

        dto.setReward("0");
        dto.setInterest("0");
        SysCfg reward = sysCfgRedisUtils.getSysCfg("REWARD", accessToken);
        if (reward != null) {
            dto.setReward(reward.getCfgValue());
        }
        SysCfg interest = sysCfgRedisUtils.getSysCfg("INTEREST", accessToken);
        if (interest != null) {
            dto.setInterest(interest.getCfgValue());
        }

        return dto;
    }

    @Override
    public List<OrderListAppDto> queryOrderListAppOut(OrderListAppVo vo) {
        if (vo == null) {
            throw new BusinessException("??????????????????!");
        }
        return baseMapper.queryOrderListAppOut(vo);
    }

    @Override
    public Object queryOrderListApp(OrderListAppVo vo, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (vo == null) {
            throw new BusinessException("??????????????????!");
        }
        List<OrderListAppOutDto> listOut = new ArrayList<>();
        if (vo.getOrderSelectType() != null && vo.getOrderSelectType() == 1) {// ??????
            return listOut;
        }
        if (StringUtils.isNotEmpty(vo.getReciveState())) {
            vo.setReciveStateArr(vo.getReciveState().split(","));
        }
//        if (StringUtils.isNotEmpty(vo.getOrderStates())) {
//            vo.setOrderStatesArr(vo.getOrderStates().split(","));
//        }
        //????????????????????????
        if (StringUtils.isNotBlank(vo.getCustomNumber())) {
            String[] cutomNumbers = vo.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sbd = new StringBuilder();
                Set<String> reciveNumber = new HashSet<String>();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (StringUtils.isNotBlank(cutomNumber)) {
                        reciveNumber.add(cutomNumber);
                        vo.setReciveNumber(reciveNumber);
                    }
                }
            }
        }
        if (vo.getOrderSelectType() != null) {
            if (vo.getOrderSelectType() == 1) {

            } else if (vo.getOrderSelectType() == 2) {
                vo.setIsHis(false);
            } else if (vo.getOrderSelectType() == 3) {
                vo.setIsHis(true);
            } else if (vo.getOrderSelectType() == 4) {
                vo.setIsHis(true);
            }
        }
        vo.setTenantId(loginInfo.getTenantId());
        vo.setCarDriverPhone(loginInfo.getBillId());
        Page<OrderListAppOutDto> orderListAppDtos = null;
        try {
            orderListAppDtos = orderInfoMapper.queryOrderListApp(new Page<>(pageNum, pageSize), vo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<OrderListAppOutDto> list = orderListAppDtos.getRecords();
        List<Long> busiIdList = new ArrayList<>();
        for (OrderListAppOutDto dto : list) {
            if (dto.getOrderId() != null) {
                busiIdList.add(dto.getOrderId());
            }
        }
        List<OrderMainReport> costReportMaps = new ArrayList<>();
        if (busiIdList != null && busiIdList.size() > 0) {
            costReportMaps = iOrderCostReportService.getCostReportStateByOrderIds(busiIdList);
        }
        Map<Long, Integer> costReportMap = new ConcurrentHashMap<Long, Integer>();
        for (OrderMainReport out : costReportMaps) {
            costReportMap.put(out.getOrderId(), out.getState());
        }
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String day = readisUtil.getSysCfg("ORDER_COST_REPORT_DAY", "-1").getCfgValue();
        for (OrderListAppOutDto orderListAppOut : list) {
            List<OrderTransitLineInfo> transitLineInfos = null;
            if (orderListAppOut.getIsHis() != null && orderListAppOut.getIsHis().intValue() == 1) {
                List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderListAppOut.getOrderId());
                if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                    transitLineInfos = new ArrayList<>();
                    for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                        OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                        BeanUtils.copyProperties(orderTransitLineInfoH, transitLineInfo);
                        transitLineInfos.add(transitLineInfo);
                    }
                }
            } else {
                transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderListAppOut.getOrderId());
            }
            orderListAppOut.setTransitLineInfos(transitLineInfos);
            orderListAppOut.setOrderCostReportDay(day == null ? 0 : Integer.parseInt(day));
            OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(orderListAppOut.getOrderId());
            orderListAppOut.setOrderLine(orderInfoDto.getOrderLine());
            orderListAppOut.setIsTransitLine(orderInfoDto.getIsTransitLine());
            OaloanCountDto loanDto = oaLoanService.queryOaloanCount(orderListAppOut.getOrderId(), vo.getCarDriverId());
            ClaimExpenseCountDto expenseInfoDto = claimExpenseInfoService.queryClaimExpenseCount(orderListAppOut.getOrderId(), vo.getCarDriverId());
            Integer expenseNum = 0;
            if (expenseInfoDto != null) {
                expenseNum = expenseInfoDto.getCount();
            }
            Integer loadNum = 0;
            if (loanDto != null) {
                loadNum = loanDto.getCount();
            }
            SysTenantDef tenantDef = iSysTenantDefService.getSysTenantDef(orderListAppOut.getTenantId(), true);
            if (tenantDef != null) {
                if (StringUtils.isNotBlank(tenantDef.getLogo())) {
                    String logo = tenantDef.getLogo();
                    if (StringUtils.isNotBlank(logo)) {
                        try {
                            orderListAppOut.setLogoUrl(client.getHttpURL(logo).split("\\?")[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Integer costReportState = costReportMap.get(orderListAppOut.getOrderId()) == null ? -1 : costReportMap.get(orderListAppOut.getOrderId());
            orderListAppOut.setIsReportFee(0);
            if (costReportState >= 0) {
                orderListAppOut.setIsReportFee(1);
                orderListAppOut.setOrderCostReportState(costReportState);
            }
            orderListAppOut.setLoadNum(loadNum);
            orderListAppOut.setExpenseNum(expenseNum);
            orderListAppOut.setOrderStateName(orderListAppOut.getOrderState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ORDER_STATE", orderListAppOut.getOrderState() + "").getCodeName());
            orderListAppOut.setDesRegionName(orderListAppOut.getDesRegion() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", orderListAppOut.getDesRegion() + "").getCodeName());
            orderListAppOut.setReciveTypeName(orderListAppOut.getReciveType() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_TYPE", orderListAppOut.getReciveType() + "").getCodeName());
            orderListAppOut.setSourceRegionName(orderListAppOut.getSourceRegion() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", orderListAppOut.getSourceRegion() + "").getCodeName());
            orderListAppOut.setPaymentWayName(orderListAppOut.getPaymentWay() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("PAYMENT_WAY", orderListAppOut.getPaymentWay() + "").getCodeName());
            orderListAppOut.setArrivePaymentStateName(orderListAppOut.getArrivePaymentState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("AMOUNT_FLAG", orderListAppOut.getArrivePaymentState() + "").getCodeName());


            if (orderListAppOut.getPreTotalFee() == null) {
                orderListAppOut.setPreTotalFee(0L);
            }
            if (orderListAppOut.getFinalFee() == null) {
                orderListAppOut.setFinalFee(0L);
            }
            if (orderListAppOut.getTotalFee() == null) {
                orderListAppOut.setTotalFee(0L);
            }
            if (orderListAppOut.getPreOilVirtualFee() == null) {
                orderListAppOut.setPreOilVirtualFee(0L);
            }
            if (orderListAppOut.getPreOilFee() == null) {
                orderListAppOut.setPreOilFee(0L);
            }
            if (orderListAppOut.getSalary() == null) {
                orderListAppOut.setSalary(0L);
            }
            if (orderListAppOut.getCopilotSalary() == null) {
                orderListAppOut.setCopilotSalary(0L);
            }
            orderListAppOut.setSalarySum(orderListAppOut.getCopilotSalary() + orderListAppOut.getSalary() + orderListAppOut.getDriverSwitchSubsidy());
            if (orderListAppOut.getVehicleClass() != null
                    && orderListAppOut.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderListAppOut.getPaymentWay() != null) {
                if (orderListAppOut.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                    orderListAppOut.setTotalFee((orderListAppOut.getPreOilFee() == null ? 0 : orderListAppOut.getPreOilFee())
                            + (orderListAppOut.getPreOilVirtualFee() == null ? 0 : orderListAppOut.getPreOilVirtualFee())
                            + (orderListAppOut.getSalarySum() == null ? 0 : orderListAppOut.getSalarySum()));
                } else if (orderListAppOut.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    orderListAppOut.setTotalFee((orderListAppOut.getPreOilFee() == null ? 0 : orderListAppOut.getPreOilFee())
                            + (orderListAppOut.getPreOilVirtualFee() == null ? 0 : orderListAppOut.getPreOilVirtualFee()));
                }
            }
            if (orderListAppOut.getIsCollection() != null && orderListAppOut.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
                //??????????????????????????????????????????????????????
                orderListAppOut.setTotalFee((orderListAppOut.getPreOilFee() == null ? 0 : orderListAppOut.getPreOilFee())
                        + (orderListAppOut.getPreOilVirtualFee() == null ? 0 : orderListAppOut.getPreOilVirtualFee()));
                orderListAppOut.setPreTotalFee(orderListAppOut.getTotalFee());
                orderListAppOut.setArrivePaymentFee(0L);
                orderListAppOut.setFinalFee(0L);
            }
            listOut.add(orderListAppOut);
        }
        orderListAppDtos.setRecords(listOut);
        return orderListAppDtos;
    }

    @Override
    public void auditPriceSuccess(Long orderId, Boolean isUpdate, String cause, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        OrderInfo orderInfo = this.getOrder(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderFee orderfee = orderFeeService.getOrderFee(orderId);
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderGoods orderGoods = orderGoodsService.getOrderGoods(orderId);
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        auditPriceSuccess(orderInfo, orderInfoExt, orderfee, orderScheduler, orderGoods, orderFeeExt, isUpdate,
                orderInfo.getTenantId(), orderId, user, accessToken);
        // ?????????????????????
        if (orderScheduler.getAppointWay() != null && orderScheduler.getAppointWay().intValue() == OrderConsts.AppointWay.APPOINT_LOCAL) {
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.DISPATCH_ING);
        } else if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0
                && ((orderScheduler.getIsCollection() == null
                || orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.NO))) {
            orderInfo.setOrderState(StringUtils.isEmpty(orderScheduler.getPlateNumber()) ? OrderConsts.ORDER_STATE.TO_BE_RECIVE : OrderConsts.ORDER_STATE.TO_BE_LOAD);
        } else {
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
        }

        this.saveOrUpdate(orderInfo);


        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Audit,
                "[" + user.getName() + "]?????????????????? ?????????" + cause);
    }

    @Override
    public void auditPriceFail(Long orderId, String cause, String token) {
        LoginInfo user = loginUtils.get(token);
        OrderInfo orderInfo = this.getOrder(orderId);
        List<OrderTransferInfo> transferInfos = orderTransferInfoService.queryTransferInfoList(null, null, orderId);
        for (OrderTransferInfo orderTransferInfo : transferInfos) {
            if (orderTransferInfo.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                orderTransferInfo.setTransferOrderState(OrderConsts.TransferOrderState.BILL_TIME_OUT);
                orderTransferInfoService.saveOrUpdate(orderTransferInfo);
            }
        }
        orderInfo.setOrderState(OrderConsts.ORDER_STATE.AUDIT_NOT);
        this.saveOrUpdate(orderInfo);
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Audit,
                "[" + user.getName() + "]?????????????????? ?????????" + cause);
    }

    @Override
    @Transactional
    public Long dispatchOrderInfo(DispatchOrderVo dispatchOrderVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (dispatchOrderVo.getOrderId() == null || dispatchOrderVo.getOrderId() <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        OrderInfo orderInfo = this.getOrder(dispatchOrderVo.getOrderId());
        if (orderInfo == null) {
            throw new BusinessException("?????????????????????[" + dispatchOrderVo.getOrderId() + "]???????????????!");
        }
        if (orderInfo.getOrderState() != OrderConsts.ORDER_STATE.TO_BE_DISPATCH) {
            throw new BusinessException("???????????????????????????!");
        }

        OrderFee orderFee = orderFeeService.getOrderFee(dispatchOrderVo.getOrderId());
        OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(dispatchOrderVo.getOrderId());
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(dispatchOrderVo.getOrderId());
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(dispatchOrderVo.getOrderId());
        OrderGoods orderGoods = orderGoodsService.getOrderGoods(dispatchOrderVo.getOrderId());
        OrderPaymentDaysInfo costPaymentDaysInfo = new OrderPaymentDaysInfo();
        if (dispatchOrderVo.getOrderInfo() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getOrderInfo(), orderInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        if (dispatchOrderVo.getOrderfee() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getOrderfee(), orderFee, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        if (dispatchOrderVo.getOrderFeeExt() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getOrderFeeExt(), orderFeeExt, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        if (dispatchOrderVo.getOrderInfoExt() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getOrderInfoExt(), orderInfoExt, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        if (dispatchOrderVo.getOrderScheduler() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getOrderScheduler(), orderScheduler, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        if (dispatchOrderVo.getOrderGoods() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getOrderGoods(), orderGoods, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        if (dispatchOrderVo.getCostPaymentDaysInfo() != null) {
            BeanUtil.copyProperties(dispatchOrderVo.getCostPaymentDaysInfo(), costPaymentDaysInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        List<OrderOilDepotScheme> depotSchemes = new ArrayList<>();
        if (dispatchOrderVo.getOrderOilDepotSchemes() != null) {
            if (dispatchOrderVo.getOrderOilDepotSchemes().size() > 0) {
                for (OrderOilDepotScheme map : dispatchOrderVo.getOrderOilDepotSchemes()) {
                    OrderOilDepotScheme depotScheme = new OrderOilDepotScheme();
                    BeanUtil.copyProperties(map, depotScheme);
                    depotSchemes.add(depotScheme);
                }
            }
        }
        List<OrderOilCardInfo> orderOilCardInfos = null;
        if (dispatchOrderVo.getOrderOilCardInfos() != null) {
            if (dispatchOrderVo.getOrderOilCardInfos().size() > 0) {
                orderOilCardInfos = new ArrayList<>();
                for (OrderOilCardInfo map : dispatchOrderVo.getOrderOilCardInfos()) {
                    OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
                    BeanUtil.copyProperties(map, orderOilCardInfo);
                    orderOilCardInfos.add(orderOilCardInfo);
                }
            }
        }
        //?????????????????????????????????
        orderGoods.setLocalPhone(loginInfo.getTelPhone());
        orderGoods.setLocalUserName(loginInfo.getName());
        orderGoods.setLocalUser(loginInfo.getUserInfoId());
        this.orderInfoReassign(orderInfo, orderInfoExt, orderGoods, orderFee, orderFeeExt, orderScheduler,
                depotSchemes, costPaymentDaysInfo, orderOilCardInfos, "??????", loginInfo, accessToken);
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String city1 = sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", orderInfo.getSourceRegion() + "").getCodeName();
        String city2 = sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", orderInfo.getDesRegion() + "").getCodeName();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localTime = df.format(orderScheduler.getDependTime());

        paraMap.put("userName", orderScheduler.getCarDriverMan());
        paraMap.put("sesCity", city1);
        paraMap.put("desCity", city2);
        paraMap.put("loadTime", localTime);
        paraMap.put("goodsDesc", "????????????");
        try {
            sysSmsSendService.sendSms(orderScheduler.getCarDriverPhone(), EnumConsts.SmsTemplate.DISPATCH_CAR,
                    com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
                    com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.ORDER_REASSIGN, String.valueOf(orderInfo.getOrderId()), paraMap, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dispatchOrderVo.getOrderId();
    }

    @Override
    public List<OrderInfo> queryOrderInfoByTenantId(Long tenantId) {
        return baseMapper.queryOrderInfoByTenantId(tenantId);
    }

    @Override
    public Integer getStatisticsOrderReview(String accessToken, Integer selectOrderType) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        OrderListWxVo orderListWxVo = new OrderListWxVo();

        if (selectOrderType == OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST) {
            List<Long> busiIds = new ArrayList<>();
            busiIds = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.PROBLEM_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
            orderListWxVo.setBusiId(busiIds);
        }
        boolean hasAllData = sysRoleService.hasAllData(loginInfo);
        orderListWxVo.setHasAllData(hasAllData);
        if (loginInfo.getOrgIds() != null) {
            List<Long> orgIdList = new ArrayList<>();
            orgIdList = sysOrganizeService.getSubOrgList(accessToken);
            orderListWxVo.setOrgIdList(orgIdList);
        }
        orderListWxVo.setTenantId(loginInfo.getTenantId());
        orderListWxVo.setTodo(true);
        orderListWxVo.setSelectOrderType(selectOrderType);

        if (selectOrderType == 3) {
            List<Long> list = baseMapper.getStatisticsOrderReview(orderListWxVo);
            if (list.size() != 0) {
                Integer orderProblemInfoByOrderIds = orderProblemInfoService.getOrderProblemInfoByOrderIds(list);
                return null == orderProblemInfoByOrderIds ? 0 : orderProblemInfoByOrderIds;
            }
        }
        if (selectOrderType == 2) {
//            Integer integer = 0;
//            Boolean isFinalAuditJurisdiction = false;

            orderListWxVo.setIsHis(false);
            List<Long> statisticsOrderReviewHuiDan = baseMapper.getStatisticsOrderReview(orderListWxVo);

            return statisticsOrderReviewHuiDan.size();

//            Map<Long, Map<String, Object>> payFinalMap = new HashMap<>();
//            payFinalMap = auditOutService.queryAuditRealTimeOperation(loginInfo.getId(), AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE, statisticsOrderReviewHuiDan, loginInfo.getTenantId());
//            for (Long orderId : statisticsOrderReviewHuiDan) {
//                Map<String, Object> finalMap = payFinalMap.get(orderId);
//                if (finalMap != null) {
//                    isFinalAuditJurisdiction = DataFormat.getBooleanKey(finalMap, "isAuditJurisdiction");
//                } /*else {
//                    isFinalAuditJurisdiction = true;
//                }*/
//                if (isFinalAuditJurisdiction) {
//                    integer++;
//                }
//            }
//            return integer;
        }

        return 0;
    }

    @Override
    public VehicleFeeFromOrderDataDto getVehicleFeeFromOrderDataByMonth(String plateNumber, Long tenantId, String month) {
        return baseMapper.getVehicleFeeFromOrderDataByMonth(plateNumber, tenantId, month);
    }

    @Override
    public String getVehicleLineFromOrderDataByMonth(String plateNumber, Long tenantId, String month) {
        String vehicleLineFromOrderDataByMonth = baseMapper.getVehicleLineFromOrderDataByMonth(plateNumber, tenantId);
        if (null == vehicleLineFromOrderDataByMonth) {
            return "??????";
        }
        return vehicleLineFromOrderDataByMonth;
    }

    @Override
    public List<OrderInfo> selectOrderInfosByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderId, orderId);
        return this.list(orderInfoLambdaQueryWrapper);
    }

    @Override
    public void updateOrderIdMessage(Long orderId) {
        baseMapper.updateOrderIdMessage(orderId);
    }

}
