package com.example.yucong.imageloader.ocp.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

public class ImageCache {

    // 图片缓存
    LruCache<String ,Bitmap> mImageCache;

    public ImageCache() {
        super();
        initImageCache();
    }

    private  void initImageCache(){
        // 计算可使用的最大内存
        final  int maxMemory= (int)(Runtime.getRuntime().maxMemory()/1024);
        // 取4分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 4;

        mImageCache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

    }


    public void put(String url,Bitmap bitmap){

        mImageCache.put(url,bitmap);
    }

    public Bitmap get(String url){

        return  mImageCache.get(url);
    }
}
