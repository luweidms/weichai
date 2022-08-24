package com.youming.youche.record.provider.mapper.cm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.dto.cm.CmCustomerLineDto;
import com.youming.youche.record.dto.cm.CmCustomerLineOrderExtend;
import com.youming.youche.record.dto.cm.QueryLineByTenantForWXDto;
import com.youming.youche.record.vo.VehicleLineRelsVo;
import com.youming.youche.record.vo.cm.CmCustomerLineVo;
import com.youming.youche.record.vo.cm.QueryLineByTenantForWXVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 客户线路信息表Mapper接口
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-15
 */
public interface CmCustomerLineMapper extends BaseMapper<CmCustomerLine> {
    /**
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.domain.cm.CmCustomerLine>
     * @author 向子俊
     * @Description //TODO 查询所有线路信息
     * @date 13:41 2022/1/21 0021
     * @Param [linePage, customerLine]
     */
    Page<CmCustomerLineDto> selectAllLine(Page<CmCustomerLineDto> linePage,
                                          @Param("customerLine") CmCustomerLineVo customerLine,
                                          @Param("user") LoginInfo user) throws Exception;

    List<CmCustomerLineDto> exportAllLine(@Param("customerLine") CmCustomerLineVo customerLine,
                                          @Param("user") LoginInfo user) throws Exception;

    /**
     * 查询车队线路信息
     *
     * @param tenantId 车队id
     * @param lineId   线路id
     * @return
     */
    CmCustomerLine selectLineById(@Param("tenantId") Long tenantId,
                                  @Param("lineId") String lineId);

    /**
     * @author 向子俊
     * @Description //TODO 线路是否存在
     * @date 16:06 2022/1/21 0021
     * @Param [tenantId, lineCodeName]
     * @return java.lang.Integer
     */
    Integer checkLineExist(@Param("tenantId")Long tenantId,
                           @Param("lineCodeName")String lineCodeName,
                           @Param("customerId")Long customerId,
                           @Param("id")Long id) throws Exception;

    Page<CmCustomerLine> getCustomerLineByLineCode(Page<CmCustomerLine> page,
                                                   @Param("tenantId") Long tenantId,
                                                   @Param("lineCodeRule") String lineCodeRule);

    //Integer insertLine(@Param("customerLine")CmCustomerLine customerLine) throws Exception;
    //查询已有往返编码
    Page<CmCustomerLineDto> selectCustomerLineByBackhaul(Page<CmCustomerLineDto> page,
                                                         @Param("customerLine") CmCustomerLineVo customerLine) throws Exception;

    List<CmCustomerLine> getCmCustomerLineByLineCodeRules(@Param("lineCodeRules") String lineCodeRules);



    List<VehicleLineRelsVo> getCmCustomerLineByLineCodeRulesTrailer(@Param("lineCodeRules") String lineCodeRules);

    CmCustomerLineOrderExtend selectLineListByAddress(@Param("customerId") Long customerId,
                                                        @Param("lineCodeRule") String lineCodeRule,
                                                      @Param("tenantId") Long tenantId) throws Exception;

    Page<QueryLineByTenantForWXDto> doQueryLineByTenantForWX(Page<QueryLineByTenantForWXDto> page, @Param("vo") QueryLineByTenantForWXVo vo);

    /**
     * 营运工作台  线路档案数量
     */
    List<WorkbenchDto> getTableLineCount();
}
