package com.example.student.projekat.util;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ProxyBitmap implements Serializable{
private int width;
private int height;
private int[] pixels;

    public ProxyBitmap(Bitmap bitmap) {
        if (bitmap == null) return;

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public Bitmap getBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
}
}
