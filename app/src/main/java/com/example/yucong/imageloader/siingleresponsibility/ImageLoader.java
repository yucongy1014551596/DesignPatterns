package com.example.yucong.imageloader.siingleresponsibility;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.example.yucong.imageloader.util.MyApplication;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 要实现图片加载   并将图片缓存起来
 */
public class ImageLoader {
    // 图片缓存
    LruCache<String ,Bitmap> mImageCache;

        // 线程池,线程数量为CPU的数量
        ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                     .availableProcessors());
    private Handler mUiHandler = new Handler(Looper.getMainLooper()) ;

    public ImageLoader() {
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

    /**
     * 在主线程中更新界面
     * @param imageView
     * @param bitmap
     */
    private void updateImageView(final ImageView imageView, final Bitmap bitmap) {

        mUiHandler.post(new Runnable() {

            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });


    }


    public void displayImage(final String url, final ImageView imageView){
        imageView.setTag(url);
        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url);
                if (bitmap == null) {
                    return;
                }
                if (imageView.getTag().equals(url)) {
                    updateImageView(imageView, bitmap) ;
                }
                mImageCache.put(url, bitmap);
            }
        });
    }

    public Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

}
