package com.youming.youche.order.vo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengwen
 * @date 2022/5/20 17:14
 */
@Data
public class QueryOrderProblemInfoQueryVo implements Serializable {

    private Long orderId;

    private Long problemId;

    private String state;

    private String codeId;

    private String problemType;

    private Integer problemCondition;

    private List<String> statesArr;

    public List<String> getStatesArr() {
        if (StringUtils.isNotBlank(state)) {
            return Arrays.stream(state.split(",")).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

}
