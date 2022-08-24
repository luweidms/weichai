package com.youming.youche.record.api.trailer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.record.dto.TrailerManagementDto;
import com.youming.youche.record.dto.VehicleCertInfoDto;
import com.youming.youche.record.vo.OrderInfoVo;
import com.youming.youche.record.vo.TrailerManagementVo;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
public interface ITrailerManagementService extends IBaseService<TrailerManagement> {

    /**
     * 实现功能: 分页查询挂车列表
     *
     * @param page            分页参数
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param accessToken
     * @return
     */
    Page<TrailerManagementVo> doQueryTrailerList(Page<TrailerManagementVo> page, String trailerNumber, Integer isState,
                                                 Integer sourceProvince, Integer sourceRegion, Integer sourceCounty,
                                                 Integer trailerMaterial, String accessToken);


    /**
     * 实现功能: 启动闲置
     *
     * @param flag 1，闲置、 其他，启动
     * @param vid  挂车id
     */
    Boolean updateTrailerManagement(Short flag, Long vid, String trailerNumber, String accessToken);

    /**
     * 实现功能: 录入订单页面：通过部门id分页查询挂车列表
     *
     * @param page            分页参数
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param accessToken
     * @param orgId           部门id
     * @return
     */
    Page<TrailerManagementVo> doQueryTrailerListByorgId(Page<TrailerManagementVo> page, String trailerNumber, Integer isState,
                                                        Integer sourceProvince, Integer sourceRegion, Integer sourceCounty,
                                                        Integer trailerMaterial, String accessToken, Long orgId);

    /**
     * 挂车信息导出
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @return
     */
    void doQueryTrailerListExport(String trailerNumber, Integer isState,
                                  Integer sourceProvince, Integer sourceRegion, Integer sourceCounty,
                                  Integer trailerMaterial, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 实现功能: 分页查询挂车列表
     *
     * @param page            分页参数
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param deleteFlag      删除标识
     * @param accessToken
     * @return
     */
    Page<TrailerManagementVo> doQueryTrailerListDel(Page<TrailerManagementVo> page, String trailerNumber, Integer isState,
                                                    Integer sourceProvince, Integer sourceRegion, Integer sourceCounty,
                                                    Integer trailerMaterial, Integer deleteFlag, String accessToken);

    /**
     * 挂车历史信息导出
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param deleteFlag      删除标识
     * @return
     */
    void doQueryTrailerListDelExport(String trailerNumber, Integer isState,
                                     Integer sourceProvince, Integer sourceRegion, Integer sourceCounty,
                                     Integer trailerMaterial, Integer deleteFlag, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 实现功能: 查看挂车详情
     *
     * @param trailerId 挂车id
     * @return
     */
    com.youming.youche.record.vo.driver.TrailerManagementVo getTrailerByTrailerId(Long trailerId, String acclnt);


    /**
     * 实现功能: 查看挂车历史详情
     *
     * @param trailerId 挂车id
     * @return
     */
    com.youming.youche.record.vo.driver.TrailerManagementVo getTrailerVerByTrailerId(Long trailerId, String accessToken);

    /**
     * 实现功能: 删除挂车
     *
     * @param trailerId   挂车id
     * @param accessToken
     * @return
     */
    Integer deleteTrailer(Long trailerId, String accessToken);

    /**
     * 实现功能: 新增或者修改挂车
     *
     * @param trailerManagementDto
     * @return
     */
    ResponseResult doSaveOrUpdate(TrailerManagementDto trailerManagementDto, String accessToken);

    /**
     * 实现功能: 查询挂车订单信息
     *
     * @param trailerPlate    挂车车牌
     * @param plateNumber     车牌
     * @param orderId         订单号
     * @param carUserName     司机名称
     * @param carUserPhone    司机手机号
     * @param dependTimeBegin 超始靠台时间
     * @param dependTimeEnd   结束靠台时间
     * @param sourceRegion    超始地
     * @param desRegion       目标地
     */
    Page<OrderInfoVo> queryTrailerOrderList(Page<OrderInfoVo> page, String accessToken, String trailerPlate, String plateNumber,
                                            String carUserName, String carUserPhone, Long orderId, Integer sourceRegion,
                                            Integer desRegion, String dependTimeBegin, String dependTimeEnd);

    /**
     * 批量导入挂车信息
     */
    void batchImport(byte[] bytes, ImportOrExportRecords record, String accessToken, String fileName) throws Exception;


    /**
     * 挂车审核 -- 成功
     *
     * @param busiId
     * @param desc
     * @param paramsMap
     * @throws Exception
     */
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 挂车审核 -- 失败
     *
     * @param busiId
     * @param desc
     * @param paramsMap
     * @param token
     * @throws BusinessException
     */
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 查询挂车信息
     *
     * @param trailerId 表主键id
     */
    TrailerManagement getTrailerManagement(Long trailerId);

    /**
     * 修改挂车信息
     *
     * @param trailerId  主键id
     * @param lat        地址经度
     * @param lng        地址纬度
     * @param provinceId 始发省
     * @param regionId   始发市
     * @param countyId   始发县区
     */
    String updateLoaction(long trailerId, String lat, String lng, int provinceId, int regionId, int countyId);


    /**
     * 精准查询挂车信息
     *
     * @param trailerNumber 挂车号码
     * @param tenantId      车队id
     * @return
     */
    TrailerManagement getTrailerManagement(String trailerNumber, Long tenantId);

    /**
     * 查询挂车信息
     */
    VehicleCertInfoDto getTenantTrialer(String plateNumber, Long tenantId);

    /**
     * 模糊查询挂车信息
     *
     * @param tenantId      车队id
     * @param trailerNumber 挂车车牌号
     * @return
     */
    List<TrailerManagement> getNotUsedTrailer(Long tenantId, String trailerNumber);

    /**
     * 分页查询本地挂车
     */
    Page<TrailerManagement> getLocalNotUsedTrailerPage(Integer sourceRegion, String trailerNumber,
                                                       Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 营运工作台 挂车年审预警数量
     */
    List<WorkbenchDto> getTableTrailerCount();

    /**
     * 营运工作台  挂车数量
     */
    List<WorkbenchDto> getTableTrailerCarCount();

}
