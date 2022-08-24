package com.youming.youche.encrypt;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class StringNullTypeAdapter extends TypeAdapter<String> {
    public StringNullTypeAdapter() {
    }

    public String read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.NULL) {
            in.nextNull();
            return "";
        } else {
            return peek == JsonToken.BOOLEAN ? Boolean.toString(in.nextBoolean()) : in.nextString();
        }
    }

    public void write(JsonWriter out, String value) throws IOException {
        if (value == null) {
            out.value("");
        } else {
            out.value(value);
        }

    }
}
