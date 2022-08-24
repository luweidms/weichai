package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductOut;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.*;
import com.youming.youche.market.dto.youca.ProductNearByDto;
import com.youming.youche.market.dto.youca.ProductNearByOutDto;
import com.youming.youche.market.dto.youca.ServiceProductOutDto;
import com.youming.youche.market.vo.facilitator.*;
import com.youming.youche.market.vo.youca.ProductNearByVo;
import com.youming.youche.market.vo.youca.ServiceProductOutVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务商站点表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface IServiceProductService extends IBaseService<ServiceProduct> {

    /**
     * 站点列表查询
     *
     * @param productQueryDto
     * @return
     */
    Page<ServiceProductVo> queryServiceProductList(ProductQueryDto productQueryDto, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 新增修改
     *
     * @param productSaveIn
     * @param accessToken
     * @return
     */
    ResponseResult saveProduct(ProductSaveDto productSaveIn, String accessToken);


    /**
     * 服务商下所有的产品
     * @param serviceUserId
     * @return
     */
    List<ServiceProduct> queryServiceProduct(Long serviceUserId,Long tenantId);

    /**
     * 新增修改
     *
     * @param productSaveIn
     * @param accessToken
     * @return
     */
    ResponseResult saveOrUpdate(ProductSaveDto productSaveIn, String accessToken);

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void fail(Long busiId, String desc, Map paramsMap,String token) throws BusinessException;

    /**
     * 校验站点有效性
     *
     * @param productSaveIn
     * @param serviceProduct
     * @return
     */
    Boolean checkProduct(ProductSaveDto productSaveIn, ServiceProduct serviceProduct);

    /**
     * 新增修改
     *
     * @param serviceProduct
     */
    void saveOrUpdata(ServiceProduct serviceProduct, boolean isUpdate, LoginInfo user);

    /**
     * 根据油站名称查询信息
     */
    List<ServiceProduct> getServiceProductByName(String productName);


    /**
     * 通过子账号查询站点信息
     *
     * @param childAccountUserId
     * @return
     */
    List<ServiceProduct> getServiceProductByChild(Long childAccountUserId);


    /**
     * 设置开票能力无的时候，站点的开票能力都设成无
     *
     * @param serviceUserId
     */
    void notBillAbility(Long serviceUserId, LoginInfo user);

    /**
     * 查出未和服务商合作的站点
     *
     * @param serviceUserId
     * @return
     */
    List<ServiceProductDto> getServiceProductApply(Long serviceUserId, Integer serviceType, LoginInfo baseUser);

    /**
     * 通过服务商用户id查询站点
     *
     * @param serviceUserId
     * @return
     */
    List<ServiceProduct> getProductByServiceUserId(Long serviceUserId);

    /**
     * 失效站点
     *
     * @param serviceUserId
     * @param serviceType
     */
    void loseServiceProduct(Long serviceUserId, Integer serviceType, LoginInfo user);

    /**
     * 查询服务商下的所有站点
     *
     * @param serviceUserId
     * @return
     */
    List<ServiceProduct> getServiceProducts(Long serviceUserId);

    /**
     * 新增修改
     *
     * @param serviceProduct
     */
    void saveOrUpdate(ServiceProduct serviceProduct, Boolean isUpdate, LoginInfo user);

    /**
     * 查询合作站点分页
     *
     * @param serviceProductInfoDto
     * @return
     */
    Page<ServiceProductInfoVo> queryServiceProductList(Integer pageNum, Integer pageSize, ServiceProductInfoDto serviceProductInfoDto);


    /**
     * 新增修改
     *
     * @param productSaveIn
     * @return
     * @throws Exception
     */
    //  String saveProduct(ProductSaveDto productSaveIn,String accessToken);

    /**
     * 审核、查看站点
     *
     * @return
     */
    ServiceDetailVo seeProduct(Long productId);


    /**
     * 查询合作车队列表
     *
     * @param pageNum
     * @param pageSize
     * @param cooperationProductDto
     * @return
     */
    Page<CooperationTenantDto> queryCooperationProduct(Integer pageNum, Integer pageSize, CooperationProductDto cooperationProductDto);

    /**
     * 共享站点审核
     *
     * @param productId
     * @param authState  2:审核通过，3：审核不通过
     * @param authRemark
     * @return Y 成功
     */
    ResponseResult auditShareProduct(Long productId, Integer authState, String authRemark, String accessToken);

    /**
     * 暂停、启用站点
     *
     * @return
     * @throws Exception
     */
    ResponseResult sure(Long productId, Boolean pass, String remark, String accessToken);


    /**
     * 查看历史备案信息
     *
     * @param relId
     * @param productId
     * @return
     */
    TenantProductOut getTenantProductHisOut(Long relId, Long productId) throws Exception;

//    String delTenantProduct(Long productId);

    /**
     * 校验服务商是否绑定银行卡
     *
     * @param serviceUserId
     * @param isShare
     * @throws Exception
     */
     ResponseResult checkServiceInfoBindBank(Long serviceUserId, Integer isShare, Integer isBill);
    /**
     * //     * 失效掉租户下站点
     * //     *
     * //     * @param userId
     * //     * @param serviceType
     * //     * @param tenantId
     * //
     */
    void loseTenantProduct(LoginInfo user, Long userId, Integer serviceType, Long tenantId, Boolean isAuthPass);

    /**
     * 微信查询站点列表
     *
     * @param authStates
     * @param state
     * @param serviceType
     * @return
     */
    Page<ServiceProductWxDto> queryWxProduct(Integer pageNum, Integer pageSize,
                                             List<Integer> authStates, List<Integer> state,
                                             Integer serviceType, String productName,
                                             String accessToken);

    /***
     * 解除合作
     * @param relId
     * @param productId
     * @return
     */
     Boolean releaseCooperation(Long relId, Long productId,String accessToken);


    /**
     * 接口编码：40008
     * 站点详情-微信
     * @return
     * @throws Exception
     */
    ProductDetailVo getProductDetail(Long productId,String accessToken);

    /**
     * 查询服务商商品信息
     *
     * @param cardNum  卡号
     * @param cardType 卡类型(中石油 中石化)
     * @param custTime 下发时间
     */
    ServiceProduct getServiceProduct(String cardNum, int cardType, String custTime);

    /**
     * 获取车队下用户信息
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
    List<ServiceProduct> getServiceProductList(Long userId, Long tenantId);

    /**
     * 接口编码：40002
     * 新增修改站点
     * @param
     * @return
     * @throws Exception
     */
    Boolean saveOrUpdateProduct(ProductSaveDto productSaveIn,String accessToken);



    /**
     * 合作车队 -站点
     * @return
     */
    List<ServiceProductVxVoOK> queryCooperationProductVx(List<String> state, Long productId,
                                                             String tenantName);


    /**
     * 通过站点id获取站点信息
     * @param productId
     * @return
     */
    ServiceProduct getServiceProduct(Long productId);

    /**
     * 根据服务商账号ID或服务商子账号ID获取服务商信息
     */
    Map getServiceInfoByUserIdOrChild(Long userId);

    /**
     * 获取服务商信息和站点子账号信息
     *
     * @param userId
     * @return serviceType 服务商类型
     * serviceName 服务商名称（子账号归属服务商）
     * mobilePhone 联系号码
     * userId 用户id
     * serviceTypeName 服务商类型（子账号类型）
     * linkman 联系人
     */
    Map<String, Object> queryChildCompanyName(long userId, int userType);
    /**
     * 统计服务商站点数量
     * @return
     * @throws Exception
     */
    int countProductNum(String accessToken);

    /**
     * 营运工作台  服务站点数量
     */
    List<WorkbenchDto> getTableManagerSiteCount();

    /**
     * 查询附近油站信息
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return 优先显示该司机归属车队和该油站的合作价格，若归属车队没有合作关系，则显示平台给的价格(根据需不需要开票改变)
     * 1、合作车队若按浮动价计算，则显示：折扣*油站归属省份的全国油价
     * 2、合作车队若按固定价计算，则显示：车队配置的规定单价
     * 3、平台若按浮动价计算，显示：平台设置折扣 * 油站归属省份的全国油价 *（1+平台设置手续费比例）
     * 4、平台若按固定价计算，显示：平台设置单价 *（1+平台设置手续费比例）
     */
    ProductNearByVo queryNearbyOil(ServiceProductOutVo vo, String accessToken);

    /**
     * 查询附近站点信息
     * @param longitude
     * @param latitude
     * @param serviceType
     * @param cityId
     * @param locationType
     * @param oilCardType
     * @param accessToken
     * @return
     */
     List<ProductNearByOutDto> queryNearby(String longitude, String latitude, int serviceType,Long cityId,Integer locationType,Long oilCardType,String accessToken);


    /**
     * 通过这里位置 查油站
     * @param tenantId
     * @param sourceNand
     * @param sourceEand
     * @param maxDistance
     * @return
     */
    List<ProductNearByDto>  getServiceNearBy( Long tenantId, String sourceNand, String sourceEand,
                                              Double maxDistance, Integer serviceType, Long cityId, Integer locationType, Long oilCardType);

    /**
     * 获取评分
     * @return
     */
    Map<String, Map<String, Object>> getMark(List<Long> productIds, int serviceType);


    /**
     * 查询服务商信息
     * @param tenantId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<ServiceProduct>  doQueryPrivateProduct(Long tenantId,Integer pageNum, Integer pageSize);

    /**
     * 查询附近油站（自有或共享）
     * @param longitude
     * @param latitude
     * @param isShare
     * @param amount
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<ProductNearByOutDto> queryNearbyOil1(String longitude, String latitude, boolean isShare,Long amount,Long tenantId,String accessToken);



    /**
     * 扫码支付详情接口
     * 接口编码：40001
     * @param oilId 油站id
     * @param tenantId 租户id
     * @return
     */
    ServiceProductOutDto scanCodePayment  (Long oilId, Long tenantId, String accessToken);


    /**
     * 接口编码：40024
     * 附近维修站查询
     * @return
     * @throws Exception
     */
    ProductNearByVo  queryNearbyRepair  (ServiceProductOutVo vo, String accessToken);

    /**
     * app接口-查询自有车司机的维修基金
     * @param userId
     * @param tenantId
     * @param userType
     * @return
     */
    Long getRepairFundSum(Long userId,Long tenantId,Integer userType);

    /**
     * 统计未确认的维修记录
     * @param driverUserId
     * @return
     */
    Integer countRepairNumNoSure(Long driverUserId);

    /**
     * 查询app维修保养记录
     * 40025
     * niejiewei
     * @param appRepairState
     * @return
     */
    IPage<UserRepairInfo> queryAppRepair(String appRepairState,String accessToken,Integer pageNum,Integer pageSize) ;
    /**
     * 获取图片URL
     * @param flowId
     * @return
     * @throws Exception
     */
    String getImgUrl(Long flowId);
}

