package com.youming.youche.encrypt;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapTypeAdapter extends TypeAdapter<Object> {
    public MapTypeAdapter() {
    }

    public void write(JsonWriter out, Object value) throws IOException {
    }

    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch(token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList();
                in.beginArray();

                while(in.hasNext()) {
                    list.add(this.read(in));
                }

                in.endArray();
                return list;
            case BEGIN_OBJECT:
                Map<String, Object> map = new TreeMap();
                in.beginObject();

                while(in.hasNext()) {
                    map.put(in.nextName(), this.read(in));
                }

                in.endObject();
                return map;
            case STRING:
                return in.nextString();
            case NUMBER:
                double dbNum = in.nextDouble();
                if (dbNum > 9.223372036854776E18D) {
                    return dbNum;
                } else {
                    long lngNum = (long)dbNum;
                    if (dbNum == (double)lngNum) {
                        return lngNum;
                    }

                    return dbNum;
                }
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
