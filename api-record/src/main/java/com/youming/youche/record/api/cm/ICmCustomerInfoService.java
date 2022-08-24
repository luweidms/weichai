package com.youming.youche.record.api.cm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.record.dto.BackUserDto;
import com.youming.youche.record.vo.cm.CmCustomerInfoVo;

import java.util.List;

/**
 * <p>
 * 客户信息表/客户档案表 服务类
 * </p>
 *
 * @author 向子俊
 * @since 2021-11-22
 */
public interface ICmCustomerInfoService extends IBaseService<CmCustomerInfo> {

    /**
     * 查询客户档案
     *
     * @return
     */
    Page<CmCustomerInfo> findCustomerList(Page<CmCustomerInfo> page, CmCustomerInfoVo cmCustomerInfo, String accessToken) throws Exception;

    /**
     * @return com.youming.youche.domain.cm.CmCustomerInfo
     * @author 向子俊
     * @Description //TODO 根据id查询客户档案
     * @date 17:14 2021/12/22 0022
     * @Param [id]
     */
    List findCustomerById(Long customerId, String accessToken) throws Exception;

    /**
     * @return com.youming.youche.domain.cm.CmCustomerInfo
     * @author 向子俊
     * @Description //TODO 根据id查询客户档案
     * @date 17:14 2021/12/22 0022
     * @Param [id]
     */
    List getCustomerInfo(Long customerId, int isEdit, String accessToken);

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 新增或修改
     * @date 10:18 2022/1/27 0027
     * @Param [customerInfo, accessToken]
     */
    String saveOrUpdateCustomer(CmCustomerInfoVo customerInfo, String accessToken) throws Exception;

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 新增或修改
     * @date 10:18 2022/1/27 0027
     * @Param [customerInfo, accessToken]
     */
    String saveOrUpdateCustomerCopy(CmCustomerInfoVo customerInfo, String accessToken) throws BusinessException;


    /**
     * @return boolean
     * @author 向子俊
     * @Description //TODO 替换更变的值
     * @date 16:52 2022/1/5 0005
     * @Param [info, ver]
     */
    boolean switchChangedVal(Object info, Object ver) throws Exception;

    /**
     * @return java.lang.Integer
     * @author 向子俊
     * @Description //TODO 导入客户档案
     * @date 17:26 2022/1/17 0017
     * @Param [customerList]
     */
    void batchImportCustomer(byte[] byteBuffer, ImportOrExportRecords records, String token);


    /**
     * @return java.lang.Integer
     * @author 向子俊
     * @Description //TODO 导出客户档案
     * @date 17:26 2022/1/17 0017
     * @Param [customerList]
     */
    void exportCustomer(CmCustomerInfoVo cmCustomerInfo, String accessToken,ImportOrExportRecords importOrExportRecords);


    /**
     * 流程结束，审核通过的回调方法
     * @param customerId    业务的主键
     * @param auditContent      结果的描述
     * @return
     */
    public void doPublicAuth(Long customerId, int authState,String auditContent) throws BusinessException;


    /**
     * （多节点、第一个节点成功后回调）审核中回调方法
     * @param busiId
     */
    void auditingCallBack(Long busiId) throws Exception;

    /***
     * @Description: 查询自有车司机和员工
     * @Author: luwei
     * @Date: 2022/2/24 9:52 下午
     * @Param inParam:
     * @return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.record.domain.cm.CmCustomerInfo>
     * @Version: 1.0
     **/
    Page<BackUserDto> doQueryBackUserList(Page<BackUserDto> page,String linkman,String accessToken);

    /**
     * 查询客户要信息
     */
    CmCustomerInfo getInfoById(long id, String accessToken);

    /**
     * 营运工作台  客户档案数量
     */
    List<WorkbenchDto> getTableCustomerCount();

    /**
     * 根据客户名称模糊查询客户id
     */
    String queryCustomerIdByName(String name);

}
