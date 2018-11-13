package com.example.yucong.imageloader.ocp.refactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 要实现图片加载   并将图片缓存起来
 *  符合 开闭原则
 */
public class ImageLoader {
     // 内存缓存
    ImageCache mImageCache=new MemoryCache();

        // 线程池,线程数量为CPU的数量
        ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                     .availableProcessors());
   //UiHandler
    private Handler mUiHandler = new Handler(Looper.getMainLooper()) ;


    /**
     * 用户可以自己定义使用那个缓存  五颗星
     * @param cache
     */
    public void setmImageCache(ImageCache cache){
        mImageCache=cache;
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

        Bitmap bitmap = mImageCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        submitLoadRequest(url,imageView);
    }


    /**
     * 网络请求
     * @param url
     * @param imageView
     */

    private void submitLoadRequest(final String url, final ImageView imageView){
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
