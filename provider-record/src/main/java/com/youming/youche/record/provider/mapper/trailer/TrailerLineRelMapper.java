package com.youming.youche.record.provider.mapper.trailer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.trailer.TrailerLineRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
public interface TrailerLineRelMapper extends BaseMapper<TrailerLineRel> {

    String getTrailerLineRelStr(@Param("trailerId") Long trailerId);

    List<TrailerLineRel> getTrailerLineRelList(@Param("trailerId") Long trailerId,
                                               @Param("state") Integer state);

    void delLineRelList(@Param("trailerId") Long trailerId);

    List<TrailerLineRel> getTrailerLineRelForIdList(@Param("trailerId")Long trailerId,
                                                    @Param("updtLineId")String updtLineId);
}
