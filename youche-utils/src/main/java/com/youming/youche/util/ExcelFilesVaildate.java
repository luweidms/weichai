//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelFilesVaildate {
    public static final String REQUIRE = ".+";
    public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    public static final String PHONE = "(^(\\d{2,4}[-_－—]?)?\\d{3,8}([-_－—]?\\d{3,8})?([-_－—]?\\d{1,7})?$)|(^0?1[35]\\d{9}$)";
    public static final String MOBILE = "^(13|15|18)\\d{9}$";
    public static final String IDCARD = "((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))";
    public static final String IPADDRESS = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
    public static final String STR_ENG_NUM_ = "^\\w+$";
    public static final String STR_ENG_NUM = "^\\w+$";
    public static final String STR_ENG = "^[A-Za-z]+$";
    public static final String STR_CHINA = "^[/u0391-/uFFE5]+$";
    public static final String STR_ENG_CHA_NUM = "^[a-zA-Z0-9/u4e00-/u9fa5]+$";
    private String pattern = null;
    private String errorMsg = null;

    public ExcelFilesVaildate(String pattern, String errorMsg) {
        this.pattern = pattern;
        this.errorMsg = errorMsg;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }



}
