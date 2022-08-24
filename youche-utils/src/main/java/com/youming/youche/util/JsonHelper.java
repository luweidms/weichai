package com.youming.youche.util;




import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.youming.youche.encrypt.JSONNullTypeAdapter;
import com.youming.youche.encrypt.MapTypeAdapter;
import com.youming.youche.encrypt.StringNullTypeAdapter;
import net.sf.json.JSONNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JsonHelper {
    private static final Log log = LogFactory.getLog(JsonHelper.class);
    public static final Gson gson;

    public JsonHelper() {
    }

    public static JsonElement parseJson(String str) {
        return (new JsonParser()).parse(str);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String jsonStr, Type typeOfT) {
        return gson.fromJson(jsonStr, typeOfT);
    }



    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        return (Map)gson.fromJson(jsonStr, Map.class);
    }

    private static void debug(Object arg0) {
        if (log.isDebugEnabled()) {
            log.debug(arg0);
        }

    }

    private static void debug(Object arg0, Throwable arg1) {
        if (log.isDebugEnabled()) {
            log.debug(arg0, arg1);
        }

    }

    private static void warn(Object arg0) {
        if (log.isWarnEnabled()) {
            log.warn(arg0);
        }

    }

    private static void warn(Object arg0, Throwable arg1) {
        if (log.isWarnEnabled()) {
            log.warn(arg0, arg1);
        }

    }

    private static void error(Object arg0) {
        if (log.isErrorEnabled()) {
            log.error(arg0);
        }

    }

    private static void error(Object arg0, Throwable arg1) {
        if (log.isErrorEnabled()) {
            log.error(arg0, arg1);
        }

    }

    static {
        gson = (new GsonBuilder()).registerTypeAdapter(Map.class, new MapTypeAdapter()).registerTypeAdapter(JSONNull.class, new JSONNullTypeAdapter()).registerTypeAdapter(String.class, new StringNullTypeAdapter()).serializeNulls().disableHtmlEscaping().setDateFormat(DateUtil.DATETIME12_FORMAT).create();
    }

    public static class FieldExclusionStrategy implements ExclusionStrategy {
        public Set<String> excludeSet = new HashSet();

        public FieldExclusionStrategy(String[] excludes) {
            if (excludes != null) {
                String[] var2 = excludes;
                int var3 = excludes.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String exclude = var2[var4];
                    this.excludeSet.add(exclude);
                }
            }

        }

        public boolean shouldSkipField(FieldAttributes f) {
            return this.excludeSet.isEmpty() ? false : this.excludeSet.contains(f.getName());
        }

        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}

