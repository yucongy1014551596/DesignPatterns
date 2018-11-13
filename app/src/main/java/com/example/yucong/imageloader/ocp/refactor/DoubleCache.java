package com.example.yucong.imageloader.ocp.refactor;

import android.graphics.Bitmap;

import com.example.yucong.imageloader.ocp.cache.DiskCache;

public class DoubleCache  implements ImageCache  {
    MemoryCache mMemoryCache = new MemoryCache();
    DiskCache mDiskCache = new DiskCache();

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap=  mMemoryCache.get(url);
        if(bitmap==null){
            bitmap= mDiskCache.get(url);
        }
        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        mMemoryCache.put(url, bitmap);
        mDiskCache.put(url, bitmap);

    }
}
