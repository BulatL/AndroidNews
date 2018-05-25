package com.example.student.projekat.util;

import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.util.Date;

public class DateSerialization extends TypeAdapter<Date> {

    private static final TypeAdapter<Date> unixEpochDateTypeAdapter = new DateSerialization();

    private DateSerialization() {
    }

    @Override
    public void write(com.google.gson.stream.JsonWriter out, Date value) throws IOException {
        out.value(value.getTime());
    }

    @Override
    public Date read(com.google.gson.stream.JsonReader in) throws IOException {
        return new Date(in.nextLong());
    }

    public static TypeAdapter<Date> getUnixEpochDateTypeAdapter() {
        return unixEpochDateTypeAdapter;
    }
}
