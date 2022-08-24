package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.api.order.IInfoHService;
import com.youming.youche.finance.domain.order.InfoH;
import com.youming.youche.finance.dto.order.OrderDealInfoDto;
import com.youming.youche.finance.provider.mapper.order.InfoHMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.order.OrderDealInfoVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;


/**
 * <p>
 * 新的订单表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
public class InfoHServiceImpl extends BaseServiceImpl<InfoHMapper, InfoH> implements IInfoHService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    private InfoHMapper orderInfoHMapper;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public InfoH getOrderInfoHByOrderId(Long orderId) throws Exception {

        QueryWrapper<InfoH> orderInfoHQueryWrapper = new QueryWrapper<>();
        orderInfoHQueryWrapper.eq("order_id", orderId);
        List<InfoH> infoHS = orderInfoHMapper.selectList(orderInfoHQueryWrapper);
        if (infoHS != null && infoHS.size() > 0) {
            return infoHS.get(0);
        }
        return null;
    }

    @Override
    public IPage<OrderDealInfoDto> getDealOrderInfo(String accessToken, OrderDealInfoVo orderDealInfoVo) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        // 处理参数
        orderDealInfoVo.setTenantId(tenantId);
        if (StringUtils.isNotEmpty(orderDealInfoVo.getStartTime())) {
            orderDealInfoVo.setStartTime(orderDealInfoVo.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderDealInfoVo.getEndTime())) {
            orderDealInfoVo.setEndTime(orderDealInfoVo.getEndTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(orderDealInfoVo.getFinalStartTime())) {
            orderDealInfoVo.setFinalStartTime(orderDealInfoVo.getFinalStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderDealInfoVo.getFinalEndTime())) {
            orderDealInfoVo.setFinalEndTime(orderDealInfoVo.getFinalEndTime() + " 23:59:59");
        }

        // 查询未到期明细信息
        List<OrderDealInfoDto> list = baseMapper.getDealOrderInfoList(orderDealInfoVo);
        for (OrderDealInfoDto orderDealInfoDto : list) {
            orderDealInfoDto.setSourceRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", String.valueOf(orderDealInfoDto.getSourceRegion())).getName());
            orderDealInfoDto.setDesRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", String.valueOf(orderDealInfoDto.getDesRegion())).getName());
            orderDealInfoDto.setFinalStsName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "GET_TYPE", String.valueOf(orderDealInfoDto.getFinalSts())).getCodeName());
        }
        int count = baseMapper.getDealOrderInfoCount(orderDealInfoVo);

        IPage<OrderDealInfoDto> page = new Page<>();

        page.setCurrent(orderDealInfoVo.getPageNum() + 1);
        page.setRecords(list);
        page.setSize(orderDealInfoVo.getPageSize());
        page.setTotal(count);
        page.setPages(count % orderDealInfoVo.getPageSize() == 0 ? count / orderDealInfoVo.getPageSize() : (count / orderDealInfoVo.getPageSize() + 1));
        return page;
    }

    @Async
    @Override
    public void downloadExcelFile(String accessToken, OrderDealInfoVo orderDealInfoVo, ImportOrExportRecords importOrExportRecords) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        orderDealInfoVo.setTenantId(tenantId);
        if (StringUtils.isNotEmpty(orderDealInfoVo.getStartTime())) {
            orderDealInfoVo.setStartTime(orderDealInfoVo.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderDealInfoVo.getEndTime())) {
            orderDealInfoVo.setEndTime(orderDealInfoVo.getEndTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(orderDealInfoVo.getFinalStartTime())) {
            orderDealInfoVo.setFinalStartTime(orderDealInfoVo.getFinalStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderDealInfoVo.getFinalEndTime())) {
            orderDealInfoVo.setFinalEndTime(orderDealInfoVo.getFinalEndTime() + " 23:59:59");
        }

        List<OrderDealInfoDto> list = baseMapper.getDealOrderInfoList(orderDealInfoVo);
        for (OrderDealInfoDto orderDealInfoDto : list) {
            orderDealInfoDto.setSourceRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", String.valueOf(orderDealInfoDto.getSourceRegion())).getName());
            orderDealInfoDto.setDesRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", String.valueOf(orderDealInfoDto.getDesRegion())).getName());
            orderDealInfoDto.setFinalStsName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "GET_TYPE", String.valueOf(orderDealInfoDto.getFinalSts())).getCodeName());
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "订单号", "始发站", "目的站",
                    "回单时间", "未到期金额", "到期时间",
                    "状态"};
            resourceFild = new String[]{
                    "getOrderId", "getSourceRegionName", "getDesRegionName",
                    "getEndDate", "getNoPayFinal", "getFinalPianDate",
                    "getFinalStsName"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, OrderDealInfoDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "account.xlsx", inputStream.available());
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
}
