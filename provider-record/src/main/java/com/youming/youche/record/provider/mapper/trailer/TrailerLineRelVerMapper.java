package com.youming.youche.record.provider.mapper.trailer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.trailer.TrailerLineRelVer;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-26
 */
public interface TrailerLineRelVerMapper extends BaseMapper<TrailerLineRelVer> {

    void updtLineRelVerStatus(@Param("newStatus")Integer newStatus,
                              @Param("trailerId")Long trailerId,
                              @Param("oldStatus")Integer oldStatus);

}
