package com.example.student.projekat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageSerialization extends TypeAdapter<Bitmap> {
    private static final TypeAdapter<Bitmap> bitmapTypeAdapter = new ImageSerialization();

    private ImageSerialization() {

    }

    @Override
    public void write(JsonWriter out, Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if(bitmap == null){
            out.jsonValue("null");
            return;
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        String bitmapBase64 = Base64.encodeToString(bitmapdata, Base64.NO_WRAP | Base64.NO_CLOSE);

        out.jsonValue("\"" + bitmapBase64 + "\"");
    }

    @Override
    public Bitmap read(JsonReader in) throws IOException {

        String imageBase64 = "";

        if (in.peek() != JsonToken.NULL) {
            imageBase64 = in.nextString();
        } else {
            in.skipValue();
        }

        if (!imageBase64.isEmpty()) {
            byte[] imageByte = Base64.decode(imageBase64, Base64.NO_WRAP | Base64.NO_CLOSE);
            return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        }
        return null;
    }

    public static TypeAdapter<Bitmap> getBitmapTypeAdapter() {
        return bitmapTypeAdapter;
    }
}
