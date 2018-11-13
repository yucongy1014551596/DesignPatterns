package com.example.yucong.imageloader.ocp.refactor;

import android.graphics.Bitmap;

public interface ImageCache {

    public Bitmap get(String url);
    public void put(String url,Bitmap bitmap);
}
