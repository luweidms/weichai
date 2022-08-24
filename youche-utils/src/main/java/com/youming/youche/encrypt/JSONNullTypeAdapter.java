package com.youming.youche.encrypt;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class JSONNullTypeAdapter extends TypeAdapter<Object> {
    public JSONNullTypeAdapter() {
    }

    public void write(JsonWriter out, Object value) throws IOException {
        out.nullValue();
    }

    public Object read(JsonReader in) throws IOException {
        return null;
    }
}