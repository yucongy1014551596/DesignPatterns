package com.example.yucong.imageloader.ocp.refactor;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
/**
 * 内存缓存
 */
public class MemoryCache implements ImageCache {


    public MemoryCache() {
        super();
        initImageCache();
    }

    private LruCache<String, Bitmap> mMemeryCache;

    private  void initImageCache(){
        // 计算可使用的最大内存
        final  int maxMemory= (int)(Runtime.getRuntime().maxMemory()/1024);
        // 取4分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 4;

        mMemeryCache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

    }

    @Override
    public Bitmap get(String url) {
        return mMemeryCache.get(url);
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        mMemeryCache.put(url, bitmap);
    }
}
