package com.example.yucong.imageloader.siingleresponsibility.refactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 要实现图片加载   并将图片缓存起来
 */
public class ImageLoader {
    // 图片缓存类
    ImageCache mImageCache=new ImageCache();

        // 线程池,线程数量为CPU的数量
        ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                     .availableProcessors());
   //UiHandler
    private Handler mUiHandler = new Handler(Looper.getMainLooper()) ;

    public ImageLoader() {
        super();

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
        Bitmap bitmap=mImageCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }


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
