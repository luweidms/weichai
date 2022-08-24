package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.vo.AppAccountDetailsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* <p>
* Mapper接口
* </p>
* @author CaoYaJie
* @since 2022-03-22
*/
    public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {

    /**
     * 查询流水明细(合并同一笔操作)
     * @param appAccountDetailsVo
     * @return
     */
    List<AccountDetails> queryAccountDetailsMerge(@Param("appAccountDetailsVo") AppAccountDetailsVo appAccountDetailsVo, @Param("userType") Integer userType);

    /**
     * 应收逾期
     * @param userId
     * @return
     */
    List<Map> getAliasName(@Param("userId") Long userId);

    Integer tableIsExist(@Param("tableName") String tableName);

    void createTable(@Param("tableName") String tableName);

    AccountDetails selectTable(@Param("tableName") String tableName, @Param("id") Long id);

    Integer insertTable(@Param("details") AccountDetails details, @Param("tableName") String tableName);

    Integer updateTable(@Param("details") AccountDetails details, @Param("tableName") String tableName);

    /**
     * 判断分表是否存在
     */
    String showTableName(@Param("tableName") String tableName);

    }
