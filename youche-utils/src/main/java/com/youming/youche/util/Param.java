package com.youming.youche.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangliu on 2018/3/16.
 */
public class Param {
    private Map<String,Object> map;
    public Param(){
        map = new HashMap<String,Object>();
    }

    public Param(String column, Object value){
        map = new HashMap<String,Object>();
        map.put(column,value);
    }

    public Param(Map<String, Object> inParam) {
        map = new HashMap<>();
        map.putAll(inParam);
    }

    public void addParam(String column,Object value){
        map.put(column,value);
    }

    public List<String> getAllcolumns(){
        List<String> list = new ArrayList<String>(map.keySet());
        return list;
    }

    public Object getParamValue(String column){
        return map.get(column);
    }

    public void addParams(Map<String, Object> inParam) {
        map.putAll(inParam);
    }
}
