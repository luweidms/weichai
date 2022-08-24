package com.youming.youche.record.provider.mapper.cm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.record.dto.BackUserDto;
import com.youming.youche.record.vo.cm.CmCustomerInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 客户信息表/客户档案表Mapper接口
 * </p>
 *
 * @author 向子俊
 * @since 2021-11-22
 */
public interface CmCustomerInfoMapper extends BaseMapper<CmCustomerInfo> {
    /**
     * @return java.util.List<com.youming.youche.domain.cm.CmCustomerInfo>
     * @author 向子俊
     * @Description //TODO 客户档案列表
     * @date 13:19 2021/12/22 0022
     * @Param [customerInfo]
     */
    Page<CmCustomerInfo> selectAllCustomer(Page<CmCustomerInfo> page,
                                           @Param("customerInfo") CmCustomerInfoVo customerInfo,
                                           @Param("user") LoginInfo user) throws Exception;

    /**
     * @return java.util.List<com.youming.youche.domain.cm.CmCustomerInfo>
     * @author 向子俊
     * @Description //TODO 客户档案列表
     * @date 13:19 2021/12/22 0022
     * @Param [customerInfo]
     */
    List<CmCustomerInfo> exportCustomer(@Param("customerInfo") CmCustomerInfoVo customerInfo, @Param("user") LoginInfo user);


    /**
     * @return com.youming.youche.domain.cm.CmCustomerInfo
     * @author 向子俊
     * @Description //TODO 根据主键查询客户档案
     * @date 16:35 2021/12/22 0022
     * @Param [id]
     */
    CmCustomerInfo selectCustomerById(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * @return java.lang.Integer
     * @author 向子俊
     * @Description //TODO 判断客户是否存在
     * @date 15:41 2021/12/23 0023
     * @Param [customerInfo]
     */
    Integer checkCustomer(@Param("customerInfo") CmCustomerInfoVo customerInfo) throws Exception;

    /**
     * @return java.lang.Integer
     * @author 向子俊
     * @Description //TODO 新增客户档案
     * @date 9:35 2021/12/24 0024
     * @Param [customerInfo]
     */
    Long insertCustomerInfo(@Param("customerInfo") CmCustomerInfoVo customerInfo) throws Exception;

    Integer updateCustomerInfo(@Param("customerInfo") CmCustomerInfo customerInfo,
                               @Param("tenantId") Long tenantId) throws Exception;

    /**
     * @return java.util.List<com.youming.youche.domain.cm.CmCustomerInfo>
     * @author 向子俊
     * @Description //TODO 根据客户全称查询客户档案
     * @date 13:48 2022/1/6 0006
     * @Param [companyName, id]
     */
    List<CmCustomerInfo> doQueryCustomerByCompanyName(@Param("customerInfo") CmCustomerInfoVo customerInfo) throws Exception;

    /**
     * @return java.lang.Long
     * @author 向子俊
     * @Description //TODO 获取订单号最大值
     * @date 16:12 2022/1/5 0005
     * @Param [tenantId]
     */
    Long queryMaxCode(@Param("tenantId") Long tenantId);

    /***
     * @Description: 查询自有车司机和员工
     * @Author: luwei
     * @Date: 2022/2/24 9:48 下午
     * @Param linkman:
     * @Param mobilePhone:
     * @Param tenantId:
     * @return: java.util.List<com.youming.youche.record.dto.BackUserDto>
     * @Version: 1.0
     **/
    Page<BackUserDto> doQueryBackUserList(Page<BackUserDto> page, @Param("linkman") String linkman, @Param("tenantId") Long tenantId);

    /***
     * @Description: 查询自有车司机和员工
     * @Author: luwei
     * @Date: 2022/2/24 9:48 下午
     * @Param linkman:
     * @Param mobilePhone:
     * @Param tenantId:
     * @return: java.util.List<com.youming.youche.record.dto.BackUserDto>
     * @Version: 1.0
     **/
    List<BackUserDto> doQueryBackUserListPhone(@Param("tenantId") Long tenantId,@Param("phone") String phone);

    /**
     * 营运工作台  客户档案数量
     */
    List<WorkbenchDto> getTableCustomerCount();
}
