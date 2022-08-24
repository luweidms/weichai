package com.youming.youche.system.api;

public interface IBasicConfigurationService {

    String doQuery(String codetype,String token);

    void doSave(String codetype, String basicType,String token);

    String getBasicCodeName(String basicType,String codeName,String token);
}
